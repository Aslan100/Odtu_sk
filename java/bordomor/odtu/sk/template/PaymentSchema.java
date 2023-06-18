package bordomor.odtu.sk.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Vector;

import org.w3c.dom.Element;

import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.PaymentFailureAction;
import bordomor.odtu.sk.Params.PaymentSchemaType;
import bordomor.odtu.sk.Params.SubscriptionCancellationAction;
import bordomor.odtu.sk.Params.TimeUnit;
import bordomor.odtu.sk.payment.EquidistributedSchema;
import bordomor.odtu.sk.payment.IntervalConstrainedSchema;
import bordomor.odtu.sk.payment.PaymentPlan;
import bordomor.util.DBUtils;

public abstract class PaymentSchema extends DBTimeTrackable implements IDBObject, IXmlObject 
{
	protected int schemaId = -1;
	protected String title = null;
	protected Branch branch = null;
	protected PaymentSchemaType type  = null;
	protected short paymentPeriod = -1;
	protected TimeUnit periodUnit = null;
	protected short paymentDayIndex = -1;
	protected short paymentDaySubindex = -1;
	protected short maxUnpaidDays = -1;
	protected PaymentFailureAction failureAction = null;
	protected SubscriptionCancellationAction cancellationAction = null;
	protected boolean autoRenew = false;
	protected short maxCancellableDays = -1;
	protected boolean isPriceModificationProtected = false;
	
	protected PaymentSchema() 
	{
		super();
	}
	
	protected PaymentSchema(int id, String title, Branch branch, PaymentSchemaType type)
	{
		super();
		this.schemaId = id;
		this.title = title;
		this.branch = branch;
		this.type = type;
	}
	
	//Soyut Metodlar
	public abstract int getId();
	public abstract PaymentPlan generatePlan(boolean isSiblingPlan);
	
	//Yardımcı Metodlar
	public int[] getHumanReadableTimeDifference(LocalDate initialDate, LocalDate finalDate, TimeUnit mainMeasureUnit)
	{
		int[] diff = new int[2];
		LocalDate endDate = finalDate.plusDays(1);
		LocalDate startDate = initialDate.plusDays(0);
		
		int counter = 0;
		TimeUnit measureUnit = mainMeasureUnit;
		
		while(true)
		{
			counter++;
			LocalDate nextDate = startDate.plus(counter, measureUnit.getChronoUnit());
			
			if(measureUnit == mainMeasureUnit && nextDate.isAfter(endDate))
			{
				diff[0] = counter - 1;
				startDate = initialDate.plus(diff[0], TimeUnit.MONTHS.getChronoUnit());
				measureUnit = TimeUnit.DAYS;
				counter = 0;
			}
			else if(measureUnit == TimeUnit.DAYS && nextDate.isAfter(endDate))
			{
				diff[1] = counter - 1;
				break;
			}
		}
		
		return diff;
	}
	
	//XML Bölümü
	@Override
	public void parseFromXMLElement(Element element) {}

	protected void appendSchemaAttributes(Element schemaEl, boolean cascadeRelations)
	{
		schemaEl.setAttribute("schema_id", this.schemaId + "");
		
		if(this.title != null)
			schemaEl.setAttribute("title", this.title);
		
		schemaEl.setAttribute("payment_period", this.paymentPeriod + "");
		
		if(this.periodUnit != null)
			schemaEl.setAttribute("period_unit", this.periodUnit.toString());
		
		schemaEl.setAttribute("payment_day_index", this.paymentDayIndex + "");
		schemaEl.setAttribute("payment_day_subindex", this.paymentDaySubindex + "");
		schemaEl.setAttribute("max_unpaid_days", this.maxUnpaidDays + "");
		
		if(this.failureAction != null)
			schemaEl.setAttribute("failure_action", this.failureAction.toString());
		
		if(this.cancellationAction != null)
			schemaEl.setAttribute("cancellation_action", this.cancellationAction.toString());
		
		schemaEl.setAttribute("auto_renew", this.autoRenew + "");
		schemaEl.setAttribute("max_cancellable_days", this.maxCancellableDays + "");
		schemaEl.setAttribute("is_price_modification_protected", this.isPriceModificationProtected + "");
		
		if(cascadeRelations && this.branch != null)
			schemaEl.appendChild(this.branch.makeXMLElement(schemaEl.getOwnerDocument(), true));
		
		this.appendTTAttributes(schemaEl);
	}
	
	//VT Bölümü
	@Override
	public void createInDB(Connection conn) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
        PreparedStatement insertSt = null;
        ResultSet keySet = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	       String insertQuery = ""
    				+ "INSERT INTO "
    				+ "		payment_schema "
    				+ "			(title, branch, type, payment_period, period_unit, payment_day_index, payment_day_subindex, "
    				+ "				max_unpaid_days, failure_action, cancellation_action, auto_renew, max_cancellable_days, is_price_modification_protected) "
    				+ "VALUES "
    				+ "		(?, ?, ?::payment_schema_type, ?, ?::time_unit, ?, ?, ?, ?::payment_failure_action, ?::subscription_cancellation_action, ?, ?, ?)";
        	
    		insertSt = newConn.prepareStatement(insertQuery, new String[] { "id", "creation_time", "last_modified_time" });
    		insertSt.setString(1, this.title.toString());
    		insertSt.setInt(2, this.branch.getId());
    		insertSt.setString(3, this.type.toString());
    		insertSt.setShort(4, this.paymentPeriod);
    		insertSt.setString(5, this.periodUnit.toString());
    		insertSt.setShort(6, this.paymentDayIndex);
    		insertSt.setShort(7, this.paymentDaySubindex);
    		insertSt.setShort(8, this.maxUnpaidDays);
    		insertSt.setString(9, this.failureAction.toString());
    		insertSt.setString(10, this.cancellationAction.toString());
    		insertSt.setBoolean(11, this.autoRenew);
    		insertSt.setShort(12, this.maxCancellableDays);
    		insertSt.setBoolean(13, this.isPriceModificationProtected);
    		insertSt.executeUpdate();
        	
    		keySet = insertSt.getGeneratedKeys();
    		keySet.next();
    		this.setSchemaId(keySet.getInt("id"));
    		this.parseTTAttributes(keySet);
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
		
	@Override
	public void deleteFromDB(Connection conn) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
        PreparedStatement sqlSt = null;
        
        try
        {
	        if(newConn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
			String deleteQuery = ""
					+ "DELETE FROM payment_schema WHERE id = ?";
	        
	        sqlSt = newConn.prepareStatement(deleteQuery);
	        sqlSt.setInt(1, this.schemaId);
	        
	        if(sqlSt.executeUpdate() != 1)
	        	throw new IllegalArgumentException();
	        
	        this.schemaId = -1;
	    }
        finally
        {
        	DBUtils.close(sqlSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
    }
		
	//Get-Set
	public int getSchemaId() 
	{
		return schemaId;
	}
	
	public void setSchemaId(int schemaId) 
	{
		this.schemaId = schemaId;
	}
	
	public String getTitle() 
	{
		return title;
	}
	
	public void setTitle(String title) 
	{
		this.title = title;
	}
	
	public Branch getBranch() 
	{
		return branch;
	}

	public void setBranch(Branch branch) 
	{
		this.branch = branch;
	}

	public PaymentSchemaType getType() 
	{
		return type;
	}
	
	public void setType(PaymentSchemaType type) 
	{
		this.type = type;
	}
	
	public short getPaymentPeriod() 
	{
		return paymentPeriod;
	}
	
	public void setPaymentPeriod(short paymentPeriod) 
	{
		this.paymentPeriod = paymentPeriod;
	}
	
	public TimeUnit getPeriodUnit() 
	{
		return periodUnit;
	}
	
	public void setPeriodUnit(TimeUnit periodUnit) 
	{
		this.periodUnit = periodUnit;
	}
	
	public short getPaymentDayIndex() 
	{
		return paymentDayIndex;
	}
	
	public void setPaymentDayIndex(short paymentDayIndex) 
	{
		this.paymentDayIndex = paymentDayIndex;
	}
	
	public short getPaymentDaySubindex() 
	{
		return paymentDaySubindex;
	}
	
	public void setPaymentDaySubindex(short paymentDaySubindex) 
	{
		this.paymentDaySubindex = paymentDaySubindex;
	}
	
	public short getMaxUnpaidDays() 
	{
		return maxUnpaidDays;
	}
	
	public void setMaxUnpaidDays(short maxUnpaidDays) 
	{
		this.maxUnpaidDays = maxUnpaidDays;
	}
	
	public PaymentFailureAction getFailureAction() 
	{
		return failureAction;
	}
	
	public void setFailureAction(PaymentFailureAction failureAction) 
	{
		this.failureAction = failureAction;
	}
	
	public SubscriptionCancellationAction getCancellationAction() 
	{
		return cancellationAction;
	}
	
	public void setCancellationAction(SubscriptionCancellationAction cancellationAction) 
	{
		this.cancellationAction = cancellationAction;
	}
	
	public boolean isAutoRenew() 
	{
		return autoRenew;
	}
	
	public void setAutoRenew(boolean autoRenew) 
	{
		this.autoRenew = autoRenew;
	}
	
	public short getMaxCancellableDays() 
	{
		return maxCancellableDays;
	}
	
	public void setMaxCancellableDays(short maxCancellableDays) 
	{
		this.maxCancellableDays = maxCancellableDays;
	}
	
	public boolean isPriceModificationProtected() 
	{
		return isPriceModificationProtected;
	}
	
	public void setPriceModificationProtected(boolean isPriceModificationProtected) 
	{
		this.isPriceModificationProtected = isPriceModificationProtected;
	}

	public void setTimingData(short paymentPeriod, TimeUnit periodUnit, short paymentDayIndex, short paymentDaySubindex, short maxUnpaidDays, short maxCancellableDays)
	{
		this.paymentPeriod = paymentPeriod;
		this.periodUnit = periodUnit;
		this.paymentDayIndex = paymentDayIndex;
		this.paymentDaySubindex = paymentDaySubindex;
		this.maxUnpaidDays = maxUnpaidDays;
		this.maxCancellableDays = maxCancellableDays;
	}
	
	public void setBehaviour(PaymentFailureAction failureAction, SubscriptionCancellationAction cancellationAction, boolean autoRenew, boolean isPriceModificationProtectedPlan)
	{
		this.failureAction = failureAction;
		this.cancellationAction = cancellationAction;
		this.autoRenew = autoRenew;
		this.isPriceModificationProtected = isPriceModificationProtectedPlan;
	}
	
	//Statik Metodlar
	private static String[] generateColumnNames(String tableVar)
	{
		if(tableVar == null)
			throw new IllegalArgumentException("Bad table variable.");
		
		String[] colNames = new String[16];
		
		if(tableVar != null)
		{
			int columnIndex = 0;
			
			colNames[columnIndex] = tableVar + "_id"; columnIndex++;
			colNames[columnIndex] = tableVar + "_title"; columnIndex++;
			colNames[columnIndex] = tableVar + "_branch"; columnIndex++;
			colNames[columnIndex] = tableVar + "_type"; columnIndex++;
			colNames[columnIndex] = tableVar + "_payment_period"; columnIndex++;
			colNames[columnIndex] = tableVar + "_period_unit"; columnIndex++;
			colNames[columnIndex] = tableVar + "_payment_day_index"; columnIndex++;
			colNames[columnIndex] = tableVar + "_payment_day_subindex"; columnIndex++;
			colNames[columnIndex] = tableVar + "_max_unpaid_days"; columnIndex++;
			colNames[columnIndex] = tableVar + "_failure_action"; columnIndex++;
			colNames[columnIndex] = tableVar + "_cancellation_action"; columnIndex++;
			colNames[columnIndex] = tableVar + "_auto_renew"; columnIndex++;
			colNames[columnIndex] = tableVar + "_max_cancellable_days"; columnIndex++;
			colNames[columnIndex] = tableVar + "_is_price_modification_protected"; columnIndex++;
			colNames[columnIndex] = tableVar + "_creation_time"; columnIndex++;
			colNames[columnIndex] = tableVar + "_last_modified_time"; columnIndex++;
		}
		
		return colNames;
	}
	
	public static String generateColumnNameString(TypeDefinition[] includedTypes, String schemaTableVar)
	{
		if(includedTypes == null || includedTypes.length == 0 || schemaTableVar == null)
			throw new IllegalArgumentException("Bad table or subtype variables.");
		
		String colNameStr = "";
		
		for(TypeDefinition nextDefinition : includedTypes)
		{
			for(String nextColStr : nextDefinition.getTypeColumns())
			{
				String nextTableVar = nextDefinition.getTableVariable();
				String orgColStr = nextColStr.substring(nextTableVar.length() + 1);
				colNameStr += ", " + nextTableVar + "." + orgColStr + " AS " + nextColStr;
			}
		}
		
		for(String nextSchemaColStr : PaymentSchema.generateColumnNames(schemaTableVar))
		{
			String orgColStr = nextSchemaColStr.substring(nextSchemaColStr.indexOf("_") + 1);
			colNameStr += ", " + schemaTableVar + "." + orgColStr + " AS " + nextSchemaColStr;
		}
		
		colNameStr = colNameStr.substring(2);
		
		return colNameStr;
	}
	
	public static PaymentSchema parseFromRecord(ResultSet rs, TypeDefinition[] typeDefinitions, String schemaTableVar)
			throws ClassNotFoundException, SQLException
	{
		PaymentSchema schema = null;
		
		for(TypeDefinition nextDef : typeDefinitions)
		{
			Class<? extends PaymentSchema> nextType = nextDef.getType();
			String nextTableVar = nextDef.getTableVariable();
			
			int nextTypeId = rs.getInt(nextTableVar + "_id");
			
			if(nextType == IntervalConstrainedSchema.class && nextTypeId > 0)
				schema = IntervalConstrainedSchema.parseFromRecord(rs, nextDef);
	    	else if(nextType == EquidistributedSchema.class && nextTypeId > 0)
	    		schema = EquidistributedSchema.parseFromRecord(rs, nextDef);
			
			if(schema != null)
			{
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals(schemaTableVar + "_id"))
						schema.setSchemaId(rs.getInt(nextCol));
					else if(nextCol.equals(schemaTableVar + "_title"))
						schema.setTitle(rs.getString(nextCol));
					else if(nextCol.equals(schemaTableVar + "_branch"))
						schema.setBranch(Branch.findById_IncludePromotions(rs.getStatement().getConnection(), rs.getInt(nextCol)));
					else if(nextCol.equals(schemaTableVar + "_type"))
						schema.setType(PaymentSchemaType.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals(schemaTableVar + "_payment_period"))
						schema.setPaymentPeriod(rs.getShort(nextCol));
					else if(nextCol.equals(schemaTableVar + "_period_unit"))
						schema.setPeriodUnit(TimeUnit.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals(schemaTableVar + "_payment_day_index"))
						schema.setPaymentDayIndex(rs.getShort(nextCol));
					else if(nextCol.equals(schemaTableVar + "_payment_day_subindex"))
						schema.setPaymentDaySubindex(rs.getShort(nextCol));
					else if(nextCol.equals(schemaTableVar + "_max_unpaid_days"))
						schema.setMaxUnpaidDays(rs.getShort(nextCol));
					else if(nextCol.equals(schemaTableVar + "_failure_action"))
						schema.setFailureAction(PaymentFailureAction.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals(schemaTableVar + "_cancellation_action"))
						schema.setCancellationAction(SubscriptionCancellationAction.valueOf(rs.getString(nextCol)));
					else if(nextCol.equals(schemaTableVar + "_auto_renew"))
						schema.setAutoRenew(rs.getBoolean(nextCol));
					else if(nextCol.equals(schemaTableVar + "_max_cancellable_days"))
						schema.setMaxCancellableDays(rs.getShort(nextCol));
					else if(nextCol.equals(schemaTableVar + "_is_price_modification_protected"))
						schema.setPriceModificationProtected(rs.getBoolean(nextCol));
				}
				
				schema.parseTTAttributes(rs, schemaTableVar + "_creation_time", schemaTableVar + "_last_modified_time");
				
				break;
			}
		}
		
		return schema;
	}
	
	//Statik Sorgular
	public static PaymentSchema findById(Connection conn, int id) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			TypeDefinition[] typeDefinitions = new TypeDefinition[] 
			{
				new TypeDefinition(IntervalConstrainedSchema.class, "int_sch"),
				new TypeDefinition(EquidistributedSchema.class, "eq_sch")
			};
			String schemaTableVar = "sch";
			
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = ""
					+ "SELECT "
					+ 		PaymentSchema.generateColumnNameString(typeDefinitions, schemaTableVar) + " "
					+ "FROM "
					+ "		payment_schema sch "
					+ "			LEFT OUTER JOIN interval_constrained_schema int_sch "
					+ "				ON (int_sch.schema_parent = sch.id) "
					+ "			LEFT OUTER JOIN equidistributed_schema eq_sch "
					+ "				ON (eq_sch.schema_parent = sch.id) "
					+ "WHERE "
					+ "		sch.id = ?";
			
			sqlSt = newConn.prepareStatement(query);
	        sqlSt.setInt(1, id);
	        rs = sqlSt.executeQuery();
	        
	        PaymentSchema schema = null;
	        
	        if(rs.next())
	        	schema = PaymentSchema.parseFromRecord(rs, typeDefinitions, schemaTableVar);
	        
			return schema;
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static PaymentSchema[] findAll(Connection conn) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			TypeDefinition[] typeDefinitions = new TypeDefinition[] 
			{
				new TypeDefinition(IntervalConstrainedSchema.class, "int_sch"),
				new TypeDefinition(EquidistributedSchema.class, "eq_sch")
			};
			String schemaTableVar = "sch";
			
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = ""
					+ "SELECT "
					+ 		PaymentSchema.generateColumnNameString(typeDefinitions, schemaTableVar) + " "
					+ "FROM "
					+ "		payment_schema sch "
					+ "			LEFT OUTER JOIN interval_constrained_schema int_sch "
					+ "				ON (int_sch.schema_parent = sch.id) "
					+ "			LEFT OUTER JOIN equidistributed_schema eq_sch "
					+ "				ON (eq_sch.schema_parent = sch.id) "
					+ "ORDER BY "
					+ "		sch_id ASC";
			
			sqlSt = newConn.prepareStatement(query);
	        rs = sqlSt.executeQuery();
	        
	        Vector<PaymentSchema> schemas = new Vector<PaymentSchema>(0);
	        
	        while(rs.next())
	        	schemas.add(PaymentSchema.parseFromRecord(rs, typeDefinitions, schemaTableVar));
	        
	        if(schemas.size() > 0)
	        	schemas.trimToSize();
	        
			return schemas.isEmpty() ? null : schemas.toArray(new PaymentSchema[schemas.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	public static PaymentSchema[] findByBranch(Connection conn, Branch branch) throws ClassNotFoundException, SQLException
	{
		Connection newConn = conn;
		PreparedStatement sqlSt = null;
		ResultSet rs = null;
		
		try
		{
			TypeDefinition[] typeDefinitions = new TypeDefinition[] 
			{
				new TypeDefinition(IntervalConstrainedSchema.class, "int_sch"),
				new TypeDefinition(EquidistributedSchema.class, "eq_sch")
			};
			String schemaTableVar = "sch";
			
			if(conn == null)
				newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			String query = ""
					+ "SELECT "
					+ 		PaymentSchema.generateColumnNameString(typeDefinitions, schemaTableVar) + " "
					+ "FROM "
					+ "		payment_schema sch "
					+ "			LEFT OUTER JOIN interval_constrained_schema int_sch "
					+ "				ON (int_sch.schema_parent = sch.id) "
					+ "			LEFT OUTER JOIN equidistributed_schema eq_sch "
					+ "				ON (eq_sch.schema_parent = sch.id) "
					+ "WHERE "
					+ "		sch.branch = ? "
					+ "ORDER BY "
					+ "		sch.id ASC";
			
			sqlSt = newConn.prepareStatement(query);
			sqlSt.setInt(1, branch.getId());
	        rs = sqlSt.executeQuery();
	        
	        Vector<PaymentSchema> schemas = new Vector<PaymentSchema>(0);
	        
	        while(rs.next())
	        	schemas.add(PaymentSchema.parseFromRecord(rs, typeDefinitions, schemaTableVar));
	        
	        if(schemas.size() > 0)
	        	schemas.trimToSize();
	        
			return schemas.isEmpty() ? null : schemas.toArray(new PaymentSchema[schemas.size()]);
		}
		finally
		{
			DBUtils.close(rs);
			DBUtils.close(sqlSt);
			
			if(conn == null)
				DBUtils.close(newConn);
		}
	}
	
	//İç Sınıflar
	public static class TypeDefinition
	{
		private Class<? extends PaymentSchema> type = null;
		private String tableVariable = null;
		
		public TypeDefinition(Class<? extends PaymentSchema> type, String tableVariable)
		{
			this.type = type;
			this.tableVariable = tableVariable;
		}
		
		//Get-Set
		public Class<? extends PaymentSchema> getType() 
		{
			return type;
		}

		public void setType(Class<? extends PaymentSchema> type) 
		{
			this.type = type;
		}

		public String getTableVariable() 
		{
			return tableVariable;
		}

		public void setTableVariable(String tableVariable) 
		{
			this.tableVariable = tableVariable;
		}
		
		public String[] getTypeColumns()
		{
			String[] typeCols = null;
			
			if(this.type == IntervalConstrainedSchema.class) 
				return IntervalConstrainedSchema.generateColumnNames(this.tableVariable);
			else if(this.type == EquidistributedSchema.class)
				return EquidistributedSchema.generateColumnNames(this.tableVariable);
			
			return typeCols;
		}
	}
}