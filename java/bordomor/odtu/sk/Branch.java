package bordomor.odtu.sk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import bordomor.odtu.sk.Params.TimeUnit;
import bordomor.odtu.sk.payment.Promotion;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.IXmlObject;
import bordomor.util.DBUtils;

@XMLAndDatabaseValues(tableName = "branch", tagName = "branch", defaultVariable = "bch")
public class Branch implements IDBObject, IXmlObject
{
	private int id = -1;
	private String title = null;
	private float[] prices = null;
	private float penaltyRate = -1f;
	
	private Promotion[] promotions = null; 
	
	public Branch()
	{
		super();
	}
	
	public Branch(int id)
	{
		this.id = id;
	}
	
	public Branch(int id, String title)
	{
		this(id);
		this.title = title;
	}
	
	public Branch(int id, String title, float[] prices, float penaltyRate)
	{
		this(id, title);
		this.prices = prices;
		this.penaltyRate = penaltyRate;
	}
	
	//Yardımcı Metodlar
	@Override
	public boolean equals(Object comparedObject) 
	{
		try
		{
			if(comparedObject == this)
				return true;
			else if(comparedObject == null || !(comparedObject instanceof Branch))
				return false;
			else
			{
				Branch comparedBranch = (Branch) comparedObject;
				
				return this.id == comparedBranch.id 
						&& ((this.title == null && comparedBranch.title == null) || this.title.equals(comparedBranch.title));
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
        final int prime = 13;
        
        int result = super.hashCode();
        result = prime*result + (int) (this.id ^ (this.id >>> 32));
        result = prime*result + (this.title == null ? 0 : this.title.hashCode());
        
        return result;
    }
	
	public synchronized void addPromotion(Promotion addedPromotion)
	{
		if(addedPromotion != null)
		{
			ArrayList<Promotion> promotionList = new ArrayList<Promotion>(Arrays.asList(this.promotions == null ? new Promotion[0] : this.promotions));
			promotionList.add(addedPromotion);
				
			this.promotions = promotionList.toArray(new Promotion[promotionList.size()]);
		}
	}
	
	public synchronized void removePromotion(Promotion removedPromotion)
	{
		if(this.promotions != null)
		{
			ArrayList<Promotion> promotionList = new ArrayList<Promotion>(Arrays.asList(this.promotions));
			promotionList.remove(removedPromotion);
			this.promotions = promotionList.toArray(new Promotion[promotionList.size()]);
		}
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
			Element branchEl = xml.createElement(this.getClass().getAnnotation(XMLAndDatabaseValues.class).tagName());
			branchEl.setAttribute("id", this.id + "");
			
			if(this.title != null)
				branchEl.setAttribute("title", this.title);
			
			branchEl.setAttribute("daily_price", this.prices[0] + "");
			branchEl.setAttribute("weekly_price", this.prices[1] + "");
			branchEl.setAttribute("monthly_price", this.prices[2] + "");
			branchEl.setAttribute("annual_price", this.prices[3] + "");
			branchEl.setAttribute("penalty_rate", this.penaltyRate + "");
			
			if(cascadeRelations && this.promotions != null)
			{
				for(Promotion nextPromotion : this.promotions)
					branchEl.appendChild(nextPromotion.makeXMLElement(xml, false));
			}
			
			return branchEl;
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
	        
	        String tableName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tableName();
	        String insertQuery = ""
    				+ "INSERT INTO "
    				+ 		tableName
    				+ "			(title, daily_price, weekly_price, monthly_price, annual_price, penalty_rate) "
    				+ "VALUES "
    				+ "		(?, ?, ?, ?, ?, ?)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] {"id"});
    		insertSt.setString(1, this.title.trim());
    		insertSt.setFloat(2, this.prices[0]);
    		insertSt.setFloat(3, this.prices[1]);
    		insertSt.setFloat(4, this.prices[2]);
    		insertSt.setFloat(5, this.prices[3]);
    		insertSt.setFloat(6, this.penaltyRate);
    		
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
	public void updateInDB(Connection conn, IDBObject updatingObj) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement updateSt = null;
        
        try
        {
        	if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
        	Branch updatingBranch = (Branch) updatingObj;
        	
        	String tableName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tableName();
        	String updateQuery = ""
    				+ "UPDATE "
    				+		tableName + " "
    				+ "SET "
    				+ "		title = ?, daily_price = ?, weekly_price = ?,  monthly_price = ?,  annual_price = ?,  penalty_rate = ? "
    				+ "WHERE "
    				+ "		id = ?";
    		
    		updateSt = newConn.prepareStatement(updateQuery, new String[] {"id"});
    		updateSt.setString(1, updatingBranch.title.trim());
    		updateSt.setFloat(2, updatingBranch.prices[0]);
    		updateSt.setFloat(3, updatingBranch.prices[1]);
    		updateSt.setFloat(4, updatingBranch.prices[2]);
    		updateSt.setFloat(5, updatingBranch.prices[3]);
    		updateSt.setFloat(6, updatingBranch.penaltyRate);
    		updateSt.setInt(7, this.id);
    		
    		if(updateSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException(); 
        }
        finally
        {
        	DBUtils.close(updateSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
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
	
	public String getTitle() 
	{
		return title;
	}

	public void setTitle(String title) 
	{
		this.title = title;
	}

	public float[] getPrices() 
	{
		return prices;
	}

	public void setPrices(float[] prices) 
	{
		this.prices = prices;
	}

	public float getPenaltyRate() 
	{
		return penaltyRate;
	}

	public void setPenaltyRate(float penaltyRate) 
	{
		this.penaltyRate = penaltyRate;
	}
	
	public float getPrice(TimeUnit unit)
	{
		if(this.prices == null || this.prices.length != 4)
			throw new IllegalArgumentException("Bad price data.");
		else if(unit == null)
			throw new IllegalArgumentException("Bad unit data.");
		
		return this.prices[unit.ordinal()];
	}
	
	public void setPrice(float price, TimeUnit unit)
	{
		if(this.prices == null)
			this.prices = new float[4];
		else if(this.prices.length != 4)
			throw new IllegalArgumentException("Bad price data.");
		else if(unit == null)
			throw new IllegalArgumentException("Bad unit data.");
		
		this.prices[unit.ordinal()] = price; 
	}
	
	public Promotion[] getPromotions() 
	{
		return promotions;
	}
	
	public void setPromotions(Promotion[] promotions) 
	{
		this.promotions = promotions;
	}
	
	public Promotion getSiblingPromotion() 
	{
		if(this.promotions != null)
		{
			for(Promotion nextPromotion : this.promotions)
			{
				if(nextPromotion.isSiblingDiscount())
					return nextPromotion;
			}
		}
		
		return null;
	}
	
	//Statik Sorgular
	public static Branch[] findAll(Connection conn) 
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
					+		Branch.generateColumnNameString("bch") + " "
					+ "FROM "
					+ "		branch bch "
					+ "ORDER BY "
					+ "		bch.title ASC";
					
			sqlSt = newConn.prepareStatement(query);
	        rs = sqlSt.executeQuery();
	        
	        Vector<Branch> branches = new Vector<Branch>(0);
	        
	        while(rs.next())
	        	branches.add(Branch.parseFromRecord(rs, "bch"));
	        
	        if(branches.size() > 0)
	        	branches.trimToSize();
	        
			return branches.isEmpty() ? null : branches.toArray(new Branch[branches.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Branch findById(Connection conn, int id) 
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
					+		Branch.generateColumnNameString("bch") + " "
					+ "FROM "
					+ "		branch bch "
					+ "WHERE "
					+ "		bch.id = ?";
					
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, id);
			rs = sqlSt.executeQuery();
	        
			Branch branch = null;
			
	        if(rs.next())
	        	branch = Branch.parseFromRecord(rs, "bch");
	        
	        return branch;
	    }
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
    }
	
	public static Branch findById_IncludePromotions(Connection conn, int id) 
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
					+		Branch.generateColumnNameString("bch") + ", "
					+ 		Promotion.generateColumnNameString("prm") + " "
					+ "FROM "
					+ "		branch bch "
					+ "			LEFT OUTER JOIN promotion prm "
					+ "				ON (prm.branch = bch.id) "
					+ "WHERE "
					+ "		bch.id = ?";
					
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, id);
			rs = sqlSt.executeQuery();
	        
			Branch branch = null;
			
	        while(rs.next())
	        {
	        	if(branch == null)
	        		branch = Branch.parseFromRecord(rs, "bch");
	        	
	        	branch.addPromotion(Promotion.parseFromRecord(rs, "prm"));
	        }
	        
	        return branch;
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
 	public static Branch parseFromRecord(ResultSet rs)
	{
		try
		{
			Branch parsedBranch = null;
			int branchId = rs.getInt("id");  
			
			if(!rs.wasNull())
			{
				parsedBranch = new Branch(branchId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals("title"))
						parsedBranch.setTitle(rs.getString(nextCol));
					else if(nextCol.equals("daily_price"))
						parsedBranch.setPrice(rs.getFloat(nextCol), TimeUnit.DAYS);
					else if(nextCol.equals("weekly_price"))
						parsedBranch.setPrice(rs.getFloat(nextCol), TimeUnit.WEEKS);
					else if(nextCol.equals("monthly_price"))
						parsedBranch.setPrice(rs.getFloat(nextCol), TimeUnit.MONTHS);
					else if(nextCol.equals("annual_price"))
						parsedBranch.setPrice(rs.getFloat(nextCol), TimeUnit.YEARS);
					else if(nextCol.equals("penaly_rate"))
						parsedBranch.setPenaltyRate(rs.getFloat(nextCol));
				}
			}
				
			return parsedBranch;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static Branch parseFromRecord(ResultSet rs, String tableVar)
	{
		try
		{
			String[] generatedColumnNames = Branch.generateColumnNames(tableVar);
			
			Branch parsedBranch = null;
			int branchId = rs.getInt(generatedColumnNames[0]);  
			
			if(!rs.wasNull())
			{
				parsedBranch = new Branch(branchId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals(generatedColumnNames[1]))
						parsedBranch.setTitle(rs.getString(nextCol));
					else if(nextCol.equals(generatedColumnNames[2]))
						parsedBranch.setPrice(rs.getFloat(nextCol), TimeUnit.DAYS);
					else if(nextCol.equals(generatedColumnNames[3]))
						parsedBranch.setPrice(rs.getFloat(nextCol), TimeUnit.WEEKS);
					else if(nextCol.equals(generatedColumnNames[4]))
						parsedBranch.setPrice(rs.getFloat(nextCol), TimeUnit.MONTHS);
					else if(nextCol.equals(generatedColumnNames[5]))
						parsedBranch.setPrice(rs.getFloat(nextCol), TimeUnit.YEARS);
					else if(nextCol.equals(generatedColumnNames[6]))
						parsedBranch.setPenaltyRate(rs.getFloat(nextCol));
				}
			}
				
			return parsedBranch;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static String[] generateColumnNames(String tableVar)
	{
		if(tableVar == null)
			throw new IllegalArgumentException("Bad table variable.");
		
		String[] colNames = new String[7];
		
		int columnIndex = 0;
		colNames[columnIndex] = tableVar + "_id"; columnIndex++;
		colNames[columnIndex] = tableVar + "_title"; columnIndex++;
		colNames[columnIndex] = tableVar + "_daily_price"; columnIndex++;
		colNames[columnIndex] = tableVar + "_weekly_price"; columnIndex++;
		colNames[columnIndex] = tableVar + "_monthly_price"; columnIndex++;
		colNames[columnIndex] = tableVar + "_annual_price"; columnIndex++;
		colNames[columnIndex] = tableVar + "_penalty_rate"; columnIndex++;
		
		return colNames;
	}
	
	public static String generateColumnNameString(String tableVar)
	{
		String[] generatedColumnNames = Branch.generateColumnNames(tableVar);
		String colNameStr = "";
			
		for(String nextColumnName : generatedColumnNames)
		{
			String nextVar = nextColumnName.substring(nextColumnName.indexOf("_") + 1);
			colNameStr += ", " + tableVar + "." + nextVar + " AS " + nextColumnName;
		}
			
		return colNameStr.substring(2);
	}
}