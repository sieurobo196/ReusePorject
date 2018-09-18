/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.promotion.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.app.service.DateUtils;

import vn.itt.msales.common.OptionItem;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.customercare.model.ReportFilter;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelLocation;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.Promotion;
import vn.itt.msales.entity.PromotionAward;
import vn.itt.msales.entity.PromotionAwardOther;
import vn.itt.msales.entity.PromotionChannel;
import vn.itt.msales.entity.PromotionConditionalRef;
import vn.itt.msales.entity.PromotionGoodsRef;
import vn.itt.msales.entity.PromotionRangeAward;
import vn.itt.msales.entity.PromotionTransRetailer;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.export.services.ExportsService;
import vn.itt.msales.promotion.model.MsalesPromotionReport;
import vn.itt.msales.promotion.model.PromotionFilter;
import vn.itt.msales.promotion.service.PromotionService;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author vtm
 */
@Controller
@RequestMapping(value = "/promotion")
public class WebMsalesPromotionController {

    @Autowired
    private ServiceFilter serviceFilter;
    @Autowired
    private DataService dataService;
    @Autowired
    private PromotionService promotionService;
    @Resource(name = "systemPros")
    private Properties systemProperties;
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private ExportsService exportsService;

    /**
     * Path of the file to be downloaded, relative to application's directory
     */
    String separator = File.separator;
    private String filePath = String.format("reports%s", separator);
    private final String FILE_NAME = "RPTCTKM01";
    private final String FILE_NAME_03 = "RPTCTKM03";
    private String fullPath = "";
    private String promotionName = "";

    @InitBinder
    public void dataBinding(WebDataBinder binder) {
        try {
            binder.setIgnoreInvalidFields(true);
            SimpleDateFormat dateFormat = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
            binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        } catch (Exception ex) {
        }
    }

    @RequestMapping(value = "/list")
    public String list(Model uiModel, HttpServletRequest request,
            @ModelAttribute("promotionFilter") PromotionFilter promotionFilter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        promotionFilter.processFilter(uiModel, request, serviceFilter, promotionService, dataService, LoginContext.getLogin(request).getCompanyId());
        MsalesResults<Promotion> list = promotionService.searchPromotion(promotionFilter, LoginContext.getLogin(request).getCompanyId(), dataService, page, size);
        for (Promotion promotion : list.getContentList()) {
            promotion.generateConditionString();
            List<Goods> goodsList = promotionService.getListPromotionGoods(promotion.getId(), dataService);
            promotion.setGoodsList(goodsList);
        }

        uiModel.addAttribute("promotionList", list.getContentList());
        uiModel.addAttribute("now", new Date());
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("showAddLink", true);
        uiModel.addAttribute("maxPages", Filter.calulatorMaxPages(list.getCount().intValue(), size));

        return "ctkm_list";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String Add(Model uiModel, HttpServletRequest request,
            PromotionFilter promotionFilter,
            @ModelAttribute("promotion") Promotion promotion) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        init(uiModel, promotion, LoginContext.getLogin(request).getCompanyId(), false);
        promotionFilter.processChannelFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());
        return "ctkm_add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String create(Model uiModel, HttpServletRequest request,
            @ModelAttribute("promotion") Promotion promotion,
            BindingResult bindingResult,
            PromotionFilter promotionFilter,
            RedirectAttributes redirectAttributes,
            @RequestParam("promotionFile") MultipartFile file) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf userLogin = LoginContext.getLogin(request);

        if (!validate(promotion, promotionFilter, userLogin.getCompanyId(), bindingResult, false)) {
            if (createRefPromotion(promotion, promotionFilter, userLogin, file, false, null)) {
                redirectAttributes.addFlashAttribute("success", true);
                return "redirect:/promotion/edit/" + promotion.getId();
            } else {
                uiModel.addAttribute("success", false);
            }
        }

        init(uiModel, promotion, LoginContext.getLogin(request).getCompanyId(), false);
        promotionFilter.processChannelFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());
        return "ctkm_add";
    }

    private boolean createRefPromotion(Promotion promotion, PromotionFilter promotionFilter, MsalesLoginUserInf userLogin, MultipartFile file, boolean update, Promotion rootPromotion) {
        try {
            //create promotion range
            if (promotion.isIsRange()) {
                //xoa nhung range ko hop le
                PromotionRangeAward promotionRangeAward = promotion.getProRanges();

                if (promotionRangeAward.getPromotionR03() == null || promotionRangeAward.getPromotionR03() == null) {
                    promotionRangeAward.setPromotionR03(null);
                    promotionRangeAward.setPromotionV03(null);
                    promotionRangeAward.setPromotionR04(null);
                    promotionRangeAward.setPromotionV04(null);
                    promotionRangeAward.setPromotionR05(null);
                    promotionRangeAward.setPromotionV05(null);
                } else if (promotionRangeAward.getPromotionR04() == null || promotionRangeAward.getPromotionR04() == null) {
                    promotionRangeAward.setPromotionR04(null);
                    promotionRangeAward.setPromotionV04(null);
                    promotionRangeAward.setPromotionR05(null);
                    promotionRangeAward.setPromotionV05(null);
                } else if (promotionRangeAward.getPromotionR05() == null || promotionRangeAward.getPromotionR05() == null) {
                    promotionRangeAward.setPromotionR05(null);
                    promotionRangeAward.setPromotionV05(null);
                }

                promotion.setQuantity(null);
                promotion.setProAwardQuantity(null);

                promotionRangeAward.setStatusId(1);
                promotionRangeAward.setCreatedAt(new Date());
                promotionRangeAward.setCreatedUser(userLogin.getId());
                promotionRangeAward.setUpdatedUser(0);
                promotionRangeAward.setDeletedUser(userLogin.getId());
                promotionRangeAward.setPromotionId(0);//bo truong nay
                dataService.insert(promotionRangeAward);
            } else {
                promotion.setProRanges(null);
            }

            //tam thoi an
//            if (promotion.isReg()) {
//                //khuyen mai tich luy => ko can luu san pham
//                promotion.setGoodsCategorys(null);
//                promotion.setGoodsIdList(null);
//                promotion.setQuantity(null);
//            }
            if (promotion.getPromotionAwards().getId() == 3 || promotion.getPromotionAwards().getId() == 4) {
                //khuyen mai tinh theo doanh so/%
                promotion.setAwardOthers(null);
                promotion.setProAwardGoodss(null);
                //thieu truong discount
                //tam su dung ProAwardQuantity
                promotion.setProAwardQuantity(promotion.getDiscount());
                promotion.setProAwardUnits(null);
            } else if (promotion.getPromotionAwards().getId() == 1) {
                promotion.setAwardOthers(null);
            } else if (promotion.getPromotionAwards().getId() == 2) {//khuyen mai tang vat pham
                promotion.setProAwardGoodss(null);
                promotion.setProAwardQuantity(promotion.getProAwardOtherQuantity());
                promotion.setProAwardUnits(null);
            }

            //create promotion deleted  
            promotion.setCompanyId(userLogin.getCompanyId());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(promotion.getEndDate());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);

            promotion.setEndDate(calendar.getTime());
            if (!update) {
                promotion.setCreatedAt(new Date());
                promotion.setCreatedUser(userLogin.getId());
                promotion.setDeletedUser(userLogin.getId());
                promotion.setUpdatedUser(0);
                dataService.insert(promotion);
            } else {
                promotion.setCreatedUser(rootPromotion.getCreatedUser());
                promotion.setCreatedAt(rootPromotion.getCreatedAt());
                promotion.setUpdatedUser(userLogin.getId());
                promotion.setUpdatedAt(new Date());
                promotion.setDeletedUser(0);
            }

            List insertList = new ArrayList<>();

            //create goodsList - not all in goodsCategory
            if (promotion.getGoodsIdList() != null && !promotion.getGoodsIdList().isEmpty()) {
                if (promotion.getGoodsCategorys() == null) {
                    promotion.setGoodsCategorys(null);
                }
                for (int id : promotion.getGoodsIdList()) {
                    //chua check goods ton tai
                    PromotionGoodsRef promotionGoodsRef = new PromotionGoodsRef();
                    promotionGoodsRef.setGoodsId(id);
                    promotionGoodsRef.setPromotionId(promotion.getId());
                    promotionGoodsRef.setQuantity(null);
                    promotionGoodsRef.setStatusId(1);
                    promotionGoodsRef.setCreatedAt(new Date());
                    promotionGoodsRef.setCreatedUser(userLogin.getId());
                    promotionGoodsRef.setUpdatedUser(0);
                    promotionGoodsRef.setDeletedUser(0);
                    insertList.add(promotionGoodsRef);
                }
            }

            //create promotion channel
            if (promotion.isScope()) {
                for (int id : promotionFilter.getChannelIds()) {
                    PromotionChannel promotionChannel = new PromotionChannel();
                    promotionChannel.setChannelId(id);
                    promotionChannel.setPromotionId(promotion.getId());
                    promotionChannel.setStatusId(1);
                    promotionChannel.setCreatedAt(new Date());
                    promotionChannel.setCreatedUser(userLogin.getId());
                    promotionChannel.setUpdatedUser(0);
                    promotionChannel.setDeletedUser(0);
                    insertList.add(promotionChannel);
                }
            }

            //create promotion codition ref
            if (promotion.getConditionList() != null && !promotion.getConditionList().isEmpty()) {
                for (int id : promotion.getConditionList()) {
                    PromotionConditionalRef promotionConditionalRef = new PromotionConditionalRef();
                    promotionConditionalRef.setPromotionId(promotion.getId());
                    promotionConditionalRef.setPromotionConditionalId(id);
                    promotionConditionalRef.setStatusId(1);
                    promotionConditionalRef.setCreatedAt(new Date());
                    promotionConditionalRef.setCreatedUser(userLogin.getId());
                    promotionConditionalRef.setUpdatedUser(0);
                    promotionConditionalRef.setDeletedUser(0);
                    insertList.add(promotionConditionalRef);
                }
            }

            if (file != null && !file.isEmpty()) {
                //luu hinh
                String folderPath = systemProperties.getProperty("system.imagesRoot") + "/" + systemProperties.getProperty("system.promotionImage") + "/";
                String urlSave = userLogin.getBranch()
                        + "/" + userLogin.getCompanyId()
                        + "/" + promotion.getId();

                File path = new File(folderPath + urlSave);
                if (!path.exists() || !path.isDirectory()) {
                    path.mkdirs();
                }
                try {
                    String fileName = promotion.getId() + ".png";
                    File promotionImage = new File(path.getPath() + "/" + fileName);
                    file.transferTo(promotionImage);
                    String fileUrl = urlSave + "/" + fileName;
                    promotion.setUrlImage(fileUrl);
                } catch (IOException ex) {
                    return false;
                }
            } else if (update) {
                promotion.setUrlImage(rootPromotion.getUrlImage());
            }

            promotion.setDeletedUser(0);
            insertList.add(promotion);
            if (promotion.isIsRange()) {
                promotion.getProRanges().setPromotionId(promotion.getId());
                promotion.getProRanges().setDeletedUser(0);
                insertList.add(promotion.getProRanges());
            }

            if (promotion.getProAwardUnits() == null || promotion.getProAwardUnits().getId() == null || promotion.getProAwardUnits().getId() == -1) {
                promotion.setProAwardUnits(null);
            }

            dataService.insertOrUpdateArray(insertList);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String detail(Model uiModel, HttpServletRequest request,
            PromotionFilter promotionFilter,
            @PathVariable(value = "id") int id,
            @ModelAttribute("promotion") Promotion promotion) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        promotion = promotionService.getPromotionFullInfo(id, LoginContext.getLogin(request).getCompanyId(), dataService);

        if (promotion != null) {
            if (promotion.getStatuss().getId() == 22 || promotion.getStatuss().getId() == 28) {
                request.setAttribute("readonly", true);//chỉ xem
            }
            init(uiModel, promotion, LoginContext.getLogin(request).getCompanyId(), true);
            List<Channel> channelList = promotionService.getListPromotionChannelId(id, dataService);
            promotionFilter.setChannelIdList(initChannel(channelList));
            promotionFilter.processChannelFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());

            uiModel.addAttribute("promotion", promotion);
            uiModel.addAttribute("isUpdate", true);
            return "ctkm_edit";
        } else {
            return "noData";
        }
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String edit(Model uiModel, HttpServletRequest request,
            PromotionFilter promotionFilter,
            @PathVariable(value = "id") int id,
            @ModelAttribute("promotion") Promotion promotion,
            BindingResult bindingResult,
            @RequestParam("promotionFile") MultipartFile file) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf userLogin = LoginContext.getLogin(request);
        Promotion rootPromotion = promotionService.getPromotionFullInfo(promotion.getId(), userLogin.getCompanyId(), dataService);
        if (rootPromotion != null && rootPromotion.getStatuss().getId() == 22 || rootPromotion.getStatuss().getId() == 28) {
            return "redirect:/promotion/edit/" + rootPromotion.getId();
        }

        if (rootPromotion != null) {
            if (!validate(promotion, promotionFilter, userLogin.getCompanyId(), bindingResult, true)) {
                List updateList = new ArrayList<>();
                //delete all ref
                //channel
                if (rootPromotion.isScope()) {
                    List<PromotionChannel> promotionChannelList = promotionService.getListPromotionChannel(rootPromotion.getId(), dataService);
                    for (PromotionChannel promotionChannel : promotionChannelList) {
                        promotionChannel.setDeletedUser(userLogin.getId());
                        promotionChannel.setDeletedAt(new Date());
                    }
                    updateList.addAll(promotionChannelList);
                }

                //condition
                List<PromotionConditionalRef> promotionConditionalRefList = promotionService.getListPromotionConditionalRef(rootPromotion.getId(), dataService);
                for (PromotionConditionalRef promotionConditionalRef : promotionConditionalRefList) {
                    promotionConditionalRef.setDeletedUser(userLogin.getId());
                    promotionConditionalRef.setDeletedAt(new Date());
                }
                updateList.addAll(promotionConditionalRefList);

                //goods
                List<PromotionGoodsRef> promotionGoodsRefList = promotionService.getListPromotionGoodsRef(rootPromotion.getId(), dataService);
                for (PromotionGoodsRef promotionGoodsRef : promotionGoodsRefList) {
                    promotionGoodsRef.setDeletedUser(userLogin.getId());
                    promotionGoodsRef.setDeletedAt(new Date());
                }
                updateList.addAll(promotionGoodsRefList);

                //bac thang
                if (rootPromotion.getProRanges() != null) {
                    rootPromotion.getProRanges().setDeletedAt(new Date());
                    rootPromotion.getProRanges().setDeletedUser(userLogin.getId());
                    updateList.add(rootPromotion.getProRanges());
                }

                //create ref
                if (createRefPromotion(promotion, promotionFilter, userLogin, file, true, rootPromotion)) {
                    try {
                        dataService.updateArray(updateList);
                    } catch (Exception ex) {
                    }
                    uiModel.addAttribute("updated", true);
                    if (promotion.getStatuss().getId() == 22 || promotion.getStatuss().getId() == 28) {
                        request.setAttribute("readonly", true);//chỉ xem
                    }
                } else {
                    uiModel.addAttribute("updated", false);
                }

            }
            init(uiModel, promotion, LoginContext.getLogin(request).getCompanyId(), true);
            promotionFilter.processChannelFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());

            uiModel.addAttribute("isUpdate", true);
            return "ctkm_edit";
        } else {
            return "noData";
        }
    }

    private List<List<Integer>> initChannel(List<Channel> channelList) {
        //al channel is same level
        List<List<Integer>> ret = new ArrayList<>();
        if (!channelList.isEmpty()) {
            try {
                Channel channel = channelList.get(0);
                while (channel != null) {
                    List<Integer> list = new ArrayList<>();
                    ret.add(list);
                    channel = channel.getParents();
                }

                for (Channel item : channelList) {
                    for (int i = ret.size() - 1; i >= 0; i--) {
                        ret.get(i).add(item.getId());
                        item = item.getParents();
                    }
                }
            } catch (Exception ex) {
                return new ArrayList<>();
            }
        }
        return ret;
    }

    private boolean validate(Promotion promotion, PromotionFilter promotionFilter, int companyId, BindingResult bindingResult, boolean update) {
        if (promotion.getName() == null || promotion.getName().isEmpty()) {
            bindingResult.rejectValue("name", "promotion_empty_name");
        }
        if (promotion.getCode() == null || promotion.getCode().trim().isEmpty()) {
            bindingResult.rejectValue("code", "promotion_empty_code");
        } else if (!update) {
            if (promotionService.checkPromotionCode(promotion.getCode(), companyId, dataService)) {
                bindingResult.rejectValue("code", "promotion_exist_code");
            }
        }
        if (promotion.getStatuss() == null || promotion.getStatuss().getId() == null || promotion.getStatuss().getId() < 1) {
            bindingResult.rejectValue("status.id", "prmotion_empty_status");
        }
        if (promotion.isReg()) {
            if (promotion.getApproveRoles() == null || promotion.getApproveRoles().getId() == null || promotion.getApproveRoles().getId() < 1) {
                bindingResult.rejectValue("approveRoles.id", "promotion_empty_apply_role");
            }
        }
        if (promotion.isScope()) {
            if (promotionFilter.getChannelIdList() == null || promotionFilter.getChannelIdList().isEmpty()) {
                bindingResult.rejectValue("scope", "promotion_empty_apply_scope");
            }
        }
        if (promotion.getStartDate() == null) {
            bindingResult.rejectValue("startDate", "promotion_empty_start_date");
        } else if (!update && promotion.getStartDate().compareTo(DateUtils.getShortNow()) < 0) {
            bindingResult.rejectValue("startDate", "promotion_old_start_date");
        }

        if (promotion.getEndDate() == null) {
            bindingResult.rejectValue("endDate", "promotion_empty_end_date");
        }

        if (promotion.getStartDate() != null && promotion.getEndDate() != null && promotion.getEndDate().compareTo(promotion.getStartDate()) < 0) {
            bindingResult.rejectValue("endDate", "promotion_empty_end_date_error");
        }

        if (promotion.getConditionQuantity() == null || promotion.getConditionQuantity() > 4
                || promotion.getConditionQuantity() < 1) {
            bindingResult.rejectValue("conditionQuantity", "promotion_empty_condition_quantity");
        }
        //if (!promotion.isReg()) {
        if (promotion.getConditionQuantity() != null && promotion.getConditionQuantity() > 0 && promotion.getConditionQuantity() < 5) {
//                switch (promotion.getConditionQuantity()) {
//                    case 1:
//                    case 2:
//                        //neu chi chon loai sp
//                        if (promotion.getGoodsIdList() == null || promotion.getGoodsIdList().isEmpty()) {
//                            bindingResult.rejectValue("goodsIdList", "promotion_empty_goods");
//                        }
//                        break;
//                    case 3:
//                        break;
//                    case 4:
//                        break;
//                }
            if (promotion.getGoodsIdList() == null || promotion.getGoodsIdList().isEmpty()) {
                bindingResult.rejectValue("goodsIdList", "promotion_empty_goods");
            }
        }
        // }

        if (!promotion.isIsRange() && !promotion.isReg()) {
            if (promotion.getQuantity() == null) {
                bindingResult.rejectValue("quantity", "promotion_empty_quantity");
            } else if (promotion.getQuantity() < 1) {
                bindingResult.rejectValue("quantity", "promotion_error_number");
            }
        }

        if (promotion.getPromotionAwards() == null
                || promotion.getPromotionAwards().getId() < 1
                || promotion.getPromotionAwards().getId() > 4) {
            bindingResult.rejectValue("promotionAwards.id", "promotion_empty_pro_award");
        }
        if (promotion.isIsRange()) {
            if (promotion.getProRanges() == null || promotion.getProRanges().getPromotionR01() == null) {
                bindingResult.rejectValue("proRanges.promotionR01", "promotion_empty_r01");
            } else if (promotion.getProRanges().getPromotionR01() < 1) {
                bindingResult.rejectValue("proRanges.promotionR01", "promotion_error_number");
            }

            if (promotion.getProRanges() == null || promotion.getProRanges().getPromotionR02() == null) {
                bindingResult.rejectValue("proRanges.promotionR02", "promotion_empty_r02");
            } else if (promotion.getProRanges().getPromotionR02() < 1) {
                bindingResult.rejectValue("proRanges.promotionR02", "promotion_error_number");
            }

            if (promotion.getProRanges() == null || promotion.getProRanges().getPromotionV01() == null) {
                bindingResult.rejectValue("proRanges.promotionV01", "promotion_empty_v01");
            } else if (promotion.getProRanges().getPromotionV01() < 1) {
                bindingResult.rejectValue("proRanges.promotionV01", "promotion_error_number");
            }

            if (promotion.getProRanges() == null || promotion.getProRanges().getPromotionV02() == null) {
                bindingResult.rejectValue("proRanges.promotionV02", "promotion_empty_v02");
            } else if (promotion.getProRanges().getPromotionV02() < 1) {
                bindingResult.rejectValue("proRanges.promotionV02", "promotion_error_number");
            }
        }

        if (promotion.getPromotionAwards() != null
                && promotion.getPromotionAwards().getId() > 0
                && promotion.getPromotionAwards().getId() < 5) {
            switch (promotion.getPromotionAwards().getId()) {
                case 1:
                    if (promotion.getProAwardGoodss() == null || promotion.getProAwardGoodss().getId() == 0) {
                        bindingResult.rejectValue("proAwardGoodss.id", "promotion_empty_goods_award");
                    }
                    if (!promotion.isIsRange()) {
                        if (promotion.getProAwardQuantity() == null || promotion.getProAwardQuantity() == 0) {
                            bindingResult.rejectValue("proAwardQuantity", "promotion_empty_pro_award_quantity");
                        }
                    }
                    break;
                case 2:
                    if (promotion.getAwardOthers() == null || promotion.getAwardOthers().getId() == 0) {
                        bindingResult.rejectValue("awardOthers.id", "promotion_empty_goods_award_other");
                    }
                    if (!promotion.isIsRange()) {
                        if (promotion.getProAwardOtherQuantity() == null || promotion.getProAwardOtherQuantity() == 0) {
                            bindingResult.rejectValue("proAwardOtherQuantity", "promotion_empty_pro_award_quantity");
                        }
                    }
                    break;
                case 3:
                case 4:
                    if (!promotion.isIsRange()) {
                        if (promotion.getDiscount() == null) {
                            bindingResult.rejectValue("discount", "promotion_empty_discount");
                        } else if (promotion.getDiscount() < 1) {
                            bindingResult.rejectValue("discount", "promotion_error_discount");
                        }
                    }
                    break;
            }
        }
        return bindingResult.hasErrors();
    }

    private void init(Model uiModel, Promotion promotion, int companyId, boolean fullStatus) {
        if (promotion.getGoodsCategorys() != null) {
            uiModel.addAttribute("goodsList", promotionService.getCbListGoodsByGoodsCategoryId(promotion.getGoodsCategorys().getId(), companyId, dataService));
        }
        //uiModel.addAttribute("goodsList", promotionService.getCbListGoodsByGoodsCategoryId(promotion.getGoodsCategoryId(), companyId, dataService));
        if (promotion.getProAwardGoodss() != null) {
            if (promotion.getProAwardGoodss().getGoodsCategorys() != null) {
                promotion.setProAwardGoodsCategory(promotion.getProAwardGoodss().getGoodsCategorys().getId());
            }
        }
        if (promotion.getProAwardGoodsCategory() != null) {
            uiModel.addAttribute("awardGoodsList", promotionService.getCbListGoodsByGoodsCategoryId(promotion.getProAwardGoodsCategory(), companyId, dataService));
        }

        uiModel.addAttribute("goodsOtherList", promotionService.getCbListPromotionAwardOther(companyId,dataService));

        uiModel.addAttribute("conditionList", promotionService.getCbListPromotionCondition(0, dataService));
        if (fullStatus) {
            uiModel.addAttribute("statusList", promotionService.getCbListFullPromotionStatus(dataService));
        } else {
            uiModel.addAttribute("statusList", promotionService.getCbListPromotionStatus(dataService));
        }
        
        
        uiModel.addAttribute("unitList", promotionService.getCbListUnit(companyId, dataService));
        uiModel.addAttribute("approveRoleList", promotionService.getCbListPromotionApproveRole(dataService));
        uiModel.addAttribute("goodsCategoryList", promotionService.getCbListGoodsCategory(companyId, dataService));
        uiModel.addAttribute("awardList", promotionService.getCbListPromotionAward(dataService));
        uiModel.addAttribute("quantityTypeList", promotionService.getCbListQuantityType());
    }

    @RequestMapping(value = "/searchsalepointlist")
    public String ctkmKiemTra(Model uiModel, HttpServletRequest request,
            @ModelAttribute("ctkmKiemTra") Filter filter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size)
            throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;
        String hql = "SELECT PTR.id as id,"
        		//Get POS
        		+ " PTR.poss.id as posId, PTR.poss.posCode as posCode, PTR.poss.name as posName,"
                + " PTR.poss.address as address,"
                + " CASE WHEN (PTR.poss.tel is not null and PTR.poss.tel != '') then PTR.poss.tel"
                + " WHEN (PTR.poss.mobile is not null and PTR.poss.mobile != '')  then PTR.poss.mobile"
                + " ELSE PTR.poss.otherTel END as tel,"
                //Get info PromotionTransRetails
                + " PTR.createdUser as createdUser, PTR.updatedUser as updatedUser,"               
                + " PTR.awardName as awardName, PTR.awardQuantity as awardQuantity, PTR.awardAmount as awardAmount,"
                + " PTR.isOther as isOther, PTR.awardGoodsId as awardGoodsId, PTR.note as note, "
                + " PTR.statuss.id as statusId, PTR.statuss.name as statusName, "
                + " PTR.quantity as quantity, PTR.amount as amount, "
                
                + " PTR.promotions.id as promotionId, PTR.promotions.name as promotionName,PTR.promotions.note as promotionNote, PTR.createdAt as createdAt,"
                
                + " PTR.promotions.statuss.id as promotionStatusId, PTR.promotions.quantity as promotionQuantity,"
                + " PTR.promotions.statuss.name as promotionStatusName,"
                
                + " PTR.promotions.promotionAwards.id as awardId, PTR.promotions.proAwardQuantity as proAwardQuantity, "
                
                + " CASE WHEN ApproveRoles.id  is null THEN NULL "
                + " ELSE ApproveRoles.name END as approveRolesName"
                
        	+ " FROM PromotionTransRetailer as PTR"
        	+ " LEFT JOIN PTR.promotions.approveRoles as ApproveRoles"
                + " WHERE PTR.deletedUser = 0"
                + " AND (ApproveRoles.id >= "+ userInf.getUserRoleId() + " OR ApproveRoles.id is null ) "
                + " AND PTR.promotions.companys.id = " + userInf.getCompanyId();
        
        String hqlS = "SELECT PTR.id as id,"
        		//Get POS
        	+ " PTR.retailers.id as posId, PTR.retailers.posCode as posCode, PTR.retailers.name as posName,"
                + " PTR.retailers.address as address,"
                + " CASE WHEN (PTR.retailers.tel is not null and PTR.retailers.tel != '') then PTR.retailers.tel"
                + " WHEN (PTR.retailers.mobile is not null and PTR.retailers.mobile != '')  then PTR.retailers.mobile"
                + " ELSE PTR.retailers.otherTel END as tel,"
                //Get info PromotionTransRetails
                + " PTR.createdUser as createdUser, PTR.updatedUser as updatedUser,"               
                + " PTR.awardName as awardName, PTR.awardQuantity as awardQuantity, PTR.awardAmount as awardAmount,"
                + " PTR.isOther as isOther, PTR.awardGoodsId as awardGoodsId, PTR.note as note, "
                + " PTR.statuss.id as statusId, PTR.statuss.name as statusName, "
                + " PTR.quantity as quantity, PTR.amount as amount, "
                
                + " PTR.promotions.id as promotionId, PTR.promotions.name as promotionName,PTR.promotions.note as promotionNote, PTR.createdAt as createdAt,"
                
                + " PTR.promotions.statuss.id as promotionStatusId, PTR.promotions.quantity as promotionQuantity, "
                + " PTR.promotions.statuss.name as promotionStatusName,"
                
                + " PTR.promotions.promotionAwards.id as awardId, PTR.promotions.proAwardQuantity as proAwardQuantity, "
                
                + " CASE WHEN ApproveRoles.id  is null THEN NULL "
                + " ELSE ApproveRoles.name END as approveRolesName"
                
        	+ " FROM PromotionAccumulationRetailer as PTR"
        	+ " LEFT JOIN PTR.promotions.approveRoles as ApproveRoles"
                + " WHERE PTR.deletedUser = 0 AND PTR.statuss.id not in (25,26)"
                + " AND (ApproveRoles.id >= "+ userInf.getUserRoleId() + " OR ApproveRoles.id is null ) "
                + " AND PTR.promotions.companys.id = " + userInf.getCompanyId();
        //Get posId 
        if(!userInf.getUserRoleCode().equals("USER_ROLE_ADMIN_COMPANY")){
        	String string = "";
    		for(OptionItem op : userInf.getChannelList()){
    			string += op.getId() + ",";
    		}
    		string += "''";
    		String hqlChannel = "SELECT C.fullCode as fullCode FROM Channel as C "
    				+ " WHERE deletedUser = 0 and id IN ("+string+")";
    		List<HashMap> listChannel = dataService.executeSelectHQL(HashMap.class, hqlChannel, true, 0, 0);
                
                String getUsers = "SELECT URC.users.id as userId FROM UserRoleChannel as URC"
                        + " WHERE users.deletedUser = 0 and deletedUser = 0 and userRoles.id = 6 and channels.companys.id = "+userInf.getCompanyId();
    		string = "";
    		for(HashMap hm : listChannel){
    			string += " OR channels.fullCode LIKE '%"+hm.get("fullCode")+"%'";
    		}
                if(string.length() > 3){
                    getUsers += " and ( "+string.substring(3)+" )";
                }else{
                    getUsers += " and channels.id = 0";
                }
                getUsers += " GROUP BY users.id";
                List<HashMap> listUserId = dataService.executeSelectHQL(HashMap.class, getUsers, true, 0, 0);
                string = "";
                for(HashMap hm : listUserId){
                    string += hm.get("userId") + ",";
                }
                string += "''";
                String getPosId = "SELECT M.poss.id as posId FROM MCPDetails as M"
                        + " WHERE M.deletedUser = 0 and M.mcps.deletedUser = 0 "
                        + " and M.mcps.implementEmployees.id in ("+string+")";
                getPosId += " GROUP BY M.poss.id";
                List<HashMap> listPosId = dataService.executeSelectHQL(HashMap.class, getPosId, true, 0, 0);
                string = "";
                for(HashMap hm : listPosId){
                    string += hm.get("posId") +",";
                }
                string += "''";
    		hql += " AND PTR.poss.id IN ("+string+")";
    		hqlS += " AND PTR.retailers.id IN ("+string+")";
        }
        if (filter.getSearchText() != null && !filter.getSearchText().isEmpty() && !filter.getSearchText().trim().isEmpty()) {
            filter.getSearchText().replaceAll(" ", "%");
            hql += " AND PTR.poss.name LIKE '%" + filter.getSearchText() + "%'";
            hqlS += " AND PTR.retailers.name LIKE '%" + filter.getSearchText() + "%'";
        }

        String posCode = filter.getEndDateString();
        if (posCode != null && !posCode.isEmpty() && !posCode.trim().isEmpty()) {
            posCode.replaceAll(" ", "%");
            hql += " AND PTR.poss.posCode LIKE '%" + posCode + "%'";
            hqlS += " AND PTR.retailers.posCode LIKE '%" + posCode + "%'";
        }
        hql += " ORDER BY PTR.id desc";
        hqlS += " ORDER BY PTR.id desc";
        
        List<HashMap> listPTR = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
        List<HashMap> listPAR = dataService.executeSelectHQL(HashMap.class, hqlS, true, 0, 0);
        if(!listPAR.isEmpty()){
        	listPTR.addAll(listPAR);
        }
        List<PromotionTransRetailer> lists = returnResultFromListHashMap(listPTR);
        if(!lists.isEmpty()){
        	Collections.sort(lists);
        }
        
        maxPages = lists.size();
        if (maxPages % size != 0) {
            maxPages = maxPages / size + 1;
        } else {
            maxPages = maxPages / size;
        }
        //Phan trang here
        if(lists.size() > 0){
            if(page*size > lists.size()){
             	uiModel.addAttribute("ctkmSalePointList", lists.subList((page-1)*size, lists.size()));
             }else{
             	uiModel.addAttribute("ctkmSalePointList", lists.subList((page-1)*size, page*size));
             }
        }     
        
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);
        return "ctkmKiemTra";
    }

    @RequestMapping(value = "/salepointlist")
    public String ctkmSalesPointList(Model uiModel, HttpServletRequest request,
            @ModelAttribute("ctkmForm") PromotionFilter promotionFilter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        //
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;

        MsalesLoginUserInf userLogin = LoginContext.getLogin(request);

        promotionFilter.processFilter(uiModel, request, serviceFilter, promotionService, dataService, LoginContext.getLogin(request).getCompanyId());
        boolean approve = false;
        uiModel.addAttribute("showCbbPromotion", true);
        uiModel.addAttribute("showCbbStatus", true);

        promotionService.initDataForPromotionCheck(request, userLogin, uiModel, approve);

        MsalesResults<HashMap> results = promotionService.getHqlStringForSearchPromotion(request, promotionFilter, uiModel, userLogin, approve, page, size);
        List<HashMap> listPTR = results.getContentList();
        List<PromotionTransRetailer> lists = returnResultFromListHashMap(listPTR);

        uiModel.addAttribute("ctkmSalePointList", lists);
        maxPages = (int) (long) results.getCount();
        if (maxPages % size != 0) {
            maxPages = maxPages / size + 1;
        } else {
            maxPages = maxPages / size;
        }

        //Thêm dữ liệu cho Model
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);
        return "ctkm_salepoint_list";
    }

    public List<PromotionTransRetailer> returnResultFromListHashMap(List<HashMap> listPTR) {
        List<PromotionTransRetailer> lists = new ArrayList<>();
        for (HashMap hm : listPTR) {
            PromotionTransRetailer prf = new PromotionTransRetailer();
            prf.setId((Integer) hm.get("id"));
            POS pos = new POS();
            pos.setId((Integer) hm.get("posId"));
            pos.setPosCode((String) hm.get("posCode"));
            pos.setName((String) hm.get("posName"));
            pos.setAddress((String) hm.get("address"));
            pos.setTel((String) hm.get("tel"));
            //set POS
            prf.setPoss(pos);
           // prf.setAwardName((String) hm.get("awardName"));
            prf.setAwardQuantity((Integer) hm.get("proAwardQuantity"));
            //if isOther = 0 is san pham cua cong ty.
            if (hm.get("isOther") != null) {
                int awardQuantity = 0;           	
            	if(hm.get("awardQuantity") != null){
                    awardQuantity = (Integer) hm.get("awardQuantity");           		
            	}        	
                prf.setAwardQuantity(awardQuantity);
                int isOther = 0, goodsId = 0;
                try {
                    isOther = (Integer) hm.get("isOther");
                    goodsId = (Integer) hm.get("awardGoodsId");
                } catch (Exception ex) {
                }
                if (isOther == 0) {
                    Goods goods = dataService.getRowById(goodsId, Goods.class);
                    if (prf.getAwardQuantity() != null && goods != null) {
                        prf.setAwardAmount(prf.getAwardQuantity() * goods.getPrice());
                    } else {
                        prf.setAwardAmount(0);
                    }

                } else if (isOther == 1) {
                    PromotionAwardOther goods = dataService.getRowById(goodsId, PromotionAwardOther.class);
                    if (prf.getAwardQuantity() != null && goods != null) {
                        prf.setAwardAmount(prf.getAwardQuantity() * goods.getPrice());
                    } else {
                        prf.setAwardAmount(0);
                    }
                }
            } else {
            	int awardId = (Integer) hm.get("awardId");
            	if(awardId == 3){
                    prf.setAwardQuantity((Integer) hm.get("awardQuantity"));
                    prf.setAwardAmount((Integer) hm.get("awardAmount"));
                    
//                    int amount = (Integer) hm.get("awardAmount");
//                    if(amount > 0){
//                        prf.setAwardQuantity(amount);
//                    }else{
//            		int boiSo = 0;
//            		if(hm.get("amount") != null && hm.get("promotionQuantity") != null && (Integer) hm.get("promotionQuantity") != 0){
//                            boiSo = ((Integer) hm.get("amount"))/((Integer) hm.get("promotionQuantity"));
//            		}        			
//                            prf.setAwardQuantity((Integer) hm.get("proAwardQuantity")*boiSo);
//                    }           		
            	}else{
                    if(hm.get("amount") != null){
                        prf.setAwardAmount((Integer) hm.get("amount"));
                    }else{
                        prf.setAwardAmount((Integer) hm.get("awardAmount"));
                    }
                    prf.setAwardQuantity((Integer) hm.get("awardQuantity"));
                }               
            }
            prf.setAwardName((String) hm.get("awardName"));
            //Set Promotion
            Promotion promotion = new Promotion();
            promotion.setId((Integer) hm.get("promotionId"));
            promotion.setName((String) hm.get("promotionName"));
            promotion.setNote((String) hm.get("promotionNote"));
            promotion.setCreatedAt((Date) hm.get("createdAt"));
            Status status = new Status();
            status.setId((Integer) hm.get("promotionStatusId"));
            status.setName((String) hm.get("promotionStatusName"));
            promotion.setStatuss(status);
            int awardId = (Integer) hm.get("awardId");
            PromotionAward promotionAward = new PromotionAward();
            promotionAward.setId(awardId);
            promotion.setPromotionAwards(promotionAward);

            UserRole userRole = new UserRole();
            userRole.setName((String) hm.get("approveRolesName"));
            promotion.setApproveRoles(userRole);
            prf.setPromotions(promotion);
            //set Status for prf
            int statusId = (Integer) hm.get("statusId");
            if (statusId == 30) {
                statusId = 23;
            }
            status.setName((String) hm.get("statusName"));
            if (statusId == 24) {
                status.setName("Chờ giao khuyến mãi");
            }
            if(statusId == 27){
            	int updatedUser = (Integer) hm.get("updatedUser");
            	if(updatedUser == 0){
            		updatedUser = (Integer) hm.get("createdUser");
            	}
            	ParameterList parameterList = new ParameterList(1,1);
            	parameterList.add("users.id", updatedUser);
            	List<UserRoleChannel> listUser = dataService.getListOption(UserRoleChannel.class, parameterList);
            	if(!listUser.isEmpty()){
            		String userRoleGiao = listUser.get(0).getUserRoles().getName();
            		status.setName("Đã giao bởi "+ userRoleGiao);
            	}
            }
            status.setId(statusId);

            prf.setStatuss(status);
            prf.setNote((String) hm.get("note"));
            if(statusId == 27){
            	prf.setNote("");
            }
            prf.setCreatedAt((Date) hm.get("createdAt"));
            lists.add(prf);
        }

        return lists;
    }

    @RequestMapping(value = "/listsalepoint/edit/{id}")
    public String editPromotion(Model uiModel, HttpServletRequest request,
            @PathVariable(value = "id") Integer id) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        //GET PromotionTransRetails

        PromotionTransRetailer promotionTransRetailer = dataService.getRowById(id, PromotionTransRetailer.class);
        //Get data here
        int statusId = promotionTransRetailer.getStatuss().getId();
        if (statusId == 24) {
        	promotionTransRetailer.getStatuss().setName("Chờ giao khuyến mãi");
        } else if(statusId == 27){
        	int updatedUser = promotionTransRetailer.getUpdatedUser();
        	if(updatedUser == 0){
        		updatedUser = promotionTransRetailer.getCreatedUser();
        	}
        	ParameterList parameterList = new ParameterList(1,1);
        	parameterList.add("users.id", updatedUser);
        	List<UserRoleChannel> listUser = dataService.getListOption(UserRoleChannel.class, parameterList);
        	if(!listUser.isEmpty()){
        		String userRoleGiao = listUser.get(0).getUserRoles().getName();
        		promotionTransRetailer.getStatuss().setName("Đã giao bởi "+ userRoleGiao);
        	}
        }
        if(promotionTransRetailer.getAwardQuantity() == null){
        	promotionTransRetailer.setAwardQuantity(0);
        }
        if(promotionTransRetailer.getIsOther() != null){
        	 if (promotionTransRetailer.getIsOther() == 0) {
                 Goods goods = dataService.getRowById(promotionTransRetailer.getAwardGoodsId(), Goods.class);
                 if (goods != null) {
                	 promotionTransRetailer.setAwardAmount(promotionTransRetailer.getAwardQuantity() * goods.getPrice());
                 } else {
                	 promotionTransRetailer.setAwardAmount(0);
                 }
             } else {
                 PromotionAwardOther goods = dataService.getRowById(promotionTransRetailer.getAwardGoodsId(), PromotionAwardOther.class);
                 if (goods != null) {
                	 promotionTransRetailer.setAwardAmount(promotionTransRetailer.getAwardQuantity() * goods.getPrice());
                 } else {
                	 promotionTransRetailer.setAwardAmount(0);
                 }
             }
        }else{
        	int awardId = promotionTransRetailer.getPromotions().getPromotionAwards().getId();
        	if(awardId == 3){
        		//promotionTransRetailer.setAwardName("Giảm tiền");
        		promotionTransRetailer.setAwardQuantity(null);
//        		if(promotionTransRetailer.getAwardAmount() == null){
//        			promotionTransRetailer.setAwardAmount(0);
//        		}
        	}else if(awardId == 4){
        		promotionTransRetailer.setAwardName("Chiết khấu: " + promotionTransRetailer.getAwardQuantity() + "%");
        		promotionTransRetailer.setAwardQuantity(null);
//        		if(promotionTransRetailer.getAmount() != null){
//        			promotionTransRetailer.setAwardAmount(promotionTransRetailer.getAmount()*promotionTransRetailer.getPromotions().getQuantity()/100);
//        		}else{
//        			promotionTransRetailer.setAwardAmount(0);
//        		}
        	}
        }
      //  if(pro)
        List<PromotionGoodsRef> listGoods = dataService.getListOption(PromotionGoodsRef.class, new ParameterList("promotions.id", promotionTransRetailer.getPromotions().getId()));
        if(!listGoods.isEmpty()){
        	uiModel.addAttribute("listGoods", listGoods);
        }
        if(promotionTransRetailer.getPromotions().getApplyScope() != 0){
        	List<PromotionChannel> listChannels = dataService.getListOption(PromotionChannel.class, new ParameterList("promotions.id", promotionTransRetailer.getPromotions().getId()));
        	List<Channel> regions = new ArrayList<>();
        	List<Channel> asm = new ArrayList<>();
        	List<Channel> nhaPP = new ArrayList<>();
        	List<Integer> listChannelId = new ArrayList<>();
        	for(PromotionChannel pc : listChannels){
        		String fullCode = pc.getChannels().getFullCode();
        		int size = fullCode.split("_").length;
        		if(size == 1){
        			if(!isChannelExist(regions, pc.getChannels().getId())){
        				regions.add(pc.getChannels());
        			}
        			
        		}else if(size == 2){
        			if(!isChannelExist(regions, pc.getChannels().getParents().getId())){
        				regions.add(pc.getChannels().getParents());
        			}
        			if(!isChannelExist(asm, pc.getChannels().getId())){
        				asm.add(pc.getChannels());
        			}
        			
        		}else if(size == 3){
        			if(!isChannelExist(regions, pc.getChannels().getParents().getParents().getId())){
        				regions.add(pc.getChannels().getParents().getParents());
        			}
        			if(!isChannelExist(asm, pc.getChannels().getParents().getId())){
        				asm.add(pc.getChannels().getParents());
        			}        			
        		}else if(size == 4){
        			if(!isChannelExist(regions, pc.getChannels().getParents().getParents().getParents().getId())){
        				regions.add(pc.getChannels().getParents().getParents().getParents());
        			} 
        			if(!isChannelExist(asm, pc.getChannels().getParents().getParents().getId())){
        				asm.add(pc.getChannels().getParents().getParents());
        			}        			
        			if(!isChannelExist(nhaPP, pc.getChannels().getId())){
        				nhaPP.add(pc.getChannels());
        			}
        		}
        		listChannelId.add(pc.getChannels().getId());
        	}
        	 uiModel.addAttribute("listRegions", regions);
        	 uiModel.addAttribute("listASMs", asm);
        	 uiModel.addAttribute("listNPPs", nhaPP);
        	 ParameterList parameterList = new ParameterList();
        	 parameterList.in("channels.id", listChannelId);;
        	 List<ChannelLocation> listChannelLocation = dataService.getListOption(ChannelLocation.class, parameterList);
        	 List<Location> listTinhThanh = new ArrayList<>();
        	 for(ChannelLocation cl : listChannelLocation){
        		 int locationType = cl.getLocations().getLocationType();
        		 if(locationType == 1){
        			 if(!isLocationIdExist(listTinhThanh, cl.getLocations().getId())){
        				 listTinhThanh.add(cl.getLocations());
        			 }
        			 
        		 }else if(locationType == 2){
        			 if(!isLocationIdExist(listTinhThanh, cl.getLocations().getParents().getId())){
        				 listTinhThanh.add(cl.getLocations().getParents());
        			 }
        			 
        		 }else if(locationType == 3){
        			 if(!isLocationIdExist(listTinhThanh, cl.getLocations().getParents().getParents().getId())){
        				 listTinhThanh.add(cl.getLocations().getParents().getParents());
        			 }        			
        		 }
        	 }
        	 uiModel.addAttribute("listTinhThanh", listTinhThanh);
        }
        uiModel.addAttribute("itemObj", promotionTransRetailer);

        return "ctkm_salepoint_edit";
    }

    private boolean isLocationIdExist(List<Location> list, int locationId){
    	boolean _bool = false;
    	for(Location location : list){
    		int localId = location.getId();
    		if(localId == locationId){
    			_bool = true;
    			break;
    		}
    	}
    	return _bool;
    }
    
    private boolean isChannelExist(List<Channel> list, int channelId){
    	boolean _bool = false;
    	for(Channel channel : list){
    		int clId = channel.getId();
    		if(clId == channelId){
    			_bool = true;
    			break;
    		}
    	}
    	return _bool;
    }
    @RequestMapping(value = "/salepointapprovelist")
    public String ctkmApproveSalesPointList(Model uiModel, HttpServletRequest request,
            @ModelAttribute("ctkmForm") PromotionFilter promotionFilter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        //
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;

        MsalesLoginUserInf userLogin = LoginContext.getLogin(request);

        promotionFilter.processFilter(uiModel, request, serviceFilter, promotionService, dataService, LoginContext.getLogin(request).getCompanyId());
        boolean approve = true;
        uiModel.addAttribute("showCbbPromotion", true);
        uiModel.addAttribute("showCbbStatus", true);
        //Get status
        String hqlStatus = "SELECT S.id as id, S.name as name FROM Status as S"
                + " WHERE deletedUser = 0 AND statusTypes.deletedUser = 0"
                + " AND statusTypes.id = 9";
        if (approve) {
            hqlStatus += " AND id not in (26,27)";
        } else {
            hqlStatus += " AND id IN (23,30) ";
        }
        List<HashMap> listStatus = dataService.executeSelectHQL(HashMap.class, hqlStatus, true, 0, 0);
        List<OptionItem> statusLists = new ArrayList<>();
        for (HashMap status : listStatus) {
            OptionItem optionItem = new OptionItem();
            int statusId = (Integer) status.get("id");

            if (statusId == 30) {
                //optionItem.setName("Đang chờ duyệt");
            } else {
                optionItem.setId(statusId);
                optionItem.setName((String) status.get("name"));
                if(statusId == 24){
                	optionItem.setName("Duyệt KM");
                }else if(statusId == 29){
                	optionItem.setName("Chờ Duyệt KM");
                }
                statusLists.add(optionItem);
            }
            
        }

        uiModel.addAttribute("listStatus", statusLists);

        promotionService.initDataForPromotionCheck(request, userLogin, uiModel, approve);

        MsalesResults<HashMap> results = promotionService.getHqlStringForSearchPromotion(request, promotionFilter, uiModel, userLogin, approve, page, size);
        List<HashMap> listPTR = results.getContentList();
        List<PromotionTransRetailer> lists = returnResultFromListHashMap(listPTR);
        uiModel.addAttribute("ctkmSalePointList", lists);
        maxPages = (int) (long) results.getCount();
        if (maxPages % size != 0) {
            maxPages = maxPages / size + 1;
        } else {
            maxPages = maxPages / size;
        }

        //Thêm dữ liệu cho Model
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);
        return "ctkm_salepoint_approve_list";
    }

    @Autowired
    View jsonView;

    @RequestMapping(value = "/listsalepoint/reject", method = RequestMethod.POST)
    public ModelAndView deleteChannelType(@RequestParam("id") Integer id,
            HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        MsalesLoginUserInf user = LoginContext.getLogin(request);
        String status = "OK";
        //Lấy dữ liệu
        PromotionTransRetailer promotionTransRetailer = dataService.getRowById(id, PromotionTransRetailer.class);
        if (promotionTransRetailer == null) {
            status = "NOT OK";
            return new ModelAndView(jsonView, "status", status);
        }
        Status status2 = new Status();
        status2.setId(27);
        promotionTransRetailer.setStatusId(27);
        promotionTransRetailer.setStatuss(status2);
        promotionTransRetailer.setNote("Giao bởi " + user.getUsername());
        promotionTransRetailer.setUpdatedUser(user.getId());

        try {
            int ret = dataService.updateSync(promotionTransRetailer);
            if (ret < 0) {
                status = "NOT OK";
            }
        } catch (Exception ex) {
            status = "NOT OK";
        }

        return new ModelAndView(jsonView, "status", status);
    }

    @RequestMapping(value = "/listsalepoint/approve")
    public String ctkmApprove(Model uiModel, HttpServletRequest request, RedirectAttributes redirectAttributes,
            @ModelAttribute("ctkmForm") PromotionFilter promotionFilter, 
            Integer page,  Integer size) {

        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
       
        if(page == null){
        	page = 1;
        }
        if(size == null){
        	size = 10;
        }
        ArrayList arrayList = new ArrayList();
        for (PromotionTransRetailer accumulationRetailers : promotionFilter.getPromotionTransRetailers()) {
            PromotionTransRetailer accumulationRetailer = dataService.getRowById(accumulationRetailers.getId(), PromotionTransRetailer.class);
            int statusId = accumulationRetailers.getStatuss().getId();
            if (statusId != 23 && statusId != accumulationRetailer.getStatuss().getId()) {
                accumulationRetailer.setStatuss(accumulationRetailers.getStatuss());
                if (accumulationRetailers.getNote() != null && !accumulationRetailers.getNote().isEmpty()) {
                    accumulationRetailer.setNote(accumulationRetailers.getNote());
                }
                accumulationRetailer.setUpdatedAt(new Date());
                accumulationRetailer.setUpdatedUser(loginUserInf.getId());
                arrayList.add(accumulationRetailer);
            }

        }
        boolean ret = true;
        if(!arrayList.isEmpty()){
        	ret = dataService.updateArray(arrayList);
        }        
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        if(!arrayList.isEmpty()){
        	if (ret) {
                redirectAttributes.addFlashAttribute("update", true);                     
            } else {
                redirectAttributes.addFlashAttribute("update", false);
            }
        }        
        if(page == 1){
       	 return "redirect:/promotion/salepointapprovelist";
        }else{
       	 return "redirect:/promotion/salepointapprovelist?page="+ page+"&size="+size;
        }
    }

    @RequestMapping(value = "/rp/list")
    public String list_rp(Model uiModel, HttpServletRequest request,
            @ModelAttribute("ctkmForm") PromotionFilter promotionFilter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        promotionFilter.processFilter(uiModel, request, serviceFilter, promotionService, dataService, LoginContext.getLogin(request).getCompanyId());
        MsalesResults<Promotion> list = promotionService.searchPromotionRP(promotionFilter, LoginContext.getLogin(request).getCompanyId(), dataService, page, size);
        for (Promotion promotion : list.getContentList()) {
            promotion.generateConditionString();
            List<Goods> goodsList = promotionService.getListPromotionGoods(promotion.getId(), dataService);
            promotion.setGoodsList(goodsList);
        }

        uiModel.addAttribute("listPromotion", list.getContentList());
        uiModel.addAttribute("now", new Date());
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("register", true);

        uiModel.addAttribute("maxPages", Filter.calulatorMaxPages(list.getCount().intValue(), size));

        return "ctkm_list_rp";
    }

    @RequestMapping(value = "/rp/export01")
    public void exportPromotion01(HttpServletRequest request, HttpServletResponse response,
            Model uiModel, @ModelAttribute("ctkmForm") PromotionFilter promotionFilter) {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        try {
//            paramList.add("promotions.companys.id", loginUserInf.getCompanyId());
            
            String hql = "SELECT PTR.poss.id as posId, PTR.poss.name as posName, PTR.poss.address as address,"
            		+ " PTR.poss.tel as tel, PTR.poss.mobile as mobile,"
            		
            		+ " PTR.statuss.id as statusId, PTR.statuss.name as statusName, PTR.quantity as quantity, PTR.amount as amount, PTR.note as note, "
            		+ " PTR.awardQuantity as awardQuantity, PTR.awardAmount as awardAmount, PTR.awardName as awardName,"
            		+ " PTR.isOther as isOther, PTR.awardGoodsId as awardGoodsId, PTR.createdAt as createdAt,"
            		
 					+ " PTR.promotions.id as promotionId, PTR.promotions.name as promotionName,PTR.promotions.isRegister as isRegister, "
 					+ " PTR.promotions.promotionAwards.promotionAwardName as promotionAwardName,"
 					+ " PTR.promotions.proAwardQuantity as proAwardQuantity, PTR.promotions.promotionAwards.id as awardId,"
 					+ " PTR.promotions.conditionQuantity as conditionQuantity, PTR.promotions.goodsCategorys.name as goodsCategoryName"
 					+ " FROM PromotionTransRetailer as PTR"
 					+ " WHERE PTR.promotions.deletedUser = 0 AND PTR.deletedUser = 0"
 					+ " AND PTR.promotions.companys.id = " + loginUserInf.getCompanyId();
            hql += " GROUP BY PTR.id ORDER BY PTR.id desc";
            List<HashMap> listPTRs = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
          //  List<PromotionTransRetailer> lists = dataService.getListOption(PromotionTransRetailer.class, paramList);
            fullPath = servletContext.getRealPath("") + "/" +  filePath + FILE_NAME + ".jasper";
            String token = exportsService.generate();
            List<MsalesPromotionReport> dataList = new ArrayList<>();                       
            dataList = getListPromotionTransRetailer(listPTRs);
            JRDataSource jrDataSource = null;
            if (dataList.size() > 0) {
                jrDataSource = new JRBeanCollectionDataSource(dataList);
            } else {
                jrDataSource = new JREmptyDataSource();
            }
            
            ReportFilter rpFilter = new ReportFilter();
            rpFilter.setTemplate(fullPath);
            rpFilter.setExportType("xls");
            rpFilter.setFileName(FILE_NAME);
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("ctkmStr", "");

            exportsService.download(rpFilter, parameters, token, jrDataSource, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/rp/export03")
    public void exportConflictCTKM(HttpServletRequest request, HttpServletResponse response,
            Model uiModel, @ModelAttribute("ctkmForm") PromotionFilter promotionFilter) {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        try {
            fullPath = servletContext.getRealPath("") + "/" +  filePath + FILE_NAME_03 + ".jrxml";
            String token = exportsService.generate();

            List<String> columnHeaders = new ArrayList<String>();
            List<Integer> widthHeaders = new ArrayList<Integer>();
            columnHeaders.add("Tên điểm bán");
            widthHeaders.add(200);
            columnHeaders.add("Di động");
            widthHeaders.add(100);
            columnHeaders.add("Điện thoại bàn");
            widthHeaders.add(100);
            columnHeaders.add("Địa chỉ");
            widthHeaders.add(250);

            ParameterList paramList = new ParameterList();
            paramList.add("companys.id", loginUserInf.getCompanyId());
            List<Promotion> listPromotion = dataService.getListOption(Promotion.class, paramList);
            for (Promotion promotion : listPromotion) {
                String proName = promotion.getName();
                columnHeaders.add(proName);
                widthHeaders.add(250);
         //       promotionName += "," + proName;
            }

            List<HashMap> listsPro = promotionService.getPromotionConflict(loginUserInf.getCompanyId());

            List<List<String>> dataList = new ArrayList<>();
            for (HashMap pos : listsPro) {
                List<String> row = new ArrayList<>();
                row.add(pos.get("POS_NAME").toString());
                row.add(pos.get("POS_MOBILE").toString());
                row.add(pos.get("TEL").toString());
                row.add(pos.get("ADDRESS").toString());
                int posId = Integer.parseInt(pos.get("POS_ID").toString());
                List<HashMap> listPoCf = promotionService.getPromotionConflictFollowPOS(posId);
                for (HashMap hash : listPoCf) {
                    int proId = Integer.parseInt(hash.get("PROMOTION_ID").toString());
                    for (Promotion listPromotion1 : listPromotion) {
                        if (listPromotion1.getId() == proId) {
                            row.add("Tham Gia");
                        } else {
                            row.add("");
                        }
                 //       System.err.println(listPromotion1.getId() + "==" + proId);
                    }
                }
                dataList.add(row);
            }
            if(dataList.isEmpty()){
            	List<String> row = new ArrayList<>();
                row.add("  ");
                row.add("  ");
                row.add("  ");
                row.add("  ");
                int sizeListPromotions = listPromotion.size();
                for (int i=0; i < sizeListPromotions; i++) {
                	row.add("  ");
                }
                dataList.add(row);
            }
            ReportFilter rpFilter = new ReportFilter();
            rpFilter.setTemplate(fullPath);
            rpFilter.setExportType("xls");
            rpFilter.setFileName(FILE_NAME_03);
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("ctkmStr", "");// promotionName);
            exportsService.download(rpFilter, parameters, token, columnHeaders, widthHeaders, dataList, response);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private List<MsalesPromotionReport> getListPromotionTransRetailer(List<HashMap> lits) {
        List<MsalesPromotionReport> proReportList = new ArrayList<>();
        try {
            for (HashMap proTransRetailer : lits) {
                MsalesPromotionReport promotionReport = new MsalesPromotionReport();
                promotionReport.setPosId((long)(Integer) proTransRetailer.get("posId"));
                promotionReport.setPosName((String) proTransRetailer.get("posName"));
                //promotionReport.setGoodsNameG(proTransRetailer.getAwardGoodss().getName());
                int quantity = 0;
                if(proTransRetailer.get("quantity") != null){
                	quantity = (int) proTransRetailer.get("quantity");
                }
                promotionReport.setGoodsQuantity((long) (Integer)quantity);
                long amount = 0;
                if(proTransRetailer.get("amount") != null){
                	amount = (long)(Integer) proTransRetailer.get("amount");
                }
                promotionReport.setGoodsAmount(amount);
                promotionReport.setAddress( (String)proTransRetailer.get("address"));                
                promotionReport.setAwardName( (String)proTransRetailer.get("awardName"));               
                promotionReport.setCategoryId((long) 1);
                promotionReport.setCategoryName((String) proTransRetailer.get("goodsCategoryName"));
                //promotionReport.setConditionQuantity((long)(Integer) proTransRetailer.get("conditionQuantity"));
                promotionReport.setCreateDate((Date) proTransRetailer.get("createdAt"));
                int promotionId = (int) proTransRetailer.get("promotionId");
                //set group name goods
                String hqlGoods = "SELECT PGR.goodss.name as goodsName FROM PromotionGoodsRef as PGR"
                		+ " WHERE deletedUser = 0 and promotions.id = "+ promotionId;
                List<HashMap> listGoodsName = dataService.executeSelectHQL(HashMap.class, hqlGoods, true, 0, 0);
                String goodsNameGroup = "";
                int i =0;
                for(HashMap hm : listGoodsName){
                	if(i == listGoodsName.size() -1){
                		goodsNameGroup += hm.get("goodsName");
                		break;
                	}
                	goodsNameGroup += hm.get("goodsName") + ", ";
                	i++;
                }
                promotionReport.setGoodsNameG(goodsNameGroup);
                
                promotionReport.setCtkmId((long) (Integer) promotionId);
                promotionReport.setCtkmName((String) proTransRetailer.get("promotionName"));
                Integer awardQuantity = (Integer) proTransRetailer.get("proAwardQuantity");
                String awardName = "";
                long award = 0, revenue = 0;
                if(proTransRetailer.get("awardAmount") != null){
                	revenue = (long)(Integer) proTransRetailer.get("awardAmount");
                }
                if (proTransRetailer.get("isOther") != null) {                	        	
                    int isOther = 0, goodsId = 0;
                    if(proTransRetailer.get("awardQuantity") != null){
                    	award = (int) proTransRetailer.get("awardQuantity");
                    }
                    try {
                        isOther = (Integer) proTransRetailer.get("isOther");
                        goodsId = (Integer) proTransRetailer.get("awardGoodsId");
                    } catch (Exception ex) {
                    }
                    if (isOther == 0) {
                        Goods goods = dataService.getRowById(goodsId, Goods.class);
                        revenue = award*goods.getPrice();
                        awardName = goods.getName();
                    } else if (isOther == 1) {
                        PromotionAwardOther goods = dataService.getRowById(goodsId, PromotionAwardOther.class);
                        revenue = award*goods.getPrice();
                        awardName = goods.getName();
                    }
                } else {
                	int awardId = (Integer) proTransRetailer.get("awardId");            	
                	if(awardId == 3){
                		awardName = "Chiết khấu giảm tiền";
                		award = awardQuantity;               		
                	}else if(awardId == 4){
                		awardName = "Chiết khấu % trên doanh số";
                		award = awardQuantity;
                	}               	
                }
                promotionReport.setAwardName(awardName);
                promotionReport.setQuantity(award);
                promotionReport.setRevenue(revenue);
                
                promotionReport.setFlagTrans(2L);

                promotionReport.setKm((String) proTransRetailer.get("promotionAwardName"));
                promotionReport.setMobi((String) proTransRetailer.get("mobile"));
                promotionReport.setNote((String) proTransRetailer.get("promotionNote"));
                promotionReport.setOrderId((long)1);
                promotionReport.setReason((String) proTransRetailer.get("note"));
                promotionReport.setSalemanId((long) 1);
                int statusId = (int) proTransRetailer.get("statusId");
                if(statusId == 27){
                	promotionReport.setStatus((long) 1);
                }else{
                	promotionReport.setStatus((long) 0);
                }
              //  promotionReport.setStatusName((String) proTransRetailer.get("statusName"));
                if(proTransRetailer.get("isRegister") != null){
                    int isRegister = (int) proTransRetailer.get("isRegister");
                    if(isRegister == 0){
                        promotionReport.setConditionQuantity(0L);
                    }else{
                        promotionReport.setConditionQuantity(1L);
                    }
                }else{
                    promotionReport.setConditionQuantity(0L);
                }
                if(statusId == 24){
                    promotionReport.setStatusName("Chờ giao khuyến mãi");
                }else{
                    promotionReport.setStatusName((String) proTransRetailer.get("statusName"));
                }
                
                promotionReport.setTel((String) proTransRetailer.get("tel"));
            //    promotionName += proTransRetailer.getPromotions().getName() + ",";
                proReportList.add(promotionReport);
            }
        } catch (Exception e) {
            System.err.println(">>getListPromotionTransRetailer: " + e.getMessage());
        }
        return proReportList;
    }
}
