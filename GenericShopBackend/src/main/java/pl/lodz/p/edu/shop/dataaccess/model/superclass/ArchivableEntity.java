package pl.lodz.p.edu.shop.dataaccess.model.superclass;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@ToString
@Getter

@MappedSuperclass
public abstract class ArchivableEntity extends AbstractEntity {

    @Getter(value = AccessLevel.NONE)
    @Column(nullable = false, name = "is_archival")
    private Boolean isArchival;

    public Boolean isArchival() {
        return isArchival;
    }

    public void setArchival(Boolean archival) {
        isArchival = archival;
    }

    @Override
    protected void prePersist() {
        super.prePersist();
        isArchival = false;
    }


}
