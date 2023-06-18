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
import bordomor.odtu.sk.Params.BloodType;
import bordomor.odtu.sk.Params.Gender;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Params.TrainerLabel;
import bordomor.odtu.sk.location.City;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@XMLAndDatabaseValues(tableName = "trainer", tagName = "trainer", defaultVariable = "trn")
public class Trainer extends Loginable
{
	private int id = -1;
	private Branch primaryBranch =	null;
	private TrainerLabel label = null;
	private short level = (short) -1;
	
	private int height = -1;
	private float weight = -1f;
	private BloodType bloodType = null;
	
	private String idNo = null;
	private Timestamp birthDate = null;
	private City placeOfBirth = null;
	private Location homeLocation = null;
	private String mothersName = null;
	private String fathersName = null;
	
	private LoginRole role = null;
	protected bordomor.odtu.sk.template.Document[] documents = null;
	private Team[] teams = null;
	
	public Trainer()
	{
		super();
	}
	
	public Trainer(int id)
	{
		super();
		this.id = id;
	}
	
	public Trainer(int id, LoginRole role, int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state)
	{
		super(loginableId, code, email, name, surname, gender, phoneNumber, state);
		this.id = id;
		this.setRole(role);
	}
	
	public Trainer(int id, LoginRole role, int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state, 
			Timestamp creationTime, Timestamp lastModifiedTime)
	{
		super(loginableId, code, email, name, surname, gender, phoneNumber, state, creationTime, lastModifiedTime);
		this.id = id;
		this.setRole(role);
	}
	
	public Trainer(int id, Branch primaryBranch, TrainerLabel label, short level, int height, float weight, BloodType bloodType,  
			String idNo, Timestamp birthDate, City placeOfBirth, Location homeLocation, String mothersName, String fathersName, LoginRole role, 
			int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state)
	{
		super(loginableId, code, email, name, surname, gender, phoneNumber, state);
		this.id = id;
		this.primaryBranch = primaryBranch;
		this.label = label;
		this.level = level;
		this.height = height;
		this.weight = weight;
		this.bloodType = bloodType;
		this.idNo = idNo;
		this.birthDate = birthDate;
		this.placeOfBirth = placeOfBirth;
		this.homeLocation = homeLocation;
		this.mothersName = mothersName;
		this.fathersName = fathersName;
		this.setRole(role);
	}
	
	public Trainer(int id, Branch primaryBranch, TrainerLabel label, short level, int height, float weight, BloodType bloodType,  
			String idNo, Timestamp birthDate, City placeOfBirth, Location homeLocation, String mothersName, String fathersName, LoginRole role, 
			int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state, 
			Timestamp creationTime, Timestamp lastModifiedTime)
	{
		this(id, primaryBranch, label, level, height, weight, bloodType, idNo, birthDate, placeOfBirth, homeLocation, mothersName, fathersName, role, 
				loginableId, code, email, name, surname, gender, phoneNumber, state);
		super.creationTime = creationTime;
		super.lastModifiedTime = lastModifiedTime;
	}
	
	//Yardımcı Metodlar
	@Override
	public boolean equals(Object comparedObject) 
	{
		if(comparedObject == this)
			return true;
		else if(comparedObject == null || !(comparedObject instanceof Trainer))
			return false;
		else
		{
			Trainer comparedUser = (Trainer) comparedObject;
			
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
	public boolean isLoginPermitted()
	{
		 return this.state == LoginableState.PENDING || (this.state == LoginableState.ACTIVE && this.role != null && this.role.canDoSomethingOnWebPortal());
	}
	
	@Override
	public boolean canOperateOn(Connection conn, Loginable user) throws ClassNotFoundException, SQLException
	{
		if(this.state != LoginableState.ACTIVE)
			return false;
		
		return this.role != null && this.role.getUserMod().toString().charAt(1) == 'T';
	}
	
	@Override
	public boolean canOperateOn(Connection conn, Team team) throws ClassNotFoundException, SQLException
	{
		return true;
	}
	
	@Override
	public boolean canOperateOn(Connection conn, Branch branch)
	{
		return this.primaryBranch != null && this.primaryBranch == branch;
	}
	
	@Override
	public String getHomeURI()
	{
		return Params.PORTAL_TRAINER_HOME_URI;
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
			Element userEl = xml.createElement("trainer");
			userEl.setAttribute("id", this.id + "");
			
			if(this.label != null)
				userEl.setAttribute("label", this.label.toString());
			
			userEl.setAttribute("level", this.level + "");
			
			userEl.setAttribute("height", this.height + "");
			userEl.setAttribute("weight", this.weight + "");
			
			if(this.bloodType != null)
				userEl.setAttribute("blood_type", this.bloodType.toString());
			
			if(this.idNo != null)
				userEl.setAttribute("id_no", this.idNo.trim());
			
			if(this.birthDate != null)
				userEl.setAttribute("birth_date", Params.DATE_FORMAT.format(this.birthDate));
			
			if(this.mothersName != null)
				userEl.setAttribute("mothers_name", this.mothersName);
			
			if(this.fathersName != null)
				userEl.setAttribute("fathers_name", this.fathersName);
			
			if(cascadeRelations)
			{
				if(this.primaryBranch != null)
					userEl.appendChild(this.primaryBranch.makeXMLElement(xml, false));
				
				if(this.placeOfBirth != null)
					userEl.appendChild(this.placeOfBirth.makeXMLElement(xml, true));
				
				if(this.homeLocation != null)
					userEl.appendChild(this.homeLocation.makeXMLElement(xml, true));
			}
			
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
	        
	        if(this.homeLocation != null && this.homeLocation.getId() == -1)
	        	this.homeLocation.createInDB(newConn);
	        
			this.state = LoginableState.PENDING;
			super.createInDB(newConn);
			
    		String insertQuery = ""
    				+ "INSERT INTO "
    				+ "		trainer "
    				+ "			(primary_branch, label, level, height, weight, blood_type, "
    				+ "				id_no, birth_date, place_of_birth, home_location, mothers_name, fathers_name, role, loginable_parent) "
    				+ "VALUES "
    				+ "		(?::branch, ?::trainer_label, ?, ?, ?, ?::blood_type, ?, ?, ?, ?, ?, ?, ?, ?)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		insertSt.setString(1, this.primaryBranch.toString());
    		insertSt.setString(2, this.label.toString());
    		insertSt.setShort(3, this.level);
    		insertSt.setObject(4, this.height > 0 ? this.height : null, Types.INTEGER);
    		insertSt.setObject(5, this.weight > 0 ? this.weight : null, Types.FLOAT);
    		insertSt.setString(6, this.bloodType != null ? this.bloodType.toString() : BloodType.A_RH_POS.toString());
    		insertSt.setObject(7, this.idNo != null ? this.idNo.trim() : null, Types.VARCHAR);
    		insertSt.setTimestamp(8, this.birthDate);
    		insertSt.setObject(9, this.placeOfBirth != null ? this.placeOfBirth.getId() : null, Types.INTEGER);
    		insertSt.setObject(10, (this.homeLocation != null && this.homeLocation.getId() > 0) ? this.homeLocation.getId() : null, Types.INTEGER);
    		insertSt.setObject(11, this.mothersName != null ? this.mothersName.trim() : null, Types.VARCHAR);
    		insertSt.setObject(12, this.fathersName != null ? this.fathersName.trim() : null, Types.VARCHAR);
    		insertSt.setObject(13, this.role != null ? this.role.getId() : null, Types.INTEGER);
    		insertSt.setInt(14, this.loginableId);
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
		
	@Override
	public void updateInDB(Connection conn, IDBObject updatingUser) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement updateSt = null;
        ResultSet keySet = null;
        
        try
        {
        	if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
        	Trainer updatingTrainer = (Trainer) updatingUser;
        	
        	if(updatingTrainer.homeLocation != null)
        		this.homeLocation.updateInDB(newConn, updatingTrainer.homeLocation);
        	else
        		this.homeLocation.deleteFromDB(newConn);
        	
	        super.updateInDB(newConn, updatingTrainer);
			
    		String insertQuery = ""
    				+ "UPDATE "
    				+ "		trainer "
    				+ "SET "
    				+ "		primary_branch = ?::branch, label = ?::trainer_label, level = ?, height = ? , weight = ?, blood_type = ?::blood_type, "
    				+ "		id_no = ?, birth_date = ?, place_of_birth = ?, mothers_name = ?, fathers_name = ? "
    				+ "WHERE "
    				+ "		id = ?";
    		
    		updateSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		updateSt.setString(1, updatingTrainer.primaryBranch.toString());
    		updateSt.setString(2, updatingTrainer.label.toString());
    		updateSt.setShort(3, updatingTrainer.level);
    		updateSt.setObject(4, updatingTrainer.height > 0 ? updatingTrainer.height : null, Types.INTEGER);
    		updateSt.setObject(5, updatingTrainer.weight > 0 ? updatingTrainer.weight : null, Types.FLOAT);
    		updateSt.setString(6, updatingTrainer.bloodType.toString());
    		updateSt.setObject(7, updatingTrainer.idNo != null ? updatingTrainer.idNo.trim() : null, Types.VARCHAR);
    		updateSt.setTimestamp(8, updatingTrainer.birthDate);
    		updateSt.setObject(9, updatingTrainer.placeOfBirth != null ? updatingTrainer.placeOfBirth.getId() : null, Types.INTEGER);
    		updateSt.setObject(10, updatingTrainer.mothersName != null ? updatingTrainer.mothersName.trim() : null, Types.VARCHAR);
    		updateSt.setObject(11, updatingTrainer.fathersName != null ? updatingTrainer.fathersName.trim() : null, Types.VARCHAR);
    		updateSt.setInt(12, this.id);
    		
    		if(updateSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException(); 
        	
    		keySet = updateSt.getGeneratedKeys();
    		keySet.next();
        	this.setId(keySet.getInt("id"));
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
	        
			String deleteQuery = ""
					+ "DELETE FROM "
					+ "		loginable "
					+ "WHERE "
					+ "		id = (SELECT loginable_parent FROM trainer WHERE id = ?)";
	        
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
		this.documents = bordomor.odtu.sk.template.Document.findByOwner(conn, this);
	}
	
	//Get- Set
	@Override
	public int getId() 
	{
		return id;
	}
	
	public void setId(int id) 
	{
		this.id = id;
	}
	
	@Override
	public Branch getPrimaryBranch() 
	{
		return primaryBranch;
	}

	public void setPrimaryBranch(Branch primaryBranch) 
	{
		this.primaryBranch = primaryBranch;
	}
	
	public TrainerLabel getLabel() 
	{
		return label;
	}
	
	public void setLabel(TrainerLabel label) 
	{
		this.label = label;
	}

	public short getLevel() 
	{
		return level;
	}
	
	public void setLevel(short level) 
	{
		this.level = level;
	}
	
	public int getHeight() 
	{
		return height;
	}
	
	public void setHeight(int height) 
	{
		this.height = height;
	}
	
	public float getWeight() 
	{
		return weight;
	}
	
	public void setWeight(float weight) 
	{
		this.weight = weight;
	}
	
	public BloodType getBloodType() 
	{
		return bloodType;
	}
	
	public void setBloodType(BloodType bloodType) 
	{
		this.bloodType = bloodType;
	}
	
	public String getIdNo() 
	{
		return idNo;
	}
	
	public void setIdNo(String idNo) 
	{
		this.idNo = idNo;
	}
	
	public Timestamp getBirthDate() 
	{
		return birthDate;
	}
	
	public void setBirthDate(Timestamp birthDate) 
	{
		this.birthDate = birthDate;
	}
	
	public City getPlaceOfBirth() 
	{
		return placeOfBirth;
	}
	
	public void setPlaceOfBirth(City placeOfBirth) 
	{
		this.placeOfBirth = placeOfBirth;
	}
	
	public Location getHomeLocation()
	{
		return homeLocation;
	}
	
	public void setHomeLocation(Location homeLocation)
	{
		this.homeLocation = homeLocation;
	}
	
	public String getMothersName() 
	{
		return mothersName;
	}
	
	public void setMothersName(String mothersName) 
	{
		this.mothersName = mothersName;
	}
	
	public String getFathersName() 
	{
		return fathersName;
	}
	
	public void setFathersName(String fathersName) 
	{
		this.fathersName = fathersName;
	}
	
	@Override
	public LoginRole getRole() 
	{
		return role;
	}
	
	@Override
	public void setRole(LoginRole role) 
	{
		if(role == null || (role.getUserMod().isUnauthorized() && role.getRoleMod().isUnauthorized() && role.getBranchMod().isUnauthorized() && !role.getTeamMod().canWrite()))
			this.role = role;
		else
			throw new IllegalArgumentException("Bad role properties.");
	}
	
	@Override
	public bordomor.odtu.sk.template.Document[] getDocuments()
	{
		return documents;
	}
	
	public Team[] getTeams() 
	{
		return teams;
	}
	
	public void setTeams(Team[] teams) 
	{
		this.teams = teams;
	}
	
	//Statik Sorgular
	public static Trainer[] findAll(Connection conn) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Trainer.class, "trn");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + ", "
					+ "		lr.* "
					+ "FROM "
					+ "		loginable l, "
					+ "		trainer trn "
					+ "			LEFT OUTER JOIN login_role lr "
					+ "				ON (trn.role = lr.id) "
					+ "WHERE "
					+ "		trn.loginable_parent = l.id ";
					
			sqlSt = newConn.prepareStatement(query);
	        rs = sqlSt.executeQuery();
	        
	        Vector<Trainer> trainers = new Vector<Trainer>(0);
	        
	        while(rs.next())
	        {
	        	Trainer nextTrainer = Trainer.parseFromRecord(rs, typeDef, "l");
	        	
	        	if(rs.getInt("id") > 0)
	        		nextTrainer.setRole(LoginRole.parseFromRecord(rs));
	        	
	        	trainers.add(nextTrainer);
	        }
	        
	        if(trainers.size() > 0)
	        	trainers.trimToSize();
	        
			return trainers.isEmpty() ? null : trainers.toArray(new Trainer[trainers.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Trainer[] findByBranch(Connection conn, Branch branch) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Trainer.class, "trn");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + ", "
					+ "		lr.* "
					+ "FROM "
					+ "		loginable l, "
					+ "		trainer trn "
					+ "			LEFT OUTER JOIN login_role lr "
					+ "				ON (trn.role = lr.id) "
					+ "WHERE "
					+ "		trn.loginable_parent = l.id "
					+ "		AND trn.primary_branch = ?::branch "
					+ "		AND l.state = ?::loginable_state "
					+ "ORDER BY "
					+ "		l.name ASC, l.surname ASC";
					
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setString(1, branch.toString());
			sqlSt.setString(2, LoginableState.ACTIVE.toString());
	        rs = sqlSt.executeQuery();
	        
	        Vector<Trainer> trainers = new Vector<Trainer>(0);
	        
	        while(rs.next())
	        {
	        	Trainer nextTrainer = Trainer.parseFromRecord(rs, typeDef, "l");
	        	
	        	if(rs.getInt("id") > 0)
	        		nextTrainer.setRole(LoginRole.parseFromRecord(rs));
	        	
	        	trainers.add(nextTrainer);
	        }
	        
	        if(trainers.size() > 0)
	        	trainers.trimToSize();
	        
			return trainers.isEmpty() ? null : trainers.toArray(new Trainer[trainers.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Trainer findById(Connection conn, int id) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Trainer.class, "trn");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + ", "
					+ "		lr.* "
					+ "FROM "
					+ "		loginable l, "
					+ "		trainer trn "
					+ "			LEFT OUTER JOIN login_role lr "
					+ "				ON (trn.role = lr.id) "
					+ "WHERE "
					+ "		trn.loginable_parent = l.id "
					+ "		AND trn.id = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, id);
	        rs = sqlSt.executeQuery();
	        
	        Trainer trainer = null;
	        
	        if(rs.next())
	        {
	        	trainer = Trainer.parseFromRecord(rs, typeDef, "l");
	        	
	        	if(rs.getInt("id") > 0)
	        		trainer.setRole(LoginRole.parseFromRecord(rs));
	        }
	        
			return trainer;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Trainer findByCode(Connection conn, String code) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Trainer.class, "trn");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + ", "
					+ "		lr.* "
					+ "FROM "
					+ "		loginable l, "
					+ "		trainer trn "
					+ "			LEFT OUTER JOIN login_role lr "
					+ "				ON (trn.role = lr.id) "
					+ "WHERE "
					+ "		trn.loginable_parent = l.id "
					+ "		AND l.code = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, code);
	        rs = sqlSt.executeQuery();
	        
	        Trainer trainer = null;
	        
	        if(rs.next())
	        {
	        	trainer = Trainer.parseFromRecord(rs, typeDef, "l");
	        	
	        	if(rs.getInt("id") > 0)
	        		trainer.setRole(LoginRole.parseFromRecord(rs));
	        }
	        
			return trainer;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Trainer findByStok(Connection conn, String stok) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Trainer.class, "trn");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + ", "
					+ "		lr.* "
					+ "FROM "
					+ "		loginable l "
					+ "			LEFT OUTER JOIN login_role lr "
					+ "				ON (l.role = lr.id), "
					+ "		session_data sd, "
					+ "		trainer trn "
					+ "WHERE "
					+ "		trn.loginable_parent = l.id "
					+ "		AND sd.loginable = l.id "
					+ "		AND sd.token = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, stok);
	        rs = sqlSt.executeQuery();
	        
	        Trainer trainer = null;
	        
	        if(rs.next())
	        {
	        	trainer = Trainer.parseFromRecord(rs, typeDef, "l");
	        	trainer.setRole(LoginRole.parseFromRecord(rs));
	        }
	        
			return trainer;
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
 	public static Trainer parseFromRecord(ResultSet rs)
	{
		try
		{
			Trainer parsedUser = null;
			int userId = rs.getInt("id");  
			
			if(!rs.wasNull())
			{
				parsedUser = new Trainer(userId);
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
					else if(nextCol.equals("gender"))
						parsedUser.setGender(Gender.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals("phone_number"))
						parsedUser.setPhoneNumber(rs.getString(nextCol));
					else if(nextCol.equals("hes_code"))
						parsedUser.setHesCode(rs.getString(nextCol));
					else if(nextCol.equals("state"))
						parsedUser.setState(LoginableState.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals("primary_branch"))
						parsedUser.setPrimaryBranch(Branch.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("label"))
						parsedUser.setLabel(TrainerLabel.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals("level"))
						parsedUser.setLevel(rs.getShort(nextCol));
					else if(nextCol.equals("height"))
						parsedUser.setHeight(rs.getInt(nextCol));
					else if(nextCol.equals("weight"))
						parsedUser.setWeight(rs.getFloat(nextCol));
					else if(nextCol.equals("blood_type"))
						parsedUser.setBloodType(BloodType.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals("id_no"))
						parsedUser.setIdNo(rs.getString(nextCol));
					else if(nextCol.equals("birth_date"))
						parsedUser.setBirthDate(rs.getTimestamp(nextCol));
					else if(nextCol.equals("place_of_birth"))
						parsedUser.setPlaceOfBirth(City.findById(rs.getStatement().getConnection(), rs.getInt(nextCol), false));
					else if(nextCol.equals("mothers_name"))
						parsedUser.setMothersName(rs.getString(nextCol));
					else if(nextCol.equals("fathers_name"))
						parsedUser.setFathersName(rs.getString(nextCol));
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
	
	public static Trainer parseFromRecord(ResultSet rs, TypeDefinition typeDefinition, String loginableTableVar)
	{
		try
		{
			Class<? extends Loginable> type = typeDefinition.getType();
			String tableVar = typeDefinition.getTableVariable();
			
			if(type != Trainer.class || tableVar == null)
				throw new Exception("Bad type definition.");
			
			Trainer parsedUser = null;
			int userId = rs.getInt(tableVar + "_id");  
			
			if(!rs.wasNull())
			{
				parsedUser = new Trainer(userId);
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
					else if(nextCol.equals(loginableTableVar + "_gender"))
						parsedUser.setGender(Gender.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals(loginableTableVar + "_phone_number"))
						parsedUser.setPhoneNumber(rs.getString(nextCol));
					else if(nextCol.equals(loginableTableVar + "_hes_code"))
						parsedUser.setHesCode(rs.getString(nextCol));
					else if(nextCol.equals(loginableTableVar + "_state"))
						parsedUser.setState(LoginableState.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals(tableVar + "_primary_branch"))
						parsedUser.setPrimaryBranch(Branch.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(tableVar + "_label"))
						parsedUser.setLabel(TrainerLabel.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals(tableVar + "_level"))
						parsedUser.setLevel(rs.getShort(nextCol));
					else if(nextCol.equals(tableVar + "_height"))
						parsedUser.setHeight(rs.getInt(nextCol));
					else if(nextCol.equals(tableVar + "_weight"))
						parsedUser.setWeight(rs.getFloat(nextCol));
					else if(nextCol.equals(tableVar + "_blood_type"))
						parsedUser.setBloodType(BloodType.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals(tableVar + "_id_no"))
						parsedUser.setIdNo(rs.getString(nextCol));
					else if(nextCol.equals(tableVar + "_birth_date"))
						parsedUser.setBirthDate(rs.getTimestamp(nextCol));
					else if(nextCol.equals(tableVar + "_place_of_birth"))
						parsedUser.setPlaceOfBirth(City.findById(rs.getStatement().getConnection(), rs.getInt(nextCol), false));
					else if(nextCol.equals(tableVar + "_home_location"))
						parsedUser.setHomeLocation(Location.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(tableVar + "_mothers_name"))
						parsedUser.setMothersName(rs.getString(nextCol));
					else if(nextCol.equals(tableVar + "_fathers_name"))
						parsedUser.setFathersName(rs.getString(nextCol));
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
	
	public static String[] generateColumnNames(String tableVar, boolean includeExtensions, String[] extensionTableVar)
	{
		if(tableVar == null)
			throw new IllegalArgumentException("Bad table variable.");
		
		String[] colNames = new String[13];
		
		int columnIndex = 0;
		colNames[columnIndex] = tableVar + "_id"; columnIndex++;
		colNames[columnIndex] = tableVar + "_primary_branch"; columnIndex++;
		colNames[columnIndex] = tableVar + "_label"; columnIndex++;
		colNames[columnIndex] = tableVar + "_level"; columnIndex++;
		
		colNames[columnIndex] = tableVar + "_height"; columnIndex++;
		colNames[columnIndex] = tableVar + "_weight"; columnIndex++;
		colNames[columnIndex] = tableVar + "_blood_type"; columnIndex++;
		
		colNames[columnIndex] = tableVar + "_id_no"; columnIndex++;
		colNames[columnIndex] = tableVar + "_birth_date"; columnIndex++;
		colNames[columnIndex] = tableVar + "_place_of_birth"; columnIndex++;
		colNames[columnIndex] = tableVar + "_home_location"; columnIndex++;
		colNames[columnIndex] = tableVar + "_mothers_name"; columnIndex++;
		colNames[columnIndex] = tableVar + "_fathers_name"; columnIndex++;
		
		return colNames;
	}
}