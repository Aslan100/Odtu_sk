package bordomor.odtu.sk.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.util.DBUtils;
import bordomor.util.StringUtils;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.IXmlObject;

@XMLAndDatabaseValues(tableName = "promotion", tagName = "promotion", defaultVariable = "prm")
public class Promotion implements IXmlObject, IDBObject
{
	private int id = -1;
	private String title = null;
	private int branchId = -1;
	private boolean isSiblingDiscount = false;
	private float discountRatio = -1;
	private float discountAmount = -1;
	private boolean overridesOthers = false;
	
	private Branch branch = null;
	
	public Promotion() {}
	
	public Promotion(int id)
	{
		this.id = id;
	}
	
	public Promotion(int id, String title, int branchId, boolean isSiblingDiscount, float discountRatio, float discountAmount, boolean overridesOthers)
	{
		this.id = id;
		this.title = title;
		this.branchId = branchId;
		this.isSiblingDiscount = isSiblingDiscount;
		this.discountRatio = discountRatio;
		this.discountAmount = discountAmount;
		this.overridesOthers = overridesOthers;
	}
	
	//Yardımcı Metodlar
	@Override
	public boolean equals(Object comparedObject) 
	{
		try
		{
			if(comparedObject == this)
				return true;
			else if(comparedObject == null || !(comparedObject instanceof Promotion))
				return false;
			else
			{
				Promotion comparedPromotion = (Promotion) comparedObject;
				
				return this.id == comparedPromotion.id
						&& (this.title == null && comparedPromotion.title == null) || this.title.equals(comparedPromotion.title)
						&& this.branchId == comparedPromotion.branchId
						&& this.isSiblingDiscount == comparedPromotion.isSiblingDiscount
						&& this.discountRatio == comparedPromotion.discountRatio
						&& this.discountAmount == comparedPromotion.discountAmount
						&& this.overridesOthers == comparedPromotion.overridesOthers;
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
        final int prime = 3;
        
        int result = super.hashCode();
        result = prime*result + (int) (this.id ^ (this.id >>> 32));
        result = prime*result + (this.title == null ? 0 : this.title.hashCode());
        result = prime*result + this.branchId;
        result = prime*result + (int) (this.isSiblingDiscount ? 1 : 0);
        result = prime*result + (int) (this.discountRatio);
        result = prime*result + (int) (this.discountAmount);
        result = prime*result + (int) (this.overridesOthers ? 1 : 0);
        
        return result;
    }
		
	//XML Bölümü
	@Override
	public void parseFromXMLElement(Element element) {}

	@Override
	public Element makeXMLElement(Document xml, boolean cascadeRelations) 
	{
		try
		{
			String tagName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tagName();
			
			Element promotionEl = xml.createElement(tagName);
			promotionEl.setAttribute("id", this.id + "");
			promotionEl.setAttribute("title", this.title);
			promotionEl.setAttribute("branch_id", this.branchId + "");
			promotionEl.setAttribute("is_sibling_discount", this.isSiblingDiscount + "");
			promotionEl.setAttribute("discount_ratio", this.discountRatio + "");
			promotionEl.setAttribute("discount_amount", this.discountAmount + "");
			promotionEl.setAttribute("overrides_others", this.overridesOthers + "");
			
			return promotionEl;
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
			    
			String tableName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tableName();
		    String insertQuery = ""
					+ "INSERT INTO "
					+		tableName
					+ "			(title, branch, is_sibling_discount, discount_ratio, discount_amount, overrides_others) "
					+ "VALUES "
					+ "		(?, ?, ?, ?, ?, ?)";
	       	
	   		insertSt = newConn.prepareStatement(insertQuery, new String[] { "id", "discount_ratio", "discount_amount" });
	   		insertSt.setString(1, this.title.trim());
	   		insertSt.setInt(2, this.branchId);
	   		insertSt.setBoolean(3, this.isSiblingDiscount);
	   		insertSt.setFloat(4, this.discountRatio > 0 ? this.discountRatio : 0);
	   		insertSt.setFloat(5, this.discountAmount > 0 ? this.discountAmount : 0);
	   		insertSt.setBoolean(6, this.overridesOthers);
	   		insertSt.executeUpdate();
	       
    		keySet = insertSt.getGeneratedKeys();
    		keySet.next();
    		this.setId(keySet.getInt("id"));
    		this.setDiscountRatio(keySet.getFloat("discount_ratio"));
    		this.setDiscountAmount(keySet.getFloat("discount_amount"));
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
	        
        	String tableName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tableName();
        	Promotion updatingPromotion = (Promotion) updatingObj;
        	
        	String updateQuery = ""
    				+ "UPDATE "
    				+ 		tableName + " "
    				+ "SET "
    				+ "		title = ?, branch = ?, is_sibling_discount = ?,  discount_ratio = ?,  discount_amount = ?,  overrides_others = ? "
    				+ "WHERE "
    				+ "		id = ?";
    		
    		updateSt = newConn.prepareStatement(updateQuery, new String[] {"id"});
    		updateSt.setString(1, updatingPromotion.title.trim());
    		updateSt.setInt(2, updatingPromotion.branchId);
    		updateSt.setBoolean(3, updatingPromotion.isSiblingDiscount);
    		updateSt.setFloat(4, updatingPromotion.discountRatio);
    		updateSt.setFloat(5, updatingPromotion.discountAmount);
    		updateSt.setBoolean(6, updatingPromotion.overridesOthers);
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
	
	public String createCodeInDB(Connection conn) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement insertSt = null;
        ResultSet keySet = null;
        
        try
        {
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			    
			String genCode = "KPN-" + StringUtils.generateRandomString(4, 5, true) + "-" + StringUtils.generateRandomString(3, 4, true);
			String insertQuery = ""
					+ "INSERT INTO "
					+ "		promotion_coupon " 
					+ "			(code, promotion) "
					+ "VALUES "
					+ "		(?, ?)";
	       	
	   		insertSt = newConn.prepareStatement(insertQuery, new String[] { "id", "code" });
	   		insertSt.setString(1, genCode);
	   		insertSt.setInt(2, this.id);
	   		insertSt.executeUpdate();
	   		
    		keySet = insertSt.getGeneratedKeys();
    		keySet.next();
    		
    		return keySet.getString("code");
    	}
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(insertSt);
        	
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
	
	public String getTitle() 
	{
		return title;
	}
	
	public void setTitle(String title) 
	{
		this.title = title;
	}
	
	public int getBranchId() 
	{
		return branchId;
	}
	
	public void setBranchId(int branchId) 
	{
		this.branchId = branchId;
	}
	
	public boolean isSiblingDiscount()
	{
		return isSiblingDiscount;
	}
	
	public  void setSiblingDiscount(boolean isSiblingDiscount)
	{
		this.isSiblingDiscount = isSiblingDiscount;
	}
	
	public float getDiscountRatio() 
	{
		return discountRatio;
	}
	
	public void setDiscountRatio(float discountRatio) 
	{
		this.discountRatio = discountRatio;
	}
	
	public float getDiscountAmount() 
	{
		return discountAmount;
	}
	
	public void setDiscountAmount(float discountAmount) 
	{
		this.discountAmount = discountAmount;
	}
	
	public boolean isOverridesOthers() 
	{
		return overridesOthers;
	}
	
	public void setOverridesOthers(boolean overridesOthers) 
	{
		this.overridesOthers = overridesOthers;
	}
	
	public Branch getBranch() 
	{
		return branch;
	}
	
	public void setBranch(Branch branch) 
	{
		this.branch = branch;
	}

	//Statik Sorgular
	public static Promotion[] findAll(Connection conn) throws ClassNotFoundException, SQLException
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
					+		Promotion.generateColumnNameString("prm") + " "
					+ "FROM "
					+ "		promotion prm "
					+ "ORDER BY "
					+ "		prm.branch ASC, prm.title ASC";
					
			sqlSt = newConn.prepareStatement(query);
			rs = sqlSt.executeQuery();
	        
			Vector<Promotion> promotions = new Vector<Promotion>();
			
	        while(rs.next())
	        	promotions.add(Promotion.parseFromRecord(rs, "prm"));
	        
	        if(promotions.size() > 0)
	        	promotions.trimToSize();
	        
	        return promotions.isEmpty() ? null : promotions.toArray(new Promotion[promotions.size()]);
	    }
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static Promotion findById(Connection conn, int id) throws ClassNotFoundException, SQLException
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
					+		Promotion.generateColumnNameString("prm") + " "
					+ "FROM "
					+ "		promotion prm "
					+ "WHERE "
					+ "		id = ?";
					
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, id);
			rs = sqlSt.executeQuery();
	        
			Promotion promotion = null;
			
	        if(rs.next())
	        	promotion = Promotion.parseFromRecord(rs, "prm");
	        
	        return promotion;
	    }
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static Promotion findByCouponCode(Connection conn, String couponCode) throws ClassNotFoundException, SQLException
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
					+		Promotion.generateColumnNameString("prm") + " "
					+ "FROM "
					+ "		promotion prm, "
					+ "		promotion_coupon cpn "
					+ "WHERE "
					+ "		cpn.promotion = prm.id "
					+ "		AND cpn.is_consumed  = false "
					+ "		AND cpn.valid_until > now() "
					+ "		AND cpn.code = ?";
					
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setString(1, couponCode);
			rs = sqlSt.executeQuery();
	        
			Promotion promotion = null;
			
	        if(rs.next())
	        	promotion = Promotion.parseFromRecord(rs, "prm");
	        
	        return promotion;
	    }
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static Promotion[] findByPaymentPlan(Connection conn, PaymentPlan plan) throws ClassNotFoundException, SQLException
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
					+		Promotion.generateColumnNameString("prm") + " "
					+ "FROM "
					+ "		promotion prm,"
					+ "		promotion_attachment att "
					+ "WHERE "
					+ "		att.attached_promotion = prm.id "
					+ "		AND att.plan = ? "
					+ "ORDER BY "
					+ "		att.id ASC";
					
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setInt(1, plan.getId());
	        rs = sqlSt.executeQuery();
	        
			Vector<Promotion> promotions = new Vector<Promotion>();
			
	        while(rs.next())
	        	promotions.add(Promotion.parseFromRecord(rs, "prm"));
	        
	        if(promotions.size() > 0)
	        	promotions.trimToSize();
	        
	        return promotions.isEmpty() ? null : promotions.toArray(new Promotion[promotions.size()]);
	    }
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static Promotion[] findByBranch(Connection conn, Branch branch) throws ClassNotFoundException, SQLException
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
					+		Promotion.generateColumnNameString("prm") + " "
					+ "FROM "
					+ "		promotion prm "
					+ "WHERE "
					+ "		prm.branch = ? "
					+ "ORDER BY "
					+ "		prm.title ASC";
					
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setInt(1, branch.getId());
	        rs = sqlSt.executeQuery();
	        
			Vector<Promotion> promotions = new Vector<Promotion>();
			
	        while(rs.next())
	        	promotions.add(Promotion.parseFromRecord(rs, "prm"));
	        
	        if(promotions.size() > 0)
	        	promotions.trimToSize();
	        
	        return promotions.isEmpty() ? null : promotions.toArray(new Promotion[promotions.size()]);
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
 	public static Promotion parseFromRecord(ResultSet rs)
	{
		try
		{
			Promotion parsedPromotion = null;
			int planId = rs.getInt("id");  
			
			if(!rs.wasNull())
			{
				parsedPromotion = new Promotion(planId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals("title"))
						parsedPromotion.setTitle(rs.getString(nextCol));
					else if(nextCol.equals("branch"))
						parsedPromotion.setBranchId(rs.getInt(nextCol));
					else if(nextCol.equals("discount_ratio"))
						parsedPromotion.setDiscountRatio(rs.getFloat(nextCol));
					else if(nextCol.equals("discount_amount"))
						parsedPromotion.setDiscountAmount(rs.getFloat(nextCol));
					else if(nextCol.equals("overrides_others"))
						parsedPromotion.setOverridesOthers(rs.getBoolean(nextCol));
				}
				
				parsedPromotion.setBranch(Branch.findById(rs.getStatement().getConnection(), parsedPromotion.branchId));
			}
			
			return parsedPromotion;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static Promotion parseFromRecord(ResultSet rs, String tableVar)
	{
		try
		{
			String[] generatedColumnNames = Promotion.generateColumnNames(tableVar);
			
			Promotion parsedPromotion = null;
			int planId = rs.getInt(generatedColumnNames[0]);  
			
			if(!rs.wasNull())
			{
				parsedPromotion = new Promotion(planId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals(generatedColumnNames[1]))
						parsedPromotion.setTitle(rs.getString(generatedColumnNames[1]));
					else if(nextCol.equals(generatedColumnNames[2]))
						parsedPromotion.setBranchId(rs.getInt(nextCol));
					else if(nextCol.equals(generatedColumnNames[3]))
						parsedPromotion.setSiblingDiscount(rs.getBoolean(generatedColumnNames[3]));
					else if(nextCol.equals(generatedColumnNames[4]))
						parsedPromotion.setDiscountRatio(rs.getFloat(generatedColumnNames[4]));
					else if(nextCol.equals(generatedColumnNames[5]))
						parsedPromotion.setDiscountAmount(rs.getFloat(generatedColumnNames[5]));
					else if(nextCol.equals(generatedColumnNames[6]))
						parsedPromotion.setOverridesOthers(rs.getBoolean(generatedColumnNames[6]));
				}
				
				parsedPromotion.setBranch(Branch.findById(rs.getStatement().getConnection(), parsedPromotion.branchId));
			}
				
			return parsedPromotion;
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
		colNames[columnIndex] = tableVar + "_branch"; columnIndex++;
		colNames[columnIndex] = tableVar + "_is_sibling_discount"; columnIndex++;
		colNames[columnIndex] = tableVar + "_discount_ratio"; columnIndex++;
		colNames[columnIndex] = tableVar + "_discount_amount"; columnIndex++;
		colNames[columnIndex] = tableVar + "_overrides_others"; columnIndex++;
		
		return colNames;
	}
	
	public static String generateColumnNameString(String tableVar)
	{
		String[] generatedColumnNames = Promotion.generateColumnNames(tableVar);
		String colNameStr = "";
			
		for(String nextColumnName : generatedColumnNames)
		{
			String nextVar = nextColumnName.substring(nextColumnName.indexOf("_") + 1);
			colNameStr += ", " + tableVar + "." + nextVar + " AS " + nextColumnName;
		}
			
		return colNameStr.substring(2);
	}
}