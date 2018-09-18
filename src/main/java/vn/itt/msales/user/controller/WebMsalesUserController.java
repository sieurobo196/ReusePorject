package vn.itt.msales.user.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.app.service.LoginService;
import vn.itt.msales.channel.services.WebMsalesChannelServices;
import vn.itt.msales.common.MsalesDownloadTemplet;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.common.xls.MsalesExcelConfig;
import vn.itt.msales.common.xls.MsalesReadExcel;
import vn.itt.msales.csb.auth.MsalesSession;
import vn.itt.msales.customercare.controller.WebPointOfSaleController;
import vn.itt.msales.database.dbrouting.DatabaseType;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelType;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.GoodsCategory;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.MsalesCompany;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.Session;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserGoodsCategory;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.CreateUserAdmin;
import vn.itt.msales.entity.searchObject.SearchUser;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesChangePassword;
import vn.itt.msales.user.model.MsalesLoginUserInf;
import vn.itt.msales.user.model.MsalesUserImport;
import vn.itt.msales.user.model.URLManager;
import vn.itt.msales.user.services.MsalesUserServices;

/**
 *
 * @author vtm
 */
@Controller
public class WebMsalesUserController {

//    @Autowired
//    private MsalesUserController userController;
    @Autowired
    private MsalesUserServices userServices;
    @Autowired
    private LoginService loginService;
    @Autowired
    private DataService dataService;
    @Autowired
    private ServiceFilter serviceFilter;

    @Autowired
    private WebMsalesChannelServices channelService;

    private List<Channel> mListChannel;
    private List<Location> mListLocation;
    private List<UserRole> mListUserRole;
    private List<Status> mListStatus;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(HttpServletRequest request) {
//        String referer = request.getHeader("Referer");
//        int login = LoginContext.isLogin(request, dataService);
//        if (login == -1) {
//            return LoginContext.redirectLogin();
//        } else if (login == 0) {
//            return LoginContext.notAccess();
//        }
//        else if(login == 2){
//            return LoginContext.redirectAdminPage();
//        }
        return "index";
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String error(HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        }
        return "noData";
    }

    @RequestMapping(value = "/notFound", method = RequestMethod.GET)
    public String notFound(HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        }
        return "notfound";
    }

    @RequestMapping(value = "/WebLogout", method = RequestMethod.GET)
    public void logout(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(value = "user") User user) throws IOException {
        LoginContext.logout(request, response, dataService, loginService);
        String contextPath = request.getContextPath();
        response.sendRedirect(contextPath + "/" + "WebLogin");
    }

    @RequestMapping(value = "/WebLogin", method = RequestMethod.GET)
    public String loginGET(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute(value = "user") User user) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login > 0) {
            return LoginContext.redirectHome();
        }
        return "login";
    }

    @RequestMapping(value = "/WebLogin", method = RequestMethod.POST)
    public String loginPOST(HttpServletRequest request, HttpServletResponse response,
            Model uiModel, @ModelAttribute(value = "user") User user) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login > 0) {
            return LoginContext.redirectHome();
        }

        String remmeber = request.getParameter("remember_checkbox");
        if (remmeber != null) {
            request.setAttribute("remember", true);
        }
        try {
            //login by admin master
            MsalesCompany msalesCompanyAdmin = loginService.loginMasterAdmin(user.getUsername(), user.getPassword(), dataService);
            if (msalesCompanyAdmin!=null) {
                MsalesLoginUserInf loginInf = new MsalesLoginUserInf();
                loginInf.setUsername(msalesCompanyAdmin.getUsername());
                loginInf.setFirstName("ADMIN");
                loginInf.setLastName("MASTER");
                loginInf.setCompanyId(0);
                loginInf.setCompanyName(msalesCompanyAdmin.getCompanyName());
                loginInf.setBranch(0);
                loginInf.setIsAdminMaster(true);

                request.getSession().setAttribute("userLogin", loginInf);
                //redirect to home
                return LoginContext.redirectHome();
            }

            String[] uName = user.getUsername().split("@");
            if (uName.length != 2) {
                uiModel.addAttribute("usernameFailed", true);
            } else {
                String companyCode = uName[1];
                ParameterList parameterList = new ParameterList(1, 1);
                parameterList.add("code", companyCode);
                parameterList.add("isActive", 1);
                //get MsalesCompnay
                dataService.setBranch(DatabaseType.DATABASE_TYPE_COMPANY.getBranch());;
                List<MsalesCompany> listMsalesCompany = dataService.getListOption(MsalesCompany.class, parameterList);
                if (listMsalesCompany != null && listMsalesCompany.size() == 1) {
                    MsalesCompany msalesCompany = listMsalesCompany.get(0);
                    int branch = msalesCompany.getBranch();
                    dataService.setBranch(branch);
                    user.setPassword(MsalesSession.getSHA256(user.getPassword()));

                    List<UserRoleChannel> userRoleChannels = loginService.loginWeb(user.getUsername(), user.getPassword(), dataService);
                    if (!userRoleChannels.isEmpty()) {
                        UserRoleChannel userRoleChannel = userRoleChannels.get(0);
                        User userFromDB = userRoleChannel.getUsers();
                        //check 
                        boolean check = true;

                        //check time company
                        Company company = userFromDB.getCompanys();
                        if (company.getExpireTime() != null) {
                            if (new Date().compareTo(company.getExpireTime()) > 0) {
                                //het thoi gian 
                                if (company.getIsRegister() == null || company.getIsRegister() == 0) {
                                    //het thoi gian mua
                                    uiModel.addAttribute("expired", true);
                                } else {
                                    //het thoi gian trai nghiem
                                    uiModel.addAttribute("expiredRegister", true);
                                }
                                check = false;
                            }
                        }

                        if (!userFromDB.getStatuss().getValue().equals("1")) {
                            uiModel.addAttribute("userNotWork", true);
                            check = false;
                        } else if (userFromDB.getIsActive() != 1) {//check khoa
                            uiModel.addAttribute("userLock", true);
                            check = false;
                        }

                        if (check) {
                            MsalesLoginUserInf loginInf = new MsalesLoginUserInf(userFromDB, userRoleChannel.getUserRoles(), request.getSession(), userRoleChannels);
                            ParameterList parameterSessionList = new ParameterList(1, 1);
                            parameterSessionList.add("token", loginInf.getToken());
                            parameterSessionList.setOrder("lastAccessedTime", "DESC");

                            List<Session> sessionList = dataService.getListOption(Session.class, parameterSessionList);
                            if (sessionList.isEmpty()) {
                                // check permission
                                Session session = new Session();
                                session.setLastAccessedTime(new Date());
                                session.setToken(loginInf.getToken());
                                session.setUserId(userFromDB.getId());
                                //role = 1
                                session.setUserRolerId(userRoleChannel.getUserRoles().getId());
                                dataService.insertRow(session);
                            }
                            loginInf.setBranch(branch);
                            request.getSession().setAttribute("userLogin", loginInf);
                            Cookie cookie = new Cookie("userLogin", "");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                            //check remmeber=>save to cookie
                            if (remmeber != null) {
                                cookie = new Cookie("userLogin", loginInf.getToken() + "-" + branch);
                                //cookie.setDomain(domain);
                                cookie.setMaxAge(24 * 60 * 60);
                                response.addCookie(cookie);
                            }
                            //redirect to home
                            return LoginContext.redirectHome();
                        }
                    } else {
                        uiModel.addAttribute("failed", true);
                    }
                } else {
                    uiModel.addAttribute("codeFailed", true);
                }
            }
        } catch (Exception ex) {
            uiModel.addAttribute("serverFailed", true);
        }
        return "login";
    }

    @RequestMapping(value = "/user/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String listUsers(
            HttpServletRequest request,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @ModelAttribute("filterModel") @Valid Filter filter,
            Model uiModel) throws JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        uiModel.addAttribute("onlyView", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"}));

        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;

        if (filter.getChannelId() != null) {
            filter = Filter.processChannel(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId(), filter.getChannelId(), true);
        } else {
            filter.processFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId(), true);
        }

        List<OptionItem> roleList = OptionItem.NoOptionList(0, "-- Chọn vai trò --");
        // get user role channel
        ParameterList parameterList = new ParameterList();
        // get List UserRole from DB
        List<UserRole> listUserRole = dataService.getListOption(UserRole.class, parameterList);
        for (UserRole userRole : listUserRole) {
            roleList.add(new OptionItem(userRole.getId(), userRole.getName()));
        }
        uiModel.addAttribute("roleList", roleList);

        List<OptionItem> statusList = OptionItem.NoOptionList(0, "-- Trạng thái làm việc --");
        ParameterList paramStatus = new ParameterList("statusTypes.id", 3);
        List<Status> listS = dataService.getListOption(Status.class, paramStatus);
        for (Status status : listS) {
            statusList.add(new OptionItem(status.getId(), status.getName()));
        }
        uiModel.addAttribute("statusList", statusList);
        SearchUser searchUser = new SearchUser();
        searchUser.setChannelId(filter.getIdChannel());
        searchUser.setLocationId(filter.getLocationId());
        searchUser.setStatusId(filter.getStatusId());
        //searchUser.setIsActive(!filter.isInActive());
        searchUser.setUserRoleId(filter.getRole());
        searchUser.setSearchText(filter.getSearchText());
        searchUser.setActiveId(filter.getActiveId());
        List<OptionItem> employeeList = filter.getUserList();
        MsalesResults<HashMap> result = userServices.searchUser(employeeList, searchUser, LoginContext.getLogin(request).getCompanyId(), new MsalesPageRequest(page, size));
        maxPages = Filter.calulatorMaxPages(result.getCount().intValue(), size);
        if (page > maxPages) {
            page = maxPages;
        }
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);
        uiModel.addAttribute("userList", result.getContentList());

        return "userList";
    }

    /**
     * update User
     * <p>
     * @param user
     * @param request
     * @param uiModel
     * @param id
     * @param bindingResult
     * <p>
     * @return
     */
    @RequestMapping(value = "/user/detail/{id}", method = RequestMethod.GET)
    public String viewUser(@PathVariable(value = "id") int id,
            HttpServletRequest request, Model uiModel) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        uiModel.addAttribute("readonly", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"}));

        try {
            MsalesLoginUserInf loginUser = LoginContext.getLogin(request);
            User user = dataService.getRowById(id, User.class);
            if (user != null && user.getId() != 0) {

                //set lai locationId
                user.setLocationId(user.getLocations().getId());
                //set lai cai truong
                user.setCompanyId(loginUser.getCompanyId());
                user.setCreatedUser(loginUser.getId());
                user.setUpdatedUser(0);
                user.setDeletedUser(0);
                // get List userRoleChannel by userId
                ParameterList parameterList = new ParameterList("users.id", user.getId(), 0, 0);
                List<UserRoleChannel> userRoleChannelList = dataService.getListOption(UserRoleChannel.class, parameterList);

                if (userRoleChannelList != null && userRoleChannelList.size() > 0
                        && userRoleChannelList.get(0).getUserRoles() != null) {
                    user.setUserRole(userRoleChannelList.get(0).getUserRoles());
                    uiModel.addAttribute("user", user);
                    for (UserRoleChannel urc : userRoleChannelList) {
                        String channelName = urc.getChannels().getName();
                        Channel channel = urc.getChannels();
                        while (channel.getParents() != null) {
                            channel = channel.getParents();
                            channelName = channel.getName() + "»" + channelName;
                        }
                        urc.getChannels().setChannelName(channelName);
                    }

                    uiModel.addAttribute("userRoleChannelList", userRoleChannelList);
                    //get List UserGoodsCategory by userId
                    ParameterList parameterUserGoodList = new ParameterList("users.id", user.getId(), 0, 0);
                    parameterList.add("statuss.id", 15);
                    List<UserGoodsCategory> userGoodsCategoryList = dataService.getListOption(UserGoodsCategory.class, parameterUserGoodList);
                    Filter filter = initData(loginUser, user, request, uiModel, userGoodsCategoryList);

                    //get max channel
                    int channelId = userRoleChannelList.get(0).getChannels().getId();
                    Channel channel = dataService.getRowById(channelId, Channel.class);
                    int maxChannel = 0;
                    int maxChannelType = 0;
                    if (channel != null) {
                        maxChannel = channel.getId();
                        maxChannelType = channel.getChannelTypes().getId();
                        while (channel.getParents() != null) {
                            maxChannel = channel.getParents().getId();
                            maxChannelType = channel.getParents().getChannelTypes().getId();
                            channel = channel.getParents();
                        }
                    }

                    //set mien
                    Integer[] channelIdList = new Integer[]{maxChannel};
                    uiModel.addAttribute("channelIdList", channelIdList);

                    OptionItem optionItem = serviceFilter.getChannelTypeByParent(maxChannelType, dataService);
                    if (optionItem != null) {
                        List<OptionItem> childList = Filter.createFirstOptionType(optionItem);
                        childList.addAll(serviceFilter.getCbListChannelByParentId(maxChannel, dataService));
                        filter.getChannelList().set(1, childList);
                    }

                    uiModel.addAttribute("channelId", maxChannel);
                    uiModel.addAttribute("level", 1);

                    List<OptionItem> provinceList = OptionItem.NoOptionList(0, "-- Tỉnh/Thành Phố --");
                    provinceList.addAll(serviceFilter.getCbListLocationByChannel(channelId, dataService));
                    uiModel.addAttribute("provinceList", provinceList);
                } else {
                    return "noData";
                }
            }
            return "userDetail";
        } catch (Exception e) {
            return "userError";
        }
    }

    @RequestMapping(value = "/user/detail/{userId}", method = RequestMethod.POST)
    public String updateUser(@ModelAttribute("user") @Valid User user,
            BindingResult bindingResult, HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model uiModel) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUser = LoginContext.getLogin(request);

        try {
            if (request.getParameter("chkIsActive") != null) {
                user.setIsActive(0);
            } else {
                user.setIsActive(1);
            }

            String[] goodsCategoryList = request.getParameterValues("goodsCategoryId");
            String[] userGoodsCategory = request.getParameterValues("userGoodsCategoryId");
            ArrayList<UserGoodsCategory> userGoodsCategoryList = new ArrayList();

            if (goodsCategoryList != null && userGoodsCategory != null) {
                for (int i = 0; i < goodsCategoryList.length; i++) {
                    try {
                        UserGoodsCategory item = new UserGoodsCategory();
                        if (i < userGoodsCategory.length && userGoodsCategory[i] != null && !userGoodsCategory[i].trim().isEmpty()) {
                            int userGoodsCategoryId = Integer.parseInt(userGoodsCategory[i]);
                            item.setId(userGoodsCategoryId);
                        }
                        int goodsCategoryId = Integer.parseInt(goodsCategoryList[i]);

                        GoodsCategory goodsCategory = new GoodsCategory();
                        goodsCategory.setId(goodsCategoryId);
                        item.setGoodsCategorys(goodsCategory);
                        userGoodsCategoryList.add(item);
                    } catch (Exception ex) {
                        //parse failed
                        return "userError";
                    }
                }
            }

            //get list userRoleChannel
            List<UserRoleChannel> userRoleChannelList = getListUserRoleChannel(request);
            uiModel.addAttribute("userRoleChannelList", userRoleChannelList);

            //validate user            
            Filter filter = initData(loginUser, user, request, uiModel, userGoodsCategoryList);

            int channelId = filter.getIdChannel();
            int level = filter.getlevel();

            uiModel.addAttribute("channelId", channelId);
            uiModel.addAttribute("level", level);

            if (userRoleChannelList != null && !userRoleChannelList.isEmpty()) {
                List<OptionItem> provinceList = OptionItem.NoOptionList(0, "-- Tỉnh/Thành Phố --");
                provinceList.addAll(serviceFilter.getCbListLocationByChannel(channelId, dataService));
                uiModel.addAttribute("provinceList", provinceList);
            }

            if (!bindingResult.hasErrors()) {
                boolean error = false;
                //check so luong nhan vien khi trai nghiem
                if (user.getStatuss().getId() == 6 && user.getIsActive() == 1) {
                    if (checkMaxSalesUser(user, user.getUserRole().getId(), loginUser.getEquipmentMax())) {
                        //vuot qua so luong sales 12 khi trai nghiem
                        error = true;
                        uiModel.addAttribute("overSale", true);
                        uiModel.addAttribute("saleMax", loginUser.getEquipmentMax());
                    }
                }

                if (!error) {
                    //set lai cai truong
                    user.setCompanyId(loginUser.getCompanyId());
                    user.setCreatedUser(loginUser.getId());
                    user.setUpdatedUser(0);
                    user.setDeletedUser(0);
                    CreateUserAdmin createUserAdmin = new CreateUserAdmin();
                    createUserAdmin.setUser(user);
                    createUserAdmin.setUserGoodsCategoryList(userGoodsCategoryList);
                    createUserAdmin.setUserRoleChannelList(userRoleChannelList);
                    boolean updated = updateUser(createUserAdmin, request);
                    if (updated) {
                        uiModel.addAttribute("success", true);
                    } else {
                        //cap nhat that bai
                        uiModel.addAttribute("success", false);
                    }
                }
            }

            return "userDetail";
        } catch (Exception ex) {
            return "userError";
        }

    }

    @RequestMapping(value = "/user/add")
    public String addUser(HttpServletRequest request, Model uiModel) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        try {
            MsalesLoginUserInf loginUser = LoginContext.getLogin(request);
            if (loginUser == null) {
                return LoginContext.redirectLogin();
            }

            User user = new User();
            user.setPassword("password");

            //set company for user
            Company company = new Company();
            company.setId(user.getCompanyId());
            user.setCompanys(company);
            user.setCreatedUser(loginUser.getId());
            user.setUpdatedUser(0);
            user.setDeletedUser(0);
            user.setIsActive(1);

            initData(loginUser, user, request, uiModel, new ArrayList<UserGoodsCategory>());

            int channelId = 0;
            int level = 1;
            uiModel.addAttribute("channelId", channelId);
            uiModel.addAttribute("level", level);
            return "userAdd";
        } catch (Exception ex) {
            return "userError";
        }
    }

    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public String addNewUser(@ModelAttribute("user") @Valid User user,
            BindingResult bindingResult, HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model uiModel) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        try {
            MsalesLoginUserInf loginUser = LoginContext.getLogin(request);

            if (request.getParameter("chkIsActive") != null) {
                user.setIsActive(0);
            } else {
                user.setIsActive(1);
            }
            // set password default for user
            // encript user pass
            String pass = MsalesSession.getSHA256(MsalesSession.DEFAULT_USER_PASS);
            // set password affter encript.
            user.setPassword(pass);

            ArrayList<UserGoodsCategory> userGoodsCategoryList = new ArrayList();

            String[] goodsCategoryList = request.getParameterValues("goodsCategoryId");
            if (goodsCategoryList != null) {
                for (String s : goodsCategoryList) {
                    try {
                        int goodsCategoryId = Integer.parseInt(s);
                        UserGoodsCategory userGoodsCategory = new UserGoodsCategory();
                        GoodsCategory goodsCategory = new GoodsCategory();
                        goodsCategory.setId(goodsCategoryId);
                        userGoodsCategory.setGoodsCategorys(goodsCategory);
                        userGoodsCategoryList.add(userGoodsCategory);
                    } catch (Exception ex) {
                        //parse failed
                        return "userError";
                    }
                }
            }

            List<UserRoleChannel> userRoleChannelList = getListUserRoleChannel(request);

            boolean error = false;
            //validate user            
            if (bindingResult.hasErrors()) {
                error = true;
            } else if (user.getStatuss().getId() == 6 && user.getIsActive() == 1) {
                if (checkMaxSalesUser(user, user.getUserRole().getId(), loginUser.getEquipmentMax())) {
                    //vuot qua so luong sales 12 khi trai nghiem
                    error = true;
                    uiModel.addAttribute("overSale", true);
                    uiModel.addAttribute("saleMax", loginUser.getEquipmentMax());
                }
            }

            if (error) {
                uiModel.addAttribute("userRoleChannelList", userRoleChannelList);
                Filter filter = initData(loginUser, user, request, uiModel, userGoodsCategoryList);
                int channelId = filter.getIdChannel();
                int level = filter.getlevel();

                uiModel.addAttribute("channelId", channelId);
                uiModel.addAttribute("level", level);

                List<OptionItem> provinceList = OptionItem.NoOptionList(0, "-- Tỉnh/Thành Phố --");
                provinceList.addAll(serviceFilter.getCbListLocationByChannel(channelId, dataService));
                uiModel.addAttribute("provinceList", provinceList);
            } else {
                //set lai cac truong
                user.setCompanyId(loginUser.getCompanyId());
                user.setCreatedUser(loginUser.getId());
                user.setUpdatedUser(0);
                user.setDeletedUser(0);
                user.setCreatedUser(LoginContext.getLogin(request).getId());
                CreateUserAdmin caUserAdmin = new CreateUserAdmin();
                caUserAdmin.setUser(user);
                caUserAdmin.setUserGoodsCategoryList(userGoodsCategoryList);
                caUserAdmin.setUserRoleChannelList(userRoleChannelList);

                int userId = executAddUser(caUserAdmin, request);
                if (userId > 0) {
                    redirectAttributes.addFlashAttribute("createUser", true);
                    return "redirect:/user/detail/" + userId;
                } else {
                    //dang ky that bai
                    return "userError";
                }
            }

            return "userAdd";
        } catch (Exception ex) {
            return "userError";
        }

    }

    private boolean checkMaxSalesUser(User user, int role, Integer equipmentMax) {
        boolean ret = false;
        if (equipmentMax != null) {
            if (role == 4 || role == 5 || role == 6) {
                //check so luong user                 
                return userServices.checkMaxSaleUser(user.getCompanys().getId(), user.getId(), equipmentMax, dataService);
            }
        }
        return ret;
    }

    @RequestMapping(value = "/user/lock", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> lockUser(@RequestParam("userId") int userId,
            HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.returnEmptyHashMap();
        } else if (login == 0 || login == 2) {
            data.put("status", "prohibited");
        }

        MsalesLoginUserInf userLogin = LoginContext.getLogin(request);

        User user = dataService.getRowById(userId, User.class);
        if (user != null) {
            boolean error = false;
            if (user.getIsActive() == 0) {//dang khoa=> mo
                UserRole userRole = loginService.getUserRole(userId, dataService);
                if (userRole != null) {
                    if (checkMaxSalesUser(user, userRole.getId(), userLogin.getEquipmentMax())) {
                        error = true;
                        data.put("status", "OVERSALE");
                        data.put("saleMax", userLogin.getEquipmentMax());
                    }
                }
            }

            if (!error) {
                if (user.getIsActive() == 0) {
                    user.setIsActive(1);
                } else {
                    user.setIsActive(0);
                }
                user.setUpdatedAt(new Date());
                user.setUpdatedUser(userLogin.getId());
                int updated = dataService.updateRow(user);
                if (updated > 0) {
                    data.put("status", "OK");
                } else {
                    data.put("status", "FALSE");
                }
            }
        } else {
            data.put("status", "FALSE");
        }
        return data;
    }

    @RequestMapping(value = "/user/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> resetPassword(@RequestParam("userId") int userId,
            HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.returnEmptyHashMap();
        } else if (login == 0 || login == 2) {
            data.put("status", "prohibited");
        }
        MsalesLoginUserInf userLogin = LoginContext.getLogin(request);

        if (userLogin != null) {
            User user = dataService.getRowById(userId, User.class
            );
            if (user
                    != null && user.getId()
                    != null) {
                user.setPassword(MsalesSession.getSHA256(MsalesSession.DEFAULT_USER_PASS));
                user.setUpdatedAt(new Date());
                user.setUpdatedUser(userLogin.getId());
                int updated = dataService.updateRow(user);
                if (updated > 0) {
                    data.put("status", "OK");
                } else {
                    data.put("status", "FALSE");
                }
            } else {
                data.put("status", "FALSE");
            }
        }
        return data;
    }

    @RequestMapping(value = "/user/checkName", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> checkUserName(@RequestBody String username,
            HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.returnEmptyHashMap();
        }

        username = username.replaceAll(" ", "");
        boolean isExists = userServices.checkUsernameExists(username);
        if (isExists) {
            //ten hop le
            data.put("username", username);
        } else {
            //ten khong hop le
            boolean flag = false;
            int i = 1;
            String name = "", companyCode = "";
            if (username.split("@").length > 1) {
                name = username.split("@")[0];
                companyCode = username.split("@")[1];
            }

            while (!flag) {
                String uName = name + i++ + "@" + companyCode;
                boolean isOK = userServices.checkUsernameExists(uName);
                if (isOK) {
                    //ten hop le
                    data.put("username", uName);
                    flag = true;
                }
            }

        }
        return data;
    }

    @RequestMapping(value = "/settings/changepasswd", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView changePassWord(HttpServletRequest request, @ModelAttribute("PassForm") MsalesChangePassword changePassword) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }
        // if request is POST
        if ("POST".equals(request.getMethod())) {
            if (changePassword != null) {
                String passOLD = changePassword.getPasswordOld();
                String passNew = changePassword.getPasswordNew();
                if (passNew.length() >= 6 && passNew.length() >= 6) {
                    // user id that you want change pass
                    changePassword.setId(LoginContext.getLogin(request).getId());
                    // user that change pass
                    changePassword.setUpdatedUser(LoginContext.getLogin(request).getId());
                    // encrypt pass to SHA256
                    changePassword.setPasswordOld(MsalesSession.getSHA256(passOLD));
                    changePassword.setPasswordNew(MsalesSession.getSHA256(passNew));

                    // ensure  passwordNew lenght and passwordOld lenght > 0
                    if (changePassword.getPasswordNew()
                            .length() > 0 && changePassword.getPasswordOld().length() > 0) {
                        if (changePassword.getId() > 0) {
                            // Get user from database with request id.
                            User userFromDB = dataService.getRowById(changePassword.getId(), User.class
                            );
                            if (userFromDB
                                    != null) {
                                // encipt old password
                                //String passEncript = MsalesSession.getAccessToken(changePass.getPasswordOld());
                                // check password old with new
                                if (changePassword.getPasswordOld().equals(userFromDB.getPassword())) {
                                    // encript new password
                                    //passEncript = MsalesSession.getAccessToken(changePass.getPasswordNew());
                                    // set new password for user
                                    userFromDB.setPassword(changePassword.getPasswordNew());
                                    // set updated by user
                                    userFromDB.setUpdatedUser(changePassword.getUpdatedUser());
                                    // update password to database
                                    int updated = dataService.updateRow(userFromDB);
                                    if (updated > 0) {
                                        return new ModelAndView("changePasswordSuccess");
                                    }

                                } else {
                                    // Old password is not match with password in database.
                                    request.setAttribute("message", MsalesStatus.USER_PASSWORD_INCORRECT.getMessage());
                                }
                            } else {
                                // Id user request change password in not exists in database.
                                request.setAttribute("message", MsalesStatus.USER_NOT_EXIST.getMessage());
                            }
                        } else {
                            // Id user request change password in not exists in database.
                            request.setAttribute("message", MsalesStatus.USER_NOT_EXIST.getMessage());
                        }
                    }
                }
                // set agin pass for show to GUI
                changePassword.setPasswordOld(passOLD);
                changePassword.setPasswordNew(passNew);
            }
        }
        return new ModelAndView("changePassword", "PassForm", changePassword);
    }

    @InitBinder
    public void dataBinding(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
        binder
                .registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    private static List<UserRoleChannel> getListUserRoleChannel(HttpServletRequest request) throws Exception {
        List<UserRoleChannel> list = new ArrayList<>();
        String[] urcArray = request.getParameterValues("urc_channelId");

        SimpleDateFormat sdf = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
        if (urcArray != null) {
            for (String s : urcArray) {
                UserRoleChannel urc = new UserRoleChannel();

                int channelId;
                channelId = Integer.parseInt(s);
                String id = request.getParameter("urc_" + channelId);
                if (id != null && !id.trim().isEmpty()) {
                    urc.setId(Integer.parseInt(id));
                }
                String channelName = request.getParameter("urc_channelName_" + channelId);
                String name = request.getParameter("urc_name_" + channelId);
                String channelType = request.getParameter("urc_channelType_" + channelId);
                String beginDate = request.getParameter("beginAt_" + channelId);
                String endDate = request.getParameter("endAt_" + channelId);
                Date beginAt = null, endAt = null;
                if (beginDate != null && !beginDate.trim().isEmpty()) {
                    beginAt = sdf.parse(beginDate);
                }
                if (endDate != null && !endDate.trim().isEmpty()) {
                    endAt = sdf.parse(endDate);
                }

                Channel channel = new Channel();
                channel.setId(channelId);
                channel.setName(name);
                channel.setChannelName(channelName);
                ChannelType type = new ChannelType();
                type.setId(0);
                type.setName(channelType);
                channel.setChannelTypes(type);

                urc.setChannels(channel);
                urc.setBeginAt(beginAt);
                urc.setEndAt(endAt);

                list.add(urc);
            }
        }

        return list;
    }

    private Filter initData(MsalesLoginUserInf loginUser, User user, HttpServletRequest request, Model uiModel, List<UserGoodsCategory> userGoodsCategoryList) {

        String companyCode = loginUser.getCompanyCode();
        uiModel.addAttribute("companyId", loginUser.getCompanyId());
        uiModel.addAttribute("companyName", loginUser.getCompanyName());

        user.setCompanyId(loginUser.getCompanyId());

        //get ListGoodsCategory
        ParameterList parameterGoodsList = new ParameterList("companys.id", loginUser.getCompanyId());
        List<GoodsCategory> list = dataService.getListOption(GoodsCategory.class, parameterGoodsList);
        List<OptionItem> goodsCategoryList = OptionItem.NoOptionList(0, "-- Loại hàng hóa --");
        for (GoodsCategory goodsCategory : list) {
            goodsCategoryList.add(new OptionItem(goodsCategory.getId(), goodsCategory.getName()));
        }
        for (UserGoodsCategory selectedItem : userGoodsCategoryList) {
            for (OptionItem optionItem : goodsCategoryList) {
                if (selectedItem.getGoodsCategorys().getId().intValue() == optionItem.getId()) {
                    selectedItem.getGoodsCategorys().setName(optionItem.getName());
                    goodsCategoryList.remove(optionItem);
                    break;
                }
            }
        }

        uiModel.addAttribute("goodsCategoryList", goodsCategoryList);
        uiModel.addAttribute("selectedGoodsCategory", userGoodsCategoryList);
        //load channel

        Filter filter = Filter.processChannel(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());

        List<OptionItem> statusItemList = new ArrayList<>();
        ParameterList parameterStatusList = new ParameterList("statusTypes.id", 3, 0, 0);
        List<Status> statusList = dataService.getListOption(Status.class, parameterStatusList);
        for (Status status : statusList) {
            statusItemList.add(new OptionItem(status.getId(), status.getName()));
        }

        uiModel.addAttribute(
                "statusList", statusItemList);

        uiModel.addAttribute("user", user);
        uiModel.addAttribute("companyCode", companyCode);

        List<OptionItem> roleList = OptionItem.NoOptionList(0, "-- Chọn vai trò --");
        // get user role channel
        ParameterList parameterList = new ParameterList();
        // get List UserRole from DB
        List<UserRole> listUserRole = dataService.getListOption(UserRole.class, parameterList);
        for (UserRole userRole : listUserRole) {
            if (!userRole.getCode().equalsIgnoreCase("USER_ROLE_ADMIN_COMPANY")) {
                roleList.add(new OptionItem(userRole.getId(), userRole.getName()));
            }
        }

        uiModel.addAttribute(
                "userRoleList", roleList);
        return filter;
    }

    private int executAddUser(CreateUserAdmin createUserAdmin, HttpServletRequest request) {
        int userId = 0;
        try {
            //set lai cac truong bat buoc
            createUserAdmin.getUser().setCreatedAt(new Date());
            createUserAdmin.getUser().setUpdatedUser(0);
            createUserAdmin.getUser().setDeletedUser(createUserAdmin.getUser().getCreatedUser());

            // insert new a user to database
            userId = dataService.insert(createUserAdmin.getUser());
            if (userId > 0) {
                SalesStock salesStock = new SalesStock();
                salesStock.setSalemanUserId(userId);
                salesStock.setCreatedUser(createUserAdmin.getUser().getCreatedUser());
                salesStock.setUpdatedUser(0);
                salesStock.setDeletedUser(0);
                // by default is active
                salesStock.setStatusId(1);

                User user = new User();
                user.setId(userId);

                //set user for userGoodsCategory
                if (createUserAdmin.getUserGoodsCategoryList() != null) {
                    for (UserGoodsCategory userGoodsCategory : createUserAdmin.getUserGoodsCategoryList()) {
                        userGoodsCategory.setUsers(user);
                        userGoodsCategory.setCreatedAt(new Date());
                        userGoodsCategory.setDeletedUser(0);
                        userGoodsCategory.setUpdatedUser(0);
                        userGoodsCategory.setCreatedUser(LoginContext.getLogin(request).getId());
                        userGoodsCategory.setStatusId(1);
                    }
                }

                //set user for userRoleChannel
                for (UserRoleChannel userRoleChannel : createUserAdmin.getUserRoleChannelList()) {
                    userRoleChannel.setUserId(userId);
                    userRoleChannel.setCreatedAt(new Date());
                    userRoleChannel.setDeletedUser(0);
                    userRoleChannel.setUpdatedUser(0);
                    userRoleChannel.setCreatedUser(LoginContext.getLogin(request).getId());
                    userRoleChannel.setUserRoleId(createUserAdmin.getUser().getUserRole().getId());
                }

                ArrayList insertList = new ArrayList();

                insertList.add(salesStock);
                insertList.addAll(createUserAdmin.getUserRoleChannelList());
                insertList.addAll(createUserAdmin.getUserGoodsCategoryList());

                //set lai user not deleted
                createUserAdmin.getUser().setDeletedUser(0);
                createUserAdmin.getUser().setId(userId);
                insertList.add(createUserAdmin.getUser());

                dataService.insertOrUpdateArray(insertList);
            }
        } catch (Exception e) {
            System.err.println(">>: " + e.getMessage());
        }
        return userId;
    }

    private boolean updateUser(CreateUserAdmin createUserAdmin, HttpServletRequest request) {
        boolean isUpdated = false;
        if (createUserAdmin != null) {
            try {
                int createdUser = createUserAdmin.getUser().getCreatedUser();
                User rootUser = dataService.getRowById(createUserAdmin.getUser().getId(), User.class
                );
                if (rootUser
                        != null) {
                    createUserAdmin.getUser().setPassword(rootUser.getPassword());
                    createUserAdmin.getUser().setCreatedUser(rootUser.getCreatedUser());
                    createUserAdmin.getUser().setUpdatedUser(createdUser);
                }

                //danh sach UserRoleChannel
                List<UserRoleChannel> oldUserRoleChannelList = new ArrayList<>();
                List<UserRoleChannel> newUserRoleChannelList = new ArrayList<>();

                for (UserRoleChannel userRoleChannel
                        : createUserAdmin.getUserRoleChannelList()) {
                    userRoleChannel.setUserId(createUserAdmin.getUser().getId());
                    userRoleChannel.setCreatedUser(LoginContext.getLogin(request).getId());
                    userRoleChannel.setUserRoleId(createUserAdmin.getUser().getUserRole().getId());
                    if (userRoleChannel.getId() != null) {
                        userRoleChannel.setCreatedUser(createdUser);
                        userRoleChannel.setDeletedUser(0);
                        userRoleChannel.setUpdatedUser(createdUser);
                        oldUserRoleChannelList.add(userRoleChannel);
                    } else {
                        userRoleChannel.setCreatedUser(createdUser);
                        userRoleChannel.setDeletedUser(0);
                        userRoleChannel.setUpdatedUser(0);
                        newUserRoleChannelList.add(userRoleChannel);
                    }
                }

                userServices.updateUserRoleChannel(createUserAdmin.getUser(), oldUserRoleChannelList);

                List<UserGoodsCategory> oldUserGoodsCategorList = new ArrayList<>();
                List<UserGoodsCategory> newUserGoodsCategorList = new ArrayList<>();

                if (createUserAdmin.getUserGoodsCategoryList()
                        != null) {
                    for (UserGoodsCategory userGoodsCategory : createUserAdmin.getUserGoodsCategoryList()) {
                        userGoodsCategory.setUserId(createUserAdmin.getUser().getId());
                        userGoodsCategory.setCreatedUser(LoginContext.getLogin(request).getId());
                        userGoodsCategory.setStatusId(1);
                        if (userGoodsCategory.getId() != null) {
                            userGoodsCategory.setCreatedUser(createdUser);
                            userGoodsCategory.setDeletedUser(0);
                            userGoodsCategory.setUpdatedUser(createdUser);
                            oldUserGoodsCategorList.add(userGoodsCategory);
                        } else {
                            userGoodsCategory.setStatusId(15);//mac dinh
                            userGoodsCategory.setDeletedUser(0);
                            userGoodsCategory.setUpdatedUser(0);
                            newUserGoodsCategorList.add(userGoodsCategory);
                        }
                    }
                }

                userServices.updateUserGoodsCategory(createUserAdmin.getUser(), oldUserGoodsCategorList);

                ArrayList insertList = new ArrayList();

                insertList.addAll(newUserGoodsCategorList);

                insertList.addAll(oldUserRoleChannelList);

                insertList.addAll(newUserRoleChannelList);

                createUserAdmin.getUser()
                        .setDeletedUser(0);
                insertList.add(createUserAdmin.getUser());

                dataService.insertOrUpdateArray(insertList);
                isUpdated = true;
            } catch (Exception ex) {
                isUpdated = false;
                System.err.println(">>>updateUser: " + ex.getMessage());
            }
        }
        return isUpdated;
    }

    /**
     * Path of the file to be downloaded, relative to application's directory
     */
    private String separator = File.separator;
    private String filePath = String.format("%sdownloads%simports%s", separator, separator, separator);

    @RequestMapping(value = "/user/download.do")
    public void downloadTemplets(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }

        MsalesDownloadTemplet downloadTemplet = new MsalesDownloadTemplet(filePath, request, response);
        downloadTemplet.getFile("Danh_sach_User.xls");
    }

    @RequestMapping(value = "/user/import", method = RequestMethod.POST)
    private String importUser(HttpServletRequest request, @RequestParam("file_user") MultipartFile multipartFile) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        List<List<Object>> listObj = getListDataFromExel(multipartFile);
        if (!listObj.isEmpty()) {
            List<MsalesUserImport> userImports = getListUserImport(listObj);
            if (!userImports.isEmpty()) {

                List<MsalesUserImport> validateChannelList = validateChannel(userImports, loginUserInf);
                List<MsalesUserImport> validateLocationList = validateLocationCode(userImports);
                List<MsalesUserImport> validateUserRoleList = validateUserRole(userImports);
                List<MsalesUserImport> validateStatusList = validateStatus(userImports);
                List<MsalesUserImport> validateUserNameList = validateUserName(userImports, loginUserInf.getCompanyCode());

                boolean validateChannelEmpty = validateChannelList.isEmpty();
                boolean validateLocationEmpty = validateLocationList.isEmpty();
                boolean validateUserRoleEmpty = validateUserRoleList.isEmpty();
                boolean validateStatusEmpty = validateStatusList.isEmpty();
                boolean validateUserNameEmpty = validateUserNameList.isEmpty();

                if (!validateChannelEmpty || !validateLocationEmpty || !validateUserRoleEmpty || !validateStatusEmpty || !validateUserNameEmpty) {

                    request.setAttribute("channelList", validateChannelList);
                    request.setAttribute("locationList", validateLocationList);
                    request.setAttribute("userRoleList", validateUserRoleList);
                    request.setAttribute("statusList", validateStatusList);
                    request.setAttribute("userNameList", validateUserNameList);

                    request.setAttribute("channelError", validateChannelEmpty);
                    request.setAttribute("locationError", validateLocationEmpty);
                    request.setAttribute("userRoleError", validateUserRoleEmpty);
                    request.setAttribute("statusError", validateStatusEmpty);
                    request.setAttribute("userNameError", validateUserNameEmpty);

                    request.setAttribute("companyCode", loginUserInf.getCompanyCode());
                    request.setAttribute("updated", false);
                    request.setAttribute("error", true);
                } else {
                    List<User> listUser = getUserFromUserImport(userImports, loginUserInf);
                    ArrayList userNameList = new ArrayList();
                    for (User user : listUser) {
                        String userName = user.getUsername();
                        ParameterList paramList = new ParameterList(1, 1);
                        paramList.add("username", userName);
                        List<User> userAdds = dataService.getListOption(User.class, paramList);
                        if (!userAdds.isEmpty()) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("userName", user.getUsername());
                            hashMap.put("name", user.getLastName() + " " + user.getFirstName());
                            userNameList.add(hashMap);
                        }
                    }
                    if (!userNameList.isEmpty()) {
                        request.setAttribute("userErrorList", userNameList);
                        return "userImport";
                    }
                    try {
                        // insert user
                        List<Integer> userIds = dataService.insertArray(listUser);
                        if (!userIds.isEmpty()) {
                            List<SalesStock> stocks = getSalesStock(userIds, loginUserInf);
                            if (!stocks.isEmpty()) {
                                // insert salestock for user
                                List<Integer> stockIds = dataService.insertArray(stocks);
                            }
                            List<UserRoleChannel> userRoleChannels = getUserRoleChannel(userIds, loginUserInf);
                            if (!userRoleChannels.isEmpty()) {
                                // insert user role channel for user
                                List<Integer> userRoleChannelIds = dataService.insertArray(userRoleChannels);
                            }
                        }
                        request.setAttribute("updated", true);
                    } catch (Exception e) {
                        System.err.println(">>>importUser: " + e.getMessage());
                    }
                }
            } else {
                request.setAttribute("updated", false);
            }
        } else {
            request.setAttribute("updated", false);
        }

        request.setAttribute("submited", true);
        return "userImport";
    }

    @RequestMapping(value = "/user/import", method = RequestMethod.GET)
    private String importUser(HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        return "userImport";
    }

    private List<UserRoleChannel> getUserRoleChannel(List<Integer> userIds, MsalesLoginUserInf loginUserInf) {
        List<UserRoleChannel> userRoleChannels = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i++) {
            UserRoleChannel urc = new UserRoleChannel();

            urc.setUserRoleId(mListUserRole.get(i).getId());
            urc.setChannelId(mListChannel.get(i).getId());
            urc.setUserId(userIds.get(i));
            urc.setCreatedUser(loginUserInf.getId());
            urc.setBeginAt(new Date());
            urc.setEndAt(null);

            userRoleChannels.add(urc);

        }
        return userRoleChannels;
    }

    private List<SalesStock> getSalesStock(List<Integer> userIds, MsalesLoginUserInf loginUserInf) {
        List<SalesStock> soSalesStocks = new ArrayList<>();
        for (Integer userId : userIds) {
            SalesStock salesStock = new SalesStock();
            salesStock.setSalemanUserId(userId);
            salesStock.setCreatedUser(loginUserInf.getId());
            salesStock.setStatusId(1);// is active

            soSalesStocks.add(salesStock);
        }
        return soSalesStocks;
    }

    private List<User> getUserFromUserImport(List<MsalesUserImport> userImports, MsalesLoginUserInf loginUserInf) {
        List<User> listUser = new ArrayList<>();
        for (int i = 0; i < userImports.size(); i++) {
            MsalesUserImport userImport = userImports.get(i);
            User user = new User();

            user.setCompanyId(loginUserInf.getCompanyId());
            user.setUserCode(userImport.getUserName());
            user.setPassword(MsalesSession.getSHA256(MsalesSession.DEFAULT_USER_PASS));
            user.setLastName(userImport.getLastName());
            user.setFirstName(userImport.getFirstName());
            user.setBirthday(userImport.getBirthday());
            user.setEmail(userImport.getEmail());
            user.setAddress(userImport.getAddress());
            user.setTel(userImport.getMobilePhone());
            user.setStatusId(getStatusId(userImport.getStatus()));//FIXME: get status id from status value
            user.setIsActive(1);
            user.setSex(true);
            try {
                Double sex = Double.parseDouble(userImport.getSex());
                if (sex.compareTo(Double.parseDouble("2")) == 0) {
                    user.setSex(false);
                }
            } catch (Exception ex) {
            }
            user.setCreatedUser(loginUserInf.getId());
            user.setLocationId(mListLocation.get(i).getId());
            // get user name
//            String fullname = getNameLetter(user.getLastName().trim().split(" ")) + user.getFirstName().trim();
//            fullname = removeAccent(fullname);
//            String userName = genUname(fullname + "@" + loginUserInf.getCompanyCode());
            user.setUsername(userImport.getUserName());

            listUser.add(user);

        }

        return listUser;
    }

    private Integer getStatusId(int statusValue) {
        try {
            ParameterList param = new ParameterList(1, 1);
            param.add("statusTypes.id", 3);
            param.add("value", String.valueOf(statusValue));
            List<Status> statusFound = dataService.getListOption(Status.class, param);
            if (!statusFound.isEmpty()) {
                return statusFound.get(0).getId();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private String getNameLetter(String[] firtname) {
        String firstLetter = "";
        for (String name : firtname) {
            firstLetter += name.substring(0, 1);
        }
        return firstLetter;
    }

    private List<List<Object>> getListDataFromExel(MultipartFile multipartFile) {
        MsalesExcelConfig excelConfig = new MsalesExcelConfig(12, 0, multipartFile);
        MsalesReadExcel msleExcel = new MsalesReadExcel(excelConfig);
        List<List<Object>> posList = msleExcel.readExcel();
        return posList;
    }

    private List<MsalesUserImport> getListUserImport(List<List<Object>> listObj) {
        List<MsalesUserImport> userImports = new ArrayList<>();
        for (List<Object> obj : listObj) {
            MsalesUserImport userImport = new MsalesUserImport();
            for (int i = 0; i < obj.size(); i++) {
                switch (i) {
                    case 0:
                        userImport.setUserName(obj.get(0).toString());
                        break;
                    case 1:
                        userImport.setChannelCode(obj.get(1).toString());
                        break;
                    case 2:
                        userImport.setLocationCode(obj.get(2).toString());
                        break;
                    case 3:
                        userImport.setRoleUserCode(obj.get(3).toString());
                        break;
                    case 4:
                        userImport.setLastName(obj.get(4).toString());
                        break;
                    case 5:
                        userImport.setFirstName(obj.get(5).toString());
                        break;
                    case 6:
                        userImport.setBirthday(getDate(obj.get(6).toString()));
                        break;
                    case 7:
                        userImport.setSex(obj.get(7).toString());
                        break;
                    case 8:
                        userImport.setEmail(obj.get(8).toString());
                        break;
                    case 9:
                        userImport.setMobilePhone(obj.get(9).toString());
                        break;
                    case 10:
                        userImport.setAddress(obj.get(10).toString());
                        break;
                    case 11:
                        userImport.setStatus(getStatus(obj.get(11).toString()));
                        break;
                }
            }
            userImports.add(userImport);
        }
        return userImports;
    }

    private Integer getStatus(String status) {
        try {
            status = status.trim();
            if (status.length() > 0) {
                if (status.indexOf(".") > 0) {
                    return (int) Double.parseDouble(status);
                } else {
                    return Integer.parseInt(status);
                }
            }
        } catch (NumberFormatException e) {
            return 1;
        }
        return 1;
    }

    private Integer getSex(String status) {
        try {
            return Integer.parseInt(status);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Date getDate(String dateString) {
        try {
            Locale lo = new Locale("vi", "VN");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", lo);
            Date date = simpleDateFormat.parse(dateString);

            return date;

        } catch (ParseException ex) {
            Logger.getLogger(WebPointOfSaleController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<MsalesUserImport> validateChannel(List<MsalesUserImport> channelImports, MsalesLoginUserInf loginUserInf) {
        List<MsalesUserImport> errorUser = new ArrayList<>();
        mListChannel = new ArrayList<>();
        for (MsalesUserImport userImport : channelImports) {
            Channel channel = channelService.getChannelByCode(userImport.getChannelCode(), loginUserInf.getCompanyId(), dataService);
            if (channel == null) {
                channel = channelService.getChannelByFullCode(userImport.getChannelCode(), loginUserInf.getCompanyId(), dataService);
            }
            if (channel == null) {
                errorUser.add(userImport);
            } else {
                mListChannel.add(channel);
            }
        }
        return errorUser;
    }

    private List<MsalesUserImport> validateLocationCode(List<MsalesUserImport> channelImports) {
        List<MsalesUserImport> locationError = new ArrayList<>();
        mListLocation = new ArrayList<>();
        for (MsalesUserImport userImport : channelImports) {
            ParameterList param = new ParameterList(1, 1);
            param.add("code", userImport.getLocationCode());
            List<Location> locationFound = dataService.getListOption(Location.class, param);
            if (locationFound.isEmpty()) {
                locationError.add(userImport);
            } else {
                mListLocation.add(locationFound.get(0));
            }
        }
        return locationError;
    }

    private List<MsalesUserImport> validateUserRole(List<MsalesUserImport> userImports) {
        List<MsalesUserImport> userRoleError = new ArrayList<>();
        mListUserRole = new ArrayList<>();
        for (MsalesUserImport userImport : userImports) {
            ParameterList param = new ParameterList(1, 1);
            param.add("code", "USER_ROLE_" + userImport.getRoleUserCode().toUpperCase());
            List<UserRole> roleFound = dataService.getListOption(UserRole.class, param);
            if (roleFound.isEmpty()) {
                userRoleError.add(userImport);
            } else {
                mListUserRole.add(roleFound.get(0));
            }
        }

        return userRoleError;
    }

    /**
     * Check username have existed in database and autogen username from first
     * name and last name
     * <p>
     * @param userName is username you want to check
     * <p>
     * @return is new username
     */
    private String genUname(String userName) {
        String u = "";
        boolean isExists = userServices.checkUsernameExists(userName);
        if (isExists) {
            //user name is avalid
            u = userName;
        } else {
            //user name is invalid
            boolean flag = false;
            int i = 1;
            String name = "", companyCode = "";
            if (userName.split("@").length > 1) {
                name = userName.split("@")[0];
                companyCode = userName.split("@")[1];
            }

            while (!flag) {
                String uName = name + i++ + "@" + companyCode;
                boolean isOK = userServices.checkUsernameExists(uName);
                if (isOK) {
                    //user name is avalid
                    flag = true;
                    u = uName;
                }
            }
        }
        return u;
    }

    /**
     * This method remove all all accent
     * <p>
     * @param str is text you want to remove.
     * <p>
     * @return text have removed accent
     */
    private String removeAccent(String str) {
        return Normalizer.normalize(str.toLowerCase().trim(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    private List<MsalesUserImport> validateStatus(List<MsalesUserImport> channelImports) {
        List<MsalesUserImport> statusErrorList = new ArrayList<>();
        mListStatus = new ArrayList<>();
        for (MsalesUserImport channelImport : channelImports) {
            ParameterList param = new ParameterList(1, 1);
            param.add("statusTypes.id", 3);
            param.add("value", String.valueOf(channelImport.getStatus()));
            List<Status> locationFound = dataService.getListOption(Status.class, param);
            if (locationFound.isEmpty()) {
                statusErrorList.add(channelImport);
            } else {
                mListStatus.add(locationFound.get(0));
            }
        }
        return statusErrorList;
    }

    private List<MsalesUserImport> validateUserName(List<MsalesUserImport> channelImports, String companyCode) {
        List<MsalesUserImport> userNameErrorList = new ArrayList<>();
        for (MsalesUserImport channelImport : channelImports) {
            String[] code = channelImport.getUserName().split("@");
            if (code.length != 2 || !companyCode.toLowerCase().equals(code[1].toLowerCase())) {
                userNameErrorList.add(channelImport);
            }
        }
        return userNameErrorList;
    }
}
