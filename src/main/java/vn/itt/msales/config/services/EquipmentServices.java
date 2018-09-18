/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.config.services;

import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.services.DataService;

/**
 *
 * @author vtm_2
 */
@Service
public interface EquipmentServices {
      
     
    public MsalesResults<HashMap> searchEquipment(Filter filter, int companyId, MsalesPageRequest pageRequest); 
    public List<User> listCbEquipmentUser(int companyId);
   
    public boolean checkEquipmentMax(int companyId,Integer equipmentId,Integer equipmentMax,DataService dataService);
    
}
