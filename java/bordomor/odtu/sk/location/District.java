package bordomor.odtu.sk.location;

import java.sql.ResultSet;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import bordomor.odtu.sk.template.IXmlObject;

import org.w3c.dom.Node;

public class District implements IXmlObject
{
	private int id = -1;
	private String name = null;
	private City city = null;
	
	public District() {}
	
	public District(int id)
	{
		this.id = id;
	}
	
	public District(int id, String name, City city) 
	{
		this.id = id;
		this.name = name;
		this.city = city;
	}
	
	//Xml Bölümü
	@Override
	public void parseFromXMLElement(Element districtEl) throws IllegalArgumentException 
	{
		try
		{
			int id = Integer.parseInt(districtEl.getAttribute("id"));
			String name = null;
			
			if(districtEl.hasAttribute("name"))
				name = districtEl.getAttribute("name");
			
			this.id = id;
			this.name = name;
			this.city = null;
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
			Element districtEl = xml.createElement("district");
			districtEl.setAttribute("id", this.id + "");
			
			if(this.name != null)
				districtEl.setAttribute("name", this.name);
			
			if(cascadeRelations && this.city != null)
				districtEl.appendChild(this.city.makeXMLElement(xml, cascadeRelations));
			
			return districtEl;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad Xml document.", ex);
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

	public City getCity() 
	{
		return city;
	}

	public void setCity(City city) 
	{
		this.city = city;
	}
	
	//Statik metodlar
	public static Vector<District> parseAll(Element parentEl) throws IllegalArgumentException
	{
		try
		{
			NodeList cityList = parentEl.getElementsByTagName("district");
			Vector<District> districts = new Vector<District>();
			
			for(int i = 0; i < cityList.getLength(); i++)
			{
				Node nextNode = (Node) cityList.item(i);
				
				if(nextNode.getNodeType() == Node.ELEMENT_NODE)
				{
					District nextDistrict = new District();
					nextDistrict.parseFromXMLElement((Element) nextNode);
					districts.add(nextDistrict);
				}
			}
			
			districts.trimToSize();
			
			if(districts.size() > 0)
				return districts;
			else
				return null;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml element.", ex);
		}
	}
	
	public static District parseFromRecord(ResultSet rs, String[] columns)
	{
		try
		{
			District parsedDistrict = null;
			int districtId = rs.getInt(columns[0]); 
			
			if(!rs.wasNull())
			{
				parsedDistrict = new District(districtId);
				parsedDistrict.setName(rs.getString(columns[1]));
			}
				
			return parsedDistrict;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
}