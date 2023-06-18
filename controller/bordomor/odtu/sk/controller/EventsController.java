package bordomor.odtu.sk.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Vector;

import bordomor.odtu.sk.Event;
import bordomor.odtu.sk.LoginRole;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(Params.PORTAL_EVENTS_URI)
public class EventsController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

    public EventsController() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		Connection conn = null;
		
		try
		{
			String stok = new WebUtils(request, response).getCookieValue("stok");
			String yearStr = request.getParameter("year");
			String monthStr = request.getParameter("month");
			String directionStr = request.getParameter("direction");
			String locIdStr = request.getParameter("location");
			
			if(stok != null)
			{
				conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
				Loginable loggedUser = Loginable.findByStok(conn, stok);
				
				if(!isUserAllowedOnPage(loggedUser))
				{
					if(loggedUser != null)
						Loginable.deleteLoginableSession(conn, stok);
						
					response.sendError(403);
				}
				else
				{
					LocalDateTime[] boundaries = this.getMonthBoundaries(yearStr, monthStr, directionStr);
					Object[] eventData = this.getEventsAndLocation(conn, locIdStr, boundaries[0].getYear(), boundaries[0].getMonthValue());
					
					String currentMonthName = "";
					int currentMonthNumber = -1;
					
					Vector<String> dayNameVector = new Vector<String>(0);
					Vector<String> monthDatesVector = new Vector<String>(0);
					Vector<String> monthNumbersVector = new Vector<String>(0);
					Vector<String> monthNamesVector = new Vector<String>(0);
					
					LocalDateTime timeIterator = boundaries[0].withHour(6);
					int index = 0;
					int dayDifference = (int) ChronoUnit.DAYS.between(boundaries[0], boundaries[1]);
					int weekCount = (int) Math.ceil(dayDifference/7f);
					
					while(index <= dayDifference)
					{
						if(dayNameVector.size() < 7)
							dayNameVector.add(timeIterator.getDayOfWeek().getDisplayName(TextStyle.FULL, Params.DEFAULT_LOCALE).toUpperCase(Params.DEFAULT_LOCALE));
							
						monthDatesVector.add(timeIterator.format(Params.DATE_FORMATTER));
						String nextMonthName = timeIterator.getMonth().getDisplayName(TextStyle.FULL, Params.DEFAULT_LOCALE);
						
						if(!monthNamesVector.contains(nextMonthName))
						{
							monthNamesVector.add(nextMonthName);
							monthNumbersVector.add(timeIterator.format(DateTimeFormatter.ofPattern("MM")));
						}
						
						timeIterator = timeIterator.plusDays(1);
						index++;
						
						if(index == 15)
						{
							currentMonthName = timeIterator.getMonth().getDisplayName(TextStyle.FULL, Params.DEFAULT_LOCALE);
							currentMonthNumber = timeIterator.getMonthValue();
						}
					}
					
					request.setAttribute("year", boundaries[1].getYear());
					request.setAttribute("month", currentMonthName);
					request.setAttribute("monthNumber", currentMonthNumber);
					request.setAttribute("today", Params.DATE_FORMAT.format(new Date()));
					request.setAttribute("weekCount", weekCount);
					request.setAttribute("weekDays", dayNameVector.toArray(new String[dayNameVector.size()]));
					request.setAttribute("monthDates", monthDatesVector.toArray(new String[monthDatesVector.size()]));
					request.setAttribute("monthNumbers", monthNumbersVector.toArray(new String[monthNumbersVector.size()]));
					request.setAttribute("monthNames", monthNamesVector.toArray(new String[monthNamesVector.size()]));
					request.setAttribute("today", Params.DATE_FORMAT.format(new Date()));
					request.setAttribute("events", (Event[]) eventData[0]);
					request.setAttribute("locations", (Location[]) eventData[1]);
					request.setAttribute("location", locIdStr != null ? (Location) ((Location[]) eventData[1])[0] : null);
					
					request.getRequestDispatcher("/view/events.jsp").forward(request, response);
				}
			}
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
	
	private boolean isUserAllowedOnPage(Loginable loggedUser)
	{
		if(loggedUser != null && loggedUser.getState() == LoginableState.ACTIVE)
		{
			LoginRole role = loggedUser.getRole();
			return (role != null && role.getAthleteMod().canRead());
		}
		else
			return false;
	}
	
	private LocalDateTime[] getMonthBoundaries(String yearStr, String monthStr, String directionStr)
	{
		LocalDateTime now = LocalDateTime.now(ZoneId.of(Params.DEFAULT_TIMEZONE.getID()));
		
		int year = yearStr != null ? Integer.parseInt(yearStr) : now.getYear();
		int monthOfYear = monthStr != null ? Integer.parseInt(monthStr) : now.getMonthValue();
		
		if(directionStr != null && directionStr.toLowerCase().equals("next"))
			now = now.withMonth(monthOfYear).withYear(year).plusMonths(1);
		else if(directionStr != null && directionStr.toLowerCase().equals("prev"))
			now = now.withMonth(monthOfYear).withYear(year).minusMonths(1);
		
		LocalDateTime monthEpoch = now.withYear(year)
				.withDayOfMonth(1)
				.with(DayOfWeek.MONDAY)
				.withHour(6).withMinute(0).withSecond(0).withNano(0);
		
		LocalDateTime monthEnd = monthEpoch.plusMonths(1); 
		/*Buraya  Bir Daha Bak! Ayın 1'i Pazartesi ise... */
		monthEnd = monthEpoch.getDayOfMonth() == 1 ? monthEnd.minusDays(1) : monthEnd;
		/*Buraya  Bir Daha Bak*/
		monthEnd = monthEnd.withDayOfMonth(monthEnd.getMonth().length(Year.now().isLeap()))
				.with(DayOfWeek.SUNDAY)
				.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
		
		return new LocalDateTime[] { monthEpoch, monthEnd };
	}
	
	private Object[] getEventsAndLocation(Connection conn, String locIdStr, int year, int month) 
			throws SQLException, ClassNotFoundException
	{
		Event[] events = null;
		Location[] locations = null;
		
		if(locIdStr == null)
		{
			events = Event.findByMonthOfYear(conn, year, month);
			
			if(events != null)
			{
				Vector<Location> locationVector = new Vector<Location>(0);
				
				for(Event nextEvent : events)
				{
					if(!locationVector.contains(nextEvent.getLocation()))
						locationVector.add(nextEvent.getLocation());
				}
				
				if(locationVector.size() > 0)
					locationVector.trimToSize();
				
				locations = locationVector.toArray(new Location[locationVector.size()]);
			}
		}
		else
		{
			Location passedLocation = Location.findById(conn, Integer.parseInt(locIdStr));
			events = Event.findByMonthOfYear_And_Location(conn, year, month, Integer.parseInt(locIdStr));
			locations = new Location[] { passedLocation };
		}
		
		return new Object[] { events, locations };
	}
}
