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

import java.util.Map;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * A Project instance represents a row in the
 * Project table in the Metrics Database.
 *
 * A project must have a name.
 *
 * @author Peter Farland
 */
public class Project extends Persistable
{
    public static final String TABLE_NAME = "Project";
    public final String name;
    public String contact;
    public String email;

    private Map clause;
    private String[] tables = new String[] {TABLE_NAME};

    Project (String name)
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
                contact = rs.getString("contact");
                email = rs.getString("email");
            }
        }
        catch (SQLException ex)
        {

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
        HashMap updates = new HashMap();

        if (contact != null)
            updates.put("contact", contact);

        if (email != null)
            updates.put("email", email);

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
