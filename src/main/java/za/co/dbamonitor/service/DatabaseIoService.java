
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
public class DatabaseIoService {

    public List<Map<String, Object>> getDatabaseIo(
            SqlConnectionRequest request) throws SQLException {

        String url = String.format(
                "jdbc:sqlserver://%s:%d;databaseName=master;"
                        + "encrypt=true;trustServerCertificate=true;loginTimeout=10",
                request.server(),
                request.port()
        );

        String sql = """
                SELECT
                    DB_NAME(mf.database_id) AS databaseName,
                    mf.name AS logicalFileName,
                    mf.type_desc AS fileType,
                    mf.physical_name AS physicalFileName,
                    vfs.num_of_reads AS numOfReads,
                    vfs.num_of_writes AS numOfWrites,
                    CAST(vfs.num_of_bytes_read / 1048576.0
                        AS DECIMAL(18,2)) AS mbRead,
                    CAST(vfs.num_of_bytes_written / 1048576.0
                        AS DECIMAL(18,2)) AS mbWritten,
                    vfs.io_stall_read_ms AS ioStallReadMs,
                    vfs.io_stall_write_ms AS ioStallWriteMs,
                    CAST(
                        vfs.io_stall_read_ms * 1.0
                        / NULLIF(vfs.num_of_reads, 0)
                        AS DECIMAL(18,2)
                    ) AS averageReadLatencyMs,
                    CAST(
                        vfs.io_stall_write_ms * 1.0
                        / NULLIF(vfs.num_of_writes, 0)
                        AS DECIMAL(18,2)
                    ) AS averageWriteLatencyMs
                FROM sys.dm_io_virtual_file_stats(NULL, NULL) AS vfs
                INNER JOIN sys.master_files AS mf
                    ON vfs.database_id = mf.database_id
                   AND vfs.file_id = mf.file_id
                ORDER BY
                    vfs.io_stall_read_ms + vfs.io_stall_write_ms DESC;
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
                row.put("logicalFileName",
                        resultSet.getString("logicalFileName"));
                row.put("fileType",
                        resultSet.getString("fileType"));
                row.put("physicalFileName",
                        resultSet.getString("physicalFileName"));
                row.put("numOfReads",
                        resultSet.getLong("numOfReads"));
                row.put("numOfWrites",
                        resultSet.getLong("numOfWrites"));
                row.put("mbRead",
                        resultSet.getBigDecimal("mbRead"));
                row.put("mbWritten",
                        resultSet.getBigDecimal("mbWritten"));
                row.put("ioStallReadMs",
                        resultSet.getLong("ioStallReadMs"));
                row.put("ioStallWriteMs",
                        resultSet.getLong("ioStallWriteMs"));
                row.put("averageReadLatencyMs",
                        resultSet.getBigDecimal("averageReadLatencyMs"));
                row.put("averageWriteLatencyMs",
                        resultSet.getBigDecimal("averageWriteLatencyMs"));

                results.add(row);
            }
        }

        return results;
    }
}
