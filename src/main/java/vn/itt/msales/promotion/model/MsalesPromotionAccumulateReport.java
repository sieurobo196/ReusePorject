package vn.itt.msales.promotion.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ChinhNQ
 */
public class MsalesPromotionAccumulateReport implements Serializable {

    private static final long serialVersionUID = -6265235830644796552L;

    private Long posId;
    private String posName;
    private String mobi;
    private String tel;
    private String address;
    private String awardName;
    private Long quantity;
    private Long revenue;
    private Long flagOther;
    private Long ctkmId;
    private String ctkmName;
    private Long categoryId;
    private String categoryName;
    private String goodsNameG;
    private String km;
    private String reason;
    private Long goodsQuantity;
    private Long goodsAmount;
    private Long conditionQuantity;
    private Date createDate;
    private Date updateDate;
    private Long status;
    private String note;
    private String statusName;

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public MsalesPromotionAccumulateReport() {
    }

    public Long getPosId() {
        return posId;
    }

    public void setPosId(Long posId) {
        this.posId = posId;
    }

    public String getPosName() {
        return posName;
    }

    public void setPosName(String posName) {
        this.posName = posName;
    }

    public String getMobi() {
        return mobi;
    }

    public void setMobi(String mobi) {
        this.mobi = mobi;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAwardName() {
        return awardName;
    }

    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getRevenue() {
        return revenue;
    }

    public void setRevenue(Long revenue) {
        this.revenue = revenue;
    }

    public Long getFlagOther() {
        return flagOther;
    }

    public void setFlagOther(Long flagOther) {
        this.flagOther = flagOther;
    }

    public Long getCtkmId() {
        return ctkmId;
    }

    public void setCtkmId(Long ctkmId) {
        this.ctkmId = ctkmId;
    }

    public String getCtkmName() {
        return ctkmName;
    }

    public void setCtkmName(String ctkmName) {
        this.ctkmName = ctkmName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getGoodsNameG() {
        return goodsNameG;
    }

    public void setGoodsNameG(String goodsNameG) {
        this.goodsNameG = goodsNameG;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getGoodsQuantity() {
        return goodsQuantity;
    }

    public void setGoodsQuantity(Long goodsQuantity) {
        this.goodsQuantity = goodsQuantity;
    }

    public Long getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(Long goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public Long getConditionQuantity() {
        return conditionQuantity;
    }

    public void setConditionQuantity(Long conditionQuantity) {
        this.conditionQuantity = conditionQuantity;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
