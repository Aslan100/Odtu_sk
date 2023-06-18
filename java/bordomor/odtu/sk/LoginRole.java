package bordomor.odtu.sk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import bordomor.odtu.sk.Params.ManipulationMode;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.odtu.sk.template.DBTimeTrackable;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.IXmlObject;
import bordomor.odtu.sk.template.Loginable;
import bordomor.util.DBUtils;

@XMLAndDatabaseValues(tableName = "login_role", tagName = "login_role", defaultVariable = "lr")
public class LoginRole extends DBTimeTrackable implements IXmlObject, IDBObject
{
	private int id = -1;
	private String title = null;
	private boolean isEmbedded = false;
	private Class<? extends Loginable> targetedType = null;
	private boolean isDefinitive = false;
	
	private ManipulationMode userMod = null;
	private ManipulationMode roleMod = null;
	private ManipulationMode branchMod = null;
	private ManipulationMode teamMod = null;
	private ManipulationMode athleteMod = null;
	private ManipulationMode facilityMod = null;
	private ManipulationMode eventMod = null;
	private ManipulationMode paymentMod = null;
	
	public LoginRole() 
	{
		super();
	}
	
	public LoginRole(int id)
	{
		super();
		this.id = id;
	}
	
	public LoginRole(int id, String title, boolean isEmbeddedRole, Class<Loginable> targetedType, boolean isDefinitive)
	{
		super();
		this.id = id;
		this.title = title;
		this.isEmbedded = isEmbeddedRole;
		this.targetedType = targetedType;
		this.isDefinitive = isDefinitive;
	}
	
	//Yardımcı Metodlar
	@Override
	public String toString()
	{
		try
		{
			String summary = "";
			
			summary += this.title + "\n";
			summary += "ID: " + this.id + "\n";
			summary += "GÖMÜLÜ: " + this.isEmbedded + "\n";
			summary += "HEDEF: " + this.getTargetedType().getSimpleName() + "\n";
			summary += "BELİRLEYİCİLİK: " + this.isDefinitive + "\n";
			
			return summary;
		}
		catch(Exception ex)
		{
			return super.toString();
		}
	}
	
	//Xml Bölümü
	@Override
	public void parseFromXMLElement(Element roleEl)
	{
		try
		{
			this.id = Integer.parseInt(roleEl.getAttribute("id"));
			this.title = roleEl.hasAttribute("title") ? roleEl.getAttribute("title") : null;
			this.isEmbedded = Boolean.parseBoolean(roleEl.getAttribute("is_embedded"));
			this.targetedType = Loginable.getClassFromAnnotatedString(roleEl.getAttribute("targeted_type"));
			this.isDefinitive = Boolean.parseBoolean(roleEl.getAttribute("is_definitive"));
			
			this.userMod = ManipulationMode.valueOf(roleEl.getAttribute("user_mod"));
			this.roleMod = ManipulationMode.valueOf(roleEl.getAttribute("role_mod"));
			this.branchMod = ManipulationMode.valueOf(roleEl.getAttribute("branch_mod"));
			this.teamMod = ManipulationMode.valueOf(roleEl.getAttribute("team_mod"));
			this.athleteMod = ManipulationMode.valueOf(roleEl.getAttribute("athlete_mod"));
			this.facilityMod = ManipulationMode.valueOf(roleEl.getAttribute("facility_mod"));
			this.eventMod = ManipulationMode.valueOf(roleEl.getAttribute("event_mod"));
			this.paymentMod = ManipulationMode.valueOf(roleEl.getAttribute("payment_mod"));
			
			this.parseTTAttributes(roleEl);
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml element.", ex);
		}
	}
	
	@Override
	public Element makeXMLElement(Document doc, boolean cascadeRelations)
	{
		try
		{
			Element roleEl = doc.createElement(LoginRole.class.getAnnotation(XMLAndDatabaseValues.class).tagName());
			roleEl.setAttribute("id", this.id + "");
			
			if(this.title != null)
				roleEl.setAttribute("title", this.title);
			
			roleEl.setAttribute("is_embedded", this.isEmbedded + "");
			roleEl.setAttribute("targeted_type", this.targetedType.getSimpleName());
			roleEl.setAttribute("is_definitive", this.isDefinitive + "");
			
			roleEl.setAttribute("user_mod", this.userMod.toString());
			roleEl.setAttribute("role_mod", this.roleMod.toString());
			roleEl.setAttribute("branch_mod", this.branchMod.toString());
			roleEl.setAttribute("team_mod", this.teamMod.toString());
			roleEl.setAttribute("athlete_mod", this.athleteMod.toString());
			roleEl.setAttribute("facility_mod", this.facilityMod.toString());
			roleEl.setAttribute("event_mod", this.eventMod.toString());
			roleEl.setAttribute("payment_mod", this.paymentMod.toString());
			
			super.appendTTAttributes(roleEl);
			
			return roleEl;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml document or object properties.", ex);
		}
	}
	
	//Veritabanı Bölümü
	@Override
	public void createInDB(Connection conn) throws ClassNotFoundException, SQLException {}

	@Override
	public void updateInDB(Connection conn, IDBObject newLoginable)	throws ClassNotFoundException, SQLException {}
	
	@Override
	public void deleteFromDB(Connection conn) throws ClassNotFoundException, SQLException {}
	
	//Get-Set
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public boolean isEmbedded()
	{
		return isEmbedded;
	}

	public void setEmbedded(boolean isEmbedded)
	{
		this.isEmbedded = isEmbedded;
	}
	
	public Class<? extends Loginable> getTargetedType()
	{
		return targetedType;
	}
	
	public void setTargetedType(Class<? extends Loginable> targetedType)
	{
		this.targetedType = targetedType;
	}
	
	public boolean isDefinitive()
	{
		return isDefinitive;
	}

	public void setDefinitive(boolean isDefinitive)
	{
		this.isDefinitive = isDefinitive;
	}
	
	public ManipulationMode getUserMod() 
	{
		return userMod;
	}

	public void setUserMod(ManipulationMode userMod) 
	{
		this.userMod = userMod;
	}

	public ManipulationMode getRoleMod() 
	{
		return roleMod;
	}

	public void setRoleMod(ManipulationMode roleMod) 
	{
		this.roleMod = roleMod;
	}

	public ManipulationMode getBranchMod() 
	{
		return branchMod;
	}

	public void setBranchMod(ManipulationMode branchMod) 
	{
		this.branchMod = branchMod;
	}

	public ManipulationMode getTeamMod() 
	{
		return teamMod;
	}

	public void setTeamMod(ManipulationMode teamMod) 
	{
		this.teamMod = teamMod;
	}

	public ManipulationMode getAthleteMod() 
	{
		return athleteMod;
	}

	public void setAthleteMod(ManipulationMode athleteMod) 
	{
		this.athleteMod = athleteMod;
	}
	
	public ManipulationMode getFacilityMod() 
	{
		return facilityMod;
	}

	public void setFacilityMod(ManipulationMode facilityMod) 
	{
		this.facilityMod = facilityMod;
	}

	public ManipulationMode getEventMod() 
	{
		return eventMod;
	}

	public void setEventMod(ManipulationMode eventMod) 
	{
		this.eventMod = eventMod;
	}

	public ManipulationMode getPaymentMod() 
	{
		return paymentMod;
	}

	public void setPaymentMod(ManipulationMode paymentMod) 
	{
		this.paymentMod = paymentMod;
	}

	public boolean canDoSomethingOnWebPortal()
	{
		try
		{
			String totalStr = this.userMod.toString() + this.roleMod.toString() + this.branchMod.toString() + this.teamMod.toString() + this.facilityMod.toString() + this.eventMod.toString() + this.paymentMod.toString();
			return totalStr != null && totalStr.contains("T");
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	
	//Statik Sorgular
	public static LoginRole[] findAll(Connection conn) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlStatement = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String tableName = LoginRole.class.getAnnotation(XMLAndDatabaseValues.class).tableName();
			String query = "SELECT * FROM " + tableName + " ORDER BY is_embedded desc, title asc";
			sqlStatement = newConn.prepareStatement(query);
	        rs = sqlStatement.executeQuery();
	        
	        Vector<LoginRole> roles = new Vector<LoginRole>(0);
	        
	        while(rs.next())
	        {
	        	LoginRole nextRole = LoginRole.parseFromRecord(rs);
	        	nextRole.parseTTAttributes(rs);
	        	roles.add(nextRole);
	        }
	        
	        if(roles.size() > 0)
	        	roles.trimToSize();
	        
	        return roles.isEmpty() ? null : roles.toArray(new LoginRole[roles.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlStatement);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static LoginRole findById(Connection conn, int id) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlStatement = null;
		ResultSet rs = null;
		LoginRole role = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String tableName = LoginRole.class.getAnnotation(XMLAndDatabaseValues.class).tableName();
			String query = "SELECT * FROM " + tableName + " WHERE id = ?";
			sqlStatement = newConn.prepareStatement(query);
			sqlStatement.setInt(1, id);
	        rs = sqlStatement.executeQuery();
	        
	        if(rs.next())
	        {
	        	role = LoginRole.parseFromRecord(rs);
	        	role.parseTTAttributes(rs);
	        }
	        
	        return role;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlStatement);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static LoginRole parseFromRecord(ResultSet rs)
	{
		try
		{
			LoginRole parsedRole = null;
			int roleId = rs.getInt("id");
			
			if(!rs.wasNull())
			{
				parsedRole = new LoginRole();
				parsedRole.setId(roleId);
		    	parsedRole.setTitle(rs.getString("title"));
		    	parsedRole.setEmbedded(rs.getBoolean("is_embedded"));
		    	parsedRole.setTargetedType(Loginable.getClassFromAnnotatedString(rs.getString("targeted_type")));
		    	parsedRole.setDefinitive(rs.getBoolean("is_definitive"));
		    	
		    	parsedRole.setUserMod(ManipulationMode.valueOf(rs.getString("user_mod")));
		    	parsedRole.setRoleMod(ManipulationMode.valueOf(rs.getString("role_mod")));
		    	parsedRole.setBranchMod(ManipulationMode.valueOf(rs.getString("branch_mod")));
		    	parsedRole.setTeamMod(ManipulationMode.valueOf(rs.getString("team_mod")));
		    	parsedRole.setAthleteMod(ManipulationMode.valueOf(rs.getString("athlete_mod")));
		    	parsedRole.setFacilityMod(ManipulationMode.valueOf(rs.getString("facility_mod")));
		    	parsedRole.setEventMod(ManipulationMode.valueOf(rs.getString("event_mod")));
		    	parsedRole.setPaymentMod(ManipulationMode.valueOf(rs.getString("payment_mod")));
		    	parsedRole.parseTTAttributes(rs);
		    }
			
	    	return parsedRole;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
    }
	
	public static LoginRole parseFromRecord(ResultSet rs, String[] columns)
	{
		try
		{
			LoginRole parsedRole = null;
			int roleId = rs.getInt(columns[0]);
			
			if(!rs.wasNull())
			{
				parsedRole = new LoginRole();
				parsedRole.setId(roleId);
		    	parsedRole.setTitle(rs.getString(columns[1]));
		    	parsedRole.setEmbedded(rs.getBoolean(columns[2]));
		    	parsedRole.setTargetedType(Loginable.getClassFromAnnotatedString(rs.getString(columns[3])));
		    	parsedRole.setDefinitive(rs.getBoolean(columns[4]));
		    	
		    	parsedRole.setUserMod(ManipulationMode.valueOf(rs.getString(columns[5])));
		    	parsedRole.setRoleMod(ManipulationMode.valueOf(rs.getString(columns[6])));
		    	parsedRole.setBranchMod(ManipulationMode.valueOf(rs.getString(columns[7])));
		    	parsedRole.setTeamMod(ManipulationMode.valueOf(rs.getString(columns[8])));
		    	parsedRole.setAthleteMod(ManipulationMode.valueOf(rs.getString(columns[9])));
		    	parsedRole.setFacilityMod(ManipulationMode.valueOf(rs.getString(columns[10])));
		    	parsedRole.setEventMod(ManipulationMode.valueOf(rs.getString(columns[11])));
		    	parsedRole.setPaymentMod(ManipulationMode.valueOf(rs.getString(columns[12])));
		    	parsedRole.parseTTAttributes(rs, columns[13], columns[14]);
		    }
			
	    	return parsedRole;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
    }
	
	//Statik Metodlar
	public static LoginRole parseFirstInstance(Element parentEl)
	{
		NodeList roleNodes = parentEl.getElementsByTagName(LoginRole.class.getAnnotation(XMLAndDatabaseValues.class).tagName());
		LoginRole role = null;
		
		if(roleNodes.getLength() > 0)
		{
			role = new LoginRole();
			role.parseFromXMLElement((Element)roleNodes.item(0));
		}
		
		return role;
	}
	
	public static LoginRole[] parseAll(Element parentEl)
	{
		NodeList roleNodes = parentEl.getElementsByTagName(LoginRole.class.getAnnotation(XMLAndDatabaseValues.class).tagName());
		Vector<LoginRole> roles = new Vector<LoginRole>(0);
		
		for(int i = 0; i < roleNodes.getLength(); i++)
		{
			LoginRole nextRole = new LoginRole();
			nextRole.parseFromXMLElement((Element) roleNodes.item(i));
			roles.add(nextRole);
		}
		
		if(roles.size() > 0)
			roles.trimToSize();
		
		return roles.isEmpty() ? null : roles.toArray(new LoginRole[roles.size()]);
	}
}