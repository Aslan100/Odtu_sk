package bordomor.odtu.sk.payment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bordomor.odtu.sk.Params.PaymentState;
import bordomor.odtu.sk.template.IDBObject;
import bordomor.odtu.sk.template.IXmlObject;
import bordomor.odtu.sk.template.XMLAndDatabaseValues;
import bordomor.util.DBUtils;

@XMLAndDatabaseValues(tableName = "payment", tagName = "payment", defaultVariable = "py")
public class Payment implements IXmlObject , IDBObject
{
	private int id = -1;
	String code = null;
	private Timestamp periodStart = null;
	private Timestamp periodEnd = null;
	private float actualAmount = -1f;
	private float discount = -1f;
	private float amount = -1f;
	private Timestamp dueDate = null;
	private float paidAmount = -1f;
	private Timestamp paidDate = null;
	private boolean isPacified = false;
	
	public Payment() {}

	public  Payment(int id)
	{
		this.id = id;
	}
	
	public Payment(int id, String code, float amount, Timestamp dueDate)
	{
		this.id = id;
		this.code = code;
		this.amount = amount;
		this.dueDate = dueDate;
	}
	
	public Payment(int id, String code, float actualAmount, float discount, float amount, Timestamp dueDate)
	{
		this.id = id;
		this.code = code;
		this.actualAmount = actualAmount;
		this.discount = discount;
		this.amount = amount;
		this.dueDate = dueDate;
	}
	
	public Payment(int id, String code, Timestamp periodStart, Timestamp periodEnd, float actualAmount, float discount, float amount, Timestamp dueDate)
	{
		this.id = id;
		this.code = code;
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
		this.actualAmount = actualAmount;
		this.discount = discount;
		this.amount = amount;
		this.dueDate = dueDate;
	}
	
	//Yardımcı Metodlar
	@Override
	public boolean equals(Object comparedObject) 
	{
		try
		{
			if(comparedObject == this)
				return true;
			else if(comparedObject == null || !(comparedObject instanceof Payment))
				return false;
			else
			{
				Payment comparedPayment = (Payment) comparedObject;
				
				return this.id == comparedPayment.id
						&& ((this.code == null && comparedPayment.code == null) || this.code.equals(comparedPayment.code))
						&& this.amount == comparedPayment.amount
						&& ((this.dueDate == null && comparedPayment.dueDate == null) || this.dueDate.equals(comparedPayment.dueDate));
						
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
        final int prime = 29;
        
        int result = super.hashCode();
        result = prime*result + (int) (this.id ^ (this.id >>> 32));
        result = prime*result + this.code == null ? 0 : this.code.hashCode();
        result = prime*result + (int) this.actualAmount;
        result = prime*result + (int) this.discount;
        result = prime*result + (int) this.amount;
        result = prime*result + (this.dueDate == null ? 0 : this.dueDate.hashCode());
        result = prime*result + (int) this.paidAmount;
        result = prime*result + (this.paidDate == null ? 0 : this.paidDate.hashCode());
        result = prime*result + (int) (this.isPacified ? 1 : 0);
        
        return result;
    }
	
	//XML Bölümü
	@Override
	public void parseFromXMLElement(Element element) 
	{
		try
		{
			this.id = Integer.parseInt(element.getAttribute("id"));
			this.code = element.hasAttribute("code") ? element.getAttribute("code") : null;
			this.periodStart = new Timestamp(Long.parseLong(element.getAttribute("period_start")));
			this.periodEnd = new Timestamp(Long.parseLong(element.getAttribute("period_end")));
			this.actualAmount = Float.parseFloat(element.getAttribute("actual_amount"));
			this.discount = Float.parseFloat(element.getAttribute("discount"));
			this.amount = Float.parseFloat(element.getAttribute("amount"));
			this.dueDate = element.hasAttribute("due_date") ? new Timestamp(Long.parseLong(element.getAttribute("due_date"))) : null;
			this.paidAmount = Float.parseFloat(element.getAttribute("paid_amount"));
			this.paidDate = element.hasAttribute("paid_date") ? new Timestamp(Long.parseLong(element.getAttribute("paid_date"))) : null;
			this.isPacified = Boolean.parseBoolean(element.getAttribute("is_pacified"));
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
			String tagName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tagName();
			
			Element paymentEl = xml.createElement(tagName);
			paymentEl.setAttribute("id", this.id + "");
			paymentEl.setAttribute("code", this.code + "");
			paymentEl.setAttribute("actual_amount", this.actualAmount + "");
			paymentEl.setAttribute("discount", this.discount + "");
			paymentEl.setAttribute("amount", this.amount + "");
			paymentEl.setAttribute("paid_amount", this.paidAmount + "");
			paymentEl.setAttribute("is_pacified", this.isPacified + "");
			paymentEl.setAttribute("state", this.getState().toString());
			
			if(this.periodStart != null)
				paymentEl.setAttribute("period_start", this.periodStart.getTime() + "");
			
			if(this.periodEnd != null)
				paymentEl.setAttribute("period_end", this.periodEnd.getTime() + "");
			
			if(this.dueDate != null)
				paymentEl.setAttribute("due_date", this.dueDate.getTime() + "");
			
			if(this.paidDate != null)
				paymentEl.setAttribute("paid_date", this.paidDate.getTime() + "");
			
			return paymentEl;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad xml document or object properties.", ex);
		}
	}
	
	//VT Bölümü
	@Override
	public void createInDB(Connection conn) throws ClassNotFoundException, SQLException {}
	
	@Override
	public void updateInDB(Connection conn, IDBObject newObject) throws ClassNotFoundException, SQLException {}

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
	
	public Timestamp getPeriodStart() 
	{
		return periodStart;
	}
	
	public void setPeriodStart(Timestamp periodStart) 
	{
		this.periodStart = periodStart;
	}
	
	public Timestamp getPeriodEnd() 
	{
		return periodEnd;
	}
	
	public void setPeriodEnd(Timestamp periodEnd) 
	{
		this.periodEnd = periodEnd;
	}
	
	public float getActualAmount() 
	{
		return actualAmount;
	}

	public void setActualAmount(float actualAmount) 
	{
		this.actualAmount = actualAmount;
	}
	
	public float getDiscount() 
	{
		return discount;
	}

	public void setDiscount(float discount) 
	{
		this.discount = discount;
	}
	
	public float getAmount() 
	{
		return amount;
	}

	public void setAmount(float amount) 
	{
		this.amount = amount;
	}
	
	public Timestamp getDueDate() 
	{
		return dueDate;
	}
	
	public void setDueDate(Timestamp dueDate) 
	{
		this.dueDate = dueDate;
	}
	
	public float getPaidAmount() 
	{
		return paidAmount;
	}

	public void setPaidAmount(float paidAmount) 
	{
		this.paidAmount = paidAmount;
	}
	
	public Timestamp getPaidDate() 
	{
		return paidDate;
	}
	
	public void setPaidDate(Timestamp paidDate) 
	{
		this.paidDate = paidDate;
	}
	
	public boolean isPacified()
	{
		return isPacified;
	}
	
	public void setPacified(boolean isPacified)
	{
		this.isPacified = isPacified;
	}
	
	public PaymentState getState()
	{
		try
		{
			LocalDateTime dueDT = this.dueDate.toLocalDateTime();
			LocalDateTime paidDT = this.paidDate != null ? this.paidDate.toLocalDateTime() : null;
			LocalDateTime endOfToday = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999);
			
			if(paidDT == null)
			{
				if(dueDT.isAfter(endOfToday))
					return PaymentState.PENDING;
				else
					return PaymentState.NO_PAYMENT;
			}
			else
			{
				if(this.paidAmount < this.amount)
					return PaymentState.PARTIAL_PAYMENT;
				else if((paidDT.isBefore(dueDT) || paidDT.isEqual(dueDT)) && this.paidAmount == this.amount)
					return PaymentState.PAID;
				else if(paidDT.isAfter(dueDT) && this.paidAmount == this.amount)
					return PaymentState.LATE_PAYMENT;
				
				return PaymentState.NO_PAYMENT;
			}
		}
		catch(Exception ex)
		{
			return PaymentState.NO_PAYMENT;
		}
	}
	
	//Statik Metodlar
 	public static Payment parseFromRecord(ResultSet rs)
	{
		try
		{
			Payment parsedPayment = null;
			int paymentId = rs.getInt("id");  
			
			if(!rs.wasNull())
			{
				parsedPayment = new Payment(paymentId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals("code"))
						parsedPayment.setCode(rs.getString(nextCol));
					else if(nextCol.equals("period_start"))
						parsedPayment.setPeriodStart(rs.getTimestamp(nextCol));
					else if(nextCol.equals("period_end"))
						parsedPayment.setPeriodEnd(rs.getTimestamp(nextCol));
					else if(nextCol.equals("actual_amount"))
						parsedPayment.setActualAmount(rs.getFloat(nextCol));
					else if(nextCol.equals("discount"))
						parsedPayment.setDiscount(rs.getFloat(nextCol) > 0 ? rs.getFloat(nextCol) : -1);
					else if(nextCol.equals("amount"))
						parsedPayment.setAmount(rs.getFloat(nextCol));
					else if(nextCol.equals("due_date"))
						parsedPayment.setDueDate(rs.getTimestamp(nextCol));
					else if(nextCol.equals("paid_amount"))
						parsedPayment.setPaidAmount(rs.getFloat(nextCol) > 0 ? rs.getFloat(nextCol) : -1);
					else if(nextCol.equals("paid_date"))
						parsedPayment.setPaidDate(rs.getTimestamp(nextCol));
					else if(nextCol.equals("is_pacified"))
						parsedPayment.setPacified(rs.getBoolean(nextCol));
				}
			}
			
			return parsedPayment;
		}
		catch(Exception ex)
		{
			throw new IllegalArgumentException("Bad data set.");
		}
	}
	
	public static Payment parseFromRecord(ResultSet rs, String tableVar)
	{
		try
		{
			String[] generatedColumnNames = Payment.generateColumnNames(tableVar);
			
			Payment parsedPayment = null;
			int paymentId = rs.getInt(generatedColumnNames[0]);  
			
			if(!rs.wasNull())
			{
				parsedPayment = new Payment(paymentId);
				String[] columns = DBUtils.getColumnLabels(rs);
				
				for(String nextCol : columns)
				{
					if(nextCol.equals(generatedColumnNames[1]))
						parsedPayment.setCode(rs.getString(nextCol));
					else if(nextCol.equals(generatedColumnNames[2]))
						parsedPayment.setPeriodStart(rs.getTimestamp(nextCol));
					else if(nextCol.equals(generatedColumnNames[3]))
						parsedPayment.setPeriodEnd(rs.getTimestamp(nextCol));
					else if(nextCol.equals(generatedColumnNames[4]))
						parsedPayment.setActualAmount(rs.getFloat(nextCol));
					else if(nextCol.equals(generatedColumnNames[5]))
						parsedPayment.setDiscount(rs.getFloat(nextCol) > 0 ? rs.getFloat(nextCol) : -1);
					else if(nextCol.equals(generatedColumnNames[6]))
						parsedPayment.setAmount(rs.getFloat(nextCol));
					else if(nextCol.equals(generatedColumnNames[7]))
						parsedPayment.setDueDate(rs.getTimestamp(nextCol));
					else if(nextCol.equals(generatedColumnNames[8]))
						parsedPayment.setPaidAmount(rs.getFloat(nextCol) > 0 ? rs.getFloat(nextCol) : -1);
					else if(nextCol.equals(generatedColumnNames[9]))
						parsedPayment.setPaidDate(rs.getTimestamp(nextCol));
					else if(nextCol.equals(generatedColumnNames[10]))
						parsedPayment.setPacified(rs.getBoolean(nextCol));
				}
			}
				
			return parsedPayment;
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
		
		String[] colNames = new String[11];
		
		int columnIndex = 0;
		colNames[columnIndex] = tableVar + "_id"; columnIndex++;
		colNames[columnIndex] = tableVar + "_code"; columnIndex++;
		colNames[columnIndex] = tableVar + "_period_start"; columnIndex++;
		colNames[columnIndex] = tableVar + "_period_end"; columnIndex++;
		colNames[columnIndex] = tableVar + "_actual_amount"; columnIndex++;
		colNames[columnIndex] = tableVar + "_discount"; columnIndex++;
		colNames[columnIndex] = tableVar + "_amount"; columnIndex++;
		colNames[columnIndex] = tableVar + "_due_date"; columnIndex++;
		colNames[columnIndex] = tableVar + "_paid_amount"; columnIndex++;
		colNames[columnIndex] = tableVar + "_paid_date"; columnIndex++;
		colNames[columnIndex] = tableVar + "_is_pacified"; columnIndex++;
		
		return colNames;
	}
	
	public static String generateColumnNameString(String tableVar)
	{
		String[] generatedColumnNames = Payment.generateColumnNames(tableVar);
		String colNameStr = "";
			
		for(String nextColumnName : generatedColumnNames)
		{
			String nextVar = nextColumnName.substring(nextColumnName.indexOf("_") + 1);
			colNameStr += ", " + tableVar + "." + nextVar + " AS " + nextColumnName;
		}
			
		return colNameStr.substring(2);
	}
}