package bordomor.odtu.sk.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.licence.Licence;
import bordomor.odtu.sk.licence.LifeguardLicence;

import bordomor.util.DBUtils;

@XMLAndDatabaseValues(tableName = "document", tagName = "document", defaultVariable = "doc")
public abstract class Document extends DBTimeTrackable implements IXmlObject, IDBObject
{
	protected int ownerLoginableId = -1;
	protected int documentId = -1;
	protected String documentNo = null;
	protected Timestamp validFrom = null;
	protected Timestamp validUntil = null;
	protected boolean isSuspended = false;
	
	protected Document() {}
	
	protected Document(int id, String documentNo, Timestamp validFrom, Timestamp validUntil, boolean isSuspended)
	{
		super();
		this.documentId = id;
		this.documentNo = documentNo;
		this.validFrom = validFrom;
		this.validUntil = validUntil;
		this.isSuspended = isSuspended;
	}
	
	protected Document(int id, String documentNo, Timestamp validFrom, Timestamp validUntil, boolean isSuspended, Timestamp creationTime, Timestamp lastModifiedTime)
	{
		this(id, documentNo, validFrom, validUntil, isSuspended);
		this.creationTime = creationTime;
		this.lastModifiedTime = lastModifiedTime;
	}
	
	//Yardımcı Metodlar
	@Override
 	public boolean equals(Object comparedObject) 
	{
		try
		{
			if(comparedObject == this)
				return true;
			else if(comparedObject == null || !(comparedObject instanceof Document))
				return false;
			else
			{
				Document comparedDoc = (Document) comparedObject;
				
				return 
						this.documentId == comparedDoc.documentId
						&& this.documentNo.equals(comparedDoc.documentNo)
						&& this.validUntil.equals(comparedDoc.validUntil);
			}
		}
		catch(Exception ex)
		{
			return this.hashCode() == comparedObject.hashCode();
		}
	}
	
	@Override
    public int hashCode() 
	{
        final int prime = 7;
        
        int result = 1;
        result = prime*result + (int) (this.documentId ^ (this.documentId >>> 32));
        result = prime*result + (this.documentNo != null ? this.documentNo.hashCode() : 0);
        result = prime*result + (this.validFrom != null ? this.validFrom.hashCode() : 0);
        result = prime*result + (this.validUntil != null ? this.validUntil.hashCode() : 0);
        result = prime*result + (this.isSuspended ? 1 : 0);
        
        return result;
    }
	
	//Soyut Metodlar
	public abstract int getId();
	
	//XML Bölümü
	protected void appendDocumentAttributes(Element docEl, boolean cascadeRelations)
	{
		docEl.setAttribute("document_id", this.documentId + "");
		
		if(this.documentNo != null)
			docEl.setAttribute("document_no", this.documentNo);
		
		if(this.validFrom != null)
			docEl.setAttribute("valid_from", Params.DATE_FORMAT.format(this.validFrom) + "");
		
		if(this.validUntil != null)
			docEl.setAttribute("valid_until", Params.DATE_FORMAT.format(this.validUntil) + "");
		
		docEl.setAttribute("is_suspended", this.isSuspended + "");
		
		this.appendTTAttributes(docEl);
	}
	
	@Override
	public void parseFromXMLElement(Element docEl)
	{
		this.documentId = Integer.parseInt(docEl.getAttribute("document_id"));
		this.documentNo = docEl.hasAttribute("document_no") ? docEl.getAttribute("document_no") : null;
		this.validFrom = docEl.hasAttribute("valid_from") ? new Timestamp(Long.parseLong(docEl.getAttribute("valid_from"))) : null;
		this.validUntil = docEl.hasAttribute("valid_until") ? new Timestamp(Long.parseLong(docEl.getAttribute("valid_until"))) : null;
		this.isSuspended = Boolean.parseBoolean(docEl.getAttribute("is_suspended"));
		
		this.parseTTAttributes(docEl);
	}
		
	//VT Bölümü
	@Override
	public void createInDB(Connection conn) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement insertSt = null;
        ResultSet keySet = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        String insertQuery = ""
					+ "INSERT INTO "
					+ "		document (owner, document_no, valid_from, valid_until, is_suspended) "
					+ "VALUES "
					+ "		(?, ?, ?, ?, ?)";
	        
			insertSt = newConn.prepareStatement(insertQuery, new String[] {"id", "creation_time", "last_modified_time"});
			insertSt.setInt(1, this.ownerLoginableId);
			insertSt.setString(2, this.documentNo);
			insertSt.setObject(3, this.validFrom != null ? this.validFrom : null, Types.TIMESTAMP);
			insertSt.setTimestamp(4, this.validUntil);
			insertSt.setBoolean(5, this.isSuspended);
			insertSt.executeUpdate();
	        
			keySet = insertSt.getGeneratedKeys();
			keySet.next();
			
        	this.setDocumentId(keySet.getInt("id"));
        	super.parseTTAttributes(keySet);
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
        ResultSet keySet = null;
        
        try
        {
        	Document updatingDoc = (Document) newDoc;
        	
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        String loginableQuery = ""
        			+ "UPDATE "
        			+ "		document "
        			+ "SET "
        			+ "		document_no = ?, valid_from = ?, valid_until = ?, is_suspended = ? "
        			+ "WHERE "
        			+ "		id = ?";
	        	
	        updateSt = newConn.prepareStatement(loginableQuery, new String[] {"creation_time", "last_modified_time"});
	        updateSt.setString(1, updatingDoc.documentNo.trim());
	        updateSt.setObject(2, updatingDoc.validFrom != null ? updatingDoc.validFrom : null, Types.TIMESTAMP);
	        updateSt.setTimestamp(3, updatingDoc.validUntil);
	        updateSt.setBoolean(4, updatingDoc.isSuspended);
	        updateSt.setInt(5, this.documentId);
	        
	        if(updateSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        keySet = updateSt.getGeneratedKeys();
			keySet.next();
			
			this.setDocumentNo(updatingDoc.getDocumentNo());
			this.setValidFrom(updatingDoc.getValidFrom());
			this.setValidUntil(updatingDoc.getValidUntil());
			this.setSuspended(updatingDoc.isSuspended());
			this.parseTTAttributes(keySet);
		}
        catch(ClassCastException cex)
        {
        	throw new IllegalArgumentException();
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
	public void deleteFromDB(Connection conn) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement sqlSt = null;
        
        try
        {
	        if(newConn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			String deleteQuery = "DELETE FROM document WHERE id = ?";
	        sqlSt = newConn.prepareStatement(deleteQuery);
	        sqlSt.setInt(1, this.documentId);
	        
	        if(sqlSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        this.documentId = -1;
	        this.creationTime = null;
	        this.lastModifiedTime = null;
	    }
        finally
        {
        	DBUtils.close(sqlSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	//Get-Set
	public int getOwnerLoginableId()
	{
		return ownerLoginableId;
	}
	
	public void setOwnerLoginableId(int ownerLoginableId)
	{
		this.ownerLoginableId = ownerLoginableId;
	}
	
	public int getDocumentId() 
	{
		return documentId;
	}
	
	public void setDocumentId(int documentId) 
	{
		this.documentId = documentId;
	}
	
	public String getDocumentNo() 
	{
		return documentNo;
	}
	
	public void setDocumentNo(String documentNo) 
	{
		this.documentNo = documentNo;
	}
	
	public Timestamp getValidFrom() 
	{
		return validFrom;
	}
	
	public void setValidFrom(Timestamp validFrom) 
	{
		this.validFrom = validFrom;
	}
	
	public Timestamp getValidUntil() 
	{
		return validUntil;
	}
	
	public void setValidUntil(Timestamp validUntil) 
	{
		this.validUntil = validUntil;
	}
	
	public boolean isSuspended() 
	{
		return isSuspended;
	}
	
	public void setSuspended(boolean isSuspended) 
	{
		this.isSuspended = isSuspended;
	}
	
	//Statik Sorgular
	public static Document[] findByOwner(Connection conn, Loginable owner) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition[] typeDefinitions = new TypeDefinition[] 
			{
				new TypeDefinition(Licence.class, "ls"),
				new TypeDefinition(LifeguardLicence.class, "lgl"),
			};
			String docTableVar = "doc";
			
			String query = ""
					+ "SELECT "
					+ 		Document.generateColumnNameString(typeDefinitions, docTableVar) + " "
					+ "FROM "
					+ "		document doc "
					+ "			LEFT OUTER JOIN licence ls "
					+ "				ON (ls.document_parent = doc.id) "
					+ "			LEFT OUTER JOIN lifeguard_licence lgl "
					+ "				ON (lgl.document_parent = doc.id) "
					+ "WHERE "
					+ "		doc.owner = ? "
					+ "ORDER BY "
					+ "		doc.valid_until ASC";
					
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setInt(1, owner.getLoginableId());
	        rs = sqlSt.executeQuery();
	        
	        Vector<Document> docs = new Vector<Document>(0);
	        
	        while(rs.next())
	        	docs.add(Document.parseFromRecord(rs, typeDefinitions, docTableVar));
	        
	        if(docs.size() > 0)
	        	docs.trimToSize();
	        
			return docs.isEmpty() ? null : docs.toArray(new Document[docs.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static Document findByDocumentId(Connection conn, int documentId) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition[] typeDefinitions = new TypeDefinition[] 
			{
				new TypeDefinition(Licence.class, "ls"),
				new TypeDefinition(LifeguardLicence.class, "lgl"),
			};
			String docTableVar = "doc";
			
			String query = ""
					+ "SELECT "
					+ 		Document.generateColumnNameString(typeDefinitions, docTableVar) + " "
					+ "FROM "
					+ "		document doc "
					+ "			LEFT OUTER JOIN licence ls "
					+ "				ON (ls.document_parent = doc.id) "
					+ "			LEFT OUTER JOIN lifeguard_licence lgl "
					+ "				ON (lgl.document_parent = doc.id) "
					+ "WHERE "
					+ "		doc.id = ? "
					+ "ORDER BY "
					+ "		doc.valid_until ASC";
					
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setInt(1, documentId);
	        rs = sqlSt.executeQuery();
	        
	        Document doc = null;
	        
	        if(rs.next())
	        	doc = Document.parseFromRecord(rs, typeDefinitions, docTableVar);
	        
	        return doc;
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
	public static Document parseFirstInstance(Element parentEl)
	{
		try
		{
			NodeList nodes = parentEl.getElementsByTagName("*");
			Document doc = null;
			
			for(int i = 0; i < nodes.getLength(); i++)
			{
				Element nextEl = (Element) nodes.item(i);
				
				if(nextEl.getNodeName().equals("licence"))
					doc = new Licence();
				else if(nextEl.getNodeName().equals("lifeguard_licence"))
					doc = new LifeguardLicence();
				
				if(doc != null)
				{
					doc.parseFromXMLElement(nextEl);
					break;
				}
			}
			
			return doc;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml element.", ex);
		}
	}
	
	public static Document parseFromRecord(ResultSet rs, TypeDefinition[] typeDefinitions, String loginableTableVar)
		throws SQLException
	{
		Document doc = null;
		
		for(TypeDefinition nextDef : typeDefinitions)
		{
			Class<? extends Document> nextType = nextDef.getType();
			String nextTableVar = nextDef.getTableVariable();
			
			int nextTypeId = rs.getInt(nextTableVar + "_id");
			
			if(nextType == Licence.class && nextTypeId > 0)
				doc = Licence.parseFromRecord(rs, nextDef, loginableTableVar);
	    	else if(nextType == LifeguardLicence.class && nextTypeId > 0)
	    		doc = LifeguardLicence.parseFromRecord(rs, nextDef, loginableTableVar);
			
			if(doc != null)
				break;
		}
		
		return doc;
	}
	
	private static String[] generateColumnNames(String tableVar)
	{
		if(tableVar == null)
			throw new IllegalArgumentException("Bad table variable.");
		
		String[] colNames = new String[7];
		
		if(tableVar != null)
		{
			int columnIndex = 0;
			
			colNames[columnIndex] = tableVar + "_id"; columnIndex++;
			colNames[columnIndex] = tableVar + "_document_no"; columnIndex++;
			colNames[columnIndex] = tableVar + "_valid_from"; columnIndex++;
			colNames[columnIndex] = tableVar + "_valid_until"; columnIndex++;
			colNames[columnIndex] = tableVar + "_is_suspended"; columnIndex++;
			colNames[columnIndex] = tableVar + "_creation_time"; columnIndex++;
			colNames[columnIndex] = tableVar + "_last_modified_time"; columnIndex++;
		}
		
		return colNames;
	}
	
	public static String[] generateColumnNames(TypeDefinition typeDefinition, String docTableVar)
	{
		if(typeDefinition == null || docTableVar == null)
			throw new IllegalArgumentException("Bad table or subtype variables.");
		
		String[] typeCols = typeDefinition.getTypeColumns();
		String[] docCols = Document.generateColumnNames(docTableVar);
		
		String[] colNames = new String[docCols.length + typeCols.length];
		
		int columnIndex = 0;
		
		for(String nextCol : typeCols)
		{
			colNames[columnIndex] = nextCol; 
			columnIndex++;
		}
		
		for(String nextCol : docCols)
		{
			colNames[columnIndex] = nextCol; 
			columnIndex++;
		}
		
		return colNames;
	}
	
	public static String generateColumnNameString(TypeDefinition[] includedTypes, String docTableVar)
	{
		if(includedTypes == null || includedTypes.length == 0 || docTableVar == null)
			throw new IllegalArgumentException("Bad table or subtype variables.");
		
		String colNameStr = "";
		
		for(TypeDefinition nextDefinition : includedTypes)
		{
			for(String nextColStr : nextDefinition.getTypeColumns())
			{
				String nextTableVar = nextDefinition.getTableVariable();
				String orgColStr = nextColStr.substring(nextTableVar.length() + 1);
				colNameStr += ", " + nextTableVar + "." + orgColStr + " AS " + nextColStr;
			}
		}
		
		for(String nextLoginableColStr : Document.generateColumnNames(docTableVar))
		{
			String orgColStr = nextLoginableColStr.substring(nextLoginableColStr.indexOf("_") + 1);
			colNameStr += ", " + docTableVar + "." + orgColStr + " AS " + nextLoginableColStr;
		}
		
		colNameStr = colNameStr.substring(2);
		
		return colNameStr;
	}
	
	//İç Sınıflar
	public static class TypeDefinition
	{
		private Class<? extends Document> type = null;
		private String tableVariable = null;
		
		public TypeDefinition(Class<? extends Document> type, String tableVariable)
		{
			this.type = type;
			this.tableVariable = tableVariable;
		}
		
		//Get-Set
		public Class<? extends Document> getType() 
		{
			return type;
		}

		public void setType(Class<? extends Document> type) 
		{
			this.type = type;
		}

		public String getTableVariable() 
		{
			return tableVariable;
		}

		public void setTableVariable(String tableVariable) 
		{
			this.tableVariable = tableVariable;
		}
		
		public String[] getTypeColumns()
		{
			String[] typeCols = null;
			
			if(this.type == Licence.class) 
				return Licence.generateColumnNames(this.tableVariable);
			else if(this.type == LifeguardLicence.class)
				return LifeguardLicence.generateColumnNames(this.tableVariable);
			
			return typeCols;
		}
	}
}