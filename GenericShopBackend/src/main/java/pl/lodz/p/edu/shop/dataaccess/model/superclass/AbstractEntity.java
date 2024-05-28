package pl.lodz.p.edu.shop.dataaccess.model.superclass;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, name = "is_archival")
    private Boolean isArchival;

    @Column(nullable = false, updatable = false, name = "created_by")
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    protected AbstractEntity(Long id, Long version, Boolean isArchival, String createdBy, String modifiedBy, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.version = version;
        this.isArchival = isArchival;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    protected AbstractEntity() {
    }

    protected AbstractEntity(AbstractEntityBuilder<?, ?> b) {
        this.id = b.id;
        this.version = b.version;
        this.isArchival = b.isArchival;
        this.createdBy = b.createdBy;
        this.modifiedBy = b.modifiedBy;
        this.createdAt = b.createdAt;
        this.modifiedAt = b.modifiedAt;
    }

    @PrePersist
    void prePersist() {
        isArchival = false;
        createdAt = LocalDateTime.now();
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

    public Long getId() {
        return this.id;
    }

    public Long getVersion() {
        return this.version;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return this.modifiedAt;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AbstractEntity)) return false;
        final AbstractEntity other = (AbstractEntity) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AbstractEntity;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    public String toString() {
        return "AbstractEntity(id=" + this.getId() + ", version=" + this.getVersion() + ", isArchival=" + this.isArchival() + ", createdBy=" + this.getCreatedBy() + ", modifiedBy=" + this.getModifiedBy() + ", createdAt=" + this.getCreatedAt() + ", modifiedAt=" + this.getModifiedAt() + ")";
    }

    public static abstract class AbstractEntityBuilder<C extends AbstractEntity, B extends AbstractEntityBuilder<C, B>> {
        private Long id;
        private Long version;
        private Boolean isArchival;
        private String createdBy;
        private String modifiedBy;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public B id(Long id) {
            this.id = id;
            return self();
        }

        public B version(Long version) {
            this.version = version;
            return self();
        }

        public B isArchival(Boolean isArchival) {
            this.isArchival = isArchival;
            return self();
        }

        public B createdBy(String createdBy) {
            this.createdBy = createdBy;
            return self();
        }

        public B modifiedBy(String modifiedBy) {
            this.modifiedBy = modifiedBy;
            return self();
        }

        public B createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return self();
        }

        public B modifiedAt(LocalDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return self();
        }

        protected abstract B self();

        public abstract C build();

        public String toString() {
            return "AbstractEntity.AbstractEntityBuilder(id=" + this.id + ", version=" + this.version + ", isArchival=" + this.isArchival + ", createdBy=" + this.createdBy + ", modifiedBy=" + this.modifiedBy + ", createdAt=" + this.createdAt + ", modifiedAt=" + this.modifiedAt + ")";
        }
    }
}
