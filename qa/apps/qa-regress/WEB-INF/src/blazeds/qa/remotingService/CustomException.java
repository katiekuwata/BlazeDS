package blazeds.qa.remotingService;
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

public class CustomException extends Exception
{
    private String _reason;
    static final long serialVersionUID = 7867324767130586850L;

    public String getReason()
    {
        return _reason;
    }

    public void setReason(String r)
    {
        _reason = r;
    }

    public int code;

    public String toString()
    {
        return "CustomException: " + _reason;
    }
}
