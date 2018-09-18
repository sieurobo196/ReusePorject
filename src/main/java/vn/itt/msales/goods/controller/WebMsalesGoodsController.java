/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.goods.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.common.MsalesDownloadTemplet;
import vn.itt.msales.common.xls.MsalesExcelConfig;
import vn.itt.msales.common.xls.MsalesReadExcel;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.GoodsCategory;
import vn.itt.msales.entity.GoodsUnit;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.goods.controller.model.GoodsFilter;
import vn.itt.msales.goods.controller.model.MsalesGoodsImport;
import vn.itt.msales.goods.controller.model.MsalesUnitImport;
import vn.itt.msales.goods.controller.model.UnitComparator;
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
public class WebMsalesGoodsController {

    @Autowired
    DataService dataService;
    @Autowired
    private goodsServices services;
    private List<GoodsCategory> mListGoodsCategory;
    private List<Unit> mListUnit;
    private List<Status> mListStatus;
    private HashMap<String, List<GoodsUnit>> mListGoodsUnit;

    /**
     * Path of the file to be downloaded, relative to application's directory
     */
    private String separator = File.separator;
    private String filePath = String.format("%sdownloads%simports%s", separator, separator, separator);

    /**
     *
     * @param uiModel
     * @param request
     * @param goods
     * @param goodsFilter
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/product/list/")
    public String listGoods(Model uiModel, HttpServletRequest request,
            @ModelAttribute(value = "productListForm") @Valid GoodsFilter goodsFilter,
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
        int maxPages = 1;
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        MsalesLoginUserInf loginUser = LoginContext.getLogin(request);
        // get combobox status
        ParameterList parameterList1 = new ParameterList();
        parameterList1.add("statusTypes.id", 7);
        MsalesResults<Status> status = dataService.getListOption(Status.class, parameterList1, true);
        List<Status> statusList = status.getContentList();
        //get combobox goodsCategory
        ParameterList parameterList = new ParameterList();
        parameterList.add("companys.id", loginUser.getCompanyId());
        parameterList.add("statuss.id", 15);
        MsalesResults<GoodsCategory> CbGoodsCategory = dataService.getListOption(GoodsCategory.class, parameterList, true);
        List<GoodsCategory> CbGoodsCategoryList = CbGoodsCategory.getContentList();

        MsalesPageRequest pageRequest = new MsalesPageRequest(page, size);
        MsalesResults<HashMap> msalesResults = services.searchGoods(goodsFilter, loginUser.getCompanyId(), pageRequest);
        List<HashMap> list = msalesResults.getContentList();

        int count = Integer.parseInt(msalesResults.getCount().toString());
        if (count % size != 0) {
            maxPages = count / size + 1;
        } else {
            maxPages = count / size;
        }

        uiModel.addAttribute("goodsList", list);
        uiModel.addAttribute("statusList", statusList);
        uiModel.addAttribute("goodsCatList", CbGoodsCategoryList);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);

        return "productList";

    }

    @RequestMapping(value = "/product/view/{id}")
    public String viewProduct(Model uiModel, HttpServletRequest request,
            @PathVariable("id") int id, Goods goods)
            throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        uiModel.addAttribute("readonly", !URLManager.checkIn(LoginContext.getLogin(request).getUserRoleCode(), new String[]{"USER_ROLE_ADMIN_COMPANY"}));
        goods = dataService.getRowById(id, Goods.class);
        ParameterList parameterList = new ParameterList();
        parameterList.add("goodss.id", id);
        parameterList.setOrder("id", "asc");
        MsalesResults<GoodsUnit> goodsUnit = dataService.getListOption(GoodsUnit.class, parameterList, true);
        List<GoodsUnit> goodsUnitList = goodsUnit.getContentList();

        uiModel.addAttribute("goodsUnits", goodsUnitList);

        uiModel.addAttribute("view", true);
        return goodsDetails(request, uiModel, goods);

    }

    private String goodsDetails(HttpServletRequest request, Model uiModel, Goods goods) throws JsonMappingException, IOException {
        MsalesLoginUserInf loginUser = LoginContext.getLogin(request);
        // get combobox status
        ParameterList parameterList1 = new ParameterList();
        parameterList1.add("statusTypes.id", 7);
        MsalesResults<Status> status = dataService.getListOption(Status.class, parameterList1, true);
        List<Status> statusList = status.getContentList();
        //get combobox goodsCategory
        ParameterList parameterList = new ParameterList();
        parameterList.add("companys.id", loginUser.getCompanyId());
        MsalesResults<GoodsCategory> CbGoodsCategory = dataService.getListOption(GoodsCategory.class, parameterList, true);
        List<GoodsCategory> CbGoodsCategoryList = CbGoodsCategory.getContentList();
        //get combobox Unit
        MsalesResults<Unit> CbUnit = dataService.getListOption(Unit.class, new ParameterList("companys.id", loginUser.getCompanyId()), true);
        List<Unit> CbUnitList = CbUnit.getContentList();

        uiModel.addAttribute("statusList", statusList);
        uiModel.addAttribute("goodsCatList", CbGoodsCategoryList);
        uiModel.addAttribute("unit", CbUnitList);
        uiModel.addAttribute("goods", goods);
        return "productView";

    }

    @RequestMapping(value = "/product/add/")
    public String addGoods(Model uiModel, HttpServletRequest request) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        Goods goods = new Goods();
        return goodsDetails(request, uiModel, goods);
    }

    @RequestMapping(value = "/product/create", method = RequestMethod.POST)
    public String createGoods(
            Model uiModel, HttpServletRequest request, @ModelAttribute(value = "goods") Goods goods,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf userLogin = LoginContext.getLogin(request);
        validateInputData(goods, bindingResult, true);
        if (!bindingResult.hasErrors()) {
            goods.setCreatedUser(userLogin.getId());
            goods.setCreatedAt(new Date());
            goods.setUpdatedUser(0);
            goods.setDeletedUser(userLogin.getId());
            int ret = dataService.insertRow(goods);
            ArrayList arrayList = new ArrayList();
            List<GoodsUnit> goodsUnits = goods.getGoodsUnits();
            if (ret > 0) {
                for (GoodsUnit goodsUnit : goodsUnits) {

                    goodsUnit.setCreatedAt(new Date());
                    goodsUnit.setCreatedUser(userLogin.getId());
                    goodsUnit.setUpdatedUser(0);
                    goodsUnit.setDeletedUser(0);
                    goodsUnit.setGoodsId(ret);
                    goodsUnit.setPrice(goods.getPrice());
                    goodsUnit.setIsActive(1);
                    if (goodsUnit.getUnits() != null) {
                        arrayList.add(goodsUnit);
                    }
                }
                goods.setDeletedUser(0);

                arrayList.add(goods);
                dataService.insertOrUpdateArray(arrayList);
                redirectAttributes.addFlashAttribute("create", true);
                return "redirect:/product/view/" + ret;
            } else {
                redirectAttributes.addFlashAttribute("create", false);

            }
        } else {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError fieldError : errors) {
                bindingResult.rejectValue(fieldError.getField(),
                        fieldError.getDefaultMessage());
            }
        }
        return goodsDetails(request, uiModel, goods);

    }

    @RequestMapping(value = "/product/update", method = RequestMethod.POST)
    public String updateGoods(
            Model uiModel, HttpServletRequest request, Goods goods,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf userLogin = LoginContext.getLogin(request);
        validateInputData(goods, bindingResult, true);
        if (!bindingResult.hasErrors()) {
            Date now = new Date();
            goods.setUpdatedUser(userLogin.getId());
            goods.setUpdatedAt(now);

            List<GoodsUnit> goodsUnits = goods.getGoodsUnits();
            List<Integer> ids = new ArrayList<>();
            for (GoodsUnit goodsUnit : goodsUnits) {
                if (goodsUnit.getId() != null) {
                    ids.add(goodsUnit.getId());
                }
            }
            ArrayList arrayList = new ArrayList();
            for (GoodsUnit goodsUnit : goodsUnits) {
                if (goodsUnit.getId() != null) {
                    //update
                    GoodsUnit rootGoodsUnit = dataService.getRowById(goodsUnit.getId(), GoodsUnit.class);
                    if (rootGoodsUnit != null) {
                        goodsUnit.setCreatedUser(rootGoodsUnit.getCreatedUser());
                        goodsUnit.setCreatedAt(rootGoodsUnit.getCreatedAt());
                        goodsUnit.setUpdatedAt(now);
                        goodsUnit.setUpdatedUser(userLogin.getId());
                    } else {
                        redirectAttributes.addFlashAttribute("update", false);
                        break;
                    }
                } else {
                    //create
                    goodsUnit.setCreatedUser(userLogin.getId());
                    goodsUnit.setUpdatedUser(0);
                }

                goodsUnit.setDeletedUser(0);
                goodsUnit.setGoodsId(goods.getId());
                goodsUnit.setPrice(goods.getPrice());
                goodsUnit.setIsActive(1);
                if (goodsUnit.getQuantity() != null) {
                    arrayList.add(goodsUnit);
                }
            }

            //xoa all 
            services.deleteGoodsUnit(goods.getId(), ids, userLogin.getId(), dataService);

            arrayList.add(goods);
            dataService.insertOrUpdateArray(arrayList);

            redirectAttributes.addFlashAttribute("update", true);
            return "redirect:/product/view/" + goods.getId();

        } else {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError fieldError : errors) {
                bindingResult.rejectValue(fieldError.getField(),
                        fieldError.getDefaultMessage());
            }
        }
        uiModel.addAttribute("view", true);
        return goodsDetails(request, uiModel, goods);

    }

    @RequestMapping(value = "/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id, Model uiModel, HttpServletRequest request, Goods goods) throws JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        goods.setDeletedUser(loginUserInf.getId());
        int ret = dataService.deleteSynch(goods);
        if (ret > 0) {
            return "redirect:/product/list/";
        } else {
            return "providerError";
        }
    }

    private void validateInputData(Goods goods, BindingResult bindingResult, boolean isAdd) {
        String name = goods.getName();
        if (name == null || name.trim().isEmpty()) {
            bindingResult.rejectValue("name", "goods_name", "empty_error_code");
        }
        String goodsCode = goods.getGoodsCode();
        if (goodsCode == null || goodsCode.trim().isEmpty()) {
            bindingResult.rejectValue("goodsCode", "goods_code", "empty_error_code");
        }
        if (goods.getPrice() == null || goods.getPrice() == 0) {
            bindingResult.rejectValue("price", "goods_price", "empty_error_code");
        }
        if (goods.getFactor() == null) {
            bindingResult.rejectValue("factor", "goods_factor", "empty_error_code");
        }
        if (goods.getGoodsCategorys().getId() == null || goods.getGoodsCategorys().getId() == 0) {
            bindingResult.rejectValue("goodsCategorys.id", "goods_category", "empty_error_code");
        }
        if (goods.getStatuss().getId() == null || goods.getStatuss().getId() == 0) {
            bindingResult.rejectValue("statuss.id", "goods_status", "empty_error_code");
        }
        if (goods.getGoodsUnits() == null) {
            bindingResult.rejectValue("goodsUnits", "goods_Units", "empty_error_code");
        }
    }

    @RequestMapping(value = "/product/import", method = RequestMethod.POST)
    public String importGoods(HttpServletRequest request,
            @RequestParam("file_goods") MultipartFile multipartFile) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        try {
            List<List<Object>> listObj = getListDataFromExel(multipartFile);
            if (listObj != null && !listObj.isEmpty()) {
                List<MsalesGoodsImport> goodsImports = getGoodsImportFromObj(listObj);
                if (!goodsImports.isEmpty()) {
                    List<MsalesGoodsImport> validateGoodsCategory = validateGoodsCategory(goodsImports, loginUserInf);
                    List<MsalesUnitImport> validateUnit = validateUnitCode(goodsImports, loginUserInf);
                    List<MsalesGoodsImport> validateStatus = validateStatus(goodsImports);

                    boolean goodsCategoryEmpty = validateGoodsCategory.isEmpty();
                    boolean goodsUnit = validateUnit.isEmpty();
                    boolean goodsStatus = validateStatus.isEmpty();

                    if (!goodsCategoryEmpty || !goodsUnit || !goodsStatus) {
                        request.setAttribute("error", true);

                        request.setAttribute("goodsCategoryError", goodsCategoryEmpty);
                        request.setAttribute("goodsUnitError", goodsUnit);
                        request.setAttribute("goodsStatusError", goodsStatus);

                        request.setAttribute("goodsCategoryList", validateGoodsCategory);
                        request.setAttribute("goodsUnitList", validateUnit);
                        request.setAttribute("goodsStatusList", validateStatus);
                    } else {
                        List<MsalesGoodsImport> listDuplicated = getDuplicated(goodsImports, loginUserInf);
                        if (listDuplicated.isEmpty()) {
                            List<Goods> listGoods = getListGoodsFromGoodsImport(goodsImports, loginUserInf);
                            boolean importisOK = false;
                            if (!listGoods.isEmpty()) {
                                List<Integer> insetedIds = dataService.insertArray(listGoods);
                                if (insetedIds != null && !insetedIds.isEmpty()) {
                                    List<GoodsUnit> listGoodsUnit = getListGoodsUnit(insetedIds, goodsImports);
                                    if (!listGoodsUnit.isEmpty()) {
                                        List<Integer> goodsUnitIDs = dataService.insertArray(listGoodsUnit);
                                        if (goodsUnitIDs != null && !goodsUnitIDs.isEmpty()) {
                                            importisOK = true;
                                        }
                                    }
                                }
                            }
                            if (importisOK) {
                                request.setAttribute("error", false);
                                request.setAttribute("updated", true);
                            } else {
                                request.setAttribute("updated", false);
                            }
                        } else {
                            request.setAttribute("goodsCategoryError", true);
                            request.setAttribute("goodsUnitError", true);
                            request.setAttribute("goodsStatusError", true);
                            request.setAttribute("duplicatedError", true);
                            request.setAttribute("duplicatedList", listDuplicated);
                            request.setAttribute("error", true);
                            request.setAttribute("updated", false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            request.setAttribute("goodsCategoryError", true);
            request.setAttribute("goodsUnitError", true);
            request.setAttribute("goodsStatusError", true);
            request.setAttribute("duplicatedError", false);
            request.setAttribute("error", false);
            request.setAttribute("updated", false);
            System.err.println(">>>importGoods:" + e.getMessage());
        }
        request.setAttribute("submited", true);
        return "goodsImport";
    }

    @RequestMapping(value = "/product/import", method = RequestMethod.GET)
    public String importGoods(HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }

        return "goodsImport";
    }

    @RequestMapping(value = "/product/download.do")
    public void downloadTemplets(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }

        MsalesDownloadTemplet downloadTemplet = new MsalesDownloadTemplet(filePath, request, response);
        downloadTemplet.getFile("Danh_sach_hang_hoa.xls");
    }

    private List<GoodsUnit> getListGoodsUnit(List<Integer> goodsIDs, List<MsalesGoodsImport> goodsImports) {
        List<GoodsUnit> goodsUnits = new ArrayList<>();
        for (int i = 0; i < goodsImports.size(); i++) {
            MsalesGoodsImport goodsImport = goodsImports.get(i);
            List<GoodsUnit> listUnits = mListGoodsUnit.get(goodsImport.getGoodsCode());
            if (listUnits != null && !listUnits.isEmpty()) {
                for (GoodsUnit goodsUnit : listUnits) {
                    goodsUnit.setGoodsId(goodsIDs.get(i));
                }
                goodsUnits.addAll(listUnits);
            }
        }
        return goodsUnits;
    }

    private List<Goods> getListGoodsFromGoodsImport(List<MsalesGoodsImport> goodsImports, MsalesLoginUserInf loginUserInf) {
        List<Goods> goodses = new ArrayList<>();
        mListGoodsUnit = new HashMap<>();
        for (int i = 0; i < goodsImports.size(); i++) {
            MsalesGoodsImport goodsImport = goodsImports.get(i);
            Goods goods = new Goods();
            goods.setGoodsCategoryId(mListGoodsCategory.get(i).getId());
            goods.setGoodsCode(goodsImport.getGoodsCode());
            goods.setName(goodsImport.getGoodsName());
            goods.setPrice(goodsImport.getGoodsPrice().intValue());
            goods.setFactor(goodsImport.getGoodsCoeffient());
            goods.setStatusId(mListStatus.get(i).getId());
            goods.setIsRecover((goodsImport.getGoodsReturnAllow() == 1));
            goods.setIsFocus(goodsImport.getGoodsFocus() == 1);

            List<MsalesUnitImport> listGoodsUnit = goodsImport.getGoodsUnits();
            List<GoodsUnit> goodsUnits = new ArrayList<>();
            if (listGoodsUnit != null && !listGoodsUnit.isEmpty()) {
                Unit childUnitID = getUnit(listGoodsUnit.get(listGoodsUnit.size() - 1).getUnitCode());
                if (childUnitID != null) {
                    for (MsalesUnitImport unitImport : listGoodsUnit) {
                        GoodsUnit goodsUnit = new GoodsUnit();

                        goodsUnit.setQuantity(unitImport.getQuantity());
                        goodsUnit.setCreatedUser(loginUserInf.getId());
                        goodsUnit.setChildUnitIds(childUnitID);
                        goodsUnit.setPrice(goods.getPrice());
                        goodsUnit.setIsActive(1);

                        Unit unid = getUnit(unitImport.getUnitCode().trim());
                        if (unid != null) {
                            goodsUnit.setUnits(unid);
                            goodsUnits.add(goodsUnit);
                        }
                    }
                }
                mListGoodsUnit.put(goods.getGoodsCode(), goodsUnits);
            }

            goods.setCreatedUser(loginUserInf.getId());
            goodses.add(goods);
        }

        return goodses;
    }

    private Unit getUnit(String code) {
        if (mListUnit != null) {
            for (Unit unit : mListUnit) {
                if (code.equalsIgnoreCase(unit.getCode())) {
                    return unit;
                }
            }
        }
        return null;
    }

    private List<List<Object>> getListDataFromExel(MultipartFile multipartFile) {
        MsalesExcelConfig excelConfig = new MsalesExcelConfig(12, 0, multipartFile);
        MsalesReadExcel msleExcel = new MsalesReadExcel(excelConfig);
        List<List<Object>> posList = msleExcel.readExcel();
        return posList;
    }

    private List<MsalesGoodsImport> getGoodsImportFromObj(List<List<Object>> goodsList) {
        List<MsalesGoodsImport> goodsImports = new ArrayList<>();
        for (List<Object> obj : goodsList) {
            if (obj.size() == 15) {
                MsalesGoodsImport goodsImport = new MsalesGoodsImport();
                List<MsalesUnitImport> unitImports = new ArrayList<>();
                MsalesUnitImport unitImport = null;
                for (int i = 0; i < obj.size(); i++) {
                    switch (i) {
                        case 0:
                            goodsImport.setGoodsCategoryCode(obj.get(0).toString());
                            break;
                        case 1:
                            goodsImport.setGoodsCode(obj.get(1).toString());
                            break;
                        case 2:
                            goodsImport.setGoodsName(obj.get(2).toString());
                            break;
                        case 3:
                            try {
                                goodsImport.setGoodsPrice(Double.parseDouble(obj.get(3).toString()));
                            } catch (Exception e) {
                                goodsImport.setGoodsPrice(0.0);
                            }
                            break;
                        case 4:
                            try {
                                goodsImport.setGoodsCoeffient((int) Double.parseDouble(obj.get(4).toString()));
                            } catch (Exception e) {
                                goodsImport.setGoodsCoeffient(0);
                            }
                            break;
                        case 5:
                            String value = obj.get(5).toString();
                            if (value != null && value.length() > 0) {
                                Integer returnAllow = (int) Double.parseDouble(value);
                                goodsImport.setGoodsReturnAllow(returnAllow);
                            } else {
                                goodsImport.setGoodsReturnAllow(0);
                            }

                            break;
                        case 6:
                            String value6 = obj.get(6).toString();
                            if (value6 != null && value6.length() > 0) {
                                goodsImport.setGoodsFocus((int) Double.parseDouble(value6));
                            } else {
                                goodsImport.setGoodsFocus(0);
                            }
                            break;
                        case 7:
                            unitImport = new MsalesUnitImport();
                            String goodsUnit1 = obj.get(7).toString();
                            if (goodsUnit1 != null && goodsUnit1.length() > 0) {
                                unitImport.setUnitCode(goodsUnit1);
                            }
                            break;
                        case 8:
                            String value8 = obj.get(8).toString();
                            if (value8 != null && value8.length() > 0) {
                                unitImport.setQuantity((int) Double.parseDouble(value8));
                            } else {
                                unitImport.setQuantity(0);
                            }
                            if (unitImport.getUnitCode() != null && unitImport.getUnitCode().length() > 0) {
                                unitImports.add(unitImport);
                            }
                            break;
                        case 9:
                            unitImport = new MsalesUnitImport();
                            String goodsUnit2 = obj.get(9).toString();
                            if (goodsUnit2 != null && goodsUnit2.length() > 0) {
                                unitImport.setUnitCode(goodsUnit2);
                            }

                            break;
                        case 10:
                            try {
                                Integer goodsValueUnit2 = (int) Double.parseDouble(obj.get(10).toString());
                                if (goodsValueUnit2 != null) {
                                    unitImport.setQuantity(goodsValueUnit2.intValue());
                                }
                                if (unitImport.getUnitCode() != null && unitImport.getUnitCode().length() > 0) {
                                    unitImports.add(unitImport);
                                }
                            } catch (Exception e) {
                            }
                            break;
                        case 11:
                            unitImport = new MsalesUnitImport();
                            String goodsUnit3 = obj.get(11).toString();
                            if (goodsUnit3 != null && goodsUnit3.length() > 0) {
                                unitImport.setUnitCode(goodsUnit3);
                            }
                            break;
                        case 12:
                            String value12 = obj.get(12).toString();
                            if (value12 != null && value12.length() > 0) {
                                Integer goodsValueUnit3 = (int) Double.parseDouble(value12);
                                unitImport.setQuantity(goodsValueUnit3);
                            } else {
                                unitImport.setQuantity(1);
                            }
                            if (unitImport.getUnitCode() != null && unitImport.getUnitCode().length() > 0) {
                                unitImports.add(unitImport);
                            }
                            break;
                        case 13:
                            unitImport = new MsalesUnitImport();
                            String goodsUnit4 = obj.get(13).toString();
                            if (goodsUnit4 != null && goodsUnit4.length() > 0) {
                                unitImport.setUnitCode(goodsUnit4);
                                unitImport.setQuantity(1);
                            }
                            unitImports.add(unitImport);
                            break;
                        case 14:
                            try {
                                Integer goodsStatus = (int) Double.parseDouble(obj.get(14).toString());
                                if (goodsStatus != null) {
                                    goodsImport.setGoodsStatus(goodsStatus);
                                } else {
                                    // ser default: 2 Chờ Duyệt
                                    goodsImport.setGoodsStatus(2);
                                }
                            } catch (Exception e) {
                                goodsImport.setGoodsStatus(2);
                            }
                            break;
                        default:
                            break;
                    }
                }
                goodsImport.setGoodsUnits(unitImports);
                goodsImports.add(goodsImport);
            }

        }
        return goodsImports;
    }

    private List<MsalesGoodsImport> validateGoodsCategory(List<MsalesGoodsImport> listGoods, MsalesLoginUserInf loginUserInf) {
        List<MsalesGoodsImport> listError = new ArrayList<>();
        mListGoodsCategory = new ArrayList<>();
        for (MsalesGoodsImport goodsImport : listGoods) {
            ParameterList param = new ParameterList(1, 1);
            param.add("goodsCode", goodsImport.getGoodsCategoryCode().trim());
            param.add("companys.id", loginUserInf.getCompanyId());

            List<GoodsCategory> found = dataService.getListOption(GoodsCategory.class, param);
            if (found != null && !found.isEmpty()) {
                mListGoodsCategory.add(found.get(0));
            } else {
                listError.add(goodsImport);
            }
        }

        return listError;
    }

    private List<MsalesUnitImport> validateUnitCode(List<MsalesGoodsImport> listGoods, MsalesLoginUserInf loginUserInf) {
        List<MsalesUnitImport> listError = new ArrayList<>();
        mListUnit = new ArrayList<Unit>();
        for (MsalesGoodsImport goodsImport : listGoods) {
            List<MsalesUnitImport> listUnit = goodsImport.getGoodsUnits();
            if (listUnit != null && !listUnit.isEmpty()) {
                for (MsalesUnitImport unitImport : listUnit) {
                    ParameterList param = new ParameterList(1, 1);
                    param.add("code", unitImport.getUnitCode().trim());
                    param.add("companys.id", loginUserInf.getCompanyId());

                    List<Unit> found = dataService.getListOption(Unit.class, param);
                    if (found != null && !found.isEmpty()) {
                        mListUnit.add(found.get(0));
                    } else {
                        listError.add(unitImport);
                    }
                }
            }
        }

        return listError;
    }

    private List<MsalesGoodsImport> validateStatus(List<MsalesGoodsImport> listGoods) {
        List<MsalesGoodsImport> listError = new ArrayList<>();
        mListStatus = new ArrayList<>();
        for (MsalesGoodsImport goodsImport : listGoods) {
            ParameterList param = new ParameterList(1, 1);
            param.add("statusTypes.id", 7);
            param.add("value", String.valueOf(goodsImport.getGoodsStatus()));

            List<Status> found = dataService.getListOption(Status.class, param);
            if (found != null && !found.isEmpty()) {
                mListStatus.add(found.get(0));
            } else {
                listError.add(goodsImport);
            }
        }

        return listError;
    }

    private List<MsalesGoodsImport> getDuplicated(List<MsalesGoodsImport> imports, MsalesLoginUserInf loginUserInf) {
        List<MsalesGoodsImport> goodsDuplicated = new ArrayList<>();
        for (MsalesGoodsImport goodsImport : imports) {
//            ParameterList param = new ParameterList();
//            param.add("goodsCode", goodsImport.getGoodsCode().trim());
//            param.add("goodsCategorys.companys.id", loginUserInf.getCompanyId());
//            List<Goods> founds = dataService.getListOption(Goods.class, param);
              List<Goods> founds = services.checkDulicated(goodsImport.getGoodsCode().trim(), loginUserInf.getCompanyId());
            if (founds != null && !founds.isEmpty()) {
                goodsDuplicated.add(goodsImport);
            }
        }
        return goodsDuplicated;
    }
}
