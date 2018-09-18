/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vn.itt.msales.entity.searchObject;

/**
 *
 * @author Khai
 */
public class AccumulationInfo {
    private Integer promotionId;
    private Integer quantity;
    private Integer amount;    
    private Integer conditionQuantity;

    public AccumulationInfo() {
    }

    public AccumulationInfo(Integer promotionId, Integer quantity, Integer amount, Integer conditionQuantity) {
        this.promotionId = promotionId;
        this.quantity = quantity;
        this.amount = amount;
        this.conditionQuantity = conditionQuantity;
    }

    
    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    
    public Integer getConditionQuantity() {
        return conditionQuantity;
    }

    public void setConditionQuantity(Integer conditionQuantity) {
        this.conditionQuantity = conditionQuantity;
    }
   
    
}
