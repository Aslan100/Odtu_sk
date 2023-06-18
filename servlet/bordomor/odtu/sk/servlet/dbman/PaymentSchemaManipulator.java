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
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Params.PaymentFailureAction;
import bordomor.odtu.sk.Params.PaymentSchemaType;
import bordomor.odtu.sk.Params.SubscriptionCancellationAction;
import bordomor.odtu.sk.Params.TimeUnit;
import bordomor.odtu.sk.payment.EquidistributedSchema;
import bordomor.odtu.sk.payment.IntervalConstrainedSchema;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.template.PaymentSchema;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_payment_schema.jsp")
public class PaymentSchemaManipulator extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public PaymentSchemaManipulator() 
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
			String typeStr = request.getParameter("type");
			String paymentPeriodStr = request.getParameter("payment_period");
			String periodUnitStr = request.getParameter("period_unit");
			String paymentDayIndexStr = request.getParameter("payment_day_index");
			String paymentDaySubindexStr = request.getParameter("payment_day_subindex");
			String maxUnpaidDaysStr = request.getParameter("max_unpaid_days");
			String failureActionStr = request.getParameter("failure_action");
			String cancellationActionStr = request.getParameter("cancellation_action");
			String autoRenewStr = request.getParameter("auto_renew");
			String maxCancellableDaysStr = request.getParameter("max_cancellable_days");
			String isPriceModificationProtectedStr = request.getParameter("is_price_modification_protected");
			
			String intervalStartDayStr = request.getParameter("interval_start_day");
			String intervalStartMonthStr = request.getParameter("interval_start_month");
			String intervalEndDayStr = request.getParameter("interval_end_day");
			String intervalEndMonthStr = request.getParameter("interval_end_month");
			String installmentsStr = request.getParameter("number_of_installments");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Loginable user = Loginable.findByStok(conn, stok);
			PaymentSchema processedSchema = new IntervalConstrainedSchema(idStr != null ? Integer.parseInt(idStr) : -1);
			
			if(user == null || user.getRole() == null || user.getState() != LoginableState.ACTIVE || !user.getRole().getPaymentMod().canWrite())
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(dataModeStr.equals(Params.DATABASE_DELETE_DATA_MODE))
					processedSchema.deleteFromDB(conn);
				else if(dataModeStr.equals(Params.DATABASE_INSERT_DATA_MODE))
	        	{
					PaymentSchemaType type = PaymentSchemaType.valueOf(typeStr);
					
					if(type == PaymentSchemaType.INTERVAL_CONSTRAINED)
					{
						String intervalStart = (intervalStartDayStr != null && intervalStartMonthStr != null) ? intervalStartDayStr.trim() + "/" + intervalStartMonthStr.trim() : null;
						String intervalEnd = intervalEndDayStr.trim() + "/" + intervalEndMonthStr.trim();
						processedSchema = new IntervalConstrainedSchema(-1, intervalStart, intervalEnd, -1, title.trim(), new Branch(Integer.parseInt(branchIdStr)), type);
					}
					else
						processedSchema = new EquidistributedSchema(-1, Short.parseShort(installmentsStr), -1, title.trim(), new Branch(Integer.parseInt(branchIdStr)), type);
					
					processedSchema.setTimingData(Short.parseShort(paymentPeriodStr), TimeUnit.valueOf(periodUnitStr), Short.parseShort(paymentDayIndexStr), Short.parseShort(paymentDaySubindexStr), 
							Short.parseShort(maxUnpaidDaysStr), Short.parseShort(maxCancellableDaysStr));
					processedSchema.setBehaviour(PaymentFailureAction.valueOf(failureActionStr), SubscriptionCancellationAction.valueOf(cancellationActionStr), Boolean.parseBoolean(autoRenewStr), Boolean.parseBoolean(isPriceModificationProtectedStr));
					processedSchema.createInDB(conn);
	        	}
	        	else if(dataModeStr.equals(Params.DATABASE_UPDATE_DATA_MODE))
	        	{
	        		//processedSchema = PaymentSchema.findById(conn, processedSchema.getId());
	        		
	        		/*PaymentSchema updatingSchema = new PaymentSchema(-1, title.trim());
	        		updatingSchema.setPaymentPeriod(Short.parseShort(paymentPeriodStr));
	        		updatingSchema.setPeriodUnit(TimeUnit.valueOf(periodUnitStr));
	        		updatingSchema.setInstallments(Short.parseShort(installmentsStr));
	        		updatingSchema.setMode(CalculationMode.valueOf(calculationModeStr));
	        		updatingSchema.setImmediateFirstPayment(Boolean.parseBoolean(immediateFirstPaymentStr));
	        		updatingSchema.setEnforcePlanOnCancellation(Boolean.parseBoolean(enforcePlanOnCancellationStr));
	        		updatingSchema.setMaxUnpaidDays(Short.parseShort(maxUnpaidDaysStr));
	        		updatingSchema.setPaymentFailureAction(PaymentFailureAction.valueOf(paymentFailureActionStr));
					
					processedSchema.updateInDB(conn, updatingSchema);*/
				}
	        	else
	        		throw new IllegalArgumentException();
				
				HttpSession session = request.getSession(false);
				session.setAttribute("dataMode", dataModeStr);
				session.setAttribute("itemId", processedSchema.getId());
				
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