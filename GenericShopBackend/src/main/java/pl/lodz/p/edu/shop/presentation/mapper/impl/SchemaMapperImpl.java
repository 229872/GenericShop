package pl.lodz.p.edu.shop.presentation.mapper.impl;

import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.presentation.mapper.api.SchemaMapper;

import java.util.Map;

@Component
public class SchemaMapperImpl implements SchemaMapper {

    @Override
    public Map<String, Object> mapDbSchemaToApplicationSchema(Map<String, Object> dbSchema) {
        dbSchema.put("property", dbSchema.remove("column_name"));
        dbSchema.put("type", dbSchema.remove("data_type"));
        dbSchema.put("nullable", dbSchema.remove("is_nullable"));

        Object maxLength = dbSchema.remove("character_maximum_length");

        if (dbSchema.get("type").equals("character varying")) {
            dbSchema.put("max_length", maxLength);
        }

        return dbSchema;
    }
}
