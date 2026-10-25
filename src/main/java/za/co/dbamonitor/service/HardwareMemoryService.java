
package za.co.dbamonitor.service;

import za.co.dbamonitor.dto.SqlConnectionRequest;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class HardwareMemoryService {

    public Map<String, Object> getHardwareMemory(
            SqlConnectionRequest request) throws SQLException {

        String url = "jdbc:sqlserver://"
                + request.server() + ":"
                + request.port()
                + ";databaseName=" + request.database()
                + ";encrypt=true"
                + ";trustServerCertificate=true"
                + ";loginTimeout=10";

        Properties properties = new Properties();
        properties.setProperty("user", request.username());
        properties.setProperty("password", request.password());

        Map<String, Object> metrics = new LinkedHashMap<>();

        String sql = """
            SELECT
                cpu_count,
                scheduler_count,
                physical_memory_kb,
                committed_kb,
                committed_target_kb,
                sqlserver_start_time
            FROM sys.dm_os_sys_info
            """;

        try (Connection connection =
                     DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            if (result.next()) {
                long totalMemoryKb = result.getLong("physical_memory_kb");
                long committedKb = result.getLong("committed_kb");
                long targetKb = result.getLong("committed_target_kb");

                metrics.put("cpuCount", result.getInt("cpu_count"));
                metrics.put("schedulerCount", result.getInt("scheduler_count"));
                metrics.put("totalMemoryGB",
                        Math.round(totalMemoryKb / 1048576.0 * 100.0) / 100.0);
                metrics.put("sqlCommittedMemoryGB",
                        Math.round(committedKb / 1048576.0 * 100.0) / 100.0);
                metrics.put("sqlCommittedTargetGB",
                        Math.round(targetKb / 1048576.0 * 100.0) / 100.0);
                metrics.put("sqlServerStartTime",
                        result.getTimestamp("sqlserver_start_time"));
            }
        }

        return metrics;
    }
}

