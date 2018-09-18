/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.goods.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.io.IOException;
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
import vn.itt.msales.entity.GoodsCategory;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.logex.MSalesException;

/**
 *
 * @author Cutx
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.GOODS_CATEGORY.NAME)
public class MsalesGoodsCategoryController extends CsbController {

    /**
     * get a GoodsCategory
     *
     * @param request is a HttpServletRequest
     * @return string json include GoodsCategory info
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_GET_GOODS_CATEGORY, method = RequestMethod.POST)
    public @ResponseBody
    String getGoodsCategory(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            GoodsCategory goodsCategory;
            try {
                //parse jsonString to a GoodsCategory Object
                goodsCategory = (GoodsCategory) MsalesJsonUtils.getObjectFromJSON(jsonString, GoodsCategory.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_INVALID));
            }
            //GoodsCategory from json not null
            if (goodsCategory != null && goodsCategory.getId() != null) {

                //get GoodsCategory from DB
                goodsCategory = dataService.getRowById(goodsCategory.getId(), GoodsCategory.class);
                //GoodsCategory not null
                if (goodsCategory != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, goodsCategory));
                } //GoodsCategory null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                }
            } //GoodsCategory from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * create a GoodsCategory
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_CREATE_GOODS_CATEGORY, method = RequestMethod.POST)
    public @ResponseBody
    String createGoodsCategory(HttpServletRequest request) {
        //get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();

        //jsonString not null
        if (jsonString != null) {
            GoodsCategory goodsCategory;
            try {
                //parse jsonString to a GoodsCategory Object
                goodsCategory = MsalesJsonUtils.getObjectFromJSON(jsonString, GoodsCategory.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);

            }
            //GoodsCategory from json not null
            if (goodsCategory != null) {

                try {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    if (goodsCategory.getCompanyId() != null) {
                        Company company = dataService.getRowById(goodsCategory.getCompanyId(), Company.class);
                        if (company == null) {
                            hashErrors.put("Company", "with ID = " + goodsCategory.getCompanyId() + " không tồn tại.");
                        }
                    }

                    if (goodsCategory.getCreatedUser() != null) {
                        User user = dataService.getRowById(goodsCategory.getCreatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", "with ID = " + goodsCategory.getCreatedUser() + " không tồn tại.");
                        }
                    }
                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    //save GoodsCategory to DB
                    int ret = dataService.insertRow(goodsCategory);

                    //save success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, ret));
                    } //save failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_INSERT_FAIL));
                    }

                } catch (MSalesException e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }

                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));

                }

            } //GoodsCategory from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Update data to GoodsCategory
     *
     * @param request is jsonString contain data to update
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_UPDATE_GOODS_CATEGORY, method = RequestMethod.POST)
    public @ResponseBody
    String updateGoodsCategory(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            GoodsCategory goodsCategory;
            try {
                //parse jsonString to a GoodsCategory Object
                goodsCategory = MsalesJsonUtils.getObjectFromJSON(jsonString, GoodsCategory.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //GoodsCategory from json not null
            if (goodsCategory != null) {
                if (goodsCategory.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
                }
                int ret = 0;
                try {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    if (goodsCategory.getUpdatedUser() != null) {
                        User user = dataService.getRowById(goodsCategory.getUpdatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", "User with ID = " + goodsCategory.getUpdatedUser() + " không tồn tại.");
                        }
                    }
                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    GoodsCategory rootGoodsCategory = dataService.getRowById(goodsCategory.getId(), GoodsCategory.class);
                    if (rootGoodsCategory == null) {
                        hashErrors.put("GoodsCategory", "ID = " + goodsCategory.getId() + " không tồn tại.");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    //not update company
                    goodsCategory.setCompanys(rootGoodsCategory.getCompanys());

                    //update GoodsCategory to DB
                    ret = dataService.updateSync(goodsCategory);
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    if (e.getCause() instanceof MySQLIntegrityConstraintViolationException) {
                        // response to client the user request create is duplicate.
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.ERROR_NAME_DUPLICATE));
                    }
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.UNKNOW));
                }
                //Update success
                if (ret == -2) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                    hashErrors.put("GoodsCategory",
                            "GoodsCategory with ID = " + goodsCategory.getId() + " không tồn tại.");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                } else if (ret > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                } //Update failed
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_UPDATE_FAIL));
                }

            } //GoodsCategory from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Delete a GoodsCategory
     *
     * @param request jsonString contain goods_category_id to delete
     * goodsCategory
     * @return content, code and status
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_DELETE_GOODS_CATEGORY, method = RequestMethod.POST)
    public @ResponseBody
    String deleteGoodsCategory(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            GoodsCategory goodsCategory;
            try {
                //parse jsonString to a GoodsCategory Object
                goodsCategory = MsalesJsonUtils.getObjectFromJSON(jsonString, GoodsCategory.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //GoodsCategory from json not null
            if (goodsCategory != null) {
                if (goodsCategory.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
                }

                if (goodsCategory.getDeletedUser() != null) {
                    User user = dataService.getRowById(goodsCategory.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, goodsCategory.getDeletedUser());
                    }
                }
                try {

                    //delete GoodsCategory from DB
                    int ret = dataService.deleteSynch(goodsCategory);
                    //delete GoodsCategory success
                    if (ret == -2) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("GoodsCategory",
                                "GoodsCategory with ID = " + goodsCategory.getId() + " không tồn tại.");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                    } //GoodsCategory failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));
                }
            } //GoodsCategory from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get List all GoodsCategory of Company
     *
     * @param request is a HttpServletRequest
     * @return string json include List GoodsCategory
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_GET_LIST_GOODS_CATEGORY, method = RequestMethod.POST)
    public @ResponseBody
    String getListGoodsCategory(HttpServletRequest request) {
        //get List GoodsCategory from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            GoodsCategory goodsCategory;
            try {
                //parse jsonString to a GoodsCategory Object
                goodsCategory = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        GoodsCategory.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (goodsCategory != null) {
                if (goodsCategory.getCompanyId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("GoodsCategory", "companyId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

//                ParameterList parameterList = new ParameterList("companys.id", goodsCategory.getCompanyId(), page.getPageNo(), page.getRecordsInPage());
                parameterList.add("companys.id", goodsCategory.getCompanyId());
                MsalesResults<GoodsCategory> list = dataService.getListOption(GoodsCategory.class, parameterList, true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
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
     * Search GoodsCategory
     *
     * @param request HttpservletRequest
     * @return Json String include List Category
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_SEARCH_GOODS_CATEGORY, method = RequestMethod.POST)
    public @ResponseBody
    String searchGoodsCategory(HttpServletRequest request) throws JsonMappingException, IOException {

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

            //searchObject from json not null
            if (searchObject != null) {
                if (searchObject.get("searchText") != null) {
                    String key = searchObject.get("searchText").toString();
                    parameterList.or("goodsCode", key, "like", "name", key, "like");
                }
                if (searchObject.get("statusId") != null) {
                    try {
                        int statusId = Integer.parseInt(searchObject.get("statusId").toString());
                        parameterList.add("statuss.id", statusId);
                    } catch (Exception ex) {
                        //error parse statusId
                    }
                }

            }
            MsalesResults<GoodsCategory> list = dataService.getListOption(GoodsCategory.class, parameterList, true);

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get Combobox List GoodsCategory
     * @param request
     * @return 
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_GET_CB_LIST_GOODS_CATEGORY, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListGoodsCategory(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            GoodsCategory goodsCategory;
            try {
                //parse jsonString to a search Object
                goodsCategory = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        GoodsCategory.class);
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (goodsCategory.getCompanyId() == null) {
                LinkedHashMap hashErrors = new LinkedHashMap();
                hashErrors.put("companyId", MsalesValidator.NOT_NULL);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                        MsalesStatus.JSON_FIELD_REQUIRED,hashErrors));
            }

            ParameterList parameterList = new ParameterList("companys.id", goodsCategory.getCompanyId());
            MsalesResults<GoodsCategory> list = dataService.getListOption(GoodsCategory.class, parameterList, true);
            // list not null display list
            String[] filter = {"order", "goodsCode", "statuss"};
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                    HttpStatus.OK, list), filter);

//        Session session = (Session) request.getAttribute("session");
//        if (session == null) {
//            //not login
//            return "login";
//        }
//        int userId = session.getUserId();
//
//        String hql = "SELECT UserGoodsCategory.goodsCategorys"
//                + " FROM UserGoodsCategory AS UserGoodsCategory"
//                + " WHERE UserGoodsCategory.users.id ='" + userId + "'";
//
//        List<GoodsCategory> list = dataService.executeSelectHQL(GoodsCategory.class, hql, false, 0, 0);
//        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                    MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }
}
