/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import vn.itt.msales.entity.request.MsalesPageRequest;

/**
 *
 * @author vtm
 */
public class ParameterList {

    private Criterion criterions = Restrictions.eq("deletedUser", 0);

    private ArrayList<Order> listOrder = new ArrayList<>();
    private ArrayList<Projection> listProjection = new ArrayList<>();

    private int pageNo;
    private int recordsInPage;

    public ParameterList() {
    }

    public ParameterList(String fieldName, Object value) {
        Criterion criterion = Restrictions.eq(fieldName, value);
        this.criterions = Restrictions.and(this.criterions, criterion);
    }

    public ParameterList(String fieldName, Object value, int pageNo, int recordsInPage) {
        Criterion criterion = Restrictions.eq(fieldName, value);
        this.criterions = Restrictions.and(this.criterions, criterion);
        this.pageNo = pageNo;
        this.recordsInPage = recordsInPage;
    }

    public ParameterList(MsalesPageRequest pages) {
        this.pageNo = pages.getPageNo();
        this.recordsInPage = pages.getRecordsInPage();
    }

    public ParameterList(int pageNo, int recordsInPage) {
        this.pageNo = pageNo;
        this.recordsInPage = recordsInPage;
    }

    public void setPage(int pageNo, int recordsInPage) {
        this.pageNo = pageNo;
        this.recordsInPage = recordsInPage;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public ArrayList<Projection> getListProjection() {
        return listProjection;
    }

    public void setListProjection(ArrayList<Projection> listProjection) {
        this.listProjection = listProjection;
    }

    public int getRecordsInPage() {
        return recordsInPage;
    }

    public void setRecordsInPage(int recordsInPage) {
        this.recordsInPage = recordsInPage;
    }

    public ArrayList<Order> getListOrder() {
        return listOrder;
    }

    public void setListOrder(ArrayList<Order> listOrder) {
        this.listOrder = listOrder;
    }

    public void setOrder(String fieldName) {
        listOrder.add(Order.asc(fieldName));
    }

    public void setOrder(String fieldName, String type) {
        if (type.toUpperCase().equals("DESC")) {
            listOrder.add(Order.desc(fieldName));
        } else {
            listOrder.add(Order.asc(fieldName));
        }
    }

    public void setOrder(String[] fieldNames) {
        for (String fieldName : fieldNames) {
            listOrder.add(Order.asc(fieldName));
        }
    }

    public void setOrder(String[] fieldNames, String type) {
        for (String fieldName : fieldNames) {
            if (type.toUpperCase().equals("DESC")) {
                listOrder.add(Order.desc(fieldName));
            } else {
                listOrder.add(Order.asc(fieldName));
            }
        }
    }

    public ParameterList(String fieldName, Object value, String operator) {
        Criterion criterion = createCriterion(fieldName, value, operator);
        this.criterions = Restrictions.and(this.criterions, criterion);
    }

    public static Criterion createCriterion(String fieldName, Object value, String operator) {
        Criterion criterion = Restrictions.eq(fieldName, value);
        switch (operator) {
            case ">":
                criterion = Restrictions.gt(fieldName, value);
                break;
            case ">=":
                criterion = Restrictions.ge(fieldName, value);
                break;
            case "<":
                criterion = Restrictions.lt(fieldName, value);
                break;
            case "<=":
                criterion = Restrictions.le(fieldName, value);
                break;
            case "!=":
                criterion = Restrictions.ne(fieldName, value);
                break;
            case "like":
                criterion = Restrictions.like(fieldName, value.toString(), MatchMode.ANYWHERE);
                break;
            case "likeSQL":
                criterion = Restrictions.sqlRestriction(fieldName + " like '%" + value.toString() + "%'");
                break;
        }
        return criterion;
    }

    public void add(String fieldName, Object value) {
        Criterion criterion = Restrictions.eq(fieldName, value);
        this.criterions = Restrictions.and(this.criterions, criterion);
    }

    public void add(String fieldName, Object value, String operator) {
        Criterion criterion = createCriterion(fieldName, value, operator);
        this.criterions = Restrictions.and(this.criterions, criterion);
    }

    public void or(String fieldNameLeft, Object valueLeft, String fieldNameRight, Object valueRight) {

        Criterion criterionLeft = Restrictions.eq(fieldNameLeft, valueLeft);
        Criterion criterionRight = Restrictions.eq(fieldNameRight, valueRight);
        Criterion criterion = Restrictions.or(criterionLeft, criterionRight);
        this.criterions = Restrictions.and(this.criterions, criterion);
    }

    public void or(String fieldNameLeft, Object valueLeft, String operatorLeft,
            String fieldNameRight, Object valueRight, String operatorRight) {
        Criterion criterionLeft = createCriterion(fieldNameLeft, valueLeft, operatorLeft);
        Criterion criterionRight = createCriterion(fieldNameRight, valueRight, operatorRight);
        Criterion criterion = Restrictions.or(criterionLeft, criterionRight);
        this.criterions = Restrictions.and(this.criterions, criterion);

    }
    
    public void orFieldsLike(Map<String, String> fieldsAndValues) {
        List<Criterion> criterionList = new ArrayList<>();
        for (Map.Entry<String, String> entry : fieldsAndValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Criterion c = createCriterion(key, value, "like");
            criterionList.add(c);
        }
        Criterion criterion = Restrictions.or((Criterion[])criterionList.toArray(new Criterion[criterionList.size()]));
        this.criterions = Restrictions.and(this.criterions, criterion);
    }

    public void or(Criterion criterion) {
        this.criterions = Restrictions.or(this.criterions, criterion);
    }

    public void or(String fieldName, Object value) {
        Criterion criterion = Restrictions.eq(fieldName, value);
        this.criterions = Restrictions.or(this.criterions, criterion);
    }

    public void or(String fieldName, Object value, String operator) {
        Criterion criterion = createCriterion(fieldName, value, operator);
        this.criterions = Restrictions.or(this.criterions, criterion);
    }

    public Criterion getCriterions() {
        return criterions;
    }

    public void setDistinct(String fieldName) {
        Projection projection = Projections.distinct(Projections.property(fieldName));
        listProjection.add(projection);
    }

    public void in(String name, List<?> objects) {
        this.criterions = Restrictions.and(this.criterions, Restrictions.in(name, objects));
    }

    public void notin(String name, List<?> objects) {
        this.criterions = Restrictions.and(this.criterions, Restrictions.not(Restrictions.in(name, objects)));
    }

}
