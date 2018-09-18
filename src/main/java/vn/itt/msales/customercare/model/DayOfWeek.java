package vn.itt.msales.customercare.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public enum DayOfWeek {
	MONDAY(1, "Thứ 2"),
	TUESDAY(2, "Thứ 3"),
	WEDNESDAY(3, "Thứ 4"),
	THURSDAY(4, "Thứ 5"),
	FRIDAY(5, "Thứ 6"),
	SATURDAY(6, "Thứ 7"),
	SUNDAY(7, "Chủ Nhật");
	
	public final int id;
	public final String name;
	
	private DayOfWeek(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static DayOfWeek getById(int id) {
		if (id==MONDAY.id) {
			return MONDAY;
		} else if (id==TUESDAY.id) {
			return TUESDAY;
		} else if (id==THURSDAY.id) {
			return THURSDAY;
		} else if (id==WEDNESDAY.id) {
			return WEDNESDAY;
		} else if (id==FRIDAY.id) {
			return FRIDAY;
		} else if (id==SATURDAY.id) {
			return SATURDAY;
		} else if (id==SUNDAY.id) {
			return SUNDAY;
		} else {
			return null;
		}
	}
	
	public static List<DayOfWeek> getList() {
		List<DayOfWeek> list = new ArrayList<DayOfWeek>(7);
		list.add(MONDAY);
		list.add(TUESDAY);
		list.add(WEDNESDAY);
		list.add(THURSDAY);
		list.add(FRIDAY);
		list.add(SATURDAY);
		list.add(SUNDAY);
		return list;
	}

	public static int current() {
		return day(Calendar.getInstance().getTime());
	}
	
	public static int day(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		if (day==Calendar.SUNDAY) {
			day = SUNDAY.id;
		} else {
			day --;
		}
		return day;
	}

	public static Date getDate(int dayOfWeek) {
		int cur = current();
		Calendar cal = Calendar.getInstance();
		if (cur < dayOfWeek) {
			cal.add(Calendar.DAY_OF_MONTH, dayOfWeek - cur);
		} else if (cur > dayOfWeek) {
			cal.add(Calendar.DAY_OF_MONTH, SUNDAY.id - cur + dayOfWeek);
		}
		return cal.getTime();
	}
	
	public static Date getDate(int dayOfWeek, Date date) {
		int cur = day(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (cur != dayOfWeek) {
			cal.add(Calendar.DAY_OF_MONTH, dayOfWeek - cur);
		}
		return cal.getTime();
	}
}
