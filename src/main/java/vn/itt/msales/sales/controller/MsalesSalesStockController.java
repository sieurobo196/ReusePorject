package vn.itt.msales.sales.controller;

import java.util.Date;
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
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.SalesStock;
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
@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCK.NAME)
public class MsalesSalesStockController extends CsbController {
	/**
	 * Get Sales_Stock
	 * 
	 * @param request is jsonString have id of Sales_Stock
	 * @return a jsonString include information of Sales_Stock
	 */
	@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCK.ACTION_GET_SALES_STOCK, method = RequestMethod.POST)
	public @ResponseBody String getSalesStock(HttpServletRequest request) {
		// get jsonString from client request via Csb
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
		if (jsonString != null) {
			SalesStock salesStock = new SalesStock();

			try {
				// get Object salesStock from jsonString
				salesStock = MsalesJsonUtils.getObjectFromJSON(jsonString,
						SalesStock.class);
			} catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
			if (salesStock != null) {
				LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
				if (salesStock.getId() != null) {
					// get info salesStock with id
					SalesStock salesStock2 = dataService.getRowById(salesStock.getId(), SalesStock.class);
					if (salesStock2 != null) {
						// return a jsonString include info of salesStock
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, salesStock2));
					} else {
						hashErrors.put("SalesStock",MsalesValidator.DK_SALES_STOCK_ID_NOT_EXIST + salesStock.getId());
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE,hashErrors));
					}
				}
				// location from json with incorrect Id
				else {
					hashErrors.put("SalesStock", MsalesValidator.DK_SALES_STOCK_ID_NULL);
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
				}
			} else {
				// Object from jsonString null
				return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
			}
		} else {
			// jsonString null
			return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
		}
	}

	/**
	 * Create Sales_Stock
	 * 
	 * @param request
	 *            is jsonString contains data to create Sales_Stock
	 * @return a jsonString include status, code and contents
	 */
	@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCK.ACTION_CREATE_SALES_STOCK, method = RequestMethod.POST)
	public @ResponseBody String createSalesStock(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
		// jsonString not null
		if (jsonString != null) {
			SalesStock salesStock = new SalesStock();
			try {
				// parse jsonString to a SalesStock Object
				salesStock = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesStock.class);
			}
			// jsonString syntax incorrect
			catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
			// salesStock from json not null
			if (salesStock != null) {
				LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
				Status status = null;
				if (salesStock.getStatusId() != null) {
					// get status with statusId
					status = dataService.getRowById(salesStock.getStatusId(), Status.class);
					if (status == null) {
						hashErrors.put("Status",MsalesValidator.MCP_STATUS_NOT_EXIST + salesStock.getStatusId());
					}
				}
				Integer salemanUserId = salesStock.getSalemanUserId();
				Integer channelId = salesStock.getChannelId();
				Integer posId = salesStock.getPosId();
				if ((salemanUserId != null && channelId == null && posId == null)
						|| ((salemanUserId == null) && channelId != null && posId == null)
						|| ((salemanUserId == null) && (channelId == null) && posId != null)) {

					if (salemanUserId != null) {
						User user = dataService.getRowById( salesStock.getSalemanUserId(), User.class);
						if (user == null) {
							hashErrors.put("SalemanUser", MsalesValidator.MCP_USER_NOT_EXIST + salesStock.getSalemanUserId());
						}
					}

					if (channelId != null) {
						Channel channel = dataService.getRowById(salesStock.getChannelId(), Channel.class);
						if (channel == null) {
							hashErrors.put("Channel",MsalesValidator.DK_CHANNEL_ID_NOT_EXIST + salesStock.getChannelId());
						}
					}
					if (posId != null) {
						POS pos = dataService.getRowById(salesStock.getPosId(), POS.class);
						if (pos == null) {
							hashErrors.put("POS",MsalesValidator.MCP_POS_NOT_EXIST + salesStock.getPosId());
						}
					}
				} else if (salemanUserId != null && channelId != null && posId == null) {
					hashErrors.put("SalesStock", MsalesValidator.SALES_STOCK_SALESMANUSERID_CHANNELID);
				} else if (salemanUserId != null && channelId == null && posId != null) {
					hashErrors.put("SalesStock", MsalesValidator.SALES_STOCK_SALESMANUSERID_POS);
				} else if (salemanUserId == null && channelId != null && posId != null) {
					hashErrors.put("SalesStock", MsalesValidator.SALES_STOCK_CHANNEL_ID_POS);					
				} else if (salemanUserId != null && channelId != null && posId != null) {
					hashErrors.put("SalesStock", MsalesValidator.SALES_STOCK_THREE_FIELD_NOT_NULL);
				} else {
					hashErrors.put("SalesStock", MsalesValidator.SALES_STOCK_THREE_FIELD_NULL);
				}
				if(salesStock.getCreatedUser() != null){
            		User userRole2 = dataService.getRowById(salesStock.getCreatedUser(), User.class);
            		if(userRole2 == null){
            			hashErrors.put("SalesStock",MsalesValidator.MCP_USER_NOT_EXIST + salesStock.getCreatedUser());
            		}
            	}
				if (hashErrors.size() > 0) {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, hashErrors));
				}
				int ret = 0;
				try {
					// save salesStock to DB
					ret = dataService.insertRow(salesStock);
				} catch (Exception e) {
					Exception ex = (Exception) e.getCause().getCause();
					if (e.getCause().getCause() instanceof ConstraintViolationException) {
						return MsalesJsonUtils.jsonValidate(ex);
					}
				}

				// salesStock from DB not null
				if (ret > 0) {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(HttpStatus.OK, null));
				}
				// salesStock from DB null
				else {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.SQL_INSERT_FAIL));
				}
			}
			// salesStock from json null
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
	 * Update Sales_Stock
	 * 
	 * @param request
	 *            is jsonString have data to update Sales_Stock
	 * @return a jsonString include status. code and contents
	 */
	// @SuppressWarnings("unchecked")
	@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCK.ACTION_UPDATE_SALES_STOCK, method = RequestMethod.POST)
	public @ResponseBody String updateSalesStock(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
		// jsonString not null
		if (jsonString != null) {
			SalesStock salesStock = null;
			try {
				// parse jsonString to a Object
				salesStock = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesStock.class);
			} // jsonString syntax incorrect
			catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
			if (salesStock != null) {
				LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
				// salesStock from json not null
				if (salesStock.getId() != null) {
					SalesStock salesStock2 = dataService.getRowById(salesStock.getId(), SalesStock.class);
					if (salesStock2 == null) {
						hashErrors.put("SalesStock", MsalesValidator.DK_SALES_STOCK_ID_NOT_EXIST + salesStock.getId());
					}
					Status status = null;
					if (salesStock.getStatusId() != null) {
						// get status with statusId
						status = dataService.getRowById(
								salesStock.getStatusId(), Status.class);
						if (status == null) {
							hashErrors.put("Status",MsalesValidator.MCP_STATUS_NOT_EXIST + salesStock.getStatusId());
						}
					}

					Integer salemanUserId = salesStock.getSalemanUserId();
					Integer channelId = salesStock.getChannelId();
					Integer posId = salesStock.getPosId();
					if ((salemanUserId != null && channelId == null && posId == null)
							|| ((salemanUserId == null) && channelId != null && posId == null)
							|| ((salemanUserId == null) && (channelId == null) && posId != null)) {

						if (salemanUserId != null) {
							User user = dataService.getRowById( salesStock.getSalemanUserId(), User.class);
							if (user == null) {
								hashErrors.put("SalemanUser", MsalesValidator.MCP_USER_NOT_EXIST + salesStock.getSalemanUserId());
							}
						}

						if (channelId != null) {
							Channel channel = dataService.getRowById(salesStock.getChannelId(), Channel.class);
							if (channel == null) {
								hashErrors.put("Channel",MsalesValidator.DK_CHANNEL_ID_NOT_EXIST + salesStock.getChannelId());
							}
						}
						if (posId != null) {
							POS pos = dataService.getRowById(salesStock.getPosId(), POS.class);
							if (pos == null) {
								hashErrors.put("POS", MsalesValidator.MCP_POS_NOT_EXIST + salesStock.getPosId());
							}
						}
					} else if (salemanUserId != null && channelId != null && posId == null) {
						hashErrors.put("SalesStock", MsalesValidator.SALES_STOCK_SALESMANUSERID_CHANNELID);
					} else if (salemanUserId != null && channelId == null && posId != null) {
						hashErrors.put("SalesStock", MsalesValidator.SALES_STOCK_SALESMANUSERID_POS);
					} else if (salemanUserId == null && channelId != null && posId != null) {
						hashErrors.put("SalesStock", MsalesValidator.SALES_STOCK_CHANNEL_ID_POS);					
					} else if (salemanUserId != null && channelId != null && posId != null) {
						hashErrors.put("SalesStock", MsalesValidator.SALES_STOCK_THREE_FIELD_NOT_NULL);
					} else {
						hashErrors.put("SalesStock", MsalesValidator.SALES_STOCK_THREE_FIELD_NULL);
					}
					if(salesStock.getUpdatedUser() != null){
	            		User userRole2 = dataService.getRowById(salesStock.getUpdatedUser(), User.class);
	            		if(userRole2 == null){
	            			hashErrors.put("SalesStock", MsalesValidator.MCP_USER_NOT_EXIST + salesStock.getUpdatedUser());
	            		}
	            	}

					if (hashErrors.size() > 0) {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, hashErrors));
					}
					salesStock.setCreatedAt(salesStock2.getCreatedAt());
					salesStock.setCreatedUser(salesStock2.getCreatedUser());
					salesStock.setDeletedAt(salesStock2.getDeletedAt());
					salesStock.setDeletedUser(salesStock2.getDeletedUser());
					salesStock.setUpdatedAt(new Date());
					int ret = 0;
					// update salesStock to DB
					try {
						ret = dataService.updateRow(salesStock);
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
				} // salesStock from json null
				else {
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.JSON_FIELD_REQUIRED, MsalesValidator.DK_SALES_STOCK_ID_NULL));
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
	 * Delete Sales_Stock
	 * 
	 * @param request
	 *            is a jsonString have id and deletedUser
	 * @return a jsonString contain status, code and contents
	 */

	// @SuppressWarnings("unchecked")
	@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCK.ACTION_DELETE_SALES_STOCK, method = RequestMethod.POST)
	public @ResponseBody String deleteSalesStock(HttpServletRequest request) {
		// get jsonString from CSB
		String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
		// jsonString not null
		if (jsonString != null) {
			SalesStock salesStock = null;
			try {
				// parse jsonString to a Object
				salesStock = MsalesJsonUtils.getObjectFromJSON(jsonString, SalesStock.class);
			} // jsonString syntax incorrect
			catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
			// salesStock from json not null
			if (salesStock != null) {
				LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
				// salesStock from json not null
				if (salesStock.getId() != null) {
					SalesStock salesStock2 = dataService.getRowById(salesStock.getId(), SalesStock.class);
					if (salesStock2 == null) {
						hashErrors.put("SalesStock",MsalesValidator.DK_SALES_STOCK_ID_NOT_EXIST + salesStock.getId());
					}
					if(salesStock.getDeletedUser() != null){
	            		User userRole2 = dataService.getRowById(salesStock.getDeletedUser(), User.class);
	            		if(userRole2 == null){
	            			hashErrors.put("SalesStock",MsalesValidator.MCP_USER_NOT_EXIST + salesStock.getDeletedUser());
	            		}
	            	}

					if (hashErrors.size() > 0) {
						return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
					}

					int ret = 0;
					// update salesStock to DB
					try {
						ret = dataService.deleteSynch(salesStock);
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
					hashErrors.put("SalesStock", MsalesValidator.DK_SALES_STOCK_ID_NULL);
					return MsalesJsonUtils.getJSONFromOject(MsalesResponse
							.create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
				}
				// salesStock from json null
			} else {
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
	 * get List Sales_Stock
	 * 
	 * @param request
	 *            is a jsonString to get list Sales_Stock
	 * @return a jsonString have list all Sales_Stock
	 */
	@RequestMapping(value = MsalesConstants.MODULE.SALESSTOCK.ACTION_GET_LIST_SALES_STOCK, method = RequestMethod.POST)
	public @ResponseBody String getListSalesStock(HttpServletRequest request) {
		MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
		ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());
		// get List UserRole from DB
		MsalesResults<SalesStock> lists = dataService.getListOption(SalesStock.class,
				parameterList, true);
		// list not null display list
		return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
				HttpStatus.OK, lists));

	}

}
