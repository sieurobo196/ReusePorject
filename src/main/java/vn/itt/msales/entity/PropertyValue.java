package vn.itt.msales.entity;
// Generated Jun 11, 2015 10:37:14 AM by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
import vn.itt.msales.common.json.validator.MsalesValidator;

/**
 * PropertyValue generated by hbm2java
 */
@Entity
@Table(name = "`property_value`"
)
public class PropertyValue implements java.io.Serializable {

    private static final long serialVersionUID = -7403174822746430317L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO ,generator = "SEQ_ID")
    @SequenceGenerator(name = "SEQ_ID",sequenceName = "SEQ_PROPERTYVALUE_ID")
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;

    @Column(name = "VALUE_ID")
    private Integer valueId;
    @Column(name = "VALUE", nullable = true, length = 256)
    private String value;

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
    private Integer tableNameId;

    @Transient
    private Integer propertyId;

    //More info
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull(message = MsalesValidator.PROPERTYVALUE_TABLENAME_ID_NULL)
    @JoinColumn(name = "TABLE_NAME_ID", nullable = false)
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private TableName tableNames;

    public TableName getTableNames() {
        return tableNames;
    }

    public void setTableNames(TableName tableNames) {
        this.tableNames = tableNames;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull(message = MsalesValidator.PROPERTYVALUE_PROPERTY_ID_NULL)
    @JoinColumn(name = "PROPERTY_ID", nullable = false)
    @JsonIgnoreProperties(value = {"tableNames", "note"})
    private Property propertys;

    public Property getPropertys() {
        return propertys;
    }

    public void setPropertys(Property propertys) {
        this.propertys = propertys;
    }

    public PropertyValue() {
    }

    public PropertyValue(Integer tableNameId, Integer propertyId, Integer valueId, String value, Date createdAt, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.tableNameId = tableNameId;
        this.propertyId = propertyId;
        this.valueId = valueId;
        this.value = value;
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
    public Integer getTableNameId() {
        return this.tableNameId;
    }

    @JsonSetter
    public void setTableNameId(Integer tableNameId) {
        this.tableNameId = tableNameId;
        if (tableNameId != null) {
            TableName tableName = new TableName();
            tableName.setId(this.getTableNameId());
            this.tableNames = tableName;
        }
    }

    @JsonIgnore
    public Integer getPropertyId() {
        return this.propertyId;
    }

    @JsonSetter
    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
        if (propertyId != null) {
            Property property = new Property();
            property.setId(this.getPropertyId());
            this.propertys = property;
        }
    }

    public Integer getValueId() {
        return this.valueId;
    }

    public void setValueId(Integer valueId) {
        this.valueId = valueId;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
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

}
