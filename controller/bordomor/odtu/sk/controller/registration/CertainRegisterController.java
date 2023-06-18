package bordomor.odtu.sk.controller.registration;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Date;

import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Registration;
import bordomor.odtu.sk.Params.BloodType;
import bordomor.odtu.sk.Params.Gender;
import bordomor.odtu.sk.Params.ParenthoodTitle;
import bordomor.odtu.sk.Params.RegistrationState;
import bordomor.odtu.sk.Params.RegistrationType;
import bordomor.odtu.sk.location.City;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import bordomor.util.StringUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(Params.PORTAL_CERTAIN_REGISTER_URI)
public class CertainRegisterController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

    public CertainRegisterController() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		Connection conn = null;
		
		try
		{
			WebUtils utils = new WebUtils(request, response);
			
			String code = request.getParameter("code");
			String cookieCode = utils.getCookieValue("registration_code");
			
			Registration newReg = null;
			
			if(code != null && code.trim().length() > 0)
				newReg = Registration.findByCode(conn, code);
			else if(cookieCode != null && cookieCode.trim().length() > 0)
			{
				code = cookieCode;
				newReg = Registration.findByCode(conn, code);
				
				if(newReg == null || newReg.getState() == RegistrationState.COMPLETED)
					utils.deleteCookie("registration_code");
			}
			
			if(newReg == null || newReg.getState() == RegistrationState.COMPLETED)
			{
				newReg = new Registration(-1, RegistrationType.CERTAIN, RegistrationState.INITIALIZED);
				newReg.createInDB(conn);
				
				utils.createCookie("registration_code", newReg.getCode(), -1);
			}
			
			City[] allCities = City.findAll(conn, true);
			Branch[] allBranches = Branch.findAll(conn);

			request.setAttribute("newReg", newReg);
			
			request.setAttribute("registrationCode", code);
			request.setAttribute("cities", allCities);
			request.setAttribute("branches", allBranches);
			request.setAttribute("genders", Gender.values());
			request.setAttribute("bloodTypes", BloodType.values());
			request.setAttribute("parenthoodTitles", ParenthoodTitle.values());
			
			request.getRequestDispatcher("/view/registration/certain/index.jsp").forward(request, response);
		}
		catch(Exception ex)
		{
			throw new ServletException(ex);
		}
		finally
		{
			DBUtils.close(conn);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		throw new ServletException("Bad call for servlet");
	}
}