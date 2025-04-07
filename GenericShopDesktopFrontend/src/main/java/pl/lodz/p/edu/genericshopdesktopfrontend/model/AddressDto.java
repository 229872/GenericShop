package pl.lodz.p.edu.genericshopdesktopfrontend.model;

public record AddressDto(
    String postalCode,
    String country,
    String city,
    String street,
    Integer houseNumber
) {
}