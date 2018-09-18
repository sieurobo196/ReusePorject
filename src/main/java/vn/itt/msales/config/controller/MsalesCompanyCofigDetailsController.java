/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vn.itt.msales.config.controller;
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
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.CompanyConfig;
import vn.itt.msales.entity.CompanyConfigDetails;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;

/**
 *
 * @author KhaiCH
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIGDETAILS.NAME)
public class MsalesCompanyCofigDetailsController extends CsbController {

    /**
     * create a CompanyConfigDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIGDETAILS.ACTION_CREATE_COMPANY_CONFIG_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String createCompanyConfigDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            CompanyConfigDetails companyConfigDetails;
            try {
                //parse jsonString to a CompanyConfig Object
                companyConfigDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyConfigDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyConfigDetails from json not null
            if (companyConfigDetails != null) {
                //validate

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (companyConfigDetails.getCompanyConfigId()!= null) {
                    CompanyConfig companyConfig = dataService.getRowById(companyConfigDetails.getCompanyConfigId(), CompanyConfig.class);
                    if (companyConfig == null) {
                        hashErrors.put("CompanyConfig", "ID = " + companyConfigDetails.getCompanyConfigId()+ " không tồn tại.");
                    }
                }

                if (companyConfigDetails.getUserRoleId() != null) {
                    UserRole userRole = dataService.getRowById(companyConfigDetails.getUserRoleId(), UserRole.class);
                    if (userRole == null) {
                        hashErrors.put("UserRole", "ID = " + companyConfigDetails.getUserRoleId() + " không tồn tại.");
                    }
                }
                if (companyConfigDetails.getCreatedUser() != null) {
                    User user = dataService.getRowById(companyConfigDetails.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + companyConfigDetails.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save CompanyConfigDetails to DB
                    int ret = dataService.insertRow(companyConfigDetails);
                    //save CompanyConfigDetails success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save CompanyConfigDetails failed
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
            } //CompanyConfigDetails from json null
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
     * update a CompanyConfigDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIGDETAILS.ACTION_UPDATE_COMPANY_CONFIG_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String updateCompanyConfig(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            CompanyConfigDetails companyConfigDetails;
            try {
                //parse jsonString to a CompanyConfigDetails Object
                companyConfigDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyConfigDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyConfigDetails from json not null
            if (companyConfigDetails != null) {
                if (companyConfigDetails.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = companyConfigDetails.getId();

                //validate
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();

                if (companyConfigDetails.getUserRoleId() != null) {
                    UserRole userRole = dataService.getRowById(companyConfigDetails.getUserRoleId(), UserRole.class);
                    if (userRole == null) {
                        hashErrors.put("UserRole", "ID = " + companyConfigDetails.getUserRoleId() + " không tồn tại.");
                    }
                }
                if (companyConfigDetails.getUpdatedUser() != null) {
                    User user = dataService.getRowById(companyConfigDetails.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + companyConfigDetails.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                
                //khong cho update CompanyConfig
                 CompanyConfigDetails rootCompanyConfigDetails = dataService.getRowById(companyConfigDetails.getId(), CompanyConfigDetails.class);
                if (rootCompanyConfigDetails == null) {
                    return MsalesJsonUtils.notExists(CompanyConfig.class, id);
                }
                companyConfigDetails.setCompanyConfigs(rootCompanyConfigDetails.getCompanyConfigs());

                try {
                    //update CompanyConfigDetails to DB
                    int ret = dataService.updateSync(companyConfigDetails);
                    //update success
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(CompanyConfig.class, id);
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
            } //CompanyConfigDetails from json null
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
     * delete a CompanyConfigDetails
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIGDETAILS.ACTION_DELETE_COMPANY_CONFIG_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String deleteCompanyConfigDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            CompanyConfigDetails companyConfigDetails;
            try {
                //parse jsonString to a CompanyConfigDetails Object
                companyConfigDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyConfigDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyConfigDetails from json not null
            if (companyConfigDetails != null) {
                if (companyConfigDetails.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                if (companyConfigDetails.getDeletedUser() != null) {
                    User user = dataService.getRowById(companyConfigDetails.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, companyConfigDetails.getDeletedUser());
                    }
                }

                int id = companyConfigDetails.getId();
                try {
                    //delete CompanyConfigDetails from DB
                    int ret = dataService.deleteSynch(companyConfigDetails);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(CompanyConfigDetails.class, id);
                    }
                    //update CompanyConfigDetails success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update CompanyConfigDetails failed
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
            } //CompanyConfigDetails from json null
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
     * get a CompanyConfigDetails info
     *
     * @param request is a HttpServletRequest
     * @return string json include CompanyConfigDetails info
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIGDETAILS.ACTION_GET_COMPANY_CONFIG_DETAILS, method = RequestMethod.POST)
    public @ResponseBody
    String getCompanyConfigDetails(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            CompanyConfigDetails companyConfigDetails;
            try {
                //parse jsonString to a CompanyConfigDetails Object
                companyConfigDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyConfigDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyConfigDetails from json not null
            if (companyConfigDetails != null) {
                if (companyConfigDetails.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = companyConfigDetails.getId();
                //get CompanyConfigDetails from DB
                companyConfigDetails = dataService.getRowById(companyConfigDetails.getId(),
                        CompanyConfigDetails.class);
                //CompanyConfigDetails not null
                if (companyConfigDetails != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, companyConfigDetails));
                } //CompanyConfigDetails null
                else {
                    return MsalesJsonUtils.notExists(CompanyConfig.class, id);
                }
            } //CompanyConfigDetails from json null
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
     * get List CompanyConfigDetails By CompanyConfigId
     *
     * @param request is a HttpServletRequest
     * @return string json include List CompanyConfigDetails
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIGDETAILS.ACTION_GET_LIST_COMPANY_CONFIG_DETAILS_BY_COMPANY_CONFIG_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListCompanyConfig(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            CompanyConfigDetails companyConfigDetails;
            try {
                //parse jsonString to a CompanyConfig Object
                companyConfigDetails = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyConfigDetails.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyConfigDetails from json not null
            if (companyConfigDetails != null) {
                //companyConfigId null
                if (companyConfigDetails.getCompanyConfigId()== null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("CompanyConfigDetails", "companyConfigId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));

                }

                //get List CompanyConfigDetails by companyConfig id
                ParameterList parameterList = new ParameterList("companyConfigs.id", companyConfigDetails.getCompanyConfigId(), page.getPageNo(), page.getRecordsInPage());

                MsalesResults<CompanyConfigDetails> list = dataService.getListOption(CompanyConfigDetails.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));

            } //CompanyConfig from json null
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
