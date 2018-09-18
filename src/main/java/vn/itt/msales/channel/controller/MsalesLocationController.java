package vn.itt.msales.channel.controller;

//import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.itt.msales.app.service.AppService;
import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.ChannelLocation;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.Session;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.SearchObject;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author DuanND
 *
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.LOCATION.NAME)
public class MsalesLocationController extends CsbController {

    /**
     * Get information of Location
     *
     * @param request
     * @return
     */
    @Autowired
    private AppService appService;

    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_GET_LOCATION, method = RequestMethod.POST)
    public @ResponseBody
    String getLocation(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
		// HashMap<String, Object> parent = new HashMap<String, Object>();

        // jsonString not null
        if (jsonString != null) {
            Location location = null;
            try {
                // parse jsonString to a Location Object
                location = MsalesJsonUtils.getObjectFromJSON(jsonString, Location.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // location from json not null
            if (location != null) {
                // location from json with correct Id
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<>();
                if (location.getId() != null) {
                    // get location from DB
                    Location location1 = dataService.getRowById(location.getId(), Location.class);
                    // location not null
                    if (location1 != null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, location1));
                    } // location null
                    else {
                        hashErrors.put("Location", MsalesValidator.DK_LOCATION_ID_NOT_EXIST + location.getId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                } // location from json with incorrect Id
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL, MsalesValidator.DK_LOCATION_ID_NULL));

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
     * Create Location
     *
     * @param request is jsonString include data to create a location
     * @return a jsonString contains status, code and contents
     */
    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_CREATE_LOCATION, method = RequestMethod.POST)
    public @ResponseBody
    String createLocation(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            Location location = null;
            try {
                // parse jsonString to a Location Object
                location = MsalesJsonUtils.getObjectFromJSON(jsonString, Location.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }// location from json not null
            MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
            location.setCreatedUser(loginUserInf.getId());
            if (location != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (location.getParentId() == 0) {
                    location.setParentId(null);

                }
                if (location.getParentId() != null && location.getParentId() != 0) {
                    Location location2 = null;
                    // get location parent ID from location
                    location2 = dataService.getRowById(location.getParentId(), Location.class);
                    hashErrors = new LinkedHashMap<String, String>();
                    // location parent is not exist
                    if (location2 == null) {
                        // return message warning location parent is not exist
                        // in DB
                        hashErrors.put("Location", MsalesValidator.DK_LOCATION_ID_NOT_EXIST + location.getParentId());
                    }
                }

                String str = location.getName();
                if (str != null) {

                    if (location.getLocationType() == 1) {
                        ParameterList parameterList = new ParameterList("name", str);
                        parameterList.add("locationType", 1);
                        List<Location> locations = dataService.getListOption(Location.class, parameterList);
                        if (locations.size() != 0) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NAME_PROVINCE));
                        }
                    }
                    if (location.getLocationType() == 2) {
                        ParameterList parameterList = new ParameterList("name", str);
                        parameterList.add("locationType", 2);
                        List<Location> locations = dataService.getListOption(Location.class, parameterList);
                        if (locations.size() != 0) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NAME_DISTRICT));
                        }
                    }
                    if (location.getLocationType() == 3) {
                        ParameterList parameterList = new ParameterList("name", str);
                        parameterList.add("locationType", 3);
                        List<Location> locations = dataService.getListOption(Location.class, parameterList);
                        if (locations.size() != 0) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NAME_WARD));
                        }
                    }

                    if (str.trim().isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, MsalesValidator.DK_GOODS_NAME_NOT_EXIST));
                    }
                }
                String str1 = location.getCode();
                if (str1 != null) {

                    if (location.getLocationType() == 1) {
                        ParameterList parameterList = new ParameterList("code", str1);
                        parameterList.add("locationType", 1);
                        List<Location> locations = dataService.getListOption(Location.class, parameterList);
                        if (locations.size() != 0) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.CODE_PROVINCE));
                        }
                    }
                    if (location.getLocationType() == 2) {
                        ParameterList parameterList = new ParameterList("code", str1);
                        parameterList.add("locationType", 2);
                        List<Location> locations = dataService.getListOption(Location.class, parameterList);
                        if (locations.size() != 0) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.CODE_DISTRICT));
                        }
                    }
                    if (location.getLocationType() == 3) {
                        ParameterList parameterList = new ParameterList("code", str1);
                        parameterList.add("locationType", 3);
                        List<Location> locations = dataService.getListOption(Location.class, parameterList);
                        if (locations.size() != 0) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.CODE_WARD));
                        }
                    }

                    if (str1.trim().isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, MsalesValidator.DK_GOODS_CODE_NOT_EXIST));
                    }
                }

                if (location.getCreatedUser() != null) {
                    User userRole2 = dataService.getRowById(location.getCreatedUser(), User.class);
                    if (userRole2 == null) {
                        hashErrors.put("Location", MsalesValidator.MCP_USER_NOT_EXIST + location.getCreatedUser());
                    }
                }
                if (hashErrors.size() > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                int ret = 0;
                try {
                    // insert new a user to database
                    ret = dataService.insertRow(location);
                } catch (Exception e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (e.getCause().getCause() instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }
                }
                if (ret > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, null));
                } // location from DB null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
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
     * Update Location
     *
     * @param request is a jsonString include data to update location
     * @return a jsonString to contain status, code and contents
     */
    // @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_UPDATE_LOCATION, method = RequestMethod.POST)
    public @ResponseBody
    String updateLocation(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            Location location = null;
            // Location location = null;
            try {
                // parse jsonString to a Location Object
                location = MsalesJsonUtils.getObjectFromJSON(jsonString, Location.class);
                // location = MsalesJsonUtils.getObjectFromJSON(jsonString,
                // Location.class);

            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }// location from json not null
            if (location != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (location.getId() != null) {
                    Location location2 = dataService.getRowById(location.getId(), Location.class);
                    if (location2 == null) {
                        hashErrors.put("Location", MsalesValidator.DK_LOCATION_ID_NOT_EXIST + location.getId());
                    }

                    if (location.getParentId() != null) {
                        // get location parent ID from location
                        Location location3 = dataService.getRowById(location.getParentId(), Location.class);
                        // location parent is not exist
                        if (location3 == null) {
                            hashErrors.put("Location", MsalesValidator.DK_LOCATION_ID_NOT_EXIST + location.getParentId());
                        }
                    }
                    if (location.getUpdatedUser() != null) {
                        User userRole2 = dataService.getRowById(location.getUpdatedUser(), User.class);
                        if (userRole2 == null) {
                            hashErrors.put("Location", MsalesValidator.MCP_USER_NOT_EXIST + location.getUpdatedUser());
                        }
                    }
                    if (hashErrors.size() > 0) {

                        // return message warning tableName or property is not
                        // exist in DB
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));

                    }
                    String str = location.getName();
                    if (str != null) {
                        if (str.trim().isEmpty()) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, MsalesValidator.DK_GOODS_NAME_NOT_EXIST));
                        }
                    }
                    String str1 = location.getCode();
                    if (str1 != null) {
                        if (str1.trim().isEmpty()) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, MsalesValidator.DK_GOODS_CODE_NOT_EXIST));
                        }
                    }
                    int ret = 0;
                    try {
                        ret = dataService.updateSync(location);
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
                } // location from json null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, MsalesValidator.DK_LOCATION_ID_NULL));
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
     * delete Location
     *
     * @param request is jsonString include id and deletedUser
     * @return a jsonString contains status, code and contents
     */
    // @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_DELETE_LOCATION, method = RequestMethod.POST)
    public @ResponseBody
    String deleteLocation(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            Location location = null;
            try {
                // parse jsonString to a Location Object
                location = MsalesJsonUtils.getObjectFromJSON(jsonString, Location.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRole from json not null
            if (location != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (location.getId() != null) {
                    Location location2 = dataService.getRowById(
                            location.getId(), Location.class);
                    if (location2 == null) {
                        hashErrors.put("Location", MsalesValidator.DK_LOCATION_ID_NOT_EXIST + location.getId());
                    }
                    if (location.getDeletedUser() != null) {
                        User userRole2 = dataService.getRowById(location.getDeletedUser(), User.class);
                        if (userRole2 == null) {
                            hashErrors.put("Location", MsalesValidator.MCP_USER_NOT_EXIST + location.getDeletedUser());
                        }
                    }

                    if (hashErrors.size() > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    int ret = 0;
                    try {
                        ret = dataService.deleteSynch(location);
                    } catch (Exception e) {
                        if (e instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(e);
                        }
                    }
                    // update delete location success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } // update delete location failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } // location from json null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL, MsalesValidator.DK_LOCATION_ID_NULL));
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
     * Get List all Locations
     *
     * @param request is jsonString
     * @return a jsonString contains data of locations
     */
    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_GET_LIST_LOCATION, method = RequestMethod.POST)
    public @ResponseBody
    String getListLocation(HttpServletRequest request) {
        // get Lists Location from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());
        MsalesResults<Location> lists = dataService.getListOption(Location.class, parameterList, true);
        // lists not null display list
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                HttpStatus.OK, lists));
    }

    /**
     * Get List Location by parentId
     *
     * @param request is a jsonString have parentId
     * @return a jsonString include locations
     */
    // @SuppressWarnings("unused")
    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_GET_LIST_LOCATION_BY_PARENTID, method = RequestMethod.POST)
    public @ResponseBody
    String getListLocationByParentId(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // jsonString not null
        if (jsonString != null) {
            Location location = null;
            try {
                // parse jsonString to a Location Object
                location = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Location.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }// location from json not null
            if (location != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // Location with correct parentId
                if (location.getParentId() != null) {
                    if (location.getParentId() == 0) {
                        //lay het tinh thanh
                        ParameterList parameterList = new ParameterList("locationType", 1, page.getPageNo(), page.getRecordsInPage());
                        parameterList.setOrder("name");
                        parameterList.setOrder("code");
                        MsalesResults<Location> list = dataService.getListOption(Location.class, parameterList, true);

                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
                    } else {
                        Location channel = dataService.getRowById(
                                location.getParentId(), Location.class);
                        if (channel == null) {
                            hashErrors.put("Location", MsalesValidator.DK_LOCATION_ID_NOT_EXIST + location.getParentId());
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));

                        }
                        // get List Location by parentId
                        ParameterList parameterList = new ParameterList(
                                "parents.id", location.getParentId(),
                                page.getPageNo(), page.getRecordsInPage());
                        parameterList.setOrder("name");
                        parameterList.setOrder("code");
                        MsalesResults<Location> list = dataService.getListOption(Location.class, parameterList, true);

                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));

                    }

                } // location from json with incorrect parentId
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
                    .create(MsalesStatus.NULL));
        }
    }

    /**
     * GET Location By LocationType
     *
     * @param request is jsonString have locationType
     * @return a jsonString have info of Location
     */
    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_GET_CB_LIST_LOCATION_BY_LOCATIONTYPE, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListLocationByLocationType(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            Location location = null;
            try {
                // parse jsonString to a Location Object
                location = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Location.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }// location from json not null
            if (location != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // Location with correct parentId
                if (location.getLocationType() != null) {
                    // get List Location by parentId
                    ParameterList parameterList = new ParameterList("locationType", location.getLocationType());
                    parameterList.setOrder("name");
                    MsalesResults<Location> lists = dataService.getListOption(Location.class, parameterList, true);
                    String[] strings = {"note", "parents", "locationType", "code", "lng", "lat"};
                    // list location not null
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, lists), strings);

                } // location from json with incorrect parentId
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, MsalesValidator.DK_LOCATION_TYPE_NULL));
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
     * get Location by ParentId
     */
    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_GET_CB_LIST_LOCATION_BY_PARENT_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListLocationByParentId(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            Location location;
            try {
                //parse jsonString to a POS Object
                location = MsalesJsonUtils.getObjectFromJSON(jsonString, Location.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channelSalePointInformation from json not null
            if (location != null) {
                if (location.getParentId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                    hashErrors.put("Location", MsalesValidator.DK_LOCATION_ID_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List POS by locationId                    
                ParameterList parameterList = new ParameterList("parents.id", location.getParentId());
                MsalesResults<Location> lists = dataService.getListOption(Location.class, parameterList, true);
                String[] strings = {"note", "parents", "locationType", "code", "lng", "lat"};
                // list location not null
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, lists), strings);
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
     * get CB List Location by EmployerId
     */
    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_GET_CB_LIST_LOCATION_BY_EMPLOYER_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListLocationByEmployerId(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            Location location;
            try {
                //parse jsonString to a POS Object
                location = MsalesJsonUtils.getObjectFromJSON(jsonString, Location.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //channelSalePointInformation from json not null
            if (location != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (location.getParentId() == null) {
                    hashErrors.put("parentId", MsalesValidator.DK_LOCATION_ID_NULL);
                }
                if (location.getLocationType() == null) {
                    hashErrors.put("locationType", MsalesValidator.DK_LOCATION_TYPE_NULL);
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get session
                Session session = (Session) request.getAttribute("session");
                if (session == null) {
                    //not login
                    return "login";
                }
                int userId = session.getUserId();

                //get UserCHannelLocation
                String hql = "SELECT DISTINCT ChannelLocation.locations"
                        + " FROM UserRoleChannel as UserRoleChannel,ChannelLocation as ChannelLocation"
                        + " WHERE UserRoleChannel.users.id = '" + userId + "'"
                        + " AND UserRoleChannel.channels.id = ChannelLocation.channels.id";
                List<Location> listLocation = dataService.executeSelectHQL(Location.class, hql, false, 0, 0);
                ArrayList<Location> list = new ArrayList<>();
                String codes = ";";
                for (Location loca : listLocation) {
                    Location temp = loca;
                    if (location.getLocationType() == 1) {
                        while (temp != null) {
                            if (temp.getLocationType() == 1) {
                                if (!codes.contains(";" + temp.getCode().trim() + ";")) {
                                    list.add(temp);
                                    codes += temp.getCode() + ";";
                                }
                                break;
                            } else {
                                temp = temp.getParents();
                            }
                        }
                    }//locationType ==2
                    else {
                        while (temp != null) {
                            if (temp.getParents() != null && location.getParentId() == temp.getParents().getId()) {
                                if (!codes.contains(";" + temp.getCode().trim() + ";")) {
                                    list.add(temp);
                                    codes += temp.getCode() + ";";
                                }
                                break;
                            } else {
                                temp = temp.getParents();
                            }
                        }

                    }
                }

                MsalesResults<Location> results = new MsalesResults<>();
                results.setContentList(list);
                results.setCount(Long.parseLong(list.size() + ""));
                String[] filters = {"parents", "code", "locationType", "note", "lat", "lng"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, results), filters);
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

    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_GET_CB_LIST_LOCATION_BY_LOCATIONTYPE_IS_1, method = RequestMethod.POST)
    public @ResponseBody
    String getListLocationCity(HttpServletRequest request) {
        // get Lists Location from DB
        ParameterList parameterList = new ParameterList("locationType", 1);
        MsalesResults<Location> lists = dataService.getListOption(Location.class, parameterList, true);
        String[] strings = {"note", "parents", "locationType", "code", "lng", "lat"};
        // lists not null display list
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, lists), strings);
    }

    /**
     * Get Location by Channel
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_GET_CB_LIST_LOCATION_BY_CHANNEL_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListLocationByChannelId(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
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
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<>();
                // Channel from channelLocation with correct channelId
                if (channelLocation.getChannelId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, MsalesValidator.DK_CHANNEL_ID_NULL));
                }

                // get List channelLocation by channelId
                // tinh thanh co locationType =1
                //ParameterList parameterList = new ParameterList("locations.locationType", 1, 0, 0);
                String hql = "SELECT DISTINCT ChannelLocation.locations"
                        + " FROM ChannelLocation AS ChannelLocation"
                        + " WHERE ChannelLocation.deletedUser = 0"
                        + " AND ChannelLocation.locations.deletedUser = 0"
                        + " AND ChannelLocation.locations.locationType = 1";

                if (channelLocation.getChannelId() != 0) {
                    //khong lay tat ca
                    //parameterList.add("channels.id", channelLocation.getChannelId());
                    hql += " AND ChannelLocation.channels.id = '" + channelLocation.getChannelId() + "'";
                }

                //List<ChannelLocation> list = dataService.getListOption(ChannelLocation.class, parameterList);
                List<Location> list = dataService.executeSelectHQL(Location.class, hql, false, 0, 0);
                // list channelLocation not null

                MsalesResults<Location> result = new MsalesResults<>();
                result.setCount((long) list.size());
//                List<Location> locationList = new ArrayList();
//                for (ChannelLocation item : list) {
//                    locationList.add(item.getLocations());
//                }
                result.setContentList(list);

                String[] filter = {"parents", "locationType", "note", "lat", "lng"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, result), filter);

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

    /**
     * Get Location by Channel - chi load tinh/thanh pho
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_GET_CB_LIST_LOCATION_BY_USER_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListLocationByUserId(HttpServletRequest request) {

        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {//dung tam 
            SearchObject searchObject;
            try {
                // parse jsonString to a ChannelLocation Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // channelLocation from json not null
            if (searchObject != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<>();
                // Channel from channelLocation with correct channelId
                if (searchObject.getUserId() == null) {
                    hashErrors.put("userId", MsalesValidator.NOT_NULL);
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                List<Location> locationList = appService.getListLocationByUserId(searchObject.getUserId(), dataService);

                List<OptionItem> ret = new ArrayList<>();
                for (Location location : locationList) {
                    if (location.getLocationType() == 1) {
                        if (!OptionItem.checkExistId(location.getId(), ret)) {
                            OptionItem optionItem = new OptionItem();
                            optionItem.setId(location.getId());
                            optionItem.setName(location.getName());
                            ret.add(optionItem);
                        }
                    } else if (location.getLocationType() == 2) {
                        if (!OptionItem.checkExistId(location.getParents().getId(), ret)) {
                            OptionItem optionItem = new OptionItem();
                            optionItem.setId(location.getParents().getId());
                            optionItem.setName(location.getParents().getName());
                            ret.add(optionItem);
                        }
                    } 
                }

                MsalesResults<OptionItem> results = new MsalesResults<>();
                results.setContentList(ret);
                results.setCount((long) ret.size());
                String[] filter = {"code"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, results), filter);

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

    /**
     * Lay danh sach Quan huyen theo tinh thanh + channel
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_GET_CB_LIST_DISTRICT_BY_USER_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListDistrictLocation(HttpServletRequest request) {

        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {//dung tam 
            SearchObject searchObject;
            try {
                // parse jsonString to a ChannelLocation Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // channelLocation from json not null
            if (searchObject != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<>();
                // Channel from channelLocation with correct channelId
                if (searchObject.getParentId() == null) {
                    hashErrors.put("parentId", MsalesValidator.NOT_NULL);
                }
                if (searchObject.getUserId() == null) {
                    hashErrors.put("userId", MsalesValidator.NOT_NULL);
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                List<Location> locationList = appService.getListLocationByUserId(searchObject.getUserId(), dataService);

                List<OptionItem> ret = new ArrayList<>();
                for (Location location : locationList) {
                    if (location.getLocationType() == 1) {
                        //get all District from location
                        if (Objects.equals(location.getId(), searchObject.getParentId())) {
                            List<Location> list = appService.getListLocationByParent(location.getId(), dataService);
                            for (Location item : list) {
                                if (!OptionItem.checkExistId(item.getId(), ret)) {
                                    OptionItem optionItem = new OptionItem();
                                    optionItem.setId(item.getId());
                                    optionItem.setName(item.getName());
                                    ret.add(optionItem);
                                }
                            }
                        }
                    } else if (location.getLocationType() == 2) {
                        if (Objects.equals(location.getParents().getId(), searchObject.getParentId())) {
                            if (!OptionItem.checkExistId(location.getId(), ret)) {
                                OptionItem optionItem = new OptionItem();
                                optionItem.setId(location.getId());
                                optionItem.setName(location.getName());
                                ret.add(optionItem);
                            }
                        }
                    } 
                }

                MsalesResults<OptionItem> results = new MsalesResults<>();
                results.setContentList(ret);
                results.setCount((long) ret.size());
                String[] filter = {"code"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, results), filter);

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

    /**
     * Search Location by parentId/searchText
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.LOCATION.ACTION_SEARCH_LOCATION, method = RequestMethod.POST)
    public @ResponseBody
    String searchLocation(HttpServletRequest request
    ) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            if (searchObject != null) {

                ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

                if (searchObject.getParentId() != null && searchObject.getParentId() != 0) {
                    parameterList.add("parents.id", searchObject.getParentId());
                } else {
                    //type =1 la tinh thanh
                    parameterList.add("locationType", 1);
                }
                if (searchObject.getSearchText() != null && !searchObject.getSearchText().trim().isEmpty()) {
                    String searchText = searchObject.getSearchText().replaceAll("'", "''");
                    parameterList.or("name", searchText, "like", "code", searchText, "like");
                }
                String[] orders = {"locationType", "name"};
                parameterList.setOrder(orders);

                MsalesResults<Location> list = dataService.getListOption(Location.class, parameterList, true);

                String[] filter = {"parents", "locationType", "note", "lat", "lng"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), filter);
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
}
