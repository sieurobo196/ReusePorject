/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import vn.itt.msales.entity.CustomerCareDetails;
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.CustomerCareDetailsById;
import vn.itt.msales.services.DataService;
import java.util.LinkedHashMap;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.customercare.model.CustomerCareDetailWorkflow;
import vn.itt.msales.customercare.model.CustomerCareImage;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.searchObject.SearchSupRoute;

/**
 *
 * @author cshiflett
 */
public class CustomerCareServiceImpl implements CustomerCareService {

    @Autowired
    private DataService dataService;

    @Override
    public MsalesResults<CustomerCareInformation> getListCustomerCareInformationByPOSId(int posid, int page, int size) {
        ParameterList parameterList = new ParameterList(page, size);
        parameterList.add("poss.id", posid);
        parameterList.setOrder("finishCustomerCareAt", "ASC");
        MsalesResults<CustomerCareInformation> list = dataService.getListOption(CustomerCareInformation.class, parameterList, true);
        for (CustomerCareInformation careInformation : list.getContentList()) {
            //set name
            careInformation.getImplementEmployees().setName(careInformation.getImplementEmployees().getLastName() + " " + careInformation.getImplementEmployees().getFirstName());
            if (careInformation.getImplementEmployees().getLocations().getLocationType() == 1) {
                careInformation.getImplementEmployees().setLocation(careInformation.getImplementEmployees().getLocations().getName());
            } else if (careInformation.getImplementEmployees().getLocations().getParents().getLocationType() == 1) {
                careInformation.getImplementEmployees().setLocation(careInformation.getImplementEmployees().getLocations().getParents().getName());
            } else {
                careInformation.getImplementEmployees().setLocation(careInformation.getImplementEmployees().getLocations().getParents().getParents().getName());
            }
            //set MCP
            careInformation.setMcps(careInformation.getMcpDetailss().getMcps());
            //Check isSold
            //Xử lí ngày:
            ParameterList parameterList2 = new ParameterList();
            //Xử lí kiểu bán hàng
            parameterList2.add("transType", 2);
            //Xử lí cho điểm bán hàng.
            List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("poss.id", careInformation.getPoss().getId()));

            if (salesStocks.isEmpty()) {
                // Điểm bán hàng chưa có kho khàng.
            }
            int stockOfPosId = salesStocks.get(0).getId();
            parameterList2.add("toStocks.id", stockOfPosId);
            //Xử lí nhân viên chăm sóc.
            parameterList2.add("createdUser", careInformation.getImplementEmployees().getId());
            //xử lí ngày giờ chăm sóc.
            Date date = careInformation.getStartCustomerCareAt();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String fDate = simpleDateFormat.format(date);
            Date fromDate = new Date();
            try {
                fromDate = simpleDateFormat.parse(fDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                // Dữ liệu chứa giá trị không hợp lệ.
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fromDate);
            calendar.add(calendar.DAY_OF_MONTH, 1);
            Date toDate = calendar.getTime();
            parameterList2.add("salesTransDate", fromDate, ">=");
            parameterList2.add("salesTransDate", toDate, "<");
            List<SalesTrans> salesTrans = dataService.getListOption(SalesTrans.class, parameterList2);
            if (salesTrans.isEmpty()) {
                careInformation.setIsSold(0);
            } else {
                careInformation.setIsSold(1);
            }
        }

        return list;
    }

    @Override
    public CustomerCareInformation getCustomerCareInformationById(int cusId) {
        CustomerCareInformation cci = dataService.getRowById(cusId, CustomerCareInformation.class);
        if (cci != null) {
            List<CustomerCareDetails> lisCustomerCareDetailses = dataService.getListOption(CustomerCareDetails.class, new ParameterList("customerCareInformations.id", cci.getId()));
            cci.setCustomerCareDetailss(lisCustomerCareDetailses);
        }
        return cci;
    }

    @Override
    public List<CustomerCareDetailsById> getListCustomerCareDetailByCustomerCareInfomationId(int cusId) {
        ParameterList parameterList = new ParameterList();
        parameterList.add("customerCareInformations.id", cusId);

        MsalesResults<CustomerCareDetails> list = dataService.getListOption(CustomerCareDetails.class, parameterList, true);
        ArrayList<Integer> arrWorkflowId = new ArrayList();
        for (int i = 0; i < list.getContentList().size(); i++) {
            boolean bool = true;
            for (int j = 0; j < i; j++) {
                if (list.getContentList().get(i).getWorkflows().getId() == list.getContentList().get(j).getWorkflows().getId()) {
                    bool = false;
                    break;
                }
            }
            if (bool) {
                arrWorkflowId.add(list.getContentList().get(i).getWorkflows().getId());
            }
        }
        //Xét List cho đầu ra:
        List<CustomerCareDetailsById> lists = new ArrayList();
        if (!arrWorkflowId.isEmpty()) {
            for (int i = 0; i < arrWorkflowId.size(); i++) {
                CustomerCareDetailsById careDetailsById = new CustomerCareDetailsById();
                List<CustomerCareDetails> cList = new ArrayList();
                String groupName = "";
                for (CustomerCareDetails careDetails : list.getContentList()) {
                    if (arrWorkflowId.get(i) == careDetails.getWorkflows().getId()) {
                        groupName = careDetails.getWorkflows().getWorkflowTypes().getName();
                        careDetails.setTitle(careDetails.getWorkflows().getTitle());
                        cList.add(careDetails);
                    }
                }
                if (!cList.isEmpty()) {
                    careDetailsById.setContents(cList);
                }
                careDetailsById.setGroupName(groupName);
                if (careDetailsById != null) {
                    lists.add(careDetailsById);
                }
            }
        }
        return lists;
    }

    @Override
    public List<LinkedHashMap> searchSupUserRoute(SearchSupRoute searchSupRoute) {
        List<LinkedHashMap> result = new ArrayList<>();
        try {
            ArrayList<Integer> userIds = new ArrayList<>();
            if (searchSupRoute.getUserId() == 0) {
                //Danh sách nhân viên có
                if (searchSupRoute.getListOfEmployees().size() > 1) {
                    //lay tat ca
                    //lay channel ma user dang lam giam sat.
                    List<OptionItem> employerList = searchSupRoute.getListOfEmployees();
                    if (employerList == null || employerList.size() <= 1) {
                        return result;
                    }
                    for (int i = 1; i < employerList.size(); i++) {
                        OptionItem item = employerList.get(i);
                        int id = item.getId();
                        if (id != 0) {
                            userIds.add(id);
                        } else {
                            return result;
                        }
                    }
                } else {
                    //lay tat ca nhan vien trong channel
                    //Không có nhân viên trong kênh
                    return result;
                }
            } else {
                //lay theo nhan vien
                userIds.add(searchSupRoute.getUserId());
            }

            SimpleDateFormat sdfHQL = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(searchSupRoute.getToDate());
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            for (int id : userIds) {
                User user = new User();
                if (id > 0) {
                    user = dataService.getRowById(id, User.class);
                }

                LinkedHashMap item = new LinkedHashMap();
                if (user != null) {
                    item.put("userId", id);
                    item.put("userName", user.getLastName() + " " + user.getFirstName());
                }

                String hql = "SELECT MCPDetails.id as mcpDetailsId,MCPDetails.poss AS pos,"
                        + " CASE WHEN CustomerCareInformation.id IS NULL THEN FALSE ELSE TRUE END AS isVisited,"
                        + " CASE WHEN CustomerCareInformation.finishCustomerCareAt IS NULL THEN NULL ELSE DATE_FORMAT(CustomerCareInformation.finishCustomerCareAt,'%d/%m/%Y %H:%i:%S') END AS timeCared"
                        + " FROM CustomerCareInformation as CustomerCareInformation"
                        + " RIGHT JOIN CustomerCareInformation.mcpDetailss as MCPDetails"
                        + " WHERE MCPDetails.mcps.implementEmployees.id = " + user.getId();
                if (searchSupRoute.getFromDate() != null) {
                    hql += " AND MCPDetails.mcps.beginDate >= '" + sdfHQL.format(searchSupRoute.getFromDate()) + "'";
                }
                hql += " AND MCPDetails.mcps.beginDate < '" + sdfHQL.format(calendar.getTime()) + "'"
                        + " AND MCPDetails.mcps.deletedUser = 0"
                        + " AND MCPDetails.deletedUser = 0"
                        + " AND (CustomerCareInformation IS NULL OR CustomerCareInformation.deletedUser = 0)"
                        + " ORDER BY MCPDetails.mcps.beginDate";
                List posLists = new ArrayList();
                List<HashMap> list = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
                LinkedHashMap pos;
                for (HashMap mcpDetails : list) {
                    //POS APP
                    pos = new LinkedHashMap();
                    pos.put("id", ((POS) mcpDetails.get("pos")).getId());
                    pos.put("mcpDetailsId", mcpDetails.get("mcpDetailsId"));
                    pos.put("soDT", ((POS) mcpDetails.get("pos")).getTel());
                    //pos.put("posCode", ((POS) mcpDetails.get("pos")).getPosCode());
                    pos.put("name", ((POS) mcpDetails.get("pos")).getName());
                    pos.put("address", ((POS) mcpDetails.get("pos")).getAddress());
                    pos.put("lat", ((POS) mcpDetails.get("pos")).getLat());
                    pos.put("lng", ((POS) mcpDetails.get("pos")).getLng());
                    pos.put("lng", ((POS) mcpDetails.get("pos")).getLng());
                    pos.put("isVisited", mcpDetails.get("isVisited"));
                    pos.put("timeCared", mcpDetails.get("timeCared"));

                    //get timesale - tranType = 2
                    String hqlDelivery = "SELECT DATE_FORMAT(SalesOrder.salesTransDate,'%d/%m/%Y') as timeDelivery,"
                            + " CASE WHEN SalesTrans IS NULL THEN NULL ELSE DATE_FORMAT(SalesTrans.salesTransDate,'%d/%m/%Y %H:%i:%S') END AS timeSale"
                            + " FROM SalesTrans AS SalesTrans"
                            + " RIGHT JOIN SalesTrans.orders AS SalesOrder"
                            + " WHERE SalesOrder.deletedUser = 0"
                            + " AND SalesTrans.deletedUser = 0";
                    if (searchSupRoute.getFromDate() != null) {
                        hqlDelivery += " AND SalesTrans.salesTransDate >= '" + sdfHQL.format(searchSupRoute.getFromDate()) + "'";
                    }
                    hqlDelivery += " AND SalesTrans.salesTransDate < '" + sdfHQL.format(calendar.getTime()) + "'"
                            + " AND SalesOrder.pos.id = " + ((POS) mcpDetails.get("pos")).getId()
                            + " AND SalesTrans.transType = 2"
                            + " AND (SalesOrder.statuss.value != 3)" //value = 3 =>salesOder bi huy
                            + " ORDER BY SalesOrder.salesTransDate DESC";
                    List<HashMap> salesTransList = dataService.executeSelectHQL(HashMap.class, hqlDelivery, true, 0, 0);

                    if (!salesTransList.isEmpty()) //get timedelevery 
                    {
                        String timeSale = salesTransList.get(0).get("timeSale").toString();
                        String timeDelivery = salesTransList.get(0).get("timeDelivery").toString();
                        pos.put("timeSale", timeSale);
                        pos.put("timeDelivery", timeDelivery);
                    } else {
                        pos.put("timeSale", null);
                        pos.put("timeDelivery", null);
                    }
                    posLists.add(pos);
                }

                String hqlRoute = "SELECT UserRoute.lat AS lat,UserRoute.lng AS lng,"
                        + " DATE_FORMAT(UserRoute.routeAt,'%d/%m/%Y %H:%i:%S') as routeAt, UserRoute.note as note"
                        + " FROM UserRoute as UserRoute"
                        + " WHERE UserRoute.user.id = '" + user.getId() + "'";
                if (searchSupRoute.getFromDate() != null) {
                    hqlRoute += " AND UserRoute.routeAt >= '" + sdfHQL.format(searchSupRoute.getFromDate()) + "'";
                }
                hqlRoute += " AND UserRoute.routeAt < '" + sdfHQL.format(calendar.getTime()) + "'"
                        + " AND UserRoute.deletedUser = 0" + " ORDER BY UserRoute.createdAt DESC";
                List<HashMap> routeList = dataService.executeSelectHQL(HashMap.class, hqlRoute, true, 0, 0);

                item.put("posList", posLists);
                item.put("routes", routeList);

                result.add(item);
            }
        } catch (Exception e) {
        }

        return result;
    }

    @Override
    public List<CustomerCareImage> getListCustomerCareImage(int cusId) {
        List<CustomerCareImage> result = new ArrayList<>();
        try {
            String hql = "SELECT d.id as id, w.title as title, d.content as content "
                    + " FROM CustomerCareDetails d "
                    + " JOIN d.workflows as w "
                    + " JOIN d.customerCareInformations as cusInfo"
                    + " WHERE cusInfo.id=" + cusId
                    + " AND w.isImage=1 ";

            List<HashMap> list = (ArrayList<HashMap>) dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            for (HashMap map : list) {
                CustomerCareImage cusImage = new CustomerCareImage();
                cusImage.setCustomerCareDetailId(Integer.parseInt(map.get("id").toString()));
                cusImage.setWorkflowTitle(map.get("title").toString());
                cusImage.setImagePath(map.get("content").toString());
                result.add(cusImage);
            }
        } catch (Exception e) {
        }

        return result;
    }

    @Override
    public List<CustomerCareDetailWorkflow> getPosToolInfo(int cusId) {
        List<CustomerCareDetailWorkflow> result = new ArrayList<>();
        try {
            String hql = "SELECT d.id as id, w.title as title, d.content as content, dt.content as wfDetailContent"
                    + " FROM CustomerCareDetails d "
                    + " JOIN d.workflows as w "
                    + " JOIN d.customerCareInformations as cusInfo "
                    + " JOIN d.workflowDetailss as dt "
                    + " WHERE cusInfo.id=" + cusId
                    + " AND w.isImage=0 AND w.code='POS_TOOLS'";

            List<HashMap> list = (ArrayList<HashMap>) dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            for (HashMap map : list) {
                CustomerCareDetailWorkflow item = new CustomerCareDetailWorkflow();
                item.setToolName(map.get("wfDetailContent").toString());
                item.setToolCount(Integer.parseInt(map.get("content").toString()));
                item.setTitle(map.get("title").toString());
                result.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<CustomerCareDetailWorkflow> getPosUpdateInfo(int cusId) {
        List<CustomerCareDetailWorkflow> result = new ArrayList<>();
        try {
            String hql = "SELECT d.id as id, w.title as title, d.content as content, dt.content as wfDetailContent"
                    + " FROM CustomerCareDetails d "
                    + " JOIN d.workflows as w "
                    + " JOIN d.customerCareInformations as cusInfo "
                    + " JOIN d.workflowDetailss as dt "
                    + " WHERE cusInfo.id=" + cusId
                    + " AND w.isImage=0 AND w.code='POS_UPDATE'";

            List<HashMap> list = (ArrayList<HashMap>) dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            for (HashMap map : list) {
                CustomerCareDetailWorkflow item = new CustomerCareDetailWorkflow();
                item.setCompatitorName(map.get("wfDetailContent").toString());
                item.setCompatitorValue(map.get("content").toString());
                item.setTitle(map.get("title").toString());
                result.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<CustomerCareDetailWorkflow> getCompetitorList(int cusId) {
        List<CustomerCareDetailWorkflow> result = new ArrayList<>();
        try {
            String hql = "SELECT d.id as id, w.title as title, d.content as content"
                    + " FROM CustomerCareDetails d "
                    + " JOIN d.workflows as w "
                    + " JOIN d.customerCareInformations as cusInfo "
                    + " WHERE cusInfo.id=" + cusId
                    + " AND w.isImage=0 AND w.code='COMPETITOR_LIST'";

            List<HashMap> list = (ArrayList<HashMap>) dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            for (HashMap map : list) {
                CustomerCareDetailWorkflow item = new CustomerCareDetailWorkflow();
                item.setCompatitorName(map.get("content").toString());
                item.setTitle(map.get("title").toString());
                result.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<CustomerCareDetailWorkflow> getCompetitorToolList(int cusId) {
        List<CustomerCareDetailWorkflow> result = new ArrayList<>();
        try {
            String hql = "SELECT d.id as id, t.name as name, w.title as title, d.content as content, dt.content as wfDetailContent"
                    + " FROM CustomerCareDetails d "
                    + " JOIN d.workflows as w "
                    + " JOIN w.workflowTypes as t "
                    + " JOIN d.customerCareInformations as cusInfo "
                    + " JOIN d.workflowDetailss as dt "
                    + " WHERE cusInfo.id=" + cusId
                    + " AND w.isImage=0 AND w.code='COMPETITOR_TOOLS'";

            List<HashMap> list = (ArrayList<HashMap>) dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            for (HashMap map : list) {
                CustomerCareDetailWorkflow item = new CustomerCareDetailWorkflow();
                item.setToolName(map.get("wfDetailContent").toString());
                item.setToolCount(Integer.parseInt(map.get("content").toString()));
                item.setTitle(map.get("name").toString() + " (" + map.get("title").toString() + ")");
                result.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<CustomerCareDetailWorkflow> getCompetitorGoodList(int cusId) {
        List<CustomerCareDetailWorkflow> result = new ArrayList<>();
        try {
            String hql = "SELECT d.id as id, t.name as name, w.title as title, d.content as content, dt.content as wfDetailContent"
                    + " FROM CustomerCareDetails d "
                    + " JOIN d.workflows as w "
                    + " JOIN w.workflowTypes as t "
                    + " JOIN d.customerCareInformations as cusInfo "
                    + " JOIN d.workflowDetailss as dt "
                    + " WHERE cusInfo.id=" + cusId
                    + " AND w.isImage=0 AND w.code='COMPETITOR_GOODS'";

            List<HashMap> list = (ArrayList<HashMap>) dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            for (HashMap map : list) {
                CustomerCareDetailWorkflow item = new CustomerCareDetailWorkflow();
                item.setToolName(map.get("wfDetailContent").toString());
                item.setToolCount(Integer.parseInt(map.get("content").toString()));
                item.setTitle(map.get("name").toString() + " (" + map.get("title").toString() + ")");
                result.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<CustomerCareDetailWorkflow> getCompetitorPromotionList(int cusId) {
        List<CustomerCareDetailWorkflow> result = new ArrayList<>();
        try {
            String hql = "SELECT d.id as id, t.name as name, w.title as title, d.content as content, dt.content as wfDetailContent"
                    + " FROM CustomerCareDetails d "
                    + " JOIN d.workflows as w "
                    + " JOIN w.workflowTypes as t "
                    + " JOIN d.customerCareInformations as cusInfo "
                    + " JOIN d.workflowDetailss as dt "
                    + " WHERE cusInfo.id=" + cusId
                    + " AND w.isImage=0 AND w.code='COMPETITOR_PROMOTION'";

            List<HashMap> list = (ArrayList<HashMap>) dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            for (HashMap map : list) {
                CustomerCareDetailWorkflow item = new CustomerCareDetailWorkflow();
                item.setCompatitorName(map.get("wfDetailContent").toString());
                item.setCompatitorValue(map.get("content").toString());
                item.setTitle(map.get("name").toString() + " (" + map.get("title").toString() + ")");
                result.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<CustomerCareDetails> getListCustomerCareDetails(int infoId, DataService dataService) {
        String hql = "FROM CustomerCareDetails"
                + " WHERE deletedUser = 0"
                + " AND customerCareInformations.id = :infoId"
                + " AND workflows.workflowDetails IS NULL"
                //+ " AND workflows.code != :exceptCode"
                + " ORDER BY workflows.workflowTypes.order,workflows.order,workflowDetailss.id";
        List<MsalesParameter> parameters = MsalesParameter.createList("infoId", infoId);
        //parameters.add(MsalesParameter.create("exceptCode", "POS_STATUS"));
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<CustomerCareDetails> getListCustomerCareDetailsByWorkflowDetailsId(int infoId, int workflowDetailsId, DataService dataService) {
        String hql = "FROM CustomerCareDetails"
                + " WHERE deletedUser = 0"
                + " AND customerCareInformations.id = :infoId"
                + " AND workflows.workflowDetails.id = :workflowDetailsId"
                + " ORDER BY workflows.workflowTypes.order,workflows.order,workflowDetailss.id";
        List<MsalesParameter> parameters = MsalesParameter.createList("infoId", infoId);
        parameters.add(MsalesParameter.create("workflowDetailsId", workflowDetailsId));
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<CustomerCareDetails> getListCustomerCareDetailsUpdatePOS(int infoId, DataService dataService) {
        String hql = "FROM CustomerCareDetails"
                + " WHERE deletedUser = 0"
                + " AND customerCareInformations.id = :infoId"
                + " AND workflows.code = :code"
                + " ORDER BY workflowDetailss.id";
        List<MsalesParameter> parameters = MsalesParameter.createList("infoId", infoId);
        parameters.add(MsalesParameter.create("code","POS_UPDATE"));
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }
}
