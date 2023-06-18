package bordomor.odtu.sk.servlet.query;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Training;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import bordomor.util.XMLUtils;

@WebServlet("/query/get_location_data.jsp")
public class LocationDataFetcher extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	public LocationDataFetcher() 
    {
        super();
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out = null;
		Connection conn = null;
		
		try 
        {
			String stok = new WebUtils(request, response).getCookieValue("stok");
			String idStr = request.getParameter("id");
			
			out = response.getWriter();
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			Loginable user = Loginable.findByStok(conn, stok);
			
			if(user == null || user.getRole() == null || !user.getRole().getFacilityMod().canRead())
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				Location[] locations = null;
				
				if(idStr != null)
        		{
	        		Location location = Location.findById(conn, Integer.parseInt(idStr));
	        		
		        	if(location != null)
		        	{
		        		locations = new Location[] {location};
		        		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(Params.DEFAULT_LOCALE);
		        		Training[] weeksTrainings = Training.findByWeekOfYear_And_Location(conn, cal.get(Calendar.YEAR), cal.get(Calendar.WEEK_OF_YEAR), location.getId());
		        		
		        		Long[] totals = new Long[]{ 0l, 0l, 0l, 0l, 0l, 0l, 0l };
		        		int dayIndex = 0;
		        		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		        		
		        		while(weeksTrainings != null && true)
		        		{
		        			String daysDate = Params.DATE_FORMAT.format(cal.getTime());
		        			
			        		for(Training nextTraining : weeksTrainings)
			        		{
			        			String nextStartTime = Params.DATE_FORMAT.format(nextTraining.getStartTime());
			        			
			        			if(nextStartTime.equals(daysDate))
			        				totals[dayIndex] += nextTraining.getDuration();
			        		}
			        		
			        		if(dayIndex == 6)
			        		{
			        			location.setWeekTotals(totals);
			        			break;
			        		}
			        		
			        		dayIndex++;
			        		cal.add(Calendar.DAY_OF_WEEK, 1);
			        	}
		        	}
        		}
				else
					locations = Location.findTrainingLocations(conn);
				
				if(locations != null)
				{
					Document xml = XMLUtils.createDocument();
		        	Element parentEl = xml.createElement("query_result");
		        	parentEl.setAttribute("time", new Date().getTime() + "");
	        		xml.appendChild(parentEl);
	        		
	        		Element locationsContainerEl = xml.createElement("locations");
            		
            		for(Location nextLocation : locations)
            			locationsContainerEl.appendChild(nextLocation.makeXMLElement(xml, true));
            		
            		parentEl.appendChild(locationsContainerEl);
            		out.print(XMLUtils.convertXMLToString(xml));
				}
				else
					out.print(Params.NO_DATA_ERROR_STRING);
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
        	out.print(Params.CODE_ERROR_STRING );
        } 
        finally 
        {
        	DBUtils.close(conn);
        }
	}
}
