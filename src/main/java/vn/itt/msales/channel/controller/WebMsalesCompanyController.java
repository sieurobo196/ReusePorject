/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.channel.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.controller.MsalesUserController;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author vtm_2
 */
@Controller
public class WebMsalesCompanyController {

    @Autowired
    private DataService dataService;

    @RequestMapping(value = "/company/detail")
    public String viewCompany(HttpServletRequest request, Company company, Model uiModel) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        company = dataService.getRowById(loginUserInf.getCompanyId(), Company.class);

        return companyDetail(company, request, uiModel);
    }

    @RequestMapping(value = "/company/detail", params = {"update"})
    public String updateCompany(HttpServletRequest request,  Company company, Model uiModel, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        validateInput(company, bindingResult, true);
        if (!bindingResult.hasErrors()) {
            Company company2 = dataService.getRowById(loginUserInf.getCompanyId(), Company.class);
            if ( company.getLogoPath()==(null)) {
                company.setLogoPath(company2.getLogoPath());
            } else {
                company.setLogoPath(loginUserInf.getBranch() + "/" + loginUserInf.getCompanyId() + "/" + company.getLogoPath());
            }
            Company company1 = new Company();
            company1.setId(loginUserInf.getCompanyId());
            company1.setCode(company.getCode());
            company1.setName(company.getName());
            company1.setAddress(company.getAddress());
            company1.setContactPersonName(company.getContactPersonName());
            company1.setEmail(company.getEmail());
            company1.setFax(company.getFax());
            company1.setTel(company.getTel());
            company1.setStatusId(1);
            company1.setLat(company.getLat());
            company1.setLng(company.getLng());
            company1.setLocationId(company.getLocations().getId());
            company1.setLogoPath(company.getLogoPath());
            company1.setIsSendmailOrderList(company.getIsSendmailOrderList());
            company1.setUpdatedUser(loginUserInf.getId());

            int ret = dataService.updateSync(company1);
            if (ret > 0) {
                redirectAttributes.addFlashAttribute("update", true);
                return "redirect:/company/detail";
            } else {
                redirectAttributes.addFlashAttribute("update", false);
            }
        } else {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError fieldError : errors) {
                bindingResult.rejectValue(fieldError.getField(),
                        fieldError.getDefaultMessage());
            }
        }

        return companyDetail(company, request, uiModel);
    }

    private String companyDetail(Company company, HttpServletRequest request, Model uiModel) {
        List<Location> locations = dataService.getListOption(Location.class, new ParameterList("locationType", 1));
        uiModel.addAttribute("location", locations);
        uiModel.addAttribute("company", company);
        return "infoCompany";
    }

    private void validateInput(Company company, BindingResult bindingResult, boolean isAdd) {
        if (company.getName() == null || company.getName().trim().isEmpty()) {

            bindingResult.rejectValue("name", "name_company", "empty_error_code");
        }
        if (company.getLocations().getId() == 0 || company.getLocations().getId() == null) {
            bindingResult.rejectValue("locations.id", "location_company", "empty_error_code");
        }
    }
}
