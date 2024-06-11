package pl.lodz.p.edu.shop.presentation.dto.user.account;

import pl.lodz.p.edu.shop.presentation.validation.annotation.AccountRole;

public record AccountRoleDto(
    @AccountRole
    String role
) {
}
