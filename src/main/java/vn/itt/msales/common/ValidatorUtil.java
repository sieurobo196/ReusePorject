package vn.itt.msales.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ValidatorUtil {

    public static Date parseLocalDate(String sDate) {
        Date result = null;
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            result = df.parse(sDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static String parseParamaterDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }

    public static String parseParameterDateTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return df.format(date);
    }
    
    public static String parseParameterDateTimeDB(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }
    
    public static Date parseParameterDateTime(String sDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            return df.parse(sDate);
        } catch (ParseException ex) {
            Logger.getLogger(ValidatorUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String getNowTime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(new Date());
    }

    public static Date parseParamaterDate(String sDate) {
        Date result = null;
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            result = df.parse(sDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isValidDate(String input) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            format.parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(String str) {
        if (null != str && !str.trim().isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isEmptyNumber(List<? extends Number> idList) {
        if (null != idList && !idList.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(List<String> idList) {
        if (null != idList && !idList.isEmpty()) {
            return true;
        }
        return false;
    }

    public static String currentDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

}
