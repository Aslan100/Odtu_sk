package bordomor.odtu.sk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bordomor.odtu.sk.Params.EventType;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Params.MemberRole;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.template.DBTimeTrackable;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.IXmlObject;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.util.DBUtils;

@XMLAndDatabaseValues(tableName = "event", tagName = "event", defaultVariable = "evt")
public class Event extends DBTimeTrackable implements IDBObject, IXmlObject 
{
	private int id = -1;
	private String name = null;
	private EventType type = null;
	private Branch branch = null;
	private Location location = null;
	private Timestamp startTime = null;
	private Timestamp endTime = null;
	private Location meetingPoint = null;
	private Timestamp meetingTime = null;
	private Location accomodationLocation = null;
	private Location breakUpPoint = null;
	private Timestamp breakUpTime = null;
	private float parentContributionAmount = -1f;
	
	private Loginable[] squad = null;
	
	public Event() {}
	
	public Event(int id)
	{
		this.id = id;
	}
	
	public Event(int id, String name, EventType type, Branch branch, Location location, Timestamp startTime, Timestamp endTime, 
			Location meetingPoint, Timestamp meetingTime, Location accomodationLocation, Location breakUpPoint, Timestamp breakUpTime, float parentContributionAmount)
	{
		this.id = id;
		this.name = name;
		this.type = type;
		this.branch = branch;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.meetingPoint = meetingPoint;
		this.meetingTime = meetingTime;
		this.accomodationLocation = accomodationLocation;
		this.breakUpPoint = breakUpPoint;
		this.breakUpTime = breakUpTime;
		this.parentContributionAmount = parentContributionAmount;
	}
	
	//XML Bölümü
	@Override
	public void parseFromXMLElement(Element element) {}

	@Override
	public Element makeXMLElement(Document xml, boolean cascadeRelations) 
	{
		try
		{
			Element eventEl = xml.createElement("event");
			eventEl.setAttribute("id", this.id + "");
			
			if(this.name != null)
				eventEl.setAttribute("name", this.name);
			
			if(this.type != null)
				eventEl.setAttribute("type", this.type.toString());
			
			if(this.branch != null)
				eventEl.setAttribute("branch", this.branch.toString());
			
			if(this.startTime != null)
				eventEl.setAttribute("start_time", this.startTime.getTime() + "");
			
			if(this.endTime != null)
				eventEl.setAttribute("end_time", this.endTime.getTime() + "");
			
			if(this.meetingTime != null)
				eventEl.setAttribute("meeting_time", this.meetingTime.getTime() + "");
			
			if(this.breakUpTime != null)
				eventEl.setAttribute("break_up_time", this.breakUpTime.getTime() + "");
			
			eventEl.setAttribute("parent_contribution_amount", this.parentContributionAmount + "");
			
			if(cascadeRelations)
			{
				if(this.location != null)
					eventEl.appendChild(this.location.makeXMLElement(xml, true));
				
				if(this.meetingPoint != null)
				{
					Element meetingPointEl = xml.createElement("meeting_point");
					meetingPointEl.appendChild(this.meetingPoint.makeXMLElement(xml, false));
					eventEl.appendChild(meetingPointEl);
				}
				
				if(this.accomodationLocation != null)
				{
					Element accomodationLocationEl = xml.createElement("accomodation_location");
					accomodationLocationEl.appendChild(this.accomodationLocation.makeXMLElement(xml, false));
					eventEl.appendChild(accomodationLocationEl);
				}
				
				if(this.breakUpPoint != null)
				{
					Element breakUpPointEl = xml.createElement("break_up_point");
					breakUpPointEl.appendChild(this.breakUpPoint.makeXMLElement(xml, false));
					eventEl.appendChild(breakUpPointEl);
				}
				
				if(this.squad != null)
				{
					Element squadEl = xml.createElement("squad");
					
					for(Loginable nextMember : this.squad)
						squadEl.appendChild(nextMember.makeXMLElement(xml, false));
					
					eventEl.appendChild(squadEl);
				}
			}
			
			super.appendTTAttributes(eventEl);
			
			return eventEl;
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
    				+ "		event "
    				+ "			(name, type, branch, location, start_time, end_time, "
    				+ "				meeting_point, meeting_time, accomodation_location, break_up_point, break_up_time, "
    				+ "				parent_contribution_amount) "
    				+ "VALUES "
    				+ "		(?, ?::event_type, ?::branch, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id", "creation_time", "last_modified_time"});
    		insertSt.setString(1, this.name.trim());
    		insertSt.setString(2, this.type.toString());
    		insertSt.setString(3, this.branch.toString());
    		insertSt.setInt(4, this.location.getId());
    		insertSt.setTimestamp(5, this.startTime);
    		insertSt.setTimestamp(6, this.endTime);
    		insertSt.setObject(7, this.meetingPoint != null ? this.meetingPoint.getId() : null, Types.INTEGER);
    		insertSt.setObject(8, this.meetingTime != null ? this.meetingTime : null, Types.TIMESTAMP);
    		insertSt.setObject(9, this.accomodationLocation != null ? this.accomodationLocation.getId() : null, Types.INTEGER);
    		insertSt.setObject(10, this.breakUpPoint != null ? this.breakUpPoint.getId() : null, Types.INTEGER);
    		insertSt.setObject(11, this.breakUpTime != null ? this.breakUpTime : null, Types.TIMESTAMP);
    		insertSt.setFloat(12, this.parentContributionAmount);
    		insertSt.executeUpdate();
        	
    		keySet = insertSt.getGeneratedKeys();
    		keySet.next();
    		this.setId(keySet.getInt("id"));
    		this.parseTTAttributes(keySet);
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
	public void updateInDB(Connection conn, IDBObject updatingObject) throws ClassNotFoundException, SQLException 
	{
		/*Connection newConn = conn;
        PreparedStatement updateSt = null;
        ResultSet keySet = null;
        
        try
        {
        	Event updatingTraining = (Event) updatingObject;
        	
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
        }*/
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
	        
			String deleteQuery = ""
    				+ "DELETE FROM event WHERE id = ?";
        	
    		deleteSt = newConn.prepareStatement(deleteQuery);
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
	
	public void addMemberInDB(Connection conn, int loginableId, MemberRole role) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement memberSt = null;
        ResultSet keySet = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			String insertQuery = ""
    				+ "INSERT INTO "
    				+ "		event_squad "
    				+ "			(event, squad_member, member_role) "
    				+ "VALUES "
    				+ "		(?, ?, ?::member_role)";
        	
    		memberSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		memberSt.setInt(1, this.id);
    		memberSt.setInt(2, loginableId);
    		memberSt.setString(3, role.toString());
    		memberSt.executeUpdate();
        	
    		keySet = memberSt.getGeneratedKeys();
    		keySet.next();
    	}
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(memberSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	public void deleteMemberInDB(Connection conn, int loginableId) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement deleteMemberSt = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			String deleteQuery = ""
    				+ "DELETE FROM event_squad WHERE squad_member = ? AND event = ?";
    		
			deleteMemberSt = newConn.prepareStatement(deleteQuery);
			deleteMemberSt.setInt(1, loginableId);
			deleteMemberSt.setInt(2, this.id);
			deleteMemberSt.executeUpdate();
        }
        finally
        {
        	DBUtils.close(deleteMemberSt);
        	
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
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public EventType getType() 
	{
		return type;
	}
	
	public void setType(EventType type) 
	{
		this.type = type;
	}
	
	public Branch getBranch()
	{
		return branch;
	}
	
	public void setBranch(Branch branch)
	{
		this.branch = branch;
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
	
	public Location getMeetingPoint() 
	{
		return meetingPoint;
	}
	
	public void setMeetingPoint(Location meetingPoint) 
	{
		this.meetingPoint = meetingPoint;
	}
	
	public Timestamp getMeetingTime() 
	{
		return meetingTime;
	}
	
	public void setMeetingTime(Timestamp meetingTime) 
	{
		this.meetingTime = meetingTime;
	}
	
	public Location getAccomodationLocation() 
	{
		return accomodationLocation;
	}
	
	public void setAccomodationLocation(Location accomodationLocation) 
	{
		this.accomodationLocation = accomodationLocation;
	}
	
	public Location getBreakUpPoint() 
	{
		return breakUpPoint;
	}
	
	public void setBreakUpPoint(Location breakUpPoint) 
	{
		this.breakUpPoint = breakUpPoint;
	}
	
	public Timestamp getBreakUpTime() 
	{
		return breakUpTime;
	}
	
	public void setBreakUpTime(Timestamp breakUpTime) 
	{
		this.breakUpTime = breakUpTime;
	}
	
	public float getParentContributionAmount() 
	{
		return parentContributionAmount;
	}
	
	public void setParentContributionAmount(float parentContributionAmount) 
	{
		this.parentContributionAmount = parentContributionAmount;
	}
	
	public Loginable[] getSquad() 
	{
		return squad;
	}
	
	public void setSquad(Loginable[] squad) 
	{
		this.squad = squad;
	}
	
	public long getDuration()
	{
		if(this.startTime != null && this.endTime != null && this.endTime.getTime() >= this.startTime.getTime())
			return this.endTime.getTime() - this.startTime.getTime();
		
		return -1;
	}
	
	//Statik Sorgular
	public static Event[] findAll(Connection conn) 
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
					+ "SELECT * FROM event";
					
			sqlSt = newConn.prepareStatement(query);
	        rs = sqlSt.executeQuery();
	        
	        Vector<Event> events = new Vector<Event>(0);
	        
	        while(rs.next())
	        {
	        	Event nextEvent = new Event();
	        	nextEvent.setId(rs.getInt("id"));
	        	nextEvent.setName(rs.getString("name"));
	        	nextEvent.setType(EventType.valueOf(rs.getString("type")));
	        	nextEvent.setBranch(Branch.findById(rs.getStatement().getConnection(), rs.getInt("branch")));
	        	nextEvent.setLocation(Location.findById(newConn, rs.getInt("location")));
	        	nextEvent.setStartTime(rs.getTimestamp("start_time"));
	        	nextEvent.setEndTime(rs.getTimestamp("end_time"));
	        	nextEvent.setMeetingPoint(rs.getInt("meeting_point") > 0 ? Location.findById(newConn, rs.getInt("meeting_point")) : null);
	        	nextEvent.setMeetingTime(rs.getTimestamp("meeting_time"));
	        	nextEvent.setAccomodationLocation(rs.getInt("accomodation_location") > 0 ? Location.findById(newConn, rs.getInt("accomodation_location")) : null);
	        	nextEvent.setBreakUpPoint(rs.getInt("break_up_point") > 0 ? Location.findById(newConn, rs.getInt("break_up_point")) : null);
	        	nextEvent.setBreakUpTime(rs.getTimestamp("break_up_time"));
	        	nextEvent.setParentContributionAmount(rs.getFloat("parent_contribution_amount"));
	        	
	        	events.add(nextEvent);
	        }
	        
	        if(events.size() > 0)
	        	events.trimToSize();
	        
			return events.isEmpty() ? null : events.toArray(new Event[events.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Loginable[] findMembersOfEvent(Connection conn, Event event) 
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
					+ "SELECT * FROM event_squad WHERE event = ?";
					
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setInt(1, event.getId());
	        rs = sqlSt.executeQuery();
	        
	        Vector<Loginable> members = new Vector<Loginable>(0);
	        
	        while(rs.next())
	        {
	        	Loginable nextLoginable = Loginable.findByLoginableId(newConn, rs.getInt("squad_member"));
	        	
	        	if(nextLoginable.getState() == LoginableState.ACTIVE)
	        		members.add(nextLoginable);
	        }
	        
	        if(members.size() > 0)
	        	members.trimToSize();
	        
			return members.isEmpty() ? null : members.toArray(new Loginable[members.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Event findById(Connection conn, int id) 
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
					+ "SELECT * FROM event WHERE id = ?";
					
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setInt(1, id);
			rs = sqlSt.executeQuery();
	        
	        Event event = null;
	        
	        if(rs.next())
	        {
	        	event = new Event(rs.getInt("id"));
	        	event.setName(rs.getString("name"));
	        	event.setType(EventType.valueOf(rs.getString("type")));
	        	event.setBranch(Branch.findById(rs.getStatement().getConnection(), rs.getInt("branch")));
	        	event.setLocation(Location.findById(newConn, rs.getInt("location")));
	        	event.setStartTime(rs.getTimestamp("start_time"));
	        	event.setEndTime(rs.getTimestamp("end_time"));
	        	event.setMeetingPoint(rs.getInt("meeting_point") > 0 ? Location.findById(newConn, rs.getInt("meeting_point")) : null);
	        	event.setMeetingTime(rs.getTimestamp("meeting_time"));
	        	event.setAccomodationLocation(rs.getInt("accomodation_location") > 0 ? Location.findById(newConn, rs.getInt("accomodation_location")) : null);
	        	event.setBreakUpPoint(rs.getInt("break_up_point") > 0 ? Location.findById(newConn, rs.getInt("break_up_point")) : null);
	        	event.setBreakUpTime(rs.getTimestamp("break_up_time"));
	        	event.setParentContributionAmount(rs.getFloat("parent_contribution_amount"));
	        }
	        
	        return event;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Event[] findByMonthOfYear(Connection conn, int year, int month) 
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
					+ "SELECT * FROM event WHERE start_time >= ? AND start_time <= ?";
					
			GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(Params.DEFAULT_LOCALE);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date monthBeginningTime = cal.getTime();
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Date monthEndingTime = cal.getTime();
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setTimestamp(1, new Timestamp(monthBeginningTime.getTime()));
			sqlSt.setTimestamp(2, new Timestamp(monthEndingTime.getTime()));
			rs = sqlSt.executeQuery();
	        
			Vector<Event> events = new Vector<Event>(0);
	        
	        while(rs.next())
	        {
	        	Event nextEvent = new Event();
	        	nextEvent.setId(rs.getInt("id"));
	        	nextEvent.setName(rs.getString("name"));
	        	nextEvent.setType(EventType.valueOf(rs.getString("type")));
	        	nextEvent.setBranch(Branch.findById(rs.getStatement().getConnection(), rs.getInt("branch")));
	        	nextEvent.setLocation(Location.findById(newConn, rs.getInt("location")));
	        	nextEvent.setStartTime(rs.getTimestamp("start_time"));
	        	nextEvent.setEndTime(rs.getTimestamp("end_time"));
	        	nextEvent.setMeetingPoint(rs.getInt("meeting_point") > 0 ? Location.findById(newConn, rs.getInt("meeting_point")) : null);
	        	nextEvent.setMeetingTime(rs.getTimestamp("meeting_time"));
	        	nextEvent.setAccomodationLocation(rs.getInt("accomodation_location") > 0 ? Location.findById(newConn, rs.getInt("accomodation_location")) : null);
	        	nextEvent.setBreakUpPoint(rs.getInt("break_up_point") > 0 ? Location.findById(newConn, rs.getInt("break_up_point")) : null);
	        	nextEvent.setBreakUpTime(rs.getTimestamp("break_up_time"));
	        	nextEvent.setParentContributionAmount(rs.getFloat("parent_contribution_amount"));
	        	
	        	events.add(nextEvent);
	        }
	        
	        if(events.size() > 0)
	        	events.trimToSize();
	        
			return events.isEmpty() ? null : events.toArray(new Event[events.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Event[] findByMonthOfYear_And_Location(Connection conn, int year, int month, int locationId) 
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
					+ "SELECT * FROM event WHERE start_time >= ? AND start_time <= ? AND location = ?";
					
			GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(Params.DEFAULT_LOCALE);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date monthBeginningTime = cal.getTime();
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Date monthEndingTime = cal.getTime();
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setTimestamp(1, new Timestamp(monthBeginningTime.getTime()));
			sqlSt.setTimestamp(2, new Timestamp(monthEndingTime.getTime()));
			sqlSt.setInt(3, locationId);
			rs = sqlSt.executeQuery();
	        
			Vector<Event> events = new Vector<Event>(0);
	        
	        while(rs.next())
	        {
	        	Event nextEvent = new Event();
	        	nextEvent.setId(rs.getInt("id"));
	        	nextEvent.setName(rs.getString("name"));
	        	nextEvent.setType(EventType.valueOf(rs.getString("type")));
	        	nextEvent.setBranch(Branch.findById(rs.getStatement().getConnection(), rs.getInt("branch")));
	        	nextEvent.setLocation(Location.findById(newConn, rs.getInt("location")));
	        	nextEvent.setStartTime(rs.getTimestamp("start_time"));
	        	nextEvent.setEndTime(rs.getTimestamp("end_time"));
	        	nextEvent.setMeetingPoint(rs.getInt("meeting_point") > 0 ? Location.findById(newConn, rs.getInt("meeting_point")) : null);
	        	nextEvent.setMeetingTime(rs.getTimestamp("meeting_time"));
	        	nextEvent.setAccomodationLocation(rs.getInt("accomodation_location") > 0 ? Location.findById(newConn, rs.getInt("accomodation_location")) : null);
	        	nextEvent.setBreakUpPoint(rs.getInt("break_up_point") > 0 ? Location.findById(newConn, rs.getInt("break_up_point")) : null);
	        	nextEvent.setBreakUpTime(rs.getTimestamp("break_up_time"));
	        	nextEvent.setParentContributionAmount(rs.getFloat("parent_contribution_amount"));
	        	
	        	events.add(nextEvent);
	        }
	        
	        if(events.size() > 0)
	        	events.trimToSize();
	        
			return events.isEmpty() ? null : events.toArray(new Event[events.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
}
