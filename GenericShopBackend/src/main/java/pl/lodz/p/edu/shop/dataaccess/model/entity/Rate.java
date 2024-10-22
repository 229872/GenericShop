package pl.lodz.p.edu.shop.dataaccess.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.edu.shop.dataaccess.model.superclass.AbstractEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "rates")
public class Rate extends AbstractEntity implements Comparable<Rate> {

    @Column(nullable = false)
    private Integer value;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Account account;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Product product;

    @Override
    public int compareTo(Rate o) {
        if (o == null) return 0;
        return value.compareTo(o.value);
    }
}
