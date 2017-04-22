package com.emc.internal.reserv.service;

import com.emc.internal.reserv.dto.FaultCode;
import com.emc.internal.reserv.dto.ReservationSearchableField;
import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.SortingOrder;
import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.entity.ReservationType;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * @author trofiv
 * @date 13.04.2017
 */
public interface ReservationService {
    Collection<Reservation> getReservations(final int page,
                                            final int pageSize,
                                            final ReservationSearchableField searchField,
                                            final SearchType searchType,
                                            final String searchValue,
                                            final String searchValueLowerBound,
                                            final String searchValueUpperBound,
                                            final SortingOrder sortingOrder,
                                            final ReservationSearchableField sortingField);

    Reservation placeReservation(final int userId,
                                 final int resourceId,
                                 final LocalDateTime startsAt,
                                 final LocalDateTime endsAt,
                                 final ReservationType type);

    Reservation updateReservation(final int userId,
                                  final long reservationId,
                                  final int resourceId,
                                  final LocalDateTime startsAt,
                                  final LocalDateTime endsAt,
                                  final ReservationType type);

    Reservation acceptReservation(final int userId,
                                  final long reservationId,
                                  final int resourceId,
                                  final LocalDateTime startsAt,
                                  final LocalDateTime endsAt,
                                  final ReservationType type);

    Reservation cancelReservation(final int userId,
                                  final long reservationId,
                                  final int resourceId,
                                  final LocalDateTime startsAt,
                                  final LocalDateTime endsAt,
                                  final ReservationType type);

    Reservation proposeNewTime(final int userId,
                               final long reservationId,
                               final int resourceId,
                               final LocalDateTime startsAt,
                               final LocalDateTime endsAt,
                               final ReservationType type,
                               final int newResourceId,
                               final LocalDateTime newStartsAt,
                               final LocalDateTime newEndsAt,
                               final ReservationType newType);

    boolean isPendingReservationsNumberLimitExceeded(final int userId);

    boolean hasOverlappingsWithUnavailableEvent(final int resourceId,
                                                final LocalDateTime startsAt,
                                                final LocalDateTime endsAt);

    boolean hasOverlappingsWithEvent(final int resourceId,
                                     final LocalDateTime startsAt,
                                     final LocalDateTime endsAt);

    void declineRequest(final String placementUuid, final FaultCode code, final String message);

    void passRequest(final String placementUuid);

    void storeReservation(final Reservation reservation);
}
