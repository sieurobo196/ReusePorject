package vn.itt.msales.goods.controller.services;

import static java.nio.file.Files.size;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.itt.msales.entity.Goods;

import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.goods.controller.model.GoodsFilter;
import vn.itt.msales.services.DataService;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vtm_2
 */
@Service
public class goodsServicesImpl implements goodsServices{
    
    @Autowired
    private DataService dataService;

    public DataService getDataService() {
        return dataService;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public MsalesResults<HashMap> searchGoods(GoodsFilter goodsFilter, int companyId, MsalesPageRequest pageRequest) {
         String hql = "SELECT DISTINCT(E.id) as id, E.name as name, E.goodsCode as goodsCode,E.price as price,E.isRecover as isRecover ,"
                + "E.factor as factor ,E.isFocus as isFocus ,E.goodsCategorys as goodsCategorys ,E.statuss as statuss"
                + " FROM Goods as E,GoodsCategory as U "
                + " WHERE E.goodsCategorys.id=U.id "
                + " AND E.deletedUser=0 AND U.deletedUser=0 "
                 + " AND U.statuss.id= 15 "
//                 + " AND E.statuss.id =15 "
                + " AND U.companys.id = " + companyId;

        
            if (goodsFilter.getSearchText() != null) {
                String key = goodsFilter.getSearchText().toString();
                // parameterList.or("name", key, "like", "code", key, "like");
                hql += " and ( E.name LIKE '%" + key + "%'" + " or E.goodsCode LIKE '%" + key + "%' ) ";
            }
            if (goodsFilter.getGoodsCategoryId()!= null && goodsFilter.getGoodsCategoryId()!= 0) {
                int statusId = Integer.parseInt(goodsFilter.getGoodsCategoryId().toString());
                try {
                    //parameterList.add("parents.id", statusId);

                    hql += " and U.id = " + statusId;

                } catch (Exception ex) {
                    //error parse statusId
                }
            }
            
            if (goodsFilter.getStatusId()!= null && goodsFilter.getStatusId()!= 0) {
                try {
                    String key = goodsFilter.getStatusId().toString();
                    int locationId = Integer.parseInt(key);
                    hql += " and E.statuss.id = " + locationId;
                } catch (Exception ex) {
                    //error parse statusId
                }

            }
//                
        
        hql += " group by E.id " + " ORDER BY E.goodsCategorys.id,E.createdAt desc";
        List<HashMap> lists = dataService.executeSelectHQL(HashMap.class, hql, true, pageRequest.getPageNo(),pageRequest.getRecordsInPage());
        List<HashMap> count1 = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);

        MsalesResults<HashMap> listChannel = new MsalesResults<HashMap>();
        listChannel.setContentList(lists);
        listChannel.setCount(Long.parseLong(count1.size() + ""));
        return listChannel;
    }

    @Override
    public void deleteGoodsUnit(int goodsId, List<Integer> ids,int userId, DataService dataService) {
        String hql = "UPDATE GoodsUnit SET deletedUser = :userId"
                + " WHERE goodss.id = :goodsId"
                + " AND id NOT IN (:ids)";
        List<MsalesParameter> parameters = MsalesParameter.createList("goodsId", goodsId);
        parameters.add(MsalesParameter.create("userId", userId));
        if (ids == null || ids.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }
        dataService.executeHQL(hql, parameters);
    }
    
    @Override
    public boolean checkName(String field, String name, int companyId) {
        String hql = "FROM GoodsCategory where "+ field + " = '" + name + "' and companys.id = " + companyId +" and deletedUser = 0";
        List<Unit> listUnit = dataService.executeSelectHQL(Unit.class, hql, false, 0, 0);
        return (listUnit.size() > 0);
    }

    @Override
    public List<Goods> checkDulicated(String goodsCode, int companyId) {
        String hql = "FROM Goods as Goods join Goods.goodsCategorys as GoodsCategoty where GoodsCategoty.companys.id=" + companyId + " AND Goods.goodsCode= '"+ goodsCode+"' AND Goods.deletedUser = 0 AND GoodsCategoty.deletedUser = 0";
        List<Goods> listGoods = dataService.executeSelectHQL(Goods.class, hql, false, 1, 1);
        
        return listGoods;
    }
    
    
}
