package vn.itt.msales.common.xls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ChinhNQ
 */
public class MsalesReadExcel {

    private MsalesExcelConfig mExcelConfig;

    public MsalesReadExcel(MsalesExcelConfig excelConfig) {
        this.mExcelConfig = excelConfig;
    }

    private List<List<Object>> readXLS() {
        List<List<Object>> listObj = new ArrayList<>();
        try {
            //Creates a workbook object from the uploaded excelfile
            HSSFWorkbook workbook = new HSSFWorkbook(mExcelConfig.getmMultipartFile().getInputStream());
            //Creates a worksheet object representing the first sheet
            HSSFSheet worksheet;
            if(mExcelConfig.getSheetIndex() >= 0){
            	worksheet = workbook.getSheetAt(mExcelConfig.getSheetIndex());
            }else{
            	worksheet = workbook.getSheetAt(0);
            }
            
            //Iterate through each rows from first sheet
            Iterator<Row> rowIterator = worksheet.iterator();
            while (rowIterator.hasNext()) {
                List<Object> dataRow = new ArrayList<>();
                Row row = rowIterator.next();
                int currnetRow = row.getRowNum();
                if (currnetRow != mExcelConfig.getBeginRow()) {
                    //For each row, iterate through each columns
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_BLANK:
                                dataRow.add("");
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                dataRow.add(cell.getBooleanCellValue());
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                                    String dateFm = "dd/MM/yyyy";//cell.getCellStyle().getDataFormatString();
                                    dataRow.add(new CellDateFormatter(dateFm).format(date));
                                } else {
                                    dataRow.add(cell.getNumericCellValue());
                                }
                                break;
                            case Cell.CELL_TYPE_STRING:
                                dataRow.add(cell.getStringCellValue());
                                break;
                        }
                    }
                    listObj.add(dataRow);
                    System.out.println("");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MsalesReadExcel.class.getName()).log(Level.SEVERE, null, ex);

        }
        return listObj;
    }

    private List<List<Object>> readXLSX() {
        List<List<Object>> listObj = new ArrayList<>();
        try {
            //Creates a workbook object from the uploaded excelfile
            XSSFWorkbook workbook = new XSSFWorkbook(mExcelConfig.getmMultipartFile().getInputStream());
            //Creates a worksheet object representing the first sheet
            XSSFSheet workSheet;
            if(mExcelConfig.getSheetIndex() >= 0){
            	workSheet = workbook.getSheetAt(mExcelConfig.getSheetIndex());
            }else{
            	workSheet = workbook.getSheetAt(0);
            }
            //Iterate through each rows from first sheet
            Iterator<Row> rowIterator = workSheet.iterator();
            while (rowIterator.hasNext()) {
                List<Object> dataRow = new ArrayList<>();
                Row row = rowIterator.next();
                int currnetRow = row.getRowNum();
                if (currnetRow != mExcelConfig.getBeginRow()) {
                    //For each row, iterate through each columns
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        
                        Cell cell = cellIterator.next();

                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_BLANK:
                                dataRow.add("");
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                dataRow.add(cell.getBooleanCellValue());
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                                    String dateFm = "dd/MM/yyyy";//cell.getCellStyle().getDataFormatString();
                                    dataRow.add(new CellDateFormatter(dateFm).format(date));
                                } else {
                                    dataRow.add(cell.getNumericCellValue());
                                }
                                break;
                            case Cell.CELL_TYPE_STRING:
                                dataRow.add(cell.getStringCellValue());
                                break;
                        }
                       
                    }
                    listObj.add(dataRow);
                }
            }
        } catch (IOException ex) {
            //Logger.getLogger(MsalesReadExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listObj;
    }
    
    private List<List<Object>> readXLS(HSSFWorkbook workbook) {
        List<List<Object>> listObj = new ArrayList<>();
        try {
            //Creates a workbook object from the uploaded excelfile
        //    HSSFWorkbook workbook = new HSSFWorkbook(mExcelConfig.getmMultipartFile().getInputStream());
            //Creates a worksheet object representing the first sheet
            HSSFSheet worksheet;
            if(mExcelConfig.getSheetIndex() >= 0){
            	worksheet = workbook.getSheetAt(mExcelConfig.getSheetIndex());
            }else{
            	worksheet = workbook.getSheetAt(0);
            }
            
            //Iterate through each rows from first sheet
            Iterator<Row> rowIterator = worksheet.iterator();
            while (rowIterator.hasNext()) {
                List<Object> dataRow = new ArrayList<>();
                Row row = rowIterator.next();
                int currnetRow = row.getRowNum();
                if (currnetRow != mExcelConfig.getBeginRow()) {
                    //For each row, iterate through each columns
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_BLANK:
                                dataRow.add("");
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                dataRow.add(cell.getBooleanCellValue());
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                                    String dateFm = "dd/MM/yyyy";//cell.getCellStyle().getDataFormatString();
                                    dataRow.add(new CellDateFormatter(dateFm).format(date));
                                } else {
                                    dataRow.add(cell.getNumericCellValue());
                                }
                                break;
                            case Cell.CELL_TYPE_STRING:
                                dataRow.add(cell.getStringCellValue());
                                break;
                        }
                    }
                    listObj.add(dataRow);
                }
            }
        } catch (Exception ex) {
            //Logger.getLogger(MsalesReadExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listObj;
    }

    private List<List<Object>> readXLSX(XSSFWorkbook workbook) {
        List<List<Object>> listObj = new ArrayList<>();
        //Creates a workbook object from the uploaded excelfile
       //   XSSFWorkbook workbook = new XSSFWorkbook(mExcelConfig.getmMultipartFile().getInputStream());
		//Creates a worksheet object representing the first sheet
		XSSFSheet workSheet;
		if(mExcelConfig.getSheetIndex() >= 0){
			workSheet = workbook.getSheetAt(mExcelConfig.getSheetIndex());
		}else{
			workSheet = workbook.getSheetAt(0);
		}
		//Iterate through each rows from first sheet
		Iterator<Row> rowIterator = workSheet.iterator();
		while (rowIterator.hasNext()) {
		    List<Object> dataRow = new ArrayList<>();
		    Row row = rowIterator.next();
		    int currnetRow = row.getRowNum();
		    if (currnetRow != mExcelConfig.getBeginRow()) {
		        //For each row, iterate through each columns
		        Iterator<Cell> cellIterator = row.cellIterator();
		        while (cellIterator.hasNext()) {
		            
		            Cell cell = cellIterator.next();

		            switch (cell.getCellType()) {
		                case Cell.CELL_TYPE_BLANK:
		                    dataRow.add("");
		                    break;
		                case Cell.CELL_TYPE_BOOLEAN:
		                    dataRow.add(cell.getBooleanCellValue());
		                    break;
		                case Cell.CELL_TYPE_NUMERIC:
		                    if (DateUtil.isCellDateFormatted(cell)) {
		                        Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
		                        String dateFm = "dd/MM/yyyy";//cell.getCellStyle().getDataFormatString();
		                        dataRow.add(new CellDateFormatter(dateFm).format(date));
		                    } else {
		                        dataRow.add(cell.getNumericCellValue());
		                    }
		                    break;
		                case Cell.CELL_TYPE_STRING:
		                    dataRow.add(cell.getStringCellValue());
		                    break;
		            }
		           
		        }
		        listObj.add(dataRow);
		    }
		}
        return listObj;
    }
    
    public static List<List<Object>> read(Sheet sheet,int beginRow,int maxRow) {
        List<List<Object>> listObj = new ArrayList<>();
        try {
            sheet.getPhysicalNumberOfRows();
            Iterator<Row> iterator = sheet.rowIterator();
            while (iterator.hasNext()) {
                List<Object> dataRow = new ArrayList<>();
                Row row = iterator.next();
                int currnetRow = row.getRowNum();
                if (currnetRow >= beginRow && currnetRow < maxRow) {
                    //For each row, iterate through each columns
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();                        
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_BLANK:
                                dataRow.add("");
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                dataRow.add(cell.getBooleanCellValue());
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                                    String dateFm = "dd/MM/yyyy";//cell.getCellStyle().getDataFormatString();
                                    dataRow.add(new CellDateFormatter(dateFm).format(date));
                                } else {
                                    dataRow.add(cell.getNumericCellValue());
                                }
                                break;
                            case Cell.CELL_TYPE_STRING:
                                dataRow.add(cell.getStringCellValue());
                                break;
                        }
                    }
                    listObj.add(dataRow);
                }
            }
        } catch (Exception ex) {
            //Logger.getLogger(MsalesReadExcel.class.getName()).log(Level.SEVERE, null, ex);

        }
        return listObj;
    }

    public List<List<Object>> readExcel() {
        String ext = FilenameUtils.getExtension(mExcelConfig.getmMultipartFile().getOriginalFilename());
        if (null != ext) {
            switch (ext) {
                case "xlsx":
                    return readXLSX();
                case "xls":
                    return readXLS();
            }
        }
        return new ArrayList();
    }
    
    public List<List<Object>> readExcel(XSSFWorkbook workbook2, HSSFWorkbook workbook) {
        String ext = FilenameUtils.getExtension(mExcelConfig.getmMultipartFile().getOriginalFilename());
        if (null != ext) {
            switch (ext) {
                case "xlsx":
                    return readXLSX(workbook2);
                case "xls":
                    return readXLS(workbook);
            }
        }
        return new ArrayList();
    }
    
}
