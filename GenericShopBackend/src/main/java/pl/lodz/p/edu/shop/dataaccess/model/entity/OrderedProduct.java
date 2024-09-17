package pl.lodz.p.edu.shop.dataaccess.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.edu.shop.dataaccess.model.superclass.AbstractEntity;

import java.math.BigDecimal;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "ordered_products")
public class OrderedProduct extends AbstractEntity {

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Product product;

    @ToString.Exclude
    @EqualsAndHashCode.Include
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Account account;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private Order order;

    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    @OneToOne
    @JoinColumn(nullable = true, unique = true)
    private Rate rate;

    public Optional<Rate> getRate() {
        return Optional.ofNullable(rate);
    }
}

