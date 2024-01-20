package pl.lodz.p.edu.dataaccess.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.edu.dataaccess.model.embeddable.AuthLogs;
import pl.lodz.p.edu.dataaccess.model.enumerated.AccountRole;
import pl.lodz.p.edu.dataaccess.model.enumerated.AccountState;
import pl.lodz.p.edu.dataaccess.model.superclass.AbstractEntity;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "accounts")
public class Account extends AbstractEntity {

    @EqualsAndHashCode.Include
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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "roles")
    private Set<AccountRole> accountRoles = new HashSet<>();

    @Builder.Default
    @Delegate
    @Embedded
    private AuthLogs authLogs = new AuthLogs();

    public Long getSumOfVersions() {
        return getVersion() + person.getVersion() + person.getAddress().getVersion();
    }
}