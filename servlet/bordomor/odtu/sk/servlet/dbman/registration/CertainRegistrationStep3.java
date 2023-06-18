package bordomor.odtu.sk.servlet.dbman.registration;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.LocationType;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Params.ParenthoodTitle;
import bordomor.odtu.sk.Params.RegistrationStep;
import bordomor.odtu.sk.location.Address;
import bordomor.odtu.sk.location.City;
import bordomor.odtu.sk.location.District;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.Parent;
import bordomor.odtu.sk.Registration;
import bordomor.util.DBUtils;
import bordomor.util.StringUtils;

@WebServlet("/dbman/manipulate_certain_registration_s3.jsp")
public class CertainRegistrationStep3 extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public CertainRegistrationStep3() 
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
			String idNo = request.getParameter("id_no");
			String registrantIdStr = request.getParameter("registrant_id");
			String isSiblingRegistraionStr = request.getParameter("is_sibling_registration");
			
			String parenthoodTitleStr = request.getParameter("parenthood_title");
			String name = request.getParameter("name");
			String surname = request.getParameter("surname");
			String email = request.getParameter("email");
			String phoneNumber = request.getParameter("phone_number");
			String licencePlate = request.getParameter("licence_plate");
			
			String atSameAddressStr = request.getParameter("at_same_address_with_registered");
			String addressCityIdStr = request.getParameter("address_city");
			String addressDistrictIdStr = request.getParameter("address_district");
			String addressStr = request.getParameter("address");
			String latitudeStr = request.getParameter("latitude");
			String longitudeStr = request.getParameter("longitude");
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			//Kayıt Grubu
			Registration regInProgress = Registration.findByCode(conn, code.trim());
    		
			ParenthoodTitle parentsTitle = ParenthoodTitle.valueOf(parenthoodTitleStr.trim());
			Parent registrant = null;
			
			if(registrantIdStr == null || registrantIdStr.trim().length() == 0)
			{
				registrant = new Parent(-1, -1, code, email, name, surname, parentsTitle.getParenthoodGender(), phoneNumber.trim(), LoginableState.PENDING);
				registrant.setIdNo(idNo.trim());
				registrant.setVehicleLicencePlate(licencePlate != null ? licencePlate.trim() : null);
				
				if(atSameAddressStr == null || Boolean.parseBoolean(atSameAddressStr) == false)
				{
					Address homeAddress = new Address();
					homeAddress.setCity(new City(Integer.parseInt(addressCityIdStr)));
					homeAddress.setDistrict(addressDistrictIdStr.trim().length() > 0 ? new District(Integer.parseInt(addressDistrictIdStr)) : null);
					homeAddress.setAddressString(addressStr.trim().length() > 0 ? addressStr.trim() : null);
					homeAddress.setLatitude(latitudeStr != null ? Float.parseFloat(latitudeStr.trim()) : -1f);
					homeAddress.setLongitude(longitudeStr != null ? Float.parseFloat(longitudeStr.trim()) : -1f);
	        		
	        		Location homeLocation = new Location(-1, "AUTOLOC-" + new Date().getTime() + "-" + StringUtils.generateRandomString(4, true), null, Color.decode("#111111"), homeAddress, LocationType.PRIVATE_ADDRESS);
	        		registrant.setHomeLocation(homeLocation);
				}
				else 
					registrant.setHomeLocation(regInProgress.getRegistered().getHomeLocation());
				
				registrant.createInDB(conn);
			}
			else
				registrant = Parent.findById(conn, Integer.parseInt(registrantIdStr));
			
    		registrant.createParenthoodInDB(conn, regInProgress.getRegistered(), parentsTitle);
    		regInProgress.setRegistrant(registrant);
    		regInProgress.setSiblingRegistration(Boolean.parseBoolean(isSiblingRegistraionStr));
    		regInProgress.setLastCompletedStep(RegistrationStep.PARENT_DATA);
    		
    		regInProgress.updateColumnInDB(conn, "registrant", regInProgress.getRegistrant().getId(), Types.INTEGER);
    		regInProgress.updateColumnInDB(conn, "is_sibling_registration", regInProgress.isSiblingRegistration(), Types.BOOLEAN);
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