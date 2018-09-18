/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.user.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.csb.auth.MsalesSession;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.UserRoleChannel;

/**
 *
 * @author ChinhNQ
 */
public class MsalesLoginUserInf implements Serializable {

    private static final long serialVersionUID = -2009047693666675517L;

    private Integer id;
    private Integer companyId;
    private String username;
    private String firstName;
    private String lastName;
    private String token;

    private int userRoleId;
    private int branch;

    private String companyName;
    private String companyCode;

    private List<OptionItem> channelList = new ArrayList<>();

    private String userRoleName;
    private String userRoleCode;
    
    private Integer packageService;    
    private Integer isRegister;
    private Integer equipmentMax;
    
    private boolean isAdminMaster = false;

    public MsalesLoginUserInf() {
    }

    private List<Channel> getMaxChannels(List<Channel> list) {
        List<Channel> ret = new ArrayList<>();
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

    private List<Channel> getMaxChannelsFromChannelList(List<UserRoleChannel> list) {
        List<Channel> ret = new ArrayList<>();
        if (!list.isEmpty()) {
            String code = list.get(0).getChannels().getFullCode();
            int level = code.split("_").length;
            ret.add(list.get(0).getChannels());
            for (int i = 1; i < list.size(); i++) {
                int lvl = list.get(i).getChannels().getFullCode().split("_").length;
                if (lvl < level) {
                    level = lvl;
                    ret = new ArrayList<>();
                    ret.add(list.get(i).getChannels());
                } else if (lvl == level) {
                    ret.add(list.get(i).getChannels());
                }
            }
        }
        return ret;
    }

    public MsalesLoginUserInf(User user, HttpSession session) {
        this.id = user.getId();
        this.companyId = user.getCompanys().getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.token = MsalesSession.getAccessToken(session.getId());
    }

    public MsalesLoginUserInf(User user, UserRole userRole, List<Channel> channelList, HttpSession session) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.token = MsalesSession.getAccessToken(session.getId());
        this.companyName = user.getCompanys().getName();
        this.companyId = user.getCompanys().getId();
        this.companyCode = user.getCompanys().getCode();
        List<Channel> list = getMaxChannels(channelList);
        for (Channel channel : list) {
            OptionItem optionItem = new OptionItem();
            optionItem.setId(channel.getId());
            optionItem.setCode((channel.getFullCode()));
            optionItem.setName(channel.getName());
            this.channelList.add(optionItem);
        }
        this.userRoleId = userRole.getId();
        this.userRoleName = userRole.getName();
        this.userRoleCode = userRole.getCode();
        
        this.packageService = user.getCompanys().getPackageService();
        this.isRegister = user.getCompanys().getIsRegister();
        this.equipmentMax = user.getCompanys().getEquipmentMax();
    }

    public MsalesLoginUserInf(User user, UserRole userRole, List<Channel> channelList, String token) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.token = token;
        this.companyName = user.getCompanys().getName();
        this.companyId = user.getCompanys().getId();
        this.companyCode = user.getCompanys().getCode();
        List<Channel> list = getMaxChannels(channelList);
        for (Channel channel : list) {
            OptionItem optionItem = new OptionItem();
            optionItem.setId(channel.getId());
            optionItem.setCode((channel.getFullCode()));
            optionItem.setName(channel.getName());
            this.channelList.add(optionItem);
        }
        this.userRoleId = userRole.getId();
        this.userRoleName = userRole.getName();
        this.userRoleCode = userRole.getCode();
        
        this.packageService = user.getCompanys().getPackageService();
        this.isRegister = user.getCompanys().getIsRegister();
        this.equipmentMax = user.getCompanys().getEquipmentMax();
    }

    public MsalesLoginUserInf(User user, UserRole userRole, HttpSession session, List<UserRoleChannel> userRoleChannelList) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.token = MsalesSession.getAccessToken(session.getId());
        this.companyName = user.getCompanys().getName();
        this.companyId = user.getCompanys().getId();
        this.companyCode = user.getCompanys().getCode();
        List<Channel> list = getMaxChannelsFromChannelList(userRoleChannelList);
        for (Channel channel : list) {
            OptionItem optionItem = new OptionItem();
            optionItem.setId(channel.getId());
            optionItem.setCode((channel.getFullCode()));
            optionItem.setName(channel.getName());
            this.channelList.add(optionItem);
        }
        this.userRoleId = userRole.getId();
        this.userRoleName = userRole.getName();
        this.userRoleCode = userRole.getCode();
        
        this.packageService = user.getCompanys().getPackageService();
        this.isRegister = user.getCompanys().getIsRegister();
        this.equipmentMax = user.getCompanys().getEquipmentMax();
    }

    public MsalesLoginUserInf(User user, UserRole userRole, String token, List<UserRoleChannel> userRoleChannelList) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.token = token;
        this.companyName = user.getCompanys().getName();
        this.companyId = user.getCompanys().getId();
        this.companyCode = user.getCompanys().getCode();
        List<Channel> list = getMaxChannelsFromChannelList(userRoleChannelList);
        for (Channel channel : list) {
            OptionItem optionItem = new OptionItem();
            optionItem.setId(channel.getId());
            optionItem.setCode((channel.getFullCode()));
            optionItem.setName(channel.getName());
            this.channelList.add(optionItem);
        }
        this.userRoleId = userRole.getId();
        this.userRoleName = userRole.getName();
        this.userRoleCode = userRole.getCode();
        
        this.packageService = user.getCompanys().getPackageService();
        this.isRegister = user.getCompanys().getIsRegister();
        this.equipmentMax = user.getCompanys().getEquipmentMax();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(int userRoleId) {
        this.userRoleId = userRoleId;
    }

    public int getBranch() {
        return branch;
    }

    public void setBranch(int branch) {
        this.branch = branch;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public List<OptionItem> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<OptionItem> channelList) {
        this.channelList = channelList;
    }

    public String getUserRoleName() {
        return userRoleName;
    }

    public void setUserRoleName(String userRoleName) {
        this.userRoleName = userRoleName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserRoleCode() {
        return userRoleCode;
    }

    public void setUserRoleCode(String userRoleCode) {
        this.userRoleCode = userRoleCode;
    }

    public Integer getPackageService() {
        return packageService;
    }

    public void setPackageService(Integer packageService) {
        this.packageService = packageService;
    }

    public Integer getIsRegister() {
        return isRegister;
    }

    public void setIsRegister(Integer isRegister) {
        this.isRegister = isRegister;
    }

    public Integer getEquipmentMax() {
        return equipmentMax;
    }

    public void setEquipmentMax(Integer equipmentMax) {
        this.equipmentMax = equipmentMax;
    }

    public boolean isIsAdminMaster() {
        return isAdminMaster;
    }

    public void setIsAdminMaster(boolean isAdminMaster) {
        this.isAdminMaster = isAdminMaster;
    }

    
}
