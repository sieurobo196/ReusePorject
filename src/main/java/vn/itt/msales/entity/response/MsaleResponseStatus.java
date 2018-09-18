/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.entity.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import vn.itt.msales.common.MsalesStatus;

/**
 * @author ChinhNQ
 * @version
 * @since 4 Jun 2015 09:35:37
 *        msales_saas#vn.itt.msales.entity.ResponseStatus.java
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MsaleResponseStatus{

    private int code;

    private String message;
    private Object messageDetails;

    public MsaleResponseStatus() {
    }

    public MsaleResponseStatus(int code, String message, Object messageDetails) {
        this.code = code;
        this.message = message;
        this.messageDetails = messageDetails;
    }
    
    
    

    public MsaleResponseStatus(Enum<?> status) {
        super();
        init(status);
    }

    private void init(Enum<?> status) {
        if (status instanceof MsalesStatus) {
            MsalesStatus msalesStatus = (MsalesStatus) status;
            this.code = msalesStatus.getCode();
            this.message = msalesStatus.getMessage();
        } else if (status instanceof HttpStatus) {
            HttpStatus httpStatus = (HttpStatus) status;
            this.code = httpStatus.value();
            this.message = httpStatus.getReasonPhrase();
        }
    }
    /**
     * Initialization a message with field.
     * @param msalesStatus is message
     * @param fieldNotify is field
     */
    public MsaleResponseStatus(MsalesStatus msalesStatus, String fieldNotify) {
        super();
        this.code = msalesStatus.getCode();
        this.message = String.format(msalesStatus.getMessage(), fieldNotify);
    }
    
//    /**
//     * Initialization msales status error code.
//     * @param httpStatus is Java 5 enumeration of HTTP status codes.
//     *
//     */
//    public MsaleResponseStatus(HttpStatus httpStatus) {
//        super();
//        this.code = httpStatus.value();
//        this.message = httpStatus.getReasonPhrase();
//    }
    
    public MsaleResponseStatus(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public MsaleResponseStatus(Enum<?> status, Object messageDetails) {
        init(status);
        this.messageDetails = messageDetails;
    }

    public Object getMessageDetails() {
        return messageDetails;
    }

    public void setMessageDetails(Object messageDetails) {
        this.messageDetails = messageDetails;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
