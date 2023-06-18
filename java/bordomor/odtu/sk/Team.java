package bordomor.odtu.sk;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import bordomor.odtu.sk.Params.AgeGroup;
import bordomor.odtu.sk.Params.Gender;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.odtu.sk.template.Loginable.TypeDefinition;
import bordomor.odtu.sk.template.DBTimeTrackable;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.IXmlObject;
import bordomor.odtu.sk.template.Loginable;
import bordomor.util.DBUtils;

@XMLAndDatabaseValues(tableName = "team", tagName = "team", defaultVariable = "tm")
public class Team extends DBTimeTrackable implements IXmlObject, IDBObject  
{
	private int id = -1;
	private String name = null;
	private AgeGroup ageCategory = null;
	private AgeGroupInterval ageGroups = null;
	private Branch branch = null;
	private Gender genderCategory = null;
	private Trainer trainer = null;
	
	private Athlete[] players = null;
	
	public Team()
	{
		super();
	}
	
	public Team(int id)
	{
		super();
		this.id = id;
	}
	
	public Team(int id, String name, AgeGroup ageCategory, AgeGroupInterval ageGroups, Branch branch)
	{
		super();
		this.id = id;
		this.name = name;
		this.ageCategory = ageCategory;
		this.ageGroups = ageGroups;
		this.branch = branch;
	}
	
	//Yardımcı Metodlar
	@Override
	public boolean equals(Object comparedObject) 
	{
		if(comparedObject == this)
			return true;
		else if(comparedObject == null || !(comparedObject instanceof Team))
			return false;
		else
		{
			Team comparedUser = (Team) comparedObject;
			
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
	
	//Xml Bölümü
	@Override
	public void parseFromXMLElement(Element userEl)
	{
		try
		{
			this.id = Integer.parseInt(userEl.getAttribute("id"));
			super.parseTTAttributes(userEl);
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
			Element teamEl = xml.createElement("team");
			teamEl.setAttribute("id", this.id + "");
			
			if(this.name != null)
				teamEl.setAttribute("name", this.name);
			
			if(this.ageCategory != null)
				teamEl.setAttribute("age_category", this.ageCategory.toString());
			
			if(this.genderCategory != null)
				teamEl.setAttribute("gender_category", this.genderCategory.toString());
			
			if(cascadeRelations)
			{
				if(this.branch != null)
					teamEl.appendChild(this.branch.makeXMLElement(xml, false));
				
				if(this.ageGroups != null)
				{
					Element ageGroupsEl = xml.createElement("age_group_interval");
					ageGroupsEl.setAttribute("start_group", this.ageGroups.startAge.toString());
					ageGroupsEl.setAttribute("final_group", this.ageGroups.finalAge.toString());
						
					teamEl.appendChild(ageGroupsEl);
				}
				
				if(this.trainer != null)
					teamEl.appendChild(this.trainer.makeXMLElement(xml, false));
				
				if(this.players != null)
				{
					Element squadEl = xml.createElement("squad");
					
					for(Athlete nextPlayer : this.players)
						squadEl.appendChild(nextPlayer.makeXMLElement(xml, false));
					
					teamEl.appendChild(squadEl);
				}
			}
			
			super.appendTTAttributes(teamEl);
			
			return teamEl;
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
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        if(this.ageGroups != null)
	        	this.ageGroups.createInDB(newConn);
	        
	        String insertQuery = ""
    				+ "INSERT INTO "
    				+ "		team "
    				+ "			(name, age_category, age_groups, branch, gender_category, trainer) "
    				+ "VALUES "
    				+ "		(?, ?::age_group, ?, ?::branch, ?::gender, ?)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		insertSt.setString(1, this.name);
    		insertSt.setString(2, this.ageCategory.toString());
    		insertSt.setObject(3, this.ageGroups == null || this.ageGroups.id <= 0 ? null : this.ageGroups.id, Types.INTEGER);
    		insertSt.setString(4, this.branch.toString());
    		insertSt.setString(5, this.genderCategory.toString());
    		insertSt.setObject(6, this.trainer == null ? null : this.trainer.getId(), Types.INTEGER);
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
	public void updateInDB(Connection conn, IDBObject newTeam) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement updateSt = null;
        ResultSet keySet = null;
        
        try
        {
        	Team updatingTeam = (Team) newTeam;
        	
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        if(this.ageGroups != null || updatingTeam.ageGroups != null)
	        {
	        	if(this.ageGroups != null && updatingTeam.ageGroups == null)
	        		this.ageGroups.deleteFromDB(newConn);
	        	else if(this.ageGroups != null && updatingTeam.ageGroups != null)
	        		this.ageGroups.updateInDB(newConn, updatingTeam.ageGroups);
	        	else if(this.ageGroups == null && updatingTeam.ageGroups != null)
	        		updatingTeam.ageGroups.createInDB(newConn);
	        	
	        	this.ageGroups = updatingTeam.ageGroups;
	        }
	        
	        String updateQuery = ""
    				+ "UPDATE "
    				+ "		team "
    				+ "SET "
    				+ "		name = ?, age_category = ?::age_group, branch = ?::branch, gender_category = ?::gender, trainer = ? "
    				+ "WHERE "
    				+ "		id = ?";
    		
	        updateSt = newConn.prepareStatement(updateQuery, new String[] {"id", "creation_time", "last_modified_time"});
	        updateSt.setString(1, updatingTeam.name.trim());
	        updateSt.setString(2, updatingTeam.ageCategory.toString());
	        updateSt.setString(3, updatingTeam.branch.toString());
	        updateSt.setString(4, updatingTeam.genderCategory.toString());
	        updateSt.setObject(5, updatingTeam.trainer == null ? null : updatingTeam.trainer.getId(), Types.INTEGER);
	        updateSt.setInt(6, this.id);
	        
	        if(updateSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException(); 
        	
    		keySet = updateSt.getGeneratedKeys();
    		keySet.next();
    		
    		this.name = updatingTeam.name;
    		this.ageCategory = updatingTeam.ageCategory;
    		this.branch = updatingTeam.branch;
    		this.genderCategory = updatingTeam.genderCategory;
    		this.trainer = updatingTeam.trainer;
    		super.parseTTAttributes(keySet);
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
					+ "		team "
					+ "WHERE "
					+ "		id = ?";
	        
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
	
	public void addAthleteInDB(Connection conn, Athlete athleteToAdd) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement insertSt = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			String insertQuery = ""
    				+ "INSERT INTO "
    				+ "		squad "
    				+ "			(team, player) "
    				+ "VALUES "
    				+ "		(?, ?)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		insertSt.setInt(1, this.id);
    		insertSt.setInt(2, athleteToAdd.getId());
    		insertSt.executeUpdate();
        }
        finally
        {
        	DBUtils.close(insertSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	public void removeAthleteInDB(Connection conn, Athlete athleteToRemove) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement insertSt = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			String insertQuery = ""
    				+ "DELETE FROM "
    				+ "		squad "
    				+ "WHERE "
    				+ "		team = ? "
    				+ "		AND player = ? ";
    				
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		insertSt.setInt(1, this.id);
    		insertSt.setInt(2, athleteToRemove.getId());
    		insertSt.executeUpdate();
        }
        finally
        {
        	DBUtils.close(insertSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	public void syncAthletesInDB(Connection conn, Team synchronizingTeam)  throws ClassNotFoundException, SQLException
	{
		if(this.players == null)
			return;
		else
		{
			Athlete[] allowedAthletes = Athlete.findAll_ForTeam(conn, synchronizingTeam);
			
			for(Athlete teamAthlete : this.players)
			{
				boolean matchFound = false;
				
				for(int i = 0; allowedAthletes != null && i < allowedAthletes.length; i++)
				{
					if(teamAthlete.getId() == allowedAthletes[i].getId())
					{	
						matchFound = true;
						break;
					}
				}
				
				if(!matchFound)
					this.removeAthleteInDB(conn, teamAthlete);
			}
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
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public AgeGroup getAgeCategory() 
	{
		return ageCategory;
	}
	
	public void setAgeCategory(AgeGroup ageCategory) 
	{
		this.ageCategory = ageCategory;
	}
	
	public AgeGroupInterval getAgeGroups()
	{
		return ageGroups;
	}
	
	public void setAgeGroups(AgeGroupInterval ageGroups) throws IOException
	{
		if(this.ageCategory != null && this.ageCategory.getValue() < ageGroups.finalAge.getValue())
			throw new IOException("Incompatible age values");
		
		this.ageGroups = ageGroups;
	}
	
	public Branch getBranch() 
	{
		return branch;
	}
	
	public void setBranch(Branch branch) 
	{
		this.branch = branch;
	}
	
	public Gender getGenderCategory() 
	{
		return genderCategory;
	}
	
	public void setGenderCategory(Gender genderCategory) 
	{
		this.genderCategory = genderCategory;
	}
	
	public Trainer getTrainer() 
	{
		return trainer;
	}
	
	public void setTrainer(Trainer trainer) 
	{
		this.trainer = trainer;
	}

	public Athlete[] getPlayers() 
	{
		return players;
	}
	
	public void setPlayers(Athlete[] players) 
	{
		this.players = players;
	}
	
	//Statik Sorgular
	public static Team[] findAll(Connection conn) 
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
					+ "		tm.*, "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		team tm "
					+ "			LEFT OUTER JOIN trainer trn  "
					+ "				ON (tm.trainer = trn.id) "
					+ "			LEFT OUTER JOIN loginable l"
					+ "				ON (trn.loginable_parent = l.id) "
					+ "ORDER BY "
					+ "		tm.branch ASC, tm.age_category ASC, tm.name ASC";
					
			sqlSt = newConn.prepareStatement(query);
	        rs = sqlSt.executeQuery();
	        
	        Vector<Team> teams = new Vector<Team>(0);
	        
	        while(rs.next())
	        {
	        	Team nextTeam = Team.parseFromRecord(rs);
	        	nextTeam.setTrainer(Trainer.parseFromRecord(rs, typeDef, "l"));
	        	teams.add(nextTeam);
	        }
	        
	        if(teams.size() > 0)
	        	teams.trimToSize();
	        
			return teams.isEmpty() ? null : teams.toArray(new Team[teams.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Team findById(Connection conn, int id) 
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
					+ "		tm.*, "
					+ "		intr.id AS int_id, intr.start_group AS int_start, intr.final_group AS int_final, "
					+ 		Loginable.generateColumnNameString(new TypeDefinition[] {typeDef}, "l") + " "
					+ "FROM "
					+ "		team tm "
					+ "			LEFT OUTER JOIN age_group_interval intr"
					+ "				ON (tm.age_groups = intr.id) "
					+ "			LEFT OUTER JOIN trainer trn "
					+ "				ON (tm.trainer = trn.id) "
					+ "			LEFT OUTER JOIN loginable l"
					+ "				ON (trn.loginable_parent = l.id) "
					+ "WHERE "
					+ "		tm.id = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, id);
	        rs = sqlSt.executeQuery();
	        
	        Team team = null;
	        
	        if(rs.next())
	        {
	        	team = Team.parseFromRecord(rs);
	        	
	        	if(rs.getInt("int_id") > 0)
	        	{
		        	AgeGroupInterval interval = team.new AgeGroupInterval();
		        	interval.parseFromRecord(rs, new String[] {"int_id", "int_start", "int_final"});
	        	}
	        	
	        	team.setTrainer(Trainer.parseFromRecord(rs, typeDef, "l"));
	        	
	        	Athlete[] squad = Athlete.findByTeamId(newConn, team.getId());
	        	team.setPlayers(squad);
	        }
	        
			return team;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Team[] findByBranch(Connection conn, Branch branch) 
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
					+ "SELECT "
					+ "		tm.* "
					+ "FROM "
					+ "		team tm "
					+ "WHERE "
					+ "		tm.branch = ?::branch";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, branch.toString());
	        rs = sqlSt.executeQuery();
	        
	        Vector<Team> teams = new Vector<Team>(0);
	        
	        while(rs.next())
		        teams.add(Team.parseFromRecord(rs));
	        
	        if(teams.size() > 0)
	        	teams.trimToSize();
	        
			return teams.isEmpty() ? null : teams.toArray(new Team[teams.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Team[] findByBranchAndAgeGroup(Connection conn, Branch branch, AgeGroup ageGroup) 
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
					+ "SELECT "
					+ "		tm.* "
					+ "FROM "
					+ "		team tm "
					+ "WHERE "
					+ "		tm.branch = ?::branch "
					+ "		AND tm.age_category = ?::age_category";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, branch.toString());
	        sqlSt.setString(2, ageGroup.toString());
	        rs = sqlSt.executeQuery();
	        
	        Vector<Team> teams = new Vector<Team>(0);
	        
	        while(rs.next())
		        teams.add(Team.parseFromRecord(rs));
	        
	        if(teams.size() > 0)
	        	teams.trimToSize();
	        
			return teams.isEmpty() ? null : teams.toArray(new Team[teams.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Team[] findByBranchAndGenderCategory(Connection conn, Branch branch, Gender genderCategory) 
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
					+ "SELECT "
					+ "		tm.* "
					+ "FROM "
					+ "		team tm "
					+ "WHERE "
					+ "		tm.branch = ?::branch "
					+ "		AND tm.gender_category = ?::gender";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, branch.toString());
	        sqlSt.setString(2, genderCategory.toString());
	        rs = sqlSt.executeQuery();
	        
	        Vector<Team> teams = new Vector<Team>(0);
	        
	        while(rs.next())
		        teams.add(Team.parseFromRecord(rs));
	        
	        if(teams.size() > 0)
	        	teams.trimToSize();
	        
			return teams.isEmpty() ? null : teams.toArray(new Team[teams.size()]);
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
 	public static Team parseFromRecord(ResultSet rs)
	{
		try
		{
			Team parsedTeam = null;
			int teamId = rs.getInt("id");  
			
			if(!rs.wasNull())
			{
				parsedTeam = new Team(teamId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals("name"))
						parsedTeam.setName(rs.getString("name"));
					else if(nextCol.equals("age_category"))
						parsedTeam.setAgeCategory(AgeGroup.valueOf(rs.getString("age_category")));
					else if(nextCol.equals("branch"))
						parsedTeam.setBranch(Branch.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("gender_category"))
						parsedTeam.setGenderCategory(Gender.valueOf(rs.getString("gender_category")));
				}
				
				parsedTeam.parseTTAttributes(rs);
			}
				
			return parsedTeam;
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
	
	//İç sınıflar
	public class AgeGroupInterval implements IDBObject
	{
		protected int id = -1;
		protected AgeGroup startAge = null;
		protected AgeGroup finalAge = null;
		
		public AgeGroupInterval() 
		{
			Team.this.ageGroups = this;
		}
		
		public AgeGroupInterval(int id)
		{
			this.id = id;
			Team.this.ageGroups = this;
		}
		
		public AgeGroupInterval(int id, AgeGroup startAge, AgeGroup finalAge)
		{
			this.id = id;
			this.startAge = startAge;
			this.finalAge = finalAge;
			
			Team.this.ageGroups = this;
		}
		
		//Yardımcı Metodlar
		@Override
		public boolean equals(Object comparedObject) 
		{
			if(comparedObject == this)
				return true;
			else if(comparedObject == null || !(comparedObject instanceof AgeGroupInterval))
				return false;
			else
			{
				AgeGroupInterval comparedInterval = (AgeGroupInterval) comparedObject;
				
				return (this.startAge != null ? this.startAge.equals(comparedInterval.startAge) : true)
						&& (this.finalAge != null ? this.finalAge.equals(comparedInterval.finalAge) : true);
			}
		}
		
		@Override
	    public int hashCode() 
		{
	        final int prime = 13;
	        
	        int result = super.hashCode();
	        result = prime*result + (int) (this.id ^ (this.id >>> 32));
	        result = prime*result + (this.startAge == null ? 0 : this.startAge.hashCode());
	        result = prime*result + (this.finalAge == null ? 0 : this.finalAge.hashCode());
	        
	        return result;
	    }
		
		public void parseFrom(AgeGroup[] groupVals)
		{
			if(groupVals == null)
				throw new IllegalArgumentException("Bad age group values");
			
			int minVal = 1000;
			int maxVal = 0;
			
			for(AgeGroup nextGroup : groupVals)
			{
				if(nextGroup.getValue() < minVal)
				{
					minVal = nextGroup.getValue();
					this.startAge = nextGroup;
				}
				
				if(nextGroup.getValue() > maxVal)
				{
					maxVal = nextGroup.getValue();
					this.finalAge = nextGroup;
				}
			}
		}
		
		/*VT Bölümü*/
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
	    				+ "		age_group_interval "
	    				+ "			(start_group, final_group) "
	    				+ "VALUES "
	    				+ "		(?::age_group, ?::age_group)";
	        	
	    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
	    		insertSt.setString(1, this.startAge.toString());
	    		insertSt.setString(2, this.finalAge.toString());
	    		
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
		public void updateInDB(Connection conn, IDBObject newInterval) throws ClassNotFoundException, SQLException 
		{
			Connection newConn = conn;
	        PreparedStatement updateSt = null;
	        ResultSet keySet = null;
	        
	        try
	        {
		        AgeGroupInterval updatingInterval = (AgeGroupInterval) newInterval;
	        	
	        	if(conn == null)
		        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
		        
		        String updateQuery = ""
	    				+ "UPDATE "
	    				+ "		age_group_interval "
	    				+ "SET"
	    				+ "		start_group = ?::age_group, final_group = ?::age_group "
	    				+ "WHERE "
	    				+ "		id = ?";
	    		
	    		updateSt = newConn.prepareStatement(updateQuery, new String[] {"id"});
	    		updateSt.setString(1, updatingInterval.startAge.toString());
	    		updateSt.setString(2, updatingInterval.finalAge.toString());
	    		updateSt.setInt(3, this.id);
	    		
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
		public void deleteFromDB(Connection conn) throws ClassNotFoundException, SQLException 
		{
			Connection newConn = conn;
			PreparedStatement deleteSt = null;
			
			try
			{
				if(conn == null)
					newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
				
				String deleteQuery = "DELETE FROM age_group WHERE id = ?";
				deleteSt = newConn.prepareStatement(deleteQuery);
		        deleteSt.setInt(1, this.id);
		        
		        if(deleteSt.executeUpdate() < 1)
		        	throw new IllegalArgumentException();
		        
		        this.id = -1;
		    }
			catch(Exception ex)
			{
				throw ex;
			}
			finally
			{
				DBUtils.close(deleteSt);
				
				if(conn == null)
					DBUtils.close(newConn);
			}
		}
		
		/*Get-set*/
		public int getId() 
		{
			return id;
		}
		
		public void setId(int id) 
		{
			this.id = id;
		}
		
		protected AgeGroup getStartAge() 
		{
			return startAge;
		}
		
		protected void setStartAge(AgeGroup startAge) 
		{
			this.startAge = startAge;
		}
		
		protected AgeGroup getFinalAge() 
		{
			return finalAge;
		}
		
		protected void setFinalAge(AgeGroup finalAge) 
		{
			this.finalAge = finalAge;
		}
	
		//Statik metodlar
		public void parseFromRecord(ResultSet rs, String[] columns)
		{
			this.id = -1;
			this.startAge = null;
			this.finalAge = null;
			
			try
			{
				int intervalId = rs.getInt(columns[0]);  
				
				if(!rs.wasNull())
				{
					this.id = (intervalId);
					this.startAge = AgeGroup.valueOf(rs.getString(columns[1]));
					this.finalAge = AgeGroup.valueOf(rs.getString(columns[2]));
				}
			}
			catch(Exception ex)
			{
				throw new IllegalArgumentException("Bad data set.");
			}
		}
	}
}