/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import vn.itt.msales.channel.model.MsalesPOSImport;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.json.validator.MsalesValidator;
import vn.itt.msales.common.xls.MsalesExcelConfig;
import vn.itt.msales.common.xls.MsalesReadExcel;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.MsalesParameter;

/**
 *
 * @author Khai
 */
public class ExcelServiceImpl implements ExcelService {

    @Override
    public boolean checkEmptyRow(List<Object> list) {
        if (list == null) {
            return true;
        }
        int count = 0;
        for (Object object : list) {
            if (object == null || object.toString().isEmpty()) {
                count++;
            }
        }
        return list.size() == count;
    }

    @Override
    public List<List<Object>> getListDataFromExel(MultipartFile multipartFile, int maxCol, int beginRow) {
        MsalesExcelConfig excelConfig = new MsalesExcelConfig(maxCol, beginRow, multipartFile);
        MsalesReadExcel msleExcel = new MsalesReadExcel(excelConfig);
        List<List<Object>> posList = msleExcel.readExcel();
        return posList;
    }

    @Override
    public String getNumberString(Object object) {
        if (object == null) {
            return null;
        } else {
            if (object instanceof Double) {
                Integer ret = getInteger(object.toString());
                if (ret != null) {
                    return ret.toString();
                } else {
                    return null;
                }
            } else if (object instanceof Integer) {
                return object.toString();
            } else {
                return object.toString();
            }
        }
    }

    @Override
    public Integer getInteger(String doubleString) {
        try {
            Double temp = Double.valueOf(doubleString);
            return temp.intValue();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Date getDate(String dateString) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MsalesValidator.DATE_FORMAT_SHORT);
            return simpleDateFormat.parse(dateString);
        } catch (ParseException ex) {
            return null;
        }
    }

    @Override
    public void setLatLngPOS(MsalesPOSImport pos) {
        if (pos.getPosAddress() != null && !pos.getPosAddress().isEmpty()) {
            try {
                URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(pos.getPosAddress(), "UTF-8"));

                URLConnection connection = url.openConnection();
                InputStream input = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                String line;
                String respond = "";
                while ((line = bufferedReader.readLine()) != null) {
                    respond += line;
                }
                LinkedHashMap object = MsalesJsonUtils.getObjectFromJSON(respond, LinkedHashMap.class);
                if (object.get("status").toString().equalsIgnoreCase("OK")) {
                    List<LinkedHashMap> results = (List) object.get("results");
                    LinkedHashMap location = (LinkedHashMap) ((LinkedHashMap) results.get(0).get("geometry")).get("location");
                    pos.setPosLAT(new BigDecimal((Double) location.get("lat")).setScale(15, RoundingMode.HALF_UP));
                    pos.setPosLNG(new BigDecimal((Double) location.get("lng")).setScale(15, RoundingMode.HALF_UP));
                }
            } catch (Exception ex) {
                pos.setPosLAT(BigDecimal.ZERO);
                pos.setPosLNG(BigDecimal.ZERO);
            }
        }
    }

    @Override
    public User getUserByUserName(String userName,String domain, int companyId, DataService dataService) {
        String hql = "FROM User"
                + " WHERE deletedUser = 0"
                + " AND (userName = :userName OR userName = :userNameFull)"
                + " AND companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("userName", userName));
        parameters.add(MsalesParameter.create("userNameFull", userName + "@" + domain));
        List<User> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        return list.isEmpty() ? null : list.get(0);
    }
    
    @Override
    public POS getPOSByPosCode(String posCode,int companyId,DataService dataService){
        String hql = "FROM POS"
                + " WHERE deletedUser = 0"
                + " AND posCode = :posCode"
                + " AND channels.companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        parameters.add(MsalesParameter.create("posCode", posCode));
        List<POS> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        return list.isEmpty() ? null : list.get(0);
    }
    
    @Override
    public boolean checkEmptyOrNull(Object value) {
        if (value != null) {
            return value.toString().trim().isEmpty();
        } else{
            return true;
        }
    }
}
