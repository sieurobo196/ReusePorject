/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.searchObject;

import vn.itt.msales.entity.GoodsCategory;

/**
 *
 * @author vtm
 */
public class StockGoodsApp {
    private GoodsCategory goodsCategory;
    private GoodsApp[] goods;

    public StockGoodsApp() {
    }

    
    public StockGoodsApp(GoodsCategory goodsCategory, GoodsApp[] goods) {
        this.goodsCategory = goodsCategory;
        this.goods = goods;
    }

    public GoodsCategory getGoodsCategory() {
        return goodsCategory;
    }

    public void setGoodsCategory(GoodsCategory goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public GoodsApp[] getGoods() {
        return goods;
    }

    public void setGoods(GoodsApp[] goods) {
        this.goods = goods;
    }

   
    
}
