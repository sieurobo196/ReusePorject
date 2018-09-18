package vn.itt.msales.sales.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import vn.itt.msales.app.service.AppService;
import vn.itt.msales.app.service.DateUtils;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.entity.SalesOrder;
import vn.itt.msales.entity.SalesOrderDetails;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.SalesTransDetails;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.services.DataService;

/**
 *
 * @author ChinhNQ
 */
public class MsalesSaleTransServiceImpl implements MsalesSaleTransService {

    @Autowired
    private AppService appService;

    @Override
    public MsalesResults<SalesTrans> searchTrans(Filter filter, int companyId, int page, int size, DataService dataService) {
        String hqlSales = "FROM SalesTrans"
                + " WHERE deletedUser = 0"
                + " AND transType = 2"
                //+ " AND transStatus = 1"
                + " AND companys.id = :companyId";
        String hqlOrder = "FROM SalesOrder"
                + " WHERE deletedUser = 0"
                + " AND (statuss.value = 4 OR statuss.value = 5 OR statuss.value = 6)"
                + " AND companys.id = :companyId";
        String hqlCondition = "";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);

        if (filter.getStartDateString() != null && !filter.getStartDateString().trim().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
            try {
                Date fromDate = sdf.parse(filter.getStartDateString());
                hqlCondition += " AND createdAt >= :fromDate";
                parameters.add(MsalesParameter.create("fromDate", fromDate, 2));
            } catch (ParseException ex) {
            }
        }

        if (filter.getEndDateString() != null && !filter.getEndDateString().trim().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
            try {
                Date toDate = sdf.parse(filter.getEndDateString());
                hqlCondition += " AND createdAt < :toDate";
                parameters.add(MsalesParameter.create("toDate", DateUtils.addDay(toDate, 1), 2));
            } catch (ParseException ex) {
            }
        }

//        if (filter.getSearchText() != null && !filter.getSearchText().trim().isEmpty()) {
//            //kiem tra ten ĐBH trươc
//            hqlOrder += " AND stocks.poss.name LIKE :searchText";
//            hqlSales += " AND toStocks.poss.name LIKE :searchText";
//            parameters.add(MsalesParameter.create("searchText", "%" + filter.getSearchText() + "%"));
//        }
        hqlOrder += hqlCondition;
        hqlSales += hqlCondition;

        List<SalesTrans> salesList = dataService.executeSelectHQL(hqlSales, parameters, false, 0, 0);
        List<SalesOrder> orderList = dataService.executeSelectHQL(hqlOrder, parameters, false, 0, 0);

        //chuyen orderList thanh salestransList
        List<SalesTrans> orderCovertList = new ArrayList<>();
        for (SalesOrder salesOrder : orderList) {
            User user = appService.getUserByMCPWeek(salesOrder.getPos().getId(), salesOrder.getCreatedAt(), dataService);
            if (user != null) {
                boolean flag = false;
                if (filter.getUserId() != null && filter.getUserId() != 0) {
                    if (Objects.equals(filter.getUserId(), user.getId())) {
                        flag = true;
                    }
                } else if (filter.getUserList() != null && !filter.getUserList().isEmpty()) {
                    for (OptionItem optionItem : filter.getUserList()) {
                        if (Objects.equals(optionItem.getId(), user.getId())) {
                            flag = true;
                            break;
                        }
                    }
                }

                if (flag) {
                    boolean flagText = false;
                    if (filter.getSearchText() != null && !filter.getSearchText().trim().isEmpty()) {
                        if ((user.getLastName().trim() + " " + user.getFirstName().trim()).toUpperCase().contains(filter.getSearchText().toUpperCase())
                                || user.getUsername().toUpperCase().contains(filter.getSearchText().toUpperCase())
                                || salesOrder.getPos().getName().toUpperCase().contains(filter.getSearchText().toUpperCase())
                                || salesOrder.getPos().getPosCode().toUpperCase().contains(filter.getSearchText().toUpperCase())) {
                            flagText = true;
                        }
                    } else {
                        flagText = true;
                    }

                    if (flagText) {
                        SalesTrans salesTrans = new SalesTrans();
                        salesTrans.setId(1);
                        salesTrans.setEmployee(user);
                        salesTrans.setCompanys(salesOrder.getCompanys());
                        salesTrans.setTransCode(salesOrder.getTransCode());
                        salesTrans.setToStocks(salesOrder.getStocks());
                        salesTrans.setSalesTransDate(salesOrder.getUpdatedAt());
                        salesTrans.setNote(salesOrder.getNote());
                        salesTrans.setCreatedAt(salesOrder.getCreatedAt());
                        salesTrans.setCreatedUser(salesOrder.getCreatedUsers().getId());
                        salesTrans.setUpdatedAt(salesOrder.getUpdatedAt());
                        salesTrans.setUpdatedUser(salesOrder.getUpdatedUser());
                        salesTrans.setDeletedAt(salesOrder.getDeletedAt());
                        salesTrans.setDeletedUser(salesOrder.getDeletedUser());

                        List<SalesOrderDetails> salesOrderDetailsList = appService.getListSalesOrderDetails(salesOrder.getId(), dataService);
                        //convert to salesTransDetails
                        List<SalesTransDetails> salesTransDetailsList = new ArrayList<>();
                        for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                            SalesTransDetails salesTransDetails = new SalesTransDetails();
                            salesTransDetails.setGoodss(salesOrderDetails.getGoodss());
                            salesTransDetails.setGoodsUnits(salesOrderDetails.getGoodsUnits());
                            salesTransDetails.setSalesTransDate(salesOrderDetails.getSalesTransDate());
                            salesTransDetails.setQuantity(salesOrderDetails.getQuantity());
                            salesTransDetails.setPrice(salesOrderDetails.getPrice());
                            salesTransDetails.setIsFocus(salesOrderDetails.getIsFocus());
                            salesTransDetailsList.add(salesTransDetails);
                        }
                        salesTrans.setSalesTransDetails(salesTransDetailsList);
                        orderCovertList.add(salesTrans);
                    }
                }
            }
        }

        //create list luu id salesTran thoa dk User
        List<SalesTrans> salesOKList = new ArrayList<>();
        for (SalesTrans salesTrans : salesList) {
            if (salesTrans.getToStocks() != null && salesTrans.getToStocks().getPoss() != null) {
                User user = appService.getUserByMCPWeek(salesTrans.getToStocks().getPoss().getId(), salesTrans.getCreatedAt(), dataService);
                if (user != null) {
                    salesTrans.setEmployee(user);
                    boolean flag = false;
                    if (filter.getUserId() != null && filter.getUserId() != 0) {
                        if (Objects.equals(filter.getUserId(), user.getId())) {
                            flag = true;
                        }
                    } else if (filter.getUserList() != null && !filter.getUserList().isEmpty()) {
                        for (OptionItem optionItem : filter.getUserList()) {
                            if (Objects.equals(optionItem.getId(), user.getId())) {
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        boolean flagText = false;
                        if (filter.getSearchText() != null && !filter.getSearchText().trim().isEmpty()) {
                            if ((user.getLastName().trim() + " " + user.getFirstName().trim()).toUpperCase().contains(filter.getSearchText().toUpperCase())
                                    || user.getUsername().toUpperCase().contains(filter.getSearchText().toUpperCase())
                                    || salesTrans.getToStocks().getPoss().getName().toUpperCase().contains(filter.getSearchText().toUpperCase())
                                    || salesTrans.getToStocks().getPoss().getPosCode().toUpperCase().contains(filter.getSearchText().toUpperCase())) {
                                flagText = true;
                            }
                        } else {
                            flagText = true;
                        }

                        if (flagText) {
                            salesTrans.setSalesTransDetails(appService.getListSalesTransDetails(salesTrans.getId(), dataService));;
                            salesOKList.add(salesTrans);
                        }
                    }
                }
            }
        }

        List list = new ArrayList<>();
        list.addAll(salesOKList);
        list.addAll(orderCovertList);

        MsalesResults<SalesTrans> result = new MsalesResults<>();
        result.setCount((long) list.size());

        int maxPages = list.size() % size != 0 ? list.size() / size + 1 : list.size() / size;
        if (page > maxPages) {
            page = maxPages;
        }
        if (page < 1) {
            page = 1;
        }

        //sort date        
        Collections.sort(list);
        //phan trang
        result.setContentList(list.subList((page - 1) * size, (page * size) > (list.size()) ? list.size() : page * size));

        return result;
    }
}
