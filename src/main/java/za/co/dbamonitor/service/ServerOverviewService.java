
package za.co.dbamonitor.service;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.LinkedHashMap;

import za.co.dbamonitor.dto.SqlConnectionRequest;

@Service
public class ServerOverviewService {

    public Map<String, Object> getOverview(
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

        Map<String, Object> overview = new LinkedHashMap<>();

        try (Connection connection =
                     DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("""
                     SELECT
                         @@SERVERNAME AS serverName,
                         CAST(SERVERPROPERTY('ProductVersion')
                             AS varchar(128)) AS sqlServerVersion,
                         CAST(SERVERPROPERTY('Edition')
                             AS varchar(128)) AS edition,
                         DB_NAME() AS databaseName,
                         SYSUTCDATETIME() AS serverUtcTime,
                         sqlserver_start_time
                     FROM sys.dm_os_sys_info
                     """)) {

            if (result.next()) {
                overview.put("serverName",
                        result.getString("serverName"));
                overview.put("sqlServerVersion",
                        result.getString("sqlServerVersion"));
                overview.put("edition",
                        result.getString("edition"));
                overview.put("databaseName",
                        result.getString("databaseName"));
                overview.put("serverUtcTime",
                        result.getTimestamp("serverUtcTime"));
                overview.put("sqlServerStartTime",
                        result.getTimestamp("sqlserver_start_time"));
            }
        }

        return overview;
    }
}
