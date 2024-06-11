package pl.lodz.p.edu.shop.presentation.controller;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.edu.shop.presentation.adapter.api.AccountManagementServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.AccountRoleDto;
import pl.lodz.p.edu.shop.presentation.dto.user.account.CreateAccountDto;

import java.net.URI;

import static pl.lodz.p.edu.shop.config.security.role.RoleName.ADMIN;
import static pl.lodz.p.edu.shop.presentation.controller.ApiRoot.API_ROOT;

@RequiredArgsConstructor

@RestController
@RequestMapping(API_ROOT + "/accounts")
@DenyAll
public class AccountManagementController {

    private final AccountManagementServiceOperations accountService;

    @GetMapping
    @RolesAllowed(ADMIN)
    ResponseEntity<Page<AccountOutputDto>> getAll(Pageable pageable) {
        Page<AccountOutputDto> result = accountService.findAll(pageable);

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
    ResponseEntity<AccountOutputDto> createAccount(@RequestBody @Valid CreateAccountDto createDto) {
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

    @PutMapping("/id/{id}/role/add")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> addRole(@PathVariable Long id, @RequestBody @Valid AccountRoleDto roleDto) {
        AccountOutputDto result = accountService.addRole(id, roleDto.role());

        return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{id}/role/remove")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> removeRole(@PathVariable Long id, @RequestBody @Valid AccountRoleDto roleDto) {
        AccountOutputDto result = accountService.removeRole(id, roleDto.role());

        return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{id}/role/change")
    @RolesAllowed(ADMIN)
    ResponseEntity<AccountOutputDto> changeExistingRole(@PathVariable Long id, @RequestBody @Valid AccountRoleDto roleDto) {
        AccountOutputDto result = accountService.changeRole(id, roleDto.role());

        return ResponseEntity.ok(result);
    }
}
