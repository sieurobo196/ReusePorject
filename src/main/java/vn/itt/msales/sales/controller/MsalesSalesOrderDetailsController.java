/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.sales.controller;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

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
import vn.itt.msales.entity.SalesOrderDetails;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsUnit;
import vn.itt.msales.entity.SalesOrder;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.logex.MSalesException;

/**
 *
 * @author Cutx
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER_DETAILS.NAME)
public class MsalesSalesOrderDetailsController extends CsbController {

    /**
     * get a SalesOrderDetails info
     *
     * @param request is a HttpServletRequest
     * @return string json include SalesOrderDetails info
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER_DETAILS.ACTION_GET_SALES_ORDER_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String getSalesOrderDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SalesOrderDetails salesOrderDetails;
            try {
                //parse jsonString to a SalesOrderDetails Object
                salesOrderDetails = (SalesOrderDetails) MsalesJsonUtils.getObjectFromJSON(jsonString, SalesOrderDetails.class);
             }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesOrderDetails from json not null
            if (salesOrderDetails != null) {
                //SalesOrderDetails from json with correct Id
                if (salesOrderDetails.getId() >= 0) {
                    //get SalesOrderDetails from DB
                    salesOrderDetails = (SalesOrderDetails) dataService.getRowById(salesOrderDetails.getId(), SalesOrderDetails.class);
                    //SalesOrderDetails not null
                    if (salesOrderDetails != null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, salesOrderDetails));
                    } //SalesOrderDetails null
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                    }
                } //SalesOrderDetails from json with incorrect Id 
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NULL, "ID Null"));
                }
            } //SalesOrderDetails from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }
    }

    /**
     * create a SalesOrderDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER_DETAILS.ACTION_CREATE_SALES_ORDER_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String createSalesOrderDetails(HttpServletRequest request) {

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SalesOrderDetails salesOrderDetails;
            try {
                //parse jsonString to a SalesOrderDetails Object
                salesOrderDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesOrderDetails.class);
             }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesOrderDetails from json not null
            if (salesOrderDetails != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (salesOrderDetails.getOrderId() != null) {
                    //get SalesOrder from SalesOrderDetails
                    SalesOrder salesOrder = dataService.getRowById(salesOrderDetails.getOrderId(), SalesOrder.class);
                    //SalesOrder is not exist
                    if (salesOrder == null) {
                        //return message warning SalesOrder is not exist in DB
                        hashErrors.put("salesOrder",
                                "salesOrder with ID = " + salesOrderDetails.getGoodsId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (salesOrderDetails.getGoodsId() != null) {
                    //get Goods from SaleOrderDetails
                    Goods goods = dataService.getRowById(salesOrderDetails.getGoodsId(), Goods.class);
                    //Goods is not exist
                    if (goods == null) {
                        //return message warning Goods is not exist in DB
                        hashErrors.put("Goods",
                                "Goods with ID = " + salesOrderDetails.getGoodsId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (salesOrderDetails.getGoodsUnitId() != null) {

                    //get GoodsUnit from SalesorderDetails
                    GoodsUnit goodsUnit = dataService.getRowById(salesOrderDetails.getGoodsUnitId(), GoodsUnit.class);
                    //GoodsUnit is not exist
                    if (goodsUnit == null) {
                        //return message warning GoodsUnit is not exist in DB
                        hashErrors.put("GoodsUnit",
                                "GoodsUnit with ID = " + salesOrderDetails.getGoodsUnitId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                //check validate user create
                if (salesOrderDetails.getCreatedUser() != null) {
                    User user = dataService.getRowById(salesOrderDetails.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + salesOrderDetails.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                try {

                    //save SalesOrderDetails to DB
                    int ret = dataService.insertRow(salesOrderDetails);

                    //save Succcess
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_INSERT_FAIL));
                    }
                } catch (MSalesException e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }

                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(e.getMessage()));

                }

            } //SalesOrderDetails from json null
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
     * update a SalesOrderDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER_DETAILS.ACTION_UPDATE_SALES_ORDER_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String updateSalesOrderDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            SalesOrderDetails salesOrderDetails;
            try {
                salesOrderDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesOrderDetails.class);
              }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesOrderDetails from json not null
            if (salesOrderDetails != null) {
                if (salesOrderDetails.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL));

                }
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (salesOrderDetails.getOrderId() != null) {
                    //get SalesOrder from SalesOrderDetails
                    SalesOrder salesOrder = dataService.getRowById(salesOrderDetails.getOrderId(), SalesOrder.class);
                    //SalesOrder is not exist
                    if (salesOrder == null) {
                        //return message warning SalesOrder is not exist in DB
                        hashErrors.put("salesOrder",
                                "salesOrder with ID = " + salesOrderDetails.getGoodsId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (salesOrderDetails.getGoodsId() != null) {
                    //get Goods from SaleOrderDetails
                    Goods goods = dataService.getRowById(salesOrderDetails.getGoodsId(), Goods.class);
                    //Goods is not exist
                    if (goods == null) {
                        //return message warning Goods is not exist in DB
                        hashErrors.put("Goods",
                                "Goods with ID = " + salesOrderDetails.getGoodsId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (salesOrderDetails.getGoodsUnitId() != null) {
                    //get GoodsUnit from SalesorderDetails
                    GoodsUnit goodsUnit = dataService.getRowById(salesOrderDetails.getGoodsUnitId(), GoodsUnit.class);
                    //GoodsUnit is not exist
                    if (goodsUnit == null) {
                        //return message warning GoodsUnit is not exist in DB
                        hashErrors.put("GoodsUnit",
                                "GoodsUnit with ID = " + salesOrderDetails.getGoodsUnitId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }

                if (salesOrderDetails.getUpdatedUser() != null) {
                    User user = dataService.getRowById(salesOrderDetails.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + salesOrderDetails.getUpdatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {

                    //update SalesorderDetails to DB
                    int ret = dataService.updateSync(salesOrderDetails);

                    //update success
                    if (ret == -2) {
                        hashErrors.put("SalesOrderDetails",
                                "SalesOrderDetails with ID = " + salesOrderDetails.getId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else if (ret > 0) {
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
                            .create(e.getMessage()));
                }
            } //SalesorderDetails from json null
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
     * delete a SalesOrderDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER_DETAILS.ACTION_DELETE_SALES_ORDER_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String deleteSalesOrderDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            SalesOrderDetails salesOrderDetails;
            try {
                //parse jsonString to a SalesorderDetails Object
                salesOrderDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesOrderDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesorderDetails from json not null
            if (salesOrderDetails != null) {
                if (salesOrderDetails.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL));

                }
                if (salesOrderDetails.getDeletedUser() != null) {
                    User user = dataService.getRowById(salesOrderDetails.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, salesOrderDetails.getDeletedUser());
                    }
                }
                try {
                    //update delete SalesorderDetails
                    int ret = dataService.deleteSynch(salesOrderDetails);
                    if (ret == -2) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("SalesOrderDetails",
                                "SalesOrderDetails with ID = " + salesOrderDetails.getId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else //update delete SalesorderDetails success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update delete failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));
                }

            } //SalesorderDetails from json null
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
     * get List SalesorderDetails by OrderId
     *
     * @param request is a HttpServletRequest
     * @return string json include List SalesorderDetails
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER_DETAILS.ACTION_GET_LIST_SALES_ORDER_DETAILS_BY_ORDER_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListSalesOrderDetailsByOrderId(HttpServletRequest request) {
       String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null

        if (jsonString != null) {
            SalesOrderDetails salesOrderDetails;
            try {
                //parse jsonString to a Channel Object
                salesOrderDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesOrderDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Schedule from json not null
            if (salesOrderDetails != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (salesOrderDetails.getOrderId()!= null) {
                    SalesOrder salesOrder = dataService.getRowById(salesOrderDetails.getOrderId(), SalesOrder.class);
                    if (salesOrder == null) {
                        hashErrors.put("SalesOrder",
                                "SalesOrder with ID = " + salesOrderDetails.getOrderId()+ " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    hashErrors.put("orderId", "  không được null!");
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List MCP by implementEmployeeId 
                ParameterList parameterList = new ParameterList("orders.id", salesOrderDetails.getOrderId(), page.getPageNo(), page.getRecordsInPage());

                MsalesResults<SalesOrderDetails> list = dataService.getListOption(SalesOrderDetails.class, parameterList,true);
                String[] strings={"statusId","pos"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list),strings);

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

    @RequestMapping(value = MsalesConstants.MODULE.SALES_ORDER_DETAILS.ACTION_UPDATE_LIST_SALES_ORDER_DETAILS, method = RequestMethod.POST)
    public @ResponseBody String updateListSalesOrderDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            SalesOrderDetails salesOrderDetails;
            try {
                salesOrderDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesOrderDetails.class);
              }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesOrderDetails from json not null
            if (salesOrderDetails != null) {
            	//list error
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                //userUpdate not exist
                if (salesOrderDetails.getUpdatedUser() != null) {
                    User user = dataService.getRowById(salesOrderDetails.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User với ID = " + salesOrderDetails.getUpdatedUser() + " không tồn tại.");
                    }
                }
                List<SalesOrderDetails> salesOrderDetailss = salesOrderDetails.getSalesOrderDetails();
                if(salesOrderDetailss == null){
                	hashErrors.put("SalesOrderDetails", "Danh sách chi tiết đặt hàng(salesOrderDetails) bắt buộc nhập");
                }
                //check id
                for(SalesOrderDetails sOrderDetails : salesOrderDetailss){
                	if(sOrderDetails.getId() == null){
                		hashErrors.put("SalesOrderDetails", "Id là bắt buộc nhập");
                	}else{
                		SalesOrderDetails salesOrderDetails2 = dataService.getRowById(sOrderDetails.getId(), SalesOrderDetails.class);
                		if(salesOrderDetails2 == null){
                			hashErrors.put("SalesOrderDetails", "Id = "+ sOrderDetails.getId()+" không tồn tại");
                		}
                	}
                }
                
                //
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_VALUE_INVALID, hashErrors));
                }

                for(SalesOrderDetails sOrderDetails : salesOrderDetailss){
                	SalesOrderDetails salesOrderDetails2 = dataService.getRowById(sOrderDetails.getId(), SalesOrderDetails.class);
                	salesOrderDetails2.setQuantity(sOrderDetails.getQuantity());
                	salesOrderDetails2.setUpdatedUser(salesOrderDetails.getUpdatedUser());
                	salesOrderDetails2.setUpdatedAt(new Date());
                	dataService.updateRow(salesOrderDetails2);
                }
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, null));
            } //SalesorderDetails from json null
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
