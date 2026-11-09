package com.app.gym.servicios;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.account.AccountCrearDTO;
import com.app.gym.dtos.account.AccountCreateWithProviderDTO;
import com.app.gym.dtos.account.AccountRequestAdminDTO;
import com.app.gym.dtos.account.AccountUpdateAdminDTO;
import com.app.gym.excepciones.AppSecurityException;
import com.app.gym.excepciones.ContrasenaIgualExcepcion;
import com.app.gym.excepciones.EmailYaExisteExcepcion;
import com.app.gym.excepciones.RecursoNoEncontradoExcepcion;
import com.app.gym.modelos.Account;
import com.app.gym.modelos.Role;
import com.app.gym.modelos.account.AccountSecurityView;
import com.app.gym.repositorios.AccountRepositorio;
import com.app.gym.repositorios.RoleRepositorio;
import com.app.gym.servicios.cache.AccountCacheService;
import com.app.gym.utils.validadores.PasswordValidator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountService {

    private final AccountRepositorio accountRepositorio;
    private final RoleRepositorio roleRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final RateLimitService rateLimitService;
    private final AccountCacheService accountCacheService;

    public AccountService(AccountRepositorio accountRepositorio, 
                        RoleRepositorio roleRepositorio, 
                        PasswordEncoder passwordEncoder, 
                        RateLimitService rateLimitService,
                        AccountCacheService accountCacheService) {
        this.accountRepositorio = accountRepositorio;
        this.roleRepositorio = roleRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.rateLimitService = rateLimitService;
        this.accountCacheService = accountCacheService;
    }

    // ----------------------------------------------------------------
    // Crear account (solo para administradores)
    // ----------------------------------------------------------------
    @Transactional
    public Account createAccount(AccountRequestAdminDTO dto) {
        existByEmail(dto.email());
        PasswordValidator.validate(dto.password());

        /*Account account = new Account();
        account.setEmail(dto.email());
        account.setPasswordHash(passwordEncoder.encode(dto.password()));
        account.setEnabled(true); // The account starts active because it is created by an admin.
        account.setEmailVerified(true); // Also starts verified

        Role role = roleRepositorio.findByRoleName(dto.role())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Role '" + dto.role() +"' not found"));

        account.getRoles().add(role);*/

        //Account savedAccount = accountRepositorio.save(account);
        return buildAndSaveAccount(
            dto.email(),
            passwordEncoder.encode(dto.password()),
            true,
            true,
            dto.role()
    );
    }

    // ----------------------------------------------------------------
    // Create account (public)
    // ----------------------------------------------------------------
    @Transactional
    public Account createAccount(AccountCrearDTO dto) {
        existByEmail(dto.email());
        PasswordValidator.validate(dto.password());

        /*Account account = new Account();
        account.setEmail(dto.email());
        account.setPasswordHash(passwordEncoder.encode(dto.password()));
        account.setEnabled(false); // La cuenta inicia deshabilitada hasta verificar el email

        Role role = roleRepositorio.findByRoleName("USER")
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Rol no encontrado"));

        account.getRoles().add(role);

        // 1. Guardar la cuenta PRIMERO (para obtener un ID)
        Account savedAccount = accountRepositorio.save(account);
        return savedAccount;*/
        return buildAndSaveAccount(
            dto.email(),
            passwordEncoder.encode(dto.password()),
            false,
            false,
            "USER"
        );
    }

    @Transactional
    public Account createAccountWithProvider(AccountCreateWithProviderDTO dto) {
        existByEmail(dto.email());

        return buildAndSaveAccount(
            dto.email(),
            null, // No password since it's an external provider
            true, // Enabled immediately since it's created through a trusted provider
            true, // Email is considered verified since it comes from the provider
            "USER"
        );
    }

    @Transactional
    private Account buildAndSaveAccount(String email, String passwordHash, boolean enabled, boolean emailVerified, String roleName){
        Role role = roleRepositorio.findByRoleName(roleName)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Role '" + roleName + "' not found"));

        Account account = new Account();
        account.setEmail(email);
        account.setPasswordHash(passwordHash);
        account.setEnabled(enabled);
        account.setEmailVerified(emailVerified);

        account.getRoles().add(role);

        return accountRepositorio.save(account);
    }

    // ----------------------------------------------------------------
    // Update account by admin
    // ----------------------------------------------------------------

    @Transactional
    public Account updateAccoundByAdmin(UUID targetId, AccountUpdateAdminDTO dto) {
        Account account = findByPublicId(targetId);

        if (dto.accountEmail() != null){
            existByEmail(dto.accountEmail());
            account.setEmail(dto.accountEmail());
        }

        if (dto.role() != null) {
            Role role = roleRepositorio.findByRoleName(dto.role())
                    .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Rol no encontrado: " + dto.role()));
            account.getRoles().clear();
            account.getRoles().add(role);
        }

        //TODO: CREAR UN INVALIDATION WORKFLOW SERVICE O ACCOUNT CACHE WORKFLOW PARA HACER INVALIDACIONES COMO ESTA (INVALIDATE NO INMEDIATELY) DE FORMA ASYNC.
        //TODO: Y MANDAR A LLAMARLO AL PUBLICAR EL EVENTO DE ESTE UPDATE.
        return accountRepositorio.save(account);
    }

    @Transactional
    public Account updateEmail(Long id, String newEmail) {
        Account account = getAccountById(id);

        if (accountRepositorio.findByEmail(newEmail).isPresent()) {
            throw new EmailYaExisteExcepcion("This email is already in use");
        }

        account.setEmail(newEmail);
        account.setEmailVerified(false); // El usuario debe verificar el nuevo email
        account.setEnabled(false); // Se va a deshabilitar hasta verificar el nuevo email

        accountCacheService.invalidateImmediately(account.getPublicId(), account.getIdAccount());
        return accountRepositorio.save(account);
    }

    @Transactional
    public Account incrementarTokenVersion(Long id) {
        Account account = getAccountById(id);
        Integer newVersion = account.getTokenVersion() + 1;

        account.setTokenVersion(newVersion);

        accountCacheService.incrementRedisTokenVersion(account.getPublicId(), newVersion);
        accountCacheService.invalidateImmediately(account.getPublicId(), account.getIdAccount());
        
        return accountRepositorio.save(account);
    }

    @Transactional
    public Account updatePassword(Long id, String newPassword) {
        Account account = getAccountById(id);

        rateLimitService.checkAndIncrementPasswordChangeAttempts(account.getIdAccount());
        PasswordValidator.validate(newPassword);

        if (passwordEncoder.matches(newPassword, account.getPasswordHash())) {
            throw new ContrasenaIgualExcepcion("The new password must be different from the current one");
        }

        account.setPasswordHash(passwordEncoder.encode(newPassword));
        incrementarTokenVersion(account.getIdAccount());

        return accountRepositorio.save(account);
    }

    // ----------------------------------------------------------------
    // Activar / Desactivar cuenta
    // ----------------------------------------------------------------
    @Transactional
    public Account actualizarEstadoCuenta(UUID publicId, boolean enabled) {
        Account account = findByPublicId(publicId);

        account.setEnabled(enabled);
        Account updatedAccount = accountRepositorio.save(account);

        accountCacheService.invalidateImmediately(account.getPublicId(), account.getIdAccount());
        return updatedAccount;
    }

    @Transactional
    public Account actualizarBloqueoCuenta(UUID publicId, boolean locked) {
        Account account = findByPublicId(publicId);

        account.setLocked(locked);
        Account updatedAccount = accountRepositorio.save(account);

        accountCacheService.invalidateImmediately(account.getPublicId(), account.getIdAccount());
        return updatedAccount;
    }

    @Transactional
    public void actualizarUltimoLogin(Long id) {
        Account account = getAccountById(id);

        account.setLastLogin(OffsetDateTime.now());
        Account updatedAccount = accountRepositorio.save(account);

        accountCacheService.updateLastLoginOnly(updatedAccount.getIdAccount(), updatedAccount.getLastLogin(), updatedAccount.getTokenVersion());
    }

    @Transactional
    public Account verificarEmail(Long id) {
        Account account = getAccountById(id);

        account.setEmailVerified(true);
        account.setEnabled(true);

        Account verifiedAccount = accountRepositorio.save(account);

        accountCacheService.invalidateImmediately(account.getPublicId(), account.getIdAccount());
        return verifiedAccount;
    }

    /*/ ----------------------------------------------------------------
    // Eliminar usuario
    // ----------------------------------------------------------------
    @Transactional
    public void eliminarUsuario(Integer id) {
        if (!accountRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoExcepcion("Usuario no encontrado con ID: " + id);
        }
        accountRepositorio.deleteById(id);
    }*/

    // ----------------------------------------------------------------
    // Listar todos
    // ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Account> listarUsuarios() {
        return accountRepositorio.findAll();
    }

    // ----------------------------------------------------------------
    // Buscar por ID
    // ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return getOrThrow(accountRepositorio.findByIdAccount(id));
    }
    
    @Transactional(readOnly = true)
    public Account findByEmail(String email){
        return getOrThrow(accountRepositorio.findByEmail(email));
    }

    @Transactional(readOnly = true)
    public Optional<Account> findOptionalByEmail(String email){
        return accountRepositorio.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Account findByPublicId(UUID publicId){
        return getOrThrow(accountRepositorio.findByPublicId(publicId));
    }

    // Helper method to be used in authentication
    @Transactional(readOnly = true)
    public void existByEmail(String email) {
        if (accountRepositorio.existsByEmail(email)) {
            throw new EmailYaExisteExcepcion("Email already exists");
        }
    }
    
    private Account getOrThrow(Optional<Account> opt) {
        return opt.orElseThrow(() -> new RecursoNoEncontradoExcepcion("Account not found"));
    }

    /* Método usado por ResetPasswordService para verificar que una cuenta exista y sea valida antes de permitirle resetear la contraseña.
    * Retorna el id para poder seguir el flujo de ese servicio (ponerle FK al token, verificar rate limit, auditar, etc...) */
    @Transactional(readOnly = true)
    public Long verificateAccountStatus(String email){
        AccountSecurityView account = accountRepositorio.findSecurityStatusByEmail(email)
            .orElseThrow(() -> new AppSecurityException("Account not found"));

        if (!account.isEmailVerified() || !account.isEnabled() || account.isLocked()) {
            throw new AppSecurityException("Sorry, an error has occurred, please contact an administrator."); 
        }

        return account.getIdAccount();
    }

    /* Método usado por AccountVerificationTokenService para revisar que una cuenta exista y no este verificada antes de emitir un nuevo token de verificación.
    * Retorna el id para poder seguir el flujo de ese servicio (ponerle FK al token, verificar rate limit, auditar, etc...)*/
    @Transactional(readOnly = true)
    public Long verificateAccountVerified(String email){
        AccountSecurityView account = accountRepositorio.findSecurityStatusByEmail(email)
            .orElseThrow(() -> new AppSecurityException("Account not found"));

        if (account.isEmailVerified()) {
            throw new AppSecurityException("Your account is alredy verified!"); 
        }

        return account.getIdAccount();
    }
}
