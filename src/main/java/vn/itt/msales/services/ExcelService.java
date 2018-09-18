/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.services;

import java.util.Date;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import vn.itt.msales.channel.model.MsalesPOSImport;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.User;

/**
 *
 * @author Khai
 */
public interface ExcelService {

    public boolean checkEmptyRow(List<Object> list);

    public List<List<Object>> getListDataFromExel(MultipartFile multipartFile, int maxCol, int beginRow);

    public String getNumberString(Object object);

    public Integer getInteger(String doubleString);

    public Date getDate(String dateString);

    public void setLatLngPOS(MsalesPOSImport pos);

    public User getUserByUserName(String userName,String domain, int companyId, DataService dataService);

    public POS getPOSByPosCode(String posCode, int companyId, DataService dataService);
    
    public boolean checkEmptyOrNull(Object value);
}
