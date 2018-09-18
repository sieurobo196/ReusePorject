package vn.itt.msales.workflow.service;

import java.util.List;
import vn.itt.msales.entity.CompanyConfig;
import vn.itt.msales.entity.CompanyConfigDetails;
import vn.itt.msales.entity.Workflow;
import vn.itt.msales.entity.WorkflowDetails;
import vn.itt.msales.entity.WorkflowType;
import vn.itt.msales.services.DataService;

/**
 *
 * @author ChinhNQ
 */
public interface CompanyWorkflowService {    
    public List<Workflow> getListWorkflow(int workflowType, int companyId,DataService dataService);

    public List<WorkflowType> getListWorkflowType(DataService dataService);
    
    public WorkflowType getWorkflowTypeByCode(String code,DataService dataService);

    public Workflow getWorkflowByCode(String code, int companyId,DataService dataService);

    public boolean deleteAllWorkflowDetails(int workflowId,int userId,DataService dataService);
    
    public boolean deleteAllWorkflowOther(int companyId,List<Integer> ids,int type,int userId,DataService dataService);
    
    public List<CompanyConfig> getListCompanyConfig(int companyId,DataService dataService);
    
    public List<CompanyConfigDetails> getListCompanyConfigDetails(int companyId,DataService dataService);
    
    public List<CompanyConfigDetails> getListCompanyConfigDetailsByListConfig(List<CompanyConfig> companyConfigList,DataService dataService);
    
    public Workflow getCompetitorWorkflow(int companyId,DataService dataService);
    public List<WorkflowDetails> getCompetitorWorkflowDetailsList(Workflow competitorWorkflow,DataService dataService);
    public List<Workflow> getCompetitorWorkflowList(List<WorkflowDetails> competitorWorkflowDetailsList,DataService dataService);
    public List<WorkflowDetails> getCompetitorDetailsList(List<Workflow> competitorWorkflowList,DataService dataService);
    
    public boolean checkImportWorkflowByPackage(String code, Integer packageService);
}
