package pl.lodz.p.edu.shop.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Contact;
import pl.lodz.p.edu.shop.dataaccess.repository.api.AccountRepository;
import pl.lodz.p.edu.shop.exception.ApplicationExceptionFactory;
import pl.lodz.p.edu.shop.exception.SystemExceptionFactory;
import pl.lodz.p.edu.shop.util.ExceptionUtil;

import java.util.Objects;

@RequiredArgsConstructor
abstract class AccountService {

    private final AccountRepository accountRepository;

    protected Account save(Account account) {
        try {
            accountRepository.save(account);
            accountRepository.flush();
            return account;

        } catch (DataAccessException e) {
            var violationException = ExceptionUtil.findCause(e, ConstraintViolationException.class);

            if (Objects.nonNull(violationException) && Objects.nonNull(violationException.getConstraintName())) {
                return handleConstraintViolationException(violationException);
            }

            throw ApplicationExceptionFactory.createUnknownException();
        }
    }

    protected void updatePersonalInformation(Account account, Contact personalInformation) {
        Contact contact = account.getContact();

        contact.setFirstName(personalInformation.getFirstName());
        contact.setLastName(personalInformation.getLastName());
        contact.setPostalCode(personalInformation.getPostalCode());
        contact.setCountry(personalInformation.getCountry());
        contact.setCity(personalInformation.getCity());
        contact.setStreet(personalInformation.getStreet());
        contact.setHouseNumber(personalInformation.getHouseNumber());
    }

    private Account handleConstraintViolationException(ConstraintViolationException e) {
        switch (Objects.requireNonNull(e.getConstraintName())) {
            case "accounts_login_key" -> throw ApplicationExceptionFactory.createAccountLoginConflictException();
            case "accounts_email_key" -> throw ApplicationExceptionFactory.createAccountEmailConflictException();
            default -> throw SystemExceptionFactory.createDbConstraintViolationException(e);
        }
    }


}
