package bordomor.odtu.sk.servlet.dbman;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.imageio.ImageIO;

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
import bordomor.odtu.sk.Params.AgeGroup;
import bordomor.odtu.sk.Params.BloodType;
import bordomor.odtu.sk.Params.Gender;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Params.TrainerLabel;
import bordomor.odtu.sk.Team;
import bordomor.odtu.sk.Team.AgeGroupInterval;
import bordomor.odtu.sk.Trainer;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import bordomor.util.FileUtils;
import bordomor.util.StreamUtils;
import bordomor.util.StringUtils;

@WebServlet("/dbman/manipulate_team.jsp")
@MultipartConfig
public class TeamManipulator extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public TeamManipulator() 
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
			
			String name = request.getParameter("name");
			String branchIdStr = request.getParameter("branch");
			String ageCategoryStr = request.getParameter("age_category");
			String ageGroupsStr = request.getParameter("age_groups");
			String genderCategoryStr = request.getParameter("gender_category");
			String trainerIdStr = request.getParameter("trainer_id");
			String autoFillSquadStr = request.getParameter("auto_fill_squad");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Loginable user = Loginable.findByStok(conn, stok);
			Team processedTeam = new Team(idStr != null ? Integer.parseInt(idStr) : -1);
			
			if(user == null || !user.canOperateOn(conn, processedTeam))
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(dataModeStr.equals(Params.DATABASE_DELETE_DATA_MODE))
					processedTeam.deleteFromDB(conn);
				else if(dataModeStr.equals(Params.DATABASE_INSERT_DATA_MODE))
	        	{
	        		if(this.getPoster(null))
	        		{
	        			processedTeam = new Team();
	        			processedTeam.setName(name);
	        			processedTeam.setBranch(new Branch(Integer.parseInt(branchIdStr)));
	        			processedTeam.setAgeCategory(AgeGroup.valueOf(ageCategoryStr));
	        			processedTeam.setGenderCategory(Gender.valueOf(genderCategoryStr));
	        			
	        			if(ageGroupsStr != null)
	        			{
	        				String[] groupVals = ageGroupsStr.trim().split(",");
	        				AgeGroup[] group = new AgeGroup[groupVals.length];
	        				
	        				for(int i = 0; i < groupVals.length; i++)
	        					group[i] = AgeGroup.valueOf(groupVals[i]);
	        						
	        				processedTeam.new AgeGroupInterval(-1, null, null);
	        				processedTeam.getAgeGroups().parseFrom(group);
	        			}
	        			
	        			if(trainerIdStr != null)
	        				processedTeam.setTrainer(new Trainer(Integer.parseInt(trainerIdStr)));
	        			
	        			processedTeam.createInDB(conn);
	        			
	        			boolean autoFill = autoFillSquadStr.equals("on") || Boolean.parseBoolean(autoFillSquadStr);
	        			
	        			if(autoFill)
	        			{
	        				Athlete[] allowedAthletes = Athlete.findAll_ForTeam(conn, processedTeam);
	        				
	        				for(Athlete nextAthlete : allowedAthletes)
	        					processedTeam.addAthleteInDB(conn, nextAthlete);
	        			}
	        		}
	        		else
	        			throw new IllegalArgumentException("Bad poster file data.");
	        	}
	        	else if(dataModeStr.equals(Params.DATABASE_UPDATE_DATA_MODE))
	        	{
	        		processedTeam = Team.findById(conn, processedTeam.getId());
	        		
	        		Team updatingTeam = new Team();
	        		updatingTeam.setName(name);
	        		updatingTeam.setBranch(new Branch(Integer.parseInt(branchIdStr)));
	        		updatingTeam.setAgeCategory(AgeGroup.valueOf(ageCategoryStr));
	        		updatingTeam.setGenderCategory(Gender.valueOf(genderCategoryStr));
        			
        			if(ageGroupsStr != null)
        			{
        				String[] groupVals = ageGroupsStr.trim().split(",");
        				AgeGroup[] group = new AgeGroup[groupVals.length];
        				
        				for(int i = 0; i < groupVals.length; i++)
        					group[i] = AgeGroup.valueOf(groupVals[i]);
        						
        				updatingTeam.new AgeGroupInterval(-1, null, null);
        				updatingTeam.getAgeGroups().parseFrom(group);
        			}
        			
        			if(trainerIdStr != null)
        				updatingTeam.setTrainer(new Trainer(Integer.parseInt(trainerIdStr)));
        			
        			boolean squadRenewalRequired = updatingTeam.getBranch() != processedTeam.getBranch() 
        					|| updatingTeam.getAgeCategory() != processedTeam.getAgeCategory() 
        					|| updatingTeam.getGenderCategory() != processedTeam.getGenderCategory() 
        					|| !updatingTeam.getAgeGroups().equals(processedTeam.getAgeGroups());
        			
        			processedTeam.updateInDB(conn, updatingTeam);
        			
        			if(squadRenewalRequired)
        				processedTeam.syncAthletesInDB(conn, updatingTeam);
        		}
	        	else
	        		throw new IllegalArgumentException();
				
				HttpSession session = request.getSession(false);
				session.setAttribute("dataMode", dataModeStr);
				session.setAttribute("itemId", processedTeam.getId());
				
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
	
	private void resizeImage(BufferedImage inputPoster, int targetWidth, int targetHeight, File outputFile) throws IOException 
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
	}
}