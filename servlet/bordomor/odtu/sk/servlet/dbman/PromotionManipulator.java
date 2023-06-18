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
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.payment.Promotion;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_promotion.jsp")
public class PromotionManipulator extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public PromotionManipulator() 
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
			String branchIdStr = request.getParameter("branch");
			String discountRatioStr = request.getParameter("discount_ratio");
			String discountAmountStr = request.getParameter("discount_amount");
			String overridesOthersStr = request.getParameter("overrides_others");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Loginable user = Loginable.findByStok(conn, stok);
			Promotion processedPromotion = new Promotion(idStr != null ? Integer.parseInt(idStr) : -1);
			
			if(user == null || user.getRole() == null || user.getState() != LoginableState.ACTIVE || !user.getRole().getPaymentMod().canWrite())
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(dataModeStr.equals(Params.DATABASE_DELETE_DATA_MODE))
					processedPromotion.deleteFromDB(conn);
				else if(dataModeStr.equals(Params.DATABASE_INSERT_DATA_MODE))
	        	{
					float discountRatio = discountRatioStr.trim().length() > 0 ? Float.parseFloat(discountRatioStr)/100 : 0;
					float discountAmount = discountAmountStr.trim().length() > 0 ? Float.parseFloat(discountAmountStr) : 0;
					
					processedPromotion = new Promotion(-1, title.trim(), Integer.parseInt(branchIdStr), false, discountRatio, discountAmount, Boolean.parseBoolean(overridesOthersStr));
					processedPromotion.createInDB(conn);
	        	}
	        	else if(dataModeStr.equals(Params.DATABASE_UPDATE_DATA_MODE))
	        	{
	        		float newDiscountRatio = discountRatioStr.trim().length() > 0 ? Float.parseFloat(discountRatioStr)/100 : 0;
					float newDiscountAmount = discountAmountStr.trim().length() > 0 ? Float.parseFloat(discountAmountStr) : 0;
					
	        		Promotion updatingPromotion = new Promotion(-1, title.trim(), Integer.parseInt(branchIdStr), false, newDiscountRatio, newDiscountAmount, Boolean.parseBoolean(overridesOthersStr));
	        		processedPromotion.updateInDB(conn, updatingPromotion);
	        	}
	        	else
	        		throw new IllegalArgumentException();
				
				HttpSession session = request.getSession(false);
				session.setAttribute("dataMode", dataModeStr);
				session.setAttribute("itemId", processedPromotion.getId());
				
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