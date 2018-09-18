/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.model;

/**
 *
 * @author cshiflett
 */
public class CustomerCareImage {
    private int customerCareDetailId;
    
    private String workflowTitle;
    
    private String imagePath;

    /**
     * @return the customerCareDetailId
     */
    public int getCustomerCareDetailId() {
        return customerCareDetailId;
    }

    /**
     * @param customerCareDetailId the customerCareDetailId to set
     */
    public void setCustomerCareDetailId(int customerCareDetailId) {
        this.customerCareDetailId = customerCareDetailId;
    }

    /**
     * @return the workflowTitle
     */
    public String getWorkflowTitle() {
        return workflowTitle;
    }

    /**
     * @param workflowTitle the workflowTitle to set
     */
    public void setWorkflowTitle(String workflowTitle) {
        this.workflowTitle = workflowTitle;
    }

    /**
     * @return the imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath the imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
}
