/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.channel.controller;

import java.util.ArrayList;
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
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.POSImg;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;

/**
 *
 * @author KhaiCH
 *
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.POS_IMG.NAME)
public class MsalesPOSImgController extends CsbController {

    /**
     * get a POSImg info
     *
     * @param request is a HttpServletRequest
     * @return string json include POSImg info
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS_IMG.ACTION_GET_POS_IMG, method = RequestMethod.POST)
    public @ResponseBody
    String getPOSImg(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            POSImg posImg;
            try {
                //parse jsonString to a posImg Object
                posImg = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POSImg.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //posImg from json not null
            if (posImg != null) {
                //posImg from json with correct Id
                if (posImg.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = posImg.getId();
                //get posImg from DB
                posImg = dataService.getRowById(posImg.getId(),
                        POSImg.class);
                //posImg not null
                if (posImg != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, posImg));
                } //posImg null
                else {
                    return MsalesJsonUtils.notExists(POSImg.class, id);
                }
            } //posImg from json null
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
     * create a POSImg
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS_IMG.ACTION_CREATE_POS_IMG, method = RequestMethod.POST)
    public @ResponseBody
    String createCPOSImg(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            POSImg posImg;
            try {
                //parse jsonString to a POSImg Object
                posImg = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POSImg.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //posImg from json not null
            if (posImg != null) {
                //validate info relation
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (posImg.getPosId() != null) {
                    POS pos = dataService.
                            getRowById(posImg.getPosId(), POS.class);
                    if (pos == null) {
                        hashErrors.put("POS", "ID = "
                                + posImg.getPosId() + " không tồn tại.");
                    }
                }
                if (posImg.getCreatedUser() != null) {
                    User user = dataService.getRowById(posImg.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + posImg.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save posImg to DB
                    int ret = dataService.insertRow(posImg);

                    //save posImg success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save posImg failed
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

            } //posImg from json null
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
     * update a POSImg
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS_IMG.ACTION_UPDATE_POS_IMG, method = RequestMethod.POST)
    public @ResponseBody
    String updatePOSImg(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            POSImg posImg;
            try {
                //parse jsonString to a POSImg Object
                posImg = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POSImg.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //posImg from json not null
            if (posImg != null) {
                if (posImg.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = posImg.getId();
                //validate info relation
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (posImg.getPosId() != null) {
                    POS pos = dataService.
                            getRowById(posImg.getPosId(), POS.class);
                    if (pos == null) {
                        hashErrors.put("POS", "ID = "
                                + posImg.getPosId() + " không tồn tại.");
                    }
                }
                if (posImg.getUpdatedUser() != null) {
                    User user = dataService.getRowById(posImg.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + posImg.getUpdatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    int ret = dataService.updateSync(posImg);
                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(POSImg.class, id);
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
            } //posImg from json null
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
     * delete a POSImg
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS_IMG.ACTION_DELETE_POS_IMG, method = RequestMethod.POST)
    public @ResponseBody
    String deletePOSImg(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            POSImg posImg;
            try {
                //parse jsonString to a POSImg Object
                posImg = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POSImg.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //posImg from json not null
            if (posImg != null) {
                if (posImg.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }

                if (posImg.getDeletedUser() != null) {
                    User user = dataService.getRowById(posImg.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, posImg.getDeletedUser());
                    }
                }

                int id = posImg.getId();
                try {
                    //delete posImg from DB
                    int ret = dataService.deleteSynch(posImg);

                    if (ret == -2) {
                        return MsalesJsonUtils.notExists(POSImg.class, id);
                    }

                    //update posImg success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //update posImg failed
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

            } //posImg from json null
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
     * get List POSImg by Channel Id
     *
     * @param request is a HttpServletRequest
     * @return string json include List Channel
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS_IMG.ACTION_GET_LIST_POS_IMG_BY_POS_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListPOSImgByPOSId(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            POSImg posImg;
            try {
                //parse jsonString to a POSImg Object
                posImg = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POSImg.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //posImg from json not null
            if (posImg != null) {
                if (posImg.getPosId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("POSImg", "posId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                ParameterList parameterList = new ParameterList("poss.id", posImg.getPosId(),
                        page.getPageNo(), page.getRecordsInPage());

                MsalesResults<POSImg> list = dataService.getListOption(POSImg.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));

            } //posImg from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }
    }
    
    /**
     * DuanND getListPOSImg xem ảnh chụp, danh sách điểm mới.
     * @param request is jsonString have POS_ID
     * @return info of POSImg
     */
    @RequestMapping(value = MsalesConstants.MODULE.POS_IMG.ACTION_GET_LIST_POS_IMG, method = RequestMethod.POST)
    public @ResponseBody String getListPOSImg(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            POSImg channelSalePointInfoImg;
            try {
                //parse jsonString to a POSImg Object
                channelSalePointInfoImg = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        POSImg.class);
            }//jsonString syntax incorrect//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //channelSalePointInfoImg from json not null
            if (channelSalePointInfoImg != null) {
                if (channelSalePointInfoImg.getPosId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_ID_NULL, MsalesValidator.MCP_POS_ID_NULL ));
                }
                ParameterList parameterList = new ParameterList("poss.id", channelSalePointInfoImg.getPosId());
                //parameterList.add("hierarchy", 0);
                List<POSImg> list2 = dataService.getListOption(POSImg.class, parameterList);
                List<POSImg> lists = new ArrayList<POSImg>();
                for(POSImg posImg : list2){
                	if(posImg.getPoss().getHierarchy() == 0){
                		lists.add(posImg);
                	}
                }
                MsalesResults<POSImg> listMCPs = new MsalesResults<POSImg>();
                listMCPs.setCount(Long.parseLong(lists.size()+""));
                if(page.getPageNo() == 1){
                	if(page.getRecordsInPage() == 10){
                		if(lists.size() <= 10){
                			listMCPs.setContentList(lists);
                		}else{
                			listMCPs.setContentList(lists.subList(0, 10));
                		}
                	}else{
                		if(lists.size() <= page.getRecordsInPage()){
                			listMCPs.setContentList(lists);
                		}else{
                			listMCPs.setContentList(lists.subList(0, page.getRecordsInPage()));
                		}
                	}
                }else{
                	if(page.getRecordsInPage() == 10){
                		if(lists.size() <= 10){
                			
                		}else{
                			if(lists.size()/10 + 1 == page.getPageNo()){
                				listMCPs.setContentList(lists.subList((page.getPageNo() - 1)*page.getRecordsInPage(), lists.size()));
                			}else if(lists.size()/10 + 1 >= page.getPageNo()){
                				listMCPs.setContentList(lists.subList((page.getPageNo() - 1)*page.getRecordsInPage(), page.getPageNo()*page.getRecordsInPage()));
                			}else{
                				
                			}
                		}
                	}else{
                		if(lists.size() <= page.getRecordsInPage()){
                			
                		}else{
                			if(lists.size()/page.getRecordsInPage() + 1 == page.getPageNo()){
                				listMCPs.setContentList(lists.subList((page.getPageNo() - 1)*page.getRecordsInPage(), lists.size()));
                			}else if(lists.size()/page.getRecordsInPage() + 1 >= page.getPageNo()){
                				listMCPs.setContentList(lists.subList((page.getPageNo() - 1)*page.getRecordsInPage(), page.getPageNo()*page.getRecordsInPage()));
                			}else{
                				
                			}
                		}
                	}
                }
                
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, listMCPs));

            } //channelSalePointInfoImg from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                    .create(MsalesStatus.NULL));
        }
    }

    
    
}
