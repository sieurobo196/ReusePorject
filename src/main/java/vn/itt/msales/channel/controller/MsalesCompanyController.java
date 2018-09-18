/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.channel.controller;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.itt.msales.app.service.AppService;
import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.csb.auth.MsalesSession;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.database.dbrouting.DatabaseType;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelLocation;
import vn.itt.msales.entity.ChannelType;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.CompanyConfig;
import vn.itt.msales.entity.CompanyConfigDetails;
import vn.itt.msales.entity.CompanyConfigKpi;
import vn.itt.msales.entity.CompanyConstant;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsUnit;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.MsalesCompany;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.Workflow;
import vn.itt.msales.entity.WorkflowDetails;
import vn.itt.msales.entity.WorkflowType;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.services.DataService;
import vn.itt.msales.workflow.service.CompanyWorkflowService;

/**
 *
 * @author KhaiCH
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.COMPANY.NAME)
public class MsalesCompanyController extends CsbController {

    /**
     * create a Company
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @Autowired
    private AppService appService;

    @Autowired
    private CompanyWorkflowService workflowService;

    @RequestMapping(value = MsalesConstants.MODULE.COMPANY.ACTION_CREATE_COMPANY, method = RequestMethod.POST)
    public @ResponseBody
    String createCompany(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Company company;
            try {
                //parse jsonString to a Company Object
                company = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Company.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //company from json not null
            if (company != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (company.getLocationId() != null) {
                    Location location = dataService.getRowById(company.getLocationId(), Location.class);
                    if (location == null) {
                        hashErrors.put("Location",
                                "ID = " + company.getLocationId() + " không tồn tại.");
                    }
                }
                if (company.getStatusId() != null) {
                    Status status = dataService.getRowById(company.getStatusId(), Status.class);
                    if (status == null) {
                        hashErrors.put("Status",
                                "ID = " + company.getStatusId() + " không tồn tại.");
                    }
                }

                if (company.getCreatedUser() != null) {
                    User user = dataService.getRowById(company.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + company.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    int ret = dataService.insertRow(company);
                    //save company success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save company failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_INSERT_FAIL));
                    }
                } catch (Exception e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }
            } //company from json null
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
     * update a Company
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANY.ACTION_UPDATE_COMPANY, method = RequestMethod.POST)
    public @ResponseBody
    String updateCompany(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Company company;
            try {
                //parse jsonString to a company Object
                company = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Company.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //company from json not null
            if (company != null) {

                if (company.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = company.getId();
                //validate info relation
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (company.getLocationId() != null) {
                    Location location = dataService.getRowById(company.getLocationId(), Location.class);
                    if (location == null) {
                        hashErrors.put("Location",
                                "ID = " + company.getLocationId() + " không tồn tại.");
                    }
                }
                if (company.getStatusId() != null) {
                    Status status = dataService.getRowById(company.getStatusId(), Status.class);
                    if (status == null) {
                        hashErrors.put("Status",
                                "ID = " + company.getStatusId() + " không tồn tại.");
                    }
                }

                if (company.getUpdatedUser() != null) {
                    User user = dataService.getRowById(company.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + company.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    int ret = dataService.updateSync(company);
                    //update success
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(Company.class, id);
                    }
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
            } //tableName from json null
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
     * delete a TableName
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANY.ACTION_DELETE_COMPANY, method = RequestMethod.POST)
    public @ResponseBody
    String deleteCompany(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Company company;
            try {
                //parse jsonString to a Company Object
                company = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Company.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //company from json not null
            if (company != null) {
                if (company.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                if (company.getDeletedUser() != null) {
                    User user = dataService.getRowById(company.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, company.getDeletedUser());
                    }
                }

                int id = company.getId();
                try {
                    //delete company from DB
                    int ret = dataService.deleteSynch(company);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(Company.class, id);
                    }

                    //update delete company success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update delete company failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }

            } //company from json null
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
     * get a Comapny info
     *
     * @param request is a HttpServletRequest
     * @return string json include Comapny info
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANY.ACTION_GET_COMPANY, method = RequestMethod.POST)
    public @ResponseBody
    String getCompany(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Company company;
            try {
                //parse jsonString to a Comapny Object
                company = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Company.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //company from json not null
            if (company != null) {
                if (company.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = company.getId();
                //get company from DB
                company = dataService.getRowById(company.getId(),
                        Company.class);
                //company not null
                if (company != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, company));
                } //company null
                else {
                    return MsalesJsonUtils.notExists(Company.class, id);
                }
            } //company from json null
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
     * get List all Company
     *
     * @param request is a HttpServletRequest
     * @return string json include List Company
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANY.ACTION_GET_LIST_COMPANY, method = RequestMethod.POST)
    public @ResponseBody
    String getListCompany(HttpServletRequest request) {

        //get List Company from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

        MsalesResults<Company> list = dataService.getListOption(Company.class, parameterList, true);

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));

    }

    /**
     * get Cb List Coompany
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANY.ACTION_GET_CB_LIST_COMPANY, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListCompany(HttpServletRequest request) {

        //get List Company from DB
        ParameterList parameterList = new ParameterList(0, 0);

        MsalesResults<Company> list = dataService.getListOption(Company.class, parameterList, true);

        String[] filter = {"address", "contactPersonName", "tel", "fax", "email", "lat", "lng", "logoPath",
            "note", "bgColor", "textColor", "buttonBgColor", "buttonBgColorOver", "topBarBGColor", "locations", "statuss"};
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), filter);

    }

    /**
     * get branch by companyCode
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANY.ACTION_GET_BRANCH_BY_COMPANY_CODE, method = RequestMethod.POST)
    public @ResponseBody
    String getBranchByCompanyCode(HttpServletRequest request) {

        //set barnch to 0 = db MsalesCompany
        dataService.setBranch(DatabaseType.DATABASE_TYPE_COMPANY.getBranch());;
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            MsalesCompany msalesCompany;
            try {
                msalesCompany = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        MsalesCompany.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            if (msalesCompany != null) {
                if (msalesCompany.getCode() == null || msalesCompany.getCode().trim().isEmpty()) {
                    LinkedHashMap hashErrors = new LinkedHashMap();
                    hashErrors.put("code", MsalesValidator.NOT_NULL_AND_EMPTY);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                ParameterList parameterList = new ParameterList(1, 1);
                parameterList.add("code", msalesCompany.getCode());
                parameterList.add("isActive", 1);
                //get MsalesCompnay
                List<MsalesCompany> listMsalesCompany = dataService.getListOption(MsalesCompany.class, parameterList);
                String[] filter = {"isActive", "companyName", "id"};
                if (listMsalesCompany.size() == 1) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, listMsalesCompany.get(0)), filter);
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //company from json null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    private List<ChannelLocation> createChannelLocation(Channel channel, int[] locationIds, int createdUser) {
        List<ChannelLocation> ret = new ArrayList<>();
        for (int locationId : locationIds) {
            ChannelLocation channelLocation = new ChannelLocation();
            channelLocation.setLocationId(locationId);
            channelLocation.setChannels(channel);
            channelLocation.setCreatedAt(new Date());
            channelLocation.setCreatedUser(createdUser);
            channelLocation.setUpdatedUser(0);
            channelLocation.setDeletedUser(0);
            ret.add(channelLocation);
        }
        return ret;
    }

    private boolean createCompanyTemplete(Company company, DataService dataService) {
        Transaction transaction = null;
        Session session = null;
        try {
            dataService.setBranch(company.getBranch());
            session = dataService.openSession();
            transaction = session.beginTransaction();

            if (company.getEmployeeAmount() == null) {
                company.setEmployeeAmount(0);
            }
            if (company.getEmployeeSaleAmount() == null) {
                company.setEmployeeSaleAmount(0);
            }
            company.setLat(BigDecimal.ZERO);
            company.setLng(BigDecimal.ZERO);
            company.setStatusId(1);
            company.setEmail(company.getEmail());
            company.setTel(company.getTel());
            company.setIsSendmailOrderList(false);
            company.setBgColor("FFFFFF");
            company.setTextColor("FFFFFF");
            company.setButtonBgColor("930b0d");
            company.setButtonBgColorOver("FF0000");
            company.setTopBarBGColor("930b0d");

            if (company.getExpireTime() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(company.getExpireTime());
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                company.setExpireTime(calendar.getTime());
            }
            company.setIsRegister(1);

            company.setCreatedAt(new Date());
            company.setCreatedUser(0);
            company.setUpdatedUser(0);
            company.setDeletedUser(0);
            session.save(company);

            //create user
            User user1 = new User();
            user1.setUsername(company.getUsername());
            user1.setCompanys(company);
            user1.setUserCode("admin01@" + company.getCode());
            user1.setPassword(MsalesSession.getSHA256(company.getPassword()));
            user1.setLastName(company.getLastName());
            user1.setFirstName(company.getFirstName());
            user1.setEmail(company.getEmail());
            user1.setTel(company.getTel());
            user1.setLocationId(4);
            user1.setIsActive(1);
            user1.setStatusId(6);
            user1.setCreatedUser(0);//cap nhat lai sau
            user1.setCreatedAt(new Date());
            user1.setUpdatedUser(0);
            user1.setDeletedUser(0);
            session.save(user1);

            int createdUser = user1.getId();

            //create KPI
            CompanyConfigKpi companyConfigKpi = new CompanyConfigKpi();
            companyConfigKpi.setCompanyId(company.getId());
            companyConfigKpi.setCreatedAt(new Date());
            companyConfigKpi.setCreatedUser(createdUser);
            companyConfigKpi.setUpdatedUser(0);
            companyConfigKpi.setDeletedUser(0);
            session.save(companyConfigKpi);

            //create channel type
            ChannelType channelType1 = new ChannelType();
            channelType1.setCompanys(company);
            channelType1.setParents(null);
            channelType1.setCode(company.getCode() + "_REGION");
            channelType1.setName("Miền");
            channelType1.setCreatedAt(new Date());
            channelType1.setCreatedUser(createdUser);
            channelType1.setUpdatedUser(0);
            channelType1.setDeletedUser(0);
            session.save(channelType1);

            ChannelType channelType2 = new ChannelType();
            channelType2.setCompanys(company);
            channelType2.setParents(channelType1);
            channelType2.setCode(company.getCode() + "_AREA");
            channelType2.setName("Khu vực");
            channelType2.setCreatedAt(new Date());
            channelType2.setCreatedUser(createdUser);
            channelType2.setUpdatedUser(0);
            channelType2.setDeletedUser(0);
            session.save(channelType2);

            ChannelType channelType3 = new ChannelType();
            channelType3.setCompanys(company);
            channelType3.setParents(channelType2);
            channelType3.setCode(company.getCode() + "_SUP");
            channelType3.setName("Giám sát");
            channelType3.setCreatedAt(new Date());
            channelType3.setCreatedUser(createdUser);
            channelType3.setUpdatedUser(0);
            channelType3.setDeletedUser(0);
            session.save(channelType3);

            ChannelType channelType4 = new ChannelType();
            channelType4.setCompanys(company);
            channelType4.setParents(channelType3);
            channelType4.setCode(company.getCode() + "_NPP");
            channelType4.setName("Nhà phân phối");
            channelType4.setCreatedAt(new Date());
            channelType4.setCreatedUser(createdUser);
            channelType4.setUpdatedUser(0);
            channelType4.setDeletedUser(0);
            session.save(channelType4);

            //create Channel
            Channel channel1 = new Channel();
            channel1.setFullCode("MN");
            channel1.setCode("MN");
            channel1.setCompanys(company);
            channel1.setParents(null);
            channel1.setChannelTypes(channelType1);
            channel1.setIsSalePoint(1);
            channel1.setName("Miền Nam");
            channel1.setStatusId(10);
            channel1.setLat(BigDecimal.ZERO);
            channel1.setLng(BigDecimal.ZERO);
            channel1.setCreatedAt(new Date());
            channel1.setCreatedUser(createdUser);
            channel1.setUpdatedUser(0);
            channel1.setDeletedUser(0);
            session.save(channel1);

            //create channel Location for channel1
            int[] locations1 = {4};
            List<ChannelLocation> channelLocations1 = createChannelLocation(channel1, locations1, createdUser);
            for (ChannelLocation channelLocation : channelLocations1) {
                session.save(channelLocation);
            }

            List<User> userList = new ArrayList<>();
            List<Channel> channelList = new ArrayList<>();
            //List<POS> posList = new ArrayList<>();

            if (company.isIsTemplate()) {
                //create Channel
                Channel channel2 = new Channel();
                channel2.setFullCode("MN_HCM01");
                channel2.setCode("HCM01");
                channel2.setCompanys(company);
                channel2.setParents(channel1);
                channel2.setChannelTypes(channelType2);
                channel2.setIsSalePoint(0);
                channel2.setName("HCM01");
                channel2.setStatusId(10);
                channel2.setLat(BigDecimal.ZERO);
                channel2.setLng(BigDecimal.ZERO);
                channel2.setCreatedAt(new Date());
                channel2.setCreatedUser(createdUser);
                channel2.setUpdatedUser(0);
                channel2.setDeletedUser(0);
                session.save(channel2);

                Channel channel3 = new Channel();
                channel3.setFullCode("MN_HCM02");
                channel3.setCode("HCM02");
                channel3.setCompanys(company);
                channel3.setParents(channel1);
                channel3.setChannelTypes(channelType2);
                channel3.setIsSalePoint(0);
                channel3.setName("HCM02");
                channel3.setStatusId(10);
                channel3.setLat(BigDecimal.ZERO);
                channel3.setLng(BigDecimal.ZERO);
                channel3.setCreatedAt(new Date());
                channel3.setCreatedUser(createdUser);
                channel3.setUpdatedUser(0);
                channel3.setDeletedUser(0);
                session.save(channel3);

                Channel channel4 = new Channel();
                channel4.setFullCode("MN_HCM01_GS01");
                channel4.setCode("GS01");
                channel4.setCompanys(company);
                channel4.setParents(channel2);
                channel4.setChannelTypes(channelType3);
                channel4.setIsSalePoint(0);
                channel4.setName("GS01");
                channel4.setStatusId(10);
                channel4.setLat(BigDecimal.ZERO);
                channel4.setLng(BigDecimal.ZERO);
                channel4.setCreatedAt(new Date());
                channel4.setCreatedUser(createdUser);
                channel4.setUpdatedUser(0);
                channel4.setDeletedUser(0);
                session.save(channel4);

                Channel channel5 = new Channel();
                channel5.setFullCode("MN_HCM01_GS02");
                channel5.setCode("GS02");
                channel5.setCompanys(company);
                channel5.setParents(channel2);
                channel5.setChannelTypes(channelType3);
                channel5.setIsSalePoint(0);
                channel5.setName("GS02");
                channel5.setStatusId(10);
                channel5.setLat(BigDecimal.ZERO);
                channel5.setLng(BigDecimal.ZERO);
                channel5.setCreatedAt(new Date());
                channel5.setCreatedUser(createdUser);
                channel5.setUpdatedUser(0);
                channel5.setDeletedUser(0);
                session.save(channel5);

                Channel channel6 = new Channel();
                channel6.setFullCode("MN_HCM02_01");
                channel6.setCode("01");
                channel6.setCompanys(company);
                channel6.setParents(channel3);
                channel6.setChannelTypes(channelType3);
                channel6.setIsSalePoint(0);
                channel6.setName("GS_01");
                channel6.setStatusId(10);
                channel6.setLat(BigDecimal.ZERO);
                channel6.setLng(BigDecimal.ZERO);
                channel6.setCreatedAt(new Date());
                channel6.setCreatedUser(createdUser);
                channel6.setUpdatedUser(0);
                channel6.setDeletedUser(0);
                session.save(channel6);

                Channel channel7 = new Channel();
                channel7.setFullCode("MN_HCM02_02");
                channel7.setCode("02");
                channel7.setCompanys(company);
                channel7.setParents(channel3);
                channel7.setChannelTypes(channelType3);
                channel7.setIsSalePoint(0);
                channel7.setName("GS_02");
                channel7.setStatusId(10);
                channel7.setLat(BigDecimal.ZERO);
                channel7.setLng(BigDecimal.ZERO);
                channel7.setCreatedAt(new Date());
                channel7.setCreatedUser(createdUser);
                channel7.setUpdatedUser(0);
                channel7.setDeletedUser(0);
                session.save(channel7);

                Channel channel8 = new Channel();
                channel8.setFullCode("MN_HCM01_GS01_01");
                channel8.setCode("01");
                channel8.setCompanys(company);
                channel8.setParents(channel4);
                channel8.setChannelTypes(channelType4);
                channel8.setIsSalePoint(0);
                channel8.setName("NPP_01");
                channel8.setStatusId(10);
                channel8.setLat(BigDecimal.ZERO);
                channel8.setLng(BigDecimal.ZERO);
                channel8.setCreatedAt(new Date());
                channel8.setCreatedUser(createdUser);
                channel8.setUpdatedUser(0);
                channel8.setDeletedUser(0);
                session.save(channel8);

                Channel channel9 = new Channel();
                channel9.setFullCode("MN_HCM01_GS01_02");
                channel9.setCode("02");
                channel9.setCompanys(company);
                channel9.setParents(channel4);
                channel9.setChannelTypes(channelType4);
                channel9.setIsSalePoint(0);
                channel9.setName("NPP_02");
                channel9.setStatusId(10);
                channel9.setLat(BigDecimal.ZERO);
                channel9.setLng(BigDecimal.ZERO);
                channel9.setCreatedAt(new Date());
                channel9.setCreatedUser(createdUser);
                channel9.setUpdatedUser(0);
                channel9.setDeletedUser(0);
                session.save(channel9);

                Channel channel10 = new Channel();
                channel10.setFullCode("MN_HCM02_02_NPP01");
                channel10.setCode("NPP01");
                channel10.setCompanys(company);
                channel10.setParents(channel7);
                channel10.setChannelTypes(channelType4);
                channel10.setIsSalePoint(0);
                channel10.setName("NPP01");
                channel10.setStatusId(10);
                channel10.setLat(BigDecimal.ZERO);
                channel10.setLng(BigDecimal.ZERO);
                channel10.setCreatedAt(new Date());
                channel10.setCreatedUser(createdUser);
                channel10.setUpdatedUser(0);
                channel10.setDeletedUser(0);
                session.save(channel10);

                Channel channel11 = new Channel();
                channel11.setFullCode("MN_HCM02_02_NPP02");
                channel11.setCode("NPP02");
                channel11.setCompanys(company);
                channel11.setParents(channel7);
                channel11.setChannelTypes(channelType4);
                channel11.setIsSalePoint(0);
                channel11.setName("NPP02");
                channel11.setStatusId(10);
                channel11.setLat(BigDecimal.ZERO);
                channel11.setLng(BigDecimal.ZERO);
                channel11.setCreatedAt(new Date());
                channel11.setCreatedUser(createdUser);
                channel11.setUpdatedUser(0);
                channel11.setDeletedUser(0);
                session.save(channel11);

                //create ChannelLocation
                int[] locations2 = {91, 76, 75, 74, 90, 80, 89, 79};
                List<ChannelLocation> channelLocations2 = createChannelLocation(channel2, locations2, createdUser);
                int[] locations3 = {68, 71, 72, 73, 80, 87, 86};
                List<ChannelLocation> channelLocations3 = createChannelLocation(channel3, locations3, createdUser);
                int[] locations4 = {79, 90};
                List<ChannelLocation> channelLocations4 = createChannelLocation(channel4, locations4, createdUser);
                int[] locations5 = {79, 90};
                List<ChannelLocation> channelLocations5 = createChannelLocation(channel5, locations5, createdUser);
                int[] locations6 = {68, 71};
                List<ChannelLocation> channelLocations6 = createChannelLocation(channel6, locations6, createdUser);
                int[] locations7 = {70};
                List<ChannelLocation> channelLocations7 = createChannelLocation(channel8, locations7, createdUser);
                int[] locations8 = {90};
                List<ChannelLocation> channelLocations8 = createChannelLocation(channel9, locations8, createdUser);
                int[] locations9 = {86, 87};
                List<ChannelLocation> channelLocations9 = createChannelLocation(channel7, locations9, createdUser);
                int[] locations10 = {87};
                List<ChannelLocation> channelLocations10 = createChannelLocation(channel10, locations10, createdUser);
                int[] locations11 = {86};
                List<ChannelLocation> channelLocations11 = createChannelLocation(channel11, locations11, createdUser);

                for (ChannelLocation channelLocation : channelLocations2) {
                    session.save(channelLocation);
                }
                for (ChannelLocation channelLocation : channelLocations3) {
                    session.save(channelLocation);
                }
                for (ChannelLocation channelLocation : channelLocations4) {
                    session.save(channelLocation);
                }
                for (ChannelLocation channelLocation : channelLocations5) {
                    session.save(channelLocation);
                }
                for (ChannelLocation channelLocation : channelLocations6) {
                    session.save(channelLocation);
                }
                for (ChannelLocation channelLocation : channelLocations7) {
                    session.save(channelLocation);
                }
                for (ChannelLocation channelLocation : channelLocations8) {
                    session.save(channelLocation);
                }
                for (ChannelLocation channelLocation : channelLocations9) {
                    session.save(channelLocation);
                }
                for (ChannelLocation channelLocation : channelLocations10) {
                    session.save(channelLocation);
                }
                for (ChannelLocation channelLocation : channelLocations11) {
                    session.save(channelLocation);
                }

                //add channel to channelList
                channelList.add(channel2);
                channelList.add(channel3);
                channelList.add(channel4);
                channelList.add(channel5);
                channelList.add(channel6);
                channelList.add(channel7);
                channelList.add(channel8);
                channelList.add(channel9);
                channelList.add(channel10);
                channelList.add(channel11);

                //create users                
                User saleman1 = createSalesman(1, createdUser, company);
                User saleman2 = createSalesman(2, createdUser, company);
                User saleman3 = createSalesman(3, createdUser, company);
                User saleman4 = createSalesman(4, createdUser, company);
                User saleman5 = createSalesman(5, createdUser, company);
                User saleman6 = createSalesman(6, createdUser, company);
                User saleman7 = createSalesman(7, createdUser, company);
                User saleman8 = createSalesman(8, createdUser, company);
                User saleman9 = createSalesman(9, createdUser, company);
                User saleman10 = createSalesman(10, createdUser, company);
                session.save(saleman1);
                session.save(saleman2);
                session.save(saleman3);
                session.save(saleman4);
                session.save(saleman5);
                session.save(saleman6);
                session.save(saleman7);
                session.save(saleman8);
                session.save(saleman9);
                session.save(saleman10);

                User salesup1 = new User();
                salesup1.setUsername("sales_sup01" + "@" + company.getCode());
                salesup1.setCompanys(company);
                salesup1.setUserCode("sales_sup01" + "@" + company.getCode());
                salesup1.setPassword(MsalesSession.getSHA256(company.getPassword()));
                salesup1.setLastName("SalesSup");
                salesup1.setFirstName("1");
                salesup1.setLocationId(4);
                salesup1.setIsActive(1);
                salesup1.setStatusId(6);
                salesup1.setCreatedUser(createdUser);
                salesup1.setCreatedAt(new Date());
                salesup1.setUpdatedUser(0);
                salesup1.setDeletedUser(0);
                session.save(salesup1);

                User salesup2 = new User();
                salesup2.setUsername("sales_sup02" + "@" + company.getCode());
                salesup2.setCompanys(company);
                salesup2.setUserCode("sales_sup02" + "@" + company.getCode());
                salesup2.setPassword(MsalesSession.getSHA256(company.getPassword()));
                salesup2.setLastName("SalesSup");
                salesup2.setFirstName("2");
                salesup2.setLocationId(4);
                salesup2.setIsActive(1);
                salesup2.setStatusId(6);
                salesup2.setCreatedUser(createdUser);
                salesup2.setCreatedAt(new Date());
                salesup2.setUpdatedUser(0);
                salesup2.setDeletedUser(0);
                session.save(salesup2);

                //add all user to userList
                userList.add(saleman1);
                userList.add(saleman2);
                userList.add(saleman3);
                userList.add(saleman4);
                userList.add(saleman5);
                userList.add(saleman6);
                userList.add(saleman7);
                userList.add(saleman8);
                userList.add(saleman9);
                userList.add(saleman10);
                userList.add(salesup1);
                userList.add(salesup2);

                UserRoleChannel userRoleChannel2 = createUserRoleChannel(saleman1, 6, channel8, createdUser);
                UserRoleChannel userRoleChannel3 = createUserRoleChannel(saleman2, 6, channel8, createdUser);
                UserRoleChannel userRoleChannel4 = createUserRoleChannel(saleman3, 6, channel8, createdUser);
                UserRoleChannel userRoleChannel5 = createUserRoleChannel(saleman4, 6, channel8, createdUser);
                UserRoleChannel userRoleChannel6 = createUserRoleChannel(saleman5, 6, channel8, createdUser);
                UserRoleChannel userRoleChannel7 = createUserRoleChannel(saleman6, 6, channel10, createdUser);
                UserRoleChannel userRoleChannel8 = createUserRoleChannel(saleman7, 6, channel10, createdUser);
                UserRoleChannel userRoleChannel9 = createUserRoleChannel(saleman8, 6, channel10, createdUser);
                UserRoleChannel userRoleChannel10 = createUserRoleChannel(saleman9, 6, channel10, createdUser);
                UserRoleChannel userRoleChannel11 = createUserRoleChannel(saleman10, 6, channel10, createdUser);
                UserRoleChannel userRoleChannel12 = createUserRoleChannel(salesup1, 4, channel4, createdUser);
                UserRoleChannel userRoleChannel13 = createUserRoleChannel(salesup2, 4, channel7, createdUser);

                session.save(userRoleChannel2);
                session.save(userRoleChannel3);
                session.save(userRoleChannel4);
                session.save(userRoleChannel5);
                session.save(userRoleChannel6);
                session.save(userRoleChannel7);
                session.save(userRoleChannel8);
                session.save(userRoleChannel9);
                session.save(userRoleChannel10);
                session.save(userRoleChannel11);
                session.save(userRoleChannel12);
                session.save(userRoleChannel13);

//                //create POS
//                POS pos1 = createPOS(channel7, "HCM_TBI_0002_0001", "Tạp hóa 01", "19 Đống Đa", 1050, 10.8094583, 106.6633143, createdUser);
//                POS pos2 = createPOS(channel7, "HCM_TBI_0008_0001", "Tạp hóa 02", "373/98 lý thường kiêt", 1052, 10.7793401, 106.6534251, createdUser);
//                POS pos3 = createPOS(channel7, "HCM_TBI_0002_0002", "Tạp hóa 03", "102 Pho Quang", 1050, 10.8078413, 106.6669053, createdUser);
//                POS pos4 = createPOS(channel7, "HCM_TBI_0002_0003", "Tạp hóa 04", "1 bạch đằng", 1050, 10.8144103, 106.6681303, createdUser);
//                POS pos5 = createPOS(channel7, "HCM_TBI_0002_0004", "Tạp hóa 05", "367 Nguyen Trong Tuyen", 1050, 10.7985522, 106.6621403, createdUser);
//                POS pos6 = createPOS(channel10, "HCM_TBI_0002_0005", "Tạp hóa 06", "25 cuu long p2", 1050, 10.8102483, 106.6643022, createdUser);
//                POS pos7 = createPOS(channel10, "HCM_TBI_0014_0001", "Tạp hóa 07", "50 nguyễn hồng đào", 1053, 10.7922692, 106.6395471, createdUser);
//                POS pos8 = createPOS(channel10, "HCM_TBI_0014_0002", "Tạp hóa 08", "371/17b trường chinh", 1053, 10.7981383, 106.6410983, createdUser);
//                POS pos9 = createPOS(channel10, "HCM_TBI_0002_0006", "Tạp hóa 09", "52 Nguyen Thanh Tuyen", 1050, 10.7976783, 106.6588603, createdUser);
//                POS pos10 = createPOS(channel10, "HCM_TBI_0002_0007", "Tạp hóa 10", "128 Bui thi xuan", 1050, 10.7962693, 106.6614053, createdUser);
//                session.save(pos1);
//                session.save(pos2);
//                session.save(pos3);
//                session.save(pos4);
//                session.save(pos5);
//                session.save(pos6);
//                session.save(pos7);
//                session.save(pos8);
//                session.save(pos9);
//                session.save(pos10);
                //add all pos to posList
//                posList.add(pos1);
//                posList.add(pos2);
//                posList.add(pos3);
//                posList.add(pos4);
//                posList.add(pos5);
//                posList.add(pos6);
//                posList.add(pos7);
//                posList.add(pos8);
//                posList.add(pos9);
//                posList.add(pos10);
            }

            channelList.add(channel1);
            userList.add(user1);
            List<SalesStock> salesStockList = createStock(userList, createdUser);
            //salesStockList.addAll(createPOSStock(posList, createdUser));
            salesStockList.addAll(createChannelStock(channelList, createdUser));
            for (SalesStock salesStock : salesStockList) {
                session.save(salesStock);
            }

            UserRoleChannel userRoleChannel1 = createUserRoleChannel(user1, 1, channel1, createdUser);
            session.save(userRoleChannel1);

            //update user1
            user1.setUpdatedUser(createdUser);
            user1.setCreatedUser(createdUser);
            user1.setUpdatedAt(new Date());
            session.update(user1);
            //update company
            company.setCreatedUser(createdUser);
            company.setUpdatedAt(new Date());
            company.setUpdatedUser(createdUser);
            session.update(company);

            //create workflow
            if (company.isIsTemplate()) {
                CompanyConfig companyConfig1 = createCompanyConfig(company, 6, createdUser, "sales_page");
                CompanyConfig companyConfig2 = createCompanyConfig(company, 6, createdUser, "main_page");
                CompanyConfig companyConfig3 = createCompanyConfig(company, 4, createdUser, "main_page");
                CompanyConfig companyConfig4 = createCompanyConfig(company, 4, createdUser, "sup_sales_page");

                session.save(companyConfig1);
                session.save(companyConfig2);
                session.save(companyConfig3);
                session.save(companyConfig4);

                //salesman sales_page
                CompanyConfigDetails companyConfigDetails1 = createCompanyConfigDetails(companyConfig1, 6, "care_page", "CHĂM SÓC", 0, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails1.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails1);
                }
                CompanyConfigDetails companyConfigDetails2 = createCompanyConfigDetails(companyConfig1, 6, "direct_sales_page", "BÁN TRỰC TIẾP", 1, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails2.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails2);
                }
                CompanyConfigDetails companyConfigDetails3 = createCompanyConfigDetails(companyConfig1, 6, "order_page", "ĐẶT HÀNG", 2, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails3.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails3);
                }
                CompanyConfigDetails companyConfigDetails4 = createCompanyConfigDetails(companyConfig1, 6, "delivery_page", "GIAO HÀNG", 3, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails4.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails4);
                }
                CompanyConfigDetails companyConfigDetails5 = createCompanyConfigDetails(companyConfig1, 6, "recover_page", "THU HỒI", 4, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails5.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails5);
                }
                CompanyConfigDetails companyConfigDetails6 = createCompanyConfigDetails(companyConfig1, 6, "sales_via_phone_page", "BÁN HÀNG QUA ĐIỆN THOẠI", 5, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails6.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails6);
                }
                CompanyConfigDetails companyConfigDetails27 = createCompanyConfigDetails(companyConfig1, 6, "list_of_promotion", "DANH SÁCH CTKM", 6, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails27.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails27);
                }
                CompanyConfigDetails companyConfigDetails26 = createCompanyConfigDetails(companyConfig1, 6, "register_promotion", "ĐĂNG KÝ CTKM", 7, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails26.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails26);
                }
                CompanyConfigDetails companyConfigDetails28 = createCompanyConfigDetails(companyConfig1, 6, "delievery_promotion", "GIAO KHUYẾN MÃI", 8, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails28.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails28);
                }
                CompanyConfigDetails companyConfigDetails29 = createCompanyConfigDetails(companyConfig1, 6, "list_of_report", "CÁC DANH SÁCH KM", 9, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails29.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails29);
                }

                //saleman main_page
                CompanyConfigDetails companyConfigDetails7 = createCompanyConfigDetails(companyConfig2, 6, "mcp_page", "TUYẾN ĐƯỜNG", 0, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails7.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails7);
                }

                CompanyConfigDetails companyConfigDetails8 = createCompanyConfigDetails(companyConfig2, 6, "plan_page", "CHỈ TIÊU", 1, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails8.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails8);
                }

                CompanyConfigDetails companyConfigDetails9 = createCompanyConfigDetails(companyConfig2, 6, "store_page", "NHẬP HÀNG", 2, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails9.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails9);
                }

                CompanyConfigDetails companyConfigDetails10 = createCompanyConfigDetails(companyConfig2, 6, "sales_page", "BÁN HÀNG", 3, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails10.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails10);
                }

                CompanyConfigDetails companyConfigDetails11 = createCompanyConfigDetails(companyConfig2, 6, "point_of_sale_page", "PT ĐIỂM MỚI", 4, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails11.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails11);
                }

                CompanyConfigDetails companyConfigDetails12 = createCompanyConfigDetails(companyConfig2, 6, "history_page", "NHẬT KÝ", 5, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails12.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails12);
                }

                //salessup main_page
                CompanyConfigDetails companyConfigDetails13 = createCompanyConfigDetails(companyConfig3, 4, "monitor_evaluation_page", "GS ĐÁNH GIÁ", 0, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails13.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails13);
                }

                CompanyConfigDetails companyConfigDetails14 = createCompanyConfigDetails(companyConfig3, 4, "monitor_sales_page", "GS BÁN HÀNG", 1, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails14.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails14);
                }

                CompanyConfigDetails companyConfigDetails15 = createCompanyConfigDetails(companyConfig3, 4, "sup_sales_page", "BÁN HÀNG", 2, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails15.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails15);
                }

                CompanyConfigDetails companyConfigDetails16 = createCompanyConfigDetails(companyConfig3, 4, "monitor_order_page", "GS ĐƠN HÀNG", 3, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails16.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails16);
                }

                CompanyConfigDetails companyConfigDetails17 = createCompanyConfigDetails(companyConfig3, 4, "monitor_route_page", "GS LỘ TRÌNH", 4, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails17.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails17);
                }

                CompanyConfigDetails companyConfigDetails18 = createCompanyConfigDetails(companyConfig3, 4, "monitor_store_page", "ĐIỂM BÁN HÀNG", 5, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails18.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails18);
                }

                //salessup sales_page
                CompanyConfigDetails companyConfigDetails19 = createCompanyConfigDetails(companyConfig4, 4, "care_page", "CHĂM SÓC", 0, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails19.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails19);
                }

                CompanyConfigDetails companyConfigDetails20 = createCompanyConfigDetails(companyConfig4, 4, "direct_sales_page", "BÁN TRỰC TIẾP", 1, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails20.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails20);
                }

                CompanyConfigDetails companyConfigDetails21 = createCompanyConfigDetails(companyConfig4, 4, "order_page", "ĐẶT HÀNG", 2, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails21.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails21);
                }

                CompanyConfigDetails companyConfigDetails22 = createCompanyConfigDetails(companyConfig4, 4, "delivery_page", "GIAO HÀNG", 3, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails22.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails22);
                }

                CompanyConfigDetails companyConfigDetails23 = createCompanyConfigDetails(companyConfig4, 4, "store_page", "NHẬP HÀNG", 4, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails23.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails23);
                }

                CompanyConfigDetails companyConfigDetails24 = createCompanyConfigDetails(companyConfig4, 4, "withdraw_page", "THU HỒI", 5, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails24.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails24);
                }

                CompanyConfigDetails companyConfigDetails25 = createCompanyConfigDetails(companyConfig4, 4, "sales_via_phone_page", "BÁN HÀNG QUA ĐIỆN THOẠI", 7, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails25.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails25);
                }

                CompanyConfigDetails companyConfigDetails30 = createCompanyConfigDetails(companyConfig4, 4, "point_of_sale_page", "PT ĐIỂM MỚI", 6, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails30.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails30);
                }

                CompanyConfigDetails companyConfigDetails31 = createCompanyConfigDetails(companyConfig4, 4, "list_of_promotion", "DANH SÁCH CTKM", 8, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails31.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails31);
                }

                CompanyConfigDetails companyConfigDetails32 = createCompanyConfigDetails(companyConfig4, 4, "register_promotion", "ĐĂNG KÝ CTKM", 9, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails32.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails32);
                }

                CompanyConfigDetails companyConfigDetails33 = createCompanyConfigDetails(companyConfig4, 4, "delievery_promotion", "GIAO KHUYẾN MÃI", 10, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails33.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails33);
                }

                CompanyConfigDetails companyConfigDetails34 = createCompanyConfigDetails(companyConfig4, 4, "list_of_report", "CÁC DANH SÁCH KM", 11, createdUser);
                if (workflowService.checkImportWorkflowByPackage(companyConfigDetails34.getCode(), company.getPackageService())) {
                    session.save(companyConfigDetails34);
                }
            }

            //create company constrant
            CompanyConstant companyConstant = new CompanyConstant();
            companyConstant.setCompanys(company);
            companyConstant.setPeriodGetPosition(10000);
            companyConstant.setPeriodSendPosition(2);
            companyConstant.setDistance(20);
            companyConstant.setMinDistance(500);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            companyConstant.setTimesheetFrom(new Time(calendar.getTimeInMillis()));

            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            companyConstant.setMorningFrom(new Time(calendar.getTimeInMillis()));

            calendar.set(Calendar.HOUR_OF_DAY, 11);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 0);
            companyConstant.setMorningTo(new Time(calendar.getTimeInMillis()));

            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 0);
            companyConstant.setEveningFrom(new Time(calendar.getTimeInMillis()));

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 0);
            companyConstant.setEveningTo(new Time(calendar.getTimeInMillis()));
            companyConstant.setCreatedUser(createdUser);
            companyConstant.setCreatedAt(new Date());
            companyConstant.setUpdatedUser(0);
            companyConstant.setDeletedUser(0);
            session.save(companyConstant);

//            //create unit
//            Unit unit1 = new Unit();
//            unit1.setCode("H");
//            unit1.setName("Hộp");
//            unit1.setCompanys(company);
//            unit1.setOrder(1);
//            unit1.setCreatedAt(new Date());
//            unit1.setCreatedUser(createdUser);
//            unit1.setUpdatedUser(0);
//            unit1.setDeletedUser(0);
//
//            Unit unit2 = new Unit();
//            unit2.setCode("T");
//            unit2.setName("Thùng");
//            unit2.setCompanys(company);
//            unit2.setOrder(1);
//            unit2.setCreatedAt(new Date());
//            unit2.setCreatedUser(createdUser);
//            unit2.setUpdatedUser(0);
//            unit2.setDeletedUser(0);
//
//            Unit unit3 = new Unit();
//            unit3.setCode("G");
//            unit3.setName("Gói");
//            unit3.setCompanys(company);
//            unit3.setOrder(1);
//            unit3.setCreatedAt(new Date());
//            unit3.setCreatedUser(createdUser);
//            unit3.setUpdatedUser(0);
//            unit3.setDeletedUser(0);
//
//            Unit unit4 = new Unit();
//            unit4.setCode("T1");
//            unit4.setName("Túi");
//            unit4.setCompanys(company);
//            unit4.setOrder(1);
//            unit4.setCreatedAt(new Date());
//            unit4.setCreatedUser(createdUser);
//            unit4.setUpdatedUser(0);
//            unit4.setDeletedUser(0);
//
//            Unit unit5 = new Unit();
//            unit5.setCode("L");
//            unit5.setName("Lô");
//            unit5.setCompanys(company);
//            unit5.setOrder(1);
//            unit5.setCreatedAt(new Date());
//            unit5.setCreatedUser(createdUser);
//            unit5.setUpdatedUser(0);
//            unit5.setDeletedUser(0);
//
//            Unit unit6 = new Unit();
//            unit6.setCode("C");
//            unit6.setName("Cái");
//            unit6.setCompanys(company);
//            unit6.setOrder(1);
//            unit6.setCreatedAt(new Date());
//            unit6.setCreatedUser(createdUser);
//            unit6.setUpdatedUser(0);
//            unit6.setDeletedUser(0);
//
//            Unit unit7 = new Unit();
//            unit7.setCode("K");
//            unit7.setName("Khay");
//            unit7.setCompanys(company);
//            unit7.setOrder(1);
//            unit7.setCreatedAt(new Date());
//            unit7.setCreatedUser(createdUser);
//            unit7.setUpdatedUser(0);
//            unit7.setDeletedUser(0);
//
//            Unit unit8 = new Unit();
//            unit8.setCode("L");
//            unit8.setName("Lon");
//            unit8.setCompanys(company);
//            unit8.setOrder(1);
//            unit8.setCreatedAt(new Date());
//            unit8.setCreatedUser(createdUser);
//            unit8.setUpdatedUser(0);
//            unit8.setDeletedUser(0);
//
//            Unit unit9 = new Unit();
//            unit9.setCode("V");
//            unit9.setName("Vĩ");
//            unit9.setCompanys(company);
//            unit9.setOrder(1);
//            unit9.setCreatedAt(new Date());
//            unit9.setCreatedUser(createdUser);
//            unit9.setUpdatedUser(0);
//            unit9.setDeletedUser(0);
//
//            Unit unit10 = new Unit();
//            unit10.setCode("Tree");
//            unit10.setName("Cây");
//            unit10.setCompanys(company);
//            unit10.setOrder(1);
//            unit10.setCreatedAt(new Date());
//            unit10.setCreatedUser(createdUser);
//            unit10.setUpdatedUser(0);
//            unit10.setDeletedUser(0);
//            session.save(unit1);
//            session.save(unit2);
//            session.save(unit3);
//            session.save(unit4);
//            session.save(unit5);
//            session.save(unit6);
//            session.save(unit7);
//            session.save(unit8);
//            session.save(unit9);
//            session.save(unit10);
//
//            // create goodscategory
//            GoodsCategory goodsCategory1 = new GoodsCategory();
//            goodsCategory1.setGoodsCode("ST");
//            goodsCategory1.setName("Sữa");
//            goodsCategory1.setStatusId(15);
//            goodsCategory1.setCompanys(company);
//            goodsCategory1.setCreatedAt(new Date());
//            goodsCategory1.setCreatedUser(createdUser);
//            goodsCategory1.setUpdatedUser(0);
//            goodsCategory1.setDeletedUser(0);
//
//            GoodsCategory goodsCategory2 = new GoodsCategory();
//            goodsCategory2.setGoodsCode("BN");
//            goodsCategory2.setName("Bánh Ngọt");
//            goodsCategory2.setStatusId(15);
//            goodsCategory2.setCompanys(company);
//            goodsCategory2.setCreatedAt(new Date());
//            goodsCategory2.setCreatedUser(createdUser);
//            goodsCategory2.setUpdatedUser(0);
//            goodsCategory2.setDeletedUser(0);
//
//            GoodsCategory goodsCategory3 = new GoodsCategory();
//            goodsCategory3.setGoodsCode("Ca");
//            goodsCategory3.setName("Kẹo");
//            goodsCategory3.setStatusId(15);
//            goodsCategory3.setCompanys(company);
//            goodsCategory3.setCreatedAt(new Date());
//            goodsCategory3.setCreatedUser(createdUser);
//            goodsCategory3.setUpdatedUser(0);
//            goodsCategory3.setDeletedUser(0);
//
//            GoodsCategory goodsCategory4 = new GoodsCategory();
//            goodsCategory4.setGoodsCode("TP");
//            goodsCategory4.setName("Thực phẩm");
//            goodsCategory4.setStatusId(15);
//            goodsCategory4.setCompanys(company);
//            goodsCategory4.setCreatedAt(new Date());
//            goodsCategory4.setCreatedUser(createdUser);
//            goodsCategory4.setUpdatedUser(0);
//            goodsCategory4.setDeletedUser(0);
//
//            GoodsCategory goodsCategory5 = new GoodsCategory();
//            goodsCategory5.setGoodsCode("TU");
//            goodsCategory5.setName("Đồ uống");
//            goodsCategory5.setStatusId(15);
//            goodsCategory5.setCompanys(company);
//            goodsCategory5.setCreatedAt(new Date());
//            goodsCategory5.setCreatedUser(createdUser);
//            goodsCategory5.setUpdatedUser(0);
//            goodsCategory5.setDeletedUser(0);
//
//            GoodsCategory goodsCategory6 = new GoodsCategory();
//            goodsCategory6.setGoodsCode("DH");
//            goodsCategory6.setName("Thực phẩm");
//            goodsCategory6.setStatusId(15);
//            goodsCategory6.setCompanys(company);
//            goodsCategory6.setCreatedAt(new Date());
//            goodsCategory6.setCreatedUser(createdUser);
//            goodsCategory6.setUpdatedUser(0);
//            goodsCategory6.setDeletedUser(0);
//
//            session.save(goodsCategory1);
//            session.save(goodsCategory2);
//            session.save(goodsCategory3);
//            session.save(goodsCategory4);
//            session.save(goodsCategory5);
//            session.save(goodsCategory6);
//
//            // create Goods
//            Goods goods1 = new Goods();
//            goods1.setGoodsCode("Choco240");
//            goods1.setName("Choco PN 240");
//            goods1.setGoodsCategorys(goodsCategory2);
//            goods1.setPrice(23000);
//            goods1.setStatusId(15);
//            goods1.setIsFocus(false);
//            goods1.setIsRecover(false);
//            goods1.setFactor(0);
//            goods1.setCreatedAt(new Date());
//            goods1.setCreatedUser(createdUser);
//            goods1.setUpdatedUser(0);
//            goods1.setDeletedUser(0);
//
//            Goods goods2 = new Goods();
//            goods2.setGoodsCode("55020075");
//            goods2.setName("Solo dứa khay 252g");
//            goods2.setGoodsCategorys(goodsCategory2);
//            goods2.setPrice(26000);
//            goods2.setStatusId(15);
//            goods2.setIsFocus(false);
//            goods2.setIsRecover(false);
//            goods2.setFactor(0);
//            goods2.setCreatedAt(new Date());
//            goods2.setCreatedUser(createdUser);
//            goods2.setUpdatedUser(0);
//            goods2.setDeletedUser(0);
//
//            Goods goods3 = new Goods();
//            goods3.setGoodsCode("55020076");
//            goods3.setName("Solo dứa hộp 192g");
//            goods3.setGoodsCategorys(goodsCategory2);
//            goods3.setPrice(20000);
//            goods3.setStatusId(15);
//            goods3.setIsFocus(false);
//            goods3.setIsRecover(false);
//            goods3.setFactor(0);
//            goods3.setCreatedAt(new Date());
//            goods3.setCreatedUser(createdUser);
//            goods3.setUpdatedUser(0);
//            goods3.setDeletedUser(0);
//
//            Goods goods4 = new Goods();
//            goods4.setGoodsCode("55070048");
//            goods4.setName("Kẹo Popit Ú Dây Doraemon 360g");
//            goods4.setGoodsCategorys(goodsCategory3);
//            goods4.setPrice(45000);
//            goods4.setStatusId(15);
//            goods4.setIsFocus(false);
//            goods4.setIsRecover(false);
//            goods4.setFactor(0);
//            goods4.setCreatedAt(new Date());
//            goods4.setCreatedUser(createdUser);
//            goods4.setUpdatedUser(0);
//            goods4.setDeletedUser(0);
//
//            Goods goods5 = new Goods();
//            goods5.setGoodsCode("55070029");
//            goods5.setName("Kẹo Popit Dây Doraemon 300g");
//            goods5.setGoodsCategorys(goodsCategory3);
//            goods5.setPrice(30000);
//            goods5.setStatusId(15);
//            goods5.setIsFocus(false);
//            goods5.setIsRecover(false);
//            goods5.setFactor(0);
//            goods5.setCreatedAt(new Date());
//            goods5.setCreatedUser(createdUser);
//            goods5.setUpdatedUser(0);
//            goods5.setDeletedUser(0);
//
//            Goods goods6 = new Goods();
//            goods6.setGoodsCode("55010127");
//            goods6.setName("Bon Choco 130g");
//            goods6.setGoodsCategorys(goodsCategory2);
//            goods6.setPrice(15000);
//            goods6.setStatusId(15);
//            goods6.setIsFocus(false);
//            goods6.setIsRecover(false);
//            goods6.setFactor(0);
//            goods6.setCreatedAt(new Date());
//            goods6.setCreatedUser(createdUser);
//            goods6.setUpdatedUser(0);
//            goods6.setDeletedUser(0);
//
//            Goods goods7 = new Goods();
//            goods7.setGoodsCode("070074642437");
//            goods7.setName("Sữa Similac Advance Non GMO (658g)");
//            goods7.setGoodsCategorys(goodsCategory1);
//            goods7.setPrice(550000);
//            goods7.setStatusId(15);
//            goods7.setIsFocus(false);
//            goods7.setIsRecover(false);
//            goods7.setFactor(0);
//            goods7.setCreatedAt(new Date());
//            goods7.setCreatedUser(createdUser);
//            goods7.setUpdatedUser(0);
//            goods7.setDeletedUser(0);
//
//            Goods goods8 = new Goods();
//            goods8.setGoodsCode("7310100680648");
//            goods8.setName("Sữa ngũ cốc Semper (18m+) (725g)");
//            goods8.setGoodsCategorys(goodsCategory1);
//            goods8.setPrice(460000);
//            goods8.setStatusId(15);
//            goods8.setIsFocus(false);
//            goods8.setIsRecover(false);
//            goods8.setFactor(0);
//            goods8.setCreatedAt(new Date());
//            goods8.setCreatedUser(createdUser);
//            goods8.setUpdatedUser(0);
//            goods8.setDeletedUser(0);
//
//            Goods goods9 = new Goods();
//            goods9.setGoodsCode("4987386091210");
//            goods9.setName("Sữa Icreo Glico số 9 (9-36m) (820g)");
//            goods9.setGoodsCategorys(goodsCategory1);
//            goods9.setPrice(435000);
//            goods9.setStatusId(15);
//            goods9.setIsFocus(false);
//            goods9.setIsRecover(false);
//            goods9.setFactor(0);
//            goods9.setCreatedAt(new Date());
//            goods9.setCreatedUser(createdUser);
//            goods9.setUpdatedUser(0);
//            goods9.setDeletedUser(0);
//
//            Goods goods10 = new Goods();
//            goods10.setGoodsCode("3041090012716");
//            goods10.setName("Vỉ 4h sữa nước Bledina (choco vani) (250ml*4) (12m+)");
//            goods10.setGoodsCategorys(goodsCategory1);
//            goods10.setPrice(200000);
//            goods10.setStatusId(15);
//            goods10.setIsFocus(false);
//            goods10.setIsRecover(false);
//            goods10.setFactor(0);
//            goods10.setCreatedAt(new Date());
//            goods10.setCreatedUser(createdUser);
//            goods10.setUpdatedUser(0);
//            goods10.setDeletedUser(0);
//
//            Goods goods11 = new Goods();
//            goods11.setGoodsCode("MS011");
//            goods11.setName("Mít sấy 300g");
//            goods11.setGoodsCategorys(goodsCategory4);
//            goods11.setPrice(54000);
//            goods11.setStatusId(15);
//            goods11.setIsFocus(false);
//            goods11.setIsRecover(false);
//            goods11.setFactor(0);
//            goods11.setCreatedAt(new Date());
//            goods11.setCreatedUser(createdUser);
//            goods11.setUpdatedUser(0);
//            goods11.setDeletedUser(0);
//
//            Goods goods12 = new Goods();
//            goods12.setGoodsCode("KL001");
//            goods12.setName("Khoai lang dẻo");
//            goods12.setGoodsCategorys(goodsCategory4);
//            goods12.setPrice(36000);
//            goods12.setStatusId(15);
//            goods12.setIsFocus(false);
//            goods12.setIsRecover(false);
//            goods12.setFactor(0);
//            goods12.setCreatedAt(new Date());
//            goods12.setCreatedUser(createdUser);
//            goods12.setUpdatedUser(0);
//            goods12.setDeletedUser(0);
//
//            Goods goods13 = new Goods();
//            goods13.setGoodsCode("Blueberries");
//            goods13.setName("Trái Việt Quốc sấy khô - Blueberries - Kirkland - tck007");
//            goods13.setGoodsCategorys(goodsCategory4);
//            goods13.setPrice(310000);
//            goods13.setStatusId(15);
//            goods13.setIsFocus(false);
//            goods13.setIsRecover(false);
//            goods13.setFactor(0);
//            goods13.setCreatedAt(new Date());
//            goods13.setCreatedUser(createdUser);
//            goods13.setUpdatedUser(0);
//            goods13.setDeletedUser(0);
//
//            Goods goods14 = new Goods();
//            goods14.setGoodsCode("T315");
//            goods14.setName("Bột Kem Pha Cà Phê Nestle Coffee Mate Vị Chocolate 425gr");
//            goods14.setGoodsCategorys(goodsCategory5);
//            goods14.setPrice(180000);
//            goods14.setStatusId(15);
//            goods14.setIsFocus(false);
//            goods14.setIsRecover(false);
//            goods14.setFactor(0);
//            goods14.setCreatedAt(new Date());
//            goods14.setCreatedUser(createdUser);
//            goods14.setUpdatedUser(0);
//            goods14.setDeletedUser(0);
//
//            Goods goods15 = new Goods();
//            goods15.setGoodsCode("nu001");
//            goods15.setName("Cà Phê Starbucks Frappuccino của Mỹ pha sẵn vị Vanilla");
//            goods15.setGoodsCategorys(goodsCategory5);
//            goods15.setPrice(70000);
//            goods15.setStatusId(15);
//            goods15.setIsFocus(false);
//            goods15.setIsRecover(false);
//            goods15.setFactor(0);
//            goods15.setCreatedAt(new Date());
//            goods15.setCreatedUser(createdUser);
//            goods15.setUpdatedUser(0);
//            goods15.setDeletedUser(0);
//
//            Goods goods16 = new Goods();
//            goods16.setGoodsCode("lipton");
//            goods16.setName("Bột trà chanh Lipton Mỹ");
//            goods16.setGoodsCategorys(goodsCategory5);
//            goods16.setPrice(125000);
//            goods16.setStatusId(15);
//            goods16.setIsFocus(false);
//            goods16.setIsRecover(false);
//            goods16.setFactor(0);
//            goods16.setCreatedAt(new Date());
//            goods16.setCreatedUser(createdUser);
//            goods16.setUpdatedUser(0);
//            goods16.setDeletedUser(0);
//
//            Goods goods17 = new Goods();
//            goods17.setGoodsCode("T293");
//            goods17.setName("Bánh Ritz Phô Mai Hộp 8 Gói 304g");
//            goods17.setGoodsCategorys(goodsCategory2);
//            goods17.setPrice(130000);
//            goods17.setStatusId(15);
//            goods17.setIsFocus(false);
//            goods17.setIsRecover(false);
//            goods17.setFactor(0);
//            goods17.setCreatedAt(new Date());
//            goods17.setCreatedUser(createdUser);
//            goods17.setUpdatedUser(0);
//            goods17.setDeletedUser(0);
//
//            Goods goods18 = new Goods();
//            goods18.setGoodsCode("T296");
//            goods18.setName("Bánh Khoai Tây Chip Star Vị Phô Mai");
//            goods18.setGoodsCategorys(goodsCategory2);
//            goods18.setPrice(35000);
//            goods18.setStatusId(15);
//            goods18.setIsFocus(false);
//            goods18.setIsRecover(false);
//            goods18.setFactor(0);
//            goods18.setCreatedAt(new Date());
//            goods18.setCreatedUser(createdUser);
//            goods18.setUpdatedUser(0);
//            goods18.setDeletedUser(0);
//            session.save(goods1);
//            session.save(goods2);
//            session.save(goods3);
//            session.save(goods4);
//            session.save(goods5);
//            session.save(goods6);
//            session.save(goods7);
//            session.save(goods8);
//            session.save(goods9);
//            session.save(goods10);
//            session.save(goods11);
//            session.save(goods12);
//            session.save(goods13);
//            session.save(goods14);
//            session.save(goods15);
//            session.save(goods16);
//            session.save(goods17);
//            session.save(goods18);
//            // create GoodsUnit
//
//            GoodsUnit goodsUnit1 = createGoodsUnit(goods1, unit2, unit4, 23000, 12, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit2 = createGoodsUnit(goods1, unit4, unit4, 23000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit3 = createGoodsUnit(goods2, unit2, unit7, 26000, 12, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit4 = createGoodsUnit(goods2, unit7, unit7, 26000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit5 = createGoodsUnit(goods3, unit2, unit1, 20000, 12, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit6 = createGoodsUnit(goods3, unit1, unit1, 20000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit7 = createGoodsUnit(goods4, unit2, unit3, 45000, 8, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit8 = createGoodsUnit(goods4, unit3, unit3, 45000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit9 = createGoodsUnit(goods5, unit2, unit3, 30000, 10, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit10 = createGoodsUnit(goods5, unit3, unit3, 30000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit11 = createGoodsUnit(goods6, unit2, unit1, 15000, 16, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit12 = createGoodsUnit(goods6, unit1, unit1, 15000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit13 = createGoodsUnit(goods7, unit2, unit1, 550000, 10, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit14 = createGoodsUnit(goods7, unit1, unit1, 550000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit15 = createGoodsUnit(goods8, unit2, unit1, 460000, 10, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit16 = createGoodsUnit(goods8, unit1, unit1, 460000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit17 = createGoodsUnit(goods9, unit2, unit8, 435000, 12, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit18 = createGoodsUnit(goods9, unit8, unit8, 435000, 1, 1, new Date(), createdUser, 0, 0);
//
//            GoodsUnit goodsUnit19 = createGoodsUnit(goods10, unit2, unit9, 200000, 10, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit20 = createGoodsUnit(goods10, unit9, unit9, 200000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit21 = createGoodsUnit(goods11, unit2, unit3, 54000, 20, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit22 = createGoodsUnit(goods11, unit3, unit3, 54000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit23 = createGoodsUnit(goods12, unit2, unit3, 36000, 25, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit24 = createGoodsUnit(goods12, unit3, unit3, 36000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit25 = createGoodsUnit(goods13, unit2, unit3, 310000, 15, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit26 = createGoodsUnit(goods13, unit3, unit3, 310000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit27 = createGoodsUnit(goods14, unit2, unit1, 180000, 10, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit28 = createGoodsUnit(goods14, unit1, unit1, 180000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit29 = createGoodsUnit(goods15, unit2, unit8, 70000, 15, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit30 = createGoodsUnit(goods15, unit8, unit8, 70000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit31 = createGoodsUnit(goods16, unit2, unit1, 125000, 10, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit32 = createGoodsUnit(goods16, unit1, unit1, 125000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit33 = createGoodsUnit(goods17, unit1, unit3, 130000, 8, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit34 = createGoodsUnit(goods17, unit3, unit3, 130000, 1, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit35 = createGoodsUnit(goods18, unit7, unit10, 35000, 10, 1, new Date(), createdUser, 0, 0);
//            GoodsUnit goodsUnit36 = createGoodsUnit(goods18, unit10, unit10, 35000, 1, 1, new Date(), createdUser, 0, 0);
//
//            session.save(goodsUnit1);
//            session.save(goodsUnit2);
//            session.save(goodsUnit3);
//            session.save(goodsUnit4);
//            session.save(goodsUnit5);
//            session.save(goodsUnit6);
//            session.save(goodsUnit7);
//            session.save(goodsUnit8);
//            session.save(goodsUnit9);
//            session.save(goodsUnit10);
//            session.save(goodsUnit11);
//            session.save(goodsUnit12);
//            session.save(goodsUnit13);
//            session.save(goodsUnit14);
//            session.save(goodsUnit15);
//            session.save(goodsUnit16);
//            session.save(goodsUnit17);
//            session.save(goodsUnit18);
//            session.save(goodsUnit19);
//            session.save(goodsUnit20);
//            session.save(goodsUnit21);
//            session.save(goodsUnit22);
//            session.save(goodsUnit23);
//            session.save(goodsUnit24);
//            session.save(goodsUnit25);
//            session.save(goodsUnit26);
//            session.save(goodsUnit27);
//            session.save(goodsUnit28);
//            session.save(goodsUnit29);
//            session.save(goodsUnit30);
//            session.save(goodsUnit31);
//            session.save(goodsUnit32);
//            session.save(goodsUnit33);
//            session.save(goodsUnit34);
//            session.save(goodsUnit35);
//            session.save(goodsUnit36);
            // create workflow
            Workflow workflow1 = createWorkflow(1, null, "TAKE_PICTURE_1", company, "Chụp ảnh mặt bằng", 1, 1, 1, new Date(), createdUser, 0, 0);
            Workflow workflow2 = createWorkflow(2, null, "POS_STATUS", company, "Tình trạng điểm bán hàng", 0, 0, 1, new Date(), createdUser, 0, 0);
            Workflow workflow3 = createWorkflow(2, null, "POS_UPDATE", company, "Cập nhật điểm bán hàng", 0, 0, 2, new Date(), createdUser, 0, 0);
            Workflow workflow4 = createWorkflow(3, null, "COMPETITOR_LIST", company, "Danh sách đối thủ cạnh tranh", 0, 0, 1, new Date(), createdUser, 0, 0);

            Workflow workflow8 = createWorkflow(4, null, "NEW_POS", company, "Phát triển điểm mới", 1, 1, 1, new Date(), createdUser, 0, 0);
            Workflow workflow9 = createWorkflow(4, null, "TARGET", company, "Chỉ tiêu", 0, 1, 2, new Date(), createdUser, 0, 0);
            Workflow workflow10 = createWorkflow(4, null, "RECEIVE_GOODS", company, "Nhập hàng", 0, 1, 3, new Date(), createdUser, 0, 0);
            Workflow workflow11 = createWorkflow(4, null, "SELL_GOODS", company, "Bán hàng", 0, 1, 4, new Date(), createdUser, 0, 0);

            //insert workflow by package
            if (workflowService.checkImportWorkflowByPackage(workflow1.getCode(), company.getPackageService())) {
                session.save(workflow1);
            }
            if (workflowService.checkImportWorkflowByPackage(workflow2.getCode(), company.getPackageService())) {
                session.save(workflow2);
                WorkflowDetails workflowDetails1 = createWorkflowDetails(workflow2, 1, "Đang hoạt động", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails2 = createWorkflowDetails(workflow2, 1, "Đóng cửa", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails3 = createWorkflowDetails(workflow2, 1, "Ngừng hoạt động", null, new Date(), createdUser, 0, 0);
                session.save(workflowDetails1);
                session.save(workflowDetails2);
                session.save(workflowDetails3);
            }
            if (workflowService.checkImportWorkflowByPackage(workflow3.getCode(), company.getPackageService())) {
                session.save(workflow3);

                WorkflowDetails workflowDetails4 = createWorkflowDetails(workflow3, 2, "Tên điểm bán hàng", "POS_UPDATE_NAME", new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails5 = createWorkflowDetails(workflow3, 2, "Chủ điểm bán hàng", "POS_UPDATE_OWNER", new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails6 = createWorkflowDetails(workflow3, 2, "Địa chỉ điểm bán hàng", "POS_UPDATE_ADDRESS", new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails7 = createWorkflowDetails(workflow3, 2, "Số điện thoại điểm bán hàng", "POS_UPDATE_TEL", new Date(), createdUser, 0, 0);

                session.save(workflowDetails4);
                session.save(workflowDetails5);
                session.save(workflowDetails6);
                session.save(workflowDetails7);
            }
            if (workflowService.checkImportWorkflowByPackage(workflow4.getCode(), company.getPackageService())) {
                session.save(workflow4);

                WorkflowDetails workflowDetails27 = createWorkflowDetails(workflow4, 3, "Đối thủ 1", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails38 = createWorkflowDetails(workflow4, 3, "Đối thủ 2", null, new Date(), createdUser, 0, 0);
                session.save(workflowDetails27);
                session.save(workflowDetails38);

                Workflow workflow5 = createWorkflow(3, workflowDetails27, "COMPETITOR_TOOLS", company, "Danh mục thiết bị", 0, 0, 1, new Date(), createdUser, 0, 0);
                Workflow workflow6 = createWorkflow(3, workflowDetails27, "COMPETITOR_GOODS", company, "Sản phẩm đối thủ", 0, 0, 2, new Date(), createdUser, 0, 0);
                Workflow workflow7 = createWorkflow(3, workflowDetails27, "COMPETITOR_PROMOTION", company, "Chương trình khuyến mãi của đối thủ", 0, 0, 3, new Date(), createdUser, 0, 0);

                Workflow workflow16 = createWorkflow(3, workflowDetails38, "COMPETITOR_TOOLS", company, "Danh mục thiết bị", 0, 0, 1, new Date(), createdUser, 0, 0);
                Workflow workflow17 = createWorkflow(3, workflowDetails38, "COMPETITOR_GOODS", company, "Sản phẩm đối thủ", 0, 0, 2, new Date(), createdUser, 0, 0);
                Workflow workflow18 = createWorkflow(3, workflowDetails38, "COMPETITOR_PROMOTION", company, "Chương trình khuyến mãi của đối thủ", 0, 0, 3, new Date(), createdUser, 0, 0);
                session.save(workflow5);
                session.save(workflow6);
                session.save(workflow7);
                session.save(workflow16);
                session.save(workflow17);
                session.save(workflow18);

                WorkflowDetails workflowDetails9 = createWorkflowDetails(workflow5, 2, "Bảng hiệu ngang", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails10 = createWorkflowDetails(workflow5, 2, "Bảng hiệu đứng", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails11 = createWorkflowDetails(workflow5, 2, "Bảng điểm bán hàng", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails12 = createWorkflowDetails(workflow5, 2, "Đèn Led", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails13 = createWorkflowDetails(workflow5, 2, "Tủ trưng bày", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails14 = createWorkflowDetails(workflow5, 2, "Poster", null, new Date(), createdUser, 0, 0);
                session.save(workflowDetails9);
                session.save(workflowDetails10);
                session.save(workflowDetails11);
                session.save(workflowDetails12);
                session.save(workflowDetails13);
                session.save(workflowDetails14);

                WorkflowDetails workflowDetails15 = createWorkflowDetails(workflow6, 2, "Sản phẩm 1", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails16 = createWorkflowDetails(workflow6, 2, "Sản phẩm 2", null, new Date(), createdUser, 0, 0);
                session.save(workflowDetails15);
                session.save(workflowDetails16);

                WorkflowDetails workflowDetails17 = createWorkflowDetails(workflow7, 2, "Khuyến mãi 1", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails18 = createWorkflowDetails(workflow7, 2, "Khuyến mãi 2", null, new Date(), createdUser, 0, 0);
                session.save(workflowDetails17);
                session.save(workflowDetails18);

                WorkflowDetails workflowDetails28 = createWorkflowDetails(workflow16, 2, "Bảng hiệu ngang", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails29 = createWorkflowDetails(workflow16, 2, "Bảng hiệu đứng", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails30 = createWorkflowDetails(workflow16, 2, "Bảng điểm bán hàng", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails31 = createWorkflowDetails(workflow16, 2, "Đèn Led", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails32 = createWorkflowDetails(workflow16, 2, "Tủ trưng bày", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails33 = createWorkflowDetails(workflow16, 2, "Poster", null, new Date(), createdUser, 0, 0);

                session.save(workflowDetails28);
                session.save(workflowDetails29);
                session.save(workflowDetails30);
                session.save(workflowDetails31);
                session.save(workflowDetails32);
                session.save(workflowDetails33);

                WorkflowDetails workflowDetails34 = createWorkflowDetails(workflow17, 2, "Sản phẩm 3", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails35 = createWorkflowDetails(workflow17, 2, "Sảm phẩm 4", null, new Date(), createdUser, 0, 0);

                session.save(workflowDetails34);
                session.save(workflowDetails35);

                WorkflowDetails workflowDetails36 = createWorkflowDetails(workflow18, 2, "Khuyến mãi 3", null, new Date(), createdUser, 0, 0);
                WorkflowDetails workflowDetails37 = createWorkflowDetails(workflow18, 2, "Khuyến mãi 4", null, new Date(), createdUser, 0, 0);

                session.save(workflowDetails36);
                session.save(workflowDetails37);
            }

            if (workflowService.checkImportWorkflowByPackage(workflow8.getCode(), company.getPackageService())) {
                session.save(workflow8);
            }
            if (workflowService.checkImportWorkflowByPackage(workflow9.getCode(), company.getPackageService())) {
                session.save(workflow9);
            }
            if (workflowService.checkImportWorkflowByPackage(workflow10.getCode(), company.getPackageService())) {
                session.save(workflow10);
            }
            if (workflowService.checkImportWorkflowByPackage(workflow11.getCode(), company.getPackageService())) {
                session.save(workflow11);
            }

            transaction.commit();

            //create msales_company
            dataService.setBranch(DatabaseType.DATABASE_TYPE_COMPANY.getBranch());;
            MsalesCompany msalesCompany = new MsalesCompany();
            msalesCompany.setBranch(company.getBranch());
            msalesCompany.setCode(company.getCode());
            msalesCompany.setCompanyName(company.getName());
            msalesCompany.setIsActive(1);
            msalesCompany.setCreatedUser(1);

            msalesCompany.setMasterAdmin(false);
            msalesCompany.setUsername(null);
            msalesCompany.setPassword(null);

            dataService.insertRow(msalesCompany);

            return true;
        } catch (ConstraintViolationException | HibernateException | SecurityException | IllegalArgumentException e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private GoodsUnit createGoodsUnit(Goods goods, Unit unit, Unit childUnit, Integer price, Integer quantity, Integer isActive, Date createdAt, Integer createdUser, Integer updatedUser, Integer deletedUser) {
        GoodsUnit goodsUnit = new GoodsUnit();
        goodsUnit.setGoodss(goods);
        goodsUnit.setUnits(unit);
        goodsUnit.setChildUnitIds(childUnit);
        goodsUnit.setPrice(price);
        goodsUnit.setQuantity(quantity);
        goodsUnit.setIsActive(isActive);
        goodsUnit.setCreatedAt(createdAt);
        goodsUnit.setCreatedUser(createdUser);
        goodsUnit.setUpdatedUser(updatedUser);
        goodsUnit.setDeletedUser(deletedUser);

        return goodsUnit;
    }

    private Workflow createWorkflow(Integer workflowTypeId, WorkflowDetails workflowDetails, String code, Company company, String title, Integer isImage, Integer isRequired,
            Integer order, Date createAt, Integer createdUser, Integer updatedUser, Integer deletedUser) {
        Workflow workflow = new Workflow();
        workflow.setWorkflowTypeId(workflowTypeId);
        workflow.setWorkflowDetails(workflowDetails);
        workflow.setCode(code);
        workflow.setCompanys(company);
        workflow.setTitle(title);
        workflow.setIsImage(isImage);
        workflow.setIsRequired(isRequired);
        workflow.setOrder(order);
        workflow.setCreatedAt(createAt);
        workflow.setCreatedUser(createdUser);
        workflow.setUpdatedUser(updatedUser);
        workflow.setDeletedUser(deletedUser);
        return workflow;
    }

    private WorkflowDetails createWorkflowDetails(Workflow workflow, Integer actionType, String content, String code, Date createdAt, Integer createdUser, Integer updatedUser, Integer deletedUser) {
        WorkflowDetails workflowDetails = new WorkflowDetails();
        workflowDetails.setWorkflows(workflow);
        workflowDetails.setActionType(actionType);
        workflowDetails.setContent(content);
        workflowDetails.setCode(code);
        workflowDetails.setCreatedAt(createdAt);
        workflowDetails.setCreatedUser(createdUser);
        workflowDetails.setUpdatedUser(updatedUser);
        workflowDetails.setDeletedUser(deletedUser);
        return workflowDetails;

    }

    private WorkflowType createWorkflowType(String code, Integer type, String name, Integer order, Date createdAt, Integer createdUser, Integer updatedUser, Integer deletedUser) {
        WorkflowType workflowType = new WorkflowType();
        workflowType.setCode(code);
        workflowType.setType(type);
        workflowType.setName(name);
        workflowType.setOrder(order);
        workflowType.setCreatedAt(createdAt);
        workflowType.setCreatedUser(createdUser);
        workflowType.setUpdatedUser(updatedUser);
        workflowType.setDeletedUser(deletedUser);
        return workflowType;

    }

    private CompanyConfigDetails createCompanyConfigDetails(CompanyConfig companyConfig, int userRoleId, String code, String content, int order, int createdUser) {
        CompanyConfigDetails companyConfigDetails = new CompanyConfigDetails();
        companyConfigDetails.setCompanyConfigs(companyConfig);
        companyConfigDetails.setUserRoleId(userRoleId);
        companyConfigDetails.setCode(code);
        companyConfigDetails.setContent(content);
        companyConfigDetails.setOrder(order);
        companyConfigDetails.setIsActive(1);
        companyConfigDetails.setCreatedUser(createdUser);
        companyConfigDetails.setCreatedAt(new Date());
        companyConfigDetails.setUpdatedUser(0);
        companyConfigDetails.setDeletedUser(0);

        return companyConfigDetails;
    }

    private CompanyConfig createCompanyConfig(Company company, int userRoleId, int createdUser, String name) {
        CompanyConfig companyConfig = new CompanyConfig();
        companyConfig.setCompanys(company);
        companyConfig.setUserRoleId(userRoleId);
        companyConfig.setName(name);
        companyConfig.setIsActive(1);
        companyConfig.setCreatedAt(new Date());
        companyConfig.setCreatedUser(createdUser);
        companyConfig.setUpdatedUser(0);
        companyConfig.setDeletedUser(0);
        return companyConfig;
    }

    private POS createPOS(Channel channel, String code, String name, String address, int locationId, double lat, double lng, int createdUser) {
        POS pos = new POS();
        pos.setPosCode(code);
        pos.setName(name);
        pos.setChannels(channel);
        pos.setAddress(address);
        pos.setLocationId(locationId);
        pos.setStatusId(5);
        pos.setHierarchy(1);
        pos.setLat(BigDecimal.valueOf(lat));
        pos.setLng(BigDecimal.valueOf(lng));
        pos.setIsActive(1);
        pos.setBeginAt(new Date());
        pos.setEndAt(new Date());
        pos.setCreatedAt(new Date());
        pos.setCreatedUser(createdUser);
        pos.setUpdatedUser(0);
        pos.setDeletedUser(0);

        return pos;
    }

    private UserRoleChannel createUserRoleChannel(User user, int roleId, Channel channel, int createdUser) {
        UserRoleChannel userRoleChannel = new UserRoleChannel();
        userRoleChannel.setUserRoleId(roleId);
        userRoleChannel.setChannels(channel);
        userRoleChannel.setUsers(user);
        userRoleChannel.setBeginAt(new Date());
        userRoleChannel.setEndAt(null);
        userRoleChannel.setCreatedUser(createdUser);
        userRoleChannel.setCreatedAt(new Date());
        userRoleChannel.setUpdatedUser(0);
        userRoleChannel.setDeletedUser(0);
        return userRoleChannel;
    }

    private List<SalesStock> createStock(List<User> users, int createdUser) {
        List<SalesStock> ret = new ArrayList<>();
        for (User user : users) {
            SalesStock salesStock = new SalesStock();
            salesStock.setSalemanUsers(user);
            salesStock.setStatusId(1);
            salesStock.setCreatedUser(createdUser);
            salesStock.setCreatedAt(new Date());
            salesStock.setUpdatedUser(0);
            salesStock.setDeletedUser(0);
            ret.add(salesStock);
        }
        return ret;
    }

    private List<SalesStock> createPOSStock(List<POS> posList, int createdUser) {
        List<SalesStock> ret = new ArrayList<>();
        for (POS pos : posList) {
            SalesStock salesStock = new SalesStock();
            salesStock.setPoss(pos);
            salesStock.setStatusId(1);
            salesStock.setCreatedUser(createdUser);
            salesStock.setCreatedAt(new Date());
            salesStock.setUpdatedUser(0);
            salesStock.setDeletedUser(0);
            ret.add(salesStock);
        }
        return ret;
    }

    private List<SalesStock> createChannelStock(List<Channel> channelList, int createdUser) {
        List<SalesStock> ret = new ArrayList<>();
        for (Channel channel : channelList) {
            SalesStock salesStock = new SalesStock();
            salesStock.setChannels(channel);
            salesStock.setStatusId(1);
            salesStock.setCreatedUser(createdUser);
            salesStock.setCreatedAt(new Date());
            salesStock.setUpdatedUser(0);
            salesStock.setDeletedUser(0);
            ret.add(salesStock);
        }
        return ret;
    }

    private User createSalesman(int i, int createdUser, Company company) {
        String name = "salesman";
        if (i < 10) {
            name += 0;
        }
        name += i;

        User user = new User();
        user.setUsername(name + "@" + company.getCode());
        user.setCompanys(company);
        user.setUserCode(name + "@" + company.getCode());
        user.setPassword(MsalesSession.getSHA256(MsalesSession.DEFAULT_USER_PASS));
        user.setLastName("Saleman");
        user.setFirstName(i + "");
        user.setLocationId(4);
        user.setIsActive(1);
        user.setStatusId(6);
        user.setCreatedUser(createdUser);
        user.setCreatedAt(new Date());
        user.setUpdatedUser(0);
        user.setDeletedUser(0);
        return user;
    }

    /**
     * create a demo Company
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANY.ACTION_CREATE_DEMO_COMPANY, method = RequestMethod.POST)
    public @ResponseBody
    String createDemoCompany(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Company company;
            try {
                //parse jsonString to a Company Object
                company = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Company.class
                );
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //company from json not null
            if (company != null) {
                //check validate
                LinkedHashMap hashErrors = new LinkedHashMap();

                if (company.getName() == null || company.getName().trim().isEmpty()) {
                    hashErrors.put("name", MsalesValidator.NOT_NULL_AND_EMPTY);
                }

                if (company.getCode() == null || company.getCode().trim().isEmpty()) {
                    hashErrors.put("code", MsalesValidator.NOT_NULL_AND_EMPTY);
                }

                if (company.getLocationId() == null) {
                    hashErrors.put("locationId", MsalesValidator.NOT_NULL);
                }

                if (company.getUsername() == null || company.getUsername().trim().isEmpty()) {
                    hashErrors.put("userName", MsalesValidator.NOT_NULL_AND_EMPTY);
                }

                if (company.getPassword() == null || company.getPassword().trim().isEmpty()) {
                    hashErrors.put("password", MsalesValidator.NOT_NULL_AND_EMPTY);
                }

                if (company.getLastName() == null || company.getLastName().trim().isEmpty()) {
                    hashErrors.put("lastName", MsalesValidator.NOT_NULL_AND_EMPTY);
                }

                if (company.getFirstName() == null || company.getFirstName().trim().isEmpty()) {
                    hashErrors.put("firstName", MsalesValidator.NOT_NULL_AND_EMPTY);
                }

                if (company.getPosition() == null || company.getPosition().trim().isEmpty()) {
                    hashErrors.put("position", MsalesValidator.NOT_NULL_AND_EMPTY);
                }

                if (company.getTel() == null || company.getTel().trim().isEmpty()) {
                    hashErrors.put("tel", MsalesValidator.NOT_NULL_AND_EMPTY);
                }

                if (company.getEmail() == null || company.getEmail().trim().isEmpty()) {
                    hashErrors.put("email", MsalesValidator.NOT_NULL_AND_EMPTY);
                }

                if (company.getBranch() == null) {
                    hashErrors.put("branch", MsalesValidator.NOT_NULL);
                }

                if (company.getExpireTime() == null) {
                    hashErrors.put("expireTime", MsalesValidator.NOT_NULL);
                }

                if (company.getPackageService() == null) {
                    hashErrors.put("packageService", MsalesValidator.NOT_NULL);
                } else if (company.getPackageService() > 3 || company.getPackageService() < 1) {
                    hashErrors.put("packageService", "chỉ từ 1 to 3");
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //check code
                Integer branch;
                try {
                    branch = appService.getBranchFromUsername(company.getUsername(), true, dataService);
                    if (branch != null) //da ton tai
                    {
                        hashErrors.put("domain ", MsalesValidator.HAD_EXISTED);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.HAD_EXISTED, hashErrors));
                    }
                } catch (Exception ex) {
                    //khong hop le
                    hashErrors.put("username", MsalesValidator.DOMAIN_USER_NAME_INVALID);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, hashErrors));
                }

                Company companyNew = new Company();

                companyNew.setName(company.getName());
                companyNew.setCode(company.getCode());
                companyNew.setLocationId(company.getLocationId());
                companyNew.setUsername(company.getUsername());
                companyNew.setPassword(company.getPassword());
                companyNew.setLastName(company.getLastName());
                companyNew.setFirstName(company.getFirstName());
                companyNew.setPosition(company.getPosition());
                companyNew.setTel(company.getTel());
                companyNew.setEmail(company.getEmail());
                companyNew.setBranch(company.getBranch());
                companyNew.setEmployeeAmount(company.getEmployeeAmount());
                companyNew.setEmployeeSaleAmount(company.getEmployeeSaleAmount());
                companyNew.setExpireTime(company.getExpireTime());
                companyNew.setIsTemplate(company.isIsTemplate());
                companyNew.setPackageService(company.getPackageService());
                companyNew.setEquipmentMax(12);//default for demo

                companyNew.setName(company.getName());
                try {
                    if (createCompanyTemplete(companyNew, dataService)) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK));
                    } else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_INSERT_FAIL));
                    }
                } catch (Exception ex) {
                    if (ex instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }
            } //company from json null
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

}
