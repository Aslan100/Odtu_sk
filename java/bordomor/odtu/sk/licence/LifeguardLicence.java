package bordomor.odtu.sk.licence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.w3c.dom.Element;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.template.Document;
import bordomor.util.DBUtils;

public class LifeguardLicence extends Document 
{
	private int id = -1;
	
	public LifeguardLicence() 
	{
		super();
	}
	
	public LifeguardLicence(int id) 
	{
		super();
		this.id = id;
	}
	
	//Yardımcı Metodlar
	@Override
 	public boolean equals(Object comparedObject) 
	{
		try
		{
			if(comparedObject == this)
				return true;
			else if(comparedObject == null || !(comparedObject instanceof Licence))
				return false;
			else
				return super.equals(comparedObject);
		}
		catch(Exception ex)
		{
			return this.hashCode() == comparedObject.hashCode();
		}
	}
	
	@Override
    public int hashCode() 
	{
        return super.hashCode();
    }
	
	//Xml Bölümü
	@Override
	public void parseFromXMLElement(Element docEl)
	{
		try
		{
			this.id = Integer.parseInt(docEl.getAttribute("id"));
			super.parseFromXMLElement(docEl);
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml element.", ex);
		}
	}
	
	@Override
	public Element makeXMLElement(org.w3c.dom.Document xml, boolean cascadeRelations)
	{
		try
		{
			Element docEl = xml.createElement("lifeguard_licence");
			docEl.setAttribute("id", this.id + "");
			
			super.appendDocumentAttributes(docEl, cascadeRelations);
			
			return docEl;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml document or object properties.", ex);
		}
	}
	
	//Veritabanı
	@Override
	public void createInDB(Connection conn) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement insertSt = null;
        ResultSet keySet = null;
        
        this.id = -1;
        this.documentId = -1;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			super.createInDB(newConn);
			
    		String insertQuery = ""
    				+ "INSERT INTO lifeguard_licence (document_parent) VALUES (?)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		insertSt.setInt(1, this.documentId);
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
	//Get-Set
	@Override
	public int getId() 
	{
		return this.id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	//Statik Metodlar
	public static LifeguardLicence parseFromRecord(ResultSet rs)
	{
		try
		{
			LifeguardLicence parsedLicence = null;
			int licenceId = rs.getInt("id");  
			
			if(!rs.wasNull())
			{
				parsedLicence = new LifeguardLicence(licenceId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals("document_parent"))
						parsedLicence.setDocumentId(rs.getInt(nextCol));
					else if(nextCol.equals("document_no"))
						parsedLicence.setDocumentNo(rs.getString(nextCol));
					else if(nextCol.equals("valid_from"))
						parsedLicence.setValidFrom(rs.getTimestamp(nextCol));
					else if(nextCol.equals("valid_until"))
						parsedLicence.setValidUntil(rs.getTimestamp(nextCol));
					else if(nextCol.equals("is_suspended"))
						parsedLicence.setSuspended(rs.getBoolean(nextCol));
				}
				
				parsedLicence.parseTTAttributes(rs);
			}
				
			return parsedLicence;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static LifeguardLicence parseFromRecord(ResultSet rs, TypeDefinition typeDefinition, String docTableVar)
	{
		try
		{
			Class<? extends Document> type = typeDefinition.getType();
			String tableVar = typeDefinition.getTableVariable();
			
			if(type != LifeguardLicence.class || tableVar == null)
				throw new Exception("Bad type definition.");
			
			LifeguardLicence parsedLicence = null;
			int licenceId = rs.getInt(tableVar + "_id");  
			
			if(!rs.wasNull())
			{
				parsedLicence = new LifeguardLicence(licenceId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals(docTableVar + "_id"))
						parsedLicence.setDocumentId(rs.getInt(nextCol));
					else if(nextCol.equals(docTableVar + "_document_no"))
						parsedLicence.setDocumentNo(rs.getString(nextCol));
					else if(nextCol.equals(docTableVar + "_valid_from"))
						parsedLicence.setValidFrom(rs.getTimestamp(nextCol));
					else if(nextCol.equals(docTableVar + "_valid_until"))
						parsedLicence.setValidUntil(rs.getTimestamp(nextCol));
					else if(nextCol.equals(docTableVar + "_is_suspended"))
						parsedLicence.setSuspended(rs.getBoolean(nextCol));
				}
				
				parsedLicence.parseTTAttributes(rs, docTableVar);
			}
				
			return parsedLicence;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static String[] generateColumnNames(String tableVar)
	{
		if(tableVar == null)
			throw new IllegalArgumentException("Bad table variable.");
		
		String[] colNames = new String[1];
		
		int columnIndex = 0;
		colNames[columnIndex] = tableVar + "_id"; columnIndex++;
		
		return colNames;
	}

}
