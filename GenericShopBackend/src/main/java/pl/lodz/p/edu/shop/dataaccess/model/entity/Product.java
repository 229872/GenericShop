package pl.lodz.p.edu.shop.dataaccess.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.edu.shop.dataaccess.model.superclass.ArchivableEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "products")
public class Product extends ArchivableEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "average_rating", nullable = false, columnDefinition = "numeric(3,2) default 0.0")
    private Double averageRating;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "product")
    private Set<Rate> rates = new HashSet<>();

    @ManyToOne
    private Category category;

    @Builder.Default
    @Transient
    private Map<String, Object> tableProperties = new HashMap<>();

    @Override
    protected void prePersist() {
        super.prePersist();
        averageRating = 0.0;
    }

    public boolean isAvailable() {
        return quantity > 0 && !isArchival();
    }
}
