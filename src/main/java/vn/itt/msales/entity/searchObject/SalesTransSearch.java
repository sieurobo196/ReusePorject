/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vn.itt.msales.entity.searchObject;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import vn.itt.msales.common.json.validator.MsalesValidator;

/**
 *
 * @author Khai
 */
public class SalesTransSearch {
    @JsonFormat(pattern = MsalesValidator.DATE_FORMAT_SHORT,shape = JsonFormat.Shape.STRING,timezone = MsalesValidator.GMT)
    private Date salesTransDateTo;
    
    @JsonFormat(pattern = MsalesValidator.DATE_FORMAT_SHORT,shape = JsonFormat.Shape.STRING,timezone = MsalesValidator.GMT)
    private Date salesTransDateFrom;

    public SalesTransSearch() {
    }

    public SalesTransSearch(Date salesTransDateTo, Date salesTransDateFrom) {
        this.salesTransDateTo = salesTransDateTo;
        this.salesTransDateFrom = salesTransDateFrom;
    }

    public Date getSalesTransDateTo() {
        return salesTransDateTo;
    }

    public void setSalesTransDateTo(Date salesTransDateTo) {
        this.salesTransDateTo = salesTransDateTo;
    }

    public Date getSalesTransDateFrom() {
        return salesTransDateFrom;
    }

    public void setSalesTransDateFrom(Date salesTransDateFrom) {
        this.salesTransDateFrom = salesTransDateFrom;
    }
    
    
    
}
