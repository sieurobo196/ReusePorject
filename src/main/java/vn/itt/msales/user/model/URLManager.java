/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.user.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author vtm
 */
public class URLManager {

    private String patern;
    private String role;
    private String method;
    
    private Integer type;

    public URLManager() {
    }

    public URLManager(String patern, String role) {
        this.patern = patern;
        this.role = role;
    }

    public URLManager(String patern, String role, String method) {
        this.patern = patern;
        this.role = role;
        this.method = method;
    }

    public String getPatern() {
        return patern;
    }

    public void setPatern(String patern) {
        this.patern = patern;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    
    

    private static List<URLManager> loadAll(HttpServletRequest request) {
        try {
            String url = request.getSession().getServletContext().getRealPath("/") + "/WEB-INF/url-manager.xml";
            List<URLManager> list = new ArrayList<>();
            File file = new File(url);

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            NodeList nodeList = doc.getElementsByTagName("url");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                //System.out.println("patern: " + node.getAttributes().getNamedItem("patern").getNodeValue());
                //System.out.println("role: " + node.getAttributes().getNamedItem("role").getNodeValue());
                URLManager urlManager = new URLManager(node.getAttributes().getNamedItem("patern").getNodeValue(), node.getAttributes().getNamedItem("role").getNodeValue());
                if (node.getAttributes().getNamedItem("method") != null) {
                    urlManager.setMethod(node.getAttributes().getNamedItem("method").getNodeValue());
                }
                
                if (node.getAttributes().getNamedItem("type") != null) {
                    urlManager.setType(Integer.parseInt(node.getAttributes().getNamedItem("type").getNodeValue()));
                }
                
                list.add(urlManager);
            }
            return list;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            return new ArrayList<>();
        }
    }
    
    

    public static boolean checkRole(String role,Integer packageService, HttpServletRequest request) {
        String requestURL = request.getRequestURI();
        requestURL = requestURL.replaceFirst(request.getContextPath(), "");
        requestURL = fixURL(requestURL);

        List<URLManager> list = URLManager.loadAll(request);
        List<PackageManager> packageManagerList = PackageManager.loadAll(request);
        
        for (URLManager urlManager : list) {
            String method = "";
            if (urlManager.getMethod() != null && !urlManager.getMethod().trim().isEmpty()) {
                method = urlManager.getMethod().toUpperCase();
            }

            boolean check = true;
            for (String url : urlManager.getPatern().split(",")) {
                url = fixURL(url);
                if (requestURL.startsWith(url)) {                    
                    if (method.isEmpty()) {
                        check = check(role, urlManager.getRole());
                    } else {
                        if (request.getMethod().toUpperCase().equals(method)) {
                            check = check(role, urlManager.getRole());
                        }
                    }
                    break;
                }
//                if (requestURL.charAt(requestURL.length() - 1) != '*') {
//                    if (requestURL.equals(url)) {
//                        return check(role, urlManager.getRole());
//                    }
//                } else {
//                    if (requestURL.startsWith(url)) {
//                        return check(role, urlManager.getRole());
//                    }
//                }
            }
            
            if(check){
                //check package
                check = PackageManager.check(packageManagerList, fixURL(requestURL), packageService);                      
            }
            return check;
        }
        return true;
    }

    private static String fixURL(String url) {
        String ret = "/" + url.trim();
//        if (ret.charAt(ret.length() - 1) != '*') {
//            ret += "/";
//        }
        ret = ret.replaceAll("//", "/");
        return ret.trim();
    }

    private static boolean check(String role, String roleType) {
        if (roleType.startsWith("any")) {
            return any(role, roleType.replaceFirst("any\\(", "").replaceFirst("\\)", ""));
        } else if (roleType.startsWith("except")) {
            return except(role, roleType.replaceFirst("except\\(", "").replaceFirst("\\)", ""));
        } else {
            return false;
        }
    }

    private static boolean any(String role, String roleList) {
        for (String s : roleList.split(",")) {
            if (s.trim().equals(role)) {
                return true;
            }
        }
        return false;
    }

    private static boolean except(String role, String roleList) {
        for (String s : roleList.split(",")) {
            if (s.trim().equals(role)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkIn(String roleCode, String[] roleCodeList) {
        for (String role : roleCodeList) {
            if (roleCode.toUpperCase().trim().equals(role.toUpperCase().trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkOut(String roleCode, String[] roleCodeList) {
        for (String role : roleCodeList) {
            if (roleCode.toUpperCase().trim().equals(role.toUpperCase().trim())) {
                return false;
            }
        }
        return true;
    }
}
