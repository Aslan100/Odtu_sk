package bordomor.odtu.sk.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.PaymentSchemaType;
import bordomor.odtu.sk.Params.TimeUnit;
import bordomor.odtu.sk.template.PaymentSchema;
import bordomor.util.DBUtils;

public class IntervalConstrainedSchema extends PaymentSchema 
{
	public int id = -1;
	public String intervalStart = null;
	public String intervalEnd = null;
	
	public IntervalConstrainedSchema() 
	{
		super();
	}
	
	public IntervalConstrainedSchema(int id)
	{
		this.id = id;
	}
	
	public IntervalConstrainedSchema(int id, String intervalStart, String intervalEnd, int schemaId, String title, Branch branch, PaymentSchemaType type)
	{
		super(schemaId, title, branch, type);
		this.id = id;
		this.intervalStart = intervalStart;
		this.intervalEnd = intervalEnd;
	}
	
	//Yardımcı Metodlar
	@Override
	public PaymentPlan generatePlan(boolean isSiblingPlan) 
	{ 
		LocalDate minDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
		LocalDate maxDate = LocalDate.of(LocalDate.now().getYear() + 1, 12, 31);
		int multiplier = 0;
		
		Vector<LocalDate[]> periods = new Vector<LocalDate[]>(0);
		
		LocalDate planStart = LocalDate.of(LocalDate.now().getYear(), Integer.parseInt(this.intervalStart.split("/")[1]), Integer.parseInt(this.intervalStart.split("/")[0]));
		LocalDate planEnd = LocalDate.of(LocalDate.now().getYear(), Integer.parseInt(this.intervalEnd.split("/")[1]), Integer.parseInt(this.intervalEnd.split("/")[0]));
		
		if(planEnd.isBefore(planStart))
			planEnd = planEnd.plusYears(1);
		
		while(true)
		{
			LocalDate periodStart = minDate.plus(this.paymentPeriod*multiplier, this.periodUnit.getChronoUnit());
			
			if(periodStart.isAfter(maxDate))
				break;
			
			LocalDate[] periodDates = new LocalDate[3];
			periodDates[0] = periodStart;
			
			LocalDate periodEnd = minDate.plus(this.paymentPeriod*(multiplier + 1), this.periodUnit.getChronoUnit());
			periodEnd = periodEnd.minusDays(1);
			periodDates[1] = periodEnd;
			
			LocalDate payDay = periodStart.plus((this.paymentDayIndex - 1), this.periodUnit.getChronoUnit());
			payDay = payDay.withDayOfMonth(this.paymentDaySubindex);
			periodDates[2] = payDay;
			
			periods.add(periodDates);
			multiplier++;
		}
		
		int max = periods.size();
		
		if(LocalDate.now().isBefore(planStart))
		{
			for(int i = 0; i < max; i++)
			{
				LocalDate[] nextPeriod = periods.get(i);
				
				if(nextPeriod[1].isBefore(planStart) || nextPeriod[0].isAfter(planEnd))
				{
					periods.remove(i);
					i--; max--;
				}
				else if(nextPeriod[0].isBefore(planStart) && nextPeriod[1].isAfter(planStart))
				{
					nextPeriod[0] = planStart;
					
					if(nextPeriod[2].isBefore(planStart))
						nextPeriod[2] = planStart; 
				}
				else if(nextPeriod[0].isBefore(planEnd) && nextPeriod[1].isAfter(planEnd))
				{
					nextPeriod[1] = planEnd;
					
					if(nextPeriod[2].isAfter(planEnd))
						nextPeriod[2] = planEnd; 
				}
			}
			
			periods.get(0)[2] = LocalDate.now();
		} 
		else
		{
			for(int i = 0; i < max; i++)
			{
				LocalDate[] nextPeriod = periods.get(i);
				
				if(nextPeriod[1].isBefore(LocalDate.now()) || nextPeriod[0].isAfter(planEnd))
				{
					periods.remove(i);
					i--; max--;
				}
				else if(nextPeriod[0].isBefore(LocalDate.now()) && nextPeriod[1].isAfter(LocalDate.now()))
				{
					nextPeriod[0] = LocalDate.now();
					nextPeriod[2] = LocalDate.now();
				}
				else if(nextPeriod[0].isBefore(planEnd) && nextPeriod[1].isAfter(planEnd))
				{
					nextPeriod[1] = planEnd;
					
					if(nextPeriod[2].isAfter(planEnd))
						nextPeriod[2] = planEnd; 
				}
			}
		}
		
		Vector<Payment> payments = new Vector<Payment>(periods.size());
		
		for(LocalDate[] nextPeriod : periods)
		{
			int[] diff = this.getHumanReadableTimeDifference(nextPeriod[0], nextPeriod[1], this.periodUnit);
			
			Timestamp periodStart = Timestamp.valueOf(nextPeriod[0].atStartOfDay());
			Timestamp periodEnd = Timestamp.valueOf(nextPeriod[1].atStartOfDay().minusNanos(1));
			
			float actualAmount = diff[0]*this.branch.getPrice(this.periodUnit) + diff[1]*this.branch.getPrice(TimeUnit.DAYS);
			float amount = actualAmount;
			float discount = -1;
			
			payments.add(new Payment(-1, null, periodStart, periodEnd, actualAmount, discount, amount, Timestamp.valueOf(nextPeriod[2].atStartOfDay().plusDays(1).minusNanos(1))));
		}
		
		PaymentPlan generatedPlan = new PaymentPlan();
		generatedPlan.setPayments(payments.toArray(new Payment[payments.size()]));
		
		if(isSiblingPlan)
			generatedPlan.addPromotion(this.branch.getSiblingPromotion());
		
		return generatedPlan;
	}
	
	//XML Bölümü
	@Override
	public Element makeXMLElement(Document xml, boolean cascadeRelations) 
	{
		try
		{
			Element schemaEl = xml.createElement("interval_constrained_schema");
			schemaEl.setAttribute("id", this.id + "");
			
			if(this.intervalStart != null)
				schemaEl.setAttribute("interval_start", this.intervalStart);
			
			if(this.intervalEnd != null)
				schemaEl.setAttribute("interval_end", this.intervalEnd);
			
			super.appendSchemaAttributes(schemaEl, cascadeRelations);
			
			return schemaEl;
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
        this.schemaId = -1;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        this.type = PaymentSchemaType.INTERVAL_CONSTRAINED;
	        super.createInDB(newConn);
	        
			String insertQuery = ""
					+ "INSERT INTO "
					+ "		interval_constrained_schema "
					+ "			(interval_start, interval_end, schema_parent) "
					+ "VALUES "
					+ "		(?, ?, ?)";
			
			insertSt = newConn.prepareStatement(insertQuery, new String[] { "id" });
			insertSt.setObject(1, this.intervalStart != null ? this.intervalStart.trim() : null, Types.VARCHAR);
			insertSt.setString(2, this.intervalEnd.trim());
			insertSt.setInt(3, this.schemaId);
			
			if(insertSt.executeUpdate() != 1)
				throw new IllegalArgumentException();
				
			keySet = insertSt.getGeneratedKeys();
			keySet.next();
		    this.setId(keySet.getInt("id"));
		}
        catch(Exception ex)
        {
        	if(this.schemaId != -1)
        		super.deleteFromDB(newConn);
        	
        	throw ex;
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
	public void deleteFromDB(Connection conn) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
        PreparedStatement deleteSt = null;
        
        try
        {
	        if(newConn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        String deleteQuery = ""
	        		+ "DELETE FROM "
	        		+ "		payment_schema "
	        		+ "WHERE "
	        		+ "		id = (SELECT schema_parent FROM interval_constrained_schema WHERE id = ?)";
	        
			deleteSt = newConn.prepareStatement(deleteQuery);
			deleteSt.setInt(1, this.id);
	        
	        if(deleteSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        this.id = -1;
	        this.schemaId = -1;
	        this.creationTime = null;
	        this.lastModifiedTime = null;
	    }
        finally
        {
        	DBUtils.close(deleteSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	//Get-Set
	@Override
	public int getId() 
	{
		return id;
	}
	
	public void setId(int id) 
	{
		this.id = id;
	}
	
	public String getIntervalStart() 
	{
		return intervalStart;
	}
	
	public void setIntervalStart(String intervalStart) 
	{
		this.intervalStart = intervalStart;
	}
	
	public String getIntervalEnd() 
	{
		return intervalEnd;
	}
	
	public void setIntervalEnd(String intervalEnd) 
	{
		this.intervalEnd = intervalEnd;
	}
	
	//Statik Metodlar
	public static String[] generateColumnNames(String tableVar)
	{
		if(tableVar == null)
			throw new IllegalArgumentException("Bad table variable.");
		
		String[] colNames = new String[3];
		
		if(tableVar != null)
		{
			int columnIndex = 0;
			
			colNames[columnIndex] = tableVar + "_id"; columnIndex++;
			colNames[columnIndex] = tableVar + "_interval_start"; columnIndex++;
			colNames[columnIndex] = tableVar + "_interval_end"; columnIndex++;
		}
		
		return colNames;
	}
	
	public static IntervalConstrainedSchema parseFromRecord(ResultSet rs, TypeDefinition typeDefinition)
	{
		try
		{
			Class<? extends PaymentSchema> type = typeDefinition.getType();
			String tableVar = typeDefinition.getTableVariable();
			
			if(type != IntervalConstrainedSchema.class || tableVar == null)
				throw new Exception("Bad type definition.");
			
			IntervalConstrainedSchema parsedSchema = null;
			int id = rs.getInt(tableVar + "_id");  
			
			if(!rs.wasNull())
			{
				parsedSchema = new IntervalConstrainedSchema(id);
				parsedSchema.setIntervalStart(rs.getString(tableVar + "_interval_start"));
				parsedSchema.setIntervalEnd(rs.getString(tableVar + "_interval_end"));
			}
				
			return parsedSchema;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
}