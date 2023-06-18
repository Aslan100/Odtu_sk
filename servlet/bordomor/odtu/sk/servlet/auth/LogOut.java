package bordomor.odtu.sk.servlet.auth;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@WebServlet("/auth/logout.jsp")
public class LogOut extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
       
    public LogOut() 
    {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		WebUtils webUtil = null; 
		
		try
        {
			webUtil = new WebUtils(request, response);
			String stok = webUtil.getCookieValue(Params.SESSION_TOKEN_ABBREVIATION);
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			webUtil.deleteCookie(Params.SESSION_TOKEN_ABBREVIATION);
			this.destroySession(stok);
			
			response.sendRedirect(WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_INDEX_URI);
        }
		catch(IllegalArgumentException iaex)
		{
			if(webUtil != null)
				webUtil.deleteCookie(Params.SESSION_TOKEN_ABBREVIATION);
			
			response.sendRedirect(request.getContextPath() + Params.PORTAL_INDEX_URI);
		}
		catch(SQLException | ClassNotFoundException dbEx)
		{
			response.sendRedirect(request.getContextPath() + Params.PORTAL_INDEX_URI);
		}
		catch (Exception ex)
        {
        	throw new ServletException(ex);
    	}
	}
	
	private int destroySession(String token) throws Exception
	{
		Connection conn = null;
		PreparedStatement sqlSt = null;
		
		try
		{
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			String query = "DELETE FROM session_data WHERE token=?";
	        sqlSt = conn.prepareStatement(query);
	        sqlSt.setString(1, token);
	        int deletedRowCount = sqlSt.executeUpdate();
	        
	        return deletedRowCount;
		}
		finally
		{
			DBUtils.close(sqlSt);
			DBUtils.close(conn);
		}
    }
}