package vn.itt.msales.report.model;

import java.util.Date;

/**
 *
 * @author ChinhNQ
 */
public class MsalesOrderReport {
    private String employeCode;
    private String employeName;
    private String customerCode;
    private String customerName;
    private String address;
    private String tel;
    private String goodsCode;
    private String goodsName;
    private int quantity;
    private int price;
    private long distributionName;
    private Date orderDate;

    public MsalesOrderReport() {
    }

    public MsalesOrderReport(String employeCode, String employeName, String customerCode, String customername, String address, String tel, String goodsCode, String goodsName, int quantity, int price, long distributionName, Date orderDate) {
        this.employeCode = employeCode;
        this.employeName = employeName;
        this.customerCode = customerCode;
        this.customerName = customername;
        this.address = address;
        this.tel = tel;
        this.goodsCode = goodsCode;
        this.goodsName = goodsName;
        this.quantity = quantity;
        this.price = price;
        this.distributionName = distributionName;
        this.orderDate = orderDate;
    }

    public String getEmployeCode() {
        return employeCode;
    }

    public void setEmployeCode(String employeCode) {
        this.employeCode = employeCode;
    }

    public String getEmployeName() {
        return employeName;
    }

    public void setEmployeName(String employeName) {
        this.employeName = employeName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getDistributionName() {
        return distributionName;
    }

    public void setDistributionName(long distributionName) {
        this.distributionName = distributionName;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

}
