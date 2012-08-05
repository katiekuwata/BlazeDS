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

import java.io.File;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * Use this class to store the context of a Run and corresponding
 * Metric being collected.
 *
 * Call newRun and newMetric before creating values.
 *
 * @author Peter Farland
 */
public class MetricsManager
{
    private Project project;
    private Build build;
    private MetricsDatabase database;
    private Run currentRun;
    private Metric currentMetric;

    public MetricsManager(String projectName, String buildNumber, File props) throws SQLException
    {
        validate(projectName, buildNumber, props);

        try
        {
            this.database = new MetricsDatabase(props);
            initProject(projectName);
            initBuild(buildNumber);
        }
        catch (SQLException ex)
        {
            try
            {
                database.dispose();
            }
            catch(Throwable t) {}

            throw ex;
        }
    }

    private void initProject(String projectName) throws SQLException
    {
        project = new Project(projectName);
        if (!project.exists(database))
        {
            project.save(database);
        }
    }

    private void initBuild(String buildNumber) throws SQLException
    {
        build = new Build(project, buildNumber);
        if (!build.exists(database))
        {
            build.save(database);
        }
    }

    public Run newRun() throws SQLException
    {
        currentRun = new Run(build);
        currentRun.time = Calendar.getInstance().getTime().getTime();

        if (currentRun.id >= 0)
            currentRun.save(database);
        else
            currentRun.insert(database);

        return getCurrentRun();
    }

    public Run getCurrentRun()
    {
        return currentRun;
    }

    public Metric newMetric(String name, String units) throws SQLException
    {
        currentMetric = new Metric(name);
        currentMetric.units = units;

        if (!currentMetric.exists(database))
        {
            if (currentMetric.id >= 0)
                currentMetric.save(database);
            else
                currentMetric.insert(database);
        }

        return getCurrentMetric();
    }

    public Metric getCurrentMetric()
    {
        return currentMetric;
    }

    public Value createValue(double val)
    {
        Value v = new Value(currentRun, currentMetric);
        v.numberValue = new Double(val);
        return v;
    }

    public Value createValue(String val)
    {
        Value v = new Value(currentRun, currentMetric);
        v.textValue = val;
        return v;
    }

    public void saveValue(Value val) throws SQLException
    {
        if (val != null)
        {
            if (val.id >= 0)
                val.save(database); //Attempt an update as we have an id
            else
                val.insert(database); //Force insert
        }
    }



    //
    // SETTINGS VALIDATION
    //

    private void validate(String project, String buildNumber, File props)
    {
        if (!validProjectName(project))
            throw new IllegalStateException("Invalid project " + project);

        if (!validBuildNumber(buildNumber))
            throw new IllegalStateException("Invalid buildNumber " + buildNumber);

        if (!validProps(props))
            throw new IllegalStateException("Invalid database properties " + props);
    }

    private static boolean validProjectName(String project)
    {
        return project != null && project.trim().length() > 0;
    }

    private static boolean validBuildNumber(String buildNumber)
    {
        return buildNumber != null && buildNumber.trim().length() > 0;
    }

    private static boolean validProps(File props)
    {
        return props != null && props.exists() && props.canRead();
    }

}
