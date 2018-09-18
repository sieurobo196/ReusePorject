/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.entity.request;

import java.util.LinkedHashMap;

/**
 *
 * @author ChinhNQ
 */
public class MsalesPageRequest {

    /**
     * current page request
     */
    private Integer pageNo;
    /**
     * number records in page
     */
    private Integer recordsInPage;

    public MsalesPageRequest() {

    }

    public MsalesPageRequest(Integer pageNo, Integer recordsInPage) {
        this.pageNo = pageNo;
        this.recordsInPage = recordsInPage;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getRecordsInPage() {
        return recordsInPage;
    }

    public void setRecordsInPage(Integer recordsInPage) {
        this.recordsInPage = recordsInPage;
    }

    public LinkedHashMap<String, String> checkFiled() {
        LinkedHashMap<String, String> maps = new LinkedHashMap<>();
        if (pageNo == null) {
            maps.put("pageNo", "PageNo là bắt buộc.");
        }
        if (recordsInPage == null) {
            maps.put("recordsInPage", "RecordsInPage là bắt buộc.");
        }
        if (maps.isEmpty()) {
            return null;
        }
        return maps;
    }
}
