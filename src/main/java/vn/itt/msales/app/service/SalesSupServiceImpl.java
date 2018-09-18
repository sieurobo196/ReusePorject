package vn.itt.msales.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.services.DataService;

public class SalesSupServiceImpl implements SalesSupService {

    @Autowired
    DataService dataService;

    @Override
    public List<HashMap> getListUserId(String hqlChannel, String searchText, int companyId) {
    	  String string2 = "Select Channel.id as channelId, Channel.fullCode as fullCode From Channel as Channel "
          		+ " Where id IN ("+hqlChannel+")";
          List<HashMap> listChannel = dataService.executeSelectHQL(HashMap.class, string2, true, 0, 0);
          String hqlX = "";
          for(HashMap hashMap : listChannel){
          	hqlX += " OR channel.FULL_CODE like '%"+hashMap.get("fullCode")+"%'";
        	//  hqlX += hashMap.get("channelId") + ",";
          }
        String sql = "select user.id as id, concat(user.LAST_NAME,' ', user.FIRST_NAME) as name "
                + " from user join user_role_channel"
                + " join channel "
                + " where user.id = user_role_channel.USER_ID "
                + " and user_role_channel.CHANNEL_ID = channel.ID "
                + " and user.DELETED_USER = 0 and user_role_channel.DELETED_USER = 0 "
                + " and user.COMPANY_ID = " + companyId
                + " and user_role_channel.USER_ROLE_ID = 6 "
                + " and user_role_channel.CHANNEL_ID not in (" + hqlChannel + " ) "
                + " and user.STATUS_ID = 6 ";
        if(hqlX.length()>3){
        	sql += " and ("+ hqlX.substring(3)+")";
        }
               
		//		+ " and concat(user.LAST_NAME,' ',user.FIRST_NAME) LIKE '%nguyên%%%muoi%%%%mot%' COLLATE utf8_unicode_ci";

//		String hql = "SELECT UserRoleChannel.users.id AS id,"
//                + " CONCAT (UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name"
//                + " FROM UserRoleChannel AS UserRoleChannel"
//                + " WHERE UserRoleChannel.deletedUser = 0"
//                + " AND UserRoleChannel.users.companys.id = "+ companyId
//                + " AND UserRoleChannel.channels.id IN (" + hqlChannel + ")"
//                + " AND UserRoleChannel.userRoles.id = 6";
//		
//		 if (searchText != null && !searchText.isEmpty() && !searchText.trim().isEmpty()) {
//			
////			 String[] arraytext = searchText.split(" ");
////			 
////			 ArrayList<String> searchText2 = new ArrayList<String>();
////			 for(String s : arraytext){
////				 ArrayList<String> searchText3 = unaccent(s); 
////				 searchText2.addAll(searchText3);
////			 }
//			 searchText = searchText.replaceAll("'", "''"); searchText = searchText.replaceAll(" ", "%");
//			 ArrayList<String> searchText2 = unaccent(searchText);
//			 
//			 String string = "";
//	         hql += " AND ( CONCAT (UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) LIKE '%" + searchText + "%' ";
//	         if(!searchText2.isEmpty()){
//	        	 for(int i = 0; i < searchText2.size(); i++){
//	        		 searchText2.get(i).replaceAll("'", "''");searchText2.get(i).replaceAll(" ", "%");
//	        		 string += " OR CONCAT (UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) LIKE '%" + searchText2.get(i) + "%' ";
//	        	 }
//	        	 hql += string;
//	         }
//	         hql += " COLLATE utf8_unicode_ci ) ";
//	     }
//		 List<HashMap> listUsers = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
        if (searchText != null && !searchText.isEmpty() && !searchText.trim().isEmpty()) {
            searchText = searchText.replaceAll("'", "''");
            searchText = searchText.replaceAll(" ", "%");
            ArrayList<String> searchText2 = unaccent(searchText);

            String string = "";
            sql += " AND ( concat(user.LAST_NAME,user.FIRST_NAME) LIKE '%" + searchText + "%' COLLATE utf8_unicode_ci ";
            if (!searchText2.isEmpty()) {
                for (int i = 0; i < searchText2.size(); i++) {
                    string += " OR concat(user.LAST_NAME,user.FIRST_NAME) LIKE '%" + searchText2.get(i) + "%' COLLATE utf8_unicode_ci ";
                }
                sql += string;
            }
            sql += " ) ";
        }
        sql +=  " GROUP BY user.id ";
        List<HashMap> listUsers = dataService.execSQL(sql);
        return listUsers;
    }

    @Override
    public String getHqlString(String fromDate, int userId) {
        String hql2 = "Select U.id from POS as U where deletedUser = 0 and createdAt >= '" + fromDate + "' and createdUser = " + userId ;
        return hql2;
    }

    @Override
    public List<MCP> getListMCP(String date1, String date2, int mcpDay, int userId, int companyId, int userSupId) {
        String hql = "FROM MCP as MCP"
                + " WHERE MCP.beginDate >= '" + date1 + "'"
                + " AND MCP.beginDate <= '" + date2 + "'"
                + " AND MCP.type = 1"
                + " AND MCP.deletedUser = 0"
                + " AND MCP.implementEmployees.companys.id = " + companyId;

        if (mcpDay != -1 && mcpDay > 0 && mcpDay < 8) {
            if (mcpDay == 1) {
                mcpDay = 8;
            }
            hql += " AND WEEKDAY(MCP.beginDate) + 2 = '" + mcpDay + "'";
        }

        if (userId != -1) {
            hql += " AND MCP.implementEmployees.id = '" + userId + "'";

        } else {
            //Lấy danh sách nhân viên của sup
            ParameterList parameterList9 = new ParameterList(0, 0);
            parameterList9.add("users.id", userSupId);
            List<UserRoleChannel> userRoleChannelList = dataService.getListOption(UserRoleChannel.class, parameterList9);
            if (!userRoleChannelList.isEmpty()) {
                //not login
                String hqlChannel = "";
                for (UserRoleChannel userRoleChannel : userRoleChannelList) {
                    hqlChannel += userRoleChannel.getChannels().getId() + ",";
                }
                hqlChannel += "''";
                String string2 = "Select Channel.id as channelId, Channel.fullCode as fullCode From Channel as Channel "
                		+ " Where id IN ("+hqlChannel+")";
                List<HashMap> listChannel = dataService.executeSelectHQL(HashMap.class, string2, true, 0, 0);
                String hqlX = "";
                for(HashMap hashMap : listChannel){
                	hqlX += " OR UserRoleChannel.channels.fullCode like '%"+hashMap.get("fullCode")+"%'";
                }
                String hqlU = "SELECT UserRoleChannel.users.id AS id,"
                        + " CONCAT (UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name"
                        + " FROM UserRoleChannel AS UserRoleChannel"
                        + " WHERE UserRoleChannel.deletedUser = 0"
                        + " AND UserRoleChannel.users.statuss.id = 6"
                        + " AND UserRoleChannel.users.companys.id = " + companyId
                        + " AND UserRoleChannel.channels.id NOT IN (" + hqlChannel + ")"
                        + " AND UserRoleChannel.userRoles.id = 6";
                if(hqlX.length() > 3){
                	hqlU += " AND ("+hqlX.substring(3)+")";
                }
                List<HashMap> listUsers = dataService.executeSelectHQL(HashMap.class, hqlU, true, 0, 0);
                String string = "";
                for (HashMap hashMap : listUsers) {
                    string += " or MCP.implementEmployees.id = " + (Integer) hashMap.get("id");
                }
                if (string.length() > 4) {
                    hql += " AND (" + string.substring(3) + " ) ";
                } else {
                    hql += " AND MCP.implementEmployees.id = '" + -1 + "'";
                }

            } else {
                hql += " AND MCP.implementEmployees.id = '" + -1 + "'";
            }
        }

        List<MCP> mcpList = dataService.executeSelectHQL(MCP.class, hql, false, 0, 0);

        return mcpList;
    }

    @Override
    public String getStringHQLSupMCPApp(int mcpId) {
        String hql = "SELECT MCPDetails.poss as pos,MCPDetails.id as mcpDetailsId,"
                + " CASE WHEN CustomerCareInformation.id IS NULL THEN FALSE ELSE TRUE END AS isVisited"
                + " FROM CustomerCareInformation as CustomerCareInformation"
                + " RIGHT JOIN CustomerCareInformation.mcpDetailss as MCPDetails"
                + " JOIN MCPDetails.mcps as MCP"
                + " WHERE MCP.id = '" + mcpId + "'"
                + " AND MCPDetails.deletedUser = 0"
                + " AND MCP.deletedUser = 0"
                + " AND MCPDetails.poss.deletedUser = 0"
                + " GROUP BY MCPDetails.id";

        return hql;
    }

    @Override
    public String getStringHQLOfgetListSoldGoodsByPOSApp(int posId, String toDate, String fromDate) {
        String hql = "SELECT SalesTransDetails.goodss.id AS id,SalesTransDetails.goodss.name AS name, SalesTransDetails.salesTranss.id AS salesTransId, SalesTransDetails.salesTranss.salesTransDate AS salesTransDate, "
                + " SalesTransDetails.quantity AS quantity"
                + " FROM SalesTransDetails AS SalesTransDetails"
                + " WHERE SalesTransDetails.salesTranss.toStocks.poss.id = '" + posId + "'"
                + " AND SalesTransDetails.salesTranss.salesTransDate <= '" + toDate + "' AND SalesTransDetails.salesTranss.salesTransDate >= '" + fromDate + "'"
                + " AND SalesTransDetails.salesTranss.transType = 2"
                + " AND SalesTransDetails.deletedUser = 0"
                + " AND SalesTransDetails.salesTranss.deletedUser = 0"
                + " AND SalesTransDetails.salesTranss.toStocks.deletedUser = 0";

        return hql;
    }
    
    
    @Override
    public String getStringHQLSalesOrderOfgetListSoldGoodsByPOSApp(int posId, String toDate, String fromDate){
    	String hql = "SELECT SalesTransDetails.goodss.id AS id,SalesTransDetails.goodss.name AS name, SalesTransDetails.orders.id AS salesTransId, SalesTransDetails.orders.salesTransDate AS salesTransDate, "
                + " SalesTransDetails.quantity AS quantity"
                + " FROM SalesOrderDetails AS SalesTransDetails"
                + " WHERE SalesTransDetails.orders.pos.id = '" + posId + "'"
                + " AND SalesTransDetails.orders.salesTransDate <= '" + toDate + "' AND SalesTransDetails.orders.salesTransDate >= '" + fromDate + "'"
                + " AND SalesTransDetails.orders.statuss.id not in (13,14)"
                + " AND SalesTransDetails.deletedUser = 0"
                + " AND SalesTransDetails.orders.deletedUser = 0";
    	return hql;
    }   

    @Override
    public String getStringHqlOfSalesTransDetails(int idUser, String fromDate, String toDate) {
        String hqlSales = "Select S.id as id, S.quantity as quantity, S.price as price, S.salesTranss.createdAt as date, S.isFocus as isFocus "
                + " from SalesTransDetails as S "
                + " where S.salesTranss.transType = 2 "
                + " AND S.salesTranss.deletedUser = 0 "
                + " AND S.deletedUser = 0"
                + " AND S.salesTranss.createdUser = " + idUser
                + " AND S.salesTranss.createdAt >= '" + fromDate + "'"
                + " AND S.salesTranss.createdAt <= '" + toDate + "' ";

        return hqlSales;
    }

    private ArrayList<String> unaccent(String text) {
        //  String newText = text.toLowerCase();
        ArrayList<String> searchText = new ArrayList<String>();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            switch (c) {

                case 'a': {
                    searchText.add(text.replace("a", "ă"));
                    searchText.add(text.substring(0, i) + "ă" + text.substring(i + 1));
                    searchText.add(text.replace("a", "â"));
                    searchText.add(text.substring(0, i) + "â" + text.substring(i + 1));
                    break;
                }
                case 'A': {
                    searchText.add(text.replace("A", "ă"));
                    searchText.add(text.substring(0, i) + "ă" + text.substring(i + 1));
                    searchText.add(text.replace("A", "â"));
                    searchText.add(text.substring(0, i) + "â" + text.substring(i + 1));
                    break;
                }

                case 'd': {
                    searchText.add(text.replace("d", "đ"));
                    searchText.add(text.substring(0, i) + "đ" + text.substring(i + 1));
                    break;
                }
                case 'D': {
                    searchText.add(text.replace("D", "đ"));
                    searchText.add(text.substring(0, i) + "đ" + text.substring(i + 1));
                    break;
                }
                case 'e': {
                    searchText.add(text.replace("e", "ê"));
                    searchText.add(text.substring(0, i) + "ê" + text.substring(i + 1));
                    break;
                }

                case 'E': {
                    searchText.add(text.replace("E", "ê"));
                    searchText.add(text.substring(0, i) + "ê" + text.substring(i + 1));
                    break;
                }

                case 'o': {
                    searchText.add(text.replace("o", "ô"));
                    searchText.add(text.replace("o", "ơ"));
                    searchText.add(text.substring(0, i) + "ô" + text.substring(i + 1));
                    searchText.add(text.substring(0, i) + "ơ" + text.substring(i + 1));
                    break;
                }

                case 'O': {
                    searchText.add(text.replace("O", "ô"));
                    searchText.add(text.replace("O", "ơ"));
                    searchText.add(text.substring(0, i) + "ô" + text.substring(i + 1));
                    searchText.add(text.substring(0, i) + "ơ" + text.substring(i + 1));
                    break;
                }
                case 'u': {
                    searchText.add(text.replace("u", "ư"));
                    searchText.add(text.substring(0, i) + "ư" + text.substring(i + 1));
                    break;
                }
                case 'U': {
                    searchText.add(text.replace("U", "ư"));
                    searchText.add(text.substring(0, i) + "ư" + text.substring(i + 1));
                    break;
                }

            }

        }

        return searchText;
    }
    
    public String getStringHqlOfSalesTransDetailsForSalesSup(int idUser, String fromDate, String toDate){
    	 String hqlSales = "Select S.id as id, S.quantity as quantity, S.price as price, S.salesTranss.createdAt as date, S.isFocus as isFocus "
                 + " from SalesTransDetails as S, MCPDetails as M, UserRoleChannel as URC"
                 + " where S.salesTranss.transType = 2 AND M.deletedUser = 0 and M.mcps.deletedUser = 0 "
                 + " AND S.salesTranss.deletedUser = 0 AND S.deletedUser = 0 AND URC.deletedUser = 0"
                 + " AND URC.users.id = S.salesTranss.createdUser"
                 + " AND S.salesTranss.toStocks.poss.id = M.poss.id"
                 + " AND WEEK(S.salesTranss.createdAt) = WEEK(M.mcps.beginDate)"
 				 + " AND YEAR(S.salesTranss.createdAt) = YEAR(M.mcps.beginDate)"
                 + " AND M.mcps.implementEmployees.id = "+ idUser
                 + " AND URC.userRoles.id != 6"
                 + " AND S.salesTranss.createdAt >= '" + fromDate + "'"
                 + " AND S.salesTranss.createdAt <= '" + toDate + "'"
                 + " GROUP BY S.id";

         return hqlSales;
    }

    public String getStringHqlForSalesOrder(int idUser, String fromDate, String toDate){
    	 String hqlSales = "Select S.id as id, S.quantity as quantity, S.price as price, S.orders.createdAt as date, S.isFocus as isFocus "
                + " from SalesOrderDetails as S,MCPDetails as M WHERE S.deletedUser = 0 AND S.orders.deletedUser = 0"
				+ " AND M.deletedUser = 0 AND M.mcps.deletedUser = 0 " 
                + " AND S.orders.pos.id = M.poss.id"
				+ " AND WEEKOFYEAR(S.orders.createdAt) = WEEKOFYEAR(M.mcps.beginDate)"
				+ " AND YEAR(S.orders.createdAt) = YEAR(M.mcps.beginDate)"
				+ " AND S.orders.statuss.id not in (14,13,12)"
				+ " AND M.mcps.implementEmployees.id = "+idUser
				+ " AND S.orders.createdAt >= '" +fromDate+"'"
				+ " AND S.orders.createdAt <= '" +toDate+"'" ;
    	 hqlSales += " GROUP BY S.id";
    	return hqlSales;
    }
}
