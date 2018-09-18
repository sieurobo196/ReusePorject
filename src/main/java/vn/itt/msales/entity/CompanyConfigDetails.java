package vn.itt.msales.entity;
// Generated Jul 11, 2015 9:18:39 AM by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import vn.itt.msales.common.json.validator.MsalesValidator;

/**
 * CompanyConfigDetails generated by hbm2java
 */
@Entity
@Table(name = "`company_config_details`"
)
public class CompanyConfigDetails implements java.io.Serializable {

    private static final long serialVersionUID = -4771244299848101842L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ID")
    @SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_COMPANYCONFIGDETAILS_ID")
    private Integer id;
    
    @Transient
    private Integer userRoleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = {"note", "parents"})
    @JoinColumn(name = "USER_ROLE_ID")
    private UserRole userRoles;

    public UserRole getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(UserRole userRoles) {
        this.userRoles = userRoles;
    }

    @Column(name = "CODE", length = 256,nullable = false)
    @NotNull(message = MsalesValidator.COMPANY_CONFIG_DETAILS_CODE_NULL)
    @NotEmpty(message = MsalesValidator.COMPANY_CONFIG_DETAILS_CODE_EMPTY)
    private String code;
    
    @Column(name = "CONTENT", length = 256)
    private String content;

    @NotNull(message = MsalesValidator.COMPANY_CONFIG_DETAILS_ORDER_ID_NULL)
    @Column(name = "`ORDER`", nullable = false)
    private Integer order;

    @NotNull(message = MsalesValidator.COMPANY_CONFIG_DETAILS_ISACTIVE_NULL)
    @Column(name = "IS_ACTIVE", nullable = false)
    private Integer isActive;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, length = 19)
    @JsonIgnore
    private Date createdAt;

    @Column(name = "CREATED_USER", nullable = false)
    @NotNull(message = MsalesValidator.CREATED_USER_NULL)
    private Integer createdUser;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT", length = 19)
    @JsonIgnore
    private Date updatedAt;

    @Column(name = "UPDATED_USER", nullable = false)
    @NotNull(message = MsalesValidator.UPDATED_USER_NULL)
    private Integer updatedUser;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DELETED_AT", length = 19)
    @JsonIgnore
    private Date deletedAt;

    @Column(name = "DELETED_USER", nullable = false)
    @NotNull(message = MsalesValidator.DELETED_USER_NULL)
    private Integer deletedUser;

    @Transient
    private Integer companyConfigId;

    
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull(message = MsalesValidator.COMPANY_CONFIG_DETAILS_COMPANY_CONFIG_ID_NULL)
    @JoinColumn(name = "COMPANY_CONFIG_ID", nullable = false)
    @JsonIgnoreProperties(value = {"order", "isActive","companys","userRoles"})
    private CompanyConfig companyConfigs;
        
    public CompanyConfig getCompanyConfigs() {
        return companyConfigs;
    }

    public void setCompanyConfigs(CompanyConfig companyConfigs) {
        this.companyConfigs = companyConfigs;
    }

    public CompanyConfigDetails() {
    }

    public CompanyConfigDetails(Integer companyConfigId, String code, Integer userRoleId, Integer order, Integer isActive, Date createdAt, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.companyConfigId = companyConfigId;
        this.userRoleId = userRoleId;
        this.order = order;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.createdUser = createdUser;
        this.updatedAt = updatedAt;
        this.updatedUser = updatedUser;
        this.deletedAt = deletedAt;
        this.deletedUser = deletedUser;
        this.code = code;
    }

    public CompanyConfigDetails(Integer  companyConfigId, String code, Integer userRoleId, String content, Integer order, Integer isActive, Date createdAt, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.companyConfigId = companyConfigId;
        this.userRoleId = userRoleId;
        this.content = content;
        this.order = order;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.createdUser = createdUser;
        this.updatedAt = updatedAt;
        this.updatedUser = updatedUser;
        this.deletedAt = deletedAt;
        this.deletedUser = deletedUser;
        this.code = code;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public Integer getCompanyConfigId() {
        return this.companyConfigId;
    }

    @JsonSetter
    public void setCompanyConfigId(Integer  companyConfigId) {        
        this.companyConfigId = companyConfigId;
        if(companyConfigId!=null)
        {
            CompanyConfig companyConfig = new CompanyConfig();
            companyConfig.setId(this.companyConfigId);
            this.companyConfigs = companyConfig;
        }
    }

    @JsonIgnore
    public Integer getUserRoleId() {
        return this.userRoleId;
    }
    
    @JsonSetter
    public void setUserRoleId(Integer  userRoleId) {
        this.userRoleId = userRoleId;
        if(userRoleId!=null)
        {
            UserRole userRole = new UserRole();
            userRole.setId((this.getUserRoleId()));
            this.userRoles = userRole;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    
    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer  order) {
        this.order = order;
    }

    public Integer getIsActive() {
        return this.isActive;
    }

    public void setIsActive(Integer  isActive) {
        this.isActive = isActive;
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

    @JsonSetter
    public void setCreatedUser(Integer  createdUser) {
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

    @JsonSetter
    public void setUpdatedUser(Integer  updatedUser) {
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

    @JsonSetter
    public void setDeletedUser(Integer  deletedUser) {
        this.deletedUser = deletedUser;
    }

}