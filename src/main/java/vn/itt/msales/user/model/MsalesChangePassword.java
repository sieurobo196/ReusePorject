/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.user.model;

import java.util.LinkedHashMap;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import vn.itt.msales.common.json.validator.MsalesValidator;

/**
 *
 * @author ChinhNQ
 */
public class MsalesChangePassword {

    @NotEmpty(message = "Bạn quên nhập mật khẩu đang sử dụng.")
    @Length(min = 6, message = "Mật khẩu mới phải có từ 6 ký tự trở lên.")
    private String passwordOld;

    @NotEmpty(message = "Mật khẩu mới không được rỗng.")
    @Length(min = 6, message = "Mật khẩu mới phải có từ 6 ký tự trở lên.")
    private String passwordNew;
    private Integer id;
    private Integer updatedUser;

    public MsalesChangePassword() {
    }

    public MsalesChangePassword(String passwordOld, String passwordNew, Integer id, Integer updatedUser) {
        this.passwordOld = passwordOld;
        this.passwordNew = passwordNew;
        this.id = id;
        this.updatedUser = updatedUser;
    }

    public String getPasswordOld() {
        return passwordOld;
    }

    public void setPasswordOld(String passwordOld) {
        this.passwordOld = passwordOld;
    }

    public String getPasswordNew() {
        return passwordNew;
    }

    public void setPasswordNew(String passwordNew) {
        this.passwordNew = passwordNew;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(Integer updatedUser) {
        this.updatedUser = updatedUser;
    }

    public LinkedHashMap<String, String> checkFiled() {
        LinkedHashMap<String, String> maps = new LinkedHashMap<>();
        if (id == null) {
            maps.put("id", MsalesValidator.USER_ID_REQUIRED);
        }
        if (passwordOld == null) {
            maps.put("passwordOld", MsalesValidator.USER_PASSWORD_OLD_REQUIRED);
        }
        if (passwordNew == null) {
            maps.put("passwordNew", MsalesValidator.USER_PASSWORD_NEW_REQUIRED);
        }
        if (updatedUser == null) {
            maps.put("updatedUser", MsalesValidator.USER_UPDATEDUSER_REQUIRED);
        }
        if (maps.isEmpty()) {
            return null;
        }
        return maps;
    }
}
