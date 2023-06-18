package bordomor.odtu.sk.servlet.dbman.registration;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.RegistrationStep;
import bordomor.odtu.sk.Registration;
import bordomor.odtu.sk.payment.PaymentPlan;
import bordomor.odtu.sk.payment.Promotion;
import bordomor.odtu.sk.template.PaymentSchema;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_certain_registration_s5.jsp")
public class CertainRegistrationStep5 extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public CertainRegistrationStep5() 
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
			
			String code = request.getParameter("code");
			String schemaIdStr = request.getParameter("schema");
			String couponCode = request.getParameter("coupon_code");
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			//Kayıt Grubu
			PaymentSchema schema = PaymentSchema.findById(conn, Integer.parseInt(schemaIdStr));
			Registration regInProgress = Registration.findByCode(conn, code.trim());
    		regInProgress.setPaymentSchema(schema.getSchemaId());
    		
    		PaymentPlan plan = schema.generatePlan(regInProgress.isSiblingRegistration());
    		plan.setLiable(regInProgress.getRegistrant());
    		plan.setPaidFor(regInProgress.getRegistered());
    		plan.setGeneratorSchema(schema);
    		
    		if(couponCode.trim().length() > 0)
    			plan.addPromotion(Promotion.findByCouponCode(conn, couponCode.trim()));	
    		
    		plan.createInDB(conn);
    		regInProgress.setGeneratedPlan(plan.getId());
    		regInProgress.setLastCompletedStep(RegistrationStep.PAYMENT_PLAN);
    		
    		regInProgress.updateColumnInDB(conn, "payment_schema", regInProgress.getPaymentSchema(), Types.INTEGER);
    		regInProgress.updateColumnInDB(conn, "generated_plan", regInProgress.getGeneratedPlan(), Types.INTEGER);
    		regInProgress.updateColumnInDB(conn, "last_completed_step", regInProgress.getLastCompletedStep(), "registration_step");
        	
			out.print(Params.DATA_MANIPULATION_RESULT_STRING);
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