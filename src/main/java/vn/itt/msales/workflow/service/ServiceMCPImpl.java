package vn.itt.msales.workflow.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.itt.msales.entity.MCP;

import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ServiceMCPImpl implements ServiceMCP {

    @Autowired
    private DataService dataService;

	@Override
	public String getSQLString(String classSelect,
			List<ArrayList<String>> fields, List<ArrayList<String>> operator,
			List<ArrayList<String>> value) {
		String hql = "Select U from " + classSelect
				+ " as U where deletedUser = 0 ";
		int i = 0;
		for (ArrayList<String> strings : fields) {
			if (strings.size() == 1) {
				if (operator.get(i).get(0).equals("LIKE")) {
					hql += " and " + fields.get(i).get(0) + " "
							+ operator.get(i).get(0) + " '%"
							+ value.get(i).get(0) + "%' ";
				} else if (operator.get(i).get(0).equals("order by")) {
					hql += " " + operator.get(i).get(0) + " "
							+ fields.get(i).get(0) + " " + value.get(i).get(0);
				} else {
					hql += " and " + fields.get(i).get(0) + " "
							+ operator.get(i).get(0) + " "
							+ value.get(i).get(0);
				}

			} else {
				hql += " and ( ";
				String string = "";
				for (int j = 0; j < strings.size(); j++) {
					if (operator.get(i).get(j).equals("LIKE")) {
						string += " or " + fields.get(i).get(j) + " "
								+ operator.get(i).get(j) + " '%"
								+ value.get(i).get(j) + "%' ";
					} else if (operator.get(i).get(j).equals("order by")) {
						string += " " + operator.get(i).get(j) + " "
								+ fields.get(i).get(j) + " "
								+ value.get(i).get(j);
					} else {
						string += " or " + fields.get(i).get(j) + " "
								+ operator.get(i).get(j) + " "
								+ value.get(i).get(j);
					}
				}
				if (string != null && string.length() > 2) {
					hql += string.substring(3) + " ) ";
				}
			}
			i++;
		}
		return hql;
	}

	@Override
	public String countSQLString(String classSelect,
			List<ArrayList<String>> fields, List<ArrayList<String>> operator,
			List<ArrayList<String>> value) {
		String hql = "Select U.id as id from " + classSelect
				+ " as U where deletedUser = 0 ";
		int i = 0;
		for (ArrayList<String> strings : fields) {
			if (strings.size() == 1) {
				if (operator.get(i).get(0).equals("LIKE")) {
					hql += " and " + fields.get(i).get(0) + " "
							+ operator.get(i).get(0) + " '%"
							+ value.get(i).get(0) + "%' ";
				} else if (operator.get(i).get(0).equals("order by")) {
					hql += " " + operator.get(i).get(0) + " "
							+ fields.get(i).get(0) + " " + value.get(i).get(0);
				} else {
					hql += " and " + fields.get(i).get(0) + " "
							+ operator.get(i).get(0) + " "
							+ value.get(i).get(0);
				}

			} else {
				hql += " and ( ";
				String string = "";
				for (int j = 0; j < strings.size(); j++) {
					if (operator.get(i).get(j).equals("LIKE")) {
						string += " or " + fields.get(i).get(j) + " "
								+ operator.get(i).get(j) + " '%"
								+ value.get(i).get(j) + "%' ";
					} else if (operator.get(i).get(j).equals("order by")) {
						string += " " + operator.get(i).get(j) + " "
								+ fields.get(i).get(j) + " "
								+ value.get(i).get(j);
					} else {
						string += " or " + fields.get(i).get(j) + " "
								+ operator.get(i).get(j) + " "
								+ value.get(i).get(j);
					}
				}
				if (string != null && string.length() > 2) {
					hql += string.substring(3) + " ) ";
				}
			}
			i++;
		}
		return hql;
	}
	
	public String getStringHQL(int userId, String fromDate, String toDate){
		 String hql = "SELECT MCPDetails.id as mcpDetailsId,MCPDetails.poss AS pos,MCPDetails.mcps.beginDate as beginDate,"
                 + " CASE WHEN CustomerCareInformation.id IS NULL THEN FALSE ELSE TRUE END AS isVisited,"
                 + " CASE WHEN CustomerCareInformation.startCustomerCareAt IS NULL THEN NULL ELSE CustomerCareInformation.startCustomerCareAt END AS timeCared"
                 + " FROM CustomerCareInformation as CustomerCareInformation"
                 + " RIGHT JOIN CustomerCareInformation.mcpDetailss as MCPDetails"
                 + " WHERE MCPDetails.mcps.implementEmployees.id = " + userId;
		 if(fromDate != null){
			 hql += " AND MCPDetails.mcps.beginDate >= '" + fromDate + "'";
		 }
		 hql  += " AND MCPDetails.mcps.beginDate < '" + toDate + "'"
                 + " AND MCPDetails.mcps.deletedUser = 0"
                 + " AND MCPDetails.deletedUser = 0"
                 + " AND (CustomerCareInformation IS NULL OR CustomerCareInformation.deletedUser = 0)"
               	+ " ORDER BY CustomerCareInformation.startCustomerCareAt";
		
		return hql;
	}
	
	public String getStringHQLDelivery(int companyId,Date beginDate, int posId, String fromDate, String toDate){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String hql = "SELECT SalesTrans.salesTransDate as timeSale, "
				+ " SalesTrans.id as id"
				+ " FROM SalesTrans AS SalesTrans"
				+ " WHERE SalesTrans.deletedUser = 0"
				+ " AND SalesTrans.companys.id = "+ companyId
				+ " AND SalesTrans.transType = 2"
				+ " AND SalesTrans.toStocks.poss.id = " + posId
				+ " AND SalesTrans.salesTransDate < '" + toDate + "'"
				+ " AND DATE(SalesTrans.salesTransDate) = '"+simpleDateFormat.format(beginDate)+"'";
		if (fromDate != null) {
			hql += " AND SalesTrans.salesTransDate >= '" + fromDate + "'";
		}
		hql += " ORDER BY SalesTrans.salesTransDate DESC";
//		String hqlDelivery = "SELECT DATE_FORMAT(SalesOrder.salesTransDate,'%d/%m/%Y') as timeDelivery,"
//				+ " CASE WHEN SalesTrans IS NULL THEN NULL ELSE DATE_FORMAT(SalesTrans.salesTransDate,'%d/%m/%Y %H:%i:%S') END AS timeSale"
//				+ " FROM SalesTrans AS SalesTrans"
//				+ " RIGHT JOIN SalesTrans.orders AS SalesOrder"
//				+ " WHERE SalesOrder.deletedUser = 0"
//				+ " AND SalesTrans.deletedUser = 0";
//		if (fromDate != null) {
//			hqlDelivery += " AND SalesTrans.salesTransDate >= '" + fromDate
//					+ "'";
//		}
//		hqlDelivery += " AND SalesTrans.salesTransDate < '" + toDate + "'"
//				+ " AND SalesOrder.pos.id = " + posId
//				+ " AND SalesTrans.transType = 2"
//				+ " AND (SalesOrder.statuss.value != 3)" // value = 3
//															// =>salesOder bi
//															// huy
//				+ " ORDER BY SalesOrder.salesTransDate DESC";
		return hql;
	}
	
	public String getStringHQLUserRoute(int userId, String fromDate, String toDate){
		String hqlRoute = "SELECT UserRoute.lat AS lat,UserRoute.lng AS lng,"
                + " UserRoute.routeAt as routeAt, UserRoute.createdAt as createdAt"
                + " FROM UserRoute as UserRoute"
                + " WHERE UserRoute.createdUser = '" + userId + "'";
                if(fromDate != null){
             	   hqlRoute += " AND UserRoute.routeAt >= '" + fromDate + "'";
                }
             hqlRoute +=  " AND UserRoute.routeAt < '" + toDate + "'"
                + " AND UserRoute.deletedUser = 0" + " ORDER BY UserRoute.id DESC";
		return hqlRoute;
	}
	public String getStringHQLNVBH(int userId, int companyId){
		String hqlC = "Select URC.channels.name as name, URC.channels.fullCode as fullCode, "
    			+ " URC.channels.tel as tel, URC.channels.address as address "
    			+ " from UserRoleChannel as URC where deletedUser = 0 "
    			+ " and channels.companys.id = "+ companyId
    			+ " and users.id = "+ userId
    			+ " order by channels.channelTypes.id desc";
		return hqlC;
	}
	
	public String getStringHQLChamSoc(int userId, int mcpId, String fromDate, String toDate){
		String hqlC = "Select CCI.id as id from CustomerCareInformation as CCI where deletedUser = 0"
        		+ " and implementEmployees.id = "+ userId
        		+ " and mcpDetailss.mcps.id = "+ mcpId
        		+ " and startCustomerCareAt >= '"+ fromDate + " 00:00:00'"
        		+ " and startCustomerCareAt < '"+ toDate + " 00:00:00'";
		return hqlC;
	}
	
	public String getPosIdForOrderListByUserId(int userId){
		String hqlC = "Select MCP.poss.id as posId from MCPDetails as MCP "
    			+ " where deletedUser = 0 and implementEmployees.id = "+userId
    			+ " group by poss.id";
		return hqlC;
	}
	
	public String getUserForOrderListByPosId(int posId){
		String hqlUser = "Select MCP.implementEmployees as user"
    			+ " from MCPDetails as MCP where MCP.deletedUser = 0 and MCP.mcps.deletedUser = 0 and MCP.poss.id = "+posId;
		return hqlUser;
	}
	
	public String getListPosForCreatedOrder(int companyId, String hqlChannel){
		String hql = "Select MCPDetails.poss.id as id, MCPDetails.poss.name as name from MCPDetails as MCPDetails where deletedUser = 0 and poss.deletedUser = 0 and poss.statuss.id = 5"
        		+ " and poss.channels.companys.id = " + companyId;
        		
		return hql;
    }
	
	@Override
	public String getStringForTheoDoiNhanHang(int companyId, int createdUser,int goodsId, int goodsUnitId, String createdAt){
		String hqlX = "SELECT SalesTrans.salesTranss.id as id, SalesTrans.salesTranss.createdAt as date "
				+ " FROM SalesTransDetails as SalesTrans"
				+ " WHERE SalesTrans.deletedUser = 0 and SalesTrans.salesTranss.transType = 3 and SalesTrans.salesTranss.deletedUser = 0"
				+ " and SalesTrans.salesTranss.companys.id = "+ companyId
				+ " and SalesTrans.salesTranss.createdUser = "+ createdUser
				+ " and SalesTrans.salesTranss.createdAt <= '"+createdAt +"'"
				+ " and SalesTrans.goodss.id = "+goodsId
				+ " and SalesTrans.goodsUnits.id = "+ goodsUnitId
				+ " order by createdAt desc";
		
		return hqlX;
	}
	
	@Override
	public String getStringForSaleTransDetails(int companyId, int createdUser, String toDate, int goodsId, int goodsUnitId, String fromDate){
		String hqlSaleDetails = "SELECT S.id as id, S.price as price, S.quantity as quantity"
        		+ " FROM SalesTransDetails as S where deletedUser = 0"
        		+ " and salesTranss.deletedUser = 0 and salesTranss.transType = 1 "
        		+ " and salesTranss.companys.id = "+companyId
        		+ " and salesTranss.createdUser = "+ createdUser
        	
        		+ " and salesTranss.createdAt < '"+toDate +"'"
        		+ " and goodss.id = "+ goodsId
        		+ " and goodsUnits.id = "+ goodsUnitId;
		if(fromDate != null){
			hqlSaleDetails += " and salesTranss.createdAt >= '"+ fromDate +"'";
		}
		
		return hqlSaleDetails;
	}

    @Override
    public boolean checkMCPExist(int userId, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM");
        
        String hql = "FROM MCP where deletedUser = 0 and type = 2 and implementEmployees.id = " + userId + " and YEAR(beginDate) = " + simpleDateFormat.format(date) 
        + " and MONTH(beginDate) = " + Integer.parseInt(simpleDateFormat2.format(date));
//        List<MsalesParameter> param = new ArrayList<>();
//        param.add(MsalesParameter.create("userId", userId));
//        param.add(MsalesParameter.create("beginDate", simpleDateFormat.format(date) + "%"));
        List<MCP> list = dataService.executeSelectHQL(MCP.class, hql, false, 0, 0);
        return list != null && !list.isEmpty();
    }

}
