/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.workflow.controller;

import com.fasterxml.jackson.core.JsonParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.itt.msales.api.common.WebMsalesOptionItem;
import vn.itt.msales.channel.controller.MsalesChannelController;
import vn.itt.msales.channel.controller.MsalesChannelTypeController;
import vn.itt.msales.channel.controller.MsalesLocationController;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.customercare.model.TimeHour;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.User;
import vn.itt.msales.goods.controller.MsalesGoodsController;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.controller.MsalesUserController;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;
import vn.itt.msales.user.model.URLManager;
import vn.itt.msales.workflow.model.WebMCP;
import vn.itt.msales.workflow.service.ServiceMCP;

/**
 *
 * @author ChinhNQ
 */
@Controller
public class WebMsalesPlanOfaSales {

    @Autowired
    private MsalesUserController userController;
    @Autowired
    private MsalesGoodsController goodsController;
    @Autowired
    private MsalesMCPController mcpController;
    @Autowired
    private MsalesChannelController channelController;

    @Autowired
    private MsalesLocationController locationController;
    @Autowired
    private MsalesMCPSalesDetailsController mcpSalesDetailsController;
    @Autowired
    private MsalesChannelTypeController channelTypeController;

    @Autowired
    private DataService dataService;

    @Autowired
    private ServiceMCP serviceMCP;

    @Autowired
    private ServiceFilter serviceFilter;

    @RequestMapping(value = "/planofsales/create")
    public ModelAndView viewPlan(
            @ModelAttribute("planofsales") WebMCP planOfSales,
            HttpServletRequest request) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        MCP mcp = new MCP();
        mcp.setBeginDate(new Date());
        mcp.setFinishTime(new Date());
        planOfSales.setMcp(mcp);
        planOfSales.setMonth(getCurrentMonth());
        initData(request,new Integer[]{6});
        request.setAttribute("copy", false);
        return new ModelAndView("planofsales", "planofsales", planOfSales);
    }

    @RequestMapping(value = "/planofsales/create", method = RequestMethod.POST)
    public ModelAndView addNewPlan(@ModelAttribute("planofsales") @Valid WebMCP webMCP,
            BindingResult result,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        if (webMCP != null) {
            MCP mcp = webMCP.getMcp();
            Calendar cal = getPlanDate(webMCP.getMonth());
            Date date = cal.getTime();
            mcp.setBeginDate(new Date(date.getYear(), date.getMonth(), 1));
            mcp.setFinishTime(new Date(date.getYear(), date.getMonth(), cal.getMaximum(Calendar.DAY_OF_MONTH)));

            boolean mcpExisted = serviceMCP.checkMCPExist(mcp.getImplementEmployees().getId(), date);
            if (!mcpExisted) {
                int mcpIdCreated = createPlan(request, mcp);
                if (mcpIdCreated > 0) {
                    initData(request,new Integer[]{6});
                    redirectAttributes.addFlashAttribute("createdSuccessfull", true);
                    return new ModelAndView("redirect:/planofsales/edit/" + mcpIdCreated);
                } else {
                    request.setAttribute("created", true);
                    request.setAttribute("errorMessage", "Giao chỉ tiêu không thành công.");
                }
            } else {
                request.setAttribute("dupblicate", true);
            }
        } else {
            request.setAttribute("errorMessage", "Giao chỉ tiêu không thành công.");
            request.setAttribute("created", true);
        }
        initData(request,new Integer[]{6});
        return new ModelAndView("planofsales", "planofsales", webMCP);
    }

    @RequestMapping(value = "/planofsales/create/{id:[\\d]+}", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView copyPlan(
            @ModelAttribute("planofsales") WebMCP planOfSales,
            @PathVariable("id") int mcpId,
            HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }
        MCP mcp = new MCP();
        if ("POST".equals(request.getMethod())) {
            MCP mcpEdit = createDatePlanOfSale(planOfSales);
            boolean mcpExisted = serviceMCP.checkMCPExist(mcpEdit.getImplementEmployees().getId(), mcpEdit.getBeginDate());
            if (!mcpExisted) {
                int mcpIdCreated = createPlan(request, mcpEdit);
                if (mcpIdCreated > 0) {
                    mcp = mcpEdit;
                    redirectAttributes.addFlashAttribute("createdSuccessfull", true);
                    return new ModelAndView("redirect:/planofsales/edit/" + mcpIdCreated);
                } else {
                    request.setAttribute("created", true);
                    request.setAttribute("errorMessage", "Giao chỉ tiêu không thành công.");
                }
            } else {
                request.setAttribute("dupblicate", true);
            }
        } else {
            mcp = initEdit(mcpId);
            if (mcp == null) {
                return new ModelAndView("noData");
            }
            planOfSales.setMonth(mcp.getBeginDate().getMonth());
            planOfSales.setMcp(mcp);
            mcp.setId(null);
        }

        request.setAttribute("copy", true);
        initData(request,new Integer[]{6});
        return new ModelAndView("planofsales", "planofsales", planOfSales);
    }

    @RequestMapping(value = "/planofsales/edit/{id:[\\d]+}")
    public ModelAndView editPlan(
            @ModelAttribute("planofsales") WebMCP planOfSales,
            @PathVariable("id") int mcpId,
            HttpServletRequest request) throws IOException {

        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }
        request.setAttribute("readonly", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY", "USER_ROLE_ADMIN_CHANNEL"}));
        MCP mcpFound = initEdit(mcpId);
        if (mcpFound == null) {
            return new ModelAndView("noData");
        }
        planOfSales.setMonth(mcpFound.getBeginDate().getMonth());
        planOfSales.setMcp(mcpFound);
        request.setAttribute("edit", true);
        initData(request,new Integer[]{6});
        return new ModelAndView("planofsales", "planofsales", planOfSales);
    }

    @RequestMapping(value = "/planofsales/edit/{id:[\\d]+}", method = RequestMethod.POST)
    public ModelAndView savePlan(
            @ModelAttribute("planofsales") WebMCP planofsales,
            @PathVariable("id") int mcpId,
            HttpServletRequest request) throws IOException {

        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        MCP mcpEdit = planofsales.getMcp();
        if (mcpEdit != null && mcpEdit.getId() > 0) {
            mcpEdit = createDatePlanOfSale(planofsales);
            mcpEdit.setUpdatedUser(LoginContext.getLogin(request).getId());
            // update mcp
            int updated = mcpController.getDataService().updateSync(mcpEdit);
            if (updated > 0) {
                //update MCPSalesDetails.
//                if (mcpEdit.getMcpSalesDetailss() != null) {
//                    List<MCPSalesDetails> mcpSDList = mcpEdit.getMcpSalesDetailss();
//                    Iterator<MCPSalesDetails> lisIterator = mcpSDList.iterator();
//                    while (lisIterator.hasNext()) {
//                        MCPSalesDetails mCPSalesDetails = lisIterator.next();
//                        mCPSalesDetails.setMcpId(mcpEdit.getId());
//                        mCPSalesDetails.setIsActive(1);//FIXME
//                        mCPSalesDetails.setFinishTime(mcpEdit.getFinishTime());
//                        // if MCPSalesDetails id not null then update it
//                        if (mCPSalesDetails.getId() != null) {
//                            // deleted from MCPSalesDetails list
//                            if (mCPSalesDetails.getDeletedUser() == 1) {
//                                mCPSalesDetails.setDeletedUser(LoginContext.getLogin(request).getId());
//                                lisIterator.remove();
//                                // deleted this MCPSalesDetails
//                                mcpController.getDataService().deleteSynch(mCPSalesDetails);
//                            } else {
//                                mCPSalesDetails.setUpdatedUser(LoginContext.getLogin(request).getId());
//                                mcpController.getDataService().updateSync(mCPSalesDetails);
//                            }
//                        } // create MCPSalesDetails for MCP
//                        else {
//                            mCPSalesDetails.setCreatedUser(LoginContext.getLogin(request).getId());
//                            mcpController.getDataService().insertRow(mCPSalesDetails);
//                        }
//                    }
//                }
                request.setAttribute("infoMessage", "Cập nhật thành công.");
            } else {
                request.setAttribute("errorMessage", "Cập nhật thất bại.");
            }
        } else {
            request.setAttribute("errorMessage", "Cập nhật thất bại.");
        }

        request.setAttribute("edit", true);
        initData(request,new Integer[]{6});
        return new ModelAndView("planofsales", "planofsales", planofsales);
    }

    /**
     * Init data list include staff list, goods list, status list
     */
    private void initData(HttpServletRequest request,Integer[] roles) throws IOException {
        if (LoginContext.getLogin(request).getUserRoleCode().equals("USER_ROLE_ADMIN_COMPANY")) {
            List<OptionItem> staffList = serviceFilter.getCbListUserByChannel(0, LoginContext.getLogin(request).getCompanyId(),LoginContext.getLogin(request).getId(),roles, dataService);
            request.setAttribute("staffList", staffList);
        } else {
            List<OptionItem> staffList = serviceFilter.getCbListUserByChannelOptionList(LoginContext.getLogin(request).getChannelList(), LoginContext.getLogin(request).getCompanyId(),LoginContext.getLogin(request).getId(),roles, dataService);
            request.setAttribute("staffList", staffList);
        }

//        // get list goods
//        List<WebMsalesOptionItem> goodsfList = new ArrayList<>();
//        // get List Goods from DB
//        ParameterList parameterGoodsList = new ParameterList();
//        List<Goods> lisGoods = dataService.getListOption(Goods.class, parameterGoodsList);
//        for (Goods goods : lisGoods) {
//            goodsfList.add(new WebMsalesOptionItem(goods.getId(), goods.getName()));
//        }
//        request.setAttribute("goodsfList", goodsfList);
        //get list goods
//        ParameterList paramGoodsCategory = new ParameterList();
//        paramGoodsCategory.add("companys.id", loginUserInf.getCompanyId());
//        List<GoodsCategory> listGoodsCategory = channelController.getDataService().getListOption(GoodsCategory.class, paramGoodsCategory);
//        List<Goods> listGoods = new ArrayList<>();
//        for (GoodsCategory listGood : listGoodsCategory) {
//            ParameterList paramGoods = new ParameterList();
//            paramGoods.add("goodsCategorys.id", listGood.getId());
//            List<Goods> list = channelController.getDataService().getListOption(Goods.class, paramGoods);
//            for (Goods goods : list) {
//                listGoods.add(goods);
//            }
//        }
//        Collections.sort(listGoods, new WebGoodsComparator());
//        request.setAttribute("goodsList", listGoods);
        // get list status
        List<WebMsalesOptionItem> statusfList = new ArrayList();
        statusfList.add(new WebMsalesOptionItem(1, "Chưa thực hiện"));
        statusfList.add(new WebMsalesOptionItem(2, "Tạm nghỉ"));
        request.setAttribute("statusfList", statusfList);

        List<WebMsalesOptionItem> monthList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            monthList.add(new WebMsalesOptionItem(i, "Tháng " + (i + 1)));
        }
        request.setAttribute("monthList", monthList);
    }

    @RequestMapping(value = "planofsales/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String listPlan(Model uiModel, HttpServletRequest request,
            @ModelAttribute(value = "listPlanForSalesForm") Filter filter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size)
            throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        request.setAttribute("only", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY", "USER_ROLE_ADMIN_CHANNEL"}));
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;
        uiModel.addAttribute("notShow", true);
        
        //set role cho Cbb Nhan vien
        filter.setRoles(new Integer[]{6});
        List<OptionItem> employerList = filter.processFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());

        //Khởi tạo tháng
        List<TimeHour> listMonth = new ArrayList<TimeHour>();
        for (int i = 1; i < 13; i++) {
            TimeHour month = new TimeHour(i, i + "");
            listMonth.add(month);
        }
        request.setAttribute("listMonth", listMonth);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy");
        Date date = new Date();
        String yearString = simpleDateFormat2.format(date);
        int year = Integer.parseInt(yearString);
        List<TimeHour> listYear = new ArrayList<TimeHour>();
        for (int i = year - 5; i <= year + 5; i++) {
            TimeHour month = new TimeHour(i, i + "");
            listYear.add(month);
        }
        request.setAttribute("listYear", listYear);
        //Tạo dữ liệu để search
        int userId = 0;
        if (filter.getUserId() != null && filter.getUserId() != 0) {
            userId = filter.getUserId();
        }
        
        /*String fromDate = null;
        String toDate = null;
        if (filter.getFromHour() != null && filter.getFromHour() > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, filter.getFromHour() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            fromDate = simpleDateFormat.format(calendar.getTime()) + " 00:00:00";
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            toDate = simpleDateFormat.format(calendar.getTime()) + " 23:59:59";
        }*/
        String key = filter.getSearchText();
        //Tạo câu hql
        String classSelect;
        List<ArrayList<String>> fields = new ArrayList<ArrayList<String>>();
        List<ArrayList<String>> operators = new ArrayList<ArrayList<String>>();
        List<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
        classSelect = "MCP";
        //Set type
        ArrayList<String> fieldtype = new ArrayList<String>();
        fieldtype.add("type");
        ArrayList<String> operatortype = new ArrayList<String>();
        operatortype.add("=");
        ArrayList<String> valuetype = new ArrayList<String>();
        valuetype.add(2 + "");
        fields.add(fieldtype);
        operators.add(operatortype);
        values.add(valuetype);
        ArrayList<String> fieldCompanyId = new ArrayList<String>();
        fieldCompanyId.add("implementEmployees.companys.id");
        ArrayList<String> operatorCompanyId = new ArrayList<String>();
        operatorCompanyId.add("=");
        ArrayList<String> valueCompanyId = new ArrayList<String>();
        valueCompanyId.add(userInf.getCompanyId() + "");
        fields.add(fieldCompanyId);
        operators.add(operatorCompanyId);
        values.add(valueCompanyId);
        //Xử lí fromDate và toDate

        //Xử lí fromDate
        /*if (fromDate != null && !fromDate.isEmpty() && !fromDate.trim().isEmpty() && !fromDate.equals("")) {
            //parameterList.add("salesTransDate", date1, ">=");
            ArrayList<String> field = new ArrayList<String>();
            field.add("beginDate");
            ArrayList<String> operator = new ArrayList<String>();
            operator.add(">=");
            ArrayList<String> value = new ArrayList<String>();
            value.add("'" + fromDate + "'");
            fields.add(field);
            operators.add(operator);
            values.add(value);

        }
        //Xử lí toDate
        if (toDate != null && !toDate.isEmpty() && !toDate.trim().isEmpty() && !toDate.equals("")) {
            ArrayList<String> field = new ArrayList<String>();
            field.add("beginDate");
            ArrayList<String> operator = new ArrayList<String>();
            operator.add("<=");
            ArrayList<String> value = new ArrayList<String>();
            value.add("'" + toDate + "'");
            fields.add(field);
            operators.add(operator);
            values.add(value);
        }*/
        if (filter.getFromHour() != null && filter.getFromHour() > 0) {
        	 ArrayList<String> field = new ArrayList<String>(); field.add("MONTH(beginDate)");
             ArrayList<String> operator = new ArrayList<String>();operator.add("=");
             ArrayList<String> value = new ArrayList<String>(); value.add(filter.getFromHour()+"");
             fields.add(field); operators.add(operator); values.add(value);
        }

        if (filter.getToHour() != null && filter.getToHour() > 0) {
       	 	ArrayList<String> field = new ArrayList<String>(); field.add("YEAR(beginDate)");
            ArrayList<String> operator = new ArrayList<String>();operator.add("=");
            ArrayList<String> value = new ArrayList<String>(); value.add(filter.getToHour()+"");
            fields.add(field); operators.add(operator); values.add(value);
        }
        //Xử lí searchText
        if (key != null && !key.isEmpty() && !key.trim().isEmpty() && !key.equals("")) {
            ArrayList<String> field = new ArrayList<String>();
            field.add("note"); field.add("name");
            ArrayList<String> operator = new ArrayList<String>();
            operator.add("LIKE"); operator.add("LIKE");
            ArrayList<String> value = new ArrayList<String>();
            value.add(key); value.add(key);
            fields.add(field);operators.add(operator); values.add(value);
        }
        //Xử lí userId và employeeList
        if (userId != 0) {
            ArrayList<String> field = new ArrayList<String>();field.add("implementEmployees.id");
            ArrayList<String> operator = new ArrayList<String>();operator.add("=");
            ArrayList<String> value = new ArrayList<String>(); value.add(userId + "");
            fields.add(field); operators.add(operator);values.add(value);
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
               
            }else{
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
        //Lấy dữ liệu
        List<MCP> count = new ArrayList<MCP>();
        List<MCP> lists = new ArrayList<MCP>();
        String hql2 = serviceMCP.countSQLString(classSelect, fields, operators, values);
        ArrayList<String> field = new ArrayList<String>();
        field.add("createdAt");
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
        //Check dữ liệu
        for (MCP mcp : lists) {
            //Xét tên cho nhân viên
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

        }
        //Trả về page và record
        maxPages = count.size();
        if (maxPages % size != 0) {
            maxPages = maxPages / size + 1;
        } else {
            maxPages = maxPages / size;
        }
        //Thêm dữ liệu cho Model
        uiModel.addAttribute("listKeHoachBanHang", lists);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);
        return "listPlanOfSales";
    }

    /**
     * get current month
     * <p>
     * @return
     */
    private int getCurrentMonth() {
        Calendar calen = Calendar.getInstance();
        return calen.get(Calendar.MONTH);
    }

    /**
     * get begin date and finish date of MCP
     * <p>
     * @param month is moth user select from GUI
     * <p>
     * @return
     */
    private Calendar getPlanDate(int month) {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        cal.set(Calendar.MONTH, month);
        // if user select month < current month then move this month to next year.
        if (currentMonth > month) {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        }
        return cal;
    }

    private MCP createDatePlanOfSale(WebMCP webMCP) {
        MCP mcp = webMCP.getMcp();
        Calendar cal = getPlanDate(webMCP.getMonth());
        Date date = cal.getTime();
        mcp.setBeginDate(new Date(date.getYear(), date.getMonth(), 1));
        mcp.setFinishTime(new Date(date.getYear(), date.getMonth(), cal.getMaximum(Calendar.DAY_OF_MONTH)));

        return mcp;
    }

    private int createPlan(HttpServletRequest request, MCP mcp) throws IOException, JsonParseException {
        mcp.setCreatedUser(LoginContext.getLogin(request).getId());
        if(mcp.getSalesFocusPerMonth() == null){
        	mcp.setSalesFocusPerMonth((long)0);
        }
        // set by default sales
        mcp.setStatusId(1);
        mcp.setType(2);
        mcp.setIsActive(1);
        int created = mcpController.getDataService().insertRow(mcp);
        if (created > 0) {
            /*  List<MCPSalesDetails> mcpSDList = mcp.getMcpSalesDetailss();
             if (mcpSDList != null) {
             for (MCPSalesDetails mCPSalesDetails : mcpSDList) {
             mCPSalesDetails.setMcps(mcp);
             mCPSalesDetails.setFinishTime(mcp.getFinishTime());
             mCPSalesDetails.setCreatedUser(LoginContext.getLogin(request).getId());
             mCPSalesDetails.setIsActive(1);
             mcpSalesDetailsController.getDataService().insertRow(mCPSalesDetails);
             }
             }*/
            //  request.setAttribute("infoMessage", "Giao chỉ tiêu thành công.");
            //  request.setAttribute("created", true);
        } else {
            request.setAttribute("errorMessage", "Không thể giao chỉ tiêu");
        }
        return created;
    }

    private MCP initEdit(int mcpId) {
        //get MCP with id
        MCP mcpFound = mcpController.getDataService().getRowById(mcpId, MCP.class);
//        if (mcpFound != null) {
//            //get list MCPSalesDetails
//            ParameterList prams = new ParameterList();
//            prams.add("mcps.id", mcpFound.getId());
//            List<MCPSalesDetails> mCPSalesDetailses = mcpController.getDataService().getListOption(MCPSalesDetails.class, prams);
//            mcpFound.setMcpSalesDetailss(mCPSalesDetailses);
//        }

        return mcpFound;
    }

}
