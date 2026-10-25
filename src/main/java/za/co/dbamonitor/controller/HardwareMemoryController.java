
package za.co.dbamonitor.controller;

import jakarta.validation.Valid;
import za.co.dbamonitor.dto.SqlConnectionRequest;
import za.co.dbamonitor.service.HardwareMemoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class HardwareMemoryController {

    private final HardwareMemoryService hardwareMemoryService;

    public HardwareMemoryController(
            HardwareMemoryService hardwareMemoryService) {
        this.hardwareMemoryService = hardwareMemoryService;
    }

    @PostMapping("/hardware-memory")
    public ResponseEntity<?> getHardwareMemory(
            @Valid @RequestBody SqlConnectionRequest request) {

        try {
            return ResponseEntity.ok(
                    hardwareMemoryService.getHardwareMemory(request));

        } catch (SQLException exception) {
            // Log the details locally for debugging.
            exception.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Could not retrieve hardware and memory metrics. "
                                    + "Check monitoring permissions."
                    )
            );
        }
    }
}
