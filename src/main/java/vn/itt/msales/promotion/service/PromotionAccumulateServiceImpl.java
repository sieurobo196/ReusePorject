/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.promotion.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.jfree.util.HashNMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.Promotion;
import vn.itt.msales.entity.PromotionAccumulationRetailer;
import vn.itt.msales.entity.PromotionRangeAward;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.promotion.model.PromotionAccumlulateFilter;
import vn.itt.msales.services.DataService;

/**
 *
 * @author vtm_2
 */
@Service
public class PromotionAccumulateServiceImpl implements PromotionAccumulateService {

    @Autowired
    private DataService dataService;

    public DataService getDataService() {
        return dataService;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public MsalesResults<PromotionAccumulationRetailer> SearchPromotion(int companyId, PromotionAccumlulateFilter promotionAccumlulateFilter, MsalesPageRequest pageRequest) {

//        String hql = "SELECT DISTINCT(PA.id) as id, PA.promotions as promotions, PA.retailers as retailers,PA.approves as approves,PA.statuss as statuss ,"
//                + "PA.awardName as awardName ,PA.awardQuantity as awardQuantity ,PA.awardAmount as awardAmount ,PA.note as note"
//                + " FROM PromotionAccumulationRetailer as PA,Promotion as P"
////                + " , PromotionConditional as PC ,PromotionConditionalRef as PCR,"
////                + " PromotionRangeAward as PRA ,PromotionChannel as PCH "
//                + " WHERE PA.promotions.id=P.id AND PA.statuss.id NOT IN (23)"
//                + " AND PA.deletedUser=0 AND P.deletedUser=0 "
////                + " AND PCH.deletedUser=0 "
////                + " AND PC.deletedUser=0 AND PCR.deletedUser=0 "
//              
//                + " AND P.companys.id =" +companyId;
        
        String hql= "SELECT PA"
//                + " DISTINCT(PA.id) as id,"
//                + " PA.promotions as promotions,"
//                + " PA.retailers as retailers,"
////                + " PA.approves as approves,"
//                + " PA.statuss as statuss,"
//                + " PA.awardName as awardName ,"
//                + " PA.awardQuantity as awardQuantity,"
//                + " PA.awardAmount as awardAmount,"
//                + "PA.amount as amount, "
//                + "PA.note as note "
                + " FROM PromotionAccumulationRetailer as PA,Promotion as P";
                
                String hqlDK = " WHERE PA.promotions.id = P.id "
                + " AND PA.deletedUser =0 "
                + " AND P.deletedUser =0 "
                + " AND PA.statuss.id NOT IN (23,30,25,26)"
                + " AND P.companys.id= " + companyId;
        
        if (promotionAccumlulateFilter.getPromotionId() != 0) {
            int promotionId = promotionAccumlulateFilter.getPromotionId();
            hqlDK += " AND P.id = " + promotionId;
        }
        if (promotionAccumlulateFilter.getPromotionConditionListId()!= null && promotionAccumlulateFilter.getPromotionConditionListId().size()!=0) {
            
            List<Integer> integers = promotionAccumlulateFilter.getPromotionConditionListId();
            hql+=" ,PromotionConditional as PC ,PromotionConditionalRef as PCR ";
            hqlDK += " AND PCR.promotionConditionals.id=PC.id "
                    + " AND PCR.promotions.id = PA.promotions.id"
                    + " AND PCR.promotionConditionals.id IN (";
            for(Integer integer :integers){
            hqlDK+=integer +",";
            }
            hqlDK+="'')";

        }
        //Check ChannelId;
        for(List<OptionItem> optionItems : promotionAccumlulateFilter.getChannelList()){
        	if(optionItems.size() > 1){
        		String string = "";
        		for(OptionItem op : optionItems){
        			string += op.getId() + ",";
        		}
        		string += "''";
        		String hqlChannel = "SELECT C.fullCode as fullCode FROM Channel as C "
        				+ " WHERE deletedUser = 0 and id IN ("+string+")";
        		List<HashMap> listChannel = dataService.executeSelectHQL(HashMap.class, hqlChannel, true, 0, 0);
        		String fullCode = "";
        		for(HashMap hm : listChannel){
        			fullCode += " OR PA.retailers.channels.fullCode LIKE '%"+hm.get("fullCode")+"_%'";
        		}
        		hqlDK += " AND ( PA.retailers.channels.id IN ("+string+")";
        		if(fullCode.length() > 3){
        			hqlDK += fullCode;
        		}
        		hqlDK += " )";
        		break;
        	}
        }
        //Search follow channelIds
        int sizeOfChannelIdList = promotionAccumlulateFilter.getChannelIdList().size();
        if(sizeOfChannelIdList > 0){
        	String string = "";
        	List<Integer> listChannelIds = promotionAccumlulateFilter.getChannelIdList().get(sizeOfChannelIdList - 1);
    		for(Integer op : listChannelIds){
    			string += op + ",";
    		}
    		string += "''";
    		String hqlChannel = "SELECT C.fullCode as fullCode FROM Channel as C "
    				+ " WHERE deletedUser = 0 and id IN ("+string+")";
    		List<HashMap> listChannel = dataService.executeSelectHQL(HashMap.class, hqlChannel, true, 0, 0);
    		String fullCode = "";
    		for(HashMap hm : listChannel){
    			fullCode += " OR PA.retailers.channels.fullCode LIKE '%"+hm.get("fullCode")+"_%'";
    		}
    		hqlDK += " AND ( PA.retailers.channels.id IN ("+string+")";
    		if(fullCode.length() > 3){
    			hqlDK += fullCode;
    		}
    		hqlDK += " )"; 
        }
        
        
        if(promotionAccumlulateFilter.getCalculation() !=0){
         int calculation = promotionAccumlulateFilter.getCalculation();
         
             hqlDK += " AND P.conditionQuantity = " +calculation;
         
        
        }

        if (promotionAccumlulateFilter.getStep() != 0) {
            int step = promotionAccumlulateFilter.getStep();
            if (step == 2) {
                hqlDK += " AND PRA.promotions.id = PA.promotions.id";
            }
            if (step == 1) {
                String hql1 = " SELECT PRA FROM PromotionRangeAward as PRA ,Promotion as P"
                        + " WHERE   PRA.deletedUser=0 AND P.deletedUser=0"
                        + " AND PRA.promotions.id=P.id "
                        + " AND P.companys.id = " + companyId;

                List<PromotionRangeAward> awards = dataService.executeSelectHQL(PromotionRangeAward.class, hql1, false, 0, 0);

                hqlDK += " AND PA.promotions.id NOT IN (";
                for (PromotionRangeAward promotionRangeAward : awards) {
                    hqlDK += promotionRangeAward.getPromotions().getId()+ ",";
                }
                hqlDK += "'')";
            }
        }
        if (promotionAccumlulateFilter.getPromotionWardId() != null) {
            List<Integer> promotionWardIdList = promotionAccumlulateFilter.getPromotionWardId();
            hqlDK += " AND P.promotionAwards.id IN (";
            for(Integer integer : promotionWardIdList){
                hqlDK+= integer+ ",";
            }
            hqlDK+="'')";
        }
        
//        if(promotionAccumlulateFilter.getChannelIds() !=null && promotionAccumlulateFilter.getChannelIds().size()!=0){
//            List<Integer> integers = promotionAccumlulateFilter.getChannelIds();
//            hql+=" AND PCH.promotions.id = PA.promotions.id "
//                    + "AND PCH.id IN (";
//            for(Integer integer :integers){
//                 hql+= integer+ ",";
//            }
//            hql+="'')";
//        
//        
//        }
        if (promotionAccumlulateFilter.getStatusId() != 0) {
            int statusId = promotionAccumlulateFilter.getStatusId();
            hqlDK += " AND PA.statuss.id = " + statusId;
        }
        if (promotionAccumlulateFilter.getGoodsCategoryId() != 0) {
            int goodsCateogryId = promotionAccumlulateFilter.getGoodsCategoryId();
            hqlDK += " AND P.goodsCategorys.id = " + goodsCateogryId;

        }
       
        
        if(promotionAccumlulateFilter.getGoodsIdList()!=null){
            List<Integer> goodsIdList = promotionAccumlulateFilter.getGoodsIdList();
            hqlDK+= " AND P.proAwardGoodss.id IN (";
            for(Integer integer :goodsIdList){
            hqlDK+= integer+ ",";
            
            }
            hqlDK+="'')";
        }
        if (promotionAccumlulateFilter.getUserRoleId() != 0) {
            int userRoleId = promotionAccumlulateFilter.getUserRoleId();
            hqlDK += " AND P.approveRoles.id = " + userRoleId;
        }
        String beginDate = promotionAccumlulateFilter.getBeginDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (beginDate != null && !beginDate.isEmpty() && !beginDate.trim().isEmpty() && !beginDate.equals("")) {

            try {
                Date date1 = sDateFormat.parse(beginDate);
                String fromDate2 = simpleDateFormat.format(date1);
                Date date2 = simpleDateFormat.parse(fromDate2);
                hqlDK += " and P.startDate >= '" + simpleDateFormat.format(date2) + "'";
            } catch (ParseException e) {
                System.err.println(">>>" + e.getMessage());
            }

        }
        String endDate = promotionAccumlulateFilter.getEndDate();
        if (endDate != null && !endDate.isEmpty() && !endDate.trim().isEmpty() && !endDate.equals("")) {

            try {
                Date date1 = sDateFormat.parse(endDate);
                String fromDate2 = simpleDateFormat.format(date1);
                Date date2 = simpleDateFormat.parse(fromDate2);
                hqlDK += " and P.endDate >= '" + simpleDateFormat.format(date2) + "'";
            } catch (ParseException e) {
                System.err.println(">>>" + e.getMessage());
            }
        }
        hqlDK+= " order by  PA.createdAt DESC";
        List<PromotionAccumulationRetailer> accumulationRetailers = dataService.executeSelectHQL(PromotionAccumulationRetailer.class, hql+hqlDK, false, pageRequest.getPageNo(), pageRequest.getRecordsInPage());
        List<PromotionAccumulationRetailer> count1 = dataService.executeSelectHQL(PromotionAccumulationRetailer.class, hql+hqlDK, false, 0, 0);

//            List<HashMap> accumulationRetailers = dataService.executeSelectHQL(hql, parameters, true, pageRequest.getPageNo(), pageRequest.getRecordsInPage());
//            List<HashMap> count1 = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        MsalesResults<PromotionAccumulationRetailer> msalesResults = new MsalesResults<>();
        msalesResults.setContentList(accumulationRetailers);
        msalesResults.setCount(Long.parseLong(count1.size() + ""));

        return msalesResults;
    }
    
    
     @Override
    public MsalesResults<PromotionAccumulationRetailer> SearchPromotionKD(int companyId, PromotionAccumlulateFilter promotionAccumlulateFilter, MsalesPageRequest pageRequest) {

        String hql = "SELECT DISTINCT(PA.id) as id,"
                + " PA.promotions as promotions,"
                + " PA.retailers as retailers,"
//                + " PA.approves as approves,"
                + " PA.statuss as statuss,"
                + " PA.awardName as awardName,"
                + " PA.awardQuantity as awardQuantity,"
                + " PA.awardAmount as awardAmount,"
                + " PA.amount as amount,"
                + " PA.note as note"
                + " FROM PromotionAccumulationRetailer as PA,"
                + " Promotion as P ";
//                + " PromotionConditional as PC ,"
//                + " PromotionConditionalRef as PCR,"
//                + " PromotionRangeAward as PRA , PromotionChannel as PCH ";
                
                String hqlDK= " WHERE PA.promotions.id=P.id AND PA.statuss.id IN (23,30,24) "
                + " AND PA.deletedUser=0 AND P.deletedUser=0 "
//                + " AND PCH.deletedUser=0 "
//                + "  "
               
                + " AND P.companys.id =" +companyId;

        if (promotionAccumlulateFilter.getPromotionId() != 0) {
            int promotionId = promotionAccumlulateFilter.getPromotionId();
            hqlDK += " AND P.id = " + promotionId;
        }
         if (promotionAccumlulateFilter.getPromotionConditionListId()!= null && promotionAccumlulateFilter.getPromotionConditionListId().size()!=0) {
            
            List<Integer> integers = promotionAccumlulateFilter.getPromotionConditionListId();
            hql+=" ,PromotionConditional as PC ,PromotionConditionalRef as PCR ";
            hqlDK += " AND PCR.promotionConditionals.id=PC.id "
                    + " AND PCR.promotions.id = PA.promotions.id"
                    + " AND PCR.promotionConditionals.id IN (";
            for(Integer integer :integers){
            hqlDK+=integer +",";
            }
            hqlDK+="'')";

        }
        if(promotionAccumlulateFilter.getCalculation() !=0){
         int calculation = promotionAccumlulateFilter.getCalculation();
         
             hqlDK += " AND P.conditionQuantity = " +calculation;
         
        
        }
//         if(promotionAccumlulateFilter.getChannelIds() !=null && promotionAccumlulateFilter.getChannelIds().size()!=0){
//            List<Integer> integers = promotionAccumlulateFilter.getChannelIds();
//            hql+=" AND PCH.promotions.id = PA.promotions.id "
//                    + "AND PCH.id IN (";
//            for(Integer integer :integers){
//                 hql+= integer+ ",";
//            }
//            hql+="'')";
//        
//        
//        }
        if (promotionAccumlulateFilter.getStep() != 0) {
            int step = promotionAccumlulateFilter.getStep();
            if (step == 2) {
                hqlDK += " AND PRA.promotions.id = PA.promotions.id";
            }
            if (step == 1) {
                String hql1 = " SELECT PRA FROM PromotionRangeAward as PRA ,Promotion as P"
                        + " WHERE   PRA.deletedUser=0 AND P.deletedUser=0"
                        + " AND PRA.promotions.id=P.id "
                        + " AND P.companys.id = " + companyId;

                List<PromotionRangeAward> awards = dataService.executeSelectHQL(PromotionRangeAward.class, hql1, false, 0, 0);

                hqlDK += " AND PA.promotions.id NOT IN (";
                for (PromotionRangeAward promotionRangeAward : awards) {
                    hqlDK += promotionRangeAward.getPromotions().getId()+ ",";
                }
                hqlDK += "'')";
            }
        }
        if (promotionAccumlulateFilter.getPromotionWardId() != null) {
            List<Integer> promotionWardIdList = promotionAccumlulateFilter.getPromotionWardId();
            hqlDK += " AND P.promotionAwards.id IN (";
            for(Integer integer : promotionWardIdList){
                hql+= integer+ ",";
            }
            hqlDK+="'')";
        }
        if (promotionAccumlulateFilter.getStatusId() != 0) {
            int statusId = promotionAccumlulateFilter.getStatusId();
            hqlDK += " AND PA.statuss.id = " + statusId;
        }
        if (promotionAccumlulateFilter.getGoodsCategoryId() != 0) {
            int goodsCateogryId = promotionAccumlulateFilter.getGoodsCategoryId();
            hqlDK += " AND P.goodsCategorys.id = " + goodsCateogryId;

        }
       
        
        if(promotionAccumlulateFilter.getGoodsIdList()!=null){
            List<Integer> goodsIdList = promotionAccumlulateFilter.getGoodsIdList();
            hqlDK+= " AND P.proAwardGoodss.id IN (";
            for(Integer integer :goodsIdList){
            hqlDK+= integer+ ",";
            
            }
            hqlDK="'')";
        }
        if (promotionAccumlulateFilter.getUserRoleId() != 0) {
            int userRoleId = promotionAccumlulateFilter.getUserRoleId();
            hqlDK += " AND P.approveRoles.id =  "+ userRoleId;
        }
        String beginDate = promotionAccumlulateFilter.getBeginDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (beginDate != null && !beginDate.isEmpty() && !beginDate.trim().isEmpty() && !beginDate.equals("")) {

            try {
                Date date1 = sDateFormat.parse(beginDate);
                String fromDate2 = simpleDateFormat.format(date1);
                Date date2 = simpleDateFormat.parse(fromDate2);
                hqlDK += " and P.startDate >= '" + simpleDateFormat.format(date2) + "'";
            } catch (ParseException e) {
                System.err.println(">>>" + e.getMessage());
            }

        }
        String endDate = promotionAccumlulateFilter.getEndDate();
        if (endDate != null && !endDate.isEmpty() && !endDate.trim().isEmpty() && !endDate.equals("")) {

            try {
                Date date1 = sDateFormat.parse(endDate);
                String fromDate2 = simpleDateFormat.format(date1);
                Date date2 = simpleDateFormat.parse(fromDate2);
                hqlDK += " and P.endDate >= '" + simpleDateFormat.format(date2) + "'";
            } catch (ParseException e) {
                System.err.println(">>>" + e.getMessage());
            }
        }
        hqlDK+=" ORDER BY PA.createdAt  DESC";
        List<PromotionAccumulationRetailer> accumulationRetailers = dataService.executeSelectHQL(PromotionAccumulationRetailer.class, hql+hqlDK, true, pageRequest.getPageNo(), pageRequest.getRecordsInPage());
//         Collections.sort(accumulationRetailers);
        List<PromotionAccumulationRetailer> count1 = dataService.executeSelectHQL(PromotionAccumulationRetailer.class, hql+hqlDK, true, 0, 0);

//            List<HashMap> accumulationRetailers = dataService.executeSelectHQL(hql, parameters, true, pageRequest.getPageNo(), pageRequest.getRecordsInPage());
//            List<HashMap> count1 = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        MsalesResults<PromotionAccumulationRetailer> msalesResults = new MsalesResults<>();
        msalesResults.setContentList(accumulationRetailers);
        msalesResults.setCount(Long.parseLong(count1.size() + ""));

        return msalesResults;
    }

    @Override
    public List<Goods> GoodsPromotion(int promotionId) {
        String hql= "SELECT G.name as name FROM Goods as G ,"
                + " PromotionGoodsRef as PGR ,Promotion as P"
                + " WHERE  G.deletedUser=0 AND PGR.deletedUser=0 AND P.deletedUser=0"
                + " AND PGR.goodss.id = G.id AND PGR.promotions.id = P.id"
                + " AND P.id = " + promotionId;
        
        List<Goods> list = dataService.executeSelectHQL(Goods.class, hql, false, 0, 0);
        return list;
    }

    @Override
    public List<Channel> ChannelPromotion(int promotionId) {
        String hql= "SELECT C FROM Channel as C ,"
                + " PromotionChannel as PRC ,Promotion as P"
                + " WHERE C.deletedUser=0 AND PRC.deletedUser=0 AND P.deletedUser=0"
                + " AND PRC.channels.id = C.id AND PRC.promotions.id = P.id"
                + " AND P.id = " + promotionId;
        List<Channel> channels = dataService.executeSelectHQL(Channel.class, hql, false, 0, 0);
        return channels;
    }
    @Override
    public List<Promotion> ListPromotion(int companyId){
    String hql="SELECT P FROM Promotion as P "
            + " WHERE P.companys.id ="+companyId;
    
    String hql1= " SELECT PA FROM PromotionAccumulationRetailer as PA ,Promotion as P"
            + " WHERE P.id = PA.promotions.id  AND P.companys.id =" +companyId +" GROUP BY PA.promotions.id";
    
    List<PromotionAccumulationRetailer> list = dataService.executeSelectHQL(PromotionAccumulationRetailer.class, hql1, false, 0, 0);
    
    hql+=" AND P.id IN (";
    for(PromotionAccumulationRetailer promotionAccumulationRetailer : list ){
    hql+=promotionAccumulationRetailer.getPromotions().getId() +",";
    }
    hql+="'')";
    List<Promotion> promotions = dataService.executeSelectHQL(Promotion.class, hql, false, 0, 0);
    return promotions;
    }


}
