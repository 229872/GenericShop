package pl.lodz.p.edu.shop.config.database.property;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Validated
public class DataSourceProperties {

    @NotBlank
    private String persistenceUnitName;

    @NotBlank
    private String url;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private SqlProperties sql = new SqlProperties();

    private JpaProperties jpa = new JpaProperties();

    @Data
    public static class SqlProperties {

        @Getter(AccessLevel.NONE)
        private Boolean enable = false;

        public Boolean isEnable() {
            return enable;
        }

        private List<String> dataLocations = new ArrayList<String>(List.of("script/data.sql"));
    }

    @Data
    @Validated
    public static class JpaProperties {

        private DatabaseAction databaseAction = DatabaseAction.NONE;
        private Map<String, String> properties = new HashMap<>();
    }
}
