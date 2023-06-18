package bordomor.odtu.sk.servlet.query;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Parent;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import bordomor.util.XMLUtils;

@WebServlet("/query/get_parent_data.jsp")
public class ParentDataFetcher extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	public ParentDataFetcher() 
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
		
		PrintWriter out = null;
		Connection conn = null;
		
		try 
        {
			String stok = new WebUtils(request, response).getCookieValue("stok");
			String code = request.getParameter("code");
			String idNo = request.getParameter("id_no");
			String email = request.getParameter("email");
			
			out = response.getWriter();
			
			if(idNo == null && email == null)
			{
				if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
					throw new IllegalArgumentException();
				
				conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
				Loginable user = Loginable.findByStok(conn, stok);
				
				if(user == null || user.getRole() == null || !user.getRole().getUserMod().canRead())
					out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
				else
				{
					Parent[] parents = null;
					
					if(code != null)
	        		{
		        		Parent parent = Parent.findByCode(conn, code.trim());
			        	
			        	if(parent != null)
			        	{
			        		parent.setName(parent.getName().toUpperCase(Params.DEFAULT_LOCALE));
			        		parent.setSurname(parent.getSurname().toUpperCase(Params.DEFAULT_LOCALE));
			        		parents = new Parent[] { parent };
			        	}
	        		}
					else
						parents = Parent.findAll(conn);
					
					if(parents != null)
					{
						Document xml = XMLUtils.createDocument();
			        	Element parentEl = xml.createElement("query_result");
			        	parentEl.setAttribute("time", new Date().getTime() + "");
		        		xml.appendChild(parentEl);
		        		
		        		Element parentsContainerEl = xml.createElement("parents");
	            		
	            		for(Parent nextParent : parents)
	            			parentsContainerEl.appendChild(nextParent.makeXMLElement(xml, false));
	            		
	            		parentEl.appendChild(parentsContainerEl);
	            		out.print(XMLUtils.convertXMLToString(xml));
					}
					else
						out.print(Params.NO_DATA_ERROR_STRING);
			    }
			}
			else if(idNo != null)
			{
				Parent parent = Parent.findByIdNo(conn, idNo.trim());
				
				Document xml = XMLUtils.createDocument();
	        	Element parentEl = xml.createElement("query_result");
	        	parentEl.setAttribute("time", new Date().getTime() + "");
        		xml.appendChild(parentEl);
        		
        		parentEl.appendChild(parent.makeXMLElement(xml, true));
        		out.print(XMLUtils.convertXMLToString(xml));
        	}
			else if(email != null)
			{
				Loginable existingloginable = Loginable.findByEmail(conn, email);
	        	out.print(existingloginable != null ? Params.DATA_MANIPULATION_RESULT_STRING : Params.NO_DATA_ERROR_STRING);
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
