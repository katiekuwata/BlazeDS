/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2008 Adobe Systems Incorporated
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

package macromedia.qa.metrics;

import macromedia.util.UnitTrace;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a row in the Metric table
 * of the Metrics Database.
 * <p/>
 * A Metric simply gives a Value some context
 * through the units of the value and a
 * brief description of what this kind of data
 * represents.
 *
 * @author Peter Farland
 */
public class Metric extends Persistable
{
    public static final String TABLE_NAME = "Metric";

    public final String name;
    public String units;
    public String description;

    private Map clause;
    private String[] tables = new String[]{TABLE_NAME};

    Metric(String name)
    {
        this.name = name;
    }

    public void load(MetricsDatabase database)
    {
        PreparedStatement statement = null;
        ResultSet rs = null;

        try
        {
            statement = get(database);
            rs = statement.executeQuery();
            rs.first(); //move to first row

            Object i = rs.getObject("id");
            if (i != null)
            {
                id = MetricsDatabase.getId(i);
                units = rs.getString("units");
                description = rs.getString("description");
            }
            else
            {
                id = -1;
            }
        }
        catch (SQLException ex)
        {
            if (UnitTrace.errors)
                System.err.println("Error loading " + getIdentity() + ". " + ex == null ? "" : ex.getMessage());
        }
        finally
        {
            closeResultSet(rs);
            closeStatement(statement);
        }
    }

    public String getIdentity()
    {
        return TABLE_NAME + ": " + name + ", id: " + id;
    }

    public String getTableName()
    {
        return TABLE_NAME;
    }

    protected String[] getTables()
    {
        return tables;
    }

    protected Map getInserts()
    {
        Map inserts = getUpdates();
        inserts.put("name", name);
        return inserts;
    }

    protected Map getUpdates()
    {
        Map updates = new HashMap();

        if (units != null)
            updates.put("units", units);

        if (description != null)
            updates.put("description", description);

        return updates;
    }

    protected Map getClauses()
    {
        if (clause == null)
        {
            clause = new HashMap();
            clause.put("name", name);
        }

        if (id >= 0)
            clause.put("id", new Long(id));

        return clause;
    }

}
