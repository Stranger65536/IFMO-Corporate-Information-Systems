package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.ActualReservation;
import com.emc.internal.reserv.entity.ReservationStatus;
import com.emc.internal.reserv.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author trofiv
 * @date 30.04.2017
 */
@Repository
public interface ActualReservationRepository extends CrudRepository<ActualReservation, Long> {
    @Query("SELECT COUNT(r) FROM ActualReservation r WHERE r.owner = :user and r.status = :status")
    long countReservationsByStatus(@Param("user") final User user, @Param("status") final ReservationStatus status);
}
