/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.param;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vtm
 */
public class MsalesParameter {

    private String name;
    private Object value;
    private int type = 0;//type = 1=>Collection;type=2=>Date;type=3=>Timestamp

    public MsalesParameter() {
    }

    public MsalesParameter(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public MsalesParameter(String name, Object value, int type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static List<MsalesParameter> createList(String name, Object value) {
        List<MsalesParameter> ret = new ArrayList<>();
        MsalesParameter parameter = new MsalesParameter(name, value);
        ret.add(parameter);
        return ret;
    }

    public static List<MsalesParameter> createList(String name, Object value, int type) {
        List<MsalesParameter> ret = new ArrayList<>();
        MsalesParameter parameter = new MsalesParameter(name, value, type);
        ret.add(parameter);
        return ret;
    }
    
    public static List<MsalesParameter> createList(MsalesParameter parameter) {
        List<MsalesParameter> ret = new ArrayList<>();
        ret.add(parameter);
        return ret;
    }

    public static MsalesParameter create(String name, Object value) {
        MsalesParameter parameter = new MsalesParameter(name, value);
        return parameter;
    }

    public static MsalesParameter create(String name, Object value, int type) {
        MsalesParameter parameter = new MsalesParameter(name, value, type);
        return parameter;
    }

    public static MsalesParameter createBadInteger(String name) {
        MsalesParameter parameter = new MsalesParameter(name, -1);
        return parameter;
    }

    public static MsalesParameter createBadString(String name) {
        MsalesParameter parameter = new MsalesParameter(name, "''");
        return parameter;
    }

}
