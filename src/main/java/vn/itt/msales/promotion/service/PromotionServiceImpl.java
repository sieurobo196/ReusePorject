package vn.itt.msales.promotion.service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import vn.itt.msales.common.HttpUtil;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.Promotion;
import vn.itt.msales.entity.PromotionAwardOther;
import vn.itt.msales.entity.PromotionChannel;
import vn.itt.msales.entity.PromotionConditionalRef;
import vn.itt.msales.entity.PromotionGoodsRef;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.promotion.model.PromotionFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author vtm
 */
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private DataService dataService;

    @Override
    public List<OptionItem> getCbListGoodsCategory(int userId, int compnayId, DataService dataService) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<OptionItem> getCbListPromotionCondition(int companyId, DataService dataService) {
        String hql = "SELECT id AS id,name AS name"
                + " FROM PromotionConditional"
                + " WHERE deletedUser = 0";

        return OptionItem.createOptionItemListFromHashMap(dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0));
    }

    @Override
    public List<OptionItem> getCbListPromotionStatus(DataService dataService) {
        String hql = "SELECT id AS id,name AS name"
                + " FROM Status"
                + " WHERE deletedUser = 0"
                + " AND statusTypes.deletedUser = 0"
                + " AND statusTypes.id = 8"
                + " AND value != 3";//8 = promotion status

        return OptionItem.createOptionItemListFromHashMap(dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0));
    }

    @Override
    public List<OptionItem> getCbListFullPromotionStatus(DataService dataService) {
        String hql = "SELECT id AS id,name AS name"
                + " FROM Status"
                + " WHERE deletedUser = 0"
                + " AND statusTypes.deletedUser = 0"
                + " AND statusTypes.id = 8";

        return OptionItem.createOptionItemListFromHashMap(dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0));
    }

    @Override
    public List<OptionItem> getCbListPromotionApproveRole(DataService dataService) {
        String hql = "SELECT id AS id,name AS name,code AS code"
                + " FROM UserRole"
                + " WHERE deletedUser = 0"
                + " AND CODE IN ('USER_ROLE_ADMIN_COMPANY','USER_ROLE_ADMIN_CHANNEL','USER_ROLE_ASSISTANT','USER_ROLE_SALES_SUPERVISOR')";
        return OptionItem.createOptionItemListFromHashMap(dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0));
    }

    @Override
    public List<OptionItem> getCbListGoodsCategory(int companyId, DataService dataService) {
        String hql = "SElECT id AS id,name AS name"
                + " FROM GoodsCategory"
                + " WHERE deletedUser = 0"
                + " AND companys.id = :companyId"
                + " AND statuss.value = 1";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        return OptionItem.createOptionItemListFromHashMap(list);
    }

    @Override
    public List<OptionItem> getCbListGoodsByGoodsCategoryId(Integer goodsCategoryId, int companyId, DataService dataService) {
        if (goodsCategoryId == null || goodsCategoryId == 0) {
            return new ArrayList<>();
        }
        String hql = "SELECT id AS id,name AS name"
                + " FROM Goods"
                + " WHERE deletedUser = 0"
                + " AND statuss.value = 1"
                + " AND goodsCategorys.companys.id = :companyId"
                + " AND goodsCategorys.statuss.value = 1";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        if (goodsCategoryId != 0) {
            parameters.add(MsalesParameter.create("goodsCategoryId", goodsCategoryId));
            hql += " AND goodsCategorys.id = :goodsCategoryId";
        }
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        return OptionItem.createOptionItemListFromHashMap(list);
    }

    @Override
    public List<OptionItem> getCbListPromotionAwardOther(int companyId, DataService dataService) {
        String hql = "SELECT id AS id,name AS name"
                + " FROM PromotionAwardOther"
                + " WHERE deletedUser = 0"
                + " AND companys.id = :companyId"
                + " AND statuss.value = 1";

        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        return OptionItem.createOptionItemListFromHashMap(list);
    }

    @Override
    public boolean createGoodsAwardOther(PromotionAwardOther promotionAwardOther, DataService dataService) {
        try {

            dataService.insertRow(promotionAwardOther);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public HashMap getGoodsUnitByGoodsId(int goodsId, DataService dataService) {
        String hql = "SELECT units.id AS id,units.name AS name"
                + " FROM GoodsUnit"
                + " WHERE deletedUser = 0"
                + " AND goodss.id = :goodsId"
                + " AND quantity = 1";
        List<MsalesParameter> parameters = MsalesParameter.createList("goodsId", goodsId);
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 1, 1);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public HashMap getOtherUnitByOtherId(int otherId, DataService dataService) {
        String hql = "SELECT units.id AS id,units.name AS name"
                + " FROM PromotionAwardOther"
                + " WHERE deletedUser = 0"
                + " AND id = :otherId";
        List<MsalesParameter> parameters = MsalesParameter.createList("otherId", otherId);
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 1, 1);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public boolean checkPromotionCode(String code, int companyId, DataService dataService) {
        String hql = "SELECT COUNT(id)"
                + " FROM Promotion"
                + " WHERE deletedUser = 0"
                + " AND code = :code"
                + " AND companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("code", code));
        List<Long> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.get(0) > 0L;
    }

    @Override
    public Promotion getPromotionFullInfo(int promotionId, int companyId, DataService dataService) {
        Promotion promotion = dataService.getRowById(promotionId, Promotion.class);
        if (promotion != null) {
            //neu ko phai cong ty
            if (promotion.getCompanys().getId() != companyId) {
                return null;
            }
            //load goods
            promotion.setGoodsIdList(getListPromotionGoodsId(promotionId, dataService));

            switch (promotion.getPromotionAwards().getId()) {
                case 2:
                    promotion.setProAwardOtherQuantity(promotion.getProAwardQuantity());
                    break;
                case 3:
                case 4:
                    promotion.setDiscount(promotion.getProAwardQuantity());
                    break;
            }
            //load Dieu kien 
            promotion.setConditionList(getListPromotionConditionalId(promotionId, dataService));
        }
        return promotion;
    }

    @Override
    public List<Channel> getListPromotionChannelId(int promotionId, DataService dataService) {
        //load all channel of promtion
        String hql = "SELECT PromotionChannel.channels"
                + " FROM PromotionChannel AS PromotionChannel"
                + " WHERE PromotionChannel.deletedUser = 0"
                + " AND PromotionChannel.promotions.id = :promotionId"
                + " AND PromotionChannel.statuss.value = 1"
                + " AND PromotionChannel.channels.statuss.value = 1"
                + " AND PromotionChannel.channels.deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<Integer> getListPromotionGoodsId(int promotionId, DataService dataService) {
        //load all goods of promtion
        String hql = "SELECT PromotionGoodsRef.goodss.id"
                + " FROM PromotionGoodsRef AS PromotionGoodsRef"
                + " WHERE PromotionGoodsRef.deletedUser = 0"
                + " AND PromotionGoodsRef.promotions.id = :promotionId"
                + " AND PromotionGoodsRef.statuss.value = 1"
                + " AND PromotionGoodsRef.goodss.statuss.value = 1"
                + " AND PromotionGoodsRef.goodss.deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<Goods> getListPromotionGoods(int promotionId, DataService dataService) {
        //load all goods of promtion
        String hql = "SELECT PromotionGoodsRef.goodss"
                + " FROM PromotionGoodsRef AS PromotionGoodsRef"
                + " WHERE PromotionGoodsRef.deletedUser = 0"
                + " AND PromotionGoodsRef.promotions.id = :promotionId"
                + " AND PromotionGoodsRef.statuss.value = 1"
                + " AND PromotionGoodsRef.goodss.statuss.value = 1"
                + " AND PromotionGoodsRef.goodss.deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<Integer> getListPromotionConditionalId(int promotionId, DataService dataService) {
        //load all channel of promtion
        String hql = "SELECT PromotionConditionalRef.promotionConditionals.id"
                + " FROM PromotionConditionalRef AS PromotionConditionalRef"
                + " WHERE PromotionConditionalRef.deletedUser = 0"
                + " AND PromotionConditionalRef.promotions.id = :promotionId"
                + " AND PromotionConditionalRef.statuss.value = 1"
                + " AND PromotionConditionalRef.promotionConditionals.statuss.value = 1"
                + " AND PromotionConditionalRef.promotionConditionals.deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<PromotionChannel> getListPromotionChannel(int promotionId, DataService dataService) {
        //load all channel of promtion
        String hql = "SELECT PromotionChannel"
                + " FROM PromotionChannel AS PromotionChannel"
                + " WHERE PromotionChannel.deletedUser = 0"
                + " AND PromotionChannel.promotions.id = :promotionId"
                + " AND PromotionChannel.statuss.value = 1"
                + " AND PromotionChannel.channels.statuss.value = 1"
                + " AND PromotionChannel.channels.deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<PromotionGoodsRef> getListPromotionGoodsRef(int promotionId, DataService dataService) {
        //load all goods of promtion
        String hql = "SELECT PromotionGoodsRef"
                + " FROM PromotionGoodsRef AS PromotionGoodsRef"
                + " WHERE PromotionGoodsRef.deletedUser = 0"
                + " AND PromotionGoodsRef.promotions.id = :promotionId"
                + " AND PromotionGoodsRef.statuss.value = 1"
                + " AND PromotionGoodsRef.goodss.statuss.value = 1"
                + " AND PromotionGoodsRef.goodss.deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<PromotionConditionalRef> getListPromotionConditionalRef(int promotionId, DataService dataService) {
        //load all channel of promtion
        String hql = "SELECT PromotionConditionalRef"
                + " FROM PromotionConditionalRef AS PromotionConditionalRef"
                + " WHERE PromotionConditionalRef.deletedUser = 0"
                + " AND PromotionConditionalRef.promotions.id = :promotionId"
                + " AND PromotionConditionalRef.statuss.value = 1"
                + " AND PromotionConditionalRef.promotionConditionals.statuss.value = 1"
                + " AND PromotionConditionalRef.promotionConditionals.deletedUser = 0";
        List<MsalesParameter> parameters = MsalesParameter.createList("promotionId", promotionId);
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public MsalesResults<Promotion> searchPromotion(PromotionFilter filter, int companyId, DataService dataService, int page, int size) {
        String hql = "FROM Promotion"
                + " WHERE deletedUser = 0"
                + " AND companys.id = :companyId"
                + " ORDER BY createdAt DESC";

        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        List<Promotion> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);

        List<Integer> removePromotionList = new ArrayList<>();
        if (filter.getApplyRoleId() != null && filter.getApplyRoleId() != 0) {
            for (Promotion promotion : list) {
                if (!Objects.equals(promotion.getApproveRoles().getId(), filter.getApplyRoleId())) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        if (filter.getGoodsCategoryId() != null && filter.getGoodsCategoryId() != 0) {
            for (Promotion promotion : list) {
                if (!Objects.equals(promotion.getGoodsCategorys().getId(), filter.getGoodsCategoryId())) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        if (filter.getGoodsIdList() != null && !filter.getGoodsIdList().isEmpty()) {
            for (Promotion promotion : list) {
                boolean flag = false;
                List<Goods> goodsList = getListPromotionGoods(promotion.getId(), dataService);
                for (int id : filter.getGoodsIdList()) {
                    for (Goods goods : goodsList) {
                        if (id == goods.getId()) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
                if (!flag) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        if (filter.getConditionQuantity() != null && filter.getConditionQuantity() != 0) {
            for (Promotion promotion : list) {
                if (!Objects.equals(promotion.getConditionQuantity(), filter.getConditionQuantity())) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        if (filter.getAwardIdList() != null && !filter.getAwardIdList().isEmpty()) {
            for (Promotion promotion : list) {
                boolean flag = false;
                for (int id : filter.getAwardIdList()) {
                    if (id == promotion.getPromotionAwards().getId()) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        if (filter.getRange() != null) {
            for (Promotion promotion : list) {
                if (filter.getRange() == 1)//bac thang
                {
                    if (promotion.getProRanges() == null) {
                        removePromotionList.add(promotion.getId());
                    }
                } else {//binh thuong
                    if (promotion.getProRanges() != null) {
                        removePromotionList.add(promotion.getId());
                    }
                }

            }
        }

        if (filter.getFromDate() != null) {
            for (Promotion promotion : list) {
                if (filter.getFromDate().compareTo(promotion.getEndDate()) > 0) {
                    removePromotionList.add(promotion.getId());
                }
            }
            //sql += " AND NOT :fromDate > PROMOTION.END_DATE";
        }

        if (filter.getToDate() != null) {
            for (Promotion promotion : list) {
                if (filter.getToDate().compareTo(promotion.getStartDate()) < 0) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        List<Integer> ids = new ArrayList<>();

        if (filter.getChannelIds() != null && !filter.getChannelIds().isEmpty() && filter.getChannelIds().get(0) != 0) {
            ids = filter.getChannelIds();
        } else if (filter.getChannelList() != null && !filter.getChannelList().isEmpty()) {
            for (int i = filter.getChannelList().size() - 1; i >= 0; i--) {
                for (OptionItem optionItem : filter.getChannelList().get(i)) {
                    if (optionItem != null) {
                        if (optionItem.getId() != 0) {
                            ids.add(optionItem.getId());
                        }
                    }
                }
                if (!ids.isEmpty()) {
                    break;
                }
            }
        }

        if (!ids.isEmpty()) {
            //loc theo kenh
            //hien thi ca nhung KM ap dung cho toan cong ty
            List<Channel> channelList = new ArrayList<>();
            for (int id : ids) {
                Channel channel = dataService.getRowById(id, Channel.class);
                if (channel != null) {
                    channelList.add(channel);
                }
            }

            for (Promotion promotion : list) {
                boolean flag = false;
                if (promotion.getApplyScope() == null || promotion.getApplyScope() == 0) {
                    //ap dung cho toan cong ty
                    flag = true;
                } else {
                    //ap dung cho kenh
                    List<PromotionChannel> promotionChannelList = getListPromotionChannel(promotion.getId(), dataService);
                    for (Channel userChannel : channelList) {
                        for (PromotionChannel promotionChannel : promotionChannelList) {
                            //KT truong hop KM ap dung cho kenh sau kenh user
                            Channel channel = promotionChannel.getChannels();
                            while (channel != null) {
                                if (Objects.equals(channel.getId(), userChannel.getId())) {
                                    flag = true;
                                    break;
                                } else {
                                    channel = channel.getParents();
                                }
                            }
                            if (flag) {
                                break;
                            } else {
                                //KT truong hop KM ap dung cho kenh truoc kenh user
                                channel = userChannel;
                                if (channel != null) {
                                    while (channel != null) {
                                        if (Objects.equals(channel.getId(), promotionChannel.getChannels().getId())) {
                                            flag = true;
                                            break;
                                        } else {
                                            channel = channel.getParents();
                                        }
                                    }
                                    if (flag) {
                                        break;
                                    }
                                }
                            }
                        }
                        if (flag) {
                            break;
                        }
                    }
                }
                if (!flag) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        if (filter.getConditionIdList() != null && !filter.getConditionIdList().isEmpty()) {
            for (Promotion promotion : list) {
                boolean flag = false;
                List<PromotionConditionalRef> promotionConditionalRefList = getListPromotionConditionalRef(promotion.getId(), dataService);
                for (int id : filter.getConditionIdList()) {
                    for (PromotionConditionalRef promotionConditionalRef : promotionConditionalRefList) {
                        if (promotionConditionalRef.getPromotionConditionals().getId() == id) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
                if (!flag) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }
        if (filter.getAwardIdList() != null && !filter.getAwardIdList().isEmpty()) {
            boolean flag = false;
            for (Promotion promotion : list) {
                for (int id : filter.getAwardIdList()) {
                    if (promotion.getPromotionAwards().getId() == id) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    removePromotionList.add(promotion.getId());
                }
            }

        }

        for (int id : removePromotionList) {
            for (Promotion promotion : list) {
                if (promotion.getId() == id) {
                    list.remove(promotion);
                    break;
                }
            }
        }

        MsalesResults<Promotion> result = new MsalesResults();
        result.setCount((long) list.size());
        int maxPages = list.size() % size != 0 ? list.size() / size + 1 : list.size() / size;
        if (page > maxPages) {
            page = maxPages;
        }
        if (page < 1) {
            page = 1;
        }
        //phan trang
        result.setContentList(list.subList((page - 1) * size, (page * size) > (list.size()) ? list.size() : page * size));
        return result;
    }

    @Override
    public List<OptionItem> getCbListPromotionAward(DataService dataService
    ) {
        String hql = "SELECT id AS id,promotionAwardName AS name"
                + " FROM PromotionAward"
                + " WHERE deletedUser = 0"
                + " AND statuss.id = 1";
        return OptionItem.createOptionItemListFromHashMap(dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0));
    }

    @Override
    public List<OptionItem> getCbListUnit(int companyId, DataService dataService) {
        String hql = "SELECT id AS id,name AS name"
                + " FROM Unit"
                + " WHERE deletedUser = 0"
                + " AND companys.id = :companyId"
                + " ORDER BY name,order ";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        List<HashMap> list = dataService.executeSelectHQL(hql, parameters, true, 0, 0);
        return OptionItem.createOptionItemListFromHashMap(list);
    }

    @Override
    public List<OptionItem> getCbListPromotionType() {
        List<OptionItem> ret = new ArrayList<>();
        OptionItem optionItem = new OptionItem(0, "Bình thường");
        ret.add(optionItem);
        optionItem = new OptionItem(1, "Bậc thang");
        ret.add(optionItem);
        return ret;
    }

    @Override
    public List<OptionItem> getCbListRegister() {
        List<OptionItem> ret = new ArrayList<>();
        OptionItem optionItem = new OptionItem(0, "Không đăng ký");
        ret.add(optionItem);
        optionItem = new OptionItem(1, "Cần đăng ký");
        ret.add(optionItem);
        return ret;
    }

    @Override
    public List<OptionItem> getCbListQuantityType() {
        List<OptionItem> list = new ArrayList<>();
        OptionItem optionItem = new OptionItem(1, "1. Số lượng bán theo đơn hàng");
        list.add(optionItem);
        optionItem = new OptionItem(2, "2. Doanh thu theo đơn hàng");
        list.add(optionItem);
        optionItem = new OptionItem(3, "3. Tích lũy số lượng bán");
        list.add(optionItem);
        optionItem = new OptionItem(4, "4. Tích lũy doanh số bán");
        list.add(optionItem);
        return list;
    }

    public void initDataForPromotionCheck(HttpServletRequest request, MsalesLoginUserInf userLogin, Model uiModel, boolean approve) {
        //Get status
        String hqlStatus = "SELECT S.id as id, S.name as name FROM Status as S"
                + " WHERE deletedUser = 0 AND statusTypes.deletedUser = 0"
                + " AND statusTypes.id = 9";
        if (!approve) {
            hqlStatus += " AND id not in (26,23,30)";
        } else {
            hqlStatus += " AND id in (23,30)";
        }
        List<HashMap> listStatus = dataService.executeSelectHQL(HashMap.class, hqlStatus, true, 0, 0);
        List<OptionItem> statusList = new ArrayList<>();
        for (HashMap status : listStatus) {
            OptionItem optionItem = new OptionItem();
            optionItem.setId((Integer) status.get("id"));
            optionItem.setName((String) status.get("name"));
            statusList.add(optionItem);
        }

        uiModel.addAttribute("statusList", statusList);

        //Lay Dieu kien khuyen mai
//        String hqlDKCTKM = "Select PCR.promotionConditionals.id as id, PCR.promotionConditionals.name as name"
//                + " FROM PromotionConditionalRef as PCR"
//                + " WHERE deletedUser = 0 "
//                + " AND promotions.companys.id = " + userLogin.getCompanyId()
//                + " GROUP BY promotionConditionals.id";
//        List<HashMap> listDKCTKM = dataService.executeSelectHQL(HashMap.class, hqlDKCTKM, true, 0, 0);
//        List<OptionItem> listDKCTKMs = new ArrayList<>();
//        OptionItem opI = new OptionItem(0, "---Chọn điều kiện ---");
//        listDKCTKMs.add(opI);
//        for (HashMap dkctkm : listDKCTKM) {
//            OptionItem optionItem = new OptionItem();
//            optionItem.setId((Integer) dkctkm.get("id"));
//            optionItem.setName((String) dkctkm.get("name"));
//            listDKCTKMs.add(optionItem);
//        }
//        uiModel.addAttribute("listDKCTKM", listDKCTKMs);
        //get List roles approve
//        String hqlRoleApprove = "SELECT UR.id as id, UR.name as name, UR.code as code"
//                + " FROM UserRole as UR WHERE deletedUser = 0 AND id not in (5,6)";
//        List<HashMap> listApproveRole = dataService.executeSelectHQL(HashMap.class, hqlRoleApprove, true, 0, 0);
//        List<OptionItem> listApproveRoles = new ArrayList<>();
//        for (HashMap role : listApproveRole) {
//            OptionItem optionItem = new OptionItem();
//            optionItem.setId((Integer) role.get("id"));
//            optionItem.setName((String) role.get("name"));
//            listApproveRoles.add(optionItem);
//        }
//        uiModel.addAttribute("listApproveRole", listApproveRoles);
        //Lay danh sach chuong trinh khuyen mai
        ParameterList parameterList = new ParameterList();
        parameterList.add("companys.id", userLogin.getCompanyId());
        List<Integer> listId = new ArrayList<>();
        listId.add(1);
        listId.add(2);
        parameterList.in("conditionQuantity", listId);
        List<Promotion> listCTKM = dataService.getListOption(Promotion.class, parameterList);
        //       List<Promotion> listCTKMs = new ArrayList<>();
//        Promotion promotions = new Promotion();
//        promotions.setId(0);
//        promotions.setName("--Chọn CTKM--");
//        listCTKMs.add(promotions);
        //     listCTKMs.addAll(listCTKM);
        uiModel.addAttribute("allPromotionList", listCTKM);

        //Lay danh sach hinh thuc tang
//        String hqlAward = "SELECT PAR.promotionAwards.id as id, PAR.promotionAwards.promotionAwardName as name"
//                + " FROM PromotionAwardRef as PAR "
//                + " WHERE PAR.deletedUser = 0 AND PAR.promotions.companys.id = " + userLogin.getCompanyId()
//                + " AND PAR.promotions.deletedUser = 0"
//                + " GROUP BY PAR.promotionAwards.id";
//        List<HashMap> listAward = dataService.executeSelectHQL(HashMap.class, hqlAward, true, 0, 0);
//        List<OptionItem> listAwards = new ArrayList<>();
//        for (HashMap award : listAward) {
//            OptionItem optionItem = new OptionItem();
//            optionItem.setId((Integer) award.get("id"));
//            optionItem.setName((String) award.get("name"));
//            //  PromotionAward pAward = (PromotionAward) award.get("promotionAwards");
//            listAwards.add(optionItem);
//        }
//        uiModel.addAttribute("listAwardCTKM", listAwards);
        //Lay danh sach loai san pham
//        boolean goodsCategoryIsNull = false;
//        for (Promotion promotion : listCTKM) {
//            if (promotion.getGoodsCategoryId() == null) {
//                goodsCategoryIsNull = true;
//                break;
//            }
//        }
//        String hqlGoodsCategory = "SELECT GC.id as id, GC.name as name"
//                + " FROM GoodsCategory as GC WHERE deletedUser = 0"
//                + " AND companys.id = " + userLogin.getCompanyId()
//                + " AND statuss.id = 15";
//        String string = "";
//        if (!goodsCategoryIsNull) {
//            for (Promotion promotion : listCTKM) {
//                string += " OR id = " + promotion.getGoodsCategoryId();
//            }
//        }
//        if (string.length() > 3) {
//            hqlGoodsCategory += " AND (" + string.substring(3) + ") ";
//        }
//        hqlGoodsCategory += " ORDER BY name";
//        List<HashMap> listGC = dataService.executeSelectHQL(HashMap.class, hqlGoodsCategory, true, 0, 0);
//        List<OptionItem> listGoodsCategory = new ArrayList<>();
//        for (HashMap gc : listGC) {
//            OptionItem optionItem = new OptionItem();
//            optionItem.setId((Integer) gc.get("id"));
//            optionItem.setName((String) gc.get("name"));
//            listGoodsCategory.add(optionItem);
//        }
//        uiModel.addAttribute("listCategory", listGoodsCategory);
        //get list calculation 
        List<OptionItem> typeQuantityList = new ArrayList<>();
        typeQuantityList.add(new OptionItem(1, "1.Số lượng bán theo đơn hàng"));
        typeQuantityList.add(new OptionItem(2, "2.Doanh thu theo đơn hàng "));
        uiModel.addAttribute("quantityTypeList", typeQuantityList);
        //get List Range Award Promotion
//        List<OptionItem> stepAward = new ArrayList<>();
//        stepAward.add(new OptionItem(0, "--- Chọn km bậc thang ---"));
//        stepAward.add(new OptionItem(1, "Bình Thường"));
//        stepAward.add(new OptionItem(2, "Bậc Thang"));
//        uiModel.addAttribute("stepAward", stepAward);
    }

    public MsalesResults<HashMap> getHqlStringForSearchPromotion(HttpServletRequest request, PromotionFilter promotionFilter, Model uiModel, MsalesLoginUserInf userLogin, boolean approve, int page, int size) {
        String hqlCount = "SELECT PTR.id as VAL ";
        String hqlGet = "SELECT PTR.id as id,"
                //Get POS
                + " PTR.poss.id as posId, PTR.poss.posCode as posCode, PTR.poss.name as posName,"
                + " PTR.poss.address as address,"
                + " CASE WHEN (PTR.poss.tel is not null and PTR.poss.tel != '') then PTR.poss.tel"
                + " WHEN (PTR.poss.mobile is not null and PTR.poss.mobile != '')  then PTR.poss.mobile"
                + " ELSE PTR.poss.otherTel END as tel,"
                //Get info PromotionTransRetails
                + " PTR.createdUser as createdUser, PTR.updatedUser as updatedUser,"
                + " PTR.awardName as awardName, PTR.awardQuantity as awardQuantity, PTR.awardAmount as awardAmount,"
                + " PTR.isOther as isOther, PTR.awardGoodsId as awardGoodsId, PTR.note as note, "
                + " PTR.statuss.id as statusId, PTR.statuss.name as statusName, "
                + " PTR.quantity as quantity, PTR.amount as amount, "
                + " PTR.promotions.id as promotionId, PTR.promotions.name as promotionName,PTR.promotions.note as promotionNote, "
                + " PTR.createdAt as createdAt, PTR.promotions.proAwardQuantity as proAwardQuantity, "
                + " PTR.promotions.statuss.id as promotionStatusId, PTR.promotions.quantity as promotionQuantity,"
                + " PTR.promotions.statuss.name as promotionStatusName,"
                + " PTR.promotions.promotionAwards.id as awardId,"
                + " CASE WHEN ApproveRoles.id  is null THEN NULL "
                + " ELSE ApproveRoles.name END as approveRolesName";

        String hqlSelected = " FROM PromotionTransRetailer as PTR, MCPDetails as M"
                + " LEFT JOIN PTR.promotions.approveRoles as ApproveRoles";
        String hql = " WHERE PTR.deletedUser = 0 AND PTR.promotions.deletedUser = 0"
                + " AND M.deletedUser = 0 and M.mcps.deletedUser = 0"
                + " AND M.poss.id = PTR.poss.id"
                + " AND (PTR.promotions.approveRoles.id >= " + userLogin.getUserRoleId() + " OR PTR.promotions.approveRoles.id is null ) "
                + " AND PTR.promotions.companys.id = " + userLogin.getCompanyId();
        if (approve) {
            hql += " AND PTR.statuss.id IN (23,30,29) ";
        } else {
            hql += " AND PTR.statuss.id not in (23,30,29) ";
        }
        //Check follow ctkm Done
        if (promotionFilter.getPromotionId() != null && promotionFilter.getPromotionId() != 0) {
            hql += " AND PTR.promotions.id = " + promotionFilter.getPromotionId();
        }
//        //Check follow status Done
        if (promotionFilter.getStatusId() != null && promotionFilter.getStatusId() != 0) {
            hql += " AND PTR.statuss.id = " + promotionFilter.getStatusId();
        }
        
        //Check ChannelId;
        for(List<OptionItem> optionItems : promotionFilter.getChannelList()){
            if(optionItems.size() > 1){
        	String string = "";
        	for(OptionItem op : optionItems){
                    string += op.getId() + ",";
        	}
        	string += "''";
        	String hqlChannel = "SELECT C.fullCode as fullCode FROM Channel as C "
        			+ " WHERE deletedUser = 0 and id IN ("+string+")";
        	List<HashMap> listChannel = dataService.executeSelectHQL(HashMap.class, hqlChannel, true, 0, 0);
                String getUsers = "SELECT URC.users.id as userId FROM UserRoleChannel as URC"
                    + " WHERE users.deletedUser = 0 and deletedUser = 0 and userRoles.id = 6 and channels.companys.id = "+userLogin.getCompanyId();
    		string = "";
    		for(HashMap hm : listChannel){
    			string += " OR channels.fullCode LIKE '%"+hm.get("fullCode")+"%'";
    		}
                if(string.length() > 3){
                    getUsers += " and ( "+string.substring(3)+" )";
                }else{
                    getUsers += " and channels.id = 0";
                }
                getUsers += " GROUP BY users.id";
                List<HashMap> listUserId = dataService.executeSelectHQL(HashMap.class, getUsers, true, 0, 0);
                string = "";
                for(HashMap hm : listUserId){
                    string += hm.get("userId") + ",";
                }
                string += "''";
                hql += " AND M.mcps.implementEmployees IN ("+string+")";
//        		String fullCode = "";
//        		for(HashMap hm : listChannel){
//        			fullCode += " OR PTR.poss.channels.fullCode LIKE '%"+hm.get("fullCode")+"_%'";
//        		}
//        		hql += " AND ( PTR.poss.channels.id IN ("+string+")";
//        		if(fullCode.length() > 3){
//        			hql += fullCode;
//        		}
//        		hql += " )";
        	break;
            }
        }
        //Search follow channelIds
        int sizeOfChannelIdList = promotionFilter.getChannelIdList().size();
        if(sizeOfChannelIdList > 0){
        	String string = "";
        	List<Integer> listChannelIds = promotionFilter.getChannelIdList().get(sizeOfChannelIdList - 1);
    		for(Integer op : listChannelIds){
    			string += op + ",";
    		}
    		string += "''";
    		String hqlChannel = "SELECT C.fullCode as fullCode FROM Channel as C "
    				+ " WHERE deletedUser = 0 and id IN ("+string+")";
    		List<HashMap> listChannel = dataService.executeSelectHQL(HashMap.class, hqlChannel, true, 0, 0);
                String getUsers = "SELECT URC.users.id as userId FROM UserRoleChannel as URC"
                    + " WHERE users.deletedUser = 0 and deletedUser = 0 and userRoles.id = 6 and channels.companys.id = "+userLogin.getCompanyId();
    		string = "";
    		for(HashMap hm : listChannel){
                    string += " OR channels.fullCode LIKE '%"+hm.get("fullCode")+"%'";
    		}
                if(string.length() > 3){
                    getUsers += " and ( "+string.substring(3)+" )";
                }else{
                    getUsers += " and channels.id = 0";
                }
                getUsers += " GROUP BY users.id";
                List<HashMap> listUserId = dataService.executeSelectHQL(HashMap.class, getUsers, true, 0, 0);
                string = "";
                for(HashMap hm : listUserId){
                    string += hm.get("userId") + ",";
                }
                string += "''";
                hql += " AND M.mcps.implementEmployees IN ("+string+")";
//    		String fullCode = "";
//    		for(HashMap hm : listChannel){
//    			fullCode += " OR PTR.poss.channels.fullCode LIKE '%"+hm.get("fullCode")+"_%'";
//    		}
//    		hql += " AND ( PTR.poss.channels.id IN ("+string+")";
//    		if(fullCode.length() > 3){
//    			hql += fullCode;
//    		}
//    		hql += " )";
        }
        //Check follow fromDate and toDate Done 
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = HttpUtil.getRequestDateParammeter(request, "fromDate", dateFormat);
        //  Date fromDate = promotionFilter.getFromDate();
        if (fromDate != null) {
            hql += " AND PTR.promotions.startDate >= '" + simpleDateFormat.format(fromDate) + "'";
            uiModel.addAttribute("frm_from_date", fromDate);
        }

        Date toDate = HttpUtil.getRequestDateParammeter(request, "toDate", dateFormat);
        if (toDate != null) {
            hql += " AND PTR.promotions.endDate <= '" + simpleDateFormat.format(toDate) + " 23:59:59'";
            uiModel.addAttribute("frm_to_date", toDate);
        }
        //Check Done
        if (promotionFilter.getConditionIdList() != null && promotionFilter.getConditionIdList().size() >= 0) {
            hqlSelected += " , PromotionConditionalRef as PCR";
            hql += " AND PCR.deletedUser = 0 AND PCR.promotionConditionals.deletedUser = 0"
                    + " AND PCR.promotions.id = PTR.promotions.id";
            String string = "";
            for (Integer id : promotionFilter.getConditionIdList()) {
                string += id + ",";
            }
            string += "''";
            hql += " AND PCR.promotionConditionals.id IN (" + string + ")";
        }
        //Check hinh thuc tang Done
        if (promotionFilter != null && promotionFilter.getAwardIdList() != null && promotionFilter.getAwardIdList().size() > 0) {
            String string = "";
            for (Integer awardId : promotionFilter.getAwardIdList()) {
                string += awardId + ",";
            }
            string += "''";

            hql += " AND PTR.promotions.promotionAwards.id IN (" + string + ")";
        }

        //search follow approveRole Done 
        if (promotionFilter.getApplyRoleId() != null && promotionFilter.getApplyRoleId() != 0) {
            hql += " AND PTR.promotions.approveRoles.id = " + promotionFilter.getApplyRoleId();
            //  hql += " AND PTR.approves.id = URC.users.id AND URC.userRoles.id = " + promotionFilter.getApplyRoleId();
        }

        //search follow range award Done 
        if (promotionFilter.getRange() != null && promotionFilter.getRange() >= 0) {
            if (promotionFilter.getRange() == 1) {
                hqlSelected += " ,PromotionRangeAward as PRA ";
                hql += " AND PRA.deletedUser = 0 AND PRA.promotions.id = PTR.promotions.id";
            } else if (promotionFilter.getRange() == 0) {
                String hql1 = " SELECT PRA.id as id FROM PromotionRangeAward as PRA ,Promotion as P"
                        + " WHERE PRA.deletedUser=0 AND P.deletedUser=0"
                        + " AND PRA.promotions.id=P.id "
                        + " AND P.companys.id = " + userLogin.getCompanyId()
                        + " GROUP BY PRA.id";

                List<HashMap> awards = dataService.executeSelectHQL(HashMap.class, hql1, true, 0, 0);

                hql += " AND PTR.promotions.id NOT IN (";
                for (HashMap promotionRangeAward : awards) {
                    hql += promotionRangeAward.get("id") + ",";
                }
                hql += "'')";
            }
        }
        // Search follow conditionQuantity Done 
        if (promotionFilter.getConditionQuantity() != null && promotionFilter.getConditionQuantity() != 0) {
            hql += " AND PTR.promotions.conditionQuantity = " + promotionFilter.getConditionQuantity();
        }

        //Search follow goodsCategoryId
        if (promotionFilter.getGoodsCategoryId() != null && promotionFilter.getGoodsCategoryId() != 0) {
            String hqlGoodsCategoryId = "SELECT P.goodsCategorys.id as goodsCategoryId FROM Promotion as P"
                    + " WHERE deletedUser = 0 AND P.companys.id = " + userLogin.getCompanyId()
                    + " AND P.goodsCategorys.id = " + promotionFilter.getGoodsCategoryId();
            List<HashMap> listGoodsCategoryId = dataService.executeSelectHQL(HashMap.class, hqlGoodsCategoryId, true, 1, 1);
            if (listGoodsCategoryId.isEmpty()) {
                hql += " AND PTR.promotions.goodsCategorys.id is null";
            } else {
                hql += " AND PTR.promotions.goodsCategorys.id = " + promotionFilter.getGoodsCategoryId();
            }

        }

        //Search listGoodsId
//        if (promotionFilter.getGoodsIdList() != null && promotionFilter.getGoodsIdList().size() > 0) {
//            String hqlGoodsList = "";
//            for (Integer goodsId : promotionFilter.getGoodsIdList()) {
//                hqlGoodsList += goodsId + ",";
//            }
//            hqlGoodsList += "''";
//            String hqlGoodsCategoryId = "SELECT P.goodsCategorys.id as goodsCategoryId FROM Promotion as P"
//                    + " WHERE deletedUser = 0 AND P.companys.id = " + userLogin.getCompanyId()
//                    + " AND P.goodsCategorys.id = " + promotionFilter.getGoodsCategoryId();
//            List<HashMap> listGoodsCategoryId = dataService.executeSelectHQL(HashMap.class, hqlGoodsCategoryId, true, 1, 1);
//        }
        hql += " GROUP BY PTR.id"
                + " ORDER BY PTR.id desc";
        String hqlX = hqlGet + hqlSelected + hql;
        MsalesResults<HashMap> result = new MsalesResults<>();
        List<HashMap> listPTR = dataService.executeSelectHQL(HashMap.class, hqlX, true, page, size);
        List<HashMap> count = dataService.executeSelectHQL(HashMap.class, hqlCount + hqlSelected + hql, true, 0, 0);

        result.setContentList(listPTR);
        result.setCount((long) count.size());
        return result;
    }

    @Override
    public MsalesResults<Promotion> searchPromotionRP(PromotionFilter filter, int companyId, DataService dataService, int page, int size) {
        String hql = "FROM Promotion"
                + " WHERE deletedUser = 0 "
                
                + " AND companys.id = :companyId"
                + " ORDER BY createdAt DESC";

        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        List<Promotion> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);

        List<Integer> removePromotionList = new ArrayList<>();
        if (filter.getApplyRoleId() != null && filter.getApplyRoleId() != 0) {
            for (Promotion promotion : list) {
                if (!Objects.equals(promotion.getApproveRoles().getId(), filter.getApplyRoleId())) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        if (filter.getGoodsCategoryId() != null && filter.getGoodsCategoryId() != 0) {
            for (Promotion promotion : list) {
                if (!Objects.equals(promotion.getGoodsCategorys().getId(), filter.getGoodsCategoryId())) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        if (filter.getGoodsIdList() != null && !filter.getGoodsIdList().isEmpty()) {
            for (Promotion promotion : list) {
                boolean flag = false;
                List<Goods> goodsList = getListPromotionGoods(promotion.getId(), dataService);
                for (int id : filter.getGoodsIdList()) {
                    for (Goods goods : goodsList) {
                        if (id == goods.getId()) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
                if (!flag) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        if (filter.getConditionQuantity() != null && filter.getConditionQuantity() != 0) {
            for (Promotion promotion : list) {
                if (!Objects.equals(promotion.getConditionQuantity(), filter.getConditionQuantity())) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        if (filter.getAwardIdList() != null && !filter.getAwardIdList().isEmpty()) {
            for (Promotion promotion : list) {
                boolean flag = false;
                for (int id : filter.getAwardIdList()) {
                    if (id == promotion.getPromotionAwards().getId()) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        if (filter.getRange() != null) {
            for (Promotion promotion : list) {
                if (filter.getRange() == 1)//bac thang
                {
                    if (promotion.getProRanges() == null) {
                        removePromotionList.add(promotion.getId());
                    }
                } else {//binh thuong
                    if (promotion.getProRanges() != null) {
                        removePromotionList.add(promotion.getId());
                    }
                }

            }
        }

        if (filter.getFromDate() != null) {
            for (Promotion promotion : list) {
                if (filter.getFromDate().compareTo(promotion.getEndDate()) > 0) {
                    removePromotionList.add(promotion.getId());
                }
            }
            //sql += " AND NOT :fromDate > PROMOTION.END_DATE";
        }

        if (filter.getToDate() != null) {
            for (Promotion promotion : list) {
                if (filter.getToDate().compareTo(promotion.getStartDate()) < 0) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        List<Integer> ids = new ArrayList<>();

        if (filter.getChannelIds() != null && !filter.getChannelIds().isEmpty() && filter.getChannelIds().get(0) != 0) {
            ids = filter.getChannelIds();
        } else if (filter.getChannelList() != null && !filter.getChannelList().isEmpty()) {
            for (int i = filter.getChannelList().size() - 1; i >= 0; i--) {
                for (OptionItem optionItem : filter.getChannelList().get(i)) {
                    if (optionItem != null) {
                        if (optionItem.getId() != 0) {
                            ids.add(optionItem.getId());
                        }
                    }
                }
                if (!ids.isEmpty()) {
                    break;
                }
            }
        }

        if (!ids.isEmpty()) {
            //loc theo kenh
            //hien thi ca nhung KM ap dung cho toan cong ty
            List<Channel> channelList = new ArrayList<>();
            for (int id : ids) {
                Channel channel = dataService.getRowById(id, Channel.class);
                if (channel != null) {
                    channelList.add(channel);
                }
            }

            for (Promotion promotion : list) {
                boolean flag = false;
                if (promotion.getApplyScope() == null || promotion.getApplyScope() == 0) {
                    //ap dung cho toan cong ty
                    flag = true;
                } else {
                    //ap dung cho kenh
                    List<PromotionChannel> promotionChannelList = getListPromotionChannel(promotion.getId(), dataService);
                    for (Channel userChannel : channelList) {
                        for (PromotionChannel promotionChannel : promotionChannelList) {
                            //KT truong hop KM ap dung cho kenh sau kenh user
                            Channel channel = promotionChannel.getChannels();
                            while (channel != null) {
                                if (Objects.equals(channel.getId(), userChannel.getId())) {
                                    flag = true;
                                    break;
                                } else {
                                    channel = channel.getParents();
                                }
                            }
                            if (flag) {
                                break;
                            } else {
                                //KT truong hop KM ap dung cho kenh truoc kenh user
                                channel = userChannel;
                                if (channel != null) {
                                    while (channel != null) {
                                        if (Objects.equals(channel.getId(), promotionChannel.getChannels().getId())) {
                                            flag = true;
                                            break;
                                        } else {
                                            channel = channel.getParents();
                                        }
                                    }
                                    if (flag) {
                                        break;
                                    }
                                }
                            }
                        }
                        if (flag) {
                            break;
                        }
                    }
                }
                if (!flag) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }

        if (filter.getConditionIdList() != null && !filter.getConditionIdList().isEmpty()) {
            for (Promotion promotion : list) {
                boolean flag = false;
                List<PromotionConditionalRef> promotionConditionalRefList = getListPromotionConditionalRef(promotion.getId(), dataService);
                for (int id : filter.getConditionIdList()) {
                    for (PromotionConditionalRef promotionConditionalRef : promotionConditionalRefList) {
                        if (promotionConditionalRef.getPromotionConditionals().getId() == id) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
                if (!flag) {
                    removePromotionList.add(promotion.getId());
                }
            }
        }
        if (filter.getAwardIdList() != null && !filter.getAwardIdList().isEmpty()) {
            boolean flag = false;
            for (Promotion promotion : list) {
                for (int id : filter.getAwardIdList()) {
                    if (promotion.getPromotionAwards().getId() == id) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    removePromotionList.add(promotion.getId());
                }
            }

        }

        for (int id : removePromotionList) {
            for (Promotion promotion : list) {
                if (promotion.getId() == id) {
                    list.remove(promotion);
                    break;
                }
            }
        }

        MsalesResults<Promotion> result = new MsalesResults();
        result.setCount((long) list.size());
        int maxPages = list.size() % size != 0 ? list.size() / size + 1 : list.size() / size;
        if (page > maxPages) {
            page = maxPages;
        }
        if (page < 1) {
            page = 1;
        }
        //phan trang
        result.setContentList(list.subList((page - 1) * size, (page * size) > (list.size()) ? list.size() : page * size));
        return result;
    }

    @Override
    public List<HashMap> getPromotionConflict(int companyID) {
        String sql = "SELECT DISTINCT p.name as POS_NAME, p.mobile as POS_MOBILE, p.tel as TEL, p.address as ADDRESS, promo.ID as PROMO_ID, p.ID as POS_ID "
                + "FROM (SELECT DISTINCT * FROM PROMOTION_ACCUMULATION_RETAILER UNION SELECT DISTINCT * FROM PROMOTION_TRANS_RETAILER) AS pro "
                + "LEFT JOIN pos as p ON pro.RETAILER_ID = p.ID "
                + "LEFT JOIN PROMOTION as promo ON pro.RETAILER_ID = promo.ID "
                + "WHERE pro.RETAILER_ID IS NOT NULL AND promo.COMPANY_ID = " + companyID
                + " HAVING COUNT(*) > 1";

        List<HashMap> listProConf = dataService.execSQL(sql);

        return listProConf;
    }

    @Override
    public List<HashMap> getPromotionConflictFollowPOS(int posID) {
        String sql = "SELECT proConf.PROMOTION_ID, proConf.RETAILER_ID "
                + "FROM (SELECT * FROM msales_pharma.PROMOTION_ACCUMULATION_RETAILER where RETAILER_ID = " + posID
                + " union "
                + "SELECT * FROM msales_pharma.PROMOTION_TRANS_RETAILER where RETAILER_ID = " + posID + ") as proConf";

        List<HashMap> listProConf = dataService.execSQL(sql);
        return listProConf;
    }

    @Override
    public List<Promotion> getRunPromotion() {
        // statuss.id = 21 is begin date
        String hql = "FROM Promotion where statuss.id = 21 AND startDate <= now()";

        List<Promotion> listRunning = dataService.executeSelectHQL(Promotion.class, hql, false, 0, 0);

        return listRunning;
    }

    @Override
    public List<Promotion> getEndPromotion() {
        // statuss.id = 22 is running
        String hql = "FROM Promotion where statuss.id = 22 AND endDate < now()";

        List<Promotion> listEnd = dataService.executeSelectHQL(Promotion.class, hql, false, 0, 0);

        return listEnd;
    }

}
