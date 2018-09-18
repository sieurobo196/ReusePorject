/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.app.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import vn.itt.msales.entity.MsalesCompany;
import vn.itt.msales.entity.Session;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author vtm
 */
public interface LoginService {
    public boolean login(String username,String password,DataService dataService,HttpServletRequest request);
    public boolean loginSHA256(String username,String passwordSHA256,DataService dataService,HttpServletRequest request);
    public boolean login(int id,String password,DataService dataService,HttpServletRequest request);
    public boolean loginSHA256(int id,String passwordSHA256,DataService dataService,HttpServletRequest request);
    
    public List<UserRoleChannel> loginWeb(String username,String password256,DataService dataService);
    
    public int checkLogin(String token,DataService dataService,HttpServletRequest request,String url);    
    public boolean logout(Session session,DataService dataService,HttpServletRequest request);
    
    //get UserRole
    public UserRole getUserRole(int userId, DataService dataService);
    
    //get UserRoleChannel
    public List<UserRoleChannel> getListUserRoleChannel(int userId,DataService dataService);
    
    public MsalesLoginUserInf doLogin(List<UserRoleChannel> userRoleChannelList,DataService dataService,HttpServletRequest request);
        
    public MsalesCompany loginMasterAdmin(String username,String password,DataService dataService);
}
