/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
 
package flex.graphics;

import java.util.HashMap;
import java.util.Map;

/**
 * This class corresponds to mx.graphics.ImageSnapshot on the client.  Clients may choose
 * to capture images and send them to the server via a RemoteObject call.  The PDF generation 
 * feature of LCDS may then be used to generate PDF's from these images.  
 *
 */
public class ImageSnapshot extends HashMap
{
    private static final long serialVersionUID = 7914317354403674061L;

    /**
     * Default constructor.
     */
    public ImageSnapshot()
    {
    }

    private Map properties;
    private String contentType;
    private byte[] data;
    private int height;
    private int width;

    /**
     * The content type for the image encoding format that was used to capture
     * this snapshot.
     * 
     * @return content type of this image
     */
    public String getContentType()
    {
        return contentType;
    }

    /**
     * Sets content type of this snapshot.
     * 
     * @param value content type
     */
    public void setContentType(String value)
    {
        contentType = value;
    }

    /**
     * The encoded data representing the image snapshot.
     * 
     * @return encoded image data
     */
    public byte[] getData()
    {
        return data;
    }

    /**
     * Set the encoded data representing the image snapshot.
     * 
     * @param value byte array of image data
     */
    public void setData(byte[] value)
    {
        data = value;
    }

    /**
     * The image height in pixels.
     * 
     * @return image height in pixels
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Set image height in pixels.
     * 
     * @param value image height in pixels.
     */
    public void setHeight(int value)
    {
        height = value;
    }

    /**
     * Additional properties of the image.
     * 
     * @return a map containing the dynamically set properties on this snapshot
     */
    public Map getProperties()
    {
        return properties;
    }

    /**
     * Sets the map of dynamic properties for this snapshot.
     * 
     * @param value map containing dynamic properties for this snapshot
     */
    public void setProperties(Map value)
    {
        properties = value;
    }

    /**
     * The image width in pixels.
     * 
     * @return width in pixels
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Set width in pixels.
     * 
     * @param value width in pixels.
     */
    public void setWidth(int value)
    {
        width = value;
    }

}
