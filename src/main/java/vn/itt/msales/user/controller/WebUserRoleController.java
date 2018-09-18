/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.user.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.itt.msales.entity.GoodsCategory;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author vtm_2
 */
@Controller
public class WebUserRoleController {
    
    @Autowired
    private DataService dataService;
    @Autowired
    MsalesUserController userController;
    
    @RequestMapping(value = "/userRole/list")
    public String listCategory(Model uiModel, HttpServletRequest request, GoodsCategory goodsCategory,
            @ModelAttribute(value = "userRoleForm") UserRole categoryFilter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        int companyId = loginUserInf.getCompanyId();
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;
        
        MsalesResults<UserRole> list = dataService.getListOption(UserRole.class, new ParameterList(), true);
        List<UserRole> userRoleList = list.getContentList();
        int count = Integer.parseInt(list.getCount().toString());
        if (count % size != 0) {
            maxPages = count / size + 1;
        } else {
            maxPages = count / size;
        }
        if (page > maxPages) {
            page = maxPages;
        }
        
        uiModel.addAttribute("userRoleList", userRoleList);
        return "userRoleList";
    }
}
