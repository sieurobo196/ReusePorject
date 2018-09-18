/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.user.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Khai
 */
public class PackageManager {

    private int type;
    private String patern;
    private String except;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPatern() {
        return patern;
    }

    public void setPatern(String patern) {
        this.patern = patern;
    }

    public PackageManager() {
    }

    public PackageManager(int type, String patern,String except) {
        this.type = type;
        this.patern = patern;
        this.except = except;
    }

    public String getExcept() {
        return except;
    }

    public void setExcept(String except) {
        this.except = except;
    }
    
    

    public static List<PackageManager> loadAll(HttpServletRequest request) {
        try {
            String url = request.getSession().getServletContext().getRealPath("/") + "/WEB-INF/url-manager.xml";
            List<PackageManager> list = new ArrayList<>();
            File file = new File(url);

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            NodeList nodeList = doc.getElementsByTagName("package");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                PackageManager packageManager = new PackageManager(Integer.parseInt(node.getAttributes().getNamedItem("type").getNodeValue()),
                        node.getAttributes().getNamedItem("patern").getNodeValue(),node.getAttributes().getNamedItem("except").getNodeValue());

                list.add(packageManager);
            }

            return list;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public static boolean check(List<PackageManager> list, String url, Integer packageService) {
        boolean flag = false;
        if (packageService == null) {
            return true;
        }
        
        if(list.isEmpty())
        {
            return true;
        }
        
        for (PackageManager packageManager : list) {            
            if (packageManager.getType() == packageService) {
                for(String s: packageManager.getExcept().split(",")){
                    if(s.isEmpty()){
                        break;
                    }
                    else if(url.toUpperCase().trim().startsWith(s.toUpperCase().trim())){
                        return false;
                    }
                }
                for (String s : packageManager.getPatern().split(",")) {
                    if(s.equals("*") || url.equals("/")){
                        //accept all
                        return true;
                    }
                    else if (url.toUpperCase().trim().startsWith(s.toUpperCase().trim())) {
                        flag = true;
                        break;
                    }
                }
            }
        }

        return flag;
    }
}
