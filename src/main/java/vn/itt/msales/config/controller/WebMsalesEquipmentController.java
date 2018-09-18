/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.config.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.common.GPSUtils;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.config.services.EquipmentServices;
import vn.itt.msales.entity.CompanyConstant;
import vn.itt.msales.entity.Equipment;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;
import vn.itt.msales.user.model.URLManager;

/**
 *
 * @author vtm_2
 */
@Controller
public class WebMsalesEquipmentController {

    @Autowired
    private DataService dataService;
    @Autowired
    private EquipmentServices equimentServices;

    @Autowired
    private ServiceFilter serviceFilter;

    @RequestMapping(value = "/device/list")
    public String listDevices(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            HttpServletRequest request, Model uiModel,
            @ModelAttribute("listForm") Filter filter) throws JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        uiModel.addAttribute("only", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY", "USER_ROLE_ADMIN_CHANNEL"}));
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        int companyId = loginUserInf.getCompanyId();
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;
        List<OptionItem> employList = filter.processFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());
        MsalesResults<UserRole> results = dataService.getListOption(UserRole.class, new ParameterList(), true);
        List<UserRole> userRoles = results.getContentList();

        MsalesPageRequest pageRequest = new MsalesPageRequest(page, size);
        MsalesResults<HashMap> msalesResults = equimentServices.searchEquipment(filter, companyId, pageRequest);
        List<HashMap> list = msalesResults.getContentList();
        int count1 = Integer.parseInt(msalesResults.getCount().toString());

        if (count1 % size != 0) {
            maxPages = count1 / size + 1;
        } else {
            maxPages = count1 / size;
        }
        if (page > maxPages) {
            page = maxPages;
        }

        uiModel.addAttribute("equipmentList", list);
        uiModel.addAttribute("userRoleId", userRoles);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);
        return "deviceList";

    }

    @RequestMapping(value = "/device/detail/{id}")
    public String deviceDetail(@PathVariable("id") int id,
            HttpServletRequest request, Model uiModel, Equipment equipment) throws ParseException, JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        uiModel.addAttribute("readonly", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY", "USER_ROLE_ADMIN_CHANNEL"}));

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        equipment = dataService.getRowById(id, Equipment.class);
        uiModel.addAttribute("isView", true);
        User user = dataService.getRowById(equipment.getUsers().getId(), User.class);
        List<User> users = equimentServices.listCbEquipmentUser(loginUserInf.getCompanyId());
        users.add(user);
        uiModel.addAttribute("userList", users);
        uiModel.addAttribute("equipment", equipment);
        return "deviceDetail";

    }

    //@RequestMapping(value = "/device/add")
    public String addEquipment(HttpServletRequest request, Model uiModel) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        Equipment equipment = new Equipment();

        List<User> users = equimentServices.listCbEquipmentUser(loginUserInf.getCompanyId());

        uiModel.addAttribute("userList", users);
        uiModel.addAttribute("equipment", equipment);
        return "deviceDetail";

    }

    //@RequestMapping(value = "/device/create")
    public String createEquipment(HttpServletRequest request, Model uiModel, Equipment equipment,
            RedirectAttributes redirectAttributes, BindingResult bindingResult)
            throws JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        validateInputData(equipment, bindingResult, request);
        if (!bindingResult.hasErrors()) {
            boolean error = false;
            if (equimentServices.checkEquipmentMax(loginUserInf.getCompanyId(), equipment.getId(), loginUserInf.getEquipmentMax(), dataService)) {
                error = true;
                uiModel.addAttribute("overEquipment", true);
                uiModel.addAttribute("equipmentMax", loginUserInf.getEquipmentMax());
            }

            if (!error) {
                equipment.setCompanyId(loginUserInf.getCompanyId());
                equipment.setCreatedUser(loginUserInf.getId());
                equipment.setActiveDate(new Date());

                int ret = dataService.insertRow(equipment);
                if (ret > 0) {
                    redirectAttributes.addFlashAttribute("create", true);
                    return "redirect:/device/detail/" + ret;
                } else {
                    redirectAttributes.addFlashAttribute("create", false);
                }
            }
        }
        List<User> users = equimentServices.listCbEquipmentUser(loginUserInf.getCompanyId());

        uiModel.addAttribute("userList", users);
        uiModel.addAttribute("equipment", equipment);
        return "deviceDetail";
    }

    @RequestMapping(value = "/device/update")
    public String updateEquipment(HttpServletRequest request, Model uiModel, Equipment equipment,
            BindingResult bindingResult, RedirectAttributes redirectAttributes)
            throws JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        validateInputData(equipment, bindingResult, request);

        if (!bindingResult.hasErrors()) {
            if (equipment.getId() != null) {
                Equipment equipment1 = dataService.getRowById(equipment.getId(), Equipment.class);
                equipment.setCompanyId(loginUserInf.getCompanyId());
                equipment.setUpdatedUser(loginUserInf.getId());
                equipment.setActiveDate(equipment1.getActiveDate());

                int ret = dataService.updateSync(equipment);
                if (ret > 0) {
                    redirectAttributes.addFlashAttribute("update", true);
                    return "redirect:/device/detail/" + ret;
                } else {
                    redirectAttributes.addFlashAttribute("update", false);
                }
            }
        }
        uiModel.addAttribute("isView", true);
        List<User> users = equimentServices.listCbEquipmentUser(loginUserInf.getCompanyId());
        uiModel.addAttribute("userList", users);
        uiModel.addAttribute("equipment", equipment);
        return "deviceDetail";
    }

    @RequestMapping(value = "/device/delete/{id}")
    public String deleteEquipment(Model uiModel, HttpServletRequest request, @PathVariable("id") int id, Equipment equipment) throws JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        equipment.setDeletedUser(loginUserInf.getId());
        int ret = dataService.deleteSynch(equipment);
        if (ret > 0) {
            return "redirect:/device/list";
        } else {

            return "providerError";
        }

    }

    @RequestMapping(value = "/device/lock/{id}")
    public String lockEquipment(Model uiModel, HttpServletRequest request, @PathVariable("id") int id) throws JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        Equipment equipment = dataService.getRowById(id, Equipment.class);
        equipment.setUpdatedUser(loginUserInf.getId());
        equipment.setUpdatedAt(new Date());
        equipment.setIsActive(false);

        int ret = dataService.updateRow(equipment);
        if (ret > 0) {
            return "redirect:/device/list";
        } else {
            return "providerError";
        }
    }

    private void validateInputData(Equipment equipment, BindingResult bindingResult,
            HttpServletRequest request) {
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

    /**
     * thông số lộ trình
     */
    @RequestMapping(value = "/device/GPSsetting")
    public String GPSsetting(HttpServletRequest request, Model uiModel, CompanyConstant companyConstant) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        //diable min distance with company package 1
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        
        if(loginUserInf.getPackageService()==1){
            uiModel.addAttribute("packageDisable", true);
        }
        
        List<CompanyConstant> companyConstants = dataService.getListOption(CompanyConstant.class, new ParameterList("companys.id", loginUserInf.getCompanyId()));
        if (companyConstants.size() != 0) {
            for (CompanyConstant companyConstant1 : companyConstants) {
                companyConstant.setDistance(companyConstant1.getDistance());
                companyConstant.setEveningFrom(companyConstant1.getEveningFrom());
                companyConstant.setEveningTo(companyConstant1.getEveningTo());
                companyConstant.setMinDistance(companyConstant1.getMinDistance());
                companyConstant.setMorningFrom(companyConstant1.getMorningFrom());
                companyConstant.setMorningTo(companyConstant1.getMorningTo());
                companyConstant.setPeriodGetPosition(companyConstant1.getPeriodGetPosition());
                companyConstant.setPeriodSendPosition(companyConstant1.getPeriodSendPosition());
                companyConstant.setTimesheetFrom(companyConstant1.getTimesheetFrom());

                companyConstant.setHourIn(companyConstant1.getTimesheetFrom().getHours());
                companyConstant.setMinutesIn(companyConstant1.getTimesheetFrom().getMinutes());

                companyConstant.setHourMorEnd(companyConstant1.getMorningTo().getHours());
                companyConstant.setMinuteMorEnd(companyConstant1.getMorningTo().getMinutes());

                companyConstant.setHourMorStart(companyConstant1.getMorningFrom().getHours());
                companyConstant.setMinuteMorStart(companyConstant1.getMorningFrom().getMinutes());

                companyConstant.setHourEvenStart(companyConstant1.getEveningFrom().getHours());
                companyConstant.setMinuteEvenStart(companyConstant1.getEveningFrom().getMinutes());

                companyConstant.setHourEvenEnd(companyConstant1.getEveningTo().getHours());
                companyConstant.setMinuteEvenEnd(companyConstant1.getEveningTo().getMinutes());
            }
        } else {
            companyConstant = new CompanyConstant();
        }
        return GPSsetting(request, companyConstant, uiModel);
    }

    @RequestMapping(value = "/device/GPSsetting", params = {"update"})
    public String updateGPSSetting(HttpServletRequest request, Model uiModel, @ModelAttribute("companyConstant") CompanyConstant companyConstant, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        if(loginUserInf.getPackageService()==1){
            uiModel.addAttribute("packageDisable", true);
        }
        
        List<CompanyConstant> companyConstants = dataService.getListOption(CompanyConstant.class, new ParameterList("companys.id", loginUserInf.getCompanyId()));
        if (companyConstants.size() != 0) {
            for (CompanyConstant companyConstant1 : companyConstants) {
                validateInputGPS(companyConstant, bindingResult, true);
                if (!bindingResult.hasErrors()) {
                    companyConstant.setId(companyConstant1.getId());
                    companyConstant.setCompanyId(loginUserInf.getCompanyId());
                    companyConstant.setUpdatedUser(loginUserInf.getId());
                    String s = "";
                    s += companyConstant.getHourIn().toString();
                    s += ":" + companyConstant.getMinutesIn();
                    s += ":00";
                    Time timeIn = Time.valueOf(s);
                    companyConstant.setTimesheetFrom(timeIn);

                    String s1 = "";
                    s1 += companyConstant.getHourMorStart().toString();
                    s1 += ":" + companyConstant.getMinuteMorStart();
                    s1 += ":00";
                    Time timeMorStart = Time.valueOf(s1);
                    companyConstant.setMorningFrom(timeMorStart);

                    String s2 = "";
                    s2 += companyConstant.getHourMorEnd().toString();
                    s2 += ":" + companyConstant.getMinuteMorEnd();
                    s2 += ":00";
                    Time timeMorEnd = Time.valueOf(s2);
                    companyConstant.setMorningTo(timeMorEnd);

                    String s3 = "";
                    s3 += companyConstant.getHourEvenStart().toString();
                    s3 += ":" + companyConstant.getMinuteEvenStart();
                    s3 += ":00";
                    Time timeEvenStart = Time.valueOf(s3);
                    companyConstant.setEveningFrom(timeEvenStart);

                    String s4 = "";
                    s4 += companyConstant.getHourEvenEnd().toString();
                    s4 += ":" + companyConstant.getMinuteEvenEnd();
                    s4 += ":00";
                    Time timeEvenEnd = Time.valueOf(s4);
                    companyConstant.setEveningTo(timeEvenEnd);

                    int ret = dataService.updateSync(companyConstant);
                    if (ret > 0) {
                        redirectAttributes.addFlashAttribute("update", true);
                        return "redirect:/device/GPSsetting";
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
            }
        } else {
            validateInputGPS(companyConstant, bindingResult, true);
            if (!bindingResult.hasErrors()) {
                companyConstant.setCompanyId(loginUserInf.getCompanyId());
                companyConstant.setCreatedUser(loginUserInf.getId());
                String s = "";
                s += companyConstant.getHourIn().toString();
                s += ":" + companyConstant.getMinutesIn();
                s += ":00";
                Time timeIn = Time.valueOf(s);
                companyConstant.setTimesheetFrom(timeIn);

                String s1 = "";
                s1 += companyConstant.getHourMorStart().toString();
                s1 += ":" + companyConstant.getMinuteMorStart();
                s1 += ":00";
                Time timeMorStart = Time.valueOf(s1);
                companyConstant.setMorningFrom(timeMorStart);

                String s2 = "";
                s2 += companyConstant.getHourMorEnd().toString();
                s2 += ":" + companyConstant.getMinuteMorEnd();
                s2 += ":00";
                Time timeMorEnd = Time.valueOf(s2);
                companyConstant.setMorningTo(timeMorEnd);

                String s3 = "";
                s3 += companyConstant.getHourEvenStart().toString();
                s3 += ":" + companyConstant.getMinuteEvenStart();
                s3 += ":00";
                Time timeEvenStart = Time.valueOf(s3);
                companyConstant.setEveningFrom(timeEvenStart);

                String s4 = "";
                s4 += companyConstant.getHourEvenEnd().toString();
                s4 += ":" + companyConstant.getMinuteEvenEnd();
                s4 += ":00";
                Time timeEvenEnd = Time.valueOf(s4);
                companyConstant.setEveningTo(timeEvenEnd);
                int ret = dataService.insertRow(companyConstant);
                if (ret > 0) {
                    redirectAttributes.addFlashAttribute("update", true);
                    return "redirect:/device/GPSsetting";
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
        }
        return GPSsetting(request, companyConstant, uiModel);
    }

    private String GPSsetting(HttpServletRequest request, CompanyConstant companyConstant, Model model) {

        GPSUtils.addTimeModel(model);
        model.addAttribute("companyConstant", companyConstant);
        return "deviceGPS";
    }

    private void validateInputGPS(CompanyConstant companyConstant, BindingResult bindingResult, boolean isAdd) {
        if (companyConstant.getPeriodGetPosition() == null) {

            bindingResult.rejectValue("periodGetPosition", "get_period", "empty_error_code");
        }
        if (companyConstant.getPeriodSendPosition() == null) {

            bindingResult.rejectValue("periodSendPosition", "send_period", "empty_error_code");
        }
        if (companyConstant.getDistance() == null) {
            bindingResult.rejectValue("distance", "distance", "empty_error_code");
        }
        if (companyConstant.getMinDistance() == null) {
            bindingResult.rejectValue("minDistance", "minDistance", "empty_error_code");
        }
    }
}
