package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author trofiv
 * @date 19.05.2017
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;

    public AuthenticationServiceImpl(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public User getCurrentUser() {
        final String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUser(username);
    }
}
