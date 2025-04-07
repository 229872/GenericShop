package pl.lodz.p.edu.genericshopdesktopfrontend.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JacksonConfig {

    private JacksonConfig() {
        throw new UnsupportedOperationException("Can't instantiate static class.");
    }


    public static JsonMapper getMapper() {
        JsonMapper mapper = new JsonMapper();
        mapper.registerModule(new JavaTimeModule()); // Register Java 8 Time Module
        return mapper;
    }
}
