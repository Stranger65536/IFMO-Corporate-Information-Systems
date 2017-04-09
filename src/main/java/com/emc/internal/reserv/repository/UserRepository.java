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
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT r FROM User r where r.email = :identity or r.login = :identity")
    Optional<User> findOneByEmailOrLogin(@Param("identity") final String identity);
}
