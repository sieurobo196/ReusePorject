package vn.itt.msales.common;

public class PaginationInfo {
	private int size;
	private int page;
	private int maxPages;
	private int start;

	public PaginationInfo(int size, int page, int maxPages, int start) {
		this.size = size;
		this.page = page;
		this.maxPages = maxPages;
		this.start = start;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getMaxPages() {
		return maxPages;
	}

	public void setMaxPages(int maxPages) {
		this.maxPages = maxPages;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

}
