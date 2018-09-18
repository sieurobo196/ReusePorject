/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.sales.controller;

import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsUnit;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.SalesOrder;
import vn.itt.msales.entity.SalesOrderDetails;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.SalesStockGoods;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.SalesTransDetails;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserGoodsCategory;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.user.controller.MsalesUserController;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;
import vn.itt.msales.user.model.URLManager;
import vn.itt.msales.workflow.service.ServiceMCP;

/**
 *
 * @author vtm
 */
@Controller
public class WebMsalesOrderController {

    @Autowired
    View jsonView;
    @Autowired
    private DataService dataService;
    @Autowired
    private ServiceMCP serviceMCP;
    @Autowired
    private ServiceFilter serviceFilter;

    @RequestMapping(value = "/order/list")
    protected String listOrders(Model uiModel, HttpServletRequest request,
            @ModelAttribute(value = "filterModel") Filter filter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size)
            throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;

        //getCbListStatusByStatusTypeId - trang thai don hang =>type =6
        ParameterList parameterList = new ParameterList("statusTypes.id", 6, 0, 0);
        List<Integer> idStatuss = new ArrayList<>();
        idStatuss.add(13);idStatuss.add(14); idStatuss.add(18);
        idStatuss.add(19); idStatuss.add(20);
        parameterList.in("id", idStatuss);
        List<Status> status = dataService.getListOption(Status.class, parameterList);

        uiModel.addAttribute("statusList", status);
        //tao du lieu
        
        //set role cho cbb Nhan vien
        filter.setRoles(new Integer[]{6});
        uiModel.addAttribute("roles", filter.getRolesString());
        List<OptionItem> employerList = filter.processFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());

        //Dữ liệu:
        int userId = 0;
        if (filter.getUserId() != null && filter.getUserId() != 0) {
            userId = filter.getUserId();
        }
        String fromDate = filter.getStartDateString();
        String toDate = filter.getEndDateString();
        String key = filter.getSearchText();
        int channelId = filter.getIdChannel();
        int statusId = 0;
        if (filter.getStatusId() != null && filter.getStatusId() != 0) {
            statusId = filter.getStatusId();
        }
        //Search
        String hql = "SELECT S ";
        String hqlCount = "SELECT S.id as id";
        String hqlS = " FROM SalesOrder as S,MCPDetails as M WHERE S.deletedUser = 0"
        		+ " AND M.deletedUser = 0 AND M.mcps.deletedUser = 0"
        		+ " AND S.pos.id = M.poss.id"
        		+ " AND WEEKOFYEAR(S.createdAt) = WEEKOFYEAR(M.mcps.beginDate)"
        		+ " AND YEAR(S.createdAt) = YEAR(M.mcps.beginDate)"
        		+ " AND S.companys.id = "+userInf.getCompanyId();

        String stringX = "";
        if (userId != 0) {
        	hqlS += " AND M.implementEmployees.id = "+userId;
        	stringX=""+userId;
        }else{
            String string = "";       	
            for(int i = 0; i < employerList.size(); i++){
                string += employerList.get(i).getId() + ",";
            }
            string += "''";
            stringX = string;
            hqlS += " AND M.implementEmployees.id IN ("+string+")";      	
        }
        //Check statusId
        if (statusId != 0) {
        	hqlS += " AND S.statuss.id = "+ statusId;
        }
        //Check fromDate
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (fromDate != null && !fromDate.isEmpty() && !fromDate.trim().isEmpty() && !fromDate.equals("")) {
            try {
                Date date1 = sDateFormat.parse(fromDate);
                hqlS += " AND S.createdAt >= '"+simpleDateFormat.format(date1)+"'";
            } catch (ParseException e) {
            }
        }
        //check theo toDate
        if (toDate != null && !toDate.isEmpty() && !toDate.trim().isEmpty() && !toDate.equals("")) {
            try {
                Date date1 = sDateFormat.parse(toDate);
                hqlS += " AND S.createdAt <= '"+simpleDateFormat.format(date1)+" 23:59:59'";
            } catch (ParseException e) {
            }
        }
        //Check theo searchText
        if (key != null && !key.isEmpty() && !key.trim().isEmpty() && !key.equals("")) {
            key = key.replaceAll("'", "''"); key = key.replaceAll(" ", "%");
            String sql = "select user.ID as id "
                    + " from user  where user.DELETED_USER = 0 "
                    + " and user.COMPANY_ID = " + userInf.getCompanyId();
                  //  + " and user.STATUS_ID = 6 ";          
                ArrayList<String> searchText2 = unaccent(key);

                String string = "";
                sql += " AND ( concat(user.LAST_NAME,user.FIRST_NAME) LIKE '%" + key + "%' COLLATE utf8_unicode_ci ";
                if (!searchText2.isEmpty()) {
                    for (int i = 0; i < searchText2.size(); i++) {
                        string += " OR concat(user.LAST_NAME,user.FIRST_NAME) LIKE '%" + searchText2.get(i) + "%' COLLATE utf8_unicode_ci ";
                    }
                    sql += string;
                }
                sql += " ) ";
                sql += " AND user.ID IN ("+stringX+")";
            sql +=  " GROUP BY user.id ";
            List<HashMap> listUsers = dataService.execSQL(sql);        
            if (!listUsers.isEmpty()) {
            	String string2 = "";
                for (HashMap id : listUsers) {                	                    
                	string2 += id.get("id") + ",";
        		}
        		string2 += "''";
        		stringX = string2;
        		hqlS += " AND M.implementEmployees.id IN ("+string2+")";
               
            } else {
            	hqlS += " AND M.implementEmployees.id IN ("+0+")";
            	stringX = 0+"";
            }

        }
        

        //Search
        List<SalesOrder> count = new ArrayList<SalesOrder>();
        List<SalesOrder> lists = new ArrayList<SalesOrder>();
        hqlS+= " GROUP BY S.id ORDER BY S.createdAt DESC";
        try {
            count = dataService.executeSelectHQL(SalesOrder.class,hqlCount+hqlS , true, 0, 0);
            lists = dataService.executeSelectHQL(SalesOrder.class, hql+hqlS, false, page, size);
        } catch (Exception ex) {

        }

        //Check dữ liệu
        
        for (SalesOrder salesOrder : lists) {
            String hqlUser = serviceMCP.getUserForOrderListByPosId(salesOrder.getPos().getId());
            hqlUser += " AND MCP.implementEmployees.id IN ("+stringX+")";
            String hqlX = hqlUser + " and MCP.mcps.beginDate >= '" + simpleDateFormat.format(salesOrder.getCreatedAt()) + " 00:00:00'"
        			+ " and MCP.mcps.beginDate < '" + simpleDateFormat.format(salesOrder.getCreatedAt()) + " 23:59:59'";
            hqlUser += " order by MCP.createdAt desc";	
            List<HashMap> listHashMaps = dataService.executeSelectHQL(HashMap.class, hqlX, true, 1, 1);
        	if(listHashMaps.isEmpty()){
        		listHashMaps = dataService.executeSelectHQL(HashMap.class, hqlUser, true, 1, 1);
        	}
            User user = new User();
            if(!listHashMaps.isEmpty()){
            	user = (User) listHashMaps.get(0).get("user");
            	salesOrder.setCreatedUsers(user);
            }
            //Check createdUser 
            if (salesOrder.getCreatedUsers() != null) {
		if (salesOrder.getCreatedUsers().getLocations().getLocationType() == 1) {
                    salesOrder.getCreatedUsers().setLocation(salesOrder.getCreatedUsers().getLocations().getName());
		} else if (salesOrder.getCreatedUsers().getLocations().getParents().getLocationType() == 1) {
                    salesOrder.getCreatedUsers().setLocation(salesOrder.getCreatedUsers().getLocations().getParents().getName());
		} else {
                    salesOrder.getCreatedUsers().setLocation(salesOrder.getCreatedUsers().getLocations().getParents().getParents().getName());
		}
            }
            List<SalesOrderDetails> salesOrderDetails = dataService.getListOption(SalesOrderDetails.class,
					new ParameterList("orders.id", salesOrder.getId()));
            int quantity = 0;
            for (SalesOrderDetails sOrderDetails : salesOrderDetails) {
                    quantity += sOrderDetails.getQuantity();
            }
            salesOrder.setQuantity(quantity);
			// set tinh thành cho nhân viên
        }

        maxPages = count.size();
        if (maxPages % size != 0) {
            maxPages = maxPages / size + 1;
        } else {
            maxPages = maxPages / size;
        }

        uiModel.addAttribute("orderList", lists);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);
        //  uiModel.addAttribute("orderListForm", lists);

        return "orderList";
    }

//    public String viewOrder(Model uiModel, LinkedHashMap salesOrderAdmin) {
//        //int type = KenhBanHang.getType(order.getToStockId());
////        if (type == MemberType.POS.id) {
////            DiemBanHang pos = posService.find(KenhBanHang.getId(order.getToStockId()));
////            order.setNoiDatHang(pos.getTenCuaHang());
////            order.setDiaChi(pos.getAddress());
////        }
////        uiModel.addAttribute("order", salesOrderAdmin);
////        return "orderView";
//    }
    @RequestMapping(value = "/order/view/{id}")
    protected String viewOrder(Model uiModel, @PathVariable("id") Integer id, HttpServletRequest request)
            throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

     //   request.setAttribute("onlyRead", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"}));
        request.setAttribute("onlyExport", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY","USER_ROLE_SALES_SUPERVISOR"}));
        //Lấy dữ liệu
        SalesOrder salesOrder = dataService.getRowById(id, SalesOrder.class);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (salesOrder != null) {
        	String hqlUser = serviceMCP.getUserForOrderListByPosId(salesOrder.getPos().getId());
        		//	"Select MCP.implementEmployees as user"
        		//	+ " from MCPDetails as MCP where MCP.deletedUser = 0 and MCP.poss.id = "+salesOrder.getPos().getId();
        	String hqlX = hqlUser + " and MCP.mcps.beginDate >= '" + simpleDateFormat.format(salesOrder.getCreatedAt()) + " 00:00:00'"
        			+ " and MCP.mcps.beginDate < '" + simpleDateFormat.format(salesOrder.getCreatedAt()) + " 23:59:59'";
        	hqlUser += " order by MCP.createdAt desc";	
        	List<HashMap> listHashMaps = dataService.executeSelectHQL(HashMap.class, hqlX, true, 1, 1);
        	if(listHashMaps.isEmpty()){
        		listHashMaps = dataService.executeSelectHQL(HashMap.class, hqlUser, true, 1, 1);
        	}
            User user = new User();
            if(!listHashMaps.isEmpty()){
            	user = (User) listHashMaps.get(0).get("user");
            	salesOrder.setCreatedUsers(user);
            }
            List<SalesOrderDetails> salesOrderDetails = dataService.getListOption(SalesOrderDetails.class, new ParameterList("orders.id", salesOrder.getId()));
            if (!salesOrderDetails.isEmpty()) {
                salesOrder.setOrderDetailsList(salesOrderDetails);
            }
        }

        // check quantity of inventory is enough for exporting 
        boolean exportable = true;

        uiModel.addAttribute("exportable", exportable);
        //return viewOrder(uiModel, "");
        //return viewOrder(uiModel, salesOrderAdmin);
        uiModel.addAttribute("order", salesOrder);
        return "orderView";
    }

    @RequestMapping(value = "/order/update/{id}", method = RequestMethod.POST)
    protected String updateOrder(Model uiModel, RedirectAttributes redirectAttributes,
            @PathVariable("id") Integer id, HttpServletRequest request)
            throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf user = LoginContext.getLogin(request);
        SalesOrder salesOrder = dataService.getRowById(id, SalesOrder.class);
        salesOrder.setUpdatedUser(user.getId());
        salesOrder.setUpdatedAt(new Date());
        dataService.updateSync(salesOrder);
        LinkedHashMap contents = new LinkedHashMap();
        ArrayList<LinkedHashMap> salesOrderDetails = new ArrayList<>();
        for (int i = 0;; i++) {

            String txtId = "txtId" + i;
            String txtQuantity = "txtQuantity" + i;
            String idx = request.getParameter(txtId);
            String quantity = request.getParameter(txtQuantity);
            if (idx == null || quantity == null) {
                break;
            } else if (idx.isEmpty() || quantity.isEmpty()) {
                redirectAttributes.addFlashAttribute("update", false);
                return "redirect:/order/view/" + id;
            }

            LinkedHashMap item = new LinkedHashMap();
            item.put("id", idx);
            item.put("quantity", quantity);
            salesOrderDetails.add(item);
        }
        List<SalesOrderDetails> listSalesOrderDetails = new ArrayList<SalesOrderDetails>();
        for (LinkedHashMap orderDetails : salesOrderDetails) {
            int orderDetailsId = Integer.parseInt(orderDetails.get("id").toString());
            int quantity = Integer.parseInt(orderDetails.get("quantity").toString());
            SalesOrderDetails salesOrderDetails2 = dataService.getRowById(orderDetailsId, SalesOrderDetails.class);
            salesOrderDetails2.setQuantity(quantity);
            salesOrderDetails2.setUpdatedUser(user.getId());
            salesOrderDetails2.setUpdatedAt(new Date());
            listSalesOrderDetails.add(salesOrderDetails2);
        }
        if (listSalesOrderDetails.isEmpty()) {
            redirectAttributes.addFlashAttribute("update", false);
            return "redirect:/order/view/" + id;
        }
        try {
            boolean ret = dataService.updateArray(listSalesOrderDetails);
            if (ret) {
                redirectAttributes.addFlashAttribute("update", true);
            } else {
                redirectAttributes.addFlashAttribute("update", false);
            }
        } catch (Exception ex) {

        }

        return "redirect:/order/view/" + id;
//        contents.put("salesOrderDetails", salesOrderDetails);
//        //gan cung = 1
//        contents.put("updatedUser", 1);
//        String requestString = MsalesJsonUtils.getJSONFromOject(contents);
//
//        request.setAttribute(MsalesConstants.CONTENTS, requestString);
//        String responseString = salesOrderDetailsController.updateListSalesOrderDetails(request);
//        MsalesResponse msalesResponse = MsalesJsonUtils.getObjectFromJSON(responseString, MsalesResponse.class);
//
//        if (msalesResponse.getStatus().getCode() == HttpStatus.OK.value()) {
//            //update thanh cong
//            //request.setAttribute("update", true); 
//            redirectAttributes.addFlashAttribute("update", true);
//        } else {
//            //request.setAttribute("update", false); 
//            redirectAttributes.addFlashAttribute("update", false);
//        }

//        redirectAttributes.addFlashAttribute("update", true);
//        return "redirect:/order/view/" + id;
    }
//	

    @RequestMapping(value = "/order/cancel/{id}")
    protected String cancelOrder(Model uiModel, @PathVariable("id") Integer id,
            HttpServletRequest request, RedirectAttributes redirectAttributes)
            throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf user = LoginContext.getLogin(request);
        SalesOrder salesOrder = dataService.getRowById(id, SalesOrder.class);
        salesOrder.setUpdatedUser(user.getId());
        salesOrder.setUpdatedAt(new Date());
        salesOrder.setStatusId(14);

        int ret = dataService.updateSync(salesOrder);
        if (ret > 0) {
            redirectAttributes.addFlashAttribute("cancelSuccess", true);
        } else {
            redirectAttributes.addFlashAttribute("cancelSuccess", false);
        }

        //gan cung createUser
//        request.setAttribute(MsalesConstants.CONTENTS, "{\"id\":" + id + ",\"updatedUser\":1}");
//        String responseString = salesOrderController.deactiveSalesOrder(request);
//        MsalesResponse msalesResponse = MsalesJsonUtils.getObjectFromJSON(responseString, MsalesResponse.class);
//
//        if (msalesResponse.getStatus().getCode() == HttpStatus.OK.value()) {
//            //huy hang thanh cong
//            redirectAttributes.addFlashAttribute("cancelSuccess", true);
//        } else {
//            //huy hang that bai
//            redirectAttributes.addFlashAttribute("cancelSuccess", false);
//        }
        return "redirect:/order/view/" + id;
    }

    @RequestMapping(value = "/order/do/{id}")
    protected String doOrder(Model uiModel, @PathVariable("id") Integer id,
            HttpServletRequest request, RedirectAttributes redirectAttributes) throws JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf user = LoginContext.getLogin(request);
        //Check hàng
        SalesOrder salesOrder = dataService.getRowById(id, SalesOrder.class);
        if (salesOrder == null) {
            redirectAttributes.addFlashAttribute("success", false);
            return "redirect:/order/view/" + id;
        }
        //Lay nhan vien cham soc cua pos
        
        //Lấy kho hàng của người bán hàng
//        List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("salemanUsers.id", userId));
//        if (salesStocks.size() == 0) {
//            redirectAttributes.addFlashAttribute("notInventory", true);
//            return "redirect:/order/view/" + id;
//        }
//        int salesStockId = salesStocks.get(0).getId();
        //cập nhật người xuất kho
        salesOrder.setUpdatedUser(user.getId());
        salesOrder.setUpdatedAt(new Date());
        //Lấy danh sách sales Order details
        List<SalesOrderDetails> salesOrderDetails = dataService.getListOption(SalesOrderDetails.class, new ParameterList("orders.id", salesOrder.getId()));

        if (!salesOrderDetails.isEmpty()) {
//            for (SalesOrderDetails sOrderDetails : salesOrderDetails) {
//                //Lấy số lượng hàng của hóa đơn.
//                int quantity = sOrderDetails.getQuantity();
//                //Lấy số lượng hàng của Sales_man_stock
//                ParameterList parameterList = new ParameterList();
//                parameterList.add("stocks.id", salesStockId);
//                parameterList.add("goodss.id", sOrderDetails.getGoodss().getId());
//                List<SalesStockGoods> salesStockGoods = dataService.getListOption(SalesStockGoods.class, parameterList);
//                //Kiểm tra số lượng trong kho
//                int sluongGoods = 0;
//                if (salesStockGoods.size() > 0) {
//                    for (SalesStockGoods sGoods : salesStockGoods) {
//                        sluongGoods = sluongGoods + sGoods.getQuantity();
//                    }
//                } else {
//                    redirectAttributes.addFlashAttribute("notInventory", true);
//                    return "redirect:/order/view/" + id;
//                }
//                if (quantity > sluongGoods) {
//                    redirectAttributes.addFlashAttribute("notInventory", true);
//                    return "redirect:/order/view/" + id;
//                }
//
//            }
        } else {
            redirectAttributes.addFlashAttribute("success", false);
            return "redirect:/order/view/" + id;
        }
        //Tạo salesTrans
        
        //Bỏ SalesTrans 
        
//        SalesTrans salesTrans = new SalesTrans();
//        salesTrans.setCompanyId(user.getCompanyId());
//        if (salesOrder.getStocks().getId() != null) {
//            salesTrans.setToStockId(salesOrder.getStocks().getId());
//        }
//
//      //  salesTrans.setFromStockId(salesStockId);
//        salesTrans.setOrderId(salesOrder.getId());
//        salesTrans.setTransStatus(1);
//        salesTrans.setSalesTransDate(new Date());
//        salesTrans.setCreatedUser(user.getId());
//        salesTrans.setTransType(2);
        //Thực hiện giao dịch
//        int ret = 0;
//        try {
//            ret = dataService.insertRow(salesTrans);
//        } catch (Exception ex) {
//            redirectAttributes.addFlashAttribute("success", false);
//            return "redirect:/order/view/" + id;
//        }
        
        //insert SalesTrans thành công
      //  if (ret > 0) {
            //Lấy dữ liệu vào salesTranDetails để insert 1 mảng
        
        //Chỉ Cộng kho cho POS
            List<Object> salesTransDetails = new ArrayList<Object>();

            for (SalesOrderDetails sDetails : salesOrderDetails) {
            	
//                SalesTransDetails sTransDetails = new SalesTransDetails();
//                sTransDetails.setSalesTransId(ret);
//                sTransDetails.setGoodsId(sDetails.getGoodss().getId());
//                sTransDetails.setGoodsUnitId(sDetails.getGoodsUnits().getId());
//                sTransDetails.setSalesTransDate(new Date());
//                sTransDetails.setPrice(sDetails.getPrice());
//                sTransDetails.setQuantity(sDetails.getQuantity());
//                sTransDetails.setIsFocus(sDetails.getIsFocus());
//                sTransDetails.setCreatedUser(user.getId());
//                sTransDetails.setUpdatedUser(0);
//                sTransDetails.setDeletedUser(0);
//                if (sTransDetails != null) {
//                    salesTransDetails.add(sTransDetails);
//                }
            	
//                //trừ kho hàng của nhân viên bán hàng
            	
//                ParameterList parameterList4 = new ParameterList();
//                parameterList4.add("stocks.id", salesStockId);
//                parameterList4.add("goodss.id", sDetails.getGoodss().getId());
//                parameterList4.add("goodsUnits.id", sDetails.getGoodsUnits().getId());
//                List<SalesStockGoods> salesStockGoodss = dataService.getListOption(SalesStockGoods.class, parameterList4);
//                if (!salesStockGoodss.isEmpty()) {
//                    //Trừ hàng hóa trong kho
//                    for (SalesStockGoods stockGoods : salesStockGoodss) {
//                        //Xét property cho salesStockGoods
//                        int quantity = stockGoods.getQuantity();
//                        stockGoods.setQuantity(quantity - sDetails.getQuantity() / salesStockGoodss.size());
//                        stockGoods.setUpdatedUser(user.getId());
//                        stockGoods.setUpdatedAt(new Date());
//                    }
//                    salesTransDetails.addAll(salesStockGoodss);
//                }
            	
//               //Cộng kho của POS
                ParameterList parameterList5 = new ParameterList();
                parameterList5.add("stocks.id", salesOrder.getStocks().getId());
                parameterList5.add("goodss.id", sDetails.getGoodss().getId());
                parameterList5.add("goodsUnits.id", sDetails.getGoodsUnits().getId());
                List<SalesStockGoods> salesStockGoodss2 = dataService.getListOption(SalesStockGoods.class, parameterList5);
                if (!salesStockGoodss2.isEmpty()) {
                    //Nếu đã có thì cần cộng thêm
                    for (SalesStockGoods stockGoods : salesStockGoodss2) {
                        //Xét property cho salesStockGoods
                        int quantity = stockGoods.getQuantity();
                        stockGoods.setQuantity(quantity + sDetails.getQuantity());
                        stockGoods.setUpdatedUser(user.getId());
                        stockGoods.setUpdatedAt(new Date());
                    }
                    salesTransDetails.addAll(salesStockGoodss2);
                } else {
                    //Nếu chưa có hàng hóa trong kho thì tạo mới record
                    SalesStockGoods stockGoods = new SalesStockGoods();
                    stockGoods.setStockId(salesOrder.getStocks().getId());
                    stockGoods.setGoodsId(sDetails.getGoodss().getId());
                    stockGoods.setGoodsUnitId(sDetails.getGoodsUnits().getId());
                    stockGoods.setGoodsStatusId(15);
                    stockGoods.setQuantity(sDetails.getQuantity());
                    stockGoods.setIsActive(1);
                    stockGoods.setActiveDate(new Date());
                    stockGoods.setCreatedUser(user.getId());
                    stockGoods.setUpdatedUser(0);
                    stockGoods.setDeletedUser(0);
                    salesTransDetails.add(stockGoods);
                }
          //  }
            if (!salesTransDetails.isEmpty()) {
                //insert tất cả salesTransDetails
                try {
                    dataService.insertOrUpdateArray(salesTransDetails);
                } catch (Exception ex) {
                	
//                    SalesTrans sTrans = dataService.getRowById(ret, SalesTrans.class);
//                    sTrans.setDeletedAt(new Date());
//                    sTrans.setDeletedUser(user.getId());
//                    try {
//                        dataService.updateRow(sTrans);
//                    } catch (Exception e) {
//
//                    }
                    redirectAttributes.addFlashAttribute("success", false);
                    return "redirect:/order/view/" + id;
                }
                //if insert salesTransDetails thành công
                //Update trạng thái giao hàng thành công cho salesOrder
                //check user dang nhap
                if(user.getUserRoleCode().equals("USER_ROLE_ADMIN_COMPANY") || user.getUserRoleCode().equals("USER_ROLE_ADMIN_CHANNEL") ){
                	salesOrder.setStatusId(19);
                }else if(user.getUserRoleCode().equals("USER_ROLE_SALES_SUPERVISOR")){
                	salesOrder.setStatusId(20);
                }
                
                try {
                    dataService.updateRow(salesOrder);
                } catch (Exception exp) {
//                    SalesTrans sTrans = dataService.getRowById(ret, SalesTrans.class);
//                    sTrans.setDeletedAt(new Date());
//                    sTrans.setDeletedUser(user.getId());
//                    try {
//                        dataService.updateRow(sTrans);
//                    } catch (Exception e) {
//
//                    }
                    redirectAttributes.addFlashAttribute("success", false);
                    return "redirect:/order/view/" + id;
                }
                //Update salesStockGoods
                redirectAttributes.addFlashAttribute("success", true);

            } else {
                redirectAttributes.addFlashAttribute("success", false);
                return "redirect:/order/view/" + id;
            }

        }
//            else {
//            redirectAttributes.addFlashAttribute("success", false);
//            return "redirect:/order/view/" + id;
//        }

        return "redirect:/order/view/" + id;
    }

    /**
     * DuanND
     *
     * @param uiModel
     * @param request
     * @param filter
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "order/requestGoods/list", method = {RequestMethod.GET, RequestMethod.POST})
    protected String listSalesTrans(Model uiModel, HttpServletRequest request,
            @ModelAttribute(value = "requestForm") Filter filter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size)
            throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;

        List<OptionItem> statusList = new ArrayList<>();
        //Tạo một OptionItem statusList
        statusList.addAll(OptionItem.NoOptionList(1, "Nhận hàng"));
        statusList.addAll(OptionItem.NoOptionList(3, "Trả hàng"));

        uiModel.addAttribute("statusList", statusList);

        //set role cbb nhan vien
        filter.setRoles(new Integer[]{6,4});
        List<OptionItem> employerList = filter.processFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());

        //Lấy dữ liệu cho Controller
        //Lấy createdUser
        int userId = 0;
        if (filter.getUserId() != null && filter.getUserId() != 0) {
            userId = filter.getUserId();
        }
        String fromDate = filter.getStartDateString();
        String toDate = filter.getEndDateString();
        String key = filter.getSearchText();
        int transType = 0;
        if (filter.getStatusId() != null && filter.getStatusId() != 0) {
            transType = filter.getStatusId();
        }
        //seach SalesTrans theo thông số.
        String classSelect = "SalesTrans";
        List<ArrayList<String>> fields = new ArrayList<ArrayList<String>>();
        List<ArrayList<String>> operators = new ArrayList<ArrayList<String>>();
        List<ArrayList<String>> values = new ArrayList<ArrayList<String>>();

        ArrayList<String> fieldCompanyId = new ArrayList<String>();
        fieldCompanyId.add("companys.id");
        ArrayList<String> operatorCompanyId = new ArrayList<String>();
        operatorCompanyId.add("=");
        ArrayList<String> valueCompanyId = new ArrayList<String>();
        valueCompanyId.add(userInf.getCompanyId() + "");
        fields.add(fieldCompanyId);
        operators.add(operatorCompanyId);
        values.add(valueCompanyId);
        //check transType
        if (transType != 0) {
            ArrayList<String> fieldtype = new ArrayList<String>();
            fieldtype.add("transType");
            ArrayList<String> operatortype = new ArrayList<String>();
            operatortype.add("=");
            ArrayList<String> valuetype = new ArrayList<String>();
            valuetype.add(transType + "");
            fields.add(fieldtype);
            operators.add(operatortype);
            values.add(valuetype);
        } else {
            ArrayList<String> fieldtype = new ArrayList<String>();
            fieldtype.add("transType");
            fieldtype.add("transType");
            ArrayList<String> operatortype = new ArrayList<String>();
            operatortype.add("=");
            operatortype.add("=");
            ArrayList<String> valuetype = new ArrayList<String>();
            valuetype.add(1 + "");
            valuetype.add(3 + "");
            fields.add(fieldtype);
            operators.add(operatortype);
            values.add(valuetype);
        }
        //Check fromDate
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (fromDate != null && !fromDate.isEmpty() && !fromDate.trim().isEmpty() && !fromDate.equals("")) {
            try {
                Date date1 = sDateFormat.parse(fromDate);
              //  String fromDate2 = simpleDateFormat.format(date1);
            //    Date date2 = simpleDateFormat.parse(fromDate2);
                //parameterList.add("salesTransDate", date1, ">=");
                ArrayList<String> field = new ArrayList<String>();field.add("salesTransDate");
                ArrayList<String> operator = new ArrayList<String>();operator.add(">=");
                ArrayList<String> value = new ArrayList<String>();value.add("'" + simpleDateFormat.format(date1) + "'");
                fields.add(field);operators.add(operator);values.add(value);
            } catch (ParseException e) {

            }
        }
        //Check toDate
        if (toDate != null && !toDate.isEmpty() && !toDate.trim().isEmpty() && !toDate.equals("")) {
            try {
                Date date1 = sDateFormat.parse(toDate);
              //  String toDate2 = simpleDateFormat.format(date1);
              //  Date date2 = simpleDateFormat.parse(toDate2);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);
                calendar.add(calendar.DAY_OF_MONTH, 1);
                //parameterList.add("salesTransDate", date2, "<=");
                ArrayList<String> field = new ArrayList<String>();field.add("salesTransDate");
                ArrayList<String> operator = new ArrayList<String>();operator.add("<");
                ArrayList<String> value = new ArrayList<String>();value.add("'" + simpleDateFormat.format(calendar.getTime()) + "'");
                fields.add(field); operators.add(operator);values.add(value);
            } catch (ParseException e) {
            }
        }
        //Check search text
        if (key != null && !key.isEmpty() && !key.trim().isEmpty() && !key.equals("")) {
            ArrayList<String> field = new ArrayList<String>();
            field.add("note"); field.add("transCode");
            ArrayList<String> operator = new ArrayList<String>();
            operator.add("LIKE"); operator.add("LIKE");
            ArrayList<String> value = new ArrayList<String>();
            value.add(key);  value.add(key);
            fields.add(field); operators.add(operator); values.add(value);
        }
        //Check userId
        if (userId != 0) {

            ArrayList<String> field = new ArrayList<String>(); field.add("createdUser");
            ArrayList<String> operator = new ArrayList<String>();  operator.add("=");
            ArrayList<String> value = new ArrayList<String>();value.add(userId + "");
            fields.add(field); operators.add(operator);  values.add(value);
        } else {
            ArrayList<String> field = new ArrayList<String>();
            ArrayList<String> operator = new ArrayList<String>();
            ArrayList<String> value = new ArrayList<String>();
            if (employerList.size() > 1) {
                for (int i = 1; i < employerList.size(); i++) {
                    int id = employerList.get(i).getId();
                    if (id != 0) {
                        List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("salemanUsers.id", id));
                        if (!salesStocks.isEmpty()) {
                            field.add("fromStocks.id"); field.add("toStocks.id");
                            operator.add("=");  operator.add("=");
                            value.add(salesStocks.get(0).getId() + ""); value.add(salesStocks.get(0).getId() + "");
                        }
                    }
                }
                
            }else{
            	field.add("fromStocks.id"); field.add("toStocks.id");
                operator.add("=");  operator.add("=");
                value.add(-1 + ""); value.add(-1 + "");
            }
            if (field.size() > 0) {
                fields.add(field); operators.add(operator); values.add(value);
            }
        }
        //Lấy dữ liệu
        List<SalesTrans> count = new ArrayList<SalesTrans>();
        List<SalesTrans> lists = new ArrayList<SalesTrans>();
        String hql2 = serviceMCP.countSQLString(classSelect, fields, operators, values);
        ArrayList<String> field = new ArrayList<String>();  field.add("createdAt");
        ArrayList<String> operator = new ArrayList<String>(); operator.add("order by");
        ArrayList<String> value = new ArrayList<String>(); value.add("desc");
        fields.add(field);  operators.add(operator); values.add(value);
        String hql = serviceMCP.getSQLString(classSelect, fields, operators, values);

        try {
            count = dataService.executeSelectHQL(SalesTrans.class, hql2, true, 0, 0);
            lists = dataService.executeSelectHQL(SalesTrans.class, hql, false, page, size);
        } catch (Exception ex) {
        }
        if (!lists.isEmpty()) {
            for (SalesTrans salesTrans : lists) {
                if (salesTrans.getTransType() == 1) {
                    if (salesTrans.getFromStocks() != null && salesTrans.getFromStocks().getChannels() != null) {
                        salesTrans.setChannel(salesTrans.getFromStocks().getChannels());
                    }

                } else {
                    if (salesTrans.getToStocks() != null && salesTrans.getToStocks().getChannels() != null) {
                        salesTrans.setChannel(salesTrans.getToStocks().getChannels());
                    }
                }
                if(salesTrans.getChannel() == null){
                	String hqlC = serviceMCP.getStringHQLNVBH(salesTrans.getCreatedUser(), userInf.getCompanyId());
                			
                	List<HashMap> lHashMaps = dataService.executeSelectHQL(HashMap.class, hqlC, true, 1, 1);
                	Channel channel = new Channel();
                	if(!lHashMaps.isEmpty()){
                		channel.setFullCode((String)lHashMaps.get(0).get("fullCode"));
                		channel.setName((String)lHashMaps.get(0).get("name"));
                		salesTrans.setChannel(channel);
                	}
                }
                int createdId = salesTrans.getCreatedUser();
                User user = dataService.getRowById(createdId, User.class);
                if (user != null) {
                    user.setName(user.getLastName() + " " + user.getFirstName());
                    if (user.getLocations().getLocationType() == 1) {
                        user.setLocation(user.getLocations().getName());
                    } else if (user.getLocations().getParents().getLocationType() == 1) {
                        user.setLocation(user.getLocations().getParents().getName());
                    } else {
                        user.setLocation(user.getLocations().getParents().getParents().getName());
                    }
                    salesTrans.setEmployee(user);
                }
            }
        }

        maxPages = count.size();
        if (maxPages % size != 0) {
            maxPages = maxPages / size + 1;
        } else {
            maxPages = maxPages / size;
        }

        uiModel.addAttribute("salesTransList", lists);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);

        return "requestList";
    }

//	@RequestMapping(value = "order/requestGoods/list", method = { RequestMethod.GET, RequestMethod.POST })
//	public String listInventory(
//			@ModelAttribute("requestForm") Filter filter,
//			@RequestParam(value = "page", required = false) Integer page,
//			@RequestParam(value = "size", required = false) Integer size,
//			HttpServletRequest request, Model uiModel) throws Exception {
//		
////			filterService.checkCompanyPermission(user, inventoryFilter, uiModel);
//			filterService.addAreaSelectionForUserToModel(user, inventoryFilter, uiModel);
//			
//			uiModel.addAttribute("requestForm", inventoryFilter);
//			
//			List<OptionItem> statusList = OptionItem.NoOptionList(99, "--Trạng Thái--");
//			statusList.add(new OptionItem(0,"Chờ"));
//			statusList.add(new OptionItem(1,"Duyệt"));
//			statusList.add(new OptionItem(2,"Hủy"));
//			uiModel.addAttribute("statusList", statusList);
//			
//			// Count
//			long count = inventoryService.inventoryCount(inventoryFilter);
//	
//			PaginationInfo paginationInfo = PaginationUtil.calculatePage(count,
//					page, size);
//			uiModel.addAttribute("size", paginationInfo.getSize());
//			uiModel.addAttribute("page", paginationInfo.getPage());
//			uiModel.addAttribute("maxPages", paginationInfo.getMaxPages());
//	
//			List<SalesInventory> inventoryList = inventoryService.listInventory(inventoryFilter, paginationInfo.getStart(), paginationInfo.getSize());
//
//			uiModel.addAttribute("inventoryList", inventoryList);
//			return "requestList";
//	}
    @RequestMapping(value = "/order/approve", method = RequestMethod.POST)
    public ModelAndView approveRequest(HttpServletRequest request,
            @RequestParam("stockId") Long stockId) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        String status = "OK";
//		try {
//			inventoryService.updateInventoryStatus(stockId, (long)1);	
//		} catch (Exception e) {
//			status = "Not OK";
//		}
        String jsonView = "";
        return new ModelAndView(jsonView, "status", status);
    }

    @RequestMapping(value = "/order/cancel", method = RequestMethod.POST)
    public ModelAndView cancelRequest(HttpServletRequest request, @RequestParam("stockId") Long stockId) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        String status = "OK";
//		try {
//			inventoryService.updateInventoryStatus(stockId, (long)2);	
//		} catch (Exception e) {
//			status = "Not OK";
//		}
        String jsonView = "";
        return new ModelAndView(jsonView, "status", status);
    }

    @RequestMapping(value = "/order/requestDetails/list/{maxId}", method = {RequestMethod.GET, RequestMethod.POST})
    public String listInventoryDetails(
            @ModelAttribute("requestDetailsForm") Filter filter,
            //@PathVariable("countId") Long countId,
            @PathVariable("maxId") int maxId,
            HttpServletRequest request, Model uiModel) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        //Get dữ liệu
        SalesTrans salesTrans = dataService.getRowById(maxId, SalesTrans.class);
        if (salesTrans != null) {
        	if(salesTrans.getTransType() == 3){
        		uiModel.addAttribute("isTraHang", true);
        	}
            User user = new User();
            int salemanStockId = 0;
            if (salesTrans.getTransType() == 1) {
                if (salesTrans.getFromStocks() != null && salesTrans.getFromStocks().getChannels() != null) {
                    salesTrans.setChannel(salesTrans.getFromStocks().getChannels());
                   
                }
                if (salesTrans.getToStocks().getId() != null) {
                    salemanStockId = salesTrans.getToStocks().getId();
                    if (salesTrans.getToStocks().getSalemanUsers() != null) {
                        user = salesTrans.getToStocks().getSalemanUsers();
                    }
                }

            }

            if (salesTrans.getTransType() == 3) {
                if (salesTrans.getToStocks() != null && salesTrans.getToStocks().getChannels() != null) {
                    salesTrans.setChannel(salesTrans.getToStocks().getChannels());
                    
                }
                if (salesTrans.getFromStocks().getId() != null) {
                    salemanStockId = salesTrans.getFromStocks().getId();
                    if (salesTrans.getFromStocks().getSalemanUsers() != null) {
                        user = salesTrans.getFromStocks().getSalemanUsers();
                    }
                }
            }
            if(salesTrans.getChannel() == null){
            	String hqlC = serviceMCP.getStringHQLNVBH(salesTrans.getCreatedUser(), LoginContext.getLogin(request).getCompanyId());
            			
            	List<HashMap> lHashMaps = dataService.executeSelectHQL(HashMap.class, hqlC, true, 1, 1);
            	Channel channel = new Channel();
            	if(!lHashMaps.isEmpty()){
            		channel.setFullCode((String)lHashMaps.get(0).get("fullCode"));
            		channel.setName((String)lHashMaps.get(0).get("name"));
            		channel.setTel((String)lHashMaps.get(0).get("tel"));
            		channel.setAddress((String)lHashMaps.get(0).get("address"));
            		salesTrans.setChannel(channel);
            	}
            }
            if (salemanStockId == 0) {
                user = dataService.getRowById(salesTrans.getCreatedUser(), User.class);
                List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("salemanUsers.id", user.getId()));
                if (!salesStocks.isEmpty()) {
                    salemanStockId = salesStocks.get(0).getId();
                }
            }
            if (user != null) {
                user.setName(user.getLastName() + " " + user.getFirstName());
                user.setUserRoles("Nhân viên bán hàng");
            }
            salesTrans.setEmployee(user);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<SalesTransDetails> lists = dataService.getListOption(SalesTransDetails.class, new ParameterList("salesTranss.id", salesTrans.getId()));
            if(salesTrans.getTransType() != 3){
            	
            	for (SalesTransDetails sTransDetails : lists) {          				
                    sTransDetails.setTotalPrice((long) sTransDetails.getPrice() * sTransDetails.getQuantity());
//                    String hqlSaleDetails = "SELECT S.id as id, S.price as price, S.quantity as quantity"
//                    		+ " FROM SalesTransDetails as S where deletedUser = 0"
//                    		+ " and salesTranss.deletedUser = 0 and salesTranss.transType = 1 "
//                    		+ " and salesTranss.companys.id = "+userInf.getCompanyId()
//                    		+ " and salesTranss.createdUser = "+ salesTrans.getCreatedUser()
//                    	
//                    		+ " and salesTranss.createdAt < '"+simpleDateFormat.format(salesTrans.getCreatedAt()) +"'"
//                    		+ " and goodss.id = "+ sTransDetails.getGoodss().getId()
//                    		+ " and goodsUnits.id = "+ sTransDetails.getGoodsUnits().getId();
                    String hqlX = serviceMCP.getStringForTheoDoiNhanHang(userInf.getCompanyId(), salesTrans.getCreatedUser(),sTransDetails.getGoodss().getId(),
                    		sTransDetails.getGoodsUnits().getId(), simpleDateFormat.format(salesTrans.getCreatedAt()));           
            		List<HashMap> listSales = dataService.executeSelectHQL(HashMap.class, hqlX, true, 1, 1);
                    String fromDate = null;
                    if(!listSales.isEmpty()){
                    	fromDate = simpleDateFormat.format((Date)listSales.get(0).get("date"));
                    //	hqlSaleDetails += " and salesTranss.createdAt >= '"+ simpleDateFormat.format((Date)listSales.get(0).get("date")) +"'";
                    }
                    String hqlSaleDetails = serviceMCP.getStringForSaleTransDetails(userInf.getCompanyId(), salesTrans.getCreatedUser(), simpleDateFormat.format(salesTrans.getCreatedAt()),
                    		sTransDetails.getGoodss().getId(), sTransDetails.getGoodsUnits().getId(), fromDate);
                    
                    List<HashMap> listSalesDetails = dataService.executeSelectHQL(HashMap.class, hqlSaleDetails, true, 0, 0);
                    if(listSalesDetails.isEmpty()){
                    	 sTransDetails.setSoLuongTon(0);
                    	 sTransDetails.setThanhTienTon((long)0);
                    }else{
                    	int  soluongTon = 0;
                    	long thanhTienTon = 0;
                    	for(HashMap hm : listSalesDetails){
                    		soluongTon += (Integer) hm.get("quantity");
                    		thanhTienTon += (Integer)hm.get("quantity")* (Integer) hm.get("price");
                    		
                    	}
                    	sTransDetails.setSoLuongTon(soluongTon);
                		sTransDetails.setThanhTienTon((long) thanhTienTon);
                    }
//                    ParameterList parameterList = new ParameterList();
//                    parameterList.add("goodss.id", sTransDetails.getGoodss().getId());
//                    parameterList.add("goodsUnits.id", sTransDetails.getGoodsUnits().getId());
//                    parameterList.add("stocks.id", salemanStockId);
//                    List<SalesStockGoods> salesStockGoods = dataService.getListOption(SalesStockGoods.class, parameterList);
//                    if (!salesStockGoods.isEmpty()) {
//                        sTransDetails.setSoLuongTon(salesStockGoods.get(0).getQuantity());
//                        sTransDetails.setThanhTienTon(salesStockGoods.get(0).getQuantity() * salesStockGoods.get(0).getGoodss().getPrice());
//                    }else{
//                    	 sTransDetails.setSoLuongTon(0);
//                    	 sTransDetails.setThanhTienTon(0);
//                    }

                }
            }else{
            	for (SalesTransDetails sTransDetails : lists) {          				
                    sTransDetails.setTotalPrice((long) sTransDetails.getPrice() * sTransDetails.getQuantity());

                }
            }
            
            salesTrans.setSalesTransDetails(lists);
        }

        uiModel.addAttribute("salesTransList", salesTrans);
        uiModel.addAttribute("inventoryDetailsList", salesTrans.getSalesTransDetails());

        return "requestDetailsList";
    }

    @RequestMapping(value = "/setOrder/create", method = RequestMethod.GET)
    public String setOrderDiaPhone(
            @ModelAttribute(value = "setOrderForm") SalesOrder salesOrder,
            Model uiModel, HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        //Lấy ListPOS
        try {
			initData(request, userInf.getCompanyId(), userInf.getId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

       // uiModel.addAttribute("listPOS", newList);
        return "setGoodsOrder";
    }

    @RequestMapping(value = "/setOrder/create", method = RequestMethod.POST)
    public String createOrder(@ModelAttribute(value = "setOrderForm") SalesOrder salesOrder,
            Model uiModel, HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        
        //Lấy userLogin
        MsalesLoginUserInf user = LoginContext.getLogin(request);
        //ReceiveGoods receiveGoods = new ReceiveGoods();
        //Lấy dữ liệu trả về
        request.setAttribute("orderDetailsList", salesOrder.getOrderDetailsList());
        request.setAttribute("frm_startDate", salesOrder.getSalesTransDate());
        //Lấy POS trả về
        try {
            initData(request, user.getCompanyId(), user.getId());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Trả về danh sách Goods
        POS pos = dataService.getRowById(salesOrder.getPosId(), POS.class);
        if (pos == null) {
            request.setAttribute("errorMessage", MsalesValidator.ERORR_MESSAGE);
            request.setAttribute("errorOrderPOS", MsalesValidator.POS_ID_NOT_EXIST);
            return "setGoodsOrder";
        }
        if (pos.getCreatedUser() == null || pos.getCreatedUser() <= 0) {
            request.setAttribute("errorMessage", MsalesValidator.ERORR_MESSAGE);
            request.setAttribute("errorCreatedUser", MsalesValidator.POS_NOT_EMPLOYEE_CARE);
            return "setGoodsOrder";
        }
        //Trả về nhân viên chăm sóc cho pos
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	String hqlUser = "Select MCP.implementEmployees as user"
    			+ " from MCPDetails as MCP where MCP.deletedUser = 0 and MCP.poss.id = "+pos.getId();
    	String hqlX = hqlUser + " and MCP.mcps.beginDate >= '" + simpleDateFormat.format(new Date()) + " 00:00:00'"
    			+ " and MCP.mcps.beginDate < '" + simpleDateFormat.format(new Date()) + " 23:59:59'"
    			+ " and MCP.mcps.deletedUser = 0";
    	hqlUser += " order by MCP.createdAt desc";	
    	List<HashMap> listHashMaps = dataService.executeSelectHQL(HashMap.class, hqlX, true, 1, 1);
    	if(listHashMaps.isEmpty()){
    		listHashMaps = dataService.executeSelectHQL(HashMap.class, hqlUser, true, 1, 1);
    	}
    	if(!listHashMaps.isEmpty()){
    		User user2 = (User) listHashMaps.get(0).get("user");
            pos.setNameNVCS(user2.getLastName() + " " + user2.getFirstName());
            pos.setCreatedUser(user2.getId());
    	}
        
        
        String hql = "Select U from UserGoodsCategory as U where deletedUser = 0 and users.id = " + pos.getCreatedUser();
        hql += " and goodsCategorys.companys.id = " + user.getCompanyId()
                + " order by goodsCategorys.id desc";

        List<UserGoodsCategory> list = new ArrayList<UserGoodsCategory>();
        try {
            list = dataService.executeSelectHQL(UserGoodsCategory.class, hql, false, 0, 0);
        } catch (Exception ex) {

        }

        List<Goods> goodsList = new ArrayList<Goods>();
        if (list.size() <= 0) {
            String hqlGoods = "Select G from Goods as G where deletedUser = 0 and statuss.id = 15 and  goodsCategorys.companys.id = "
                    + user.getCompanyId() + " order by name asc";
            try {
                goodsList = dataService.executeSelectHQL(Goods.class, hqlGoods, false, 0, 0);
            } catch (Exception ex) {

            }
        }
        /*ParameterList parameterList = new ParameterList("users.id", pos.getCreatedUser());
         parameterList.setOrder("goodsCategorys.id", "DESC");
         List<UserGoodsCategory> list = dataService.getListOption(UserGoodsCategory.class, parameterList);

         List<Goods> goodsList = new ArrayList<Goods>();
         if (list.isEmpty()) {
         ParameterList parameteList3 = new ParameterList();
         parameteList3.setOrder("name");
         goodsList = dataService.getListOption(Goods.class, parameteList3);
         }*/

        for (UserGoodsCategory uGoodsCategory : list) {
            ParameterList parameterList2 = new ParameterList("goodsCategorys.id", uGoodsCategory.getGoodsCategorys().getId());
            parameterList2.add("statuss.id", 15);
            parameterList2.setOrder("name");
            List<Goods> goods = dataService.getListOption(Goods.class, parameterList2);

            if (goods.size() > 0) {
                goodsList.addAll(goods);
            }
            // uGoodsCategory.setGoodsCategory(uGoodsCategory.getGoodsCategorys());
        }

        uiModel.addAttribute("goodsfList", goodsList);

        //Lấy goods
        List<Goods> goodss = new ArrayList<Goods>();

        for (SalesOrderDetails salesOrderDetails : salesOrder.getOrderDetailsList()) {
            Goods goods = new Goods();
            //Set GoodsId
            goods.setId(salesOrderDetails.getGoodsId());
            //Set goodsUnitId
         //   goods.setGoodsUnitId(salesOrderDetails.getGoodsUnitId());
            //goods.setPrice(salesOrderDetails.getPrice2());
      //      goods.setQuantity(salesOrderDetails.getQuantity());
            //Cần phải check số lượng và giá tiền ở đây
        //    Goods goodsX = dataService.getRowById(salesOrderDetails.getGoodsId(), Goods.class);
            GoodsUnit goodsUnit = dataService.getRowById(salesOrderDetails.getGoodsUnitId(), GoodsUnit.class);
            goods.setPrice(goodsUnit.getGoodss().getPrice());
            //Đưa vào thông tin là goodsId, goodsUnitId trả ra số lượng cần tìm
           /* int quantity = 0;
            quantity += goodsUnit.getQuantity();
            for(int i = 0; i < 10; i++){
            	boolean bool = false;
            	if(goodsUnit != null && goodsUnit.getChildUnitIds() != null && goodsUnit.getChildUnitIds().getId() == goodsUnit.getUnits().getId()){
            		//quantity += goodsUnit.getQuantity();
            		goods.setGoodsUnitId(goodsUnit.getId());
            		break;
            	}else if(goodsUnit != null && goodsUnit.getChildUnitIds() != null){
            		ParameterList pList = new ParameterList(1,1);
                	pList.add("goodss.id", goodsUnit.getGoodss().getId());
                	pList.add("units.id", goodsUnit.getChildUnitIds().getId());
                	List<GoodsUnit> listGoodsUnit = dataService.getListOption(GoodsUnit.class, pList);
                	if(!listGoodsUnit.isEmpty()){
                		goodsUnit = listGoodsUnit.get(0);
                		quantity = quantity*goodsUnit.getQuantity();
                		
                	}else{
                		bool = true;
                	}
            	}else{
            		bool = true;
            	}
            	if(bool){
            		break;
            	}
            }*/
            if(goodsUnit != null){
            	goods.setQuantity(goodsUnit.getQuantity()*salesOrderDetails.getQuantity());
                salesOrderDetails.setPrice2((long)(goodsUnit.getQuantity()*goodsUnit.getGoodss().getPrice()));
                salesOrderDetails.setTotalPrice((long)((long) salesOrderDetails.getQuantity()*(long)(goodsUnit.getQuantity()*goodsUnit.getGoodss().getPrice())));
                if(goodsUnit.getChildUnitIds() == null || goodsUnit.getUnits().getId() == goodsUnit.getChildUnitIds().getId() || goodsUnit.getQuantity() == 1 ){
                	goods.setGoodsUnitId(goodsUnit.getId());
                }
                goodss.add(goods);
            }
            
        }
        //Xử lí goods và goodsUnit
        ArrayList<Integer> arrayGoodsId = new ArrayList<Integer>();
        for (int i = 0; i < goodss.size(); i++) {
            boolean bool = true;
            for (int j = 0; j < i; j++) {
                if (goodss.get(i).getId() == goodss.get(j).getId()) {
                    bool = false;
                    break;
                }
            }
            if (bool) {
                arrayGoodsId.add(goodss.get(i).getId());
            }
        }
        //Xử lí Goods
        List<Goods> gList = new ArrayList<Goods>();
        for (int i = 0; i < arrayGoodsId.size(); i++) {
            Goods goods = new Goods();
            goods.setId(arrayGoodsId.get(i));
//            ParameterList parameterList2 = new ParameterList("goodss.id", arrayGoodsId.get(i));
//            parameterList2.setOrder("quantity", "asc");
//            List<GoodsUnit> goodsUnits = dataService.getListOption(GoodsUnit.class, parameterList2);
//            if (goodsUnits.isEmpty()) {
//                request.setAttribute("errorMessage", MsalesValidator.ERORR_MESSAGE);
//                return "setGoodsOrder";
//            }
            int quantity = 0;
            for (Goods sp : goodss) {
                if (sp.getId() == arrayGoodsId.get(i)) {
                	quantity += sp.getQuantity();
                	goods.setPrice(sp.getPrice());
                	if(sp.getGoodsUnitId() != null){
                		goods.setGoodsUnitId(sp.getGoodsUnitId());
                	}

                }
            	
            	
            }
            if (goods.getGoodsUnitId() == null) {
              ParameterList parameterList2 = new ParameterList("goodss.id", arrayGoodsId.get(i));
              parameterList2.setOrder("quantity", "asc");
              List<GoodsUnit> goodsUnits = dataService.getListOption(GoodsUnit.class, parameterList2);
              if (goodsUnits.isEmpty()) {
                  request.setAttribute("errorMessage", MsalesValidator.ERORR_MESSAGE);
                  return "setGoodsOrder";
              }
              for(GoodsUnit goodsUnit : goodsUnits){
            	  if(goodsUnit.getChildUnitIds() == null || goodsUnit.getUnits().getId() == goodsUnit.getChildUnitIds().getId() || goodsUnit.getQuantity() == 1 ){
                  	goods.setGoodsUnitId(goodsUnit.getId());
                  }else{
                	  goods.setGoodsUnitId(goodsUnits.get(0).getId());
                  }
              }
               
             //   goods.setPrice(goodsUnits.get(0).getPrice());
            }
            
            goods.setQuantity(quantity);
            gList.add(goods);
        }

        //Tạo dữ liệu:
        //Check salesTranDate
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date date = cal.getTime();
        if (date.getTime() > salesOrder.getSalesTransDate().getTime()) {
            request.setAttribute("errorMessage", MsalesValidator.ERORR_MESSAGE);
            request.setAttribute("errorDateSalesTrans", MsalesValidator.SALES_TRANS_DATE_IS_PAST);
            return "setGoodsOrder";
        }
        //
        //Dữ liệu để create
        SalesOrder salesOrder2 = new SalesOrder();
        //set thong tin công ty
        salesOrder2.setCompanyId(user.getCompanyId());
        //Xét địa điểm POS DBH
        salesOrder2.setPosId(salesOrder.getPosId());
        //Lấy danh sách kho hàng của điểm bán hàng
        List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("poss.id", salesOrder.getPosId()));

        if (salesStocks.isEmpty()) {
            request.setAttribute("errorMessage", MsalesValidator.ERORR_MESSAGE);
            request.setAttribute("errorOrderPOS", MsalesValidator.APP_POS_NO_HAVE_STOCK);
            return "setGoodsOrder";
        }
        //set id kho hàng
        salesOrder2.setStockId(salesStocks.get(0).getId());
        //set ngày giao dịch
        salesOrder2.setSalesTransDate(salesOrder.getSalesTransDate());
        //sét trạng thái giao dịch
        salesOrder2.setStatusId(13);
        //Xét Note với đặt hàng qua điện thoại
        salesOrder2.setNote("MOBILE");
        //người tạo
        salesOrder2.setCreatedUser(user.getId());
        int channelId = 0;
    	String hqlChannel = "Select URC.channels.id as channelId from UserRoleChannel as URC"
    			+ " WHERE URC.deletedUser = 0"
    			+ " AND URC.users.id = " + pos.getCreatedUser()
    			+ " ORDER BY URC.channels.channelTypes.id desc";
    	List<HashMap> listChannelId = dataService.executeSelectHQL(HashMap.class, hqlChannel, true, 1, 1);
    	if(!listChannelId.isEmpty()){
    		channelId = (Integer) listChannelId.get(0).get("channelId");
    	}else{
    		channelId = pos.getChannels().getId();
    	}
    	Channel channel = dataService.getRowById(channelId, Channel.class);
    	
		String string = "";
		if (channel != null) {
			//Chi Nhanh
			string = " channels.id = "+channelId;
			if(channel.getParents() != null){
				//Dai Ly
				string += " OR channels.id = "+channel.getParents().getId();
				if(channel.getParents().getParents() != null){
					//Nha PP
					string += " OR channels.id = "+channel.getParents().getParents().getId();
					if(channel.getParents().getParents().getParents() != null){
						//Giam sat
						string += " OR channels.id = "+channel.getParents().getParents().getParents().getId();
						if(channel.getParents().getParents().getParents().getParents() != null){
							//khu vuc
							string += " OR channels.id = "+channel.getParents().getParents().getParents().getParents().getId();
							if(channel.getParents().getParents().getParents().getParents().getParents() != null){
								//Mien
								string += " OR channels.id = "+channel.getParents().getParents().getParents().getParents().getParents().getId();
							}
						}
					}
				}
			}
		}

        int ret = 0;
        try {
            //save SalesTrans
            ret = dataService.insertRow(salesOrder2);
        } catch (Exception e) {
            request.setAttribute("errorMessage", MsalesValidator.ERORR_MESSAGE);
            return "setGoodsOrder";
        }
        if (ret > 0) {
            List<SalesOrderDetails> salesOrderDetails = new ArrayList<SalesOrderDetails>();
            for (Goods sp : gList) {
                SalesOrderDetails salesTransDetails = new SalesOrderDetails();
                salesTransDetails.setGoodsId(sp.getId());
                salesTransDetails.setGoodsUnitId(sp.getGoodsUnitId());
                salesTransDetails.setOrderId(ret);
                salesTransDetails.setQuantity(sp.getQuantity());
               
                salesTransDetails.setSalesTransDate(salesOrder.getSalesTransDate());
                salesTransDetails.setCreatedUser(user.getId());
                Goods goods2 = dataService.getRowById(sp.getId(), Goods.class);
                if(!goods2.getIsFocus()){
                	//Xet tiep xem Goods co phai la san pham trong tam cua kenh khong
                	if(channelId != 0){
                		String hqlIsFocus = "Select GCF.id as id from GoodsChannelFocus as GCF "
                				+ " WHERE deletedUser = 0 "
                				+ " AND channels.deletedUser = 0"
                				+ " AND goodss.id = " + sp.getId()
                				+ " AND ("+string+")";
                		List<HashMap> listIsFocus = dataService.executeSelectHQL(HashMap.class, hqlIsFocus, true, 1, 1);
                		if(!listIsFocus.isEmpty()){
                			goods2.setIsFocus(true);
                		}else{
                			goods2.setIsFocus(false);
                		}
                	}
                	
                }
                salesTransDetails.setPrice(goods2.getPrice());
                salesTransDetails.setIsFocus(goods2.getIsFocus());
                salesOrderDetails.add(salesTransDetails);
            }
            try {
                dataService.insertArray(salesOrderDetails);
            } catch (Exception ex) {
                // TODO: handle exception
                SalesOrder salesTrans2 = dataService.getRowById(ret, SalesOrder.class);
                salesTrans2.setDeletedUser(user.getId());
                salesTrans2.setDeletedAt(new Date());
                dataService.updateRow(salesTrans2);
                request.setAttribute("errorMessage", MsalesValidator.ERORR_MESSAGE);
                return "setGoodsOrder";
            }
            request.setAttribute("infoMessage", MsalesValidator.SUCCESS_MESSAGE);
            return "setGoodsOrder";
        } else {
            request.setAttribute("errorMessage", MsalesValidator.ERORR_MESSAGE);
            return "setGoodsOrder";
        }

    }

    private void initData(HttpServletRequest request, int companyId, int userId) throws IOException {
    	//get List channelId Of User_login.
    	String hqlC = "SELECT URC.channels.id as channelId, URC.userRoles.code as userCode FROM UserRoleChannel as URC "
    			+ " WHERE URC.deletedUser = 0 AND URC.channels.deletedUser = 0 "
    			+ " AND URC.channels.channelTypes.deletedUser = 0 "
    			+ " AND URC.channels.companys.id = " + companyId
    			+ " AND URC.users.id = "+ userId
    			+ " GROUP BY URC.channels.id "
    			+ " ORDER BY URC.channels.channelTypes.id";
    	List<HashMap> listChannelId = dataService.executeSelectHQL(HashMap.class, hqlC, true, 0, 0);
    	int channelId = 0;
    	String userCode = "";
    	if(!listChannelId.isEmpty()){
    		channelId = (Integer) listChannelId.get(0).get("channelId");
    		userCode = (String) listChannelId.get(0).get("userCode");
    	}
    	
    	//get list user id of channelId 
    	
        
    	String hqlU = "SELECT URC.users.id as userId FROM UserRoleChannel as URC "
    			+ " WHERE URC.deletedUser = 0 AND URC.channels.deletedUser = 0 "
    			+ " AND URC.channels.channelTypes.deletedUser = 0 "
    			+ " AND URC.userRoles.id = 6 "
    			+ " AND URC.channels.companys.id = " + companyId
    			+ " AND URC.users.id != "+ userId;
    	
    	if(userCode.equals("USER_ROLE_ADMIN_COMPANY")){
    		
    	}else{
    		String hqlChannelType = "SELECT CT.id as id FROM ChannelType as CT where deletedUser = 0 and companys.id = "+ companyId;
    		List<HashMap> listChannelTypeId = dataService.executeSelectHQL(HashMap.class, hqlChannelType, true, 0, 0);
    		List<String> arrayString = new ArrayList<>();
    		String stringChannel = "URC.channels";
    		String stringParents = "parents";
    		String string = "";
    		for(int i = 0; i < listChannelTypeId.size(); i++){
    			if(i==0){
    				string += " OR "+stringChannel+".id ="+channelId;
    			}else{
    				string += " OR "+stringChannel+"."+stringParents+".id ="+channelId;
    				stringParents+=".parents";
    			}
    			arrayString.add(string);
    		}
    		Channel channel = dataService.getRowById(channelId, Channel.class);
    		String codeChannelType = "";
    		if(channel != null){
    			codeChannelType = channel.getChannelTypes().getCode();
    		}
    		if(arrayString.size() > 0){
    			int i = arrayString.size();
    			if(codeChannelType.equals("CHT_REGION")){
        			string = arrayString.get(i-1);
        		}else if(codeChannelType.equals("CHT_AREA")){
        			string = arrayString.get(i-2);
        		}else if(codeChannelType.equals("CHT_MONITORING")){
        			string = arrayString.get(i-3);
        		}else if(codeChannelType.equals("CHT_PROVIDER")){
        			string = arrayString.get(i-4);
        		}else if(codeChannelType.equals("CHT_AGENCY")){
        			string = arrayString.get(i-5);
        		}else if(codeChannelType.equals("CHT_BRANCH")){
        			string = arrayString.get(i-6);
        		}
    		}
    		if(string.length() > 3){
    			hqlU += " AND ( "+string.substring(3) + " )";
    		}
    	}
    	
    	hqlU += " GROUP BY URC.users.id";
    	
    	List<HashMap> listUserId = dataService.executeSelectHQL(HashMap.class, hqlU, true, 0, 0);
    	String hqlChannel = "";
        for (HashMap userRoleChannel : listUserId) {
            hqlChannel += userRoleChannel.get("userId")+ ",";
        }
        hqlChannel += "''";
    	
    	//Select posId from channel
    	
    	String hql = "Select MCPDetails.poss.id as id, MCPDetails.poss.name as name from MCPDetails as MCPDetails "
    			+ " where deletedUser = 0 and poss.deletedUser = 0 and poss.statuss.id = 5"
    			+ " and mcps.deletedUser = 0 "
        		+ " and poss.channels.companys.id = " + companyId
        		+" and mcps.implementEmployees.id in (" + hqlChannel + " ) "
        		+ " group by poss.id"
 		        + " order by poss.name asc";
        
        
        
    //	String hql = serviceMCP.getListPosForCreatedOrder(companyId, hqlChannel);
//    	Date date = new Date();
// 		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
// 		String fromDate = simpleDateFormat.format(date);
// 		String toDate = simpleDateFormat.format(date) + " 23:59:59";
// 		String hqlX = hql + " and mcps.beginDate >= '" + fromDate + "'"
// 				+ " and mcps.beginDate <= '" + toDate + "'"
// 				+ " and mcps.deletedUser = 0"
// 				+ " group by poss.id"
// 		        + " order by poss.name asc";	
 		
 		List<HashMap> list = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
        
        List<OptionItem> newList = new ArrayList<OptionItem>();
        for(HashMap hashMap : list){
        	OptionItem optionItem = new OptionItem();
        	optionItem.setId((Integer) hashMap.get("id"));
        	optionItem.setName((String) hashMap.get("name"));
        	newList.add(optionItem);
        }

     //   uiModel.addAttribute("listPOS", newList);

        request.setAttribute("listPOS", newList);
    }

    @InitBinder
    public void dataBinding(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    
    private ArrayList<String> unaccent(String text) {
        //  String newText = text.toLowerCase();
        ArrayList<String> searchText = new ArrayList<String>();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            switch (c) {

                case 'a': {
                    searchText.add(text.replace("a", "ă"));
                    searchText.add(text.substring(0, i) + "ă" + text.substring(i + 1));
                    searchText.add(text.replace("a", "â"));
                    searchText.add(text.substring(0, i) + "â" + text.substring(i + 1));
                    break;
                }
                case 'A': {
                    searchText.add(text.replace("A", "ă"));
                    searchText.add(text.substring(0, i) + "ă" + text.substring(i + 1));
                    searchText.add(text.replace("A", "â"));
                    searchText.add(text.substring(0, i) + "â" + text.substring(i + 1));
                    break;
                }

                case 'd': {
                    searchText.add(text.replace("d", "đ"));
                    searchText.add(text.substring(0, i) + "đ" + text.substring(i + 1));
                    break;
                }
                case 'D': {
                    searchText.add(text.replace("D", "đ"));
                    searchText.add(text.substring(0, i) + "đ" + text.substring(i + 1));
                    break;
                }
                case 'e': {
                    searchText.add(text.replace("e", "ê"));
                    searchText.add(text.substring(0, i) + "ê" + text.substring(i + 1));
                    break;
                }

                case 'E': {
                    searchText.add(text.replace("E", "ê"));
                    searchText.add(text.substring(0, i) + "ê" + text.substring(i + 1));
                    break;
                }

                case 'o': {
                    searchText.add(text.replace("o", "ô"));
                    searchText.add(text.replace("o", "ơ"));
                    searchText.add(text.substring(0, i) + "ô" + text.substring(i + 1));
                    searchText.add(text.substring(0, i) + "ơ" + text.substring(i + 1));
                    break;
                }

                case 'O': {
                    searchText.add(text.replace("O", "ô"));
                    searchText.add(text.replace("O", "ơ"));
                    searchText.add(text.substring(0, i) + "ô" + text.substring(i + 1));
                    searchText.add(text.substring(0, i) + "ơ" + text.substring(i + 1));
                    break;
                }
                case 'u': {
                    searchText.add(text.replace("u", "ư"));
                    searchText.add(text.substring(0, i) + "ư" + text.substring(i + 1));
                    break;
                }
                case 'U': {
                    searchText.add(text.replace("U", "ư"));
                    searchText.add(text.substring(0, i) + "ư" + text.substring(i + 1));
                    break;
                }

            }

        }

        return searchText;
    }

}
