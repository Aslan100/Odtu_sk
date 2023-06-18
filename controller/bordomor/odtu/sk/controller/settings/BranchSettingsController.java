package bordomor.odtu.sk.controller.settings;

import java.io.IOException;
import java.sql.Connection;

import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.BranchManager;
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

@WebServlet(Params.PORTAL_BRANCH_SETTINGS_URI)
public class BranchSettingsController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

    public BranchSettingsController() {}

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
				Loginable loggedUser = Loginable.findByStok(conn, stok);
				
				if(loggedUser == null || !loggedUser.isLoginPermitted() || !loggedUser.getRole().getBranchMod().canRead())
				{
					Loginable.deleteLoginableSession(conn, stok);
					response.sendRedirect(WebUtils.PORTAL_CONTEXT_PATH + "/");
				}
				else
				{
					Branch[] branches = loggedUser instanceof ClubManager ? Branch.findAll(conn) : new Branch[] { ((BranchManager) loggedUser).getBranch() };
					request.setAttribute("branches", branches);
					request.getRequestDispatcher("/view/settings/branch_settings.jsp").forward(request, response);
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