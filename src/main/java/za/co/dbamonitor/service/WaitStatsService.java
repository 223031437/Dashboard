
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
public class WaitStatsService {

    public List<Map<String, Object>> getWaitStats(
            SqlConnectionRequest request) throws SQLException {

        String url = String.format(
                "jdbc:sqlserver://%s:%d;databaseName=master;"
                        + "encrypt=true;trustServerCertificate=true;loginTimeout=10",
                request.server(),
                request.port()
        );

        String sql = """
                SELECT TOP (30)
                    wait_type AS waitType,
                    waiting_tasks_count AS waitingTasksCount,
                    CAST(wait_time_ms / 1000.0 AS DECIMAL(18,2))
                        AS waitTimeSeconds,
                    CAST(signal_wait_time_ms / 1000.0 AS DECIMAL(18,2))
                        AS signalWaitSeconds,
                    CAST(
                        (wait_time_ms - signal_wait_time_ms) / 1000.0
                        AS DECIMAL(18,2)
                    ) AS resourceWaitSeconds,
                    CAST(
                        100.0 * wait_time_ms
                        / NULLIF(SUM(wait_time_ms) OVER (), 0)
                        AS DECIMAL(6,2)
                    ) AS percentOfReturnedWaitTime
                FROM sys.dm_os_wait_stats
                WHERE wait_time_ms > 0
                  AND wait_type NOT LIKE 'SLEEP%'
                  AND wait_type NOT LIKE 'BROKER%'
                  AND wait_type NOT LIKE 'XE_TIMER%'
                  AND wait_type NOT IN (
                      'CLR_AUTO_EVENT',
                      'CLR_MANUAL_EVENT',
                      'LAZYWRITER_SLEEP',
                      'RESOURCE_QUEUE',
                      'SQLTRACE_BUFFER_FLUSH',
                      'WAITFOR',
                      'LOGMGR_QUEUE',
                      'CHECKPOINT_QUEUE',
                      'REQUEST_FOR_DEADLOCK_SEARCH',
                      'XE_DISPATCHER_WAIT',
                      'XE_DISPATCHER_JOIN'
                  )
                ORDER BY wait_time_ms DESC;
                """;

        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                url, request.username(), request.password());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();

                row.put("waitType", resultSet.getString("waitType"));
                row.put("waitingTasksCount",
                        resultSet.getLong("waitingTasksCount"));
                row.put("waitTimeSeconds",
                        resultSet.getBigDecimal("waitTimeSeconds"));
                row.put("signalWaitSeconds",
                        resultSet.getBigDecimal("signalWaitSeconds"));
                row.put("resourceWaitSeconds",
                        resultSet.getBigDecimal("resourceWaitSeconds"));
                row.put("percentOfReturnedWaitTime",
                        resultSet.getBigDecimal("percentOfReturnedWaitTime"));

                results.add(row);
            }
        }

        return results;
    }
}
