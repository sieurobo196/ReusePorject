/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.csb.controller;
import com.fasterxml.jackson.databind.JsonMappingException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.itt.msales.common.MsalesConstants;
import vn.itt.msales.common.MsalesStatus;
import vn.itt.msales.entity.response.MsalesResponse;
import vn.itt.msales.common.json.MsalesJsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import vn.itt.msales.app.service.LoginService;
import vn.itt.msales.csb.auth.MsalesHttpHeader;
import vn.itt.msales.database.dbrouting.DatabaseType;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.request.MsalesRequest;
import vn.itt.msales.entity.response.MsaleResponseStatus;
import vn.itt.msales.services.DataService;

@Controller
@RequestMapping(name = "/*", headers = {"Accept=application/json"}, produces = "application/json;charset=UTF-8")
public class CsbController {

    private final String ACTION = "action";
    private final String ORIGINAL_ACTION = "/{" + ACTION + "}";

    //private String mAction;
    @Autowired
    protected User mUser;

    @Autowired
    protected DataService dataService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private LoginService loginService;

    /**
     * get data service
     *
     * @return
     */
    public DataService getDataService() {
        return dataService;
    }

    /**
     * Set data service
     *
     * @param dataService
     */
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @RequestMapping(value = ORIGINAL_ACTION)
    public String requestHandling(@RequestBody String json, @PathVariable(value = ACTION) String action,
            HttpServletRequest request, HttpServletResponse response, Model model, HttpSession sessionServlet) {
        String mAction = action;
        // get header informations.
        MsalesHttpHeader header = getHeadersInfo(request);
        int branch = 0;

        //RegisterDevice
        if (MsalesConstants.MODULE.APP.ACTION_REGISTER_DEVICE_APP.equals(mAction)
                || MsalesConstants.MODULE.COMPANY.ACTION_CREATE_DEMO_COMPANY.equals(mAction)) {
            dataService.setBranch(DatabaseType.DATABASE_TYPE_COMPANY.getBranch());;
        } else {
            // get branch database from header.
            branch = header.getBranch();
            // check branch in header request.
            if (branch == 0 || !header.checkBranch()) {
                model.addAttribute(MsalesHttpHeader.BRANCH, (new MsaleResponseStatus(MsalesStatus.BRANCH_ERROR)).getMessage());
                return forwardController(MsalesConstants.MODULE.CSB.REQUEST_ERROR);
            } else {
                dataService.setBranch(branch);
            }
        }

        // if request from client is not login action
        if (!MsalesConstants.MODULE.USER.ACTION_LOGIN.equals(mAction)
                && !MsalesConstants.MODULE.APP.ACTION_LOGIN_APP.equals(mAction)
                && !MsalesConstants.MODULE.APP.ACTION_REGISTER_DEVICE_APP.equals(mAction)
                && !MsalesConstants.MODULE.COMPANY.ACTION_CREATE_DEMO_COMPANY.equals(mAction)) {
            // get access token
            final String token = header.getAuthorization();
            //check login and access
            int check = loginService.checkLogin(token, dataService, request, mAction);
            if (check == 0)//not login
            {
                model.addAttribute(MsalesHttpHeader.AUTHORIZATION, (new MsaleResponseStatus(HttpStatus.UNAUTHORIZED)).getMessage());
                return forwardController(MsalesConstants.MODULE.CSB.REQUEST_ERROR);
            } else if (check == -1)//cannot acess
            {
                return forwardController(MsalesConstants.MODULE.CSB.ACCESS_IS_DENIED);
            }
            //check = 1 => pass
            model.addAttribute(MsalesHttpHeader.HEADER, header);
        }

        // get content form JSON request from client.
        JsonNode jsonNode;
        try {
            jsonNode = MsalesJsonUtils.getJsonNote(json);

            model.addAttribute(MsalesHttpHeader.BRANCH, branch);

            MsalesRequest msalesRequest = MsalesJsonUtils.getObjectFromJSON(json, MsalesRequest.class);
            model.addAttribute(MsalesConstants.CONTENTS, MsalesJsonUtils.getValue(jsonNode, MsalesConstants.CONTENTS));
            //Logger.getLogger(CsbController.class.getName()).log(Level.SEVERE, "action:\n{0}", mAction);
            //Logger.getLogger(CsbController.class.getName()).log(Level.SEVERE, "CSB: \n{0}", json);

            MsalesPageRequest pageReques = msalesRequest.getPage();
            // get lis objec, json page is required

            if (mAction.toLowerCase().contains("list") || mAction.toLowerCase().contains("search")) {

                if (mAction.toLowerCase().contains("cblist")) {
                    pageReques = new MsalesPageRequest(0, 0);
                } else if (pageReques == null) {
                    pageReques = new MsalesPageRequest(1, 10);
                    //return forwardController(MsalesConstants.MODULE.CSB.PAGE_REQUIRED);
                } else {
                    // check pageno
                    if (pageReques.getPageNo() == null || pageReques.getPageNo() <= 0) {
                        // get by default pageNo
                        pageReques.setPageNo(1);
                    }
                    // check records in page
                    if (pageReques.getRecordsInPage() == null || pageReques.getRecordsInPage() <= 0) {
                        // set by default recordsInPage
                        pageReques.setRecordsInPage(10);
                    }
                }
                model.addAttribute(MsalesConstants.PAGE, pageReques);
            }
        } catch (Exception ex) {
            if (ex instanceof JsonMappingException) {
                return forwardController(MsalesConstants.MODULE.CSB.JSON_INVALID);
            }

            model.addAttribute("ERROR", ex.getMessage());
            return forwardController(MsalesConstants.MODULE.CSB.REQUEST_ERROR);
        }
        return forwardController(mAction);
    }

    /**
     * Check branch number request from client.
     * <p>
     * @return if branch number is out of range then return error.
     */
    @RequestMapping(value = MsalesConstants.MODULE.CSB.CONNECTION_FAIL)
    private @ResponseBody
    String connectedFail() {
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.DATABASE_CONNECT_FIAL));
    }

    @RequestMapping(value = MsalesConstants.MODULE.CSB.PAGE_REQUIRED)
    private @ResponseBody
    String pageRequired() {

        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_PAGE_REQUIRED));

    }

    @RequestMapping(value = MsalesConstants.MODULE.CSB.PAGE_FILEDS_REQUEST)
    private @ResponseBody
    String pageFiledsRequest(HttpServletRequest request
    ) {
        MsalesPageRequest pageRequest = (MsalesPageRequest) request.getAttribute(MsalesConstants.PAGE);
        LinkedHashMap<String, String> field = pageRequest.checkFiled();
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_FIELD_REQUIRED, field));
    }

    @RequestMapping(value = MsalesConstants.MODULE.CSB.JSON_INVALID)
    private @ResponseBody
    String jsonInvalid() {
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_INVALID));
    }

    @RequestMapping(value = MsalesConstants.MODULE.CSB.ACCESS_IS_DENIED)
    private @ResponseBody
    String accessDenied() {
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.FORBIDDEN));
    }

    @RequestMapping(value = MsalesConstants.MODULE.CSB.REQUEST_ERROR)
    private @ResponseBody
    String requestError(HttpServletRequest httpServletRequest
    ) {
        String er[];
        if (httpServletRequest != null) {
            Enumeration attribute = httpServletRequest.getAttributeNames();
            while (attribute.hasMoreElements()) {
                String key = (String) attribute.nextElement();
                // JSON format invalid
                switch (key) {
                    case "ERROR": {
                        String value = httpServletRequest.getAttribute("ERROR").toString();
                        if (value != null) {
                            er = value.split("\\n");
                            if (er.length >= 3) {
                                // er[0]: the root cause
                                // er[1]: specify line numbe error.
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_INVALID, er[0] + er[er.length - 1]));
                            } else {
                                return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_INVALID, value));
                            }
                        }
                    }
                    case MsalesHttpHeader.BRANCH:
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.BRANCH_ERROR));
                    case MsalesHttpHeader.AUTHORIZATION:
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.UNAUTHORIZED));
                    case MsalesConstants.PAGE:
                        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.JSON_PAGE_REQUIRED));

                }
            }

        }
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(MsalesStatus.UNKNOW));
    }

    /**
     * Forward to controller
     * <p>
     * @param module module
     * @param action is action in controller
     */
    protected String forwardController(String action) {
        if (action.startsWith("images") || action.startsWith("css") || action.startsWith("js") || action.startsWith("script")) {
            return String.format("forward:/%s", action);
        }
        String module = "";
        int pos = 999999;

        if (action.endsWith("App")) {
            module = MsalesConstants.MODULE.APP.NAME;
        } else {
            for (String md : MsalesConstants.MODULES) {
                int idx = action.toUpperCase().indexOf(md.toUpperCase());
                if (idx != -1) {
                    if (idx < pos && idx != 0) {
                        pos = idx;
                        module = md;
                    } else if (idx == pos) {
                        if (md.length() > module.length()) {
                            pos = idx;
                            module = md;
                        }
                    }
                }
            }
        }

        // request from client have module and action
        if (module.length() > 0) {
            return String.format("forward:/%s/%s", module, action);
        } // reuquest login
        else if (action.equals(MsalesConstants.MODULE.USER.ACTION_LOGIN)) {
            return String.format("forward:/%s/%s", MsalesConstants.MODULE.USER.NAME,
                    MsalesConstants.MODULE.USER.ACTION_LOGIN);
        } // request logout
        else if (action.equals(MsalesConstants.MODULE.USER.ACTION_LOGOUT)) {
            return String.format("forward:/%s/%s", MsalesConstants.MODULE.USER.NAME,
                    MsalesConstants.MODULE.USER.ACTION_LOGOUT);
        } // request error
        else if (action.equals(MsalesConstants.MODULE.CSB.REQUEST_ERROR)) {
            return String.format("forward:/%s", MsalesConstants.MODULE.CSB.REQUEST_ERROR);
        } // session expired
        else if (action.equals(MsalesConstants.MODULE.CSB.SESSION_EXPIRED)) {
            return String.format("forward:/%s", MsalesConstants.MODULE.CSB.SESSION_EXPIRED);
        }// page json objec is required when to get list.
        else if (action.equals(MsalesConstants.MODULE.CSB.PAGE_REQUIRED)) {
            return String.format("forward:/%s", MsalesConstants.MODULE.CSB.PAGE_REQUIRED);
        }// json request from client is invalid
        else if (action.equals(MsalesConstants.MODULE.CSB.JSON_INVALID)) {
            return String.format("forward:/%s", MsalesConstants.MODULE.CSB.JSON_INVALID);
        }// missing fields in json request from client
        else if (action.equals(MsalesConstants.MODULE.CSB.PAGE_FILEDS_REQUEST)) {
            return String.format("forward:/%s", MsalesConstants.MODULE.CSB.PAGE_FILEDS_REQUEST);
        }// request have not permission
        else if (action.equals(MsalesConstants.MODULE.CSB.ACCESS_IS_DENIED)) {
            return String.format("forward:/%s", MsalesConstants.MODULE.CSB.ACCESS_IS_DENIED);
        }// forward to page not found
        else {
            return String.format("forward:/%s", action);
        }
    }

    /**
     * The 404 (Not Found) status code indicates that the origin server did not
     * find a current representation for the target resource or is not willing
     * to disclose that one exists.
     * <p>
     * @see http://tools.ietf.org/html/rfc7231#section-6.5.4
     */
    @RequestMapping(value = MsalesConstants.MODULE.CSB.PAGE_NOT_FOUND)
    private @ResponseBody
    String pageNotFound() {
        return MsalesJsonUtils.getJSONFromOject(MsalesResponse.create(HttpStatus.NOT_FOUND));
    }

    /**
     * Get header request from client. Header include.
     * <p>
     * branch: is database type
     * <p>
     * authorization: is access token, this token server will be response user
     * login is successful.
     */
    protected MsalesHttpHeader getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();
        MsalesHttpHeader httpHeader = new MsalesHttpHeader();
        Enumeration headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
            httpHeader.add(key, value);
        }
        return httpHeader;
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "The session has expired")
    public String handleSessionExpired() {
        System.err.println("sessionExpired");
        return "sessionExpired";
    }

}
