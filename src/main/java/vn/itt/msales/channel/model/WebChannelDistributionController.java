package vn.itt.msales.channel.model;

import java.util.List;
import vn.itt.msales.entity.Channel;

/**
 *
 * @author ChinhNQ
 */
public class WebChannelDistributionController {

    private int channelType;
    private String textSearch = "";
    private List<Channel> listChannel;

    public WebChannelDistributionController() {
    }

    public WebChannelDistributionController(int channelType, String textSearch, List<Channel> listChannel) {
        this.channelType = channelType;
        this.textSearch = textSearch;
        this.listChannel = listChannel;
    }

    public int getChannelType() {
        return channelType;
    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }

    public String getTextSearch() {
        return textSearch;
    }

    public void setTextSearch(String textSearch) {
        this.textSearch = textSearch;
    }

    public List<Channel> getListChannel() {
        return listChannel;
    }

    public void setListChannel(List<Channel> listChannel) {
        this.listChannel = listChannel;
    }

}
