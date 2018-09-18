package vn.itt.msales.entity.searchObject;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonFilter("JSONFILTER")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class POSInfo {
	//tạo biến
	private Integer posId;
	private String posName;
	private String posAddress;
	private Integer total;
	
	public POSInfo() {
	}
	
	public POSInfo(Integer posId, String posName, String posAddress) {
		this.posId = posId;
		this.posName = posName;
		this.posAddress = posAddress;
	}
	
	public Integer getPosId() {
		return posId;
	}
	public void setPosId(Integer posId) {
		this.posId = posId;
	}
	public String getPosName() {
		return posName;
	}
	public void setPosName(String posName) {
		this.posName = posName;
	}
	public String getPosAddress() {
		return posAddress;
	}
	public void setPosAddress(String posAddress) {
		this.posAddress = posAddress;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}
	
}
