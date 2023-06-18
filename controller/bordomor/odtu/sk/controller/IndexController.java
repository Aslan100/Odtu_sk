package bordomor.odtu.sk.controller;

import java.io.IOException;
import java.sql.Connection;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(Params.PORTAL_INDEX_URI)
public class IndexController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

    public IndexController() {}

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
				
				if(loggedUser != null && loggedUser.isLoginPermitted())
					response.sendRedirect(WebUtils.PORTAL_CONTEXT_PATH + loggedUser.getHomeURI());
				else if(loggedUser != null && loggedUser.getState() == LoginableState.PENDING)
					response.sendRedirect(WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_PWD_RESET_URI);
				else
					request.getRequestDispatcher("/view/index.jsp").forward(request, response);
			}
			else
			{
				String error = (String) request.getSession().getAttribute("error");
				
				if(error != null)
				{
					request.getSession().removeAttribute("error");
					request.setAttribute("error", error);
				}
				
				request.getRequestDispatcher("/view/index.jsp").forward(request, response);
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
