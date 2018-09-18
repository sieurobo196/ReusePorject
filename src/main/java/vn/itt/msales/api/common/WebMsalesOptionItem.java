package vn.itt.msales.api.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ChinhNQ
 */
public class WebMsalesOptionItem {
	private long id;
	private String name;
    private String code;

	public WebMsalesOptionItem() {
	};

        public WebMsalesOptionItem(long id, String name) {
        this.id = id;
        this.name = name;
        this.code = "";
    }

	public WebMsalesOptionItem(long id, String name, String code) {
		this.id = id;
		this.name = name;
		this.code = code;
	}

	public WebMsalesOptionItem(String code, String name) {
		this.name = name;
		this.code = code;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public static List<WebMsalesOptionItem> NoOptionList(long id, String name) {
		List<WebMsalesOptionItem> list = new ArrayList<WebMsalesOptionItem>(1);
		list.add(new WebMsalesOptionItem(id, name));
		return list;
	}
}
