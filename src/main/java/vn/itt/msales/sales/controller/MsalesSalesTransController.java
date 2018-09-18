
package vn.itt.msales.sales.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.SalesOrder;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.SalesStockGoods;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.SalesTransDetails;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.SalesTransSearch;

/**
 *
 * @author KhaiCH
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.SALESTRANS.NAME)
public class MsalesSalesTransController extends CsbController {

    /**
     * get a SalesTrans info
     *
     * @param request is a HttpServletRequest
     * @return string json include SalesTrans info
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANS.ACTION_GET_SALES_TRANS, method = RequestMethod.POST)
    public @ResponseBody
    String getSalesTrans(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTrans salesTrans;
            try {
                //parse jsonString to a SalesTrans Object
                salesTrans = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTrans.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTrans from json not null
            if (salesTrans != null) {
                //SalesTrans from json with correct Id
                if (salesTrans.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                int id = salesTrans.getId();
                //get SalesTrans from DB
                salesTrans = dataService.getRowById(salesTrans.getId(),
                        SalesTrans.class);
                //SalesTrans not null
                if (salesTrans != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, salesTrans));
                } //SalesTrans null
                else {
                    return MsalesJsonUtils.notExists(SalesTrans.class, id);
                }

            } //SalesTrans from json null
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
     * create a SalesTrans
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANS.ACTION_CREATE_SALES_TRANS, method = RequestMethod.POST)
    public @ResponseBody
    String createSalesTrans(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTrans salesTrans;
            try {
                //parse jsonString to a SalesTrans Object
                salesTrans = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTrans.class
                );
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesTrans from json not null
            if (salesTrans != null) {
                //validate info 

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();

                if (salesTrans.getCompanyId() != null) {
                    Company company = dataService.getRowById(salesTrans.getCompanyId(), Company.class);
                    if (company == null) {
                        hashErrors.put("Company", "ID = " + salesTrans.getCompanyId() + " không tồn tại.");
                    }
                }
                if (salesTrans.getFromStockId() != null) {
                    SalesStock fromStock = dataService.getRowById(salesTrans.getFromStockId(), SalesStock.class);
                    if (fromStock == null) {
                        hashErrors.put("SalesStock", "ID = " + salesTrans.getFromStockId() + " không tồn tại.");
                    }
                }
                if (salesTrans.getToStockId() != null) {
                    SalesStock toStock = dataService.getRowById(salesTrans.getToStockId(), SalesStock.class);
                    if (toStock == null) {
                        hashErrors.put("SalesStock", "ID = " + salesTrans.getToStockId() + " không tồn tại.");
                    }
                }
                if (salesTrans.getOrderId() != null) {
                    SalesOrder salesOrder = dataService.getRowById(salesTrans.getOrderId(), SalesOrder.class);
                    if (salesOrder == null) {
                        hashErrors.put("SalesOrder", "ID = " + salesTrans.getOrderId() + " không tồn tại.");
                    }
                }
                if (salesTrans.getMcpId() != null) {
                    MCP schedule = dataService.getRowById(salesTrans.getMcpId(), MCP.class);
                    if (schedule == null) {
                        hashErrors.put("Schedule", "ID = " + salesTrans.getMcpId() + " không tồn tại.");
                    }
                }

                if (salesTrans.getCreatedUser() != null) {
                    User createdusUser = dataService.getRowById(salesTrans.getCreatedUser(), User.class);
                    if (createdusUser == null) {
                        hashErrors.put("User", "ID = " + salesTrans.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (salesTrans.getCreatedUser() != null) {
                    User user = dataService.getRowById(salesTrans.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + salesTrans.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save SalesTrans to DB
                    int ret = dataService.insertRow(salesTrans);

                    //save SalesTrans success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save SalesTrans failed
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

            } //SalesTrans from json null
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
     * update a SalesTrans
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANS.ACTION_UPDATE_SALES_TRANS, method = RequestMethod.POST)
    public @ResponseBody
    String updateSalesTrans(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTrans salesTrans;
            try {
                //parse jsonString to a SalesTrans Object
                salesTrans = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTrans.class
                );
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTrans from json not null
            if (salesTrans != null) {
                if (salesTrans.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = salesTrans.getId();
                //validate info
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (salesTrans.getFromStockId() != null) {
                    SalesStock fromStock = dataService.getRowById(salesTrans.getFromStockId(), SalesStock.class);
                    if (fromStock == null) {
                        hashErrors.put("SalesStock", "fromStock  ID = " + salesTrans.getFromStockId() + " không tồn tại.");
                    }
                }
                if (salesTrans.getToStockId() != null) {
                    SalesStock toStock = dataService.getRowById(salesTrans.getToStockId(), SalesStock.class);
                    if (toStock == null) {
                        hashErrors.put("SalesStock", "toStock  ID = " + salesTrans.getToStockId() + " không tồn tại.");
                    }
                }
                if (salesTrans.getOrderId() != null) {
                    SalesOrder salesOrder = dataService.getRowById(salesTrans.getOrderId(), SalesOrder.class);
                    if (salesOrder == null) {
                        hashErrors.put("SalesOrder", "ID = " + salesTrans.getOrderId() + " không tồn tại.");
                    }
                }
                if (salesTrans.getMcpId() != null) {
                    MCP schedule = dataService.getRowById(salesTrans.getMcpId(), MCP.class);
                    if (schedule == null) {
                        hashErrors.put("Schedule", "ID = " + salesTrans.getMcpId() + " không tồn tại.");
                    }
                }

                if (salesTrans.getUpdatedUser() != null) {
                    User user = dataService.getRowById(salesTrans.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + salesTrans.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                
                //khong cho update CompanyId
                SalesTrans rootSalesTrans = dataService.getRowById(salesTrans.getId(), SalesTrans.class);
                if (rootSalesTrans == null) {
                    return MsalesJsonUtils.notExists(SalesTrans.class, id);
                }
                salesTrans.setCompanys(rootSalesTrans.getCompanys());

                try {
                    //update SalesTrans to DB
                    int ret = dataService.updateSync(salesTrans);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(SalesTrans.class, id);
                    }
                    //update success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NULL));
                    }
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }
            } //SalesTrans from json null
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
     * delete a SalesTrans
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANS.ACTION_DELETE_SALES_TRANS, method = RequestMethod.POST)
    public @ResponseBody
    String deleteSalesTrans(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTrans salesTrans;
            try {
                //parse jsonString to a SalesTrans Object
                salesTrans = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTrans.class
                );
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTrans from json not null
            if (salesTrans != null) {
                if (salesTrans.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                if (salesTrans.getDeletedUser() != null) {
                    User user = dataService.getRowById(salesTrans.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, salesTrans.getDeletedUser());
                    }
                }

                int id = salesTrans.getId();
                try {
                    //delete SalesTrans from DB
                    int ret = dataService.deleteSynch(salesTrans);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(SalesTrans.class, id);
                    }
                    //update delete SalesTrans success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update delete SalesTrans failed
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

            } //SalesTrans from json null
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
     * get List SalesTrans by OrderId
     *
     * @param request is a HttpServletRequest
     * @return string json include List SalesTrans
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANS.ACTION_GET_LIST_SALES_TRANS_BY_ORDER_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListSalesTransByOrderId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            SalesTrans salesTrans;
            try {
                //parse jsonString to a SalesTrans Object
                salesTrans = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTrans.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTrans from json not null
            if (salesTrans != null) {
                if (salesTrans.getOrderId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("SalesTrans", "orderId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                //get List SalesTrans by Order id                    
                ParameterList parameterList = new ParameterList("orders.id", salesTrans.getOrderId(), page.getPageNo(), page.getRecordsInPage());
                MsalesResults<SalesTrans> list = dataService.getListOption(SalesTrans.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
            } //SalesTrans from json null
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
     * get List SalesTrans by mcpId
     *
     * @param request is a HttpServletRequest
     * @return string json include List SalesTrans
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANS.ACTION_GET_LIST_SALES_TRANS_BY_MCP_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListSalesTransByMCPId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            SalesTrans salesTrans;
            try {
                //parse jsonString to a SalesTrans Object
                salesTrans = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTrans.class
                );
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTrans from json not null
            if (salesTrans != null) {
                if (salesTrans.getMcpId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("SalesTrans", "mcpId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                //get List SalesTrans by MCP id                    
                ParameterList parameterList = new ParameterList("mcps.id", salesTrans.getMcpId(), page.getPageNo(), page.getRecordsInPage());
                MsalesResults<SalesTrans> list = dataService.getListOption(SalesTrans.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));

            } //SalesTrans from json null
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
     * get List SalesTrans by mcpId
     *
     * @param request is a HttpServletRequest
     * @return string json include List SalesTrans
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANS.ACTION_GET_LIST_SALES_TRANS, method = RequestMethod.POST)
    public @ResponseBody
    String getListSalesTrans(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            SalesTransSearch searchSalesTrans;
            try {
                //parse jsonString to a SalesTransSearch Object
                searchSalesTrans = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTransSearch.class
                );
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTrans from json not null
            if (searchSalesTrans != null) {
                if (searchSalesTrans.getSalesTransDateFrom() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("SalesTrans", "salesTransDateFrom không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                
                if (searchSalesTrans.getSalesTransDateTo()== null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("SalesTrans", "salesTransDateTo không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                
                //get List SalesTrans by MCP id                    
                ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());
                parameterList.add("salesTransDate", searchSalesTrans.getSalesTransDateFrom(),">=");
                parameterList.add("salesTransDate", searchSalesTrans.getSalesTransDateTo(),"<=");
                MsalesResults<SalesTrans> list = dataService.getListOption(SalesTrans.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));

            } //SalesTrans from json null
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
     * DuanND
     * searchSalesTransReceiveReturn
     * Tìm kiếm Thông tin nhận trả hàng của nhân viên bán hàng 
     * @param request
     * @return 
     */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = MsalesConstants.MODULE.SALESTRANS.ACTION_SEARCH_SALES_TRANS_RECEIVE_RETURN, method = RequestMethod.POST)
    public @ResponseBody String searchSalesTransReceiveReturn(HttpServletRequest request) {
        //get List GoodsCategory from DB
    	MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            LinkedHashMap<String, Object> searchObject;
            try {
                //parse jsonString to a search Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, LinkedHashMap.class);
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            String hql = "Select U  from SalesTrans U where deletedUser = 0 ";
            String hql2 = "Select U.id as id from SalesTrans U where deletedUser = 0 ";
            //searchObject from json not null
            if (searchObject != null) {
              //check status
              if(searchObject.get("transType") != null){
            	  try{
                	  int statusId = Integer.parseInt(searchObject.get("transType").toString());
                	  if(statusId != 0){
                		  hql += " and transType = " + statusId;
                		  hql2 += " and transType = " + statusId;
                	  }else{
                		  hql += " and (transType = 1 or transType = 3) ";
                		  hql2 += " and (transType = 1 or transType = 3) ";
                	  }
            	  }catch(Exception ex){
            		  if (ex instanceof ConstraintViolationException) {
                          return MsalesJsonUtils.jsonValidate(ex);
                      }//else
                      return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, ex.getMessage()));
            	  }
              }else{
        		  hql += " and (transType = 1 or transType = 3) ";
        		  hql2 += " and (transType = 1 or transType = 3) ";
        	  }
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
                          hql += " and salesTransDate >= '" + simpleDateFormat.format(date2)+"'";
                          hql2 += " and salesTransDate >= '" + simpleDateFormat.format(date2)+"'";
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
                          hql += " and salesTransDate <= '" + simpleDateFormat.format(calendar.getTime())+"'";
                          hql2 += " and salesTransDate <= '" + simpleDateFormat.format(calendar.getTime())+"'";
                      } catch (ParseException e) {
                          // TODO Auto-generated catch block
                    	  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                      }
                  }
              }
              
              //check createdUser
              if(searchObject.get("searchText") != null){
            	  String key = searchObject.get("searchText").toString();
            	  if(key != null && !key.isEmpty() && !key.trim().isEmpty() && !key.equals("")){
            		  hql += " and ( note LIKE '%" + key + "%'" + " or transCode LIKE '%" + key + "%'" + " ) ";
            		  hql2 += " and ( note LIKE '%" + key + "%'" + " or transCode LIKE '%" + key + "%'" + " ) ";
            	  }
              }
              if(searchObject.get("createdUser") != null){
            	  try{
                	  int createdUser = Integer.parseInt(searchObject.get("createdUser").toString());
                	  if(createdUser != 0){
                		  hql += " and createdUser = " + createdUser;
                		  hql2 += " and createdUser = " + createdUser;
                	  }else{
                		  if(searchObject.get("employerList") != null){
                			  ArrayList<LinkedHashMap<String, Object>> employerList = (ArrayList<LinkedHashMap<String, Object>>) searchObject.get("employerList");
                				  String string1 = "";
                				  String string2 = "";
                				  if(employerList.size() > 1){
                					  for(int i = 1; i < employerList.size(); i++){
                    					  int id = Integer.parseInt(employerList.get(i).get("id").toString());
                    					  if(id != 0){
                    						  List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("salemanUsers.id", id));
                                          	  if(!salesStocks.isEmpty()){
                                          		  string1 += " or fromStocks.id = " + salesStocks.get(0).getId();
                                          		  string2 += " or toStocks.id = " + salesStocks.get(0).getId();
                                          	  }
                    					  }else{
                    						  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                    					  }
                    				  }
                    				  //Xét hql
                    				  hql += " and ( " + string1.substring(3) + string2 + ") ";
                    				  hql2 += " and ( " + string1.substring(3) + string2 + ") ";
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
            //Try....catch
            List<SalesTrans> count = new ArrayList<SalesTrans>();
            List<SalesTrans> lists = new ArrayList<SalesTrans>();
            hql+= " order by salesTransDate DESC ";
            try{
            	count = dataService.executeSelectHQL(SalesTrans.class, hql2, true, 0, 0);
            	lists = dataService.executeSelectHQL(SalesTrans.class, hql, false, page.getPageNo(), page.getRecordsInPage());
            }catch(Exception ex){
            	return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, ex.getMessage()));
            }
           
            if(!lists.isEmpty()){
            	for(SalesTrans salesTrans : lists){
            		if(salesTrans.getTransType() == 1){
            			if(salesTrans.getFromStocks() != null && salesTrans.getFromStocks().getChannels() != null){
            				salesTrans.setChannel(salesTrans.getFromStocks().getChannels());
            			}
                		
                    } else {
                    	if(salesTrans.getToStocks() != null && salesTrans.getToStocks().getChannels() != null )
                    		salesTrans.setChannel(salesTrans.getToStocks().getChannels());
            		}
            		int userId = salesTrans.getCreatedUser();
            		User user = dataService.getRowById(userId, User.class);
            		if(user != null){
            			user.setName(user.getLastName() + " " + user.getFirstName());
                		if(user.getLocations().getLocationType() == 1){
                			user.setLocation(user.getLocations().getName());
                		}else if(user.getLocations().getParents().getLocationType() == 1){
                			user.setLocation(user.getLocations().getParents().getName());
                		}else{
                			user.setLocation(user.getLocations().getParents().getParents().getName());
                		}
                		salesTrans.setEmployee(user);
            		}
            	}
            }
            
            //Phân trang
            MsalesResults<SalesTrans> listMCPs = new MsalesResults<SalesTrans>();
            listMCPs.setCount(Long.parseLong(count.size()+""));
            listMCPs.setContentList(lists);
            //Phân trang
            String[] strings = {"fromStocks","companys","transCode","toStocks","orders","mcps"};
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listMCPs), strings);
           
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
        
    }

    /**
     * get thông tin giao dịch của nhân viên bán hàng.
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANS.ACTION_GET_SALES_TRANS_RECEIVE_RETURN, method = RequestMethod.POST)
    public @ResponseBody String getSalesTransReceiveReturn(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTrans salesTrans;
            try {
                //parse jsonString to a SalesTrans Object
                salesTrans = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTrans.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTrans from json not null
            if (salesTrans != null) {
                //SalesTrans from json with correct Id
                if (salesTrans.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                int id = salesTrans.getId();
                //get SalesTrans from DB
                salesTrans = dataService.getRowById(salesTrans.getId(), SalesTrans.class);
                //SalesTrans not null
                if (salesTrans != null) {
                	User user = new User();
                	int salemanStockId = 0;
                	if(salesTrans.getTransType() == 1){
                		if(salesTrans.getFromStocks() != null && salesTrans.getFromStocks().getChannels() != null){
//                			Channel channel = dataService.getRowById(.getId(), Channel.class);
                			salesTrans.setChannel(salesTrans.getFromStocks().getChannels());
                			//salemanStockId = salesTrans.getToStocks().getId();
                		}
            		
            		}
                	
                	if(salesTrans.getTransType() == 3){
            			//Channel channel = dataService.getRowById(.getId(), Channel.class);
                		if(salesTrans.getToStocks() != null && salesTrans.getToStocks().getChannels() != null){
                			salesTrans.setChannel(salesTrans.getToStocks().getChannels());
                			//salemanStockId = salesTrans.getFromStocks().getId();
                		}
            		}
                	user = dataService.getRowById(salesTrans.getCreatedUser(), User.class);
                	List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("salemanUsers.id", user.getId()));
                	if(salesStocks.isEmpty()){
                		return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALESMAN_NO_HAVE_STOCK));
                	}
                	salemanStockId = salesStocks.get(0).getId();
                	if(user != null){
                		user.setName(user.getLastName() + " " + user.getFirstName());
                		user.setUserRoles("Nhân viên bán hàng");
                	}
            		salesTrans.setEmployee(user);
                	List<SalesTransDetails> lists = dataService.getListOption(SalesTransDetails.class, new ParameterList("salesTranss.id", salesTrans.getId()));
                	if(lists.isEmpty()){
                		return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALES_TRANS_DETAILS_NULL));
                	}
                	for(SalesTransDetails sTransDetails : lists){
                		sTransDetails.setTotalPrice((long) sTransDetails.getPrice()*sTransDetails.getQuantity());
                		ParameterList parameterList = new ParameterList();
                		parameterList.add("goodss.id", sTransDetails.getGoodss().getId());
                		parameterList.add("goodsUnits.id", sTransDetails.getGoodsUnits().getId());
                		parameterList.add("stocks.id", salemanStockId);
                		List<SalesStockGoods> salesStockGoods = dataService.getListOption(SalesStockGoods.class, parameterList);
                		if(salesStockGoods.isEmpty()){
                			return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_GOODS_NO_HAVE_IN_STOCK));
                		}
                		sTransDetails.setSoLuongTon(salesStockGoods.get(0).getQuantity());
                		sTransDetails.setThanhTienTon((long) salesStockGoods.get(0).getQuantity()*salesStockGoods.get(0).getGoodsUnits().getPrice());
                	}
                	salesTrans.setSalesTransDetails(lists);
                	String[] strings = {"companys","fromStocks", "toStocks", "orders", "mcps"};
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, salesTrans), strings);
                } //SalesTrans null
                else {
                    return MsalesJsonUtils.notExists(SalesTrans.class, id);
                }

            } //SalesTrans from json null
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

    //Update duyệt và hủy bán hàng
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANS.ACTION_UPDATE_CANCEL_SALES_TRANS, method = RequestMethod.POST)
    public @ResponseBody
    String DuyetVaHuySalesTrans(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTrans salesTrans;
            try {
                //parse jsonString to a SalesTrans Object
                salesTrans = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesTrans.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTrans from json not null
            if (salesTrans != null) {
                if (salesTrans.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = salesTrans.getId();
                //validate info
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (salesTrans.getFromStockId() != null) {
                    SalesStock fromStock = dataService.getRowById(salesTrans.getFromStockId(), SalesStock.class);
                    if (fromStock == null) {
                        hashErrors.put("SalesStock", "fromStock  ID = " + salesTrans.getFromStockId() + " không tồn tại.");
                    }
                }
                if (salesTrans.getToStockId() != null) {
                    SalesStock toStock = dataService.getRowById(salesTrans.getToStockId(), SalesStock.class);
                    if (toStock == null) {
                        hashErrors.put("SalesStock", "toStock  ID = " + salesTrans.getToStockId() + " không tồn tại.");
                    }
                }
                if (salesTrans.getOrderId() != null) {
                    SalesOrder salesOrder = dataService.getRowById(salesTrans.getOrderId(), SalesOrder.class);
                    if (salesOrder == null) {
                        hashErrors.put("SalesOrder", "ID = " + salesTrans.getOrderId() + " không tồn tại.");
                    }
                }
                if (salesTrans.getMcpId() != null) {
                    MCP schedule = dataService.getRowById(salesTrans.getMcpId(), MCP.class);
                    if (schedule == null) {
                        hashErrors.put("Schedule", "ID = " + salesTrans.getMcpId() + " không tồn tại.");
                    }
                }

                if (salesTrans.getUpdatedUser() != null) {
                    User user = dataService.getRowById(salesTrans.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + salesTrans.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                
                //khong cho update CompanyId
                SalesTrans rootSalesTrans = dataService.getRowById(salesTrans.getId(), SalesTrans.class);
                if (rootSalesTrans == null) {
                    return MsalesJsonUtils.notExists(SalesTrans.class, id);
                }
                salesTrans.setCompanys(rootSalesTrans.getCompanys());

                try {
                    //update SalesTrans to DB
                    int ret = dataService.updateSync(salesTrans);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(SalesTrans.class, id);
                    }
                    //update success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NULL));
                    }
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }
            } //SalesTrans from json null
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANS.ACTION_SEARCH_SALES_TRANS, method = RequestMethod.POST)
    public @ResponseBody
    String searchSalesTrans(HttpServletRequest request) {
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            LinkedHashMap<String, Object> searchObject;
            try {
                //parse jsonString to a search Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, LinkedHashMap.class);
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            String hql = "Select U  from SalesTrans U where (transType = 2) and deletedUser = 0 ";
            String hql2 = "Select U.id as id  from SalesTrans U where (transType = 2) and deletedUser = 0 ";
            //searchObject from json not null
            if (searchObject != null) {
                if (searchObject.get("implementEmployeeId") != null) {
                    try {
                        int createdUser = Integer.parseInt(searchObject.get("implementEmployeeId").toString());
                        if (createdUser != 0) {
                            hql += " and createdUser = " + createdUser;
                            hql2 += " and createdUser = " + createdUser;
                        } else {
                        	 if(searchObject.get("employerList") != null){
                   			  ArrayList<LinkedHashMap<String, Object>> employerList = (ArrayList<LinkedHashMap<String, Object>>) searchObject.get("employerList");
                   				  String string1 = "";
                   				  String string2 = "";
                   				  if(employerList.size() > 1){
                   					  for(int i = 1; i < employerList.size(); i++){
                       					  int id = Integer.parseInt(employerList.get(i).get("id").toString());
                       					  if(id != 0){
                       						  List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("salemanUsers.id", id));
                                             	  if(!salesStocks.isEmpty()){
                                             		  string1 += " or fromStocks.id = " + salesStocks.get(0).getId();
                                             		  string2 += " or toStocks.id = " + salesStocks.get(0).getId();
                                             	  }
                       					  }else{
                       						  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                       					  }
                       				  }
                       				  //Xét hql
                       				  hql += " and ( " + string1.substring(3) + string2 + ") ";
                       				 hql2 += " and ( " + string1.substring(3) + string2 + ") ";
                   				  }else{
                   					  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                   			}
                   		  }
                        }
                    } catch (Exception ex) {
                        if (ex instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(ex);
                        }//else
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, ex.getMessage()));
                    }
                }

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
                            hql += " and salesTransDate >= '" + simpleDateFormat.format(date2) + "'";
                            hql2 += " and salesTransDate >= '" + simpleDateFormat.format(date2) + "'";
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
                            hql += " and salesTransDate <= '" + simpleDateFormat.format(calendar.getTime()) + "'";
                            hql2 += " and salesTransDate <= '" + simpleDateFormat.format(calendar.getTime()) + "'";
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                        }
                    }
                }

                //check createdUser
                if (searchObject.get("searchText") != null) {
                    String key = searchObject.get("searchText").toString();
                    if (key != null && !key.isEmpty() && !key.trim().isEmpty() && !key.equals("")) {
                        hql += " and ( note LIKE '%" + key + "%'" + " or transCode LIKE '%" + key + "%'" + " ) ";
                        hql += " and ( note LIKE '%" + key + "%'" + " or transCode LIKE '%" + key + "%'" + " ) ";
                    }
                }
            }

            List<SalesTrans> lists = dataService.executeSelectHQL(SalesTrans.class, hql, false, page.getPageNo(), page.getRecordsInPage());
            List<SalesTrans> count = dataService.executeSelectHQL(SalesTrans.class, hql2, true, 0, 0);
            if (!lists.isEmpty()) {
                for (SalesTrans salesTrans : lists) {
                    User user = dataService.getRowById(salesTrans.getCreatedUser(), User.class);
                    user.setName(user.getLastName() + " " + user.getFirstName());
                    if (user.getLocations().getLocationType() == 1) {
                        user.setLocation(user.getLocations().getName());
                    } else if (user.getLocations().getParents().getLocationType() == 1) {
                        user.setLocation(user.getLocations().getParents().getName());
                    } else {
                        user.setLocation(user.getLocations().getParents().getParents().getName());
                    }
                    salesTrans.setEmployee(user);

                    // Get sales tran details
                    List<SalesTransDetails> salesTransDetails = dataService.getListOption(SalesTransDetails.class, new ParameterList("salesTranss.id", salesTrans.getId(), "="));
                    salesTrans.setSalesTransDetails(salesTransDetails);
                }
            }

            //pagination
            MsalesResults<SalesTrans> listMCPs = new MsalesResults<SalesTrans>();
            listMCPs.setCount(Long.parseLong(count.size() + ""));
            listMCPs.setContentList(lists);
           
            String[] strings = {"fromStocks", "companys", "transCode", "orders", "mcps"};
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listMCPs), strings);

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

    }

}
