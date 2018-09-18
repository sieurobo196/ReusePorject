package vn.itt.msales.sales.controller;

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
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsUnit;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.SalesStockGoods;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;

/**
 * 
 * @author  DuanND
 * 
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCKGOODS.NAME)
public class MsalesSalesStockGoodsController extends CsbController {

	/**
	 * Get Sales_Stock_Goods By Id
	 * 
	 * @param request
	 *            is jsonString have id to get info of Sale_Stock_Goods
	 * @return a jsonString include informations
	 */
	@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCKGOODS.ACTION_GET_SALES_STOCK_GOODS, method = RequestMethod.POST)
	public @ResponseBody String getSalesStockGoods(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
		// jsonString not null
		if (jsonString != null) {
			SalesStockGoods salesStockGoods = null;
			try {
				// parse jsonString to a SalesStockGoods Object
				salesStockGoods = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesStockGoods.class);
			} // jsonString syntax incorrect
			catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
			// salesStockGoods from json not null
			if (salesStockGoods != null) {
				LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
				// salesStockGoods from json with correct Id
				if (salesStockGoods.getId() != null) {
					// get salesStockGoods from DB
					SalesStockGoods salesStockGoods2 = dataService.getRowById(
							salesStockGoods.getId(), SalesStockGoods.class);
					// salesStockGoods not null
					if (salesStockGoods2 != null) {

						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(HttpStatus.OK, salesStockGoods2));
					} // salesStockGoods null
					else {
						hashErrors.put("SalesStockGoods",MsalesValidator.DK_SALES_STOCK_GOODS_ID_NOT_EXIST + salesStockGoods.getId());
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
					}

				} // location from json with incorrect Id
				else {
					hashErrors.put("SalesStockGoods", MsalesValidator.DK_SALES_STOCK_GOODS_ID_NULL);
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
				}

			} // salesStockGoods from json null
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
	 * Create a Sales_Stock_Goods
	 * @param request
	 *            is a jsonString contains data to create a Sales_Stock_Goods
	 * @return a jsonString have status, code and contents
	 */
	@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCKGOODS.ACTION_CREATE_SALES_STOCK_GOODS, method = RequestMethod.POST)
	public @ResponseBody String createSalesStockGoods(HttpServletRequest request) {
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
		// jsonString not null
		if (jsonString != null) {
			SalesStockGoods salesStockGoods = null;
			try {
				// parse jsonString to a salesStockGoods Object
				salesStockGoods = MsalesJsonUtils.getObjectFromJSON(jsonString,SalesStockGoods.class);
			} // jsonString syntax incorrect
			catch (Exception e) {
                if (e instanceof ConstraintViolationException) {
                    return MsalesJsonUtils.jsonValidate(e);
                }//else
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
            }
			// salesStockGoods from json not null
			if (salesStockGoods != null) {
				LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
				Goods goods = null;
				SalesStock salesStock = null;
				Status status = null;

				if (salesStockGoods.getGoodsUnitId() != null) {
					GoodsUnit user = dataService.getRowById(
							salesStockGoods.getGoodsUnitId(), GoodsUnit.class);
					if (user == null) {
						// return message warning goods or salesStock or status
						// is not exist in DB
						hashErrors.put("GoodsUnit",MsalesValidator.MCP_UNIT_ID_NOT_EXIST + salesStockGoods.getGoodsUnitId());
					}
				}
				// goods or salesStock or status is not exist
				if (salesStockGoods.getStockId() != null) {
					salesStock = dataService.getRowById(
							salesStockGoods.getStockId(), SalesStock.class);
					if (salesStock == null) {
						hashErrors.put("Stock", MsalesValidator.DK_SALES_STOCK_ID_NOT_EXIST + salesStockGoods.getStockId());
					}
				}

				if (salesStockGoods.getGoodsStatusId() != null) {
					status = dataService.getRowById(salesStockGoods.getGoodsStatusId(), Status.class);

					if (status == null) {
						hashErrors.put("GoodsStatus",MsalesValidator.MCP_STATUS_NOT_EXIST + salesStockGoods.getGoodsStatusId());
					}

				}
				if (salesStockGoods.getGoodsId() != null) {
					goods = dataService.getRowById(salesStockGoods.getGoodsId(), Goods.class);
					if (goods == null) {
						hashErrors.put("Goods",MsalesValidator.MCP_GOODS_ID_NOT_EXIST + salesStockGoods.getGoodsId());
					}
				}
				if(salesStockGoods.getCreatedUser() != null){
            		User userRole2 = dataService.getRowById(salesStockGoods.getCreatedUser(), User.class);
            		if(userRole2 == null){
            			hashErrors.put("SalesStockGoods", MsalesValidator.MCP_USER_NOT_EXIST + salesStockGoods.getCreatedUser());
            		}
            	}
				if (hashErrors.size() > 0) {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
				}

				int ret = 0;
				try {
					// save salesStockGoods to DB
					ret = dataService.insertRow(salesStockGoods);
				} catch (Exception e) {
					Exception ex = (Exception) e.getCause().getCause();
					if (e.getCause().getCause() instanceof ConstraintViolationException) {
						return MsalesJsonUtils.jsonValidate(ex);
					}
				}
				// salesStockGoods from DB not null
				if (ret > 0) {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(HttpStatus.OK, null));
				} // salesStockGoods from DB null
				else {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.SQL_INSERT_FAIL));
				}
			} // salesStockGoods from json null
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
	 * Update Sales_Stock_Goods
	 * 
	 * @param request
	 *            is jsonString contain data to update Sales_Stock_Goods
	 * @return a jsonString have status, code and contents
	 */
	// @SuppressWarnings("unchecked")
	@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCKGOODS.ACTION_UPDATE_SALES_STOCK_GOODS, method = RequestMethod.POST)
	public @ResponseBody String updateSalesStockGoods(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
		// jsonString not null
		if (jsonString != null) {
			SalesStockGoods salesStockGoods;
			try {
				// parse jsonString to a salesStockGoods Object
				salesStockGoods = MsalesJsonUtils.getObjectFromJSON(jsonString,
						SalesStockGoods.class);
			} // jsonString syntax incorrect
			catch (Exception e) {
                if (e instanceof ConstraintViolationException) {
                    return MsalesJsonUtils.jsonValidate(e);
                }//else
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
            }
			// salesStockGoods from json not null
			if (salesStockGoods != null) {
				LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
				// SalesStockGoods salesStockGoods2 = null;
				if (salesStockGoods.getId() != null) {
					SalesStockGoods salesStockGoods2 = dataService.getRowById(salesStockGoods.getId(), SalesStockGoods.class);
					if (salesStockGoods2 == null) {
						hashErrors.put("SalesStockGoods",MsalesValidator.DK_SALES_STOCK_GOODS_ID_NOT_EXIST + salesStockGoods.getId());
					}

					Goods goods = null;
					SalesStock salesStock = null;
					Status status = null;

					if (salesStockGoods.getGoodsUnitId() != null) {
						GoodsUnit user = dataService.getRowById(salesStockGoods.getGoodsUnitId(), GoodsUnit.class);
						if (user == null) {
							hashErrors.put("GoodsUnit", MsalesValidator.MCP_UNIT_ID_NOT_EXIST + salesStockGoods.getGoodsUnitId() );
						}
					}
					// goods or salesStock or status is not exist
					if (salesStockGoods.getStockId() != null) {
						salesStock = dataService.getRowById(
								salesStockGoods.getStockId(), SalesStock.class);
						if (salesStock == null) {
							hashErrors.put("Stock",MsalesValidator.DK_SALES_STOCK_GOODS_ID_NOT_EXIST + salesStockGoods.getStockId());
						}
					}

					if (salesStockGoods.getGoodsStatusId() != null) {
						status = dataService.getRowById(salesStockGoods.getGoodsStatusId(), Status.class);
						if (status == null) {
							hashErrors.put("GoodsStatus",MsalesValidator.MCP_STATUS_NOT_EXIST + salesStockGoods.getGoodsStatusId());
						}

					}
					if (salesStockGoods.getGoodsId() != null) {
						goods = dataService.getRowById(salesStockGoods.getGoodsId(), Goods.class);
						if (goods == null) {
							hashErrors.put("Goods",MsalesValidator.MCP_GOODS_ID_NOT_EXIST + salesStockGoods.getGoodsId());
						}
					}

					if(salesStockGoods.getUpdatedUser() != null){
	            		User userRole2 = dataService.getRowById(salesStockGoods.getUpdatedUser(), User.class);
	            		if(userRole2 == null){
	            			hashErrors.put("SalesStockGoods",MsalesValidator.MCP_USER_NOT_EXIST + salesStockGoods.getUpdatedUser());
	            		}
	            	}

					if (hashErrors.size() > 0) {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
					}

					int ret = 0;
					try {
						// update salesStockGoods to DB
						ret = dataService.updateSync(salesStockGoods);

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

				} // salesStockGoods from json null
				else {
					hashErrors.put("SalesStockGoods", MsalesValidator.DK_SALES_STOCK_GOODS_ID_NULL);
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
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
	 * Delete Sales_Stock_Goods
	 * @param request
	 *            is a jsonString have id and deletedUser to delete
	 *            Sales_Stock_Goods
	 * @return a jsonString to have status, code and contents
	 */
	// @SuppressWarnings("unchecked")
	@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCKGOODS.ACTION_DELETE_SALES_STOCK_GOODS, method = RequestMethod.POST)
	public @ResponseBody String deleteSalesStockGoods(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
		// jsonString not null
		if (jsonString != null) {
			SalesStockGoods salesStockGoods;
			try {
				// parse jsonString to a salesStockGoods Object
				salesStockGoods = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesStockGoods.class);
			} // jsonString syntax incorrect
			catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
			// salesStockGoods from json not null
			if (salesStockGoods != null) {
				LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
				// SalesStockGoods salesStockGoods2 = null;
				if (salesStockGoods.getId() != null) {
					SalesStockGoods salesStockGoods2 = dataService.getRowById(salesStockGoods.getId(), SalesStockGoods.class);
					if (salesStockGoods2 == null) {
						hashErrors.put("SalesStockGoods",MsalesValidator.DK_SALES_STOCK_GOODS_ID_NOT_EXIST + salesStockGoods.getId());
					}
					if(salesStockGoods.getDeletedUser() != null){
	            		User userRole2 = dataService.getRowById(salesStockGoods.getDeletedUser(), User.class);
	            		if(userRole2 == null){
	            			hashErrors.put("SalesStockGoods",MsalesValidator.MCP_USER_NOT_EXIST + salesStockGoods.getDeletedUser());
	            		}
	            	}

					if (hashErrors.size() > 0) {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
					}

					int ret = 0;
					// update salesStock to DB
					try {
						ret = dataService.deleteSynch(salesStockGoods);
					} catch (Exception e) {
						if (e instanceof ConstraintViolationException) {
							return MsalesJsonUtils.jsonValidate(e);
						}
					}

					// int ret = dataService.deleteSynch(salesStock);
					// update delete salesStock success
					if (ret > 0) {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(HttpStatus.OK, null));
					} // update delete salesStock failed
					else {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(MsalesStatus.SQL_DELETE_FAIL));
					}
				} else {
					hashErrors.put("SalesStockGoods",MsalesValidator.DK_SALES_STOCK_GOODS_ID_NULL);
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
				}
			} // salesStockGoods from json null
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
	 * Get List Sales_Stock_Goods By StockId
	 * 
	 * @param request is jsonString have stockId
	 * @return a jsonString include all Sales_Stock_Goods have general stockId
	 */
	@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCKGOODS.ACTION_GET_LIST_SALES_STOCK_GOODS_BY_STOCKID, method = RequestMethod.POST)
	public @ResponseBody String getListSalesStockGoodsByStockId(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
		MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
		// jsonString not null
		if (jsonString != null) {
			SalesStockGoods salesStockGoods;
			try {
				// parse jsonString to a salesStockGoods Object
				salesStockGoods = MsalesJsonUtils.getObjectFromJSON(jsonString,SalesStockGoods.class);
			} // jsonString syntax incorrect
			catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
			// salesStockGoods from json not null
			if (salesStockGoods != null) {
				LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
				// SalesStock from salesStockGoods with correct stockId
				if (salesStockGoods.getStockId() != null) {
					SalesStock salesStock = dataService.getRowById(
							salesStockGoods.getStockId(), SalesStock.class);
					if (salesStock == null) {
						hashErrors.put("Stock",MsalesValidator.DK_SALES_STOCK_ID_NOT_EXIST + salesStockGoods.getStockId());
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
					}
					// get List salesStockGoods by stockId
					ParameterList parameterList = new ParameterList("stocks.id", salesStockGoods.getStockId(), page.getPageNo(), page.getRecordsInPage());
					MsalesResults<SalesStockGoods> lists = dataService.getListOption(SalesStockGoods.class, parameterList, true);
					// list salesStockGoods not null
					if (lists != null) {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(HttpStatus.OK, lists));
					} // list salesStockGoods null
					else {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(MsalesStatus.NULL));
					}
				} // SalesStock from json with incorrect stockId
				else {
					hashErrors.put("Stock", MsalesValidator.DK_SALES_STOCK_ID_NULL);
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
				}
			} // stock from json null
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
	 * Get List Sales_Stock_Goods By GoodsId
	 * @param request
	 *            is jsonString have goodsId
	 * @return a jsonString contains all Sales_Stock_Goods have general goodsId
	 */
	@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCKGOODS.ACTION_GET_LIST_SALES_STOCK_GOODS_BY_GOODSID, method = RequestMethod.POST)
	public @ResponseBody String getListSalesStockGoodsByGoodsId(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
		MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
		// jsonString not null
		if (jsonString != null) {
			SalesStockGoods salesStockGoods;
			try {
				// parse jsonString to a salesStockGoods Object
				salesStockGoods = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesStockGoods.class);
			} // jsonString syntax incorrect
			catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
			// salesStockGoods from json not null
			if (salesStockGoods != null) {
				LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
				if (salesStockGoods.getGoodsId() != null) {
					Goods goods = dataService.getRowById(salesStockGoods.getGoodsId(), Goods.class);
					if (goods == null) {
						hashErrors.put("Goods", MsalesValidator.MCP_GOODS_ID_NOT_EXIST + salesStockGoods.getGoodsId());
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
					}
					// get List salesStockGoods by goodsId
					ParameterList parameterList = new ParameterList("goodss.id", salesStockGoods.getGoodsId(), page.getPageNo(), page.getRecordsInPage());
					MsalesResults<SalesStockGoods> lists = dataService.getListOption(SalesStockGoods.class, parameterList, true);
					// list salesStockGoods not null
					if (lists != null) {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(HttpStatus.OK, lists));
					} // list salesStockGoods null
					else {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(MsalesStatus.NULL));
					}
				} // Goods from json with incorrect goodsId
				else {
					hashErrors.put("Goods", MsalesValidator.MCP_GOODS_ID_NULL);
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
				}
			} // Goods from json null
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
