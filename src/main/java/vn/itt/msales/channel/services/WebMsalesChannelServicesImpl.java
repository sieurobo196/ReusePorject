package vn.itt.msales.channel.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelLocation;
import vn.itt.msales.entity.ChannelType;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsChannelFocus;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.services.DataService;
import vn.itt.msales.services.DataServiceImpl;

/**
 *
 * @author ChinhNQ
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class WebMsalesChannelServicesImpl extends DataServiceImpl implements WebMsalesChannelServices {

    @Autowired
    private DataService dataService;

    @Override
    public List<ChannelType> getListChannelType(int companyid) {
        ParameterList param = new ParameterList();
        param.add("companys.id", companyid);
        List<ChannelType> listChannelType = getListOption(ChannelType.class, param);
        return listChannelType;
    }

    @Override
    public MsalesResults<Channel> getListChannelByType(int channelId, int channelTypeId, int companyId, int page, int size) {
        ParameterList param = new ParameterList("channelTypes.id", channelTypeId, page, size);
        param.add("companys.id", companyId);
        if (channelId > 0) {
            param.add("parents.id", channelId);
        }
        MsalesResults<Channel> listChannel = getListOption(Channel.class, param, true);
        return listChannel;
    }

    @Override
    public MsalesResults<Channel> searchChannel(String channelparentCode, List<?> channelTypes, int channelTypeId, int companyId, String textSearch, MsalesPageRequest page) {
        ParameterList parameterList = new ParameterList("companys.id", companyId, page.getPageNo(), page.getRecordsInPage());
        parameterList.add("fullCode", channelparentCode + "%", "like");
        parameterList.notin("channelTypes.id", channelTypes);
        if (channelTypeId > 0) {
            parameterList.add("channelTypes.id", channelTypeId);
        }
        if (!textSearch.isEmpty() && !textSearch.trim().isEmpty()) {
            parameterList.or("fullCode", textSearch, "like", "name", textSearch, "like");
        }
        parameterList.setOrder(new String[]{"channelTypes.id"});

        return dataService.getListOption(Channel.class, parameterList, true);
    }

    @Override
    public MsalesResults<Channel> searchChannel(int channelTypeId, int companyId, String textSearch, MsalesPageRequest page) {
        ParameterList parameterList = new ParameterList("companys.id", companyId, page.getPageNo(), page.getRecordsInPage());
        parameterList.setOrder(new String[]{"channelTypes.id"});

        if (channelTypeId > 0) {
            parameterList.add("channelTypes.id", channelTypeId);
        }
        if (!textSearch.isEmpty() && !textSearch.trim().isEmpty()) {
            parameterList.or("fullCode", textSearch, "like", "name", textSearch, "like");
        }

        return dataService.getListOption(Channel.class, parameterList, true);
    }

    @Override
    public List<ChannelType> getChannelRegion(int companyId) {
        String hql = "FROM ChannelType WHERE parents.id IS NULL AND companys.id = " + companyId;
        List<ChannelType> list = dataService.executeSelectHQL(ChannelType.class, hql, false, 0, 0);

        return list;
    }

    @Override
    public MsalesResults<Channel> getListChannelByParent(int parentId, int companyId) {
        //get List Channel by parentId id
        ParameterList parameterList = new ParameterList("parents.id", parentId, 0, 0);
        parameterList.add("companys.id", companyId);
        parameterList.setOrder("createdAt", "DESC");
        MsalesResults<Channel> list = dataService.getListOption(Channel.class, parameterList, true);

        return list;
    }

    @Override
    public List<Channel> checkFullCodeChannelParent(String fullCode, int companyId) {
        String hql = "FROM Channel WHERE fullCode = '" + fullCode + "' AND companys.id = " + companyId;
        List<Channel> listFound = dataService.executeSelectHQL(Channel.class, hql, false, 1, 1);
        return listFound;
    }

    @Override
    public Channel getChannelByFullCode(String fullCode, int companyId, DataService dataService) {
        String hql = "FROM Channel"
                + " WHERE deletedUser = 0"
                + " AND fullCode = :fullCode"
                + " AND companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("fullCode", fullCode.trim()));
        List<Channel> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Channel getChannelByCode(String code, int companyId, DataService dataService) {
        String hql = "FROM Channel"
                + " WHERE deletedUser = 0"
                + " AND code = :code"
                + " AND companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("code", code.trim()));
        List<Channel> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public ChannelType getChannelTypeByCode(String code, int companyId, DataService dataService) {
        String hql = "FROM ChannelType"
                + " WHERE deletedUser = 0"
                + " AND code = :code"
                + " AND companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("code", code.trim()));
        List<ChannelType> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Goods getGoodsByCode(String code, int companyId, DataService dataService) {
        String hql = "FROM Goods"
                + " WHERE deletedUser = 0"
                + " AND goodsCode = :code"
                + " AND goodsCategorys.companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("code", code.trim()));
        List<Goods> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Status> getChannelStatus(DataService dataService) {
        String hql = "FROM Status"
                + " WHERE deletedUser = 0"
                + " AND statusTypes.id = 5";//trang thai channel
        List<MsalesParameter> parameters = new ArrayList<>();
        List<Status> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        return list;
    }

    @Override
    public List<ChannelLocation> getListChannelLocation(int channelId, DataService dataService) {
        ParameterList param = new ParameterList(0, 0);
        param.add("channels.id", channelId);
        List<ChannelLocation> list = dataService.getListOption(ChannelLocation.class, param);
        return list;
    }

    @Override
    public List<GoodsChannelFocus> getListGoodsChannelFocus(int channelId, DataService dataService) {
        ParameterList param = new ParameterList(0, 0);
        param.add("channels.id", channelId);
        List<GoodsChannelFocus> list = dataService.getListOption(GoodsChannelFocus.class, param);
        return list;
    }

    @Override
    public String checkChannelRelation(int companyId, int channelId, DataService dataService) {
        String error = "";
        //get List Channel by parentId id
        try {
            ParameterList paramChan = new ParameterList("parents.id", channelId, 1, 1);
            paramChan.add("companys.id", companyId);
            List<Channel> listChannel = dataService.getListOption(Channel.class, paramChan);

            String hqlUserRoleChannel = "SELECT id"
                    + " FROM UserRoleChannel"
                    + " WHERE deletedUser = 0"
                    + " AND channels.id = :channelId"
                    + " AND channels.companys.id = :companyId";
            List<MsalesParameter> parameters = MsalesParameter.createList("channelId", channelId);
            parameters.add(MsalesParameter.create("companyId", companyId));
            List<Integer> listUserRoleChannel = dataService.executeSelectHQL(hqlUserRoleChannel, parameters, false, 1, 1);

            String hqlPOS = "SELECT id"
                    + " FROM POS"
                    + " WHERE deletedUser = 0"
                    + " AND channels.id = :channelId"
                    + " AND channels.companys.id = :companyId";
            List<Integer> listPOS = dataService.executeSelectHQL(hqlPOS, parameters, false, 1, 1);
            if (!listChannel.isEmpty()) {
                error += "Kênh con, ";
            }
            if (!listUserRoleChannel.isEmpty()) {
                error += "Nhân viên, ";
            }
            if (!listPOS.isEmpty()) {
                error += "Điểm bán hàng, ";
            }
            if (!error.isEmpty()) {
                error = error.substring(0, error.length() - 2);
            }
        } catch (Exception e) {
            //System.err.println(">>>checkChannelRelation: " + e.getMessage());

        }
        return error;
    }

    @Override
    public Location getCityByCode(String code, DataService dataService) {
        String hql = "FROM Location"
                + " WHERE deletedUser = 0"
                + " AND locationType = 1"
                + " AND code = :code";
        List<MsalesParameter> parameters = MsalesParameter.createList("code", code);
        List<Location> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        return list.isEmpty() ? null : list.get(0);
    }
        
    @Override
    public Location getDistrictByCode(String code, DataService dataService){
        String hql = "FROM Location"
                + " WHERE deletedUser = 0"
                + " AND locationType = 2"
                + " AND code = :code";
        List<MsalesParameter> parameters = MsalesParameter.createList("code", code);
        List<Location> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Location getDistrictByCode(String code, int parentId, DataService dataService) {
        String hql = "FROM Location"
                + " WHERE deletedUser = 0"
                + " AND locationType = 2"
                + " AND parents.id = :parentId"
                + " AND code = :code";
        List<MsalesParameter> parameters = MsalesParameter.createList("code", code);
        parameters.add(MsalesParameter.create("parentId", parentId));
        List<Location> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Location getWardByCode(String code, int parentId, DataService dataService) {
        String hql = "FROM Location"
                + " WHERE deletedUser = 0"
                + " AND locationType = 3"
                + " AND parents.id = :parentId"
                + " AND code = :code";
        List<MsalesParameter> parameters = MsalesParameter.createList("code", code);
        parameters.add(MsalesParameter.create("parentId", parentId));
        List<Location> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        return list.isEmpty() ? null : list.get(0);
    }

}
