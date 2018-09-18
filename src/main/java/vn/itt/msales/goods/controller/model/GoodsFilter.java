/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.goods.controller.model;

/**
 *
 * @author vtm_2
 */
public class GoodsFilter {
    private String searchText;
    private Integer goodsCategoryId;
    private Integer statusId;

    public GoodsFilter() {
    }

    public GoodsFilter(String searchText, Integer goodsCategoryId, Integer statusId) {
        this.searchText = searchText;
        this.goodsCategoryId = goodsCategoryId;
        this.statusId = statusId;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Integer getGoodsCategoryId() {
        return goodsCategoryId;
    }

    public void setGoodsCategoryId(Integer goodsCategoryId) {
        this.goodsCategoryId = goodsCategoryId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
    public static int calulatorMaxPages(int count, int size) {
        if (count % size != 0) {
            return count / size + 1;
        } else {
            if (count / size < 1) {
                return 1;
            } else {
                return count / size;
            }
        }
    }
   
}
