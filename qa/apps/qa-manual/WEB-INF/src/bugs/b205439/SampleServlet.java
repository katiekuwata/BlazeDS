//=========================================================================
//@author Bill Sahlas
//test file needed for bug 205439
//=========================================================================

package bugs.b205439;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SampleServlet extends HttpServlet  {
    private static final long serialVersionUID = 1L;



//=========================================================================
//Generate Displayed Data Process
//in param req : HttpServletRequest
//param res : HttpServletResponse
//out param StringBuffer : Displayed Data
//=========================================================================
public void service (HttpServletRequest req,
HttpServletResponse res)
throws ServletException, IOException {
//Define Variable
StringBuffer strbufXml = null; // Return value(XML format)

//for the garbled character
req.setCharacterEncoding("utf-8");
res.setContentType("text/xml; charset=utf-8");
res.setHeader("Cache-Control", "no-cache");

//Dedit XML
System.out.println("@@ Make Start");
strbufXml = xmlEdit();
System.out.println("@@ Make End");

//Force to delayed process
System.out.println("@@ Loop Start ");
for (long i=0; i<2000000000;) {
i++;
}
System.out.println("@@ Loopl End!!");


//Returning watched detailed data
System.out.println("@@ Send Start");
PrintWriter out = res.getWriter();
out.print(strbufXml);
System.out.println("@@ Send End");
}

//=========================================================================
//Edit return value (XML) process
//in param : null
//out param StringBuffer : Return value (XML)
//=========================================================================
protected StringBuffer xmlEdit() {
//Define variables
StringBuffer strbufXml = new StringBuffer(); // Return value(XML format)
long i = 0; // counter
long lCnt = 0; // count
Long work01;
//Generate Displayed Data
strbufXml.append("<TOP>");
for (i=0; i<5000; i++) {
strbufXml.append("<DD>");

strbufXml.append("<C1>");
work01 =new Long(lCnt+1);
strbufXml.append(work01.toString());
strbufXml.append("</C1>");

strbufXml.append("<C2>");
strbufXml.append("Colum_2");
strbufXml.append("</C2>");

strbufXml.append("<C3>");
strbufXml.append("Colum_3");
strbufXml.append("</C3>");

strbufXml.append("<C4>");
strbufXml.append("Colum_4");
strbufXml.append("</C4>");

strbufXml.append("<C5>");
strbufXml.append("Colum_5");
strbufXml.append("</C5>");

strbufXml.append("</DD>");

lCnt++;
}

//Generate the maximum count for watched detail data
strbufXml.append("<CNT>");
work01 =new Long(lCnt);
strbufXml.append(work01.toString());
strbufXml.append("</CNT>");

strbufXml.append("</TOP>");

return strbufXml;
}
}
