<%@ page import="java.io.*"%><%@ page contentType="text/xml" %><% 
	response.setContentType("text/xml");
    InputStreamReader is = new InputStreamReader(request.getInputStream());
	OutputStreamWriter  os = new OutputStreamWriter(response.getOutputStream());
	char buf[] = new char[4096];
	for(int read = 0; (read = is.read(buf, 0, buf.length)) != -1;)
		os.write(buf, 0, read);

	is.close();
	os.flush();
	os.close();
%>