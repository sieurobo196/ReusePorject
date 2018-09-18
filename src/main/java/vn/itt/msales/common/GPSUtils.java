package vn.itt.msales.common;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.round;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.ui.Model;




public class GPSUtils {
	public static String USER_VIEW=" nguoi_su_dung_location ";
	public final static String pathProperties = "webapps"+System.getProperty("file.separator")+"salesmgtwebapps"+System.getProperty("file.separator")+"WEB-INFwebapps"+System.getProperty("file.separator")+"system.properties";
	/**
	 * Tinh khoang cach giua 2 diem tren Trai Dat theo luat Haversine.
	 * viDo1,viDo2,kinhDo1,kinhDo2 tinh bang do.
	 */
	public static Integer distance(Double viDo1, Double kinhDo1, Double viDo2, Double kinhDo2) {
		if (viDo1==null||viDo2==null||kinhDo1==null|kinhDo2==null) return null;
		double rad = 0.01745329252;
		double earthRadius = 6371000;
		//doi don vi qua radians
		double f1=rad*viDo1;//toRadians(viDo1);
		double f2=rad*viDo2;//toRadians(viDo2);
		double l1=rad*kinhDo1;//toRadians(kinhDo1);
		double l2=rad*kinhDo2;//toRadians(kinhDo2);
		double hcos=cos(f2-f1)-cos(f2)*cos(f1)*(1-cos(l2-l1));
		return (int) round(earthRadius*acos(hcos));
	}
	
	
	public static Date convertStringToDate(String strDate, String format){
		 DateFormat formatter = new SimpleDateFormat(format);
		 Date date ;
		 try {
			 date = (Date)formatter.parse(strDate);
			 return date;
		 } 
		 catch (ParseException e) {
			 System.out.println("convertStringToDate : Exception :"+e);  
		}  
		 return null;
	}
	
	public static String convertStringDateToStringDate(String strDate){
		try {
			Date date= GPSUtils.convertStringToDate(strDate,"yyyyMMddHHmmss");
			SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
			return df.format(date);
		}
		catch (Exception e) {
			 System.out.println("convertStringToDate : Exception :"+e); 
			 return null;
		}  
	}
	
	public static boolean isNumeric(String str) {
		try {
			@SuppressWarnings("unused")
			Double d = Double.valueOf(str);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public static String numberFormat(long value ) {
		String pattern = "###,###";
		
	    DecimalFormat myFormatter = new DecimalFormat(pattern);
	    String output = myFormatter.format(value);
	    return output;
	 }
	
	public static int getDaysOfMonth(String month){
		int days = 0;
		
		try {
			Date date= GPSUtils.convertStringToDate(month,"MM-yyyy");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			return days;
		}
		catch (Exception e) {
			 System.out.println("convertStringToDate : Exception :"+e); 
			 return 0;
		} 
		
		
	}
	
	
	public static String getParamSystemProperties(String param){
		String value = "";
		Properties prop = new Properties();
		 
		try {
	           //load a properties file
				prop.load(new FileInputStream(pathProperties));
	
	           //get the property value and print it out
				value = prop.getProperty(param);
	    } catch (IOException ex) {
			ex.printStackTrace();
			return value ;
	    }
		return value;
	} 
	
	
	public static String convertTenToMaDangNhap(String ten){
		if((ten==null)||(ten.length()==0))
			return "";
		ten = ten.toLowerCase();
		 String delims = "[ ]+";
		 String[] tokens = ten.split(delims);
		 
		 String mdn = "" ;
		 if((tokens!=null)&&(tokens.length>0)){
			  for(int i=0; i<tokens.length; i++){
				  if(i==tokens.length-1){
					  mdn = tokens[i]+mdn;
				  } else {
					  if((tokens[i]!=null)&&(tokens[i].length()>0))
					  mdn += tokens[i].charAt(0);
				  }
				  
			  }
		 }
		 return mdn;
	}
	
	public static int CompareDate(String fromDate, String toDate){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if((fromDate==null)&&(toDate==null)){
			return -2;
		}
		try{
	    	Date date1 = sdf.parse(fromDate);
	    	Date date2 = sdf.parse(toDate);
	
	    	if(date1.compareTo(date2)>0){
	    		//System.out.println("Date1 is after Date2");
	    		return -1;
	    	}else if(date1.compareTo(date2)<0){
	    		//System.out.println("Date1 is before Date2");
	    		return 1;
	    	}else if(date1.compareTo(date2)==0){
	    		//System.out.println("Date1 is equal to Date2");
	    		return 0;
	    	}else{
	    		return -2;
	    	}
		}catch (Exception e) {
			// TODO: handle exception
	    	e.printStackTrace();
	    	return -2;
		}
	}
	
	public static  String generalCode(Long id){
		
		String uuid = UUID.randomUUID().toString();
		String code  = Math.random() + "" + new Date() + "_" + uuid + "_";
		if(id!=null){
			code += code+id;
		}
		return code ;
	
	}
	
	
	public static String encryptPassword(String plainPassword)
			throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(plainPassword.getBytes(), 0,
				plainPassword.length());
		String hashedPass = new BigInteger(1, messageDigest.digest())
				.toString(16);
		if (hashedPass.length() < 32) {
			hashedPass = "0" + hashedPass;
		}

		return hashedPass;
	}
	
	public static boolean checkPhoneNumber(String phone){
		
		if((phone==null)||(phone.isEmpty())){
			return false;
		}
		// kiem tra xem so dien thoai co so 0 phia truoc hay khong. neu khong co thi them vao.
		if(!"0".equals(phone.substring(0, 1))){
			phone = "0"+phone;
		}
		// kiem tra so dien thoai co hop le hay khong.
		if((phone.length()==10)||(phone.length()==11)){
			
			// lay danh sach dau so cua mobiphone
//			String prefix = getParamSystemProperties("system.prefix_mobiphone");
//			//
//			String delimiter = "_";
//			
//			if((prefix!=null)&&(!prefix.isEmpty())){
				//
//				String[] str = prefix.split(delimiter);
//				String PrePhone = "";
//				// neu la 10 so thi lay 3 so dau, neu la 11 so lay 4 so dau
//				if(phone.length()==10){
//					PrePhone = phone.substring(0,3);
//				} else {
//					PrePhone = phone.substring(0,4);
//				}
//				
//				// kiem tra xem dau so co hop le hay khong.
//				if((str!=null)&&(str.length>0)){
//					for(int i=0; i <str.length; i++){
////						System.out.println(PrePhone + " --- " + str[i]);
//						if(PrePhone.equals(str[i])){
//							return true;
//						}
//					}
//				}
				return true;
//			}else{
//				return false;
//			}
		} else {
			return false;
		}
//		return false;
	}
	
	public static void deleteFolder(File path){		
		File[] file = path.listFiles();
		if(file != null){
			for (File f:file){
				if(f.isFile() )
					f.delete();
				else
					deleteFolder(f);
			}
		}
		path.delete();		
	}

	public static String formatName(String code, String name) {
		return "[" + code + "] - " + name;
	}
	
	public static String formatNameSQL(String code, String name) {
		return "'['||" + code + "||'] - '||" + name;
	}

	static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	public static String formatDate(Date date) {
		if (date == null) {
			return "";
		}
		return sdf.format(date);
	}
	
	public static boolean isValid(Integer num) {
		return num != null && num != 0;
	}
	
	public static boolean isValid(Double num) {
		return num != null && num != 0;
	}
	
	public static boolean isValid(Long num) {
		return num != null && num != 0;
	}

	public static boolean isValid(String s) {
		return s != null && !s.isEmpty();
	}
	
	public static Date getStartDate(Date date) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		ca.add(Calendar.SECOND, -1);
		return ca.getTime();
	}

	public static Date getEndDate(Date date) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		ca.add(Calendar.DAY_OF_MONTH, 1);
		return ca.getTime();
	}
	
	public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "d");
    }


	public static String formatName(String name) {
		if (isValid(name)) {
			name = name.trim().toLowerCase();
			String[] sts = name.split("[ ]");
			name = "";
			for (String s : sts) {
				if (s.isEmpty()) {
					continue;
				}
				if (!name.isEmpty()) {
					name += " ";
				}
				int i;
				if ((i = s.indexOf(".")) != -1 && i + 2 < s.length()) {
					name += s.substring(0, i + 2).toUpperCase() + s.substring(i + 2);
				} else {
					name += s.substring(0, 1).toUpperCase() + s.substring(1);
				}
			}
			return name;
		}
		return name;
	}

	public static void addTimeModel(Model uiModel) {
		Integer[] am = new Integer[12];
		Integer[] pm = new Integer[12];
		Integer[] minute = new Integer[60];
		for(int i = 0; i < 12; i++){
			am[i] = i;
			pm[i] = i+12;
		}
		for(int i = 0; i < 60; i++){
			minute[i] = i;
		}
		uiModel.addAttribute("am", am);
		uiModel.addAttribute("pm", pm);
		uiModel.addAttribute("minute", minute);
	}
	
	public static String getContent(InputStream inputStream) throws IOException {
		String line, content = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		while ((line = br.readLine()) != null) {
			content += line;
		}
		br.close();
		return content;
	}


	public static String addCond(String cond, String field, String op, Object value) {
		if (isValid(cond)) {
			cond += String.format(" and %s %s %s ", field, op, value);
		} else {
			cond = String.format("%s %s %s", field, op, value);
		}
		return cond;
	}
}
