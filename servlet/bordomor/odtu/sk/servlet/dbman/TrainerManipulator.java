package bordomor.odtu.sk.servlet.dbman;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.BloodType;
import bordomor.odtu.sk.Params.Gender;
import bordomor.odtu.sk.Params.LocationType;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Params.TrainerLabel;
import bordomor.odtu.sk.Trainer;
import bordomor.odtu.sk.location.Address;
import bordomor.odtu.sk.location.City;
import bordomor.odtu.sk.location.District;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import bordomor.util.StringUtils;

@WebServlet("/dbman/manipulate_trainer.jsp")
@MultipartConfig
public class TrainerManipulator extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public TrainerManipulator() 
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
			
			String heightStr = request.getParameter("height");
			String weightStr = request.getParameter("weight");
			String birthDateStr = request.getParameter("birth_date");
			String bloodTypeStr = request.getParameter("blood_type");
			String primaryBranchIdStr = request.getParameter("primary_branch");
			String labelStr = request.getParameter("label");
			String levelStr = request.getParameter("level");
			String idNo = request.getParameter("id_no");
			String placeOfBirthStr = request.getParameter("place_of_birth");
			String mothersName = request.getParameter("mothers_name");
			String fathersName = request.getParameter("fathers_name");
			
			String addressCityIdStr = request.getParameter("address_city");
			String addressDistrictIdStr = request.getParameter("address_district");
			String addressStr = request.getParameter("address");
			String latitudeStr = request.getParameter("latitude");
			String longitudeStr = request.getParameter("longitude");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Loginable user = Loginable.findByStok(conn, stok);
			Trainer processedTrainer = new Trainer(idStr != null ? Integer.parseInt(idStr) : -1);
			
			if(user == null || !user.canOperateOn(conn, processedTrainer))
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(dataModeStr.equals(Params.DATABASE_DELETE_DATA_MODE))
					processedTrainer.deleteFromDB(conn);
				else if(dataModeStr.equals(Params.DATABASE_INSERT_DATA_MODE))
	        	{
	        		if(this.getPoster(null))
	        		{
	        			processedTrainer = new Trainer(-1, null, -1, code, email, name, surname, Gender.valueOf(genderStr), null, LoginableState.PENDING);
	        			processedTrainer.setPhoneNumber(phoneNumber.trim().length() > 0 ? phoneNumber.trim() : null);
	        			processedTrainer.setHesCode(hesCode.trim().length() > 0 ? hesCode.trim() : null);
	        			
	        			processedTrainer.setPrimaryBranch(new Branch(Integer.parseInt(primaryBranchIdStr)));
	        			processedTrainer.setLabel(TrainerLabel.valueOf(labelStr));
	        			processedTrainer.setLevel(Short.parseShort(levelStr));
	        			
	        			processedTrainer.setHeight(heightStr.trim().length() > 0 ? Integer.parseInt(heightStr) : -1);
	        			processedTrainer.setWeight(weightStr.trim().length() > 0 ? Float.parseFloat(weightStr) : -1f);
	        			processedTrainer.setBloodType(BloodType.valueOf(bloodTypeStr));
	        			processedTrainer.setIdNo(idNo.trim().length() > 0 ? idNo.trim() : null);
	        			processedTrainer.setBirthDate(Timestamp.valueOf(LocalDate.parse(birthDateStr, Params.DATE_FORMATTER).atStartOfDay()));
	        			processedTrainer.setPlaceOfBirth(City.findById(conn, Integer.parseInt(placeOfBirthStr), false));
	        			processedTrainer.setMothersName(mothersName.trim().length() > 0 ? mothersName.trim() : null);
	        			processedTrainer.setFathersName(fathersName.trim().length() > 0 ? fathersName.trim() : null);
	        			
	        			if(addressCityIdStr.trim().length() > 0 && Integer.parseInt(addressCityIdStr) > 0)
	        			{
	        				Address homeAddress = new Address();
	        				homeAddress.setCity(new City(Integer.parseInt(addressCityIdStr)));
	        				homeAddress.setDistrict(addressDistrictIdStr.trim().length() > 0 ? new District(Integer.parseInt(addressDistrictIdStr)) : null);
	        				homeAddress.setAddressString(addressStr.trim().length() > 0 ? addressStr.trim() : null);
	        				homeAddress.setLatitude(latitudeStr.trim().length() > 0 ? Float.parseFloat(latitudeStr) : -1f);
	        				homeAddress.setLongitude(longitudeStr.trim().length() > 0 ? Float.parseFloat(longitudeStr) : -1f);
	        				
	        				Location homeLocation = new Location(-1, "AUTOLOC-" + new Date().getTime() + "-" + StringUtils.generateRandomString(4, true), null, Color.decode("#111111"), homeAddress, LocationType.PRIVATE_ADDRESS);
	        				processedTrainer.setHomeLocation(homeLocation);
	        			}
	        			
	        			processedTrainer.createInDB(conn);
	        		}
	        		else
	        			throw new IllegalArgumentException("Bad poster file data.");
	        	}
	        	else if(dataModeStr.equals(Params.DATABASE_UPDATE_DATA_MODE))
	        	{
	        		processedTrainer = (Trainer) Loginable.findByLoginableId(conn, Integer.parseInt(idStr));
	        		
	        		Trainer updatingTrainer = new Trainer(-1, null, -1, code, email, name, surname, Gender.valueOf(genderStr), null, processedTrainer.getState());
	        		updatingTrainer.setPhoneNumber(phoneNumber.trim().length() > 0 ? phoneNumber.trim() : null);
	        		updatingTrainer.setHesCode(hesCode.trim().length() > 0 ? hesCode.trim() : null);
        			
	        		updatingTrainer.setPrimaryBranch(new Branch(Integer.parseInt(primaryBranchIdStr)));
	        		updatingTrainer.setLabel(TrainerLabel.valueOf(labelStr));
	        		updatingTrainer.setLevel(Short.parseShort(levelStr));
        			
	        		updatingTrainer.setHeight(heightStr.trim().length() > 0 ? Integer.parseInt(heightStr) : -1);
	        		updatingTrainer.setWeight(weightStr.trim().length() > 0 ? Float.parseFloat(weightStr) : -1f);
	        		updatingTrainer.setBloodType(BloodType.valueOf(bloodTypeStr));
	        		updatingTrainer.setIdNo(idNo.trim().length() > 0 ? idNo.trim() : null);
	        		updatingTrainer.setBirthDate(new Timestamp(Params.DATE_FORMAT.parse(birthDateStr).getTime()));
	        		updatingTrainer.setPlaceOfBirth(City.findById(conn, Integer.parseInt(placeOfBirthStr), false));
	        		updatingTrainer.setMothersName(mothersName.trim().length() > 0 ? mothersName.trim() : null);
	        		updatingTrainer.setFathersName(fathersName.trim().length() > 0 ? fathersName.trim() : null);
        			
        			if(addressCityIdStr.trim().length() > 0 && Integer.parseInt(addressCityIdStr) > 0)
        			{
        				Address homeAddress = new Address();
        				homeAddress.setCity(new City(Integer.parseInt(addressCityIdStr)));
        				homeAddress.setDistrict(addressDistrictIdStr.trim().length() > 0 ? new District(Integer.parseInt(addressDistrictIdStr)) : null);
        				homeAddress.setAddressString(addressStr.trim().length() > 0 ? addressStr.trim() : null);
        				homeAddress.setLatitude(latitudeStr.trim().length() > 0 ? Float.parseFloat(latitudeStr) : -1f);
        				homeAddress.setLongitude(longitudeStr.trim().length() > 0 ? Float.parseFloat(longitudeStr) : -1f);
        				
        				Location homeLocation = new Location(-1, "AUTOLOC-" + new Date().getTime() + "-" + StringUtils.generateRandomString(4, true), null, Color.decode("#111111"), homeAddress, LocationType.PRIVATE_ADDRESS);
        				updatingTrainer.setHomeLocation(homeLocation);
        			}
        			
        			processedTrainer.updateInDB(conn, updatingTrainer);
        		}
	        	else
	        		throw new IllegalArgumentException();
				
				HttpSession session = request.getSession(false);
				session.setAttribute("dataMode", dataModeStr);
				session.setAttribute("itemId", processedTrainer.getId());
				
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