package com.emc.internal.reserv.service;

import com.emc.internal.reserv.dto.FaultCode;
import com.emc.internal.reserv.dto.ReservationSearchableField;
import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.SortingOrder;
import com.emc.internal.reserv.entity.Action;
import com.emc.internal.reserv.entity.Action.ActionBuilder;
import com.emc.internal.reserv.entity.ActualReservation;
import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.entity.Reservation.ReservationBuilder;
import com.emc.internal.reserv.entity.ReservationType;
import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.repository.ActionRepository;
import com.emc.internal.reserv.repository.ActualReservationRepository;
import com.emc.internal.reserv.repository.ActualReservedResourceRepository;
import com.emc.internal.reserv.repository.ReservationRepository;
import com.emc.internal.reserv.util.RuntimeUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static com.emc.internal.reserv.dto.FaultCode.PENDING_RESERVATIONS_LIMIT_EXCEEDED;
import static com.emc.internal.reserv.dto.FaultCode.RESERVATION_OVERLAPS_WITH_UNAVAILABLE_RESERVATION;
import static com.emc.internal.reserv.entity.ReservationStatuses.CANCELED;
import static com.emc.internal.reserv.entity.ReservationStatuses.WAITING_FOR_APPROVAL;
import static com.emc.internal.reserv.entity.ReservationTypes.UNAVAILABLE;
import static com.emc.internal.reserv.util.EndpointUtil.getPendingReservationsLimitExceededMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getUnavailableEventOverlappingMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.RuntimeUtil.enterMethodMessage;
import static com.emc.internal.reserv.util.RuntimeUtil.exitMethodMessage;
import static com.emc.internal.reserv.util.RuntimeUtil.raiseForgotEnumBranchException;
import static com.emc.internal.reserv.util.UserAction.ACCEPT;
import static com.emc.internal.reserv.util.UserAction.APPROVE;
import static com.emc.internal.reserv.util.UserAction.CANCEL;
import static com.emc.internal.reserv.util.UserAction.PROPOSE_NEW_TIME;
import static com.emc.internal.reserv.util.UserAction.UPDATE;
import static java.sql.Timestamp.valueOf;
import static java.util.Collections.emptyList;

/**
 * @author trofiv
 * @date 13.04.2017
 */
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
        //noinspection DuplicateStringLiteralInspection
        log.info("{} " +
                        "page: {}, " +
                        "pageSize: {}," +
                        "searchField: {}, " +
                        "searchType: {}, " +
                        "searchValue: {}, " +
                        "searchValueLowerBound: {}, " +
                        "searchValueUpperBound: {}, " +
                        "sortingOrder: {}, " +
                        "sortingField: {}",
                enterMethodMessage(),
                page, pageSize, searchField, searchType, searchValue,
                searchValueLowerBound, searchValueUpperBound, sortingOrder, sortingField);
        //TODO
        final Collection<Reservation> result = emptyList();
        log.info(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Reservation> getReservation(final long id) {
        log.info("{} id: {}", enterMethodMessage(), id);
        final Optional<Reservation> result = Optional.ofNullable(reservationRepository.findOne(id));
        log.info(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<ActualReservation> getActualReservation(final long id) {
        log.info("{} id: {}", enterMethodMessage(), id);
        final Optional<ActualReservation> result = Optional.ofNullable(actualReservationRepository.findOne(id));
        log.info(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Reservation placeReservation(
            final User user,
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type) {
        //noinspection DuplicateStringLiteralInspection
        log.info("{} " +
                        "user: {}, " +
                        "resource: {}," +
                        "startsAt: {}, " +
                        "endsAt: {}, " +
                        "type: {}",
                enterMethodMessage(),
                user, resource, startsAt, endsAt, type);
        final Reservation reservation = createReservation(user);
        final ExecutionEntity process = (ExecutionEntity) processEngine.getRuntimeService()
                .startProcessInstanceByKey("Reservation", Long.toString(reservation.getId()),
                        ImmutableMap.<String, Object>builder()
                                .put("user", user)
                                .put("resource", resource)
                                .put("reservation", reservation)
                                .put("startsAt", startsAt)
                                .put("endsAt", endsAt)
                                .put("type", type)
                                .put("PENDING_RESERVATIONS_LIMIT_EXCEEDED", PENDING_RESERVATIONS_LIMIT_EXCEEDED)
                                .put("EVENT_OVERLAPS_WITH_UNAVAILABLE_EVENT", RESERVATION_OVERLAPS_WITH_UNAVAILABLE_RESERVATION)
                                .put("APPROVE", APPROVE)
                                .put("CANCEL", CANCEL)
                                .put("UPDATE", UPDATE)
                                .put("ACCEPT", ACCEPT)
                                .put("PROPOSE_NEW_TIME", PROPOSE_NEW_TIME)
                                .build());
        final FaultCode faultCode = (FaultCode) process.getVariable("faultCode");

        if (faultCode == null) {
            final Reservation result = reservationRepository.findOne(reservation.getId());
            log.info(exitMethodMessage());
            return result;
        }

        //noinspection EnumSwitchStatementWhichMissesCases
        switch (faultCode) {
            case PENDING_RESERVATIONS_LIMIT_EXCEEDED:
                throw raiseServiceFaultException(faultCode, getPendingReservationsLimitExceededMessage());
            case RESERVATION_OVERLAPS_WITH_UNAVAILABLE_RESERVATION:
                throw raiseServiceFaultException(faultCode, getUnavailableEventOverlappingMessage());
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Reservation updateReservation(
            final User user,
            final Reservation reservation,
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type) {
        //noinspection DuplicateStringLiteralInspection
        log.info("{} " +
                        "user: {}, " +
                        "reservation: {}, " +
                        "resource: {}," +
                        "startsAt: {}, " +
                        "endsAt: {}, " +
                        "type: {}",
                enterMethodMessage(),
                user, reservation, resource, startsAt, endsAt, type);
        //TODO
        final Reservation result = null;
        log.info(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Reservation acceptReservation(
            final User user,
            final Reservation reservation,
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type) {
        //noinspection DuplicateStringLiteralInspection
        log.info("{} " +
                        "user: {}, " +
                        "reservation: {}, " +
                        "resource: {}," +
                        "startsAt: {}, " +
                        "endsAt: {}, " +
                        "type: {}",
                enterMethodMessage(),
                user, reservation, resource, startsAt, endsAt, type);
        //TODO
        final Reservation result = null;
        log.info(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void fulfillReservationCancel(final Reservation reservation) {
        //noinspection DuplicateStringLiteralInspection
        log.info("{} reservation: {}", enterMethodMessage(), reservation);
        final Action lastAction = Streams.findLast(reservation.getActions().stream())
                .orElseThrow(RuntimeUtil::raiseUninitializedEntityField);
        final Action action = lastAction.builder()
                .id(0)
                .reservedAt(valueOf(LocalDateTime.now()))
                .status(CANCELED.getReservationStatus())
                .build();
        reservation.getActions().add(action);
        reservationRepository.saveAndFlush(reservation);
        log.info(exitMethodMessage());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Reservation cancelReservation(final Reservation reservation) {
        //noinspection DuplicateStringLiteralInspection
        log.info("{} reservation: {}", enterMethodMessage(), reservation);

        final Task task = processEngine.getTaskService()
                .createTaskQuery()
                .processInstanceBusinessKey(Long.toString(reservation.getId()))
                .singleResult();

        processEngine.getTaskService().setVariable(task.getId(), "action", CANCEL);
        processEngine.getTaskService().complete(task.getId());

        final Reservation result = reservationRepository.findOne(reservation.getId());
        log.info(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
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
        //noinspection DuplicateStringLiteralInspection
        log.info("{} " +
                        "user: {}, " +
                        "reservation: {}, " +
                        "resource: {}," +
                        "startsAt: {}, " +
                        "endsAt: {}, " +
                        "type: {}, " +
                        "newResource: {}," +
                        "newStartsAt: {}, " +
                        "newEndsAt: {}, " +
                        "newType: {}",
                enterMethodMessage(),
                user, reservation, resource, startsAt, endsAt, type, newResource, newStartsAt, newEndsAt, newType);
        //TODO
        final Reservation result = null;
        log.info(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean isPendingReservationsNumberLimitExceeded(final User user) {
        log.info("{} user: {}", enterMethodMessage(), user);
        final boolean result = actualReservationRepository.countReservationsByStatus(
                user, WAITING_FOR_APPROVAL.getReservationStatus()) >= pendingReservationsLimit;
        log.info(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean hasOverlappingsWithUnavailableReservations(
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt) {
        //noinspection DuplicateStringLiteralInspection
        log.info("{} " +
                        "resource: {}, " +
                        "startsAt: {}, " +
                        "endsAt: {}",
                enterMethodMessage(),
                resource, startsAt, endsAt);
        final boolean result = actualReservedResourceRepository.hasOverlappingReservation(
                resource, UNAVAILABLE.getReservationType(), valueOf(startsAt), valueOf(endsAt));
        log.info(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean hasOverlappingsWithReservations(
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt) {
        //noinspection DuplicateStringLiteralInspection
        log.info("{} " +
                        "resource: {}, " +
                        "startsAt: {}, " +
                        "endsAt: {}",
                enterMethodMessage(),
                resource, startsAt, endsAt);
        final boolean result = actualReservedResourceRepository.hasOverlappingReservation(resource, valueOf(startsAt), valueOf(endsAt));
        log.info(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Reservation createReservation(final User user) {
        log.info("{} user: {}", enterMethodMessage(), user);
        final Reservation reservation = new ReservationBuilder().user(user).build();
        final Reservation result = reservationRepository.saveAndFlush(reservation);
        log.info(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void fulfillReservationPlacement(
            final Reservation reservation,
            final Resource resource,
            final LocalDateTime start,
            final LocalDateTime end,
            final ReservationType type) {
        //noinspection DuplicateStringLiteralInspection
        log.info("{} " +
                        "reservation: {}, " +
                        "resource: {}," +
                        "startsAt: {}, " +
                        "endsAt: {}, " +
                        "type: {}",
                enterMethodMessage(),
                reservation, resource, start, end, type);
        final Action action = new ActionBuilder()
                .reservedAt(valueOf(LocalDateTime.now()))
                .reservationStart(valueOf(start))
                .reservationEnd(valueOf(end))
                .user(reservation.getUser())
                .resource(resource)
                .type(type)
                .status(WAITING_FOR_APPROVAL.getReservationStatus())
                .reservation(reservation)
                .build();
        actionRepository.saveAndFlush(action);
        reservation.getActions().add(action);
        reservationRepository.saveAndFlush(reservation);
        log.info(exitMethodMessage());
    }
}
