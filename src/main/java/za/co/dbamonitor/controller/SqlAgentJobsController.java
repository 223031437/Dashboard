
package za.co.dbamonitor.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import za.co.dbamonitor.dto.SqlConnectionRequest;
import za.co.dbamonitor.service.SqlAgentJobsService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class SqlAgentJobsController {

    private final SqlAgentJobsService service;

    public SqlAgentJobsController(SqlAgentJobsService service) {
        this.service = service;
    }

    @PostMapping("/sql-agent-jobs")
    public ResponseEntity<?> getSqlAgentJobs(
            @Valid @RequestBody SqlConnectionRequest request) {
        try {
            List<Map<String, Object>> jobs =
                    service.getSqlAgentJobs(request);

            return ResponseEntity.ok(jobs);

        } catch (SQLException exception) {
            exception.printStackTrace();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "Unable to retrieve SQL Server Agent jobs. "
                                    + "Check msdb permissions and connection details."
                    ));
        }
    }
}
