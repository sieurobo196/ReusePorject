/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.goods.controller.model;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import vn.itt.msales.entity.GoodsCategory;

/**
 *
 * @author vtm_2
 */
public class GoodsCategoryFilter {

    private Integer statusId;
    private String searchText;

    public GoodsCategoryFilter() {
    }

    public GoodsCategoryFilter(Integer statusId, String searchText) {
        this.statusId = statusId;
        this.searchText = searchText;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
  
  

}
