/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.common;

/**
 * Identify all constants for system msale.
 * <p>
 * @author ChinhNQ
 * @version
 * @since 5 Jun 2015 11:11:03 msales_saas#vn.itt.msales.common.Constants.java
 *
 */
public class MsalesConstants {

    /* The key contents of JSON contents post to server from client*/
    public static final String CONTENTS = "contents";
    public static final String PAGE = "page";
    public static final String API_NAME = "apiName";
    public static final String DATE_FORMAT = "dd/mm/yyyy";
    public static final String SESSION = "session";
    public static final String COUNT = "count";

    /**
     * Identify company type.
     */
    public static enum CompanyType {

        /* Telecommunications Company */
        TELECOM(1),
        /* Insurence Company */
        INSURANCE(2),
        /* Pharmaceutical Company*/
        PHARMA(3),
        /* Financial Company*/
        FINANCIAL(4);

        private int mCode;

        private CompanyType(int code) {
            this.mCode = code;
        }

        public int getmCode() {
            return mCode;
        }

        public void setmCode(int mCode) {
            this.mCode = mCode;
        }

    }

    public static final boolean DEBUG = true;
    /**
     * If you want to debug then set it to true else set false.
     */
    public static final String BAD_FILE_TYPE = "BAD_FILE_TYPE";
    /**
     * File not found
     */
    public static final String FILE_NOT_FOUND_EXCEPTION = "FILE_NOT_FOUND_EXCEPTION";
    /**
     * close file is error
     */
    public static final String FILE_CLOSE_EXCEPTION = "FILE_CLOSE_EXCEPTION";

    public static final String MODULES[] = {
        MODULE.CSB.NAME,
        MODULE.USER.NAME,
        MODULE.APP.NAME,
        MODULE.CHANNEL.NAME,
        MODULE.USER_ROUTE.NAME,
        MODULE.GOODS.NAME,
        MODULE.USERGOODSCATEGORY.NAME,
        MODULE.GOODS_CATEGORY.NAME,
        MODULE.GOODS_SERI.NAME,
        MODULE.GOODS_UNIT.NAME,
        MODULE.UNIT.NAME,
        MODULE.SALES.NAME,
        MODULE.STORE.NAME,
        MODULE.COMMISSION.NAME,
        MODULE.CUSTOMERCARE.NAME,
        MODULE.TABLENAME.NAME,
        MODULE.PROPERTY.NAME,
        MODULE.PROPERTYVALUE.NAME,
        MODULE.STATUS.NAME,
        MODULE.STATUSTYPE.NAME,
        MODULE.COMPANY.NAME,
        MODULE.COMPANYCONFIG.NAME,
        MODULE.COMPANYCONFIGDETAILS.NAME,
        MODULE.COMPANYHOLIDAY.NAME,
        MODULE.CHANNELTYPE.NAME,
        MODULE.CHANNEL.NAME,
        MODULE.POS.NAME,
        MODULE.POS_IMG.NAME,
        MODULE.SALESTRANS.NAME,
        MODULE.SALESTRANSDETAILS.NAME,
        MODULE.SALESTRANSSERI.NAME,
        MODULE.VERSION.NAME,
        MODULE.EQUIPMENT.NAME,
        MODULE.WORKFLOW.NAME,
        MODULE.WORKFLOW_TYPE.NAME,
        MODULE.MCP.NAME,
        MODULE.MCP_DETAILS.NAME,
        MODULE.MCP_SALES_DETAILS.NAME,
        MODULE.CUSTOMER_CARE_DETAILS.NAME,
        MODULE.CUSTOMER_CARE_INFORMATION.NAME,
        MODULE.USERROLE.NAME,
        MODULE.LOCATION.NAME,
        MODULE.CHANNELLOCATION.NAME,
        MODULE.USER_ROLE_CHANNEL.NAME,
        MODULE.SALESSTOCK.NAME,
        MODULE.SALESSTOCKGOODS.NAME,
        MODULE.SALES_STOCK_GOODS_SERIAL.NAME,
        MODULE.SALES_ORDER.NAME,
        MODULE.SALES_ORDER_DETAILS.NAME,
        MODULE.SALESSTOCK.NAME,
        MODULE.SALESSTOCKGOODS.NAME,
        MODULE.PROMOTION_AWARD.NAME
    };

    /**
     * Definer constants error
     *
     */
    public class ERROR {

        public static final String SQL_FALTA = "SQL_FALTA";
        public static final int SQL_ERROR = -1;
    }

    /**
     * All modules in system msales
     *
     */
    public class MODULE {

        public class CSB {

            /**
             * Module name
             */
            public static final String NAME = "csb";
            /**
             * Connect to database is fail.
             */
            public static final String CONNECTION_FAIL = "connectedFail";
            /**
             * JSON request from client is invalid
             */
            public static final String JSON_INVALID = "jsonInvalid";
            /**
             * Request error
             */
            public static final String REQUEST_ERROR = "requestError";
            /**
             * Request page not found
             */
            public static final String PAGE_NOT_FOUND = "pageNotFound";
            /**
             * Session Expired.
             */
            public static final String SESSION_EXPIRED = "sessionExpired";
            /**
             * Page json required
             */
            public static final String PAGE_REQUIRED = "pageRequired";
            public static final String PAGE_FILEDS_REQUEST = "requestFiledsPage";
            public static final String ACCESS_IS_DENIED = "accessDenied";

        }

        /**
         * Module APP.
         */
        public class APP {

            /**
             * Module APP
             */
            public static final String NAME = "app";

            /**
             * Register APP
             */
            public static final String ACTION_REGISTER_DEVICE_APP = "registerDeviceApp";

            /**
             * Login APP
             */
            public static final String ACTION_LOGIN_APP = "loginApp";
            
            /**
             * get Workflow App
             */
            public static final String ACTION_GET_WORKFLOW_APP = "getWorkflowApp";
            
             /**
             * Change password App
             */
            public static final String ACTION_CHANGE_PASSWORD_APP = "changePasswordApp";

            /**
             * get List MCP APP
             */
            public static final String ACTION_GET_LIST_MCP_APP = "getListMCPApp";

            /**
             * Module get Targer Info APP
             */
            public static final String ACTION_GET_TARGETS_INFO_APP = "getTargetsInfoApp";

            /**
             * Module create UserRoute App
             */
            public static final String ACTION_CREATE_USER_ROUTE_APP = "createUserRouteApp";

            /**
             * Module getPOSListBySalesman App
             */
            public static final String ACTION_GET_POST_LIST_BY_SALESMAN_APP = "getPOSListBySalesmanApp";
            /**
             * getPOSListBySalesSupApp
             */
            public static final String ACTION_GET_POST_LIST_BY_SALESSUP_APP = "getPOSListBySalesSupApp";

            public static final String ACTION_GET_SUP_KPI_APP = "getSupKPIApp";
            public static final String ACTION_SAVE_SUP_KPI_APP = "saveSupKPIApp";
            
            /**
             * Module get WorkflowCustomerCare App
             */
            public static final String ACTION_GET_WORKFLOW_CUSTOMERCARE_APP = "getWorkflowCustomerCareApp";
            /**
             * Module save CustomerCare App
             */
            public static final String ACTION_SAVE_CUSTOMERCARE_APP = "saveCustomerCareApp";

            /**
             * Module save CustomerCareImage App
             */
            public static final String ACTION_SAVE_CUSTOMERCARE_IMAGE_APP = "saveCustomerCareImageApp";

            /**
             * Module get POSInfo App
             */
            public static final String ACTION_GET_POS_INFO_APP = "getPOSInfoApp";

            /**
             * Module get POSInfo App
             */
            public static final String ACTION_GET_AVAILABLE_FUNCTION_APP = "getAvailableFunctionsApp";

            /**
             * Module search POS App
             */
            public static final String ACTION_SEARCH_POS_APP = "searchPOSApp";

            /**
             * Module get CustomerCareHistory App
             */
            public static final String ACTION_GET_CUSTOMERCARE_HISTORY_APP = "getCustomerCareHistoryApp";

            /**
             * Module get SalesTransHistory App
             */
            public static final String ACTION_GET_SALES_TRANS_HISTORY_APP = "getSalesTransHistoryApp";

            /**
             * Module get OrderListHistory App
             */
            public static final String ACTION_GET_ORDER_LIST_HISTORY_APP = "getOrderListHistoryApp";

            /**
             * Module get CbList SupChannel App
             */
            public static final String ACTION_GET_CB_SUP_CHANNEL_APP = "getCbListSupChannelApp";
            /**
             * Module search SupRoute App
             */
            public static final String ACTION_SEARCH_SUP_ROUTE_APP = "searchSupRouteApp";

            /**
             * Module search SupMCP App
             */
            public static final String ACTION_SEARCH_SUP_MCP_APP = "searchSupMCPApp";

            /**
             * Module get SupMCP App
             */
            public static final String ACTION_GET_SUP_MCP_APP = "getSupMCPApp";

            /**
             * Module get List SoldGoods ByPOS App
             */
            public static final String ACTION_GET_LIST_SOLD_GOODS_BY_POS_APP = "getListSoldGoodsByPOSApp";

            /**
             * Module search SupOrder App
             */
            public static final String ACTION_SEARCH_SUP_ORDER_APP = "searchSupOrderApp";

            /**
             * Module update SupPOS App
             */
            public static final String ACTION_UPDATE_SUP_POS_APP = "updateSupPOSApp";

            /**
             * Module search SupRoute App
             */
            public static final String ACTION_RETURN_GOODS_APP = "returnGoodsApp";

            /**
             * get StockGoods
             */
            public static final String ACTION_GET_STOCK_APP = "getStockApp";

            /**
             * get List recovery Goods APP
             */
            public static final String ACTION_GET_RECOVER_GOODS_APP = "getRecoverGoodsApp";
            
             /**
             * get Goods info of order
             */
            public static final String ACTION_GET_ORDER_GOODS_INFO_APP = "getOrderGoodsInfoApp";

            /**
             * recovery Goods APP
             */
            public static final String ACTION_RECOVER_GOODS_APP = "recoverGoodsApp";

            /**
             * Nhập hàng hóa vào hệ thống
             */
            public static final String ACTION_RECEIVE_GOODS_APP = "receiveGoodsApp";

            /**
             * create POS App
             */
            public static final String ACTION_CREATE_POS_APP = "createPOSApp";
            /**
             * Danh sách hàng hóa Salesman được bán getGoodsSalesmanApp
             */
            public static final String ACTION_GET_GOODS_SALES_MAN_APP = "getGoodsSalesmanApp";
            
             /**
             * Danh sách hàng hóa salesSup được bán - tất cả sản phẩm của nhân viên
             */
            public static final String ACTION_GET_GOODS_SUP_APP = "getGoodsSupApp";
            
            /**
             * bán hàng trực tiếp sellGoodsApp
             */
            public static final String ACTION_SELL_GOODS_APP = "sellGoodsApp";
            /**
             * đặt hàng qua điện thoại
             */
            public static final String ACTION_SET_GOODS_ORDER_APP = "setGoodsOrderApp";
            /**
             * getOrderListByPOSApp Danh sách đơn hàng của điểm bán hàng
             */
            public static final String ACTION_GET_ORDER_LIST_BY_POS_APP = "getOrderListByPOSApp";
            /**
             * checkGoodsInStockApp Kiểm tra điều kiện trước khi giao hàng
             */
            public static final String ACTION_CHECK_GOODS_IN_STOCK_APP = "checkGoodsInStockApp";
            /**
             * deliveryGoodsApp giao hàng
             */
            public static final String ACTION_DELIVERY_GOODS_APP = "deliveryGoodsApp";
            /**
             * setGoodsOrderMobileApp
             */
            public static final String ACTION_SET_GOODS_ORDER_MOBILE_APP = "setGoodsOrderMobileApp";
            /**
             * getSupSellApp
             */
            public static final String ACTION_GET_SUP_SELL_APP = "getSupSellApp";

            /**
             * get ListUserSupApp
             */
            public static final String ACTION_GET_LIST_USER_SUP_APP = "getListUserSupApp";

            /**
             * Search SupPOS App
             */
            public static final String ACTION_SEARCH_SUP_POS_APP = "searchSupPOSApp";
            
            /**
             * --------------------------------
             * PROMOTION
             * --------------------------------
             */
            public static final String ACTION_GET_LIST_TEST_APP = "getListTestApp";
            
            public static final String ACTION_GET_LIST_PROMOTION_APP = "getListPromotionApp";
            public static final String ACTION_CB_GET_LIST_PROMOTION_APP = "getCbListPromotionApp";
            public static final String ACTION_SEARCH_PROMOTION_APP = "searchPromotionApp";
            public static final String ACTION_GET_PROMOTION_APP = "getPromotionApp";
            public static final String ACTION_GET_LIST_REGISTER_PROMOTION_APP = "getListRegisterPromotionApp";
            public static final String ACTION_REGISTER_PROMOTION_APP = "registerPromotionApp";
            public static final String ACTION_GET_LIST_COMPLETED_PROMOTION_APP = "getListCompletedPromotionApp";
            public static final String ACTION_GET_LIST_POS_COMPLETED_APP = "getListPOSCompletedApp";
            public static final String ACTION_GET_LIST_WAITING_POS_APP = "getListPOSWaitingApp";
            public static final String ACTION_GET_LIST_DELIVERED_POS_APP = "getListPOSDeliveredApp";
            public static final String ACTION_GET_LIST_CANCEL_POS_APP = "getListPOSCancelApp";
            public static final String ACTION_DELIVER_PROMOTION_APP = "deliverPromotionApp";
            public static final String ACTION_SAVE_PROMOTION_APP = "savePromotionApp";
            public static final String ACTION_CANCEL_PROMOTION_APP = "cancelPromotionApp";
            public static final String ACTION_APPROVE_PROMOTION_APP = "approvePromotionApp";
            public static final String ACTION_APPROVE_REGISTER_PROMOTION_APP = "approveRegisterPromotionApp";
        }

        /**
         * Module user.
         */
        public class USER {

            /**
             * Module name
             */
            public static final String NAME = "user";
            /**
             * get user by id
             */
            public static final String ACTION_GET_USER = "getUser";
            /**
             * get list user
             */
            public static final String ACTION_GET_LIST_USER = "getListUser";
            /**
             * Delete user
             */
            public static final String ACTION_DELETE_USER = "deleteUser";
            /**
             * Update user
             */
            public static final String ACTION_UPDATE_USER = "updateUser";
            /**
             * Register user
             */
            public static final String ACTION_REGISTER_USER = "createUser";

            /**
             * create user by admin
             */
            public static final String ACTION_CREATE_USER_ADMIN = "createUserAdmin";

            /**
             * update user by admin
             */
            public static final String ACTION_UPDATE_USER_ADMIN = "updateUserAdmin";

            /**
             * Change password user
             */
            public static final String ACTION_CHANGE_PASSWORD_USER = "changePasswordUser";
            /**
             * User login
             */
            public static final String ACTION_LOGIN = "login";

            /**
             * Admin login
             */
            public static final String ACTION_LOGIN_ADMIN = "loginAdmin";

            /**
             * check login
             */
            public static final String ACTION_CHECK_LOGIN = "checkLogin";

            /**
             * User logout
             */
            public static final String ACTION_LOGOUT = "logout";

            /**
             * User search
             */
            public static final String ACTION_SEARCH = "searchUser";

            /**
             * check username
             */
            public static final String ACTION_CHECK_USERNAME = "checkUserName";

            /**
             * get user by locationId
             */
            public static final String ACTION_GET_CB_LIST_USER_BY_LOCATION_ID = "getCbListUserByLocationId";

            /**
             * get user by channelId
             */
            public static final String ACTION_GET_CB_LIST_USER_BY_CHANNEL_ID = "getCbListUserByChannelId";
            /**
             * get_Cn_List_User
             */
            public static final String ACTION_GET_CB_LIST_USER = "getCbListUser";

            /**
             * search Admin User
             */
            public static final String ACTION_SEARCH_ADMIN_USER = "searchAdminUser";
            /**
             * lock User
             */
            public static final String ACTION_LOCK_USER = "lockUser";

            /**
             * reset password User
             */
            public static final String ACTION_RESET_PASSWORD_USER = "resetPasswordUser";
            /**
             * unlock User
             */
            public static final String ACTION_UNLOCK_USER = "unlockUser";
            /**
             * get user by channelId
             */
            public static final String ACTION_GET_LIST_USER_BY_CHANNEL_ID = "getListUserByChannelId";
            /**
             * get user by locationId
             */
            public static final String ACTION_GET_CB_LIST_USER_BY_TINH_THANH_PHO = "getCbListUserByTinhThanh";

            /**
             * get Cb List user by channel
             */
            public static final String ACTION_GET_CB_LIST_USER_BY_CHANNEL_AND_LOCATION = "getCbListUserByChannelAndLocation";

            /**
             * get Cb List monitoring by channel
             */
            public static final String ACTION_GET_CB_LIST_MONITORING_BY_CHANNEL = "getCbListMonotoringByChannel";

        }

        /**
         *
         * Module UserRole
         *
         */
        public class USERROLE {

            /**
             * Module name
             */
            public static final String NAME = "userrole";
            /**
             * get userRole by id
             */
            public static final String ACTION_GET_USERROLE = "getUserRole";
            /**
             * get list userRole
             */
            public static final String ACTION_GET_LIST_USERROLE = "getListUserRole";
            /**
             * Delete userRole
             */
            public static final String ACTION_DELETE_USERROLE = "deleteUserRole";
            /**
             * Update userRole
             */
            public static final String ACTION_UPDATE_USERROLE = "updateUserRole";
            /**
             * create userRole
             */
            public static final String ACTION_CREATE_USERROLE = "createUserRole";
            /**
             * get List user_role by parentId
             */
            public static final String ACTION_GET_LIST_USERROLE_BY_PARENT_ID = "getCbListUserRoleByParentId";
            /**
             * get Cb list userRole
             */
            public static final String ACTION_GET_CB_LIST_USERROLE = "getCbListUserRole";

            /**
             * get UserRole By UserId
             */
            public static final String ACTION_GET_USERROLE_BY_USER_ID = "getUserRoleByUserId";

            /**
             * get Cb List UserRole By Monitoring
             */
            public static final String ACTION_GET_CB_LIST_USERROLE_BY_USER_ID = "getCbListUserRoleByUserId";
        }

        /**
         *
         * Module Location
         *
         */
        public class LOCATION {

            /**
             * Module name
             */
            public static final String NAME = "location";
            /**
             * get Location by id
             */
            public static final String ACTION_GET_LOCATION = "getLocation";
            /**
             * get list Location
             */
            public static final String ACTION_GET_LIST_LOCATION = "getListLocation";
            /**
             * Delete Location
             */
            public static final String ACTION_DELETE_LOCATION = "deleteLocation";
            /**
             * Update Location
             */
            public static final String ACTION_UPDATE_LOCATION = "updateLocation";
            /**
             * create Location
             */
            public static final String ACTION_CREATE_LOCATION = "createLocation";

            /**
             * GET LIST LOCATION BY PARENTID
             */
            public static final String ACTION_GET_LIST_LOCATION_BY_PARENTID = "getListLocationByParentId";

            /**
             * GET CB LIST LOCATION BY LOCATION_TYPE
             */
            public static final String ACTION_GET_CB_LIST_LOCATION_BY_LOCATIONTYPE = "getCbListLocationByLocationType";

            /**
             * get Combobox List Location By ParentId
             */
            public static final String ACTION_GET_CB_LIST_LOCATION_BY_PARENT_ID = "getCbListLocationByParentId";

            /**
             * get Combobox List Location By EmployerId
             */
            public static final String ACTION_GET_CB_LIST_LOCATION_BY_EMPLOYER_ID = "getCbListLocationByEmployerId";

            public static final String ACTION_GET_CB_LIST_LOCATION_BY_LOCATIONTYPE_IS_1 = "getCbListLocationCity";
            /**
             * get CB List Location by Channel Id
             */
            public static final String ACTION_GET_CB_LIST_LOCATION_BY_CHANNEL_ID = "getCbListLocationByChannelId";

            /**
             * get CB List Location by userId
             */
            public static final String ACTION_GET_CB_LIST_LOCATION_BY_USER_ID = "getCbListLocationByUserId";
            
            public static final String ACTION_GET_CB_LIST_DISTRICT_BY_USER_ID = "getCbListDistrictLocation";

            /**
             * get search Location By ParentId and searchText
             */
            public static final String ACTION_SEARCH_LOCATION = "searchLocation";
        }

        /**
         *
         * Module CHANNEL_LOCATION
         *
         */
        public class CHANNELLOCATION {

            /**
             * Module name
             */
            public static final String NAME = "channellocation";
            /**
             * get channelLocation by id
             */
            public static final String ACTION_GET_CHANNEL_LOCATION = "getChannelLocation";
            /**
             * get list channelLocation
             */
            public static final String ACTION_GET_LIST_CHANNEL_LOCATION = "getListChannelLocation";
            /**
             * Delete channelLocation
             */
            public static final String ACTION_DELETE_CHANNEL_LOCATION = "deleteChannelLocation";
            public static final String ACTION_DELETE_CHANNEL_LOCATION_BY_CHANNEL_ID_AND_LOCATION_ID = "deleteChannelLocationByChannelIdAndLocationId";
            /**
             * Update channelLocation
             */
            public static final String ACTION_UPDATE_CHANNEL_LOCATION = "updateChannelLocation";
            /**
             * create channelLocation
             */
            public static final String ACTION_CREATE_CHANNEL_LOCATION = "createChannelLocation";

            /**
             * GET LIST CHANNEL_LOCATION BY CHANNEL_ID
             */
            public static final String ACTION_GET_LIST_CHANNEL_LOCATION_BY_CHANNEL_ID = "getListChannelLocationByChannelId";

            /**
             * GET LIST CHANNEL_LOCATION BY LOCATION_ID
             */
            public static final String ACTION_GET_LIST_CHANNEL_LOCATION_BY_LOCATION_ID = "getListChannelLocationByLocationId";

        }

        /**
         * Module goods
         *
         */
        public class GOODS {

            /**
             * Module name
             */
            public static final String NAME = "goods";
            /**
             * Create Goods
             */
            public static final String ACTION_CREATE_GOODS = "createGoods";
            /**
             * Update Goods
             */
            public static final String ACTION_UPDATE_GOODS = "updateGoods";
            /**
             * View Goods
             */
            public static final String ACTION_GET_GOODS = "getGoods";

            /**
             * Delete Goods
             */
            public static final String ACTION_DELETE_GOODS = "deleteGoods";

            /**
             * Get List Goods
             */
            public static final String ACTION_GET_LIST_GOODS = "getListGoods";

            public static final String ACTION_GET_GOODS_BY_GOODSCATEGORY = "getListGoodsByGoodsCategoryId";

            public static final String ACTION_SEARCH_GOODS = "searchGoods";
            /**
             * get Combobox List Goods
             */
            public static final String ACTION_GET_CB_LIST_GOODS = "getCbListGoods";
            /**
             * get combobox List Goods By GoodsCategoryId
             */
            public static final String ACTION_GET_CB_LIST_GOODS_BY_GOODS_CATEGORY_ID = "getCbListGoodsByGoodsCategoryId";
            /**
             * create Goods Admin
             */
            public static final String ACTION_CREATE_GOODS_ADMIN = "createGoodsAdmin";
            /**
             * update Goods Admin
             */
            public static final String ACTION_UPDATE_GOODS_ADMIN = "updateGoodsAdmin";
            /**
             * get Goods Admin
             */
            public static final String ACTION_GET_GOODS_ADMIN = "getGoodsAdmin";

            public static final String ACTION_GET_GOODS_SALES_MAN = "getGoodsSalesman";
            
            public static final String ACTION_GET_GOODS_BY_GOODSCATEGORY_OF_PROMOTION = "getListGoodsByGoodsCategoryIdOfPromotion";
        }

        /**
         * Module UserGoodsCategory
         *
         */
        public class USERGOODSCATEGORY {

            /**
             * Module name
             */
            public static final String NAME = "usergoodscategory";
            /**
             * Create UserGoodsCategory
             */
            public static final String ACTION_CREATE_USERGOODSCATEGORY = "createUserGoodsCategory";
            /**
             * Update UserGoodsCategory
             */
            public static final String ACTION_UPDATE_USERGOODSCATEGORY = "updateUserGoodsCategory";

            /**
             * Delete UserGoodsCategory
             */
            public static final String ACTION_DELETE_USERGOODSCATEGORY = "deleteUserGoodsCategory";

            /**
             * Get List UserGoodsCategory
             */
            public static final String ACTION_GET_LIST_USERGOODSCATEGORY = "getListUserGoodsCategory";

            /**
             * Get Cb List UserGoodsCategory by UserId
             */
            public static final String ACTION_GET_CB_LIST_USERGOODSCATEGORY_BY_USER_ID = "getCbListUserGoodsCategoryByUserId";

        }

        /**
         * Module goods_Unit
         *
         */
        public class GOODS_UNIT {

            /**
             * Module name
             */
            public static final String NAME = "goodsunit";
            /**
             * Create Goods_Unit
             */
            public static final String ACTION_CREATE_GOODS_UNIT = "createGoodsUnit";
            /**
             * Update Goods_Unit
             */
            public static final String ACTION_UPDATE_GOODS_UNIT = "updateGoodsUnit";
            /**
             * View Goods_Unit
             */
            public static final String ACTION_GET_GOODS_UNIT = "getGoodsUnit";

            /**
             * Delete Goods_unit
             */
            public static final String ACTION_DELETE_GOODS_UNIT = "deleteGoodsUnit";

            /**
             * Get List GoodsUnit
             */
            public static final String ACTION_GET_LIST_GOODS_UNIT = "getListGoodsUnit";
            /**
             * Get goodsUnit by Goods
             */
            public static final String ACTION_GET_GOODSUNIT_BY_GOODS = "getListGoodsUnitByGoodsId";

            /**
             * Get GoodsUnit by Unit
             */
            public static final String ACTION_GET_GOODSUNIT_BY_UNIT = "getListGoodsUnitByUnitId";
            
            public static final String ACTION_GET_GOODS_UNIT_FOR_ORDER = "getGoodsUnitForOrder";

        }

        /**
         * Module Goods Category
         */
        public class GOODS_CATEGORY {

            /**
             * Module name
             */
            public static final String NAME = "goodscategory";

            /**
             * Create Goods_Category
             */
            public static final String ACTION_CREATE_GOODS_CATEGORY = "createGoodsCategory";
            /**
             * Update Goods_Category
             */
            public static final String ACTION_UPDATE_GOODS_CATEGORY = "updateGoodsCategory";
            /**
             * View Goods_Category
             */
            public static final String ACTION_GET_GOODS_CATEGORY = "getGoodsCategory";

            /**
             * Delete Goods_Category
             */
            public static final String ACTION_DELETE_GOODS_CATEGORY = "deleteGoodsCategory";

            /**
             * Get List Goods_Category
             */
            public static final String ACTION_GET_LIST_GOODS_CATEGORY = "getListGoodsCategory";

            /**
             * search GoodsCategory
             */
            public static final String ACTION_SEARCH_GOODS_CATEGORY = "searchGoodsCategory";
            /**
             * get Combobox List GoodsCategory
             */
            public static final String ACTION_GET_CB_LIST_GOODS_CATEGORY = "getCbListGoodsCategory";

        }

        /**
         * Module Goods_Seri
         */
        public class GOODS_SERI {

            /**
             * Module name
             */
            public static final String NAME = "goodsseri";

            /**
             * Create Goods_Seri
             */
            public static final String ACTION_CREATE_GOODS_SERI = "createGoodsSeri";
            /**
             * Update Goods_Seri
             */
            public static final String ACTION_UPDATE_GOODS_SERI = "updateGoodsSeri";
            /**
             * View Goods_Seri
             */
            public static final String ACTION_GET_GOODS_SERI = "getGoodsSeri";

            /**
             * Delete Goods_Seri
             */
            public static final String ACTION_DELETE_GOODS_SERI = "deleteGoodsSeri";
            /**
             * get List Goods Seri
             */
            public static final String ACTION_GET_LIST_SERI = "getListGoodsSeri";
            /**
             * List seri by Goods
             */
            public static final String ACTION_GET_LIST_SERI_BY_GOODS_ID = "getListGoodsSeriByGoodsId";
        }

        /**
         * Module Unit
         */
        public class UNIT {

            /**
             * Module name
             */
            public static final String NAME = "unit";

            /**
             * Create Unit
             */
            public static final String ACTION_CREATE_UNIT = "createUnit";
            /**
             * Update Unit
             */
            public static final String ACTION_UPDATE_UNIT = "updateUnit";
            /**
             * View Unit
             */
            public static final String ACTION_GET_UNIT = "getUnit";

            /**
             * Delete Unit
             */
            public static final String ACTION_DELETE_UNIT = "deleteUnit";
            /**
             * list Unit
             */
            public static final String ACTION_GET_LIST_UNIT = "getListUnit";
            /**
             * get CB List
             */
            public static final String ACTION_GET_CB_LIST_UNIT = "getCbListUnit";
        }

        /**
         * Module sales
         *
         */
        public class SALES {

            /**
             * Module name
             */
            public static final String NAME = "sales";
        }

        /**
         * Module Sales Order
         */
        public class SALES_ORDER {

            /**
             * Module Name
             */
            public static final String NAME = "salesorder";
            /**
             * Create Sales Order
             */
            public static final String ACTION_CREATE_SALES_ORDER = "createSalesOrder";
            /**
             * Update Sales Order
             */
            public static final String ACTION_UPDATE_SALES_ORDER = "updateSalesOrder";
            /**
             * Delete Sales Order
             */
            public static final String ACTION_DELETE_SALES_ORDER = "deleteSalesOrder";
            /**
             * get Sales Order
             */
            public static final String ACTION_GET_SALES_ORDER = "getSalesOrder";
            /**
             * get List Sales Order By Stock Id
             */
            public static final String ACTION_GET_LIST_SALES_ORDER_BY_STOCK_ID = "getListSalesOrderByStockId";
            /**
             * get List Sales Order By Sales Trans Date
             */
            public static final String ACTION_GET_LIST_SALES_ORDER_BY_SALES_TRANS_DATE = "getListSalesOrderBySalesTransDate";
            /**
             * Hủy bỏ SalesOrder
             */
            public static final String ACTION_DEACTIVE_SALES_ORDER = "deactiveSalesOrder";
            /**
             * exportSalesOrder Xuất kho
             */
            public static final String ACTION_EXPORT_SALES_ORDER = "exportSalesOrder";
            /**
             * getSalesOrder
             */
            public static final String ACTION_GET_SALES_ORDER_ADMINISTRATOR = "getSalesOrderAdmin";
            /**
             * searchOrder
             */
            public static final String ACTION_SEARCH_ORDER = "searchSalesOrder";

            /**
             * searchOrder by List User Id
             */
            public static final String ACTION_SEARCH_ORDER_BY_LIST_USER_ID = "searchSalesOrderByListUserId";

            /**
             * setGoodsOrderMobile
             */
            public static final String ACTION_SET_GOODS_SALES_ORDER_MOBILE = "setSalesOrderMobile";

        }

        /**
         * Module Sales Order Details
         */
        public class SALES_ORDER_DETAILS {

            /**
             * Module Name
             */
            public static final String NAME = "salesorderdetails";
            /**
             * Create Sales Order Details
             */
            public static final String ACTION_CREATE_SALES_ORDER_DETAILS = "createSalesOrderDetails";
            /**
             * Update Sales Order Details
             */
            public static final String ACTION_UPDATE_SALES_ORDER_DETAILS = "updateSalesOrderDetails";
            /**
             * Delete Sales Order Details
             */
            public static final String ACTION_DELETE_SALES_ORDER_DETAILS = "deleteSalesOrderDetails";
            /**
             * Get Sales Order Details
             */
            public static final String ACTION_GET_SALES_ORDER_DETAILS = "getSalesOrderDetails";
            /**
             * get List Sales Order Details By Order Id
             */
            public static final String ACTION_GET_LIST_SALES_ORDER_DETAILS_BY_ORDER_ID = "getListSalesOrderDetailsByOrderId";
            /**
             * updateListSalesOrderDetails
             */
            public static final String ACTION_UPDATE_LIST_SALES_ORDER_DETAILS = "updateListSalesOrderDetails";

        }

        /**
         * Module store
         *
         */
        public class STORE {

            /**
             * Module name
             */
            public static final String NAME = "store";
        }

        /**
         * Module commission
         *
         */
        public class COMMISSION {

            /**
             * Module name
             */
            public static final String NAME = "commission";
        }

        /**
         * Module User_route
         */
        public class USER_ROUTE {

            /**
             * Module name
             */
            public static final String NAME = "userroute";

            /**
             * search User route
             */
            public static final String ACTION_SEARCH_USER_ROUTE = "searchUserRoute";

            public static final String ACTION_SEARCH_USER_ROUTE_APP = "searchUserRouteAdmin";
            public static final String ACTION_SEARCH_USER_ROUTE_ADMIN = "searchUserRouteBB";

        }

        /**
         * Module Workflow Type
         */
        public class WORKFLOW_TYPE {

            /**
             * Module name
             */
            public static final String NAME = "workflowtype";
            /**
             * create Wrokflow Type
             */
            public static final String ACTION_CREATE_WORKFLOW_TYPE = "createWorkflowType";
            /**
             * update Workflow Type
             */
            public static final String ACTION_UPDATE_WORKFLOW_TYPE = "updateWorkflowType";
            /**
             * delete Workflow Type
             */
            public static final String ACTION_DELETE_WORKFLOW_TYPE = "deleteWorkflowType";
            /**
             * get Workflow Type
             */
            public static final String ACTION_GET_WORKFLOW_TYPE = "getWorkflowType";
            /**
             * get List Workflow Type
             */
            public static final String ACTION_GET_LIST_WORKFLOW_TYPE = "getListWorkflowType";

        }

        /**
         * Module Workflow
         */
        public class WORKFLOW {

            /**
             * Module name
             */
            public static final String NAME = "workflow";
            /**
             * create Wrokflow
             */
            public static final String ACTION_CREATE_WORKFLOW = "createWorkflow";
//            public static final String ACTION_GET_WORKFLOW ="getWorkflow";
            /**
             * update Workflow
             */
            public static final String ACTION_UPDATE_WORKFLOW = "updateWorkflow";
            /**
             * delete Workflow
             */
            public static final String ACTION_DELETE_WORKFLOW = "deleteWorkflow";

            /**
             * get List Workflow By CompanyId
             */
            public static final String ACTION_GET_LIST_WORKFLOW_BY_COMPANY_ID = "getListWorkflowByCompanyId";
            /**
             * get List Workflow By WorkflowYpeID
             */
            public static final String ACTION_GET_LIST_WORKFLOW_BY_WORKFLOW_TYPE_ID = "getListWorkflowByWorkflowTypeId";
        }

        /**
         * Module MCP
         */
        public class MCP {

            /**
             * Module name
             */
            public static final String NAME = "mcp";
            /**
             * create MCP
             */
            public static final String ACTION_CREATE_MCP = "createMCP";
            /**
             * update MCP
             */
            public static final String ACTION_UPDATE_MCP = "updateMCP";
            /**
             * delete MCP
             */
            public static final String ACTION_DELETE_MCP = "deleteMCP";
            /**
             * get MCP
             */
            public static final String ACTION_GET_MCP = "getMCP";
            /**
             * get List MCP theo Implement Id
             */
            public static final String ACTION_GET_LIST_MCP_BY_IMPLEMENT_EMPLOYEE_ID = "getListMCPByImplementEmployeeId";
            /**
             * get MCP Customer Care Details admin
             */
            public static final String ACTION_GET_MCP_AND_DETAILS = "getMCPAndDetails";
            /**
             * update MCP customer care details Admin
             */
            public static final String ACTION_UPDATE_MCP_DETAILS_ADMIN = "updateMCPAndDetails";
            /**
             * search MCP Details
             */
            public static final String ACTION_SEARCH_MCP_DETAILS = "searchMCP";

            /**
             * get MCP_Administrator
             */
            public static final String ACTION_GET_MCP_ADMINISTRATOR = "getMCPAndSalesDetails";
            /**
             * Search MCP (Administrator)
             */
            public static final String ACTION_SEARCH_MCP = "searchMCPSales";
            /**
             * create MCP_Sales_Details_Administrator
             */
            public static final String ACTION_CREATE_MCP_SALES_DETAILS_ADMINISTRATOR = "createMCPAndSalesDetails";
            /**
             * update MCP MCPSalesDetails
             */
            public static final String ACTION_UPDATE_MCP_SALES_DETAILS_ADMINISTRATOR = "updateMCPAndSalesDetails";
            /**
             * create MCPDetails for Administrator
             */
            public static final String ACTION_CREATE_MCP_DETAILS_ADMINISTRATOR = "createMCPAndDetails";

        }

        /**
         * Module MCP Details
         */
        public class MCP_DETAILS {

            /**
             * Module name
             */
            public static final String NAME = "mcpdetails";
            /**
             * create MCPDetails
             */
            public static final String ACTION_CREATE_MCP_DETAILS = "createMCPDetails";
            /**
             * update MCPDetails
             */
            public static final String ACTION_UPDATE_MCP_DETAILS = "updateMCPDetails";
            /**
             * delete MCPDetails
             */
            public static final String ACTION_DELETE_MCP_DETAILS = "deleteMCPDetails";
            /**
             * get List MCPDetails
             */
            public static final String ACTION_GET_LIST_MCP_DETAILS = "getListMCPDetails";

        }

        /**
         * Module MCP MCPSalesDetails
         */
        public class MCP_SALES_DETAILS {

            /**
             * Module Name
             */
            public static final String NAME = "mcpsalesdetails";
            /**
             * create MCP MCPSalesDetails
             */
            public static final String ACTION_CREATE_MCP_SALES_DETAILS = "createMCPSalesDetails";
            /**
             * update MCP MCPSalesDetails
             */
            public static final String ACTION_UPDATE_MCP_SALES_DETAILS = "updateMCPSalesDetails";
            /**
             * delete MCP MCPSalesDetails
             */
            public static final String ACTION_DELETE_MCP_SALES_DETAILS = "deleteMCPSalesDetails";
            /**
             * get List MCP MCPSalesDetails by MCPId
             */
            public static final String ACTION_GET_LIST_MCP_SALES_DETAILS_BY_MCP_ID = "getListMCPSalesDetails";

//            /**
//             * get List MCP MCPSalesDetails By GoodsId
//             */
//            public static final String ACTION_GET_LIST_SCHEDULE_SALES_DETAILS_BY_GOODS_ID = "getListMCPSalesDetailsByGoodsId";
        }

        /**
         * Module Customer Care Detials
         */
        public class CUSTOMER_CARE_DETAILS {

            /**
             * Module name
             */
            public static final String NAME = "customercaredetails";
            /**
             * create CustomerCareDetails
             */
            public static final String ACTION_CREATE_CUSTOMER_CARE_DETAILS = "createCustomerCareDetails";
            /**
             * update CustomerCareDetails
             */
            public static final String ACTION_UPDATE_CUSTOMER_CARE_DETAILS = "updateCustomerCareDetails";
            /**
             * delete CustomerCareDetails
             */
            public static final String ACTION_DELETE_CUSTOMER_CARE_DETAILS = "deleteCustomerCareDetails";
            /**
             * get CustomerCareDetails
             */
            public static final String ACTION_GET_CUSTOMER_CARE_DETAILS = "getCustomerCareDetails";
            /**
             * get List CustomerCareDetails
             */
            public static final String ACTION_GET_LIST_CUSTOMER_CARE_DETAILS = "getListCustomerCareDetails";
            /**
             * getListCustomerCareDetailsById
             */
            public static final String ACTION_GET_LIST_CUSTOMER_CARE_DETAILS_BY_ID = "getListCustomerCareDetailsById";

        }

        /**
         * Module Customer Care Information
         */
        public class CUSTOMER_CARE_INFORMATION {

            /**
             * Module name
             */
            public static final String NAME = "customercareinformation";
            /**
             * create CustomerCareInformation
             */
            public static final String ACTION_CREATE_CUSTOMER_CARE_INFORMATION = "createCustomerCareInformation";
            /**
             * update CustomerCareInformation
             */
            public static final String ACTION_UPDATE_CUSTOMER_CARE_INFORMATION = "updateCustomerCareInformation";
            /**
             * delete CustomerCareInformation
             */
            public static final String ACTION_DELETE_CUSTOMER_CARE_INFORMATION = "deleteCustomerCareInformation";
            /**
             * get List CustomerCareInformation
             */
            public static final String ACTION_GET_CUSTOMER_CARE_INFORMATION = "getCustomerCareInformation";

            /**
             * get List CustomerCareInformation
             */
            public static final String ACTION_GET_LIST_CUSTOMER_CARE_INFORMATION = "getListCustomerCareInformation";

            /**
             * get List CustomerCareInformation admin
             */
            public static final String ACTION_GET_CUSTOMER_CARE_INFORMATION_ADMIN = "getCustomerCareInformationAdmin";
            /**
             * get List CustomerCareInformation admin
             */
            public static final String ACTION_SEARCH_CUSTOMER_CARE_INFORMATION_ADMIN = "searchCustomerCareInformationAdmin";
            /**
             * get List CustomerCareInformation
             */
            public static final String ACTION_GET_LIST_CUSTOMER_CARE_INFORMATION_BY_POSID = "getListCustomerCareInformationByPosId";

        }

        /**
         * Module Customercare
         *
         */
        public class CUSTOMERCARE {

            /**
             * Module name
             */
            public static final String NAME = "customercare";
        }

        /**
         * Module TableName
         */
        public class TABLENAME {

            /**
             * Module name
             */
            public static final String NAME = "tablename";

            /**
             * get TableName by id
             */
            public static final String ACTION_GET_TABLENAME = "getTableName";

            /**
             * create TableName
             */
            public static final String ACTION_CREATE_TABLENAME = "createTableName";

            /**
             * delete TableName
             */
            public static final String ACTION_DELETE_TABLENAME = "deleteTableName";

            /**
             * update TableName
             */
            public static final String ACTION_UPDATE_TABLENAME = "updateTableName";

            /**
             * get list TableName
             */
            public static final String ACTION_GET_LIST_TABLENAME = "getListTableName";
        }

        /**
         * Module Property
         */
        public class PROPERTY {

            /**
             * Module name
             */
            public static final String NAME = "property";

            /**
             * get Property by id
             */
            public static final String ACTION_GET_PROPERTY = "getProperty";

            /**
             * create Property
             */
            public static final String ACTION_CREATE_PROPERTY = "createProperty";

            /**
             * delete Property
             */
            public static final String ACTION_DELETE_PROPERTY = "deleteProperty";

            /**
             * update Property
             */
            public static final String ACTION_UPDATE_PROPERTY = "updateProperty";

            /**
             * get list Property
             */
            public static final String ACTION_GET_LIST_PROPERTY = "getListProperty";

            /**
             * get Property by TableName
             */
            public static final String ACTION_GET_LIST_PROPERTY_BY_TABLENAME_ID = "getListPropertyByTableNameId";
        }

        /**
         * Module PropertyValue
         */
        public class PROPERTYVALUE {

            /**
             * Module name
             */
            public static final String NAME = "propertyvalue";

            /**
             * get PropertyValue by id
             */
            public static final String ACTION_GET_PROPERTYVALUE = "getPropertyValue";

            /**
             * create PropertyValue
             */
            public static final String ACTION_CREATE_PROPERTYVALUE = "createPropertyValue";

            /**
             * delete PropertyValue
             */
            public static final String ACTION_DELETE_PROPERTYVALUE = "deletePropertyValue";

            /**
             * update PropertyValue
             */
            public static final String ACTION_UPDATE_PROPERTYVALUE = "updatePropertyValue";

            /**
             * get list PropertyValue
             */
            public static final String ACTION_GET_LIST_PROPERTYVALUE = "getListPropertyValue";

            /**
             * get PropertyValue by TableName
             */
            public static final String ACTION_GET_LIST_PROPERTYVALUE_BY_TABLENAME_ID = "getListPropertyValueByTableNameId";

            /**
             * get PropertyValue by Property
             */
            public static final String ACTION_GET_LIST_PROPERTYVALUE_BY_PROPERTY_ID = "getListPropertyValueByPropertyId";
        }

        /**
         * Module StatusType
         */
        public class STATUSTYPE {

            /**
             * Module name
             */
            public static final String NAME = "statustype";

            /**
             * get StatusType by id
             */
            public static final String ACTION_GET_STATUSTYPE = "getStatusType";

            /**
             * create StatusType
             */
            public static final String ACTION_CREATE_STATUSTYPE = "createStatusType";

            /**
             * delete StatusType
             */
            public static final String ACTION_DELETE_STATUSTYPE = "deleteStatusType";

            /**
             * update StatusType
             */
            public static final String ACTION_UPDATE_STATUSTYPE = "updateStatusType";

            /**
             * get list StatusType
             */
            public static final String ACTION_GET_LIST_STATUSTYPE = "getListStatusType";
        }

        /**
         * Module Status
         */
        public class STATUS {

            /**
             * Module name
             */
            public static final String NAME = "status";

            /**
             * get Status by id
             */
            public static final String ACTION_GET_STATUS = "getStatus";

            /**
             * create Status
             */
            public static final String ACTION_CREATE_STATUS = "createStatus";

            /**
             * delete StatusType
             */
            public static final String ACTION_DELETE_STATUS = "deleteStatus";

            /**
             * update Status
             */
            public static final String ACTION_UPDATE_STATUS = "updateStatus";

            /**
             * get list Status
             */
            public static final String ACTION_GET_LIST_STATUS = "getListStatus";

            /**
             * get Status by StatusType
             */
            public static final String ACTION_GET_LIST_STATUS_BY_STATUSTYPE_ID = "getListStatusByStatusTypeId";
            /**
             * get Cb List Status by statusTypeId
             */
            public static final String ACTION_GET_CB_LIST_STATUS_BY_STATUSTYPE_ID = "getCbListStatusByStatusTypeId";

        }

        public class COMPANY {

            /**
             * Module name
             */
            public static final String NAME = "company";

            /**
             * get Company by id
             */
            public static final String ACTION_GET_COMPANY = "getCompany";
            /**
             * create Company
             */
            public static final String ACTION_CREATE_COMPANY = "createCompany";
            
            /**
             * create DEMO Company
             */
            public static final String ACTION_CREATE_DEMO_COMPANY = "createDemoCompany";
            
            /**
             * delete Company
             */
            public static final String ACTION_DELETE_COMPANY = "deleteCompany";
            /**
             * update Company
             */
            public static final String ACTION_UPDATE_COMPANY = "updateCompany";
            /**
             * get List Company
             */
            public static final String ACTION_GET_LIST_COMPANY = "getListCompany";

            /**
             * get List Cb Company
             */
            public static final String ACTION_GET_CB_LIST_COMPANY = "getCbListCompany";

            /**
             * get Branch by CompanyCode
             */
            public static final String ACTION_GET_BRANCH_BY_COMPANY_CODE = "getBranchByCompnayCode";

        }

        public class CHANNELTYPE {

            /**
             * Module name
             */
            public static final String NAME = "channeltype";

            /**
             * get ChannelType by id
             */
            public static final String ACTION_GET_CHANNEL_TYPE = "getChannelType";
            /**
             * create ChannelType
             */
            public static final String ACTION_CREATE_CHANNEL_TYPE = "createChannelType";
            /**
             * delete ChannelType
             */
            public static final String ACTION_DELETE_CHANNEL_TYPE = "deleteChannelType";
            /**
             * update ChannelType
             */
            public static final String ACTION_UPDATE_CHANNEL_TYPE = "updateChannelType";
            /**
             * get List ChannelType
             */
            public static final String ACTION_GET_LIST_CHANNEL_TYPE = "getListChannelType";
            public static final String ACTION_GET_LIST_CHANNEL_TYPE_BY_PARENT = "getListChannelTypeByParent";

            /**
             * get ChannelLocation by channelParent
             */
            public static final String ACTION_GET_CHANNELLOCATION_BY_CHANNEL_PARENT = "getCbListChannelByLocationId";

        }

        public class CHANNEL {

            /**
             * Module name
             */
            public static final String NAME = "channel";

            /**
             * get Channel by id
             */
            public static final String ACTION_GET_CHANNEL = "getChannel";
            public static final String ACTION_GET_CHANNEL_PARENTS = "getListChannelParents";
            /**
             * create Channel
             */
            public static final String ACTION_CREATE_CHANNEL = "createChannel";
            /**
             * delete Channel
             */
            public static final String ACTION_DELETE_CHANNEL = "deleteChannel";
            /**
             * update Channel
             */
            public static final String ACTION_UPDATE_CHANNEL = "updateChannel";
            /**
             * get List Channel
             */
            public static final String ACTION_GET_LIST_CHANNEL = "getListChannel";
            /**
             * get List Channel by ChannelType Id
             */
            public static final String ACTION_GET_LIST_CHANNEL_BY_CHANNEL_TYPE_ID = "getListChannelByChannelTypeId";
            /**
             * get List Channel by Company Id
             */
            public static final String ACTION_GET_LIST_CHANNEL_BY_COMPANY_ID = "getListChannelByCompanyId";
            /**
             * get List Channel by Parent Channel
             */
            public static final String ACTION_GET_LIST_CHANNEL_BY_PARENT_ID = "getListChannelByParentId";
            /**
             * Search Channel
             */
            public static final String ACTION_SEARCH_CHANNEL = "searchChannel";

            /**
             * get Combobox list channel by channelTypeId
             */
            public static final String ACTION_GET_CB_CHANNEL_BY_CHANNEL_TYPE_ID = "getCbListChannelByChannelTypeId";

            /**
             * get Cb list channel by channel and location
             */
            public static final String ACTION_GET_CB_CHANNEL_BY_CHANNEL_AND_LOCATION = "getCbListChannelByChannelAndLocation";
            /**
             * get Combobox list channel by parentId
             */
            public static final String ACTION_GET_CB_LIST_CHANNEL_BY_PARENT_ID = "getCbListChannelByParentId";
            /**
             * get Combobox List Channel By LocationId
             */
            public static final String ACTION_GET_CB_LIST_CHANNEL_BY_LOCATION_ID = "getCbListChannelByLocationId";

        }

        public class POS {

            /**
             * Module name
             */
            public static final String NAME = "pos";

            /**
             * get POS by id
             */
            public static final String ACTION_GET_POS = "getPOS";
            /**
             * create POS
             */
            public static final String ACTION_CREATE_POS = "createPOS";
            /**
             * delete POS
             */
            public static final String ACTION_DELETE_POS = "deletePOS";
            /**
             * update POS
             */
            public static final String ACTION_UPDATE_POS = "updatePOS";
            /**
             * get List POS
             */
            public static final String ACTION_GET_LIST_POS = "getListPOS";
            /**
             * search POS
             */
            public static final String ACTION_SEARCH_POS_DETAILS = "searchPOS";

            /**
             * get List POS by Channel Id
             */
            public static final String ACTION_GET_LIST_POS_BY_CHANNEL_ID = "getListPOSByChannelId";

            /**
             * get List POS by Location Id
             */
            public static final String ACTION_GET_LIST_POS_BY_LOCATION_ID = "getListPOSByLocationId";
            /**
             * get List POS by Location Id
             */
            public static final String ACTION_GET_CB_LIST_POS_BY_LOCATION_ID = "getCbListPOSByLocationId";

            /**
             * update POS Administrator
             */
            public static final String ACTION_UPDATE_POS_ADMINISTRATOR = "activePOS";
            /**
             * search POS
             */
            public static final String ACTION_SEARCH_POS_NEW = "searchPOSNew";
            /**
             * search POS
             */
            public static final String ACTION_SEARCH_POS_ADMIN = "searchPOSAdmin";

            /**
             * get List POS By User and Day
             */
            public static final String ACTION_GET_LIST_POS_BY_USER_AND_DAY = "getListPOSByUserDay";
            
            /**
             * get POS by id
             */
            public static final String ACTION_GET_POS_ORDER = "getPOSOrder";

        }

        public class POS_IMG {

            /**
             * Module name
             */
            public static final String NAME = "posimg";

            /**
             * get POS by id
             */
            public static final String ACTION_GET_POS_IMG = "getPOSImg";
            /**
             * create POSImg
             */
            public static final String ACTION_CREATE_POS_IMG = "createPOSImg";
            /**
             * delete POSImg
             */
            public static final String ACTION_DELETE_POS_IMG = "deletePOSImg";
            /**
             * update POSImg
             */
            public static final String ACTION_UPDATE_POS_IMG = "updatePOSImg";
            /**
             * get List POSImg
             */
            public static final String ACTION_GET_LIST_POS_IMG = "getListPOSImg";
            /**
             * get List POSImg by POSId
             */
            public static final String ACTION_GET_LIST_POS_IMG_BY_POS_ID = "getListPOSImgByPOSId";

        }

        public class SALESTRANS {

            /**
             * Module name
             */
            public static final String NAME = "salestrans";

            /**
             * get SalesTrans by id
             */
            public static final String ACTION_GET_SALES_TRANS = "getSalesTrans";

            /**
             * create SalesTrans
             */
            public static final String ACTION_CREATE_SALES_TRANS = "createSalesTrans";

            /**
             * delete SalesTrans
             */
            public static final String ACTION_DELETE_SALES_TRANS = "deleteSalesTrans";

            /**
             * update SalesTrans
             */
            public static final String ACTION_UPDATE_SALES_TRANS = "updateSalesTrans";

            /**
             * get list SalesTrans by OrderId
             */
            public static final String ACTION_GET_LIST_SALES_TRANS_BY_ORDER_ID = "getListSalesTransByOrderId";

            /**
             * get list SalesTrans by MCPId
             */
            public static final String ACTION_GET_LIST_SALES_TRANS_BY_MCP_ID = "getListSalesTransByMCPId";

            /**
             * get list SalesTrans
             */
            public static final String ACTION_GET_LIST_SALES_TRANS = "getListSalesTrans";
            /**
             * searchSalesTransReceiveReturn
             */
            public static final String ACTION_SEARCH_SALES_TRANS_RECEIVE_RETURN = "searchSalesTransReceiveReturn";
            public static final String ACTION_SEARCH_SALES_TRANS = "searchSalesTrans";
            /**
             * getSalesTransReceiveReturn
             */
            public static final String ACTION_GET_SALES_TRANS_RECEIVE_RETURN = "getSalesTransReceiveReturn";
            /**
             * Canncel and approve SalesTrans
             */
            public static final String ACTION_UPDATE_CANCEL_SALES_TRANS = "updateCancelSalesTrans";

        }

        public class SALESTRANSDETAILS {

            /**
             * Module name
             */
            public static final String NAME = "salestransdetails";

            /**
             * get SalesTransDetail by id
             */
            public static final String ACTION_GET_SALES_TRANS_DETAILS = "getSalesTransDetails";

            /**
             * create SalesTransDetail
             */
            public static final String ACTION_CREATE_SALES_TRANS_DETAILS = "createSalesTransDetails";

            /**
             * delete SalesTransDetail
             */
            public static final String ACTION_DELETE_SALES_TRANS_DETAILS = "deleteSalesTransDetails";

            /**
             * update SalesTransDetail
             */
            public static final String ACTION_UPDATE_SALES_TRANS_DETAILS = "updateSalesTransDetails";

            /**
             * get list SalesTransDetail by GoodsId
             */
            public static final String ACTION_GET_LIST_SALES_TRANS_DETAILS_BY_GOODS_ID = "getListSalesTransDetailsByGoodsId";

            /**
             * get list SalesTransDetail by SaleTransId
             */
            public static final String ACTION_GET_LIST_SALES_TRANS_DETAILS_BY_SALES_TRANS_ID = "getListSalesTransDetailsBySalesTransId";

        }

        public class SALESTRANSSERI {

            /**
             * Module name
             */
            public static final String NAME = "salestransseri";

            /**
             * get SalesTransSeri by id
             */
            public static final String ACTION_GET_SALES_TRANS_SERI = "getSalesTransSeri";

            /**
             * create SalesTransSeri
             */
            public static final String ACTION_CREATE_SALES_TRANS_SERI = "createSalesTransSeri";

            /**
             * delete SalesTransSeri
             */
            public static final String ACTION_DELETE_SALES_TRANS_SERI = "deleteSalesTransSeri";

            /**
             * update SalesTransSeri
             */
            public static final String ACTION_UPDATE_SALES_TRANS_SERI = "updateSalesTransSeri";
            /**
             * get List SalesTransSeri by SalesTransDetailsId
             */
            public static final String ACTION_GET_LIST_SALES_TRANS_SERI_BY_SALES_TRANS_DETAILS_ID = "getListSalesTransSeriBySalesTransDetailsId";
        }

        public class VERSION {

            /**
             * Module name
             */
            public static final String NAME = "version";

            /**
             * get Version by id
             */
            public static final String ACTION_GET_VERSION = "getVersion";

            /**
             * create Version
             */
            public static final String ACTION_CREATE_VERSION = "createVersion";

            /**
             * update Version
             */
            public static final String ACTION_UPDATE_VERSION = "updateVersion";

        }

        public class EQUIPMENT {

            /**
             * Module name
             */
            public static final String NAME = "equipment";

            /**
             * get Equipment by id
             */
            public static final String ACTION_GET_EQUIPMENT = "getEquipment";

            /**
             * create Equipment
             */
            public static final String ACTION_CREATE_EQUIPMENT = "createEquipment";

            /**
             * update Equipment
             */
            public static final String ACTION_UPDATE_EQUIPMENT = "updateEquipment";
            /**
             * delete Equipment
             */
            public static final String ACTION_DELETE_EQUIPMENT = "deleteEquipment";

            /**
             * get List Equipment
             */
            public static final String ACTION_GET_LIST_EQUIPMENT = "getListEquipment";

            /**
             * get List Equipment by CompanyId
             */
            public static final String ACTION_GET_LIST_EQUIPMENT_BY_COMPANY_ID = "getListEquipmentByCompanyId";

            /**
             * get List Equipment by UserId
             */
            public static final String ACTION_GET_LIST_EQUIPMENT_BY_USER_ID = "getListEquipmentByUserId";
            /**
             * block Equipment
             */
            public static final String ACTION_BLOCK_EQUIPMENT = "lockEquipment";
            /**
             * search Equipment
             */
            public static final String ACTION_SEARCH_EQUIPMENT = "searchEquipment";
        }

        public class COMPANYCONFIG {

            /**
             * Module name
             */
            public static final String NAME = "companyconfig";

            /**
             * get CompanyConfig by id
             */
            public static final String ACTION_GET_COMPANY_CONFIG = "getCompanyConfig";

            /**
             * create CompanyConfig
             */
            public static final String ACTION_CREATE_COMPANY_CONFIG = "createCompanyConfig";

            /**
             * update CompanyConfig
             */
            public static final String ACTION_UPDATE_COMPANY_CONFIG = "updateCompanyConfig";
            /**
             * get List CompanyConfig
             */
            public static final String ACTION_GET_LIST_COMPANY_CONFIG = "getListCompanyConfig";
            /**
             * search company_config
             */
            public static final String ACTION_SEARCH_COMPANY_CONFIG = "searchCompanyConfig";
            /**
             * lockCompanyConfig
             */
            public static final String ACTION_LOCK_COMPANY_CONFIG = "lockCompanyConfig";
            /**
             * createCompanyConfigAndDetails
             */
            public static final String ACTION_CREATE_COMPANY_CONFIG_AND_DETAILS = "createCompanyConfigAndDetails";
            /**
             * update company_config_and_details
             */
            public static final String ACTION_UPDATE_COMPANY_CONFIG_AND_DETAILS = "updateCompanyConfigAndDetails";
        }

        public class COMPANYCONFIGDETAILS {

            /**
             * Module name
             */
            public static final String NAME = "companyconfigdetails";

            /**
             * get CompanyConfigDetails by id
             */
            public static final String ACTION_GET_COMPANY_CONFIG_DETAILS = "getCompanyConfigDetails";

            /**
             * create CompanyConfigDetails
             */
            public static final String ACTION_CREATE_COMPANY_CONFIG_DETAILS = "createCompanyConfigDetails";

            /**
             * update CompanyConfigDetails
             */
            public static final String ACTION_UPDATE_COMPANY_CONFIG_DETAILS = "updateCompanyConfigDetails";

            /**
             * update CompanyConfigDetails
             */
            public static final String ACTION_DELETE_COMPANY_CONFIG_DETAILS = "deleteCompanyConfigDetails";

            /**
             * get List CompanyConfigDetails by CompanyConfigId
             */
            public static final String ACTION_GET_LIST_COMPANY_CONFIG_DETAILS_BY_COMPANY_CONFIG_ID = "getListCompanyConfigDetailsByCompanyConfigId";

        }

        public class COMPANYHOLIDAY {

            /**
             * Module name
             */
            public static final String NAME = "companyholiday";

            /**
             * get CompanyHoliday by id
             */
            public static final String ACTION_GET_COMPANY_HOLIDAY = "getCompanyHoliday";

            /**
             * create CompanyHoliday
             */
            public static final String ACTION_CREATE_COMPANY_HOLIDAY = "createCompanyHoliday";

            /**
             * update CompanyHoliday
             */
            public static final String ACTION_UPDATE_COMPANY_HOLIDAY = "updateCompanyHoliday";

            /**
             * delete CompanyHoliday
             */
            public static final String ACTION_DELETE_COMPANY_HOLIDAY = "deleteCompanyHoliday";

            /**
             * get List CompanyHoliday
             */
            public static final String ACTION_GET_LIST_COMPANY_HOLIDAY = "getListCompanyHoliday";
        }

        public class USER_ROLE_CHANNEL {

            /**
             * Module name
             */
            public static final String NAME = "userrolechannel";
            /**
             * get userRoleChannel by id
             */
            public static final String ACTION_GET_USER_ROLE_CHANNEL = "getUserRoleChannel";
            /**
             * get list userRoleChannel by UserId
             */
            public static final String ACTION_GET_LIST_USER_ROLE_CHANNEL_BY_USERID = "getListUserRoleChannelByUserId";
            /**
             * Delete userRoleChannel
             */
            public static final String ACTION_DELETE_USER_ROLE_CHANNEL = "deleteUserRoleChannel";
            /**
             * Update userRoleChannel
             */
            public static final String ACTION_UPDATE_USER_ROLE_CHANNEL = "updateUserRoleChannel";
            /**
             * create userRoleChannel
             */
            public static final String ACTION_CREATE_USER_ROLE_CHANNEL = "createUserRoleChannel";
            /**
             * Get list UserRoleChannel By ChannelId
             */
            public static final String ACTION_GET_LIST_USER_ROLE_CHANNEL_BY_CHANNELID = "getListUserRoleChannelByChannelId";

        }

        /**
         *
         * Module Sales_Stock
         *
         */
        public class SALESSTOCK {

            /**
             * Module name
             */
            public static final String NAME = "salesstock";

            /**
             * get SalesStock by id
             */
            public static final String ACTION_GET_SALES_STOCK = "getSalesStock";
            /**
             * create SalesStock
             */
            public static final String ACTION_CREATE_SALES_STOCK = "createSalesStock";
            /**
             * delete SalesStock
             */
            public static final String ACTION_DELETE_SALES_STOCK = "deleteSalesStock";
            /**
             * update SalesStock
             */
            public static final String ACTION_UPDATE_SALES_STOCK = "updateSalesStock";
            /**
             * get List SalesStock
             */
            public static final String ACTION_GET_LIST_SALES_STOCK = "getListSalesStock";

        }

        /**
         *
         * Module Sales_Stock_Goods
         *
         */
        public class SALESSTOCKGOODS {

            /**
             * Module name
             */
            public static final String NAME = "salesstockgoods";

            /**
             * get SalesStockGoods by id
             */
            public static final String ACTION_GET_SALES_STOCK_GOODS = "getSalesStockGoods";
            /**
             * create SalesStockGoods
             */
            public static final String ACTION_CREATE_SALES_STOCK_GOODS = "createSalesStockGoods";
            /**
             * delete SalesStockGoods
             */
            public static final String ACTION_DELETE_SALES_STOCK_GOODS = "deleteSalesStockGoods";
            /**
             * update SalesStockGoods
             */
            public static final String ACTION_UPDATE_SALES_STOCK_GOODS = "updateSalesStockGoods";
            /**
             * Get List SalesStockGoods By stockId
             */
            public static final String ACTION_GET_LIST_SALES_STOCK_GOODS_BY_STOCKID = "getListSalesStockGoodsByStockId";

            /**
             * Get List SalesStockGoods by goodsId
             */
            public static final String ACTION_GET_LIST_SALES_STOCK_GOODS_BY_GOODSID = "getListSalesStockGoodsByGoodsId";
        }

        /**
         *
         * Module Sales_Stock_Goods_Serial
         *
         */
        public class SALES_STOCK_GOODS_SERIAL {

            /**
             * Module name
             */
            public static final String NAME = "salesstockgoodsserial";

            /**
             * get SalesStockGoodsSerial by id
             */
            public static final String ACTION_GET_SALES_STOCK_GOODS_SERIAL = "getSalesStockGoodsSerial";
            /**
             * create SalesStockGoodsSerial
             */
            public static final String ACTION_CREATE_SALES_STOCK_GOODS_SERIAL = "createSalesStockGoodsSerial";
            /**
             * delete SalesStockGoodsSerial
             */
            public static final String ACTION_DELETE_SALES_STOCK_GOODS_SERIAL = "deleteSalesStockGoodsSerial";
            /**
             * update SalesStockGoodsSerial
             */
            public static final String ACTION_UPDATE_SALES_STOCK_GOODS_SERIAL = "updateSalesStockGoodsSerial";
            /**
             * Get List SalesStockGoodsSerial By stockId
             */
            public static final String ACTION_GET_LIST_SALES_STOCK_GOODS_SERIAL_BY_STOCKID = "getListSalesStockGoodsSerialByStockId";

            /**
             * Get List SalesStockGoodsSerial by goodsId
             */
            public static final String ACTION_GET_LIST_SALES_STOCK_GOODS_SERIAL_BY_GOODSID = "getListSalesStockGoodsSerialByGoodsId";
        }

        /**
         *
         * Module Promotion_Award
         *
         */
        public class PROMOTION_AWARD {

            /**
             * Module name
             */
            public static final String NAME = "promotionaward";

            /**
             * create PromotionAward
             */
            public static final String ACTION_CREATE_PROMOTION_AWARD = "createPromotionAward";

            /**
             * update PromotionAward
             */
            public static final String ACTION_UPDATE_PROMOTION_AWARD = "updatePromotionAward";

            /**
             * delete PromotionAward
             */
            public static final String ACTION_DELETE_PROMOTION_AWARD = "deletePromotionAward";

            /**
             * get PromotionAward
             */
            public static final String ACTION_GET_PROMOTION_AWARD = "getPromotionAward";

            /**
             * Get List PromotionAward
             */
            public static final String ACTION_GET_LIST_PROMOTION_AWARD = "getListPromotionAward";
        }

    }
}
