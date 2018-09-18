package vn.itt.msales.user.controller;

import java.util.LinkedHashMap;

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
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;

/**
 *
 * @author DuanND 
 *
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.USER_ROLE_CHANNEL.NAME)
public class MsalesUserRoleChannelController extends CsbController {

    /**
     * Get userRoleChannel with id
     * @param request is jsonString have id to get info of userRoleChannel
     * @return a jsonString contains status, code and contents
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER_ROLE_CHANNEL.ACTION_GET_USER_ROLE_CHANNEL, method = RequestMethod.POST)
    public @ResponseBody String getUserRoleChannel(HttpServletRequest request) {
        // get jsonString from client request via Csb
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            UserRoleChannel userRoleChannel = new UserRoleChannel();

            try {
                // get Object userRoleChannel from jsonString
                userRoleChannel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        UserRoleChannel.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (userRoleChannel != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (userRoleChannel.getId() != null) {
                    // get info userRoleChannel with id
                    UserRoleChannel userRoleChannel2 = dataService.getRowById(
                            userRoleChannel.getId(), UserRoleChannel.class);
                    if (userRoleChannel2 != null) {
                        // return a jsonString include info of userRoleChannel
                    	//String[] strings = {""};
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, userRoleChannel2));
                    } else {
                        // object userRoleChannel == null
                        hashErrors.put("UserRoleChannel",MsalesValidator.DK_USER_ROLE_CHANNEL_ID_NOT_EXIST + userRoleChannel.getId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    // jsonString have id invalid
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
                }
            } else {
                // Object from jsonString null
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            // jsonString null
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * create UserRoleChannel
     *
     * @param request is jsonString have data to create UserRoleChannel
     * @return a jsonString include content, status and code
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER_ROLE_CHANNEL.ACTION_CREATE_USER_ROLE_CHANNEL, method = RequestMethod.POST)
    public @ResponseBody String createUserRoleChannel(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        // jsonString not null
        if (jsonString != null) {
            UserRoleChannel userRoleChannel = null;
            try {
                // parse jsonString to a userRoleChannel Object
                userRoleChannel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        UserRoleChannel.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRoleChannel from json not null
            if (userRoleChannel != null) {
                Channel channel = null;
                User user = null;
                UserRole userRole = null;
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (userRoleChannel.getChannelId() != null) {
                    // get channel with id from channelId
                    channel = dataService.getRowById(
                            userRoleChannel.getChannelId(), Channel.class);
                    if (channel == null) {
                        hashErrors.put("Channel",MsalesValidator.DK_CHANNEL_ID_NOT_EXIST + userRoleChannel.getChannelId());
                    }
                }
                if (userRoleChannel.getUserId() != null) {
                    // get user with id from userRoleChannel
                    user = dataService.getRowById(userRoleChannel.getUserId(), User.class);
                    if (user == null) {
                        hashErrors.put("User",MsalesValidator.MCP_USER_NOT_EXIST + userRoleChannel.getUserId());
                    }
                }
                if (userRoleChannel.getUserRoleId() != null) {
					// get channel with id from channelId
                    // get UserRole with UserRoleId
                    userRole = dataService.getRowById( userRoleChannel.getUserRoleId(), UserRole.class);
                    if (userRole == null) {hashErrors.put("UserRole",MsalesValidator.DK_USER_ROLE_ID_NOT_EXIST + userRoleChannel.getUserRoleId());
                    }
                }
                if(userRoleChannel.getCreatedUser() != null){
            		User userRole2 = dataService.getRowById(userRoleChannel.getCreatedUser(), User.class);
            		if(userRole2 == null){
            			hashErrors.put("UserRoleChannel",MsalesValidator.MCP_USER_NOT_EXIST + userRoleChannel.getCreatedUser());
            		}
            	}
                if (hashErrors.size() > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

				// channel, user and userRole is exist
                // save userRoleChannel to DB
                int ret = 0;
                try {
                    // insert new a user to database
                    ret = dataService.insertRow(userRoleChannel);
                } catch (Exception e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (e.getCause().getCause() instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }
                }

				// int ret = dataService.insertRow(userRoleChannel);
                // userRoleChannel from DB not null
                if (ret > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK,null));
                } // userRoleChannel from DB null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
            } // userRoleChannel from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } // jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * update UserRoleChannel
     *
     * @param request is JsonString to update UserRoleChannel
     * @return a jsonString
     */
    // @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.USER_ROLE_CHANNEL.ACTION_UPDATE_USER_ROLE_CHANNEL, method = RequestMethod.POST)
    public @ResponseBody String updateUserRoleChannel(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            UserRoleChannel userRoleChannel;
            try {
                // parse jsonString to a userRoleChannel Object
                userRoleChannel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        UserRoleChannel.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRoleChannel from json not null
            if (userRoleChannel != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // get user, userRole, channel from their Id
                if (userRoleChannel.getId() != null) {
                    UserRoleChannel userRole2 = dataService.getRowById(
                            userRoleChannel.getId(), UserRoleChannel.class);
                    if (userRole2 == null) {
                        hashErrors.put("UserRoleChannel",MsalesValidator.DK_USER_ROLE_CHANNEL_ID_NOT_EXIST + userRoleChannel.getId());
                    }

                    Channel channel = null;
                    User user = null;
                    UserRole userRole = null;
                    if (userRoleChannel.getChannelId() != null) {
                        // get channel with id from channelId
                        channel = dataService.getRowById(
                                userRoleChannel.getChannelId(), Channel.class);
                        if (channel == null) {
                            hashErrors.put("Channel",MsalesValidator.DK_CHANNEL_ID_NOT_EXIST + userRoleChannel.getChannelId());
                        }
                    }
                    if (userRoleChannel.getUserId() != null) {
                        // get user with id from userRoleChannel
                        user = dataService.getRowById( userRoleChannel.getUserId(), User.class);
                        if (user == null) {
                            hashErrors.put("User",MsalesValidator.MCP_USER_NOT_EXIST + userRoleChannel.getUserId());
                        }
                    }
                    if (userRoleChannel.getUserRoleId() != null) {
						// get channel with id from channelId
                        // get UserRole with UserRoleId
                        userRole = dataService.getRowById(userRoleChannel.getUserRoleId(), UserRole.class);
                        if (userRole == null) {
                            hashErrors.put("UserRole", MsalesValidator.DK_USER_ROLE_ID_NOT_EXIST + userRoleChannel.getUserRoleId());
                        }
                    }
                    if(userRoleChannel.getUpdatedUser() != null){
                		User userRole3 = dataService.getRowById(userRoleChannel.getUpdatedUser(), User.class);
                		if(userRole3 == null){
                			hashErrors.put("UserRoleChannel", MsalesValidator.MCP_USER_NOT_EXIST + userRoleChannel.getUpdatedUser());
                		}
                	}
                    if (hashErrors.size() > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    int ret = 0;
                    try {
                        // insert new a user to database
                        ret = dataService.updateSync(userRoleChannel);
                    } catch (Exception e) {
                        if (e instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(e);
                        }
                    }
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } // update failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_UPDATE_FAIL));
                    }
                } // userRoleChannel from json null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL));
                }
            } // jsonString null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * deleted UserRoleChannel
     *
     * @param request is jsonString have id and deletedUser
     * @return a jsonString
     */
    // @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.USER_ROLE_CHANNEL.ACTION_DELETE_USER_ROLE_CHANNEL, method = RequestMethod.POST)
    public @ResponseBody String deleteUserRoleChannel(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            UserRoleChannel userRoleChannel;
            try {
                // parse jsonString to a userRoleChannel Object
                userRoleChannel = MsalesJsonUtils.getObjectFromJSON(jsonString, UserRoleChannel.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRoleChannel from json not null
            if (userRoleChannel != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // get user, userRole, channel from their Id
                if (userRoleChannel.getId() != null) {
                    UserRoleChannel userRole2 = dataService.getRowById( userRoleChannel.getId(), UserRoleChannel.class);
                    if (userRole2 == null) {
                        hashErrors.put("UserRoleChannel",MsalesValidator.DK_USER_ROLE_CHANNEL_ID_NOT_EXIST + userRoleChannel.getId());
                    }
                    if(userRoleChannel.getDeletedUser() != null){
                		User userRole3 = dataService.getRowById(userRoleChannel.getDeletedUser(), User.class);
                		if(userRole3 == null){
                			hashErrors.put("UserRoleChannel",MsalesValidator.MCP_USER_NOT_EXIST + userRoleChannel.getDeletedUser());
                		}
                	}
                    if (hashErrors.size() > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    int ret = 0;
                    try {
                        // insert new a user to database
                        ret = dataService.deleteSynch(userRoleChannel);
                    } catch (Exception e) {
                        if (e instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(e);
                        }
                    }
					// delete userRoleChannel
                    // delete userRoleChannel success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } // delete failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NULL));
                    }
                } // userRoleChannel from json null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
                }
            } // jsonString null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Get List UserRoleChannel by ChannelId
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER_ROLE_CHANNEL.ACTION_GET_LIST_USER_ROLE_CHANNEL_BY_CHANNELID, method = RequestMethod.POST)
    public @ResponseBody String getListUserRoleChannelByChannelId(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // jsonString not null
        if (jsonString != null) {
            UserRoleChannel userRoleChannel;
            try {
                // parse jsonString to a userRoleChannel Object
                userRoleChannel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        UserRoleChannel.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRoleChannel from json not null
            if (userRoleChannel != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // Channel from userRoleChannel with correct channelId
                if (userRoleChannel.getChannelId() != null) {
                    Channel channel = dataService.getRowById(userRoleChannel.getChannelId(), Channel.class);
                    if (channel == null) {
                        hashErrors.put("Channel",MsalesValidator.DK_CHANNEL_ID_NOT_EXIST + userRoleChannel.getChannelId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    // get List userRoleChannel by channelId
                    ParameterList parameterList = new ParameterList("channels.id", userRoleChannel.getChannelId(),page.getPageNo(), page.getRecordsInPage());
                    MsalesResults<UserRoleChannel> list = dataService.getListOption(
                            UserRoleChannel.class, parameterList, true);
                    // list userRoleChannel not null
                    if (list != null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, list));
                    } // list userRoleChannel null
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NULL));
                    }
                } // channel from json with incorrect channelId
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL));
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
     * Get List UserRoleChannel By UserId
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER_ROLE_CHANNEL.ACTION_GET_LIST_USER_ROLE_CHANNEL_BY_USERID, method = RequestMethod.POST)
    public @ResponseBody String getListUserRoleChannelByUserId(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
       
        // jsonString not null
        if (jsonString != null) {
            UserRoleChannel userRoleChannel;
            try {
                // parse jsonString to a userRoleChannel Object
                userRoleChannel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        UserRoleChannel.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRoleChannel from json not null
            if (userRoleChannel != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // user from userRoleChannel with correct userId
                if (userRoleChannel.getUserId() != null) {
                    User user = dataService.getRowById(userRoleChannel.getUserId(), User.class);
                    if (user == null) {
                        hashErrors.put("User", MsalesValidator.MCP_USER_NOT_EXIST + userRoleChannel.getUserId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE,  hashErrors));
                    }

                    // get List userRoleChannel by userId
                    ParameterList parameterList = new ParameterList("users.id", userRoleChannel.getUserId(), page.getPageNo(), page.getRecordsInPage());
                    MsalesResults<UserRoleChannel> list = dataService.getListOption(
                            UserRoleChannel.class, parameterList, true);
                    // list userRoleChannel not null
                    if (list != null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, list));
                        
                    } // list userRoleChannel null
                    else {
                    	return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
                    }
                } // user from json with incorrect userId
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_NULL));
                }
            } // user from json null
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
