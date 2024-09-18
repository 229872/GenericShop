package pl.lodz.p.edu.shop.dataaccess.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.edu.shop.dataaccess.model.superclass.ArchivableEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "addresses")
public class Address extends ArchivableEntity {

    @Column(nullable = false, name = "postal_code")
    private String postalCode;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false, name = "house_number")
    private Integer houseNumber;
}
