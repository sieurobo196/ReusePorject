/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.common.json;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.entity.request.MsalesRequest;
import vn.itt.msales.entity.response.MsaleResponseStatus;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.logex.MSalesException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import vn.itt.msales.common.json.validator.MsalesValidator;

/**
 * @author ChinhNQ
 * @version
 * @since 1 Jun 2015 19:14:03
 * msales#com.itt.msales.utils.converter.json.JsonUtils.java
 * <p>
 * This class using convert JOSN to Object and Object to JSON
 * <p>
 */
public class MsalesJsonUtils {

    private static String filterName = "JSONFILTER";

    private MsalesJsonUtils() {
    }

    /**
     * Get object class from JSON string
     * <p>
     * @param jsonString  is string with JSON format
     * @param objectClass is object mapping with JSON string
     * <p>
     * @throws @seeIOException
     * @throws @seeJsonMappingException
     * @throws @seeJsonParseException
     */
    public static <T> T getObjectFromJSON(final String jsonString,
            final Class<T> objectClass) throws JsonParseException,
            JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(MsalesValidator.DATE_FORMAT_LONG));
        mapper.setTimeZone(TimeZone.getTimeZone(MsalesValidator.GMT));
        return mapper.readValue(jsonString, objectClass);
    }

    /**
     * Get JSOn from a byte array.
     * <p>
     * @param <T>
     * @param bytes
     * @param objectClass
     * <p>
     * @return
     * <p>
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> T getObjectFromBytes(final byte[] bytes,
            final Class<T> objectClass) throws JsonParseException,
            JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(MsalesValidator.DATE_FORMAT_LONG));
        mapper.setTimeZone(TimeZone.getTimeZone(MsalesValidator.GMT));
        return mapper.readValue(bytes, objectClass);
    }

    public static String getJSONFromOject(final Object object, String[] fieldNames) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(MsalesValidator.DATE_FORMAT_LONG));
        mapper.setTimeZone(TimeZone.getTimeZone(MsalesValidator.GMT));
        com.fasterxml.jackson.databind.ser.FilterProvider filterProvider = new com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider()
                .addFilter(filterName, com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.serializeAllExcept(fieldNames));
        mapper.setFilters(filterProvider);

        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String getJSONFromOject(Object object, MsaleResponseStatus sResponseStatus, String[] fieldNames) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(MsalesValidator.DATE_FORMAT_LONG));
        mapper.setTimeZone(TimeZone.getTimeZone(MsalesValidator.GMT));
        com.fasterxml.jackson.databind.ser.FilterProvider filterProvider = new com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider()
                .addFilter(filterName, com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.serializeAllExcept(fieldNames));
        mapper.setFilters(filterProvider);
        MsalesResponse response = new MsalesResponse();
        response.setStatus(sResponseStatus);
        try {
            if (object == null) {
                object = new Object();
            }
            response.setContents(object);
            return mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            response.setStatus(new MsaleResponseStatus(
                    MsalesStatus.JSON_INVALID));
            try {
                return mapper.writeValueAsString(response);
            } catch (JsonProcessingException e1) {
            }
        }
        return "";
    }

    /**
     * This method get JSON from Object
     * <p>
     * @param object is object you want convert to json
     * <p>
     * @return json string
     * <p>
     * @throws JsonProcessingException
     */
    public static String getJSONFromOject(final Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(MsalesValidator.DATE_FORMAT_LONG));
        mapper.setTimeZone(TimeZone.getTimeZone(MsalesValidator.GMT));
        com.fasterxml.jackson.databind.ser.FilterProvider filterProvider = new com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider()
                .addFilter(filterName, com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.serializeAllExcept(""));
        mapper.setFilters(filterProvider);

        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
//
//    public static String getJSONFromOject(Object object, MsaleResponseStatus sResponseStatus) {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setDateFormat(Utilities.dateFormat());
//        com.fasterxml.jackson.databind.ser.FilterProvider filterProvider = new com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider()
//        .addFilter(filterName, com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.serializeAllExcept(""));
//		mapper.setFilters(filterProvider);
//        MsalesResponse response = new MsalesResponse();
//        response.setStatus(sResponseStatus);
//        try {
//            if (object == null) {
//                object = new Object();
//            }
//            response.setContentsList(object);
//            return mapper.writeValueAsString(response);
//        } catch (JsonProcessingException e) {
//            response.setStatus(new MsaleResponseStatus(
//                    MsalesStatus.JSON_INVALID));
//            try {
//                return mapper.writeValueAsString(response);
//            } catch (JsonProcessingException e1) {
//            }
//        }
//        return "";
//    }

    public static String getJSONFromOject(Object content, MsaleResponseStatus sResponseStatus) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(MsalesValidator.DATE_FORMAT_LONG));
        mapper.setTimeZone(TimeZone.getTimeZone(MsalesValidator.GMT));
        com.fasterxml.jackson.databind.ser.FilterProvider filterProvider = new com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider()
                .addFilter(filterName, com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.serializeAllExcept(""));
        mapper.setFilters(filterProvider);
        MsalesResponse response = new MsalesResponse();
        response.setStatus(sResponseStatus);
        try {
            response.setContents(content);
            return mapper.writeValueAsString(response);

        } catch (JsonProcessingException e) {
            response.setStatus(new MsaleResponseStatus(
                    MsalesStatus.JSON_INVALID));
            try {
                return mapper.writeValueAsString(response);
            } catch (JsonProcessingException e1) {
            }
        }
        return "";
    }

    /**
     * Get value of key from json string.
     * <p>
     * @param json json string
     * @param key  is key you want get value.
     * <p>
     * @return value of the key
     */
    public static String getValue(String json, String key)
            throws JsonProcessingException, IOException {
        JsonFactory factory = new JsonFactory();

        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode rootNode = mapper.readTree(json);

        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode
                .fields();
        while (fieldsIterator.hasNext()) {

            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            if (field.getKey().equals(key)) {
                return field.getValue().asText();
            }
        }
        return "";
    }

    public static String getValue(JsonNode jsonNote, String key) {
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = jsonNote.fields();
        while (fieldsIterator.hasNext()) {

            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            if (field.getKey().equals(key)) {
                return field.getValue().toString();
            }
        }
        return "";
    }

    public static MsalesRequest getRequest(String jsonString) {

        try {
            return getObjectFromJSON(jsonString, MsalesRequest.class);
        } catch (IOException e) {
            throw new MSalesException(e.getMessage());
        }
    }

    public static JsonNode getJsonNote(String jsonString) {

        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(jsonString);

            return rootNode;
        } catch (Exception e) {
            throw new MSalesException(e.getMessage(), e.getCause());
        }
    }

    public interface JSONListener {

        public void parse(int code, String message);
    }

    /**
     * Check json validate
     */
    public static String jsonValidate(Exception e) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap();
        ConstraintViolationException va = (ConstraintViolationException) e;
        Set<ConstraintViolation<?>> setVa = va.getConstraintViolations();
        int i = 1;
        for (ConstraintViolation<?> setVa1 : setVa) {
            hashMap.put("" + (i++), setVa1.getMessage());
        }

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, hashMap));
    }

    public boolean isValidJSON(final String json) {
        boolean valid = false;
        try {
            final JsonParser parser = new ObjectMapper().getJsonFactory()
                    .createJsonParser(json);
            while (parser.nextToken() != null) {
            }
            valid = true;
        } catch (JsonParseException jpe) {
            jpe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return valid;
    }

    /**
     * Check filed in json request
     */
    public static String validateFormat(Exception ex) {
        // can't create User object from JSON requesst from client.
        LinkedHashMap<String, String> object = new LinkedHashMap();
        // check field in JSOn request not match with field in JSON.
        if (ex instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException unre = (UnrecognizedPropertyException) ex;
            object.put(unre.getPropertyName(), String.format(MsalesStatus.JSON_UNRECOGNIZED_PROPETYNAME.getMessage(), unre.getPropertyName()));
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_UNRECOGNIZED_FILEDS, object));
        } else if (ex instanceof InvalidFormatException) {
            InvalidFormatException inva = (InvalidFormatException) ex;
            List<JsonMappingException.Reference> re = inva.getPath();
            if (!re.isEmpty()) {
                int i = 1;
                for (JsonMappingException.Reference re1 : re) {
                    object.put("" + (i++), String.format(MsalesStatus.JSON_FIELD_IVALID_CONVERT.getMessage(), inva.getValue(), inva.getTargetType().getSimpleName()));
                }
            }
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_INVALID_FORMAT, object));
        } else if (ex instanceof JsonMappingException) {
            JsonMappingException jsonMappingException = (JsonMappingException) ex;
            List<JsonMappingException.Reference> re = jsonMappingException.getPath();
            if (!re.isEmpty()) {
                for (JsonMappingException.Reference re1 : re) {
                    int idx = jsonMappingException.getMessage().indexOf((": Unparseable"));
                    object.put(re1.getFieldName(), jsonMappingException.getMessage().substring(0, idx).replace("\"", ""));
                }
            }
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_INVALID_FORMAT, object));
        }
        // JSON request from client is invalid.
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_INVALID));
    }

    /**
     * notify id of object is not null in json request.
     */
    public static String idNull() {
        // JSON request id user is not exist in json
        LinkedHashMap<String, String> object = new LinkedHashMap();
        object.put("id", MsalesStatus.JSON_ID_NULL.getMessage());
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, object));
    }

    /**
     * Notify id request is not exists in database.
     */
    public static String notExists(Class<?> classs, int id) {
        // This user not exist in database.
        LinkedHashMap<String, String> object = new LinkedHashMap();
        object.put("id", String.format(MsalesStatus.NOT_EXISTS.getMessage(), "[ID = " + id + "]"));
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, object));
    }

}
