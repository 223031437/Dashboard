
package za.co.dbamonitor.service;

import org.springframework.stereotype.Service;

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

import za.co.dbamonitor.dto.SqlConnectionRequest;

@Service
public class LongRunningQueriesService {

    public List<Map<String, Object>> getLongRunningQueries(
            SqlConnectionRequest request) throws SQLException {

        String url = "jdbc:sqlserver://" + request.server()
                + ":" + request.port()
                + ";databaseName=" + request.database()
                + ";encrypt=true;trustServerCertificate=true;loginTimeout=10";

        Properties properties = new Properties();
        properties.setProperty("user", request.username());
        properties.setProperty("password", request.password());

        String sql = """
            SELECT
                r.session_id AS sessionId,
                s.login_name AS loginName,
                DB_NAME(r.database_id) AS databaseName,
                r.status AS status,
                r.command AS command,
                r.cpu_time AS cpuTimeMs,
                r.total_elapsed_time AS elapsedTimeMs,
                r.wait_type AS waitType,
                r.blocking_session_id AS blockingSessionId,
                SUBSTRING(
                    t.text,
                    (r.statement_start_offset / 2) + 1,
                    (
                        (
                            CASE r.statement_end_offset
                                WHEN -1 THEN DATALENGTH(t.text)
                                ELSE r.statement_end_offset
                            END
                            - r.statement_start_offset
                        ) / 2
                    ) + 1
                ) AS queryText
            FROM sys.dm_exec_requests AS r
            INNER JOIN sys.dm_exec_sessions AS s
                ON r.session_id = s.session_id
            CROSS APPLY sys.dm_exec_sql_text(r.sql_handle) AS t
            WHERE r.session_id <> @@SPID
              AND r.total_elapsed_time >= 5000
            ORDER BY r.total_elapsed_time DESC;
            """;

        List<Map<String, Object>> queries = new ArrayList<>();

        try (Connection connection =
                     DriverManager.getConnection(url, properties);
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Map<String, Object> query = new LinkedHashMap<>();

                query.put("sessionId", resultSet.getInt("sessionId"));
                query.put("loginName", resultSet.getString("loginName"));
                query.put("databaseName", resultSet.getString("databaseName"));
                query.put("status", resultSet.getString("status"));
                query.put("command", resultSet.getString("command"));
                query.put("cpuTimeMs", resultSet.getLong("cpuTimeMs"));
                query.put("elapsedTimeMs", resultSet.getLong("elapsedTimeMs"));
                query.put("waitType", resultSet.getString("waitType"));
                query.put("blockingSessionId",
                        resultSet.getInt("blockingSessionId"));
                query.put("queryText", resultSet.getString("queryText"));

                queries.add(query);
            }
        }

        return queries;
    }
}
