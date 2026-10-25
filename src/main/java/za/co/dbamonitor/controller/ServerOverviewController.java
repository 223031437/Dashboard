
package za.co.dbamonitor.controller;

import jakarta.validation.Valid;
import za.co.dbamonitor.dto.SqlConnectionRequest;
import za.co.dbamonitor.service.ServerOverviewService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class ServerOverviewController {

    private final ServerOverviewService overviewService;

    public ServerOverviewController(
            ServerOverviewService overviewService) {
        this.overviewService = overviewService;
    }

    @PostMapping("/overview")
    public ResponseEntity<?> getOverview(
            @Valid @RequestBody SqlConnectionRequest request) {

        try {
            Map<String, Object> overview =
                    overviewService.getOverview(request);

            return ResponseEntity.ok(overview);

        } catch (SQLException exception) {
            exception.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Could not retrieve server overview. "
                                    + "Check connection details and monitoring permissions."
                    )
            );
        }
    }
}
