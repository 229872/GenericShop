package pl.lodz.p.edu.logic.model;

import lombok.Builder;

@Builder
public record NewContactData(
    String firstName,
    String lastName,
    String postalCode,
    String country,
    String city,
    String street,
    Integer houseNumber
) {

}
