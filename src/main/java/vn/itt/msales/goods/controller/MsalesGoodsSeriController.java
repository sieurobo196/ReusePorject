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
import vn.itt.msales.entity.GoodsSeri;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.csb.controller.CsbController;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.logex.MSalesException;

;

/**
 *
 * @author Cutx
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.GOODS_SERI.NAME)
public class MsalesGoodsSeriController extends CsbController {

    /**
     * get a GoodsSeri info
     *
     * @param request is a HttpServletRequest
     * @return string json include GoodsSeri info
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_SERI.ACTION_GET_GOODS_SERI, method = RequestMethod.POST)
    public @ResponseBody
    String getGoodsSeri(HttpServletRequest request) {
          // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null

        if (jsonString != null) {
            GoodsSeri goodsSeri;
            try {
                //parse jsonString to a Channel Object
                goodsSeri = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        GoodsSeri.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //channel from json not null
            if (goodsSeri != null) {
                if (goodsSeri.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = goodsSeri.getId();
                //get channel from DB
                goodsSeri = dataService.getRowById(goodsSeri.getId(),
                        GoodsSeri.class);
                //channel not null
                if (goodsSeri != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, goodsSeri));
                } //channel null
                else {
                    return MsalesJsonUtils.notExists(GoodsSeri.class, id);
                }
            } //channel from json null
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
     * create a GoodsSeri
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_SERI.ACTION_CREATE_GOODS_SERI, method = RequestMethod.POST)
    public @ResponseBody
    String createGoodsSeri(HttpServletRequest request) {

        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            GoodsSeri goodsSeri;
            try {
                //parse jsonString to a GoodsSeri Object
                goodsSeri = MsalesJsonUtils.getObjectFromJSON(jsonString, GoodsSeri.class);
              }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }

            //GoodsSeri from json not null
            if (goodsSeri != null) {

                if (goodsSeri.getGoodsId() != null) {
                    //get Goods from GoodsSeri
                    Goods goods = dataService.getRowById(goodsSeri.getGoodsId(), Goods.class);
                    //Goods is not exist
                    if (goods == null) {
                        //return message warning Goods is not exist in DB
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("Goods",
                                "Goods with ID = " + goodsSeri.getGoodsId() + " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                }
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (goodsSeri.getCreatedUser() != null) {
                    User user = dataService.getRowById(goodsSeri.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "User with ID = " + goodsSeri.getCreatedUser() + " không tồn tại.");
                    }
                }
                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }
                try {

                    //save GoodsSeri to DB
                    int ret = dataService.insertRow(goodsSeri);

                    //save Succcess
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                    } //save failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_INSERT_FAIL));
                    }
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

            } //GoodsSeri from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * update a GoodsSeri
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_SERI.ACTION_UPDATE_GOODS_SERI, method = RequestMethod.POST)
    public @ResponseBody
    String updateGoodsSeri(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null

        if (jsonString != null) {
            GoodsSeri goodsSeri;
            try {
                goodsSeri = MsalesJsonUtils.getObjectFromJSON(jsonString, GoodsSeri.class);
              }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //GoodsSeri from json not null
            if (goodsSeri != null) {
                if (goodsSeri.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));

                }
                //get Goods from GoodsSeri
                if (goodsSeri.getGoodsId() != null) {
                    Goods goods = dataService.getRowById(goodsSeri.getGoodsId(), Goods.class);
                    if (goods == null) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("Goods",
                                "Goods with ID = " + goodsSeri.getGoodsId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }

                }
                int ret;
                try {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    if (goodsSeri.getUpdatedUser() != null) {
                        User user = dataService.getRowById(goodsSeri.getUpdatedUser(), User.class);
                        if (user == null) {
                            hashErrors.put("User", "User with ID = " + goodsSeri.getUpdatedUser() + " không tồn tại.");
                        }
                    }
                    if (!hashErrors.isEmpty()) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                    //update GoodsSeri to DB
                    ret = dataService.updateSync(goodsSeri);
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));
                }

                //update success
                if (ret == -2) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                    hashErrors.put("GoodsSeri",
                            "GoodsSeri with ID = " + goodsSeri.getId() + " không tồn tại trên Database");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                } else if (ret > 0) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                } //update failed
                else {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_UPDATE_FAIL));
                }

            } //GoodsSeri from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    }

    /**
     * delete a GoodsSeri
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_SERI.ACTION_DELETE_GOODS_SERI, method = RequestMethod.POST)
    public @ResponseBody
    String deleteGoodsSeri(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        //jsonString not null
        if (jsonString != null) {
            GoodsSeri goodsSeri;
            try {
                //parse jsonString to a GoodsSeri Object
                goodsSeri = MsalesJsonUtils.getObjectFromJSON(jsonString, GoodsSeri.class);
           }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //GoodsSeri from json not null
            if (goodsSeri != null) {
                if (goodsSeri.getId() == null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_ID_NULL));
                }
                
                if (goodsSeri.getDeletedUser()!= null) {
                    User user = dataService.getRowById(goodsSeri.getDeletedUser(), User.class);
                    if (user == null) {
                        return MsalesJsonUtils.notExists(User.class, goodsSeri.getDeletedUser());
                    }
                }
                try {
                    
                    //delete GoodsSeri from DB
                    int ret = dataService.deleteSynch(goodsSeri);
                    //delete GoodsSeri success
                    if (ret == -2) {
                        LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                        hashErrors.put("GoodsSeri",
                                "GoodsSeri with ID = " + goodsSeri.getId() + " không tồn tại trên Database");
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    } else if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, null));
                    } //GoodsCategory failed
                    else {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.SQL_DELETE_FAIL));
                    }
                } catch (Exception e) {
                    if (e instanceof ConstraintViolationException) {
                        return MsalesJsonUtils.jsonValidate(e);
                    }//else
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(e.getMessage()));
                }
            } //GoodsCategory from json null
            else {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }

        } //jsonString null
        else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_CONTENTS_EMPTY));
        }
    
    }

    /**
     * get List all GoodsSeri
     *
     * @param request is a HttpServletRequest
     * @return string json include List goodsSeri
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_SERI.ACTION_GET_LIST_SERI, method = RequestMethod.POST)
    public @ResponseBody
    String getListGoodsSeri(HttpServletRequest request) {

        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

        MsalesResults<GoodsSeri> list = dataService.getListOption(GoodsSeri.class, parameterList,true);
        if (page.getPageNo() > 0 && page.getRecordsInPage() > 0) {
            if (list == null) {
                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
            }
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list));
        } else {
            return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.PAGE_INVALID));
        }

    }

    /**
     * get List GoodsSeri by Goods
     *
     * @param request is a HttpServletRequest
     * @return string json include List GoodsSeri
     */
    @RequestMapping(value = MsalesConstants.MODULE.GOODS_SERI.ACTION_GET_LIST_SERI_BY_GOODS_ID, method = RequestMethod.POST)
    public @ResponseBody
    String getListGoodsSeriByGoodsId(HttpServletRequest request) {
      String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null

        if (jsonString != null) {
            GoodsSeri goodsSeri;
            try {
                //parse jsonString to a Channel Object
                goodsSeri = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        GoodsSeri.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //Schedule from json not null
            if (goodsSeri != null) {
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (goodsSeri.getGoodsId()!= null) {
                    Goods goods = dataService.getRowById(goodsSeri.getGoodsId(), Goods.class);
                    if (goods == null) {
                        hashErrors.put("Goods",
                                "Goods with ID = " + goodsSeri.getGoodsId()+ " không tồn tại trên Database");
                        String s = MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                        System.out.println(s);
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                    }
                } else {
                    hashErrors.put("goodsId", "  không được null!");
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));
                }

                //get List MCP by implementEmployeeId 
                ParameterList parameterList = new ParameterList("goodss.id", goodsSeri.getGoodsId(), page.getPageNo(), page.getRecordsInPage());

                MsalesResults<GoodsSeri> list = dataService.getListOption(GoodsSeri.class, parameterList,true);

                return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                        .create(HttpStatus.OK, list));

            } //goods from json null
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
