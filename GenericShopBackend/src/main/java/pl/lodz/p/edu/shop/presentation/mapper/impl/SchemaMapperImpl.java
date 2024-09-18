package pl.lodz.p.edu.shop.presentation.mapper.impl;

import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.presentation.mapper.api.SchemaMapper;
import pl.lodz.p.edu.shop.util.TextUtil;

import java.util.Map;

@Component
public class SchemaMapperImpl implements SchemaMapper {

    @Override
    public Map<String, Object> mapDbSchemaToApplicationSchema(Map<String, Object> dbSchema) {
        Object columnName = dbSchema.remove("column_name");

        if (columnName.equals("product_id")) {
            return Map.of();
        }

        if (columnName instanceof String name) {
            columnName = TextUtil.toCamelCase(name);
        }

        dbSchema.put("property", columnName);
        dbSchema.put("type", mapDbTypeToApplicationType((String) dbSchema.remove("data_type")));
        dbSchema.put("nullable", dbSchema.remove("is_nullable"));

        Object maxLength = dbSchema.remove("character_maximum_length");

        if (dbSchema.get("type").equals("character varying")) {
            dbSchema.put("maxlength", maxLength);
        }

        return dbSchema;
    }

    private String mapDbTypeToApplicationType(String dbType) {
        return switch (dbType) {
            case "integer" -> "NUMBER";
            case "bigint" -> "BIG_NUMBER";
            case "boolean" -> "LOGICAL_VALUE";
            case "character varying" -> "TEXT";
            case "double precision" -> "FRACTIONAL_NUMBER";
            default -> "";
        };
    }


}
