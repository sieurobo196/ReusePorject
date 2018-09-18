/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.customercare.model;

import java.util.Date;

/**
 * Search options customer care information.
 * <p>
 * @author ChinhNQ
 */
public class MsalesSearchCCI {

    private Integer channelId;
    private Integer locationId;
    private Integer implementEmployeeId;
    private Date fromDate;
    private Date toDate;
    private String searchText;

    public MsalesSearchCCI() {

    }
    public MsalesSearchCCI(Integer channelId, Integer locationId, Integer implementEmployeeId, Date fromDate, Date toDate, String searchText) {
        this.channelId = channelId;
        this.locationId = locationId;
        this.implementEmployeeId = implementEmployeeId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.searchText = searchText;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getImplementEmployeeId() {
        return implementEmployeeId;
    }

    public void setImplementEmployeeId(Integer implementEmployeeId) {
        this.implementEmployeeId = implementEmployeeId;
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

}
