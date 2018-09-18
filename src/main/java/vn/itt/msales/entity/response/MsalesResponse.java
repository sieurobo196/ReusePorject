/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.entity.response;
import org.springframework.http.HttpStatus;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.common.json.MsalesJsonUtils;

/**
 * @author ChinhNQ
 * @version
 * @since 4 Jun 2015 09:37:49 msales_saas#vn.itt.msales.entity.Response.java
 *
 */
public class MsalesResponse {

    private MsaleResponseStatus status;
    private Object contents;

    public MsalesResponse() {
    }

    public MsalesResponse(MsaleResponseStatus status) {
        super();
        this.status = status;
        this.contents = null;
    }

    public MsalesResponse(MsaleResponseStatus status, Object contents) {
        super();
        this.status = status;
        this.contents = contents;
    }

    public MsaleResponseStatus getStatus() {
        return status;
    }

    public void setStatus(MsaleResponseStatus status) {
        this.status = status;
    }

    public static MsalesResponse createMsalesResponseFromString(String responseString) {
        try {
            MsalesResponse msalesResponse = MsalesJsonUtils.getObjectFromJSON(responseString, MsalesResponse.class);
            return msalesResponse;
        } catch (Exception ex) {
            //loi parse json
        }
        return new MsalesResponse();
    }

    public <T> T parseContents(Class<T> clazz)
    {
        String json = MsalesJsonUtils.getJSONFromOject(contents);
        try {
            T ret =  MsalesJsonUtils.getObjectFromJSON(json, clazz);
            return ret;
        } catch (Exception ex) {
            //loi parse
        }
        return null;
    }
    
    public <T> T[] parseContentsList(Class clazz)
    {
        String json = MsalesJsonUtils.getJSONFromOject(contents);
        try {
            MsalesResults<T> msalesResults = MsalesJsonUtils.getObjectFromJSON(json,MsalesResults.class);
            json = MsalesJsonUtils.getJSONFromOject(msalesResults.getContentList());
            return (T[])MsalesJsonUtils.getObjectFromJSON(json,clazz);
        } catch (Exception ex) {
            //loi parse
            System.out.println("Loi " + ex.getMessage());
        }
        return null;
    }
    
    public <T> T parseContents(Class<T> clazz,String dateFormat)
    {        
        String json = MsalesJsonUtils.getJSONFromOject(contents);
        try {
            T ret =  MsalesJsonUtils.getObjectFromJSON(json, clazz);
            return ret;
        } catch (Exception ex) {
            //loi parse
        }
        return null;
    }

    public static MsalesResponse create(MsalesStatus status) {
        MsalesResponse response = new MsalesResponse();
        response.setStatus(new MsaleResponseStatus(status));
        response.setContents(null);

        return response;
    }

    public static MsalesResponse createExtreme(MsalesStatus status, String detail) {
        MsalesResponse response = new MsalesResponse();
        MsaleResponseStatus responseStatus = new MsaleResponseStatus(status);
        responseStatus.setMessage(responseStatus.getMessage() + ". " + detail);
        response.setStatus(responseStatus);
        response.setContents(null);

        return response;
    }

    public static MsalesResponse create(String fieldNotify) {
        MsalesResponse response = new MsalesResponse();
        response.setStatus(new MsaleResponseStatus(MsalesStatus.JSON_FIELD_VALUE_NULL, fieldNotify));
        response.setContents(null);

        return response;
    }

    public static MsalesResponse create(HttpStatus status) {
        MsalesResponse response = new MsalesResponse();
        response.setStatus(new MsaleResponseStatus(status));
        response.setContents(null);

        return response;
    }

    public static MsalesResponse create(HttpStatus status, Object content) {
        MsalesResponse response = new MsalesResponse();
        response.setStatus(new MsaleResponseStatus(status));
        response.setContents(content);

        return response;
    }

    public static MsalesResponse create(Enum<?> status, Object content) {
        MsalesResponse response = new MsalesResponse();
        if (status instanceof HttpStatus) {
            response.setStatus(new MsaleResponseStatus(status));
            response.setContents(content);
        } else if (status instanceof MsalesStatus) {
            response.setStatus(new MsaleResponseStatus(status, content));
        }

        return response;
    }

    public static MsalesResponse create(Object content) {
        MsalesResponse response = new MsalesResponse();
        response.setStatus(new MsaleResponseStatus(MsalesStatus.UNKNOW));
        response.setContents(content);

        return response;
    }

    public Object getContents() {
        return contents;
    }

    public void setContents(Object contents) {
        this.contents = contents;
    }
}
