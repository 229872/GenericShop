package pl.lodz.p.edu.genericshopdesktopfrontend.model;

public record AddressOutputDto(
    String postalCode,
    String country,
    String city,
    String street,
    Integer houseNumber
) {
}