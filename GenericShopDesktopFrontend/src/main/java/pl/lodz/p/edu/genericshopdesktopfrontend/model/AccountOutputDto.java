package pl.lodz.p.edu.genericshopdesktopfrontend.model;

import java.util.List;

public record AccountOutputDto(
    Long id,
    String version,
    Boolean archival,
    String login,
    String email,
    String locale,
    String firstName,
    String lastName,
    AddressOutputDto address,
    String accountState,
    List<String> accountRoles,
    AuthLogOutputDto authLogs
) {
}
