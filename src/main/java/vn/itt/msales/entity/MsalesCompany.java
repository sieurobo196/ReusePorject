package vn.itt.msales.entity;
// Generated Jul 20, 2015 9:39:17 AM by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import vn.itt.msales.common.json.validator.MsalesValidator;

/**
 * MsalesCompany generated by hbm2java
 */
@Entity
@Table(name = "msales_company"
)
@JsonFilter("JSONFILTER")
public class MsalesCompany implements java.io.Serializable {

    private static final long serialVersionUID = 8054080619797673714L;
    
    @Id
    @Column(name = "ID", unique = true, nullable = false)
    private int id;
    @Column(name = "COMPANY_NAME", nullable = false, length = 100)
    private String companyName;
    @Column(name = "BRANCH", nullable = false)
    private int branch;
    @Column(name = "CODE", nullable = false, length = 20)
    private String code;
    @Column(name = "IS_ACTIVE", nullable = false)
    private int isActive;

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
    
    @Column(name = "MASTER_ADMIN")
    private Boolean masterAdmin;
    
    @Column(name = "USER_NAME")
    private String username;
    
    @Column(name = "PASSWORD")
    private String password;

    public MsalesCompany() {
    }

    public MsalesCompany(int id, String companyName, int branch, String code, int isActive) {
        this.id = id;
        this.companyName = companyName;
        this.branch = branch;
        this.code = code;
        this.isActive = isActive;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getBranch() {
        return this.branch;
    }

    public void setBranch(int branch) {
        this.branch = branch;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getIsActive() {
        return this.isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(Integer createdUser) {
        this.createdUser = createdUser;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(Integer updatedUser) {
        this.updatedUser = updatedUser;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getDeletedUser() {
        return deletedUser;
    }

    public void setDeletedUser(Integer deletedUser) {
        this.deletedUser = deletedUser;
    }

    public Boolean getMasterAdmin() {
        return masterAdmin;
    }

    public void setMasterAdmin(Boolean masterAdmin) {
        this.masterAdmin = masterAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
}
