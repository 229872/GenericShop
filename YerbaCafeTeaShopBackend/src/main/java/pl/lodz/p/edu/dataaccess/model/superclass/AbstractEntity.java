package pl.lodz.p.edu.dataaccess.model.superclass;

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

    @Getter(value = AccessLevel.NONE)
    @Column(nullable = false, name = "is_archival")
    private Boolean isArchival;

    @Column(nullable = false, updatable = false, name = "created_by")
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime cratedAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @PrePersist
    void prePersist() {
        isArchival = false;
        cratedAt = LocalDateTime.now();
        //fixme After adding security implement mechanism
        createdBy = "test";
    }

    @PreUpdate
    void preUpdate() {
        modifiedAt = LocalDateTime.now();
    }

    public Boolean isArchival() {
        return isArchival;
    }

    public void setArchival(Boolean archival) {
        isArchival = archival;
    }
}
