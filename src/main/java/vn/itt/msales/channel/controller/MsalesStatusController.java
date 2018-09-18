/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.channel.controller;

import java.util.ArrayList;
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
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.StatusType;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;

/**
 *
 * @author KhaiCH
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.STATUS.NAME)
public class MsalesStatusController extends CsbController {

    /**
     * create a Status
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.STATUS.ACTION_CREATE_STATUS, method = RequestMethod.POST)
    public @ResponseBody
    String createStatus(HttpServletRequest request) {

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Status status;
            try {
                //parse jsonString to a status Object
                status = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Status.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //status from json not null
            if (status != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (status.getStatusTypeId() != null) {
                    StatusType statusType = dataService.getRowById(status.getStatusTypeId(), StatusType.class);
                    //statusType is not exist
                    if (statusType == null) {
                        //return message warning StatusType is not exist in DB
                        hashErrors.put("StatusType","ID = " + status.getStatusTypeId() + " không tồn tại.");
                    }
                }
                if (status.getCreatedUser() != null) {
                    User user = dataService.getRowById(status.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + status.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save status to DB
                    int ret = dataService.insertRow(status);

                    //save Succcess
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
            } //status from json null
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
     * update a Status
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.STATUS.ACTION_UPDATE_STATUS, method = RequestMethod.POST)
    public @ResponseBody
    String updateStatus(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Status status;
            try {
                status = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Status.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //status from json not null
            if (status != null) {
                if (status.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (status.getStatusTypeId() != null) {
                    StatusType statusType = dataService.getRowById(status.getStatusTypeId(), StatusType.class);
                    //statusType is not exist
                    if (statusType == null) {
                        //return message warning StatusType is not exist in DB
                        hashErrors.put("StatusType","ID = " + status.getStatusTypeId() + " không tồn tại.");
                    }
                }
                if (status.getUpdatedUser()!= null) {
                    User user = dataService.getRowById(status.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + status.getUpdatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                int id = status.getId();

                try {
                    //update status to DB
                    int ret = dataService.updateSync(status);
                    //update success
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(Status.class, id);
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
                            .create(MsalesStatus.UNKNOW));
                }
            } //status from json null
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
     * delete a Status
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.STATUS.ACTION_DELETE_STATUS, method = RequestMethod.POST)
    public @ResponseBody
    String deleteStatus(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Status status;
            try {
                //parse jsonString to a status Object
                status = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Status.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //status from json not null
            if (status != null) {
                if (status.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                if (status.getDeletedUser() != null) {
                    User user = dataService.getRowById(status.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, status.getDeletedUser());
                    }
                }

                int id = status.getId();
                try {
                    //update delete status
                    int ret = dataService.deleteSynch(status);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(Status.class, id);
                    }
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
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }

            } //status from json null
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
     * get a Status info
     *
     * @param request is a HttpServletRequest
     * @return string json include Status info
     */
    @RequestMapping(value = MsalesConstants.MODULE.STATUS.ACTION_GET_STATUS, method = RequestMethod.POST)
    public @ResponseBody
    String getStatus(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Status status;
            try {
                //parse jsonString to a status Object
                status = MsalesJsonUtils.getObjectFromJSON(jsonString, Status.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //status from json not null
            if (status != null) {
                if (status.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = status.getId();
                //get status from DB
                status = dataService.getRowById(status.getId(),
                        Status.class);
                //status not null
                if (status != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, status));
                } //status null
                else {
                    return MsalesJsonUtils.notExists(Status.class, id);
                }
            } else {
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
     * get List all status
     *
     * @param request is a HttpServletRequest
     * @return string json include List status
     */
    @RequestMapping(value = MsalesConstants.MODULE.STATUS.ACTION_GET_LIST_STATUS, method = RequestMethod.POST)
    public @ResponseBody
    String getListStatus(HttpServletRequest request) {

        //get List status from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

        MsalesResults<Status> list = dataService.getListOption(Status.class, parameterList,true);

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));

    }

    /**
     * get List status by StatusType
     *
     * @param request is a HttpServletRequest
     * @return string json include List status
     */
    @RequestMapping(value = MsalesConstants.MODULE.STATUS.ACTION_GET_LIST_STATUS_BY_STATUSTYPE_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListStatusByStatusType(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            Status status;
            try {
                //parse jsonString to a Status Object
                status = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Status.class
                );
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //status from json not null
            if (status != null) {
                if (status.getStatusTypeId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("Status", "StatusTypeId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                //get List Status by statusType id
                //ParameterList parameterList = new ParameterList("statusTypes.id", status.getStatusTypeId(), page.getPageNo(), page.getRecordsInPage());
                ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());
                parameterList.add("statusTypes.id", 1);                
                MsalesResults<Status> list = dataService.getListOption(Status.class, parameterList,true);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));
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
     * DuanND: getCbListStatusByStatusTypeId
     * @param request is jsonString have statusTypeId
     * @return a jsonString have statuss
     */
    @RequestMapping(value = MsalesConstants.MODULE.STATUS.ACTION_GET_CB_LIST_STATUS_BY_STATUSTYPE_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListStatusByStatusTypeId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
       
        //jsonString not null
        if (jsonString != null) {
            Status status;
            try {
                //parse jsonString to a Status Object
                status = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Status.class
                );
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //status from json not null
            if (status != null) {
                if (status.getStatusTypeId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                    hashErrors.put("Status", "StatusTypeId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                //get List Status by statusType id
                ParameterList parameterList = new ParameterList("statusTypes.id", status.getStatusTypeId(), 0, 0);
                MsalesResults<Status> list = dataService.getListOption(Status.class, parameterList, true);
                String[] strings = {"value", "statusTypes"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list), strings);
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

}
