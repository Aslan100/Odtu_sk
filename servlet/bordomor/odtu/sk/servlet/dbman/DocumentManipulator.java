package bordomor.odtu.sk.servlet.dbman;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.licence.Licence;
import bordomor.odtu.sk.licence.LifeguardLicence;
import bordomor.odtu.sk.template.Document;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;

@WebServlet("/dbman/manipulate_document.jsp")
@MultipartConfig
public class DocumentManipulator extends HttpServlet
{
	private static final long serialVersionUID = 1L;
		
	public DocumentManipulator() 
    {
        super();
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		throw new ServletException("Bad call for servlet.");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out = null;
        Connection conn = null;
        
		try 
        {
			out = response.getWriter();
			
			String stok = new WebUtils(request, response).getCookieValue("stok");
			String dataModeStr = request.getParameter("data_mode");
			
			String idStr = request.getParameter("id");
			
			String ownerLoginableIdStr = request.getParameter("owner_loginable");
			String docNo = request.getParameter("document_no");
			String branchIdStr = request.getParameter("licence_branch");
			String validFromStr = request.getParameter("valid_from");
			String validUntilStr = request.getParameter("valid_until");
			String docTypeStr = request.getParameter("document_type");
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			
			Loginable user = Loginable.findByStok(conn, stok);
			Document processedDoc = null;
			
			if(user == null || user.getRole() == null || !user.getRole().getUserMod().canWrite())
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(dataModeStr.equals(Params.DATABASE_DELETE_DATA_MODE))
				{
					processedDoc = new LifeguardLicence();
					processedDoc.setDocumentId(Integer.parseInt(idStr));
					processedDoc.deleteFromDB(conn);
				}
				else if(dataModeStr.equals(Params.DATABASE_INSERT_DATA_MODE))
	        	{
					int ownerLoginableId = Integer.parseInt(ownerLoginableIdStr);
					
	        		if(Integer.parseInt(docTypeStr) == 0)
	        		{
	        			//Spor Lisansı
	        			processedDoc = new Licence(-1, new Branch(Integer.parseInt(branchIdStr)));
	        			processedDoc.setOwnerLoginableId(ownerLoginableId);
	        			processedDoc.setDocumentNo(docNo.trim());
	        			
	        			if(validFromStr.trim().length() > 0)
	        				processedDoc.setValidFrom(new Timestamp(Params.DATE_FORMAT.parse(validFromStr).getTime()));
	        			
	        			processedDoc.setValidUntil(new Timestamp(Params.DATE_FORMAT.parse(validUntilStr).getTime()));
	        			processedDoc.createInDB(conn);
	        		}
	        		else if(Integer.parseInt(docTypeStr) == 1)
	        		{
	        			processedDoc = new LifeguardLicence();
	        			processedDoc.setOwnerLoginableId(ownerLoginableId);
	        			processedDoc.setDocumentNo(docNo.trim());
	        			
	        			if(validFromStr.trim().length() > 0)
	        				processedDoc.setValidFrom(new Timestamp(Params.DATE_FORMAT.parse(validFromStr).getTime()));
	        			
	        			processedDoc.setValidUntil(new Timestamp(Params.DATE_FORMAT.parse(validUntilStr).getTime()));
	        			processedDoc.createInDB(conn);
	        		}
	        		else
	        			throw new IllegalArgumentException();
	        	}
	        	else if(dataModeStr.equals(Params.DATABASE_UPDATE_DATA_MODE))
	        	{
	        		processedDoc = Document.findByDocumentId(conn, Integer.parseInt(idStr));
	        		
	        		if(processedDoc instanceof Licence)
	        		{
	        			Licence updatingLicence = new Licence(-1, new Branch(Integer.parseInt(branchIdStr)));
	        			updatingLicence.setDocumentNo(docNo.trim());
	        			
	        			if(validFromStr.trim().length() > 0)
	        				updatingLicence.setValidFrom(new Timestamp(Params.DATE_FORMAT.parse(validFromStr).getTime()));
	        			else
	        				updatingLicence.setValidFrom(null);
	        			
	        			updatingLicence.setValidUntil(new Timestamp(Params.DATE_FORMAT.parse(validUntilStr).getTime()));
	        			processedDoc.updateInDB(conn, updatingLicence);
	        		}
	        		if(processedDoc instanceof LifeguardLicence)
	        		{
	        			LifeguardLicence updatingLicence = new LifeguardLicence();
	        			updatingLicence.setDocumentNo(docNo.trim());
	        			
	        			if(validFromStr.trim().length() > 0)
	        				updatingLicence.setValidFrom(new Timestamp(Params.DATE_FORMAT.parse(validFromStr).getTime()));
	        			else
	        				updatingLicence.setValidFrom(null);
	        			
	        			updatingLicence.setValidUntil(new Timestamp(Params.DATE_FORMAT.parse(validUntilStr).getTime()));
	        			processedDoc.updateInDB(conn, updatingLicence);
	        		}
	        		else
	        			throw new IllegalArgumentException();
	        	}
	        	else
	        		throw new IllegalArgumentException();
				
				HttpSession session = request.getSession(false);
				session.setAttribute("dataMode", dataModeStr);
				session.setAttribute("itemId", processedDoc.getId());
				
				out.print(Params.DATA_MANIPULATION_RESULT_STRING);
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
        	out.print(Params.CODE_ERROR_STRING);
        } 
        finally 
        {
        	DBUtils.close(conn);
        }
	}
}