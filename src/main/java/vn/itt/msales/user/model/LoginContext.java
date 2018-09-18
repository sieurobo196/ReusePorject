/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.user.model;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import vn.itt.msales.app.service.LoginService;
import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.entity.Session;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesMapContent;
import vn.itt.msales.services.DataService;

/**
 *
 * @author vtm
 */
public class LoginContext {

    public static void logout(HttpServletRequest request, HttpServletResponse response, DataService dataService, LoginService loginService) {
        request.setAttribute("userPrincipal", null);
        String token;
        int branch;
        if (request.getSession().getAttribute("userLogin") != null) {
            MsalesLoginUserInf loginUser = (MsalesLoginUserInf) request.getSession().getAttribute("userLogin");
            token = loginUser.getToken();
            branch = loginUser.getBranch();

            request.getSession().invalidate();
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals("userLogin")) {
                        String tokenBranch = cookie.getValue();
                        try {
                            token = tokenBranch.split("-")[0];
                            branch = Integer.parseInt(tokenBranch.split("-")[1]);
                        } catch (Exception ex) {
                        }
                        //huy coookie
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                        break;
                    }
                }
            }

            //goi API logout
            if (token != null && branch != 0) {
                //MsalesMapContent msalesMapContent = new MsalesMapContent();
                //msalesMapContent.add("token", token);
                //msalesMapContent.add("branch", branch);
                Session session = new Session();
                session.setToken(token);

                request.setAttribute(MsalesConstants.SESSION, session);
                dataService.setBranch(branch);
                loginService.logout(session, dataService, request);
            }
        }
    }

    public static String redirectAdminPage(){
        return "redirect:/admin/company";
    }
    

    public static String redirectLogin() {
        return "redirect:/WebLogin";
    }

    public static ModelAndView redirectModelLogin() {
        return new ModelAndView("redirect:/");
    }

    public static Map<String, Object> returnEmptyHashMap() {
        return new HashMap<>();
    }
    

    public static String redirectHome() {
        return "redirect:/";
    }

    public static String returnString() {
        return "NOT LOGIN";
    }

    public static String notAccess() {
        return "notAccess";
    }

    public static ModelAndView notAccessModelAndView() {
        return new ModelAndView("notAccess");
    }

    public static void sendRedirectHome(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/WebLogin");
    }

    public static int isLogin(HttpServletRequest request, DataService dataService) {
        String token = null;
        int branch = 0;
        boolean isAdminMaster = false;

        //if(request.getRequestURI().startsWith(request.getContextPath() +"/" + "WebLogin"));
        //request.setAttribute("preURL", request.getRequestURI());
        if (request.getSession().getAttribute("userLogin") != null) {
            try {
                MsalesLoginUserInf msalesLoginUserInf = (MsalesLoginUserInf) request.getSession().getAttribute("userLogin");
                token = msalesLoginUserInf.getToken();
                branch = msalesLoginUserInf.getBranch();
                isAdminMaster = msalesLoginUserInf.isIsAdminMaster();
            } catch (Exception ex) {
                return -1;//0 => not login
            }
        } else if (request.getCookies() != null) {
            //get token from cookie
            //check API to get UserInfo
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("userLogin")) {
                    String tokenBranch = cookie.getValue();
                    try {
                        token = tokenBranch.split("-")[0];
                        branch = Integer.parseInt(tokenBranch.split("-")[1]);
                        cookie.setMaxAge(24 * 60 * 60);
                    } catch (Exception ex) {
                        return -1;//not login
                    }
                }
            }
        } else {
            return -1;//not login
        }
        if(isAdminMaster){
            return 2;
        }
        
        if (token != null && branch != 0) {
            MsalesMapContent msalesMapContent = new MsalesMapContent();
            msalesMapContent.add("token", token);
            msalesMapContent.add("branch", branch);

//            request.setAttribute(MsalesConstants.CONTENTS, msalesMapContent.toString());
            dataService.setBranch(branch);
//            String responseString = userController.checkLoginAdmin(request);
//            MsalesResponse msalesResponse = MsalesResponse.createMsalesResponseFromString(responseString);
//            MsalesLoginUserInf loginUserInf = msalesResponse.parseContents(MsalesLoginUserInf.class);
//            MsalesLoginUserInf loginUserInf = checkLogin(token, userController);
            LoginContext loginContext = new LoginContext();
            MsalesLoginUserInf loginUserInf = loginContext.checkLogin(dataService, token);
            if (loginUserInf != null) {
                //da dang nhap
                loginUserInf.setBranch(branch);
                request.getSession().setAttribute("userLogin", loginUserInf);
                request.setAttribute("userPrincipal", loginUserInf);
                request.setAttribute("userRole", loginUserInf.getUserRoleCode());
                return URLManager.checkRole(loginUserInf.getUserRoleCode(),loginUserInf.getPackageService(), request) ? 1 : 0;//1 => 1=> duoc phep truy cap,0=>ko co quyen tuy cap
            } else {
                //chua dang nhap
                //xoa het session
                request.getSession().invalidate();
                return -1;//not login
            }
        } else {
            return -1;//not login
        }
    }

    private MsalesLoginUserInf checkLogin(DataService dataService, String token) {
        MsalesLoginUserInf loginInf = null;
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("token", token);
        parameterList.setOrder("lastAccessedTime", "DESC");

        List<Session> sessionList = dataService.getListOption(Session.class, parameterList);
        if (!sessionList.isEmpty()) {
            String hql = "FROM UserRoleChannel"
                    + " WHERE users.id = :userId"
                    + " AND userRoles.code NOT IN ('USER_ROLE_SALESMAN')"
                    + " AND deletedUser = 0"
                    + " AND users.deletedUser = 0"
                    + " AND users.isActive = 1"
                    + " AND users.statuss.value = 1"
                    + " AND userRoles.deletedUser = 0"
                    + " AND channels.deletedUser = 0";
            List<MsalesParameter> parameters = MsalesParameter.createList("userId", sessionList.get(0).getUserId());
            List<UserRoleChannel> userRoleChannel = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
            if (!userRoleChannel.isEmpty()) {
                User userFromDB = userRoleChannel.get(0).getUsers();
                dataService.updateRow(userFromDB);
                // get user login
                loginInf = new MsalesLoginUserInf(userFromDB, userRoleChannel.get(0).getUserRoles(), sessionList.get(0).getToken(), userRoleChannel);
                //update table session
                sessionList.get(0).setLastAccessedTime(new Date());
                dataService.updateRow(sessionList.get(0));
            }
        }
        return loginInf;
    }

    public static MsalesLoginUserInf getLogin(HttpServletRequest request) {
        return (MsalesLoginUserInf) request.getSession().getAttribute("userLogin");
    }
}
