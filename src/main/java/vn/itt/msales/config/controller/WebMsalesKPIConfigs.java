package vn.itt.msales.config.controller;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.itt.msales.entity.CompanyConfigKpi;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author ChinhnQ
 */
@Controller
public class WebMsalesKPIConfigs {

    @Autowired
    private DataService dataService;

    private CompanyConfigKpi getCompanyConfigKPI(int companyId) {
        ParameterList param = new ParameterList(1, 1);
        param.add("companyId", companyId);
        List<CompanyConfigKpi> list = dataService.getListOption(CompanyConfigKpi.class, param);
        return list.isEmpty() ? null : list.get(0);
    }

    @RequestMapping(value = "/kpi/config", method = RequestMethod.GET)
    public String kpiConfig(HttpServletRequest request, Model uiModel,
            @ModelAttribute("kpiConFigForm") CompanyConfigKpi companyConfigKpi) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        companyConfigKpi = getCompanyConfigKPI(loginUserInf.getCompanyId());
        if (companyConfigKpi == null) {
            companyConfigKpi = new CompanyConfigKpi();
            companyConfigKpi.setCompanyId(loginUserInf.getCompanyId());
            uiModel.addAttribute("newKPI", true);
        }
        uiModel.addAttribute("kpiConFigForm", companyConfigKpi);

        return "kpiConfig";
    }

    @RequestMapping(value = "/kpi/config", method = RequestMethod.POST)
    public String kpiConfig(HttpServletRequest request,
            @ModelAttribute("kpiConFigForm") CompanyConfigKpi companyConfigKpi,
            BindingResult bindingResult) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        CompanyConfigKpi rootCompanyConfigKpi = getCompanyConfigKPI(loginUserInf.getCompanyId());
        request.setAttribute("submit", true);

        if (validate(companyConfigKpi, bindingResult)) {
            //co loi
            if (rootCompanyConfigKpi == null) {
                request.setAttribute("newKPI", true);
            }
            request.setAttribute("error", true);
            return "kpiConfig";
        }

        try {
            if (rootCompanyConfigKpi == null) {
                //create
                companyConfigKpi.setCompanyId(loginUserInf.getCompanyId());
                companyConfigKpi.setCreatedAt(new Date());
                companyConfigKpi.setCreatedUser(loginUserInf.getId());
                companyConfigKpi.setUpdatedUser(0);
                companyConfigKpi.setDeletedUser(0);

                dataService.insert(companyConfigKpi);
                request.setAttribute("updatedSuccess", true);
                request.setAttribute("newKPI", true);
            } else {
                //update
                companyConfigKpi.setCompanyId(rootCompanyConfigKpi.getCompanyId());
                companyConfigKpi.setId(rootCompanyConfigKpi.getId());
                companyConfigKpi.setCreatedAt(rootCompanyConfigKpi.getCreatedAt());
                companyConfigKpi.setCreatedUser(rootCompanyConfigKpi.getCreatedUser());
                companyConfigKpi.setUpdatedAt(new Date());
                companyConfigKpi.setUpdatedUser(rootCompanyConfigKpi.getUpdatedUser());
                companyConfigKpi.setDeletedUser(rootCompanyConfigKpi.getDeletedUser());

                dataService.updateRow(companyConfigKpi);
                request.setAttribute("updatedSuccess", true);
            }
        } catch (Exception ex) {
            request.setAttribute("updatedSuccess", false);
        }
        return "kpiConfig";
    }

    private boolean validate(CompanyConfigKpi companyConfigKpi, BindingResult bindingResult) {
        if (companyConfigKpi.getPeriod1() == null) {
            bindingResult.rejectValue("period1", "company_config_kpi_empty_hour");
        } else if (companyConfigKpi.getPeriod1() < 0) {
            bindingResult.rejectValue("period1", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getPeriod2() == null) {
            bindingResult.rejectValue("period2", "company_config_kpi_empty_hour");
        } else if (companyConfigKpi.getPeriod2() < 0) {
            bindingResult.rejectValue("period2", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getPercentPos1() == null) {
            bindingResult.rejectValue("percentPos1", "company_config_kpi_empty_percent");
        } else if (companyConfigKpi.getPercentPos1() < 0) {
            bindingResult.rejectValue("percentPos1", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getPercentPos2() == null) {
            bindingResult.rejectValue("percentPos2", "company_config_kpi_empty_percent");
        } else if (companyConfigKpi.getPercentPos2() < 0) {
            bindingResult.rejectValue("percentPos2", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getPercentTarget1() == null) {
            bindingResult.rejectValue("percentTarget1", "company_config_kpi_empty_percent");
        } else if (companyConfigKpi.getPercentTarget1() < 0) {
            bindingResult.rejectValue("percentTarget1", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getPercentTarget2() == null) {
            bindingResult.rejectValue("percentTarget2", "company_config_kpi_empty_percent");
        } else if (companyConfigKpi.getPercentTarget2() < 0) {
            bindingResult.rejectValue("percentTarget2", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getMaxPoint() == null) {
            bindingResult.rejectValue("maxPoint", "company_config_kpi_empty_point");
        } else if (companyConfigKpi.getMaxPoint() < 0) {
            bindingResult.rejectValue("maxPoint", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getMediumPoint() == null) {
            bindingResult.rejectValue("mediumPoint", "company_config_kpi_empty_point");
        } else if (companyConfigKpi.getMediumPoint() < 0) {
            bindingResult.rejectValue("mediumPoint", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getMinPoint() == null) {
            bindingResult.rejectValue("minPoint", "company_config_kpi_empty_point");
        } else if (companyConfigKpi.getMinPoint() < 0) {
            bindingResult.rejectValue("minPoint", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getPeriodPoint1() == null) {
            bindingResult.rejectValue("periodPoint1", "company_config_kpi_empty_point");
        } else if (companyConfigKpi.getPeriodPoint1() < 0) {
            bindingResult.rejectValue("periodPoint1", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getPeriodPoint2() == null) {
            bindingResult.rejectValue("periodPoint2", "company_config_kpi_empty_point");
        } else if (companyConfigKpi.getPeriodPoint2() < 0) {
            bindingResult.rejectValue("periodPoint2", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getPercentPosPoint1() == null) {
            bindingResult.rejectValue("percentPosPoint1", "company_config_kpi_empty_point");
        } else if (companyConfigKpi.getPercentPosPoint1() < 0) {
            bindingResult.rejectValue("percentPosPoint1", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getPercentPosPoint2() == null) {
            bindingResult.rejectValue("percentPosPoint2", "company_config_kpi_empty_point");
        } else if (companyConfigKpi.getPercentPosPoint2() < 0) {
            bindingResult.rejectValue("percentPosPoint2", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getPercentTargetPoint1() == null) {
            bindingResult.rejectValue("percentTargetPoint1", "company_config_kpi_empty_point");
        } else if (companyConfigKpi.getPercentTargetPoint1() < 0) {
            bindingResult.rejectValue("percentTargetPoint1", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getPercentTargetPoint2() == null) {
            bindingResult.rejectValue("percentTargetPoint2", "company_config_kpi_empty_point");
        } else if (companyConfigKpi.getPercentTargetPoint2() < 0) {
            bindingResult.rejectValue("percentTargetPoint2", "company_config_kpi_lesser_than_0");
        }

        if (companyConfigKpi.getMaxCorlor() == null || companyConfigKpi.getMaxCorlor().trim().isEmpty()) {
            bindingResult.rejectValue("maxCorlor", "company_config_kpi_empty_color");
        }

        if (companyConfigKpi.getMediumCorlor() == null || companyConfigKpi.getMediumCorlor().trim().isEmpty()) {
            bindingResult.rejectValue("mediumCorlor", "company_config_kpi_empty_color");
        }

        if (companyConfigKpi.getMediumCorlor() == null || companyConfigKpi.getMediumCorlor().trim().isEmpty()) {
            bindingResult.rejectValue("minCorlor", "company_config_kpi_empty_color");
        }

        if (companyConfigKpi.getPeriod1() != null
                && companyConfigKpi.getPeriod2() != null
                && (companyConfigKpi.getPeriod1() >= companyConfigKpi.getPeriod2())) {
            bindingResult.rejectValue("period1", "company_config_kpi_period1_error");
        }

        if (companyConfigKpi.getPeriodPoint1() != null
                && companyConfigKpi.getPeriodPoint2() != null
                && (companyConfigKpi.getPeriodPoint1() <= companyConfigKpi.getPeriodPoint2())) {
            bindingResult.rejectValue("periodPoint1", "company_config_kpi_periodPoint1_error");
        }

        if (companyConfigKpi.getPercentPos1() != null
                && companyConfigKpi.getPercentPos2() != null
                && (companyConfigKpi.getPercentPos1() >= companyConfigKpi.getPercentPos2())) {
            bindingResult.rejectValue("percentPos1", "company_config_kpi_percentPos1_error");
        }

        if (companyConfigKpi.getPercentPosPoint1() != null
                && companyConfigKpi.getPercentPosPoint2() != null
                && (companyConfigKpi.getPercentPosPoint1() <= companyConfigKpi.getPercentPosPoint2())) {
            bindingResult.rejectValue("percentPosPoint1", "company_config_kpi_percentPosPoint1_error");
        }

        if (companyConfigKpi.getPercentTarget1() != null
                && companyConfigKpi.getPercentTarget2() != null
                && (companyConfigKpi.getPercentTarget1() >= companyConfigKpi.getPercentTarget2())) {
            bindingResult.rejectValue("percentTarget1", "company_config_kpi_percentTarget1_error");
        }

        if (companyConfigKpi.getPercentTargetPoint1() != null
                && companyConfigKpi.getPercentTargetPoint1() != null
                && (companyConfigKpi.getPercentTargetPoint1() <= companyConfigKpi.getPercentTargetPoint2())) {
            bindingResult.rejectValue("percentTargetPoint1", "company_config_kpi_percentTargetPoint1_error");
        }

        if (companyConfigKpi.getMaxPoint() != null
                && companyConfigKpi.getMediumPoint() != null
                && (companyConfigKpi.getMaxPoint() <= companyConfigKpi.getMediumPoint())) {
            bindingResult.rejectValue("maxPoint", "company_config_kpi_maxPoint_error");
        }

        return bindingResult.hasErrors();
    }
}
