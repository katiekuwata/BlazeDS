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

package dev.logging;

import java.io.*;

public class serverlog  {

    public String logMessage;

    //contructor
    public serverlog(){

    }


    public void logToConsole(String logThis) {

        System.out.println(logThis);

    }


    public void logToFile(String logThis) throws IOException {


        BufferedWriter out = new BufferedWriter(new FileWriter("c:\\serverlog.txt", true));
        out.write(logThis + "\n");
        out.close();


    }



}
