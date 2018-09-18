package vn.itt.msales.channel.controller;

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
import vn.itt.msales.entity.ChannelLocation;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.User;
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
@RequestMapping(value = MsalesConstants.MODULE.CHANNELLOCATION.NAME)
public class MsalesChannelLocationController extends CsbController {

    /**
     * Get a ChannelLocation by id
     *
     * @param request is a jsonString have id to get info channelLocation
     * @return a jsonString contains status, code, and content include info of
     * channelLocation
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELLOCATION.ACTION_GET_CHANNEL_LOCATION, method = RequestMethod.POST)
    public @ResponseBody
    String getChannelLocation(HttpServletRequest request) {
        // get jsonString from client request via Csb
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            ChannelLocation channelLocation = new ChannelLocation();

            try {
                // get Object channelLocation from jsonString
                channelLocation = MsalesJsonUtils.getObjectFromJSON(jsonString, ChannelLocation.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (channelLocation != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (channelLocation.getId() != null) {
                    // get info channelLocations with id
                    ChannelLocation channelLocation2 = dataService.getRowById(channelLocation.getId(), ChannelLocation.class);
                    if (channelLocation2 != null) {
                        // return a jsonString include info of channelLocation
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, channelLocation2));
                    } else {
                        // object channelLocation == null
                        hashErrors.put("ChannelLocation", MsalesValidator.DK_CHANNEL_LOCATION_ID_NOT_EXIST + channelLocation.getId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    // jsonString have id invalid
                    hashErrors.put("ChannelLocation", MsalesValidator.DK_CHANNEL_LOCATION_ID_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
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
     * createChannelLocation
     *
     * @param request is a jsonString contains data to create ChannelLocation
     * @return a jsonString include status, code, contents
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELLOCATION.ACTION_CREATE_CHANNEL_LOCATION, method = RequestMethod.POST)
    public @ResponseBody
    String createChannelLocation(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            ChannelLocation channelLocation = null;
            try {
                // parse jsonString to a channelLocation Object
                channelLocation = MsalesJsonUtils.getObjectFromJSON(jsonString, ChannelLocation.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // channelLocation from json not null
            if (channelLocation != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                Location location = null;
                Channel channel = null;
                if (channelLocation.getLocationId() != null) {
                    // get Location with id from channelLocation
                    location = dataService.getRowById(channelLocation.getLocationId(), Location.class);
                    if (location == null) {
                        // return message warning location is not exist
                        hashErrors.put("Location", MsalesValidator.DK_LOCATION_ID_NOT_EXIST + channelLocation.getLocationId());
                    }
                }
                if (channelLocation.getChannelId() != null) {
                    // get channel with id from channelId
                    channel = dataService.getRowById(channelLocation.getChannelId(), Channel.class);
                    if (channel == null) {
                        // return message warning location is not exist
                        hashErrors.put("Channel", MsalesValidator.DK_CHANNEL_ID_NOT_EXIST + channelLocation.getChannelId());
                    }
                }
                if (channelLocation.getCreatedUser() != null) {
                    User userRole2 = dataService.getRowById(channelLocation.getCreatedUser(), User.class);
                    if (userRole2 == null) {
                        hashErrors.put("ChannelLocation", MsalesValidator.MCP_USER_NOT_EXIST + channelLocation.getCreatedUser());
                    }
                }
                if (hashErrors.size() > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                int ret = 0;
                try {
                    // insert new a user to database
                    ret = dataService.insertRow(channelLocation);
                } catch (Exception e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (e.getCause().getCause() instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }
                }
                // save channelLocation to DB
                // int ret = dataService.insertRow(channelLocation);
                // channelLocation from DB not null
                if (ret > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, null));
                } // channelLocation from DB null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
            } // channelLocation from json null
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
     * Update channelLocation
     *
     * @param request is jsonString contains data to update ChannelLocation
     * @return is jsonString include status, code and contents
     */
    // @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELLOCATION.ACTION_UPDATE_CHANNEL_LOCATION, method = RequestMethod.POST)
    public @ResponseBody
    String updateChannelLocation(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            ChannelLocation channelLocation;
            try {
                // parse jsonString to a channelLocation Object
                channelLocation = MsalesJsonUtils.getObjectFromJSON(jsonString, ChannelLocation.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // channelLocation from json not null
            if (channelLocation != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (channelLocation.getId() != null) {
                    ChannelLocation channelLocation2 = dataService.getRowById(
                            channelLocation.getId(), ChannelLocation.class);
                    if (channelLocation2 == null) {
                        hashErrors.put("ChannelLocation", MsalesValidator.DK_CHANNEL_LOCATION_ID_NOT_EXIST + channelLocation.getId());
                    }

                    Location location = null;
                    Channel channel = null;
                    if (channelLocation.getLocationId() != null) {
                        // get Location with id from channelLocation
                        location = dataService.getRowById(
                                channelLocation.getLocationId(), Location.class);
                        if (location == null) {
                            // return message warning location is not exist
                            hashErrors.put("Location", MsalesValidator.DK_LOCATION_ID_NOT_EXIST + channelLocation.getLocationId());
                        }
                    }

                    if (channelLocation.getChannelId() != null) {
                        // get channel with id from channelId
                        channel = dataService.getRowById(channelLocation.getChannelId(), Channel.class);
                        if (channel == null) {
                            // return message warning location is not exist
                            hashErrors.put("Channel", MsalesValidator.DK_CHANNEL_ID_NOT_EXIST + channelLocation.getChannelId());
                        }
                    }
                    if (channelLocation.getUpdatedUser() != null) {
                        User userRole2 = dataService.getRowById(channelLocation.getUpdatedUser(), User.class);
                        if (userRole2 == null) {
                            hashErrors.put("ChannelLocation", MsalesValidator.MCP_USER_NOT_EXIST + channelLocation.getUpdatedUser());
                        }
                    }
                    if (hashErrors.size() > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    int ret = 0;
                    try {
                        // update channelLocation to DB
                        ret = dataService.updateSync(channelLocation);

                    } catch (Exception e) {
                        if (e instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(e);
                        }
                    }
                    // update success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } // update failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_UPDATE_FAIL));
                    }
                } // channelLocation from json null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL, MsalesValidator.DK_CHANNEL_LOCATION_ID_NULL));
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
     * delete ChannelLocation
     *
     * @param request is jsonString have id and deletedUser
     * @return a jsonString contains status, code and contents
     */
    // @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELLOCATION.ACTION_DELETE_CHANNEL_LOCATION, method = RequestMethod.POST)
    public @ResponseBody
    String deleteChannelLocation(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            ChannelLocation channelLocation;
            try {
                // parse jsonString to a channelLocation Object
                channelLocation = MsalesJsonUtils.getObjectFromJSON(jsonString, ChannelLocation.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // channelLocation from json not null
            if (channelLocation != null) {
                // delete channelLocation
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (channelLocation.getId() != null) {
                    ChannelLocation channelLocation2 = dataService.getRowById(channelLocation.getId(), ChannelLocation.class);
                    if (channelLocation2 == null) {
                        hashErrors.put("ChannelLocation", MsalesValidator.DK_CHANNEL_LOCATION_ID_NOT_EXIST + channelLocation.getId());
                    }
                    if (channelLocation.getDeletedUser() != null) {
                        User userRole2 = dataService.getRowById(channelLocation.getDeletedUser(), User.class);
                        if (userRole2 == null) {
                            hashErrors.put("ChannelLocation", MsalesValidator.MCP_USER_NOT_EXIST + channelLocation.getDeletedUser());
                        }
                    }
                    if (hashErrors.size() > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    int ret = 0;
                    try {
                        // update channelLocation to DB
                        ret = dataService.deleteSynch(channelLocation);

                    } catch (Exception e) {
                        if (e instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(e);
                        }
                    }
                    // delete channelLocation success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } // delete failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } // channelLocation from json null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL, MsalesValidator.DK_CHANNEL_LOCATION_ID_NULL));
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
     * getListChannelLocation
     *
     * @param request is jsonString have branch to connect DB
     * @return a jsonString contains all channelLocation
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELLOCATION.ACTION_GET_LIST_CHANNEL_LOCATION, method = RequestMethod.POST)
    public @ResponseBody
    String listChannelLocation(HttpServletRequest request) {

        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());
        // get List channelLocation from DB
        MsalesResults<ChannelLocation> list = dataService.getListOption(
                ChannelLocation.class, parameterList, true);
        if (list != null) {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                    HttpStatus.OK, list));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }
    }

    /**
     * Get List ChannelLocation by LocationId
     *
     * @param request is jsonString have locationId and branch to connected with
     * DB
     * @return a jsonString contains all ChannelLocation have common locationId
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELLOCATION.ACTION_GET_LIST_CHANNEL_LOCATION_BY_LOCATION_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListChannelLocationByLocationId(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // jsonString not null
        if (jsonString != null) {
            ChannelLocation channelLocation;
            try {
                // parse jsonString to a ChannelLocation Object
                channelLocation = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        ChannelLocation.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // channelLocation from json not null
            if (channelLocation != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // Location from channelLocation with correct Id
                if (channelLocation.getLocationId() != null) {
                    Location channel = dataService.getRowById(channelLocation.getLocationId(), Location.class);
                    if (channel == null) {
                        hashErrors.put("Location", MsalesValidator.DK_LOCATION_ID_NOT_EXIST + channelLocation.getLocationId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    // get List channelLocation by locationId
                    ParameterList parameterList = new ParameterList("locations.id", channelLocation.getLocationId(), page.getPageNo(), page.getRecordsInPage());
                    MsalesResults<ChannelLocation> list = dataService.getListOption(
                            ChannelLocation.class, parameterList, true);
                    // list channelLocation not null
                    if (list != null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, list));
                    } // list channelLocation null
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NULL));
                    }
                } // location from json with incorrect Id
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, MsalesValidator.DK_LOCATION_ID_NULL));
                }
            } // location from json null
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
     * getChannelLocationByChannelId
     *
     * @param request is a jsonString have channelId, page include pageNo,
     * RecordInPage to get channelLocation By channelId
     * @return a jsonString contains channelLocations
     */
    @RequestMapping(value = MsalesConstants.MODULE.CHANNELLOCATION.ACTION_GET_LIST_CHANNEL_LOCATION_BY_CHANNEL_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListChannelLocationByChannelId(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // jsonString not null
        if (jsonString != null) {
            ChannelLocation channelLocation;
            try {
                // parse jsonString to a ChannelLocation Object
                channelLocation = MsalesJsonUtils.getObjectFromJSON(jsonString, ChannelLocation.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // channelLocation from json not null
            if (channelLocation != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // Channel from channelLocation with correct channelId
                if (channelLocation.getChannelId() != null) {
                    Channel channel = dataService.getRowById(channelLocation.getChannelId(), Channel.class);
                    if (channel == null) {
                        hashErrors.put("Channel", MsalesValidator.DK_CHANNEL_ID_NOT_EXIST + channelLocation.getChannelId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    // get List channelLocation by channelId
                    ParameterList parameterList = new ParameterList("channels.id", channelLocation.getChannelId(), page.getPageNo(), page.getRecordsInPage());
                    MsalesResults<ChannelLocation> list = dataService.getListOption(
                            ChannelLocation.class, parameterList, true);
                    // list channelLocation not null

                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, list));

                } // channel from json with incorrect channelId
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, MsalesValidator.DK_CHANNEL_ID_NULL));
                }
            } // channel from json null
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

    @RequestMapping(value = MsalesConstants.MODULE.CHANNELLOCATION.ACTION_DELETE_CHANNEL_LOCATION_BY_CHANNEL_ID_AND_LOCATION_ID, method = RequestMethod.POST)
    public @ResponseBody
    String deleteChannaelLocationByChannelIdAndLocationId(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            ChannelLocation channelLocation;
            try {
                // parse jsonString to a channelLocation Object
                channelLocation = MsalesJsonUtils.getObjectFromJSON(jsonString, ChannelLocation.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // channelLocation from json not null
            if (channelLocation != null) {
                // delete channelLocation
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (channelLocation.getId() != null) {
                    ChannelLocation channelLocation2 = dataService.getRowById(channelLocation.getId(), ChannelLocation.class);
                    if (channelLocation2 == null) {
                        hashErrors.put("ChannelLocation", MsalesValidator.DK_CHANNEL_LOCATION_ID_NOT_EXIST + channelLocation.getId());
                    }
                    if (channelLocation.getDeletedUser() != null) {
                        User userRole2 = dataService.getRowById(channelLocation.getDeletedUser(), User.class);
                        if (userRole2 == null) {
                            hashErrors.put("ChannelLocation", MsalesValidator.MCP_USER_NOT_EXIST + channelLocation.getDeletedUser());
                        }
                    }
                    if (hashErrors.size() > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    int ret = 0;
                    try {
                        // update channelLocation to DB
                        ret = dataService.deleteSynch(channelLocation);

                    } catch (Exception e) {
                        if (e instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(e);
                        }
                    }
                    // delete channelLocation success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } // delete failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } // channelLocation from json null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL, MsalesValidator.DK_CHANNEL_LOCATION_ID_NULL));
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
}
