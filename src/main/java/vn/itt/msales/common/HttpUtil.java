package vn.itt.msales.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

public class HttpUtil {

	public static int getRequestParammeter(HttpServletRequest request, String name, int defaultValue) {
		int result = defaultValue;
		try {
			result = Integer.parseInt(request.getParameter(name));
		} catch (Exception e) {
		}
		return result;
	}
	
	public static double getRequestParammeter(HttpServletRequest request, String name, double defaultValue) {
		double result = defaultValue;
		try {
			result = Double.parseDouble(request.getParameter(name));
		} catch (Exception e) {
		}
		return result;
	}

	public static long getRequestParammeter(HttpServletRequest request, String name, long defaultValue) {
		long result = defaultValue;
		try {
			result = Long.parseLong(request.getParameter(name));
		} catch (Exception e) {
		}
		return result;
	}

	public static String getRequestParammeter(HttpServletRequest request, String name, String defaultValue) {
		String result = request.getParameter(name);
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	public static Date getRequestDateParammeter(HttpServletRequest request, String name, String dateFormat) {
		Date result = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat(dateFormat);
			result = df.parse(request.getParameter(name));
		} catch (Exception e) {
		}
		return result;
	}
}
