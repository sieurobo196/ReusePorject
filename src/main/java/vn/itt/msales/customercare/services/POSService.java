/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.services;

import java.util.LinkedHashMap;
import java.util.List;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author cshiflett
 */
public interface POSService {

    public MsalesResults<POS> searchPOSByParams(MsalesLoginUserInf userInf, Filter filter, LinkedHashMap<String, String> params, int page, int size);

    public POS getPOSById(int id);

    public List<OptionItem> getChannelById(int id);

    public MsalesResults<CustomerCareInformation> searchCustomerCareByParams(LinkedHashMap<String, String> params, int page, int size);

    public int insertRow(POS pos,int companyId, DataService dataService);

    public Boolean updateRow(POS pos);

    public List<POS> getListPOSIn(List<String> ids);

    public POS getPOSByCode(String code, int companyId, DataService dataService);

    public List<Status> getPOSStatus(DataService dataService);
}
