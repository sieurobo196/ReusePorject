/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.common;

/**
 * @author ChinhNQ
 * @version
 * @since 5 Jun 2015 09:43:03 msales_saas#vn.itt.msales.common.MsalesStatus.java
 * <p>
 */
public enum MsalesStatus {

    UNKNOW(-1111, "Không xác nhận được lỗi."),
    //6xx json invalid
    /**
     * JSON invalid
     */
    JSON_INVALID(600, "Dữ liệu không hợp lệ."),
    JSON_VALUE_INVALID(601, "Dữ liệu chứa giá trị không hợp lệ."),
    /**
     * If request field in JSON is null then get this code and notify to client.
     */
    JSON_FIELD_NULL(602, "The field not exists in JSON"),
    JSON_FIELD_VALUE_NULL(603, "Giá trị của [%s] là bắt buộc."),
    JSON_CONTENTS_EMPTY(604, "Dữ liệu của [contents] rỗng."),
    JSON_ID_NULL(605, "ID là bắt buộc nhập."),
    JSON_FIELD_REQUIRED(606, "Thiếu dữ liệu bắt buộc."),
    JSON_PAGE_REQUIRED(607, "Dữ liệu của [page] là bắt buộc."),
    JSON_UNRECOGNIZED_FILEDS(608, "Không chấp nhận khóa ngoại lai."),
    JSON_UNRECOGNIZED_PROPETYNAME(609, "Không chấp nhận khóa [%s]"),
    JSON_FIELD_INVALID_FORMAT(610, "Lỗi kiểu dữ liệu."),
    JSON_FIELD_IVALID_CONVERT(611, "Không thể chuyển [%s] thành [%s]."),
    //7xx not exist
    /**
     * Field not exists
     */
    NOT_EXISTS_IN_DATABASE(700, "Giá trị không tồn tại."),
    NOT_EXISTS(701, "%s không tồn tại."),
    PARENT_SAME_CHILD(702, "Parent and Child are same."),
    NAME_PROVINCE(703, "Tên tỉnh /TP đã tồn tại"),
    NAME_DISTRICT(704, "Tên Quận / Huyện đã tồn tại"),
    NAME_WARD(705, "Tên Phường /Xã đã tồn tại"),
    CODE_PROVINCE(706, "Mã Tỉnh /TP đã tồn tại"),
    CODE_DISTRICT(707, "Mã Tỉnh /TP đã tồn tại"),
    CODE_WARD(708, "Mã Tỉnh /TP đã tồn tại"),
    //8xx SQL
    /**
     * When attempt access to database with entry have not database.
     */
    SQL_NULL_ENTRY(800, "Không thể thực hiện hành động xóa với dữ liệu rỗng."),
    /**
     * Update to database is fail.
     */
    SQL_UPDATE_FAIL(801, "Không thể cập nhật."),
    SQL_INSERT_FAIL(802, "Không thể tạo."),
    SQL_DELETE_FAIL(803, "Không thể xóa."),
    // 9xx null
    NULL(900, "Giá trị yêu cầu không được rỗng."),
    //1xxx about USER
    USER_PASSWORD_INCORRECT(901, "Mật khẩu không đúng."),
    /**
     * user not exists
     */
    USER_NOT_EXIST(902, "Tài khoản không tồn tại."),
    /**
     * ID user is invalid
     */
    USER_ID_INVALID(903, "ID không hợp lệ."),
    USER_NAME_DUPLICATE(904, "Tài khoản này đã có."),
    REQUEST_FIELD_NULL(905, "Giá trị [%s] là bắt buộc nhập."),
    USER_PASSWORD_NOT_MATCH(906, "Mật khẩu không đúng."),
    ERROR_NAME_DUPLICATE(907, "Tên yêu cầu đã có."),
    PAGE_INVALID(908, " Page or recordsInPage không hợp lệ"),
    USER_LOCK(909, "Tài khoản này đã bị khóa"),
    USER_UNLOCK(910, "Tài khoản này được kích hoạt"),
    EQUIPMENT_LOCK(911, "Thiết bị đã bị khóa"),
    USER_OR_PASSWORD_INCORRECT(912, "Tài khoản không tồn tại hoặc mật khẩu không chính xác."),
    //Lỗi cho API Application 
    APP_TRANS_CODE_NULL(921, "Cần phải có mã giao dịch!"),
    APP_STOCK_GOODS_NULL(922, "Cần phải có chi tiết các mặt hàng!"),
    APP_USER_NO_LOGIN(923, "Bạn chưa đăng nhập, vui lòng đăng nhập để thực hiện giao dịch!"),
    APP_GOODS_NULL(924, "Không có hàng hóa!"),
    APP_GOODS_CATEGORY_NULL(925, "Không có loại sản phẩm!"),
    APP_SALESMAN_NO_HAVE_STOCK(926, "Bạn chưa có kho hàng!"),
    APP_POS_NO_HAVE_STOCK(927, "Điểm bán hàng chưa có kho hàng!"),
    APP_POS_ID_NULL(928, "Vui lòng chọn điểm bán hàng để thực hiện giao dịch!"),
    APP_SALES_TRANS_DATE(929, "Bạn không thể thực hiện giao dịch nếu thiếu ngày giao dịch!"),
    APP_OVER_ORDER_GOODS(930, "Lượng mua quá lớn!"),
    APP_GOODS_NO_HAVE_IN_STOCK(931, "Hàng hóa không có trong kho hoặc số lượng bằng 0!"),
    APP_SALES_ORDER_DETAILS_NULL(932, "Không có chi tiết đơn hàng!"),
    APP_OVER_GOODS(912, "Hàng hóa không có trong kho hàng!"),
    APP_GOODS_UNIT_NULL(933, "Hàng hóa không có đơn vị tính!"),
    APP_ORDER_ID_NULL(934, "Cần phải có id của đơn hàng!"),
    APP_TRANS_CODE_IS_EXIST(935, "Mã giao dịch đã tồn tại!"),
    APP_SALES_TRANS_DETAILS_NULL(937, "Thiếu chi tiết giao dịch hàng hóa!"),
    APP_TRANSACTIONS_FAIL(936, "Đã xảy ra lỗi giao dịch, giao dịch thất bại vui lòng thử lại sau!"),
    APP_SUP_NO_HAVE_EMPLOYEE(938, "Bạn không giám sát nhân viên nào!"),
    APP_SALES_TRANS_DATE_IN_PAST(949, "Ngày giao dịch không thể trong quá khứ!"),
    APP_CHANNEL_NO_HAVE_STOCK(950, "Kênh chưa có kho hàng!"),
    APP_ORDER_INCORRECT(951, "Đơn hàng không tồn tại, bị hủy, hoặc đã giao dịch."),
    APP_ORDER_CHECKED_FAILED(952, "Đơn hàng không thể giao dịch."),
    APP_MCPDETAILS_ISVISITED(953, "Điểm bán hàng đã được chăm sóc."),
    APP_GOODS_NO_HAVE_IN_POS_STOCK(954, "ĐBH không nhập sản phẩm hoặc số lượng bằng 0!"),
    APP_ORDER_NOT_DELIVER(955,"Đơn đặt hàng chưa được giao"),
    APP_NOT_SALESTYPE_SALESTRANS(956,"Đơn hàng không phải loại bán hàng"),
    APP_PROMOTION_NOT_REGISTER(957,"Khuyến mãi chưa đăng ký."),
    APP_COMPANY_CONFIG_KPI_NULL(958,"Công ty chưa có thông số KPI."),
    APP_COMPANY_CONSTRANT_NULL(959,"Công ty chưa có thông số lộ trình."),
    APP_GOODS_ID_NULL(960,"Sản phẩm không tồn tại."),
    APP_PROMOTION_ERROR(961,"Lỗi dữ liệu khuyến mãi."),
    //2xxx about CHANNEL

    //3xxx about CUSTOMERCARE

    //4xxx about GOODS

    //5xxx about SALE

    //6xxx about STORE

    // 1000 Connect to database
    DATABASE_CONNECT_FIAL(1000, "Không thể kết nối đến dữ liệu."),
    BRANCH_ERROR(1001, "[branch] Không hợp lệ."),
    SAVE_FILE_FAIL(1001, "Lỗi lưu file."),
    USER_ROLE_NULL(1002, "User chưa có role."),
    USER_STOCK_NULL(1003, "User chưa có kho hàng."),
    CHANNEL_STOCK_NULL(1004, "Channel chưa có kho hàng."),
    ERROR_LOGIN(1005, "Lỗi login"),
    ERROR_SALES_STOCK(1006, "Lỗi dữ liệu sản phẩm trong Kho hàng."),
    USER_ROLE_FAILED(1007, "Lỗi User Role."),
    EQUIPMENT_NOT_EXIST(1008, "Thiết bị không tồn tại."),
    EQUIPMENT_NOT_REGISTER(1009, "Thiết bị chưa được đăng ký."),
    EQUIPMENT_REGISTERED(1010, "Thiết bị đã được đăng ký."),
    ERROR_MCP_SALES_NULL(1011, "Bạn chưa có chỉ tiêu."),
    ERROR_NOT_YET_RECEIVE(1011, "Bạn chưa nhập hàng."),
    USER_LOCKED(1012, "Tài khoản đang bị khóa."),
    USER_NOT_WORK(1013, "Nhân viên đã nghỉ việc."),
    NOT_TIME_WORK(1013, "Chưa tới làm việc hoặc hết thời gian làm việc."),
    HAD_EXISTED(1014, "Dữ liệu đã tồn tại."),
    USER_REGISTERED_OTHER_DEVICE(1015, "Bạn đã đăng ký thiết bị khác."),
    PROMOTION_NOT_REGISTER(1016, "Khuyến mãi này chưa được đăng ký."),
    COMPANY_EXPIRED(1017, "Công ty đã hết hạn sử dụng."),
    COMPANY_EXPIRED_REGISTER(1018, "Công ty đã hết thời gian trải nghiệm."),
    ERROR_MCP_NULL(1019, "Bạn chưa có tuyến đường."),
    ERROR_MOBILE_ROLE(1020, "Không hỗ trợ quyền %s trên điện thoại."),
    ERROR_MAX_EQUIPMENT(1021, "Vượt quá %s thiết bị Công ty đăng ký.");

    private int code;
    private String message;

    private MsalesStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private MsalesStatus(String message) {
        this.code = -1111;
        this.message = message;
    }

    /**
     * Return the integer value of this status code.
     * <p>
     * @return
     */
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Return the reason phrase of this status code.
     * <p>
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
