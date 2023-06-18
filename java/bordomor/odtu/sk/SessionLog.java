package bordomor.odtu.sk;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import bordomor.odtu.sk.Params.DeviceType;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.IXmlObject;
import bordomor.odtu.sk.template.Loginable;
import bordomor.util.DBUtils;

public class SessionLog implements IXmlObject, IDBObject 
{
	private int id = -1;
	private Loginable loginable = null;
	private int sessionDataId = -1;
	private InetAddress ipAddress = null;
	private DeviceType deviceType = null;
	private Timestamp sessionCreated = null;
	private Timestamp sessionTerminated = null;
	
	public SessionLog() {}
	
	public SessionLog(int id)
	{
		this.id = id;
	}
	
	public SessionLog(int id, Loginable loginable, int sessionDataId, InetAddress ipAddress, DeviceType deviceType, Timestamp sessionCreated, Timestamp sessionTerminated)
	{
		this.id = id;
		this.loginable = loginable;
		this.sessionDataId = sessionDataId;
		this.ipAddress = ipAddress;
		this.deviceType = deviceType;
		this.sessionCreated = sessionCreated;
		this.sessionTerminated = sessionTerminated;
	}
	
	//Xml Bölümü
	@Override
	public void parseFromXMLElement(Element element) {}

	@Override
	public Element makeXMLElement(Document xml, boolean cascadeRelations)
	{
		try
		{
			Element logEl = xml.createElement("session_log");
			logEl.setAttribute("id", this.id + "");
			
			if(this.loginable != null)
				logEl.setAttribute("loginable_id", this.loginable.getLoginableId() + "");
			
			logEl.setAttribute("session_data_id", this.sessionDataId + "");
			
			if(this.ipAddress != null)
				logEl.setAttribute("ip", this.ipAddress.getHostAddress() + "");
			
			if(this.deviceType != null)
				logEl.setAttribute("device_type", this.deviceType.toString() + "");
			
			if(this.sessionCreated != null)
			{
				logEl.setAttribute("session_created", this.sessionCreated.getTime() + "");
				logEl.setAttribute("session_created_str", Params.DATE_TIME_FORMAT.format(new Date(this.sessionCreated.getTime())));
			}
			
			if(this.sessionTerminated != null)
			{
				logEl.setAttribute("session_terminated", this.sessionTerminated.getTime() + "");
				logEl.setAttribute("session_terminated_str", Params.DATE_TIME_FORMAT.format(new Date(this.sessionTerminated.getTime())));
			}
			
			return logEl;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml document.", ex);
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
    				+ "		session_log "
    				+ "			(loginable, session_data, ip, device_type, session_created, session_terminated) "
    				+ "VALUES "
    				+ "		(?, ?, ?::inet, ?::device_type, ?, ?)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		insertSt.setInt(1, this.loginable.getLoginableId());
    		insertSt.setObject(2, this.sessionDataId > 0 ? this.sessionDataId : null, Types.INTEGER);
    		insertSt.setObject(3, this.ipAddress != null ? this.ipAddress.getHostAddress() : null, Types.VARCHAR);
    		insertSt.setString(4, this.deviceType.toString());
    		insertSt.setTimestamp(5, this.sessionCreated);
    		insertSt.setObject(6, this.sessionTerminated != null ? this.sessionTerminated : null, Types.TIMESTAMP);
    		insertSt.executeUpdate();
        	
    		keySet = insertSt.getGeneratedKeys();
    		keySet.next();
    		this.setId(keySet.getInt("id"));
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

	public void terminateSessionInDB(Connection conn) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
        PreparedStatement insertSt = null;
        ResultSet keySet = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			String terminateQuery = ""
    				+ "UPDATE "
    				+ "		session_log "
    				+ "SET "
    				+ "		session_terminated = now() "
    				+ "WHERE "
    				+ "		session_data = ?";
        	
    		insertSt = newConn.prepareStatement(terminateQuery);
    		insertSt.setInt(1, this.sessionDataId);
    		insertSt.executeUpdate();
    		
    		this.setSessionDataId(-1);
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
	public void deleteFromDB(Connection conn) throws ClassNotFoundException, SQLException {}
	
	//Get-set	
	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public Loginable getLoginable() 
	{
		return loginable;
	}

	public void setLoginable(Loginable loginable) 
	{
		this.loginable = loginable;
	}

	public int getSessionDataId() 
	{
		return sessionDataId;
	}

	public void setSessionDataId(int sessionDataId) 
	{
		this.sessionDataId = sessionDataId;
	}

	public InetAddress getIpAddress() 
	{
		return ipAddress;
	}

	public void setIpAddress(InetAddress ipAddress) 
	{
		this.ipAddress = ipAddress;
	}

	public DeviceType getDeviceType() 
	{
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) 
	{
		this.deviceType = deviceType;
	}

	public Timestamp getSessionCreated() 
	{
		return sessionCreated;
	}

	public void setSessionCreated(Timestamp sessionCreated) 
	{
		this.sessionCreated = sessionCreated;
	}

	public Timestamp getSessionTerminated() 
	{
		return sessionTerminated;
	}

	public void setSessionTerminated(Timestamp sessionTerminated) 
	{
		this.sessionTerminated = sessionTerminated;
	}
	
	//Statik Sorgular
	public static SessionLog[] findByLoginableId(Connection conn, int loginableId) 
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
					+ "		slog.* "
					+ "FROM "
					+ "		session_log slog "
					+ "WHERE "
					+ "		slog.loginable = ? "
					+ "ORDER BY "
					+ "		slog.session_created DESC";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, loginableId);
	        
	        rs = sqlSt.executeQuery();
	        
	        Vector<SessionLog> logs = new Vector<SessionLog>(0);
	        
	        while(rs.next())
	        	logs.add(SessionLog.parseFromRecord(rs));
	        
	        if(logs.size() > 0)
	        	logs.trimToSize();
	        
			return logs.isEmpty() ? null : logs.toArray(new SessionLog[logs.size()]);
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
	public static SessionLog parseFirstInstance(Element parentEl)
	{
		NodeList sessionNodes = parentEl.getElementsByTagName("session_log");
		SessionLog log = null;
		
		if(sessionNodes.getLength() > 0)
		{
			log = new SessionLog();
			log.parseFromXMLElement((Element)sessionNodes.item(0));
		}
		
		return log;
	}
	
	public static SessionLog[] parseAll(Element parentEl)
	{
		NodeList sessionNodes = parentEl.getElementsByTagName("session_log");
		Vector<SessionLog> sessionList = new Vector<SessionLog>(0);
		
		for(int i = 0; i < sessionNodes.getLength(); i++)
		{
			SessionLog nextLog = new SessionLog();
			nextLog.parseFromXMLElement((Element) sessionNodes.item(i));
			sessionList.add(nextLog);
		}
		
		if(sessionList.size() > 0)
			sessionList.trimToSize();
        
        return sessionList.isEmpty() ? null : sessionList.toArray(new SessionLog[sessionList.size()]);
	}
	
	public static SessionLog parseFromRecord(ResultSet rs)
	{
		try
		{
			SessionLog parsedLog = null;
			int logId = rs.getInt("id"); 
			
			if(!rs.wasNull())
			{
				parsedLog = new SessionLog(logId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals("loginable"))
					{
						Loginable l = new Athlete();
						l.setLoginableId(rs.getInt(nextCol));
						parsedLog.setLoginable(l);
					}
					else if(nextCol.equals("session_data"))
						parsedLog.setSessionDataId(rs.getInt(nextCol));
					else if(nextCol.equals("ip"))
						parsedLog.setIpAddress(InetAddress.getByName(rs.getString(nextCol)));
					else if(nextCol.equals("device_type"))
						parsedLog.setDeviceType(DeviceType.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals("session_created"))
						parsedLog.setSessionCreated(rs.getTimestamp(nextCol));
					else if(nextCol.equals("session_terminated"))
						parsedLog.setSessionTerminated(rs.getTimestamp(nextCol));
				}
			}
				
			return parsedLog;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
}
