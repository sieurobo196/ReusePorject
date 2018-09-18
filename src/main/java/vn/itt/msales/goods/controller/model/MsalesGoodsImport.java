package vn.itt.msales.goods.controller.model;

import java.util.List;

/**
 *
 * @author ChinhNQ
 */
public class MsalesGoodsImport {

    private String goodsCategoryCode;
    private String goodsCode;
    private String goodsName;
    private Double goodsPrice;
    private Integer goodsCoeffient;
    private Integer goodsReturnAllow;
    private Integer goodsFocus;
    private List<MsalesUnitImport> goodsUnits;
    private String goodsMinUnit;
    private Integer goodsStatus;

    public MsalesGoodsImport() {
    }

    public String getGoodsCategoryCode() {
        return goodsCategoryCode;
    }

    public void setGoodsCategoryCode(String goodsCategoryCode) {
        this.goodsCategoryCode = goodsCategoryCode;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public Integer getGoodsCoeffient() {
        return goodsCoeffient;
    }

    public void setGoodsCoeffient(Integer goodsCoeffient) {
        this.goodsCoeffient = goodsCoeffient;
    }

    public Integer getGoodsReturnAllow() {
        return goodsReturnAllow;
    }

    public void setGoodsReturnAllow(Integer goodsReturnAllow) {
        this.goodsReturnAllow = goodsReturnAllow;
    }

    public Integer getGoodsFocus() {
        return goodsFocus;
    }

    public void setGoodsFocus(Integer goodsFocus) {
        this.goodsFocus = goodsFocus;
    }

    public List<MsalesUnitImport> getGoodsUnits() {
        return goodsUnits;
    }

    public void setGoodsUnits(List<MsalesUnitImport> goodsUnits) {
        this.goodsUnits = goodsUnits;
    }

    public String getGoodsMinUnit() {
        return goodsMinUnit;
    }

    public void setGoodsMinUnit(String goodsMinUnit) {
        this.goodsMinUnit = goodsMinUnit;
    }

    public Integer getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(Integer goodsStatus) {
        this.goodsStatus = goodsStatus;
    }


}
