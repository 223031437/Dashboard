
package za.co.dbamonitor.service;

import za.co.dbamonitor.dto.SqlConnectionRequest;
import za.co.dbamonitor.dto.ConnectionTestResponse;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Properties;

@Service
public class ConnectionService {

    public ConnectionTestResponse testConnection(
            SqlConnectionRequest request) {

        String url = "jdbc:sqlserver://" + request.server()
                + ":" + request.port()
                + ";databaseName=" + request.database()
                + ";encrypt=true"
                + ";trustServerCertificate=true"
                + ";loginTimeout=10";

        Properties properties = new Properties();
        properties.setProperty("user", request.username());
        properties.setProperty("password", request.password());

        try (Connection connection =
                     DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(
                     "SELECT @@SERVERNAME AS serverName, "
                             + "DB_NAME() AS databaseName")) {

            if (result.next()) {
                return new ConnectionTestResponse(
                        true,
                        "Connection successful.",
                        result.getString("serverName"),
                        result.getString("databaseName")
                );
            }

            return new ConnectionTestResponse(
                    false, "Connected, but no server information returned.",
                    null, null
            );

        } catch (SQLException exception) {
            // Do not return credentials or raw SQL error details.
            return new ConnectionTestResponse(
                    false,
                    "Connection failed. Check the server, port, "
                            + "database, credentials and SQL Server settings.",
                    null,
                    null
            );
        }
    }
}
