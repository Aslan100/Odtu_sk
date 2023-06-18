package bordomor.odtu.sk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import bordomor.odtu.sk.Params.Gender;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Params.ParenthoodTitle;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@XMLAndDatabaseValues(tableName = "parent", tagName = "parent", defaultVariable = "pnt")
public class Parent extends Loginable
{
	private int id = -1;
	private String idNo = null;
	private Location homeLocation = null;
	private String vehicleLicencePlate = null;
	private boolean hasChildren = false;
	
	private Athlete[] children = null;
	
	public Parent()
	{
		super();
	}
	
	public Parent(int id)
	{
		super();
		this.id = id;
	}
	
	public Parent(int id, int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state)
	{
		super(loginableId, code, email, name, surname, gender, phoneNumber, state);
		this.id = id;
	}
	
	public Parent(int id, int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state, 
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
		else if(comparedObject == null || !(comparedObject instanceof Parent))
			return false;
		else
		{
			Parent comparedUser = (Parent) comparedObject;
			
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
		 return this.state == LoginableState.PENDING || (this.state == LoginableState.ACTIVE);
	}
	
	@Override
	public boolean canOperateOn(Connection conn, Loginable user)
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
			Element userEl = xml.createElement("parent");
			userEl.setAttribute("id", this.id + "");
			
			if(this.idNo != null)
				userEl.setAttribute("id_no", this.idNo);
			
			if(this.vehicleLicencePlate != null)
				userEl.setAttribute("vehicle_licence_plate", this.vehicleLicencePlate);
			
			userEl.setAttribute("has_children", this.hasChildren + "");
			super.appendLoginableAttributes(userEl, cascadeRelations);
			
			if(cascadeRelations && this.homeLocation != null)
				userEl.appendChild(this.homeLocation.makeXMLElement(xml, true));
			
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
	        
	        if(this.homeLocation != null && this.homeLocation.getId() == -1)
	        	this.homeLocation.createInDB(newConn);
	        
			this.state = LoginableState.PENDING;
			super.createInDB(newConn);
			
    		String insertQuery = ""
    				+ "INSERT INTO "
    				+ "		parent "
    				+ "			(id_no, home_location, vehicle_licence_plate, loginable_parent) "
    				+ "VALUES "
    				+ "		(?, ?, ?, ?)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		insertSt.setString(1, this.idNo.trim());
    		insertSt.setInt(2, this.homeLocation.getId());
    		insertSt.setObject(3, this.vehicleLicencePlate != null ? this.vehicleLicencePlate.trim() : null, Types.VARCHAR);
    		insertSt.setInt(4, this.loginableId);
    		insertSt.executeUpdate();
        	
    		keySet = insertSt.getGeneratedKeys();
    		keySet.next();
    		this.setId(keySet.getInt("id"));
    	}
        catch(Exception ex)
        {
        	if(this.homeLocation.getId() > 0)
        	{
        		try { this.homeLocation.deleteFromDB(newConn);} catch(Exception ex1) {}
        	}
        	
        	if(this.loginableId > 0)
        	{
        		try { super.deleteFromDB(newConn);} catch(Exception ex2) {}
        	}
        }
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(insertSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	public void createParenthoodInDB(Connection conn, Athlete child, ParenthoodTitle title) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement insertSt = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        String insertQuery = ""
    				+ "INSERT INTO "
    				+ "		parenthood "
    				+ "			(parent, child, title) "
    				+ "VALUES "
    				+ "		(?, ?, ?::parenthood_title)";
        	
    		insertSt = newConn.prepareStatement(insertQuery);
    		insertSt.setInt(1, this.id);
    		insertSt.setInt(2, child.getId());
    		insertSt.setString(3, title.toString());
    		insertSt.executeUpdate();
    	}
        finally
        {
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
	        Parent updatingUser = (Parent) newUser;
	        
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
					+ "		id = (SELECT loginable_parent FROM parent WHERE id = ?)";
	        
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
	
	public String getIdNo()
	{
		return idNo;
	}
	
	public void setIdNo(String idNo)
	{
		this.idNo = idNo;
	}
	
	public String getVehicleLicencePlate()
	{
		return vehicleLicencePlate;
	}
	
	public Location getHomeLocation()
	{
		return homeLocation;
	}
	
	public void setHomeLocation(Location homeLocation)
	{
		this.homeLocation = homeLocation;
	}
	
	public void setVehicleLicencePlate(String vehicleLicencePlate)
	{
		this.vehicleLicencePlate = vehicleLicencePlate;
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
	
	public boolean hasChildren() 
	{
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) 
	{
		this.hasChildren = hasChildren;
	}

	public Athlete[] getChildren()
	{
		return children;
	}
	
	public void setChildren(Athlete[] children)
	{
		this.children = children;
	}
	
	@Override
	public bordomor.odtu.sk.template.Document[] getDocuments()
	{
		return null;
	}
	
	//Statik Sorgular	
	public static Parent[] findAll(Connection conn) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Parent.class, "pnt");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		loginable l, "
					+ "		parent pnt "
					+ "WHERE "
					+ "		pnt.loginable_parent = l.id";
			
			sqlSt = newConn.prepareStatement(query);
	        rs = sqlSt.executeQuery();
	        
	        Vector<Parent> parents = new Vector<Parent>(0);
	        
	        while(rs.next())
	        	parents.add(Parent.parseFromRecord(rs,Loginable.generateColumnNames(typeDef, "l")));
	        
	        if(parents.size() > 0)
	        	parents.trimToSize();
	        
			return parents.isEmpty() ? null : parents.toArray(new Parent[parents.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Parent findById(Connection conn, int id) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Parent.class, "pnt");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		loginable l, "
					+ "		parent pnt "
					+ "WHERE "
					+ "		pnt.loginable_parent = l.id "
					+ "		AND pnt.id = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, id);
	        rs = sqlSt.executeQuery();
	        
	        Parent parent = null;
	        
	        if(rs.next())
	        	parent = Parent.parseFromRecord(rs, typeDef, "l");
	        
			return parent;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Parent findByStok(Connection conn, String stok) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Parent.class, "pnt");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		loginable l, session_data sd, "
					+ "		parent pnt "
					+ "WHERE "
					+ "		pnt.loginable_parent = l.id "
					+ "		AND sd.loginable = l.id "
					+ "		AND sd.token = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, stok);
	        rs = sqlSt.executeQuery();
	        
	        Parent systemUser = null;
	        
	        if(rs.next())
	        {
	        	systemUser = Parent.parseFromRecord(rs,Loginable.generateColumnNames(typeDef, "l"));
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
	
	public static Parent findByCode(Connection conn, String code) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Parent.class, "pnt");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		loginable l, "
					+ "		parent pnt "
					+ "WHERE "
					+ "		pnt.loginable_parent = l.id "
					+ "		AND l.code = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, code);
	        rs = sqlSt.executeQuery();
	        
	        Parent parent = null;
	        
	        if(rs.next())
	        	parent = Parent.parseFromRecord(rs,Loginable.generateColumnNames(typeDef, "l"));
	        
			return parent;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Parent findByIdNo(Connection conn, String idNo) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Parent.class, "pnt");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + ","
					+ " 	(SELECT "
					+ "			count(phd.id) "
					+ "		FROM "
					+ "			parenthood phd, parent pnt2 "
					+ "		WHERE "
					+ "			phd.parent = pnt2.id "
					+ "			AND pnt2.id_no = ?) AS pnt_children_count "
					+ "FROM "
					+ "		loginable l, "
					+ "		parent pnt "
					+ "WHERE "
					+ "		pnt.loginable_parent = l.id "
					+ "		AND pnt.id_no = ?";
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setString(1, idNo);
			sqlSt.setString(2, idNo);
	        rs = sqlSt.executeQuery();
	        
	        Parent parent = null;
	        
	        if(rs.next())
	        	parent = Parent.parseFromRecord(rs, typeDef, "l");
	        
			return parent;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Parent findByEmail(Connection conn, String email) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Parent.class, "pnt");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		loginable l, "
					+ "		parent pnt "
					+ "WHERE "
					+ "		pnt.loginable_parent = l.id "
					+ "		AND l.email = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, email);
	        rs = sqlSt.executeQuery();
	        
	        Parent parent = null;
	        
	        if(rs.next())
	        	parent = Parent.parseFromRecord(rs,Loginable.generateColumnNames(typeDef, "l"));
	        
			return parent;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Vector<Object[]> findAll_GroupedByRoles(Connection conn) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlStatement = null;
		ResultSet rs = null;
		Vector<Object[]> groupedUsers = new Vector<Object[]>(0);
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = "" +
					"SELECT "
					+ "		su.id AS user_id, su.name, su.surname, su.role, "
					+ "		l.id AS loginable_id, l.email, l.state, "
					+ " 	l.creation_time AS log_creation, l.last_modified_time AS log_modified, "
					+ "		lr.id AS login_role_id, lr.* "
					+ "FROM "
					+ "		system_user su "
					+ "			LEFT OUTER JOIN login_role lr "
					+ "				ON (su.role = lr.id), loginable l "
					+ "WHERE "
					+ "		su.loginable_parent = l.id "
					+ "ORDER BY "
					+ "		lr.id asc, su.name asc, su.surname asc";
			sqlStatement = newConn.prepareStatement(query);
	        rs = sqlStatement.executeQuery();
	        
	        LoginRole lastRole = null;
	        Vector<Parent> usersOfRole = null;
	        
	        String[] roleColumnSet = new String[]{"id", "title", "is_embedded_role", 
        			"can_manipulate_users_and_roles", "can_manipulate_locations", "can_manipulate_types", 
        			"can_manipulate_training_programs", "can_manipulate_certificates", "can_manipulate_web_news", 
        			"can_manipulate_files", "can_manipulate_onlie_trainings", "can_manipulate_exams", 
        			"can_assess_exams", "can_manipulate_purchases", "can_manipulate_other_web_content", 
        			"creation_time", "last_modified_time"};
        	 
	        String[] userColumnSet = new String[]{"user_id", "name", "surname", 
    				"loginable_id", "email", "state", "log_creation", "log_modified"};
        	
	        while(rs.next())
	        {
	        	int nextRoleId = rs.getInt("id");
	        	int lastRoleId = lastRole == null ? 0 : lastRole.getId();
	        	
	        	if(rs.isFirst())
	        	{
	        		usersOfRole = new Vector<Parent>(0);
	        		lastRole = LoginRole.parseFromRecord(rs, roleColumnSet);
	        	}
	        	else if(nextRoleId != lastRoleId)
	        	{
	        		usersOfRole.trimToSize();
	        		groupedUsers.add(new Object[] {lastRole, usersOfRole});
	        		usersOfRole = new Vector<Parent>(0);
	        		lastRole = LoginRole.parseFromRecord(rs, roleColumnSet);
	        	}
	        	
        		Parent nextUser = parseFromRecord(rs, userColumnSet);
        		usersOfRole.add(nextUser);
        	}
	        
	        if(usersOfRole != null && !usersOfRole.isEmpty())
	        {
	        	usersOfRole.trimToSize();
	        	groupedUsers.add(new Object[] {lastRole, usersOfRole});
	        }
	        
	        if(groupedUsers.isEmpty()) 
	        	return null;
	        
	        groupedUsers.trimToSize();
	        return groupedUsers;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlStatement);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	//Statik Metodlar
 	public static Parent parseFromRecord(ResultSet rs)
	{
		try
		{
			Parent parsedUser = null;
			int userId = rs.getInt("id");  
			
			if(!rs.wasNull())
			{
				parsedUser = new Parent(userId);
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
	
	public static Parent parseFromRecord(ResultSet rs, TypeDefinition typeDefinition, String loginableTableVar)
	{
		try
		{
			Class<? extends Loginable> type = typeDefinition.getType();
			String tableVar = typeDefinition.getTableVariable();
			
			if(type != Parent.class || tableVar == null)
				throw new Exception("Bad type definition.");
			
			Parent parsedUser = null;
			int userId = rs.getInt(tableVar + "_id");  
			
			if(!rs.wasNull())
			{
				parsedUser = new Parent(userId);
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
					else if(nextCol.equals(tableVar + "_id_no"))
						parsedUser.setIdNo(rs.getString(nextCol));
					else if(nextCol.equals(tableVar + "_home_location") && rs.getInt(nextCol) > 0)
						parsedUser.setHomeLocation(Location.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(tableVar + "_vehicle_licence_plate"))
						parsedUser.setVehicleLicencePlate(rs.getString(nextCol));
					else if(nextCol.equals(tableVar + "_children_count"))
						parsedUser.setHasChildren(rs.getInt(nextCol) > 0);
					
				}
				
				parsedUser.parseTTAttributes(rs, "l");
			}
				
			return parsedUser;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static Parent parseFromRecord(ResultSet rs, String[] columns)
	{
		try
		{
			Parent parsedUser = null;
			int userId = rs.getInt(columns[0]); 
			
			if(!rs.wasNull())
			{
				parsedUser = new Parent();
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
		
		String[] colNames = new String[4];
		
		int columnIndex = 0;
		colNames[columnIndex] = tableVar + "_id"; columnIndex++;
		colNames[columnIndex] = tableVar + "_id_no"; columnIndex++;
		colNames[columnIndex] = tableVar + "_home_location"; columnIndex++;
		colNames[columnIndex] = tableVar + "_vehicle_licence_plate"; columnIndex++;
		
		return colNames;
	}
}