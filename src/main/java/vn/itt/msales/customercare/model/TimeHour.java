package vn.itt.msales.customercare.model;

public class TimeHour {
	private Integer id;
	private String name;
	
	public TimeHour() {
		// TODO Auto-generated constructor stub
	}

	public TimeHour(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
