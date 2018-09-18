/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.promotion.controller;

import com.sun.corba.se.impl.naming.cosnaming.InterOperableNamingImpl;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.customercare.model.ReportFilter;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelLocation;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsCategory;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.Promotion;
import vn.itt.msales.entity.PromotionAccumulationRetailer;
import vn.itt.msales.entity.PromotionAward;
import vn.itt.msales.entity.PromotionAwardOther;
import vn.itt.msales.entity.PromotionChannel;
import vn.itt.msales.entity.PromotionConditional;
import vn.itt.msales.entity.PromotionGoodsRef;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.export.services.ExportsService;
import vn.itt.msales.promotion.model.Item;
import vn.itt.msales.promotion.model.MsalesPromotionAccumulateReport;
import vn.itt.msales.promotion.model.MsalesPromotionReport;
import vn.itt.msales.promotion.model.PromotionAccumlulateFilter;
import vn.itt.msales.promotion.model.PromotionFilter;
import vn.itt.msales.promotion.service.PromotionAccumulateService;
import vn.itt.msales.promotion.service.PromotionService;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.controller.MsalesUserController;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author vtm_2
 */
@Controller
public class WebPromotionAccumulateController {

    @Autowired
    DataService dataService;
    @Autowired
    private ServiceFilter serviceFilter;
    @Autowired
    private PromotionAccumulateService accumulateService;
    @Autowired
    private PromotionService promotionService;

    String separator = File.separator;
    private String filePath = String.format("reports%s", separator);
    private final String FILE_NAME = "RPTCTKM02";
    private String fullPath = "";
    private String promotionName = "";
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private ExportsService exportsService;

    @RequestMapping(value = "/promotion/accumulationsalepointlist")
    public String PromotionList(Model uiModel, HttpServletRequest request,
            @ModelAttribute("promotionFilter") @Valid PromotionAccumlulateFilter promotionFilter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        int maxPages = 1;
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        if (promotionFilter.getFilter() == null) {
            Filter filter = new Filter();
            promotionFilter.setFilter(filter);
        }
        promotionFilter.processFilter(uiModel, request, serviceFilter, promotionService, dataService, loginUserInf.getCompanyId());
        List<GoodsCategory> categoryslist = dataService.getListOption(GoodsCategory.class, new ParameterList("companys.id", loginUserInf.getCompanyId()));

        List<Status> listStatus = dataService.getListOption(Status.class, new ParameterList("statusTypes.id", 9));
        List<PromotionAward> listWard = dataService.getListOption(PromotionAward.class, new ParameterList());
        List<Promotion> listPromotion = accumulateService.ListPromotion(loginUserInf.getCompanyId());

        List<PromotionConditional> listPromtionCondition = dataService.getListOption(PromotionConditional.class, new ParameterList());
        List<Goods> goodses = dataService.getListOption(Goods.class, new ParameterList());
        List<Item> calculation = new ArrayList<>();
        calculation.add(new Item("3.Tích lũy số lượng bán", 3));
        calculation.add(new Item("4.Tích lũy doanh số bán", 4));

        List<Item> step = new ArrayList<Item>();
        step.add(new Item("Bình thường", 1));
        step.add(new Item("Bậc thang", 2));

        List<Item> statusApprove = new ArrayList<Item>();
        statusApprove.add(new Item("Đã giao", 27));
        statusApprove.add(new Item("Hoàn thành", 24));
        statusApprove.add(new Item("Tích lũy", 26));
        statusApprove.add(new Item("Bị loại", 25));

//        List<PromotionAccumulationRetailer> list =dataService.getListOption(PromotionAccumulationRetailer.class, new ParameterList());
        MsalesPageRequest pageRequest = new MsalesPageRequest(page, size);
        MsalesResults<PromotionAccumulationRetailer> accumulationRetailers = accumulateService.SearchPromotion(loginUserInf.getCompanyId(), promotionFilter, pageRequest);
        List<PromotionAccumulationRetailer> list = accumulationRetailers.getContentList();
        for (PromotionAccumulationRetailer accumulationRetailer : list) {
            Status status = new Status();
            status.setId(accumulationRetailer.getStatuss().getId());
            if (accumulationRetailer.getStatuss().getId() == 27) {

                int updateUser = accumulationRetailer.getUpdatedUser();
                if (updateUser == 0) {
                    updateUser = accumulationRetailer.getCreatedUser();
                }
                ParameterList parameterList = new ParameterList(1, 1);
                parameterList.add("users.id", updateUser);
                List<UserRoleChannel> listUser = dataService.getListOption(UserRoleChannel.class, parameterList);
                if (!listUser.isEmpty()) {
                    String userRoleGiao = listUser.get(0).getUserRoles().getName();
                    status.setName("Đã giao bởi " + userRoleGiao);
                }
            } else if (accumulationRetailer.getStatuss().getId() == 24) {
                status.setName("Giao khuyến mãi");

            } else {
                status.setName(accumulationRetailer.getStatuss().getName());
            }
            accumulationRetailer.setStatuss(status);
            if (accumulationRetailer.getAwardName()!=null) {
                String AwardName = accumulationRetailer.getAwardName();
               int compare_ds = AwardName.compareToIgnoreCase("Chiết khấu % trên doanh số");
               int compare_Gt=AwardName.compareToIgnoreCase("Chiết khấu giảm tiền");
               if(compare_ds==0){
                   accumulationRetailer.setIsOther(2);
               }
               if(compare_Gt==0){
               accumulationRetailer.setAwardQuantity(accumulationRetailer.getAwardAmount());
               }
            }
        }
        int count = Integer.parseInt(accumulationRetailers.getCount().toString());
        if (count % size != 0) {
            maxPages = count / size + 1;
        } else {
            maxPages = count / size;
        }

        uiModel.addAttribute("step", step);
        uiModel.addAttribute("calculation", calculation);
        uiModel.addAttribute("listPromotionCondition", listPromtionCondition);
        uiModel.addAttribute("listPromotion", listPromotion);
        uiModel.addAttribute("listWard", listWard);
        uiModel.addAttribute("listStatus", statusApprove);
        if (promotionFilter.getGoodsCategoryId()!= 0) {
            uiModel.addAttribute("goodsList", promotionService.getCbListGoodsByGoodsCategoryId(promotionFilter.getGoodsCategoryId(), loginUserInf.getCompanyId(), dataService));
        }
        uiModel.addAttribute("listRole", promotionService.getCbListPromotionApproveRole(dataService));
        uiModel.addAttribute("listCate", promotionService.getCbListGoodsCategory(loginUserInf.getCompanyId(), dataService));
        uiModel.addAttribute("listPromotionAccumulate", list);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);
        return "ctkm_salepointaccumulation_list";
    }

    @RequestMapping(value = "/promotion/accumulationsalepointapprovelist")
    public String PromotionApproveList(Model uiModel, HttpServletRequest request,
            @ModelAttribute("promotionAccumulate") @Valid PromotionAccumlulateFilter promotionFilter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        int maxPages = 1;
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        if (promotionFilter.getFilter() == null) {
            Filter filter = new Filter();
            promotionFilter.setFilter(filter);
        }
        promotionFilter.processFilter(uiModel, request, serviceFilter, promotionService, dataService, loginUserInf.getCompanyId());

        List<UserRole> listRole = dataService.getListOption(UserRole.class, new ParameterList());
        List<Status> listStatus = dataService.getListOption(Status.class, new ParameterList("statusTypes.id", 9));
        List<PromotionAward> listWard = dataService.getListOption(PromotionAward.class, new ParameterList());
        List<Promotion> listPromotion = accumulateService.ListPromotion(loginUserInf.getCompanyId());

        List<PromotionConditional> listPromtionCondition = dataService.getListOption(PromotionConditional.class, new ParameterList());

        List<Item> calculation = new ArrayList<>();
        calculation.add(new Item("3.Tích lũy số lượng bán", 3));
        calculation.add(new Item("4.Tích lũy doanh số bán", 4));

        List<Item> step = new ArrayList<Item>();
        step.add(new Item("Bình thường", 1));
        step.add(new Item("Bậc thang", 2));

        List<Item> statusApprove = new ArrayList<Item>();
        statusApprove.add(new Item("Đang chờ duyệt", 23));
        statusApprove.add(new Item("Hoàn thành", 24));
       
        statusApprove.add(new Item("Bị loại", 25));
        
       

//        List<PromotionAccumulationRetailer> list =dataService.getListOption(PromotionAccumulationRetailer.class, new ParameterList());
        MsalesPageRequest pageRequest = new MsalesPageRequest(page, size);
        MsalesResults<PromotionAccumulationRetailer> accumulationRetailers = accumulateService.SearchPromotionKD(loginUserInf.getCompanyId(), promotionFilter, pageRequest);
        List<PromotionAccumulationRetailer> list = accumulationRetailers.getContentList();

        int count = Integer.parseInt(accumulationRetailers.getCount().toString());
        if (count % size != 0) {
            maxPages = count / size + 1;
        } else {
            maxPages = count / size;
        }
        
        
        if (promotionFilter.getGoodsCategoryId()!= 0) {
            uiModel.addAttribute("goodsList", promotionService.getCbListGoodsByGoodsCategoryId(promotionFilter.getGoodsCategoryId(), loginUserInf.getCompanyId(), dataService));
        }
        uiModel.addAttribute("step", step);
        uiModel.addAttribute("calculation", calculation);
        uiModel.addAttribute("listPromotionCondition", listPromtionCondition);
        uiModel.addAttribute("listPromotion", listPromotion);
        uiModel.addAttribute("listWard", listWard);
        uiModel.addAttribute("listStatus", statusApprove);
        uiModel.addAttribute("listRole", promotionService.getCbListPromotionApproveRole(dataService));
        uiModel.addAttribute("statusApprove", statusApprove);
        uiModel.addAttribute("listCate", promotionService.getCbListGoodsCategory(loginUserInf.getCompanyId(), dataService));
        uiModel.addAttribute("listPromotionAccumulate", list);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);
        return "ctkm_salepoint_approveaccumulation_list";
    }

    @RequestMapping(value = "/promotion/accumulationsalepointapprovelist/update")
    public String PromotionApproveList(Model uiModel, HttpServletRequest request, RedirectAttributes redirectAttributes,
            @ModelAttribute("promotionAccumulate") PromotionAccumlulateFilter accumlulateFilter) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        ArrayList arrayList = new ArrayList();
        for (PromotionAccumulationRetailer accumulationRetailers : accumlulateFilter.getPromotionAcculate()) {
            PromotionAccumulationRetailer accumulationRetailer = dataService.getRowById(accumulationRetailers.getId(), PromotionAccumulationRetailer.class);
            accumulationRetailer.setStatusId(accumulationRetailers.getStatuss().getId());
            accumulationRetailer.setNote(accumulationRetailers.getNote());
            accumulationRetailer.setUpdatedAt(new Date());
            accumulationRetailer.setUpdatedUser(loginUserInf.getId());
            arrayList.add(accumulationRetailer);
        }
        boolean ret = dataService.updateArray(arrayList);
        if (ret) {
            redirectAttributes.addFlashAttribute("update", true);
            return "redirect:/promotion/accumulationsalepointapprovelist";
        } else {
            redirectAttributes.addFlashAttribute("update", false);
            return "redirect:/promotion/accumulationsalepointapprovelist";
        }

    }

    @RequestMapping(value = "/promotion/deleted/{id}")
    public String deletedPromotionAccum(Model uiModel, HttpServletRequest request,
            @PathVariable("id") int id, PromotionAccumlulateFilter accumlulateFilter) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        PromotionAccumulationRetailer accumulationRetailer = dataService.getRowById(id, PromotionAccumulationRetailer.class);

        accumulationRetailer.setDeletedUser(loginUserInf.getId());
        int ret = dataService.deleteSynch(accumulationRetailer);
        if (ret > 0) {
            return "redirect:/promotion/accumulationsalepointlist";
        } else {
            return "redirect:/promotion/accumulationsalepointlist";
        }

    }

    @RequestMapping(value = "/promotion/delivery/{id}")
    public String deliveryPromotionAccum(Model model, HttpServletRequest request,
            @PathVariable("id") int id, PromotionAccumlulateFilter accumlulateFilter
    ) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        PromotionAccumulationRetailer accumulationRetailer = dataService.getRowById(id, PromotionAccumulationRetailer.class);
        accumulationRetailer.setStatusId(27);
        
        accumulationRetailer.setUpdatedUser(loginUserInf.getId());

        int ret = dataService.updateSync(accumulationRetailer);
        if (ret > 0) {
            return "redirect:/promotion/accumulationsalepointlist";
        } else {
            return "redirect:/promotion/accumulationsalepointlist";
        }
    }

    @RequestMapping(value = "/promotion/view/{id}")
    public String viewPromotion(Model model, HttpServletRequest request,
            @PathVariable("id") int id) {

        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        PromotionAccumulationRetailer promotionAccumulationRetailer = dataService.getRowById(id, PromotionAccumulationRetailer.class);
        int statusId = promotionAccumulationRetailer.getStatuss().getId();
         if (statusId == 24) {
        	promotionAccumulationRetailer.getStatuss().setName("Chờ giao khuyến mãi");
        } else if(statusId == 27){
        	int updatedUser = promotionAccumulationRetailer.getUpdatedUser();
        	if(updatedUser == 0){
        		updatedUser = promotionAccumulationRetailer.getCreatedUser();
        	}
        	ParameterList parameterList = new ParameterList(1,1);
        	parameterList.add("users.id", updatedUser);
        	List<UserRoleChannel> listUser = dataService.getListOption(UserRoleChannel.class, parameterList);
        	if(!listUser.isEmpty()){
        		String userRoleGiao = listUser.get(0).getUserRoles().getName();
        		promotionAccumulationRetailer.getStatuss().setName("Đã giao bởi "+ userRoleGiao);
        	}
        }
         List<PromotionGoodsRef> listGoods = dataService.getListOption(PromotionGoodsRef.class, new ParameterList("promotions.id", promotionAccumulationRetailer.getPromotions().getId()));
        if(!listGoods.isEmpty()){
        	model.addAttribute("listGoods", listGoods);
        }
          if(promotionAccumulationRetailer.getAwardQuantity() == null){
        	promotionAccumulationRetailer.setAwardQuantity(0);
        }
        if(promotionAccumulationRetailer.getIsOther() != null){
         if (promotionAccumulationRetailer.getIsOther() == 0) {
                 Goods goods = dataService.getRowById(promotionAccumulationRetailer.getAwardGoodsId(), Goods.class);
                 if (goods != null) {
                	 promotionAccumulationRetailer.setAwardAmount(promotionAccumulationRetailer.getAwardQuantity() * goods.getPrice());
                 } else {
                	 promotionAccumulationRetailer.setAwardAmount(0);
                 }
             } else {
                 PromotionAwardOther goods = dataService.getRowById(promotionAccumulationRetailer.getAwardGoodsId(), PromotionAwardOther.class);
                 if (goods != null) {
                	 promotionAccumulationRetailer.setAwardAmount(promotionAccumulationRetailer.getAwardQuantity() * goods.getPrice());
                 } else {
                	 promotionAccumulationRetailer.setAwardAmount(0);
                 }
             }
        }else{
        	int awardId = promotionAccumulationRetailer.getPromotions().getPromotionAwards().getId();
        	if(awardId == 3){
        		promotionAccumulationRetailer.setAwardName("Giảm tiền");
        		promotionAccumulationRetailer.setAwardQuantity(null);
        		if(promotionAccumulationRetailer.getAwardAmount() == null){
        			promotionAccumulationRetailer.setAwardAmount(0);
        		}
        	}else if(awardId == 4){
        		promotionAccumulationRetailer.setAwardName("Chiết khấu " + promotionAccumulationRetailer.getAwardQuantity()+ "%");
        		promotionAccumulationRetailer.setAwardQuantity(null);
        	}
        }
    
        if(promotionAccumulationRetailer.getPromotions().getApplyScope() != 0){
        	List<PromotionChannel> listChannels = dataService.getListOption(PromotionChannel.class, new ParameterList("promotions.id", promotionAccumulationRetailer.getPromotions().getId()));
        	List<Channel> regions = new ArrayList<>();
        	List<Channel> asm = new ArrayList<>();
        	List<Channel> nhaPP = new ArrayList<>();
        	List<Integer> listChannelId = new ArrayList<>();
        	for(PromotionChannel pc : listChannels){
        		String fullCode = pc.getChannels().getFullCode();
        		int size = fullCode.split("_").length;
        		if(size == 1){
        			regions.add(pc.getChannels());
        		}else if(size == 2){
        			regions.add(pc.getChannels().getParents());
        			asm.add(pc.getChannels());
        		}else if(size == 3){
        			regions.add(pc.getChannels().getParents().getParents());
        			asm.add(pc.getChannels().getParents());
        		}else if(size == 4){
        			regions.add(pc.getChannels().getParents().getParents().getParents());
        			asm.add(pc.getChannels().getParents().getParents());
        			nhaPP.add(pc.getChannels());
        		}
        		listChannelId.add(pc.getChannels().getId());
        	}
        	 model.addAttribute("listRegions", regions);
        	 model.addAttribute("listASMs", asm);
        	 model.addAttribute("listNPPs", nhaPP);
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
        	 model.addAttribute("listTinhThanh", listTinhThanh);
        }
//        List<Channel> channels = accumulateService.ChannelPromotion(promotionAccumulationRetailer.getPromotions().getId());
//        List<Goods> goodses = accumulateService.GoodsPromotion(promotionAccumulationRetailer.getPromotions().getId());
//
//        promotionAccumulationRetailer.setListGoods(goodses);
//        promotionAccumulationRetailer.setChannelList(channels);
        model.addAttribute("itemObj", promotionAccumulationRetailer);

        return "promotion_salepointaccumulation_view";
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
    @RequestMapping(value = "/promotion/rp/export02")
    public void exportPromotion02(HttpServletRequest request, HttpServletResponse response,
            Model uiModel, @ModelAttribute("ctkmForm") PromotionAccumlulateFilter promotionFilter) {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        try {
            ParameterList paramList = new ParameterList();
            String hql = "SELECT PTR.retailers.id as posId, PTR.retailers.name as posName, PTR.retailers.address as address,"
            		+ " PTR.retailers.tel as tel, PTR.retailers.mobile as mobile,"
            		
            		+ " PTR.statuss.id as statusId, PTR.statuss.name as statusName, PTR.quantity as quantity, PTR.amount as amount, PTR.note as note, "
            		+ " PTR.awardQuantity as awardQuantity, PTR.awardAmount as awardAmount, PTR.awardName as awardName,"
            		+ " PTR.isOther as isOther, PTR.awardGoodsId as awardGoodsId, PTR.createdAt as createdAt,"
            		
 			+ " PTR.promotions.id as promotionId, PTR.promotions.name as promotionName,PTR.promotions.isRegister as isRegister, PTR.promotions.startDate as startDate, "
 			+ " PTR.promotions.promotionAwards.promotionAwardName as promotionAwardName, PTR.promotions.endDate as endDate,"
 			+ " PTR.promotions.proAwardQuantity as proAwardQuantity, PTR.promotions.promotionAwards.id as awardId,"
 			+ " PTR.promotions.conditionQuantity as conditionQuantity, PTR.promotions.goodsCategorys.name as goodsCategoryName"
 			+ " FROM PromotionAccumulationRetailer as PTR"
 			+ " WHERE PTR.promotions.deletedUser = 0 AND PTR.deletedUser = 0"
 			+ " AND PTR.promotions.companys.id = " + loginUserInf.getCompanyId();
            hql += " GROUP BY PTR.id ORDER BY PTR.id desc";
            List<HashMap> listPTRs = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
           // List<PromotionAccumulationRetailer> lists = dataService.getListOption(PromotionAccumulationRetailer.class, paramList);
            fullPath = servletContext.getRealPath("") + "/" + filePath + FILE_NAME + ".jasper";
            String token = exportsService.generate();
            List<MsalesPromotionAccumulateReport> dataList = new ArrayList<>();
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
//            ParameterList paramProList = new ParameterList();
//            paramList.add("companys.id", loginUserInf.getCompanyId());
//            List<Promotion> listPromotion = dataService.getListOption(Promotion.class, paramProList);
//
//            for (Promotion promotion : listPromotion) {
//                String proName = promotion.getName();
//                promotionName += "," + proName;
//            }
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("ctkmStr", "");

            exportsService.download(rpFilter, parameters, token, jrDataSource, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<MsalesPromotionAccumulateReport> getListPromotionTransRetailer(List<HashMap> lits) {
        List<MsalesPromotionAccumulateReport> proReportList = new ArrayList<>();
        try {
            for (HashMap proTransRetailer : lits) {
            	MsalesPromotionAccumulateReport promotionReport = new MsalesPromotionAccumulateReport();
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
               
                promotionReport.setCreateDate((Date) proTransRetailer.get("startDate"));
                promotionReport.setUpdateDate((Date) proTransRetailer.get("endDate"));
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
                
             //   promotionReport.setFlagTrans(2L);

                promotionReport.setKm((String) proTransRetailer.get("promotionAwardName"));
                promotionReport.setMobi((String) proTransRetailer.get("mobile"));
                promotionReport.setNote((String) proTransRetailer.get("promotionNote"));
             //   promotionReport.setOrderId((long)1);
                promotionReport.setReason((String) proTransRetailer.get("note"));
             //   promotionReport.setSalemanId((long) 1);
                int statusId = (int) proTransRetailer.get("statusId");
                if(statusId == 27){
                	promotionReport.setStatus((long) 1);
                }else{
                	promotionReport.setStatus((long) 0);
                }
                
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
