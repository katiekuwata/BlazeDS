<%@ page import="java.util.Enumeration,
                 java.net.URLEncoder"%>
<%@ page contentType="text/plain" %>
<%
    Enumeration names = request.getParameterNames();
    StringBuffer buf = new StringBuffer();
    boolean wroteOne = false;
    while (names.hasMoreElements())
    {
        String name = (String)names.nextElement();
        String[] values = request.getParameterValues(name);
        for (int i = 0; i < values.length; ++i)
        {
            if (wroteOne)
            {
                buf.append("&");
            }
            buf.append(URLEncoder.encode(name, "UTF-8")).append("=").append(URLEncoder.encode(values[i], "UTF-8"));
            wroteOne = true;
        }
    }
%>
<%= buf.toString() %>