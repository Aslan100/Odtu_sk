package bordomor.odtu.sk.servlet.query;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Registration;
import bordomor.odtu.sk.Training;
import bordomor.util.DBUtils;
import bordomor.util.XMLUtils;

@WebServlet("/query/get_suitable_training_data_for_registration.jsp")
public class SuitableTrainingsForRegisterationDataFetcher extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	public SuitableTrainingsForRegisterationDataFetcher() 
    {
        super();
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out = null;
		Connection conn = null;
		
		try 
        {
			out = response.getWriter();
			String registrationCode = request.getParameter("code");
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Registration reg = Registration.findByCode(conn, registrationCode.trim());
			Training[] trainings = Training.findForAthlete(conn, reg.getRegistered());
				
			if(trainings != null)
			{
				Document xml = XMLUtils.createDocument();
	        	Element parentEl = xml.createElement("query_result");
	        	parentEl.setAttribute("time", new Date().getTime() + "");
        		xml.appendChild(parentEl);
        		
        		Element trainingContainerEl = xml.createElement("trainings");
        		
        		for(Training nextTraining : trainings)
        			trainingContainerEl.appendChild(nextTraining.makeXMLElement(xml, true));
        		
        		parentEl.appendChild(trainingContainerEl);
        		
        		out.print(XMLUtils.convertXMLToString(xml));
			}
			else
				out.print(Params.NO_DATA_ERROR_STRING);
		}
		catch(IllegalArgumentException iae)
		{
			out.print(Params.ILLEGAL_SERVLET_PARAMETER_ERROR_STRING);
		}
		catch(SQLException | ClassNotFoundException dbEx)
		{
			if(dbEx instanceof ClassNotFoundException)
				out.print(Params.DATABASE_CONNECTION_ERROR_STRING);
			else
			{
				SQLException sqlEx = (SQLException)dbEx;
				
				if(sqlEx.getSQLState().equals(Params.DATABASE_CONNECTION_ERROR_CODE))
					out.print(Params.DATABASE_CONNECTION_ERROR_STRING);
				else
					out.print(Params.SQL_EXCEPTION_ERROR_STRING);
			}
		}
		catch (Exception ex)
        {
        	out.print(Params.CODE_ERROR_STRING );
        } 
        finally 
        {
        	DBUtils.close(conn);
        }
	}
}
