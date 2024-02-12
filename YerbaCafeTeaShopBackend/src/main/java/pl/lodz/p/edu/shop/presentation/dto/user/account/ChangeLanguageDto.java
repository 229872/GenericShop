package pl.lodz.p.edu.shop.presentation.dto.user.account;

import lombok.Builder;
import pl.lodz.p.edu.shop.presentation.validation.annotation.Locale;

@Builder
public record ChangeLanguageDto(
    @Locale
    String locale
) {
}
