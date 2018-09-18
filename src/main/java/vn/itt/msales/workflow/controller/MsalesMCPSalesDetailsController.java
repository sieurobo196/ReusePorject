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
import vn.itt.msales.entity.MCPSalesDetails;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Goods;
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
@RequestMapping(value = MsalesConstants.MODULE.MCP_SALES_DETAILS.NAME)
public class MsalesMCPSalesDetailsController extends CsbController {

    /**
     * create a MCPSalesDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP_SALES_DETAILS.ACTION_CREATE_MCP_SALES_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String createMCPSalesDetails(HttpServletRequest request) {

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            MCPSalesDetails mcpSalesDetails;
            try {
                //parse jsonString to a goodsseri Object
                mcpSalesDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, MCPSalesDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //goodsseri from json not null
            if (mcpSalesDetails != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (mcpSalesDetails.getGoodsId() != null) {
                    //get goods from mcpSalesDetails
                    Goods goods = dataService.getRowById(mcpSalesDetails.getGoodsId(), Goods.class);
                    //goods is not exist
                    if (goods == null) {
                        //return message warning goods is not exist in DB
                        hashErrors.put("Goods",
                                "Goods with ID = " + mcpSalesDetails.getGoodsId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpSalesDetails.getMcpId() != null) {
                    // get mcp from mcpSalesDetails
                    MCP mcp = dataService.getRowById(mcpSalesDetails.getMcpId(), MCP.class);
                    //goods is not exist
                    if (mcp == null) {
                        //return message warning goods is not exist in DB
                        hashErrors.put("MCP",
                                "MCP with ID = " + mcpSalesDetails.getMcpId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpSalesDetails.getUnitId() != null) {
                    // get Unit from mcpSalesDetails
                    Unit unit = dataService.getRowById(mcpSalesDetails.getUnitId(), Unit.class);
                    //goods is not exist
                    if (unit == null) {
                        //return message warning goods is not exist in DB
                        hashErrors.put("Unit",
                                "Unit with ID = " + mcpSalesDetails.getUnitId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpSalesDetails.getStatusId() != null) {
                    //get Status from sheduleSalesDetails
                    Status status = dataService.getRowById(mcpSalesDetails.getStatusId(), Status.class);
                    //goods is not exist
                    if (status == null) {
                        //return message warning goods is not exist in DB
                        hashErrors.put("Status",
                                "Status with ID = " + mcpSalesDetails.getStatusId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }

                try {
                    if (mcpSalesDetails.getCreatedUser() != null) {
                        User user = dataService.getRowById(mcpSalesDetails.getCreatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", "User with ID = " + mcpSalesDetails.getCreatedUser() + " không tồn tại.");
                        }
                    }
                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    //save goodsseri to DB
                    int ret = dataService.insertRow(mcpSalesDetails);

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

                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(e.getMessage()));

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
     * update a MCPSalesDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP_SALES_DETAILS.ACTION_UPDATE_MCP_SALES_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String updateMCPSalesDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            MCPSalesDetails mcpSalesDetails;

            try {
                mcpSalesDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, MCPSalesDetails.class
                );
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //goodsseri from json not null
            if (mcpSalesDetails != null) {
                if (mcpSalesDetails.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));

                }
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (mcpSalesDetails.getGoodsId() != null) {
                    //get goods from mcpSalesDetails
                    Goods goods = dataService.getRowById(mcpSalesDetails.getGoodsId(), Goods.class);
                    //goods is not exist
                    if (goods == null) {
                        //return message warning goods is not exist in DB
                        hashErrors.put("Goods",
                                "Goods with ID = " + mcpSalesDetails.getGoodsId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpSalesDetails.getMcpId() != null) {
                    // get mcp from mcpSalesDetails
                    MCP mcp = dataService.getRowById(mcpSalesDetails.getMcpId(), MCP.class);
                    //goods is not exist
                    if (mcp == null) {
                        //return message warning goods is not exist in DB
                        hashErrors.put("MCP",
                                "MCP with ID = " + mcpSalesDetails.getMcpId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpSalesDetails.getUnitId() != null) {
                    // get Unit from mcpSalesDetails
                    Unit unit = dataService.getRowById(mcpSalesDetails.getUnitId(), Unit.class);
                    //goods is not exist
                    if (unit == null) {
                        //return message warning goods is not exist in DB
                        hashErrors.put("Unit",
                                "Unit with ID = " + mcpSalesDetails.getUnitId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcpSalesDetails.getStatusId() != null) {
                    //get Status from sheduleSalesDetails
                    Status status = dataService.getRowById(mcpSalesDetails.getStatusId(), Status.class);
                    //goods is not exist
                    if (status == null) {
                        //return message warning goods is not exist in DB
                        hashErrors.put("Status",
                                "Status with ID = " + mcpSalesDetails.getStatusId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }

                try {

                    if (mcpSalesDetails.getUpdatedUser() != null) {
                        User user = dataService.getRowById(mcpSalesDetails.getUpdatedUser(), User.class
                        );
                        if (user
                                == null) {
                            hashErrors.put("User", "User with ID = " + mcpSalesDetails.getUpdatedUser() + " không tồn tại.");
                        }
                    }
                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    //update goodseri to DB
                    int ret = dataService.updateSync(mcpSalesDetails);

                    //update success
                    if (ret == -2) {
                        hashErrors.put("MCPSalesDetails",
                                "MCPSalesDetails with ID = " + mcpSalesDetails.getId() + " không tồn tại trên Database");
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
     * delete a MCPSalesDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP_SALES_DETAILS.ACTION_DELETE_MCP_SALES_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String deleteMCPSalesDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            MCPSalesDetails mcpSalesDetails;

            try {
                //parse jsonString to a goodsseri Object
                mcpSalesDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, MCPSalesDetails.class
                );
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //mcpSalesDetails from json not null
            if (mcpSalesDetails != null) {
                if (mcpSalesDetails.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));

                }
                if (mcpSalesDetails.getDeletedUser() != null) {
                    User user = dataService.getRowById(mcpSalesDetails.getDeletedUser(), User.class
                    );
                    if (user
                            == null) {
                        return MsalesJsonUtils.notExists(User.class, mcpSalesDetails.getDeletedUser());
                    }
                }
                try {
                    //update delete mcpSalesDetails
                    int ret = dataService.deleteSynch(mcpSalesDetails);
                    if (ret == -2) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("MCPSalesDetails",
                                "MCPSalesDetails with ID = " + mcpSalesDetails.getId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else //update delete mcpSalesDetails success
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

            } //mcpSalesDetails from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get List mcpSalesDetails by Goods
     *
     * @param request is a HttpServletRequest
     * @return string json include List mcpSalesDetails
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP_SALES_DETAILS.ACTION_GET_LIST_MCP_SALES_DETAILS_BY_MCP_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListMCPSalesDetails(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null

        if (jsonString != null) {
            MCPSalesDetails mcpSalesDetails;

            try {
                //parse jsonString to a Channel Object
                mcpSalesDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        MCPSalesDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Schedule from json not null
            if (mcpSalesDetails != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();

                if (mcpSalesDetails.getMcpId() != null) {
                    MCP mcp = dataService.getRowById(mcpSalesDetails.getMcpId(), MCP.class
                    );
                    if (mcp
                            == null) {
                        hashErrors.put("MCP",
                                "MCP with ID = " + mcpSalesDetails.getMcpId() + " không tồn tại trên Database");
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
                ParameterList parameterList = new ParameterList("mcps.id", mcpSalesDetails.getMcpId(), page.getPageNo(), page.getRecordsInPage());

                MsalesResults<MCPSalesDetails> list = dataService.getListOption(MCPSalesDetails.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));

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
