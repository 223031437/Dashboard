
package za.co.dbamonitor.controller;

import jakarta.validation.Valid;
import za.co.dbamonitor.dto.SqlConnectionRequest;
import za.co.dbamonitor.dto.ConnectionTestResponse;
import za.co.dbamonitor.service.ConnectionService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping("/test")
    public ResponseEntity<ConnectionTestResponse> testConnection(
            @Valid @RequestBody SqlConnectionRequest request) {

        ConnectionTestResponse response =
                connectionService.testConnection(request);

        if (!response.success()) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}

