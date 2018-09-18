package vn.itt.msales.app.service;

import java.util.HashMap;
import java.util.List;

import vn.itt.msales.entity.MCP;

public interface SalesSupService {
	//getSupSellApp
	public List<HashMap> getListUserId(String hqlChannel, String searchText, int companyId);
	
	//getSupSellApp
	public String getHqlString(String fromDate, int userId);
	
	//searchSupMCPApp
	public List<MCP> getListMCP(String date1, String date2, int mcpDay, int userId, int companyId, int userSupId);
	
	//getSupMCPApp
	public String getStringHQLSupMCPApp(int mcpId);
	
	//getListSoldGoodsByPOSApp
	public String getStringHQLOfgetListSoldGoodsByPOSApp(int posId, String toDate, String fromDate);
	
	public String getStringHQLSalesOrderOfgetListSoldGoodsByPOSApp(int posId, String toDate, String fromDate);
	
	public String getStringHqlOfSalesTransDetails(int idUser, String fromDate, String toDate);
	
	public String getStringHqlOfSalesTransDetailsForSalesSup(int idUser, String fromDate, String toDate);
	
	public String getStringHqlForSalesOrder(int idUser, String fromDate, String toDate);
	
}
