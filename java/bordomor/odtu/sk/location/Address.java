package bordomor.odtu.sk.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.IXmlObject;
import bordomor.util.DBUtils;

public class Address implements IXmlObject, IDBObject
{
	private int id = -1;
	private String addressString = null;
	private District district = null;
	private City city = null;
	private String postCode = null;
	private float latitude = -1f;
	private float longitude = -1f;
	private Location location = null;
	
	public Address(){}
	
	public Address(int id)
	{
		this.id = id;
	}
	
	public Address(int id, Location location, String addressString, City city, String postCode) 
	{
		this.id = id;
		this.location = location;
		this.addressString = addressString;
		this.city = city;
		this.postCode = postCode;
	}
	
	public Address(int id, Location location, String addressString, District district, City city, String postCode, float latitude, float longitude) 
	{
		this.id = id;
		this.location = location;
		this.addressString = addressString;
		this.district = district;
		this.city = city;
		this.postCode = postCode;
		this.latitude = latitude;
		this.longitude  = longitude;
	}
	
	@Override
	public String toString()
	{
		String address = this.getDistrictAndCity();
		address = this.postCode != null ? this.postCode + " " + address : address;
		address = this.addressString != null ? this.addressString + " " + address : address;
		
		return address;
	}
	
	//Xml Bölümü
	@Override
	public void parseFromXMLElement(Element addressEl) throws IllegalArgumentException
	{
		try
		{
			this.id = Integer.parseInt(addressEl.getAttribute("id"));
			
			if(addressEl.hasAttribute("address_string"))
				this.addressString = addressEl.getAttribute("address_string");
			
			if(addressEl.hasAttribute("post_code"))
				this.postCode = addressEl.getAttribute("post_code"); 
			
			if(addressEl.hasAttribute("latitude"))
				this.latitude = Float.parseFloat(addressEl.getAttribute("latitude"));
			
			if(addressEl.hasAttribute("longitude"))
				this.longitude = Float.parseFloat(addressEl.getAttribute("longitude"));
			
			NodeList districtNodes = addressEl.getElementsByTagName("district");
			District district = null;
			
			if(districtNodes.getLength() > 0)
			{
				district = new District();
				district.parseFromXMLElement((Element) districtNodes.item(0));
			}
			
			NodeList cityNodes = addressEl.getElementsByTagName("city");
			City city = null;
			
			if(cityNodes.getLength() > 0)
			{
				city = new City();
				city.parseFromXMLElement((Element) cityNodes.item(0));
			}
			
			if(this.district != null)
				this.district.setCity(city);
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml element.", ex);
		}
	}
	
	@Override
	public Element makeXMLElement(Document xml, boolean cascadeRelations) throws IllegalArgumentException
	{
		try
		{
			Element addressEl = xml.createElement("address");
			addressEl.setAttribute("id", this.id + "");
			
			if(this.addressString != null)
				addressEl.setAttribute("address_string", this.addressString);
			
			if(this.postCode != null)
				addressEl.setAttribute("post_code", this.postCode);
			
			addressEl.setAttribute("latitude", this.latitude + "");
			addressEl.setAttribute("longitude", this.longitude + "");
			
			if(cascadeRelations)
			{
				if(this.district != null)
					addressEl.appendChild(this.district.makeXMLElement(xml, cascadeRelations));
				
				addressEl.appendChild(this.city.makeXMLElement(xml, cascadeRelations));
			}
			
			return addressEl;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml document", ex);
		}
	}
	
	//VT Bölümü
	@Override
	public void createInDB(Connection conn) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet keySet = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
					
			String addressQuery = ""
					+ "INSERT INTO address "
					+ "		(location, address_string, district, city, post_code, latitude, longitude) " +
					"VALUES "
					+ "		(?, ?, ?, ?, ?, ?, ?)";
			
			sqlSt = newConn.prepareStatement(addressQuery, Statement.RETURN_GENERATED_KEYS);
			sqlSt.setInt(1, this.location.getId());
			sqlSt.setObject(2, this.addressString == null ? null : this.addressString.trim(), Types.VARCHAR);
			sqlSt.setObject(3, this.district == null ? null : this.district.getId(), Types.INTEGER);
			sqlSt.setInt(4, this.city.getId());
	        sqlSt.setObject(5, this.postCode == null ? null : this.postCode.trim(), Types.INTEGER);
	        sqlSt.setObject(6, this.latitude < 0 ? null : this.latitude, Types.FLOAT);
	        sqlSt.setObject(7, this.longitude < 0 ? null : this.longitude, Types.FLOAT);
	        
	        sqlSt.executeUpdate();
	        keySet = sqlSt.getGeneratedKeys();
	        keySet.next();
	        
        	int addressId = keySet.getInt("id");
        	this.id = addressId;
	    }
		catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			DBUtils.close(keySet);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}

	@Override
	public void updateInDB(Connection conn, IDBObject newAddress) throws ClassNotFoundException, SQLException, IllegalArgumentException 
	{
		Connection newConn = conn;
		PreparedStatement updateSt = null;
		
		try
		{
			Address updateAddress = (Address) newAddress;
			
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
						
			String updateQuery = ""
					+ "UPDATE "
					+ "		address "
					+ "SET "
					+ "		address_string = ?, district = ?, city = ?, post_code = ?, "
					+ "		latitude = ?, longitude = ? "
					+ "WHERE "
					+ "		id = ?";
			
			updateSt = newConn.prepareStatement(updateQuery);
	        
			updateSt.setObject(1, updateAddress.addressString == null ? null : updateAddress.addressString.trim(), Types.VARCHAR);
			updateSt.setObject(2, updateAddress.district == null ? null : updateAddress.district.getId(), Types.INTEGER);
			updateSt.setInt(3, updateAddress.city.getId());
			updateSt.setObject(4, updateAddress.postCode == null ? null : updateAddress.postCode.trim(), Types.VARCHAR);
			updateSt.setObject(5, updateAddress.latitude < 0 ? null : updateAddress.latitude, Types.FLOAT);
			updateSt.setObject(6, updateAddress.longitude < 0 ? null : updateAddress.longitude, Types.FLOAT);
			updateSt.setInt(7, this.id);
	        
	        if(updateSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        this.addressString = updateAddress.addressString;
	        this.postCode = updateAddress.postCode;
	        this.district = updateAddress.district;
	        this.city = updateAddress.city;
	        this.latitude = updateAddress.latitude;
	        this.longitude = updateAddress.longitude;
        }
		catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			DBUtils.close(updateSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}

	@Override
	public void deleteFromDB(Connection conn) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
		PreparedStatement sqlStatement = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String deleteQuery = "delete from address where id = ?";
			sqlStatement = newConn.prepareStatement(deleteQuery);
	        sqlStatement.setInt(1, this.id);
	        sqlStatement.executeUpdate();
		}
		catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			DBUtils.close(sqlStatement);
			
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
	
	public String getAddressString() 
	{
		return addressString;
	}

	public void setAddressString(String addressString) 
	{
		this.addressString = addressString;
	}

	public District getDistrict() 
	{
		return district;
	}

	public void setDistrict(District district) 
	{
		this.district = district;
	}

	public String getPostCode() 
	{
		return postCode;
	}

	public void setPostCode(String postCode) 
	{
		this.postCode = postCode;
	}

	public City getCity() 
	{
		return city;
	}
	
	public void setCity(City city)
	{
		this.city = city;
	}
	
	public String getDistrictAndCity()
	{
		if(this.district != null && this.district.getName().length() > 0)
			return this.district.getName() + "/" + this.city.getName();
		else
			return this.city.getName();
	}
	
	public float getLatitude() 
	{
		return latitude;
	}

	public void setLatitude(float latitude) 
	{
		this.latitude = latitude;
	}

	public float getLongitude() 
	{
		return longitude;
	}

	public void setLongitude(float longitude) 
	{
		this.longitude = longitude;
	}

	public Location getLocation() 
	{
		return location;
	}

	public void setLocation(Location location) 
	{
		this.location = location;
	}

	//Statik Metodlar
	public static Address parseFirstInstance(Element parentEl) throws IllegalArgumentException
	{
		try
		{
			NodeList addressNodes = parentEl.getElementsByTagName("address");
			Address address = new Address();
			
			if(addressNodes.getLength() > 0)
			{
				address = new Address();
				address.parseFromXMLElement((Element)addressNodes.item(0)); 
			}
			
			return address;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml document.", ex);
		}
	}
	
	public static Vector<Address> parseAll(Element parentEl) throws IllegalArgumentException
	{
		try
		{
			NodeList addressNodes = parentEl.getElementsByTagName("address");
			Vector<Address> addresses = new Vector<Address>();
			
			for(int i = 0; i < addressNodes.getLength(); i++)
			{
				Address nextAddress = new Address();
				nextAddress.parseFromXMLElement((Element) addressNodes.item(i)); 
				addresses.add(nextAddress);
			}
			
			if(addresses.isEmpty())
	        	return null;
	        
			addresses.trimToSize();
	        return addresses;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml document.", ex);
		}
	}
	
	public static Address parseFromRecord(ResultSet rs, String[] columns)
	{
		try
		{
			Address parsedAdr = null;
			int adrId = rs.getInt(columns[0]); 
			
			if(!rs.wasNull())
			{
				parsedAdr = new Address(adrId);
				parsedAdr.setAddressString(rs.getString(columns[1]));
				parsedAdr.setPostCode(rs.getString(columns[2]));
				parsedAdr.setLatitude(rs.getFloat(columns[3]) > 0 ? rs.getFloat(columns[3]) : -1f);
				parsedAdr.setLongitude(rs.getFloat(columns[4]) > 0 ? rs.getFloat(columns[4]) : -1f);
				
				parsedAdr.setDistrict(District.parseFromRecord(rs, new String[] { columns[5], columns[6] }));
				parsedAdr.setCity(City.parseFromRecord(rs, new String[] { columns[7], columns[8], columns[9] }));
			}
				
			return parsedAdr;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	//Sorgular
	public static int findAddressIdByCompanyId(Connection conn, int compId) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlStatement = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = "Select l.address From location l, company c " +
					"Where c.location = l.id and l.address = ?";
			
			sqlStatement = newConn.prepareStatement(query);
			sqlStatement.setInt(1, compId);
			
	        rs = sqlStatement.executeQuery();
	        
	        if(rs.next())
	        	return rs.getInt(1);
	        else 
	        	return -1;
		}
		finally
		{
			DBUtils.close(rs);
	        DBUtils.close(sqlStatement);
			
	        if(conn == null)
	        	DBUtils.close(newConn);
	    }
	}
}