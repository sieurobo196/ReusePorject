/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.searchObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Khai
 */
public class OrderItem implements Comparable<OrderItem> {   
    private LinkedHashMap POSInfo;
    
    @JsonIgnoreProperties(value = {"createdAt"})
    private List<LinkedHashMap> orderList = new ArrayList<>();

    public OrderItem() {
    }

    @JsonProperty(value = "POSInfo")
    public LinkedHashMap getPOSInfo() {
        return POSInfo;
    }

    public void setPOSInfo(LinkedHashMap POSInfo) {
        this.POSInfo = POSInfo;
    }
    
    public List<LinkedHashMap> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<LinkedHashMap> orderList) {
        this.orderList = orderList;
    }

    @Override
    public int compareTo(OrderItem object) {
        if (!orderList.isEmpty()) {
            if (object != null && !object.orderList.isEmpty()) {
                Date date1 = (Date) object.orderList.get(0).get("createdAt");
                Date date2 = (Date) orderList.get(0).get("createdAt");
                return date1.compareTo(date2);
            } else {
                return 1;
            }
        } else {
            return -1;
        }
    }
}
