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
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelLocation;
import vn.itt.msales.entity.ChannelType;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;

/**
 *
 * @author KhaiCH
 *
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.CHANNEL.NAME, headers = {"Accept=application/json"}, produces = "application/json;charset=UTF-8")
public class MsalesChannelController extends CsbController {

    /**
     * create a Channel
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNEL.ACTION_CREATE_CHANNEL, method = RequestMethod.POST)
    public @ResponseBody
    String createChannel(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            Channel channel;
            try {
                //parse jsonString to a Channel Object
                channel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Channel.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //channel from json not null
            if (channel != null) {
                //validate info relation

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (channel.getParentId() != null) {
                    Channel parent = dataService.getRowById(channel.getParentId(), Channel.class);
                    if (parent == null) {
                        hashErrors.put("Channel parent", "ID = " + channel.getParentId() + " không tồn tại.");
                    }
                }
                if (channel.getCompanyId() != null) {
                    Company company = dataService.getRowById(channel.getCompanyId(), Company.class);
                    if (company == null) {
                        hashErrors.put("Company", "ID = " + channel.getCompanyId() + " không tồn tại.");
                    }
                }
                if (channel.getChannelTypeId() != null) {
                    ChannelType channelType = dataService.getRowById(channel.getChannelTypeId(), ChannelType.class);
                    if (channelType == null) {
                        hashErrors.put("ChannelType", "ID = " + channel.getChannelTypeId() + "không tồn tại.");
                    }else{
                      channel.setChannelTypeId(channelType.getId() + 1);
                    }
                } else {
                  Channel channelParent = dataService.getRowById(channel.getParentId(), Channel.class);
                  if (channelParent != null) {
                    channel.setChannelTypeId(channelParent.getChannelTypes().getId()+ 1);
                  }
                }
                if (channel.getStatusId() != null) {
                    Status status = dataService.getRowById(channel.getStatusId(), Status.class);
                    if (status == null) {
                        hashErrors.put("Status", "ID = " + channel.getStatusId() + "không tồn tại.");
                    }
                }

                if (channel.getCreatedUser() != null) {
                    User user = dataService.getRowById(channel.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + channel.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save channel to DB
                    int ret = dataService.insertRow(channel);

                    //save success
                    if (ret > 0) {
                        //create a salesStock
                        SalesStock salesStock = new SalesStock();
                        salesStock.setChannelId(ret);
                        salesStock.setCreatedUser(channel.getCreatedUser());
                        salesStock.setStatusId(1);//hoat dong
                        try {
                            dataService.insertRow(salesStock);
                        } catch (Exception e) {
                            //Error create SalesStock
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_INSERT_FAIL));
                        }
                        //create json and post to API
                        LinkedHashMap<String, Object> hashOk = new LinkedHashMap<>();
                        hashOk.put("channelId", ret);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, hashOk));
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
     * update a Channel
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNEL.ACTION_UPDATE_CHANNEL, method = RequestMethod.POST)
    public @ResponseBody
    String updateChannel(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Channel channel;
            try {
                //parse jsonString to a Channel Object
                channel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Channel.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channel from json not null
            if (channel != null) {
                if (channel.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                //validate info relation
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (channel.getParentId() != null) {
                    if (channel.getId() == channel.getParentId()) {
                        hashErrors.put("Channel", "parentId không được trùng với id.");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.PARENT_SAME_CHILD, hashErrors));
                    }
                    Channel parent = dataService.getRowById(channel.getParentId(), Channel.class);
                    if (parent == null) {
                        hashErrors.put("Channel parent", "ID = " + channel.getParentId() + " không tồn tại.");
                    }
                }
                if (channel.getCompanyId() != null) {
                    Company company = dataService.getRowById(channel.getCompanyId(), Company.class);
                    if (company == null) {
                        hashErrors.put("Company", "ID = " + channel.getCompanyId() + " không tồn tại.");
                    }
                }
                if (channel.getChannelTypeId() != null) {
                    ChannelType channelType = dataService.getRowById(channel.getChannelTypeId(), ChannelType.class);
                    if (channelType == null) {
                        hashErrors.put("ChannelType", "ID = " + channel.getCompanyId() + " không tồn tại..");
                    }
                }
                if (channel.getStatusId() != null) {
                    Status status = dataService.getRowById(channel.getStatusId(), Status.class);
                    if (status == null) {
                        hashErrors.put("Status", "ID = " + channel.getStatusId() + " không tồn tại.");
                    }
                }
                if (channel.getUpdatedUser() != null) {
                    User user = dataService.getRowById(channel.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + channel.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //update Channel to DB
                    int ret = dataService.updateSync(channel);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(Channel.class, channel.getId());
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
            } //Channel from json null
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
     * get a Channel info
     *
     * @param request is a HttpServletRequest
     * @return string json include Channel info
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNEL.ACTION_GET_CHANNEL, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String getChannel(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null

        if (jsonString != null) {
            Channel channel;
            try {
                //parse jsonString to a Channel Object
                channel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Channel.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channel from json not null
            if (channel != null) {
                if (channel.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = channel.getId();
                //get channel from DB
                channel = dataService.getRowById(channel.getId(),
                        Channel.class);
                //channel not null
                if (channel != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, channel));
                } //channel null
                else {
                    return MsalesJsonUtils.notExists(Channel.class, id);
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
     * get a Channel info
     * <p>
     * @param request is a HttpServletRequest
     * <p>
     * @return string json include Channel info
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNEL.ACTION_GET_CHANNEL_PARENTS, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String getListChannelParents(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            Channel channel;
            try {
                //parse jsonString to a Channel Object
                channel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Channel.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //channel from json not null
            if (channel != null) {
                if (channel.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = channel.getId();
                //get channel from DB
                channel = dataService.getRowById(channel.getId(), Channel.class);
                //channel not null
                List<Channel> channelParentList = new ArrayList();
                channelParentList.add(channel);
                if (channel != null) {
                    while (channel.getParents() != null) {
                        channelParentList.add(channel.getParents());
                        channel = channel.getParents();
                    }
                    String[] ignoreProperties = {"isSalePoint", "address", "contactPersonName", "fax", "email", "lat", "lng", "note", "companys", "statuss"};
                    MsalesResults<Channel> channelResult = new MsalesResults<>();
                    channelResult.setContentList(channelParentList);
                    channelResult.setCount((long) channelParentList.size());
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, channelResult), ignoreProperties);
                } //channel null
                else {
                    return MsalesJsonUtils.notExists(Channel.class, id);
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
     * get List Channel by Comapny Id
     *
     * @param request is a HttpServletRequest
     * @return string json include List Channel
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNEL.ACTION_GET_LIST_CHANNEL_BY_COMPANY_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListChannelByCompanyId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            Channel channel;
            try {
                //parse jsonString to a Channel Object
                channel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Channel.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channel from json not null
            if (channel != null) {
                if (channel.getCompanyId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("Channel", "companyId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));

                }
                //get List Channel by Company id
                ParameterList parameterList = new ParameterList("companys.id", channel.getCompanyId(), page.getPageNo(), page.getRecordsInPage());
                MsalesResults<Channel> list = dataService.getListOption(Channel.class, parameterList, true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));
            } //Channel from json null
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
     * get List Channel by ChannelType Id
     *
     * @param request is a HttpServletRequest
     * @return string json include List Channel
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNEL.ACTION_GET_LIST_CHANNEL_BY_CHANNEL_TYPE_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListChannelByChannelTypeId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            Channel channel;
            try {
                //parse jsonString to a Channel Object
                channel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Channel.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channel from json not null
            if (channel != null) {
                if (channel.getChannelTypeId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("Channel", "channelTypeId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));

                }

                //get List Channel by channelType id
                ParameterList parameterList = new ParameterList("channelTypes.id", channel.getChannelTypeId(), page.getPageNo(), page.getRecordsInPage());
                MsalesResults<Channel> list = dataService.getListOption(Channel.class, parameterList, true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));
            } //Channel from json null
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
     * get List Channel by Parent Id
     *
     * @param request is a HttpServletRequest
     * @return string json include List Channel
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNEL.ACTION_GET_LIST_CHANNEL_BY_PARENT_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListChannelByParentId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            Channel channel;
            try {
                //parse jsonString to a Channel Object
                channel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Channel.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //channel from json not null
            if (channel != null) {
                //parentId from channel with correct Id
                if (channel.getParentId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("Channel", "parentId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                //get List Channel by parentId id
                ParameterList parameterList = new ParameterList("parents.id", channel.getParentId(), page.getPageNo(), page.getRecordsInPage());
                MsalesResults<Channel> list = dataService.getListOption(Channel.class, parameterList, true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));

            } //parentId from channel with incorrect Id 
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
     * get combobox channel by channelTypeId
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNEL.ACTION_GET_CB_CHANNEL_BY_CHANNEL_TYPE_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListChannelByChannelTypeId(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            Channel channel;
            try {
                //parse jsonString to a POS Object
                channel = MsalesJsonUtils.getObjectFromJSON(jsonString, Channel.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channelSalePointInformation from json not null
            if (channel != null) {
                // Location with correct parentId
                if (channel.getChannelTypeId() != null) {
                    // get List Location by parentId
                    ParameterList parameterList = new ParameterList("channelTypes.id", channel.getChannelTypeId());
                    parameterList.setOrder("name");
                    MsalesResults<Channel> lists = dataService.getListOption(Channel.class, parameterList, true);
                    String[] strings = {"locations", "statuss", "parents", "companys",
                        "statusType", "channelTypes", "code", "channelTypeId",
                        "isSalePoint", "address", "contactPersonName", "tel", "fax",
                        "email", "lat", "lng", "note"};
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, lists), strings);
                } // location from json with incorrect parentId
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED));
                }
            } // location from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } // jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }

    }


    /**
     * get Combobox List Channel By ParentId
     *
     * @param request
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNEL.ACTION_GET_CB_LIST_CHANNEL_BY_PARENT_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListChannelByParentId(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();

        //jsonString not null
        if (jsonString != null) {
            Channel channel;
            try {
                //parse jsonString to a POS Object
                channel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Channel.class
                );
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channelSalePointInformation from json not null
            if (channel != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // Location with correct parentId
                if (channel.getParentId() != null) {
                    // get List Location by parentId
                    ParameterList parameterList = new ParameterList("parents.id", channel.getParentId(), 0, 0);
                    parameterList.setOrder("name");

                    MsalesResults<Channel> lists = dataService.getListOption(Channel.class, parameterList, true);
//                    List<LinkedHashMap<String, String>> list2 = new ArrayList<LinkedHashMap<String, String>>();
//                    for (Channel channel1 : lists.getContentList()) {
//                        LinkedHashMap<String, String> u = new LinkedHashMap<String, String>();
//                        u.put("id", "" + channel1.getId());
//                        u.put("name", "" + channel1.getCode() + " - " + channel1.getName());
//                        list2.add(u);
//                    }
//                    MsalesResults<LinkedHashMap<String,String>> listCbResults = new MsalesResults<>();
//                    listCbResults.setCount(lists.getCount());
//                    listCbResults.setContentList(list2);
                    // list location not null
                    //  if (list != null) {
                    String[] filter = {"locations", "statuss", "parents", "companys",
                        "statusType", "channelTypes", "code", "channelTypeId",
                        "isSalePoint", "address", "contactPersonName", "tel", "fax",
                        "email", "lat", "lng", "note"};
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, lists), filter);
                } // location from json with incorrect parentId
                else {
                    hashErrors.put("Channel", "parentId không được null " + channel.getParentId());
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
            } // location from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } // jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }

    }

    /**
     * get Combobox List Channel By LocationId
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNEL.ACTION_GET_CB_LIST_CHANNEL_BY_LOCATION_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListChannelByLocationId(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            ChannelLocation channelLocation;
            try {
                // parse jsonString to a userRoleChannel Object
                channelLocation = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        ChannelLocation.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRoleChannel from json not null
            if (channelLocation != null) {
                // Channel from userRoleChannel with correct channelId
                if (channelLocation.getLocationId() != null) {
                    // get List userRoleChannel by channelId
                    String hql = "Select C from Channel as C, ChannelLocation as CL ";
                    hql += " where ( CL.locations.id = " + channelLocation.getLocationId() + " or CL.locations.parents.id = " + channelLocation.getLocationId()
                            + " or CL.locations.parents.parents.id = " + channelLocation.getLocationId() + ") ";
                    hql += " and C.channelTypes.id = 3 and C.id = CL.channels.id ";
                    hql += " group by C.id ";
                    List<Channel> lists = dataService.executeSelectHQL(Channel.class, hql, false, 0, 0);
                    MsalesResults<Channel> listChannels = new MsalesResults<>();
                    listChannels.setContentList(lists);
                    listChannels.setCount(Long.parseLong(lists.size() + ""));
                    String[] strings = {"code", "isSalePoint", "address", "contactPersonName", "tel", "fax", "email", "lat", "lng", "note",
                        "companys", "parents", "channelTypes", "statuss"};
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, listChannels), strings);
                } // channel from json with incorrect channelId
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED));
                }
            } // channel from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } // jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }
    }

    /**
     * search Channel
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNEL.ACTION_SEARCH_CHANNEL, method = RequestMethod.POST)
    public @ResponseBody
    String searchChannel(HttpServletRequest request) {
        //get List Channel from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

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
            String hql = "SELECT DISTINCT(U.id) as id,U.code as code, U.channelTypes as channelTypes, U.name as name, U.statuss as statuss, U.address as address, U.tel as tel"
                    + " FROM Channel as U,ChannelLocation as CL"
                    + " WHERE U.id=CL.channels.id";
            //searchObject from json not null
            if (searchObject != null) {
                if (searchObject.get("searchText") != null) {
                    String key = searchObject.get("searchText").toString();
                    // parameterList.or("name", key, "like", "code", key, "like");
                    hql += " and ( U.name LIKE '%" + key + "%'" + " or U.address LIKE '%" + key + "%' ) ";
                }
                if (searchObject.get("channelId") != null) {
                    try {
                        int statusId = Integer.parseInt(searchObject.get("channelId").toString());
                        //parameterList.add("parents.id", statusId);
                        hql += " and U.parents.id = " + statusId;

                    } catch (Exception ex) {
                        //error parse statusId
                    }
                }
                if (searchObject.get("locationId") != null) {
                    try {
                        String key = searchObject.get("locationId").toString();
                        int locationId = Integer.parseInt(key);
                        hql += " and CL.locations.id = " + locationId;
                    } catch (Exception ex) {
                        //error parse statusId
                    }

                }
//                
            }
            hql += " group by U.id";
            List<Channel> lists = dataService.executeSelectHQL(Channel.class, hql, true, page.getPageNo(), page.getRecordsInPage());

            MsalesResults<Channel> listChannel = new MsalesResults(lists, Long.parseLong(lists.size() + ""));

            // MsalesResults<Channel> lists = dataService.getListOption(Channel.class, parameterList, true);
            String[] filters = {"companys", "parents", "isSalePoint", "contactPersonName", "fax", "email", "lat", "lng", "note", "value", "statusTypes"};

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listChannel), filters);
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * ACTION_GET_LIST_CHANNEL
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNEL.ACTION_GET_LIST_CHANNEL, method = RequestMethod.POST)
    public @ResponseBody
    String getListChannel(HttpServletRequest request) {
        // get Lists Location from DB
//    	String json = request.getHeader("Authorization");
//        ParameterList parameterList3 = new ParameterList("token", json, "like");
//        List<Session> sessions = dataService.getListOption(Session.class, parameterList3);
//        if (sessions.isEmpty()) {
//            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.APP_USER_NO_LOGIN));
//        }
//        User user = dataService.getRowById(sessions.get(0).getUserId(), User.class);
        ParameterList parameterList = new ParameterList("companys.id", 1);
        MsalesResults<Channel> lists = dataService.getListOption(Channel.class, parameterList, true);
        // lists not null display list
        String[] strings = {"locations", "statuss", "parents", "companys",
            "statusType", "channelTypes", "code", "channelTypeId",
            "isSalePoint", "address", "contactPersonName", "tel", "fax",
            "email", "lat", "lng", "note"};
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                HttpStatus.OK, lists), strings);
    }

}
