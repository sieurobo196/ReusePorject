package vn.itt.msales.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ChinhNQ
 */
public class MsalesDownloadTemplet {

    private String mFolderpath;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public MsalesDownloadTemplet(String mFolderpath, HttpServletRequest request, HttpServletResponse response) {
        this.mFolderpath = mFolderpath;
        this.request = request;
        this.response = response;
    }

    /**
     * Size of a byte buffer to read/write file
     */
    private static final int BUFFER_SIZE = 4096;

    public OutputStream getFile(String fileName) throws IOException {
        // get absolute path of the application
        ServletContext context = request.getSession().getServletContext();
        String appPath = context.getRealPath("");
        String fullPath = appPath + mFolderpath + fileName;
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

        return outStream;
    }
}
