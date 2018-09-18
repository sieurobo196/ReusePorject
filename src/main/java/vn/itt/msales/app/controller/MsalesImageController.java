/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.app.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Properties;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author vtm
 */
@Controller

public class MsalesImageController {

    @Resource(name = "systemPros")
    private Properties systemProperties;

    @RequestMapping(value = "/imagesSource/**")
    public void image(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        String filename = URLDecoder.decode(request.getServletPath(), "UTF-8");
        filename = filename.replaceFirst("/imagesSource", systemProperties.getProperty("system.imagesRoot"));
        //String folderPath = systemProperties.getProperty("system.imagesRoot") + "/";
        File file = new File(filename);
        if (!file.exists() || file.isDirectory()) {
            file = new File(systemProperties.getProperty("system.imagesRoot") + "/404.png");
        }

        response.setHeader("Content-Length", "image/png");
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        Files.copy(file.toPath(), response.getOutputStream());
    }
    
    @RequestMapping(value = "/imagesSource/")
    public void imageNotFound(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {        
        //String folderPath = systemProperties.getProperty("system.imagesRoot") + "/";
        File file = new File(systemProperties.getProperty("system.imagesRoot") + "/404.png");      

        response.setHeader("Content-Length", "image/png");
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        Files.copy(file.toPath(), response.getOutputStream());
    }
    
}
