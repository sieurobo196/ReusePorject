/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.workflow.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.MCPDetails;
import vn.itt.msales.entity.MCPSalesDetails;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.SalesTransDetails;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.logex.MSalesException;

/**
 *
 * @author cutx
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.MCP.NAME)
public class MsalesMCPController extends CsbController {

    public MsalesMCPController() {
    }

    /**
     * get a MCP info
     *
     * @param request is a HttpServletRequest
     * @return string json include MCP info
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_GET_MCP, method = RequestMethod.POST)
    public @ResponseBody
    String getMCP(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            MCP mcp;
            try {
                //parse jsonString to a mcp Object
                mcp = (MCP) MsalesJsonUtils.getObjectFromJSON(jsonString, MCP.class);
            } //jsonString syntax incorrect //jsonString syntax incorrect //jsonString syntax incorrect //jsonString syntax incorrect
            catch (Exception ex) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.JSON_INVALID));
            }
            //mcp from json not null
            if (mcp != null && mcp.getId() != null) {

                //get mcp from DB
                mcp = dataService.getRowById(mcp.getId(), MCP.class);
                //mcp not null
                if (mcp != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, mcp));
                } //mcp null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                }
            } //mcp from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.JSON_ID_NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * create a MCP
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_CREATE_MCP, method = RequestMethod.POST)
    public @ResponseBody
    String createMCP(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            MCP mcp;
            try {
                //parse jsonString to a mcp Object
                mcp = MsalesJsonUtils.getObjectFromJSON(jsonString, MCP.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //mcp from json not null
            if (mcp != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (mcp.getImplementEmployeeId() != null) {
                    //get ImplementEmployee from mcp
                    User implementEmployee = dataService.getRowById(mcp.getImplementEmployeeId(), User.class);
                    //implementEmployee is not exist
                    if (implementEmployee == null) {
                        //return message warning implementEmployee is not exist in DB
                        hashErrors.put("implementEmployee",
                                "implementEmployee with ID = " + mcp.getImplementEmployeeId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcp.getStatusId() != null) {

                    // get status from mcp
                    Status status = dataService.getRowById(mcp.getStatusId(), Status.class);
                    //status is not exist
                    if (status == null) {
                        //return message warning status is not exist in DB
                        hashErrors.put("status",
                                "status with ID = " + mcp.getStatusId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }

                if (mcp.getCreatedUser() != null) {
                    User user = dataService.getRowById(mcp.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + mcp.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                try {

                    //save status to DB
                    int ret = dataService.insertRow(mcp);

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
            } //mcp from json null
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
     * Delete a MCP
     *
     * @param request jsonString contain goods_category_id to delete mcp
     * @return content, code and status
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_DELETE_MCP, method = RequestMethod.POST)
    public @ResponseBody  String deleteMCP(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            MCP mcp;
            try {
                //parse jsonString to a MCP Object
                mcp = MsalesJsonUtils.getObjectFromJSON(jsonString, MCP.class);
              }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //Schedule from json not null
            if (mcp != null) {
                if (mcp.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL));

                }
                if (mcp.getDeletedUser() != null) {
                    User user = dataService.getRowById(mcp.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, mcp.getDeletedUser());
                    }
                }
                try {
                    //update delete MCP
                    int ret = dataService.deleteSynch(mcp);
                    if (ret == -2) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("Schedule",
                                "Schedule with ID = " + mcp.getId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else //update delete MCP success
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
                            .create(e.getMessage()));
                }

            } //Schedule from json null
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
     * Update data to MCP
     *
     * @param request is jsonString contain data to update
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_UPDATE_MCP, method = RequestMethod.POST)
    public @ResponseBody
    String updateMCP(HttpServletRequest request) {

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            MCP mcp;
            try {
                //parse jsonString to a customerCareInformation Object
                mcp = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        MCP.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CustomerCareInformation from json not null
            if (mcp != null) {
                // check id
                if (mcp.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL));

                }
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();

                if (mcp.getImplementEmployeeId() != null) {
                    // get ImplementEmployee from customerCareInformation
                    User user = dataService.getRowById(mcp.getImplementEmployeeId(), User.class);
                    //ImplementEmployee is not exist
                    if (user == null) {
                        //return message warning ImplementEmployee is not exist in DB
                        hashErrors.put("ImplementEmployee",
                                "ImplementEmployee with ID = " + mcp.getImplementEmployeeId() + "không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (mcp.getStatusId() != null) {
                    // get mcp from CustomerCareInformation
                    Status status = dataService.getRowById(mcp.getStatusId(), Status.class);
                    //mcp is not exist
                    if (status == null) {
                        //return message warning mcp is not exist in DB
                        hashErrors.put("Status",
                                "status with ID = " + mcp.getStatusId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                //check validate user update
                if (mcp.getUpdatedUser() != null) {
                    User user = dataService.getRowById(mcp.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + mcp.getUpdatedUser() + "không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {

                    //update CustomerCareInformation to DB
                    int ret = dataService.updateSync(mcp);

                    //update success
                    if (ret == -2) {
                        hashErrors.put("MCP",
                                "MCP with ID = " + mcp.getId() + " không tồn tại trên Database");
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
            } //CustomerCareInformation from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils
                    .getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

    }

    /**
     * get List MCP By ImplementEmployeeId
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_GET_LIST_MCP_BY_IMPLEMENT_EMPLOYEE_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListMCPByImplementEmployeeId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null

        if (jsonString != null) {
            MCP mcp;
            try {
                //parse jsonString to a Channel Object
                mcp = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        MCP.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Schedule from json not null
            if (mcp != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (mcp.getImplementEmployeeId() != null) {
                    User implementEmployee = dataService.getRowById(mcp.getImplementEmployeeId(), User.class);
                    if (implementEmployee == null) {
                        hashErrors.put("implementEmployee",
                                "implementEmployee with ID = " + mcp.getImplementEmployeeId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    hashErrors.put("implementEmployeeId", "  không được null!");
                }
//                if (mcp.getBeginDate() == null) {
//                    hashErrors.put("BeginDate", "  không được null!");
//                }
//                if (mcp.getFinishTime() == null) {
//                    hashErrors.put("FinishDate", " không được null!");
//                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List MCP by implementEmployeeId 
                ParameterList parameterList = new ParameterList("implementEmployees.id", mcp.getImplementEmployeeId(), page.getPageNo(), page.getRecordsInPage());
//                parameterList.add("beginDate", mcp.getBeginDate(), ">=");
//                parameterList.add("finishTime", mcp.getFinishTime(), "<=");

                MsalesResults<MCP> list = dataService.getListOption(MCP.class, parameterList, true);

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
     * get MCP_Administrator
     *	DuanND
     * @param request is a jsonString have ID to get info
     * @return a jsonString include info of MCP
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_GET_MCP_ADMINISTRATOR, method = RequestMethod.POST)
    public @ResponseBody
    String getMCPAdministrator(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            MCP mcp;
            try {
                //parse jsonString to a mcp Object
                mcp = (MCP) MsalesJsonUtils.getObjectFromJSON(jsonString, MCP.class);
            } //jsonString syntax incorrect //jsonString syntax incorrect //jsonString syntax incorrect //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }            //mcp from json not null
            if (mcp != null) {
                if (mcp.getId() != null) {
                    //get mcp from DB
                    MCP mcp2 = dataService.getRowById(mcp.getId(), MCP.class);
                    //mcp not null
                    if (mcp2 != null) {

                        ParameterList parameterList = new ParameterList("mcps.id", mcp.getId());
                        List<MCPSalesDetails> list = dataService.getListOption(MCPSalesDetails.class, parameterList);
//                        for(MCPSalesDetails mSalesDetails : list){
//                        	mSalesDetails.setGoodsId(mSalesDetails.getGoodss().getId());
//                        	mSalesDetails.setUnitId(mSalesDetails.getUnits().getId());
//                        	mSalesDetails.setStatusId(mSalesDetails.getStatuss().getId());
//                        }
                        mcp2.setMcpSalesDetailss(list);
                        String[] strings = {"statusType"};
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, mcp2), strings);
                    } //mcp null
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE,MsalesValidator.MCP_ID_NOT_EXIST + mcp.getId() ));
                    }
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL));
                }

            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Search MCP
     * DuanND
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_SEARCH_MCP, method = RequestMethod.POST)
    public @ResponseBody String searchMCPSales(HttpServletRequest request) {
        //get List GoodsCategory from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            LinkedHashMap<String, Object> searchObject;
            try {
                //parse jsonString to a search Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        LinkedHashMap.class);
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            
            String hql = "Select U from MCP as U where type = 2 and deletedUser = 0 ";
            String hql2 = "Select U.id as id from MCP as U where type = 2 and deletedUser = 0 ";
            if (searchObject != null) {
               //Check from Date, toDate
               SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
               SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy");
               if (searchObject.get("fromDate") != null) {
                   String fromDate = searchObject.get("fromDate").toString();
                   if (fromDate != null && !fromDate.isEmpty() && !fromDate.trim().isEmpty() && !fromDate.equals("")) {
                       try {
                           Date date1 = sDateFormat.parse(fromDate);
                           String fromDate2 = simpleDateFormat.format(date1);
                           Date date2 = simpleDateFormat.parse(fromDate2);
                           //parameterList.add("salesTransDate", date1, ">=");
                           hql += " and beginDate >= '" + simpleDateFormat.format(date2)+"'";
                           hql2 += " and beginDate >= '" + simpleDateFormat.format(date2)+"'";
                       } catch (ParseException e) {
                           // TODO Auto-generated catch block
                     	  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                           
                       }
                   }
               }
               
               if (searchObject.get("toDate") != null) {
                   String toDate = searchObject.get("toDate").toString();
                   if (toDate != null && !toDate.isEmpty() && !toDate.trim().isEmpty() && !toDate.equals("")) {
                       try {
                     	  Date date1 = sDateFormat.parse(toDate);
                           String toDate2 = simpleDateFormat.format(date1);
                           Date date2 = simpleDateFormat.parse(toDate2);
                           Calendar calendar = Calendar.getInstance();
                           calendar.setTime(date2);
                           calendar.add(calendar.DAY_OF_MONTH, 1);
                           //parameterList.add("salesTransDate", date2, "<=");
                           hql += " and beginDate <= '" + simpleDateFormat.format(calendar.getTime())+"'";
                           hql2 += " and beginDate <= '" + simpleDateFormat.format(calendar.getTime())+"'";
                       } catch (ParseException e) {
                           // TODO Auto-generated catch block
                     	  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                       }
                   }
               }
               // 
               //check createdUser
               if(searchObject.get("searchText") != null){
             	  String key = searchObject.get("searchText").toString();
             	  if(key != null && !key.isEmpty() && !key.trim().isEmpty() && !key.equals("")){
             		  hql += " and ( note LIKE '%" + key + "%'" + " or name LIKE '%" + key + "%'" + " ) ";
             		 hql2 += " and ( note LIKE '%" + key + "%'" + " or name LIKE '%" + key + "%'" + " ) ";
             	  }
               }
               if(searchObject.get("implementEmployeeId") != null){
             	  try{
                 	  int createdUser = Integer.parseInt(searchObject.get("implementEmployeeId").toString());
                 	  if(createdUser != 0){
                 		 hql += " and implementEmployees.id = " + createdUser;
                 		 hql2 += " and implementEmployees.id = " + createdUser;
                 	  }else{
                 		  if(searchObject.get("employerList") != null){
                 			  ArrayList<LinkedHashMap<String, Object>> employerList = (ArrayList<LinkedHashMap<String, Object>>) searchObject.get("employerList");
                 				  String string = "";
                 				  if(employerList.size() > 1){
                 					  for(int i = 1; i < employerList.size(); i++){
                     					  int id = Integer.parseInt(employerList.get(i).get("id").toString());
                     					  if(id != 0){
                                           		  string += " or implementEmployees.id = " + id;
                     					  }else{
                     						  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                     					  }
                     				  }
                     				  //Xét hql
                     				  hql += " and ( " + string.substring(3) + ") ";
                     				 hql2 += " and ( " + string.substring(3) + ") ";
                 				  }else{
                 					  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                 			}
                 		  }
                 	  }
             	  }catch(Exception ex){
             		  if (ex instanceof ConstraintViolationException) {
                           return MsalesJsonUtils.jsonValidate(ex);
                       }//else
                       return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, ex.getMessage()));
             	  }
               }
               
            }
            
            List<MCP> count = new ArrayList<MCP>();
            List<MCP> lists = new ArrayList<MCP>();
            hql += " order by beginDate desc";
            try{
            	count = dataService.executeSelectHQL(MCP.class, hql2, true, 0, 0);
                lists = dataService.executeSelectHQL(MCP.class, hql, false, page.getPageNo(), page.getRecordsInPage());
            }catch(Exception ex){
            	return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, ex.getMessage()));
            }
            //Cập nhật thông tin cho list.
            for(MCP mcp : lists){
            	//Xét tên cho nhân viên
            //	mcp.getImplementEmployees().setName(mcp.getImplementEmployees().getLastName() + " " + mcp.getImplementEmployees().getFirstName());
            	//Xét tỉnh thành cho nhân viên
            	if(mcp.getImplementEmployees().getLocations().getLocationType() == 1){
            		mcp.getImplementEmployees().setLocation(mcp.getImplementEmployees().getLocations().getName());
        		}else if(mcp.getImplementEmployees().getLocations().getParents().getLocationType() == 1){
        			mcp.getImplementEmployees().setLocation(mcp.getImplementEmployees().getLocations().getParents().getName());
        		}else{
        			mcp.getImplementEmployees().setLocation(mcp.getImplementEmployees().getLocations().getParents().getParents().getName());
        		}
            	//Xét Người lập
            	int createdUserId = mcp.getCreatedUser();
            	User user = dataService.getRowById(createdUserId, User.class);
            	if(user != null){
            		mcp.setNameAdmin(user.getLastName() + " " + user.getFirstName());
            	}else{
            		mcp.setNameAdmin("Admin");
            	}
            	
            }
            MsalesResults<MCP> listMCPs = new MsalesResults<MCP>();
            listMCPs.setCount(Long.parseLong(count.size()+""));
            listMCPs.setContentList(lists);
            String[] strings = {"type","updatedAt","isActive","statusTypes","note"};
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listMCPs), strings);
        
        }else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * DuanND: Create_MCP_Details_Administrator
     *
     * @param request is jsonString have data to createMCPDetailsAdministrator
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_CREATE_MCP_DETAILS_ADMINISTRATOR, method = RequestMethod.POST)
    public @ResponseBody
    String createMCPAndDetailsAdministrator(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            MCP mcp;

            try {
                //parse jsonString to a mcp Object
                mcp = MsalesJsonUtils.getObjectFromJSON(jsonString, MCP.class);
            } catch (Exception e) {
                if (e instanceof ConstraintViolationException) {
                    return MsalesJsonUtils.jsonValidate(e);
                }//else
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
            }
            //mcp from json not null
            if (mcp != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                List<MCPDetails> mcpSalesDetailss = mcp.getMcpDetailss();
                if (mcpSalesDetailss != null && !mcpSalesDetailss.isEmpty()) {
                    for (MCPDetails mSalesDetails : mcpSalesDetailss) {
                        if (mSalesDetails != null) {
                            if (mSalesDetails.getImplementEmployeeId() != null) {
                                User goods = dataService.getRowById(mSalesDetails.getImplementEmployeeId(), User.class);
                                if (goods == null) {
                                    hashErrors.put("implementEmployee", MsalesValidator.MCP_USER_NOT_EXIST + mSalesDetails.getImplementEmployeeId());
                                }
                            }
                            if (mSalesDetails.getPosId() != null) {
                                POS unit = dataService.getRowById(mSalesDetails.getPosId(), POS.class);
                                //goods is not exist
                                if (unit == null) {
                                    hashErrors.put("POS", MsalesValidator.MCP_POS_NOT_EXIST + mSalesDetails.getPosId());
                                }
                                Location location = dataService.getRowById(unit.getLocations().getId(), Location.class);
                                mSalesDetails.setLocations(location);

                            }
                            if (mSalesDetails.getStatusId() != null) {
                                Status status = dataService.getRowById(mSalesDetails.getStatusId(), Status.class);
                                if (status == null) {
                                    hashErrors.put("Status", MsalesValidator.MCP_STATUS_NOT_EXIST + mSalesDetails.getStatusId());
                                }
                            }
                            mSalesDetails.setCreatedUser(mcp.getCreatedUser());
                        } else {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL, "Chi tiết chăm sóc khách hàng cần nhập"));
                        }
                    }
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, MsalesValidator.MCP_MCP_DETAILS_NULL));
                }
                if (mcp.getImplementEmployeeId() != null) {
                    //get ImplementEmployee from mcp
                    User implementEmployee = dataService.getRowById(mcp.getImplementEmployeeId(), User.class);
                    //implementEmployee is not exist
                    if (implementEmployee == null) {
                        //return message warning implementEmployee is not exist in DB
                        hashErrors.put("implementEmployee", MsalesValidator.MCP_USER_NOT_EXIST + mcp.getImplementEmployeeId());
                    }
                }
                if (mcp.getStatusId() != null) {

                    // get status from mcp
                    Status status = dataService.getRowById(mcp.getStatusId(), Status.class);
                    //status is not exist
                    if (status == null) {
                        //return message warning status is not exist in DB
                        hashErrors.put("status", MsalesValidator.MCP_STATUS_NOT_EXIST + mcp.getStatusId());
                    }
                }
                if (mcp.getType() != null && mcp.getType() != 1) {
                    hashErrors.put("MCP", MsalesValidator.MCP_SEARCH_MCP__TYPE_IS_1);
                }
                if (mcp.getCreatedUser() != null) {
                    User user = dataService.getRowById(mcp.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", MsalesValidator.MCP_USER_NOT_EXIST + mcp.getCreatedUser());
                    }
                }
                if (hashErrors.size() > 0) {
                    // return message warning tableName or property is not exist
                    // in DB
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));

                }

                int ret1 = 0;
                int ret2 = 0;
                try {
                    //save status to DB
                    ret1 = dataService.insertRow(mcp);
                } catch (MSalesException e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }
                }

                if (ret1 > 0) {
                    for (MCPDetails mcpSalesDetails : mcpSalesDetailss) {
                        mcpSalesDetails.setMcpId(ret1);
//                    		POS pos = dataService.getRowById(mcpSalesDetails.getPosId(), POS.class);
//                    		Location location = dataService.getRowById(pos.getLocations().getId(), Location.class);
//                    		mcpSalesDetails.setLocations(location);
//                    		mcpSalesDetails.setCreatedUser(mcp.getCreatedUser());
                        try {
                            //save status to DB
                            ret2 = dataService.insertRow(mcpSalesDetails);
                        } catch (MSalesException e) {
                            Exception ex = (Exception) e.getCause().getCause();
                            if (ex instanceof javax.validation.ConstraintViolationException) {
                                return MsalesJsonUtils.jsonValidate(ex);
                            }
                        }
                    }
                }
                //save Succcess
                if (ret2 > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, null));
                } //save failed
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
            } //mcp from json null
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
     * DuanND: create MCP_Sales_Details_Administrator
     *
     * @param request is jsonString have data to create
     * MCP_Sales_Details_Administrator
     * @return a jsonString have code, status and contents
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_CREATE_MCP_SALES_DETAILS_ADMINISTRATOR, method = RequestMethod.POST)
    public @ResponseBody
    String createMCPAndSalesDetailsAdministrator(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            MCP mcp;

            try {
                //parse jsonString to a mcp Object
                mcp = MsalesJsonUtils.getObjectFromJSON(jsonString, MCP.class);
            } catch (Exception e) {
                if (e instanceof ConstraintViolationException) {
                    return MsalesJsonUtils.jsonValidate(e);
                }//else
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
            }
            //mcp from json not null
            if (mcp != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                List<MCPSalesDetails> mcpSalesDetailss = mcp.getMcpSalesDetailss();
                if (mcpSalesDetailss != null) {
                    for (MCPSalesDetails mSalesDetails : mcpSalesDetailss) {
                        if (mSalesDetails != null) {
                            if (mSalesDetails.getGoodsId() != null) {
                                Goods goods = dataService.getRowById(mSalesDetails.getGoodsId(), Goods.class);
                                if (goods == null) {
                                    hashErrors.put("Goods", MsalesValidator.MCP_GOODS_ID_NOT_EXIST + mSalesDetails.getGoodsId());
                                }
                            }
                            if (mSalesDetails.getUnitId() != null) {
                                Unit unit = dataService.getRowById(mSalesDetails.getUnitId(), Unit.class);
                                //goods is not exist
                                if (unit == null) {
                                    hashErrors.put("Unit",MsalesValidator.MCP_UNIT_ID_NOT_EXIST + mSalesDetails.getUnitId());
                                }
                            }
                            if (mSalesDetails.getStatusId() != null) {
                                Status status = dataService.getRowById(mSalesDetails.getStatusId(), Status.class);
                                if (status == null) {
                                    hashErrors.put("Status",MsalesValidator.MCP_STATUS_NOT_EXIST + mSalesDetails.getStatusId());
                                }
                            }
                        } else {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
                        }
                    }
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.
                            create(MsalesStatus.JSON_FIELD_REQUIRED, MsalesValidator.MCP_MCP_SALES_DETAILS_NULL));
                }
                if (mcp.getImplementEmployeeId() != null) {
                    //get ImplementEmployee from mcp
                    User implementEmployee = dataService.getRowById(mcp.getImplementEmployeeId(), User.class);
                    //implementEmployee is not exist
                    if (implementEmployee == null) {
                        //return message warning implementEmployee is not exist in DB
                        hashErrors.put("implementEmployee", MsalesValidator.MCP_USER_NOT_EXIST + mcp.getImplementEmployeeId());
                    }
                }
                if (mcp.getStatusId() != null) {

                    // get status from mcp
                    Status status = dataService.getRowById(mcp.getStatusId(), Status.class);
                    //status is not exist
                    if (status == null) {
                        //return message warning status is not exist in DB
                        hashErrors.put("status", MsalesValidator.MCP_STATUS_NOT_EXIST + mcp.getStatusId());
                    }
                }
                if (mcp.getType() != null && mcp.getType() != 2) {
                    hashErrors.put("MCP", MsalesValidator.MCP_SEARCH_MCP_AND_DETAILS_TYPE_IS_2);
                }
                if (mcp.getCreatedUser() != null) {
                    User user = dataService.getRowById(mcp.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", MsalesValidator.MCP_USER_NOT_EXIST + mcp.getCreatedUser());
                    }
                }
                if (hashErrors.size() > 0) {
                    // return message warning tableName or property is not exist
                    // in DB
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));

                }

                int ret1 = 0;
                int ret2 = 0;
                try {
                    //save status to DB
                    ret1 = dataService.insertRow(mcp);
                } catch (MSalesException e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }
                }

                if (ret1 > 0) {
                    for (MCPSalesDetails mcpSalesDetails : mcpSalesDetailss) {
                        mcpSalesDetails.setMcpId(ret1);
                        mcpSalesDetails.setCreatedUser(mcp.getCreatedUser());
                        try {
                            //save status to DB
                            ret2 = dataService.insertRow(mcpSalesDetails);
                        } catch (MSalesException e) {
                            Exception ex = (Exception) e.getCause().getCause();
                            if (ex instanceof javax.validation.ConstraintViolationException) {
                                return MsalesJsonUtils.jsonValidate(ex);
                            }
                        }
                    }
                }
                //save Succcess
                if (ret2 > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, null));
                } //save failed
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
            } //mcp from json null
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
     * DuanND updateMCPDSalesDetailsAdministrator
     *
     * @param request is jsonString have data to update
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_UPDATE_MCP_SALES_DETAILS_ADMINISTRATOR, method = RequestMethod.POST)
    public @ResponseBody
    String updateMCPSalesDetailsAdministrator(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            MCP mcp;

            try {
                //parse jsonString to a mcp Object
                mcp = MsalesJsonUtils.getObjectFromJSON(jsonString, MCP.class);
            } catch (Exception e) {
                if (e instanceof ConstraintViolationException) {
                    return MsalesJsonUtils.jsonValidate(e);
                }//else
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
            }
            //mcp from json not null
            if (mcp != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (mcp.getId() != null) {
                    MCP mcp2 = dataService.getRowById(mcp.getId(), MCP.class);
                    if (mcp2 == null) {
                        hashErrors.put("MCP", MsalesValidator.MCP_MCP_ID_NOT_EXIST + mcp.getId());
                    }

                    List<MCPSalesDetails> mcpSalesDetailss = mcp.getMcpSalesDetailss();
                    if (mcpSalesDetailss != null) {
                        for (MCPSalesDetails mSalesDetails : mcpSalesDetailss) {
                            if (mSalesDetails != null) {
                                if (mSalesDetails.getId() != null) {
                                    if (mSalesDetails.getGoodsId() != null) {
                                        Goods goods = dataService.getRowById(mSalesDetails.getGoodsId(), Goods.class);
                                        if (goods == null) {
                                            hashErrors.put("Goods", MsalesValidator.MCP_GOODS_ID_NOT_EXIST + mSalesDetails.getGoodsId());
                                        }
                                    }
                                    if (mSalesDetails.getUnitId() != null) {
                                        Unit unit = dataService.getRowById(mSalesDetails.getUnitId(), Unit.class);
                                        //goods is not exist
                                        if (unit == null) {
                                            hashErrors.put("Unit", MsalesValidator.MCP_UNIT_ID_NOT_EXIST + mSalesDetails.getUnitId());
                                        }
                                    }
                                    if (mSalesDetails.getStatusId() != null) {
                                        Status status = dataService.getRowById(mSalesDetails.getStatusId(), Status.class);
                                        if (status == null) {
                                            hashErrors.put("Status", MsalesValidator.MCP_STATUS_NOT_EXIST + mSalesDetails.getStatusId());
                                        }
                                    }
                                } else {
                                    hashErrors.put("MCPSalesDetails", MsalesValidator.MCP_MCP_SALES_DETAILS_NULL);
                                }
                            } else {
                                hashErrors.put("MCPSalesDetails", MsalesValidator.MCP_MCP_SALES_DETAILS_NULL);
                            }
                        }
                    } else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.
                                create(MsalesStatus.JSON_FIELD_REQUIRED, MsalesValidator.MCP_MCP_SALES_DETAILS_NULL));
                    }
                    if (mcp.getImplementEmployeeId() != null) {
                        //get ImplementEmployee from mcp
                        User implementEmployee = dataService.getRowById(mcp.getImplementEmployeeId(), User.class);
                        //implementEmployee is not exist
                        if (implementEmployee == null) {
                            //return message warning implementEmployee is not exist in DB
                            hashErrors.put("implementEmployee", MsalesValidator.MCP_USER_NOT_EXIST + mcp.getImplementEmployeeId());
                        }
                    }
                    if (mcp.getStatusId() != null) {

                        // get status from mcp
                        Status status = dataService.getRowById(mcp.getStatusId(), Status.class);
                        //status is not exist
                        if (status == null) {
                            //return message warning status is not exist in DB
                            hashErrors.put("status", MsalesValidator.MCP_STATUS_NOT_EXIST + mcp.getStatusId());
                        }
                    }
                    if (mcp.getType() != null && mcp.getType() != 2) {
                        hashErrors.put("MCP", MsalesValidator.MCP_SEARCH_MCP_AND_DETAILS_TYPE_IS_2);
                    }
                    if (mcp.getUpdatedUser() != null) {
                        User user = dataService.getRowById(mcp.getUpdatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", MsalesValidator.MCP_USER_NOT_EXIST + mcp.getUpdatedUser());
                        }
                    }
                    if (hashErrors.size() > 0) {
                        // return message warning tableName or property is not exist
                        // in DB
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));

                    }

                    int ret1 = 0;
                    int ret2 = 0;
                    try {
                        //save status to DB
                        ret1 = dataService.updateSync(mcp);
                    } catch (Exception e) {
                        if (e instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(e);
                        }
                    }

                    if (ret1 > 0) {
                        for (MCPSalesDetails mcpSalesDetails : mcpSalesDetailss) {
                            mcpSalesDetails.setMcpId(mcp.getId());
                            mcpSalesDetails.setUpdatedUser(mcp.getUpdatedUser());
                            try {
                                //save status to DB
                                ret2 = dataService.updateSync(mcpSalesDetails);
                            } catch (Exception e) {
                                if (e instanceof ConstraintViolationException) {
                                    return MsalesJsonUtils.jsonValidate(e);
                                }
                            }
                        }
                    }
                    //save Succcess
                    if (ret2 > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_UPDATE_FAIL));
                    }
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL));

                }
                //mcp from json null
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
     * DuanND sửa
     * CuTX get MCP Customer Care Details Admin
     * @param request
     * @return 
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_GET_MCP_AND_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String getMCPAndDetailsAdmin(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            MCP mcp;
            try {
                //parse jsonString to a mcp Object
                mcp = (MCP) MsalesJsonUtils.getObjectFromJSON(jsonString, MCP.class);
            } //jsonString syntax incorrect //jsonString syntax incorrect //jsonString syntax incorrect //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }            //mcp from json not null
            if (mcp != null) {
                if (mcp.getId() != null) {
                    //get mcp from DB
                    mcp = dataService.getRowById(mcp.getId(), MCP.class);
                    //mcp not null
                    if (mcp != null) {
                        mcp.getImplementEmployees().setName(mcp.getImplementEmployees().getLastName()+ " " + mcp.getImplementEmployees().getFirstName());

                        //Tìm người tạo
                        int createdUserId = mcp.getCreatedUser();
                    	User user = dataService.getRowById(createdUserId, User.class);
                    	if(user != null){
                    		mcp.setNameAdmin(user.getLastName() + " " + user.getFirstName());
                    	}else{
                    		mcp.setNameAdmin("Admin");
                    	}
                    	//Tìm người updated
                    	int updatedUserId = mcp.getUpdatedUser();
                    	if(updatedUserId > 0){
                    		User user2 = dataService.getRowById(updatedUserId, User.class);
                    		if(user2 != null){
                    			mcp.setNameUpdatedUser(user2.getLastName() + " " + user2.getFirstName());
                    		}else{
                    			mcp.setNameUpdatedUser("Admin");
                    		}
                    	}
                    	//Check số điểm bán hàng
                    	 //Xử lí thời điểm hiện tại
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        //Lưu thời điểm hiện tại.
                        Date dateCurrent = cal.getTime();
                        //Lưu thời điểm ngày đầu tiên của tháng
                        cal.set(Calendar.DAY_OF_MONTH,1);
                        Date date2 = cal.getTime();
                        String dateString = dateFormat.format(date2);
                        Date date = date2;
						try {
							date = dateFormat.parse(dateString);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        if(mcp.getImplementEmployees().getId() > 0){
                        	 String hql = "Select U.id from POS as U where createdAt >= '" + dateFormat.format(date2) + "' and createdUser = " + mcp.getImplementEmployees().getId();
                             List<POS> pos = dataService.executeSelectHQL(POS.class, hql, true, 0, 0);
                             mcp.setNewPOSComplete(pos.size());
                        }else{
                        	mcp.setNewPOSComplete(0);
                        }
                        //Tính doanh số trọng tâm...
                        //Xét số ngày làm việc trong tháng
                        int[] ngayLamViec = {25,16,26,25,25,25,26,25,25,26,26,25};
                        //Xét doanh số /ngay
                        mcp.setDsTargetPerDate(mcp.getSalesPerMonth()/ngayLamViec[dateCurrent.getMonth() - 1]);
                        mcp.setDsTargetFocusPerDate(mcp.getSalesFocusPerMonth()/ngayLamViec[dateCurrent.getMonth() - 1]);
                        //Lấy giao dịch và giao dịch chi tiết của từng nhân viên bán hàng
                        //Lấy giao dịch
                        //Tông số tiền bán được trong tháng
                        long totalSold = 0;
                        //Tổng số tiền trọng tâm đã bán được trong tháng
                        long totalSoldFocus = 0;
                        
                        ParameterList parameterList2 = new ParameterList();
                        parameterList2.add("transType", 2);
                        parameterList2.add("createdUser", mcp.getImplementEmployees().getId());
                     //   parameterList2.add("mcps.id", mcp.getId());
                        parameterList2.add("salesTransDate", date, ">=");
                        parameterList2.add("salesTransDate", dateCurrent, "<=");
                        List<SalesTrans> salesTrans = dataService.getListOption(SalesTrans.class, parameterList2);
                        if (!salesTrans.isEmpty()) {
                            for (SalesTrans sTrans : salesTrans) {
                                List<SalesTransDetails> salesTransDetails = dataService.getListOption(SalesTransDetails.class, new ParameterList("salesTranss.id", sTrans.getId()));
                                if (!salesTransDetails.isEmpty()) {

                                    for (SalesTransDetails sTransDetails : salesTransDetails) {
                                        //Xử lí số tiền bán được trong ngày.
//                                        if ((dateCurrent.getTime() - sTrans.getSalesTransDate().getTime()) / (24 * 60 * 60 * 1000) <= 1) {
//                                            totalSoldDate += sTransDetails.getQuantity() * sTransDetails.getPrice();
//                                            if (sTransDetails.getGoodss().getIsFocus() != 0) {
//                                                totalSoldDateFocus += sTransDetails.getQuantity() * sTransDetails.getPrice();
//                                            }
//                                        }
                                        //Xử lí tổng số tiền đã bán được trong tháng
                                        totalSold += sTransDetails.getQuantity() * sTransDetails.getPrice();
                                        //xử lí tổng số tiền trọng tâm đã bán được trong tháng
                                        if (sTransDetails.getGoodss().getIsFocus()) {
                                            totalSoldFocus += sTransDetails.getQuantity() * sTransDetails.getPrice();
                                        }
                                    }
                                }
                            }
                        }
                       mcp.setDsPerMonth(totalSold);
                       mcp.setDsFocusPerMonth(totalSoldFocus);
                       mcp.setDsPerDate(totalSold/ngayLamViec[dateCurrent.getMonth()-1]);
                    	
                        ParameterList parameterList = new ParameterList("mcps.id", mcp.getId());
                        parameterList.setOrder("finishTime","desc");
                        //Lấy danh sách MCPDetails.
                        List<MCPDetails> list = dataService.getListOption(MCPDetails.class, parameterList);
                        int i = 0;
                        for(MCPDetails mcpDetails : list){
                        	
                        	//Check xem nhân viên đã chăm sóc chưa
                        	ParameterList parameterList3 = new ParameterList();
                        	parameterList3.add("implementEmployees.id", mcp.getImplementEmployees().getId());
                        	parameterList3.add("mcpDetailss.id", mcpDetails.getId());
                        	parameterList3.add("poss.id", mcpDetails.getPoss().getId());
                        	//xử lí ngày giờ chăm sóc.
                        	Date date3 = mcp.getBeginDate();
                        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        	String fDate = simpleDateFormat.format(date3);
                        	Date fromDate = new Date();
                        	try {
            					fromDate = simpleDateFormat.parse(fDate);
            				} catch (ParseException e) {
            					// TODO Auto-generated catch block
            					return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
            				}
                        	Calendar calendar = Calendar.getInstance();
                        	calendar.setTime(fromDate);
                        	calendar.add(calendar.DAY_OF_MONTH, 1);
                        	Date toDate = calendar.getTime();
                        	parameterList3.add("startCustomerCareAt", fromDate, ">=");
                        	parameterList3.add("startCustomerCareAt", toDate, "<");
                        	
                        	List<CustomerCareInformation> cList = dataService.getListOption(CustomerCareInformation.class, parameterList3);
                        	if(cList.isEmpty()){
                        		mcpDetails.setIsCSDBH(0);
                        	}else{
                        		mcpDetails.setIsCSDBH(1);
                        		mcpDetails.setNgayCSDBH(cList.get(0).getStartCustomerCareAt());
                        		i++;
                        	}
                        }
                        mcp.setTotalPOS(list.size());
                        mcp.setTotalPOSComplete(i);
                        mcp.setMcpDetailss(list);
                        
                        String[] strings = {"type","isActive","note", "lastName","firstName", "statusTypes"};
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, mcp), strings);

                    } else {
                        //return khong ton tai
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                    }
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL));
                }

            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * CuTX update MCP customer Care Details admin
     * @param request
     * @return 
     */
    @RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_UPDATE_MCP_DETAILS_ADMIN, method = RequestMethod.POST)
    public @ResponseBody
    String updateMCPAndDetailsAdmin(HttpServletRequest request) {

        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            MCP mcp;

            try {
                //parse jsonString to a mcp Object
                mcp = MsalesJsonUtils.getObjectFromJSON(jsonString, MCP.class);
             }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //mcp from json not null
            if (mcp != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();

                if (mcp.getId() != null) {
                    MCP mcp2 = dataService.getRowById(mcp.getId(), MCP.class);

                    if (mcp2 == null) {
                        hashErrors.put("MCP", "MCP =" + mcp.getId() + "không tồn tại");
                    }
                }
                List<MCPDetails> mcpSalesDetailss = mcp.getMcpDetailss();
                for (MCPDetails mSalesDetails : mcpSalesDetailss) {
                    if (mSalesDetails != null) {
                        if (mSalesDetails.getId() == null) {
                            /*
                            MCP mcp1 = dataService.getRowById(mSalesDetails.getId(), MCP.class);
                            if (mcp1 == null) {
                                hashErrors.put("mcpDetailss", "mcpDetailss =" + mSalesDetails.getId() + "không tồn tại");
                            }
                            */
                        }
                        if (mSalesDetails.getImplementEmployeeId() != null) {
                            User user = dataService.getRowById(mSalesDetails.getImplementEmployeeId(), User.class);
                            if (user == null) {
                                hashErrors.put("implementEmployee", "implementEmployeeId = " + mSalesDetails.getImplementEmployeeId() + " không tồn tại!");
                            }
                        }
                        if (mSalesDetails.getPosId() != null) {
                            POS unit = dataService.getRowById(mSalesDetails.getPosId(), POS.class);
                            //goods is not exist
                            if (unit == null) {
                                hashErrors.put("POS",
                                        "posId = " + mSalesDetails.getPosId() + " không tồn tại!");
                            }
                            Location location = dataService.getRowById(unit.getLocations().getId(), Location.class);
                            mSalesDetails.setLocations(location);

                        }
                        if (mSalesDetails.getStatusId() != null) {
                            Status status = dataService.getRowById(mSalesDetails.getStatusId(), Status.class);
                            if (status == null) {
                                hashErrors.put("Status", "statusID = " + mSalesDetails.getStatusId() + " không tồn tại!");
                            }
                        }
                        mSalesDetails.setUpdatedUser(mcp.getUpdatedUser());
                    } else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
                    }
                }
                if (mcp.getImplementEmployeeId() != null) {
                    //get ImplementEmployee from mcp
                    User implementEmployee = dataService.getRowById(mcp.getImplementEmployeeId(), User.class);
                    //implementEmployee is not exist
                    if (implementEmployee == null) {
                        //return message warning implementEmployee is not exist in DB
                        hashErrors.put("implementEmployee", "implementEmployeeID = " + mcp.getImplementEmployeeId() + " không tồn tại!");
                    }
                }
                if (mcp.getStatusId() != null) {

                    // get status from mcp
                    Status status = dataService.getRowById(mcp.getStatusId(), Status.class);
                    //status is not exist
                    if (status == null) {
                        //return message warning status is not exist in DB
                        hashErrors.put("status", "statusID = " + mcp.getStatusId() + " không tồn tại!");
                    }
                }
                if (mcp.getType() != null && mcp.getType() != 1) {
                    hashErrors.put("MCP", "type bắt buộc là 1");
                }
                if (mcp.getUpdatedUser() != null) {
                    User user = dataService.getRowById(mcp.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + mcp.getUpdatedUser() + " không tồn tại.");
                    }
                }
                if (mcp.getUpdatedUser() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, "updatedUser không được null"));
                }

                if (hashErrors.size() > 0) {
                    // return message warning tableName or property is not exist
                    // in DB
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));

                }

                int ret1 = 0;
                int ret2 = 0;
                try {
                    //save status to DB
                    ret1 = dataService.updateSync(mcp);
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }
                }

                if (ret1 > 0) {
                    for (MCPDetails newMcpDetail : mcpSalesDetailss) {
                        newMcpDetail.setMcpId(ret1);

                        try {
                            newMcpDetail.setCreatedUser(mcp.getUpdatedUser());
                            ret2 = dataService.insertRow(newMcpDetail);
                        } catch (MSalesException e) {
                            Exception ex = (Exception) e.getCause().getCause();
                            if (ex instanceof javax.validation.ConstraintViolationException) {
                                return MsalesJsonUtils.jsonValidate(ex);
                            }
                        }
                    }
                }
                //save Succcess
                if (ret2 > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, null));
                } //save failed
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
            } //mcp from json null
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
     * search MCP Details
     *	DuanND sửa
     * @param request
     * @return
     */
    @SuppressWarnings( "unchecked")
	@RequestMapping(value = MsalesConstants.MODULE.MCP.ACTION_SEARCH_MCP_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String searchMCPDetailsAdmin(HttpServletRequest request) {
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
    //    ParameterList parameterList = new ParameterList();
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            LinkedHashMap<String, Object> searchObject;
            try {
                //parse jsonString to a search Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        LinkedHashMap.class);
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //searchObject from json not null
            String hql = "Select U from MCP as U where type = 1 and deletedUser = 0 ";
            String hql2 = "Select U.id as id from MCP as U where type = 1 and deletedUser = 0 ";
            if (searchObject != null) {
               //Check from Date, toDate
               SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
               SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy");
               if (searchObject.get("fromDate") != null) {
                   String fromDate = searchObject.get("fromDate").toString();
                   if (fromDate != null && !fromDate.isEmpty() && !fromDate.trim().isEmpty() && !fromDate.equals("")) {
                       try {
                           Date date1 = sDateFormat.parse(fromDate);
                           String fromDate2 = simpleDateFormat.format(date1);
                           Date date2 = simpleDateFormat.parse(fromDate2);
                           //parameterList.add("salesTransDate", date1, ">=");
                           hql += " and beginDate >= '" + simpleDateFormat.format(date2)+"'";
                           hql2 += " and beginDate >= '" + simpleDateFormat.format(date2)+"'";
                       } catch (ParseException e) {
                           // TODO Auto-generated catch block
                     	  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                           
                       }
                   }
               }
               
               if (searchObject.get("toDate") != null) {
                   String toDate = searchObject.get("toDate").toString();
                   if (toDate != null && !toDate.isEmpty() && !toDate.trim().isEmpty() && !toDate.equals("")) {
                       try {
                     	  Date date1 = sDateFormat.parse(toDate);
                           String toDate2 = simpleDateFormat.format(date1);
                           Date date2 = simpleDateFormat.parse(toDate2);
                           Calendar calendar = Calendar.getInstance();
                           calendar.setTime(date2);
                           calendar.add(calendar.DAY_OF_MONTH, 1);
                           //parameterList.add("salesTransDate", date2, "<=");
                           hql += " and beginDate <= '" + simpleDateFormat.format(calendar.getTime())+"'";
                           hql2 += " and beginDate <= '" + simpleDateFormat.format(calendar.getTime())+"'";
                       } catch (ParseException e) {
                           // TODO Auto-generated catch block
                     	  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                       }
                   }
               }
               // 
               //check createdUser
               if(searchObject.get("searchText") != null){
             	  String key = searchObject.get("searchText").toString();
             	  if(key != null && !key.isEmpty() && !key.trim().isEmpty() && !key.equals("")){
             		  hql += " and ( note LIKE '%" + key + "%'" + " or name LIKE '%" + key + "%'" + " ) ";
             		 hql2 += " and ( note LIKE '%" + key + "%'" + " or name LIKE '%" + key + "%'" + " ) ";
             	  }
               }
               if(searchObject.get("implementEmployeeId") != null){
             	  try{
                 	  int createdUser = Integer.parseInt(searchObject.get("implementEmployeeId").toString());
                 	  if(createdUser != 0){
                 		 hql += " and implementEmployees.id = " + createdUser;
                 		 hql2 += " and implementEmployees.id = " + createdUser;
                 	  }else{
                 		  if(searchObject.get("employerList") != null){
                 			  ArrayList<LinkedHashMap<String, Object>> employerList = (ArrayList<LinkedHashMap<String, Object>>) searchObject.get("employerList");
                 				  String string = "";
                 				  if(employerList.size() > 1){
                 					  for(int i = 1; i < employerList.size(); i++){
                     					  int id = Integer.parseInt(employerList.get(i).get("id").toString());
                     					  if(id != 0){
                                           		  string += " or implementEmployees.id = " + id;
                     					  }else{
                     						  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                     					  }
                     				  }
                     				  //Xét hql
                     				  hql += " and ( " + string.substring(3) + ") ";
                     				 hql2 += " and ( " + string.substring(3) + ") ";
                 				  }else{
                 					  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                 			}
                 		  }
                 	  }
             	  }catch(Exception ex){
             		  if (ex instanceof ConstraintViolationException) {
                           return MsalesJsonUtils.jsonValidate(ex);
                       }//else
                       return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, ex.getMessage()));
             	  }
               }
               
            }
            //
            List<MCP> count = new ArrayList<MCP>();
            List<MCP> lists = new ArrayList<MCP>();
            hql += " order by beginDate DESC ";
            try{
            	count = dataService.executeSelectHQL(MCP.class, hql2, true, 0, 0);
                lists = dataService.executeSelectHQL(MCP.class, hql, false, page.getPageNo(), page.getRecordsInPage());
            }catch(Exception ex){
            	return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, ex.getMessage()));
            }
            
            //Cập nhật thông tin cho list.
            for(MCP mcp : lists){
            	//Xét tên cho nhân viên
            	mcp.getImplementEmployees().setName(mcp.getImplementEmployees().getLastName() + " " + mcp.getImplementEmployees().getFirstName());
            	//Xét tỉnh thành cho nhân viên
            	if(mcp.getImplementEmployees().getLocations().getLocationType() == 1){
            		mcp.getImplementEmployees().setLocation(mcp.getImplementEmployees().getLocations().getName());
        		}else if(mcp.getImplementEmployees().getLocations().getParents().getLocationType() == 1){
        			mcp.getImplementEmployees().setLocation(mcp.getImplementEmployees().getLocations().getParents().getName());
        		}else{
        			mcp.getImplementEmployees().setLocation(mcp.getImplementEmployees().getLocations().getParents().getParents().getName());
        		}
            	//Xét Người lập
            	int createdUserId = mcp.getCreatedUser();
            	User user = dataService.getRowById(createdUserId, User.class);
            	if(user != null){
            		mcp.setNameAdmin(user.getLastName() + " " + user.getFirstName());
            	}else{
            		mcp.setNameAdmin("Admin");
            	}
            	//Check số điểm bán hàng trên tuyến đường
            	ParameterList parameterList = new ParameterList("mcps.id", mcp.getId());
                parameterList.setOrder("finishTime","desc");
                //Lấy danh sách MCPDetails.
                List<MCPDetails> list = dataService.getListOption(MCPDetails.class, parameterList);
                int i = 0;
                for(MCPDetails mcpDetails : list){
                	
                	//Check xem nhân viên đã chăm sóc chưa
                	ParameterList parameterList3 = new ParameterList(1, 1);
                	parameterList3.add("implementEmployees.id", mcp.getImplementEmployees().getId());
                	parameterList3.add("mcpDetailss.id", mcpDetails.getId());
                	parameterList3.add("poss.id", mcpDetails.getPoss().getId());
                	//xử lí ngày giờ chăm sóc.
                	Date date3 = mcp.getBeginDate();
                	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                	String fDate = simpleDateFormat.format(date3);
                	Date fromDate = new Date();
                	try {
    					fromDate = simpleDateFormat.parse(fDate);
    				} catch (ParseException e) {
    					// TODO Auto-generated catch block
    					return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
    				}
                	Calendar calendar = Calendar.getInstance();
                	calendar.setTime(fromDate);
                	calendar.add(calendar.DAY_OF_MONTH, 1);
                	Date toDate = calendar.getTime();
                	parameterList3.add("startCustomerCareAt", fromDate, ">=");
                	parameterList3.add("startCustomerCareAt", toDate, "<");
                	
                	List<CustomerCareInformation> cList = dataService.getListOption(CustomerCareInformation.class, parameterList3);
                	if(cList.isEmpty()){
                	}else{
                		i++;
                	}
                }
                mcp.setTotalPOS(list.size());
                mcp.setTotalPOSComplete(i);
                if(i == list.size() && i != 0){
                	mcp.setIsComplete(1);
                }else{
                	mcp.setIsComplete(0);
                }
            }
            MsalesResults<MCP> listMCPs = new MsalesResults<MCP>();
            listMCPs.setCount(Long.parseLong(count.size()+""));
            listMCPs.setContentList(lists);
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listMCPs));
            //jsonString null
        }else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }
        
}
