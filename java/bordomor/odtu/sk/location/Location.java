package bordomor.odtu.sk.location;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.LocationType;
import bordomor.odtu.sk.template.DBTimeTrackable;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.IXmlObject;
import bordomor.util.DBUtils;

public class Location extends DBTimeTrackable implements IXmlObject, IDBObject
{
	private int id = -1;
	private String name = null;
	private String description = null;
	private Color representingColour = null;
	private Address address = null;
	private LocationType type = null;
	private Long[] weekTotals = null;
	
	public Location() {}
	
	public Location(int id) 
	{
		this.id = id;
	}
	
	public Location(int id, String name, String description, Color representingColour, Address address, LocationType type)
	{
		this.id = id;
		this.name = name;
		this.description = description;
		this.representingColour = representingColour;
		this.setAddress(address);
		this.type = type;
	}
	
	//Yardımcı Metodlar
	@Override
	public boolean equals(Object comparedObject) 
	{
		if(comparedObject == this)
			return true;
		else if(comparedObject == null || !(comparedObject instanceof Location))
			return false;
		else
		{
			Location comparedLoc = (Location) comparedObject;
			
			try
			{
				return (this.id == comparedLoc.id && this.name.equals(comparedLoc.getName()));
			}
			catch(Exception ex)
			{
				return this.hashCode() == comparedLoc.hashCode();
			}
		}
	}
	
	@Override
    public int hashCode() 
	{
        final int prime = 5;
        
        int result = 1;
        result = prime*result + (int) (this.id ^ (this.id >>> 32));
        result = prime*result + (this.name != null ? this.name.hashCode() : 0);
        
        return result;
    }
	
	//XML Bölümü
	@Override
	public void parseFromXMLElement(Element locEl) 
	{
		try
		{
			this.id = Integer.parseInt(locEl.getAttribute("id"));
			
			if(locEl.hasAttribute("name"))
				this.name = locEl.getAttribute("name");
			
			if(locEl.hasAttribute("description"))
				this.description = locEl.getAttribute("description");
			
			if(locEl.hasAttribute("representing_colour"))
				this.setRepresentingColour(locEl.getAttribute("representing_colour"));
			
			this.address = Address.parseFirstInstance(locEl);
			super.parseTTAttributes(locEl);
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml element.", ex);
		}	
	}

	@Override
	public Element makeXMLElement(Document xml, boolean cascadeRelations)
	{
		try
		{
			Element locEl = xml.createElement("location");
			locEl.setAttribute("id", this.id + "");
			
			if(this.name != null)
				locEl.setAttribute("name", this.name);
			
			if(this.description != null)
				locEl.setAttribute("description", this.description);
			
			if(this.representingColour != null)
				locEl.setAttribute("representing_colour", this.getRepresentingColourHex());
			
			if(this.weekTotals != null)
				locEl.setAttribute("week_totals", this.weekTotals[0] + "-" + this.weekTotals[1] + "-" + this.weekTotals[2] + "-" + this.weekTotals[3] + "-" + this.weekTotals[4] + "-" + this.weekTotals[5] + "-" + this.weekTotals[6]);
			
			if(cascadeRelations && this.address != null)
				locEl.appendChild(this.address.makeXMLElement(xml, cascadeRelations));
			
			super.appendTTAttributes(locEl);
			
			return locEl;
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
		ResultSet locKeySet = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String insertQuery = ""
					+ "INSERT INTO location "
					+ "		(name, description, representing_colour, type) "
					+ "VALUES "
					+ "		(?, ?, ?, ?::location_type)";
			
			insertSt = newConn.prepareStatement(insertQuery, new String[] {"id", "creation_time", "last_modified_time"});
	        insertSt.setString(1, this.name.trim());
	        insertSt.setObject(2, this.description == null ? null : this.description.trim(), Types.VARCHAR);
	        insertSt.setString(3, this.getRepresentingColourHex());
	        insertSt.setString(4, this.type.toString());
	        
	        if(insertSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        locKeySet = insertSt.getGeneratedKeys();
	        locKeySet.next();
	        
	        this.id = locKeySet.getInt("id");
	        this.parseTTAttributes(locKeySet);
	        
	        this.address.createInDB(newConn);
		}
		catch(Exception ex)
		{
			if(this.id > 0)
				this.deleteFromDB(newConn);
			
			throw ex;
		}
		finally
		{
			DBUtils.close(locKeySet);
			DBUtils.close(insertSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}

	@Override
	public void updateInDB(Connection conn, IDBObject newLoc) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
		PreparedStatement updateSt = null;
		ResultSet keySet = null;
		
		try
		{
			Location updatingLoc = (Location) newLoc;
			
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String updateQuery = ""
					+ "UPDATE "
					+ "		location "
					+ "SET "
					+ "		name = ?, description = ?, representing_colour = ? "
					+ "WHERE "
					+ "		id = ?";
			
			updateSt = newConn.prepareStatement(updateQuery, new String[] {"id", "creation_time", "last_modified_time"});
			updateSt.setString(1, updatingLoc.name.trim());
			updateSt.setObject(2, updatingLoc.description == null ? null : updatingLoc.description.trim(), Types.VARCHAR);
			updateSt.setString(3, updatingLoc.getRepresentingColourHex());
			updateSt.setInt(4, this.id);
			
	        if(updateSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        keySet = updateSt.getGeneratedKeys();
	        keySet.next();
	        
	        this.id = keySet.getInt("id");
	        this.name = updatingLoc.name;
	        this.description = updatingLoc.description;
	        
	        this.address.updateInDB(conn, updatingLoc.address);
	        super.parseTTAttributes(keySet);
	    }
		catch(Exception ex)
		{
			throw ex;
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
			
			String deleteQuery = "DELETE FROM location WHERE id = ?";
			deleteSt = newConn.prepareStatement(deleteQuery);
	        deleteSt.setInt(1, this.id);
	        
	        if(deleteSt.executeUpdate() < 1)
	        	throw new IllegalArgumentException();
	        
	        this.id = -1;
	        this.address = null;
	    }
		catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			DBUtils.close(deleteSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}

	//Get-set
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
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public Color getRepresentingColour() 
	{
		return representingColour;
	}
	
	public String getRepresentingColourHex()
	{
		if(this.representingColour != null)
			return "#" + Integer.toHexString(this.representingColour.getRGB()).substring(2).toLowerCase();
		
		return null;
	}
	
	public void setRepresentingColour(Color representingColour) 
	{
		this.representingColour = representingColour;
	}
	
	public void setRepresentingColour(String representingColourHex) 
	{
		this.representingColour = representingColourHex != null ? Color.decode(representingColourHex) : null;
	}
	
	public Address getAddress() 
	{
		return address;
	}
	
	public void setAddress(Address address) 
	{
		this.address = address;
		
		if(this.address  != null)
			this.address.setLocation(this);
	}
	
	public LocationType getType()
	{
		return type;
	}
	
	public void setType(LocationType type)
	{
		this.type = type;
	}
	
	public void setWeekTotals(Long[] weekTotals)
	{
		this.weekTotals = weekTotals;
	}
	
	//Statik sorgular
	public static Location[] findTrainingLocations(Connection conn) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement selectSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = null;
				
			query = "SELECT "
					+ "		l.id, l.name, l.description, l.representing_colour, l.type, l.creation_time, l.last_modified_time, "
					+ "		a.id AS adr_id, a.address_string AS adr_str, a.post_code AS adr_p_code, a.latitude AS adr_lat, a.longitude AS adr_long, "
					+ "		ds.id AS ds_id, ds.name AS ds_name, ct.id AS ct_id, ct.name AS ct_name, ct.code AS ct_code "
					+ "FROM "
					+ "		location l, "
					+ "		address a "
					+ "			LEFT OUTER JOIN "
					+ "				district ds ON (ds.id = a.district), "
					+ "		city ct "
					+ "WHERE "
					+ "		a.location = l.id "
					+ "		AND ct.id = a.city "
					+ "		AND l.type = ?::location_type "
					+ "ORDER BY "
					+ "		l.name ASC, ct.name ASC";
						
			selectSt = newConn.prepareStatement(query);
			selectSt.setString(1, LocationType.FACILITY.toString());
		    rs = selectSt.executeQuery();
	        
			Vector<Location> locations = new Vector<Location>(0);
	        String[] addressCols = null;
			
	        while(rs.next())
	        {
	        	Location nextLoc = Location.parseFromRecord(rs);
	        	
	        	if(addressCols == null)
	        		addressCols = new String[] { "adr_id", "adr_str", "adr_p_code", "adr_lat", "adr_long", "ds_id", "ds_name", "ct_id", "ct_name", "ct_code"};
	        	
	        	Address nextAddress = Address.parseFromRecord(rs, addressCols);
	        	nextLoc.setAddress(nextAddress);
	        	
	        	locations.add(nextLoc);
	        }
	        
	        if(locations.size() > 0)
	        	locations.trimToSize();
	        
	        return locations.isEmpty() ? null : locations.toArray(new Location[locations.size()]);
	    }
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(selectSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static Location[] findEventLocations(Connection conn) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement selectSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = null;
				
			query = "SELECT "
					+ "		l.id, l.name, l.description, l.representing_colour, l.type, l.creation_time, l.last_modified_time, "
					+ "		a.id AS adr_id, a.address_string AS adr_str, a.post_code AS adr_p_code, a.latitude AS adr_lat, a.longitude AS adr_long, "
					+ "		ds.id AS ds_id, ds.name AS ds_name, ct.id AS ct_id, ct.name AS ct_name, ct.code AS ct_code "
					+ "FROM "
					+ "		location l, "
					+ "		address a "
					+ "			LEFT OUTER JOIN "
					+ "				district ds ON (ds.id = a.district), "
					+ "		city ct "
					+ "WHERE "
					+ "		a.location = l.id "
					+ "		AND ct.id = a.city "
					+ "		AND (l.type = ?::location_type  "
					+ "			OR l.type = ?::location_type) "
					+ "ORDER BY "
					+ "		l.type ASC, l.name ASC, ct.name ASC";
						
			selectSt = newConn.prepareStatement(query);
			selectSt.setString(1, LocationType.FACILITY.toString());
			selectSt.setString(2, LocationType.EVENT_POINT.toString());
		    rs = selectSt.executeQuery();
	        
			Vector<Location> locations = new Vector<Location>(0);
	        String[] addressCols = null;
			
	        while(rs.next())
	        {
	        	Location nextLoc = Location.parseFromRecord(rs);
	        	
	        	if(addressCols == null)
	        		addressCols = new String[] { "adr_id", "adr_str", "adr_p_code", "adr_lat", "adr_long", "ds_id", "ds_name", "ct_id", "ct_name", "ct_code"};
	        	
	        	Address nextAddress = Address.parseFromRecord(rs, addressCols);
	        	nextLoc.setAddress(nextAddress);
	        	
	        	locations.add(nextLoc);
	        }
	        
	        if(locations.size() > 0)
	        	locations.trimToSize();
	        
	        return locations.isEmpty() ? null : locations.toArray(new Location[locations.size()]);
	    }
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(selectSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static Location findById(Connection conn, int id) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement selectSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = null;
				
			query = "SELECT "
					+ "		l.id, l.name, l.description, l.representing_colour, l.type, l.creation_time, l.last_modified_time, "
					+ "		a.id AS adr_id, a.address_string AS adr_str, a.post_code AS adr_p_code, a.latitude AS adr_lat, a.longitude AS adr_long, "
					+ "		ds.id AS ds_id, ds.name AS ds_name, ct.id AS ct_id, ct.name AS ct_name, ct.code AS ct_code "
					+ "FROM "
					+ "		location l, "
					+ "		address a "
					+ "			LEFT OUTER JOIN "
					+ "				district ds ON (ds.id = a.district), "
					+ "		city ct "
					+ "WHERE "
					+ "		a.location = l.id "
					+ "		AND ct.id = a.city "
					+ "		AND l.id = ?"
					+ "ORDER BY "
					+ "		ct.name ASC, l.name ASC";
						
			selectSt = newConn.prepareStatement(query);
			selectSt.setInt(1, id);
		    rs = selectSt.executeQuery();
	        
			Location location = null;
	        String[] addressCols = null;
			
	        if(rs.next())
	        {
	        	location = Location.parseFromRecord(rs);
	        	
	        	if(addressCols == null)
	        		addressCols = new String[] { "adr_id", "adr_str", "adr_p_code", "adr_lat", "adr_long", "ds_id", "ds_name", "ct_id", "ct_name", "ct_code"};
	        	
	        	location.setAddress(Address.parseFromRecord(rs, addressCols));
	        }
	        
	        return location;
	    }
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(selectSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	//Statik metodlar
	public static Location parseFromRecord(ResultSet rs)
	{
		try
		{
			Location parsedLoc = null;
			int id = rs.getInt("id"); 
			
			if(!rs.wasNull())
			{
				parsedLoc = new Location(id);
				parsedLoc.setName(rs.getString("name"));
				parsedLoc.setDescription(rs.getString("description"));
				parsedLoc.setRepresentingColour(rs.getString("representing_colour"));
				parsedLoc.setType(LocationType.valueOf(rs.getString("type")));
				parsedLoc.parseTTAttributes(rs);
			}
				
			return parsedLoc;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static Location parseFromRecord(ResultSet rs, String[] columns)
	{
		try
		{
			Location parsedLoc = null;
			int locId = rs.getInt(columns[0]); 
			
			if(!rs.wasNull())
			{
				parsedLoc = new Location(locId);
				parsedLoc.setName(rs.getString(columns[1]));
				parsedLoc.setDescription(rs.getString(columns[2]));
				parsedLoc.setRepresentingColour(rs.getString(columns[3]));
				parsedLoc.setType(LocationType.valueOf(rs.getString(columns[4])));
			}
				
			return parsedLoc;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
}