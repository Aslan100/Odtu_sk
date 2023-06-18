package bordomor.odtu.sk.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.template.IXmlObject;
import bordomor.util.DBUtils;

public class City implements IXmlObject
{
	private int id = -1;
	private String name = null;
	private String code = null;
	private Vector<District> districts = null;
	
	public City() {}
	
	public City(int id)
	{
		this.id = id;
	}
	
	public City(int id, String name, String code, Vector<District> districts) 
	{
		super();
		this.id = id;
		this.name = name;
		this.code = code;
		this.districts = districts;
	}
	
	//Xml Bölümü
	@Override
	public void parseFromXMLElement(Element cityEl) throws IllegalArgumentException 
	{
		try
		{
			int id = Integer.parseInt(cityEl.getAttribute("id"));
			
			String name = null;
			
			if(cityEl.hasAttribute("name"))
				name = cityEl.getAttribute("name");
			
			String code = null;
			
			if(cityEl.hasAttribute("code"))
				code = cityEl.getAttribute("code");
			
			Vector<District> districts = District.parseAll(cityEl);
			
			if(districts != null)
			{
				for(District nextDistrict : districts)
					nextDistrict.setCity(this);
			}
			
			this.id = id;
			this.name = name;
			this.code = code;
			this.districts = districts;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml element.", ex);
		}
	}
	
	public Element makeXMLElement(Document xml, boolean cascadeRelations) throws IllegalArgumentException
	{
		try
		{
			Element cityEl = xml.createElement("city");
			cityEl.setAttribute("id", this.id + "");
			
			if(this.name != null)
				cityEl.setAttribute("name", this.name);
			
			if(this.code != null)
				cityEl.setAttribute("code", this.code);
			
			if(cascadeRelations && this.districts != null)
			{
				for(District nextDistrict : this.districts)
					cityEl.appendChild(nextDistrict.makeXMLElement(xml, false));
			}
			
			return cityEl;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml document.", ex);
		}
	}
	
	//Diğer metodlar
	public synchronized void addDistrict(District district)
	{
		this.districts = this.districts == null ? new Vector<District>(0) : this.districts;
		this.districts.add(district);
		district.setCity(this);
		this.districts.trimToSize();
	}
	
	public synchronized void removeDistrict(District district)
	{
		if(this.districts != null)
		{
			if(this.districts.remove(district))
			{
				district.setCity(null);
				this.districts.trimToSize();
				this.districts = this.districts.isEmpty() ? null : this.districts;
			}
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

	public String getCode() 
	{
		return code;
	}

	public void setCode(String code) 
	{
		this.code = code;
	}

	public Vector<District> getDistricts() 
	{
		return districts;
	}

	public void setDistricts(Vector<District> districts) 
	{
		this.districts = districts;
	}
	
	//Statik metodlar
	public static Vector<City> parseAll(Element parentEl) throws IllegalArgumentException
	{
		try
		{
			NodeList cityList = parentEl.getElementsByTagName("city");
			Vector<City> cities = new Vector<City>();
			
			for(int i = 0; i < cityList.getLength(); i++)
			{
				Node nextNode = (Node) cityList.item(i);
				
				if(nextNode.getNodeType() == Node.ELEMENT_NODE)
				{
					City nextCity = new City();
					nextCity.parseFromXMLElement((Element) nextNode);
					cities.add(nextCity);
				}
			}
			
			cities.trimToSize();
			
			if(cities.size() > 0)
				return cities;
			else
				return null;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml element.", ex);
		}
	}

	public static City[] findAll(Connection conn, boolean includeDistricts) 
			throws ClassNotFoundException, SQLException
	{
 		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = "";
			
			if(includeDistricts)
				query += "SELECT "
						+ "		c.*, d.id AS ds_id, d.name AS ds_name "
						+ "FROM "
						+ "		city c LEFT OUTER JOIN district d "
						+ "			ON (d.city = c.id) "
						+ "WHERE "
						+ "		c.country = 1 " 
						+ "ORDER BY "
						+ "		c.code, d.name";
			else
				query += "SELECT c.* FROM city c WHERE c.country = 1 ORDER BY c.code";
			
			sqlSt = newConn.prepareStatement(query);
	        rs = sqlSt.executeQuery();
	        
	        City lastCity = null;
	        Vector<City> cities = new Vector<City>(0);
	        
	        while(rs.next())
	        {
	        	if(lastCity == null || rs.getInt("id") != lastCity.getId())
	        	{
	        		lastCity = City.parseFromRecord(rs);
		        	cities.add(lastCity);
		        }
	        	
	        	if(includeDistricts)
	        		lastCity.addDistrict(District.parseFromRecord(rs, new String[] {"ds_id", "ds_name"}));
	        }
	        
	        if(cities.size() > 0)
	        	cities.trimToSize();
	        
	        return cities.isEmpty() ? null : cities.toArray(new City[cities.size()]);
		}
		finally
		{
			DBUtils.close(rs);
	        DBUtils.close(sqlSt);
			
	        if(conn == null)
	        	DBUtils.close(newConn);
	    }
	}
	
	public static City findById(Connection conn, int id, boolean includeDistricts) 
			throws ClassNotFoundException, SQLException
	{
 		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = "";
			
			if(includeDistricts)
				query += "SELECT "
						+ "		c.*, d.id AS ds_id, d.name AS ds_name "
						+ "FROM "
						+ "		city c LEFT OUTER JOIN district d "
						+ "			ON (d.city = c.id) "
						+ "WHERE "
						+ "		c.country = 1 "
						+ "		AND c.id = ? " 
						+ "ORDER BY "
						+ "		c.code, d.name";
			else
				query += "SELECT c.* FROM city c WHERE c.country = 1 AND c.id = ? ORDER BY c.code";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, id);
			rs = sqlSt.executeQuery();
	        
	        City lastCity = null;
	        Vector<City> cities = new Vector<City>(0);
	        
	        while(rs.next())
	        {
	        	if(lastCity == null || rs.getInt("id") != lastCity.getId())
	        	{
	        		lastCity = City.parseFromRecord(rs);
		        	cities.add(lastCity);
		        }
	        	
	        	if(includeDistricts)
	        		lastCity.addDistrict(District.parseFromRecord(rs, new String[] {"ds_id", "ds_name"}));
	        }
	        
	        if(cities.size() > 0)
	        	cities.trimToSize();
	        
	        return lastCity;
		}
		finally
		{
			DBUtils.close(rs);
	        DBUtils.close(sqlSt);
			
	        if(conn == null)
	        	DBUtils.close(newConn);
	    }
	}
	
	public static City parseFromRecord(ResultSet rs)
	{
		try
		{
			City parsedCity = null;
			int cityId = rs.getInt("id");
			
			if(!rs.wasNull())
			{
				parsedCity = new City(cityId);
				parsedCity.setName(rs.getString("name"));
				parsedCity.setCode(rs.getString("code"));
			}
				
			return parsedCity;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static City parseFromRecord(ResultSet rs, String[] columns)
	{
		try
		{
			City parsedCity = null;
			int cityId = rs.getInt(columns[0]); 
			
			if(!rs.wasNull())
			{
				parsedCity = new City(cityId);
				parsedCity.setName(rs.getString(columns[1]));
				parsedCity.setCode(rs.getString(columns[2]));
			}
				
			return parsedCity;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
}