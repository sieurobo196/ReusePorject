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
@RequestMapping(value = MsalesConstants.MODULE.PROPERTY.NAME)
public class MsalesPropertyController extends CsbController {

    /**
     * create a Property
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTY.ACTION_CREATE_PROPERTY, method = RequestMethod.POST)
    public @ResponseBody
    String createProperty(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Property property;
            try {
                //parse jsonString to a Property Object
                property = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Property.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //property from json not null
            if (property != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (property.getTableNameId() != null) {
                    TableName tableName = dataService.getRowById(property.getTableNameId(), TableName.class);
                    if (tableName == null) {
                        //return message warning TableName is not exist in DB                                                
                        hashErrors.put("TableName",
                                "ID = " + property.getTableNameId() + " không tồn tại.");
                    }
                }

                if (property.getCreatedUser() != null) {
                    User user = dataService.getRowById(property.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + property.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save property to DB
                    int ret = dataService.insertRow(property);
                    //property from DB not null
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //property from DB null
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
            } //property from json null
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
     * update a Property
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTY.ACTION_UPDATE_PROPERTY, method = RequestMethod.POST)
    public @ResponseBody
    String updateProperty(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Property property;

            try {
                //parse jsonString to a Property Object
                property = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Property.class
                );
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //property from json not null
            if (property != null) {
                if (property.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = property.getId();

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (property.getTableNameId() != null) {
                    TableName tableName = dataService.getRowById(property.getTableNameId(), TableName.class);
                    if (tableName == null) {
                        //return message warning TableName is not exist in DB                                                
                        hashErrors.put("TableName",
                                "ID = " + property.getTableNameId() + " không tồn tại.");
                    }
                }

                if (property.getUpdatedUser() != null) {
                    User user = dataService.getRowById(property.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + property.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                try {
                    //update property to DB
                    int ret = dataService.updateSync(property);

                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(Property.class, id);
                    }

                    //update success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update failed
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
            } //property from json null
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
     * delete a Property
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTY.ACTION_DELETE_PROPERTY, method = RequestMethod.POST)
    public @ResponseBody
    String deleteProperty(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null

        if (jsonString != null) {
            Property property;

            try {
                //parse jsonString to a Property Object
                property = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Property.class
                );
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //property from json not null
            if (property != null) {
                if (property.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                if (property.getDeletedUser() != null) {
                    User user = dataService.getRowById(property.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, property.getDeletedUser());
                    }
                }

                int id = property.getId();

                try {
                    //delete property from DB
                    int ret = dataService.deleteSynch(property);
                    //update success

                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(Property.class, id);
                    }
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

            } //property from json null
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
     * get a Property info
     *
     * @param request is a HttpServletRequest
     * @return string json include Property info
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTY.ACTION_GET_PROPERTY, method = RequestMethod.POST)
    public @ResponseBody
    String getProperty(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null

        if (jsonString != null) {
            Property property;

            try {
                //parse jsonString to a Property Object
                property = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Property.class
                );
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //property from json not null
            if (property != null) {
                if (property.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                int id = property.getId();
                //get property from DB
                property = dataService.getRowById(property.getId(),Property.class);
                //property not null
                if (property
                        != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, property));
                } //property null
                else {
                    return MsalesJsonUtils.notExists(Property.class, id);
                }
            } //property from json null
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
     * get List all Property
     *
     * @param request is a HttpServletRequest
     * @return string json include List property
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTY.ACTION_GET_LIST_PROPERTY, method = RequestMethod.POST)
    public @ResponseBody
    String getListProperty(HttpServletRequest request) {

        //get List property from DB
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

        MsalesResults<Property> list = dataService.getListOption(Property.class, parameterList,true);

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));

    }

    /**
     * get List Property by TableName
     *
     * @param request is a HttpServletRequest
     * @return string json include List property
     */
    @RequestMapping(value = MsalesConstants.MODULE.PROPERTY.ACTION_GET_LIST_PROPERTY_BY_TABLENAME_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListPropertyByTableName(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Property property;

            try {
                //parse jsonString to a Property Object
                property = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Property.class
                );
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //property from json not null
            if (property != null) {

                if (property.getTableNameId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("Property", "tableNameId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List Property by TableName id
                MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
                ParameterList parameterList = new ParameterList("tableNames.id", property.getTableNameId(), page.getPageNo(), page.getRecordsInPage());

                MsalesResults<Property> list = dataService.getListOption(Property.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));

            } //property from json null
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
