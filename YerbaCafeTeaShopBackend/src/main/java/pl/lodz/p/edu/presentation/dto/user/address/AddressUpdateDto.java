package pl.lodz.p.edu.presentation.dto.user.address;

import lombok.Builder;

@Builder
public record AddressUpdateDto(
    String postalCode,
    String country,
    String city,
    String street,
    Integer houseNumber
) {
}
