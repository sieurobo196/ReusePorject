/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.sales.service;

import java.util.List;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelType;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.services.DataService;

/**
 *
 * @author vtm
 */
public interface ServiceFilter {

    public OptionItem getFirstChannelType(int companyId, DataService dataService);

    public OptionItem getChannelTypeByParent(Integer parentId, DataService dataService);

    public List<ChannelType> getListChannelType();

    public List<OptionItem> getCbListChannelByParentId(int parentId, DataService dataService);

    public List<OptionItem> getCbListChannelByListParentId(List<Integer> ids, DataService dataService);

    public List<OptionItem> getCbListChannelByChannelType(int type, int companyId, DataService dataService);

    public List<OptionItem> getCbListUserByChannel(int channelId, int companyId, int userLoginId, Integer[] roles, DataService dataService);

    public List<OptionItem> getCbListFullUserByChannel(int channelId, int companyId, int userLoginId, Integer[] roles, DataService dataService);

    public List<OptionItem> getCbListUserByChannelIdList(List<Integer> ids, int companyId, int userLoginId, Integer[] roles, DataService dataService);

    public List<OptionItem> getCbListFullUserByChannelIdList(List<Integer> ids, int companyId, int userLoginId, Integer[] roles, DataService dataService);

    public List<OptionItem> getCbListUserByChannelOptionList(List<OptionItem> ids, int companyId, int userLoginId, Integer[] roles, DataService dataService);

    public List<OptionItem> getCbListFullUserByChannelOptionList(List<OptionItem> ids, int companyId, int userLoginId, Integer[] roles, DataService dataService);

    public List<OptionItem> getCbListUserByChannelSalesMan(int userRole, int channelId, int companyId, int userLoginId, DataService dataService);

    public List<OptionItem> getCbListUserRole(DataService dataService);

    public List<OptionItem> getCbListLocationByType(int type, DataService dataService);

    public List<OptionItem> getCbListLocationByChannel(int channelId, DataService dataService);
    
    public List<OptionItem> getCbListLocationByListChannelId(List<Integer> ids, DataService dataService);

    public List<Channel> getMaxChannels(int userId, DataService dataService);
    
    public List<Channel> getListChannelByParent(int parentId,int page,int size,DataService dataService);
    public List<Channel> getListChannelByChannelType(int type, int companyId,int page,int size, DataService dataService);
    public MsalesResults<Channel> getListChannelByOptionItemList(List<OptionItem> list,int page,int size,DataService dataService);

    public List<OptionItem> getCbListUserByChannelOptionListLTNV(int userRole, List<OptionItem> ids, int companyId, int userLoginId, DataService dataService);

}
