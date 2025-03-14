package pl.lodz.p.edu.genericshopdesktopfrontend.service.image;

import javafx.scene.image.Image;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

class ImageServiceImp implements ImageService {

    @Override
    public Image loadImage(String fileBaseNameWithExtension) throws ApplicationException {

        String fullPath = "/images/%s".formatted(fileBaseNameWithExtension);

        try (var imageStream = getClass().getResourceAsStream(fullPath)) {

            return new Image(requireNonNull(imageStream));

        } catch (IOException e) {
            throw new ApplicationException("Couldn't load image", e);
        }
    }
}
