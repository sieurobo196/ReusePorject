/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.searchObject;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.entity.CustomerCareDetails;

/**
 *
 * @author vtm
 */
public class SearchObject {

    private Integer id;
    private Integer userId;
    private Integer channelId;
    private Integer locationId;
    private Integer userRoleId;
    private Integer statusId;
    private Integer type;
    private Integer day;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_SHORT, locale = MsalesValidator.GMT)
    private Date date;
    private Integer posId;
    private Integer orderId;

    private String name;
    private String searchText;
    private String code;
    private String posCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_SHORT, locale = MsalesValidator.GMT)
    private Date fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_SHORT, locale = MsalesValidator.GMT)
    private Date toDate;

    private List<Integer> promotionList;
    private Integer promotionId;
    private Integer transRetailerId;
    private Integer accumulationRetailerId;
    private Integer parentId;
    private Integer implementEmployeeId;

    private String passwordOld;
    private String passwordNew;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_SHORT, locale = MsalesValidator.GMT)
    private Date beginDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_SHORT, locale = MsalesValidator.GMT)
    private Date finishTime;

    private Integer mcpId;
    private Integer mcpDetailsId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_LONG, timezone = "GMT+7")
    private Date startAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_LONG, timezone = "GMT+7")
    private Date finishAt;

    private BigDecimal lat;
    private BigDecimal lng;
    private CustomerCareDetails[] details;

    private Integer customerCareDetailsId;
    private String content;

    private String transCode;
    private Integer mcpDay;
    
    private Integer salesTransId;
    private Integer salesOrderId;
    
    private String note;
    
    private Integer month;
    private Integer year;
    private Integer mcpSalesId;
    
    private Integer branch = 0;
    private Integer packageService = 0;
    
    private String imei;
    
    private String subscriberId;
    
    private Integer companyId;
    
    public SearchObject() {
    }

    public SearchObject(Integer id, Integer userId, Integer channelId, Integer locationId, Integer userRoleId, Integer statusId, Integer type, Integer day, Date date, Integer posId, Integer orderId, String name, String searchText, String code, String posCode, Date fromDate, Date toDate) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.locationId = locationId;
        this.userRoleId = userRoleId;
        this.statusId = statusId;
        this.type = type;
        this.day = day;
        this.date = date;
        this.posId = posId;
        this.orderId = orderId;
        this.name = name;
        this.searchText = searchText;
        this.code = code;
        this.posCode = posCode;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getPosId() {
        return posId;
    }

    public void setPosId(Integer posId) {
        this.posId = posId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public List<Integer> getPromotionList() {
        return promotionList;
    }

    public void setPromotionList(List<Integer> promotionList) {
        this.promotionList = promotionList;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public Integer getTransRetailerId() {
        return transRetailerId;
    }

    public void setTransRetailerId(Integer transRetailerId) {
        this.transRetailerId = transRetailerId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getImplementEmployeeId() {
        return implementEmployeeId;
    }

    public void setImplementEmployeeId(Integer implementEmployeeId) {
        this.implementEmployeeId = implementEmployeeId;
    }

    public String getPasswordOld() {
        return passwordOld;
    }

    public void setPasswordOld(String passwordOld) {
        this.passwordOld = passwordOld;
    }

    public String getPasswordNew() {
        return passwordNew;
    }

    public void setPasswordNew(String passwordNew) {
        this.passwordNew = passwordNew;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getMcpId() {
        return mcpId;
    }

    public void setMcpId(Integer mcpId) {
        this.mcpId = mcpId;
    }

    public Integer getMcpDetailsId() {
        return mcpDetailsId;
    }

    public void setMcpDetailsId(Integer mcpDetailsId) {
        this.mcpDetailsId = mcpDetailsId;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getFinishAt() {
        return finishAt;
    }

    public void setFinishAt(Date finishAt) {
        this.finishAt = finishAt;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public CustomerCareDetails[] getDetails() {
        return details;
    }

    public void setDetails(CustomerCareDetails[] details) {
        this.details = details;
    }

    public Integer getCustomerCareDetailsId() {
        return customerCareDetailsId;
    }

    public void setCustomerCareDetailsId(Integer customerCareDetailsId) {
        this.customerCareDetailsId = customerCareDetailsId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public Integer getMcpDay() {
        return mcpDay;
    }

    public void setMcpDay(Integer mcpDay) {
        this.mcpDay = mcpDay;
    }

    public Integer getSalesTransId() {
        return salesTransId;
    }

    public void setSalesTransId(Integer salesTransId) {
        this.salesTransId = salesTransId;
    }

    public Integer getSalesOrderId() {
        return salesOrderId;
    }

    public void setSalesOrderId(Integer salesOrderId) {
        this.salesOrderId = salesOrderId;
    }

    public Integer getAccumulationRetailerId() {
        return accumulationRetailerId;
    }

    public void setAccumulationRetailerId(Integer accumulationRetailerId) {
        this.accumulationRetailerId = accumulationRetailerId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMcpSalesId() {
        return mcpSalesId;
    }

    public void setMcpSalesId(Integer mcpSalesId) {
        this.mcpSalesId = mcpSalesId;
    }

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public Integer getPackageService() {
        return packageService;
    }

    public void setPackageService(Integer packageService) {
        this.packageService = packageService;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}
