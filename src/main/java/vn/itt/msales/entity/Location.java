package vn.itt.msales.entity;

// Generated Jun 11, 2015 10:37:14 AM by Hibernate Tools 4.3.1
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.NumberFormat;

import vn.itt.msales.common.json.validator.MsalesValidator;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

/**
 * Location generated by hbm2java
 */
/**
 *
 * @author DuanND
 *
 */
@Entity
@Table(name = "`location`")
@JsonFilter("JSONFILTER")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Location implements java.io.Serializable {

    private static final long serialVersionUID = -3327873986750826968L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ID")
    @SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_LOCATION_ID")
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
    
    @Column(name = "CODE", nullable = false, length = 100)
    @NotEmpty(message = MsalesValidator.LOCATION_CODE_EMPTY)
    @Size(max = 100, message = MsalesValidator.LOCATION_CODE_MAX_LENGTH)
    private String code;
   
    @Column(name = "NAME", nullable = false, length = 100)
    @NotNull(message = MsalesValidator.NAME_NULL)
    @NotEmpty(message = "Chưa nhập tên địa điểm")
    @Size(max = 100, message = MsalesValidator.LOCATION_NAME_MAX_LENGTH)
    private String name;
    
    @Transient
    private Integer parentId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PARENT_ID", nullable = true)
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @JsonIgnoreProperties(value = {"code",  "locationType", "note",
            "lat", "lng"})
    private Location parents;
    

    public Location getParents() {
        return parents;
    }

    public void setParents(Location parents) {
        this.parents = parents;
    }

    @Column(name = "LOCATION_TYPE", nullable = false)
    @NotNull(message = MsalesValidator.LOCATION_LOCATIONTYPE_NULL)
    @Max(value = 4, message = MsalesValidator.LOCATION_LOCATIONTYPE_MAX_VALUE)
    private Integer locationType;
    
    @Column(name = "NOTE", nullable = true, length = 1024)
    private String note;
   
    @Column(name = "LAT", nullable = false, precision = 24, scale = 15)
    @NotNull(message = "Chưa nhập vĩ độ")
    private BigDecimal lat;

    @Column(name = "LNG", nullable = false, precision = 24, scale = 15)
    @NotNull(message = "Chưa nhập kinh độ")
    private BigDecimal lng;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, length = 19)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_LONG, timezone = "GMT+7")
    private Date createdAt;
    
    @Column(name = "CREATED_USER", nullable = false)
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @NotNull(message = MsalesValidator.CREATED_USER_NULL)
    private Integer createdUser;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT", length = 19)
    @JsonIgnore
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_LONG, timezone = "GMT+7")
    private Date updatedAt;

    @Column(name = "UPDATED_USER", nullable = false)
    @NotNull(message = MsalesValidator.UPDATED_USER_NULL)
    private Integer updatedUser;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DELETED_AT", length = 19)
    @JsonIgnore
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_LONG, timezone = "GMT+7")
    private Date deletedAt;

    @Column(name = "DELETED_USER", nullable = false)
    @NotNull(message = MsalesValidator.DELETED_USER_NULL)
    private Integer deletedUser;

    public Location() {
    }

    public Location(String code, String name, Integer locationType,
            BigDecimal lat, BigDecimal lng, Date createdAt,
            Integer createdUser, Date updatedAt, Integer updatedUser,
            Date deletedAt, Integer deletedUser) {
        this.code = code;
        this.name = name;
        this.locationType = locationType;
        this.lat = lat;
        this.lng = lng;
        this.createdAt = createdAt;
        this.createdUser = createdUser;
        this.updatedAt = updatedAt;
        this.updatedUser = updatedUser;
        this.deletedAt = deletedAt;
        this.deletedUser = deletedUser;
    }

    public Location(String code, String name, Integer parentId,
            Integer locationType, String note, BigDecimal lat, BigDecimal lng,
            Date createdAt, Integer createdUser, Date updatedAt,
            Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.code = code;
        this.name = name;
        this.parentId = parentId;
        this.locationType = locationType;
        this.note = note;
        this.lat = lat;
        this.lng = lng;
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

    // @Column(name = "PARENT_ID")
    @JsonIgnore
    public Integer getParentId() {
        return this.parentId;

    }

    @JsonSetter
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
        if (this.parentId == null) {
            this.parents = null;
        } else {
            Location location = new Location();
            location.setId(this.parentId);
            this.setParents(location);
        }
    }

    public Integer getLocationType() {
        return this.locationType;
    }

    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
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

    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
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
