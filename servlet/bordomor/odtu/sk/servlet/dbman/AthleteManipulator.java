package bordomor.odtu.sk.servlet.dbman;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import bordomor.odtu.sk.Athlete;
import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.BloodType;
import bordomor.odtu.sk.Params.Gender;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Params.MembershipState;
import bordomor.odtu.sk.Params.PaymentState;
import bordomor.odtu.sk.Params.SubscriptionCancellationAction;
import bordomor.odtu.sk.Registration;
import bordomor.odtu.sk.Team;
import bordomor.odtu.sk.payment.Payment;
import bordomor.odtu.sk.payment.PaymentPlan;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.template.PaymentSchema;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_athlete.jsp")
@MultipartConfig
public class AthleteManipulator extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public AthleteManipulator() 
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
			String code = request.getParameter("code");
			
			String email = request.getParameter("email");
			String name = request.getParameter("name");
			String surname = request.getParameter("surname");
			String genderStr = request.getParameter("gender");
			String hesCode = request.getParameter("hes_code");
			String phoneNumber = request.getParameter("phone_number");
			
			//String heightStr = request.getParameter("height");
			//String weightStr = request.getParameter("weight");
			String birthDateStr = request.getParameter("birth_date");
			String bloodTypeStr = request.getParameter("blood_type");
			String primaryBranchIdStr = request.getParameter("primary_branch");
			String teamIdStr = request.getParameter("primary_team");
			//String idNo = request.getParameter("id_no");
			//String school = request.getParameter("school");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Loginable user = Loginable.findByStok(conn, stok);
			Athlete processedAthlete = new Athlete(idStr != null ? Integer.parseInt(idStr) : -1);
			
			if(user == null || !user.canOperateOn(conn, processedAthlete))
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(dataModeStr.equals(Params.DATABASE_DELETE_DATA_MODE))
					processedAthlete.deleteFromDB(conn);
				else if(dataModeStr.equals(Params.DATABASE_INSERT_DATA_MODE))
	        	{
	        		if(this.getPoster(null))
	        		{
			            processedAthlete = new Athlete(-1, -1, code, email, name, surname, Gender.valueOf(genderStr), phoneNumber, LoginableState.PENDING);
			            processedAthlete.setBloodType(BloodType.valueOf(bloodTypeStr));
			            processedAthlete.setBirthDate(new Timestamp(Params.DATE_FORMAT.parse(birthDateStr).getTime()));
			            processedAthlete.setPrimaryBranch(new Branch(Integer.parseInt(primaryBranchIdStr)));
			            processedAthlete.setPrimaryTeam(new Team(Integer.parseInt(teamIdStr)));
			            processedAthlete.setHesCode(hesCode != null ? hesCode.trim() : null);
		        		processedAthlete.createInDB(conn);
	        		}
	        		else
	        			throw new IllegalArgumentException("Bad poster file data.");
	        	}
	        	else if(dataModeStr.equals(Params.DATABASE_UPDATE_DATA_MODE))
	        	{
	        		int updateMode = Integer.parseInt(updateModeStr);
	        		
	        		if(updateMode == 0)
	        		{
	        			//Hesabı Askıya Alma
	        		}
	        		else if(updateMode == 1)
	        		{
	        			//Hesap Etkinleştirme
	        		}
	        		else if(updateMode == 2 || updateMode == 3)
	        		{
	        			PaymentPlan[] plans = PaymentPlan.findByPaidFor(conn, processedAthlete);
	        			
	        			for(PaymentPlan nextPlan : plans)
	        			{
	        				for(Payment nextPayment : nextPlan.getPayments())
	        				{
	        					PaymentState nextState = nextPayment.getState();
	        					LocalDateTime nextDueDT = nextPayment.getDueDate().toLocalDateTime().plusDays(1).minusSeconds(1);
	        					LocalDateTime comparedDT = LocalDate.now().plusDays(1).atStartOfDay();
	        					
	        					if(nextDueDT.isAfter(comparedDT) && nextState != PaymentState.PAID)
	        						nextPayment.updateColumnInDB(conn, "is_pacified", true, Types.BOOLEAN);
	        					else if(updateMode == 3 && !nextDueDT.isAfter(comparedDT) && nextState != PaymentState.PAID)
	        						nextPayment.updateColumnInDB(conn, "is_pacified", true, Types.BOOLEAN);
	        				}
	        			}
	        			
	        			processedAthlete.updateColumnInDB(conn, "membership", MembershipState.FROZEN, "membership_state");
	        		}
	        		else if(updateMode == 4)
	        		{
	        			PaymentPlan[] plans = PaymentPlan.findByPaidFor(conn, processedAthlete);
	        			
	        			for(PaymentPlan nextPlan : plans)
	        			{
	        				if(nextPlan.isInEffect())
	        				{
	        					for(Payment nextPayment : nextPlan.getPayments())
		        				{
		        					PaymentState nextState = nextPayment.getState();
		        					LocalDateTime nextDueDT = nextPayment.getDueDate().toLocalDateTime().plusDays(1).minusSeconds(1);
		        					LocalDateTime comparedDT = LocalDate.now().plusDays(1).atStartOfDay();
		        					
		        					if(nextDueDT.isAfter(comparedDT) && nextState != PaymentState.PAID)
		        						nextPayment.updateColumnInDB(conn, "is_pacified", false, Types.BOOLEAN);
		        				}
	        				}
	        			}
	        			
	        			processedAthlete.updateColumnInDB(conn, "membership", MembershipState.STANDARD, "membership_state");
	        		}
	        		else if(updateMode == 5)
	        		{
	        			//Üyelik İptali
	        			Registration userReg = Registration.findByRegistered_CompletedRegistration(conn, processedAthlete);
	        			PaymentPlan plan = PaymentPlan.findByPaidFor_MostRecent(conn, processedAthlete);
	        			
	        			if(userReg != null && plan != null && plan.getPayments() != null)
	        			{
	        				PaymentSchema sch = PaymentSchema.findById(conn, userReg.getPaymentSchema());
	        				SubscriptionCancellationAction action = sch.getCancellationAction();
	        				
	        				if(action == SubscriptionCancellationAction.CANCEL_FUTURE_PAYMENTS)
	        				{
	        					for(Payment nextPayment : plan.getPayments())
	        						nextPayment.deleteFromDB(conn);
	        				}
	        				else if(action == SubscriptionCancellationAction.PACIFY_FUTURE_PAYMENTS)
	        				{
	        					for(Payment nextPayment : plan.getPayments())
		        				{
		        					PaymentState nextState = nextPayment.getState();
		        					LocalDateTime nextDueDT = nextPayment.getDueDate().toLocalDateTime().plusDays(1).minusSeconds(1);
		        					LocalDateTime comparedDT = LocalDate.now().plusDays(1).atStartOfDay();
		        					
		        					if(nextDueDT.isAfter(comparedDT) && nextState != PaymentState.PAID)
		        						nextPayment.updateColumnInDB(conn, "is_pacified", true, Types.BOOLEAN);
		        				}
	        				}
	        			}
	        			
	        			processedAthlete.manipulateState(conn, LoginableState.SUSPENDED);
	        			processedAthlete.updateColumnInDB(conn, "membership", MembershipState.DISMISSED, "membership_state");
	        		}
	        		else if(updateMode == 6)
	        		{
	        			//Genel Güncelleme
	        		}
	        		/*if(updateMode == 0)
	        			processedETraining.manipulateState(conn, ETrainingState.SUSPENDED); //Askıya alma
	        		else if(updateMode == 1)
	        			processedETraining.manipulateState(conn, ETrainingState.ACTIVE); //Etkinleştirme
	        		else if(updateMode == 2)
	        			processedETraining.manipulateState(conn, ETrainingState.SUBMITTED); //Onay talebi oluşturma
	        		else if(updateMode == 3)
	        			processedETraining.manipulateState(conn, ETrainingState.PENDING); //Revizyon talebi oluşturma
	        		else if(updateMode == 4)
	        		{
	        			//Güncelleme
	        			processedETraining = ETraining.findById(conn, processedETraining.getId());
	        			
	        			Part posterPart = request.getPart("poster");
	        			boolean hasPosterUpdate = posterPart != null && posterPart.getSize() > 0;
	        			
	        			if(hasPosterUpdate && !this.getPoster(posterPart, processedETraining.getCode()))
	        				throw new IllegalArgumentException("Bad poster file data.");
	        			
	        			CertificateType certType = certTypeStr != null ? CertificateType.valueOf(certTypeStr) : null;
		        		Exam finalExam = examIdStr != null ? new Exam(Integer.parseInt(examIdStr)) : null;
		        		int categoryId = categoryIdStr != null ? Integer.parseInt(categoryIdStr) : -1;
		        		
		        		ETraining updatingETraining = new ETraining(-1, null, title, desc, Language.build(langStr), Float.parseFloat(priceStr), Currency.valueOf(currencyStr), 
		        				Float.parseFloat(discountRatioStr), Float.parseFloat(discountAmountStr), categoryId, null, true, certType, processedETraining.getState(), finalExam);
		        		processedETraining.updateInDB(conn, updatingETraining);
	        		}
	        		else if(updateMode == 5)
	        		{
	        			//Yayınlama
	        			processedETraining = ETraining.findById(conn, processedETraining.getId());
	        			int categoryId = categoryIdStr != null ? Integer.parseInt(categoryIdStr) : -1;
	        			
	        			processedETraining.publish(conn, categoryId);
	        		}
	        		else
	        			throw new IllegalArgumentException();*/
	        	}
	        	else
	        		throw new IllegalArgumentException();
				
				HttpSession session = request.getSession(false);
				session.setAttribute("dataMode", dataModeStr);
				session.setAttribute("itemId", processedAthlete.getId());
				
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
	
	private boolean getPoster(Part posterPart)
	{
		return true;
	}
	
	/*private void resizeImage(BufferedImage inputPoster, int targetWidth, int targetHeight, File outputFile) throws IOException 
	{
		Image scaledImg = null;
		BufferedImage outputImg = null;
		
		try
		{
			scaledImg = inputPoster.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
		    
			outputImg = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
			outputImg.getGraphics().drawImage(scaledImg, 0, 0, null);
		    
		    ImageIO.write(outputImg, "jpg", outputFile);
	    }
		finally
		{
			if(scaledImg != null)
				scaledImg.flush();
			
			if(outputImg != null)
				outputImg.flush();
		}
	}*/
}