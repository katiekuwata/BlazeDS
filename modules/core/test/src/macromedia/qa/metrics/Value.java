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
 * Represents a row in the Value table of the
 * Metrics Database.
 * <p/>
 * A value must be associated with a concrete
 * Metric and Run.
 *
 * @author Peter Farland
 */
public class Value extends Persistable
{
    public static final String TABLE_NAME = "Value";

    public final Run run;
    public final Metric metric;
    public boolean outlier;
    public String textValue;
    public Double numberValue;

    private Map clause;
    private String[] tables = new String[]{TABLE_NAME};

    Value(Run run, Metric statistic)
    {
        this.run = run;
        this.metric = statistic;
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
                outlier = rs.getBoolean("outlier");
                textValue = rs.getString("textvalue");
                numberValue = new Double(rs.getDouble("numvalue"));
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
        return TABLE_NAME + ": " + run.build.getIdentity() + ", " + metric.getIdentity() + ", id: " + id;
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
        inserts.put("metric_id", metric);
        inserts.put("run_id", run);
        return inserts;
    }

    protected Map getUpdates()
    {
        HashMap updates = new HashMap();
        updates.put("outlier", outlier ? Boolean.TRUE : Boolean.FALSE);

        if (textValue != null)
            updates.put("textvalue", textValue);

        if (numberValue != null)
            updates.put("numvalue", numberValue);

        return updates;
    }

    protected Map getClauses()
    {
        if (clause == null)
        {
            clause = new HashMap();
            clause.put("metric_id", metric);
            clause.put("run_id", run);
        }

        if (id >= 0)
            clause.put("id", new Long(id));

        return clause;
    }
}
