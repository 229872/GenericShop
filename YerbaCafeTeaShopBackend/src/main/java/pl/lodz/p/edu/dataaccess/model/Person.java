package pl.lodz.p.edu.dataaccess.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "people")
public class Person extends AbstractEntity {

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Delegate
    @OneToOne
    @JoinColumn(unique = true)
    private Address address;

}
