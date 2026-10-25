
package za.co.dbamonitor.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.dbamonitor.dto.SqlConnectionRequest;
import za.co.dbamonitor.service.BlockingSessionsService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class BlockingSessionsController {

    private final BlockingSessionsService service;

    public BlockingSessionsController(BlockingSessionsService service) {
        this.service = service;
    }

    @PostMapping("/blocking-sessions")
    public ResponseEntity<?> getBlockingSessions(
            @Valid @RequestBody SqlConnectionRequest request) {
        try {
            List<Map<String, Object>> sessions =
                    service.getBlockingSessions(request);

            return ResponseEntity.ok(sessions);
        } catch (SQLException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                            "success", false,
                            "message", "Unable to retrieve blocking sessions. "
                                    + "Check SQL Server permissions and connection details."
                    )
            );
        }
    }
}
