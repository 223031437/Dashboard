
package za.co.dbamonitor.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import za.co.dbamonitor.dto.SqlConnectionRequest;
import za.co.dbamonitor.service.LongRunningQueriesService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class LongRunningQueriesController {

    private final LongRunningQueriesService service;

    public LongRunningQueriesController(
            LongRunningQueriesService service) {
        this.service = service;
    }

    @PostMapping("/long-running-queries")
    public ResponseEntity<?> getLongRunningQueries(
            @Valid @RequestBody SqlConnectionRequest request) {
        try {
            List<Map<String, Object>> queries =
                    service.getLongRunningQueries(request);

            return ResponseEntity.ok(queries);

        } catch (SQLException exception) {
            exception.printStackTrace();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "Unable to retrieve long-running queries. "
                                    + "Check SQL Server permissions and connection details."
                    ));
        }
    }
}
