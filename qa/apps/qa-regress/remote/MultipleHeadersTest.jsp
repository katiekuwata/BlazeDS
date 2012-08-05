<%@ page import="java.util.*" %><%	 
	 // take multiple headers from a client and send them back
	 Enumeration types = request.getHeaders("type");
	 while(types.hasMoreElements())
	 {
		response.addHeader("type", types.nextElement().toString());		
	 }
	
	 // if the client doesn't send any, add couple 
	 response.addHeader("type", "jsp");
 	 response.addHeader("type", "servlet");

	 response.setContentType("text/html");
     out.print("hello from MultiHeaderTest");
%>