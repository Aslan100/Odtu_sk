package bordomor.odtu.sk.servlet.dbman;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Vector;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.TimeUnit;
import bordomor.odtu.sk.Team;
import bordomor.odtu.sk.Trainer;
import bordomor.odtu.sk.Training;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_training.jsp")
public class TrainingManipulator extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public TrainingManipulator() 
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
			//String updateModeStr = request.getParameter("update_mode");
			
			String idStr = request.getParameter("id");
			
			String teamIdStr = request.getParameter("team");
			String trainerIdStr = request.getParameter("trainer");
			String locationIdStr = request.getParameter("location");
			String startDateStr = request.getParameter("start_date");
			String startTimeStr = request.getParameter("start_time");
			
			String planTypeStr = request.getParameter("plan_type");
			String plannedTimeValues = request.getParameter("planned_time_values");
			
			String durationModeStr = request.getParameter("duration_mode");
			String endDateStr = request.getParameter("end_date");
			String endTimeStr = request.getParameter("end_time");
			String durationHourStr = request.getParameter("duration_hour");
			String durationMinuteStr = request.getParameter("duration_minute");
			
			String isReccurringTrainingStr = request.getParameter("is_reccurring_training");
			String reccurrenceRateStr = request.getParameter("reccurrence_rate");
			String reccurrenceUnitStr = request.getParameter("reccurrence_unit");
			String reccurrenceEndDateStr = request.getParameter("reccurrence_end_date");
			
			String conflictModeStr = request.getParameter("conflict_mode");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Loginable user = Loginable.findByStok(conn, stok);
			Training processedTraining = new Training(idStr != null ? Integer.parseInt(idStr) : -1);
			
			if(user == null || user.getRole() == null /*|| user.getRole().getEventMod().canWrite()*/)
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(dataModeStr.equals(Params.DATABASE_DELETE_DATA_MODE))
					processedTraining.deleteFromDB(conn);
				else if(dataModeStr.equals(Params.DATABASE_INSERT_DATA_MODE))
	        	{
					Vector<Training> initialTrainings = new Vector<Training>(0);
					
					Team team = Team.findById(conn, Integer.parseInt(teamIdStr.trim()));
					Trainer trainer = trainerIdStr != null ? new Trainer(Integer.parseInt(trainerIdStr)) : null;
					Location location = new Location(Integer.parseInt(locationIdStr.trim()));
					
					LocalDateTime trainingDT = LocalDateTime.parse(startDateStr + " " + startTimeStr, Params.DATE_TIME_FORMATTER);
					Timestamp startTime = Timestamp.valueOf(trainingDT);
					Timestamp endTime = null;
					
					TimeUnit planType = TimeUnit.valueOf(planTypeStr.trim());
					
					if(planType == TimeUnit.DAYS)
					{
						if(Integer.parseInt(durationModeStr) == 0)
							endTime = Timestamp.valueOf(LocalDateTime.parse(endDateStr + " " + endTimeStr, Params.DATE_TIME_FORMATTER));
						else
							endTime = new Timestamp(startTime.getTime() + (Integer.parseInt(durationHourStr.trim())*60 + Integer.parseInt(durationMinuteStr.trim()))*60*1000l);
						
						boolean isGroupParent = isReccurringTrainingStr != null;
						Training training = new Training(-1, team, location, startTime, endTime, trainer, planType, null, isGroupParent);
						training.setPlanType(isGroupParent ? planType : null);
						training.setPlayers(team.getPlayers());
						initialTrainings.add(training);
					}
					else if(planType == TimeUnit.WEEKS)
					{
						if(plannedTimeValues.trim().length() == 0)
							throw new IllegalArgumentException();
						
						String[] plannedDayIndexes = plannedTimeValues.split(",");
						Training.TrainingReccurrence oneReccurrence = null;
						
						for(String nextDayIndex : plannedDayIndexes)
						{
							while(trainingDT.getDayOfWeek().getValue() != Integer.parseInt(nextDayIndex))
								trainingDT = trainingDT.plusDays(1);
							
							startTime = Timestamp.valueOf(trainingDT);
							
							if(durationModeStr == null)
								endTime = Timestamp.valueOf(LocalDateTime.parse(Params.DATE_FORMATTER.format(trainingDT) + " " + endTimeStr, Params.DATE_TIME_FORMATTER));
							else
								endTime = new Timestamp(startTime.getTime() + (Integer.parseInt(durationHourStr.trim())*60 + Integer.parseInt(durationMinuteStr.trim()))*60*1000l);
							
							boolean isGroupParent = (isReccurringTrainingStr != null || plannedDayIndexes.length > 1) && initialTrainings.size() == 0;
							
							Training nextTraining = new Training(-1, team, location, startTime, endTime, trainer, (isGroupParent ? planType : null), (isGroupParent ? plannedTimeValues : null), isGroupParent);
							nextTraining.setPlayers(team.getPlayers());
							
							if(plannedDayIndexes.length > 1 && isReccurringTrainingStr == null && oneReccurrence == null)
								oneReccurrence = new Training.TrainingReccurrence(-1, -1, (short) 1, TimeUnit.WEEKS, nextTraining.getStartTime(), null);
							
							nextTraining.setReccurrence(oneReccurrence);
							initialTrainings.add(nextTraining);
						}
						
						if(oneReccurrence != null)
							oneReccurrence.setReccurrenceEndTime(initialTrainings.get(initialTrainings.size() - 1).getEndTime());
					}
					
					if(isReccurringTrainingStr != null)
					{
						Training groupOwner = initialTrainings.get(0);
						Training.TrainingReccurrence reccurrence = new Training.TrainingReccurrence(
								-1, 
								groupOwner.getId(), 
								Short.parseShort(reccurrenceRateStr), 
								TimeUnit.valueOf(reccurrenceUnitStr.trim()), 
								groupOwner.getStartTime(), 
								Timestamp.valueOf(LocalDateTime.parse(reccurrenceEndDateStr + " 23:59", Params.DATE_TIME_FORMATTER)));
						
						for(Training nextInitialTraining : initialTrainings)
						{
							nextInitialTraining.setReccurrence(reccurrence);
							this.planReccurringTrainings(conn, nextInitialTraining, Integer.parseInt(conflictModeStr));
						}
					}
					else
					{
						for(Training nextInitialTraining : initialTrainings)
							nextInitialTraining.createInDB(conn);
					}
				}
	        	else if(dataModeStr.equals(Params.DATABASE_UPDATE_DATA_MODE))
	        	{
	        		Training updatingTraining = new Training();
	        		updatingTraining.setTeam(new Team(Integer.parseInt(teamIdStr.trim())));
	        		updatingTraining.setTrainer(trainerIdStr != null ? new Trainer(Integer.parseInt(trainerIdStr)) : null);
	        		updatingTraining.setLocation(new Location(Integer.parseInt(locationIdStr.trim())));
	        		
	        		Timestamp startTime = new Timestamp(Params.DATE_TIME_FORMAT.parse(startDateStr + " " + startTimeStr).getTime());
					Timestamp endTime = null;
					
					if(Integer.parseInt(durationModeStr) == 0)
						endTime = new Timestamp(Params.DATE_TIME_FORMAT.parse(endDateStr + " " + endTimeStr).getTime());
					else
					{
						long trainingDuration = Integer.parseInt(durationHourStr.trim())*60*60*1000l + Integer.parseInt(durationMinuteStr.trim())*60*1000l;
						endTime = new Timestamp(startTime.getTime() + trainingDuration);
					}
					
					updatingTraining.setStartTime(startTime);
					updatingTraining.setEndTime(endTime);
					
					processedTraining.updateInDB(conn, updatingTraining);
        		}
	        	else
	        		throw new IllegalArgumentException();
				
				HttpSession session = request.getSession(false);
				session.setAttribute("dataMode", dataModeStr);
				session.setAttribute("itemId", processedTraining.getId());
				
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
	
	private void planReccurringTrainings(Connection conn, Training training, int conflictMode) 
			throws ClassNotFoundException, SQLException
	{
		training.createInDB(conn);
		
		try
		{
			Training[] group = training.generateReccurringTrainings(conn, conflictMode);
			
			if(group != null)
			{
				for(Training nextTraining : group)
					nextTraining.createInDB(conn);
			}
		}
		catch(IllegalArgumentException iaex) {}
	}
}