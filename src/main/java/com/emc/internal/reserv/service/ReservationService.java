package com.emc.internal.reserv.service;

import com.emc.internal.reserv.dto.ReservationSearchableField;
import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.SortingOrder;
import com.emc.internal.reserv.entity.ActualReservation;
import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.entity.ReservationType;
import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

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

    Optional<Reservation> getReservation(final long id);

    Optional<ActualReservation> getActualReservation(final long id);

    Reservation placeReservation(final User user,
                                 final Resource resource,
                                 final LocalDateTime startsAt,
                                 final LocalDateTime endsAt,
                                 final ReservationType type);

    Reservation updateReservation(final User user,
                                  final Reservation reservation,
                                  final Resource resource,
                                  final LocalDateTime startsAt,
                                  final LocalDateTime endsAt,
                                  final ReservationType type);

    Reservation acceptReservation(final User user,
                                  final Reservation reservation,
                                  final Resource resource,
                                  final LocalDateTime startsAt,
                                  final LocalDateTime endsAt,
                                  final ReservationType type);

    void fulfillReservationCancel(final Reservation reservation);

    Reservation cancelReservation(final Reservation reservation);

    Reservation proposeNewTime(final User user,
                               final Reservation reservation,
                               final Resource resource,
                               final LocalDateTime startsAt,
                               final LocalDateTime endsAt,
                               final ReservationType type,
                               final Resource newResource,
                               final LocalDateTime newStartsAt,
                               final LocalDateTime newEndsAt,
                               final ReservationType newType);

    @SuppressWarnings("unused")
        //used explicitly at bpmn
    boolean isPendingReservationsNumberLimitExceeded(final User user);

    @SuppressWarnings("unused")
        //used explicitly at bpmn
    boolean hasOverlappingsWithUnavailableReservations(final Resource resource,
                                                       final LocalDateTime startsAt,
                                                       final LocalDateTime endsAt);

    @SuppressWarnings("unused")
        //used explicitly at bpmn
    boolean hasOverlappingsWithReservations(final Resource resource,
                                            final LocalDateTime startsAt,
                                            final LocalDateTime endsAt);

    Reservation createReservation(final User user);

    @SuppressWarnings("unused")
        //used explicitly at bpmn
    void fulfillReservationPlacement(
            final Reservation reservation,
            final Resource resource,
            final LocalDateTime start,
            final LocalDateTime end,
            final ReservationType type);
}
