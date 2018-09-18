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
import vn.itt.msales.entity.MCPDetails;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.MCPSalesDetails;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.logex.MSalesException;

;

/**
 *
 * @author Cutx
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.MCP_DETAILS.NAME)
public class MsalesMCPDetailsController extends CsbController {

    /**
     * create a MCPDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP_DETAILS.ACTION_CREATE_MCP_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String createMCPDetails(HttpServletRequest request) {

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            MCPDetails mcpDetails;
            try {
                //parse jsonString to a Channel Object
                mcpDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        MCPDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //mcpDetails from json not null
            if (mcpDetails != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (mcpDetails.getMcpId() != null) {
                    //get mcp from mcpDetails
                    MCP mcp = dataService.getRowById(mcpDetails.getMcpId(), MCP.class);
                    //goods is not exist
                    if (mcp == null) {
                        //return message warning mcp is not exist in DB
                        hashErrors.put("MCP",
                                "MCP with ID = " + mcpDetails.getMcpId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpDetails.getPosId() != null) {

                    //get POS from mcpDetails
                    POS pos = dataService.getRowById(mcpDetails.getPosId(), POS.class);
                    //pos is not exist
                    if (pos == null) {
                        //return message warning pos is not exist in DB
                        hashErrors.put("POS",
                                "POS with ID = " + mcpDetails.getPosId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpDetails.getImplementEmployeeId() != null) {
                    //get implementEmployeeId from mcpDetails
                    User implementEmployee = dataService.getRowById(mcpDetails.getImplementEmployeeId(), User.class);
                    //implementEmployee is not exist
                    if (implementEmployee == null) {
                        //return message warning implementEmployee is not exist in DB
                        hashErrors.put("implementEmployee",
                                "implementEmployee with ID = " + mcpDetails.getImplementEmployeeId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpDetails.getLocationId() != null) {

                    // get location from mcpDetails
                    Location location = dataService.getRowById(mcpDetails.getLocationId(), Location.class);
                    //location is not exist
                    if (location == null) {
                        //return message warning location is not exist in DB
                        hashErrors.put("Location",
                                "Location with ID = " + mcpDetails.getLocationId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpDetails.getStatusId() != null) {
                    // get Status from mcpDetails
                    Status status = dataService.getRowById(mcpDetails.getStatusId(), Status.class);
                    //status is not exist
                    if (status == null) {
                        //return message warning status is not exist in DB
                        hashErrors.put("Status",
                                "Status with ID = " + mcpDetails.getStatusId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                // check user create
                if (mcpDetails.getCreatedUser() != null) {
                    User user = dataService.getRowById(mcpDetails.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + mcpDetails.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                try {

                    //save mcpDetails to DB
                    int ret = dataService.insertRow(mcpDetails);

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

            } //goodsseri from json null
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
     * update a MCPDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP_DETAILS.ACTION_UPDATE_MCP_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String updateMCPDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            MCPDetails mcpDetails;
            try {
                mcpDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, MCPDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //mcpDetails from json not null
            if (mcpDetails != null) {
                if (mcpDetails.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL));

                }
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (mcpDetails.getMcpId() != null) {
                    //get mcp from mcpDetails
                    MCP mcp = dataService.getRowById(mcpDetails.getMcpId(), MCP.class);
                    //goods is not exist
                    if (mcp == null) {
                        //return message warning mcp is not exist in DB
                        hashErrors.put("MCP",
                                "MCP with ID = " + mcpDetails.getMcpId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpDetails.getPosId() != null) {

                    //get POS from mcpDetails
                    POS pos = dataService.getRowById(mcpDetails.getPosId(), POS.class);
                    //pos is not exist
                    if (pos == null) {
                        //return message warning pos is not exist in DB
                        hashErrors.put("POS",
                                "POS with ID = " + mcpDetails.getPosId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpDetails.getImplementEmployeeId() != null) {
                    //get implementEmployeeId from mcpDetails
                    User implementEmployee = dataService.getRowById(mcpDetails.getImplementEmployeeId(), User.class);
                    //implementEmployee is not exist
                    if (implementEmployee == null) {
                        //return message warning implementEmployee is not exist in DB
                        hashErrors.put("implementEmployee",
                                "implementEmployee with ID = " + mcpDetails.getImplementEmployeeId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpDetails.getLocationId() != null) {

                    // get location from mcpDetails
                    Location location = dataService.getRowById(mcpDetails.getLocationId(), Location.class);
                    //location is not exist
                    if (location == null) {
                        //return message warning location is not exist in DB
                        hashErrors.put("Location",
                                "Location with ID = " + mcpDetails.getLocationId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpDetails.getStatusId() != null) {
                    // get Status from mcpDetails
                    Status status = dataService.getRowById(mcpDetails.getStatusId(), Status.class);
                    //status is not exist
                    if (status == null) {
                        //return message warning status is not exist in DB
                        hashErrors.put("Status",
                                "Status with ID = " + mcpDetails.getStatusId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                //check user update
                if (mcpDetails.getUpdatedUser() != null) {
                    User user = dataService.getRowById(mcpDetails.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + mcpDetails.getUpdatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {

                    //update mcpDetails to DB
                    int ret = dataService.updateSync(mcpDetails);

                    //update success
                    if (ret == -2) {
                        hashErrors.put("MCPDetails",
                                "MCPDetails with ID = " + mcpDetails.getId() + " không tồn tại trên Database");
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
            } //mcpDetails from json null
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
     * delete a MCPDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP_DETAILS.ACTION_DELETE_MCP_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String deleteMCPDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            MCPDetails mcpDetails;
            try {
                //parse jsonString to a mcpDetails Object
                mcpDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, MCPDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //mcpDetails from json not null
            if (mcpDetails != null) {
                //check ID
                if (mcpDetails.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL));

                }
                // check User delete
                if (mcpDetails.getDeletedUser() != null) {
                    User user = dataService.getRowById(mcpDetails.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, mcpDetails.getDeletedUser());
                    }
                }
                try {
                    //update delete mcpDetails
                    int ret = dataService.deleteSynch(mcpDetails);
                    if (ret == -2) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("ScheduleCustomerCareDetails",
                                "ScheduleCustomerCareDetails with ID = " + mcpDetails.getId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else //update delete mcpDetails success
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

            } //mcpDetails from json null
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
     * get List mcpDetails by Goods
     *
     * @param request is a HttpServletRequest
     * @return string json include List mcpDetails
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP_DETAILS.ACTION_GET_LIST_MCP_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String getListMCPDetails(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null

        if (jsonString != null) {
            MCPDetails mcpDetails;
            try {
                //parse jsonString to a Channel Object
                mcpDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        MCPDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Schedule from json not null
            if (mcpDetails != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (mcpDetails.getMcpId() != null) {
                    MCP mcp = dataService.getRowById(mcpDetails.getMcpId(), MCP.class);
                    if (mcp == null) {
                        hashErrors.put("MCP",
                                "MCP with ID = " + mcpDetails.getMcpId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    hashErrors.put("mcpId", "  không được null!");
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List MCP by implementEmployeeId 
                ParameterList parameterList = new ParameterList("mcps.id", mcpDetails.getMcpId(), page.getPageNo(), page.getRecordsInPage());

                MsalesResults<MCPDetails> list = dataService.getListOption(MCPDetails.class, parameterList,true);
                String [] strings={"posCode",
                    "ownerCode",
                    "ownerCodeDate",
                    "ownerCodeLocation"};
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

}
