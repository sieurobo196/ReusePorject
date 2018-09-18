/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import vn.itt.msales.app.service.AppService;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.common.ValidatorUtil;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.Location;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.SalesStock;
import vn.itt.msales.entity.SalesTrans;
import vn.itt.msales.entity.Status;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author cshiflett
 */
public class POSServiceImpl implements POSService {

    @Autowired
    private DataService dataService;
    @Autowired
    private AppService  appService;

    @Override
    public MsalesResults<POS> searchPOSByParams(MsalesLoginUserInf userInf, Filter filter, LinkedHashMap<String, String> params, int page, int size) {
        MsalesResults<POS> results = new MsalesResults<>();
        try {
            String hqlList = "SELECT POS ";
            String hqlCount = "SELECT COUNT(POS) AS VAL";
            String hql = " FROM POS as POS "
                    + " JOIN POS.channels as Channel "
                    + " JOIN POS.statuss as Status "
                    + " JOIN POS.locations as Location ";
            if (params.size() > 0) {
                Boolean _and = false;
                hql += " WHERE POS.deletedUser = 0 ";

                if (params.get("channels.companys.id") != null) {
                    hql += " AND Channel.companys.id = " + params.get("channels.companys.id");
                    _and = true;
                }

                //get List Channel 
                List<List<OptionItem>> optionItems = filter.getChannelList();	                
                for(List<OptionItem> ops : optionItems){
                	if(ops.size() > 1){
                		String string = "";
                		for(OptionItem optionItem : ops){
                			string += optionItem.getId() + ",";
                		}
                		string += "''";
                		String hqlChannel = "SELECT C.fullCode as fullCode FROM Channel as C"
                				+ " WHERE deletedUser = 0 and C.id IN ("+string+")";
                		List<HashMap> listFullCode = dataService.executeSelectHQL(HashMap.class, hqlChannel, true, 0, 0);
                		String channelCode = "";
                		for(HashMap hm : listFullCode){
                			channelCode += " OR Channel.fullCode LIKE '" + hm.get("fullCode") + "%'";
                		}
                		if(channelCode.length() > 3){
                			hql += " AND ("+ channelCode.substring(3)+") ";
                			_and = true;
                		}else{
                			hql += " AND Channel.id = 0";
                			_and = true;
                		}
                		break;
                	}
                }
                
                if (params.get("channelId") != null) {
					Channel channel = dataService.getRowById(Integer.parseInt(params.get("channelId")),
							Channel.class);
					if (channel != null) {
						if (_and)
							hql += " AND ";
						String channelCode = channel.getFullCode();
						hql += " Channel.fullCode LIKE '" + channelCode + "%'";
						_and = true;
					}
				}
                
                if(userInf.getUserRoleCode().equals("USER_ROLE_ADMIN_COMPANY")){
					
				}else{
					
	                
				}
               // }               
                if (params.get("hierarchyType") != null) {
                    if (_and) {
                        hql += " AND ";
                    }
                    int type = Integer.parseInt(params.get("hierarchyType"));
                    if (type == 0) {
                        hql += " POS.hierarchy = 0";
                    } else {
                        hql += " POS.hierarchy > 0";
                    }
                    _and = true;
                }

                if (params.get("statusId") != null) {
                    if (_and) {
                        hql += " AND ";
                    }
                    hql += " Status.id =" + Integer.parseInt(params.get("statusId"));
                    _and = true;
                }

                if (params.get("createdUser") != null) {
                    if (_and) {
                        hql += " AND ";
                    }
                    hql += " POS.createdUser =" + Integer.parseInt(params.get("createdUser"));
                    _and = true;
                } 
                /*else {
                    String string = "";
                    if (employeeList.size() > 1) {
                        for (int i = 1; i < employeeList.size(); i++) {
                            string += employeeList.get(i).getId() + ",";
                        }
                    }
                    string += "''";
                    String hqlChannel = "Select URC.channels.id as id "
                            + " FROM UserRoleChannel as URC where deletedUser = 0"
                            + " AND users.id IN (" + string + ")";
                    List<HashMap> listChannelId = dataService.executeSelectHQL(HashMap.class, hqlChannel, true, 0, 0);
                    if (!listChannelId.isEmpty()) {
                        String string2 = "";
                        for (HashMap hm : listChannelId) {
                            string2 += hm.get("id") + ",";
                        }
                        string2 += "''";
                        if (_and) {
                            hql += " AND ";
                        }
                        hql += " POS.channels.id IN (" + string2 + ")";
                        _and = true;
                    } else {
                        if (_and) {
                            hql += " AND ";
                        }
                        hql += " POS.channels.id IN (0)";
                        _and = true;
                    }
                } */

                if (params.get("beginAt") != null) {
                    if (_and) {
                        hql += " AND ";
                    }
                    hql += " POS.createdAt >= '" + params.get("beginAt") + "'";
                    _and = true;
                }

                if (params.get("endAt") != null) {
                    SimpleDateFormat sdfHQL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(ValidatorUtil.parseParamaterDate(params.get("endAt")));
                    calendar.add(Calendar.DAY_OF_MONTH, 1);

                    if (_and) {
                        hql += " AND ";
                    }
                    hql += " POS.createdAt <= '" + sdfHQL.format(calendar.getTime()) + "'";
                    _and = true;
                }

                if (params.get("searchText") != null) {
                    if (_and) {
                        hql += " AND ";
                    }
                    String searchText = params.get("searchText");
                    hql += " (POS.posCode LIKE '%" + searchText + "%'";
                    hql += " OR POS.name LIKE '%" + searchText + "%')";
                    //hql += " OR POS.address LIKE '%" + searchText + "%'";
                    //hql += " OR POS.tel LIKE '%" + searchText + "%'";
                    //hql += " OR POS.mobile LIKE '%" + searchText + "%')";
                }
            }
            hql += " ORDER BY POS.createdAt DESC";
            List<POS> list = new ArrayList<POS>();
            List<HashMap> count = new ArrayList<HashMap>();
            try {
                list = (ArrayList<POS>) dataService.executeSelectHQL(POS.class, hqlList + hql, false, page, size);
                count = dataService.executeSelectHQL(HashMap.class, hqlCount + hql, true, 0, 0);
            } catch (Exception ex) {

            }
            results.setContentList(list);
            results.setCount((Long) count.get(0).get("VAL"));
        } catch (Exception e) {
            Logger.getLogger(POSServiceImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return results;
    }

    @Override
    public POS getPOSById(int id) {
        POS pos = dataService.getRowById(id, POS.class);
        return pos;
    }

    @Override
    public List<OptionItem> getChannelById(int id) {
        List<OptionItem> list = new ArrayList();
        List<Channel> channelList = dataService.getListOption(Channel.class, new ParameterList("parents.id", id));
        if (channelList != null) {
            for (Channel channel : channelList) {
                int _id = (int) channel.getId();
                String name = channel.getName();
                OptionItem optionItem = new OptionItem(_id, name);
                if (channel.getCode() != null) {
                    String code = channel.getCode();
                    optionItem.setCode(code);
                }
                list.add(optionItem);
            }
        }

        return list;
    }

    @Override
    public MsalesResults<CustomerCareInformation> searchCustomerCareByParams(LinkedHashMap<String, String> params, int page, int size) {
        MsalesResults<CustomerCareInformation> listcustomerCare = new MsalesResults<CustomerCareInformation>();
        String hqlList = "SELECT C ";
        String hqlCount = "SELECT COUNT(POS) AS VAL";
        String hql = " FROM CustomerCareInformation as C "
                + " JOIN C.mcpDetailss as MCPDetails "
                + " JOIN C.implementEmployees as User "
                + " JOIN C.poss as POS "
                + " JOIN POS.channels as Channel "
                + " JOIN POS.locations as Location ";
        if (params.size() > 0) {
            boolean _and = false;
            hql += " WHERE POS.deletedUser = 0 ";

            if (params.get("channels.companys.id") != null) {
                hql += " AND Channel.companys.id = " + params.get("channels.companys.id");
                _and = true;
            }

            if (params.get("channelId") != null) {
                Channel channel = dataService.getRowById(Integer.parseInt(params.get("channelId")), Channel.class);
                if (channel != null) {
                    if (_and) {
                        hql += " AND ";
                    }
                    String channelCode = channel.getFullCode();
                    hql += " Channel.fullCode LIKE '" + channelCode + "%'";
                    _and = true;
                }
            }

            if (params.get("fromDate") != null) {
                if (_and) {
                    hql += " AND ";
                }
                hql += " C.startCustomerCareAt >= '" + params.get("fromDate") + "'";
                _and = true;
            }

            if (params.get("toDate") != null) {
                if (_and) {
                    hql += " AND ";
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                try {
                    date = simpleDateFormat.parse(params.get("toDate"));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                hql += " C.startCustomerCareAt <= '" + simpleDateFormat.format(date) + " 23:59:59'";
                _and = true;
            }

            if (params.get("searchText") != null) {
                if (_and) {
                    hql += " AND ";
                }
                String searchText = params.get("searchText");
                hql += " (POS.posCode LIKE '%" + searchText + "%'";
                hql += " OR POS.name LIKE '%" + searchText + "%')";
            }

            if (params.get("implementEmployeeId") != null) {
                if (_and) {
                    hql += " AND ";
                }
                hql += " User.id=" + Integer.parseInt(params.get("implementEmployeeId"));
                _and = true;
            }
            hql += " and C.deletedUser = 0";

            hqlList = hqlList + hql + " order by C.createdAt desc";

        }
        try {
            List<CustomerCareInformation> list = (ArrayList<CustomerCareInformation>) dataService.executeSelectHQL(CustomerCareInformation.class, hqlList, false, page, size);
            for (CustomerCareInformation careInformation : list) {
                careInformation.getImplementEmployees().setName(careInformation.getImplementEmployees().getLastName() + " " + careInformation.getImplementEmployees().getFirstName());
                if (careInformation.getImplementEmployees().getLocations().getLocationType() == 1) {
                    careInformation.getImplementEmployees().setLocation(careInformation.getImplementEmployees().getLocations().getName());
                } else if (careInformation.getImplementEmployees().getLocations().getParents().getLocationType() == 1) {
                    careInformation.getImplementEmployees().setLocation(careInformation.getImplementEmployees().getLocations().getParents().getName());
                } else {
                    careInformation.getImplementEmployees().setLocation(careInformation.getImplementEmployees().getLocations().getParents().getParents().getName());
                }
                careInformation.setChannels(careInformation.getPoss().getChannels());
                careInformation.setMcps(careInformation.getMcpDetailss().getMcps());
                //Check xem bán hàng hay chưa.
                //Xử lí ngày:
                ParameterList parameterList2 = new ParameterList();
                //Xử lí kiểu bán hàng
                parameterList2.add("transType", 2);
                //Xử lí cho điểm bán hàng.
                List<SalesStock> salesStocks = dataService.getListOption(SalesStock.class, new ParameterList("poss.id", careInformation.getPoss().getId()));

                if (salesStocks != null && !salesStocks.isEmpty()) {
                    int stockOfPosId = salesStocks.get(0).getId();
                    parameterList2.add("toStocks.id", stockOfPosId);
                    //Xử lí nhân viên chăm sóc.
                    parameterList2.add("createdUser", careInformation.getImplementEmployees().getId());
                    //xử lí ngày giờ chăm sóc.
                    Date date = careInformation.getStartCustomerCareAt();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String fDate = simpleDateFormat.format(date);
                    Date fromDate = new Date();
                    try {
                        fromDate = simpleDateFormat.parse(fDate);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(fromDate);
                    calendar.add(calendar.DAY_OF_MONTH, 1);
                    Date toDate = calendar.getTime();
                    parameterList2.add("salesTransDate", fromDate, ">=");
                    parameterList2.add("salesTransDate", toDate, "<");
                    List<SalesTrans> salesTrans = dataService.getListOption(SalesTrans.class, parameterList2);
                    if (salesTrans != null && salesTrans.isEmpty()) {
                        careInformation.setIsSold(0);
                    } else {
                        careInformation.setIsSold(1);
                    }
                }
            }

            List<HashMap> count = dataService.executeSelectHQL(HashMap.class, hqlCount + hql, true, 0, 0);
            listcustomerCare.setContentList(list);
            listcustomerCare.setCount((Long) count.get(0).get("VAL"));
        } catch (Exception e) {
            listcustomerCare.setContentList(new ArrayList<CustomerCareInformation>());
            listcustomerCare.setCount((long) 0);
        }
        return listcustomerCare;
    }

    @Override
    public int insertRow(POS pos,int companyId,DataService dataService) {
        Location location = dataService.getRowById(pos.getLocations().getId(), Location.class);
        if(location == null || location.getLocationType()!=3){
            return 0;
        }
        //set posCode
        pos.setPosCode(appService.createPOSCode(companyId, location, dataService));
        int rs = dataService.insertRow(pos);
        if (rs > 0) {
            SalesStock salesStock = new SalesStock();
            salesStock.setPosId(rs);
            salesStock.setCreatedUser(pos.getCreatedUser());
            salesStock.setStatusId(1);//1 hoat dong
            try {
                dataService.insertRow(salesStock);
            } catch (Exception e) {
                return 0;
            }
            return rs;
        } else {
            return 0;
        }
    }

    @Override
    public Boolean updateRow(POS pos) {
        int rs = dataService.updateRow(pos);
        if (rs > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<POS> getListPOSIn(List<String> ids) {
        String hql = "SELECT POS FROM POS AS POS WHERE ID IN (";
        int i = 0;
        for (String s : ids) {
            hql += Integer.parseInt(s);
            if (i < (ids.size() - 1)) {
                hql += ",";
            }
            i++;
        }
        hql += ")";

        List<POS> list = dataService.executeSelectHQL(POS.class, hql, false, 0, 0);
        return list;
    }

    @Override
    public POS getPOSByCode(String code, int companyId, DataService dataService) {
        String hql = "FROM POS"
                + " WHERE deletedUser = 0"
                + " AND channels.companys.id = :companyId"
                + " AND posCode = :code";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("code", code));
        List<POS> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Status> getPOSStatus(DataService dataService) {
        String hql = "FROM Status"
                + " WHERE deletedUser = 0"
                + " AND statusTypes.id = 2";//trang thai POS
        List<MsalesParameter> parameters = new ArrayList<>();
        List<Status> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        return list;
    }
}
