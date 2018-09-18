/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.goods.controller;

import java.text.SimpleDateFormat;
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
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.GoodsCategory;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserGoodsCategory;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;

/**
 *
 * @author KhaiCH
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.USERGOODSCATEGORY.NAME)
public class MsalesUserGoodsCategoryController extends CsbController {

    /**
     * create UserGoodsCategory
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.USERGOODSCATEGORY.ACTION_CREATE_USERGOODSCATEGORY, method = RequestMethod.POST)
    public @ResponseBody
    String createUserGoodsCategory(HttpServletRequest request) {

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            UserGoodsCategory userGoodsCategory;
            try {
                //parse jsonString to a UserGoodsCategory Object
                userGoodsCategory = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        UserGoodsCategory.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //UserGoodsCategory from json not null
            if (userGoodsCategory != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (userGoodsCategory.getUserId()!= null) {
                    User user = dataService.getRowById(userGoodsCategory.getUserId(), User.class);
                    //user is not exist
                    if (user == null) {
                        //return message warning user is not exist in DB
                        hashErrors.put("User","ID = " + userGoodsCategory.getUserId()+ " không tồn tại.");
                    }
                }
                if (userGoodsCategory.getGoodsCategoryId()!= null) {
                    GoodsCategory goodsCategory = dataService.getRowById(userGoodsCategory.getGoodsCategoryId(), GoodsCategory.class);
                    //goodsCategory is not exist
                    if (goodsCategory == null) {
                        //return message warning GoodsCategory is not exist in DB
                        hashErrors.put("GoodsCategory","ID = " + userGoodsCategory.getGoodsCategoryId()+ " không tồn tại.");
                    }
                }
                
                if (userGoodsCategory.getStatusId()!= null) {
                    Status status = dataService.getRowById(userGoodsCategory.getStatusId(), Status.class);
                    //Status is not exist
                    if (status == null) {
                        //return message warning GoodsCategory is not exist in DB
                        hashErrors.put("Status","ID = " + userGoodsCategory.getStatusId()+ " không tồn tại.");
                    }
                }
                
                if (userGoodsCategory.getCreatedUser() != null) {
                    User user = dataService.getRowById(userGoodsCategory.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("CreatedUser", "ID = " + userGoodsCategory.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save UserGoodsCategory to DB
                    int ret = dataService.insertRow(userGoodsCategory);

                    //save Succcess
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
            } //status from json null
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
     * update a UserGoodsCategory
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.USERGOODSCATEGORY.ACTION_UPDATE_USERGOODSCATEGORY, method = RequestMethod.POST)
    public @ResponseBody
    String updateUserGoodsCategory(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            UserGoodsCategory userGoodsCategory;
            try {
                userGoodsCategory = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        UserGoodsCategory.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //UserGoodsCategory from json not null
            if (userGoodsCategory != null) {
                if (userGoodsCategory.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (userGoodsCategory.getUserId()!= null) {
                    User user = dataService.getRowById(userGoodsCategory.getUserId(), User.class);
                    //user is not exist
                    if (user == null) {
                        //return message warning user is not exist in DB
                        hashErrors.put("User","ID = " + userGoodsCategory.getUserId()+ " không tồn tại.");
                    }
                }
                if (userGoodsCategory.getGoodsCategoryId()!= null) {
                    GoodsCategory goodsCategory = dataService.getRowById(userGoodsCategory.getGoodsCategoryId(), GoodsCategory.class);
                    //goodsCategory is not exist
                    if (goodsCategory == null) {
                        //return message warning GoodsCategory is not exist in DB
                        hashErrors.put("GoodsCategory","ID = " + userGoodsCategory.getGoodsCategoryId()+ " không tồn tại.");
                    }
                }
                if (userGoodsCategory.getStatusId()!= null) {
                    Status status = dataService.getRowById(userGoodsCategory.getStatusId(), Status.class);
                    //Status is not exist
                    if (status == null) {
                        //return message warning GoodsCategory is not exist in DB
                        hashErrors.put("Status","ID = " + userGoodsCategory.getStatusId()+ " không tồn tại.");
                    }
                }
                
                if (userGoodsCategory.getUpdatedUser()!= null) {
                    User user = dataService.getRowById(userGoodsCategory.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("UpdatedUser", "ID = " + userGoodsCategory.getUpdatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                int id = userGoodsCategory.getId();

                try {
                    //update UserGoodsCategory to DB
                    int ret = dataService.updateSync(userGoodsCategory);
                    //update success
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(Status.class, id);
                    } else if (ret > 0) {
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
            } //UserGoodsCategory from json null
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
     * delete a UserGoodsCategory
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.USERGOODSCATEGORY.ACTION_DELETE_USERGOODSCATEGORY, method = RequestMethod.POST)
    public @ResponseBody
    String deleteUserGoodsCategory(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            UserGoodsCategory userGoodsCategory;
            try {
                //parse jsonString to a UserGoodsCategory Object
                userGoodsCategory = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        UserGoodsCategory.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //UserGoodsCategory from json not null
            if (userGoodsCategory != null) {
                if (userGoodsCategory.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                if (userGoodsCategory.getDeletedUser() != null) {
                    User user = dataService.getRowById(userGoodsCategory.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, userGoodsCategory.getDeletedUser());
                    }
                }

                int id = userGoodsCategory.getId();
                try {
                    //update delete UserGoodsCategory
                    int ret = dataService.deleteSynch(userGoodsCategory);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(Status.class, id);
                    }
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update delete failed
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

            } //UserGoodsCategory from json null
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
     * get List all UserGoodsCategory by userId
     *
     * @param request is a HttpServletRequest
     * @return string json include List UserGoodsCategory
     */   
    @RequestMapping(value = MsalesConstants.MODULE.USERGOODSCATEGORY.ACTION_GET_LIST_USERGOODSCATEGORY, method = RequestMethod.POST)
    public @ResponseBody
    String getListUserGoodsCategory(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            UserGoodsCategory userGoodsCategory;
            try {
                //parse jsonString to a UserGoodsCategory Object
                userGoodsCategory = MsalesJsonUtils.getObjectFromJSON(jsonString,UserGoodsCategory.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //UserGoodsCategory from json not null
            if (userGoodsCategory != null) {
                if (userGoodsCategory.getUserId()== null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("UserGoodsCategory", "userId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                //get List UserGoodsCategory by userId
                ParameterList parameterList = new ParameterList("users.id", userGoodsCategory.getUserId(), page.getPageNo(), page.getRecordsInPage());
                parameterList.add("statuss.id",15);
                MsalesResults<UserGoodsCategory> list = dataService.getListOption(UserGoodsCategory.class, parameterList,true);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));
            } //UserGoodsCategory from json null
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
     * get Cb List  UserGoodsCategory by userId
     *
     * @param request is a HttpServletRequest
     * @return string json include List UserGoodsCategory
     */   
    @RequestMapping(value = MsalesConstants.MODULE.USERGOODSCATEGORY.ACTION_GET_CB_LIST_USERGOODSCATEGORY_BY_USER_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListUserGoodsCategoryByUserId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            UserGoodsCategory userGoodsCategory;
            try {
                //parse jsonString to a UserGoodsCategory Object
                userGoodsCategory = MsalesJsonUtils.getObjectFromJSON(jsonString,UserGoodsCategory.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //UserGoodsCategory from json not null
            if (userGoodsCategory != null) {
                if (userGoodsCategory.getUserId()== null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("UserGoodsCategory", "userId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                //get List UserGoodsCategory by userId
                ParameterList parameterList = new ParameterList("users.id", userGoodsCategory.getUserId());
                //companyId theo login user - chua fix - gan cung 1
                parameterList.add("users.companys.id", 1);
                Date now = new Date();
                parameterList.add("beginAt",now,"<=");
                parameterList.add("endAt", now,">=");
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String hqlCount = "SELECT COUNT(DISTINCT UserGoodsCategory.goodsCategorys.id) AS VAL";
                String hqlList = "SELECT UserGoodsCategory.goodsCategorys.id AS id, UserGoodsCategory.goodsCategorys.name AS name";
                String hql = " FROM UserGoodsCategory AS UserGoodsCategory"
                        + " WHERE UserGoodsCategory.deletedUser = 0"
                        + " AND UserGoodsCategory.users.id = '" + userGoodsCategory.getUserId() + "'"
                        + " AND UserGoodsCategory.users.companys.id = '" + 1 +"'"
                        + " AND UserGoodsCategory.statuss.id = 15"//trang thai = 15
                        + " AND (UserGoodsCategory.beginAt IS NULL OR UserGoodsCategory.beginAt <= '" + sdf.format(now) + "')"
                        + " AND (UserGoodsCategory.endAt IS NULL OR UserGoodsCategory.endAt >= '" + sdf.format(now) + "')";
                
                List<HashMap> countList = dataService.executeSelectHQL(HashMap.class, hqlCount + hql, true, 0, 0);
                List<HashMap> mapList = dataService.executeSelectHQL(HashMap.class, hqlList + hql, true, 0, 0);
                
                long count = (long) countList.get(0).get("VAL");
                MsalesResults<HashMap> list = new MsalesResults<>();
                list.setCount(count);
                list.setContentList(mapList);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
            } //UserGoodsCategory from json null
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