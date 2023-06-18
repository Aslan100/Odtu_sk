package bordomor.odtu.sk.servlet.dbman;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_branch.jsp")
public class BranchManipulator extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public BranchManipulator() 
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
			
			String stok = new WebUtils(request, response).getCookieValue("stok");
			String dataModeStr = request.getParameter("data_mode");
			
			String idStr = request.getParameter("id");
			String title = request.getParameter("title");
			String dailyPriceStr = request.getParameter("daily_price");
			String weeklyPriceStr = request.getParameter("weekly_price");
			String monthlyPriceStr = request.getParameter("monthly_price");
			String annualPriceStr = request.getParameter("annual_price");
			String penaltyRateStr = request.getParameter("penalty_rate");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Loginable user = Loginable.findByStok(conn, stok);
			Branch processedBranch = new Branch(idStr != null ? Integer.parseInt(idStr) : -1);
			
			if(user == null || !user.canOperateOn(conn, processedBranch))
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(dataModeStr.equals(Params.DATABASE_DELETE_DATA_MODE))
					processedBranch.deleteFromDB(conn);
				else if(dataModeStr.equals(Params.DATABASE_INSERT_DATA_MODE))
	        	{
	        		processedBranch = new Branch(-1, title.trim());
        			processedBranch.setPrices(new float[] { Float.parseFloat(dailyPriceStr), Float.parseFloat(weeklyPriceStr), Float.parseFloat(monthlyPriceStr), Float.parseFloat(annualPriceStr) });
        			processedBranch.setPenaltyRate(Float.parseFloat(penaltyRateStr)/100);
        			
        			processedBranch.createInDB(conn);
	        	}
	        	else if(dataModeStr.equals(Params.DATABASE_UPDATE_DATA_MODE))
	        	{
	        		Branch updatingBranch = new Branch(-1, title.trim());
	        		updatingBranch.setPrices(new float[] { Float.parseFloat(dailyPriceStr), Float.parseFloat(weeklyPriceStr), Float.parseFloat(monthlyPriceStr), Float.parseFloat(annualPriceStr) });
	        		updatingBranch.setPenaltyRate(Float.parseFloat(penaltyRateStr)/100);
        			processedBranch.updateInDB(conn, updatingBranch);
	        	}
	        	else
	        		throw new IllegalArgumentException();
				
				HttpSession session = request.getSession(true);
				session.setAttribute("dataMode", dataModeStr);
				session.setAttribute("itemId", processedBranch.getId());
				
				out.print(Params.DATA_MANIPULATION_RESULT_STRING);
			}
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