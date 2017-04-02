package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.Action;
import com.emc.internal.reserv.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author trofiv
 * @date 05.03.2017
 */
@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
}
