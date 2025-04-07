package pl.lodz.p.edu.genericshopdesktopfrontend.service.http;

import java.util.Map;

import static java.util.Map.entry;

public class HttpHelper {

    private HttpHelper() {}


    static final String APPLICATION_JSON = "Application/json";
    static final String CONTENT_TYPE = "Content-Type";
    static final String AUTHORIZATION_HEADER = "Authorization";
    static final String BEARER_TOKEN_PARAM = "Bearer %s";


    public enum Method {
        GET, POST, PUT, PATCH, DELETE
    }


    static final Map.Entry<String, String> ENTRY_CONTENT_JSON = entry(CONTENT_TYPE, APPLICATION_JSON);


    static Map.Entry<String, String> entryBearerToken(String authToken) {
        return entry(AUTHORIZATION_HEADER, BEARER_TOKEN_PARAM.formatted(authToken));
    }
}
