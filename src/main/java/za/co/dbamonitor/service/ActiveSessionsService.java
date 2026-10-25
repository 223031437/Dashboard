
package za.co.dbamonitor.service;

import org.springframework.stereotype.Service;
import za.co.dbamonitor.dto.SqlConnectionRequest;

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

@Service
public class ActiveSessionsService {

    public List<Map<String, Object>> getActiveSessions(
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
                s.session_id AS sessionId,
                s.login_name AS loginName,
                s.host_name AS hostName,
                s.program_name AS programName,
                s.status AS sessionStatus,
                s.login_time AS loginTime,
                s.last_request_start_time AS lastRequestStartTime,
                s.last_request_end_time AS lastRequestEndTime,
                s.cpu_time AS cpuTimeMs,
                s.memory_usage AS memoryPages,
                s.reads AS reads,
                s.writes AS writes,
                s.logical_reads AS logicalReads,
                c.client_net_address AS clientAddress
            FROM sys.dm_exec_sessions AS s
            LEFT JOIN sys.dm_exec_connections AS c
                ON s.session_id = c.session_id
            WHERE s.is_user_process = 1
            ORDER BY s.cpu_time DESC;
            """;

        List<Map<String, Object>> sessions = new ArrayList<>();

        try (Connection connection =
                     DriverManager.getConnection(url, properties);
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Map<String, Object> session = new LinkedHashMap<>();

                session.put("sessionId", resultSet.getInt("sessionId"));
                session.put("loginName", resultSet.getString("loginName"));
                session.put("hostName", resultSet.getString("hostName"));
                session.put("programName", resultSet.getString("programName"));
                session.put("sessionStatus",
                        resultSet.getString("sessionStatus"));
                session.put("loginTime", resultSet.getTimestamp("loginTime"));
                session.put("lastRequestStartTime",
                        resultSet.getTimestamp("lastRequestStartTime"));
                session.put("lastRequestEndTime",
                        resultSet.getTimestamp("lastRequestEndTime"));
                session.put("cpuTimeMs", resultSet.getLong("cpuTimeMs"));
                session.put("memoryPages", resultSet.getLong("memoryPages"));
                session.put("reads", resultSet.getLong("reads"));
                session.put("writes", resultSet.getLong("writes"));
                session.put("logicalReads",
                        resultSet.getLong("logicalReads"));
                session.put("clientAddress",
                        resultSet.getString("clientAddress"));

                sessions.add(session);
            }
        }

        return sessions;
    }
}
