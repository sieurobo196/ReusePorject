/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.searchObject;

import java.util.List;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserGoodsCategory;
import vn.itt.msales.entity.UserRoleChannel;

/**
 *
 * @author Khai
 */
public class CreateUserAdmin {
    private User user;
    private List<UserRoleChannel> userRoleChannelList;
    private List<UserGoodsCategory> userGoodsCategoryList;

    public CreateUserAdmin() {
    }

    public CreateUserAdmin(User user, List<UserRoleChannel> userRoleChannel, List<UserGoodsCategory> userGoodsCategoryList) {
        this.user = user;
        this.userRoleChannelList = userRoleChannel;
        this.userGoodsCategoryList = userGoodsCategoryList;
    }
    
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<UserRoleChannel> getUserRoleChannelList() {
        return userRoleChannelList;
    }

    public void setUserRoleChannelList(List<UserRoleChannel> userRoleChannelList) {
        this.userRoleChannelList = userRoleChannelList;
    }

    public List<UserGoodsCategory> getUserGoodsCategoryList() {
        return userGoodsCategoryList;
    }

    public void setUserGoodsCategoryList(List<UserGoodsCategory> userGoodsCategoryList) {
        this.userGoodsCategoryList = userGoodsCategoryList;
    }

}
