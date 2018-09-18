/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.searchObject;

/**
 *
 * @author vtm
 */
public class DeviceApp {

    private String username;
    private String password;
    private String imei;
    private String subscriberId;
    private String appVersion;

    public DeviceApp() {
    }

    public DeviceApp(String username, String password, String imei, String subscriberId, String appVersion) {
        this.username = username;
        this.password = password;
        this.imei = imei;
        this.subscriberId = subscriberId;
        this.appVersion = appVersion;
    }    

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
    
}
