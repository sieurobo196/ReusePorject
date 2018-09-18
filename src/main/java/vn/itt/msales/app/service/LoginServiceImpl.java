/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.app.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.csb.auth.MsalesRoles;
import vn.itt.msales.csb.auth.MsalesSession;
import vn.itt.msales.database.dbrouting.DatabaseType;
import vn.itt.msales.entity.MsalesCompany;
import vn.itt.msales.entity.Session;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author vtm
 */
public class LoginServiceImpl implements LoginService {

    @Override
    public MsalesLoginUserInf doLogin(List<UserRoleChannel> userRoleChannelList, DataService dataService, HttpServletRequest request) {
        //User userFromDB = userRoleChannel.getUsers();
        // update IP last login
        //userFromDB.setIpLastLogin(request.getRemoteAddr());
        // update to databse
        //dataService.updateRow(userFromDB);
        // get user login
        if (userRoleChannelList == null || userRoleChannelList.isEmpty()) {
            return null;
        }

        MsalesLoginUserInf msalesLoginUserInf = new MsalesLoginUserInf(userRoleChannelList.get(0).getUsers(), userRoleChannelList.get(0).getUserRoles(), request.getSession(), userRoleChannelList);
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("token", msalesLoginUserInf.getToken());
        parameterList.setOrder("lastAccessedTime", "DESC");

        List<Session> sessionList = dataService.getListOption(Session.class, parameterList);
        if (sessionList.isEmpty() || MsalesSession.isExpired(sessionList.get(0).getLastAccessedTime().getTime(), request.getSession().getMaxInactiveInterval())) {
            //check permission
            Session session = new Session();
            session.setLastAccessedTime(new Date());
            session.setToken(msalesLoginUserInf.getToken());
            session.setUserId(userRoleChannelList.get(0).getUsers().getId());
            session.setUserRolerId(userRoleChannelList.get(0).getUserRoles().getId());
            dataService.insertRow(session);
        } else {
            sessionList.get(0).setLastAccessedTime(new Date());
            dataService.updateRow(sessionList.get(0));
        }
        return msalesLoginUserInf;
    }

    @Override
    public int checkLogin(String token, DataService dataService, HttpServletRequest request, String url) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("token", token);
        parameterList.setOrder("lastAccessedTime", "DESC");

        List<Session> sessionList = dataService.getListOption(Session.class, parameterList);
        // token request doesn't exists.
        if (!sessionList.isEmpty()) {
            Session session = sessionList.get(0);
            // check access token is expired.
            if (!MsalesSession.isExpired(session.getLastAccessedTime().getTime(), request.getSession().getMaxInactiveInterval())) {
                session.setLastAccessedTime(new Date());
                dataService.updateRow(session);
                if (!MsalesRoles.checkPermissionDenie(session.getUserRolerId(), url)) {
                    request.setAttribute(MsalesConstants.SESSION, session);
                    return 1;//check login pass
                } else {
                    return -1;//cannot access url
                }
            }
        }
        return 0;//not login
    }

    @Override
    public boolean login(String username, String password, DataService dataService, HttpServletRequest request) {
        return loginSHA256(username, MsalesSession.getSHA256(password), dataService, request);
    }

    @Override
    public boolean loginSHA256(String username, String passwordSHA256, DataService dataService, HttpServletRequest request) {
        if (username != null && username.length() > 0) {
            String hql = "SELECT UserRoleChannel"
                    + " FROM UserRoleChannel AS UserRoleChannel"
                    + " JOIN UserRoleChannel.users AS User"
                    + " WHERE UserRoleChannel.deletedUser = 0"
                    + " AND User.deletedUser = 0"
                    + " AND User.username = :username"
                    + " AND User.password = :password"
                    + " ORDER BY LENGTH(fullCode),fullCode";
            List<MsalesParameter> parameters = MsalesParameter.createList("username", username);
            parameters.add(MsalesParameter.create("password", passwordSHA256));

            List<UserRoleChannel> userRoleChannelList = dataService.executeSelectHQL(hql, parameters, false, 0, 0);

            if (!userRoleChannelList.isEmpty()) {
                doLogin(userRoleChannelList, dataService, request);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean login(int id, String password, DataService dataService, HttpServletRequest request) {
        return loginSHA256(id, MsalesSession.getSHA256(password), dataService, request);
    }

    @Override
    public boolean loginSHA256(int id, String passwordSHA256, DataService dataService, HttpServletRequest request) {
        String hql = "SELECT UserRoleChannel"
                + " FROM UserRoleChannel AS UserRoleChannel"
                + " JOIN UserRoleChannel.users AS User"
                + " WHERE UserRoleChannel.deletedUser = 0"
                + " AND User.deletedUser = 0"
                + " AND User.id = :id"
                + " AND User.password = :password"
                + " ORDER BY LENGTH(fullCode),fullCode";
        List<MsalesParameter> parameters = MsalesParameter.createList("id", id);
        parameters.add(MsalesParameter.create("password", passwordSHA256));

        List<UserRoleChannel> userRoleChannelList = dataService.executeSelectHQL(hql, parameters, false, 1, 1);

        if (!userRoleChannelList.isEmpty()) {
            doLogin(userRoleChannelList, dataService, request);
            return true;
        }
        return false;
    }

    @Override
    public List<UserRoleChannel> loginWeb(String username, String password256,DataService dataService) {
        String hql = "FROM UserRoleChannel"
                + " WHERE deletedUser = 0"
                + " AND users.deletedUser = 0"
                + " AND userRoles.deletedUser = 0"
                + " AND channels.deletedUser = 0"
                + " AND users.username = :username"
                + " AND users.password = :password"
                + " AND userRoles.code NOT IN ('USER_ROLE_SALESMAN')";
        List<MsalesParameter> parameters = MsalesParameter.createList("username", username);
        parameters.add(MsalesParameter.create("password", password256));
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);

    }

    @Override
    public boolean logout(Session session, DataService dataService, HttpServletRequest request) {
        try {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.SECOND, 0 - request.getSession().getMaxInactiveInterval());
            session.setLastAccessedTime(now.getTime());
            session.setDeletedUser(session.getUserId());
            dataService.updateRow(session);
            // clear session
            HttpSession sesion = request.getSession(false);
            if (sesion != null) {
                sesion.invalidate();
            }
            SecurityContextHolder.clearContext();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public UserRole getUserRole(int userId, DataService dataService) {
        ParameterList parameterList = new ParameterList("users.id", userId, 1, 1);
        parameterList.setOrder("userRoles.id");//no need
        List<UserRoleChannel> userRoleChannelList = dataService.getListOption(UserRoleChannel.class, parameterList);
        if (userRoleChannelList.isEmpty()) {
            return null;
        } else {
            return userRoleChannelList.get(0).getUserRoles();
        }
    }

    @Override
    public List<UserRoleChannel> getListUserRoleChannel(int userId, DataService dataService) {
        String hql = "FROM UserRoleChannel"
                + " WHERE deletedUser = 0"
                + " AND users.id = :userId";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public MsalesCompany loginMasterAdmin(String username, String password, DataService dataService) {
        dataService.setBranch(DatabaseType.DATABASE_TYPE_COMPANY.getBranch());;
        String hql = "FROM MsalesCompany"
                + " WHERE deletedUser = 0"
                + " AND masterAdmin = TRUE"
                + " AND username = :username"
                + " AND password = :password";
        List<MsalesParameter> parameters = MsalesParameter.createList("username",username);
        parameters.add(MsalesParameter.create("password", password));
        
        List<MsalesCompany> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);        
        if(list.size()>0){
            return list.get(0);
        }
        else{
            return null;
        }
    }

}
