/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.csb.auth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ChinhNQ
 */
public class MsalesSession {

    private static char[] hexits = "0123456789ABCDEF".toCharArray();
    private static final String SALT = "258195";
    private static final String PIN = "123654";
    public static final String DEFAULT_USER_PASS = "123456";

    public static String getAccessToken(String sessionID) {
        return getSHA256(sessionID + getSHA256(SALT + PIN));
    }

    /**
     * This method create access token from session id.
     * <p>
     * @param data is session id
     * <p>
     * @return access token
     */
    public static String getSHA256(String data) {
        try {
            // the array of bytes for the resulting hash value.
            byte[] ba = null;
            // Creates a message digest with the specified algorithm name
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Updates the digest using UTF-8.
            md.update(data.getBytes("UTF-8"));
            // Completes the hash computation by performing final operations
            // such as padding. The digest is reset after this call is made.
            ba = md.digest();
            // Create a StringBuffer to hold our computation.
            StringBuffer sb = new StringBuffer(ba.length * 2);
            // Putting this all together we can do it as a for loop and convert the entire array
            for (int i = 0; i < ba.length; i++) {
                // we need to isolate the lower order bits.
                // Since the bits we want are already in the correct position
                sb.append(hexits[(((int) ba[i] & 0xFF) / 16) & 0x0F]);
                sb.append(hexits[((int) ba[i] & 0xFF) % 16]);
            }
            // convert hex string buffer to String.
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(MsalesSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    /**
     * Returns true if the session is expired.
     * <p>
     * @param lastAccessTime is the last time this {@link Session} was accessed
     *                       expressed in milliseconds since midnight of
     *                       1/1/1970 GMT
     * @param max            is the maximum inactive interval in seconds between
     *                       requests before this session will be invalidated. A
     *                       negative time indicates that the session will never
     *                       timeout.
     * <p>
     * @return true if the session is expired, else false.
     */
    public static boolean isExpired(long lastAccessTime, long max) {
        return (System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(max)) >= lastAccessTime;
    }
}
