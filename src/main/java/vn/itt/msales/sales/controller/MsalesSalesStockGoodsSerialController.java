package vn.itt.msales.sales.controller;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.SalesStockGoodsSerial;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResponse;

/**
 * 
 * @author DuanND
 * 
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.SALES_STOCK_GOODS_SERIAL.NAME)
public class MsalesSalesStockGoodsSerialController extends CsbController {

	/**
	 * 
	 */

	/**
	 * Get Sales_Stock_Goods_Serial by Id
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = MsalesConstants.MODULE.SALES_STOCK_GOODS_SERIAL.ACTION_GET_SALES_STOCK_GOODS_SERIAL, method = RequestMethod.POST)
	public @ResponseBody
	String getSalesStockGoodsSerial(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
				.toString();
		// jsonString not null
		if (jsonString != null) {
			SalesStockGoodsSerial salesStockGoodsSerial = null;
			try {
				// parse jsonString to a salesStockGoodsSerial Object
				salesStockGoodsSerial = MsalesJsonUtils.getObjectFromJSON(
						jsonString, SalesStockGoodsSerial.class);
			}
			// jsonString syntax incorrect
			catch (Exception ex) {
				return MsalesJsonUtils.getJSONFromOject(MsalesResponse
						.create(MsalesStatus.JSON_INVALID));
			}
			// salesStockGoodsSerial from json not null
			if (salesStockGoodsSerial != null) {
				// salesStockGoodsSerial from json with correct Id
				if (salesStockGoodsSerial.getId() >= 0) {
					// get salesStockGoodsSerial from DB
					salesStockGoodsSerial = dataService.getRowById(
							salesStockGoodsSerial.getId(),
							SalesStockGoodsSerial.class);
					// salesStockGoodsSerial not null
					if (salesStockGoodsSerial != null) {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(HttpStatus.OK, salesStockGoodsSerial));
					}
					// salesStockGoodsSerial null
					else {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(MsalesStatus.NULL));
					}
				}
				// salesStockGoodsSerial from json with incorrect Id
				else {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.JSON_VALUE_INVALID));
				}
			}
			// salesStockGoodsSerial from json null
			else {
				return MsalesJsonUtils.getJSONFromOject(MsalesResponse
						.create(MsalesStatus.NULL));
			}
		}
		// jsonString null
		else {
			return MsalesJsonUtils.getJSONFromOject(MsalesResponse
					.create(MsalesStatus.NULL));
		}
	}

	/**
	 * Create a Sales_Stock_Goods_Serial
	 * 
	 * @param request
	 *            is a jsonString contains data to create a
	 *            Sales_Stock_Goods_Serial
	 * @return a jsonString have status, code and contents
	 */
	@RequestMapping(value = MsalesConstants.MODULE.SALES_STOCK_GOODS_SERIAL.ACTION_CREATE_SALES_STOCK_GOODS_SERIAL, method = RequestMethod.POST)
	public @ResponseBody
	String createSalesStockGoodsSerial(HttpServletRequest request) {
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
				.toString();
		// jsonString not null
		if (jsonString != null) {
			SalesStockGoodsSerial salesStockGoods = null;
			try {
				// parse jsonString to a salesStockGoods Object
				salesStockGoods = MsalesJsonUtils.getObjectFromJSON(jsonString,
						SalesStockGoodsSerial.class);
			} // jsonString syntax incorrect
			catch (Exception ex) {
				return MsalesJsonUtils.getJSONFromOject(MsalesResponse
						.create(MsalesStatus.JSON_INVALID));
			}
			// salesStockGoods from json not null
			if (salesStockGoods != null) {
				Goods goods = null;
				SalesStock salesStock = null;
				Status status = null;
				if (salesStockGoods.getGoodsId() > 0
						&& salesStockGoods.getStockId() > 0
						&& salesStockGoods.getStatusId() > 0) {
					// get salesStock with id from salesStockGoods
					salesStock = dataService.getRowById(
							salesStockGoods.getStockId(), SalesStock.class);
					// get goods with id from GoodsId
					goods = dataService.getRowById(
							salesStockGoods.getGoodsId(), Goods.class);
					// get status with goodsStatusId
					status = dataService.getRowById(
							salesStockGoods.getStatusId(), Status.class);
				}
				// goods or salesStock or status is not exist
				if (salesStock == null || goods == null || status == null) {
					// return message warning goods or salesStock or status is
					// not exist in DB
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.NULL));
				}

				// goods, salesStock and status is exist
				// save salesStockGoods to DB
				salesStockGoods.setCreatedAt(new Date());
				salesStockGoods.setUpdatedAt(new Date());
				salesStockGoods.setDeletedAt(new Date());
				int ret = dataService.insertRow(salesStockGoods);

				// salesStockGoods from DB not null
				if (ret > 0) {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(HttpStatus.OK, null));
				} // salesStockGoods from DB null
				else {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.NULL));
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
	 * Update Sales_Stock_Goods_Serial
	 * 
	 * @param request
	 *            is jsonString contain data to update Sales_Stock_Goods_Serial
	 * @return a jsonString have status, code and contents
	 */

	@SuppressWarnings("unchecked")
	@RequestMapping(value = MsalesConstants.MODULE.SALES_STOCK_GOODS_SERIAL.ACTION_UPDATE_SALES_STOCK_GOODS_SERIAL, method = RequestMethod.POST)
	public @ResponseBody
	String updateSalesStockGoodsSerial(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
				.toString();
		// jsonString not null
		if (jsonString != null) {
			LinkedHashMap<String, Object> salesStockGoods;
			try {
				// parse jsonString to a salesStockGoods Object
				salesStockGoods = MsalesJsonUtils.getObjectFromJSON(jsonString,
						LinkedHashMap.class);
			} // jsonString syntax incorrect
			catch (Exception ex) {
				return MsalesJsonUtils.getJSONFromOject(MsalesResponse
						.create(MsalesStatus.JSON_INVALID));
			}
			// salesStockGoods from json not null
			if (salesStockGoods != null) {
				// get stock, goods, status from their Id
				int stockId;
				int goodsId;
				int statusId;
				try {
					stockId = Integer.parseInt(salesStockGoods.get("stockId")
							.toString());
					goodsId = Integer.parseInt(salesStockGoods.get("goodsId")
							.toString());
					statusId = Integer.parseInt(salesStockGoods.get("statusId")
							.toString());

				} catch (Exception ex) {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.JSON_INVALID));
				}

				SalesStock salesStock = dataService.getRowById(stockId,
						SalesStock.class);
				Goods goods = dataService.getRowById(goodsId, Goods.class);
				Status status = dataService.getRowById(statusId, Status.class);
				if (salesStock != null && goods != null && status != null) {
					salesStockGoods.put("stock", salesStock);
					salesStockGoods.put("goods", goods);
					salesStockGoods.put("status", status);
				} else {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.NULL));
				}

				// update salesStockGoods to DB
				int ret = dataService.updateSynchronize(
						SalesStockGoodsSerial.class, salesStockGoods);
				// update success
				if (ret > 0) {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(HttpStatus.OK, null));
				} // update failed
				else {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.NULL));
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
	 * Delete Sales_Stock_Goods_Serial
	 * 
	 * @param request
	 *            is a jsonString have id and deletedUser to delete
	 *            Sales_Stock_Goods_Serial
	 * @return a jsonString to have status, code and contents
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = MsalesConstants.MODULE.SALES_STOCK_GOODS_SERIAL.ACTION_DELETE_SALES_STOCK_GOODS_SERIAL, method = RequestMethod.POST)
	public @ResponseBody
	String deleteSalesStockGoodsSerial(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
				.toString();
		// jsonString not null
		if (jsonString != null) {
			LinkedHashMap<String, Object> salesStockGoods;
			try {
				// parse jsonString to a salesStockGoods Object
				salesStockGoods = MsalesJsonUtils.getObjectFromJSON(jsonString,
						LinkedHashMap.class);
			} // jsonString syntax incorrect
			catch (Exception ex) {
				return MsalesJsonUtils.getJSONFromOject(MsalesResponse
						.create(MsalesStatus.JSON_INVALID));
			}
			// salesStockGoods from json not null
			if (salesStockGoods != null) {
				// delete salesStockGoods
				int ret = dataService.deleteSynchronize(
						SalesStockGoodsSerial.class, salesStockGoods);

				// delete salesStockGoods success
				if (ret > 0) {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(HttpStatus.OK, null));
				} // delete failed
				else {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.NULL));
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
	 * Get List Sales_Stock_Goods_Serial By StockId
	 * 
	 * @param request
	 *            is jsonString have stockId
	 * @return a jsonString include all Sales_Stock_Goods_Serial have general
	 *         stockId
	 */
	@RequestMapping(value = MsalesConstants.MODULE.SALES_STOCK_GOODS_SERIAL.ACTION_GET_LIST_SALES_STOCK_GOODS_SERIAL_BY_STOCKID, method = RequestMethod.POST)
	public @ResponseBody
	String getListSalesStockGoodsSerialByStockId(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
				.toString();
		// jsonString not null
		if (jsonString != null) {
			SalesStockGoodsSerial salesStockGoods;
			try {
				// parse jsonString to a salesStockGoods Object
				salesStockGoods = MsalesJsonUtils.getObjectFromJSON(jsonString,
						SalesStockGoodsSerial.class);
			} // jsonString syntax incorrect
			catch (Exception ex) {
				return MsalesJsonUtils.getJSONFromOject(MsalesResponse
						.create(MsalesStatus.JSON_INVALID));
			}
			// salesStockGoods from json not null
			if (salesStockGoods != null) {
				// SalesStock from salesStockGoods with correct stockId
				if (salesStockGoods.getStockId() > 0) {
					// get List salesStockGoods by stockId
					ParameterList parameterList = new ParameterList("stock.id",
							salesStockGoods.getStockId());
					List<SalesStockGoodsSerial> list = dataService
							.getListOption(SalesStockGoodsSerial.class,
									parameterList);
					// list salesStockGoods not null
					if (list != null) {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(HttpStatus.OK, list));
					} // list salesStockGoods null
					else {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(MsalesStatus.NULL));
					}
				} // SalesStock from json with incorrect stockId
				else {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.JSON_VALUE_INVALID));
				}
			} // stock from json null
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
	 * Get List Sales_Stock_Goods By GoodsId
	 * 
	 * @param request
	 *            is jsonString have goodsId
	 * @return a jsonString contains all Sales_Stock_Goods have general goodsId
	 */
	@RequestMapping(value = MsalesConstants.MODULE.SALES_STOCK_GOODS_SERIAL.ACTION_GET_LIST_SALES_STOCK_GOODS_SERIAL_BY_GOODSID, method = RequestMethod.POST)
	public @ResponseBody
	String getListSalesStockGoodsSerialByGoodsId(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
				.toString();
		// jsonString not null
		if (jsonString != null) {
			SalesStockGoodsSerial salesStockGoods;
			try {
				// parse jsonString to a salesStockGoods Object
				salesStockGoods = MsalesJsonUtils.getObjectFromJSON(jsonString,
						SalesStockGoodsSerial.class);
			} // jsonString syntax incorrect
			catch (Exception ex) {
				return MsalesJsonUtils.getJSONFromOject(MsalesResponse
						.create(MsalesStatus.JSON_INVALID));
			}
			// salesStockGoods from json not null
			if (salesStockGoods != null) {
				// SalesStock from salesStockGoods with correct stockId
				if (salesStockGoods.getGoodsId() > 0) {
					// get List salesStockGoods by goodsId
					ParameterList parameterList = new ParameterList("goods.id",
							salesStockGoods.getGoodsId());
					List<SalesStockGoodsSerial> list = dataService
							.getListOption(SalesStockGoodsSerial.class,
									parameterList);
					// list salesStockGoods not null
					if (list != null) {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(HttpStatus.OK, list));
					} // list salesStockGoods null
					else {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse
								.create(MsalesStatus.NULL));
					}
				} // Goods from json with incorrect goodsId
				else {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.JSON_VALUE_INVALID));
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
