package pl.lodz.p.edu.presentation.dto.user.account;

import lombok.Builder;
import pl.lodz.p.edu.presentation.validation.annotation.Locale;

@Builder
public record ChangeLanguageDto(
    @Locale
    String locale
) {
}
