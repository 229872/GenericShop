package pl.lodz.p.edu.presentation.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.logic.service.api.AccountService;
import pl.lodz.p.edu.presentation.dto.user.account.AccountCreateDto;
import pl.lodz.p.edu.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.presentation.mapper.AccountMapper;

import java.net.URI;
import java.util.List;

import static pl.lodz.p.edu.config.RoleName.GUEST;

@RequiredArgsConstructor

@RestController
@RequestMapping("/api/account")
@RolesAllowed({GUEST})
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;


    @GetMapping
    ResponseEntity<List<AccountOutputDto>> getAll() {
        List<AccountOutputDto> result = accountService.findAll().stream()
            .map(accountMapper::mapToAccountOutputDto)
            .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/id/{id}")
    ResponseEntity<AccountOutputDto> getById(@PathVariable Long id) {
        Account account = accountService.findById(id);
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(account);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    ResponseEntity<AccountOutputDto> createAccount(@RequestBody @Valid AccountCreateDto createDto) {
        Account account = accountMapper.mapToAccount(createDto);
        Account createdAccount = accountService.create(account);
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(createdAccount);

        return ResponseEntity.created(URI.create("/id/%d".formatted(result.id()))).body(result);
    }

    @PutMapping("/id/{id}/block")
    ResponseEntity<AccountOutputDto> blockAccount(@PathVariable Long id) {
        Account account = accountService.block(id);
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(account);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{id}/unblock")
    ResponseEntity<AccountOutputDto> unblockAccount(@PathVariable Long id) {
        Account account = accountService.unblock(id);
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(account);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{id}/archive")
    ResponseEntity<AccountOutputDto> archiveAccount(@PathVariable Long id) {
        Account account = accountService.archive(id);
        AccountOutputDto result = accountMapper.mapToAccountOutputDto(account);

        return ResponseEntity.ok(result);
    }
}
