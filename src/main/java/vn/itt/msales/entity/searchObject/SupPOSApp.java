/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.searchObject;

import java.math.BigDecimal;

/**
 *
 * @author Khai
 */
public class SupPOSApp {
    private Integer id;
    private BigDecimal lat ;
    private BigDecimal lng ;

    public SupPOSApp() {
    }

    public SupPOSApp(Integer id, BigDecimal lat, BigDecimal lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    
    
}
