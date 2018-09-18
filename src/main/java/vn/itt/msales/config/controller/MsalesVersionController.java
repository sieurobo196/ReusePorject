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
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.Version;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.services.DataService;

/**
 *
 * @author KhaiCH
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.VERSION.NAME)
public class MsalesVersionController extends CsbController {

    @Override
    public void setDataService(DataService dataService)
    {
        super.dataService = dataService;
    }
    /**
     * create a Version
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.VERSION.ACTION_CREATE_VERSION, method = RequestMethod.POST)
    public @ResponseBody
    String createVersion(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Version version;
            try {
                //parse jsonString to a Version Object
                version = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Version.class);
            } //jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Version from json not null
            if (version != null) {
                //validate

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<>();
                if (version.getCompanyId() != null) {
                    Company company = dataService.getRowById(version.getCompanyId(), Company.class);
                    if (company == null) {
                        hashErrors.put("Company", "ID = " + version.getCompanyId() + " không tồn tại.");
                    }
                }

                if (version.getCreatedUser() != null) {
                    User user = dataService.getRowById(version.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + version.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                try {
                    //validate Version
                    ParameterList parameterList = new ParameterList("isActive", 1);
                    List<Version> listVersions = dataService.getListOption(Version.class, parameterList);
                    if (listVersions.size() == 1) {
                        String versionName = listVersions.get(0).getVersion();
                        if (versionName.equals(version.getVersion())) {
                            //return OK and not create Version
                            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                    .create(HttpStatus.OK, null));
                        }
                    }

                    //save Version to DB
                    //set isActive for version
                    version.setIsActive(1);
                    int ret = dataService.insertRow(version);
                    //save Version success
                    if (ret > 0) {
                        //update isActive 
                        String hql = "update " + Version.class.getSimpleName() + " set isActive=0 where isActive=1 and id!=" + ret;
                        try {
                            dataService.executeHQL(hql);
                        } catch (Exception ex) {
                            //error update isActive
                        }
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save Version failed
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
            } //Version from json null
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
     * update a Version
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.VERSION.ACTION_UPDATE_VERSION, method = RequestMethod.POST)
    public @ResponseBody
    String updateVersion(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Version version;
            try {
                //parse jsonString to a Version Object
                version = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Version.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Version from json not null
            if (version != null) {
                if (version.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = version.getId();

                //validate 
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap<>();

                if (version.getUpdatedUser() != null) {
                    User user = dataService.getRowById(version.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + version.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                Version rootVersion = dataService.getRowById(version.getId(), Version.class);
                if (rootVersion == null) {
                    hashErrors.put("Version", "ID = " + version.getId() + " không tồn tại.");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                //not update company
                version.setCompanys(rootVersion.getCompanys());
                try {
                    //update Version to DB
                    int ret = dataService.updateSync(version);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(Version.class, id);
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
            } //Version from json null
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
     * get a Version info
     *
     * @param request is a HttpServletRequest
     * @return string json include Table info
     */
    @RequestMapping(value = MsalesConstants.MODULE.VERSION.ACTION_GET_VERSION, method = RequestMethod.POST)
    public @ResponseBody
    String getVersion(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Version version;
            try {
                //parse jsonString to a Version Object
                version = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Version.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Version from json not null
            if (version != null) {
                if (version.getCompanyId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("Version", "companyId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                int id = version.getCompanyId();
                //get Version from DB
                ParameterList parameterList = new ParameterList("isActive", 1);
                parameterList.add("companys.id", id);
                List<Version> listVersions = dataService.getListOption(Version.class, parameterList);

                if (listVersions.size() == 1) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, listVersions.get(0)));
                } //Version null
                else {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap<>();
                    hashErrors.put("Version", "không có version.");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
            } //Version from json null
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
