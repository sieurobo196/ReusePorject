/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.app.service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.CompanyConfig;
import vn.itt.msales.entity.CompanyConfigDetails;
import vn.itt.msales.entity.CompanyConfigKpi;
import vn.itt.msales.entity.CompanyConstant;
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.Equipment;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsCategory;
import vn.itt.msales.entity.GoodsUnit;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.MCPDetails;
import vn.itt.msales.entity.MsalesCompany;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.Promotion;
import vn.itt.msales.entity.PromotionAccumulationRetailer;
import vn.itt.msales.entity.PromotionGoodsRef;
import vn.itt.msales.entity.PromotionTransRetailer;
import vn.itt.msales.entity.SalesOrder;
import vn.itt.msales.entity.SalesOrderDetails;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.SalesStockGoods;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.SalesTransDetails;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.Version;
import vn.itt.msales.entity.Workflow;
import vn.itt.msales.entity.WorkflowDetails;
import vn.itt.msales.entity.WorkflowType;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.searchObject.AccumulationInfo;
import vn.itt.msales.services.DataService;

/**
 *
 * @author vtm
 */
public class AppServiceImpl implements AppService {

    @Override
    public List<HashMap> searchSupOrderApp(List<Integer> userIds, Date fromDate, Date toDate, DataService dataService) {
        Date nextDate = DateUtils.getNextDate(toDate);
        //status co id = 18,19,20 <=> value 4,5,6 la da giao hang thanh cong
        String hql = "SELECT SalesOrder.id AS id,"
                + " SalesOrder.pos.id as posId,"
                + " SalesOrder.pos.name as posName,"
                + " SUM(SalesOrderDetails.price * SalesOrderDetails.quantity) AS totalPrice,"
                + " SUM(SalesOrderDetails.quantity) AS totalQuantity,"
                + " CASE WHEN SalesOrder.statuss.value IN (4,5,6) THEN TRUE ELSE FALSE END AS orderSuccess"
                + " FROM SalesOrderDetails AS SalesOrderDetails"
                + " JOIN SalesOrderDetails.orders AS SalesOrder"
                + " WHERE SalesOrder.createdAt >= :fromDate"
                + " AND SalesOrder.createdAt < :nextDate"
                + " AND (SalesOrder.createdUsers.id IN (:userIds) OR SalesOrder.pos.id IN (:ids))"
                + " AND SalesOrder.deletedUser = 0"
                + " AND SalesOrder.statuss.value != 3"//3 = huy
                + " GROUP BY SalesOrder.id";
        List<MsalesParameter> parameters = MsalesParameter.createList("fromDate", fromDate, 2);
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        parameters.add(MsalesParameter.create("userIds", userIds, 1));
        List<Integer> ids = getListPOSIdByListUserId(userIds, dataService);
        if (ids.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }
        return dataService.executeSelectHQL(hql, parameters, true, 0, 0);
    }

    @Override
    public List<HashMap> getOrderGoodsDetails(int orderId, DataService dataService) {
        String hqlGoodsList = "SELECT SalesOrderDetails.goodss.id AS id,"
                + " SalesOrderDetails.goodss.name AS name,"
                + " SalesOrderDetails.quantity AS quantity,"
                + " SUM(SalesOrderDetails.quantity*SalesOrderDetails.price) AS total"
                + " FROM SalesOrderDetails AS SalesOrderDetails"
                + " WHERE SalesOrderDetails.orders.id = :orderId"
                + " AND SalesOrderDetails.deletedUser = 0"
                + " GROUP BY SalesOrderDetails.id";
        List<MsalesParameter> parameters = MsalesParameter.createList("orderId", orderId);
        return dataService.executeSelectHQL(hqlGoodsList, parameters, true, 0, 0);
    }

    @Override
    public Integer getMCPId(int userId, Date date, DataService dataService) {
        String hql = "SELECT id"
                + " FROM MCP"
                + " WHERE deletedUser = 0"
                + " AND isActive = 1"
                + " AND implementEmployees.id = :userId"
                + " AND beginDate = :beginDate"
                + " AND type = 1";

        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("beginDate", date, 2));

        List<Integer> mcpList = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        if (mcpList.isEmpty()) {
            return null;
        } else {
            return mcpList.get(0);
        }
    }

    @Override
    public MCP getMCPNow(int userId, DataService dataService) {
        String hql = "FROM MCP"
                + " WHERE deletedUser = 0"
                + " AND isActive = 1"
                + " AND implementEmployees.id = :userId"
                + " AND beginDate = :beginDate"
                + " AND type = 1";

        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("beginDate", DateUtils.getShortNow(), 2));

        List<MCP> mcpList = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        if (mcpList.isEmpty()) {
            return null;
        } else {
            return mcpList.get(0);
        }
    }

    @Override
    public List<MCP> getMCP(int userId, Date fromDate, Date toDate, DataService dataService) {
        String hql = "FROM MCP"
                + " WHERE deletedUser = 0"
                + " AND isActive = 1"
                + " AND implementEmployees.id = :userId"
                + " AND beginDate >= :fromDate"
                + " AND beginDate <= :toDate"
                + " AND type = 1";

        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("fromDate", fromDate, 2));
        parameters.add(MsalesParameter.create("toDate", toDate, 2));

        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<MCPDetails> getListMCPDetails(int mcpId, DataService dataService) {
        //lay danh sach mcpDetails
        ParameterList parameterList = new ParameterList();
        parameterList.add("mcps.id", mcpId);
        parameterList.add("isActive", 1);
        parameterList.add("statuss.id", 1);
        return dataService.getListOption(MCPDetails.class, parameterList);
    }

    @Override
    public int createMCP(int userId, Date date, DataService dataService) {
        User user = dataService.getRowById(userId, User.class);
        if (user == null) {
            return 0;
        }

        //create MCP neu chua co
        Date beginDate = DateUtils.getShortDate(date);
        Date finishDate = DateUtils.addSecond(DateUtils.getNextDate(beginDate), -1);
        Integer mcpId = getMCPId(userId, beginDate, dataService);
        if (mcpId == null) {
            MCP mcp = new MCP();
            mcp.setType(1);

            mcp.setName(user.getLastName() + " " + user.getFirstName() + " - " + DateUtils.getDayString(beginDate));
            mcp.setImplementEmployees(user);
            Status status = new Status();
            status.setId(1);
            mcp.setStatuss(status);

            mcp.setBeginDate(beginDate);
            //set to 23:59:59
            mcp.setFinishTime(finishDate);
            mcp.setIsActive(1);
            mcp.setCreatedUser(user.getId());
            //tao moi mcp
            dataService.insertRow(mcp);

            //copy cac MCPdetails cua thu này tuan truoc            
            //get MCP cua ngay nay tuan truoc
            Integer preMCPId = getMCPId(userId, DateUtils.addDay(beginDate, -7), dataService);

            if (preMCPId != null) {
                //lay danh sach mcpDetails                
                List<MCPDetails> mcpDetailsList = getListMCPDetails(preMCPId, dataService);
                if (!mcpDetailsList.isEmpty()) {
                    //set now 23h59m59s de set finishTime
                    for (MCPDetails mcpDetails : mcpDetailsList) {
                        //set lai cac truong
                        mcpDetails.setMcpId(mcp.getId());
                        mcpDetails.setImplementEmployeeId(userId);//truong hop loi
                        mcpDetails.setIsActive(1);
                        mcpDetails.setFinishTime(finishDate);
                        mcpDetails.setCreatedUser(user.getId());
                        mcpDetails.setCreatedAt(new Date());
                        mcpDetails.setUpdatedUser(0);
                        mcpDetails.setUpdatedAt(null);
                        mcpDetails.setDeletedAt(null);
                        mcpDetails.setDeletedUser(0);
                    }
                    dataService.insertArray(mcpDetailsList);
                }
            }
            return mcp.getId();
        } else {
            return mcpId;
        }
    }

    @Override
    public int createMCP(int userId, DataService dataService) {
        Date now = DateUtils.getShortNow();
        return createMCP(userId, now, dataService);
    }

    @Override
    public Integer getBranchFromUsername(String username, boolean deleted, DataService dataService) throws Exception {
        String domain = username.split("@")[1];
        String hql = "FROM MsalesCompany"
                + " WHERE code = :code";
        List<MsalesParameter> parameters = MsalesParameter.createList("code",domain);
        if(!deleted){
            hql+=" AND deletedUser = 0";
        }
        List<MsalesCompany> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        if (list.isEmpty()) {
            //domain not exist
            return null;
        } else {
            return list.get(0).getBranch();
        }
    }

    @Override
    public User getUser(String username, String password, DataService dataService) {
        ParameterList parameterList = new ParameterList("username", username, 1, 1);
        parameterList.add("password", password.toUpperCase());
        List<User> userList = dataService.getListOption(User.class, parameterList);
        if (userList.isEmpty()) {
            return null;
        } else {
            return userList.get(0);
        }
    }

    @Override
    public boolean checkUserRegister(int userId, String imei, String subscriberId, DataService dataService) {
        String hql = "SELECT id"
                + " FROM Equipment"
                + " WHERE deletedUser = 0"
                + " AND users.id = :userId"
                + " AND isActive = true"
                + " AND imei != :imei"
                + " AND subscriberId != :subscriberId";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("imei", imei));
        parameters.add(MsalesParameter.create("subscriberId", subscriberId));
        List<Integer> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return !list.isEmpty();
    }

    @Override
    public Equipment getEquipment(String imei, String subscriberId, DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("imei", imei);
        parameterList.add("subscriberId", subscriberId);
        parameterList.add("isActive", true);
        List<Equipment> equipments = dataService.getListOption(Equipment.class, parameterList);
        if (equipments.isEmpty()) {
            return null;
        } else {
            return equipments.get(0);
        }
    }

    @Override
    public Equipment getEquipmentByUser(int userId, DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("users.id", userId);
        parameterList.add("isActive", true);
        List<Equipment> equipments = dataService.getListOption(Equipment.class, parameterList);
        if (equipments.isEmpty()) {
            return null;
        } else {
            return equipments.get(0);
        }
    }

    @Override
    public Version getVersion(int companyId, DataService dataService) {
        ParameterList parameterList = new ParameterList("isActive", 1, 1, 1);
        parameterList.add("companys.id", companyId);
        List<Version> versionList = dataService.getListOption(Version.class, parameterList);
        if (versionList.isEmpty()) {
            return null;
        } else {
            return versionList.get(0);
        }
    }

    @Override
    public List<CompanyConfig> getListCompanyConfig(int companyId, int userRoleId, DataService dataService) {
        ParameterList parameterList = new ParameterList("companys.id", companyId, 0, 0);
        parameterList.add("userRoles.id", userRoleId);
        parameterList.add("isActive", 1);
        return dataService.getListOption(CompanyConfig.class, parameterList);
    }

    @Override
    public List<CompanyConfigDetails> getListCompanyConfigDetails(int companyConfigId, DataService dataService) {
        ParameterList parameterList = new ParameterList("companyConfigs.id", companyConfigId, 0, 0);
        parameterList.add("isActive", 1);
        parameterList.setOrder("order");
        return dataService.getListOption(CompanyConfigDetails.class, parameterList);
    }

    @Override
    public List<CompanyConfigDetails> getListHistoryFunctionDetails(int companyConfigId, DataService dataService) {
        //get list CompanyConfigDetails
        String hql = "FROM CompanyConfigDetails"
                + " WHERE companyConfigs.id = :companyConfigId"
                + " AND code IN ('order_page','care_page','sell_page','sales_page','direct_sales_page','sales_via_phone_page','order_via_phone_page')"
                + " AND deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyConfigId", companyConfigId);

        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    //get List WorkflowType
    public List<WorkflowType> getListWorkflowType(int type, DataService dataService) {
        ParameterList parameterList = new ParameterList("type", type);
        parameterList.setOrder("order");
        return dataService.getListOption(WorkflowType.class, parameterList);
    }

    @Override
    public List<Workflow> getListWorkflowByWorkflowType(int workflowTypeId, int companyId, DataService dataService) {
        String hql = "FROM Workflow"
                + " WHERE deletedUser = 0"
                + " AND companys.id = :companyId"
                + " AND workflowTypes.id = :workflowTypeId"
                + " AND workflowDetails IS NULL"
                + " ORDER BY order";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("workflowTypeId", workflowTypeId));
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<Workflow> getListWorkflowByWorkflowDetails(int workflowDetailsId, DataService dataService) {
        ParameterList parameterList = new ParameterList("workflowDetails.id", workflowDetailsId);
        return dataService.getListOption(Workflow.class, parameterList);
    }

    @Override
    public List<Workflow> getListCustomerCareWorkflow(int type, int companyId, DataService dataService) {
        //get workflow       
        String hql = "FROM Workflow"
                + " WHERE deletedUser = 0"
                + " AND workflowTypes.type = " + type//mac dinh 1= cham soc
                + " AND companys.id = :companyId"
                + " ORDER BY order";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<WorkflowDetails> getListWorkflowDetails(int worlflowId, DataService dataService) {
        ParameterList parameterList = new ParameterList();
        parameterList.add("workflows.id", worlflowId);
        return dataService.getListOption(WorkflowDetails.class, parameterList);
    }

    @Override
    public User getUser(int id, String password, DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("id", id);
        parameterList.add("password", password.toUpperCase());
        List<User> userList = dataService.getListOption(User.class, parameterList);
        if (userList.isEmpty()) {
            return null;
        } else {
            return userList.get(0);
        }
    }

    @Override
    public List<HashMap> getListMCP(int userId, Date fromDate, Date toDate, DataService dataService) {
        String hql = "SELECT MCPDetails.poss.id AS posId,MCPDetails.poss.name AS posName,"
                + " MCPDetails.poss.posCode AS posCode,MCPDetails.poss.address AS address,"
                + " MCPDetails.poss.lat AS lat,MCPDetails.poss.lng AS lng,"
                + " MCPDetails.id as mcpDetailsId,"
                + " CASE WHEN (CustomerCareInformation IS NULL OR CustomerCareInformation.deletedUser!=0) THEN FALSE ELSE TRUE END AS isVisited"
                + " FROM CustomerCareInformation AS CustomerCareInformation"
                + " RIGHT JOIN CustomerCareInformation.mcpDetailss AS MCPDetails"
                + " WHERE MCPDetails.mcps.implementEmployees.id = :userId"
                + " AND MCPDetails.mcps.beginDate >= :fromDate"
                + " AND MCPDetails.mcps.beginDate <= :toDate"
                + " AND MCPDetails.deletedUser = 0"
                + " AND MCPDetails.mcps.deletedUser = 0"
                + " AND MCPDetails.poss.deletedUser = 0"
                + " AND MCPDetails.isActive = 1"
                //+ " AND (CustomerCareInformation IS NULL OR CustomerCareInformation.deletedUser = 0)"
                + " GROUP BY MCPDetails.id"
                + " ORDER BY MCPDetails.poss.name";

        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("fromDate", fromDate, 2));
        parameters.add(MsalesParameter.create("toDate", toDate, 2));
        return dataService.executeSelectHQL(hql, parameters, true, 0, 0);
    }

    @Override
    public List<HashMap> searchPOS(int userId, String posCode, DataService dataService) {
        Date beginDate = DateUtils.getShortNow();
        String hql = "SELECT MCPDetails.poss.id AS posId,MCPDetails.poss.name AS posName,"
                + " MCPDetails.poss.posCode AS posCode,MCPDetails.poss.address AS address,"
                + " MCPDetails.poss.lat AS lat,MCPDetails.poss.lng AS lng,"
                + " MCPDetails.id as mcpDetailsId,"
                //+ " CASE WHEN CustomerCareInformation.id IS NULL THEN FALSE ELSE TRUE END AS isVisited"
                + " CASE WHEN (CustomerCareInformation IS NULL OR CustomerCareInformation.deletedUser!=0) THEN FALSE ELSE TRUE END AS isVisited"
                + " FROM CustomerCareInformation AS CustomerCareInformation"
                + " RIGHT JOIN CustomerCareInformation.mcpDetailss AS MCPDetails"
                + " WHERE MCPDetails.mcps.implementEmployees.id = :userId"
                + " AND MCPDetails.mcps.beginDate = :beginDate"
                + " AND MCPDetails.mcps.deletedUser = 0"
                + " AND MCPDetails.deletedUser = 0"
                + " AND MCPDetails.isActive = 1"
                + " AND MCPDetails.mcps.deletedUser = 0"
                + " AND MCPDetails.poss.posCode LIKE :posCode "
                //+ " AND (CustomerCareInformation IS NULL OR CustomerCareInformation.deletedUser = 0)"
                + " GROUP BY MCPDetails.id"
                + " ORDER BY MCPDetails.poss.name";

        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("beginDate", beginDate, 2));
        parameters.add(MsalesParameter.create("posCode", "%" + posCode));
        return dataService.executeSelectHQL(hql, parameters, true, 0, 0);
    }

    @Override
    public MCP getMCPSale(int userId, Date date, DataService dataService)//ban han
    {
        Date beginDate = DateUtils.getFirstDayOfMonth(date);
        String hql = "FROM MCP"
                + " WHERE deletedUser = 0"
                + " AND implementEmployees.id = :userId"
                + " AND beginDate = :beginDate"
                + " AND type = 2";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("beginDate", beginDate, 2));//type=2=>date
        List<MCP> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public MCP getMCP(int userId, Date date, DataService dataService) {
        String hql = "FROM MCP"
                + " WHERE deletedUser = 0"
                + " AND implementEmployees.id = :userId"
                + " AND beginDate = :beginDate"
                + " AND type = 1";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("beginDate", date, 2));//type=2=>date
        List<MCP> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public Integer getMCPSaleId(int userId, DataService dataService)//ban hang
    {
        Date beginDate = DateUtils.getFirstDayOfMonth();
        String hql = "SELECT id"
                + " FROM MCP"
                + " WHERE deletedUser = 0"
                + " AND implementEmployees.id = :userId"
                + " AND beginDate = :beginDate"
                + " AND type = 2";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("beginDate", beginDate));//type=2=>date
        List<Integer> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public long getPOSNew(int userId, DataService dataService) {
        String hqlPOSNew = "SELECT COUNT(id)"
                + " FROM POS"
                + " WHERE createdUser = :userId"
                + " AND createdAt >= :minDate"
                + " AND createdAt < :maxDate"
                //+ " AND hierarchy = 0"
                + " AND deletedUser = 0";

        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        Date minDate = DateUtils.getFirstDayOfMonth();
        parameters.add(MsalesParameter.create("minDate", minDate, 2));//type2 = date                     
        Date maxDate = DateUtils.getNextDate(DateUtils.getLastDayOfMonth());
        parameters.add(MsalesParameter.create("maxDate", maxDate, 2));//type2 = date

        List<Long> ret = dataService.executeSelectHQL(hqlPOSNew, parameters, false, 0, 0);
        return ret.get(0) == null ? 0 : ret.get(0);
    }

    @Override
    public List<SalesStockGoods> getStockApp(int userId, DataService dataService) {
        String hql = "FROM SalesStockGoods"
                + " WHERE stocks.salemanUsers.id = :userId"
                + " AND stocks.deletedUser = 0"
                + " AND deletedUser = 0"
                + " AND stocks.statuss.id = 1"//status id = 1=> salestock dang hoat dong
                + " AND isActive = 1"//dang hoat dong
                + " AND quantity > 0"
                + " ORDER BY goodss.goodsCategorys.id,goodss.id";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<GoodsUnit> getListGoodsUnitByGoodsId(int goodsId, DataService dataService) {
        //List<GoodsUnit> ret = new ArrayList<>();
        String hql = "FROM GoodsUnit"
                + " WHERE deletedUser = 0"
                + " AND units.deletedUser = 0"
                + " AND goodss.id = :goodsId"
                + " AND isActive = 1"
                + " ORDER BY quantity DESC";
        List<MsalesParameter> parameters = MsalesParameter.createList("goodsId", goodsId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
//        if (!list.isEmpty()) {
        //tim goodsUnit min
//            GoodsUnit GoodsUnitMin = null;
//            for (GoodsUnit goodsUnit : list) {
//                if (goodsUnit.getChildUnitIds() == null) {
//                    break;
//                }
//                if (Objects.equals(goodsUnit.getUnits().getId(), goodsUnit.getChildUnitIds().getId())) {
//                    GoodsUnitMin = goodsUnit;
//                    list.remove(goodsUnit);
//                    break;
//                }
//            }
//
//            if (GoodsUnitMin != null) {
//                ret.add(GoodsUnitMin);
//                int minId = GoodsUnitMin.getUnits().getId();
//                while (!list.isEmpty()) {
//                    int tempId = minId;
//                    for (GoodsUnit goodsUnit : list) {
//                        if (goodsUnit.getChildUnitIds().getId() == minId) {
//                            minId = goodsUnit.getUnits().getId();
//                            //tinh lai quantity
//                            for (GoodsUnit goodsUnitChild : ret) {
//                                goodsUnit.setQuantity(goodsUnit.getQuantity() * goodsUnitChild.getQuantity());
//                                break;
//                            }
//                            ret.add(0, goodsUnit);
//                            list.remove(goodsUnit);
//                            break;
//                        }
//                    }
//                    if (tempId == minId) {
//                        break;
//                    }
//                }
//            }

        //   }
        // return ret;
    }

    @Override
    public GoodsUnit getMinGoodsUnit(int goodsId, DataService dataService) {
        List<GoodsUnit> list = getListGoodsUnitByGoodsId(goodsId, dataService);
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    @Override
    public List<UserRoleChannel> getListUserRoleChannel(int userId, DataService dataService) {
        ParameterList parameterList = new ParameterList(0, 0);
        parameterList.add("users.id", userId);
        return dataService.getListOption(UserRoleChannel.class, parameterList);
    }

    @Override
    public List<Integer> getListUserRoleChannelId(int userId, DataService dataService) {
        String hql = "FROM UserRoleChannel.channels.id"
                + " WHERE deletedUser = 0"
                + " AND users.id = :userId";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<Channel> getListChannel(int userId, DataService dataService) {
        List<Channel> channelList = new ArrayList<>();
        List<UserRoleChannel> userRoleChannelList = getListUserRoleChannel(userId, dataService);
        for (UserRoleChannel userRoleChannel : userRoleChannelList) {
            channelList.add(userRoleChannel.getChannels());
        }
        return channelList;
    }

    @Override
    public List<Integer> getListChannelId(int userId, DataService dataService) {
        List<Integer> channelIdList = new ArrayList<>();
        List<UserRoleChannel> userRoleChannelList = getListUserRoleChannel(userId, dataService);
        for (UserRoleChannel userRoleChannel : userRoleChannelList) {
            channelIdList.add(userRoleChannel.getChannels().getId());
        }
        return channelIdList;
    }

    @Override
    public List<Channel> getListChannelByParent(List<Integer> ids, DataService dataService) {
        String hql = "FROM Channel"
                + " WHERE deletedUser = 0"
                + " AND parents.id IN (:ids)";
        List<MsalesParameter> parameters = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<User> getListUserBySup(int userId, int companyId, DataService dataService) {
        List<Channel> channelList = getListChannel(userId, dataService);
        if (channelList.isEmpty()) {
            return new ArrayList<>();
        }

        //lay tat ca nhan vien thuoc giam sat
        String hql = "SELECT DISTINCT UserRoleChannel.users"
                + " FROM UserRoleChannel AS UserRoleChannel"
                + " WHERE UserRoleChannel.deletedUser = 0"
                + " AND UserRoleChannel.userRoles.id = 6"
                + " AND UserRoleChannel.users.deletedUser = 0"
                + " AND UserRoleChannel.users.statuss.value = 1"
                + " AND UserRoleChannel.users.companys.id = :companyId";//dang lam viec

        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        int idx = 1;
        hql += " AND (1 > 1";
        for (Channel channel : channelList) {
            if (channel != null) {
                hql += " OR UserRoleChannel.channels.fullCode LIKE :channelCode" + idx + " ESCAPE '!'";
                parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode() + "!_%"));
            }
        }
        hql += ")";
        hql += " ORDER BY UserRoleChannel.users.lastName,UserRoleChannel.users.firstName"; //chi lay nhan vien => role=6

        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<Integer> getListUserIdBySup(int userId, int companyId, DataService dataService) {
        List<User> userList = getListUserBySup(userId, companyId, dataService);
        List<Integer> list = new ArrayList<>();
        for (User user : userList) {
            list.add(user.getId());
        }
        return list;
    }

    @Override
    public List<HashMap> getCbListUserBySup(int userId, int companyId, DataService dataService) {
        List<HashMap> list = new ArrayList<>();
        List<User> userList = getListUserBySup(userId, companyId, dataService);
        for (User user : userList) {
            HashMap hashMap = new HashMap();
            hashMap.put("id", user.getId());
            hashMap.put("name", user.getLastName() + " " + user.getFirstName());
            list.add(hashMap);
        }
        return list;
    }

    @Override
    public List<User> getListUserByChannel(int channelId, DataService dataService) {
        String hql = "SELECT DISTINCT UserRoleChannel.users"
                + " FROM UserRoleChannel AS UserRoleChannel"
                + " WHERE UserRoleChannel.deletedUser = 0"
                + " AND UserRoleChannel.users.deletedUser = 0"
                + " AND UserRoleChannel.channels.id = :channelId"
                + " AND UserRoleChannel.userRoles.id = 6"//chi lay nhan vien => role=6
                + " ORDER BY UserRoleChannel.users.lastName,UserRoleChannel.users.firstName";

        List<MsalesParameter> parameters = MsalesParameter.createList("channelId", channelId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<HashMap> getCbListUserByChannel(int channelId, DataService dataService) {
        String hql = "SELECT DISTINCT UserRoleChannel.users.id AS id,"
                + " CONCAT(UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name"
                + " FROM UserRoleChannel AS UserRoleChannel"
                + " WHERE UserRoleChannel.deletedUser = 0"
                + " AND UserRoleChannel.users.deletedUser = 0"
                + " AND UserRoleChannel.channels.id = :channelId"
                + " AND UserRoleChannel.userRoles.id = 6"//chi lay nhan vien => role=6
                + " ORDER BY UserRoleChannel.users.lastName,UserRoleChannel.users.firstName";

        List<MsalesParameter> parameters = MsalesParameter.createList("channelId", channelId);
        return dataService.executeSelectHQL(hql, parameters, true, 0, 0);
    }

    @Override
    public List<User> getAllListUserByChannel(int channelId, int companyId, DataService dataService) {
        List<User> ret = new ArrayList<>();
        Channel channel = dataService.getRowById(channelId, Channel.class);
        if (channel != null) {
            String hql = "SELECT DISTINCT UserRoleChannel.users"
                    + " FROM UserRoleChannel AS UserRoleChannel"
                    + " WHERE UserRoleChannel.deletedUser = 0"
                    + " AND UserRoleChannel.users.deletedUser = 0"
                    + " AND UserRoleChannel.users.companys.id = :companyId"
                    + " AND (UserRoleChannel.channels.fullCode = :channelCode1 OR UserRoleChannel.channels.fullCode LIKE :channelCode2 ESCAPE '!')"
                    + " AND UserRoleChannel.userRoles.id = 6"//chi lay nhan vien => role=6
                    + " ORDER BY UserRoleChannel.users.lastName,UserRoleChannel.users.firstName";

            List<MsalesParameter> parameters = MsalesParameter.createList("channelCode1", channel.getFullCode());
            parameters.add(MsalesParameter.create("companyId", companyId));
            parameters.add(MsalesParameter.create("channelCode2", channel.getFullCode() + "!_%"));
            List<User> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
            ret.addAll(list);
        }
        return ret;
    }

    @Override
    public List<HashMap> getCbAllListUserByChannel(int channelId, int companyId, DataService dataService) {
        List<HashMap> ret = new ArrayList<>();
        List<User> list = getAllListUserByChannel(channelId, companyId, dataService);
        for (User user : list) {
            HashMap hashMap = new HashMap();
            hashMap.put("id", user.getId());
            hashMap.put("name", user.getLastName() + " " + user.getFirstName());
            ret.add(hashMap);
        }

        return ret;
    }

    @Override
    public List<POS> getListPOSByListUserId(List<Integer> userIds, DataService dataService) {
        Date minDate = DateUtils.getMondayOfWeek();
        Date maxDate = DateUtils.getSundayOfWeek();

        String hql = "SELECT DISTINCT MCPDetails.poss"
                + " FROM MCPDetails AS MCPDetails"
                + " JOIN MCPDetails.mcps AS MCP"
                + " WHERE MCPDetails.deletedUser = 0"
                + " AND MCP.deletedUser = 0"
                + " AND MCP.statuss.value = 1"//status value =1=> con hoat dong
                + " AND MCP.beginDate >= :minDate"
                + " AND MCP.beginDate <= :maxDate"
                + " AND MCP.implementEmployees.id IN (:userIds)"
                + " AND MCPDetails.isActive = 1"
                + " AND MCPDetails.poss.deletedUser = 0";
        //List<MsalesParameter> parameters = new ArrayList<>();
        List<MsalesParameter> parameters = MsalesParameter.createList("minDate", minDate, 2);//type =2 => Date
        parameters.add(MsalesParameter.create("maxDate", maxDate, 2));

        if (userIds == null || userIds.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("userIds"));
        } else {
            parameters.add(MsalesParameter.create("userIds", userIds, 1));//type=1=> collection
        }
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<Integer> getListPOSIdByListUserId(List<Integer> userIds, DataService dataService) {
        List<Integer> ret = new ArrayList<>();
        List<POS> list = getListPOSByListUserId(userIds, dataService);
        for (POS pos : list) {
            ret.add(pos.getId());
        }
        return ret;
    }

    @Override
    public List<Integer> getListPOSIdByUser(int userId, Date fromDate, Date toDate, DataService dataService) {
        String hql = "SELECT DISTINCT MCPDetails.poss.id"
                + " FROM MCPDetails AS MCPDetails"
                + " JOIN MCPDetails.mcps AS MCP"
                + " WHERE MCPDetails.deletedUser = 0"
                + " AND MCPDetails.isActive = 1"
                + " AND MCP.statuss.value = 1"//status value = 1=>con hoat dong
                + " AND MCP.deletedUser = 0"
                + " AND MCP.beginDate >= :fromDate"
                + " AND MCP.beginDate <= :toDate"
                + " AND MCP.type = 1"
                + " AND MCP.implementEmployees.id = :userId";
        List<MsalesParameter> parameters = MsalesParameter.createList("fromDate", fromDate, 2);//type =2 => Date
        parameters.add(MsalesParameter.create("toDate", toDate, 2));//type =2 => Date
        parameters.add(MsalesParameter.create("userId", userId));

        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<Integer> getListPOSIdByListUserId(List<Integer> ids, Date fromDate, Date toDate, DataService dataService) {
        String hql = "SELECT DISTINCT MCPDetails.poss.id"
                + " FROM MCPDetails AS MCPDetails"
                + " JOIN MCPDetails.mcps AS MCP"
                + " WHERE MCPDetails.deletedUser = 0"
                + " AND MCPDetails.isActive = 1"
                + " AND MCP.statuss.value = 1"//status value = 1=>con hoat dong
                + " AND MCP.deletedUser = 0"
                + " AND MCP.beginDate >= :fromDate"
                + " AND MCP.beginDate <= :toDate"
                + " AND MCP.type = 1"
                + " AND MCP.implementEmployees.id IN (:ids)";
        List<MsalesParameter> parameters = MsalesParameter.createList("fromDate", fromDate, 2);//type =2 => Date
        parameters.add(MsalesParameter.create("toDate", toDate, 2));//type =2 => Date
        if (ids == null || ids.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }

        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<MCPDetails> getListMCPDetailsByUser(int userId, Date beginDate, DataService dataService) {
        String hql = "FROM MCPDetails"
                + " WHERE mcps.beginDate = :beginDate"
                + " AND mcps.implementEmployees.id = :userId"
                + " AND mcps.deletedUser = 0"
                + " AND mcps.type = 1"
                + " AND deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("beginDate", beginDate, 2);//type =2 => Date        
        parameters.add(MsalesParameter.create("userId", userId));
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<MCPDetails> getListMCPDetailsByPOS(int posId, Date beginDate, DataService dataService) {
        String hql = "FROM MCPDetails"
                + " WHERE mcps.beginDate = :beginDate"
                + " AND poss.id = :posId"
                + " AND mcps.deletedUser = 0"
                + " AND mcps.type = 1"
                + " AND deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("beginDate", beginDate, 2);//type =2 => Date        
        parameters.add(MsalesParameter.create("posId", posId));
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<HashMap> searchSupRouteApp(int userId, Date beginDate, DataService dataService) {
        //tim mcp details        
        String hql = "SELECT MCPDetails.id as mcpDetailsId,MCPDetails.poss AS pos,"
                + " CASE WHEN (CustomerCareInformation IS NULL OR CustomerCareInformation.deletedUser!=0) THEN FALSE ELSE TRUE END AS isVisited,"
                + " CASE WHEN CustomerCareInformation.finishCustomerCareAt IS NULL THEN NULL ELSE DATE_FORMAT(CustomerCareInformation.finishCustomerCareAt,'%d/%m/%Y %H:%i:%S') END AS timeCared"
                + " FROM CustomerCareInformation as CustomerCareInformation"
                + " RIGHT JOIN CustomerCareInformation.mcpDetailss as MCPDetails"
                + " WHERE MCPDetails.mcps.implementEmployees.id = :userId"
                + " AND MCPDetails.mcps.beginDate = :beginDate"
                + " AND MCPDetails.mcps.deletedUser = 0"
                + " AND MCPDetails.isActive = 1"
                + " AND MCPDetails.deletedUser = 0";

        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("beginDate", beginDate, 2));
        return dataService.executeSelectHQL(hql, parameters, true, 0, 0);
    }

    @Override
    public List<HashMap> getListUserRoute(int userId, Date date, DataService dataService) {
        Date nextDate = DateUtils.getNextDate(date);

        String hqlRoute = "SELECT lat AS lat,lng AS lng,routeAt AS routeAt,note AS note"
                + " FROM UserRoute"
                + " WHERE createdUser = :userId"
                + " AND routeAt >= :routeAt"
                + " AND routeAt < :nextDate"
                + " AND deletedUser = 0"
                + " ORDER BY id DESC";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("routeAt", date, 2));//type =2=>Date
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));//type =2=>Date
        return dataService.executeSelectHQL(hqlRoute, parameters, true, 0, 0);
    }

    @Override
    public Date checkLastOrder(int posId, Date date, DataService dataService) {
        //tim SalesOrder/salesTrans  sau cung
        Date nextDate = DateUtils.getNextDate(date);
        String hqlOrder = "SELECT updatedAt"
                + " FROM SalesOrder"
                + " WHERE deletedUser = 0"
                + " AND pos.id = :posId"
                + " AND updatedAt >= :date"
                + " AND updatedAt < :nextDate"
                + " AND statuss.value In (4,5,6)" //đã giao
                + " ORDER BY updatedAt DESC";

        List<MsalesParameter> parameters = MsalesParameter.createList("posId", posId);
        parameters.add(MsalesParameter.create("date", date, 2));
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        List<Date> list = dataService.executeSelectHQL(hqlOrder, parameters, false, 1, 1);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public Date checkLastSale(int posId, Date date, DataService dataService) {
        //tim SalesOrder/salesTrans  sau cung
        Date nextDate = DateUtils.getNextDate(date);
        String hqlSale = "SELECT createdAt"
                + " FROM SalesTrans"
                + " WHERE deletedUser = 0"
                + " AND orders IS NULL"
                + " AND transType = 2"
                + " AND toStocks.poss.id = :posId"
                //+ " AND orders.pos.id = :posId"
                //+ " AND orders.statuss.value != 3" //value = 3 =>salesOder bi huy
                + " AND createdAt >= :date"
                + " AND createdAt < :nextDate"
                + " ORDER BY createdAt DESC";
        List<MsalesParameter> parameters = MsalesParameter.createList("date", date, 2);
        parameters.add(MsalesParameter.create("posId", posId));
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        List<Date> list = dataService.executeSelectHQL(hqlSale, parameters, false, 1, 1);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public long countSalesOrder(int userId, Date fromDate, Date toDate, DataService dataService) {
        Date nexDate = DateUtils.getNextDate(toDate);
        String hql = "SELECT COUNT(id)"
                + " FROM SalesOrder"
                + " WHERE createdUsers.id = :userId"
                + " AND salesTransDate >= :fromDate"
                + " AND salesTransDate < :nexDate"
                + " AND deletedUser = 0"
                + " AND statuss.value != 3";//value =3 => huy

        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("fromDate", fromDate, 2));//type 2=>Date
        parameters.add(MsalesParameter.create("nexDate", nexDate, 2));//type 2=>Date

        List<Long> ret = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return ret.get(0);

    }

    @Override
    public List<SalesOrder> getListSalesOrderByPOS(int posId, Date fromDate, Date toDate, DataService dataService) {
        Date nextDate = DateUtils.getNextDate(toDate);
        String hql = "FROM SalesOrder"
                + " WHERE deletedUser = 0"
                + " AND pos.id = :posId"
                + " AND createdAt >= :fromDate"
                + " AND createdAt < :nextDate"
                + " AND statuss.value != 3"
                + " ORDER BY createdAt DESC";
        List<MsalesParameter> parameters = MsalesParameter.createList("posId", posId);
        parameters.add(MsalesParameter.create("fromDate", fromDate, 2));
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<SalesOrderDetails> getListSalesOrderDetailsByPOS(int posId, Date fromDate, Date toDate, DataService dataService) {
        Date nextDate = DateUtils.getNextDate(toDate);
        String hql = "FROM SalesOrderDetails"
                + " WHERE deletedUser = 0"
                + " AND orders.deletedUser = 0"
                + " AND orders.pos.id = :posId"
                + " AND orders.createdAt >= :fromDate"
                + " AND orders.createdAt < :nextDate"
                + " AND orders.statuss.value != 3"
                + " ORDER BY orders.createdAt DESC";
        List<MsalesParameter> parameters = MsalesParameter.createList("posId", posId);
        parameters.add(MsalesParameter.create("fromDate", fromDate, 2));
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<SalesOrderDetails> getListSalesOrderDetails(int salesOrderId, DataService dataService) {
        String hql = "FROM SalesOrderDetails"
                + " WHERE deletedUser = 0"
                + " AND orders.id = :orderId";
        List<MsalesParameter> parameters = MsalesParameter.createList("orderId", salesOrderId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<SalesTrans> getListSalesTransByPOS(int posId, Date fromDate, Date toDate, int transType, DataService dataService) {
        Date nextDate = DateUtils.getNextDate(toDate);
        String hql = "FROM SalesTrans"
                + " WHERE deletedUser = 0"
                + " AND transType = :transType"
                + " AND createdAt >= :fromDate"
                + " AND createdAt <= :nextDate"
                + " AND toStocks.poss.id = :posId";
        List<MsalesParameter> parameters = MsalesParameter.createList("posId", posId);
        parameters.add(MsalesParameter.create("fromDate", fromDate, 2));
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        parameters.add(MsalesParameter.create("transType", transType));
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<SalesTransDetails> getListSalesTransDetailsByPOS(int posId, Date fromDate, Date toDate, int transType, DataService dataService) {
        Date nextDate = DateUtils.getNextDate(toDate);
        String hql = "FROM SalesTransDetails"
                + " WHERE deletedUser = 0"
                + " AND salesTranss.deletedUser = 0"
                + " AND salesTranss.transType = :transType"
                + " AND salesTranss.createdAt >= :fromDate"
                + " AND salesTranss.createdAt <= :nextDate"
                + " AND salesTranss.toStocks.poss.id = :posId";
        List<MsalesParameter> parameters = MsalesParameter.createList("posId", posId);
        parameters.add(MsalesParameter.create("fromDate", fromDate, 2));
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        parameters.add(MsalesParameter.create("transType", transType));
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<SalesTransDetails> getListSalesTransDetails(int salesTransId, DataService dataService) {
        String hql = "FROM SalesTransDetails"
                + " WHERE deletedUser = 0"
                + " AND salesTranss.id = :salesTransId";
        List<MsalesParameter> parameters = MsalesParameter.createList("salesTransId", salesTransId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public long getSumSalesTrans(int salesTransId, DataService dataService) {
        String hql = "SELECT SUM(price*quantity)"
                + " FROM SalesTransDetails"
                + " WHERE deletedUser = 0"
                + " AND salesTranss.id = :salesTransId";
        List<MsalesParameter> parameters = MsalesParameter.createList("salesTransId", salesTransId);
        List<Long> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.get(0);
    }

    @Override
    public long getTotalPOS(int userId, DataService dataService) {
        Date beginDate = DateUtils.getShortNow();
        String hql = "SELECT COUNT(DISTINCT poss.id)"
                + " FROM MCPDetails"
                + " WHERE deletedUser = 0"
                + " AND isActive = 1"
                + " AND mcps.statuss.value = 1"//status value = 1=>con hoat dong
                + " AND mcps.deletedUser = 0"
                + " AND mcps.beginDate = :beginDate"
                + " AND mcps.implementEmployees.id = :userId";
        List<MsalesParameter> parameters = MsalesParameter.createList("beginDate", beginDate, 2);//type =2 => Date
        parameters.add(MsalesParameter.create("userId", userId));

        List<Long> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.get(0) == null ? 0 : list.get(0);
    }

    @Override
    public long getTotalCared(List<Integer> posIdList, DataService dataService) {
        Date now = DateUtils.getShortNow();
        Date nextDate = DateUtils.getNextDate(now);
        String hql = "SELECT COUNT(DISTINCT poss.id)"
                + " FROM CustomerCareInformation"
                + " WHERE deletedUser = 0"
                + " AND poss.id IN (:posIdList)"
                + " AND createdAt >= :now"
                + " AND createdAt < :nextDate";
        List<MsalesParameter> parameters = MsalesParameter.createList("now", now, 2);//type =2 => Date
        parameters.add(MsalesParameter.create("nextDate", nextDate));
        if (posIdList == null || posIdList.isEmpty()) {
            return 0L;
        } else {
            parameters.add(MsalesParameter.create("posIdList", posIdList, 1));
        }
        List<Long> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.get(0);
    }

    @Override
    public long getTotalOrder(List<Integer> ids, DataService dataService) {
        Date minDate = DateUtils.getShortNow();
        Date nextDate = DateUtils.getNextDate(minDate);
        String hql = "SELECT COUNT(DISTINCT pos.id)"
                + " FROM SalesOrder"
                + " WHERE createdAt >= :minDate"
                + " AND createdAt < :nextDate"
                + " AND pos.id IN (:ids)"
                + " AND deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("minDate", minDate, 2);
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        if (ids == null || ids.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }

        List<Long> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.get(0);
    }

    @Override
    public long getTotalSale(List<Integer> ids, DataService dataService) {
        Date minDate = DateUtils.getShortNow();
        Date nextDate = DateUtils.getNextDate(minDate);
        String hql = "SELECT COUNT(DISTINCT toStocks.poss.id)"
                + " FROM SalesTrans"
                + " WHERE transType = 2"
                + " AND deletedUser = 0"
                + " AND salesTransDate >= :minDate"
                + " AND salesTransDate < :nextDate"
                + " AND toStocks.poss.id IN (:ids)";
        List<MsalesParameter> parameters = MsalesParameter.createList("minDate", minDate, 2);
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        if (ids == null || ids.isEmpty()) {
            return 0L;
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }

        List<Long> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.get(0);
    }

    @Override
    public long getTotalPriceOrder(List<Integer> ids, DataService dataService) {
        Date minDate = DateUtils.getShortNow();
        Date nextDate = DateUtils.getNextDate(minDate);
        String hql = "FROM SalesOrderDetails"
                + " WHERE orders.createdAt >= :minDate"
                + " AND orders.createdAt < :nextDate"
                + " AND orders.pos.id IN (:ids)"
                + " AND orders.deletedUser = 0"
                + " AND deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("minDate", minDate, 2);
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        if (ids == null || ids.isEmpty()) {
            return 0L;
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }

        List<SalesOrderDetails> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        long ret = 0L;
        for (SalesOrderDetails salesOrderDetails : list) {
            ret += 1L * salesOrderDetails.getQuantity() * salesOrderDetails.getPrice();
        }
        return ret;
    }

    @Override
    public long getTotalPriceSale(List<Integer> ids, DataService dataService) {
        Date minDate = DateUtils.getShortNow();
        Date nextDate = DateUtils.getNextDate(minDate);
        String hql = "FROM SalesTransDetails"
                + " WHERE salesTranss.transType = 2"
                + " AND salesTranss.createdAt >= :minDate"
                + " AND salesTranss.createdAt < :nextDate"
                + " AND salesTranss.toStocks.poss.id IN (:ids)"
                + " AND salesTranss.deletedUser = 0"
                + " AND deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("minDate", minDate, 2);
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        if (ids == null || ids.isEmpty()) {
            return 0L;
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }

        List<SalesTransDetails> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        long ret = 0L;
        for (SalesTransDetails salesTransDetails : list) {
            ret += 1L * salesTransDetails.getQuantity() * salesTransDetails.getPrice();
        }
        return ret;
    }

    @Override
    public boolean checkPOSVisited(int mcpDetailsId, DataService dataService) {
        //kiem tra MCPDetails da cham soc chưa.
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("mcpDetailss.id", mcpDetailsId);

        List<CustomerCareInformation> customerCareInformationList = dataService.getListOption(CustomerCareInformation.class, parameterList);
        return !customerCareInformationList.isEmpty();
    }

    @Override
    public boolean checkVisited(int posId, Date date, DataService dataService) {
        Date nextDate = DateUtils.getNextDate(date);
        String hql = "SELECT id"
                + " FROM CustomerCareInformation"
                + " WHERE deletedUser = 0"
                + " AND poss.id = :posId"
                + " AND createdAt >= :date"
                + " AND createdAt < :nextDate";

        List<MsalesParameter> parameters = MsalesParameter.createList("posId", posId);
        parameters.add(MsalesParameter.create("date", date, 2));
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        List<Integer> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return !list.isEmpty();
    }

    @Override
    public int getCountVisited(List<POS> posList, Date fromDate, Date toDate, DataService dataService) {
        Date nextDate = DateUtils.getNextDate(toDate);
        List<Integer> ids = new ArrayList<>();
        for (POS pos : posList) {
            ids.add(pos.getId());
        }

        if (posList == null || posList.isEmpty()) {
            return 0;
        }
        String hql = "SELECT DISTINCT poss.id"
                + " FROM CustomerCareInformation"
                + " WHERE deletedUser = 0"
                + " AND poss.id IN (:ids)"
                + " AND createdAt >= :fromDate"
                + " AND createdAt < :nextDate";

        List<MsalesParameter> parameters = MsalesParameter.createList("ids", ids, 1);
        parameters.add(MsalesParameter.create("fromDate", fromDate, 2));
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        List<Integer> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        return list.size();
    }

    @Override
    public List<Status> getListStatusByStatusType(int statusTypeId, DataService dataService) {
        ParameterList parameterList = new ParameterList("statusTypes.id", statusTypeId);
        return dataService.getListOption(Status.class, parameterList);
    }

    @Override
    public Status getStatusByValue(int statusTypeId, String value, DataService dataService) {
        ParameterList parameterList = new ParameterList("statusTypes.id", statusTypeId, 1, 1);
        parameterList.add("value", value);
        List<Status> list = dataService.getListOption(Status.class, parameterList);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public CompanyConstant getCompanyConstant(int companyId, DataService dataService) {
        ParameterList parameterList = new ParameterList("companys.id", companyId, 1, 1);
        List<CompanyConstant> list = dataService.getListOption(CompanyConstant.class, parameterList);
        if (list.isEmpty()) {
            CompanyConstant companyConstant = new CompanyConstant();
            companyConstant.setPeriodGetPosition(0);
            companyConstant.setPeriodSendPosition(0);
            companyConstant.setDistance(0);
            companyConstant.setMinDistance(0);
            return companyConstant;
        } else {
            return list.get(0);
        }
    }

    @Override
    public CompanyConfigKpi getCompanyConfigKPI(int companyId, DataService dataService) {
        ParameterList parameterList = new ParameterList("companyId", companyId, 1, 1);
        List<CompanyConfigKpi> list = dataService.getListOption(CompanyConfigKpi.class, parameterList);

        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean checkRequireWorkflowTarget(int companyId, int userId, DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("code", "TARGET");
        parameterList.add("companys.id", companyId);
        List<Workflow> list = dataService.getListOption(Workflow.class, parameterList);

        if (!(list.isEmpty() || list.get(0).getIsRequired() == 0)) {
            //require
            if (getMCPSaleId(userId, dataService) == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkRequireWorkflowReceive(int companyId, int userId, DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("code", "RECEIVE_GOODS");
        parameterList.add("companys.id", companyId);
        List<Workflow> list = dataService.getListOption(Workflow.class, parameterList);

        if (!(list.isEmpty() || list.get(0).getIsRequired() == 0)) {
            if (!checkUserReceivedGoods(userId, DateUtils.getShortNow(), dataService)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkUserReceivedGoods(int userId, Date date, DataService dataService) {
        Date nextDate = DateUtils.getNextDate(date);
        String hql = "SELECT COUNT(id)"
                + " FROM SalesTrans"
                + " WHERE deletedUser = 0"
                + " AND transType = 1"
                + " AND salesTransDate >= :date"
                + " AND salesTransDate < :nextDate"
                + " AND createdUser = :userId";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("date", date, 2));
        parameters.add(MsalesParameter.create("nextDate", nextDate, 2));
        List<Long> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.get(0) > 0;
    }

    @Override
    public List<POS> getListPOSByMCP(List<Integer> userIds, Date fromDate, Date toDate, DataService dataService) {
        String hql = "SELECT DISTINCT MCPDetails.poss"
                + " FROM MCPDetails AS MCPDetails"
                + " JOIN MCPDetails.mcps AS MCP"
                + " WHERE MCPDetails.deletedUser = 0"
                + " AND MCP.deletedUser = 0"
                + " AND MCP.implementEmployees.id IN (:ids)"
                + " AND MCP.beginDate >= :fromDate"
                + " AND MCP.beginDate <= :toDate"
                + " AND MCP.type = 1"
                + " AND MCPDetails.deletedUser = 0"
                + " AND MCPDetails.isActive = 1"
                + " ORDER BY MCPDetails.poss.name";
        List<MsalesParameter> parameters = MsalesParameter.createList("fromDate", fromDate, 2);
        parameters.add(MsalesParameter.create("toDate", toDate, 2));
        if (userIds == null || userIds.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", userIds, 1));
        }
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<Integer> getListPOSIdByMCP(List<Integer> userIds, Date fromDate, Date toDate, DataService dataService) {
        String hql = "SELECT DISTINCT MCPDetails.poss.id"
                + " FROM MCPDetails AS MCPDetails"
                + " JOIN MCPDetails.mcps AS MCP"
                + " WHERE MCPDetails.deletedUser = 0"
                + " AND MCP.deletedUser = 0"
                + " AND MCP.implementEmployees.id IN (:ids)"
                + " AND MCP.beginDate >= :fromDate"
                + " AND MCP.beginDate <= :toDate"
                + " AND MCP.type = 1"
                + " AND MCPDetails.deletedUser = 0"
                + " AND MCPDetails.isActive = 1"
                + " ORDER BY MCPDetails.poss.name";
        List<MsalesParameter> parameters = MsalesParameter.createList("fromDate", fromDate, 2);
        parameters.add(MsalesParameter.create("toDate", toDate, 2));
        if (userIds == null || userIds.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", userIds, 1));
        }
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public Integer getMCPUserIdFromPOS(int posId, DataService dataService) {
        Date now = DateUtils.getShortNow();
        String hql = "SELECT MCP.implementEmployees.id"
                + " FROM MCPDetails AS MCPDetails"
                + " JOIN MCPDetails.mcps AS MCP"
                + " WHERE MCPDetails.deletedUser = 0"
                + " AND MCP.deletedUser = 0"
                + " AND MCP.beginDate = :now"
                + " AND MCP.type = 1"
                + " AND MCPDetails.poss.id = :posId"
                + " AND MCPDetails.isActive = 1";

        List<MsalesParameter> parameters = MsalesParameter.createList("now", now, 2);
        parameters.add(MsalesParameter.create("posId", posId));
        List<Integer> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Integer getMCPIdFromPOS(int posId, DataService dataService) {
        Date now = DateUtils.getShortNow();
        String hql = "SELECT MCP.id"
                + " FROM MCPDetails AS MCPDetails"
                + " JOIN MCPDetails.mcps AS MCP"
                + " WHERE MCPDetails.deletedUser = 0"
                + " AND MCP.deletedUser = 0"
                + " AND MCP.beginDate = :now"
                + " AND MCP.type = 1"
                + " AND MCPDetails.isActive = 1"
                + " AND MCPDetails.poss.id = :posId";

        List<MsalesParameter> parameters = MsalesParameter.createList("now", now, 2);
        parameters.add(MsalesParameter.create("posId", posId));
        List<Integer> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean checkMCPWeekByUser(int userId, int posId, Date monday, Date sunday, DataService dataService) {
        String hql = "SELECT MCP.id"
                + " FROM MCPDetails AS MCPDetails"
                + " JOIN MCPDetails.mcps AS MCP"
                + " WHERE MCPDetails.deletedUser = 0"
                + " AND MCP.deletedUser = 0"
                + " AND MCP.type = 1"
                + " AND MCP.beginDate >= :monday"
                + " AND MCP.beginDate <= :sunday"
                + " AND MCP.implementEmployees.id = :userId"
                + " AND MCPDetails.isActive = 1"
                + " AND MCPDetails.poss.id = :posId";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("monday", monday, 2));
        parameters.add(MsalesParameter.create("sunday", sunday, 2));
        parameters.add(MsalesParameter.create("posId", posId));
        List<Integer> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return !list.isEmpty();
    }

    @Override
    public User getUserByMCPWeek(int posId, Date date, DataService dataService) {
        String hql = "SELECT MCP.implementEmployees"
                + " FROM MCPDetails AS MCPDetails"
                + " JOIN MCPDetails.mcps AS MCP"
                + " WHERE MCPDetails.deletedUser = 0"
                + " AND MCP.deletedUser = 0"
                + " AND MCP.type = 1"
                + " AND MCP.beginDate >= :monday"
                + " AND MCP.beginDate <= :sunday"
                + " AND MCPDetails.isActive = 1"
                + " AND MCPDetails.poss.id = :posId";
        List<MsalesParameter> parameters = MsalesParameter.createList("posId", posId);
        parameters.add(MsalesParameter.create("monday", DateUtils.getMonday(date), 2));
        parameters.add(MsalesParameter.create("sunday", DateUtils.getSunday(date), 2));
        List<User> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<GoodsCategory> getListGoodsCategory(int userId, int companyId, DataService dataService) {
        String hqlListCategory = "SELECT GoodsCategory"
                + " FROM UserGoodsCategory AS UserGoodsCategory"
                + " JOIN UserGoodsCategory.goodsCategorys AS GoodsCategory"
                + " WHERE UserGoodsCategory.deletedUser = 0"
                + " AND GoodsCategory.deletedUser = 0"
                + " AND GoodsCategory.companys.id = :companyId"
                + " AND UserGoodsCategory.users.id = :userId"
                + " AND GoodsCategory.statuss.id = 15"
                + " ORDER BY GoodsCategory.order";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("userId", userId));
        return dataService.executeSelectHQL(hqlListCategory, parameters, false, 0, 0);
    }

    @Override
    public List<Goods> getListGoods(int companyId, DataService dataService) {
        String hqlGoods = "FROM Goods"
                + " WHERE deletedUser = 0"
                + " AND deletedUser = 0"
                + " AND goodsCategorys.companys.id = :companyId"
                + " AND goodsCategorys.statuss.id = 15"
                + " AND statuss.id = 15"
                + " ORDER BY goodsCategorys.id";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        return dataService.executeSelectHQL(hqlGoods, parameters, false, 0, 0);
    }

    @Override
    public List<Goods> getListGoodsByListGoodsCategoryId(List<Integer> ids, DataService dataService) {
        String hqlGoods = "FROM Goods"
                + " WHERE deletedUser = 0"
                + " AND goodsCategorys.id IN (:ids)"
                + " AND statuss.id = 15"
                + " ORDER BY goodsCategorys.id";
        List<MsalesParameter> parameters = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }
        return dataService.executeSelectHQL(hqlGoods, parameters, false, 0, 0);
    }

    @Override
    public List<HashMap> searchSupPOS(int mcpId, String posCode, DataService dataService) {
        String hqlPOS = "SELECT DISTINCT POS AS pos,MCPDetails.id AS mcpDetailsId,"
                + " CASE WHEN (CustomerCareInformation.id IS NULL OR CustomerCareInformation.deletedUser != 0) THEN FALSE ELSE TRUE END AS isVisited"
                + " FROM CustomerCareInformation as CustomerCareInformation"
                + " RIGHT JOIN CustomerCareInformation.mcpDetailss as MCPDetails"
                + " JOIN MCPDetails.poss AS POS"
                + " WHERE MCPDetails.deletedUser = 0"
                + " AND MCPDetails.mcps.id = :mcpId"
                + " AND POS.deletedUser = 0";

        List<MsalesParameter> parameters = MsalesParameter.createList("mcpId", mcpId);
        if (posCode != null && !posCode.trim().isEmpty()) {
            hqlPOS += " AND POS.posCode LIKE :posCode";
            parameters.add(MsalesParameter.create("posCode", "%" + posCode + "%"));
        }
        hqlPOS += " ORDER BY MCPDetails.mcps.beginDate DESC";

        return dataService.executeSelectHQL(hqlPOS, parameters, true, 0, 0);
    }

    @Override
    public boolean checkSalesTransCode(String transCode, DataService dataService) {
        String hql = "SELECT id"
                + " FROM SalesTrans"
                + " WHERE deletedUser = 0"
                + " AND transCode = :transCode";
        List<MsalesParameter> parameters = MsalesParameter.createList("transCode", transCode);
        List<Integer> checkCodeList = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return !checkCodeList.isEmpty();
    }

    @Override
    public boolean checkOrderCode(String transCode, DataService dataService) {
        String hql = "SELECT id"
                + " FROM SalesOrder"
                + " WHERE deletedUser = 0"
                + " AND transCode = :transCode";
        List<MsalesParameter> parameters = MsalesParameter.createList("transCode", transCode);
        List<Integer> checkCodeList = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return !checkCodeList.isEmpty();
    }

    @Override
    public boolean checkTimeWork(CompanyConstant companyConstant) {
        if (companyConstant.getMorningFrom() == null || companyConstant.getMorningTo() == null
                || companyConstant.getEveningFrom() == null || companyConstant.getEveningTo() == null) {
            return true;
        }

        Calendar now = Calendar.getInstance();
        Calendar morningFrom = Calendar.getInstance();
        Calendar morningTo = Calendar.getInstance();
        Calendar eveningFrom = Calendar.getInstance();
        Calendar eveningTo = Calendar.getInstance();

        morningFrom.setTime(companyConstant.getMorningFrom());
        morningTo.setTime(companyConstant.getMorningTo());
        eveningFrom.setTime(companyConstant.getEveningFrom());
        eveningTo.setTime(companyConstant.getEveningTo());

        now.set(morningFrom.get(Calendar.YEAR), morningFrom.get(Calendar.MONTH), morningFrom.get(Calendar.DATE));
        return (now.compareTo(morningFrom) >= 0 && now.compareTo(morningTo) <= 0)
                || ((now.compareTo(eveningFrom) >= 0 && now.compareTo(eveningTo) <= 0));
    }

    @Override
    public boolean checkGoodsFocusByPOSId(int goodsId, int posId, DataService dataService) {
        POS pos = dataService.getRowById(posId, POS.class);
        if (pos == null) {
            return false;
        } else {
            return checkGoodsFocusByChannel(goodsId, pos.getChannels(), dataService);
        }

    }

    @Override
    public boolean checkGoodsFocusByChannelId(int goodsId, int channelId, DataService dataService) {
        Channel channel = dataService.getRowById(channelId, Channel.class);
        return checkGoodsFocusByChannel(goodsId, channel, dataService);
    }

    @Override
    public boolean checkGoodsFocusByChannel(int goodsId, Channel channel, DataService dataService) {
        if (channel == null) {
            return false;
        }

        List<Integer> channelIdList = new ArrayList<>();
        channelIdList.add(channel.getId());
        Channel tempChannel = channel.getParents();
        while (tempChannel != null) {
            channelIdList.add(tempChannel.getId());
            tempChannel = tempChannel.getParents();
        }

        return checkGoodsFocus(goodsId, channelIdList, dataService);
    }

    @Override
    public boolean checkGoodsFocusByUserId(int goodsId, int userId, DataService dataService) {
        List<Channel> channelList = getListChannel(userId, dataService);

        List<Integer> channelIdList = new ArrayList<>();
        for (Channel channel : channelList) {
            channelIdList.add(channel.getId());
            Channel tempChannel = channel.getParents();
            while (tempChannel != null) {
                channelIdList.add(tempChannel.getId());
                tempChannel = tempChannel.getParents();
            }
        }
        return checkGoodsFocus(goodsId, channelIdList, dataService);
    }

    @Override
    public boolean checkGoodsFocus(int goodsId, List<Integer> channelIdList, DataService dataService) {
        if (!channelIdList.isEmpty()) {
            String hql = "SELECT id"
                    + " FROM GoodsChannelFocus"
                    + " WHERE deletedUser = 0"
                    + " AND channels.id IN (:channelIdList)"
                    + " AND goodss.id = :goodsId";
            List<MsalesParameter> parameters = MsalesParameter.createList("goodsId", goodsId);
            parameters.add(MsalesParameter.create("channelIdList", channelIdList, 1));
            List<Integer> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
            return !list.isEmpty();
        }
        return false;
    }

    @Override
    public List<SalesStockGoods> getListSalesStockGoodsByListGoodsId(int salesStockId, List<Integer> goodsIds, DataService dataService) {
        String hql = "SELECT SalesStockGoods"
                + " FROM SalesStockGoods AS SalesStockGoods"
                + " WHERE SalesStockGoods.deletedUser = 0"
                + " AND SalesStockGoods.stocks.id = :salesStockId"
                + " AND SalesStockGoods.isActive = 1"
                + " AND SalesStockGoods.goodss.id IN (:goodsIds)"
                + " ORDER BY SalesStockGoods.goodss.id";
        List<MsalesParameter> parameters = MsalesParameter.createList("salesStockId", salesStockId);
        if (goodsIds == null || goodsIds.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("goodsIds"));
        } else {
            parameters.add(MsalesParameter.create("goodsIds", goodsIds, 1));
        }
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<SalesStockGoods> getListSalesStockGoodsByType(int userId, int type, Integer id, DataService dataService) {
        //get ListSaleStockGoods cua user
        //tra het san pham con trong kho 
        //type =3
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        String hql = "SELECT SalesStockGoods"
                + " FROM SalesStockGoods AS SalesStockGoods"
                + " JOIN SalesStockGoods.stocks AS SalesStock"
                + " JOIN SalesStockGoods.goodss AS Goods"
                + " WHERE SalesStockGoods.deletedUser = 0"
                + " AND Goods.deletedUser = 0"
                + " AND SalesStock.deletedUser = 0"
                + " AND SalesStockGoods.isActive = 1"
                + " AND SalesStockGoods.quantity > 0";

        if (type == 1)//tra mot san pham
        {
            hql += " AND Goods.id = :goodsId";
            parameters.add(MsalesParameter.create("goodsId", id));
        } else if (type == 2) {//tra mot nhom san pham
            hql += " AND Goods.goodsCategorys.id = :goodsCategoryId";
            parameters.add(MsalesParameter.create("goodsCategoryId", id));
        }
        hql += " AND SalesStock.salemanUsers.id = :userId"
                + " ORDER BY Goods.id";
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public SalesStock getSalesStockUser(int userId, DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("salemanUsers.id", userId);
        parameterList.add("statuss.id", 1);//status = 1 => hoat dong
        List<SalesStock> list = dataService.getListOption(SalesStock.class, parameterList);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public SalesStock getSalesStockPOS(int posId, DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("poss.id", posId);
        parameterList.add("statuss.id", 1);//status = 1 => hoat dong
        List<SalesStock> list = dataService.getListOption(SalesStock.class, parameterList);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public SalesStock getSalesStockChannel(int channelId, DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("channels.id", channelId);
        parameterList.add("statuss.id", 1);//status = 1 => hoat dong
        List<SalesStock> list = dataService.getListOption(SalesStock.class, parameterList);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public SalesStockGoods getSalesStockGoodsByGoodsId(int salesStockId, int goodsId, DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("goodss.id", goodsId);
        parameterList.add("stocks.id", salesStockId);
        parameterList.add("isActive", 1);
        List<SalesStockGoods> list = dataService.getListOption(SalesStockGoods.class, parameterList);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public SalesOrder getSalesOrder(int orderId, DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("id", orderId);
        parameterList.add("statuss.id", 14, "!=");//status 14 => don hang bi huy
        List<SalesOrder> list = dataService.getListOption(SalesOrder.class, parameterList);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public SalesOrder getSalesOrderWaiting(int orderId, DataService dataService) {
        SalesOrder salesOrder = getSalesOrder(orderId, dataService);
        if (salesOrder != null && salesOrder.getStatuss().getId() == 13) {
            return salesOrder;
        } else {
            return null;
        }
    }

    @Override
    public List<Location> getListLocationByUserId(int userId, DataService dataService) {
        List<Integer> channelIdList = getListChannelId(userId, dataService);
        String hql = "SELECT DISTINCT ChannelLocation.locations"
                + " FROM ChannelLocation AS ChannelLocation"
                + " WHERE ChannelLocation.deletedUser = 0"
                + " AND ChannelLocation.channels.id IN (:channelIdList)"
                + " ORDER BY ChannelLocation.locations.name";
        List<MsalesParameter> parameters = MsalesParameter.createList("channelIdList", channelIdList, 1);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<Location> getListLocationByParent(int parentId, DataService dataService) {
        String hql = "FROM Location"
                + " WHERE deletedUser = 0"
                + " AND parents.id = :parentId";
        List<MsalesParameter> parameters = MsalesParameter.createList("parentId", parentId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    //KPI 
    @Override
    public Long getTargetCare(int userId, Date fromDate, Date toDate, DataService dataService) {
        String hql = "SELECT COUNT(id)"
                + " FROM MCPDetails"
                + " WHERE deletedUser = 0"
                + " AND isActive = 1"
                + " AND mcps.implementEmployees.id = :userId"
                + " AND mcps.type = 1"
                + " AND mcps.beginDate >= :fromDate"
                + " AND mcps.beginDate <= :toDate";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("fromDate", fromDate, 2));
        parameters.add(MsalesParameter.create("toDate", toDate, 2));
        return (long) dataService.executeSelectHQL(hql, parameters, false, 1, 1).get(0);
    }

    @Override
    public Long getTotalCared(int userId, Date fromDate, Date toDate, DataService dataService) {
        String hql = "SELECT COUNT(id)"
                + " FROM CustomerCareInformation"
                + " WHERE deletedUser = 0"
                + " AND mcpDetailss.mcps.deletedUser = 0"
                + " AND mcpDetailss.deletedUser = 0"
                + " AND mcpDetailss.isActive = 1"
                + " AND mcpDetailss.mcps.implementEmployees.id = :userId"
                + " AND mcpDetailss.mcps.type = 1"
                + " AND mcpDetailss.mcps.beginDate >= :fromDate"
                + " AND mcpDetailss.mcps.beginDate <= :toDate";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        parameters.add(MsalesParameter.create("fromDate", fromDate, 2));
        parameters.add(MsalesParameter.create("toDate", toDate, 2));
        return (long) dataService.executeSelectHQL(hql, parameters, false, 1, 1).get(0);
    }

    @Override
    public long getWorkedTime(int userId, CompanyConstant companyConstant, Date date, DataService dataService) {
        if (companyConstant != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            //set for morning from
            calendar.setTime(companyConstant.getMorningFrom());
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);
            Date morningFrom = calendar.getTime();

            //set for morning To
            calendar.setTime(companyConstant.getMorningTo());
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);
            Date morningTo = calendar.getTime();

            //set for evening from
            calendar.setTime(companyConstant.getEveningFrom());
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);
            Date eveningFrom = calendar.getTime();

            //set for evening to
            calendar.setTime(companyConstant.getEveningTo());
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);
            Date eveningTo = calendar.getTime();

            String hql = "FROM CustomerCareInformation"
                    + " WHERE deletedUser = 0"
                    + " AND mcpDetailss.implementEmployees.id = :userId"
                    + " AND mcpDetailss.mcps.beginDate = :date";

            List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
            parameters.add(MsalesParameter.create("date", date, 2));

            List<CustomerCareInformation> customerCareInformationList = dataService.executeSelectHQL(hql, parameters, false, 0, 0);

            if (!customerCareInformationList.isEmpty()) {
                Date minTime = customerCareInformationList.get(0).getStartCustomerCareAt();
                Date maxTime = customerCareInformationList.get(0).getFinishCustomerCareAt();

                for (CustomerCareInformation customerCareInformation : customerCareInformationList) {
                    if (customerCareInformation.getStartCustomerCareAt().compareTo(minTime) < 0) {
                        minTime = customerCareInformation.getStartCustomerCareAt();
                    }
                    if (customerCareInformation.getFinishCustomerCareAt().compareTo(maxTime) > 0) {
                        maxTime = customerCareInformation.getFinishCustomerCareAt();
                    }
                }

                if (minTime.compareTo(morningFrom) < 0) {
                    minTime = morningFrom;
                }
                if (maxTime.compareTo(eveningTo) > 0) {
                    maxTime = eveningTo;
                }

                long time = 0;
                if (minTime.compareTo(morningTo) > 0) {
                    if (minTime.compareTo(eveningFrom) > 0) {
                        if (minTime.compareTo(maxTime) > 0) {
                            return 0;
                        } else {
                            time = maxTime.getTime() - minTime.getTime();
                        }
                    } else {
                        time = maxTime.getTime() - eveningFrom.getTime();
                    }
                } else {
                    if (maxTime.compareTo(eveningFrom) < 0) {
                        if (maxTime.compareTo(morningTo) > 0) {
                            time = morningTo.getTime() - minTime.getTime();
                        } else {
                            if (maxTime.compareTo(minTime) < 0) {
                                return 0;
                            } else {
                                time = maxTime.getTime() - minTime.getTime();
                            }
                        }
                    } else {
                        time = (maxTime.getTime() - eveningFrom.getTime()) + (morningTo.getTime() - minTime.getTime());
                    }
                }

                return time / 1000;

            } else {
                return 0;
            }

            //find morning first
            //list working morning
//            List<CustomerCareInformation> morningList = new ArrayList<>();
//            for (CustomerCareInformation customerCareInformation : customerCareInformationList) {
//                if (customerCareInformation.getStartCustomerCareAt().compareTo(morningTo) <= 0) {
//                    morningList.add(customerCareInformation);
//                }
//            }
//
//            //list evening
//            List<CustomerCareInformation> eveningList = new ArrayList<>();
//            for (CustomerCareInformation customerCareInformation : customerCareInformationList) {
//                if (customerCareInformation.getStartCustomerCareAt().compareTo(morningTo) > 0) {
//                    eveningList.add(customerCareInformation);
//                }
//            }
//            Collections.sort(eveningList);
//            Collections.sort(morningList);
//
//            long morningTime = 0;
//            long eveningTime = 0;
//            if (!morningList.isEmpty()) {
//                Date firstTime = morningList.get(0).getStartCustomerCareAt();
//                Date lastTime = morningList.get(morningList.size() - 1).getFinishCustomerCareAt();
//
//                if (firstTime.compareTo(morningFrom) < 0) {
//                    firstTime = morningFrom;
//                }
//                if (lastTime.compareTo(morningTo) > 0) {
//                    lastTime = morningTo;
//                }
//                
//                morningTime = lastTime.getTime() - firstTime.getTime();
//            }
//            if (!eveningList.isEmpty()) {
//                Date firstTime = eveningList.get(0).getStartCustomerCareAt();
//                Date lastTime = eveningList.get(eveningList.size() - 1).getFinishCustomerCareAt();
//
//                if (firstTime.compareTo(eveningFrom) < 0) {
//                    firstTime = eveningFrom;
//                }
//                if (lastTime.compareTo(eveningTo) > 0) {
//                    lastTime = eveningTo;
//                }
//                eveningTime = lastTime.getTime() - firstTime.getTime();
//            }
        } else {
            return 0;
        }
    }

    /**
     * -------------------------------------------------------------------------------- PROMOTION --------------------------------------------------------------------------------
     */
    public List<Promotion> getCbListPromotionByChannelList(List<Channel> channelList, int companyId, DataService dataService) {
        List<Promotion> ret = new ArrayList<>();
        //Date now = DateUtils.getShortNow();
        String hqlFullApply = "FROM Promotion"
                + " WHERE deletedUser = 0"
                + " AND applyScope != 1"
                + " AND companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        List<Promotion> fullList = dataService.executeSelectHQL(hqlFullApply, parameters, false, 0, 0);
        List<Integer> ids = new ArrayList<>();
        for (Promotion promotion : fullList) {
            ids.add(promotion.getId());
        }
        if (ids.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }

        String hqlChannelApply = "SELECT Promotion"
                + " FROM PromotionChannel AS PromotionChannel"
                + " JOIN PromotionChannel.promotions AS Promotion"
                + " WHERE PromotionChannel.deletedUser = 0"
                + " AND PromotionChannel.statuss.value = 1"
                + " AND Promotion.deletedUser = 0"
                //+ " AND Promotion.statuss.value = 2"
                //                + " AND Promotion.startDate <= :now"
                //                + " AND Promotion.endDate >= :now"
                + " AND Promotion.companys.id = :companyId"
                + " AND Promotion.id NOT IN (:ids)";

        int idx = 1;
        hqlChannelApply += " AND (Promotion.applyScope != 1";
        for (Channel channel : channelList) {
            hqlChannelApply += " OR PromotionChannel.channels.fullCode = :channelCode" + idx
                    + " OR :channelCode" + idx + " LIKE CONCAT(PromotionChannel.channels.fullCode,'!_%') ESCAPE '!'"
                    + " OR PromotionChannel.channels.fullCode  LIKE :channelCode" + (idx + 1) + " ESCAPE '!'";
            parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode()));
            parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode() + "!_%"));
        }
        hqlChannelApply += ")";
        List<Promotion> applyList = dataService.executeSelectHQL(hqlChannelApply, parameters, false, 0, 0);
        ret.addAll(fullList);
        ret.addAll(applyList);
        Collections.sort(ret);
        return ret;
    }

    @Override
    public List<Promotion> getListPromotionByChannelIdList(List<Integer> channelIds, int companyId, DataService dataService) {
        String hql = "FROM Channel"
                + " WHERE deletedUser = 0"
                + " AND statuss.value = 1"
                + " AND id IN (:ids)";
        List<MsalesParameter> parameters = new ArrayList<>();
        if (channelIds == null || channelIds.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", channelIds, 1));
        }
        List<Channel> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        return getListPromotionByChannelList(list, companyId, dataService);
    }

    @Override
    public List<Promotion> getListPromotionByChannelList(List<Channel> channelList, int companyId, DataService dataService) {
        List<Promotion> ret = new ArrayList<>();
        //Date now = DateUtils.getShortNow();
        String hqlFullApply = "FROM Promotion"
                + " WHERE deletedUser = 0"
                + " AND applyScope != 1"
                + " AND companys.id = :companyId"
                + " AND statuss.value = 2";
        //                + " AND startDate <= :now"
        //                + " AND endDate >= :now";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        //parameters.add(MsalesParameter.create("now", now, 2));
        List<Promotion> fullList = dataService.executeSelectHQL(hqlFullApply, parameters, false, 0, 0);
        List<Integer> ids = new ArrayList<>();
        for (Promotion promotion : fullList) {
            ids.add(promotion.getId());
        }
        if (ids.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }

        String hqlChannelApply = "SELECT Promotion"
                + " FROM PromotionChannel AS PromotionChannel"
                + " JOIN PromotionChannel.promotions AS Promotion"
                + " WHERE PromotionChannel.deletedUser = 0"
                + " AND PromotionChannel.statuss.value = 1"
                + " AND Promotion.deletedUser = 0"
                + " AND Promotion.statuss.value = 2"
                //                + " AND Promotion.startDate <= :now"
                //                + " AND Promotion.endDate >= :now"
                + " AND Promotion.companys.id = :companyId"
                + " AND Promotion.id NOT IN (:ids)";

        int idx = 1;
        hqlChannelApply += " AND (Promotion.applyScope != 1";
        for (Channel channel : channelList) {
            hqlChannelApply += " OR PromotionChannel.channels.fullCode = :channelCode" + idx
                    + " OR :channelCode" + idx + " LIKE CONCAT(PromotionChannel.channels.fullCode,'!_%') ESCAPE '!'"
                    + " OR PromotionChannel.channels.fullCode  LIKE :channelCode" + (idx + 1) + " ESCAPE '!'";
            parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode()));
            parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode() + "!_%"));
        }
        hqlChannelApply += ")";
        List<Promotion> applyList = dataService.executeSelectHQL(hqlChannelApply, parameters, false, 0, 0);
        ret.addAll(fullList);
        ret.addAll(applyList);
        Collections.sort(ret);
        return ret;
    }

    @Override
    public List<Promotion> searchPromotionApp(List<Channel> channelList, int companyId, Date fromDate, Date toDate, DataService dataService) {
        List<Promotion> ret = new ArrayList<>();
        //get all promotion with applyScope != 1
        String hqlFullApply = "FROM Promotion"
                + " WHERE deletedUser = 0"
                + " AND applyScope != 1"
                + " AND companys.id = :companyId"
                //                + " AND statuss.value = 2"
                + " AND NOT startDate > :toDate"
                + " AND NOT endDate < :fromDate"
                + " AND :fromDate <= :toDate";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("fromDate", fromDate, 2));
        parameters.add(MsalesParameter.create("toDate", toDate, 2));

        List<Promotion> fullList = dataService.executeSelectHQL(hqlFullApply, parameters, false, 0, 0);
        List<Integer> ids = new ArrayList<>();
        for (Promotion promotion : fullList) {
            ids.add(promotion.getId());
        }
        if (ids.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }

        String hqlChannelApply = "SELECT Promotion"
                + " FROM PromotionChannel AS PromotionChannel"
                + " JOIN PromotionChannel.promotions AS Promotion"
                + " WHERE PromotionChannel.deletedUser = 0"
                + " AND PromotionChannel.statuss.value = 1"
                + " AND Promotion.deletedUser = 0"
                //                + " AND Promotion.statuss.value = 2"
                + " AND NOT Promotion.startDate > :toDate"
                + " AND NOT Promotion.endDate < :fromDate"
                + " AND :fromDate <= :toDate"
                + " AND Promotion.companys.id = :companyId"
                + " AND Promotion.id NOT IN (:ids)";

        int idx = 1;
        hqlChannelApply += " AND (Promotion.applyScope != 1";
        for (Channel channel : channelList) {
            hqlChannelApply += " OR PromotionChannel.channels.fullCode = :channelCode" + idx
                    + " OR :channelCode" + idx + " LIKE CONCAT(PromotionChannel.channels.fullCode,'!_%') ESCAPE '!'"
                    + " OR PromotionChannel.channels.fullCode  LIKE :channelCode" + (idx + 1) + " ESCAPE '!'";
            parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode()));
            parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode() + "!_%"));
        }
        hqlChannelApply += ")";
        List<Promotion> applyList = dataService.executeSelectHQL(hqlChannelApply, parameters, false, 0, 0);
        ret.addAll(fullList);
        ret.addAll(applyList);
        Collections.sort(ret);
        return ret;
    }

    @Override
    public List<Goods> getListGoodsPromotion(int promotionId, DataService dataService
    ) {
        String hql = "SELECT PromotionGoodsRef.goodss"
                + " FROM PromotionGoodsRef AS PromotionGoodsRef"
                + " WHERE PromotionGoodsRef.deletedUser = 0"
                + " AND PromotionGoodsRef.promotions.id = :promotionId"
                + " AND PromotionGoodsRef.statuss.value = 1";

        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<PromotionGoodsRef> getListPromotionGoodsRef(int promotionId, DataService dataService) {
        String hql = "SELECT PromotionGoodsRef"
                + " FROM PromotionGoodsRef AS PromotionGoodsRef"
                + " WHERE PromotionGoodsRef.deletedUser = 0"
                + " AND PromotionGoodsRef.promotions.id = :promotionId"
                + " AND PromotionGoodsRef.statuss.value = 1";

        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<Goods> getListGoodsByGoodsCategoryId(int goodsCategoryId, DataService dataService
    ) {
        String hql = "FROM Goods"
                + " WHERE deletedUser = 0"
                + " AND statuss.value = 1"
                + " AND goodsCategorys.id = :goodsCategoryId";
        List<MsalesParameter> parameters = MsalesParameter.createList("goodsCategoryId", goodsCategoryId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public boolean checkRegisterPromotion(int promotionId, int posId, DataService dataService) {
        String hql1 = "SELECT id"
                + " FROM PromotionTransRetailer"
                + " WHERE deletedUser = 0"
                + " AND statuss.value NOT IN (3,5)"//bi loai/da giao
                + " AND poss.id = :posId"
                + " AND promotions.id = :promotionId";
        String hql2 = "SELECT id"
                + " FROM PromotionAccumulationRetailer"
                + " WHERE deletedUser = 0"
                + " AND statuss.value NOT IN (3,5)"//bi loai/da giao
                + " AND retailers.id = :posId"
                + " AND promotions.id = :promotionId";

        List<MsalesParameter> parameters = MsalesParameter.createList("posId", posId);
        parameters.add(MsalesParameter.create("promotionId", promotionId));
        List<Integer> list1 = dataService.executeSelectHQL(hql1, parameters, false, 1, 1);
        List<Integer> list2 = dataService.executeSelectHQL(hql2, parameters, false, 1, 1);
        return (list1.isEmpty() && list2.isEmpty());
    }

    @Override
    public List<Promotion> getListPromotionCanByPOS(int posId, List<Goods> goodsList, int createdUser, DataService dataService) {
        List<Promotion> ret = new ArrayList<>();
        POS pos = dataService.getRowById(posId, POS.class);
        if (pos == null) {
            return ret;
        }

        //lay tat cả danh sach KM ap dung cho ĐBH theo kênh
        List<Channel> channelList = new ArrayList<>();
        channelList.add(pos.getChannels());
        List<Promotion> promotionList = getListPromotionByChannelList(channelList, pos.getChannels().getCompanys().getId(), dataService);

        //kiem tra san pham
        List<Integer> notRequiredList = new ArrayList<>();
        List<AccumulationInfo> accumulationInfoList = new ArrayList<>();

        for (Promotion promotion : promotionList) {
            boolean flag = false;
            List<PromotionGoodsRef> list = getListPromotionGoodsRef(promotion.getId(), dataService);

            int quantity = 0;
            int amount = 0;

            for (PromotionGoodsRef proGoodsRef : list) {
                for (Goods goods : goodsList) {
                    if (Objects.equals(proGoodsRef.getGoodss().getId(), goods.getId())) {
                        if (promotion.getConditionQuantity() == 1) {
                            //so luong ban theo don hang
//                            if (goods.getQuantity() >= promotion.getQuantity()) {
//                                flag = true;
//                                break;
//                            }
                            quantity += goods.getQuantity();
                            break;
                        } else if (promotion.getConditionQuantity() == 2) {
                            //doanh thu theo don hang
//                            if (goods.getQuantity() * goods.getPrice() >= promotion.getQuantity()) {
//                                flag = true;
//                                break;
//                            }
                            amount += goods.getQuantity() * goods.getPrice();
                            break;
                        } else if (promotion.getConditionQuantity() == 3) {
                            //tich luy so luong ban
                            //tinh so luong de cong vao TransRetailer
                            quantity += goods.getQuantity();
                            amount += goods.getQuantity() * goods.getPrice();
                            flag = true;
                            break;
                        } else if (promotion.getConditionQuantity() == 4) {
                            //tich luy doanh so ban
                            //tinh doanh so de cong vao accumulationRetailer
                            quantity += goods.getQuantity();
                            amount += goods.getQuantity() * goods.getPrice();
                            flag = true;
                            break;
                        }
                    }
                }
            }

            if (promotion.getConditionQuantity() == 1) {
                if (quantity >= promotion.getQuantity()) {
                    flag = true;
                }
            } else if (promotion.getConditionQuantity() == 2) {
                if (amount >= promotion.getQuantity()) {
                    flag = true;
                }
            }

            if (flag) {
                if (promotion.getConditionQuantity() == 3 || promotion.getConditionQuantity() == 4) {
                    AccumulationInfo accumulationInfo = new AccumulationInfo();
                    accumulationInfo.setPromotionId(promotion.getId());
                    accumulationInfo.setQuantity(quantity);
                    accumulationInfo.setAmount(amount);
                    accumulationInfo.setConditionQuantity(promotion.getConditionQuantity());
                    accumulationInfoList.add(accumulationInfo);
                }
            } else {
                notRequiredList.add(promotion.getId());
            }
        }

        //xoa het promotion ko thoa dk
        for (int id : notRequiredList) {
            for (Promotion promotion : promotionList) {
                if (promotion.getId() == id) {
                    promotionList.remove(promotion);
                    break;
                }
            }
        }

        //tao list luu accumulationRetailer
        List<PromotionAccumulationRetailer> accumulationRetailerList = new ArrayList<>();

        for (Promotion promotion : promotionList) {
            if (promotion.getConditionQuantity() == 1 || promotion.getConditionQuantity() == 2) {
                //KM truc tiep
                PromotionTransRetailer promotionTransRetailer = null;
                List<PromotionTransRetailer> promotionTransRetailerList = getListPromotionTransRetailer(posId, promotion.getId(), dataService);
                if (promotion.getIsOnce() == 1) {
                    //KM 1 lan                    
                    int isOnce = 0;
                    for (PromotionTransRetailer transRetailer : promotionTransRetailerList) {
                        if (transRetailer.getStatuss().getId() != 25)//bi loai
                        {
                            isOnce++;
                            if (isOnce < 2) {
                                promotionTransRetailer = transRetailer;
                            } else {
                                break;
                            }
                        }
                    }
                    //KM 1 lan va chua tham gia KM hoac dang tham gia KM
                    if (isOnce < 2) {
                        //KT promotionTransRetailer
                        if (promotionTransRetailer == null) {
                            //neu la CTKM can dang ky => loai
                            if (promotion.getIsRegister() == null || promotion.getIsRegister() == 0) {
                                //OK
                                ret.add(promotion);
                            }
                        } else if (//promotionTransRetailer.getStatuss().getId() == 23//dang cho duyet
                                promotionTransRetailer.getStatuss().getId() == 29)//da duyet dang ky
                        {
                            //OK
                            ret.add(promotion);
                        }
                    }
                } else {
                    boolean flagCompleted = false;
                    //KM nhieu lan
                    for (PromotionTransRetailer transRetailer : promotionTransRetailerList) {
                        //KT promotionTransRetailer da dc dang ky chua
                        if (transRetailer.getStatuss().getId() == 29//da duyet dang ky
                                ) {
                            //OK
                            promotionTransRetailer = transRetailer;
                            ret.add(promotion);
                            break;
                        }
                    }

                    //chua dang ky?
                    if (promotionTransRetailer == null) {
                        //KT da dc hoan thanh/giao hang chua=> ko can dang ky lai
                        for (PromotionTransRetailer transRetailer : promotionTransRetailerList) {
                            //KT promotionTransRetailer
                            if (transRetailer.getStatuss().getId() == 24//hoan thanh
                                    || transRetailer.getStatuss().getId() == 29//da giao
                                    || transRetailer.getStatuss().getId() == 23//dang cho duyet
                                    ) {
                                //OK
                                flagCompleted = true;
                                break;
                            }
                        }

                        //chua tham gia KM hoac cac KM da hoan thanh/giao
                        if (promotionTransRetailer == null) {
                            //neu la KM ko can dang ky => OK
                            if (promotion.getIsRegister() == null || promotion.getIsRegister() == 0) {
                                ret.add(promotion);
                            } else if (flagCompleted) {
                                //KM dang ky+KM nhieu lan+da hoan thanh/giao KM=>tiep tuc huong KM
                                ret.add(promotion);
                            }
                        }
                    }
                }
            } else {
                //KM tich luy
                boolean flag = false;
                PromotionAccumulationRetailer promotionAccumulationRetailer = null;
                List<PromotionAccumulationRetailer> promotionAccumulationRetailerList = getListPromotionAccumulationRetailer(posId, promotion.getId(), dataService);
                if (promotion.getIsOnce() == 1) {
                    //KM 1 lan     
                    int isOnce = 0;
                    for (PromotionAccumulationRetailer accumulationRetailer : promotionAccumulationRetailerList) {
                        if (accumulationRetailer.getStatuss().getId() != 25)//bi loai
                        {
                            isOnce++;
                            if (isOnce < 2) {
                                promotionAccumulationRetailer = accumulationRetailer;
                            } else {
                                break;
                            }
                        }
                    }
                    //KM 1 lan va chua tham gia KM hoac dang tham gia KM
                    if (isOnce < 2) {
                        //KT
                        if (promotionAccumulationRetailer == null) {
                            //neu la CTKM can dang ky => loai
                            if (promotion.getIsRegister() == null || promotion.getIsRegister() == 0) {
                                //OK=> tao moi PromotionAccumulationRetailer
                                promotionAccumulationRetailer = new PromotionAccumulationRetailer();
                                promotionAccumulationRetailer.setPromotions(promotion);
                                promotionAccumulationRetailer.setRetailerId(posId);
                                promotionAccumulationRetailer.setQuantity(0);
                                promotionAccumulationRetailer.setAmount(0);
                                promotionAccumulationRetailer.setCreatedUser(createdUser);
                                promotionAccumulationRetailer.setCreatedAt(new Date());
                                promotionAccumulationRetailer.setUpdatedUser(0);
                                promotionAccumulationRetailer.setDeletedUser(0);
                                promotionAccumulationRetailer.setStatusId(26);//dang tich luy

                                if (promotion.getPromotionAwards().getId() == 1) {
                                    //tang sp
                                    promotionAccumulationRetailer.setIsOther(0);
                                    promotionAccumulationRetailer.setAwardGoodsId(promotion.getProAwardGoodss().getId());
                                    promotionAccumulationRetailer.setAwardName(promotion.getProAwardGoodss().getName());
                                    promotionAccumulationRetailer.setAwardQuantity(0);
                                } else if (promotion.getPromotionAwards().getId() == 2) {
                                    //tang vat pham
                                    promotionAccumulationRetailer.setIsOther(1);
                                    promotionAccumulationRetailer.setAwardGoodsId(promotion.getAwardOthers().getId());
                                    promotionAccumulationRetailer.setAwardName(promotion.getAwardOthers().getName());
                                    promotionAccumulationRetailer.setAwardQuantity(0);
                                } else if (promotion.getPromotionAwards().getId() == 3) {
                                    //giam tien
                                    promotionAccumulationRetailer.setAwardName(promotion.getPromotionAwards().getPromotionAwardName());
                                    promotionAccumulationRetailer.setAwardAmount(0);
                                    promotionAccumulationRetailer.setAwardQuantity(0);
                                } else {
                                    //chiet khau %
                                    promotionAccumulationRetailer.setAwardName(promotion.getPromotionAwards().getPromotionAwardName());
                                    promotionAccumulationRetailer.setAwardQuantity(promotion.getProAwardQuantity());
                                    promotionAccumulationRetailer.setAwardAmount(0);
                                }

                                flag = true;
                            }
                        } else if (promotionAccumulationRetailer.getStatuss().getId() == 23//dang cho duyet
                                || promotionAccumulationRetailer.getStatuss().getId() == 26//dang tich luy
                                || promotionAccumulationRetailer.getStatuss().getId() == 29)//da duyet dang ky
                        {
                            //OK
                            flag = true;
                        }
                    }
                } else {
                    //KM nhieu lan
                    for (PromotionAccumulationRetailer accumulationRetailer : promotionAccumulationRetailerList) {
                        //KT promotionTransRetailer
                        if (accumulationRetailer.getStatuss().getId() == 23//dang cho duyet
                                || accumulationRetailer.getStatuss().getId() == 26//dang tich luy
                                || accumulationRetailer.getStatuss().getId() == 29)//da duyet dang ky
                        {
                            //OK
                            promotionAccumulationRetailer = accumulationRetailer;
                            flag = true;
                            break;
                        }
                    }
                    //chua tham gia KM hoac cac KM da hoan thanh/giao
                    if (promotionAccumulationRetailer == null) {
                        //neu la KM ko can dang ky => OK
                        if (promotion.getIsRegister() == null || promotion.getIsRegister() == 0) {
                            //OK=> tao moi PromotionAccumulationRetailer
                            promotionAccumulationRetailer = new PromotionAccumulationRetailer();
                            promotionAccumulationRetailer.setPromotions(promotion);
                            promotionAccumulationRetailer.setRetailerId(posId);
                            promotionAccumulationRetailer.setQuantity(0);
                            promotionAccumulationRetailer.setAmount(0);
                            promotionAccumulationRetailer.setCreatedUser(createdUser);
                            promotionAccumulationRetailer.setCreatedAt(new Date());
                            promotionAccumulationRetailer.setUpdatedUser(0);
                            promotionAccumulationRetailer.setDeletedUser(0);
                            promotionAccumulationRetailer.setStatusId(26);//dang tich luy

                            if (promotion.getPromotionAwards().getId() == 1) {
                                //tang sp
                                promotionAccumulationRetailer.setIsOther(0);
                                promotionAccumulationRetailer.setAwardGoodsId(promotion.getProAwardGoodss().getId());
                                promotionAccumulationRetailer.setAwardName(promotion.getProAwardGoodss().getName());
                                promotionAccumulationRetailer.setAwardQuantity(0);
                            } else if (promotion.getPromotionAwards().getId() == 2) {
                                //tang vat pham
                                promotionAccumulationRetailer.setIsOther(1);
                                promotionAccumulationRetailer.setAwardGoodsId(promotion.getAwardOthers().getId());
                                promotionAccumulationRetailer.setAwardName(promotion.getAwardOthers().getName());
                                promotionAccumulationRetailer.setAwardQuantity(0);
                            } else if (promotion.getPromotionAwards().getId() == 3) {
                                // giam tien
                                promotionAccumulationRetailer.setAwardName(promotion.getPromotionAwards().getPromotionAwardName());
                                promotionAccumulationRetailer.setAwardAmount(0);
                                promotionAccumulationRetailer.setAwardQuantity(0);
                            } else {
                                //chiet khau %
                                promotionAccumulationRetailer.setAwardName(promotion.getPromotionAwards().getPromotionAwardName());
                                promotionAccumulationRetailer.setAwardQuantity(promotion.getProAwardQuantity());
                                promotionAccumulationRetailer.setAwardAmount(0);
                            }

                            flag = true;
                        }
                    }
                }

                if (flag) {
                    for (AccumulationInfo accumulationInfo : accumulationInfoList) {
                        if (Objects.equals(promotion.getId(), accumulationInfo.getPromotionId())) {
                            if (promotion.getConditionQuantity() == 3) {
                                //Tich luy so luong
                                promotionAccumulationRetailer.setQuantity(promotionAccumulationRetailer.getQuantity() + accumulationInfo.getQuantity());
                                promotionAccumulationRetailer.setAmount(promotionAccumulationRetailer.getAmount() + accumulationInfo.getAmount());
                                if (promotionAccumulationRetailer.getQuantity() >= promotion.getQuantity()) {
                                    //tich luy so luong thoa dk
                                    ret.add(promotion);

                                    //tinh so luong san pham dc tang thuong
                                    int awardQuantity = (promotionAccumulationRetailer.getQuantity() / promotion.getQuantity()) * promotion.getProAwardQuantity();

                                    //de trang thai tich luy la bi loai=> neu ko dc chon se bi loai
                                    promotionAccumulationRetailer.setStatusId(25);
                                    if (promotion.getPromotionAwards().getId() == 1) {
                                        //tang sp                                        
                                        promotionAccumulationRetailer.setAwardQuantity(awardQuantity);
                                    } else if (promotion.getPromotionAwards().getId() == 2) {
                                        //tang vat pham
                                        promotionAccumulationRetailer.setAwardQuantity(awardQuantity);
                                    } else if (promotion.getPromotionAwards().getId() == 3) {
                                        //giam tien
                                        promotionAccumulationRetailer.setAwardAmount(awardQuantity);
                                    } else {
                                        //chiet khau %
                                        promotionAccumulationRetailer.setAwardAmount(promotion.getProAwardQuantity() * promotionAccumulationRetailer.getAmount() / 100);
                                    }
                                }
                                accumulationRetailerList.add(promotionAccumulationRetailer);
                                break;
                            } else {
                                //tich luy doanh so
                                promotionAccumulationRetailer.setQuantity(promotionAccumulationRetailer.getQuantity() + accumulationInfo.getQuantity());
                                promotionAccumulationRetailer.setAmount(promotionAccumulationRetailer.getAmount() + accumulationInfo.getAmount());
                                if (promotionAccumulationRetailer.getAmount() >= promotion.getQuantity()) {
                                    //tich luy doanh so thoa dk
                                    ret.add(promotion);

                                    promotionAccumulationRetailer.setStatusId(25);
                                    //tinh so luong san pham dc tang thuong
                                    int awardQuantity = (promotionAccumulationRetailer.getAmount() / promotion.getQuantity()) * promotion.getProAwardQuantity();

                                    //de trang thai tich luy la bi loai=> neu ko dc chon se bi loai
                                    promotionAccumulationRetailer.setStatusId(25);
                                    if (promotion.getPromotionAwards().getId() == 1) {
                                        //tang sp                                        
                                        promotionAccumulationRetailer.setAwardQuantity(awardQuantity);
                                    } else if (promotion.getPromotionAwards().getId() == 2) {
                                        //tang vat pham
                                        promotionAccumulationRetailer.setAwardQuantity(awardQuantity);
                                    } else if (promotion.getPromotionAwards().getId() == 3) {
                                        //chiet khau giam tien
                                        promotionAccumulationRetailer.setAwardAmount(awardQuantity);
                                    } else {
                                        //chiet khau %
                                        promotionAccumulationRetailer.setAwardAmount(promotion.getProAwardQuantity() * promotionAccumulationRetailer.getAmount() / 100);
                                    }
                                }
                                accumulationRetailerList.add(promotionAccumulationRetailer);
                                break;
                            }
                        }
                    }
                }
            }
        }

        //cap nhat lai cac KM tich luy
        try {
            dataService.insertOrUpdateArray(accumulationRetailerList);
            for (PromotionAccumulationRetailer promotionAccumulationRetailer : accumulationRetailerList) {
                for (Promotion promotion : ret) {
                    if (Objects.equals(promotion.getId(), promotionAccumulationRetailer.getPromotions().getId())) {
                        promotion.setAccumulationRetailerId(promotionAccumulationRetailer.getId());
                        break;
                    }
                }
            }
        } catch (Exception ex) {
        }

        //tao chuoi dieu kien
        for (Promotion promotion : ret) {
            promotion.generateConditionString();
        }

        return ret;
    }

    @Override
    public List<PromotionTransRetailer> getListPromotionTransRetailer(int posId, int promotionId, DataService dataService) {
        String hql = "FROM PromotionTransRetailer"
                + " WHERE deletedUser = 0"
                + " AND poss.id = :posId"
                + " AND promotions.id = :promotionId";
        //+ " ORDER BY createdAt DESC";
        //+ " AND statuss.value IN (1)";//hien tai cho huong nhieu lan
        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);
        parameters.add(MsalesParameter.create("posId", posId));
        List<PromotionTransRetailer> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        return list;
    }

    @Override
    public List<PromotionAccumulationRetailer> getListPromotionAccumulationRetailer(int posId, int promotionId, DataService dataService) {
        String hql = "FROM PromotionAccumulationRetailer"
                + " WHERE deletedUser = 0"
                + " AND retailers.id = :posId"
                + " AND promotions.id = :promotionId";
        //+ " ORDER BY createdAt DESC";
        // + " AND statuss.value IN (1)";//hien tai cho huong nhieu lan
        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);
        parameters.add(MsalesParameter.create("posId", posId));
        List<PromotionAccumulationRetailer> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        return list;
    }

    @Override
    public List<Promotion> getListRegisterPromotionApp(int posId, DataService dataService) {
        List<Promotion> ret = new ArrayList<>();
        POS pos = dataService.getRowById(posId, POS.class);
        if (pos != null) {
            List<Channel> channelList = new ArrayList<>();
            channelList.add(pos.getChannels());
            List<Promotion> promotionList = getListPromotionByChannelList(channelList, pos.getChannels().getCompanys().getId(), dataService);

            if (!promotionList.isEmpty()) {
//                List<Integer> ids = new ArrayList<>();
//                for (Promotion promotion : promotionList) {
//                    if (promotion.getIsRegister() == 1) {
//                        ids.add(promotion.getId());//chi lay KM dang ky
//                    }
//                }
                //lay danh CTKM da dang ky (Đang chờ duyệt,Hoàn thành,Tích lũy).

                for (Promotion promotion : promotionList) {
                    boolean flag = true;
                    if (promotion.getIsRegister() == 1) {
                        if (promotion.getConditionQuantity() == 1 || promotion.getConditionQuantity() == 2) {
                            //KM truc tiep
                            List<PromotionTransRetailer> promotionTransRetailerList = getListPromotionTransRetailer(posId, promotion.getId(), dataService);
                            if (promotion.getIsOnce() == 1) {
                                //KM mot lan
                                for (PromotionTransRetailer promotionTransRetailer : promotionTransRetailerList) {
                                    if (promotionTransRetailer.getStatuss().getId() != 25)//ko bi loai
                                    {
                                        //da dc dang ky
                                        flag = false;
                                        break;
                                    }
                                }
                            } else {
                                //KM nhieu lan
                                for (PromotionTransRetailer promotionTransRetailer : promotionTransRetailerList) {
                                    if (promotionTransRetailer.getStatuss().getId() == 30//da dang ky
                                            || promotionTransRetailer.getStatuss().getId() == 29//da duyet dang ky                                            
                                            || promotionTransRetailer.getStatuss().getId() == 23//dang cho duyet
                                            || promotionTransRetailer.getStatuss().getId() == 24//Hoan thanh
                                            || promotionTransRetailer.getStatuss().getId() == 27//Da giao
                                            ) {
                                        //da dc dang ky
                                        flag = false;
                                    }
                                }
                            }
                        } else {
                            //KM tich luy
                            List<PromotionAccumulationRetailer> promotionAccumulationRetailerList = getListPromotionAccumulationRetailer(posId, promotion.getId(), dataService);
                            if (promotion.getIsOnce() == 1) {
                                //KM mot lan
                                for (PromotionAccumulationRetailer promotionAccumulationRetailer : promotionAccumulationRetailerList) {
                                    if (promotionAccumulationRetailer.getStatuss().getId() != 25)//ko bi loai
                                    {
                                        //da dc dang ky
                                        flag = false;
                                        break;
                                    }
                                }
                            } else {
                                //KM nhieu lan
                                for (PromotionAccumulationRetailer promotionAccumulationRetailer : promotionAccumulationRetailerList) {
                                    if (promotionAccumulationRetailer.getStatuss().getId() == 30//da dang ky
                                            || promotionAccumulationRetailer.getStatuss().getId() == 29//da duyet dang ky                                            
                                            || promotionAccumulationRetailer.getStatuss().getId() == 23//dang cho duyet
                                            || promotionAccumulationRetailer.getStatuss().getId() == 24//Hoan thanh
                                            || promotionAccumulationRetailer.getStatuss().getId() == 27//Da giao
                                            || promotionAccumulationRetailer.getStatuss().getId() == 26//tich luy
                                            ) {
                                        //da dc dang ky
                                        flag = false;
                                    }
                                }
                            }
                        }
                        if (flag) {
                            ret.add(promotion);
                        }
                    }
                }
            }
        }

        return ret;
    }

    //Kiem tra DBH cua KM co thuoc quan ly cua nhan vien ko
    private List checkPromotionRetailerByMCP(List promotionRetailerList, List<Integer> userIdList, DataService dataService) {
        List ret = new ArrayList();
        for (Object object : promotionRetailerList) {
            Date monday;
            Date sunday;
            int posId;
            if (object instanceof PromotionTransRetailer) {
                PromotionTransRetailer promotionTransRetailer = (PromotionTransRetailer) object;
                monday = DateUtils.getMonday(promotionTransRetailer.getCreatedAt());
                sunday = DateUtils.getSunday(promotionTransRetailer.getCreatedAt());
                posId = promotionTransRetailer.getPoss().getId();
            } else {
                //PromotionAccumulationRetailer
                PromotionAccumulationRetailer PromotionAccumulationRetailer = (PromotionAccumulationRetailer) object;
                monday = DateUtils.getMonday(PromotionAccumulationRetailer.getCreatedAt());
                sunday = DateUtils.getSunday(PromotionAccumulationRetailer.getCreatedAt());
                posId = PromotionAccumulationRetailer.getRetailers().getId();
            }

            for (int userId : userIdList) {
                if (checkMCPWeekByUser(userId, posId, monday, sunday, dataService)) {
                    //OK
                    ret.add(object);
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    public List<Object> getListPromotionCompleted(List<Channel> channelList, int companyId, List<Integer> userIdList, DataService dataService) {
        List<Promotion> promotionList = getCbListPromotionByChannelList(channelList, companyId, dataService);
        List ret = new ArrayList<>();

        if (promotionList.isEmpty()) {
            return ret;
        }

        List<Integer> ids = new ArrayList<>();
        for (Promotion promotion : promotionList) {
            ids.add(promotion.getId());
        }

        String hql1 = "FROM PromotionTransRetailer"
                + " WHERE deletedUser = 0"
                + " AND statuss.value = 2"//chi lay KM da hoan thanh
                //+ " AND poss.id = :posId"
                + " AND promotions.deletedUser = 0"
                + " AND promotions.id IN (:ids)";
        //+ " ORDER BY createdAt DESC";

        String hql2 = "FROM PromotionAccumulationRetailer"
                + " WHERE deletedUser = 0"
                + " AND statuss.value = 2"//chi lay KM da hoan thanh
                //+ " AND retailers.id = :posId"
                + " AND promotions.deletedUser = 0"
                + " AND promotions.id IN (:ids)";
        //+ " ORDER BY createdAt DESC";
        List<MsalesParameter> parameters = new ArrayList<>();
        parameters.add(MsalesParameter.create("ids", ids, 1));

        ret.addAll(dataService.executeSelectHQL(hql1, parameters, false, 0, 0));
        ret.addAll(dataService.executeSelectHQL(hql2, parameters, false, 0, 0));
        Collections.sort(ret);
        return checkPromotionRetailerByMCP(ret, userIdList, dataService);
    }

    @Override
    public List<Object> getListPOSWaiting(int promotionId, List<Integer> userIdList, DataService dataService
    ) {
        String hql1 = "FROM PromotionTransRetailer"
                + " WHERE deletedUser = 0"
                + " AND statuss.value IN (1,6,7)"//KM da dang ky,KM da duyet dang ky,KM dang cho duyet
                + " AND promotions.id = :promotionId"
                + " AND promotions.deletedUser = 0";
        //+ " ORDER BY createdAt DESC";

        String hql2 = "FROM PromotionAccumulationRetailer"
                + " WHERE deletedUser = 0"
                + " AND statuss.value IN (1,6,7)"//KM da dang ky,KM da duyet dang ky,KM dang cho duyet
                + " AND promotions.id = :promotionId"
                + " AND promotions.deletedUser = 0";
        //+ " ORDER BY createdAt DESC";
        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);

        List ret = new ArrayList<>();
        ret.addAll(dataService.executeSelectHQL(hql1, parameters, false, 0, 0));
        ret.addAll(dataService.executeSelectHQL(hql2, parameters, false, 0, 0));
        Collections.sort(ret);
        return checkPromotionRetailerByMCP(ret, userIdList, dataService);
    }

    @Override
    public List<Object> getListPOSCompleted(int promotionId, List<Integer> userIdList, DataService dataService
    ) {
        String hql1 = "FROM PromotionTransRetailer"
                + " WHERE deletedUser = 0"
                + " AND statuss.value IN (2,5)"//chi lay KM hoan thanh (da duyet),da giao
                + " AND promotions.id = :promotionId"
                + " AND promotions.deletedUser = 0";
        //+ " ORDER BY createdAt DESC";
        String hql2 = "FROM PromotionAccumulationRetailer"
                + " WHERE deletedUser = 0"
                + " AND statuss.value IN (2,5)"//chi lay KM hoan thanh (da duyet),da giao
                + " AND promotions.id = :promotionId"
                + " AND promotions.deletedUser = 0";
        //+ " ORDER BY createdAt DESC";
        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);
        List ret = new ArrayList<>();
        ret.addAll(dataService.executeSelectHQL(hql1, parameters, false, 0, 0));
        ret.addAll(dataService.executeSelectHQL(hql2, parameters, false, 0, 0));
        Collections.sort(ret);
        return checkPromotionRetailerByMCP(ret, userIdList, dataService);
    }

    @Override
    public List<Object> getListPOSDelivered(int promotionId, List<Integer> userIdList, DataService dataService
    ) {
        String hql1 = "FROM PromotionTransRetailer"
                + " WHERE deletedUser = 0"
                + " AND statuss.value = 2"//chi lay KM hoan thanh
                + " AND promotions.id = :promotionId"
                + " AND promotions.deletedUser = 0";
        //+ " ORDER BY createdAt DESC";

        String hql2 = "FROM PromotionAccumulationRetailer"
                + " WHERE deletedUser = 0"
                + " AND statuss.value = 2"//chi lay KM hoan thanh
                + " AND promotions.id = :promotionId"
                + " AND promotions.deletedUser = 0";
        //+ " ORDER BY createdAt DESC";

        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);

        List ret = new ArrayList<>();
        ret.addAll(dataService.executeSelectHQL(hql1, parameters, false, 0, 0));
        ret.addAll(dataService.executeSelectHQL(hql2, parameters, false, 0, 0));
        Collections.sort(ret);
        return checkPromotionRetailerByMCP(ret, userIdList, dataService);
    }

    @Override
    public List<Object> getListPOSCancel(int promotionId, List<Integer> userIdList, DataService dataService) {
        String hql1 = "FROM PromotionTransRetailer"
                + " WHERE deletedUser = 0"
                + " AND statuss.value = 3"//chi lay KM bi loai
                + " AND promotions.id = :promotionId"
                + " AND promotions.deletedUser = 0";
        //+ " ORDER BY createdAt DESC";

        String hql2 = "FROM PromotionAccumulationRetailer"
                + " WHERE deletedUser = 0"
                + " AND statuss.value = 3"//chi lay KM bi loai
                + " AND promotions.id = :promotionId"
                + " AND promotions.deletedUser = 0";
        //+ " ORDER BY createdAt DESC";
        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);

        List ret = new ArrayList<>();
        ret.addAll(dataService.executeSelectHQL(hql1, parameters, false, 0, 0));
        ret.addAll(dataService.executeSelectHQL(hql2, parameters, false, 0, 0));
        Collections.sort(ret);
        return checkPromotionRetailerByMCP(ret, userIdList, dataService);
    }

    @Override
    public LinkedHashMap getSupKPIApp(List<User> userList, int companyId, CompanyConfigKpi companyConfigKpi, CompanyConstant companyConstant, int month, int year, DataService dataService) {
        Date fromDate = DateUtils.getFirstDayOfMonth(month, year);
        Date toDate = DateUtils.getLastDayOfMonth(month, year);

        Long dateDif = DateUtils.subDate(fromDate, toDate);

        //tao map luu doanh so ban
        HashMap soldMap = new HashMap();
        List<Integer> ids = new ArrayList<>();
        for (User user : userList) {
            ids.add(user.getId());
            HashMap hashMap = new HashMap();
            hashMap.put("amount", 0L);
            hashMap.put("posSold", 0);
            soldMap.put(user.getId(), hashMap);
        }

        List<Integer> posIdList = getListPOSIdByListUserId(ids, DateUtils.addDay(fromDate, -6), DateUtils.addDay(toDate, 6), dataService);

        List<LinkedHashMap> mapList = new ArrayList<>();
        LinkedHashMap contents = new LinkedHashMap();

        //quy tat ca ra phut
        int timeWorkConfig1 = companyConfigKpi.getPeriod1() * 60;
        int timePoint1 = companyConfigKpi.getPeriodPoint1();
        int timeWorkConfig2 = companyConfigKpi.getPeriod2() * 60;
        int timePoint2 = companyConfigKpi.getPeriodPoint2();

        for (int posId : posIdList) {
            List<SalesTrans> salesTransList = getListSalesTransByPOS(posId, fromDate, toDate, 2, dataService);
            for (SalesTrans salesTrans : salesTransList) {
                int userId = 0;
                boolean flag = false;
                //check 
                for (int id : ids) {
                    if (salesTrans.getCreatedUser() == id) {
                        //OK
                        flag = true;
                        userId = id;
                        break;
                    }
                }
                if (!flag) {
                    User user = getUserByMCPWeek(posId, salesTrans.getCreatedAt(), dataService);
                    if (user != null) {
                        for (int id : ids) {
                            if (user.getId() == id) {
                                //OK
                                flag = true;
                                userId = id;
                                break;
                            }
                        }
                    }
                }
                if (flag) {
                    List<SalesTransDetails> salesTransDetailList = getListSalesTransDetails(salesTrans.getId(), dataService);

                    HashMap hashMap = (HashMap) soldMap.get(userId);
                    for (SalesTransDetails salesTransDetails : salesTransDetailList) {
                        long amount = (long) hashMap.get("amount");
                        amount += 1L * salesTransDetails.getPrice() * salesTransDetails.getQuantity();
                        hashMap.put("amount", amount);
                    }
                    hashMap.put("posSold", (int) hashMap.get("posSold") + 1);
                }
            }

            //sales order
            List<SalesOrder> salesOrderList = getListSalesOrderByPOS(posId, fromDate, toDate, dataService);
            for (SalesOrder salesOrder : salesOrderList) {
                int userId = 0;
                boolean flag = false;
                //check 
                if (salesOrder.getStatuss().getId() == 18
                        || salesOrder.getStatuss().getId() == 19
                        || salesOrder.getStatuss().getId() == 20) {

                    for (int id : ids) {
                        if (salesOrder.getCreatedUsers().getId() == id) {
                            //OK
                            flag = true;
                            userId = id;
                            break;
                        }
                    }
                    if (!flag) {
                        User user = getUserByMCPWeek(posId, salesOrder.getCreatedAt(), dataService);
                        if (user != null) {
                            for (int id : ids) {
                                if (user.getId() == id) {
                                    //OK
                                    flag = true;
                                    userId = id;
                                    break;
                                }
                            }
                        }
                    }
                    if (flag) {
                        List<SalesOrderDetails> salesOrderDetailsList = getListSalesOrderDetails(salesOrder.getId(), dataService);

                        HashMap hashMap = (HashMap) soldMap.get(userId);
                        for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                            long amount = (long) hashMap.get("amount");
                            amount += 1L * salesOrderDetails.getPrice() * salesOrderDetails.getQuantity();
                            hashMap.put("amount", amount);
                        }
                        hashMap.put("posSold", (int) hashMap.get("posSold") + 1);
                    }
                }
            }
        }
//        int userId = 0;
//        int salesTransId = 0;
//        int salesOrderId = 0;
//        for (int posId : posIdList) {
//            List<SalesTransDetails> salesTransDetailsList = getListSalesTransDetailsByPOS(posId, fromDate, toDate, 2, dataService);
//            for (SalesTransDetails salesTransDetails : salesTransDetailsList) {
//                boolean flag = false;
//                //check 
//                for (int id : ids) {
//                    if (salesTransDetails.getCreatedUser() == id) {
//                        //OK
//                        flag = true;
//                        userId = id;
//                        break;
//                    }
//                }
//                if (!flag) {
//                    if (salesTransDetails.getSalesTranss().getId() == salesTransId) {
//                        for (int id : ids) {
//                            if (userId == id) {
//                                //OK
//                                flag = true;
//                                break;
//                            }
//                        }
//                    } else {
//                        salesTransId = salesTransDetails.getSalesTranss().getId();
//                        User user = getUserByMCPWeek(posId, salesTransDetails.getCreatedAt(), dataService);
//                        for (int id : ids) {
//                            if (user.getId() == id) {
//                                //OK
//                                flag = true;
//                                userId = id;
//                                break;
//                            }
//                        }
//                    }
//                }
//                if (flag) {
//                    HashMap hashMap = (HashMap) soldMap.get(userId);
//                    long amount = (long) hashMap.get("amount");
//                    amount += 1L * salesTransDetails.getPrice() * salesTransDetails.getQuantity();
//                    hashMap.put("amount", amount);
//                    hashMap.put("posSold", (int) hashMap.get("posSold") + 1);
//                }
//            }
//
//            //sales order
//            List<SalesOrderDetails> salesOrderDetailsList = getListSalesOrderDetailsByPOS(posId, fromDate, toDate, dataService);
//            for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
//                boolean flag = false;
//                //check 
//                if (salesOrderDetails.getOrders().getStatuss().getId() == 18
//                        || salesOrderDetails.getOrders().getStatuss().getId() == 19
//                        || salesOrderDetails.getOrders().getStatuss().getId() == 20) {
//
//                    for (int id : ids) {
//                        if (salesOrderDetails.getOrders().getCreatedUsers().getId() == id) {
//                            //OK
//                            flag = true;
//                            userId = id;
//                            break;
//                        }
//                    }
//                    if (!flag) {
//                        if (salesOrderDetails.getOrders().getId() == salesOrderId) {
//                            for (int id : ids) {
//                                if (userId == id) {
//                                    //OK
//                                    flag = true;
//                                    break;
//                                }
//                            }
//                        } else {                            
//                            salesOrderId = salesOrderDetails.getOrders().getId();
//                            User user = getUserByMCPWeek(posId, salesOrderDetails.getCreatedAt(), dataService);
//                            for (int id : ids) {
//                                if (user.getId() == id) {
//                                    //OK
//                                    flag = true;
//                                    userId = id;
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    if (flag) {
//                        HashMap hashMap = (HashMap) soldMap.get(userId);
//                        long amount = (long) hashMap.get("amount");
//                        amount += 1L * salesOrderDetails.getPrice() * salesOrderDetails.getQuantity();
//                        hashMap.put("amount", amount);
//
//                        hashMap.put("posSold", (int) hashMap.get("posSold") + 1);
//                    }
//                }
//            }
//        }

        int mcpNull = 0;
        DecimalFormat decimalFormat = new DecimalFormat("###0.##");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(',');
        dfs.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(dfs);
        for (User user : userList) {
            int workTimePoint;
            long workedTime;

            long targetCare = getTargetCare(user.getId(), fromDate, toDate, dataService);
            double totalCared = getTotalCared(user.getId(), fromDate, toDate, dataService);
            double percentCare;
            int carePoint;

            double targetSold;
            long soldPoint;
            double soldPercent;

            LinkedHashMap item = new LinkedHashMap();
            item.put("id", user.getId());
            item.put("name", user.getLastName() + " " + user.getFirstName());
            item.put("posSold", ((HashMap) soldMap.get(user.getId())).get("posSold"));

            MCP mcpSale = getMCPSale(user.getId(), fromDate, dataService);

            if (mcpSale != null) {
                item.put("mcpSalesId", mcpSale.getId());
                //tinh chi tieu ban
                long amount = (long) ((HashMap) soldMap.get(user.getId())).get("amount");
                targetSold = mcpSale.getSalesPerMonth();
                soldPercent = targetSold == 0 ? 100 : amount * 100 / targetSold;//neu chi tieu là 0=>% =?

                if (soldPercent < companyConfigKpi.getPercentTarget1()) {
                    soldPoint = companyConfigKpi.getPercentTargetPoint1();
                } else if (soldPercent < companyConfigKpi.getPercentTarget2()) {
                    soldPoint = companyConfigKpi.getPercentTargetPoint2();
                } else {
                    soldPoint = 0;
                }

                //tinh gio lam
                Date date = fromDate;
                long workedTimeFull = 0;
                while (toDate.compareTo(date) >= 0) {
                    //tinh theo giay
                    workedTimeFull += getWorkedTime(user.getId(), companyConstant, date, dataService);
                    date = DateUtils.addDay(date, 1);
                }

                workedTime = workedTimeFull / (dateDif.intValue() * 60);

                if (workedTime < timeWorkConfig1) {
                    workTimePoint = timePoint1;
                } else if (workedTime < timeWorkConfig2) {
                    //70
                    workTimePoint = timePoint2;
                } else {
                    //0
                    workTimePoint = 0;
                }

                //tinh cham soc
                percentCare = (targetCare == 0 ? 0 : totalCared * 100 / targetCare);
                if (percentCare < companyConfigKpi.getPercentPos1()) {
                    carePoint = companyConfigKpi.getPercentPosPoint1();
                } else if (percentCare < companyConfigKpi.getPercentPos2()) {
                    carePoint = companyConfigKpi.getPercentPosPoint2();
                } else {
                    carePoint = 0;
                }

                item.put("targetSold", targetSold);
                item.put("totalSold", amount);
                item.put("soldPercent", decimalFormat.format(soldPercent));
                item.put("soldPoint", soldPoint);

                item.put("targetCare", targetCare);
                item.put("totalCared", totalCared);
                item.put("carePercent", decimalFormat.format(percentCare));
                item.put("carePoint", carePoint);

                item.put("workTime", workedTimeFull / 3600 + " giờ " + workedTimeFull % 3600 / 60 + " phút");
                item.put("workTimePoint", workTimePoint);

                long point = soldPoint + carePoint + workTimePoint;
                item.put("point", point);
                String color;
                if (point > companyConfigKpi.getMaxPoint()) {
                    color = companyConfigKpi.getMaxCorlor();
                } else if (point > companyConfigKpi.getMediumPoint()) {
                    color = companyConfigKpi.getMediumCorlor();
                } else {
                    color = companyConfigKpi.getMinCorlor();
                }
                item.put("color", color);
                item.put("note", mcpSale.getNote());
                mapList.add(item);
            } else {
                mcpNull++;
            }

        }
        contents.put("mcpNull", mcpNull);
        contents.put("userList", mapList);
        return contents;
    }

    @Override
    public String createPOSCode(int companyId, Location ward, DataService dataService) {
        String posCode = ward.getParents().getParents().getCode() + "_"
                + ward.getParents().getCode() + "_" + ward.getCode();
        String hql = "SELECT posCode"
                + " FROM POS"
                + " WHERE deletedUser = 0"
                + " AND channels.companys.id = :companyId"
                + " AND posCode LIKE :posCode ESCAPE '!'";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("posCode", posCode + "!_%"));
        List<String> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        int count = 0;
        for (String s : list) {
            try {
                String[] tempArray = s.split("_");
                int temp = Integer.parseInt(tempArray[tempArray.length - 1]);
                if (temp > count) {
                    count = temp;
                }
            } catch (Exception ex) {
            }
        }
        count++;
        return posCode + "_" + (count < 10 ? "000" + count : count < 100 ? "00" + count : count);
    }
}
