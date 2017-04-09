package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.ReservationType;
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
public interface ReservationTypeRepository extends JpaRepository<ReservationType, Integer> {
    @Query("SELECT r FROM ReservationType r where r.name = :name")
    Optional<ReservationType> findOneByName(@Param("name") final String role);
}
