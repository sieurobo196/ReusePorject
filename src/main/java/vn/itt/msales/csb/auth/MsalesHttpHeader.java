/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.csb.auth;

import org.springframework.http.HttpHeaders;
import vn.itt.msales.database.dbrouting.DatabaseType;

/**
 *
 * @author ChinhNQ
 */
public class MsalesHttpHeader extends HttpHeaders {

    /* The key branch of JSON contents post to server from client*/
    public static final String BRANCH = "branch";
    public static final String HEADER = "header";
    private static final long serialVersionUID = -2322612251819581714L;

    public MsalesHttpHeader() {

    }

    /**
     * get branch from header.
     * <p>
     * @return is branch number
     */
    public int getBranch() {
        String strBranch = getFirst(BRANCH);

        int branch = 0;
        try {
            branch = Integer.parseInt(strBranch);
        } catch (NumberFormatException e) {
        }

        return branch;
    }

    /**
     * get access token
     * <p>
     * @return is string access token
     */
    public String getAuthorization() {
        return getFirst(AUTHORIZATION);
    }

    /**
     * Check branch request from client.
     * <p>
     */
    public boolean checkBranch() {
        for (DatabaseType database : DatabaseType.values()) {
            if (getBranch() == database.getBranch()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean checkBranch(int branch) {
        for (DatabaseType database : DatabaseType.values()) {
            if (branch == database.getBranch()) {
                return true;
            }
        }
        return false;
    }
}
