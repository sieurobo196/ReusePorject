package vn.itt.msales.report.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Company;
import vn.itt.msales.report.model.MsalesOrderReport;
import vn.itt.msales.report.model.ReportTargetPerMonth;

/**
 *
 * @author ChinhNQ
 */
public interface ReportService {

    public List<Company> getListCompanySendMail();

    public List<Channel> getListDistribution(int companyId);

    public List<HashMap> getListOrders(int companyId, int channelId);
    
    public String getHqlForUser(Integer months, String fromDate, String hqlUser);
    
    public long getSumSellPerMonthService(String year, Integer month, int userId);
    
    public long getSalesOfStaffService(String fromDate, String toDate, int userId);
}
