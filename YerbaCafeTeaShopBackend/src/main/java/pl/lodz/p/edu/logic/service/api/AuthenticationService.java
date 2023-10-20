package pl.lodz.p.edu.logic.service.api;

import java.util.List;

public interface AuthenticationService {

    List<String> authenticate(String login, String password);
}
