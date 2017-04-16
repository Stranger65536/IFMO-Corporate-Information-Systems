package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * @author trofiv
 * @date 05.03.2017
 */
@Repository
@SuppressWarnings("DuplicateStringLiteralInspection")
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT r FROM User r where r.email = :identity or r.username = :identity")
    Optional<User> findOneByEmailOrLogin(@Param("identity") final String identity);

    @Query("SELECT r FROM User r where r.email = :email")
    Optional<User> findOneByEmail(@Param("email") final String email);

    @Query("SELECT r FROM User r where r.email = :username or r.username = :username")
    Optional<User> findOneByUsername(@Param("username") final String username);
}
