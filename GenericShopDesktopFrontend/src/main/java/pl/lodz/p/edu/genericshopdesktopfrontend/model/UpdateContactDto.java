package pl.lodz.p.edu.genericshopdesktopfrontend.model;

public record UpdateContactDto(
    String version,
    String firstName,
    String lastName,
    AddressDto address
) {
}
