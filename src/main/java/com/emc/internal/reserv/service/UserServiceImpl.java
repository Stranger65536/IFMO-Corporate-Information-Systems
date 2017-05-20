package com.emc.internal.reserv.service;

import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.SortingOrder;
import com.emc.internal.reserv.dto.UserSearchableField;
import com.emc.internal.reserv.entity.Role;
import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.entity.User.UserBuilder;
import com.emc.internal.reserv.repository.UserRepository;
import com.emc.internal.reserv.util.query.QueryBuilder;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.emc.internal.reserv.dto.FaultCode.USER_ALREADY_REGISTERED;
import static com.emc.internal.reserv.dto.FaultCode.USER_DOES_NOT_EXIST;
import static com.emc.internal.reserv.entity.Roles.USER;
import static com.emc.internal.reserv.util.EndpointUtil.getNonExistentUserIdMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getNonExistentUsernameMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getUserEmailTakenMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getUsernameTakenMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.RuntimeUtil.enterMethodMessage;
import static com.emc.internal.reserv.util.RuntimeUtil.exitMethodMessage;
import static com.emc.internal.reserv.util.RuntimeUtil.hashPassword;
import static java.util.Collections.singletonList;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * @author trofiv
 * @date 04.04.2017
 */
@Log4j2
@Service("userService")
@SuppressWarnings("DuplicateStringLiteralInspection")
public class UserServiceImpl implements UserService, UserDetailsService {
    static {
        checkSha512Supported();
    }

    private final UserRepository userRepository;
    private final QueryBuilder<User, UserSearchableField> queryBuilder;

    @Autowired
    public UserServiceImpl(
            final UserRepository userRepository,
            final QueryBuilder<User, UserSearchableField> queryBuilder) {
        this.userRepository = userRepository;
        this.queryBuilder = queryBuilder;
    }

    @SneakyThrows
    private static void checkSha512Supported() {
        MessageDigest.getInstance("SHA-512");
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
        final User user = userOptional.orElseThrow(() -> new UsernameNotFoundException(getNonExistentUsernameMessage(username)));

        return new org.springframework.security.core.userdetails.User(
                username, user.getPassword(), true, true, true, true,
                singletonList(user.getRole()));
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
        log.debug("{} " +
                        "username: {}, " +
                        "email: {}, " +
                        "firstName: {}, " +
                        "lastName: {}, " +
                        "middleName: {}",
                enterMethodMessage(),
                username, email, firstName, lastName, middleName);
        checkEmailIsNotTaken(email);
        checkUsernameIsNotTaken(username);

        final User user = new UserBuilder()
                .username(username)
                .email(email)
                .password(hashPassword(password))
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .role(USER.getRole())
                .build();

        userRepository.save(user);

        log.debug(exitMethodMessage());
    }

    private void checkEmailIsNotTaken(final String email) {
        if (userRepository.findOneByEmail(email).isPresent()) {
            throw raiseServiceFaultException(USER_ALREADY_REGISTERED, getUserEmailTakenMessage(email));
        }
    }

    private void checkUsernameIsNotTaken(final String username) {
        if (userRepository.findOneByUsername(username).isPresent()) {
            throw raiseServiceFaultException(USER_ALREADY_REGISTERED, getUsernameTakenMessage(username));
        }
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, propagation = REQUIRED)
    public User getUser(final int id) {
        log.debug("{} id: {}", enterMethodMessage(), id);
        final User user = Optional.ofNullable(userRepository.findOne(id)).orElseThrow(() ->
                raiseServiceFaultException(USER_DOES_NOT_EXIST, getNonExistentUserIdMessage(id)));
        log.debug(exitMethodMessage());
        return user;
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, propagation = REQUIRED)
    public User getUser(final String username) {
        log.debug("{} username: {}", enterMethodMessage(), username);
        final User user = userRepository.findOneByUsername(username).orElseThrow(() ->
                raiseServiceFaultException(USER_DOES_NOT_EXIST, getNonExistentUsernameMessage(username)));
        log.debug(exitMethodMessage());
        return user;
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
        log.debug("{} " +
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

        log.debug(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public void updateUserInfo(
            final User user,
            final String email,
            final String username,
            final String firstName,
            final String middleName,
            final String lastName) {
        log.debug("{} " +
                        "user: {}, " +
                        "email: {}, " +
                        "username: {}, " +
                        "firstName: {}, " +
                        "middleName: {}, " +
                        "lastName: {}",
                enterMethodMessage(),
                user, email, username, firstName, middleName, lastName);
        if (!Objects.equals(user.getEmail(), email)) {
            checkEmailIsNotTaken(email);
        }
        if (!Objects.equals(user.getUsername(), username)) {
            checkUsernameIsNotTaken(username);
        }

        final User updatedUser = user.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .build();

        userRepository.save(updatedUser);

        log.debug(exitMethodMessage());
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public void updatePassword(final User user, final String newPassword) {
        log.debug("{} " +
                        "user: {}",
                enterMethodMessage(), user);

        final User updatedUser = user.builder()
                .password(hashPassword(newPassword))
                .build();

        userRepository.save(updatedUser);

        log.debug(exitMethodMessage());
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public void grantPermissions(final User user, final Role role) {
        log.debug("{} " +
                        "user: {}, " +
                        "role: {}",
                enterMethodMessage(), user, role);

        final User updatedUser = user.builder()
                .role(role)
                .build();

        userRepository.save(updatedUser);

        log.debug(exitMethodMessage());
    }
}
