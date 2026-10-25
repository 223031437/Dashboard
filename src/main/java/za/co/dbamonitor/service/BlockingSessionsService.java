
package za.co.dbamonitor.service;

import org.springframework.stereotype.Service;
import za.co.dbamonitor.dto.SqlConnectionRequest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlockingSessionsService {

    public List<Map<String, Object>> getBlockingSessions(
            SqlConnectionRequest request) throws SQLException {

        String url = String.format(
                "jdbc:sqlserver://%s:%d;databaseName=master;"
                        + "encrypt=true;trustServerCertificate=true;loginTimeout=10",
                request.server(),
                request.port()
        );

        String sql = """
                SELECT
                    r.session_id AS blockedSessionId,
                    r.blocking_session_id AS blockingSessionId,
                    DB_NAME(r.database_id) AS databaseName,
                    s.login_name AS blockedLoginName,
                    r.status AS requestStatus,
                    r.command AS command,
                    r.wait_type AS waitType,
                    r.wait_time AS waitTimeMs,
                    r.wait_resource AS waitResource,
                    r.start_time AS requestStartTime,
                    txt.text AS batchText,
                    SUBSTRING(
                        txt.text,
                        (r.statement_start_offset / 2) + 1,
                        (
                            (
                                CASE
                                    WHEN r.statement_end_offset = -1
                                    THEN DATALENGTH(txt.text)
                                    ELSE r.statement_end_offset
                                END - r.statement_start_offset
                            ) / 2
                        ) + 1
                    ) AS sqlStatement
                FROM sys.dm_exec_requests AS r
                INNER JOIN sys.dm_exec_sessions AS s
                    ON r.session_id = s.session_id
                OUTER APPLY sys.dm_exec_sql_text(r.sql_handle) AS txt
                WHERE r.blocking_session_id > 0
                ORDER BY r.wait_time DESC;
                """;

        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                url, request.username(), request.password());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();

                row.put("blockedSessionId",
                        resultSet.getInt("blockedSessionId"));
                row.put("blockingSessionId",
                        resultSet.getInt("blockingSessionId"));
                row.put("databaseName",
                        resultSet.getString("databaseName"));
                row.put("blockedLoginName",
                        resultSet.getString("blockedLoginName"));
                row.put("requestStatus",
                        resultSet.getString("requestStatus"));
                row.put("command",
                        resultSet.getString("command"));
                row.put("waitType",
                        resultSet.getString("waitType"));
                row.put("waitTimeMs",
                        resultSet.getLong("waitTimeMs"));
                row.put("waitResource",
                        resultSet.getString("waitResource"));
                row.put("requestStartTime",
                        resultSet.getTimestamp("requestStartTime"));
                row.put("batchText",
                        resultSet.getString("batchText"));
                row.put("sqlStatement",
                        resultSet.getString("sqlStatement"));

                results.add(row);
            }
        }

        return results;
    }
}
