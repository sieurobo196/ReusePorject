package vn.itt.msales.entity;
// Generated Jun 11, 2015 10:37:14 AM by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonFilter;
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
 * GoodsCategory generated by hbm2java
 */
@Entity
@Table(name = "`goods_category`"
)
@JsonFilter("JSONFILTER")
@JsonIgnoreProperties(value = {"companys"})
public class GoodsCategory implements java.io.Serializable {

    private static final long serialVersionUID = 2934568448188767585L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ID")
    @SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_GOODSCATEGORY_ID")
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
    @Column(name = "NAME", nullable = false, length = 200)
    @NotNull(message = "Name không được Null")
    @NotEmpty(message = "Name không được rỗng")
    private String name;
    @Column(name = "GOODS_CODE", nullable = false, length = 100)
    @NotNull(message = "goodsCode  không được null")
    @NotEmpty(message = "goodsCode không được rỗng")
    private String goodsCode;

    @Column(name = "`ORDER`")
    private Integer order;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, length = 19)
    @JsonIgnore
    private Date createdAt = new Date();
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

    @NotNull(message = MsalesValidator.GOODS_CATEGORY_COMPANY_ID_NULL)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    @JsonIgnoreProperties(value = {"locations", "statuss", "code", "address", "contactPersonName", "tel", "fax",
        "email", "lat", "lng", "logoPath", "note"})
    private Company companys;
    @Transient
    private Integer statusId;
    @NotNull(message = "statusId không được Null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STATUS_ID", nullable = false)
    @JsonIgnoreProperties(value = {"statusTypes"})
    private Status statuss;

    public GoodsCategory() {
    }

    public GoodsCategory(Integer id, String name, String goodsCode, Integer order, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser, Integer companyId, Integer statusId) {
        this.id = id;
        this.name = name;
        this.goodsCode = goodsCode;
        this.order = order;
        this.createdUser = createdUser;
        this.updatedAt = updatedAt;
        this.updatedUser = updatedUser;
        this.deletedAt = deletedAt;
        this.deletedUser = deletedUser;
        this.companyId = companyId;
        this.statusId = statusId;
    }

    public GoodsCategory(Integer id, String name, String goodsCode, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser, Integer companyId, Integer statusId) {
        this.id = id;
        this.name = name;
        this.goodsCode = goodsCode;
        this.createdUser = createdUser;
        this.updatedAt = updatedAt;
        this.updatedUser = updatedUser;
        this.deletedAt = deletedAt;
        this.deletedUser = deletedUser;
        this.companyId = companyId;
        this.statusId = statusId;
    }
    


    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }
    @JsonIgnore
    public Integer getStatusId() {
        return statusId;
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

    public Status getStatuss() {
        return statuss;
    }

    public void setStatuss(Status statuss) {
        this.statuss = statuss;
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
    public Integer getCompanyId() {
        return companyId;
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

    public Company getCompanys() {
        return companys;
    }

    public void setCompanys(Company companys) {
        this.companys = companys;
    }

}
