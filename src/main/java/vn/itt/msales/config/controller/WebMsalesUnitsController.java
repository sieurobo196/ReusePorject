package vn.itt.msales.config.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.config.model.WebUnits;
import vn.itt.msales.config.services.MsalesUnitServices;
import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author ChinhNQ
 */
@Controller
public class WebMsalesUnitsController {

    @Autowired
    private MsalesUnitServices unitService;
    @Autowired
    protected DataService dataService;

    @RequestMapping(value = "/unit/create")
    public ModelAndView createUnit(HttpServletRequest request,
            RedirectAttributes redirectAttr,
            @ModelAttribute("unitForm") Unit unit) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        request.setAttribute("created", true);

        if ("POST".equals(request.getMethod())) {
            if (unit != null) {
                try {
                    unit.setCompanyId(loginUserInf.getCompanyId());
                    unit.setCreatedUser(loginUserInf.getId());
                    unit.setOrder(1);//FIXME
                    if (unit.getChildUnit() == null || unit.getChildUnit() == 0) {
                        unit.setChildQuantity(null);
                        unit.setChildUnit(null);
                    }
                   
                    boolean dupblicate = unitService.checkName("name", unit.getName().trim(), loginUserInf.getCompanyId());
                    boolean dupblicateCode = unitService.checkName("code", unit.getCode().trim(), loginUserInf.getCompanyId());
                    if (!dupblicate && !dupblicateCode) {
                    	unit.setName(unit.getName().trim());
                    	unit.setCode(unit.getCode().trim());
                        int created = dataService.insertRow(unit);
                        if (created > 0) {
                            redirectAttr.addFlashAttribute("cratedSuccess", true);
                            request.setAttribute("created", false);

                            return new ModelAndView("redirect:/unit/edit/" + created);
                        } else {
                            request.setAttribute("cratedSuccess", false);
                        }
                    } else {
                    	if(dupblicate){
                    		request.setAttribute("dupblicate", true);
                    	}
                        if(dupblicateCode){
                        	request.setAttribute("dupblicateCode", true);
                        }
                    }

                } catch (Exception e) {
                    System.err.println(">>>:" + e.getMessage());
                    request.setAttribute("cratedSuccess", false);
                }
            }
        }

        request.setAttribute("childUnitList", getListUnit(loginUserInf.getCompanyId()));
        return new ModelAndView("units", "unitForm", unit);
    }

    @RequestMapping(value = "/unit/edit/{id:[\\d]+}")
    public ModelAndView editUnit(HttpServletRequest request,
            @PathVariable("id") int id,
            @ModelAttribute("unitForm") Unit unit) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }
        
        request.setAttribute("edit", true);
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        Unit unitFound = dataService.getRowById(id, Unit.class);
        if (unitFound != null && unitFound.getId() != null) {
            request.setAttribute("unit_name", unitFound.getName().toLowerCase().trim());
            request.setAttribute("unit_code", unitFound.getCode().toLowerCase().trim());
            if ("POST".equals(request.getMethod())) {
                unitFound.setUpdatedUser(loginUserInf.getId());
                if (unit.getChildUnit() == null || unit.getChildUnit() == 0) {
                    unitFound.setChildQuantity(null);
                    unitFound.setChildUnit(null);
                } else {
                    unitFound.setChildUnit(unit.getChildUnit());
                    unitFound.setChildQuantity(unit.getChildQuantity());
                }
                String nameOld = request.getAttribute("unit_name").toString();
                boolean dupblicate = false;
                if (nameOld != null) {
                    if (!nameOld.equals(unit.getName().toLowerCase().trim())) {
                        dupblicate = unitService.checkName("name", unit.getName().trim(), loginUserInf.getCompanyId());
                    }
                }
                String codeOld  = request.getAttribute("unit_code").toString();
                boolean dupblicateCode = false;
                if (codeOld != null) {
                    if (!codeOld.equals(unit.getCode().toLowerCase().trim())) {
                    	dupblicateCode = unitService.checkName("code", unit.getCode().trim(), loginUserInf.getCompanyId());
                    }
                }
                if (dupblicate || dupblicateCode) {
                	if(dupblicate){
                		 request.setAttribute("dupblicate", true);
                	}
                	if(dupblicateCode){
                		request.setAttribute("dupblicateCode", true);
                	}
                   
                } else {
                    unitFound.setCode(unit.getCode());
                    unitFound.setName(unit.getName().trim());
                    unitFound.setUpdatedAt(new Date());
                    unitFound.setUpdatedUser(loginUserInf.getId());

                    int updated = dataService.updateRow(unitFound);
                    if (updated > 0) {
                        request.setAttribute("updated", true);
                    } else {
                        request.setAttribute("updated", false);
                    }
                }
            } else if (id > 0) {
                if (unit != null && unit.getId() != null) {
                    unit = unitFound;
                } else {
                    return new ModelAndView("noData");
                }

            } else {
                return new ModelAndView("noData");
            }
        } else {
            return new ModelAndView("noData");
        }
        request.setAttribute("childUnitList", getListUnit(loginUserInf.getCompanyId()));
        return new ModelAndView("units", "unitForm", unit);
    }

    @RequestMapping(value = "/unit/list")
    public ModelAndView getListUnit(HttpServletRequest request,
            @ModelAttribute("unitListForm") WebUnits units,
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
        int maxPages = 1;
        MsalesResults<Unit> listUnit = new MsalesResults<>();
        if (units.getTextSearch() != null && units.getTextSearch().length() > 0) {
            String key =units.getTextSearch();
            ParameterList parameterList = new ParameterList(page, size);
            parameterList.add("companys.id", loginUserInf.getCompanyId());
            parameterList.or("code", key, "like", "name", key, "like");
            listUnit = dataService.getListOption(Unit.class, parameterList, true);
//            listUnit = unitService.searchUnit(units.getTextSearch(), loginUserInf.getCompanyId(), new MsalesPageRequest(page, size));
//            if (listUnit != null && !listUnit.getContentList().isEmpty()) {
//            } else {
//                page = 1;
//                size = 1;
//                request.setAttribute("notFound", true);
//            }

        } else {
            ParameterList param = new ParameterList(page, size);
            param.add("companys.id", loginUserInf.getCompanyId());
            param.setOrder("order");
            listUnit = dataService.getListOption(Unit.class, param, true);
        }
        units.setListUnits(listUnit.getContentList());
        maxPages = listUnit.getCount().intValue();
        if (maxPages % size != 0) {
            maxPages = maxPages / size + 1;
        } else {
            maxPages = maxPages / size;
        }

        request.setAttribute("page", page);
        request.setAttribute("size", size);
        request.setAttribute("maxPages", maxPages);
        return new ModelAndView("listUnits", "unitListForm", units);
    }

    private List<Unit> getListUnit(int companyId) {
        List<Unit> list = new ArrayList<>();
        try {
            list = unitService.getListUnit(companyId);
        } catch (Exception e) {
        }
        return list;
    }
}
