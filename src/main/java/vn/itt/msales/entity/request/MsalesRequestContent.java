/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.entity.request;

/**
 * @author ChinhNQ
 * @version
 * @since 9 Jun 2015 13:16:45
 * msales_saas#vn.itt.msales.entity.request.MsaleRequestContent.java
 *
 */
public class MsalesRequestContent {
    
    private Object contents;

    
    public Object getContents() {
        return contents;
    }

    public void setContents(Object contents) {
        this.contents = contents;
    }

    public MsalesRequestContent(Object contents) {
        super();
        this.contents = contents;
    }
}
