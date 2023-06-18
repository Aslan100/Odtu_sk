package bordomor.odtu.sk.controller.registration;

import java.io.IOException;
import java.sql.Connection;

import bordomor.odtu.sk.Params;
import bordomor.util.DBUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(Params.PORTAL_REGISTER_INDEX_URI)
public class IndexController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

    public IndexController() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		Connection conn = null;
		
		try
		{
			request.getRequestDispatcher("/view/registration/index.jsp").forward(request, response);
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