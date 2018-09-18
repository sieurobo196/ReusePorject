/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.sales.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelType;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.services.DataService;

/**
 *
 * @author vtm
 */
public class ServiceFilterImpl implements ServiceFilter {

    @Autowired
    private DataService dataService;

    @Override
    public OptionItem getFirstChannelType(int companyId, DataService dataService) {
        String hql = "FROM ChannelType"
                + " WHERE deletedUser = 0"
                + " AND parents = NULL"
                + " AND companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        List<ChannelType> channelTypeList = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        if (channelTypeList.isEmpty()) {
            OptionItem optionItem = new OptionItem(0, "-- Chọn kênh --");
            return optionItem;
        } else {
            OptionItem optionItem = new OptionItem(channelTypeList.get(0).getId(), "-- " + channelTypeList.get(0).getName() + " --");
            return optionItem;
        }
    }

    @Override
    public OptionItem getChannelTypeByParent(Integer parentId, DataService dataService) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("parents.id", parentId);
        List<ChannelType> channelTypeList = dataService.getListOption(ChannelType.class, parameterList);
        if (!channelTypeList.isEmpty()) {
            OptionItem optionItem = new OptionItem(channelTypeList.get(0).getId(), "-- " + channelTypeList.get(0).getName() + " --");
            return optionItem;
        } else {
            return null;
        }
    }

    @Override
    public List<ChannelType> getListChannelType() {
        ParameterList parameterList = new ParameterList(0, 0);
        parameterList.setOrder("parents.id");
        List<ChannelType> channelTypeList = dataService.getListOption(ChannelType.class, parameterList);
        return channelTypeList;
    }

    @Override
    public List<OptionItem> getCbListChannelByParentId(int parentId, DataService dataService) {
        String hql = "SELECT id AS id,name AS name,code AS code"
                + " FROM Channel"
                + " WHERE parents.id = :parentId"
                + " AND deletedUser = 0"
                + " AND statuss.id = 10"
                + " ORDER BY name";
        List<MsalesParameter> parameters = MsalesParameter.createList("parentId", parentId);
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        List<OptionItem> ret = new ArrayList<>();
        ret.addAll(OptionItem.createOptionItemListFromHashMap(list));
        return ret;
    }

    @Override
    public List<OptionItem> getCbListChannelByListParentId(List<Integer> ids, DataService dataService) {
        String hql = "SELECT id AS id,name AS name,code AS code"
                + " FROM Channel"
                + " WHERE parents.id IN (:ids)"
                + " AND deletedUser = 0"
                + " AND statuss.id = 10"
                + " ORDER BY name";
        List<MsalesParameter> parameters = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        List<OptionItem> ret = new ArrayList<>();
        ret.addAll(OptionItem.createOptionItemListFromHashMap(list));
        return ret;
    }

    @Override
    public List<OptionItem> getCbListChannelByChannelType(int type, int companyId, DataService dataService) {
        String hql = "SELECT id AS id,name AS name,code AS code"
                + " FROM Channel"
                + " WHERE deletedUser = 0"
                + " AND statuss.id = 10"
                + " AND channelTypes.id = :typeId"
                + " AND companys.id = :companyId"
                + " ORDER BY name";
        List<MsalesParameter> parameters = MsalesParameter.createList("typeId", type);
        parameters.add(MsalesParameter.create("companyId", companyId));
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        List<OptionItem> ret = new ArrayList<>();
        ret.addAll(OptionItem.createOptionItemListFromHashMap(list));
        return ret;
    }

    @Override
    public List<Channel> getListChannelByChannelType(int type, int companyId, int page, int size, DataService dataService) {
        String hql = "FROM Channel"
                + " WHERE deletedUser = 0"
                + " AND statuss.id = 10"
                + " AND channelTypes.id = :typeId"
                + " AND companys.id = :companyId"
                + " ORDER BY name";
        List<MsalesParameter> parameters = MsalesParameter.createList("typeId", type);
        parameters.add(MsalesParameter.create("companyId", companyId));
        return dataService.executeSelectHQL(hql, parameters, true, 0, 0);
    }

    @Override
    public MsalesResults<Channel> getListChannelByOptionItemList(List<OptionItem> list, int page, int size, DataService dataService) {
        String hql = "FROM Channel"
                + " WHERE deletedUser = 0"
                + " AND statuss.id = 10"
                + " AND id IN (:ids)"
                + " ORDER BY name";

        List<MsalesParameter> parameters = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        long count = 0;
        for (OptionItem optionItem : list) {
            if (optionItem.getId() != 0) {
                ids.add(optionItem.getId());
                count++;
            }
        }

        if (!ids.isEmpty()) {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        } else {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        }

        List<Channel> channelList = dataService.executeSelectHQL(hql, parameters, false, page, size);
        MsalesResults<Channel> ret = new MsalesResults<>();
        ret.setContentList(channelList);
        ret.setCount(count);
        return ret;
    }

    @Override
    public List<OptionItem> getCbListUserByChannel(int channelId, int companyId, int userLoginId, Integer[] roles, DataService dataService) {
        List<OptionItem> ret = new ArrayList<>();
        String hql = "SELECT DISTINCT UserRoleChannel.users.id AS id,"
                + " CONCAT('[',UserRoleChannel.users.username,']',' - ',UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name"
                + " FROM UserRoleChannel AS UserRoleChannel"
                + " JOIN UserRoleChannel.users AS User"
                + " WHERE UserRoleChannel.deletedUser = 0"
                + " AND User.deletedUser = 0"
                + " AND User.id != :userLoginId"
                + " AND User.statuss.value = 1"//dang lam viec
                + " AND User.companys.id = :companyId";

        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("userLoginId", userLoginId));

        if (roles != null && roles.length > 0) {
            hql += " AND UserRoleChannel.userRoles.id IN (:roles)";
            parameters.add(MsalesParameter.create("roles", roles, 5));
        }

        if (channelId != 0)//load all
        {
            Channel channel = dataService.getRowById(channelId, Channel.class);
            if (channel == null) {
                return ret;
            }
            parameters.add(MsalesParameter.create("channelCode1", channel.getFullCode()));
            parameters.add(MsalesParameter.create("channelCode2", channel.getFullCode() + "!_%"));

            hql += " AND (UserRoleChannel.channels.fullCode = :channelCode1 OR UserRoleChannel.channels.fullCode LIKE :channelCode2 ESCAPE '!')";
        }
        hql += " ORDER BY name";
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        ret.addAll(OptionItem.createOptionItemListFromHashMap(list));
        return ret;
    }

    @Override
    public List<OptionItem> getCbListFullUserByChannel(int channelId, int companyId, int userLoginId, Integer[] roles, DataService dataService) {
        List<OptionItem> ret = new ArrayList<>();
        String hql = "SELECT DISTINCT UserRoleChannel.users.id AS id,"
                + " CONCAT('[',UserRoleChannel.users.username,']',' - ',UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name"
                + " FROM UserRoleChannel AS UserRoleChannel"
                + " JOIN UserRoleChannel.users AS User"
                + " WHERE UserRoleChannel.deletedUser = 0"
                + " AND User.deletedUser = 0"
                + " AND User.id != :userLoginId"
                + " AND User.companys.id = :companyId";

        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("userLoginId", userLoginId));

        if (roles != null && roles.length > 0) {
            hql += " AND UserRoleChannel.userRoles.id IN (:roles)";
            parameters.add(MsalesParameter.create("roles", roles, 5));
        }

        if (channelId != 0)//load all
        {
            Channel channel = dataService.getRowById(channelId, Channel.class);
            if (channel == null) {
                return ret;
            }
            parameters.add(MsalesParameter.create("channelCode1", channel.getFullCode()));
            parameters.add(MsalesParameter.create("channelCode2", channel.getFullCode() + "!_%"));

            hql += " AND (UserRoleChannel.channels.fullCode = :channelCode1 OR UserRoleChannel.channels.fullCode LIKE :channelCode2 ESCAPE '!')";
        }
        hql += " ORDER BY name";
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        ret.addAll(OptionItem.createOptionItemListFromHashMap(list));
        return ret;
    }

    @Override
    public List<OptionItem> getCbListUserByChannelIdList(List<Integer> ids, int companyId, int userLoginId, Integer[] roles, DataService dataService) {
        List<OptionItem> ret = new ArrayList<>();
        String hql = "SELECT DISTINCT User.id AS id,"
                + " CONCAT('[',UserRoleChannel.users.username,']',' - ',UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name "
                + " FROM UserRoleChannel AS UserRoleChannel"
                + " JOIN UserRoleChannel.users AS User"
                + " WHERE UserRoleChannel.deletedUser = 0"
                + " AND User.deletedUser = 0"
                + " AND User.id != :userLoginId"
                + " AND User.statuss.value = 1"//dang lam viec
                + " AND User.companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("userLoginId", userLoginId));

        if (roles != null && roles.length > 0) {
            hql += " AND UserRoleChannel.userRoles.id IN (:roles)";
            parameters.add(MsalesParameter.create("roles", roles, 5));
        }

        int idx = 1;
        hql += " AND (1 > 1";
        for (int id : ids) {
            Channel channel = dataService.getRowById(id, Channel.class);
            if (channel != null) {
                hql += " OR UserRoleChannel.channels.fullCode = :channelCode" + idx;
                parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode()));
                hql += " OR UserRoleChannel.channels.fullCode LIKE :channelCode" + idx + " ESCAPE '!'";
                parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode() + "!_%"));
            }
        }
        hql += ")";
        hql += " GROUP BY User.id"
                + " ORDER BY name";

        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        ret.addAll(OptionItem.createOptionItemListFromHashMap(list));

        return ret;
    }

    @Override
    public List<OptionItem> getCbListFullUserByChannelIdList(List<Integer> ids, int companyId, int userLoginId, Integer[] roles, DataService dataService) {
        List<OptionItem> ret = new ArrayList<>();
        String hql = "SELECT DISTINCT User.id AS id,"
                + " CONCAT('[',UserRoleChannel.users.username,']',' - ',UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name "
                + " FROM UserRoleChannel AS UserRoleChannel"
                + " JOIN UserRoleChannel.users AS User"
                + " WHERE UserRoleChannel.deletedUser = 0"
                + " AND User.deletedUser = 0"
                + " AND User.id != :userLoginId"
                + " AND User.companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("userLoginId", userLoginId));

        if (roles != null && roles.length > 0) {
            hql += " AND UserRoleChannel.userRoles.id IN (:roles)";
            parameters.add(MsalesParameter.create("roles", roles, 5));
        }

        int idx = 1;
        hql += " AND (1 > 1";
        for (int id : ids) {
            Channel channel = dataService.getRowById(id, Channel.class);
            if (channel != null) {
                hql += " OR UserRoleChannel.channels.fullCode = :channelCode" + idx;
                parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode()));
                hql += " OR UserRoleChannel.channels.fullCode LIKE :channelCode" + idx + " ESCAPE '!'";
                parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode() + "!_%"));
            }
        }
        hql += ")";
        hql += " GROUP BY User.id"
                + " ORDER BY name";

        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        ret.addAll(OptionItem.createOptionItemListFromHashMap(list));

        return ret;
    }

    @Override
    public List<OptionItem> getCbListUserByChannelOptionList(List<OptionItem> ids, int companyId, int userLoginId, Integer[] roles, DataService dataService) {
        List<Integer> list = new ArrayList<>();
        for (OptionItem optionItem : ids) {
            list.add(optionItem.getId());
        }
        return getCbListUserByChannelIdList(list, companyId, userLoginId, roles, dataService);
    }

    @Override
    public List<OptionItem> getCbListFullUserByChannelOptionList(List<OptionItem> ids, int companyId, int userLoginId, Integer[] roles, DataService dataService) {
        List<Integer> list = new ArrayList<>();
        for (OptionItem optionItem : ids) {
            list.add(optionItem.getId());
        }
        return getCbListFullUserByChannelIdList(list, companyId, userLoginId, roles, dataService);
    }

    @Override
    public List<OptionItem> getCbListUserRole(DataService dataService) {
        String hql = "SELECT id AS id,name AS name"
                + " FROM UserRole"
                + " WHERE deletedUser = 0";
        List<HashMap> list = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
        List<OptionItem> ret = new ArrayList<>();
        ret.addAll(OptionItem.createOptionItemListFromHashMap(list));
        return ret;
    }

    @Override
    public List<OptionItem> getCbListLocationByType(int type, DataService dataService) {
        String hql = "SELECT id AS id,name AS name"
                + " FROM Location"
                + " WHERE deletedUser = 0"
                + " AND locationType = :type";
        List<MsalesParameter> parameters = MsalesParameter.createList("type", type);
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        List<OptionItem> ret = new ArrayList<>();
        ret.addAll(OptionItem.createOptionItemListFromHashMap(list));
        return ret;
    }

    @Override
    public List<OptionItem> getCbListLocationByChannel(int channelId, DataService dataService) {
        //get max channel
        Channel channel = dataService.getRowById(channelId, Channel.class);
        int maxChannel = 0;
        if (channel != null) {
            maxChannel = channel.getId();
            while (channel.getParents() != null) {
                maxChannel = channel.getParents().getId();
                channel = channel.getParents();
            }
        }

        String hql = "SELECT locations.id AS id,locations.name AS name"
                + " FROM ChannelLocation"
                + " WHERE deletedUser = 0"
                + " AND locations.deletedUser= 0"
                + " AND locations.locationType = 1"
                + " AND channels.id = :channelId";
        List<MsalesParameter> parameters = MsalesParameter.createList("channelId", maxChannel);
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        return OptionItem.createOptionItemListFromHashMap(list);
    }

    @Override
    public List<OptionItem> getCbListLocationByListChannelId(List<Integer> ids, DataService dataService) {
        String hql = "SELECT DISTINCT locations.id AS id,locations.name AS name"
                + " FROM ChannelLocation"
                + " WHERE deletedUser = 0"
                + " AND locations.deletedUser= 0"
                + " AND locations.locationType = 1"
                + " AND channels.id IN (:ids)";
        List<MsalesParameter> parameters = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("ids"));
        } else {
            parameters.add(MsalesParameter.create("ids", ids, 1));
        }
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        return OptionItem.createOptionItemListFromHashMap(list);
    }

    @Override
    public List<OptionItem> getCbListUserByChannelSalesMan(int userRole, int channelId, int companyId, int userLoginId, DataService dataService) {

        List<OptionItem> ret = new ArrayList<>();
        String hql = "SELECT DISTINCT UserRoleChannel.users.id AS id,"
                + " CONCAT('[',UserRoleChannel.users.username,']',' - ',UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name"
                + " FROM UserRoleChannel AS UserRoleChannel"
                + " WHERE UserRoleChannel.deletedUser = 0"
                + " AND UserRoleChannel.users.deletedUser = 0"
                + " AND UserRoleChannel.userRoles.id = " + userRole
                + " AND UserRoleChannel.users.id != :userLoginId"
                + " AND UserRoleChannel.users.companys.id = :companyId";

        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("userLoginId", userLoginId));
        if (channelId != 0)//load all
        {
            Channel channel = dataService.getRowById(channelId, Channel.class);
            if (channel == null) {
                return ret;
            }
            parameters.add(MsalesParameter.create("channelCode1", channel.getFullCode()));
            parameters.add(MsalesParameter.create("channelCode2", channel.getFullCode() + "!_%"));

            hql += " AND (UserRoleChannel.channels.fullCode = :channelCode1 OR UserRoleChannel.channels.fullCode LIKE :channelCode2 ESCAPE '!')";
        }
        hql += " ORDER BY UserRoleChannel.users.lastName,UserRoleChannel.users.firstName";
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        ret.addAll(OptionItem.createOptionItemListFromHashMap(list));

//         
//        String hql = "SELECT DISTINCT User.id AS id,"
//                + " CONCAT('[',UserRoleChannel.users.userCode,']',' - ',UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name "
//                + " FROM UserRoleChannel AS UserRoleChannel"
//                + " RIGHT JOIN UserRoleChannel.users AS User"
//                + " WHERE UserRoleChannel.deletedUser = 0"
//                + " AND User.deletedUser = 0"
//                + " AND UserRoleChannel.userRoles.id = " + userRole
//                + " AND User.companys.id = :companyId";
//        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
//
//        Channel channel = dataService.getRowById(channelId, Channel.class);
//        if (channel != null) {
//            hql += " AND UserRoleChannel.channels.fullCode LIKE :channelCode";
//            parameters.add(MsalesParameter.create("channelCode", channel.getFullCode() + "%"));
//        }
//        hql += " GROUP BY User.id"
//                + " ORDER BY User.lastName,User.firstName,User.userCode";
//
//        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
//        List<OptionItem> ret = new ArrayList<>();
//        ret.addAll(OptionItem.createOptionItemListFromHashMap(list));
        return ret;

    }

    @Override
    public List<Channel> getMaxChannels(int userId, DataService dataService) {
        List<Channel> ret = new ArrayList<>();
        String hql = "SELECT Channel"
                + " FROM UserRoleChannel AS UserRoleChannel"
                + " JOIN UserRoleChannel.userRoles AS UserRole"
                + " JOIN UserRoleChannel.channels AS Channel"
                + " WHERE UserRoleChannel.deletedUser = 0"
                + " AND UserRole.deletedUser = 0"
                + " AND Channel.deletedUser = 0"
                + " AND UserRoleChannel.users.id = :userId";
        List<MsalesParameter> parameters = MsalesParameter.createList("userId", userId);
        List<Channel> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        if (!list.isEmpty()) {
            String code = list.get(0).getFullCode();
            int level = code.split("_").length;
            ret.add(list.get(0));
            for (int i = 1; i < list.size(); i++) {
                int lvl = list.get(i).getFullCode().split("_").length;
                if (lvl < level) {
                    level = lvl;
                    ret = new ArrayList<>();
                    ret.add(list.get(i));
                } else if (lvl == level) {
                    ret.add(list.get(i));
                }
            }
        }
        return ret;
    }

    @Override
    public List<Channel> getListChannelByParent(int parentId, int page, int size, DataService dataService) {
        String hql = "FROM channel"
                + " WHERE deletedUser = 0"
                + " AND parents.id = :parentId";
        List<MsalesParameter> parameters = MsalesParameter.createList("parentId", parentId);
        return dataService.executeSelectHQL(hql, parameters, false, page, size);
    }

    public List<OptionItem> getCbListUserByChannelOptionListLTNV(int userRole, List<OptionItem> ids, int companyId, int userLoginId, DataService dataService) {
        List<Integer> list = new ArrayList<>();
        for (OptionItem optionItem : ids) {
            list.add(optionItem.getId());
        }
        List<OptionItem> ret = new ArrayList<>();
        String hql = "SELECT DISTINCT User.id AS id,"
                + " CONCAT('[',UserRoleChannel.users.username,']',' - ',UserRoleChannel.users.lastName,' ',UserRoleChannel.users.firstName) AS name "
                + " FROM UserRoleChannel AS UserRoleChannel"
                + " RIGHT JOIN UserRoleChannel.users AS User"
                + " WHERE UserRoleChannel.deletedUser = 0"
                + " AND UserRoleChannel.userRoles.id = " + userRole
                + " AND User.deletedUser = 0"
                + " AND User.id != :userLoginId"
                + " AND User.companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("userLoginId", userLoginId));

        int idx = 1;
        hql += " AND (1 > 1";
        for (int id : list) {
            Channel channel = dataService.getRowById(id, Channel.class);
            if (channel != null) {
                hql += " OR UserRoleChannel.channels.fullCode = :channelCode" + idx;
                parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode()));
                hql += " OR UserRoleChannel.channels.fullCode LIKE :channelCode" + idx + " ESCAPE '!'";
                parameters.add(MsalesParameter.create("channelCode" + idx++, channel.getFullCode() + "!_%"));
            }
        }
        hql += ")";
        hql += " GROUP BY User.id"
                + " ORDER BY User.lastName,User.firstName,User.userCode";

        List<HashMap> lists = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        ret.addAll(OptionItem.createOptionItemListFromHashMap(lists));

        return ret;
    }
}
