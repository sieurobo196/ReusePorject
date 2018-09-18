package vn.itt.msales.entity;
// Generated Jul 3, 2015 4:54:37 PM by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import java.util.List;
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
 * CompanyConfig generated by hbm2java
 */
@Entity
@Table(name = "`company_config`"
)
@JsonFilter("JSONFILTER")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CompanyConfig implements java.io.Serializable {

    private static final long serialVersionUID = -7828441337249226633L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ID")
    @SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_COMPANYCONFIG_ID")
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
    
    @Transient
    private Integer userRoleId;

    @NotNull(message = MsalesValidator.COMPANY_CONFIG_USER_ROLE_ID_NULL)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ROLE_ID", nullable = false)
    @JsonIgnoreProperties(value = {"note", "parents"})
    private UserRole userRoles;

    public UserRole getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(UserRole userRoles) {
        this.userRoles = userRoles;
    }

    @NotNull(message = MsalesValidator.COMPANY_CONFIG_NAME_NULL)
    @NotEmpty(message = MsalesValidator.COMPANY_CONFIG_NAME_EMPTY)
    @Column(name = "NAME", nullable = false, length = 100)
    private String name;
    @NotNull(message = MsalesValidator.COMPANY_CONFIG_IS_ACTIVE_NULL)
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
    private Integer companyId;

    @NotNull(message = MsalesValidator.COMPANY_CONFIG_COMPANY_ID_NULL)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    @JsonIgnoreProperties(value = {"locations", "statuss", "code", "address", "contactPersonName", "tel", "fax",
        "email", "lat", "lng", "logoPath", "note"})
    private Company companys;

    public Company getCompanys() {
        return companys;
    }

    public void setCompanys(Company companys) {
        this.companys = companys;
    }

        
    @Transient
    private List<CompanyConfigDetails> companyConfigDetails;
    
    @JsonGetter
    @JsonIgnoreProperties(value = "companyConfigs")
    public List<CompanyConfigDetails> getCompanyConfigDetails() {
		return companyConfigDetails;
	}

    @JsonSetter
	public void setCompanyConfigDetails(
			List<CompanyConfigDetails> companyConfigDetails) {
		this.companyConfigDetails = companyConfigDetails;
	}

	public CompanyConfig() {
    }

    public CompanyConfig(Integer companyId, String name, Integer isActive, Date createdAt, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.companyId = companyId;
        this.name = name;
        this.isActive = isActive;
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

    @JsonIgnore
    public Integer getCompanyId() {
        return this.companyId;
    }

    @JsonSetter
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
        if (companyId != null) {
            Company company = new Company();
            company.setId(this.companyId);
            this.companys = company;
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsActive() {
        return this.isActive;
    }

    public void setIsActive(Integer isActive) {
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

    @JsonSetter
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

    @JsonSetter
    public void setDeletedUser(Integer deletedUser) {
        this.deletedUser = deletedUser;
    }

    @JsonIgnore
    public Integer getUserRoleId() {
        return userRoleId;
    }

    @JsonSetter
    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
        if (userRoleId != null) {
            UserRole userRole = new UserRole();
            userRole.setId(this.userRoleId);
            this.userRoles = userRole;
        }
    }

}
