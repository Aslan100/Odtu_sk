package bordomor.odtu.sk.licence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.w3c.dom.Element;

import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.template.Document;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.util.DBUtils;

public class Licence extends Document 
{
	private int id = -1;
	private Branch licenceBranch = null;
	
	public Licence() 
	{
		super();
	}
	
	public Licence(int id)
	{
		super();
		this.id = id;
	}
	
	public Licence(int id, Branch licenceBranch)
	{
		super();
		this.id = id;
		this.licenceBranch = licenceBranch;
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
			Element docEl = xml.createElement("licence");
			docEl.setAttribute("id", this.id + "");
			
			if(cascadeRelations && this.licenceBranch != null)
				docEl.appendChild(this.licenceBranch.makeXMLElement(xml, false));
			
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
    				+ "INSERT INTO "
    				+ "		licence "
    				+ "			(licence_branch, document_parent) "
    				+ "VALUES "
    				+ "		(?::branch, ?)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		insertSt.setString(1, this.licenceBranch.toString());
    		insertSt.setInt(2, this.documentId);
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
	public void updateInDB(Connection conn, IDBObject newDoc) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement updateSt = null;
        
        try
        {
	        Licence updatingLicence = (Licence) newDoc;
	        
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        if(!super.equals(updatingLicence))
	        	super.updateInDB(newConn, updatingLicence);
	        
	        String updateQuery = "UPDATE licence SET licence_branch = ?::branch WHERE id = ?";
	        
	        updateSt = newConn.prepareStatement(updateQuery);
	        updateSt.setString(1, updatingLicence.getLicenceBranch().toString());
	        updateSt.setInt(2, this.id);
	        
	        if(updateSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        this.setLicenceBranch(updatingLicence.getLicenceBranch());
	    }
        catch(ClassCastException cex)
        {
        	throw new IllegalArgumentException();
        }
        finally
        {
        	DBUtils.close(updateSt);
        	
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
	
	public Branch getLicenceBranch() 
	{
		return this.licenceBranch;
	}
	
	public void setLicenceBranch(Branch licenceBranch)
	{
		this.licenceBranch = licenceBranch;
	}
	
	//Statik Metodlar
	public static Licence parseFromRecord(ResultSet rs)
	{
		try
		{
			Licence parsedLicence = null;
			int licenceId = rs.getInt("id");  
			
			if(!rs.wasNull())
			{
				parsedLicence = new Licence(licenceId);
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
					else if(nextCol.equals("licence_branch"))
						parsedLicence.setLicenceBranch(Branch.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
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
	
	public static Licence parseFromRecord(ResultSet rs, TypeDefinition typeDefinition, String docTableVar)
	{
		try
		{
			Class<? extends Document> type = typeDefinition.getType();
			String tableVar = typeDefinition.getTableVariable();
			
			if(type != Licence.class || tableVar == null)
				throw new Exception("Bad type definition.");
			
			Licence parsedLicence = null;
			int licenceId = rs.getInt(tableVar + "_id");  
			
			if(!rs.wasNull())
			{
				parsedLicence = new Licence(licenceId);
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
					else if(nextCol.equals(tableVar + "_licence_branch"))
						parsedLicence.setLicenceBranch(Branch.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
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
		
		String[] colNames = new String[2];
		
		int columnIndex = 0;
		colNames[columnIndex] = tableVar + "_id"; columnIndex++;
		colNames[columnIndex] = tableVar + "_licence_branch"; columnIndex++;
		
		return colNames;
	}
}
