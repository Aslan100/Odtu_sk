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

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Registration;
import bordomor.odtu.sk.payment.PaymentPlan;
import bordomor.odtu.sk.payment.Promotion;
import bordomor.odtu.sk.template.PaymentSchema;
import bordomor.util.DBUtils;
import bordomor.util.XMLUtils;

@WebServlet("/query/get_payment_plan_for_registration.jsp")
public class PaymentPlanForRegistrationDataFetcher extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	public PaymentPlanForRegistrationDataFetcher() 
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
			String registrationCode = request.getParameter("code");
			String schemaIdStr = request.getParameter("schema");
			String couponCode = request.getParameter("coupon_code");
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Registration reg = Registration.findByCode(conn, registrationCode.trim());
			PaymentSchema schema = PaymentSchema.findById(conn, Integer.parseInt(schemaIdStr));
			PaymentPlan plan = schema.generatePlan(reg.isSiblingRegistration());
			
			if(couponCode != null)
			{
				Promotion appliedPromotion = Promotion.findByCouponCode(conn, couponCode); 
				
				if(appliedPromotion != null)
					plan.addPromotion(appliedPromotion);
			}
			
			if(plan != null)
			{
				plan.setLiable(reg.getRegistrant());
				plan.setPaidFor(reg.getRegistered());
					
				Document xml = XMLUtils.createDocument();
	        	Element parentEl = xml.createElement("query_result");
	        	parentEl.setAttribute("time", new Date().getTime() + "");
        		xml.appendChild(parentEl);
        		
        		parentEl.appendChild(plan.makeXMLElement(xml, true));
        		
        		out.print(XMLUtils.convertXMLToString(xml));
			}
			else
				out.print(Params.NO_DATA_ERROR_STRING);
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
