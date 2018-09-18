/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.api.common;

/**
 *
 * @author ChinhNQ
 */
public class MsalesConstants {

    /**
     * URL server msales API
     */
    private static final String REST_ROOT = "http://115.78.155.77:8080/mSales/";
//    private static final String REST_ROOT = "http://localhost:8081/mSales/";
    /**
     * Branch database
     */
    public static final int BRANCH = 1;
    public static final int COMPANY_ID = 1;
    public static final int USER_ID = 1;
    /**
     * Token login
     */
    public static final String TOKEN = "E67720405F41D5E441621678BDDC19D44F6DD806";

    public static class REST_URL {

        /**
         * Change password user
         */
        public static final String USER_CHANGE_PASS = REST_ROOT + "changePasswordUser";

        /**
         * Get web version of company
         */
        public static final String GET_VERSION = REST_ROOT + "getVersion";

        /**
         * Update web version of company
         */
        public static final String UPDATE_VERSION = REST_ROOT + "updateVersion";
        //------------Channenl
        /**
         * Get channel details
         */
        public static final String CHANNEL_GET_DETAILS = REST_ROOT + "getChannel";

        public static final String UPDATE_CHANNEL = REST_ROOT + "updateChannel";

        public static final String SEARCH_CHANNEL = REST_ROOT + "searchChannel";

        public static final String GET_POS = REST_ROOT + "getPOS";

        //------------Company
        public static final String COMPANY_GET_DETAILS = REST_ROOT + "getCompany";

        public static final String LOCATION_GET_CB_LIST_RESIONS = REST_ROOT + "getCbListLocationByLocationType";
        public static final String LOCATION_GETCB_PROVINCE_BY_PARENT = REST_ROOT + "getCbListLocationByParentId";
        public static final String CHANNEL_CB_LIST_BY_PARENT = REST_ROOT + "getCbListChannelByParentId";
        public static final String CHANNEL_LIST_BY_PARENT = REST_ROOT + "getListChannelByParentId";
        public static final String CHANNEL_CB_LIST_BY_CHANNEL_TYPE = REST_ROOT + "getCbListChannelByChannelTypeId";

        public static final String GET_CB_STATUS_BY_STATUS_TYPE_ID = REST_ROOT + "getCbListStatusByStatusTypeId";

        public static final String SEARCH_GOODS_CATEGORY = REST_ROOT + "searchGoodsCategory";

        public static final String CREATE_GOODS_CATEGORY = REST_ROOT + "createGoodsCategory";

        public static final String GET_GOODS_CATEGORY = REST_ROOT + "getGoodsCategory";

        public static final String UPDATE_GOODS_CATEGORY = REST_ROOT + "updateGoodsCategory";

        public static final String DELETE_GOODS_CATEGORY = REST_ROOT + "deleteGoodsCategory";

        public static final String GET_CB_LIST_GOODS_CATEGORY = REST_ROOT + "getCbListGoodsCategory";

        public static final String SEARCH_GOODS = REST_ROOT + "searchGoods";

        public static final String CREATE_GOODS = REST_ROOT + "createGoods";

        public static final String GET_GOODS = REST_ROOT + "getGoods";

        public static final String GET_LIST_GOODS = REST_ROOT + "getListGoods";

        public static final String UPDATE_GOODS = REST_ROOT + "updateGoods";

        public static final String DELETE_GOODS = REST_ROOT + "deleteGoods";

        public static final String GET_CB_USER_ROLE = REST_ROOT + "getCbListUserRole";

        public static final String SEARCH_USER = REST_ROOT + "searchUser";

        public static final String GET_USER = REST_ROOT + "getUser";

        public static final String GET_LIST_USER = REST_ROOT + "getListUser";

        public static final String UPDATE_USER = REST_ROOT + "updateUser";

        public static final String LOCK_USER = REST_ROOT + "lockUser";

        public static final String UNLOCK_USER = REST_ROOT + "unlockUser";

        public static final String RESET_PASSWORD = REST_ROOT + "resetPassword";

        public static final String SEARCH_EQUIPMENT = REST_ROOT + "searchEquipment";

        public static final String GET_EQUIPMENT = REST_ROOT + "getEquipment";

        public static final String UPDATE_EQUIPMENT = REST_ROOT + "updateEquipment";

        public static final String DELETE_EQUIPMENT = REST_ROOT + "deleteEquipment";

        public static final String LOCK_EQUIPMENT = REST_ROOT + "lockEquipment";
        //------------MCP
        public static final String MCP_GET_MCP_AND_SALES_DETAILS = REST_ROOT + "getMCPAndSalesDetails";
        public static final String MCP_CREATE_MCP_AND_SALES_DETAILS = REST_ROOT + "createMCPAndSalesDetails";
        public static final String MCP_UPDATE_MCP_AND_SALES_DETAILS = REST_ROOT + "updateMCPAndSalesDetails";
        public static final String MCP_GET_DETAILS = REST_ROOT + "getMCPAndDetails";
        //------------MCP sales details.
        public static final String MCP_SALES_DETIALS_SEARCH = REST_ROOT + "searchMCPSales";
        public static final String MCP_SEARCH = REST_ROOT + "searchMCP";
        //------------GOOGS
        public static final String GOOGS_GET_LIST_GOOD_CATEGORY = REST_ROOT + "getListGoodsCategory";
        public static final String GOODS_GET_CB_LIST = REST_ROOT + "getCbListGoods";
        //------------USER
        public static final String USER_GET_CB = REST_ROOT + "getCbListUser";

        public static final String USER_GET_CB_LIST_BY_CHANNEL = REST_ROOT + "getCbListUserByChannelId";
        public static final String USER_CREATE = REST_ROOT + "createUser";
        public static final String USER_LIST_BY_CHANNEL = REST_ROOT + "getListUserByChannelId";
        //------------MAP
        public static final String SEARCH_USER_ROUTE = REST_ROOT + "searchUserRoute";

    }

}
