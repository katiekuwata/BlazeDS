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
import blazeds.qa.remotingService.FileReference;

/**
 * This class is registered to our FileReference type.  It's role is to
 * convert the FileReference after serialization to a java.io.File instance
 */
public class FileReferenceProxy extends BeanProxy
{
    public Object instanceComplete(Object fileRef)
    {
        if (fileRef == null)
            return null;

        if (fileRef instanceof blazeds.qa.remotingService.FileReference)
        {
            return new File(((FileReference)fileRef).fileName);
        }
        throw new IllegalArgumentException("FileReferenceProxy registered to class of type: " + fileRef.getClass().getName());
    }
}

