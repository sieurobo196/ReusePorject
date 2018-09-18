/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.dao;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.database.dbrouting.DatabaseContextHolder;
import vn.itt.msales.database.dbrouting.DatabaseType;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.OptionQuery;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.logex.MSalesException;
import vn.itt.msales.logex.MSalesLogger;

/**
 *
 * @author ChinhNQ
 * @version
 * @since 5 Jun 2015 13:40:17 msales_saas#vn.itt.msales.dao.DataDaoImpl.java
 *
 */
@Repository("msalesDao")
public class DataDaoImpl implements DataDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void setBranch(int branch) {
        for (DatabaseType database : DatabaseType.values()) {
            if (branch == database.getBranch()) {
                DatabaseContextHolder.setCustomerType(database);
                break;
            }
        }
    }

    @Override
    @Transactional
    public int insert(Object object) throws MSalesException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Serializable id = session.save(object);
            transaction.commit();
            return (Integer) id;
        } catch (Exception e) {
            transaction.rollback();
            throw new MSalesException(e);
        } finally {
            close(session);
        }
    }

    @Override
    @Transactional
    public int insertRow(Object object) throws MSalesException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();

            transaction = session.beginTransaction();

            Date now = new Date();

            Field field;
            field = object.getClass().getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(object, now);
            field.setAccessible(false);

            field = object.getClass().getDeclaredField("updatedUser");
            field.setAccessible(true);
            field.set(object, 0);
            field.setAccessible(false);

            field = object.getClass().getDeclaredField("deletedUser");
            field.setAccessible(true);
            field.set(object, 0);
            field.setAccessible(false);

            Serializable id = session.save(object);
            transaction.commit();
            return (Integer) id;
        } catch (HibernateException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new MSalesException(e.getCause());
        } finally {
            close(session);
        }
    }

    @Override
    @Transactional
    public <T> List<T> getList(Class<T> classs) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            @SuppressWarnings("unchecked")
            List<T> objectList = session.createCriteria(classs).add(Restrictions.eq("deletedUser", 0)).list();
            return objectList;
        } catch (Exception e) {
            throw new MSalesException(MsalesConstants.ERROR.SQL_FALTA, e.getCause());
        } finally {
            close(session);
        }
    }

    @Override
    @Transactional
    public <T> T getRowById(int id, Class<T> classs) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            List<T> list = session.createCriteria(classs).add(Restrictions.eq("deletedUser", 0)).add(Restrictions.eq("id", id)).list();
            if (!list.isEmpty()) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new MSalesException(MsalesConstants.ERROR.SQL_FALTA, e.getCause());
        } finally {
            close(session);
        }
    }

    @Override
    @Transactional
    public int updateRow(Object object) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            session.update(object);
            tx.commit();
            Serializable id = session.getIdentifier(object);
            return (Integer) id;
        } catch (HibernateException | SecurityException | IllegalArgumentException e) {
            throw new MSalesException(e.getCause());
        } finally {
            close(session);
        }
    }

    @Override
    @Transactional
    public boolean deleteRow(int id, Class<?> classs) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            Object object = session.get(classs, id);
            session.delete(object);
            tx.commit();
            return true;
        } catch (Exception e) {
            throw new MSalesException(MsalesConstants.ERROR.SQL_FALTA, e.getCause());
        } finally {
            close(session);
        }
    }

    @Override
    @Transactional
    public <T> T execSQL(String tableName, String[] fields, String[] values) throws MSalesException {
        Session session = null;

        try {
            session = sessionFactory.openSession();
            String hql = createSQL(tableName, fields);
            Query query = session.createQuery(hql);
            for (int i = 0; i < fields.length; i++) {
                query.setParameter(fields[i], values[i]);
            }
            List<T> result = query.list();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        } catch (Exception e) {
            throw new MSalesException(MsalesConstants.ERROR.SQL_FALTA, e.getCause());
        } finally {
            close(session);
        }

        return null;
    }

    private String createSQL(String tableName, String[] fields) {
        String hql = "FROM " + tableName + " WHERE ";
        for (String field : fields) {
            hql += field + "=:" + field + " ";
        }
        return hql;
    }

    @Override
    @Transactional
    public Object query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) throws MSalesException {
        return null;
    }

    @Override
    @Transactional
    public Object query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) throws MSalesException {
        return null;
    }

    public void close(Session session) {
        if (session != null) {
            if (session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * author KhaiCh get List by multi conditions
     *
     * @param clazz is name of class
     * @param <T> is clazz
     * @param parameterList is a List of Parameter
     * @return
     */
    @Override
    @Transactional
    public <T> List<T> getListOption(Class<T> clazz, ParameterList parameterList) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            Criteria criteria = session.createCriteria(clazz).add(Restrictions.eq("deletedUser", 0));
            //fix sau
            if (parameterList.getCriterions() != null) {
                criteria.add(parameterList.getCriterions());
            }

            if (parameterList.getPageNo() > 0 && parameterList.getRecordsInPage() > 0) {
                criteria.setFirstResult((parameterList.getPageNo() - 1) * parameterList.getRecordsInPage());
                criteria.setMaxResults(parameterList.getRecordsInPage());
            }
            if (!parameterList.getListOrder().isEmpty()) {
                for (Order order : parameterList.getListOrder()) {
                    criteria.addOrder(order);
                }
            }

            List<T> objectList = criteria.list();
            return objectList;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new MSalesException(DataDaoImpl.class, e.getMessage(),
                    MsalesConstants.ERROR.SQL_FALTA);
        } finally {
            close(session);
        }
    }

    @Override
    @Transactional
    public <T> MsalesResults<T> getListOption(Class<T> clazz, ParameterList parameterList, boolean isList) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            Criteria criteria = session.createCriteria(clazz).add(Restrictions.eq("deletedUser", 0));
            //fix sau
            if (parameterList.getCriterions() != null) {
                criteria.add(parameterList.getCriterions());
            }

            Criteria criteria1 = session.createCriteria(clazz);
            //fix sau
            if (parameterList.getCriterions() != null) {
                criteria1.add(parameterList.getCriterions());
            }

            criteria1.setProjection(Projections.rowCount());
            long count = (Long) criteria1.uniqueResult();

            if (parameterList.getPageNo() > 0 && parameterList.getRecordsInPage() > 0) {
                criteria.setFirstResult((parameterList.getPageNo() - 1) * parameterList.getRecordsInPage());
                criteria.setMaxResults(parameterList.getRecordsInPage());
            }
            if (!parameterList.getListOrder().isEmpty()) {
                for (Order order : parameterList.getListOrder()) {
                    criteria.addOrder(order);
                }
            }
            MsalesResults<T> msalesResults = new MsalesResults();
            msalesResults.setCount(count);
            msalesResults.setContentList(criteria.list());

            return msalesResults;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new MSalesException(DataDaoImpl.class, e.getMessage(),
                    MsalesConstants.ERROR.SQL_FALTA);
        } finally {
            close(session);
        }
    }

    /**
     * author KhaiCH Update only some field
     *
     * @param clazz is Class updated
     * @param object
     * @return
     */
    @Override
    @SuppressWarnings("UseSpecificCatch")
    @Transactional
    public int updateSynchronize(Class<?> clazz, LinkedHashMap<String, Object> object) {
        int id;
        try {
            id = Integer.parseInt(object.get("id").toString());
        } catch (Exception ex) {
            id = -1;
            MSalesLogger.error(ex.getMessage());
        }
        if (id > 0) {
            Object item = getRowById(id, clazz);
            try {
                if (item == null) {
                    return -1;
                }

                Field updatedAtField = clazz.getDeclaredField("updatedAt");
                updatedAtField.setAccessible(true);
                updatedAtField.set(item, new Date());
                updatedAtField.setAccessible(false);

                Field updatedUserField = clazz.getDeclaredField("updatedUser");
                updatedUserField.setAccessible(true);
                updatedUserField.set(item, object.get("updatedUser"));
                updatedUserField.setAccessible(false);

                for (Map.Entry<String, Object> entry : object.entrySet()) {
                    Field field = clazz.getDeclaredField(entry.getKey());
                    field.setAccessible(true);

                    if (!field.getName().equals("deletedAt")
                            && !field.getName().equals("createdAt")
                            && !field.getName().equals("updatedAt")
                            && !field.getName().equals("deletedUser")
                            && !field.getName().equals("updatedUser")
                            && !field.getName().equals("createdUser")) {
                        if (field.getType().getName().toLowerCase().equals("java.util.date")) {
                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                            Date d;
                            try {
                                d = sdf1.parse(entry.getValue().toString());
                            } catch (Exception e) {
                                d = sdf2.parse(entry.getValue().toString());
                            }
                            field.set(item, d);
                        } else if (field.getType().getName().toLowerCase().equals("java.math.bigdecimal")) {
                            BigDecimal bd = BigDecimal.valueOf(new Double(entry.getValue().toString()));
                            field.set(item, bd);
                        } else {
                            field.set(item, entry.getValue());
                        }
                    }

                    field.setAccessible(false);
                }
            } catch (Exception ex) {
                MSalesLogger.error(ex.getMessage());
                return -1;
            }

            id = updateRow(item);
        }

        return id;
    }

    /**
     * author KhaiCH Update only some field
     *
     * @param clazz is Class updated
     * @param object
     * @return
     */
    @Override
    @SuppressWarnings("UseSpecificCatch")
    @Transactional
    public int updateSync(Object object) {

        Integer id = 0;
        try {
            Field fieldStatic = object.getClass().getDeclaredField("id");
            fieldStatic.setAccessible(true);
            id = (Integer) fieldStatic.get(object);
            fieldStatic.setAccessible(false);

            Class clazz = object.getClass();
            Object item = getRowById(id, object.getClass());

            if (item == null) {
                return -2;//null
            }

            fieldStatic = clazz.getDeclaredField("updatedAt");
            fieldStatic.setAccessible(true);
            fieldStatic.set(object, new Date());
            fieldStatic.setAccessible(false);

            fieldStatic = clazz.getDeclaredField("deletedAt");
            fieldStatic.setAccessible(true);
            fieldStatic.set(object, fieldStatic.get(item));
            fieldStatic.setAccessible(false);

            fieldStatic = clazz.getDeclaredField("createdAt");
            fieldStatic.setAccessible(true);
            fieldStatic.set(object, fieldStatic.get(item));
            fieldStatic.setAccessible(false);

            fieldStatic = clazz.getDeclaredField("createdUser");
            fieldStatic.setAccessible(true);
            fieldStatic.set(object, fieldStatic.get(item));
            fieldStatic.setAccessible(false);

            fieldStatic = clazz.getDeclaredField("deletedUser");
            fieldStatic.setAccessible(true);
            fieldStatic.set(object, fieldStatic.get(item));
            fieldStatic.setAccessible(false);

            for (Field field : object.getClass().getDeclaredFields()) {
                for (Annotation annotation : field.getAnnotations()) {
                    if (annotation.annotationType().getName().equals("javax.persistence.JoinColumn")
                            || annotation.annotationType().getName().equals("javax.persistence.Column")) {
                        for (Method method : annotation.annotationType().getDeclaredMethods()) {
                            if (method.getName().equals("nullable")) {
                                System.out.print(field.getName() + " ");
                                Object value = method.invoke(annotation, (Object[]) null);
                                if (value.toString().equals("true")) {
                                    field.setAccessible(true);
                                    if (field.get(object) == null) {
                                        //set value
                                        field.set(object, field.get(item));
                                    }
                                    field.setAccessible(false);
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            id = updateRow(object);

        } catch (Exception ex) {
            throw new MSalesException(ex);
        }

        return id;
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    @Transactional
    public int deleteSynch(Object object) {

        Integer id = 0;
        try {
            Field fieldStatic = object.getClass().getDeclaredField("id");
            fieldStatic.setAccessible(true);
            id = (Integer) fieldStatic.get(object);
            fieldStatic.setAccessible(false);

            Class clazz = object.getClass();
            Object item = null;
            if (id != null) {
                item = getRowById(id, object.getClass());
            }

            if (item == null) {
                return -2;//null
            }

            fieldStatic = clazz.getDeclaredField("deletedAt");
            fieldStatic.setAccessible(true);
            fieldStatic.set(item, new Date());
            fieldStatic.setAccessible(false);

            fieldStatic = clazz.getDeclaredField("deletedUser");
            fieldStatic.setAccessible(true);
            fieldStatic.set(item, fieldStatic.get(object));
            fieldStatic.setAccessible(false);

            id = updateRow(item);

        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            throw new MSalesException(ex);
        }

        return id;
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    @Transactional
    public int deleteSynchronize(Class<?> clazz, LinkedHashMap<String, Object> object) {
        int id;
        try {
            id = Integer.parseInt(object.get("id").toString());
        } catch (Exception ex) {
            MSalesLogger.error(ex.getMessage());
            id = -1;
        }
        if (id > 0) {
            Object item = getRowById(id, clazz);
            if (item == null) {
                return -1;
            }
            try {
                Field deleledAtField = clazz.getDeclaredField("deletedAt");
                Field deleledUserField = clazz.getDeclaredField("deletedUser");

                deleledAtField.setAccessible(true);
                deleledAtField.set(item, new Date());
                deleledAtField.setAccessible(false);

                deleledUserField.setAccessible(true);
                deleledUserField.set(item, object.get("deletedUser"));
                deleledAtField.setAccessible(false);

            } catch (Exception ex) {
                MSalesLogger.error(ex.getMessage());
                return -1;
            }
            id = updateRow(item);
        }
        return id;
    }

    @Override
    @Transactional
    public <T> List<T> findByNamedQuery(String namedQuery, String[] fields, String[] values) {
        List<T> listObj = new ArrayList<>();
        Session session = sessionFactory.openSession();
        try {
            Query query = session.getNamedQuery(namedQuery);
            for (int i = 0; i < fields.length; i++) {
                query.setParameter(fields[i], values[i]);
            }

            listObj = query.list();
        } catch (Exception e) {
            System.err.println(">>findbynamedquery: " + e.getMessage());
        } finally {
            close(session);
        }
        return listObj;
    }

    @Override
    @Transactional
    public int updateNamedQuery(String namedQuery, String[] fields, String[] values) {
        Session session = sessionFactory.openSession();
        Query query = session.getNamedQuery(namedQuery);
        try {
            for (int i = 0; i < fields.length; i++) {
                query.setParameter(fields[i], values[i]);
            }
        } catch (Exception e) {
        } finally {
            close(session);
        }
        return query.executeUpdate();
    }

    @Override
    @Transactional
    public int executeHQL(String hql) {

        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            int ret = session.createQuery(hql).executeUpdate();
            transaction.commit();
            return ret;

        } catch (Exception e) {
            transaction.rollback();
            close(session);
            throw new MSalesException(e);
        } finally {
            close(session);
        }
    }

    @Override
    public int executeHQL(String hql, List<MsalesParameter> parameters) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery(hql);
            for (MsalesParameter parameter : parameters) {
                if (parameter.getType() == 1) {
                    //Collection
                    query.setParameterList(parameter.getName(), (Collection) parameter.getValue());
                } else if (parameter.getType() == 2) {
                    //Date
                    query.setDate(parameter.getName(), (Date) parameter.getValue());
                } else if (parameter.getType() == 3) {
                    //Timestamp
                    query.setTimestamp(parameter.getName(), (Date) parameter.getValue());
                } else {
                    query.setParameter(parameter.getName(), parameter.getValue());
                }
            }
            int ret = query.executeUpdate();
            transaction.commit();
            return ret;

        } catch (Exception e) {
            transaction.rollback();
            close(session);
            throw new MSalesException(e);
        } finally {
            close(session);
        }
    }

    @Override
    @Transactional
    public <T> List<T> executeSelectHQL(Class<T> classs, String hql, boolean transform, int pageNo, int recordsInPage) {
        Session session = null;
        List<T> listObj = null;
        try {
            session = sessionFactory.openSession();
            Query query = session.createQuery(hql);
            if (pageNo > 0 && recordsInPage > 0) {
                query.setFirstResult((pageNo - 1) * recordsInPage);
                query.setMaxResults(recordsInPage);
            }

            if (transform) {
                listObj = query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP).list();
            } else {
                listObj = query.list();
            }

        } catch (HibernateException e) {
            throw new MSalesException(e);
        } finally {
            close(session);
        }
        return listObj;
    }

    @Override
    @Transactional
    public <T> List<T> executeSelectHQL(String hql, List<MsalesParameter> parameters, boolean transform, int pageNo, int recordsInPage) {
        Session session = null;
        List<T> listObj = new ArrayList<>();
        try {
            session = sessionFactory.openSession();
            Query query = session.createQuery(hql);
            if (pageNo > 0 && recordsInPage > 0) {
                query.setFirstResult((pageNo - 1) * recordsInPage);
                query.setMaxResults(recordsInPage);
            }

            for (MsalesParameter parameter : parameters) {
                if (parameter.getType() == 1) {
                    //Collection
                    query.setParameterList(parameter.getName(), (Collection) parameter.getValue());
                } else if (parameter.getType() == 5) {
                    //object[]
                    query.setParameterList(parameter.getName(), (Object[]) parameter.getValue());
                } else if (parameter.getType() == 2) {
                    //Date
                    query.setDate(parameter.getName(), (Date) parameter.getValue());
                } else if (parameter.getType() == 3) {
                    //Timestamp
                    query.setTimestamp(parameter.getName(), (Date) parameter.getValue());
                } else {
                    query.setParameter(parameter.getName(), parameter.getValue());
                }
            }

            if (transform) {
                listObj = query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP).list();
            } else {
                listObj = query.list();
            }

        } catch (HibernateException e) {
            throw new MSalesException(e);
        } finally {
            close(session);
        }
        return listObj;
    }

    @Override
    @Transactional
    public <T> List<T> executeSelectSQL(String sql, List<MsalesParameter> parameters, boolean transform, int pageNo, int recordsInPage, String entity, Class clazz) {
        Session session = null;
        List<T> listObj = new ArrayList<>();
        try {
            session = sessionFactory.openSession();
            SQLQuery query = session.createSQLQuery(sql);
            if (pageNo > 0 && recordsInPage > 0) {
                query.setFirstResult((pageNo - 1) * recordsInPage);
                query.setMaxResults(recordsInPage);
            }

            for (MsalesParameter parameter : parameters) {
                if (parameter.getType() == 1) {
                    //Collection
                    query.setParameterList(parameter.getName(), (Collection) parameter.getValue());
                } else if (parameter.getType() == 2) {
                    //Date
                    query.setDate(parameter.getName(), (Date) parameter.getValue());
                } else if (parameter.getType() == 3) {
                    //Timestamp
                    query.setTimestamp(parameter.getName(), (Date) parameter.getValue());
                } else {
                    query.setParameter(parameter.getName(), parameter.getValue());
                }
            }

            if (entity != null && !entity.isEmpty() && clazz != null) {
                query.addEntity(entity, clazz);
            }
            if (transform) {
                listObj = query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP).list();
            } else {
                listObj = query.list();
            }

        } catch (HibernateException e) {
            throw new MSalesException(e);
        } finally {
            close(session);
        }
        return listObj;
    }

    @Override
    @Transactional
    public <T> T execSQL(String sql) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Query query = session.createSQLQuery(sql);
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            return (T) query.list();
        } catch (HibernateException e) {
        } finally {
            close(session);
        }
        return null;
    }

    @Override
    @Transactional
    public List<Integer> insertArray(List objects) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();

            transaction = session.beginTransaction();
            List<Integer> list = new ArrayList<>();

            Date now = new Date();
            for (Object object : objects) {
                Field field;
                field = object.getClass().getDeclaredField("createdAt");
                field.setAccessible(true);
                field.set(object, now);
                field.setAccessible(false);

                field = object.getClass().getDeclaredField("updatedAt");
                field.setAccessible(true);
                field.set(object, null);
                field.setAccessible(false);

                field = object.getClass().getDeclaredField("deletedAt");
                field.setAccessible(true);
                field.set(object, null);
                field.setAccessible(false);

                field = object.getClass().getDeclaredField("updatedUser");
                field.setAccessible(true);
                field.set(object, 0);
                field.setAccessible(false);

                field = object.getClass().getDeclaredField("deletedUser");
                field.setAccessible(true);
                field.set(object, 0);
                field.setAccessible(false);

                Serializable id = session.save(object);
                list.add(Integer.parseInt(id.toString()));
            }

            transaction.commit();
            return list;

        } catch (ConstraintViolationException | HibernateException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            transaction.rollback();
            throw new MSalesException(e);
        } finally {
            close(session);
        }
    }

    @Override
    @Transactional
    public void insertOrUpdateArray(List objects) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();

            transaction = session.beginTransaction();
            for (Object object : objects) {
                session.saveOrUpdate(object);
            }
            transaction.commit();

        } catch (ConstraintViolationException | HibernateException | SecurityException | IllegalArgumentException e) {
            transaction.rollback();
            throw new MSalesException(e);
        } finally {
            close(session);
        }
    }

    @Override
    @Transactional
    public boolean updateArray(List objects) {
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            for (Object object : objects) {
                session.update(object);
            }
            tx.commit();
            return true;
        } catch (HibernateException | SecurityException | IllegalArgumentException e) {
            tx.rollback();
            throw new MSalesException(e.getCause());
        } finally {
            close(session);
        }
    }

    @Override
    @Transactional
    public boolean multiQueryArray(List<OptionQuery> optionQuerys) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            for (OptionQuery optionQuery : optionQuerys) {
                if (optionQuery.getType() == 1)//insert
                {
                    Date now = new Date();
                    for (Object object : optionQuery.getObjects()) {
                        Field field;
                        field = object.getClass().getDeclaredField("createdAt");
                        field.setAccessible(true);
                        field.set(object, now);
                        field.setAccessible(false);

                        field = object.getClass().getDeclaredField("updatedAt");
                        field.setAccessible(true);
                        field.set(object, null);
                        field.setAccessible(false);

                        field = object.getClass().getDeclaredField("deletedAt");
                        field.setAccessible(true);
                        field.set(object, null);
                        field.setAccessible(false);

                        field = object.getClass().getDeclaredField("updatedUser");
                        field.setAccessible(true);
                        field.set(object, 0);
                        field.setAccessible(false);

                        field = object.getClass().getDeclaredField("deletedUser");
                        field.setAccessible(true);
                        field.set(object, 0);
                        field.setAccessible(false);

                        Serializable id = session.save(object);
                    }
                } else if (optionQuery.getType() == 2)//update
                {
                    for (Object object : optionQuery.getObjects()) {
                        session.update(object);
                    }
                }
            }
            transaction.commit();
            return true;

        } catch (ConstraintViolationException | HibernateException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            transaction.rollback();
            throw new MSalesException(e);
        } finally {
            close(session);
        }
    }

    @Override
    public Session openSession() {
        Session session = sessionFactory.openSession();
        return session;
    }

    @Override
    @Transactional
    public void insertOrUpdate(Object object) throws MSalesException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.saveOrUpdate(object);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new MSalesException(e);
        } finally {
            close(session);
        }
    }
}
