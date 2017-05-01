package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author trofiv
 * @date 05.03.2017
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
