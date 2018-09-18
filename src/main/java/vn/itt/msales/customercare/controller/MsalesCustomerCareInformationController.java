/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.controller;

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
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.customercare.model.MsalesSearchCCI;
import vn.itt.msales.entity.CustomerCareDetails;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;

;

/**
 *
 * @author Cutx
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_INFORMATION.NAME)
public class MsalesCustomerCareInformationController extends CsbController {

    /**
     * get a CustomerCareInformation info
     *
     * @param request is a HttpServletRequest
     * @return string json include CustomerCareInformation
     */
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_INFORMATION.ACTION_GET_CUSTOMER_CARE_INFORMATION, method = RequestMethod.POST)
    public @ResponseBody
    String getCustomerCareInformation(HttpServletRequest request) {
         // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null

        if (jsonString != null) {
            CustomerCareInformation customerCareInformation;
            try {
                //parse jsonString to a Channel Object
                customerCareInformation = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CustomerCareInformation.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channel from json not null
            if (customerCareInformation != null) {
                if (customerCareInformation.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = customerCareInformation.getId();
                //get channel from DB
                customerCareInformation = dataService.getRowById(customerCareInformation.getId(),
                        CustomerCareInformation.class);
                //channel not null
                if (customerCareInformation != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, customerCareInformation));
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
     * create a CustomerCareInformation
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_INFORMATION.ACTION_CREATE_CUSTOMER_CARE_INFORMATION, method = RequestMethod.POST)
    public @ResponseBody
    String createCustomerCareInformation(HttpServletRequest request) {

    // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            CustomerCareInformation customerCareInformation;
            try {
                //parse jsonString to a customerCareInformation Object
                customerCareInformation = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CustomerCareInformation.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //customerCareInformation from json not null
            if (customerCareInformation != null) {
                //validate info relation
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
//                if (customerCareInformation.getChannelId()!= null) {
//                    Channel channel = dataService.getRowById(customerCareInformation.getChannelId(), Channel.class);
//                    if (channel == null) {
//                        hashErrors.put("Channel", "ID = " + customerCareInformation.getChannelId()+ " không tồn tại.");
//                    }
//                }
                if (customerCareInformation.getImplementEmployeeId()!= null) {
                    User implementEmployee = dataService.getRowById(customerCareInformation.getImplementEmployeeId(), User.class);
                    if (implementEmployee == null) {
                        hashErrors.put("implementEmployeeId", "ID = " + customerCareInformation.getImplementEmployeeId()+ " không tồn tại.");
                    }
                }
                
                //Check validate POS/MCPDetails
                

                if (customerCareInformation.getCreatedUser() != null) {
                    User user = dataService.getRowById(customerCareInformation.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + customerCareInformation.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save channel to DB
                    int ret = dataService.insertRow(customerCareInformation);

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
     * update a CustomerCareInformation
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_INFORMATION.ACTION_UPDATE_CUSTOMER_CARE_INFORMATION, method = RequestMethod.POST)
    public @ResponseBody
    String updateCustomerCareInformation(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            CustomerCareInformation customerCareInformation;
            try {
                customerCareInformation = MsalesJsonUtils.getObjectFromJSON(jsonString, CustomerCareInformation.class);
             }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CustomerCareInformation from json not null
            if (customerCareInformation != null) {
                // check id
                if (customerCareInformation.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));

                }
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
//                if (customerCareInformation.getChannelId() != null) {
//                    //get Channel from CustomerCareInformation
//                    Channel channel = dataService.getRowById(customerCareInformation.getChannelId(), Channel.class);
//                    //Channel is not exist
//                    if (channel == null) {
//                        //return message warning Channel is not exist in DB
//                        hashErrors.put("Channel",
//                                "Channel with ID = " + customerCareInformation.getChannelId() + " không tồn tại trên Database");
//                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
//                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
//                    }
//                }
                if (customerCareInformation.getImplementEmployeeId() != null) {
                    // get ImplementEmployee from customerCareInformation
                    User user = dataService.getRowById(customerCareInformation.getImplementEmployeeId(), User.class);
                    //ImplementEmployee is not exist
                    if (user == null) {
                        //return message warning ImplementEmployee is not exist in DB
                        hashErrors.put("ImplementEmployee",
                                "ImplementEmployee with ID = " + customerCareInformation.getImplementEmployeeId() + "không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                //Check validate POS/MCPDetail
                
                //check validate user update
                if (customerCareInformation.getUpdatedUser() != null) {
                    User user = dataService.getRowById(customerCareInformation.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + customerCareInformation.getUpdatedUser() + "không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {

                    //update CustomerCareInformation to DB
                    int ret = dataService.updateSync(customerCareInformation);

                    //update success
                    if (ret == -2) {
                        hashErrors.put("CustomerCareInformation",
                                "CustomerCareInformation with ID = " + customerCareInformation.getId() + " không tồn tại trên Database");
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
            } //CustomerCareInformation from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * delete a CustomerCareInformation
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_INFORMATION.ACTION_DELETE_CUSTOMER_CARE_INFORMATION, method = RequestMethod.POST)
    public @ResponseBody
    String deleteCustomerCareInformation(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            CustomerCareInformation customerCareInformation;
            try {
                //parse jsonString to a CustomerCareInformation Object
                customerCareInformation = MsalesJsonUtils.getObjectFromJSON(jsonString, CustomerCareInformation.class);
              }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //customerCareInformation from json not null
            if (customerCareInformation != null) {
                if (customerCareInformation.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));

                }
                if (customerCareInformation.getDeletedUser() != null) {
                    User user = dataService.getRowById(customerCareInformation.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, customerCareInformation.getDeletedUser());
                    }

                }
                try {
                    //update delete customerCareInformation
                    int ret = dataService.deleteSynch(customerCareInformation);
                    if (ret == -2) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("CustomerCareInformation",
                                "CustomerCareInformation with ID = " + customerCareInformation.getId() + " không tồn tại.");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else //update delete customerCareInformation success
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

            } //customerCareInformation from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get List customerCareInformation by Goods
     *
     * @param request is a HttpServletRequest
     * @return string json include List customerCareInformation
     */
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_INFORMATION.ACTION_GET_LIST_CUSTOMER_CARE_INFORMATION, method = RequestMethod.POST)
    public @ResponseBody
    String getListCustomerCareInformation(HttpServletRequest request) {
        // get jsonString from CSB 
         String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null

        if (jsonString != null) {
            CustomerCareInformation customerCareInformation;
            try {
                //parse jsonString to a Channel Object
                customerCareInformation = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CustomerCareInformation.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CustomerCareInformation from json not null
            if (customerCareInformation != null) {
              LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
//                if (customerCareInformation.getChannelId()!= null) {
//                    Channel channel = dataService.getRowById(customerCareInformation.getChannelId(), Channel.class);
//                    if (channel == null) {
//                        hashErrors.put("Channel",
//                                "channel with ID = " + customerCareInformation.getChannelId()+ " không tồn tại.");
//                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
//                        System.out.println(s);
//                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
//                    }
//                }
//                else{
//                hashErrors.put("channelId", "  không được null!");
//                }
                if (customerCareInformation.getStartCustomerCareAt()== null) {
                    hashErrors.put("startCustomerCareAt", "  không được null!");
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());
                parameterList.add("startCustomerCareAt", customerCareInformation.getStartCustomerCareAt(), ">=");
//                parameterList.add("implementEmployees.id", customerCareInformation.getImplementEmployeeId());
//                parameterList.add("isActiveChannel", customerCareInformation.getIsActiveChannel());

                MsalesResults<CustomerCareInformation> list = dataService.getListOption(CustomerCareInformation.class, parameterList,true);
                String [] strings ={"mcpDetailss","poss"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list),strings);

            } //goods from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }


    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_INFORMATION.ACTION_GET_CUSTOMER_CARE_INFORMATION_ADMIN, method = RequestMethod.POST)
    public @ResponseBody
    String getCustomerCareInfAdmin(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            try {
                // convert json to CustomerCareInformation object
                CustomerCareInformation customerCareInformation = MsalesJsonUtils.getObjectFromJSON(jsonString, CustomerCareInformation.class);
                if (customerCareInformation != null && customerCareInformation.getId() != null) {
                    CustomerCareInformation cci = dataService.getRowById(customerCareInformation.getId(), CustomerCareInformation.class);
                    if (cci != null) {
                        List<CustomerCareDetails> lisCustomerCareDetailses = dataService.getListOption(CustomerCareDetails.class, new ParameterList("customerCareInformations.id", cci.getId()));
                        cci.setCustomerCareDetailss(lisCustomerCareDetailses);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, cci));
                    } else {
                        return MsalesJsonUtils.notExists(CustomerCareInformation.class, customerCareInformation.getId());
                    }
                } else {
                    return MsalesJsonUtils.idNull();
                }
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

//    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_INFORMATION.ACTION_SEARCH_CUSTOMER_CARE_INFORMATION_ADMIN, method = RequestMethod.POST)
//    public @ResponseBody
//    String searchCustomerCareInfAdmin(HttpServletRequest request) {
//        // get jsonString from CSB
//        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
//        if (jsonString != null) {
//            try {
//                MsalesSearchCCI mscci = MsalesJsonUtils.getObjectFromJSON(jsonString, MsalesSearchCCI.class);
//                if (mscci != null) {
//                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, mscci));
//                } else {
//                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_INVALID));
//                }
//            } catch (Exception ex) {
//                // check field in JSOn request not match with field in JSON.
//                return MsalesJsonUtils.validateFormat(ex);
//            }
//        } //jsonString null
//        else {
//            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
//        }
//    }

//    @SuppressWarnings("unchecked")
//    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_INFORMATION.ACTION_SEARCH_CUSTOMER_CARE_INFORMATION_ADMIN, method = RequestMethod.POST)
//    public @ResponseBody String searchCustomerCareInformationAdmin(HttpServletRequest request) {
//        //get List GoodsCategory from DB
//        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
//        ParameterList parameterList = new ParameterList();
//        // get jsonString from CSB 
//        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
//        //jsonString not null
//        if (jsonString != null) {
//            LinkedHashMap<String, Object> searchObject;
//            try {
//                //parse jsonString to a search Object
//                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString,
//                        LinkedHashMap.class);
//                //jsonString syntax incorrect
//            } catch (Exception ex) {
//                // check field in JSOn request not match with field in JSON.
//                return MsalesJsonUtils.validateFormat(ex);
//            }
//            ArrayList<Integer> arrayLocationId = new ArrayList<Integer>();
//            ArrayList<Integer> arrayChannelId = new ArrayList<Integer>();
//            List<CustomerCareInformation> lists = new ArrayList<CustomerCareInformation>();
//            //searchObject from json not null
//            if (searchObject != null) {
//                String key = null;
//                String fromDate = null;
//                String toDate = null;
//                if (searchObject.get("searchText") != null) {
//                    key = searchObject.get("searchText").toString();
//                }
//                
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
//                if (searchObject.get("fromDate") != null) {
//                    fromDate = searchObject.get("fromDate").toString();
//                }
//                if (searchObject.get("toDate") != null) {
//                    toDate = searchObject.get("toDate").toString();
//                }
//
//                if (searchObject.get("implementEmployeeId") != null) {
//                    try {
//                        int implementEmployeeId = Integer.parseInt(searchObject.get("implementEmployeeId").toString());
//                        ParameterList parameterList2 = new ParameterList();
//                        parameterList2.add("implementEmployees.id", implementEmployeeId);
//                        List<CustomerCareInformation> list = dataService.getListOption(CustomerCareInformation.class, parameterList2);
//                        if (list != null) {
//                            lists.addAll(list);
//                        }
//                    } catch (Exception ex) {
//
//                    }
//                } else {
//                    if (searchObject.get("channelId") != null) {
//                        try {
//
//                            int channelId = Integer.parseInt(searchObject.get("channelId").toString());
//                            ParameterList parameterList2 = new ParameterList("channels.id", channelId);
//                            //get List all user have channelId
//                            List<UserRoleChannel> list = dataService.getListOption(UserRoleChannel.class, parameterList2);
//                            int[] userId = new int[list.size()];
//                            int count = 0;
//                            for (UserRoleChannel user : list) {
//                                if (user != null) {
//                                    userId[count] = user.getUsers().getId();
//                                    count++;
//                                }
//                            }
//                            for (int i = 0; i < userId.length; i++) {
//                                boolean blean = true;
//                                for (int j = 0; j < i; j++) {
//                                    if ((userId[i] == userId[j]) || userId[i] == 0) {
//                                        blean = false;
//                                        break;
//                                    }
//                                }
//                                if (blean) {
//                                    arrayChannelId.add(userId[i]);
//                                }
//                            }
//
//                        } catch (Exception ex) {
//                            //error parse goodsCategoryId
//                        }
//                    }
//                    if (searchObject.get("locationId") != null) {
//                        try {
//                            int locationId = Integer.parseInt(searchObject.get("locationId").toString());
//                            ParameterList parameterList2 = new ParameterList("locations.id", locationId);
//                            List<User> list = dataService.getListOption(User.class, parameterList2);
//                            int[] userId = new int[list.size()];
//                            int count = 0;
//                            for (User user : list) {
//                                if (user != null) {
//                                    userId[count] = user.getId();
//                                    count++;
//                                }
//                            }
//                            for (int i = 0; i < userId.length; i++) {
//                                boolean blean = true;
//                                for (int j = 0; j < i; j++) {
//                                    if ((userId[i] == userId[j]) || userId[i] == 0) {
//                                        blean = false;
//                                        break;
//                                    }
//                                }
//                                if (blean) {
//                                    arrayLocationId.add(userId[i]);
//                                }
//                            }
//                        } catch (Exception ex) {
//                            //error parse goodsCategoryId
//                        }
//                    }
//                    if (searchObject.get("channelId") != null && searchObject.get("locationId") == null) {
//                        for (int i = 0; i < arrayChannelId.size(); i++) {
//                            ParameterList parameterList2 = new ParameterList();
//                            parameterList2.add("implementEmployees.id", arrayChannelId.get(i));
//
//                            if (key != null) {
//                                parameterList2.or("transCode", key, "like", "picture1FilePath", key, "like");
//                            }
//                            if (fromDate != null) {
//                                try {
//                                    Date date1 = simpleDateFormat.parse(fromDate);
//                                    parameterList2.add("beginDate", date1, ">=");
//                                } catch (ParseException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                            }
//                            if (toDate != null) {
//                                try {
//                                    Date date2 = simpleDateFormat.parse(toDate);
//                                    parameterList2.add("beginDate", date2, "<=");
//                                } catch (ParseException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                            }
//                            List<CustomerCareInformation> list = dataService.getListOption(CustomerCareInformation.class, parameterList2);
//                            if (list != null) {
//                                lists.addAll(list);
//                            }
//                        }
//                    } else if (searchObject.get("channelId") == null && searchObject.get("locationId") != null) {
//                        for (int i = 0; i < arrayLocationId.size(); i++) {
//                            ParameterList parameterList2 = new ParameterList();
//                            parameterList2.add("implementEmployees.id", arrayLocationId.get(i));
//                            if (key != null) {
//                                parameterList2.or("transCode", key, "like", "picture1FilePath", key, "like");
//                            }
//                            if (fromDate != null) {
//                                try {
//                                    Date date1 = simpleDateFormat.parse(fromDate);
//                                    parameterList2.add("beginDate", date1, ">=");
//                                } catch (ParseException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                            }
//                            if (toDate != null) {
//                                try {
//                                    Date date2 = simpleDateFormat.parse(toDate);
//                                    parameterList2.add("beginDate", date2, "<=");
//                                } catch (ParseException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                            }
//                            List<CustomerCareInformation> list = dataService.getListOption(CustomerCareInformation.class, parameterList2);
//                            if (list != null) {
//                                lists.addAll(list);
//                            }
//                        }
//                    } else {
//                        for (int i = 0; i < arrayChannelId.size(); i++) {
//                            boolean blean = false;
//                            for (int j = 0; j < arrayLocationId.size(); j++) {
//                                if (arrayChannelId.get(i) == arrayLocationId.get(j) && arrayChannelId.get(i) != null && arrayChannelId.get(i) > 0) {
//                                    blean = true;
//                                    break;
//                                }
//                            }
//                            if (blean) {
//                                //parameterList.add("implementEmployees.id", arrayUserId.get(i));
//                                ParameterList parameterList2 = new ParameterList();
//                                parameterList2.add("implementEmployees.id", arrayChannelId.get(i));
//                                if (key != null) {
//                                    parameterList2.or("transCode", key, "like", "picture1FilePath", key, "like");
//                                }
//                                if (fromDate != null) {
//                                    try {
//                                        Date date1 = simpleDateFormat.parse(fromDate);
//                                        parameterList2.add("beginDate", date1, ">=");
//                                    } catch (ParseException e) {
//                                        // TODO Auto-generated catch block
//                                        e.printStackTrace();
//                                    }
//                                }
//                                if (toDate != null) {
//                                    try {
//                                        Date date2 = simpleDateFormat.parse(toDate);
//                                        parameterList2.add("beginDate", date2, "<=");
//                                    } catch (ParseException e) {
//                                        // TODO Auto-generated catch block
//                                        e.printStackTrace();
//                                    }
//                                }
//                                List<CustomerCareInformation> list = dataService.getListOption(CustomerCareInformation.class, parameterList2);
//                                if (list != null) {
//                                    lists.addAll(list);
//                                }
//                            }
//                        }
//                    }
//                }
//                if (searchObject.get("channelId") == null && searchObject.get("locationId") == null && searchObject.get("implementEmployeeId") == null) {
//                    if (key != null) {
//                        parameterList.or("transCode", key, "like", "picture1FilePath", key, "like");
//                    }
//                    if (fromDate != null) {
//                        try {
//                            Date date1 = simpleDateFormat.parse(fromDate);
//                            parameterList.add("beginDate", date1, ">=");
//                        } catch (ParseException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                    if (toDate != null) {
//                        try {
//                            Date date2 = simpleDateFormat.parse(toDate);
//                            parameterList.add("beginDate", date2, "<=");
//                        } catch (ParseException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                    List<CustomerCareInformation> list = dataService.getListOption(CustomerCareInformation.class, parameterList);
//                    if (list != null) {
//                        lists.addAll(list);
//                    }
//                }
//
//            }
//            for(CustomerCareInformation careInformation : lists){
//            	careInformation.setChannels(careInformation.getPoss().getChannels());
//            	careInformation.setMcps(careInformation.getMcpDetailss().getMcps());
//            }
//            // parameterList.add("implementEmployees.id", 0);  
//            MsalesResults<CustomerCareInformation> listMCPs = new MsalesResults<CustomerCareInformation>();
//            listMCPs.setCount(Long.parseLong(lists.size()+""));
//            if(page.getPageNo() == 1){
//            	if(page.getRecordsInPage() == 10){
//            		if(lists.size() <= 10){
//            			listMCPs.setContentList(lists);
//            		}else{
//            			listMCPs.setContentList(lists.subList(0, 10));
//            		}
//            	}else{
//            		if(lists.size() <= page.getRecordsInPage()){
//            			listMCPs.setContentList(lists);
//            		}else{
//            			listMCPs.setContentList(lists.subList(0, page.getRecordsInPage()));
//            		}
//            	}
//            }else{
//            	if(page.getRecordsInPage() == 10){
//            		if(lists.size() <= 10){
//            			
//            		}else{
//            			if(lists.size()/10 + 1 == page.getPageNo()){
//            				listMCPs.setContentList(lists.subList((page.getPageNo() - 1)*page.getRecordsInPage(), lists.size()));
//            			}else if(lists.size()/10 + 1 >= page.getPageNo()){
//            				listMCPs.setContentList(lists.subList((page.getPageNo() - 1)*page.getRecordsInPage(), page.getPageNo()*page.getRecordsInPage()));
//            			}else{
//            				
//            			}
//            		}
//            	}else{
//            		if(lists.size() <= page.getRecordsInPage()){
//            			
//            		}else{
//            			if(lists.size()/page.getRecordsInPage() + 1 == page.getPageNo()){
//            				listMCPs.setContentList(lists.subList((page.getPageNo() - 1)*page.getRecordsInPage(), lists.size()));
//            			}else if(lists.size()/page.getRecordsInPage() + 1 >= page.getPageNo()){
//            				listMCPs.setContentList(lists.subList((page.getPageNo() - 1)*page.getRecordsInPage(), page.getPageNo()*page.getRecordsInPage()));
//            			}else{
//            				
//            			}
//            		}
//            	}
//            }
//            String[] strings = {"isActiveChannel","picture1FilePath","picture2FilePath","picture3FilePath","mcpDetailss"};
//            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listMCPs), strings);
//        } //jsonString null
//        else {
//            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
//                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
//        }
//    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_INFORMATION.ACTION_SEARCH_CUSTOMER_CARE_INFORMATION_ADMIN, method = RequestMethod.POST)
    public @ResponseBody String searchCustomerCareInformationAdmin(HttpServletRequest request) {
        //get List GoodsCategory from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList();
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
        
            List<CustomerCareInformation> lists = new ArrayList<CustomerCareInformation>();
            try {
                //searchObject from json not null
                String hql = "Select U from CustomerCareInformation as U where 1=1 ";
                if (searchObject != null) {
                    //Check from Date, toDate
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    if (searchObject.get("fromDate") != null) {
                        String fromDate = searchObject.get("fromDate").toString();
                        if (fromDate != null) {
                            try {
                                Date date1 = sDateFormat.parse(fromDate);
                                String fromDate2 = simpleDateFormat.format(date1);
                                Date date2 = simpleDateFormat.parse(fromDate2);
                                //parameterList.add("salesTransDate", date1, ">=");
                                hql += " and startCustomerCareAt >= '" + simpleDateFormat.format(date2) + "'";
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));

                            }
                        }
                    }

                    if (searchObject.get("toDate") != null) {
                        String toDate = searchObject.get("toDate").toString();
                        if (toDate != null) {
                            try {
                                Date date1 = sDateFormat.parse(toDate);
                                String toDate2 = simpleDateFormat.format(date1);
                                Date date2 = simpleDateFormat.parse(toDate2);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date2);
                                calendar.add(calendar.DAY_OF_MONTH, 1);
                                //parameterList.add("salesTransDate", date2, "<=");
                                hql += " and startCustomerCareAt <= '" + simpleDateFormat.format(calendar.getTime()) + "'";
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                            }
                        }
                    }

                    //check searchText
                    if (searchObject.get("searchText") != null) {
                        String key = searchObject.get("searchText").toString();
                        hql += " and transCode LIKE '%" + key + "%' ";
                    }
                    //Check implementEmployeeId;
                    if (searchObject.get("implementEmployeeId") != null) {
                        try {
                            int implementEmployeeId = Integer.parseInt(searchObject.get("implementEmployeeId").toString());
                            hql += " and implementEmployees.id = " + implementEmployeeId;
                            lists = dataService.executeSelectHQL(CustomerCareInformation.class, hql, false, 0, 0);
                        } catch (Exception ex) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, ex.getMessage()));
                        }
                    } else {
                        String hql2 = "select U "
                                + "from User as U,UserRoleChannel as URC "
                                + "where U.id=URC.users.id";
                        if (searchObject.get("channelId") != null) {
                            try {
                                int channelId = Integer.parseInt(searchObject.get("channelId").toString());
                                hql2 += " and URC.channels.id = " + channelId;
                            } catch (Exception ex) {
                                if (ex instanceof ConstraintViolationException) {
                                    return MsalesJsonUtils.jsonValidate(ex);
                                }//else
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, ex.getMessage()));
                            }
                        }
                        if (searchObject.get("locationId") != null) {
                            try {
                                int locationId = Integer.parseInt(searchObject.get("locationId").toString());
                                hql2 += " and ( U.locations.id = " + locationId + " or U.locations.parents.id = " + locationId + " or U.locations.parents.parents.id = " + locationId + " ) ";
                            } catch (Exception e) {
                                if (e instanceof ConstraintViolationException) {
                                    return MsalesJsonUtils.jsonValidate(e);
                                }//else
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                            }
                        }
                        hql2 += " group by U.id";
                        if (searchObject.get("locationId") != null || searchObject.get("channelId") != null) {
                            List<User> list = dataService.executeSelectHQL(User.class, hql2, false, 0, 0);
                            String string1 = "";
                            if (!list.isEmpty()) {
                                for (User user : list) {
                                    string1 += " or implementEmployees.id = " + user.getId();
                                }
                                hql += " and ( " + string1.substring(3) + ") ";
                            }
                        }
                        lists = dataService.executeSelectHQL(CustomerCareInformation.class, hql, false, 0, 0);
                    }
                }
            } catch (Exception e) {
                lists = new ArrayList<CustomerCareInformation>();
            }
            
            for(CustomerCareInformation careInformation : lists){
            	careInformation.getImplementEmployees().setName(careInformation.getImplementEmployees().getLastName() + " " + careInformation.getImplementEmployees().getFirstName());
            	if(careInformation.getImplementEmployees().getLocations().getLocationType() == 1){
            		careInformation.getImplementEmployees().setLocation(careInformation.getImplementEmployees().getLocations().getName());
            	}else if(careInformation.getImplementEmployees().getLocations().getParents().getLocationType() == 1){
            		careInformation.getImplementEmployees().setLocation(careInformation.getImplementEmployees().getLocations().getParents().getName());
            	}else{
            		careInformation.getImplementEmployees().setLocation(careInformation.getImplementEmployees().getLocations().getParents().getParents().getName());
            	}
            	careInformation.setChannels(careInformation.getPoss().getChannels());
            	careInformation.setMcps(careInformation.getMcpDetailss().getMcps());
            	//Check xem bán hàng hay chưa.
            	//Xử lí ngày:
            	ParameterList parameterList2 = new ParameterList();
            	//Xử lí kiểu bán hàng
            	parameterList2.add("transType", 2);
            	//Xử lí cho điểm bán hàng.
            	List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("poss.id", careInformation.getPoss().getId()));
            	
            	if(salesStocks.isEmpty()){
            		return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_NO_HAVE_STOCK));
            	}
            	int stockOfPosId = salesStocks.get(0).getId();
            	parameterList2.add("toStocks.id", stockOfPosId);
            	//Xử lí nhân viên chăm sóc.
            	parameterList2.add("createdUser", careInformation.getImplementEmployees().getId());
            	//xử lí ngày giờ chăm sóc.
            	Date date = careInformation.getStartCustomerCareAt();
            	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            	String fDate = simpleDateFormat.format(date);
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
            	parameterList2.add("salesTransDate", fromDate, ">=");
            	parameterList2.add("salesTransDate", toDate, "<");
            	List<SalesTrans> salesTrans = dataService.getListOption(SalesTrans.class, parameterList2);
            	if(salesTrans.isEmpty()){
            		careInformation.setIsSold(0);
            	}else{
            		careInformation.setIsSold(1);
            	}
            }
            // parameterList.add("implementEmployees.id", 0);  
            MsalesResults<CustomerCareInformation> listMCPs = new MsalesResults<CustomerCareInformation>();
            listMCPs.setCount(Long.parseLong(lists.size()+""));
            if(page.getPageNo() == 1){
            	if(page.getRecordsInPage() == 10){
            		if(lists.size() <= 10){
            			listMCPs.setContentList(lists);
            		}else{
            			listMCPs.setContentList(lists.subList(0, 10));
            		}
            	}else{
            		if(lists.size() <= page.getRecordsInPage()){
            			listMCPs.setContentList(lists);
            		}else{
            			listMCPs.setContentList(lists.subList(0, page.getRecordsInPage()));
            		}
            	}
            }else{
            	if(page.getRecordsInPage() == 10){
            		if(lists.size() <= 10){
            			
            		}else{
            			if(lists.size()/10 + 1 == page.getPageNo()){
            				listMCPs.setContentList(lists.subList((page.getPageNo() - 1)*page.getRecordsInPage(), lists.size()));
            			}else if(lists.size()/10 + 1 >= page.getPageNo()){
            				listMCPs.setContentList(lists.subList((page.getPageNo() - 1)*page.getRecordsInPage(), page.getPageNo()*page.getRecordsInPage()));
            			}else{
            				
            			}
            		}
            	}else{
            		if(lists.size() <= page.getRecordsInPage()){
            			
            		}else{
            			if(lists.size()/page.getRecordsInPage() + 1 == page.getPageNo()){
            				listMCPs.setContentList(lists.subList((page.getPageNo() - 1)*page.getRecordsInPage(), lists.size()));
            			}else if(lists.size()/page.getRecordsInPage() + 1 >= page.getPageNo()){
            				listMCPs.setContentList(lists.subList((page.getPageNo() - 1)*page.getRecordsInPage(), page.getPageNo()*page.getRecordsInPage()));
            			}else{
            				
            			}
            		}
            	}
            }
            String[] strings = {"isActiveChannel","picture1FilePath","picture2FilePath","picture3FilePath","mcpDetailss"};
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listMCPs), strings);
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }
    
    @RequestMapping(value = MsalesConstants.MODULE.CUSTOMER_CARE_INFORMATION.ACTION_GET_LIST_CUSTOMER_CARE_INFORMATION_BY_POSID, method = RequestMethod.POST)
    public @ResponseBody String getListCustomerCareInformationByPOSId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null

        if (jsonString != null) {
            CustomerCareInformation customerCareInformation;
            try {
                //parse jsonString to a Channel Object
                customerCareInformation = MsalesJsonUtils.getObjectFromJSON(jsonString, CustomerCareInformation.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CustomerCareInformation from json not null
            if (customerCareInformation != null) {
              if(customerCareInformation.getPosId() != null){
                
                ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());
                parameterList.add("poss.id", customerCareInformation.getPosId());
                MsalesResults<CustomerCareInformation> list = dataService.getListOption(CustomerCareInformation.class, parameterList,true);
                for(CustomerCareInformation careInformation : list.getContentList()){
                	//Xét name
                	careInformation.getImplementEmployees().setName(careInformation.getImplementEmployees().getLastName() + " " + careInformation.getImplementEmployees().getFirstName());
                	if(careInformation.getImplementEmployees().getLocations().getLocationType() == 1){
                		careInformation.getImplementEmployees().setLocation(careInformation.getImplementEmployees().getLocations().getName());
                	}else if(careInformation.getImplementEmployees().getLocations().getParents().getLocationType() == 1){
                		careInformation.getImplementEmployees().setLocation(careInformation.getImplementEmployees().getLocations().getParents().getName());
                	}else{
                		careInformation.getImplementEmployees().setLocation(careInformation.getImplementEmployees().getLocations().getParents().getParents().getName());
                	}
                	//Xét MCP
                	careInformation.setMcps(careInformation.getMcpDetailss().getMcps());
                	//Check isSold
                	//Xử lí ngày:
                	ParameterList parameterList2 = new ParameterList();
                	//Xử lí kiểu bán hàng
                	parameterList2.add("transType", 2);
                	//Xử lí cho điểm bán hàng.
                	List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("poss.id", careInformation.getPoss().getId()));
                	
                	if(salesStocks.isEmpty()){
                		return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_NO_HAVE_STOCK));
                	}
                	int stockOfPosId = salesStocks.get(0).getId();
                	parameterList2.add("toStocks.id", stockOfPosId);
                	//Xử lí nhân viên chăm sóc.
                	parameterList2.add("createdUser", careInformation.getImplementEmployees().getId());
                	//xử lí ngày giờ chăm sóc.
                	Date date = careInformation.getStartCustomerCareAt();
                	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                	String fDate = simpleDateFormat.format(date);
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
                	parameterList2.add("salesTransDate", fromDate, ">=");
                	parameterList2.add("salesTransDate", toDate, "<");
                	List<SalesTrans> salesTrans = dataService.getListOption(SalesTrans.class, parameterList2);
                	if(salesTrans.isEmpty()){
                		careInformation.setIsSold(0);
                	}else{
                		careInformation.setIsSold(1);
                	}
                }
                String[] strings = {"isActiveChannel","picture1FilePath","picture2FilePath","picture3FilePath","mcpDetailss","poss"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list),strings);
            }else{
            	return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_POS_ID_NULL));
            }
            }//goods from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

}
