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
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.payment.PaymentPlan;
import bordomor.odtu.sk.payment.Promotion;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_payment_plan.jsp")
public class PaymentPlanManipulator extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public PaymentPlanManipulator() 
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
			String updateModeStr = request.getParameter("update_mode");
			
			String idStr = request.getParameter("id");
			String promotionIdStr = request.getParameter("promotion");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Loginable user = Loginable.findByStok(conn, stok);
			PaymentPlan processedPlan = new PaymentPlan(idStr != null ? Integer.parseInt(idStr) : -1);
			
			if(user == null || user.getRole() == null || user.getState() != LoginableState.ACTIVE || !user.getRole().getPaymentMod().canWrite())
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(dataModeStr.equals(Params.DATABASE_DELETE_DATA_MODE))
					processedPlan.deleteFromDB(conn);
				else if(dataModeStr.equals(Params.DATABASE_INSERT_DATA_MODE)) {}
	        	else if(dataModeStr.equals(Params.DATABASE_UPDATE_DATA_MODE))
	        	{
	        		if(Integer.parseInt(updateModeStr) == 0)
	        		{
	        			PaymentPlan updatingPlan = processedPlan;
	        			processedPlan.updateInDB(conn, updatingPlan);
	        		}
	        		else if(Integer.parseInt(updateModeStr) == 1)
	        		{
	        			processedPlan = PaymentPlan.findById(conn, processedPlan.getId());
	        			Promotion attachedPromotion = Promotion.findById(conn, Integer.parseInt(promotionIdStr));
	        			processedPlan.attachPromotionInDB(conn, attachedPromotion);
	        		}
	        		else if(Integer.parseInt(updateModeStr) == 2)
	        		{
	        			processedPlan = PaymentPlan.findById(conn, processedPlan.getId());
	        			Promotion detachedPromotion = Promotion.findById(conn, Integer.parseInt(promotionIdStr));
	        			processedPlan.detachPromotionInDB(conn, detachedPromotion);
	        		}
	        	}
	        	else
	        		throw new IllegalArgumentException();
				
				//HttpSession session = request.getSession(false);
				//session.setAttribute("dataMode", dataModeStr);
				//session.setAttribute("itemId", processedPlan.getId());
				
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