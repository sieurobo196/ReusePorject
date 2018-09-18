package vn.itt.msales.channel.model;

import java.math.BigDecimal;
import java.util.Date;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.Status;

/**
 *
 * @author ChinhNQ
 */
public class MsalesPOSImport {

    private String posCode;
    private String posName;
    private String wardCode;
    private String districtCode;
    private String cityCode;
    private String posChannelCode;
    private String posAddress;
    private BigDecimal posLAT;
    private BigDecimal posLNG;
    private String posOwnerName;
    private Date posBirthay;
    private String posOwnerCode;
    private Date posOwnerCodeDate;
    private String posOwnerCodeLocation;
    private String posHomePhone;
    private String posMobilePhone;
    private String posOtherTel;
    private String posEmail;
    private String posWebsite;
    private String posFax;
    private Integer posFrequency;
    private Integer posDayOfWeek;
    private Date posBeginAt;
    private Integer posHierarchy;
    private Integer status;
    
    private Channel channel;
    private Location location;
    private Status statuss;
    
    private boolean cityError = false;
    private boolean districtError = false;
    private boolean wardErrror = false;
    

    public MsalesPOSImport() {

    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }

    public String getPosName() {
        return posName;
    }

    public void setPosName(String posName) {
        this.posName = posName;
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }    
    
    public String getPosChannelCode() {
        return posChannelCode;
    }

    public void setPosChannelCode(String posChannelCode) {
        this.posChannelCode = posChannelCode;
    }

    public String getPosAddress() {
        return posAddress;
    }

    public void setPosAddress(String posAddress) {
        this.posAddress = posAddress;
    }

    public BigDecimal getPosLAT() {
        return posLAT;
    }

    public void setPosLAT(BigDecimal posLAT) {
        this.posLAT = posLAT;
    }

    public BigDecimal getPosLNG() {
        return posLNG;
    }

    public void setPosLNG(BigDecimal posLNG) {
        this.posLNG = posLNG;
    }

    public String getPosOwnerName() {
        return posOwnerName;
    }

    public void setPosOwnerName(String posOwnerName) {
        this.posOwnerName = posOwnerName;
    }

    public String getPosOwnerCode() {
        return posOwnerCode;
    }

    public void setPosOwnerCode(String posOwnerCode) {
        this.posOwnerCode = posOwnerCode;
    }

    public Date getPosOwnerCodeDate() {
        return posOwnerCodeDate;
    }

    public void setPosOwnerCodeDate(Date posOwnerCodeDate) {
        this.posOwnerCodeDate = posOwnerCodeDate;
    }

    public String getPosOwnerCodeLocation() {
        return posOwnerCodeLocation;
    }

    public void setPosOwnerCodeLocation(String posOwnerCodeLocation) {
        this.posOwnerCodeLocation = posOwnerCodeLocation;
    }

    public String getPosHomePhone() {
        return posHomePhone;
    }

    public void setPosHomePhone(String posHomePhone) {
        this.posHomePhone = posHomePhone;
    }

    public String getPosMobilePhone() {
        return posMobilePhone;
    }

    public void setPosMobilePhone(String posMobilePhone) {
        this.posMobilePhone = posMobilePhone;
    }

    public String getPosOtherTel() {
        return posOtherTel;
    }

    public void setPosOtherTel(String posOtherTel) {
        this.posOtherTel = posOtherTel;
    }

    public String getPosEmail() {
        return posEmail;
    }

    public void setPosEmail(String posEmail) {
        this.posEmail = posEmail;
    }

    public String getPosWebsite() {
        return posWebsite;
    }

    public void setPosWebsite(String posWebsite) {
        this.posWebsite = posWebsite;
    }

    public String getPosFax() {
        return posFax;
    }

    public void setPosFax(String posFax) {
        this.posFax = posFax;
    }

    
    public Date getPosBirthay() {
        return posBirthay;
    }

    public void setPosBirthay(Date posBirthay) {
        this.posBirthay = posBirthay;
    }

    public Date getPosBeginAt() {
        return posBeginAt;
    }

    public void setPosBeginAt(Date posBeginAt) {
        this.posBeginAt = posBeginAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Status getStatuss() {
        return statuss;
    }

    public void setStatuss(Status statuss) {
        this.statuss = statuss;
    }

    public Integer getPosHierarchy() {
        return posHierarchy;
    }

    public void setPosHierarchy(Integer posHierarchy) {
        this.posHierarchy = posHierarchy;
    }

    public Integer getPosFrequency() {
        return posFrequency;
    }

    public void setPosFrequency(Integer posFrequency) {
        this.posFrequency = posFrequency;
    }

    public Integer getPosDayOfWeek() {
        return posDayOfWeek;
    }

    public void setPosDayOfWeek(Integer posDayOfWeek) {
        this.posDayOfWeek = posDayOfWeek;
    }

    public boolean isCityError() {
        return cityError;
    }

    public void setCityError(boolean cityError) {
        this.cityError = cityError;
    }

    public boolean isDistrictError() {
        return districtError;
    }

    public void setDistrictError(boolean districtError) {
        this.districtError = districtError;
    }

    public boolean isWardErrror() {
        return wardErrror;
    }

    public void setWardErrror(boolean wardErrror) {
        this.wardErrror = wardErrror;
    }
}
