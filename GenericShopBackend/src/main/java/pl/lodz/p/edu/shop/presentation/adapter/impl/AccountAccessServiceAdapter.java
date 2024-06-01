package pl.lodz.p.edu.shop.presentation.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Contact;
import pl.lodz.p.edu.shop.logic.service.api.AccountAccessService;
import pl.lodz.p.edu.shop.presentation.adapter.api.AccountAccessServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.user.account.*;
import pl.lodz.p.edu.shop.presentation.mapper.api.AccountMapper;

import java.util.Locale;

@RequiredArgsConstructor

@Component
class AccountAccessServiceAdapter implements AccountAccessServiceOperations {

    private final AccountAccessService accountAccessService;
    private final AccountMapper accountMapper;

    @Override
    public AccountOutputDto findByLogin(String login) {
        Account account = accountAccessService.findByLogin(login);
        return accountMapper.mapToAccountOutputDtoWithVersion(account);
    }

    @Override
    public AccountOutputDto updateOwnLocale(String login, ChangeLanguageDto locale) {
        Locale language = new Locale(locale.locale());
        Account account = accountAccessService.updateOwnLocale(login, language);
        return accountMapper.mapToAccountOutputDtoWithVersion(account);
    }

    @Override
    public AccountOutputDto changePassword(String login, ChangePasswordDto passwords) {
        Account account = accountAccessService.changePassword(login, passwords.currentPassword(), passwords.newPassword());
        return accountMapper.mapToAccountOutputDtoWithVersion(account);
    }

    @Override
    public AccountOutputDto changeEmail(String login, ChangeEmailDto email) {
        Account account = accountAccessService.changeEmail(login, email.newEmail());
        return accountMapper.mapToAccountOutputDtoWithVersion(account);
    }

    @Override
    public AccountOutputDto updateContactInformation(String login, UpdateContactDto updateDto) {
        Contact contact = accountMapper.mapToContact(updateDto);
        Account account = accountAccessService.updateContactInformation(login, contact, updateDto.version());
        return accountMapper.mapToAccountOutputDtoWithVersion(account);
    }

    @Override
    public AccountOutputDto register(RegisterDto registerDto) {
        Account account = accountMapper.mapToAccount(registerDto);
        Account registeredAccount = accountAccessService.register(account);
        return accountMapper.mapToAccountOutputDtoWithVersion(registeredAccount);
    }

    @Override
    public void confirmRegistration(String verificationToken) {
        accountAccessService.confirmRegistration(verificationToken);
    }

    @Override
    public void forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        accountAccessService.forgotPassword(forgotPasswordDto.email());
    }

    @Override
    public void validateResetPasswordToken(String token) {
        accountAccessService.validateResetPasswordToken(token);
    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        accountAccessService.resetPassword(resetPasswordDto.password(), resetPasswordDto.resetPasswordToken());
    }
}
