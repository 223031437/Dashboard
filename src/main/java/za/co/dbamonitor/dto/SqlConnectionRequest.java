
package za.co.dbamonitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record SqlConnectionRequest(
        @NotBlank String server,
        @NotNull @Min(1) @Max(65535) Integer port,
        @NotBlank String database,
        @NotBlank String username,
        @NotBlank String password
) {
}
