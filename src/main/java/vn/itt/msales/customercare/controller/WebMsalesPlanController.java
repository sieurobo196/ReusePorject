package vn.itt.msales.customercare.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import vn.itt.msales.app.service.AppService;
import vn.itt.msales.app.service.DateUtils;
import vn.itt.msales.app.service.SalesSupService;
import vn.itt.msales.channel.controller.MsalesChannelController;
import vn.itt.msales.channel.controller.MsalesLocationController;
import vn.itt.msales.channel.controller.MsalesPOSController;
import vn.itt.msales.channel.services.WebLocationService;
import vn.itt.msales.common.Gen2Image2;
import vn.itt.msales.common.MsalesDownloadTemplet;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.common.Utilities;
import vn.itt.msales.common.ValidatorUtil;
import vn.itt.msales.common.WebMsalesComparatorLocation;
import vn.itt.msales.customercare.model.DayOfWeek;
import vn.itt.msales.customercare.model.MsalesPlanImport;
import vn.itt.msales.customercare.model.WebPlanForm;
import vn.itt.msales.customercare.services.MCPService;
import vn.itt.msales.customercare.services.POSService;
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.MCPDetails;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.services.ExcelService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;
import vn.itt.msales.user.model.URLManager;
import vn.itt.msales.workflow.controller.MsalesMCPController;
import vn.itt.msales.workflow.controller.MsalesMCPDetailsController;
import vn.itt.msales.workflow.service.ServiceMCP;

@Controller
public class WebMsalesPlanController {

    @Autowired
    private Validator planValidator;

    @Autowired
    MsalesLocationController locationController;

    @Autowired
    MsalesPOSController posController;

    @Autowired
    MsalesMCPController mcpContronller;

    @Autowired
    MsalesMCPDetailsController mcpDetailController;

    @Autowired
    MsalesChannelController channelController;

    @Autowired
    View jsonView;

    @Autowired
    private DataService dataService;

    @Autowired
    private MCPService mcpService;

    @Autowired
    private ServiceMCP serviceMCP;

    @Autowired
    private WebLocationService locationService;

    @Autowired
    private ServiceFilter serviceFilter;

    @Autowired
    POSService posService;

    @Autowired
    private SalesSupService salesSupService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private AppService appService;

    Map<String, String> userList = new LinkedHashMap<>();
    private List<User> listUser;
    private List<POS> listPOS;

    /**
     * Path of the file to be downloaded, relative to application's directory
     */
    String separator = File.separator;
    private String filePath = String.format("%sdownloads%simports%s", separator, separator, separator);

    @RequestMapping(value = "/plan/download.do")
    public void downloadTemplets(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }

        MsalesDownloadTemplet downloadTemplet = new MsalesDownloadTemplet(filePath, request, response);
        downloadTemplet.getFile("PLAN_IMPORT.xlsx");
    }

    @RequestMapping(value = "/plan/create", method = RequestMethod.GET)
    public String init(Model model, HttpServletRequest request) {

        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        //  userInf.getChannelList();
        // User list
        model.addAttribute("isCreated", true);
        initializeStaffList(request, model, userInf.getCompanyId(), new Integer[]{6});

        // Province list
        if (userInf.getUserRoleCode().equals("USER_ROLE_ADMIN_COMPANY")) {
            model.addAttribute("provinceList", locationService.getListLocationByType(1));
        } else {
            model.addAttribute("provinceList", locationService.getListLocationByListChannelId(userInf.getChannelList()));
        }
        //    

        WebPlanForm plan = new WebPlanForm();
        plan.setEstablistId(1);
        model.addAttribute("planForm", plan);
        model.addAttribute("tempPos", "");
        plan.setBeginDate(ValidatorUtil.parseParamaterDate(new Date()));
        model.addAttribute("tempPos", Arrays.toString(plan.getTempPos().toArray()));

        return "planPage";
    }

    private void initializeStaffList(HttpServletRequest request, Model model, int companyId, Integer[] roles) {
        if (LoginContext.getLogin(request).getUserRoleCode().equals("USER_ROLE_ADMIN_COMPANY")) {
            List<OptionItem> staffList = serviceFilter.getCbListUserByChannel(0, companyId, LoginContext.getLogin(request).getId(), roles, dataService);
            model.addAttribute("staffList", staffList);
        } else {
            List<OptionItem> staffList = serviceFilter.getCbListUserByChannelOptionList(LoginContext.getLogin(request).getChannelList(), companyId, LoginContext.getLogin(request).getId(), roles, dataService);
            model.addAttribute("staffList", staffList);
        }

        model.addAttribute("provinceList", serviceFilter.getCbListLocationByType(1, dataService));
    }

    @RequestMapping(value = "/plan/create", method = RequestMethod.POST)
    public String submitPlan(
            @ModelAttribute("planForm") @Valid WebPlanForm plan,
            BindingResult result, Model model, HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        model.addAttribute("isCreated", true);

        try {
            postNewPlan(plan, result, model, request);
            if (!result.hasErrors()) {
                model.addAttribute("success", true);
            }
        } catch (Exception e) {
            //new FunctionException(getClass(), e);
            Logger.getLogger(WebMsalesPlanController.class.getName()).log(Level.SEVERE, null, e);
            model.addAttribute("success", false);
        }

        return "planPage";
    }

    @RequestMapping(value = "/plan/copy/{id}", method = RequestMethod.GET)
    public String copyPlan(@PathVariable("id") Integer keHoachId,
            HttpServletRequest request, Model model) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        WebPlanForm plan = new WebPlanForm();
        try {
            MCP mcp = mcpService.getMCPById(keHoachId);
            plan.setMcpName(mcp.getName());
            plan.setEstablistId(mcp.getCreatedUser());
            plan.setImplementId(mcp.getImplementEmployees().getId());
            plan.setBeginDate(ValidatorUtil.parseParamaterDate(mcp.getBeginDate()));

            List<MCPDetails> listMcpDetail = mcpService.getListMCPDetailByMcpId(keHoachId);
            plan.setKeHoachChiTiets(listMcpDetail);

            initCopyPlan(plan.getKeHoachChiTiets(), plan, new WebPlanForm(), model, request);

        } catch (Exception ex) {
            Logger.getLogger(WebMsalesPlanController.class.getName()).log(Level.SEVERE, null, ex);
            return "notFoundError";
        }

        return "planPage";
    }

    @RequestMapping(value = "/plan/copy/{id}", method = RequestMethod.POST)
    public String submitCopyPlan(
            @ModelAttribute("planForm") @Valid WebPlanForm kehoach,
            BindingResult result, Model model, HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        System.out.println("**************** Submit copy plan");
        try {
            postNewPlan(kehoach, result, model, request);
            if (!result.hasErrors()) {
                model.addAttribute("success", true);
            }
        } catch (Exception e) {
            model.addAttribute("success", false);
        }
        return "planPage";
    }

    @RequestMapping(value = "/plan/edit/{id}", method = RequestMethod.GET)
    public String editPlan(@PathVariable("id") int keHoachId,
            HttpServletRequest request, Model model) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        model.addAttribute("isUpdate", true);

        try {
            WebPlanForm plan = new WebPlanForm();
            MCP mcp = mcpService.getMCPById(keHoachId);
            if (mcp != null) {
                plan.setMcpId(mcp.getId());
                plan.setMcpName(mcp.getName());
                plan.setEstablistId(mcp.getCreatedUser());
                plan.setImplementId(mcp.getImplementEmployees().getId());
                plan.setBeginDate(ValidatorUtil.parseParamaterDate(mcp.getBeginDate()));
            }

            List<MCPDetails> listMcpDetail = mcpService.getListMCPDetailByMcpId(keHoachId);
            if (listMcpDetail != null) {
                plan.setKeHoachChiTiets(listMcpDetail);
            }

            initPlanForm(plan, plan.getKeHoachChiTiets(), model, request);
        } catch (Exception ex) {
            Logger.getLogger(WebMsalesPlanController.class.getName()).log(Level.SEVERE, null, ex);
            return "notFoundError";
        }

        return "planPage";
    }

    @RequestMapping(value = "/plan/edit/{id}", method = RequestMethod.POST)
    public String submitEditPlan(
            @ModelAttribute("planForm") @Valid WebPlanForm kehoach,
            BindingResult result, Model model, HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        model.addAttribute("isUpdate", true);

        //System.out.println("**************** Submit Edit Kehoach");
        ArrayList<String> listDiemBH = (ArrayList<String>) kehoach
                .getListDiemBH();
        ArrayList<String> listDiemBHSelect = (ArrayList<String>) kehoach
                .getListDiemBHSelect();
        planValidator.validate(kehoach, result);
        //validateInputData(kehoach, result, model);
        if (result.hasErrors()) {
            // System.out.println("BindingResult:" + result);
            //initPlanForm(kehoach, listDiemBH, listDiemBHSelect, model);
        } else {
            try {
                List<Integer> intList = new ArrayList<>();
                for (String s : listDiemBHSelect) {
                    intList.add(Integer.valueOf(s));
                }
                kehoach.setTempPos(intList);
                saveEditPlan(kehoach, model, request);

            } catch (Exception e) {
                model.addAttribute("errorMessage", "Cập nhật không thành công.");
                model.addAttribute("success", false);
            }
        }
        initPlanForm(kehoach, listDiemBH, listDiemBHSelect, model, request);
        return "planPage";
    }

    @RequestMapping(value = "/plan/view/{id}")
    public String viewPlan(@PathVariable("id") int id,
            HttpServletRequest request, Model uiModel,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 5;
        }
        int maxPages = 1;
        //Lấy dữ liệu
        MCP mcp = dataService.getRowById(id, MCP.class);
        if (mcp != null) {
            mcp.getImplementEmployees().setName(mcp.getImplementEmployees().getLastName() + " " + mcp.getImplementEmployees().getFirstName());

            //Tìm người tạo
            int createdUserId = mcp.getCreatedUser();
            User user = dataService.getRowById(createdUserId, User.class);
            if (user != null) {
                mcp.setNameAdmin(user.getLastName() + " " + user.getFirstName());
            } else {
                mcp.setNameAdmin("Admin");
            }
            //Tìm người updated
            int updatedUserId = mcp.getUpdatedUser();
            if (updatedUserId > 0) {
                User user2 = dataService.getRowById(updatedUserId, User.class);
                if (user2 != null) {
                    mcp.setNameUpdatedUser(user2.getLastName() + " " + user2.getFirstName());
                } else {
                    mcp.setNameUpdatedUser("Admin");
                }
            }

            //Check số điểm bán hàng
            //Xử lí thời điểm hiện tại
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            //Lưu thời điểm hiện tại.
            Date dateCurrent = mcp.getBeginDate();
            cal.setTime(dateCurrent);
            //Lưu thời điểm ngày đầu tiên của tháng
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Date date2 = cal.getTime();
            String dateString = dateFormat.format(date2);
            //Xử lí ngày cuối tháng
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(dateCurrent);
            calendar2.add(Calendar.MONTH, 1);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            calendar2.add(Calendar.DAY_OF_MONTH, -1);

            //Tính số ngày còn lại
            int ngayConLai = calendar2.getTime().getDate() - mcp.getBeginDate().getDate() + 1;

            String endDateOfMonth = dateFormat.format(calendar2.getTime()) + " 23:59:59";
            if (mcp.getImplementEmployees().getId() > 0) {
                String hql = "Select U.id from POS as U where deletedUser =0 and  createdAt >= '" + dateString + "' and createdUser = " + mcp.getImplementEmployees().getId() + " and createdAt <= '" + endDateOfMonth + "'";
                List<POS> pos = dataService.executeSelectHQL(POS.class, hql, true, 0, 0);
                mcp.setNewPOSComplete(pos.size());
            } else {
                mcp.setNewPOSComplete(0);
            }
            //Tính doanh số trọng tâm...
            //Xét số ngày làm việc trong tháng
            // int[] ngayLamViec = {25,16,26,25,25,25,26,25,25,26,26,25};
            ParameterList pList = new ParameterList();
            pList.add("implementEmployees.id", mcp.getImplementEmployees().getId());
            pList.add("type", 2);
            pList.add("beginDate", date2, ">=");
            pList.add("beginDate", calendar2.getTime(), "<");
            List<MCP> mcps = dataService.getListOption(MCP.class, pList);
            long salesPerMonth = 0, salesFocusPerMonth = 0;
            int newPOSTarget = 0;
            for (MCP m : mcps) {
                if (m.getSalesPerMonth() != null) {
                    salesPerMonth += m.getSalesPerMonth();
                }
                if (m.getSalesFocusPerMonth() != null) {
                    salesFocusPerMonth += m.getSalesFocusPerMonth();
                }
                newPOSTarget += m.getNewPOS();
            }
            //lấy tổng doanh số
            mcp.setSalesPerMonth(salesPerMonth);
            mcp.setSalesFocusPerMonth(salesFocusPerMonth);
            mcp.setNewPOS(newPOSTarget);

            //Xét chỉ tiêu doanh số /ngay
            if (ngayConLai <= 0) {
                ngayConLai = 1;
            }
            long totalSold = 0, totalSoldFocus = 0, dsSalesPerDay = 0, totalSoldDateFocus = 0;

            String hqlS = salesSupService.getStringHqlOfSalesTransDetails(mcp.getImplementEmployees().getId(), dateString, endDateOfMonth);
            List<HashMap> salesTransDetails = new ArrayList<HashMap>();
            try {
                salesTransDetails = dataService.executeSelectHQL(HashMap.class, hqlS, true, 0, 0);
            } catch (Exception ex) {

            }
            String hqlS2 = salesSupService.getStringHqlOfSalesTransDetailsForSalesSup(mcp.getImplementEmployees().getId(), dateString, endDateOfMonth);
            List<HashMap> salesTransDetails2 = new ArrayList<HashMap>();
            try {
                salesTransDetails2 = dataService.executeSelectHQL(HashMap.class, hqlS2, true, 0, 0);
            } catch (Exception ex) {

            }
            if (!salesTransDetails2.isEmpty()) {
                salesTransDetails.addAll(salesTransDetails2);
            }

            String hqlSalesOrder = salesSupService.getStringHqlForSalesOrder(mcp.getImplementEmployees().getId(), dateString, endDateOfMonth);
            List<HashMap> salesOrderList = dataService.executeSelectHQL(HashMap.class, hqlSalesOrder, true, 0, 0);
            if (!salesOrderList.isEmpty()) {
                salesTransDetails.addAll(salesOrderList);
            }
            if (!salesTransDetails.isEmpty()) {

                for (HashMap sTransDetails : salesTransDetails) {
                    //Xử lí số tiền bán được trong ngày.
                    if (mcp.getBeginDate().getDate() == ((Timestamp) sTransDetails.get("date")).getDate()) {
                        dsSalesPerDay += (Integer) sTransDetails.get("quantity") * (Integer) sTransDetails.get("price");

                        if ((Boolean) sTransDetails.get("isFocus")) {
                            totalSoldDateFocus += (Integer) sTransDetails.get("quantity") * (Integer) sTransDetails.get("price");
                        }
                    }

                    //Xử lí tổng số tiền đã bán được trong tháng
                    totalSold += (Integer) sTransDetails.get("quantity") * (Integer) sTransDetails.get("price");
                    //   totalBanDuoc += (Integer) sTransDetails.get("quantity") * (Integer) sTransDetails.get("price");
                    //xử lí tổng số tiền trọng tâm đã bán được trong tháng
                    if ((Boolean) sTransDetails.get("isFocus")) {
                        totalSoldFocus += (Integer) sTransDetails.get("quantity") * (Integer) sTransDetails.get("price");
                    }
                }

            }

            //Xét chi tiêu doanh số trên ngày
            if (mcp.getSalesPerMonth() > totalSold) {
                mcp.setDsTargetPerDate((mcp.getSalesPerMonth() - totalSold) / ngayConLai);
            } else {
                mcp.setDsTargetPerDate((long) 0);
            }

            if (mcp.getSalesFocusPerMonth() > totalSoldFocus) {
                mcp.setDsTargetFocusPerDate((mcp.getSalesFocusPerMonth() - totalSoldFocus) / ngayConLai);
            } else {
                mcp.setDsTargetFocusPerDate((long) 0);
            }

            //Xét doanh số đã bán trong ngày
            mcp.setDsPerMonth(totalSold);
            mcp.setDsFocusPerMonth(totalSoldFocus);
            mcp.setDsPerDate(dsSalesPerDay);
            mcp.setDsFocusPerDate(totalSoldDateFocus);

            ParameterList parameterList = new ParameterList(page, size);
            parameterList.add("mcps.id", mcp.getId());
            parameterList.add("implementEmployees.id", mcp.getImplementEmployees().getId());
            parameterList.setOrder("finishTime", "desc");

            //xử lí ngày giờ chăm sóc.
            Date date3 = mcp.getBeginDate();
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
            String fDate = simpleDateFormat2.format(date3);
            Date fromDate = new Date();
            try {
                fromDate = simpleDateFormat2.parse(fDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fromDate);
            calendar.add(calendar.DAY_OF_MONTH, 1);
            Date toDate = calendar.getTime();

            String hqlC = serviceMCP.getStringHQLChamSoc(mcp.getImplementEmployees().getId(), mcp.getId(), dateFormat.format(fromDate), dateFormat.format(toDate));

            List<HashMap> listHashMaps = dataService.executeSelectHQL(HashMap.class, hqlC, true, 0, 0);
            mcp.setTotalPOSComplete(listHashMaps.size());

            //Lấy danh sách MCPDetails.
            MsalesResults<MCPDetails> list = dataService.getListOption(MCPDetails.class, parameterList, true);
            for (MCPDetails mcpDetails : list.getContentList()) {

                //Check xem nhân viên đã chăm sóc chưa
                ParameterList parameterList3 = new ParameterList(1, 1);
                parameterList3.add("implementEmployees.id", mcp.getImplementEmployees().getId());
                parameterList3.add("mcpDetailss.id", mcpDetails.getId());
                parameterList3.add("poss.id", mcpDetails.getPoss().getId());

                parameterList3.add("startCustomerCareAt", fromDate, ">=");
                parameterList3.add("startCustomerCareAt", toDate, "<");

                List<CustomerCareInformation> cList = dataService.getListOption(CustomerCareInformation.class, parameterList3);
                if (cList.isEmpty()) {
                    mcpDetails.setIsCSDBH(0);
                } else {
                    mcpDetails.setIsCSDBH(1);
                    mcpDetails.setNgayCSDBH(cList.get(0).getStartCustomerCareAt());

                }
            }

            mcp.setTotalPOS((int) (long) list.getCount());

            mcp.setMcpDetailss(list.getContentList());
            maxPages = (int) (long) list.getCount();
            if (maxPages % size != 0) {
                maxPages = maxPages / size + 1;
            } else {
                maxPages = maxPages / size;
            }
            //Thêm dữ liệu cho Model
            uiModel.addAttribute("page", page);
            uiModel.addAttribute("size", size);
            uiModel.addAttribute("maxPages", maxPages);
            uiModel.addAttribute("kehoach", mcp);
            uiModel.addAttribute("kehoachchitiets", mcp.getMcpDetailss());
            // Trả về trang ViewPlanPage
            return "viewPlanPage";

        } else {
            return "notFoundError";
        }

    }

    @RequestMapping(value = "plan/delete", method = RequestMethod.POST)
    public ModelAndView deleteMCP(@RequestParam("id") Integer id,
            HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        MsalesLoginUserInf user = LoginContext.getLogin(request);
        String status = "OK";
        //Lấy dữ liệu
        MCP mcp = new MCP();
        mcp.setId(id);
        mcp.setDeletedUser(user.getId());
        try {
            int ret = dataService.deleteSynch(mcp);
            if (ret < 0) {
                status = "NOT OK";
            }
        } catch (Exception ex) {
            status = "NOT OK";
        }

        return new ModelAndView(jsonView, "status", status);
    }

    private void saveNewPlan(WebPlanForm plan, Model model, HttpServletRequest request) {
        MsalesLoginUserInf user = LoginContext.getLogin(request);

        List<MCPDetails> mcpDetailList = new ArrayList<>();
        if (plan.getTempPos().size() > 0) {
            for (Integer posId : plan.getTempPos()) {
                MCPDetails mcpDetail = new MCPDetails();
                mcpDetail.setPosId(posId);
                mcpDetail.setImplementEmployeeId(plan.getImplementId());
                mcpDetail.setStatusId(1);
                mcpDetail.setIsActive(1);
                mcpDetail.setFinishTime(ValidatorUtil.parseParameterDateTime(plan.getBeginDate() + " " + ValidatorUtil.getNowTime()));
                mcpDetailList.add(mcpDetail);
            }
        }

        MCP mcp = new MCP();
        mcp.setType(1);
        mcp.setName(plan.getMcpName());
        mcp.setImplementEmployeeId(plan.getImplementId());
        mcp.setBeginDate(ValidatorUtil.parseParamaterDate(plan.getBeginDate()));
        mcp.setStatusId(1);
        mcp.setIsActive(1);
        mcp.setCreatedUser(user.getId());
        mcp.setMcpDetailss(mcpDetailList);
        mcp.setFinishTime(ValidatorUtil.parseParameterDateTime(plan.getBeginDate() + " " + ValidatorUtil.getNowTime()));

        if (mcpService.insertMCPAndDetails(mcp)) {
            // Susscess
            model.addAttribute("infoMessage", "Lưu kế hoạch thành công.");
        } else {
            // Fail
        }
    }

    private void saveEditPlan(WebPlanForm plan, Model model, HttpServletRequest request) {
        MsalesLoginUserInf user = LoginContext.getLogin(request);
        List<MCPDetails> mcpDetailList = mcpService.getListMCPDetailByMcpIdAndImplementId(plan.getMcpId(), plan.getImplementId());
        List<MCPDetails> mcpDetailListNew = new ArrayList<MCPDetails>();
        if (plan.getTempPos().size() > 0) {

            for (int posId : plan.getTempPos()) {

                boolean skip = false;
                if (mcpDetailList != null && mcpDetailList.size() > 0) {
                    for (MCPDetails mcpDetail : mcpDetailList) {
                        int posIdOld = mcpDetail.getPoss().getId();
                        if (posId == posIdOld) {
                            skip = true;
                            break;
                        }
                    }
                }
                if (skip == false) {
                    MCPDetails mcpDetail = new MCPDetails();
                    mcpDetail.setMcpId(plan.getMcpDetailId());
                    mcpDetail.setPosId(posId);
                    mcpDetail.setImplementEmployeeId(plan.getImplementId());
                    mcpDetail.setStatusId(1);
                    mcpDetail.setIsActive(1);
                    //mcpDetail.setFinishTime(ValidatorUtil.parseParameterDateTime(plan.getBeginDate() + " " + ValidatorUtil.getNowTime()));
                    mcpDetailListNew.add(mcpDetail);
                }
            }
        }

        MCP mcp = new MCP();
        mcp.setId(plan.getMcpId());
        mcp.setType(1);
        mcp.setName(plan.getMcpName());
        mcp.setImplementEmployeeId(plan.getImplementId());
        //mcp.setBeginDate(ValidatorUtil.parseParamaterDate(plan.getBeginDate()));
        mcp.setStatusId(1);
        mcp.setIsActive(1);
        mcp.setUpdatedUser(user.getId());
        mcp.setMcpDetailss(mcpDetailListNew);
        //mcp.setFinishTime(ValidatorUtil.parseParameterDateTime(plan.getBeginDate() + " " + ValidatorUtil.getNowTime()));

        if (mcpService.updateMCPAndDetails(mcp, plan.getTempPos(), plan.isOverride(), user.getId())) {
            // Susscess
            model.addAttribute("infoMessage", "Thông tin được cập nhật thành công.");
            model.addAttribute("success", true);
        } else {
            // Fail
            model.addAttribute("errorMessage", "Cập nhật không thành công.");
            model.addAttribute("success", false);
        }

    }

    private void initCopyPlan(List<MCPDetails> listMcpDetail, WebPlanForm kehoach, WebPlanForm copyKeHoach,
            Model model, HttpServletRequest request) {
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);

        copyKeHoach.setMcpName(kehoach.getMcpName() + " - COPY");
        copyKeHoach.setEstablistId(kehoach.getEstablistId());
        copyKeHoach.setImplementId(kehoach.getImplementId());
        copyKeHoach.setBeginDate(kehoach.getBeginDate());
        model.addAttribute("keHoach", copyKeHoach.getMcpId());

        initializeStaffList(request, model, userInf.getCompanyId(), new Integer[]{6});
        //  MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        if (userInf.getUserRoleCode().equals("USER_ROLE_ADMIN_COMPANY")) {
            model.addAttribute("provinceList", locationService.getListLocationByType(1));
        } else {
            model.addAttribute("provinceList", locationService.getListLocationByListChannelId(userInf.getChannelList()));
        }
//        model.addAttribute("provinceList", getListLocation(1));
//        if (kehoach.getProvinceId() > 0) {
//            model.addAttribute("townList", getListLocationByParentId(kehoach.getProvinceId()));
//        }
//        if (kehoach.getDistricId() > 0) {
//            model.addAttribute("wardList", getListLocationByParentId(kehoach.getDistricId()));
//        }

        List<Integer> tempPost = new ArrayList<>();
        if (listMcpDetail != null && listMcpDetail.size() > 0) {
            List<String> ids = new ArrayList<>();
            for (MCPDetails d : listMcpDetail) {
                ids.add(d.getPoss().getId().toString());
                tempPost.add(d.getPoss().getId());
            }

            if (ids.size() > 0) {
                List<POS> listPos = posService.getListPOSIn(ids);
                Map<String, String> listDBHSelected = new LinkedHashMap<>();
                int j = 1;
                for (POS pos : listPos) {
                    listDBHSelected.put(pos.getId().toString(), j + ". " + pos.getPosCode() + " - " + pos.getName());
                    j++;
                }
                model.addAttribute("saleAgentSelectList", listDBHSelected);
            }
        }
        model.addAttribute("planForm", copyKeHoach);
        model.addAttribute("tempPos", Arrays.toString(tempPost.toArray()));
        model.addAttribute("dayOfWeek", DayOfWeek.getList());

    }

    private void initPlanForm(WebPlanForm plan, ArrayList<String> listDiemBH,
            ArrayList<String> listDiemBHSelect, Model model, HttpServletRequest request) {

        model.addAttribute("keHoach", plan.getMcpId());
        model.addAttribute("staffList", userList);
        //  model.addAttribute("provinceList", getListLocation(1));
        initializeStaffList(request, model, LoginContext.getLogin(request).getCompanyId(), new Integer[]{6});
//        if (plan.getProvinceId() > 0) {
//            model.addAttribute("townList", getListLocationByParentId(plan.getProvinceId()));
//        }
//        if (plan.getDistricId() > 0) {
//            model.addAttribute("wardList", getListLocationByParentId(plan.getDistricId()));
//        }
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        if (userInf.getUserRoleCode().equals("USER_ROLE_ADMIN_COMPANY")) {
            model.addAttribute("provinceList", locationService.getListLocationByType(1));
        } else {
            model.addAttribute("provinceList", locationService.getListLocationByListChannelId(userInf.getChannelList()));
        }
        if (listDiemBH != null) {
            if (listDiemBH.size() > 0) {
                List<POS> listPos = posService.getListPOSIn(listDiemBH);
                Map<String, String> listDBH = new LinkedHashMap<>();
                int j = 1;
                for (POS pos : listPos) {
                    listDBH.put(pos.getId().toString(), j + ". " + pos.getPosCode() + " - " + pos.getName());
                    j++;
                }
                model.addAttribute("saleAgentList", listDBH);
            }
        }

        if (listDiemBHSelect != null) {
            if (listDiemBHSelect.size() > 0) {
                List<POS> listPos = posService.getListPOSIn(listDiemBHSelect);
                Map<String, String> listDBHSelected = new LinkedHashMap<>();
                int j = 1;
                for (POS pos : listPos) {
                    listDBHSelected.put(pos.getId().toString(), j + ". " + pos.getPosCode() + " - " + pos.getName());
                    j++;
                }
                model.addAttribute("saleAgentSelectList", listDBHSelected);
            }
        }

        model.addAttribute("planForm", plan);
        model.addAttribute("tempPos", Arrays.toString(plan.getTempPos().toArray()));
        model.addAttribute("dayOfWeek", DayOfWeek.getList());

    }

    private void initPlanForm(WebPlanForm kehoach,
            List<MCPDetails> listMcpDetail, Model model, HttpServletRequest request) {

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);

        initializeStaffList(request, model, userInf.getCompanyId(), new Integer[]{6});
        model.addAttribute("keHoach", kehoach.getMcpId());

//        model.addAttribute("provinceList", getListLocation(1));
//        if (kehoach.getProvinceId() > 0) {
//            model.addAttribute("townList", getListLocationByParentId(kehoach.getProvinceId()));
//        }
//        if (kehoach.getDistricId() > 0) {
//            model.addAttribute("wardList", getListLocationByParentId(kehoach.getDistricId()));
//        }
        //   MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        if (userInf.getUserRoleCode().equals("USER_ROLE_ADMIN_COMPANY")) {
            model.addAttribute("provinceList", locationService.getListLocationByType(1));
        } else {
            model.addAttribute("provinceList", locationService.getListLocationByListChannelId(userInf.getChannelList()));
        }
        List<Integer> tempPost = new ArrayList<>();
        if (listMcpDetail != null && listMcpDetail.size() > 0) {
            List<String> ids = new ArrayList<>();
            for (MCPDetails d : listMcpDetail) {
                ids.add(d.getPoss().getId().toString());
                tempPost.add(d.getPoss().getId());
            }

            if (ids.size() > 0) {
                List<POS> listPos = posService.getListPOSIn(ids);
                Map<String, String> listDBHSelected = new LinkedHashMap<>();
                int j = 1;
                for (POS pos : listPos) {
                    listDBHSelected.put(pos.getId().toString(), j + ". " + pos.getPosCode() + " - " + pos.getName());
                    j++;
                }
                model.addAttribute("saleAgentSelectList", listDBHSelected);
            }
        }
        model.addAttribute("planForm", kehoach);
        model.addAttribute("tempPos", Arrays.toString(tempPost.toArray()));
        model.addAttribute("dayOfWeek", DayOfWeek.getList());
    }

    private void postNewPlan(WebPlanForm plan, BindingResult result,
            Model model, HttpServletRequest request) {
        ArrayList<String> listDiemBH = (ArrayList<String>) plan.getListDiemBH();
        ArrayList<String> listDiemBHSelect = (ArrayList<String>) plan
                .getListDiemBHSelect();

        planValidator.validate(plan, result);

        int existMCPId = mcpService.checkIsExistMCP(plan.getImplementId(), Utilities.dateFormat("yyyy-MM-dd", ValidatorUtil.parseLocalDate(plan.getBeginDate())));
        boolean needCheckExist = false;
        if (existMCPId != -1 && plan.getMcpId() == 0 && plan.isOverride() == false) {
            needCheckExist = true;
            result.rejectValue("beginDate", "errNgayBDExist", "errCodeNgayBD");
            model.addAttribute("existDay", new Object[]{existMCPId, Utilities.dateFormat("dd/MM/yyyy", ValidatorUtil.parseLocalDate(plan.getBeginDate()))});
        }

        if (result.hasErrors()) {
            System.out.println("BindingResult:" + result);
        } else if (existMCPId != -1 && needCheckExist == false) {
            plan.setMcpId(existMCPId);
            saveEditPlan(plan, model, request);
        } else {
            saveNewPlan(plan, model, request);
        }
        initPlanForm(plan, listDiemBH, listDiemBHSelect, model, request);
    }

    private List<Location> getListLocation(int locationType) {
        List<Location> ttpList = new ArrayList();
        try {
            List<Location> list = (List<Location>) locationController.getDataService().getListOption(Location.class, new ParameterList("locationType", locationType));
            if (list != null) {
                for (Location location : list) {
                    ttpList.add(location);
                }
            }
            if (ttpList.size() > 0) {
                Collections.sort(ttpList, new WebMsalesComparatorLocation());
            }
        } catch (Exception ex) {
            Logger.getLogger(WebMsalesPlanController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ttpList;
    }

    private List<Location> getListLocationByParentId(int parentId) {
        List<Location> ttpList = new ArrayList();
        try {
            List<Location> list = (List<Location>) locationController.getDataService().getListOption(Location.class, new ParameterList("parents.id", parentId));
            if (list != null) {
                for (Location location : list) {
                    ttpList.add(location);
                }
            }
            if (ttpList.size() > 0) {
                Collections.sort(ttpList, new WebMsalesComparatorLocation());
            }
        } catch (Exception ex) {
            Logger.getLogger(WebMsalesPlanController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ttpList;
    }

    @RequestMapping(value = "plan/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String listPlan(Model uiModel, HttpServletRequest request,
            @ModelAttribute(value = "listPlanForm") Filter filter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size)
            throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        uiModel.addAttribute("only", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY", "USER_ROLE_ADMIN_CHANNEL", "USER_ROLE_SALES_SUPERVISOR"}));
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }

        uiModel.addAttribute("notShow", true);
        int maxPages = 1;
        //set role cho cbb Nhan vien
        filter.setRoles(new Integer[]{6});
        List<OptionItem> employerList = filter.processFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());

        //Tạo dữ liệu search
        int userId = 0;
        if (filter.getUserId() != null && filter.getUserId() != 0) {
            userId = filter.getUserId();
        }
        String fromDate = filter.getStartDateString();
        String toDate = filter.getEndDateString();
        String key = filter.getSearchText();
        //Lấy dữ liệu
        //Tạo câu hql
        String classSelect;
        List<ArrayList<String>> fields = new ArrayList<ArrayList<String>>();
        List<ArrayList<String>> operators = new ArrayList<ArrayList<String>>();
        List<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
        classSelect = "MCP";
        ArrayList<String> fieldtype = new ArrayList<String>();
        fieldtype.add("type");
        ArrayList<String> operatortype = new ArrayList<String>();
        operatortype.add("=");
        ArrayList<String> valuetype = new ArrayList<String>();
        valuetype.add(1 + "");
        fields.add(fieldtype);
        operators.add(operatortype);
        values.add(valuetype);
        //Check companyId
        ArrayList<String> fieldCompanyId = new ArrayList<String>();
        fieldCompanyId.add("implementEmployees.companys.id");
        ArrayList<String> operatorCompanyId = new ArrayList<String>();
        operatorCompanyId.add("=");
        ArrayList<String> valueCompanyId = new ArrayList<String>();
        valueCompanyId.add(userInf.getCompanyId() + "");
        fields.add(fieldCompanyId);
        operators.add(operatorCompanyId);
        values.add(valueCompanyId);
        //Xử lí fromDate and toDate
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (fromDate != null && !fromDate.isEmpty() && !fromDate.trim().isEmpty() && !fromDate.equals("")) {
            try {
                Date date1 = sDateFormat.parse(fromDate);
                //   String fromDate2 = simpleDateFormat.format(date1);
                //     Date date2 = simpleDateFormat.parse(fromDate2);
                //parameterList.add("salesTransDate", date1, ">=");
                ArrayList<String> field = new ArrayList<String>();
                field.add("beginDate");
                ArrayList<String> operator = new ArrayList<String>();
                operator.add(">=");
                ArrayList<String> value = new ArrayList<String>();
                value.add("'" + simpleDateFormat.format(date1) + "'");
                fields.add(field);
                operators.add(operator);
                values.add(value);
            } catch (ParseException e) {

            }
        }

        if (toDate != null && !toDate.isEmpty() && !toDate.trim().isEmpty() && !toDate.equals("")) {
            try {
                Date date1 = sDateFormat.parse(toDate);
                //  String toDate2 = simpleDateFormat.format(date1);
                //  Date date2 = simpleDateFormat.parse(toDate2);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);
                calendar.add(calendar.DAY_OF_MONTH, 1);
                //parameterList.add("salesTransDate", date2, "<=");
                ArrayList<String> field = new ArrayList<String>();
                field.add("beginDate");
                ArrayList<String> operator = new ArrayList<String>();
                operator.add("<");
                ArrayList<String> value = new ArrayList<String>();
                value.add("'" + simpleDateFormat.format(calendar.getTime()) + "'");
                fields.add(field);
                operators.add(operator);
                values.add(value);
            } catch (ParseException e) {
            }
        }
        //Xử lí searchText
        if (key != null && !key.isEmpty() && !key.trim().isEmpty() && !key.equals("")) {
            ArrayList<String> field = new ArrayList<String>();
            field.add("note");
            field.add("name");
            ArrayList<String> operator = new ArrayList<String>();
            operator.add("LIKE");
            operator.add("LIKE");
            ArrayList<String> value = new ArrayList<String>();
            value.add(key);
            value.add(key);

            fields.add(field);
            operators.add(operator);
            values.add(value);
        }
        //Xử lí userId
        if (userId != 0) {
            ArrayList<String> field = new ArrayList<String>();
            field.add("implementEmployees.id");
            ArrayList<String> operator = new ArrayList<String>();
            operator.add("=");
            ArrayList<String> value = new ArrayList<String>();
            value.add(userId + "");
            fields.add(field);
            operators.add(operator);
            values.add(value);
        } else {
            ArrayList<String> field = new ArrayList<String>();
            ArrayList<String> operator = new ArrayList<String>();
            ArrayList<String> value = new ArrayList<String>();
            if (employerList.size() > 1) {
                for (int i = 1; i < employerList.size(); i++) {
                    int id = employerList.get(i).getId();
                    if (id != 0) {
                        field.add("implementEmployees.id");
                        operator.add("=");
                        value.add(id + "");
                    }
                }

            } else {
                field.add("implementEmployees.id");
                operator.add("=");
                value.add(-1 + "");
            }
            if (field.size() > 0) {
                fields.add(field);
                operators.add(operator);
                values.add(value);
            }
        }
        //Lấy dữ liệu:
        List<MCP> count = new ArrayList<MCP>();
        List<MCP> lists = new ArrayList<MCP>();
        String hql2 = serviceMCP.countSQLString(classSelect, fields, operators, values);
        ArrayList<String> field = new ArrayList<String>();
        field.add("beginDate");
        ArrayList<String> operator = new ArrayList<String>();
        operator.add("order by");
        ArrayList<String> value = new ArrayList<String>();
        value.add("desc");
        fields.add(field);
        operators.add(operator);
        values.add(value);
        String hql = serviceMCP.getSQLString(classSelect, fields, operators, values);
        try {
            count = dataService.executeSelectHQL(MCP.class, hql2, true, 0, 0);
            lists = dataService.executeSelectHQL(MCP.class, hql, false, page, size);
        } catch (Exception ex) {

        }
        //Cập nhật thông tin cho list.
        for (MCP mcp : lists) {
            //Xét tên cho nhân viên
            mcp.getImplementEmployees().setName(mcp.getImplementEmployees().getLastName() + " " + mcp.getImplementEmployees().getFirstName());
            //Xét tỉnh thành cho nhân viên
            if (mcp.getImplementEmployees().getLocations().getLocationType() == 1) {
                mcp.getImplementEmployees().setLocation(mcp.getImplementEmployees().getLocations().getName());
            } else if (mcp.getImplementEmployees().getLocations().getParents().getLocationType() == 1) {
                mcp.getImplementEmployees().setLocation(mcp.getImplementEmployees().getLocations().getParents().getName());
            } else {
                mcp.getImplementEmployees().setLocation(mcp.getImplementEmployees().getLocations().getParents().getParents().getName());
            }
            //Xét Người lập
            int createdUserId = mcp.getCreatedUser();
            User user = dataService.getRowById(createdUserId, User.class);
            if (user != null) {
                mcp.setNameAdmin(user.getLastName() + " " + user.getFirstName());
            } else {
                mcp.setNameAdmin("Admin");
            }
            //Check số điểm bán hàng trên tuyến đường
            ParameterList parameterList = new ParameterList("mcps.id", mcp.getId());
            //   parameterList.add("implementEmployees.id", mcp.getImplementEmployees().getId());
            parameterList.setOrder("finishTime", "desc");
            //Lấy danh sách MCPDetails.
            List<MCPDetails> list = dataService.getListOption(MCPDetails.class, parameterList);
            int i = 0;
            for (MCPDetails mcpDetails : list) {

                //Check xem nhân viên đã chăm sóc chưa
                ParameterList parameterList3 = new ParameterList(1, 1);
                parameterList3.add("implementEmployees.id", mcp.getImplementEmployees().getId());
                parameterList3.add("mcpDetailss.id", mcpDetails.getId());
                parameterList3.add("poss.id", mcpDetails.getPoss().getId());
                //xử lí ngày giờ chăm sóc.
                Date date3 = mcp.getBeginDate();
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                String fDate = simpleDateFormat2.format(date3);
                Date fromDate2 = new Date();
                try {
                    fromDate2 = simpleDateFormat2.parse(fDate);
                } catch (ParseException e) {

                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(fromDate2);
                calendar.add(calendar.DAY_OF_MONTH, 1);
                Date toDate2 = calendar.getTime();
                parameterList3.add("startCustomerCareAt", fromDate2, ">=");
                parameterList3.add("startCustomerCareAt", toDate2, "<");
                List<CustomerCareInformation> cList = dataService.getListOption(CustomerCareInformation.class, parameterList3);
                if (cList.isEmpty()) {
                } else {
                    i++;
                }
            }
            mcp.setTotalPOS(list.size());
            mcp.setTotalPOSComplete(i);
            if (i == list.size() && i != 0) {
                mcp.setIsComplete(1);
            } else {
                mcp.setIsComplete(0);
            }
        }

        maxPages = count.size();
        if (maxPages % size != 0) {
            maxPages = maxPages / size + 1;
        } else {
            maxPages = maxPages / size;
        }

        //Thêm dữ liệu cho Model
        uiModel.addAttribute("planList", lists);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);
        return "listPlanPage";
    }

    @RequestMapping(value = "plan/printQRCode/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String printQRCode(@PathVariable("id") int id, HttpServletRequest request) {

        List<MCPDetails> listMcpDetail = mcpService.getListMCPDetailByMcpId(id);
        if (listMcpDetail != null && listMcpDetail.size() > 0) {
            try {
                return export_QRCode(request, listMcpDetail);
            } catch (IOException ex) {
                Logger.getLogger(WebMsalesPlanController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return "redirect:/plan/list";
    }

    //======================== Export QRCode to Zip File ============================??
    public String export_QRCode(HttpServletRequest request, List<MCPDetails> dbh) throws IOException {

        try {

            MsalesLoginUserInf user = LoginContext.getLogin(request);

            String userName = user.getUsername();

            String ZipPath = request.getRealPath("") + "/QRCode";

            String QRPath, chiNhanh = "", quanHuyen = "", maDBH;
            int disDBH;

            //====Create directory to store QRCode
            File path = new File(ZipPath);
            if (path.exists()) {
                deleteFolder(path);
                System.out.println("The QRCode folder already exists !");
            } else {
                path.mkdir();
                System.out.println("New QRCode folder " + ZipPath + " is created !");
            }

            //====Create Zip Directory
            ZipPath = ZipPath + "/" + userName;
            path = new File(ZipPath);
            if (path.exists()) {
                deleteFolder(path);
                System.out.println("Old folder " + ZipPath + " is deleted !");
            }
            path.mkdir();
            System.out.println("New folder " + ZipPath + " is created !");

            for (MCPDetails mcpDetail : dbh) {
                maDBH = mcpDetail.getPoss().getPosCode();
                disDBH = mcpDetail.getPoss().getLocations().getId();
                switch (disDBH) {
                    case 1289:
                        chiNhanh = "CN6";
                        quanHuyen = "Q.BTH";
                        break;
                    case 1290:
                        chiNhanh = "CN5";
                        quanHuyen = "H.CGI";
                        break;
                    case 1291:
                        chiNhanh = "CN1";
                        quanHuyen = "Q.04";
                        break;
                    case 1292:
                        chiNhanh = "CN1";
                        quanHuyen = "Q.05";
                        break;
                    case 1293:
                        chiNhanh = "CN3";
                        quanHuyen = "Q.06";
                        break;
                    case 1294:
                        chiNhanh = "CN3";
                        quanHuyen = "Q.08";
                        break;
                    case 1295:
                        chiNhanh = "CN2";
                        quanHuyen = "Q.09";
                        break;
                    case 1296:
                        chiNhanh = "CN4";
                        quanHuyen = "Q.12";
                        break;
                    case 1297:
                        chiNhanh = "CN3";
                        quanHuyen = "H.BCH";
                        break;
                    case 1298:
                        chiNhanh = "CN3";
                        quanHuyen = "Q.BTA";
                        break;
                    case 1299:
                        chiNhanh = "CN4";
                        quanHuyen = "H.CCH";
                        break;
                    case 1300:
                        chiNhanh = "CN1";
                        quanHuyen = "Q.PNH";
                        break;
                    case 1301:
                        chiNhanh = "CN1";
                        quanHuyen = "Q.03";
                        break;
                    case 1303:
                        chiNhanh = "CN6";
                        quanHuyen = "Q.GVA";
                        break;
                    case 1304:
                        chiNhanh = "CN4";
                        quanHuyen = "H.HMO";
                        break;
                    case 1305:
                        chiNhanh = "CN5";
                        quanHuyen = "H.NBE";
                        break;
                    case 1306:
                        chiNhanh = "CN5";
                        quanHuyen = "Q.07";
                        break;
                    case 1307:
                        chiNhanh = "CN1";
                        quanHuyen = "Q.10";
                        break;
                    case 1308:
                        chiNhanh = "CN6";
                        quanHuyen = "Q.TBI";
                        break;
                    case 1309:
                        chiNhanh = "CN2";
                        quanHuyen = "Q.TDU";
                        break;
                    case 1310:
                        chiNhanh = "CN2";
                        quanHuyen = "Q.02";
                        break;
                    case 1311:
                        chiNhanh = "CN1";
                        quanHuyen = "Q.11";
                        break;
                    case 1312:
                        chiNhanh = "CN6";
                        quanHuyen = "Q.TPH";
                        break;
                    case 1313:
                        chiNhanh = "CN1";
                        quanHuyen = "Q.01";
                        break;
                    default:
                        break;
                }
                QRPath = ZipPath + "/" + chiNhanh;
                File qrpath = new File(QRPath);
                if (!qrpath.exists()) {
                    qrpath.mkdir();
                    System.out.println("New folder " + QRPath + " is created !");
                }

                QRPath = QRPath + "/" + quanHuyen;
                qrpath = new File(QRPath);
                if (!qrpath.exists()) {
                    qrpath.mkdir();
                    System.out.println("New folder " + QRPath + " is created !");
                }

                //==== Generate and store QRCode Images to folder
                Gen2Image2.Gen2Image(request, QRPath, maDBH, quanHuyen);
            }

            //===== Zip the folder and export
            File directoryToZip = new File(ZipPath);
            List<File> fileList = new ArrayList<File>();
            System.out.println("---Getting references to all files in: " + directoryToZip.getCanonicalPath());

            getAllFiles(directoryToZip, fileList);
            System.out.println("---Creating zip file");

            writeZipFile(directoryToZip, fileList, request);
            System.out.println("---Done");

            return "redirect:/QRCode/" + directoryToZip.getName() + ".zip";

        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
            return "redirect:/plan/list";
        }
    }

    public static void deleteFolder(File path) {
        File[] file = path.listFiles();
        if (file != null) {
            for (File f : file) {
                if (f.isFile()) {
                    f.delete();
                } else {
                    deleteFolder(f);
                }
            }
        }
        path.delete();
    }

    public static void getAllFiles(File dir, List<File> fileList) {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    System.out.println("directory:" + file.getCanonicalPath());
                    getAllFiles(file, fileList);
                } else {
                    System.out.println("     file:" + file.getCanonicalPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeZipFile(File directoryToZip, List<File> fileList, HttpServletRequest request) {

        try {
            FileOutputStream fos = new FileOutputStream(request.getRealPath("") + "/QRCode/" + directoryToZip.getName() + ".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (File file : fileList) {
                if (!file.isDirectory()) { //only zip files, not directories
                    addToZip(directoryToZip, file, zos);
                }
            }

            zos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
            IOException {

        FileInputStream fis = new FileInputStream(file);

        // we want the zipEntry's path to be a relative path that is relative
        // to the directory being zipped, so chop off the rest of the path
        String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
                file.getCanonicalPath().length());

        System.out.println("Writing '" + zipFilePath + "' to zip file");
        ZipEntry zipEntry = new ZipEntry(zipFilePath);
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }

    @RequestMapping(value = "plan/import", method = RequestMethod.POST)
    private String importPlan(HttpServletRequest request, @RequestParam("file_plan") MultipartFile multipartFile) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        boolean updated = imports(request, multipartFile);
        request.setAttribute("updated", updated);
        request.setAttribute("submited", true);
        return "planImport";
    }

    @RequestMapping(value = "plan/import", method = RequestMethod.GET)
    private String importPlan(HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        return "planImport";
    }

    private List<MsalesPlanImport> getListPlan(List<List<Object>> listPlanObj) {
        List<MsalesPlanImport> listPlan = new ArrayList<>();
        for (List<Object> plan : listPlanObj) {
            if (!excelService.checkEmptyRow(plan)) {
                MsalesPlanImport planImport = new MsalesPlanImport();
                planImport.setMcpName(plan.get(0) == null ? null : excelService.getNumberString(plan.get(0).toString()));
                planImport.setUserName(plan.get(1) == null ? null : excelService.getNumberString(plan.get(1).toString()));
                planImport.setPosCode(plan.get(2) == null ? null : excelService.getNumberString(plan.get(2).toString()));
                planImport.setDateCare(plan.get(3) == null ? null : excelService.getDate(plan.get(3).toString()));

                listPlan.add(planImport);
            }
        }
        return listPlan;
    }

    private boolean imports(HttpServletRequest request, MultipartFile multipartFile) {
        List<List<Object>> mcpList = excelService.getListDataFromExel(multipartFile, 4, 0);
        List<MsalesPlanImport> msalesPlanImports = getListPlan(mcpList);
        if (!msalesPlanImports.isEmpty()) {
            return getListMCPFromPlanImport(request, msalesPlanImports);
        }

        return false;
    }

    private boolean getListMCPFromPlanImport(HttpServletRequest request, List<MsalesPlanImport> msalesPlanImports) {
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        //check require
        List<String> requiredList = checkRequired(msalesPlanImports);
        if (!requiredList.isEmpty()) {
            request.setAttribute("error", true);
            request.setAttribute("required", true);
            request.setAttribute("requiredList", requiredList);
            return false;
        }

        //check user,posdo
        List<String> checkUsernames = validateUserName(loginUserInf, msalesPlanImports);
        List<String> checkPOSList = validatePOSCode(loginUserInf, msalesPlanImports);
        if (!checkUsernames.isEmpty() || !checkUsernames.isEmpty()) {
            request.setAttribute("userList", checkUsernames);
            request.setAttribute("postList", checkPOSList);
            request.setAttribute("userCodeError", !checkUsernames.isEmpty());
            request.setAttribute("posCodeError", !checkUsernames.isEmpty());
            request.setAttribute("error", true);
            return false;
        }
        
        //check repeat POS in EXCEL
        List<MsalesPlanImport> checkRepeatPOSInExcel = checkRepeatPOSInExcel(msalesPlanImports);
        if (!checkRepeatPOSInExcel.isEmpty()) {
            request.setAttribute("duplicateList", checkRepeatPOSInExcel);
            request.setAttribute("repeatPOSExcel", true);
            request.setAttribute("error", true);
            return false;
        }
        
        //check 1 DBH trong cung 1 ngay thuoc nhieu Ke hoach
        List<MsalesPlanImport> checkUniqeDayPOSList = checkUniqeDayPOS(msalesPlanImports);
        if (!checkUniqeDayPOSList.isEmpty()) {
            request.setAttribute("duplicateList", checkUniqeDayPOSList);
            request.setAttribute("uniqeDayPOS", true);
            request.setAttribute("error", true);
            return false;
        }

        //check duplicate MCP in EXCEL
        List<MsalesPlanImport> checkDuplicateMCPInExcel = checkDuplicateMCPInExcel(msalesPlanImports);
        if (!checkDuplicateMCPInExcel.isEmpty()) {
            request.setAttribute("duplicateList", checkDuplicateMCPInExcel);
            request.setAttribute("duplicatedMCPExcel", true);
            request.setAttribute("error", true);
            return false;
        }

        //check duplicate MCP
        List<MsalesPlanImport> checkDuplicateMCP = checkDuplicateMCP(msalesPlanImports);
        if (!checkDuplicateMCP.isEmpty()) {
            request.setAttribute("duplicateList", checkDuplicateMCP);
            request.setAttribute("dublicated", true);
            request.setAttribute("error", true);
            return false;
        }

        //insert
        Transaction transaction = null;
        org.hibernate.Session datasSession = null;
        try {
            for (MsalesPlanImport planImport : msalesPlanImports) {
                datasSession = dataService.openSession();
                transaction = datasSession.beginTransaction();

                MCP mcp = appService.getMCP(planImport.getUser().getId(), planImport.getDateCare(), dataService);
                if (mcp == null) {
                    mcp = new MCP();
                    //create MCP neu chua co
                    Date beginDate = DateUtils.getShortDate(planImport.getDateCare());
                    Date finishDate = DateUtils.addSecond(DateUtils.getNextDate(beginDate), -1);

                    mcp.setType(1);
                    if (excelService.checkEmptyOrNull(planImport.getMcpName())) {
                        mcp.setName(planImport.getUser().getLastName() + " " + planImport.getUser().getFirstName() + " - " + DateUtils.getDayString(beginDate));
                    } else {
                        mcp.setName(planImport.getMcpName());
                    }
                    mcp.setBeginDate(beginDate);
                    mcp.setFinishTime(finishDate);
                    mcp.setStatusId(1);
                    mcp.setIsActive(1);
                    mcp.setImplementEmployees(planImport.getUser());
                    mcp.setCreatedAt(new Date());
                    mcp.setCreatedUser(loginUserInf.getId());
                    mcp.setUpdatedAt(null);
                    mcp.setUpdatedUser(0);
                    mcp.setDeletedAt(null);
                    mcp.setDeletedUser(0);

                    datasSession.save(mcp);
                }

                for (POS pos : planImport.getPosList()) {
                    //create MCPDetails
                    MCPDetails mcpDetails = new MCPDetails();

                    mcpDetails.setMcps(mcp);
                    mcpDetails.setLocations(pos.getLocations());
                    mcpDetails.setPoss(pos);
                    mcpDetails.setImplementEmployees(planImport.getUser());
                    mcpDetails.setStatusId(1);
                    mcpDetails.setFinishTime(mcp.getFinishTime());
                    mcpDetails.setIsActive(1);
                    mcpDetails.setNote("");
                    mcpDetails.setCreatedAt(new Date());
                    mcpDetails.setCreatedUser(loginUserInf.getId());
                    mcpDetails.setUpdatedAt(null);
                    mcpDetails.setUpdatedUser(0);
                    mcpDetails.setDeletedAt(null);
                    mcpDetails.setDeletedUser(0);

                    //insert
                    datasSession.save(mcpDetails);
                }
                transaction.commit();
            }
        } catch (Exception e) {
            transaction.rollback();
            request.setAttribute("error", true);
            request.setAttribute("errorSQL", true);
            return false;
        } finally {
            datasSession.close();
        }

        return true;
    }

    private List<String> checkRequired(List<MsalesPlanImport> planImports) {
        List<String> requiredList = new ArrayList<>();
        boolean flag = true;
        for (MsalesPlanImport mcpImport : planImports) {
            if (excelService.checkEmptyOrNull(mcpImport.getMcpName())) {
                requiredList.add("Tên kế hoạch là bắt buộc nhập.");
                flag = false;
            }
            if (excelService.checkEmptyOrNull(mcpImport.getUserName())) {
                requiredList.add("Tên nhân viên là bắt buộc nhập.");
                flag = false;
            }
            if (excelService.checkEmptyOrNull(mcpImport.getPosCode())) {
                requiredList.add("Mã Điểm bán hàng là bắt buộc nhập.");
                flag = false;
            }
            if (excelService.checkEmptyOrNull(mcpImport.getDateCare())) {
                requiredList.add("Ngày chăm sóc nhập không chính xác hoặc để trống.");
                flag = false;
            }
            if (!flag) {
                break;
            }
        }
        return requiredList;
    }

    private List<MsalesPlanImport> checkRepeatPOSInExcel(List<MsalesPlanImport> msalesPlanImports) {
        List<MsalesPlanImport> posCodeRepeatList = new ArrayList<>();
        for (MsalesPlanImport planImport : msalesPlanImports) {
            String[] array = planImport.getPosCode().split(";");
            planImport.setPosCodeRepeatList(new HashSet<String>());
            boolean flag = false;
            for (int i = 0; i < array.length - 1; i++) {
                for (int j = i + 1; j < array.length; j++) {
                    //check code in planImport.getPosCodeRepeatList()                    
                    if (array[i].equalsIgnoreCase(array[j])) {
                        planImport.getPosCodeRepeatList().add(array[i]);
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) {
                posCodeRepeatList.add(planImport);
            }
        }
        return posCodeRepeatList;
    }

    private List<MsalesPlanImport> checkDuplicateMCPInExcel(List<MsalesPlanImport> msalesPlanImports) {
        List<MsalesPlanImport> duplicatePlanList = new ArrayList<>();
        List<MsalesPlanImport> list = new ArrayList<>();
        for (int i = 0; i < msalesPlanImports.size() - 1; i++) {
            for (int j = i + 1; j < msalesPlanImports.size(); j++) {
                if (!list.contains(msalesPlanImports.get(j))) {
                    if (msalesPlanImports.get(i).getUser().getId().equals(msalesPlanImports.get(j).getUser().getId())
                            && DateUtils.getShortDate(msalesPlanImports.get(i).getDateCare()).equals(DateUtils.getShortDate(msalesPlanImports.get(j).getDateCare()))) {
                        duplicatePlanList.add(msalesPlanImports.get(i));
                        list.add(msalesPlanImports.get(j));
                    }
                }
            }
        }
        return duplicatePlanList;
    }

    private List<MsalesPlanImport> checkUniqeDayPOS(List<MsalesPlanImport> msalesPlanImports) {
        List<MsalesPlanImport> list = new ArrayList<>();
        for (int i = 0; i < msalesPlanImports.size() - 1; i++) {
            boolean flag = false;
            for (int j = i + 1; j < msalesPlanImports.size(); j++) {
                if (DateUtils.getShortDate(msalesPlanImports.get(i).getDateCare()).equals(DateUtils.getShortDate(msalesPlanImports.get(j).getDateCare()))) {
                    msalesPlanImports.get(i).setPosCodeDuplicate(new ArrayList<String>());
                    for (POS posI : msalesPlanImports.get(i).getPosList()) {
                        for (POS posJ : msalesPlanImports.get(j).getPosList()) {
                            if (posI.getId().equals(posJ.getId())) {
                                msalesPlanImports.get(i).getPosCodeDuplicate().add(posI.getPosCode());
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (flag) {
                list.add(msalesPlanImports.get(i));
            }
        }
        return list;
    }

    private List<MsalesPlanImport> checkDuplicateMCP(List<MsalesPlanImport> msalesPlanImports) {
        List<MsalesPlanImport> duplicateList = new ArrayList<>();
        for (MsalesPlanImport planImport : msalesPlanImports) {
            planImport.setPosDuplicateList(new ArrayList<String>());
            boolean flag = false;
            for (POS pos : planImport.getPosList()) {
                List<MCPDetails> mcpDetailsList = appService.getListMCPDetailsByPOS(pos.getId(), planImport.getDateCare(), dataService);
                if (!mcpDetailsList.isEmpty()) {
                    //DBH đa duoc lap tuyen duong
                    flag = true;
                    planImport.getPosDuplicateList().add(pos.getPosCode());
                }
            }
            if (flag) {
                duplicateList.add(planImport);
            }
        }
        return duplicateList;
    }

    private List<String> validateUserName(MsalesLoginUserInf userInf, List<MsalesPlanImport> msalesPlanImports) {
        List<String> validateUsername = new ArrayList<>();
        for (MsalesPlanImport planImport : msalesPlanImports) {
            User user = excelService.getUserByUserName(planImport.getUserName(), userInf.getCompanyCode(), userInf.getCompanyId(), dataService);
            if (user != null) {
                planImport.setUser(user);
            } else {
                if (!validateUsername.contains(planImport.getUserName())) {
                    validateUsername.add(planImport.getUserName());
                }
            }
        }
        return validateUsername;
    }

    private List<String> validatePOSCode(MsalesLoginUserInf userInf, List<MsalesPlanImport> msalesPlanImports) {
        List<String> validatePOS = new ArrayList<>();
        for (MsalesPlanImport planImport : msalesPlanImports) {
            planImport.setPosList(new ArrayList<POS>());
            for (String posCode : planImport.getPosCode().split(";")) {
                POS pos = excelService.getPOSByPosCode(posCode, userInf.getCompanyId(), dataService);
                if (pos != null) {
                    planImport.getPosList().add(pos);
                } else {
                    if (!validatePOS.contains(posCode)) {
                        validatePOS.add(posCode);
                    }
                }
            }
        }
        return validatePOS;
    }
}
