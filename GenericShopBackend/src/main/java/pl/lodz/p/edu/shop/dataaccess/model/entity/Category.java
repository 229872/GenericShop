package pl.lodz.p.edu.shop.dataaccess.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "categories")
public class Category extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String name;

    public String getCategoryTableName() {
        return "%ss".formatted(name.toLowerCase());
    }
}
