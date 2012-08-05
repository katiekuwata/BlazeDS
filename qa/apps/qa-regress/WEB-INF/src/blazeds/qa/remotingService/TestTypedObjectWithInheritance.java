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
 
import blazeds.qa.remotingService.TestTypedObject;

/**
 * Created by IntelliJ IDEA.
 * User: wchan
 * Date: Apr 25, 2005
 * Time: 9:26:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestTypedObjectWithInheritance extends TestTypedObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public String getProp2() {
        return "Overrided" + super.getProp2();
    }
}
