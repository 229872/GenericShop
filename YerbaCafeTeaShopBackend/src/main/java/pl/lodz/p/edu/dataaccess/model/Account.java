package pl.lodz.p.edu.dataaccess.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.edu.dataaccess.model.sub.AccountRole;
import pl.lodz.p.edu.dataaccess.model.sub.AccountState;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "accounts")
public class Account extends AbstractEntity {

    @Column(nullable = false, updatable = false, unique = true)
    private String login;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String locale;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(unique = true)
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "state")
    private AccountState accountState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "roles")
    private Set<AccountRole> accountRoles = new HashSet<>();

    @Embedded
    private AuthLogs authLogs;

    public Long getSumOfVersions() {
        return getVersion() + person.getVersion() + person.getAddress().getVersion();
    }
}
