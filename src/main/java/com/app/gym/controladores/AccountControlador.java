package com.app.gym.controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.gym.application.AccountAppService;
import com.app.gym.dtos.account.AccountRequestAdminDTO;
import com.app.gym.dtos.account.AccountUpdateAdminDTO;
import com.app.gym.dtos.account.UpdateEmailDto;
import com.app.gym.dtos.account.UpdateLockDTO;
import com.app.gym.dtos.account.UpdatePasswordAdminDTO;
import com.app.gym.dtos.account.UpdatePasswordDto;
import com.app.gym.dtos.account.UpdateStatusDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountControlador {
    private final AccountAppService accountAppService;

    public AccountControlador(AccountAppService accountAppService) {
        this.accountAppService = accountAppService;
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountRequestAdminDTO dto) {
        return ResponseEntity.ok(accountAppService.createAccount(dto));
    }

    @PatchMapping("/admin/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAccount(@RequestBody @Valid AccountUpdateAdminDTO dto) {
        return ResponseEntity.ok(accountAppService.updateAccount(dto));
    }

    @PatchMapping("/me/change-password")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> changePassword(@RequestBody @Valid UpdatePasswordDto dto) {
        return ResponseEntity.ok(accountAppService.updatePassword(dto));
    }

    @PatchMapping("/me/update-email")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> updateEmail(@RequestBody @Valid UpdateEmailDto dto) {
        return ResponseEntity.ok(accountAppService.updateEmail(dto));
    }

    @PatchMapping("/admin/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changePasswordAdmin(@RequestBody @Valid UpdatePasswordAdminDTO dto) {
        return ResponseEntity.ok(accountAppService.updatePasswordAdmin(dto));
    }

    @PatchMapping("/admin/update-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(@RequestBody @Valid UpdateStatusDTO updateStatusDTO) {
        return ResponseEntity.ok(accountAppService.updateAccountStatus(updateStatusDTO));
    }

    @PatchMapping("/admin/lock-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLockStatus(@RequestBody @Valid UpdateLockDTO updateLockDTO) {
        return ResponseEntity.ok(accountAppService.updateAccountLock(updateLockDTO));
    }

    /*@DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        accountService.eliminarUsuario(id);
        return ResponseEntity.ok("Usuario eliminado");
    }*/

    @GetMapping("/admin/list")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> listUsers() {
        return ResponseEntity.ok(accountAppService.getAccounts());
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(accountAppService.getAccountById(id));
    }
}
