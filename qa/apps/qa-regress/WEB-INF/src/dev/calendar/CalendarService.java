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

package dev.calendar;

public class CalendarService 
{

    public String getEvents(String idName)
    {
        String events = "No events scheduled.";
                
        int id = Double.valueOf(idName).intValue();            
        switch (id)
        {           
            case 1:
            case 12:
            case 25:
            {                
                events = "Swimming 7:00 pm";
                break;
            }
            case 2:
            case 15:
            case 26:
            {
                events = "Lunch with Friend 12:00 pm";
                break;
            }
            case 3:
            case 18:
            case 29:
            case 30:
            {
                events = "Cooking class 7:00 pm";
                break;
            }
            case 4:
            case 5:
            {
                events = "Vacation";
                break;
            }
            case 6:
            case 19:
            case 28:
            {
                events = "Skiing";
                break;
            }
            case 7:
            case 14:
            case 23:
            {
                events = "Class 7-9 pm";
                break;
            }
            case 9:
            {
                events = "Holiday Party 8:00 pm";
                break;
            }
            case 10:
            case 16:
            case 22:
            {
                events = "Help friend move";
                break;
            }
        }
        
        return events;
    }
}