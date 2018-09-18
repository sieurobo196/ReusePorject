package vn.itt.msales.channel.controller;

import com.mysql.jdbc.MysqlDataTruncation;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.api.common.WebGoodsComparator;
import vn.itt.msales.api.common.WebMsalesOptionItem;
import vn.itt.msales.channel.model.MsalesChannelImport;
import vn.itt.msales.channel.model.WebChannelComparationChannelType;
import vn.itt.msales.channel.model.WebChannelDistributionController;
import vn.itt.msales.channel.model.WebChannelLocation;
import vn.itt.msales.channel.services.WebLocationService;
import vn.itt.msales.channel.services.WebMsalesChannelServices;
import vn.itt.msales.common.MsalesDownloadTemplet;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.xls.MsalesExcelConfig;
import vn.itt.msales.common.xls.MsalesReadExcel;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.ChannelLocation;
import vn.itt.msales.entity.ChannelType;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsCategory;
import vn.itt.msales.entity.GoodsChannelFocus;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;
import vn.itt.msales.user.model.URLManager;

@Controller
public class WebMsalesChannelController {

//    @Autowired
//    private MsalesChannelController channelController;
//    @Autowired
//    private MsalesChannelTypeController channelTypeController;
//    @Autowired
//    private MsalesStatusController statusController;
//    @Autowired
//    private MsalesChannelLocationController channelLocationController;
//    @Autowired
//    private MsalesGoodsController goodsController;
    @Autowired
    private WebMsalesChannelServices channelService;
    @Autowired
    private WebLocationService locationService;
    @Autowired
    private ServiceFilter serviceFilter;
    @Autowired
    private DataService dataService;

    private List<Channel> mListChannelDuplicate;
    private List<List<Location>> mListLocation;
    private List<List<Goods>> mListGoods;
    private List<MsalesChannelImport> mListChannelImportUpdate;

    @RequestMapping(value = "/channel/list")
    public String test(Model uiModel, HttpServletRequest request, Filter filter,
            @RequestParam(value = "parent", required = false) Integer parent,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        MsalesResults<Channel> list;
        if (parent != null) {
            uiModel.addAttribute("parent", parent);
            list = filter.processChannelSystem(uiModel, request, serviceFilter, dataService, loginUserInf.getCompanyId(), parent, page, size);
        } else {
            list = filter.processChannelSystem(uiModel, request, serviceFilter, dataService, loginUserInf.getCompanyId(), page, size);
        }

        int maxPages = Filter.calulatorMaxPages(list.getCount().intValue(), size);
        if (page > maxPages) {
            page = maxPages;
        }

        uiModel.addAttribute("list", list.getContentList());
        uiModel.addAttribute("maxPages", maxPages);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("channelTree", filter.getChannelTree());

        return "channel";
    }

//    @RequestMapping(value = "/channel/list")
//    public String channelList(Model uiModel,
//            HttpServletRequest request,
//            @ModelAttribute("channelForm") @Valid WebMsalesChannelType channelForm,
//            @RequestParam(value = "page", required = false) Integer page,
//            @RequestParam(value = "size", required = false) Integer size,
//            @RequestParam(value = "types", required = false) String types) {
//
//        int login = LoginContext.isLogin(request, dataService);
//        if (login == -1) {
//            return LoginContext.redirectLogin();
//        } else if (login == 0 || login == 2) {
//            return LoginContext.notAccess();
//        }
//
//        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
//
//        if (page == null || page < 1) {
//            page = 1;
//        }
//        if (size == null || size < 1) {
//            size = 10;
//        }
//        int maxPages = 1;
//        boolean isAdmin = URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"});
//        boolean isAssistant = URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ASSISTANT"});
//        request.setAttribute("isAdmin", isAdmin);
//        request.setAttribute("isAssistant", isAssistant);
//        request.setAttribute("type", false);
//        try {
//            //fix
//            List<Channel> channelMaxs = serviceFilter.getMaxChannels(loginUserInf.getId(), dataService);
//            if (!channelMaxs.isEmpty()) {
//                //List<Channel> channelParents = getListChannelParent(loginUserInf.getChannelId());
//                List<Channel> channelParents = getListChannelParent(channelMaxs.get(0).getId());
//                request.setAttribute("minLevel", (channelParents != null && !channelParents.isEmpty()) ? channelParents.size() : 0);
//                List<ChannelType> listChannelType = channelService.getChannelRegion(loginUserInf.getCompanyId());
//                if (listChannelType != null && !listChannelType.isEmpty()) {
//                    ParameterList paramList = new ParameterList(page, size);
//                    paramList.add("channelTypes.id", listChannelType.get(0).getId());
//                    paramList.add("companys.id", loginUserInf.getCompanyId());
//                    MsalesResults<Channel> list = channelController.getDataService().getListOption(Channel.class, paramList, true);
//                    maxPages = list.getCount().intValue();
//                    if (maxPages % size != 0) {
//                        maxPages = maxPages / size + 1;
//                    } else {
//                        maxPages = maxPages / size;
//                    }
//
//                    request.setAttribute("memberList", list.getContentList());
//                    request.setAttribute("channelListParent", getListChannelParent(list.getContentList(), channelMaxs));
//
//                    List<WebMsalesOptionItem> listC = new ArrayList<>();
//                    for (Channel channel : list.getContentList()) {
//                        listC.add(new WebMsalesOptionItem(channel.getId(), "[" + channel.getFullCode() + "] - " + channel.getName(), channel.getFullCode()));
//                    }
//                    request.setAttribute("companyAgent", listC);
//
//                    // get list channel type
//                    List<WebMsalesChannelType> webChannelTypelist = new ArrayList<>();
//                    List<WebMsalesOptionItem> listChannelTypeJSON = new ArrayList<>();
//                    List<ChannelType> chanelTypeList = channelService.getListChannelType(loginUserInf.getCompanyId());
//
//                    Collections.sort(chanelTypeList, new WebChannelTypeComparation());
//                    if (isAdmin || isAssistant) {
//                        if (types != null && types.length() > 0) {
//                            String channelParentList[] = types.split("_");
//                            int lent = channelParentList.length;
//                            Collections.reverse(Arrays.asList(channelParentList));
//                            if (isNumber(channelParentList)) {
//                                // add all channel type
//                                for (ChannelType channelType : chanelTypeList) {
//                                    webChannelTypelist.add(new WebMsalesChannelType(0, channelType, null));
//                                }
//                                List<List<Channel>> webChannels = new ArrayList<>();
//                                webChannels.add(list.getContentList());
//                                // get channel parent of the currnet channel login
//                                for (String chanId : channelParentList) {
//                                    MsalesResults<Channel> chan = channelService.getListChannelByParent(Integer.parseInt(chanId), loginUserInf.getCompanyId());
//                                    webChannels.add(chan.getContentList());
//                                }
//                                // insert channel parent
//                                for (int i = 0; i < lent; i++) {
//                                    webChannelTypelist.set(i, new WebMsalesChannelType(Integer.parseInt(channelParentList[i]), chanelTypeList.get(i), webChannels.get(i)));
//                                }
//                                webChannelTypelist.set(lent, new WebMsalesChannelType(0, chanelTypeList.get(lent), webChannels.get(lent)));
//                                request.setAttribute("memberList", webChannels.get(lent));
//                                request.setAttribute("type", true);
//                                request.setAttribute("types", types);
//
//                                maxPages = webChannels.get(lent).size() % size != 0 ? webChannels.get(lent).size() / size + 1 : webChannels.get(lent).size() / size;
//                                if (page > maxPages) {
//                                    page = maxPages;
//                                }
//                                if (page < 1) {
//                                    page = 1;
//                                }
//                                //phan trang
//                                request.setAttribute("memberList", webChannels.get(lent).subList((page - 1) * size, (page * size) > (webChannels.get(lent).size()) ? webChannels.get(lent).size() : page * size));
//
//                            } else {
//                                return "noData";
//                            }
//                        } else {
//                            for (ChannelType channelType : chanelTypeList) {
//                                if (channelType.getParents() == null) {
//                                    webChannelTypelist.add(new WebMsalesChannelType(0, channelType, list.getContentList()));
//                                } else {
//                                    webChannelTypelist.add(new WebMsalesChannelType(0, channelType, null));
//                                }
//                            }
//                        }
//                    } else {
//                        //boolean found;
//                        int index = 0;
//                        // Add channel List for channelType
//                        for (Channel channelParent : channelParents) {
//                            //found = false;
//                            for (ChannelType chanelTypeList1 : chanelTypeList) {
//                                if (channelParent.getChannelTypes().getId() == chanelTypeList1.getId().intValue()) {
//                                    List<Channel> listChan = new ArrayList<>();
//                                    listChan.add(channelParent);
//                                    webChannelTypelist.add(new WebMsalesChannelType(0, chanelTypeList1, listChan));
//                                    //found = true;
//                                    index++;
//                                }
//                            }
//                        }
//
//                        // get all channel member for channel have channel type is lowest.
//                        ParameterList param = new ParameterList(page, size);
//                        param.add("companys.id", loginUserInf.getCompanyId());
//                        param.add("parents.id", channelParents.get(channelParents.size() - 1).getId());
//                        MsalesResults<Channel> channels = channelController.getDataService().getListOption(Channel.class, param, true);
//                        maxPages = channels.getCount().intValue();
//                        if (maxPages % size != 0) {
//                            maxPages = maxPages / size + 1;
//                        } else {
//                            maxPages = maxPages / size;
//                        }
//
//                        request.setAttribute("memberList", channels.getContentList());
//
//                        // Add Null list channel for channel type
//                        for (int i = channelParents.size() - 1; i < chanelTypeList.size(); i++) {
//                            if (i == index) {
//                                webChannelTypelist.add(i, new WebMsalesChannelType(0, chanelTypeList.get(i), channels.getContentList()));
//                            } else if (i == index - 1) {
//                                webChannelTypelist.set(i, new WebMsalesChannelType(0, chanelTypeList.get(i), channelMaxs));
//                            } else {
//                                webChannelTypelist.add(i, new WebMsalesChannelType(0, chanelTypeList.get(i), null));
//                            }
//                        }
//                    }
//
//                    request.setAttribute("chanelTypeList", webChannelTypelist);
//
//                    // get JSON channel Type
//                    for (ChannelType channelType : chanelTypeList) {
//                        listChannelTypeJSON.add(new WebMsalesOptionItem(channelType.getId(), "-- " + channelType.getName() + " --", channelType.getCode()));
//                    }
//                    request.setAttribute("chanelTypeListJSON", MsalesJsonUtils.getJSONFromOject(listChannelTypeJSON));
//                }
//            }
//        } catch (JsonMappingException ex) {
//            Logger.getLogger(WebMsalesChannelController.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException | NumberFormatException ex) {
//            Logger.getLogger(WebMsalesChannelController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        uiModel.addAttribute("page", page);
//        uiModel.addAttribute("size", size);
//        uiModel.addAttribute("maxPages", maxPages);
//        return "channel";
//    }
    @RequestMapping(value = "/channel/create")
    public ModelAndView createChannel(Model uiModel, HttpServletRequest request,
            @RequestParam(value = "parent", required = false) Integer parent,
            RedirectAttributes redirectAttributes,
            @ModelAttribute("channelLocation") WebChannelLocation channelLocation) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        Channel channelParent = null;
        List<ChannelType> channelTypeParentList = null;
        if (parent != null && parent != 0) {
            channelParent = dataService.getRowById(parent, Channel.class);
            if (channelParent == null) {
                return new ModelAndView("noData");
            } else {
                if (channelParent.getLat().intValue() == 0 || (int) channelParent.getLng().intValue() == 0) {
                    channelParent.setLat(BigDecimal.ZERO);
                    channelParent.setLng(BigDecimal.ZERO);
                }
                // get id of channel type 
                int channelTyepPrarentId = channelParent.getChannelTypes().getId();
                // get channe type by parent id
                ParameterList paramChType = new ParameterList();
                paramChType.add("parents.id", channelTyepPrarentId);
                channelTypeParentList = dataService.getListOption(ChannelType.class, paramChType);
                if (channelTypeParentList != null && channelTypeParentList.isEmpty()) {
                    request.setAttribute("channelError", channelParent.getName());
                    return new ModelAndView("createChannelError");
                }
            }
        }

        // get channel parent infor assign to child channel
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            int channelLocationType = channelLocation.getLocationType();

            Channel channelNew = channelLocation.getChannel();
            channelNew.setCreatedUser(LoginContext.getLogin(request).getId());
            channelNew.setCompanyId(LoginContext.getLogin(request).getCompanyId());
            channelNew.setIsSalePoint(0);//FIXME
//            channelNew.setStatusId(10);//FIXME
            if (parent != null && parent != 0) {
                channelNew.setParents(channelParent);
                channelNew.setChannelTypeId(channelTypeParentList.get(0).getId());
                // set full code. Full code = Full code parent + self-Code
                channelNew.setFullCode(channelParent.getFullCode() + "_" + channelNew.getCode());
            } else {
                // get channel type region by company
                List<ChannelType> channelTypeParentRegion = channelService.getChannelRegion(loginUserInf.getCompanyId());
                if (channelTypeParentRegion != null && !channelTypeParentRegion.isEmpty()) {
                    channelNew.setChannelTypeId(channelTypeParentRegion.get(0).getId());
                }
                channelNew.setParents(null);

                // set full code. Full code = Full code parent + self-Code
                channelNew.setFullCode(channelNew.getCode());
            }

            try {
                boolean duplicate = checkChannelDupblicate(channelNew.getFullCode().trim(), loginUserInf);
                if (!duplicate) {
                    int createdChannelId = dataService.insertRow(channelNew);
                    // create channel location
                    if (createdChannelId > 0) {
                        ArrayList<ChannelLocation> insertArray = new ArrayList();
                        List<ChannelLocation> list = channelLocation.getLocationList();
                        if (list != null) {
                            for (ChannelLocation chanLo : list) {
                                if (chanLo.getLocations() != null && chanLo.getLocations().getId() != null) {
                                    chanLo.setChannelId(createdChannelId);
                                    chanLo.setCreatedUser(LoginContext.getLogin(request).getId());
                                    insertArray.add(chanLo);
                                }
                            }
                            dataService.insertArray(insertArray);
                        }
                        // create goods channel focus
                        if (channelLocation.getGoodsChannelFocusList() != null) {
                            if (!channelLocation.getGoodsChannelFocusList().isEmpty()) {
                                ArrayList<GoodsChannelFocus> insertArrayGoodsFocus = new ArrayList();
                                List<GoodsChannelFocus> listGoods = channelLocation.getGoodsChannelFocusList();
                                for (GoodsChannelFocus goodsChannelFocus : listGoods) {
                                    goodsChannelFocus.setChannelId(createdChannelId);
                                    goodsChannelFocus.setCreatedUser(LoginContext.getLogin(request).getId());
                                    insertArrayGoodsFocus.add(goodsChannelFocus);
                                }
                                dataService.insertArray(insertArrayGoodsFocus);
                            }
                        }
                        // create salestock of this channel
                        SalesStock salesStock = new SalesStock();
                        salesStock.setChannelId(createdChannelId);
                        salesStock.setCreatedUser(LoginContext.getLogin(request).getId());
                        salesStock.setStatusId(15);//FIXME: by default
                        dataService.insertRow(salesStock);
                        // set flag edit
                        request.setAttribute("editChannel", true);
                        request.setAttribute("infoMessage", "Tạo kênh thành công.");
                        redirectAttributes.addFlashAttribute("createSuccess", true);
                        if (parent == null || parent == 0) {
                            return new ModelAndView("redirect:/channel/list");
                        } else {
                            return new ModelAndView("redirect:/channel/list?parent=" + parent);
                        }
                    } else {
                        // set flag edit
                        request.setAttribute("createChannel", true);
                        request.setAttribute("errorMessage", "Không thể thêm mới kênh.");
                    }
                } else {
                    request.setAttribute("editChannel", false);
                    request.setAttribute("createChannel", true);
                    request.setAttribute("duplicated", true);
                    request.setAttribute("fullCodeChannel", channelNew.getFullCode());
                    dataCreate(request, channelParent, parent, channelLocation);
                    // init data: status, channel type follow channel parrnet
                    initData(request, channelParent);
                    channelLocation.setChannel(channelNew);
                    channelLocation.setLocationType(channelLocationType);
                    return new ModelAndView("channeledit", "channelLocation", channelLocation);
                }
            } catch (Exception e) {
                if (e.getCause().getCause() instanceof MysqlDataTruncation) {
                    MysqlDataTruncation mysqlTrun = (MysqlDataTruncation) e.getCause().getCause();
                    String meString = mysqlTrun.getMessage().replace("'", "").toLowerCase();
                    if (meString.contains("lat")) {
                        request.setAttribute("errorMessage", "Kinh độ không hợp lệ.");
                    } else if (meString.contains("lng")) {
                        request.setAttribute("errorMessage", "Vĩ độ không hợp lệ.");
                    }
                }
                request.setAttribute("editChannel", false);
                request.setAttribute("createChannel", true);
                dataCreate(request, channelParent, parent, channelLocation);
                // init data: status, channel type follow channel parrnet
                initData(request, channelParent);
                channelLocation.setChannel(channelNew);
                channelLocation.setLocationType(channelLocationType);
                return new ModelAndView("channeledit", "channelLocation", channelLocation);
            }
        } else {
            dataCreate(request, channelParent, parent, channelLocation);
        }

        request.setAttribute("editChannel", false);
        request.setAttribute("createChannel", true);

        // init data: status, channel type follow channel parrnet
        initData(request, channelParent);
        return new ModelAndView("channeledit", "channelLocation", channelLocation);
    }

    private void dataCreate(HttpServletRequest request, Channel channelParent, Integer channelParentId, WebChannelLocation channelLocation) {
        if (channelParentId != null && channelParentId != 0) {
            Channel chanNewFirst = new Channel();
            chanNewFirst.setFullCode(channelParent.getFullCode());
            chanNewFirst.setChannelTypes(channelParent.getChannelTypes());
            chanNewFirst.setParents(channelParent);
            channelLocation.setChannel(chanNewFirst);

            List<ChannelLocation> chaloList = new ArrayList<>();
            List<Location> listLocal = new ArrayList<>();
            List<WebMsalesOptionItem> locationTypes = new ArrayList<>();

            ParameterList pramChanLocal = new ParameterList();
            pramChanLocal.add("channels.id", channelParent.getId());
            chaloList = dataService.getListOption(ChannelLocation.class, pramChanLocal);
            if (chaloList != null && chaloList.size() > 0) {
                Location local = chaloList.get(0).getLocations();
                if (local.getLocationType() == 1) {
                    request.setAttribute("isRegion", true);
                    locationTypes.add(new WebMsalesOptionItem(1, "Tỉnh/Thành phố"));
                    locationTypes.add(new WebMsalesOptionItem(2, "Quận/Huyện"));
                } else {
                    request.setAttribute("isArea", true);
                    locationTypes.add(new WebMsalesOptionItem(1, "Quận/Huyện"));
                }
            }
            for (ChannelLocation channelLocation1 : chaloList) {
                listLocal.add(channelLocation1.getLocations());
            }
            channelLocation.setLocationType(0);
            request.setAttribute("locationTypes", locationTypes);
            request.setAttribute("provinceList", listLocal);
        } else {
            Channel chanNewFirst = new Channel();
            chanNewFirst.setFullCode(null);
            chanNewFirst.setChannelTypeId(1);
            chanNewFirst.setParents(null);

            List<ChannelLocation> chaloList = new ArrayList<>();
            List<Location> listLocal = new ArrayList<>();
            listLocal = locationService.getListLocationByType(1);
            for (Location location : listLocal) {
                ChannelLocation channelLocation1 = new ChannelLocation();
                channelLocation1.setLocations(location);
                chaloList.add(channelLocation1);
            }
            request.setAttribute("provinceList", listLocal);
            request.setAttribute("isRegion", true);
            channelLocation.setLocationType(0);
            List<WebMsalesOptionItem> locationTypes = new ArrayList<>();
            locationTypes.add(new WebMsalesOptionItem(1, "Tỉnh/Thành phố"));
            request.setAttribute("locationTypes", locationTypes);
        }
    }

    @RequestMapping(value = "/channel/edit/{id:[\\d]+}", method = RequestMethod.POST)
    public ModelAndView updateChannel(Model uiModel,
            HttpServletRequest request, @PathVariable(value = "id") int id,
            @ModelAttribute("channelLocation") WebChannelLocation channelLocation) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        Channel channel = channelLocation.getChannel();
        if (channel == null) {
            return new ModelAndView("noData");
        }
        if (channel.getId() > 0) {
            Channel channelFound = dataService.getRowById(id, Channel.class);
            if (channelFound != null) {
                channel.setCompanyId(channelFound.getCompanys().getId());
                channel.setUpdatedUser(channelFound.getUpdatedUser());
                channel.setIsSalePoint(1);
                channel.setFullCode(channelFound.getFullCode());
                channel.setCode(channelFound.getCode());
                channel.setUpdatedAt(new Date());
                channel.setUpdatedUser(LoginContext.getLogin(request).getId());
                channel.setParents(channelFound.getParents());

                String relatedError = "";
                if (channel.getStatuss().getId() == 11 && channelFound.getStatuss().getId() == 10)//khong hoat dong
                {
                    //neu co du lieu phu thuoc
                    relatedError = channelService.checkChannelRelation(channel.getCompanyId(), channel.getId(), dataService);
                }

                if (!relatedError.isEmpty()) {
                    //request.setAttribute("isRelated", true);
                    String error = "Không thể đổi kênh sang trạng thái này khi có dữ liệu phụ thuộc: ";
                    error += relatedError;
                    request.setAttribute("errorMessage", error);
                } else {
                    try {
                        int updated = dataService.updateSync(channel);
                        if (updated > 0) {
                            // create goods channel focus
                            if (channelLocation.getGoodsChannelFocusList() != null) {
                                if (!channelLocation.getGoodsChannelFocusList().isEmpty()) {
                                    List<GoodsChannelFocus> listGoods = channelLocation.getGoodsChannelFocusList();
                                    for (GoodsChannelFocus goodsChannelFocus : listGoods) {
                                        goodsChannelFocus.setChannelId(channelFound.getId());
                                        if (goodsChannelFocus.getCreatedUser() == 0) {
                                            goodsChannelFocus.setCreatedUser(LoginContext.getLogin(request).getId());
                                            dataService.insertRow(goodsChannelFocus);
                                        } else if (goodsChannelFocus.getDeletedUser() == 1) {
                                            goodsChannelFocus.setDeletedUser(LoginContext.getLogin(request).getId());
                                            dataService.deleteSynch(goodsChannelFocus);
                                        }
                                    }
                                }
                            }
                            // update location
                            if (channelLocation.getLocationList() != null) {
                                if (!channelLocation.getLocationList().isEmpty()) {
                                    List<ChannelLocation> listChannelLocal = channelLocation.getLocationList();
                                    for (ChannelLocation cnLocal : listChannelLocal) {
                                        cnLocal.setChannels(channelFound);
                                        if (cnLocal.getCreatedUser() == 0) {
                                            cnLocal.setCreatedUser(LoginContext.getLogin(request).getId());
                                            dataService.insertRow(cnLocal);
                                        } else if (cnLocal.getDeletedUser() == 1) {
                                            cnLocal.setDeletedUser(LoginContext.getLogin(request).getId());
                                            dataService.deleteSynch(cnLocal);
                                        }
                                    }
                                }
                            }
                            //redirectAttributes.addFlashAttribute("updatedSuccess", true);
                            request.setAttribute("infoMessage", "Cập nhật kênh thành công.");
                        }
                    } catch (Exception e) {
                        if (e.getCause() instanceof MysqlDataTruncation) {
                            MysqlDataTruncation mysqlTrun = (MysqlDataTruncation) e.getCause();
//                        if (mysqlTrun.getErrorCode() == 1264) {
                            String meString = mysqlTrun.getMessage().replace("'", "").toLowerCase();
                            if (meString.contains("lat")) {
                                request.setAttribute("errorMessage", "Kinh độ không hợp lệ.");
                            } else if (meString.contains("lng")) {
                                request.setAttribute("errorMessage", "Vĩ độ không hợp lệ.");
                            }
                            //System.err.println(">>>" + e.getMessage());
//                        }
                        }
                    }
                }
            } else {
                //redirectAttributes.addFlashAttribute("updatedSuccess", true);
                request.setAttribute("errorMessage", "Cập nhật kênh không thành công.");
            }

        } else {
            return new ModelAndView("noData");
        }

        request.setAttribute("editChannel", true);
        request.setAttribute("createChannel", false);
        initLocation(request, channelLocation, channel);
        initData(request, channel);
        return new ModelAndView("channeledit", "channelLocation", channelLocation);
    }

    @RequestMapping(value = "/channel/edit/{id:[\\d]+}")
    public ModelAndView getChannel(Model uiModel, HttpServletRequest request,
            @PathVariable(value = "id") int id,
            @ModelAttribute("channelLocation") WebChannelLocation channelLocation) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }
        boolean readOnly = !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"});
        request.setAttribute("readonly", readOnly);
        Channel channel = dataService.getRowById(id, Channel.class);
        if (channel == null) {
            return new ModelAndView("noData");
        }
        if (channel.getLat().intValue() == 0 || (int) channel.getLng().intValue() == 0) {
            channel.setLat(BigDecimal.ZERO);
            channel.setLng(BigDecimal.ZERO);
        }
        channelLocation.setChannel(channel);
        request.setAttribute("editChannel", true);
        request.setAttribute("createChannel", false);
        initLocation(request, channelLocation, channel);
        initData(request, channel);
        return new ModelAndView("channeledit", "channelLocation", channelLocation);
    }

    private void initLocation(HttpServletRequest request, WebChannelLocation channelLocation, Channel channel) throws IOException {

        List<Location> provinceList = new ArrayList<>();
        List<WebMsalesOptionItem> locationTypes = new ArrayList<>();
        // get channel location for channel
        if (channel != null) {
            List<ChannelLocation> channelLocationList = dataService.getListOption(ChannelLocation.class,
                    new ParameterList("channels.id", channel.getId()));
            if (channelLocationList != null && !channelLocationList.isEmpty()) {
                for (ChannelLocation chanL : channelLocationList) {
                    Location location = chanL.getLocations();
                    if (chanL.getLocations() != null && chanL.getLocations().getLocationType() != null) {
                        if (location.getLocationType() == 1) {
                            channelLocation.setIsProvince(1);
                            channelLocation.setLocationType(1);
                            locationTypes.add(new WebMsalesOptionItem(1, "Tỉnh/Thành phố"));
                            break;
                        } else {
                            channelLocation.setIsProvince(0);
                            channelLocation.setLocationType(2);
                            locationTypes.add(new WebMsalesOptionItem(2, "Quận/Huyện"));
                            break;
                        }
                    }
                }
            }
//            else {
//                channelLocation.setLocationType(0);
//                locationTypes.add(new WebMsalesOptionItem(2, "-- Chọn loại địa điểm--"));
//            }

            request.setAttribute("locationTypes", locationTypes);
            channelLocation.setLocationList(channelLocationList);
            // get province from district id
            if (channel.getParents() != null && channel.getChannelTypes().getId() != 1) {
                ParameterList paramch = new ParameterList();
                paramch.add("channels.id", channel.getParents().getId());
                List<ChannelLocation> channelLocationParentList = dataService.getListOption(ChannelLocation.class, paramch);
                // get list province after remove duplicate province
                int parent = 0;
                int location = 0;
                for (ChannelLocation chanL : channelLocationParentList) {
                    for (ChannelLocation cal : channelLocationList) {
                        if (chanL.getLocations().getId().intValue() != cal.getLocations().getId()) {
                            int chanType = chanL.getLocations().getLocationType();
                            if (chanType == 1) {
                                provinceList.add(chanL.getLocations());
                                break;
                            } else if (chanType == 2) {
                                if (parent != chanL.getLocations().getParents().getId()) {
                                    provinceList.add(chanL.getLocations().getParents());
                                    parent = chanL.getLocations().getParents().getId();
                                }
                                break;
                            }
                        }
                    }
                }
                // get povinces parent.
//                ParameterList paramParent = new ParameterList();
//                paramch.add("channels.parents.id", channel.getParents().getId());
//                List<ChannelLocation> chlParent = channelLocationController.getDataService().getListOption(ChannelLocation.class, paramParent);

            } else {
                provinceList = locationService.getListLocationByType(1);
            }

            Iterator<Location> iterLocal = provinceList.iterator();
            while (iterLocal.hasNext()) {
                Location location = iterLocal.next();
                for (ChannelLocation locationAdded : channelLocation.getLocationList()) {
                    if (location.getId().intValue() == locationAdded.getLocations().getId()) {
                        iterLocal.remove();
                        break;
                    }
                }
            }
            request.setAttribute("provinceList", provinceList);

            // get goods focus list
            ParameterList param = new ParameterList("channels.id", channel.getId());
            List<GoodsChannelFocus> goodsChannelFocusesList = dataService.getListOption(GoodsChannelFocus.class, param);
            channelLocation.setGoodsChannelFocusList(goodsChannelFocusesList);
        }

    }

    private void initData(HttpServletRequest request, Channel channel) throws IOException {
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        if (channel != null) {
            ParameterList parameterListChannelType = new ParameterList(0, 0);
            parameterListChannelType.add("parents.id", channel.getChannelTypes().getId(), ">");
            List<ChannelType> listsChannelTYpe = dataService.getListOption(ChannelType.class, parameterListChannelType);
            request.setAttribute("channelTyleList", listsChannelTYpe);
        }

        //get List Status by statusType id
        ParameterList parameterList = new ParameterList("statusTypes.id", 5);
        List<Status> listStatus = dataService.getListOption(Status.class, parameterList);
        request.setAttribute("statusList", listStatus);
        request.setAttribute("channelItem", "");//chanString
        //get list goods
        ParameterList paramGoodsCategory = new ParameterList();
        paramGoodsCategory.add("companys.id", loginUserInf.getCompanyId());
        List<GoodsCategory> listGoodsCategory = dataService.getListOption(GoodsCategory.class, paramGoodsCategory);
        List<Goods> listGoods = new ArrayList<>();
        for (GoodsCategory listGood : listGoodsCategory) {
            ParameterList paramGoods = new ParameterList();
            paramGoods.add("goodsCategorys.id", listGood.getId());
            List<Goods> list = dataService.getListOption(Goods.class, paramGoods);
            for (Goods goods : list) {
                listGoods.add(goods);
            }
        }
        Collections.sort(listGoods, new WebGoodsComparator());
        request.setAttribute("goodsList", listGoods);
    }

    private HttpServletRequest getContentListChannelType(HttpServletRequest request, String jsonRes, String attribuleList) throws IOException {
        MsalesResponse response = MsalesJsonUtils.getObjectFromJSON(jsonRes, MsalesResponse.class);
        if (response.getStatus().getCode() == 200) {
            LinkedHashMap<String, Object> jsonContents = MsalesJsonUtils.getObjectFromJSON(jsonRes, LinkedHashMap.class);
            if (jsonContents != null) {
                LinkedHashMap<String, Object> contenst = (LinkedHashMap) jsonContents.get("contents");
                ArrayList<LinkedHashMap<String, Object>> contensList = (ArrayList) contenst.get("contentList");
                String jsonList = MsalesJsonUtils.getJSONFromOject(contensList);

                ChannelType[] channelTypeList = MsalesJsonUtils.getObjectFromJSON(jsonList, ChannelType[].class);
                request.setAttribute(attribuleList, channelTypeList);
            }
        }
        return request;
    }

    private HttpServletRequest getContentListStatus(HttpServletRequest request, String jsonRes, String attribuleList) throws IOException {
        MsalesResponse response = MsalesJsonUtils.getObjectFromJSON(jsonRes, MsalesResponse.class);
        if (response.getStatus().getCode() == 200) {
            LinkedHashMap<String, Object> jsonContents = MsalesJsonUtils.getObjectFromJSON(jsonRes, LinkedHashMap.class);
            if (jsonContents != null) {
                LinkedHashMap<String, Object> contenst = (LinkedHashMap) jsonContents.get("contents");
                ArrayList<LinkedHashMap<String, Object>> contensList = (ArrayList) contenst.get("contentList");
                String jsonList = MsalesJsonUtils.getJSONFromOject(contensList);

                Status[] channelTypeList = MsalesJsonUtils.getObjectFromJSON(jsonList, Status[].class);
                request.setAttribute(attribuleList, channelTypeList);
            }
        }
        return request;
    }

    public List<List<WebMsalesOptionItem>> getListChannelParent(List<Channel> listChannel, List<Channel> currentChannel) throws IOException {
        List<Channel> ccList = new ArrayList<>();
        for (int i = listChannel.size() - 1; i >= 0; i--) {
            ccList.add(listChannel.get(i));
        }
        //Collections.sort(ccList, new WebChannelComparator());
        List<List<WebMsalesOptionItem>> list = new ArrayList<>();
        for (Channel channel : ccList) {
            List<WebMsalesOptionItem> ls = new ArrayList<>();
            ls.add(new WebMsalesOptionItem(channel.getId(), "[" + channel.getFullCode() + "] - " + channel.getName(), channel.getFullCode()));
            list.add(ls);
        }
        List<WebMsalesOptionItem> curList = new ArrayList<>();
        for (Channel channel : currentChannel) {
            curList.add(new WebMsalesOptionItem(channel.getId(), "[" + channel.getFullCode() + "] - " + channel.getName(), channel.getFullCode()));
        }

        list.add(list.size() - 1, curList);

        return list;
    }

    private List<UserRoleChannel> getListUserRoleChannelByUserId(int userID) {
        // get List userRoleChannel by userId
        ParameterList parameterList = new ParameterList("users.id", userID, 1, 1);
        List<UserRoleChannel> list = dataService.getListOption(UserRoleChannel.class, parameterList);

        return list;
    }

    private String getJSONChannel(Channel channel) throws IOException {
        String[] jssonEgnore = {"companys", "channelTypes", "statuss"};
        String jsonChannel = MsalesJsonUtils.getJSONFromOject(channel, jssonEgnore);
        LinkedHashMap<String, Object> channelHash = MsalesJsonUtils.getObjectFromJSON(jsonChannel, LinkedHashMap.class);
        channelHash.put("companyId", channel.getCompanys().getId());
        channelHash.put("channelTypeId", channel.getChannelTypes().getId());
        channelHash.put("statusId", channel.getStatuss().getId());
        channelHash.put("createdUser", channel.getCreatedUser());
        channelHash.put("parentId", channel.getParents().getId());

        return MsalesJsonUtils.getJSONFromOject(channelHash);
    }

    private String jsonHashChannelLocation(List<Location> locationList, Channel channel, HttpServletRequest request) {
        if (locationList != null) {
            for (Location locationPro : locationList) {
                LinkedHashMap<String, Object> linkedHash = new LinkedHashMap<>();
                linkedHash.put("locationId", locationPro.getId());
                linkedHash.put("channelId", channel.getId());
                linkedHash.put("createdUser", LoginContext.getLogin(request).getId());
            }
        }
        return "";
    }

    @RequestMapping(value = "/channelType/list")
    public String listChannelType(Model uiModel, HttpServletRequest request,
            @ModelAttribute(value = "requestForm") Filter filter) throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        request.setAttribute("only", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"}));

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        ParameterList parameterList = new ParameterList();
        if (filter.getSearchText() != null && !filter.getSearchText().isEmpty() && !filter.getSearchText().trim().isEmpty()) {
            parameterList.or("code", filter.getSearchText(), "like", "name", filter.getSearchText(), "like");
        }
        parameterList.add("companys.id", userInf.getCompanyId());
        parameterList.setOrder("parents.id");
        List<ChannelType> channelTypes = dataService.getListOption(ChannelType.class, parameterList);
        uiModel.addAttribute("channelTypeList", channelTypes);
        return "channelTypeList";
    }

    @RequestMapping(value = "/channelType/view/{id}")
    public String getChannelTypeById(@ModelAttribute(value = "channelTypeForm") ChannelType channelType,
            @PathVariable("id") int id,
            HttpServletRequest request, Model uiModel) {
        //Check đăng nhập
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        request.setAttribute("only", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"}));
        request.setAttribute("readonly", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"}));
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        ChannelType channelType2 = new ChannelType();
        //Lấy channelType

        if (id > 0) {
            channelType2 = dataService.getRowById(id, ChannelType.class);
        }
        List<ChannelType> channelTypesList = new ArrayList<ChannelType>();
        if (channelType2.getParents() != null && channelType2.getParents().getId() != null && channelType2.getParents().getId() > 0) {
            //ChannelType channelType2 = dataService.getRowById(channelType.getParentId(), ChannelType.class);
            channelTypesList.add(channelType2.getParents());

        } else {
            ChannelType channelType3 = new ChannelType();
            channelType3.setId(0);
            channelType3.setName("---Cấp Cha---");
            channelTypesList.add(channelType3);
        }
        request.setAttribute("channelTypeParent", channelTypesList);
//        if (channelType2.getParents() == null) {
//            ChannelType channelType3 = new ChannelType();
//            channelType3.setId(0);
//            channelType3.setName("---Cấp cha---");
//            channelType2.setParents(channelType3);
//        }
//        channelType = channelType2;
//        List<ChannelType> channelTypes = new ArrayList<ChannelType>();
//        channelTypes.add(channelType2.getParents());
//        if (channelType2.getParents().getId() > 0) {
//            ChannelType channelType3 = new ChannelType();
//            channelType3.setId(0);
//            channelType3.setName("---Cấp cha---");
//            channelTypes.add(channelType3);
//        }
//        ParameterList parameterList = new ParameterList();
//        parameterList.add("companys.id", userInf.getCompanyId());
//        parameterList.setOrder("parents.id");
//        List<ChannelType> channelTypeList = dataService.getListOption(ChannelType.class, parameterList);
//        for (ChannelType channelType3 : channelTypeList) {
//            if (channelType3.getId() != channelType2.getParents().getId() && channelType3.getId() != channelType2.getId()) {
//                channelTypes.add(channelType3);
//            }
//        }
        //       request.setAttribute("channelTypeParent", channelTypes);
        request.setAttribute("channelType", channelType2);
        return "channelTypeDetails";
    }

    @RequestMapping(value = "/channelType/view/{id}", method = RequestMethod.POST)
    public String updateChannelType(@ModelAttribute(value = "channelTypeForm") ChannelType channelType,
            @PathVariable("id") int id,
            HttpServletRequest request, Model uiModel) {
        //Check đăng nhập
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        //Khởi tạo dữ liệu trả về
        List<ChannelType> channelTypesList = new ArrayList<ChannelType>();
        if (channelType.getParentId() != null && channelType.getParentId() > 0) {
            ChannelType channelType2 = dataService.getRowById(channelType.getParentId(), ChannelType.class);
            if (channelType2 != null) {
                channelTypesList.add(channelType2);
            }

        } else {
            ChannelType channelType2 = new ChannelType();
            channelType2.setId(0);
            channelType2.setName("---Cấp Cha---");
            channelTypesList.add(channelType2);
        }
        request.setAttribute("channelTypeParent", channelTypesList);
  //      request.setAttribute("channelTypeParent", channelTypes);
        //      request.setAttribute("channelType", channelType);

        //Update
        if (channelType.getParentId() == 0) {
            channelType.setParentId(null);
            channelType.setParents(null);
        }
        channelType.setId(id);
        channelType.setUpdatedUser(userInf.getId());
        channelType.setCompanyId(userInf.getCompanyId());
        request.setAttribute("channelType", channelType);
        int ret = 0;
        try {
            ret = dataService.updateSync(channelType);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Cập nhật loại kênh không thành công!");
            return "channelTypeDetails";
        }
        if (ret > 0) {
            request.setAttribute("infoMessage", "Cập nhật loại kênh thành công!");
        } else {
            request.setAttribute("errorMessage", "Cập nhật loại kênh không thành công!");
            return "channelTypeDetails";
        }
        return "channelTypeDetails";
    }

    @RequestMapping(value = "/channelType/create", method = RequestMethod.GET)
    public String createChannelType(@ModelAttribute(value = "channelTypeForm") ChannelType channelType,
            HttpServletRequest request, Model uiModel) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        intiChannelTypeParent(request, userInf.getCompanyId());

        return "channelTypeCreate";
    }

    @RequestMapping(value = "/channelType/create", method = RequestMethod.POST)
    public String channelTypeCreate(@ModelAttribute(value = "channelTypeForm") ChannelType channelType,
            HttpServletRequest request, Model uiModel, RedirectAttributes redirectAttributes) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        //    intiChannelTypeParent(request, userInf.getCompanyId());
        if (channelType.getParentId() == 0) {
            channelType.setParentId(null);
            channelType.setParents(null);
        }
        channelType.setCompanyId(userInf.getCompanyId());
        channelType.setCreatedUser(userInf.getId());
        request.setAttribute("channelType", channelType);
        int ret = 0;
        try {
            ret = dataService.insertRow(channelType);
        } catch (Exception ex) {
            intiChannelTypeParent(request, userInf.getCompanyId());
            request.setAttribute("errorMessage", "Tạo mới loại kênh không thành công!");
            return "channelTypeCreate";
        }
        if (ret > 0) {
            request.setAttribute("infoMessage", "Tạo mới loại kênh thành công!");
            redirectAttributes.addFlashAttribute("infoMessage", "Tạo mới loại kênh thành công!");
            //  redirectAttributes.addFlashAttribute("edit", true);
            //  redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/channelType/view/" + ret;
        } else {
            intiChannelTypeParent(request, userInf.getCompanyId());
            request.setAttribute("errorMessage", "Tạo mới loại kênh không thành công!");
            return "channelTypeCreate";
        }

//        List<ChannelType> channelTypesList = new ArrayList<ChannelType>();
//        if(channelType.getParentId() != null && channelType.getParentId() > 0){
//        	ChannelType channelType2 = dataService.getRowById(channelType.getParentId(), ChannelType.class);
//        	if(channelType2 != null){
//        		channelTypesList.add(channelType2);
//        	}
//        	
//        }else{
//        	ChannelType channelType2 = new ChannelType();
//        	channelType2.setId(0);
//        	channelType2.setName("---Cấp Cha---");
//        	channelTypesList.add(channelType2);
//        }
//        request.setAttribute("channelTypeParent", channelTypesList);
//        return "channelTypeCreate";
    }

    @Autowired
    View jsonView;

    @RequestMapping(value = "channelType/delete", method = RequestMethod.POST)
    public ModelAndView deleteChannelType(@RequestParam("id") Integer id,
            HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        MsalesLoginUserInf user = LoginContext.getLogin(request);
        String status = "OK";
        ParameterList parameterList = new ParameterList();
        parameterList.add("channelTypes.id", id);
        parameterList.add("companys.id", user.getCompanyId());
        List<Channel> channelTypes = dataService.getListOption(Channel.class, parameterList);
        if (!channelTypes.isEmpty()) {
            status = "NOT OK";
            return new ModelAndView(jsonView, "status", status);
        }
        ChannelType channelType = new ChannelType();
        channelType.setId(id);
        channelType.setDeletedUser(user.getId());

        try {
            int ret = dataService.deleteSynch(channelType);
            if (ret < 0) {
                status = "NOT OK";
            }
        } catch (Exception ex) {
            status = "NOT OK";
        }

        return new ModelAndView(jsonView, "status", status);
    }

    @RequestMapping(value = "/channel/distribution")
    public ModelAndView listDistribution(HttpServletRequest request,
            @ModelAttribute("distributionForm") WebChannelDistributionController distribution,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        // check roles for user access to url
        boolean isAdmin = URLManager.checkIn(loginUserInf.getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"});
        request.setAttribute("readOnly", isAdmin);

        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;

        List<ChannelType> channelTypeList = new ArrayList<>();
        MsalesResults<Channel> liResults = new MsalesResults<>();
        try {
            channelTypeList = channelService.getListChannelType(loginUserInf.getCompanyId());
            // get Channel have user role channel is max

            //fix
            List<Channel> channelMaxs = serviceFilter.getMaxChannels(loginUserInf.getId(), dataService);

            if (!channelMaxs.isEmpty()) {

                if (!isAdmin) {
                    List<Channel> listChannel = new ArrayList<>();
                    for (Channel channelMax : channelMaxs) {
                        List<Integer> channelTypeParent = new ArrayList<>();
                        ChannelType parent = channelMax.getChannelTypes().getParents();
                        while (parent != null) {
                            channelTypeParent.add(parent.getId());
                            parent = parent.getParents();
                        }

                        Iterator<ChannelType> iteratorChanelType = channelTypeList.iterator();
                        while (iteratorChanelType.hasNext()) {
                            ChannelType next = iteratorChanelType.next();
                            for (Integer type : channelTypeParent) {
                                if (type.intValue() == next.getId()) {
                                    iteratorChanelType.remove();
                                    break;
                                }
                            }
                        }
                        // get all channel for company
                        MsalesResults<Channel> list = channelService.searchChannel(channelMax.getFullCode(), channelTypeParent, distribution.getChannelType(),
                                loginUserInf.getCompanyId(),
                                distribution.getTextSearch() == null ? "" : distribution.getTextSearch(),
                                new MsalesPageRequest(page, size));

                        listChannel.addAll(list.getContentList());

                    }
                    Collections.sort(listChannel, new WebChannelComparationChannelType());
                    distribution.setListChannel(listChannel);
                    maxPages = listChannel.size();
                    if (maxPages % size != 0) {
                        maxPages = maxPages / size + 1;
                    } else {
                        maxPages = maxPages / size;
                    }
                } else {
                    // get all channel for company
                    liResults = channelService.searchChannel(distribution.getChannelType(),
                            loginUserInf.getCompanyId(),
                            distribution.getTextSearch() == null ? "" : distribution.getTextSearch(),
                            new MsalesPageRequest(page, size));

                    if (liResults != null) {
                        if (liResults.getContentList() != null) {
                            distribution.setListChannel(liResults.getContentList());
                            maxPages = liResults.getCount().intValue();
                            if (maxPages % size != 0) {
                                maxPages = maxPages / size + 1;
                            } else {
                                maxPages = maxPages / size;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            channelTypeList = new ArrayList<>();
        }

        request.setAttribute("listChannelType", channelTypeList);
        request.setAttribute("page", page);
        request.setAttribute("size", size);
        request.setAttribute("maxPages", maxPages);
        return new ModelAndView("distribution", "distributionForm", distribution);
    }

    public void intiChannelTypeParent(HttpServletRequest request, int companyId) {
        ParameterList parameterList = new ParameterList(1, 1);
        parameterList.add("companys.id", companyId);
        parameterList.setOrder("parents.id", "desc");
        List<ChannelType> channelTypes = dataService.getListOption(ChannelType.class, parameterList);
        List<ChannelType> channelTypesList = new ArrayList<ChannelType>();
        if (channelTypes.isEmpty()) {
            ChannelType channelType2 = new ChannelType();
            channelType2.setId(0);
            channelType2.setName("---Cấp Cha---");
            channelTypesList.add(channelType2);

        } else {
            channelTypesList.add(channelTypes.get(0));
        }
        request.setAttribute("channelTypeParent", channelTypesList);
    }

    private HttpServletRequest checkIn(HttpServletRequest request, String roles[]) {
        boolean readOnly = !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), roles);
        request.setAttribute("readonly", readOnly);

        return request;
    }

    /**
     * Get List channel parent of the channel.
     * <p>
     * @param channelId
     * <p>
     * @return
     */
    private List<Channel> getListChannelParent(int channelId) {
        Channel currentChannelLogin = dataService.getRowById(channelId, Channel.class);
        //channel not null
        List<Channel> channelParentList = new ArrayList();
        if (currentChannelLogin != null) {
            channelParentList.add(currentChannelLogin);
            while (currentChannelLogin.getParents() != null) {
                channelParentList.add(currentChannelLogin.getParents());
                currentChannelLogin = currentChannelLogin.getParents();
            }
        }

        Collections.sort(channelParentList, new WebChannelComparationChannelType());
        return channelParentList;
    }

    private List<ChannelType> getListChannelTypeParent(int channelId) {
        List<Channel> listChanl = getListChannelParent(channelId);
        List<ChannelType> channelTypes = new ArrayList<>();
        for (Channel listChanl1 : listChanl) {
            channelTypes.add(listChanl1.getChannelTypes());
        }
        return channelTypes;
    }

    private boolean isNumber(String[] numberArray) {
        for (String number : numberArray) {
            try {
                Integer.parseInt(number);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Path of the file to be downloaded, relative to application's directory
     */
    private String separator = File.separator;
    private String filePath = String.format("%sdownloads%simports%s", separator, separator, separator);

    @RequestMapping(value = "/channel/download.do")
    public void downloadTemplets(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }

        MsalesDownloadTemplet downloadTemplet = new MsalesDownloadTemplet(filePath, request, response);
        downloadTemplet.getFile("Danh_sach_kenh.xls");
    }

    @RequestMapping(value = "/channel/import", method = RequestMethod.POST)
    private String importChannel(HttpServletRequest request,
            @RequestParam("file_channel") MultipartFile multipartFile,
            @RequestParam(value = "update_duplicated", required = false) boolean isUpdate) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        List<List<Object>> listObj = getListDataFromExel(multipartFile);

        List<MsalesChannelImport> channelImportList = getListChannelImportFromListObj(listObj);
        List<String> requiredList = checkRequired(channelImportList, loginUserInf);

        if (!requiredList.isEmpty()) {
            request.setAttribute("error", true);
            request.setAttribute("requiredError", true);
            request.setAttribute("requiredList", requiredList);
            return "channelImport";
        }

        if (!channelImportList.isEmpty()) {

            List<MsalesChannelImport> validateChannelType = validateChannelTypeCode(channelImportList, loginUserInf);
            List<MsalesChannelImport> validateChannelParent = validateChannelParent(channelImportList, loginUserInf);
            List<MsalesChannelImport> validateLocation = validateLocationCode(channelImportList);
            List<Goods> validateGoods = validateGoods(channelImportList, loginUserInf);
            List<MsalesChannelImport> validateStatus = validateStatus(channelImportList);

            boolean channelTypeEmpty = validateChannelType.isEmpty();
            boolean channelParentEmpty = validateChannelParent.isEmpty();
            boolean locationEmpty = validateLocation.isEmpty();
            boolean goodsEmpty = validateGoods.isEmpty();
            boolean statusEmpty = validateStatus.isEmpty();

            if (!channelParentEmpty || !channelTypeEmpty || !locationEmpty || !goodsEmpty || !statusEmpty) {
                request.setAttribute("channelTypeList", validateChannelType);
                request.setAttribute("channelParentList", validateChannelParent);
                request.setAttribute("locationList", validateLocation);
                request.setAttribute("goodsList", validateGoods);
                request.setAttribute("statusList", validateStatus);

                request.setAttribute("channelTypeCodeError", channelTypeEmpty);
                request.setAttribute("channelParentCodeError", channelParentEmpty);
                request.setAttribute("locationError", locationEmpty);
                request.setAttribute("goodsError", goodsEmpty);
                request.setAttribute("statusError", statusEmpty);

                request.setAttribute("error", true);
                return "channelImport";
            } else {

                request.setAttribute("error", false);
                int insertCount = 0;
                int updateCount = 0;
                List<MsalesChannelImport> duplicateList = new ArrayList<>();

                for (MsalesChannelImport channelImport : channelImportList) {
                    //check insert or update
                    Channel channel = new Channel();
                    String code = channelImport.getChannelCode();
                    if (channelImport.getChannelParent() != null) {
                        code = channelImport.getChannelParent().getFullCode() + "_" + code;
                    }
                    Channel rootChannel = channelService.getChannelByFullCode(code, loginUserInf.getCompanyId(), dataService);

                    if (rootChannel != null) {
                        //update channel
                        updateCount++;
                        duplicateList.add(channelImport);

                        channel.setId(rootChannel.getId());
                        channel.setUpdatedAt(new Date());
                        channel.setUpdatedUser(loginUserInf.getId());
                        channel.setCreatedUser(rootChannel.getCreatedUser());
                        channel.setCreatedAt(rootChannel.getCreatedAt());
                        channel.setDeletedUser(rootChannel.getDeletedUser());

                    } else {
                        insertCount++;
                        channel.setCreatedUser(loginUserInf.getId());
                        channel.setCreatedAt(new Date());
                        channel.setUpdatedUser(0);
                        channel.setDeletedUser(0);
                    }
                    channelImport.setChannel(channel);
                }

                if (!isUpdate) {
                    if (updateCount > 0) {
                        //bao loi
                        request.setAttribute("listDuplicate", duplicateList);
                        request.setAttribute("duplicated", true);
                        request.setAttribute("submited", true);
                        request.setAttribute("updated", false);
                        return "channelImport";
                    }
                }

                //truong hop cho update hoac tao moi all
                Transaction transaction = null;
                org.hibernate.Session datasSession = null;
                try {
                    datasSession = dataService.openSession();
                    transaction = datasSession.beginTransaction();

                    for (MsalesChannelImport channelImport : channelImportList) {
                        Channel channel = channelImport.getChannel();
                        //set info
                        channel.setCode(channelImport.getChannelCode());
                        channel.setCompanyId(loginUserInf.getCompanyId());
                        if (checkRegion(channelImport, loginUserInf)) {
                            channel.setParentId(null);
                            channel.setFullCode(channelImport.getChannelCode());
                        } else {
                            channel.setParents(channelImport.getChannelParent());
                            channel.setFullCode(channelImport.getChannelParent().getFullCode() + "_" + channelImport.getChannelCode());
                        }

                        channel.setChannelTypes(channelImport.getChannelType());
                        channel.setIsSalePoint(0);
                        channel.setName(channelImport.getChannelName());
                        channel.setAddress(channelImport.getChannelAddress());
                        channel.setContactPersonName(channelImport.getChannelPersonContact());
                        if (channelImport.getChannelTel() != null) {
                            String tel = channelImport.getChannelTel();
                            if (!tel.startsWith("0") && tel.length() > 0) {
                                tel = "0" + tel;
                            }
                            channel.setTel(tel);
                        }

                        if (channelImport.getChannelFax() != null) {
                            String fax = channelImport.getChannelFax();
                            if (!fax.startsWith("0") && fax.length() > 0) {
                                fax = "0" + fax;
                            }
                            channel.setFax(fax);
                        }

                        channel.setEmail(channelImport.getChannelEmail());
                        channel.setLat(channelImport.getChannelLAT());
                        channel.setLng(channelImport.getChannelLNG());
                        channel.setStatuss(channelImport.getStatus());

                        //save or update channel
                        datasSession.saveOrUpdate(channel);

                        if (channel.getUpdatedUser() != 0) {
                            //delete all channel location
                            //deleted all Channel Goods
                            List<ChannelLocation> channelLocationList = channelService.getListChannelLocation(channel.getId(), dataService);
                            List<GoodsChannelFocus> goodsChannelFocusList = channelService.getListGoodsChannelFocus(channel.getId(), dataService);
                            for (ChannelLocation channelLocation : channelLocationList) {
                                channelLocation.setDeletedAt(new Date());
                                channelLocation.setDeletedUser(loginUserInf.getId());
                                datasSession.update(channelLocation);
                            }
                            for (GoodsChannelFocus goodsChannelFocus : goodsChannelFocusList) {
                                goodsChannelFocus.setDeletedAt(new Date());
                                goodsChannelFocus.setDeletedUser(loginUserInf.getId());
                                datasSession.update(goodsChannelFocus);
                            }
                        } else {
                            //new channel
                            //create salesStock
                            SalesStock salesStock = new SalesStock();
                            salesStock.setChannels(channel);
                            salesStock.setStatusId(1);
                            salesStock.setCreatedUser(loginUserInf.getId());
                            salesStock.setCreatedAt(new Date());
                            salesStock.setUpdatedUser(0);
                            salesStock.setDeletedUser(0);

                            datasSession.save(salesStock);

                        }
                        //insert all channel location
                        //insert all Channel Goods
                        if (channelImport.getGoodsList() != null && !channelImport.getGoodsList().isEmpty()) {
                            for (Goods goods : channelImport.getGoodsList()) {
                                GoodsChannelFocus goodsChannelFocus = new GoodsChannelFocus();
                                goodsChannelFocus.setChannels(channel);
                                goodsChannelFocus.setGoodss(goods);
                                goodsChannelFocus.setCreatedUser(loginUserInf.getId());
                                goodsChannelFocus.setCreatedAt(new Date());
                                goodsChannelFocus.setUpdatedUser(0);
                                goodsChannelFocus.setDeletedUser(0);

                                datasSession.save(goodsChannelFocus);
                            }
                        }

                        for (Location location : channelImport.getLocationList()) {
                            ChannelLocation channelLocation = new ChannelLocation();
                            channelLocation.setChannels(channel);
                            channelLocation.setLocations(location);
                            channelLocation.setCreatedUser(loginUserInf.getId());
                            channelLocation.setCreatedAt(new Date());
                            channelLocation.setUpdatedUser(0);
                            channelLocation.setDeletedUser(0);

                            datasSession.save(channelLocation);
                        }

                    }

                    transaction.commit();

                    //update done 
                    request.setAttribute("submited", true);
                    request.setAttribute("updated", true);

                    request.setAttribute("insertCount", insertCount);
                    request.setAttribute("updateCount", updateCount);
                    return "channelImport";

                } catch (Exception e) {
                    transaction.rollback();
                    request.setAttribute("errorSQL", true);
                    request.setAttribute("error", true);
                    return "channelImport";
                } finally {
                    datasSession.close();
                }
            }
        } else {
            request.setAttribute("error", true);
            request.setAttribute("emptyData", true);
            return "channelImport";
        }
    }

    private List<String> checkRequired(List<MsalesChannelImport> channelImportList, MsalesLoginUserInf loginUserInf) {
        List<String> requiredList = new ArrayList<>();
        boolean flag = true;
        for (MsalesChannelImport channelImport : channelImportList) {
            if (channelImport.getChannelTypeCode() == null || channelImport.getChannelTypeCode().trim().isEmpty()) {
                flag = false;
                requiredList.add("Mã loại kênh là bắt buộc nhập.");
            }

            if (channelImport.getChannelCode() == null || channelImport.getChannelCode().trim().isEmpty()) {
                flag = false;
                requiredList.add("Mã kênh là bắt buộc nhập.");
            }

            if (channelImport.getChannelName() == null || channelImport.getChannelName().trim().isEmpty()) {
                flag = false;
                requiredList.add("Tên kênh là bắt buộc nhập.");
            }

            if (channelImport.getChannelLAT() == null) {
                flag = false;
                requiredList.add("Vĩ độ là bắt buộc nhập.");
            }

            if (channelImport.getChannelLNG() == null) {
                flag = false;
                requiredList.add("Kinh độ là bắt buộc nhập.");
            }

            if (channelImport.getChannelStatus() == null || channelImport.getChannelStatus().trim().isEmpty()) {
                flag = false;
                requiredList.add("Tình trạng là bắt buộc nhập.");
            }

            if (channelImport.getChannelLocation() == null || channelImport.getChannelLocation().trim().isEmpty()) {
                flag = false;
                requiredList.add("Tỉnh thành, quận huyện là bắt buộc nhập.");
            }
            if (!flag) {
                break;
            }
        }
        return requiredList;
    }

    @RequestMapping(value = "/channel/import", method = RequestMethod.GET)
    private String importChannel(HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        return "channelImport";
    }

    private List<Channel> getListChannelUpdate(MsalesLoginUserInf loginUserInf) {

        List<Channel> channelList = new ArrayList<>();
        for (int i = 0; i < mListChannelImportUpdate.size(); i++) {
            MsalesChannelImport channelImport = mListChannelImportUpdate.get(i);
            Channel channelOld = mListChannelDuplicate.get(i);

            channelOld.setCompanyId(loginUserInf.getCompanyId());
            channelOld.setIsSalePoint(0);
            channelOld.setName(channelImport.getChannelName());
            channelOld.setAddress(channelImport.getChannelAddress());
            channelOld.setContactPersonName(channelImport.getChannelPersonContact());
            channelOld.setTel(channelImport.getChannelTel());
            channelOld.setEmail(channelImport.getChannelEmail());
            channelOld.setFax(channelImport.getChannelFax());
            channelOld.setLat(channelImport.getChannelLAT());
            channelOld.setLng(channelImport.getChannelLNG());
            channelOld.setUpdatedUser(loginUserInf.getId());

            channelList.add(channelOld);
        }

        return channelList;
    }

    private List<Channel> convertToChannelList(List<MsalesChannelImport> channelImports, MsalesLoginUserInf loginUserInf) {
        List<Channel> listChannel = new ArrayList<>();
        for (MsalesChannelImport channelImport : channelImports) {
            Channel channel = new Channel();

            channel.setCode(channelImport.getChannelCode());
            channel.setCompanyId(loginUserInf.getCompanyId());
            if (checkRegion(channelImport, loginUserInf)) {
                channel.setParentId(null);
                channel.setFullCode(channelImport.getChannelCode());
            } else {
                channel.setParents(channelImport.getChannelParent());
                channel.setFullCode(channelImport.getChannelParent().getFullCode() + "_" + channelImport.getChannelCode());
            }

            channel.setChannelTypes(channelImport.getChannelType());
            channel.setIsSalePoint(0);
            channel.setName(channelImport.getChannelName());
            channel.setAddress(channelImport.getChannelAddress());
            channel.setContactPersonName(channelImport.getChannelPersonContact());
            if (channelImport.getChannelTel() != null) {
                String tel = channelImport.getChannelTel();
                if (!tel.startsWith("0") && tel.length() > 0) {
                    tel = "0" + tel;
                }
                channel.setTel(tel);
            }

            if (channelImport.getChannelFax() != null) {
                String fax = channelImport.getChannelFax();
                if (!fax.startsWith("0") && fax.length() > 0) {
                    fax = "0" + fax;
                }
                channel.setFax(fax);
            }

            channel.setEmail(channelImport.getChannelEmail());
            channel.setLat(channelImport.getChannelLAT());
            channel.setLng(channelImport.getChannelLNG());
            channel.setCreatedUser(loginUserInf.getId());
            listChannel.add(channel);
        }
        return listChannel;
    }

    private List<ChannelLocation> getChannelLocationList(List<Integer> channelIds, MsalesLoginUserInf loginUserInf) {
        List<ChannelLocation> channelLocations = new ArrayList<>();
        for (int i = 0; i < channelIds.size(); i++) {
            List<Location> locaionsChan = mListLocation.get(i);
            for (Location location : locaionsChan) {
                ChannelLocation channelLocation = new ChannelLocation();
                channelLocation.setChannelId(channelIds.get(i));
                channelLocation.setLocationId(location.getId());
                channelLocation.setCreatedUser(loginUserInf.getId());
                channelLocations.add(channelLocation);
            }
        }
        return channelLocations;
    }

    private List<SalesStock> getSalesStock(List<Integer> channelIds, MsalesLoginUserInf loginUserInf) {
        List<SalesStock> soSalesStocks = new ArrayList<>();
        for (Integer channelId : channelIds) {
            SalesStock salesStock = new SalesStock();
            salesStock.setChannelId(channelId);
            salesStock.setCreatedUser(loginUserInf.getId());
            salesStock.setStatusId(15);// is active
            soSalesStocks.add(salesStock);
        }
        return soSalesStocks;
    }

    private List<GoodsChannelFocus> getGoodsChannelFocus(List<Integer> channelIds, MsalesLoginUserInf loginUserInf) {
        List<GoodsChannelFocus> goodsChannelFocuses = new ArrayList<>();
        for (int i = 0; i < channelIds.size(); i++) {
            List<Goods> goodses = mListGoods.get(i);
            for (Goods goods : goodses) {
                GoodsChannelFocus goodsChannel = new GoodsChannelFocus();
                goodsChannel.setChannelId(channelIds.get(i));
                goodsChannel.setGoodsId(goods.getId());
                goodsChannel.setCreatedUser(loginUserInf.getId());
                goodsChannelFocuses.add(goodsChannel);
            }
        }
        return goodsChannelFocuses;
    }

    private String getCodeFromFullCode(String fullCode) {
        String[] fullCoodes = fullCode.split("_");
        return fullCoodes[fullCoodes.length - 1];
    }

    private void checkInsertUpdate(List<Channel> channelList, MsalesLoginUserInf loginUserInf) {
//        mListChannelImportInsert = new ArrayList<>();
//        mListChannelImportUpdate = new ArrayList<>();
//        mListChannelDuplicate = new ArrayList<>();
//        mListChannelTypeInsert = new ArrayList<>();
//        mListChannelTypeUpdate = new ArrayList<>();
//        mListLocationInsert = new ArrayList<>();
//        mListLocationUpdate = new ArrayList<>();
//        mListGoodsInsert = new ArrayList<>();
//        mListGoodsUpdate = new ArrayList<>();
//
//        for (Channel channel : channelList) {
//            Channel rootChannel = channelService.getChannelByFullCode(channel.getFullCode(), loginUserInf.getCompanyId(), dataService);
//           
//            if (rootChannel == null) {
//                //insert
//                mListChannelImportInsert.add(channelImport);
//                mListChannelTypeInsert.add(listChannelType.get(i));
//                mListLocationInsert.add(mListLocation.get(i));
//                mListGoodsInsert.add(mListGoods.get(i));
//            } else {
//                Channel chan = channelFound.get(0);
//                mListChannelDuplicate.add(chan);
//                mListChannelImportUpdate.add(channelImport);
//                mListChannelTypeUpdate.add(listChannelType.get(i));
//
//                // get list location
//                List<Location> listChanLO = mListLocation.get(i);
//                List<Location> list = new ArrayList<>();
//                for (Location location : listChanLO) {
//                    ParameterList paramChanLo = new ParameterList(1, 1);
//                    paramChanLo.add("locations.id", location.getId());
//                    paramChanLo.add("channels.id", chan.getId());
//                    List<ChannelLocation> chanLoFound = dataService.getListOption(ChannelLocation.class, paramChanLo);
//                    if (chanLoFound != null && chanLoFound.isEmpty()) {
//                        list.add(location);
//                    }
//                }
//                mListLocationUpdate.add(list);
//
//                // get lit goods
//                List<Goods> listGoods = mListGoods.get(i);
//                List<Goods> listChanGoods = new ArrayList<>();
//                for (Goods goods : listGoods) {
//                    ParameterList paramChanGoods = new ParameterList(1, 1);
//                    paramChanGoods.add("channels.id", chan.getId());
//                    paramChanGoods.add("goodss.id", goods.getId());
//                    List<GoodsChannelFocus> chanGodFoFound = dataService.getListOption(GoodsChannelFocus.class, paramChanGoods);
//                    if (chanGodFoFound != null && chanGodFoFound.isEmpty()) {
//                        listChanGoods.add(goods);
//                    }
//                }
//                mListGoodsUpdate.add(listChanGoods);
//            }
//        }
    }

    private String getStringFromBigNumber(String bigNumber) {
        if (bigNumber != null && bigNumber.length() > 0) {
            BigDecimal bd = new BigDecimal(bigNumber);
            return bd.toString();
        }
        return "";
    }

    private List<List<Object>> getListDataFromExel(MultipartFile multipartFile) {
        MsalesExcelConfig excelConfig = new MsalesExcelConfig(14, 0, multipartFile);
        MsalesReadExcel msleExcel = new MsalesReadExcel(excelConfig);
        List<List<Object>> posList = msleExcel.readExcel();
        return posList;
    }

    private List<MsalesChannelImport> getListChannelImportFromListObj(List<List<Object>> listObj) {
        List<MsalesChannelImport> listChannelImports = new ArrayList<>();
        for (List<Object> objs : listObj) {
            MsalesChannelImport channelImport = new MsalesChannelImport();
            for (int i = 0; i < objs.size(); i++) {
                String channelFullCode = objs.get(i).toString();
                if (channelFullCode.length() > 0) {
                    switch (i) {
                        case 0:
                            channelImport.setChannelTypeCode(objs.get(0).toString().trim());
                            break;
                        case 1:
                            channelImport.setChannelCode(objs.get(1).toString().trim());
                            break;
                        case 2:
                            channelImport.setChannelName(objs.get(2).toString().trim());
                            break;
                        case 3:
                            channelImport.setChannelParentCode(objs.get(3).toString().trim());
                            break;
                        case 4:
                            channelImport.setChannelAddress(objs.get(4).toString());
                            break;
                        case 5:
                            channelImport.setChannelLAT(BigDecimal.valueOf(Double.parseDouble(objs.get(5).toString().trim())));
                            break;
                        case 6:
                            channelImport.setChannelLNG(BigDecimal.valueOf(Double.parseDouble(objs.get(6).toString().trim())));
                            break;
                        case 7:
                            channelImport.setChannelPersonContact(objs.get(7).toString().trim());
                            break;
                        case 8:
                            String tel = getStringFromBigNumber(objs.get(8).toString().trim());
                            if (tel != null && tel.length() > 0) {
                                channelImport.setChannelTel(tel.startsWith("0") ? tel : "0" + tel);
                            }
                            break;
                        case 9:
                            channelImport.setChannelEmail(objs.get(9).toString().trim());
                        case 10:
                            String fax = objs.get(10).toString().trim();
                            if (fax != null && fax.length() > 0) {
                                channelImport.setChannelFax(fax.startsWith("0") ? fax : "0" + fax);
                            }
                            break;
                        case 11:
                            channelImport.setChannelStatus(getStatus(objs.get(11).toString().trim()).toString());
                            break;
                        case 12:
                            channelImport.setChannelLocation(objs.get(12).toString().trim());
                            break;
                        case 13:
                            channelImport.setChannelGoodsCodeFocus(objs.get(13).toString().trim());
                            break;
                    }
                }
            }
            listChannelImports.add(channelImport);
        }
        return listChannelImports;
    }

    private List<MsalesChannelImport> validateChannelTypeCode(List<MsalesChannelImport> channelImports, MsalesLoginUserInf loginUserInf) {
        List<MsalesChannelImport> errorChannelType = new ArrayList<>();
        List<ChannelType> channelTypeList = channelService.getListChannelType(loginUserInf.getCompanyId());
        for (MsalesChannelImport channelImport : channelImports) {
            boolean flag = false;
            for (ChannelType channelType : channelTypeList) {
                if (channelImport.getChannelTypeCode().trim().equals(channelType.getCode())) {
                    flag = true;
                    channelImport.setChannelType(channelType);
                    break;
                }
            }
            if (!flag) {
                errorChannelType.add(channelImport);
            }
        }
        return errorChannelType;
    }

    private List<MsalesChannelImport> validateChannelParent(List<MsalesChannelImport> channelImports, MsalesLoginUserInf loginUserInf) {
        List<MsalesChannelImport> errorChannel = new ArrayList<>();

        for (MsalesChannelImport channelImport : channelImports) {
            if (channelImport.getChannelParentCode() != null && !channelImport.getChannelParentCode().trim().isEmpty()) {
                Channel parrentChannel = channelService.getChannelByCode(channelImport.getChannelParentCode(), loginUserInf.getCompanyId(), dataService);
                if (parrentChannel == null) {
                    //thu tim voi full code
                    parrentChannel = channelService.getChannelByFullCode(channelImport.getChannelParentCode(), loginUserInf.getCompanyId(), dataService);
                }
                if (parrentChannel == null) {
                    errorChannel.add(channelImport);
                } else {
                    channelImport.setChannelParent(parrentChannel);
                }
            } else {
                //channel parent code null
                if (!checkRegion(channelImport, loginUserInf)) {
                    errorChannel.add(channelImport);
                }
            }
        }
        return errorChannel;
    }

    private boolean checkRegion(MsalesChannelImport channelImport, MsalesLoginUserInf loginUserInf) {
        if (channelImport.getChannelTypeCode() != null) {
            ChannelType channelType = channelService.getChannelTypeByCode(channelImport.getChannelTypeCode(), loginUserInf.getCompanyId(), dataService);
            if (channelType != null && channelType.getParents() == null) {
                return true;
            }
        }
        return false;
    }

    private List<MsalesChannelImport> validateLocationCode(List<MsalesChannelImport> channelImports) {
        List<MsalesChannelImport> channelErrorList = new ArrayList<>();

        for (MsalesChannelImport channelImport : channelImports) {
            String chanString = channelImport.getChannelLocation();
            if (chanString != null && chanString.length() > 0) {
                String[] locationsCode = locationsCode = chanString.split(";");
                List<Location> locationList = new ArrayList<>();
                boolean flag = true;
                for (String code : locationsCode) {
                    Location location = channelService.getCityByCode(code, dataService);
                    if (location != null) {
                        locationList.add(location);
                    } else {
                        location = channelService.getDistrictByCode(code, dataService);
                        if (location != null) {
                            locationList.add(location);
                        } else {
                            flag = false;
                            channelErrorList.add(channelImport);
                        }
                    }
                }
                if (flag) {
                    channelImport.setLocationList(locationList);
                }
            }
        }
        return channelErrorList;
    }

    private List<MsalesChannelImport> validateStatus(List<MsalesChannelImport> channelImports) {
        List<MsalesChannelImport> statusErrorList = new ArrayList<>();
        List<Status> statusList = channelService.getChannelStatus(dataService);
        for (MsalesChannelImport channelImport : channelImports) {
            boolean flag = false;
            for (Status status : statusList) {
                if (channelImport.getChannelStatus().trim().equals(status.getValue())) {
                    channelImport.setStatus(status);
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                statusErrorList.add(channelImport);
            }
        }
        return statusErrorList;
    }

    private Integer getStatusId(int statusValue) {
        try {
            ParameterList param = new ParameterList(1, 1);
            param.add("statusTypes.id", 5);
            param.add("value", String.valueOf(statusValue));
            List<Status> statusFound = dataService.getListOption(Status.class, param);
            if (!statusFound.isEmpty()) {
                return statusFound.get(0).getId();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private List<Goods> validateGoods(List<MsalesChannelImport> channelImports, MsalesLoginUserInf loginUserInf) {
        List<Goods> goodsErrors = new ArrayList<>();
        for (MsalesChannelImport channelImport : channelImports) {
            List<Goods> goodsList = new ArrayList<>();
            if (channelImport.getChannelGoodsCodeFocus() != null) {
                boolean flag = true;
                String[] goodsCode = channelImport.getChannelGoodsCodeFocus().trim().split(";");

                if (goodsCode != null) {
                    for (String code : goodsCode) {
                        Goods goods = channelService.getGoodsByCode(code, loginUserInf.getCompanyId(), dataService);
                        if (goods == null) {
                            Goods error = new Goods();
                            error.setGoodsCode(code);
                            goodsErrors.add(error);
                            flag = false;
                        } else {
                            goodsList.add(goods);
                        }
                    }
                    if (flag) {
                        channelImport.setGoodsList(goodsList);
                    }
                }
            }
        }
        return goodsErrors;
    }

    private Integer getStatus(String status) {
        try {
            status = status.trim();
            if (status.length() > 0) {
                if (status.indexOf(".") > 0) {
                    return (int) Double.parseDouble(status);
                } else {
                    return Integer.parseInt(status);
                }
            }
        } catch (NumberFormatException e) {
            return 1;
        }
        return 1;
    }

    private boolean checkChannelDupblicate(String fullCode, MsalesLoginUserInf loginUserInf) {
        ParameterList param = new ParameterList(1, 1);
        param.add("fullCode", fullCode);
        param.add("companys.id", loginUserInf.getCompanyId());
        List<Channel> channelFound = dataService.getListOption(Channel.class, param);
        return channelFound != null && !channelFound.isEmpty();
    }

    private List<ChannelLocation> getChannelLocationByChannelId(int channelId) {
        ParameterList param = new ParameterList(0, 0);
        param.add("channels.id", channelId);

        List<ChannelLocation> chlList = dataService.getListOption(ChannelLocation.class, param);
        if (chlList != null && !chlList.isEmpty()) {
            return chlList;
        }

        return null;
    }

    private String getFullCode(String parentCode, String code) {
        if (parentCode != null && parentCode.length() > 0 && code != null && code.length() > 0) {
            return parentCode + "_" + code;
        }
        return null;
    }
}
