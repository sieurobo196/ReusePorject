/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.database.dbrouting.audit;

import java.io.Serializable;
import java.util.Date;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

/**
 *
 * @author vtm036
 */
public class MsalesAuditInterceptor extends EmptyInterceptor {

    private int updates;
    private int creates;
    private int loads;

    public MsalesAuditInterceptor() {
        super();

    }

    public MsalesAuditInterceptor(int updates, int creates) {
        super();
        this.updates = updates;
        this.creates = creates;
    }

    public void onDelete(Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {
        // do nothing
    }

    public boolean onFlushDirty(Object entity,
            Serializable id,
            Object[] currentState,
            Object[] previousState,
            String[] propertyNames,
            Type[] types) {

        if (entity instanceof Object) {//Auditable
            updates++;
            for (int i = 0; i < propertyNames.length; i++) {
                if ("lastUpdateTimestamp".equals(propertyNames[i])) {
                    currentState[i] = new Date();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onLoad(Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {
        if (entity instanceof Object) {//Auditable
            loads++;
        }
        return false;
    }

    public boolean onSave(Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {

        if (entity instanceof Object) {//Auditable
            creates++;
            for (int i = 0; i < propertyNames.length; i++) {
                if ("createTimestamp".equals(propertyNames[i])) {
                    state[i] = new Date();
                    return true;
                }
            }
        }
        return false;
    }

    public void afterTransactionCompletion(Transaction tx) {
        if (tx.wasCommitted()) {
            //System.err.println("Creations: " + creates + ", Updates: " + updates, "Loads: " + loads);
        }
        updates = 0;
        creates = 0;
        loads = 0;
    }
}
