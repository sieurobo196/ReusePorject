package vn.itt.msales.entity.searchObject;

import java.util.ArrayList;
import java.util.List;

import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsCategory;

public class StockGoods {

    private GoodsCategory goodsCategory;

    private List<Goods> goods = new ArrayList<>();

    public StockGoods() {
    }

    public StockGoods(GoodsCategory goodsCategory, List<Goods> goods) {
        this.goodsCategory = goodsCategory;
        this.goods = goods;
    }

    public GoodsCategory getGoodsCategory() {
        return goodsCategory;
    }

    public void setGoodsCategory(GoodsCategory goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public List<Goods> getGoods() {
        return goods;
    }

    public void setGoods(List<Goods> goods) {
        this.goods = goods;
    }

    public int count() {
        if (goods == null) {
            return 0;
        }
        return goods.size();
    }

    public void add(Goods goods) {
        this.goods.add(goods);
    }

}
