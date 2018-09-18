package vn.itt.msales.channel.model;

import java.util.List;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelLocation;
import vn.itt.msales.entity.GoodsChannelFocus;

/**
 * @author ChinhNQ
 */
public class WebChannelLocation {

    private Channel channel;
    private List<ChannelLocation> locationList;
    private List<GoodsChannelFocus> goodsChannelFocusList;
    private int isProvince;
    private int locationType;
    public WebChannelLocation() {
    }

    public WebChannelLocation(Channel channel, List<ChannelLocation> locationList, List<GoodsChannelFocus> goodsChannelFocusList, int isProvince, int locationType) {
        this.channel = channel;
        this.locationList = locationList;
        this.goodsChannelFocusList = goodsChannelFocusList;
        this.isProvince = isProvince;
        this.locationType = locationType;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public List<ChannelLocation> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<ChannelLocation> locationList) {
        this.locationList = locationList;
    }

    public List<GoodsChannelFocus> getGoodsChannelFocusList() {
        return goodsChannelFocusList;
    }

    public void setGoodsChannelFocusList(List<GoodsChannelFocus> goodsChannelFocusList) {
        this.goodsChannelFocusList = goodsChannelFocusList;
    }

    public int getIsProvince() {
        return isProvince;
    }

    public void setIsProvince(int isProvince) {
        this.isProvince = isProvince;
    }

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

}
