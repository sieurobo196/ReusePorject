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
import vn.itt.msales.entity.SalesTransDetails;
import vn.itt.msales.entity.SalesTransSeri;
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
@RequestMapping(value = MsalesConstants.MODULE.SALESTRANSSERI.NAME)
public class MsalesSalesTransSeriController extends CsbController {

    /**
     * get a SalesTransSeri info
     *
     * @param request is a HttpServletRequest
     * @return string json include SalesTransSeri info
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANSSERI.ACTION_GET_SALES_TRANS_SERI, method = RequestMethod.POST)
    public @ResponseBody
    String getSalesTransSeri(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTransSeri salesTransSeri;
            try {
                //parse jsonString to a SalesTrans Object
                salesTransSeri = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTransSeri.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTransSeri from json not null
            if (salesTransSeri != null) {
                if (salesTransSeri.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = salesTransSeri.getId();
                //get SalesTransSeri from DB
                salesTransSeri = dataService.getRowById(salesTransSeri.getId(),
                        SalesTransSeri.class);
                //SalesTransSeri not null
                if (salesTransSeri != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, salesTransSeri));
                } //SalesTransSeri null
                else {
                    return MsalesJsonUtils.notExists(SalesTransSeri.class, id);
                }
            } //SalesTransSeri from json null
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
     * create a SalesTransSeri
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANSSERI.ACTION_CREATE_SALES_TRANS_SERI, method = RequestMethod.POST)
    public @ResponseBody
    String createSalesTransSeri(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTransSeri salesTransSeri;
            try {
                //parse jsonString to a SalesTransSeri Object
                salesTransSeri = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTransSeri.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTransSeri from json not null
            if (salesTransSeri != null) {
                //validate info                 
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (salesTransSeri.getSalesTransDetailsId() != null) {
                    SalesTransDetails salesTransDetails = dataService.getRowById(salesTransSeri.getSalesTransDetailsId(), SalesTransDetails.class);
                    if (salesTransDetails == null) {
                        hashErrors.put("SalesTransDetails", "ID = " + salesTransSeri.getSalesTransDetailsId() + " không tồn tại.");
                    }
                }
                if (salesTransSeri.getCreatedUser() != null) {
                    User user = dataService.getRowById(salesTransSeri.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + salesTransSeri.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save SalesTransSeri to DB
                    int ret = dataService.insertRow(salesTransSeri);

                    //save SalesTransSeri success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save SalesTransSeri failed
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
            } //SalesTransSeri from json null
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
     * update a SalesTransSeri
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANSSERI.ACTION_UPDATE_SALES_TRANS_SERI, method = RequestMethod.POST)
    public @ResponseBody
    String updateSalesTransSeri(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTransSeri salesTransSeri;
            try {
                //parse jsonString to a SalesTransSeri Object
                salesTransSeri = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTransSeri.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTransSeri from json not null
            if (salesTransSeri != null) {
                if (salesTransSeri.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                int id = salesTransSeri.getId();
                //validate
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (salesTransSeri.getSalesTransDetailsId() != null) {
                    SalesTransDetails salesTransDetails = dataService.getRowById(salesTransSeri.getSalesTransDetailsId(), SalesTransDetails.class);
                    if (salesTransDetails == null) {
                        hashErrors.put("SalesTransDetails", "ID = " + salesTransSeri.getSalesTransDetailsId() + " không tồn tại.");
                    }
                }

                if (salesTransSeri.getUpdatedUser() != null) {
                    User user = dataService.getRowById(salesTransSeri.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + salesTransSeri.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //update SalesTransSeri to DB
                    int ret = dataService.updateSync(salesTransSeri);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(SalesTransSeri.class, id);
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
            } //SalesTransSeri from json null
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
     * delete a SalesTransSeri
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANSSERI.ACTION_DELETE_SALES_TRANS_SERI, method = RequestMethod.POST)
    public @ResponseBody
    String deleteSalesTransSeri(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SalesTransSeri salesTransSeri;
            try {
                //parse jsonString to a SalesTransSeri Object
                salesTransSeri = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTransSeri.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTransSeri from json not null
            if (salesTransSeri != null) {
                if (salesTransSeri.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = salesTransSeri.getId();
                try {
                    if (salesTransSeri.getDeletedUser() != null) {
                        User user = dataService.getRowById(salesTransSeri.getDeletedUser(), User.class);
                        if (user == null) {
                            return MsalesJsonUtils.notExists(User.class, salesTransSeri.getDeletedUser());
                        }
                    }
                    //delete SalesTransSeri from DB
                    int ret = dataService.deleteSynch(salesTransSeri);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(SalesTransSeri.class, id);
                    }
                    //update delete SalesTransSeri success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update delete SalesTransSeri failed
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
            } //SalesTransSeri from json null
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
     * get List SalesTransSeri by SalesTransDetailsId
     *
     * @param request is a HttpServletRequest
     * @return string json include List SalesTransSeri
     */
    @RequestMapping(value = MsalesConstants.MODULE.SALESTRANSSERI.ACTION_GET_LIST_SALES_TRANS_SERI_BY_SALES_TRANS_DETAILS_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListSalesTransSeriBySalesTransDetailsId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            SalesTransSeri salesTransSeri;
            try {
                //parse jsonString to a SalesTransSeri Object
                salesTransSeri = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SalesTransSeri.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SalesTransSeri from json not null
            if (salesTransSeri != null) {
                if (salesTransSeri.getSalesTransDetailsId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("SalesTransSeri", "salesTransDetailsId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                //get List SalesTransSeri by SalesTransDetailsId                
                ParameterList parameterList = new ParameterList("salesTransDetailss.id", salesTransSeri.getSalesTransDetailsId(), page.getPageNo(), page.getRecordsInPage());
                MsalesResults<SalesTransSeri> list = dataService.getListOption(SalesTransSeri.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));

            } //SalesTransSeri from json null
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
