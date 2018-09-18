/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.channel.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import vn.itt.msales.api.ajax.controller.WebAjaxController;
import vn.itt.msales.channel.model.WebMsalesLocationForm;
import vn.itt.msales.entity.Location;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.math.BigDecimal;
import java.util.Date;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.controller.MsalesUserController;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.URLManager;

/**
 *
 * @author vtmvietnam
 */
@Controller
public class WebMsalesLocationController {

    private String JsonGetLocationById = "{\"parentId\":%s}";
    private String JsonSearchLocationByText = "{\"parentId\":%s,\"searchText\":\"%s\"}";

    @Autowired
    private DataService dataService;

    @RequestMapping(value = "/settings/list/wards", method = {RequestMethod.POST, RequestMethod.GET})
    public String wards(HttpServletRequest request, @ModelAttribute(value = "locationForm") WebMsalesLocationForm msalesLocationForm,
            @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size)
            throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        int maxPages = 1;
        WebMsalesLocationForm mModel = new WebMsalesLocationForm();
        mModel = msalesLocationForm;
        request.setAttribute("agentId", mModel.getAgentId());
        request.setAttribute("provinceId", mModel.getProvinceId());
        request.setAttribute("districtId", mModel.getDistrictId());
        getListAgent(request);
//        if (mModel.getAgentId() > 0) {
//            getListProvinceByParentId(request, mModel.getAgentId());
//            if (mModel.getProvinceId() > 0) {
//                getListDictrist(request, mModel.getProvinceId());
//            }
//        } else {
//            request.setAttribute("provinceList", null);
//            request.setAttribute("dictristList", null);
//        }
        getListProvince(request);
        if (mModel.getProvinceId() > 0) {
            getListDictrist(request, mModel.getProvinceId());
        }
        int parentId = 0;
        if (mModel.getDistrictId() > 0) {
            parentId = mModel.getDistrictId();
        } else {
            if (mModel.getProvinceId() > 0) {
                parentId = mModel.getProvinceId();
            } else {
                parentId = mModel.getAgentId();
            }
        }

        ParameterList parameterList = new ParameterList(page, size);
        String searchText = msalesLocationForm.getSearchText();

        if (searchText == null || searchText.trim().isEmpty()) {
            if (parentId == 0) {
                parameterList.add("locationType", 1);
            } else {
                parameterList.add("parents.id", parentId);
            }
        } else {
            if (parentId == 0) {
                parameterList.add("locationType", 1);
            } else {
                parameterList.add("parents.id", parentId);
            }
            if (searchText != null && !searchText.trim().isEmpty()) {
                parameterList.or("name", searchText, "like", "code", searchText, "like");
            }
        }

        MsalesResults<Location> results = dataService.getListOption(Location.class, parameterList, true);
        List<Location> locations = results.getContentList();
        int count = Integer.parseInt(results.getCount().toString());
        if (count % size != 0) {
            maxPages = count / size + 1;
        } else {
            maxPages = count / size;
        }
        if (page > maxPages) {
            page = maxPages;
        }
        request.setAttribute("wardList", locations);
        request.setAttribute("page", page);
        request.setAttribute("size", size);
        request.setAttribute("maxPages", maxPages);
        return "wards";
//        if (page == null) {
//            page = 1;
//        }
//        if (size == null) {
//            size = 10;
//        }
//        WebMsalesLocationForm mModel = new WebMsalesLocationForm();
//        mModel = model;
//        request.setAttribute("agentId", mModel.getAgentId());
//        request.setAttribute("provinceId", mModel.getProvinceId());
//        request.setAttribute("districtId", mModel.getDistrictId());
//        getListAgent(request);
//        if (mModel.getAgentId() > 0) {
//            getListProvinceByParentId(request, mModel.getAgentId());
//            if (mModel.getProvinceId() > 0) {
//                getListDictrist(request, mModel.getProvinceId());
//            }
//        } else {
//            request.setAttribute("provinceList", null);
//            request.setAttribute("dictristList", null);
//        }
//        int parentId = 0;
//        if (mModel.getDistrictId() > 0) {
//            parentId = mModel.getDistrictId();
//        } else {
//            if (mModel.getProvinceId() > 0) {
//                parentId = mModel.getProvinceId();
//            } else {
//                parentId = mModel.getAgentId();
//            }
//        }
//
//        String mLocation = new String();
//        if (mModel.getSearchText() == null || mModel.getSearchText().trim().isEmpty()) {
//            String jsonPost = String.format(JsonGetLocationById, parentId);
//            request.setAttribute(MsalesConstants.CONTENTS, jsonPost);
//            request.setAttribute(MsalesConstants.PAGE, new MsalesPageRequest(page, size));
//            mLocation = locationController.getListLocationByParentId(request);
//        } else {
//            String jsonPost = String.format(JsonSearchLocationByText, parentId, mModel.getSearchText());
//            request.setAttribute(MsalesConstants.CONTENTS, jsonPost);
//            request.setAttribute(MsalesConstants.PAGE, new MsalesPageRequest(page, size));
//            mLocation = locationController.searchLocation(request);
//        }
//
//        LinkedHashMap<String, Object> jsonHash = (LinkedHashMap) MsalesJsonUtils.getObjectFromJSON(mLocation, LinkedHashMap.class);
//        Location[] mListOption = null;
//        if (MsalesData.checkCode(jsonHash)) {
//            mListOption = MsalesResponse.createMsalesResponseFromString(mLocation).parseContentsList(Location[].class);
//            int maxPage = 1;
//            try {
//                MsalesResponse response = MsalesJsonUtils.getObjectFromJSON(mLocation, MsalesResponse.class);
//                if (response.getStatus().getCode() == HttpStatus.OK.value()) {
//                    //thanh cong
//                    maxPage = (Integer) ((LinkedHashMap) response.getContents()).get("count");
//                    if (maxPage % size != 0) {
//                        maxPage = maxPage / size + 1;
//                    } else {
//                        maxPage = maxPage / size;
//                    }
//                }
//            } catch (Exception e) {
//            }
//
//            request.setAttribute("wardList", mListOption);
//            request.setAttribute("page", page);
//            request.setAttribute("size", size);
//            request.setAttribute("maxPages", maxPage);
//
//        }
//
//        return new ModelAndView("locationWards", "locationForm", mModel);
    }

    @RequestMapping(value = "/settings/list/wardForm")
    public String wardForm(HttpServletRequest request) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        return "locationWardForm";
    }

    @RequestMapping(value = "/settings/location/wardFormEdit/{agentId}/{cityId}/{districtId}/{id}", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView wardEditForm(HttpServletRequest request, @PathVariable(value = "agentId") int agentId,
            @PathVariable(value = "cityId") int cityId, @PathVariable(value = "districtId") int districtId, @PathVariable(value = "id") int id,
            @ModelAttribute(value = "locationEditForm") @Valid Location location, BindingResult bindingResult) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }
        request.setAttribute("readonly", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"}));
        if (request.getMethod().equals("GET")) {
//            location = locationController.getDataService().getRowById(id, Location.class);

            location = dataService.getRowById(id, Location.class);
            if (location != null) {
                if (location.getLat().intValue()==0) {
                    location.setLat(BigDecimal.ZERO);

                }
                if (location.getLng().intValue()==0) {
                    location.setLng(BigDecimal.ZERO);
                }
            }
            if (location == null) {
                return new ModelAndView("userError");//bao loi - sua sau
            }
        } else {// Post		
//            if (!bindingResult.hasErrors()) {
            try {
                Location rootLocation = dataService.getRowById(location.getId(), Location.class);
//                    Location rootLocation = locationController.getDataService().getRowById(location.getId(), Location.class);
                if (rootLocation == null) {
                    return new ModelAndView("userError");//bao loi - sua sau
                }

                rootLocation.setUpdatedUser(LoginContext.getLogin(request).getId());
                rootLocation.setUpdatedAt(new Date());
                rootLocation.setName(location.getName());
                rootLocation.setLat(location.getLat());
                rootLocation.setLng(location.getLng());
                rootLocation.setNote(location.getNote());
                int result = dataService.updateRow(rootLocation);
//                    int result = locationController.getDataService().updateRow(rootLocation);
                if (result > 0) {
                    request.setAttribute("success", true);
                } else {
                    request.setAttribute("success", false);
                }
            } catch (Exception ex) {
                System.out.println("loi");
            }
//            }
        }
        getListProvince(request);
        getListDictrist(request, cityId);
        request.setAttribute("cityId", cityId);
        return new ModelAndView("wardEditForm", "locationEditForm", location);
    }

    /**
     * Get List Agent
     *
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    private void getListAgent(HttpServletRequest request) throws IOException {
        ParameterList parameterList = new ParameterList();
        parameterList.add("locationType", 0);
        MsalesResults<Location> results = dataService.getListOption(Location.class, parameterList, true);
        List<Location> locations = results.getContentList();
        request.setAttribute("agentList", locations);
//        String jsonPost = "{\"locationType\": 0}";
//        request.setAttribute(MsalesConstants.CONTENTS, jsonPost);
//        String mLocation = locationController.getCbListLocationByLocationType(request);
//        LinkedHashMap<String, Object> jsonHash = (LinkedHashMap) MsalesJsonUtils.getObjectFromJSON(mLocation, LinkedHashMap.class);
//        List<WebMsalesOptionItem> mListOption = null;
//        if (MsalesData.checkCode(jsonHash)) {
//            mListOption = (ArrayList) MsalesData.getListOption(jsonHash);
//        }
//        request.setAttribute("agentList", mListOption);

    }

    /**
     * Get List City/Province
     *
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    private void getListProvince(HttpServletRequest request) throws IOException {
        ParameterList parameterList = new ParameterList();
        parameterList.add("locationType", 1);
        MsalesResults<Location> results = dataService.getListOption(Location.class, parameterList, true);
        List<Location> locations = results.getContentList();
        request.setAttribute("provinceList", locations);

//        String jsonPost = "{\"locationType\": 1}";
//        request.setAttribute(MsalesConstants.CONTENTS, jsonPost);
//        String mLocation = locationController.getCbListLocationByLocationType(request);
//        LinkedHashMap<String, Object> jsonHash = (LinkedHashMap) MsalesJsonUtils.getObjectFromJSON(mLocation, LinkedHashMap.class);
//        List<WebMsalesOptionItem> mListOption = null;
//        if (MsalesData.checkCode(jsonHash)) {
//            mListOption = (ArrayList) MsalesData.getListOption(jsonHash);
//        }
//        request.setAttribute("provinceList", mListOption);
    }

    private void getListProvinceByParentId(HttpServletRequest request, int i) throws IOException {
        ParameterList parameterList = new ParameterList(0, 0);
        parameterList.add("parents.id", i);
        MsalesResults<Location> results = dataService.getListOption(Location.class, parameterList, true);
        List<Location> locations = results.getContentList();
        request.setAttribute("provinceList", locations);

//        String jsonPost = String.format("{\"parentId\":%s}", i);
//        String pages = "{\"pageNo\": 0, \"recordsInPage\": 0}";
//        request.setAttribute(MsalesConstants.CONTENTS, jsonPost);
//        request.setAttribute(MsalesConstants.PAGE, pages);
//        String mLocation = locationController.getCbListLocationByParentId(request);
//        LinkedHashMap<String, Object> jsonHash = (LinkedHashMap) MsalesJsonUtils.getObjectFromJSON(mLocation, LinkedHashMap.class);
//        List<WebMsalesOptionItem> mListOption = null;
//        if (MsalesData.checkCode(jsonHash)) {
//            mListOption = (ArrayList) MsalesData.getListOption(jsonHash);
//        }
//        request.setAttribute("provinceList", mListOption);
    }

    /**
     * Get List Dictrist
     *
     * @param request
     * @throws IOException
     */
    private void getListDictrist(HttpServletRequest request, int i) throws IOException {
        ParameterList parameterList = new ParameterList(0, 0);
        parameterList.add("parents.id", i);
        MsalesResults<Location> results = dataService.getListOption(Location.class, parameterList, true);
        List<Location> locations = results.getContentList();
        request.setAttribute("dictristList", locations);

//        String jsonPost = String.format("{\"parentId\":%s}", i);
//        String pages = "{\"pageNo\": 0, \"recordsInPage\": 0}";
//        request.setAttribute(MsalesConstants.CONTENTS, jsonPost);
//        request.setAttribute(MsalesConstants.PAGE, pages);
//        String mLocation = locationController.getCbListLocationByParentId(request);
//        LinkedHashMap<String, Object> jsonHash = (LinkedHashMap) MsalesJsonUtils.getObjectFromJSON(mLocation, LinkedHashMap.class);
//        List<WebMsalesOptionItem> mListOption = null;
//        if (MsalesData.checkCode(jsonHash)) {
//            mListOption = (ArrayList) MsalesData.getListOption(jsonHash);
//        }
//        request.setAttribute("dictristList", mListOption);
    }

    private void validateInputData(Location location, BindingResult bindingResult, boolean isAdd) {
        // Name
        String name = location.getName();
        if (name == null || name.trim().isEmpty()) {
            bindingResult.rejectValue("name", "location_name", "empty_error_code");
        }

        // Short name
        String code = location.getCode();
        if (code == null || code.trim().isEmpty()) {
            bindingResult.rejectValue("code", "location_code", "empty_error_code");
        }
        if (location.getLat() == null) {
            bindingResult.rejectValue("lat", "location_lat", "empty_error_code");
        }
        if (location.getLng() == null) {
            bindingResult.rejectValue("lng", "location_lng", "empty_error_code");
        }
    }

}
