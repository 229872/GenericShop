package pl.lodz.p.edu.genericshopdesktopfrontend.service.http;

import com.fasterxml.jackson.databind.json.JsonMapper;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Tokens;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;

class HttpServiceImpl implements HttpService {

    private final HttpClient client;
    private final String API_ROOT;
    private final JsonMapper jsonMapper;

    HttpServiceImpl(HttpClient client, String apiRoot, JsonMapper jsonMapper) {
        this.client = requireNonNull(client);
        this.API_ROOT = requireNonNull(apiRoot);
        this.jsonMapper = requireNonNull(jsonMapper);
    }


    @Override
    public Tokens sendAuthenticationRequest(String login, String password) throws ApplicationException {
        try {
            Map<String, String> obj = Map.ofEntries(
                entry("login", login),
                entry("password", password)
            );
            String json = jsonMapper.writeValueAsString(obj);

            HttpRequest request = HttpRequest.newBuilder(URI.create("%s/auth".formatted(API_ROOT)))
                .POST(BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApplicationException("Authentication failed");
            }

            return jsonMapper.readValue(response.body(), Tokens.class);

        } catch (IOException | InterruptedException e) {
            throw new ApplicationException("Authentication failed", e);
        }

    }
}
