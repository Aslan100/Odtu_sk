package bordomor.odtu.sk.controller.result.payment;

import java.io.BufferedReader;
import java.io.IOException;
import bordomor.odtu.sk.Params;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(Params.PORTAL_3DS_FAILURE_URI)
public class Payment3DSFailureController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

    public Payment3DSFailureController() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		throw new ServletException("Bad call for servlet.");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		System.out.println("Failure new");
		
		StringBuilder builder = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    
	    char[] charBuffer = new char[2048];
	    int bytesRead = -1;
	    
	    while ((bytesRead = reader.read(charBuffer)) > 0) 
	    	builder.append(charBuffer, 0, bytesRead);
	        
	    String[] valuePairs = builder.toString().split("&");
	    
	    for(String nextValuePair : valuePairs)
	    {
	    	String[] nextPair = nextValuePair.split("=");
	    	String pairStr = nextValuePair.split("=")[0];
	    	pairStr += nextPair.length > 1 ? " --> " + nextPair[1] : "";
	    	
	    	System.out.println(pairStr);
	    }
	    
	    reader.close();
	}
}
