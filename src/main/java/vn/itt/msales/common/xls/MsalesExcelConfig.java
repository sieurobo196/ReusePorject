package vn.itt.msales.common.xls;

import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ChinhNQ
 */
public class MsalesExcelConfig {

	private int sheetIndex;
    /* number column you want ro read. */
    private int numberColumns;
    /* Begin row you want to read. */
    private int beginRow;
    /* This is data file you want read*/
    private MultipartFile mMultipartFile;

    public MsalesExcelConfig(int numberColumns, int beginRow, MultipartFile mMultipartFile) {
        this.numberColumns = numberColumns;
        this.beginRow = beginRow;
        this.mMultipartFile = mMultipartFile;
    }
  
    public MsalesExcelConfig(int sheetIndex, int numberColumns, int beginRow, MultipartFile mMultipartFile) {
		this.sheetIndex = sheetIndex;
		this.numberColumns = numberColumns;
		this.beginRow = beginRow;
		this.mMultipartFile = mMultipartFile;
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public int getNumberColumns() {
        return numberColumns;
    }

    public void setNumberColumns(int numberColumns) {
        this.numberColumns = numberColumns;
    }

    public int getBeginRow() {
        return beginRow;
    }

    public void setBeginRow(int beginRow) {
        this.beginRow = beginRow;
    }

    public MultipartFile getmMultipartFile() {
        return mMultipartFile;
    }

    public void setmMultipartFile(MultipartFile mMultipartFile) {
        this.mMultipartFile = mMultipartFile;
    }

}
