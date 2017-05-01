package com.emc.internal.reserv.service;

import com.emc.internal.reserv.dto.ReservationSearchableField;
import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.SortingOrder;
import com.emc.internal.reserv.entity.Action;
import com.emc.internal.reserv.entity.Action.ActionBuilder;
import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.entity.Reservation.ReservationBuilder;
import com.emc.internal.reserv.entity.ReservationType;
import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.repository.ActionRepository;
import com.emc.internal.reserv.repository.ActualReservationRepository;
import com.emc.internal.reserv.repository.ActualReservedResourceRepository;
import com.emc.internal.reserv.repository.ReservationRepository;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.emc.internal.reserv.entity.ReservationStatuses.WAITING_FOR_APPROVAL;
import static com.emc.internal.reserv.entity.ReservationTypes.UNAVAILABLE;
import static java.sql.Timestamp.valueOf;
import static java.util.Collections.emptyList;

/**
 * @author trofiv
 * @date 13.04.2017
 */
//TODO enter exit operations
@Log4j2
@Service("reservationService")
public class ReservationServiceImpl implements ReservationService {
    private final ProcessEngine processEngine;
    private final ActionRepository actionRepository;
    private final ReservationRepository reservationRepository;
    private final ActualReservationRepository actualReservationRepository;
    private final ActualReservedResourceRepository actualReservedResourceRepository;
    private final int pendingReservationsLimit;

    @Autowired
    public ReservationServiceImpl(
            final ProcessEngine processEngine,
            final ActionRepository actionRepository,
            final ReservationRepository reservationRepository,
            final ActualReservationRepository actualReservationRepository,
            final ActualReservedResourceRepository actualReservedResourceRepository,
            @Value("${com.emc.internal.reserv.pending-reservations-limit}") final int pendingReservationsLimit) {
        this.processEngine = processEngine;
        this.actionRepository = actionRepository;
        this.reservationRepository = reservationRepository;
        this.pendingReservationsLimit = pendingReservationsLimit;
        this.actualReservationRepository = actualReservationRepository;
        this.actualReservedResourceRepository = actualReservedResourceRepository;
    }

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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public Reservation placeReservation(
            final User user,
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type) {
        final Map<String, Object> variables = new HashMap<>(16);
        variables.put("user", user);
        variables.put("resource", resource);
        variables.put("startsAt", startsAt);
        variables.put("endsAt", endsAt);
        variables.put("type", type);
        final ProcessInstance process = this.processEngine.getRuntimeService().startProcessInstanceByKey("Reservation", variables);
        //TODO
        return null;
    }

    @Override
    public Reservation updateReservation(
            final User user,
            final Reservation reservation,
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type) {
        //TODO
        return null;
    }

    @Override
    public Reservation acceptReservation(
            final User user,
            final Reservation reservation,
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type) {
        //TODO
        return null;
    }

    @Override
    public Reservation cancelReservation(
            final User user,
            final Reservation reservation,
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type) {
        //TODO
        return null;
    }

    @Override
    public Reservation proposeNewTime(
            final User user,
            final Reservation reservation,
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type,
            final Resource newResource,
            final LocalDateTime newStartsAt,
            final LocalDateTime newEndsAt,
            final ReservationType newType) {
        //TODO
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean isPendingReservationsNumberLimitExceeded(final User user) {
        return actualReservationRepository.countReservationsByStatus(user, WAITING_FOR_APPROVAL.getReservationStatus()) > pendingReservationsLimit;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean hasOverlappingsWithUnavailableEvent(
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt) {
        return actualReservedResourceRepository.hasOverlappingReservation(
                resource, UNAVAILABLE.getReservationType(), valueOf(startsAt), valueOf(endsAt));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean hasOverlappingsWithEvent(
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt) {
        return actualReservedResourceRepository.hasOverlappingReservation(resource, valueOf(startsAt), valueOf(endsAt));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void createReservation(
            final User user,
            final Resource resource,
            final LocalDateTime start,
            final LocalDateTime end,
            final ReservationType type) {
        final Reservation reservation = new ReservationBuilder().user(user).build();
        reservationRepository.saveAndFlush(reservation);
        final Action action = new ActionBuilder()
                .reservedAt(valueOf(LocalDateTime.now()))
                .reservationStart(valueOf(start))
                .reservationEnd(valueOf(end))
                .user(user)
                .resource(resource)
                .type(type)
                .status(WAITING_FOR_APPROVAL.getReservationStatus())
                .reservation(reservation)
                .build();
        actionRepository.saveAndFlush(action);
    }

    public void message(final String message) {
        log.info(message);
    }
}
