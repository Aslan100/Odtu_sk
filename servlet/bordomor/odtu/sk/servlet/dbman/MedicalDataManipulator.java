package bordomor.odtu.sk.servlet.dbman;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Trainer;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.template.Loginable.MedicalData;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_medical_data.jsp")
@MultipartConfig
public class MedicalDataManipulator extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public MedicalDataManipulator() 
    {
        super();
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		throw new ServletException("Bad call for servlet.");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out = null;
        Connection conn = null;
        
		try 
        {
			out = response.getWriter();
			
			String stok = new WebUtils(request, response).getCookieValue("stok");
			String dataModeStr = request.getParameter("data_mode");
			
			String idStr = request.getParameter("id");
			
			String loginableIdStr = request.getParameter("loginable");
			String pastTherapies = request.getParameter("past_therapies");
			String activeIssues = request.getParameter("active_issues");
			String activeMedications = request.getParameter("active_medications");
			String specialCareNeeds = request.getParameter("special_care_needs");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Loginable user = Loginable.findByStok(conn, stok);
			Loginable processedLoginable = Loginable.findByLoginableId(conn, Integer.parseInt(loginableIdStr));
			MedicalData processedData = processedLoginable.new MedicalData(idStr != null ? Integer.parseInt(idStr) : -1);
			
			if(user == null || user.getRole() == null || !user.getRole().getUserMod().canWrite())
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(dataModeStr.equals(Params.DATABASE_DELETE_DATA_MODE))
					processedData.deleteFromDB(conn);
				else if(dataModeStr.equals(Params.DATABASE_INSERT_DATA_MODE))
	        	{
					processedData.setPastTherapies(pastTherapies.trim().length() > 0 ? pastTherapies.trim() : null);
					processedData.setActiveIssues(activeIssues.trim().length() > 0 ? activeIssues.trim() : null);
					processedData.setActiveMedications(activeMedications.trim().length() > 0 ? activeMedications.trim() : null);
					processedData.setSpecialCareNeeds(specialCareNeeds.trim().length() > 0 ? specialCareNeeds.trim() : null);
					
					processedData.createInDB(conn);
	        	}
	        	else if(dataModeStr.equals(Params.DATABASE_UPDATE_DATA_MODE))
	        	{
	        		Loginable updatingLoginable = new Trainer();
	        		MedicalData updatingData = updatingLoginable.new MedicalData(-1);
	        		updatingData.setPastTherapies(pastTherapies.trim().length() > 0 ? pastTherapies.trim() : null);
	        		updatingData.setActiveIssues(activeIssues.trim().length() > 0 ? activeIssues.trim() : null);
	        		updatingData.setActiveMedications(activeMedications.trim().length() > 0 ? activeMedications.trim() : null);
	        		updatingData.setSpecialCareNeeds(specialCareNeeds.trim().length() > 0 ? specialCareNeeds.trim() : null);
					
	        		processedData.updateInDB(conn, updatingData);
	        	}
	        	else
	        		throw new IllegalArgumentException();
				
				HttpSession session = request.getSession(false);
				session.setAttribute("dataMode", dataModeStr);
				session.setAttribute("itemId", processedData.getId());
				
				out.print(Params.DATA_MANIPULATION_RESULT_STRING);
			}
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
        	out.print(Params.CODE_ERROR_STRING);
        } 
        finally 
        {
        	DBUtils.close(conn);
        }
	}
}