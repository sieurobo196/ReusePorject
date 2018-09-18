package vn.itt.msales.entity.searchObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ThongTinCskhForMapMarker {	
	public String thoiGianBH = "";
	public String thoiGianCskh = "";
	public String thoiGianGH   = "";
	public String tenNV;
	public String tenDBH = "";
	public String soDienThoai = "";
	public String diaChi = "";
	public double kinhDo;
	public double viDo;
	public long	  flagPhone=0;
	
	private static SimpleDateFormat dformat = new SimpleDateFormat(
			"dd/MM/yy HH:mm:ss");

	public String getThoiGianBH() {
		return thoiGianBH;
	}

	public void setThoiGianBH(Date thoiGianBH) {
		if (thoiGianBH != null) {
			this.thoiGianBH = dformat.format(thoiGianBH);
		}
	}

	public String getThoiGianCskh() {
		return thoiGianCskh;
	}

	public void setThoiGianCskh(Date thoiGianCskh) {
		if (thoiGianCskh != null) {
			this.thoiGianCskh = dformat.format(thoiGianCskh);
		}
	}
	
	public String getThoiGianGH() {
		return thoiGianGH;
	}

	public void setThoiGianGH(Date thoiGianGH) {
		if (thoiGianGH != null) {
			this.thoiGianGH = dformat.format(thoiGianGH);
		}
	}

	public String getTenDBH() {
		return tenDBH;
	}

	public void setTenDBH(String tenDBH) {
		this.tenDBH = tenDBH;
	}

	public String getSoDienThoai() {
		return soDienThoai;
	}

	public void setSoDienThoai(String soDienThoai) {
		if (soDienThoai != null) {
			this.soDienThoai = soDienThoai;
		}
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		if (diaChi != null) {
			this.diaChi = diaChi;
		}
	}

	public double getKinhDo() {
		return kinhDo;
	}

	public void setKinhDo(Double kinhDo) {
		if (kinhDo != null) {
			this.kinhDo = kinhDo;
		} else {
			kinhDo=0.0;
		}
	}

	public double getViDo() {
		return viDo;
	}

	public void setViDo(Double viDo) {
		if (viDo != null) {
			this.viDo = viDo;
		} else {
			this.viDo = 0.0;
		}
	}

	public long getFlagPhone() {
		return flagPhone;
	}

	public void setFlagPhone(Long flagPhone) {
		if (flagPhone != null) {
			this.flagPhone = flagPhone;
		} else {
			this.flagPhone = 0l;
		}		
	}

	public String getTenNV() {
		return tenNV;
	}

	public void setTenNV(String tenNV) {
		this.tenNV = tenNV;
	}
	
	

}
