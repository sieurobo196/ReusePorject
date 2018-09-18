package vn.itt.msales.entity;
// Generated Jun 30, 2015 4:09:30 PM by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * Equipment generated by hbm2java
 */
@Entity
@Table(name = "`equipment`"
)
public class Equipment implements java.io.Serializable, Comparable<Equipment> {

    private static final long serialVersionUID = -4279281852915216976L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ID")
    @SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_EQUIPMENT_ID")
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;

    @NotNull(message = MsalesValidator.EQUIPMENT_VERSION_NULL)
    @NotEmpty(message = MsalesValidator.EQUIPMENT_VERSION_EMPTY)
    @Column(name = "VERSION", nullable = false, length = 20)
    private String version;

    @NotNull(message = MsalesValidator.EQUIPMENT_IMEI_NULL)
    @NotEmpty(message = MsalesValidator.EQUIPMENT_IMEI_EMPTY)
    @Column(name = "IMEI", nullable = false, length = 20)
    private String imei;

    @NotNull(message = MsalesValidator.EQUIPMENT_SUBCRIBER_ID_NULL)
    @NotEmpty(message = MsalesValidator.EQUIPMENT_SUBCRIBER_ID_EMPTY)
    @Column(name = "SUBSCRIBER_ID", nullable = false, length = 20)
    private String subscriberId;

    @Column(name = "TEL_NO", nullable = true, length = 15)
    private String telNo;
    @Column(name = "NAME", nullable = true, length = 256)
    private String name;

    @NotNull(message = MsalesValidator.EQUIPMENT_IS_ACTIVE_NULL)
    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean isActive;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "GMT+7")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = MsalesValidator.EQUIPMENT_ACTIVE_DATE_NULL)
    @Column(name = "ACTIVE_DATE", nullable = false, length = 19)
    private Date activeDate;

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
    @Transient
    private Integer userId;

    @NotNull(message = MsalesValidator.EQUIPMENT_COMPANY_ID_NULL)
    @JsonIgnoreProperties(value = {"code", "address", "contactPersonName", "tel", "fax", "email", "lat",
        "lng", "logoPath", "note", "locations", "statuss"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private Company companys;

    public Company getCompanys() {
        return companys;
    }

    public void setCompanys(Company companys) {
        this.companys = companys;
    }

    @JsonIgnoreProperties(value = {"username", "userCode", "birthday", "sex", "email", "yahooId", "skypeId", "isdn",
        "tel", "address", "note", "isActive", "activeCode", "employerType", "useEvoucher", "ipLastLogin", "statusId",
        "updatedAt", "updatedUser", "imployerUser", "monitoringUser", "monitoringUsers", "employerUsers", "createdUser", "comanys"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = true)
    private User users;

    public User getUsers() {
        return users;
    }

    public void setUsers(User users) {
        this.users = users;
    }

    @Transient
    private Integer branch;

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public Equipment() {
    }

    public Equipment(Integer companyId, Integer userId, String imei, String subcriberId, String telNo, String name, boolean isActive, Date activeDate, Date createdAt, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.companyId = companyId;
        this.userId = userId;
        this.imei = imei;
        this.subscriberId = subcriberId;
        this.telNo = telNo;
        this.name = name;
        this.isActive = isActive;
        this.activeDate = activeDate;
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

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    @JsonIgnore
    public Integer getUserId() {
        return this.userId;
    }

    @JsonSetter
    public void setUserId(Integer userId) {
        this.userId = userId;
        if (userId != null) {
            User user = new User();
            user.setId(this.userId);
            this.users = user;
        }
    }

    public String getImei() {
        return this.imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSubscriberId() {
        return this.subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getTelNo() {
        return this.telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Date getActiveDate() {
        return this.activeDate;
    }

    public void setActiveDate(Date activeDate) {
        this.activeDate = activeDate;
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
    @Transient
    private UserRole userRoles;

    public UserRole getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(UserRole userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public int compareTo(Equipment o) {
        if (o.createdAt != null && this.createdAt != null) {
            return o.createdAt.compareTo(this.createdAt);
        } else {
            return -1;
        }
    }
}