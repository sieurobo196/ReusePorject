package vn.itt.msales.customercare.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.itt.msales.channel.controller.MsalesChannelController;
import vn.itt.msales.channel.controller.MsalesChannelTypeController;
import vn.itt.msales.channel.controller.MsalesLocationController;
import vn.itt.msales.common.HttpUtil;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.customercare.model.TimeHour;
import vn.itt.msales.customercare.services.CustomerCareService;
import vn.itt.msales.entity.User;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.controller.MsalesUserController;
import vn.itt.msales.user.controller.MsalesUserRouteController;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;
import vn.itt.msales.workflow.service.ServiceMCP;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.sales.service.ServiceFilter;

/*
 import vn.itt.base.services.ConvertParameters;
 import vn.itt.base.utils.ItemLong;
 import vn.itt.customercare.models.JsonAllSalemanPoint;
 import vn.itt.customercare.models.JsonSalemanPoint;
 import vn.itt.customercare.models.NguoiSuDung;
 import vn.itt.customercare.models.PhuongXa;
 import vn.itt.customercare.models.QuanHuyen;
 import vn.itt.customercare.models.SalemanPoint;
 import vn.itt.customercare.models.UserRole;
 import vn.itt.customercare.models.filters.UserListFilter;
 import vn.itt.customercare.services.DiemBanHangService;
 import vn.itt.customercare.services.FilterService;
 import vn.itt.customercare.services.LoTrinhService;
 import vn.itt.customercare.services.NguoiSuDungService;
 import vn.itt.customercare.services.PhuongXaService;
 import vn.itt.customercare.services.PosCareInforService;
 import vn.itt.customercare.services.QuanHuyenService;
 import vn.itt.customercare.services.TinhThanhPhoService;
 import vn.itt.utils.GPSUtils;
 import vn.itt.utils.HttpUtil;
 import flexjson.JSONSerializer;
 import java.util.LinkedHashMap;
 import vn.itt.api.common.MsalesConstants;
 import vn.itt.api.rest.MsalesData;
 import static vn.itt.api.rest.MsalesData.getData;
 import vn.itt.base.utils.ValidatorUtil;
 */
@Controller
public class WebMsalesMapController {
    /*
     @Autowired
     private TinhThanhPhoService tinhThanhPhoService;
     @Autowired
     private QuanHuyenService quanHuyenService;
     @Autowired
     private PhuongXaService phuongXaService;
     @Autowired
     private PosCareInforService posCareInforService;
     @Autowired
     private DiemBanHangService diemBanHangService;
     @Autowired
     private NguoiSuDungService userService;
    
     @Autowired
     private LoTrinhService loTrinhService;
     //hello
    
     @Autowired
     FilterService filterService;
     */

    @Autowired
    DataService dataService;
    @Autowired
    ServiceMCP serviceMCP;
    @Autowired
    private ServletContext context;
    @Autowired
    private MsalesChannelTypeController channelTypeController;

    @Value("#{systemPros['system.map.lat']}")
    private String mapLat;
    @Value("#{systemPros['system.map.lng']}")
    private String mapLng;
    @Value("#{systemPros['system.map.type']}")
    private String mapType;
    @Value("#{systemPros['system.map.zoom']}")
    private String mapZoom;

    private String dateFormat = "dd/MM/yyyy";
    
    @Autowired
    MsalesLocationController locationController;
    @Autowired
    MsalesChannelController channelController;
    @Autowired
    MsalesUserRouteController userRouteController;

    @Autowired
    private ServiceFilter serviceFilter;

    @RequestMapping(value = "/map/salesman")
    public String displayMap(@RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "mapType", required = false) String mapType,
            @RequestParam(value = "zoom", required = false) Double zoom,
            HttpServletRequest request, Model uiModel, Boolean isSupervisor,
            @ModelAttribute(value = "searchForm") Filter filter) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        request.setAttribute("salesSup", false);
        //Lấy listUser
        filter.setRoles(new Integer[]{6});
        uiModel.addAttribute("roles", filter.getRolesString());
        List<OptionItem> employerList = filter.processFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());
        //Khởi tạo thời gian
        List<TimeHour> listHour = new ArrayList<TimeHour>();
        listHour.add(new TimeHour(0, ""));
        for (int i = 0; i < 24; i++) {
            TimeHour timeHour = new TimeHour();
            timeHour.setId(i + 1);
            if (i < 10) {
                timeHour.setName("0" + i);
            } else {
                timeHour.setName(i + "");
            }
            listHour.add(timeHour);
        }
        uiModel.addAttribute("listHour", listHour);
        List<TimeHour> listMin = new ArrayList<TimeHour>();
        listMin.add(new TimeHour(0, ""));
        for (int i = 0; i < 60; i++) {
            TimeHour timeHour = new TimeHour();
            timeHour.setId(i + 1);
            if (i < 10) {
                timeHour.setName("0" + i);
            } else {
                timeHour.setName(i + "");
            }
            listMin.add(timeHour);
        }
        uiModel.addAttribute("listMin", listMin);

        SimpleDateFormat sdfHQL = new SimpleDateFormat("yyyy/MM/dd");
		//SimpleDateFormat sdfHQL2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        //Nếu POST
        if ("POST".equals(request.getMethod())) {
            Date date = HttpUtil.getRequestDateParammeter(request, "frm_from_date", dateFormat);
            if (date == null) {
                date = new Date();
            }
            uiModel.addAttribute("frm_from_date", date);
            //Dữ liệu:
            int userId = filter.getUserId() == null ? 0 : filter.getUserId();
            List<User> users = new ArrayList<User>();
            if (userId == 0) {
                //Danh sách nhân viên có
                if (employerList.size() > 1) {
                    //lay tat ca
                    for (int i = 1; i < employerList.size(); i++) {
                        int id = employerList.get(i).getId();
                        if (id != 0) {
                            User user = new User();
                            user.setId(id);
                            user.setName(employerList.get(i).getName());
                            users.add(user);
                        }
                    }
                }
            } else {
                //lay theo nhan vien
                User user = dataService.getRowById(userId, User.class);
                if (user != null) {
                    user.setName(user.getLastName() + " " + user.getFirstName());
                    users.add(user);
                }
            }
            //Lấy ngày giờ
            String fromDate = null;
            String toDate = null;
            String date2 = null;
            if (date != null) {
                date2 = sdfHQL.format(date);
                if (filter.getFromHour() == 0) {
                    fromDate = " 00:";
                    filter.setFromHour(1);
                } else {
                    fromDate = " " + (filter.getFromHour() - 1) + ":";

                }
                if (filter.getFromMin() == 0) {
                    fromDate += "00:";
                    filter.setFromMin(1);
                } else {
                    fromDate += (filter.getFromMin() - 1) + ":";
                }
                fromDate += "00";
                if (filter.getToHour() == 0) {
                    toDate = " 23:";
                    filter.setToHour(24);
                } else {
                    toDate = " " + (filter.getToHour() - 1) + ":";
                }
                if (filter.getToMin() == 0) {
                    toDate += "59:";
                    filter.setToMin(60);
                } else {
                    toDate += (filter.getToMin() - 1) + ":";
                }
                toDate += "59";
            }
            if (date != null) {
                fromDate = date2 + fromDate;
                toDate = date2 + toDate;
            }
//            if(date == null){
//            	toDate = sdfHQL2.format(new Date());
//            }

            List<LinkedHashMap> items = new ArrayList<>();
            for (User user : users) {

                LinkedHashMap item = new LinkedHashMap();
                if (user != null) {
                    item.put("userId", user.getId());
                    item.put("userName", user.getName());
                }
                String hql = serviceMCP.getStringHQL(user.getId(), fromDate, toDate);
                List posLists = new ArrayList();
                List<HashMap> list = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
                LinkedHashMap pos;
                for (HashMap mcpDetails : list) {
                    // HashMap mcpDetails = (HashMap) iterator.next();
                    //POS APP
                    pos = new LinkedHashMap();
                    pos.put("id", ((POS) mcpDetails.get("pos")).getId());
                    pos.put("mcpDetailsId", mcpDetails.get("mcpDetailsId"));
                    pos.put("soDT", ((POS) mcpDetails.get("pos")).getTel());
                    //pos.put("posCode", ((POS) mcpDetails.get("pos")).getPosCode());
                    pos.put("name", ((POS) mcpDetails.get("pos")).getName());
                    pos.put("address", ((POS) mcpDetails.get("pos")).getAddress());
                    pos.put("lat", ((POS) mcpDetails.get("pos")).getLat());
                    pos.put("lng", ((POS) mcpDetails.get("pos")).getLng());
                    pos.put("lng", ((POS) mcpDetails.get("pos")).getLng());
                    pos.put("isVisited", mcpDetails.get("isVisited"));
                    pos.put("timeCared", mcpDetails.get("timeCared"));
                    Date beginDate = (Date) mcpDetails.get("beginDate");
                    //get timesale - tranType = 2
                    String hqlDelivery = serviceMCP.getStringHQLDelivery(userInf.getCompanyId(), beginDate, ((POS) mcpDetails.get("pos")).getId(), fromDate, toDate);
                    List<HashMap> salesTransList = dataService.executeSelectHQL(HashMap.class, hqlDelivery, true, 1, 1);
                    if (!salesTransList.isEmpty()) //get timedelevery 
                    {
                    //	String timeSale = salesTransList.get(0).get("timeSale").toString();
                        //	String timeDelivery = salesTransList.get(0).get("timeDelivery").toString();
                        pos.put("timeSale", salesTransList.get(0).get("timeSale"));
                        int salesTransId = (int) salesTransList.get(0).get("id");
                        if (salesTransId != 0) {
                            SalesTrans salesTrans = dataService.getRowById(salesTransId, SalesTrans.class);
                            if (salesTrans != null && salesTrans.getOrders() != null) {
                                pos.put("timeDelivery", salesTrans.getOrders().getSalesTransDate());
                            } else {
                                pos.put("timeDelivery", null);
                            }
                        }

                    } else {
                        pos.put("timeSale", null);
                        pos.put("timeDelivery", null);
                    }
                    posLists.add(pos);
                }
                String hqlRoute = serviceMCP.getStringHQLUserRoute(user.getId(), fromDate, toDate);                
                List<HashMap> routeList = dataService.executeSelectHQL(HashMap.class, hqlRoute, true, 0, 0);

                item.put("posList", posLists);
                item.put("routes", routeList);
                items.add(item);
            }
            String ttCskh = MsalesJsonUtils.getJSONFromOject(items);
            uiModel.addAttribute("items", ttCskh);
        }

        //lưu lại kiểu bản đồ, tâm bản đồ và độ phóng đại
        if (latitude == null) {
            latitude = 10.785250547433419;
        }
        if (longitude == null) {
            longitude = 106.6841983795166;
        }
        if (mapType == null) {
            mapType = "ROADMAP";
        }
        if (zoom == null) {
            zoom = 13.;
        }
        uiModel.addAttribute("latitude", latitude);
        uiModel.addAttribute("longitude", longitude);
        uiModel.addAttribute("mapType", mapType);
        uiModel.addAttribute("zoom", zoom);
        uiModel.addAttribute("searchForm", filter);
        // return view page
        return "map";
    }

    @RequestMapping(value = "/map/staff")
    public String showStaffOnMap(
            @RequestParam(value = "staffId", required = false) String staffId,
            @RequestParam(value = "fromDate", required = false) String fDate,
            HttpServletRequest request, Model uiModel) throws ParseException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        // return view page
        return "map";
    }

    @RequestMapping(value = "/map/salesSup")
    public String displayMapSup(@RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "mapType", required = false) String mapType,
            @RequestParam(value = "zoom", required = false) Double zoom,
            HttpServletRequest request, Model uiModel, Boolean isSupervisor,
            @ModelAttribute(value = "searchForm") Filter filter) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        request.setAttribute("salesSup", true);
        //Lấy listUser
        filter.setRoles(new Integer[]{4});
        uiModel.addAttribute("roles", filter.getRolesString());
        List<OptionItem> employerList = filter.processFilter(uiModel, request, serviceFilter, dataService, LoginContext.getLogin(request).getCompanyId());//Khởi tạo thời gian
        List<TimeHour> listHour = new ArrayList<TimeHour>();
        listHour.add(new TimeHour(0, ""));
        for (int i = 0; i < 24; i++) {
            TimeHour timeHour = new TimeHour();
            timeHour.setId(i + 1);
            if (i < 10) {
                timeHour.setName("0" + i);
            } else {
                timeHour.setName(i + "");
            }
            listHour.add(timeHour);
        }
        uiModel.addAttribute("listHour", listHour);
        List<TimeHour> listMin = new ArrayList<TimeHour>();
        listMin.add(new TimeHour(0, ""));
        for (int i = 0; i < 60; i++) {
            TimeHour timeHour = new TimeHour();
            timeHour.setId(i + 1);
            if (i < 10) {
                timeHour.setName("0" + i);
            } else {
                timeHour.setName(i + "");
            }
            listMin.add(timeHour);
        }
        uiModel.addAttribute("listMin", listMin);

        //Nếu POST
        if ("POST".equals(request.getMethod())) {
//    		SimpleDateFormat sdfHQL = new SimpleDateFormat("yyyy/MM/dd");
//    		SimpleDateFormat sdfHQL2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//            Date date = HttpUtil.getRequestDateParammeter(request, "frm_from_date", dateFormat);
//            if(date != null){
//            	uiModel.addAttribute("frm_from_date", date);
//            }
            SimpleDateFormat sdfHQL = new SimpleDateFormat("yyyy/MM/dd");
            //SimpleDateFormat sdfHQL2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = HttpUtil.getRequestDateParammeter(request, "frm_from_date", dateFormat);
            if (date == null) {
                date = new Date();
            }
            uiModel.addAttribute("frm_from_date", date);
            //Dữ liệu:
            int userId = filter.getUserId() == null ? 0 : filter.getUserId();
            List<User> users = new ArrayList<User>();
            if (userId == 0) {
                //Danh sách nhân viên có
                if (employerList.size() > 1) {
                    //lay tat ca
                    for (int i = 1; i < employerList.size(); i++) {
                        int id = employerList.get(i).getId();
                        if (id != 0) {
                            User user = new User();
                            user.setId(id);
                            user.setName(employerList.get(i).getName());
                            users.add(user);
                        }
                    }
                }
            } else {
                //lay theo nhan vien
                User user = dataService.getRowById(userId, User.class);
                if (user != null) {
                    user.setName(user.getLastName() + " " + user.getFirstName());
                    users.add(user);
                }
            }
            //Lấy ngày giờ
            String fromDate = null;
            String toDate = null;
            String date2 = null;
            if (date != null) {
                date2 = sdfHQL.format(date);
                if (filter.getFromHour() == 0) {
                    fromDate = " 00:";
                    filter.setFromHour(1);
                } else {
                    fromDate = " " + (filter.getFromHour() - 1) + ":";

                }
                if (filter.getFromMin() == 0) {
                    fromDate += "00:";
                    filter.setFromMin(1);
                } else {
                    fromDate += (filter.getFromMin() - 1) + ":";
                }
                fromDate += "00";
                if (filter.getToHour() == 0) {
                    toDate = " 23:";
                    filter.setToHour(24);
                } else {
                    toDate = " " + (filter.getToHour() - 1) + ":";
                }
                if (filter.getToMin() == 0) {
                    toDate += "59:";
                    filter.setToMin(60);
                } else {
                    toDate += (filter.getToMin() - 1) + ":";
                }
                toDate += "59";
            }
            if (date != null) {
                fromDate = date2 + fromDate;
                toDate = date2 + toDate;
            }
//            if(date == null){
//            	toDate = sdfHQL2.format(new Date());
//            }

            List<LinkedHashMap> items = new ArrayList<>();
            for (User user : users) {

                LinkedHashMap item = new LinkedHashMap();
                if (user != null) {
                    item.put("userId", user.getId());
                    item.put("userName", user.getName());
                }
                String hql = serviceMCP.getStringHQL(user.getId(), fromDate, toDate);
                List posLists = new ArrayList();
                List<HashMap> list = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
                LinkedHashMap pos;
                for (HashMap mcpDetails : list) {
                    // HashMap mcpDetails = (HashMap) iterator.next();
                    //POS APP
                    pos = new LinkedHashMap();
                    pos.put("id", ((POS) mcpDetails.get("pos")).getId());
                    pos.put("mcpDetailsId", mcpDetails.get("mcpDetailsId"));
                    pos.put("soDT", ((POS) mcpDetails.get("pos")).getTel());
                    //pos.put("posCode", ((POS) mcpDetails.get("pos")).getPosCode());
                    pos.put("name", ((POS) mcpDetails.get("pos")).getName());
                    pos.put("address", ((POS) mcpDetails.get("pos")).getAddress());
                    pos.put("lat", ((POS) mcpDetails.get("pos")).getLat());
                    pos.put("lng", ((POS) mcpDetails.get("pos")).getLng());
                    pos.put("lng", ((POS) mcpDetails.get("pos")).getLng());
                    pos.put("isVisited", mcpDetails.get("isVisited"));
                    pos.put("timeCared", mcpDetails.get("timeCared"));
                    Date beginDate = (Date) mcpDetails.get("beginDate");
                    //get timesale - tranType = 2
                    String hqlDelivery = serviceMCP.getStringHQLDelivery(userInf.getCompanyId(), beginDate, ((POS) mcpDetails.get("pos")).getId(), fromDate, toDate);
                    List<HashMap> salesTransList = dataService.executeSelectHQL(HashMap.class, hqlDelivery, true, 0, 0);
                    if (!salesTransList.isEmpty()) //get timedelevery 
                    {
                        String timeSale = salesTransList.get(0).get("timeSale").toString();
                        String timeDelivery = salesTransList.get(0).get("timeDelivery").toString();
                        pos.put("timeSale", timeSale);
                        pos.put("timeDelivery", timeDelivery);
                    } else {
                        pos.put("timeSale", null);
                        pos.put("timeDelivery", null);
                    }
                    posLists.add(pos);
                }
                String hqlRoute = serviceMCP.getStringHQLUserRoute(user.getId(), fromDate, toDate);
                List<HashMap> routeList = dataService.executeSelectHQL(HashMap.class, hqlRoute, true, 0, 0);

                item.put("posList", posLists);
                item.put("routes", routeList);
                items.add(item);
            }
            String ttCskh = MsalesJsonUtils.getJSONFromOject(items);
            uiModel.addAttribute("items", ttCskh);
        }

        //lưu lại kiểu bản đồ, tâm bản đồ và độ phóng đại
        if (latitude == null) {
            latitude = 10.785250547433419;
        }
        if (longitude == null) {
            longitude = 106.6841983795166;
        }
        if (mapType == null) {
            mapType = "ROADMAP";
        }
        if (zoom == null) {
            zoom = 13.;
        }
        uiModel.addAttribute("latitude", latitude);
        uiModel.addAttribute("longitude", longitude);
        uiModel.addAttribute("mapType", mapType);
        uiModel.addAttribute("zoom", zoom);
        uiModel.addAttribute("searchForm", filter);
        // return view page
        return "map";
    }
}
