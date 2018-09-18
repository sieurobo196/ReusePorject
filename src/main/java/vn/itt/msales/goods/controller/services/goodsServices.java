/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.goods.controller.services;

import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.goods.controller.model.GoodsFilter;
import vn.itt.msales.services.DataService;

/**
 *
 * @author vtm_2
 */
@Service
public interface goodsServices {
    public MsalesResults<HashMap> searchGoods(GoodsFilter goodsFilter, int companyId, MsalesPageRequest pageRequest); 
    public void deleteGoodsUnit(int goodsId,List<Integer> ids,int userId,DataService dataService );
    public boolean checkName(String field, String name, int companyId);
    public List<Goods> checkDulicated(String goodsCode, int companyId);
    
}
