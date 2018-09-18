/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.controller;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

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
import vn.itt.msales.entity.CustomerCareDetails;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.Workflow;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.CustomerCareDetailsById;
import vn.itt.msales.logex.MSalesException;

;

/**
 *
 * @author Cutx
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_DETAILS.NAME)
public class MsalesCustomerCareDetailsController extends CsbController {

    /**
     * get a CustomerCareDetails info
     *
     * @param request is a HttpServletRequest
     * @return string json include CustomerCareDetails info
     */
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_DETAILS.ACTION_GET_CUSTOMER_CARE_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String getCustomerCareDetails(HttpServletRequest request) {
         // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null

        if (jsonString != null) {
            CustomerCareDetails customerCareDetails;
            try {
                //parse jsonString to a Channel Object
                customerCareDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CustomerCareDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channel from json not null
            if (customerCareDetails != null) {
                if (customerCareDetails.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = customerCareDetails.getId();
                //get channel from DB
                customerCareDetails = dataService.getRowById(customerCareDetails.getId(),
                        CustomerCareDetails.class);
                //channel not null
                if (customerCareDetails != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, customerCareDetails));
                } //channel null
                else {
                    return MsalesJsonUtils.notExists(CustomerCareInformation.class, id);
                }
            } //channel from json null
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
     * create a CustomerCareDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_DETAILS.ACTION_CREATE_CUSTOMER_CARE_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String createCustomerCareDetails(HttpServletRequest request) {

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            CustomerCareDetails customerCareDetails;
            try {
                //parse jsonString to a CustomerCareDetails Object
                customerCareDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, CustomerCareDetails.class);
             }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //CustomerCareDetails from json not null
            if (customerCareDetails != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (customerCareDetails.getCustomerCareInformationId() != null) {
                    // get CustomerCareInformation from CustomerCaredetails
                    CustomerCareInformation customerCareInformation = dataService.getRowById(customerCareDetails.getCustomerCareInformationId(), CustomerCareInformation.class);
                    //CustomerCareInformation is not exist
                    if (customerCareInformation == null) {
                        //return message warning customerCareInformation is not exist in DB
                        hashErrors.put("CustomerCareInformation",
                                "CustomerCareInformation with ID = " + customerCareDetails.getCustomerCareInformationId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (customerCareDetails.getPosId() != null) {
                    // get Channel from CustomerCareDetails
                    Channel channel = dataService.getRowById(customerCareDetails.getPosId(), Channel.class);
                    //Channel is not exist
                    if (channel == null) {
                        //return message warning Channel is not exist in DB
                        hashErrors.put("Channel",
                                "Channel with ID = " + customerCareDetails.getPosId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (customerCareDetails.getWorkflowId() != null) {
                    // get Workflow from CustomerCareDetails
                    Workflow workflow = dataService.getRowById(customerCareDetails.getWorkflowId(), Workflow.class);
                    //Workflow is not exist
                    if (workflow == null) {
                        //return message warning Workflow is not exist in DB
                        hashErrors.put("Workflow",
                                "Workflow with ID = " + customerCareDetails.getWorkflowId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                //check validate user create
                if (customerCareDetails.getCreatedUser() != null) {
                    User user = dataService.getRowById(customerCareDetails.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + customerCareDetails.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                try {

                    //save CustomerCareDetails to DB
                    int ret = dataService.insertRow(customerCareDetails);

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
            } //CustomerCareDetails from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * update a CustomerCareDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_DETAILS.ACTION_UPDATE_CUSTOMER_CARE_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String updateCustomerCareDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            CustomerCareDetails customerCareDetails;
            try {
                customerCareDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, CustomerCareDetails.class);
             }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CustomerCareDetails from json not null
            if (customerCareDetails != null) {
                //check Id
                if (customerCareDetails.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));

                }
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();

                if (customerCareDetails.getCustomerCareInformationId() != null) {
                    // get CustomerCareInformation from CustomerCareDetails
                    CustomerCareInformation customerCareInformation = dataService.getRowById(customerCareDetails.getCustomerCareInformationId(), CustomerCareInformation.class);
                    //CustomerCareInformation is not exist
                    if (customerCareInformation == null) {
                        //return message warning CustomerCareInformation is not exist in DB
                        hashErrors.put("CustomerCareInformation",
                                "CustomerCareInformation with ID = " + customerCareDetails.getCustomerCareInformationId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (customerCareDetails.getPosId() != null) {
                    // get Channel from CustomerCareDetails
                    Channel channel = dataService.getRowById(customerCareDetails.getPosId(), Channel.class);
                    //Channel is not exist
                    if (channel == null) {
                        //return message warning Channel is not exist in DB
                        hashErrors.put("Channel",
                                "Channel with ID = " + customerCareDetails.getPosId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                if (customerCareDetails.getWorkflowId() != null) {
                    // get Workflow from CustomerCareDetails
                    Workflow workflow = dataService.getRowById(customerCareDetails.getWorkflowId(), Workflow.class);
                    //Workflow is not exist
                    if (workflow == null) {
                        //return message warning Workflow is not exist in DB
                        hashErrors.put("Workflow",
                                "Workflow with ID = " + customerCareDetails.getWorkflowId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                //check validate user update
                if (customerCareDetails.getUpdatedUser() != null) {
                    User user = dataService.getRowById(customerCareDetails.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + customerCareDetails.getUpdatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {

                    //update CustomerCareDetails to DB
                    int ret = dataService.updateSync(customerCareDetails);

                    //update success
                    if (ret == -2) {
                        hashErrors.put("CustomerCareDetails",
                                "CustomerCareDetails with ID = " + customerCareDetails.getId() + " không tồn tại trên Database");
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

            } //CustomerCareDetails from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * delete a CustomerCareDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_DETAILS.ACTION_DELETE_CUSTOMER_CARE_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String deleteCustomerCareDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            CustomerCareDetails customerCareDetails;
            try {
                //parse jsonString to a CustomerCareDetails Object
                customerCareDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, CustomerCareDetails.class);
             }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //CustomerCareDetails from json not null
            if (customerCareDetails != null) {
                if (customerCareDetails.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));

                }
                if (customerCareDetails.getDeletedUser() != null) {
                    User user = dataService.getRowById(customerCareDetails.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, customerCareDetails.getDeletedUser());
                    }

                }
                try {

                    //update delete CustomerCareDetails
                    int ret = dataService.deleteSynch(customerCareDetails);
                    if (ret == -2) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("CustomerCareDetails",
                                "CustomerCareDetails with ID = " + customerCareDetails.getId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else //update delete CustomerCareDetails success
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

            } //CustomerCareDetails from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get List customerCareDetails by channel
     *
     * @param request is a HttpServletRequest
     * @return string json include List CustomerCareDetails
     */
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_DETAILS.ACTION_GET_LIST_CUSTOMER_CARE_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String getListCustomerCareDetails(HttpServletRequest request) {
       String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null

        if (jsonString != null) {
            CustomerCareDetails customerCareDetails;
            try {
                //parse jsonString to a Channel Object
                customerCareDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CustomerCareDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Schedule from json not null
            if (customerCareDetails != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (customerCareDetails.getCustomerCareInformationId()!= null) {
                    CustomerCareInformation customerCareInformation = dataService.getRowById(customerCareDetails.getCustomerCareInformationId(), CustomerCareInformation.class);
                    if (customerCareInformation == null) {
                        hashErrors.put("CustomerCareInformation",
                                "CustomerCareInformation with ID = " + customerCareDetails.getCustomerCareInformationId()+ " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    hashErrors.put("customerCareInformationId", "customerCareInformationId  không được null!");
                }
               
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List MCP by implementEmployeeId 
                ParameterList parameterList = new ParameterList("customerCareInformations.id", customerCareDetails.getCustomerCareInformationId(), page.getPageNo(), page.getRecordsInPage());
//                parameterList.add("channels.id", customerCareDetails.getChannelId());
                
                MsalesResults<CustomerCareDetails> list = dataService.getListOption(CustomerCareDetails.class, parameterList,true);
                String[] fiStrings = {"mcpDetailss","poss"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list),fiStrings);

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
     * getListCustomerCareDetailsById
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_DETAILS.ACTION_GET_LIST_CUSTOMER_CARE_DETAILS_BY_ID, method = RequestMethod.POST)
    public @ResponseBody String getListCustomerCareDetailsById(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            CustomerCareDetails customerCareDetails;
            try {
                //parse jsonString to a Channel Object
                customerCareDetails = MsalesJsonUtils.getObjectFromJSON(jsonString, CustomerCareDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Schedule from json not null
            if (customerCareDetails != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (customerCareDetails.getCustomerCareInformationId() == null) {
                	 hashErrors.put("customerCareInformationId", "customerCareInformationId  không được null!");
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List MCP by implementEmployeeId 
                ParameterList parameterList = new ParameterList("customerCareInformations.id", customerCareDetails.getCustomerCareInformationId(), page.getPageNo(), page.getRecordsInPage());
//                parameterList.add("channels.id", customerCareDetails.getChannelId());
                MsalesResults<CustomerCareDetails> list = dataService.getListOption(CustomerCareDetails.class, parameterList,true);
                ArrayList<Integer> arrWorkflowId = new ArrayList<Integer>();
                for(int i = 0; i < list.getContentList().size(); i++){
                	boolean bool = true;
                	for(int j = 0; j < i; j++){
                		if(list.getContentList().get(i).getWorkflows().getId() == list.getContentList().get(j).getWorkflows().getId()){
                			bool = false;
                			break;
                		}
                	}
                	if(bool){
                		arrWorkflowId.add(list.getContentList().get(i).getWorkflows().getId());
                	}
                }
                //Xét List cho đầu ra:
                List<CustomerCareDetailsById> lists = new ArrayList<CustomerCareDetailsById>();
                if(!arrWorkflowId.isEmpty()){
                	for(int i = 0; i < arrWorkflowId.size(); i++){
                		CustomerCareDetailsById careDetailsById = new CustomerCareDetailsById();
                		List<CustomerCareDetails> cList = new ArrayList<CustomerCareDetails>();
                		String groupName = "";
                		for(CustomerCareDetails careDetails : list.getContentList()){
                			if(arrWorkflowId.get(i) == careDetails.getWorkflows().getId()){
                				groupName = careDetails.getWorkflows().getWorkflowTypes().getName();
                				careDetails.setTitle(careDetails.getWorkflows().getTitle());
                				cList.add(careDetails);
                			}
                		}
                		if(!cList.isEmpty()){
                			careDetailsById.setContents(cList);
                		}
                		careDetailsById.setGroupName(groupName);
                		if(careDetailsById != null){
                    		lists.add(careDetailsById);
                    	}
                	}
                }
                MsalesResults<CustomerCareDetailsById> listsCCDs = new MsalesResults<CustomerCareDetailsById>();
                listsCCDs.setCount(list.getCount());
                listsCCDs.setContentList(lists);
                
                String[] fiStrings = {"mcpDetailss","poss"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listsCCDs),fiStrings);
            } //goods from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

    }
    
}
