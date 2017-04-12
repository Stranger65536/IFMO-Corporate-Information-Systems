package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.entity.User.UserBuilder;
import com.emc.internal.reserv.repository.UserRepository;
import com.emc.internal.reserv.util.HibernateUtil;
import https.internal_emc_com.reserv_io.ws.SortingOrder;
import https.internal_emc_com.reserv_io.ws.UserField;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
    private final EntityManager entityManager;

    @Autowired
    public UserServiceImpl(
            final UserRepository userRepository,
            final EntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
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
        if (userRepository.findOneByEmail(email) != null) {
            throw raiseServiceFaultException(USER_ALREADY_REGISTERED, format("User with email {0} already registered", email));
        }
        if (userRepository.findOneByUsername(username) != null) {
            throw raiseServiceFaultException(USER_ALREADY_REGISTERED, format("User with username {0} already registered", username));
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
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> query = builder.createQuery(User.class);
        final Root<User> root = query.from(User.class);
        CriteriaQuery<User> select = query.select(root);

        if (sortingField != null) {
            select = select.orderBy(HibernateUtil.getOrderExpression(builder, sortingOrder,
                    sortingField == UserField.ROLE
                            ? root.join(UserField.ROLE.value()).get("name")
                            : root.get(sortingField.value())));
        }

        if (filteringField != null) {
            select = select.where(builder.like(
                    filteringField == UserField.ROLE
                            ? root.join(UserField.ROLE.value()).get("name").as(String.class)
                            : root.get(filteringField.value()).as(String.class),
                    format("%{0}%", filteringValue), '%'));
        }

        final TypedQuery<User> typedQuery = entityManager.createQuery(select);
        typedQuery.setFirstResult((page - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);

        final List<User> result = typedQuery.getResultList();

        log.info(exitMethodMessage());
        return result;
    }
}
