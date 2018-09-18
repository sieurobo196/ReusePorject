/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.entity.response;

/**
 * @author ChinhNQ
 * @version
 * @since 4 Jun 2015 09:36:54
 *        msales_saas#vn.itt.msales.entity.ResponseContents.java
 *
 */
public class MsalesResponseContents {

    private Object contentsList;

    public MsalesResponseContents(Object contents) {
        super();
        this.contentsList = contents;
    }

    public Object getContents() {
        return contentsList;
    }

    public void setContents(Object contents) {
        this.contentsList = contents;
    }

}
