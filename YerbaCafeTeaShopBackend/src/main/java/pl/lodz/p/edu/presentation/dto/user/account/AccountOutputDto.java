package pl.lodz.p.edu.presentation.dto.user.account;

import lombok.Builder;
import pl.lodz.p.edu.presentation.dto.user.address.AddressOutputDto;
import pl.lodz.p.edu.presentation.dto.user.log.AuthLogOutputDto;

import java.util.List;

@Builder
public record AccountOutputDto(
    Long id,
    Boolean archival,
    String login,
    String email,
    String locale,
    String firstName,
    String lastName,
    AddressOutputDto address,
    String state,
    List<String> roles,
    AuthLogOutputDto authLogs
) {
}
