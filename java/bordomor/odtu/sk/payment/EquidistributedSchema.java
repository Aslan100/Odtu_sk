package bordomor.odtu.sk.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.PaymentSchemaType;
import bordomor.odtu.sk.template.PaymentSchema;
import bordomor.util.DBUtils;

public class EquidistributedSchema extends PaymentSchema 
{
	public int id = -1;
	public short numberOfInstallments = -1;
	
	public EquidistributedSchema() 
	{
		super();
	}
	
	public EquidistributedSchema(int id)
	{
		this.id = id;
	}
	
	public EquidistributedSchema(int id, short numberOfInstallments, int schemaId, String title, Branch branch, PaymentSchemaType type)
	{
		super(schemaId, title, branch, type);
		this.id = id;
		this.numberOfInstallments = numberOfInstallments;
	}
	
	//Yardımcı Metodlar
	@Override
	public PaymentPlan generatePlan(boolean isSiblingPlan) { return null; }
	
	@Override
	public Element makeXMLElement(Document xml, boolean cascadeRelations) 
	{
		try
		{
			Element schemaEl = xml.createElement("interval_constrained_schema");
			schemaEl.setAttribute("id", this.id + "");
			schemaEl.setAttribute("number_of_installments", this.numberOfInstallments + "");
			
			super.appendSchemaAttributes(schemaEl, cascadeRelations);
			
			return schemaEl;
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
        ResultSet keySet = null;
        
        this.id = -1;
        this.schemaId = -1;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        this.type = PaymentSchemaType.INTERVAL_CONSTRAINED;
	        super.createInDB(newConn);
	        
			String insertQuery = ""
					+ "INSERT INTO "
					+ "		interval_constrained_schema "
					+ "			(number_of_installments, schema_parent) "
					+ "VALUES "
					+ "		(?, ?)";
			
			insertSt = newConn.prepareStatement(insertQuery, new String[] { "id" });
			insertSt.setShort(1, this.numberOfInstallments);
			insertSt.setInt(2, this.schemaId);
			
			if(insertSt.executeUpdate() != 1)
				throw new IllegalArgumentException();
				
			keySet = insertSt.getGeneratedKeys();
			keySet.next();
		    this.setId(keySet.getInt("id"));
		}
        catch(Exception ex)
        {
        	if(this.schemaId != -1)
        		super.deleteFromDB(newConn);
        	
        	throw ex;
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
	public void deleteFromDB(Connection conn) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
        PreparedStatement deleteSt = null;
        
        try
        {
	        if(newConn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        String deleteQuery = ""
	        		+ "DELETE FROM "
	        		+ "		payment_schema "
	        		+ "WHERE "
	        		+ "		id = (SELECT schema_parent FROM equidistributed_schema WHERE id = ?)";
	        
			deleteSt = newConn.prepareStatement(deleteQuery);
			deleteSt.setInt(1, this.id);
	        
	        if(deleteSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        this.id = -1;
	        this.schemaId = -1;
	        this.creationTime = null;
	        this.lastModifiedTime = null;
	    }
        finally
        {
        	DBUtils.close(deleteSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	//Get-Set
	@Override
	public int getId() 
	{
		return id;
	}
	
	public void setId(int id) 
	{
		this.id = id;
	}

	public short getNumberOfInstallments() 
	{
		return numberOfInstallments;
	}
	
	public void setNumberOfInstallments(short numberOfInstallments) 
	{
		this.numberOfInstallments = numberOfInstallments;
	}
	
	//Statik Metodlar
	public static String[] generateColumnNames(String tableVar)
	{
		if(tableVar == null)
			throw new IllegalArgumentException("Bad table variable.");
		
		String[] colNames = new String[2];
		
		if(tableVar != null)
		{
			int columnIndex = 0;
			
			colNames[columnIndex] = tableVar + "_id"; columnIndex++;
			colNames[columnIndex] = tableVar + "_number_of_installments"; columnIndex++;
		}
		
		return colNames;
	}
	
	public static EquidistributedSchema parseFromRecord(ResultSet rs, TypeDefinition typeDefinition)
	{
		try
		{
			Class<? extends PaymentSchema> type = typeDefinition.getType();
			String tableVar = typeDefinition.getTableVariable();
			
			if(type != EquidistributedSchema.class || tableVar == null)
				throw new Exception("Bad type definition.");
			
			EquidistributedSchema parsedSchema = null;
			int id = rs.getInt(tableVar + "_id");  
			
			if(!rs.wasNull())
			{
				parsedSchema = new EquidistributedSchema(id);
				parsedSchema.setNumberOfInstallments(rs.getShort(tableVar + "_number_of_installments"));
			}
				
			return parsedSchema;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
}