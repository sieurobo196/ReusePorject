/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.config.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.jfree.util.HashNMap;
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
import vn.itt.msales.entity.Equipment;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.SearchObject;

/**
 *
 * @author KhaiCH
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.EQUIPMENT.NAME)
public class MsalesEquipmentController extends CsbController {

    /**
     * create a Equipment
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.EQUIPMENT.ACTION_CREATE_EQUIPMENT, method = RequestMethod.POST)
    public @ResponseBody
    String createEquipment(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Equipment equipment;
            try {
                //parse jsonString to a Equipment Object
                equipment = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Equipment.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Equipment from json not null
            if (equipment != null) {
                //validate

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (equipment.getCompanyId() != null) {
                    Company company = dataService.getRowById(equipment.getCompanyId(), Company.class);
                    if (company == null) {
                        hashErrors.put("Company", "ID = " + equipment.getCompanyId() + " không tồn tại.");
                    }
                }
                if (equipment.getUserId() != null) {
                    User user = dataService.getRowById(equipment.getUserId(), User.class);
                    if (user == null) {
                        hashErrors.put("User", " ID = " + equipment.getUserId() + " không tồn tại.");
                    }
                }
                if (equipment.getCreatedUser() != null) {
                    User user = dataService.getRowById(equipment.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + equipment.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save Equipment to DB
                    int ret = dataService.insertRow(equipment);
                    //save Equipment success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, ret));
                    } //save Equipment failed
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
            } //Equipment from json null
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
     * update a Equipment
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.EQUIPMENT.ACTION_UPDATE_EQUIPMENT, method = RequestMethod.POST)
    public @ResponseBody
    String updateEquipment(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Equipment equipment;
            try {
                //parse jsonString to a Equipment Object
                equipment = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Equipment.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //tableName from json not null
            if (equipment != null) {
                if (equipment.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                int id = equipment.getId();
                //validate
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();

                if (equipment.getUserId() != null) {
                    User user = dataService.getRowById(equipment.getUserId(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + equipment.getUserId() + " không tồn tại.");
                    }
                }

                if (equipment.getUpdatedUser() != null) {
                    User user = dataService.getRowById(equipment.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + equipment.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                //khong cho update CompanyId
                Equipment rooteEquipment = dataService.getRowById(equipment.getId(), Equipment.class);
                if (rooteEquipment == null) {
                    return MsalesJsonUtils.notExists(Equipment.class, id);
                }
                equipment.setCompanys(rooteEquipment.getCompanys());

                try {
                    //update Equipment to DB
                    int ret = dataService.updateSync(equipment);
                    //update success
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(Equipment.class, id);
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
            } //Equipment from json null
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
     * delete a Equipment
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.EQUIPMENT.ACTION_DELETE_EQUIPMENT, method = RequestMethod.POST)
    public @ResponseBody
    String deleteEquipment(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Equipment equipment;
            try {
                //parse jsonString to a Equipment Object
                equipment = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Equipment.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Equipment from json not null
            if (equipment != null) {
                if (equipment.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                if (equipment.getDeletedUser() != null) {
                    User user = dataService.getRowById(equipment.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, equipment.getDeletedUser());
                    }
                }

                int id = equipment.getId();
                try {
                    //delete Equipment from DB
                    int ret = dataService.deleteSynch(equipment);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(Equipment.class, id);
                    }
                    //update Equipment success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update Equipment failed
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
            } //Equipment from json null
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
     * get a Equipment info
     *
     * @param request is a HttpServletRequest
     * @return string json include Equipment info
     */
    @RequestMapping(value = MsalesConstants.MODULE.EQUIPMENT.ACTION_GET_EQUIPMENT, method = RequestMethod.POST)
    public @ResponseBody
    String getEquipment(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            Equipment equipment;
            try {
                //parse jsonString to a Equipment Object
                equipment = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Equipment.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Equipment from json not null
            if (equipment != null) {
                if (equipment.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = equipment.getId();
                //get Equipment from DB
                equipment = dataService.getRowById(equipment.getId(), Equipment.class);
                //Equipment not null
                if (equipment != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, equipment));
                } //Equipment null
                else {
                    return MsalesJsonUtils.notExists(Equipment.class, id);
                }
            } //Equipment from json null
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
     * get List all Equipment
     *
     * @param request is a HttpServletRequest
     * @return string json include List Equipment
     */
    @RequestMapping(value = MsalesConstants.MODULE.EQUIPMENT.ACTION_GET_LIST_EQUIPMENT_BY_COMPANY_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListEquipmentByCompanyId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            Equipment equipment;
            try {
                //parse jsonString to a Equipment Object
                equipment = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Equipment.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Equipment from json not null
            if (equipment != null) {
                //companyId incorrect
                if (equipment.getCompanyId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("Equipment", "companyId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List Equipment by company id
                ParameterList parameterList = new ParameterList("companys.id", equipment.getCompanyId(), page.getPageNo(), page.getRecordsInPage());

                MsalesResults<Equipment> list = dataService.getListOption(Equipment.class, parameterList, true);
                String[] strings = {"companys"};
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list), strings);

            } //Equipment from json null
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
     * get List all Equipment
     *
     * @param request is a HttpServletRequest
     * @return string json include List Equipment
     */
    @RequestMapping(value = MsalesConstants.MODULE.EQUIPMENT.ACTION_GET_LIST_EQUIPMENT_BY_USER_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListEquipmentByUserId(HttpServletRequest request
    ) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            Equipment equipment;
            try {
                //parse jsonString to a Equipment Object
                equipment = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        Equipment.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //Equipment from json not null
            if (equipment != null) {
                //userId incorrect
                if (equipment.getUserId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("Equipment", "userId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }
                //get List Equipment by user id
                ParameterList parameterList = new ParameterList("users.id", equipment.getUserId(), page.getPageNo(), page.getRecordsInPage());

                MsalesResults<Equipment> list = dataService.getListOption(Equipment.class, parameterList, true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));
            } //companyId null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } //json content empty
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

    }

    /**
     * getListEquipment
     */
    @RequestMapping(value = MsalesConstants.MODULE.EQUIPMENT.ACTION_GET_LIST_EQUIPMENT, method = RequestMethod.POST)
    public @ResponseBody
    String getListEquiment(HttpServletRequest request) throws JsonMappingException, IOException {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        if (jsonString != null) {
            Equipment equipment = MsalesJsonUtils.getObjectFromJSON(jsonString, Equipment.class);
            if (equipment != null) {
                ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());
                MsalesResults<Equipment> list = dataService.getListOption(Equipment.class, parameterList, true);
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));

            } else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }

    }

    /**
     * lockEquipment
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.EQUIPMENT.ACTION_BLOCK_EQUIPMENT, method = RequestMethod.POST)
    public @ResponseBody
    String lockEquipment(HttpServletRequest request) {
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        if (jsonString != null) {
            Equipment equipment;
            try {
                // Convert JSOn string to LinkedHashMap object
                equipment = MsalesJsonUtils.getObjectFromJSON(jsonString, Equipment.class);
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            if (equipment != null) {
                if (equipment.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                if (equipment.getId() != null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    equipment = dataService.getRowById(equipment.getId(), Equipment.class);
                    if (equipment == null) {
                        hashErrors.put("User", "ID = " + equipment.getId() + " không tồn tại.");
                    }

                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                equipment = dataService.getRowById(equipment.getId(), Equipment.class);
                if (equipment != null) {
                    if (equipment.getIsActive()) {
                        equipment.setIsActive(false);
                    }

                    dataService.updateSync(equipment);
                }

            }
        }

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK));
    }

    /**
     * search Equipment
     *
     * @param request
     * @return
     */
    @RequestMapping(value = MsalesConstants.MODULE.EQUIPMENT.ACTION_SEARCH_EQUIPMENT, method = RequestMethod.POST)
    public @ResponseBody
    String searchEquipment(HttpServletRequest request) {
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
//        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            SearchObject searchObject;
            try {
                //parse jsonString to a search Object
                searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        SearchObject.class);
                //jsonString syntax incorrect
            } catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            String hql = "SELECT DISTINCT(E.id) as id, E.name as name, E.telNo as telNo,E.imei as imei,E.subscriberId as subscriberId ,"
                    + "E.isActive as isActive ,E.activeDate as activeDate ,E.version as version ,E.users as users,URC.userRoles as userRoles"
                    + " FROM Equipment as E,User as U,UserRoleChannel as URC, UserRole as UR"
                    + " WHERE E.users.id=U.id AND URC.users.id=U.id AND URC.userRoles.id=UR.id AND E.deletedUser=0 AND UR.deletedUser=0";
            //searchObject from json not null
            if (searchObject != null) {
                if (searchObject.getSearchText() != null) {
                    String key = searchObject.getSearchText().toString();
                    // parameterList.or("name", key, "like", "code", key, "like");
                    hql += " and ( E.name LIKE '%" + key + "%'" + " or E.subscriberId LIKE '%" + key + "%' ) ";
                }
                if (searchObject.getUserRoleId() !=null && searchObject.getUserRoleId() !=0) {
                        int statusId = Integer.parseInt(searchObject.getUserRoleId().toString());
                    try {
                        //parameterList.add("parents.id", statusId);
                        
                            hql += " and UR.id = " + statusId;
             
                    } catch (Exception ex) {
                        //error parse statusId
                    }
                }
                if (searchObject.getLocationId() != null && searchObject.getLocationId()!=0 ) {
                    try {
                        String key = searchObject.getLocationId().toString();
                        int locationId = Integer.parseInt(key);
                        hql += " and U.locations.id = " + locationId;
                    } catch (Exception ex) {
                        //error parse statusId
                    }

                }
//                
            }
            hql += " group by E.id";
            List<Equipment> lists = dataService.executeSelectHQL(Equipment.class, hql, true, page.getPageNo(), page.getRecordsInPage());
            List<HashNMap> count = dataService.executeSelectHQL(HashNMap.class, hql, true, 0, 0);

            MsalesResults<Equipment> listChannel = new MsalesResults<Equipment>();
            listChannel.setContentList(lists);
            listChannel.setCount(Long.parseLong(count.size() + ""));

            // MsalesResults<Channel> lists = dataService.getListOption(Channel.class, parameterList, true);
            String[] filters = {"companys", "parents", "isSalePoint", "contactPersonName", "fax", "email", "lat", "lng", "note", "value", "statusTypes"};

            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, listChannel), filters);
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }
}
