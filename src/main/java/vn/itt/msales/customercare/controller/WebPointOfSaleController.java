package vn.itt.msales.customercare.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.app.service.AppService;
import vn.itt.msales.channel.controller.MsalesLocationController;
import vn.itt.msales.channel.controller.MsalesPOSController;
import vn.itt.msales.channel.model.MsalesPOSImport;
import vn.itt.msales.channel.services.WebMsalesChannelServices;
import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.common.MsalesDownloadTemplet;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.common.OptionItemComparator;
import vn.itt.msales.common.Utilities;
import vn.itt.msales.common.ValidatorUtil;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.customercare.model.WebCustomerCare;
import vn.itt.msales.customercare.services.CustomerCareService;
import vn.itt.msales.customercare.services.POSService;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.CustomerCareDetails;
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.POSImg;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.services.ExcelService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;
import vn.itt.msales.user.model.URLManager;

@Controller
public class WebPointOfSaleController {

    @Autowired
    private MsalesLocationController locationController;

    @Autowired
    private MsalesPOSController posController;

    @Autowired
    private View jsonView_i;

    @Autowired
    private POSService posService;

    @Autowired
    private DataService dataService;

    @Autowired
    private WebMsalesChannelServices channelService;

    @Autowired
    private CustomerCareService customerCareService;

    @Autowired
    private AppService appService;
    
    @Autowired
    private ExcelService excelService;

    @InitBinder
    protected void initBiner(WebDataBinder biner) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        biner.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }

    @Autowired
    private ServiceFilter serviceFilter;

    @Value("#{systemProperties['system.imagesRoot']}")
    private String imagesRoot;

    @Resource(name = "systemPros")
    private Properties systemProperties;

    private List<Location> mListLocation;
    private List<Channel> mListChannel;
    private List<Status> mListStatus;

    // ================== Show list of POS =============
    @RequestMapping(value = "/pos/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String initListPointOfSales(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @ModelAttribute("frmPOSList") @Valid Filter filter,
            HttpServletRequest request, Model uiModel) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);

        uiModel.addAttribute("only", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY", "USER_ROLE_ADMIN_CHANNEL", "USER_ROLE_SALES_SUPERVISOR"}));

        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }

        ParameterList parameterList = new ParameterList("statusTypes.id", 2, 0, 0);
        List<Status> status = dataService.getListOption(Status.class, parameterList);
        uiModel.addAttribute("statusList", status);
        //set role cho Cbb Nhan vien
        filter.setRoles(new Integer[]{6});
        List<OptionItem> employeeList = filter.processFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());
        uiModel.addAttribute("frmPOSList", filter);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

        params.put("channels.companys.id", userInf.getCompanyId().toString());
        params.put("hierarchyType", "1");  // If 0 -> search hierarchy = 0, 1 -> search hierarchy > 0

        //search Order theo danh sach nhan vien
        if (filter.getStatusId() != null && filter.getStatusId() > 0) {
            params.put("statusId", filter.getStatusId().toString());
        }
        //  if (userInf.getUserRoleCode().equals("USER_ROLE_ADMIN_COMPANY")) {
        if (filter.getIdChannel() != null && filter.getIdChannel() > 0) {
            params.put("channelId", filter.getIdChannel().toString());
        }
        // }
        if (filter.getUserId() != null && filter.getUserId() > 0) {
            params.put("createdUser", filter.getUserId().toString());
        }
        if (filter.getStartDateString() != null && !filter.getStartDateString().isEmpty()) {
            try {
                Date date = simpleDateFormat.parse(filter.getStartDateString());
                params.put("beginAt", ValidatorUtil.parseParameterDateTimeDB(date));
            } catch (ParseException ex) {
                Logger.getLogger(WebPointOfSaleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (filter.getEndDateString() != null && !filter.getEndDateString().isEmpty()) {
            params.put("endAt", filter.getEndDateString());
        }
        if (filter.getSearchText() != null && !filter.getSearchText().isEmpty()) {
            String key = filter.getSearchText();
            key = key.replaceAll("'", "''");
            key = key.replaceAll(" ", "%");
            params.put("searchText", key);
        }

        MsalesResults<POS> results = posService.searchPOSByParams(userInf, filter, params, page, size);
        List<POS> posList = new ArrayList<POS>();
        if (results != null) {
            posList = results.getContentList();
            Utilities.addPaginationToModel(results.getCount(), page, size, uiModel);
        }
        uiModel.addAttribute("posList", posList);

        return "posList";
    }

    @RequestMapping(value = "/pos/diary")
    public String posDiaryGet(HttpServletRequest request,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @ModelAttribute("frmDiaryList") @Valid Filter diaryFilter,
            Model uiModel) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        return showDiary(page, size, diaryFilter, uiModel, request);
    }

    public String showDiary(Integer page, Integer size,
            Filter filter, Model uiModel, HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);

        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }

        //set role cho Cbb Nhan vien
        filter.setRoles(new Integer[]{6});
        filter.processFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());

        //search Order theo danh sach nhan vien
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

        params.put("channels.companys.id", userInf.getCompanyId() + "");

        if (filter.getIdChannel() != null && filter.getIdChannel() > 0) {
            params.put("channelId", filter.getIdChannel().toString());
        }
        if (filter.getLocationId() != null && filter.getLocationId() > 0) {
            params.put("locationId", filter.getLocationId().toString());
        }
        if (filter.getUserId() != null && filter.getUserId() > 0) {
            params.put("implementEmployeeId", filter.getUserId().toString());
        }
        if (filter.getStartDateString() != null && !filter.getStartDateString().isEmpty()) {
            try {
                Date date = simpleDateFormat.parse(filter.getStartDateString());
                params.put("fromDate", ValidatorUtil.parseParameterDateTimeDB(date));
            } catch (ParseException ex) {
                Logger.getLogger(WebPointOfSaleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (filter.getEndDateString() != null && !filter.getEndDateString().isEmpty()) {
            try {
                Date date = simpleDateFormat.parse(filter.getEndDateString());
                params.put("toDate", ValidatorUtil.parseParameterDateTimeDB(date));
            } catch (ParseException ex) {
                Logger.getLogger(WebPointOfSaleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (filter.getSearchText() != null && !filter.getSearchText().isEmpty()) {
            params.put("searchText", filter.getSearchText());
        }

        MsalesResults<CustomerCareInformation> results = posService.searchCustomerCareByParams(params, page, size);
        List<CustomerCareInformation> cusList = new ArrayList();
        if (results != null) {
            cusList = results.getContentList();
            Utilities.addPaginationToModel(results.getCount(), page, size, uiModel);
        }
        uiModel.addAttribute("posDiary", cusList);

        uiModel.addAttribute("frmDiaryList", filter);
        return "posDiary";
    }

    @RequestMapping(value = "/pos/details/{posId}", method = {RequestMethod.GET, RequestMethod.POST})
    public String details(@PathVariable("posId") int posId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            HttpServletRequest request, Model uiModel) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }

        MsalesResults<CustomerCareInformation> results = customerCareService.getListCustomerCareInformationByPOSId(posId, page, size);
        List<CustomerCareInformation> cusList = new ArrayList();
        if (results != null) {
            cusList = results.getContentList();
            Utilities.addPaginationToModel(results.getCount(), page, size, uiModel);
        }
        uiModel.addAttribute("ccList", cusList);
        uiModel.addAttribute("imagesRoot", imagesRoot);

        return "posDetails";
    }

    private Object[] getSearchFilter(Model uiModel, Long posId) {
        List<String> listSearchFields = new ArrayList<String>();
        List<Object> listSearchValues = new ArrayList<Object>();

        listSearchFields.add("pos.id");
        listSearchValues.add(posId);

        String[] fieldNames = new String[0];
        fieldNames = listSearchFields.toArray(fieldNames);
        Object[] fieldValues = listSearchValues.toArray();

        return new Object[]{fieldNames, fieldValues};
    }

    //======================================================================
    // Add DBH 
    @RequestMapping(value = "/pos/add", method = RequestMethod.GET)
    public String addPos(@ModelAttribute("posForm") POS pos,
            HttpServletRequest request, Model uiModel) {

        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        uiModel.addAttribute("edit", false);        
        List<OptionItem> allStatus = getListStatus(2);
        List<OptionItem> statusList = OptionItem.NoOptionList(0, "-- Trạng thái --");
        statusList.addAll(allStatus);
        uiModel.addAttribute("statusList", statusList);
        
        //fix
        Filter filter = Filter.processChannel(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId(), 0);
        initLocationByChannel(uiModel, filter.getIdChannel(), LoginContext.getLogin(request).getCompanyId(), request);

        return "posAdd";
    }

    //----------------- Save DBH
    @RequestMapping(value = "/pos/add", method = RequestMethod.POST)
    public String submitSavePos(RedirectAttributes redirectAttributes,
            @ModelAttribute("posForm") POS pos,
            BindingResult result, Model uiModel, HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        Filter filter = Filter.processChannel(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());
        Channel channel = new Channel();
        channel.setId(filter.getIdChannel());
        pos.setChannels(channel);

        Location location = new Location();
        if (pos.getWardId() != null && pos.getWardId() > 0) {
            location.setId(pos.getWardId());
            location.setLocationType(3);
        }
        if ((pos.getWardId() == null || pos.getWardId() == 0) && (pos.getDistrictId() != null && pos.getDistrictId() > 0)) {
            location.setId(pos.getDistrictId());
            location.setLocationType(2);
        }
        if ((pos.getWardId() == null || pos.getWardId() == 0) && (pos.getDistrictId() == null || pos.getDistrictId() == 0)) {
            location.setId(pos.getProvinceId());
            location.setLocationType(1);
        }
        pos.setLocations(location);
        pos.setCreatedUser(LoginContext.getLogin(request).getId());
        pos.setUpdatedUser(0);
        pos.setDeletedUser(0);
        pos.setIsActive(1);
        pos.setBeginAt(new Date());
        pos.setEndAt(new Date());

        int ret = posService.insertRow(pos,LoginContext.getLogin(request).getCompanyId(),dataService);

        if (ret > 0) {
            redirectAttributes.addFlashAttribute("createSuccess", true);
            redirectAttributes.addFlashAttribute("edit", true);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/pos/sales/" + ret;
        } else {
            uiModel.addAttribute("success", false);
        }

        uiModel.addAttribute("edit", true);

        initPosForm(pos, uiModel, request);
        //   filter.processChannel(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());
        return "posAdd";
    }

    // View DBH 
    @RequestMapping(value = "/pos/sales/{posId}", method = RequestMethod.GET)
    public String editPos(@PathVariable("posId") Integer posId,
            HttpServletRequest request, Model uiModel) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        uiModel.addAttribute("readonly", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY", "USER_ROLE_ADMIN_CHANNEL", "USER_ROLE_SALES_SUPERVISOR"}));

        if (posId == null) {
            return "prohibited";
        }

        uiModel.addAttribute("edit", true);
        POS pos = posService.getPOSById(posId);
        if (pos != null) {
            if (pos.getHierarchy() > 4) {
                pos.setHierarchy(0);
            }

            if (pos.getLat().compareTo(BigDecimal.ZERO) == 0) {
                pos.setLat(BigDecimal.ZERO);
            }
            if (pos.getLng().compareTo(BigDecimal.ZERO) == 0) {
                pos.setLng(BigDecimal.ZERO);
            }
            Filter filter = Filter.processChannel(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId(), pos.getChannels().getId());
            initPosForm(pos, uiModel, request);

        } else {
            return "prohibited";
        }
        return "posSales";
    }

    //----------------- Update DBH
    @RequestMapping(value = "/pos/sales/{posId}", method = RequestMethod.POST)
    public String submitEditPos(
            //@PathVariable("posId") Long posId,
            @ModelAttribute("posForm") @Valid POS pos,
            BindingResult result, HttpServletRequest request, Model uiModel) {

        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        uiModel.addAttribute("edit", true);
        Filter filter = Filter.processChannel(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());
        Channel channel = new Channel();
        channel.setId(filter.getIdChannel());
        pos.setChannels(channel);

        Location location = new Location();
        if (pos.getWardId() != null && pos.getWardId() > 0) {
            location.setId(pos.getWardId());
            location.setLocationType(3);
        }
        if ((pos.getWardId() == null || pos.getWardId() == 0) && (pos.getDistrictId() != null && pos.getDistrictId() > 0)) {
            location.setId(pos.getDistrictId());
            location.setLocationType(2);
        }
        if ((pos.getWardId() == null || pos.getWardId() == 0) && (pos.getDistrictId() == null || pos.getDistrictId() == 0)) {
            location.setId(pos.getProvinceId());
            location.setLocationType(1);
        }
        pos.setLocations(location);

        if (posService.updateRow(pos)) {
            uiModel.addAttribute("success", true);
        } else {
            uiModel.addAttribute("success", false);
        }

        POS _pos = posService.getPOSById(pos.getId());
        if (_pos.getLat().compareTo(BigDecimal.ZERO) == 0) {
            _pos.setLat(BigDecimal.ZERO);
        }
        if (_pos.getLng().compareTo(BigDecimal.ZERO) == 0) {
            _pos.setLng(BigDecimal.ZERO);
        }
        initPosForm(_pos, uiModel, request);
        //      filter.processChannel(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());

        return "posSales";
    }

    public void initPosForm(POS pos, Model uiModel, HttpServletRequest request) {
        List<OptionItem> allStatus = getListStatus(2);
        List<OptionItem> statusList = OptionItem.NoOptionList(0, "-- Trạng thái --");
        statusList.addAll(allStatus);
        uiModel.addAttribute("statusList", statusList);

        List<OptionItem> all = getListLocation(1);
        List<OptionItem> provinceList = new ArrayList<OptionItem>();
        provinceList.addAll(all);
        uiModel.addAttribute("provineList", provinceList);

        if (pos.getLocations() != null) {
            int locationType = pos.getLocations().getLocationType();
            if (locationType == 1) {
                if (pos.getProvinceId() == null) {
                    pos.setProvinceId(pos.getLocations().getId());
                }
                initDistrict(pos.getLocations().getId(), uiModel);

            } else if (locationType == 3) {

                if (pos.getDistrictId() == null) {
                    pos.setDistrictId(pos.getLocations().getParents().getId());
                }
                if (pos.getProvinceId() == null) {
                    pos.setProvinceId(pos.getLocations().getParents().getParents().getId());
                }
                pos.setWardId(pos.getLocations().getId());
                // get xa
                initWard(pos.getDistrictId(), uiModel);
                // get quan/huyen
                initDistrict(pos.getProvinceId(), uiModel);
            } else if (locationType == 2) {

                if (pos.getDistrictId() == null) {
                    pos.setDistrictId(pos.getLocations().getParents().getId());
                }
                if (pos.getProvinceId() == null) {
                    pos.setProvinceId(pos.getLocations().getParents().getParents().getId());
                }
                // get xa
                initWard(pos.getDistrictId(), uiModel);

                // get quan/huyen
                initDistrict(pos.getProvinceId(), uiModel);
            }
        } else {
            if (pos.getProvinceId() != null && pos.getProvinceId() > 0) {
                initDistrict(pos.getProvinceId(), uiModel);
            }
            if (pos.getDistrictId() != null && pos.getDistrictId() > 0) {
                initWard(pos.getDistrictId(), uiModel);
            }
        }
        uiModel.addAttribute("posForm", pos);
    }

    private void initWard(int id, Model uiModel) {
        List<OptionItem> all = getListLocationByParentId(id);
        List<OptionItem> wardList = new ArrayList<OptionItem>();
        wardList.addAll(all);
        uiModel.addAttribute("wardList", wardList);
    }

    private void initDistrict(int id, Model uiModel) {
        List<OptionItem> all = getListLocationByParentId(id);
        List<OptionItem> districtList = new ArrayList<OptionItem>();
        districtList.addAll(all);
        uiModel.addAttribute("districtList", districtList);
    }

    @RequestMapping(value = "/pos/customercare/update/{customerCareId}")
    public String updateCustomerCare(@PathVariable("customerCareId") int customerCareId,
            HttpServletRequest request, RedirectAttributes redirectAttributes) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        CustomerCareInformation customerCareInformation = customerCareService.getCustomerCareInformationById(customerCareId);
        if (customerCareInformation != null) {
            try {
                POS pos = customerCareInformation.getPoss();
                //update
                List<CustomerCareDetails> list = customerCareService.getListCustomerCareDetailsUpdatePOS(customerCareId, dataService);

                for (CustomerCareDetails customerCareDetails : list) {
                    if (customerCareDetails.getWorkflowDetailss() != null && customerCareDetails.getWorkflowDetailss().getCode() != null) {
                        if (customerCareDetails.getWorkflowDetailss().getCode().equals("POS_UPDATE_NAME")) {
                            pos.setName(customerCareDetails.getContent());
                        } else if (customerCareDetails.getWorkflowDetailss().getCode().equals("POS_UPDATE_OWNER")) {
                            pos.setOwnerName(customerCareDetails.getContent());
                        } else if (customerCareDetails.getWorkflowDetailss().getCode().equals("POS_UPDATE_ADDRESS")) {
                            pos.setAddress(customerCareDetails.getContent());
                        } else if (customerCareDetails.getWorkflowDetailss().getCode().equals("POS_UPDATE_TEL")) {
                            pos.setTel(customerCareDetails.getContent());
                        }
                    }
                }
                pos.setUpdatedUser(LoginContext.getLogin(request).getId());
                pos.setUpdatedAt(new Date());
                dataService.updateRow(pos);
                redirectAttributes.addFlashAttribute("update", true);
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("update", false);
            }
        } else {
            redirectAttributes.addFlashAttribute("update", false);
        }
        return "redirect:/pos/customercare/" + customerCareId;
    }

    @RequestMapping("/pos/customercare/{customerCareId}")
    public String customerCare(
            @PathVariable("customerCareId") int customerCareId,
            HttpServletRequest request, ModelMap uiModel) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        CustomerCareInformation ccInfo = customerCareService.getCustomerCareInformationById(customerCareId);

        List<POSImg> posImgs = dataService.getListOption(POSImg.class, new ParameterList("poss.id", ccInfo.getPoss().getId(), 1, 1));
        if (!posImgs.isEmpty()) {
            for (POSImg posImg : posImgs) {
                if (posImg.getPath() != null) {
                    ccInfo.getPoss().setImage(posImg.getPath());
                    break;
                }
            }
        }

        if (ccInfo != null) {
            uiModel.addAttribute("ccInfo", ccInfo);

            List<CustomerCareDetails> list = customerCareService.getListCustomerCareDetails(customerCareId, dataService);
            for (CustomerCareDetails customerCareDetails : list) {
                if (customerCareDetails.getWorkflowDetailss() != null) {
                    customerCareDetails.setCustomerCareDetailsList(customerCareService.getListCustomerCareDetailsByWorkflowDetailsId(customerCareId, customerCareDetails.getWorkflowDetailss().getId(), dataService));
                }
            }
            List<WebCustomerCare> webCustomerCareList = new ArrayList<>();

            processWebCustomerCare(list, webCustomerCareList);

            uiModel.addAttribute("webCustomerCareList", webCustomerCareList);

        } else {
            return "notFoundError";
        }

        uiModel.addAttribute("customerCareImage", systemProperties.getProperty("system.customerCareImage"));

        return "posCustomerCare";
    }

    private void processWebCustomerCare(List<CustomerCareDetails> list, List<WebCustomerCare> webCustomerCareList) {
        if (list != null && !list.isEmpty()) {
            int workflowTypeId = -1;
            int workflowId = -1;
            List<CustomerCareDetails> customerDetailsList = new ArrayList<>();
            for (CustomerCareDetails customerCareDetails : list) {
                if (customerCareDetails.getWorkflows().getIsImage() == 1) {
                    if (customerCareDetails.getWorkflows().getWorkflowTypes().getId() != workflowTypeId) {
                        if (!customerDetailsList.isEmpty()) {
                            webCustomerCareList.add(new WebCustomerCare(customerDetailsList));
                        }
                        customerDetailsList = new ArrayList<>();
                        workflowTypeId = customerCareDetails.getWorkflows().getWorkflowTypes().getId();
                    }
                } else {
                    if (customerCareDetails.getWorkflows().getId() != workflowId) {
                        if (!customerDetailsList.isEmpty()) {
                            webCustomerCareList.add(new WebCustomerCare(customerDetailsList));
                        }
                        customerDetailsList = new ArrayList<>();
                        workflowId = customerCareDetails.getWorkflows().getId();
                    }
                }
                customerDetailsList.add(customerCareDetails);
            }

            if (!customerDetailsList.isEmpty()) {
                webCustomerCareList.add(new WebCustomerCare(customerDetailsList));
            }
        }
    }

    @RequestMapping(value = "/newPos/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String ListPointOfSales(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @ModelAttribute("frmNewPosList") Filter filter,
            HttpServletRequest request, Model uiModel) {

        // Checks login
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);

        uiModel.addAttribute("only", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY", "USER_ROLE_ADMIN_CHANNEL", "USER_ROLE_SALES_SUPERVISOR"}));
        uiModel.addAttribute("notShow", true);
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }

        //set roles cho cbb Nhan vien
        filter.setRoles(new Integer[]{6});
        List<OptionItem> employeeList = filter.processFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());

        uiModel.addAttribute("frmNewPosList", filter);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

        params.put("channels.companys.id", userInf.getCompanyId().toString());
        params.put("hierarchyType", "0");  // If 0 -> search hierarchy = 0, 1 -> search hierarchy > 0

        //search Order theo danh sach nhan vien
        if (filter.getStatusId() != null && filter.getStatusId() > 0) {
            params.put("statusId", filter.getStatusId().toString());
        }
        //  if (userInf.getUserRoleCode().equals("USER_ROLE_ADMIN_COMPANY")) {
        if (filter.getIdChannel() != null && filter.getIdChannel() > 0) {
            params.put("channelId", filter.getIdChannel().toString());
        }
        // }

        if (filter.getUserId() != null && filter.getUserId() > 0) {
            params.put("createdUser", filter.getUserId().toString());
        }
        if (filter.getStartDateString() != null && !filter.getStartDateString().isEmpty()) {
            try {
                Date date = simpleDateFormat.parse(filter.getStartDateString());
                params.put("beginAt", ValidatorUtil.parseParameterDateTimeDB(date));
            } catch (ParseException ex) {
                Logger.getLogger(WebPointOfSaleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (filter.getEndDateString() != null && !filter.getEndDateString().isEmpty()) {
            params.put("endAt", filter.getEndDateString());
        }
        if (filter.getSearchText() != null && !filter.getSearchText().isEmpty()) {
            params.put("searchText", filter.getSearchText());
        }

        MsalesResults<POS> results = posService.searchPOSByParams(userInf, filter, params, page, size);
        List<POS> posList = new ArrayList<POS>();
        if (results != null) {
            posList = results.getContentList();
            for (POS pos : posList) {
                User user = dataService.getRowById(pos.getCreatedUser(), User.class);
                if (user != null) {
                    pos.setUserCreated(user);
                }
                //Xét địa chỉ quận huyện của điểm bán hàng
                if (pos.getLocations().getLocationType() == 3) {
                    pos.setQuanHuyen(pos.getLocations().getParents().getName());
                    pos.setTinhThanh(pos.getLocations().getParents().getParents().getName());
                } else if (pos.getLocations().getLocationType() == 2) {
                    pos.setQuanHuyen(pos.getLocations().getName());
                    pos.setTinhThanh(pos.getLocations().getParents().getName());
                } else {
                    pos.setTinhThanh(pos.getLocations().getName());
                }

                List<POSImg> posImgs = dataService.getListOption(POSImg.class, new ParameterList("poss.id", pos.getId()));
                if (!posImgs.isEmpty()) {
                    for (POSImg posImg : posImgs) {
                        if (posImg.getPath() != null) {
                            pos.setImage(posImg.getPath());
                            break;
                        }
                    }
                }
            }

            Utilities.addPaginationToModel(results.getCount(), page, size, uiModel);
        }
        uiModel.addAttribute("newPosList", posList);

        return "newPosList";
    }

    @RequestMapping(value = "/newPos/approve", method = RequestMethod.POST)
    public ModelAndView approve(@RequestParam("posId") Integer posId,
            HttpServletRequest request) {
        String status = "OK";
        // Check đăng nhập
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        MsalesLoginUserInf user = LoginContext.getLogin(request);

        request.setAttribute(MsalesConstants.CONTENTS, "{\"id\": " + posId
                + ", \"hierarchy\": 1, \"updatedUser\": " + user.getId() + "} ");
        String responseString = posController.activePOS(request);
        try {
            MsalesResponse response = MsalesJsonUtils.getObjectFromJSON(
                    responseString, MsalesResponse.class);
            if (response.getStatus().getCode() == HttpStatus.OK.value()) {

            } else {
                status = "Not OK";
            }
        } catch (Exception e) {
            status = "Not OK";
        }

        return new ModelAndView(jsonView_i, "status", status);
    }

    private List<OptionItem> getListStatus(int statusType) {
        List<OptionItem> data = new ArrayList<>();
        try {
            List<Status> list = (List<Status>) posController.getDataService().getListOption(Status.class, new ParameterList("statusTypes.id", statusType));
            if (list != null) {
                for (Status status : list) {
                    OptionItem item = new OptionItem();
                    item.setId(status.getId());
                    item.setName(status.getName());
                    data.add(item);
                }
            }
            if (data.size() > 0) {
                Collections.sort(data, new OptionItemComparator());
            }
        } catch (Exception ex) {
            Logger.getLogger(WebPointOfSaleController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;
    }

    private List<OptionItem> getListLocation(int locationType) {
        List<OptionItem> data = new ArrayList<>();
        try {
            List<Location> list = (List<Location>) locationController.getDataService().getListOption(Location.class, new ParameterList("locationType", locationType));
            if (list != null) {
                for (Location location : list) {
                    OptionItem item = new OptionItem();
                    item.setId(location.getId());
                    item.setName(location.getName());
                    data.add(item);
                }
            }
            if (data.size() > 0) {
                Collections.sort(data, new OptionItemComparator());
            }
        } catch (Exception ex) {
            Logger.getLogger(WebPointOfSaleController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;
    }

    private List<OptionItem> getListLocationByParentId(int parentId) {
        List<OptionItem> data = new ArrayList<>();
        try {
            List<Location> list = (List<Location>) locationController.getDataService().getListOption(Location.class, new ParameterList("parents.id", parentId));
            if (list != null) {
                for (Location location : list) {
                    OptionItem item = new OptionItem();
                    item.setId(location.getId());
                    item.setName(location.getName());
                    data.add(item);
                }
            }
            if (data.size() > 0) {
                Collections.sort(data, new OptionItemComparator());
            }
        } catch (Exception ex) {
            Logger.getLogger(WebPointOfSaleController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    public void initPos(POS pos, int channelId, int companyId, Model uiModel, HttpServletRequest request) {
        List<OptionItem> allStatus = getListStatus(2);
        List<OptionItem> statusList = OptionItem.NoOptionList(0, "-- Trạng thái --");
        statusList.addAll(allStatus);
        uiModel.addAttribute("statusList", statusList);
        //Khởi tạo danh sách location cho form
        String hql = "Select ChannelLocation.locations.id as id, ChannelLocation.locations.name as name, ChannelLocation.locations.locationType as locationType"
                + " from ChannelLocation as ChannelLocation where deletedUser = 0"
                + " and channels.id = " + channelId + " and channels.companys.id = " + companyId;
        List<HashMap> lists = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
        List<HashMap> provinceList = new ArrayList<HashMap>();
        List<HashMap> districtList = new ArrayList<HashMap>();
        List<HashMap> wardList = new ArrayList<HashMap>();
        for (HashMap hashMap : lists) {
            if ((Integer) hashMap.get("locationType") == 1) {
                provinceList.add(hashMap);
            } else if ((Integer) hashMap.get("locationType") == 2) {
                Location location = dataService.getRowById((Integer) hashMap.get("id"), Location.class);
                int proviceSize = provinceList.size();
                boolean boolProvince = true;
                for (int i = 0; i < proviceSize; i++) {
                    if ((Integer) provinceList.get(i).get("id") == location.getParents().getId()) {
                        boolProvince = false;
                        break;
                    }
                }
                if (boolProvince) {
                    HashMap parent = new HashMap();
                    parent.put("id", location.getParents().getId());
                    parent.put("name", location.getParents().getName());
                    provinceList.add(parent);
                }
                districtList.add(hashMap);
            } else if ((Integer) hashMap.get("locationType") == 3) {
                Location location = dataService.getRowById((Integer) hashMap.get("id"), Location.class);
                int proviceSize = provinceList.size();
                boolean boolProvince = true;
                for (int i = 0; i < proviceSize; i++) {
                    if ((Integer) provinceList.get(i).get("id") == location.getParents().getParents().getId()) {
                        boolProvince = false;
                        break;

                    }
                }
                if (boolProvince) {
                    HashMap parent = new HashMap();
                    parent.put("id", location.getParents().getParents().getId());
                    parent.put("name", location.getParents().getParents().getName());
                    provinceList.add(parent);
                }
                int districtSize = districtList.size();
                boolean boolDistrict = true;
                for (int i = 0; i < districtSize; i++) {
                    if ((Integer) districtList.get(i).get("id") != location.getParents().getId()) {
                        boolDistrict = false;
                        break;

                    }
                }
                if (boolDistrict) {
                    HashMap parent = new HashMap();
                    parent.put("id", location.getParents().getId());
                    parent.put("name", location.getParents().getName());
                    districtList.add(parent);
                }
                wardList.add(hashMap);
            }
        }
        List<OptionItem> province = new ArrayList<OptionItem>();
        List<OptionItem> district = new ArrayList<OptionItem>();
        List<OptionItem> ward = new ArrayList<OptionItem>();
        if (!provinceList.isEmpty()) {
            province.addAll(OptionItem.createOptionItemListFromHashMap(provinceList));

        }
        if (!districtList.isEmpty()) {
            district.addAll(OptionItem.createOptionItemListFromHashMap(districtList));

        }
        if (!wardList.isEmpty()) {
            ward.addAll(OptionItem.createOptionItemListFromHashMap(wardList));

        }
        if (pos.getLocations() != null) {
            if (pos.getLocations().getLocationType() == 3) {

            }
        }

        uiModel.addAttribute("provineList", province);
        uiModel.addAttribute("districtList", district);
        uiModel.addAttribute("wardList", ward);
        uiModel.addAttribute("posForm", pos);
    }

    public void initLocationByChannel(Model uiModel, int channelId, int companyId, HttpServletRequest request) {
        String hql = "Select ChannelLocation.locations.id as id, ChannelLocation.locations.name as name, ChannelLocation.locations.locationType as locationType"
                + " from ChannelLocation as ChannelLocation where deletedUser = 0"
                + " and channels.id = " + channelId + " and channels.companys.id = " + companyId;
        List<HashMap> lists = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
        List<HashMap> provinceList = new ArrayList<HashMap>();
        List<HashMap> districtList = new ArrayList<HashMap>();
        List<HashMap> wardList = new ArrayList<HashMap>();
        for (HashMap hashMap : lists) {
            if ((Integer) hashMap.get("locationType") == 1) {
                provinceList.add(hashMap);
            } else if ((Integer) hashMap.get("locationType") == 2) {
                Location location = dataService.getRowById((Integer) hashMap.get("id"), Location.class);
                int proviceSize = provinceList.size();
                boolean boolProvince = true;
                for (int i = 0; i < proviceSize; i++) {
                    if ((Integer) provinceList.get(i).get("id") == location.getParents().getId()) {
                        boolProvince = false;
                        break;
                    }
                }
                if (boolProvince) {
                    HashMap parent = new HashMap();
                    parent.put("id", location.getParents().getId());
                    parent.put("name", location.getParents().getName());
                    provinceList.add(parent);
                }
                // districtList.add(hashMap);
            } else if ((Integer) hashMap.get("locationType") == 3) {
                Location location = dataService.getRowById((Integer) hashMap.get("id"), Location.class);
                int proviceSize = provinceList.size();
                boolean boolProvince = true;
                for (int i = 0; i < proviceSize; i++) {
                    if ((Integer) provinceList.get(i).get("id") == location.getParents().getParents().getId()) {
                        boolProvince = false;
                        break;

                    }
                }
                if (boolProvince) {
                    HashMap parent = new HashMap();
                    parent.put("id", location.getParents().getParents().getId());
                    parent.put("name", location.getParents().getParents().getName());
                    provinceList.add(parent);
                }

            }
        }
        List<OptionItem> province = new ArrayList<OptionItem>();

        if (!provinceList.isEmpty()) {
            province.addAll(OptionItem.createOptionItemListFromHashMap(provinceList));

        }
        uiModel.addAttribute("provineList", province);
    }

    /**
     * Path of the file to be downloaded, relative to application's directory
     */
    private String separator = File.separator;
    private String filePath = String.format("%sdownloads%simports%s", separator, separator, separator);

    @RequestMapping(value = "/pos/download.do")
    public void downloadTemplets(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }

        MsalesDownloadTemplet downloadTemplet = new MsalesDownloadTemplet(filePath, request, response);
        downloadTemplet.getFile("POS_IMPORT.xlsx");
    }

    @RequestMapping(value = "/pos/import", method = RequestMethod.POST)
    private String importPOS(HttpServletRequest request, @RequestParam("file_pos") MultipartFile multipartFile) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        List<List<Object>> listOjb = excelService.getListDataFromExel(multipartFile,18,0);
        List<MsalesPOSImport> posImports = getListPOSImport(listOjb);
        if (posImports.isEmpty()) {
            request.setAttribute("error", true);
            request.setAttribute("emptyData", true);
            return "posImport";
        }

        List<String> requiredList = checkRequired(posImports);
        if (requiredList.isEmpty()) {
            if (listOjb != null && !listOjb.isEmpty()) {
                List<MsalesPOSImport> validateChannelCode = validateChannelCode(posImports, loginUserInf);
                List<MsalesPOSImport> validateLocation = validateLocationCode(posImports);
                List<MsalesPOSImport> validateStatus = validateStatus(posImports);

                boolean channelEmpty = validateChannelCode.isEmpty();
                boolean locationEmpty = validateLocation.isEmpty();
                boolean statusEmpty = validateStatus.isEmpty();

                if (!channelEmpty || !locationEmpty || !statusEmpty) {
                    request.setAttribute("listChannelCode", validateChannelCode);
                    request.setAttribute("listLocationCode", validateLocation);
                    request.setAttribute("listStatus", validateStatus);

                    request.setAttribute("channelError", channelEmpty);
                    request.setAttribute("locationError", locationEmpty);
                    request.setAttribute("statusError", statusEmpty);
                    request.setAttribute("error", true);
                    request.setAttribute("updated", false);
                } else {
                    // get list POS from POSImport
                    List<POS> listPOS = getListPOSFromPOSImport(posImports, loginUserInf);
                    if (!listPOS.isEmpty()) {
                        Transaction transaction = null;
                        org.hibernate.Session datasSession = null;
                        try {
                            for (POS pos : listPOS) {
                                datasSession = dataService.openSession();
                                transaction = datasSession.beginTransaction();
                                //general posCode
                                pos.setPosCode(appService.createPOSCode(loginUserInf.getCompanyId(), pos.getLocations(), dataService));
                                datasSession.save(pos);
                                //create pos stock
                                SalesStock salesStock = new SalesStock();
                                salesStock.setPoss(pos);
                                salesStock.setStatusId(1);
                                salesStock.setCreatedUser(loginUserInf.getId());
                                salesStock.setCreatedAt(new Date());
                                salesStock.setUpdatedUser(0);
                                salesStock.setDeletedUser(0);
                                datasSession.save(salesStock);
                                //commit
                                transaction.commit();
                            }
                            request.setAttribute("updated", true);
                        } catch (Exception e) {
                            transaction.rollback();
                            request.setAttribute("error", true);
                            request.setAttribute("errorSQL", true);
                        } finally {
                            datasSession.close();
                        }
                    } else {
                        //thong bao ko co du lieu dc import
                    }
                }
            }
        } else {
            request.setAttribute("required", true);
            request.setAttribute("requiredList", requiredList);

            request.setAttribute("error", true);
            request.setAttribute("updated", true);
            request.setAttribute("channelError", true);
            request.setAttribute("locationError", true);
            request.setAttribute("duplicatedCode", false);
            request.setAttribute("statusError", true);
        }

        request.setAttribute("submited", true);
        return "posImport";
    }

    @RequestMapping(value = "/pos/import", method = RequestMethod.GET)
    private String importPOS(HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        return "posImport";
    }

    private List<POS> getListPOSFromPOSImport(List<MsalesPOSImport> pOSImports, MsalesLoginUserInf loginUserInf) {
        List<POS> listPos = new ArrayList<>();
        for (MsalesPOSImport posImport : pOSImports) {
            POS pos = new POS();
            // set value from POSIMPORT
            pos.setPosCode(posImport.getPosCode());
            pos.setName(posImport.getPosName());
            pos.setLocations(posImport.getLocation());
            pos.setChannels(posImport.getChannel());
            pos.setAddress(posImport.getPosAddress());
            pos.setLat(posImport.getPosLAT());
            pos.setLng(posImport.getPosLNG());
            pos.setOwnerName(posImport.getPosOwnerName());
            pos.setBirthday(posImport.getPosBirthay());
            pos.setOwnerCode(posImport.getPosOwnerCode());
            pos.setOwnerCodeDate(posImport.getPosOwnerCodeDate());
            pos.setOwnerCodeLocation(posImport.getPosOwnerCodeLocation());
            pos.setTel(posImport.getPosHomePhone());
            pos.setMobile(posImport.getPosMobilePhone());
            pos.setOtherTel(posImport.getPosOtherTel());
            pos.setEmail(posImport.getPosEmail());
            pos.setWebsite(posImport.getPosWebsite());
            pos.setFax(posImport.getPosFax());
            pos.setFrequency(posImport.getPosFrequency());
            pos.setDayOfWeek(posImport.getPosDayOfWeek());
            pos.setBeginAt(posImport.getPosBeginAt() == null ? new Date() : posImport.getPosBeginAt());
            pos.setEndAt(pos.getBeginAt());//FIX?
            pos.setHierarchy(posImport.getPosHierarchy());
            pos.setStatuss(posImport.getStatuss());
            // set values by default
            pos.setIsActive(1);
            pos.setCreatedUser(loginUserInf.getId());
            pos.setCreatedAt(new Date());
            pos.setUpdatedUser(0);
            pos.setDeletedUser(0);
            listPos.add(pos);
        }
        return listPos;

    }

    private List<MsalesPOSImport> getListPOSImport(List<List<Object>> listObj) {
        List<MsalesPOSImport> listPOSImports = new ArrayList<>();
        for (List<Object> object : listObj) {
            if (!excelService.checkEmptyRow(object)) {
                MsalesPOSImport posImport = new MsalesPOSImport();

                posImport.setPosName(object.get(0) == null ? null : object.get(0).toString());
                posImport.setPosOwnerName(object.get(1) == null ? null : object.get(1).toString());
                posImport.setPosAddress(object.get(2) == null ? null : object.get(2).toString());
                try {
                    posImport.setPosLAT(new BigDecimal((Double) object.get(3)).setScale(15, RoundingMode.HALF_UP));
                    posImport.setPosLNG(new BigDecimal((Double) object.get(4)).setScale(15, RoundingMode.HALF_UP));
                } catch (Exception ex) {
                    posImport.setPosLAT(BigDecimal.ZERO);
                    posImport.setPosLNG(BigDecimal.ZERO);
                }

                posImport.setPosOwnerCode(object.get(5) == null ? null : excelService.getNumberString(object.get(5)));
                posImport.setPosOwnerCodeLocation(object.get(6) == null ? null : excelService.getNumberString(object.get(6)));
                posImport.setPosHomePhone(object.get(7) == null ? null : excelService.getNumberString(object.get(7).toString()));
                posImport.setPosMobilePhone(object.get(8) == null ? null : excelService.getNumberString(object.get(8).toString()));

                posImport.setCityCode(object.get(9) == null ? null : excelService.getNumberString(object.get(9).toString()));
                posImport.setDistrictCode(object.get(10) == null ? null : excelService.getNumberString(object.get(10).toString()));
                posImport.setWardCode(object.get(11) == null ? null : excelService.getNumberString(object.get(11).toString()));

                posImport.setPosChannelCode(object.get(12) == null ? null : excelService.getNumberString(object.get(12).toString()));

                posImport.setPosFrequency(object.get(13) == null ? null : excelService.getInteger(object.get(13).toString()));
                posImport.setPosDayOfWeek(object.get(14) == null ? null : excelService.getInteger(object.get(14).toString()));

                posImport.setPosBeginAt(object.get(15) == null ? null : excelService.getDate(object.get(15).toString()));
                try {
                    posImport.setPosHierarchy(excelService.getInteger(object.get(16).toString()));
                } catch (Exception ex) {
                    posImport.setPosHierarchy(0);
                }
                try {
                    posImport.setStatus(excelService.getInteger(object.get(17).toString()));
                    if (posImport.getStatus() < 0 || posImport.getStatus() > 3) {
                        posImport.setStatus(1);
                    }
                } catch (Exception ex) {
                    posImport.setStatus(1);
                }
                listPOSImports.add(posImport);
            }
        }
        return listPOSImports;
    }
    
    private List<MsalesPOSImport> validateLocationCode(List<MsalesPOSImport> posImports) {
        List<MsalesPOSImport> list = new ArrayList<>();
        for (MsalesPOSImport pos : posImports) {
            boolean flag = false;
            if (pos.getCityCode() != null && pos.getDistrictCode() != null && pos.getWardCode() != null
                    && !pos.getCityCode().trim().isEmpty() && !pos.getDistrictCode().trim().isEmpty() && !pos.getWardCode().trim().isEmpty()) {
                //check city
                Location city = channelService.getCityByCode(pos.getCityCode(), dataService);
                if (city != null) {
                    Location district = channelService.getDistrictByCode(pos.getDistrictCode(), city.getId(), dataService);
                    if (district != null) {
                        Location ward = channelService.getWardByCode(pos.getWardCode(), district.getId(), dataService);
                        if (ward != null) {
                            pos.setLocation(ward);
                            flag = true;
                        } else {
                            pos.setWardErrror(true);
                        }
                    } else {
                        pos.setDistrictError(true);
                    }
                } else {
                    pos.setCityError(true);
                }
            }
            if (!flag) {
                list.add(pos);
            }
        }
        return list;
    }

    private List<MsalesPOSImport> validateChannelCode(List<MsalesPOSImport> pOSImports, MsalesLoginUserInf loginUserInf) {
        List<MsalesPOSImport> posImport = new ArrayList<>();
        mListChannel = new ArrayList<>();
        for (MsalesPOSImport pos : pOSImports) {
            if (pos.getPosChannelCode() != null && !pos.getPosChannelCode().trim().isEmpty()) {
                Channel channel = channelService.getChannelByCode(pos.getPosChannelCode(), loginUserInf.getCompanyId(), dataService);
                if (channel == null) {
                    //thu tim voi fullcode
                    channel = channelService.getChannelByFullCode(pos.getPosChannelCode(), loginUserInf.getCompanyId(), dataService);
                }

                if (channel == null) {
                    posImport.add(pos);
                } else {
                    pos.setChannel(channel);
                }
            }
        }
        return posImport;
    }

    private List<String> checkRequired(List<MsalesPOSImport> pOSImports) {
        List<String> requireList = new ArrayList<>();
        boolean flag = true;
        for (MsalesPOSImport pOSImport : pOSImports) {
            if (excelService.checkEmptyOrNull(pOSImport.getPosName())) {
                requireList.add("Tên điểm bán hàng là bắt buộc nhập.");
                flag = false;
            }
            if (excelService.checkEmptyOrNull(pOSImport.getPosChannelCode())) {
                requireList.add("Mã kênh là bắt buộc nhập.");
                flag = false;
            }
            if (excelService.checkEmptyOrNull(pOSImport.getPosAddress())) {
                requireList.add("Địa chỉ là bắt buộc nhập.");
                flag = false;
            }
            if (excelService.checkEmptyOrNull(pOSImport.getCityCode())) {
                requireList.add("Mã Tỉnh/Thành phố là bắt buộc nhập.");
                flag = false;
            }
            if (excelService.checkEmptyOrNull(pOSImport.getDistrictCode())) {
                requireList.add("Mã Quận/huyện là bắt buộc nhập.");
                flag = false;
            }
            if (excelService.checkEmptyOrNull(pOSImport.getWardCode())) {
                requireList.add("Mã Phường/xã là bắt buộc nhập.");
                flag = false;
            }
            if (!flag) {
                break;
            }
        }
        return requireList;
    }

    private List<MsalesPOSImport> validateStatus(List<MsalesPOSImport> channelImports) {
        List<MsalesPOSImport> statusErrorList = new ArrayList<>();
        List<Status> statusList = posService.getPOSStatus(dataService);
        for (MsalesPOSImport posImport : channelImports) {
            boolean flag = false;
            for (Status status : statusList) {
                if (posImport.getStatus().toString().equals(status.getValue())) {
                    flag = true;
                    posImport.setStatuss(status);
                    break;
                }
            }

            if (!flag) {
                statusErrorList.add(posImport);
            }
        }
        return statusErrorList;
    }
}
