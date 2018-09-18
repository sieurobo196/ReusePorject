package vn.itt.msales.entity.searchObject;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import vn.itt.msales.entity.User;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonFilter("JSONFILTER")
public class SupSellApp {
	//variable
	private long totalPrice;
	private long totalPriceFocus;
	private long totalPriceDate;
	private long totalPriceDateFocus;
	private long totalSoldDate;
	private long totalSoldDateFocus;
	private long totalSold;
	private int targetPOSNew;
	private int posNew;
	private long totalSoldFocus;
	private List<User> users;
	//Constructor
	public SupSellApp() {
	}
	public SupSellApp(int totalPrice, int totalPriceFocus, int totalPriceDate,
			int totalPriceDateFocus, int totalSoldDate, int totalSoldDateFocus,
			int totalSold, int targetPOSNew, int posNew, List<User> user) {
		this.totalPrice = totalPrice;
		this.totalPriceFocus = totalPriceFocus;
		this.totalPriceDate = totalPriceDate;
		this.totalPriceDateFocus = totalPriceDateFocus;
		this.totalSoldDate = totalSoldDate;
		this.totalSoldDateFocus = totalSoldDateFocus;
		this.totalSold = totalSold;
		this.targetPOSNew = targetPOSNew;
		this.posNew = posNew;
		this.users = user;
	}
	//getter and setter
	public long getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(long totalPrice) {
		this.totalPrice = totalPrice;
	}
	public long getTotalPriceFocus() {
		return totalPriceFocus;
	}
	public void setTotalPriceFocus(long totalPriceFocus) {
		this.totalPriceFocus = totalPriceFocus;
	}
	public long getTotalPriceDate() {
		return totalPriceDate;
	}
	public void setTotalPriceDate(long totalPriceDate) {
		this.totalPriceDate = totalPriceDate;
	}
	public long getTotalPriceDateFocus() {
		return totalPriceDateFocus;
	}
	public void setTotalPriceDateFocus(long totalPriceDateFocus) {
		this.totalPriceDateFocus = totalPriceDateFocus;
	}
	public long getTotalSoldDate() {
		return totalSoldDate;
	}
	public void setTotalSoldDate(long totalSoldDate) {
		this.totalSoldDate = totalSoldDate;
	}
	public long getTotalSoldDateFocus() {
		return totalSoldDateFocus;
	}
	public void setTotalSoldDateFocus(long totalSoldDateFocus) {
		this.totalSoldDateFocus = totalSoldDateFocus;
	}
	public long getTotalSold() {
		return totalSold;
	}
	public void setTotalSold(long totalSold) {
		this.totalSold = totalSold;
	}
	public int getTargetPOSNew() {
		return targetPOSNew;
	}
	public void setTargetPOSNew(int targetPOSNew) {
		this.targetPOSNew = targetPOSNew;
	}
	public int getPosNew() {
		return posNew;
	}
	public void setPosNew(int posNew) {
		this.posNew = posNew;
	}
	@JsonIgnoreProperties(value = {"password", "tel","userCode","monitoringUsers","lastName", "firstName", "statuss", "employerUsers", "username", "sex", "email", "isActive", "activeCode", "employerType", "useEvoucher", "statusId", "createdUser", "updatedUser", "locations", "companys", "birthday", "yahooId", "skypeId", "isdn", "address", "note", "ipLastLogin"})
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public long getTotalSoldFocus() {
		return totalSoldFocus;
	}
	public void setTotalSoldFocus(long totalSoldFocus) {
		this.totalSoldFocus = totalSoldFocus;
	}
	
	
	
	
}
