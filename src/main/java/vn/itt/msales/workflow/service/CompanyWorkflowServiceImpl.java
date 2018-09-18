package vn.itt.msales.workflow.service;

import java.util.ArrayList;
import java.util.List;
import vn.itt.msales.entity.CompanyConfig;
import vn.itt.msales.entity.CompanyConfigDetails;
import vn.itt.msales.entity.Workflow;
import vn.itt.msales.entity.WorkflowDetails;
import vn.itt.msales.entity.WorkflowType;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.services.DataService;

/**
 *
 * @author ChinhNQ
 */
public class CompanyWorkflowServiceImpl implements CompanyWorkflowService {   
    @Override
    public List<Workflow> getListWorkflow(int workflowType, int companyId,DataService dataService){
        String hql = "FROM Workflow"
                + " WHERE deletedUser = 0"
                + " AND workflowTypes.type= :workflowType" 
                + " AND companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("workflowType", workflowType));
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }
    
    @Override
    public List<WorkflowType> getListWorkflowType(DataService dataService){
        String hql = "FROM WorkflowType"
                + " WHERE deletedUser = 0";
        return dataService.executeSelectHQL(hql, new ArrayList(), false, 0, 0);
    }
    
    @Override
    public WorkflowType getWorkflowTypeByCode(String code,DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("code", code);
        //parameterList.add("companys.id", companyId);
        List<WorkflowType> list = dataService.getListOption(WorkflowType.class, parameterList);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Workflow getWorkflowByCode(String code, int companyId,DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("code", code);
        parameterList.add("companys.id", companyId);
        List<Workflow> list = dataService.getListOption(Workflow.class, parameterList);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean deleteAllWorkflowDetails(int workflowId, int userId,DataService dataService) {
        try {
            ParameterList parameterList = new ParameterList(0, 0);
            parameterList.add("workflows.id", workflowId);
            List<WorkflowDetails> list = dataService.getListOption(WorkflowDetails.class, parameterList);
            for (WorkflowDetails workflowDetails : list) {
                workflowDetails.setDeletedUser(userId);
            }
            dataService.updateArray(list);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean deleteAllWorkflowOther(int companyId,List<Integer> ids, int type, int userId,DataService dataService) {
        try {
            String hql = "UPDATE Workflow "
                    + " SET deletedUser = :userId"
                    + " WHERE id NOT IN (:ids)"                    
                    + " AND companys.id = :companyId"
                    + " AND workflowTypes.id IN (SELECT id FROM WorkflowType WHERE type = :type)";
            List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
            parameters.add(MsalesParameter.create("type", type));
            parameters.add(MsalesParameter.create("companyId", companyId));
            if (ids == null || ids.isEmpty()) {
                parameters.add(MsalesParameter.createBadInteger("ids"));
            } else {
                parameters.add(MsalesParameter.create("ids", ids, 1));
            }
            return dataService.executeHQL(hql, parameters) > -1;
        } catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    public List<CompanyConfig> getListCompanyConfig(int companyId,DataService dataService)
    {
        String hql = "FROM CompanyConfig"
                + " WHERE deletedUser = 0"
                + " AND companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId",companyId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }
    
    @Override
    public List<CompanyConfigDetails> getListCompanyConfigDetails(int companyId,DataService dataService)
    {
        String hql = "FROM CompanyConfigDetails"
                + " WHERE deletedUser = 0"
                + " AND companyConfigs.companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId",companyId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }
    
    @Override
    public List<CompanyConfigDetails> getListCompanyConfigDetailsByListConfig(List<CompanyConfig> companyConfigList,DataService dataService)
    {
        if(companyConfigList==null || companyConfigList.isEmpty()){
           return new ArrayList<>();
        }
        else{
            List<Integer> ids = new ArrayList<>();
            for(CompanyConfig companyConfig : companyConfigList){
                ids.add(companyConfig.getId());
            }
            List<MsalesParameter> parameters = MsalesParameter.createList("ids", ids,1);
            String hql = "FROM CompanyConfigDetails"
                + " WHERE deletedUser = 0"
                + " AND companyConfigs.id IN (:ids)";
            return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        }
    }

    @Override
    public Workflow getCompetitorWorkflow(int companyId, DataService dataService) {
        String hql = "FROM Workflow"
                + " WHERE deletedUser = 0"
                + " AND companys.id = :companyId"
                + " AND code = :code";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId",companyId);
        parameters.add(MsalesParameter.create("code", "COMPETITOR_LIST"));
        List<Workflow> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        if(list.isEmpty())
        {
            return null;
        }
        else{
            return list.get(0);
        }
    }

    @Override
    public List<WorkflowDetails> getCompetitorWorkflowDetailsList(Workflow competitorWorkflow, DataService dataService) {
        String hql = "FROM WorkflowDetails"
                + " WHERE deletedUser = 0"
                + " AND workflows.id = :id";
        List<MsalesParameter> parameters = MsalesParameter.createList("id",competitorWorkflow.getId());
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<Workflow> getCompetitorWorkflowList(List<WorkflowDetails> competitorWorkflowDetailsList, DataService dataService) {
        String hql = "FROM Workflow"
                + " WHERE deletedUser = 0"
                + " AND workflowDetails.id IN (:ids)";
        List<MsalesParameter> parameters = new ArrayList<>();
        if(competitorWorkflowDetailsList == null ||competitorWorkflowDetailsList.isEmpty()){
            parameters.add(MsalesParameter.createBadInteger("ids"));
        }
        else{
            List<Integer> ids = new ArrayList<>();
            for(WorkflowDetails workflowDetails:competitorWorkflowDetailsList){
                ids.add(workflowDetails.getId());
            }
            parameters.add(MsalesParameter.create("ids", ids,1));
        }
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<WorkflowDetails> getCompetitorDetailsList(List<Workflow> competitorWorkflowList, DataService dataService) {
        String hql = "FROM WorkflowDetails"
                + " WHERE deletedUser = 0"
                + " AND workflows.id IN (:ids)";
        List<MsalesParameter> parameters = new ArrayList<>();
        if(competitorWorkflowList == null ||competitorWorkflowList.isEmpty()){
            parameters.add(MsalesParameter.createBadInteger("ids"));
        }
        else{
            List<Integer> ids = new ArrayList<>();
            for(Workflow workflow:competitorWorkflowList){
                ids.add(workflow.getId());
            }
            parameters.add(MsalesParameter.create("ids", ids,1));
        }
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }
    
    @Override
    public boolean checkImportWorkflowByPackage(String code, Integer packageService) {
        if(packageService==null){
            return true;
        }
        boolean flag  //GUI
              = code.equalsIgnoreCase("mcp_page")
                || code.equalsIgnoreCase("plan_page")
                || code.equalsIgnoreCase("point_of_sale_page")
                || code.equalsIgnoreCase("history_page")
                || code.equalsIgnoreCase("monitor_order_page")
                || code.equalsIgnoreCase("monitor_route_page")
                || code.equalsIgnoreCase("monitor_store_page")
                || code.equalsIgnoreCase("order_page")
                || code.equalsIgnoreCase("delivery_page")
                || code.equalsIgnoreCase("point_of_sale_page")
                //Sales
                || code.equalsIgnoreCase("NEW_POS")
                || code.equalsIgnoreCase("TARGET");
        if (flag) {
            return packageService >= 1;
        } else {
            flag = //GUI                    
                    code.equalsIgnoreCase("store_page")
                    || code.equalsIgnoreCase("sales_page")
                    || code.equalsIgnoreCase("monitor_sales_page")
                    || code.equalsIgnoreCase("sup_sales_page")
                    || code.equalsIgnoreCase("care_page")
                    || code.equalsIgnoreCase("direct_sales_page")
                    || code.equalsIgnoreCase("recover_page")
                    || code.equalsIgnoreCase("sales_via_phone_page")
                    || code.equalsIgnoreCase("withdraw_page")
                    //Sales
                    || code.equalsIgnoreCase("RECEIVE_GOODS")
                    || code.equalsIgnoreCase("SELL_GOODS")
                    //Care
                    || code.equalsIgnoreCase("TAKE_PICTURE_1")
                    || code.equalsIgnoreCase("TAKE_PICTURE_2")
                    || code.equalsIgnoreCase("TAKE_PICTURE_3")
                    || code.equalsIgnoreCase("POS_STATUS")
                    || code.equalsIgnoreCase("POS_UPDATE")
                    || code.equalsIgnoreCase("POS_TOOLS")
                    || code.equalsIgnoreCase("COMPETITOR_LIST")
                    //Competitor
                    || code.equalsIgnoreCase("COMPETITOR_TOOLS")
                    || code.equalsIgnoreCase("COMPETITOR_GOODS")
                    || code.equalsIgnoreCase("COMPETITOR_PROMOTION")
                    || code.equalsIgnoreCase("COMPETITOR_GOODS");
            if (flag) {
                return packageService >= 2;
            } else {
                flag = //GUI                    
                        code.equalsIgnoreCase("monitor_evaluation_page")
                        || code.equalsIgnoreCase("list_of_promotion")
                        || code.equalsIgnoreCase("register_promotion")
                        || code.equalsIgnoreCase("delievery_promotion")
                        || code.equalsIgnoreCase("list_of_report");
                if (flag) {
                    return packageService == 3;
                } else {
                    return false;
                }
            }
        }
    }
}
