package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.entity.ReservationType;
import com.emc.internal.reserv.entity.Resource;

import java.sql.Timestamp;

/**
 * @author trofiv
 * @date 30.04.2017
 */
public interface ActualReservedResourceRepository {
    boolean hasOverlappingReservation(final Reservation reservation,
                                      final Resource resource,
                                      final ReservationType reservationType,
                                      final Timestamp start,
                                      final Timestamp end);

    boolean hasOverlappingReservation(final Reservation reservation,
                                      final Resource resource,
                                      final Timestamp start,
                                      final Timestamp end);

    boolean hasOverlappingReservation(final Resource resource,
                                      final ReservationType reservationType,
                                      final Timestamp start,
                                      final Timestamp end);

    boolean hasOverlappingReservation(final Resource resource,
                                      final Timestamp start,
                                      final Timestamp end);
}
