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
 * Represents a row in the Run table of the
 * Metrics database.
 * <p/>
 * A Run is one test of a Build and may be responsible
 * for generating numerous Values.
 *
 * @author Peter Farland
 */
public class Run extends Persistable
{
    public static final String TABLE_NAME = "Run";
    public final Build build;
    public String description;
    public long time;

    private Map clauses;
    private String[] tables = new String[]{TABLE_NAME};

    Run(Build build)
    {
        this.build = build;
    }

    public void load(MetricsDatabase database)
    {
        PreparedStatement statement = null;
        ResultSet rs = null;

        try
        {
            // Hack, for a load operation we may not yet have the real
            // id, so try to use other fields to make a run unique.
            if (time > 0)
            {
                getClauses();
                clauses.put("runtime", new Long(time));
            }

            statement = get(database);

            if (time > 0)
                clauses.remove("runtime");

            rs = statement.executeQuery();
            rs.first(); //move to first row

            Object i = rs.getObject("id");
            if (i != null)
            {
                id = MetricsDatabase.getId(i);
                description = rs.getString("description");

                java.sql.Date sqlDate = rs.getDate("runtime");
                if (sqlDate != null)
                {
                    time = sqlDate.getTime();
                }
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
        return TABLE_NAME + ": " + build.getIdentity() + ", id: " + id;
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
        inserts.put("build_id", build);
        return inserts;
    }

    protected Map getUpdates()
    {
        HashMap updates = new HashMap();

        if (description != null)
            updates.put("description", description);

        if (time > 0)
            updates.put("runtime", new Long(time));

        return updates;
    }

    protected Map getClauses()
    {
        if (clauses == null)
        {
            clauses = new HashMap();
            clauses.put("build_id", build);
        }

        if (id >= 0)
            clauses.put("id", new Long(id));

        return clauses;
    }

}
