package bordomor.odtu.sk.controller.home;

import java.io.IOException;
import java.sql.Connection;

import bordomor.odtu.sk.ClubManager;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(Params.PORTAL_CLUB_MANAGER_HOME_URI)
public class ClubManagerHomeController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

    public ClubManagerHomeController() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		Connection conn = null;
		
		try
		{
			String stok = new WebUtils(request, response).getCookieValue("stok");
			
			if(stok == null)
				response.sendRedirect(WebUtils.PORTAL_CONTEXT_PATH + "/");
			else
			{
				conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
				ClubManager loggedUser = ClubManager.findByStok(conn, stok);
				
				if(loggedUser == null || !loggedUser.isLoginPermitted())
				{
					Loginable.deleteLoginableSession(conn, stok);
					response.sendRedirect(WebUtils.PORTAL_CONTEXT_PATH + "/");
				}
				else
				{
					request.setAttribute("user", loggedUser);
					request.setAttribute("locale", Params.DEFAULT_LOCALE);
					request.getRequestDispatcher("/view/club_manager/home.jsp").forward(request, response);
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
}