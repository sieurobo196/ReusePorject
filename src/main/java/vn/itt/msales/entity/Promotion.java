package vn.itt.msales.entity;
// Generated Oct 9, 2015 3:36:35 PM by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import vn.itt.msales.common.json.validator.MsalesValidator;

/**
 * Promotion generated by hbm2java
 */
@Entity
@Table(name = "PROMOTION"
)
@JsonFilter("JSONFILTER")
public class Promotion implements java.io.Serializable, Comparable<Promotion> {

    private static final long serialVersionUID = 4040452406518958068L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 512)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 1024)
    private String description;

    @Transient
    private Integer approveRoleId;

    public Integer getApproveRoleId() {
        return approveRoleId;
    }

    public void setApproveRoleId(Integer approveRoleId) {
        this.approveRoleId = approveRoleId;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "APPROVE_ROLE_ID")
    private UserRole approveRoles;

    public UserRole getApproveRoles() {
        return approveRoles;
    }

    public void setApproveRoles(UserRole approveRoles) {
        this.approveRoles = approveRoles;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_SHORT)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE", length = 19)
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = MsalesValidator.DATE_FORMAT_SHORT)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE", length = 19)
    private Date endDate;

    @Transient
    private Integer companyId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private Company companys;

    public Company getCompanys() {
        return companys;
    }

    public void setCompanys(Company companys) {
        this.companys = companys;
    }

    @Transient
    private Integer statusId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STATUS_ID", nullable = false)
    private Status statuss;

    public Status getStatuss() {
        return statuss;
    }

    public void setStatuss(Status statuss) {
        this.statuss = statuss;
    }

    @Column(name = "CODE", length = 512)
    private String code;
    @Column(name = "NOTE", length = 512)
    private String note;

    @Column(name = "APPROVE_USER_ID")
    private Integer approveUserId;

    @Column(name = "APPLY_SCOPE")
    private Integer applyScope;

    @Transient
    private boolean scope;

    public boolean isScope() {
        scope = (applyScope != null && applyScope == 1);
        return scope;
    }

    public void setScope(boolean scope) {
        this.scope = scope;
        if (scope) {
            applyScope = 1;
        } else {
            applyScope = 0;
        }
    }

    @Column(name = "CONDITION_QUANTITY")
    private Integer conditionQuantity;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "PRO_AWARD_CODE", length = 64)
    private String proAwardCode;
    @Column(name = "PRO_AWARD_QUANTITY")
    private Integer proAwardQuantity;

    @Transient
    private Integer proAwardGoodsCategory;

    public Integer getProAwardGoodsCategory() {
        return proAwardGoodsCategory;
    }

    public void setProAwardGoodsCategory(Integer proAwardGoodsCategory) {
        this.proAwardGoodsCategory = proAwardGoodsCategory;
    }

    @Transient
    private Integer proAwardGoodsId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRO_AWARD_GOODS_ID")
    private Goods proAwardGoodss;

    public Goods getProAwardGoodss() {
        return proAwardGoodss;
    }

    public void setProAwardGoodss(Goods proAwardGoodss) {
        this.proAwardGoodss = proAwardGoodss;
    }

    @Transient
    private Integer proAwardUnitId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRO_AWARD_UNIT_ID")
    private Unit proAwardUnits;

    public Unit getProAwardUnits() {
        return proAwardUnits;
    }

    public void setProAwardUnits(Unit proAwardUnits) {
        this.proAwardUnits = proAwardUnits;
    }

    @Transient
    private Integer goodsCategoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GOODS_CATEGORY_ID")
    private GoodsCategory goodsCategorys;

    public GoodsCategory getGoodsCategorys() {
        return goodsCategorys;
    }

    public void setGoodsCategorys(GoodsCategory goodsCategorys) {
        this.goodsCategorys = goodsCategorys;
    }

    @Transient
    private Integer proAwardId;

    @JoinColumn(name = "PRO_AWARD_ID")
    @ManyToOne(fetch = FetchType.EAGER)

    private PromotionAward promotionAwards;

    public PromotionAward getPromotionAwards() {
        return promotionAwards;
    }

    public void setPromotionAwards(PromotionAward promotionAwards) {
        this.promotionAwards = promotionAwards;
    }

    @Transient
    private Integer goodsIdMin;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GOODS_ID_MIN")
    private Goods goodsIdMins;

    public Goods getGoodsIdMins() {
        return goodsIdMins;
    }

    public void setGoodsIdMins(Goods goodsIdMins) {
        this.goodsIdMins = goodsIdMins;
    }

    @Column(name = "QUANTITY_MIN")
    private Integer quantityMin;

    @Transient
    private Integer awardOtherId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "AWARD_OTHER_ID")
    private PromotionAwardOther awardOthers;

    public PromotionAwardOther getAwardOthers() {
        return awardOthers;
    }

    public void setAwardOthers(PromotionAwardOther awardOthers) {
        this.awardOthers = awardOthers;
    }

    @Transient
    private Integer proRangeId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRO_RANGE_ID")
    private PromotionRangeAward proRanges;

    public PromotionRangeAward getProRanges() {
        return proRanges;
    }

    public void setProRanges(PromotionRangeAward proRanges) {
        this.proRanges = proRanges;
    }
    @Transient
    private Integer goodsCategoryIdMin;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GOODS_CATEGORY_ID_MIN")
    private GoodsCategory goodsCategoryIdMins;

    public GoodsCategory getGoodsCategoryIdMins() {
        return goodsCategoryIdMins;
    }

    public void setGoodsCategoryIdMins(GoodsCategory goodsCategoryIdMins) {
        this.goodsCategoryIdMins = goodsCategoryIdMins;
    }

    @Column(name = "FLAG_LEVEL")
    private Integer flagLevel;

    @Transient
    private boolean flag;

    public boolean isFlag() {
        flag = (flagLevel != null && flagLevel == 1);
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
        if (flag) {
            flagLevel = 1;
        } else {
            flagLevel = 0;
        }
    }

    @Column(name = "URL_IMAGE", length = 512)
    private String urlImage;
    @Column(name = "IS_REGISTER")
    private Integer isRegister;
    @Column(name = "IS_ONCE")
    private Integer isOnce = 1;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, length = 19)
    private Date createdAt;
    @Column(name = "CREATED_USER", nullable = false)
    private Integer createdUser;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT", length = 19)
    private Date updatedAt;
    @Column(name = "UPDATED_USER", nullable = false)
    private Integer updatedUser;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DELETED_AT", length = 19)
    private Date deletedAt;
    @Column(name = "DELETED_USER", nullable = false)
    private Integer deletedUser;

    @Transient
    private boolean reg;

    @Transient
    private boolean isMany = false;

    public boolean isReg() {
        this.reg = (this.isRegister != null && this.isRegister == 1);
        return this.reg;
    }

    public void setReg(boolean reg) {
        this.reg = reg;
        if (this.reg) {
            this.isRegister = 1;
        } else {
            this.isRegister = 0;
        }
    }

    public boolean isIsMany() {
        this.isMany = (this.isOnce == null || this.isOnce == 0);
        return this.isMany;
    }

    public void setIsMany(boolean isMany) {
        this.isMany = isMany;
        if (this.isMany) {
            this.isOnce = 0;
        } else {
            this.isOnce = 1;
        }
    }

    @Transient
    private List<Integer> conditionList;

    public List<Integer> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<Integer> conditionList) {
        this.conditionList = conditionList;
    }

    @Transient
    private List<Integer> goodsIdList;

    public List<Integer> getGoodsIdList() {
        return goodsIdList;
    }

    public void setGoodsIdList(List<Integer> goodsIdList) {
        this.goodsIdList = goodsIdList;
    }

    @Transient
    @JsonIgnoreProperties(value = {"goodsCategorys", "goodsCode", "parents", "statuss", "isRecover", "price", "factor", "isFocus"})
    private List<Goods> goodsList = new ArrayList<>();

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    @Transient
    private boolean isRange;

    public boolean isIsRange() {
        return isRange;
    }

    public void setIsRange(boolean isRange) {
        this.isRange = isRange;
    }

    @Transient
    private Integer discount;

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    @Transient
    private Integer proAwardOtherQuantity;

    public Integer getProAwardOtherQuantity() {
        return proAwardOtherQuantity;
    }

    public void setProAwardOtherQuantity(Integer proAwardOtherQuantity) {
        this.proAwardOtherQuantity = proAwardOtherQuantity;
    }

    @Transient
    private List<String> conditionString = new ArrayList<>();

    public List<String> getConditionString() {
        return conditionString;
    }

    public void setConditionString(List<String> conditionString) {
        this.conditionString = conditionString;
    }

    @Transient
    private List<GoodsCategory> goodsCategoryList = new ArrayList<>();

    public List<GoodsCategory> getGoodsCategoryList() {
        return goodsCategoryList;
    }

    public void setGoodsCategoryList(List<GoodsCategory> goodsCategoryList) {
        this.goodsCategoryList = goodsCategoryList;
    }

    public void generateConditionString() {
        NumberFormat numberFormat = new DecimalFormat("#,##0");
        try {
            if (proRanges != null) {//KM bac thang
                String ret = "";
                if (conditionQuantity != null) {
                    switch (conditionQuantity) {
                        case 1:
                            ret = "Đạt số lượng từ " + "%s" + "(sp)";
                            break;
                        case 2:
                            ret = "Đạt doanh số từ " + "%s" + " VNĐ";
                            break;
                        case 3:
                            ret = "Tích lũy số lượng từ " + "%s" + "(sp)";
                            break;
                        case 4:
                            ret = "Tích lũy doanh số từ " + "%s" + " VNĐ";
                            break;
                        default:
                            ret = "";
                            break;
                    }
                }
                switch (promotionAwards.getId()) {
                    case 1:
                        ret += " tặng %s " + "(" + proAwardUnits.getName() + ") " + proAwardGoodss.getName();
                        break;
                    case 2:
                        ret += " tặng %s " + awardOthers.getName();
                        break;
                    case 3:
                        ret += " giảm %s VNĐ";//thieu discount
                        break;
                    case 4:
                        ret += " chiết khấu %s%";//discount
                        break;
                    default:
                        ret += "";
                        break;
                }

                if (flagLevel != null && flagLevel == 1) {//tính từng bậc
                    if (proRanges.getPromotionR01() != null) {
                        int sumR = proRanges.getPromotionR01();
                        int sumV = proRanges.getPromotionV01();
                        String txt1 = String.format(ret, numberFormat.format(sumR), numberFormat.format(sumV));
                        conditionString.add(txt1);
                        if (proRanges.getPromotionR02() != null) {
                            sumR += proRanges.getPromotionR02();
                            sumV += proRanges.getPromotionV02();
                            String txt2 = String.format(ret, numberFormat.format(sumR), numberFormat.format(sumV));
                            conditionString.add(txt2);
                            if (proRanges.getPromotionR03() != null) {
                                sumR += proRanges.getPromotionR03();
                                sumV += proRanges.getPromotionV03();
                                String txt3 = String.format(ret, numberFormat.format(sumR), numberFormat.format(sumV));
                                conditionString.add(txt3);
                                if (proRanges.getPromotionR04() != null) {
                                    sumR += proRanges.getPromotionR04();
                                    sumV += proRanges.getPromotionV04();
                                    String txt4 = String.format(ret, numberFormat.format(sumR), numberFormat.format(sumV));
                                    conditionString.add(txt4);
                                    if (proRanges.getPromotionR05() != null) {
                                        sumR += proRanges.getPromotionR05();
                                        sumV += proRanges.getPromotionV05();
                                        String txt5 = String.format(ret, numberFormat.format(sumV), numberFormat.format(sumR));
                                        conditionString.add(txt5);
                                    }
                                }
                            }
                        }
                    }
                } else {//chi 1 bac thoa dk
                    if (proRanges.getPromotionR01() != null) {
                        String txt1 = String.format(ret, numberFormat.format(proRanges.getPromotionR01()), numberFormat.format(proRanges.getPromotionV01()));
                        conditionString.add(txt1);
                        if (proRanges.getPromotionR02() != null) {
                            String txt2 = String.format(ret, numberFormat.format(proRanges.getPromotionR02()), numberFormat.format(proRanges.getPromotionV02()));
                            conditionString.add(txt2);
                            if (proRanges.getPromotionR03() != null) {
                                String txt3 = String.format(ret, numberFormat.format(proRanges.getPromotionR03()), numberFormat.format(proRanges.getPromotionV03()));
                                conditionString.add(txt3);
                                if (proRanges.getPromotionR04() != null) {
                                    String txt4 = String.format(ret, numberFormat.format(proRanges.getPromotionR04()), numberFormat.format(proRanges.getPromotionV04()));
                                    conditionString.add(txt4);
                                    if (proRanges.getPromotionR05() != null) {
                                        String txt5 = String.format(ret, numberFormat.format(proRanges.getPromotionR05()), numberFormat.format(proRanges.getPromotionV05()));
                                        conditionString.add(txt5);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (promotionAwards != null) {//KM thuong
                String ret = "";
                if (conditionQuantity != null) {
                    switch (conditionQuantity) {
                        case 1:
                            ret = "Đạt số lượng từ " + numberFormat.format(quantity) + "(sp)";
                            break;
                        case 2:
                            ret = "Đạt doanh số từ " + numberFormat.format(quantity) + " VNĐ";
                            break;
                        case 3:
                            ret = "Tích lũy số lượng từ " + numberFormat.format(quantity) + "(sp)";
                            break;
                        case 4:
                            ret = "Tích lũy doanh số từ " + numberFormat.format(quantity) + " VNĐ";
                            break;
                        default:
                            ret = "";
                            break;
                    }
                }

                switch (promotionAwards.getId()) {
                    case 1:
                        String unit = "";
                        if (proAwardUnits != null) {
                            unit = "(" + proAwardUnits.getName() + ")";
                        }
                        ret += " tặng " + numberFormat.format(proAwardQuantity) + unit + " " + proAwardGoodss.getName();
                        break;
                    case 2:
                        String otherUnit = "";
                        if (awardOthers != null && awardOthers.getUnits() != null) {
                            otherUnit = "(" + awardOthers.getUnits().getName() + ")";
                        }
                        ret += " tặng " + numberFormat.format(proAwardQuantity) + otherUnit + " " + awardOthers.getName();
                        break;
                    case 3:
                        ret += " giảm " + numberFormat.format(proAwardQuantity) + " VNĐ";//thieu discount
                        break;
                    case 4:
                        ret += " chiết khấu " + numberFormat.format(proAwardQuantity) + "%";//discount
                        break;
                    default:
                        ret += "";
                        break;
                }
                conditionString.add(ret);
            }
        } catch (Exception ex) {
        }
    }
    
    @Transient
    private Integer accumulationRetailerId = 0;

    public Integer getAccumulationRetailerId() {
        return accumulationRetailerId;
    }

    public void setAccumulationRetailerId(Integer accumulationRetailerId) {
        this.accumulationRetailerId = accumulationRetailerId;
    }
    

    public Promotion() {
    }

    public Promotion(String name, Integer companyId, String desciption, Integer approveRoleId, Integer statusId, Date createdAt, Integer createdUser, Integer updatedUser, Integer deletedUser) {
        this.name = name;
        this.companyId = companyId;
        this.description = desciption;
        this.approveRoleId = approveRoleId;
        this.statusId = statusId;
        this.createdAt = createdAt;
        this.createdUser = createdUser;
        this.updatedUser = updatedUser;
        this.deletedUser = deletedUser;
    }

    public Promotion(String name, Integer companyId, String desciption, Integer approveRoleId, Date startDate, Date endDate, Integer statusId, String code, String note, Integer approveUserId, Integer applyScope, Integer conditionQuantity, Integer quantity, String proAwardCode, Integer proAwardQuantity, Integer proAwardGoodsId, Integer proAwardUnitId, Integer goodsCategoryId, Integer proAwardId, Integer goodsIdMin, Integer quantityMin, Integer awardOtherId, Integer proRangeId, Integer goodsCategoryIdMin, Integer flagLevel, String urlImage, Integer isRegister, Integer isOnce, Date createdAt, Integer createdUser, Date updatedAt, Integer updatedUser, Date deletedAt, Integer deletedUser) {
        this.name = name;
        this.companyId = companyId;
        this.description = desciption;
        this.approveRoleId = approveRoleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.statusId = statusId;
        this.code = code;
        this.note = note;
        this.approveUserId = approveUserId;
        this.applyScope = applyScope;
        this.conditionQuantity = conditionQuantity;
        this.quantity = quantity;
        this.proAwardCode = proAwardCode;
        this.proAwardQuantity = proAwardQuantity;
        this.proAwardGoodsId = proAwardGoodsId;
        this.proAwardUnitId = proAwardUnitId;
        this.goodsCategoryId = goodsCategoryId;
        this.proAwardId = proAwardId;
        this.goodsIdMin = goodsIdMin;
        this.quantityMin = quantityMin;
        this.awardOtherId = awardOtherId;
        this.proRangeId = proRangeId;
        this.goodsCategoryIdMin = goodsCategoryIdMin;
        this.flagLevel = flagLevel;
        this.urlImage = urlImage;
        this.isRegister = isRegister;
        this.isOnce = isOnce;
        this.createdAt = createdAt;
        this.createdUser = createdUser;
        this.updatedAt = updatedAt;
        this.updatedUser = updatedUser;
        this.deletedAt = deletedAt;
        this.deletedUser = deletedUser;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public Integer getCompanyId() {
        return this.companyId;
    }

    @JsonSetter
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
        if (companyId != null) {
            Company company = new Company();
            company.setId(companyId);
            this.companys = company;
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @JsonIgnore
    public Integer getStatusId() {
        return this.statusId;
    }

    @JsonSetter
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
        if (statusId != null) {
            Status status = new Status();
            status.setId(statusId);
            this.statuss = status;
        }
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getApproveUserId() {
        return this.approveUserId;
    }

    public void setApproveUserId(Integer approveUserId) {
        this.approveUserId = approveUserId;
    }

    public Integer getApplyScope() {
        return this.applyScope;
    }

    public void setApplyScope(Integer applyScope) {
        this.applyScope = applyScope;
    }

    public Integer getConditionQuantity() {
        return this.conditionQuantity;
    }

    public void setConditionQuantity(Integer conditionQuantity) {
        this.conditionQuantity = conditionQuantity;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProAwardCode() {
        return this.proAwardCode;
    }

    public void setProAwardCode(String proAwardCode) {
        this.proAwardCode = proAwardCode;
    }

    public Integer getProAwardQuantity() {
        return this.proAwardQuantity;
    }

    public void setProAwardQuantity(Integer proAwardQuantity) {
        this.proAwardQuantity = proAwardQuantity;
    }

    @JsonIgnore
    public Integer getProAwardGoodsId() {
        return this.proAwardGoodsId;
    }

    @JsonSetter
    public void setProAwardGoodsId(Integer proAwardGoodsId) {
        this.proAwardGoodsId = proAwardGoodsId;
        if (proAwardGoodsId != null) {
            Goods goods = new Goods();
            goods.setId(proAwardGoodsId);
            this.proAwardGoodss = goods;

        }
    }

    @JsonIgnore
    public Integer getProAwardUnitId() {
        return this.proAwardUnitId;
    }

    @JsonSetter
    public void setProAwardUnitId(Integer proAwardUnitId) {
        this.proAwardUnitId = proAwardUnitId;
        if (proAwardUnitId != null) {
            Unit unit = new Unit();
            unit.setId(proAwardUnitId);
            this.proAwardUnits = unit;
        }
    }

    @JsonIgnore
    public Integer getGoodsCategoryId() {
        return this.goodsCategoryId;
    }

    @JsonSetter
    public void setGoodsCategoryId(Integer goodsCategoryId) {
        this.goodsCategoryId = goodsCategoryId;
        if (goodsCategoryId != null) {
            GoodsCategory goodsCategory = new GoodsCategory();
            goodsCategory.setId(goodsCategoryId);
            this.goodsCategorys = goodsCategory;
        }
    }

    @JsonIgnore
    public Integer getProAwardId() {
        return this.proAwardId;
    }

    @JsonSetter
    public void setProAwardId(Integer proAwardId) {
        this.proAwardId = proAwardId;
        if (proAwardId != null) {
            PromotionAward promotionAward = new PromotionAward();
            promotionAward.setId(proAwardId);
            this.promotionAwards = promotionAward;
        }
    }

    @JsonIgnore
    public Integer getGoodsIdMin() {
        return this.goodsIdMin;
    }

    @JsonSetter
    public void setGoodsIdMin(Integer goodsIdMin) {
        this.goodsIdMin = goodsIdMin;
        if (goodsIdMin != null) {
            Goods goods = new Goods();
            goods.setId(goodsIdMin);
            this.goodsIdMins = goods;
        }
    }

    public Integer getQuantityMin() {
        return this.quantityMin;

    }

    public void setQuantityMin(Integer quantityMin) {
        this.quantityMin = quantityMin;
    }

    @JsonIgnore
    public Integer getAwardOtherId() {
        return this.awardOtherId;
    }

    @JsonSetter
    public void setAwardOtherId(Integer awardOtherId) {
        this.awardOtherId = awardOtherId;
        if (awardOtherId != null) {
            PromotionAwardOther promotionAwardOther = new PromotionAwardOther();
            promotionAwardOther.setId(awardOtherId);
            this.awardOthers = promotionAwardOther;
        }
    }

    @JsonIgnore
    public Integer getProRangeId() {
        return this.proRangeId;
    }

    @JsonSetter
    public void setProRangeId(Integer proRangeId) {
        this.proRangeId = proRangeId;
        if (proRangeId != null) {
            PromotionRangeAward promotionRangeAward = new PromotionRangeAward();
            promotionRangeAward.setId(proRangeId);
            this.proRanges = promotionRangeAward;
        }
    }

    @JsonIgnore
    public Integer getGoodsCategoryIdMin() {
        return this.goodsCategoryIdMin;
    }

    @JsonSetter
    public void setGoodsCategoryIdMin(Integer goodsCategoryIdMin) {
        this.goodsCategoryIdMin = goodsCategoryIdMin;
        if (goodsCategoryIdMin != null) {
            GoodsCategory goodsCategory = new GoodsCategory();
            goodsCategory.setId(goodsCategoryIdMin);
            this.goodsCategoryIdMins = goodsCategory;

        }
    }

    public Integer getFlagLevel() {
        return this.flagLevel;
    }

    public void setFlagLevel(Integer flagLevel) {
        this.flagLevel = flagLevel;
    }

    public String getUrlImage() {
        return this.urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Integer getIsRegister() {
        return this.isRegister;
    }

    public void setIsRegister(Integer isRegister) {
        this.isRegister = isRegister;
    }

    public Integer getIsOnce() {
        return this.isOnce;
    }

    public void setIsOnce(Integer isOnce) {
        this.isOnce = isOnce;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedUser() {
        return this.createdUser;
    }

    public void setCreatedUser(Integer createdUser) {
        this.createdUser = createdUser;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpdatedUser() {
        return this.updatedUser;
    }

    public void setUpdatedUser(Integer updatedUser) {
        this.updatedUser = updatedUser;
    }

    public Date getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getDeletedUser() {
        return this.deletedUser;
    }

    public void setDeletedUser(Integer deletedUser) {
        this.deletedUser = deletedUser;
    }

    @Override
    public int compareTo(Promotion object) {
        return object.createdAt.compareTo(createdAt);
    }

}