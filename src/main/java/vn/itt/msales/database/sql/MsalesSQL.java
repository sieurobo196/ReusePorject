/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.database.sql;

/**
 *
 * @author ChinhNQ
 */
public class MsalesSQL {

    public static final String GET_MAX_USER_ROLE_CHANNEL = "select * from user_role_channel where USER_ID = %s order by user_role_id";
}
