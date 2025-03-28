package pl.lodz.p.edu.genericshopdesktopfrontend.service.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.AccountOutputDto;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Tokens;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.UpdateContactDto;
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
import static pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpHelper.Method.*;

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
        String errorMessage = "Authentication failed";
        try {
            Map<String, Object> obj = Map.ofEntries(
                entry("login", login),
                entry("password", password)
            );

            var headers = headers(ENTRY_CONTENT_JSON);
            var requestParams = new RequestParams("/auth", POST, headers);
            var request = prepareRequest(requestParams, obj);
            var response = sendRequest(request);

            handleResponse(response, errorMessage);

            return deserializeResponse(response, Tokens.class);

        } catch (IOException | InterruptedException e) {
            throw new ApplicationException(errorMessage, e);
        }
    }


    @Override
    public AccountOutputDto sendGetOwnAccountInformationRequest() throws ApplicationException {
        String errorMessage = "Couldn't fetch account data";
        try {
            String authToken = getAuthToken();
            var headers = headers(ENTRY_CONTENT_JSON, entryBearerToken(authToken));
            var requestParams = new RequestParams(ACCOUNT_SELF, GET, headers);
            var request = prepareRequest(requestParams);
            var response = sendRequest(request);

            handleResponse(response, errorMessage);

            return deserializeResponse(response, AccountOutputDto.class);

        } catch (IOException | InterruptedException e) {
            throw new ApplicationException("Couldn't change account language", e);
        }
    }


    @Override
    public void sendChangeAccountLanguageRequest(String locale) throws ApplicationException {
        String errorMessage = "Couldn't change account language";
        try {
            Map<String, Object> obj = Map.ofEntries(
                entry("locale", locale)
            );

            String authToken = getAuthToken();
            var headers = headers(ENTRY_CONTENT_JSON, entryBearerToken(authToken));
            var requestParams = new RequestParams(ACCOUNT_SELF + "/change-locale", PUT, headers);
            var request = prepareRequest(requestParams, obj);
            var response = sendRequest(request);

            handleResponse(response, errorMessage);

        } catch (IOException | InterruptedException e) {
            throw new ApplicationException(errorMessage, e);
        }
    }


    @Override
    public AccountOutputDto sendChangeOwnPasswordRequest(String currentPassword, String newPassword) throws ApplicationException {
        String errorMessage = "Couldn't change password";
        try {
            Map<String, Object> obj = Map.ofEntries(
                entry("currentPassword", currentPassword),
                entry("newPassword", newPassword)
            );

            String authToken = getAuthToken();
            var headers = headers(ENTRY_CONTENT_JSON, entryBearerToken(authToken));
            var requestParams = new RequestParams(ACCOUNT_SELF + "/change-password", PUT, headers);
            var request = prepareRequest(requestParams, obj);
            var response = sendRequest(request);

            handleResponse(response, errorMessage);

            return deserializeResponse(response, AccountOutputDto.class);

        } catch (IOException | InterruptedException e) {
            throw new ApplicationException(errorMessage, e);
        }
    }


    @Override
    public AccountOutputDto sendChangeOwnEmailRequest(String newEmail) throws ApplicationException {
        String errorMessage = "Couldn't change email";
        try {
            Map<String, Object> obj = Map.ofEntries(
                entry("newEmail", newEmail)
            );

            String authToken = getAuthToken();
            var headers = headers(ENTRY_CONTENT_JSON, entryBearerToken(authToken));
            var requestParams = new RequestParams(ACCOUNT_SELF + "/change-email", PUT, headers);
            var request = prepareRequest(requestParams, obj);
            var response = sendRequest(request);

            handleResponse(response, errorMessage);

            return deserializeResponse(response, AccountOutputDto.class);

        } catch (IOException | InterruptedException e) {
            throw new ApplicationException(errorMessage, e);
        }
    }


    @Override
    public AccountOutputDto sendUpdateContactInformationRequest(UpdateContactDto dto) throws ApplicationException {
        String errorMessage = "Couldn't update contact information";
        try {
            String authToken = getAuthToken();
            var headers = headers(ENTRY_CONTENT_JSON, entryBearerToken(authToken));
            var requestParams = new RequestParams(ACCOUNT_SELF + "/edit", PUT, headers);
            var request = prepareRequest(requestParams, dto);
            var response = sendRequest(request);

            handleResponse(response, errorMessage);

            return deserializeResponse(response, AccountOutputDto.class);

        } catch (IOException | InterruptedException e) {
            throw new ApplicationException(errorMessage, e);
        }
    }





    record RequestParams(String endpointPath, Method method, Map<String, String> headers) {}


    private <T> T deserializeResponse(HttpResponse<String> response, Class<T> type) throws JsonProcessingException {
        return jsonMapper.readValue(response.body(), type);
    }


    private HttpRequest prepareRequest(RequestParams params) throws JsonProcessingException {
        return prepareRequest(params, Map.of());
    }


    private HttpRequest prepareRequest(RequestParams params, Object obj) throws JsonProcessingException {
        requireNonNull(params);

        String endpointPath = requireNonNull(params.endpointPath());
        Map<String, String> headers = requireNonNull(params.headers());
        Method method = requireNonNull(params.method());

        String json = jsonMapper.writeValueAsString(obj);

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(format(API_ROOT, endpointPath)));
        headers.forEach(builder::header);

        switch (method) {
            case GET -> builder.GET();
            case POST -> builder.POST(BodyPublishers.ofString(json));
            case PUT, PATCH -> builder.PUT(BodyPublishers.ofString(json));
            case DELETE -> builder.DELETE();
        }

        return builder.build();
    }


    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    private void handleResponse(HttpResponse<String> response, String message) throws ApplicationException {
        if (response.statusCode() != 200) {
            throw new ApplicationException(message);
        }
    }


    private String getAuthToken() throws ApplicationException {
        return authService.getAuthToken()
            .orElseThrow(() -> new ApplicationException("Session expired"));
    }


    @SafeVarargs
    private Map<String, String> headers(Map.Entry<String, String> ... entries) {
        return Map.ofEntries(entries);
    }
}
