/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.config.controller;

import java.util.Date;
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
import vn.itt.msales.entity.CompanyConfig;
import vn.itt.msales.entity.CompanyConfigDetails;
import vn.itt.msales.entity.Goods;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.MCPSalesDetails;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserRole;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.logex.MSalesException;

/**
 *
 * @author KhaiCH
 */
@Controller
@RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIG.NAME)
public class MsalesCompanyConfigController extends CsbController {

    /**
     * create a CompanyConfig
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIG.ACTION_CREATE_COMPANY_CONFIG, method = RequestMethod.POST)
    public @ResponseBody
    String createCompanyConfig(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            CompanyConfig companyConfig;
            try {
                //parse jsonString to a CompanyConfig Object
                companyConfig = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyConfig.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyConfig from json not null
            if (companyConfig != null) {
                //validate

                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                if (companyConfig.getCompanyId() != null) {
                    Company company = dataService.getRowById(companyConfig.getCompanyId(), Company.class);
                    if (company == null) {
                        hashErrors.put("Company", "ID = " + companyConfig.getCompanyId() + " không tồn tại.");
                    }
                }

                if (companyConfig.getUserRoleId() != null) {
                    UserRole userRole = dataService.getRowById(companyConfig.getUserRoleId(), UserRole.class);
                    if (userRole == null) {
                        hashErrors.put("UserRole", "ID = " + companyConfig.getUserRoleId() + " không tồn tại.");
                    }
                }
                if (companyConfig.getCreatedUser() != null) {
                    User user = dataService.getRowById(companyConfig.getCreatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + companyConfig.getCreatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                try {
                    //save CompanyConfig to DB
                    int ret = dataService.insertRow(companyConfig);
                    //save CompanyConfig success
                    if (ret > 0) {
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                .create(HttpStatus.OK, null));
                    } //save CompanyConfig failed
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

    /**
     * update a CompanyConfig
     *
     * @param request is a HttpServletRequest
     * @return string json include MsalesResponse
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIG.ACTION_UPDATE_COMPANY_CONFIG, method = RequestMethod.POST)
    public @ResponseBody
    String updateCompanyConfig(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            CompanyConfig companyConfig;
            try {
                //parse jsonString to a CompanyConfig Object
                companyConfig = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyConfig.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyConfig from json not null
            if (companyConfig != null) {
                if (companyConfig.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = companyConfig.getId();

                //validate
                LinkedHashMap<String, String> hashErrors = new LinkedHashMap();

                if (companyConfig.getUserRoleId() != null) {
                    UserRole userRole = dataService.getRowById(companyConfig.getUserRoleId(), UserRole.class);
                    if (userRole == null) {
                        hashErrors.put("UserRole", "ID = " + companyConfig.getUserRoleId() + " không tồn tại.");
                    }
                }
                if (companyConfig.getUpdatedUser() != null) {
                    User user = dataService.getRowById(companyConfig.getUpdatedUser(), User.class);
                    if (user == null) {
                        hashErrors.put("User", "ID = " + companyConfig.getUpdatedUser() + " không tồn tại.");
                    }
                }

                if (!hashErrors.isEmpty()) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                }

                
                //khong cho update CompanyId
                CompanyConfig rootCompanyConfig = dataService.getRowById(companyConfig.getId(), CompanyConfig.class);
                if (rootCompanyConfig == null) {
                    return MsalesJsonUtils.notExists(CompanyConfig.class, id);
                }
                companyConfig.setCompanys(rootCompanyConfig.getCompanys());

                try {
                    //update CompanyConfig to DB
                    int ret = dataService.updateSync(companyConfig);
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

   
    /**
     * get a CompanyConfig info
     *
     * @param request is a HttpServletRequest
     * @return string json include CompanyConfig info
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIG.ACTION_GET_COMPANY_CONFIG, method = RequestMethod.POST)
    public @ResponseBody
    String getCompanyConfig(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                .toString();
        //jsonString not null
        if (jsonString != null) {
            CompanyConfig companyConfig;
            try {
                //parse jsonString to a CompanyConfig Object
                companyConfig = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyConfig.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyConfig from json not null
            if (companyConfig != null) {
                if (companyConfig.getId() == null) {
                    return MsalesJsonUtils.idNull();
                }
                int id = companyConfig.getId();
                //get CompanyConfig from DB
                companyConfig = dataService.getRowById(companyConfig.getId(),
                        CompanyConfig.class);
                //CompanyConfig not null
                if (companyConfig != null) {
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(HttpStatus.OK, companyConfig));
                } //CompanyConfig null
                else {
                    return MsalesJsonUtils.notExists(CompanyConfig.class, id);
                }
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

    /**
     * get List all CompanyConfig
     *
     * @param request is a HttpServletRequest
     * @return string json include List CompanyConfig
     */
    @RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIG.ACTION_GET_LIST_COMPANY_CONFIG, method = RequestMethod.POST)
    public @ResponseBody
    String getListCompanyConfig(HttpServletRequest request) {
        // get jsonString from CSB 
        String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
        MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        //jsonString not null
        if (jsonString != null) {
            CompanyConfig companyConfig;
            try {
                //parse jsonString to a CompanyConfig Object
                companyConfig = MsalesJsonUtils.getObjectFromJSON(jsonString,
                        CompanyConfig.class);
            }//jsonString syntax incorrect
            catch (Exception ex) {
                // check field in JSOn request not match with field in JSON.
                return MsalesJsonUtils.validateFormat(ex);
            }
            //CompanyConfig from json not null
            if (companyConfig != null) {
                //companyId null
                if (companyConfig.getCompanyId() == null) {
                    LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                    hashErrors.put("CompanyConfig", "companyId không được null!");
                    return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                            .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));

                }
                
                

                //get List CompanyConfig by company id
                ParameterList parameterList = new ParameterList("companys.id", companyConfig.getCompanyId(), page.getPageNo(), page.getRecordsInPage());

                MsalesResults<CompanyConfig> list = dataService.getListOption(CompanyConfig.class, parameterList,true);

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
    /**
     * DuanND: search company_config
     * @param request is a text to search
     * @return info company_config
     */
    @SuppressWarnings("unchecked")
   	@RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIG.ACTION_SEARCH_COMPANY_CONFIG, method = RequestMethod.POST)
       public @ResponseBody String searchCompanyConfig(HttpServletRequest request) {
           //get List GoodsCategory from DB
           MsalesPageRequest page = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
           ParameterList parameterList = new ParameterList(page.getPageNo(), page.getRecordsInPage());

           // get jsonString from CSB 
           String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                   .toString();
           //jsonString not null
           if (jsonString != null) {
               LinkedHashMap<String, Object> searchObject;
               try {
                   //parse jsonString to a search Object
                   searchObject = MsalesJsonUtils.getObjectFromJSON(jsonString,
                           LinkedHashMap.class);
                   //jsonString syntax incorrect
               } catch (Exception ex) {
                   // check field in JSOn request not match with field in JSON.
                   return MsalesJsonUtils.validateFormat(ex);
               }

               //searchObject from json not null
               if (searchObject != null) {
                   if (searchObject.get("searchText") != null) {
                       String key = searchObject.get("searchText").toString();
                      // parameterList.or("name", key, "like", "{alias}.id", key, "likeSQL");
                       parameterList.add("name", key, "like");
                      // parameterList.or("companys", key,"like");
                   }
               }
               
               List<CompanyConfig> list = dataService.getListOption(CompanyConfig.class, parameterList);
               for(CompanyConfig companyConfig : list){
            	   if(companyConfig != null){
            		   ParameterList parameterList2 = new ParameterList("companyConfigs.id", companyConfig.getId());
            		   List<CompanyConfigDetails> list2 = dataService.getListOption(CompanyConfigDetails.class, parameterList2);
            		   if(list2 != null){
            			   companyConfig.setCompanyConfigDetails(list2);
            		   }
            	   }
               }
               String[] strings = {"companys"};
               return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.OK, list), strings);
           } //jsonString null
           else {
               return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                       .create(MsalesStatus.JSON_CONTENTS_EMPTY));
           }
       }
       
       /**
        * lock company_config_details
        * @param request is json have isActive, id to lock company_config_details
        * @return contents
        */
       @RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIG.ACTION_LOCK_COMPANY_CONFIG, method = RequestMethod.POST)
       public @ResponseBody String lockCompanyConfig(HttpServletRequest request) {
           // get jsonString from CSB 
           String jsonString = request.getAttribute(MsalesConstants.CONTENTS)
                   .toString();
           //jsonString not null
           if (jsonString != null) {
               CompanyConfig pos;
               try {
                   //parse jsonString to a POS Object
                   pos = MsalesJsonUtils.getObjectFromJSON(jsonString,
                           CompanyConfig.class);
               }//jsonString syntax incorrect//jsonString syntax incorrect
               catch (Exception ex) {
                   // check field in JSOn request not match with field in JSON.
                   return MsalesJsonUtils.validateFormat(ex);
               }

               //channelSalePointInformation from json not null
               if (pos != null) {
                   if (pos.getId() == null) {
                       return MsalesJsonUtils.idNull();
                   }
                   int id = pos.getId();
                   //validate info relation
                   LinkedHashMap<String, String> hashErrors = new LinkedHashMap();
                   CompanyConfig pos2 = dataService.getRowById(id, CompanyConfig.class);
                   if(pos2 == null){
                   	hashErrors.put("CompanyConfig", "ID = " + pos.getId() + " không tồn tại.");
                   }
                   if (pos.getUpdatedUser() != null) {
                       User user = dataService.getRowById(pos.getUpdatedUser(), User.class);
                       if (user == null) {
                           hashErrors.put("User", "ID = " + pos.getUpdatedUser() + " không tồn tại.");
                       }
                   }
                   pos2.setUpdatedUser(pos.getUpdatedUser());
                   pos2.setUpdatedAt(new Date());
                   if (!hashErrors.isEmpty()) {
                       return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                               .create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));
                   }
                   
                   try {
                       //update channelSalePointInformation to DB
                       int ret = dataService.updateRow(pos2);
                       if (ret == -2) {
                           return MsalesJsonUtils.notExists(CompanyConfig.class, id);
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
               } //channelSalePointInformation from json null
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
        * DuanND: createCompanyConfigAndDetails
        * @param request is JsonString have data to create
        * @return a jsonString contains code, status, contents
        */
       @RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIG.ACTION_CREATE_COMPANY_CONFIG_AND_DETAILS, method = RequestMethod.POST)
       public @ResponseBody String createCompanyConfigAndDetails(HttpServletRequest request) {
           // get jsonString from CSB 
           String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
           //jsonString not null
           if (jsonString != null) {
               CompanyConfig mcp;

               try {
                   //parse jsonString to a mcp Object
                   mcp = MsalesJsonUtils.getObjectFromJSON(jsonString, CompanyConfig.class);
               } catch (Exception e) {
                   if (e instanceof ConstraintViolationException) {
                       return MsalesJsonUtils.jsonValidate(e);
                   }//else
                   return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
               }
               //mcp from json not null
               if (mcp != null) {
                   LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                   List<CompanyConfigDetails> mcpSalesDetailss = mcp.getCompanyConfigDetails();
                   if (mcpSalesDetailss != null) {
                       for (CompanyConfigDetails mSalesDetails : mcpSalesDetailss) {
                           if (mSalesDetails != null) {
                               if (mSalesDetails.getUserRoleId() != null) {
                                   UserRole goods = dataService.getRowById(mSalesDetails.getUserRoleId(), UserRole.class);
                                   if (goods == null) {
                                       hashErrors.put("UserRole", "userRoleId = " + mSalesDetails.getUserRoleId() + " không tồn tại!");
                                   }
                               }
                               
                           } else {
                               return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NULL));
                           }
                       }
                   } else {
                       return MsalesJsonUtils.getJSONFromOject(MsalesResponse.
                    		   create(MsalesStatus.JSON_FIELD_REQUIRED, "companyConfigDetails bắt buộc nhập"));
                   }
                   
                   if (mcp.getUserRoleId() != null) {
                       UserRole goods = dataService.getRowById(mcp.getUserRoleId(), UserRole.class);
                       if (goods == null) {
                           hashErrors.put("UserRole", "userRoleId = " + mcp.getUserRoleId() + " không tồn tại!");
                       }
                   }
                  User user;
                  if(mcp.getCreatedUser() != null){
                	  user = dataService.getRowById(mcp.getCreatedUser(), User.class);
                      if (user == null) {
                              hashErrors.put("User", "User with ID = " + mcp.getCreatedUser() + " không tồn tại.");                      
                      }else{
                    	  mcp.setCompanyId(user.getCompanys().getId());
                      }
                  }
                   
                   //Company company = dataService.getRowById(user.getCompanyId(), Company.class);
                   
                   mcp.setIsActive(1);
                   if (hashErrors.size() > 0) {
                       // return message warning tableName or property is not exist
                       // in DB
                       return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));

                   }

                   int ret1 = 0;
                   int ret2 = 0;
                   try {
                       //save status to DB
                       ret1 = dataService.insertRow(mcp);
                   } catch (MSalesException e) {
                       Exception ex = (Exception) e.getCause().getCause();
                       if (ex instanceof javax.validation.ConstraintViolationException) {
                           return MsalesJsonUtils.jsonValidate(ex);
                       }
                   }

                   if (ret1 > 0) {
                       for (CompanyConfigDetails mcpSalesDetails : mcpSalesDetailss) {
                           mcpSalesDetails.setCompanyConfigId(ret1);
                           mcpSalesDetails.setCreatedUser(mcp.getCreatedUser());
                           mcpSalesDetails.setIsActive(1);
                           try {
                               //save status to DB
                               ret2 = dataService.insertRow(mcpSalesDetails);
                           } catch (MSalesException e) {
                               Exception ex = (Exception) e.getCause().getCause();
                               if (ex instanceof javax.validation.ConstraintViolationException) {
                                   return MsalesJsonUtils.jsonValidate(ex);
                               }
                           }
                       }
                   }
                   //save Succcess
                   if (ret2 > 0) {
                       return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                               .create(HttpStatus.OK, null));
                   } //save failed
                   else {
                       return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                               .create(MsalesStatus.SQL_INSERT_FAIL));
                   }
               } //mcp from json null
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
        * DuanND updateCompanyConfigAndDetails
        * @param request is jsonString have data to update
        * @return is code, status, contents
        */
       @RequestMapping(value = MsalesConstants.MODULE.COMPANYCONFIG.ACTION_UPDATE_COMPANY_CONFIG_AND_DETAILS, method = RequestMethod.POST)
       public @ResponseBody String updateCompanyConfigAndDetails(HttpServletRequest request) {
           // get jsonString from CSB 
           String jsonString = request.getAttribute(MsalesConstants.CONTENTS).toString();
           //jsonString not null
           if (jsonString != null) {
               CompanyConfig mcp;

               try {
                   //parse jsonString to a mcp Object
                   mcp = MsalesJsonUtils.getObjectFromJSON(jsonString, CompanyConfig.class);
               } catch (Exception e) {
                   if (e instanceof ConstraintViolationException) {
                       return MsalesJsonUtils.jsonValidate(e);
                   }//else
                   return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_VALUE_INVALID, e.getMessage()));
               }
               //mcp from json not null
               if (mcp != null) {
                   LinkedHashMap<String, String> hashErrors = new LinkedHashMap<String, String>();
                   if (mcp.getId() != null) {
                       CompanyConfig mcp2 = dataService.getRowById(mcp.getId(), CompanyConfig.class);
                       if (mcp2 == null) {
                           hashErrors.put("CompanyConfig", "ID = " + mcp.getId() + " không tồn tại!");
                       }

                       List<CompanyConfigDetails> mcpSalesDetailss = mcp.getCompanyConfigDetails();
                      if(mcpSalesDetailss != null){
                       for (CompanyConfigDetails mSalesDetails : mcpSalesDetailss) {
                           if (mSalesDetails != null) {
                               if (mSalesDetails.getId() != null) {
                            	   CompanyConfigDetails configDetails = dataService.getRowById(mSalesDetails.getId(), CompanyConfigDetails.class);
                            	   if(configDetails == null){
                                       hashErrors.put("CompanyConfigDetails", "Id = " + mSalesDetails.getId() + " không tồn tại!");
                            	   }
                            	   if (mSalesDetails.getUserRoleId() != null) {
                                       UserRole goods = dataService.getRowById(mSalesDetails.getUserRoleId(), UserRole.class);
                                       if (goods == null) {
                                           hashErrors.put("UserRole", "userRoleId = " + mSalesDetails.getUserRoleId() + " không tồn tại!");
                                       }
                                   }

                               } else {
                                   hashErrors.put("CompanyConfigDetails", "ID không được " + mSalesDetails.getId());
                               }
                           } else {
                               hashErrors.put("CompanyConfigDetails", "Danh sách companyConfigDetails không được null ");
                           }
                       }
                      }else{
                    	  return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, "companyConfigDetails bắt buộc nhập"));
                      }
                      if (mcp.getUserRoleId() != null) {
                          UserRole goods = dataService.getRowById(mcp.getUserRoleId(), UserRole.class);
                          if (goods == null) {
                              hashErrors.put("UserRole", "userRoleId = " + mcp.getUserRoleId() + " không tồn tại!");
                          }
                      }
                      User user;
                      if(mcp.getUpdatedUser() != null){
                    	  user = dataService.getRowById(mcp.getUpdatedUser(), User.class);
                          if (user == null) {
                                  hashErrors.put("User", "User with ID = " + mcp.getUpdatedUser() + " không tồn tại.");                      
                          }else{
                        	  mcp.setCompanyId(user.getCompanys().getId());
                          }
                      }
                      //Company company = dataService.getRowById(user.getCompanyId(), Company.class);
                      //mcp.setCompanyId(user.getCompanys().getId());
                      mcp.setIsActive(1);
                      if (hashErrors.size() > 0) {
                          // return message warning tableName or property is not exist
                          // in DB
                          return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.NOT_EXISTS_IN_DATABASE, hashErrors));

                      }

                       int ret1 = 0;
                       int ret2 = 0;
                       try {
                           //save status to DB
                           ret1 = dataService.updateSync(mcp);
                       } catch (Exception e) {
                           if (e instanceof ConstraintViolationException) {
                               return MsalesJsonUtils.jsonValidate(e);
                           }
                       }

                       if (ret1 > 0) {
                           for (CompanyConfigDetails mcpSalesDetails : mcpSalesDetailss) {
                               mcpSalesDetails.setCompanyConfigId(mcp.getId());
                               mcpSalesDetails.setUpdatedUser(mcp.getUpdatedUser());
                               mcpSalesDetails.setIsActive(1);
                               try {
                                   //save status to DB
                                   ret2 = dataService.updateSync(mcpSalesDetails);
                               } catch (Exception e) {
                                   if (e instanceof ConstraintViolationException) {
                                       return MsalesJsonUtils.jsonValidate(e);
                                   }
                               }
                           }
                       }
                       //save Succcess
                       if (ret2 > 0) {
                           return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                   .create(HttpStatus.OK, null));
                       } //save failed
                       else {
                           return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                                   .create(MsalesStatus.SQL_UPDATE_FAIL));
                       }
                   } else {
                       hashErrors.put("CompanyConfig", "ID không được " + mcp.getId());
                       return MsalesJsonUtils.getJSONFromOject(MsalesResponse
                               .create(MsalesStatus.JSON_FIELD_REQUIRED, hashErrors));

                   }
                   //mcp from json null
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

       
}
