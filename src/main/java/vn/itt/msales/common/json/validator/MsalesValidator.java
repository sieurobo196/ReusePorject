/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.common.json.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ChinhNQ
 */
public class MsalesValidator {

    private static Pattern pattern;
    private static Matcher matcher;

    /**
     * ^#start of the line *[_A-Za-z0-9-\\+]+	# must start with string in the
     * bracket [ ], must contains one or more (+) (	# start of group #1
     * \\.[_A-Za-z0-9-]+	# follow by a dot "." and string in the bracket [ ],
     * must contains one or more (+) )*	# end of group #1, this group is
     * optional (*)
     * <p>
     * @	# must contains a "@" symbol [A-Za-z0-9-]+ # follow by string in the
     * bracket [ ], must contains one or more (+) (	# start of group #2 - first
     * level TLD checking \\.[A-Za-z0-9]+ # follow by a dot "." and string in
     * the bracket [ ], must contains one or more (+) )*	# end of group #2, this
     * group is optional (*) (	# start of group #3 - second level TLD checking
     * \\.[A-Za-z]{2,} # follow by a dot "." and string in the bracket [ ], with
     * minimum length of 2 )	# end of group #3 $	#end of the line
     */
    public static final String EMAIL_PATTERN
            = "((?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\]))|(\\s*)";

    public static final String PHONE_NUMBER = "^(\\s*|[0-9]{8,15})$";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_LONG = "dd/MM/yyyy HH:mm:ss";
    public static final String DATE_FORMAT_SHORT = "dd/MM/yyyy";
    public static final String GMT = "GMT+7";
    public static final String IP_ADDRESS = "(^(?:(?:1\\d?\\d|[1-9]?\\d|2[0-4]\\d|25[0-5])\\.){3}(?:1\\d?\\d|[1-9]?\\d|2[0-4]\\d|25[0-5])$)|(\\s*)";

    public static final String USER_USERNAME_NULL = "Mã nhân viên là bắt buộc.";
    public static final String USER_USERNAME_LENGTH_MIN = "Mã nhân viên không được để rỗng.";
    public static final String USER_CODE_NULL = "Số thẻ là bắt buộc.";
    public static final String USER_CODE_LENGHT_MIN = "Số thẻ phải có ít nhất 3 ký tự.";
    public static final String USER_PASSWORD_NULL = "Mật Khẩu không được để rỗng.";
    public static final String USER_PASSWORD_LENGTH_MIN = "Mật khẩu phải có ít nhất 6 ký tự.";
    public static final String USER_EMAIL_INVALID = "Địa chỉ email không hợp lệ.";
    public static final String USER_PHONE_INVALID = "Số điện thoại không hợp lệ.";
    public static final String USER_IP_ADDRESS = "Địa chỉ IP không hợp lệ.";
    public static final String USER_PASSWORD_OLD_REQUIRED = "Mật khẩu cũ là bắt buộc.";
    public static final String USER_PASSWORD_NEW_REQUIRED = "Mật khẩu mới là bắt buộc.";
    public static final String USER_ID_REQUIRED = "id là bắt buộc.";
    public static final String USER_UPDATEDUSER_REQUIRED = "Người cập nhật là bắt buộc.";

    public static final String USER_LAST_NAME = "Thiếu họ và tên đệm";
    public static final String USER_LAST_NAME_INVALID = "Họ không được để rỗng";
    public static final String USER_FIRST_NAME = "Tên là bắt buộc nhập.";
    public static final String USER_FIRST_NAME_INVALID = "Tên không được để rỗng";
    public static final String USER_IS_ACTIVE = "Trạng thái hoạt động là bắt buộc.";
    public static final String USER_ACTIVE_CODE = "Mã kích hoạt là bắt buộc.";
    public static final String USER_EMPLOYER_USERTYPE = "Chức vụ người dùng là bắt buộc.";
    public static final String USER_EVOUCHER = "Evoucher là bắt buộc.";
    public static final String USER_STATUS = "Trạng thái tài hoản là bắt buộc.";
    public static final String USER_CREATEDUSER = "Người tạo là bắt buộc.";
    public static final String USER_UPDATEDUSER = "Người cập nhật là bắt buộc.";
    public static final String USER_DELETEDUSER = "Người xóa tài khoản là bắt buộc.";
    public static final String USER_LOCATION = "Thông tin vị trí là bắt buộc.";
    public static final String USER_MONTORING_USER = "Thông tin người quản lý là bắt buộc.";
    public static final String USER_IMPLEMENT = "Người thực hiện là bắt buộc.";
    public static final String USER_COMPANY = "Thông tin công ty là bắt buộc";
    public static final String USER_ACTIVE_CODE_NOT_EMPTY = "Mã kích hoạt không được rỗng.";

    /**
     * CuTX validate
     */
    public static final String ERROR_EMPTY = "dữ liệu rỗng , vui lòng nhập lại";
    public static final String ERROR_VALUE_INVALID = "giá trị nhập không đúng , mời nhập lại";

    public static final String CREATED_USER_NULL = "createdUser là bắt buộc.";
    public static final String UPDATED_USER_NULL = "updatedUser là bắt buộc.";
    public static final String DELETED_USER_NULL = "deletedUser là bắt buộc.";

    public static final String NOT_NULL_IN_ARRAY = "thiếu dữ liệu bắt buộc trong danh sách.";
    public static final String NOT_NULL = "là bắt buộc.";
    public static final String ALL_NULL = "phải có ít nhất một biến khác rỗng.";
    public static final String NOT_EMPTY = "không được để rỗng.";
    public static final String NOT_NULL_AND_EMPTY = "là bắt buộc hoặc không được để rỗng.";
    public static final String DOMAIN_USER_NAME_INVALID = "không hợp lệ (name@domain).";
    public static final String NOT_EXIST = "không tồn tại.";
    public static final String HAD_EXISTED = "đã tồn tại.";
    public static final String INVALID = "không hợp lệ.";
    public static final String EQUIPMENT_EXISTED = "Thiết bị đã tồn tại.";
    public static final String EQUIPMENT_IMEI_SUBSCRIBER_ID_WRONG = "Không đúng số IMEI và SubscriberId.";
    public static final String EQUIPMENT_NOT_EXIST = "Thiết bị không tồn tại.";
    public static final String VERSION_GET_ERROR = "Có lỗi lúc lấy thông tin Version.";

    public static final String STATUS_TYPE_STATUSTYPE_EMPTY = "statusType không được để rỗng.";
    public static final String STATUS_TYPE_STATUSTYPE_NULL = "statusType là bắt buộc.";
    public static final String STATUS_TYPE_NAME_EMPTY = "name không được để rỗng.";
    public static final String STATUS_TYPE_NAME_NULL = "name là bắt buộc.";

    public static final String STATUS_STATUSTYPE_ID_NULL = "statusTypeId là bắt buộc.";
    public static final String STATUS_NAME_NULL = "name là bắt buộc.";
    public static final String STATUS_NAME_EMPTY = "name không được để rỗng.";
    public static final String STATUS_VALUE_NULL = "value không được null";
    public static final String STATUS_VALUE_EMPTY = "value không được để rỗng.";

    public static final String TABLENAME_NAME_NULL = "name là bắt buộc.";
    public static final String TABLENAME_NAME_EMTY = "name không được để rỗng.";
    public static final String TABLENAME_CODE_NULL = "code là bắt buộc.";
    public static final String TABLENAME_CODE_EMTY = "code không được để rỗng.";

    public static final String PROPERTY_CODE_NULL = "code là bắt buộc.";
    public static final String PROPERTY_CODE_EMPTY = "code không được để rỗng.";
    public static final String PROPERTY_NAME_NULL = "name là bắt buộc.";
    public static final String PROPERTY_NAME_EMPTY = "name không được để rỗng.";
    public static final String PROPERTY_TABLENAME_ID_NULL = "TableNameId không được null";

    public static final String PROPERTYVALUE_TABLENAME_ID_NULL = "TableNameId là bắt buộc.";
    public static final String PROPERTYVALUE_PROPERTY_ID_NULL = "PropertyId không được null";

    public static final String VERSION_COMPANY_ID_NULL = "companyId là bắt buộc.";
    public static final String VERSION_VERSION_NULL = "version là bắt buộc.";
    public static final String VERSION_VERSION_EMPTY = "version không được rỗng.";
    public static final String VERSION_IS_ACTIVE_NULL = "isActive là bắt buộc.";

    public static final String EQUIPMENT_COMPANY_ID_NULL = "companyId là bắt buộc.";
    //public static final String EQUIPMENT_USER_ID_NULL="userId là bắt buộc.";
    public static final String EQUIPMENT_IS_ACTIVE_NULL = "isActive là bắt buộc.";
    public static final String EQUIPMENT_ACTIVE_DATE_NULL = "activeDate là bắt buộc.";
    public static final String EQUIPMENT_VERSION_NULL = "version là bắt buộc.";
    public static final String EQUIPMENT_VERSION_EMPTY = "version không được rỗng.";
    public static final String EQUIPMENT_IMEI_EMPTY = "imeikhông được rỗng.";
    public static final String EQUIPMENT_IMEI_NULL = "imeilà bắt buộc.";
    public static final String EQUIPMENT_SUBCRIBER_ID_EMPTY = "subcriberId không được rỗng.";
    public static final String EQUIPMENT_SUBCRIBER_ID_NULL = "subcriberId là bắt buộc.";

    public static final String CHANNEL_CODE_NULL = "code là bắt buộc.";
    public static final String CHANNEL_CODE_EMPTY = "code không được rỗng.";
    public static final String CHANNEL_FULL_CODE_NULL = "fullCode là bắt buộc.";
    public static final String CHANNEL_FULL_CODE_EMPTY = "fullCode không được rỗng.";
    public static final String CHANNEL_COMPANY_ID_NULL = "companyId là bắt buộc.";
    public static final String CHANNEL_CHANNELTYPE_ID_NULL = "channelTypeId là bắt buộc.";
    public static final String CHANNEL_IS_SALE_POINT_NULL = "isSalePoint là bắt buộc.";
    public static final String CHANNEL_NAME_NULL = "name là bắt buộc.";
    public static final String CHANNEL_NAME_EMPTY = "name không được rỗng.";
    public static final String CHANNEL_STATUS_ID_NULL = "statusId là bắt buộc.";
    public static final String CHANNEL_TEL_INVALID = "Số điện thoại không hợp lệ.";
    public static final String CHANNEL_FAX_INVALID = "FAX không hợp lệ.";
    public static final String CHANNEL_EMAIL_INVALID = "Email không hợp lệ.";

    public static final String COMPANY_CODE_NULL = "code là bắt buộc.";
    public static final String COMPANY_CODE_EMPTY = "code không được rỗng.";
    public static final String COMPANY_NAME_NULL = "name là bắt buộc.";
    public static final String COMPANY_NAME_EMPTY = "name không được rỗng.";
    public static final String COMPANY_LOCATION_ID_NULL = "locationId là bắt buộc.";
    public static final String COMPANY_STATUS_ID_NULL = "statusId là bắt buộc.";
    public static final String COMPANY_LAT_NULL = "lat là bắt buộc.";
    public static final String COMPANY_LNG_NULL = "lng là bắt buộc.";

    public static final String COMPANY_CONFIG_DETAILS_COMPANY_CONFIG_ID_NULL = "companyConfigId là bắt buộc.";
    public static final String COMPANY_CONFIG_DETAILS_ORDER_ID_NULL = "order là bắt buộc.";
    public static final String COMPANY_CONFIG_DETAILS_CODE_NULL = "code là bắt buộc.";
    public static final String COMPANY_CONFIG_DETAILS_CODE_EMPTY = "code không được rỗng.";
    public static final String COMPANY_CONFIG_DETAILS_ISACTIVE_NULL = "isActive là bắt buộc.";

    public static final String COMPANY_HOLIDAY_COMPANY_ID_NULL = "companyId là bắt buộc.";
    public static final String COMPANY_HOLIDAY_TYPE_NULL = "type là bắt buộc.";
    public static final String COMPANY_HOLIDAY_HOLIDAYDATE_NULL = "holidayDate là bắt buộc.";

    public static final String CHANNELTYPE_CODE_NULL = "code là bắt buộc.";
    public static final String CHANNELTYPE_CODE_EMPTY = "code không được rỗng.";
    public static final String CHANNELTYPE_NAME_NULL = "name là bắt buộc.";
    public static final String CHANNELTYPE_NAME_EMPTY = "name không được rỗng.";

    public static final String POS_POS_CODE_EMPTY = "poscode không được rỗng hoặc null.";
    public static final String POS_NAME_NULL = "name là bắt buộc.";
    public static final String POS_NAME_EMPTY = "name không được rỗng.";
    public static final String POS_CHANNEL_ID_NULL = "channelId là bắt buộc.";
    public static final String POS_ADDRESS_NULL = "address là bắt buộc.";
    public static final String POS_ADDRESS_EMPTY = "address không được rỗng.";
    public static final String POS_STREET_NULL = "street là bắt buộc.";
    public static final String POS_STREET_EMPTY = "street không được rỗng.";
    public static final String POS_LOCATION_ID_NULL = "locationId là bắt buộc.";
    public static final String POS_STATUS_ID_NULL = "statusId là bắt buộc.";
    public static final String POS_HIERARCHY_NULL = "hierarchy là bắt buộc.";
    public static final String POS_LAT_NULL = "Vĩ độ là bắt buộc.";
    public static final String POS_LNG_NULL = "Kinh độ là bắt buộc.";
    public static final String POS_IS_ACTIVE_NULL = "isActive là bắt buộc.";
    public static final String POS_BEGIN_AT = "beginAt là bắt buộc.";
    public static final String POS_END_AT = "endAt là bắt buộc.";

    public static final String POS_IMG_POS_ID_NULL = "posId là bắt buộc.";
    public static final String POS_IMG_PATH_NULL = "path không được null";
    public static final String POS_IMG_PATH_EMPTY = "path không được empty.";
    public static final String POS_IMG_IS_FOCUS_NULL = "isFocus là bắt buộc.";

    public static final String SALES_ORDER_COMPANY_ID_NULL = "companyId là bắt buộc.";

    public static final String SALES_TRANS_TRANS_TYPE_NULL = "transType là bắt buộc.";
    public static final String SALES_TRANS_TRANS_STATUS_NULL = "transStatus là bắt buộc.";
    public static final String SALES_TRANS_SALES_TRANS_DATE_NULL = "salesTransDate là bắt buộc.";
    public static final String SALES_TRANS_COMPANY_ID_NULL = "companyId là bắt buộc.";

    public static final String SALES_TRANS_DETAILS_GOODS_ID_NULL = "goodsId là bắt buộc.";
    public static final String SALES_TRANS_DETAILS_GOODS_UNITS_ID_NULL = "goodsUnitsId là bắt buộc.";
    public static final String SALES_TRANS_DETAILS_SALES_TRANS_ID_NULL = "salesTransId là bắt buộc.";
    public static final String SALES_TRANS_DETAILS_SALES_TRANS_DATE_NULL = "salesTransDate là bắt buộc.";
    public static final String SALES_TRANS_DETAILS_QUANTITY_NULL = "quantity là bắt buộc.";
    public static final String SALES_TRANS_DETAILS_PRICE_NULL = "price là bắt buộc.";
    public static final String SALES_TRANS_DETAILS_IS_FOCUS_NULL = "isFocus là bắt buộc.";

    public static final String SALES_TRANS_SERI_SALES_TRANS_DETAILS_ID_NULL = "salesTransDetailsId là bắt buộc.";
    public static final String SALES_TRANS_SERI_SALES_TRANS_DATE_NULL = "salesTransDate là bắt buộc.";
    public static final String SALES_TRANS_SERI_QUANTITY_NULL = "quantity là bắt buộc.";
    public static final String SALES_TRANS_SERI_FROM_SERIAL_NULL = "fromSerial là bắt buộc.";
    public static final String SALES_TRANS_SERI_FROM_SERIAL_EMPTY = "fromSerial không được rỗng.";
    public static final String SALES_TRANS_SERI_TO_SERIAL_NULL = "toSerial là bắt buộc.";
    public static final String SALES_TRANS_SERI_TO_SERIAL_EMPTY = "toSerial không được rỗng.";

    public static final String COMPANY_CONFIG_COMPANY_ID_NULL = "companyId là bắt buộc.";
    public static final String COMPANY_CONFIG_NAME_NULL = "name là bắt buộc.";
    public static final String COMPANY_CONFIG_NAME_EMPTY = "name không được rỗng.";
    public static final String COMPANY_CONFIG_IS_ACTIVE_NULL = "isActive là bắt buộc.";
    public static final String COMPANY_CONFIG_USER_ROLE_ID_NULL = "userRoleId là bắt buộc.";

    public static final String USERROUTE_LAT_NULL = "lat là bắt buộc.";
    public static final String USERROUTE_LNG_NULL = "lng là bắt buộc.";
    public static final String USERROUTE_ROUTE_AT_NULL = "routeAt là bắt buộc.";
    public static final String USERROUTE_USER_ID_NULL = "userId là bắt buộc.";

    public static final String GOODS_GOODSCODE_NULL = "goodsCode không được null";
    public static final String GOODS_GOODSCODE_EMPTY = "goodsCode không được rỗng hoặc null";
    public static final String STATUS_NULL = "statusId không được null";
    public static final String GOODS_GOODS_CATEGORY_ID_NULL = "goodsCategoryId không được null";
    public static final String GOODS_IS_RECOVER_NULL = "isRecover không được null";
    public static final String PRICE_NULL = "price không được null";
    public static final String NAME_NULL = "name không được null";
    public static final String GOODS_FACTOR_NULL = "factor không được null";
    public static final String GOODS_IS_FOCUS = "isFocus không được null";
    public static final String GOODS_PRICE = "Giá sản phẩm không được nhỏ hơn 0";

    public static final String GOODS_CATEGORY_COMPANY_ID_NULL = "companyId không được null";
    public static final String GOODS_UNIT_COMPANY_ID_NULL = "companyId không được null";

    public static final String UNIT_ORDER_NOT_NULL = "order là bắt buộc.";

    public static final String USER_GOODS_CATEGORY_STATUS_ID_NULL = "statusId là bắt buộc.";
    public static final String USER_GOODS_CATEGORY_USER_ID_NULL = "userId là bắt buộc.";
    public static final String USER_GOODS_CATEGORY_GOODS_CATEGORY_ID_NULL = "goodsCategoryId là bắt buộc.";

    public static final String GOODS_UNIT_GOODS_ID_NULL = "goodsId không được null";
    public static final String GOODS_UNIT_UNIT_ID_NULL = "UnitId không được null";
    public static final String GOODS_UNIT_QUANTITY_NULL = "quantity không được null";
    public static final String GOODS_UNIT_IS_ACTIVE_NULL = "isActive không được null";
    public static final String GOOGS_UNIT_NOTE_MAX_LENGTH = "Note tối đa là 200 kí tự";

    public static final String LOCATION_CODE_NULL = "code không được null";
    public static final String LOCATION_CODE_EMPTY = "code không được rỗng hoặc null";
    public static final String LOCATION_CODE_MAX_LENGTH = "code có chiều dài tối đa là 100 kí tự";
    public static final String LOCATION_NAME_MAX_LENGTH = "name có chiều dài tối đa là 100 kí tự";
    public static final String LOCATION_LOCATIONTYPE_NULL = "LocationType không được null";
    public static final String LOCATION_LOCATIONTYPE_MAX_VALUE = "LocationType có giá trị từ 1 tới 4";
    public static final String LOCATION_LAT_NULL = "lat không được null";
    public static final String LOCATION_LNG_NULL = "lng không được null";

    public static final String CHANNEL_LOCATION_LOCATION_ID_NULL = "locationId không được null";
    public static final String CHANNEL_LOCATION_CHANNEL_ID_NULL = "Channelid không được null";

    public static final String USER_ROLE_NAME_NULL = "name không được rỗng hoặc null";
    public static final String USER_ROLE_NAME_MAX_VALUE = "name có chiều dài tối đa là 100 kí tự";

    public static final String USER_ROLE_CHANNEL_USER_ID_NULL = "userId không được null";
    public static final String USER_ROLE_CHANNEL_USER_ROLE_ID_NULL = "userRoleId không được null";
    public static final String USER_ROLE_CHANNEL_CHANNEL_ID_NULL = "channelId không được null";

    public static final String SALES_STOCK_GOODS_STOCK_ID_NULL = "stockId không được null";
    public static final String SALES_STOCK_GOODS_GOODS_ID_NULL = "goodsId không được null";
    public static final String SALES_STOCK_GOODS_GOODS_STATUS_ID_NULL = "goodsStatusId không được null";
    public static final String SALES_STOCK_GOODS_IS_ACTIVE_NULL = "isActive không được null";
    public static final String SALES_STOCK_SALESMANUSERID_CHANNELID = "Trong 2 trường salemanUserId và channelId chỉ tồn tại một trường";
    public static final String SALES_STOCK_SALESMANUSERID_POS = "Trong 2 trường salemanUserId và posId chỉ tồn tại một trường";
    public static final String SALES_STOCK_CHANNEL_ID_POS = "Trong 2 trường channelId và posId chỉ tồn tại một trường";
    public static final String SALES_STOCK_THREE_FIELD_NOT_NULL = "Trong 3 trường salemanUserId, channelId và posId chỉ tồn tại một trường";
    public static final String SALES_STOCK_THREE_FIELD_NULL = "Trong 3 trường salemanUserId, channelId và posId phải tồn tại một trường";

    //Validator cho loi APP
    //Receive_Goods_App
    public static final String APP_TRANS_CODE_IS_EXISTS = "Mã giao dịch đã tồn tại";
    public static final String APP_GOODS_NULL = "Bạn không thể giao dịch khi không có hàng hóa";
    public static final String APP_GOODS_CATEGORY_NULL = "Bạn không thể giao dịch khi chưa có loại hàng hóa";
    public static final String APP_SALESMAN_NO_HAVE_STOCK = "Bạn không thể giao dịch vì chưa có kho hàng";
    public static final String APP_POS_NO_HAVE_STOCK = "Điểm bán hàng không có kho hàng";
    public static final String APP_USER_GOODS_CATEGORY = "Bạn không được phân công bán loại sản phẩm, bạn có quyền bán tất cả các loại sản phẩm";
    public static final String APP_OVER_ORDER_GOODS = "Không có đủ số lượng hàng trong kho";
    public static final String APP_OVER_GOODS_POS = "Bạn đã nhập vượt quá số lượng đã bán";
    public static final String APP_POS_NOT_RECEIVE_GOODS = "ĐBH không nhập sản phẩm";
    public static final String APP_SALESMAN_NOT_RECEIVE_GOODS = "Bạn không nhập sản phẩm";
    public static final String APP_GOODS_NO_HAVE_IN_STOCK = "Không có trong kho hàng";
    public static final String APP_POS_ID_EMPTY_NULL = "Id của điểm bán hàng không được bỏ trống";
    public static final String APP_POS_ID_NOT_NUMBER = "Id của điểm bán hàng là số tự nhiên";
    public static final String APP_SALES_ORDER_DETAILS_NULL = "Đơn hàng không có danh sách số liệu chi tiết đơn hàng";
    public static final String APP_OVER_GOODS = " : không đủ cung cấp cho đơn hàng";
    public static final String APP_GOODS_UNIT_NULL = "Hàng hóa không có đơn vị tính, không thể tính được giá của đơn hàng";
    public static final String APP_ORDER_ID_NOT_EXIST = "Không có đơn hàng nào có id = ";
    public static final String APP_RETURN_GOODS_TYPE_ERROR = "chỉ từ 1 - 3";

    //Validator for Administrator of DuanND
    //V.Quản lí nhân viên bán hàng
    public static final String MCP_ID_NOT_EXIST = "Không có kế hoạch nào được tìm thấy với id = ";
    public static final String MCP_SEARCH_MCP_AND_DETAILS_TYPE_IS_2 = "Kế hoạch bán hàng có type = 2";
    public static final String MCP_SEARCH_MCP__TYPE_IS_1 = "Kế hoạch chăm sóc khách hàng có type = 1";
    public static final String MCP_USER_NOT_EXIST = "Không tìm thấy người dùng có id = ";
    public static final String MCP_POS_NOT_EXIST = "Không tìm thấy điểm bán hàng có id = ";
    public static final String MCP_STATUS_NOT_EXIST = "Không tồn tại trạng thái có id = ";
    public static final String MCP_MCP_DETAILS_NULL = "Cần phải có chi tiết kế hoạch chăm sóc khách hàng (mcpDetailss)!";
    public static final String MCP_GOODS_ID_NOT_EXIST = "Không tìm thấy hàng hóa có id = ";
    public static final String MCP_UNIT_ID_NOT_EXIST = "Không tồn tại đơn vị hàng hóa có id = ";
    public static final String MCP_MCP_SALES_DETAILS_NULL = "Cần phải có kế hoạch chi tiết bán hàng(mcpSalesDetailss)!";
    public static final String MCP_MCP_ID_NOT_EXIST = "Không tồn tại kế hoạch có id = ";
    public static final String MCP_POS_ID_NULL = "Cần phải có id của điểm bán hàng!";
    public static final String MCP_POS_HIERATRY_NULL = "Cần phải có thông tin của hierarchy!";
    public static final String MCP_GOODS_CATEGORY_NOT_EXIST = "Không tìm thấy loại hàng hóa có id = ";
    public static final String MCP_GOODS_CATEGORY_ID_NULL = "Cần phải có id của loại hàng hóa!";
    public static final String MCP_STATUS_NULL = "Cần phải có thông tin id của trạng thái!";
    public static final String DK_GOODS_NAME_NOT_EXIST = "Tên hợp lệ không được chứa toàn khoảng trắng hoặc rỗng!";
    public static final String DK_GOODS_CODE_NOT_EXIST = "Mã hợp lệ không được chứa toàn khoảng trắng hoặc rỗng!";
    public static final String MCP_GOODS_ID_NULL = "Cần phải có thông tin id của hàng hóa!";
    public static final String MCP_UNIT_ID_NULL = "Cần phải có thông tin id của đơn vị hàng hóa!";
    public static final String DK_USER_ROLE_ID_NOT_EXIST = "Không tìm thấy vai trò người dùng với id = ";
    public static final String DK_USER_ROLE_ID_NULL = "Cần có thông tin id của vai trò người dùng!";
    public static final String DK_USER_ROLE_CHANNEL_ID_NOT_EXIST = "Không tìm thấy thông tin vai trò người dùng đối với kênh có id = ";
    public static final String DK_CHANNEL_ID_NOT_EXIST = "Không tìm thấy thông tin của kênh có id = ";
    public static final String DK_LOCATION_ID_NOT_EXIST = "Không tìm thấy địa điểm có id = ";
    public static final String DK_LOCATION_ID_NULL = "Cần nhập thông tin id của đia điểm!";
    public static final String DK_LOCATION_TYPE_NULL = "Cần nhập thông tin loại địa điểm!";
    public static final String DK_CHANNEL_LOCATION_ID_NOT_EXIST = "Không tìm thấy thông tin Địa điểm - kênh với id = ";
    public static final String DK_CHANNEL_LOCATION_ID_NULL = "Cần nhập thông tin id của Địa điểm - kênh!";
    public static final String DK_CHANNEL_ID_NULL = "Cần nhập thông tin id của Kênh!";
    public static final String DK_SALES_STOCK_ID_NOT_EXIST = "Không tìm thấy thông tin kho hàng với id = ";
    public static final String DK_SALES_STOCK_ID_NULL = "Cần nhâp thông tin id của kho hàng!";
    public static final String DK_SALES_STOCK_GOODS_ID_NOT_EXIST = "Không tìm thấy thông tin chi tiết hàng hóa trong kho với id = ";
    public static final String DK_SALES_STOCK_GOODS_ID_NULL = "Cần nhập thông tin id của chi tiết hàng hóa trong kho!";
    public static final String SALES_TRANS_DATE_IS_PAST = "Ngày giao dịch không thể trong quá khứ!";
    public static final String ERORR_MESSAGE = "Đã xảy ra lỗi, đặt hàng không thành công!";
    public static final String SUCCESS_MESSAGE = "Đặt hàng thành công!";
    public static final String POS_ID_NOT_EXIST = "Điểm bán hàng không tồn tại!";
    public static final String POS_NOT_EMPLOYEE_CARE = "Điểm bán hàng không có nhân viên chăm sóc!";
    
    public static final String WORKFLOW_CODE_NOT_NULL = "code là bắt buộc.";
    public static final String WORKFLOW_CODE_NOT_EMPTY = "code không được để trống.";
    public static final String CUSTOMER_CARE_DETAILS_WORKFLOW_DETAILS_NULL = "workflowDetailsId code là bắt buộc.";
    public static final String WORKFLOW_TYPE_CODE_NOT_NULL = "code là bắt buộc.";
    public static final String WORKFLOW_TYPE_CODE_NOT_EMPTY = "code không được để trống.";
    
    public static final String PROMOTION_APP_NOT_COMPLETED = " chưa hoàn thành.";
    

    /**
     * Validate hex with regular expression
     * <p>
     * @param hex hex for validation
     * <p>
     * @return true valid hex, false invalid hex
     */
    public static boolean validate(final String hex) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }
}
