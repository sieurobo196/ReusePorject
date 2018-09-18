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
import vn.itt.msales.entity.StatusType;
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
@RequestMapping(value = MsalesConstants.MODULE.STATUSTYPE.NAME)
public class MsalesStatusTypeController extends CsbController {

    /**
     * create a StatusType
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.STATUSTYPE.ACTION_CREATE_STATUSTYPE, method = RequestMethod.POST)
    public @ResponseBody
    String createStatusType(HttpServletRequest request) {
        //get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();

        //jsonString not null
        if (jsonString != null) {
            StatusType statusType;
            try {
                //parse jsonString to a statusType Object
                statusType = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        StatusType.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //statusType from json not null
            if (statusType != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (statusType.getCreatedUser() != null) {
                    User user = dataService.getRowById(statusType.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + statusType.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save statusType to DB
                    int ret = dataService.insertRow(statusType);
                    //save success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save failed
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
            } //statusType from json null
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
     * update a StatusType
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.STATUSTYPE.ACTION_UPDATE_STATUSTYPE, method = RequestMethod.POST)
    public @ResponseBody
    String updateStatusType(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            StatusType statusType;
            try {
                //parse jsonString to a statusType Object
                statusType = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        StatusType.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //statusType from json not null
            if (statusType != null) {
                if (statusType.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                int id = statusType.getId();
                try {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    if (statusType.getUpdatedUser() != null) {
                        User user = dataService.getRowById(statusType.getUpdatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", "ID = " + statusType.getUpdatedUser() + " không tồn tại.");
                        }
                    }
                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    //update statusType to DB
                    int ret = dataService.updateSync(statusType);
                    //Update success
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(StatusType.class, id);
                    } else if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //Update failed
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
            } //statusType from json null
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
     * delete a StatusType
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.STATUSTYPE.ACTION_DELETE_STATUSTYPE, method = RequestMethod.POST)
    public @ResponseBody
    String deleteStatusType(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            StatusType statusType;
            try {
                //parse jsonString to a statusType Object
                statusType = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        StatusType.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //statusType from json not null
            if (statusType != null) {
                if (statusType.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                if (statusType.getDeletedUser() != null) {
                    User user = dataService.getRowById(statusType.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, statusType.getDeletedUser());
                    }
                }

                int id = statusType.getId();
                try {
                    //delete statusType from DB
                    int ret = dataService.deleteSynch(statusType);
                    //delete statusType success
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(StatusType.class, id);
                    } else if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //statusType failed
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
            } //statusType from json null
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
     * get a StatusType info
     *
     * @param request is a HttpServletRequest
     * @return string json include StatusType info
     */
    @RequestMapping(value = MsalesConstants.MODULE.STATUSTYPE.ACTION_GET_STATUSTYPE, method = RequestMethod.POST)
    public @ResponseBody
    String getStatusType(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            StatusType statusType;
            try {
                //parse jsonString to a StatusType Object
                statusType = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        StatusType.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //statusType from json not null
            if (statusType != null) {
                if (statusType.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                int id = statusType.getId();
                //get statusType from DB
                statusType = dataService.getRowById(statusType.getId(),
                        StatusType.class);
                //statusType not null
                if (statusType != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, statusType));
                } //statusType null
                else {
                    return MsalesJsonUtils.notExists(StatusType.class, id);
                }
            } //statusType from json null
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
     * get List all StatusType
     *
     * @param request is a HttpServletRequest
     * @return string json include List StatusType
     */
    @RequestMapping(value = MsalesConstants.MODULE.STATUSTYPE.ACTION_GET_LIST_STATUSTYPE, method = RequestMethod.POST)
    public @ResponseBody
    String listStatusType(HttpServletRequest request) {
        //get List StatusType from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

        MsalesResults<StatusType> list = dataService.getListOption(StatusType.class, parameterList,true);
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));

    }
}
