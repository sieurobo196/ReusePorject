/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.goods.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsCategory;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;

import vn.itt.msales.goods.controller.model.GoodsCategoryFilter;
import vn.itt.msales.goods.controller.services.goodsServices;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;
import vn.itt.msales.user.model.URLManager;

/**
 *
 * @author vtm_2
 */
@Controller
public class WebGoodsCategoryController {

    @Autowired
    private DataService dataService;
    
    @Autowired
    private goodsServices gServices;

    @RequestMapping(value = "/category/list/")
    public String listCategory(Model uiModel, HttpServletRequest request, GoodsCategory goodsCategory,
            @ModelAttribute(value = "catListForm") GoodsCategoryFilter categoryFilter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        uiModel.addAttribute("only", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"}));

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        int companyId = loginUserInf.getCompanyId();
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;
        //Cb Status
        ParameterList parameterList1 = new ParameterList();
        parameterList1.add("statusTypes.id", 7);
        MsalesResults<Status> status = dataService.getListOption(Status.class, parameterList1, true);
        List<Status> statusList = status.getContentList();
        //search GoodsCategory
        String key = categoryFilter.getSearchText();

        // List GoodsCategory
        ParameterList parameterList = new ParameterList(page, size);
        parameterList.add("companys.id", companyId);

        if (key != null) {
            parameterList.or("goodsCode", key, "like", "name", key, "like");
        }
        if (categoryFilter.getStatusId() != null) {
            int statusId1 = Integer.parseInt(categoryFilter.getStatusId().toString());
            parameterList.add("statuss.id", statusId1);
        }
        parameterList.setOrder("createdAt", "desc");
        MsalesResults<GoodsCategory> list = dataService.getListOption(GoodsCategory.class, parameterList, true);
        List<GoodsCategory> goodsCategoryList = list.getContentList();
        int count = Integer.parseInt(list.getCount().toString());
        if (count % size != 0) {
            maxPages = count / size + 1;
        } else {
            maxPages = count / size;
        }
        if (page > maxPages) {
            page = maxPages;
        }
        uiModel.addAttribute("statusList", statusList);
        uiModel.addAttribute("goodsCategoryList", goodsCategoryList);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);
        return "categoryList";
        /*
         int maxPages = 1;
         if (page == null) {
         page = 1;
         }
         if (size == null) {
         size = 10;
         }
         request.setAttribute(MsalesConstants.CONTENTS, "{\"statusTypeId\":7}");
         String responseString = msalesStatusController.getCbListStatusByStatusTypeId(request);
         //ArrayList statusList = (ArrayList) ((LinkedHashMap)responseString.getContents()).get("contentList");
         List<OptionItem> statusList = new ArrayList<>();
         statusList.addAll(OptionItem.createOptionListFromResponseString(responseString));
         uiModel.addAttribute("statusList", statusList);

         //search GoodsCategory
         String json = "";
         String obj = "";
         if (categoryFilter.getSearchText() == null && categoryFilter.getStatusId() == null) {
         int companyId = 1;
         request.setAttribute(MsalesConstants.CONTENTS, "{\"companyId\": " + companyId + "}");
         request.setAttribute(MsalesConstants.PAGE, new MsalesPageRequest(page, size));
         obj = categoryController.getListGoodsCategory(request);
         } else {
         LinkedHashMap searchObject = new LinkedHashMap();
         searchObject.put("searchText", categoryFilter.getSearchText());
         searchObject.put("statusId", categoryFilter.getStatusId());
         json = MsalesJsonUtils.getJSONFromOject(searchObject);
         request.setAttribute(MsalesConstants.CONTENTS, json);
         request.setAttribute(MsalesConstants.PAGE, new MsalesPageRequest(page, size));
         obj = categoryController.searchGoodsCategory(request);
         }

         LinkedHashMap<String, Object> goodsCategoryList = MsalesJsonUtils.getObjectFromJSON(obj, LinkedHashMap.class);
         LinkedHashMap<String, Object> content = (LinkedHashMap) goodsCategoryList.get("contents");
         String contentList = MsalesJsonUtils.getJSONFromOject(content.get("contentList"));
         String countList = MsalesJsonUtils.getJSONFromOject(content.get("count"));
         GoodsCategory[] list = MsalesJsonUtils.getObjectFromJSON(contentList, GoodsCategory[].class);

         int count = Integer.parseInt(countList);
         if (count % size != 0) {
         maxPages = count / size + 1;
         } else {
         maxPages = count / size;
         }

         uiModel.addAttribute("goodsCategoryList", list);
         uiModel.addAttribute("page", page);
         uiModel.addAttribute("size", size);
         uiModel.addAttribute("maxPages", maxPages);

         return "categoryList";
         */
    }

    @RequestMapping(value = "/category/view/{id}")
    public String viewCategory(HttpServletRequest request, Model uiModel, @PathVariable("id") int id, GoodsCategory goodsCategory)
            throws NoSuchAlgorithmException, JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        uiModel.addAttribute("readonly", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"}));
        goodsCategory = dataService.getRowById(id, GoodsCategory.class);
        if(goodsCategory != null){
        	request.setAttribute("goods_code", goodsCategory.getGoodsCode().toLowerCase().trim());
        }
        uiModel.addAttribute("view", true);
        return productDetail(uiModel, request, goodsCategory);
        /*
         request.setAttribute(MsalesConstants.CONTENTS, "{\"id\": " + id + "}");
         String json = categoryController.getGoodsCategory(request);
         LinkedHashMap<String, Object> category = MsalesJsonUtils.getObjectFromJSON(json, LinkedHashMap.class);
         LinkedHashMap<String, Object> content1 = (LinkedHashMap<String, Object>) category.get("contents");
         GoodsCategory goodsCategory = new GoodsCategory();
         goodsCategory.setId(Integer.parseInt(content1.get("id").toString()));
         goodsCategory.setGoodsCode(content1.get("goodsCode").toString());
         goodsCategory.setName(content1.get("name").toString());

         LinkedHashMap<String, Object> statuss = (LinkedHashMap<String, Object>) content1.get("statuss");
         Status status = new Status();
         status.setId(Integer.parseInt(statuss.get("id").toString()));
         status.setName(statuss.get("name").toString());
         goodsCategory.setStatuss(status);
         uiModel.addAttribute("view", true);
         return productDetail(uiModel, request, goodsCategory);
         */

    }

    @RequestMapping(value = "/category/add/")
    protected String addProduct(HttpServletRequest request, Model uiModel)
            throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        GoodsCategory goodsCategory = new GoodsCategory();
        return productDetail(uiModel, request, goodsCategory);
    }

    private String productDetail(Model uiModel, HttpServletRequest request,
            GoodsCategory goodsCategory) throws JsonMappingException, IOException {
        //Cb Status
        ParameterList parameterList1 = new ParameterList();
        parameterList1.add("statusTypes.id", 7);
        MsalesResults<Status> status = dataService.getListOption(Status.class, parameterList1, true);
        List<Status> statusList = status.getContentList();
        request.setAttribute("statusList", statusList);
        uiModel.addAttribute("goodsCategory", goodsCategory);
        return "categoryView";
        /*
         request.setAttribute(MsalesConstants.CONTENTS, "{\"statusTypeId\": 7}");
         request.setAttribute(MsalesConstants.PAGE, new MsalesPageRequest(1, 10));
         String json = msalesStatusController.getCbListStatusByStatusTypeId(request);
         LinkedHashMap<String, Object> statusList = MsalesJsonUtils.getObjectFromJSON(json, LinkedHashMap.class);
         LinkedHashMap<String, Object> content1 = (LinkedHashMap<String, Object>) statusList.get("contents");
         String contString = MsalesJsonUtils.getJSONFromOject(content1.get("contentList"));
         Status[] list1 = MsalesJsonUtils.getObjectFromJSON(contString, Status[].class);
         List<WebMsalesOptionItem> statusList1 = new ArrayList();
         for (Status status : list1) {
         statusList1.add(new WebMsalesOptionItem(status.getId(), status.getName()));
         }
         request.setAttribute("statusList", statusList1);
         uiModel.addAttribute("goodsCategory", goodsCategory);
         return "categoryView";
         */
    }

    @RequestMapping(value = "/category/create")
    public String createGoodsCategory(HttpServletRequest request, GoodsCategory goodsCategory,
            Model uiModel, RedirectAttributes redirectAttributes, BindingResult bindingResult) throws JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        boolean dupblicateCode = gServices.checkName("goodsCode", goodsCategory.getGoodsCode().trim(), loginUserInf.getCompanyId());
        validateInputData(goodsCategory, bindingResult, true);
        if (!bindingResult.hasErrors() && !dupblicateCode) {
        	goodsCategory.setGoodsCode(goodsCategory.getGoodsCode().trim());
            goodsCategory.setCompanyId(loginUserInf.getCompanyId());
            goodsCategory.setCreatedUser(loginUserInf.getId());
            int ret = dataService.insertRow(goodsCategory);
            if (ret > 0) {
                redirectAttributes.addFlashAttribute("success", true);
                return "redirect:/category/view/" + ret;
            } else {
                uiModel.addAttribute("success", false);
            }
        }else{
        	if(dupblicateCode){
        		uiModel.addAttribute("dupblicateCode", true);
        	}
        }
        return productDetail(uiModel, request, goodsCategory);
        /*
         validateInputData(goodsCategory, bindingResult, true);
         if (!bindingResult.hasErrors()) {
            
         LinkedHashMap contents = new LinkedHashMap();
         contents.put("companyId", 1);
         contents.put("statusId", goodsCategory.getStatuss().getId());
            
         contents.put("name", goodsCategory.getName());
         contents.put("goodsCode", goodsCategory.getGoodsCode());
         contents.put("createdUser", 1);
         String json = MsalesJsonUtils.getJSONFromOject(contents);
            
         request.setAttribute(MsalesConstants.CONTENTS, json);
            
         String jsonOut = categoryController.createGoodsCategory(request);
         MsalesResponse response = MsalesJsonUtils.getObjectFromJSON(jsonOut, MsalesResponse.class);
         if (response.getStatus().getCode() == 200) {
         redirectAttributes.addFlashAttribute("success", true);
         Integer id = response.parseContents(Integer.class);
         return "redirect:/category/view/" + id;
         } else {
         uiModel.addAttribute("success", false);
         }
         } else {
         List<FieldError> errors = bindingResult.getFieldErrors();
         for (FieldError fieldError : errors) {
         bindingResult.rejectValue(fieldError.getField(),
         fieldError.getDefaultMessage());
         }
         }
         return productDetail(uiModel, request, goodsCategory);
         */
    }

    @RequestMapping(value = "/category/update")
    public String updateGoodsCategory(Model uiModel, HttpServletRequest request, GoodsCategory goodsCategory, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        GoodsCategory goodsCategory2 = dataService.getRowById(goodsCategory.getId(), GoodsCategory.class);
        String codeOld = goodsCategory2.getGoodsCode().toLowerCase().trim();
        boolean dupblicateCode = false;
        if (codeOld != null) {
            if (!codeOld.equals(goodsCategory.getGoodsCode().toLowerCase().trim())) {
            	dupblicateCode = gServices.checkName("goodsCode", goodsCategory.getGoodsCode().trim(), loginUserInf.getCompanyId());
            }
        }
        validateInputData(goodsCategory, bindingResult, true);
        if (!bindingResult.hasErrors() && !dupblicateCode) {
            if (goodsCategory.getId() != null) {
            	goodsCategory.setGoodsCode(goodsCategory.getGoodsCode().trim());
                goodsCategory.setCompanyId(loginUserInf.getCompanyId());
                goodsCategory.setUpdatedUser(loginUserInf.getId());
                int ret = dataService.updateSync(goodsCategory);
                if (ret > 0) {
                    redirectAttributes.addFlashAttribute("update", true);
                    return "redirect:/category/view/" + ret;
                } else {
                    redirectAttributes.addFlashAttribute("update", false);
                }
            }
        }else{
        	if(dupblicateCode){
        		uiModel.addAttribute("dupblicateCode", true);
        	}
        }
        uiModel.addAttribute("view", true);
        return productDetail(uiModel, request, goodsCategory);
        /*
         validateInputData(goodsCategory, bindingResult, true);
         if (!bindingResult.hasErrors()) {
         if (goodsCategory.getId() != null) {
         LinkedHashMap contents = new LinkedHashMap();
         contents.put("id", goodsCategory.getId());
         contents.put("companyId", 1);
         contents.put("statusId", goodsCategory.getStatuss().getId());
         contents.put("name", goodsCategory.getName());
         contents.put("goodsCode", goodsCategory.getGoodsCode());
         contents.put("updatedUser", 1);
         String json = MsalesJsonUtils.getJSONFromOject(contents);

         request.setAttribute(MsalesConstants.CONTENTS, json);

         String jsonOut = categoryController.updateGoodsCategory(request);
         MsalesResponse response = MsalesJsonUtils.getObjectFromJSON(jsonOut, MsalesResponse.class);
         if (response.getStatus().getCode() == 200) {

         uiModel.addAttribute("update", true);
         } else {
         uiModel.addAttribute("update", false);
         }
         }
         } else {
         List<FieldError> errors = bindingResult.getFieldErrors();
         for (FieldError fieldError : errors) {
         bindingResult.rejectValue(fieldError.getField(),
         fieldError.getDefaultMessage());
         }
         }
         uiModel.addAttribute("view", true);
         return productDetail(uiModel, request, goodsCategory);
         */
    }

   @Autowired
    View jsonView;

    @RequestMapping(value = "category/delete", method = RequestMethod.POST)
    public ModelAndView deleteGoodsCategory(@RequestParam("id") Integer id,
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
        parameterList.add("goodsCategorys.id", id);
        List<Goods> goodses = dataService.getListOption(Goods.class, parameterList);
        if (!goodses.isEmpty()) {
            status = "NOT OK";
            return new ModelAndView(jsonView, "status", status);
        }
        GoodsCategory goodsCategory = new GoodsCategory();
        goodsCategory.setId(id);
        goodsCategory.setDeletedUser(user.getId());

        try {
            int ret = dataService.deleteSynch(goodsCategory);
            if (ret < 0) {
                status = "NOT OK";
            }
        } catch (Exception ex) {
            status = "NOT OK";
        }

        return new ModelAndView(jsonView, "status", status);
    }
    
    private void validateInputData(GoodsCategory cat, BindingResult bindingResult, boolean isAdd) {
        // Name
        String name = cat.getName();
        if (name == null || name.trim().isEmpty()) {
            bindingResult.rejectValue("name", "cat_fullname", "empty_error_code");
        }

        // Short name
        String shortName = cat.getGoodsCode();
        if (shortName == null || shortName.trim().isEmpty()) {
            bindingResult.rejectValue("goodsCode", "cat_code", "empty_error_code");
        }

        if (cat.getStatuss().getId() == null || cat.getStatuss().getId() == 0) {
            bindingResult.rejectValue("statuss.id", "product_status", "empty_error_code");
        }
    }
}
