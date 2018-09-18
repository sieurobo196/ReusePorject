/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.logex;

import vn.itt.msales.dao.MsalesJsonValidator;

/**
 * @author ChinhNQ
 * @version
 * @since 27 May 2015 13:28:37
 */
public class MSalesException extends RuntimeException {

    private static final long serialVersionUID = 5986673168654382149L;
    private String mErrorCode = "Unknown_Exception";

    public MSalesException() {
        super();
    }

    public MSalesException(String message) {
        super(message);
        MSalesLogger.error(message, "");
    }

    public MSalesException(String message, Throwable cause) {
        super(message, cause);
        MSalesLogger.error(message, "");
    }

    public MSalesException(Throwable cause) {
        super(cause);
    }

    public MSalesException(Throwable cause, MsalesJsonValidator msalesJsonValidator) {
        super(cause);
        msalesJsonValidator.jsonValidator(this);
    }

    protected MSalesException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        MSalesLogger.error(message, "");
    }

    /**
     * Initialization Exception message and code.
     * <p>
     * @param message   is error message
     * @param errorCode is error code
     */
    public MSalesException(Class<?> classs, String message, String errorCode) {
        super(message);
        this.mErrorCode = errorCode;
        MSalesLogger.error(classs.getSimpleName() + "#" + message, errorCode);
    }

    /**
     * get error code
     */
    public String getErrorCode() {
        return this.mErrorCode;
    }

    @Override
    public void printStackTrace() {
        MSalesLogger.error(getMessage(), getErrorCode());
    }
}
