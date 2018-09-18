package vn.itt.msales.channel.services;

import java.util.List;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelLocation;
import vn.itt.msales.entity.ChannelType;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsChannelFocus;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.services.DataService;

/**
 *
 * @author ChinhNQ
 */
public interface WebMsalesChannelServices {

    public List<ChannelType> getListChannelType(int companyid);

    public MsalesResults<Channel> getListChannelByType(int channelId, int channelTypeId, int companyId, int page, int size);

    public MsalesResults<Channel> searchChannel(String channelparentCode, List<?> channelTypes, int channelTypeId, int companyId, String textSearch, MsalesPageRequest page);

    public MsalesResults<Channel> searchChannel(int channelTypeId, int companyId, String textSearch, MsalesPageRequest page);

    public List<ChannelType> getChannelRegion(int companyId);

    public MsalesResults<Channel> getListChannelByParent(int parentId, int companyId);

    public List<Channel> checkFullCodeChannelParent(String fullCode, int companyId);

    public Channel getChannelByFullCode(String fullCode, int companyId, DataService dataService);

    public Channel getChannelByCode(String code, int companyId, DataService dataService);

    public ChannelType getChannelTypeByCode(String code, int companyId, DataService dataService);

    public Goods getGoodsByCode(String code, int companyId, DataService dataService);

    public List<Status> getChannelStatus(DataService dataService);

    public Location getCityByCode(String code, DataService dataService);
    
    public Location getDistrictByCode(String code, DataService dataService);

    public Location getDistrictByCode(String code,int parentId, DataService dataService);

    public Location getWardByCode(String code,int parentId, DataService dataService);

    public List<ChannelLocation> getListChannelLocation(int channelId, DataService dataService);

    public List<GoodsChannelFocus> getListGoodsChannelFocus(int channelId, DataService dataService);

    public String checkChannelRelation(int companyId, int channelId, DataService dataService);

}
