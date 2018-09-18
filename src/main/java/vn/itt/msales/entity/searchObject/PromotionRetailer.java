/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.searchObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.PromotionAccumulationRetailer;
import vn.itt.msales.entity.PromotionTransRetailer;
import vn.itt.msales.entity.Status;

/**
 *
 * @author vtm
 */
public class PromotionRetailer {

    private Integer id;
    private Integer transRetailerId;
    private Integer accumulationRetailerId;

    @JsonIgnoreProperties(value = {"posCode", "channels", "locations", "statuss", "hierarchy", "ownerName", "ownerCode",
        "ownerCodeLocation", "tel", "mobile", "otherTel", "fax", "email", "website", "lat", "lng", "isActive",
        "beginAt", "endAt", "createdAt", "createdUser","note"})
    private POS poss;

    @JsonIgnoreProperties(value = {"unitId","isFocus", "factor", "isRecover", "goodsCategorys", "goodsCode", "statuss", "createdAt", "createdUser", "updatedAt", "updatedUser", "deletedAt", "deletedUser"})
    private Object awards;

    @JsonIgnoreProperties(value = {"statusTypes"})
    private Status statuss;
    private boolean approve;

    public PromotionRetailer() {
    }

    public PromotionRetailer(Integer id, Integer transRetailerId, Integer accumulationRetailerId, POS poss, Object awards, Status statuss, boolean approve) {
        this.id = id;
        this.transRetailerId = transRetailerId;
        this.accumulationRetailerId = accumulationRetailerId;
        this.poss = poss;
        this.awards = awards;
        this.statuss = statuss;
        this.approve = approve;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTransRetailerId() {
        return transRetailerId;
    }

    public void setTransRetailerId(Integer transRetailerId) {
        this.transRetailerId = transRetailerId;
    }

    public Integer getAccumulationRetailerId() {
        return accumulationRetailerId;
    }

    public void setAccumulationRetailerId(Integer accumulationRetailerId) {
        this.accumulationRetailerId = accumulationRetailerId;
    }

    public POS getPoss() {
        return poss;
    }

    public void setPoss(POS poss) {
        this.poss = poss;
    }

    public Object getAwards() {
        return awards;
    }

    public void setAwards(Object awards) {
        this.awards = awards;
    }

    public Status getStatuss() {
        return statuss;
    }

    public void setStatuss(Status statuss) {
        this.statuss = statuss;
    }

    public boolean isApprove() {
        return approve;
    }

    public void setApprove(boolean approve) {
        this.approve = approve;
    }
    
    public static PromotionRetailer convertObject(Object object) {
        PromotionRetailer promotionRetailer = new PromotionRetailer();
        if (object instanceof PromotionTransRetailer) {
            promotionRetailer.setId(((PromotionTransRetailer) object).getId());
            promotionRetailer.setTransRetailerId(((PromotionTransRetailer) object).getTransRetailerId());
            promotionRetailer.setAccumulationRetailerId(0);
            promotionRetailer.setPoss(((PromotionTransRetailer) object).getPoss());
            promotionRetailer.setStatuss(((PromotionTransRetailer) object).getStatuss());
            promotionRetailer.setApprove(((PromotionTransRetailer) object).isApprove());
            promotionRetailer.setAwards(((PromotionTransRetailer) object).getAwards());
        } else if (object instanceof PromotionAccumulationRetailer) {
            promotionRetailer.setId(((PromotionAccumulationRetailer) object).getId());
            promotionRetailer.setTransRetailerId(0);
            promotionRetailer.setAccumulationRetailerId(((PromotionAccumulationRetailer) object).getAccumulationRetailerId());
            promotionRetailer.setPoss(((PromotionAccumulationRetailer) object).getRetailers());
            promotionRetailer.setStatuss(((PromotionAccumulationRetailer) object).getStatuss());
            promotionRetailer.setApprove(((PromotionAccumulationRetailer) object).isApprove());
            promotionRetailer.setAwards(((PromotionAccumulationRetailer) object).getAwards());
        }
        return promotionRetailer;
    }

    public static List<PromotionRetailer> convertList(List list) {
        List<PromotionRetailer> ret = new ArrayList<>();
        for (Object object : list) {
            ret.add(PromotionRetailer.convertObject(object));
        }
        return ret;
    }

}
