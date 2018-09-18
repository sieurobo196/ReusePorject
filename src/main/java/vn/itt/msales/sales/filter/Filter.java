/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.sales.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author vtm
 */
public class Filter {

    //time selector
    private String startDateString;
    private String endDateString;
    private String searchText;

    private List<Integer> channelIdPOST;

    //user 
    private Integer locationId;

    private Integer channelId;

    private List<Integer> channelIdList;
    private List<List<OptionItem>> channelList;

    private Integer userId;

    private Integer statusId;

    private Integer role;
    private boolean locked;
    private boolean inActive;
    private Integer activeId;

    private Integer fromHour;
    private Integer toHour;
    private Integer fromMin;
    private Integer toMin;

    //set role cb nhan vien muon lay
    private Integer[] roles;

    private List<OptionItem> userList = new ArrayList<>();

    private List<OptionItem> channelTree;

    public Filter() {
    }

    public Integer[] getRoles() {
        return roles;
    }

    public void setRoles(Integer[] roles) {
        this.roles = roles;
    }

    public String getRolesString() {
        String ret = "";
        if (roles != null) {
            for (int id : roles) {
                ret += id + ";";
            }
        }
        if (roles != null && roles.length > 0) {
            ret += "-1";
        }
        return ret;
    }

    public String getStartDateString() {
        return startDateString;
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
    }

    public String getEndDateString() {
        return endDateString;
    }

    public void setEndDateString(String endDateString) {
        this.endDateString = endDateString;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isInActive() {
        return inActive;
    }

    public void setInActive(boolean inActive) {
        this.inActive = inActive;
    }

    public Integer getActiveId() {
        return activeId;
    }

    public void setActiveId(Integer activeId) {
        this.activeId = activeId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public List<Integer> getChannelIdList() {
        return channelIdList;
    }

    public void setChannelIdList(List<Integer> channelIdList) {
        this.channelIdList = channelIdList;
    }

    public List<List<OptionItem>> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<List<OptionItem>> channelList) {
        this.channelList = channelList;
    }

    public Integer getFromHour() {
        return fromHour;
    }

    public void setFromHour(Integer fromHour) {
        this.fromHour = fromHour;
    }

    public Integer getToHour() {
        return toHour;
    }

    public void setToHour(Integer toHour) {
        this.toHour = toHour;
    }

    public Integer getFromMin() {
        return fromMin;
    }

    public void setFromMin(Integer fromMin) {
        this.fromMin = fromMin;
    }

    public Integer getToMin() {
        return toMin;
    }

    public void setToMin(Integer toMin) {
        this.toMin = toMin;
    }

    public List<OptionItem> getUserList() {
        return userList;
    }

    public void setUserList(List<OptionItem> userList) {
        this.userList = userList;
    }

    public List<Integer> getChannelIdPOST() {
        return channelIdPOST;
    }

    public void setChannelIdPOST(List<Integer> channelIdPOST) {
        this.channelIdPOST = channelIdPOST;
    }

    public Integer getIdChannel() {
        Integer id = 0;
        if (channelIdList != null) {
            for (int i = channelIdList.size() - 1; i > -1; i--) {
                if (channelIdList.get(i) != null && channelIdList.get(i) != 0) {
                    id = channelIdList.get(i);
                    break;
                }
            }
        } else {
            id = 0;
        }
        return id;
    }

    public Integer getlevel() {
        Integer level = 0;
        if (channelIdList != null) {
            for (int i = channelIdList.size() - 1; i > -1; i--) {
                if (channelIdList.get(i) != null && channelIdList.get(i) != 0) {
                    level = i + 1;
                    break;
                }
            }
        }
        return level;
    }

    public Integer getChannelIdByLevel(int level) {
        Integer id = 0;
        if (channelIdList != null) {
            if (level > 0 && level <= channelIdList.size()) {
                id = channelIdList.get(level - 1);
            }
        }
        return id;
    }

    public static int calulatorMaxPages(int count, int size) {
        if (count % size != 0) {
            return count / size + 1;
        } else {
            if (count / size < 1) {
                return 1;
            } else {
                return count / size;
            }
        }
    }

    public List<OptionItem> getChannelTree() {
        return channelTree;
    }

    public void setChannelTree(List<OptionItem> channelTree) {
        this.channelTree = channelTree;
    }

    public static ArrayList<OptionItem> createFirstOptionType(OptionItem optionItem) {
        //lay loai channel
        ArrayList<OptionItem> list = new ArrayList<>();
        OptionItem option = new OptionItem(0, optionItem.getName());
        list.add(option);
        return list;
    }

    public MsalesResults<Channel> processChannelSystem(Model uiModel, HttpServletRequest request,
            ServiceFilter serviceFilter,
            DataService dataService,
            int companyId, int channelId, int page, int size) {

        if (channelList == null) {
            channelList = new ArrayList<>();
        }
        if (channelIdList == null) {
            channelIdList = new ArrayList<>();
        }
        Channel channel = dataService.getRowById(channelId, Channel.class);

        channelTree = new ArrayList<>();
        while (channel != null) {
            channelIdList.add(0, channel.getId());
            channelTree.add(0, new OptionItem(channel.getId(), channel.getName(), false));//tao menu cho He thong kenh quan ly
            channel = channel.getParents();
        }

        return processChannelSystem(uiModel, request, serviceFilter, dataService, companyId, page, size);
    }

    public MsalesResults<Channel> processChannelSystem(Model uiModel, HttpServletRequest request,
            ServiceFilter serviceFilter,
            DataService dataService,
            int companyId, int page, int size) {
        if (channelList == null) {
            channelList = new ArrayList<>();
        }
        if (channelIdList == null) {
            channelIdList = new ArrayList<>();
        }

        uiModel.addAttribute("isAdminCompany", false);
        MsalesResults<Channel> ret = new MsalesResults<>(new ArrayList<Channel>(), 0L);

        //get login user
        int userLoginId = LoginContext.getLogin(request).getId();

        //bien xac dinh kenh chon la kenh nam trước kenh Max
        boolean isMax = false;

        OptionItem optionItem = new OptionItem();
        int idx = 1;

        int level = this.getlevel();
        //neu la admin companyes    
        MsalesLoginUserInf loginUser = LoginContext.getLogin(request);
        if (loginUser.getUserRoleCode().toUpperCase().equals("USER_ROLE_ADMIN_COMPANY")) {
            OptionItem firstOptionItem = serviceFilter.getFirstChannelType(companyId, dataService);
            List<OptionItem> channelFirstList = Filter.createFirstOptionType(firstOptionItem);
            channelFirstList.addAll(serviceFilter.getCbListChannelByChannelType(firstOptionItem.getId(), companyId, dataService));
            channelList.add(channelFirstList);
            optionItem = firstOptionItem;
            uiModel.addAttribute("isAdminCompany", true);
        } else {
            List<Channel> channelMaxs = serviceFilter.getMaxChannels(userLoginId, dataService);
            if (channelMaxs.isEmpty()) {
                //user not have channel
                List<OptionItem> tempList = new ArrayList<>();
                tempList.add(new OptionItem(0, "-- Chọn kênh --"));
                channelList.add(0, tempList);
                return ret;
            }

            //this.maxChannelId = channelMax.getId();
            Channel temp = channelMaxs.get(0).getParents();
            boolean needTree = false;
            if (channelTree == null) {
                channelTree = new ArrayList<>();
                needTree = true;
            }

            while (temp != null) {
                if (!isMax && getIdChannel() != null && getIdChannel() != 0) {
                    if (Objects.equals(temp.getId(), getIdChannel())) {
                        isMax = true;
                    }
                }

                List<OptionItem> tempList = new ArrayList<>();
                OptionItem item = new OptionItem(temp.getId(), temp.getName());
                tempList.add(item);

                if (needTree) {
                    item.setChecked(true);
                    channelTree.add(0, item);//add menu cho he thong kenh quna ly
                } else {
                    //kiem tra xem channel nay co nam trong channelTree ko
                    for (OptionItem option : channelTree) {
                        if (Objects.equals(option.getId(), temp.getId())) {
                            option.setChecked(true);//ko click
                            break;
                        }
                    }
                }

                channelList.add(0, tempList);
                temp = temp.getParents();
                idx++;
            }

            List<OptionItem> channelFirstList = new ArrayList<>();
            OptionItem firstOptionItem = new OptionItem();
            firstOptionItem.setId(0);
            firstOptionItem.setName("-- " + channelMaxs.get(0).getChannelTypes().getName() + " --");
            channelFirstList.add(firstOptionItem);
            for (Channel channel : channelMaxs) {
                firstOptionItem = new OptionItem(channel.getId(), channel.getName());
                channelFirstList.add(firstOptionItem);
            }

            int firstType = channelMaxs.get(0).getChannelTypes().getId();
            channelList.add(channelFirstList);
            //add channelList child
            OptionItem firstOptionType = new OptionItem(firstType, channelMaxs.get(0).getChannelTypes().getName());
            optionItem = firstOptionType;
        }

        optionItem = serviceFilter.getChannelTypeByParent(optionItem.getId(), dataService);
        while (optionItem != null) {
            List<OptionItem> list = createFirstOptionType(optionItem);
            if (level >= idx) {
                if (channelIdList != null && channelIdList.get(idx - 1) != null) {//truong hop chon Mien 0 
                    list.addAll(serviceFilter.getCbListChannelByParentId(channelIdList.get(idx - 1), dataService));
                }
            }
            channelList.add(list);
            optionItem = serviceFilter.getChannelTypeByParent(optionItem.getId(), dataService);
            idx++;
        }

        for (int i = channelList.size() - 1; i >= 0; i--) {
            if (channelList.get(i).size() > 1 && level <= i) {
                ret = serviceFilter.getListChannelByOptionItemList(channelList.get(i), page, size, dataService);
                OptionItem item = channelList.get(i).get(0);
                channelList.get(i).clear();
                channelList.get(i).add(item);
                for (Channel channel : ret.getContentList()) {
                    item = new OptionItem(channel.getId(), channel.getName());
                    channelList.get(i).add(item);
                }
                break;
            }
        }

        uiModel.addAttribute("channelList", channelList);
        uiModel.addAttribute("channelIdList", channelIdList);
        return ret;
    }

    public List<OptionItem> process(Model uiModel, HttpServletRequest request,
            ServiceFilter serviceFilter,
            DataService dataService,
            int companyId, boolean notWork) {
        if (channelList == null) {
            channelList = new ArrayList<>();
        }
        if (channelIdList == null) {
            channelIdList = new ArrayList<>();
        }

        //get login user
        int userLoginId = LoginContext.getLogin(request).getId();

        //bien xac dinh kenh chon la kenh nam trước kenh Max
        boolean isMax = false;

        OptionItem optionItem = new OptionItem();
        int idx = 1;
        int level = this.getlevel();
        //neu la admin company
        if (LoginContext.getLogin(request).getUserRoleCode().toUpperCase().equals("USER_ROLE_ADMIN_COMPANY")) {
            OptionItem firstOptionItem = serviceFilter.getFirstChannelType(companyId, dataService);
            List<OptionItem> channelFirstList = Filter.createFirstOptionType(firstOptionItem);
            channelFirstList.addAll(serviceFilter.getCbListChannelByChannelType(firstOptionItem.getId(), companyId, dataService));
            channelList.add(channelFirstList);
            optionItem = firstOptionItem;
        } else {
            List<Channel> channelMaxs = serviceFilter.getMaxChannels(userLoginId, dataService);
            if (channelMaxs.isEmpty()) {
                //user not have channel
                List<OptionItem> tempList = new ArrayList<>();
                tempList.add(new OptionItem(0, "-- Chọn kênh --"));
                channelList.add(0, tempList);
                return new ArrayList();
            }

            //this.maxChannelId = channelMax.getId();
            Channel temp = channelMaxs.get(0).getParents();
            while (temp != null) {
                if (!isMax && getIdChannel() != null && getIdChannel() != 0) {
                    if (Objects.equals(temp.getId(), getIdChannel())) {
                        isMax = true;
                    }
                }
                List<OptionItem> tempList = new ArrayList<>();
                tempList.add(new OptionItem(temp.getId(), temp.getName()));
                channelList.add(0, tempList);
                temp = temp.getParents();
                idx++;
            }

            List<OptionItem> channelFirstList = new ArrayList<>();
            OptionItem firstOptionItem = new OptionItem();
            firstOptionItem.setId(0);
            firstOptionItem.setName("-- " + channelMaxs.get(0).getChannelTypes().getName() + " --");
            channelFirstList.add(firstOptionItem);
            for (Channel channel : channelMaxs) {
                firstOptionItem = new OptionItem(channel.getId(), channel.getName());
                channelFirstList.add(firstOptionItem);
            }

            int firstType = channelMaxs.get(0).getChannelTypes().getId();
            channelList.add(channelFirstList);
            //add channelList child
            OptionItem firstOptionType = new OptionItem(firstType, channelMaxs.get(0).getChannelTypes().getName());
            optionItem = firstOptionType;
        }

        optionItem = serviceFilter.getChannelTypeByParent(optionItem.getId(), dataService);
        while (optionItem != null) {
            List<OptionItem> list = Filter.createFirstOptionType(optionItem);
            if (level >= idx) {
                if (channelIdList != null && channelIdList.get(idx - 1) != null) {//truong hop chon Mien 0 
                    list.addAll(serviceFilter.getCbListChannelByParentId(channelIdList.get(idx - 1), dataService));
                }
            }
            channelList.add(list);
            optionItem = serviceFilter.getChannelTypeByParent(optionItem.getId(), dataService);
            idx++;
        }

        List<OptionItem> provinceList = OptionItem.NoOptionList(0, "-- Tỉnh/Thành phố --");
        List<OptionItem> employerList = OptionItem.NoOptionList(0, "-- Nhân viên --");

        //getLocationByChannelId
        //lay danh sach tinh thanh
        provinceList.addAll(serviceFilter.getCbListLocationByType(1, dataService));
        //get employeeList by channelId

        if (getIdChannel() == null || getIdChannel() == 0) {
            if (notWork) {
                if (LoginContext.getLogin(request).getUserRoleCode().toUpperCase().equals("USER_ROLE_ADMIN_COMPANY")) {
                    employerList.addAll(serviceFilter.getCbListFullUserByChannel(0, companyId, LoginContext.getLogin(request).getId(), roles, dataService));
                } else {
                    employerList.addAll(serviceFilter.getCbListFullUserByChannelOptionList(LoginContext.getLogin(request).getChannelList(), companyId, LoginContext.getLogin(request).getId(), roles, dataService));
                }
            } else {
                if (LoginContext.getLogin(request).getUserRoleCode().toUpperCase().equals("USER_ROLE_ADMIN_COMPANY")) {
                    employerList.addAll(serviceFilter.getCbListUserByChannel(0, companyId, LoginContext.getLogin(request).getId(), roles, dataService));
                } else {
                    employerList.addAll(serviceFilter.getCbListUserByChannelOptionList(LoginContext.getLogin(request).getChannelList(), companyId, LoginContext.getLogin(request).getId(), roles, dataService));
                }
            }
        } else {
            if (notWork) {
                if (!isMax) {
                    employerList.addAll(serviceFilter.getCbListFullUserByChannel(getIdChannel(), companyId, LoginContext.getLogin(request).getId(), roles, dataService));
                } else {
                    employerList.addAll(serviceFilter.getCbListFullUserByChannelOptionList(LoginContext.getLogin(request).getChannelList(), companyId, LoginContext.getLogin(request).getId(), roles, dataService));
                }
            } else {
                if (!isMax) {
                    employerList.addAll(serviceFilter.getCbListUserByChannel(getIdChannel(), companyId, LoginContext.getLogin(request).getId(), roles, dataService));
                } else {
                    employerList.addAll(serviceFilter.getCbListUserByChannelOptionList(LoginContext.getLogin(request).getChannelList(), companyId, LoginContext.getLogin(request).getId(), roles, dataService));
                }
            }
        }

        userList.addAll(employerList);
        userList.remove(0);//xoa dau muc
        uiModel.addAttribute("channelList", channelList);
        uiModel.addAttribute("channelIdList", channelIdList);
        uiModel.addAttribute("provinceList", provinceList);
        uiModel.addAttribute("employeeList", employerList);
        uiModel.addAttribute("roles", getRolesString());

        return employerList;
    }

    public List<OptionItem> processFilter(Model uiModel, HttpServletRequest request,
            ServiceFilter serviceFilter,
            DataService dataService,
            int companyId) {
        return process(uiModel, request, serviceFilter, dataService, companyId, false);
    }

    public List<OptionItem> processFilter(Model uiModel, HttpServletRequest request,
            ServiceFilter serviceFilter,
            DataService dataService,
            int companyId, boolean notWork) {
        return process(uiModel, request, serviceFilter, dataService, companyId, notWork);
    }

    //init channel select from request
    public static Filter processChannel(Model uiModel, HttpServletRequest request,
            ServiceFilter serviceFilter,
            DataService dataService,
            int companyId) {
        Filter filter = new Filter();
        if (filter.channelList == null) {
            filter.channelList = new ArrayList<>();
        }
        if (filter.channelIdList == null) {
            filter.channelIdList = new ArrayList<>();
        }
        for (int i = 0;; i++) {
            try {
                String strId = request.getParameter("channelIdList[" + i + "]");
                if (strId == null) {
                    break;
                }
                int id = Integer.parseInt(strId);
                filter.channelIdList.add(id);
            } catch (Exception ex) {
                break;
            }
        }
        filter.processFilter(uiModel, request, serviceFilter, dataService, companyId);
        return filter;
    }

    public static Filter processChannel(Model uiModel, HttpServletRequest request,
            ServiceFilter serviceFilter,
            DataService dataService,
            int companyId, int channelId) {

        Filter filter = new Filter();

        if (filter.channelList == null) {
            filter.channelList = new ArrayList<>();
        }
        if (filter.channelIdList == null) {
            filter.channelIdList = new ArrayList<>();
        }
        Channel channel = dataService.getRowById(channelId, Channel.class);

        while (channel != null) {
            filter.channelIdList.add(0, channel.getId());
            channel = channel.getParents();
        }
        filter.processFilter(uiModel, request, serviceFilter, dataService, companyId);
        return filter;
    }

    public static Filter processChannel(Model uiModel, HttpServletRequest request,
            ServiceFilter serviceFilter,
            DataService dataService,
            int companyId, int channelId, boolean notWork) {

        Filter filter = new Filter();

        if (filter.channelList == null) {
            filter.channelList = new ArrayList<>();
        }
        if (filter.channelIdList == null) {
            filter.channelIdList = new ArrayList<>();
        }
        Channel channel = dataService.getRowById(channelId, Channel.class);

        while (channel != null) {
            filter.channelIdList.add(0, channel.getId());
            channel = channel.getParents();
        }
        filter.processFilter(uiModel, request, serviceFilter, dataService, companyId, notWork);
        return filter;
    }

    public List<OptionItem> processFilterSalesMan(Model uiModel, HttpServletRequest request,
            ServiceFilter serviceFilter,
            DataService dataService,
            int companyId, int userRole) {
        if (channelList == null) {
            channelList = new ArrayList<>();
        }
        if (channelIdList == null) {
            channelIdList = new ArrayList<>();
        }

        //get login user
        int userLoginId = LoginContext.getLogin(request).getId();

        OptionItem optionItem = new OptionItem();
        int idx = 1;
        int level = this.getlevel();
        //neu la admin company
        if (LoginContext.getLogin(request).getUserRoleCode().toUpperCase().equals("USER_ROLE_ADMIN_COMPANY")) {
            OptionItem firstOptionItem = serviceFilter.getFirstChannelType(companyId, dataService);
            List<OptionItem> channelFirstList = Filter.createFirstOptionType(firstOptionItem);
            channelFirstList.addAll(serviceFilter.getCbListChannelByChannelType(firstOptionItem.getId(), companyId, dataService));
            channelList.add(channelFirstList);
            optionItem = firstOptionItem;
        } else {
            List<Channel> channelMaxs = serviceFilter.getMaxChannels(userLoginId, dataService);
            if (channelMaxs.isEmpty()) {
                //user not have channel
                List<OptionItem> tempList = new ArrayList<>();
                tempList.add(new OptionItem(0, "-- Chọn kênh --"));
                channelList.add(0, tempList);
                return new ArrayList();
            }

            //this.maxChannelId = channelMax.getId();
            Channel temp = channelMaxs.get(0).getParents();
            while (temp != null) {
                List<OptionItem> tempList = new ArrayList<>();
                tempList.add(new OptionItem(temp.getId(), temp.getName()));
                channelList.add(0, tempList);
                temp = temp.getParents();
                idx++;
            }

            List<OptionItem> channelFirstList = new ArrayList<>();
            for (Channel channel : channelMaxs) {
                OptionItem firstOptionItem = new OptionItem(channel.getId(), channel.getName());
                channelFirstList.add(firstOptionItem);
            }

            int firstType = channelMaxs.get(0).getChannelTypes().getId();

            channelList.add(channelFirstList);

            //add channelList child
            optionItem = serviceFilter.getChannelTypeByParent(firstType, dataService);
            List<OptionItem> childList = Filter.createFirstOptionType(optionItem);
            if (level > idx) {
                childList.addAll(serviceFilter.getCbListChannelByParentId(channelIdList.get(idx - 1), dataService));
            } else {
                childList.addAll(serviceFilter.getCbListChannelByParentId(channelMaxs.get(0).getId(), dataService));
            }
            channelList.add(childList);
            idx++;
        }

        optionItem = serviceFilter.getChannelTypeByParent(optionItem.getId(), dataService);
        while (optionItem != null) {
            List<OptionItem> list = Filter.createFirstOptionType(optionItem);
            if (level >= idx) {
                if (channelIdList != null && channelIdList.get(idx - 1) != null) {//truong hop chon Mien 0 
                    list.addAll(serviceFilter.getCbListChannelByParentId(channelIdList.get(idx - 1), dataService));
                }
            }
            channelList.add(list);
            optionItem = serviceFilter.getChannelTypeByParent(optionItem.getId(), dataService);
            idx++;
        }

        List<OptionItem> provinceList = OptionItem.NoOptionList(0, "-- Tỉnh/Thành phố --");
        List<OptionItem> employerList = OptionItem.NoOptionList(0, "-- Nhân viên --");

        //getLocationByChannelId
        //lay danh sach tinh thanh
        provinceList.addAll(serviceFilter.getCbListLocationByType(1, dataService));
        //get employeeList by channelId

        if (getIdChannel() == null || getIdChannel() == 0) {
            if (LoginContext.getLogin(request).getUserRoleCode().toUpperCase().equals("USER_ROLE_ADMIN_COMPANY")) {
                // employerList.addAll(serviceFilter.getCbListUserByChannel(0, companyId,LoginContext.getLogin(request).getId(), dataService));
                employerList.addAll(serviceFilter.getCbListUserByChannelSalesMan(userRole, 0, companyId, userLoginId, dataService));
            } else {
                // employerList.addAll(serviceFilter.getCbListUserByChannelOptionList(LoginContext.getLogin(request).getChannelList(), companyId,LoginContext.getLogin(request).getId(), dataService));
                employerList.addAll(serviceFilter.getCbListUserByChannelOptionListLTNV(userRole, LoginContext.getLogin(request).getChannelList(), companyId, userLoginId, dataService));
            }
        } else {
            // employerList.addAll(serviceFilter.getCbListUserByChannel(getIdChannel(), companyId,LoginContext.getLogin(request).getId(), dataService));
            employerList.addAll(serviceFilter.getCbListUserByChannelSalesMan(userRole, getIdChannel(), companyId, userLoginId, dataService));
        }

        userList.addAll(employerList);
        userList.remove(0);
        uiModel.addAttribute("channelList", channelList);
        uiModel.addAttribute("channelIdList", channelIdList);
        uiModel.addAttribute("provinceList", provinceList);
        uiModel.addAttribute("employeeList", employerList);

        return employerList;
    }

}
