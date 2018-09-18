package vn.itt.msales.channel.model;

import java.util.List;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelType;

/**
 *
 * @author ChinhNQ
 */
public class WebMsalesChannelType {

    private int channelSelected;
    private ChannelType channelType;
    List<Channel> channels;

    public WebMsalesChannelType() {
    }

    public WebMsalesChannelType(int channelSelected, ChannelType channelType, List<Channel> channels) {
        this.channelSelected = channelSelected;
        this.channelType = channelType;
        this.channels = channels;
    }

    public int getChannelSelected() {
        return channelSelected;
    }

    public void setChannelSelected(int channelSelected) {
        this.channelSelected = channelSelected;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

}
