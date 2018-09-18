/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.channel.controller;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.entity.Property;
import vn.itt.msales.entity.PropertyValue;
import vn.itt.msales.entity.TableName;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;

/**
 *
 * @author KhaiCH
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.PROPERTYVALUE.NAME)
public class MsalesPropertyValueController extends CsbController {

    /**
     * create a Property
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTYVALUE.ACTION_CREATE_PROPERTYVALUE, method = RequestMethod.POST)
    public @ResponseBody
    String createPropertyValue(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            PropertyValue propertyValue;
            try {
                //parse jsonString to a PropertyValue Object
                propertyValue = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        PropertyValue.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //propertyValue from json not null
            if (propertyValue != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (propertyValue.getTableNameId() != null) {
                    TableName tableName = dataService.getRowById(propertyValue.getTableNameId(), TableName.class);
                    if (tableName == null) {
                        hashErrors.put("TableName", "ID = " + propertyValue.getTableNameId() + " is not exist!");
                    }
                }
                if (propertyValue.getPropertyId() != null) {
                    Property property = dataService.getRowById(propertyValue.getPropertyId(), Property.class);
                    if (property == null) {
                        hashErrors.put("Property", "ID = " + propertyValue.getPropertyId() + " is not exist!");
                    }
                }

                if (propertyValue.getCreatedUser() != null) {
                    User user = dataService.getRowById(propertyValue.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + propertyValue.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    //return message warning tableName or property is not exist in DB
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));

                }
                try {
                    //save propertyValue to DB
                    int ret = dataService.insertRow(propertyValue);
                    //propertyValue from DB not null
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //propertyValue from DB null
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_INSERT_FAIL));
                    }
                } catch (Exception e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }
            } //propertyValue from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * update a PropertyValue
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTYVALUE.ACTION_UPDATE_PROPERTYVALUE, method = RequestMethod.POST)
    public @ResponseBody
    String updatePropertyValue(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null

        if (jsonString != null) {
            PropertyValue propertyValue;
            try {
                //parse jsonString to a PropertyValue Object
                propertyValue = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        PropertyValue.class
                );
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //propertyValue from json not null
            if (propertyValue != null) {
                if (propertyValue.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                int id = propertyValue.getId();
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (propertyValue.getTableNameId() != null) {
                    TableName tableName = dataService.getRowById(propertyValue.getTableNameId(), TableName.class);
                    if (tableName == null) {
                        hashErrors.put("TableName", "ID = " + propertyValue.getTableNameId() + " is not exist!");
                    }
                }
                if (propertyValue.getPropertyId() != null) {
                    Property property = dataService.getRowById(propertyValue.getPropertyId(), Property.class);
                    if (property == null) {
                        hashErrors.put("Property", "ID = " + propertyValue.getPropertyId() + " is not exist!");
                    }
                }

                if (propertyValue.getUpdatedUser() != null) {
                    User user = dataService.getRowById(propertyValue.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + propertyValue.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    //return message warning tableName or property is not exist in DB
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    int ret = dataService.updateSync(propertyValue);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(PropertyValue.class, id);
                    }

                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //propertyValue from DB null or incorrect
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_UPDATE_FAIL));
                    }
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * delete a PropertyValue
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTYVALUE.ACTION_DELETE_PROPERTYVALUE, method = RequestMethod.POST)
    public @ResponseBody
    String deletePropertyValue(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null

        if (jsonString != null) {
            PropertyValue propertyValue;
            try {
                //parse jsonString to a PropertyValue Object
                propertyValue = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        PropertyValue.class
                );
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //propertyValue from json not null
            if (propertyValue != null) {
                if (propertyValue.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                if (propertyValue.getDeletedUser() != null) {
                    User user = dataService.getRowById(propertyValue.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, propertyValue.getDeletedUser());
                    }
                }

                int id = propertyValue.getId();

                try {
                    //delete propertyValue from DB
                    int ret = dataService.deleteSynch(propertyValue);
                    
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(PropertyValue.class, id);
                    }
                    //update success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.UNKNOW));
                }
            } //propertyValue from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get a PropertyValue info
     *
     * @param request is a HttpServletRequest
     * @return string json include PropertyValue info
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTYVALUE.ACTION_GET_PROPERTYVALUE, method = RequestMethod.POST)
    public @ResponseBody
    String getPropertyValue(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null

        if (jsonString != null) {
            PropertyValue propertyValue;
            try {
                //parse jsonString to a PropertyValue Object
                propertyValue = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        PropertyValue.class
                );
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //propertyValue from json not null
            if (propertyValue != null) {
                //propertyValue from json with correct Id
                if (propertyValue.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = propertyValue.getId();

                //get propertyValue from DB
                propertyValue = dataService.getRowById(propertyValue.getId(),
                        PropertyValue.class);
                //propertyValue not null
                if (propertyValue != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, propertyValue));
                } //propertyValue null
                else {
                    return MsalesJsonUtils.notExists(PropertyValue.class, id);
                }
            } //propertyValue from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get List all PropertyValue
     *
     * @param request is a HttpServletRequest
     * @return string json include List property
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTYVALUE.ACTION_GET_LIST_PROPERTYVALUE, method = RequestMethod.POST)
    public @ResponseBody
    String getListPropertyValue(HttpServletRequest request) {

        //get List propertyValue from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

        MsalesResults<PropertyValue> list = dataService.getListOption(PropertyValue.class, parameterList,true);

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));

    }

    /**
     * get List PropertyValue by TableName
     *
     * @param request is a HttpServletRequest
     * @return string json include List PropertyValue
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTYVALUE.ACTION_GET_LIST_PROPERTYVALUE_BY_TABLENAME_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListPropertyByTableName(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            PropertyValue propertyValue;
            try {
                //parse jsonString to a PropertyValue object
                propertyValue = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        PropertyValue.class
                );
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //propertyValue from json not null
            if (propertyValue != null) {
                //tableName from propertyValue with correct Id
                if (propertyValue.getTableNameId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("PropertyValue", "tableNameId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
                ParameterList parameterList = new ParameterList("tableNames.id", propertyValue.getTableNameId(), page.getPageNo(), page.getRecordsInPage());
                //get List PropertyValue by TableName id                    
                MsalesResults<PropertyValue> list = dataService.getListOption(PropertyValue.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));

                //tableName from json with incorrect Id 
            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get List PropertyValue by Property
     *
     * @param request is a HttpServletRequest
     * @return string json include List PropertyValue
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTYVALUE.ACTION_GET_LIST_PROPERTYVALUE_BY_PROPERTY_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListPropertyValueByProperty(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null

        if (jsonString != null) {
            PropertyValue propertyValue;
            try {
                //parse jsonString to a PropertyValue object
                propertyValue = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        PropertyValue.class
                );
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //propertyValue from json not null
            if (propertyValue != null) {
                //property from propertyValue with correct Id
                if (propertyValue.getPropertyId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("PropertyValue", "propertyId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
                ParameterList parameterList = new ParameterList("propertys.id", propertyValue.getPropertyId(), page.getPageNo(), page.getRecordsInPage());

                //get List PropertyValue by property id
                MsalesResults<PropertyValue> list = dataService.getListOption(PropertyValue.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));

            } //propertyValue from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

}
