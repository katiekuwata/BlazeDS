package flex.samples.qos;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import flex.messaging.FlexContext;
import flex.messaging.client.FlexClient;

public class FlexClientConfigService 
{

	public void setAttribute(String name, Object value) 
	{
		FlexClient flexClient = FlexContext.getFlexClient();
		flexClient.setAttribute(name, value);
	}

	public List getAttributes() 
	{
		FlexClient flexClient = FlexContext.getFlexClient();
		List attributes = new ArrayList();
		Enumeration attrNames = flexClient.getAttributeNames();
		while (attrNames.hasMoreElements())
		{
			String attrName = (String) attrNames.nextElement();
			attributes.add(new Attribute(attrName, flexClient.getAttribute(attrName)));
		}

		return attributes;
		
	}
	
	public class Attribute {
		
		private String name;
		private Object value;

		public Attribute(String name, Object value) {
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
		
	}
	
}
