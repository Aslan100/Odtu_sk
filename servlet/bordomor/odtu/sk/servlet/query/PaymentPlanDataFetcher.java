package bordomor.odtu.sk.servlet.query;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bordomor.odtu.sk.Athlete;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.payment.PaymentPlan;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import bordomor.util.XMLUtils;

@WebServlet("/query/get_payment_plan_data.jsp")
public class PaymentPlanDataFetcher extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	public PaymentPlanDataFetcher() 
    {
        super();
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
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
			String idStr = request.getParameter("id");
			String athleteIdStr = request.getParameter("athlete");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			Loginable user = Loginable.findByStok(conn, stok);
			
			if(user == null || user.getRole() == null || !user.getRole().getPaymentMod().canRead())
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				PaymentPlan[] plans = null;
				
				if(idStr != null || athleteIdStr != null)
				{
					PaymentPlan plan = idStr != null ? PaymentPlan.findById(conn, Integer.parseInt(idStr)) 
							: PaymentPlan.findByPaidFor_MostRecent(conn, new Athlete(Integer.parseInt(athleteIdStr)));
					
					plans = plan != null ? new PaymentPlan[] { plan } : null;
				}
				else
					plans = PaymentPlan.findAll(conn);
				
				if(plans != null)
				{
					Document xml = XMLUtils.createDocument();
		        	Element parentEl = xml.createElement("query_result");
		        	parentEl.setAttribute("time", new Date().getTime() + "");
	        		xml.appendChild(parentEl);
	        		
	        		Element planContainerEl = xml.createElement("payment_plans");
	        		
	        		for(PaymentPlan nextPlan : plans)
	        			planContainerEl.appendChild(nextPlan.makeXMLElement(xml, true));
	        		
	        		parentEl.appendChild(planContainerEl);
	        		
	        		out.print(XMLUtils.convertXMLToString(xml));
				}
				else
					out.print(Params.NO_DATA_ERROR_STRING);
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
        	out.print(Params.CODE_ERROR_STRING );
        } 
        finally 
        {
        	DBUtils.close(conn);
        }
	}
}
