/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.goods.controller.model;

/**
 *
 * @author vtm_2
 */
public class MsalesGoodsUrl {
    private static final String REST_ROOT = "http://115.78.155.77:8080/mSales/";
    /**
     * Branch database
     */
    public static final int BRANCH = 1;
    /**
     * Token login
     */
    public static final String TOKEN = "4ADB0EBF480875917E1F1BF2B47CC8554871327D";

    public static class REST_URL {
        
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
    }
}
