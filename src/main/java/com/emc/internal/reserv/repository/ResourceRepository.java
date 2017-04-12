package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.Resource;
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
public interface ResourceRepository extends JpaRepository<Resource, Integer> {
    @Query("SELECT r FROM Resource r where r.name = :name and r.location = :location")
    Optional<Resource> findOneByNameAndLocation(@Param("name") final String name, @Param("location") final String location);
}
