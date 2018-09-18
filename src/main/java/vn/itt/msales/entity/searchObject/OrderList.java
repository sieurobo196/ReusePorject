package vn.itt.msales.entity.searchObject;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonFilter("JSONFILTER")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class OrderList {
	//biến
	private Integer orderId;
	private Integer orderUserId;
	private String orderUserName;
	private String transCode;
	//cấu trúc
	public OrderList() {
	}
	public OrderList(Integer orderId, Integer orderUserId, String orderUserName) {
		this.orderId = orderId;
		this.orderUserId = orderUserId;
		this.orderUserName = orderUserName;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getOrderUserId() {
		return orderUserId;
	}
	public void setOrderUserId(Integer orderUserId) {
		this.orderUserId = orderUserId;
	}
	public String getOrderUserName() {
		return orderUserName;
	}
	public void setOrderUserName(String orderUserName) {
		this.orderUserName = orderUserName;
	}
	public String getTransCode() {
		return transCode;
	}
	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}
	
	
}
