/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.sales.controller;

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
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsUnit;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.SalesTransDetails;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;

/**
 *
 * @author KhaiCH
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.SALESTRANSDETAILS.NAME)
public class MsalesSalesTransDetailsController extends CsbController {

    /**
     * get a SalesTransDetail info
     *
     * @param request is a HttpServletRequest
     * @return string json include SalesTransDetails info
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANSDETAILS.ACTION_GET_SALES_TRANS_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String getSalesTransDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTransDetails salesTransDetails;
            try {
                //parse jsonString to a SalesTransDetails Object
                salesTransDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTransDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesTransDetails from json not null
            if (salesTransDetails != null) {
                if (salesTransDetails.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = salesTransDetails.getId();

                //get SalesTransDetails from DB
                salesTransDetails = dataService.getRowById(salesTransDetails.getId(),
                        SalesTransDetails.class);
                //SalesTransDetails not null
                if (salesTransDetails != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, salesTransDetails));
                } //SalesTransDetails null
                else {
                    return MsalesJsonUtils.notExists(SalesTransDetails.class, id);
                }
            } //SalesTransDetails from json null
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
     * create a SalesTransDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANSDETAILS.ACTION_CREATE_SALES_TRANS_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String createSalesTransDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTransDetails salesTransDetails;
            try {
                //parse jsonString to a SalesTransDetails Object
                salesTransDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTransDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTransDetails from json not null
            if (salesTransDetails != null) {
                //validate info 
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (salesTransDetails.getGoodsId() != null) {
                    Goods goods = dataService.getRowById(salesTransDetails.getGoodsId(), Goods.class);
                    if (goods == null) {
                        hashErrors.put("Goods", "ID = " + salesTransDetails.getGoodsId() + " không tồn tại.");
                    }
                }
                if (salesTransDetails.getGoodsUnitId() != null) {
                    GoodsUnit goodsUnit = dataService.getRowById(salesTransDetails.getGoodsUnitId(), GoodsUnit.class);
                    if (goodsUnit == null) {
                        hashErrors.put("GoodsUnit", "ID = " + salesTransDetails.getGoodsUnitId() + " không tồn tại.");
                    }
                }
                if (salesTransDetails.getSalesTransId() != null) {
                    SalesTrans salesTrans = dataService.getRowById(salesTransDetails.getSalesTransId(), SalesTrans.class);
                    if (salesTrans == null) {
                        hashErrors.put("SalesTrans", "ID = " + salesTransDetails.getSalesTransId() + " không tồn tại.");
                    }
                }

                if (salesTransDetails.getCreatedUser() != null) {
                    User createdUser = dataService.getRowById(salesTransDetails.getCreatedUser(), User.class);
                    if (createdUser == null) {
                        hashErrors.put("User", "ID = " + salesTransDetails.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (salesTransDetails.getCreatedUser() != null) {
                    User user = dataService.getRowById(salesTransDetails.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + salesTransDetails.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save SalesTransDetails to DB
                    int ret = dataService.insertRow(salesTransDetails);

                    //save SalesTransDetails success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save SalesTransDetails failed
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
            } //SalesTransDetails from json null
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
     * update a SalesTransDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANSDETAILS.ACTION_UPDATE_SALES_TRANS_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String updateSalesTransDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTransDetails salesTransDetails;
            try {
                //parse jsonString to a SalesTransDetails Object
                salesTransDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTransDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesTransDetails from json not null
            if (salesTransDetails != null) {
                if (salesTransDetails.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = salesTransDetails.getId();
                //validate info
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (salesTransDetails.getGoodsId() != null) {
                    Goods goods = dataService.getRowById(salesTransDetails.getGoodsId(), Goods.class);
                    if (goods == null) {
                        hashErrors.put("Goods", "ID = " + salesTransDetails.getGoodsId() + " không tồn tại.");
                    }
                }
                if (salesTransDetails.getGoodsUnitId() != null) {
                    GoodsUnit goodsUnit = dataService.getRowById(salesTransDetails.getGoodsUnitId(), GoodsUnit.class);
                    if (goodsUnit == null) {
                        hashErrors.put("GoodsUnit", "ID = " + salesTransDetails.getGoodsUnitId() + " không tồn tại.");
                    }
                }
                if (salesTransDetails.getSalesTransId() != null) {
                    SalesTrans salesTrans = dataService.getRowById(salesTransDetails.getSalesTransId(), SalesTrans.class);
                    if (salesTrans == null) {
                        hashErrors.put("SalesTrans", "ID = " + salesTransDetails.getSalesTransId() + " không tồn tại.");
                    }
                }

                if (salesTransDetails.getUpdatedUser() != null) {
                    User user = dataService.getRowById(salesTransDetails.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + salesTransDetails.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                try {
                    //update SalesTransDetails to DB
                    int ret = dataService.updateSync(salesTransDetails);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(SalesTransDetails.class, id);
                    }
                    //update success
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
            } //SalesTransDetails from json null
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
     * delete a SalesTransDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANSDETAILS.ACTION_DELETE_SALES_TRANS_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String deleteSalesTransDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTransDetails salesTransDetails;
            try {
                //parse jsonString to a SalesTransDetails Object
                salesTransDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTransDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTransDetails from json not null
            if (salesTransDetails != null) {
                if (salesTransDetails.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = salesTransDetails.getId();
                try {
                    if (salesTransDetails.getDeletedUser() != null) {
                        User user = dataService.getRowById(salesTransDetails.getDeletedUser(), User.class);
                        if (user == null) {
                            return MsalesJsonUtils.notExists(User.class, salesTransDetails.getDeletedUser());
                        }
                    }
                    //delete SalesTransDetails from DB
                    int ret = dataService.deleteSynch(salesTransDetails);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(SalesTransDetails.class, id);
                    }
                    //update delete SalesTransDetails success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update delete SalesTransDetails failed
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

            } //SalesTransDetails from json null
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
     * get List SalesTransDetails by SalesTransId
     *
     * @param request is a HttpServletRequest
     * @return string json include List SalesTransDetails
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANSDETAILS.ACTION_GET_LIST_SALES_TRANS_DETAILS_BY_SALES_TRANS_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListSalesTransDetailsBySalesTransId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            SalesTransDetails salesTransDetails;
            try {
                //parse jsonString to a SalesTransDetails Object
                salesTransDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTransDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //SalesTransDetails from json not null
            if (salesTransDetails != null) {
                if (salesTransDetails.getSalesTransId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("SalesTransDetails", "salesTransId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                //get List SalesTransDetails by SalesTransId
                ParameterList parameterList = new ParameterList("salesTranss.id", salesTransDetails.getSalesTransId(), page.getPageNo(), page.getRecordsInPage());
                MsalesResults<SalesTransDetails> list = dataService.getListOption(SalesTransDetails.class, parameterList,true);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));

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
}
