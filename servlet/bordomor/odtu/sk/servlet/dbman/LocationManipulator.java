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
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.LocationType;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.location.Address;
import bordomor.odtu.sk.location.City;
import bordomor.odtu.sk.location.District;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_location.jsp")
public class LocationManipulator extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public LocationManipulator() 
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
			
			String name = request.getParameter("name");
			String description = request.getParameter("description");
			String colourCode = request.getParameter("colour_code");
			String cityIdStr = request.getParameter("city_id");
			String districtIdStr = request.getParameter("district_id");
			String addressStr = request.getParameter("address");
			String postCode = request.getParameter("post_code");
			String latitude = request.getParameter("latitude");
			String longitude = request.getParameter("longitude");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Loginable user = Loginable.findByStok(conn, stok);
			Location processedLoc = new Location(idStr != null ? Integer.parseInt(idStr) : -1);
			
			if(user == null || user.getRole() == null || user.getState() != LoginableState.ACTIVE || !user.getRole().getFacilityMod().canWrite())
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(dataModeStr.equals(Params.DATABASE_DELETE_DATA_MODE))
					processedLoc.deleteFromDB(conn);
				else if(dataModeStr.equals(Params.DATABASE_INSERT_DATA_MODE))
	        	{
	        		processedLoc = new Location();
	        		processedLoc.setName(name);
	        		processedLoc.setDescription(description.trim().length() > 0 ? description.trim() : null);
	        		processedLoc.setRepresentingColour(colourCode);
	        		processedLoc.setType(LocationType.FACILITY);
	        		
	        		Address adr = new Address();
	        		adr.setAddressString(addressStr.trim().length() > 0 ? addressStr.trim() : null);
	        		adr.setPostCode(postCode.trim().length() > 0 ? postCode.trim() : null);
	        		adr.setDistrict((districtIdStr != null && districtIdStr.trim().length() > 0) ? new District(Integer.parseInt(districtIdStr)) : null);
	        		adr.setCity(new City(Integer.parseInt(cityIdStr)));
	        		adr.setLatitude(latitude.trim().length() > 0 ? Float.parseFloat(latitude.trim()) : -1f);
	        		adr.setLongitude(longitude.trim().length() > 0 ? Float.parseFloat(longitude.trim()) : -1f);
	        		
	        		processedLoc.setAddress(adr);
	        		processedLoc.createInDB(conn);
	        	}
	        	else if(dataModeStr.equals(Params.DATABASE_UPDATE_DATA_MODE))
	        	{
	        		processedLoc = Location.findById(conn, processedLoc.getId());
	        		
	        		Location updatingLoc = new Location();
	        		updatingLoc.setName(name);
	        		updatingLoc.setDescription(description.trim().length() > 0 ? description.trim() : null);
	        		updatingLoc.setRepresentingColour(colourCode);
	        		updatingLoc.setType(LocationType.FACILITY);
	        		
	        		Address adr = new Address();
	        		adr.setAddressString(addressStr.trim().length() > 0 ? addressStr.trim() : null);
	        		adr.setPostCode(postCode.trim().length() > 0 ? postCode.trim() : null);
	        		adr.setDistrict((districtIdStr != null && districtIdStr.trim().length() > 0) ? new District(Integer.parseInt(districtIdStr)) : null);
	        		adr.setCity(new City(Integer.parseInt(cityIdStr)));
	        		adr.setLatitude(latitude.trim().length() > 0 ? Float.parseFloat(latitude.trim()) : -1f);
	        		adr.setLongitude(longitude.trim().length() > 0 ? Float.parseFloat(longitude.trim()) : -1f);
	        		
	        		updatingLoc.setAddress(adr);
	        		processedLoc.updateInDB(conn, updatingLoc);
	        	}
	        	else
	        		throw new IllegalArgumentException();
				
				HttpSession session = request.getSession(false);
				session.setAttribute("dataMode", dataModeStr);
				session.setAttribute("itemId", processedLoc.getId());
				
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