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

import bordomor.odtu.sk.Athlete;
import bordomor.odtu.sk.Branch;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.AgeGroup;
import bordomor.odtu.sk.Team;
import bordomor.odtu.sk.template.Loginable;
import bordomor.odtu.sk.util.WebUtils;
import bordomor.util.DBUtils;
import bordomor.util.XMLUtils;

@WebServlet("/query/get_team_data.jsp")
public class TeamDataFetcher extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	public TeamDataFetcher() 
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
			String idStr = request.getParameter("id");
			String birthDateStr = request.getParameter("birth_date");
			String branchIdStr = request.getParameter("branch");
			
			out = response.getWriter();
			
			if(stok == null || stok.length() < Params.MIN_TOKEN_LENGTH || stok.length() > Params.MAX_TOKEN_LENGTH)
				throw new IllegalArgumentException();
			
			conn = DBUtils.openDBConn(Params.DB_URL, Params.DB_USERNAME, Params.DB_PWD);
			Loginable user = Loginable.findByStok(conn, stok);
			
			if(user == null || user.getRole() == null || !user.getRole().getTeamMod().canRead() || !user.getRole().getAthleteMod().canRead())
				out.print(Params.UNAUTHORIZED_USER_ERROR_STRING);
			else
			{
				if(idStr != null)
        		{
	        		Team team = Team.findById(conn, Integer.parseInt(idStr.trim()));
	        		
		        	if(team != null)
		        	{
		        		Athlete[] playerPool = Athlete.findAll_ForTeam(conn, team);
		        		
		        		Document xml = XMLUtils.createDocument();
			        	Element parentEl = xml.createElement("query_result");
			        	parentEl.setAttribute("time", new Date().getTime() + "");
		        		xml.appendChild(parentEl);
	        		
			        	team.setName(team.getName().toUpperCase(Params.DEFAULT_LOCALE));
				        parentEl.appendChild(team.makeXMLElement(xml, true));
			        	
			        	if(playerPool != null)
			        	{
			        		Element playerPoolEl = xml.createElement("player_pool");
			        		
			        		for(Athlete nextAthlete : playerPool)
			        			playerPoolEl.appendChild(nextAthlete.makeXMLElement(xml, false));
			        		
			        		parentEl.appendChild(playerPoolEl);
			        	}
			        	
			        	out.print(XMLUtils.convertXMLToString(xml));
		        	}
		        	else
		        		out.print(Params.NO_DATA_ERROR_STRING);
        		}
				else
        		{
        			Team[] teams = null; 
        					
        			if(branchIdStr != null && birthDateStr != null)
        				teams = Team.findByBranchAndAgeGroup(conn, new Branch(Integer.parseInt(branchIdStr)), AgeGroup.getGroup(birthDateStr.trim()));
        			else if(branchIdStr != null)
        				teams = Team.findByBranch(conn, new Branch(Integer.parseInt(branchIdStr)));
        			else
        				teams = Team.findAll(conn);
        					
        			if(teams != null)
        			{
	        			Document xml = XMLUtils.createDocument();
			        	Element parentEl = xml.createElement("query_result");
			        	parentEl.setAttribute("time", new Date().getTime() + "");
		        		xml.appendChild(parentEl);
			        	
		        		Element teamContainerEl = xml.createElement("teams");
		        		
		        		for(Team nextTeam : teams)
		        			teamContainerEl.appendChild(nextTeam.makeXMLElement(xml, false));
		        		
		        		parentEl.appendChild(teamContainerEl);
		        		out.print(XMLUtils.convertXMLToString(xml));
        			}
        			else
        				out.print(Params.NO_DATA_ERROR_STRING);
        		}
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
