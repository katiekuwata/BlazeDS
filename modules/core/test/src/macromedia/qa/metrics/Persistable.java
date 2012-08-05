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
import java.sql.Statement;
import java.util.Map;

/**
 * A base type whose subclasses are expected to be backed
 * by a database table.
 * <p/>
 * A Persistable instance represents a row in that
 * table. All Persistable items are expected to have
 * a unique identifier field called simply, "id" of
 * SQL Type "bigint", or a Long in Java.
 *
 * @author Peter Farland
 */
public abstract class Persistable
{
    protected long id = -1;

    /**
     * Determines whether a Persistable instance exists in a table
     * by counting the number of records returned by a SELECT statement.
     * If the count is greater than 0, it is assumed that the instance
     * exists.
     *
     * @param database
     * @return
     * @throws SQLException
     */
    public boolean exists(MetricsDatabase database) throws SQLException
    {
        if (database != null)
        {
            PreparedStatement statement = null;
            ResultSet rs = null;

            try
            {
                int count = 0;
                statement = get(database);
                rs = statement.executeQuery();

                count = MetricsDatabase.countRecords(rs);

                if (count > 0)
                {
                    rs.first();
                    Object val = rs.getObject("id");
                    if (val != null)
                    {
                        id = MetricsDatabase.getId(val);
                        return id >= 0;
                    }
                }
            }
            catch (SQLException ex)
            {
                if (UnitTrace.errors)
                    System.err.println("Error checking if " + getIdentity() + " exists." + ex == null ? "" : ex.getMessage());

                throw ex;
            }
            finally
            {
                closeResultSet(rs);
                closeStatement(statement);
            }
        }

        return false;
    }

    protected PreparedStatement get(MetricsDatabase database) throws SQLException
    {
        PreparedStatement statement = null;
        statement = database.select(null, getTables(), getClauses(), null);
        return statement;
    }

    /**
     * Silently tries to close a ResultSet. This must be called before executing
     * another query on most drivers when sharing a common connection.
     *
     * @param rs
     */
    protected void closeResultSet(ResultSet rs)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
            }
            catch (Exception ex)
            {
            }
        }
    }

    /**
     * Silently tries to close a Statement. This must be called before executing
     * another query on most drivers when sharing a common connection.
     *
     * @param stmt
     */
    protected void closeStatement(Statement stmt)
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (Exception ex)
            {
            }
        }
    }


    /**
     * A utility method to save a Persistable instance to a given database. This method
     * first checkes whether the instance exists, in which case an update is performed,
     * otherwise an insert is performed. In the case of an insert, a load operation is
     * also called to populate the instance's id field.
     *
     * @param database
     * @throws SQLException
     */
    public void save(MetricsDatabase database) throws SQLException
    {
        PreparedStatement statement = null;

        try
        {
            if (exists(database))
            {
                statement = database.update(getTableName(), getUpdates(), getClauses(), null);
                statement.executeUpdate();
            }
            else
            {
                statement = database.insert(getTableName(), getInserts(), null, null);
                statement.executeUpdate();
                load(database);
            }
        }
        catch (SQLException ex)
        {
            if (UnitTrace.errors)
                System.err.println("Error saving " + getIdentity() + ". " + ex == null ? "" : ex.getMessage());

            throw ex;
        }
        finally
        {
            closeStatement(statement);
        }
    }

    /**
     * A short-circuit utility method that forces a record to be inserted without first checking
     * that it exists (an hence usually would be processed as an update).
     * <p/>
     * As with save(), load() is called after an insert to populate a Persistable instance's
     * id field.
     *
     * @param database
     * @throws SQLException
     */
    public void insert(MetricsDatabase database) throws SQLException
    {
        PreparedStatement statement = null;

        try
        {
            statement = database.insert(getTableName(), getInserts(), null, null);
            statement.executeUpdate();
            load(database);
        }
        catch (SQLException ex)
        {
            if (UnitTrace.errors)
                System.err.println("Error saving " + getIdentity() + ". " + ex == null ? "" : ex.getMessage());

            throw ex;
        }
        finally
        {
            closeStatement(statement);
        }
    }

    public abstract void load(MetricsDatabase database);

    public abstract String getTableName();

    public abstract String getIdentity();

    protected abstract String[] getTables();

    /*
     * Note that Persistable entries in the following Maps
     * will be referenced by id in SQL statements.
     */
    protected abstract Map getInserts();

    protected abstract Map getUpdates();

    protected abstract Map getClauses();
}
