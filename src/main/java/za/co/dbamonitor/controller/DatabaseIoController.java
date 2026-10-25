
package za.co.dbamonitor.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.dbamonitor.dto.SqlConnectionRequest;
import za.co.dbamonitor.service.DatabaseIoService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class DatabaseIoController {

    private final DatabaseIoService service;

    public DatabaseIoController(DatabaseIoService service) {
        this.service = service;
    }

    @PostMapping("/database-io")
    public ResponseEntity<?> getDatabaseIo(
            @Valid @RequestBody SqlConnectionRequest request) {
        try {
            List<Map<String, Object>> metrics =
                    service.getDatabaseIo(request);

            return ResponseEntity.ok(metrics);
        } catch (SQLException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                            "success", false,
                            "message", "Unable to retrieve database I/O metrics. "
                                    + "Check SQL Server permissions and connection details."
                    )
            );
        }
    }
}
