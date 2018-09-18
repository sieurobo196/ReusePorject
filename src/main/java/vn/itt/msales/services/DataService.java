/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.hibernate.Session;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.OptionQuery;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;

import vn.itt.msales.logex.MSalesException;

/**
 *
 * @author vtm036
 */
public interface DataService {

    /**
     * set branch database
     */
    public void setBranch(int branch);

    /**
     * Insert object to database
     * 
     * @param object
     *            is object
     * @return id of row
     */
    public int insertRow(Object object) throws MSalesException;

    /**
     * get all rows in table from database
     * 
     * @param <T>
     * @param classs
     * @return list object
     */
    public <T> List<T> getList(Class<T> classs) throws MSalesException;

    /**
     * get an object of of row in table from database
     * 
     * @param <T>
     * @param id
     *            is ID of object
     * @param classs
     * @return is object
     */
    public <T> T getRowById(int id, Class<T> classs) throws MSalesException;

    /**
     * Update property of the Object
     * 
     * @param object
     *            is object will be update
     * @return id of Object
     */
    public int updateRow(Object object) throws MSalesException;

    /**
     * Delete Object with id
     * 
     * @param id
     *            is ID of Object
     *
     */
    public boolean deleteRow(int id, Class<?> classs) throws MSalesException;
    /**
     * Execute a single SQL statement that is NOT a SELECT or any other SQL
     * statement that returns data.
     * <p>
     * @param sql the SQL statement to be executed. Multiple statements
     *            separated by semicolons are not supported.
     * <p>
     * @throws MSalesException if the SQL string is invalid (@{link
     *                         SQLException})
     */
    public <T> T execSQL(String tableName, String[] field, String[] value) throws MSalesException;

    public <T> List<T> getListOption(Class<T> clazz, ParameterList parameterList);

    public int updateSynchronize(Class<?> clazz, LinkedHashMap<String, Object> object);
    public int updateSync(Object object);
    public int deleteSynch(Object object);
    public int executeHQL(String hql);
    public int executeHQL(String hql,List<MsalesParameter> parameters);
    public List<Integer> insertArray(List objects);
    public void insertOrUpdateArray(List objects);
    public boolean updateArray(List objects);
    public boolean multiQueryArray(List<OptionQuery> optionQuerys);
    public int insert(Object object);

    public <T> List<T> executeSelectHQL(Class<T> classs, String hql,boolean transform, int pageNo, int recordsInPage);
    public <T> List<T> executeSelectHQL(String hql, List<MsalesParameter> parameters, boolean transform, int pageNo, int recordsInPage);
    public <T> List<T> executeSelectSQL(String sql, List<MsalesParameter> parameters, boolean transform, int pageNo, int recordsInPage, String entity, Class clazz);
    
    public int deleteSynchronize(Class<?> clazz, LinkedHashMap<String, Object> object);

    public <T> List<T> findByNamedQuery(String namedQuery, String[] fields, String[] values);

    public int updateNamedQuery(String namedQuery, String[] fields, String[] values);

    public <T> T execSQL(String sql);

    public <T> MsalesResults<T> getListOption(Class<T> clazz, ParameterList parameterList, boolean isList);
    public Session openSession();

    public void insertOrUpdate(Object object) throws MSalesException;
}
