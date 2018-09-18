/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.user.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.config.services.EquipmentServices;
import vn.itt.msales.database.dbrouting.DatabaseType;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.Equipment;
import vn.itt.msales.entity.MsalesCompany;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.searchObject.SearchObject;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.services.MsalesUserServices;

/**
 *
 * @author Khai
 */
@Controller
@RequestMapping(value = "admin")
public class WebAdminMasterController {

    @Autowired
    private DataService dataService;

    @Autowired
    private ServiceFilter serviceFilter;

    @Autowired
    private MsalesUserServices userServices;

    @Autowired
    private EquipmentServices equipmentServices;

    @RequestMapping(value = "/company")
    public String adminPage(HttpServletRequest request, Model uiModel,
            @ModelAttribute(value = "searchObject") SearchObject searchObject,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == 1) {
            return LoginContext.redirectHome();
        } else if (login != 2) {
            return LoginContext.redirectLogin();
        }

        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPage = 1;

        List<OptionItem> branchList = userServices.getListBranch();
        uiModel.addAttribute("branchList", branchList);

        List<OptionItem> packageList = userServices.getListPackage(true);
        uiModel.addAttribute("packageList", packageList);

        List<Company> results = userServices.searchCompany(searchObject, dataService);
        Collections.sort(results);

        maxPage = results.size() / size;
        if (results.size() % size > 0 || maxPage == 0) {
            maxPage++;
        }
        if (page > maxPage) {
            page = maxPage;
        }
        //Phan trang here
        if (results.size() > 0) {
            if (page * size > results.size()) {
                uiModel.addAttribute("companyList", results.subList((page - 1) * size, results.size()));
            } else {
                uiModel.addAttribute("companyList", results.subList((page - 1) * size, page * size));
            }
        }

        uiModel.addAttribute("maxPages", maxPage);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("now", new Date());
        return "adminPage";
    }

    @RequestMapping(value = "company/edit/{branch}/{id}", method = RequestMethod.GET)
    public String edit(HttpServletRequest request,
            Model uiModel,
            @PathVariable(value = "branch") int branch,
            @PathVariable(value = "id") int id) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == 1) {
            return LoginContext.redirectHome();
        } else if (login != 2) {
            return LoginContext.redirectLogin();
        }

        dataService.setBranch(branch);
        Company company = dataService.getRowById(id, Company.class);
        if (company == null) {
            return "noData";
        }

        initData(uiModel);
        uiModel.addAttribute("company", company);
        return "adminPageEdit";
    }

    @RequestMapping(value = "company/edit/{branch}/{id}", method = RequestMethod.POST)
    public String update(HttpServletRequest request,
            Model uiModel, @ModelAttribute(value = "company") Company company,
            @PathVariable(value = "branch") int branch,
            @PathVariable(value = "id") int id) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == 1) {
            return LoginContext.redirectHome();
        } else if (login != 2) {
            return LoginContext.redirectLogin();
        }

        //validate data
        List<String> errorList = new ArrayList<>();
        if (checkNullOrEmpty(company.getName())) {
            errorList.add("name");
        }
        if (checkNullOrEmpty(company.getPackageService())) {
            errorList.add("packageService");
        } else if (company.getPackageService() < 1 || company.getPackageService() > 3) {
            errorList.add("packageServiceType");
        }
        if (checkNullOrEmpty(company.getEquipmentMax())) {
            errorList.add("equipemtMax");
        } else if (company.getEquipmentMax() < 0) {
            errorList.add("equipmentMaxType");
        }
        if (checkNullOrEmpty(company.getExpireTime())) {
            errorList.add("expireTime");
        }
        if (checkNullOrEmpty(company.getTel())) {
            errorList.add("tel");
        } else if (!Pattern.matches(MsalesValidator.PHONE_NUMBER, company.getTel())) {
            errorList.add("telType");
        }
        if (checkNullOrEmpty(company.getEmail())) {
            errorList.add("email");
        } else if (!Pattern.matches(MsalesValidator.EMAIL_PATTERN, company.getEmail())) {
            errorList.add("emailType");
        }
        if (checkNullOrEmpty(company.getLocations().getId())) {
            errorList.add("locationId");
        } else if (company.getLocations().getId() < 1) {
            errorList.add("locationIdType");
        }

        if (checkNullOrEmpty(company.getLat())) {
            errorList.add("lat");
        }
        if (checkNullOrEmpty(company.getLng())) {
            errorList.add("lng");
        }

        if (errorList.isEmpty()) {
            //update
            dataService.setBranch(branch);
            Company rootCompany = dataService.getRowById(id, Company.class);
            if (rootCompany == null) {
                return "noData";
            } else {
                company.setCode(rootCompany.getCode());
                company.setStatuss(rootCompany.getStatuss());
                company.setFax(rootCompany.getFax());
                company.setLogoPath(rootCompany.getLogoPath());
                company.setNote(rootCompany.getNote());
                company.setBgColor(rootCompany.getBgColor());
                company.setTextColor(rootCompany.getTextColor());
                company.setButtonBgColor(rootCompany.getButtonBgColor());
                company.setButtonBgColorOver(rootCompany.getButtonBgColorOver());
                company.setTopBarBGColor(rootCompany.getTopBarBGColor());
                company.setIsSendmailOrderList(rootCompany.getIsSendmailOrderList());
                company.setEmployeeAmount(rootCompany.getEmployeeAmount());
                company.setEmployeeSaleAmount(rootCompany.getEmployeeSaleAmount());
                company.setExpireTime(rootCompany.getExpireTime());
                company.setIsTemplate(rootCompany.isIsTemplate());
                company.setIsRegister(rootCompany.getIsRegister());
                company.setCreatedAt(rootCompany.getCreatedAt());
                company.setCreatedUser(rootCompany.getCreatedUser());
                company.setUpdatedAt(new Date());
                company.setUpdatedUser(-1);//admin
                company.setDeletedAt(rootCompany.getDeletedAt());
                company.setDeletedUser(rootCompany.getDeletedUser());
                try {
                    dataService.updateRow(company);

                    dataService.setBranch(DatabaseType.DATABASE_TYPE_COMPANY.getBranch());
                    MsalesCompany msalesCompany = userServices.getMsalesCompany(company.getCode(), branch, dataService);
                    if (msalesCompany == null) {
                        return "noData";
                    } else {
                        msalesCompany.setCompanyName(company.getName());
                        dataService.updateRow(msalesCompany);
                    }

                    dataService.setBranch(branch);
                    uiModel.addAttribute("success", true);
                } catch (Exception ex) {
                    uiModel.addAttribute("success", false);
                }
            }
        } else {
            uiModel.addAttribute("errorList", errorList);
            uiModel.addAttribute("errorData", false);
        }

        initData(uiModel);
        return "adminPageEdit";
    }

    @RequestMapping(value = "/company/delete/{branch}/{id}")
    @ResponseBody
    public Map delete(HttpServletRequest request,
            @PathVariable(value = "branch") int branch,
            @PathVariable(value = "id") int id) {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 2) {
            return LoginContext.returnEmptyHashMap();
        }
        HashMap<String, String> data = new HashMap<>();
        dataService.setBranch(branch);
        Company company = dataService.getRowById(id, Company.class);
        if (company == null) {
            data.put("success", "Lỗi dữ liệu.");
        } else {
            company.setBranch(branch);
            try {
                if (userServices.deleteCompany(company, dataService)) {
                    data.put("success", "OK");
                } else {
                    data.put("success", "FALSE");
                }
            } catch (Exception ex) {
                data.put("success", "FALSE");
            }
        }
        return data;
    }

    @RequestMapping(value = "/equipment")
    public String adminEquimentPage(HttpServletRequest request, Model uiModel,
            @ModelAttribute(value = "searchObject") SearchObject searchObject,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == 1) {
            return LoginContext.redirectHome();
        } else if (login != 2) {
            return LoginContext.redirectLogin();
        }

        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPage = 1;

        List<OptionItem> branchList = userServices.getListBranch();
        uiModel.addAttribute("branchList", branchList);

        List<Equipment> results = userServices.searchEquipment(searchObject, dataService);
        Collections.sort(results);

        maxPage = results.size() / size;
        if (results.size() % size > 0 || maxPage == 0) {
            maxPage++;
        }
        if (page > maxPage) {
            page = maxPage;
        }
        //Phan trang here
        if (results.size() > 0) {
            if (page * size > results.size()) {
                uiModel.addAttribute("equipmentList", results.subList((page - 1) * size, results.size()));
            } else {
                uiModel.addAttribute("equipmentList", results.subList((page - 1) * size, page * size));
            }
        }

        uiModel.addAttribute("maxPages", maxPage);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        return "adminEquipmentPage";
    }

    @RequestMapping(value = "/equipment/company/{branch}/{id}")
    public String adminEquimentByCompany(HttpServletRequest request, Model uiModel,
            @ModelAttribute(value = "searchObject") SearchObject searchObject,
            @PathVariable(value = "branch") int branch,
            @PathVariable(value = "id") int id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == 1) {
            return LoginContext.redirectHome();
        } else if (login != 2) {
            return LoginContext.redirectLogin();
        }

        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPage = 1;

        dataService.setBranch(branch);
        Company company = dataService.getRowById(id, Company.class);
        if (company != null) {
            List<Equipment> results = userServices.getListEquipmentByCompanyId(id, searchObject.getSearchText(), dataService);
            for (Equipment equipment : results) {
                equipment.setBranch(branch);
            }
            Collections.sort(results);

            maxPage = results.size() / size;
            if (results.size() % size > 0 || maxPage == 0) {
                maxPage++;
            }
            if (page > maxPage) {
                page = maxPage;
            }
            //Phan trang here
            if (results.size() > 0) {
                if (page * size > results.size()) {
                    uiModel.addAttribute("equipmentList", results.subList((page - 1) * size, results.size()));
                } else {
                    uiModel.addAttribute("equipmentList", results.subList((page - 1) * size, page * size));
                }
            }

            uiModel.addAttribute("maxPages", maxPage);
            uiModel.addAttribute("page", page);
            uiModel.addAttribute("size", size);
            uiModel.addAttribute("branch", branch);
            uiModel.addAttribute("id", id);
            uiModel.addAttribute("companyName", company.getName());
            return "masterAdminEquipmentCompany";
        } else {
            return "noData";
        }
    }

    @RequestMapping(value = "/equipment/delete/{branch}/{id}")
    @ResponseBody
    public Map deleteEquipment(HttpServletRequest request,
            @PathVariable(value = "branch") int branch,
            @PathVariable(value = "id") int id) {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 2) {
            return LoginContext.returnEmptyHashMap();
        }
        HashMap<String, String> data = new HashMap<>();
        dataService.setBranch(branch);
        Equipment equipment = dataService.getRowById(id, Equipment.class);
        if (equipment == null) {
            data.put("success", "Lỗi dữ liệu.");
        } else {
            equipment.setBranch(branch);
            try {
                if (userServices.deleteEquipment(equipment, dataService)) {
                    data.put("success", "OK");
                } else {
                    data.put("success", "FALSE");
                }
            } catch (Exception ex) {
                data.put("success", "FALSE");
            }
        }
        return data;
    }

    @RequestMapping(value = "equipment/edit/{branch}/{id}", method = RequestMethod.GET)
    public String editEquipment(HttpServletRequest request,
            Model uiModel,
            @RequestParam(value = "flag", required = false) Boolean flag,
            @PathVariable(value = "branch") int branch,
            @PathVariable(value = "id") int id) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == 1) {
            return LoginContext.redirectHome();
        } else if (login != 2) {
            return LoginContext.redirectLogin();
        }

        dataService.setBranch(branch);
        Equipment equipment = dataService.getRowById(id, Equipment.class);
        if (equipment == null) {
            return "noData";
        }

        uiModel.addAttribute("equipment", equipment);
        User user = dataService.getRowById(equipment.getUsers().getId(), User.class);
        List<User> users = equipmentServices.listCbEquipmentUser(equipment.getCompanys().getId());
        users.add(user);
        uiModel.addAttribute("userList", users);

        if (flag != null && flag) {
            uiModel.addAttribute("flag", true);
            uiModel.addAttribute("branch", branch);
            uiModel.addAttribute("id", equipment.getCompanys().getId());
        }

        return "adminPageEquipmentEdit";
    }

    @RequestMapping(value = "equipment/edit/{branch}/{id}", method = RequestMethod.POST)
    public String updateEquipment(HttpServletRequest request,
            Model uiModel,
            @ModelAttribute(value = "equipment") Equipment equipment,
            BindingResult bindingResult, RedirectAttributes redirectAttributes,
            @RequestParam(value = "flag", required = false) Boolean flag,
            @PathVariable(value = "branch") int branch,
            @PathVariable(value = "id") int id) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == 1) {
            return LoginContext.redirectHome();
        } else if (login != 2) {
            return LoginContext.redirectLogin();
        }

        dataService.setBranch(branch);
        Equipment rootEquipment = dataService.getRowById(id, Equipment.class);
        if (rootEquipment == null) {
            return "noData";
        }

        validateInputData(equipment, bindingResult);

        if (!bindingResult.hasErrors()) {
            if (equipment.getId() != null) {
                //re-set data
                rootEquipment.setImei(equipment.getImei());
                rootEquipment.setSubscriberId(equipment.getSubscriberId());
                rootEquipment.setTelNo(equipment.getTelNo());
                rootEquipment.setName(equipment.getName());
                rootEquipment.setVersion(equipment.getVersion());
                rootEquipment.setUserId(equipment.getUsers().getId());
                rootEquipment.setIsActive(equipment.getIsActive());

                rootEquipment.setUpdatedAt(new Date());
                rootEquipment.setUpdatedUser(-1);//admin
                int ret = dataService.updateRow(rootEquipment);
                if (ret > 0) {
                    redirectAttributes.addFlashAttribute("update", true);
                    return "redirect:/admin/equipment/edit/" + branch + "/" + ret;
                }
            }
        }

        uiModel.addAttribute("update", false);

        User user = dataService.getRowById(rootEquipment.getUsers().getId(), User.class);
        List<User> users = equipmentServices.listCbEquipmentUser(rootEquipment.getCompanys().getId());
        users.add(user);
        uiModel.addAttribute("userList", users);

        if (flag != null && flag) {
            uiModel.addAttribute("branch", branch);
            uiModel.addAttribute("id", id);
        }
        return "adminPageEquipmentEdit";
    }

    private void validateInputData(Equipment equipment, BindingResult bindingResult) {
        String imei = equipment.getImei();
        if (imei == null || imei.trim().isEmpty()) {
            bindingResult.rejectValue("imei", "equipment_imei", "empty_error_code");

        }
        String sub = equipment.getSubscriberId();
        if (sub == null || sub.trim().isEmpty()) {
            bindingResult.rejectValue("subscriberId", "equipment_sub", "empty_error_code");
        }
        String version = equipment.getVersion();
        if (version == null || version.trim().isEmpty()) {
            bindingResult.rejectValue("version", "equipment_version", "empty_error_code");
        }
        if (equipment.getUsers().getId() == null || equipment.getUsers().getId() == 0) {
            bindingResult.rejectValue("users.id", "equipment_user", "empty_error_code");
        }
    }

    private boolean checkNullOrEmpty(Object o) {
        return o == null || o.toString().trim().isEmpty();
    }

    private void initData(Model uiModel) {
        List<OptionItem> packageList = userServices.getListPackage(false);
        uiModel.addAttribute("packageList", packageList);

        List<OptionItem> locationList = OptionItem.NoOptionList(0, "-- Tỉnh/Thành phố --");
        locationList.addAll(serviceFilter.getCbListLocationByType(1, dataService));
        locationList.remove(0);
        uiModel.addAttribute("locationList", locationList);
    }

    @InitBinder
    public void dataBinding(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
}
