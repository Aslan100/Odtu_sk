package bordomor.odtu.sk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import bordomor.odtu.sk.Params.RegistrationState;
import bordomor.odtu.sk.Params.RegistrationStep;
import bordomor.odtu.sk.Params.RegistrationType;
import bordomor.odtu.sk.template.DBTimeTrackable;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.IXmlObject;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.util.DBUtils;
import bordomor.util.StringUtils;

@XMLAndDatabaseValues(tableName = "registration", tagName = "registration", defaultVariable = "reg")
public class Registration extends DBTimeTrackable implements IXmlObject, IDBObject 
{
	private int id = -1;
	private String code = null;
	private RegistrationType type = null;
	private RegistrationStep lastCompletedStep = null;
	private Branch registartionBranch = null;
	private Athlete registered = null;
	private Parent registrant = null;
	private Training firstTraining = null;
	private int paymentSchema = -1;
	private int generatedPlan = -1;
	private boolean isSiblingRegistration = false;
	private RegistrationState state = null;
	
	public Registration() { super(); }
	
	public Registration(int id)
	{
		super();
		this.id = id;
	}
	
	public Registration(int id, RegistrationType type, RegistrationState state)
	{
		super();
		this.id = id;
		this.type = type;
		this.state = state;
	}
	
	public Registration(int id, String code, RegistrationType type, RegistrationState state)
	{
		this(id, type, state);
		this.code = code;
	}
	
	//Xml Bölümü
	@Override
	public void parseFromXMLElement(Element element) {}

	@Override
	public Element makeXMLElement(Document xml, boolean cascadeRelations)
	{
		try
		{
			Element registrationEl = xml.createElement("registration");
			registrationEl.setAttribute("id", this.id + "");
			
			if(this.code != null)
				registrationEl.setAttribute("code", this.code);
			
			if(this.type != null)
				registrationEl.setAttribute("type", this.type.toString());
			
			if(this.lastCompletedStep != null)
				registrationEl.setAttribute("last_completed_step", this.lastCompletedStep.toString());
			
			registrationEl.setAttribute("payment_schema", this.paymentSchema + "");
			registrationEl.setAttribute("generated_plan", this.generatedPlan + "");
			registrationEl.setAttribute("is_sibling_registration", this.isSiblingRegistration + "");
			
			if(this.state != null)
				registrationEl.setAttribute("state", this.state.toString());
			
			if(cascadeRelations)
			{
				if(this.registartionBranch != null)
					registrationEl.appendChild(this.registartionBranch.makeXMLElement(xml, false));
				
				if(this.registered != null)
					registrationEl.appendChild(this.registered.makeXMLElement(xml, false));
				
				if(this.registrant != null)
					registrationEl.appendChild(this.registrant.makeXMLElement(xml, false));
				
				if(this.firstTraining != null)
					registrationEl.appendChild(this.firstTraining.makeXMLElement(xml, true));
			}
			
			return registrationEl;
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
	        
	        String genCode = "KYT-" + LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue() + "-" + StringUtils.generateRandomString(4, 5, true);
	        
			String insertQuery = ""
    				+ "INSERT INTO "
    				+ "		registration "
    				+ "			(code, type, last_completed_step, registration_branch, registered, registrant, first_training, payment_schema, generated_plan, is_sibling_registration, state) "
    				+ "VALUES "
    				+ "		(?, ?::registration_type, ?::registration_step, ?, ?, ?, ?, ?, ?, ?, ?::registration_state)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id", "code", "creation_time", "last_modified_time"});
    		insertSt.setString(1, genCode);
    		insertSt.setString(2, this.type.toString());
    		insertSt.setObject(3, this.lastCompletedStep != null ? this.lastCompletedStep.toString() : null, Types.VARCHAR);
    		insertSt.setObject(4, this.registartionBranch != null ? this.registartionBranch.getId() : null, Types.INTEGER);
    		insertSt.setObject(5, this.registered != null ? this.registered.getId() : null, Types.INTEGER);
    		insertSt.setObject(6, this.registrant != null ? this.registrant.getId() : null, Types.INTEGER);
    		insertSt.setObject(7, this.firstTraining != null ? this.firstTraining.getId() : null, Types.INTEGER);
    		insertSt.setObject(8, this.paymentSchema > 0 ? this.paymentSchema : null, Types.INTEGER);
    		insertSt.setObject(9, this.generatedPlan > 0 ? this.generatedPlan : null, Types.INTEGER);
    		insertSt.setBoolean(10, this.isSiblingRegistration);
    		insertSt.setString(11, this.state.toString());
    		
    		insertSt.executeUpdate();
        	
    		keySet = insertSt.getGeneratedKeys();
    		keySet.next();
    		this.setId(keySet.getInt("id"));
    		this.setCode(keySet.getString("code"));
    		this.parseTTAttributes(keySet);
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
	        
			String deleteQuery = ""
					+ "DELETE FROM registration WHERE id = ?";
	        
	        sqlSt = newConn.prepareStatement(deleteQuery);
	        sqlSt.setInt(1, this.id);
	        
	        if(sqlSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        this.id = -1;
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
		
	//Get-set	
	public int getId() 
	{
		return id;
	}
	
	public void setId(int id) 
	{
		this.id = id;
	}
	
	public String getCode() 
	{
		return code;
	}
	
	public void setCode(String code) 
	{
		this.code = code;
	}
	
	public RegistrationType getType() 
	{
		return type;
	}
	
	public void setType(RegistrationType type) 
	{
		this.type = type;
	}
	
	public RegistrationStep getLastCompletedStep() 
	{
		return lastCompletedStep;
	}
	
	public void setLastCompletedStep(RegistrationStep lastCompletedStep) 
	{
		this.lastCompletedStep = lastCompletedStep;
	}
	
	public Branch getRegistartionBranch() 
	{
		return registartionBranch;
	}
	
	public void setRegistartionBranch(Branch registartionBranch) 
	{
		this.registartionBranch = registartionBranch;
	}
	
	public Athlete getRegistered() 
	{
		return registered;
	}
	
	public void setRegistered(Athlete registered) 
	{
		this.registered = registered;
	}
	
	public Parent getRegistrant() 
	{
		return registrant;
	}
	
	public void setRegistrant(Parent registrant) 
	{
		this.registrant = registrant;
	}
	
	public Training getFirstTraining() 
	{
		return firstTraining;
	}
	
	public void setFirstTraining(Training firstTraining) 
	{
		this.firstTraining = firstTraining;
	}
	
	public int getPaymentSchema() 
	{		
		return paymentSchema;
	}
	
	public void setPaymentSchema(int paymentSchema) 
	{
		this.paymentSchema = paymentSchema;
	}
	
	public int getGeneratedPlan() 
	{		
		return generatedPlan;
	}
	
	public void setGeneratedPlan(int generatedPlan) 
	{
		this.generatedPlan = generatedPlan;
	}
	
	public boolean isSiblingRegistration() 
	{
		return isSiblingRegistration;
	}

	public void setSiblingRegistration(boolean isSiblingRegistration) 
	{
		this.isSiblingRegistration = isSiblingRegistration;
	}

	public RegistrationState getState() 
	{
		return state;
	}
	
	public void setState(RegistrationState state) 
	{
		this.state = state;
	}
	
	//Statik Sorgular
	public static Registration findByCode(Connection conn, String code) 
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
					+ "SELECT * FROM registration WHERE code = ?";
					
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, code);
	        rs = sqlSt.executeQuery();
	        
	        Registration reg = null;
	        
	        if(rs.next())
	        	reg = Registration.parseFromRecord(rs);
	        
	        return reg;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static Registration findByRegistered_CompletedRegistration(Connection conn, Athlete registered) 
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
					+ "SELECT * FROM registration WHERE registered = ? AND state = ?::registration_state";
					
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, registered.getId());
	        sqlSt.setString(2, RegistrationState.COMPLETED.toString());
	        rs = sqlSt.executeQuery();
	        
	        Registration reg = null;
	        
	        if(rs.next())
	        	reg = Registration.parseFromRecord(rs);
	        
	        return reg;
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
	public static Registration parseFromRecord(ResultSet rs)
	{
		try
		{
			Registration parsedReg = null;
			int regId = rs.getInt("id"); 
			
			if(!rs.wasNull())
			{
				parsedReg = new Registration(regId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals("code"))
						parsedReg.setCode(rs.getString(nextCol));
					else if(nextCol.equals("type"))
						parsedReg.setType(RegistrationType.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals("last_completed_step") && rs.getString(nextCol) != null)
						parsedReg.setLastCompletedStep(RegistrationStep.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals("registration_branch") && rs.getString(nextCol) != null)
						parsedReg.setRegistartionBranch(Branch.findById_IncludePromotions(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("registered") && rs.getInt(nextCol) > 0)
						parsedReg.setRegistered(Athlete.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("registrant") && rs.getInt(nextCol) > 0)
						parsedReg.setRegistrant(Parent.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("payment_schema"))
						parsedReg.setPaymentSchema(rs.getInt(nextCol) > 0 ? rs.getInt(nextCol) : -1);
					else if(nextCol.equals("generated_plan"))
						parsedReg.setGeneratedPlan(rs.getInt(nextCol) > 0 ? rs.getInt(nextCol) : -1);
					else if(nextCol.equals("is_sibling_registration"))
						parsedReg.setSiblingRegistration(rs.getBoolean(nextCol));
					else if(nextCol.equals("state"))
						parsedReg.setState(RegistrationState.valueOf(rs.getString(nextCol)));
				}
				
				parsedReg.parseTTAttributes(rs);
			}
				
			return parsedReg;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
}
