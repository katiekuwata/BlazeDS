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

import java.io.File;

import flex.messaging.io.BeanProxy;

/**
 * This class can be registered as the proxy handler for the java.io.File class.  It's
 * role is to convert the File to a FileReference class which strips out the file name
 * on the way out and sends that across instead.
 */
public class FileProxy extends BeanProxy
{
    public Object getInstanceToSerialize(Object srcInst)
    {
        if (srcInst == null)
            return null;

        if (srcInst instanceof File)
        {
            return new FileReference(((File) srcInst).getPath());
        }
        throw new IllegalArgumentException("FileProxy registered to class of type: " + srcInst.getClass().getName());
    }
}

