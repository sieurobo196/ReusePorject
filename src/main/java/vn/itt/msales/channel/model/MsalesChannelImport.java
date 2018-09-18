package vn.itt.msales.channel.model;

import java.math.BigDecimal;
import java.util.List;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelType;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.Status;

/**
 *
 * @author ChinhNQ
 */
public class MsalesChannelImport {

    private Integer channelID;
    private String channelTypeCode;
    private String channelCode;
    private String channelName;
    private String channelParentCode;
    private String channelAddress;
    private BigDecimal channelLAT;
    private BigDecimal channelLNG;
    private String channelPersonContact;
    private String channelTel;
    private String channelEmail;
    private String channelFax;
    private String channelStatus;
    private String channelLocation;
    private String channelGoodsCodeFocus;

    private Channel channelParent;
    private List<Goods> goodsList;
    private ChannelType channelType;
    private Status status;
    private List<Location> locationList;

    private Channel channel;
    
    public MsalesChannelImport() {
    }

    public Integer getChannelID() {
        return channelID;
    }

    public void setChannelID(Integer channelID) {
        this.channelID = channelID;
    }

    public String getChannelTypeCode() {
        return channelTypeCode;
    }

    public void setChannelTypeCode(String channelTypeCode) {
        this.channelTypeCode = channelTypeCode;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelParentCode() {
        return channelParentCode;
    }

    public void setChannelParentCode(String channelParentCode) {
        this.channelParentCode = channelParentCode;
    }

    public String getChannelAddress() {
        return channelAddress;
    }

    public void setChannelAddress(String channelAddress) {
        this.channelAddress = channelAddress;
    }

    public BigDecimal getChannelLAT() {
        return channelLAT;
    }

    public void setChannelLAT(BigDecimal channelLAT) {
        this.channelLAT = channelLAT;
    }

    public BigDecimal getChannelLNG() {
        return channelLNG;
    }

    public void setChannelLNG(BigDecimal channelLNG) {
        this.channelLNG = channelLNG;
    }

    public String getChannelPersonContact() {
        return channelPersonContact;
    }

    public void setChannelPersonContact(String channelPersonContact) {
        this.channelPersonContact = channelPersonContact;
    }

    public String getChannelTel() {
        return channelTel;
    }

    public void setChannelTel(String channelTel) {
        this.channelTel = channelTel;
    }

    public String getChannelEmail() {
        return channelEmail;
    }

    public void setChannelEmail(String channelEmail) {
        this.channelEmail = channelEmail;
    }

    public String getChannelFax() {
        return channelFax;
    }

    public void setChannelFax(String channelFax) {
        this.channelFax = channelFax;
    }

    public String getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(String channelStatus) {
        this.channelStatus = channelStatus;
    }

    
   
    public String getChannelLocation() {
        return channelLocation;
    }

    public void setChannelLocation(String channelLocation) {
        this.channelLocation = channelLocation;
    }

    public String getChannelGoodsCodeFocus() {
        return channelGoodsCodeFocus;
    }

    public void setChannelGoodsCodeFocus(String channelGoodsCodeFocus) {
        this.channelGoodsCodeFocus = channelGoodsCodeFocus;
    }

    public Channel getChannelParent() {
        return channelParent;
    }

    public void setChannelParent(Channel channelParent) {
        this.channelParent = channelParent;
    }

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    
}
