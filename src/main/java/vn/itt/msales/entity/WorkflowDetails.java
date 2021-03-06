package vn.itt.msales.entity;
// Generated Sep 11, 2015 2:56:59 PM by Hibernate Tools 4.3.1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

/**
 * WorkflowDetails generated by hbm2java
 */
@Entity
@Table(name = "workflow_details")
@JsonFilter("JSONFILTER")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class WorkflowDetails implements java.io.Serializable {

    private static final long serialVersionUID = 3782611107611565747L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ID")
    @SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_WORKFLOWDETAILS_ID")
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;

    @Transient
    private Integer workflowId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "WORKFLOW_ID", nullable = false)
    private Workflow workflows;

    public Workflow getWorkflows() {
        return workflows;
    }

    public void setWorkflows(Workflow workflows) {
        this.workflows = workflows;
    }

    @Column(name = "ACTION_TYPE", nullable = false)
    private Integer actionType;
    @Column(name = "CONTENT", nullable = false, length = 128)
    private String content;
    @Column(name = "CODE", length = 128)
    private String code;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, length = 19)
    private Date createdAt;
    @Column(name = "CREATED_USER", nullable = false)
    private Integer createdUser;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT", length = 19)
    private Date updatedAt;
    @Column(name = "UPDATED_USER", nullable = false)
    private Integer updatedUser;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DELETED_AT", length = 19)
    private Date deletedAt;
    @Column(name = "DELETED_USER", nullable = false)
    private Integer deletedUser;
    
    @JsonIgnoreProperties({"workflowDetails","actionType"})
    @Transient
    private List<Workflow> details;

    public List<Workflow> getDetails() {
        return details;
    }

    public void setDetails(List<Workflow> details) {
        this.details = details;
    }

    public WorkflowDetails() {
    }

    public WorkflowDetails(Integer id, Integer workflowId, Integer actionType, String content, Date createdAt, Integer createdUser, Integer updatedUser, Integer deletedUser) {
        this.id = id;
        this.workflowId = workflowId;
        this.actionType = actionType;
        this.content = content;
        this.createdAt = createdAt;
        this.createdUser = createdUser;
        this.updatedUser = updatedUser;
        this.deletedUser = deletedUser;
    }

    public WorkflowDetails(Integer id, Integer workflowId, Integer actionType, String content, String note, Date createdAt, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.id = id;
        this.workflowId = workflowId;
        this.actionType = actionType;
        this.content = content;
        this.code = note;
        this.createdAt = createdAt;
        this.createdUser = createdUser;
        this.updatedAt = updatedAt;
        this.updatedUser = updatedUser;
        this.deletedAt = deletedAt;
        this.deletedUser = deletedUser;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWorkflowId() {
        return this.workflowId;
    }

    public void setWorkflowId(Integer workflowId) {
        this.workflowId = workflowId;
        if (workflowId != null) {
            Workflow workflow = new Workflow();
            workflow.setId(workflowId);
            this.workflows = workflow;
        }
    }

    public Integer getActionType() {
        return this.actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    
    public Date getCreatedAt() {
        return this.createdAt;
    }

    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonIgnore
    public Integer getCreatedUser() {
        return this.createdUser;
    }

    public void setCreatedUser(Integer createdUser) {
        this.createdUser = createdUser;
    }

    
    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonIgnore
    public Integer getUpdatedUser() {
        return this.updatedUser;
    }

    public void setUpdatedUser(Integer updatedUser) {
        this.updatedUser = updatedUser;
    }

    
    public Date getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    @JsonIgnore
    public Integer getDeletedUser() {
        return this.deletedUser;
    }

    public void setDeletedUser(Integer deletedUser) {
        this.deletedUser = deletedUser;
    }

}
