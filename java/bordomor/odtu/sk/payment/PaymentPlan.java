package bordomor.odtu.sk.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import bordomor.odtu.sk.Athlete;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.PaymentState;
import bordomor.odtu.sk.Parent;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.util.DBUtils;
import bordomor.util.StringUtils;
import bordomor.odtu.sk.template.DBTimeTrackable;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.IXmlObject;
import bordomor.odtu.sk.template.PaymentSchema;

@XMLAndDatabaseValues(tableName = "payment_plan", tagName = "payment_plan", defaultVariable = "pln")
public class PaymentPlan extends DBTimeTrackable implements IXmlObject, IDBObject
{
	private int id = -1;
	private String code = null;
	private Parent liable = null;
	private Athlete paidFor = null;
	private PaymentSchema generatorSchema = null;
	private boolean isDefinitive = false;
	
	private Payment[] payments = null;
	private Promotion[] promotions = null;
	
	public PaymentPlan() {}
	
	public PaymentPlan(int id)
	{
		this.id = id;
	}
	
	public PaymentPlan(int id, String code)
	{
		this.id = id;
		this.code = code;
	}
	
	public PaymentPlan(int id, String code, Parent liable, Athlete paidFor, PaymentSchema generatorSchema)
	{
		this.id = id;
		this.code = code;
		this.liable = liable;
		this.paidFor = paidFor;
		this.generatorSchema = generatorSchema;
	}
	
	//Yardımcı Metodlar
	@Override
	public boolean equals(Object comparedObject) 
	{
		try
		{
			if(comparedObject == this)
				return true;
			else if(comparedObject == null || !(comparedObject instanceof PaymentPlan))
				return false;
			else
			{
				PaymentPlan comparedPlan = (PaymentPlan) comparedObject;
				
				return this.id == comparedPlan.id 
						&& ((this.code == null && comparedPlan.code == null) || this.code.equals(comparedPlan.code))
						&& this.isDefinitive == comparedPlan.isDefinitive;
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
        result = prime*result + (this.code == null ? 0 : this.code.hashCode());
        result = prime*result + (this.liable == null ? 0 : this.liable.hashCode());
        result = prime*result + (this.paidFor == null ? 0 : this.paidFor.hashCode());
        result = prime*result + (this.generatorSchema == null ? 0 : this.generatorSchema.hashCode());
        result = prime*result + (int) (this.isDefinitive ? 1 : 0);
        
        return result;
    }
	
	public synchronized void addPayment(Payment addedPayment)
	{
		ArrayList<Payment> paymentList = new ArrayList<Payment>(Arrays.asList(this.payments == null ? new Payment[0] : this.payments));
		paymentList.add(addedPayment);
			
		this.payments = paymentList.toArray(new Payment[paymentList.size()]);
	}
	
	public synchronized void removePayment(Payment removedPayment)
	{
		if(this.payments != null)
		{
			ArrayList<Payment> paymentList = new ArrayList<Payment>(Arrays.asList(this.payments));
			paymentList.remove(removedPayment);
			this.payments = paymentList.toArray(new Payment[paymentList.size()]);
		}
	}
	
	public synchronized void addPromotion(Promotion addedPromotion)
	{
		if(addedPromotion != null)
		{
			ArrayList<Promotion> promotionList = new ArrayList<Promotion>(Arrays.asList(this.promotions == null ? new Promotion[0] : this.promotions));
			promotionList.add(addedPromotion);
				
			this.promotions = promotionList.toArray(new Promotion[promotionList.size()]);
			this.recalculate();
		}
	}
	
	public synchronized void removePromotion(Promotion removedPromotion)
	{
		if(this.promotions != null)
		{
			ArrayList<Promotion> promotionList = new ArrayList<Promotion>(Arrays.asList(this.promotions));
			promotionList.remove(removedPromotion);
			this.promotions = promotionList.toArray(new Promotion[promotionList.size()]);
			this.recalculate();
		}
	}
	
	private void recalculate()
	{
		if(this.payments == null || this.payments.length == 0)
			return;
		
		for(Payment nextPayment : this.payments)
		{
			if(nextPayment.getState() == PaymentState.PENDING)
			{
				nextPayment.setAmount(nextPayment.getActualAmount());
				nextPayment.setDiscount(-1);
				
				for(int i = 0; this.promotions != null && i < this.promotions.length; i++)
				{
					Promotion nextPromotion = this.promotions[i];
					float nextAmount = nextPayment.getAmount();
					nextAmount -= nextPromotion.getDiscountAmount() > 0 ? nextPromotion.getDiscountAmount() : 0;
					nextAmount *= 1 - nextPromotion.getDiscountRatio();
					
					nextPayment.setAmount(nextAmount > 0 ? nextAmount : 0);
					nextPayment.setDiscount(nextPayment.getActualAmount() - nextAmount);
				}
			}
		}
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
			
			Element planEl = xml.createElement(tagName);
			planEl.setAttribute("id", this.id + "");
			planEl.setAttribute("is_definitive", this.isDefinitive + "");
			planEl.setAttribute("plan_total", this.getPlanTotal() + "");
			planEl.setAttribute("total_payment", this.getTotalPayment() + "");
			planEl.setAttribute("total_liability", this.getTotalLiability() + "");
			planEl.setAttribute("liability", this.getLiability() + "");
			
			if(this.code != null)
				planEl.setAttribute("code", this.code);
			
			if(cascadeRelations)
			{
				if(this.liable != null)
					planEl.appendChild(this.liable.makeXMLElement(xml, false));
				
				if(this.paidFor != null)
					planEl.appendChild(this.paidFor.makeXMLElement(xml, false));
				
				if(this.generatorSchema != null)
					planEl.appendChild(this.generatorSchema.makeXMLElement(xml, false));
				
				if(this.payments != null)
				{
					Element paymentsEl = xml.createElement("payments");
					
					for(Payment nextPayment : this.payments)
						paymentsEl.appendChild(nextPayment.makeXMLElement(xml, false));
					
					planEl.appendChild(paymentsEl);
				}
				
				if(this.promotions != null)
				{
					Element promotionsEl = xml.createElement("promotions");
					
					for(Promotion nextPromotion : this.promotions)
						promotionsEl.appendChild(nextPromotion.makeXMLElement(xml, false));
					
					planEl.appendChild(promotionsEl);
				}
			}
			
			super.appendTTAttributes(planEl);
			
			return planEl;
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
        PreparedStatement paymentSt = null;
        PreparedStatement promotionSt = null;
        ResultSet keySet = null;
        
        this.id = -1;
        
        try
        {
        	if(conn == null)
	    	   newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
        	String tableName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tableName();
	       	String genCode = "PLN-" + StringUtils.generateRandomString(4, 5, true) + "-" + StringUtils.generateRandomString(3, 4, true);
		        
			String insertQuery = ""
					+ "INSERT INTO "
					+ 		tableName
					+ "			(code, liable, paid_for, generator_schema, is_definitive) "
					+ "VALUES "
					+ "		(?, ?, ?, ?, ?)";
	       	
	   		insertSt = newConn.prepareStatement(insertQuery, new String[] { "id", "code", "creation_time", "last_modified_time" });
	   		insertSt.setString(1, genCode);
	   		insertSt.setInt(2, this.liable.getId());
	   		insertSt.setInt(3, this.paidFor.getId());
	   		insertSt.setObject(4, this.generatorSchema.getId(), Types.INTEGER);
	   		insertSt.setBoolean(5, this.isDefinitive);
	   		insertSt.executeUpdate();
	       
    		keySet = insertSt.getGeneratedKeys();
    		keySet.next();
    		this.setId(keySet.getInt("id"));
    		this.setCode(keySet.getString("code"));
    		this.parseTTAttributes(keySet);
    		
    		/*Ödemeler*/
    		if(this.payments != null)
    		{
    			DBUtils.close(keySet);
    			
    			String paymentTableName = Payment.class.getAnnotation(XMLAndDatabaseValues.class).tableName();
    			String paymentQuery =  ""
	    				+ "INSERT INTO "
	    				+		paymentTableName 
	    				+ "			(code, plan, period_start, period_end, actual_amount, discount, amount, due_date, paid_amount, paid_date, is_pacified) "
	    				+ "VALUES "
	    				+ "		(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    		
	    		paymentSt = newConn.prepareStatement(paymentQuery, new String[] { "id", "code" });
	    		newConn.setAutoCommit(false);
	    		
	    		for(Payment nextPayment : this.payments)
	    		{
	    			paymentSt.setString(1, "ODM-" + StringUtils.generateRandomString(4, 5, true) + "-" + StringUtils.generateRandomString(3, 4, true));
	    			paymentSt.setInt(2, this.id);
	    			paymentSt.setObject(3, nextPayment.getPeriodStart(), Types.TIMESTAMP);
	    			paymentSt.setObject(4, nextPayment.getPeriodEnd(), Types.TIMESTAMP);
	    			paymentSt.setFloat(5, nextPayment.getActualAmount() > 0 ? nextPayment.getActualAmount() : 0);
	    			paymentSt.setObject(6, nextPayment.getDiscount() > 0 ? nextPayment.getDiscount() : null, Types.FLOAT);
	    			paymentSt.setFloat(7, nextPayment.getAmount() > 0 ? nextPayment.getAmount() : 0);
	    			paymentSt.setTimestamp(8, nextPayment.getDueDate());
	    			paymentSt.setObject(9, nextPayment.getPaidAmount() > 0 ? nextPayment.getPaidAmount() : null, Types.FLOAT);
	    			paymentSt.setObject(10, nextPayment.getPaidDate(), Types.TIMESTAMP);
	    			paymentSt.setBoolean(11, nextPayment.isPacified());
	    			paymentSt.addBatch();
	    		}
	    		
	    		paymentSt.executeBatch();
	    		
	    		keySet = paymentSt.getGeneratedKeys();
	    		
	    		while(keySet.next())
	    		{
	    			this.payments[keySet.getRow() - 1].setId(keySet.getInt("id"));
	    			this.payments[keySet.getRow() - 1].setCode(keySet.getString("code"));
	    		}
	    		
	    		newConn.commit();
    		}
    		
    		/*Promosyonlar*/
    		if(this.promotions != null)
    		{
	    		String promotionQuery = ""
						+ "INSERT INTO "
						+ "		promotion_attachment "		
						+ "			(plan, attached_promotion) "
						+ "VALUES "
						+ "		(?, ?)";
		       	
	    		promotionSt = newConn.prepareStatement(promotionQuery);
	    		
	    		for(Promotion nextPromotion : this.promotions)
	    		{
	    			promotionSt.setInt(1, this.id);
	    			promotionSt.setInt(2, nextPromotion.getId());
	    			promotionSt.addBatch();
	    		}
	    		
	    		promotionSt.executeBatch();
	    		newConn.commit();
    		}
    		
    		newConn.setAutoCommit(true);
    	}
        catch(Exception ex)
        {
        	if(this.id != 1)
        		this.deleteFromDB(newConn);
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
	
	public void attachPromotionInDB(Connection conn, Promotion attachedPromotion) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
        PreparedStatement attachSt = null;
        
        try
        {
        	if(conn == null)
	    	   newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
        	String attachQuery = ""
					+ "INSERT INTO "
					+ "		promotion_attachment "		
					+ "			(plan, attached_promotion) "
					+ "VALUES "
					+ "		(?, ?)";
	       	
        	attachSt = newConn.prepareStatement(attachQuery);
        	attachSt.setInt(1, this.id);
        	attachSt.setInt(2, attachedPromotion.getId());
	   		
	   		if(attachSt.executeUpdate() != 1)
	   			throw new IllegalArgumentException();
	   		
	   		this.addPromotion(attachedPromotion);
	   		this.updatePlanWithPromotionInDB(newConn);
	    }
        finally
        {
        	DBUtils.close(attachSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);
        }
	}
	
	public void detachPromotionInDB(Connection conn, Promotion detachedPromotion) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
        PreparedStatement detachSt = null;
        
        try
        {
        	if(conn == null)
	    	   newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
        	String detachQuery = ""
					+ "DELETE FROM "
					+ "		promotion_attachment "		
					+ "WHERE "
					+ "		plan = ? "
					+ "		AND attached_promotion = ?";
	       	
        	detachSt = newConn.prepareStatement(detachQuery);
        	detachSt.setInt(1, this.id);
        	detachSt.setInt(2, detachedPromotion.getId());
	   		
	   		if(detachSt.executeUpdate() != 1)
	   			throw new IllegalArgumentException();
	   		
	   		this.removePromotion(detachedPromotion);
	   		this.updatePlanWithPromotionInDB(newConn);
	    }
        finally
        {
        	DBUtils.close(detachSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);
        }
	}
	
	private void updatePlanWithPromotionInDB(Connection conn) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
        PreparedStatement attachSt = null;
        
        try
        {
        	if(conn == null)
	    	   newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
        	for(int i = 0; i < this.payments.length; i++)
    		{
        		this.payments[i].updateColumnInDB(newConn, "discount", this.payments[i].getDiscount() > 0 ? this.payments[i].getDiscount() : null, Types.FLOAT);
        		this.payments[i].updateColumnInDB(newConn, "amount", this.payments[i].getAmount() > 0 ? this.payments[i].getAmount() : 0, Types.FLOAT);
    		}
        }
        finally
        {
        	DBUtils.close(attachSt);
        	
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

	public String getCode() 
	{
		return code;
	}
	
	public void setCode(String code) 
	{
		this.code = code;
	}
	
	public Parent getLiable() 
	{
		return liable;
	}
	
	public void setLiable(Parent liable) 
	{
		this.liable = liable;
	}
	
	public Athlete getPaidFor() 
	{
		return paidFor;
	}

	public void setPaidFor(Athlete paidFor) 
	{
		this.paidFor = paidFor;
	}
	
	public PaymentSchema getGeneratorSchema() 
	{
		return generatorSchema;
	}
	
	public void setGeneratorSchema(PaymentSchema generatorSchema) 
	{
		this.generatorSchema = generatorSchema;
	}

	public boolean isDefinitive()
	{
		return isDefinitive;
	}
	
	public void setDefinitive(boolean isDefinitive)
	{
		this.isDefinitive = isDefinitive;
	}
	
	public Payment[] getPayments() 
	{
		return payments;
	}
	
	public void setPayments(Payment[] payments) 
	{
		this.payments = payments;
	}
	
	public Promotion[] getPromotions() 
	{
		return promotions;
	}
	
	public void setPromotions(Promotion[] promotions)
	{
		this.promotions = promotions;
	}
	
	public float getPlanTotal()
	{
		float total = 0;
		
		if(this.payments != null)
		{
			for(Payment nextPayment : this.payments)
				total += nextPayment.getAmount() > 0 ? nextPayment.getAmount() : 0;
		}
		
		return total;
	}
	
	public float getTotalPayment()
	{
		float totalPayment = 0;
		
		if(this.payments != null)
		{
			for(Payment nextPayment : this.payments)
				totalPayment += nextPayment.getPaidAmount() > 0 ? nextPayment.getPaidAmount() : 0;
		}
		
		return totalPayment;
	}
	
	public float getTotalLiability()
	{
		float totalLiability = this.getPlanTotal();
		
		if(this.payments != null)
		{
			for(Payment nextPayment : this.payments)
			{
				if(nextPayment.getAmount() > 0 && nextPayment.isPacified())
					totalLiability -= (nextPayment.getAmount() - (nextPayment.getPaidAmount() > 0 ? nextPayment.getPaidAmount() : 0));
			}
		}
		
		return totalLiability;
	}
	
	public float getLiability()
	{
		float paidTotal = 0;
		float totalLiability = this.getTotalLiability();
		
		if(this.payments != null)
		{
			for(Payment nextPayment : this.payments)
				paidTotal += (nextPayment.getPaidAmount() > 0 ? nextPayment.getPaidAmount() : 0);
		}
		
		return totalLiability - paidTotal;
	}
	
	public boolean isInEffect()
	{
		if(this.payments == null || this.payments.length == 0)
			return false;
		
		long maxMsec = 0;
		
		for(Payment nextPayment : this.payments)
			maxMsec = nextPayment.getDueDate().getTime() > maxMsec ? nextPayment.getDueDate().getTime() : maxMsec;  
			
		LocalDateTime latestDT = new Timestamp(maxMsec).toLocalDateTime();
		LocalDateTime comparedDT = LocalDate.now().atStartOfDay();
			
		return latestDT.isAfter(comparedDT);
	}
	
	//Statik Sorgular
	public static PaymentPlan[] findAll(Connection conn) throws ClassNotFoundException, SQLException
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
					+		PaymentPlan.generateColumnNameString("pln") + " "
					+ "FROM "
					+ "		payment_plan pln "
					+ "ORDER BY "
					+ "		pln.creation_time DESC";
					
			sqlSt = newConn.prepareStatement(query);
	        rs = sqlSt.executeQuery();
	        
			Vector<PaymentPlan> plans = new Vector<PaymentPlan>();
			
	        while(rs.next())
	        	plans.add(PaymentPlan.parseFromRecord(rs, "pln"));
	        
	        if(plans.size() > 0)
	        	plans.trimToSize();
	        
	        return plans.isEmpty() ? null : plans.toArray(new PaymentPlan[plans.size()]);
	    }
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static PaymentPlan findById(Connection conn, int id) throws ClassNotFoundException, SQLException
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
					+		PaymentPlan.generateColumnNameString("pln") + ", "
					+		Payment.generateColumnNameString("py") + " "
					+ "FROM "
					+ "		payment_plan pln, "
					+ "		payment py "
					+ "WHERE "
					+ "		py.plan = pln.id "
					+ "		AND pln.id = ?"
					+ "ORDER BY "
					+ "		py.due_date ASC";
					
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, id);
			rs = sqlSt.executeQuery();
	        
			PaymentPlan plan = null;
			
	        while(rs.next())
	        {
	        	if(plan == null)
	        	{
	        		plan = PaymentPlan.parseFromRecord(rs, "pln"); 
	        		plan.setPromotions(Promotion.findByPaymentPlan(conn, plan));
	        	}
	        	
	        	plan.addPayment(Payment.parseFromRecord(rs, "py"));
	        }
	        
	        return plan;
	    }
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static PaymentPlan[] findByPaidFor(Connection conn, Athlete paidFor) throws ClassNotFoundException, SQLException
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
					+		PaymentPlan.generateColumnNameString("pln") + ", "
					+		Payment.generateColumnNameString("py") + " "
					+ "FROM "
					+ "		payment_plan pln, "
					+ "		payment py "
					+ "WHERE "
					+ "		py.plan = pln.id "
					+ "		AND pln.paid_for = ?"
					+ "ORDER BY "
					+ "		pln.creation_time DESC, "
					+ "		py.due_date ASC";
					
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, paidFor.getId());
			rs = sqlSt.executeQuery();
	        
			Vector<PaymentPlan> plans = new Vector<PaymentPlan>();
			PaymentPlan lastPlan = null;
			
	        while(rs.next())
	        {
	        	int nextPlanId = rs.getInt("pln_id");
	        	
	        	if(lastPlan == null || nextPlanId != lastPlan.getId())
	        	{
	        		PaymentPlan nextPlan = PaymentPlan.parseFromRecord(rs, "pln"); 
	        		nextPlan.setPromotions(Promotion.findByPaymentPlan(conn, nextPlan));
	        		plans.add(nextPlan);
	        		
	        		lastPlan = nextPlan;
	        	}
	        	
	        	lastPlan.addPayment(Payment.parseFromRecord(rs, "py"));
	        }
	        
	        if(plans.size() > 0)
	        	plans.trimToSize();
	        
	        return plans.isEmpty() ? null : plans.toArray(new PaymentPlan[plans.size()]);
	    }
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static PaymentPlan findByPaidFor_MostRecent(Connection conn, Athlete paidFor) throws ClassNotFoundException, SQLException
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
					+		PaymentPlan.generateColumnNameString("pln") + ","
					+		Payment.generateColumnNameString("py") + " "
					+ "FROM "
					+ "		payment_plan pln, payment py "
					+ "WHERE "
					+ "		py.plan = pln.id "
					+ "		AND pln.paid_for = ? "
					+ "ORDER BY "
					+ "		pln.creation_time DESC, "
					+ "		py.due_date ASC";
					
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, paidFor.getId());
			rs = sqlSt.executeQuery();
	        
			PaymentPlan plan = null;
			
	        while(rs.next())
	        {
	        	if(plan == null)
	        	{
	        		plan = PaymentPlan.parseFromRecord(rs, "pln");
	        		plan.setPromotions(Promotion.findByPaymentPlan(conn, plan));
	        	}
	        	
	        	plan.addPayment(Payment.parseFromRecord(rs, "py"));
	        }
	        
	        return plan;
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
 	public static PaymentPlan parseFromRecord(ResultSet rs)
	{
		try
		{
			PaymentPlan parsedPlan = null;
			int planId = rs.getInt("id");  
			
			if(!rs.wasNull())
			{
				parsedPlan = new PaymentPlan(planId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals("code"))
						parsedPlan.setCode(rs.getString(nextCol));
					else if(nextCol.equals("liable"))
						parsedPlan.setLiable(Parent.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("paid_for"))
						parsedPlan.setPaidFor(Athlete.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("generator_schema") && rs.getInt(nextCol) > 0)
						parsedPlan.setGeneratorSchema(PaymentSchema.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals("is_definitive"))
						parsedPlan.setDefinitive(rs.getBoolean(nextCol));
				}
			}
			
			parsedPlan.parseTTAttributes(rs);
			
			return parsedPlan;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static PaymentPlan parseFromRecord(ResultSet rs, String tableVar)
	{
		try
		{
			String[] generatedColumnNames = PaymentPlan.generateColumnNames(tableVar);
			
			PaymentPlan parsedPlan = null;
			int planId = rs.getInt(generatedColumnNames[0]);  
			
			if(!rs.wasNull())
				
			{
				parsedPlan = new PaymentPlan(planId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals(generatedColumnNames[1]))
						parsedPlan.setCode(rs.getString(nextCol));
					else if(nextCol.equals(generatedColumnNames[2]))
						parsedPlan.setLiable(Parent.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(generatedColumnNames[3]))
						parsedPlan.setPaidFor(Athlete.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(generatedColumnNames[4]) && rs.getInt(nextCol) > 0)
						parsedPlan.setGeneratorSchema(PaymentSchema.findById(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(generatedColumnNames[5]))
						parsedPlan.setDefinitive(rs.getBoolean(nextCol));
				}
				
				parsedPlan.parseTTAttributes(rs, generatedColumnNames[6], generatedColumnNames[7]);
			}
				
			return parsedPlan;
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
		
		String[] colNames = new String[8];
		
		int columnIndex = 0;
		colNames[columnIndex] = tableVar + "_id"; columnIndex++;
		colNames[columnIndex] = tableVar + "_code"; columnIndex++;
		colNames[columnIndex] = tableVar + "_liable"; columnIndex++;
		colNames[columnIndex] = tableVar + "_paid_for"; columnIndex++;
		colNames[columnIndex] = tableVar + "_generator_schema"; columnIndex++;
		colNames[columnIndex] = tableVar + "_is_definitive"; columnIndex++;
		colNames[columnIndex] = tableVar + "_creation_time"; columnIndex++;
		colNames[columnIndex] = tableVar + "_last_modified_time"; columnIndex++;
		
		return colNames;
	}
	
	public static String generateColumnNameString(String tableVar)
	{
		String[] generatedColumnNames = PaymentPlan.generateColumnNames(tableVar);
		String colNameStr = "";
			
		for(String nextColumnName : generatedColumnNames)
		{
			String nextVar = nextColumnName.substring(nextColumnName.indexOf("_") + 1);
			colNameStr += ", " + tableVar + "." + nextVar + " AS " + nextColumnName;
		}
			
		return colNameStr.substring(2);
	}
}