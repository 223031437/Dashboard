
package za.co.dbamonitor.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.dbamonitor.dto.SqlConnectionRequest;
import za.co.dbamonitor.service.WaitStatsService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class WaitStatsController {

    private final WaitStatsService service;

    public WaitStatsController(WaitStatsService service) {
        this.service = service;
    }

    @PostMapping("/wait-stats")
    public ResponseEntity<?> getWaitStats(
            @Valid @RequestBody SqlConnectionRequest request) {
        try {
            List<Map<String, Object>> stats =
                    service.getWaitStats(request);

            return ResponseEntity.ok(stats);
        } catch (SQLException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                            "success", false,
                            "message", "Unable to retrieve wait statistics. "
                                    + "Check SQL Server permissions and connection details."
                    )
            );
        }
    }
}
