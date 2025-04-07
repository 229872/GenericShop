package pl.lodz.p.edu.genericshopdesktopfrontend.service.image;

import javafx.scene.image.Image;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;

public interface ImageService {

    static ImageService getInstance() {
        return new ImageServiceImp();
    }

    Image loadImage(String fileBaseName) throws ApplicationException;
}
