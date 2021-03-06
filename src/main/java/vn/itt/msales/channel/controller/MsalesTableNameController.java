/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.channel.controller;

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
import vn.itt.msales.entity.TableName;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;

/**
 *
 * @author KhaiCH
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.TABLENAME.NAME)
public class MsalesTableNameController extends CsbController {

    /**
     * create a TableName
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.TABLENAME.ACTION_CREATE_TABLENAME, method = RequestMethod.POST)
    public @ResponseBody
    String createTableName(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            TableName tableName;
            try {
                //parse jsonString to a TableName Object
                tableName = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        TableName.class);
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //tableName from json not null
            if (tableName != null) {
                try {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    if (tableName.getCreatedUser() != null) {
                        User user = dataService.getRowById(tableName.getCreatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", "ID = " + tableName.getCreatedUser() + " không tồn tại.");
                        }
                    }
                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    //save tableName to DB
                    int ret = dataService.insertRow(tableName);
                    //save tableName success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save tableName failed
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
            } //tableName from json null
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
     * update a TableName
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.TABLENAME.ACTION_UPDATE_TABLENAME, method = RequestMethod.POST)
    public @ResponseBody
    String updateTableName(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            TableName tableName;
            try {
                //parse jsonString to a TableName Object
                tableName = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        TableName.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //tableName from json not null
            if (tableName != null) {
                if (tableName.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (tableName.getUpdatedUser()!= null) {
                    User user = dataService.getRowById(tableName.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + tableName.getUpdatedUser()+ " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                int id = tableName.getId();
                try {
                    //update tableName to DB
                    int ret = dataService.updateSync(tableName);
                    //update success
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(TableName.class, id);
                    }
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

            } //tableName from json null
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
     * delete a TableName
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.TABLENAME.ACTION_DELETE_TABLENAME, method = RequestMethod.POST)
    public @ResponseBody
    String deleteTableName(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            TableName tableName;
            try {
                //parse jsonString to a TableName Object
                tableName = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        TableName.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //tableName from json not null
            if (tableName != null) {
                if (tableName.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                
                if (tableName.getDeletedUser()!= null) {
                    User user = dataService.getRowById(tableName.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, tableName.getDeletedUser());
                    }
                }

                
                int id = tableName.getId();
                try {
                    //delete tableName from DB
                    int ret = dataService.deleteSynch(tableName);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(TableName.class, id);
                    }
                    //update tableName success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update tableName failed
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
            } //tableName from json null
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
     * get a TableName info
     *
     * @param request is a HttpServletRequest
     * @return string json include Table info
     */
    @RequestMapping(value = MsalesConstants.MODULE.TABLENAME.ACTION_GET_TABLENAME, method = RequestMethod.POST)
    public @ResponseBody
    String getTableName(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            TableName tableName;
            try {
                //parse jsonString to a TableName Object
                tableName = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        TableName.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //tableName from json not null
            if (tableName != null) {
                if (tableName.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = tableName.getId();
                //get tableName from DB
                tableName = dataService.getRowById(tableName.getId(),
                        TableName.class);
                //tableName not null
                if (tableName != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, tableName));
                } //tableName null
                else {
                    return MsalesJsonUtils.notExists(TableName.class, id);
                }
            } //tableName from json null
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
     * get List all TableName
     *
     * @param request is a HttpServletRequest
     * @return string json include List TableName
     */
    @RequestMapping(value = MsalesConstants.MODULE.TABLENAME.ACTION_GET_LIST_TABLENAME, method = RequestMethod.POST)
    public @ResponseBody
    String getListTableName(HttpServletRequest request) {
        //get List TableName from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

        MsalesResults<TableName> list = dataService.getListOption(TableName.class, parameterList,true);

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));

    }
}
