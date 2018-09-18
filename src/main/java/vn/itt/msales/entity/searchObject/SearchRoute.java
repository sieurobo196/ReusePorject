package vn.itt.msales.entity.searchObject;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.UserRoute;

public class SearchRoute {
	private String userName;
	private List<POS> posList;
	private List<UserRoute> routes;
	public SearchRoute() {
	}
	public SearchRoute(String userName, List<POS> posList,
			List<UserRoute> routes) {
		this.userName = userName;
		this.posList = posList;
		this.routes = routes;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@JsonIgnoreProperties(value = {"statuss", "locations", "channels", "hierarchy", "ownerName", "street",
	        "birthday", "owerCode", "owerCodeDate", "owerCodeLocation", "mobile", "otherTel", "fax", "email",
	        "website", "gpkd", "gpkdDate", "gpkdLocation", "note", "isActive", "beginAt", "endAt", "createdAt","createdUser"})
	public List<POS> getPosList() {
		return posList;
	}
	public void setPosList(List<POS> posList) {
		this.posList = posList;
	}
	@JsonIgnoreProperties(value = "note")
	public List<UserRoute> getRoutes() {
		return routes;
	}
	public void setRoutes(List<UserRoute> routes) {
		this.routes = routes;
	}
	
}
