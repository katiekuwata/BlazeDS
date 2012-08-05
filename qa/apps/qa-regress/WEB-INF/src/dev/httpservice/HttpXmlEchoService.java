/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2008 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/


package dev.httpservice;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class HttpXmlEchoService extends HttpServlet
{

    private static final long serialVersionUID = 1L;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/xml");
        InputStreamReader isr = new InputStreamReader(request.getInputStream());
        OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
        char[] buf = new char[4096];
        int read = 0;
        while ((read = isr.read(buf, 0, buf.length)) != -1)
        {
            osw.write(buf, 0, read);
        }
        isr.close();
        osw.flush();
        osw.close();
    }

}