package com.emc.internal.reserv.service;

import com.emc.internal.reserv.dto.FaultCode;
import com.emc.internal.reserv.dto.ReservationSearchableField;
import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.SortingOrder;
import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.entity.ReservationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import static java.util.Collections.emptyList;

/**
 * @author trofiv
 * @date 13.04.2017
 */
@Service
public class ReservationServiceImpl implements ReservationService {
    @Override
    public Collection<Reservation> getReservations(
            final int page,
            final int pageSize,
            final ReservationSearchableField searchField,
            final SearchType searchType,
            final String searchValue,
            final String searchValueLowerBound,
            final String searchValueUpperBound,
            final SortingOrder sortingOrder,
            final ReservationSearchableField sortingField) {
        //TODO
        return emptyList();
    }

    @Override
    public Reservation placeReservation(
            final int userId,
            final int resourceId,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type) {
        return null;
    }

    @Override
    public Reservation updateReservation(
            final int userId,
            final long reservationId,
            final int resourceId,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type) {
        return null;
    }

    @Override
    public Reservation acceptReservation(
            final int userId,
            final long reservationId,
            final int resourceId,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type) {
        return null;
    }

    @Override
    public Reservation cancelReservation(
            final int userId,
            final long reservationId,
            final int resourceId,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type) {
        return null;
    }

    @Override
    public Reservation proposeNewTime(
            final int userId,
            final long reservationId,
            final int resourceId,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type,
            final int newResourceId,
            final LocalDateTime newStartsAt,
            final LocalDateTime newEndsAt,
            final ReservationType newType) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean isPendingReservationsNumberLimitExceeded(final int userId) {
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean hasOverlappingsWithUnavailableEvent(
            final int resourceId,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt) {
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean hasOverlappingsWithEvent(
            final int resourceId,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt) {
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void declineRequest(
            final String placementUuid,
            final FaultCode code, final String message) {

    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void passRequest(final String placementUuid) {

    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void storeReservation(final Reservation reservation) {

    }
}
