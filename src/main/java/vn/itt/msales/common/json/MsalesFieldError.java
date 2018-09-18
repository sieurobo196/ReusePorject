/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.common.json;

/**
 * @author ChinhNQ
 * @version
 * @since 1 Jun 2015 18:04:50
 * msales#com.itt.msales.utils.converter.json.FieldError.java
 *
 */
public class MsalesFieldError {
    private String field;
    
    private String message;
 
    public MsalesFieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
}
