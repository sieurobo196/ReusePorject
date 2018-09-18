/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.promotion.model;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.PromotionAccumulationRetailer;
import vn.itt.msales.promotion.service.PromotionService;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;

/**
 *
 * @author vtm_2
 */
public class PromotionAccumlulateFilter {
    private List<List<Integer>> channelIdList;
    private List<List<OptionItem>> channelList;
    private int promotionId;
    private int promotionConditionId;
    private List<Integer> promotionConditionListId;
    private int statusId;
    private int userRoleId;
    private int goodsCategoryId;
    private int goodsId;
    private String beginDate;
    private String endDate;
    private String note;
    private int promotionRangWardId;
    private List<Integer> promotionWardId;
    private int calculation;
    private int step;
    private Filter filter;
    private List<Integer> goodsIdList;

    public List<Integer> getGoodsIdList() {
        return goodsIdList;
    }

    public void setGoodsIdList(List<Integer> goodsIdList) {
        this.goodsIdList = goodsIdList;
    }

    public List<List<Integer>> getChannelIdList() {
        return channelIdList;
    }

    public void setChannelIdList(List<List<Integer>> channelIdList) {
        this.channelIdList = channelIdList;
    }

    public List<List<OptionItem>> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<List<OptionItem>> channelList) {
        this.channelList = channelList;
    }
    
    
    
    private List<PromotionAccumulationRetailer> promotionAcculate;

    public List<PromotionAccumulationRetailer> getPromotionAcculate() {
        return promotionAcculate;
    }

    public void setPromotionAcculate(List<PromotionAccumulationRetailer> promotionAcculate) {
        this.promotionAcculate = promotionAcculate;
    }
    
    
    public PromotionAccumlulateFilter() {
    }

    
    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
  
    public int getPromotionRangWardId() {
        return promotionRangWardId;
    }

    public void setPromotionRangWardId(int promotionRangWardId) {
        this.promotionRangWardId = promotionRangWardId;
    }

    public List<Integer> getPromotionWardId() {
        return promotionWardId;
    }

    public void setPromotionWardId(List<Integer> promotionWardId) {
        this.promotionWardId = promotionWardId;
    }

   
    
    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public int getPromotionConditionId() {
        return promotionConditionId;
    }

    public void setPromotionConditionId(int promotionConditionId) {
        this.promotionConditionId = promotionConditionId;
    }

    public List<Integer> getPromotionConditionListId() {
        return promotionConditionListId;
    }

    public void setPromotionConditionListId(List<Integer> promotionConditionListId) {
        this.promotionConditionListId = promotionConditionListId;
    }

    
    
    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(int userRoleId) {
        this.userRoleId = userRoleId;
    }

    public int getGoodsCategoryId() {
        return goodsCategoryId;
    }

    public void setGoodsCategoryId(int goodsCategoryId) {
        this.goodsCategoryId = goodsCategoryId;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

   

    public int getCalculation() {
        return calculation;
    }

    public void setCalculation(int calculation) {
        this.calculation = calculation;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
      public Integer getlevel() {
        Integer level = 0;
        if (channelIdList != null) {
            for (int i = channelIdList.size()-1;i>=0;i--) {
                if (channelIdList.get(i) != null && channelIdList.get(i).size() > 0) {
                    level = i+1;
                    break;
                }
            }
        }
        return level;
    }
    
    public List<Integer> getChannelIds() {
        List<Integer> ret = new ArrayList<>();
        ret.add(0);
        if (channelIdList != null) {
            for (int i = channelIdList.size() - 1; i > -1; i--) {
                if (channelIdList.get(i) != null && channelIdList.get(i).size() > 0) {
                    ret = channelIdList.get(i);
                    break;
                }
            }
        }
        return ret;
    }
   public void processFilter(Model uiModel, HttpServletRequest request,
            ServiceFilter serviceFilter,
            PromotionService promotionService,
            DataService dataService,
            int companyId) {
        processChannelFilter(uiModel, request, serviceFilter, dataService, companyId);
        uiModel.addAttribute("conditionList", promotionService.getCbListPromotionCondition(companyId, dataService));
        uiModel.addAttribute("approveRoleList", promotionService.getCbListPromotionApproveRole(dataService));
        uiModel.addAttribute("goodsCategoryList", promotionService.getCbListGoodsCategory(companyId, dataService));
        uiModel.addAttribute("awardList", promotionService.getCbListPromotionAward(dataService));
        uiModel.addAttribute("goodsList", promotionService.getCbListGoodsByGoodsCategoryId(this.goodsCategoryId, companyId, dataService));
        uiModel.addAttribute("quantityTypeList", promotionService.getCbListQuantityType());
        uiModel.addAttribute("promotionTypeList", promotionService.getCbListPromotionType());
        uiModel.addAttribute("registerList", promotionService.getCbListRegister());

    }

    public void processChannelFilter(Model uiModel, HttpServletRequest request,
            ServiceFilter serviceFilter,
            DataService dataService,
            int companyId) {
        if (channelList == null) {
            channelList = new ArrayList<>();
        }
        if (channelIdList == null) {
            channelIdList = new ArrayList<>();
        }

        //get login user
        int userLoginId = LoginContext.getLogin(request).getId();

        OptionItem optionItem = new OptionItem();
        int idx = 1;
        int level = this.getlevel();
        //neu la admin company
        if (LoginContext.getLogin(request).getUserRoleCode().toUpperCase().equals("USER_ROLE_ADMIN_COMPANY")) {
            OptionItem firstOptionItem = serviceFilter.getFirstChannelType(companyId, dataService);
            List<OptionItem> channelFirstList = Filter.createFirstOptionType(firstOptionItem);
            channelFirstList.addAll(serviceFilter.getCbListChannelByChannelType(firstOptionItem.getId(), companyId, dataService));
            channelList.add(channelFirstList);
            optionItem = firstOptionItem;
        } else {
            List<Channel> channelMaxs = serviceFilter.getMaxChannels(userLoginId, dataService);
            if (channelMaxs.isEmpty()) {
                //user not have channel
                List<OptionItem> tempList = new ArrayList<>();
                tempList.add(new OptionItem(0, "-- Chọn kênh --"));
                channelList.add(0, tempList);
                //return new ArrayList();
            }

            //this.maxChannelId = channelMax.getId();
            Channel temp = channelMaxs.get(0).getParents();
            while (temp != null) {
                List<OptionItem> tempList = new ArrayList<>();
                tempList.add(new OptionItem(temp.getId(), temp.getName()));
                channelList.add(0, tempList);
                temp = temp.getParents();
                idx++;
            }

            List<OptionItem> channelFirstList = new ArrayList<>();
            OptionItem firstOptionItem = new OptionItem();
            firstOptionItem.setId(0);
            firstOptionItem.setName("-- " + channelMaxs.get(0).getChannelTypes().getName() + " --");
            channelFirstList.add(firstOptionItem);
            for (Channel channel : channelMaxs) {
                firstOptionItem = new OptionItem(channel.getId(), channel.getName());
                channelFirstList.add(firstOptionItem);
            }

            int firstType = channelMaxs.get(0).getChannelTypes().getId();

            channelList.add(channelFirstList);
            //add channelList child
            OptionItem firstOptionType = new OptionItem(firstType, channelMaxs.get(0).getChannelTypes().getName());
            optionItem = firstOptionType;
        }

        optionItem = serviceFilter.getChannelTypeByParent(optionItem.getId(), dataService);
        while (optionItem != null) {
            List<OptionItem> list = Filter.createFirstOptionType(optionItem);
            if (level >= idx) {
                if (channelIdList != null && channelIdList.get(idx - 1) != null) {//truong hop chon Mien 0 
                    list.addAll(serviceFilter.getCbListChannelByListParentId(channelIdList.get(idx - 1), dataService));
                }
            }
            channelList.add(list);
            optionItem = serviceFilter.getChannelTypeByParent(optionItem.getId(), dataService);
            idx++;
        }

        //set selected
        for (int i = 0; i < channelIdList.size(); i++) {
            if (channelIdList.get(i) != null) {
                for (int selected : channelIdList.get(i)) {
                    for (OptionItem option : channelList.get(i)) {
                        if (option.getId() == selected) {
                            option.setChecked(true);
                            break;
                        }
                    }
                }
            }
        }

        //List<OptionItem> provinceList = OptionItem.NoOptionList(0, "-- Tỉnh/Thành phố --");
        //getLocationByChannelId
        //lay danh sach tinh thanh
        //provinceList.addAll(serviceFilter.getCbListLocationByListChannelId(this.getChannelIds(), dataService));
//        if (this.locationIdList != null) {
//            for (int selected : this.locationIdList) {
//                for (OptionItem option : provinceList) {
//                    if (selected == option.getId()) {
//                        option.setChecked(true);
//                        break;
//                    }
//                }
//            }
//        }
        uiModel.addAttribute("channelList", channelList);
        uiModel.addAttribute("channelIdList", channelIdList);
        //uiModel.addAttribute("provinceList", provinceList);

    }
    
}
