/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.workflow.controller;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
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
import vn.itt.msales.entity.Workflow;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.WorkflowType;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.logex.MSalesException;

/**
 *
 * @author CuTX
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.WORKFLOW.NAME)
public class MsalesWorkflowController extends CsbController {

    public MsalesWorkflowController() {
    }

    /**
     * create a Workflow
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.WORKFLOW.ACTION_CREATE_WORKFLOW, method = RequestMethod.POST)
    public @ResponseBody
    String createWorkflow(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            Workflow workflow;
            try {
                //parse jsonString to a workflow Object
                workflow = MsalesJsonUtils.getObjectFromJSON(jsonString, Workflow.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //worfkflow from json not null
            if (workflow != null) {

                if (workflow.getWorkflowTypeId() != null && workflow.getCompanyId() != null) {
                    //get WorkflowType from workflow
                    WorkflowType workflowType = dataService.getRowById(workflow.getWorkflowTypeId(), WorkflowType.class);
                    //WorkflowType is not exist
                    if (workflowType == null) {
                        //return message warning WorkflowType is not exist in DB
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("WorkflowType",
                                "WorkflowType with ID = " + workflow.getWorkflowTypeId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    // get Company from workflow
                    Company company = dataService.getRowById(workflow.getCompanyId(), Company.class);
                    //company is not exist
                    if (company == null) {
                        //return message warning company is not exist in DB
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("Company",
                                "Company with ID = " + workflow.getCompanyId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }

                try {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    if (workflow.getCreatedUser() != null) {
                        User user = dataService.getRowById(workflow.getCreatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", "User with ID = " + workflow.getCreatedUser() + " không tồn tại.");
                        }
                    }
                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    //save workflow to DB
                    int ret = dataService.insertRow(workflow);

                    //save Succcess
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                    } //save failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_INSERT_FAIL));
                    }
                } catch (MSalesException e) {
                    Exception ex = (Exception) e.getCause().getCause();
//                    if (ex instanceof org.hibernate.exception.ConstraintViolationException) {
//                        org.hibernate.exception.ConstraintViolationException conViEx = (org.hibernate.exception.ConstraintViolationException) ex;
//                        // check duplicate user name
//                        if (conViEx.getCause() instanceof MySQLIntegrityConstraintViolationException) {
//                            // response to client the user request create is duplicate.
//                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
//                                    .create(MsalesStatus.USER_NAME_DUPLICATE));
//                        }
//                    } else 
                    if (ex instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }

                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));

                }

            } //workflow from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Delete a Workflow
     *
     * @param request jsonString contain goods_category_id to delete workflow
     * @return content, code and status
     */
    @RequestMapping(value = MsalesConstants.MODULE.WORKFLOW.ACTION_DELETE_WORKFLOW, method = RequestMethod.POST)
    public @ResponseBody
    String deleteWorkflow(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            Workflow workflow;
            try {
                //parse jsonString to a goodsseri Object
                workflow = MsalesJsonUtils.getObjectFromJSON(jsonString, Workflow.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //goodsSeri from json not null
            if (workflow != null) {
                if (workflow.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));

                }
                if (workflow.getDeletedUser() != null) {
                    User user = dataService.getRowById(workflow.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, workflow.getDeletedUser());
                    }
                }
                try {
                    //update delete goodsSeri
                    int ret = dataService.deleteSynch(workflow);
                    if (ret == -2) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("Workflow",
                                "Workflow with ID = " + workflow.getId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else //update delete goodsSeri success
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

            } //goodsSeri from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Update data to Workflow
     *
     * @param request is jsonString contain data to update
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.WORKFLOW.ACTION_UPDATE_WORKFLOW, method = RequestMethod.POST)
    public @ResponseBody
    String updateWorkflow(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            Workflow workflow;
            try {
                workflow = MsalesJsonUtils.getObjectFromJSON(jsonString, Workflow.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //workflow from json not null
            if (workflow != null) {
                if (workflow.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));

                }
                //get workflowtype from workflow
                if (workflow.getWorkflowTypeId() != null && workflow.getCompanyId() != null) {
                    WorkflowType workflowType = dataService.getRowById(workflow.getWorkflowTypeId(), WorkflowType.class);
                    if (workflowType == null) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("WorkflowType",
                                "WorkflowType with ID = " + workflow.getWorkflowTypeId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    //get company from workflow]
                    Company company = dataService.getRowById(workflow.getCompanyId(), Company.class);
                    if (company == null) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("Company",
                                "Company with ID = " + workflow.getWorkflowTypeId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                }
                int ret;
                try {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    if (workflow.getUpdatedUser() != null) {
                        User user = dataService.getRowById(workflow.getUpdatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", "User with ID = " + workflow.getUpdatedUser() + " không tồn tại.");
                        }
                    }
                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    //update workflow to DB
                    ret = dataService.updateSync(workflow);
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));
                }
                //update success
                if (ret == -2) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                    hashErrors.put("Workflow",
                            "workflow with ID = " + workflow.getId() + " không tồn tại trên Database");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                } else if (ret > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                } //update failed
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_UPDATE_FAIL));
                }

            } //goodsseri from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get List Workflow by CompanyId
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.WORKFLOW.ACTION_GET_LIST_WORKFLOW_BY_COMPANY_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListWorkflowByCompanyId(HttpServletRequest request) {
         String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null

        if (jsonString != null) {
            Workflow workflow;
            try {
                //parse jsonString to a Channel Object
                workflow = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Workflow.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Schedule from json not null
            if (workflow != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (workflow.getCompanyId()!= null) {
                    Company company = dataService.getRowById(workflow.getCompanyId(), Company.class);
                    if (company == null) {
                        hashErrors.put("Company",
                                "Company with ID = " + workflow.getCompanyId()+ " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    hashErrors.put("companyId", "  không được null!");
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List MCP by implementEmployeeId 
                ParameterList parameterList = new ParameterList("companys.id", workflow.getCompanyId(), page.getPageNo(), page.getRecordsInPage());

                MsalesResults<Workflow> list = dataService.getListOption(Workflow.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));

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
     * get Workflow by WorkflowType
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.WORKFLOW.ACTION_GET_LIST_WORKFLOW_BY_WORKFLOW_TYPE_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListWorkflowByWorkflowTypeId(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null

        if (jsonString != null) {
            Workflow workflow;
            try {
                //parse jsonString to a Channel Object
                workflow = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Workflow.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Schedule from json not null
            if (workflow != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (workflow.getCompanyId()!= null) {
                    Company company = dataService.getRowById(workflow.getCompanyId(), Company.class);
                    if (company == null) {
                        hashErrors.put("Company",
                                "Company with ID = " + workflow.getCompanyId()+ " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    hashErrors.put("companyId", "  không được null!");
                }
                if (workflow.getWorkflowTypeId()!= null) {
                    WorkflowType workflowType = dataService.getRowById(workflow.getWorkflowTypeId(), WorkflowType.class);
                    if (workflowType == null) {
                        hashErrors.put("WorkflowType",
                                "WorkflowType with ID = " + workflow.getWorkflowTypeId()+ " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    hashErrors.put("workflowTypeId", "  không được null!");
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List MCP by implementEmployeeId 
                ParameterList parameterList = new ParameterList("companys.id", workflow.getCompanyId(), page.getPageNo(), page.getRecordsInPage());
                parameterList.add("workflowTypes.id", workflow.getWorkflowTypeId());
                MsalesResults<Workflow> list = dataService.getListOption(Workflow.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));

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

}
