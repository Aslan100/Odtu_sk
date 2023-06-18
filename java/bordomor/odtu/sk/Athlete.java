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
import bordomor.odtu.sk.Params.AgeGroup;
import bordomor.odtu.sk.Params.BloodType;
import bordomor.odtu.sk.Params.Gender;
import bordomor.odtu.sk.Params.LoginableState;
import bordomor.odtu.sk.Params.MembershipState;
import bordomor.odtu.sk.Team.AgeGroupInterval;
import bordomor.odtu.sk.location.City;
import bordomor.odtu.sk.location.Location;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@XMLAndDatabaseValues(tableName = "athlete", tagName = "athlete", defaultVariable = "ath")
public class Athlete extends Loginable
{
	private int id = -1;
	
	private Branch primaryBranch = null;
	private Team primaryTeam = null;
	
	private int height = -1;
	private float weight = -1f;
	private BloodType bloodType = null;
	
	private String idNo = null;
	private Timestamp birthDate = null;
	private City placeOfBirth = null;
	private Location homeLocation = null;
	private String mothersName = null;
	private String fathersName = null;
	private String school = null;
	private MembershipState membership = null;
	
	private AgeGroup ageGroup = null;
	
	private Parent[] parents = null;
	private bordomor.odtu.sk.template.Document[] documents = null;
	
	public Athlete()
	{
		super();
	}
	
	public Athlete(int id)
	{
		super();
		this.id = id;
	}
	
	public Athlete(int id, int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state)
	{
		super(loginableId, code, email, name, surname, gender, phoneNumber, state);
		this.id = id;
		this.membership = MembershipState.STANDARD;
	}
	
	public Athlete(int id, int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber,  LoginableState state, 
			Timestamp creationTime, Timestamp lastModifiedTime)
	{
		super(loginableId, code, email, name, surname, gender, phoneNumber, state, creationTime, lastModifiedTime);
		this.id = id;
		this.membership = MembershipState.STANDARD;
	}
	
	public Athlete(int id, int height, float weight, Timestamp birthDate, BloodType bloodType, Branch primaryBranch, Team primaryTeam, String idNo, String school, MembershipState membership,  
			int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state)
	{
		super(loginableId, code, email, name, surname, gender, phoneNumber, state);
		this.id = id;
		this.height = height;
		this.weight = weight;
		this.birthDate = birthDate;
		this.bloodType = bloodType;
		this.primaryBranch = primaryBranch;
		this.primaryTeam = primaryTeam;
		this.idNo = idNo;
		this.school = school;
		this.membership = membership;
	}
	
	public Athlete(int id, int height, float weight, Timestamp birthDate, BloodType bloodType, Branch primaryBranch, Team primaryTeam, String idNo, String school, MembershipState membership,  
			int loginableId, String code, String email, String name, String surname, Gender gender, String phoneNumber, LoginableState state, 
			Timestamp creationTime, Timestamp lastModifiedTime)
	{
		this(id, height, weight, birthDate, bloodType, primaryBranch, primaryTeam, idNo, school, membership, loginableId, code, email, name, surname, gender, phoneNumber, state);
		super.creationTime = creationTime;
		super.lastModifiedTime = lastModifiedTime;
	}
	
	//Yardımcı Metodlar
	@Override
	public boolean equals(Object comparedObject) 
	{
		if(comparedObject == this)
			return true;
		else if(comparedObject == null || !(comparedObject instanceof Athlete))
			return false;
		else
		{
			Athlete comparedUser = (Athlete) comparedObject;
			
			return (super.equals(comparedUser) && this.id == comparedUser.id);
		}
	}
	
	@Override
    public int hashCode() 
	{
        final int prime = 13;
        
        int result = super.hashCode();
        result = prime*result + (int) (this.id ^ (this.id >>> 32));
        result = prime*result + (this.birthDate == null ? 0 : this.birthDate.hashCode());
        result = prime*result + (this.bloodType == null ? 0 : this.bloodType.hashCode());
        
        return result;
    }
	
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
		return Params.PORTAL_ATHLETE_HOME_URI;
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
	
	public boolean compliesWith(AgeGroupInterval interval)
	{
		if(this.ageGroup == null)
			return false;
		else
		{
			if(interval.finalAge == null)
				return false;
			
			boolean compliesWithFinalAge = this.ageGroup.getValue() <= interval.finalAge.getValue();
			
			return compliesWithFinalAge &= (interval.startAge == null ?  true : this.ageGroup.getValue() >= interval.startAge.getValue());
		}
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
			Element userEl = xml.createElement("athlete");
			userEl.setAttribute("id", this.id + "");
			userEl.setAttribute("height", this.height + "");
			userEl.setAttribute("weight", this.weight + "");
			
			if(this.birthDate != null)
				userEl.setAttribute("birth_date", Params.DATE_FORMAT.format(this.birthDate));
			
			if(this.bloodType != null)
				userEl.setAttribute("blood_type", this.bloodType.toString());
			
			if(this.primaryBranch != null)
				userEl.setAttribute("primary_branch", this.primaryBranch.toString());
			
			if(this.idNo != null)
				userEl.setAttribute("id_no", this.idNo.trim());
			
			if(this.school != null)
				userEl.setAttribute("school", this.school.trim());
			
			if(this.membership != null)
				userEl.setAttribute("membership", this.membership.toString());
			
			if(this.ageGroup != null)
				userEl.setAttribute("age_group", this.ageGroup.toString());
			
			if(cascadeRelations)
			{
				if(this.parents != null && this.parents.length > 0)
				{
					Element parentsEl = xml.createElement("parents");
					
					for(Parent nextParent : this.parents)
						parentsEl.appendChild(nextParent.makeXMLElement(xml, false));
					
					xml.appendChild(parentsEl);
				}
				
				if(this.documents != null && this.documents.length > 0)
				{
					Element licencesEl = xml.createElement("documents");
					
					for(bordomor.odtu.sk.template.Document nextDoc : this.documents)
						licencesEl.appendChild(nextDoc.makeXMLElement(xml, false));
					
					xml.appendChild(licencesEl);
				}
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
    				+ "		athlete "
    				+ "			(primary_branch, primary_team, height, weight, blood_type, "
    				+ "				id_no, birth_date, place_of_birth, home_location, mothers_name, fathers_name, school, membership, loginable_parent) "
    				+ "VALUES "
    				+ "		(?, ?, ?, ?, ?::blood_type, ?, ?, ?, ?, ?, ?, ?, ?::membership_state, ?)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		insertSt.setInt(1, this.primaryBranch.getId());
    		insertSt.setObject(2, this.primaryTeam != null ? this.primaryTeam.getId() : null, Types.INTEGER);
    		insertSt.setObject(3, this.height > 0 ? this.height : null, Types.INTEGER);
    		insertSt.setObject(4, this.weight > 0 ? this.weight : null, Types.FLOAT);
    		insertSt.setString(5, this.bloodType != null ? this.bloodType.toString() : BloodType.A_RH_POS.toString());
    		insertSt.setObject(6, this.idNo != null ? this.idNo.trim() : null, Types.VARCHAR);
    		insertSt.setTimestamp(7, this.birthDate);
    		insertSt.setObject(8, this.placeOfBirth != null ? this.placeOfBirth.getId() : null, Types.INTEGER);
    		insertSt.setObject(9, this.homeLocation != null ? this.homeLocation.getId() : null, Types.INTEGER);
    		insertSt.setObject(10, this.mothersName != null ? this.mothersName.trim() : null, Types.VARCHAR);
    		insertSt.setObject(11, this.fathersName != null ? this.fathersName.trim() : null, Types.VARCHAR);
    		insertSt.setObject(12, this.school != null ? this.school.trim() : null, Types.VARCHAR);
    		insertSt.setString(13, this.membership.toString());
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
        		try { this.homeLocation.deleteFromDB(newConn); } catch(Exception ex1) {}
        	}
        	
        	if(this.loginableId > 0)
        	{
        		try { super.deleteFromDB(newConn); } catch(Exception ex2) {}
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
	        
        	Athlete updatingAthlete = (Athlete) updatingUser;
        	
        	/*if(newAthlete.homeLocation != null)
        		this.homeLocation.updateInDB(newConn, newAthlete.homeLocation);
        	else
        		this.homeLocation.deleteFromDB(newConn);*/
        	
	        super.updateInDB(newConn, updatingAthlete);
			
    		String insertQuery = ""
    				+ "UPDATE "
    				+ "		athlete "
    				+ "SET "
    				+ "		primary_branch = ?, height = ?, weight = ?, blood_type = ?::blood_type, "
    				+ "		id_no = ?, birth_date = ?, place_of_birth = ?, mothers_name = ?, fathers_name = ?,"
    				+ "		school = ?, membership = ?::membership_state "
    				+ "WHERE "
    				+ "		id = ?";
    		
    		updateSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		updateSt.setInt(1, updatingAthlete.primaryBranch.getId());
    		updateSt.setObject(2, updatingAthlete.height > 0 ? updatingAthlete.height : null, Types.INTEGER);
    		updateSt.setObject(3, updatingAthlete.weight > 0 ? updatingAthlete.weight : null, Types.FLOAT);
    		updateSt.setString(4, updatingAthlete.bloodType.toString());
    		updateSt.setObject(5, updatingAthlete.idNo != null ? updatingAthlete.idNo.trim() : null, Types.VARCHAR);
    		updateSt.setTimestamp(6, updatingAthlete.birthDate);
    		updateSt.setObject(7, updatingAthlete.placeOfBirth != null ? updatingAthlete.placeOfBirth.getId() : null, Types.INTEGER);
    		updateSt.setObject(8, updatingAthlete.mothersName != null ? updatingAthlete.mothersName.trim() : null, Types.VARCHAR);
    		updateSt.setObject(9, updatingAthlete.fathersName != null ? updatingAthlete.fathersName.trim() : null, Types.VARCHAR);
    		updateSt.setObject(10, updatingAthlete.fathersName != null ? updatingAthlete.fathersName.trim() : null, Types.VARCHAR);
    		updateSt.setString(11, updatingAthlete.membership.toString());
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
					+ "		id = (SELECT loginable_parent FROM athlete WHERE id = ?)";
	        
	        sqlSt = newConn.prepareStatement(deleteQuery);
	        sqlSt.setInt(1, this.id);
	        
	        if(sqlSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        this.id = -1;
	        this.loginableId = -1;
	        this.code = null;
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
        PreparedStatement stateSt = null;
        ResultSet keySet = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			String modificationQuery = ""
					+ "UPDATE "
					+ "		loginable "
					+ "SET "
					+ "		state = ?::loginable_state "
					+ "WHERE "
					+ "		id = "
					+ "			(SELECT loginable_parent FROM athlete WHERE id = ?)";
	        
			stateSt = newConn.prepareStatement(modificationQuery, new String[] {"id", "last_modified_time"});
			stateSt.setString(1, newState.name());
			stateSt.setInt(2, this.id);
			
			if(stateSt.executeUpdate() != 1)
				throw new IllegalArgumentException();
	        
			keySet = stateSt.getGeneratedKeys();
			keySet.next();
			
			this.setLoginableId(keySet.getInt("id"));
        	this.setLastModifiedTime(keySet.getTimestamp("last_modified_time"));
        	this.setState(newState);
        }
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(stateSt);
        	
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
	
	public Team getPrimaryTeam() 
	{
		return primaryTeam;
	}
	
	public void setPrimaryTeam(Team primaryTeam) 
	{
		this.primaryTeam = primaryTeam;
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
	
	public String getSchool() 
	{
		return school;
	}
	
	public void setSchool(String school) 
	{
		this.school = school;
	}
	
	@Override
	public void setRole(LoginRole role) {}
	
	@Override
	public LoginRole getRole()
	{
		return null;
	}
	
	public MembershipState getMembership() 
	{
		return membership;
	}
	
	public void setMembership(MembershipState membership) 
	{
		this.membership = membership;
	}
	
	public AgeGroup getAgeGroup() 
	{
		return ageGroup;
	}
	
	public void setAgeGroup(AgeGroup ageGroup) 
	{
		this.ageGroup = ageGroup;
	}
	
	public Parent[] getParents() 
	{
		return parents;
	}
	
	public void setParents(Parent[] parents) 
	{
		this.parents = parents;
	}
	
	public bordomor.odtu.sk.template.Document[] getDocuments() 
	{
		return documents;
	}
	
	public void setDocuments(bordomor.odtu.sk.template.Document[] documents) 
	{
		this.documents = documents;
	}
	
	//Statik Sorgular
	public static Athlete[] findAll(Connection conn) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Athlete.class, "ath");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		loginable l, "
					+ "		athlete ath "
					+ "WHERE "
					+ "		ath.loginable_parent = l.id ";
					
			sqlSt = newConn.prepareStatement(query);
	        rs = sqlSt.executeQuery();
	        
	        Vector<Athlete> athletes = new Vector<Athlete>(0);
	        
	        while(rs.next())
	        	athletes.add(Athlete.parseFromRecord(rs, typeDef, "l"));
	        
	        if(athletes.size() > 0)
	        	athletes.trimToSize();
	        
			return athletes.isEmpty() ? null : athletes.toArray(new Athlete[athletes.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Athlete[] findAll_ForTeam(Connection conn, Team team) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Athlete.class, "ath");
			boolean hasGender = team.getGenderCategory() != null && team.getGenderCategory() != Gender.PREFER_NOT_TO_SAY;
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		loginable l, "
					+ "		athlete ath "
					+ "WHERE "
					+ "		ath.loginable_parent = l.id "
					+ "		AND ath.primary_branch = ?::branch ";
			
				if(hasGender)	
					query += ""
						+ "	AND l.gender = ?::gender ";
				
				query += ""
					+ "ORDER BY "
					+ "		l.gender ASC, ath.birth_date ASC, l.name ASC";
					
					
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setString(1, team.getBranch().toString());
			
			if(hasGender)
				sqlSt.setString(2, team.getGenderCategory().toString());
			
	        rs = sqlSt.executeQuery();
	        
	        Vector<Athlete> athletes = new Vector<Athlete>(0);
	        
	        while(rs.next())
	        {
	        	Athlete nextAthlete = Athlete.parseFromRecord(rs, typeDef, "l");
	        	
	        	if(team.getAgeGroups() == null || nextAthlete.compliesWith(team.getAgeGroups()))
	        		athletes.add(nextAthlete);
	        }
	        
	        if(athletes.size() > 0)
	        	athletes.trimToSize();
	        
			return athletes.isEmpty() ? null : athletes.toArray(new Athlete[athletes.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Athlete[] findByTeamId(Connection conn, int teamId) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Athlete.class, "ath");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		loginable l, "
					+ "		athlete ath, "
					+ "		squad sqd "
					+ "WHERE "
					+ "		ath.loginable_parent = l.id "
					+ "		AND sqd.player = ath.id "
					+ "		AND sqd.team = ?";
					
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, teamId);
			rs = sqlSt.executeQuery();
	        
	        Vector<Athlete> athletes = new Vector<Athlete>(0);
	        
	        while(rs.next())
	        	athletes.add(Athlete.parseFromRecord(rs, typeDef, "l"));
	        
	        if(athletes.size() > 0)
	        	athletes.trimToSize();
	        
			return athletes.isEmpty() ? null : athletes.toArray(new Athlete[athletes.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Athlete findById(Connection conn, int id) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Athlete.class, "ath");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		loginable l, "
					+ "		athlete ath "
					+ "WHERE "
					+ "		ath.loginable_parent = l.id "
					+ "		AND ath.id = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, id);
	        rs = sqlSt.executeQuery();
	        
	        Athlete athlete = null;
	        
	        if(rs.next())
	        	athlete = Athlete.parseFromRecord(rs, typeDef, "l");
	        
			return athlete;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Athlete findByCode(Connection conn, String code) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Athlete.class, "ath");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		loginable l, "
					+ "		athlete ath "
					+ "WHERE "
					+ "		ath.loginable_parent = l.id "
					+ "		AND l.code = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, code);
	        rs = sqlSt.executeQuery();
	        
	        Athlete athlete = null;
	        
	        if(rs.next())
	        	athlete = Athlete.parseFromRecord(rs, typeDef, "l");
	        
			return athlete;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Athlete findByIdNo(Connection conn, String idNo) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Athlete.class, "ath");
			
			String query = ""
					+ "SELECT "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		loginable l, "
					+ "		athlete ath "
					+ "WHERE "
					+ "		ath.loginable_parent = l.id "
					+ "		AND ath.id_no = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, idNo);
	        rs = sqlSt.executeQuery();
	        
	        Athlete athlete = null;
	        
	        if(rs.next())
	        	athlete = Athlete.parseFromRecord(rs, typeDef, "l");
	        
			return athlete;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Athlete findByStok(Connection conn, String stok) 
			throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			TypeDefinition typeDef = new TypeDefinition(Athlete.class, "su");
			
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
	        
	        Athlete systemUser = null;
	        
	        if(rs.next())
	        {
	        	systemUser = Athlete.parseFromRecord(rs, typeDef, "l");
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
 	public static Athlete parseFromRecord(ResultSet rs)
	{
		try
		{
			Athlete parsedUser = null;
			int userId = rs.getInt("id");  
			
			if(!rs.wasNull())
			{
				parsedUser = new Athlete(userId);
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
					else if(nextCol.equals("state"))
						parsedUser.setState(LoginableState.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals("height"))
						parsedUser.setHeight(rs.getInt(nextCol));
					else if(nextCol.equals("weight"))
						parsedUser.setWeight(rs.getFloat(nextCol));
					else if(nextCol.equals("birth_date"))
					{
						parsedUser.setBirthDate(rs.getTimestamp(nextCol));
						parsedUser.setAgeGroup(AgeGroup.getGroup(Params.DATE_FORMAT.format(parsedUser.getBirthDate())));
					}
					else if(nextCol.equals("blood_type"))
						parsedUser.setBloodType(BloodType.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals("primary_branch"))
						parsedUser.setPrimaryBranch(Branch.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("primary_team"))
						parsedUser.setPrimaryTeam(Team.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("id_no"))
						parsedUser.setIdNo(rs.getString(nextCol));
					else if(nextCol.equals("school"))
						parsedUser.setSchool(rs.getString(nextCol));
					else if(nextCol.equals("membership"))
						parsedUser.setMembership(MembershipState.valueOf(rs.getString(nextCol)));
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
	
	public static Athlete parseFromRecord(ResultSet rs, TypeDefinition typeDefinition, String loginableTableVar)
	{
		try
		{
			Class<? extends Loginable> type = typeDefinition.getType();
			String tableVar = typeDefinition.getTableVariable();
			
			if(type != Athlete.class || tableVar == null)
				throw new Exception("Bad type definition.");
			
			Athlete parsedUser = null;
			int userId = rs.getInt(tableVar + "_id");  
			
			if(!rs.wasNull())
			{
				parsedUser = new Athlete(userId);
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
					else if(nextCol.equals(loginableTableVar + "_state"))
						parsedUser.setState(LoginableState.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals(tableVar + "_height"))
						parsedUser.setHeight(rs.getInt(nextCol));
					else if(nextCol.equals(tableVar + "_weight"))
						parsedUser.setWeight(rs.getFloat(nextCol));
					else if(nextCol.equals(tableVar + "_birth_date"))
					{
						parsedUser.setBirthDate(rs.getTimestamp(nextCol));
						parsedUser.setAgeGroup(AgeGroup.getGroup(Params.DATE_FORMAT.format(parsedUser.getBirthDate())));
					}
					else if(nextCol.equals(tableVar + "_home_location") && rs.getInt(tableVar + "_home_location") > 0)
						parsedUser.setHomeLocation(Location.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(tableVar + "_blood_type"))
						parsedUser.setBloodType(BloodType.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals(tableVar + "_primary_branch"))
						parsedUser.setPrimaryBranch(Branch.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(tableVar + "_primary_team") && rs.getInt(tableVar + "_primary_team") > 0)
						parsedUser.setPrimaryTeam(Team.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(tableVar + "_id_no"))
						parsedUser.setIdNo(rs.getString(nextCol));
					else if(nextCol.equals(tableVar + "_school"))
						parsedUser.setSchool(rs.getString(nextCol));
					else if(nextCol.equals(tableVar + "_membership"))
						parsedUser.setMembership(MembershipState.valueOf(rs.getString(nextCol)));
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
	
	public static String[] generateColumnNames(String tableVar, boolean includeExtensions, String extensionTableVar)
	{
		if(tableVar == null)
			throw new IllegalArgumentException("Bad table variable.");
		
		String[] colNames = new String[11];
		
		int columnIndex = 0;
		colNames[columnIndex] = tableVar + "_id"; columnIndex++;
		colNames[columnIndex] = tableVar + "_height"; columnIndex++;
		colNames[columnIndex] = tableVar + "_weight"; columnIndex++;
		colNames[columnIndex] = tableVar + "_birth_date"; columnIndex++;
		colNames[columnIndex] = tableVar + "_home_location"; columnIndex++;
		colNames[columnIndex] = tableVar + "_blood_type"; columnIndex++;
		colNames[columnIndex] = tableVar + "_primary_branch"; columnIndex++;
		colNames[columnIndex] = tableVar + "_primary_team"; columnIndex++;
		colNames[columnIndex] = tableVar + "_id_no"; columnIndex++;
		colNames[columnIndex] = tableVar + "_school"; columnIndex++;
		colNames[columnIndex] = tableVar + "_membership"; columnIndex++;
		
		return colNames;
	}
}