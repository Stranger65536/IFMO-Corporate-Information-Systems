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

/**
 * @author trofiv
 * @date 13.04.2017
 */
public interface ReservationService {
    Collection<ActualReservation> getReservations(final int page,
                                                  final int pageSize,
                                                  final ReservationSearchableField searchField,
                                                  final SearchType searchType,
                                                  final Object searchValue,
                                                  final Object searchValueLowerBound,
                                                  final Object searchValueUpperBound,
                                                  final SortingOrder sortingOrder,
                                                  final ReservationSearchableField sortingField);

    Reservation getReservation(final long id);

    ActualReservation getActualReservation(final long id);

    Reservation placeReservation(final User user,
                                 final Resource resource,
                                 final LocalDateTime startsAt,
                                 final LocalDateTime endsAt,
                                 final ReservationType type);

    Reservation cancelReservation(final User user,
                                  final Reservation reservation);

    Reservation proposeNewTime(final User user,
                               final Reservation reservation,
                               final Resource newResource,
                               final LocalDateTime newStartsAt,
                               final LocalDateTime newEndsAt,
                               final ReservationType newType);

    Reservation updateReservation(final User user,
                                  final Reservation reservation,
                                  final Resource newResource,
                                  final LocalDateTime newStartsAt,
                                  final LocalDateTime newEndsAt,
                                  final ReservationType newType);

    Reservation approveReservation(final User user,
                                   final Reservation reservation);

    //used explicitly at bpmn
    @SuppressWarnings("unused")
    void fulfillReservationPlacement(
            final Reservation reservation,
            final Resource resource,
            final LocalDateTime start,
            final LocalDateTime end,
            final ReservationType type);

    //used explicitly at bpmn
    @SuppressWarnings("unused")
    void fulfillReservationCancellation(final User user,
                                        final Reservation reservation);

    //used explicitly at bpmn
    @SuppressWarnings("unused")
    void fulfillReservationNewTimeProposal(
            final User user,
            final Reservation reservation,
            final Resource newResource,
            final LocalDateTime newStart,
            final LocalDateTime newEnd,
            final ReservationType newType);

    //used explicitly at bpmn
    @SuppressWarnings("unused")
    void fulfillReservationUpdate(
            final User user,
            final Reservation reservation,
            final Resource newResource,
            final LocalDateTime newStart,
            final LocalDateTime newEnd,
            final ReservationType newType
    );

    //used explicitly at bpmn
    @SuppressWarnings("unused")
    void fulfillReservationApproval(final User user,
                                    final Reservation reservation);

    //used explicitly at bpmn
    @SuppressWarnings("unused")
    boolean isPendingReservationsNumberLimitExceeded(final User user);

    //used explicitly at bpmn
    @SuppressWarnings("unused")
    boolean hasOverlappingsWithUnavailableReservations(final Reservation reservation,
                                                       final Resource resource,
                                                       final LocalDateTime startsAt,
                                                       final LocalDateTime endsAt);

    //used explicitly at bpmn
    @SuppressWarnings("unused")
    boolean hasOverlappingsWithReservations(final Reservation reservation,
                                            final Resource resource,
                                            final LocalDateTime startsAt,
                                            final LocalDateTime endsAt);

    //used explicitly at bpmn
    @SuppressWarnings("unused")
    boolean hasOverlappingsWithUnavailableReservations(final Resource resource,
                                                       final LocalDateTime startsAt,
                                                       final LocalDateTime endsAt);

    //used explicitly at bpmn
    @SuppressWarnings("unused")
    boolean hasOverlappingsWithReservations(final Resource resource,
                                            final LocalDateTime startsAt,
                                            final LocalDateTime endsAt);

    Reservation createReservation(final User user);
}
