package vn.itt.msales.user.controller;

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
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.SearchObject;

/**
 *
 * @author DuanND
 *
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.USERROLE.NAME)
public class MsalesUserRoleController extends CsbController {

    /**
     *
     * @param request is a jsonString contain id of userRole to get info
     * @return a jsonString contain status, code, contents is info
     */
    @RequestMapping(value = MsalesConstants.MODULE.USERROLE.ACTION_GET_USERROLE, method = RequestMethod.POST)
    public @ResponseBody
    String getUserRole(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            UserRole userRole = null;
            try {
                // parse jsonString to a UserRole Object
                userRole = (UserRole) MsalesJsonUtils.getObjectFromJSON(jsonString, UserRole.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRole from json not null
            if (userRole != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // userRole from json with correct Id
                if (userRole.getId() != null) {
                    // get userRole from DB
                    UserRole userRole2 = dataService.getRowById(userRole.getId(), UserRole.class);
                    // userRole not null
                    if (userRole2 != null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, userRole2));
                    } // userRole null
                    else {
                        hashErrors.put("UserRole", MsalesValidator.DK_USER_ROLE_ID_NOT_EXIST + userRole.getId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } // userRole from json with incorrect Id
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
                }
            } // userRole from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                        MsalesStatus.JSON_INVALID));
            }
        } // jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                    MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Create UserRole
     *
     * @param request is JsonString contain data to create UserRole
     * @return a jsonString contains status, code and contents
     */
    @RequestMapping(value = MsalesConstants.MODULE.USERROLE.ACTION_CREATE_USERROLE, method = RequestMethod.POST)
    public @ResponseBody
    String createUserRole(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        // jsonString not null
        if (jsonString != null) {
            UserRole userRole = new UserRole();
            try {
                // parse jsonString to a UserRole Object
                userRole = (UserRole) MsalesJsonUtils.getObjectFromJSON(
                        jsonString, UserRole.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRole from json not null
            if (userRole != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (userRole.getParentId() != null) {
                    UserRole userRole2 = dataService.getRowById(userRole.getParentId(), UserRole.class);
                    if (userRole2 == null) {
                        hashErrors.put("UserRole", MsalesValidator.DK_USER_ROLE_ID_NOT_EXIST + userRole.getParentId());
                    }
                }
                if (userRole.getCreatedUser() != null) {
                    User userRole2 = dataService.getRowById(userRole.getCreatedUser(), User.class);
                    if (userRole2 == null) {
                        hashErrors.put("UserRole", MsalesValidator.MCP_USER_NOT_EXIST + userRole.getCreatedUser());
                    }
                }
                if (hashErrors.size() > 0) {

                    // return message warning tableName or property is not exist
                    // in DB
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));

                }
                String str = userRole.getName();
                if (str != null) {
                    if (str.trim().isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, MsalesValidator.DK_GOODS_NAME_NOT_EXIST));
                    }
                }

                // save userRole to DB
                int ret = 0;
                try {
                    // insert new a user to database
                    ret = dataService.insertRow(userRole);
                } catch (Exception e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (e.getCause().getCause() instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }
                }
                // int ret = dataService.insertRow(userRole);
                // userRole from DB not null
                if (ret > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, null));
                } // userRole from DB null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
            } // userRole from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } // jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Update UserRole
     *
     * @param request is jsonString contains data to update UserRole
     * @return a jsonString contains status, code, and contents
     */
    // @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.USERROLE.ACTION_UPDATE_USERROLE, method = RequestMethod.POST)
    public @ResponseBody
    String updateUserRole(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            UserRole userRole = null;
            try {
                // parse jsonString to a UserRole Object
                userRole = MsalesJsonUtils.getObjectFromJSON(jsonString, UserRole.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRole from json not null
            if (userRole != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (userRole.getId() != null) {
                    UserRole userRole2 = dataService.getRowById(userRole.getId(), UserRole.class);
                    if (userRole2 == null) {
                        hashErrors.put("UserRole", MsalesValidator.DK_USER_ROLE_ID_NOT_EXIST + userRole.getId());
                    }

                    if (userRole.getParentId() != null) {
                        UserRole userRole3 = dataService.getRowById(userRole.getParentId(), UserRole.class);
                        if (userRole3 == null) {
                            hashErrors.put("UserRole", MsalesValidator.DK_USER_ROLE_ID_NOT_EXIST + userRole.getParentId());
                        }
                    }
                    if (userRole.getUpdatedUser() != null) {
                        User userRole3 = dataService.getRowById(userRole.getUpdatedUser(), User.class);
                        if (userRole3 == null) {
                            hashErrors.put("UserRole", MsalesValidator.MCP_USER_NOT_EXIST + userRole.getUpdatedUser());
                        }
                    }

                    if (hashErrors.size() > 0) {

                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));

                    }
                    String str = userRole.getName();
                    if (str != null) {
                        if (str.trim().isEmpty()) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, MsalesValidator.DK_GOODS_NAME_NOT_EXIST));
                        }
                    }
                    // update userRole to DB
                    int ret = 0;
                    try {
                        // insert new a user to database
                        ret = dataService.updateSync(userRole);
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
                } // userRole from json null
                else {
                    hashErrors.put("UserRole", MsalesValidator.DK_USER_ROLE_ID_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
            } // jsonString null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }
    }

    /**
     * Delete UserRole
     *
     * @param request is a jsonString have id and deletedUser
     * @return a jsonString contain status, code and contents
     */
    // @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.USERROLE.ACTION_DELETE_USERROLE, method = RequestMethod.POST)
    public @ResponseBody
    String deleteUserRole(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        // jsonString not null
        if (jsonString != null) {
            UserRole userRole = null;
            try {
                // parse jsonString to a UserRole Object
                userRole = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        UserRole.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRole from json not null
            if (userRole != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (userRole.getId() != null) {
                    UserRole userRole2 = dataService.getRowById(userRole.getId(), UserRole.class);
                    if (userRole2 == null) {
                        hashErrors.put("UserRole", MsalesValidator.DK_USER_ROLE_ID_NOT_EXIST + userRole.getId());
                    }
                    if (userRole.getDeletedUser() != null) {
                        User userRole3 = dataService.getRowById(userRole.getDeletedUser(), User.class);
                        if (userRole3 == null) {
                            hashErrors.put("UserRole", MsalesValidator.MCP_USER_NOT_EXIST + userRole.getDeletedUser());
                        }
                    }
                    if (hashErrors.size() > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    int ret = 0;
                    try {
                        // insert new a user to database
                        ret = dataService.deleteSynch(userRole);
                    } catch (Exception e) {
                        if (e instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(e);
                        }
                    }
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } // update delete userRole failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } // userRole from json null
                else {
                    hashErrors.put("UserRole", MsalesValidator.DK_USER_ROLE_ID_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
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
     * Get List UserRoles
     *
     * @param request is jsonString contain branch for connect database
     * @return a jsonString contains status, code and information of UserRoles
     */
    @RequestMapping(value = MsalesConstants.MODULE.USERROLE.ACTION_GET_LIST_USERROLE, method = RequestMethod.POST)
    public @ResponseBody
    String getListUserRole(HttpServletRequest request) {
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());
        // get List UserRole from DB
        MsalesResults<UserRole> list = dataService.getListOption(UserRole.class, parameterList, true);
        // list not null display list
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                HttpStatus.OK, list));

    }

    /**
     * get_Cb_List_UserRole_By_Parent_Id
     *
     * @param request is a jsonString have parentId
     * @return a list user_role
     */
    @RequestMapping(value = MsalesConstants.MODULE.USERROLE.ACTION_GET_LIST_USERROLE_BY_PARENT_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListUserRoleByParentId(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();

        // jsonString not null
        if (jsonString != null) {
            UserRole userRole = null;
            try {
                // parse jsonString to a Location Object
                userRole = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        UserRole.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }// location from json not null
            if (userRole != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // Location with correct parentId
                if (userRole.getParentId() != null) {
                    // get List Location by parentId
                    ParameterList parameterList = new ParameterList("parents.id", userRole.getParentId());
                    MsalesResults<UserRole> lists = dataService.getListOption(UserRole.class, parameterList, true);
                    String[] strings = {"note", "parents"};
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, lists), strings);
                } // location from json with incorrect parentId
                else {
                    hashErrors.put("UserRole", MsalesValidator.DK_USER_ROLE_ID_NULL);
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
     * get Cb List UserRole
     *
     * @param request is jsonString have a
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USERROLE.ACTION_GET_CB_LIST_USERROLE, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListUserRole(HttpServletRequest request) {

        ParameterList parameterList = new ParameterList();
        // get List UserRole from DB
        MsalesResults<UserRole> list = dataService.getListOption(UserRole.class, parameterList, true);
        String[] strings = {"note", "parents"};
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), strings);
    }

    /**
     * get userRole cua user
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USERROLE.ACTION_GET_USERROLE_BY_USER_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getUserRoleByUserId(HttpServletRequest request) {
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
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<>();
                // user from userRoleChannel with correct userId
                if (userRoleChannel.getUserId() == null) {
                    hashErrors.put("userId", MsalesValidator.NOT_EXIST);
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                // get List userRoleChannel by userId
                ParameterList parameterList = new ParameterList("users.id", userRoleChannel.getUserId(), 1, 1);
                parameterList.setOrder("userRoles.id");
                List<UserRoleChannel> list = dataService.getListOption(UserRoleChannel.class, parameterList);

                if (!list.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, list.get(0).getUserRoles()));
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, null));
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

    /**
     * get Cb List userRole by monitoringUser
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USERROLE.ACTION_GET_CB_LIST_USERROLE_BY_USER_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListUserRoleByUserId(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            //dung tam class user
            SearchObject searchObject;
            try {
                // parse jsonString to a userRoleChannel Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SearchObject.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRoleChannel from json not null
            if (searchObject != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<>();
                // user from userRoleChannel with correct userId
                if (searchObject.getUserId()== null) {
                    hashErrors.put("monitoringUserId", MsalesValidator.NOT_EXIST);
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                // get userRole cua monitoring
                ParameterList parameterList = new ParameterList("users.id", searchObject.getUserId(), 1, 1);
                parameterList.setOrder("userRoles.id");
                //monitoringId = 0 => lay tat ca role
                List<UserRoleChannel> list = dataService.getListOption(UserRoleChannel.class, parameterList);

                if (!list.isEmpty()) {
                    //neu monitor la adminCompany=> chi tra ve AdminChannel
                    parameterList = new ParameterList();
                    parameterList.add("id", list.get(0).getUserRoles().getId(), ">");
                    MsalesResults<UserRole> userRoleList = dataService.getListOption(UserRole.class, parameterList, true);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, userRoleList));
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, null));
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
