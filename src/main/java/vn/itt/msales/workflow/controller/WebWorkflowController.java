package vn.itt.msales.workflow.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.common.xls.MsalesReadExcel;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.CompanyConfig;
import vn.itt.msales.entity.CompanyConfigDetails;
import vn.itt.msales.entity.Workflow;
import vn.itt.msales.entity.WorkflowDetails;
import vn.itt.msales.entity.WorkflowType;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.controller.MsalesUserController;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;
import vn.itt.msales.workflow.model.WebCompanyWorkflow;
import vn.itt.msales.workflow.model.WebWorkflow;
import vn.itt.msales.workflow.service.CompanyWorkflowService;

/**
 *
 * @author ChinhNQ
 */
@Controller
public class WebWorkflowController {

    @Autowired
    private MsalesUserController userController;
    @Autowired
    private DataService dataService;
    @Autowired
    private CompanyWorkflowService workflowService;

    @Resource(name = "systemPros")
    private Properties systemProperties;

    /**
     * Size of a byte buffer to read/write file
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Path of the file to be downloaded, relative to application's directory
     */
    String separator = File.separator;
    private String filePath = String.format("%sdownloads%sworkflow%stemplets%s", separator, separator, separator, separator);

    @RequestMapping(value = "/workflow/download")
    public void downloadTemplets(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }
        // get absolute path of the application
        ServletContext context = request.getSession().getServletContext();
        String fullPath = context.getRealPath("") + filePath;

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);

        if (userInf.getPackageService() == null || userInf.getPackageService() == 3) {
            fullPath += "mSales_Workflow_Premium.xlsx";
        } else if (userInf.getPackageService() == 1) {
            fullPath += "mSales_Workflow_Nornal.xlsx";
        } else if (userInf.getPackageService() == 2) {
            fullPath += "mSales_Workflow_Profressional.xlsx";
        } else {
            throw new IOException();
        }

        File downloadFile = new File(fullPath);
        OutputStream outStream;
        // get MIME type of the file
        try (FileInputStream inputStream = new FileInputStream(downloadFile)) {
            // get MIME type of the file
            String mimeType = context.getMimeType(fullPath);
            if (mimeType == null) {
                // set to binary type if MIME mapping not found
                mimeType = "application/octet-stream";
            }
            // set content attributes for the response
            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());
            // set headers for the response
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
            response.setHeader(headerKey, headerValue);
            // get output stream of the response
            outStream = response.getOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            // write bytes read from the input stream into the output stream
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }
        outStream.close();
    }

    @RequestMapping(value = "/workflow/current")
    public void downloadWorkflow(HttpServletRequest request, HttpServletResponse response) throws IOException, NoSuchRequestHandlingMethodException, ServletException {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            LoginContext.sendRedirectHome(request, response);
            return;
        }
        // get absolute path of the application
        ServletContext context = request.getSession().getServletContext();

        // construct the complete absolute path of the file
        String rootFolderCurrentWorkflow = systemProperties.getProperty("system.workflowRoot");
        String pathCurrentWorkflow = LoginContext.getLogin(request).getBranch() + "/" + LoginContext.getLogin(request).getCompanyId();
        String fullPath = rootFolderCurrentWorkflow + "/" + pathCurrentWorkflow + "/" + systemProperties.getProperty("system.WorkflowExcelXLSX");
        File downloadFileTest = new File(fullPath);
        if (!downloadFileTest.exists()) {
            fullPath = rootFolderCurrentWorkflow + "/" + pathCurrentWorkflow + "/" + systemProperties.getProperty("system.WorkflowExcelXLS");
        }

        File downloadFile = new File(fullPath);
        if (!downloadFile.exists()) {
            //throw new NoSuchRequestHandlingMethodException(request);\
            request.setAttribute("fileNotFound", true);
            request.getRequestDispatcher("/workflow").forward(request, response);
            return;
        }
        OutputStream outStream;
        // get MIME type of the file
        try (FileInputStream inputStream = new FileInputStream(downloadFile)) {
            // get MIME type of the file
            String mimeType = context.getMimeType(fullPath);
            if (mimeType == null) {
                // set to binary type if MIME mapping not found
                mimeType = "application/octet-stream";
            }
            // set content attributes for the response
            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());
            // set headers for the response
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
            response.setHeader(headerKey, headerValue);
            // get output stream of the response
            outStream = response.getOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            // write bytes read from the input stream into the output stream
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }
        outStream.close();
    }

    @RequestMapping(value = "/workflow")
    public ModelAndView workflow(HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }
        return new ModelAndView("workflow");
    }

    private List<Integer> saveWorkflow(List<Workflow> workFlowList, int workflowTypeId, int companyId, int userLoginId, Integer workflowDetailsId) {
        List<Integer> workflowIds = new ArrayList<>();
        if (workFlowList != null) {
            for (Workflow workflow : workFlowList) {
                //create
                workflow.setCreatedUser(userLoginId);
                workflow.setCompanyId(companyId);
                workflow.setWorkflowTypeId(workflowTypeId);
                workflow.setWorkflowDetailsId(workflowDetailsId);
                Workflow rootWorkflow = workflow;
                dataService.insertRow(rootWorkflow);

                workflowIds.add(rootWorkflow.getId());

                if (workflowService.deleteAllWorkflowDetails(rootWorkflow.getId(), userLoginId, dataService)) {
                    if (workflow.getOption_check() != null) {
                        for (WorkflowDetails workflowDetails : workflow.getOption_check()) {
                            workflowDetails.setCreatedUser(userLoginId);
                            workflowDetails.setActionType(3);//type =3 => option_check
                            workflowDetails.setWorkflowId(rootWorkflow.getId());
                            int detailsId = dataService.insertRow(workflowDetails);
                            if (workflowDetails.getDetails() != null) {
                                List<Integer> ids = saveWorkflow(workflowDetails.getDetails(), workflowTypeId, companyId, userLoginId, detailsId);
                                if (ids != null) {
                                    workflowIds.addAll(ids);
                                }
                            }
                        }
                    }
                    if (workflow.getOption_check_update() != null) {
                        for (WorkflowDetails workflowDetails : workflow.getOption_check_update()) {
                            workflowDetails.setCreatedUser(userLoginId);
                            workflowDetails.setActionType(2);//type =2 => option_check_update
                            workflowDetails.setWorkflowId(rootWorkflow.getId());
                            int detailsId = dataService.insertRow(workflowDetails);
                            if (workflowDetails.getDetails() != null) {
                                List<Integer> ids = saveWorkflow(workflowDetails.getDetails(), workflowTypeId, companyId, userLoginId, detailsId);
                                if (ids != null) {
                                    workflowIds.addAll(ids);
                                }
                            }
                        }
                    }
                    if (workflow.getOption_select() != null) {
                        for (WorkflowDetails workflowDetails : workflow.getOption_select()) {
                            workflowDetails.setCreatedUser(userLoginId);
                            workflowDetails.setActionType(1);//type =1 => option_select
                            workflowDetails.setWorkflowId(rootWorkflow.getId());
                            int detailsId = dataService.insertRow(workflowDetails);
                            if (workflowDetails.getDetails() != null) {
                                List<Integer> ids = saveWorkflow(workflowDetails.getDetails(), workflowTypeId, companyId, userLoginId, detailsId);
                                if (ids != null) {
                                    workflowIds.addAll(ids);
                                }
                            }
                        }
                    }
                } else {
                    //loi
                    return null;
                }
            }
        }
        return workflowIds;
    }

    //@RequestMapping(value = "/workflow/importFileCare", method = RequestMethod.POST)
    public ModelAndView workflowImportFileCare(HttpServletRequest request, RedirectAttributes redirectAttributes,
            @RequestParam("file_care") MultipartFile file) {

        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        int companyId = LoginContext.getLogin(request).getCompanyId();
        int userLoginId = LoginContext.getLogin(request).getId();

        redirectAttributes.addFlashAttribute("careSubmited", true);

        if (validateFileData(file, WebWorkflow[].class)) {
            WebWorkflow[] webWorkflowList = getData(file);
            if (webWorkflowList != null) {
                int order = 1;
                try {
                    List<Integer> workflowIds = new ArrayList();
                    for (WebWorkflow webWorkflow : webWorkflowList) {
                        String workFlowTypeCode = webWorkflow.getWorkFlowTypeCode();
                        //get workflowType by code
                        //if not  exist => create WorkFlowType = > update                        
                        WorkflowType rootWorkflowType = workflowService.getWorkflowTypeByCode(workFlowTypeCode, dataService);
                        if (rootWorkflowType != null) {
                            //exist workflow
                            //update name
                            rootWorkflowType.setName(webWorkflow.getWorkFlowTypeName());
                            rootWorkflowType.setOrder(order++);
                            dataService.updateRow(rootWorkflowType);
                        } else {
                            //create
                            WorkflowType workflowType = new WorkflowType();
                            workflowType.setCode(workFlowTypeCode);
                            workflowType.setName(webWorkflow.getWorkFlowTypeName());
                            workflowType.setType(1);//1 => cham soc
                            workflowType.setCreatedUser(userLoginId);
                            workflowType.setOrder(order++);
                            rootWorkflowType = workflowType;
                            dataService.insertRow(rootWorkflowType);
                        }

                        List<Workflow> workFlowList = webWorkflow.getWorkFlow();

                        List<Integer> ids = saveWorkflow(workFlowList, rootWorkflowType.getId(), companyId, userLoginId, null);
                        if (ids != null) {
                            workflowIds.addAll(ids);
                        }
                    }

                    if (!workflowIds.isEmpty()) {
                        //deleteAll workflow not in file
                        if (!workflowService.deleteAllWorkflowOther(companyId, workflowIds, 1, userLoginId, dataService)) {
                            redirectAttributes.addFlashAttribute("create", false);
                        }
                    }
                    //save db done
                    String rootFolder = systemProperties.getProperty("system.workflowRoot");
                    String rootPath = LoginContext.getLogin(request).getBranch() + "/" + LoginContext.getLogin(request).getCompanyId();
                    File path = new File(rootFolder + "/" + rootPath);
                    if (!path.exists() || !path.isDirectory()) {
                        path.mkdirs();
                    }
                    File workflowCareFile = new File(path.getPath() + "/" + systemProperties.getProperty("system.workflowCare"));
                    file.transferTo(workflowCareFile);
                    redirectAttributes.addFlashAttribute("create", true);
                } catch (Exception ex) {
                    redirectAttributes.addFlashAttribute("create", false);
                }
            } else {
                redirectAttributes.addFlashAttribute("create", false);
            }

        } else {
            redirectAttributes.addFlashAttribute("fileError", true);
        }
        return new ModelAndView("redirect:/workflow");
    }

    //@RequestMapping(value = "/workflow/importFileSales")
    public ModelAndView workflowImportSales(HttpServletRequest request, RedirectAttributes redirectAttributes,
            @RequestParam("file_sales") MultipartFile file) {

        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        int companyId = LoginContext.getLogin(request).getCompanyId();
        int userLoginId = LoginContext.getLogin(request).getId();

        redirectAttributes.addFlashAttribute("salesSubmited", true);

        if (validateFileData(file, WebWorkflow[].class)) {
            WebWorkflow[] webWorkflowList = getData(file);
            if (webWorkflowList != null) {
                int order = 1;
                try {
                    List<Integer> workflowIds = new ArrayList();
                    for (WebWorkflow webWorkflow : webWorkflowList) {
                        String workFlowTypeCode = webWorkflow.getWorkFlowTypeCode();
                        //get workflowType by code
                        //if not  exist => create WorkFlowType = > update
                        WorkflowType rootWorkflowType = workflowService.getWorkflowTypeByCode(workFlowTypeCode, dataService);
                        if (rootWorkflowType != null) {
                            //exist workflow
                            //update name
                            rootWorkflowType.setName(webWorkflow.getWorkFlowTypeName());
                            rootWorkflowType.setOrder(order++);
                            dataService.updateRow(rootWorkflowType);
                        } else {
                            //create
                            WorkflowType workflowType = new WorkflowType();
                            workflowType.setCode(workFlowTypeCode);
                            workflowType.setName(webWorkflow.getWorkFlowTypeName());
                            workflowType.setType(2);//1 => ban hang
                            workflowType.setCreatedUser(userLoginId);
                            workflowType.setOrder(order++);
                            rootWorkflowType = workflowType;
                            dataService.insertRow(rootWorkflowType);
                        }

                        List<Workflow> workFlowList = webWorkflow.getWorkFlow();
                        List<Integer> ids = saveWorkflow(workFlowList, rootWorkflowType.getId(), companyId, userLoginId, null);
                        if (ids != null) {
                            workflowIds.addAll(ids);
                        }
                    }

                    if (!workflowIds.isEmpty()) {
                        //deleteAll workflow not in file
                        if (!workflowService.deleteAllWorkflowOther(companyId, workflowIds, 2, userLoginId, dataService)) {
                            redirectAttributes.addFlashAttribute("update", false);
                        }
                    }

                    //save db done
                    String rootFolder = systemProperties.getProperty("system.workflowRoot");
                    String path = LoginContext.getLogin(request).getBranch() + "/" + LoginContext.getLogin(request).getCompanyId();
                    File filePath = new File(rootFolder + "/" + path);
                    if (!filePath.exists() || !filePath.isDirectory()) {
                        filePath.mkdirs();
                    }
                    File workflowCareFile = new File(filePath.getPath() + "/" + systemProperties.getProperty("system.workflowSale"));
                    file.transferTo(workflowCareFile);
                    redirectAttributes.addFlashAttribute("update", true);
                } catch (Exception ex) {
                    redirectAttributes.addFlashAttribute("update", false);
                }
            } else {
                redirectAttributes.addFlashAttribute("update", false);
            }

        } else {
            redirectAttributes.addFlashAttribute("fileError", true);
        }

        return new ModelAndView("redirect:/workflow");
    }

    private WebWorkflow[] getData(MultipartFile file) {
        WebWorkflow[] workflow = null;
        try {
            byte[] bytes = file.getBytes();
            workflow = MsalesJsonUtils.getObjectFromBytes(bytes, WebWorkflow[].class);
        } catch (Exception ex) {
            Logger.getLogger(WebWorkflowController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return workflow;
    }

    private WebCompanyWorkflow getJSON(MultipartFile file) {
        WebCompanyWorkflow companyWorkflow = null;
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                companyWorkflow = MsalesJsonUtils.getObjectFromBytes(bytes, WebCompanyWorkflow.class);
            } catch (IOException ex) {
                Logger.getLogger(WebWorkflowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return companyWorkflow;
    }

    private File saveToFile(byte[] bytes, String fileName) {
        BufferedOutputStream stream = null;
        File serverFile = null;
        try {
            // Creating the directory to store file
            String rootPath = System.getProperty("catalina.home");
            File dir = new File(rootPath + File.separator + "tmpFiles");
            if (!dir.exists()) {
                dir.mkdirs();
            }   // Create the file on server
            serverFile = new File(dir.getAbsolutePath() + File.separator + fileName);
            stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();
        } catch (Exception ex) {
            Logger.getLogger(WebWorkflowController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                Logger.getLogger(WebWorkflowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return serverFile;
    }

    private LinkedHashMap<CompanyConfig, List<CompanyConfigDetails>> getCompanyConfig(LinkedHashMap pages, MsalesLoginUserInf loginUserInf) {
        LinkedHashMap<CompanyConfig, List<CompanyConfigDetails>> gui = new LinkedHashMap();
        if (pages != null && !pages.isEmpty()) {
            Iterator<String> iter = pages.keySet().iterator();
            // init user role
            int userRoleId = -1;
            while (iter.hasNext()) {
                String key = iter.next();
                // User role of the salesman is 6
                switch (key) {
                    // User role of the salessup is 4
                    case "salesman":
                        userRoleId = 6;
                        break;
                    case "salessup":
                        userRoleId = 4;
                        break;
                }
                // get infor comnay config and company config details
                ArrayList<LinkedHashMap<String, Object>> salesManArray = (ArrayList) pages.get(key);
                /**
                 * get company config
                 */
                for (LinkedHashMap<String, Object> companyConfig : salesManArray) {
                    // get name for comany config "pageId" in joson = "name" in table
                    String pageId = companyConfig.get("pageId").toString();

                    // create CompanyConfig
                    CompanyConfig compaConfig = new CompanyConfig();
                    compaConfig.setCompanyId(loginUserInf.getCompanyId());
                    compaConfig.setUserRoleId(userRoleId);
                    compaConfig.setName(pageId);
                    // by defaul is 1
                    compaConfig.setIsActive(1);
                    compaConfig.setCreatedUser(loginUserInf.getId());

                    // get company config details from "buttons" key in json
                    ArrayList<LinkedHashMap<String, Object>> buttons = (ArrayList) companyConfig.get("buttons");
                    /**
                     * get company config detail.
                     */
                    List<CompanyConfigDetails> listCompanyConfigDetails = new ArrayList<>();
                    for (int i = 0; i < buttons.size(); i++) {
                        LinkedHashMap<String, Object> companyConfigDetail = buttons.get(i);
                        // get "code" from json. the "code" field in table = "actionId" in json
                        String actionId = companyConfigDetail.get("actionId").toString();
                        // get "content" from json. the "content" field in table = "name" in json
                        String name = companyConfigDetail.get("name").toString();

                        // crate a CompanyConfig from abve infor
                        CompanyConfigDetails companyConfigDetails = new CompanyConfigDetails();
                        companyConfigDetails.setCode(actionId);
                        companyConfigDetails.setContent(name);
                        companyConfigDetails.setUserRoleId(userRoleId);
                        companyConfigDetails.setCreatedUser(loginUserInf.getId());
                        // by default is 1
                        companyConfigDetails.setIsActive(1);
                        companyConfigDetails.setOrder(i);

                        // add to list companyconfig details
                        listCompanyConfigDetails.add(companyConfigDetails);
                        gui.put(compaConfig, listCompanyConfigDetails);
                    }
                }
            }
        }
        return gui;
    }

    private boolean updateCompany(WebCompanyWorkflow companyWorkflow, MsalesLoginUserInf loginUserInf) {
        try {
            if (loginUserInf != null) {
                Company company = userController.getDataService().getRowById(loginUserInf.getCompanyId(), Company.class);
                if (company != null && company.getId() > 0) {
                    company.setBgColor(companyWorkflow.getBgColor());
                    company.setTextColor(companyWorkflow.getTextColor());
                    company.setButtonBgColor(companyWorkflow.getButtonBgColor());
                    company.setButtonBgColorOver(companyWorkflow.getButtonBgColorOver());
                    company.setTopBarBGColor(companyWorkflow.getTopBarBgColor());
                    company.setUpdatedUser(loginUserInf.getId());

                    // update to db
                    int updated = userController.getDataService().updateRow(company);
                    return updated > 0;
                }
            }
        } catch (Exception e) {
            System.err.println(">>>WebWorkflowController#updateCompany: " + e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * Validate json file that user post to server.
     * <p>
     * @param file is file templets
     * @param classs is class you want convert to it.
     */
    private boolean validateFileData(MultipartFile file, Class<?> classs) {
        boolean isOK = false;
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                MsalesJsonUtils.getObjectFromBytes(bytes, classs);
                isOK = true;
            } catch (IOException ex) {
                isOK = false;
                Logger.getLogger(WebWorkflowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return isOK;
    }

    /**
     * Get list workflow
     * <p>
     * @param webWorkflow
     * <p>
     * @return
     */
    private List<HashMap<WorkflowType, List<LinkedHashMap<Workflow, WorkflowDetails>>>> getListWorkflowAndType(HttpServletRequest request, MultipartFile file) {

        MsalesLoginUserInf loginUserInf = LoginContext.getLogin(request);
        List<HashMap<WorkflowType, List<LinkedHashMap<Workflow, WorkflowDetails>>>> linkedHashMaps = new ArrayList<>();

        // list worklow and workflowdetail
        List<HashMap<Workflow, WorkflowDetails>> listworkflow = new ArrayList<>();
        // list workflow type
        HashMap<WorkflowType, List<LinkedHashMap<Workflow, WorkflowDetails>>> workflowType = new LinkedHashMap<>();
        // get list webworkflow from json
        WebWorkflow webWorkflows[] = getData(file);
        for (WebWorkflow webWorkflow : webWorkflows) {
            // creatr workflow type
            WorkflowType type = new WorkflowType();
            type.setName(webWorkflow.getWorkFlowTypeName());

            // create workflow tye and workflow details
//            List<LinkedHashMap<String, Object>> listWebworkflows = webWorkflow.getWorkFlow();
//            for (LinkedHashMap<String, Object> listWebworkflow : listWebworkflows) {
//                // create workflow
//                Workflow workflow = new Workflow();
//                workflow.setTitle(listWebworkflow.get("content").toString());
//                workflow.setCode(listWebworkflow.get("code").toString());
//                workflow.setIsImage(Integer.parseInt(listWebworkflow.get("isImage").toString()));
//                workflow.setIsRequired(Integer.parseInt(listWebworkflow.get("isRequired").toString()));
//
//                if (listWebworkflow.get("code") != null) {
//                    workflow.setOrder(Integer.parseInt(listWebworkflow.get("order").toString()));
//                } else {
//                    // create workflow details
//                    if (listWebworkflow.get("options_select") != null) {
//                        List<HashMap<String, Object>> listWDetails = (ArrayList) listWebworkflow.get("options_select");
//                        for (HashMap<String, Object> listWDetail : listWDetails) {
//                            WorkflowDetails workflowDetails = new WorkflowDetails();
//                            // type 2 is option check update
//                            workflowDetails.setActionType(2);
//                            workflowDetails.setContent(listWDetail.get("name").toString());
//                        }
//
//                    }
//                }
//
//            }
        }

        return linkedHashMaps;
    }

    @RequestMapping(value = "/workflow/importWorkflow", method = RequestMethod.POST)
    public String importWorkFlowByExcel(HttpServletRequest request, @RequestParam("file_workflow") MultipartFile multipartFile) throws IOException {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        int packageService = 3;
        if (userInf.getPackageService() != null) {
            packageService = userInf.getPackageService();
        }
        //Get data fromData.
        String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        Workbook workbook = null;
        boolean isXLSX = false;
        //Check file not excel
        switch (ext) {
            case "xlsx":
                XSSFWorkbook xlsxBook = new XSSFWorkbook(multipartFile.getInputStream());
                workbook = (Workbook) xlsxBook;
                isXLSX = true;
                break;
            case "xls":
                HSSFWorkbook xlsBook  = new HSSFWorkbook(multipartFile.getInputStream());
                workbook = (Workbook) xlsBook;
                break;
            default:
                String errorException = "Lỗi: File import cần là file Excel có phần mở rộng xlsx hoặc xls.";
                request.setAttribute("errorException", errorException);
                request.setAttribute("importExcelSuccess", false);
                return "workflow";
        }
        //Check nameSheet 
        boolean nameSheetIsExists = false;

        Sheet guiSheet = workbook.getSheet("GUI");
        Sheet salesSheet = workbook.getSheet("WORKFLOW_SALES");
        Sheet careSheet = workbook.getSheet("WORKFLOW_CARE");
        Sheet competitorSheet = workbook.getSheet("COMPETITOR_LIST");
        
        List<WorkflowType> workflowTypeList = workflowService.getListWorkflowType(dataService);
        Company company = null;

        Transaction transaction = null;
        org.hibernate.Session dataSession = null;

        //import GUI
        if (guiSheet != null) {
            List<CompanyConfig> oldCompanyConfigList = workflowService.getListCompanyConfig(userInf.getCompanyId(), dataService);
            List<CompanyConfigDetails> oldCompanyConfigDetailsList = workflowService.getListCompanyConfigDetailsByListConfig(oldCompanyConfigList, dataService);

            nameSheetIsExists = true;
            //read excel with max 100 row
            List<List<Object>> objectList = MsalesReadExcel.read(guiSheet, 0, 100);

            try {
                dataSession = dataService.openSession();
                transaction = dataSession.beginTransaction();

                //get list GUI APP config
                List<CompanyConfig> companyConfigList = getListCompanyConfig(objectList, userInf);
                try {
                    //get company config
                    company = getCompanyForListObject(workbook,guiSheet, userInf);
                    //update company config
                    dataSession.update(company);
                } catch (Exception ex) {
                    String errorException = "Lỗi: Sheet GUI không đúng định dạng như file mẫu!";
                    request.setAttribute("errorException", errorException);
                    request.setAttribute("importExcelSuccess", false);
                    return "workflow";
                }

                //insert company config
                for (CompanyConfig companyConfig : companyConfigList) {
                    //find id of companyConfig
                    CompanyConfig foundCompanyConfig = null;
                    for (CompanyConfig oldCompanyConfig : oldCompanyConfigList) {
                        if (Objects.equals(companyConfig.getUserRoles().getId(), oldCompanyConfig.getUserRoles().getId()) && oldCompanyConfig.getName().equalsIgnoreCase(companyConfig.getName())) {
                            foundCompanyConfig = oldCompanyConfig;
                            break;
                        }
                    }
                    //insert CompanyConfig if not exist
                    if (foundCompanyConfig == null) {
                        foundCompanyConfig = companyConfig;
                        dataSession.save(foundCompanyConfig);
                    }

                    for (CompanyConfigDetails companyConfigDetails : companyConfig.getCompanyConfigDetails()) {
                        //insert company config details
                        if (workflowService.checkImportWorkflowByPackage(companyConfigDetails.getCode(), packageService)) {
                            companyConfigDetails.setCompanyConfigs(foundCompanyConfig);
                            companyConfigDetails.setIsActive(1);
                            companyConfigDetails.setCreatedUser(userInf.getId());
                            companyConfigDetails.setCreatedAt(new Date());
                            companyConfigDetails.setUpdatedUser(0);
                            companyConfigDetails.setUpdatedAt(null);
                            companyConfigDetails.setDeletedUser(0);
                            companyConfigDetails.setDeletedAt(null);
                            dataSession.save(companyConfigDetails);
                        }
                    }
                }

                //delete old company configDetails
                for (CompanyConfigDetails oldCompanyConfigDetails : oldCompanyConfigDetailsList) {
                    oldCompanyConfigDetails.setDeletedAt(new Date());
                    oldCompanyConfigDetails.setDeletedUser(userInf.getId());
                    dataSession.update(oldCompanyConfigDetails);
                }

                //commit GUI
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                String errorException = "Lỗi: Sheet GUI:" + e.getMessage();
                request.setAttribute("errorException", errorException);
                request.setAttribute("importExcelSuccess", false);
                return "workflow";
            } finally {
                dataSession.close();
            }
        }

        //import Workflow Sale
        if (salesSheet != null) {
            nameSheetIsExists = true;

            //read excel with max 100 row
            List<List<Object>> objectList = MsalesReadExcel.read(salesSheet, 0, 100);
            List<Workflow> workflows;
            try {
                workflows = getListWorkflowForSales(objectList, workflowTypeList, userInf);
            } catch (Exception e) {
                String errorException = "Lỗi: Sheet WORKFLOW_SALES không đúng định dạng như file mẫu!";
                request.setAttribute("errorException", errorException);
                request.setAttribute("importExcelSuccess", false);
                return "workflow";
            }

            try {
                List<Workflow> oldWorkflowSalesList = workflowService.getListWorkflow(2, userInf.getCompanyId(), dataService);
                dataSession = dataService.openSession();
                transaction = dataSession.beginTransaction();

                //insert new workflow
                for (Workflow workflow : workflows) {
                    if (workflowService.checkImportWorkflowByPackage(workflow.getCode(), packageService)) {
                        dataSession.save(workflow);
                    }
                }

                //delete all old workflow sales
                for (Workflow oldWorkflow : oldWorkflowSalesList) {
                    oldWorkflow.setDeletedAt(new Date());
                    oldWorkflow.setDeletedUser(userInf.getId());
                    dataSession.update(oldWorkflow);
                }
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                String errorException = "Lỗi: Import Sheet WORKFLOW_SALES không thành công!";
                request.setAttribute("errorException", errorException);
                request.setAttribute("importExcelSuccess", false);
                return "workflow";
            } finally {
                dataSession.close();
            }
        }

        //import Workflow Care
        if (careSheet != null) {
            nameSheetIsExists = true;

            //read excel with max 100 row
            List<List<Object>> objectList = MsalesReadExcel.read(careSheet, 0, 100);
            List<Workflow> workflows;
            try {
                workflows = insertListWorkflowForCare(objectList, workflowTypeList, userInf);
            } catch (Exception e) {
                String errorException = "Lỗi: Sheet WORKFLOW_CARE không đúng định dạng như file mẫu!";
                request.setAttribute("errorException", errorException);
                request.setAttribute("importExcelSuccess", false);
                return "workflow";
            }

            try {
                List<Workflow> oldWorkflowSalesList = workflowService.getListWorkflow(1, userInf.getCompanyId(), dataService);

                Workflow oldCompetitorWorkflow = null;
                List<WorkflowDetails> oldCompetitorWorkflowDetailsList = null;
                List<Workflow> oldCompetitorWorkflowList = new ArrayList<>();
                List<WorkflowDetails> oldCompetitorDetailsList = null;

                for (Workflow workflow : oldWorkflowSalesList) {
                    if (workflow.getCode().equalsIgnoreCase("COMPETITOR_LIST")) {
                        oldCompetitorWorkflow = workflow;
                    } else if (workflow.getWorkflowTypes().getCode().equalsIgnoreCase("DIRECT_COMPETITOR")) {
                        oldCompetitorWorkflowList.add(workflow);
                    }
                }

                if (oldCompetitorWorkflow != null) {
                    oldCompetitorWorkflowDetailsList = workflowService.getCompetitorWorkflowDetailsList(oldCompetitorWorkflow, dataService);
                    //OldCompetitorWorkflowList = workflowService.getCompetitorWorkflowList(oldCompetitorWorkflowDetailsList, dataService);

                    oldCompetitorDetailsList = workflowService.getCompetitorDetailsList(oldCompetitorWorkflowList, dataService);
                }

                Workflow workflowCompetitor = null;
                dataSession = dataService.openSession();
                transaction = dataSession.beginTransaction();

                //insert new workflow
                for (Workflow workflow : workflows) {
                    if (workflowService.checkImportWorkflowByPackage(workflow.getCode(), packageService)) {
                        dataSession.save(workflow);
                        if (workflow.getCode().equalsIgnoreCase("COMPETITOR_LIST")) {
                            workflowCompetitor = workflow;
                        }
                        if (workflow.getOptions() != null && !workflow.getOptions().isEmpty()) {
                            for (WorkflowDetails workflowDetails : workflow.getOptions()) {
                                try {
                                    dataSession.save(workflowDetails);
                                } catch (Exception ex) {
                                    //insert listObject not success
                                    String errorException = "Lỗi:Import Sheet WORKFLOW_CARE không thành công!";
                                    request.setAttribute("errorException", errorException);
                                    request.setAttribute("importExcelSuccess", false);
                                    return "workflow";
                                }
                            }
                        }
                    }
                }

                //delete all workflow care which can insert
                for (Workflow oldWorkflow : oldWorkflowSalesList) {
                    //delete workflow sales which was add new
                    if (workflowService.checkImportWorkflowByPackage(oldWorkflow.getCode(), packageService)) {
                        oldWorkflow.setDeletedAt(new Date());
                        oldWorkflow.setDeletedUser(userInf.getId());
                        dataSession.update(oldWorkflow);
                    }
                }

                if (oldCompetitorWorkflow != null) {
                    //delete all competitor 
                    oldCompetitorWorkflow.setDeletedAt(new Date());
                    oldCompetitorWorkflow.setDeletedUser(userInf.getId());
                    dataSession.update(oldCompetitorWorkflow);

                    for (WorkflowDetails workflowDetails : oldCompetitorWorkflowDetailsList) {
                        workflowDetails.setDeletedAt(new Date());
                        workflowDetails.setDeletedUser(userInf.getId());
                        dataSession.update(workflowDetails);
                    }

                    for (Workflow workflow : oldCompetitorWorkflowList) {
                        workflow.setDeletedAt(new Date());
                        workflow.setDeletedUser(userInf.getId());
                        dataSession.update(workflow);
                    }

                    for (WorkflowDetails workflowDetails : oldCompetitorDetailsList) {
                        workflowDetails.setDeletedAt(new Date());
                        workflowDetails.setDeletedUser(userInf.getId());
                        dataSession.update(workflowDetails);
                    }
                }

                //import Competitor
                if (competitorSheet != null && workflowCompetitor != null && workflowCompetitor.getIsRequired() < 2 && workflowCompetitor.getIsRequired() >= 0) {
                    //read excel with max 100 row
                    List<List<Object>> objectListCompetior = MsalesReadExcel.read(competitorSheet, 0, 100);
                    List<WorkflowDetails> competitorList;
                    try {
                        competitorList = getListObjectForCompetitor(objectListCompetior, workflowCompetitor, userInf);
                    } catch (Exception e) {
                        String errorException = "Lỗi: Sheet COMPETITOR_LIST không đúng định dạng như file mẫu!";
                        request.setAttribute("errorException", errorException);
                        request.setAttribute("importExcelSuccess", false);
                        return "workflow";
                    }

                    for (WorkflowDetails workflowDetails : competitorList) {
                        try {
                            dataSession.save(workflowDetails);
                            for (Workflow workflow : workflowDetails.getDetails()) {
                                dataSession.save(workflow);
                                for (WorkflowDetails workflowDetailsOther : workflow.getOptions()) {
                                    dataSession.save(workflowDetailsOther);
                                }
                            }
                        } catch (Exception ex) {
                            String errorException = "Lỗi:Import Sheet COMPETITOR_LIST không thành công!";
                            request.setAttribute("errorException", errorException);
                            request.setAttribute("importExcelSuccess", false);
                            return "workflow";
                        }
                    }
                }
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                String errorException = "Lỗi: Import Sheet WORKFLOW_SALES không thành công!";
                request.setAttribute("errorException", errorException);
                request.setAttribute("importExcelSuccess", false);
                return "workflow";
            } finally {
                dataSession.close();
            }
        }

//        //Check exist it nhat 1 file 
        if (!nameSheetIsExists) {
            if(company == null){
                company = dataService.getRowById(userInf.getCompanyId(), Company.class);
                if(company==null){
                    return "noData";
                }
                else{
                    company.setUpdatedUser(userInf.getId());
                    company.setUpdatedAt(new Date());
                    dataService.updateRow(company);
                }
            }
            
            String errorException = "Lỗi: Phải có ít nhất 1 sheet có tên thuộc {GUI, WORKFLOW_SALES, WORKFLOW_CARE}!";
            request.setAttribute("errorException", errorException);
            request.setAttribute("importExcelSuccess", false);
            return "workflow";
        }

        //Save file from here
        try {
            String rootFolder = systemProperties.getProperty("system.workflowRoot");
            String rootPath = LoginContext.getLogin(request).getBranch() + "/" + LoginContext.getLogin(request).getCompanyId();
            File path = new File(rootFolder + "/" + rootPath);
            if (!path.exists() || !path.isDirectory()) {
                path.mkdirs();
            }
            File guiFile = new File(path.getPath() + "/" + systemProperties.getProperty("system.WorkflowExcelXLSX"));
            if (!isXLSX) {
                guiFile = new File(path.getPath() + "/" + systemProperties.getProperty("system.WorkflowExcelXLS"));
            }
            multipartFile.transferTo(guiFile);
        } catch (Exception ex) {
            String errorException = "Lỗi: Không thể lưu file Workflow hiện tại!";
            request.setAttribute("errorException", errorException);
            request.setAttribute("importExcelSuccess", false);
            return "workflow";
        }
        request.setAttribute("importExcelSuccess", true);
        return "workflow";
    }

    public Company getCompanyForListObject(Workbook book,Sheet sheet, MsalesLoginUserInf userInf) {
        Company company = dataService.getRowById(userInf.getCompanyId(), Company.class);
        if (company != null) {
            sheet.getPhysicalNumberOfRows();
            int intStart = 0;
            Iterator<Row> iterator = sheet.rowIterator();
            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (row.getLastCellNum() > 1) {
                    Cell cell1 = row.getCell(0);
                    Cell cell2 = row.getCell(1);
                    Cell cell3 = row.getCell(2);

                    if (intStart > 5) {
                        break;
                    }
                    if (intStart == 0 && cell1 != null && cell2 != null && cell3 != null
                            && !cell1.toString().isEmpty() && !cell2.toString().isEmpty() && !cell3.toString().isEmpty()) {
                        intStart++;
                    } else if (intStart > 0 && cell2 != null && cell3 != null && !cell2.toString().isEmpty()) {                        
                        Color color = cell3.getCellStyle().getFillForegroundColorColor();
                        String hexColor = "";
                        if (color instanceof XSSFColor) {
                            hexColor = Hex.encodeHexString(((XSSFColor) color).getRgb());
                        } else if (color instanceof HSSFColor) {
                            String strTemp = ((HSSFColor) color).getHexString();
                            for(String str : strTemp.split(":")){
                                if(str.length()>2){
                                    str = str.substring(0, 2);
                                }
                                else if(str.length()==1){
                                    str+=str;
                                }
                                hexColor+=str;
                            }
                        }
                        if (hexColor!=null && !hexColor.isEmpty()) {
                            if (cell2.toString().equalsIgnoreCase("bgColor")) {
                                company.setBgColor(hexColor);
                            } else if (cell2.toString().equalsIgnoreCase("textColor")) {
                                company.setTextColor(hexColor);
                            } else if (cell2.toString().equalsIgnoreCase("buttonBgColor")) {
                                company.setButtonBgColor(hexColor);
                            } else if (cell2.toString().equalsIgnoreCase("buttonBgColorOver")) {
                                company.setButtonBgColorOver(hexColor);
                            } else if (cell2.toString().equalsIgnoreCase("topBarBgColor")) {
                                company.setTopBarBGColor(hexColor);
                            }
                            intStart++;
                        }
                    }
                }
            }
            
            company.setUpdatedUser(userInf.getId());
            company.setUpdatedAt(new Date());
            return company;
        } else {
            return null;
        }
    }

    public List<WorkflowDetails> getListObjectForCompetitor(List<List<Object>> objectList, Workflow workflow, MsalesLoginUserInf userInf) {
        List<WorkflowDetails> workflowDetailsList = new ArrayList<>();
        boolean flagStart = false;
        Workflow workflowOther = null;
        WorkflowDetails workflowDetails = null;
        int order = 0;
        for (List list : objectList) {
            if (list.size() > 6) {
                if (!flagStart) {
                    flagStart = true;
                    for (int i = 0; i < 7; i++) {
                        if (list.get(i).toString().trim().isEmpty()) {
                            flagStart = false;
                            break;
                        }
                    }
                } else {
                    if (!list.get(0).toString().trim().isEmpty()) {
                        workflowDetails = new WorkflowDetails();
                        workflowDetails.setActionType(workflow.getIsRequired() == 1 ? 3 : 1);
                        workflowDetails.setCode(null);
                        workflowDetails.setContent(list.get(0).toString().trim());
                        workflowDetails.setWorkflows(workflow);

                        workflowDetails.setCreatedUser(userInf.getId());
                        workflowDetails.setCreatedAt(new Date());
                        workflowDetails.setUpdatedUser(0);
                        workflowDetails.setUpdatedAt(null);
                        workflowDetails.setDeletedUser(0);
                        workflowDetails.setDeletedAt(null);

                        workflowDetails.setDetails(new ArrayList<Workflow>());
                        workflowDetailsList.add(workflowDetails);
                        //reset order
                        order = 0;
                    }

                    if (workflowDetails != null && !list.get(1).toString().trim().isEmpty()
                            && !list.get(2).toString().trim().isEmpty()
                            && !list.get(3).toString().trim().isEmpty()
                            && !list.get(4).toString().trim().isEmpty()) {
                        workflowOther = new Workflow();
                        workflowOther.setTitle(list.get(1).toString().trim());
                        workflowOther.setCode(list.get(2).toString().trim());
                        workflowOther.setCompanyId(userInf.getCompanyId());
                        workflowOther.setWorkflowTypeId(3);//DIRECT_COMPETITOR
                        workflowOther.setIsRequired(Integer.parseInt(list.get(3).toString().trim().substring(0, list.get(3).toString().trim().indexOf("."))));
                        workflowOther.setIsImage(Integer.parseInt(list.get(4).toString().trim().substring(0, list.get(4).toString().trim().indexOf("."))));
                        workflowOther.setOrder(++order);
                        workflowOther.setCreatedAt(new Date());
                        workflowOther.setCreatedUser(userInf.getId());
                        workflowOther.setUpdatedAt(null);
                        workflowOther.setUpdatedUser(0);
                        workflowOther.setDeletedAt(null);
                        workflowOther.setDeletedUser(0);

                        workflowOther.setWorkflowDetails(workflowDetails);
                        workflowDetails.getDetails().add(workflowOther);
                        workflowOther.setOptions(new ArrayList<WorkflowDetails>());
                    }

                    if (workflowOther != null && !list.get(5).toString().trim().isEmpty()
                            && !list.get(6).toString().trim().isEmpty()) {
                        WorkflowDetails workflowDetailsOther = new WorkflowDetails();

                        workflowDetailsOther.setContent(list.get(5).toString().trim());
                        workflowDetailsOther.setCode(null);
                        workflowDetailsOther.setActionType(Integer.parseInt(list.get(6).toString().trim().substring(0, list.get(6).toString().trim().indexOf("."))));
                        workflowDetailsOther.setWorkflows(workflowOther);

                        workflowDetailsOther.setCreatedAt(new Date());
                        workflowDetailsOther.setCreatedUser(userInf.getId());
                        workflowDetailsOther.setUpdatedAt(null);
                        workflowDetailsOther.setUpdatedUser(0);
                        workflowDetailsOther.setDeletedAt(null);
                        workflowDetailsOther.setDeletedUser(0);

                        workflowOther.getOptions().add(workflowDetailsOther);
                    }
                }
            }
        }
        return workflowDetailsList;
    }

    private int convertExcelToInt(String value) throws NumberFormatException {
        return Integer.parseInt(value.trim().substring(0, value.trim().indexOf(".")));
    }

    public boolean checkBreakForWorkflowCompetitor(List<Object> objects) {
        if (objects.size() >= 7) {
            String cell0 = objects.get(0).toString();
            String cell6 = objects.get(6).toString();
            return !((cell0 != null && !cell0.isEmpty() && !cell0.trim().isEmpty()) || (cell6 != null && !cell6.isEmpty() && !cell6.trim().isEmpty()));
        } else {
            return true;
        }
    }

    public List<Workflow> insertListWorkflowForCare(List<List<Object>> objectList, List<WorkflowType> workflowTypeList, MsalesLoginUserInf userInf) {
        List<Workflow> workflowList = new ArrayList<>();
        int orderWorkflowType = 0;
        int order = 0;
        boolean flagStart = false;
        boolean flagWorkflowType = false;
        WorkflowType workflowType = null;
        Workflow workflow = null;
        for (List list : objectList) {
            if (list.size() > 8) {
                if (!flagStart) {
                    flagStart = true;
                    for (int i = 0; i < 9; i++) {
                        if (list.get(i).toString().isEmpty()) {
                            flagStart = false;
                            break;
                        }
                    }
                } else if (flagStart) {
                    if (!list.get(0).toString().trim().isEmpty() && !list.get(1).toString().trim().isEmpty()) {
                        //find workflowType
                        for (WorkflowType oldWorkflowType : workflowTypeList) {
                            if (list.get(1).toString().trim().equalsIgnoreCase(oldWorkflowType.getCode())) {
                                workflowType = oldWorkflowType;
                                flagWorkflowType = true;
                                break;
                            }

                            if (!flagWorkflowType) {
                                //insert new WorkflowType
                                workflowType = new WorkflowType();
                                workflowType.setType(1);
                                workflowType.setName(list.get(0).toString().trim());
                                workflowType.setCode(list.get(1).toString().trim());
                                workflowType.setOrder(++orderWorkflowType);
                                workflowType.setCreatedUser(userInf.getId());
                                workflowType.setCreatedAt(new Date());
                                workflowType.setUpdatedUser(0);
                                workflowType.setUpdatedAt(null);
                                workflowType.setDeletedAt(new Date());
                                workflowType.setDeletedUser(0);
                                dataService.insert(workflowType);
                            }
                        }
                        order = 0;
                    }
                    if (flagWorkflowType) {
                        //insert new workflow
                        if (!(list.get(2).toString().trim().isEmpty()
                                || list.get(3).toString().trim().isEmpty()
                                || list.get(4).toString().trim().isEmpty()
                                || list.get(5).toString().trim().isEmpty())) {
                            //set data for workflow
                            workflow = new Workflow();
                            workflow.setWorkflowTypes(workflowType);
                            workflow.setCompanyId(userInf.getCompanyId());
                            workflow.setWorkflowDetails(null);
                            workflow.setTitle(list.get(2).toString().trim());
                            workflow.setCode(list.get(3).toString().trim());

                            workflow.setIsRequired(Integer.parseInt(list.get(4).toString().trim().substring(0, list.get(5).toString().trim().indexOf('.'))));
                            workflow.setIsImage(Integer.parseInt(list.get(5).toString().trim().substring(0, list.get(5).toString().trim().indexOf('.'))));
                            workflow.setOrder(++order);

                            workflow.setCreatedAt(new Date());
                            workflow.setCreatedUser(userInf.getId());
                            workflow.setUpdatedAt(null);
                            workflow.setUpdatedUser(0);
                            workflow.setDeletedAt(new Date());
                            workflow.setDeletedUser(0);
                            //add list include workflowDetails
                            workflow.setOptions(new ArrayList<WorkflowDetails>());

                            workflowList.add(workflow);
                        }

                        if (workflow != null && !list.get(6).toString().trim().isEmpty() && !list.get(7).toString().trim().isEmpty()) {
                            //insert new workflowDetails
                            WorkflowDetails workflowDetails = new WorkflowDetails();
                            workflowDetails.setCode(list.size() > 8 ? list.get(8).toString().trim() : null);
                            workflowDetails.setActionType(Integer.parseInt(list.get(7).toString().trim().substring(0, list.get(7).toString().trim().indexOf("."))));
                            workflowDetails.setContent(list.get(6).toString().trim());
                            workflowDetails.setWorkflows(workflow);

                            workflowDetails.setCreatedUser(userInf.getId());
                            workflowDetails.setCreatedAt(new Date());
                            workflowDetails.setUpdatedUser(0);
                            workflowDetails.setUpdatedAt(null);
                            workflowDetails.setDeletedUser(0);
                            workflowDetails.setDeletedAt(null);

                            workflow.getOptions().add(workflowDetails);
                        }
                    }
                }
            }
        }
        return workflowList;
    }

    //Delete workflow have type = care
    public boolean deletedWorkflowCare(MsalesLoginUserInf userInf) {
        List<Object> objects = new ArrayList<>();
        ParameterList parameterList = new ParameterList();
        parameterList.add("companys.id", userInf.getCompanyId());
        List<Integer> idList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            idList.add(i + 1);
        }
        parameterList.in("workflowTypes.id", idList);
        List<Workflow> workflows = dataService.getListOption(Workflow.class, parameterList);
        List<Integer> idListWFDetails = new ArrayList<>();
        for (Workflow workflow : workflows) {
            idListWFDetails.add(workflow.getId());

            workflow.setDeletedUser(userInf.getId());
            workflow.setDeletedAt(new Date());
        }

        if (!workflows.isEmpty()) {
            objects.addAll(workflows);
            ParameterList param = new ParameterList();
            param.in("workflows.id", idListWFDetails);
            List<WorkflowDetails> workflowDetails = dataService.getListOption(WorkflowDetails.class, param);
            if (!workflowDetails.isEmpty()) {
                for (WorkflowDetails wDetails : workflowDetails) {
                    wDetails.setDeletedUser(userInf.getId());
                    wDetails.setDeletedAt(new Date());
                }
                objects.addAll(workflowDetails);
            }
        }

        if (objects.isEmpty()) {
            return true;
        } else {
            return dataService.updateArray(objects);
        }
    }

    public boolean checkIsEmptyForListObjectCare(List<Object> objects) {
        if (objects.size() >= 9) {
            String cell0 = objects.get(0).toString();
            String cell1 = objects.get(1).toString();
            String cell2 = objects.get(2).toString();
            String cell3 = objects.get(3).toString();
            String cell4 = objects.get(4).toString();
            String cell5 = objects.get(5).toString();
            String cell6 = objects.get(6).toString();
            String cell7 = objects.get(7).toString();
            String cell8 = objects.get(8).toString();
            if ((cell0 != null && !cell0.isEmpty() && !cell0.trim().isEmpty())
                    || (cell1 != null && !cell1.isEmpty() && !cell1.trim().isEmpty())
                    || (cell2 != null && !cell2.isEmpty() && !cell2.trim().isEmpty())
                    || (cell3 != null && !cell3.isEmpty() && !cell3.trim().isEmpty())
                    || (cell4 != null && !cell4.isEmpty() && !cell4.trim().isEmpty())
                    || (cell5 != null && !cell5.isEmpty() && !cell5.trim().isEmpty())
                    || (cell6 != null && !cell6.isEmpty() && !cell6.trim().isEmpty())
                    || (cell7 != null && !cell7.isEmpty() && !cell7.trim().isEmpty())
                    || (cell8 != null && !cell8.isEmpty() && !cell8.trim().isEmpty())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public List<Workflow> getListWorkflowForSales(List<List<Object>> objectList, List<WorkflowType> workflowTypeList, MsalesLoginUserInf userInf) {
        List<Workflow> workflowList = new ArrayList<>();

        int order = 0;
        boolean flagStart = false;
        for (List list : objectList) {
            if (list.size() > 5) {
                if (!flagStart) {
                    flagStart = true;
                    for (int i = 0; i < 6; i++) {
                        if (list.get(i).toString().isEmpty()) {
                            flagStart = false;
                            break;
                        }
                    }

                } else if (flagStart) {
                    if (!(list.get(2).toString().trim().isEmpty()
                            || list.get(3).toString().trim().isEmpty()
                            || list.get(4).toString().trim().isEmpty()
                            || list.get(5).toString().trim().isEmpty())) {
                        //set data for workflow
                        Workflow workflow = new Workflow();
                        workflow.setWorkflowTypeId(4);//WORKFLOW_COMPANY
                        workflow.setCompanyId(userInf.getId());
                        workflow.setWorkflowDetails(null);
                        workflow.setTitle(list.get(2).toString().trim());
                        workflow.setCode(list.get(3).toString().trim());

                        workflow.setIsRequired(Integer.parseInt(list.get(4).toString().trim().substring(0, list.get(5).toString().trim().indexOf('.'))));
                        workflow.setIsImage(Integer.parseInt(list.get(5).toString().trim().substring(0, list.get(5).toString().trim().indexOf('.'))));
                        workflow.setOrder(++order);

                        workflow.setCreatedAt(new Date());
                        workflow.setCreatedUser(userInf.getId());
                        workflow.setUpdatedAt(null);
                        workflow.setUpdatedUser(0);
                        workflow.setDeletedAt(new Date());
                        workflow.setDeletedUser(0);

                        workflowList.add(workflow);
                    }
                }
            }
        }

        return workflowList;
    }

    public boolean deletedWorkflowOfSales(MsalesLoginUserInf userInf) {
        ParameterList parameterList = new ParameterList();
        parameterList.add("companys.id", userInf.getCompanyId());
        parameterList.add("workflowTypes.id", 4);
        List<Workflow> workflows = dataService.getListOption(Workflow.class, parameterList);
        for (Workflow workflow : workflows) {
            workflow.setDeletedUser(userInf.getId());
            workflow.setDeletedAt(new Date());
        }

        if (workflows.isEmpty()) {
            return true;
        } else {

            boolean updateSuccess = dataService.updateArray(workflows);
            return updateSuccess;
        }

    }

//getCompanyConfig
    public List<CompanyConfig> getListCompanyConfig(List<List<Object>> objectList, MsalesLoginUserInf userInf) {
        List<CompanyConfig> companyConfigs = new ArrayList<>();
        int numberStart = 0;
        CompanyConfig ccfSalesmanSalepages = new CompanyConfig();
        ccfSalesmanSalepages.setUserRoleId(6);
        ccfSalesmanSalepages.setName("sales_page");

        CompanyConfig ccfSalesmanMainPages = new CompanyConfig();
        ccfSalesmanMainPages.setUserRoleId(6);
        ccfSalesmanMainPages.setName("main_page");

        CompanyConfig ccfSalesSupSalepages = new CompanyConfig();
        ccfSalesSupSalepages.setUserRoleId(4);
        ccfSalesSupSalepages.setName("sup_sales_page");

        CompanyConfig ccfSalesSupMainPages = new CompanyConfig();
        ccfSalesSupMainPages.setUserRoleId(4);
        ccfSalesSupMainPages.setName("main_page");

        List<CompanyConfigDetails> salesmanMainPage = new ArrayList<>();
        List<CompanyConfigDetails> salesmanSalesPage = new ArrayList<>();
        List<CompanyConfigDetails> salesSupMainPage = new ArrayList<>();
        List<CompanyConfigDetails> salesSupSalesPage = new ArrayList<>();
        int orderSalespages = 0, orderMainPage = 0, orderSupsalesPage = 0, orderSupMainPage = 0;
        for (List<Object> objects : objectList) {
            String cell0 = "";
            String cell1 = "";
            String cell2 = "";
            String cell3 = "";
            String cell4 = "";
            if (objects.size() >= 5) {
                cell0 = objects.get(0).toString();
                cell1 = objects.get(1).toString();
                cell2 = objects.get(2).toString();
                cell3 = objects.get(3).toString();
                cell4 = objects.get(4).toString();
            }
            if (!cell0.isEmpty() && !cell1.isEmpty() && !cell2.isEmpty() && !cell3.isEmpty() && !cell4.isEmpty()) {
                numberStart++;
            }
            //Get data
            CompanyConfigDetails cDetailsSalespage = new CompanyConfigDetails();
            CompanyConfigDetails cDetailsMainPage = new CompanyConfigDetails();
            if (numberStart == 1) {
                //is the Salesman config
                if (cell1 != null && !cell1.isEmpty() && !cell1.trim().isEmpty()) {
                    cDetailsMainPage.setCode(cell1);
                    cDetailsMainPage.setContent(cell2);
                    cDetailsMainPage.setUserRoleId(6);
                    cDetailsMainPage.setOrder(orderMainPage++);
                    salesmanMainPage.add(cDetailsMainPage);
                }
                if (cell3 != null && !cell3.isEmpty() && !cell3.trim().isEmpty()) {
                    cDetailsSalespage.setCode(cell3);
                    cDetailsSalespage.setContent(cell4);
                    cDetailsSalespage.setUserRoleId(6);
                    cDetailsSalespage.setOrder(orderSalespages++);
                    salesmanSalesPage.add(cDetailsSalespage);
                }
            } else if (numberStart == 2) {
                //is the salessup config
                if (cell1 != null && !cell1.isEmpty() && !cell1.trim().isEmpty()) {
                    cDetailsMainPage.setCode(cell1);
                    cDetailsMainPage.setContent(cell2);
                    cDetailsMainPage.setUserRoleId(4);
                    cDetailsMainPage.setOrder(orderSupMainPage++);
                    salesSupMainPage.add(cDetailsMainPage);
                }
                if (cell3 != null && !cell3.isEmpty() && !cell3.trim().isEmpty()) {
                    cDetailsSalespage.setCode(cell3);
                    cDetailsSalespage.setContent(cell4);
                    cDetailsSalespage.setUserRoleId(4);
                    cDetailsSalespage.setOrder(orderSupsalesPage++);
                    salesSupSalesPage.add(cDetailsSalespage);
                }
            } else if (numberStart > 2) {
                break;
            }
        }

        if (!salesmanMainPage.isEmpty() && !salesmanSalesPage.isEmpty() && !salesSupMainPage.isEmpty() && !salesSupSalesPage.isEmpty()) {
            ccfSalesmanMainPages.setCompanyConfigDetails(salesmanMainPage);
            companyConfigs.add(ccfSalesmanMainPages);

            ccfSalesmanSalepages.setCompanyConfigDetails(salesmanSalesPage);
            companyConfigs.add(ccfSalesmanSalepages);

            ccfSalesSupMainPages.setCompanyConfigDetails(salesSupMainPage);
            companyConfigs.add(ccfSalesSupMainPages);

            ccfSalesSupSalepages.setCompanyConfigDetails(salesSupSalesPage);
            companyConfigs.add(ccfSalesSupSalepages);
            for (CompanyConfig config : companyConfigs) {
                config.setCreatedUser(userInf.getId());
                config.setCreatedAt(new Date());
                config.setUpdatedAt(null);
                config.setUpdatedUser(0);
                config.setDeletedAt(null);
                config.setDeletedUser(0);
                config.setIsActive(1);
                config.setCompanyId(userInf.getCompanyId());
            }
        }
        return companyConfigs;
    }

}
