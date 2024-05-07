package pl.lodz.p.edu.shop.presentation.controller;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.edu.shop.presentation.adapter.api.AccountManagementServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountCreateDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountOutputDto;

import java.net.URI;
import java.util.List;

import static pl.lodz.p.edu.shop.config.security.role.RoleName.ADMIN;
import static pl.lodz.p.edu.shop.presentation.controller.ApiRoot.API_ROOT;

@RequiredArgsConstructor

@RestController
@RequestMapping(API_ROOT + "/account")
@DenyAll
public class AccountManagementController {

    private final AccountManagementServiceOperations accountService;

    @GetMapping
    @RolesAllowed(ADMIN)
    ResponseEntity<List<AccountOutputDto>> getAll() {
        List<AccountOutputDto> result = accountService.findAll();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/id/{id}")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> getById(@PathVariable Long id) {
        AccountOutputDto result = accountService.findById(id);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> createAccount(@RequestBody @Valid AccountCreateDto createDto) {
        AccountOutputDto result = accountService.create(createDto);

        return ResponseEntity.created(URI.create("/id/%d".formatted(result.id()))).body(result);
    }

    @PutMapping("/id/{id}/block")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> blockAccount(@PathVariable Long id) {
        AccountOutputDto result = accountService.block(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{id}/unblock")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> unblockAccount(@PathVariable Long id) {
        AccountOutputDto result = accountService.unblock(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{id}/archive")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> archiveAccount(@PathVariable Long id) {
        AccountOutputDto result = accountService.archive(id);

        return ResponseEntity.ok(result);
    }
}
