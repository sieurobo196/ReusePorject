/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.logex;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import vn.itt.msales.common.MsalesConstants;

/**
 * This class we are using for log manager.
 * <p>
 * @author ChinhNQ
 * @version
 * @since 27 May 2015 13:28:37
 */
public class MSalesLogger extends Logger {

    private final static Logger logger = Logger.getLogger(MSalesLogger.class);
    private static String mCode = "Unknown";

    protected MSalesLogger(String message) {
        super(message);
    }

    /**
     * Check whether this category is enabled for the <code>DEBUG</code> Level.
     * this variable get from {@link ErrorConstants#DEBUG}
     */
    @Override
    public boolean isDebugEnabled() {
        return MsalesConstants.DEBUG;
    }

    /**
     * Log a message object with the {@link Level#INFO INFO} Level.
     * <p>
     * @param message is message you want print to consoler
     * @param code    is code log
     */
    public static void info(String message, String code) {
        if (logger.isDebugEnabled()) {
            logger.info(printLog(message, code));
        }

    }

    /**
     * Log a message object with the {@link Level#INFO INFO} Level.
     * <p>
     * @param message is message you want print to consoler
     */
    public static void info(String message) {
        info(message, mCode);
    }

    /**
     * Log a message object with the {@link Level#ERROR ERROR} Level.
     * <p>
     * @param message is message you want print to consoler
     * @param code    is code log
     */
    public static void error(String message, String code) {
        if (logger.isDebugEnabled()) {
            logger.error(printLog(message, code));
        }
    }

    /**
     * Log a message object with the {@link Level#ERROR ERROR} Level.
     * <p>
     * @param message is message you want print to consoler
     */
    public static void error(String message) {
        error(message, mCode);
    }

    /**
     * Log a message object with the {@link Level#DEBUG DEBUG} Level.
     * <p>
     * @param message is message you want print to consoler
     * @param code    is code log
     */
    public static void debug(String message, String code) {
        if (logger.isDebugEnabled()) {
            logger.debug(printLog(message, code));
        }
    }

    /**
     * Log a message object with the {@link Level#DEBUG DEBUG} Level.
     * <p>
     * @param message is message you want print to consoler
     */
    public static void debug(String message) {
        debug(message, mCode);
    }

    /**
     * Log a message object with the {@link Level#WARN WARN} Level.
     * <p>
     * @param message is message you want print to consoler
     * @param code    is code log
     */
    public static void warning(String message, String code) {
        if (logger.isDebugEnabled()) {
            logger.warn(printLog(message, code));
        }
    }

    /**
     * Log a message object with the {@link Level#WARN WARN} Level.
     * <p>
     * @param message is message you want print to consoler
     */
    public static void warning(String message) {
        warning(message, mCode);
    }

    /**
     * Print log follow format
     * <p>
     * @param message is error message
     * @param code    is error code
     */
    private static String printLog(String message, String code) {
        return String.format("Message: %s, ErrorCode: %s", message, code);
    }
}
