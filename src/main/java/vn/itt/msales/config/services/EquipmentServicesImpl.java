/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.config.services;

import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author vtm_2
 */
@Service
public class EquipmentServicesImpl implements EquipmentServices {

    @Autowired
    private DataService dataService;

    public DataService getDataService() {
        return dataService;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public MsalesResults<HashMap> searchEquipment(Filter filter, int companyId, MsalesPageRequest pageRequest) {
        String hql = "SELECT DISTINCT(E.id) as id, E.name as name, E.telNo as telNo,E.imei as imei,E.subscriberId as subscriberId ,"
                + "E.isActive as isActive ,E.activeDate as activeDate ,E.version as version ,E.users as users,URC.userRoles as userRoles"
                + " FROM Equipment as E,User as U,UserRoleChannel as URC, UserRole as UR"
                + " WHERE E.users.id=U.id AND URC.users.id=U.id AND URC.userRoles.id=UR.id AND E.users.id=URC.users.id"
                + " AND E.deletedUser=0 AND UR.deletedUser=0"
                + " AND E.companys.id =" + companyId;

        if (filter.getSearchText() != null && ! filter.getSearchText().isEmpty()) {
         
                String key = filter.getSearchText().toString();
                // parameterList.or("name", key, "like", "code", key, "like");
                hql += " AND ( E.imei LIKE '%" + key + "%'" + " or E.subscriberId LIKE '%" + key + "%' ) ";
           
        }
        if (filter.getRole() != null && filter.getRole() != 0) {
            int roleId = Integer.parseInt(filter.getRole().toString());
            try {
                //parameterList.add("parents.id", statusId);

                hql += "  AND URC.userRoles.id = " + roleId;

            } catch (Exception ex) {
                //error parse statusId
            }
        }
            List<OptionItem> userList =filter.getUserList();
            
        if (userList.size()!=0) {
            
            
            hql += " AND E.users.id IN (";
            for(OptionItem optionItem:userList){
                hql+=optionItem.getId() + ",";
            }
            hql+="'')";
            

        }

//                
        hql += " group by E.id " + " ORDER BY E.createdAt desc";
        List<HashMap> lists = dataService.executeSelectHQL(HashMap.class, hql, true, pageRequest.getPageNo(), pageRequest.getRecordsInPage());
        List<HashMap> count = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);

        MsalesResults<HashMap> listChannel = new MsalesResults<HashMap>();
        listChannel.setContentList(lists);
        listChannel.setCount(Long.parseLong(count.size() + ""));

        return listChannel;
    }

    @Override
    public List<User> listCbEquipmentUser(int comapnyId) {
        String hql = "SELECT Equipment.users.id"
                + " FROM Equipment AS Equipment"
                + " WHERE Equipment.deletedUser = 0"
                + " AND Equipment.companys.id= :companyId"
                + " AND Equipment.isActive = 1";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", comapnyId);
        List<Integer> userIdList = dataService.executeSelectHQL(hql, parameters, false, 0, 0);

        String hqlUser = "SELECT User"
                + " FROM User AS User"
                + " WHERE User.deletedUser = 0"
                + " AND User.companys.id= :companyId"
                + " AND User.id NOT IN (:userIdList)";
        if (userIdList == null || userIdList.isEmpty()) {
            parameters.add(MsalesParameter.createBadInteger("userIdList"));
        } else {
            parameters.add(MsalesParameter.create("userIdList", userIdList, 1));
        }
        List<User> userList = dataService.executeSelectHQL(hqlUser, parameters, false, 0, 0);
        return userList;
    }

    @Override
    public boolean checkEquipmentMax(int companyId,Integer equipmentId,Integer equipmentMax, DataService dataService) {
        if(equipmentMax==null){
            return false;
        }
        
        String hql = "SELECT DISTINCT id"
                + " FROM Equipment"
                + " WHERE deletedUser = 0"
                + " AND companys.id = :companyId"
                + " AND users.isActive = 1"//dang mo khoa'
                + " AND users.statuss.id = 6";//dang lam viec
        
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId",companyId);
        List<Integer> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        
        if(equipmentId!=null){
            for(int id : list){
                if(equipmentId==id){
                    return false;
                }
            }
        }
        
        return list.size() >= equipmentMax;
    }

}
