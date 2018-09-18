package vn.itt.msales.config.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.entity.Version;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;

@Controller
public class WebMsalesVersionController {

    @Autowired
    ServletContext context;

    @Autowired
    private DataService dataService;

    @RequestMapping(value = "/version/detail")
    public String detail(MsalesLoginUserInf loginUser, HttpServletRequest request, Model uiModel, Version version) throws JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);

        List<Version> versions = dataService.getListOption(Version.class, new ParameterList("companys.id", loginUserInf.getCompanyId()));
        if (versions.size() != 0) {
            for (Version version1 : versions) {
                version.setVersion(version1.getVersion());
            }
        } else {
            version = new Version();
        }
        return version(request, uiModel, version);
        /*
         LinkedHashMap contents = new LinkedHashMap();
         contents.put("companyId",1);
         String json1=MsalesJsonUtils.getJSONFromOject(contents);
       
         request.setAttribute(MsalesConstants.CONTENTS, json1);
         String json = versionController.getVersion(request);
         LinkedHashMap<String, Object> category = MsalesJsonUtils.getObjectFromJSON(json, LinkedHashMap.class);
         LinkedHashMap<String, Object> content1 = (LinkedHashMap<String, Object>) category.get("contents");
         Version version = new Version();
         version.setId(Integer.parseInt(content1.get("id").toString()));
         version.setIsActive(Integer.parseInt(content1.get("isActive").toString()));
         version.setVersion(content1.get("version").toString());
         return version(request, uiModel, version);
         */
    }

    private String version(HttpServletRequest request, Model uiModel, Version version) {

        uiModel.addAttribute("version", version);
        return "versionSendSms";
    }

    @RequestMapping(value = "/version/detail", params = {"update"})
    public String updateVesion(MsalesLoginUserInf loginUser, @ModelAttribute("version") Version version,
            BindingResult bindingResult, RedirectAttributes redirectAttributes,
            HttpServletRequest request, Model uiModel) throws JsonMappingException, IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        
        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        List<Version> version1 = dataService.getListOption(Version.class, new ParameterList("companys.id", loginUserInf.getCompanyId()));
        if (version1.size() != 0) {
            for (Version version2 : version1) {
                validateInput(version, bindingResult, true);
                if (!bindingResult.hasErrors()) {
                    version.setId(version2.getId());
                    version.setCompanyId(loginUserInf.getCompanyId());
                    version.setUpdatedUser(loginUserInf.getId());
                    version.setIsActive(1);
                    int ret = dataService.updateSync(version);
                    if (ret > 0) {
                        redirectAttributes.addFlashAttribute("success", true);
                        return "redirect:/version/detail";
                    } else {
                        redirectAttributes.addFlashAttribute("success", false);
                    }
                }
            }
        } else {
            validateInput(version, bindingResult, true);
            if (!bindingResult.hasErrors()) {
                version.setCompanyId(loginUserInf.getCompanyId());
                version.setIsActive(1);
                version.setCreatedAt(new Date());
                version.setCreatedUser(loginUserInf.getId());

                int ret1 = dataService.insertRow(version);

                if (ret1 > 0) {
                    redirectAttributes.addFlashAttribute("success", true);
                    return "redirect:/version/detail";
                } else {
                    redirectAttributes.addFlashAttribute("success", false);
                }
            }
        }
        return version(request, uiModel, version);
        /*
         validateInputData(version, bindingResult, request);
         if (version.getId() != null) {
         LinkedHashMap contents = new LinkedHashMap();
         contents.put("id", version.getId());
         contents.put("companyId", loginUser.getCompanyId());
         contents.put("isActive", version.getIsActive());
         contents.put("version", version.getVersion());
           
         contents.put("updatedUser", 1);
         String json = MsalesJsonUtils.getJSONFromOject(contents);

         request.setAttribute(MsalesConstants.CONTENTS, json);

         String jsonOut = versionController.updateVersion(request);
         MsalesResponse response = MsalesJsonUtils.getObjectFromJSON(jsonOut, MsalesResponse.class);
         if (response.getStatus().getCode() == 200) {

         uiModel.addAttribute("success", true);
         } else {
         uiModel.addAttribute("success", false);
         }
         }

         return version(request, uiModel, version);
         */
    }

    private void validateInput(Version version, BindingResult bindingResult, boolean issAdd) {

        if (version.getVersion() == null || version.getVersion().trim().isEmpty()) {
            bindingResult.rejectValue("version", "version_error", "empty_error_code");
        }
    }

    @RequestMapping(value = "/version/sendsms")
    public String sendsms(HttpServletRequest request, Model uiModel) {
        /*
         try{
			
         Version new_version = versionService.find(1);
         if(new_version!=null){
         versionService.sendSMS(new_version.getTen());
         }
         return "redirect:/version/detail?sendSuccess=t";
         }catch(Exception e){
         e.printStackTrace();
         return "redirect:/version/detail";
         }
         */
        return "redirect:/version/detail";
    }

    @RequestMapping(value = "/guide")
    public void guide(
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
         Version new_version = versionService.find(1);
         String version_name = "";
         if(new_version!=null){
         version_name = new_version.getTen();
         }
         String appPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "";
         String sHTML = "";
         sHTML += "<html>";
         sHTML += "	<head>";
         sHTML += "		<title>";
         sHTML += "		Huong dan cai dat chuong trinh CSDBH";
         sHTML += "		</title>";
         sHTML += "	</head>";
         sHTML += "	<body>";
         sHTML += "		<div styel='font-size:18x'>";
         sHTML += "			<b>HƯỚNG DẪN NÂNG CẤP CHƯƠNG TRÌNH BÁN  TRÊN THIẾT BỊ DI ĐỘNG.</b>";
         sHTML += "		</div><br/>";
         //		sHTML += "		<div styel='font-size:15x'>";
         //		sHTML += "			<b>Bước 1: Gỡ bỏ chương trình BHKPP cũ.</b>";
         //		sHTML += "		</div>";
         //		sHTML += "		<div style=‘padding-left:10px’>";
         //		sHTML += "			<p>";
         //		sHTML += "			- Nhấn vào menu cài đặt <img src=\""+appPath+"/salesmgt/images/menu.jpeg\"/>,  chọn \"Quản lý ứng dụng\"";
         //		sHTML += "			</p>";
         //		sHTML += "			<p>";
         //		sHTML += "			<img src=\""+appPath+"/salesmgt/images/setting_app.png\"/> ";
         //		sHTML += "			</p>";
         //		sHTML += "			<p>";
         //		sHTML += "			- Chọn tab \"Cài đặt ứng dụng\" ";
         //		sHTML += "			</p>";
         //		sHTML += "			<p>";
         //		sHTML += "			- Tìm chương trình BHKPP và nhấn vào nút “Gỡ cài đặt” ";
         //		sHTML += "			</p>";
         //		sHTML += "			<p>";
         //		sHTML += "			<img src=\""+appPath+"/salesmgt/images/setting_remove.png\"/> ";
         //		sHTML += "			</p>";
         //		sHTML += "		</div><br/>";
         sHTML += "		<div styel='font-size:15x'>";
         sHTML += "			<b>Bước 1: Tải chương trình BHKPP phiên bản mới về.</b>";
         sHTML += "		</div>";
         sHTML += "		<div  styel='font-size:15x'>";
         sHTML += "			<p>";
         sHTML += "			- Bạn click <a href=\""+appPath+"/download/salestool_v"+version_name+".apk\">vào đây</a> để tải chương trình BHKPP về";
         sHTML += "			</p>";
         sHTML += "		</div><br/>";
         sHTML += "		<div styel='font-size:15x'>";
         sHTML += "			<b>Bước 2: Cài đặt chương trình BHKPP.</b>";
         sHTML += "		</div>";
         sHTML += "		<div style=‘padding-left:10px’>";
         sHTML += "			<p>";
         sHTML += "			- Nhấn vào menu, chọn \"Tải xuống\" ";
         sHTML += "			</p>";
         sHTML += "			<p>";
         sHTML += "			<img src=\""+appPath+"/salesmgt/images/setting_download.png\"/> ";
         sHTML += "			</p>";
         sHTML += "			<p>";
         sHTML += "			- Click  tập tin <b>salestool_v"+version_name+".apk</b> để thực hiện cài đặt ";
         sHTML += "			</p>";
         sHTML += "			<p>";
         sHTML += "			<img src=\""+appPath+"/salesmgt/images/setting_down_detail.png\"/> ";
         sHTML += "			</p>";
         //		sHTML += "			Bạn nhấn vào menu chọn \"Tải xuống\"  bạn tìm tập tin <b>Tracking.apk</b> và nhấn vào đó để thực hiện cài đặt";
         sHTML += "		</div><br/>";
         sHTML += "	</body>";
         sHTML += "</html>";
         response.setContentType("text/html;charset=UTF-8");
         response.getWriter().println(sHTML);
         */

    }

    private void validateInputData(Version version, BindingResult bindingResult, HttpServletRequest request) {
        // Fullname

        String name = version.getVersion();
        if (name == null || name.trim().isEmpty()) {
            bindingResult.rejectValue("version", "version_error", "empty_error_code");
        }

    }

}
