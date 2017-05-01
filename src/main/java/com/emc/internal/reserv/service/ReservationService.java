package com.emc.internal.reserv.service;

import com.emc.internal.reserv.dto.ReservationSearchableField;
import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.SortingOrder;
import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.entity.ReservationType;
import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.entity.User;

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

    Reservation cancelReservation(final User user,
                                  final Reservation reservation,
                                  final Resource resource,
                                  final LocalDateTime startsAt,
                                  final LocalDateTime endsAt,
                                  final ReservationType type);

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

    @SuppressWarnings("unused") //used explicitly at bpmn
    boolean isPendingReservationsNumberLimitExceeded(final User user);

    @SuppressWarnings("unused") //used explicitly at bpmn
    boolean hasOverlappingsWithUnavailableEvent(final Resource resource,
                                                final LocalDateTime startsAt,
                                                final LocalDateTime endsAt);

    @SuppressWarnings("unused") //used explicitly at bpmn
    boolean hasOverlappingsWithEvent(final Resource resource,
                                     final LocalDateTime startsAt,
                                     final LocalDateTime endsAt);

    @SuppressWarnings("unused") //used explicitly at bpmn
    void createReservation(
            final User user,
            final Resource resource,
            final LocalDateTime start,
            final LocalDateTime end,
            final ReservationType type);

    void message(final String message);
}
