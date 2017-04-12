package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.repository.UserRepository;
import https.internal_emc_com.reserv_io.ws.SortingOrder;
import https.internal_emc_com.reserv_io.ws.UserField;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static com.emc.internal.reserv.util.RuntimeUtil.enterMethodMessage;
import static com.emc.internal.reserv.util.RuntimeUtil.exitMethodMessage;
import static java.text.MessageFormat.format;

/**
 * @author trofiv
 * @date 04.04.2017
 */
@Log4j2
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
    public void registerUser(
            final String username,
            final String email,
            final String password,
            final String firstName,
            final String lastName,
            final String middleName) {
        log.info("{} " +
                        "username: {}, " +
                        "email: {}, " +
                        "password: {}, " +
                        "firstName: {}, " +
                        "lastName: {}, " +
                        "middleName: {}",
                enterMethodMessage(),
                username, email, password, firstName, lastName, middleName);
        log.info(exitMethodMessage());
    }

    @Override
    public Optional<User> getUser(final int id) {
        log.info("{} id: {}", enterMethodMessage(), id);
        log.info(exitMethodMessage());
        return Optional.empty();
    }

    @Override
    public Collection<User> getUsers(
            final int page,
            final int pageSize,
            final UserField filteringField,
            final String filteringValue,
            final SortingOrder sortingOrder,
            final UserField sortingField) {
        log.info("{} " +
                        "page: {}, " +
                        "pageSize: {}, " +
                        "filteringField: {}, " +
                        "filteringValue: {}, " +
                        "sortingOrder: {}, " +
                        "sortingField: {}",
                enterMethodMessage(),
                page, pageSize, filteringField, filteringValue, sortingOrder, sortingField);
        log.info(exitMethodMessage());
        return Collections.emptyList();
    }
}
