package com.emc.internal.reserv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import static com.emc.internal.reserv.util.RuntimeUtil.hashPassword;

/**
 * @author trofiv
 * @date 03.04.2017
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userRepository;

    @Autowired
    @SuppressWarnings("DuplicateStringLiteralInspection")
    public CustomAuthenticationProvider(@Qualifier("userService") final UserDetailsService userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        final UserDetails user = userRepository.loadUserByUsername(username);

        if (!user.getPassword().equals(hashPassword(password))) {
            throw new BadCredentialsException("Invalid login / password!");
        }

        return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
