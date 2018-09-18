/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.promotion.service;

import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.Promotion;
import vn.itt.msales.entity.PromotionAccumulationRetailer;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.promotion.model.PromotionAccumlulateFilter;

/**
 *
 * @author vtm_2
 */
@Service
public interface PromotionAccumulateService {
   public  MsalesResults<PromotionAccumulationRetailer> SearchPromotion(int companyId ,PromotionAccumlulateFilter promotionAccumlulateFilter ,MsalesPageRequest pageRequest );
   
   public MsalesResults<PromotionAccumulationRetailer> SearchPromotionKD (int companyId,PromotionAccumlulateFilter promotionAccumlulateFilter ,MsalesPageRequest pageRequest);
   public List<Goods> GoodsPromotion (int promotionId);
   public List<Channel> ChannelPromotion(int promotionId);
   public List<Promotion> ListPromotion(int companyId);
}
