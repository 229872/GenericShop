package pl.lodz.p.edu.genericshopdesktopfrontend.service;

import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.fxml.FXMLService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.image.ImageService;

public record Services(HttpService http,
                       AuthService auth,
                       AnimationService animation,
                       ImageService image,
                       FXMLService fxml) {

}
