/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.report.excelBuilder;

import java.io.IOException;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author vtm
 */
public class excelCreator {
    
    public static XSSFWorkbook createWorkbook() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        return workbook;
    }

    public static XSSFSheet createSheet(String sheetName, XSSFWorkbook workbook) {
        return workbook.createSheet(sheetName);
    }

    public static XSSFRow createRow(int index, XSSFSheet sheet) {
        return sheet.createRow(index);
    }
}
