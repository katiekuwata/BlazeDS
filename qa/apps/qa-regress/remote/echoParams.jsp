<%@ page import="java.util.Enumeration"%>
<%@ page contentType="text/xml" %>

<parameters>
<%
    Enumeration names = request.getParameterNames();
    StringBuffer buf = new StringBuffer();
    while (names.hasMoreElements())
    {
        String name = (String)names.nextElement();
        String[] values = request.getParameterValues(name);
        for (int i = 0; i < values.length; ++i)
        {
            buf.append("<").append(name).append(">").append(values[i]).append("</").append(name).append(">");
        }
    }
%>
<%= buf.toString() %>
</parameters>