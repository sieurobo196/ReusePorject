package vn.itt.msales.workflow.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface ServiceMCP {

	public String getSQLString(String classSelect, List<ArrayList<String>> fields, List<ArrayList<String>> operator, List<ArrayList<String>> value);
	public String countSQLString(String classSelect, List<ArrayList<String>> fields, List<ArrayList<String>> operator, List<ArrayList<String>> value);
	public String getStringHQL(int userId, String fromDate, String toDate);
	public String getStringHQLDelivery(int companyId, Date beginDate, int posId, String fromDate, String toDate);
	public String getStringHQLUserRoute(int userId, String fromDate, String toDate);
	public String getStringHQLNVBH(int userId, int companyId);
	public String getStringHQLChamSoc(int userId, int mcpId, String fromDate, String toDate);
	public String getPosIdForOrderListByUserId(int userId);
	public String getUserForOrderListByPosId(int posId);	
    public String getListPosForCreatedOrder(int companyId, String hqlChannel);
    public String getStringForTheoDoiNhanHang(int companyId, int createdUser,int goodsId, int goodsUnitId, String createdAt);
    public String getStringForSaleTransDetails(int companyId, int createdUser, String toDate, int goodsId, int goodsUnitId, String fromDate);

    public boolean checkMCPExist(int userId, Date date);
}
