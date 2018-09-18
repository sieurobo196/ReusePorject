package vn.itt.msales.channel.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.POSImg;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.Session;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.SearchPOS;

/**
 *
 * @author KhaiCH
 *
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.POS.NAME)
public class MsalesPOSController extends CsbController {

    /**
     * create a POS
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_CREATE_POS, method = RequestMethod.POST)
    public @ResponseBody
    String createPOS(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            POS pos;
            try {
                //parse jsonString to a POS Object
                pos = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POS.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //POS from json not null
            if (pos != null) {
                //validate info relation
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (pos.getLocationId() != null) {
                    Location location = dataService.getRowById(pos.getLocationId(), Location.class);
                    if (location == null) {
                        hashErrors.put("Location", "ID = " + pos.getLocationId() + " không tồn tại.");
                    }
                }
                
                if (pos.getChannelId() != null) {
                    Channel channel = dataService.getRowById(pos.getChannelId(), Channel.class);
                    if (channel == null) {
                        hashErrors.put("Channel", "ID = " + pos.getChannelId() + " không tồn tại.");
                    }
                }
                if (pos.getStatusId() != null) {
                    Status status = dataService.getRowById(pos.getStatusId(), Status.class);
                    if (status == null) {
                        hashErrors.put("Status", "ID = " + pos.getStatusId() + " không tồn tại.");
                    }
                }
                if (pos.getCreatedUser() != null) {
                    User user = dataService.getRowById(pos.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + pos.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                
                if (pos.getIsActive() == null) {
                    pos.setIsActive(1);
                }

                //tao posCode
                if (pos.getLocationId() != null) {
                    Location location = dataService.getRowById(pos.getLocationId(), Location.class);
                    
                    String posCode = "";
                    if (location.getLocationType() == 3) {
                        Location location2 = location.getParents();
                        Location location3 = location.getParents().getParents();
                        if (location3.getId() == 4) {//HCM
                            posCode += location3.getCode() + location2.getCode() + location.getCode();
                        } else {
                            posCode = location.getCode();
                        }
                    } else {
                        LinkedHashMap<String, String> hashErrors2 = new LinkedHashMap<>();
                        hashErrors2.put("Location", "Địa điểm với id = " + pos.getLocationId() + " không phải là cấp phường xã");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.JSON_VALUE_INVALID, hashErrors2));
                    }
                    ParameterList parameterList2 = new ParameterList("locations.id", pos.getLocationId(), 0, 0);
                    parameterList2.add("createdUser", pos.getCreatedUser());
                    List<POS> listPOS = dataService.getListOption(POS.class, parameterList2);
                    String posCode2 = "";
                    
                    if (listPOS.isEmpty()) {
                        posCode += "0001";
                        String string = posCode.substring(0, 3);
                        String string2 = posCode.substring(3, 6);
                        String string3 = posCode.substring(6, 10);
                        posCode2 = string + "_" + string2 + "_" + string3 + "_" + "0001";
                    } else {
                        int codeString = 0;
                        String string = "";
                        String string2 = "";
                        for (POS pos2 : listPOS) {
                            string = pos2.getPosCode().substring(13);
                            int i = Integer.parseInt(string);
                            if (i > codeString) {
                                codeString = i;
                            }
                        }
                        codeString += 1;
                        if (codeString < 10) {
                            string2 += "000" + codeString;
                        } else if ((10 <= codeString) && codeString < 100) {
                            string2 += "00" + codeString;
                        } else if (100 <= codeString && codeString < 1000) {
                            string2 += "0" + codeString;
                        } else {
                            string2 += codeString;
                        }
                        posCode2 = listPOS.get(0).getPosCode().substring(0, 13) + string2;
                    }
                    pos.setPosCode(posCode2);
                }
                
                try {
                    //save POS to DB
                    int ret = dataService.insertRow(pos);
                    //save POS success
                    if (ret > 0) {
                        //create a salesStock
                        SalesStock salesStock = new SalesStock();
                        salesStock.setPosId(ret);
                        salesStock.setCreatedUser(pos.getCreatedUser());
                        salesStock.setStatusId(1);//1 hoat dong
                        try {
                            dataService.insertRow(salesStock);
                        } catch (Exception e) {
                            //Error create SalesStock
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.SQL_INSERT_FAIL));
                        }
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save POS failed
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
            } //POS from json null
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
     * update a POS
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_UPDATE_POS, method = RequestMethod.POST)
    public @ResponseBody
    String updatePOS(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            POS pos;
            try {
                //parse jsonString to a POS Object
                pos = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POS.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //POS from json not null
            if (pos != null) {
                if (pos.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = pos.getId();
                //validate info relation
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (pos.getLocationId() != null) {
                    Location location = dataService.getRowById(pos.getLocationId(), Location.class);
                    if (location == null) {
                        hashErrors.put("Location", "ID = " + pos.getLocationId() + " không tồn tại.");
                    }
                }
                
                if (pos.getChannelId() != null) {
                    Channel channel = dataService.getRowById(pos.getChannelId(), Channel.class);
                    if (channel == null) {
                        hashErrors.put("Channel", "ID = " + pos.getChannelId() + " không tồn tại.");
                    }
                }
                if (pos.getStatusId() != null) {
                    Status status = dataService.getRowById(pos.getStatusId(), Status.class);
                    if (status == null) {
                        hashErrors.put("Status", "ID = " + pos.getStatusId() + " không tồn tại.");
                    }
                }
                
                if (pos.getUpdatedUser() != null) {
                    User user = dataService.getRowById(pos.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + pos.getUpdatedUser() + " không tồn tại.");
                    }
                }
                
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                
                try {
                    //get rootPOS
                    POS rootPOS = dataService.getRowById(pos.getId(), POS.class);
                    if (rootPOS == null) {
                        return MsalesJsonUtils.notExists(POS.class, id);
                    }

                    //set lai isActive/channelCode
                    pos.setPosCode(rootPOS.getPosCode());
                    pos.setIsActive(rootPOS.getIsActive());

                    //update POS to DB
                    int ret = dataService.updateSync(pos);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(POS.class, id);
                    }
                    //update success
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
            } //POS from json null
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
     * delete a POS
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_DELETE_POS, method = RequestMethod.POST)
    public @ResponseBody
    String deletePOS(HttpServletRequest request
    ) {
        
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            //not login
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.ERROR_LOGIN));
        }
        int userId = session.getUserId();

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null

        if (jsonString != null) {
            POS pos;
            try {
                //parse jsonString to a POS Object
                pos = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POS.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //POS from json not null
            if (pos != null) {
                if (pos.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                
                int id = pos.getId();
                
                if (pos.getDeletedUser() != null) {
                    User user = dataService.getRowById(pos.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, pos.getDeletedUser());
                    }
                }
                try {
                    //delete POS from DB
                    int ret = dataService.deleteSynch(pos);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(POS.class, id);
                    }
                    //update delete POS success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update delete POS failed
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
            } //POS from json null
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
     * get a POS info
     *
     * @param request is a HttpServletRequest
     * @return string json include POS info
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_GET_POS, method = RequestMethod.POST)
    public @ResponseBody
    String getPOS(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null

        if (jsonString != null) {
            POS pos;
            try {
                //parse jsonString to a POS Object
                pos = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POS.class
                );
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //POS from json not null
            if (pos != null) {
                if (pos.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                
                int id = pos.getId();
                pos = dataService.getRowById(pos.getId(),
                        POS.class);
                //POS not null
                if (pos != null) {
                    User user = dataService.getRowById(pos.getCreatedUser(), User.class);
                    pos.setNameNVCS(user.getLastName() + " " + user.getFirstName());
                    String[] strings = {"createdAt"};
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, pos), strings);
                } //POS null
                else {
                    return MsalesJsonUtils.notExists(POS.class, id);
                }
            } //POS from json null
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
     * get List all POS
     *
     * @param request is a HttpServletRequest
     * @return string json include List POS
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_GET_LIST_POS, method = RequestMethod.POST)
    public @ResponseBody
    String getListPOS(HttpServletRequest request) {
        //get List POS from DB
        //MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList();
        parameterList.setOrder("name");
        
        MsalesResults<POS> list = dataService.getListOption(POS.class, parameterList, true);
        String[] strings = {"statuss", "locations", "channels", "address", "hierarchy", "ownerName", "street",
            "birthday", "owerCode", "owerCodeDate", "owerCodeLocation", "tel", "mobile", "otherTel", "fax", "email",
            "website", "gpkd", "gpkdDate", "gpkdLocation", "note", "lat", "lng", "isActive", "beginAt", "endAt", "createdAt", "createdUser"};
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), strings);
        
    }

    /**
     * get List POS by Channel Id
     *
     * @param request is a HttpServletRequest
     * @return string json include List Channel
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_GET_LIST_POS_BY_CHANNEL_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListPOSByChannelId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            POS pos;
            
            try {
                //parse jsonString to a POS Object
                pos = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POS.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //POS from json not null
            if (pos != null) {
                
                if (pos.getChannelId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("POS", "channelId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                //get List POS by Channel id
                ParameterList parameterList = new ParameterList("channels.id", pos.getChannelId(), page.getPageNo(), page.getRecordsInPage());
                MsalesResults<POS> list = dataService.getListOption(POS.class, parameterList, true);
                String[] strings = {"createdUser", "createdAt"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list), strings);
            } //POS from json null
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
     * get List POS by Location Id
     *
     * @param request is a HttpServletRequest
     * @return string json include List POS
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_GET_LIST_POS_BY_LOCATION_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListPOSByLocationId(HttpServletRequest request
    ) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            POS pos;
            try {
                //parse jsonString to a POS Object
                pos = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POS.class
                );
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //POS from json not null
            if (pos != null) {
                if (pos.getLocationId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("POS", "locationId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List POS by locationId                    
                ParameterList parameterList = new ParameterList("locations.id", pos.getLocationId(), page.getPageNo(), page.getRecordsInPage());
                MsalesResults<POS> list = dataService.getListOption(POS.class, parameterList, true);
                String[] strings = {"createdUser", "createdAt"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list), strings);
            } //channelSalePointInformation from json null
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
     * DuanND: get Cb List POS by locationId
     *
     * @param request is jsonString have locationId
     * @return is jsonString have info of POS
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_GET_CB_LIST_POS_BY_LOCATION_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListPOSByLocationId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //   MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            POS channelSalePointInformation;
            try {
                //parse jsonString to a POS Object
                channelSalePointInformation = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POS.class
                );
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channelSalePointInformation from json not null
            if (channelSalePointInformation != null) {
                if (channelSalePointInformation.getLocationId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                    hashErrors.put("POS", "locationId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List POS by locationId                    
                ParameterList parameterList = new ParameterList("locations.id", channelSalePointInformation.getLocationId());
                MsalesResults<POS> list = dataService.getListOption(POS.class, parameterList, true);
                String[] strings = {"channels", "address", "street", "locations", "statuss", "hierarchy", "ownerName",
                    "birthday", "owerCode", "owerCodeDate", "owerCodeLocation", "tel", "mobile", "otherTel",
                    "fax", "email", "website", "gpkd", "gpkdDate", "gpkdLocation", "note", "lat", "lng",
                    "isActive", "beginAt", "endAt", "createdAt", "createdUser"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list), strings);
            } //channelSalePointInformation from json null
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
     * search POS
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_SEARCH_POS_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String searchPOS(HttpServletRequest request) {
        
        ParameterList parameterList;

        //get List Channel from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        if (page != null) {
            parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());
        } else {
            parameterList = new ParameterList();
        }

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

            //searchObject from json not null
            if (searchObject != null) {
                if (searchObject.get("searchText") != null) {
                    String key = searchObject.get("searchText").toString();
                    parameterList.or("posCode", key, "like", "name", key, "like");
                }
                if (searchObject.get("channelId") != null) {
                    try {
                        int channelId = Integer.parseInt(searchObject.get("channelId").toString());
                        parameterList.add("channels.id", channelId);
                    } catch (Exception ex) {
                        //error parse statusId
                    }
                }
                if (searchObject.get("locationId") != null) {
                    try {
                        int locationId = Integer.parseInt(searchObject.get("locationId").toString());
                        parameterList.add("locations.id", locationId);
                    } catch (Exception ex) {
                        //error parse statusId
                    }
                }
                if (searchObject.get("statusId") != null) {
                    try {
                        int statusId = Integer.parseInt(searchObject.get("statusId").toString());
                        parameterList.add("statuss.id", statusId);
                    } catch (Exception ex) {
                        //error parse statusId
                    }
                }
                if (searchObject.get("createdUser") != null) {
                    try {
                        int createdUser = Integer.parseInt(searchObject.get("createdUser").toString());
                        parameterList.add("createdUser", createdUser);
                    } catch (Exception ex) {
                        //error parse statusId
                    }
                }
                if (searchObject.get("hierarchy") != null) {
                    try {
                        int hierarchy = Integer.parseInt(searchObject.get("hierarchy").toString());
                        parameterList.add("hierarchy", hierarchy);
                    } catch (Exception ex) {
                        //error parse statusId
                    }
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
                if (searchObject.get("fromDate") != null) {
                    String fromDate = searchObject.get("fromDate").toString();
                    if (fromDate != null) {
                        try {
                            Date date1 = simpleDateFormat.parse(fromDate);
                            parameterList.add("beginAt", date1, ">=");
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                if (searchObject.get("toDate") != null) {
                    String toDate = searchObject.get("toDate").toString();
                    if (toDate != null) {
                        try {
                            Date date2 = simpleDateFormat.parse(toDate);
                            parameterList.add("beginAt", date2, "<=");
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                
            }
            MsalesResults<POS> lists = dataService.getListOption(POS.class, parameterList, true);
            String[] strings = {"birthday", "owerCode", "owerCodeDate", "owerCodeLocation", "otherTel",
                "fax", "email", "website", "gpkd", "gpkdDate", "gpkdLocation", "note", "lat", "lng",
                "isActive", "beginAt", "endAt", "ownerCodeDate", "ownerCodeLocation", "createdAt", "createdUser"};
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, lists), strings);
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * DuanND: update POS Administrator for xét duyệt điểm bán hàng
     *
     * @param request is jsonString have id, hierarchy để xét duyệt điểm bán
     * hàng
     * @return code, status.
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_UPDATE_POS_ADMINISTRATOR, method = RequestMethod.POST)
    public @ResponseBody String activePOS(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            POS pos;
            try {
                //parse jsonString to a POS Object
                pos = MsalesJsonUtils.getObjectFromJSON(jsonString,POS.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channelSalePointInformation from json not null
            if (pos != null) {
                if (pos.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = pos.getId();
                //validate info relation
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                POS pos2 = dataService.getRowById(id, POS.class);
                if (pos2 == null) {
                    hashErrors.put("POS", MsalesValidator.MCP_POS_NOT_EXIST + pos.getId());
                }
                if (pos.getUpdatedUser() != null) {
                    User user = dataService.getRowById(pos.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", MsalesValidator.MCP_USER_NOT_EXIST + pos.getUpdatedUser());
                    }
                }
                
                if (pos.getHierarchy() != null) {
                    pos2.setHierarchy(pos.getHierarchy());
                } else {
                    hashErrors.put("POS", MsalesValidator.MCP_POS_HIERATRY_NULL);
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                pos2.setUpdatedUser(pos.getUpdatedUser());
                pos2.setUpdatedAt(new Date());
                if(pos2.getEndAt() == null){
                	try {
						pos2.setEndAt(simpleDateFormat.parse("31/12/2020 23:59:59"));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
                if(pos.getBeginAt() == null){
                	pos.setBeginAt(new Date());
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                
                try {
                    //update channelSalePointInformation to DB
                    int ret = dataService.updateRow(pos2);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(POS.class, id);
                    }
                    //update success
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
            } //POS from json null
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
     * DuanND search POS
     *
     * @param request jsonString have information to search POS
     * @return POSs
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_SEARCH_POS_NEW, method = RequestMethod.POST)
    public @ResponseBody String searchPOSNew(HttpServletRequest request) {
        //get List GoodsCategory from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            LinkedHashMap<String, Object> searchObject;
            try {
                //parse jsonString to a search Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString,LinkedHashMap.class);
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            String hql = "Select P from POS as P where hierarchy = 0 and deletedUser = 0 ";
            String hql2 = "Select P.id as id from POS as P where hierarchy = 0 and deletedUser = 0 ";
            //searchObject from json not null
            if (searchObject != null) {
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
                            hql += " and createdAt >= '" + simpleDateFormat.format(date2)+"'";
                            hql2 += " and createdAt >= '" + simpleDateFormat.format(date2)+"'";
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
                            hql += " and createdAt <= '" + simpleDateFormat.format(calendar.getTime())+"'";
                            hql2 += " and createdAt <= '" + simpleDateFormat.format(calendar.getTime())+"'";
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                      	  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
                        }
                    }
                }
                // 
                //check searchText
                if(searchObject.get("searchText") != null){
              	  String key = searchObject.get("searchText").toString();
              	  if(key != null && !key.isEmpty() && !key.trim().isEmpty() && !key.equals("")){
              		  hql += " and ( address LIKE '%" + key + "%'" + " or name LIKE '%" + key + "%'" + " or tel LIKE '%" + key + "%'" + " ) ";
              		 hql2 += " and ( address LIKE '%" + key + "%'" + " or name LIKE '%" + key + "%'" + " or tel LIKE '%" + key + "%'" + " ) ";
              	  }
                }
                //Check createdUser
                if(searchObject.get("createdUser") != null){
               	  try{
                   	  int createdUser = Integer.parseInt(searchObject.get("createdUser").toString());
                   	  if(createdUser != 0){
                   		 hql += " and createdUser = " + createdUser;
                   		 hql2 += " and createdUser = " + createdUser;
                   	  }else{
                   		  if(searchObject.get("employerList") != null){
                   			  ArrayList<LinkedHashMap<String, Object>> employerList = (ArrayList<LinkedHashMap<String, Object>>) searchObject.get("employerList");
                   				  String string = "";
                   				  if(employerList.size() > 1){
                   					  for(int i = 1; i < employerList.size(); i++){
                       					  int id = Integer.parseInt(employerList.get(i).get("id").toString());
                       					  if(id != 0){
                                             		  string += " or createdUser = " + id;
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
            
            List<POS> count = new ArrayList<POS>();
            List<POS> lists = new ArrayList<POS>();
            hql += " order by createdAt desc";
            try{
            	count = dataService.executeSelectHQL(POS.class, hql2, true, 0, 0);
                lists = dataService.executeSelectHQL(POS.class, hql, false, page.getPageNo(), page.getRecordsInPage());
            }catch(Exception ex){
            	return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, ex.getMessage()));
            }
            for(POS pos : lists){
            	//Xét User tạo điểm BH
            	User user = dataService.getRowById(pos.getCreatedUser(), User.class);
            	if(user != null){
            		pos.setUserCreated(user);
            	}
            	//Xét địa chỉ quận huyện của điểm bán hàng
            	if(pos.getLocations().getLocationType() == 3){
            		pos.setQuanHuyen(pos.getLocations().getParents().getName());
            		pos.setTinhThanh(pos.getLocations().getParents().getParents().getName());
            	}else if(pos.getLocations().getLocationType() == 2){
            		pos.setQuanHuyen(pos.getLocations().getName());
            		pos.setTinhThanh(pos.getLocations().getParents().getName());
            	}else{
            		pos.setTinhThanh(pos.getLocations().getName());
            	}
            	//Xét ảnh chụp cho điểm bán hàng
            	List<POSImg> posImgs = dataService.getListOption(POSImg.class, new ParameterList("poss.id", pos.getId()));
            	if(!posImgs.isEmpty()){
            		for(POSImg posImg : posImgs){
            			if(posImg.getPath() != null){
            				pos.setImage(posImg.getPath());
            				break;
            			}
            		}
            	}
            }
            MsalesResults<POS> listsPOS = new MsalesResults<POS>(lists, Long.parseLong(count.size() +""));
            String[] strings = {"birthday", "owerCode", "owerCodeDate", "owerCodeLocation", "otherTel",
                "fax", "email", "website", "gpkd", "gpkdDate", "gpkdLocation", "note", "lat", "lng",
                "isActive", "beginAt", "endAt"};
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listsPOS), strings);
            //return json;
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }
    
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_SEARCH_POS_ADMIN, method = RequestMethod.POST)
    public @ResponseBody
    String searchPOSAdmin(HttpServletRequest request) {
        //get List Channel from DB
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            SearchPOS searchObject;
            try {
                //parse jsonString to a search Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchPOS.class);
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //searchObject from json not null
            String hql = "Select P.id as id, P.posCode as code, P.name as name from POS as P "
                    + " JOIN P.channels as Channel "
                    + " JOIN P.statuss as Status ";
            hql += " where P.deletedUser = 0 AND Status.id=5 "; // status id = 5 --> hoat động
            hql += " AND Channel.companys.id = " +  request.getAttribute("companyId");
            if (searchObject.getPosListSelected() != null && searchObject.getPosListSelected().length > 0) {
                hql += " and P.id in (";
                int i = 0;
                for (String s : searchObject.getPosListSelected()) {
                    hql += Integer.parseInt(s);
                    if (i < (searchObject.getPosListSelected().length - 1)) {
                        hql += ",";
                    }
                    i++;
                }
                hql += ")";
            } else if (searchObject.getPosList() != null && searchObject.getPosList().length > 0) {
                hql += " and P.id in (";
                int i = 0;
                for (String s : searchObject.getPosList()) {
                    hql += Integer.parseInt(s);
                    if (i < (searchObject.getPosList().length - 1)) {
                        hql += ",";
                    }
                    i++;
                }
                hql += ")";
            } else {
                if (searchObject.getLocationId() != null) {
                	Location location = dataService.getRowById(searchObject.getLocationId(), Location.class);
                	
                	if(location != null){
                		int id = location.getId();
                		if(location.getLocationType() == 1){
                			hql += " and (P.locations.id = " + id
                                    + " or P.locations.parents.id = " + id + " or P.locations.parents.parents.id = " + id + ") ";
                		}else if(location.getLocationType() == 2){
                			hql += " and (P.locations.id = " + id
                                    + " or P.locations.parents.id = " + id + ") ";
                		}else if(location.getLocationType() == 3){
                			hql += " and P.locations.id = " + id ;
                		}
                	}else{
                		hql += " and P.locations.id = " + -1;
                                
                	}
                    
                }
                if (searchObject.getSearchText() != null) {
                    hql += " and ( P.posCode LIKE '%" + searchObject.getSearchText() + "%' or P.name LIKE '%" + searchObject.getSearchText() + "%' or P.address LIKE '%" + searchObject.getSearchText() + "%' ) ";
                }
            }
        //    hql += " order by P.name";
            List<HashMap> lists = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            //Xử lí ngày giờ trong ngày
            SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
			try {
				date = sDateFormat.parse(searchObject.getBeginDate());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            String fromDate = simpleDateFormat.format(date);
            String toDate = simpleDateFormat.format(date) + " 23:59:59";
            String hqlX = "Select MCPDetails.poss.id as id from MCPDetails as MCPDetails where deletedUser = 0 and poss.deletedUser = 0 and poss.statuss.id = 5"
            		+ " and finishTime >= '" + fromDate +"'"
            		+ " and finishTime <= '" + toDate + "'"
            		+ " and mcps.deletedUser = 0"
            		+ " and poss.channels.companys.id = " +  request.getAttribute("companyId")
            		+ " group by poss.id";
            List<HashMap> listPOSId = dataService.executeSelectHQL(HashMap.class, hqlX, true, 0, 0);
            MsalesResults<POS> listPOS = new MsalesResults<POS>();
            List<POS> posList = new ArrayList<POS>();
            for(HashMap hashMap : lists){
            	int id1 = (Integer) hashMap.get("id");
            	POS pos = new POS();
            	boolean bool = true;
            	for(HashMap hMap : listPOSId){
            		int id2 = (Integer) hMap.get("id");
            		if(id1 == id2){
            			bool = false;
            			break;
            		}
            	}
            	if(bool){
            		pos.setId((Integer) hashMap.get("id"));
            		pos.setName((String) hashMap.get("name"));
            		pos.setPosCode((String) hashMap.get("code"));
            		//listPOS.getContentList().add(pos);
            		posList.add(pos);
            	}
            }
            listPOS.setContentList(posList);
            listPOS.setCount(Long.parseLong(posList.size() + ""));
            // MsalesResults<POS> lists = dataService.getListOption(POS.class, parameterList, true);
//            String[] strings = {"channels", "statuss", "hierarchy", "ownerName", "birthday", "owerCode", "owerCodeDate", "owerCodeLocation", "otherTel",
//                "fax", "email", "website", "gpkd", "gpkdDate", "gpkdLocation", "note", "lat", "lng",
//                "isActive", "beginAt", "endAt", "parents", "locationType", "ownerCodeDate", "ownerCodeLocation", "tel", "mobile", "createdAt", "createdUser", "ownerCode"};
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listPOS));
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }
    
    
    @RequestMapping(value = MsalesConstants.MODULE.POS.ACTION_GET_POS_ORDER, method = RequestMethod.POST)
    public @ResponseBody String getPOSOrder(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            POS pos;
            try {
                //parse jsonString to a POS Object
                pos = MsalesJsonUtils.getObjectFromJSON(jsonString,POS.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //POS from json not null
            if (pos != null) {
                if (pos.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                
                int id = pos.getId();
                pos = dataService.getRowById(pos.getId(), POS.class);
                //POS not null
                if (pos != null) {
                	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                	String hqlUser = "Select MCP.implementEmployees as user"
                			+ " from MCPDetails as MCP where MCP.deletedUser = 0 and MCP.poss.id = "+id;
                	String hql = hqlUser + " and MCP.mcps.beginDate >= '" + simpleDateFormat.format(new Date()) + " 00:00:00'"
                			+ " and MCP.mcps.beginDate < '" + simpleDateFormat.format(new Date()) + " 23:59:59'"
                			+ " and MCP.mcps.deletedUser = 0";
                	hqlUser += " order by MCP.createdAt desc";	
                	List<HashMap> listHashMaps = dataService.executeSelectHQL(HashMap.class, hql, true, 1, 1);
                	if(listHashMaps.isEmpty()){
                		listHashMaps = dataService.executeSelectHQL(HashMap.class, hqlUser, true, 1, 1);
                	}
                	if(!listHashMaps.isEmpty()){
                		User user = (User) listHashMaps.get(0).get("user");
                        pos.setNameNVCS(user.getLastName() + " " + user.getFirstName());
                        pos.setCreatedUser(user.getId());
                	}
                    
                    String[] strings = {"createdAt"};
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, pos), strings);
                } //POS null
                else {
                    return MsalesJsonUtils.notExists(POS.class, id);
                }
            } //POS from json null
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
