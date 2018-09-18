package vn.itt.msales.entity.searchObject;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonFilter("JSONFILTER")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class OrderDetailsList {
	//Biến
	private Integer orderDetailsId;
	private String goodsName;
	private String unitName;
	private Integer quantity;
	//
	public OrderDetailsList() {
	}
	public OrderDetailsList(Integer orderDetailsId, String goodsName,
			String unitName, Integer quantity) {
		this.orderDetailsId = orderDetailsId;
		this.goodsName = goodsName;
		this.unitName = unitName;
		this.quantity = quantity;
	}
	public Integer getOrderDetailsId() {
		return orderDetailsId;
	}
	public void setOrderDetailsId(Integer orderDetailsId) {
		this.orderDetailsId = orderDetailsId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
}
