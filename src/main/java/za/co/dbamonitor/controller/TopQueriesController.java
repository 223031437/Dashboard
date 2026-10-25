
package za.co.dbamonitor.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.dbamonitor.dto.SqlConnectionRequest;
import za.co.dbamonitor.service.TopQueriesService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class TopQueriesController {

    private final TopQueriesService service;

    public TopQueriesController(TopQueriesService service) {
        this.service = service;
    }

    @PostMapping("/top-queries")
    public ResponseEntity<?> getTopQueries(
            @Valid @RequestBody SqlConnectionRequest request) {
        try {
            List<Map<String, Object>> queries =
                    service.getTopQueries(request);

            return ResponseEntity.ok(queries);
        } catch (SQLException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                            "success", false,
                            "message", "Unable to retrieve top queries. "
                                    + "Check SQL Server permissions and connection details."
                    )
            );
        }
    }
}
