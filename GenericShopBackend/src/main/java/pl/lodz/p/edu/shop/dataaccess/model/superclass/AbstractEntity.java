package pl.lodz.p.edu.shop.dataaccess.model.superclass;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter

@MappedSuperclass
public abstract class AbstractEntity {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, updatable = false, name = "created_by")
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        //fixme After adding security implement mechanism
        createdBy = "test";
    }

    @PreUpdate
    void preUpdate() {
        modifiedAt = LocalDateTime.now();
    }
}
