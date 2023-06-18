package bordomor.odtu.sk.controller.registration;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Registration;
import bordomor.odtu.sk.payment.PaymentPlan;
import bordomor.util.DBUtils;
import bordomor.util.StringUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(Params.PORTAL_PAYMENT_URI)
public class PaymentController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

    public PaymentController() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		Connection conn = null;
		
		try
		{
			String code =request.getParameter("code");
			
			Registration reg = Registration.findByCode(conn, code);
			PaymentPlan selectedPlan = PaymentPlan.findById(conn, reg.getGeneratedPlan());
			float amount = selectedPlan.getPayments()[0].getAmount();
			
			request.setAttribute("reg", reg);
			request.setAttribute("registrationCode", code);
			
			/*Ödeme İçin Gerekli Parametreler*/
			int thisYear = LocalDate.now().getYear();
			int[] years = new int[10];
			
			for(int i = 0; i < years.length; i++)
				years[i] = thisYear + i;
			
			request.setAttribute("years", years);
			request.setAttribute("months", Params.MONTH_VALS);
			request.setAttribute("amount", amount);
			/*Ödeme İçin Gerekli Parametreler Bitiş*/
			
			
			String clientId = "700655000100";
			String storeKey = "TRPS0100";
			String orderId = "12345679" + (int) (Math.round(Math.random()*1000));
			String rnd = StringUtils.generateRandomString(17, false) + Math.round(Math.random()*1000);
			String okURL = "http://100.87.138.63:8080/ODTU_SK/result/3ds_success.jsp";
			String failURL = "http://100.87.138.63:8080/ODTU_SK/result/3ds_failure.jsp";
			String transactionType = "Auth";
			String model = "3d";
			
			String hashStr = clientId + orderId + amount + okURL + failURL + transactionType + rnd + storeKey;
			java.security.MessageDigest sha1 = java.security.MessageDigest.getInstance("SHA-1");
			String hash = Base64.getEncoder().encodeToString(sha1.digest(hashStr.getBytes()));
			
			request.setAttribute("hash", hash);
			request.setAttribute("clientId", clientId);
			request.setAttribute("orderId", orderId);
			request.setAttribute("storeKey", storeKey);
			request.setAttribute("model", model);
			request.setAttribute("type", transactionType);
			
			request.setAttribute("okURL", okURL);
			request.setAttribute("failURL", failURL);
			request.setAttribute("rnd", rnd);
			
			request.getRequestDispatcher("/view/registration/payment.jsp").forward(request, response);
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