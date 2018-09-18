/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.user.controller;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.itt.msales.app.service.LoginService;
import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.response.MsaleResponseStatus;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.csb.auth.MsalesSession;
import vn.itt.msales.dao.MsalesJsonValidator;
import vn.itt.msales.database.sql.MsalesSQL;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.Session;
import vn.itt.msales.entity.UserGoodsCategory;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.CreateUserAdmin;
import vn.itt.msales.entity.searchObject.SearchObject;
import vn.itt.msales.entity.searchObject.SearchUser;
import vn.itt.msales.logex.MSalesException;
import vn.itt.msales.user.model.MsalesChangePassword;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 * Support API about User.
 * <p>
 * @author ChinhNQ
 * @version
 * @since 5 Jun 2015 10:37:20
 * msales_saas#vn.itt.msales.user.controller.UserController.java
 *
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.USER.NAME)
public class MsalesUserController extends CsbController {

    private MsalesJsonValidator msalesJsonValidator;

    @Autowired
    private LoginService loginService;

    /**
     * Get {@link User} info.
     *
     * @param jsonString this JSON string get from client.
     *
     * @return JSON string include User info.
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_GET_USER, method = RequestMethod.POST)
    public @ResponseBody
    String getUser(HttpServletRequest request) {
        // get JSON string from csb controller.
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            User user = null;
            try {
                // Convert JSOn string to User object
                user = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        User.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (user != null) {
                if (user.getId() != null && user.getId() >= 0) {
                    // get User from datababase with id get from client
                    User userDB = (User) dataService.getRowById(user.getId(), User.class);
                    if (userDB != null) {
                        // Response to client user information
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, userDB));
                    } else {
                        return MsalesJsonUtils.notExists(User.class, user.getId());
                    }
                } else {
                    return MsalesJsonUtils.idNull();
                }
            } else {
                // can not convert JSON request to User object
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } else {
            // JSON request is null
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
        }
    }

    /**
     * Register new account
     *
     * @param jsonString is json string requets register
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_REGISTER_USER, method = RequestMethod.POST)
    public @ResponseBody
    String createUser(HttpServletRequest request) {
        // get JSON string from csb controller.
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            User user = null;
            try {
                /// Convert JSOn string to User object
                user = MsalesJsonUtils.getObjectFromJSON(jsonString, User.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (user != null) {
                try {
                    // sett current day to user
                    user.setCreatedAt(new Date());
                    if (user.getPassword() != null && user.getPassword().length() >= 6) {
                        // encript user pass
                        String pass = MsalesSession.getAccessToken(user.getPassword());
                        // set password affter encript.
                        user.setPassword(pass);
                    }
                    // insert new a user to database
                    int userId = dataService.insertRow(user);
                    if (userId > 0) {
                        SalesStock salesStock = new SalesStock();
                        salesStock.setSalemanUserId(user.getId());
                        salesStock.setCreatedUser(user.getCreatedUser());
                        // by default is active
                        salesStock.setStatusId(1);
                        // insert SalesStock for this user.
                        dataService.insertRow(salesStock);

                        // get list user role follow user id crate
                        List<UserRoleChannel> userRoleChannels = dataService.getListOption(UserRoleChannel.class, new ParameterList("users.id", user.getCreatedUser()));
                        if (userRoleChannels != null && !userRoleChannels.isEmpty()) {
                            UserRoleChannel urc = new UserRoleChannel();
                            // set by defualt user roler channel is saleman
                            urc.setUserRoleId(4);
                            // set channel id
                            urc.setChannelId(userRoleChannels.get(0).getChannels().getId());
                            // set user id
                            urc.setUserId(userId);
                            urc.setCreatedUser(user.getCreatedUser());

                            // create user role channel for this user
                            dataService.insertRow(urc);
                        }
                    }
                } catch (MSalesException e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof ConstraintViolationException) {
                        ConstraintViolationException conViEx = (ConstraintViolationException) ex;
                        // check duplicate user name
                        if (conViEx.getCause() instanceof MySQLIntegrityConstraintViolationException) {
                            String message = conViEx.getCause().getMessage().toLowerCase();
                            if (message.contains("duplicate")) {
                                // response to client the user request create is duplicate.
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                        .create(MsalesStatus.USER_NAME_DUPLICATE));
                            } else {
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_INVALID));
                            }
                        }
                    } else if (ex instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_INVALID));
                }
                // reponse status ok to client
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
            } else {
                // can't create user from JSON request from client.
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } else {
            // JSON string request from client is null
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
        }
    }

    /**
     * create User by Admin
     *
     * @param jsonString is json string requets register
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_CREATE_USER_ADMIN, method = RequestMethod.POST)
    public @ResponseBody
    String createUserAdmin(HttpServletRequest request) {
        // get JSON string from csb controller.
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            CreateUserAdmin createUserAdmin;
            try {
                /// Convert JSOn string to User object
                createUserAdmin = MsalesJsonUtils.getObjectFromJSON(jsonString, CreateUserAdmin.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (createUserAdmin != null) {
                try {

                    LinkedHashMap hashErrors = new LinkedHashMap();
                    if (createUserAdmin.getUser() == null) {
                        hashErrors.put("user", MsalesValidator.NOT_NULL);
                    }

                    if (createUserAdmin.getUserRoleChannelList() == null || createUserAdmin.getUserRoleChannelList().isEmpty()) {
                        hashErrors.put("userRoleChannelList", MsalesValidator.NOT_EMPTY);
                    }

                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                    }

                    // sett current day to user
                    if (createUserAdmin.getUser().getPassword() != null) {
                        // encript user pass
                        String pass = MsalesSession.getSHA256(createUserAdmin.getUser().getPassword());
                        // set password affter encript.
                        createUserAdmin.getUser().setPassword(pass);
                    }

                    //set lai cac truong bat buoc
                    createUserAdmin.getUser().setCreatedAt(new Date());
                    createUserAdmin.getUser().setUpdatedUser(0);
                    createUserAdmin.getUser().setDeletedUser(createUserAdmin.getUser().getCreatedUser());

                    // insert new a user to database
                    int userId = dataService.insert(createUserAdmin.getUser());
                    if (userId > 0) {
                        SalesStock salesStock = new SalesStock();
                        salesStock.setSalemanUserId(userId);
                        salesStock.setCreatedUser(createUserAdmin.getUser().getCreatedUser());
                        salesStock.setUpdatedUser(0);
                        salesStock.setDeletedUser(0);
                        // by default is active
                        salesStock.setStatusId(1);

                        User user = new User();
                        user.setId(userId);

                        //set user for userGoodsCategory
                        if (createUserAdmin.getUserGoodsCategoryList() != null) {
                            for (UserGoodsCategory userGoodsCategory : createUserAdmin.getUserGoodsCategoryList()) {
                                userGoodsCategory.setUsers(user);
                                userGoodsCategory.setCreatedAt(new Date());
                                userGoodsCategory.setDeletedUser(0);
                                userGoodsCategory.setUpdatedUser(0);
                            }
                        }

                        //set user for userRoleChannel
                        for (UserRoleChannel userRoleChannel : createUserAdmin.getUserRoleChannelList()) {
                            userRoleChannel.setUserId(userId);
                            userRoleChannel.setCreatedAt(new Date());
                            userRoleChannel.setDeletedUser(0);
                            userRoleChannel.setUpdatedUser(0);
                        }

                        ArrayList insertList = new ArrayList();

                        insertList.add(salesStock);
                        insertList.addAll(createUserAdmin.getUserRoleChannelList());
                        insertList.addAll(createUserAdmin.getUserGoodsCategoryList());

                        //set lai user not deleted
                        createUserAdmin.getUser().setDeletedUser(0);
                        createUserAdmin.getUser().setId(userId);
                        insertList.add(createUserAdmin.getUser());

                        dataService.insertOrUpdateArray(insertList);
                        // reponse status ok to client
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, userId));
                    } else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_INSERT_FAIL));
                    }
                } catch (MSalesException e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }
            } else {
                // can't create user from JSON request from client.
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } else {
            // JSON string request from client is null
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * update User by Admin
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_UPDATE_USER_ADMIN, method = RequestMethod.POST)
    public @ResponseBody
    String updateUserAdmin(HttpServletRequest request) {
        // get JSON string from csb controller.
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            CreateUserAdmin createUserAdmin;
            try {
                /// Convert JSOn string to User object
                createUserAdmin = MsalesJsonUtils.getObjectFromJSON(jsonString, CreateUserAdmin.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (createUserAdmin != null) {
                try {

                    LinkedHashMap hashErrors = new LinkedHashMap();
                    if (createUserAdmin.getUser() == null) {
                        hashErrors.put("user", MsalesValidator.NOT_NULL);
                    }
                    if (createUserAdmin.getUserRoleChannelList() == null) {
                        hashErrors.put("userRoleChannelList", MsalesValidator.NOT_NULL);
                    }

                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                    }

                    int createdUser = createUserAdmin.getUser().getCreatedUser();

                    if (createUserAdmin.getUser().getPassword() != null) {
                        // encript user pass
                        String pass = MsalesSession.getSHA256(createUserAdmin.getUser().getPassword());
                        // set password affter encript.
                        createUserAdmin.getUser().setPassword(pass);
                    } else {
                        User rootUser = dataService.getRowById(createUserAdmin.getUser().getId(), User.class);
                        if (rootUser != null) {
                            createUserAdmin.getUser().setPassword(rootUser.getPassword());
                            createUserAdmin.getUser().setCreatedUser(rootUser.getCreatedUser());
                            createUserAdmin.getUser().setUpdatedUser(createdUser);
                        } else {
                            hashErrors.put("user", "ID=" + createUserAdmin.getUser().getId() + " " + MsalesValidator.NOT_EXIST);
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        }
                    }

                    //danh sach UserRoleChannel
                    List<UserRoleChannel> oldUserRoleChannelList = new ArrayList<>();
                    List<UserRoleChannel> newUserRoleChannelList = new ArrayList<>();

                    for (UserRoleChannel userRoleChannel : createUserAdmin.getUserRoleChannelList()) {
                        userRoleChannel.setUserId(createUserAdmin.getUser().getId());
                        if (userRoleChannel.getId() != null) {
                            userRoleChannel.setCreatedUser(createdUser);
                            userRoleChannel.setDeletedUser(0);
                            userRoleChannel.setUpdatedUser(createdUser);
                            oldUserRoleChannelList.add(userRoleChannel);
                        } else {
                            userRoleChannel.setCreatedUser(createdUser);
                            userRoleChannel.setDeletedUser(0);
                            userRoleChannel.setUpdatedUser(0);
                            newUserRoleChannelList.add(userRoleChannel);
                        }
                    }

                    if (!oldUserRoleChannelList.isEmpty()) {
                        String hql = "UPDATE UserRoleChannel"
                                + " SET deletedUser = '" + createUserAdmin.getUser().getUpdatedUser() + "'"
                                + " WHERE users.id = '" + createUserAdmin.getUser().getId() + "'"
                                + " AND id NOT IN (";
                        for (UserRoleChannel userRoleChannel : oldUserRoleChannelList) {
                            hql += userRoleChannel.getId() + ",";
                        }
                        hql += "'')";
                        dataService.executeHQL(hql);
                    }

                    //lay danh sach nhung UserGOodsCategory ma co Id
                    //delete all userGoodsCategory tren DB ma khong nam trong danh sach
                    //insert UserGOodsCategory ko co id
                    List<UserGoodsCategory> oldUserGoodsCategorList = new ArrayList<>();
                    List<UserGoodsCategory> newUserGoodsCategorList = new ArrayList<>();
                    if (createUserAdmin.getUserGoodsCategoryList() != null) {
                        for (UserGoodsCategory userGoodsCategory : createUserAdmin.getUserGoodsCategoryList()) {
                            userGoodsCategory.setUserId(createUserAdmin.getUser().getId());
                            if (userGoodsCategory.getId() != null) {
                                userGoodsCategory.setCreatedUser(createdUser);
                                userGoodsCategory.setDeletedUser(0);
                                userGoodsCategory.setUpdatedUser(createdUser);
                                oldUserGoodsCategorList.add(userGoodsCategory);
                            } else {
                                userGoodsCategory.setStatusId(15);//mac dinh
                                userGoodsCategory.setDeletedUser(0);
                                userGoodsCategory.setUpdatedUser(0);
                                newUserGoodsCategorList.add(userGoodsCategory);
                            }
                        }
                    }
                    if (!oldUserGoodsCategorList.isEmpty()) {
                        String hql = "UPDATE UserGoodsCategory"
                                + " SET statuss.id = 17,"
                                + " deletedUser = '" + createUserAdmin.getUser().getUpdatedUser() + "'"//sua lai sau
                                + " WHERE users.id = '" + createUserAdmin.getUser().getId() + "'"
                                + " AND id NOT IN (";
                        for (UserGoodsCategory userGoodsCategory : oldUserGoodsCategorList) {
                            hql += userGoodsCategory.getId() + ",";
                        }
                        hql += "'')";
                        dataService.executeHQL(hql);
                    }

                    ArrayList insertList = new ArrayList();
                    insertList.addAll(newUserGoodsCategorList);
                    insertList.addAll(oldUserRoleChannelList);
                    insertList.addAll(newUserRoleChannelList);

                    createUserAdmin.getUser().setDeletedUser(0);
                    insertList.add(createUserAdmin.getUser());

                    dataService.insertOrUpdateArray(insertList);
                    // reponse status ok to client
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                } catch (Exception ex) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
            } else {
                // can't create user from JSON request from client.
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } else {
            // JSON string request from client is null
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Delete a {@link User} from database.
     *
     * @param jsonString is json string request delete user.
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_DELETE_USER, method = RequestMethod.POST)
    public @ResponseBody
    String deleteUser(HttpServletRequest request) {
        // get JSON string from csb controller.
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            User tableUser;

            try {
                // Convert JSOn string to LinkedHashMap object
                tableUser = MsalesJsonUtils.getObjectFromJSON(jsonString, User.class
                );
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (tableUser != null && tableUser.getId() != null) {
                // execute delete user
                int ret = -1;
                try {
                    ret = dataService.deleteSynch(tableUser);
                } catch (Exception e) {
                    if (e instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }
                }
                //update success
                if (ret > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                } // id is not exist in database
                else if (ret == -2) {
                    LinkedHashMap<String, String> object = new LinkedHashMap();
                    object.put("id", String.format(MsalesStatus.NOT_EXISTS.getMessage(), "ID = " + tableUser.getId()));
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, object));
                }
            } else {
                // can't create User object from JSON requesst from client.
                LinkedHashMap<String, String> object = new LinkedHashMap();
                object.put("id", MsalesStatus.JSON_ID_NULL.getMessage());
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, object));
            }
        } else {
            // JSON request from client is null
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
        }
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_DELETE_FAIL));
    }

    /**
     * Update {@link User} info.
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_UPDATE_USER, method = RequestMethod.POST)
    public @ResponseBody
    String updateUser(HttpServletRequest request) {
        // get JSON string from csb controller.
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            User tableUser;

            try {
                // Convert JSOn string to LinkedHashMap object
                tableUser = MsalesJsonUtils.getObjectFromJSON(jsonString, User.class
                );
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (tableUser.getId() != null) {
                int ret = -1;
                try {
                    if (tableUser.getPassword() != null && tableUser.getPassword().length() >= 6) {
                        // encript user pass
                        String pass = MsalesSession.getAccessToken(tableUser.getPassword());
                        // set password affter encript.
                        tableUser.setPassword(pass);
                    }
                    ret = dataService.updateSync(tableUser);
                    if (ret >= 0) {
                        //update success
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK));
                    } // id is not exist in database
                    else if (ret == -2) {
                        LinkedHashMap<String, String> object = new LinkedHashMap();
                        object.put("id", String.format(MsalesStatus.NOT_EXISTS.getMessage(), "ID = " + tableUser.getId()));
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, object));
                    }

                } catch (Exception e) {
                    if (e instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }
                }
            } else {
                // can't create User object from JSON requesst from client.
                LinkedHashMap<String, String> object = new LinkedHashMap();
                object.put("id", MsalesStatus.JSON_ID_NULL.getMessage());
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, object));
            }
        } else {
            // JSON request from client is null.
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
        }
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_UPDATE_FAIL));
    }

    /**
     * Change password for user.
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_CHANGE_PASSWORD_USER)
    public @ResponseBody
    String changePasswordUser(HttpServletRequest request) {
        // get JSON string from csb controller.
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();

        if (jsonString != null) {
            try {
                MsalesChangePassword changePass = MsalesJsonUtils.getObjectFromJSON(jsonString, MsalesChangePassword.class
                );
                LinkedHashMap<String, String> field = changePass.checkFiled();
                if (field
                        != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, field));
                }

                // ensure  passwordNew lenght and passwordOld lenght > 0
                if (changePass.getPasswordNew()
                        .length() > 0 && changePass.getPasswordOld().length() > 0) {
                    if (changePass.getId() > 0) {
                        // Get user from database with request id.
                        User userFromDB = (User) dataService.getRowById(changePass.getId(), User.class);
                        if (userFromDB != null) {
                            // encipt old password
                            //String passEncript = MsalesSession.getAccessToken(changePass.getPasswordOld());
                            // check password old with new
                            if (changePass.getPasswordOld().equals(userFromDB.getPassword())) {
                                // encript new password
                                //passEncript = MsalesSession.getAccessToken(changePass.getPasswordNew());
                                // set new password for user
                                userFromDB.setPassword(changePass.getPasswordOld());
                                // set updated by user
                                userFromDB.setUpdatedUser(changePass.getUpdatedUser());
                                // update password to database
                                dataService.updateRow(userFromDB);
                                // crate sales stock for this user.
                                //FIXME add code create saleStock here
                                // response OK to client.
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                            } else {
                                // Old password is not match with password in database.
                                return MsalesJsonUtils
                                        .getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_PASSWORD_INCORRECT));
                            }
                        } else {
                            // Id user request change password in not exists in database.
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(new MsaleResponseStatus(
                                                    MsalesStatus.USER_NOT_EXIST, String
                                                    .valueOf(changePass.getId()))));
                        }
                    } else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_ID_INVALID));
                    }
                } else {
                    // password new or old is null
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
                }
            } catch (IOException | MSalesException ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
        } else {
            // JSON request from clietn is null.
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
        }
    }

    /**
     * Get all user.
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_GET_LIST_USER, headers = "Accept=application/json", produces = "application/json")
    public @ResponseBody
    String getUsers(HttpServletRequest request) {
        // get JSON string from csb controller.
        MsalesPageRequest pages = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        try {

            ParameterList params = new ParameterList(pages);
            MsalesResults<User> msalesResults = dataService.getListOption(User.class, params, true);
            return MsalesJsonUtils.getJSONFromOject(msalesResults,
                    new MsaleResponseStatus(HttpStatus.OK));
        } catch (Exception e) {
            if (e instanceof PropertyValueException) {
                PropertyValueException pro = (PropertyValueException) e;
                return MsalesJsonUtils.getJSONFromOject(new MsalesResponse(new MsaleResponseStatus(MsalesStatus.JSON_FIELD_VALUE_NULL, pro.getPropertyName())));
            }
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, new ArrayList()));
        }
    }

    /**
     * User login
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_LOGIN)
    public @ResponseBody
    String login(HttpServletRequest request) {
        // get JSON string from csb controller.
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();

        if (jsonString != null) {
            User user = null;

            try {
                // Convert JSOn string to User object
                user = MsalesJsonUtils.getObjectFromJSON(jsonString, User.class
                );
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);

            }
            // ensure username not null and it lenght > 0
            if (user != null && user.getUsername() != null && user.getUsername().length() > 0) {
                // get user object from database.
                User userFromDB = dataService.execSQL(User.class
                        .getSimpleName(), new String[]{
                            "username"}, new String[]{user.getUsername().trim()});
                if (userFromDB != null) {
                    //String passEncript = MsalesSession.getAccessToken(user.getPassword());
                    // check password
                    if (userFromDB.getPassword().toUpperCase().equals(user.getPassword().toUpperCase())) {
//                        mUser = userFromDB;
                        // update login status to database
//                        userFromDB.setStatusId(10);
                        // set to http session
                        request.getSession().setAttribute(MsalesConstants.MODULE.USER.NAME, mUser);
                        // update IP last login
                        //userFromDB.setIpLastLogin(request.getRemoteAddr());
                        // update to databse
                        dataService.updateRow(userFromDB);
                        // get user login
                        MsalesLoginUserInf loginInf = new MsalesLoginUserInf(userFromDB, request.getSession());
                        List<Session> ses = dataService.findByNamedQuery("Session.findByToken", new String[]{"token"}, new String[]{loginInf.getToken()});
                        if (ses.isEmpty()) {
                            // check permission
                            //List<UserRoleChannel> list = dataService.executeSelectHQL(UserRoleChannel.class, "from UserRoleChannel where users.id = " + userFromDB.getId() + " order by userRoles.id", false, 1, 10);
                            List<HashMap<String, Object>> list = dataService.execSQL(String.format(MsalesSQL.GET_MAX_USER_ROLE_CHANNEL, userFromDB.getId()));
                            if (list != null && !list.isEmpty()) {
                                // get index 0, because this row have role max of the user in a channel
                                HashMap<String, Object> userRoleChannel = list.get(0);
                                Session session = new Session();
                                session.setLastAccessedTime(new Date());
                                session.setToken(loginInf.getToken());
                                session.setUserId(userFromDB.getId());
                                session.setUserRolerId((Integer) userRoleChannel.get("USER_ROLE_ID"));
                                dataService.insertRow(session);
                            }
                        }

                        String[] filter = {"userRoleId", "branch", "companyName", "companyCode", "channelId",
                            "channelName", "channelCode", "userRoleName"};
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                                HttpStatus.OK, loginInf), filter);
                    } else {
                        // password not match
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                                MsalesStatus.USER_PASSWORD_NOT_MATCH));
                    }

                } else {
                    // user request login is not exists in database
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_NOT_EXIST));
                }
            } else {
                // response to client json is invalid when request to server
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_INVALID));
            }
        }
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.UNKNOW));
    }

    /**
     * Check login
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_CHECK_LOGIN)
    public @ResponseBody
    String checkLoginAdmin(HttpServletRequest request) {
        // get JSON string from csb controller.
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();

        if (jsonString != null) {
            MsalesLoginUserInf msalesLoginUserInf;

            try {
                msalesLoginUserInf = MsalesJsonUtils.getObjectFromJSON(jsonString, MsalesLoginUserInf.class
                );
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            if (msalesLoginUserInf != null) {
                if (msalesLoginUserInf.getToken() == null || msalesLoginUserInf.getToken().trim().isEmpty()) {
                    LinkedHashMap hashErrors = new LinkedHashMap();
                    hashErrors.put("token", MsalesValidator.NOT_NULL_AND_EMPTY);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                            MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                ParameterList parameterList = new ParameterList(1, 1);
                parameterList.add("token", msalesLoginUserInf.getToken());
                parameterList.setOrder("lastAccessedTime", "DESC");

                List<Session> sessionList = dataService.getListOption(Session.class, parameterList);
                if (!sessionList.isEmpty()) {
                    parameterList = new ParameterList("users.id", sessionList.get(0).getUserId(), 0, 0);
                    parameterList.add("userRoles.id", 1);
                    parameterList.setOrder("userRoles.id");
                    List<UserRoleChannel> userRoleChannel = dataService.getListOption(UserRoleChannel.class, parameterList);

                    if (userRoleChannel.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                                MsalesStatus.NULL));
                    }

                    User userFromDB = userRoleChannel.get(0).getUsers();
//                    mUser = userFromDB;
                    // update login status to database
//                    userFromDB.setStatusId(10);
                    // set to http session
                    //request.getSession().setAttribute(MsalesConstants.MODULE.USER.NAME, mUser);
                    // update IP last login
                    //userFromDB.setIpLastLogin(request.getRemoteAddr());
                    // update to databse
                    dataService.updateRow(userFromDB);
                    // get user login
                    MsalesLoginUserInf loginInf = new MsalesLoginUserInf(userFromDB, userRoleChannel.get(0).getUserRoles(), sessionList.get(0).getToken(),userRoleChannel);
                    //update table session
                    sessionList.get(0).setLastAccessedTime(new Date());
                    dataService.updateRow(sessionList.get(0));
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                            HttpStatus.OK, loginInf));
                } else {
                    // password not match
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                            MsalesStatus.NULL));
                }
            } else {
                // user request login is not exists in database
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } else {
            // response to client json is invalid when request to server
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Admin login
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_LOGIN_ADMIN)
    public @ResponseBody
    String loginAdmin(HttpServletRequest request) {
        // get JSON string from csb controller.
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();

        if (jsonString != null) {
            User user;

            try {
                user = MsalesJsonUtils.getObjectFromJSON(jsonString, User.class
                );
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (user != null && user.getUsername() != null && user.getUsername().length() > 0) {
                String hql = "SELECT UserRoleChannel"
                        + " FROM UserRoleChannel AS UserRoleChannel"
                        + " JOIN UserRoleChannel.users AS User"
                        + " WHERE UserRoleChannel.deletedUser = 0"
                        + " AND User.deletedUser = 0"
                        + " AND User.username = '" + user.getUsername() + "'"
                        + " AND LCASE(User.password) = '" + user.getPassword().toLowerCase() + "'"
                        + " AND UserRoleChannel.userRoles.id = 1"
                        + " ORDER BY UserRoleChannel.userRoles.id ASC";

                List<UserRoleChannel> userRoleChannelList = dataService.executeSelectHQL(UserRoleChannel.class, hql, false, 0, 0);

                if (!userRoleChannelList.isEmpty()) {
                    //String passEncript = MsalesSession.getAccessToken(user.getPassword());
                    // check password
                    User userFromDB = userRoleChannelList.get(0).getUsers();
//                    mUser = userFromDB;
                    // update login status to database
//                    userFromDB.setStatusId(10);
                    // set to http session
                    //request.getSession().setAttribute(MsalesConstants.MODULE.USER.NAME, mUser);
                    // update IP last login
                    //userFromDB.setIpLastLogin(request.getRemoteAddr());
                    // update to databse
                    dataService.updateRow(userFromDB);
                    // get user login
                    MsalesLoginUserInf loginInf = new MsalesLoginUserInf(userFromDB, userRoleChannelList.get(0).getUserRoles(), request.getSession(),userRoleChannelList);
                    ParameterList parameterList = new ParameterList(1, 1);
                    parameterList.add("token", loginInf.getToken());
                    parameterList.setOrder("lastAccessedTime", "DESC");

                    List<Session> sessionList = dataService.getListOption(Session.class, parameterList);
                    if (sessionList.isEmpty()) {
                        // check permission
                        Session session = new Session();
                        session.setLastAccessedTime(new Date());
                        session.setToken(loginInf.getToken());
                        session.setUserId(userFromDB.getId());
                        //role = 1
                        session.setUserRolerId(userRoleChannelList.get(0).getUserRoles().getId());
                        dataService.insertRow(session);
                    }
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                            HttpStatus.OK, loginInf));
                } else {
                    // password not match
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                            MsalesStatus.USER_PASSWORD_NOT_MATCH));
                }

            } else {
                // user request login is not exists in database
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } else {
            // response to client json is invalid when request to server
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * User logout
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_LOGOUT)
    public @ResponseBody
    String logout(HttpServletRequest request) {
        // get session from request
        Session session = (Session) request.getAttribute(MsalesConstants.SESSION);
        if (session != null) {
            if (loginService.logout(session, dataService, request)) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
            }
        }

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.UNKNOW));
    }

    /**
     * check UserName
     *
     * @param request HttpservletRequest
     * @return Json String include List User
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_CHECK_USERNAME, method = RequestMethod.POST)
    public @ResponseBody
    String checkUserName(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //gan cung = 1, get companyId tu userLogin
        int companyId = 1;
        //jsonString not null
        if (jsonString != null) {
            User user;

            try {
                //parse jsonString to a search Object
                user = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        User.class
                );
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SearchUser from json not null
            if (user != null) {
                if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                    LinkedHashMap hashErrors = new LinkedHashMap();
                    hashErrors.put("username", MsalesValidator.NOT_NULL_AND_EMPTY);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED));
                }

                String hql = "FROM User"
                        + " WHERE username = '" + user.getUsername() + "'";

                List<User> list = dataService.executeSelectHQL(User.class, hql, false, 1, 1);
                if (list.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.USER_NAME_DUPLICATE, null));
                }
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
     * Search User
     *
     * @param request HttpservletRequest
     * @return Json String include List User
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_SEARCH, method = RequestMethod.POST)
    public @ResponseBody
    String searchUser(HttpServletRequest request) {
        //get List GoodsCategory from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();

        //gan cung = 1, get companyId tu userLogin
        int companyId = 1;
        //jsonString not null
        if (jsonString != null) {
            SearchUser searchUser;

            try {
                //parse jsonString to a search Object
                searchUser = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SearchUser.class
                );
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //SearchUser from json not null
            if (searchUser != null) {

                String hqlList = "SELECT DISTINCT(User.id) AS id,User.username AS username, CONCAT(User.lastName , ' ' , User.firstName) AS name,User.tel AS tel,"
                        + " UserRoleChannel.userRoles as userRoles,User.locations AS locations, User.isActive AS isActive,"
                        + " User.statuss AS statuss,User.userCode AS userCode";
                String hqlCount = "SELECT COUNT(DISTINCT User) AS VAL";

                String hql = " FROM UserRoleChannel as UserRoleChannel"
                        + " RIGHT JOIN UserRoleChannel.users AS User"
                        + " JOIN UserRoleChannel.channels AS Channel"
                        + " JOIN User.locations AS Location"
                        + " WHERE UserRoleChannel.deletedUser = 0"
                        + " AND User.deletedUser = 0"
                        + " AND User.companys.id = '" + companyId + "'";

                if (searchUser.getChannelId() != null && searchUser.getChannelId() != 0) {
                    Channel channel = dataService.getRowById(searchUser.getChannelId(), Channel.class
                    );
                    if (channel
                            != null) {
                        String channelCode = channel.getFullCode();
                        hql += " AND Channel.fullCode LIKE '" + channelCode + "%'";
                    } else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, new MsalesResults<User>()));
                    }
                }

                int isActive = 0;
                if (searchUser.isIsActive()) {
                    isActive = 1;
                }
                hql += " AND User.isActive = '" + isActive + "'";

                if (searchUser.getStatusId() != null && searchUser.getStatusId() != 0) {
                    hql += " AND User.statuss.id = '" + searchUser.getStatusId() + "'";
                }
                if (searchUser.getUserRoleId() != null && searchUser.getUserRoleId() != 0) {
                    hql += " AND UserRoleChannel.userRoles.id = '" + searchUser.getUserRoleId() + "'";
                }
                if (searchUser.getSearchText() != null && !searchUser.getSearchText().trim().isEmpty()) {
                    String searchText = searchUser.getSearchText().replaceAll("'", "''");
                    hql += " AND (CONCAT(User.lastName , ' ' , User.firstName) LIKE '%" + searchText + "%' OR User.userCode LIKE '%" + searchText + "%')";
                }
                if (searchUser.getLocationId() != null && searchUser.getLocationId() != 0) {
                    hql += " AND " + searchUser.getLocationId() + " IN (Location.id,Location.parents.id,Location.parents.parents.id,Location.parents.parents.parents.id)";
                }

                hqlCount += hql;
                hql += " GROUP BY User.id"
                        + " ORDER BY UserRoleChannel.userRoles.id,User.lastName,User.firstName,User.userCode";
                List<HashMap> list = dataService.executeSelectHQL(HashMap.class, hqlList + hql, true, page.getPageNo(), page.getRecordsInPage());

                List<HashMap> count = dataService.executeSelectHQL(HashMap.class, hqlCount, true, 0, 0);

                String[] filter = {"code", "parents", "locationType", "note", "lat", "lng", "statusTypes", "value"};
                MsalesResults<HashMap> results = new MsalesResults<>();

                results.setContentList(list);

                results.setCount(
                        (Long) count.get(0).get("VAL"));
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, results), filter);
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
     * get_Cb_List_User_By_Location_Id
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_GET_CB_LIST_USER_BY_LOCATION_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListUserByLocationId(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            User user = null;

            try {
                // parse jsonString to a Location Object
                user = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        User.class
                );
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }// location from json not null
            if (user != null) {
                if (user.getLocationId() != null) {
                    // get List Location by parentId
                    ParameterList parameterList = new ParameterList("locations.id", user.getLocationId());
                    MsalesResults<User> lists = dataService.getListOption(User.class, parameterList, true);
                    for (User users
                            : lists.getContentList()) {
                        users.setName("[" + users.getUserCode() + "] - " + users.getLastName() + " " + users.getFirstName());
                    }
                    // list location not null
                    //  if (list != null) {

                    String[] strings = {"statuss", "userCode", "birthday", "monitoringUsers",
                        "employerUserId", "isActive", "activeCode", "employerType",
                        "useEvoucher", "statusId", "username", "sex", "email",
                        "yahooId", "skypeId", "isdn", "tel", "address", "note",
                        "ipLastLogin", "updatedAt", "updatedUser", "employerUsers",
                        "createdUser", "companys", "lastName", "firstName", "locations"};

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
     * get Cb List User By ChannelId
     *
     * @param request is jsonString have channelId
     * @return info of users
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_GET_CB_LIST_USER_BY_CHANNEL_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListUserByChannelId(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            UserRoleChannel userRoleChannel;

            try {
                // parse jsonString to a userRoleChannel Object
                userRoleChannel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        UserRoleChannel.class
                );
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRoleChannel from json not null
            if (userRoleChannel != null) {
                // Channel from userRoleChannel with correct channelId
                if (userRoleChannel.getChannelId() != null) {
                    String hql = "Select U from User as U, UserRoleChannel as UserRoleChannel where U.id = UserRoleChannel.users.id ";
                    hql += " and UserRoleChannel.channels.id = " + userRoleChannel.getChannelId();
                    hql += " group by U.id ";
                    List<User> usList = dataService.executeSelectHQL(User.class, hql, false, 0, 0);
                    for (User user : usList) {
                        user.setName("[" + user.getUserCode() + "] - " + user.getLastName() + " " + user.getFirstName());
                    }
                    MsalesResults<User> users = new MsalesResults<User>();

                    users.setContentList(usList);

                    users.setCount(Long.parseLong(usList.size() + ""));
                    String[] strings = {"statuss", "lastName", "firstName", "tel", "userCode", "employerUsers", "username", "sex", "email", "isActive", "activeCode", "employerType", "useEvoucher", "statusId", "createdUser", "updatedUser", "locations", "monitoringUsers", "companys", "birthday", "yahooId", "skypeId", "isdn", "address", "note", "ipLastLogin"};

                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, users), strings);
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
     * get_Cb_List_User
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_GET_CB_LIST_USER, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListUser(HttpServletRequest request) {
        // get List UserRole from DB
        ParameterList parameterList = new ParameterList(0, 0);
        MsalesResults<User> list = dataService.getListOption(User.class, parameterList, true);
        for (User users
                : list.getContentList()) {
            users.setName("[" + users.getUserCode() + "] - " + users.getLastName() + " " + users.getFirstName());
        }
        // list location not null
        //  if (list != null) {

        String[] strings = {"statuss", "userCode", "birthday", "monitoringUsers",
            "employerUserId", "isActive", "activeCode", "employerType",
            "useEvoucher", "statusId", "username", "sex", "email",
            "yahooId", "skypeId", "isdn", "tel", "address", "note",
            "ipLastLogin", "updatedAt", "updatedUser", "employerUsers",
            "createdUser", "companys", "lastName", "firstName", "locations"};

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), strings);
    }

    /**
     * search Admin User DuanND
     *
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_SEARCH_ADMIN_USER, method = RequestMethod.POST)
    public @ResponseBody
    String searchAdminUser(HttpServletRequest request) {
        //get List GoodsCategory from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
     //   ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            LinkedHashMap<String, Object> searchObject;

            try {
                //parse jsonString to a search Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        LinkedHashMap.class
                );
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            String hql = "select DISTINCT(U.id) as id,U.firstName as firstName,U.lastName as lastName,U.userCode as code, U.tel as tel,U.employerUsers as employerUser "
                    + "from User as U,UserRoleChannel as UserRoleChannel "
                    + "where U.id=UserRoleChannel.users.id";
            //searchObject from json not null
            if (searchObject != null) {
                if (searchObject.get("searchText") != null) {
                    String key = searchObject.get("searchText").toString();
                    hql += " and CONCAT(U.lastName, ' ', U.firstName) LIKE '%" + key + "%'";
                }
                if (searchObject.get("locationId") != null) {
                    try {
                        int locationId = Integer.parseInt(searchObject.get("locationId").toString());
                        hql += " and U.locations.id = " + locationId;
                        //parameterList.add("locations.id", locationId);
                    } catch (Exception ex) {
                        //error parse goodsCategoryId
                    }
                }
                if (searchObject.get("searchField") != null) {
                    String key = searchObject.get("searchField").toString();
                    hql += "and ( CONCAT(U.lastName, ' ', U.firstName) LIKE '%" + key + "%'" + " or U.userCode LIKE '%" + key + "%'" + " or U.address LIKE '%" + key + "%'" + " or U.tel LIKE '%" + key + "%' ) ";
                }
            }

            hql += " and UserRoleChannel.userRoles.id = " + 2;
            hql += " group by U.id";
            List<User> lists = dataService.executeSelectHQL(User.class, hql, true, 0, 0);

//            MsalesResults<User> usResults = new MsalesResults<User>();
//            usResults.setContentList(lists);
//            usResults.setCount(Long.parseLong(lists.size() + ""));
            MsalesResults<User> listMCPs = new MsalesResults<User>();

            listMCPs.setCount(Long.parseLong(lists.size() + ""));
            if (page.getPageNo()
                    == 1) {
                if (page.getRecordsInPage() == 10) {
                    if (lists.size() <= 10) {
                        listMCPs.setContentList(lists);
                    } else {
                        listMCPs.setContentList(lists.subList(0, 10));
                    }
                } else {
                    if (lists.size() <= page.getRecordsInPage()) {
                        listMCPs.setContentList(lists);
                    } else {
                        listMCPs.setContentList(lists.subList(0, page.getRecordsInPage()));
                    }
                }
            } else {
                if (page.getRecordsInPage() == 10) {
                    if (lists.size() <= 10) {

                    } else {
                        if (lists.size() / 10 + 1 == page.getPageNo()) {
                            listMCPs.setContentList(lists.subList((page.getPageNo() - 1) * page.getRecordsInPage(), lists.size()));
                        } else if (lists.size() / 10 + 1 >= page.getPageNo()) {
                            listMCPs.setContentList(lists.subList((page.getPageNo() - 1) * page.getRecordsInPage(), page.getPageNo() * page.getRecordsInPage()));
                        } else {

                        }
                    }
                } else {
                    if (lists.size() <= page.getRecordsInPage()) {

                    } else {
                        if (lists.size() / page.getRecordsInPage() + 1 == page.getPageNo()) {
                            listMCPs.setContentList(lists.subList((page.getPageNo() - 1) * page.getRecordsInPage(), lists.size()));
                        } else if (lists.size() / page.getRecordsInPage() + 1 >= page.getPageNo()) {
                            listMCPs.setContentList(lists.subList((page.getPageNo() - 1) * page.getRecordsInPage(), page.getPageNo() * page.getRecordsInPage()));
                        } else {

                        }
                    }
                }
            }
            //  List<User> list = dataService.getListOption(User.class, parameterList);
            String[] strings = {"statuss", "tel", "userCode", "employerUsers", "username", "sex", "email", "isActive", "activeCode", "employerType", "useEvoucher", "statusId", "createdUser", "updatedUser", "locations", "monitoringUsers", "companys", "birthday", "yahooId", "skypeId", "isdn", "address", "note", "ipLastLogin"};

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listMCPs), strings);
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * lockUser
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_LOCK_USER)
    public @ResponseBody
    String lockUser(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //gan cung userLogin  = 1
        //chua check quyen
        int userLoginId = 1;
        if (jsonString != null) {
            User user;

            try {
                // Convert JSOn string to LinkedHashMap object
                user = MsalesJsonUtils.getObjectFromJSON(jsonString, User.class
                );
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (user != null) {
                if (user.getId() == null) {
                    return MsalesJsonUtils.idNull();

                }

                user = dataService.getRowById(user.getId(), User.class
                );
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (user == null) {
                    hashErrors.put("User", "ID = " + user.getId() + " " + MsalesValidator.NOT_EXIST);
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                if (user.getIsActive() == 0) {
                    user.setIsActive(1);
                } else {
                    user.setIsActive(0);
                }

                user.setUpdatedAt(new Date());
                user.setUpdatedUser(userLoginId);

                try {
                    dataService.updateRow(user);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                } catch (Exception ex) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_UPDATE_FAIL));
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }
    }

    /**
     * reset password user
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_RESET_PASSWORD_USER)
    public @ResponseBody
    String resetPassword(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //gan cung userLogin  = 1
        //chua check quyen
        int userLoginId = 1;
        if (jsonString != null) {
            User user;

            try {
                // Convert JSOn string to LinkedHashMap object
                user = MsalesJsonUtils.getObjectFromJSON(jsonString, User.class
                );
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (user != null) {
                if (user.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                user = dataService.getRowById(user.getId(), User.class);
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (user == null) {
                    hashErrors.put("User", "ID = " + user.getId() + " " + MsalesValidator.NOT_EXIST);
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                //check quyen
                //gan lai mat khau 123456
                user.setPassword("8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92");
                user.setUpdatedAt(new Date());
                user.setUpdatedUser(userLoginId);

                try {
                    dataService.updateRow(user);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                } catch (Exception ex) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_UPDATE_FAIL));
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }
    }

//    /**
//     * unlockUser
//     */
//    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_UNLOCK_USER)
//    public @ResponseBody
//    String unlockUser(HttpServletRequest request) {
//        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
//        //gan cung userLogin  = 1
//        //chua check quyen
//        int userLoginId = 1;
//        if (jsonString != null) {
//            User user;
//
//            try {
//                // Convert JSOn string to LinkedHashMap object
//                user = MsalesJsonUtils.getObjectFromJSON(jsonString, User.class);
//            } catch (Exception ex) {
//                // check field in JSOn request not match with field in JSON.
//                return MsalesJsonUtils.validateFormat(ex);
//            }
//            if (user != null) {
//                if (user.getId() == null) {
//                    return MsalesJsonUtils.idNull();
//                }
//
//                user = dataService.getRowById(user.getId(), User.class);
//                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
//                if (user == null) {
//                    hashErrors.put("User", "ID = " + user.getId() + " " + MsalesValidator.NOT_EXIST);
//                }
//                if (!hashErrors.isEmpty()) {
//                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
//                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
//                }
//
//                user.setIsActive(1);
//                user.setUpdatedAt(new Date());
//                user.setUpdatedUser(userLoginId);
//                try {
//                    dataService.updateRow(user);
//                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
//                } catch (Exception ex) {
//                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_UPDATE_FAIL));
//                }
//            } else {
//                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
//                        .create(MsalesStatus.NULL));
//            }
//        } else {
//            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
//                    .create(MsalesStatus.NULL));
//        }
//    }
    /**
     * getListUserByChannelId
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_GET_LIST_USER_BY_CHANNEL_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListUserByChannelId(HttpServletRequest request
    ) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            UserRoleChannel userRoleChannel;

            try {
                // parse jsonString to a userRoleChannel Object
                userRoleChannel = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        UserRoleChannel.class
                );
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // userRoleChannel from json not null
            if (userRoleChannel != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                ArrayList<Integer> arrayChannelId = new ArrayList<Integer>();
                //List<LinkedHashMap<String, String>> list2 = new ArrayList<LinkedHashMap<String,String>>();
                List<User> usList = new ArrayList<User>();
                // Channel from userRoleChannel with correct channelId
                if (userRoleChannel.getChannelId() != null) {
                    // get List userRoleChannel by channelId
                    ParameterList parameterList2 = new ParameterList("channels.id", userRoleChannel.getChannelId());
                    List<UserRoleChannel> list = dataService.getListOption(UserRoleChannel.class, parameterList2);
                    int[] userId = new int[list.size()];
                    int count = 0;
                    for (UserRoleChannel user : list) {
                        if (user != null) {
                            userId[count] = user.getUsers().getId();
                            count++;
                        }
                    }
                    for (int i = 0;
                            i < userId.length;
                            i++) {
                        boolean blean = true;
                        for (int j = 0; j < i; j++) {
                            if ((userId[i] == userId[j]) || userId[i] == 0) {
                                blean = false;
                                break;
                            }
                        }
                        if (blean) {
                            //parameterList.add("implementEmployees.id", userId[i]);
                            arrayChannelId.add(userId[i]);
                        }
                    }
                    for (int i = 0;
                            i < arrayChannelId.size();
                            i++) {
                        User user = dataService.getRowById(arrayChannelId.get(i), User.class);
                        if (user != null) {
                            List<UserRoleChannel> uChannels = dataService.getListOption(UserRoleChannel.class, new ParameterList("users.id", user.getId()));
                            int minUserRoleId = 0;
                            for (int j = 0; j < uChannels.size() - 1; j++) {
                                if (uChannels.get(j).getId() < uChannels.get(j + 1).getId()) {
                                    minUserRoleId = uChannels.get(j).getId();
                                } else {
                                    minUserRoleId = uChannels.get(j + 1).getId();
                                }
                            }
                            UserRole userRole = dataService.getRowById(minUserRoleId, UserRole.class);
                            user.setName(user.getLastName() + " " + user.getFirstName());
                            user.setUserRole(userRole);
                            user.getMonitoringUsers().setName(user.getMonitoringUsers().getLastName() + " " + user.getMonitoringUsers().getFirstName());
                            user.setMonitoringUsers(user.getMonitoringUsers());
                            usList.add(user);
                        }
                    }
                    MsalesResults<User> users = new MsalesResults<User>();

                    users.setContentList(usList);

                    users.setCount(Long.parseLong(usList.size() + ""));
                    String[] strings = {"lastName", "firstName", "statuss", "employerUsers", "username", "sex", "email", "isActive", "activeCode", "employerType", "useEvoucher", "statusId", "createdUser", "updatedUser", "locations", "companys", "birthday", "yahooId", "skypeId", "isdn", "address", "note", "ipLastLogin"};

                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, users), strings);
                } // channel from json with incorrect channelId
                else {
                    hashErrors.put("channelId", MsalesValidator.NOT_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
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

    //GetListUser theo tá»‰nh thĂ nh
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_GET_CB_LIST_USER_BY_TINH_THANH_PHO, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListUserByTinhThanhPho(HttpServletRequest request
    ) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            User user = null;

            try {
                // parse jsonString to a Location Object
                user = MsalesJsonUtils.getObjectFromJSON(jsonString, User.class
                );
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }// location from json not null
            if (user != null) {
                if (user.getLocationId() != null) {
                    // get List Location by parentId
                    String hql = "Select U from User as U where locations.id = " + user.getLocationId()
                            + " or locations.parents.id = " + user.getLocationId() + " or locations.parents.parents.id = " + user.getLocationId();
                    List<User> lists = dataService.executeSelectHQL(User.class, hql, false, 0, 0);
                    for (User users : lists) {
                        users.setName("[" + users.getUserCode() + "] - " + users.getLastName() + " " + users.getFirstName());
                    }
                    // list location not null
                    //  if (list != null) {
                    MsalesResults<User> listUsers = new MsalesResults<User>();

                    listUsers.setContentList(lists);

                    listUsers.setCount(Long.parseLong(lists.size() + ""));
                    String[] strings = {"statuss", "userCode", "birthday", "monitoringUsers",
                        "employerUserId", "isActive", "activeCode", "employerType",
                        "useEvoucher", "statusId", "username", "sex", "email",
                        "yahooId", "skypeId", "isdn", "tel", "address", "note",
                        "ipLastLogin", "updatedAt", "updatedUser", "employerUsers",
                        "createdUser", "companys", "lastName", "firstName", "locations"};

                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listUsers), strings);
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
     * Lay danh sach nhan vien theo channel
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_GET_CB_LIST_USER_BY_CHANNEL_AND_LOCATION, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListUserByChannelAndLocation(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();

        //gan cung company
        int companyId = 1;

        if (jsonString != null) {
            SearchObject searchObject;

            try {
                // parse jsonString to a Location Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }// location from json not null
            if (searchObject != null) {

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (searchObject.getChannelId() == null) {
                    hashErrors.put("channelId", MsalesValidator.NOT_NULL);
                }
                if (searchObject.getLocationId() == null) {
                    hashErrors.put("locationId", MsalesValidator.NOT_NULL);
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //tra ve tat ca ko tinh channelId
                String channelHQL = "";

                if (searchObject.getChannelId() != 0) {
                    Channel channel = dataService.getRowById(searchObject.getChannelId(), Channel.class
                    );
                    if (channel != null) {
                        channelHQL = " AND UserRoleChannel.channels.fullCode LIKE '" + channel.getFullCode() + "%' ";
                    } else {
                        //channel khong ton tai=> tra ve rong
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, new MsalesResults<User>()));
                    }
                }

                //location = 0 => tra ve tat ca ko tinh locationId
                String locationHQL = "";

                if (searchObject.getLocationId() != 0) {
                    Location location = dataService.getRowById(searchObject.getLocationId(), Location.class
                    );
                    if (location
                            != null) {
                        //hql += " AND (Location.code LIKE '" + location.getCode() + "%'";
                        locationHQL = " AND " + location.getId() + " IN (Location.id,Location.parents.id,Location.parents.parents.id,Location.parents.parents.parents.id )";
                    } else {
                        //location khong ton tai=> tra ve rong.
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, new MsalesResults<User>()));
                    }

                }

                String hql = "SELECT DISTINCT User.id AS id,"
                        + " CONCAT('[',UserRoleChannel.users.userCode,']',' - ',UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name "
                        + " FROM UserRoleChannel AS UserRoleChannel"
                        + " RIGHT JOIN UserRoleChannel.users AS User"
                        + " JOIN User.locations AS Location"
                        + " WHERE UserRoleChannel.deletedUser = 0"
                        + " AND User.deletedUser = 0"
                        + " AND User.companys.id = '" + companyId + "'";
                hql += channelHQL + locationHQL;
                hql += " GROUP BY User.id"
                        + " ORDER BY User.lastName,User.firstName,User.userCode";

                List<HashMap> userList = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);

                MsalesResults<HashMap> result = new MsalesResults<>();

                result.setContentList(userList);

                result.setCount(
                        (long) userList.size()
                );

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, result));
            } // location from json with incorrect parentId
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } // location from json null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Lay danh sach monitoring theo channel
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.USER.ACTION_GET_CB_LIST_MONITORING_BY_CHANNEL, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListMonototingByChannel(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();

        //gan cung company
        int companyId = 1;

        if (jsonString != null) {
            SearchObject searchObject;

            try {
                // parse jsonString to a Location Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString, SearchObject.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }// location from json not null
            if (searchObject != null) {

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (searchObject.getChannelId() == null) {
                    hashErrors.put("channelId", MsalesValidator.NOT_NULL);
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //lay nhan vien tu role 3
                String hql = "SELECT DISTINCT User.id AS id,"
                        + " CONCAT('[',UserRoleChannel.users.userCode,']',' - ',UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name "
                        + " FROM UserRoleChannel AS UserRoleChannel"
                        + " JOIN UserRoleChannel.users AS User"
                        + " WHERE UserRoleChannel.deletedUser = 0"
                        + " AND User.deletedUser = 0"
                        + " AND User.companys.id = '" + companyId + "'"
                        + " AND UserRoleChannel.channels.id = '" + searchObject.getChannelId() + "%' "
                        + " AND UserRoleChannel.userRoles.id > 1"
                        + " AND UserRoleChannel.userRoles.id < 6"
                        + " GROUP BY User.id"
                        + " ORDER BY User.lastName,User.firstName,User.userCode";

                List<HashMap> userList = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);

                //lay them admin cua channelParent hoac adminCompany neu la channel cap 1
                Channel channel = dataService.getRowById(searchObject.getChannelId(), Channel.class);
                if (channel
                        != null) {
                    if (channel.getParents() != null) {
                        String hqlParent = "SELECT DISTINCT User.id AS id,"
                                + " CONCAT('[',UserRoleChannel.users.userCode,']',' - ',UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name "
                                + " FROM UserRoleChannel AS UserRoleChannel"
                                + " JOIN UserRoleChannel.users AS User"
                                + " WHERE UserRoleChannel.deletedUser = 0"
                                + " AND User.deletedUser = 0"
                                + " AND User.companys.id = '" + companyId + "'"
                                + " AND UserRoleChannel.channels.parents.id = '" + channel.getParents().getId() + "%' "
                                + " AND UserRoleChannel.userRoles.id = 2"//lay role admin channel
                                + " GROUP BY User.id"
                                + " ORDER BY User.lastName,User.firstName,User.userCode";
                        List<HashMap> userParentList = dataService.executeSelectHQL(HashMap.class, hqlParent, true, 0, 0);
                        userList.addAll(userParentList);
                    } else {
                        String hqlAdmin = "SELECT DISTINCT User.id AS id,"
                                + " CONCAT('[',UserRoleChannel.users.userCode,']',' - ',UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name "
                                + " FROM UserRoleChannel AS UserRoleChannel"
                                + " JOIN UserRoleChannel.users AS User"
                                + " WHERE UserRoleChannel.deletedUser = 0"
                                + " AND User.deletedUser = 0"
                                + " AND User.companys.id = '" + companyId + "'"
                                + " AND UserRoleChannel.userRoles.id = 1"//lay role admin company
                                + " GROUP BY User.id"
                                + " ORDER BY User.lastName,User.firstName,User.userCode";
                        List<HashMap> userAdmintList = dataService.executeSelectHQL(HashMap.class, hqlAdmin, true, 0, 0);
                        userList.addAll(userAdmintList);
                    }
                }

                MsalesResults<HashMap> result = new MsalesResults<>();

                result.setContentList(userList);

                result.setCount(
                        (long) userList.size()
                );

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, result));
            } // location from json with incorrect parentId
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } // location from json null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

}
