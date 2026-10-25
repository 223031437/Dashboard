
package za.co.dbamonitor.service;

import org.springframework.stereotype.Service;
import za.co.dbamonitor.dto.SqlConnectionRequest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class SqlAgentJobsService {

    public List<Map<String, Object>> getSqlAgentJobs(
            SqlConnectionRequest request) throws SQLException {

        String url = "jdbc:sqlserver://" + request.server()
                + ":" + request.port()
                + ";databaseName=msdb"
                + ";encrypt=true;trustServerCertificate=true;loginTimeout=10";

        Properties properties = new Properties();
        properties.setProperty("user", request.username());
        properties.setProperty("password", request.password());

        String sql = """
            SELECT
                j.job_id AS jobId,
                j.name AS jobName,
                j.enabled AS enabled,
                j.description AS description,
                j.date_created AS dateCreated,
                j.date_modified AS dateModified,
                h.run_date AS lastRunDate,
                h.run_time AS lastRunTime,
                h.run_duration AS lastRunDuration,
                h.run_status AS lastRunStatus,
                CASE h.run_status
                    WHEN 0 THEN 'Failed'
                    WHEN 1 THEN 'Succeeded'
                    WHEN 2 THEN 'Retry'
                    WHEN 3 THEN 'Canceled'
                    WHEN 4 THEN 'In Progress'
                    ELSE 'Never Run'
                END AS lastRunStatusDescription
            FROM msdb.dbo.sysjobs AS j
            OUTER APPLY (
                SELECT TOP (1)
                    run_date,
                    run_time,
                    run_duration,
                    run_status
                FROM msdb.dbo.sysjobhistory
                WHERE job_id = j.job_id
                  AND step_id = 0
                ORDER BY instance_id DESC
            ) AS h
            ORDER BY j.name;
            """;

        List<Map<String, Object>> jobs = new ArrayList<>();

        try (Connection connection =
                     DriverManager.getConnection(url, properties);
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Map<String, Object> job = new LinkedHashMap<>();

                job.put("jobId", resultSet.getString("jobId"));
                job.put("jobName", resultSet.getString("jobName"));
                job.put("enabled", resultSet.getBoolean("enabled"));
                job.put("description", resultSet.getString("description"));
                job.put("dateCreated", resultSet.getTimestamp("dateCreated"));
                job.put("dateModified", resultSet.getTimestamp("dateModified"));
                job.put("lastRunDate", resultSet.getInt("lastRunDate"));
                job.put("lastRunTime", resultSet.getInt("lastRunTime"));
                job.put("lastRunDuration", resultSet.getInt("lastRunDuration"));
                job.put("lastRunStatus", resultSet.getObject("lastRunStatus"));
                job.put("lastRunStatusDescription",
                        resultSet.getString("lastRunStatusDescription"));

                jobs.add(job);
            }
        }

        return jobs;
    }
}
