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
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@XMLAndDatabaseValues(tableName = "club_manager", tagName = "club_manager", defaultVariable = "cm")
public class ClubManager extends Loginable
{
	private int id = -1;
	private LoginRole role = null;
	
	public ClubManager()
	{
		super();
	}
	
	public ClubManager(int id)
	{
		super();
		this.id = id;
	}
	
	public ClubManager(int id, LoginRole role, int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state)
	{
		super(loginableId, code, email, name, surname, gender, phoneNumber, state);
		this.id = id;
		this.setRole(role); 
	}
	
	public ClubManager(int id, LoginRole role, int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state, 
			Timestamp creationTime, Timestamp lastModifiedTime)
	{
		super(loginableId, code, email, name, surname, gender, phoneNumber, state, creationTime, lastModifiedTime);
		
		this.id = id;
		this.setRole(role);
	}
	
	//Yardımcı Metodlar
	@Override
	public boolean equals(Object comparedObject) 
	{
		if(comparedObject == this)
			return true;
		else if(comparedObject == null || !(comparedObject instanceof ClubManager))
			return false;
		else
		{
			ClubManager comparedUser = (ClubManager) comparedObject;
			
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
		 return this.state == LoginableState.PENDING || (this.state == LoginableState.ACTIVE && this.role != null && this.role.canDoSomethingOnWebPortal());
	}
	
	@Override
	public boolean canOperateOn(Connection conn, Loginable user) throws ClassNotFoundException, SQLException
	{
		try
		{
			if(this.state != LoginableState.ACTIVE)
				return false;
			
			return this.role != null && this.role.getUserMod().toString().charAt(1) == 'T';
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	
	@Override
	public boolean canOperateOn(Connection conn, Team team) throws ClassNotFoundException, SQLException
	{
		return true;
	}
	
	@Override
	public boolean canOperateOn(Connection conn, Branch branch)
	{
		return true;
	}
	
	@Override
	public String getHomeURI()
	{
		return Params.PORTAL_CLUB_MANAGER_HOME_URI;
	}
	
	@Override
	public String generatePortalLeftMenu() 
	{
		String menuCode = "<nav>";
		menuCode += "<img src=\"res/visual/logo.png\"/>";
 		menuCode += "<ul>";
 		menuCode += "<li><img src=\"res/visual/icon/navigation_panel/home.png\"/><a href=\"javascript:void(0)\">Ana Sayfa</a></li>";
 		menuCode += "<li><img src=\"res/visual/icon/navigation_panel/account.png\"/><button class=\"clear_style\">Kullanıcı Hesapları</button></li>";
 		menuCode += "<li><img src=\"res/visual/icon/navigation_panel/account.png\"/><a href=\"trainers.jsp\">Antrenörler</a></button></li>";
 		menuCode += "<li><img src=\"res/visual/icon/navigation_panel/team.png\"/><a href=\"teams.jsp\">Takımlar</a></li>";
 		menuCode += "<li><img src=\"res/visual/icon/navigation_panel/athlete.png\"/><a href=\"athletes.jsp\">Sporcular</a></li>";
 		menuCode += "<li><img src=\"res/visual/icon/navigation_panel/training_ground.png\"/><a href=\"locations.jsp\">Tesisler</a></li>";
 		menuCode += "<li><img src=\"res/visual/icon/navigation_panel/calendar.png\"/><a href=\"trainings.jsp\">Antrenman Programı</a></li>";
 		menuCode += "<li><img src=\"res/visual/icon/navigation_panel/calendar.png\"/><a href=\"events.jsp\">Etkinlik Takvimi</a></li>";
 		//menuCode += "<li><img src=\"res/visual/icon/navigation_panel/payment.png\"/><button class=\"clear_style\">Ödemeler</button></li>";
 		menuCode += "<li><img src=\"res/visual/icon/navigation_panel/payment.png\"/><a href=\"payment_schemas.jsp\">Ödeme Şemaları</a></li>";
 		menuCode += "<li><img src=\"res/visual/icon/navigation_panel/payment.png\"/><a href=\"promotions.jsp\">Promosyon Tanımları</a></li>";
 		menuCode += "</ul>";
 		menuCode += "</nav>";
 		
		return menuCode;
	}
	
	@Override
	public String generatePortalTopBar() 
	{
		String barCode = "<ul class=\"top_menu\">";
		barCode += "<li class=\"search_item\"><input type=\"text\" placeholder=\"Ara\"/></li>";
		barCode += "<li>";
		barCode += "<button class=\"clear_style\"><img src=\"res/visual/icon/top_panel/notification.png\"/></button>";
		barCode +="<ul class=\"placeholder\">";
		barCode += "<li>";
		barCode += "<img src=\"res/visual/bildirim.png\"/>";
		barCode += "<h2>Bildirimler</h2>";
		barCode += "<p>Takımınız, sporcularınız ve antrenmanlarınız ile ilgili güncellemeleri takip edebilirsiniz.</p>";
		barCode += "</li>";
		barCode += "</ul>";
		barCode += "</li>";
		barCode += "<li>";
		barCode += "<button class=\"clear_style\"><img src=\"res/visual/icon/top_panel/email.png\"/></button>";
		barCode += "<ul class=\"placeholder\">";
		barCode += "<li>";
		barCode += "<img src=\"res/visual/mesaj.png\"/>";
		barCode += "<h2>Mesajlar</h2>";
		barCode += "<p>Kulüp ve branş yöneticilerinizden gelen mesajları takip edebilirsiniz.</p>";
		barCode += "</li>";
		barCode += "</ul>";
		barCode += "</li>";
		barCode += "<li>";
		barCode += "<button class=\"clear_style\"><img src=\"res/visual/icon/top_panel/calendar.png\"/></button>";
		barCode += "<ul class=\"placeholder\">";
		barCode += "<li>";
		barCode += "<img src=\"res/visual/etkinlik.png\"/>";
		barCode += "<h2>Etkinlikler</h2>";
		barCode += "<p>Haftalık antrenman saatlerinizi ve etkinlik takviminizi takip edeblirsiniz.</p>";
		barCode += "</li>";
		barCode += "</ul>";
		barCode += "</li>";
		barCode += "<li>";
		barCode += "<button class=\"clear_style no_padding\"><img src=\"res/visual/icon/top_panel/account.png\"/></button>";
		barCode += "<ul>";
		barCode += "<li class=\"user_data\">";
		barCode += "<p>" + this.getName() + this.getSurname().toUpperCase(Params.DEFAULT_LOCALE) + "</p>";
		barCode += "<p>" + this.getEmail() + "</p>";
		barCode += "</li>";
		barCode += "<li><a href=\"javascript:void(0)\">Hesap Ayarlarım</a></li>";
		barCode += "<li><a href=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.PORTAL_BRANCH_SETTINGS_URI + "\">Branş Ayarları</a></li>";
		barCode += "<li><a href=\"javascript:void(0)\">Şifre Sıfırlama</a></li>";
		barCode += "<li><a href=\"javascript:void(0)\">Gizlilik Politikası</a></li>";
		barCode += "<li><form action=\"" + WebUtils.PORTAL_CONTEXT_PATH + Params.LOGOUT_URI + "\" method=\"post\"><button type=\"submit\" class=\"clear_style\">Çıkış Yap</button></form></li>";
		barCode += "</ul>";
		barCode += "</li>";
		barCode += "</ul>";
		
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
			this.role = LoginRole.parseFirstInstance(userEl);
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
			Element userEl = xml.createElement("club_manager");
			userEl.setAttribute("id", this.id + "");
			
			if(cascadeRelations && this.role != null)
				userEl.appendChild(this.role.makeXMLElement(xml, true));
			
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
    				+ "		club_manager "
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
	        ClubManager updatingUser = (ClubManager) newUser;
	        
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
	
	//Get-Set
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
		return role;
	}
	
	@Override
	public void setRole(LoginRole role) 
	{
		this.role = role;
	}
	
	@Override
	public bordomor.odtu.sk.template.Document[] getDocuments()
	{
		return null;
	}
	
	//Statik Sorgular
	public static ClubManager findById(Connection conn, int id) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(ClubManager.class, "cm");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + ", "
					+ "		lr.* "
					+ "FROM "
					+ "		loginable l, "
					+ "		club_manager cm "
					+ " 		LEFT OUTER JOIN login_role lr "
					+ "				ON (cm.role = lr.id) "
					+ "WHERE "
					+ "		cm.loginable_parent = l.id "
					+ "		AND cm.id = ?";
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, id);
	        rs = sqlSt.executeQuery();
	        
	        ClubManager manager = null;
	        
	        if(rs.next())
	        {
	        	manager = ClubManager.parseFromRecord(rs, typeDef, "l");
	        	manager.setRole(LoginRole.parseFromRecord(rs));
	        }
	        
			return manager;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static ClubManager findByStok(Connection conn, String stok) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(ClubManager.class, "cm");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + ", "
					+ "		lr.* "
					+ "FROM "
					+ "		loginable l, "
					+ "		session_data sd, "
					+ "		club_manager cm "
					+ " 		LEFT OUTER JOIN login_role lr "
					+ "				ON (cm.role = lr.id) "
					+ "WHERE "
					+ "		cm.loginable_parent = l.id "
					+ "		AND sd.loginable = l.id "
					+ "		AND sd.token = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, stok);
	        rs = sqlSt.executeQuery();
	        
	        ClubManager manager = null;
	        
	        if(rs.next())
	        {
	        	manager = ClubManager.parseFromRecord(rs, typeDef, "l");
	        	manager.setRole(LoginRole.parseFromRecord(rs));
	        }
	        
			return manager;
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
 	public static ClubManager parseFromRecord(ResultSet rs)
	{
		try
		{
			ClubManager parsedUser = null;
			int userId = rs.getInt("id");  
			
			if(!rs.wasNull())
			{
				parsedUser = new ClubManager(userId);
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
	
	public static ClubManager parseFromRecord(ResultSet rs, TypeDefinition typeDefinition, String loginableTableVar)
	{
		try
		{
			Class<? extends Loginable> type = typeDefinition.getType();
			String tableVar = typeDefinition.getTableVariable();
			
			if(type != ClubManager.class || tableVar == null)
				throw new Exception("Bad type definition.");
			
			ClubManager parsedUser = null;
			int userId = rs.getInt(tableVar + "_id");  
			
			if(!rs.wasNull())
			{
				parsedUser = new ClubManager(userId);
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
				
				parsedUser.parseTTAttributes(rs, loginableTableVar);
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