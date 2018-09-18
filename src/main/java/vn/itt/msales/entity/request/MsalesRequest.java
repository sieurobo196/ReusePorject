/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.entity.request;

/**
 * @author ChinhNQ
 * @version
 * @since 9 Jun 2015 13:15:38
 * msales_saas#vn.itt.msales.entity.MsalesRequest.java
 *
 */
public class MsalesRequest {

    
    private Object contents;
    private MsalesPageRequest page;

    public MsalesRequest() {
    }

    public MsalesRequest(Object contents, MsalesPageRequest page) {
        this.contents = contents;
        this.page = page;
    }

    public Object getContents() {
        return contents;
    }

    public void setContents(Object contents) {
        this.contents = contents;
    }

    public MsalesPageRequest getPage() {
        return page;
    }

    public void setPage(MsalesPageRequest page) {
        this.page = page;
    }

   
    @Override
    public String toString() {
        return "MsalesRequest{"+"contents=" + contents + ", page=" + page + '}';
    }
}
