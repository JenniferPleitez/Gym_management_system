package com.app.gym.servicios;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.gym.excepciones.AccountDisabledException;
import com.app.gym.excepciones.AccountLockedException;
import com.app.gym.excepciones.EmailNotVerifiedException;
import com.app.gym.modelos.Account;
import com.app.gym.modelos.CustomUserDetails;
import com.app.gym.repositorios.AccountRepositorio;

/*
 * Servicio para cargar los detalles del usuario para la autenticación.
 * Implementa UserDetailsService de Spring Security.
 */
@Service
public class AccountDetailsService implements UserDetailsService{
    private final AccountRepositorio accountRepositorio;

    public AccountDetailsService(AccountRepositorio accountRepositorio) {
        this.accountRepositorio = accountRepositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepositorio.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        if (!account.isEmailVerified()) {
            throw new EmailNotVerifiedException("Debes verificar tu correo para iniciar sesión.");
        }

        if (!account.isEnabled()) {
            throw new AccountDisabledException("Tu cuenta está deshabilitada. Por favor, contacta al soporte técnico para más información.");
        }

        if(account.isLocked()) {
            throw new AccountLockedException("Your account has been blocked for security reasons. If you believe this is a mistake, please contact us.");
        }

        // Retorna un objeto que implementa UserDetails
        return new CustomUserDetails(account);
    }

    
}
