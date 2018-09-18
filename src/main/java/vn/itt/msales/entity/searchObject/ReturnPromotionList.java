/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.searchObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import vn.itt.msales.entity.Promotion;

/**
 *
 * @author vtm
 */
public class ReturnPromotionList {

    private Integer salesTransId;
    private Integer salesOrderId;

    @JsonIgnoreProperties(value = {"isMany","goodsCategoryList", "reg", "description", "goodsIdList", "proAwardGoodsCategory", "discount", "proAwardOtherQuantity", "", "proAwardUnits", "goodsIdMins", "awardOthers", "proRanges", "goodsCategoryIdMins", "proAwardGoodss", "approveRoleId", "approveRoles", "startDate", "endDate", "companyId", "companys", "statuss", "code", "note", "approveUserId", "applyScope", "scope", "conditionQuantity", "quantity", "proAwardCode", "proAwardQuantity", "proAwardGoodsId", "proAwardUnitId", "goodsCategorys", "promotionAwards", "goodsIdMin", "quantityMin", "awardOtherId", "proRangeId", "goodsCategoryIdMin", "flagLevel", "flag", "urlImage", "isRegister", "isOnce", "createdAt", "createdUser", "updatedAt", "updatedUser", "deletedAt", "deletedUser", "register", "once", "conditionList", "goodsList", "isRange", "promotionRangeAward"})
    private List<Promotion> promotionList;

    public ReturnPromotionList() {
    }

    public ReturnPromotionList(Integer salesTransId, Integer salesOrderId, List<Promotion> list) {
        this.salesTransId = salesTransId;
        this.salesOrderId = salesOrderId;
        this.promotionList = list;
    }

    public Integer getSalesTransId() {
        return salesTransId;
    }

    public void setSalesTransId(Integer salesTransId) {
        this.salesTransId = salesTransId;
    }

    public Integer getSalesOrderId() {
        return salesOrderId;
    }

    public void setSalesOrderId(Integer salesOrderId) {
        this.salesOrderId = salesOrderId;
    }

    public List<Promotion> getPromotionList() {
        return promotionList;
    }

    public void setPromotionList(List<Promotion> promotionList) {
        this.promotionList = promotionList;
    }

}
