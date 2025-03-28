package pl.lodz.p.edu.genericshopdesktopfrontend.service.http;

import pl.lodz.p.edu.genericshopdesktopfrontend.config.JacksonConfig;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.AccountOutputDto;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Tokens;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.UpdateContactDto;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthService;

import java.net.http.HttpClient;

public interface HttpService {

    static HttpService getInstance(AuthService authService) {
        return new HttpServiceImpl(
            HttpClient.newHttpClient(),
            "http://localhost:8080/api/v1",
            JacksonConfig.getMapper(),
            authService
        );
    }


    Tokens sendAuthenticationRequest(String login, String password) throws ApplicationException;

    AccountOutputDto sendGetOwnAccountInformationRequest() throws ApplicationException;

    void sendChangeAccountLanguageRequest(String locale) throws ApplicationException;

    AccountOutputDto sendChangeOwnPasswordRequest(String currentPassword, String newPassword) throws ApplicationException;

    AccountOutputDto sendChangeOwnEmailRequest(String newEmail) throws ApplicationException;

    AccountOutputDto sendUpdateContactInformationRequest(UpdateContactDto dto) throws ApplicationException;


}
