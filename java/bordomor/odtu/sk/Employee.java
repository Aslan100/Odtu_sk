package bordomor.odtu.sk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import bordomor.odtu.sk.Params.Gender;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@XMLAndDatabaseValues(tableName = "employee", tagName = "employee", defaultVariable = "emp")
public class Employee extends Loginable
{
	private int id = -1;
	
	public Employee()
	{
		super();
	}
	
	public Employee(int id)
	{
		super();
		this.id = id;
	}
	
	public Employee(int id, int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state)
	{
		super(loginableId, code, email, name, surname, gender, phoneNumber, state);
		this.id = id;
	}
	
	public Employee(int id, int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state, 
			Timestamp creationTime, Timestamp lastModifiedTime)
	{
		super(loginableId, code, email, name, surname, gender, phoneNumber, state, creationTime, lastModifiedTime);
		this.id = id;
	}
	
	//Yardımcı Metodlar
	@Override
	public boolean equals(Object comparedObject) 
	{
		if(comparedObject == this)
			return true;
		else if(comparedObject == null || !(comparedObject instanceof Employee))
			return false;
		else
		{
			Employee comparedUser = (Employee) comparedObject;
			
			return (super.equals(comparedUser) && this.id == comparedUser.id);
		}
	}
	
	@Override
    public int hashCode() 
	{
        final int prime = 13;
        
        int result = super.hashCode();
        result = prime*result + this.id;
        
        return result;
    }
	
	@Override
	public Branch getPrimaryBranch()
	{
		return null;
	}
	
	@Override
	public boolean isLoginPermitted()
	{
		 return this.state == LoginableState.PENDING || this.state == LoginableState.ACTIVE;
	}
	
	@Override
	public boolean canOperateOn(Connection conn, Loginable user) throws ClassNotFoundException, SQLException
	{
		return false;
	}
	
	@Override
	public boolean canOperateOn(Connection conn, Team team) throws ClassNotFoundException, SQLException
	{
		return false;
	}
	
	@Override
	public boolean canOperateOn(Connection conn, Branch branch) 
	{
		return false;
	}
	
	@Override
	public String getHomeURI()
	{
		return Params.PORTAL_CLUB_MANAGER_HOME_URI;
	}
	
	@Override
	public String generatePortalLeftMenu() 
	{
		String menuCode = "";
		menuCode = "<div id=\"main_menu\" class=\"left_panel\">";
		menuCode += "<p class=\"menu_title_container\">İşlem Menüsü</p>";
		menuCode += "<ul>";
		
		/*
		if(this.getRole().canManipulateOnlineTrainings() || this.getRole().canManipulateExams() || this.getRole().canAssessExams())
		{
			if(this.getRole().canManipulateOnlineTrainings())
			{
				menuCode += "<li class=\"accordion_item\">";
				menuCode += "<button><p>Çevrimiçi Eğitimler</p></button>";
				menuCode += "<ul>";
				
				menuCode += "<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_ONLINE_TRAININGS_URI + "\"><p>Eğitim Tanımları</p></a></li>";
				menuCode += "<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_INSTRUCTOR_UPLOADS_URI + "\"><p>Eğitmen Yüklemeleri</p></a></li>";
				menuCode += "<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_CORPORATION_UPLOADS_URI + "\"><p>Kurum Yüklemeleri</p></a></li>";
				menuCode += "<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_SUBMISSIONS_URI + "\"><p>Onay Bekleyenler</p></a></li>";
				menuCode += "</ul></li>";
			}
			
			if(this.getRole().canManipulateExams())
				menuCode += "<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_EXAMS_URI + "\"><p>Sınav Tanımları</p></a></li>";
			
			if(this.getRole().canManipulateOnlineTrainings())
				menuCode += "<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_MODULES_URI + "\"><p>Eğitim Modülleri</p></a></li>";
		}
		
		if(this.getRole().canManipulatePurchases() || this.getRole().canManipulateOnlineTrainings())
		{
			menuCode += "<li class=\"accordion_item\">";
			menuCode += "<button><p>Veri ve İstatistik Merkezi</p></button>";
			menuCode += "<ul>";
			
			if(this.getRole().canManipulatePurchases())
				menuCode += "<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_PURCHASES_URI + "\"><p>Siparişler</p></a></li>";
			
			if(this.getRole().canManipulateOnlineTrainings())
				menuCode += "<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_COMPLETED_TRAININGS_URI + "\"><p>Tamamlanan Eğitimler</p></a></li>";
			
			menuCode += "</ul></li>";
		}
		
		menuCode += "</ul>";
		menuCode += "</div>";
		*/
		
		return menuCode;
	}
	
	@Override
	public String generatePortalTopBar() 
	{
		String barCode = "<div class=\"top_panel\">";
		barCode += "<button id=\"menu_button\" name=\"menu_button\">";
		barCode += "<img src=\"" + WebUtils.PORTAL_CONTEXT_PATH + "/visual/menu.png\"/>";
		barCode += "<span>MENÜ</span>";
		barCode += "</button>";
		
		/*
		barCode += "<a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + "/\"><img src=\"" + WebUtils.PORTAL_CONTEXT_PATH + "/visual/tuv_austria.png\"/></a>";
		barCode += "<p>TÜV AUSTRIA TURK<br>" + Params.SYSTEM_TITLE + "</p>";
		barCode += "<p class=\"user_info\">" + this.getFullName() + "</br><span>" + this.getRole().getTitle() + "</span></p>";
		barCode += "<button id=\"settings_button\" name=\"settings_button\">";
		barCode += "<img src=\"" + WebUtils.PORTAL_CONTEXT_PATH + "/visual/settings.png\"/>";
		barCode += "<span>AYARLAR</span>";
		barCode += "</button>";
		barCode += "<form action=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.LOGOUT_URI + "\" method=\"post\" autofill=\"false\">";
		barCode += "<button type=\"submit\" id=\"logout_button\"><img src=\"" + WebUtils.PORTAL_CONTEXT_PATH + "/visual/logout.png\"/></button>";
		barCode += "</form>";
		barCode += "<ul id=\"settings_menu\" class=\"account_settings_menu\">";
		barCode += "<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_ACCOUNT_UPDATE_URI + "\"><img src=\"" + WebUtils.PORTAL_CONTEXT_PATH + "/visual/user_form/user_name.png\"/><p>Hesap Bilgilerini Düzenle</p></a></li>";
		barCode += "<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_PWD_RESET_URI + "\"><img src=\"" + WebUtils.PORTAL_CONTEXT_PATH + "/visual/user_form/pwd.png\"/><p>Şifre Güncelle</p></a></li></ul>";
		barCode += "</div>";*/
		
		return barCode;
	}
	
	@Override
	public String generatePageMenu(String returnURI) 
	{
		String pageMenu = "";
		pageMenu += "<nav class=\"submenu\">";
		pageMenu += "	<div class=\"submenu_title\">";
		pageMenu += "		<p>SN. " + this.getFullName().toUpperCase(Params.DEFAULT_LOCALE) + "</p>";
		pageMenu += "	</div>";
		pageMenu += "	<ul>";
		pageMenu += "		<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + "/\">KONTROL PANELİ</a></li>";
		pageMenu += "		<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + "/\">AYARLAR</a></li>";
		pageMenu += "		<li>";
		pageMenu += "			<form action=\"" + WebUtils.PORTAL_CONTEXT_PATH + "/auth/logout.jsp\" method=\"post\">";
		pageMenu += "				<input type=\"hidden\" name=\"return_uri\" value=\"" + returnURI + "\"/>";
		pageMenu += "				<button type=\"submit\" class=\"dark\" style=\"width: 100%\">ÇIKIŞ YAP</button>";
		pageMenu += "			</form>";
		pageMenu += "		</li>";
		pageMenu += "	</ul>";
		pageMenu += "</nav>";
		
		return pageMenu;
	}
	
	//Xml Bölümü
	@Override
	public void parseFromXMLElement(Element userEl)
	{
		try
		{
			this.id = Integer.parseInt(userEl.getAttribute("id"));
			super.parseFromXMLElement(userEl);
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
			Element userEl = xml.createElement("system_user");
			userEl.setAttribute("id", this.id + "");
			
			
			super.appendLoginableAttributes(userEl, cascadeRelations);
			
			return userEl;
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
        this.loginableId = -1;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			this.state = LoginableState.PENDING;
			super.createInDB(newConn);
			
    		String insertQuery = ""
    				+ "INSERT INTO "
    				+ "		system_user "
    				+ "			(role, loginable_parent) "
    				+ "VALUES "
    				+ "		(?, ?)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		insertSt.setInt(1, this.getRole().getId());
    		insertSt.setInt(2, this.loginableId);
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
	public void updateInDB(Connection conn, IDBObject newUser) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement userStatement = null;
        ResultSet keySet = null;
        
        try
        {
	        Employee updatingUser = (Employee) newUser;
	        
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        if(!super.equals(updatingUser))
	        	super.updateInDB(newConn, updatingUser);
	        
	        String systemUserQuery = ""
	        		+ "UPDATE "
	        		+ "		system_user "
	        		+ "SET "
	        		+ "		role = ? "
	        		+ "WHERE "
	        		+ "		id = ?";
	        
	        userStatement = newConn.prepareStatement(systemUserQuery);
	        userStatement.setInt(1, updatingUser.getRole().getId());
	        userStatement.setInt(2, this.id);
	        
	        if(userStatement.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        this.setName(updatingUser.getName());
	        this.setSurname(updatingUser.getSurname());
	        this.setRole(updatingUser.getRole());
	    }
        catch(ClassCastException cex)
        {
        	throw new IllegalArgumentException();
        }
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(userStatement);
        	
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
	        
			String deleteQuery = ""
					+ "DELETE FROM "
					+ "		loginable "
					+ "WHERE "
					+ "		id = (SELECT loginable_parent FROM system_user WHERE id = ?)";
	        
	        sqlSt = newConn.prepareStatement(deleteQuery);
	        sqlSt.setInt(1, this.id);
	        
	        if(sqlSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        this.id = -1;
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
	
	@Override
	public void manipulateState(Connection conn, LoginableState newState) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement stateStatement = null;
        PreparedStatement deleteStatement = null;
        ResultSet keySet = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			String modificationQuery = "UPDATE loginable SET state = ?::loginable_state " +
					"WHERE id = (SELECT loginable_parent FROM system_user WHERE id = ?)";
	        
			stateStatement = newConn.prepareStatement(modificationQuery, 
					new String[] {"id", "last_modified_time"});
			stateStatement.setString(1, newState.name());
			stateStatement.setInt(2, this.id);
			
			if(stateStatement.executeUpdate() != 1)
				throw new IllegalArgumentException();
	        
			keySet = stateStatement.getGeneratedKeys();
			keySet.next();
			
			this.setLoginableId(keySet.getInt("id"));
        	this.setLastModifiedTime(keySet.getTimestamp("last_modified_time"));
        	this.setState(newState);
        	
        	if(this.state == LoginableState.SUSPENDED)
        	{
        		String deleteSessionsQuery = "DELETE FROM session_data " +
    					"WHERE loginable = (SELECT loginable_parent FROM system_user WHERE id = ?)";
    	        
        		deleteStatement = newConn.prepareStatement(deleteSessionsQuery);
        		deleteStatement.setInt(1, this.id);
        		deleteStatement.executeUpdate();
        	}
		}
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(stateStatement);
        	DBUtils.close(deleteStatement);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	@Override
	public void manipulatePwd(Connection conn, String newPwd, boolean activate) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement pwdStatement = null;
        ResultSet keySet = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        String modificationQuery = ""
	        		+ "UPDATE "
	        		+ "		loginable "
	        		+ "SET "
	        		+ "		pwd = ? "
	        		+ (activate ? ", state = ?::loginable_state " : "")
	        		+ "WHERE "
	        		+ "		id = (SELECT loginable_parent FROM system_user WHERE id = ?)";
	        
			pwdStatement = newConn.prepareStatement(modificationQuery, 
					new String[] {"id", "email", "state", "last_modified_time"});
			pwdStatement.setString(1, newPwd);
			
			if(activate)
				pwdStatement.setString(2, LoginableState.ACTIVE.toString());
			
			pwdStatement.setInt(activate ? 3 : 2, this.id);
			pwdStatement.executeUpdate();
	        
			super.setPwd(newPwd);
			keySet = pwdStatement.getGeneratedKeys();
			keySet.next();
			this.setEmail(keySet.getString("email"));
			this.setState(LoginableState.valueOf(keySet.getString("state")));
        	this.setLastModifiedTime(keySet.getTimestamp("last_modified_time"));
		}
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(pwdStatement);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	@Override
	public void fetchDocumentsFromDB(Connection conn) throws ClassNotFoundException, SQLException
	{
		throw new IllegalArgumentException("Incompatible object type for document fetch.");
	}
	
	//Get- Set
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	@Override
	public LoginRole getRole() 
	{
		return null;
	}
	
	@Override
	public void setRole(LoginRole role) 
	{
		throw new IllegalArgumentException("This user type cannot have a login role.");
	}
	
	@Override
	public bordomor.odtu.sk.template.Document[] getDocuments()
	{
		return null;
	}
	
	//Statik Sorgular
	public static Employee findById(Connection conn, int id) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Employee.class, "su");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + ", "
					+ "		lr.* "
					+ "FROM "
					+ "		loginable l, "
					+ "		system_user su "
					+ "			LEFT OUTER JOIN login_role lr "
					+ "				ON (su.role = lr.id) "
					+ "WHERE "
					+ "		su.loginable_parent = l.id "
					+ "		AND su.id = ?";
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, id);
	        rs = sqlSt.executeQuery();
	        
	        Employee systemUser = null;
	        
	        if(rs.next())
	        {
	        	systemUser = Employee.parseFromRecord(rs,Loginable.generateColumnNames(typeDef, "l"));
	        	systemUser.setRole(LoginRole.parseFromRecord(rs));
	        }
	        
			return systemUser;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Employee findByStok(Connection conn, String stok) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Employee.class, "su");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + ", "
					+ "		lr.* "
					+ "FROM "
					+ "		loginable l, session_data sd, "
					+ "		system_user su "
					+ "			LEFT OUTER JOIN login_role lr "
					+ "				ON (su.role = lr.id) "
					+ "WHERE "
					+ "		su.loginable_parent = l.id "
					+ "		AND sd.loginable = l.id "
					+ "		AND sd.token = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, stok);
	        rs = sqlSt.executeQuery();
	        
	        Employee systemUser = null;
	        
	        if(rs.next())
	        {
	        	systemUser = Employee.parseFromRecord(rs,Loginable.generateColumnNames(typeDef, "l"));
	        	systemUser.setRole(LoginRole.parseFromRecord(rs));
	        }
	        
			return systemUser;
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
 	public static Employee parseFromRecord(ResultSet rs)
	{
		try
		{
			Employee parsedUser = null;
			int userId = rs.getInt("id");  
			
			if(!rs.wasNull())
			{
				parsedUser = new Employee(userId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals("loginable_parent"))
						parsedUser.setLoginableId(rs.getInt(nextCol));
					else if(nextCol.equals("email"))
						parsedUser.setEmail(rs.getString(nextCol));
					else if(nextCol.equals("name"))
						parsedUser.setName(rs.getString(nextCol));
					else if(nextCol.equals("surname"))
						parsedUser.setSurname(rs.getString(nextCol));
					else if(nextCol.equals("state"))
						parsedUser.setState(LoginableState.valueOf(rs.getString(nextCol)));
				}
				
				parsedUser.parseTTAttributes(rs);
			}
				
			return parsedUser;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static Employee parseFromRecord(ResultSet rs, TypeDefinition typeDefinition, String loginableTableVar)
	{
		try
		{
			Class<? extends Loginable> type = typeDefinition.getType();
			String tableVar = typeDefinition.getTableVariable();
			
			if(type != Employee.class || tableVar == null)
				throw new Exception("Bad type definition.");
			
			Employee parsedUser = null;
			int userId = rs.getInt(tableVar + "_id");  
			
			if(!rs.wasNull())
			{
				parsedUser = new Employee(userId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals(loginableTableVar + "_id"))
						parsedUser.setLoginableId(rs.getInt(nextCol));
					else if(nextCol.equals(loginableTableVar + "_code"))
						parsedUser.setCode(rs.getString(nextCol));
					else if(nextCol.equals(loginableTableVar + "_email"))
						parsedUser.setEmail(rs.getString(nextCol));
					else if(nextCol.equals(loginableTableVar + "_name"))
						parsedUser.setName(rs.getString(nextCol));
					else if(nextCol.equals(loginableTableVar + "_surname"))
						parsedUser.setSurname(rs.getString(nextCol));
					else if(nextCol.equals(loginableTableVar + "_phone_number"))
						parsedUser.setPhoneNumber(rs.getString(nextCol));
					else if(nextCol.equals(loginableTableVar + "_state"))
						parsedUser.setState(LoginableState.valueOf(rs.getString(nextCol)));
				}
				
				parsedUser.parseTTAttributes(rs);
			}
				
			return parsedUser;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static Employee parseFromRecord(ResultSet rs, String[] columns)
	{
		try
		{
			Employee parsedUser = null;
			int userId = rs.getInt(columns[0]); 
			
			if(!rs.wasNull())
			{
				parsedUser = new Employee();
				parsedUser.setId(userId);
				parsedUser.setLoginableId(rs.getInt(columns[1]));
				parsedUser.setEmail(rs.getString(columns[2]));
				parsedUser.setName(rs.getString(columns[3]));
				parsedUser.setSurname(rs.getString(columns[4]));
				parsedUser.setState(LoginableState.valueOf(rs.getString(columns[5])));
				parsedUser.parseTTAttributes(rs, columns[6], columns[7]);
			}
				
			return parsedUser;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static String[] generateColumnNames(String tableVar, boolean includeExtensions, String extensionTableVar)
	{
		if(tableVar == null)
			throw new IllegalArgumentException("Bad table variable.");
		
		String[] colNames = new String[1];
		
		int columnIndex = 0;
		colNames[columnIndex] = tableVar + "_id"; columnIndex++;
		
		return colNames;
	}
}