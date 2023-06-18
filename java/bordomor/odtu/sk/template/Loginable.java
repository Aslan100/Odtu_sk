package bordomor.odtu.sk.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import bordomor.odtu.sk.Athlete;
import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.BranchManager;
import bordomor.odtu.sk.ClubManager;
import bordomor.odtu.sk.Donor;
import bordomor.odtu.sk.Employee;
import bordomor.odtu.sk.LoginRole;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.Gender;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Parent;
import bordomor.odtu.sk.Team;
import bordomor.odtu.sk.Trainer;
import bordomor.util.DBUtils;
import bordomor.util.StringUtils;

@XMLAndDatabaseValues(tableName = "loginable", tagName = "loginable", defaultVariable = "l")
public abstract class Loginable extends DBTimeTrackable implements IXmlObject, IDBObject
{
	protected int loginableId = -1;
	protected String code = null;
	protected String email = null;
	protected String name = null;
	protected String surname = null;
	protected Gender gender = null;
	protected String phoneNumber = null;
	protected String hesCode = null;
	protected LoginableState state = null;
	
	private MedicalData medicals = null;
	private String pwd = null;
	
	protected Loginable() {}
	
	protected Loginable(int id, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state)
	{
		super();
		this.loginableId = id;
		this.code = code;
		this.email = email;
		this.name = name;
		this.surname = surname;
		this.gender = gender;
		this.phoneNumber = phoneNumber;
		
		this.state = state;
	}
	
	protected Loginable(int id, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state, Timestamp creationTime, Timestamp lastModifiedTime)
	{
		super(creationTime, lastModifiedTime);
		this.loginableId = id;
		this.code = code;
		this.email = email;
		this.name = name;
		this.surname = surname;
		this.gender = gender;
		this.phoneNumber = phoneNumber;
		this.state = state;
	}
	
	//Yardımcı Metodlar
	@Override
 	public boolean equals(Object comparedObject) 
	{
		try
		{
			if(comparedObject == this)
				return true;
			else if(comparedObject == null || !(comparedObject instanceof Loginable))
				return false;
			else
			{
				Loginable comparedLoginable = (Loginable) comparedObject;
				
				return 
						this.loginableId == comparedLoginable.loginableId 
						&& this.email.equals(comparedLoginable.email)
						&& this.name.equals(comparedLoginable.name)
						&& this.surname.equals(comparedLoginable.surname)
						&& this.state == comparedLoginable.state
						&& this.gender == comparedLoginable.gender
						&& this.phoneNumber.equals(comparedLoginable.phoneNumber)
						&& this.hesCode.equals(comparedLoginable.hesCode);
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
        result = prime*result + (int) (this.loginableId ^ (this.loginableId >>> 32));
        result = prime*result + (this.email != null ? this.email.hashCode() : 0);
        result = prime*result + (this.name != null ? this.name.hashCode() : 0);
        result = prime*result + (this.surname != null ? this.surname.hashCode() : 0);
        result = prime*result + (this.state != null ? this.state.hashCode() : 0);
        result = prime*result + (this.gender != null ? this.gender.hashCode() : 0);
        result = prime*result + (this.phoneNumber != null ? this.phoneNumber.hashCode() : 0);
        result = prime*result + (this.hesCode != null ? this.hesCode.hashCode() : 0);
        
        return result;
    }
	
	//Soyut Metodlar
	public abstract bordomor.odtu.sk.template.Document[] getDocuments();
	
	public abstract LoginRole getRole();
	
	public abstract Branch getPrimaryBranch();
	
	public abstract void setRole(LoginRole role);
	
	public abstract void manipulateState(Connection conn, LoginableState newState) throws ClassNotFoundException, SQLException;
	
	public abstract void manipulatePwd(Connection conn, String newPwd, boolean activate)  throws ClassNotFoundException, SQLException; 
	
	public abstract void fetchDocumentsFromDB(Connection conn) throws ClassNotFoundException, SQLException;
	
	public abstract boolean isLoginPermitted();
	
	public abstract boolean canOperateOn(Connection conn, Loginable loginable) throws ClassNotFoundException, SQLException;
	
	public abstract boolean canOperateOn(Connection conn, Team team) throws ClassNotFoundException, SQLException;
	
	public abstract boolean canOperateOn(Connection conn, Branch branch);
	
	public abstract String getHomeURI();
	
	public abstract String generatePortalLeftMenu();
	
	public abstract String generatePortalTopBar();
	
	public abstract String generatePageMenu(String returnURI);
	
	//XML Bölümü
	protected void appendLoginableAttributes(Element userEl, boolean cascadeRelations)
	{
		userEl.setAttribute("loginable_id", this.loginableId + "");
		
		if(this.code != null)
			userEl.setAttribute("code", this.code);
		
		if(this.email != null)
			userEl.setAttribute("email", this.email);
		
		if(this.name != null)
			userEl.setAttribute("name", this.name);
		
		if(this.surname != null)
			userEl.setAttribute("surname", this.surname);
		
		if(this.gender != null)
			userEl.setAttribute("gender", this.gender.toString());
		
		if(this.phoneNumber != null)
			userEl.setAttribute("phone_number", this.phoneNumber);
		
		if(this.hesCode != null)
			userEl.setAttribute("hes_code", this.hesCode);
		
		if(this.state != null)
			userEl.setAttribute("state", this.state.toString());
		
		this.appendTTAttributes(userEl);
	}
	
	@Override
	public void parseFromXMLElement(Element userEl)
	{
		this.loginableId = Integer.parseInt(userEl.getAttribute("loginable_id"));
		this.email = userEl.hasAttribute("email") ? userEl.getAttribute("email") : null;
		this.name = userEl.hasAttribute("name") ? userEl.getAttribute("name") : null;
		this.surname = userEl.hasAttribute("surname") ? userEl.getAttribute("surname") : null;
		this.gender = userEl.hasAttribute("gender") ? Gender.valueOf(userEl.getAttribute("gender")) : null;
		this.phoneNumber = userEl.hasAttribute("phone_number") ? userEl.getAttribute("phone_number") : null;
		this.hesCode = userEl.hasAttribute("hes_code") ? userEl.getAttribute("hes_code") : null;
		this.state = userEl.hasAttribute("state") ? LoginableState.valueOf(userEl.getAttribute("state")) : null;
		
		this.parseTTAttributes(userEl);
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
	        
	        String genCode = "SPR-" + StringUtils.generateRandomString(3, 4, true) + "-" + StringUtils.generateRandomString(3, 4, true);
	        String pwd = StringUtils.generateRandomString(8, 10, true);
	        
			String insertQuery = ""
					+ "INSERT INTO "
					+ "		loginable (code, email, pwd, name, surname, gender, phone_number, hes_code, state) "
					+ "VALUES "
					+ "		(?, ?, ?, ?, ?, ?::gender, ?, ?, ?::loginable_state)";
	        
			insertSt = newConn.prepareStatement(insertQuery, new String[] {"id", "code", "creation_time", "last_modified_time"});
			insertSt.setString(1, genCode);
			insertSt.setString(2, this.email.trim());
			insertSt.setString(3, pwd);
			insertSt.setString(4, this.name.trim());
			insertSt.setString(5, this.surname.trim());
    		insertSt.setString(6, this.gender.toString());
    		insertSt.setObject(7, this.phoneNumber != null ? this.phoneNumber.trim() : null, Types.VARCHAR);
    		insertSt.setObject(8, this.hesCode != null ? this.hesCode.trim() : null, Types.VARCHAR);
			insertSt.setString(9, this.state.toString());
			insertSt.executeUpdate();
	        
			keySet = insertSt.getGeneratedKeys();
			keySet.next();
			
        	this.setLoginableId(keySet.getInt("id"));
        	this.setCode(keySet.getString("code"));
        	this.setPwd(pwd);
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
	public void updateInDB(Connection conn, IDBObject newLoginable) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
        PreparedStatement updateSt = null;
        ResultSet keySet = null;
        
        try
        {
        	Loginable updatingLoginable = (Loginable) newLoginable;
        	
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        String loginableQuery = ""
        			+ "UPDATE "
        			+ "		loginable "
        			+ "SET "
        			+ "		email = ?, name = ?, surname = ?, gender = ?::gender, phone_number = ?, hes_code = ?, state = ?::loginable_state "
        			+ "WHERE "
        			+ "		id = ?";
	        	
	        updateSt = newConn.prepareStatement(loginableQuery, new String[] {"creation_time", "last_modified_time"});
	        updateSt.setString(1, updatingLoginable.email.trim());
	        updateSt.setString(2, updatingLoginable.name.trim());
	        updateSt.setString(3, updatingLoginable.surname.trim());
	        updateSt.setString(4, updatingLoginable.gender.toString());
	        updateSt.setObject(5, updatingLoginable.phoneNumber != null ? updatingLoginable.phoneNumber.trim() : null, Types.VARCHAR);
	        updateSt.setObject(6, updatingLoginable.hesCode != null ? updatingLoginable.hesCode.trim() : null, Types.VARCHAR);
	        updateSt.setString(7, updatingLoginable.getState().toString());
	        updateSt.setInt(8, this.loginableId);
	        
	        if(updateSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        keySet = updateSt.getGeneratedKeys();
			keySet.next();
			
			this.setEmail(updatingLoginable.getEmail());
	        this.setState(updatingLoginable.getState());
	        super.parseTTAttributes(keySet);
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
	public void updateColumnInDB(Connection conn, String columnName, Object newValue, int columnType) 
			throws ClassNotFoundException, SQLException 
	{
		if(columnName == null || this.getId() == -1)
			throw new IllegalArgumentException();
		
		String tableName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tableName();
		
		Connection newConn = conn;
        PreparedStatement updateSt = null;
        ResultSet keySet = null;
        
        try
        {
        	 if(conn == null)
 	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
        	 
        	 String updateQuery = ""
        	 		+ "UPDATE " + tableName + " SET " + columnName + " = ? WHERE id = ?";
        	 
        	 updateSt = newConn.prepareStatement(updateQuery);
        	 updateSt.setObject(1, newValue != null ? newValue.toString() : null, columnType);
        	 updateSt.setInt(2, this.getId());
        	 
        	 if(updateSt.executeUpdate() != 1)
 	        	throw new IllegalArgumentException();
        	 
        	 keySet = updateSt.getGeneratedKeys();
        	 keySet.next();
        	 //this.parseTTAttributes(keySet);
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
	public void updateColumnInDB(Connection conn, String columnName, Enum<?> newValue, String dbEnumName) 
			throws ClassNotFoundException, SQLException 
	{
		if(columnName == null || this.getId() == -1)
			throw new IllegalArgumentException();
		
		String tableName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tableName();
		
		Connection newConn = conn;
        PreparedStatement updateSt = null;
        ResultSet keySet = null;
        
        try
        {
        	 if(conn == null)
 	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
        	 
        	 String updateQuery = ""
        	 		+ "UPDATE " + tableName + " SET " + columnName + " = ?::" + dbEnumName + " WHERE id = ?";
        	 
        	 updateSt = newConn.prepareStatement(updateQuery);
        	 updateSt.setObject(1, newValue != null ? newValue.toString() : null, Types.VARCHAR);
        	 updateSt.setInt(2, this.getId());
        	 
        	 if(updateSt.executeUpdate() != 1)
 	        	throw new IllegalArgumentException();
        	
        	 keySet = updateSt.getGeneratedKeys();
        	 keySet.next();
        	 //this.parseTTAttributes(keySet);
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
	        
			String deleteQuery = "DELETE FROM loginable WHERE id = ?";
	        sqlSt = newConn.prepareStatement(deleteQuery);
	        sqlSt.setInt(1, this.loginableId);
	        
	        if(sqlSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        this.loginableId = -1;
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
	
	public void recoverInDB(Connection conn) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement insertSt = null;
        ResultSet keySet = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			String insertQuery = ""
					+ "UPDATE "
					+ "		loginable "
					+ "SET "
					+ "		pwd = ?, state = ?::loginable_state "
					+ "WHERE "
					+ "		id = ? "
					+ "		AND (state = ?::loginable_state OR state = ?::loginable_state)";
	        
			insertSt = newConn.prepareStatement(insertQuery, new String[] {"last_modified_time"});
			String pwd = StringUtils.generateRandomString(8, 10, true);
			insertSt.setString(1, pwd);
			insertSt.setString(2, LoginableState.PENDING.toString());
			insertSt.setInt(3, this.loginableId);
			insertSt.setString(4, LoginableState.ACTIVE.toString());
			insertSt.setString(5, LoginableState.PENDING.toString());
			insertSt.executeUpdate();
	        
			keySet = insertSt.getGeneratedKeys();
			keySet.next();
			
        	this.setPwd(pwd);
        	this.setState(LoginableState.PENDING);
        	this.setLastModifiedTime(keySet.getTimestamp("last_modified_time"));
    	}
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(insertSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	public void fetchMedicalDataFromDB(Connection conn) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = "SELECT * FROM medical_data WHERE loginable = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, this.loginableId);
	        rs = sqlSt.executeQuery();
	        
	        this.medicals = null;
	        
	        if(rs.next())
	        {
	        	this.medicals = this.new MedicalData();
	        	this.medicals.setId(rs.getInt("id"));
	        	this.medicals.setPastTherapies(rs.getString("past_therapies"));
	        	this.medicals.setActiveMedications(rs.getString("active_medications"));
	        	this.medicals.setActiveIssues(rs.getString("active_issues"));
	        	this.medicals.setSpecialCareNeeds(rs.getString("special_care_needs"));
	        }
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	//Get-Set
	public int getLoginableId() 
	{
		return loginableId;
	}
	
	public void setLoginableId(int loginableId) 
	{
		this.loginableId = loginableId;
	}
	
	public String getCode() 
	{
		return code;
	}

	public void setCode(String code) 
	{
		this.code = code;
	}

	public String getEmail() 
	{
		return email;
	}
	
	public void setEmail(String email) 
	{
		this.email = email;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getSurname()
	{
		return surname;
	}
	
	public void setSurname(String surname)
	{
		this.surname = surname;
	}
	
	public String getFullName()
	{
		String fullName = "";
		
		if(this.name != null && this.name.trim().length() > 0)
			fullName += " " + this.name.trim();
		
		if(this.surname != null && this.surname.trim().length() > 0)
			fullName += " " + this.surname.trim();
		
		return fullName.length() > 0 ? fullName.substring(1) : null;
	}
	
	public String getInitials()
	{
		if(this.name != null && this.surname != null)
			return this.name.substring(0, 1) + this.surname.substring(0, 1);
		
		return null;
	}
	
	public Gender getGender() 
	{
		return gender;
	}
	
	public void setGender(Gender gender) 
	{
		this.gender = gender;
	}
	
	public String getPhoneNumber() 
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) 
	{
		this.phoneNumber = phoneNumber;
	}
	
	public String getHesCode()
	{
		return hesCode;
	}
	
	public void setHesCode(String hesCode)
	{
		this.hesCode = hesCode;
	}
	
	public LoginableState getState() 
	{
		return state;
	}
	
	public void setState(LoginableState state) 
	{
		this.state = state;
	}
	
	public String getPwd()
	{
		return this.pwd;
	}
	
	protected void setPwd(String pwd)
	{
		this.pwd = pwd;
	}
	
	public MedicalData getMedicals()
	{
		return medicals;
	}
	
	//Statik Sorgular
	/*
	public static Loginable findByEmail(Connection conn, String email) throws ClassNotFoundException, SQLException
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
				new TypeDefinition(ClubManager.class, "su"),
				new TypeDefinition(ClientUser.class, "cu"),
				new TypeDefinition(Instructor.class, "ins"),
				new TypeDefinition(Corporation.class, "crp")
			}; 
			String loginableTableVar = "l";
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(typeDefinitions, loginableTableVar) + ", "
					+ "		lr.* "
					+ "FROM "
					+ "		loginable l "
					+ "			LEFT OUTER JOIN system_user su "
					+ "				ON (su.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN login_role lr "
					+ "				ON (su.role = lr.id) "
					+ "			LEFT OUTER JOIN client_user cu "
					+ "				ON (cu.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN instructor ins "
					+ "				ON (ins.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN corporation crp "
					+ "				ON (crp.loginable_parent = l.id) "
					+ "WHERE "
					+ "		l.email = ?";
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setString(1, email);
	        rs = sqlSt.executeQuery();
	        
	        Loginable user = null;
	        
	        if(rs.next())
	        	user = Loginable.parseFromRecord(rs, typeDefinitions, loginableTableVar);
	        
			return user;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }*/
	
	public static Loginable findByEmailAndPwd(Connection conn, String email, String pwd) 
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
				new TypeDefinition(ClubManager.class, "cm"),
				new TypeDefinition(BranchManager.class, "bm"),
				new TypeDefinition(Trainer.class, "trn"),
				new TypeDefinition(Parent.class, "prn"),
				new TypeDefinition(Athlete.class, "ath"),
			};
			String loginableTableVar = "l";
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(typeDefinitions, loginableTableVar) + ", "
					+ "		lr.* "
					+ "FROM "
					+ "		loginable l "
					+ "			LEFT OUTER JOIN club_manager cm "
					+ "				ON (cm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN branch_manager bm "
					+ "				ON (bm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN trainer trn "
					+ "				ON (trn.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN parent prn "
					+ "				ON (prn.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN athlete ath "
					+ "				ON (ath.loginable_parent = l.id) "
					+ " 		LEFT OUTER JOIN login_role lr "
					+ "				ON (cm.role = lr.id OR bm.role = lr.id OR trn.role = lr.id) "
					+ "WHERE "
					+ "		l.email = ? AND l.pwd = CRYPT(?, l.pwd)";
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setString(1, email);
	        sqlSt.setString(2, pwd);
	        rs = sqlSt.executeQuery();
	        
	        Loginable user = null;
	        
	        if(rs.next())
	        	user = Loginable.parseFromRecord(rs, typeDefinitions, loginableTableVar);
	        
			return user;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Loginable findByEmail(Connection conn, String email) 
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
				new TypeDefinition(ClubManager.class, "cm"),
				new TypeDefinition(BranchManager.class, "bm"),
				new TypeDefinition(Trainer.class, "trn"),
				new TypeDefinition(Parent.class, "prn"),
				new TypeDefinition(Athlete.class, "ath"),
			};
			String loginableTableVar = "l";
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(typeDefinitions, loginableTableVar) + ", "
					+ "		lr.* "
					+ "FROM "
					+ "		loginable l "
					+ "			LEFT OUTER JOIN club_manager cm "
					+ "				ON (cm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN branch_manager bm "
					+ "				ON (bm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN trainer trn "
					+ "				ON (trn.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN parent prn "
					+ "				ON (prn.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN athlete ath "
					+ "				ON (ath.loginable_parent = l.id) "
					+ " 		LEFT OUTER JOIN login_role lr "
					+ "				ON (cm.role = lr.id OR bm.role = lr.id OR trn.role = lr.id) "
					+ "WHERE "
					+ "		l.email = ?";
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setString(1, email);
	        rs = sqlSt.executeQuery();
	        
	        Loginable user = null;
	        
	        if(rs.next())
	        	user = Loginable.parseFromRecord(rs, typeDefinitions, loginableTableVar);
	        
			return user;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	/*
	public static Loginable findByStokAndPwd(Connection conn, String stok, String pwd) 
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
				new TypeDefinition(ClubManager.class, "su"),
				new TypeDefinition(ClientUser.class, "cu"),
				new TypeDefinition(Instructor.class, "ins"),
				new TypeDefinition(Corporation.class, "crp")
			};
			String loginableTableVar = "l";
					
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(typeDefinitions, loginableTableVar) + ", "
					+ "		lr.* " 
					+ "FROM "
					+ "		loginable l"
					+ "			LEFT OUTER JOIN system_user su "
					+ "				ON (su.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN login_role lr "
					+ "				ON (su.role = lr.id) "
					+ "			LEFT OUTER JOIN client_user cu "
					+ "				ON (cu.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN instructor ins "
					+ "				ON (ins.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN corporation crp "
					+ "				ON (crp.loginable_parent = l.id), "
					+ "			session_data sd "
					+ "WHERE "
					+ "		sd.loginable = l.id "
					+ "		AND sd.token = ? "
					+ "		AND l.pwd = CRYPT(?, l.pwd)";
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setString(1, stok);
	        sqlSt.setString(2, pwd);
	        rs = sqlSt.executeQuery();
	        
	        Loginable user = null;
	        
	        if(rs.next())
	        	user = Loginable.parseFromRecord(rs, typeDefinitions, loginableTableVar);
	        
			return user;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	*/
	
	public static Loginable findByStok(Connection conn, String stok) 
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
				new TypeDefinition(ClubManager.class, "cm"),
				new TypeDefinition(BranchManager.class, "bm"),
				new TypeDefinition(Trainer.class, "trn"),
				new TypeDefinition(Parent.class, "prn"),
				new TypeDefinition(Athlete.class, "ath")
			};
			String loginableTableVar = "l";
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(typeDefinitions, loginableTableVar) + ", "
					+ 		"lr.* "
					+ "FROM "
					+ "		loginable l "
					+ "			LEFT OUTER JOIN club_manager cm "
					+ "				ON (cm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN branch_manager bm "
					+ "				ON (bm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN trainer trn "
					+ "				ON (trn.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN parent prn "
					+ "				ON (prn.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN athlete ath "
					+ "				ON (ath.loginable_parent = l.id) "
					+ " 		LEFT OUTER JOIN login_role lr "
					+ "				ON (cm.role = lr.id OR bm.role = lr.id OR trn.role = lr.id), "
					+ "			session_data sd "
					+ "WHERE "
					+ "		sd.loginable = l.id "
					+ "		AND sd.token = ? ";
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setString(1, stok);
	        rs = sqlSt.executeQuery();
	        
	        Loginable user = null;
	        
	        if(rs.next())
	        	user = Loginable.parseFromRecord(rs, typeDefinitions, loginableTableVar);
	        
			return user;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Loginable findByLoginableId(Connection conn, int loginableId) 
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
				new TypeDefinition(ClubManager.class, "cm"),
				new TypeDefinition(BranchManager.class, "bm"),
				new TypeDefinition(Trainer.class, "trn"),
				new TypeDefinition(Parent.class, "prn"),
				new TypeDefinition(Athlete.class, "ath")
			};
			String loginableTableVar = "l";
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(typeDefinitions, loginableTableVar) + ", "
					+ 		"lr.* "
					+ "FROM "
					+ "		loginable l "
					+ "			LEFT OUTER JOIN club_manager cm "
					+ "				ON (cm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN branch_manager bm "
					+ "				ON (bm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN trainer trn "
					+ "				ON (trn.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN parent prn "
					+ "				ON (prn.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN athlete ath "
					+ "				ON (ath.loginable_parent = l.id) "
					+ " 		LEFT OUTER JOIN login_role lr "
					+ "				ON (cm.role = lr.id OR bm.role = lr.id OR trn.role = lr.id) "
					+ "WHERE "
					+ "		l.id = ? ";
					
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setInt(1, loginableId);
	        rs = sqlSt.executeQuery();
	        
	        Loginable user = null;
	        
	        if(rs.next())
	        	user = Loginable.parseFromRecord(rs, typeDefinitions, loginableTableVar);
	        
			return user;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Loginable findByCode(Connection conn, String code) 
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
				new TypeDefinition(ClubManager.class, "cm"),
				new TypeDefinition(BranchManager.class, "bm"),
				new TypeDefinition(Trainer.class, "trn"),
				new TypeDefinition(Parent.class, "prn"),
				new TypeDefinition(Athlete.class, "ath")
			};
			String loginableTableVar = "l";
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(typeDefinitions, loginableTableVar) + ", "
					+ 		"lr.* "
					+ "FROM "
					+ "		loginable l "
					+ "			LEFT OUTER JOIN club_manager cm "
					+ "				ON (cm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN branch_manager bm "
					+ "				ON (bm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN trainer trn "
					+ "				ON (trn.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN parent prn "
					+ "				ON (prn.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN athlete ath "
					+ "				ON (ath.loginable_parent = l.id) "
					+ " 		LEFT OUTER JOIN login_role lr "
					+ "				ON (cm.role = lr.id OR bm.role = lr.id OR trn.role = lr.id) "
					+ "WHERE "
					+ "		l.code = ? ";
					
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setString(1, code);
	        rs = sqlSt.executeQuery();
	        
	        Loginable user = null;
	        
	        if(rs.next())
	        	user = Loginable.parseFromRecord(rs, typeDefinitions, loginableTableVar);
	        
			return user;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	/*
	public static Loginable findRecoverable(Connection conn, String email, String birthDate) 
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
				new TypeDefinition(ClubManager.class, "su"),
				new TypeDefinition(ClientUser.class, "cu"),
				new TypeDefinition(Instructor.class, "ins"),
				new TypeDefinition(Corporation.class, "crp")
			};
			String loginableTableVar = "l";
					
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(typeDefinitions, loginableTableVar) + " " 
					+ "FROM "
					+ "		loginable l "
					+ "			LEFT OUTER JOIN system_user su "
					+ "				ON (su.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN client_user cu "
					+ "				ON (cu.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN instructor ins "
					+ "				ON (ins.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN corporation crp "
					+ "				ON (crp.loginable_parent = l.id) "
					+ "WHERE "
					+ "		l.email = ? "
					+ "		AND (l.state = ?::loginable_state OR l.state = ?::loginable_state) "
					+ "		AND (cu.id > ? AND cu.birth_date = ? OR su.id > ?) ";
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setString(1, email);
	        sqlSt.setString(2, LoginableState.ACTIVE.toString());
	        sqlSt.setString(3, LoginableState.PENDING.toString());
	        sqlSt.setInt(4, 0);
	        sqlSt.setString(5, birthDate);
	        sqlSt.setInt(6, 0);
	        rs = sqlSt.executeQuery();
	        
	        Loginable user = null;
	        
	        if(rs.next())
	        	user = Loginable.parseFromRecord(rs, typeDefinitions, loginableTableVar);
	        
			return user;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }*/
	
	public static Loginable[] findAll(Connection conn) 
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
				new TypeDefinition(ClubManager.class, "cm"),
				new TypeDefinition(BranchManager.class, "bm"),
				new TypeDefinition(Trainer.class, "trn"),
				new TypeDefinition(Parent.class, "prn"),
				new TypeDefinition(Athlete.class, "ath")
			};
			String loginableTableVar = "l";
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(typeDefinitions, loginableTableVar) + ", "
					+ 		"lr.* "
					+ "FROM "
					+ "		loginable l "
					+ "			LEFT OUTER JOIN club_manager cm "
					+ "				ON (cm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN branch_manager bm "
					+ "				ON (bm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN trainer trn "
					+ "				ON (trn.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN parent prn "
					+ "				ON (prn.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN athlete ath "
					+ "				ON (ath.loginable_parent = l.id) "
					+ " 		LEFT OUTER JOIN login_role lr "
					+ "				ON (cm.role = lr.id OR bm.role = lr.id OR trn.role = lr.id) ";
						
			sqlSt = newConn.prepareStatement(query);
			rs = sqlSt.executeQuery();
	        
	        Vector<Loginable> users = new Vector<Loginable>(0);
	        
	        while(rs.next())
	        	users.add(Loginable.parseFromRecord(rs, typeDefinitions, loginableTableVar));
	        
	        if(users.size() > 0)
	        	users.trimToSize();
	        
			return users.isEmpty() ? null : users.toArray(new Loginable[users.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Loginable[] findPossibleGroupHeads(Connection conn) 
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
				new TypeDefinition(ClubManager.class, "cm"),
				new TypeDefinition(BranchManager.class, "bm"),
				new TypeDefinition(Trainer.class, "trn")
			};
			String loginableTableVar = "l";
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(typeDefinitions, loginableTableVar) + ", "
					+ 		"lr.* "
					+ "FROM "
					+ "		loginable l "
					+ "			LEFT OUTER JOIN club_manager cm "
					+ "				ON (cm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN branch_manager bm "
					+ "				ON (bm.loginable_parent = l.id) "
					+ "			LEFT OUTER JOIN trainer trn "
					+ "				ON (trn.loginable_parent = l.id) "
					+ " 		LEFT OUTER JOIN login_role lr "
					+ "				ON (cm.role = lr.id OR bm.role = lr.id OR trn.role = lr.id) "
					+ "WHERE "
					+ "		l.state = ?::loginable_state";
						
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setString(1, LoginableState.ACTIVE.toString());
			rs = sqlSt.executeQuery();
	        
	        Vector<Loginable> users = new Vector<Loginable>(0);
	        
	        while(rs.next())
	        {
	        	Loginable nextLoginable = Loginable.parseFromRecord(rs, typeDefinitions, loginableTableVar);
	        	
	        	if(nextLoginable != null)
	        		users.add(nextLoginable);
	        }
	        
	        if(users.size() > 0)
	        	users.trimToSize();
	        
			return users.isEmpty() ? null : users.toArray(new Loginable[users.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static void deleteLoginableSession(Connection conn, String stok) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlStatement = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = "DELETE FROM session_data WHERE token = ?";
			sqlStatement = newConn.prepareStatement(query);
	        sqlStatement.setString(1, stok);
	        sqlStatement.executeUpdate();
	    }
		finally
		{
			DBUtils.close(sqlStatement);
			
	        if(conn == null)
	        	DBUtils.close(newConn);
	    }
	}

	//Statik Metodlar
	public static Loginable parseFirstInstance(Element parentEl)
	{
		try
		{
			NodeList nodes = parentEl.getElementsByTagName("*");
			Loginable user = null;
			
			for(int i = 0; i < nodes.getLength(); i++)
			{
				Element nextEl = (Element) nodes.item(i);
				
				if(nextEl.getNodeName().equals("club_manager"))
					user = new ClubManager();
				else if(nextEl.getNodeName().equals("branch_manager"))
					user = new BranchManager();
				else if(nextEl.getNodeName().equals("trainer"))
					user = new Trainer();
				else if(nextEl.getNodeName().equals("parent"))
					user = new Parent();
				else if(nextEl.getNodeName().equals("donor"))
					user = new Donor();
				else if(nextEl.getNodeName().equals("employee"))
					user = new Employee();
				
				if(user != null)
				{
					user.parseFromXMLElement(nextEl);
					break;
				}
			}
			
			return user;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml element.", ex);
		}
	}
	
	public static Loginable parseFromRecord(ResultSet rs, TypeDefinition[] typeDefinitions, String loginableTableVar)
		throws SQLException
	{
		Loginable loginable = null;
		
		for(TypeDefinition nextDef : typeDefinitions)
		{
			Class<? extends Loginable> nextType = nextDef.getType();
			String nextTableVar = nextDef.getTableVariable();
			
			int nextTypeId = rs.getInt(nextTableVar + "_id");
			
			if(nextType == ClubManager.class && nextTypeId > 0)
				loginable = ClubManager.parseFromRecord(rs, nextDef, loginableTableVar);
	    	else if(nextType == BranchManager.class && nextTypeId > 0)
				loginable = BranchManager.parseFromRecord(rs, nextDef, loginableTableVar);
			else if(nextType == Trainer.class && nextTypeId > 0)
				loginable = Trainer.parseFromRecord(rs, nextDef, loginableTableVar);
			else if(nextType == Parent.class && nextTypeId > 0)
				loginable = Parent.parseFromRecord(rs, nextDef, loginableTableVar);
			else if(nextType == Athlete.class && nextTypeId > 0)
				loginable = Athlete.parseFromRecord(rs, nextDef, loginableTableVar);
			else if(nextType == Donor.class && nextTypeId > 0)
				loginable = Donor.parseFromRecord(rs, nextDef, loginableTableVar);
			else if(nextType == Employee.class && nextTypeId > 0)
				loginable = Employee.parseFromRecord(rs, nextDef, loginableTableVar);
			
			try { loginable.setRole(LoginRole.parseFromRecord(rs)); } catch(Exception ex) {}
			
			if(loginable != null)
				break;
		}
		
		return loginable;
	}
	
	private static String[] generateColumnNames(String tableVar)
	{
		if(tableVar == null)
			throw new IllegalArgumentException("Bad table variable.");
		
		String[] colNames = new String[11];
		
		if(tableVar != null)
		{
			int columnIndex = 0;
			
			colNames[columnIndex] = tableVar + "_id"; columnIndex++;
			colNames[columnIndex] = tableVar + "_code"; columnIndex++;
			colNames[columnIndex] = tableVar + "_email"; columnIndex++;
			colNames[columnIndex] = tableVar + "_name"; columnIndex++;
			colNames[columnIndex] = tableVar + "_surname"; columnIndex++;
			colNames[columnIndex] = tableVar + "_gender"; columnIndex++;
			colNames[columnIndex] = tableVar + "_phone_number"; columnIndex++;
			colNames[columnIndex] = tableVar + "_hes_code"; columnIndex++;
			colNames[columnIndex] = tableVar + "_state"; columnIndex++;
			colNames[columnIndex] = tableVar + "_creation_time"; columnIndex++;
			colNames[columnIndex] = tableVar + "_last_modified_time"; columnIndex++;
		}
		
		return colNames;
	}
	
	public static String[] generateColumnNames(TypeDefinition typeDefinition, String loginableTableVar)
	{
		if(typeDefinition == null || loginableTableVar == null)
			throw new IllegalArgumentException("Bad table or subtype variables.");
		
		String[] typeCols = typeDefinition.getTypeColumns();
		String[] loginableCols = Loginable.generateColumnNames(loginableTableVar);
		
		String[] colNames = new String[loginableCols.length + typeCols.length];
		
		int columnIndex = 0;
		
		for(String nextCol : typeCols)
		{
			colNames[columnIndex] = nextCol; 
			columnIndex++;
		}
		
		for(String nextCol : loginableCols)
		{
			colNames[columnIndex] = nextCol; 
			columnIndex++;
		}
		
		return colNames;
	}
	
	public static String generateColumnNameString(TypeDefinition[] includedTypes, String loginableTableVar)
	{
		if(includedTypes == null || includedTypes.length == 0 || loginableTableVar == null)
			throw new IllegalArgumentException("Bad table or subtype variables.");
		
		String colNameStr = "";
		
		for(TypeDefinition nextDefinition : includedTypes)
		{
			for(String nextColStr : nextDefinition.getTypeColumns())
			{
				boolean isExtensionCol = nextDefinition.getExtensionTableVariables() != null && nextColStr.startsWith(nextDefinition.getExtensionTableVariables()[0] + "_");
				String nextTableVar = !isExtensionCol ? nextDefinition.getTableVariable() : nextDefinition.getExtensionTableVariables()[0];
				
				String orgColStr = nextColStr.substring(nextTableVar.length() + 1);
				colNameStr += ", " + nextTableVar + "." + orgColStr + " AS " + nextColStr;
			}
		}
		
		for(String nextLoginableColStr : Loginable.generateColumnNames(loginableTableVar))
		{
			String orgColStr = nextLoginableColStr.substring(nextLoginableColStr.indexOf("_") + 1);
			colNameStr += ", " + loginableTableVar + "." + orgColStr + " AS " + nextLoginableColStr;
		}
		
		colNameStr = colNameStr.substring(2);
		
		return colNameStr;
	}
	
	public static Class<? extends Loginable> getClassFromAnnotatedString(String tableName)
	{
		int i = -1;
		
		do
		{
			Class<? extends Loginable> nextClass = null;
			i++;
			
			if(i == 0)
				nextClass = ClubManager.class;
			else if(i == 1)
				nextClass = BranchManager.class;
			else if(i == 2)
				nextClass = Trainer.class;
			else if(i == 3)
				nextClass = Parent.class;
			else if(i == 4)
				nextClass = Athlete.class;
			
			if(nextClass.getAnnotation(XMLAndDatabaseValues.class).tableName().equalsIgnoreCase(tableName))
				return nextClass;
		} while(i <= 4);
		
		return null;
	}
	
	//İç Sınıflar
	public static class TypeDefinition
	{
		private Class<? extends Loginable> type = null;
		private String tableVariable = null;
		private boolean includeExtensions = false;
		private String[] extensionTableVariables = null;
		
		public TypeDefinition(Class<? extends Loginable> type, String tableVariable)
		{
			this.type = type;
			this.tableVariable = tableVariable;
		}
		
		public TypeDefinition(Class<? extends Loginable> type, String tableVariable, boolean includeExtensions, String[] extensionTableVariables)
		{
			this.type = type;
			this.tableVariable = tableVariable;
			this.includeExtensions = includeExtensions;
			this.extensionTableVariables = extensionTableVariables;
		}
		
		//Get-Set
		public Class<? extends Loginable> getType() 
		{
			return type;
		}

		public void setType(Class<? extends Loginable> type) 
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

		public boolean includeExtensions() 
		{
			return includeExtensions;
		}

		public void setIncludeExtensions(boolean includeExtensions) 
		{
			this.includeExtensions = includeExtensions;
		}

		public String[] getExtensionTableVariables() 
		{
			return extensionTableVariables;
		}

		public void setExtensionTableVariables(String[] extensionTableVariables) 
		{
			this.extensionTableVariables = extensionTableVariables;
		}
		
		public String[] getTypeColumns()
		{
			String[] typeCols = null;
			
			if(this.type == ClubManager.class) 
				return ClubManager.generateColumnNames(this.tableVariable, this.includeExtensions, this.extensionTableVariables != null ? this.extensionTableVariables[0] : null);
			else if(this.type == BranchManager.class)
				return BranchManager.generateColumnNames(this.tableVariable, this.includeExtensions, this.extensionTableVariables != null ? this.extensionTableVariables[0] : null);
			else if(this.type == Trainer.class)
				return Trainer.generateColumnNames(this.tableVariable, this.includeExtensions, this.extensionTableVariables != null ? this.extensionTableVariables : null);
			else if(this.type == Parent.class)
				return Parent.generateColumnNames(this.tableVariable, this.includeExtensions, this.extensionTableVariables != null ? this.extensionTableVariables[0] : null);
			else if(this.type == Athlete.class)
				return Athlete.generateColumnNames(this.tableVariable, this.includeExtensions, this.extensionTableVariables != null ? this.extensionTableVariables[0] : null);
			
			return typeCols;
		}
	}
	
	public class MedicalData implements IXmlObject, IDBObject
	{
		private int id = -1;
		private String pastTherapies = null;
		private String activeIssues = null;
		private String activeMedications = null;
		private String specialCareNeeds = null;
		
		private MedicalData() 
		{
			Loginable.this.medicals = this;
		}
		
		public MedicalData(int id) 
		{
			this.id = id;
			Loginable.this.medicals = this;
		}
		
		public MedicalData(int id, String pastTherapies, String activeIssues, String activeMedications, String specialCareNeeds)
		{
			this.id = id;
			this.pastTherapies = pastTherapies;
			this.activeIssues = activeIssues;
			this.activeMedications = activeMedications;
			this.specialCareNeeds = specialCareNeeds;
			
			Loginable.this.medicals = this;
		}
		
		//Xml Bölümü
		@Override
		public void parseFromXMLElement(Element userEl)
		{
			try
			{
				this.id = Integer.parseInt(userEl.getAttribute("id"));
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
				Element userEl = xml.createElement("medical_data");
				userEl.setAttribute("id", this.id + "");
				
				if(this.pastTherapies != null)
					userEl.setAttribute("past_therapies", this.pastTherapies);
				
				if(this.activeIssues != null)
					userEl.setAttribute("active_issues", this.activeIssues);
				
				if(this.activeMedications != null)
					userEl.setAttribute("active_medications", this.activeMedications);
				
				if(this.specialCareNeeds != null)
					userEl.setAttribute("special_care_needs", this.specialCareNeeds);
				
				return userEl;
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
	        
	        try
	        {
		        if(conn == null)
		        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
		        
				String insertQuery = ""
	    				+ "INSERT INTO "
	    				+ "		medical_data "
	    				+ "			(loginable, past_therapies, active_issues, active_medications, special_care_needs) "
	    				+ "VALUES "
	    				+ "		(?, ?, ?, ?, ?)";
	        	
	    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
	    		insertSt.setInt(1, Loginable.this.loginableId);
	    		insertSt.setObject(2, this.pastTherapies != null ? this.pastTherapies.trim() : null, Types.VARCHAR);
	    		insertSt.setObject(3, this.activeIssues != null ? this.activeIssues.trim() : null, Types.VARCHAR);
	    		insertSt.setObject(4, this.activeMedications != null ? this.activeMedications.trim() : null, Types.VARCHAR);
	    		insertSt.setObject(5, this.specialCareNeeds != null ? this.specialCareNeeds.trim() : null, Types.VARCHAR);
	    		
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
		public void updateInDB(Connection conn, IDBObject newObject) throws ClassNotFoundException, SQLException 
		{
			Connection newConn = conn;
	        PreparedStatement updateSt = null;
	        
	        this.id = -1;
	        
	        try
	        {
	        	MedicalData newData = (MedicalData) newObject;
	        	
		        if(conn == null)
		        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
		        
				String updateQuery = ""
	    				+ "UPDATE "
	    				+ "		medical_data "
	    				+ "SET "
	    				+ "		past_therapies = ?, active_issues = ?, active_medications = ?, special_care_needs = ? "
	    				+ "WHERE "
	    				+ "		loginable = ?";
	        	
	    		updateSt = newConn.prepareStatement(updateQuery);
	    		updateSt.setObject(1, newData.pastTherapies != null ? newData.pastTherapies.trim() : null, Types.VARCHAR);
	    		updateSt.setObject(2, newData.activeIssues != null ? newData.activeIssues.trim() : null, Types.VARCHAR);
	    		updateSt.setObject(3, newData.activeMedications != null ? newData.activeMedications.trim() : null, Types.VARCHAR);
	    		updateSt.setObject(4, newData.specialCareNeeds != null ? newData.specialCareNeeds.trim() : null, Types.VARCHAR);
	    		updateSt.setInt(5, Loginable.this.loginableId);
	    		
	    		if(updateSt.executeUpdate() != 1)
	    			throw new IllegalArgumentException();
	        	
	    		this.setPastTherapies(newData.pastTherapies);
	    		this.setActiveIssues(newData.activeIssues);
	    		this.setActiveMedications(newData.activeMedications);
	    		this.setSpecialCareNeeds(newData.specialCareNeeds);
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
	        PreparedStatement sqlSt = null;
	        
	        try
	        {
		        if(newConn == null)
		        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
		        
				String deleteQuery = ""
						+ "DELETE FROM medical_data WHERE loginable = ?";
		        
		        sqlSt = newConn.prepareStatement(deleteQuery);
		        sqlSt.setInt(1, Loginable.this.loginableId);
		        
		        if(sqlSt.executeUpdate() != 1)
		        	throw new IllegalArgumentException();
		        
		        this.id = -1;
		    }
	        finally
	        {
	        	DBUtils.close(sqlSt);
	        	
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
		
		public String getPastTherapies() 
		{
			return pastTherapies;
		}
		
		public void setPastTherapies(String pastTherapies) 
		{
			this.pastTherapies = pastTherapies;
		}
		
		public String getActiveIssues() 
		{
			return activeIssues;
		}
		
		public void setActiveIssues(String activeIssues) 
		{
			this.activeIssues = activeIssues;
		}
		
		public String getActiveMedications()
		{
			return activeMedications;
		}
		
		public void setActiveMedications(String activeMedications)
		{
			this.activeMedications = activeMedications;
		}
		
		public String getSpecialCareNeeds() 
		{
			return specialCareNeeds;
		}
		
		public void setSpecialCareNeeds(String specialCareNeeds) 
		{
			this.specialCareNeeds = specialCareNeeds;
		}
	}
}