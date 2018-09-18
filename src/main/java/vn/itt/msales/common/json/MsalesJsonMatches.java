/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.common.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ChinhNQ
 * @version
 * @since 15 Jun 2015 10:22:03
 * msales#vn.itt.msales.utils.converter.json.MsalesJsonMatches.java
 * <p>
 * This class using check request json from client to server.
 * <p>
 */
public class MsalesJsonMatches {

    private static String regexJsonRequest = "(\\s*\\{\\s*\"branch\"\\s*:\\s*[0-9]*\\s*,\\s*\"contents\"\\s*:\\s*\\{\\s*.{0,}\\s*\\}\\s*\\}\\s*)";

    public static boolean jsonChangePassword(String json) {
        String regexJsonPassword = "\\s*\"id\"\\s*:\\s*[0-9]\\s*,\\s*\"passwordOld\"\\s*:\\s*\"[a-zA-Z0-9]+\"\\s*,\\s*\"passwordNew\"\\s*:\\s*\"[a-zA-Z0-9]+\"\\s*,\\s*\"updatedUser\"\\s*:[0-9]{1,9}\\s*";
        //String jsonPass = String.format(regexJsonRequest, regexJsonPassword);

        Pattern pattern = Pattern.compile(regexJsonPassword);
        Matcher matcher = pattern.matcher(json);
        return matcher.find();
    }

    /**
     * Check JSON invalid request from client.
     * <p>
     * @param jsonString is JSON from client.
     */
    public static boolean checkJsonRequest(String jsonString) {
        Pattern pattern = Pattern.compile(regexJsonRequest);
        Matcher matcher = pattern.matcher(jsonString);
        return matcher.find();
    }
}
