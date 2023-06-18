package bordomor.odtu.sk.template;

import java.lang.reflect.Array;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public interface IXmlObject
{
	public void parseFromXMLElement(Element element);
	public Element makeXMLElement(Document xml, boolean cascadeRelations);
	
	public static Object[] parseAll(Document xml, Class<?> objectClass) throws Exception
	{
		if(!IXmlObject.class.isAssignableFrom(objectClass))
			throw new IllegalArgumentException("Passed class parameter must implement IXmlObject");
		
		String tagName = objectClass.getAnnotation(XMLAndDatabaseValues.class).tagName();
		NodeList objectList = xml.getElementsByTagName(tagName);
		
		Object[] parsedObjects = (Object[]) Array.newInstance(objectClass, objectList.getLength());
		
		for(int i = 0; i < objectList.getLength(); i++)
		{
			Object nextObject = objectClass.getDeclaredConstructor().newInstance();
			((IXmlObject) nextObject).parseFromXMLElement((Element) objectList.item(i));
			parsedObjects[i] = nextObject;
		}
		
		return parsedObjects.length != 0 ? parsedObjects : null;
	}
	
	public static Object[] parseAll(Element parentEl, Class<?> objectClass) throws Exception
	{
		if(!IXmlObject.class.isAssignableFrom(objectClass))
			throw new IllegalArgumentException("Passed class parameter must implement IXmlObject");
		
		String tagName = objectClass.getAnnotation(XMLAndDatabaseValues.class).tagName();
		NodeList objectList = parentEl.getElementsByTagName(tagName);
		
		Object[] parsedObjects = (Object[]) Array.newInstance(objectClass, objectList.getLength());
		
		for(int i = 0; i < objectList.getLength(); i++)
		{
			Object nextObject = objectClass.getDeclaredConstructor().newInstance();
			((IXmlObject) nextObject).parseFromXMLElement((Element) objectList.item(i));
			parsedObjects[i] = nextObject;
		}
		
		return parsedObjects.length != 0 ? parsedObjects : null;
	}
	
	public static Object parseFirst(Element parentEl, Class<?> objectClass) throws Exception
	{
		if(!IXmlObject.class.isAssignableFrom(objectClass))
			throw new IllegalArgumentException("Passed class parameter must implement IXmlObject");
		
		String tagName = objectClass.getAnnotation(XMLAndDatabaseValues.class).tagName();
		NodeList objectList = parentEl.getElementsByTagName(tagName);
		
		if(objectList.getLength() > 0)
		{
			Object nextObject = objectClass.getDeclaredConstructor().newInstance();
			((IXmlObject) nextObject).parseFromXMLElement((Element) objectList.item(0));
			return nextObject;
		}
		
		return null;
	}
}