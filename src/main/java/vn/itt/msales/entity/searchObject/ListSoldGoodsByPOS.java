package vn.itt.msales.entity.searchObject;

import java.util.Date;
import java.util.List;

import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.entity.Goods;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class ListSoldGoodsByPOS {
	@JsonFormat(pattern = MsalesValidator.DATE_FORMAT_LONG, shape = JsonFormat.Shape.STRING, timezone = MsalesValidator.GMT)
	private Date soldDate;
	
	private List<Goods> goodss;

	public ListSoldGoodsByPOS() {
	}

	public ListSoldGoodsByPOS(Date soldDate, List<Goods> goodss) {
		this.soldDate = soldDate;
		this.goodss = goodss;
	}

	public Date getSoldDate() {
		return soldDate;
	}

	public void setSoldDate(Date soldDate) {
		this.soldDate = soldDate;
	}

	@JsonIgnoreProperties(value={"isRecover","isFocus"})
	public List<Goods> getGoodss() {
		return goodss;
	}

	public void setGoodss(List<Goods> goodss) {
		this.goodss = goodss;
	}
	
}
