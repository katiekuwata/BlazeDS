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

package errorhandling;

import java.util.HashMap;
import java.util.Map;
import flex.messaging.MessageException;

public class testException {
    
    public String generateMessageExceptionWithExtendedData(String extraData)
    {       
        MessageException me = new MessageException("Testing extendedData.");
        Map extendedData = new HashMap();
        // The test method that invokes this expects an "extraData" slot in this map.
        extendedData.put("extraData", extraData);
        me.setExtendedData(extendedData);
        me.setCode("999");
        me.setDetails("Some custom details.");
        throw me;
    }    
}
