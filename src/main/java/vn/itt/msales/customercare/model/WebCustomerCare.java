/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.model;

import java.util.List;
import vn.itt.msales.entity.CustomerCareDetails;

/**
 *
 * @author vtm
 */
public class WebCustomerCare {
    private List<CustomerCareDetails> list;

    public WebCustomerCare() {
    }

    public WebCustomerCare(List<CustomerCareDetails> list) {
        this.list = list;
    }

    public List<CustomerCareDetails> getList() {
        return list;
    }

    public void setList(List<CustomerCareDetails> list) {
        this.list = list;
    }

}
