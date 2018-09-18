/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.services;

import java.util.LinkedHashMap;
import java.util.List;
import org.hibernate.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.dao.DataDao;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.OptionQuery;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.logex.MSalesException;

/**
 *
 * @author vtm036
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DataServiceImpl implements DataService {

    @Autowired
    private DataDao dataDao;

    @Override
    public void setBranch(int branch) {
        dataDao.setBranch(branch);
    }

    @Override
    public int insertRow(Object object) {
        if (object != null) {
            try {
                return dataDao.insertRow(object);
            } catch (Exception e) {
                throw new MSalesException(e);
            }
        }
        return MsalesConstants.ERROR.SQL_ERROR;
    }

    @Override
    public <T> List<T> getList(Class<T> classs) {
        if (dataDao != null) {
            try {
                return dataDao.getList(classs);
            } catch (MSalesException e) {
            }
        }
        return null;
    }

    @Override
    public <T> T getRowById(int id, Class<T> classs) {
        if (id > 0) {
            try {
                return dataDao.getRowById(id, classs);
            } catch (MSalesException e) {
            }
        }
        return null;
    }

    @Override
    public int updateRow(Object object) {
        if (object != null) {
            try {
                return dataDao.updateRow(object);
            } catch (MSalesException e) {
            }
        }
        return MsalesConstants.ERROR.SQL_ERROR;
    }

    @Override
    public boolean deleteRow(int id, Class<?> classs) {
        if (id > 0) {
            try {
                return dataDao.deleteRow(id, classs);
            } catch (MSalesException e) {
            }
        }
        return false;
    }

    @Override
    public <T> List<T> getListOption(Class<T> clazz, ParameterList parameterList) {
        return dataDao.getListOption(clazz, parameterList);
    }

    @Override
    public int updateSynchronize(Class<?> clazz, LinkedHashMap<String, Object> object) {
        if (dataDao != null) {
            try {
                return dataDao.updateSynchronize(clazz, object);
            } catch (MSalesException e) {
            }
        }
        return -1;
    }

    @Override
    public int deleteSynchronize(Class<?> clazz, LinkedHashMap<String, Object> object) {
        if (dataDao != null) {
            try {
                return dataDao.deleteSynchronize(clazz, object);
            } catch (MSalesException e) {
            }
        }
        return -1;
    }

    @Override
    public <T> T execSQL(String tableName, String[] field, String[] value) throws MSalesException {
        if (dataDao != null) {
            try {
                return dataDao.execSQL(tableName, field, value);
            } catch (Exception e) {
                throw new MSalesException(e.getMessage(), e.getCause());
            }
        }
        return null;
    }

    @Override
    public <T> List<T> findByNamedQuery(String namedQuery, String[] fields, String[] values) {
        if (dataDao != null) {
            return dataDao.findByNamedQuery(namedQuery, fields, values);
        }

        return null;
    }

    public int updateNamedQuery(String namedQuery, String[] fields, String[] values) {
        if (dataDao != null) {
            return updateNamedQuery(namedQuery, fields, values);
        }
        return -1;
    }

    @Override
    public int updateSync(Object object) {
        if (dataDao != null) {
            return dataDao.updateSync(object);
        }
        return -1;
    }

    @Override
    public int deleteSynch(Object object) {
        if (dataDao != null) {
            return dataDao.deleteSynch(object);
        }
        return -1;
    }

    @Override
    public int executeHQL(String hql) {
        if (dataDao != null) {
            return dataDao.executeHQL(hql);
        }
        return -1;
    }

    @Override
    public int executeHQL(String hql, List<MsalesParameter> parameters) {
        if (dataDao != null) {
            return dataDao.executeHQL(hql, parameters);
        }
        return -1;
    }

    @Override
    public <T> List<T> executeSelectHQL(Class<T> classs, String hql, boolean transform, int pageNo, int recordsInPage) {
        if (dataDao != null) {
            return dataDao.executeSelectHQL(classs, hql, transform, pageNo, recordsInPage);
        }
        return null;
    }

    @Override
    public <T> T execSQL(String sql) {
        if (dataDao != null) {
            return dataDao.execSQL(sql);
        }
        return null;
    }

    @Override
    public <T> MsalesResults<T> getListOption(Class<T> clazz, ParameterList parameterList, boolean isList) {
        return dataDao.getListOption(clazz, parameterList, isList);
    }

    @Override
    public List<Integer> insertArray(List objects) {
        return dataDao.insertArray(objects);
    }

    @Override
    public boolean updateArray(List objects) {
        return dataDao.updateArray(objects);
    }

    @Override
    public boolean multiQueryArray(List<OptionQuery> optionQuerys) {
        return dataDao.multiQueryArray(optionQuerys);
    }

    @Override
    public void insertOrUpdateArray(List objects) {
        dataDao.insertOrUpdateArray(objects);
    }

    @Override
    public int insert(Object object) {
        return dataDao.insert(object);
    }

    @Override
    public <T> List<T> executeSelectHQL(String hql, List<MsalesParameter> parameters, boolean transform, int pageNo, int recordsInPage) {
        return dataDao.executeSelectHQL(hql, parameters, transform, pageNo, recordsInPage);
    }

    @Override
    public <T> List<T> executeSelectSQL(String sql, List<MsalesParameter> parameters, boolean transform, int pageNo, int recordsInPage, String entity, Class clazz) {
        return dataDao.executeSelectSQL(sql, parameters, transform, pageNo, recordsInPage, entity, clazz);
    }

    @Override
    public Session openSession() {
        return dataDao.openSession();
    }

    @Override
    public void insertOrUpdate(Object object) {
        if (object != null) {
            try {
                dataDao.insertRow(object);
            } catch (Exception e) {
                throw new MSalesException(e);
            }
        }
    }
}
