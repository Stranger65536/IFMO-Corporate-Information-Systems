package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.entity.User.UserBuilder;
import com.emc.internal.reserv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static java.text.MessageFormat.format;

/**
 * @author trofiv
 * @date 04.04.2017
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @param username username or email to login
     * @return detailed information about user identified by the specified username / email
     * @throws UsernameNotFoundException if user with specified email / username has been found
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDetails loadUserByUsername(final String username) {
        final Optional<User> userOptional = userRepository.findOneByEmailOrLogin(username);
        final User user = userOptional.orElseThrow(() -> new UsernameNotFoundException(
                format("User with identity '{0} has not been found!'", username)));

        return new org.springframework.security.core.userdetails.User(
                username, user.getPassword(), true, true, true, true,
                Collections.singletonList(user.getRole()));
    }

    @Override
    public int createUser(final String login, final String email, final String password) {
        final User user = new UserBuilder().login(login).email(email).password(password).build();
        return userRepository.save(user).getId();
    }
}
