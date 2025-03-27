package pl.lodz.p.edu.genericshopdesktopfrontend.service.http;

import com.fasterxml.jackson.databind.json.JsonMapper;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.AccountOutputDto;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Tokens;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpHelper.*;

class HttpServiceImpl implements HttpService {

    private final static String ACCOUNT_SELF = "/account/self";

    private final HttpClient client;
    private final String API_ROOT;
    private final JsonMapper jsonMapper;
    private final AuthService authService;


    HttpServiceImpl(HttpClient client, String apiRoot, JsonMapper jsonMapper, AuthService authService) {
        this.client = requireNonNull(client);
        this.API_ROOT = requireNonNull(apiRoot).concat("%s");
        this.jsonMapper = requireNonNull(jsonMapper);
        this.authService = requireNonNull(authService);
    }


    @Override
    public Tokens sendAuthenticationRequest(String login, String password) throws ApplicationException {
        try {
            Map<String, String> obj = Map.ofEntries(
                entry("login", login),
                entry("password", password)
            );
            String json = jsonMapper.writeValueAsString(obj);

            var request = HttpRequest.newBuilder(URI.create(format(API_ROOT, "/auth")))
                .header(CONTENT_TYPE, APPLICATION_JSON)
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


    @Override
    public AccountOutputDto sendGetOwnAccountInformationRequest() throws ApplicationException {
        try {
            String authToken = authService.getAuthToken()
                .orElseThrow(() -> new ApplicationException("Session expired"));

            var request = HttpRequest.newBuilder(URI.create(format(API_ROOT, ACCOUNT_SELF)))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, format(BEARER_TOKEN_PARAM, authToken))
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApplicationException("Couldn't fetch account data");
            }

            String json = response.body();
            return jsonMapper.readValue(json, AccountOutputDto.class);

        } catch (IOException | InterruptedException e) {
            throw new ApplicationException("Couldn't change account language", e);
        }
    }


    @Override
    public void sendChangeAccountLanguageRequest(String locale) throws ApplicationException {
        try {
            String authToken = authService.getAuthToken()
                .orElseThrow(() -> new ApplicationException("Session expired"));

            Map<String, String> obj = Map.ofEntries(entry("locale", locale));
            String json = jsonMapper.writeValueAsString(obj);

            var request = HttpRequest.newBuilder(URI.create(format(API_ROOT, ACCOUNT_SELF + "/change-locale")))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, format(BEARER_TOKEN_PARAM, authToken))
                .PUT(BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApplicationException("Couldn't change account language");
            }

        } catch (IOException | InterruptedException e) {
            throw new ApplicationException("Couldn't change account language", e);
        }
    }
}
