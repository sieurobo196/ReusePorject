/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.promotion.service;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.Promotion;
import vn.itt.msales.entity.PromotionAwardOther;
import vn.itt.msales.entity.PromotionChannel;
import vn.itt.msales.entity.PromotionConditionalRef;
import vn.itt.msales.entity.PromotionGoodsRef;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.promotion.model.PromotionFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author vtm
 */
public interface PromotionService {

    public List<OptionItem> getCbListGoodsCategory(int userId, int compnayId, DataService dataService);

    public List<OptionItem> getCbListPromotionCondition(int companyId, DataService dataService);

    public List<OptionItem> getCbListPromotionStatus(DataService dataService);

    public List<OptionItem> getCbListFullPromotionStatus(DataService dataService);

    public List<OptionItem> getCbListPromotionApproveRole(DataService dataService);

    public List<OptionItem> getCbListGoodsCategory(int companyId, DataService dataService);

    public List<OptionItem> getCbListGoodsByGoodsCategoryId(Integer goodsCategoryId, int companyId, DataService dataService);

    public List<OptionItem> getCbListPromotionAward(DataService dataService);
    
    public List<OptionItem> getCbListUnit(int companyId,DataService dataService);

    public List<OptionItem> getCbListPromotionType();

    public List<OptionItem> getCbListRegister();

    public List<OptionItem> getCbListQuantityType();
    
    //danh sach san pham dduoc tang -- chua xet company
    public List<OptionItem> getCbListPromotionAwardOther(int companyId,DataService dataService);

    //create Pro award other
    public boolean createGoodsAwardOther(PromotionAwardOther promotionAwardOther, DataService dataService);

    public HashMap getGoodsUnitByGoodsId(int goodsId, DataService dataService);
    
    public HashMap getOtherUnitByOtherId(int otherId, DataService dataService);

    //check code promotion
    public boolean checkPromotionCode(String code, int companyId, DataService dataService);

    //get and put all info for promotion
    public Promotion getPromotionFullInfo(int promotionId, int companyId, DataService dataService);

    public List<Channel> getListPromotionChannelId(int promotionId, DataService dataService);

    public List<Integer> getListPromotionGoodsId(int promotionId, DataService dataService);

    public List<Goods> getListPromotionGoods(int promotionId, DataService dataService);

    public List<Integer> getListPromotionConditionalId(int promotionId, DataService dataService);

    public List<PromotionChannel> getListPromotionChannel(int promotionId, DataService dataService);

    public List<PromotionGoodsRef> getListPromotionGoodsRef(int promotionId, DataService dataService);

    public List<PromotionConditionalRef> getListPromotionConditionalRef(int promotionId, DataService dataService);

    public MsalesResults<Promotion> searchPromotion(PromotionFilter filter, int companyId, DataService dataService, int page, int size);

    public void initDataForPromotionCheck(HttpServletRequest request, MsalesLoginUserInf userLogin, Model uiModel, boolean approve);

    public MsalesResults<HashMap> getHqlStringForSearchPromotion(HttpServletRequest request, PromotionFilter promotionFilter, Model uiModel, MsalesLoginUserInf userLogin, boolean approve, int page, int size);

    public MsalesResults<Promotion> searchPromotionRP(PromotionFilter filter, int companyId, DataService dataService, int page, int size);

    public List<HashMap> getPromotionConflict(int companyID);

    public List<HashMap> getPromotionConflictFollowPOS(int posID);

    public List<Promotion> getRunPromotion();

    public List<Promotion> getEndPromotion();
}
