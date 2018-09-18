/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.searchObject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import vn.itt.msales.common.OptionItem;

import vn.itt.msales.common.json.validator.MsalesValidator;

/**
 *
 * @author vtm
 */
public class SearchSupRoute {

    private Integer channelId;
    
    private Integer userId;
    
    @JsonIgnore
    private List<OptionItem> listOfEmployees;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_SHORT, locale = MsalesValidator.GMT)
    private Date fromDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_SHORT, locale = MsalesValidator.GMT)
    private Date toDate;

    public SearchSupRoute() {
    }

    public SearchSupRoute(Integer channelId, Integer userId, Date fromDate, Date toDate) {
        this.channelId = channelId;
        this.userId = userId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
    
    ArrayList<LinkedHashMap<String, Object>> employerList;

    public ArrayList<LinkedHashMap<String, Object>> getEmployerList() {
        return employerList;
    }

    public void setEmployerList(
            ArrayList<LinkedHashMap<String, Object>> employerList) {
        this.employerList = employerList;
    }

    /**
     * @return the listOfEmployees
     */
    public List<OptionItem> getListOfEmployees() {
        return listOfEmployees;
    }

    /**
     * @param listOfEmployees the listOfEmployees to set
     */
    public void setListOfEmployees(List<OptionItem> listOfEmployees) {
        this.listOfEmployees = listOfEmployees;
    }

}
