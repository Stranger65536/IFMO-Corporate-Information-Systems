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
import com.emc.internal.reserv.util.query.QueryBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.emc.internal.reserv.dto.FaultCode.PENDING_RESERVATIONS_LIMIT_EXCEEDED;
import static com.emc.internal.reserv.dto.FaultCode.RESERVATION_DOES_NOT_EXIST;
import static com.emc.internal.reserv.dto.FaultCode.RESERVATION_OVERLAPS_WITH_ANOTHER_RESERVATION;
import static com.emc.internal.reserv.dto.FaultCode.RESERVATION_OVERLAPS_WITH_UNAVAILABLE_RESERVATION;
import static com.emc.internal.reserv.entity.ReservationStatuses.APPROVED;
import static com.emc.internal.reserv.entity.ReservationStatuses.CANCELED;
import static com.emc.internal.reserv.entity.ReservationStatuses.NEW_TIME_PROPOSED;
import static com.emc.internal.reserv.entity.ReservationStatuses.WAITING_FOR_APPROVAL;
import static com.emc.internal.reserv.entity.ReservationTypes.UNAVAILABLE;
import static com.emc.internal.reserv.util.EndpointUtil.getNonExistentReservationIdMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getPendingReservationsLimitExceededMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getUnavailableEventOverlappingMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.RuntimeUtil.enterMethodMessage;
import static com.emc.internal.reserv.util.RuntimeUtil.exitMethodMessage;
import static com.emc.internal.reserv.util.RuntimeUtil.raiseForgotEnumBranchException;
import static com.emc.internal.reserv.util.UserAction.APPROVE;
import static com.emc.internal.reserv.util.UserAction.CANCEL;
import static com.emc.internal.reserv.util.UserAction.PROPOSE_NEW_TIME;
import static com.emc.internal.reserv.util.UserAction.UPDATE;
import static java.sql.Timestamp.valueOf;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * @author trofiv
 * @date 13.04.2017
 */
@Log4j2
@Service("reservationService")
public class ReservationServiceImpl implements ReservationService {
    @SuppressWarnings("DuplicateStringLiteralInspection")
    private static final String RESERVATION_VARIABLE = "reservation";
    private static final String RESOURCE_VARIABLE = "resource";
    private static final String STARTS_AT_VARIABLE = "startsAt";
    private static final String ENDS_AT_VARIABLE = "endsAt";
    private static final String TYPE_VARIABLE = "type";
    private static final String FAULT_CODE = "faultCode";
    private static final String ACTION_VARIABLE = "action";
    private static final String USER_VARIABLE = "user";
    private static final String NEW_RESOURCE_VARIABLE = "newResource";
    private static final String NEW_STARTS_AT_VARIABLE = "newStartsAt";
    private static final String NEW_ENDS_AT_VARIABLE = "newEndsAt";
    private static final String NEW_TYPE_VARIABLE = "newType";
    private final ProcessEngine processEngine;
    private final ActionRepository actionRepository;
    private final ReservationRepository reservationRepository;
    private final QueryBuilder<ActualReservation, ReservationSearchableField> queryBuilder;
    private final ActualReservationRepository actualReservationRepository;
    private final ActualReservedResourceRepository actualReservedResourceRepository;
    private final int pendingReservationsLimit;


    @Autowired
    public ReservationServiceImpl(
            final ProcessEngine processEngine,
            final ActionRepository actionRepository,
            final ReservationRepository reservationRepository,
            final QueryBuilder<ActualReservation, ReservationSearchableField> queryBuilder,
            final ActualReservationRepository actualReservationRepository,
            final ActualReservedResourceRepository actualReservedResourceRepository,
            @Value("${com.emc.internal.reserv.pending-reservations-limit}") final int pendingReservationsLimit) {
        this.queryBuilder = queryBuilder;
        this.processEngine = processEngine;
        this.actionRepository = actionRepository;
        this.reservationRepository = reservationRepository;
        this.pendingReservationsLimit = pendingReservationsLimit;
        this.actualReservationRepository = actualReservationRepository;
        this.actualReservedResourceRepository = actualReservedResourceRepository;
    }

    @Override
    @Transactional(propagation = REQUIRED, isolation = READ_COMMITTED)
    public Collection<ActualReservation> getReservations(
            final int page,
            final int pageSize,
            final ReservationSearchableField searchField,
            final SearchType searchType,
            final Object searchValue,
            final Object searchValueLowerBound,
            final Object searchValueUpperBound,
            final SortingOrder sortingOrder,
            final ReservationSearchableField sortingField) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
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

        final TypedQuery<ActualReservation> query = queryBuilder.buildQuery(
                searchField, searchType, searchValue, searchValueLowerBound,
                searchValueUpperBound, sortingOrder, sortingField);

        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        final List<ActualReservation> result = query.getResultList();

        log.debug(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public Reservation getReservation(final long id) {
        log.debug("{} id: {}", enterMethodMessage(), id);
        final Reservation result = Optional.ofNullable(reservationRepository.findOne(id)).orElseThrow(() ->
                raiseServiceFaultException(RESERVATION_DOES_NOT_EXIST, getNonExistentReservationIdMessage(id)));
        log.debug(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public ActualReservation getActualReservation(final long id) {
        log.debug("{} id: {}", enterMethodMessage(), id);
        final ActualReservation result = Optional.ofNullable(actualReservationRepository.findOne(id)).orElseThrow(() ->
                raiseServiceFaultException(RESERVATION_DOES_NOT_EXIST, getNonExistentReservationIdMessage(id)));
        log.debug(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public Reservation placeReservation(
            final User user,
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType type) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "user: {}, " +
                        "resource: {}," +
                        "startsAt: {}, " +
                        "endsAt: {}, " +
                        "type: {}",
                enterMethodMessage(),
                user, resource, startsAt, endsAt, type);
        final Reservation reservation = createReservation(user);
        //noinspection DuplicateStringLiteralInspection
        final ExecutionEntity process = (ExecutionEntity) processEngine.getRuntimeService()
                .startProcessInstanceByKey("Reservation", Long.toString(reservation.getId()),
                        ImmutableMap.<String, Object>builder()
                                .put(USER_VARIABLE, user)
                                .put(RESOURCE_VARIABLE, resource)
                                .put(RESERVATION_VARIABLE, reservation)
                                .put(STARTS_AT_VARIABLE, startsAt)
                                .put(ENDS_AT_VARIABLE, endsAt)
                                .put(TYPE_VARIABLE, type)
                                .put("PENDING_RESERVATIONS_LIMIT_EXCEEDED", PENDING_RESERVATIONS_LIMIT_EXCEEDED)
                                .put("RESERVATION_OVERLAPS_WITH_UNAVAILABLE_RESERVATION", RESERVATION_OVERLAPS_WITH_UNAVAILABLE_RESERVATION)
                                .put("RESERVATION_OVERLAPS_WITH_ANOTHER_RESERVATION", RESERVATION_OVERLAPS_WITH_ANOTHER_RESERVATION)
                                .put("APPROVE", APPROVE)
                                .put("CANCEL", CANCEL)
                                .put("UPDATE", UPDATE)
                                .put("PROPOSE_NEW_TIME", PROPOSE_NEW_TIME)
                                .build());
        final FaultCode faultCode = (FaultCode) process.getVariable(FAULT_CODE);

        if (faultCode == null) {
            final Reservation result = reservationRepository.findOne(reservation.getId());
            log.debug(exitMethodMessage());
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
    @SuppressWarnings("Duplicates")
    @Transactional(propagation = MANDATORY)
    public Reservation cancelReservation(final User user, final Reservation reservation) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "user: {}, " +
                        "reservation: {}",
                enterMethodMessage(),
                user, reservation);
        final Task task = processEngine.getTaskService()
                .createTaskQuery()
                .processInstanceBusinessKey(Long.toString(reservation.getId()))
                .singleResult();

        processEngine.getTaskService().setVariable(task.getId(), ACTION_VARIABLE, CANCEL);
        processEngine.getTaskService().setVariable(task.getId(), USER_VARIABLE, user);
        processEngine.getTaskService().complete(task.getId());

        final Reservation result = reservationRepository.findOne(reservation.getId());
        log.debug(exitMethodMessage());
        return result;
    }

    @Override
    @SuppressWarnings("Duplicates")
    @Transactional(propagation = MANDATORY)
    public Reservation proposeNewTime(
            final User user,
            final Reservation reservation,
            final Resource newResource,
            final LocalDateTime newStartsAt,
            final LocalDateTime newEndsAt,
            final ReservationType newType) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "user: {}, " +
                        "reservation: {}, " +
                        "newResource: {}," +
                        "newStartsAt: {}, " +
                        "newEndsAt: {}, " +
                        "newType: {}",
                enterMethodMessage(),
                user, reservation, newResource, newStartsAt, newEndsAt, newType);
        final Task task = processEngine.getTaskService()
                .createTaskQuery()
                .processInstanceBusinessKey(Long.toString(reservation.getId()))
                .singleResult();

        processEngine.getRuntimeService().setVariable(task.getExecutionId(), FAULT_CODE, null);
        processEngine.getTaskService().setVariable(task.getId(), ACTION_VARIABLE, PROPOSE_NEW_TIME);
        processEngine.getTaskService().setVariable(task.getId(), USER_VARIABLE, user);
        processEngine.getTaskService().setVariable(task.getId(), NEW_RESOURCE_VARIABLE, newResource);
        processEngine.getTaskService().setVariable(task.getId(), NEW_STARTS_AT_VARIABLE, newStartsAt);
        processEngine.getTaskService().setVariable(task.getId(), NEW_ENDS_AT_VARIABLE, newEndsAt);
        processEngine.getTaskService().setVariable(task.getId(), NEW_TYPE_VARIABLE, newType);
        processEngine.getTaskService().complete(task.getId());

        final FaultCode faultCode = (FaultCode) processEngine.getRuntimeService().getVariable(task.getExecutionId(), FAULT_CODE);

        if (faultCode == null) {
            final Reservation result = reservationRepository.findOne(reservation.getId());
            log.debug(exitMethodMessage());
            return result;
        }

        //noinspection EnumSwitchStatementWhichMissesCases
        switch (faultCode) {
            case RESERVATION_OVERLAPS_WITH_UNAVAILABLE_RESERVATION:
                throw raiseServiceFaultException(faultCode, getUnavailableEventOverlappingMessage());
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    @Transactional(propagation = MANDATORY)
    public Reservation updateReservation(
            final User user,
            final Reservation reservation,
            final Resource newResource,
            final LocalDateTime newStartsAt,
            final LocalDateTime newEndsAt,
            final ReservationType newType) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "user: {}, " +
                        "reservation: {}, " +
                        "newResource: {}," +
                        "newStartsAt: {}, " +
                        "newEndsAt: {}, " +
                        "newType: {}",
                enterMethodMessage(),
                user, reservation, newResource, newStartsAt, newEndsAt, newType);
        final Task task = processEngine.getTaskService()
                .createTaskQuery()
                .processInstanceBusinessKey(Long.toString(reservation.getId()))
                .singleResult();

        processEngine.getRuntimeService().setVariable(task.getExecutionId(), FAULT_CODE, null);
        processEngine.getTaskService().setVariable(task.getId(), ACTION_VARIABLE, UPDATE);
        processEngine.getTaskService().setVariable(task.getId(), USER_VARIABLE, user);
        processEngine.getTaskService().setVariable(task.getId(), NEW_RESOURCE_VARIABLE, newResource);
        processEngine.getTaskService().setVariable(task.getId(), NEW_STARTS_AT_VARIABLE, newStartsAt);
        processEngine.getTaskService().setVariable(task.getId(), NEW_ENDS_AT_VARIABLE, newEndsAt);
        processEngine.getTaskService().setVariable(task.getId(), NEW_TYPE_VARIABLE, newType);
        processEngine.getTaskService().complete(task.getId());

        final FaultCode faultCode = (FaultCode) processEngine.getRuntimeService().getVariable(task.getExecutionId(), FAULT_CODE);

        if (faultCode == null) {
            final Reservation result = reservationRepository.findOne(reservation.getId());
            log.debug(exitMethodMessage());
            return result;
        }

        //noinspection EnumSwitchStatementWhichMissesCases
        switch (faultCode) {
            case RESERVATION_OVERLAPS_WITH_UNAVAILABLE_RESERVATION:
                throw raiseServiceFaultException(faultCode, getUnavailableEventOverlappingMessage());
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    @Transactional(propagation = MANDATORY)
    public Reservation approveReservation(final User user, final Reservation reservation) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "user: {}, " +
                        "reservation: {}",
                enterMethodMessage(),
                user, reservation);
        final Task task = processEngine.getTaskService()
                .createTaskQuery()
                .processInstanceBusinessKey(Long.toString(reservation.getId()))
                .singleResult();

        processEngine.getRuntimeService().setVariable(task.getExecutionId(), FAULT_CODE, null);
        processEngine.getTaskService().setVariable(task.getId(), ACTION_VARIABLE, APPROVE);
        processEngine.getTaskService().setVariable(task.getId(), USER_VARIABLE, user);
        processEngine.getTaskService().complete(task.getId());

        final FaultCode faultCode = (FaultCode) processEngine.getRuntimeService().getVariable(task.getExecutionId(), FAULT_CODE);

        if (faultCode == null) {
            final Reservation result = reservationRepository.findOne(reservation.getId());
            log.debug(exitMethodMessage());
            return result;
        }

        //noinspection EnumSwitchStatementWhichMissesCases
        switch (faultCode) {
            case RESERVATION_OVERLAPS_WITH_ANOTHER_RESERVATION:
                throw raiseServiceFaultException(faultCode, getUnavailableEventOverlappingMessage());
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public void fulfillReservationPlacement(
            final Reservation reservation,
            final Resource resource,
            final LocalDateTime start,
            final LocalDateTime end,
            final ReservationType type) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
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
        actionRepository.save(action);
        reservation.getActions().add(action);
        reservationRepository.save(reservation);
        log.debug(exitMethodMessage());
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public void fulfillReservationCancellation(final User user, final Reservation reservation) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "user: {}, " +
                        "reservation: {}",
                enterMethodMessage(),
                user, reservation);
        final Action lastAction = getLastAction(reservation);
        final Action action = lastAction.builder()
                .id(0)
                .user(user)
                .reservedAt(valueOf(LocalDateTime.now()))
                .status(CANCELED.getReservationStatus())
                .build();
        actionRepository.save(action);
        reservation.getActions().add(action);
        reservationRepository.save(reservation);
        log.debug(exitMethodMessage());
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public void fulfillReservationNewTimeProposal(
            final User user,
            final Reservation reservation,
            final Resource newResource,
            final LocalDateTime newStart,
            final LocalDateTime newEnd,
            final ReservationType newType) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "reservation: {}, " +
                        "newResource: {}," +
                        "newStart: {}, " +
                        "newEnd: {}, " +
                        "newType: {}",
                enterMethodMessage(),
                reservation, newResource, newStart, newEnd, newType);
        final Action action = new ActionBuilder()
                .reservedAt(valueOf(LocalDateTime.now()))
                .reservationStart(valueOf(newStart))
                .reservationEnd(valueOf(newEnd))
                .user(user)
                .resource(newResource)
                .type(newType)
                .status(NEW_TIME_PROPOSED.getReservationStatus())
                .reservation(reservation)
                .build();
        actionRepository.save(action);
        reservation.getActions().add(action);
        reservationRepository.save(reservation);
        log.debug(exitMethodMessage());
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public void fulfillReservationUpdate(
            final User user,
            final Reservation reservation,
            final Resource newResource,
            final LocalDateTime newStart,
            final LocalDateTime newEnd,
            final ReservationType newType) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "reservation: {}, " +
                        "newResource: {}," +
                        "newStart: {}, " +
                        "newEnd: {}, " +
                        "newType: {}",
                enterMethodMessage(),
                reservation, newResource, newStart, newEnd, newType);
        final Action lastAction = getLastAction(reservation);
        final Action action = lastAction.builder()
                .reservedAt(valueOf(LocalDateTime.now()))
                .reservationStart(valueOf(newStart))
                .reservationEnd(valueOf(newEnd))
                .resource(newResource)
                .type(newType)
                .build();
        reservation.getActions().remove(lastAction);
        reservation.getActions().add(action);
        actionRepository.save(action);
        reservationRepository.save(reservation);
        log.debug(exitMethodMessage());
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public void fulfillReservationApproval(final User user, final Reservation reservation) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "user: {}, " +
                        "reservation: {}",
                enterMethodMessage(),
                user, reservation);
        final Action lastAction = getLastAction(reservation);
        final Action action = lastAction.builder()
                .id(0)
                .user(user)
                .reservedAt(valueOf(LocalDateTime.now()))
                .status(APPROVED.getReservationStatus())
                .build();
        actionRepository.save(action);
        reservation.getActions().add(action);
        reservationRepository.save(reservation);
        log.debug(exitMethodMessage());
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public boolean isPendingReservationsNumberLimitExceeded(final User user) {
        log.debug("{} user: {}", enterMethodMessage(), user);
        final boolean result = actualReservationRepository.countReservationsByStatus(
                user, WAITING_FOR_APPROVAL.getReservationStatus()) >= pendingReservationsLimit;
        log.debug(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public boolean hasOverlappingsWithUnavailableReservations(
            final Reservation reservation,
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "reservation: {}, " +
                        "resource: {}, " +
                        "startsAt: {}, " +
                        "endsAt: {}",
                enterMethodMessage(),
                reservation, resource, startsAt, endsAt);
        final boolean result = actualReservedResourceRepository.hasOverlappingReservation(
                reservation, resource, UNAVAILABLE.getReservationType(), valueOf(startsAt), valueOf(endsAt));
        log.debug(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public boolean hasOverlappingsWithReservations(
            final Reservation reservation,
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "reservation: {}, " +
                        "resource: {}, " +
                        "startsAt: {}, " +
                        "endsAt: {}",
                enterMethodMessage(),
                reservation, resource, startsAt, endsAt);
        final boolean result = actualReservedResourceRepository.hasOverlappingReservation(
                reservation, resource, valueOf(startsAt), valueOf(endsAt));
        log.debug(exitMethodMessage());
        return result;
    }

    @Override
    public boolean hasOverlappingsWithUnavailableReservations(
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "resource: {}, " +
                        "startsAt: {}, " +
                        "endsAt: {}",
                enterMethodMessage(),
                resource, startsAt, endsAt);
        final boolean result = actualReservedResourceRepository.hasOverlappingReservation(
                resource, UNAVAILABLE.getReservationType(), valueOf(startsAt), valueOf(endsAt));
        log.debug(exitMethodMessage());
        return result;
    }

    @Override
    public boolean hasOverlappingsWithReservations(
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt) {
        //noinspection DuplicateStringLiteralInspection
        log.debug("{} " +
                        "resource: {}, " +
                        "startsAt: {}, " +
                        "endsAt: {}",
                enterMethodMessage(),
                resource, startsAt, endsAt);
        final boolean result = actualReservedResourceRepository.hasOverlappingReservation(
                resource, valueOf(startsAt), valueOf(endsAt));
        log.debug(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public Reservation createReservation(final User user) {
        log.debug("{} user: {}", enterMethodMessage(), user);
        final Reservation reservation = new ReservationBuilder().user(user).build();
        final Reservation result = reservationRepository.save(reservation);
        log.debug(exitMethodMessage());
        return result;
    }

    private static Action getLastAction(final Reservation reservation) {
        return Streams.findLast(reservation.getActions().stream())
                .orElseThrow(RuntimeUtil::raiseUninitializedEntityField);
    }
}
