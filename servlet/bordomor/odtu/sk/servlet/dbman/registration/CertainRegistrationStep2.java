package bordomor.odtu.sk.servlet.dbman.registration;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bordomor.odtu.sk.Athlete;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.BloodType;
import bordomor.odtu.sk.Params.RegistrationStep;
import bordomor.odtu.sk.Registration;
import bordomor.odtu.sk.template.Loginable.MedicalData;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_certain_registration_s2.jsp")
public class CertainRegistrationStep2 extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public CertainRegistrationStep2() 
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
			String code = request.getParameter("code");
			
			String pastTherapies = request.getParameter("past_therapies");
			String activeIssues = request.getParameter("active_issues");
			String activeMedications = request.getParameter("active_medications");
			String specialCareNeeds = request.getParameter("special_care_needs");
			
			String heightStr = request.getParameter("height");
			String weightStr = request.getParameter("weight");
			String bloodTypeStr = request.getParameter("blood_type");
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			//Kayıt Grubu
			Registration regInProgress = Registration.findByCode(conn, code.trim());
				
			MedicalData medicals = regInProgress.getRegistered().new MedicalData(-1);
			medicals.setPastTherapies(pastTherapies != null && pastTherapies.trim().length() > 0 ? pastTherapies.trim() : null);
			medicals.setActiveIssues(activeIssues != null && activeIssues.trim().length() > 0 ? activeIssues.trim() : null);
			medicals.setActiveMedications(activeMedications != null && activeMedications.trim().length() > 0 ? activeMedications.trim() : null);
			medicals.setSpecialCareNeeds(specialCareNeeds != null && specialCareNeeds.trim().length() > 0 ? specialCareNeeds.trim() : null);
			
			medicals.createInDB(conn);
			regInProgress.setLastCompletedStep(RegistrationStep.MEDICAL_DATA);
    		regInProgress.updateColumnInDB(conn, "last_completed_step", regInProgress.getLastCompletedStep(), "registration_step");
    		
    		Athlete registered = regInProgress.getRegistered();
    		Athlete updatingRegistered = registered;
    		updatingRegistered.setHeight(heightStr != null ? Integer.parseInt(heightStr) : -1);
    		updatingRegistered.setWeight(weightStr != null ? Float.parseFloat(weightStr) : -1f);
    		updatingRegistered.setBloodType(BloodType.valueOf(bloodTypeStr));
    		registered.updateInDB(conn, updatingRegistered);
        	
			out.print(Params.DATA_MANIPULATION_RESULT_STRING);
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