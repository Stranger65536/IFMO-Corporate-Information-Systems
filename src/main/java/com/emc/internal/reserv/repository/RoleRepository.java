package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author trofiv
 * @date 05.03.2017
 */
@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    @Query("SELECT r FROM Role r where r.name = :name")
    Optional<Role> findOneByName(@Param("name") final String name);
}
