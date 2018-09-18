package vn.itt.msales.sales.service;

import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.services.DataService;

/**
 *
 * @author ChinhNQ
 */
public interface MsalesSaleTransService {
    public MsalesResults<SalesTrans> searchTrans(Filter filter,int companyId,int page,int size,DataService dataService);
}
