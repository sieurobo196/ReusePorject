package vn.itt.msales.goods.controller.model;

import java.util.ArrayList;
import java.util.List;
import vn.itt.msales.entity.Status;

public enum GoodsStatus {
	ACTIVE("1", "Hiệu lực"),
	WAIT("2", "Chờ"),
	DEACTIVE("3", "Hủy");
	
	public final String id;
	public final String name;
	
	private GoodsStatus(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static GoodsStatus getById(String id) {
		if (id.equals(ACTIVE.id)) {
			return ACTIVE;
		} else if (id.equals(WAIT.id)) {
			return WAIT;
		} else if (id.equals(DEACTIVE.id)) {
			return DEACTIVE;
		} else {
			return null;
		}
	}
	
	public static List<GoodsStatus> getList() {
		List<GoodsStatus> list = new ArrayList<GoodsStatus>(3);
		list.add(ACTIVE);
		list.add(WAIT);
		list.add(DEACTIVE);
		
		return list;
	}
}