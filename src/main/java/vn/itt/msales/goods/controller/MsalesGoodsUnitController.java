package vn.itt.msales.goods.controller;

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
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsUnit;
import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsaleResponseStatus;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.csb.controller.CsbController;

/**
 *
 * @author DuanND 
 *
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.GOODS_UNIT.NAME)
public class MsalesGoodsUnitController extends CsbController {

    /**
     * Create a GoodsUnit from jsonString
     *
     * @param is a jsonString Request to create a GoodsUnit
     * @return a jsonString contain status, code and contents
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_UNIT.ACTION_CREATE_GOODS_UNIT, method = RequestMethod.POST)
    public @ResponseBody String createGoodsUnit(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            GoodsUnit goods = null;
            try {
                goods = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        GoodsUnit.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // goodsUnit from json not null
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                Goods goodsCategory = null;
                Unit unit = new Unit();
                if (goods.getGoodsId() != null) {
                    // get StatusType with id from status
                    goodsCategory = dataService.getRowById(goods.getGoodsId(),
                            Goods.class);
                    if (goodsCategory == null) {
                        // return message warning StatusType is not exist in DB
                        hashErrors.put("Goods",MsalesValidator.MCP_GOODS_ID_NOT_EXIST + goods.getGoodsId());
                    }

                }else{
                	hashErrors.put("Goods", MsalesValidator.MCP_GOODS_ID_NULL);
                }
                if (goods.getUnitId() != null) {
                    // get StatusType with id from status
                    unit = dataService.getRowById(goods.getUnitId(), Unit.class);
                    if (unit == null) {
                        hashErrors.put("Unit", MsalesValidator.MCP_UNIT_ID_NOT_EXIST + goods.getUnitId());
                    }

                }else{
                	hashErrors.put("Unit", MsalesValidator.MCP_UNIT_ID_NULL);
                }
                if(goods.getCreatedUser() != null){
            		User userRole2 = dataService.getRowById(goods.getCreatedUser(), User.class);
            		if(userRole2 == null){
            			hashErrors.put("GoodsUnit", MsalesValidator.MCP_USER_NOT_EXIST + goods.getCreatedUser());
            		}
            	}
                if (hashErrors.size() > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
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
     *
     * @param request is jsonString to get GoodsUnit's information from client
     * Request
     * @return GoodsUnit's information along with status, code.
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_UNIT.ACTION_GET_GOODS_UNIT, method = RequestMethod.POST)
    public @ResponseBody String getGoodsUnit(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        if (jsonString != null) {
            GoodsUnit goods = new GoodsUnit();
            try {
                goods = (GoodsUnit) MsalesJsonUtils.getObjectFromJSON(
                        jsonString, GoodsUnit.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (goods.getId() != null) {
                    GoodsUnit goods2 = dataService.getRowById(goods.getId(),
                            GoodsUnit.class);
                    if (goods2 != null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, goods2));
                    } else {
                        hashErrors.put("GoodsUnit",MsalesValidator.MCP_UNIT_ID_NOT_EXIST + goods.getId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    hashErrors.put("GoodsUnit", MsalesValidator.MCP_UNIT_ID_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Delete GoodsUnit
     *
     * @param request is jsonString to delete GoodsUnit
     * @return
     */
    // @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_UNIT.ACTION_DELETE_GOODS_UNIT, method = RequestMethod.POST)
    public @ResponseBody
    String deleteGoodsUnit(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        // jsonString not null
        if (jsonString != null) {
            GoodsUnit goods = null;
            try {
                // parse jsonString to a Property Object
                goods = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        GoodsUnit.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // property from json not null
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (goods.getId() != null) {
                    GoodsUnit goodsUnit = dataService.getRowById(goods.getId(),
                            GoodsUnit.class);
                    if (goodsUnit == null) {
                        hashErrors.put("GoodsUnit", MsalesValidator.MCP_UNIT_ID_NOT_EXIST + goods.getId());
                    }
                    if(goods.getDeletedUser() != null){
	            		User userRole2 = dataService.getRowById(goods.getDeletedUser(), User.class);
	            		if(userRole2 == null){
	            			hashErrors.put("GoodsUnit",MsalesValidator.MCP_USER_NOT_EXIST + goods.getDeletedUser());
	            		}
	            	}
                    if (hashErrors.size() > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE,
                                        hashErrors));
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
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } // update delete failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } // property from json null
                else {
                    hashErrors.put("GoodsUnit", MsalesValidator.MCP_UNIT_ID_NULL);
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
     * Update GoodsUnit
     *
     * @param request is a jsonString contain database to update GoodsUnit
     * @return is a jsonString contain code, status
     */
    // @SuppressWarnings("unchecked")
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_UNIT.ACTION_UPDATE_GOODS_UNIT, method = RequestMethod.POST)
    public @ResponseBody String updateGoodsUnit(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        // jsonString not null
        if (jsonString != null) {
            GoodsUnit goods = null;
            try {
                // parse jsonString to a Property Object
                goods = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        GoodsUnit.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // property from json not null
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (goods.getId() != null) {
                    GoodsUnit goodsUnit = dataService.getRowById(goods.getId(),
                            GoodsUnit.class);
                    if (goodsUnit == null) {
                        hashErrors.put("GoodsUnit",MsalesValidator.MCP_UNIT_ID_NOT_EXIST + goods.getId());
                    }

                    Goods goodsCategory = null;
                    Unit unit = new Unit();
                    if (goods.getGoodsId() != null) {
                        // get StatusType with id from status
                        goodsCategory = dataService.getRowById(goods.getGoodsId(),
                                Goods.class);
                        if (goodsCategory == null) {
                            // return message warning StatusType is not exist in DB
                            hashErrors.put("Goods",MsalesValidator.MCP_GOODS_ID_NOT_EXIST + goods.getGoodsId());
                        }

                    }else{
                    	hashErrors.put("Goods",MsalesValidator.MCP_GOODS_ID_NULL);
                    }
                    if (goods.getUnitId() != null) {
                        // get StatusType with id from status
                        unit = dataService.getRowById(goods.getUnitId(), Unit.class);
                        if (unit == null) {
                            hashErrors.put("Unit",MsalesValidator.MCP_UNIT_ID_NOT_EXIST + goods.getUnitId());
                        }

                    }else{
                    	hashErrors.put("Unit", MsalesValidator.MCP_UNIT_ID_NULL);
                    }
                    if(goods.getUpdatedUser() != null){
	            		User userRole2 = dataService.getRowById(goods.getUpdatedUser(), User.class);
	            		if(userRole2 == null){
	            			hashErrors.put("GoodsUnit",MsalesValidator.MCP_USER_NOT_EXIST + goods.getUpdatedUser());
	            		}
	            	}
                    if (hashErrors.size() > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE,hashErrors));
                    }

                    int ret = 0;
                    try {
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
                    hashErrors.put("GoodsUnit", MsalesValidator.MCP_UNIT_ID_NULL);
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
     * Get a list all goodsUnit
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_UNIT.ACTION_GET_LIST_GOODS_UNIT)
    public @ResponseBody String getListGoodsUnit(HttpServletRequest request) {
        // @SuppressWarnings("unchecked")
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(),
                page.getRecordsInPage());
        MsalesResults<GoodsUnit> goods = dataService.getListOption(GoodsUnit.class, parameterList, true);
        return MsalesJsonUtils.getJSONFromOject(goods, new MsaleResponseStatus(HttpStatus.OK));
    }

    /**
     * getListGoodsUnit from GoodsId
     * @param request is jsonString to get GoodsUnit have goodsId common
     * @return a jsonString contain information about GoodsUnit
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_UNIT.ACTION_GET_GOODSUNIT_BY_GOODS, method = RequestMethod.POST)
    public @ResponseBody String getGoodsUnitByGoods(HttpServletRequest request) {
        // get jsonString from CSB
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // jsonString not null
        if (jsonString != null) {
            GoodsUnit status = null;
            try {
                // parse jsonString to a GoodsUnit Object
                status = (GoodsUnit) MsalesJsonUtils.getObjectFromJSON(
                        jsonString, GoodsUnit.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // GoodsUnit from json not null
            if (status != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                // GoodsUnit from goods with correct Id
                if (status.getGoodsId() != null) {
                    Goods goodsCategory = dataService.getRowById(
                            status.getGoodsId(), Goods.class);
                    if (goodsCategory == null) {
                        hashErrors.put("Goods", MsalesValidator.MCP_GOODS_ID_NOT_EXIST + status.getGoodsId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    ParameterList parameterList = new ParameterList("goodss.id", status.getGoodsId(), page.getPageNo(), page.getRecordsInPage());
                    MsalesResults<GoodsUnit> list = dataService.getListOption( GoodsUnit.class, parameterList, true);
                    
                    if (list != null) {
                    	for(GoodsUnit goodsUnit : list.getContentList()){
                        	goodsUnit.setName(goodsUnit.getUnits().getName());
                        }
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, list));
                    } 
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NULL));
                    }
                }
                else {
                    hashErrors.put("Goods",MsalesValidator.MCP_GOODS_ID_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
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
     *
     * @param request is jsonString to get GoodsUnit have unitId common
     * @return a jsonString contain information about GoodsUnit
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_UNIT.ACTION_GET_GOODSUNIT_BY_UNIT, method = RequestMethod.POST)
    public @ResponseBody String getGoodsUnitByUnit(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        // jsonString not null
        if (jsonString != null) {
            GoodsUnit status = null;
            try {
                status = (GoodsUnit) MsalesJsonUtils.getObjectFromJSON(jsonString, GoodsUnit.class);
            } // jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            // status from json not null
            if (status != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (status.getUnitId() != null) {
                    Unit goodsCategory = dataService.getRowById(status.getUnitId(), Unit.class);
                    if (goodsCategory == null) {
                        hashErrors.put("Unit",MsalesValidator.MCP_UNIT_ID_NOT_EXIST + status.getUnitId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    ParameterList parameterList = new ParameterList("units.id", status.getUnitId(), page.getPageNo(), page.getRecordsInPage());
                    MsalesResults<GoodsUnit> list = dataService.getListOption(GoodsUnit.class, parameterList, true);
                    if (list != null) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, list));
                    } 
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NULL));
                    }
                }
                else {
                    hashErrors.put("Unit", MsalesValidator.MCP_UNIT_ID_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
            } 
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
    
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_UNIT.ACTION_GET_GOODS_UNIT_FOR_ORDER, method = RequestMethod.POST)
    public @ResponseBody String getGoodsUnitForOrder(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        if (jsonString != null) {
            GoodsUnit goods = new GoodsUnit();
            try {
                goods = (GoodsUnit) MsalesJsonUtils.getObjectFromJSON(
                        jsonString, GoodsUnit.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (goods != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                if (goods.getId() != null) {
                    GoodsUnit goodsUnit = dataService.getRowById(goods.getId(), GoodsUnit.class);
                    if (goodsUnit != null) {
                    	int price = goodsUnit.getGoodss().getPrice();
                    	 int quantity = 0;
                         quantity += goodsUnit.getQuantity();
                         /*for(int i = 0; i < 10; i++){
                         	boolean bool = false;
                         	if(goodsUnit != null && goodsUnit.getChildUnitIds() != null && goodsUnit.getChildUnitIds().getId() == goodsUnit.getUnits().getId()){
                         		//quantity += goodsUnit.getQuantity();
                         		//goods.setGoodsUnitId(goodsUnit.getId());
                         		break;
                         	}else if(goodsUnit != null && goodsUnit.getChildUnitIds() != null){
                         		ParameterList pList = new ParameterList(1,1);
                             	pList.add("goodss.id", goodsUnit.getGoodss().getId());
                             	pList.add("units.id", goodsUnit.getChildUnitIds().getId());
                             	List<GoodsUnit> listGoodsUnit = dataService.getListOption(GoodsUnit.class, pList);
                             	if(!listGoodsUnit.isEmpty()){
                             		goodsUnit = listGoodsUnit.get(0);
                             		quantity = quantity*goodsUnit.getQuantity();
                             		
                             	}else{
                             		bool = true;
                             	}
                         	}else{
                         		bool = true;
                         	}
                         	if(bool){
                         		break;
                         	}
                         }*/
                        goodsUnit.setPrice(price*quantity);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, goodsUnit));
                    } else {
                        hashErrors.put("GoodsUnit",MsalesValidator.MCP_UNIT_ID_NOT_EXIST + goods.getId());
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    hashErrors.put("GoodsUnit", MsalesValidator.MCP_UNIT_ID_NULL);
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

}
