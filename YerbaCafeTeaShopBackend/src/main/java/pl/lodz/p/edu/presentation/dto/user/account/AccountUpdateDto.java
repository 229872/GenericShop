package pl.lodz.p.edu.presentation.dto.user.account;

import lombok.Builder;
import pl.lodz.p.edu.presentation.dto.user.address.AddressCreateDto;

@Builder
public record AccountUpdateDto(
    String firstName,
    String lastName,
    AddressCreateDto address,
    String accountState,
    String role
) {
}
