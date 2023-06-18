package bordomor.odtu.sk.servlet.dbman.registration;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bordomor.odtu.sk.Athlete;
import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.Gender;
import bordomor.odtu.sk.Params.LocationType;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Params.RegistrationState;
import bordomor.odtu.sk.Params.RegistrationStep;
import bordomor.odtu.sk.Registration;
import bordomor.odtu.sk.location.Address;
import bordomor.odtu.sk.location.City;
import bordomor.odtu.sk.location.District;
import bordomor.odtu.sk.location.Location;
import bordomor.util.DBUtils;
import bordomor.util.StringUtils;

@WebServlet("/dbman/manipulate_certain_registration_s1.jsp")
public class CertainRegistrationStep1 extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public CertainRegistrationStep1() 
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
			String registrationBranchIdStr = request.getParameter("registration_branch");
			String name = request.getParameter("name");
			String surname = request.getParameter("surname");
			String genderStr = request.getParameter("gender");
			String birthDateStr = request.getParameter("birth_date");
			String placeOfBirthIdStr = request.getParameter("place_of_birth");
			String idNo = request.getParameter("id_no");
			String email = request.getParameter("email");
			String phoneNumber = request.getParameter("phone_number");
			
			String addressCityIdStr = request.getParameter("address_city");
			String addressDistrictIdStr = request.getParameter("address_district");
			String addressStr = request.getParameter("address");
			String latitudeStr = request.getParameter("latitude");
			String longitudeStr = request.getParameter("longitude");
			
			String school = request.getParameter("school");
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			//Kayıt Grubu
			Athlete registeredAthlete = new Athlete(-1, -1, code, email, name, surname, Gender.valueOf(genderStr), (phoneNumber.trim().length() > 0 ? phoneNumber.trim() : null), LoginableState.PENDING);
			registeredAthlete.setPrimaryBranch(new Branch(Integer.parseInt(registrationBranchIdStr)));
			registeredAthlete.setBirthDate(Timestamp.valueOf(LocalDate.parse(birthDateStr, Params.DATE_FORMATTER).atStartOfDay()));
			registeredAthlete.setPlaceOfBirth(new City(Integer.parseInt(placeOfBirthIdStr)));
			registeredAthlete.setIdNo(idNo.trim());
    		registeredAthlete.setSchool(school != null ? school.trim() : null);
			
    		Address homeAddress = new Address();
			homeAddress.setCity(new City(Integer.parseInt(addressCityIdStr)));
			homeAddress.setDistrict(addressDistrictIdStr.trim().length() > 0 ? new District(Integer.parseInt(addressDistrictIdStr)) : null);
			homeAddress.setAddressString(addressStr.trim().length() > 0 ? addressStr.trim() : null);
			homeAddress.setLatitude(latitudeStr != null ? Float.parseFloat(latitudeStr.trim()) : -1f);
			homeAddress.setLongitude(longitudeStr != null ? Float.parseFloat(longitudeStr.trim()) : -1f);
    		
    		Location homeLocation = new Location(-1, "AUTOLOC-" + new Date().getTime() + "-" + StringUtils.generateRandomString(4, true), null, Color.decode("#111111"), homeAddress, LocationType.PRIVATE_ADDRESS);
    		registeredAthlete.setHomeLocation(homeLocation);
    		
    		registeredAthlete.createInDB(conn);
    		
    		Registration regInProgress = Registration.findByCode(conn, code.trim());
    		regInProgress.setState(RegistrationState.IN_PROGRESS);
    		regInProgress.setRegistered(registeredAthlete);
    		regInProgress.setRegistartionBranch(new Branch(Integer.parseInt(registrationBranchIdStr)));
    		regInProgress.setLastCompletedStep(RegistrationStep.ATHLETE_DATA);
    		
    		regInProgress.updateColumnInDB(conn, "registered", regInProgress.getRegistered().getId(), Types.INTEGER);
    		regInProgress.updateColumnInDB(conn, "registration_branch", regInProgress.getRegistartionBranch().getId(), Types.INTEGER);
    		regInProgress.updateColumnInDB(conn, "last_completed_step", regInProgress.getLastCompletedStep(), "registration_step");
    		regInProgress.updateColumnInDB(conn, "state", regInProgress.getState(), "registration_state");
        	
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