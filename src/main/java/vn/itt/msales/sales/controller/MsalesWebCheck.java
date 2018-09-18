/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.sales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import vn.itt.msales.user.controller.MsalesUserController;

/**
 *
 * @author vtm
 */
@Controller
public class MsalesWebCheck {
    @Autowired
    private MsalesUserController userController;

   
}
