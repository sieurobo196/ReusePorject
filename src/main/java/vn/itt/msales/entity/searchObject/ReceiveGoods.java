/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.searchObject;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.entity.Goods;

/**
 *
 * @author vtm
 */
public class ReceiveGoods {
    private String transCode;
    private List<Goods> stockGoods;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_LONG, timezone = "GMT+7")
    private Date salesTransDate;
    private Integer posId;
    private Integer createdUser;
    
	public ReceiveGoods() {
	}

	public ReceiveGoods(String transCode, List<Goods> stockGoods) {
		this.transCode = transCode;
		this.stockGoods = stockGoods;
	}

	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	public List<Goods> getStockGoods() {
		return stockGoods;
	}

	public void setStockGoods(List<Goods> stockGoods) {
		this.stockGoods = stockGoods;
	}

	public Date getSalesTransDate() {
		return salesTransDate;
	}

	public void setSalesTransDate(Date salesTransDate) {
		this.salesTransDate = salesTransDate;
	}

	public Integer getPosId() {
		return posId;
	}

	public void setPosId(Integer posId) {
		this.posId = posId;
	}

	public Integer getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(Integer createdUser) {
		this.createdUser = createdUser;
	}
    
}
