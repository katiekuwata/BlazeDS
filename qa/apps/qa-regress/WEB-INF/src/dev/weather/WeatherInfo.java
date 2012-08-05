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

package dev.weather;

import java.io.Serializable;

public class WeatherInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String location;
    private String temperature;
    private String forecast;
    private String[] extendedForecast = new String[5];

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }
    
    public String[] getExtendedForecast() {
    	return extendedForecast;
	}
	    	
    public void setExtendedForecast(String monday, String tuesday, String wednesday, String thursday, String friday) {
    	this.extendedForecast[0] = monday;
    	this.extendedForecast[1] = tuesday;
    	this.extendedForecast[2] = wednesday;
    	this.extendedForecast[3] = thursday;
    	this.extendedForecast[4] = friday;
	}	
}
