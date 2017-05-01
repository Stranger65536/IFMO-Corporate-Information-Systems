package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.ReservationType;
import com.emc.internal.reserv.entity.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * @author trofiv
 * @date 30.04.2017
 */
public interface ActualReservedResourceRepository {
    boolean hasOverlappingReservation(final Resource resource,
                                      final ReservationType reservationType,
                                      final Timestamp start,
                                      final Timestamp end);

    boolean hasOverlappingReservation(final Resource resource,
                                      final Timestamp start,
                                      final Timestamp end);
}
