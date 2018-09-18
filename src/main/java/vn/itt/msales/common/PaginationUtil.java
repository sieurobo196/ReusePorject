package vn.itt.msales.common;

import org.springframework.ui.Model;

public class PaginationUtil {

	public static PaginationInfo calculatePage(long count, Integer page, Integer size) {
		if (page == null) {
			page = 1;
		}
		if (size == null) {
			size = 10;
		}
		int maxPages = 0;
		if (size > 0) {
			maxPages = (int) (count / size + (count % size > 0 ? 1 : 0));
		}
		if (maxPages == 0) {
			maxPages = 1;
		}
		if (page > maxPages) {
			page = maxPages;
		}
		int start = 0;
		if (size > 0) {
			start = (int) ((page - 1) * size);
		}
		PaginationInfo result = new PaginationInfo(size, page, maxPages, start);
		return result;
	}

	public static PaginationInfo calculatePage(long count, Integer page,
			Integer size, Model uiModel) {
		PaginationInfo result = calculatePage(count, page, size);
		uiModel.addAttribute("size", result.getSize());
		uiModel.addAttribute("page", result.getPage());
		uiModel.addAttribute("maxPages", result.getMaxPages());
		return result;
	}
}
