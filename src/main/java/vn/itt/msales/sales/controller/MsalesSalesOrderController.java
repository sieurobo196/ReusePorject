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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.entity.SalesOrder;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsUnit;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.SalesOrderDetails;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.SalesStockGoods;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.SalesTransDetails;
import vn.itt.msales.entity.Session;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.ReceiveGoods;
import vn.itt.msales.entity.searchObject.SearchArrayId;
import vn.itt.msales.logex.MSalesException;

/**
 *
 * @author Cutx
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.NAME)
public class MsalesSalesOrderController extends CsbController {

    /**
     * get a SalesOrder info
     *
     * @param request is a HttpServletRequest
     * @return string json include SalesOrder info
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.ACTION_GET_SALES_ORDER, method = RequestMethod.POST)
    public @ResponseBody
    String getSalesOrder(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SalesOrder salesOrder;
            try {
                //parse jsonString to a SalesOrder Object
                salesOrder = (SalesOrder) MsalesJsonUtils.getObjectFromJSON(jsonString, SalesOrder.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesOrder from json not null
            if (salesOrder != null) {
                //SalesOrder from json with correct Id
                if (salesOrder.getId() >= 0) {
                    //get SalesOrder from DB
                    salesOrder = (SalesOrder) dataService.getRowById(salesOrder.getId(), SalesOrder.class);
                    //SalesOrder not null
                    if (salesOrder != null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, salesOrder));
                    } //SalesOrder null
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                    }
                } //SalesOrder from json with incorrect Id 
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL, "ID Null"));
                }
            } //SalesOrder from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
        }
    }

    /**
     * create a SalesOrder
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.ACTION_CREATE_SALES_ORDER, method = RequestMethod.POST)
    public @ResponseBody
    String createSalesOrder(HttpServletRequest request) {

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SalesOrder salesOrder;
            try {
                //parse jsonString to a SalesOrder Object
                salesOrder = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesOrder.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesOrder from json not null
            if (salesOrder != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (salesOrder.getCompanyId() != null) {
                    Company company = dataService.getRowById(salesOrder.getCompanyId(), Company.class);
                    if (company == null) {
                        hashErrors.put("Company", "ID = " + salesOrder.getCompanyId() + " không tồn tại.");
                    }
                }

                if (salesOrder.getStockId() != null) {
                    //get stock from SalesOrder
                    SalesStock stock = dataService.getRowById(salesOrder.getStockId(), SalesStock.class);
                    //stock is not exist
                    if (stock == null) {
                        //return message warning goods is not exist in DB
                        hashErrors.put("SalesStock",
                                "SalesStock with ID = " + salesOrder.getStockId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                //check validate user create
                if (salesOrder.getCreatedUser() != null) {
                    User user = dataService.getRowById(salesOrder.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + salesOrder.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                try {

                    //save SalesOrder to DB
                    int ret = dataService.insertRow(salesOrder);

                    //save Succcess
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                    } //save failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_INSERT_FAIL));
                    }
                } catch (MSalesException e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }

                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));

                }

            } //salesOrder from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * update a SalesOrder
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.ACTION_UPDATE_SALES_ORDER, method = RequestMethod.POST)
    public @ResponseBody
    String updateSalesOrder(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            SalesOrder salesOrder;
            try {
                salesOrder = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesOrder.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesOrder from json not null
            if (salesOrder != null) {
                //check id
                if (salesOrder.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));

                }
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                //get stock from SalesOrder
                if (salesOrder.getStockId() != null) {
                    SalesStock stock = dataService.getRowById(salesOrder.getStockId(), SalesStock.class);
                    if (stock == null) {
                        hashErrors.put("stock",
                                "stock with ID = " + salesOrder.getStockId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                }
                //check validate user update
                if (salesOrder.getUpdatedUser() != null) {
                    User user = dataService.getRowById(salesOrder.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + salesOrder.getUpdatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                //khong cho update CompanyId
                SalesOrder rootsaSalesOrder = dataService.getRowById(salesOrder.getId(), SalesOrder.class);
                if (rootsaSalesOrder == null) {
                    return MsalesJsonUtils.notExists(SalesOrder.class, salesOrder.getId());
                }
                salesOrder.setCompanys(rootsaSalesOrder.getCompanys());

                try {
                    //update SalesOrder to DB
                    int ret = dataService.updateSync(salesOrder);

                    //update success
                    if (ret == -2) {
                        hashErrors.put("SalesOrder",
                                "SalesOrder with ID = " + salesOrder.getId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                    } //update failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_UPDATE_FAIL));
                    }
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));
                }
            } //SalesOrder from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * delete a SalesOrder
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.ACTION_DELETE_SALES_ORDER, method = RequestMethod.POST)
    public @ResponseBody
    String deleteSalesOrder(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            SalesOrder salesOrder;
            try {
                //parse jsonString to a SalesOrder Object
                salesOrder = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesOrder.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesOrder from json not null
            if (salesOrder != null) {
                if (salesOrder.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));

                }
                if (salesOrder.getDeletedUser() != null) {
                    User user = dataService.getRowById(salesOrder.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, salesOrder.getDeletedUser());
                    }

                }
                try {
                    //update delete SalesOrder
                    int ret = dataService.deleteSynch(salesOrder);
                    if (ret == -2) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("SalesOrder",
                                "SalesOrder with ID = " + salesOrder.getId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else //update delete SalesOrder success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                    } //update delete failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));
                }

            } //SalesOrder from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get List salesOrder by Goods
     *
     * @param request is a HttpServletRequest
     * @return string json include List salesOrder
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.ACTION_GET_LIST_SALES_ORDER_BY_STOCK_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListSalesOrderByStockId(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null

        if (jsonString != null) {
            SalesOrder salesOrder;
            try {
                //parse jsonString to a Channel Object
                salesOrder = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesOrder.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Schedule from json not null
            if (salesOrder != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (salesOrder.getStockId() != null) {
                    SalesStock salesStock = dataService.getRowById(salesOrder.getStockId(), SalesStock.class);
                    if (salesStock == null) {
                        hashErrors.put("SalesStock",
                                "SalesStock with ID = " + salesOrder.getStockId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    hashErrors.put("stockId", "  không được null!");
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List MCP by implementEmployeeId 
                ParameterList parameterList = new ParameterList("stocks.id", salesOrder.getStockId(), page.getPageNo(), page.getRecordsInPage());

                MsalesResults<SalesOrder> list = dataService.getListOption(SalesOrder.class, parameterList, true);

                String[] strings = {
                    "poss",
                    "salemanUsers",
                    "channels", "statusId", "pos"};

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list), strings);

            } //goods from json null
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
     * get List sales Order by sales trans date
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.ACTION_GET_LIST_SALES_ORDER_BY_SALES_TRANS_DATE, method = RequestMethod.POST)
    public @ResponseBody
    String getListSalesOrderBySalesTransDate(HttpServletRequest request) throws ParseException {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SalesOrder salesOrder;
            try {
                //parse jsonString to a SalesOrder Object
                salesOrder = (SalesOrder) MsalesJsonUtils.getObjectFromJSON(jsonString, SalesOrder.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //salesOrder from json not null
            if (salesOrder != null) {
                //salesOrder from json with correct Id
                if (salesOrder.getSalesTransDate() != null) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    Date dateMin = salesOrder.getSalesTransDate();
                    dateMin.setHours(0);
                    dateMin.setMinutes(0);
                    dateMin.setSeconds(0);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateMin);
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);

                    Date dateMax = calendar.getTime();

                    ArrayList<Object> value = new ArrayList<Object>();
                    value.add(dateMin);
                    value.add(dateMax);

                    MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
                    ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());
//                    parameterList.add("salesTransDate", salesOrder.getSalesTransDate());
                    parameterList.add("salesTransDate", dateMin, ">");
                    parameterList.add("salesTransDate", dateMax, "<");

                    //get SalesOrder from DB
                    MsalesResults<SalesOrder> list = dataService.getListOption(SalesOrder.class, parameterList, true);
                    String[] strings = {
                        "poss",
                        "salemanUsers",
                        "channels", "statusId", "pos"};
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, list), strings);

                } //goods from json null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NULL));
                }

            } //jsonString null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.JSON_CONTENTS_EMPTY));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
        }
    }

    /**
     * DuanND Hủy bỏ deactiveSalesOrder
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.ACTION_DEACTIVE_SALES_ORDER, method = RequestMethod.POST)
    public @ResponseBody
    String deactiveSalesOrder(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            LinkedHashMap<String, Object> salesOrder = new LinkedHashMap<String, Object>();
            try {
                salesOrder = MsalesJsonUtils.getObjectFromJSON(jsonString, LinkedHashMap.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesOrder from json not null
            if (salesOrder != null) {
                //check id
                SalesOrder rootsaSalesOrder;
                if (salesOrder.get("id") == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
                }
                String orderId = salesOrder.get("id").toString();

                if (orderId == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
                } else {
                    if (!orderId.matches("-?\\d+(\\.\\d+)?")) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.JSON_VALUE_INVALID, "id là một số tự nhiên"));
                    }
                    int id = 0;
                    try {
                        id = Integer.parseInt(orderId);
                    } catch (NumberFormatException nfe) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.JSON_VALUE_INVALID, "id là một số tự nhiên không phải số thập phân"));
                    }
                    rootsaSalesOrder = dataService.getRowById(id, SalesOrder.class);
                    if (rootsaSalesOrder == null) {
                        return MsalesJsonUtils.notExists(SalesOrder.class, id);
                    }
                }
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                //check validate user update
                if (salesOrder.get("updatedUser") == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, "updatedUser bắt buộc nhập!"));
                }
                String updatedUser = salesOrder.get("updatedUser").toString();
                if (updatedUser != null) {
                    if (!updatedUser.matches("-?\\d+(\\.\\d+)?")) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.JSON_VALUE_INVALID, "updatedUser là một số tự nhiên"));
                    }
                    int updatedUserId = 0;
                    try {
                        updatedUserId = Integer.parseInt(updatedUser);
                    } catch (NumberFormatException nfe) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.JSON_VALUE_INVALID, "updatedUser là một số tự nhiên không phải số thập phân"));
                    }
                    User user = dataService.getRowById(updatedUserId, User.class);
                    if (user == null) {
                        hashErrors.put("User", "updatedUser với Id = " + updatedUserId + " không tồn tại.");
                    }
                    rootsaSalesOrder.setUpdatedUser(updatedUserId);
                } else {
                    hashErrors.put("User", "updatedUser bắt buộc nhập!");
                }
                //check customerCareUser
                if (salesOrder.get("customerCareUser") != null) {
                    String customerCareUser = salesOrder.get("customerCareUser").toString();
                    if (customerCareUser != null) {
                        if (!customerCareUser.matches("-?\\d+(\\.\\d+)?")) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.JSON_VALUE_INVALID, "customerCareUser là một số tự nhiên"));
                        }
                        int customerCareUserId = 0;
                        try {
                            customerCareUserId = Integer.parseInt(customerCareUser);
                        } catch (NumberFormatException nfe) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.JSON_VALUE_INVALID, "customerCareUser là một số tự nhiên không phải số thập phân"));
                        }
                        //int customerCareUserId = Integer.parseInt(customerCareUser);
                        User user = dataService.getRowById(customerCareUserId, User.class);
                        if (user == null) {
                            hashErrors.put("User", "customerCareUser với Id = " + customerCareUserId + " không tồn tại.");
                        }
                        rootsaSalesOrder.setCreatedUser(customerCareUserId);
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_VALUE_INVALID, hashErrors));
                }
                //khong cho update CompanyId

                //salesOrder.setCompanys(rootsaSalesOrder.getCompanys());
                rootsaSalesOrder.setUpdatedAt(new Date());

                rootsaSalesOrder.setStatusId(14);
                dataService.updateRow(rootsaSalesOrder);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));

            } //SalesOrder from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * DuanND exportSalesOrder
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.ACTION_EXPORT_SALES_ORDER, method = RequestMethod.POST)
    public @ResponseBody
    String exportSalesOrder(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            SalesOrder salesOrder;
            try {
                salesOrder = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesOrder.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesOrder from json not null
            if (salesOrder != null) {
                SalesOrder rootsaSalesOrder;
                //Kiểm tra id null và id có trong DB không?
                if (salesOrder.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));

                } else {
                    rootsaSalesOrder = dataService.getRowById(salesOrder.getId(), SalesOrder.class);
                    if (rootsaSalesOrder == null) {
                        return MsalesJsonUtils.notExists(SalesOrder.class, salesOrder.getId());
                    }
                }

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                //Kiểm tra người tạo giao dịch có tồn tại không
                User user = new User();
                if (salesOrder.getCreatedUser() != null) {
                    user = dataService.getRowById(salesOrder.getCreatedUsers().getId(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User với Id = " + salesOrder.getCreatedUser() + " không tồn tại.");
                    }
                }
                //Lấy kho hàng của người bán hàng
                List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("salemanUsers.id", rootsaSalesOrder.getCreatedUsers().getId()));
                if (salesStocks.size() == 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.APP_SALESMAN_NO_HAVE_STOCK, MsalesValidator.APP_SALESMAN_NO_HAVE_STOCK));
                }
                int salesStockId = salesStocks.get(0).getId();
                //check kho

                //cập nhật người tạo giao dịch xuất kho
                rootsaSalesOrder.setUpdatedUser(salesOrder.getCreatedUsers().getId());
                rootsaSalesOrder.setUpdatedAt(new Date());
                //Lấy danh sách sales Order details
                List<SalesOrderDetails> salesOrderDetails = dataService.getListOption(SalesOrderDetails.class, new ParameterList("orders.id", salesOrder.getId()));
                if (!salesOrderDetails.isEmpty()) {
                    for (SalesOrderDetails sOrderDetails : salesOrderDetails) {
                        //Lấy số lượng hàng của hóa đơn.
                        int quantity = sOrderDetails.getQuantity();
                        //Lấy số lượng hàng của Sales_man_stock
                        ParameterList parameterList = new ParameterList();
                        parameterList.add("stocks.id", salesStockId);
                        parameterList.add("goodss.id", sOrderDetails.getGoodss().getId());
                        List<SalesStockGoods> salesStockGoods = dataService.getListOption(SalesStockGoods.class, parameterList);
                        //Kiểm tra số lượng trong kho
                        int sluongGoods = 0;
                        if (salesStockGoods.size() > 0) {
                            for (SalesStockGoods sGoods : salesStockGoods) {
                                sluongGoods = sluongGoods + sGoods.getQuantity();
                            }
                        } else {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_GOODS_NO_HAVE_IN_STOCK, sOrderDetails.getGoodss().getName() + ": " + MsalesValidator.APP_GOODS_NO_HAVE_IN_STOCK));
                        }
                        if (quantity > sluongGoods) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_OVER_GOODS, sOrderDetails.getGoodss().getName() + ": " + MsalesValidator.APP_OVER_GOODS));
                        }
                    }
                }
                //Xét thuộc tính cho Sales_Trans
                SalesTrans salesTrans = new SalesTrans();
                salesTrans.setCompanyId(user.getCompanys().getId());
                salesTrans.setToStockId(rootsaSalesOrder.getStocks().getId());
                salesTrans.setFromStockId(salesStockId);
                salesTrans.setOrderId(rootsaSalesOrder.getId());
                salesTrans.setSalesTransDate(new Date());
                salesTrans.setCreatedUser(salesOrder.getCreatedUsers().getId());
                salesTrans.setTransType(2);

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                int ret = 0;
                try {
                    //insert salesTrans
                    ret = dataService.insertRow(salesTrans);
                    if (ret > 0) {
                        SalesTransDetails sTransDetails = new SalesTransDetails();
                        for (SalesOrderDetails sDetails : salesOrderDetails) {
                            //Xét dữ liệu cho SalesTransDetails
                            sTransDetails.setSalesTransId(ret);
                            sTransDetails.setGoodsId(sDetails.getGoodss().getId());
                            sTransDetails.setGoodsUnitId(sDetails.getGoodsUnits().getId());
                            sTransDetails.setSalesTransDate(new Date());
                            sTransDetails.setPrice(sDetails.getPrice());
                            sTransDetails.setQuantity(sDetails.getQuantity());
                            sTransDetails.setIsFocus(sDetails.getIsFocus());
                            sTransDetails.setCreatedUser(salesOrder.getCreatedUsers().getId());
                            //insert một salesTransDetails
                            dataService.insertRow(sTransDetails);
                            //Cập nhật quantity cho SalesStockGoods
                            ParameterList parameterList4 = new ParameterList();
                            //set stockId for get info
                            parameterList4.add("stocks.id", salesStockId);
                            parameterList4.add("goodss.id", sDetails.getGoodss().getId());
                            parameterList4.add("goodsUnits.id", sDetails.getGoodsUnits().getId());
                            List<SalesStockGoods> salesStockGoods = dataService.getListOption(SalesStockGoods.class, parameterList4);
                            for (SalesStockGoods stockGoods : salesStockGoods) {
                                //Xét property cho salesStockGoods
                                int quantity = stockGoods.getQuantity();
                                stockGoods.setQuantity(quantity - sDetails.getQuantity());
                                stockGoods.setUpdatedUser(salesOrder.getCreatedUsers().getId());
                                stockGoods.setUpdatedAt(new Date());
                                dataService.updateRow(stockGoods);
                            }
                        }
                        //update SalesOrder
                        //trang thai Don hang thanh cong id = 12
                        rootsaSalesOrder.setStatusId(12);
                        dataService.updateRow(rootsaSalesOrder);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } // location from DB null
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_INSERT_FAIL));
                    }

                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));
                }

            } //SalesOrder from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * DuanND getSalesOrder administrator
     *
     * @param request is a jsonString have id
     * @return info of SalesOrder
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.ACTION_GET_SALES_ORDER_ADMINISTRATOR, method = RequestMethod.POST)
    public @ResponseBody
    String getSalesOrderAdmin(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SalesOrder salesOrder;
            try {
                //parse jsonString to a SalesOrder Object
                salesOrder = (SalesOrder) MsalesJsonUtils.getObjectFromJSON(jsonString, SalesOrder.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesOrder from json not null
            if (salesOrder != null) {
                //SalesOrder from json with correct Id
                if (salesOrder.getId() != null) {
                    //get SalesOrder from DB
                    int id = salesOrder.getId();
                    salesOrder = (SalesOrder) dataService.getRowById(salesOrder.getId(), SalesOrder.class);
                    if (salesOrder == null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, "Id = " + id + "không tồn tại!"));
                    }
                    //Lấy thêm dữ liệu cho salesOrder
                    //lấy thông tin nhân viên chăm sóc khách hàng
                    User user = dataService.getRowById(salesOrder.getCreatedUsers().getId(), User.class);
                    //xét tên nhân viên
                    user.setName(user.getLastName() + " " + user.getFirstName());
                    //ghi thông tin nhân viên vào Order
                    salesOrder.setCustomerCareUser(user);
                    SalesOrder sOrder = new SalesOrder();
                    sOrder.setOrderInfomation(salesOrder);
                    //xét ngày tạo order
                    sOrder.setOrderDate(salesOrder.getCreatedAt());
                    //xét ngày giao dịch
                    sOrder.setSalesTransDate(salesOrder.getSalesTransDate());
                    //Lấy thông tin Status
                    //sOrder.setStatus(salesOrder.getStatuss().getName());
                    sOrder.setStatuss(salesOrder.getStatuss());
                    //Lấy thông tin chi tết đặt hàng
                    List<SalesOrderDetails> salesOrderDetails = dataService.getListOption(SalesOrderDetails.class, new ParameterList("orders.id", salesOrder.getId()));
                    for (SalesOrderDetails sOrderDetails : salesOrderDetails) {
                        Goods goods = sOrderDetails.getGoodss();
                        goods.setCode(goods.getGoodsCode());
                        sOrderDetails.setGoods(goods);
                        GoodsUnit goodsUnit = sOrderDetails.getGoodsUnits();
                        goodsUnit.setName(goodsUnit.getUnits().getName());
                        sOrderDetails.setUnit(goodsUnit);
                        sOrderDetails.setOrderDetailsId(sOrderDetails.getId());
                    }
                    sOrder.setOrderDetailsList(salesOrderDetails);
                    sOrder.setId(salesOrder.getId());
                    String[] strings = {"createdUsers", "status", "statusTypes"};
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, sOrder), strings);
                    //SalesOrder not null
                } //SalesOrder from json with incorrect Id 
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL, "Id bắt buộc nhập"));
                }
            } //SalesOrder from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
        }
    }

    /**
     * DuanND: Tìm kiếm Sales_Order
     *
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.ACTION_SEARCH_ORDER, method = RequestMethod.POST)
    public @ResponseBody
    String searchOrder(HttpServletRequest request) {
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

            String hql = "Select U  from SalesOrder U where 1=1 ";
            //searchObject from json not null
            if (searchObject != null) {
                //String hql = "Select U from " + SalesOrder.class.getName() + " U "+ " where 1=1 ";

                ParameterList parameterList2 = new ParameterList();
                if (searchObject.get("channelId") != null) {
                    try {
                        int channelId = Integer.parseInt(searchObject.get("channelId").toString());
                        parameterList2.add("channels.id", channelId);
                    } catch (Exception ex) {
                        if (ex instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(ex);
                        }//else
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, ex.getMessage()));
                    }
                }
                if (searchObject.get("locationId") != null) {
                    try {
                        int locationId = Integer.parseInt(searchObject.get("locationId").toString());
                        parameterList2.add("locations.id", locationId);
                    } catch (Exception e) {
                        if (e instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(e);
                        }//else
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                    }
                }
                if (searchObject.get("locationId") != null || searchObject.get("channelId") != null) {
                    List<POS> listPOS = dataService.getListOption(POS.class, parameterList2);
                    for (POS pos : listPOS) {
                        hql += " or pos.id = " + pos.getId();
                    }
                }

                //check trường 
                if (searchObject.get("transStatus") != null) {
                    try {
                        int statusId = Integer.parseInt(searchObject.get("transStatus").toString());
                        hql += " and statusId = " + statusId;
                    } catch (Exception ex) {
                        if (ex instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(ex);
                        }//else
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, ex.getMessage()));
                    }
                }
                //Check from Date, toDate
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if (searchObject.get("fromDate") != null) {
                    String fromDate = searchObject.get("fromDate").toString();
                    if (fromDate != null) {
                        try {
                            Date date1 = simpleDateFormat.parse(fromDate);
                            //parameterList.add("salesTransDate", date1, ">=");
                            hql += " and salesTransDate >= '" + simpleDateFormat.format(date1) + "'";
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
                            //parameterList.add("salesTransDate", date2, "<=");
                            hql += " and salesTransDate <= '" + simpleDateFormat.format(date2) + "'";
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                //check createdUser
                if (searchObject.get("searchText") != null) {
                    String key = searchObject.get("searchText").toString();
                    hql += " and ( note LIKE '%" + key + "%'" + " or transCode LIKE '%" + key + "%'" + " ) ";
                }
                if (searchObject.get("createdUser") != null) {
                    try {
                        int createdUser = Integer.parseInt(searchObject.get("createdUser").toString());
                        hql += " and createdUser = " + createdUser;
                    } catch (Exception ex) {
                        if (ex instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(ex);
                        }//else
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, ex.getMessage()));
                    }
                }
            }

            List<SalesOrder> lists = dataService.executeSelectHQL(SalesOrder.class, hql, false, 0, 0);
            for (SalesOrder salesOrder : lists) {
                salesOrder.setStatus(dataService.getRowById(salesOrder.getStatusId(), Status.class).getName());
                salesOrder.setOrderDate(salesOrder.getCreatedAt());
                User user = dataService.getRowById(salesOrder.getCreatedUser(), User.class);
                user.setName(user.getLastName() + " " + user.getFirstName());
                user.setLocation(user.getLocations().getName());
                salesOrder.setCustomerCareUser(user);
                List<SalesOrderDetails> salesOrderDetails = dataService.getListOption(SalesOrderDetails.class, new ParameterList("orders.id", salesOrder.getId()));
                int quantity = 0;
                for (SalesOrderDetails sOrderDetails : salesOrderDetails) {
                    quantity += sOrderDetails.getQuantity();
                }
                salesOrder.setQuantity(quantity);
            }
            //Phân trang
            MsalesResults<SalesOrder> listMCPs = new MsalesResults<SalesOrder>();
            listMCPs.setCount(Long.parseLong(lists.size() + ""));
            //Phân trang
            if (page.getPageNo() == 1) {
                if (page.getRecordsInPage() == 10) {
                    if (lists.size() <= 10) {
                        listMCPs.setContentList(lists);
                    } else {
                        listMCPs.setContentList(lists.subList(0, 10));
                    }
                } else {
                    if (lists.size() <= page.getRecordsInPage()) {
                        listMCPs.setContentList(lists);
                    } else {
                        listMCPs.setContentList(lists.subList(0, page.getRecordsInPage()));
                    }
                }
            } else {
                if (page.getRecordsInPage() == 10) {
                    if (lists.size() <= 10) {

                    } else {
                        if (lists.size() / 10 + 1 == page.getPageNo()) {
                            listMCPs.setContentList(lists.subList((page.getPageNo() - 1) * page.getRecordsInPage(), lists.size()));
                        } else if (lists.size() / 10 + 1 >= page.getPageNo()) {
                            listMCPs.setContentList(lists.subList((page.getPageNo() - 1) * page.getRecordsInPage(), page.getPageNo() * page.getRecordsInPage()));
                        } else {

                        }
                    }
                } else {
                    if (lists.size() <= page.getRecordsInPage()) {

                    } else {
                        if (lists.size() / page.getRecordsInPage() + 1 == page.getPageNo()) {
                            listMCPs.setContentList(lists.subList((page.getPageNo() - 1) * page.getRecordsInPage(), lists.size()));
                        } else if (lists.size() / page.getRecordsInPage() + 1 >= page.getPageNo()) {
                            listMCPs.setContentList(lists.subList((page.getPageNo() - 1) * page.getRecordsInPage(), page.getPageNo() * page.getRecordsInPage()));
                        } else {

                        }
                    }
                }
            }
            String[] strings = {"transCode", "statusId", "note", "stocks", "statuss", "locations"};
            //MsalesResults<SalesOrder> sList = dataService.getListOption(SalesOrder.class, parameterList, true);
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listMCPs), strings);

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Search Order by List UserId
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.ACTION_SEARCH_ORDER_BY_LIST_USER_ID, method = RequestMethod.POST)
    public @ResponseBody
    String searchSalesOrderByListUserId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            SearchArrayId searchArrayId;
            try {
                //parse jsonString to a SalesOrder Object
                searchArrayId = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchArrayId.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesOrder from json not null
            if (searchArrayId != null) {
                LinkedHashMap<String, Object> hashErrors = new LinkedHashMap();
                if (searchArrayId.getIds() == null) {
                    hashErrors.put("ids", MsalesValidator.NOT_NULL);
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                String idsString = "";
                for (int i = 0; i < searchArrayId.getIds().length; i++) {
                    idsString += searchArrayId.getIds()[i] + ",";
                }
                idsString += "''";

                String hqlCount = "SELECT COUNT(DISTINCT SalesOrder.id) AS count";
                String hqlResult = "SELECT SalesOrder.id AS id,"
                        + " SalesOrder.pos AS pos,"
                        + " User AS customerCareUser,"
                        + " SalesOrder.createdAt AS orderDate,"
                        + " SalesOrder.salesTransDate AS salesTransDate,"
                        + " SalesOrder.statuss AS status,"
                        + " SUM(SalesOrderDetails.quantity) AS quantity";

                String hql = " FROM SalesOrderDetails AS SalesOrderDetails"
                        + " JOIN SalesOrderDetails.orders AS SalesOrder"
                        + " JOIN SalesOrder.createdUsers AS User"
                        + " WHERE SalesOrderDetails.deletedUser = 0"
                        + " AND SalesOrder.deletedUser = 0"
                        + " AND User.id IN (" + idsString + ")";

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (searchArrayId.getFromDate() != null) {
                    hql += " AND SalesOrder.createdAt >= '" + sdf.format(searchArrayId.getFromDate()) + "'";
                }
                if (searchArrayId.getToDate() != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(searchArrayId.getToDate());
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    hql += " AND SalesOrder.createdAt < '" + sdf.format(calendar.getTime()) + "'";
                }
                if (searchArrayId.getStatusId() != null && searchArrayId.getStatusId() != 0) {
                    hql += " AND SalesOrder.statuss.id = '" + searchArrayId.getStatusId() + "'";
                }

                if (searchArrayId.getSearchText() != null && !searchArrayId.getSearchText().trim().isEmpty()) {
                    String searchText = searchArrayId.getSearchText();
                    searchText = searchText.replaceAll("'", "''");
                    //searchText la ten/ma cua nhan vien 
                    hql += " AND ( CONCAT(User.lastName,' ',User.firstName) LIKE '%" + searchText + "%'"
                            + " OR User.userCode LIKE '%" + searchText + "%')";
                }

                hqlCount += hql;
                List<HashMap> countMap = dataService.executeSelectHQL(HashMap.class, hqlCount, true, 0, 0);

                hqlResult += hql + " GROUP BY SalesOrder.id"
                        + " ORDER BY SalesOrder.salesTransDate DESC";

                List<HashMap> listMap = dataService.executeSelectHQL(HashMap.class, hqlResult, true, page.getPageNo(), page.getRecordsInPage());

                long count = (long) countMap.get(0).get("count");
                MsalesResults<HashMap> result = new MsalesResults();
                result.setContentList(listMap);
                result.setCount(count);

                String[] filter = {"birthday", "sex", "email", "yahooId", "skypeId", "isdn", "tel", "note", "isActive",
                    "activeCode", "employerType", "useEvoucher", "ipLastLogin", "createdUser", "updatedUser", "monitoringUsers", "employerUsers", "companys",
                    "statusType", "value", "channels", "statuss", "hierarchy", "ownerName", "ownerCode", "ownerCodeDate", "ownerCodeLocation",
                    "mobile", "otherTel", "fax", "website", "gpkd", "gpkdDate", "gpkdLocation", "lat", "lng", "isActive", "beginAt", "endAt", "createdAt"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, result), filter);
            } //SalesOrder from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
        }
    }
    
    /**
     * Đặt hàng qua điện thoại
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER.ACTION_SET_GOODS_SALES_ORDER_MOBILE, method = RequestMethod.POST)
    public @ResponseBody String setGoodsOrderMobileApp(HttpServletRequest request) {
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
                if (receiveGoods.getPosId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_ID_NULL));
                }
                if (receiveGoods.getSalesTransDate() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALES_TRANS_DATE));
                }
                if (receiveGoods.getStockGoods() == null || receiveGoods.getStockGoods().isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_STOCK_GOODS_NULL));
                }
                if (receiveGoods.getCreatedUser() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_USER_NO_LOGIN));
                }
                //Kiểm tra xem transCode có trong đơn đặt hàng chưa.
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, -1);
                Date date = cal.getTime();
                if(date.getTime() > receiveGoods.getSalesTransDate().getTime()){
                	 return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_SALES_TRANS_DATE_IN_PAST));
                }
                //Kiểm tra xem đã đăng nhập chưa
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
                }
                //set data for Sales_Order
                SalesOrder salesOrder = new SalesOrder();
                //Lấy thông tin User
                User user = dataService.getRowById(receiveGoods.getCreatedUser(), User.class);

                //set thong tin công ty
                salesOrder.setCompanyId(user.getCompanys().getId());
                //Xét địa điểm POS DBH
                salesOrder.setPosId(receiveGoods.getPosId());
                //Lấy danh sách kho hàng của điểm bán hàng
                List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("poss.id", receiveGoods.getPosId()));

                if (salesStocks.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_NO_HAVE_STOCK, MsalesValidator.APP_POS_NO_HAVE_STOCK));
                }

                //Xử lí Goods cùng id và goodsUnitId khác nhau
//                ArrayList<Integer> arrayGoodsId = new ArrayList<Integer>();
//                for(int i = 0; i < receiveGoods.getStockGoods().size(); i++){
//                	boolean bool = true;
//                	for(int j = 0; j<i; j++){
//                		if(receiveGoods.getStockGoods().get(i).getId() == receiveGoods.getStockGoods().get(j).getId()){
//                			bool = false;
//                			break;
//                		}
//                	}
//                	if(bool){
//                		arrayGoodsId.add(receiveGoods.getStockGoods().get(i).getId());
//                	}
//                }
//                //Xử lí Goods
//                List<Goods> gList = new ArrayList<Goods>();
//                for(int i=0; i < arrayGoodsId.size(); i++){
//                	Goods goods = new Goods();
//                	goods.setId(arrayGoodsId.get(i));
//                	ParameterList parameterList = new ParameterList("goodss.id", arrayGoodsId.get(i));
//                	parameterList.setOrder("quantity", "asc");
//                	List<GoodsUnit> goodsUnits = dataService.getListOption(GoodsUnit.class, parameterList);
//                	if(goodsUnits.isEmpty()){
//                		 return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_GOODS_UNIT_NULL));
//                	}
//                	int quantity = 0;
//                	for(Goods sp : receiveGoods.getStockGoods()){
//                		if(sp.getId() == arrayGoodsId.get(i)){
//                			if(sp.getGoodsUnitId() == goodsUnits.get(0).getId()){
//                				goods.setGoodsUnitId(sp.getGoodsUnitId());
//                				goods.setPrice(sp.getPrice());
//                				quantity+= sp.getQuantity();
//                			}else{
//                				for(GoodsUnit goodsUnit: goodsUnits){
//                					if(sp.getGoodsUnitId() == goodsUnit.getId()){
//                						quantity+=sp.getQuantity()*goodsUnit.getQuantity();
//                					}
//                				}
//                			}
//                			
//                		}
//                	}
//                	if(goods.getGoodsUnitId() == null){
//                		goods.setGoodsUnitId(goodsUnits.get(0).getId());
//                		goods.setPrice(goodsUnits.get(0).getPrice());
//                	}
//                	goods.setQuantity(quantity);
//                	gList.add(goods);
//                }
                //set id kho hàng
                salesOrder.setStockId(salesStocks.get(0).getId());
                //set ngày giao dịch
                salesOrder.setSalesTransDate(receiveGoods.getSalesTransDate());
                //sét trạng thái giao dịch
                salesOrder.setStatusId(13);
                //Xét Note với đặt hàng qua điện thoại
                salesOrder.setNote("MOBILE");
                //người tạo
                salesOrder.setCreatedUser(user.getId());
                //xet TransCode
             //   salesOrder.setTransCode(transCode);

                //set data for Sales_Trans
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                int ret = 0;

                try {
                    //save SalesTrans
                    ret = dataService.insertRow(salesOrder);
                } catch (Exception e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }
                if (ret > 0) {
                    SalesOrderDetails salesTransDetails = new SalesOrderDetails();
                    for (Goods sp : stockGoods) {
                        //set property for SalesTransDetails
                        salesTransDetails.setGoodsId(sp.getId());
                        salesTransDetails.setGoodsUnitId(sp.getGoodsUnitId());
                        salesTransDetails.setOrderId(ret);
                        salesTransDetails.setQuantity(sp.getQuantity());
//                        ParameterList parameterList5 = new ParameterList("goodss.id", sp.getId());
//                        parameterList5.setOrder("quantity", "DESC");
//                        List<GoodsUnit> goodsUnits = dataService.getListOption(GoodsUnit.class, parameterList5);
//
//                        //Tính số tiền của mỗi order_details
//                        int total = 0;
//                        int quantity = sp.getQuantity();
//                        for (int i = 0; i < goodsUnits.size(); i++) {
//                            total = total + (quantity / goodsUnits.get(i).getQuantity()) * goodsUnits.get(i).getPrice();
//                            quantity = quantity % goodsUnits.get(i).getQuantity();
//                        }
                        salesTransDetails.setPrice(sp.getPrice());
                        salesTransDetails.setSalesTransDate(receiveGoods.getSalesTransDate());
                        salesTransDetails.setCreatedUser(user.getId());
                        Goods goods2 = dataService.getRowById(sp.getId(), Goods.class);
                        salesTransDetails.setIsFocus(goods2.getIsFocus());
                        //insert record in SalesOrderDetails
                        try {
                            dataService.insertRow(salesTransDetails);
                        } catch (Exception e) {
                            Exception ex = (Exception) e.getCause().getCause();
                            if (ex instanceof ConstraintViolationException) {
                                SalesOrder salesTrans2 = dataService.getRowById(ret, SalesOrder.class);
                                salesTrans2.setDeletedUser(user.getId());
                                salesTrans2.setDeletedAt(new Date());
                                //insert a Sale Stock Goods
                                dataService.updateRow(salesTrans2);
                                return MsalesJsonUtils.jsonValidate(ex);
                            }//else
                            SalesOrder salesTrans2 = dataService.getRowById(ret, SalesOrder.class);
                            salesTrans2.setDeletedUser(user.getId());
                            salesTrans2.setDeletedAt(new Date());
                            //insert a Sale Stock Goods
                            dataService.updateRow(salesTrans2);
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.UNKNOW));
                        }

                    }
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
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


}
