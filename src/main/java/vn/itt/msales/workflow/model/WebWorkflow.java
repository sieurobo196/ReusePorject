/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.workflow.model;

import java.util.LinkedHashMap;
import java.util.List;
import vn.itt.msales.entity.Workflow;

/**
 *
 * @author vtm_2
 */
public class WebWorkflow {

    private String workFlowTypeCode;
    private String workFlowTypeName;
    private List<Workflow> workFlow;

    public WebWorkflow() {
    }

    public String getWorkFlowTypeCode() {
        return workFlowTypeCode;
    }

    public void setWorkFlowTypeCode(String workFlowTypeCode) {
        this.workFlowTypeCode = workFlowTypeCode;
    }

    public String getWorkFlowTypeName() {
        return workFlowTypeName;
    }

    public void setWorkFlowTypeName(String workFlowTypeName) {
        this.workFlowTypeName = workFlowTypeName;
    }

    public List<Workflow> getWorkFlow() {
        return workFlow;
    }

    public void setWorkFlow(List<Workflow> workFlow) {
        this.workFlow = workFlow;
    }

   

}
