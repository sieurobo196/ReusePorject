package vn.itt.msales.entity;
// Generated Jun 11, 2015 10:37:14 AM by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.math.BigDecimal;
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
 * Company generated by hbm2java
 */
@Entity
@Table(name = "`company`"
)
@JsonFilter("JSONFILTER")
public class Company implements java.io.Serializable,Comparable<Company> {

    private static final long serialVersionUID = 2989911022417317915L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ID")
    @SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_COMPANY_ID")
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;

    @NotNull(message = MsalesValidator.COMPANY_CODE_NULL)
    @NotEmpty(message = MsalesValidator.COMPANY_CODE_EMPTY)
    @Column(name = "CODE", nullable = false, length = 100)
    private String code;

    @NotNull(message = MsalesValidator.COMPANY_NAME_NULL)
    @NotEmpty(message = MsalesValidator.COMPANY_NAME_EMPTY)
    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "ADDRESS", length = 1024)
    private String address;
    @Column(name = "CONTACT_PERSON_NAME", length = 100)
    private String contactPersonName;
    @Column(name = "TEL", length = 50)
    private String tel;
    @Column(name = "FAX", length = 50)
    private String fax;
    @Column(name = "EMAIL", length = 100)
    private String email;

    @NotNull(message = MsalesValidator.COMPANY_LAT_NULL)
    @Column(name = "LAT", nullable = false, precision = 24, scale = 15)
    private BigDecimal lat;

    @NotNull(message = MsalesValidator.COMPANY_LNG_NULL)
    @Column(name = "LNG", nullable = false, precision = 24, scale = 15)
    private BigDecimal lng;

    @Column(name = "LOGO_PATH", length = 1024)
    private String logoPath;
    @Column(name = "NOTE", length = 1024)
    private String note;

    @Column(name = "BG_COLOR", length = 1024)
    private String bgColor;

    @Column(name = "TEXT_COLOR", length = 1024)
    private String textColor;

    @Column(name = "BUTTON_BG_COLOR", length = 1024)
    private String buttonBgColor;

    @Column(name = "BUTTON_BG_COLOR_OVER", length = 1024)
    private String buttonBgColorOver;

    @Column(name = "TOP_BAR_BG_COLOR", length = 1024)
    private String topBarBGColor;

    @Column(name = "IS_SENDMAIL_ORDER_LIST", length = 1)
    private Boolean isSendmailOrderList;

    public Boolean getIsSendmailOrderList() {
        return isSendmailOrderList;
    }

    public void setIsSendmailOrderList(Boolean isSendmailOrderList) {
        this.isSendmailOrderList = isSendmailOrderList;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getButtonBgColor() {
        return buttonBgColor;
    }

    public void setButtonBgColor(String buttonBgColor) {
        this.buttonBgColor = buttonBgColor;
    }

    public String getButtonBgColorOver() {
        return buttonBgColorOver;
    }

    public void setButtonBgColorOver(String buttonBgColorOver) {
        this.buttonBgColorOver = buttonBgColorOver;
    }

    public String getTopBarBGColor() {
        return topBarBGColor;
    }

    public void setTopBarBGColor(String topBarBGColor) {
        this.topBarBGColor = topBarBGColor;
    }

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
    private Integer locationId;

    @Transient
    private Integer statusId;

    @NotNull(message = MsalesValidator.COMPANY_LOCATION_ID_NULL)
    @JsonIgnoreProperties(value = {"locationType", "parents", "code", "note", "lat", "lng"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LOCATION_ID", nullable = false)
    private Location locations;

    public Location getLocations() {
        return locations;
    }

    public void setLocations(Location locations) {
        this.locations = locations;
    }

    @NotNull(message = MsalesValidator.COMPANY_STATUS_ID_NULL)
    @JsonIgnoreProperties(value = {"statusTypes", "value"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STATUS_ID", nullable = false)
    private Status statuss;

    public Status getStatuss() {
        return statuss;
    }

    public void setStatuss(Status statuss) {
        this.statuss = statuss;
    }

    @Transient
    private String username;
    @Transient
    private String password;
    @Transient
    private String lastName;
    @Transient
    private String firstName;
    @Transient
    private String position;
    @Transient
    private Integer branch;
    
    @Column(name = "EMPLOYEE_AMOUNT")
    private Integer employeeAmount = 0;
    
    @Column(name = "EMPLOYEE_SALE_AMOUNT")
    private Integer employeeSaleAmount = 0;
    
    @Column(name = "EXPIRE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireTime;
        
    @Column(name = "IS_TEMPLATE", nullable = false)
    private boolean isTemplate;
    
    @Column(name = "IS_REGISTER", nullable = false)
    private Integer isRegister = 0;

    public Integer getIsRegister() {
        return isRegister;
    }

    public void setIsRegister(Integer isRegister) {
        this.isRegister = isRegister;
    }
    
    public boolean isIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public Integer getEmployeeAmount() {
        return employeeAmount;
    }

    public void setEmployeeAmount(Integer employeeAmount) {
        this.employeeAmount = employeeAmount;
    }

    public Integer getEmployeeSaleAmount() {
        return employeeSaleAmount;
    }

    public void setEmployeeSaleAmount(Integer employeeSaleAmount) {
        this.employeeSaleAmount = employeeSaleAmount;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
    
    @Column(name = "PACKAGE_SERVICE")
    private Integer packageService;
    
    @Column(name = "EQUIPMENT_MAX")
    private Integer equipmentMax;

    public Integer getPackageService() {
        return packageService;
    }

    public void setPackageService(Integer packageService) {
        this.packageService = packageService;
    }

    public Integer getEquipmentMax() {
        return equipmentMax;
    }

    public void setEquipmentMax(Integer equipmentMax) {
        this.equipmentMax = equipmentMax;
    }
    
    public Company() {
    }

    public Company(String code, String name, Integer locationId, Integer statusId, BigDecimal lat, BigDecimal lng, Date createdAt, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.code = code;
        this.name = name;
        this.locationId = locationId;
        this.statusId = statusId;
        this.lat = lat;
        this.lng = lng;
        this.createdAt = createdAt;
        this.createdUser = createdUser;
        this.updatedAt = updatedAt;
        this.updatedUser = updatedUser;
        this.deletedAt = deletedAt;
        this.deletedUser = deletedUser;
    }

    public Company(String code, String name, Integer locationId, Integer statusId, String address, String contactPersonName, String tel, String fax, String email, BigDecimal lat, BigDecimal lng, String logoPath, String note, Date createdAt, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.code = code;
        this.name = name;
        this.locationId = locationId;
        this.statusId = statusId;
        this.address = address;
        this.contactPersonName = contactPersonName;
        this.tel = tel;
        this.fax = fax;
        this.email = email;
        this.lat = lat;
        this.lng = lng;
        this.logoPath = logoPath;
        this.note = note;
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

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public Integer getLocationId() {
        return this.locationId;
    }

    @JsonSetter
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
        if (locationId != null) {
            Location location = new Location();
            location.setId(this.locationId);
            this.locations = location;
        }

    }

    @JsonIgnore
    public Integer getStatusId() {
        return this.statusId;
    }

    @JsonSetter
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
        if (statusId != null) {
            Status status = new Status();
            status.setId(this.statusId);
            this.statuss = status;
        }
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPersonName() {
        return this.contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getTel() {
        return this.tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getLat() {
        return this.lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return this.lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public String getLogoPath() {
        return this.logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
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

    @Override
    public int compareTo(Company o) {
        return o.createdAt.compareTo(this.createdAt);
    }

}