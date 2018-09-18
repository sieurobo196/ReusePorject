/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.workflow.controller;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
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
import vn.itt.msales.entity.WorkflowType;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
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
@RequestMapping(value = MsalesConstants.MODULE.WORKFLOW_TYPE.NAME)
public class MsalesWorkflowTypeController extends CsbController {

    /**
     * get a WorkflowType
     *
     * @param request is a HttpServletRequest
     * @return string json include WorkflowType info
     */
    @RequestMapping(value = MsalesConstants.MODULE.WORKFLOW_TYPE.ACTION_GET_WORKFLOW_TYPE, method = RequestMethod.POST)
    public @ResponseBody
    String getWorkflowType(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            WorkflowType workflowType;
            try {
                //parse jsonString to a WorkflowType Object
                workflowType = (WorkflowType) MsalesJsonUtils.getObjectFromJSON(jsonString, WorkflowType.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_INVALID));
            }
            //workflowType from json not null
            if (workflowType != null && workflowType.getId() != null) {

                //get workflowType from DB
                workflowType = dataService.getRowById(workflowType.getId(), WorkflowType.class);
                //workflowType not null
                if (workflowType != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, workflowType));
                } //workflowType null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                }
            } //workflowType from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * create a WorkflowType
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.WORKFLOW_TYPE.ACTION_CREATE_WORKFLOW_TYPE, method = RequestMethod.POST)
    public @ResponseBody
    String createWorkflowType(HttpServletRequest request) {
        //get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();

        //jsonString not null
        if (jsonString != null) {
            WorkflowType workflowType;
            try {
                //parse jsonString to a workflowType Object
                workflowType = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        WorkflowType.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //workflowType from json not null
            if (workflowType != null) {

                try {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    if (workflowType.getCreatedUser() != null) {
                        User user = dataService.getRowById(workflowType.getCreatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", "User with ID = " + workflowType.getCreatedUser() + " không tồn tại.");
                        }
                    }
                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    //save workflowType to DB
                    int ret = dataService.insertRow(workflowType);
                    //save success
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
                    if (ex instanceof org.hibernate.exception.ConstraintViolationException) {
                        org.hibernate.exception.ConstraintViolationException conViEx = (org.hibernate.exception.ConstraintViolationException) ex;
                        // check duplicate user name
                        if (conViEx.getCause() instanceof MySQLIntegrityConstraintViolationException) {
                            // response to client the user request create is duplicate.
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.ERROR_NAME_DUPLICATE));
                        }
                    } else if (ex instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }

                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(e.getMessage()));

                }

            } //workflowType from json null
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
     * Update data to WorkflowType
     *
     * @param request is jsonString contain data to update
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.WORKFLOW_TYPE.ACTION_UPDATE_WORKFLOW_TYPE, method = RequestMethod.POST)
    public @ResponseBody
    String updateWorkflowType(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            WorkflowType workflowType;
            try {
                //parse jsonString to a workflowType Object
                workflowType = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        WorkflowType.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //workflowType from json not null
            if (workflowType != null) {
                if (workflowType.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL));
                }
                int ret;
                try {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    if (workflowType.getUpdatedUser() != null) {
                        User user = dataService.getRowById(workflowType.getUpdatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", "User with ID = " + workflowType.getUpdatedUser() + " không tồn tại.");
                        }
                    }
                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    //update workflowType to DB
                    ret = dataService.updateSync(workflowType);
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }
                    if (e.getCause() instanceof MySQLIntegrityConstraintViolationException) {
                        // response to client the user request create is duplicate.
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.ERROR_NAME_DUPLICATE));
                    }
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }
                //Update success
                if (ret == -2) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                    hashErrors.put("WorkflowType",
                            "WorkflowType with ID = " + workflowType.getId() + " không tồn tại trên Database");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                } else if (ret > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                } //Update failed
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_UPDATE_FAIL));
                }

            } //workflowType from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Delete a WorkflowType
     *
     * @param request jsonString contain goods_category_id to delete
     * workflowType
     * @return content, code and status
     */
    @RequestMapping(value = MsalesConstants.MODULE.WORKFLOW_TYPE.ACTION_DELETE_WORKFLOW_TYPE, method = RequestMethod.POST)
    public @ResponseBody
    String deleteWorkflowType(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            WorkflowType workflowType;
            try {
                //parse jsonString to a WorkflowType Object
                workflowType = MsalesJsonUtils.getObjectFromJSON(jsonString, WorkflowType.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //WorkflowType from json not null
            if (workflowType != null) {
                if (workflowType.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
                }
                if (workflowType.getDeletedUser() != null) {
                    User user = dataService.getRowById(workflowType.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, workflowType.getDeletedUser());
                    }
                }
                try {
                    //delete WorkflowType from DB
                    int ret = dataService.deleteSynch(workflowType);
                    //delete WorkflowType success
                    if (ret == -2) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("WorkflowType",
                                "WorkflowType with ID = " + workflowType.getId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                    } //WorkflowType failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));
                }
            } //WorkflowType from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get List all WorkflowType
     *
     * @param request is a HttpServletRequest
     * @return string json include List WorkflowType
     */
    @RequestMapping(value = MsalesConstants.MODULE.WORKFLOW_TYPE.ACTION_GET_LIST_WORKFLOW_TYPE, method = RequestMethod.POST)
    public @ResponseBody
    String getListWorkflowType(HttpServletRequest request) {

        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

        MsalesResults<WorkflowType> list = dataService.getListOption(WorkflowType.class, parameterList,true);

       
            if (list == null ) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
            }
       

    }

}
