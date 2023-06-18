package bordomor.odtu.sk.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Vector;

import bordomor.odtu.sk.LoginRole;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Training;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(Params.PORTAL_TRAININGS_URI)
public class TrainingsController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

    public TrainingsController() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		Connection conn = null;
		
		try
		{
			String stok = new WebUtils(request, response).getCookieValue("stok");
			String yearStr = request.getParameter("year");
			String weekStr = request.getParameter("week");
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
					LocalDateTime[] boundaries = this.getWeekBoundaries(yearStr, weekStr, directionStr);
					Object[] trainingData = this.getTrainingsAndLocations(conn, locIdStr, boundaries[0].getYear(), boundaries[0].get(WeekFields.of(Params.DEFAULT_LOCALE).weekOfWeekBasedYear()));
					
					Vector<String> timeIntervalVector = new Vector<String>(0);
					Vector<String> dayNameVector = new Vector<String>(0);
					Vector<String> weekDatesVector = new Vector<String>(0);
					
					LocalDateTime timeIterator = boundaries[0].withHour(6);
					
					boolean timesConsumed = false;
					boolean daysConsumed = false;
					
					while(!timesConsumed || !daysConsumed)
					{
						if(!timesConsumed)
						{
							timeIntervalVector.add(timeIterator.format(Params.TIME_FORMATTER));
							timeIterator = timeIterator.plusMinutes(15);
							
							if(timeIterator.format(Params.TIME_FORMATTER).equals("00:00"))
							{
								timeIterator = boundaries[0].withHour(6);
								timesConsumed = true;
							}
						}
						else if(!daysConsumed)
						{
							dayNameVector.add(timeIterator.getDayOfWeek().getDisplayName(TextStyle.SHORT, Params.DEFAULT_LOCALE).toUpperCase(Params.DEFAULT_LOCALE));
							weekDatesVector.add(timeIterator.format(Params.DATE_FORMATTER));
							timeIterator = timeIterator.plusDays(1);
							
							if(dayNameVector.size() == 7)
								daysConsumed = true;
						}
					}
					
					HttpSession session = request.getSession();
					
					if(session.getAttribute("dataMode") != null)
					{
						String dataMode = (String) session.getAttribute("dataMode");
						session.removeAttribute("dataMode");
						request.setAttribute("dataMode", dataMode);
					}
					
					if(session.getAttribute("itemId") != null)
					{
						int itemId = (int) session.getAttribute("itemId");
						session.removeAttribute("itemId");
						request.setAttribute("itemId", itemId);
					}
					
					request.setAttribute("year", boundaries[0].get(WeekFields.of(Params.DEFAULT_LOCALE).weekBasedYear()));
					request.setAttribute("month", boundaries[1].getMonth().getDisplayName(TextStyle.FULL, Params.DEFAULT_LOCALE));
					request.setAttribute("week", boundaries[0].get(WeekFields.of(Params.DEFAULT_LOCALE).weekOfWeekBasedYear()));
					
					request.setAttribute("today", Params.DATE_FORMAT.format(new Date()));
					request.setAttribute("weekDays", dayNameVector.toArray(new String[dayNameVector.size()]));
					request.setAttribute("weekDates", weekDatesVector.toArray(new String[weekDatesVector.size()]));
					request.setAttribute("intervals", timeIntervalVector.toArray(new String[timeIntervalVector.size()]));
					request.setAttribute("trainings", (Training[]) trainingData[0]);
					request.setAttribute("locations", (Location[]) trainingData[1]);
					request.setAttribute("location", locIdStr != null ? (Location) ((Location[]) trainingData[1])[0] : null);
					
					request.getRequestDispatcher("/view/trainings.jsp").forward(request, response);
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
	
	private LocalDateTime[] getWeekBoundaries(String yearStr, String weekStr, String directionStr)
	{
		LocalDateTime now = LocalDateTime.now(ZoneId.of(Params.DEFAULT_TIMEZONE.getID()));
		
		int year = yearStr != null ? Integer.parseInt(yearStr) : now.getYear();
		int weekOfYear = weekStr != null ? Integer.parseInt(weekStr) : now.get(WeekFields.of(Params.DEFAULT_LOCALE).weekOfWeekBasedYear());
		
		LocalDateTime weekEpoch = now.withYear(year)
				.with(WeekFields.of(Params.DEFAULT_LOCALE).weekOfWeekBasedYear(), weekOfYear)
				.with(DayOfWeek.MONDAY)
				.withHour(6).withMinute(0).withSecond(0).withNano(0);
		LocalDateTime weekEnd = weekEpoch.with(DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
		
		if(directionStr != null && directionStr.toLowerCase().equals("next"))
		{
			weekEpoch = weekEpoch.plusWeeks(1);
			weekEnd = weekEnd.plusWeeks(1);
		}
		else if(directionStr != null && directionStr.toLowerCase().equals("prev"))
		{
			weekEpoch = weekEpoch.minusWeeks(1);
			weekEnd = weekEnd.minusWeeks(1);
		}
		
		return new LocalDateTime[] { weekEpoch, weekEnd };
	}
	
	private Object[] getTrainingsAndLocations(Connection conn, String locIdStr, int year, int weekNumber) 
			throws SQLException, ClassNotFoundException
	{
		Training[] trainings = null;
		Location[] locations = null;
		
		if(locIdStr == null)
		{
			trainings = Training.findByWeekOfYear(conn, year, weekNumber);
			
			if(trainings != null)
			{
				Vector<Location> locationVector = new Vector<Location>(0);
				
				for(Training nextTraining : trainings)
				{
					if(!locationVector.contains(nextTraining.getLocation()))
						locationVector.add(nextTraining.getLocation());
				}
				
				if(locationVector.size() > 0)
					locationVector.trimToSize();
				
				locations = locationVector.toArray(new Location[locationVector.size()]);
			}
		}
		else
		{
			Location passedLocation = Location.findById(conn, Integer.parseInt(locIdStr));
			trainings = Training.findByWeekOfYear_And_Location(conn, year, weekNumber, Integer.parseInt(locIdStr));
			locations = new Location[] { passedLocation };
		}
		
		return new Object[] { trainings, locations };
	}
}
