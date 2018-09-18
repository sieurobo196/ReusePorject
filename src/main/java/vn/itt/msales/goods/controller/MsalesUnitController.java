/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.goods.controller;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
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
import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.logex.MSalesException;

/**
 *
 * @author Cutx
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.UNIT.NAME)
public class MsalesUnitController extends CsbController {

    /**
     * get a Unit
     *
     * @param request is a HttpServletRequest
     * @return string json include Unit info
     */
    @RequestMapping(value = MsalesConstants.MODULE.UNIT.ACTION_GET_UNIT, method = RequestMethod.POST)
    public @ResponseBody
    String getUnit(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            Unit unit;
            try {
                //parse jsonString to a Unit Object
                unit = (Unit) MsalesJsonUtils.getObjectFromJSON(jsonString, Unit.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Unit from json not null
            if (unit != null && unit.getId() != null) {

                //get Unit from DB
                unit = dataService.getRowById(unit.getId(), Unit.class);
                //Unit not null
                if (unit != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, unit));
                } //Unit null
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE));
                }
            } //Unit from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * create a Unit
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.UNIT.ACTION_CREATE_UNIT, method = RequestMethod.POST)
    public @ResponseBody
    String createUnit(HttpServletRequest request) {
        //get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();

        //jsonString not null
        if (jsonString != null) {
            Unit unit;
            try {
                //parse jsonString to a Unit Object
                unit = MsalesJsonUtils.getObjectFromJSON(jsonString, Unit.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Unit from json not null
            if (unit != null) {

                try {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    if (unit.getCreatedUser() != null) {
                        User user = dataService.getRowById(unit.getCreatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", "User with ID = " + unit.getCreatedUser() + " không tồn tại.");
                        }
                    }
                    if (unit.getCompanyId() != null) {
                        Company company = dataService.getRowById(unit.getCompanyId(), Company.class);
                        if (company == null) {
                            hashErrors.put("Company", "with ID = " + unit.getCompanyId() + " không tồn tại.");
                        }
                    }

                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    //save Unit to DB
                    dataService.insertRow(unit);
                } catch (MSalesException e) {
                    Exception ex = (Exception) e.getCause().getCause();
                    if (ex instanceof org.hibernate.exception.ConstraintViolationException) {
                        org.hibernate.exception.ConstraintViolationException conViEx = (org.hibernate.exception.ConstraintViolationException) ex;
                        // check duplicate user name
                        if (conViEx.getCause() instanceof MySQLIntegrityConstraintViolationException) {
                            // response to client the user request create is duplicate.
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(MsalesStatus.USER_NAME_DUPLICATE));
                        }
                    } else if (ex instanceof javax.validation.ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(ex);
                    }

                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));

                }
                // reponse Unit ok to client
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
            } else {
                // can't create Unit from JSON request from client.
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_INSERT_FAIL));
            }
        } else {
            // JSON string request from client is null
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
        }
    }

    /**
     * Update data to Unit
     *
     * @param request is jsonString contain data to update
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.UNIT.ACTION_UPDATE_UNIT, method = RequestMethod.POST)
    public @ResponseBody
    String updateUnit(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            Unit unit;
            try {
                //parse jsonString to a Unit Object
                unit = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Unit.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Unit from json not null
            if (unit != null) {
                if (unit.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
                }
                int ret = 0;
                try {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    if (unit.getUpdatedUser() != null) {
                        User user = dataService.getRowById(unit.getUpdatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", "User with ID = " + unit.getUpdatedUser() + " không tồn tại.");
                        }
                    }
                    if (unit.getCompanyId() != null) {
                        Company company = dataService.getRowById(unit.getCompanyId(), Company.class);
                        if (company == null) {
                            hashErrors.put("Company", "with ID = " + unit.getCompanyId() + " không tồn tại.");
                        }
                    }
                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    //not update companyId
                    Unit rootUnit = dataService.getRowById(unit.getId(), Unit.class);
                    if (rootUnit == null) {
                        hashErrors.put("Unit", "ID = " + unit.getId() + " không tồn tại.");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                    //not update company
                    unit.setCompanys(rootUnit.getCompanys());

                    //update Unit to DB
                    ret = dataService.updateSync(unit);
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }
                    return MsalesJsonUtils.getJSONFromOject(e.getMessage());
                }
                //Update success
                if (ret == -2) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                    hashErrors.put("Unit",
                            "Unit with ID = " + unit.getId() + " không tồn tại");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                } else if (ret > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                } //Update failed
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_UPDATE_FAIL));
                }

            } //Unit from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * Delete a Unit
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.UNIT.ACTION_DELETE_UNIT, method = RequestMethod.POST)
    public @ResponseBody
    String deleteUnit(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            Unit unit;
            try {
                //parse jsonString to a Unit Object
                unit = MsalesJsonUtils.getObjectFromJSON(jsonString, Unit.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Unit from json not null
            if (unit != null) {
                if (unit.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
                }

                if (unit.getDeletedUser() != null) {
                    User user = dataService.getRowById(unit.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, unit.getDeletedUser());
                    }
                }

                try {
                    //delete Unit from DB
                    int ret = dataService.deleteSynch(unit);
                    //delete Unit success
                    if (ret == -2) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("Unit",
                                "Unit with ID = " + unit.getId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                    } //Unit failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));
                }
            } //Unit from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * get List all Unit
     *
     * @param request is a HttpServletRequest
     * @return string json include List Unit
     */
    @RequestMapping(value = MsalesConstants.MODULE.UNIT.ACTION_GET_LIST_UNIT, method = RequestMethod.POST)
    public @ResponseBody
    String getListUnit(HttpServletRequest request) {

        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Unit unit;
            try {
                //parse jsonString to a Unit Object
                unit = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Unit.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (unit != null) {
                if (unit.getCompanyId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("Unit", "companyId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                ParameterList parameterList = new ParameterList("companys.id", unit.getCompanyId(), page.getPageNo(), page.getRecordsInPage());
                MsalesResults<Unit> list = dataService.getListOption(Unit.class, parameterList, true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
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
     * get Combobox Unit
     */
    @RequestMapping(value = MsalesConstants.MODULE.UNIT.ACTION_GET_CB_LIST_UNIT, method = RequestMethod.POST)
    public @ResponseBody
    String getCbListUnit(HttpServletRequest request) {
        //get List GoodsCategory from DB
        // get List Goods from DB
        ParameterList parameterList = new ParameterList();
        MsalesResults<Unit> list = dataService.getListOption(Unit.class, parameterList, true);
        // list not null display list
        String[] strings = {"code","order"};
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(
                HttpStatus.OK, list),strings);

    }
}
