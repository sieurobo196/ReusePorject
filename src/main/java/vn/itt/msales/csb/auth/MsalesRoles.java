/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.csb.auth;

import vn.itt.msales.common.MsalesConstants;

/**
 * @author ChinhNQ
 * <p>
 * Check roles fro url. In msale system have 4 roles is company's role, channel
 * role, assistant's role, salesman's role.
 * <p>
 * The Company's role have max role level (level 1), channel role have role
 * level 2, assistant's role have role level 3, saleman's role have role level 4
 * <p>
 * If any url belong to company's role then this url company user access only.
 * <p>
 * Sequence roles: ROLE_COMPANY(1) > ROLE_CHANNEL(2) > ROLE_ASSISTANT(3) >
 * ROLE_SALESMAN(4)
 */
public class MsalesRoles {

    /**
     * Company role
     */
    public static final String COMPANY_NAME = "ROLE_COMPANY";
    public static final int ROLE_COMPANY_ID = 1;
    /**
     * channel role
     */
    public static final String CHANNEL_NAME = "ROLE_CHANNEL";
    public static final int ROLE_CHANNEL_ID = 2;
    /**
     * assistant role
     */
    public static final String ASSISTANT_NAME = "ASSISTAT_ROLES";
    public static final int ROLE_ASSISTANT_ID = 3;
    /**
     * salesman role
     */
    public static final String SALESMAN_NAME = "ROLE_SALESMAN";
    public static final int ROLE_SALESMAN_ID = 4;

    /**
     * List roles
     */
    public enum UserRoles {

        USER_ROLE_COMPANY(Roles.ROLE_COMPANY, COMPANY_ACCESS_DENIED),
        USER_ROLE_CHANNEL(Roles.ROLE_COMPANY, CHANNEL_ACCESS_DENIED),
        USER_ROLE_ASSISTANT(Roles.ROLE_COMPANY, COMPANY_ACCESS_DENIED),
        USER_ROLE_SALESMAN(Roles.ROLE_COMPANY, SALESMAN_ACCESS_DENIED);

        private Roles roles;
        private String[] urls;

        private UserRoles(Roles role, String[] urls) {
            this.roles = role;
            this.urls = urls;
        }

        public Roles getRoles() {
            return roles;
        }

        public void setRoles(Roles roles) {
            this.roles = roles;
        }

        public String[] getUrls() {
            return urls;
        }

        public void setUrls(String[] urls) {
            this.urls = urls;
        }

    }

    /**
     * List all roles of msale system
     * <p>
     * ROLE_COMPANY > ROLE_CHANNEL > ROLE_ASSISTANT > ROLE_SALESMAN
     */
    public enum Roles {

        // Max roles: level 1;
        ROLE_COMPANY(ROLE_COMPANY_ID, COMPANY_NAME),
        // > : level 2;
        ROLE_CHANNEL(ROLE_CHANNEL_ID, CHANNEL_NAME),
        // > : level 3;
        ROLE_ASSISTANT(ROLE_ASSISTANT_ID, ASSISTANT_NAME),
        // > : level 4;
        ROLE_SALESMAN(ROLE_SALESMAN_ID, ASSISTANT_NAME);

        private int id;
        private String roleName;

        private Roles(int id, String name) {
            this.id = id;
            this.roleName = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }
    }

    /**
     * Company roles. If url have in the Company roles, all roles that have
     * lower rights will not be accessible
     */
    public static final String[] COMPANY_ACCESS_DENIED = new String[]{ //FIXME: remove this commnets
 //        MsalesConstants.MODULE.COMPANY.ACTION_CREATE_COMPANY,
    //        MsalesConstants.MODULE.COMPANY.ACTION_DELETE_COMPANY,
    //        MsalesConstants.MODULE.COMPANY.ACTION_GET_LIST_COMPANY,
    //        MsalesConstants.MODULE.USERROLE.ACTION_CREATE_USERROLE,
    //        MsalesConstants.MODULE.USERROLE.ACTION_UPDATE_USERROLE,
    //        MsalesConstants.MODULE.USERROLE.ACTION_DELETE_USERROLE,
    //        MsalesConstants.MODULE.SALESSTOCK.ACTION_CREATE_SALES_STOCK,
    //        MsalesConstants.MODULE.SALESSTOCK.ACTION_UPDATE_SALES_STOCK,
    //        MsalesConstants.MODULE.SALESSTOCK.ACTION_DELETE_SALES_STOCK
    };

    /**
     * Channel roles If url have in the Channel roles, all roles that have lower
     * rights will not be accessible
     */
    public static final String[] CHANNEL_ACCESS_DENIED = new String[]{
        MsalesConstants.MODULE.COMPANYCONFIG.ACTION_CREATE_COMPANY_CONFIG,
        MsalesConstants.MODULE.COMPANYCONFIG.ACTION_UPDATE_COMPANY_CONFIG,
        MsalesConstants.MODULE.VERSION.ACTION_CREATE_VERSION,
        MsalesConstants.MODULE.VERSION.ACTION_UPDATE_VERSION,
        MsalesConstants.MODULE.COMPANY.ACTION_CREATE_COMPANY,
        MsalesConstants.MODULE.COMPANY.ACTION_DELETE_COMPANY,
        MsalesConstants.MODULE.COMPANY.ACTION_GET_LIST_COMPANY,
        MsalesConstants.MODULE.CHANNELTYPE.ACTION_CREATE_CHANNEL_TYPE,
        MsalesConstants.MODULE.CHANNELTYPE.ACTION_UPDATE_CHANNEL_TYPE,
        MsalesConstants.MODULE.CHANNELTYPE.ACTION_DELETE_CHANNEL_TYPE,
        MsalesConstants.MODULE.LOCATION.ACTION_CREATE_LOCATION,
        MsalesConstants.MODULE.LOCATION.ACTION_UPDATE_LOCATION,
        MsalesConstants.MODULE.LOCATION.ACTION_DELETE_LOCATION,
        MsalesConstants.MODULE.LOCATION.ACTION_DELETE_LOCATION,
        MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_CREATE_GOODS_CATEGORY,
        MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_UPDATE_GOODS_CATEGORY,
        MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_DELETE_GOODS_CATEGORY,
        MsalesConstants.MODULE.SALESSTOCK.ACTION_CREATE_SALES_STOCK,
        MsalesConstants.MODULE.SALESSTOCK.ACTION_DELETE_SALES_STOCK,
        MsalesConstants.MODULE.SALESSTOCK.ACTION_UPDATE_SALES_STOCK
    };

    /**
     * Assistant roles If url have in the Assistant roles, all roles that have
     * lower rights will not be accessible
     */
    public static final String[] ASSISTAT_ACCESS_DENIED = new String[]{
        MsalesConstants.MODULE.COMPANYCONFIG.ACTION_CREATE_COMPANY_CONFIG,
        MsalesConstants.MODULE.COMPANYCONFIG.ACTION_UPDATE_COMPANY_CONFIG,
        MsalesConstants.MODULE.VERSION.ACTION_CREATE_VERSION,
        MsalesConstants.MODULE.VERSION.ACTION_UPDATE_VERSION,
        MsalesConstants.MODULE.STATUSTYPE.ACTION_CREATE_STATUSTYPE,
        MsalesConstants.MODULE.STATUSTYPE.ACTION_UPDATE_STATUSTYPE,
        MsalesConstants.MODULE.STATUSTYPE.ACTION_DELETE_STATUSTYPE,
        MsalesConstants.MODULE.STATUSTYPE.ACTION_GET_LIST_STATUSTYPE,
        MsalesConstants.MODULE.STATUS.ACTION_CREATE_STATUS,
        MsalesConstants.MODULE.STATUS.ACTION_UPDATE_STATUS,
        MsalesConstants.MODULE.STATUS.ACTION_DELETE_STATUS,
        MsalesConstants.MODULE.STATUS.ACTION_GET_LIST_STATUS,
        MsalesConstants.MODULE.TABLENAME.ACTION_CREATE_TABLENAME,
        MsalesConstants.MODULE.TABLENAME.ACTION_UPDATE_TABLENAME,
        MsalesConstants.MODULE.TABLENAME.ACTION_DELETE_TABLENAME,
        MsalesConstants.MODULE.TABLENAME.ACTION_GET_LIST_TABLENAME,
        MsalesConstants.MODULE.PROPERTY.ACTION_CREATE_PROPERTY,
        MsalesConstants.MODULE.PROPERTY.ACTION_UPDATE_PROPERTY,
        MsalesConstants.MODULE.PROPERTY.ACTION_DELETE_PROPERTY,
        MsalesConstants.MODULE.PROPERTY.ACTION_GET_LIST_PROPERTY,
        MsalesConstants.MODULE.PROPERTYVALUE.ACTION_CREATE_PROPERTYVALUE,
        MsalesConstants.MODULE.PROPERTYVALUE.ACTION_UPDATE_PROPERTYVALUE,
        MsalesConstants.MODULE.PROPERTYVALUE.ACTION_DELETE_PROPERTYVALUE,
        MsalesConstants.MODULE.PROPERTYVALUE.ACTION_GET_LIST_PROPERTYVALUE,
        MsalesConstants.MODULE.COMPANY.ACTION_CREATE_COMPANY,
        MsalesConstants.MODULE.COMPANY.ACTION_UPDATE_COMPANY,
        MsalesConstants.MODULE.COMPANY.ACTION_DELETE_COMPANY,
        MsalesConstants.MODULE.COMPANY.ACTION_GET_LIST_COMPANY,
        MsalesConstants.MODULE.CHANNELTYPE.ACTION_CREATE_CHANNEL_TYPE,
        MsalesConstants.MODULE.CHANNELTYPE.ACTION_UPDATE_CHANNEL_TYPE,
        MsalesConstants.MODULE.CHANNELTYPE.ACTION_DELETE_CHANNEL_TYPE,
        MsalesConstants.MODULE.CHANNEL.ACTION_CREATE_CHANNEL,
        MsalesConstants.MODULE.CHANNEL.ACTION_UPDATE_CHANNEL,
        MsalesConstants.MODULE.CHANNEL.ACTION_DELETE_CHANNEL,
        MsalesConstants.MODULE.CHANNEL.ACTION_GET_LIST_CHANNEL_BY_CHANNEL_TYPE_ID,
        MsalesConstants.MODULE.CHANNEL.ACTION_GET_LIST_CHANNEL_BY_COMPANY_ID,
        MsalesConstants.MODULE.CHANNEL.ACTION_GET_LIST_CHANNEL_BY_PARENT_ID,
        MsalesConstants.MODULE.LOCATION.ACTION_CREATE_LOCATION,
        MsalesConstants.MODULE.LOCATION.ACTION_UPDATE_LOCATION,
        MsalesConstants.MODULE.LOCATION.ACTION_DELETE_LOCATION,
        MsalesConstants.MODULE.CHANNELLOCATION.ACTION_CREATE_CHANNEL_LOCATION,
        MsalesConstants.MODULE.CHANNELLOCATION.ACTION_UPDATE_CHANNEL_LOCATION,
        MsalesConstants.MODULE.CHANNELLOCATION.ACTION_DELETE_CHANNEL_LOCATION,
        MsalesConstants.MODULE.USER_ROLE_CHANNEL.ACTION_CREATE_USER_ROLE_CHANNEL,
        MsalesConstants.MODULE.USER_ROLE_CHANNEL.ACTION_UPDATE_USER_ROLE_CHANNEL,
        MsalesConstants.MODULE.USER_ROLE_CHANNEL.ACTION_DELETE_USER_ROLE_CHANNEL,
        MsalesConstants.MODULE.WORKFLOW_TYPE.ACTION_CREATE_WORKFLOW_TYPE,
        MsalesConstants.MODULE.WORKFLOW_TYPE.ACTION_UPDATE_WORKFLOW_TYPE,
        MsalesConstants.MODULE.WORKFLOW_TYPE.ACTION_DELETE_WORKFLOW_TYPE,
        MsalesConstants.MODULE.WORKFLOW.ACTION_CREATE_WORKFLOW,
        MsalesConstants.MODULE.WORKFLOW.ACTION_UPDATE_WORKFLOW,
        MsalesConstants.MODULE.WORKFLOW.ACTION_DELETE_WORKFLOW,
        MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_CREATE_GOODS_CATEGORY,
        MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_UPDATE_GOODS_CATEGORY,
        MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_DELETE_GOODS_CATEGORY,
        MsalesConstants.MODULE.GOODS.ACTION_CREATE_GOODS,
        MsalesConstants.MODULE.GOODS.ACTION_UPDATE_GOODS,
        MsalesConstants.MODULE.GOODS.ACTION_DELETE_GOODS,
        MsalesConstants.MODULE.UNIT.ACTION_CREATE_UNIT,
        MsalesConstants.MODULE.UNIT.ACTION_UPDATE_UNIT,
        MsalesConstants.MODULE.UNIT.ACTION_DELETE_UNIT,
        MsalesConstants.MODULE.SALESSTOCK.ACTION_CREATE_SALES_STOCK,
        MsalesConstants.MODULE.SALESSTOCK.ACTION_UPDATE_SALES_STOCK,
        MsalesConstants.MODULE.SALESSTOCK.ACTION_DELETE_SALES_STOCK
    };

    /**
     * SalesMan roles If url have in the SalesMan roles, all roles that have
     * lower rights will not be accessible
     */
    public static final String[] SALESMAN_ACCESS_DENIED = new String[]{
        MsalesConstants.MODULE.USER.ACTION_REGISTER_USER,
        MsalesConstants.MODULE.USER.ACTION_DELETE_USER,
        MsalesConstants.MODULE.COMPANYCONFIG.ACTION_CREATE_COMPANY_CONFIG,
        MsalesConstants.MODULE.COMPANYCONFIG.ACTION_UPDATE_COMPANY_CONFIG,
        MsalesConstants.MODULE.VERSION.ACTION_CREATE_VERSION,
        MsalesConstants.MODULE.VERSION.ACTION_UPDATE_VERSION,
        MsalesConstants.MODULE.EQUIPMENT.ACTION_CREATE_EQUIPMENT,
        MsalesConstants.MODULE.EQUIPMENT.ACTION_UPDATE_EQUIPMENT,
        MsalesConstants.MODULE.EQUIPMENT.ACTION_DELETE_EQUIPMENT,
        MsalesConstants.MODULE.EQUIPMENT.ACTION_GET_LIST_EQUIPMENT,
        MsalesConstants.MODULE.EQUIPMENT.ACTION_GET_LIST_EQUIPMENT_BY_COMPANY_ID,
        MsalesConstants.MODULE.STATUSTYPE.ACTION_CREATE_STATUSTYPE,
        MsalesConstants.MODULE.STATUSTYPE.ACTION_UPDATE_STATUSTYPE,
        MsalesConstants.MODULE.STATUSTYPE.ACTION_DELETE_STATUSTYPE,
        MsalesConstants.MODULE.STATUSTYPE.ACTION_GET_LIST_STATUSTYPE,
        MsalesConstants.MODULE.STATUSTYPE.ACTION_CREATE_STATUSTYPE,
        MsalesConstants.MODULE.STATUSTYPE.ACTION_UPDATE_STATUSTYPE,
        MsalesConstants.MODULE.STATUSTYPE.ACTION_DELETE_STATUSTYPE,
        MsalesConstants.MODULE.STATUSTYPE.ACTION_GET_LIST_STATUSTYPE,
        MsalesConstants.MODULE.STATUS.ACTION_CREATE_STATUS,
        MsalesConstants.MODULE.STATUS.ACTION_UPDATE_STATUS,
        MsalesConstants.MODULE.STATUS.ACTION_DELETE_STATUS,
        MsalesConstants.MODULE.STATUS.ACTION_GET_LIST_STATUS,
        MsalesConstants.MODULE.TABLENAME.ACTION_CREATE_TABLENAME,
        MsalesConstants.MODULE.TABLENAME.ACTION_UPDATE_TABLENAME,
        MsalesConstants.MODULE.TABLENAME.ACTION_DELETE_TABLENAME,
        MsalesConstants.MODULE.TABLENAME.ACTION_GET_LIST_TABLENAME,
        MsalesConstants.MODULE.PROPERTY.ACTION_CREATE_PROPERTY,
        MsalesConstants.MODULE.PROPERTY.ACTION_UPDATE_PROPERTY,
        MsalesConstants.MODULE.PROPERTY.ACTION_DELETE_PROPERTY,
        MsalesConstants.MODULE.PROPERTY.ACTION_GET_LIST_PROPERTY,
        MsalesConstants.MODULE.PROPERTYVALUE.ACTION_CREATE_PROPERTYVALUE,
        MsalesConstants.MODULE.PROPERTYVALUE.ACTION_UPDATE_PROPERTYVALUE,
        MsalesConstants.MODULE.PROPERTYVALUE.ACTION_DELETE_PROPERTYVALUE,
        MsalesConstants.MODULE.PROPERTYVALUE.ACTION_GET_LIST_PROPERTYVALUE,
        MsalesConstants.MODULE.COMPANY.ACTION_CREATE_COMPANY,
        MsalesConstants.MODULE.COMPANY.ACTION_UPDATE_COMPANY,
        MsalesConstants.MODULE.COMPANY.ACTION_DELETE_COMPANY,
        MsalesConstants.MODULE.COMPANY.ACTION_GET_LIST_COMPANY,
        MsalesConstants.MODULE.CHANNELTYPE.ACTION_CREATE_CHANNEL_TYPE,
        MsalesConstants.MODULE.CHANNELTYPE.ACTION_UPDATE_CHANNEL_TYPE,
        MsalesConstants.MODULE.CHANNELTYPE.ACTION_DELETE_CHANNEL_TYPE,
        MsalesConstants.MODULE.CHANNEL.ACTION_CREATE_CHANNEL,
        MsalesConstants.MODULE.CHANNEL.ACTION_UPDATE_CHANNEL,
        MsalesConstants.MODULE.CHANNEL.ACTION_DELETE_CHANNEL,
        MsalesConstants.MODULE.CHANNEL.ACTION_GET_LIST_CHANNEL_BY_CHANNEL_TYPE_ID,
        MsalesConstants.MODULE.CHANNEL.ACTION_GET_LIST_CHANNEL_BY_COMPANY_ID,
        MsalesConstants.MODULE.CHANNEL.ACTION_GET_LIST_CHANNEL_BY_PARENT_ID,
        MsalesConstants.MODULE.LOCATION.ACTION_CREATE_LOCATION,
        MsalesConstants.MODULE.LOCATION.ACTION_UPDATE_LOCATION,
        MsalesConstants.MODULE.LOCATION.ACTION_DELETE_LOCATION,
        MsalesConstants.MODULE.CHANNELLOCATION.ACTION_CREATE_CHANNEL_LOCATION,
        MsalesConstants.MODULE.CHANNELLOCATION.ACTION_UPDATE_CHANNEL_LOCATION,
        MsalesConstants.MODULE.CHANNELLOCATION.ACTION_DELETE_CHANNEL_LOCATION,
        MsalesConstants.MODULE.USER_ROLE_CHANNEL.ACTION_CREATE_USER_ROLE_CHANNEL,
        MsalesConstants.MODULE.USER_ROLE_CHANNEL.ACTION_UPDATE_USER_ROLE_CHANNEL,
        MsalesConstants.MODULE.USER_ROLE_CHANNEL.ACTION_DELETE_USER_ROLE_CHANNEL,
        MsalesConstants.MODULE.WORKFLOW_TYPE.ACTION_CREATE_WORKFLOW_TYPE,
        MsalesConstants.MODULE.WORKFLOW_TYPE.ACTION_UPDATE_WORKFLOW_TYPE,
        MsalesConstants.MODULE.WORKFLOW_TYPE.ACTION_DELETE_WORKFLOW_TYPE,
        MsalesConstants.MODULE.WORKFLOW.ACTION_CREATE_WORKFLOW,
        MsalesConstants.MODULE.WORKFLOW.ACTION_UPDATE_WORKFLOW,
        MsalesConstants.MODULE.WORKFLOW.ACTION_DELETE_WORKFLOW,
        MsalesConstants.MODULE.MCP.ACTION_CREATE_MCP,
        MsalesConstants.MODULE.MCP.ACTION_UPDATE_MCP,
        MsalesConstants.MODULE.MCP.ACTION_DELETE_MCP,
        MsalesConstants.MODULE.MCP_DETAILS.ACTION_CREATE_MCP_DETAILS,
        MsalesConstants.MODULE.MCP_DETAILS.ACTION_UPDATE_MCP_DETAILS,
        MsalesConstants.MODULE.MCP_DETAILS.ACTION_DELETE_MCP_DETAILS,
        MsalesConstants.MODULE.MCP_SALES_DETAILS.ACTION_CREATE_MCP_SALES_DETAILS,
        MsalesConstants.MODULE.MCP_SALES_DETAILS.ACTION_UPDATE_MCP_SALES_DETAILS,
        MsalesConstants.MODULE.MCP_SALES_DETAILS.ACTION_DELETE_MCP_SALES_DETAILS,
        MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_CREATE_GOODS_CATEGORY,
        MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_UPDATE_GOODS_CATEGORY,
        MsalesConstants.MODULE.GOODS_CATEGORY.ACTION_DELETE_GOODS_CATEGORY,
        MsalesConstants.MODULE.GOODS.ACTION_CREATE_GOODS,
        MsalesConstants.MODULE.GOODS.ACTION_UPDATE_GOODS,
        MsalesConstants.MODULE.GOODS.ACTION_DELETE_GOODS
    };

    public static boolean checkPermissionDenie(int permission, String url) {
        switch (permission) {
            case ROLE_COMPANY_ID:
                return companyDenied(url);
            case ROLE_CHANNEL_ID:
                return channelDenied(url);
            case ROLE_ASSISTANT_ID:
                return assistantDenied(url);

            case ROLE_SALESMAN_ID:
                return salesmanDenied(url);
        }
        return false;
    }
    
    /**
     * check url belong to Compan permission
     */
    private static boolean companyDenied(String url) {
        for (String controll : COMPANY_ACCESS_DENIED) {
            if (url.toLowerCase().equals(controll.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * check url belong to Channel permission
     */
    private static boolean channelDenied(String url) {
        for (String controll : CHANNEL_ACCESS_DENIED) {
            if (url.toLowerCase().equals(controll.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * check url belong to Assistant permission
     */
    private static boolean assistantDenied(String url) {
        for (String controll : ASSISTAT_ACCESS_DENIED) {
            if (url.toLowerCase().equals(controll.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * check url belong to salesman permission
     */
    private static boolean salesmanDenied(String url) {
        for (String controll : SALESMAN_ACCESS_DENIED) {
            if (url.toLowerCase().equals(controll.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
