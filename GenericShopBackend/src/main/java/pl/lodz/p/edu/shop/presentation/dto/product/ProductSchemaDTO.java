package pl.lodz.p.edu.shop.presentation.dto.product;

import pl.lodz.p.edu.shop.presentation.validation.annotation.Schema;
import pl.lodz.p.edu.shop.presentation.validation.annotation.TableName;

import java.util.List;
import java.util.Map;

public record ProductSchemaDTO(
    @TableName
    String categoryName,
    @Schema
    Map<String, List<String>> properties
) {
}
