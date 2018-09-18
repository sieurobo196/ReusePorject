package vn.itt.msales.goods.controller;

import java.util.ArrayList;
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
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsCategory;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsaleResponseStatus;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.dao.MsalesJsonValidator;
import vn.itt.msales.entity.GoodsSeri;
import vn.itt.msales.entity.GoodsUnit;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.MCPDetails;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.Session;
import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.UserGoodsCategory;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.logex.MSalesException;

/**
 *
 * @author DuanND
 *
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.GOODS.NAME)
public class MsalesGoodsController extends CsbController {

    /**
     *
     * @param request is jsonString to get Goods's information from client
     * Request
     * @return Goods's information along with status, code.
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_GET_GOODS, method = RequestMethod.POST)
    public @ResponseBody
    String getGoods(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            Goods goods = new Goods();
            try {
                goods = (Goods) MsalesJsonUtils.getObjectFromJSON(jsonString, Goods.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (goods.getId() != null) {
                    Goods goods2 = dataService.getRowById(goods.getId(), Goods.class);
                    if (goods2 != null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, goods2));
                    } else {
                        hashErrors.put("Goods", MsalesValidator.MCP_GOODS_ID_NOT_EXIST + goods.getId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Create a Goods from jsonString
     *
     * @param is a jsonString Request to create a Goods
     * @return a jsonString contain status, code and contents
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_CREATE_GOODS, method = RequestMethod.POST)
    public @ResponseBody
    String createGoods(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            Goods goods = null;
            try {
                // parse jsonString to a status Object
                goods = MsalesJsonUtils.getObjectFromJSON(jsonString, Goods.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // status from json not null
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (goods.getParentId() != null) {
                    Goods user = dataService.getRowById(goods.getParentId(), Goods.class);
                    if (user == null) {
                        // is not exist in DB
                        hashErrors.put("ParentId", MsalesValidator.MCP_GOODS_ID_NOT_EXIST + goods.getParentId());
                    }
                }
                if (goods.getGoodsCategoryId() != null) {
                    GoodsCategory user = dataService.getRowById(
                            goods.getGoodsCategoryId(), GoodsCategory.class);
                    if (user == null) {
                        // is not exist in DB
                        hashErrors.put("GoodsCategory", MsalesValidator.MCP_GOODS_CATEGORY_NOT_EXIST + goods.getGoodsCategoryId());
                    }
                } else {
                    hashErrors.put("GoodsCategory", MsalesValidator.MCP_GOODS_CATEGORY_ID_NULL);
                }

                if (goods.getStatusId() != null) {
                    Status user = dataService.getRowById(goods.getStatusId(),
                            Status.class);
                    if (user == null) {
                        // is not exist in DB
                        hashErrors.put("Status", MsalesValidator.MCP_STATUS_NOT_EXIST + goods.getStatusId());
                    }
                } else {
                    hashErrors.put("Status", MsalesValidator.MCP_STATUS_NULL);
                }
                if (goods.getCreatedUser() != null) {
                    User userRole2 = dataService.getRowById(goods.getCreatedUser(), User.class);
                    if (userRole2 == null) {
                        hashErrors.put("Goods", MsalesValidator.MCP_USER_NOT_EXIST + goods.getCreatedUser());
                    }
                }
                if (hashErrors.size() > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                String str = goods.getName();
                if (str != null) {
                    if (str.trim().isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, MsalesValidator.DK_GOODS_NAME_NOT_EXIST));
                    }
                }

                String str1 = goods.getGoodsCode();
                if (str1 != null) {
                    if (str1.trim().isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, MsalesValidator.DK_GOODS_CODE_NOT_EXIST));
                    }
                }
                int ret = 0;
                try {
                    // insert new a user to database
                    ret = dataService.insertRow(goods);

                } catch (Exception e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (e.getCause().getCause() instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }
                }
                if (ret > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                } // status from DB null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }

                // status from DB not null
            } // status from json null
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
     * Delete Goods
     *
     * @param request is jsonString to delete Goods
     * @return
     */
    // @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_DELETE_GOODS, method = RequestMethod.POST)
    public @ResponseBody
    String deleteGoods(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        // jsonString not null
        if (jsonString != null) {
            Goods goods = null;
            try {
                // parse jsonString to a Property Object
                goods = MsalesJsonUtils.getObjectFromJSON(jsonString, Goods.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // property from json not null
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (goods.getId() != null) {
                    Goods goods2 = dataService.getRowById(goods.getId(), Goods.class);
                    if (goods2 == null) {
                        // is not exist in DB
                        hashErrors.put("Goods", MsalesValidator.MCP_GOODS_ID_NOT_EXIST + goods.getId());
                    }
                    if (goods.getDeletedUser() != null) {
                        User userRole2 = dataService.getRowById(goods.getDeletedUser(), User.class);
                        if (userRole2 == null) {
                            hashErrors.put("Goods", MsalesValidator.MCP_USER_NOT_EXIST + goods.getDeletedUser());
                        }
                    }
                    if (hashErrors.size() > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    int ret = 0;
                    try {
                        // insert new a user to database
                        ret = dataService.deleteSynch(goods);
                    } catch (Exception e) {
                        if (e instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(e);
                        }
                    }
                    // update delete success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                    } // update delete failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } // property from json null
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
     * Update Goods
     *
     * @param request is a jsonString contain database to update Goods
     * @return is a jsonString contain code, status
     */
    // @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_UPDATE_GOODS, method = RequestMethod.POST)
    public @ResponseBody
    String updateGoods(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            Goods goods = null;
            try {
                // parse jsonString to a Property Object
                goods = MsalesJsonUtils.getObjectFromJSON(jsonString, Goods.class);

            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // property from json not null
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (goods.getId() != null) {
                    Goods goods2 = dataService.getRowById(goods.getId(), Goods.class);
                    if (goods2 == null) {
                        // is not exist in DB
                        hashErrors.put("Goods", MsalesValidator.MCP_GOODS_ID_NOT_EXIST + goods.getId());
                    }
                    if (goods.getParentId() != null) {
                        Goods user = dataService.getRowById(goods.getParentId(), Goods.class);
                        if (user == null) {
                            // is not exist in DB
                            hashErrors.put("ParentId", MsalesValidator.MCP_GOODS_ID_NOT_EXIST + goods.getParentId());
                        }
                    }

                    if (goods.getGoodsCategoryId() != null) {
                        GoodsCategory user = dataService.getRowById(
                                goods.getGoodsCategoryId(), GoodsCategory.class);
                        if (user == null) {
                            // is not exist in DB
                            hashErrors.put("GoodsCategory", MsalesValidator.MCP_GOODS_CATEGORY_NOT_EXIST + goods.getGoodsCategoryId());
                        }
                    } else {
                        hashErrors.put("GoodsCategory", MsalesValidator.MCP_GOODS_CATEGORY_ID_NULL);
                    }

                    if (goods.getStatusId() != null) {
                        Status user = dataService.getRowById(goods.getStatusId(),
                                Status.class);
                        if (user == null) {
                            // is not exist in DB
                            hashErrors.put("Status", MsalesValidator.MCP_STATUS_NOT_EXIST + goods.getStatusId());
                        }
                    } else {
                        hashErrors.put("Status", MsalesValidator.MCP_STATUS_NULL);
                    }
                    if (goods.getUpdatedUser() != null) {
                        User userRole2 = dataService.getRowById(goods.getUpdatedUser(), User.class);
                        if (userRole2 == null) {
                            hashErrors.put("Goods", MsalesValidator.MCP_USER_NOT_EXIST + goods.getUpdatedUser());
                        }
                    }
                    String str = goods.getName();
                    if (str != null) {
                        if (str.trim().isEmpty()) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, MsalesValidator.DK_GOODS_NAME_NOT_EXIST));
                        }
                    }

                    String str1 = goods.getGoodsCode();
                    if (str1 != null) {
                        if (str1.trim().isEmpty()) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, MsalesValidator.DK_GOODS_CODE_NOT_EXIST));
                        }
                    }
                    if (hashErrors.size() > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));

                    }

                    int ret = 0;
                    try {
                        // insert new a user to database
                        ret = dataService.updateSync(goods);
                    } catch (Exception e) {
                        if (e instanceof ConstraintViolationException) {
                            return MsalesJsonUtils.jsonValidate(e);
                        }
                    }
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } // property from DB null or incorrect
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_UPDATE_FAIL));
                    }
                } // property from json null
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
                    .create(MsalesStatus.NULL));
        }
    }

    /**
     * Get a list all goods
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_GET_LIST_GOODS)
    public @ResponseBody
    String getListGoods(HttpServletRequest request) {
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

        MsalesResults<Goods> list = dataService.getListOption(Goods.class, parameterList, true);
        if (page.getPageNo() > 0 && page.getRecordsInPage() > 0) {
            if (list == null) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.PAGE_INVALID));
        }
    }

    /**
     *
     * @param request is jsonString to get Goods have goodCategyId common
     * @return a jsonString contain information about Goods
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_GET_GOODS_BY_GOODSCATEGORY, method = RequestMethod.POST)
    public @ResponseBody
    String getListGoodsByGoodsCategoryId(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // jsonString not null
        if (jsonString != null) {
            Goods goods = null;
            try {
                // parse jsonString to a Status Object
                goods = (Goods) MsalesJsonUtils.getObjectFromJSON(jsonString, Goods.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // status from json not null
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (goods.getGoodsCategoryId() != null) {
                    GoodsCategory goodsCategory = dataService.getRowById(
                            goods.getGoodsCategoryId(), GoodsCategory.class);
                    if (goodsCategory == null) {
                        hashErrors.put("GoodsCategory", MsalesValidator.MCP_GOODS_CATEGORY_NOT_EXIST + goods.getGoodsCategoryId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    ParameterList parameterList = new ParameterList("goodsCategorys.id", goods.getGoodsCategoryId(), page.getPageNo(), page.getRecordsInPage());
                    MsalesResults<Goods> list = dataService.getListOption(Goods.class, parameterList, true);

                    // list PropertyValue not null
                    if (list != null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
                    } // list Status null
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
                    }
                } // statusType from json with incorrect Id
                else {
                    hashErrors.put("GoodsCategory", MsalesValidator.MCP_GOODS_CATEGORY_ID_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
            } // statusType from json null
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
     * Search Goods
     *
     * @param request HttpservletRequest
     * @return Json String include List Goods
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_SEARCH_GOODS, method = RequestMethod.POST)
    public @ResponseBody
    String searchGoods(HttpServletRequest request) {
        //get List GoodsCategory from DB
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
                if (searchObject.get("goodsCategoryId") != null) {
                    try {
                        int goodsCategoryId = Integer.parseInt(searchObject.get("goodsCategoryId").toString());
                        parameterList.add("goodsCategorys.id", goodsCategoryId);
                    } catch (Exception ex) {
                        //error parse goodsCategoryId
                    }
                }
            }
            MsalesResults<Goods> list = dataService.getListOption(Goods.class, parameterList, true);

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get Combobox List Goods
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_GET_CB_LIST_GOODS, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListGoods(HttpServletRequest request) {
        // get List Goods from DB
        ParameterList parameterList = new ParameterList();
        MsalesResults<Goods> list = dataService.getListOption(Goods.class, parameterList, true);
        // list not null display list
        String[] strings = {"parents", "goodsCode", "statuss", "goodsCategorys", "isRecover",
            "price", "factor", "isFocus", "order"};
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                HttpStatus.OK, list), strings);
    }

    /**
     * get combobox List Goods By GoodsCategory
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_GET_CB_LIST_GOODS_BY_GOODS_CATEGORY_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListGoodsByGoodsCategoryId(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // jsonString not null
        if (jsonString != null) {
            Goods goods = null;
            try {
                // parse jsonString to a Goods Object
                goods = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Goods.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }// location from json not null
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // Location with correct parentId
                if (goods.getGoodsCategoryId() != null) {
                    // get List Location by parentId
                    ParameterList parameterList = new ParameterList("goodsCategorys.id", goods.getGoodsCategoryId(), page.getPageNo(), page.getRecordsInPage());
                    MsalesResults<Goods> lists = dataService.getListOption(Goods.class, parameterList, true);
                    String[] strings = {"parents", "goodsCode", "statuss", "goodsCategorys", "isRecover", "factor", "price", "isFocus", "order"};
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, lists), strings);
                } // location from json with incorrect parentId
                else {
                    hashErrors.put("Goods", MsalesValidator.MCP_GOODS_CATEGORY_ID_NULL);
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
     * create Goods Admin
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_CREATE_GOODS_ADMIN, method = RequestMethod.POST)
    public @ResponseBody
    String createGoodsAdmin(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            Goods goods;
            try {
                //parse jsonString to a mcp Object
                goods = MsalesJsonUtils.getObjectFromJSON(jsonString, Goods.class);
            } catch (Exception e) {
                if (e instanceof ConstraintViolationException) {
                    return MsalesJsonUtils.jsonValidate(e);
                }//else
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
            }
            LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
            if (goods.getCreatedUser() == null) {
                hashErrors.put("createdUser", MsalesValidator.NOT_NULL);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL, hashErrors));
            }

            if (goods != null) {
                List<GoodsUnit> goodsUnits = goods.getGoodsUnits();
                if (goodsUnits != null && !goodsUnits.isEmpty()) {
                    for (GoodsUnit goodsUnit : goodsUnits) {
                        if (goodsUnit != null) {
                            if (goodsUnit.getUnits().getId() != null) {
                                Unit unit = dataService.getRowById(goodsUnit.getUnits().getId(), Unit.class);
                                if (unit == null) {
                                    hashErrors.put("Unit", MsalesValidator.NOT_EXIST + goodsUnit.getUnitId());
                                }
                                goodsUnit.setCreatedUser(goods.getCreatedUser());
                            } else {
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL, "UnitId cần nhập"));
                            }
                        }
                    }
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, "GoodsUnits cần nhập"));
                }
                if (goods.getParentId() != null) {
                    Goods user = dataService.getRowById(goods.getParentId(), Goods.class);
                    if (user == null) {
                        // is not exist in DB
                        hashErrors.put("ParentId", MsalesValidator.MCP_GOODS_ID_NOT_EXIST + goods.getParentId());
                    }
                }
                if (goods.getGoodsCategoryId() != null) {
                    GoodsCategory user = dataService.getRowById(
                            goods.getGoodsCategoryId(), GoodsCategory.class);
                    if (user == null) {
                        // is not exist in DB
                        hashErrors.put("GoodsCategory", MsalesValidator.MCP_GOODS_CATEGORY_NOT_EXIST + goods.getGoodsCategoryId());
                    }
                } else {
                    hashErrors.put("GoodsCategory", MsalesValidator.MCP_GOODS_CATEGORY_ID_NULL);
                }

                if (goods.getStatusId() != null) {
                    Status user = dataService.getRowById(goods.getStatusId(),
                            Status.class);
                    if (user == null) {
                        // is not exist in DB
                        hashErrors.put("Status", MsalesValidator.MCP_STATUS_NOT_EXIST + goods.getStatusId());
                    }
                } else {
                    hashErrors.put("Status", MsalesValidator.MCP_STATUS_NULL);
                }
                if (goods.getCreatedUser() != null) {
                    User userRole2 = dataService.getRowById(goods.getCreatedUser(), User.class);
                    if (userRole2 == null) {
                        hashErrors.put("Goods", MsalesValidator.MCP_USER_NOT_EXIST + goods.getCreatedUser());
                    }
                }
                if (hashErrors.size() > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                int goodsId = 0;
                try {
                    goods.setDeletedUser(0);
                    goods.setCreatedAt(new Date());
                    goods.setUpdatedUser(0);
                    goodsId = dataService.insert(goods);
                } catch (MSalesException e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }
                }
                //set lai cac truognbat buoc cho Goodsunit
                if (goodsId > 0) {
                    for (GoodsUnit goodsUnit : goodsUnits) {
                        goodsUnit.setGoodsId(goods.getId());
                        goodsUnit.setCreatedAt(new Date());
                        goodsUnit.setCreatedUser(goods.getCreatedUser());
                        goodsUnit.setUpdatedUser(0);
                        goodsUnit.setDeletedUser(0);
                        goodsUnit.setPrice(goods.getPrice());
                        goodsUnit.setIsActive(1);
                    }

                    goods.setDeletedUser(0);
                    ArrayList insertList = new ArrayList();
                    insertList.addAll(goodsUnits);
                    insertList.add(goods);
                    try {
                        dataService.insertOrUpdateArray(insertList);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, goodsId));
                    } catch (Exception ex) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_INSERT_FAIL));
                    }

                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
            } //mcp from json null
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
     * update Goods Admin
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_UPDATE_GOODS_ADMIN, method = RequestMethod.POST)
    public @ResponseBody
    String updateGoodsAdmin(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            Goods goods;
            try {
                //parse jsonString to a mcp Object
                goods = MsalesJsonUtils.getObjectFromJSON(jsonString, Goods.class);
            } catch (Exception e) {
                if (e instanceof ConstraintViolationException) {
                    return MsalesJsonUtils.jsonValidate(e);
                }//else
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
            }
            LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
            if (goods.getUpdatedUser() == null) {
                hashErrors.put("updatedUser", MsalesValidator.NOT_NULL);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL, hashErrors));
            }

            if (goods != null) {
                if (goods.getId() != null) {
                    Goods goods1 = dataService.getRowById(goods.getId(), Goods.class);
                    if (goods1 == null) {
                        hashErrors.put("Goods", MsalesValidator.NOT_EXIST + goods1.getId());
                    }
                } else {
                    hashErrors.put("Id goods", MsalesValidator.NOT_NULL);
                }
                List<GoodsUnit> goodsUnits = goods.getGoodsUnits();
                if (goodsUnits != null && !goodsUnits.isEmpty()) {
                    for (GoodsUnit goodsUnit : goodsUnits) {
                        if (goodsUnit != null) {
                            if (goodsUnit.getId() != null) {
                                GoodsUnit goodsUnit1 = dataService.getRowById(goodsUnit.getId(), GoodsUnit.class);
                                if (goodsUnit1 == null) {
                                    hashErrors.put("GoodsUnit", MsalesValidator.NOT_EXIST + goodsUnit.getId());
                                }
                            } else {
                                hashErrors.put("Id GoodsUnit", MsalesValidator.NOT_NULL);
                            }
                            if (goodsUnit.getUnits().getId() != null) {
                                Unit unit = dataService.getRowById(goodsUnit.getUnits().getId(), Unit.class);
                                if (unit == null) {
                                    hashErrors.put("Unit", MsalesValidator.NOT_EXIST + goodsUnit.getUnitId());
                                }
                                goodsUnit.setCreatedUser(goods.getCreatedUser());
                            } else {
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL, "UnitId cần nhập"));
                            }
                        }
                    }
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, "GoodsUnits cần nhập"));
                }
                if (goods.getParentId() != null) {
                    Goods goods1 = dataService.getRowById(goods.getParentId(), Goods.class);
                    if (goods1 == null) {
                        // is not exist in DB
                        hashErrors.put("ParentId", MsalesValidator.MCP_GOODS_ID_NOT_EXIST + goods.getParentId());
                    }
                }
                if (goods.getGoodsCategoryId() != null) {
                    GoodsCategory goodsCategory = dataService.getRowById(
                            goods.getGoodsCategoryId(), GoodsCategory.class);
                    if (goodsCategory == null) {
                        // is not exist in DB
                        hashErrors.put("GoodsCategory", MsalesValidator.MCP_GOODS_CATEGORY_NOT_EXIST + goods.getGoodsCategoryId());
                    }
                } else {
                    hashErrors.put("GoodsCategory", MsalesValidator.MCP_GOODS_CATEGORY_ID_NULL);
                }

                if (goods.getStatusId() != null) {
                    Status status = dataService.getRowById(goods.getStatusId(),
                            Status.class);
                    if (status == null) {
                        // is not exist in DB
                        hashErrors.put("Status", MsalesValidator.MCP_STATUS_NOT_EXIST + goods.getStatusId());
                    }
                } else {
                    hashErrors.put("Status", MsalesValidator.MCP_STATUS_NULL);
                }
                if (goods.getUpdatedUser() != null) {
                    User user = dataService.getRowById(goods.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("Goods", MsalesValidator.MCP_USER_NOT_EXIST + goods.getUpdatedUser());
                    }
                }
                if (hashErrors.size() > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                int goodsId = 0;
                try {
                    goods.setDeletedUser(0);
                    goods.setUpdatedAt(new Date());
                    
                    goodsId = dataService.updateSync(goods);
                } catch (MSalesException e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }
                }
                //set lai cac truognbat buoc cho Goodsunit
                if (goodsId > 0) {
                    for (GoodsUnit goodsUnit : goodsUnits) {
                        goodsUnit.getId();
                        goodsUnit.setGoodsId(goods.getId());
                        goodsUnit.setCreatedUser(goods.getCreatedUser());
                        goodsUnit.setUpdatedUser(goods.getUpdatedUser());
                        goodsUnit.getUnitId();
                        goodsUnit.setDeletedUser(0);
                        goodsUnit.setPrice(goods.getPrice());
                        goodsUnit.setIsActive(1);
                    }

                    goods.setDeletedUser(0);
                    ArrayList insertList = new ArrayList();
                    insertList.addAll(goodsUnits);
                    insertList.add(goods);
                    try {
                        dataService.insertOrUpdateArray(insertList);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, goodsId));
                    } catch (Exception ex) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_INSERT_FAIL));
                    }

                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.SQL_INSERT_FAIL));
                }
            } //mcp from json null
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

    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_GET_GOODS_ADMIN, method = RequestMethod.POST)
    public @ResponseBody
    String getGoodsAdmin(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            Goods goods;
            try {
                //parse jsonString to a mcp Object
                goods = (Goods) MsalesJsonUtils.getObjectFromJSON(jsonString, Goods.class);
            } //jsonString syntax incorrect //jsonString syntax incorrect //jsonString syntax incorrect //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }            //mcp from json not null
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (goods.getId() != null) {
                    //get mcp from DB
                    goods = dataService.getRowById(goods.getId(), Goods.class);
                    //mcp not null
                    if (goods != null) {
                        ParameterList parameterList = new ParameterList("goodss.id", goods.getId(), 0, 0);
                        List<GoodsUnit> list = dataService.getListOption(GoodsUnit.class, parameterList);
                        goods.setGoodsUnits(list);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, goods));

                    } else {
                        //return khong ton tai
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                    }
                } else {
                    hashErrors.put("Goods", "Id không được " + goods.getId());
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    //Lấy danh sách hàng hóa salesMan được bán
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_GET_GOODS_SALES_MAN, method = RequestMethod.POST)
    public @ResponseBody
    String getGoodsSalesmanApp(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // String json = request.getHeader("Authorization");
        //jsonString not null
        if (jsonString != null) {

            UserGoodsCategory user = new UserGoodsCategory();
            try {
                user = (UserGoodsCategory) MsalesJsonUtils.getObjectFromJSON(jsonString, UserGoodsCategory.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //UserGoodsCategory from json not null
            //get List UserGoodsCategory by userId
            //
            String hql = "Select U from UserGoodsCategory as U where deletedUser = 0 and users.id = " + user.getUserId() ;
            		hql+= " and goodsCategorys.companys.id = " + user.getGoodsCategoryId() + " order by goodsCategorys.id desc";
//            ParameterList parameterList = new ParameterList("users.id", user.getUserId());
//            
//            parameterList.setOrder("goodsCategorys.id", "DESC");
//            List<UserGoodsCategory> list = dataService.getListOption(UserGoodsCategory.class, parameterList);
			List<UserGoodsCategory> list = new ArrayList<UserGoodsCategory>();
			try {
				list = dataService.executeSelectHQL(UserGoodsCategory.class,
						hql, false, 0, 0);
			} catch (Exception ex) {

			}

            List<Goods> goodsList = new ArrayList<Goods>();
            if (list.size() <= 0) {
            	String hqlGoods = "Select G from Goods as G where deletedUser = 0 and statuss.id = 15 and  goodsCategorys.companys.id = " + user.getGoodsCategoryId() + " order by name asc";
               try{
            	   goodsList = dataService.executeSelectHQL(Goods.class, hqlGoods, false, 0, 0);
               }catch(Exception ex){
            	   
               }
            }

            for (UserGoodsCategory uGoodsCategory : list) {
                ParameterList parameterList2 = new ParameterList("goodsCategorys.id", uGoodsCategory.getGoodsCategorys().getId());
                parameterList2.add("statuss.id", 15);
                parameterList2.setOrder("name");
                List<Goods> goods = dataService.getListOption(Goods.class, parameterList2);

                if (goods.size() > 0) {
                    goodsList.addAll(goods);
                }
                // uGoodsCategory.setGoodsCategory(uGoodsCategory.getGoodsCategorys());
            }
            MsalesResults<Goods> goods = new MsalesResults<Goods>();
            goods.setCount(Long.parseLong(goodsList.size() + ""));
            goods.setContentList(goodsList);
            String[] strings = {"goodsCategorys", "users", "statuss", "parents", "goodsCode", "statuss",
                "isRecover", "factor", "isFocus", "order", "goodss", "isActive", "note", "price"};

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, goods), strings);
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }
    
    /**
     * DuanND
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS.ACTION_GET_GOODS_BY_GOODSCATEGORY_OF_PROMOTION, method = RequestMethod.POST)
    public @ResponseBody
    String getListGoodsByGoodsCategoryIdOfPromotion(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        
        // jsonString not null
        if (jsonString != null) {
            Goods goods = null;
            try {
                // parse jsonString to a Status Object
                goods = (Goods) MsalesJsonUtils.getObjectFromJSON(jsonString, Goods.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // status from json not null
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (goods.getGoodsCategoryId() != null) {
                	List<Goods> goodsList = new ArrayList<>();
                    String hql = "SELECT PGR.goodss as goods FROM PromotionGoodsRef as PGR"
                    		+ " WHERE PGR.deletedUser = 0"
                    		+ "	AND PGR.goodss.deletedUser = 0"
                    		+ " AND PGR.goodss.statuss.id = 15"
                    		+ "	AND PGR.goodss.goodsCategorys.id = PGR.promotions.goodsCategorys.id"
                    		+ " AND PGR.promotions.deletedUser = 0"
                    		+ " AND PGR.promotions.goodsCategorys.id = "+ goods.getGoodsCategoryId();
                    List<HashMap> listGoods = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
                    if(!listGoods.isEmpty()){
                    	
                    	for(HashMap go : listGoods){
                    		goodsList.add((Goods) go.get("goods"));
                    		
                    	}
                    }else{
                    	ParameterList parameterList = new ParameterList("goodsCategorys.id", goods.getGoodsCategoryId());
                    	List<Goods> lists = dataService.getListOption(Goods.class, parameterList);
                    	if(!lists.isEmpty()){
                    		goodsList.addAll(lists);
                    	}
                    }
                    
                    MsalesResults<Goods> list = new MsalesResults<>();
                    list.setContentList(goodsList);
                    list.setCount((Long)(long) goodsList.size());

                    // list PropertyValue not null
                   
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
                   
                   
                } // statusType from json with incorrect Id
                else {
                    hashErrors.put("GoodsCategory", MsalesValidator.MCP_GOODS_CATEGORY_ID_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
            } // statusType from json null
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
