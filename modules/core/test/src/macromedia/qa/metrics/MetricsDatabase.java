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

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the connection for the Metrics Database.
 *
 * Properties can be set in a .properties file; the following is a list of the relevant keys.
 * <ul>
 *     <li>host - the server name or ip address </li>
 *     <li>port - the port the database is listening on</li>
 *     <li>driver - the fully qualified class name of JDBC driver </li>
 *     <li>urlbase - the JDBC connection URL prefix </li>
 *     <li>account - the user name required to connect to the database</li>
 *     <li>password - the password required to connect to the database</li>
 *     <li>datasource - the datasource, used for JDBC-ODBC drivers </li>
 *     <li>database - the database name to connect to </li>
 * </ul>
 *
 * @author Peter Farland
 */
public class MetricsDatabase extends AbstractDatabase
{
    private Properties properties;

    private static final String DEFAULT_HOST = "10.1.144.66";
    private static final String DEFAULT_PORT = "1433";
    private static final String DEFAULT_DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";
    private static final String DEFAULT_URLBASE = "jdbc:odbc:";
    private static final String DEFAULT_DATASOURCE = "master";

    public MetricsDatabase()
    {
        init(null);
    }

    public MetricsDatabase(Properties props)
    {
        properties = props;
        init(null);
    }

    public MetricsDatabase(File f)
    {
        init(f);
    }

    private void init(File f)
    {
        if (f != null)
            loadProperties(f);

        if (properties == null)
            properties = new Properties();

        try
        {
            Class.forName(getDriver()).newInstance();
            connection = getConnection();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException
    {
        if (connection == null)
            connection = DriverManager.getConnection(getConnectionURL(), getConnectionProperties());

        return connection;
    }

    public void dispose()
    {
        try
        {
            connection.close();
            connection = null;
        }
        catch (Exception ex)
        {
        }
    }

    public void loadProperties(File f)
    {
        try
        {
            FileInputStream fis = new FileInputStream(f);
            properties = new Properties();
            properties.load(fis);
        }
        catch (FileNotFoundException ex)
        {
            System.err.println();
            System.err.println("Could not find database properties file " + f.getAbsolutePath());
            System.err.println("\t" + ex.getMessage());
        }
        catch (IOException ioe)
        {
            System.err.println();
            System.err.println("Error reading database properties file " + f.getAbsolutePath());
            System.err.println("\t" + ioe.getMessage());
        }

    }

    String getDriver()
    {
        String driver = properties.getProperty("driver");
        if (driver == null)
            driver = DEFAULT_DRIVER;
        else
            driver = driver.trim();

        return driver;
    }

    String getHost()
    {
        String host = properties.getProperty("host");
        if (host == null)
            host = DEFAULT_HOST;
        else
            host = host.trim();

        return host;
    }

    String getPort()
    {
        String port = properties.getProperty("port");
        if (port == null)
            port = DEFAULT_PORT;
        else
            port = port.trim();

        return port;
    }

    String getAccount()
    {
        String account = properties.getProperty("account");
        if (account != null)
            account = account.trim();

        return account;
    }

    String getPassword()
    {
        String pass = properties.getProperty("password");
        if (pass != null)
            pass = pass.trim();

        return pass;
    }


    String getDatabase()
    {
        String db = properties.getProperty("database");
        if (db != null)
            db = db.trim();

        return db;
    }

    String getDatasource()
    {
        String ds = properties.getProperty("datasource");
        if (ds != null)
            ds = ds.trim();

        return ds;
    }

    String getURLBase()
    {
        String urlb = properties.getProperty("urlbase");
        if (urlb == null)
            urlb = DEFAULT_URLBASE;

        return urlb;
    }

    String getConnectionURL()
    {
        StringBuffer sb = new StringBuffer();
        String urlb = getURLBase();

        if (DEFAULT_URLBASE.equals(urlb))
        {
            String odbc = getDatasource();
            if (odbc == null)
                odbc = DEFAULT_DATASOURCE;
            else
                odbc = odbc.trim();

            sb.append(urlb);

            if (!urlb.endsWith(":"))
                sb.append(":");

            sb.append(odbc);
        }
        else
        {
            sb.append(urlb).append(getHost()).append(":").append(getPort());
        }

        return sb.toString();
    }

    private Properties getConnectionProperties()
    {
        Properties connectionProps = new Properties();

        if (getDatabase() != null)
            connectionProps.setProperty("databaseName", getDatabase());

        connectionProps.setProperty("user", getAccount());
        connectionProps.setProperty("password", getPassword());

        return connectionProps;
    }
}

