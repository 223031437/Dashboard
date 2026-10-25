
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
public class TopQueriesService {

    public List<Map<String, Object>> getTopQueries(
            SqlConnectionRequest request) throws SQLException {

        String url = String.format(
                "jdbc:sqlserver://%s:%d;databaseName=master;"
                        + "encrypt=true;trustServerCertificate=true;loginTimeout=10",
                request.server(),
                request.port()
        );

        String sql = """
                SELECT TOP (50)
                    DB_NAME(st.dbid) AS databaseName,
                    qs.execution_count AS executionCount,
                    CAST(qs.total_worker_time / 1000.0
                        AS DECIMAL(18,2)) AS totalCpuMs,
                    CAST(qs.total_worker_time * 1.0
                        / NULLIF(qs.execution_count, 0) / 1000.0
                        AS DECIMAL(18,2)) AS averageCpuMs,
                    CAST(qs.total_elapsed_time / 1000.0
                        AS DECIMAL(18,2)) AS totalElapsedMs,
                    CAST(qs.total_elapsed_time * 1.0
                        / NULLIF(qs.execution_count, 0) / 1000.0
                        AS DECIMAL(18,2)) AS averageElapsedMs,
                    qs.total_logical_reads AS totalLogicalReads,
                    qs.total_logical_writes AS totalLogicalWrites,
                    qs.last_execution_time AS lastExecutionTime,
                    SUBSTRING(
                        st.text,
                        (qs.statement_start_offset / 2) + 1,
                        (
                            (
                                CASE
                                    WHEN qs.statement_end_offset = -1
                                    THEN DATALENGTH(st.text)
                                    ELSE qs.statement_end_offset
                                END - qs.statement_start_offset
                            ) / 2
                        ) + 1
                    ) AS sqlStatement
                FROM sys.dm_exec_query_stats AS qs
                CROSS APPLY sys.dm_exec_sql_text(qs.sql_handle) AS st
                WHERE qs.execution_count > 0
                ORDER BY qs.total_worker_time DESC;
                """;

        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                url, request.username(), request.password());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();

                row.put("databaseName",
                        resultSet.getString("databaseName"));
                row.put("executionCount",
                        resultSet.getLong("executionCount"));
                row.put("totalCpuMs",
                        resultSet.getBigDecimal("totalCpuMs"));
                row.put("averageCpuMs",
                        resultSet.getBigDecimal("averageCpuMs"));
                row.put("totalElapsedMs",
                        resultSet.getBigDecimal("totalElapsedMs"));
                row.put("averageElapsedMs",
                        resultSet.getBigDecimal("averageElapsedMs"));
                row.put("totalLogicalReads",
                        resultSet.getLong("totalLogicalReads"));
                row.put("totalLogicalWrites",
                        resultSet.getLong("totalLogicalWrites"));
                row.put("lastExecutionTime",
                        resultSet.getTimestamp("lastExecutionTime"));
                row.put("sqlStatement",
                        resultSet.getString("sqlStatement"));

                results.add(row);
            }
        }

        return results;
    }
}
