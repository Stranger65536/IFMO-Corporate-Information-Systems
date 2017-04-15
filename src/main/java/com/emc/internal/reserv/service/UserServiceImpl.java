package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.entity.User.UserBuilder;
import com.emc.internal.reserv.repository.UserRepository;
import com.emc.internal.reserv.util.query.QueryBuilder;
import https.internal_emc_com.reserv_io.ws.SearchType;
import https.internal_emc_com.reserv_io.ws.SortingOrder;
import https.internal_emc_com.reserv_io.ws.UserSearchableField;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.emc.internal.reserv.entity.Roles.USER;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.RuntimeUtil.enterMethodMessage;
import static com.emc.internal.reserv.util.RuntimeUtil.exitMethodMessage;
import static https.internal_emc_com.reserv_io.ws.FaultCode.USER_ALREADY_REGISTERED;
import static java.text.MessageFormat.format;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * @author trofiv
 * @date 04.04.2017
 */
@Log4j2
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final QueryBuilder<User, UserSearchableField> queryBuilder;

    @Autowired
    public UserServiceImpl(
            final UserRepository userRepository,
            final QueryBuilder<User, UserSearchableField> queryBuilder) {
        this.userRepository = userRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * @param username username or email to login
     * @return detailed information about user identified by the specified username / email
     * @throws UsernameNotFoundException if user with specified email / username has been found
     */
    @Override
    @Transactional(isolation = READ_COMMITTED)
    public UserDetails loadUserByUsername(final String username) {
        final Optional<User> userOptional = userRepository.findOneByEmailOrLogin(username);
        final User user = userOptional.orElseThrow(() -> new UsernameNotFoundException(
                format("User with identity '{0} has not been found!'", username)));

        return new org.springframework.security.core.userdetails.User(
                username, user.getPassword(), true, true, true, true,
                Collections.singletonList(user.getRole()));
    }

    @Override
    @Transactional(isolation = SERIALIZABLE, propagation = REQUIRED)
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
        if (userRepository.findOneByEmail(email).isPresent()) {
            throw raiseServiceFaultException(USER_ALREADY_REGISTERED,
                    format("User with email {0} is already registered", email));
        }
        if (userRepository.findOneByUsername(username).isPresent()) {
            throw raiseServiceFaultException(USER_ALREADY_REGISTERED,
                    format("User with username {0} is already registered", username));
        }

        final User user = new UserBuilder()
                .username(username)
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .role(USER.getRole())
                .build();

        userRepository.saveAndFlush(user);

        log.info(exitMethodMessage());
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, propagation = REQUIRED)
    public Optional<User> getUser(final int id) {
        log.info("{} id: {}", enterMethodMessage(), id);
        final Optional<User> optional = Optional.ofNullable(userRepository.findOne(id));
        log.info(exitMethodMessage());
        return optional;
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, propagation = REQUIRED)
    public Collection<User> getUsers(
            final int page,
            final int pageSize,
            final UserSearchableField searchField,
            final SearchType searchType,
            final Object searchValue,
            final Object searchValueLowerBound,
            final Object searchValueUpperBound,
            final SortingOrder sortingOrder,
            final UserSearchableField sortingField) {
        log.info("{} " +
                        "page: {}, " +
                        "pageSize: {}, " +
                        "searchField: {}, " +
                        "searchType: {}, " +
                        "searchValue: {}, " +
                        "searchValueLowerBound: {}, " +
                        "searchValueUpperBound: {}, " +
                        "sortingOrder: {}, " +
                        "sortingField: {}",
                enterMethodMessage(),
                page, pageSize, searchField, searchType, searchValue,
                searchValueLowerBound, searchValueUpperBound, sortingOrder, sortingField);

        final TypedQuery<User> query = queryBuilder.buildQuery(
                searchField, searchType, searchValue, searchValueLowerBound,
                searchValueUpperBound, sortingOrder, sortingField);

        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        final List<User> result = query.getResultList();

        log.info(exitMethodMessage());
        return result;
    }
}
