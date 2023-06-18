package bordomor.odtu.sk.controller;

import java.io.IOException;
import java.sql.Connection;

import bordomor.odtu.sk.Athlete;
import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.LoginRole;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.AgeGroup;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Team;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(Params.PORTAL_ATHLETES_URI)
public class AthletesController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

    public AthletesController() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		Connection conn = null;
		
		try
		{
			String stok = new WebUtils(request, response).getCookieValue("stok");
			
			if(stok != null)
			{
				conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
				Loginable loggedUser = Loginable.findByStok(conn, stok);
				
				if(!isUserAllowedOnPage(loggedUser))
				{
					if(loggedUser != null)
						Loginable.deleteLoginableSession(conn, stok);
						
					response.sendError(403);
				}
				else
				{
					Athlete[] athletes = Athlete.findAll(conn);
					Branch[] branches = bordomor.odtu.sk.Branch.findAll(conn);
					AgeGroup[] ageGroups = AgeGroup.values();
					Team[] teams = Team.findAll(conn);
					
					HttpSession session = request.getSession();
					
					if(session.getAttribute("dataMode") != null)
					{
						String dataMode = (String) session.getAttribute("dataMode");
						session.removeAttribute("dataMode");
						request.setAttribute("dataMode", dataMode);
					}
					
					if(session.getAttribute("itemId") != null)
					{
						int itemId = (int) session.getAttribute("itemId");
						session.removeAttribute("itemId");
						request.setAttribute("itemId", itemId);
					}
					
					request.setAttribute("athletes", athletes);
					request.setAttribute("branches", branches);
					request.setAttribute("ageGroups", ageGroups);
					request.setAttribute("teams", teams);
					
					request.getRequestDispatcher("/view/athletes.jsp").forward(request, response);
				}
			}
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
	
	private boolean isUserAllowedOnPage(Loginable loggedUser)
	{
		if(loggedUser != null && loggedUser.getState() == LoginableState.ACTIVE)
		{
			LoginRole role = loggedUser.getRole();
			return (role != null && role.getAthleteMod().canRead());
		}
		else
			return false;
	}
}
