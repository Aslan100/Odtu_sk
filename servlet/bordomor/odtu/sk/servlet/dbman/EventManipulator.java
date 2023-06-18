package bordomor.odtu.sk.servlet.dbman;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Event;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.EventType;
import bordomor.odtu.sk.Params.LocationType;
import bordomor.odtu.sk.Params.MemberRole;
import bordomor.odtu.sk.location.Address;
import bordomor.odtu.sk.location.City;
import bordomor.odtu.sk.location.District;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_event.jsp")
public class EventManipulator extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public EventManipulator() 
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
			
			String name = request.getParameter("event_name");
			String typeStr = request.getParameter("event_type");
			String branchIdStr = request.getParameter("event_branch");
			String startDateStr = request.getParameter("start_date");
			String startTimeStr = request.getParameter("start_time");
			String endDateStr = request.getParameter("end_date");
			String endTimeStr = request.getParameter("end_time");
			String parentContributionAmountStr = request.getParameter("parent_contribution_amount");
			
			String locationIdStr = request.getParameter("location");
			String locationName = request.getParameter("location_name");
			String cityIdStr = request.getParameter("city");
			String districtIdStr = request.getParameter("district");
			String addressStr = request.getParameter("address");
			String latitude = request.getParameter("latitude");
			String longitude = request.getParameter("longitude");
			
			String selectedMembersStr = request.getParameter("selected_members");
			String memberRoleStr = request.getParameter("member_role");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Loginable user = Loginable.findByStok(conn, stok);
			Event processedEvent = new Event(idStr != null ? Integer.parseInt(idStr) : -1);
			
			if(user == null || user.getRole() == null /*|| user.getRole().getEventMod().canWrite()*/)
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(dataModeStr.equals(Params.DATABASE_DELETE_DATA_MODE))
					processedEvent.deleteFromDB(conn);
				else if(dataModeStr.equals(Params.DATABASE_INSERT_DATA_MODE))
	        	{
					processedEvent.setName(name);
					processedEvent.setType(EventType.valueOf(typeStr));
					processedEvent.setBranch(new Branch(Integer.parseInt(branchIdStr)));
					processedEvent.setStartTime(new Timestamp(Params.DATE_TIME_FORMAT.parse(startDateStr + " " + startTimeStr).getTime()));
					processedEvent.setEndTime(new Timestamp(Params.DATE_TIME_FORMAT.parse(endDateStr + " " + endTimeStr).getTime()));
					processedEvent.setParentContributionAmount(Float.parseFloat(parentContributionAmountStr));
					
					if(locationIdStr != null)
						processedEvent.setLocation(new Location(Integer.parseInt(locationIdStr.trim())));
					else
					{
						Location newLoc = new Location();
						newLoc.setName(locationName);
						newLoc.setRepresentingColour(Color.BLACK);
		        		newLoc.setType(LocationType.EVENT_POINT);
		        		
		        		Address adr = new Address();
		        		adr.setAddressString(addressStr.trim().length() > 0 ? addressStr.trim() : null);
		        		adr.setDistrict((districtIdStr != null && districtIdStr.trim().length() > 0) ? new District(Integer.parseInt(districtIdStr)) : null);
		        		adr.setCity(new City(Integer.parseInt(cityIdStr)));
		        		adr.setLatitude(latitude.trim().length() > 0 ? Float.parseFloat(latitude.trim()) : -1f);
		        		adr.setLongitude(longitude.trim().length() > 0 ? Float.parseFloat(longitude.trim()) : -1f);
		        		
		        		newLoc.setAddress(adr);
		        		newLoc.createInDB(conn);
		        		
		        		processedEvent.setLocation(newLoc);
					}
					
					processedEvent.createInDB(conn);
				}
	        	else if(dataModeStr.equals(Params.DATABASE_UPDATE_DATA_MODE))
	        	{
	        		if(updateModeStr == null)
	        		{
		        		Event updatingEvent = new Event();
		        		updatingEvent.setName(name);
		        		updatingEvent.setType(EventType.valueOf(typeStr));
		        		updatingEvent.setStartTime(new Timestamp(Params.DATE_TIME_FORMAT.parse(startDateStr + " " + startTimeStr).getTime()));
		        		updatingEvent.setEndTime(new Timestamp(Params.DATE_TIME_FORMAT.parse(endDateStr + " " + endTimeStr).getTime()));
		        		updatingEvent.setParentContributionAmount(Float.parseFloat(parentContributionAmountStr));
		        		
		        		if(locationIdStr != null)
		        			updatingEvent.setLocation(new Location(Integer.parseInt(locationIdStr.trim())));
		        		else
		        		{
		        			
		        		}
		        		
		        		//updatingEvent.createInDB(conn);
		        		processedEvent.updateInDB(conn, updatingEvent);
		        		
		        		HttpSession session = request.getSession(false);
						session.setAttribute("dataMode", dataModeStr);
						session.setAttribute("itemId", processedEvent.getId());
	        		}
	        		else if(Integer.parseInt(updateModeStr) == 0)
	        			processedEvent.deleteMemberInDB(conn, Integer.parseInt(selectedMembersStr));
	        		else if(Integer.parseInt(updateModeStr) == 1)
	        		{
	        			String[] memberIds = selectedMembersStr.split(",");
	        			
	        			for(String nextMemberIdStr : memberIds)
	        				processedEvent.addMemberInDB(conn, Integer.parseInt(nextMemberIdStr), MemberRole.valueOf(memberRoleStr));
	        		}
	        	}
	        	else
	        		throw new IllegalArgumentException();
				
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