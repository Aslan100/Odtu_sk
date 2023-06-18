package bordomor.odtu.sk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bordomor.odtu.sk.Params.AgeGroup;
import bordomor.odtu.sk.Params.TimeUnit;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.template.DBTimeTrackable;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.IXmlObject;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.util.DBUtils;

@XMLAndDatabaseValues(tableName = "training", tagName = "training", defaultVariable = "tng")
public class Training extends DBTimeTrackable implements IDBObject, IXmlObject 
{
	private int id = -1;
	private Team team = null;
	private Location location = null;
	private Timestamp startTime = null;
	private Timestamp endTime = null;
	private Trainer trainer = null;
	
	private TimeUnit planType = null;
	private Integer[] plannedTimeValues = null;
	
	private boolean isGroupParent = false;
	private TrainingReccurrence reccurrence = null;
	
	private Athlete[] players = null;
	
	public Training() {}
	
	public Training(int id)
	{
		this.id = id;
	}
	
	public Training(int id, Team team, Location location, Timestamp startTime, Timestamp endTime, Trainer trainer, boolean isGroupParent)
	{
		this.id = id;
		this.team = team;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.trainer = trainer;
		this.isGroupParent = isGroupParent;
	}
	
	public Training(int id, Team team, Location location, Timestamp startTime, Timestamp endTime, Trainer trainer, TimeUnit planType, String plannedTimeValues, boolean isGroupParent)
	{
		this.id = id;
		this.team = team;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.trainer = trainer;
		this.planType = planType;
		this.setPlannedTimeValues(plannedTimeValues);
		this.isGroupParent = isGroupParent;
	}
	
	//Yardımcı Metodlar
	public Training clone()
	{
		Training clone = new Training(this.id, this.team, this.location, this.startTime, this.endTime, this.trainer, this.planType, this.getPlannedTimeValuesString(), this.isGroupParent);
		return clone;
	}
	
	public Training[] generateReccurringTrainings(Connection conn, int conflictMode) 
			throws ClassNotFoundException, SQLException
	{
		LocalDateTime reccurrenceDT = Training.this.startTime.toLocalDateTime().plus(this.reccurrence.reccurrenceRate, ChronoUnit.valueOf(this.reccurrence.reccurrenceUnit.toString()));
		LocalDateTime reccurrenceEndDT = this.reccurrence.reccurrenceEndTime.toLocalDateTime(); 
		
		Vector<Training> trainings = new Vector<Training>(0);
		
		while(reccurrenceDT.isBefore(reccurrenceEndDT))
		{
			Training nextTraining = Training.this.clone();
			nextTraining.setGroupParent(false);
			nextTraining.setPlanType(null);
			nextTraining.setPlannedTimeValues((String) null);
			nextTraining.setReccurrence(this.reccurrence);
			nextTraining.setStartTime(Timestamp.valueOf(reccurrenceDT));
			nextTraining.setEndTime(new Timestamp(Timestamp.valueOf(reccurrenceDT).getTime() + Training.this.getDuration()));
			
			if(conflictMode == 0)
				trainings.add(nextTraining);
			else if(conflictMode == 1)
			{
				boolean hasConflict = Training.checkConflictInDB(conn, nextTraining);
				
				if(!hasConflict)
					trainings.add(nextTraining);
			}
			else if(conflictMode == 2)
			{
				boolean hasConflict = Training.checkConflictInDB(conn, nextTraining);
				
				if(hasConflict)
				{
					trainings.removeAllElements();
					throw new IllegalArgumentException("Conflicting group.");
				}
			}
			
			reccurrenceDT = reccurrenceDT.plus(this.reccurrence.reccurrenceRate, ChronoUnit.valueOf(this.reccurrence.reccurrenceUnit.toString()));
		}
		
		if(trainings.size() > 0)
			trainings.trimToSize();
		
		return trainings.isEmpty() ? null : trainings.toArray(new Training[trainings.size()]);
	}
	
	//XML Bölümü
	@Override
	public void parseFromXMLElement(Element element) {}

	@Override
	public Element makeXMLElement(Document xml, boolean cascadeRelations) 
	{
		try
		{
			Element trainingEl = xml.createElement("training");
			trainingEl.setAttribute("id", this.id + "");
			
			if(this.startTime != null)
				trainingEl.setAttribute("start_time", this.startTime.getTime() + "");
			
			if(this.endTime != null)
				trainingEl.setAttribute("end_time", this.endTime.getTime() + "");
			
			if(this.planType != null)
				trainingEl.setAttribute("plan_type", this.planType.toString());
			
			if(this.plannedTimeValues != null)
				trainingEl.setAttribute("planned_time_values", this.getPlannedTimeValuesString());
			
			trainingEl.setAttribute("is_group_parent", this.isGroupParent + "");
			
			if(cascadeRelations)
			{
				if(this.team != null)
					trainingEl.appendChild(this.team.makeXMLElement(xml, true));
				
				if(this.location != null)
					trainingEl.appendChild(this.location.makeXMLElement(xml, true));
				
				if(this.trainer != null)
					trainingEl.appendChild(this.trainer.makeXMLElement(xml, false));
					
				if(this.trainer != null)
					trainingEl.appendChild(this.trainer.makeXMLElement(xml, false));
				
				if(this.reccurrence != null)
					trainingEl.appendChild(this.reccurrence.makeXMLElement(xml, true));
			}
			
			super.appendTTAttributes(trainingEl);
			
			return trainingEl;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml document or object properties.", ex);
		}
	}

	//VT Bölümü
	@Override
	public void createInDB(Connection conn) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
        PreparedStatement insertSt = null;
        PreparedStatement playerSt = null;
        ResultSet keySet = null;
        
        this.id = -1;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        String insertQuery = ""
    				+ "INSERT INTO "
    				+ "		training "
    				+ "			(team, location, start_time, end_time, trainer, plan_type, planned_time_values, is_group_parent, reccurrence) "
    				+ "VALUES "
    				+ "		(?, ?, ?, ?, ?, ?::time_unit, ?, ?, ?)";
        	
			insertSt = newConn.prepareStatement(insertQuery, new String[] {"id", "creation_time", "last_modified_time"});
    		insertSt.setInt(1, this.team.getId());
    		insertSt.setInt(2, this.location.getId());
    		insertSt.setTimestamp(3, this.startTime);
    		insertSt.setTimestamp(4, this.endTime);
    		insertSt.setObject(5, this.trainer != null ? this.trainer.getId() : null, Types.INTEGER);
    		insertSt.setObject(6, this.planType != null ? this.planType.toString() : null, Types.VARCHAR);
    		insertSt.setObject(7, this.plannedTimeValues != null ? this.getPlannedTimeValuesString() : null, Types.VARCHAR);
    		insertSt.setBoolean(8, this.isGroupParent);
    		insertSt.setObject(9, this.reccurrence != null && this.reccurrence.id > 0 ? this.reccurrence.id : null, Types.INTEGER);
    		insertSt.executeUpdate();
        	
    		keySet = insertSt.getGeneratedKeys();
    		keySet.next();
    		this.setId(keySet.getInt("id"));
    		this.parseTTAttributes(keySet);
    		
    		if(this.isGroupParent && this.reccurrence != null && this.reccurrence.id == -1)
    			this.reccurrence.createInDB(newConn);
	        
    		if(this.players != null)
    		{
    			String playerQuery = ""
						+ "INSERT INTO training_squad (training, player) VALUES (?,  ?)";
				
    			for(Athlete nextPlayer : this.players)
    			{
    				try
    				{
	    				playerSt = newConn.prepareStatement(playerQuery);
	    				playerSt.setInt(1, this.id);
	    				playerSt.setInt(2, nextPlayer.getId());
	    				playerSt.executeUpdate();
    				}
    				catch(Exception ex) {}
    			}
    		}
    	}
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(insertSt);
        	DBUtils.close(playerSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}

	@Override
	public void updateInDB(Connection conn, IDBObject updatingObject) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
        PreparedStatement updateSt = null;
        ResultSet keySet = null;
        
        try
        {
        	Training updatingTraining = (Training) updatingObject;
        	
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			String updateQuery = ""
    				+ "UPDATE "
    				+ "		training "
    				+ "SET "
    				+ "		team = ?, location = ?, start_time = ?, end_time = ?, trainer = ? "
    				+ "WHERE "
    				+ "		id = ?";
        	
    		updateSt = newConn.prepareStatement(updateQuery, new String[] {"id"});
    		updateSt.setInt(1, updatingTraining.team.getId());
    		updateSt.setInt(2, updatingTraining.location.getId());
    		updateSt.setTimestamp(3, updatingTraining.startTime);
    		updateSt.setTimestamp(4, updatingTraining.endTime);
    		updateSt.setObject(5, updatingTraining.trainer != null ? updatingTraining.trainer.getId() : null, Types.INTEGER);
    		updateSt.setInt(6, this.id);
    		
    		if(updateSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
        	
    		keySet = updateSt.getGeneratedKeys();
    		keySet.next();
    		this.setId(keySet.getInt("id"));
    	}
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(updateSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }

	}

	@Override
	public void deleteFromDB(Connection conn) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
        PreparedStatement deleteSt = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			String insertQuery = ""
    				+ "DELETE FROM training WHERE id = ?";
        	
    		deleteSt = newConn.prepareStatement(insertQuery);
    		deleteSt.setInt(1, this.id);
    		deleteSt.executeUpdate();
        	
    		this.id = -1;
    	}
        finally
        {
        	DBUtils.close(deleteSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
    }

	public static boolean checkConflictInDB(Connection conn, Training possibleTraining) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = ""
					+ "SELECT EXISTS(SELECT id FROM training WHERE (end_time > ? AND start_time < ?) OR (start_time < ? AND end_time > ?) OR (start_time > ? AND end_time < ?) AND location = ?)";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setTimestamp(1, possibleTraining.getEndTime());
	        sqlSt.setTimestamp(2, possibleTraining.getEndTime());
	        sqlSt.setTimestamp(3, possibleTraining.getStartTime());
	        sqlSt.setTimestamp(4, possibleTraining.getStartTime());
	        sqlSt.setTimestamp(5, possibleTraining.getStartTime());
	        sqlSt.setTimestamp(6, possibleTraining.getEndTime());
	        sqlSt.setInt(7, possibleTraining.getLocation().getId());
	        rs = sqlSt.executeQuery();
	        
	        if(rs.next())
	        	return rs.getBoolean(1);
	        
			return false;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	//Get-Set
	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
		
		if(this.isGroupParent && this.reccurrence != null && this.reccurrence.id == -1)
			this.reccurrence.setGroupParentId(id);
	}

	public Team getTeam() 
	{
		return team;
	}

	public void setTeam(Team team) 
	{
		this.team = team;
	}

	public Location getLocation() 
	{
		return location;
	}

	public void setLocation(Location location) 
	{
		this.location = location;
	}

	public Timestamp getStartTime() 
	{
		return startTime;
	}

	public void setStartTime(Timestamp startTime) 
	{
		this.startTime = startTime;
	}

	public Timestamp getEndTime() 
	{
		return endTime;
	}

	public void setEndTime(Timestamp endTime) 
	{
		this.endTime = endTime;
	}
	
	public Trainer getTrainer() 
	{
		return trainer;
	}

	public void setTrainer(Trainer trainer) 
	{
		this.trainer = trainer;
	}
	
	public TimeUnit getPlanType() 
	{
		return planType;
	}
	
	public void setPlanType(TimeUnit planType) 
	{
		this.planType = planType;
	}
	
	public Integer[] getPlannedTimeValues() 
	{
		return plannedTimeValues;
	}
	
	public String getPlannedTimeValuesString() 
	{
		String values = null;
		
		if(this.plannedTimeValues != null)
		{
			values = "";
			
			for(Integer nextValue : this.plannedTimeValues)
				values += "," + nextValue.intValue();
			
			values = values.substring(1);
		}
		
		return values;
	}
	
	public void setPlannedTimeValues(Integer[] plannedTimeValues) 
	{
		this.plannedTimeValues = plannedTimeValues;
	}
	
	public void setPlannedTimeValues(String plannedTimeValuesStr) 
	{
		if(plannedTimeValuesStr == null)
			this.plannedTimeValues = null;
		else
		{
			String[] values = plannedTimeValuesStr.split(",");
			this.plannedTimeValues = new Integer[values.length];
			
			for(int i = 0; i < values.length; i++)
				this.plannedTimeValues[i] = Integer.parseInt(values[i]);
		}
	}
	
	public TrainingReccurrence getReccurrence() 
	{
		return reccurrence;
	}
	
	public void setReccurrence(TrainingReccurrence reccurrence) 
	{
		this.reccurrence = reccurrence;
	}
	
	public boolean isGroupParent()
	{
		return isGroupParent;
	}
	
	public void setGroupParent(boolean isGroupParent)
	{
		this.isGroupParent = isGroupParent;
	}
	
	public Athlete[] getPlayers()
	{
		return players;
	}
	
	public void setPlayers(Athlete[] players)
	{
		this.players = players;
	}
	
	public long getDuration()
	{
		if(this.startTime != null && this.endTime != null && this.endTime.getTime() >= this.startTime.getTime())
			return this.endTime.getTime() - this.startTime.getTime();
		
		return -1;
	}

	//Statik Sorgular
	public static Training[] findAll(Connection conn) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = ""
					+ "SELECT " + Training.generateColumnNameString("tr", null) + " FROM training tr";
					
			sqlSt = newConn.prepareStatement(query);
	        rs = sqlSt.executeQuery();
	        
	        Vector<Training> trainings = new Vector<Training>(0);
	        
	        while(rs.next())
	        	trainings.add(Training.parseFromRecord(rs, "tr", null));
	        
	        if(trainings.size() > 0)
	        	trainings.trimToSize();
	        
			return trainings.isEmpty() ? null : trainings.toArray(new Training[trainings.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Training[] findByLocationId(Connection conn, int locationId) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = ""
					+ "SELECT " + Training.generateColumnNameString("tr", null) + " FROM training tr WHERE tr.location = ?";
					
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setInt(1, locationId);
	        rs = sqlSt.executeQuery();
	        
	        Vector<Training> trainings = new Vector<Training>(0);
	        
	        while(rs.next())
	        	trainings.add(Training.parseFromRecord(rs, "tr", null));
	        
	        if(trainings.size() > 0)
	        	trainings.trimToSize();
	        
			return trainings.isEmpty() ? null : trainings.toArray(new Training[trainings.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Training findById(Connection conn, int id) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = ""
					+ "SELECT "
					+ 		Training.generateColumnNameString("tr", "rec") + " "
					+ "FROM "
					+ "		training tr " 
					+ "			LEFT OUTER JOIN training_reccurrence rec "
					+ "				ON (tr.reccurrence = rec.id) "
					+ "WHERE "
					+ "		tr.id = ?";
					
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setInt(1, id);
			rs = sqlSt.executeQuery();
	        
	        Training training = null;
	        
	        if(rs.next())
	        	training = Training.parseFromRecord(rs, "tr", "rec");
	        
	        return training;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Training[] findByWeekOfYear(Connection conn, int year, int week) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = ""
					+ "SELECT " + Training.generateColumnNameString("tr", null) +  " FROM training tr WHERE tr.start_time >= ? AND tr.start_time <= ?";
					
			GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(Params.DEFAULT_LOCALE);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.WEEK_OF_YEAR, week);
			cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setTimestamp(1, new Timestamp(cal.getTime().getTime()));
			sqlSt.setTimestamp(2, new Timestamp(cal.getTime().getTime() + 7*24*60*60*1000l));
			rs = sqlSt.executeQuery();
	        
	        Vector<Training> trainings = new Vector<Training>(0);
	        
	        while(rs.next())
	        	trainings.add(Training.parseFromRecord(rs, "tr", null));
	        
	        if(trainings.size() > 0)
	        	trainings.trimToSize();
	        
			return trainings.isEmpty() ? null : trainings.toArray(new Training[trainings.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Training[] findByWeekOfYear_And_Location(Connection conn, int year, int week, int locationId) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = ""
					+ "SELECT " + Training.generateColumnNameString("tr", null) + " FROM training tr  WHERE tr.start_time >= ? AND tr.start_time <= ? AND tr.location = ?";
					
			GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(Params.DEFAULT_LOCALE);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.WEEK_OF_YEAR, week);
			cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setTimestamp(1, new Timestamp(cal.getTime().getTime()));
			sqlSt.setTimestamp(2, new Timestamp(cal.getTime().getTime() + 7*24*60*60*1000l));
			sqlSt.setInt(3, locationId);
			rs = sqlSt.executeQuery();
	        
	        Vector<Training> trainings = new Vector<Training>(0);
	        
	        while(rs.next())
	        	trainings.add(Training.parseFromRecord(rs, "tr", null));
	        
	        if(trainings.size() > 0)
	        	trainings.trimToSize();
	        
			return trainings.isEmpty() ? null : trainings.toArray(new Training[trainings.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Training[] findForAthlete(Connection conn, Athlete athlete) 
			throws ClassNotFoundException, SQLException, ParseException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = ""
					+ "SELECT "
					+ 		Training.generateColumnNameString("tr", null) + " "
					+ "FROM "
					+ "		training tr, team tm "
					+ "WHERE "
					+ "		tr.team = tm.id "
					+ "		AND tm.branch = ? "
					+ "		AND tm.gender_category = ?::gender "
					+ "		AND tm.age_category = ?::age_group "
					+ "		AND tr.start_time > ? "
					+ "		AND tr.end_time < ? "
					+ "ORDER BY "
					+ "		tr.start_time ASC";
			
			Timestamp startTime = Timestamp.valueOf(LocalDate.now().atStartOfDay());
			Timestamp endTime = Timestamp.valueOf(LocalDate.now().atStartOfDay().plusMonths(1));
			AgeGroup athletesGroup = AgeGroup.getGroup(Params.DATE_FORMATTER.format(athlete.getBirthDate().toLocalDateTime())); 
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setInt(1, athlete.getPrimaryBranch().getId());
			sqlSt.setString(2, athlete.getGender().toString());
			sqlSt.setString(3, athletesGroup.toString());
			sqlSt.setTimestamp(4, startTime);
			sqlSt.setTimestamp(5, endTime);
			
			rs = sqlSt.executeQuery();
	        
	        Vector<Training> trainings = new Vector<Training>(0);
	        
	        while(rs.next())
	        	trainings.add(Training.parseFromRecord(rs, "tr", null));
	        
	        if(trainings.size() > 0)
	        	trainings.trimToSize();
	        
			return trainings.isEmpty() ? null : trainings.toArray(new Training[trainings.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	//Statik Metodlar
	public static Training parseFromRecord(ResultSet rs)
	{
		try
		{
			Training parsedTraining = null;
			int trId = rs.getInt("id"); 
			
			if(!rs.wasNull())
			{
				parsedTraining = new Training(trId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals("team"))
						parsedTraining.setTeam(Team.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("location"))
						parsedTraining.setLocation(Location.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("start_time"))
						parsedTraining.setStartTime(rs.getTimestamp(nextCol));
					else if(nextCol.equals("end_time"))
						parsedTraining.setEndTime(rs.getTimestamp(nextCol));
					else if(nextCol.equals("trainer"))
						parsedTraining.setTrainer(Trainer.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("plan_type"))
						parsedTraining.setPlanType(TimeUnit.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals("planned_time_values"))
						parsedTraining.setPlannedTimeValues(rs.getString(nextCol));
					else if(nextCol.equals("is_group_parent"))
						parsedTraining.setGroupParent(rs.getBoolean(nextCol));
				}
				
				parsedTraining.parseTTAttributes(rs);
			}
				
			return parsedTraining;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
 	
	public static Training parseFromRecord(ResultSet rs, String tableVar, String extensionTableVar)
	{
		try
		{
			if(tableVar == null)
				throw new Exception("Bad table values.");
			
			Training parsedTraining = null;
			int trId = rs.getInt(tableVar + "_id"); 
			
			if(!rs.wasNull())
			{
				parsedTraining = new Training(trId);
				int recId = extensionTableVar != null ? rs.getInt(extensionTableVar + "_id") : -1;
				
				if(recId > 0)
					parsedTraining.setReccurrence(new TrainingReccurrence(recId));
				
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals(tableVar + "_team"))
						parsedTraining.setTeam(Team.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(tableVar + "_location"))
						parsedTraining.setLocation(Location.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(tableVar + "_start_time"))
						parsedTraining.setStartTime(rs.getTimestamp(nextCol));
					else if(nextCol.equals(tableVar + "_end_time"))
						parsedTraining.setEndTime(rs.getTimestamp(nextCol));
					else if(nextCol.equals(tableVar + "_trainer"))
						parsedTraining.setTrainer(Trainer.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(tableVar + "_plan_type"))
						parsedTraining.setPlanType(rs.getString(nextCol) != null ? TimeUnit.valueOf(rs.getString(nextCol)) : null);
					else if(nextCol.equals(tableVar + "_planned_time_values"))
						parsedTraining.setPlannedTimeValues(rs.getString(nextCol));
					else if(nextCol.equals(tableVar + "_is_group_parent"))
						parsedTraining.setGroupParent(rs.getBoolean(nextCol));
					else if(parsedTraining.reccurrence != null && nextCol.equals(extensionTableVar + "_group_parent"))
						parsedTraining.reccurrence.setGroupParentId(rs.getInt(nextCol));
					else if(parsedTraining.reccurrence != null && nextCol.equals(extensionTableVar + "_reccurrence_rate"))
						parsedTraining.reccurrence.setReccurrenceRate(rs.getShort(nextCol));
					else if(parsedTraining.reccurrence != null && nextCol.equals(extensionTableVar + "_reccurrence_unit"))
						parsedTraining.reccurrence.setReccurrenceUnit(TimeUnit.valueOf(rs.getString(nextCol)));
					else if(parsedTraining.reccurrence != null && nextCol.equals(extensionTableVar + "_reccurrence_start_time"))
						parsedTraining.reccurrence.setReccurrenceStartTime(rs.getTimestamp(nextCol));
					else if(parsedTraining.reccurrence != null && nextCol.equals(extensionTableVar + "_reccurrence_end_time"))
						parsedTraining.reccurrence.setReccurrenceEndTime(rs.getTimestamp(nextCol));
				}
				
				parsedTraining.parseTTAttributes(rs, "tr");
			}
				
			return parsedTraining;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static String generateColumnNameString(String tableVar, String extensionTableVar)
	{
		if(tableVar == null)
			throw new IllegalArgumentException("Bad table variable.");
		
		String colNameStr = "";
		
		for(String nextColStr : Training.generateColumnNames(tableVar, extensionTableVar))
		{
			boolean isExtensionCol = extensionTableVar != null && nextColStr.startsWith(extensionTableVar + "_");
			String nextTableVar = !isExtensionCol ? tableVar : extensionTableVar;
			
			String orgColStr = nextColStr.substring(nextTableVar.length() + 1);
			colNameStr += ", " + nextTableVar + "." + orgColStr + " AS " + nextColStr;
		}
		
		colNameStr = colNameStr.substring(2);
		
		return colNameStr;
	}
	
	public static String[] generateColumnNames(String tableVar, String extensionTableVar)
	{
		if(tableVar == null)
			throw new IllegalArgumentException("Bad table variable.");
		
		String[] colNames = new String[extensionTableVar != null ? 18 : 12];
		
		int columnIndex = 0;
		
		colNames[columnIndex] = tableVar + "_id"; columnIndex++;
		colNames[columnIndex] = tableVar + "_team"; columnIndex++;
		colNames[columnIndex] = tableVar + "_location"; columnIndex++;
		colNames[columnIndex] = tableVar + "_start_time"; columnIndex++;
		colNames[columnIndex] = tableVar + "_end_time"; columnIndex++;
		colNames[columnIndex] = tableVar + "_trainer"; columnIndex++;
		colNames[columnIndex] = tableVar + "_plan_type"; columnIndex++;
		colNames[columnIndex] = tableVar + "_planned_time_values"; columnIndex++;
		colNames[columnIndex] = tableVar + "_is_group_parent"; columnIndex++;
		colNames[columnIndex] = tableVar + "_reccurrence"; columnIndex++;
		colNames[columnIndex] = tableVar + "_creation_time"; columnIndex++;
		colNames[columnIndex] = tableVar + "_last_modified_time"; columnIndex++;
		
		if(extensionTableVar != null)
		{
			colNames[columnIndex] = extensionTableVar + "_id"; columnIndex++;
			colNames[columnIndex] = extensionTableVar + "_group_parent"; columnIndex++;
			colNames[columnIndex] = extensionTableVar + "_reccurrence_rate"; columnIndex++;
			colNames[columnIndex] = extensionTableVar + "_reccurrence_unit"; columnIndex++;
			colNames[columnIndex] = extensionTableVar + "_reccurrence_start_time"; columnIndex++;
			colNames[columnIndex] = extensionTableVar + "_reccurrence_end_time"; columnIndex++;
		}
		
		return colNames;
	}
	
	//İç Sınıflar
	public static class TrainingReccurrence implements IXmlObject, IDBObject
	{
		private int id = -1;
		private int groupParentId = -1;
		private short reccurrenceRate = (short) -1;
		private TimeUnit reccurrenceUnit = null;
		private Timestamp reccurrenceStartTime = null;
		private Timestamp reccurrenceEndTime = null;
		
		public TrainingReccurrence() {}
		
		public TrainingReccurrence(int id) 
		{
			this.id = id;
		}
		
		public TrainingReccurrence(int id, int groupParentId, short reccurrenceRate, TimeUnit reccurrenceUnit, Timestamp reccurrenceStartTime, Timestamp reccurrenceEndTime)
		{
			this.id = id;
			this.groupParentId = groupParentId;
			this.reccurrenceRate = reccurrenceRate;
			this.reccurrenceUnit = reccurrenceUnit;
			this.reccurrenceStartTime = reccurrenceStartTime;
			this.reccurrenceEndTime  = reccurrenceEndTime;
		}
		
		//XML Bölümü
		@Override
		public void parseFromXMLElement(Element element) {}

		@Override
		public Element makeXMLElement(Document xml, boolean cascadeRelations) 
		{
			try
			{
				Element reccurrenceEl = xml.createElement("reccurrence");
				reccurrenceEl.setAttribute("id", this.id + "");
				reccurrenceEl.setAttribute("reccurrence_rate", this.reccurrenceRate + "");
				reccurrenceEl.setAttribute("group_parent_id", this.groupParentId + "");
				
				if(this.reccurrenceUnit != null)
					reccurrenceEl.setAttribute("reccurrence_unit", this.reccurrenceUnit.toString());
				
				if(this.reccurrenceStartTime != null)
					reccurrenceEl.setAttribute("reccurrence_start_time", this.reccurrenceStartTime.getTime() + "");
				
				if(this.reccurrenceEndTime != null)
					reccurrenceEl.setAttribute("reccurrence_start_time", this.reccurrenceEndTime.getTime() + "");
				
				return reccurrenceEl;
			}
			catch(Exception ex)
			{
				throw new IllegalArgumentException("Bad xml document or object properties.", ex);
			}
		}
		
		//VT Bölümü
		@Override
		public void createInDB(Connection conn) throws ClassNotFoundException, SQLException 
		{
			Connection newConn = conn;
	        PreparedStatement insertSt = null;
	        ResultSet keySet = null;
	        
	        this.id = -1;
	        
	        try
	        {
		        if(conn == null)
		        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
		        
		        String insertQuery = ""
	    				+ "INSERT INTO "
	    				+ "		training_reccurrence "
	    				+ "			(group_parent, reccurrence_rate, reccurrence_unit, reccurrence_start_time, reccurrence_end_time) "
	    				+ "VALUES "
	    				+ "		(?, ?, ?::time_unit, ?, ?)";
	        	
				insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
	    		insertSt.setInt(1, this.groupParentId);
	    		insertSt.setShort(2, this.reccurrenceRate);
	    		insertSt.setString(3, this.reccurrenceUnit.toString());
	    		insertSt.setTimestamp(4, this.reccurrenceStartTime);
	    		insertSt.setTimestamp(5, this.reccurrenceEndTime);
	    		insertSt.executeUpdate();
	        	
	    		keySet = insertSt.getGeneratedKeys();
	    		keySet.next();
	    		this.setId(keySet.getInt("id"));
	    		
	    		this.assignReccurrenceInDB(newConn);
	    	}
	        catch(IllegalArgumentException iaex)
	        {
	        	if(this.id > 0)
	        	{
	        		try { this.deleteFromDB(newConn); } catch(Exception ex) {}
	        	}
	        }
	        finally
	        {
	        	DBUtils.close(keySet);
	        	DBUtils.close(insertSt);
	        	
	        	if(conn == null)
	        		DBUtils.close(newConn);	
	        }
		}

		@Override
		public void updateInDB(Connection conn, IDBObject newObject) throws ClassNotFoundException, SQLException {}

		@Override
		public void deleteFromDB(Connection conn) throws ClassNotFoundException, SQLException {}
		
		public void assignReccurrenceInDB(Connection conn) throws ClassNotFoundException, SQLException
		{
			Connection newConn = conn;
	        PreparedStatement assignSt = null;
	        
	        try
	        {
		        if(conn == null)
		        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
		        
				String recQuery = "UPDATE training SET reccurrence = ? WHERE id = ?";
				
				assignSt = newConn.prepareStatement(recQuery);
				assignSt.setInt(1, this.id);
				assignSt.setInt(2, this.groupParentId);
	    		
	    		if(assignSt.executeUpdate() != 1)
	    			throw new IllegalArgumentException();
	        }
	        catch(IllegalArgumentException iaex)
	        {
	        	if(this.id > 0)
	        	{
	        		try { this.deleteFromDB(newConn); } catch(Exception ex) {}
	        	}
	        }
	        finally
	        {
	        	DBUtils.close(assignSt);
	        	
	        	if(conn == null)
	        		DBUtils.close(newConn);	
	        }
		}
		
		//Get-Set
		public int getId() 
		{
			return id;
		}
		
		public void setId(int id) 
		{
			this.id = id;
		}
		
		public int getGroupParentId() 
		{
			return groupParentId;
		}
		
		protected void setGroupParentId(int groupParentId)
		{
			this.groupParentId = groupParentId;
		}
		
		public short getReccurrenceRate() 
		{
			return reccurrenceRate;
		}
		
		protected void setReccurrenceRate(short reccurrenceRate) 
		{
			this.reccurrenceRate = reccurrenceRate;
		}
		
		public TimeUnit getReccurrenceUnit() 
		{
			return reccurrenceUnit;
		}
		
		protected void setReccurrenceUnit(TimeUnit reccurrenceUnit) 
		{
			this.reccurrenceUnit = reccurrenceUnit;
		}
		
		public Timestamp getReccurrenceStartTime() 
		{
			return reccurrenceStartTime;
		}
		
		public void setReccurrenceStartTime(Timestamp reccurrenceStartTime) 
		{
			this.reccurrenceStartTime = reccurrenceStartTime;
		}
		
		public Timestamp getReccurrenceEndTime() 
		{
			return reccurrenceEndTime;
		}
		
		public void setReccurrenceEndTime(Timestamp reccurrenceEndTime) 
		{
			this.reccurrenceEndTime = reccurrenceEndTime;
		}
	}
}
