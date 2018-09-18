package vn.itt.msales.api.ajax.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import vn.itt.msales.channel.controller.MsalesChannelController;
import vn.itt.msales.channel.controller.MsalesChannelTypeController;
import vn.itt.msales.channel.controller.MsalesLocationController;
import vn.itt.msales.channel.controller.MsalesPOSController;
import vn.itt.msales.channel.controller.MsalesStatusController;
import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.common.OptionItemComparator;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.goods.controller.MsalesGoodsController;
import vn.itt.msales.goods.controller.MsalesGoodsUnitController;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.user.controller.MsalesUserController;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Date;
import java.util.Objects;
import org.springframework.web.bind.annotation.PathVariable;
import vn.itt.msales.channel.controller.MsalesChannelLocationController;
import vn.itt.msales.channel.services.WebMsalesChannelServices;
import vn.itt.msales.customercare.controller.WebMsalesPlanController;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.Promotion;
import vn.itt.msales.entity.PromotionAwardOther;
import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.SearchChannel;
import vn.itt.msales.promotion.service.PromotionService;
import vn.itt.msales.sales.controller.MsalesWebCheck;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author ChinhNQ
 */
@Controller
@RequestMapping(value = "ajax")
public class WebAjaxController {

    @Autowired
    private MsalesChannelController channelController;

    @Autowired
    private MsalesLocationController locationController;

    @Autowired
    private MsalesPOSController posController;

    @Autowired
    private View jsonView;

    private static final String DATA_FIELD = "data";

    @Autowired
    private MsalesStatusController statusController;

    @Autowired
    private MsalesChannelTypeController channelTypeController;

    @Autowired
    private MsalesUserController userController;
    @Autowired
    private MsalesGoodsUnitController goodsUnitController;
    @Autowired
    private MsalesGoodsController goodsController;
    @Autowired
    private MsalesChannelLocationController channelLocationController;

    @Autowired
    private WebMsalesChannelServices channelService;

    @Autowired
    private DataService dataService;
    @Autowired
    private ServiceFilter serviceFilter;
    @Autowired
    private PromotionService promotionService;
    
    @Autowired
    private WebMsalesChannelServices webMsalesChannelServices;

    @RequestMapping(value = "/channel/getListChannelByParentId")
    public @ResponseBody
    String getListChannel(HttpServletRequest request, @RequestBody SearchChannel searchChannel) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        int page = 1;
        int size = 10;
        if (searchChannel.getPage() != null) {
            page = searchChannel.getPage();
        }
        if (searchChannel.getSize() != null) {
            size = searchChannel.getSize();
        }

        //get List Channel by parentId id
        ParameterList parameterList = new ParameterList("parents.id", searchChannel.getParentId(), page, size);
        parameterList.add("companys.id", loginUserInf.getCompanyId());
        parameterList.setOrder("createdAt", "DESC");
        MsalesResults<Channel> list = channelController.getDataService().getListOption(Channel.class, parameterList, true);

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
    }

    @ResponseBody
    @RequestMapping(value = "getCbListLocationByParentId", method = RequestMethod.GET)
    public ModelAndView getCbListLocationByParentId(HttpServletRequest request,
            Model uiModel, @RequestParam("parentId") Integer parentId) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        List<OptionItem> data = new ArrayList<>();
        try {
            List<Location> list = (List<Location>) locationController.getDataService().getListOption(Location.class, new ParameterList("parents.id", parentId));
            if (list != null) {
                for (Location location : list) {
                    OptionItem item = new OptionItem();
                    item.setId(location.getId());
                    item.setName(location.getName());
                    data.add(item);
                }
            }
            if (data.size() > 0) {
                Collections.sort(data, new OptionItemComparator());
            }
        } catch (Exception ex) {
            Logger.getLogger(WebMsalesPlanController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ModelAndView(jsonView, DATA_FIELD, data);
    }

    @ResponseBody
    @RequestMapping(value = "getCbListDistrictByParentIdAndChannelId", method = RequestMethod.GET)
    public ModelAndView getCbListDistrictByParentId(HttpServletRequest request,
            Model uiModel, @RequestParam("parentId") Integer parentId) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        List<OptionItem> data = new ArrayList<>();
        try {
            if (userInf.getUserRoleCode().equals("USER_ROLE_ADMIN_COMPANY")) {
                List<Location> list = (List<Location>) locationController.getDataService().getListOption(Location.class,
                        new ParameterList("parents.id", parentId));
                if (list != null) {
                    for (Location location : list) {
                        OptionItem item = new OptionItem();
                        item.setId(location.getId());
                        item.setName(location.getName());
                        data.add(item);
                    }
                }
            } else {
                String string = "";
                for (OptionItem optionItem : userInf.getChannelList()) {
                    string += optionItem.getId() + ",";
                }
                string += "''";
                String hql = "SELECT ChannelLocation.locations.id as locationId, ChannelLocation.locations.name as name, "
                        + " ChannelLocation.locations.locationType as locationType"
                        + " FROM ChannelLocation as ChannelLocation " + " WHERE ChannelLocation.deletedUser = 0"
                        + " AND ChannelLocation.locations.deletedUser = 0"
                        + " AND (ChannelLocation.locations.parents.id = " + parentId
                        + " OR ChannelLocation.locations.id = " + parentId + " OR ChannelLocation.locations.parents.parents.id = " + parentId + " )"
                        + " AND ChannelLocation.channels.id IN (" + string + ")";
                List<HashMap> hashMaps = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
                for (HashMap hm : hashMaps) {
                    int locationType = 0;
                    if (hm.get("locationType") != null) {
                        locationType = (int) hm.get("locationType");
                    }

                    int locationId = 0;
                    if (hm.get("locationId") != null) {
                        locationId = (int) hm.get("locationId");
                    }
                    OptionItem optionItem = new OptionItem();
                    if (locationType == 1) {
                        // La tinh thanh
                        if (!data.isEmpty()) {
                            data.removeAll(data);
                        }
                        List<Location> list = (List<Location>) locationController.getDataService()
                                .getListOption(Location.class, new ParameterList("parents.id", parentId));
                        if (list != null) {
                            for (Location location : list) {
                                OptionItem item = new OptionItem();
                                item.setId(location.getId());
                                item.setName(location.getName());
                                data.add(item);
                            }
                        }
                        break;
                    } else if (locationType == 2) {
                        optionItem.setId((Integer) hm.get("locationId"));
                        optionItem.setName((String) hm.get("name"));
                        data.add(optionItem);
                    } else if (locationType == 3) {
                        Location location = dataService.getRowById(locationId, Location.class);
                        boolean _bool = true;
                        for (OptionItem opItem : data) {
                            if (opItem.getId() == location.getParents().getId()) {
                                _bool = false;
                                break;
                            }
                        }
                        if (_bool) {
                            optionItem.setId(location.getParents().getId());
                            optionItem.setName(location.getParents().getName());
                            data.add(optionItem);
                        }
                    }
                }
            }

            if (data.size() > 0) {
                Collections.sort(data, new OptionItemComparator());
            }
        } catch (Exception ex) {
            Logger.getLogger(WebMsalesPlanController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ModelAndView(jsonView, DATA_FIELD, data);
    }

    @RequestMapping(value = "/location/getListProvince", method = RequestMethod.POST)
    public @ResponseBody
    String getListProvince(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String response = locationController.getCbListLocationByLocationType(request);
        return response;
    }

    @RequestMapping(value = "/location/getListDistrict", method = RequestMethod.POST)
    public @ResponseBody
    String getListDistrict(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String response = locationController
                .getCbListLocationByParentId(request);
        return response;
    }

    @RequestMapping(value = "/location/getListWard", method = RequestMethod.POST)
    public @ResponseBody
    String getListWard(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String response = locationController
                .getCbListLocationByParentId(request);
        return response;
    }

    @RequestMapping(value = "/status/getListStatus", method = RequestMethod.POST)
    public @ResponseBody
    String getListStatus(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContentAndPage(request);
        String response = statusController
                .getCbListStatusByStatusTypeId(request);
        return response;
    }

    @RequestMapping(value = "/channel/getListChannelTypeByParent", method = RequestMethod.POST)
    public @ResponseBody
    String getListChannelType(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String responseList = channelTypeController
                .getListChannelTypeByParent(request);
        return responseList;
    }

    @RequestMapping(value = "/channel/getListChannelParent", method = RequestMethod.POST)
    public @ResponseBody
    String getListChannelParent(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String responseList = channelController.getListChannelParents(request);
        return responseList;
    }

    @RequestMapping(value = "/channel/createChannel", method = RequestMethod.POST)
    public @ResponseBody
    String createChannel(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String responseList = channelController.createChannel(request);
        return responseList;
    }

    // DuanND getListChannelByChannelType
    @RequestMapping(value = "/channel/getCbChannelByChannelTypeId")
    public @ResponseBody
    String getCbListChannelByChannelTypeId(HttpServletRequest request)
            throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        // lấy danh sách Channel theo channelType
        String responseList = channelController
                .getCbListChannelByChannelTypeId(request);
        return responseList;
    }

    // DuanND getListUser của công ty từ nhớn tới bé.
    @RequestMapping(value = "/user/getCbListUser")
    public @ResponseBody
    String getCbListUser(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String response = userController.getCbListUser(request);
        return response;
    }

    // Lấy ListUserByTinhThanh
    @RequestMapping(value = "/user/getCbListUserByTinhThanh", method = RequestMethod.POST)
    public @ResponseBody
    String getCbListUserByCity(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String response = userController.getCbListUserByTinhThanhPho(request);
        return response;
    }

    // Lấy danh sách nhà phân phối theo locationId
    @RequestMapping(value = "/channel/getCbListChannelByLocationId", method = RequestMethod.POST)
    public @ResponseBody
    String getCbListChannelByLocationId(HttpServletRequest request)
            throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String response = channelController
                .getCbListChannelByLocationId(request);
        return response;
    }

    // Lấy danh sách User theo ChannelId
    @RequestMapping(value = "/user/getCbListUserByChannelId", method = RequestMethod.POST)
    public @ResponseBody
    String getCbListUserByChannelId(HttpServletRequest request)
            throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String response = userController.getCbListUserByChannelId(request);
        return response;
    }

    @RequestMapping(value = "searchPOS", method = RequestMethod.POST)
    public @ResponseBody
    String searchPOSByLocationId(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        request.setAttribute("companyId", userInf.getCompanyId().toString());

        request = getContent(request);

        String response = posController.searchPOSAdmin(request);
        return response;
    }

    /**
     *
     * @param request
     * <p>
     * @return
     * <p>
     * @throws IOException
     */
    @RequestMapping(value = "/location/getListWardDetails", method = RequestMethod.POST)
    public @ResponseBody
    String getListWardDetails(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String response = locationController.getListLocationByParentId(request);
        return response;
    }

    /**
     *
     * @param request
     * <p>
     * @return
     * <p>
     * @throws IOException
     */
    @RequestMapping(value = "/location/getListWardDetailsBykey", method = RequestMethod.POST)
    public @ResponseBody
    String getListWardDetailsByKey(HttpServletRequest request)
            throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContentAndPage(request);
        String response = locationController.searchLocation(request);
        return response;
    }

    /**
     *
     * @param request
     * <p>
     * @return String json format
     * <p>
     * @throws IOException
     */
    @RequestMapping(value = "/location/getListLocationByParentId", method = RequestMethod.POST)
    public @ResponseBody
    String getListProvinceCityById(HttpServletRequest request)
            throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String response = locationController.getCbListLocationByParentId(request);
        return response;
    }

    /**
     *
     * @param request
     * <p>
     * @return
     * <p>
     * @throws IOException
     */
    @RequestMapping(value = "/location/createLocation", method = RequestMethod.POST)
    public @ResponseBody
    String createLocation(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }
        request = getContent(request);
        String response = locationController.createLocation(request);
        return response;
    }

    /**
     *
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/location/getLocationByLocationId", method = RequestMethod.POST)
    public @ResponseBody
    String getLocationById(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String response = locationController.getLocation(request);
        return response;
    }

    @RequestMapping(value = "/pos/getPOS", method = RequestMethod.POST)
    public @ResponseBody
    String getAddressByPOSId(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);

        String response = posController.getPOSOrder(request);
        return response;
    }

    //Lấy danh sách hàng hóa salesman được bán.
    @RequestMapping(value = "/goods/getGoodsSalesman", method = RequestMethod.POST)
    public @ResponseBody
    String getGoodsSalesman(HttpServletRequest request) throws IOException {

        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        request = getContent(request);
        String pos = posController.getPOSOrder(request);
        LinkedHashMap<String, Object> poss = MsalesJsonUtils.getObjectFromJSON(pos, LinkedHashMap.class);
        LinkedHashMap<String, Object> contents = (LinkedHashMap<String, Object>) poss.get("contents");
        int createdUser = Integer.parseInt(contents.get("createdUser").toString());
        request.setAttribute(MsalesConstants.CONTENTS, "{\"userId\": " + createdUser + ",\"goodsCategoryId\": " + userInf.getCompanyId() + "}");
        String response = goodsController.getGoodsSalesmanApp(request);
        return response;
    }

    private HttpServletRequest getContentAndPage(HttpServletRequest request)
            throws IOException {
        // 1. get received JSON data from request
        BufferedReader br = new BufferedReader(new InputStreamReader(
                request.getInputStream(), "UTF-8"));
        String json = "";
        if (br != null) {
            json = br.readLine();
        }

        JsonNode node = MsalesJsonUtils.getJsonNote(json);

        String contents = MsalesJsonUtils.getValue(node, "contents");
        String jsonpage = MsalesJsonUtils.getValue(node, "page");
        MsalesPageRequest page = MsalesJsonUtils.getObjectFromJSON(jsonpage,
                MsalesPageRequest.class);
        request.setAttribute(MsalesConstants.CONTENTS, contents);
        request.setAttribute(MsalesConstants.PAGE, page);
        return request;
    }

    private HttpServletRequest getContent(HttpServletRequest request)
            throws IOException {
        // 1. get received JSON data from request
        BufferedReader br = new BufferedReader(new InputStreamReader(
                request.getInputStream(), "UTF-8"));
        String json = "";
        if (br != null) {
            json = br.readLine();
        }
        request.setAttribute(MsalesConstants.CONTENTS, json);

        return request;
    }

    @ResponseBody
    @RequestMapping(value = "/getUserData")
    public Map<String, Object> getUserDataBySup(@RequestBody Filter filter,
            HttpServletRequest request) {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnEmptyHashMap();
        }

        Map<String, Object> data = new HashMap<>();
        // filterService.fillAreaSelectionForUser(filter, data);
        List<OptionItem> channelList = new ArrayList<>();

        List<OptionItem> provinceList = new ArrayList<>();
        List<OptionItem> employerList = new ArrayList<>();

        if (filter.getChannelIdPOST() != null) {
            //chon dau muc = 0
            employerList.addAll(serviceFilter.getCbListUserByChannelIdList(filter.getChannelIdPOST(), LoginContext.getLogin(request).getCompanyId(), LoginContext.getLogin(request).getId(), filter.getRoles(), dataService));
            //ko can tra ve channelList
        } else {
            //chon 1 kenh
            Integer channelId = filter.getChannelId();
            if (channelId != null) {
                if (channelId == 0) {
                    // lay danh danh sach vung mien
                    OptionItem optionItem = serviceFilter.getFirstChannelType(LoginContext.getLogin(request).getCompanyId(), dataService);
                    channelList.addAll(serviceFilter.getCbListChannelByChannelType(optionItem.getId(), LoginContext.getLogin(request).getCompanyId(), dataService));
                } else {
                    channelList.addAll(serviceFilter.getCbListChannelByParentId(channelId, dataService));
                }
                // getLocationByChannelId -- chi tinh thanh
                provinceList.addAll(serviceFilter.getCbListLocationByChannel(channelId, dataService));
                data.put("channelList", channelList);
                //serviceFilter.getCbListUserByChannel(channelId, LoginContext.getLogin(request).getCompanyId(),LoginContext.getLogin(request).getId(),filter.getRoles(), dataService);
                employerList.addAll(serviceFilter.getCbListUserByChannel(channelId, LoginContext.getLogin(request).getCompanyId(), LoginContext.getLogin(request).getId(), filter.getRoles(), dataService));
            }
        }

        data.put("provinceList", provinceList);
        data.put("employeeList", employerList);
        return data;
    }

    @ResponseBody
    @RequestMapping(value = "/getCbListChannelByParent")
    public Map<String, Object> getCbListChannelByParent(@RequestBody Integer parentId,
            HttpServletRequest request) {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnEmptyHashMap();
        }

        Map<String, Object> data = new HashMap<>();

        // filterService.fillAreaSelectionForUser(filter, data);
        List<OptionItem> channelList = new ArrayList<>();
        channelList.addAll(serviceFilter.getCbListChannelByParentId(parentId, dataService));

        data.put("channelList", channelList);

        return data;
    }

//    @ResponseBody
//    @RequestMapping(value = "/getCbListUserRoleByMonitoring")
//    public Map<String, Object> getCbListUserRoleByMonitoring(@RequestBody Integer userId,
//            HttpServletRequest request) {
//        Map<String, Object> data = new HashMap<>();
//        List<OptionItem> userRoleList = new ArrayList<>();
//        // getListLocationByChannelId -- chi tinh thanh
//        request.setAttribute(MsalesConstants.CONTENTS, "{\"userId\":" + userId + "}");
//        String responseString = userRoleController.getCbListUserRoleByUserId(request);
//        userRoleList.addAll(OptionItem.createOptionListFromResponseString(responseString));
//
//        data.put("userRoleList", userRoleList);
//        return data;
//    }
    @RequestMapping(value = "/goods/goodsUnits", method = RequestMethod.POST)
    public @ResponseBody
    String getListUnitByGoods(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        request.setAttribute(MsalesConstants.PAGE, new MsalesPageRequest(0, 0));
        String jsonRes = goodsUnitController.getGoodsUnitByGoods(request);
        return jsonRes;
    }

    //Lấy giá của sản phẩm
    @RequestMapping(value = "/goodsUnit/getGoodsUnit", method = RequestMethod.POST)
    public @ResponseBody
    String getPriceOfGoodsUnit(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }
        request = getContent(request);
        String response = goodsUnitController.getGoodsUnitForOrder(request);
        return response;
    }

    @RequestMapping(value = "/channel/location/delete", method = RequestMethod.POST)
    public @ResponseBody
    String deleteLocation(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);
        String jsonRes = "";
        String jsonPost = request.getAttribute(MsalesConstants.CONTENTS).toString();
        LinkedHashMap<String, Object> hashMap = MsalesJsonUtils.getObjectFromJSON(jsonPost, LinkedHashMap.class);
        if (!hashMap.isEmpty()) {
            int channelId = Integer.parseInt(hashMap.get("channelId").toString());
            int locationId = Integer.parseInt(hashMap.get("locationId").toString());

            String hqlDelete = "UPDATE ChannelLocation SET deletedUser=" + 1 + " where locations.id=" + locationId + " AND channels.id=" + channelId;
            int deledtedCount = channelLocationController.getDataService().executeHQL(hqlDelete);
            if (deledtedCount > 0) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
            }
        }

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/channel/delete", method = RequestMethod.POST)
    public @ResponseBody
    String deleteChannel(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        request = getContent(request);
        LinkedHashMap<String, Object> hashContent = (LinkedHashMap) MsalesJsonUtils.getObjectFromJSON(request.getAttribute(MsalesConstants.CONTENTS).toString(), LinkedHashMap.class);
        if (hashContent != null) {
            int channelId = Integer.parseInt(hashContent.get("id").toString());
            if (channelId > 0) {
                Channel channelFound = channelController.getDataService().getRowById(channelId, Channel.class);
                if (channelFound != null && channelFound.getId() != null) {
                    // get list channel by parents
                    String relatedError = webMsalesChannelServices.checkChannelRelation(loginUserInf.getCompanyId(), channelFound.getId(),dataService);
                    if (!relatedError.isEmpty()) {
                        //fix
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.NOT_ACCEPTABLE,relatedError));
                    } else {
                        channelFound.setDeletedUser(LoginContext.getLogin(request).getId());
                        int deleted = channelController.getDataService().deleteSynch(channelFound);
                        if (deleted > 0) {
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
                        }
                    }
                } else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.NOT_FOUND));
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.NOT_FOUND));
            }
        }
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/channel/getListChannelByChannelType")
    public @ResponseBody
    String getListChannelByChannelType(HttpServletRequest request, @RequestBody SearchChannel searchChannel) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        if (searchChannel != null) {
            int channelType = searchChannel.getChannelType();
            int channelId = searchChannel.getChannelId();
            int page = 1;
            int size = 10;
            if (searchChannel.getPage() != null) {
                page = searchChannel.getPage();
            }
            if (searchChannel.getSize() != null) {
                size = searchChannel.getSize();
            }

            MsalesResults<Channel> listChannelType = channelService.getListChannelByType(channelId, channelType, loginUserInf.getCompanyId(), page, size);
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listChannelType));
        }

//        
//        request = getContent(request);
//        LinkedHashMap<String, Object> hashContent = (LinkedHashMap) MsalesJsonUtils.getObjectFromJSON(request.getAttribute(MsalesConstants.CONTENTS).toString(), LinkedHashMap.class);
//        if (hashContent != null) {
//            int channelType = Integer.parseInt(hashContent.get("channelType").toString());
//            int channelId = Integer.parseInt(hashContent.get("channelId").toString());
//            int page = 1;
//            int size = 10;
//            if (hashContent.get("page") != null) {
//                page = Integer.parseInt(hashContent.get("page").toString());
//            }
//            if (hashContent.get("size") != null) {
//                size = Integer.parseInt(hashContent.get("size").toString());
//            }
//
//            MsalesResults<Channel> listChannelType = channelService.getListChannelByType(channelId, channelType, loginUserInf.getCompanyId(), page, size);
//            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listChannelType));
//
//        }
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.NOT_FOUND));
    }

    @ResponseBody
    @RequestMapping(value = "/getUserSalesman")
    public Map<String, Object> getUserData(@RequestBody Filter filter,
            HttpServletRequest request) {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnEmptyHashMap();
        }
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);

        Map<String, Object> data = new HashMap<>();
        // filterService.fillAreaSelectionForUser(filter, data);
        List<OptionItem> channelList = new ArrayList<>();

        List<OptionItem> provinceList = new ArrayList<>();
        List<OptionItem> employerList = new ArrayList<>();

        Integer channelId = filter.getChannelId();

        if (channelId == 0) {
            // lay danh danh sach vung mien
            // getCbChannelByChannelType = 1
            OptionItem optionItem = serviceFilter.getFirstChannelType(LoginContext.getLogin(request).getCompanyId(), dataService);
            channelList.addAll(serviceFilter.getCbListChannelByChannelType(optionItem.getId(), LoginContext.getLogin(request).getCompanyId(), dataService));
        } else {
            channelList.addAll(serviceFilter.getCbListChannelByParentId(channelId, dataService));
        }

        // getLocationByChannelId -- chi tinh thanh
        provinceList.addAll(serviceFilter.getCbListLocationByChannel(channelId, dataService));

        data.put("channelList", channelList);

        // get employeeList by channelId        
        List<OptionItem> findList = new ArrayList<>();
        findList.addAll(channelList);
        findList.add(new OptionItem(channelId, ""));
        employerList.addAll(serviceFilter.getCbListUserByChannelOptionListLTNV(6, findList, userInf.getCompanyId(), userInf.getId(), dataService));

        data.put("provinceList", provinceList);
        data.put("employeeList", employerList);
        return data;
    }

    @ResponseBody
    @RequestMapping(value = "/getUserSalesSup")
    public Map<String, Object> getUserDataSalesSup(@RequestBody Filter filter,
            HttpServletRequest request) {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnEmptyHashMap();
        }
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);

        Map<String, Object> data = new HashMap<>();
        // filterService.fillAreaSelectionForUser(filter, data);
        List<OptionItem> channelList = new ArrayList<>();

        List<OptionItem> provinceList = new ArrayList<>();
        List<OptionItem> employerList = new ArrayList<>();

        Integer channelId = filter.getChannelId();

        if (channelId == 0) {
            // lay danh danh sach vung mien
            // getCbChannelByChannelType = 1
            OptionItem optionItem = serviceFilter.getFirstChannelType(LoginContext.getLogin(request).getCompanyId(), dataService);
            channelList.addAll(serviceFilter.getCbListChannelByChannelType(optionItem.getId(), LoginContext.getLogin(request).getCompanyId(), dataService));
        } else {
            channelList.addAll(serviceFilter.getCbListChannelByParentId(channelId, dataService));
        }

        // getLocationByChannelId -- chi tinh thanh
        provinceList.addAll(serviceFilter.getCbListLocationByChannel(channelId, dataService));

        data.put("channelList", channelList);

        // get employeeList by channelId        
        List<OptionItem> findList = new ArrayList<>();
        findList.addAll(channelList);
        findList.add(new OptionItem(channelId, ""));
        employerList.addAll(serviceFilter.getCbListUserByChannelOptionListLTNV(4, findList, userInf.getCompanyId(), userInf.getId(), dataService));

        data.put("provinceList", provinceList);
        data.put("employeeList", employerList);
        return data;
    }

    @RequestMapping(value = "/unit/delete/{id:[\\d]+}", method = RequestMethod.POST)
    public @ResponseBody
    String deleteUnit(HttpServletRequest request, @PathVariable("id") int id) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }
        if (id > 0) {
            Unit unit = dataService.getRowById(id, Unit.class);
            if (unit != null && unit.getId() != null) {
                unit.setDeletedUser(LoginContext.getLogin(request).getId());
                int deleted = dataService.deleteSynch(unit);
                if (deleted > 0) {
                    return "200";
                }
            }
        }
        return "404";
    }

    private String getData(HttpServletRequest request) throws IOException {
        // 1. get received JSON data from request
        BufferedReader br = new BufferedReader(new InputStreamReader(
                request.getInputStream(), "UTF-8"));
        String json = "";
        json = br.readLine();
        return json;
    }
   
    @ResponseBody
    @RequestMapping(value = "/getLocationByChannel")
    public Map<String, Object> getLocationByChannelId(@RequestBody Filter filter,
            HttpServletRequest request) {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnEmptyHashMap();
        }

        Map<String, Object> data = new HashMap<>();
        // filterService.fillAreaSelectionForUser(filter, data);
        List<OptionItem> channelList = new ArrayList<>();
        //   List<OptionItem> provinceList = new ArrayList<>();
        // List<OptionItem> employerList = new ArrayList<>();

        Integer channelId = filter.getChannelId();

        if (channelId == 0) {
            // lay danh danh sach vung mien
            // getCbChannelByChannelType = 1
            OptionItem optionItem = serviceFilter.getFirstChannelType(LoginContext.getLogin(request).getCompanyId(), dataService);
            channelList.addAll(serviceFilter.getCbListChannelByChannelType(optionItem.getId(), LoginContext.getLogin(request).getCompanyId(), dataService));
        } else {
            channelList.addAll(serviceFilter.getCbListChannelByParentId(channelId, dataService));
        }
        // getLocationByChannelId -- chi tinh thanh
        //request.setAttribute(MsalesConstants.CONTENTS, "{\"channelId\":" + channelId + "}");
        //responseString = locationController.getCbListLocationByChannelId(request);
        //provinceList.addAll(OptionItem.createOptionListFromResponseString(responseString));
        // get employeeList by channelId
        //   employerList.addAll(serviceFilter.getCbListUserByChannelSalesMan(6, channelId, LoginContext.getLogin(request).getCompanyId(), dataService));

        String hql = "Select ChannelLocation.locations.id as id, ChannelLocation.locations.name as name, ChannelLocation.locations.locationType as locationType"
                + " from ChannelLocation as ChannelLocation where deletedUser = 0"
                + " and channels.id = " + channelId + " and channels.companys.id = " + LoginContext.getLogin(request).getCompanyId();
        List<HashMap> lists = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
        List<HashMap> provinceList = new ArrayList<HashMap>();
        List<HashMap> districtList = new ArrayList<HashMap>();
        List<HashMap> wardList = new ArrayList<HashMap>();
        for (HashMap hashMap : lists) {
            if ((Integer) hashMap.get("locationType") == 1) {
                provinceList.add(hashMap);
            } else if ((Integer) hashMap.get("locationType") == 2) {
                Location location = dataService.getRowById((Integer) hashMap.get("id"), Location.class);
                int proviceSize = provinceList.size();
                boolean boolProvince = true;
                for (int i = 0; i < proviceSize; i++) {
                    if ((Integer) provinceList.get(i).get("id") == location.getParents().getId()) {
                        boolProvince = false;
                        break;
                    }
                }
                if (boolProvince) {
                    HashMap parent = new HashMap();
                    parent.put("id", location.getParents().getId());
                    parent.put("name", location.getParents().getName());
                    provinceList.add(parent);
                }
                // districtList.add(hashMap);
            } else if ((Integer) hashMap.get("locationType") == 3) {
                Location location = dataService.getRowById((Integer) hashMap.get("id"), Location.class);
                int proviceSize = provinceList.size();
                boolean boolProvince = true;
                for (int i = 0; i < proviceSize; i++) {
                    if ((Integer) provinceList.get(i).get("id") == location.getParents().getParents().getId()) {
                        boolProvince = false;
                        break;

                    }
                }
                if (boolProvince) {
                    HashMap parent = new HashMap();
                    parent.put("id", location.getParents().getParents().getId());
                    parent.put("name", location.getParents().getParents().getName());
                    provinceList.add(parent);
                }
//        		 int districtSize = districtList.size();
//        		 boolean boolDistrict = true;
//        		 for(int i = 0; i < districtSize; i++){
//        			 if((Integer) districtList.get(i).get("id") != location.getParents().getId()){
//        				 boolDistrict = false;
//        				 break;
//        				
//        			 }
//        		 }
//        		 if(boolDistrict){
//        			 HashMap parent = new HashMap();
//    				 parent.put("id", location.getParents().getId());
//    				 parent.put("name", location.getParents().getName());
//    				 districtList.add(parent);
//        		 }
//        		 wardList.add(hashMap);
            }
        }
        List<OptionItem> province = new ArrayList<OptionItem>();
        List<OptionItem> district = new ArrayList<OptionItem>();
        List<OptionItem> ward = new ArrayList<OptionItem>();
        if (!provinceList.isEmpty()) {
            province.addAll(OptionItem.createOptionItemListFromHashMap(provinceList));

        }
        if (!districtList.isEmpty()) {
            district.addAll(OptionItem.createOptionItemListFromHashMap(districtList));

        }
        if (!wardList.isEmpty()) {
            ward.addAll(OptionItem.createOptionItemListFromHashMap(wardList));

        }
        data.put("provinceList", province);
        data.put("districtList", district);
        data.put("channelList", channelList);
        data.put("wardList", ward);
        //    data.put("employeeList", employerList);
        return data;
    }

    @ResponseBody
    @RequestMapping(value = "getDistrictByChannelId", method = RequestMethod.GET)
    public ModelAndView getCbListLocationByChannelId(HttpServletRequest request,
            Model uiModel, @RequestParam("parentId") Integer parentId,
            @RequestParam("channelId") Integer channelId) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        //  Integer channelId = filter.getChannelId();
        List<OptionItem> data = new ArrayList<>();
        try {
            String hql = "Select ChannelLocation.locations.id as id, ChannelLocation.locations.name as name, ChannelLocation.locations.locationType as locationType"
                    + " from ChannelLocation as ChannelLocation where deletedUser = 0"
                    + " and channels.id = " + channelId + " and locations.parents.id = " + parentId
                    + " and channels.companys.id = " + LoginContext.getLogin(request).getCompanyId();
            List<HashMap> list = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            if (!list.isEmpty()) {
                for (HashMap location : list) {
                    OptionItem item = new OptionItem();
                    item.setId((Integer) location.get("id"));
                    item.setName((String) location.get("name"));
                    data.add(item);
                }
            } else {
                List<Location> locations = dataService.getListOption(Location.class, new ParameterList("parents.id", parentId));
                if (!locations.isEmpty()) {
                    for (Location location : locations) {
                        OptionItem item = new OptionItem();
                        item.setId(location.getId());
                        item.setName(location.getName());
                        data.add(item);
                    }
                }
            }
            if (data.size() > 0) {
                Collections.sort(data, new OptionItemComparator());
            }
        } catch (Exception ex) {
            Logger.getLogger(WebMsalesPlanController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ModelAndView(jsonView, DATA_FIELD, data);
    }

    //get goods from goodsCategoryId
    @ResponseBody
    @RequestMapping(value = "getGoodsByGoodsCategoryId", method = RequestMethod.POST)
    public Map<String, Object> getGoodsByGoodsCategoryId(HttpServletRequest request,
            @RequestBody int goodsCategoryId) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.returnEmptyHashMap();
        } else if (login == 0 || login == 2) {
            return LoginContext.returnEmptyHashMap();
        }

        Map<String, Object> data = new HashMap<>();
        if (goodsCategoryId == 0) {
            data.put("goodsList", new ArrayList<>());
            return data;
        }
        String hql = "SELECT id AS id,name AS name"
                + " FROM Goods"
                + " WHERE deletedUser = 0"
                + " AND statuss.value = 1"
                + " AND goodsCategorys.statuss.value = 1"
                + " AND goodsCategorys.companys.id = :companyId";

        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", LoginContext.getLogin(request).getCompanyId());
        if (goodsCategoryId != 0) {
            hql += " AND goodsCategorys.id = :goodsCategoryId";
            parameters.add(MsalesParameter.create("goodsCategoryId", goodsCategoryId));
        }
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        data.put("goodsList", OptionItem.createOptionItemListFromHashMap(list));
        return data;
    }

    //get goods from goodsCategoryId
    @ResponseBody
    @RequestMapping(value = "/deletePromotion/{id}", method = RequestMethod.POST)
    public Map<String, Object> deletePromotion(HttpServletRequest request,
            @PathVariable(value = "id") int id) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.returnEmptyHashMap();
        } else if (login == 0 || login == 2) {
            return LoginContext.returnEmptyHashMap();
        }

        Map<String, Object> data = new HashMap<>();
        boolean success = false;

        try {
            Promotion promotion = dataService.getRowById(id, Promotion.class);
            if (promotion != null
                    && Objects.equals(promotion.getCompanys().getId(), LoginContext.getLogin(request).getCompanyId())
                    && promotion.getStatuss().getId() == 21) {
                promotion.setDeletedAt(new Date());
                promotion.setDeletedUser(LoginContext.getLogin(request).getId());
                dataService.updateRow(promotion);
                success = true;
            } else {
                success = false;
            }
        } catch (Exception ex) {
            success = false;
        }

        data.put("success", success);
        return data;
    }

    @RequestMapping(value = "/getListGoodsByGoodsCategoryIdOfPromotion", method = RequestMethod.POST)
    public @ResponseBody
    String getListGoodsByGoodsCategoryOfPromotion(HttpServletRequest request) throws IOException {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnString();
        }

        request = getContent(request);

        String response = goodsController.getListGoodsByGoodsCategoryIdOfPromotion(request);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getCbChannelPromotion")
    public Map<String, Object> getCbChannelPromotion(@RequestBody ArrayList<Integer> channelIds,
            HttpServletRequest request) {
        if (LoginContext.isLogin(request, dataService) != 1) {
            return LoginContext.returnEmptyHashMap();
        }

        Map<String, Object> data = new HashMap<>();
        // filterService.fillAreaSelectionForUser(filter, data);
        List<OptionItem> channelList = new ArrayList<>();

        List<OptionItem> provinceList = new ArrayList<>();
        //List<OptionItem> employerList = new ArrayList<>();

        if (channelIds != null && !channelIds.isEmpty()) {
            channelList.addAll(serviceFilter.getCbListChannelByListParentId(channelIds, dataService));
            // getLocationByChannelId -- chi tinh thanh
            provinceList.addAll(serviceFilter.getCbListLocationByListChannelId(channelIds, dataService));
        }

        // get employeeList by channelId
        // employerList.addAll(serviceFilter.getCbListUserByChannel(channelId, LoginContext.getLogin(request).getCompanyId(), dataService));
        data.put("channelList", channelList);
        data.put("provinceList", provinceList);
        //data.put("employeeList", employerList);
        return data;
    }

    @RequestMapping(value = "/channel/checkDuplicate")
    private @ResponseBody
    Map<String, Object> checkDuplicate(HttpServletRequest request) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.returnEmptyHashMap();
        } else if (login == 0 || login == 2) {
            return LoginContext.returnEmptyHashMap();
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        BufferedReader br = new BufferedReader(new InputStreamReader(
                request.getInputStream(), "UTF-8"));
        String json = "";
        if (br != null) {
            json = br.readLine();
        }
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        LinkedHashMap<String, Object> hash = MsalesJsonUtils.getObjectFromJSON(json, LinkedHashMap.class);
        if (!hash.isEmpty()) {
            String fullCode = hash.get("fullCode").toString().trim();
            boolean duplicate = checkChannelDupblicate(fullCode, loginUserInf);
            response.put("duplicated", duplicate);
        }

        return response;
    }

    private boolean checkChannelDupblicate(String fullCode, MsalesLoginUserInf loginUserInf) {
        ParameterList param = new ParameterList(1, 1);
        param.add("fullCode", fullCode);
        param.add("companys.id", loginUserInf.getCompanyId());
        List<Channel> channelFound = dataService.getListOption(Channel.class, param);
        return channelFound != null && !channelFound.isEmpty();
    }

    //get goodsUnit by GoodsId
    @ResponseBody
    @RequestMapping(value = "/getGoodsUnitByGoodsId")
    public Map getGoodsUnitByGoods(HttpServletRequest request, @RequestBody int goodsId) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.returnEmptyHashMap();
        }
        HashMap unit = promotionService.getGoodsUnitByGoodsId(goodsId, dataService);
        if (unit == null) {
            unit = new HashMap();
            unit.put("id", -1);
            unit.put("name", "Đơn vị nhỏ nhất");
        }
        return unit;
    }

    @ResponseBody
    @RequestMapping(value = "/getOtherUnitByOtherId")
    public Map getOtherUnitByOtherId(HttpServletRequest request, @RequestBody int otherId) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.returnEmptyHashMap();
        }
        HashMap unit = promotionService.getOtherUnitByOtherId(otherId, dataService);
        if (unit == null) {
            unit = new HashMap();
            unit.put("id", -1);
            unit.put("name", "Vật phẩm");
        }
        return unit;
    }

    //get goodsUnit by GoodsId
    @ResponseBody
    @RequestMapping(value = "/createGoodsAwardOther")
    public Map createGoodsAwardOther(HttpServletRequest request, @RequestBody PromotionAwardOther promotionAwardOther) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.returnEmptyHashMap();
        }
        Map data = new HashMap();
        promotionAwardOther.setStatusId(1);
        promotionAwardOther.setCompanyId(LoginContext.getLogin(request).getCompanyId());
        promotionAwardOther.setCreatedUser(LoginContext.getLogin(request).getId());
        boolean created = promotionService.createGoodsAwardOther(promotionAwardOther, dataService);
        data.put("success", created);
        if (created) {
            OptionItem optionItem = new OptionItem(promotionAwardOther.getId(), promotionAwardOther.getName());
            data.put("item", optionItem);
        }
        return data;
    }

    //get goodsUnit by GoodsId
    @ResponseBody
    @RequestMapping(value = "/deletePromotionImage")
    public Map deletePromotionImage(HttpServletRequest request, @RequestBody int id) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.returnEmptyHashMap();
        }
        Map data = new HashMap();
        Promotion promotion = dataService.getRowById(id, Promotion.class);
        if (promotion == null || promotion.getUrlImage() == null || promotion.getUrlImage().trim().isEmpty()) {
            data.put("success", false);
        } else {
            promotion.setUrlImage(null);
            try {
                dataService.updateRow(promotion);
                data.put("success", true);
            } catch (Exception ex) {
                data.put("success", false);
            }
        }
        return data;
    }

}
