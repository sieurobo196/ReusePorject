package vn.itt.msales.sales.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.sales.service.MsalesSaleTransService;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author ChinhNQ
 */
@Controller
public class WebMsalesSalesTransController {
    
    @Autowired
    private MsalesSaleTransService salesTransService;
    
    @Autowired
    private DataService dataService;
    @Autowired
    private ServiceFilter serviceFilter;
    
    @RequestMapping(value = "/salestrans/list/")
    public ModelAndView getSalesTransList(Model uiModel, HttpServletRequest request,
            @ModelAttribute(value = "filter") Filter filter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }
        
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        
        uiModel.addAttribute("notShow", true);

        // create filter
        //set role cho Cbb Nhan vien
        filter.setRoles(new Integer[]{6});
        filter.processFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());
        
        SimpleDateFormat sdf = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
        if ((filter.getStartDateString() == null || filter.getStartDateString().trim().isEmpty())
                && (filter.getEndDateString() == null || filter.getEndDateString().trim().isEmpty())) {
            filter.setStartDateString(sdf.format(new Date()));
            filter.setEndDateString(sdf.format(new Date()));
        }

        // get list sales trans
        List<SalesTrans> salesTransList = new ArrayList<>();
        
        int maxPages = 1;
        try {
            MsalesResults<SalesTrans> list = salesTransService.searchTrans(filter, loginUserInf.getCompanyId(), page, size, dataService);
            if (!list.getContentList().isEmpty()) {
                salesTransList = list.getContentList();
                maxPages = Filter.calulatorMaxPages(list.getCount().intValue(), size);
            } else {
                page = 1;
                size = 1;
                uiModel.addAttribute("notFound", true);
            }
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
        
        uiModel.addAttribute("salesTransList", salesTransList);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);
        
        return new ModelAndView("transList");
    }
    
}
