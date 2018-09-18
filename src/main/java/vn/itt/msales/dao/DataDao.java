/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.OptionQuery;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.logex.MSalesException;

/**
 * @author ChinhNQ
 * @version
 * @since 29 May 2015 10:11:37
 * msales#com.itt.msales.dao.DataDao.java
 *
 */
public interface DataDao {

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
    public <T> List<T>
    getList (Class<T> classs) throws MSalesException;

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
     * @param <T>
     * @param tableName
     * @param field
     * @param value
     * @param sql the SQL statement to be executed. Multiple statements
     *            separated by semicolons are not supported.
     * <p>
     * @return 
     * @throws MSalesException if the SQL string is invalid (@{link
     *                         SQLException})
     */
    public <T> T execSQL(String tableName, String[] field, String[] value) throws MSalesException;

    /**
     * Query the given table, returning a Cursor over the result set.
     * <p>
     * @param table         The table name to compile the query against.
     * @param columns       A list of which columns to return. Passing null will
     *                      return all columns, which is discouraged to prevent
     *                      reading data from storage that isn't going to be
     *                      used.
     * @param selection     A filter declaring which rows to return, formatted
     *                      as an SQL WHERE clause (excluding the WHERE itself).
     *                      Passing null will return all rows for the given
     *                      table.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in order
     *                      that they appear in the selection. The values will
     *                      be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as
     *                      an SQL GROUP BY clause (excluding the GROUP BY
     *                      itself). Passing null will cause the rows to not be
     *                      grouped.
     * @param having        A filter declare which row groups to include in the
     *                      cursor, if row grouping is being used, formatted as
     *                      an SQL HAVING clause (excluding the HAVING itself).
     *                      Passing null will cause all row groups to be
     *                      included, and is required when row grouping is not
     *                      being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY
     *                      clause (excluding the ORDER BY itself). Passing null
     *                      will use the default sort order, which may be
     *                      unordered.
     * @param limit         Limits the number of rows returned by the query,
     *                      formatted as LIMIT clause. Passing null denotes no
     *                      LIMIT clause.
     * <p>
     * @return A object, which is positioned before the first entry. Note that
     *         Cursors are not synchronized, see the documentation for more
     *         details.
     * <p>
     * @throws MSalesException
     */
    public Object query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) throws MSalesException;

    /**
     * Query the given table, returning a Object over the result set.
     * <p>
     * @param table         The table name to compile the query against.
     * @param columns       A list of which columns to return. Passing null will
     *                      return all columns, which is discouraged to prevent
     *                      reading data from storage that isn't going to be
     *                      used.
     * @param selection     A filter declaring which rows to return, formatted
     *                      as an SQL WHERE clause (excluding the WHERE itself).
     *                      Passing null will return all rows for the given
     *                      table.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in order
     *                      that they appear in the selection. The values will
     *                      be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as
     *                      an SQL GROUP BY clause (excluding the GROUP BY
     *                      itself). Passing null will cause the rows to not be
     *                      grouped.
     * @param having        A filter declare which row groups to include in the
     *                      cursor, if row grouping is being used, formatted as
     *                      an SQL HAVING clause (excluding the HAVING itself).
     *                      Passing null will cause all row groups to be
     *                      included, and is required when row grouping is not
     *                      being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY
     *                      clause (excluding the ORDER BY itself). Passing null
     *                      will use the default sort order, which may be
     *                      unordered.
     * <p>
     * @return A object, which is positioned before the first entry. Note that
     *         Cursors are not synchronized, see the documentation for more
     *         details.
     * <p>
     * @throws MSalesException
     */
    public Object query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) throws MSalesException;
    
    public <T> List<T>  getListOption(Class<T> clazz, ParameterList parameterList);
    
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
