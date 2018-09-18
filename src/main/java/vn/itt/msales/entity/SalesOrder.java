package vn.itt.msales.entity;
// Generated Jun 22, 2015 9:37:59 AM by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
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

import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.entity.searchObject.OrderList;
import vn.itt.msales.entity.searchObject.POSInfo;

/**
 * SalesOrder generated by hbm2java
 */
@Entity
@Table(name = "`sales_order`"
)
@JsonIgnoreProperties(value = {"companys"})
@JsonFilter("JSONFILTER")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SalesOrder implements java.io.Serializable {

    private static final long serialVersionUID = -7009143610938504774L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ID")
    @SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_SALESORDER_ID")
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;

    @Column(name = "TRANS_CODE", nullable = true, length = 256)
    private String transCode;

    @Transient
    private Integer stockId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SALES_TRANS_DATE", nullable = false, length = 19)
    @NotNull(message = "salesTransDate không được null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_SHORT, timezone = "GMT+7")
    private Date salesTransDate;

    @Transient
    private Integer statusId;

    @NotNull(message = "Trạng thái đặt hàng bắt buộc nhập")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STATUS_ID", nullable = false)
    private Status statuss;

    @Column(name = "NOTE", length = 256)
    private String note;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, length = 19)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_LONG, timezone = "GMT+7")
    @JsonIgnore
    private Date createdAt;

    //@Column(name = "CREATED_USER", nullable = false)
    @Transient
    private Integer createdUser;

    @NotNull(message = MsalesValidator.CREATED_USER_NULL)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CREATED_USER", nullable = false)
    private User createdUsers;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STOCK_ID")
    private SalesStock stocks;

    @JsonIgnoreProperties(value = {"statuss"})
    public SalesStock getStocks() {
        return stocks;
    }

    public void setStocks(SalesStock stocks) {
        this.stocks = stocks;

    }

    @Transient
    private Integer companyId;

    @NotNull(message = MsalesValidator.SALES_ORDER_COMPANY_ID_NULL)
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
    private User customerCareUser;

    @JsonIgnoreProperties(value = {"userCode", "birthday", "monitoringUsers",
        "employerUserId", "isActive", "activeCode", "employerType",
        "useEvoucher", "statusId", "username", "sex", "email",
        "yahooId", "skypeId", "isdn", "tel", "address", "note",
        "ipLastLogin", "updatedAt", "updatedUser", "employerUsers",
        "createdUser", "companys", "lastName", "firstName"})
    public User getCustomerCareUser() {
        return customerCareUser;
    }

    public void setCustomerCareUser(User customerCareUser) {
        this.customerCareUser = customerCareUser;
    }

    @Transient
    private SalesOrder orderInfomation;

    @JsonIgnoreProperties(value = {"id", "salesTransDate", "statusId", "stocks"})
    public SalesOrder getOrderInfomation() {
        return orderInfomation;
    }

    public void setOrderInfomation(SalesOrder orderInfomation) {
        this.orderInfomation = orderInfomation;
    }

    @Transient
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_LONG, timezone = "GMT+7")
    private Date orderDate;

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    @Transient
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Transient
    private List<SalesOrderDetails> orderDetailsList;

    @JsonIgnoreProperties(value = { "salesTransDate", "isFocus", "orders"})
    public List<SalesOrderDetails> getOrderDetailsList() {
        return orderDetailsList;
    }

    public void setOrderDetailsList(List<SalesOrderDetails> orderDetailsList) {
        this.orderDetailsList = orderDetailsList;
    }

    @Transient
    private Integer posId;

    @JsonIgnoreProperties(value = {"ownerCode", "ownerCodeDate", "ownerCodeLocation", "posCode", "statuss", "locations", "channels", "hierarchy", "ownerName", "street",
        "birthday", "owerCode", "owerCodeDate", "owerCodeLocation", "tel", "mobile", "otherTel", "fax", "email",
        "website", "gpkd", "gpkdDate", "gpkdLocation", "note", "lat", "lng", "isActive", "beginAt", "endAt", "createdAt", "createdUser"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POS_ID", nullable = false)
    private POS pos;

    public POS getPos() {
        return pos;
    }

    public void setPos(POS pos) {
        this.pos = pos;
    }

    public SalesOrder() {
    }

    public SalesOrder(Date salesTransDate, Date createdAt, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.salesTransDate = salesTransDate;
        this.createdAt = createdAt;
        this.createdUser = createdUser;
        this.updatedAt = updatedAt;
        this.updatedUser = updatedUser;
        this.deletedAt = deletedAt;
        this.deletedUser = deletedUser;
    }

    public SalesOrder(Integer stockId, Date salesTransDate, String note, Date createdAt, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.stockId = stockId;
        this.salesTransDate = salesTransDate;
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

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    @JsonIgnore
    public Integer getPosId() {
        return posId;
    }

    @JsonSetter
    public void setPosId(Integer posId) {
        this.posId = posId;
        POS poss = new POS();
        poss.setId(this.getPosId());
        this.pos = poss;
    }

    @JsonIgnore
    public Integer getStockId() {
        return this.stockId;
    }

    @JsonSetter
    public void setStockId(Integer stockId) {
        this.stockId = stockId;
        if (stockId != null) {
            SalesStock s = new SalesStock();
            s.setId(this.getStockId());
            this.stocks = s;
        }
    }

    public Date getSalesTransDate() {
        return this.salesTransDate;
    }

    public void setSalesTransDate(Date salesTransDate) {
        this.salesTransDate = salesTransDate;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
        if (statusId != null) {
            Status st = new Status();
            st.setId(this.statusId);
            this.statuss = st;

        }
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
        if (createdUser != null) {
            User user = new User();
            user.setId(this.createdUser);
            this.createdUsers = user;
        }
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
            company.setId(this.getCompanyId());
            this.companys = company;
        }
    }

    @Transient
    private POSInfo posInfo;

    public POSInfo getPosInfo() {
        return posInfo;
    }

    public void setPosInfo(POSInfo pOSInfo) {
        posInfo = pOSInfo;
    }

    @Transient
    private List<OrderList> orderList;

    public List<OrderList> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderList> orderList) {
        this.orderList = orderList;
    }
    @Transient
    private POSInfo orderInfo;

    public POSInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(POSInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    @Transient
    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public User getCreatedUsers() {
        return createdUsers;
    }

    public void setCreatedUsers(User createdUsers) {
        this.createdUsers = createdUsers;
    }

    public Status getStatuss() {
        return statuss;
    }

    public void setStatuss(Status statuss) {
        this.statuss = statuss;
    }
    @Transient
    private String nameNVCS;

	public String getNameNVCS() {
		return nameNVCS;
	}

	public void setNameNVCS(String nameNVCS) {
		this.nameNVCS = nameNVCS;
	}
   
    @Transient 
    private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	@Transient 
	@Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy", timezone = "GMT+7")
	private Date dateSalesTrans;

	public Date getDateSalesTrans() {
		return dateSalesTrans;
	}

	public void setDateSalesTrans(Date dateSalesTrans) {
		this.dateSalesTrans = dateSalesTrans;
	}
	

}
