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
 * Represents a row in the Build database table for
 * a given Project.
 * <p/>
 * A concrete Project must exist before creating
 * an instance of Build.
 *
 * @author Peter Farland
 */
public class Build extends Persistable
{
    public static final String TABLE_NAME = "Build";
    public final Project project;
    public final String number;

    private Map clauses;
    private String[] tables = new String[]{TABLE_NAME};

    Build(Project project, String number)
    {
        this.project = project;
        this.number = number;
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
        return TABLE_NAME + ": " + number + ", Project " + project.getIdentity() + ", id: " + id;
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
        inserts.put("number", number);
        return inserts;
    }

    protected Map getUpdates()
    {
        Map updates = new HashMap();
        updates.put("project_id", project);
        return updates;
    }

    protected Map getClauses()
    {
        if (clauses == null)
        {
            clauses = new HashMap();
            clauses.put("number", number);
            clauses.put("project_id", project);
        }

        if (id >= 0)
            clauses.put("id", new Long(id));

        return clauses;
    }
}
