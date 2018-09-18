/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.app.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.eclipse.persistence.internal.oxm.conversion.Base64;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.itt.msales.app.service.AppService;
import vn.itt.msales.app.service.DateUtils;
import vn.itt.msales.app.service.LoginService;
import vn.itt.msales.app.service.SalesSupService;
import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.config.services.EquipmentServices;
import vn.itt.msales.csb.auth.MsalesHttpHeader;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.CompanyConfig;
import vn.itt.msales.entity.CompanyConfigDetails;
import vn.itt.msales.entity.CompanyConfigKpi;
import vn.itt.msales.entity.CompanyConstant;
import vn.itt.msales.entity.CustomerCareDetails;
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.Equipment;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsCategory;
import vn.itt.msales.entity.GoodsUnit;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.MCPDetails;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.POSImg;
import vn.itt.msales.entity.Promotion;
import vn.itt.msales.entity.PromotionAccumulationRetailer;
import vn.itt.msales.entity.PromotionAwardOther;
import vn.itt.msales.entity.PromotionGoodsRef;
import vn.itt.msales.entity.PromotionTransRetailer;
import vn.itt.msales.entity.SalesOrder;
import vn.itt.msales.entity.SalesOrderDetails;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.SalesStockGoods;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.SalesTransDetails;
import vn.itt.msales.entity.Session;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.UserRoute;
import vn.itt.msales.entity.Version;
import vn.itt.msales.entity.Workflow;
import vn.itt.msales.entity.WorkflowDetails;
import vn.itt.msales.entity.WorkflowType;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.searchObject.DeviceApp;
import vn.itt.msales.entity.searchObject.ListSoldGoodsByPOS;
import vn.itt.msales.entity.searchObject.OrderItem;
import vn.itt.msales.entity.searchObject.OrderList;
import vn.itt.msales.entity.searchObject.PromotionRetailer;
import vn.itt.msales.entity.searchObject.ReceiveGoods;
import vn.itt.msales.entity.searchObject.ReturnPromotionList;
import vn.itt.msales.entity.searchObject.SearchObject;
import vn.itt.msales.entity.searchObject.StockGoods;
import vn.itt.msales.entity.searchObject.SupPOSApp;
import vn.itt.msales.entity.searchObject.SupSellApp;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.MsalesLoginUserInf;
import vn.itt.msales.workflow.service.CompanyWorkflowService;
import vn.itt.msales.workflow.service.ServiceMCP;

/**
 * log
 *
 * @author vtm
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.APP.NAME)
public class MsalesAppController extends CsbController {

    @Autowired
    ServiceMCP serviceMCP;
    @Resource(name = "systemPros")
    private Properties systemProperties;

    @Autowired
    private LoginService loginService;
    @Autowired
    private AppService appService;

    @Autowired
    SalesSupService salesSupService;

    @Autowired
    private CompanyWorkflowService workflowService;

    @Autowired
    private EquipmentServices equipmentService;

    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_REGISTER_DEVICE_APP, method = RequestMethod.POST)
    public @ResponseBody
    String registerDeviceApp(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            DeviceApp deviceApp;
            try {
                deviceApp = MsalesJsonUtils.getObjectFromJSON(jsonString, DeviceApp.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (deviceApp != null) {
                LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();
                if (deviceApp.getUsername() == null || deviceApp.getUsername().trim().isEmpty()) {
                    hashErrors.put("username", MsalesValidator.NOT_NULL_AND_EMPTY);
                }
                if (deviceApp.getPassword() == null || deviceApp.getPassword().trim().isEmpty()) {
                    hashErrors.put("password", MsalesValidator.NOT_NULL_AND_EMPTY);
                }
                if (deviceApp.getImei() == null || deviceApp.getImei().trim().isEmpty()) {
                    hashErrors.put("imei", MsalesValidator.NOT_NULL_AND_EMPTY);
                }
                if (deviceApp.getSubscriberId() == null || deviceApp.getSubscriberId().trim().isEmpty()) {
                    hashErrors.put("subscriberId", MsalesValidator.NOT_NULL_AND_EMPTY);
                }
                if (deviceApp.getAppVersion() == null || deviceApp.getAppVersion().trim().isEmpty()) {
                    hashErrors.put("appVersion", MsalesValidator.NOT_NULL_AND_EMPTY);
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                //find DB

                Integer branch;
                try {
                    branch = appService.getBranchFromUsername(deviceApp.getUsername(), false, dataService);
                    if (branch == null) {
                        //domain khong ton tai
                        hashErrors.put("domain", MsalesValidator.NOT_EXIST);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS, hashErrors));
                    }
                } catch (Exception ex) {
                    //username khong hop le
                    hashErrors.put("username", MsalesValidator.DOMAIN_USER_NAME_INVALID);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, hashErrors));
                }

                //branch is OK                
                dataService.setBranch(branch);
                User user = appService.getUser(deviceApp.getUsername(), deviceApp.getPassword(), dataService);
                if (user == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_OR_PASSWORD_INCORRECT));
                }

                //check cong ty
                Company company = user.getCompanys();
                if (company.getExpireTime() != null) {
                    if (new Date().compareTo(company.getExpireTime()) > 0) {
                        //het thoi gian 
                        if (company.getIsRegister() == null || company.getIsRegister() == 0) {
                            //het thoi gian mua
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.COMPANY_EXPIRED));
                        } else {
                            //het thoi gian trai nghiem
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.COMPANY_EXPIRED_REGISTER));
                        }
                    }
                }

                //check thoi gian lam viec
                CompanyConstant companyConstant = appService.getCompanyConstant(user.getCompanys().getId(), dataService);
                if (!appService.checkTimeWork(companyConstant)) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_TIME_WORK));
                }

                //check role user                
                String userRoleName = "";
                List<UserRoleChannel> userRoleChannelList = appService.getListUserRoleChannel(user.getId(), dataService);
                if (userRoleChannelList != null && !userRoleChannelList.isEmpty()) {
                    boolean flagRole = false;//not salesman/salessup
                    for (UserRoleChannel userRoleChannel : userRoleChannelList) {
                        if (userRoleChannel.getUserRoles().getId() == 4 || userRoleChannel.getUserRoles().getId() == 6) {
                            flagRole = true;//OK
                            break;
                        } else {
                            userRoleName = userRoleChannel.getUserRoles().getName();
                        }
                    }

                    if (!flagRole) {
                        MsalesStatus msalesStatus = MsalesStatus.ERROR_MOBILE_ROLE;
                        msalesStatus.setMessage(String.format(msalesStatus.getMessage(), userRoleName));
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(msalesStatus));
                    }
                } else {
                    //loi userRole
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_ROLE_NULL));
                }

                //check user da dang ky thiet bi khac chua
                if (appService.checkUserRegister(user.getId(), deviceApp.getImei(), deviceApp.getSubscriberId(), dataService)) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_REGISTERED_OTHER_DEVICE));
                }

                if (user.getIsActive() == 0) {
                    //user dang bi khoa
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_LOCKED));
                } else if (!"1".equals(user.getStatuss().getValue()))//value = 1 => dang lam viec
                {
                    //user da nghi viec
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_NOT_WORK));
                }

                boolean register = true;

                Equipment equipment = appService.getEquipment(deviceApp.getImei(), deviceApp.getSubscriberId(), dataService);
                if (equipment != null) {
                    if (equipment.getUsers() != null && Objects.equals(equipment.getUsers().getId(), user.getId())) {
                        //Login
                        register = false;
                    } else {
                        //thiet bi da duoc dang ky boi nguoi khac
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.EQUIPMENT_REGISTERED));
                    }
                }
                //tao moi equipment
                if (register) {
                    Equipment newEquipment = new Equipment();
                    newEquipment.setImei(deviceApp.getImei());
                    newEquipment.setSubscriberId(deviceApp.getSubscriberId());
                    newEquipment.setUsers(user);
                    newEquipment.setCompanys(user.getCompanys());
                    newEquipment.setIsActive(true);
                    newEquipment.setActiveDate(new Date());
                    //set Version
                    newEquipment.setVersion(deviceApp.getAppVersion());
                    newEquipment.setCreatedUser(user.getId());

                    try {
                        //check so luong equipment
                        if (equipmentService.checkEquipmentMax(user.getCompanys().getId(), null, user.getCompanys().getEquipmentMax(), dataService)) {
                            MsalesStatus msalesStatus = MsalesStatus.ERROR_MAX_EQUIPMENT;
                            msalesStatus.setMessage(String.format(msalesStatus.getMessage(), user.getCompanys().getEquipmentMax()));
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(msalesStatus));
                        }

                        //tao moi equiment
                        dataService.insertRow(newEquipment);
                    } catch (Exception e) {
                        Exception ex = (Exception) e.getCause().getCause();
                        if (ex instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(ex);
                        }//else
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.UNKNOW));
                    }
                }
                return loginApp(request, branch, deviceApp);
            }
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
        }
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
    }

    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_LOGIN_APP, method = RequestMethod.POST)
    public @ResponseBody
    String loginApp(HttpServletRequest request, Integer branch, DeviceApp device) {
        DeviceApp deviceApp;
        if (branch != null) {
            deviceApp = device;
        } else {
            String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                    .toString();
            if (jsonString != null) {
                try {
                    deviceApp = MsalesJsonUtils.getObjectFromJSON(jsonString, DeviceApp.class);
                } catch (Exception ex) {
                    return MsalesJsonUtils.validateFormat(ex);
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
            }
        }

        LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

        if (deviceApp.getPassword() == null || deviceApp.getPassword().trim().isEmpty()) {
            hashErrors.put("password", MsalesValidator.NOT_NULL_AND_EMPTY);
        }

        if (deviceApp.getImei() == null || deviceApp.getImei().trim().isEmpty()) {
            hashErrors.put("imei", MsalesValidator.NOT_NULL_AND_EMPTY);
        }

        if (deviceApp.getSubscriberId() == null || deviceApp.getSubscriberId().trim().isEmpty()) {
            hashErrors.put("subscriberId", MsalesValidator.NOT_NULL);
        }

        if (deviceApp.getAppVersion() == null || deviceApp.getAppVersion().trim().isEmpty()) {
            hashErrors.put("appVersion", MsalesValidator.NOT_NULL_AND_EMPTY);
        }

        if (!hashErrors.isEmpty()) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
        }

        try {

            Equipment equipment = appService.getEquipment(deviceApp.getImei(), deviceApp.getSubscriberId(), dataService);

            if (equipment == null) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.EQUIPMENT_NOT_EXIST));
            } else if (equipment.getUsers() == null) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.EQUIPMENT_NOT_REGISTER));
            } else {
                //check cong ty
                Company company = equipment.getUsers().getCompanys();
                if (company.getExpireTime() != null) {
                    if (new Date().compareTo(company.getExpireTime()) > 0) {
                        //het thoi gian 
                        if (company.getIsRegister() == null || company.getIsRegister() == 0) {
                            //het thoi gian mua
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.COMPANY_EXPIRED));
                        } else {
                            //het thoi gian trai nghiem
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.COMPANY_EXPIRED_REGISTER));
                        }
                    }
                }

                if (equipment.getUsers().getIsActive() == 0) {
                    //user dang bi khoa
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_LOCKED));
                } else if (!"1".equals(equipment.getUsers().getStatuss().getValue()))//value = 1 => dang lam viec
                {
                    //user da nghi viec
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_NOT_WORK));
                }
            }

            User user = equipment.getUsers();

            //check thoi gian lam viec
            CompanyConstant companyConstant = appService.getCompanyConstant(user.getCompanys().getId(), dataService);
            if (!appService.checkTimeWork(companyConstant)) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_TIME_WORK));
            }

            //check role user                
            String userRoleName = "";
            List<UserRoleChannel> userRoleChannelList = appService.getListUserRoleChannel(user.getId(), dataService);
            if (userRoleChannelList != null && !userRoleChannelList.isEmpty()) {
                boolean flagRole = false;//not salesman/salessup
                for (UserRoleChannel userRoleChannel : userRoleChannelList) {
                    if (userRoleChannel.getUserRoles().getId() == 4 || userRoleChannel.getUserRoles().getId() == 6) {
                        flagRole = true;//OK
                        break;
                    } else {
                        userRoleName = userRoleChannel.getUserRoles().getName();
                    }
                }

                if (!flagRole) {
                    MsalesStatus msalesStatus = MsalesStatus.ERROR_MOBILE_ROLE;
                    msalesStatus.setMessage(String.format(msalesStatus.getMessage(), userRoleName));
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(msalesStatus));
                }
            } else {
                //loi userRole
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_ROLE_NULL));
            }

            String token;
            int role;
            String roleName;

            if (user.getPassword().toUpperCase().equals(deviceApp.getPassword().toUpperCase())) {
                List<UserRoleChannel> userRoleChannel = loginService.getListUserRoleChannel(user.getId(), dataService);

                if (!userRoleChannel.isEmpty()) {
                    MsalesLoginUserInf msalesLoginUserInf = loginService.doLogin(userRoleChannel, dataService, request);
                    token = msalesLoginUserInf.getToken();
                    role = msalesLoginUserInf.getUserRoleId();
                    roleName = msalesLoginUserInf.getUserRoleName();
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_ROLE_NULL));
                }

                if (role == 6)//only saleman
                {
                    //check Workflow
                    if (!appService.checkRequireWorkflowTarget(user.getCompanys().getId(), user.getId(), dataService)) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.ERROR_MCP_SALES_NULL));
                    }
                    appService.createMCP(user.getId(), dataService);
                } else if (role == 4)//sale sup
                {
                    Date monday = DateUtils.getMondayOfWeek();
                    //lay danh sach tat ca nhan vien thuoc sale sup
                    List<Integer> userIds = appService.getListUserIdBySup(user.getId(), user.getCompanys().getId(), dataService);
                    for (int i = 0; i < 7; i++) {
                        for (int id : userIds) {
                            appService.createMCP(id, DateUtils.addDay(monday, i), dataService);
                        }
                    }
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_PASSWORD_NOT_MATCH));
            }

            LinkedHashMap contents = new LinkedHashMap();
            if (branch != null) {
                contents.put("branch", branch);
            }

            LinkedHashMap userInfo = new LinkedHashMap();
            userInfo.put("id", user.getId());
            userInfo.put("company_id", user.getCompanys().getId());
            userInfo.put("company_code", user.getCompanys().getCode());
            userInfo.put("company_package", user.getCompanys().getPackageService());
            userInfo.put("username", user.getUsername());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("token", token);
            LinkedHashMap userRole = new LinkedHashMap();
            userRole.put("id", role);
            userRole.put("name", roleName);
            userInfo.put("userRole", userRole);
            contents.put("userInfo", userInfo);

            contents.put("lastUpdateConfig", user.getCompanys().getUpdatedAt() == null ? user.getCompanys().getCreatedAt() : user.getCompanys().getUpdatedAt());

            //get CompanyConstant
            //CompanyConstant companyConstant = appService.getCompanyConstant(user.getCompanys().getId(), dataService);
            companyConstant.setServerTime(new Date().getTime());
            Version version = appService.getVersion(user.getCompanys().getId(), dataService);
            companyConstant.setAppVersion(version == null ? "0.0.1" : version.getVersion());
            contents.put("appConfig", companyConstant);

            //luu thong tin thiet bi
            equipment.setVersion(deviceApp.getAppVersion());
            equipment.setUpdatedAt(new Date());
            equipment.setUpdatedUser(equipment.getUsers().getId());
            dataService.updateRow(equipment);
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, contents));

        } catch (Exception e) {
            //donot known error
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.UNKNOW));
        }

    }

    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_WORKFLOW_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getWorkflowApp(HttpServletRequest request) {
        try {
            //get session
            Session session = (Session) request.getAttribute("session");
            if (session == null) {
                //not login
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.ERROR_LOGIN));
            }

            int userLoginId = session.getUserId();

            //lay role cua UserLogin
            List<UserRoleChannel> userRoleChannel = loginService.getListUserRoleChannel(userLoginId, dataService);
            if (userRoleChannel.isEmpty()) {
                //not login
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.ERROR_LOGIN));
            }

            //User userLogin = userRoleChannelList.get(0).getUsers();
            int role = userRoleChannel.get(0).getUserRoles().getId();
            User userLogin = userRoleChannel.get(0).getUsers();

            LinkedHashMap contents = new LinkedHashMap();
            LinkedHashMap appConfigLayout = new LinkedHashMap();

            appConfigLayout.put("logoUrl", userLogin.getCompanys().getLogoPath());
            appConfigLayout.put("bgColor", userLogin.getCompanys().getBgColor());
            appConfigLayout.put("textColor", userLogin.getCompanys().getTextColor());
            appConfigLayout.put("buttonBgColor", userLogin.getCompanys().getButtonBgColor());
            appConfigLayout.put("buttonBgColorOver", userLogin.getCompanys().getButtonBgColorOver());
            appConfigLayout.put("topBarBgColor", userLogin.getCompanys().getTopBarBGColor());

            List<CompanyConfig> companyConfigList = appService.getListCompanyConfig(userLogin.getCompanys().getId(), role, dataService);
            List pages = new LinkedList();
            LinkedHashMap pageApp;
            int companyConfigHistory = 0;
            for (CompanyConfig companyConfig : companyConfigList) {
                if (companyConfig.getName().toLowerCase().equals("sales_page")) {
                    companyConfigHistory = companyConfig.getId();
                }

                pageApp = new LinkedHashMap();
                pageApp.put("pageId", companyConfig.getName());
                List buttons = new LinkedList();

                List<CompanyConfigDetails> companyConfigDetailsList = appService.getListCompanyConfigDetails(companyConfig.getId(), dataService);
                LinkedHashMap button;
                for (CompanyConfigDetails companyConfigDetails : companyConfigDetailsList) {
                    button = new LinkedHashMap();
                    button.put("actionId", companyConfigDetails.getCode());
                    button.put("name", companyConfigDetails.getContent());
                    buttons.add(button);
                }
                pageApp.put("buttons", buttons);
                pages.add(pageApp);
            }

            appConfigLayout.put("pages", pages);
            contents.put("appConfigLayout", appConfigLayout);

            //get avaliableFunction
            List<LinkedHashMap> historyFunctions = new ArrayList<>();
            LinkedHashMap buttonSale = new LinkedHashMap();
            LinkedHashMap buttonOrder = new LinkedHashMap();

            List<CompanyConfigDetails> historyFunctionList = appService.getListHistoryFunctionDetails(companyConfigHistory, dataService);
            for (CompanyConfigDetails companyConfigDetails : historyFunctionList) {
                LinkedHashMap button = new LinkedHashMap();
                if (companyConfigDetails.getCode().equals("sell_page")
                        || companyConfigDetails.getCode().equals("sales_page")
                        || companyConfigDetails.getCode().equals("direct_sales_page")) {
                    if (buttonSale.isEmpty()) {
                        buttonSale.put("actionId", "sales_page");
                        buttonSale.put("name", "BÁN HÀNG");
                        historyFunctions.add(buttonSale);
                    }
                } else if (companyConfigDetails.getCode().equals("order_via_phone_page")
                        || companyConfigDetails.getCode().equals("sales_via_phone_page")
                        || companyConfigDetails.getCode().equals("order_page")) {
                    if (buttonOrder.isEmpty()) {
                        buttonOrder.put("actionId", "order_page");
                        buttonOrder.put("name", "ĐẶT HÀNG");
                        historyFunctions.add(buttonOrder);
                    }
                } else {
                    button.put("actionId", companyConfigDetails.getCode());
                    button.put("name", companyConfigDetails.getContent());
                    historyFunctions.add(button);
                }
            }
            contents.put("historyFunctions", historyFunctions);

            //get list Workflow Type
            List<WorkflowType> workflowTypeCareList = appService.getListWorkflowType(1, dataService);
            List<WorkflowType> workflowTypeSaleList = appService.getListWorkflowType(2, dataService);

            List<LinkedHashMap> workflowTypeListMap = new ArrayList();
            for (WorkflowType workflowType : workflowTypeCareList) {
                LinkedHashMap workflowTypeMap = new LinkedHashMap<>();
                workflowTypeMap.put("id", workflowType.getId());
                workflowTypeMap.put("workFlowTypeCode", workflowType.getCode());
                workflowTypeMap.put("workFlowTypeName", workflowType.getName());
                List<Workflow> workflowList = appService.getListWorkflowByWorkflowType(workflowType.getId(), userLogin.getCompanys().getId(), dataService);
                if (!workflowList.isEmpty()) {
                    processWorkflow(workflowList);
                    workflowTypeMap.put("workflow", workflowList);
                    workflowTypeListMap.add(workflowTypeMap);
                }
            }
            contents.put("workflowCustomerCare", workflowTypeListMap);

            workflowTypeListMap = new ArrayList();
            for (WorkflowType workflowType : workflowTypeSaleList) {
                LinkedHashMap workflowTypeMap = new LinkedHashMap<>();
                workflowTypeMap.put("id", workflowType.getId());
                workflowTypeMap.put("workFlowTypeCode", workflowType.getCode());
                workflowTypeMap.put("workFlowTypeName", workflowType.getName());
                List<Workflow> workflowList = appService.getListWorkflowByWorkflowType(workflowType.getId(), userLogin.getCompanys().getId(), dataService);
                if (!workflowList.isEmpty()) {
                    processWorkflow(workflowList);
                    workflowTypeMap.put("workflow", workflowList);
                    workflowTypeListMap.add(workflowTypeMap);
                }
            }
            contents.put("workflowSale", workflowTypeListMap);

            String[] filters = {"companys", "workflowTypes"};
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, contents), filters);

        } catch (Exception ex) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.UNKNOW));
        }
    }

    private void processWorkflow(List<Workflow> workflowList) {
        if (!workflowList.isEmpty()) {
            for (Workflow workflow : workflowList) {
                List<WorkflowDetails> list = appService.getListWorkflowDetails(workflow.getId(), dataService);
                List<WorkflowDetails> option_select = new ArrayList<>();
                List<WorkflowDetails> option_check = new ArrayList<>();
                List<WorkflowDetails> option_check_update = new ArrayList<>();
                if (!list.isEmpty()) {
                    for (WorkflowDetails workflowDetails : list) {
                        workflowDetails.setDetails(appService.getListWorkflowByWorkflowDetails(workflowDetails.getId(), dataService));
                        processWorkflow(workflowDetails.getDetails());
                        if (workflowDetails.getActionType() == 1) {
                            option_select.add(workflowDetails);
                        } else if (workflowDetails.getActionType() == 2) {
                            option_check_update.add(workflowDetails);
                        } else {
                            option_check.add(workflowDetails);
                        }
                    }
                }
                workflow.setOption_select(option_select);
                workflow.setOption_check(option_check);
                workflow.setOption_check_update(option_check_update);
            }
        }
    }

    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_CHANGE_PASSWORD_APP, method = RequestMethod.POST)
    public @ResponseBody
    String changePassword(HttpServletRequest request) {
        try {
            Session session = (Session) request.getAttribute("session");
            if (session == null) {
                //not login
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.ERROR_LOGIN));
            }
            int userId = session.getUserId();

            String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                    .toString();
            if (jsonString != null) {
                SearchObject searchObject;

                try {
                    searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
                } catch (Exception ex) {
                    return MsalesJsonUtils.validateFormat(ex);
                }
                LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

                if (searchObject.getPasswordOld() == null) {
                    hashErrors.put("passwordOld", MsalesValidator.NOT_NULL);
                }

                if (searchObject.getPasswordNew() == null || searchObject.getPasswordNew().trim().isEmpty()) {
                    hashErrors.put("passwordNew", MsalesValidator.NOT_NULL_AND_EMPTY);
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                User user = appService.getUser(userId, searchObject.getPasswordOld(), dataService);
                if (user == null) {
                    //mat khau khong dung
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_PASSWORD_INCORRECT));
                } else {
                    //doi mat khau
                    user.setPassword(searchObject.getPasswordNew().toUpperCase());
                    user.setUpdatedUser(userId);
                    try {
                        dataService.updateRow(user);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                    } catch (Exception e) {
                        if (e instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(e);
                        }//else
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.UNKNOW));
                    }
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
            }
        } catch (Exception ex) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.UNKNOW));
        }
    }

    /**
     * ------------------------------------------------------------------------------------------------------------------------------------------- SALES MAN -------------------------------------------------------------------------------------------------------------------------------------------
     */
    /**
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_LIST_MCP_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getListMCPApp(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchObject.getImplementEmployeeId() == null) {
                hashErrors.put("implementEmployeeId", MsalesValidator.NOT_NULL);
            }

            if (searchObject.getBeginDate() == null) {
                hashErrors.put("beginDate", MsalesValidator.NOT_NULL);
            }

            if (searchObject.getFinishTime() == null) {
                hashErrors.put("finishTime", MsalesValidator.NOT_NULL);
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            try {
                LinkedHashMap<String, Object> contents = new LinkedHashMap();
                List<HashMap> list = appService.getListMCP(searchObject.getImplementEmployeeId(), searchObject.getBeginDate(), searchObject.getFinishTime(), dataService);
                List posLists = new ArrayList();
                LinkedHashMap pos;
                for (Iterator iterator = list.iterator();
                        iterator.hasNext();
                        posLists.add(pos)) {
                    HashMap mcpDetails = (HashMap) iterator.next();
                    //POS APP
                    pos = new LinkedHashMap();
                    pos.put("id", mcpDetails.get("posId"));
                    pos.put("mcpDetailsId", mcpDetails.get("mcpDetailsId"));
                    pos.put("posCode", mcpDetails.get("posCode"));
                    pos.put("name", mcpDetails.get("posName"));
                    pos.put("address", mcpDetails.get("address"));
                    pos.put("lat", mcpDetails.get("lat"));
                    pos.put("lng", mcpDetails.get("lng"));
                    pos.put("isVisited", mcpDetails.get("isVisited"));
                }

                contents.put("posLists", posLists);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, contents));
            } catch (Exception e) {
                //unknow error
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.UNKNOW));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_TARGETS_INFO_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getTargetsInfoApp(HttpServletRequest request) {
        String jsonString = request.getAttribute("contents").toString();
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            LinkedHashMap hashErrors = new LinkedHashMap();

            if (searchObject.getImplementEmployeeId() == null) {
                hashErrors.put("implementEmployeeId", MsalesValidator.NOT_NULL);
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            try {
                LinkedHashMap contents = new LinkedHashMap();
                LinkedHashMap target = new LinkedHashMap<>();

                MCP mcpSale = appService.getMCPSale(searchObject.getImplementEmployeeId(), new Date(), dataService);
                if (mcpSale == null) {
                    //bao loi
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.ERROR_MCP_SALES_NULL));
                }

                //set for months
                long totalTargetPerMonth = mcpSale.getSalesPerMonth() == null ? 0 : mcpSale.getSalesPerMonth();
                long totalTargetFocusPerMonth = mcpSale.getSalesFocusPerMonth() == null ? 0 : mcpSale.getSalesFocusPerMonth();
                target.put("total", totalTargetPerMonth);
                target.put("totalFocus", totalTargetFocusPerMonth);
                contents.put("targetPerMonths", target);

                //set for Solds
                target = new LinkedHashMap<>();
                Date fromDate = DateUtils.getFirstDayOfMonth();
                Date toDate = DateUtils.getLastDayOfMonth();
                List<Integer> posIdList = appService.getListPOSIdByUser(searchObject.getImplementEmployeeId(), DateUtils.addDay(fromDate, -6), DateUtils.addDay(toDate, 6), dataService);

                long totalSold = 0L;
                long totalSoldPerDay = 0L;
                long totalSoldFocusPerMonth = 0L;
                long totalSoldFocusPerDay = 0L;

                for (int posId : posIdList) {
                    List<SalesTrans> salesTransList = appService.getListSalesTransByPOS(posId, fromDate, toDate, 2, dataService);
                    for (SalesTrans salesTrans : salesTransList) {
                        boolean flag;
                        if (Objects.equals(salesTrans.getCreatedUser(), searchObject.getImplementEmployeeId())) {
                            flag = true;
                        } else {
                            flag = appService.checkMCPWeekByUser(searchObject.getImplementEmployeeId(), posId, DateUtils.getMonday(salesTrans.getCreatedAt()), DateUtils.getSunday(salesTrans.getCreatedAt()), dataService);
                        }
                        boolean isNow = salesTrans.getCreatedAt().compareTo(DateUtils.getShortNow()) >= 0 && salesTrans.getCreatedAt().compareTo(DateUtils.getNextDate(DateUtils.getShortNow())) < 0;
                        if (flag) {
                            List<SalesTransDetails> salesTransDetailsList = appService.getListSalesTransDetails(salesTrans.getId(), dataService);
                            for (SalesTransDetails salesTransDetails : salesTransDetailsList) {
                                totalSold += 1L * salesTransDetails.getQuantity() * salesTransDetails.getPrice();

                                if (isNow) {
                                    totalSoldPerDay += 1L * salesTransDetails.getQuantity() * salesTransDetails.getPrice();
                                }
                                if (salesTransDetails.getIsFocus()) {
                                    totalSoldFocusPerMonth += 1L * salesTransDetails.getQuantity() * salesTransDetails.getPrice();
                                    if (isNow) {
                                        totalSoldFocusPerDay += 1L * salesTransDetails.getQuantity() * salesTransDetails.getPrice();
                                    }
                                }
                            }
                        }
                    }

                    List<SalesOrder> salesOrderList = appService.getListSalesOrderByPOS(posId, fromDate, toDate, dataService);
                    for (SalesOrder salesOrder : salesOrderList) {
                        if (salesOrder.getStatuss().getId() == 18
                                || salesOrder.getStatuss().getId() == 19
                                || salesOrder.getStatuss().getId() == 20) {
                            boolean flag;
                            if (Objects.equals(salesOrder.getCreatedUsers().getId(), searchObject.getImplementEmployeeId())) {
                                flag = true;
                            } else {
                                flag = appService.checkMCPWeekByUser(searchObject.getImplementEmployeeId(), posId, DateUtils.getMonday(salesOrder.getCreatedAt()), DateUtils.getSunday(salesOrder.getCreatedAt()), dataService);
                            }
                            boolean isNow = salesOrder.getCreatedAt().compareTo(DateUtils.getShortNow()) >= 0 && salesOrder.getCreatedAt().compareTo(DateUtils.getNextDate(DateUtils.getShortNow())) < 0;
                            if (flag) {
                                List<SalesOrderDetails> salesOrderDetailsList = appService.getListSalesOrderDetails(salesOrder.getId(), dataService);
                                for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                                    totalSold += 1L * salesOrderDetails.getQuantity() * salesOrderDetails.getPrice();

                                    if (isNow) {
                                        totalSoldPerDay += 1L * salesOrderDetails.getQuantity() * salesOrderDetails.getPrice();
                                    }
                                    if (salesOrderDetails.getIsFocus()) {
                                        totalSoldFocusPerMonth += 1L * salesOrderDetails.getQuantity() * salesOrderDetails.getPrice();
                                        if (isNow) {
                                            totalSoldFocusPerDay += 1L * salesOrderDetails.getQuantity() * salesOrderDetails.getPrice();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

//              
                target.put("total", totalSoldPerDay);//total doi lai la so luong ban trong ngay
                target.put("soldPerMonth", totalSold);//tong doanh so thang
                target.put("totalFocusPerDay", totalSoldFocusPerDay);
                target.put("totalFocusPerMonth", totalSoldFocusPerMonth);
                contents.put("solds", target);

                //set for days
                target = new LinkedHashMap<>();
                Date now = DateUtils.getShortNow();
                long countDay = DateUtils.subDate(now, toDate);
                long totalTargetPerDay = (totalTargetPerMonth - totalSold) > 0 ? (totalTargetPerMonth - totalSold) / countDay : 0;
                long totalTargetFocusPerDay = (totalTargetFocusPerMonth - totalSoldFocusPerMonth) > 0 ? (totalTargetFocusPerMonth - totalSoldFocusPerMonth) / countDay : 0;

                target.put("total", totalTargetPerDay);
                target.put("totalFocus", totalTargetFocusPerDay);
                contents.put("targetPerDays", target);

                //set targetPOSNew            
                contents.put("targetPOSNew", mcpSale.getNewPOS());
                //set POSNew            
                contents.put("POSNew", appService.getPOSNew(searchObject.getImplementEmployeeId(), dataService));

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, contents));
            } catch (Exception e) {
                //unknow error
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.UNKNOW));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_STOCK_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getStockApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();

        try {
            //List<SalesStockGoods> listSalesStockGoods = dataService.executeSelectHQL(SalesStockGoods.class, hql, false, 0, 0);
            List<SalesStockGoods> listSalesStockGoods = appService.getStockApp(userId, dataService);
            LinkedHashMap contents = new LinkedHashMap<>();
            List listStockGoods = new ArrayList();

            if (!listSalesStockGoods.isEmpty()) {
                //list not empty
                int goodsCategoryId = listSalesStockGoods.get(0).getGoodss().getGoodsCategorys().getId();

                LinkedHashMap goodsCategory = new LinkedHashMap();
                goodsCategory.put("id", listSalesStockGoods.get(0).getGoodss().getGoodsCategorys().getId());
                goodsCategory.put("name", listSalesStockGoods.get(0).getGoodss().getGoodsCategorys().getName());

                List<LinkedHashMap> listGoods = new ArrayList<>();
                LinkedHashMap goods;
                LinkedHashMap stockGoods = new LinkedHashMap<>();

                for (SalesStockGoods salesStockGoods : listSalesStockGoods) {
                    if (salesStockGoods.getGoodss().getGoodsCategorys().getId() != goodsCategoryId) {
                        stockGoods.put("goodsCategory", goodsCategory);
                        stockGoods.put("goods", listGoods);
                        listStockGoods.add(stockGoods);
                        stockGoods = new LinkedHashMap<>();
                        goodsCategory = new LinkedHashMap();
                        listGoods = new ArrayList<>();
                        goodsCategoryId = salesStockGoods.getGoodss().getGoodsCategorys().getId();
                        goodsCategory.put("id", salesStockGoods.getGoodss().getGoodsCategorys().getId());
                        goodsCategory.put("name", salesStockGoods.getGoodss().getGoodsCategorys().getName());
                    }

                    goods = new LinkedHashMap();
                    goods.put("id", salesStockGoods.getGoodss().getId());
                    goods.put("name", salesStockGoods.getGoodss().getName());
                    goods.put("quantity", salesStockGoods.getQuantity());
                    listGoods.add(goods);

                    int goodsId = salesStockGoods.getGoodss().getId();
                    List<GoodsUnit> listGoodsUnit = appService.getListGoodsUnitByGoodsId(goodsId, dataService);
                    List<LinkedHashMap> listUnit = new ArrayList<>();
                    for (GoodsUnit goodsUnit : listGoodsUnit) {
                        LinkedHashMap unit = new LinkedHashMap();
                        unit.put("id", goodsUnit.getId());//unit of goodsUnit
                        unit.put("name", goodsUnit.getUnits().getName());
                        unit.put("quantity", goodsUnit.getQuantity());
                        unit.put("price", goodsUnit.getPrice());
                        listUnit.add(unit);
                    }
                    goods.put("units", listUnit);
                }

                stockGoods.put("goodsCategory", goodsCategory);
                stockGoods.put("goods", listGoods);
                listStockGoods.add(stockGoods);
            }

            contents.put("stockGoods", listStockGoods);

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, contents));
        } catch (Exception e) {
            //unknow error
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.UNKNOW));
        }
    }

    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_CREATE_USER_ROUTE_APP, method = RequestMethod.POST)
    public @ResponseBody
    String createUserRouteApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();

        String jsonString = request.getAttribute("contents").toString();
        if (jsonString != null) {
            UserRoute[] userRoutes;
            try {
                userRoutes = MsalesJsonUtils.getObjectFromJSON(jsonString, UserRoute[].class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            try {
                for (UserRoute userRoute : userRoutes) {
                    userRoute.setCreatedUser(userId);
                }
                dataService.insertArray(Arrays.asList(userRoutes));
                //insert success all
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, null));
            } catch (Exception e) {
                Exception ex = (Exception) e.getCause().getCause();
                if (ex instanceof ConstraintViolationException) {
                    return MsalesJsonUtils.jsonValidate(ex);
                }//else
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.UNKNOW));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Bán hàng qua điện thoại Lấy danh sách tất cả ĐBH của nhân viên
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_POST_LIST_BY_SALESMAN_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getPOSListBySalesmanApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //khong phai salesSup
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        //lay role cua UserLogin
        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int role = userRole.getId();

        List<Integer> userIdList = new ArrayList();
        if (role == 4)//giam sat
        {
            List<Integer> list = appService.getListUserIdBySup(userLoginId, userLogin.getCompanys().getId(), dataService);
            userIdList.addAll(list);
        } else {
            userIdList.add(userLoginId);
        }

        List<POS> list = appService.getListPOSByListUserId(userIdList, dataService);
        String[] filters = {"channels", "locations", "statuss", "hierarchy", "ownerName",
            "birthday", "ownerCode", "ownerCodeDate", "ownerCodeLocation", "tel", "mobile", "otherTel", "fax",
            "email", "website", "gpkd", "gpkdDate", "gpkdLocation", "note", "isActive", "beginAt", "endAt", "createdAt", "createdUser"};

        //POS APP
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), filters);

    }

    /**
     * Cham soc
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SAVE_CUSTOMERCARE_APP, method = RequestMethod.POST)
    public @ResponseBody
    String saveCustomerCareApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();
        //lay role cua UserLogin
        List<UserRoleChannel> userRoleChannelList = loginService.getListUserRoleChannel(userLoginId, dataService);
        if (userRoleChannelList.isEmpty()) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                //parse jsonString to a CustomerCareApp Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            MCPDetails mcpDetails = null;
            LinkedHashMap hashErrors = new LinkedHashMap();
            if (searchObject.getMcpDetailsId() == null) {
                hashErrors.put("mcpDetailsId", MsalesValidator.NOT_NULL);

            } else {
                mcpDetails = dataService.getRowById(searchObject.getMcpDetailsId(), MCPDetails.class);
                if (mcpDetails == null) {
                    hashErrors.put("mcpDetails", "ID=" + searchObject.getMcpDetailsId() + " " + MsalesValidator.NOT_EXIST);
                }
            }
            if (searchObject.getPosId() == null) {
                hashErrors.put("posId", MsalesValidator.NOT_NULL);
            }
            //khong kiem tra POS
            if (searchObject.getLat() == null) {
                hashErrors.put("lat", MsalesValidator.NOT_NULL);
            }
            if (searchObject.getLng() == null) {
                hashErrors.put("lng", MsalesValidator.NOT_NULL);
            }
            if (searchObject.getDetails() == null) {
                hashErrors.put("details", MsalesValidator.NOT_NULL);
            } else {
                for (CustomerCareDetails customerCareDetails : searchObject.getDetails()) {
                    boolean flag = false;
                    if (customerCareDetails.getWorkflowId() == null) {
                        hashErrors.put("workflowId", MsalesValidator.NOT_NULL_IN_ARRAY);
                        flag = true;
                    }
                    if (!flag) {
                        Workflow workflow = dataService.getRowById(customerCareDetails.getWorkflowId(), Workflow.class);
                        if (workflow == null) {
                            hashErrors.put("Workflow", "ID=" + customerCareDetails.getWorkflowId() + " " + MsalesValidator.NOT_EXIST);
                            flag = true;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            //kiem tra POS
            POS pos = dataService.getRowById(searchObject.getPosId(), POS.class);
            if (pos == null) {
                hashErrors.put("POS", "ID=" + searchObject.getPosId() + " " + MsalesValidator.NOT_EXIST);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
            }

            if (pos.getLat().compareTo(BigDecimal.ZERO) == 0 && pos.getLng().compareTo(BigDecimal.ZERO) == 0) {
                pos.setLat(searchObject.getLat());
                pos.setLng(searchObject.getLng());
                dataService.updateRow(pos);
            }

            //kiem tra mcpDetails da cham soc chua
            if (appService.checkPOSVisited(searchObject.getMcpDetailsId(), dataService)) {
                //da cham soc.
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.APP_MCPDETAILS_ISVISITED));
            }

            CustomerCareInformation customerCareInformation = new CustomerCareInformation();
            customerCareInformation.setMcpDetailsId(searchObject.getMcpDetailsId());
            customerCareInformation.setPosId(searchObject.getPosId());
            customerCareInformation.setStartCustomerCareAt(searchObject.getStartAt());
            customerCareInformation.setLat(searchObject.getLat());
            customerCareInformation.setLng(searchObject.getLng());
            customerCareInformation.setFinishCustomerCareAt(searchObject.getFinishAt());
            customerCareInformation.setCreatedUser(userLoginId);
            customerCareInformation.setImplementEmployeeId(mcpDetails.getImplementEmployees().getId());
            customerCareInformation.setCreatedAt(new Date());
            customerCareInformation.setUpdatedUser(0);
            customerCareInformation.setDeletedUser(userLoginId);//xem nhu dang xoa

            List insertList = new ArrayList();
            try {
                int ret = dataService.insert(customerCareInformation);

                LinkedHashMap customerCareDetailsIds = new LinkedHashMap();
                List list = new ArrayList();
                if (ret > 0) {
                    for (CustomerCareDetails customerCareDetails : searchObject.getDetails()) {
                        Workflow workflow = dataService.getRowById(customerCareDetails.getWorkflowId(), Workflow.class);
                        //da validate phia tren
                        if (workflow != null) {
                            //Tạo data để insert a custommercaredetails
                            customerCareDetails.setCustomerCareInformationId(ret);
                            customerCareDetails.setCreatedUser(userLoginId);
                            customerCareDetails.setPosId(searchObject.getPosId());
                            customerCareDetails.setWorkflowId(workflow.getId());
                            customerCareDetails.setCreatedAt(new Date());
                            customerCareDetails.setUpdatedUser(0);
                            customerCareDetails.setDeletedUser(userLoginId);//xem nhu xoa
                            //tao moi customerCareDetail
                            String contentString = customerCareDetails.getContent();

                            if (workflow.getIsImage() == 1) {
                                customerCareDetails.setContent("");
                                int customerCareDetailsId = dataService.insertRow(customerCareDetails);
                                list.add(customerCareDetailsId);
                                if (contentString != null && !contentString.trim().isEmpty()) {
                                    //luu hinh anh
                                    String branch = request.getAttribute(MsalesHttpHeader.BRANCH).toString();
                                    byte[] imageBytes = Base64.base64Decode(contentString.getBytes());

                                    //String rootPath = request.getSession().getServletContext().getRealPath("/");
                                    String folderPath = systemProperties.getProperty("system.imagesRoot") + "/" + systemProperties.getProperty("system.customerCareImage") + "/";
                                    String urlSave = branch
                                            + "/" + userRoleChannelList.get(0).getUsers().getCompanys().getId()
                                            + "/" + customerCareInformation.getId();

                                    File path = new File(folderPath + urlSave);
                                    if (!path.exists() || !path.isDirectory()) {
                                        path.mkdirs();
                                    }
                                    String fileUrl;

                                    try {
                                        String fileName = "CustomerCareDetails" + customerCareDetailsId + ".png";
                                        File file = new File(path.getPath() + "/" + fileName);
                                        file.createNewFile();

                                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                                        fileOutputStream.write(imageBytes);
                                        fileOutputStream.close();
                                        fileUrl = urlSave + "/" + fileName;
                                    } catch (IOException ex) {
                                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                                .create(MsalesStatus.SAVE_FILE_FAIL));
                                    }

                                    //set url image cho CustomeCareDetails co workflow type la isImage
                                    customerCareDetails.setContent(fileUrl);
                                    customerCareDetails.setId(customerCareDetailsId);
                                    customerCareDetails.setDeletedUser(0);//bo xoa
                                    insertList.add(customerCareDetails);
                                    //dataService.updateRow(customerCareDetails);
                                    //add id vao list tra ve neu Workflow la images
                                }
                            } else if (customerCareDetails.getOption_check() != null && !customerCareDetails.getOption_check().isEmpty()) {
                                for (Integer id : customerCareDetails.getOption_check()) {
                                    WorkflowDetails workflowDetails = dataService.getRowById(id, WorkflowDetails.class);
                                    if (workflowDetails != null) {
                                        CustomerCareDetails careDetails = new CustomerCareDetails();
                                        careDetails.setCustomerCareInformationId(ret);
                                        careDetails.setCreatedUser(userLoginId);
                                        careDetails.setPosId(searchObject.getPosId());
                                        careDetails.setWorkflowId(workflow.getId());
                                        careDetails.setWorkflowDetailsId(id);
                                        careDetails.setContent(workflowDetails.getContent());

                                        careDetails.setCreatedAt(new Date());
                                        careDetails.setUpdatedUser(0);
                                        careDetails.setDeletedUser(0);

                                        insertList.add(careDetails);
                                    }
                                }
                            } else if (customerCareDetails.getOption_select() != null && customerCareDetails.getOption_select() != null) {
                                WorkflowDetails workflowDetails = dataService.getRowById(customerCareDetails.getOption_select(), WorkflowDetails.class);
                                if (workflowDetails != null) {
                                    customerCareDetails.setWorkflowDetailsId(customerCareDetails.getOption_select());
                                    customerCareDetails.setContent(workflowDetails.getContent());

                                    customerCareDetails.setCreatedAt(new Date());
                                    customerCareDetails.setUpdatedUser(0);
                                    customerCareDetails.setDeletedUser(0);

                                    insertList.add(customerCareDetails);
                                }
                            } else if (customerCareDetails.getOption_check_update() != null && !customerCareDetails.getOption_check_update().isEmpty()) {
                                for (WorkflowDetails workflowDetails : customerCareDetails.getOption_check_update()) {
                                    CustomerCareDetails careDetails = new CustomerCareDetails();
                                    careDetails.setCustomerCareInformationId(ret);
                                    careDetails.setCreatedUser(userLoginId);
                                    careDetails.setPosId(searchObject.getPosId());
                                    careDetails.setWorkflowId(workflow.getId());
                                    careDetails.setWorkflowDetailsId(workflowDetails.getId());
                                    careDetails.setContent(workflowDetails.getContent());
                                    careDetails.setCreatedAt(new Date());
                                    careDetails.setUpdatedUser(0);
                                    careDetails.setDeletedUser(0);

                                    insertList.add(careDetails);
                                }
                            }

                        }
                    }

                    //set lai customerCareInformation not delete
                    customerCareInformation.setDeletedUser(0);
                    insertList.add(customerCareInformation);
                    if (!insertList.isEmpty()) {
                        try {
                            dataService.insertOrUpdateArray(insertList);
                        } catch (Exception ex) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.SQL_INSERT_FAIL, ex.getMessage()));
                        }

                    }
                }
                customerCareDetailsIds.put("customerCareDetailsIds", list);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, customerCareDetailsIds));
            } catch (Exception e) {
                Exception ex = (Exception) e.getCause().getCause();
                if (ex instanceof ConstraintViolationException) {
                    return MsalesJsonUtils.jsonValidate(ex);
                }//else
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.UNKNOW));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Luu hinh anh khi cham soc khach hang
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SAVE_CUSTOMERCARE_IMAGE_APP, method = RequestMethod.POST)
    public @ResponseBody
    String saveCustomerCareImageApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();
        //lay role cua UserLogin
        List<UserRoleChannel> userRolChannelList = loginService.getListUserRoleChannel(userLoginId, dataService);
        if (userRolChannelList.isEmpty()) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject[] searchObjects;

            try {
                //parse jsonString to a CustomerCareImageApp Object
                searchObjects = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject[].class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            try {
                if (searchObjects == null || searchObjects.length == 0) {
                    //return meessage
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NULL));
                }

                Date now = new Date();
                List updateList = new ArrayList();
                for (SearchObject searchObject : searchObjects) {
                    if (searchObject.getCustomerCareDetailsId() != null) {
                        LinkedHashMap hashErrors = new LinkedHashMap();
                        if (searchObject.getCustomerCareDetailsId() == null) {
                            hashErrors.put("customerCareDetailsId", MsalesValidator.NOT_NULL);
                        }
                        if (searchObject.getContent() == null || searchObject.getContent().trim().isEmpty()) {
                            hashErrors.put("content", MsalesValidator.NOT_NULL_AND_EMPTY);
                        }
                        if (!hashErrors.isEmpty()) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));

                        }

                        CustomerCareDetails customerCareDetails = dataService.getRowById(searchObject.getCustomerCareDetailsId(), CustomerCareDetails.class);
                        if (customerCareDetails == null) {
                            hashErrors.put("CustomerCareDetails", "ID=" + searchObject.getCustomerCareDetailsId() + " " + MsalesValidator.NOT_EXIST);
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        }

                        String branch = request.getAttribute(MsalesHttpHeader.BRANCH).toString();
                        byte[] imageBytes = Base64.base64Decode(searchObject.getContent().getBytes());
                        //String rootPath = request.getSession().getServletContext().getRealPath("/");
                        String folderPath = systemProperties.getProperty("system.imagesRoot") + "/" + systemProperties.getProperty("system.customerCareImage") + "/";
                        String urlSave = branch
                                + "/" + userRolChannelList.get(0).getUsers().getCompanys().getId()
                                + "/" + customerCareDetails.getCustomerCareInformations().getId();

                        File path = new File(folderPath + urlSave);
                        if (!path.exists() || !path.isDirectory()) {
                            path.mkdirs();
                        }
                        String fileUrl;

                        try {
                            String fileName = "CustomerCareDetails" + customerCareDetails.getId() + ".png";
                            File file = new File(path.getPath() + "/" + fileName);
                            file.createNewFile();

                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(imageBytes);
                            fileOutputStream.close();
                            fileUrl = urlSave + "/" + fileName;
                        } catch (IOException ex) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.SAVE_FILE_FAIL));
                        }
                        customerCareDetails.setContent(fileUrl);
                        customerCareDetails.setUpdatedAt(now);
                        updateList.add(customerCareDetails);
                        //dataService.updateRow(customerCareDetails);                       
                    }
                }

                if (!updateList.isEmpty()) {
                    try {
                        dataService.updateArray(updateList);
                    } catch (Exception ex) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_UPDATE_FAIL));
                    }
                }
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK));
            } catch (Exception e) {
                if (e instanceof ConstraintViolationException) {
                    return MsalesJsonUtils.jsonValidate(e);
                }//else
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.UNKNOW));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

    }

    /**
     * Search POS App
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SEARCH_POS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String searchPOSApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                //parse jsonString to a CustomerCareImageApp Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            if (searchObject != null) {
                if (searchObject.getPosCode() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("posCode", MsalesValidator.NOT_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                List<HashMap> list = appService.searchPOS(userId, searchObject.getPosCode(), dataService);
                List posLists = new ArrayList();
                LinkedHashMap pos;
                for (Iterator iterator = list.iterator();
                        iterator.hasNext();
                        posLists.add(pos)) {
                    HashMap mcpDetails = (HashMap) iterator.next();
                    //POS APP
                    pos = new LinkedHashMap();
                    pos.put("id", mcpDetails.get("posId"));
                    pos.put("mcpDetailsId", mcpDetails.get("mcpDetailsId"));
                    pos.put("posCode", mcpDetails.get("posCode"));
                    pos.put("name", mcpDetails.get("posName"));
                    pos.put("address", mcpDetails.get("address"));
                    pos.put("lat", mcpDetails.get("lat"));
                    pos.put("lng", mcpDetails.get("lng"));
                    pos.put("isVisited", mcpDetails.get("isVisited"));
                }

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, posLists));
            } //Channel from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        }//jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get POSInfo App
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_POS_INFO_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getPOSInfoApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();

        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                //parse jsonString to a CustomerCareImageApp Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (searchObject != null) {
                LinkedHashMap hashErrors = new LinkedHashMap();
                if (searchObject.getPosId() == null) {
                    hashErrors.put("posId", MsalesValidator.NOT_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                int posId = searchObject.getPosId();

                POS pos = dataService.getRowById(posId, POS.class);
                if (pos != null) {
                    LinkedHashMap content;
                    //POS APP
                    content = new LinkedHashMap();
                    content.put("id", pos.getId());
                    content.put("posCode", pos.getPosCode());
                    content.put("name", pos.getName());
                    content.put("address", pos.getAddress());
                    content.put("lat", pos.getLat());
                    content.put("lng", pos.getLng());

                    //check isVisited
                    content.put("isVisited", appService.checkVisited(posId, DateUtils.getShortNow(), dataService));
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, content));
                } else {
                    hashErrors.put("pos", "ID = " + posId + " " + MsalesValidator.NOT_EXIST);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

            } //pos from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * DuanND create POS Application
     *
     * @param request is jsonString have data to create POS
     * @return a message and posCode
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_CREATE_POS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String createPOSApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int userLoginId = session.getUserId();
        User userLogin = dataService.getRowById(userLoginId, User.class);

        //lay role cua UserLogin
        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userLogin == null || userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int role = userRole.getId();

        int createdUser = userLoginId;

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            POS pos;
            try {
                //parse jsonString to a POS Object
                pos = MsalesJsonUtils.getObjectFromJSON(jsonString, POS.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //validate
            //POS from json not null
            if (pos != null) {
                //validate info relation
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<>();

                if (pos.getName() == null || pos.getName().trim().isEmpty()) {
                    hashErrors.put("name", MsalesValidator.NOT_NULL_AND_EMPTY);
                }

                if (pos.getLocationId() == null) {
                    hashErrors.put("locationId", MsalesValidator.NOT_NULL);
                }

                if (pos.getAddress() == null || pos.getAddress().trim().isEmpty()) {
                    hashErrors.put("address", MsalesValidator.NOT_NULL_AND_EMPTY);
                }
                if (role == 4) {
                    //check createdUser neu la giam sat
                    if (pos.getCreatedUser() == null || pos.getCreatedUser() == 0) {
                        hashErrors.put("createdUser", MsalesValidator.NOT_NULL);
                    } else {
                        createdUser = pos.getCreatedUser();
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                Location location = null;

                if (pos.getLocationId() != null) {
                    location = dataService.getRowById(pos.getLocationId(), Location.class);
                    if (location == null) {
                        hashErrors.put("Location", "ID = " + pos.getLocationId() + " không tồn tại.");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                pos.setCreatedUser(createdUser);

                //set chanel cho POS
                //hien tai nhan vien chi thuoc 1 channel
                List<UserRoleChannel> createdUserRoleChannel = loginService.getListUserRoleChannel(createdUser, dataService);
                if (createdUserRoleChannel != null) {
                    pos.setChannelId(createdUserRoleChannel.get(0).getChannels().getId());
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.USER_ROLE_NULL));
                }
                //set StatusId
                //fix sau
                Date now = new Date();
                pos.setStatusId(5);//status 5 => hoat dong
                //set beginAT and endAt
                pos.setBeginAt(now);
                pos.setEndAt(now);

                //set posCode
                if (location.getLocationType() == 3) {
                    pos.setPosCode(appService.createPOSCode(userLogin.getCompanys().getId(), location, dataService));
                } else {
                    LinkedHashMap<String, String> hashErrors2 = new LinkedHashMap<>();
                    hashErrors2.put("Location", "Địa điểm với id = " + pos.getLocationId() + " không phải là cấp phường xã");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_VALUE_INVALID, hashErrors2));
                }

                pos.setHierarchy(0);
                pos.setIsActive(1);

                if (pos.getLat() == null) {
                    pos.setLat(BigDecimal.ZERO);
                }
                if (pos.getLng() == null) {
                    pos.setLng(BigDecimal.ZERO);
                }

                try {
                    //save POS to DB
                    //mo transaction
                    Transaction transaction = null;
                    org.hibernate.Session datasSession = null;
                    try {
                        datasSession = dataService.openSession();
                        transaction = datasSession.beginTransaction();

                        //insert POS
                        pos.setDeletedUser(0);
                        pos.setUpdatedUser(0);
                        datasSession.save(pos);

                        //create POSImg    
                        if (pos.getImages() != null && pos.getImages().length != 0) {
                            for (String image : pos.getImages()) {
                                String branch = request.getAttribute(MsalesHttpHeader.BRANCH).toString();
                                byte[] imageBytes = Base64.base64Decode(image.getBytes());
                                //String rootPath = request.getSession().getServletContext().getRealPath("/");

                                String folderPath = systemProperties.getProperty("system.imagesRoot") + "/" + systemProperties.getProperty("system.posImage") + "/";
                                String urlSave = branch
                                        + "/" + createdUserRoleChannel.get(0).getUsers().getCompanys().getId()
                                        + "/" + pos.getLocationId()
                                        + "/" + pos.getId();//posId

                                File path = new File(folderPath + urlSave);
                                if (!path.exists() || !path.isDirectory()) {
                                    path.mkdirs();
                                }

                                int length = path.listFiles().length;
                                String fileName = length + ".png";
                                File file = new File(path.getPath() + "/" + fileName);
                                file.createNewFile();

                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(imageBytes);
                                fileOutputStream.close();
                                urlSave += "/" + fileName;

                                POSImg posImg = new POSImg();
                                posImg.setIsFocus(0);
                                posImg.setPosId(pos.getId());
                                posImg.setPath(urlSave);

                                posImg.setCreatedUser(createdUser);
                                posImg.setCreatedAt(now);
                                posImg.setDeletedUser(0);
                                posImg.setUpdatedUser(0);

                                datasSession.save(posImg);
                            }
                        }

                        //innsert salesStock
                        SalesStock salesStock = new SalesStock();
                        salesStock.setPoss(pos);
                        salesStock.setStatusId(1);//hoat dong
                        salesStock.setCreatedUser(pos.getCreatedUser());
                        salesStock.setCreatedAt(now);
                        salesStock.setDeletedUser(0);
                        salesStock.setUpdatedUser(0);
                        datasSession.save(salesStock);

                        //kiem tra Workflow-
                        Workflow workflow = workflowService.getWorkflowByCode("NEW_POS", userLogin.getCompanys().getId(), dataService);
                        if (workflow != null) {
                            if (workflow.getIsRequired() == 1) {
                                //can approve?
                            } else if (workflow.getIsRequired() > 1) {
                                //dua vao MCP
                                //copy/tao mcp 
                                int mcpId = appService.createMCP(createdUser, dataService);
                                //createdMCP details cho POS moi
                                MCPDetails mcpDetails = new MCPDetails();
                                mcpDetails.setMcpId(mcpId);
                                mcpDetails.setLocationId(pos.getLocationId());
                                mcpDetails.setPosId(pos.getId());
                                mcpDetails.setImplementEmployeeId(createdUser);
                                //status = 1
                                mcpDetails.setStatusId(1);
                                Date finishDate = DateUtils.addSecond(DateUtils.getNextDate(DateUtils.getShortNow()), -1);
                                mcpDetails.setFinishTime(finishDate);
                                mcpDetails.setIsActive(1);
                                mcpDetails.setCreatedUser(createdUser);
                                mcpDetails.setCreatedAt(now);
                                mcpDetails.setDeletedUser(0);
                                mcpDetails.setUpdatedUser(0);

                                datasSession.save(mcpDetails);

                                if (workflow.getIsRequired() == 3) {
                                    //dua vao MCP và chăm sóc
                                    //cham soc DBH
                                    CustomerCareInformation customerCareInformation = new CustomerCareInformation();
                                    customerCareInformation.setMcpDetailsId(mcpDetails.getId());
                                    customerCareInformation.setPosId(pos.getId());
                                    customerCareInformation.setStartCustomerCareAt(new Date());
                                    customerCareInformation.setImplementEmployeeId(createdUser);
                                    customerCareInformation.setIsActiveChannel(1);
                                    customerCareInformation.setLat(pos.getLat());
                                    customerCareInformation.setLng(pos.getLng());
                                    customerCareInformation.setFinishCustomerCareAt(new Date());
                                    customerCareInformation.setCreatedUser(createdUser);
                                    customerCareInformation.setDeletedUser(0);
                                    customerCareInformation.setUpdatedUser(0);

                                    datasSession.save(customerCareInformation);
                                }
                            }
                        }

                        transaction.commit();
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, pos.getPosCode()));
                    } catch (Exception e) {
                        transaction.rollback();
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_INSERT_FAIL));
                    } finally {
                        datasSession.close();
                    }

                } catch (Exception e) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }
            } //POS from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Lich su cham soc
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_CUSTOMERCARE_HISTORY_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getCustomerCareHistoryApp(HttpServletRequest request) {

        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();

        Date now = DateUtils.getShortNow();
        Date fromDate = DateUtils.getMondayOfWeek();
        Date toDate = DateUtils.getSundayOfWeek();
        LinkedHashMap contents = new LinkedHashMap();
        List<Integer> totalNowPOSList = appService.getListPOSIdByUser(userId, now, now, dataService);
        List<Integer> totalPOSList = appService.getListPOSIdByUser(userId, fromDate, toDate, dataService);
        long totalPOS = totalNowPOSList.size();
        long totalCared = appService.getTotalCared(totalNowPOSList, dataService);
        long totalOrder = appService.getTotalOrder(totalPOSList, dataService);
        long totalSale = appService.getTotalSale(totalPOSList, dataService);
        long totalPriceOrder = appService.getTotalPriceOrder(totalPOSList, dataService);
        long totalPriceSale = appService.getTotalPriceSale(totalPOSList, dataService);

        contents.put("totalPOS", totalPOS);
        contents.put("totalCared", totalCared);
        contents.put("totalOrder", totalOrder);
        contents.put("totalSale", totalSale);
        contents.put("totalPriceOrder", totalPriceOrder);
        contents.put("totalPriceSale", totalPriceSale);

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, contents));

    }

    /**
     * Tra hang
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_RETURN_GOODS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String returnGoodsApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();
        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

//        if (!appService.checkRequireWorkflowReceive(userLogin.getCompanys().getId(), userLoginId, dataService)) {
//            //check nhan hang neu la workflow nhan hang isRequired = 1
//            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
//                    .create(MsalesStatus.ERROR_NOT_YET_RECEIVE));
//        }
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            //validate JSON
            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();
            if (searchObject.getTransCode() == null || searchObject.getTransCode().trim().isEmpty()) {
                hashErrors.put("transCode", MsalesValidator.NOT_NULL_AND_EMPTY);
            }
            if (searchObject.getType() != null) {
                if (searchObject.getType() < 1 || searchObject.getType() > 3) {
                    hashErrors.put("type", MsalesValidator.APP_RETURN_GOODS_TYPE_ERROR);
                } else if (searchObject.getType() != 3) {
                    if (searchObject.getId() == null) {
                        hashErrors.put("id", MsalesValidator.NOT_NULL);
                    }
                }
            } else {
                hashErrors.put("type", MsalesValidator.NOT_NULL);
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            String transCode = searchObject.getTransCode();
            if (appService.checkSalesTransCode(transCode, dataService)) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_TRANS_CODE_IS_EXIST));
            }

            //get List SaleStockGoods cua user            
            List<SalesStockGoods> salesStockGoodsUser = appService.getListSalesStockGoodsByType(userLoginId, searchObject.getType(), searchObject.getId(), dataService);

            if (salesStockGoodsUser.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.APP_GOODS_NO_HAVE_IN_STOCK));
            }

            //tao list SalesTranDetails
            List<SalesTransDetails> salesTransDetailsList = new ArrayList<>();

            for (int i = 0; i < salesStockGoodsUser.size(); i++) {
                //tao du lieu cho sales Trans details
                SalesTransDetails salesTransDetails = new SalesTransDetails();
                salesTransDetails.setGoodsId(salesStockGoodsUser.get(i).getGoodss().getId());
                salesTransDetails.setGoodsUnitId(salesStockGoodsUser.get(i).getGoodsUnits().getId());
                salesTransDetails.setSalesTransDate(new Date());
                salesTransDetails.setQuantity(salesStockGoodsUser.get(i).getQuantity());
                salesTransDetails.setPrice(salesStockGoodsUser.get(i).getGoodsUnits().getPrice());

                if (!salesStockGoodsUser.get(i).getGoodss().getIsFocus()) {
                    //check GoodsFocus
                    salesTransDetails.setIsFocus(appService.checkGoodsFocusByUserId(salesStockGoodsUser.get(i).getGoodss().getId(), userLoginId, dataService));
                } else {
                    salesTransDetails.setIsFocus(true);
                }
                //salesTransDetails.setIsFocus(false);//mac dinh 0/false
                salesTransDetails.setCreatedUser(userLoginId);
                salesTransDetails.setCreatedAt(new Date());
                salesTransDetails.setUpdatedUser(0);
                salesTransDetails.setDeletedUser(0);

                salesTransDetailsList.add(salesTransDetails);

                //tru het so luong cua san pham do trong kho cua User
                salesStockGoodsUser.get(i).setQuantity(0);
                salesStockGoodsUser.get(i).setUpdatedUser(userLoginId);
                salesStockGoodsUser.get(i).setUpdatedAt(new Date());
            }

            //tao salesTrans
            SalesTrans salesTrans = new SalesTrans();

            salesTrans.setTransCode(searchObject.getTransCode());
            salesTrans.setCompanyId(salesStockGoodsUser.get(0).getStocks().getSalemanUsers().getCompanys().getId());
            salesTrans.setTransType(3);//tra hang = 3
            salesTrans.setFromStockId(salesStockGoodsUser.get(0).getStocks().getId());
            salesTrans.setToStockId(null);
            salesTrans.setSalesTransDate(new Date());
            salesTrans.setTransStatus(1);//gan cung - chua co du lieu
            salesTrans.setCreatedUser(userLoginId);

            salesTrans.setCreatedAt(new Date());
            salesTrans.setUpdatedUser(0);
            salesTrans.setDeletedUser(0);

            Transaction transaction = null;
            org.hibernate.Session dataSession = null;
            try {
                dataSession = dataService.openSession();
                dataSession.save(salesTrans);

                transaction = dataSession.beginTransaction();

                for (SalesTransDetails salesTransDetails : salesTransDetailsList) {
                    salesTransDetails.setSalesTransId(salesTrans.getId());
                    dataSession.save(salesTransDetails);
                }
                for (SalesStockGoods salesStockGoods : salesStockGoodsUser) {
                    dataSession.update(salesStockGoods);
                }
                transaction.commit();

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK));
            } catch (Exception e) {
                transaction.rollback();
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.SQL_INSERT_FAIL));
            } finally {
                dataSession.close();
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Lich su Ban hang
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_SALES_TRANS_HISTORY_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getSalesTransHistoryApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();
            if (searchObject.getFromDate() == null) {
                hashErrors.put("fromDate", MsalesValidator.NOT_NULL);
            }
            if (searchObject.getToDate() == null) {
                hashErrors.put("toDate", MsalesValidator.NOT_NULL);
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            List<POS> posList = appService.getListPOSByMCP(Arrays.asList(new Integer[]{userId}), DateUtils.addDay(searchObject.getFromDate(), -6), DateUtils.addDay(searchObject.getToDate(), 6), dataService);
            LinkedHashMap contents = new LinkedHashMap();
            List<LinkedHashMap> posListMap = new ArrayList<>();
            int rootTotalQuantity = 0;
            long rootTotalPrice = 0;

            for (POS pos : posList) {
                LinkedHashMap posMap = new LinkedHashMap();
                posMap.put("posName", pos.getName());
                int quantity = 0;
                long total = 0;
                List<LinkedHashMap> goodsList = new ArrayList<>();
                List<SalesTrans> salesTransList = appService.getListSalesTransByPOS(pos.getId(), searchObject.getFromDate(), searchObject.getToDate(), 2, dataService);
                List<SalesOrder> salesOrderList = appService.getListSalesOrderByPOS(pos.getId(), searchObject.getFromDate(), searchObject.getToDate(), dataService);
                for (SalesTrans salesTrans : salesTransList) {
                    boolean flag;
                    if (salesTrans.getCreatedUser() == userId) {
                        flag = true;
                    } else {
                        flag = appService.checkMCPWeekByUser(userId, pos.getId(), DateUtils.getMonday(salesTrans.getCreatedAt()), DateUtils.getSunday(salesTrans.getCreatedAt()), dataService);
                    }

                    if (flag) {
                        //get salesTransDetails
                        List<SalesTransDetails> salesTransDetailsList = appService.getListSalesTransDetails(salesTrans.getId(), dataService);
                        for (SalesTransDetails salesTransDetails : salesTransDetailsList) {
                            LinkedHashMap goodsMap = getGoodsMap(salesTransDetails.getGoodss().getId(), goodsList);
                            if (goodsMap != null) {
                                goodsMap.put("quantity", (int) goodsMap.get("quantity") + salesTransDetails.getQuantity());
                                goodsMap.put("total", (long) goodsMap.get("total") + 1L * salesTransDetails.getPrice() * salesTransDetails.getQuantity());
                            } else {
                                goodsMap = new LinkedHashMap();
                                goodsMap.put("id", salesTransDetails.getGoodss().getId());
                                goodsMap.put("goodsName", salesTransDetails.getGoodss().getName());
                                goodsMap.put("quantity", salesTransDetails.getQuantity());
                                goodsMap.put("total", 1L * salesTransDetails.getPrice() * salesTransDetails.getQuantity());
                                goodsList.add(goodsMap);
                            }

                            total += 1L * salesTransDetails.getPrice() * salesTransDetails.getQuantity();
                            quantity += salesTransDetails.getQuantity();
                        }
                    }
                }

                for (SalesOrder salesOrder : salesOrderList) {
                    if (salesOrder.getStatuss().getId() == 18
                            || salesOrder.getStatuss().getId() == 19
                            || salesOrder.getStatuss().getId() == 20) {
                        boolean flag;

                        if (salesOrder.getCreatedUsers().getId() == userId) {
                            flag = true;
                        } else {
                            flag = appService.checkMCPWeekByUser(userId, pos.getId(), DateUtils.getMonday(salesOrder.getCreatedAt()), DateUtils.getSunday(salesOrder.getCreatedAt()), dataService);
                        }

                        if (flag) {
                            //get salesTransDetails
                            List<SalesOrderDetails> salesOrderDetailsList = appService.getListSalesOrderDetails(salesOrder.getId(), dataService);
                            for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                                LinkedHashMap goodsMap = getGoodsMap(salesOrderDetails.getGoodss().getId(), goodsList);
                                if (goodsMap != null) {
                                    goodsMap.put("quantity", (int) goodsMap.get("quantity") + salesOrderDetails.getQuantity());
                                    goodsMap.put("total", (long) goodsMap.get("total") + 1L * salesOrderDetails.getPrice() * salesOrderDetails.getQuantity());
                                } else {
                                    goodsMap = new LinkedHashMap();
                                    goodsMap.put("id", salesOrderDetails.getGoodss().getId());
                                    goodsMap.put("goodsName", salesOrderDetails.getGoodss().getName());
                                    goodsMap.put("quantity", salesOrderDetails.getQuantity());
                                    goodsMap.put("total", 1L * salesOrderDetails.getPrice() * salesOrderDetails.getQuantity());
                                    goodsList.add(goodsMap);
                                }

                                total += 1L * salesOrderDetails.getPrice() * salesOrderDetails.getQuantity();
                                quantity += salesOrderDetails.getQuantity();
                            }
                        }
                    }
                }

                if (total > 0 && quantity > 0) {
                    rootTotalPrice += total;
                    rootTotalQuantity += quantity;
                    posMap.put("totalQuantity", quantity);
                    posMap.put("total", total);
                    posMap.put("goodsLists", goodsList);
                    posListMap.add(posMap);
                }
            }
            contents.put("totalQuantity", rootTotalQuantity);
            contents.put("total", rootTotalPrice);
            contents.put("posLists", posListMap);

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, contents));

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    //get linkedHashMap goodsMap from goodsList
    private LinkedHashMap getGoodsMap(int id, List<LinkedHashMap> goodsList) {
        for (LinkedHashMap goodsMap : goodsList) {
            if ((int) goodsMap.get("id") == id) {
                return goodsMap;
            }
        }
        return null;
    }

    /**
     * get List SoldGoodsByPOS App
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_LIST_SOLD_GOODS_BY_POS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getListSoldGoodsByPOSApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject objectPOSId;

            try {
                objectPOSId = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (objectPOSId.getPosId() == null) {
                hashErrors.put("posId", MsalesValidator.NOT_NULL);
            }
            if (objectPOSId.getMcpId() == null) {
                hashErrors.put("mcpId", MsalesValidator.NOT_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

//            POS pos = dataService.getRowById(objectPOSId.getPosId(), POS.class);
//            if(pos==null)
//            {
//                hashErrors.put("pos", MsalesValidator.NOT_EXIST);
//                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
//            }            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //Xử lí thời gian
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, objectPOSId.getMcpId());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Date fromDate = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date toDate = cal.getTime();

            //transType = 2 => ban hang
//            String hql = "SELECT SalesTransDetails.goodss.id AS id,SalesTransDetails.goodss.name AS name, SalesTransDetails.salesTranss.id AS salesTransId, SalesTransDetails.salesTranss.salesTransDate AS salesTransDate, "
//                    + " SalesTransDetails.quantity AS quantity"
//                    + " FROM SalesTransDetails AS SalesTransDetails"
//                    + " WHERE SalesTransDetails.salesTranss.toStocks.poss.id = '" + objectPOSId.getPosId() + "'"
//                    + " AND SalesTransDetails.salesTranss.salesTransDate <= '" + sdf.format(toDate) + "' AND SalesTransDetails.salesTranss.salesTransDate >= '" + sdf.format(fromDate) + "'"
//                    + " AND SalesTransDetails.salesTranss.transType = 2"
//                    + " AND SalesTransDetails.deletedUser = 0"
//                    + " AND SalesTransDetails.salesTranss.deletedUser = 0"
//                    + " AND SalesTransDetails.salesTranss.toStocks.deletedUser = 0";
            //  + " GROUP BY SalesTransDetails.goodss.id";
            String hql = salesSupService.getStringHQLOfgetListSoldGoodsByPOSApp(objectPOSId.getPosId(), sdf.format(toDate), sdf.format(fromDate));
            List<HashMap> salesTransDetailsList = new ArrayList<HashMap>();
            try {
                salesTransDetailsList = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            } catch (Exception ex) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.UNKNOW, ex.getMessage()));
            }
            String hqlSalesOrder = salesSupService.getStringHQLSalesOrderOfgetListSoldGoodsByPOSApp(objectPOSId.getPosId(), sdf.format(toDate), sdf.format(fromDate));
            try {
                List<HashMap> salesOrderDetailsList = dataService.executeSelectHQL(HashMap.class, hqlSalesOrder, true, 0, 0);
                if (!salesOrderDetailsList.isEmpty()) {
                    salesTransDetailsList.addAll(salesOrderDetailsList);
                }
            } catch (Exception ex) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.UNKNOW, ex.getMessage()));
            }
            //Lấy mảng salesTransId
            ArrayList<Integer> arraySalesTransId = new ArrayList<Integer>();
            for (int i = 0; i < salesTransDetailsList.size(); i++) {
                int id1 = Integer.parseInt(salesTransDetailsList.get(i).get("salesTransId").toString());
                boolean bool = true;
                for (int j = 0; j < i; j++) {
                    int id2 = Integer.parseInt(salesTransDetailsList.get(j).get("salesTransId").toString());
                    if (id1 == id2) {
                        bool = false;
                        break;
                    }
                }
                if (bool) {
                    arraySalesTransId.add(id1);
                }
            }
            List<ListSoldGoodsByPOS> listSoldGoodsByPOSs = new ArrayList<ListSoldGoodsByPOS>();
            for (Integer id : arraySalesTransId) {
                ListSoldGoodsByPOS soldGoodsByPOS = new ListSoldGoodsByPOS();
                List<Goods> goods = new ArrayList<>();
                for (HashMap salesTransDetails : salesTransDetailsList) {
                    int salesTransId = Integer.parseInt(salesTransDetails.get("salesTransId").toString());
                    if (id == salesTransId) {
                        Goods goods2 = new Goods();
                        goods2.setId((Integer) salesTransDetails.get("id"));
                        goods2.setName((String) salesTransDetails.get("name"));
                        goods2.setQuantity((Integer) salesTransDetails.get("quantity"));
                        goods.add(goods2);
                        soldGoodsByPOS.setSoldDate((Date) salesTransDetails.get("salesTransDate"));
                    }
                }
                soldGoodsByPOS.setGoodss(goods);
                listSoldGoodsByPOSs.add(soldGoodsByPOS);
            }

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listSoldGoodsByPOSs));

        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

    }

    /**
     * Giam sat don dat hang
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SEARCH_SUP_ORDER_APP, method = RequestMethod.POST)
    public @ResponseBody
    String searchSupOrderApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();
        User userLogin = dataService.getRowById(userId, User.class);
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        UserRole userRole = loginService.getUserRole(userId, dataService);
        if (userRole == null || userRole.getId() != 4) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchObject.getUserId() == null) {
                hashErrors.put("userId", MsalesValidator.NOT_NULL);
            }
            if (searchObject.getFromDate() == null) {
                hashErrors.put("fromDate", MsalesValidator.NOT_NULL);
            }
            if (searchObject.getToDate() == null) {
                hashErrors.put("toDate", MsalesValidator.NOT_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            List<Integer> userIds = new ArrayList<>();
            if (searchObject.getUserId() == -1 || searchObject.getUserId() == 0) {
                //lay tat ca nhanvien của giam sat
                userIds.addAll(appService.getListUserIdBySup(userId, userLogin.getCompanys().getId(), dataService));
                //userIds.add(userId);
            } else {
                userIds.add(searchObject.getUserId());
            }
            List<POS> posList = appService.getListPOSByMCP(userIds, DateUtils.addDay(searchObject.getFromDate(), -6), DateUtils.addDay(searchObject.getToDate(), 6), dataService);
            List<SalesOrder> salesOrderList = new ArrayList<>();
            for (POS pos : posList) {
                salesOrderList.addAll(appService.getListSalesOrderByPOS(pos.getId(), searchObject.getFromDate(), searchObject.getToDate(), dataService));
            }

            LinkedHashMap contents = new LinkedHashMap();
            List<LinkedHashMap> posListMap = new ArrayList<>();
            int orderSuccess = 0;
            long totalAll = 0L;
            for (SalesOrder salesOrder : salesOrderList) {
                boolean flag = false;
                for (int id : userIds) {
                    if (salesOrder.getCreatedUsers().getId() == id) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    for (int id : userIds) {
                        if (appService.checkMCPWeekByUser(id, salesOrder.getPos().getId(), DateUtils.getMonday(salesOrder.getCreatedAt()), DateUtils.getSunday(salesOrder.getCreatedAt()), dataService)) {
                            flag = true;
                            break;
                        }
                    }
                }

                if (flag) {
                    LinkedHashMap item = new LinkedHashMap();
                    item.put("id", salesOrder.getId());
                    item.put("posId", salesOrder.getPos().getId());
                    item.put("posName", salesOrder.getPos().getName());

                    int quantity = 0;
                    long total = 0L;
                    List<LinkedHashMap> goodsList = new ArrayList<>();
                    List<SalesOrderDetails> salesOrderDetailsList = appService.getListSalesOrderDetails(salesOrder.getId(), dataService);
                    for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                        LinkedHashMap goodsMap = new LinkedHashMap();
                        goodsMap.put("id", salesOrderDetails.getGoodss().getId());
                        goodsMap.put("name", salesOrderDetails.getGoodss().getName());
                        goodsMap.put("quantity", salesOrderDetails.getQuantity());
                        goodsMap.put("total", 1L * salesOrderDetails.getPrice() * salesOrderDetails.getQuantity());
                        goodsList.add(goodsMap);

                        total += 1L * salesOrderDetails.getPrice() * salesOrderDetails.getQuantity();
                        quantity += salesOrderDetails.getQuantity();
                    }
                    item.put("goodsList", goodsList);
                    item.put("totalPrice", total);
                    item.put("totalQuantity", quantity);
                    item.put("statuss", salesOrder.getStatuss());
                    posListMap.add(item);

                    if (salesOrder.getStatuss().getId() == 18
                            || salesOrder.getStatuss().getId() == 19
                            || salesOrder.getStatuss().getId() == 20) {
                        orderSuccess++;
                    }
                    totalAll += total;
                }
            }
            contents.put("posList", posListMap);
            contents.put("orderSuccess", orderSuccess);
            contents.put("total", totalAll);

            String[] filter = {"statusTypes"};

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, contents), filter);
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

    }

    /**
     * update SupPOS App
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_UPDATE_SUP_POS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String updateSupPOSApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SupPOSApp supPOSApp;

            try {
                supPOSApp = MsalesJsonUtils.getObjectFromJSON(jsonString, SupPOSApp.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (supPOSApp.getId() == null) {
                hashErrors.put("id", MsalesValidator.NOT_NULL);
            }
            if (supPOSApp.getLat() == null) {
                hashErrors.put("lat", MsalesValidator.NOT_NULL);
            }
            if (supPOSApp.getLng() == null) {
                hashErrors.put("lng", MsalesValidator.NOT_NULL);
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            POS pos = dataService.getRowById(supPOSApp.getId(), POS.class);
            if (pos == null) {
                hashErrors.put("pos", MsalesValidator.NOT_EXIST);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
            }

            try {
                pos.setLat(supPOSApp.getLat());
                pos.setLng(supPOSApp.getLng());
                //update POS to DB
                int ret = dataService.updateRow(pos);

                //update success
                if (ret > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, null));
                } //update failed
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_UPDATE_FAIL));
                }
            } catch (Exception e) {
                if (e instanceof ConstraintViolationException) {
                    return MsalesJsonUtils.jsonValidate(e);
                }//else
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.UNKNOW));
            }

        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

    }

    /**
     * Lich su Dat hang
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_ORDER_LIST_HISTORY_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getOrderListHistoryApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();
            if (searchObject.getFromDate() == null) {
                hashErrors.put("fromDate", MsalesValidator.NOT_NULL);
            }
            if (searchObject.getToDate() == null) {
                hashErrors.put("toDate", MsalesValidator.NOT_NULL);
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            //long numberOfOrder = appService.countSalesOrder(userId, searchObject.getFromDate(), searchObject.getToDate(), dataService);
            List<POS> posList = appService.getListPOSByMCP(Arrays.asList(new Integer[]{userId}), DateUtils.addDay(searchObject.getFromDate(), -6), DateUtils.addDay(searchObject.getToDate(), 6), dataService);
            LinkedHashMap contents = new LinkedHashMap();
            List<LinkedHashMap> posListMap = new ArrayList<>();
            int numberOfOrder = 0;
            long totalPrice = 0;

            for (POS pos : posList) {
                LinkedHashMap posMap = new LinkedHashMap();
                posMap.put("posName", pos.getName());
                int quantity = 0;
                long total = 0;
                List<SalesOrder> orderList = appService.getListSalesOrderByPOS(pos.getId(), searchObject.getFromDate(), searchObject.getToDate(), dataService);
                List<LinkedHashMap> orderListMap = new ArrayList<>();
                for (SalesOrder salesOrder : orderList) {
                    boolean flag;
                    if (salesOrder.getCreatedUsers().getId() == userId) {
                        flag = true;
                    } else {
                        flag = appService.checkMCPWeekByUser(userId, pos.getId(), DateUtils.getMonday(salesOrder.getCreatedAt()), DateUtils.getSunday(salesOrder.getCreatedAt()), dataService);
                    }

                    if (flag) {
                        numberOfOrder++;
                        LinkedHashMap orderMap = new LinkedHashMap();
                        orderMap.put("id", salesOrder.getId());
                        orderMap.put("createdAt", salesOrder.getCreatedAt());
                        orderMap.put("status", salesOrder.getStatuss().getName());
                        List<LinkedHashMap> goodsList = new ArrayList<>();
                        //get salesOrderDetails
                        List<SalesOrderDetails> salesOrderDetailsList = appService.getListSalesOrderDetails(salesOrder.getId(), dataService);
                        for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                            LinkedHashMap goodsMap = new LinkedHashMap();
                            goodsMap.put("goodsName", salesOrderDetails.getGoodss().getName());
                            goodsMap.put("quantity", salesOrderDetails.getQuantity());
                            goodsMap.put("total", 1L * salesOrderDetails.getPrice() * salesOrderDetails.getQuantity());
                            total += 1L * salesOrderDetails.getPrice() * salesOrderDetails.getQuantity();
                            quantity += salesOrderDetails.getQuantity();
                            goodsList.add(goodsMap);
                        }

                        orderMap.put("goodsList", goodsList);
                        orderListMap.add(orderMap);
                    }
                }

                if (!orderList.isEmpty()) {
                    totalPrice += total;
                    posMap.put("orderList", orderListMap);
                    posMap.put("totalQuantity", quantity);
                    posMap.put("total", total);
                    posListMap.add(posMap);
                }
            }
            contents.put("numberOfOrder", numberOfOrder);
            contents.put("total", totalPrice);
            contents.put("posLists", posListMap);

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, contents));

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Lay danh sach san pham User co the thu hoi
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_RECOVER_GOODS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getRecoverGoodsApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        List<GoodsCategory> categoryList = appService.getListGoodsCategory(userLoginId, userLogin.getCompanys().getId(), dataService);

        List<Goods> goodsList;

        if (categoryList.isEmpty()) {
            //lay tat ca san pham
            goodsList = appService.getListGoods(userLogin.getCompanys().getId(), dataService);
        } else {
            List<Integer> goodsCategoryIds = new ArrayList<>();
            for (GoodsCategory goodsCategory : categoryList) {
                goodsCategoryIds.add(goodsCategory.getId());
            }

            goodsList = appService.getListGoodsByListGoodsCategoryId(goodsCategoryIds, dataService);
        }

        List<Goods> goodsRecoverList = new ArrayList<>();

        for (Goods goods : goodsList) {
            if (goods.getIsRecover()) {
                goodsRecoverList.add(goods);
            }
        }

        List<StockGoods> list = new ArrayList<>();
        int goodsCategoryId = -1;
        StockGoods stockGoods = new StockGoods();
        for (Goods goods : goodsRecoverList) {
            if (goods.getGoodsCategorys().getId() != goodsCategoryId) {
                if (stockGoods.count() > 0) {
                    stockGoods.setGoodsCategory(stockGoods.getGoods().get(0).getGoodsCategorys());
                    list.add(stockGoods);
                }
                stockGoods = new StockGoods();
                goodsCategoryId = goods.getGoodsCategorys().getId();
            }
            //get danh Unit sach cua Goods
            List<GoodsUnit> goodsUnitList = appService.getListGoodsUnitByGoodsId(goods.getId(), dataService);
            for (GoodsUnit goodsUnit : goodsUnitList) {
                goodsUnit.setName(goodsUnit.getUnits().getName());
            }
            goods.setUnits(goodsUnitList);
            stockGoods.add(goods);
        }

        if (stockGoods.count() > 0) {
            stockGoods.setGoodsCategory(stockGoods.getGoods().get(0).getGoodsCategorys());
            list.add(stockGoods);
        }

        String[] filter = {"parents", "statuss", "goodsCategorys", "factor", "goodsCode", "order", "isRecover", "childUnitIds"};

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), filter);

    }

    /**
     * Lay thong tin chi tiet tung san pham trong đon dat hang
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_ORDER_GOODS_INFO_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getOrderGoodsInfo(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        String jsonString = request.getAttribute("contents").toString();
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (searchObject != null) {
                LinkedHashMap hashErrors = new LinkedHashMap<>();
                if (searchObject.getOrderId() == null) {
                    hashErrors.put("orderId", MsalesValidator.NOT_NULL);
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                ParameterList parameterList = new ParameterList("orders.id", searchObject.getOrderId());
                List<SalesOrderDetails> salesOrderDetailsList = dataService.getListOption(SalesOrderDetails.class, parameterList);

                List<StockGoods> list = new ArrayList<>();
                int goodsCategoryId = -1;
                StockGoods stockGoods = new StockGoods();
                for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                    if (salesOrderDetails.getGoodss().getGoodsCategorys().getId() != goodsCategoryId) {
                        if (stockGoods.count() > 0) {
                            stockGoods.setGoodsCategory(stockGoods.getGoods().get(0).getGoodsCategorys());
                            list.add(stockGoods);
                        }
                        stockGoods = new StockGoods();
                        goodsCategoryId = salesOrderDetails.getGoodss().getGoodsCategorys().getId();
                    }

                    //get danh Unit sach cua Goods                    
                    List<GoodsUnit> goodsUnitList = appService.getListGoodsUnitByGoodsId(salesOrderDetails.getGoodss().getId(), dataService);
                    for (GoodsUnit goodsUnit : goodsUnitList) {
                        goodsUnit.setName(goodsUnit.getUnits().getName());
                    }

                    salesOrderDetails.getGoodss().setUnits(goodsUnitList);
                    salesOrderDetails.getGoodss().setQuantity(salesOrderDetails.getQuantity());

                    //check Focus
                    if (!salesOrderDetails.getGoodss().getIsFocus()) {
                        salesOrderDetails.getGoodss().setIsFocus(salesOrderDetails.getIsFocus());
                    }
                    stockGoods.add(salesOrderDetails.getGoodss());
                }

                if (stockGoods.count() > 0) {
                    stockGoods.setGoodsCategory(stockGoods.getGoods().get(0).getGoodsCategorys());
                    list.add(stockGoods);
                }

                String[] filter = {"parents", "statuss", "goodsCategorys", "factor", "goodsCode", "order", "isRecover", "childUnitIds"};

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), filter);

            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * thu hoi san pham
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_RECOVER_GOODS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String recoverGoodsApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        String jsonString = request.getAttribute("contents").toString();
        if (jsonString != null) {
            ReceiveGoods receiveGoods;
            try {
                receiveGoods = MsalesJsonUtils.getObjectFromJSON(jsonString, ReceiveGoods.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (receiveGoods != null) {
                LinkedHashMap hashErrors = new LinkedHashMap<>();
                if (receiveGoods.getTransCode() == null || receiveGoods.getTransCode().trim().isEmpty()) {
                    hashErrors.put("transCode", MsalesValidator.NOT_NULL);
                }
                if (receiveGoods.getStockGoods() == null || receiveGoods.getStockGoods().isEmpty()) {
                    hashErrors.put("stockGoods", MsalesValidator.NOT_NULL);
                }
                if (receiveGoods.getPosId() == null) {
                    hashErrors.put("posId", MsalesValidator.NOT_NULL);
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //Kiem tra transcode
                String transCode = receiveGoods.getTransCode();
                if (appService.checkSalesTransCode(transCode, dataService)) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_TRANS_CODE_IS_EXIST));
                }

                //sort List<goods>
                Collections.sort(receiveGoods.getStockGoods());
                //tao list salesTransDetails
                List<SalesTransDetails> salesTransDetailsList = new ArrayList<>();
                //tao chuoi id cua goods
                List<Integer> goodsIds = new ArrayList<>();
                //Kiem tra co Goods nao rong ?
                for (Goods goods : receiveGoods.getStockGoods()) {
                    SalesTransDetails salesTransDetails = new SalesTransDetails();
                    //set property for salesOrderDetails
                    salesTransDetails.setGoodsId(goods.getId());

                    //id cua unit luc dua len la GoodsUnit
                    GoodsUnit goodsUnit = dataService.getRowById(goods.getUnit().getId(), GoodsUnit.class);
                    if (goodsUnit == null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.APP_GOODS_UNIT_NULL));
                    }

                    goods.setName(goodsUnit.getGoodss().getName());
                    salesTransDetails.setGoodsUnits(goodsUnit);
                    salesTransDetails.setQuantity(goods.getQuantity());

                    if (!goodsUnit.getGoodss().getIsFocus()) {
                        //check GoodsFocus
                        salesTransDetails.setIsFocus(appService.checkGoodsFocusByPOSId(goodsUnit.getGoodss().getId(), receiveGoods.getPosId(), dataService));
                    } else {
                        salesTransDetails.setIsFocus(true);
                    }

                    //salesTransDetails.setIsFocus(goodsTemp.getIsFocus());
                    salesTransDetails.setPrice(goodsUnit.getPrice());

                    salesTransDetails.setSalesTransDate(new Date());
                    salesTransDetails.setCreatedUser(userLoginId);
                    salesTransDetails.setCreatedAt(new Date());
                    salesTransDetails.setUpdatedUser(0);
                    salesTransDetails.setDeletedUser(0);

                    salesTransDetailsList.add(salesTransDetails);
                    goodsIds.add(goods.getId());
                }

                //lấy id kho hàng của salesman                
                SalesStock salesStockUser = appService.getSalesStockUser(userLoginId, dataService);
                if (salesStockUser == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALESMAN_NO_HAVE_STOCK));
                }

                int userStockId = salesStockUser.getId();

                //Lấy Id kho hàng của điểm bán hàng
                SalesStock salesStockPOS = appService.getSalesStockPOS(receiveGoods.getPosId(), dataService);
                if (salesStockPOS == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_NO_HAVE_STOCK));
                }
                int posStockId = salesStockPOS.getId();

                //lay danh sach kho hang cua POS ung voi tung san pham
                List<SalesStockGoods> salesStockGoodsPOSList = appService.getListSalesStockGoodsByListGoodsId(posStockId, goodsIds, dataService);

                String notReceiveError = "";
                String overError = "";
                boolean flagOverError = false;
                boolean flagReceiveError = false;
                for (Goods goods : receiveGoods.getStockGoods()) {
                    int flag = 0;//chua nhap san pham

                    for (SalesStockGoods salesStockGoods : salesStockGoodsPOSList) {
                        if (Objects.equals(goods.getId(), salesStockGoods.getGoodss().getId())) {
                            if (goods.getQuantity() > salesStockGoods.getQuantity()) {
                                flag = -1;//so luong thu hoi qua lon
                            } else {
                                //OK
                                flag = 1;
                            }
                            break;
                        }
                    }
                    if (flag == 0) {
                        notReceiveError += goods.getName() + ", ";
                        flagReceiveError = true;
                    } else if (flag == -1) {
                        overError += goods.getName() + ", ";
                        flagOverError = true;
                    }
                }

                if (flagReceiveError || flagOverError) {
                    //co loi
                    LinkedHashMap error = new LinkedHashMap();
                    if (flagReceiveError) {
                        notReceiveError = notReceiveError.substring(0, notReceiveError.length() - 2);
                        error.put(MsalesValidator.APP_POS_NOT_RECEIVE_GOODS, notReceiveError);
                    }
                    if (flagOverError) {
                        overError = overError.substring(0, overError.length() - 2);
                        error.put(MsalesValidator.APP_OVER_GOODS_POS, overError);
                    }
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.ERROR_SALES_STOCK, error));
                }

                //set data for Sales_Trans
                SalesTrans salesTrans = new SalesTrans();
                salesTrans.setTransCode(receiveGoods.getTransCode());
                salesTrans.setFromStockId(posStockId);
                salesTrans.setToStockId(userStockId);
                salesTrans.setCompanyId(userLogin.getCompanys().getId());
                salesTrans.setTransType(4);//type = 4 thu hoi
                salesTrans.setTransStatus(1);
                salesTrans.setSalesTransDate(new Date());
                salesTrans.setCreatedUser(userLoginId);
                salesTrans.setCreatedAt(new Date());
                salesTrans.setDeletedUser(0);
                salesTrans.setUpdatedUser(0);

                Transaction transaction = null;
                org.hibernate.Session datasSession = null;
                try {
                    datasSession = dataService.openSession();
                    transaction = datasSession.beginTransaction();

                    datasSession.save(salesTrans);

                    //update salesStockGoods cua POS va SalesStock cua Nhan vien
                    ////get danh sach SaleStockGoods cua nhan vien ung voi tung san pham trong don hang
                    List<SalesStockGoods> salesStockGoodsUser = new ArrayList<>();
                    //danh sach SalesStockGoods cua user
                    for (SalesTransDetails salesTransDetails : salesTransDetailsList) {
                        //kiem tra salesStock cua User. neu chua co thi new
                        SalesStockGoods salesStockGoods = appService.getSalesStockGoodsByGoodsId(userStockId, salesTransDetails.getGoodss().getId(), dataService);

                        if (salesStockGoods != null) {
                            salesStockGoods.setUpdatedAt(new Date());
                            salesStockGoods.setUpdatedUser(userLoginId);
                        } else {
                            salesStockGoods = new SalesStockGoods();
                            salesStockGoods.setDeletedUser(0);
                            salesStockGoods.setUpdatedUser(0);
                            salesStockGoods.setCreatedUser(userLoginId);
                            salesStockGoods.setGoodss(salesTransDetails.getGoodss());
                            salesStockGoods.setGoodsUnits(salesTransDetails.getGoodsUnits());
                            salesStockGoods.setIsActive(1);
                            salesStockGoods.setQuantity(0);
                            salesStockGoods.setStockId(userStockId);
                            salesStockGoods.setGoodsStatusId(15);
                            salesStockGoods.setActiveDate(new Date());
                        }

                        salesStockGoods.setQuantity(salesStockGoods.getQuantity() + salesTransDetails.getQuantity());
                        datasSession.saveOrUpdate(salesStockGoods);
                        //tim salesStock goods POS
                        for (SalesStockGoods salesStockGoodsPOS : salesStockGoodsPOSList) {
                            salesStockGoodsPOS.setQuantity(salesStockGoodsPOS.getQuantity() - salesTransDetails.getQuantity());
                            datasSession.update(salesStockGoodsPOS);
                        }

                        //gan salesTranId cho salesTranDetails
                        salesTransDetails.setSalesTransId(salesTrans.getId());
                        datasSession.save(salesTransDetails);
                    }

                    transaction.commit();
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));

                } catch (Exception e) {
                    transaction.rollback();
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                } finally {
                    datasSession.close();
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

    }

    /**
     * Nhap hang hoa vao he thong receive Goods App DuanND
     *
     * @param request is json have info goods, goodsCategory
     * @return a message, code.
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_RECEIVE_GOODS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String receiveGoodsApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int userLoginId = session.getUserId();
        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        String jsonString = request.getAttribute("contents").toString();
        if (jsonString
                != null) {
            ReceiveGoods receiveGoods;
            try {
                receiveGoods = MsalesJsonUtils.getObjectFromJSON(jsonString, ReceiveGoods.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (receiveGoods != null) {
                LinkedHashMap<String, Object> hashErrors = new LinkedHashMap<String, Object>();
                if (receiveGoods.getTransCode() == null || receiveGoods.getTransCode().isEmpty() || receiveGoods.getTransCode().trim().isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_TRANS_CODE_NULL));
                }
                if (receiveGoods.getStockGoods() == null || receiveGoods.getStockGoods().isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_STOCK_GOODS_NULL));
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //Kiểm tra xem transCode có đúng không
                String transCode = receiveGoods.getTransCode();

                if (appService.checkSalesTransCode(transCode, dataService)) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_TRANS_CODE_IS_EXIST));
                }

                //sort List<goods>
                Collections.sort(receiveGoods.getStockGoods());
                //tao list salesTransDetails
                List<SalesTransDetails> salesTransDetailsList = new ArrayList<>();
                //Kiểm tra xem có Goods nào rỗng không
                for (Goods goods : receiveGoods.getStockGoods()) {
                    SalesTransDetails salesTransDetails = new SalesTransDetails();
                    //set property for salesOrderDetails
                    salesTransDetails.setGoodsId(goods.getId());

                    //id cua unit luc dua len la GoodsUnit
                    GoodsUnit goodsUnit = dataService.getRowById(goods.getUnit().getId(), GoodsUnit.class);
                    if (goodsUnit == null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.APP_GOODS_UNIT_NULL));
                    }
                    salesTransDetails.setGoodsUnits(goodsUnit);
                    salesTransDetails.setQuantity(goods.getQuantity());

                    if (!goodsUnit.getGoodss().getIsFocus()) {
                        //check GoodsFocus
                        salesTransDetails.setIsFocus(appService.checkGoodsFocusByUserId(goodsUnit.getGoodss().getId(), userLoginId, dataService));
                    } else {
                        salesTransDetails.setIsFocus(true);
                    }

                    //salesTransDetails.setIsFocus(goodsTemp.getIsFocus());
                    salesTransDetails.setPrice(goodsUnit.getPrice());

                    salesTransDetails.setSalesTransDate(new Date());
                    salesTransDetails.setCreatedUser(userLoginId);
                    salesTransDetails.setCreatedAt(new Date());
                    salesTransDetails.setUpdatedUser(0);
                    salesTransDetails.setDeletedUser(0);

                    salesTransDetailsList.add(salesTransDetails);
                }

                //Lấy Id kho hàng của user
                ParameterList parameterList = new ParameterList(1, 1);
                parameterList.add("salemanUsers.id", userLoginId);
                parameterList.add("statuss.id", 1);//status = 1 => hoat dong
                List<SalesStock> userSalesStock = dataService.getListOption(SalesStock.class, parameterList);
                if (userSalesStock.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALESMAN_NO_HAVE_STOCK));
                }
                int userStockId = userSalesStock.get(0).getId();

                //set data for Sales_Trans
                SalesTrans salesTrans = new SalesTrans();
                salesTrans.setTransCode(receiveGoods.getTransCode());
                salesTrans.setFromStockId(null);
                salesTrans.setToStockId(userStockId);
                salesTrans.setCompanyId(userLogin.getCompanys().getId());
                salesTrans.setTransType(1);//type = 1 nhan hàng
                salesTrans.setTransStatus(1);
                salesTrans.setSalesTransDate(new Date());
                salesTrans.setCreatedUser(userLoginId);
                salesTrans.setCreatedAt(new Date());
                salesTrans.setDeletedUser(userLoginId);
                salesTrans.setDeletedAt(new Date());
                salesTrans.setUpdatedUser(0);

                int salesTransId;
                try {
                    //save SalesTrans
                    salesTransId = dataService.insert(salesTrans);
                } catch (Exception e) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
                if (salesTransId > 0) {
                    List<SalesStockGoods> salesStockGoodsUser = new ArrayList<>();
                    //danh sach SalesStockGoods cua user
                    for (SalesTransDetails salesTransDetails : salesTransDetailsList) {
                        //kiem tra salesStock cua User. neu chua co thi new
                        parameterList = new ParameterList(1, 1);
                        parameterList.add("goodss.id", salesTransDetails.getGoodss().getId());
                        parameterList.add("stocks.id", userStockId);
                        parameterList.add("isActive", 1);
                        List<SalesStockGoods> stockGoodsUser = dataService.getListOption(SalesStockGoods.class, parameterList);
                        if (!stockGoodsUser.isEmpty()) {
                            stockGoodsUser.get(0).setUpdatedAt(new Date());
                            stockGoodsUser.get(0).setUpdatedUser(userLoginId);
                            salesStockGoodsUser.add(stockGoodsUser.get(0));
                        } else {
                            SalesStockGoods temp = new SalesStockGoods();
                            temp.setDeletedUser(0);
                            temp.setUpdatedUser(0);
                            temp.setCreatedUser(userLoginId);
                            temp.setGoodss(salesTransDetails.getGoodss());
                            temp.setGoodsUnits(salesTransDetails.getGoodsUnits());
                            temp.setIsActive(1);
                            temp.setQuantity(0);
                            temp.setStockId(userStockId);
                            temp.setGoodsStatusId(15);
                            temp.setActiveDate(new Date());
                            salesStockGoodsUser.add(temp);
                        }
                        //gan salesTranId cho salesTranDetails
                        salesTransDetails.setSalesTransId(salesTransId);
                    }

                    //chinh lai so luong
                    for (int i = 0; i < salesTransDetailsList.size(); i++) {
                        //Cong so luong trong kho hang cua nhan vien
                        salesStockGoodsUser.get(i).setQuantity(
                                salesStockGoodsUser.get(i).getQuantity() + salesTransDetailsList.get(i).getQuantity());

                    }

                    ArrayList insertArray = new ArrayList();
                    insertArray.addAll(salesTransDetailsList);
                    insertArray.addAll(salesStockGoodsUser);

                    //chinh lai status cho SalesTrans
                    salesTrans.setTransStatus(1);//chua co gia tri
                    salesTrans.setDeletedUser(0);
                    salesTrans.setDeletedAt(null);
                    salesTrans.setId(salesTransId);

                    insertArray.add(salesTrans);
                    try {
                        dataService.insertOrUpdateArray(insertArray);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                    } catch (Exception ex) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.APP_TRANSACTIONS_FAIL));
                    }

                } //save fails
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * getGoodsSalesmanApp
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_GOODS_SALES_MAN_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getGoodsSalesmanApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        List<GoodsCategory> categoryList = appService.getListGoodsCategory(userLoginId, userLogin.getCompanys().getId(), dataService);

        List<Goods> goodsList;

        if (categoryList.isEmpty()) {
            //lay tat ca san pham
            goodsList = appService.getListGoods(userLogin.getCompanys().getId(), dataService);
        } else {
            List<Integer> goodsCategoryIds = new ArrayList<>();
            for (GoodsCategory goodsCategory : categoryList) {
                goodsCategoryIds.add(goodsCategory.getId());
            }
            goodsList = appService.getListGoodsByListGoodsCategoryId(goodsCategoryIds, dataService);
        }

        List<StockGoods> list = new ArrayList<>();
        int goodsCategoryId = -1;
        StockGoods stockGoods = new StockGoods();
        for (Goods goods : goodsList) {
            if (goods.getGoodsCategorys().getId() != goodsCategoryId) {
                if (stockGoods.count() > 0) {
                    stockGoods.setGoodsCategory(stockGoods.getGoods().get(0).getGoodsCategorys());
                    list.add(stockGoods);
                }
                stockGoods = new StockGoods();
                goodsCategoryId = goods.getGoodsCategorys().getId();
            }
            //get danh Unit sach cua Goods
            List<GoodsUnit> goodsUnitList = appService.getListGoodsUnitByGoodsId(goods.getId(), dataService);
            for (GoodsUnit goodsUnit : goodsUnitList) {
                goodsUnit.setName(goodsUnit.getUnits().getName());
            }
            goods.setUnits(goodsUnitList);
            stockGoods.add(goods);
        }

        if (stockGoods.count() > 0) {
            stockGoods.setGoodsCategory(stockGoods.getGoods().get(0).getGoodsCategorys());
            list.add(stockGoods);
        }

        String[] filter = {"parents", "statuss", "goodsCategorys", "factor", "goodsCode", "order", "isRecover", "childUnitIds"};

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), filter);

    }

    /**
     * Bán hàng trực tiếp
     *
     * @param request a json have data to sellGoodsApp
     * @return message, code and status
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SELL_GOODS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String sellGoodsApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int userLoginId = session.getUserId();
        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        String jsonString = request.getAttribute("contents").toString();
        if (jsonString != null) {
            ReceiveGoods receiveGoods;
            try {
                receiveGoods = MsalesJsonUtils.getObjectFromJSON(jsonString, ReceiveGoods.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (receiveGoods != null) {
                LinkedHashMap<String, Object> hashErrors = new LinkedHashMap<String, Object>();
                if (receiveGoods.getTransCode() == null || receiveGoods.getTransCode().isEmpty() || receiveGoods.getTransCode().trim().isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_TRANS_CODE_NULL));
                }
                if (receiveGoods.getSalesTransDate() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALES_TRANS_DATE));
                }
                if (receiveGoods.getPosId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_ID_NULL));
                }
                if (receiveGoods.getStockGoods() == null || receiveGoods.getStockGoods().isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_STOCK_GOODS_NULL));
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //Kiểm tra xem transCode có đúng không
                String transCode = receiveGoods.getTransCode();

                if (appService.checkSalesTransCode(transCode, dataService)) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_TRANS_CODE_IS_EXIST));
                }

                //Lấy danh sách StockGoods
                List<Goods> stockGoods = receiveGoods.getStockGoods();

                if (stockGoods.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.APP_GOODS_NULL, MsalesValidator.APP_GOODS_NULL));
                }
                //lấy id kho hàng của salesman                
                SalesStock salesStockUser = appService.getSalesStockUser(userLoginId, dataService);
                if (salesStockUser == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALESMAN_NO_HAVE_STOCK, MsalesValidator.APP_SALESMAN_NO_HAVE_STOCK));
                }
                int salesStockId = salesStockUser.getId();

                //Lấy Id kho hàng của điểm bán hàng
                SalesStock salesStockPOS = appService.getSalesStockPOS(receiveGoods.getPosId(), dataService);
                if (salesStockPOS == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_NO_HAVE_STOCK, MsalesValidator.APP_POS_NO_HAVE_STOCK));
                }
                int posStockId = salesStockPOS.getId();

                //set data for Sales_Trans
                SalesTrans salesTrans = new SalesTrans();
                salesTrans.setTransCode(transCode);
                //salesTrans.setFromStockId(salesStockId);//set NULL?
                salesTrans.setToStockId(posStockId);
                salesTrans.setCompanyId(userLogin.getCompanys().getId());
                salesTrans.setTransType(2);//type =2
                salesTrans.setTransStatus(1);
                salesTrans.setSalesTransDate(new Date());
                salesTrans.setCreatedUser(userLoginId);
                salesTrans.setCreatedAt(new Date());
                salesTrans.setDeletedUser(0);
                salesTrans.setUpdatedUser(0);

                Transaction transaction = null;
                org.hibernate.Session datasSession = null;
                try {
                    datasSession = dataService.openSession();
                    transaction = datasSession.beginTransaction();
                    datasSession.save(salesTrans);

                    boolean flagNotReceive = false;
                    boolean flagOver = false;
                    String notReveiveError = "";
                    String overError = "";

                    List<SalesTransDetails> salesTransDetailsList = new ArrayList<>();
                    List<SalesStockGoods> salesStockGoodsUserList = new ArrayList<>();
                    List<SalesStockGoods> salesStockGoodsPOSList = new ArrayList<>();

                    for (Goods goods : stockGoods) {
                        //get quantity from goods
                        int quantity = goods.getQuantity();

                        SalesTransDetails salesTransDetails = new SalesTransDetails();
                        salesTransDetails.setGoodsId(goods.getId());
                        //id cua unit luc dua len la GoodsUnit
                        GoodsUnit goodsUnit = dataService.getRowById(goods.getUnit().getId(), GoodsUnit.class);
                        if (goodsUnit == null) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.APP_GOODS_UNIT_NULL));
                        }
                        salesTransDetails.setGoodsUnits(goodsUnit);
                        salesTransDetails.setQuantity(quantity);

                        //lay thong tin goods
                        Goods goodsTemp = dataService.getRowById(goods.getId(), Goods.class);
                        if (goodsTemp == null) {
                            hashErrors.put("goods", "ID=" + goods.getId() + " " + MsalesValidator.NOT_EXIST);
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        }
                        if (goodsTemp.getIsFocus()) {
                            salesTransDetails.setIsFocus(goodsTemp.getIsFocus());
                        } else {
                            salesTransDetails.setIsFocus(appService.checkGoodsFocusByPOSId(goodsTemp.getId(), receiveGoods.getPosId(), dataService));
                        }
                        salesTransDetails.setPrice(goodsUnit.getPrice());
                        salesTransDetails.setSalesTransDate(receiveGoods.getSalesTransDate());
                        salesTransDetails.setSalesTransId(salesTrans.getId());
                        salesTransDetails.setCreatedUser(userLoginId);
                        salesTransDetails.setCreatedAt(new Date());
                        salesTransDetails.setUpdatedUser(0);
                        salesTransDetails.setDeletedUser(0);

                        //set lai de tinh KM
                        goods.setPrice(goodsUnit.getPrice());

                        SalesStockGoods salesStockGoodsUser = appService.getSalesStockGoodsByGoodsId(salesStockId, goods.getId(), dataService);
                        if (salesStockGoodsUser == null) {
                            notReveiveError += goodsTemp.getName() + ", ";
                            flagNotReceive = true;
                        } else {

                            //kiem tra salesStock cua POS da co san pham trong order chua. neu chua co thi new                       
                            SalesStockGoods salesStockGoodsPOS = appService.getSalesStockGoodsByGoodsId(posStockId, goods.getId(), dataService);
                            if (salesStockGoodsPOS != null) {
                                salesStockGoodsPOS.setUpdatedAt(new Date());
                                salesStockGoodsPOS.setUpdatedUser(userLoginId);
                            } else {
                                salesStockGoodsPOS = new SalesStockGoods();
                                salesStockGoodsPOS.setGoodss(salesStockGoodsUser.getGoodss());
                                salesStockGoodsPOS.setGoodsUnits(salesStockGoodsUser.getGoodsUnits());
                                salesStockGoodsPOS.setIsActive(1);
                                salesStockGoodsPOS.setQuantity(0);
                                salesStockGoodsPOS.setStockId(posStockId);
                                salesStockGoodsPOS.setGoodsStatusId(15);
                                salesStockGoodsPOS.setActiveDate(new Date());
                                salesStockGoodsPOS.setDeletedUser(0);
                                salesStockGoodsPOS.setUpdatedUser(0);
                                salesStockGoodsPOS.setCreatedUser(userLoginId);
                                salesStockGoodsPOS.setCreatedAt(new Date());
                            }

                            //kiem tra so luong
                            if (quantity > salesStockGoodsUser.getQuantity()) {
                                flagOver = true;
                                overError += goodsTemp.getName() + ", ";
                            }
                            //tru so luong trong kho hang cua nhan vien
                            salesStockGoodsUser.setQuantity(salesStockGoodsUser.getQuantity() - quantity);
                            //Cong so luong cho kho hang cua POS
                            salesStockGoodsPOS.setQuantity(salesStockGoodsPOS.getQuantity() + quantity);

                            salesTransDetailsList.add(salesTransDetails);
                            salesStockGoodsPOSList.add(salesStockGoodsPOS);
                            salesStockGoodsUserList.add(salesStockGoodsUser);
                        }
                    }

                    //kiem tra loi
                    if (flagNotReceive || flagOver) {
                        LinkedHashMap error = new LinkedHashMap();
                        if (flagNotReceive) {
                            notReveiveError = notReveiveError.substring(0, notReveiveError.length() - 2);
                            error.put(MsalesValidator.APP_SALESMAN_NOT_RECEIVE_GOODS, notReveiveError);
                        }
                        if (flagOver) {
                            overError = overError.substring(0, overError.length() - 2);
                            error.put(MsalesValidator.APP_OVER_ORDER_GOODS, overError);
                        }

                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.ERROR_SALES_STOCK, error));
                    }

                    for (SalesTransDetails salesTransDetails : salesTransDetailsList) {
                        datasSession.save(salesTransDetails);
                    }
                    for (SalesStockGoods salesStockGoods : salesStockGoodsPOSList) {
                        datasSession.saveOrUpdate(salesStockGoods);
                    }
                    for (SalesStockGoods salesStockGoods : salesStockGoodsUserList) {
                        datasSession.update(salesStockGoods);
                    }

                    transaction.commit();

                    try {
                        //ban hang thanh cong => kiem tra km                    
                        ReturnPromotionList returnPromotionList = new ReturnPromotionList();
                        List<Promotion> list = appService.getListPromotionCanByPOS(receiveGoods.getPosId(), receiveGoods.getStockGoods(), userLoginId, dataService);
                        if (list.isEmpty()) {
                            //ko co KM thoa
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                        }
                        returnPromotionList.setSalesOrderId(0);//set  null to 0
                        returnPromotionList.setSalesTransId(salesTrans.getId());
                        returnPromotionList.setPromotionList(list);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, returnPromotionList));
                    } catch (Exception ex) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                    }
                } catch (Exception e) {
                    transaction.rollback();
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                } finally {
                    datasSession.close();
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Đặt hàng
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SET_GOODS_ORDER_APP, method = RequestMethod.POST)
    public @ResponseBody
    String setGoodsOrderApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int userLoginId = session.getUserId();
        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        String jsonString = request.getAttribute("contents").toString();
        if (jsonString != null) {
            ReceiveGoods receiveGoods;
            try {
                receiveGoods = MsalesJsonUtils.getObjectFromJSON(jsonString, ReceiveGoods.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (receiveGoods != null) {
                LinkedHashMap<String, Object> hashErrors = new LinkedHashMap<>();
                if (receiveGoods.getTransCode() == null || receiveGoods.getTransCode().isEmpty() || receiveGoods.getTransCode().trim().isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_TRANS_CODE_NULL));
                }
                if (receiveGoods.getSalesTransDate() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALES_TRANS_DATE));
                }
                if (receiveGoods.getPosId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_ID_NULL));
                }
                if (receiveGoods.getStockGoods() == null || receiveGoods.getStockGoods().isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_STOCK_GOODS_NULL));
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //Kiểm tra xem transCode có đúng không
                String transCode = receiveGoods.getTransCode();
                if (appService.checkOrderCode(transCode, dataService)) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_TRANS_CODE_IS_EXIST));
                }

                //Lấy danh sách StockGoods
                List<Goods> stockGoods = receiveGoods.getStockGoods();

                if (stockGoods.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.APP_GOODS_NULL, MsalesValidator.APP_GOODS_NULL));
                }
                //Kiểm tra xem có Goods nào rỗng không
                for (Goods goods : stockGoods) {
                    if (goods == null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_GOODS_NULL, MsalesValidator.APP_GOODS_NULL));
                    }
                    if (goods.getUnit() == null) {
                        hashErrors.put("unit", MsalesValidator.NOT_NULL);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                    }
                }
                ParameterList parameterList = new ParameterList();
                List<SalesOrderDetails> salesOrderDetailsList = new ArrayList<>();
                for (Goods goods : stockGoods) {
                    SalesOrderDetails salesOrderDetails = new SalesOrderDetails();
                    //set property for salesOrderDetails
                    salesOrderDetails.setGoodsId(goods.getId());
                    //id cua unit luc dua len la GoodsUnit
                    GoodsUnit goodsUnit = dataService.getRowById(goods.getUnit().getId(), GoodsUnit.class);
                    if (goodsUnit == null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.APP_GOODS_UNIT_NULL));
                    }

                    salesOrderDetails.setGoodsUnits(goodsUnit);
                    salesOrderDetails.setQuantity(goods.getQuantity());

                    if (!goodsUnit.getGoodss().getIsFocus()) {
                        //check GoodsFocus
                        salesOrderDetails.setIsFocus(appService.checkGoodsFocusByPOSId(goodsUnit.getGoodss().getId(), receiveGoods.getPosId(), dataService));
                    } else {
                        salesOrderDetails.setIsFocus(true);
                    }
                    salesOrderDetails.setPrice(goodsUnit.getPrice());

                    salesOrderDetails.setSalesTransDate(receiveGoods.getSalesTransDate());
                    salesOrderDetails.setCreatedUser(userLoginId);
                    salesOrderDetails.setCreatedAt(new Date());
                    salesOrderDetails.setUpdatedUser(0);
                    salesOrderDetails.setDeletedUser(0);

                    salesOrderDetailsList.add(salesOrderDetails);
                }

                //set data for Sales_Order
                SalesOrder salesOrder = new SalesOrder();

                //set thong tin công ty
                salesOrder.setCompanyId(userLogin.getCompanys().getId());
                //Xét địa điểm POS DBH
                salesOrder.setPosId(receiveGoods.getPosId());
                //Lấy kho hàng của điểm bán hàng
                parameterList = new ParameterList("poss.id", receiveGoods.getPosId(), 1, 1);
                List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, parameterList);

                if (salesStocks.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_NO_HAVE_STOCK, MsalesValidator.APP_POS_NO_HAVE_STOCK));
                }

                //set id kho hàng
                salesOrder.setStockId(salesStocks.get(0).getId());
                //set ngày giao dịch
                salesOrder.setSalesTransDate(receiveGoods.getSalesTransDate());
                //sét trạng thái giao dịch
                salesOrder.setStatusId(13);//13 dang cho
                //xet TransCode
                salesOrder.setTransCode(transCode);
                //người tạo
                salesOrder.setCreatedUser(userLoginId);
                salesOrder.setCreatedAt(new Date());
                salesOrder.setUpdatedUser(0);
                salesOrder.setDeletedUser(userLoginId);
                salesOrder.setDeletedAt(new Date());

                int orderId;
                try {
                    //save SalesOrder 
                    orderId = dataService.insert(salesOrder);
                } catch (Exception e) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
                if (orderId > 0) {
                    //cap nhat lai salesOrderDetails
                    for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                        salesOrderDetails.setOrderId(orderId);
                    }
                    //cap nhat lai salesOrder
                    salesOrder.setId(orderId);
                    salesOrder.setDeletedAt(null);
                    salesOrder.setDeletedUser(0);

                    ArrayList insertArray = new ArrayList();
                    insertArray.addAll(salesOrderDetailsList);
                    insertArray.add(salesOrder);
                    try {
                        dataService.insertOrUpdateArray(insertArray);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                    } catch (Exception e) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.UNKNOW));
                    }
                } //save fails
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Danh sách đơn hàng của điểm bán hàng
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_ORDER_LIST_BY_POS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getOrderListByPOSApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //khong phai salesSup
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        //lay role cua UserLogin
        UserRole userRole = loginService.getUserRole(userLoginId, dataService);

        if (userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        //User userLogin = userRoleChannelList.get(0).getUsers();
        int role = userRole.getId();

        //list ids User
        List<Integer> ids = new ArrayList<>();
        //check salesSup       
        if (role == 4) {
            //sale sup
            List<Integer> userIdList = appService.getListUserIdBySup(userLoginId, userLogin.getCompanys().getId(), dataService);
            ids.addAll(userIdList);
        } else {
            ids.add(userLoginId);
        }

        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                //parse jsonString to a search Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap hashErrors = new LinkedHashMap<>();
            if (searchObject.getFromDate() == null) {
                hashErrors.put("fromDate", MsalesValidator.NOT_NULL);
            }

            if (searchObject.getToDate() == null) {
                hashErrors.put("toDate", MsalesValidator.NOT_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            ArrayList<OrderItem> contents = new ArrayList<>();
            List<POS> posList = appService.getListPOSByMCP(ids, DateUtils.addDay(searchObject.getFromDate(), -6), DateUtils.addDay(searchObject.getToDate(), 6), dataService);

            for (POS pos : posList) {
                OrderItem orderItem = new OrderItem();
                LinkedHashMap POSInfo = new LinkedHashMap();
                POSInfo.put("posId", pos.getId());
                POSInfo.put("posName", pos.getName());
                POSInfo.put("posAddress", pos.getAddress());
                POSInfo.put("lat", pos.getLat());
                POSInfo.put("lng", pos.getLng());
                orderItem.setPOSInfo(POSInfo);

                List<SalesOrder> salesOrderList = appService.getListSalesOrderByPOS(pos.getId(), searchObject.getFromDate(), searchObject.getToDate(), dataService);

                //List<LinkedHashMap> orderListMap = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_LONG);
                if (!salesOrderList.isEmpty()) {
                    for (SalesOrder salesOrder : salesOrderList) {
                        boolean flag = false;
                        for (int id : ids) {
                            if (salesOrder.getCreatedUsers().getId() == id) {
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            //check theo MCP
                            for (int id : ids) {
                                flag = appService.checkMCPWeekByUser(id, pos.getId(), DateUtils.getMonday(salesOrder.getCreatedAt()), DateUtils.getSunday(salesOrder.getCreatedAt()), dataService);
                                if (flag) {
                                    break;
                                }
                            }
                        }

                        if (flag) {
                            LinkedHashMap orderInfo = new LinkedHashMap();
                            orderInfo.put("orderId", salesOrder.getId());
                            orderInfo.put("orderUserId", salesOrder.getCreatedUsers().getId());
                            orderInfo.put("orderUserName", salesOrder.getCreatedUsers().getLastName() + " " + salesOrder.getCreatedUsers().getFirstName());
                            orderInfo.put("orderDate", sdf.format(salesOrder.getCreatedAt()));
                            orderInfo.put("createdAt", salesOrder.getCreatedAt());
                            orderInfo.put("orderStatus", salesOrder.getStatuss().getValue());
                            orderInfo.put("orderStatusName", salesOrder.getStatuss().getName());

                            if (salesOrder.getStatuss().getId() == 18
                                    || salesOrder.getStatuss().getId() == 19
                                    || salesOrder.getStatuss().getId() == 20) {
                                orderInfo.put("delivery", salesOrder.getUpdatedAt() == null ? null : sdf.format(salesOrder.getUpdatedAt()));
                            } else {
                                orderInfo.put("delivery", null);
                            }
                            //orderListMap.add(orderInfo);
                            orderItem.getOrderList().add(orderInfo);
                        }
                    }
                    if (!orderItem.getOrderList().isEmpty()) {
//                        LinkedHashMap item = new LinkedHashMap();
//                        item.put("POSInfo", POSInfo);
//                        item.put("orderList", orderListMap);
                        contents.add(orderItem);
                    }
                }
            }

            Collections.sort(contents);
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, contents));
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Kiểm tra số lượng hàng trước khi giao hàng
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_CHECK_GOODS_IN_STOCK_APP, method = RequestMethod.POST)
    public @ResponseBody
    String checkGoodsInStockApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int userLoginId = session.getUserId();

        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                // parse jsonString to a Location Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // location from json not null
            if (searchObject != null) {
                // Order from json with correct Id
                LinkedHashMap hashErrors = new LinkedHashMap<>();
                if (searchObject.getOrderId() == null) {
                    hashErrors.put("orderId", MsalesValidator.NOT_NULL);
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));

                }

                // get Order from DB
                SalesOrder salesOrder = dataService.getRowById(searchObject.getOrderId(), SalesOrder.class);
                // Order not null
                if (salesOrder != null) {
                    //lấy danh sách Sales_Order_Details
                    List<SalesOrderDetails> salesOrderDetailsList = dataService.getListOption(SalesOrderDetails.class, new ParameterList("orders.id", searchObject.getOrderId()));
                    if (salesOrderDetailsList.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALES_ORDER_DETAILS_NULL));
                    }

                    //Lấy id kho hàng của salesman                    
                    SalesStock salesStockUser = appService.getSalesStockUser(userLoginId, dataService);
                    if (salesStockUser == null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALESMAN_NO_HAVE_STOCK));
                    }
                    int salesStockId = salesStockUser.getId();

                    boolean flagNotReceive = false;
                    boolean flagOver = false;
                    String notReveiveError = "";
                    String overError = "";

                    for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                        //Lấy số lượng hàng của Sales_man_stock                        
                        SalesStockGoods salesStockGoods = appService.getSalesStockGoodsByGoodsId(salesStockId, salesOrderDetails.getGoodss().getId(), dataService);
                        //Kiểm tra số lượng trong kho
                        if (salesStockGoods != null) {
                            if (salesOrderDetails.getQuantity() > salesStockGoods.getQuantity()) {
                                //loi hang dat so luong qua nhieu
                                overError += salesOrderDetails.getGoodss().getName() + ", ";
                                flagOver = true;
                            }
                        } else {
                            //loi hang hoa khong co trong kho cua nhan vien. == trung thong bao vơi APP_OVER_ORDER_GOOD
                            notReveiveError += salesOrderDetails.getGoodss().getName() + ", ";
                            flagNotReceive = true;
                        }
                    }

                    if (flagNotReceive || flagOver) {
                        //tra ve loi
                        LinkedHashMap error = new LinkedHashMap();
                        if (flagNotReceive) {
                            notReveiveError = notReveiveError.substring(0, notReveiveError.length() - 2);
                            error.put(MsalesValidator.APP_SALESMAN_NOT_RECEIVE_GOODS, notReveiveError);
                        }
                        if (flagOver) {
                            overError = overError.substring(0, overError.length() - 2);
                            error.put(MsalesValidator.APP_OVER_ORDER_GOODS, overError);
                        }

                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.APP_ORDER_CHECKED_FAILED, error));
                    }
                    //check thanh cong
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                } // Order null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, MsalesValidator.APP_ORDER_ID_NOT_EXIST + searchObject.getOrderId()));
                }
            } // Order from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } // jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Giao hàng cho điểm bán hàng
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_DELIVERY_GOODS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String deliveryGoodsApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int userLoginId = session.getUserId();

        //lay role cua UserLogin
        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int role = userRole.getId();

        String jsonString = request.getAttribute("contents").toString();
        if (jsonString != null) {
            OrderList receiveGoods;

            try {
                receiveGoods = MsalesJsonUtils.getObjectFromJSON(jsonString, OrderList.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (receiveGoods != null) {
                LinkedHashMap<String, Object> hashErrors = new LinkedHashMap<>();
                if (receiveGoods.getOrderId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_ORDER_ID_NULL));
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //Lấy SalesOrder đơn hàng dang cho
                SalesOrder salesOrder = appService.getSalesOrderWaiting(receiveGoods.getOrderId(), dataService);
                if (salesOrder == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_ORDER_INCORRECT));
                }

                //lấy id kho hàng của salesman
                SalesStock salesStockUser = appService.getSalesStockUser(userLoginId, dataService);
                if (salesStockUser == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALESMAN_NO_HAVE_STOCK, MsalesValidator.APP_SALESMAN_NO_HAVE_STOCK));
                }
                int salesStockId = salesStockUser.getId();

                //Lấy Id kho hàng của điểm bán hàng
                SalesStock salesStockPOS = appService.getSalesStockPOS(salesOrder.getPos().getId(), dataService);

                if (salesStockPOS == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_NO_HAVE_STOCK, MsalesValidator.APP_POS_NO_HAVE_STOCK));
                }
                int posStockId = salesStockPOS.getId();

                //Lấy danh sách Sales_Order_details
                List<SalesOrderDetails> salesOrderDetailsList = appService.getListSalesOrderDetails(salesOrder.getId(), dataService);
                if (salesOrderDetailsList.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALES_ORDER_DETAILS_NULL, MsalesValidator.APP_SALES_ORDER_DETAILS_NULL));
                }
                //update salesStockGoods cua POS va SalesStock cua Nhan vien
                ////get danh sach SaleStockGoods cua nhan vien ung voi tung san pham trong don hang//   
                List<Integer> goodsIds = new ArrayList<>();
                List<Goods> goodsList = new ArrayList<>();//de kiem tra KM
                for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                    goodsIds.add(salesOrderDetails.getGoodss().getId());
                    salesOrderDetails.getGoodss().setQuantity(salesOrderDetails.getQuantity());
                    goodsList.add(salesOrderDetails.getGoodss());
                }
                List<SalesStockGoods> salesStockGoodsListUser = appService.getListSalesStockGoodsByListGoodsId(salesStockId, goodsIds, dataService);

//                if (salesOrderDetailsList.size() != salesStockGoodsListUser.size()) {
//                    //return loi trongkho hang
//                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.ERROR_SALES_STOCK));
//                }
                //danh sach SalesStockGoods cua POS
                List<SalesStockGoods> salesStockGoodsListPOS = new ArrayList<>();

                boolean flagNotReceive = false;
                boolean flagOver = false;
                String notReveiveError = "";
                String overError = "";

                for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                    //kiem tra salesStock cua POS da co san pham trong order chua. neu chua co thi new

                    SalesStockGoods salesStockGoodsPOS = appService.getSalesStockGoodsByGoodsId(posStockId, salesOrderDetails.getGoodss().getId(), dataService);
                    if (salesStockGoodsPOS != null) {
                        salesStockGoodsPOS.setUpdatedAt(new Date());
                        salesStockGoodsPOS.setUpdatedUser(userLoginId);
                    } else {
                        salesStockGoodsPOS = new SalesStockGoods();
                        salesStockGoodsPOS.setDeletedUser(0);
                        salesStockGoodsPOS.setUpdatedUser(0);
                        salesStockGoodsPOS.setCreatedUser(userLoginId);
                        salesStockGoodsPOS.setGoodss(salesOrderDetails.getGoodss());
                        salesStockGoodsPOS.setGoodsUnits(salesOrderDetails.getGoodsUnits());
                        salesStockGoodsPOS.setIsActive(1);
                        salesStockGoodsPOS.setQuantity(0);
                        salesStockGoodsPOS.setStockId(posStockId);
                        salesStockGoodsPOS.setGoodsStatusId(15);
                        salesStockGoodsPOS.setActiveDate(new Date());
                    }

                    salesStockGoodsListPOS.add(salesStockGoodsPOS);
                    //chinh so luong
                    SalesStockGoods salesStockGoodsUser = getSalesStockGoodsByGoodsId(salesOrderDetails.getGoodss().getId(), salesStockGoodsListUser);
                    if (salesStockGoodsUser != null) {
                        if (salesOrderDetails.getQuantity() > salesStockGoodsUser.getQuantity()) {
                            flagOver = true;
                            overError += salesOrderDetails.getGoodss().getName() + ", ";
                        } else {
                            salesStockGoodsUser.setQuantity(salesStockGoodsUser.getQuantity() - salesOrderDetails.getQuantity());
                            salesStockGoodsPOS.setQuantity(salesStockGoodsPOS.getQuantity() + salesOrderDetails.getQuantity());
                        }
                    } else {
                        notReveiveError += salesOrderDetails.getGoodss().getName() + ", ";
                        flagNotReceive = true;
                    }
                }

                if (flagNotReceive || flagOver) {
                    LinkedHashMap error = new LinkedHashMap();
                    if (flagNotReceive) {
                        notReveiveError = notReveiveError.substring(0, notReveiveError.length() - 2);
                        error.put(MsalesValidator.APP_SALESMAN_NOT_RECEIVE_GOODS, notReveiveError);
                    }
                    if (flagOver) {
                        overError = overError.substring(0, overError.length() - 2);
                        error.put(MsalesValidator.APP_OVER_ORDER_GOODS, overError);
                    }

                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.ERROR_SALES_STOCK, error));
                }

                //chinh lai status cho Salesorder
                salesOrder.setStatusId(18);//giao boi salesman
                if (role == 4) {//sales sup
                    salesOrder.setStatusId(20);//giao boi giam sat
                }
                salesOrder.setUpdatedAt(new Date());
                salesOrder.setUpdatedUser(userLoginId);

                ArrayList insertArray = new ArrayList();
                insertArray.addAll(salesStockGoodsListUser);
                insertArray.addAll(salesStockGoodsListPOS);
                insertArray.add(salesOrder);
                try {
                    dataService.insertOrUpdateArray(insertArray);

                    try {
                        ReturnPromotionList returnPromotionList = new ReturnPromotionList();
                        //giao hang thanh cong => kiem tra km
                        List<Promotion> list = appService.getListPromotionCanByPOS(salesOrder.getPos().getId(), goodsList, userLoginId, dataService);
                        if (list.isEmpty()) {
                            //ko co KM thoa
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                        }
                        returnPromotionList.setSalesOrderId(salesOrder.getId());
                        returnPromotionList.setSalesTransId(0);//set null to 0
                        returnPromotionList.setPromotionList(list);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, returnPromotionList));
                    } catch (Exception ex) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                    }

                } catch (Exception ex) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.APP_TRANSACTIONS_FAIL));
                }

            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    //get salesStoc by goods
    private SalesStockGoods getSalesStockGoodsByGoodsId(int goodsId, List<SalesStockGoods> list) {
        for (SalesStockGoods salesStockGoods : list) {
            if (goodsId == salesStockGoods.getGoodss().getId()) {
                return salesStockGoods;
            }
        }
        return null;
    }

    /**
     * Đặt hàng qua điện thoại
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SET_GOODS_ORDER_MOBILE_APP, method = RequestMethod.POST)
    public @ResponseBody
    String setGoodsOrderMobileApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int userLoginId = session.getUserId();
        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        String jsonString = request.getAttribute("contents").toString();
        if (jsonString != null) {
            ReceiveGoods receiveGoods;

            try {
                receiveGoods = MsalesJsonUtils.getObjectFromJSON(jsonString, ReceiveGoods.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (receiveGoods != null) {
                LinkedHashMap<String, Object> hashErrors = new LinkedHashMap<String, Object>();
                if (receiveGoods.getTransCode() == null || receiveGoods.getTransCode().trim().isEmpty()) {
                    hashErrors.put("transCode", MsalesValidator.NOT_NULL_AND_EMPTY);
                }
                if (receiveGoods.getSalesTransDate() == null) {
                    hashErrors.put("salesTransDate", MsalesValidator.NOT_NULL);
                }
                if (receiveGoods.getPosId() == null) {
                    hashErrors.put("posId", MsalesValidator.NOT_NULL);
                }
                if (receiveGoods.getStockGoods() == null || receiveGoods.getStockGoods().isEmpty()) {
                    hashErrors.put("stockGoods", MsalesValidator.NOT_NULL_AND_EMPTY);
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //Kiểm tra xem transCode có đúng không
                String transCode = receiveGoods.getTransCode();

                if (appService.checkOrderCode(transCode, dataService)) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_TRANS_CODE_IS_EXIST));
                }

                //Lấy danh sách StockGoods
                List<Goods> stockGoods = receiveGoods.getStockGoods();

                if (stockGoods.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.APP_GOODS_NULL, MsalesValidator.APP_GOODS_NULL));
                }
                //Kiểm tra xem có Goods nào rỗng không
                for (Goods goods : stockGoods) {
                    if (goods == null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_GOODS_NULL, MsalesValidator.APP_GOODS_NULL));
                    }
                    if (goods.getUnit() == null) {
                        hashErrors.put("unit", MsalesValidator.NOT_NULL);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                    }
                }

//                Transaction transaction = null;
//                org.hibernate.Session datasSession = null;
//                try {
//                    datasSession = dataService.openSession();
//                    transaction = datasSession.beginTransaction();
//                    
//                    
//                    transaction.commit();
//                    return "";
//
//                } catch (Exception e) {
//                    transaction.rollback();
//                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
//                            .create(MsalesStatus.SQL_INSERT_FAIL));
//                } finally {
//                    datasSession.close();
//                }
                Transaction transaction = null;
                org.hibernate.Session datasSession = null;
                try {
                    datasSession = dataService.openSession();
                    transaction = datasSession.beginTransaction();

                    //set data for Sales_Order
                    SalesOrder salesOrder = new SalesOrder();
                    salesOrder.setCompanyId(userLogin.getCompanys().getId());
                    salesOrder.setPosId(receiveGoods.getPosId());
                    //kho hang cua DBH
                    SalesStock salesStocksPOS = appService.getSalesStockPOS(receiveGoods.getPosId(), dataService);

                    if (salesStocksPOS == null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_NO_HAVE_STOCK, MsalesValidator.APP_POS_NO_HAVE_STOCK));
                    }

                    //set id kho hàng
                    salesOrder.setStockId(salesStocksPOS.getId());
                    //set ngày giao dịch
                    salesOrder.setSalesTransDate(receiveGoods.getSalesTransDate());
                    //sét trạng thái giao dịch
                    salesOrder.setStatusId(13);//13 dang cho
                    //Xét Note với đặt hàng qua điện thoại
                    salesOrder.setNote("MOBILE");
                    //xet TransCode
                    salesOrder.setTransCode(transCode);
                    //người tạo
                    salesOrder.setCreatedUser(userLoginId);
                    salesOrder.setCreatedAt(new Date());
                    salesOrder.setUpdatedUser(0);
                    salesOrder.setDeletedUser(0);//xem nhu bi xoa

                    //save SalesOrder 
                    datasSession.save(salesOrder);

                    //List<SalesOrderDetails> salesOrderDetailsList = new ArrayList<>();
                    for (Goods goods : stockGoods) {
                        SalesOrderDetails salesOrderDetails = new SalesOrderDetails();
                        //set property for salesOrderDetails
                        salesOrderDetails.setGoodsId(goods.getId());

                        //id cua unit luc dua len la GoodsUnit
                        GoodsUnit goodsUnit = dataService.getRowById(goods.getUnit().getId(), GoodsUnit.class);
                        if (goodsUnit == null) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.APP_GOODS_UNIT_NULL));
                        }

                        salesOrderDetails.setGoodsUnits(goodsUnit);
                        salesOrderDetails.setQuantity(goods.getQuantity());

                        if (!goodsUnit.getGoodss().getIsFocus()) {
                            //check GoodsFocus
                            salesOrderDetails.setIsFocus(appService.checkGoodsFocusByPOSId(goodsUnit.getGoodss().getId(), receiveGoods.getPosId(), dataService));
                        } else {
                            salesOrderDetails.setIsFocus(true);
                        }

                        salesOrderDetails.setPrice(goodsUnit.getPrice());

                        salesOrderDetails.setSalesTransDate(receiveGoods.getSalesTransDate());
                        salesOrderDetails.setCreatedUser(userLoginId);
                        salesOrderDetails.setCreatedAt(new Date());
                        salesOrderDetails.setUpdatedUser(0);
                        salesOrderDetails.setDeletedUser(0);
                        salesOrderDetails.setOrderId(salesOrder.getId());

                        //salesOrderDetailsList.add(salesOrderDetails);
                        datasSession.save(salesOrderDetails);
                    }

                    transaction.commit();
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK));

                } catch (Exception e) {
                    transaction.rollback();
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                } finally {
                    datasSession.close();
                }

            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * ------------------------------------------------------------------------------------------------------------------------------------------- SALES SUP -------------------------------------------------------------------------------------------------------------------------------------------
     */
    /**
     * Giam sat ban hang
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_SUP_SELL_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getSupSellApp(HttpServletRequest request) {

        // get Lists Location from DB
        //Lấy Id của User giám sát
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            // not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();
        User userSup = dataService.getRowById(userId, User.class
        );
        String jsonString = request.getAttribute("contents").toString();
        SearchObject searchObject;
        if (jsonString
                != null) {
            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
        if (searchObject == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }
        String searchText = searchObject.getSearchText();
        //Lấy danh sách users thuộc User giám sát.
        ParameterList parameterList9 = new ParameterList(0, 0);

        parameterList9.add(
                "users.id", userId);
        List<UserRoleChannel> userRoleChannelList = dataService.getListOption(UserRoleChannel.class, parameterList9);

        if (userRoleChannelList.isEmpty()) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        //User userLogin = userRoleChannelList.get(0).getUsers();
        int role = userRoleChannelList.get(0).getUserRoles().getId();
        //check salesSup
        if (role != 4) {
            //khong phai salesSup
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        String hqlChannel = "";
        for (UserRoleChannel userRoleChannel : userRoleChannelList) {
            hqlChannel += userRoleChannel.getChannels().getId() + ",";
        }
        hqlChannel += "''";

        List<HashMap> listUsers = salesSupService.getListUserId(hqlChannel, searchText, userSup.getCompanys().getId());

        //Xử lí thời điểm hiện tại
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        //Lưu thời điểm hiện tại.
        Date dateCurrent = cal.getTime();

        //Lưu thời điểm ngày đầu tiên của tháng
        cal.set(Calendar.DAY_OF_MONTH,
                1);
        cal.set(Calendar.HOUR,
                0);
        cal.set(Calendar.MINUTE,
                0);
        cal.set(Calendar.SECOND,
                0);
        Date date2 = cal.getTime();

        //Lưu thời điểm cuối tháng
        cal.add(Calendar.MONTH,
                1);
        cal.set(Calendar.DAY_OF_MONTH,
                1);
        cal.add(Calendar.DAY_OF_MONTH,
                -1);

        Date date3 = cal.getTime();
        //Tính số ngày còn lại trong tháng:
        long ngayConLai = date3.getDate() - dateCurrent.getDate() + 1;
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat2.format(date2);
        Date date = date2;

        try {
            date = dateFormat2.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Tạo biến lưu thông tin
        int posNew = 0;
        int targetPOS = 0;
        long totalPrice = 0;
        long totalPriceFocus = 0;
        //Tổng doanh số chỉ tiêu còn lại trong tháng
        long totalPriceDate = 0;
        long totalPriceDateFocus = 0;
        //Tổng doanh số bán được trong ngày
        long totalSoldDate = 0;
        long totalSoldDateFocus = 0;
        long totalSoldFocus = 0;
        //Tổng doanh số đã bán trong tháng
        long totalSold = 0;
        SupSellApp supSellApp = new SupSellApp();
        //Lấy thông tin bán hàng cũng như tạo điểm mới của tất cả nhân viên.
        List<User> users = new ArrayList<User>();
        for (HashMap user : listUsers) {
            User userSales = new User();
            int idUser = Integer.parseInt(user.get("id").toString());
            userSales.setId(idUser);
            String name = user.get("name").toString();
            userSales.setName(name);
            //Xét ratio cho mỗi user;
            long totalChiTieu = 0;
            long totalBanDuoc = 0;
            long totalChiTieuFocus = 0;
            long totalBanDuocFocus = 0;
            //Lấy tổng số điểm bán hàng mới mà nhân viên đã tạo mới từ đầu tháng tới hiện tại
            // String hql2 = "Select U.id from POS as U where createdAt >= '" + dateFormat2.format(date2) + "' and createdUser = " + idUser;
            String hql2 = salesSupService.getHqlString(dateFormat2.format(date2), idUser);
            List<POS> pos = dataService.executeSelectHQL(POS.class, hql2, true, 0, 0);
            posNew += pos.size();

            //Tính tổng doanh thu trọng tâm và tổng doanh thu tháng.
            //Lấy kế hoạch bán hàng của nhân viên
            ParameterList parameterList = new ParameterList();
            parameterList.add("implementEmployees.id", idUser);
            parameterList.add("beginDate", date, ">=");
            parameterList.add("beginDate", dateCurrent, "<=");
            parameterList.add("type", 2);
            List<MCP> mcpList = dataService.getListOption(MCP.class, parameterList);

            if (!mcpList.isEmpty()) {
                for (MCP mcp : mcpList) {
                    targetPOS += mcp.getNewPOS();
                    //lấy kế hoạch bán hàng chi tiết của từng nhân viên
                    //Lấy tổng số giao chỉ tiêu trên tháng
                    if (mcp.getSalesPerMonth() != null) {
                        totalPrice += mcp.getSalesPerMonth();
                        totalChiTieu += mcp.getSalesPerMonth();
                    }
                    if (mcp.getSalesFocusPerMonth() != null) {
                        totalPriceFocus += mcp.getSalesFocusPerMonth();
                        totalChiTieuFocus += mcp.getSalesFocusPerMonth();
                    }
                    //Check số tiền bằng sql
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String hqlS = salesSupService.getStringHqlOfSalesTransDetails(idUser, simpleDateFormat.format(date), simpleDateFormat.format(dateCurrent));
                    List<HashMap> salesTransDetails = new ArrayList<HashMap>();
                    try {
                        salesTransDetails = dataService.executeSelectHQL(HashMap.class, hqlS, true, 0, 0);
                    } catch (Exception ex) {

                    }
                    String hqlS2 = salesSupService.getStringHqlOfSalesTransDetailsForSalesSup(idUser, simpleDateFormat.format(date), simpleDateFormat.format(dateCurrent));
                    List<HashMap> salesTransDetails2 = new ArrayList<HashMap>();
                    try {
                        salesTransDetails2 = dataService.executeSelectHQL(HashMap.class, hqlS2, true, 0, 0);
                    } catch (Exception ex) {

                    }
                    if (!salesTransDetails2.isEmpty()) {
                        salesTransDetails.addAll(salesTransDetails2);
                    }
                    String hqlSalesOrder = salesSupService.getStringHqlForSalesOrder(mcp.getImplementEmployees().getId(), simpleDateFormat.format(date), simpleDateFormat.format(dateCurrent));
                    List<HashMap> salesOrderList = dataService.executeSelectHQL(HashMap.class, hqlSalesOrder, true, 0, 0);
                    if (!salesOrderList.isEmpty()) {
                        salesTransDetails.addAll(salesOrderList);
                    }

                    if (!salesTransDetails.isEmpty()) {

                        for (HashMap sTransDetails : salesTransDetails) {
                            //Xử lí số tiền bán được trong ngày.
                            if (dateCurrent.getDate() == ((Timestamp) sTransDetails.get("date")).getDate()) {
                                totalSoldDate += (Integer) sTransDetails.get("quantity") * (Integer) sTransDetails.get("price");

                                if ((Boolean) sTransDetails.get("isFocus")) {
                                    totalSoldDateFocus += (Integer) sTransDetails.get("quantity") * (Integer) sTransDetails.get("price");
                                }
                            }

                            //Xử lí tổng số tiền đã bán được trong tháng
                            totalSold += (Integer) sTransDetails.get("quantity") * (Integer) sTransDetails.get("price");
                            totalBanDuoc += (Integer) sTransDetails.get("quantity") * (Integer) sTransDetails.get("price");
                            //xử lí tổng số tiền trọng tâm đã bán được trong tháng
                            if ((Boolean) sTransDetails.get("isFocus")) {
                                totalSoldFocus += (Integer) sTransDetails.get("quantity") * (Integer) sTransDetails.get("price");
                            }
                        }

                    }

                }
            }

            //Xét name và ratio cho user
            if (totalChiTieu != 0 && totalChiTieuFocus != 0) {
                Double ratio = (double) (((double) totalBanDuoc / totalChiTieu + (double) totalBanDuocFocus / totalChiTieuFocus) * 100);
                // NumberFormat formatter = new DecimalFormat("#0.00");
                userSales.setRatio(ratio);
            } else {
                userSales.setRatio(0.00);
            }
            users.add(userSales);

        }
        //Xét tổng doanh số chỉ tiêu còn lại trong tháng.
        if (ngayConLai
                > 0) {
            totalPriceDate = (totalPrice - totalSold) / ngayConLai;
            totalPriceDateFocus = (totalPriceFocus - totalSoldFocus) / ngayConLai;
        } else {
            totalPriceDate = totalPrice - totalSold;
            totalPriceDateFocus = totalPriceFocus - totalSoldFocus;
        }

        //Cập nhật dữ liệu cho supSellApp
        supSellApp.setPosNew(posNew);

        supSellApp.setTotalSoldFocus(totalSoldFocus);

        supSellApp.setTargetPOSNew(targetPOS);

        supSellApp.setTotalPrice(totalPrice);

        supSellApp.setTotalPriceFocus(totalPriceFocus);

        supSellApp.setTotalSoldDate(totalSoldDate);

        supSellApp.setTotalSoldDateFocus(totalSoldDateFocus);

        supSellApp.setTotalSold(totalSold);

        supSellApp.setTotalPriceDate(totalPriceDate);

        supSellApp.setTotalPriceDateFocus(totalPriceDateFocus);

        supSellApp.setUsers(users);

        //    MsalesResults<Location> lists = dataService.getListOption(Location.class, parameterList, true);
        // lists not null display list
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                HttpStatus.OK, supSellApp));
    }

    /**
     * get CbList SupChannel App
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_CB_SUP_CHANNEL_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListSupChannelApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class
        );
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole
                == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int role = userRole.getId();
        if (role != 4) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        List<LinkedHashMap> contents = new ArrayList<>();

        //lay channel ma user dang lam giam sat.
        List<Integer> ids = appService.getListChannelId(userLoginId, dataService);
        List<Channel> channelList = appService.getListChannelByParent(ids, dataService);
        for (Channel channel : channelList) {
            LinkedHashMap channelMap = new LinkedHashMap();
            channelMap.put("id", channel.getId());
            channelMap.put("name", channel.getName());

            List<HashMap> userList = appService.getCbAllListUserByChannel(channel.getId(), userLogin.getCompanys().getId(), dataService);

            channelMap.put("users", userList);
            contents.add(channelMap);
        }

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, contents));
    }

    /**
     * search SupRoute App
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SEARCH_SUP_ROUTE_APP, method = RequestMethod.POST)
    public @ResponseBody
    String searchSupRouteApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();
        User userLogin = dataService.getRowById(userLoginId, User.class
        );
        if (userLogin == null) {
            //khong phai salesSup
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole == null || userRole.getId() > 4) {
            //khong phai salesSup
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchObject.getChannelId() == null) {
                hashErrors.put("channelId", MsalesValidator.NOT_NULL);
            }
            if (searchObject.getUserId() == null) {
                hashErrors.put("userId", MsalesValidator.NOT_NULL);
            }
            if (searchObject.getDate() == null) {
                hashErrors.put("date", MsalesValidator.NOT_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            ArrayList<User> userList = new ArrayList<>();
            //tim user
            if (searchObject.getUserId() == -1 || searchObject.getUserId() == 0) {
                if (searchObject.getChannelId() == -1 || searchObject.getChannelId() == 0) {
                    //lay tat ca nhan vien thuoc giam sat
                    List<User> list = appService.getListUserBySup(userLoginId, userLogin.getCompanys().getId(), dataService);
                    userList.addAll(list);
                } else {
                    //lay tat ca nhan vien thuoc channel
                    List<User> list = appService.getListUserByChannel(searchObject.getChannelId(), dataService);
                    userList.addAll(list);
                }
            } else {
                //lay theo nhan vien
                User user = dataService.getRowById(searchObject.getUserId(), User.class);
                if (user == null) {
                    hashErrors.put("user", "ID=" + searchObject.getUserId() + " " + MsalesValidator.NOT_EXIST);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                userList.add(user);
            }

            List<LinkedHashMap> items = new ArrayList<>();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_LONG);
            for (User user : userList) {
                LinkedHashMap item = new LinkedHashMap();
                item.put("userId", user.getId());
                item.put("username", user.getLastName() + " " + user.getFirstName());

                List posLists = new ArrayList();
                List<HashMap> hashMapList = appService.searchSupRouteApp(user.getId(), searchObject.getDate(), dataService);
                for (HashMap hashMap : hashMapList) {
                    LinkedHashMap posMap = new LinkedHashMap();
                    //POS APP
                    posMap.put("id", ((POS) hashMap.get("pos")).getId());
                    posMap.put("name", ((POS) hashMap.get("pos")).getName());
                    posMap.put("address", ((POS) hashMap.get("pos")).getAddress());
                    posMap.put("lat", ((POS) hashMap.get("pos")).getLat());
                    posMap.put("lng", ((POS) hashMap.get("pos")).getLng());
                    posMap.put("isVisited", hashMap.get("isVisited"));
                    posMap.put("timeCared", hashMap.get("timeCared"));

                    Date timeOrder = appService.checkLastOrder(((POS) hashMap.get("pos")).getId(), searchObject.getDate(), dataService);
                    Date timeSale = appService.checkLastSale(((POS) hashMap.get("pos")).getId(), searchObject.getDate(), dataService);
                    posMap.put("timeDelivery", timeOrder == null ? null : simpleDateFormat.format(timeOrder));
                    posMap.put("timeSale", timeSale == null ? null : simpleDateFormat.format(timeSale));
                    posLists.add(posMap);
                }

                List<HashMap> routeList = appService.getListUserRoute(user.getId(), searchObject.getDate(), dataService);
                item.put("posList", posLists);
                item.put("routes", routeList);
                items.add(item);
            }
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(HttpStatus.OK, items));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * search SupMCP App
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SEARCH_SUP_MCP_APP, method = RequestMethod.POST)
    public @ResponseBody
    String searchSupMCPApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();
        User userSup = dataService.getRowById(userId, User.class);
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchSupMCP;
            try {
                searchSupMCP = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchSupMCP.getMcpDay() == null) {
                hashErrors.put("day", MsalesValidator.NOT_NULL);
            }
            if (searchSupMCP.getUserId() == null) {
                hashErrors.put("userId", MsalesValidator.NOT_NULL);
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            Calendar now = Calendar.getInstance();
            now.set(Calendar.HOUR_OF_DAY, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            int thu = now.get(Calendar.DAY_OF_WEEK);

            if (thu == 1)//chu nhat
            {
                thu = 8;
            }
            thu = thu - 2;
            now.add(Calendar.DAY_OF_MONTH, 0 - thu);
            Date date1 = now.getTime();
            now.add(Calendar.DAY_OF_MONTH, 6);
            Date date2 = now.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            List<MCP> mcpList = salesSupService.getListMCP(sdf.format(date1), sdf.format(date2), searchSupMCP.getMcpDay(), searchSupMCP.getUserId(), userSup.getCompanys().getId(), userSup.getId());
            //List ArrayUserId
            List<User> users = new ArrayList<User>();
            ArrayList<Integer> arrayEmployeeId = new ArrayList<Integer>();
            for (int i = 0; i < mcpList.size(); i++) {
                boolean bool = true;
                for (int j = 0; j < i; j++) {
                    if (mcpList.get(i).getImplementEmployees().getId() == mcpList.get(j).getImplementEmployees().getId()) {
                        bool = false;
                        break;
                    }
                }
                if (bool) {
                    arrayEmployeeId.add(mcpList.get(i).getImplementEmployees().getId());
                }
            }
            for (Integer id : arrayEmployeeId) {
                User user = new User();
                List<MCP> mcps = new ArrayList<MCP>();
                for (MCP mcp : mcpList) {
                    if (mcp.getImplementEmployees().getId() == id) {
                        int idMCP = 0;
                        idMCP = mcp.getBeginDate().getDay();
                        mcp.setMcpId(idMCP + 1);
                        mcps.add(mcp);
                        user = mcp.getImplementEmployees();
                    }
                }
                if (user == null) {
                    user = dataService.getRowById(id, User.class);

                }
                if (!mcps.isEmpty()) {
                    user.setMcpList(mcps);
                }
                user.setName(user.getLastName() + " " + user.getFirstName());
                users.add(user);
            }

            String[] filters = {"username", "userCode", "password",
                "lastName", "firstName", "birthday", "sex", "email",
                "yahooId", "skypeId", "isdn", "tel", "address", "note",
                "activeCode", "employerType", "useEvoucher", "ipLastLogin",
                "updatedUser", "locations", "monitoringUsers", "companys",
                "createdAt", "salesFocusPerMonth", "newPOS",
                "salesPerMonth", "createdUser", "type", "beginDate",
                "finishTime", "isActive", "implementEmployees", "statuss"};

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, users), filters);
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

    }

    /**
     * get SupMCP App
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_SUP_MCP_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getSupMCPApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchObject.getId() == null) {
                hashErrors.put("id", MsalesValidator.NOT_NULL);
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            String hql = salesSupService.getStringHQLSupMCPApp(searchObject.getId());

            List<HashMap> list = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            List<LinkedHashMap> poss = new ArrayList<LinkedHashMap>();

            if (!list.isEmpty()) {
                for (HashMap mcpDetails : list) {
                    LinkedHashMap pos = new LinkedHashMap();
                    pos = new LinkedHashMap();
                    pos.put("id", ((POS) mcpDetails.get("pos")).getId());
                    pos.put("mcpDetailsId", mcpDetails.get("mcpDetailsId"));
                    pos.put("posCode", ((POS) mcpDetails.get("pos")).getPosCode());
                    pos.put("name", ((POS) mcpDetails.get("pos")).getName());
                    pos.put("address", ((POS) mcpDetails.get("pos")).getAddress());
                    pos.put("lat", ((POS) mcpDetails.get("pos")).getLat());
                    pos.put("lng", ((POS) mcpDetails.get("pos")).getLng());
                    pos.put("isVisited", mcpDetails.get("isVisited"));
                    poss.add(pos);
                }

            }

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, poss));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

    }

    /**
     * get ListUserSup App
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_LIST_USER_SUP_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getListUserSupApp(HttpServletRequest request
    ) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        //lay role cua UserLogin
        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int role = userRole.getId();
        //check salesSup
        if (role != 4) {
            //khong phai salesSup
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        List<HashMap> list = appService.getCbListUserBySup(userLoginId, userLogin.getCompanys().getId(), dataService);

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
    }

    /**
     * search SupPOS App
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SEARCH_SUP_POS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String SearchSupPOSApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class
        );
        if (userLogin == null) {
            //khong phai salesSup
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        //lay role cua UserLogin
        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int role = userRole.getId();

        if (role != 4) {
            //khong phai salesSup
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchObject.getUserId() == null) {
                hashErrors.put("userId", MsalesValidator.NOT_NULL);
            }
            if (searchObject.getPosCode() == null) {
                hashErrors.put("posCode", MsalesValidator.NOT_NULL);
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            ArrayList<LinkedHashMap> contents = new ArrayList<>();
            ArrayList<User> userList = new ArrayList<>();
            if (searchObject.getUserId() != 0 && searchObject.getUserId() != -1) {
                //lay thong tin cua user
                User user = dataService.getRowById(searchObject.getUserId(), User.class);
                if (user != null) {
                    userList.add(user);
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.USER_NOT_EXIST));
                }
            } else {
                //lay tat ca nhan vien thuoc giam sat
                List<User> list = appService.getListUserBySup(userLoginId, userLogin.getCompanys().getId(), dataService);
                userList.addAll(list);
            }

            for (User user : userList) {
                LinkedHashMap item = new LinkedHashMap();
                item.put("id", user.getId());
                item.put("name", user.getLastName() + " " + user.getFirstName());

                //tim MCP
                //List<MCP> list = appService.getMCP(user.getId(), fromDate, toDate, dataService);
                MCP mcp = appService.getMCPNow(user.getId(), dataService);
                List<LinkedHashMap> mcpList = new ArrayList<>();
                if (mcp != null) {
                    LinkedHashMap mcpMap = new LinkedHashMap();
                    mcpMap.put("id", mcp.getId());
                    mcpMap.put("name", mcp.getName());

                    //lay thong tin POS               
                    List<HashMap> posMapList = appService.searchSupPOS(mcp.getId(), searchObject.getPosCode(), dataService);

                    List<LinkedHashMap> posList = new ArrayList<>();
                    for (HashMap posInfo : posMapList) {
                        //POS APP
                        LinkedHashMap pos = new LinkedHashMap();
                        pos.put("id", ((POS) posInfo.get("pos")).getId());
                        pos.put("mcpDetailsId", posInfo.get("mcpDetailsId"));
                        pos.put("posCode", ((POS) posInfo.get("pos")).getPosCode());
                        pos.put("name", ((POS) posInfo.get("pos")).getName());
                        pos.put("address", ((POS) posInfo.get("pos")).getAddress());
                        pos.put("lat", ((POS) posInfo.get("pos")).getLat());
                        pos.put("lng", ((POS) posInfo.get("pos")).getLng());
                        pos.put("isVisited", posInfo.get("isVisited"));

                        posList.add(pos);
                    }
                    mcpMap.put("posList", posList);
                    mcpList.add(mcpMap);
                }
                item.put("mcpList", mcpList);
                contents.add(item);
            }

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(HttpStatus.OK, contents));

        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Lay danh sach tat ca san pham salesup duoc quyen ban (san pham cua tat ca nhan vien)
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_GOODS_SUP_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getGoodsSupApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        int userLoginId = session.getUserId();

        //lay role cua UserLogin
        List<UserRoleChannel> userRoleChannelList = loginService.getListUserRoleChannel(userLoginId, dataService);

        if (userRoleChannelList.isEmpty()) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        //User userLogin = userRoleChannelList.get(0).getUsers();
        int role = userRoleChannelList.get(0).getUserRoles().getId();
        //check salesSup
        if (role != 4) {
            //khong phai salesSup
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        List<Integer> userIdList = appService.getListUserIdBySup(userLoginId, userRoleChannelList.get(0).getUsers().getCompanys().getId(), dataService);
        boolean flag = false;
        List<Integer> goodsCategoryIds = new ArrayList<>();
        //kiem tra co nhan vien nao duoc ban het san pham
        for (Integer id : userIdList) {
            List<GoodsCategory> categoryList = appService.getListGoodsCategory(id, userRoleChannelList.get(0).getUsers().getCompanys().getId(), dataService);
            if (categoryList.isEmpty()) {
                flag = true;
                break;
            } else {
                for (GoodsCategory goodsCategory : categoryList) {
                    goodsCategoryIds.add(goodsCategory.getId());
                }
            }
        }

        List<Goods> goodsList;

        if (flag) {
            //lay tat ca san pham           
            goodsList = appService.getListGoods(userRoleChannelList.get(0).getUsers().getCompanys().getId(), dataService);
        } else {
            goodsList = appService.getListGoodsByListGoodsCategoryId(goodsCategoryIds, dataService);
        }

        List<StockGoods> list = new ArrayList<>();
        int goodsCategoryId = -1;
        StockGoods stockGoods = new StockGoods();
        for (Goods goods : goodsList) {
            if (goods.getGoodsCategorys().getId() != goodsCategoryId) {
                if (stockGoods.count() > 0) {
                    stockGoods.setGoodsCategory(stockGoods.getGoods().get(0).getGoodsCategorys());
                    list.add(stockGoods);
                }
                stockGoods = new StockGoods();
                goodsCategoryId = goods.getGoodsCategorys().getId();
            }
            //get danh Unit sach cua Goods
            List<GoodsUnit> goodsUnitList = appService.getListGoodsUnitByGoodsId(goods.getId(), dataService);
            for (GoodsUnit goodsUnit : goodsUnitList) {
                goodsUnit.setName(goodsUnit.getUnits().getName());
            }
            goods.setUnits(goodsUnitList);
            stockGoods.add(goods);
        }

        if (stockGoods.count() > 0) {
            stockGoods.setGoodsCategory(stockGoods.getGoods().get(0).getGoodsCategorys());
            list.add(stockGoods);
        }

        String[] filter = {"parents", "statuss", "goodsCategorys", "factor", "goodsCode", "order", "isRecover", "childUnitIds"};

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), filter);

    }

    /**
     * Giam sat danh gia
     *
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_SUP_KPI_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getSupKPIApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        } else if (userRole.getId() != 4) {
            //role failed
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        SearchObject searchObject;

        if (jsonString != null) {
            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }

        LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();
        if (searchObject.getMonth() == null) {
            hashErrors.put("month", MsalesValidator.NOT_NULL);
        }
        if (searchObject.getYear() == null) {
            hashErrors.put("year", MsalesValidator.NOT_NULL);
        }
        if (!hashErrors.isEmpty()) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
        }

        List<User> userList = new ArrayList<>();
        if (searchObject.getUserId() == null || searchObject.getUserId() == 0 || searchObject.getUserId() == -1) {
            //lay tat ca nv thuoc giam sat
            List<User> temp = appService.getListUserBySup(userLoginId, userLogin.getCompanys().getId(), dataService);
            userList.addAll(temp);
        } else {
            User user = dataService.getRowById(searchObject.getUserId(), User.class);
            if (user != null) {
                userList.add(user);
            } else {
                hashErrors.put("User:", "ID=" + searchObject.getUserId() + " " + MsalesValidator.NOT_EXIST);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
            }
        }

        CompanyConstant companyConstant = appService.getCompanyConstant(userLogin.getCompanys().getId(), dataService);
        CompanyConfigKpi companyConfigKpi = appService.getCompanyConfigKPI(userLogin.getCompanys().getId(), dataService);
        if (companyConfigKpi == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.APP_COMPANY_CONFIG_KPI_NULL));
        }

        if (companyConstant == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.APP_COMPANY_CONSTRANT_NULL));
        }

        LinkedHashMap contents = appService.getSupKPIApp(userList, userLogin.getCompanys().getId(), companyConfigKpi, companyConstant, searchObject.getMonth(), searchObject.getYear(), dataService);

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, contents));
    }

    /**
     * Luu giai thich cua giam sat danh gia
     *
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SAVE_SUP_KPI_APP, method = RequestMethod.POST)
    public @ResponseBody
    String saveSupKPIApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();
        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        } else if (userRole.getId() != 4) {
            //role failed
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.USER_ROLE_FAILED));
        }

        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        SearchObject searchObject;

        if (jsonString != null) {
            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }

        LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

        if (searchObject.getMcpSalesId() == null) {
            hashErrors.put("mcpSalesId", MsalesValidator.NOT_NULL);
        }

        if (searchObject.getNote() == null || searchObject.getNote().trim().isEmpty()) {
            hashErrors.put("note", MsalesValidator.NOT_NULL_AND_EMPTY);
        }

        if (!hashErrors.isEmpty()) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
        }

        MCP mcpSales = dataService.getRowById(searchObject.getMcpSalesId(), MCP.class);
        if (mcpSales == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.ERROR_MCP_SALES_NULL));
        } else {
            mcpSales.setNote(searchObject.getNote());
            try {
                dataService.updateRow(mcpSales);
            } catch (Exception ex) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_UPDATE_FAIL));
            }
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
        }

    }

    //-------------------------------------------------------------------------------------
    /**
     * PROMOTION
     */
    //-------------------------------------------------------------------------------------
    /**
     * Danh sach Combobox tất cả CTKM
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_CB_GET_LIST_PROMOTION_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getListCbPromotion(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();
        User user = dataService.getRowById(userLoginId, User.class);
        if (user == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        String[] filter = {"startDate", "endDate", "statuss", "accumulationRetailerId", "description", "isMany", "statusTypes", "goodsCategoryList", "reg", "goodsIdList", "proAwardGoodsCategory", "discount", "proAwardOtherQuantity", "conditionString", "proAwardUnits", "goodsIdMins", "awardOthers", "proRanges", "goodsCategoryIdMins", "proAwardGoodss", "approveRoleId", "approveRoles", "companyId", "companys", "code", "note", "approveUserId", "applyScope", "scope", "conditionQuantity", "quantity", "proAwardCode", "proAwardQuantity", "proAwardGoodsId", "proAwardUnitId", "goodsCategorys", "promotionAwards", "goodsIdMin", "quantityMin", "awardOtherId", "proRangeId", "goodsCategoryIdMin", "flagLevel", "flag", "urlImage", "isRegister", "isOnce", "createdAt", "createdUser", "updatedAt", "updatedUser", "deletedAt", "deletedUser", "register", "once", "conditionList", "goodsList", "isRange", "promotionRangeAward"};
        List<Promotion> list = appService.getCbListPromotionByChannelList(appService.getListChannel(userLoginId, dataService), user.getCompanys().getId(), dataService);

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), filter);
    }

    /**
     * Danh sach CTKM dang chay
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_LIST_PROMOTION_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getListPromotion(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();
        User user = dataService.getRowById(userLoginId, User.class);
        if (user == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        //String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        ///Logger.getLogger(CsbController.class.getName()).log(Level.SEVERE, "getListPromotion: \n{0}", jsonString);
        String[] filter = {"accumulationRetailerId", "isMany", "statusTypes", "goodsCategoryList", "reg", "goodsIdList", "proAwardGoodsCategory", "discount", "proAwardOtherQuantity", "conditionString", "proAwardUnits", "goodsIdMins", "awardOthers", "proRanges", "goodsCategoryIdMins", "proAwardGoodss", "approveRoleId", "approveRoles", "companyId", "companys", "code", "note", "approveUserId", "applyScope", "scope", "conditionQuantity", "quantity", "proAwardCode", "proAwardQuantity", "proAwardGoodsId", "proAwardUnitId", "goodsCategorys", "promotionAwards", "goodsIdMin", "quantityMin", "awardOtherId", "proRangeId", "goodsCategoryIdMin", "flagLevel", "flag", "urlImage", "isRegister", "isOnce", "createdAt", "createdUser", "updatedAt", "updatedUser", "deletedAt", "deletedUser", "register", "once", "conditionList", "goodsList", "isRange", "promotionRangeAward"};
        List<Promotion> list = appService.getListPromotionByChannelList(appService.getListChannel(userLoginId, dataService), user.getCompanys().getId(), dataService);

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), filter);
    }

    /**
     * Tim kiem CTKM.
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SEARCH_PROMOTION_APP, method = RequestMethod.POST)
    public @ResponseBody
    String searctPromotionApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();
        User user = dataService.getRowById(userLoginId, User.class
        );
        if (user
                == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchObject.getFromDate() == null) {
                hashErrors.put("fromDate", MsalesValidator.NOT_NULL);
            }
            if (searchObject.getToDate() == null) {
                hashErrors.put("toDate", MsalesValidator.NOT_NULL);
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            String[] filter = {"accumulationRetailerId", "isMany", "goodsCategoryList", "reg", "note", "goodsCategorys", "goodsIdList", "proAwardOtherQuantity", "conditionString", "discount", "proAwardGoodsCategory", "proAwardUnits", "goodsIdMins", "awardOthers", "proRanges", "goodsCategoryIdMins", "proAwardGoodss", "statusTypes", "value", "approveRoleId", "approveRoles", "companyId", "companys", "code", "approveUserId", "applyScope", "scope", "conditionQuantity", "quantity", "proAwardCode", "proAwardQuantity", "proAwardGoodsId", "proAwardUnitId", "promotionAwards", "goodsIdMin", "quantityMin", "awardOtherId", "proRangeId", "goodsCategoryIdMin", "flagLevel", "flag", "urlImage", "isRegister", "isOnce", "createdAt", "createdUser", "updatedAt", "updatedUser", "deletedAt", "deletedUser", "register", "once", "conditionList", "goodsList", "isRange", "promotionRangeAward"};
            List<Promotion> list = appService.searchPromotionApp(appService.getListChannel(userLoginId, dataService), user.getCompanys().getId(), searchObject.getFromDate(), searchObject.getToDate(), dataService);
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), filter);
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Chi tiet CTKM.
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_PROMOTION_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getPromotionApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchObject.getId() == null) {
                hashErrors.put("id", MsalesValidator.NOT_NULL);
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            String[] filter = {"parents", "goodsCategorys", "goodsIdList", "goodsCode", "order", "statuss", "proAwardOtherQuantity", "discount", "proAwardGoodsCategory", "proAwardUnits", "goodsIdMins", "awardOthers", "proRanges", "goodsCategoryIdMins", "proAwardGoodss", "statusTypes", "value", "approveRoleId", "companyId", "companys", "code", "note", "approveUserId", "applyScope", "scope", "conditionQuantity", "quantity", "proAwardCode", "proAwardQuantity", "proAwardGoodsId", "proAwardUnitId", "promotionAwards", "goodsIdMin", "quantityMin", "awardOtherId", "proRangeId", "goodsCategoryIdMin", "flagLevel", "flag", "urlImage", "isRegister", "isOnce", "createdAt", "createdUser", "updatedAt", "updatedUser", "deletedAt", "deletedUser", "register", "conditionList", "isRange", "promotionRangeAward"};

            Promotion promotion = dataService.getRowById(searchObject.getId(), Promotion.class);
            if (promotion == null) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
            }

            //get goodsList
            List<Goods> goodsList = appService.getListGoodsPromotion(promotion.getId(), dataService);
            List<GoodsCategory> goodsCategoryList = new ArrayList<>();

            if (goodsList.isEmpty()) {
                //get all goods by goodsCategory
                if (promotion.getGoodsCategorys() != null) {
                    promotion.setGoodsList(appService.getListGoodsByGoodsCategoryId(promotion.getGoodsCategorys().getId(), dataService));
                    goodsCategoryList.add(promotion.getGoodsCategorys());
                }
            } else {
                promotion.setGoodsList(goodsList);
                for (Goods goods : goodsList) {
                    boolean flag = false;
                    int goodsCategoryId = goods.getGoodsCategorys().getId();
                    for (GoodsCategory goodsCategory : goodsCategoryList) {
                        if (goodsCategoryId == goodsCategory.getId()) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        goodsCategoryList.add(goods.getGoodsCategorys());
                    }
                }
            }

            promotion.setGoodsCategoryList(goodsCategoryList);

            //generateCondition
            promotion.generateConditionString();

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, promotion), filter);
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Danh sach CTKM de dang ky cho DBH
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_LIST_REGISTER_PROMOTION_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getListRegisterPromotion(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchObject.getPosId() == null) {
                hashErrors.put("posId", MsalesValidator.NOT_NULL);

            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            List<Promotion> list = appService.getListRegisterPromotionApp(searchObject.getPosId(), dataService);

            String[] filter = {"isMany", "goodsCategoryList", "reg", "note", "goodsCategorys", "statuss", "goodsIdList", "proAwardOtherQuantity", "conditionString", "discount", "proAwardGoodsCategory", "proAwardUnits", "goodsIdMins", "awardOthers", "proRanges", "goodsCategoryIdMins", "proAwardGoodss", "statusTypes", "value", "approveRoleId", "approveRoles", "companyId", "companys", "code", "approveUserId", "applyScope", "scope", "conditionQuantity", "quantity", "proAwardCode", "proAwardQuantity", "proAwardGoodsId", "proAwardUnitId", "promotionAwards", "goodsIdMin", "quantityMin", "awardOtherId", "proRangeId", "goodsCategoryIdMin", "flagLevel", "flag", "urlImage", "isRegister", "isOnce", "createdAt", "createdUser", "updatedAt", "updatedUser", "deletedAt", "deletedUser", "register", "once", "conditionList", "goodsList", "isRange", "promotionRangeAward"};
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), filter);
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Dang ky CTKM
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_REGISTER_PROMOTION_APP, method = RequestMethod.POST)
    public @ResponseBody
    String registerPromotion(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            POS pos = null;
            List<Promotion> promotionList = new ArrayList<>();
            if (searchObject.getPosId() == null) {
                hashErrors.put("posId", MsalesValidator.NOT_NULL);

            } else {
                pos = dataService.getRowById(searchObject.getPosId(), POS.class);
                if (pos == null) {
                    hashErrors.put("POS", "ID=" + searchObject.getPosId() + " " + MsalesValidator.NOT_EXIST);
                }
            }

            if (searchObject.getPromotionList() == null || searchObject.getPromotionList().isEmpty()) {
                hashErrors.put("promotionList", MsalesValidator.NOT_NULL);

            } else {
                for (Integer id : searchObject.getPromotionList()) {
                    Promotion promotion = dataService.getRowById(id, Promotion.class);
                    if (promotion == null) {
                        hashErrors.put("Promotion", "ID=" + id + " " + MsalesValidator.NOT_EXIST);
                    } else {
                        promotionList.add(promotion);
                    }
                }
            }
            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }
            List insertList = new ArrayList<>();
            for (Promotion promotion : promotionList) {
                if (appService.checkRegisterPromotion(promotion.getId(), pos.getId(), dataService)) {
                    if (promotion.getConditionQuantity() == 1 || promotion.getConditionQuantity() == 2) {
                        insertList.add(createTransRetailer(promotion, pos.getId(), userLoginId));
                    } else if (promotion.getConditionQuantity() == 3 || promotion.getConditionQuantity() == 4) {
                        //KM tich luy => ko can đang ky
                        insertList.add(createAccumulationRetailer(promotion, pos.getId(), userLoginId));
                    }
                }
            }
            try {
                dataService.insertArray(insertList);
            } catch (Exception ex) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.SQL_INSERT_FAIL));
            }
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    private PromotionTransRetailer createTransRetailer(Promotion promotion, int posId, int createdUser) {
        PromotionTransRetailer promotionTransRetailer = new PromotionTransRetailer();
        promotionTransRetailer.setPromotionId(promotion.getId());
        promotionTransRetailer.setRetailerId(posId);
        promotionTransRetailer.setStatusId(30);//da dang ky
        promotionTransRetailer.setCreatedAt(new Date());
        promotionTransRetailer.setCreatedUser(createdUser);
        promotionTransRetailer.setUpdatedUser(0);
        promotionTransRetailer.setDeletedUser(0);

        if (promotion.getPromotionAwards() != null) {
            promotionTransRetailer.setAwardAmount(0);
            switch (promotion.getPromotionAwards().getId()) {
                case 1://tang san pham
                    promotionTransRetailer.setIsOther(0);
                    if (promotion.getProAwardGoodss() != null) {
                        promotionTransRetailer.setAwardGoodsId(promotion.getProAwardGoodss().getId());
                        promotionTransRetailer.setAwardName(promotion.getProAwardGoodss().getName());
                        //promotionTransRetailer.setAwardQuantity(promotion.getProAwardQuantity());
                        promotionTransRetailer.setAwardQuantity(0);
                    }
                    break;
                case 2://tang vat pham
                    promotionTransRetailer.setIsOther(1);
                    if (promotion.getAwardOthers() != null) {
                        promotionTransRetailer.setAwardGoodsId(promotion.getAwardOthers().getId());
                        promotionTransRetailer.setAwardName(promotion.getAwardOthers().getName());
                        //promotionTransRetailer.setAwardQuantity(promotion.getProAwardQuantity());
                        promotionTransRetailer.setAwardQuantity(0);
                    }
                    break;
                case 3:
                    //promotionTransRetailer.setAwardAmount(promotion.getProAwardQuantity());
                    promotionTransRetailer.setAwardName(promotion.getPromotionAwards().getPromotionAwardName());
                    promotionTransRetailer.setAwardQuantity(0);
                    break;
                case 4:
                    //promotionTransRetailer.setAwardQuantity(promotion.getProAwardQuantity());
                    promotionTransRetailer.setAwardName(promotion.getPromotionAwards().getPromotionAwardName());
                    promotionTransRetailer.setAwardQuantity(0);
                    break;
            }
        }
        return promotionTransRetailer;
    }

    private PromotionAccumulationRetailer createAccumulationRetailer(Promotion promotion, int posId, int createdUser) {
        PromotionAccumulationRetailer promotionAccumulationRetailer = new PromotionAccumulationRetailer();
        promotionAccumulationRetailer.setPromotionId(promotion.getId());
        promotionAccumulationRetailer.setRetailerId(posId);
        promotionAccumulationRetailer.setStatusId(30);//da dang ky
        promotionAccumulationRetailer.setAmount(0);
        promotionAccumulationRetailer.setQuantity(0);
        promotionAccumulationRetailer.setCreatedAt(new Date());
        promotionAccumulationRetailer.setCreatedUser(createdUser);
        promotionAccumulationRetailer.setUpdatedUser(0);
        promotionAccumulationRetailer.setDeletedUser(0);

        if (promotion.getPromotionAwards() != null) {
            switch (promotion.getPromotionAwards().getId()) {
                case 1://tang san pham
                    promotionAccumulationRetailer.setIsOther(0);
                    if (promotion.getProAwardGoodss() != null) {
                        promotionAccumulationRetailer.setAwardGoodsId(promotion.getProAwardGoodss().getId());
                        promotionAccumulationRetailer.setAwardName(promotion.getProAwardGoodss().getName());
                        promotionAccumulationRetailer.setAwardQuantity(0);
                    }
                    break;
                case 2://tang vat pham
                    promotionAccumulationRetailer.setIsOther(1);
                    if (promotion.getAwardOthers() != null) {
                        promotionAccumulationRetailer.setAwardGoodsId(promotion.getAwardOthers().getId());
                        promotionAccumulationRetailer.setAwardName(promotion.getAwardOthers().getName());
                        promotionAccumulationRetailer.setAwardQuantity(0);
                    }
                    break;
                case 3:
                    promotionAccumulationRetailer.setAwardAmount(0);
                    promotionAccumulationRetailer.setAwardName(promotion.getPromotionAwards().getPromotionAwardName());
                    break;
                case 4:
                    promotionAccumulationRetailer.setAwardName(promotion.getPromotionAwards().getPromotionAwardName());
                    promotionAccumulationRetailer.setAwardAmount(0);
                    break;
            }
        }
        return promotionAccumulationRetailer;
    }

    /**
     * Danh sach CTKM cua DBH
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_LIST_COMPLETED_PROMOTION_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getListCompletedPromotion(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        List<Integer> userIdList = new ArrayList<>();
        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        } else if (userRole.getId() == 4) {
            //giam sat
            userIdList.addAll(appService.getListUserIdBySup(userLoginId, userLogin.getCompanys().getId(), dataService));
        } else {
            userIdList.add(userLoginId);
        }

        List<Object> list = appService.getListPromotionCompleted(appService.getListChannel(userLoginId, dataService), userLogin.getCompanys().getId(), userIdList, dataService);
        for (Object object : list) {
            initPromotionRetailer(object, userRole, dataService);
        }

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, PromotionRetailer.convertList(list)));

    }

    /**
     * load goods/otherGoods for promtionTrans
     *
     * @param promotionTransRetailer
     */
    private void initPromotionRetailer(Object object, UserRole userRole, DataService dataService) {
        try {
            boolean isTrans = true;
            int promotionAwardId = 0;
            Integer awardId = 0;
            if (object instanceof PromotionTransRetailer) {
                promotionAwardId = ((PromotionTransRetailer) object).getPromotions().getPromotionAwards().getId();
                awardId = ((PromotionTransRetailer) object).getAwardGoodsId();
                if (((PromotionTransRetailer) object).getPromotions().getApproveRoles() == null || ((PromotionTransRetailer) object).getPromotions().getApproveRoles().getId() == userRole.getId()) {
                    ((PromotionTransRetailer) object).setApprove(true);
                }

            } else if (object instanceof PromotionAccumulationRetailer) {
                promotionAwardId = ((PromotionAccumulationRetailer) object).getPromotions().getPromotionAwards().getId();
                awardId = ((PromotionAccumulationRetailer) object).getAwardGoodsId();
                isTrans = false;
                if (((PromotionAccumulationRetailer) object).getPromotions().getApproveRoles() == null || ((PromotionAccumulationRetailer) object).getPromotions().getApproveRoles().getId() == userRole.getId()) {
                    ((PromotionAccumulationRetailer) object).setApprove(true);
                }
            }
            switch (promotionAwardId) {
                case 1:
                    //tang san pham                    
                    Goods goods = dataService.getRowById(awardId, Goods.class);
                    if (goods != null) {
                        PromotionAwardOther promotionAwardOther = new PromotionAwardOther();
                        promotionAwardOther.setId(goods.getId());
                        promotionAwardOther.setName(goods.getName());
                        int price = 0;
                        GoodsUnit goodsUnit = appService.getMinGoodsUnit(goods.getId(), dataService);
                        if (goodsUnit != null) {
                            price = goodsUnit.getPrice();
                            promotionAwardOther.setUnit(goodsUnit.getUnits().getName());
                        }

                        if (isTrans) {
                            promotionAwardOther.setQuantity(((PromotionTransRetailer) object).getAwardQuantity());
                            promotionAwardOther.setPrice(promotionAwardOther.getQuantity() * price);
                            ((PromotionTransRetailer) object).setAwards(promotionAwardOther);
                        } else {
                            promotionAwardOther.setQuantity(((PromotionAccumulationRetailer) object).getAwardQuantity());
                            promotionAwardOther.setPrice(promotionAwardOther.getQuantity() * price);
                            ((PromotionAccumulationRetailer) object).setAwards(promotionAwardOther);
                        }
                    }
                    break;
                case 2:
                    //tang vat pham
                    PromotionAwardOther promotionAwardOther = dataService.getRowById(awardId, PromotionAwardOther.class);
                    if (promotionAwardOther != null) {
                        if (isTrans) {
                            promotionAwardOther.setQuantity(((PromotionTransRetailer) object).getAwardQuantity());
                            promotionAwardOther.setPrice(promotionAwardOther.getQuantity() * promotionAwardOther.getPrice());
                            ((PromotionTransRetailer) object).setAwards(promotionAwardOther);
                        } else {
                            promotionAwardOther.setQuantity(((PromotionAccumulationRetailer) object).getAwardQuantity());
                            promotionAwardOther.setPrice(promotionAwardOther.getQuantity() * promotionAwardOther.getPrice());
                            ((PromotionAccumulationRetailer) object).setAwards(promotionAwardOther);
                        }
                    }
                    break;
                case 3:
                case 4:
                    //chiet khau giam tien - chiet khau % tren doanh so
                    //gan tam chiet khau vào PromotionAwardOther
                    PromotionAwardOther discount = new PromotionAwardOther();
                    discount.setId(0);
                    if (isTrans) {
                        discount.setName(((PromotionTransRetailer) object).getPromotions().getPromotionAwards().getPromotionAwardName());
                        if (promotionAwardId == 3) {
                            //chiet khau giam tien=> id = 0
                            discount.setId(0);
                            discount.setPrice(((PromotionTransRetailer) object).getAwardAmount());
                        } else {
                            //chiet khau % => id =-1
                            discount.setId(-1);
                            discount.setQuantity(((PromotionTransRetailer) object).getAwardQuantity());
                            discount.setPrice(((PromotionTransRetailer) object).getAmount());
                        }
                        ((PromotionTransRetailer) object).setAwards(discount);
                    } else {
                        discount.setName(((PromotionAccumulationRetailer) object).getPromotions().getPromotionAwards().getPromotionAwardName());
                        if (promotionAwardId == 3) {
                            //chiet khau giam tien=> id = 0
                            discount.setId(0);
                            discount.setPrice(((PromotionAccumulationRetailer) object).getAwardAmount());
                        } else {
                            //chiet khau % => id =-1
                            discount.setId(-1);
                            discount.setQuantity(((PromotionAccumulationRetailer) object).getAwardQuantity());
                            discount.setPrice(((PromotionAccumulationRetailer) object).getAmount());
                        }
                        ((PromotionAccumulationRetailer) object).setAwards(discount);
                    }
                    break;
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Danh sach DBH da dang ky CTKM (can duyet)
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_LIST_WAITING_POS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getListPOSWaiting(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();
        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        List<Integer> userIdList = new ArrayList<>();
        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        } else if (userRole.getId() == 4) {
            //giam sat
            userIdList.addAll(appService.getListUserIdBySup(userLoginId, userLogin.getCompanys().getId(), dataService));
        } else {
            userIdList.add(userLoginId);
        }

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //Logger.getLogger(CsbController.class.getName()).log(Level.SEVERE, "getListPOSWaiting: \n{0}", jsonString);
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchObject.getPromotionId() == null) {
                hashErrors.put("promotionId", MsalesValidator.NOT_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            List list = appService.getListPOSWaiting(searchObject.getPromotionId(), userIdList, dataService);
            for (Object object : list) {
                initPromotionRetailer(object, userRole, dataService);
            }

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, PromotionRetailer.convertList(list)));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * danh sach CTKM da hoan thanh
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_LIST_POS_COMPLETED_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getListPOSCompleted(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        List<Integer> userIdList = new ArrayList<>();
        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        } else if (userRole.getId() == 4) {
            //giam sat
            userIdList.addAll(appService.getListUserIdBySup(userLoginId, userLogin.getCompanys().getId(), dataService));
        } else {
            userIdList.add(userLoginId);
        }

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchObject.getPromotionId() == null) {
                hashErrors.put("promotionId", MsalesValidator.NOT_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            List<Object> list = appService.getListPOSCompleted(searchObject.getPromotionId(), userIdList, dataService);
            for (Object object : list) {
                initPromotionRetailer(object, userRole, dataService);
            }

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, PromotionRetailer.convertList(list)));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Danh sach CTKM da giao
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_LIST_DELIVERED_POS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getListPOSDelivered(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        List<Integer> userIdList = new ArrayList<>();
        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        } else if (userRole.getId() == 4) {
            //giam sat
            userIdList.addAll(appService.getListUserIdBySup(userLoginId, userLogin.getCompanys().getId(), dataService));
        } else {
            userIdList.add(userLoginId);
        }

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchObject.getPromotionId() == null) {
                hashErrors.put("promotionId", MsalesValidator.NOT_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            List<Object> list = appService.getListPOSDelivered(searchObject.getPromotionId(), userIdList, dataService);
            for (Object object : list) {
                initPromotionRetailer(object, userRole, dataService);
            }

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, PromotionRetailer.convertList(list)));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Danh sach CTKM da huy
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_GET_LIST_CANCEL_POS_APP, method = RequestMethod.POST)
    public @ResponseBody
    String getListPOSCancelApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        User userLogin = dataService.getRowById(userLoginId, User.class);
        if (userLogin == null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }

        List<Integer> userIdList = new ArrayList<>();
        UserRole userRole = loginService.getUserRole(userLoginId, dataService);
        if (userRole == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        } else if (userRole.getId() == 4) {
            //giam sat
            userIdList.addAll(appService.getListUserIdBySup(userLoginId, userLogin.getCompanys().getId(), dataService));
        } else {
            userIdList.add(userLoginId);
        }

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchObject.getPromotionId() == null) {
                hashErrors.put("promotionId", MsalesValidator.NOT_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            List<Object> list = appService.getListPOSCancel(searchObject.getPromotionId(), userIdList, dataService);
            for (Object object : list) {
                initPromotionRetailer(object, userRole, dataService);
            }

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, PromotionRetailer.convertList(list)));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Giao khuyen mai
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_DELIVER_PROMOTION_APP, method = RequestMethod.POST)
    public @ResponseBody
    String deliverPromotionApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if ((searchObject.getTransRetailerId() == null || searchObject.getTransRetailerId() == 0)
                    && (searchObject.getAccumulationRetailerId() == null || searchObject.getAccumulationRetailerId() == 0)) {
                hashErrors.put("transRetailerId,accumulationRetailerId", MsalesValidator.ALL_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            if (searchObject.getTransRetailerId() != null && searchObject.getTransRetailerId() != 0) {
                //KM so luong/doanh so
                PromotionTransRetailer promotionTransRetailer = dataService.getRowById(searchObject.getTransRetailerId(), PromotionTransRetailer.class);
                if (promotionTransRetailer == null) {
                    hashErrors.put("PromotionTransRetailer", "ID=" + searchObject.getTransRetailerId() + " " + MsalesValidator.NOT_EXIST);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                //change status
                promotionTransRetailer.setStatusId(27);//hoanh thanh=>da giao
                promotionTransRetailer.setUpdatedUser(userLoginId);
                promotionTransRetailer.setUpdatedAt(new Date());

                Transaction transaction = null;
                org.hibernate.Session datasSession = null;
                try {
                    datasSession = dataService.openSession();
                    transaction = datasSession.beginTransaction();

                    datasSession.update(promotionTransRetailer);
                    //tru so luong hang hoa trong kho neu la KM tang sanpham
                    if (promotionTransRetailer.getPromotions().getPromotionAwards().getId() == 1) {
                        //KM tang san pham
                        SalesStock salesStock = appService.getSalesStockUser(userLoginId, dataService);
                        if (salesStock == null) {
                            //loi kho hang
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALESMAN_NO_HAVE_STOCK, MsalesValidator.APP_SALESMAN_NO_HAVE_STOCK));
                        } else {
                            Integer goodsId = promotionTransRetailer.getPromotions().getProAwardGoodss().getId();
                            if (goodsId != null) {
                                SalesStockGoods salesStockGoods = appService.getSalesStockGoodsByGoodsId(salesStock.getId(), goodsId, dataService);
                                LinkedHashMap error = new LinkedHashMap();
                                if (salesStockGoods != null) {
                                    //KT so luong sp trong kho
                                    if (promotionTransRetailer.getAwardQuantity() > salesStockGoods.getQuantity()) {
                                        error.put(MsalesValidator.APP_OVER_ORDER_GOODS, promotionTransRetailer.getAwardName());
                                    } else {
                                        salesStockGoods.setQuantity(salesStockGoods.getQuantity() - promotionTransRetailer.getAwardQuantity());
                                    }
                                } else {
                                    //hang hoa ko co trong kho
                                    error.put(MsalesValidator.APP_SALESMAN_NOT_RECEIVE_GOODS, promotionTransRetailer.getAwardName());
                                }
                                if (!error.isEmpty()) {
                                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                            .create(MsalesStatus.ERROR_SALES_STOCK, error));
                                } else {
                                    datasSession.update(salesStockGoods);
                                }
                            } else {
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                        .create(MsalesStatus.APP_GOODS_ID_NULL));
                            }
                        }
                    }
                    transaction.commit();
                } catch (Exception e) {
                    transaction.rollback();
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                } finally {
                    datasSession.close();
                }
            } else {
                //KM tich luy
                PromotionAccumulationRetailer promotionAccumulationRetailer = dataService.getRowById(searchObject.getAccumulationRetailerId(), PromotionAccumulationRetailer.class);
                if (promotionAccumulationRetailer == null) {
                    hashErrors.put("PromotionAccumulationRetailer", "ID=" + searchObject.getAccumulationRetailerId() + " " + MsalesValidator.NOT_EXIST);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                promotionAccumulationRetailer.setStatusId(27);//hoanh thanh=>da giao
                promotionAccumulationRetailer.setUpdatedUser(userLoginId);
                promotionAccumulationRetailer.setUpdatedAt(new Date());

                Transaction transaction = null;
                org.hibernate.Session datasSession = null;
                try {
                    datasSession = dataService.openSession();
                    transaction = datasSession.beginTransaction();

                    datasSession.update(promotionAccumulationRetailer);
                    //tru so luong hang hoa trong kho neu la KM tang sanpham
                    if (promotionAccumulationRetailer.getPromotions().getPromotionAwards().getId() == 1) {
                        //KM tang san pham
                        SalesStock salesStock = appService.getSalesStockUser(userLoginId, dataService);
                        if (salesStock == null) {
                            //loi kho hang
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALESMAN_NO_HAVE_STOCK, MsalesValidator.APP_SALESMAN_NO_HAVE_STOCK));
                        } else {
                            Integer goodsId = promotionAccumulationRetailer.getPromotions().getProAwardGoodss().getId();
                            if (goodsId != null) {
                                SalesStockGoods salesStockGoods = appService.getSalesStockGoodsByGoodsId(salesStock.getId(), goodsId, dataService);
                                LinkedHashMap error = new LinkedHashMap();
                                if (salesStockGoods != null) {
                                    //KT so luong sp trong kho
                                    if (promotionAccumulationRetailer.getAwardQuantity() > salesStockGoods.getQuantity()) {
                                        error.put(MsalesValidator.APP_OVER_ORDER_GOODS, promotionAccumulationRetailer.getAwardName());
                                    } else {
                                        salesStockGoods.setQuantity(salesStockGoods.getQuantity() - promotionAccumulationRetailer.getAwardQuantity());
                                    }
                                } else {
                                    //hang hoa ko co trong kho
                                    error.put(MsalesValidator.APP_SALESMAN_NOT_RECEIVE_GOODS, promotionAccumulationRetailer.getAwardName());
                                }
                                if (!error.isEmpty()) {
                                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                            .create(MsalesStatus.ERROR_SALES_STOCK, error));
                                } else {
                                    datasSession.update(salesStockGoods);
                                }
                            } else {
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                        .create(MsalesStatus.APP_GOODS_ID_NULL));
                            }
                        }
                    }
                    transaction.commit();
                } catch (Exception e) {
                    transaction.rollback();
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                } finally {
                    datasSession.close();
                }
            }

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Luu khuyen mai
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_SAVE_PROMOTION_APP, method = RequestMethod.POST)
    public @ResponseBody
    String savePromotionApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if ((searchObject.getSalesTransId() == null || searchObject.getSalesTransId() == null)
                    && (searchObject.getSalesOrderId() == null || searchObject.getSalesOrderId() == 0)) {
                hashErrors.put("salesTransId,salesOrderId", MsalesValidator.ALL_NULL);
            }
            if (searchObject.getPromotionId() == null) {
                hashErrors.put("promotionId", MsalesValidator.NOT_NULL);
            }

            if (searchObject.getAccumulationRetailerId() == null) {
                hashErrors.put("accumulationRetailerId", MsalesValidator.NOT_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));

            }

            if (searchObject.getAccumulationRetailerId() != 0) {
                //KM tich luy
                PromotionAccumulationRetailer promotionAccumulationRetailer = dataService.getRowById(searchObject.getAccumulationRetailerId(), PromotionAccumulationRetailer.class);
                if (promotionAccumulationRetailer == null) {
                    hashErrors.put("PromotionAccumulationRetailer", "ID=" + searchObject.getAccumulationRetailerId() + " " + MsalesValidator.NOT_EXIST);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                } else {
                    //tich luy da tu tinh
                    promotionAccumulationRetailer.setStatusId(24);//hoan thanh
                    promotionAccumulationRetailer.setUpdatedUser(userLoginId);
                    promotionAccumulationRetailer.setUpdatedAt(new Date());

                    try {
                        dataService.updateRow(promotionAccumulationRetailer);
                    } catch (Exception ex) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_UPDATE_FAIL));
                    }
                }
            } else {
                //KM truc tiep
                Promotion promotion = dataService.getRowById(searchObject.getPromotionId(), Promotion.class);
                if (promotion == null) {
                    hashErrors.put("Promotion", "ID=" + searchObject.getPromotionId() + " " + MsalesValidator.NOT_EXIST);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                int posId = 0;
                SalesOrder salesOrder = null;
                SalesTrans salesTrans = null;
                //test salesTrans/salesOrder
                if (searchObject.getSalesOrderId() != null && searchObject.getSalesOrderId() != 0) {
                    salesOrder = dataService.getRowById(searchObject.getSalesOrderId(), SalesOrder.class);
                    if (salesOrder == null) {
                        hashErrors.put("SalesOrder", "ID=" + searchObject.getSalesOrderId() + " " + MsalesValidator.NOT_EXIST);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else if (salesOrder.getStatuss().getId() != 18
                            && salesOrder.getStatuss().getId() != 19
                            && salesOrder.getStatuss().getId() != 20) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.APP_ORDER_NOT_DELIVER));
                    }
                    posId = salesOrder.getPos().getId();
                } else if (searchObject.getSalesTransId() != null && searchObject.getSalesTransId() != 0) {
                    salesTrans = dataService.getRowById(searchObject.getSalesTransId(), SalesTrans.class);
                    if (salesTrans == null) {
                        hashErrors.put("SalesTrans", "ID=" + searchObject.getSalesTransId() + " " + MsalesValidator.NOT_EXIST);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else if (salesTrans.getTransStatus() != 1 && salesTrans.getTransType() != 2) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.APP_NOT_SALESTYPE_SALESTRANS));
                    }
                    posId = salesTrans.getToStocks().getPoss().getId();
                }

                boolean flagCompleted = false;
                if (promotion.getConditionQuantity() == 1 || promotion.getConditionQuantity() == 2) {
                    //khuyen mai Truc tiep
                    PromotionTransRetailer promotionTransRetailer = null;
                    List<PromotionTransRetailer> promotionTransRetailerList = appService.getListPromotionTransRetailer(posId, searchObject.getPromotionId(), dataService);
                    for (PromotionTransRetailer transRetailer : promotionTransRetailerList) {
                        if (transRetailer != null
                                && transRetailer.getStatuss().getId() != 25 // bi loai
                                ) {
                            if (transRetailer.getStatuss().getId() == 24 //hoan thanh
                                    || transRetailer.getStatuss().getId() == 27//da giao KM  
                                    || transRetailer.getStatuss().getId() == 23//dang cho duyet     
                                    ) {
                                flagCompleted = true;
                            } else if (transRetailer.getStatuss().getId() == 29) {
                                //da duyet dang ky
                                promotionTransRetailer = transRetailer;
                                break;
                            }
                        }
                    }

                    int awardQuantity = 0;
                    int amount = 0;
                    int quantity = 0;

                    List<PromotionGoodsRef> list = appService.getListPromotionGoodsRef(promotion.getId(), dataService);

                    for (PromotionGoodsRef proGoodsRef : list) {
                        if (salesOrder != null) {
                            List<SalesOrderDetails> salesOrderDetailsList = appService.getListSalesOrderDetails(salesOrder.getId(), dataService);
                            for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                                if (Objects.equals(proGoodsRef.getGoodss().getId(), salesOrderDetails.getGoodss().getId())) {
                                    if (promotion.getConditionQuantity() == 1) {
                                        //so luong ban theo don hang
                                        if (salesOrderDetails.getQuantity() >= promotion.getQuantity()) {
                                            quantity += salesOrderDetails.getQuantity();
                                            amount += salesOrderDetails.getQuantity() * salesOrderDetails.getPrice();

                                            break;
                                        }
                                    } else if (promotion.getConditionQuantity() == 2) {
                                        //doanh thu theo don hang
                                        if (salesOrderDetails.getQuantity() * salesOrderDetails.getPrice() >= promotion.getQuantity()) {
                                            quantity += salesOrderDetails.getQuantity();
                                            amount += salesOrderDetails.getQuantity() * salesOrderDetails.getPrice();
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            //salesTrans
                            List<SalesTransDetails> salesTransDetailsList = appService.getListSalesTransDetails(salesTrans.getId(), dataService);
                            for (SalesTransDetails salesTransDetails : salesTransDetailsList) {
                                if (Objects.equals(proGoodsRef.getGoodss().getId(), salesTransDetails.getGoodss().getId())) {
                                    if (promotion.getConditionQuantity() == 1) {
                                        //so luong ban theo don hang
//                                        if (salesTransDetails.getQuantity() >= promotion.getQuantity()) {
//                                            quantity += salesTransDetails.getQuantity();
//                                            amount += salesTransDetails.getQuantity() * salesTransDetails.getPrice();
//
//                                            break;
//                                        }
                                        quantity += salesTransDetails.getQuantity();
                                        amount += salesTransDetails.getQuantity() * salesTransDetails.getPrice();
                                        break;

                                    } else if (promotion.getConditionQuantity() == 2) {
                                        //doanh thu theo don hang
//                                        if (salesTransDetails.getQuantity() * salesTransDetails.getPrice() >= promotion.getQuantity()) {
//                                            quantity += salesTransDetails.getQuantity();
//                                            amount += salesTransDetails.getQuantity() * salesTransDetails.getPrice();
//                                            break;
//                                        }
                                        quantity += salesTransDetails.getQuantity();
                                        amount += salesTransDetails.getQuantity() * salesTransDetails.getPrice();
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    //ko kiem tra lai so luong thoa KM-ben getListPromotionCanByPOS da kt                    
                    if (promotion.getConditionQuantity() == 1) {
                        //So luong theo don hang
                        awardQuantity = quantity / promotion.getQuantity() * promotion.getProAwardQuantity();
                    } else {
                        //2=>doanh thu theo don hang
                        awardQuantity = amount / promotion.getQuantity() * promotion.getProAwardQuantity();
                    }
                    if (promotion.getPromotionAwards().getId() == 4) {
                        //chiet khau %
                        awardQuantity = promotion.getProAwardQuantity();
                    }

                    if (promotion.getIsRegister() == 1) {
                        //KM dang ky
                        //kiem tra
                        if (promotionTransRetailer == null) {
                            if (flagCompleted)//da giao /hoan thanh thi ko can dang ky lai
                            {
                                //tao moi TransRetailer
                                promotionTransRetailer = new PromotionTransRetailer();
                                promotionTransRetailer.setRetailerId(posId);
                                promotionTransRetailer.setPromotionId(promotion.getId());
                                promotionTransRetailer.setStatusId(23);//chuyen trang thai Dang cho duyet voi KM Truc tiep can dang ky
                                promotionTransRetailer.setAmount(amount);
                                promotionTransRetailer.setQuantity(quantity);
                                if (promotion.getPromotionAwards().getId() == 1) {
                                    promotionTransRetailer.setAwardQuantity(awardQuantity);
                                    promotionTransRetailer.setAwardName(promotion.getProAwardGoodss().getName());
                                    promotionTransRetailer.setIsOther(0);
                                    promotionTransRetailer.setAwardGoodsId(promotion.getProAwardGoodss().getId());
                                } else if (promotion.getPromotionAwards().getId() == 2) {
                                    promotionTransRetailer.setAwardQuantity(awardQuantity);
                                    promotionTransRetailer.setAwardName(promotion.getAwardOthers().getName());
                                    promotionTransRetailer.setIsOther(1);
                                    promotionTransRetailer.setAwardGoodsId(promotion.getAwardOthers().getId());
                                } else {
                                    promotionTransRetailer.setAwardName(promotion.getPromotionAwards().getPromotionAwardName());
                                    if (promotion.getPromotionAwards().getId() == 3) {
                                        promotionTransRetailer.setAwardAmount(awardQuantity);
                                    } else {
                                        //= 4
                                        promotionTransRetailer.setAwardQuantity(awardQuantity);
                                        promotionTransRetailer.setAwardAmount(awardQuantity * amount / 100);
                                    }
                                }
                                promotionTransRetailer.setCreatedUser(userLoginId);
                                promotionTransRetailer.setCreatedAt(new Date());
                                promotionTransRetailer.setUpdatedUser(0);
                                promotionTransRetailer.setDeletedUser(0);

                                try {
                                    dataService.insert(promotionTransRetailer);
                                } catch (Exception ex) {
                                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                            .create(MsalesStatus.SQL_INSERT_FAIL));
                                }

                            } else {
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                        .create(MsalesStatus.APP_PROMOTION_ERROR));
                            }
                        } else {
                            //KM TT can dang ky=>ban hang thoa=>dang cho duyet
                            promotionTransRetailer.setStatusId(23);//dang cho duyet
                            promotionTransRetailer.setUpdatedUser(userLoginId);
                            promotionTransRetailer.setUpdatedAt(new Date());

                            promotionTransRetailer.setAmount(amount);
                            promotionTransRetailer.setQuantity(quantity);

                            if (promotion.getPromotionAwards().getId() == 4) {
                                //chiet khau %
                                promotionTransRetailer.setAwardAmount(awardQuantity * amount / 100);
                                promotionTransRetailer.setAwardQuantity(awardQuantity);
                            } else if (promotion.getPromotionAwards().getId() == 3) {
                                //giam tien
                                promotionTransRetailer.setAwardAmount(awardQuantity);
                            } else {
                                promotionTransRetailer.setAwardQuantity(awardQuantity);
                            }

                            try {
                                dataService.updateRow(promotionTransRetailer);
                            } catch (Exception ex) {
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                        .create(MsalesStatus.SQL_UPDATE_FAIL));
                            }
                        }
                    } else {
                        //KM ko dang ky
                        promotionTransRetailer = new PromotionTransRetailer();

                        promotionTransRetailer.setRetailerId(posId);
                        promotionTransRetailer.setPromotionId(promotion.getId());
                        promotionTransRetailer.setStatusId(24);//chuyen trang thai hoan thanh

                        promotionTransRetailer.setAmount(amount);
                        promotionTransRetailer.setQuantity(quantity);
                        if (promotion.getPromotionAwards().getId() == 1) {
                            promotionTransRetailer.setAwardQuantity(awardQuantity);
                            promotionTransRetailer.setAwardName(promotion.getProAwardGoodss().getName());
                            promotionTransRetailer.setIsOther(0);
                            promotionTransRetailer.setAwardGoodsId(promotion.getProAwardGoodss().getId());
                        } else if (promotion.getPromotionAwards().getId() == 2) {
                            promotionTransRetailer.setAwardQuantity(awardQuantity);
                            promotionTransRetailer.setAwardName(promotion.getAwardOthers().getName());
                            promotionTransRetailer.setIsOther(1);
                            promotionTransRetailer.setAwardGoodsId(promotion.getAwardOthers().getId());
                        } else {
                            promotionTransRetailer.setAwardName(promotion.getPromotionAwards().getPromotionAwardName());
                            if (promotion.getPromotionAwards().getId() == 3) {
                                promotionTransRetailer.setAwardAmount(awardQuantity);
                            } else {
                                //= 4
                                promotionTransRetailer.setAwardQuantity(awardQuantity);
                                promotionTransRetailer.setAwardAmount(awardQuantity * amount / 100);
                            }
                        }
                        promotionTransRetailer.setCreatedUser(userLoginId);
                        promotionTransRetailer.setCreatedAt(new Date());
                        promotionTransRetailer.setUpdatedUser(0);
                        promotionTransRetailer.setDeletedUser(0);

                        try {
                            dataService.insert(promotionTransRetailer);
                        } catch (Exception ex) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.SQL_INSERT_FAIL));
                        }
                    }
                } else {
                    //loi du lieu
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.APP_PROMOTION_ERROR));
                }
            }

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Duyet Dang Ky khuyen mai
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_APPROVE_REGISTER_PROMOTION_APP, method = RequestMethod.POST)
    public @ResponseBody
    String approveRegisterPromotionApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if ((searchObject.getTransRetailerId() == null || searchObject.getTransRetailerId() == 0)
                    && (searchObject.getAccumulationRetailerId() == null || searchObject.getAccumulationRetailerId() == 0)) {
                hashErrors.put("transRetailerId,accumulationRetailerId", MsalesValidator.ALL_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            //khong check trang thai la dang cho duyet
            if (searchObject.getTransRetailerId() != null && searchObject.getTransRetailerId() != 0) {
                //KM doanh so/so luong
                PromotionTransRetailer promotionTransRetailer = dataService.getRowById(searchObject.getTransRetailerId(), PromotionTransRetailer.class);
                if (promotionTransRetailer == null) {
                    hashErrors.put("PromotionTransRetailer", "ID=" + searchObject.getTransRetailerId() + " " + MsalesValidator.NOT_EXIST);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                } else if (promotionTransRetailer.getStatuss().getId() != 30) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.APP_PROMOTION_NOT_REGISTER));
                }
                promotionTransRetailer.setStatusId(29);//da duyet dang ky
                promotionTransRetailer.setApproveId(userLoginId);
                promotionTransRetailer.setUpdatedUser(userLoginId);
                promotionTransRetailer.setUpdatedAt(new Date());
                try {
                    dataService.updateRow(promotionTransRetailer);
                } catch (Exception ex) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_UPDATE_FAIL));
                }

            } else {
                //Km tich luy
                PromotionAccumulationRetailer promotionAccumulationRetailer = dataService.getRowById(searchObject.getAccumulationRetailerId(), PromotionAccumulationRetailer.class);
                if (promotionAccumulationRetailer == null) {
                    hashErrors.put("PromotionAccumulationRetailer", "ID=" + searchObject.getAccumulationRetailerId() + " " + MsalesValidator.NOT_EXIST);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                } else if (promotionAccumulationRetailer.getStatuss().getId() != 30) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.APP_PROMOTION_NOT_REGISTER));
                }

                promotionAccumulationRetailer.setStatusId(29);//da duyet dang ky
                promotionAccumulationRetailer.setApproveId(userLoginId);
                promotionAccumulationRetailer.setUpdatedUser(userLoginId);
                promotionAccumulationRetailer.setUpdatedAt(new Date());
                try {
                    dataService.updateRow(promotionAccumulationRetailer);
                } catch (Exception ex) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_UPDATE_FAIL));
                }
            }
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Huy khuyen mai
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_CANCEL_PROMOTION_APP, method = RequestMethod.POST)
    public @ResponseBody
    String cancelPromotionApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if ((searchObject.getTransRetailerId() == null || searchObject.getTransRetailerId() == 0)
                    && (searchObject.getAccumulationRetailerId() == null || searchObject.getAccumulationRetailerId() == 0)) {
                hashErrors.put("transRetailerId,accumulationRetailerId", MsalesValidator.ALL_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            //khong check trang thai la dang cho duyet
            if (searchObject.getTransRetailerId() != null && searchObject.getTransRetailerId() != 0) {
                //KM doanh so/so luong
                PromotionTransRetailer promotionTransRetailer = dataService.getRowById(searchObject.getTransRetailerId(), PromotionTransRetailer.class);
                if (promotionTransRetailer == null) {
                    hashErrors.put("PromotionTransRetailer", "ID=" + searchObject.getTransRetailerId() + " " + MsalesValidator.NOT_EXIST);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                promotionTransRetailer.setStatusId(25);
                promotionTransRetailer.setUpdatedUser(userLoginId);
                promotionTransRetailer.setUpdatedAt(new Date());
                try {
                    dataService.updateRow(promotionTransRetailer);
                } catch (Exception ex) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_UPDATE_FAIL));
                }

            } else {
                //KM tich luy
                PromotionAccumulationRetailer promotionAccumulationRetailer = dataService.getRowById(searchObject.getAccumulationRetailerId(), PromotionAccumulationRetailer.class);
                if (promotionAccumulationRetailer == null) {
                    hashErrors.put("PromotionAccumulationRetailer", "ID=" + searchObject.getAccumulationRetailerId() + " " + MsalesValidator.NOT_EXIST);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                promotionAccumulationRetailer.setStatusId(25);
                promotionAccumulationRetailer.setUpdatedUser(userLoginId);
                promotionAccumulationRetailer.setUpdatedAt(new Date());
                try {
                    dataService.updateRow(promotionAccumulationRetailer);
                } catch (Exception ex) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_UPDATE_FAIL));
                }
            }
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Duyet khuyen mai=> hoan thanh
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.APP.ACTION_APPROVE_PROMOTION_APP, method = RequestMethod.POST)
    public @ResponseBody
    String approvePromotionApp(HttpServletRequest request) {
        //get session
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userLoginId = session.getUserId();

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;

            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }

            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if ((searchObject.getTransRetailerId() == null || searchObject.getTransRetailerId() == 0)
                    && (searchObject.getAccumulationRetailerId() == null || searchObject.getAccumulationRetailerId() == 0)) {
                hashErrors.put("transRetailerId,accumulationRetailerId", MsalesValidator.ALL_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            //Khong check trang thai la dang cho duyet
            if (searchObject.getTransRetailerId() != null && searchObject.getTransRetailerId() != 0) {
                //KM doanh so/so luong
                PromotionTransRetailer promotionTransRetailer = dataService.getRowById(searchObject.getTransRetailerId(), PromotionTransRetailer.class);
                if (promotionTransRetailer == null) {
                    hashErrors.put("PromotionTransRetailer", "ID=" + searchObject.getTransRetailerId() + " " + MsalesValidator.NOT_EXIST);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                promotionTransRetailer.setStatusId(24);//hoan thanh
                promotionTransRetailer.setApproveId(userLoginId);
                promotionTransRetailer.setUpdatedUser(userLoginId);
                promotionTransRetailer.setUpdatedAt(new Date());
                try {
                    dataService.updateRow(promotionTransRetailer);
                } catch (Exception ex) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_UPDATE_FAIL));
                }

            } else {
                //Km tich luy
                PromotionAccumulationRetailer promotionAccumulationRetailer = dataService.getRowById(searchObject.getAccumulationRetailerId(), PromotionAccumulationRetailer.class);
                if (promotionAccumulationRetailer == null) {
                    hashErrors.put("PromotionAccumulationRetailer", "ID=" + searchObject.getAccumulationRetailerId() + " " + MsalesValidator.NOT_EXIST);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                promotionAccumulationRetailer.setStatusId(24);//hoanh thanh
                promotionAccumulationRetailer.setApproveId(userLoginId);
                promotionAccumulationRetailer.setUpdatedUser(userLoginId);
                promotionAccumulationRetailer.setUpdatedAt(new Date());
                try {
                    dataService.updateRow(promotionAccumulationRetailer);
                } catch (Exception ex) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_UPDATE_FAIL));
                }
            }
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

}
