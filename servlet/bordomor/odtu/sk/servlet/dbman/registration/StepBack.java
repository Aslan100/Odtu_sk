package bordomor.odtu.sk.servlet.dbman.registration;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.RegistrationStep;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.odtu.sk.Registration;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_certain_registration_step_back.jsp")
public class StepBack extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public StepBack() 
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
			String isCancellationStr = request.getParameter("is_cancellation");
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			/*Kayıt Grubu*/
			Registration regInProgress = Registration.findByCode(conn, code.trim());
			RegistrationStep step = regInProgress.getLastCompletedStep();
			
			if(step == null || Boolean.parseBoolean(isCancellationStr))
			{
				regInProgress.deleteFromDB(conn);
				regInProgress.setLastCompletedStep(null);
				new WebUtils(request, response).deleteCookie("registration_code");
			}
			else if(step == RegistrationStep.ATHLETE_DATA)
			{
				regInProgress.updateColumnInDB(conn, "registered", null, Types.INTEGER);
				regInProgress.getRegistered().deleteFromDB(conn);
				regInProgress.updateColumnInDB(conn, "last_completed_step", null, "registration_step");
				
				regInProgress.setRegistered(null);
				regInProgress.setLastCompletedStep(null);
			}
			else if(step == RegistrationStep.MEDICAL_DATA)
			{
				regInProgress.getRegistered().fetchMedicalDataFromDB(conn);
				regInProgress.getRegistered().getMedicals().deleteFromDB(conn);
				regInProgress.setLastCompletedStep(RegistrationStep.ATHLETE_DATA);
				regInProgress.updateColumnInDB(conn, "last_completed_step", RegistrationStep.ATHLETE_DATA, "registration_step");
			}
			else if(step == RegistrationStep.PARENT_DATA)
			{
				regInProgress.updateColumnInDB(conn, "registrant", null, Types.INTEGER);
				
				if(!regInProgress.getRegistrant().hasChildren())
					regInProgress.getRegistrant().deleteFromDB(conn);
				
				regInProgress.updateColumnInDB(conn, "last_completed_step", RegistrationStep.MEDICAL_DATA, "registration_step");
				
				regInProgress.setRegistrant(null);
				regInProgress.setLastCompletedStep(RegistrationStep.MEDICAL_DATA);
			}
			else if(step == RegistrationStep.TRAINING_SELECTION)
			{
				regInProgress.updateColumnInDB(conn, "first_training", null, Types.INTEGER);
				regInProgress.updateColumnInDB(conn, "last_completed_step", RegistrationStep.PARENT_DATA, "registration_step");
				
				regInProgress.setFirstTraining(null);
				regInProgress.setLastCompletedStep(RegistrationStep.PARENT_DATA);
			}
			else if(step == RegistrationStep.PAYMENT_PLAN)
			{
				regInProgress.updateColumnInDB(conn, "payment_schema", null, Types.INTEGER);
				regInProgress.updateColumnInDB(conn, "generated_plan", null, Types.INTEGER);
				regInProgress.updateColumnInDB(conn, "last_completed_step", RegistrationStep.TRAINING_SELECTION, "registration_step");
				
				regInProgress.setPaymentSchema(-1);
				regInProgress.setGeneratedPlan(-1);
				regInProgress.setLastCompletedStep(RegistrationStep.TRAINING_SELECTION);
			}
			
			int toStep = regInProgress.getLastCompletedStep() != null ? regInProgress.getLastCompletedStep().ordinal() : -1;
			out.print(Params.DATA_MANIPULATION_RESULT_STRING + "-" + toStep);
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