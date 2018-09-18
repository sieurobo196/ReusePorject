/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.channel.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;

/**
 *
 * @author KhaiCH
 *
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.CHANNELTYPE.NAME)
public class MsalesChannelTypeController extends CsbController {

    /**
     * create a ChannelType
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELTYPE.ACTION_CREATE_CHANNEL_TYPE, method = RequestMethod.POST)
    public @ResponseBody
    String createChannelType(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            ChannelType channelType;
            try {
                //parse jsonString to a ChannelType Object
                channelType = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        ChannelType.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //channelType from json not null
            if (channelType != null) {
                //validate info relation

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (channelType.getParentId() != null) {
                    ChannelType parent = dataService.getRowById(channelType.getParentId(), ChannelType.class);
                    if (parent == null) {
                        hashErrors.put("ChannelType parent", "ID = " + channelType.getParentId() + " không tồn tại.");
                    }
                }
                if (channelType.getCreatedUser() != null) {
                    User user = dataService.getRowById(channelType.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + channelType.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                try {
                    //save channelType to DB
                    int ret = dataService.insertRow(channelType);
                    //save channelType success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save channelType failed
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
            } //channelType from json null
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
     * update a ChannelType
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELTYPE.ACTION_UPDATE_CHANNEL_TYPE, method = RequestMethod.POST)
    public @ResponseBody
    String updateChannelType(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            ChannelType channelType;
            try {
                //parse jsonString to a ChannelType Object
                channelType = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        ChannelType.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channelType from json not null
            if (channelType != null) {
                if (channelType.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = channelType.getId();
                //validate info relation
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (channelType.getParentId() != null) {
                    if (channelType.getId() == channelType.getParentId()) {
                        hashErrors.put("ChannelType", "parentId không thể trùng với id.");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.PARENT_SAME_CHILD, hashErrors));
                    }
                    ChannelType parent = dataService.getRowById(channelType.getParentId(), ChannelType.class);
                    if (parent == null) {
                        hashErrors.put("ChannelType parent", "ID = " + channelType.getParentId() + " không tồn tại.");
                    }
                }

                if (channelType.getUpdatedUser() != null) {
                    User user = dataService.getRowById(channelType.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + channelType.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                try {
                    //update channelType to DB
                    int ret = dataService.updateSync(channelType);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(ChannelType.class, id);
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
            } //ChannelType from json null
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
     * delete a ChannelType
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELTYPE.ACTION_DELETE_CHANNEL_TYPE, method = RequestMethod.POST)
    public @ResponseBody
    String deleteChannelType(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            ChannelType channelType;
            try {
                //parse jsonString to a ChannelType Object
                channelType = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        ChannelType.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //channelType from json not null
            if (channelType != null) {
                if (channelType.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                if (channelType.getDeletedUser() != null) {
                    User user = dataService.getRowById(channelType.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, channelType.getDeletedUser());
                    }
                }

                int id = channelType.getId();
                try {
                    //delete channelType from DB
                    int ret = dataService.deleteSynch(channelType);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(ChannelType.class, id);
                    }
                    //update delete channelType success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update delete channelType failed
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
            } //channelType from json null
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
     * get a ChannelType info
     *
     * @param request is a HttpServletRequest
     * @return string json include ChannelType info
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELTYPE.ACTION_GET_CHANNEL_TYPE, method = RequestMethod.POST)
    public @ResponseBody
    String getChannelType(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            ChannelType channelType;
            try {
                //parse jsonString to a ChannelType Object
                channelType = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        ChannelType.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channelType from json not null
            if (channelType != null) {
                //channelType from json with correct Id
                if (channelType.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = channelType.getId();
                //get channelType from DB
                channelType = dataService.getRowById(channelType.getId(),
                        ChannelType.class);
                //channelType not null
                if (channelType != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, channelType));
                } //channelType null
                else {
                    return MsalesJsonUtils.notExists(ChannelType.class, id);
                }
            } //channelType from json with incorrect Id 
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
     * Get List channle type
     * <p>
     * @param request is jsonString
     * <p>
     * @return a jsonString contains data of locations
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELTYPE.ACTION_GET_LIST_CHANNEL_TYPE, method = RequestMethod.POST)
    public @ResponseBody
    String getListChannelType(HttpServletRequest request) {
        ParameterList parameterList = new ParameterList(0, 0);
        MsalesResults<ChannelType> lists = dataService.getListOption(ChannelType.class, parameterList, true);
        // lists not null display list
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                HttpStatus.OK, lists));
    }

    /**
     * Get List channle type
     * <p>
     * @param request is jsonString
     * <p>
     * @return a jsonString contains data of locations
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELTYPE.ACTION_GET_LIST_CHANNEL_TYPE_BY_PARENT, method = RequestMethod.POST)
    public @ResponseBody
    String getListChannelTypeByParent(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            ChannelType channelType = null;
            try {
                channelType = MsalesJsonUtils.getObjectFromJSON(jsonString, ChannelType.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                Logger.getLogger(MsalesChannelTypeController.class.getName()).log(Level.SEVERE, null, ex);
                return MsalesJsonUtils.validateFormat(ex);

            }
            if (channelType != null) {
                if (channelType.getId() != null && channelType.getId() > 0) {
                    ParameterList parameterList = new ParameterList(0, 0);
                    parameterList.add("parents.id", channelType.getId(), ">");
                    MsalesResults<ChannelType> lists = dataService.getListOption(ChannelType.class, parameterList, true);
                    // lists not null display list
                    String[] jsonIgnore = {"parents", "note", "code"};
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, lists), jsonIgnore);
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }

        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.UNKNOW));
    }

    /**
     * get cb channelLocation by channelParent
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELTYPE.ACTION_GET_CHANNELLOCATION_BY_CHANNEL_PARENT, method = RequestMethod.POST)
    public @ResponseBody
    String getChannelTypeByChannelParent(HttpServletRequest request) {
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
            LinkedHashMap hashErrors = new LinkedHashMap();
            if (channelLocation != null) {
                if (channelLocation.getChannelId() == null) {
                    hashErrors.put("channelId", MsalesValidator.NOT_NULL);
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get channel parent
                int ChannelTypeParentId = 1;
                if (channelLocation.getChannelId() != 0) {
                    Channel channel = dataService.getRowById(channelLocation.getChannelId(), Channel.class);
                    if (channel != null) {
                        ChannelTypeParentId = channel.getChannelTypes().getId();
                    }
                }

                ParameterList parameterList = new ParameterList(1, 1);
                parameterList.add("parents.id", ChannelTypeParentId);
                parameterList.setOrder("id");
                List<ChannelType> list = dataService.getListOption(ChannelType.class, parameterList);
                if (!list.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, list.get(0)));
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
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
}
