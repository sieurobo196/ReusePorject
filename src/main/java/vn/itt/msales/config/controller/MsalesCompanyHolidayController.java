/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.config.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.CompanyHoliday;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;

/**
 *
 * @author KhaiCH
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.COMPANYHOLIDAY.NAME)
public class MsalesCompanyHolidayController extends CsbController {

    /**
     * create a CompanyHoliday
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYHOLIDAY.ACTION_CREATE_COMPANY_HOLIDAY, method = RequestMethod.POST)
    public @ResponseBody
    String createCompanyHoliday(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            CompanyHoliday companyHoliday;
            try {
                //parse jsonString to a CompanyHoliday Object
                companyHoliday = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyHoliday.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyHoliday from json not null
            if (companyHoliday != null) {
                //validate

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (companyHoliday.getCompanyId() != null) {
                    Company company = dataService.getRowById(companyHoliday.getCompanyId(), Company.class);
                    if (company == null) {
                        hashErrors.put("Company", "ID = " + companyHoliday.getCompanyId() + " không tồn tại.");
                    }
                }

                if (companyHoliday.getCreatedUser() != null) {
                    User user = dataService.getRowById(companyHoliday.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + companyHoliday.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                if (companyHoliday.getType() != null) {
                    if (companyHoliday.getType() > 3 || companyHoliday.getType() < 1) {
                        hashErrors.put("CompanyHoliday", "Type chỉ từ 1 - 3.");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.JSON_VALUE_INVALID, hashErrors));
                    }

                }

                try {
                    //save CompanyHoliday to DB
                    int ret = dataService.insertRow(companyHoliday);
                    //save CompanyHoliday success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save CompanyHoliday failed
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
            } //CompanyHoliday from json null
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
     * update a CompanyHoliday
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYHOLIDAY.ACTION_UPDATE_COMPANY_HOLIDAY, method = RequestMethod.POST)
    public @ResponseBody
    String updateCompanyHoliday(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            CompanyHoliday companyHoliday;
            try {
                //parse jsonString to a CompanyHoliday Object
                companyHoliday = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyHoliday.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyHoliday from json not null
            if (companyHoliday != null) {
                if (companyHoliday.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = companyHoliday.getId();
                //validate
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();

                if (companyHoliday.getUpdatedUser() != null) {
                    User user = dataService.getRowById(companyHoliday.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + companyHoliday.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                if (companyHoliday.getType() != null) {
                    if (companyHoliday.getType() > 3 || companyHoliday.getType() < 1) {
                        hashErrors.put("CompanyHoliday", "Type chỉ từ 1 - 3.");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.JSON_VALUE_INVALID, hashErrors));
                    }

                }

                //khong update companyId
                CompanyHoliday rootCompanyHoliday = dataService.getRowById(companyHoliday.getId(), CompanyHoliday.class);
                if (rootCompanyHoliday == null) {
                    return MsalesJsonUtils.notExists(CompanyHoliday.class, id);
                }
                companyHoliday.setCompanys(rootCompanyHoliday.getCompanys());

                try {
                    //update CompanyHoliday to DB
                    int ret = dataService.updateSync(companyHoliday);
                    //update success
                    //no need!!
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(CompanyHoliday.class, id);
                    }
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
            } //CompanyHoliday from json null
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
     * delete a CompanyHoliday
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYHOLIDAY.ACTION_DELETE_COMPANY_HOLIDAY, method = RequestMethod.POST)
    public @ResponseBody
    String deleteCompanyHoliday(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            CompanyHoliday companyHoliday;
            try {
                //parse jsonString to a CompanyHoliday Object
                companyHoliday = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyHoliday.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyHoliday from json not null
            if (companyHoliday != null) {
                if (companyHoliday.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                if (companyHoliday.getDeletedUser() != null) {
                    User user = dataService.getRowById(companyHoliday.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, companyHoliday.getDeletedUser());
                    }
                }

                int id = companyHoliday.getId();
                try {
                    //delete CompanyHoliday from DB
                    int ret = dataService.deleteSynch(companyHoliday);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(CompanyHoliday.class, id);
                    }
                    //update CompanyHoliday success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update CompanyHoliday failed
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
            } //CompanyHoliday from json null
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
     * get a CompanyHoliday info
     *
     * @param request is a HttpServletRequest
     * @return string json include CompanyHoliday info
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYHOLIDAY.ACTION_GET_COMPANY_HOLIDAY, method = RequestMethod.POST)
    public @ResponseBody
    String getCompanyHoliday(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            CompanyHoliday companyHoliday;
            try {
                //parse jsonString to a CompanyHoliday Object
                companyHoliday = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyHoliday.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Equipment from json not null
            if (companyHoliday != null) {
                if (companyHoliday.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = companyHoliday.getId();
                //get CompanyHoliday from DB
                companyHoliday = dataService.getRowById(companyHoliday.getId(),
                        CompanyHoliday.class);
                //CompanyHoliday not null
                if (companyHoliday != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, companyHoliday));
                } //CompanyHoliday null
                else {
                    return MsalesJsonUtils.notExists(CompanyHoliday.class, id);
                }
            } //CompanyHoliday from json null
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
     * get List all CompanyHoliday
     *
     * @param request is a HttpServletRequest
     * @return string json include List CompanyHoliday
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYHOLIDAY.ACTION_GET_LIST_COMPANY_HOLIDAY, method = RequestMethod.POST)
    public @ResponseBody
    String getListCompanyConfig(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            CompanyHoliday companyHoliday;
            try {
                //parse jsonString to a CompanyConfig Object
                companyHoliday = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyHoliday.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyHoliday from json not null
            if (companyHoliday != null) {
                //companyId null
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();

                if (companyHoliday.getCompanyId() == null) {
                    hashErrors.put("CompanyConfig", "companyId không được null!");
                }

                if (companyHoliday.getYear() == null) {
                    hashErrors.put("CompanyHoliday", "year không được null!");
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                int year = companyHoliday.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, 0, 1,0,0,0);
                Date dateMin = calendar.getTime();
                calendar.set(year, 11, 31,23,59,59);
                Date dateMax = calendar.getTime();

                //get List CompanyHoliday by company id and year
                ParameterList parameterList = new ParameterList("companys.id", companyHoliday.getCompanyId(), page.getPageNo(), page.getRecordsInPage());
                parameterList.add("holidayDate", dateMin, ">=");
                parameterList.add("holidayDate", dateMax, "<=");
                MsalesResults<CompanyHoliday> list = dataService.getListOption(CompanyHoliday.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));

            } //CompanyHoliday from json null
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
