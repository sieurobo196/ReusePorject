package vn.itt.msales.report.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import vn.itt.msales.app.service.SalesSupService;
import vn.itt.msales.common.GPSUtils;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.report.model.ReportTargetPerMonth;
import vn.itt.msales.services.DataService;
import vn.itt.msales.services.DataServiceImpl;

/**
 *
 * @author ChinhNQ
 */
public class ReportServiceImpl extends DataServiceImpl implements ReportService {

    @Autowired
    private DataService dataService;
    
    @Autowired
    private SalesSupService salesSupService;
    
    @Value("#{mailer['msale.mail.time']}")
    private Integer mailTime;
    @Override
    public List<Company> getListCompanySendMail() {
        ParameterList param = new ParameterList();
        param.add("isSendmailOrderList", true);

        List<Company> listCompanySendReport = dataService.getListOption(Company.class, param);

        return listCompanySendReport;
    }

    @Override
    public List<HashMap> getListOrders(int companyId, int channelId) {
        List<HashMap> listOrder = new ArrayList<>();
        Date startDate = GPSUtils.convertStringToDate(GPSUtils.formatDate(new Date()), "dd/MM/yyyy");
        startDate.setHours(0);
        startDate.setMinutes(0);
        Date endDate = GPSUtils.convertStringToDate(GPSUtils.formatDate(new Date()), "dd/MM/yyyy");

        endDate.setHours(23);
        endDate.setMinutes(58);

        String hqlSalesOrder = "Select S.orders.createdUsers.userCode as employeCode, S.orders.createdUsers.lastName as lastName, "
                + " S.orders.createdUsers.firstName as firstName, "
                + " S.createdAt as orderDate, "
                + " S.orders.pos.posCode as customerCode,"
                + " S.orders.pos.tel as tel, "
                + " S.orders.pos.name as customerName,"
                + " S.orders.pos.address as address,"
                + " S.goodss.goodsCode as goodsCode,"
                + " S.goodss.name as goodsName,"
                + " S.quantity as quantity,"
                + " S.orders.pos.channels.name as distributionName,"
                + " S.orders.pos.channels.email as distributionEmail,"
                + " S.price as price from SalesOrderDetails as S where deletedUser = 0 and S.orders.companys.id =:companyId"
                + " and S.orders.pos.channels.id =:channelId and S.createdAt between :startDate and  :endDate order by distributionName, employeCode, orderDate, customerCode, goodsName";

        try {
            List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
            parameters.add(MsalesParameter.create("channelId", channelId));
            parameters.add(MsalesParameter.create("startDate", startDate, 3));
            parameters.add(MsalesParameter.create("endDate", endDate, 3));
            listOrder = dataService.executeSelectHQL(hqlSalesOrder, parameters, true, 0, 0);
        } catch (Exception e) {
            System.err.println(">>>" + e.getMessage());
        }
        return listOrder;
    }

    @Override
    public List<Channel> getListDistribution(int companyId) {
        List<Channel> channels = new ArrayList<>();
        List<HashMap> distributionList = new ArrayList<>();
        String hql = "SELECT pos.channels as channel FROM POS as pos where pos.channels.deletedUser = 0 and pos.channels.companys.id =" + companyId + " group by pos.channels.id";
        distributionList = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
        for (HashMap hashMap : distributionList) {
            Channel channel = (Channel) hashMap.get("channel");
            System.err.println(">>:" + channel.getId());
            channels.add(channel);
        }
        return channels;
    }
    
    @Override
    public String getHqlForUser(Integer months, String fromDate, String hqlUser){
    	 String hql = "SELECT MCP.implementEmployees.id as id, CONCAT(MCP.implementEmployees.lastName, ' ',MCP.implementEmployees.firstName) as name"
          		+ " FROM MCP as MCP WHERE deletedUser = 0 "
          		+ " AND type = 2 "
          		+ " AND MONTH(beginDate) = "+months
          		+ " AND YEAR(beginDate) = "+fromDate
          		+ " AND implementEmployees.id IN ("+hqlUser+")"
          		+ " GROUP BY implementEmployees.id ";
    	return hql;
    }
    
    public long getSumSellPerMonthService(String year, Integer month, int userId){
    	String hqlSumYear = "SELECT sum(MCP.salesPerMonth) as salesPerMonth "
    			+ " FROM MCP as MCP WHERE MCP.deletedUser = 0"
    			//+ " AND MCP.beginDate LIKE '"+date+"-%'"
    			+ " AND MCP.type = 2 AND MCP.implementEmployees.id = "+userId;
    			if(year != null && !year.isEmpty()){
    				hqlSumYear += " AND YEAR(MCP.beginDate) = " + year;
    			}
    			if(month != null){
    				hqlSumYear += " AND MONTH(MCP.beginDate) = " + month;
    			}
    	List<HashMap> sellPerMonth = dataService.executeSelectHQL(HashMap.class, hqlSumYear, true, 0, 0);
    	long salesTarget = 0;
    	if(!sellPerMonth.isEmpty()){
    		if(sellPerMonth.get(0).get("salesPerMonth") != null){
    			salesTarget = (long) sellPerMonth.get(0).get("salesPerMonth");
    		}	
    	}
    	return salesTarget;
    }
    
    public long getSalesOfStaffService(String fromDate, String toDate, int userId){
         String hqlS = salesSupService.getStringHqlOfSalesTransDetails(userId, fromDate, toDate);
         List<HashMap> salesTransDetails = new ArrayList<HashMap>();
         try {
             salesTransDetails = dataService.executeSelectHQL(HashMap.class, hqlS, true, 0, 0);
         } catch (Exception ex) {

         }
         String hqlS2 = salesSupService.getStringHqlOfSalesTransDetailsForSalesSup(userId, fromDate, toDate);
         List<HashMap> salesTransDetails2 = new ArrayList<HashMap>();
         try {
             salesTransDetails2 = dataService.executeSelectHQL(HashMap.class, hqlS2, true, 0, 0);
         } catch (Exception ex) {

         }
         if (!salesTransDetails2.isEmpty()) {
             salesTransDetails.addAll(salesTransDetails2);
         }
         String hqlSalesOrder = salesSupService.getStringHqlForSalesOrder(userId, fromDate, toDate);
         List<HashMap> salesOrderList = dataService.executeSelectHQL(HashMap.class, hqlSalesOrder, true, 0, 0);
         if (!salesOrderList.isEmpty()) {
             salesTransDetails.addAll(salesOrderList);
         }
         long salesPerYear = 0;
         if (!salesTransDetails.isEmpty()) {
             for (HashMap sTransDetails : salesTransDetails) {
                salesPerYear += (Integer) sTransDetails.get("quantity") * (Integer) sTransDetails.get("price");
             }

         }
    	return salesPerYear;

    }    

}
