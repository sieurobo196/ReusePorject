/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.app.service;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.CompanyConfig;
import vn.itt.msales.entity.CompanyConfigDetails;
import vn.itt.msales.entity.CompanyConfigKpi;
import vn.itt.msales.entity.CompanyConstant;
import vn.itt.msales.entity.Equipment;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsCategory;
import vn.itt.msales.entity.GoodsUnit;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.MCPDetails;
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
import vn.itt.msales.services.DataService;

/**
 *
 * @author vtm
 */
public interface AppService {

    //searchSupOrderApp
    public List<HashMap> searchSupOrderApp(List<Integer> userIds, Date fromDate, Date toDate, DataService dataService);

    //get order details
    public List<HashMap> getOrderGoodsDetails(int orderId, DataService dataService);

    //mcp 
    public int createMCP(int userId, DataService dataService);

    public int createMCP(int userId, Date date, DataService dataService);

    public Integer getMCPId(int userId, Date date, DataService dataService);

    public MCP getMCPNow(int userId, DataService dataService);

    public List<MCP> getMCP(int userId, Date fromDate, Date toDate, DataService dataService);

    public List<MCPDetails> getListMCPDetails(int mcpId, DataService dataService);

    public List<HashMap> getListMCP(int userId, Date fromDate, Date toDate, DataService dataService);

    //branch
    public Integer getBranchFromUsername(String username,boolean deleted, DataService dataService) throws Exception;

    //user
    public User getUser(String username, String password, DataService dataService);

    public User getUser(int id, String password, DataService dataService);

    //check user da dang ky thiet bi khac 
    public boolean checkUserRegister(int userId, String imei, String subscriberId, DataService dataService);

    //Equipment
    public Equipment getEquipment(String imei, String subscriberId, DataService dataService);

    public Equipment getEquipmentByUser(int userId, DataService dataService);

    //get Version 
    public Version getVersion(int companyId, DataService dataService);

    //get List companyConfig
    public List<CompanyConfig> getListCompanyConfig(int companyId, int userRoleId, DataService dataService);

    //get List companyConfigDetails
    public List<CompanyConfigDetails> getListCompanyConfigDetails(int companyConfigId, DataService dataService);

    //get List companyConfigDetails HistoryFunction
    public List<CompanyConfigDetails> getListHistoryFunctionDetails(int companyConfigId, DataService dataService);

    //get List WorkflowType
    public List<WorkflowType> getListWorkflowType(int type, DataService dataService);

    //get List Workflow
    public List<Workflow> getListCustomerCareWorkflow(int type, int companyId, DataService dataService);

    //get List Workflow
    public List<Workflow> getListWorkflowByWorkflowType(int workflowTypeId, int companyId, DataService dataService);

    public List<Workflow> getListWorkflowByWorkflowDetails(int workflowDetailsId, DataService dataService);

    //get List WorkflowDetails 
    public List<WorkflowDetails> getListWorkflowDetails(int worlflowId, DataService dataService);

    //get Target Info
    public long getPOSNew(int userId, DataService dataService);

    public MCP getMCPSale(int userId,Date date, DataService dataService);//ban hang
    public MCP getMCP(int userId,Date date, DataService dataService);//tuyen duong

    public Integer getMCPSaleId(int userId, DataService dataService);//ban hang

    //get Stock App
    public List<SalesStockGoods> getStockApp(int userId, DataService dataService);

    //get List GoodsUnit by goodsId
    public List<GoodsUnit> getListGoodsUnitByGoodsId(int goodsId, DataService dataService);
    
    public GoodsUnit getMinGoodsUnit(int goodsId,DataService dataService);

    //get List UserRoleChannel
    public List<UserRoleChannel> getListUserRoleChannel(int userId, DataService dataService);

    public List<Integer> getListUserRoleChannelId(int userId, DataService dataService);

    public List<Channel> getListChannel(int userId, DataService dataService);

    public List<Integer> getListChannelId(int userId, DataService dataService);

    public List<Channel> getListChannelByParent(List<Integer> ids, DataService dataService);

    //get List User by Sup
    public List<User> getListUserBySup(int userId, int companyId, DataService dataService);

    public List<HashMap> getCbListUserBySup(int userId, int companyId, DataService dataService);

    public List<Integer> getListUserIdBySup(int userId, int companyId, DataService dataService);

    public List<User> getListUserByChannel(int channelId, DataService dataService);

    public List<HashMap> getCbListUserByChannel(int channelId, DataService dataService);

    public List<User> getAllListUserByChannel(int channelId, int companyId, DataService dataService);

    public List<HashMap> getCbAllListUserByChannel(int channelId, int companyId, DataService dataService);

    //get List POS by MCP trong tuan
    public List<POS> getListPOSByListUserId(List<Integer> userIds, DataService dataService);

    public List<Integer> getListPOSIdByListUserId(List<Integer> userIds, DataService dataService);

    public List<Integer> getListPOSIdByUser(int userId, Date fromDate, Date toDate, DataService dataService);
    
    public List<Integer> getListPOSIdByListUserId(List<Integer> ids, Date fromDate, Date toDate, DataService dataService);

    public List<MCPDetails> getListMCPDetailsByUser(int userId, Date beginDate, DataService dataService);
    
    public List<MCPDetails> getListMCPDetailsByPOS(int posId, Date beginDate, DataService dataService);

    //searchSupRouteApp
    public List<HashMap> searchSupRouteApp(int userId, Date beginDate, DataService dataService);

    public List<HashMap> getListUserRoute(int userId, Date date, DataService dataService);

    //check Last order/sale
    public Date checkLastOrder(int posId, Date date, DataService dataService);

    public Date checkLastSale(int posId, Date date, DataService dataService);

    //order
    public long countSalesOrder(int userId, Date fromDate, Date toDate, DataService dataService);

    public List<SalesOrder> getListSalesOrderByPOS(int posId, Date fromDate, Date toDate, DataService dataService);
    
    public List<SalesOrderDetails> getListSalesOrderDetailsByPOS(int posId, Date fromDate, Date toDate, DataService dataService);

    public List<SalesOrderDetails> getListSalesOrderDetails(int salesOrderId, DataService dataService);

    public List<SalesTrans> getListSalesTransByPOS(int posId, Date fromDate, Date toDate, int transType, DataService dataService);
    
    public List<SalesTransDetails> getListSalesTransDetailsByPOS(int posId, Date fromDate, Date toDate, int transType, DataService dataService);

    public List<SalesTransDetails> getListSalesTransDetails(int salesTransId, DataService dataService);

    public long getSumSalesTrans(int salesTransId, DataService dataService);

    //history Care
    public long getTotalPOS(int userId, DataService dataService);

    public long getTotalCared(List<Integer> posIdList, DataService dataService);

    public long getTotalOrder(List<Integer> ids, DataService dataService);

    public long getTotalSale(List<Integer> ids, DataService dataService);

    public long getTotalPriceOrder(List<Integer> ids, DataService dataService);

    public long getTotalPriceSale(List<Integer> ids, DataService dataService);

    //check visited
    public boolean checkPOSVisited(int mcpDetailsId, DataService dataService);

    public boolean checkVisited(int posId, Date date, DataService dataService);

    public int getCountVisited(List<POS> posList, Date fromDate, Date toDate, DataService dataService);

    //search POS cua MCP trong ngay
    public List<HashMap> searchPOS(int userId, String posCode, DataService dataService);

    //get List Status by StatusType
    public List<Status> getListStatusByStatusType(int statusTypeId, DataService dataService);

    public Status getStatusByValue(int statusTypeId, String value, DataService dataService);

    public CompanyConstant getCompanyConstant(int companyId, DataService dataService);
    
    //get KPI config
    public CompanyConfigKpi getCompanyConfigKPI(int companyId, DataService dataService);

    //check workflow
    public boolean checkRequireWorkflowTarget(int companyId, int userId, DataService dataService);

    public boolean checkRequireWorkflowReceive(int companyId, int userId, DataService dataService);

    //check user receive Goods ?
    public boolean checkUserReceivedGoods(int userId, Date date, DataService dataService);

    //get list POS by MCP
    public List<POS> getListPOSByMCP(List<Integer> userIds, Date fromDate, Date toDate, DataService dataService);

    public List<Integer> getListPOSIdByMCP(List<Integer> userIds, Date fromDate, Date toDate, DataService dataService);

    //get implement user from POS + Now
    public Integer getMCPUserIdFromPOS(int posId, DataService dataService);

    //get mcp from POS + now
    public Integer getMCPIdFromPOS(int posId, DataService dataService);

    //check MCP user in WEEK
    public boolean checkMCPWeekByUser(int userId, int posId, Date monday, Date sunday, DataService dataService);
    //get user MCP by pos and date
    public User getUserByMCPWeek(int posId,Date date,DataService dataService);

    //get List GoodsCategory
    public List<GoodsCategory> getListGoodsCategory(int userId, int companyId, DataService dataService);

    //get list all goods
    public List<Goods> getListGoods(int companyId, DataService dataService);

    //get list goods by list GoodsCategory
    public List<Goods> getListGoodsByListGoodsCategoryId(List<Integer> ids, DataService dataService);

    //search SUP POS
    public List<HashMap> searchSupPOS(int mcpId, String posCode, DataService dataService);

    //check transcode
    public boolean checkSalesTransCode(String transCode, DataService dataService);

    public boolean checkOrderCode(String transCode, DataService dataService);

    //check time work
    public boolean checkTimeWork(CompanyConstant companyConstant);

    //check goods Focus
    public boolean checkGoodsFocusByPOSId(int goodsId, int posId, DataService dataService);

    public boolean checkGoodsFocusByChannelId(int goodsId, int channelId, DataService dataService);

    public boolean checkGoodsFocusByChannel(int goodsId, Channel channel, DataService dataService);

    public boolean checkGoodsFocusByUserId(int goodsId, int userId, DataService dataService);

    public boolean checkGoodsFocus(int goodsId, List<Integer> channelIdList, DataService dataService);

    //get salesStockGoods by goodsId
    public List<SalesStockGoods> getListSalesStockGoodsByListGoodsId(int salesStockId, List<Integer> goodsIds, DataService dataService);

    //get salesStockGoods by type 1:1 san pham;2:nhom san pham;3:tat ca san pham
    public List<SalesStockGoods> getListSalesStockGoodsByType(int userId, int type, Integer id, DataService dataService);

    //get salesStock
    public SalesStock getSalesStockUser(int userId, DataService dataService);

    public SalesStock getSalesStockPOS(int posId, DataService dataService);

    public SalesStock getSalesStockChannel(int channelId, DataService dataService);

    public SalesStockGoods getSalesStockGoodsByGoodsId(int salesStockId, int goodsId, DataService dataService);

    //get SalesOrder
    public SalesOrder getSalesOrder(int orderId, DataService dataService);

    public SalesOrder getSalesOrderWaiting(int orderId, DataService dataService);

    public List<Location> getListLocationByUserId(int userId, DataService dataService);

    public List<Location> getListLocationByParent(int parentId, DataService dataService);
    
    //KPI sales sup
    public Long getTargetCare(int userId,Date fromDate,Date toDate,DataService dataService);
    public Long getTotalCared(int userId,Date fromDate,Date toDate,DataService dataService);
    
    //tinh theo phut
    public long getWorkedTime(int userId,CompanyConstant companyConstant,Date date,DataService dataService);

    /**
     * --------------------------------------------------------------------------------
     * PROMOTION
     * --------------------------------------------------------------------------------
     */
    //get list promotion running
    public List<Promotion> getCbListPromotionByChannelList(List<Channel> channelList, int companyId, DataService dataService);
    
    public List<Promotion> getListPromotionByChannelIdList(List<Integer> channelIds, int companyId, DataService dataService);

    public List<Promotion> getListPromotionByChannelList(List<Channel> channelList, int companyId, DataService dataService);
    

    public List<Promotion> searchPromotionApp(List<Channel> channelList, int companyId, Date fromDate, Date toDate, DataService dataService);

    public List<Goods> getListGoodsPromotion(int promotionId, DataService dataService);

    public List<Goods> getListGoodsByGoodsCategoryId(int goodsCategoryId, DataService dataService);

    public List<PromotionGoodsRef> getListPromotionGoodsRef(int promotionId, DataService dataService);

    public boolean checkRegisterPromotion(int promotionId, int posId, DataService dataService);

    public List<Promotion> getListRegisterPromotionApp(int posId, DataService dataService);

    public List<Promotion> getListPromotionCanByPOS(int posId, List<Goods> goodsList, int createdUser, DataService dataService);

    public List<PromotionTransRetailer> getListPromotionTransRetailer(int posId, int promotionId, DataService dataService);
    
    public List<PromotionAccumulationRetailer> getListPromotionAccumulationRetailer(int posId, int promotionId, DataService dataService);

    public List<Object> getListPromotionCompleted(List<Channel> channelList,int companyId, List<Integer> userIdList, DataService dataService);

    public List<Object> getListPOSWaiting(int promotionId,List<Integer> userIdList, DataService dataService);

    public List<Object> getListPOSCompleted(int promotionId,List<Integer> userIdList, DataService dataService);

    public List<Object> getListPOSDelivered(int promotionId,List<Integer> userIdList, DataService dataService);

    public List<Object> getListPOSCancel(int promotionId,List<Integer> userIdList, DataService dataService);
    
    public LinkedHashMap getSupKPIApp(List<User> userList,int companyId,CompanyConfigKpi companyConfigKpi,CompanyConstant companyConstant,int month,int year,DataService dataService);

    public String createPOSCode(int companyId,Location ward,DataService dataService);
}
