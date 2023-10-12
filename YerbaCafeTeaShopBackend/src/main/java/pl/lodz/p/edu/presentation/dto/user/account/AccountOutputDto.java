package pl.lodz.p.edu.presentation.dto.user.account;

import lombok.Builder;
import pl.lodz.p.edu.presentation.dto.user.address.AddressOutputDto;

import java.util.List;

@Builder
public record AccountOutputDto(
    Long id,
    String login,
    String email,
    String locale,
    String firstName,
    String lastName,
    AddressOutputDto address,
    String state,
    List<String> roles
) {
}
