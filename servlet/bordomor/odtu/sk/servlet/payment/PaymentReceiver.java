package bordomor.odtu.sk.servlet.payment;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

import com.est.jpay;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Registration;
import bordomor.odtu.sk.Params.RegistrationState;
import bordomor.odtu.sk.Params.RegistrationStep;
import bordomor.odtu.sk.payment.PaymentPlan;
import bordomor.util.DBUtils;

@WebServlet("/payment/receive.jsp")
public class PaymentReceiver extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public PaymentReceiver() 
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
			
			String regCode = request.getParameter("reg_code");
			
			String pan = request.getParameter("pan");
			String cvv2 = request.getParameter("cvv2");
			/*String cardHolder = request.getParameter("card_holder");*/
			String expiryMonth = request.getParameter("expiry_month");
			String expiryYear = request.getParameter("expiry_year");
			String amount = request.getParameter("amount");
			String currency = request.getParameter("currency");
			
			jpay paymentReceiver = new jpay();
			
			paymentReceiver.setName("odtuapiuser");
			paymentReceiver.setPassword("A_12345678");
			
			paymentReceiver.setClientId("700655000100");
			//paymentReceiver.setOrderId("SPR-" + LocalDate.now().getYear() + "-" + StringUtils.generateRandomString(5, true));
			paymentReceiver.setType("Auth");
			paymentReceiver.setTotal(amount);
			paymentReceiver.setCurrency(currency);
			
			paymentReceiver.setNumber(pan);
			paymentReceiver.setCvv2Val(cvv2);
			paymentReceiver.setExpires(expiryMonth + "/" + expiryYear);
			
			if(paymentReceiver.processTransaction("entegrasyon.asseco-see.com.tr", 443, "/fim/api") > 0)
			{
				//Kayıt Grubu
				Registration regInProgress = Registration.findByCode(conn, regCode.trim());
				PaymentPlan generatedPlan = PaymentPlan.findById(conn, regInProgress.getGeneratedPlan());
				generatedPlan.updateColumnInDB(conn, "is_definitive", true, Types.BOOLEAN);
				generatedPlan.getPayments()[0].updateColumnInDB(conn, "paid_amount", generatedPlan.getPayments()[0].getAmount(), Types.FLOAT);
				generatedPlan.getPayments()[0].updateColumnInDB(conn, "paid_date", Timestamp.valueOf(LocalDateTime.now()), Types.TIMESTAMP);
				
				//Update first payment
				regInProgress.setLastCompletedStep(RegistrationStep.PAYMENT);
				regInProgress.setState(RegistrationState.COMPLETED);
				
				regInProgress.updateColumnInDB(conn, "last_completed_step", regInProgress.getLastCompletedStep(), "registration_step");
				regInProgress.updateColumnInDB(conn, "state", regInProgress.getState(), "registration_state");
				
				System.out.println("Ödeme Alındı");
			}
			else 
				System.out.println(paymentReceiver.getResponse() + ": " + paymentReceiver.getErrMsg());
			
			/*HttpSession session = request.getSession(false);
			session.setAttribute("dataMode", dataModeStr);
			session.setAttribute("itemId", processedAthlete.getId());*/
			
			out.print("Done");
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