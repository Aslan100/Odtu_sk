package bordomor.odtu.sk.servlet.query;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.location.City;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import bordomor.util.XMLUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/query/get_city_data.jsp")
public class CityAndDistrictDataFetcher extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	public CityAndDistrictDataFetcher() 
    {
        super();
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		Connection conn = null;
		PrintWriter out = null;
		
		try 
        {
			out = response.getWriter();
			
			String stok = new WebUtils(request, response).getCookieValue("stok");
			String includeDistrictsStr = request.getParameter("include_districts");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			Loginable user = Loginable.findByStok(conn, stok);
			
			if(user == null || user.getRole() == null)
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				Document xml = XMLUtils.createDocument();
	        	Element parentEl = xml.createElement("query_result");
	        	parentEl.setAttribute("time", new Date().getTime() + "");
        		xml.appendChild(parentEl);
	        	
	        	City[] cities = City.findAll(conn, Boolean.parseBoolean(includeDistrictsStr));
	        	
	        	if(cities != null)
	        	{
	        		Element cityContainerEl = xml.createElement("cities");
	        		
	        		for(City nextCity : cities)
	        			cityContainerEl.appendChild(nextCity.makeXMLElement(xml, true));
	        		
	        		parentEl.appendChild(cityContainerEl);
	        		out.print(XMLUtils.convertXMLToString(xml));
	        	}
	        	else
	        		out.print(Params.NO_DATA_ERROR_STRING);
			}
		}
		catch(IllegalArgumentException iae)
		{
			out.print(Params.ILLEGAL_SERVLET_PARAMETER_ERROR_STRING);
		}
		catch(SQLException | ClassNotFoundException dbEx)
		{
			if(dbEx instanceof ClassNotFoundException)
				out.print(Params.DATABASE_CONNECTION_ERROR_STRING);
			else
			{
				SQLException sqlEx = (SQLException)dbEx;
				
				if(sqlEx.getSQLState().equals(Params.DATABASE_CONNECTION_ERROR_CODE))
					out.print(Params.DATABASE_CONNECTION_ERROR_STRING);
				else
					out.print(Params.SQL_EXCEPTION_ERROR_STRING);
			}
		}
		catch (Exception ex)
        {
        	out.print(Params.CODE_ERROR_STRING );
        } 
        finally 
        {
        	DBUtils.close(conn);
        }
	}
}
