/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.services;

import java.util.List;
import vn.itt.msales.entity.User;

/**
 *
 * @author cshiflett
 */
public interface StaffService {
    public List<User> getListUser(int companyId);
}
