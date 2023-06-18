package bordomor.odtu.sk.servlet.auth;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.DeviceType;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;

import bordomor.util.DBUtils;
import bordomor.util.StringUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/auth/web_login.jsp")
public class WebLogin extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

    public WebLogin() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		throw new ServletException("Bad call for servlet.");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		Connection conn = null;
        
		try
        {	
			String userName = request.getParameter("user_name").trim();
			String pwd = request.getParameter("pwd").trim();
			String sessionStr = request.getParameter("login_type_selector");
			boolean isTempSession = !Boolean.parseBoolean(sessionStr);
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			Loginable user = Loginable.findByEmailAndPwd(conn, userName, pwd);
			
			String forwardURI = WebUtils.PORTAL_CONTEXT_PATH + "/";
			
			if(user == null)
				request.getSession().setAttribute("error", "no_user");
			else if(user.getState() == LoginableState.PENDING_CONFIRMATION)
				request.getSession().setAttribute("error", "unconfirmed_user");
			else if(!user.isLoginPermitted())
				request.getSession().setAttribute("error", "unauthorized_user");
			else
			{
				String ipAddress = request.getHeader("X-FORWARDED-FOR");  
			    
    			if (ipAddress == null)
    				ipAddress = request.getRemoteAddr();
    			
    			String createdStok = this.createSession(conn, user, ipAddress);
    			
    			WebUtils webUtil = new WebUtils(request, response);
    			webUtil.createCookie(Params.SESSION_TOKEN_ABBREVIATION, createdStok, (isTempSession ? -1 : 365*24*60*60));
    			
    			boolean isPending = user.getState() == LoginableState.PENDING;
    			forwardURI = WebUtils.PORTAL_CONTEXT_PATH + (isPending ? Params.PORTAL_PWD_RESET_URI : user.getHomeURI());
    		}
			
			response.sendRedirect(forwardURI);
        }
		catch (Exception ex)
        {
        	throw new ServletException(ex);
        }
        finally 
        {
        	DBUtils.close(conn);
        }
	}
	
	private String createSession(Connection conn, Loginable loggedUser, String ipAddress) throws ClassNotFoundException, SQLException
	{
		PreparedStatement sqlSt = null;
		String stok = null;
		
		while(true)
		{
			try
			{
				stok = StringUtils.generateRandomString(Params.MIN_TOKEN_LENGTH, Params.MAX_TOKEN_LENGTH, true);
					
				String query = ""
						+ "INSERT INTO "
						+ "		session_data "
						+ "			(token, loginable, ip, device_type, session_created) "
						+ "VALUES "
						+ "		(?, ?, ?::inet, ?::device_type, now())";
				sqlSt = conn.prepareStatement(query);
		        sqlSt.setString(1, stok);
		        sqlSt.setInt(2, loggedUser.getLoginableId());
		        sqlSt.setString(3, ipAddress);
		        sqlSt.setString(4, DeviceType.BROWSER.toString());
		        
		        sqlSt.executeUpdate();
		        
		        return stok;
			}
			catch(SQLException sqlEx)
			{
				if(!sqlEx.getSQLState().equals(Params.DATABASE_UNIQUE_CONSTRAINT_ERROR_CODE))
					throw sqlEx;
			}
			finally
			{
				DBUtils.close(sqlSt);
			}
		}
	}
}
