/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.report.controller;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ComparisonOperator;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FontFormatting;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import vn.itt.msales.app.service.AppService;
import vn.itt.msales.app.service.LoginService;
import vn.itt.msales.common.HttpUtil;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.customercare.model.ReportFilter;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.CompanyConfigKpi;
import vn.itt.msales.entity.CompanyConstant;
import vn.itt.msales.entity.CustomerCareInformation;
import vn.itt.msales.entity.MCP;
import vn.itt.msales.entity.SalesOrder;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.export.services.ExportsService;
import vn.itt.msales.report.excelBuilder.excelCreator;
import vn.itt.msales.report.model.DailyOrderReport;
import vn.itt.msales.report.model.KPIReport;
import vn.itt.msales.report.model.ReportKPI;
import vn.itt.msales.report.model.ReportTargetPerMonth;
import vn.itt.msales.report.service.ReportService;
import vn.itt.msales.sales.filter.Filter;
import vn.itt.msales.sales.service.ServiceFilter;
import vn.itt.msales.services.DataService;
import vn.itt.msales.user.controller.MsalesUserController;
import vn.itt.msales.user.model.LoginContext;
import vn.itt.msales.user.model.MsalesLoginUserInf;

/**
 *
 * @author vtm
 */
@Controller
@RequestMapping(value = "/report")
public class MsalesReportController {

    @Autowired
    private AppService appService;
    @Autowired
    private DataService dataService;
    @Autowired
    private ServiceFilter serviceFilter;
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private ExportsService exportsService;
    @Autowired
    private ReportService reportService;

    String separator = File.separator;
    private String filePath = String.format("reports%s", separator);
    private final String FILE_NAME = "RPT006";
    private final String FILE_NAME_KPI = "RPT005";
    private String fullPath = "";

    @RequestMapping(value = "test")
    public ModelAndView reportTest() {
        // get data model which is passed by the Spring container
        //List<Book> listBooks = (List<Book>) model.get("listBooks");

        // tạo một workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        //tạo một sheet
        XSSFSheet sheet = workbook.createSheet("Nhân viên");
        sheet.setDefaultColumnWidth(30);//gan mặc định độ rộng mỗi cột là 30

        //tạo dòng header
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        style.setFillForegroundColor(HSSFColor.BLUE.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);
        style.setFont(font);

        //tạo dòng header
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("USER_NAME");
        header.createCell(2).setCellValue("LAST_NAME");
        header.createCell(3).setCellValue("FIRST_NAME");

        //tạo dữ liệu cho các dòng tiếp theo
        XSSFRow row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue(1);
        row1.createCell(1).setCellValue("nguyenthia");
        row1.createCell(2).setCellValue("Nguyễn Thị");
        row1.createCell(3).setCellValue("A");
        row1.createCell(4).setCellFormula("IF(A2=2,TRUE,FALSE)");

        XSSFRow row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue(2);
        row2.createCell(1).setCellValue("nguyenthib");
        row2.createCell(2).setCellValue("Nguyễn Thị");
        row2.createCell(3).setCellValue("B");
        row2.createCell(4).setCellFormula("IF(A3=2,TRUE,FALSE)");

        XSSFRow row3 = sheet.createRow(3);
        row3.createCell(0).setCellValue(3);
        row3.createCell(1).setCellValue("nguyenthic");
        row3.createCell(2).setCellValue("Nguyễn Thị");
        row3.createCell(3).setCellValue("C");
        row3.createCell(4).setCellFormula("IF(A4=2,TRUE,FALSE)");

        XSSFRow row4 = sheet.createRow(4);
        XSSFCell cell = row4.createCell(4);
        cell.setCellValue("TEST");
        cell.setCellStyle(style);//set stype cho ô
        XSSFRow row5 = sheet.createRow(5);
        row5.setRowStyle(style);//set style cho dong

        //create SheetConditionalFormatting
        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

        //điều kiện 1: ô có giá trị  > 2 => đỏ
        ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingRule(ComparisonOperator.GT, "2");
        PatternFormatting fill1 = rule1.createPatternFormatting();
        fill1.setFillBackgroundColor(IndexedColors.RED.index);
        fill1.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        //Điều kiện 2: ô có giá trị < 2 => xanh lá
        ConditionalFormattingRule rule2 = sheetCF.createConditionalFormattingRule(ComparisonOperator.LT, "2");
        PatternFormatting fill2 = rule2.createPatternFormatting();
        fill2.setFillBackgroundColor(IndexedColors.GREEN.index);
        fill2.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
        //tạo vùng để set điều kiện
        CellRangeAddress[] regions = {
            CellRangeAddress.valueOf("A2:A4")
        };
        sheetCF.addConditionalFormatting(regions, rule1, rule2);

        //điều kiện 3: hiện chữ Xanh với những giá trị bị trùng ở cột LAST_NAME
        ConditionalFormattingRule rule3 = sheetCF.createConditionalFormattingRule("COUNTIF($C$2:$C$4,C2)>1");
        FontFormatting font3 = rule3.createFontFormatting();
        font3.setFontStyle(true, true);//chữ in đậm và nghiêng
        font3.setFontColorIndex(IndexedColors.BLUE.index);
        //tạo vùng set điều kiện
        CellRangeAddress[] regions3 = {
            CellRangeAddress.valueOf("C2:C4")
        };
        sheetCF.addConditionalFormatting(regions3, rule3);

        //điều kiện 4: đổi màu dòng theo chẵn/lẻ
        ConditionalFormattingRule rule4 = sheetCF.createConditionalFormattingRule("MOD(ROW(),2)");
        PatternFormatting fill4 = rule4.createPatternFormatting();
        fill4.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);
        fill4.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
        //tạo vùng để set điều kiện
        CellRangeAddress[] regions4 = {
            CellRangeAddress.valueOf("A1:Z100")
        };

        sheetCF.addConditionalFormatting(regions4, rule4);

        return new ModelAndView("ExcelRevenueSummary", "workbook", workbook);
    }

    public void readFileExcel() {
        //read file excel
        try {
            //tạo liên kết đến file excel
            FileInputStream file = new FileInputStream(new File("test.xlsx"));
            //tạo Workbook from file excel
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            //Get first sheet
            XSSFSheet sheet = workbook.getSheetAt(0);
            //dùng vòng lặp chạy qua các row của sheet và xử ly theo y muốn.
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
                //các hàm xử lý
            }
            file.close();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @RequestMapping(value = "listUser")
    public ModelAndView listUser(HttpServletRequest request) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectModelLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccessModelAndView();
        }

        XSSFWorkbook workbook = excelCreator.createWorkbook();
        XSSFSheet sheet = excelCreator.createSheet("Danh sách nhân viên", workbook);

        //style
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFColor green = new XSSFColor(Color.GREEN);
        style.setFillBackgroundColor(green);

        XSSFRow header = excelCreator.createRow(0, sheet);
        header.setRowStyle(style);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("UserName");
        header.createCell(3).setCellValue("BirthDay");
        header.createCell(4).setCellValue("UserCode");
        header.createCell(5).setCellValue("SEX");
        header.createCell(6).setCellValue("EMAIL");

        //FIXED
        List<User> userList = new ArrayList<>();
        List<Integer> channelIdList = appService.getListChannelId(LoginContext.getLogin(request).getId(), dataService);
        for (int id : channelIdList) {
            appService.getAllListUserByChannel(id, LoginContext.getLogin(request).getCompanyId(), dataService);
        }

        int idx = 1;
        for (User user : userList) {
            XSSFRow row = excelCreator.createRow(idx++, sheet);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getLastName() + " " + user.getFirstName());
            row.createCell(2).setCellValue(user.getUsername());
            row.createCell(3).setCellValue(user.getBirthday());
            row.createCell(4).setCellValue(user.getUserCode());
            row.createCell(5).setCellValue(user.getSex() ? "NAM" : "NỮ");
            row.createCell(6).setCellValue(user.getEmail());
        }

        return new ModelAndView("ExcelRevenueSummary", "workbook", workbook);
    }

    //Report target month
    @RequestMapping(value = "/rpt006/list")
    public String ctkmApproveSalesPointList(Model uiModel, HttpServletRequest request,
            @ModelAttribute("rpt006Form") Filter filter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        //
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;
        filter.setRoles(new Integer[]{6});
        uiModel.addAttribute("roles", filter.getRolesString());
        List<OptionItem> employeeList = filter.processFilter(uiModel, request, serviceFilter, dataService, userInf.getCompanyId());
        uiModel.addAttribute("notShow", true);
        //Search here     

        List<ReportTargetPerMonth> objectList = new ArrayList<>();
        MsalesResults<ReportTargetPerMonth> results = getListReportTargetPerMonth(request, uiModel, userInf, filter, employeeList, page, size);
        objectList.addAll(results.getContentList());

        maxPages = (int) (long) results.getCount();
        if (maxPages % size != 0) {
            maxPages = maxPages / size + 1;
        } else {
            maxPages = maxPages / size;
        }
        //Thêm dữ liệu cho Model
        uiModel.addAttribute("objectList", objectList);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);

        return "rpt006List";
    }

    public MsalesResults<ReportTargetPerMonth> getListReportTargetPerMonth(HttpServletRequest request, Model uiModel,
            MsalesLoginUserInf userInf, Filter filter, List<OptionItem> employeeList, Integer page, Integer size) {
        MsalesResults<ReportTargetPerMonth> results = new MsalesResults<>();
        List<ReportTargetPerMonth> objectList = new ArrayList<>();
        //get month to search
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = HttpUtil.getRequestDateParammeter(request, "startDateString", dateFormat);
        boolean fromDateIsNull = false;
        if (fromDate == null) {
            fromDate = new Date();
            fromDateIsNull = true;
        }
        //String hqlUser
        String hqlUser = "";
        if (filter.getUserId() != null && filter.getUserId() != 0) {
            hqlUser += filter.getUserId() + ",";
        } else {
            if (employeeList.size() > 1) {
                for (int i = 1; i < employeeList.size(); i++) {
                    hqlUser += employeeList.get(i).getId() + ",";
                }
            } else {
                hqlUser += 0 + ",";
            }
        }
        hqlUser += "''";
        SimpleDateFormat simpleDateFormatYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat simpleDateFormatMonth = new SimpleDateFormat("MM");
        String month = simpleDateFormatMonth.format(fromDate);
        Integer months = Integer.parseInt(month);
        String yearOfFromDate = simpleDateFormatYear.format(fromDate);
        // Create hql String
        String hql = reportService.getHqlForUser(months, simpleDateFormatYear.format(fromDate), hqlUser);

        List<HashMap> count = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
        List<HashMap> listUser = dataService.executeSelectHQL(HashMap.class, hql, true, page, size);
        Date date = new Date();
        String monthCurrent = simpleDateFormatMonth.format(date);
        Integer monthsCurrent = Integer.parseInt(monthCurrent);
        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);
        //Set ngay dau thang
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String firstDayOfMonth = simpleDateFormat.format(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        String endDayOfMonth = simpleDateFormat.format(cal.getTime()) + " 23:59:59";
        cal.setTime(date);

        //Xet ngay dau tien cua tuan:
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String firstDayOfWeek = simpleDateFormat.format(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        String endDayOfWeek = simpleDateFormat.format(cal.getTime()) + " 23:59:59";
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        //Ngay cuoi thang hien tai. 
        Date date3 = cal.getTime();
        for (HashMap user : listUser) {
            ReportTargetPerMonth report = new ReportTargetPerMonth();

            report.setUserId((Integer) user.get("id"));
            report.setTenNVBH((String) user.get("name"));

            int userId = (int) user.get("id");

            long salesTargetPerYear = getSumSellPerMonth(simpleDateFormatYear.format(fromDate), null, userId);
            long salesTargetPerMonth = 0;
            long salesTargetPerWeek = 0;
            long salesTargetPerDay = 0;
            //if fromDate is null only get sumSalesPerMonth of month current
            if (fromDateIsNull) {
                salesTargetPerMonth = getSumSellPerMonth(simpleDateFormatYear.format(fromDate), months, userId);
                salesTargetPerWeek = (long) salesTargetPerMonth / 4;
                salesTargetPerDay = (long) salesTargetPerMonth / date3.getDate();
            } else {
                salesTargetPerMonth = getSumSellPerMonth(simpleDateFormatYear.format(fromDate), months, userId);
                long salesPerMonth = getSumSellPerMonth(simpleDateFormatYear.format(date), monthsCurrent, userId);
                salesTargetPerWeek = (long) salesPerMonth / 4;
                salesTargetPerDay = (long) salesPerMonth / date3.getDate();
            }

            report.setDailyRevenueTarget((Long) salesTargetPerDay);
            report.setMonthlyRevenueTarget(salesTargetPerMonth);//(salesTargetPerMonth);
            report.setWeeklyRevenueTarget(salesTargetPerWeek);//(salesTargetPerWeek);
            report.setYearlyRevenueTarget(salesTargetPerYear);//(salesTargetPerYear);

            long salesPerYear = reportService.getSalesOfStaffService(yearOfFromDate + "-01-01", yearOfFromDate + "-12-31 23:59:59", userId);
            long salesPerMonth = reportService.getSalesOfStaffService(firstDayOfMonth, endDayOfMonth, userId);
            long salesPerWeek = reportService.getSalesOfStaffService(firstDayOfWeek, endDayOfWeek, userId);
            long salesPerDay = reportService.getSalesOfStaffService(simpleDateFormat.format(date), simpleDateFormat.format(date) + " 23:59:59", userId);

            report.setDailyRevenue(salesPerDay);//(salesPerDay);
            report.setMonthlyRevenue(salesPerMonth);//(salesPerMonth);
            report.setWeeklyRevenue(salesPerWeek);//(salesPerWeek);
            report.setYearlyRevenue(salesPerYear);//(salesPerYear);
            double percent = 0.00;
            DecimalFormat decimalFormat = new DecimalFormat("###.0000");
            if (salesTargetPerDay != 0) {
            	percent = new Double(decimalFormat.format((double) salesPerDay / salesTargetPerDay));
            	report.setDailyPercentRevenue((float) percent);
            }else{
            	report.setDailyPercentRevenue((float) 0.00);
            }
            if (salesTargetPerWeek != 0) {
            	percent = new Double(decimalFormat.format((double) salesPerWeek / salesTargetPerWeek));
            	report.setWeeklyPercentRevenue((float) percent);
            }else{
            	report.setWeeklyPercentRevenue((float) 0.00);
            }
            if (salesTargetPerMonth != 0) {
            	percent = new Double(decimalFormat.format((double) salesPerMonth / salesTargetPerMonth));
                report.setMonthlyPercentRevenue((float) percent);
            }else{
            	report.setMonthlyPercentRevenue((float) 0.00);
            }
            if (salesTargetPerYear != 0) {
            	percent = new Double(decimalFormat.format((double) salesPerYear / salesTargetPerYear));
                report.setYearlyPercentRevenue((float) percent);
            }else{
            	report.setYearlyPercentRevenue((float) 0.00);
            }

            objectList.add(report);
        }
        results.setCount((long) count.size());
        results.setContentList(objectList);
        return results;
    }

    @RequestMapping(value = "/rpt006/export")
    public void exportReportTargerPerMonth(HttpServletRequest request, HttpServletResponse response,
            Model uiModel, @ModelAttribute("rpt006Form") Filter filter) {
        //Đăng nhập 
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }

        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yyyy");
        Date fromDate = HttpUtil.getRequestDateParammeter(request, "startDateString", dateFormat);
        if (fromDate == null) {
            fromDate = new Date();
        }
        try {
            //get Data
            filter.setRoles(new Integer[]{6});
            uiModel.addAttribute("roles", filter.getRolesString());
            List<OptionItem> employeeList = filter.processFilter(uiModel, request, serviceFilter, dataService, userInf.getCompanyId());

            List<ReportTargetPerMonth> dataList = new ArrayList<>();
            MsalesResults<ReportTargetPerMonth> results = getListReportTargetPerMonth(request, uiModel, userInf, filter, employeeList, 0, 0);
            dataList.addAll(results.getContentList());
            fullPath = servletContext.getRealPath("") + "/" + filePath + FILE_NAME + ".jasper";
            String token = exportsService.generate();
            JRDataSource jrDataSource = null;
            if (dataList.size() > 0) {
                jrDataSource = new JRBeanCollectionDataSource(dataList);
            } else {
                jrDataSource = new JREmptyDataSource();
            }
            ReportFilter rpFilter = new ReportFilter();
            rpFilter.setTemplate(fullPath);
            rpFilter.setExportType("xls");
            rpFilter.setFileName(FILE_NAME);
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("tenTinh", "");
            parameters.put("tenASM", "");
            parameters.put("thang_nam", simpleDateFormat.format(fromDate));

            exportsService.download(rpFilter, parameters, token, jrDataSource, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getSumSellPerMonth(String year, Integer month, int userId) {
        long salesTarget = reportService.getSumSellPerMonthService(year, month, userId);
        return salesTarget;
    }

    @RequestMapping("/rpt005/export")
    public void exportKPI(HttpServletRequest request, HttpServletResponse response, Model uiModel, @ModelAttribute("rpt005Form") Filter filter) {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }
        // get khu vực
        String KhuVuc = "";
        String GiamSat = "";
        if (filter.getChannelIdList().get(1) != 0) {
            Channel channelKV = dataService.getRowById(filter.getChannelIdList().get(1), Channel.class);

            KhuVuc = channelKV.getName();
        }
        if (filter.getChannelIdList().get(2) != 0) {
            Channel channelGS = dataService.getRowById(filter.getChannelIdList().get(2), Channel.class);
            GiamSat = channelGS.getName();
        }
        //
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        ParameterList parameterList = new ParameterList("companyId", userInf.getCompanyId());
        List< CompanyConfigKpi> companyConfigKpi = dataService.getListOption(CompanyConfigKpi.class, parameterList);
        CompanyConfigKpi companyConfigKpi1 = new CompanyConfigKpi();
        for (CompanyConfigKpi companyConfigKpi2 : companyConfigKpi) {
            companyConfigKpi1 = companyConfigKpi2;
        }
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yyyy");
        Date fromDate = HttpUtil.getRequestDateParammeter(request, "startDateString", dateFormat);
        if (fromDate == null) {
            fromDate = new Date();
        }
        try {
            String token = exportsService.generate();
            filter.setRoles(new Integer[]{6});
            List<OptionItem> employeeList = filter.processFilter(uiModel, request, serviceFilter, dataService, userInf.getCompanyId());

            MsalesResults<ReportKPI> msalesResults = getListReport(request, uiModel, userInf, filter, employeeList, 0, 0);
//            List<ReportKPI> dataList = new ArrayList<>();
//            dataList.addAll(msalesResults.getContentList());

            List<KPIReport> dataList = new ArrayList<>();
            for (ReportKPI reportKPI : msalesResults.getContentList()) {
                KPIReport kpir = new KPIReport();

                kpir.setTenNVBH(reportKPI.getNameUser());
                kpir.setPercentTarget(reportKPI.getPercentRevenue() / 100);
                kpir.setPercentSalePoint(reportKPI.getPercentRoute() / 100);
                kpir.setSoLuongChiTieu(reportKPI.getTargetRevenue());

                kpir.setSoLuongBan(reportKPI.getRevenue());
                kpir.setTargetPointTotal((long) reportKPI.getTargetPosCare());
                kpir.setCarePointTotal((long) reportKPI.getPosCare());
                kpir.setSoldPointTotal((long) reportKPI.getPosSales());

                kpir.setTotalHours(reportKPI.getTotalHour());
                kpir.setAsmComment(reportKPI.getComment());

                if (reportKPI.getSupervisorComment().equalsIgnoreCase(companyConfigKpi1.getMaxCorlor())) {
                    kpir.setPerformanceStatus(1L);
                } else if (reportKPI.getSupervisorComment().equalsIgnoreCase(companyConfigKpi1.getMediumCorlor())) {
                    kpir.setPerformanceStatus(2L);
                } else {
                    kpir.setPerformanceStatus(3L);
                }

                dataList.add(kpir);

            }

            JRDataSource jrDataSource = null;
            if (dataList.size() > 0) {
                jrDataSource = new JRBeanCollectionDataSource(dataList);
            } else {
                jrDataSource = new JREmptyDataSource();
            }
            ReportFilter reportFilter = new ReportFilter();
            fullPath = servletContext.getRealPath("") + "/" + filePath + FILE_NAME_KPI + ".jasper";
            reportFilter.setTemplate(fullPath);
            reportFilter.setExportType("xls");
            reportFilter.setFileName(FILE_NAME_KPI);
            Map<String, Object> parameters = new HashMap<String, Object>();

            String tenKhuVuc = "Khu vực nam sài gòn";

            parameters.put("tenKhuVuc", KhuVuc);

            String tenTinhThanh = "Hồ Chí Minh";
            parameters.put("tenTinhThanh", "");

            String superVisorName = "Trần Văn Quang";
            parameters.put("tenGiamSat", GiamSat);

            String ngay = (new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            String thang_nam = simpleDateFormat.format(fromDate);
            parameters.put("ngay", ngay);
            parameters.put("thang_nam", thang_nam);
            exportsService.download(reportFilter, parameters, token, jrDataSource, response);
            //downloadService.download(filter, token, jrDataSource, response);
        } catch (Exception e) {
            System.err.println("Method export exception: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/rpt005/list")
    public String reportListKPI(Model uiModel, HttpServletRequest request,
            @ModelAttribute("rpt005Form") Filter filter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        int login = LoginContext.isLogin(request, dataService);
        if (login == -1) {
            return LoginContext.redirectLogin();
        } else if (login == 0 || login == 2) {
            return LoginContext.notAccess();
        }
        //
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int maxPages = 1;
        
        filter.setRoles(new Integer[]{6});
        List<OptionItem> employeeList = filter.processFilter(uiModel, request, serviceFilter, dataService, userInf.getCompanyId());
        uiModel.addAttribute("notShow", true);
        MsalesResults<ReportKPI> msalesResults = getListReport(request, uiModel, userInf, filter, employeeList, page, size);
        int count = Integer.parseInt(msalesResults.getCount().toString());
        if (count % size != 0) {
            maxPages = count / size + 1;
        } else {
            maxPages = count / size;
        }

        uiModel.addAttribute("user", msalesResults.getContentList());
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("maxPages", maxPages);

        return "rpt005List";
    }

    public MsalesResults<ReportKPI> getListReport(HttpServletRequest request, Model uiModel,
            MsalesLoginUserInf userInf, Filter filter, List<OptionItem> employeeList, Integer page, Integer size) {
        //get company_config_kpi
        ParameterList parameterList = new ParameterList("companyId", userInf.getCompanyId());
        List< CompanyConfigKpi> companyConfigKpi = dataService.getListOption(CompanyConfigKpi.class, parameterList);
        CompanyConfigKpi companyConfigKpi1 = new CompanyConfigKpi();
        for (CompanyConfigKpi companyConfigKpi2 : companyConfigKpi) {
            companyConfigKpi1 = companyConfigKpi2;
        }
        //get company_contants 
        List<CompanyConstant> companyConstants = dataService.getListOption(CompanyConstant.class, new ParameterList("companys.id", userInf.getCompanyId()));
        CompanyConstant companyConstant = new CompanyConstant();
        for (CompanyConstant companyConstant1 : companyConstants) {
            companyConstant = companyConstant1;
        }
        //get userList
        List<User> userList = new ArrayList<>();
        if (filter.getUserId() != null) {
            int userId = filter.getUserId();
            if (userId != 0) {
                User user = dataService.getRowById(filter.getUserId(), User.class);
                if (user != null) {
                    userList.add(user);
                }
            } else {
                for (OptionItem optionItem : employeeList) {
                    User user = dataService.getRowById(optionItem.getId(), User.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
            }
        } else {
            for (OptionItem optionItem : employeeList) {
                User user = dataService.getRowById(optionItem.getId(), User.class);
                if (user != null) {
                    userList.add(user);
                }
            }
        }
        //compayId
        int companyId = userInf.getCompanyId();
        // get  month ,get year
        String fomatDate = "dd/MM/yyyy";
        SimpleDateFormat simpleFormatMonth = new SimpleDateFormat("MM");
        SimpleDateFormat simpleFormatYear = new SimpleDateFormat("yyyy");
        Date date = HttpUtil.getRequestDateParammeter(request, "startDateString", fomatDate);
        if (date == null) {
            date = new Date();
        }
        Integer month = Integer.parseInt(simpleFormatMonth.format(date));
        Integer year = Integer.parseInt(simpleFormatYear.format(date));

        LinkedHashMap linkedHashMap = appService.getSupKPIApp(userList, companyId, companyConfigKpi1, companyConstant, month, year, dataService);

        List<HashMap> arrayList = (List<HashMap>) linkedHashMap.get("userList");
        MsalesResults<ReportKPI> listReport = new MsalesResults<>();
        List<ReportKPI> objectList = new ArrayList<>();
        for (HashMap hashMap : arrayList) {
            ReportKPI reportKPI = new ReportKPI();
            reportKPI.setNameUser(hashMap.get("name").toString());
            reportKPI.setPercentRevenue((float) Double.parseDouble(hashMap.get("soldPercent").toString()));
            reportKPI.setPercentRoute((float) Double.parseDouble(hashMap.get("carePercent").toString()));
            reportKPI.setTargetRevenue((long) Double.parseDouble(hashMap.get("targetSold").toString()));
            reportKPI.setRevenue(Long.parseLong(hashMap.get("totalSold").toString()));
            reportKPI.setTargetPosCare((int) Long.parseLong(hashMap.get("targetCare").toString()));
            reportKPI.setPosCare((int) Double.parseDouble(hashMap.get("totalCared").toString()));
            reportKPI.setPosSales(Integer.parseInt(hashMap.get("posSold").toString()));
            reportKPI.setTotalHour(hashMap.get("workTime").toString());
            reportKPI.setSupervisorComment(hashMap.get("color").toString());
            reportKPI.setPointNV(Long.parseLong(hashMap.get("carePoint").toString()));
            reportKPI.setComment(hashMap.get("note").toString());

            objectList.add(reportKPI);
        }
        MsalesResults<ReportKPI> msalesResults = new MsalesResults<>();
        msalesResults.setContentList(objectList);
        msalesResults.setCount((long) objectList.size());
        return msalesResults;

    }

    @RequestMapping(value = "/detailsSalesTrans")
    public void exportReportDetailSalesTrans(HttpServletRequest request, HttpServletResponse response,
            Model uiModel, @ModelAttribute("salesTransListForm") Filter filter) {
        //Đăng nhập 
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        List<OptionItem> employeeList = filter.processFilter(uiModel, request, serviceFilter, dataService, userInf.getCompanyId());

        try {
            //Get List here
            String hql = "SELECT S.id as id, 1 as isSalesTrans, S.salesTranss.toStocks.poss.id as posId,"
                    + " S.salesTranss.createdAt as salesTransDate, S.salesTranss.toStocks.poss.name as posName,"
                    + " S.salesTranss.toStocks.poss.address as address, S.salesTranss.toStocks.poss.lat as lat,"
                    + " S.salesTranss.toStocks.poss.lng as lng, S.salesTranss.toStocks.poss.posCode as posCode,"
                    + " CASE WHEN S.salesTranss.toStocks.poss.locations.locationType = 1 "
                    + "	THEN S.salesTranss.toStocks.poss.locations.name "
                    + " WHEN S.salesTranss.toStocks.poss.locations.locationType = 2 "
                    + " THEN S.salesTranss.toStocks.poss.locations.parents.name "
                    + " ELSE S.salesTranss.toStocks.poss.locations.parents.parents.name END as tinhThanh,"
                    + " S.goodss.name as goodsName, S.quantity as quantity, S.price as price,"
                    + " S.salesTranss.toStocks.poss.frequency as frequency, "
                    + " URC.users.lastName as lastName, URC.users.firstName as firstName,"
                    + " Channel as channel"
                    + " FROM SalesTransDetails as S, UserRoleChannel as URC"
                    + " JOIN URC.channels as Channel"
                    + " WHERE S.deletedUser = 0 and URC.deletedUser = 0 AND S.salesTranss.deletedUser = 0"
                    + " AND S.createdUser = URC.users.id AND S.salesTranss.transType = 2 and Channel.deletedUser = 0 "
                    + " AND Channel.id = URC.channels.id AND URC.userRoles.id = 6"
                    + " AND S.salesTranss.companys.id = " + userInf.getCompanyId();

            String hqlSalesSup = "SELECT S.id as id, 1 as isSalesTrans, S.salesTranss.toStocks.poss.id as posId,"
                    + " S.salesTranss.createdAt as salesTransDate, S.salesTranss.toStocks.poss.name as posName,"
                    + " S.salesTranss.toStocks.poss.address as address, S.salesTranss.toStocks.poss.lat as lat,"
                    + " S.salesTranss.toStocks.poss.lng as lng, S.salesTranss.toStocks.poss.posCode as posCode,"
                    + " CASE WHEN S.salesTranss.toStocks.poss.locations.locationType = 1 "
                    + "	THEN S.salesTranss.toStocks.poss.locations.name "
                    + " WHEN S.salesTranss.toStocks.poss.locations.locationType = 2 "
                    + " THEN S.salesTranss.toStocks.poss.locations.parents.name "
                    + " ELSE S.salesTranss.toStocks.poss.locations.parents.parents.name END as tinhThanh,"
                    + " S.goodss.name as goodsName, S.quantity as quantity, S.price as price,"
                    + " S.salesTranss.toStocks.poss.frequency as frequency, "
                    + " URC.users.lastName as lastName, URC.users.firstName as firstName,"
                    + " Channel as channel"
                    + " FROM SalesTransDetails as S , MCPDetails as M, UserRoleChannel as URC "
                    + " JOIN URC.channels as Channel"
                    + " WHERE S.deletedUser = 0 and URC.deletedUser = 0 AND S.salesTranss.deletedUser = 0"
                    + " AND M.deletedUser = 0 and M.mcps.deletedUser = 0"
                    + " AND S.salesTranss.toStocks.poss.id = M.poss.id"
                    + " AND WEEK(S.salesTranss.createdAt) = WEEK(M.mcps.beginDate)"
                    + " AND YEAR(S.salesTranss.createdAt) = YEAR(M.mcps.beginDate)"
                    + " AND M.mcps.implementEmployees.id = URC.users.id "
                    + " AND S.salesTranss.transType = 2 and Channel.deletedUser = 0 "
                    + " AND Channel.id = URC.channels.id "
                    + " AND S.salesTranss.companys.id = " + userInf.getCompanyId();

            String hqlSalesOrder = "SELECT S.id as id, 0 as isSalesTrans, S.orders.pos.id as posId, S.orders.note as note, "
                    + " S.orders.createdAt as salesTransDate, S.orders.pos.name as posName,"
                    + " S.orders.pos.address as address, S.orders.pos.lat as lat,"
                    + " S.orders.pos.lng as lng, S.orders.pos.posCode as posCode,"
                    + " CASE WHEN S.orders.pos.locations.locationType = 1 "
                    + "	THEN S.orders.pos.locations.name "
                    + " WHEN S.orders.pos.locations.locationType = 2 "
                    + " THEN S.orders.pos.locations.parents.name "
                    + " ELSE S.orders.pos.locations.parents.parents.name END as tinhThanh,"
                    + " S.goodss.name as goodsName, S.quantity as quantity, S.price as price,"
                    + " S.orders.pos.frequency as frequency, "
                    + " URC.users.lastName as lastName, URC.users.firstName as firstName,"
                    + " Channel as channel"
                    + " FROM SalesOrderDetails as S , MCPDetails as M, UserRoleChannel as URC "
                    + " JOIN URC.channels as Channel"
                    + " WHERE S.deletedUser = 0 and URC.deletedUser = 0 AND S.orders.deletedUser = 0"
                    + " AND M.deletedUser = 0 and M.mcps.deletedUser = 0"
                    + " AND S.orders.pos.id = M.poss.id"
                    + " AND WEEK(S.orders.createdAt) = WEEK(M.mcps.beginDate)"
                    + " AND YEAR(S.orders.createdAt) = YEAR(M.mcps.beginDate)"
                    + " AND S.orders.statuss.id not in (14,13,12)"
                    + " AND M.mcps.implementEmployees.id = URC.users.id "
                    + " AND Channel.deletedUser = 0 "
                    + " AND Channel.id = URC.channels.id AND URC.userRoles.id = 6 "// AND URC.userRoles.id = 6
                    + " AND S.orders.companys.id = " + userInf.getCompanyId();
            //Search here
            if (filter.getUserId() != null && filter.getUserId() != 0) {
                hql += " AND S.salesTranss.createdUser = " + filter.getUserId();
                hqlSalesSup += " AND M.mcps.implementEmployees.id = " + filter.getUserId();
                hqlSalesOrder += " AND M.mcps.implementEmployees.id = " + filter.getUserId();
            } else {
                String string = "";
                for (OptionItem optionItem : employeeList) {
                    string += optionItem.getId() + ",";
                }
                string += "''";
                hql += " AND S.salesTranss.createdUser IN (" + string + ") ";
                hqlSalesSup += " AND M.mcps.implementEmployees.id IN (" + string + ") ";
                hqlSalesOrder += " AND M.mcps.implementEmployees.id IN (" + string + ") ";
            }

            //Not createdUser here
            String hqlGetListSalesman = "SELECT URC.users.id as userId FROM UserRoleChannel as URC"
                    + " WHERE deletedUser = 0 and userRoles.id = 6"
                    + " and users.companys.id = " + userInf.getCompanyId();
            List<HashMap> listUserId = dataService.executeSelectHQL(HashMap.class, hqlGetListSalesman, true, 0, 0);
            if (!listUserId.isEmpty()) {
                String string = "";
                for (HashMap hm : listUserId) {
                    string += hm.get("userId") + ",";
                }
                string += "''";
                hqlSalesSup += " AND S.salesTranss.createdUser NOT IN (" + string + ") ";
            }

            //Search theo fromDate, toDate;
            String fromDate = filter.getStartDateString();
            String toDate = filter.getEndDateString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fDate = new Date();
            if (fromDate != null && !fromDate.isEmpty() && !fromDate.trim().isEmpty()) {
                try {
                    fDate = simpleDateFormat.parse(fromDate);
                    hql += " AND S.salesTranss.createdAt >= '" + dateFormat.format(fDate) + "'";
                    hqlSalesSup += " AND S.salesTranss.createdAt >= '" + dateFormat.format(fDate) + "'";
                    hqlSalesOrder += " AND S.orders.createdAt >= '" + dateFormat.format(fDate) + "'";
                } catch (Exception ex) {
                }
            }

            if (toDate != null && !toDate.isEmpty() && !toDate.trim().isEmpty()) {
                try {
                    fDate = simpleDateFormat.parse(toDate);
                    hql += " AND S.salesTranss.createdAt <= '" + dateFormat.format(fDate) + " 23:59:59'";
                    hqlSalesSup += " AND S.salesTranss.createdAt <= '" + dateFormat.format(fDate) + " 23:59:59'";
                    hqlSalesOrder += " AND S.orders.createdAt <= '" + dateFormat.format(fDate) + " 23:59:59'";
                } catch (Exception ex) {
                }
            }
            String searchText = filter.getSearchText();
            if (searchText != null && !searchText.isEmpty() && !searchText.trim().isEmpty()) {
                searchText = searchText.replace(" ", "%");
            }

            hql += " GROUP BY S.id ORDER BY S.id DESC";
            hqlSalesSup += " GROUP BY S.id ORDER BY S.id DESC";
            hqlSalesOrder += " GROUP BY S.id ORDER BY S.id DESC";
            List<HashMap> listReportDetail = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            List<HashMap> listSalesSup = dataService.executeSelectHQL(HashMap.class, hqlSalesSup, true, 0, 0);
            if (!listSalesSup.isEmpty()) {
                listReportDetail.addAll(listSalesSup);
            }
            //Get follow SalesOrder
            List<HashMap> listSalesOrder = dataService.executeSelectHQL(HashMap.class, hqlSalesOrder, true, 0, 0);

            if (!listSalesOrder.isEmpty()) {
                listReportDetail.addAll(listSalesOrder);
            }

            Runtime rt = Runtime.getRuntime();
            long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;

            String temp = String.format("%sdownloads%spnReportTemplate_new.xlsx", File.separator, File.separator);
            File f = new File(servletContext.getRealPath("") + temp);
            ServletOutputStream os = null;
            FileInputStream fPath = null;
            try {
    			// Set our response properties
                // Here you can declare a custom filename
                String fileName = "Details Sales Report.xlsx";
                response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
                // Set content type
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                Cookie cookie = new Cookie("downloaded", "true");
                cookie.setPath("/");
                response.addCookie(cookie);
                fPath = new FileInputStream(f);

                OPCPackage pkg = OPCPackage.open(new FileInputStream(f.getAbsolutePath()));
                XSSFWorkbook xssfwb = new XSSFWorkbook(pkg);
                Workbook wb = new SXSSFWorkbook(xssfwb, 1000);
                Sheet posSheet = wb.getSheetAt(0);

                int i = 1;
                for (HashMap hm : listReportDetail) {
                    Row row = posSheet.createRow(i++);
                    int col = 0;
                    //Ngay giao dich
                    createCell(simpleDateFormat.format((Date) hm.get("salesTransDate")), row, col++);
                    createCell((String) hm.get("posName"), row, col++);
                    createCell((String) hm.get("address"), row, col++);
                    BigDecimal lat = new BigDecimal(0);
                    BigDecimal lng = new BigDecimal(0);
                    if (hm.get("lng") != null) {
                        lng = (BigDecimal) hm.get("lng");
                    }
                    if (hm.get("lat") != null) {
                        lat = (BigDecimal) hm.get("lat");
                    }
                    createCell("(" + lng + ":" + lat + ")", row, col++);
                    createCell((String) hm.get("posCode"), row, col++);
                    createCell((String) hm.get("tinhThanh"), row, col++);
                    //Check ban hang truc tiep o day
                    int isSalesTrans = (Integer) hm.get("isSalesTrans");
                    if (isSalesTrans == 1) {
                        createCell("Bán hàng trực tiếp", row, col++);
                    } else {
                        if (hm.get("note") != null && hm.get("note").equals("MOBILE")) {
                            createCell("Đặt hàng qua điện thoại", row, col++);
                        } else {
                            createCell("Đặt hàng", row, col++);
                        }
                    }

                    createCell((String) hm.get("goodsName"), row, col++);
                    createCell((long) (Integer) hm.get("quantity") * (Integer) hm.get("price"), row, col++);
//    				createCell(item.getQuantityBelongBarrel(), row, col ++);
                    createCell((Integer) hm.get("quantity"), row, col++);
                    Integer frequency = null;
                    if (hm.get("frequency") != null) {
                        frequency = (Integer) hm.get("frequency");
                    }
                    createCell(frequency, row, col++);
                    createCell((Integer) hm.get("price"), row, col++);
                    //Check channel here
                    Channel channel = (Channel) hm.get("channel");
                    String region = "";
                    String asm = "";
                    String giamsat = "";
                    String npp = "";
                    String fullCode = channel.getFullCode();
                    String[] arrayString = fullCode.split("_");
                    int sizeArrayString = arrayString.length;
                    if (sizeArrayString == 1) {
                        region = channel.getName();
                    } else if (sizeArrayString == 2) {
                        asm = channel.getName();
                        region = channel.getParents().getName();
                    } else if (sizeArrayString == 3) {
                        giamsat = channel.getName();
                        asm = channel.getParents().getName();
                        region = channel.getParents().getParents().getName();
                    } else if (sizeArrayString == 4) {
                        npp = channel.getName();
                        giamsat = channel.getParents().getName();
                        asm = channel.getParents().getParents().getName();
                        region = channel.getParents().getParents().getParents().getName();
                    } else if (sizeArrayString == 5) {
                        npp = channel.getParents().getName();
                        giamsat = channel.getParents().getParents().getName();
                        asm = channel.getParents().getParents().getParents().getName();
                        region = channel.getParents().getParents().getParents().getParents().getName();
                    } else if (sizeArrayString == 6) {
                        npp = channel.getParents().getParents().getName();
                        giamsat = channel.getParents().getParents().getParents().getName();
                        asm = channel.getParents().getParents().getParents().getParents().getName();
                        region = channel.getParents().getParents().getParents().getParents().getParents().getName();
                    }

                    createCell(region, row, col++);
                    createCell(asm, row, col++);
                    createCell(giamsat, row, col++);
                    createCell((String) hm.get("lastName") + " " + (String) hm.get("firstName"), row, col++);

                    createCell(npp, row, col++);

//    				createCell(item.getBarrelQuantity(), row, col ++);
//    				createCell(item.getUnit(), row, col ++);
//    				createCell(item.getQuantity(), row, col ++);
                    if (i % 100 == 0) {
                        ((SXSSFSheet) posSheet).flushRows(1000);
                    }
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    wb.write(bos);
                } finally {
                    bos.close();
                }
                byte[] bytes = bos.toByteArray();
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                response.setContentLength(bytes.length);
                rt = Runtime.getRuntime();
                usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
                System.out.println("memory usage" + usedMB);

                os = response.getOutputStream();
                try {
                    int buffSize = 4096;
                    byte[] buffer = new byte[buffSize];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                        os.flush();
                        response.flushBuffer();
                    }
                } finally {
                    os.flush();
                    os.close();
                    in.close();
                }
//    			
                rt = Runtime.getRuntime();
                usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
                System.out.println("memory usage" + usedMB);

            } catch (Exception e) {
                //new FunctionException(getClass(), e);
            } finally {
                if (null != os) {
                    os.close();
                }
                if (null != fPath) {
                    fPath.close();
                }
                System.gc();
            }
            //downloadService.download(filter, token, jrDataSource, response);
        } catch (Exception e) {
            System.err.println("Method export exception: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/exportDaily", method = RequestMethod.POST)
    protected void exportDaily(Model uiModel, @ModelAttribute("salesTransListForm") Filter filter,
            HttpServletResponse response, HttpServletRequest request) throws Exception {
        int login = LoginContext.isLogin(request, dataService);
        if (login != 1) {
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        MsalesLoginUserInf userInf = LoginContext.getLogin(request);
        filter.setRoles(new Integer[]{6});
        uiModel.addAttribute("roles", filter.getRolesString());
        List<OptionItem> employeeList = filter.processFilter(uiModel, request, serviceFilter, dataService, userInf.getCompanyId());
         //Search here
        //Get Data for here
        List<Integer> listUserId = new ArrayList<>();
        if (filter.getUserId() != null && filter.getUserId() > 0) {
            listUserId.add(filter.getUserId());
        } else {
            int sizeEmployeeList = employeeList.size();
            for (int i = 1; i < sizeEmployeeList; i++) {
                listUserId.add(employeeList.get(i).getId());
            }
        }
        String fromDate = filter.getStartDateString();
        Date date = new Date();
        if (fromDate != null && !fromDate.isEmpty() && !fromDate.trim().isEmpty()) {
            try {
                date = simpleDateFormat.parse(fromDate);
            } catch (Exception ex) {
            }
        }
        fromDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

        List<DailyOrderReport> listOrderDailys = new ArrayList<>();

        for (Integer userId : listUserId) {
            // Step 1 lay data for 1 salesMan;
            DailyOrderReport dailyOrder = new DailyOrderReport();
            // Get from ID to npp
            dailyOrder.setUserId(userId);
            String hqlUser = "SELECT URC.users.username as username, URC.users.lastName as lastName,"
                    + " URC.users.firstName as firstName, Channel as channel"
                    + " FROM UserRoleChannel as URC JOIN URC.channels as Channel" + " WHERE URC.users.id = " + userId
                    + " AND URC.deletedUser = 0 AND Channel.deletedUser = 0";
            List<HashMap> users = dataService.executeSelectHQL(HashMap.class, hqlUser, true, 1, 1);
            for (HashMap hm : users) {
                dailyOrder.setUsername((String) hm.get("username"));
                dailyOrder.setName((String) hm.get("lastName") + " " + (String) hm.get("firstName"));
                Channel channel = (Channel) hm.get("channel");
                int channelId = 0;
                String region = "", asm = "", giamsat = "", npp = "", fullCode = channel.getFullCode();
                String[] arrayString = fullCode.split("_");
                int sizeArrayString = arrayString.length;
                if (sizeArrayString == 1) {
                    region = channel.getName();
                } else if (sizeArrayString == 2) {
                    asm = channel.getName();
                    region = channel.getParents().getName();
                } else if (sizeArrayString == 3) {
                    giamsat = channel.getName();
                    channelId = channel.getId();
                    asm = channel.getParents().getName();
                    region = channel.getParents().getParents().getName();
                } else if (sizeArrayString == 4) {
                    npp = channel.getName();
                    giamsat = channel.getParents().getName();
                    // Xet giam sat o day
                    channelId = channel.getParents().getId();
                    asm = channel.getParents().getParents().getName();
                    region = channel.getParents().getParents().getParents().getName();
                } else if (sizeArrayString == 5) {
                    npp = channel.getParents().getName();
                    giamsat = channel.getParents().getParents().getName();
                    channelId = channel.getParents().getParents().getId();
                    asm = channel.getParents().getParents().getParents().getName();
                    region = channel.getParents().getParents().getParents().getParents().getName();
                } else if (sizeArrayString == 6) {
                    npp = channel.getParents().getParents().getName();
                    giamsat = channel.getParents().getParents().getParents().getName();
                    channelId = channel.getParents().getParents().getParents().getId();
                    asm = channel.getParents().getParents().getParents().getParents().getName();
                    region = channel.getParents().getParents().getParents().getParents().getParents().getName();
                }
                String hqlChannel = "SELECT URC.users.lastName as lastName, URC.users.firstName as firstName"
                        + " FROM UserRoleChannel as URC WHERE URC.deletedUser = 0"
                        + " AND URC.userRoles.id = 4 and URC.channels.id = " + channelId;
                List<HashMap> listGiamsat = dataService.executeSelectHQL(HashMap.class, hqlChannel, true, 1, 1);
                for (HashMap hms : listGiamsat) {
                    giamsat = hms.get("lastName") + " " + hms.get("firstName");
                }
                dailyOrder.setSalesSup(giamsat);
                dailyOrder.setAsm(asm);
                dailyOrder.setNpp(npp);
                dailyOrder.setRegion(region);
            }

            // Step 2 get so diem dc cham soc
            String hql = "SELECT MCPDetails.id as mcpDetailsId, CustomerCareInformation.id as ccId, CustomerCareInformation.poss.id as posId"
                    + " FROM CustomerCareInformation as CustomerCareInformation"
                    + " RIGHT JOIN CustomerCareInformation.mcpDetailss as MCPDetails"
                    + " WHERE MCPDetails.mcps.implementEmployees.id = " + userId
                    + " AND DATE(MCPDetails.mcps.beginDate) = '" + fromDate + "'" //date
                    + " AND MCPDetails.mcps.deletedUser = 0"
                    + " AND MCPDetails.deletedUser = 0"
                    + " AND (CustomerCareInformation IS NULL OR CustomerCareInformation.deletedUser = 0)";
            List<HashMap> listMCP = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
            dailyOrder.setTotalPos(listMCP.size());
            int totalPosIsCare = 0;
            String poss = "";
            for (HashMap hm : listMCP) {
                if (hm.get("ccId") != null) {
                    totalPosIsCare++;
                    poss += hm.get("posId") + ",";
                }
            }
            poss += "''";
            dailyOrder.setTotalPosIsCare(totalPosIsCare);
            // Get số điểm dc bán lẻ
            String hqlIsSales = "SELECT S.toStocks.poss.id as posId FROM SalesTrans as S"
                    + " WHERE S.deletedUser = 0 AND DATE(S.salesTransDate) = '" + fromDate + "'"
                    + " AND S.toStocks.poss.id IN (" + poss + ") GROUP BY S.toStocks.poss.id";
            List<HashMap> listPosIsSales = dataService.executeSelectHQL(HashMap.class, hqlIsSales, true, 0, 0);
            dailyOrder.setTotalPosIsSales(listPosIsSales.size());

            //Get List SalesOrder for userId
            String hqlSalesOrder = "SELECT S.id as id, S.statuss.id as statusId, S.note as note"
                    + " FROM SalesOrder as S,MCPDetails as M WHERE S.deletedUser = 0"
                    + " AND M.deletedUser = 0 AND M.mcps.deletedUser = 0" + " AND S.pos.id = M.poss.id"
                    + " AND S.statuss.id not in (13,14,12)"
                    + " AND WEEKOFYEAR(S.createdAt) = WEEKOFYEAR(M.mcps.beginDate)"
                    + " AND YEAR(S.createdAt) = YEAR(M.mcps.beginDate)" + " AND S.companys.id = " + userInf.getCompanyId()
                    + " AND M.mcps.implementEmployees.id = " + userId
                    + " AND DATE(S.createdAt)='" + fromDate + "'";
            hqlSalesOrder += " GROUP BY S.id";
            List<HashMap> listSalesOrder = dataService.executeSelectHQL(HashMap.class, hqlSalesOrder, true, 0, 0);
            int soDHThuong = 0, soDHDienThoai = 0, soDHThuongIsGiao = 0, soDHDienThoaiIsGiao = 0;
            String dhThuong = "", dhDienThoai = "";
            for (HashMap hm : listSalesOrder) {
                String note = "";
                if (hm.get("note") != null) {
                    note = (String) hm.get("note");
                }
                int statusId = (Integer) hm.get("statusId");
                if (note.equals("MOBILE")) {
                    // Is dhDienThoai
                    if (statusId == 18 || statusId == 19 || statusId == 20) {
                        soDHDienThoaiIsGiao++;
                    }
                    if (statusId != 14) {
                        soDHDienThoai++;
                        dhDienThoai += hm.get("id") + ",";
                    }

                } else {
                    if (statusId == 18 || statusId == 19 || statusId == 20) {
                        soDHThuongIsGiao++;
                    }
                    if (statusId != 14) {
                        soDHThuong++;
                        dhThuong += hm.get("id") + ",";
                    }
                }
            }
            dhDienThoai += "''";
            dhThuong += "''";
            dailyOrder.setDhDienThoai(soDHDienThoai);
            dailyOrder.setDhDienThoaiIsGiao(soDHDienThoaiIsGiao);
            dailyOrder.setDonHangThuong(soDHThuong);
            dailyOrder.setDonHangThuongIsGiao(soDHThuongIsGiao);

            // Step get doanh so don hang thuong va don hang dien thoai.
            String hqlDoanhSoDh = "SELECT SUM(S.quantity*S.price) as doanhSo FROM SalesOrderDetails as S "
                    + " WHERE S.deletedUser = 0";
            dhThuong = hqlDoanhSoDh + " AND S.orders.id IN (" + dhThuong + ")";
            dhDienThoai = hqlDoanhSoDh + " AND S.orders.id IN (" + dhDienThoai + ")";
            List<HashMap> doanhSoDHT = dataService.executeSelectHQL(HashMap.class, dhThuong, true, 0, 0);
            List<HashMap> doanhSoDHDT = dataService.executeSelectHQL(HashMap.class, dhDienThoai, true, 0, 0);
            long dsDHT = 0;
            if (doanhSoDHT.get(0).get("doanhSo") != null) {
                dsDHT = (long) (Long) doanhSoDHT.get(0).get("doanhSo");
            }
            long dsDHDT = 0;
            if (doanhSoDHDT.get(0).get("doanhSo") != null) {
                dsDHDT = (long) (Long) doanhSoDHDT.get(0).get("doanhSo");
            }
            dailyOrder.setDoanhSoDH(dsDHT);
            dailyOrder.setDoanhSoDHDienThoai(dsDHDT);

            // Phat trien diem moi
            String hqlDiemMoi = "SELECT POS.id as id FROM POS as POS WHERE deletedUser = 0 and createdUser = " + userId// createUser																													// here
                    + " and Date(createdAt) = '" + fromDate + "'";
            List<HashMap> listPOS = dataService.executeSelectHQL(HashMap.class, hqlDiemMoi, true, 0, 0);
            int soDiemMoi = 0;
            if (!listPOS.isEmpty()) {
                soDiemMoi = listPOS.size();
            }
            dailyOrder.setSoDiemMoi(soDiemMoi);

            // Set so luong hang nhan, so luong hang ban, doanh so
            boolean isNhanHang = false;
            int soHangNhan = 0, soHangBan = 0;
            long doanhSo = 0;
            String hqlSalesTrans = "SELECT S.quantity as quantity, S.price as price, S.salesTranss.transType as transType"
                    + " FROM SalesTransDetails as S WHERE S.deletedUser = 0 AND S.salesTranss.deletedUser = 0"
                    + " AND S.salesTranss.transType IN (1,2) " + " AND S.salesTranss.createdUser = " + userId
                    + " AND DATE(S.salesTranss.createdAt) = '" + fromDate + "'";// salesTransDate
            List<HashMap> listSalesTrans = dataService.executeSelectHQL(HashMap.class, hqlSalesTrans, true, 0, 0);
            for (HashMap hm : listSalesTrans) {
                int transType = (Integer) hm.get("transType");
                if (transType == 1) {
                    isNhanHang = true;
                    soHangNhan += (Integer) hm.get("quantity");
                } else if (transType == 2) {
                    soHangBan += (Integer) hm.get("quantity");
                    doanhSo += (long) (Integer) hm.get("quantity") * (Integer) hm.get("price");
                }
            }
            //Get Doanh so salesTran cua salesSup 
            String hqlSales = "Select S.id as id, S.quantity as quantity, S.price as price "
                    + " from SalesTransDetails as S, MCPDetails as M, UserRoleChannel as URC"
                    + " where S.salesTranss.transType = 2 AND M.deletedUser = 0 and M.mcps.deletedUser = 0 "
                    + " AND S.salesTranss.deletedUser = 0 AND S.deletedUser = 0 AND URC.deletedUser = 0"
                    + " AND URC.users.id = S.salesTranss.createdUser"
                    + " AND S.salesTranss.toStocks.poss.id = M.poss.id"
                    + " AND WEEK(S.salesTranss.createdAt) = WEEK(M.mcps.beginDate)"
                    + " AND YEAR(S.salesTranss.createdAt) = YEAR(M.mcps.beginDate)"
                    + " AND M.mcps.implementEmployees.id = " + userId
                    + " AND URC.userRoles.id != 6"
                    + " AND DATE(S.salesTranss.createdAt) = '" + fromDate + "'"
                    + " GROUP BY S.id";
            List<HashMap> listSalesOfSalesSup = dataService.executeSelectHQL(HashMap.class, hqlSales, true, 0, 0);
            for (HashMap hm : listSalesOfSalesSup) {
                soHangBan += (Integer) hm.get("quantity");
                doanhSo += (long) (Integer) hm.get("quantity") * (Integer) hm.get("price");
            }

            dailyOrder.setIsNhanHang(isNhanHang);
            dailyOrder.setSoluongNhan(soHangNhan);
            dailyOrder.setSoluongBan(soHangBan);
            dailyOrder.setDoanhSo(doanhSo);

            listOrderDailys.add(dailyOrder);
        }

        String temp = String.format("%sdownloads%sBaocaongay.xls", File.separator, File.separator);
        File f = new File(servletContext.getRealPath("") + temp);
        try {
 			// Set our response properties
            // Here you can declare a custom filename
            //String fileName = "SAM_REPORT_12h_" + new SimpleDateFormat("dd-MM-yyyy").format(filter.getStartDateString()) + ".xls";
            String fileName = "SAAS_REPORT_DAY_" + new SimpleDateFormat("dd-MM-yyyy").format(date) + ".xls";
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            // Set content type
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            Cookie cookie = new Cookie("downloaded", "true");
            cookie.setPath("/");
            response.addCookie(cookie);

            Workbook workbook = WorkbookFactory.create(new FileInputStream(f));
            //HHHWORK	
            Sheet posSheet = workbook.getSheetAt(0);
            int i = 1;
            for (DailyOrderReport item : listOrderDailys) {
                Row row = posSheet.createRow(i++);
                int col = 0;
                createCell(item.getUserId(), row, col++);
                createCell(item.getUsername(), row, col++);
                createCell(item.getName(), row, col++);
                createCell(item.getSalesSup(), row, col++);
                createCell(item.getAsm(), row, col++);
                createCell(item.getRegion(), row, col++);
                createCell(item.getNpp(), row, col++);
                createCell(item.getTotalPos(), row, col++);
                createCell(item.getTotalPosIsCare(), row, col++);
                createCell(item.getTotalPosIsSales(), row, col++);
                createCell(item.getDonHangThuong(), row, col++);
                createCell(item.getDonHangThuongIsGiao(), row, col++);
                createCell(item.getDhDienThoai(), row, col++);
                createCell(item.getDhDienThoaiIsGiao(), row, col++);
                createCell(item.getSoDiemMoi(), row, col++);
                if (item.getIsNhanHang()) {
                    createCell("x", row, col++);
                } else {
                    createCell("", row, col++);
                }
                createCell(item.getSoluongNhan(), row, col++);
                createCell(item.getSoluongBan(), row, col++);
                createCell(item.getDoanhSo(), row, col++);
                createCell(item.getDoanhSoDH(), row, col++);
                createCell(item.getDoanhSoDHDienThoai(), row, col++);
            }
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            workbook.write(outByteStream);
            response.setContentLength(outByteStream.size());
            response.getOutputStream().write(outByteStream.toByteArray());
            response.getOutputStream().flush();
        } catch (Exception e) {
            //new FunctionException(getClass(), e);
        }
    }

    private void createCell(Object ob, Row row, int col) {
        if (ob == null) {
            return;
        }
        if (ob instanceof String) {
            row.createCell(col).setCellValue((String) ob);
        } else if (ob instanceof Integer) {
            row.createCell(col).setCellValue((Integer) ob);
        } else if (ob instanceof Long) {
            row.createCell(col).setCellValue((Long) ob);
        } else if (ob instanceof Float) {
            float v = (Float) ob;
            DecimalFormat f = new DecimalFormat("##.00");
            row.createCell(col).setCellValue(Float.parseFloat(f.format(v)));
        } else if (ob instanceof Double) {
            double v = (Double) ob;
            DecimalFormat f = new DecimalFormat("##.00");
            row.createCell(col).setCellValue(Double.parseDouble(f.format(v)));
        }
    }
}
