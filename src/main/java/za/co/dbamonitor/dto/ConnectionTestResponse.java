
package za.co.dbamonitor.dto;

public record ConnectionTestResponse(
        boolean success,
        String message,
        String serverName,
        String databaseName
) {
}
