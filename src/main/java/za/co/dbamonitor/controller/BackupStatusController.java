
package za.co.dbamonitor.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.dbamonitor.dto.SqlConnectionRequest;
import za.co.dbamonitor.service.BackupStatusService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class BackupStatusController {

    private final BackupStatusService service;

    public BackupStatusController(BackupStatusService service) {
        this.service = service;
    }

    @PostMapping("/backup-status")
    public ResponseEntity<?> getBackupStatus(
            @Valid @RequestBody SqlConnectionRequest request) {
        try {
            List<Map<String, Object>> backups =
                    service.getBackupStatus(request);

            return ResponseEntity.ok(backups);
        } catch (SQLException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                            "success", false,
                            "message", "Unable to retrieve backup history. "
                                    + "Check msdb permissions and connection details."
                    )
            );
        }
    }
}
