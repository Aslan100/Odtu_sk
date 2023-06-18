package bordomor.odtu.sk.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Vector;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import bordomor.odtu.sk.Params;
import bordomor.odtu.sk.Params.Language;

public class WebUtils 
{
	public static final boolean IS_REMOTE_SERVER_MODE = false;
	public static final String PORTAL_CONTEXT_PATH = IS_REMOTE_SERVER_MODE ? "" : "/ODTU_SK";
	
	public static final boolean COOKIE_HTTP_ONLY = IS_REMOTE_SERVER_MODE ? false : false;
	public static final boolean COOKIE_SECURE = IS_REMOTE_SERVER_MODE ? false : false;
	public static final String COOKIE_DOMAIN = IS_REMOTE_SERVER_MODE ? "bas-ka.com" : "localhost";
	public static final String COOKIE_PATH = "/";
	
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	
	public WebUtils(HttpServletRequest request, HttpServletResponse response)
	{
		this.request = request;
		this.response = response;
	}
	
	public String getCookieValue(String cookieName)
	{
		String value = null;
		Cookie[] cookies = this.request.getCookies();
		
		for(int i = 0; cookies != null && i < cookies.length; i++)
		{
			if(cookies[i].getName().equalsIgnoreCase(cookieName))
			{
				value = cookies[i].getValue();
				break;
			}
		}
		
		return value;
	}
	
	public boolean createCookie(String cookieName, String value, int durationSec)
	{
		try
		{
			Cookie newCookie = new Cookie(cookieName, value);
			newCookie.setHttpOnly(WebUtils.COOKIE_HTTP_ONLY);
			newCookie.setSecure(WebUtils.COOKIE_SECURE);
			newCookie.setMaxAge(durationSec);
			newCookie.setDomain(WebUtils.COOKIE_DOMAIN);
			newCookie.setPath(WebUtils.COOKIE_PATH);
			this.response.addCookie(newCookie);
			
			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	
	public boolean deleteCookie(String cookieName)
	{
		try
		{
			Cookie[] cookies = this.request.getCookies();
			
			for(int i = 0; cookies != null && i < cookies.length; i++)
			{
				if(cookies[i].getName().equals(Params.SESSION_TOKEN_ABBREVIATION))
				{
					Cookie oldCookie = new Cookie(Params.SESSION_TOKEN_ABBREVIATION, "");
					oldCookie.setHttpOnly(WebUtils.COOKIE_HTTP_ONLY);
					oldCookie.setSecure(WebUtils.COOKIE_SECURE);
					oldCookie.setMaxAge(0);
					oldCookie.setDomain(WebUtils.COOKIE_DOMAIN);
					oldCookie.setPath(WebUtils.COOKIE_PATH);
					this.response.addCookie(oldCookie);
					
					return true;
				}
			}
			
			return false;
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	
	public boolean switchSession()
	{
		try
		{
			if(this.request == null)
				return false;
			
			HttpSession oldSession = this.request.getSession();
			Vector<String> keyVector = new Vector<String>(0);
			Vector<Object> valueVector = new Vector<Object>(0);
			
			if(oldSession != null)
			{
				Enumeration<String> keys = oldSession.getAttributeNames();
				
				while (keys.hasMoreElements())
				{
				   String nextKey = (String) keys.nextElement();
				   keyVector.add(nextKey);
				   valueVector.add(oldSession.getAttribute(nextKey));
				}
				
				oldSession.invalidate();
			}
			
			HttpSession newSession = this.request.getSession(true);
			
			for(int i = 0; i < keyVector.size(); i++)
				newSession.setAttribute(keyVector.get(i), valueVector.get(i));
			
			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	
	//Statik Metodlar
	public static String encodeURIComponent(String uri) 
	{
	    String result;

	    try
	    {
	    	result = URLEncoder.encode(uri, "UTF-8")
	                .replaceAll("\\+", "%20")
	                .replaceAll("\\%21", "!")
	                .replaceAll("\\%27", "'")
	                .replaceAll("\\%28", "(")
	                .replaceAll("\\%29", ")")
	                .replaceAll("\\%7E", "~");
	    }
	    catch (UnsupportedEncodingException e) 
	    {
	        result = uri;
	    }

	    return result;
	}
	
	public static String getLinkPrefix(short order)
	{
		String linkPrefix = "";
		
		if(order == 0)
			linkPrefix = "./";
		else
		{
			while(order > 0)
			{
				linkPrefix += "../";
				order--;
			}
		}
		
		return linkPrefix;
	}
	
	public static String getLinkPrefix(String servletPath)
	{
		String[] urlParts = servletPath.split("[^\\/]+");
		short partCount = (short) 0;
		
		for(String nextPart : urlParts)
		{
			if(nextPart.length() > 0)
				partCount++;
		}
		
		return getLinkPrefix((short) (partCount - 1));
	}
	
	public static Language getLanguageFromURL(String url)
	{
		String[] urlParts = url.split("[\\/]+");
		Language parsedLang = null;
		
		for(String nextPart : urlParts)
		{
			try
			{
				parsedLang = Language.build(nextPart);
				
				if(parsedLang != null)
					break;
			}
			catch(Exception ex) {}
		}
		
		return parsedLang == null ? Params.DEFAULT_LANGUGAE : parsedLang;
	}
}