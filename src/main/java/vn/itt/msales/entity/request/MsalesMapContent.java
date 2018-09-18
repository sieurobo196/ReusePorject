/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.request;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.itt.msales.common.json.MsalesJsonUtils;

/**
 *
 * @author vtm
 */
public class MsalesMapContent {

    private LinkedHashMap content = new LinkedHashMap();

    public MsalesMapContent() {
    }

    public LinkedHashMap getContent() {
        return content;
    }

    public void setContent(LinkedHashMap content) {
        this.content = content;
    }

    public void add(String fieldName, Object value) {
        content.put(fieldName, value);
    }

    @Override
    public String toString() {
        return MsalesJsonUtils.getJSONFromOject(content);
    }

    public static String createMapContent(String fieldName, Object value) {
        LinkedHashMap content = new LinkedHashMap();
        content.put(fieldName, value);
        return MsalesJsonUtils.getJSONFromOject(content);
    }

    public static MsalesMapContent createMapContent(Object object) {
        try {
            String json = MsalesJsonUtils.getJSONFromOject(object);
            LinkedHashMap content = MsalesJsonUtils.getObjectFromJSON(json, LinkedHashMap.class);
            MsalesMapContent msalesMapContent = new MsalesMapContent();
            msalesMapContent.setContent(content);
            return msalesMapContent;
        } catch (Exception ex) {
            return new MsalesMapContent();
        } 
    }

}
