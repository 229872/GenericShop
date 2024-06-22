package pl.lodz.p.edu.shop.presentation.mapper.api;

import java.util.Map;

public interface SchemaMapper {

    Map<String, Object> mapDbSchemaToApplicationSchema(Map<String, Object> dbSchema);

}
