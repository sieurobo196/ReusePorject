package vn.itt.msales.common;


import java.awt.Color;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
  
/**
 * Uses the Jasper Report API to dynamically add columns to the Report
 */
public class DynamicReportBuilder {
//The prefix used in defining the field name that is later used by the JasperFillManager
    public static final String COL_EXPR_PREFIX = "col";
  
    // The prefix used in defining the column header name that is later used by the JasperFillManager
    public static final String COL_HEADER_EXPR_PREFIX = "header";
  
    // The page width for a page in portrait mode with 10 pixel margins
    private final static int TOTAL_PAGE_WIDTH = 2000;
  
    // The whitespace between columns in pixels
    private final static int SPACE_BETWEEN_COLS = 0;
  
    // The height in pixels of an element in a row and column
    private final static int COLUMN_HEIGHT = 20;
  
    // The total height of the column header or detail band
    private final static int BAND_HEIGHT = 25;
  
    // The left and right margin in pixels
    private final static int MARGIN = 0;
  
    // The JasperDesign object is the internal representation of a report
    private JasperDesign jasperDesign;
  
    // The number of columns that are to be displayed
    private int numColumns;
    
    private List<Integer> widthHeaders;
  
    public DynamicReportBuilder(JasperDesign jasperDesign, int numColumns,List<Integer> widthHeaders) {
        this.jasperDesign = jasperDesign;
        this.numColumns = numColumns;
        this.widthHeaders=widthHeaders;
    }
  
    public void addDynamicColumns() throws JRException {
  
        JRDesignBand detailBand = new JRDesignBand();
        JRDesignBand headerBand = new JRDesignBand();
  
        JRDesignStyle normalStyle = getNormalStyle();
        JRDesignStyle columnHeaderStyle = getColumnHeaderStyle();
        jasperDesign.addStyle(normalStyle);
        jasperDesign.addStyle(columnHeaderStyle);
  
        int xPos = MARGIN;
        int columnWidth = (TOTAL_PAGE_WIDTH - (SPACE_BETWEEN_COLS * (numColumns - 1))) / numColumns;
  
        for (int i = 0; i < numColumns; i++) {
  
            // Create a Column Field
            JRDesignField field = new JRDesignField();
            field.setName(COL_EXPR_PREFIX + i);
            field.setValueClass(java.lang.String.class);
            jasperDesign.addField(field);
  
            // Create a Header Field
            JRDesignField headerField = new JRDesignField();
            headerField.setName(COL_HEADER_EXPR_PREFIX + i);
            headerField.setValueClass(java.lang.String.class);
            jasperDesign.addField(headerField);
  
            // Add a Header Field to the headerBand
            headerBand.setHeight(BAND_HEIGHT);
            JRDesignTextField colHeaderField = new JRDesignTextField();
            colHeaderField.setX(xPos);
            colHeaderField.setY(2);
            columnWidth=this.widthHeaders.get(i);
            colHeaderField.setWidth(columnWidth);
            colHeaderField.setHeight(COLUMN_HEIGHT);
            colHeaderField.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
            colHeaderField.setStyle(columnHeaderStyle);
            JRDesignExpression headerExpression = new JRDesignExpression();
            headerExpression.setValueClass(java.lang.String.class);
            headerExpression.setText("$F{" + COL_HEADER_EXPR_PREFIX + i + "}");
            colHeaderField.setExpression(headerExpression);
            headerBand.addElement(colHeaderField);
  
            // Add text field to the detailBand
            detailBand.setHeight(BAND_HEIGHT);
            JRDesignTextField textField = new JRDesignTextField();
            textField.setX(xPos);
            textField.setY(2);
            textField.setWidth(columnWidth);
            textField.setHeight(COLUMN_HEIGHT);
            textField.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
            textField.setStyle(normalStyle);
            JRDesignExpression expression = new JRDesignExpression();
            expression.setValueClass(java.lang.String.class);
            expression.setText("$F{" + COL_EXPR_PREFIX + i + "}");
            textField.setExpression(expression);
            detailBand.addElement(textField);
  
            xPos = xPos + columnWidth + SPACE_BETWEEN_COLS;
        }
  
        jasperDesign.setColumnHeader(headerBand);
        ((JRDesignSection)jasperDesign.getDetailSection()).addBand(detailBand);
    }
  
    private JRDesignStyle getNormalStyle() {
        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Times New Roman");
        normalStyle.setDefault(true);
        normalStyle.setFontName("Times New Roman");
        normalStyle.setFontSize(11);
        normalStyle.setPdfFontName("Helvetica");
        normalStyle.setBackcolor(null);
        normalStyle.setForecolor(new Color(0,0,0));  
        normalStyle.setMode(ModeEnum.OPAQUE);
        normalStyle.getLineBox().getPen().setLineColor(new Color(0,0,0));
        normalStyle.getLineBox().getPen().setLineWidth(0.5f);       
        //normalStyle.setPdfEncoding("UTF-8");
        normalStyle.setPdfEmbedded(false);
        return normalStyle;
    }
  
    private JRDesignStyle getColumnHeaderStyle() {
        JRDesignStyle columnHeaderStyle = new JRDesignStyle();
        columnHeaderStyle.setName("Sans_Header");
        columnHeaderStyle.setDefault(false);
        columnHeaderStyle.setFontName("Times New Roman");
        columnHeaderStyle.setFontSize(12);
        columnHeaderStyle.setBackcolor(new Color(240,240,240));
        columnHeaderStyle.setForecolor(new Color(0,0,0));  
        columnHeaderStyle.setMode(ModeEnum.OPAQUE);  
        columnHeaderStyle.getLineBox().getPen().setLineColor(new Color(0,0,0));
        columnHeaderStyle.getLineBox().getPen().setLineWidth(0.5f);       
        columnHeaderStyle.setBold(true);
        columnHeaderStyle.setPdfFontName("Helvetica");
        //columnHeaderStyle.setPdfEncoding("UTF-8");
        columnHeaderStyle.setPdfEmbedded(false);
        return columnHeaderStyle;
    }
  
}

