package vn.itt.msales.entity.searchObject;

import java.util.List;

import vn.itt.msales.entity.CustomerCareDetails;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonFilter("JSONFILTER")
public class CustomerCareDetailsById {
	private String groupName;
	private List<CustomerCareDetails> contents;
	
	public CustomerCareDetailsById() {
	}
	public CustomerCareDetailsById(String groupName,
			List<CustomerCareDetails> contents) {
		this.groupName = groupName;
		this.contents = contents;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	@JsonIgnoreProperties(value = {"workflows", "channels","customerCareInformations"})
	public List<CustomerCareDetails> getContents() {
		return contents;
	}
	public void setContents(List<CustomerCareDetails> contents) {
		this.contents = contents;
	}
	
}
