package bordomor.odtu.sk.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.TimeZone;

import org.w3c.dom.Element;

import bordomor.odtu.sk.Params;
import bordomor.util.DBUtils;

public abstract class DBTimeTrackable implements IDBObject
{
	protected Timestamp creationTime = null;
	protected Timestamp lastModifiedTime = null;
	
	protected DBTimeTrackable() {}
	
	protected DBTimeTrackable(Timestamp creationTime, Timestamp lastModifiedTime) 
	{
		this.creationTime = creationTime;
		this.lastModifiedTime = lastModifiedTime;
	}
	
	//VT Özellkleri
	@Override
	public void updateColumnInDB(Connection conn, String columnName, Object newValue, int columnType) 
			throws ClassNotFoundException, SQLException 
	{
		if(columnName == null || this.getId() == -1)
			throw new IllegalArgumentException();
		
		String tableName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tableName();
		
		Connection newConn = conn;
        PreparedStatement updateSt = null;
        ResultSet keySet = null;
        
        try
        {
        	 if(conn == null)
 	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
        	 
        	 String updateQuery = ""
        	 		+ "UPDATE " + tableName + " SET " + columnName + " = ? WHERE id = ?";
        	 
        	 updateSt = newConn.prepareStatement(updateQuery, new String[] { "creation_time", "last_modified_time" });
        	 updateSt.setObject(1, newValue != null ? newValue.toString() : null, columnType);
        	 updateSt.setInt(2, this.getId());
        	 
        	 if(updateSt.executeUpdate() != 1)
 	        	throw new IllegalArgumentException();
        	 
        	 keySet = updateSt.getGeneratedKeys();
        	 keySet.next();
        	 this.parseTTAttributes(keySet);
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
	public void updateColumnInDB(Connection conn, String columnName, Enum<?> newValue, String dbEnumName) 
			throws ClassNotFoundException, SQLException 
	{
		if(columnName == null || this.getId() == -1)
			throw new IllegalArgumentException();
		
		String tableName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tableName();
		
		Connection newConn = conn;
        PreparedStatement updateSt = null;
        ResultSet keySet = null;
        
        try
        {
        	 if(conn == null)
 	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
        	 
        	 String updateQuery = ""
        	 		+ "UPDATE " + tableName + " SET " + columnName + " = ?::" + dbEnumName + " WHERE id = ?";
        	 
        	 updateSt = newConn.prepareStatement(updateQuery, new String[] { "creation_time", "last_modified_time" });
        	 updateSt.setObject(1, newValue != null ? newValue.toString() : null, Types.VARCHAR);
        	 updateSt.setInt(2, this.getId());
        	 
        	 if(updateSt.executeUpdate() != 1)
 	        	throw new IllegalArgumentException();
        	
        	 keySet = updateSt.getGeneratedKeys();
        	 keySet.next();
        	 this.parseTTAttributes(keySet);
        }
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(updateSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	//XML Özellikleri
	public void parseTTAttributes(Element ttEl)
	{
		try
		{
			Timestamp creationTime = ttEl.hasAttribute("creation_time") ? 
					new Timestamp(Long.parseLong(ttEl.getAttribute("creation_time"))) : null;
	
			Timestamp lastModifiedTime = ttEl.hasAttribute("last_modified_time") ? 
					new Timestamp(Long.parseLong(ttEl.getAttribute("last_modified_time"))) : null;
					
			this.creationTime = creationTime;
			this.lastModifiedTime = lastModifiedTime;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml element.", ex);
		}
	}
	
	public void appendTTAttributes(Element ttEl)
	{
		if(this.creationTime != null)
			ttEl.setAttribute("creation_time", this.creationTime.getTime() + "");
		
		if(this.lastModifiedTime != null)
			ttEl.setAttribute("last_modified_time", this.lastModifiedTime.getTime() + "");
	}
	
	public void parseTTAttributes(ResultSet rs)
	{
		try
		{
			Timestamp creationTime =  rs.getTimestamp("creation_time");
			Timestamp lastModifiedTime = rs.getTimestamp("last_modified_time");
			
			this.creationTime = DBTimeTrackable.addTimeZoneShift(creationTime);
			this.lastModifiedTime = DBTimeTrackable.addTimeZoneShift(lastModifiedTime);
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.", ex);
		}
    }
	
	public void parseTTAttributes(ResultSet rs, String tableVar)
	{
		try
		{
			Timestamp creationTime =  rs.getTimestamp(tableVar + "_creation_time");
			Timestamp lastModifiedTime = rs.getTimestamp(tableVar + "_last_modified_time");
			
			this.creationTime = DBTimeTrackable.addTimeZoneShift(creationTime);
			this.lastModifiedTime = DBTimeTrackable.addTimeZoneShift(lastModifiedTime);
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.", ex);
		}
    }
	
	public void parseTTAttributes(ResultSet rs, String ctColumnName, String lmColumnName)
	{
		try
		{
			Timestamp creationTime =  rs.getTimestamp(ctColumnName);
			Timestamp lastModifiedTime = rs.getTimestamp(lmColumnName);
			
			this.creationTime = DBTimeTrackable.addTimeZoneShift(creationTime);
			this.lastModifiedTime = DBTimeTrackable.addTimeZoneShift(lastModifiedTime);
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.", ex);
		}
    }
	
	//Get-Set
	public Timestamp getCreationTime()
	{
		return creationTime;
	}
	
	public void setCreationTime(Timestamp creationTime)
	{
		this.creationTime = creationTime;
	}
	
	public Timestamp getLastModifiedTime()
	{
		return this.lastModifiedTime;
	}
	
	public void setLastModifiedTime(Timestamp lastModifiedTime)
	{
		this.lastModifiedTime = lastModifiedTime;
	}
	
	public String getCreationTime(boolean isoFormatted)
	{
		if(this.creationTime != null)
		{
			if(isoFormatted)
				return Params.ISO8601_DATE_TIME_FORMAT.format(this.creationTime);
			else
				return Params.DATE_TIME_FORMAT.format(this.creationTime);
		}
		else
			return null;
	}
	
	public String getLastModifiedTime(boolean isoFormatted)
	{
		if(this.lastModifiedTime != null)
		{
			if(isoFormatted)
				return Params.ISO8601_DATE_TIME_FORMAT.format(this.lastModifiedTime);
			else
				return Params.DATE_TIME_FORMAT.format(this.lastModifiedTime);
		}
		else
			return null;
	}
	
	//Statik
	public static Timestamp addTimeZoneShift(Timestamp time)
	{
		/*if(time == null)
			return null;
		
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(Params.DEFAULT_TIMEZONE, Params.DEFAULT_LOCALE);
    	cal.setTime(new Date(time.getTime()));
    	cal.add(Calendar.HOUR_OF_DAY, cal.getTimeZone().getRawOffset()/(1000*60*60));
    	
    	return new Timestamp(cal.getTime().getTime());*/
		
		return time;
	}
	
	public static Date addTimeZoneShift(Date time)
	{
		/*if(time == null)
			return null;
		
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(Params.DEFAULT_TIMEZONE, Params.DEFAULT_LOCALE);
    	cal.setTime(new Date(time.getTime()));
    	cal.add(Calendar.HOUR_OF_DAY, cal.getTimeZone().getRawOffset()/(1000*60*60));
    	
    	return new Timestamp(cal.getTime().getTime());*/
		
		return time;
	}
	
	public static Timestamp addTimeZoneShift(Timestamp time, TimeZone zone)
	{
		/*if(time == null || zone == null)
			return null;
		
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(zone, Params.DEFAULT_LOCALE);
    	cal.setTime(new Date(time.getTime()));
    	cal.add(Calendar.HOUR_OF_DAY, cal.getTimeZone().getRawOffset()/(1000*60*60));
    	
    	return new Timestamp(cal.getTime().getTime());*/
		
		return time;
	}
}