/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.user.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.MCPDetails;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.Session;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.UserRoute;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.SearchRoute;
import vn.itt.msales.entity.searchObject.SearchSupRoute;

/**
 *
 * @author vtm_2
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.USER_ROUTE.NAME)
public class MsalesUserRouteController extends CsbController {


    /**
     * search route
     *
     */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = MsalesConstants.MODULE.USER_ROUTE.ACTION_SEARCH_USER_ROUTE, method = RequestMethod.POST)
    public @ResponseBody
    String searchUserRoute(HttpServletRequest request) {
        ParameterList parameterList = new ParameterList();

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            LinkedHashMap<String, Object> searchObject;
            try {
                //parse jsonString to a search Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        LinkedHashMap.class);
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //searchObject from json not null
            if (searchObject != null) {
                if (searchObject.get("userId") != null) {
                    try {
                        int userId = Integer.parseInt(searchObject.get("userId").toString());
                        parameterList.add("user.id", userId);
                    } catch (Exception ex) {
                        //error parse goodsCategoryId
                    }
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
                if (searchObject.get("fromDate") != null) {
                    String fromDate = searchObject.get("fromDate").toString();
                    if (fromDate != null) {
                        try {
                            Date date1 = simpleDateFormat.parse(fromDate);
                            parameterList.add("routeAt", date1, ">=");
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                if (searchObject.get("toDate") != null) {
                    String toDate = searchObject.get("toDate").toString();
                    if (toDate != null) {
                        try {
                            Date date2 = simpleDateFormat.parse(toDate);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date2);
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                            parameterList.add("routeAt", calendar.getTime(), "<");
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                MsalesResults<UserRoute> list = new MsalesResults<UserRoute>();
                if(searchObject.get("userId") != null){
                	list = dataService.getListOption(UserRoute.class, parameterList,true);
                	User u = new User();
                    if(!list.getContentList().isEmpty()){
                    	for(UserRoute user : list.getContentList()){
//                    		u = user.getUser();
//                        	u.setName(u.getLastName() + " " + u.getFirstName());
//                        	user.setUser(u);
                        }
                    }
                    
                }else{
                	list = dataService.getListOption(UserRoute.class, new ParameterList("user.id", 0),true);
                }
                String[] strings = {"firstName", "lastName"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), strings);
            } //jsonString null

        }

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                .create(MsalesStatus.JSON_CONTENTS_EMPTY));

    }
    
 
    
    @RequestMapping(value = MsalesConstants.MODULE.USER_ROUTE.ACTION_SEARCH_USER_ROUTE_APP, method = RequestMethod.POST)
    public @ResponseBody String searchSupUserRoute(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchSupRoute searchSupRoute;
            try {
                searchSupRoute = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchSupRoute.class);
            } catch (Exception ex) {
                return MsalesJsonUtils.validateFormat(ex);
            }
            LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();

            if (searchSupRoute.getUserId() == null) {
                hashErrors.put("userId", MsalesValidator.NOT_NULL);
            }
            if (searchSupRoute.getFromDate() == null) {
                hashErrors.put("fromDate", MsalesValidator.NOT_NULL);
            }
            if (searchSupRoute.getToDate() == null) {
                hashErrors.put("toDate", MsalesValidator.NOT_NULL);
            }

            if (!hashErrors.isEmpty()) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
            }

            ArrayList<Integer> userIds = new ArrayList<>();

            if (searchSupRoute.getUserId() == 0) {
            	//Danh sách nhân viên có
                if (searchSupRoute.getEmployerList().size() > 1) {
                    //lay tat ca
                    //lay channel ma user dang lam giam sat.
                	ArrayList<LinkedHashMap<String, Object>> employerList = searchSupRoute.getEmployerList();
                	for(int i = 1; i < employerList.size(); i++){
  					  int id = Integer.parseInt(employerList.get(i).get("id").toString());
  					  if(id != 0){
  						 userIds.add(id);
  					  }else{
  						  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
  					  }
  				  }
                    
                } else {
                    //lay tat ca nhan vien trong channel
                	//Không có nhân viên trong kênh
                	return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                }
            } else {
                //lay theo nhan vien
                userIds.add(searchSupRoute.getUserId());
            }

            SimpleDateFormat sdfHQL = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(searchSupRoute.getToDate());
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            List<LinkedHashMap> items = new ArrayList<>();

            for (int id : userIds) {
            	User user = new User();
            	if(id > 0){
            		user = dataService.getRowById(id, User.class);
            	}
            	
                LinkedHashMap item = new LinkedHashMap();
                if(user != null){
                	 item.put("userId", id);
                     item.put("userName", user.getLastName() + " " + user.getFirstName());
                }

                String hql = "SELECT DISTINCT MCPDetails.poss as pos,MCPDetails.id as mcpDetailsId,"
                        + " CASE WHEN CustomerCareInformation.id IS NULL THEN FALSE ELSE TRUE END AS isVisited,"
                        + " CASE WHEN CustomerCareInformation.startCustomerCareAt IS NULL THEN NULL ELSE DATE_FORMAT(CustomerCareInformation.startCustomerCareAt,'%d/%m/%Y %H:%i:%S') END AS timeCared"
                        + " FROM CustomerCareInformation as CustomerCareInformation"
                        + " RIGHT JOIN CustomerCareInformation.mcpDetailss as MCPDetails"
                        + " JOIN MCPDetails.mcps as MCP"
                        + " WHERE MCP.implementEmployees.id = '" + id + "'"
                        + " AND MCP.beginDate >= '" + sdfHQL.format(searchSupRoute.getFromDate()) + "'"
                        + " AND MCP.beginDate < '" + sdfHQL.format(calendar.getTime()) + "'"
                        + " AND MCP.deletedUser = 0"
                        + " AND MCPDetails.deletedUser = 0"
                        + " GROUP BY MCPDetails.id";
                List posLists = new ArrayList();
                List list = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
                LinkedHashMap pos;
                for (Iterator iterator = list.iterator();
                        iterator.hasNext();
                        posLists.add(pos)) {
                    HashMap mcpDetails = (HashMap) iterator.next();
                    //POS APP
                    pos = new LinkedHashMap();
                    pos.put("id", ((POS) mcpDetails.get("pos")).getId());
                    pos.put("mcpDetailsId", mcpDetails.get("mcpDetailsId"));
                    pos.put("soDT", ((POS) mcpDetails.get("pos")).getTel());
                    //pos.put("posCode", ((POS) mcpDetails.get("pos")).getPosCode());
                    pos.put("name", ((POS) mcpDetails.get("pos")).getName());
                    pos.put("address", ((POS) mcpDetails.get("pos")).getAddress());
                    pos.put("lat", ((POS) mcpDetails.get("pos")).getLat());
                    pos.put("lng", ((POS) mcpDetails.get("pos")).getLng());
                    pos.put("lng", ((POS) mcpDetails.get("pos")).getLng());
                    pos.put("isVisited", mcpDetails.get("isVisited"));
                    pos.put("timeCared", mcpDetails.get("timeCared"));

                    //get timesale - tranType = 2
                    String hqlDelivery = "SELECT SalesTrans"
                            + " FROM SalesTrans AS SalesTrans"
                            + " JOIN SalesTrans.toStocks AS ToStock"
                            + " JOIN SalesTrans.fromStocks AS FromStock"
                            + " WHERE ToStock.poss.id = '" + ((POS) mcpDetails.get("pos")).getId() + "'"
                            + " AND FromStock.salemanUsers.id = '" + id + "'"
                            + " AND SalesTrans.transType = 2"
                            + " AND SalesTrans.salesTransDate >= '" + sdfHQL.format(searchSupRoute.getFromDate()) + "'"
                            + " AND SalesTrans.salesTransDate < '" + sdfHQL.format(calendar.getTime()) + "'"
                            + " AND SalesTrans.deletedUser = 0"
                            + " ORDER BY SalesTrans.salesTransDate DESC";
                    List<SalesTrans> salesTransList = dataService.executeSelectHQL(SalesTrans.class, hqlDelivery, false, 0, 0);

                    if (!salesTransList.isEmpty()) //get timedelevery 
                    {
                        pos.put("timeSale", salesTransList.get(0).getSalesTransDate());
                    }
                    else pos.put("timeSale", null);
                    pos.put("timeDelivery", "FIX ME");
                }

                String hqlRoute = "SELECT UserRoute.lat AS lat,UserRoute.lng AS lng,"
                        + " DATE_FORMAT(UserRoute.routeAt,'%d/%m/%Y %H:%i:%S') as routeAt, UserRoute.note as note"
                        + " FROM UserRoute as UserRoute"
                        + " WHERE UserRoute.user.id = '" + id + "'"
                        + " AND UserRoute.createdAt >= '" + sdfHQL.format(searchSupRoute.getFromDate()) + "'"
                        + " AND UserRoute.createdAt < '" + sdfHQL.format(calendar.getTime()) + "'"
                        + " AND UserRoute.deletedUser = 0";
                List<HashMap> routeList = dataService.executeSelectHQL(HashMap.class, hqlRoute, true, 0, 0);

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
     * Search MCP
     * DuanND
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.USER_ROUTE.ACTION_SEARCH_USER_ROUTE_ADMIN, method = RequestMethod.POST)
    public @ResponseBody String searchMCPSales(HttpServletRequest request) {
        //get List GoodsCategory from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            LinkedHashMap<String, Object> searchObject;
            try {
                //parse jsonString to a search Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        LinkedHashMap.class);
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            
            String hql = "Select U from MCP as U where type = 1 and deletedUser = 0 ";
            String hql2 = "Select UR from UserRoute as UR where deletedUser = 0 ";
            List<SearchRoute> listSearch = new ArrayList<SearchRoute>();
            if (searchObject != null) {
               //Check from Date, toDate
               SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
               SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy");
               if (searchObject.get("fromDate") != null) {
                   String fromDate = searchObject.get("fromDate").toString();
                   if (fromDate != null && !fromDate.isEmpty() && !fromDate.trim().isEmpty() && !fromDate.equals("")) {
                       try {
                           Date date1 = sDateFormat.parse(fromDate);
                           String fromDate2 = simpleDateFormat.format(date1);
                           Date date2 = simpleDateFormat.parse(fromDate2);
                           //parameterList.add("salesTransDate", date1, ">=");
                           hql += " and beginDate >= '" + simpleDateFormat.format(date2)+"'";
                           hql2 += " and routeAt >= '" + simpleDateFormat.format(date2)+"'";
                       } catch (ParseException e) {
                           // TODO Auto-generated catch block
                     	  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                           
                       }
                   }
               }
               
               if (searchObject.get("toDate") != null) {
                   String toDate = searchObject.get("toDate").toString();
                   if (toDate != null && !toDate.isEmpty() && !toDate.trim().isEmpty() && !toDate.equals("")) {
                       try {
                     	  Date date1 = sDateFormat.parse(toDate);
                           String toDate2 = simpleDateFormat.format(date1);
                           Date date2 = simpleDateFormat.parse(toDate2);
                           Calendar calendar = Calendar.getInstance();
                           calendar.setTime(date2);
                           calendar.add(calendar.DAY_OF_MONTH, 1);
                           //parameterList.add("salesTransDate", date2, "<=");
                           hql += " and beginDate <= '" + simpleDateFormat.format(calendar.getTime())+"'";
                           hql2 += " and routeAt <= '" + simpleDateFormat.format(calendar.getTime())+"'";
                       } catch (ParseException e) {
                           // TODO Auto-generated catch block
                     	  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                       }
                   }
               }
               
               if(searchObject.get("implementEmployeeId") != null){
             	  try{
             		  //Trường hợp chỉ có một nhân viên
                 	  int createdUser = Integer.parseInt(searchObject.get("implementEmployeeId").toString());
                 	  if(createdUser != 0){
                 		 hql += " and implementEmployees.id = " + createdUser;
                 		 hql2 += " and user.id = " + createdUser;
                 		 SearchRoute searchRoute = new SearchRoute();
                 		 //Lấy danh sách tuyến đường mà nhân viên đã đi.
                 		 List<UserRoute> userRoutes = dataService.executeSelectHQL(UserRoute.class, hql2, false, 0, 0);
                 		 if(!userRoutes.isEmpty()){
                 			 searchRoute.setRoutes(userRoutes);
                 		 }
                 		 //Lấy danh sách MCP của nhân viên.
                 		 List<POS> poss = new ArrayList<POS>();
                 		 List<MCP> mcps = dataService.executeSelectHQL(MCP.class, hql, false, 0, 0);
                 		 if(!mcps.isEmpty()){
                 			 if(mcps.get(0).getImplementEmployees() != null){
                     			 searchRoute.setUserName(mcps.get(0).getImplementEmployees().getLastName() + " " + mcps.get(0).getImplementEmployees().getFirstName());
                 			 }
                 		
                 			 for(MCP mcp : mcps){
                 				ParameterList parameterList = new ParameterList("mcps.id", mcp.getId());
                                //Lấy danh sách MCPDetails.
                                List<MCPDetails> list = dataService.getListOption(MCPDetails.class, parameterList);
                                if(!list.isEmpty()){
                                	//Check xem POS đã được chăm sóc chưa
                                	for(MCPDetails mcpDetails : list){
                                		
                                		//Check xem nhân viên đã chăm sóc chưa
                                    	ParameterList parameterList3 = new ParameterList();
                                    	parameterList3.add("implementEmployees.id", mcp.getImplementEmployees().getId());
                                    	parameterList3.add("mcpDetailss.id", mcpDetails.getId());
                                    	parameterList3.add("poss.id", mcpDetails.getPoss().getId());
                                    	//xử lí ngày giờ chăm sóc.
                                    	Date date3 = mcp.getBeginDate();
                                    	SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                                    	String fDate = simpleDateFormat2.format(date3);
                                    	Date fromDate = new Date();
                                    	try {
                        					fromDate = simpleDateFormat2.parse(fDate);
                        				} catch (ParseException e) {
                        					// TODO Auto-generated catch block
                        					return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                        				}
                                    	Calendar calendar = Calendar.getInstance();
                                    	calendar.setTime(fromDate);
                                    	calendar.add(calendar.DAY_OF_MONTH, 1);
                                    	Date toDate = calendar.getTime();
                                    	parameterList3.add("startCustomerCareAt", fromDate, ">=");
                                    	parameterList3.add("startCustomerCareAt", toDate, "<");
                                    	
                                    	List<CustomerCareInformation> cList = dataService.getListOption(CustomerCareInformation.class, parameterList3);
                                    	if(!cList.isEmpty()){
                                    		mcpDetails.getPoss().setIsVisited(1);
                                    		mcpDetails.getPoss().setTimeCared(cList.get(0).getStartCustomerCareAt());
                                    	}
                                    	//Check xem có bán hàng hay không.
                                    	ParameterList parameterList2 = new ParameterList();
                                    	//Xử lí kiểu bán hàng
                                    	parameterList2.add("transType", 2);
                                    	//Xử lí cho điểm bán hàng.
                                    	List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("poss.id", mcpDetails.getPoss().getId()));
                                    	
                                    	if(salesStocks.isEmpty()){
                                    		return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_NO_HAVE_STOCK));
                                    	}
                                    	int stockOfPosId = salesStocks.get(0).getId();
                                    	parameterList2.add("toStocks.id", stockOfPosId);
                                    	//Xử lí nhân viên chăm sóc.
                                    	parameterList2.add("createdUser", mcpDetails.getImplementEmployees().getId());
                                    	//xử lí ngày giờ chăm sóc.
                                    	parameterList2.add("salesTransDate", fromDate, ">=");
                                    	parameterList2.add("salesTransDate", toDate, "<");
                                    	List<SalesTrans> salesTrans = dataService.getListOption(SalesTrans.class, parameterList2);
                                    	if(!salesTrans.isEmpty()){
                                    		mcpDetails.getPoss().setTimeSale(salesTrans.get(0).getSalesTransDate());
                                    	}
                                    	poss.add(mcpDetails.getPoss());
                                	}
                                	
                                	
                                }
                 				 
                 			 }
                 			 
                 		 }
                 		 if(!poss.isEmpty()){
                 			 searchRoute.setPosList(poss);
                 		 }
                 		 listSearch.add(searchRoute);
                 		 //Check xem các 
                 	  }else{
                 		  //Trường hợp có nhiều nhân viên
                 		  if(searchObject.get("employerList") != null){
                 			  ArrayList<LinkedHashMap<String, Object>> employerList = (ArrayList<LinkedHashMap<String, Object>>) searchObject.get("employerList");
                 				  if(employerList.size() > 1){
                 					  
                 					  for(int i = 1; i < employerList.size(); i++){
                     					  int id = Integer.parseInt(employerList.get(i).get("id").toString());
                     					  if(id != 0){
                     						  //Với mỗi nhân viên
                     						 String string1 = "";
                     						 String string2 = "";
                     						 string1 = hql + " and implementEmployees.id = " + id;
                                     		 string2 = hql2 + " and user.id = " + id;
                                     		 SearchRoute searchRoute = new SearchRoute();
                                     		 //Lấy danh sách tuyến đường mà nhân viên đã đi.
                                     		 List<UserRoute> userRoutes = dataService.executeSelectHQL(UserRoute.class, string2, false, 0, 0);
                                     		 if(!userRoutes.isEmpty()){
                                     			 searchRoute.setRoutes(userRoutes);
                                     		 }
                                     		 //Lấy danh sách MCP của nhân viên.
                                     		 List<POS> poss = new ArrayList<POS>();
                                     		 List<MCP> mcps = dataService.executeSelectHQL(MCP.class, string1, false, 0, 0);
                                     		 if(!mcps.isEmpty()){
                                     			 if(mcps.get(0).getImplementEmployees() != null){
                                         			 searchRoute.setUserName(mcps.get(0).getImplementEmployees().getLastName() + " " + mcps.get(0).getImplementEmployees().getFirstName());
                                     			 }
                                     		
                                     			 for(MCP mcp : mcps){
                                     				ParameterList parameterList = new ParameterList("mcps.id", mcp.getId());
                                                    //Lấy danh sách MCPDetails.
                                                    List<MCPDetails> list = dataService.getListOption(MCPDetails.class, parameterList);
                                                    if(!list.isEmpty()){
                                                    	//Check xem POS đã được chăm sóc chưa
                                                    	for(MCPDetails mcpDetails : list){
                                                    		
                                                    		//Check xem nhân viên đã chăm sóc chưa
                                                        	ParameterList parameterList3 = new ParameterList();
                                                        	parameterList3.add("implementEmployees.id", mcp.getImplementEmployees().getId());
                                                        	parameterList3.add("mcpDetailss.id", mcpDetails.getId());
                                                        	parameterList3.add("poss.id", mcpDetails.getPoss().getId());
                                                        	//xử lí ngày giờ chăm sóc.
                                                        	Date date3 = mcp.getBeginDate();
                                                        	SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                                                        	String fDate = simpleDateFormat2.format(date3);
                                                        	Date fromDate = new Date();
                                                        	try {
                                            					fromDate = simpleDateFormat2.parse(fDate);
                                            				} catch (ParseException e) {
                                            					// TODO Auto-generated catch block
                                            					return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                                            				}
                                                        	Calendar calendar = Calendar.getInstance();
                                                        	calendar.setTime(fromDate);
                                                        	calendar.add(calendar.DAY_OF_MONTH, 1);
                                                        	Date toDate = calendar.getTime();
                                                        	parameterList3.add("startCustomerCareAt", fromDate, ">=");
                                                        	parameterList3.add("startCustomerCareAt", toDate, "<");
                                                        	
                                                        	List<CustomerCareInformation> cList = dataService.getListOption(CustomerCareInformation.class, parameterList3);
                                                        	if(!cList.isEmpty()){
                                                        		mcpDetails.getPoss().setIsVisited(1);
                                                        		mcpDetails.getPoss().setTimeCared(cList.get(0).getStartCustomerCareAt());
                                                        	}else{
                                                        		mcpDetails.getPoss().setIsVisited(0);
                                                        	}
                                                        	//Check xem có bán hàng hay không.
                                                        	ParameterList parameterList2 = new ParameterList();
                                                        	//Xử lí kiểu bán hàng
                                                        	parameterList2.add("transType", 2);
                                                        	//Xử lí cho điểm bán hàng.
                                                        	List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("poss.id", mcpDetails.getPoss().getId()));
                                                        	
                                                        	if(salesStocks.isEmpty()){
                                                        		return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_NO_HAVE_STOCK));
                                                        	}
                                                        	int stockOfPosId = salesStocks.get(0).getId();
                                                        	parameterList2.add("toStocks.id", stockOfPosId);
                                                        	//Xử lí nhân viên chăm sóc.
                                                        	parameterList2.add("createdUser", mcpDetails.getImplementEmployees().getId());
                                                        	//xử lí ngày giờ chăm sóc.
                                                        	parameterList2.add("salesTransDate", fromDate, ">=");
                                                        	parameterList2.add("salesTransDate", toDate, "<");
                                                        	List<SalesTrans> salesTrans = dataService.getListOption(SalesTrans.class, parameterList2);
                                                        	if(!salesTrans.isEmpty()){
                                                        		mcpDetails.getPoss().setTimeSale(salesTrans.get(0).getSalesTransDate());
                                                        	}
                                                        	poss.add(mcpDetails.getPoss());
                                                    	}
                                                    }
                                     				 
                                     			 }
                                     			 
                                     		 }
                                     		 if(!poss.isEmpty()){
                                     			 searchRoute.setPosList(poss);
                                     		 }
                                     		 listSearch.add(searchRoute);
                     						  
                     					  }else{
                     						  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                     					  }
                     				  }
                     				
                 				  }else{
                 					  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                 			}
                 		  }
                 	  }
             	  }catch(Exception ex){
             		  if (ex instanceof ConstraintViolationException) {
                           return MsalesJsonUtils.jsonValidate(ex);
                       }//else
                       return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, ex.getMessage()));
             	  }
               }
               
            }

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listSearch));
        
        }else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

}
