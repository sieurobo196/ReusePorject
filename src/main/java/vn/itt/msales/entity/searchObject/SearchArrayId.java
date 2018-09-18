/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.searchObject;

import java.util.Date;

/**
 *
 * @author vtm
 */
public class SearchArrayId {
    private Integer[] ids;
    private Date fromDate;
    private Date toDate;
    private String searchText;
    private Integer statusId;

    public SearchArrayId() {
    }

    public SearchArrayId(Integer[] ids, Date fromDate, Date toDate, String searchText, Integer statusId) {
        this.ids = ids;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.searchText = searchText;
        this.statusId = statusId;
    }

    public Integer[] getIds() {
        return ids;
    }

    public void setIds(Integer[] ids) {
        this.ids = ids;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
    
    
}
