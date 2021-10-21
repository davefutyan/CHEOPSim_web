package cheopsim;

import java.io.FileInputStream;
import java.io.IOException;
        
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadXml extends HttpServlet {
        
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

	String filePath =this.getServletContext().getRealPath("/");
	FileInputStream fileToDownload = new FileInputStream(filePath+"/configuration.zip");

	ServletOutputStream output = response.getOutputStream();
	response.setContentType("application/xml");
	response.setHeader("Content-Disposition", "attachment; filename=configuration.zip");
	response.setContentLength(fileToDownload.available());
                        
	byte[] buffer = new byte[4096];
	int length;
	while ((length = fileToDownload.read(buffer)) > 0){
	    output.write(buffer, 0, length);
	}
              
	output.close();
	fileToDownload.close();
    }
}
