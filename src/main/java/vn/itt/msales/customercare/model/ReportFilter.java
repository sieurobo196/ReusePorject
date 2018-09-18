package vn.itt.msales.customercare.model;

import java.util.HashMap;
import java.util.Map;

public class ReportFilter{
	private Long province;
	private Long district;
	private String startDate;
	private String endDate;
	private Long employee;
	private String exportType;
	private String template;
	private String fileName;
	private Map<String, Object> paramsMap = new HashMap<String, Object>();
	
	private String startDateString;
	private String endDateString;
	
	public Map<String, Object> getParamsMap() {
		return paramsMap;
	}
	public void putParamsMap(String field, Object value) {
		this.paramsMap.put(field, value);
	}
	public Long getProvince() {
		return province;
	}
	public void setProvince(Long province) {
		this.province = province;
	}
	public Long getDistrict() {
		return district;
	}
	public void setDistrict(Long district) {
		this.district = district;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public Long getEmployee() {
		return employee;
	}
	public void setEmployee(Long employee) {
		this.employee = employee;
	}
	public String getExportType() {
		return exportType;
	}
	public void setExportType(String exportType) {
		this.exportType = exportType;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getStartDateString() {
		return startDateString;
	}
	public void setStartDateString(String startDateString) {
		this.startDateString = startDateString;
		this.startDate=startDateString;
	}
	public String getEndDateString() {
		return endDateString;
	}
	public void setEndDateString(String endDateString) {
		this.endDateString = endDateString;		
		this.endDate=endDateString;
	}
	public String getStartDate() {
		return startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	
	
}
