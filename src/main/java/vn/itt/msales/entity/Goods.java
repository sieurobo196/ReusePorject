package vn.itt.msales.entity;

// Generated Jun 12, 2015 1:28:48 PM by Hibernate Tools 4.0.0
import java.util.Date;
import java.util.List;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.format.annotation.NumberFormat;
import vn.itt.msales.common.json.validator.MsalesValidator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Min;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Goods generated by hbm2java
 */
@Entity
@Table(name = "`goods`")
@JsonFilter("JSONFILTER")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Goods implements java.io.Serializable, Comparable<Goods> {

    private static final long serialVersionUID = 4597098302254342389L;

    @Column(name = "ID", unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ID")
    @SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_GOODS_ID")
    private Integer id;

    @Transient
    private Integer parentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PARENT_ID", nullable = true)
    private Goods parents;

    @JsonIgnoreProperties(value = {"parentId", "goodsCode", "statusId",
        "isRecover", "price", "factor", "isFocus", "order", "parents",
        "goodsCategorys", "statuss"})
    public Goods getParents() {
        return parents;
    }

    @JsonGetter
    public void setParents(Goods parents) {
        this.parents = parents;
    }

    @Column(name = "GOODS_CODE", nullable = false, length = 100)
    @NotEmpty(message = "goodsCode không được rỗng hoặc null")
    @NotNull(message = MsalesValidator.GOODS_GOODSCODE_NULL)
    @Size(min = 1, max = 100, message = "GoodsCode có chiều dài từ 1 tới 100 kí tự")
    private String goodsCode;

    @Transient
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Transient
    private Integer statusId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STATUS_ID", nullable = false)
    @NotNull(message = MsalesValidator.STATUS_NULL)
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Status statuss;

    @JsonIgnoreProperties(value = {"value", "statusTypes"})
    public Status getStatuss() {
        return statuss;
    }

    public void setStatuss(Status statuss) {
        this.statuss = statuss;
    }

    @Transient
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @Min(value = 1, message = "goodsCategoryId không được rỗng")
    private Integer goodsCategoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GOOD_CATEGORY_ID", nullable = false)
    @NotNull(message = MsalesValidator.GOODS_GOODS_CATEGORY_ID_NULL)
    @JsonIgnoreProperties(value = {"companys"})
    private GoodsCategory goodsCategorys;

    @JsonIgnoreProperties(value = "order")
    public GoodsCategory getGoodsCategorys() {
        return goodsCategorys;
    }

    public void setGoodsCategorys(GoodsCategory goodsCategory) {
        this.goodsCategorys = goodsCategory;
    }

    @Column(name = "IS_RECOVER", nullable = true)
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private boolean isRecover;

    @Column(name = "PRICE", nullable = false)
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @NotNull(message = MsalesValidator.PRICE_NULL)
    @javax.validation.constraints.Min(value = 0, message = MsalesValidator.GOODS_PRICE)
    private Integer price;

    @Column(name = "NAME", nullable = false, length = 200)
    @NotNull(message = MsalesValidator.NAME_NULL)
    @Size(min = 3, max = 200, message = "Tên hợp lệ có chiều dài từ 3 tới 200 kí tự")
    private String name;

    @Column(name = "FACTOR", nullable = true)
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Integer factor;

    @Column(name = "IS_FOCUS", nullable = true)
    private boolean isFocus;

    @Column(name = "`ORDER`", nullable = true)
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Integer order;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, length = 10)
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

    @Transient
    private List<GoodsUnit> goodsUnits;

    @JsonGetter
    public List<GoodsUnit> getGoodsUnits() {
        return goodsUnits;
    }

    @JsonSetter
    public void setGoodsUnits(List<GoodsUnit> goodsUnits) {
        this.goodsUnits = goodsUnits;
    }
    @Transient
    private List<GoodsUnit> units;

    @JsonIgnoreProperties(value = {"goodss", "units", "isActive", "note"})
    public List<GoodsUnit> getUnits() {
        return units;
    }

    public void setUnits(List<GoodsUnit> units) {
        this.units = units;
    }

    @Transient
    private GoodsUnit unit;

    public GoodsUnit getUnit() {
        return unit;
    }

    public void setUnit(GoodsUnit unit) {
        this.unit = unit;
    }

    @Transient
    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Goods() {
    }

    public Goods(String goodsCode, Integer statusId, Integer goodCategoryId,
            boolean isRecover, Integer price, String name, Integer factor,
            boolean isFocus, Integer createdUser, Integer updatedUser,
            Integer deletedUser) {
        this.goodsCode = goodsCode;
        this.statusId = statusId;
        this.goodsCategoryId = goodCategoryId;
        this.isRecover = isRecover;
        this.price = price;
        this.name = name;
        this.factor = factor;
        this.isFocus = isFocus;
        this.createdUser = createdUser;
        this.updatedUser = updatedUser;
        this.deletedUser = deletedUser;
    }

    public Goods(Integer parentId, String goodsCode, Integer statusId,
            Integer goodCategoryId, boolean isRecover, Integer price,
            String name, Integer factor, boolean isFocus, Integer order,
            Date createdAt, Integer createdUser, Date updatedAt,
            Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.parentId = parentId;
        this.goodsCode = goodsCode;
        this.statusId = statusId;
        this.goodsCategoryId = goodCategoryId;
        this.isRecover = isRecover;
        this.price = price;
        this.name = name;
        this.factor = factor;
        this.isFocus = isFocus;
        this.order = order;
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
    public Integer getParentId() {
        return this.parentId;
    }

    @JsonSetter
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
        if (this.parentId == null) {
            this.parents = null;
        } else {
            Goods goods = new Goods();
            goods.setId(this.parentId);
            this.setParents(goods);
        }
    }

    public String getGoodsCode() {
        return this.goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    @JsonIgnore
    public Integer getStatusId() {
        return this.statusId;
    }

    @JsonSetter
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
        Status status = new Status();
        status.setId(this.getStatusId());
        this.setStatuss(status);
    }

    @JsonIgnore
    public Integer getGoodsCategoryId() {
        return this.goodsCategoryId;
    }

    @JsonSetter
    public void setGoodsCategoryId(Integer goodsCategoryId) {
        this.goodsCategoryId = goodsCategoryId;
        GoodsCategory goodsCategory = new GoodsCategory();
        goodsCategory.setId(this.getGoodsCategoryId());
        this.setGoodsCategorys(goodsCategory);
    }

    public boolean getIsRecover() {
        return this.isRecover;
    }

    public void setIsRecover(boolean isRecover) {
        this.isRecover = isRecover;
    }

    public Integer getPrice() {
        return this.price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFactor() {
        return this.factor;
    }

    public void setFactor(Integer factor) {
        this.factor = factor;
    }

    public boolean getIsFocus() {
        return this.isFocus;
    }

    public void setIsFocus(boolean isFocus) {
        this.isFocus = isFocus;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @JsonIgnore
    public Date getCreatedAt() {
        return this.createdAt;
    }

    @JsonSetter
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

    @JsonSetter
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

    @JsonSetter
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
    public int compareTo(Goods compareGoods) {
        int goodsId = compareGoods.getId();
        return this.id - goodsId;
    }

    @Transient
    private Integer goodsUnitId;

	public Integer getGoodsUnitId() {
		return goodsUnitId;
	}

	public void setGoodsUnitId(Integer goodsUnitId) {
		this.goodsUnitId = goodsUnitId;
	}
    
}
