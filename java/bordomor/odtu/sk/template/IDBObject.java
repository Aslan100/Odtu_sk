package bordomor.odtu.sk.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Registration;
import bordomor.util.DBUtils;

public interface IDBObject 
{
	public int getId();
	public void setId(int id);
	public void createInDB(Connection conn) throws ClassNotFoundException, SQLException;
	public void updateInDB(Connection conn, IDBObject newObject) throws ClassNotFoundException, SQLException;
	
	public default void deleteFromDB(Connection conn) throws ClassNotFoundException, SQLException 
	{
		Connection newConn = conn;
        PreparedStatement deleteSt = null;
        
        try
        {
	        if(conn == null)
	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
	        
	        String tableName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tableName(); 
			String deleteQuery = "DELETE FROM " + tableName + " WHERE id = ?";
        	deleteSt = newConn.prepareStatement(deleteQuery);
    		deleteSt.setInt(1, this.getId());
    		deleteSt.executeUpdate();
        	
    		this.setId(-1);
    	}
        finally
        {
        	DBUtils.close(deleteSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	public default void updateColumnInDB(Connection conn, String columnName, Object newValue, int columnType) throws ClassNotFoundException, SQLException 
	{
		if(columnName == null || this.getId() == -1)
			throw new IllegalArgumentException();
		
		String tableName = this.getClass().getAnnotation(XMLAndDatabaseValues.class).tableName();
		
		Connection newConn = conn;
        PreparedStatement updateSt = null;
        ResultSet keySet = null;
        
        try
        {
        	 if(conn == null)
 	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
        	 
        	 String updateQuery = ""
        	 		+ "UPDATE " + tableName + " SET " + columnName + " = ? WHERE id = " + this.getId();
        	 
        	 updateSt = newConn.prepareStatement(updateQuery);
        	 updateSt.setObject(1, newValue != null ? newValue.toString() : null, columnType);
        	 
        	 if(updateSt.executeUpdate() != 1)
 	        	throw new IllegalArgumentException();
        }
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(updateSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
	
	public default void updateColumnInDB(Connection conn, String columnName, Enum<?> newValue, String dbEnumName) throws ClassNotFoundException, SQLException 
	{
		if(columnName == null || this.getId() == -1)
			throw new IllegalArgumentException();
		
		String tableName = Registration.class.getAnnotation(XMLAndDatabaseValues.class).tableName();
		
		Connection newConn = conn;
        PreparedStatement updateSt = null;
        ResultSet keySet = null;
        
        try
        {
        	 if(conn == null)
 	        	newConn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
        	 
        	 String updateQuery = ""
        	 		+ "UPDATE " + tableName + " SET " + columnName + " = ?::" + dbEnumName + " WHERE id = " + this.getId();
        	 
        	 updateSt = newConn.prepareStatement(updateQuery);
        	 updateSt.setObject(1, newValue != null ? newValue.toString() : null, Types.VARCHAR);
        	 
        	 if(updateSt.executeUpdate() != 1)
 	        	throw new IllegalArgumentException();
        }
        finally
        {
        	DBUtils.close(keySet);
        	DBUtils.close(updateSt);
        	
        	if(conn == null)
        		DBUtils.close(newConn);	
        }
	}
}