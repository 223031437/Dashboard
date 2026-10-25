
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
public class MissingIndexesService {

    public List<Map<String, Object>> getMissingIndexes(
            SqlConnectionRequest request) throws SQLException {

        String url = String.format(
                "jdbc:sqlserver://%s:%d;databaseName=master;"
                        + "encrypt=true;trustServerCertificate=true;loginTimeout=10",
                request.server(),
                request.port()
        );

        String sql = """
                SELECT TOP (100)
                    DB_NAME(mid.database_id) AS databaseName,
                    OBJECT_SCHEMA_NAME(mid.object_id, mid.database_id)
                        AS schemaName,
                    OBJECT_NAME(mid.object_id, mid.database_id)
                        AS tableName,
                    mid.equality_columns AS equalityColumns,
                    mid.inequality_columns AS inequalityColumns,
                    mid.included_columns AS includedColumns,
                    migs.user_seeks AS userSeeks,
                    migs.user_scans AS userScans,
                    CAST(migs.avg_user_impact AS DECIMAL(5,2))
                        AS averageImpactPercent,
                    CAST(
                        migs.avg_total_user_cost
                        * migs.avg_user_impact
                        * (migs.user_seeks + migs.user_scans)
                        AS DECIMAL(18,2)
                    ) AS improvementScore
                FROM sys.dm_db_missing_index_group_stats AS migs
                INNER JOIN sys.dm_db_missing_index_groups AS mig
                    ON migs.group_handle = mig.index_group_handle
                INNER JOIN sys.dm_db_missing_index_details AS mid
                    ON mig.index_handle = mid.index_handle
                WHERE mid.database_id IS NOT NULL
                  AND mid.object_id IS NOT NULL
                ORDER BY improvementScore DESC;
                """;

        List<Map<String, Object>> indexes = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                url, request.username(), request.password());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();

                row.put("databaseName",
                        resultSet.getString("databaseName"));
                row.put("schemaName",
                        resultSet.getString("schemaName"));
                row.put("tableName",
                        resultSet.getString("tableName"));
                row.put("equalityColumns",
                        resultSet.getString("equalityColumns"));
                row.put("inequalityColumns",
                        resultSet.getString("inequalityColumns"));
                row.put("includedColumns",
                        resultSet.getString("includedColumns"));
                row.put("userSeeks",
                        resultSet.getLong("userSeeks"));
                row.put("userScans",
                        resultSet.getLong("userScans"));
                row.put("averageImpactPercent",
                        resultSet.getBigDecimal("averageImpactPercent"));
                row.put("improvementScore",
                        resultSet.getBigDecimal("improvementScore"));

                indexes.add(row);
            }
        }

        return indexes;
    }
}
