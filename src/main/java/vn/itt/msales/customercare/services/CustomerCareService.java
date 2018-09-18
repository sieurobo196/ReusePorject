/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.services;

import java.util.LinkedHashMap;
import java.util.List;
import vn.itt.msales.customercare.model.CustomerCareDetailWorkflow;
import vn.itt.msales.customercare.model.CustomerCareImage;
import vn.itt.msales.entity.CustomerCareDetails;
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.CustomerCareDetailsById;
import vn.itt.msales.entity.searchObject.SearchSupRoute;
import vn.itt.msales.services.DataService;

/**
 *
 * @author cshiflett
 */
public interface CustomerCareService {
    public MsalesResults<CustomerCareInformation> getListCustomerCareInformationByPOSId(int posid, int page, int size);
    
    public CustomerCareInformation getCustomerCareInformationById(int cusId);
    
    public List<CustomerCareDetailsById> getListCustomerCareDetailByCustomerCareInfomationId(int cusId);
    
    public List<LinkedHashMap> searchSupUserRoute(SearchSupRoute searchSupRoute);
    
    public List<CustomerCareImage> getListCustomerCareImage(int cusId);
    
    public List<CustomerCareDetailWorkflow> getPosUpdateInfo(int cusId);
    
    public List<CustomerCareDetailWorkflow> getPosToolInfo(int cusId);
    
    public List<CustomerCareDetailWorkflow> getCompetitorList(int cusId);
    
    public List<CustomerCareDetailWorkflow> getCompetitorToolList(int cusId);
    
    public List<CustomerCareDetailWorkflow> getCompetitorGoodList(int cusId);
    
    public List<CustomerCareDetailWorkflow> getCompetitorPromotionList(int cusId);
    
    public List<CustomerCareDetails> getListCustomerCareDetails(int infoId,DataService dataService);
    
    public List<CustomerCareDetails> getListCustomerCareDetailsByWorkflowDetailsId(int infoId,int workflowDetailsId,DataService dataService);
    
    public List<CustomerCareDetails> getListCustomerCareDetailsUpdatePOS(int infoId,DataService dataService);
}
