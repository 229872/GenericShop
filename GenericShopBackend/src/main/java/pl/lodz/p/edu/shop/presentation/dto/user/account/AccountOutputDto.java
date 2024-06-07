package pl.lodz.p.edu.shop.presentation.dto.user.account;

import lombok.Builder;
import pl.lodz.p.edu.shop.presentation.dto.user.address.AddressOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.user.log.AuthLogOutputDto;

import java.util.List;

@Builder
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
