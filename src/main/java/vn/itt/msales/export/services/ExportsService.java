package vn.itt.msales.export.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import vn.itt.msales.common.ArrayListUtils;
import vn.itt.msales.common.DynamicColumnDataSource;
import vn.itt.msales.common.DynamicReportBuilder;

@Service
public class ExportsService {

    protected static Logger logger = Logger.getLogger("service");
    public static final String TEMPLATE = "template";
    public static final String TEMPLATES = "templates";
    public static final String EXPORT_TYPE = "exportType";
    public static final String FILE_NAME = "fileName";
    public static final String MEDIA_TYPE_EXCEL = "application/vnd.ms-excel";
    public static final String MEDIA_TYPE_PDF = "application/pdf";
    public static final String MEDIA_TYPE_PPT = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String EXTENSION_TYPE_EXCEL = "xls";
    public static final String EXTENSION_TYPE_EXCEL_2007 = "xlsx";
    public static final String EXTENSION_TYPE_PDF = "pdf";
    public static final String EXTENSION_TYPE_PPT = "ppt";
    public static final String EXTENSION_TYPE_PPTX = "pptx";
    private final String STATUS_DOWNLOAD = "downloaded";
    private final String DOWNLOADED = "true";
    private final String SHEET_TYPE_MULTI = "multi";
    private final String SHEET_TYPE_SINGLE = "single";

    private HashMap<String, String> tokens = new HashMap<String, String>();
    private FileSystem fileSystem = FileSystems.getDefault();
    private String dot = ".";
    private String jasperExt = String.format("%s%s", dot, "jasper");
    private String jrxmlExt = String.format("%s%s", dot, "jrxml");
    private String tmpDir = System.getProperties().get("java.io.tmpdir")
            .toString();

    public String check(String token) {
        return tokens.get(token);
    }

    public void download(Object objFilter, String token,
            JRDataSource jrDataSource, HttpServletResponse response) {
        FileInputStream jr;
        JRFileVirtualizer virtualizer = new JRFileVirtualizer(20, tmpDir);
        try {
            // Get report parameters and name
            Map<String, Object> params = ArrayListUtils
                    .getAllProperties(objFilter);
            String jrxmlName = (String) params.get(TEMPLATE);
            // get compiled template and fill report
            File jasperFile = getJasperFile(jrxmlName);
            jr = new FileInputStream(jasperFile);
            params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
            JasperPrint jp = JasperFillManager.fillReport(jr, params,
                    jrDataSource);
            jr.close();
            // export
            File tmpFile = exportToFile(jp, params.get(EXPORT_TYPE).toString(),
                    response, token, params.get(FILE_NAME).toString(),
                    SHEET_TYPE_SINGLE);
            // produce response
            if (tmpFile == null) {
                // failed to export
                throw new RuntimeException();
            } else {
                // produce response
                Path tmpPath = tmpFile.toPath();
                // response.setHeader("Content-Encoding","gzip");
                response.setHeader("Content-Disposition",
                        " attachment; filename=" + tmpFile.getName());
                response.setContentLength((int) tmpFile.length());
                Cookie cookie = new Cookie(STATUS_DOWNLOAD, DOWNLOADED);
                cookie.setPath("/");
                response.addCookie(cookie);
				// GZIPOutputStream gout=new
                // GZIPOutputStream(response.getOutputStream());
                // FileCopyUtils.copy(Files.newInputStream(tmpPath),
                // gout);
                FileCopyUtils.copy(Files.newInputStream(tmpPath),
                        response.getOutputStream());
                Files.delete(tmpPath);
            }
        } catch (Exception e) {
            String err = e.getMessage();
            logger.error(err);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } finally {
            remove(token);
            virtualizer.cleanup();
            System.gc();
        }
    }

    @SuppressWarnings("unchecked")
    public void downloadMulti(List<Object> objFilterList, String token,
            List<Object> objectList, HttpServletResponse response, String type,
            String fileName) {
        JRFileVirtualizer virtualizer = new JRFileVirtualizer(20, tmpDir);
        try {
            if (objFilterList.size() == objectList.size()) {
                List<JasperPrint> jpList = new ArrayList<JasperPrint>();
                for (int i = 0; i < objectList.size(); i++) {
                    // 1. Add report parameters
                    Map<String, Object> params = ArrayListUtils
                            .getAllProperties(objFilterList.get(i));
                    // 2. Retrieve template
                    File jasperFile = getJasperFile(params.get(TEMPLATE)
                            .toString());
                    // 3. Convert template to FileInputStream and set vitualizer
                    FileInputStream jr = new FileInputStream(jasperFile);
                    params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
                    // 4. Create the JasperPrint object
                    JRDataSource jrDS = null;
                    List<Object> list = (List<Object>) objectList.get(i);
                    if (list.size() > 0) {

                        jrDS = new JRBeanCollectionDataSource(list);
                    } else {
                        jrDS = new JREmptyDataSource();
                    }
					// 5. Create the JasperPrint object
                    // Make sure to pass the JasperReport, report parameters,
                    // and
                    // data
                    // source
                    if (fileName == null) {
                        fileName = params.get(FILE_NAME).toString();
                    }
                    JasperPrint jp = JasperFillManager.fillReport(jr, params,
                            jrDS);
                    jp.setName(params.get(FILE_NAME).toString());
                    jpList.add(jp);

                }

                // 7. Export report
                File tmpFile = exportToFile(jpList, type, response, token,
                        fileName, SHEET_TYPE_MULTI);

                // produce response
                if (tmpFile == null) {
                    // failed to export
                    throw new RuntimeException();
                } else {
                    // produce response
                    Path tmpPath = tmpFile.toPath();
                    response.setHeader("Content-Disposition",
                            " attachment; filename=" + tmpFile.getName());
                    long length = tmpFile.length();
                    if (length <= Integer.MAX_VALUE) {
                        response.setContentLength((int) length);
                    } else {
                        response.addHeader("Content-Length",
                                Long.toString(length));
                    }
                    Cookie cookie = new Cookie(STATUS_DOWNLOAD, DOWNLOADED);
                    cookie.setPath("/");
                    response.addCookie(cookie);
					// GZIPOutputStream gout=new
                    // GZIPOutputStream(response.getOutputStream());
                    // FileCopyUtils.copy(Files.newInputStream(tmpPath),
                    // gout);
                    FileCopyUtils.copy(Files.newInputStream(tmpPath),
                            response.getOutputStream());
                    Files.delete(tmpPath);
                }
            } else {
                logger.error("Number filter size  not equals JRDataSource size ");
            }

        } catch (Exception jre) {
            logger.error("Method downloadMulti error: " + jre.getMessage());
        } finally {
            remove(token);
            virtualizer.cleanup();
            System.gc();
        }
    }

    /**
     * Export JasperPrint to a file in temporary directory
     * <p>
     * @param jp
     * @param params
     * @param token <p>
     * @return <p>
     * @throws IOException
     */
    public File exportToFile(Object jp, String type,
            HttpServletResponse response, String fileName, String token,
            String sheetType) throws IOException {
        Path resultPath = fileSystem.getPath(tmpDir,
                String.format("%s-%s%s%s", fileName, token, dot, type));
        File resultFile = resultPath.toFile();

        if (type.equals(EXTENSION_TYPE_EXCEL)) {
            response.setContentType(MEDIA_TYPE_EXCEL);
            exportXls(jp, resultFile, sheetType);
        } else if (type.equals(EXTENSION_TYPE_EXCEL_2007)) {
            response.setContentType(MEDIA_TYPE_EXCEL);
            exportXlsx(jp, resultFile, sheetType);
        } else if (type.equals(EXTENSION_TYPE_PPT)) {
            response.setContentType(MEDIA_TYPE_PPT);
            exportPPT(jp, resultFile, sheetType);
        } else if (type.equals(EXTENSION_TYPE_PPTX)) {
            response.setContentType(MEDIA_TYPE_PPT);
            exportPPTx(jp, resultFile, sheetType);
        } else {
            resultFile = null;
        }
        return resultFile;
    }

    /**
     * Return jasper file for a jrxml file
     * <p>
     * @param jrxmlName <p>
     * @return <p>
     * @throws JRException
     * @throws IOException
     */
    private File getJasperFile(String jrxmlName) throws JRException,
            IOException {
        Resource jrxmlRc = new ClassPathResource(jrxmlName);
        Long jrxmlLastModified = jrxmlRc.lastModified();
        String jrxmlPath = jrxmlRc.getFile().getAbsolutePath();
        String jasperPath = jrxmlPath.replace(jrxmlExt, jasperExt);
        File jasperFile = new File(jasperPath);
        if (!jasperFile.exists()
                || jasperFile.lastModified() < jrxmlLastModified) {
            JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);
        }
        return jasperFile;
    }

    public void exportXls(Object obj, File f, String sheetType) {
        // Create a JRXlsExporter instance
        JRXlsExporter exporter = new JRXlsExporter();
        // Here we assign the parameters jp and baos to the exporter
        if (sheetType.equals(SHEET_TYPE_SINGLE)) {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, obj);
        } else {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, obj);
        }
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE, f);

        // Excel specific parameters
        exporter.setParameter(
                JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET,
                Boolean.FALSE);
        exporter.setParameter(
                JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
                Boolean.TRUE);
        exporter.setParameter(
                JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND,
                Boolean.FALSE);

        try {
            exporter.exportReport();

        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportXlsx(Object obj, File f, String sheetType) {
        // Create a JRXlsExporter instance
        JRXlsxExporter exporter = new JRXlsxExporter();

        // Here we assign the parameters jp and baos to the exporter
        if (sheetType.equals(SHEET_TYPE_SINGLE)) {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, obj);
        } else {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, obj);
        }
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE, f);

        // Excel specific parameters
        exporter.setParameter(
                JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET,
                Boolean.FALSE);
        exporter.setParameter(
                JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
                Boolean.TRUE);
        exporter.setParameter(
                JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND,
                Boolean.FALSE);

        try {
            exporter.exportReport();

        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportPPT(Object obj, File file, String sheetType) {
        // Create a JRXlsExporter instance
        JRPptxExporter exporter = new JRPptxExporter();

        if (sheetType.equals(SHEET_TYPE_SINGLE)) {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, obj);
        } else {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, obj);
        }
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);

        try {
            exporter.exportReport();

        } catch (JRException e) {
            logger.error("Export PPTx Erorr: " + e.getMessage());
        }
    }

    public void exportPPTx(Object obj, File file, String sheetType) {
        // Create a JRXlsExporter instance
        JRPptxExporter exporter = new JRPptxExporter();

        if (sheetType.equals(SHEET_TYPE_SINGLE)) {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, obj);
        } else {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, obj);
        }
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);

        try {
            exporter.exportReport();

        } catch (JRException e) {
            logger.error("Export PPTx Erorr: " + e.getMessage());
        }
    }

    public void exportPdf(Object obj, File f, String sheetType) {
        // Create a JRXlsExporter instance
        JRPdfExporter exporter = new JRPdfExporter();

        // Here we assign the parameters jp and baos to the exporter
        if (sheetType.equals(SHEET_TYPE_SINGLE)) {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, obj);
        } else {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, obj);
        }
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE, f);

        try {
            exporter.exportReport();

        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportPdfx(Object obj, File f, String sheetType) {
        // Create a JRXlsExporter instance
        JRPdfExporter exporter = new JRPdfExporter();

        // Here we assign the parameters jp and baos to the exporter
        if (sheetType.equals(SHEET_TYPE_SINGLE)) {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, obj);
        } else {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, obj);
        }
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE, f);

        try {
            exporter.exportReport();

        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    public String generate() {
        String uid = UUID.randomUUID().toString();
        tokens.put(uid, uid);
        return uid;
    }

    public void remove(String token) {
        tokens.remove(token);
    }

	// public void exportXlsMulti(List<JasperPrint> jpList, File file) {
    // // Create a JRXlsExporter instance
    // JRXlsExporter exporter = new JRXlsExporter();
    //
    // // Here we assign the parameters jp and baos to the exporter
    // exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jpList);
    // exporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);
    //
    // // Excel specific parameters
    // //
    // exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET,
    // // Boolean.FALSE);
    // exporter.setParameter(
    // JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
    // Boolean.TRUE);
    // exporter.setParameter(
    // JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND,
    // Boolean.FALSE);
    // exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
    // Boolean.FALSE);
    //
    // try {
    // exporter.exportReport();
    //
    // } catch (JRException e) {
    // logger.error("exportXlsMulti Xlsx Erorr: " + e.getMessage());
    // }
    // }
    //
    // public void exportXlsxMulti(List<JasperPrint> jpList, File file) {
    // // Create a JRXlsExporter instance
    // JRXlsxExporter exporter = new JRXlsxExporter();
    //
    // // Here we assign the parameters jp and baos to the exporter
    // exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jpList);
    // exporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);
    //
    // // Excel specific parameters
    // //
    // exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET,
    // // Boolean.FALSE);
    // exporter.setParameter(
    // JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
    // Boolean.TRUE);
    // exporter.setParameter(
    // JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND,
    // Boolean.FALSE);
    // exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
    // Boolean.FALSE);
    //
    // try {
    // exporter.exportReport();
    //
    // } catch (JRException e) {
    // logger.error("exportXlsxMulti Xls Erorr: " + e.getMessage());
    // }
    // }
    public void download(Object objFilter, Map<String, Object> parameters, String token,
            JRDataSource jrDataSource, HttpServletResponse response) {
        FileInputStream jr;
        JRFileVirtualizer virtualizer = new JRFileVirtualizer(20, tmpDir);
        try {
            // Get report parameters and name
            Map<String, Object> params = ArrayListUtils
                    .getAllProperties(objFilter);
            String jrxmlName = (String) params.get(TEMPLATE);
            // get compiled template and fill report
//            File jasperFile = getJasperFile(jrxmlName);
            jr = new FileInputStream(jrxmlName);
            params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
            JasperPrint jp = JasperFillManager.fillReport(jr, parameters,
                    jrDataSource);
            jr.close();
            // export
            File tmpFile = exportToFile(jp, params.get(EXPORT_TYPE).toString(),
                    response, params.get(FILE_NAME).toString(), token,
                    SHEET_TYPE_SINGLE);
            // produce response
            if (tmpFile == null) {
                // failed to export
                // java.util.logging.Logger.getLogger("error").log(Level.WARNING, "error: file temp null.");
                throw new RuntimeException();
            } else {
                // produce response
                Path tmpPath = tmpFile.toPath();
                // response.setHeader("Content-Encoding","gzip");
                response.setHeader("Content-Disposition",
                        " attachment; filename=" + tmpFile.getName());
                response.setContentLength((int) tmpFile.length());
                Cookie cookie = new Cookie(STATUS_DOWNLOAD, DOWNLOADED);
                cookie.setPath("/");
                response.addCookie(cookie);
				// GZIPOutputStream gout=new
                // GZIPOutputStream(response.getOutputStream());
                // FileCopyUtils.copy(Files.newInputStream(tmpPath),
                // gout);
                FileCopyUtils.copy(Files.newInputStream(tmpPath),
                        response.getOutputStream());
                Files.delete(tmpPath);
            }
        } catch (Exception e) {
            String err = e.getMessage();
            logger.error(err);
           // java.util.logging.Logger.getLogger("error").log(Level.WARNING, "error: "+ e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } finally {
            remove(token);
            virtualizer.cleanup();
            System.gc();
        }
    }

    public File getFontFilePath(String classpathRelativePath) {
        ClassPathResource rsrc = new ClassPathResource(classpathRelativePath);
        try {
            return rsrc.getFile();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ExportsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void download(Object objFilter, Map<String, Object> parameters, String token,
            List<String> columnHeaders, List<Integer> widthHeaders, List<List<String>> dataList, HttpServletResponse response) {
        JRFileVirtualizer virtualizer = new JRFileVirtualizer(20, tmpDir);
        try {
            // Get report parameters and name
            Map<String, Object> params = ArrayListUtils.getAllProperties(objFilter);
            String jrxmlName = (String) params.get(TEMPLATE);
//            Resource jrxmlRc = new ClassPathResource(jrxmlName);
            File fileName = new File(jrxmlName);
            String jrxmlPath = fileName.getAbsolutePath();
            JasperDesign jd = JRXmlLoader.load(jrxmlPath);
            params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
            DynamicReportBuilder reportBuilder = new DynamicReportBuilder(jd, columnHeaders.size(), widthHeaders);
            reportBuilder.addDynamicColumns();
            JasperReport jr = JasperCompileManager.compileReport(jd);
            DynamicColumnDataSource dataSource = null;
            if (null == dataList || dataList.size() <= 0) {
                dataSource = null;
            } else {
                dataSource = new DynamicColumnDataSource(columnHeaders, dataList);
            }
            JasperPrint jp = JasperFillManager.fillReport(jr, parameters, dataSource);
            File tmpFile = exportToFile(jp, params.get(EXPORT_TYPE).toString(),
                    response, params.get(FILE_NAME).toString(),token,
                    SHEET_TYPE_SINGLE);
            // produce response
            if (tmpFile == null) {
                // failed to export
                throw new RuntimeException();
            } else {
                // produce response
                Path tmpPath = tmpFile.toPath();
                // response.setHeader("Content-Encoding","gzip");
                response.setHeader("Content-Disposition",
                        " attachment; filename=" + tmpFile.getName());
                response.setContentLength((int) tmpFile.length());
                Cookie cookie = new Cookie(STATUS_DOWNLOAD, DOWNLOADED);
                cookie.setPath("/");
                response.addCookie(cookie);
                FileCopyUtils.copy(Files.newInputStream(tmpPath),
                        response.getOutputStream());
                Files.delete(tmpPath);
            }
        } catch (Exception e) {
            String err = e.getMessage();
           // java.util.logging.Logger.getLogger("error").log(Level.WARNING, "error: "+ e.getMessage());
            logger.error(err);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } finally {
            remove(token);
            virtualizer.cleanup();
            System.gc();
        }
    }

}
