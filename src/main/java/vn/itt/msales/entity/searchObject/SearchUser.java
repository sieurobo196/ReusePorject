/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.searchObject;

/**
 *
 * @author vtm
 */
public class SearchUser {

    private Integer channelId;
    private Integer locationId;
    private Integer statusId;
    private boolean isActive;
    private Integer userRoleId;
    private String searchText;
    private Integer activeId;

    public SearchUser() {
    }
    
    public SearchUser(Integer channelId, Integer locationId, Integer statusId, boolean isActive, Integer userRoleId, String searchText, Integer activeId) {
        this.channelId = channelId;
        this.locationId = locationId;
        this.statusId = statusId;
        this.isActive = isActive;
        this.userRoleId = userRoleId;
        this.searchText = searchText;
        this.activeId = activeId;
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

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Integer getActiveId() {
        return activeId;
    }

    public void setActiveId(Integer activeId) {
        this.activeId = activeId;
    }

    
}
