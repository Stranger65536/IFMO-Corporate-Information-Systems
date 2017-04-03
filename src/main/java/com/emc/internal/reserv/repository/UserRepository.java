package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Optional;

import static java.text.MessageFormat.format;


/**
 * @author trofiv
 * @date 05.03.2017
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer>, UserDetailsService {
    @Override
    default UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Optional<User> userOptional = this.findOneByEmailOrLogin(username);
        final User user = userOptional.orElseThrow(() -> new UsernameNotFoundException(
                format("User with identity '{0} has not been found!'", username)));

        return new org.springframework.security.core.userdetails.User(
                username, user.getPassword(), true, true, true, true,
                Collections.singletonList(user.getRole()));
    }

    @Query("SELECT r FROM User r where r.email = :identity or r.login = :identity")
    Optional<User> findOneByEmailOrLogin(@Param("identity") final String identity);
}
