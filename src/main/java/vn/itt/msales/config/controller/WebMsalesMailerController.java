package vn.itt.msales.config.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.itt.msales.customercare.model.ReportFilter;
import vn.itt.msales.database.dbrouting.MsalesBranches;
import vn.itt.msales.entity.Channel;
import vn.itt.msales.entity.Company;
import vn.itt.msales.report.model.MsalesOrderReport;
import vn.itt.msales.report.service.ReportService;
import vn.itt.msales.user.controller.MsalesUserController;

/**
 *
 * @author ChinhNQ
 */
@Service
public class WebMsalesMailerController {

    String separator = File.separator;
    private final String filePath = String.format("reports%s", separator);

    @Autowired
    private MsalesUserController userController;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ReportService reportService;

    @Value("#{mailer['msale.mail.user']}")
    private String mailUser;

    @Value("#{mailer['msale.mail.pass']}")
    private String mailPass;

    @Value("#{mailer['msale.mail.from']}")
    private String pnFromEmail;

    @Value("#{mailer['msale.mail.bcc']}")
    private String pnBccEmail;

    @Value("#{mailer['msale.mail.host.value']}")
    private String hostValue;

    @Value("#{mailer['msale.mail.socket.value']}")
    private String socketValue;

    @Value("#{mailer['msale.mail.auth.value']}")
    private String authValue;

    @Value("#{mailer['msale.mail.port.value']}")
    private String portValue;

    @Value("#{mailer['msale.mail.title']}")
    private String mailTitle;

    @Value("#{mailer['msale.mail.charset']}")
    private String mailCharset;
    private String appPath;
    private String fullpath;

    public void sendMailAndAttachFile() {
        appPath = servletContext.getRealPath("");
        fullpath = appPath + filePath + "OrderEmail.jasper";
        try {
            for (int branh : MsalesBranches.BRANCHES) {
                // open database
                userController.getDataService().setBranch(branh);
                // get list company in branh, have config send email
                List<Company> listCompany = reportService.getListCompanySendMail();
                if (listCompany != null && !listCompany.isEmpty()) {
                    for (Company company : listCompany) {
                        List<Channel> distributionList = reportService.getListDistribution(company.getId());
                        if (distributionList != null && !distributionList.isEmpty()) {
                            for (Channel distributrion : distributionList) {
                                //System.err.println(">>send to: " + distributrion.getId() + ":" + distributrion.getName());
                                List<HashMap> listOrder = reportService.getListOrders(company.getId(), distributrion.getId());
                                if (listOrder != null && !listOrder.isEmpty()) {
                                    List<MsalesOrderReport> listOrders = getListOrder(listOrder);

                                    if (!listOrders.isEmpty()) {
                                        JRDataSource jrDataSource = new JRBeanCollectionDataSource(listOrders);
                                        ReportFilter filter = createReportFilter(distributrion.getName(), distributrion.getAddress(), distributrion.getTel());
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        // create xml report from jasper
                                        download(filter, jrDataSource, baos);
                                        // create session send email
                                        Session session = createSession();
                                        // create a message
                                        MimeMessage message = new MimeMessage(session);
                                        // Set From: header field of the header.
                                        message.setFrom(new InternetAddress(pnFromEmail));
                                        if (pnBccEmail != null && !pnBccEmail.isEmpty()) {
                                            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(pnBccEmail));
                                        }
                                        message.setSubject(getSubject(mailTitle, distributrion.getName()), mailCharset);
                                        DataSource source = new ByteArrayDataSource(baos.toByteArray(), "application/vnd.ms-excel");

                                        // create and fill the first message part
                                        MimeBodyPart messageBodyPart = new MimeBodyPart();
                                        messageBodyPart.setDataHandler(new DataHandler(source));
                                        messageBodyPart.setFileName("Don Dat Hang.xls");

                                        // create the Multipart and add its parts to it
                                        Multipart multipart = new MimeMultipart();
                                        BodyPart messageBodyPart1 = new MimeBodyPart();
                                        messageBodyPart1.setContent(getContent(distributrion.getName()), "text/html; charset=utf-8");

                                        multipart.addBodyPart(messageBodyPart1);
                                        multipart.addBodyPart(messageBodyPart);

                                        // 6) set the multiplart object to the message object
                                        message.setContent(multipart);
                                        // set the Date: header
                                        message.setSentDate(new Date());
                                        //FIXME: get all list email of distribution
                                        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(distributrion.getEmail()));
                                        // Send message
                                        System.out.println(">>>Sending...");
                                        Transport.send(message);

                                        System.out.println(">>>Finished");
                                    }
                                    
                                }
                            }
                        }
                    }
                }
            }

        } catch (MessagingException mex) {
            System.err.println(">>>: " + mex.getMessage());
            Exception ex = null;
            if ((ex = mex.getNextException()) != null) {
                System.err.println(">>>: " + mex.getMessage());
            }
        }
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        //get current date time with Calenda;
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    private String getSubject(String title, String channelName) {
        return String.format("%s - %s - %s", title, channelName, getCurrentDate());
    }

    private String getContent(String channelName) {
        String content = String.format("- Đơn đặt hàng ngày %s\n- Nhà phân phối: %s", getCurrentDate(), channelName);
        return content;
    }

    private void download(ReportFilter filter, JRDataSource jrDataSource, ByteArrayOutputStream baos) {

        try {
            JasperPrint jp = getJasperPrint(filter, jrDataSource);
            Object name = filter.getParamsMap().get("reportName");
            if (name != null) {
                jp.setName(name.toString());
            }
            Object sheetName = filter.getParamsMap().get("sheetName");
            if (sheetName != null) {
                jp.setProperty("sheetName", sheetName.toString());
            }

            // Export report
            exportXls(jp, baos);

        } catch (Exception jre) {
            System.err.println(">>:" + jre.getMessage());
        }
    }

    private JasperPrint getJasperPrint(ReportFilter filter, JRDataSource jrDataSource) throws JRException {
        JasperPrint jp = null;
        // 1. Add report parameters
        Map<String, Object> params = filter.getParamsMap();
        params.put("Title", "User Report");
        boolean isJasperFile = filter.getTemplate().toLowerCase().endsWith(".jasper");

        // 2.  Retrieve template
        InputStream reportStream = getClass().getClassLoader().getResourceAsStream(filter.getTemplate());

        try {
            reportStream = new FileInputStream(filter.getTemplate());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WebMsalesMailerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JasperReport jr = null;
        if (!isJasperFile) {
            // 3. Convert template to JasperDesign
            JasperDesign jd = JRXmlLoader.load(reportStream);

            // 4. Compile design to JasperReport
            jr = JasperCompileManager.compileReport(jd);
        }

        // 5. Create the JasperPrint object
        // Make sure to pass the JasperReport, report parameters, and data source
        try {
            if (!isJasperFile) {
                jp = JasperFillManager.fillReport(jr, params, jrDataSource);
            } else {
                jp = JasperFillManager.fillReport(reportStream, params, jrDataSource);
            }
        } catch (Exception e) {
            System.err.println(">>:" + e.getMessage());
        }
        return jp;
    }

    private void exportXls(JasperPrint jp, ByteArrayOutputStream baos) {
        // Create a JRXlsExporter instance
        JRXlsExporter exporter = new JRXlsExporter();

        // Here we assign the parameters jp and baos to the exporter
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);

        // Excel specific parameters
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);

        try {
            exporter.exportReport();

        } catch (JRException e) {
            System.err.println(">>:" + e.getMessage());
        }
    }

    private List<MsalesOrderReport> getListOrder(List<HashMap> listhash) {
        List<MsalesOrderReport> listOrders = new ArrayList<>();
        for (HashMap hash : listhash) {
            MsalesOrderReport mreOrderReport = new MsalesOrderReport();
            mreOrderReport.setOrderDate(new Date());//new Date(hash.get("orderDate").toString())
            mreOrderReport.setEmployeCode(hash.get("employeCode").toString());
            mreOrderReport.setEmployeName(hash.get("firstName").toString() + " " + hash.get("lastName").toString());
            mreOrderReport.setCustomerCode(hash.get("customerCode").toString());
            mreOrderReport.setCustomerName(hash.get("customerName").toString());
            mreOrderReport.setAddress(hash.get("address").toString());
            mreOrderReport.setTel(hash.get("tel").toString());
            mreOrderReport.setGoodsCode(hash.get("goodsCode").toString());
            mreOrderReport.setGoodsName(hash.get("goodsName").toString());
            mreOrderReport.setQuantity(Integer.parseInt(hash.get("quantity").toString()));
            mreOrderReport.setPrice(Integer.parseInt(hash.get("price").toString()));

            listOrders.add(mreOrderReport);
        }
        return listOrders;
    }

    private Session createSession() {
        // Get the default Session object.
        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        if (hostValue != null && !hostValue.isEmpty()) {
            properties.put("mail.smtp.host", hostValue);
        }

        if (socketValue != null && !socketValue.isEmpty()) {
            properties.put("mail.smtp.socketFactory.port", socketValue);
        }

        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        if (authValue != null && !authValue.isEmpty()) {
            properties.put("mail.smtp.auth", authValue);
        }

        if (portValue != null && !portValue.isEmpty()) {
            properties.put("mail.smtp.port", portValue);
        }
        Session session = javax.mail.Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailUser, mailPass);
            }
        });

        return session;
    }

    private ReportFilter createReportFilter(String distributionName, String distributionAdress, String distributionTel) {
        ReportFilter filter = new ReportFilter();
        filter.putParamsMap("distributionName", distributionName);
        filter.putParamsMap("address", distributionAdress);
        filter.putParamsMap("tel", distributionTel);
        filter.setTemplate(fullpath);
        filter.setFileName("OrderReport");

        return filter;
    }
}
