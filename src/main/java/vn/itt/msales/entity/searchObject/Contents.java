package vn.itt.msales.entity.searchObject;

import java.util.Date;

import vn.itt.msales.common.json.validator.MsalesValidator;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Contents {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_SHORT, timezone = "GMT+7")
	private Date fromDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_SHORT, timezone = "GMT+7")
	private Date toDate;
	
	private Integer userId;
	
	public Contents() {
	}
	
	public Contents(Date fromDate, Date toDate, Integer userId) {
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.userId = userId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
}
