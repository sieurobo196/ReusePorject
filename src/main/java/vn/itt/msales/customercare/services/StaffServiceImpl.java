/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import vn.itt.msales.entity.User;
import vn.itt.msales.services.DataService;

/**
 *
 * @author cshiflett
 */
public class StaffServiceImpl implements StaffService {
    
    @Autowired
    private DataService dataService;

    @Override
    public List<User> getListUser(int companyId) {
        
        String hql = "Select User"
                + " FROM UserRoleChannel AS UserRoleChannel"
                + " JOIN UserRoleChannel.users AS User"
                + " WHERE  UserRoleChannel.deletedUser = 0"
                + " AND User.companys.id = " + companyId
                + " AND  UserRoleChannel.userRoles.id > 4";
        
        List<User> list =  dataService.executeSelectHQL(User.class, hql, false, 0, 0);
        return list;
    }
}
