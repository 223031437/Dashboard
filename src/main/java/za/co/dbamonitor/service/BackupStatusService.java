
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
public class BackupStatusService {

    public List<Map<String, Object>> getBackupStatus(
            SqlConnectionRequest request) throws SQLException {

        String url = String.format(
                "jdbc:sqlserver://%s:%d;databaseName=msdb;"
                        + "encrypt=true;trustServerCertificate=true;loginTimeout=10",
                request.server(),
                request.port()
        );

        String sql = """
                WITH LatestBackups AS (
                    SELECT
                        database_name,
                        type,
                        backup_finish_date,
                        backup_size,
                        backup_set_id,
                        ROW_NUMBER() OVER (
                            PARTITION BY database_name, type
                            ORDER BY backup_finish_date DESC, backup_set_id DESC
                        ) AS rn
                    FROM dbo.backupset
                    WHERE type IN ('D', 'I', 'L')
                      AND backup_finish_date IS NOT NULL
                )
                SELECT
                    d.name AS databaseName,
                    d.state_desc AS databaseState,

                    fullBackup.backup_finish_date AS lastFullBackup,
                    CAST(fullBackup.backup_size / 1048576.0
                        AS DECIMAL(18,2)) AS lastFullBackupSizeMB,

                    diffBackup.backup_finish_date AS lastDifferentialBackup,
                    CAST(diffBackup.backup_size / 1048576.0
                        AS DECIMAL(18,2)) AS lastDifferentialBackupSizeMB,

                    logBackup.backup_finish_date AS lastLogBackup,
                    CAST(logBackup.backup_size / 1048576.0
                        AS DECIMAL(18,2)) AS lastLogBackupSizeMB

                FROM sys.databases AS d

                LEFT JOIN LatestBackups AS fullBackup
                    ON fullBackup.database_name = d.name
                   AND fullBackup.type = 'D'
                   AND fullBackup.rn = 1

                LEFT JOIN LatestBackups AS diffBackup
                    ON diffBackup.database_name = d.name
                   AND diffBackup.type = 'I'
                   AND diffBackup.rn = 1

                LEFT JOIN LatestBackups AS logBackup
                    ON logBackup.database_name = d.name
                   AND logBackup.type = 'L'
                   AND logBackup.rn = 1

                ORDER BY d.name;
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
                row.put("databaseState",
                        resultSet.getString("databaseState"));

                row.put("lastFullBackup",
                        resultSet.getTimestamp("lastFullBackup"));
                row.put("lastFullBackupSizeMB",
                        resultSet.getBigDecimal("lastFullBackupSizeMB"));

                row.put("lastDifferentialBackup",
                        resultSet.getTimestamp("lastDifferentialBackup"));
                row.put("lastDifferentialBackupSizeMB",
                        resultSet.getBigDecimal("lastDifferentialBackupSizeMB"));

                row.put("lastLogBackup",
                        resultSet.getTimestamp("lastLogBackup"));
                row.put("lastLogBackupSizeMB",
                        resultSet.getBigDecimal("lastLogBackupSizeMB"));

                results.add(row);
            }
        }

        return results;
    }
}
