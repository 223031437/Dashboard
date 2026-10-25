
package za.co.dbamonitor.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import za.co.dbamonitor.dto.SqlConnectionRequest;
import za.co.dbamonitor.service.ActiveSessionsService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class ActiveSessionsController {

    private final ActiveSessionsService service;

    public ActiveSessionsController(ActiveSessionsService service) {
        this.service = service;
    }

    @PostMapping("/active-sessions")
    public ResponseEntity<?> getActiveSessions(
            @Valid @RequestBody SqlConnectionRequest request) {
        try {
            List<Map<String, Object>> sessions =
                    service.getActiveSessions(request);

            return ResponseEntity.ok(sessions);

        } catch (SQLException exception) {
            exception.printStackTrace();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "Unable to retrieve active sessions. "
                                    + "Check SQL Server permissions and connection details."
                    ));
        }
    }
}
