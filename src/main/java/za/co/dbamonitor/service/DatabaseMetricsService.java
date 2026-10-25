
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
public class DatabaseMetricsService {

    public List<Map<String, Object>> getDatabaseMetrics(
            SqlConnectionRequest request) throws SQLException {

        String url = String.format(
                "jdbc:sqlserver://%s:%d;databaseName=master;"
                        + "encrypt=true;trustServerCertificate=true;loginTimeout=10",
                request.server(),
                request.port()
        );

        String sql = """
                SELECT
                    d.name AS databaseName,
                    d.state_desc AS databaseState,
                    d.recovery_model_desc AS recoveryModel,
                    CAST(SUM(mf.size) * 8.0 / 1024 AS DECIMAL(18,2))
                        AS allocatedSizeMB,
                    CAST(SUM(
                        CASE
                            WHEN mf.type_desc = 'ROWS'
                            THEN FILEPROPERTY(mf.name, 'SpaceUsed')
                            ELSE 0
                        END
                    ) * 8.0 / 1024 AS DECIMAL(18,2))
                        AS usedDataSpaceMB,
                    CAST(SUM(
                        CASE
                            WHEN mf.type_desc = 'ROWS'
                            THEN mf.size -
                                 FILEPROPERTY(mf.name, 'SpaceUsed')
                            ELSE 0
                        END
                    ) * 8.0 / 1024 AS DECIMAL(18,2))
                        AS freeDataSpaceMB
                FROM sys.databases AS d
                LEFT JOIN sys.master_files AS mf
                    ON d.database_id = mf.database_id
                GROUP BY
                    d.name,
                    d.state_desc,
                    d.recovery_model_desc
                ORDER BY d.name;
                """;

        List<Map<String, Object>> metrics = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                url, request.username(), request.password());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();

                row.put("databaseName",
                        resultSet.getString("databaseName"));
                row.put("databaseState",
                        resultSet.getString("databaseState"));
                row.put("recoveryModel",
                        resultSet.getString("recoveryModel"));
                row.put("allocatedSizeMB",
                        resultSet.getBigDecimal("allocatedSizeMB"));
                row.put("usedDataSpaceMB",
                        resultSet.getBigDecimal("usedDataSpaceMB"));
                row.put("freeDataSpaceMB",
                        resultSet.getBigDecimal("freeDataSpaceMB"));

                metrics.add(row);
            }
        }

        return metrics;
    }
}
