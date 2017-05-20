package com.emc.internal.reserv.facade;

import com.emc.internal.reserv.converter.FieldConverter;
import com.emc.internal.reserv.converter.SearchValueHolder;
import com.emc.internal.reserv.dto.ApproveReservationRequest;
import com.emc.internal.reserv.dto.ApproveReservationResponse;
import com.emc.internal.reserv.dto.CancelReservationRequest;
import com.emc.internal.reserv.dto.CancelReservationResponse;
import com.emc.internal.reserv.dto.GetReservationRequest;
import com.emc.internal.reserv.dto.GetReservationResponse;
import com.emc.internal.reserv.dto.GetReservationsRequest;
import com.emc.internal.reserv.dto.GetReservationsResponse;
import com.emc.internal.reserv.dto.PlaceReservationRequest;
import com.emc.internal.reserv.dto.PlaceReservationResponse;
import com.emc.internal.reserv.dto.ProposeNewTimeRequest;
import com.emc.internal.reserv.dto.ProposeNewTimeResponse;
import com.emc.internal.reserv.dto.ReservationSearchableField;
import com.emc.internal.reserv.dto.UpdateReservationRequest;
import com.emc.internal.reserv.dto.UpdateReservationResponse;
import com.emc.internal.reserv.entity.Action;
import com.emc.internal.reserv.entity.ActualReservation;
import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.entity.ReservationStatus;
import com.emc.internal.reserv.entity.ReservationType;
import com.emc.internal.reserv.entity.ReservationTypes;
import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.entity.Roles;
import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.service.AuthenticationService;
import com.emc.internal.reserv.service.ReservationService;
import com.emc.internal.reserv.service.ResourceService;
import com.emc.internal.reserv.util.RuntimeUtil;
import com.emc.internal.reserv.validator.RequestValidator;
import com.google.common.collect.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

import static com.emc.internal.reserv.dto.FaultCode.ACCESS_DENIED;
import static com.emc.internal.reserv.dto.FaultCode.ACTION_IS_NOT_SUPPORTED_AT_RESERVATION_STATE;
import static com.emc.internal.reserv.dto.FaultCode.RESERVATION_INFO_IS_DIFFERENT;
import static com.emc.internal.reserv.entity.ReservationStatuses.APPROVED;
import static com.emc.internal.reserv.entity.ReservationStatuses.CANCELED;
import static com.emc.internal.reserv.util.EndpointUtil.getAccessDeniedMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getActionIsNotSupportedMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getReservationInfoIsDifferentMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.RuntimeUtil.toLocalDateTime;
import static java.sql.Timestamp.valueOf;
import static java.util.stream.Collectors.toList;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * @author trofiv
 * @date 17.04.2017
 */
@Service
public class ReservationFacadeImpl implements ReservationFacade {
    private final ResourceService resourceService;
    private final ReservationService reservationService;
    private final AuthenticationService authenticationService;
    private final RequestValidator<GetReservationsRequest> getReservationsRequestValidator;
    private final RequestValidator<PlaceReservationRequest> placeReservationRequestValidator;
    private final RequestValidator<UpdateReservationRequest> updateReservationRequestValidator;
    private final RequestValidator<ApproveReservationRequest> approveReservationRequestValidator;
    private final RequestValidator<CancelReservationRequest> cancelReservationRequestValidator;
    private final RequestValidator<ProposeNewTimeRequest> proposeNewTimeRequestValidator;
    private final FieldConverter<ReservationSearchableField> reservationSearchableFieldConverter;

    @Autowired
    public ReservationFacadeImpl(
            final ResourceService resourceService,
            final ReservationService reservationService,
            final AuthenticationService authenticationService,
            final RequestValidator<GetReservationsRequest> getReservationsRequestValidator,
            final RequestValidator<PlaceReservationRequest> placeReservationRequestValidator,
            final RequestValidator<UpdateReservationRequest> updateReservationRequestValidator,
            final RequestValidator<ApproveReservationRequest> approveReservationRequestValidator,
            final RequestValidator<CancelReservationRequest> cancelReservationRequestValidator,
            final RequestValidator<ProposeNewTimeRequest> proposeNewTimeRequestValidator,
            final FieldConverter<ReservationSearchableField> reservationSearchableFieldConverter) {
        this.resourceService = resourceService;
        this.reservationService = reservationService;
        this.authenticationService = authenticationService;
        this.getReservationsRequestValidator = getReservationsRequestValidator;
        this.placeReservationRequestValidator = placeReservationRequestValidator;
        this.updateReservationRequestValidator = updateReservationRequestValidator;
        this.approveReservationRequestValidator = approveReservationRequestValidator;
        this.cancelReservationRequestValidator = cancelReservationRequestValidator;
        this.proposeNewTimeRequestValidator = proposeNewTimeRequestValidator;
        this.reservationSearchableFieldConverter = reservationSearchableFieldConverter;
    }

    @Override
    @Transactional(propagation = REQUIRED, isolation = Isolation.READ_COMMITTED)
    public GetReservationResponse getReservation(final GetReservationRequest request) {
        final GetReservationResponse response = new GetReservationResponse();
        response.setReservationInfo(reservationService.getReservation(request.getId()).toReservationInfo());
        return response;
    }

    @Override
    //Callable service marked with the corresponding transactional level
    public GetReservationsResponse getReservations(final GetReservationsRequest request) {
        getReservationsRequestValidator.validate(request);
        final SearchValueHolder convertedSearchValues = reservationSearchableFieldConverter.convertSearchValues(
                request.getSearchType(),
                request.getSearchField(),
                request.getSearchValue(),
                request.getSearchValueLowerBound(),
                request.getSearchValueUpperBound());
        final Collection<ActualReservation> matchedReservations = reservationService.getReservations(
                request.getPage(),
                request.getPageSize(),
                request.getSearchField(),
                request.getSearchType(),
                convertedSearchValues.getSearchValue(),
                convertedSearchValues.getSearchValueLowerBound(),
                convertedSearchValues.getSearchValueUpperBound(),
                request.getSortingOrder(),
                request.getSortingField());
        final GetReservationsResponse response = new GetReservationsResponse();
        response.getActualReservationInfo().addAll(matchedReservations.stream().map(ActualReservation::toActualReservationInfo).collect(toList()));
        return response;
    }

    @Override
    @Transactional(propagation = REQUIRED, isolation = SERIALIZABLE)
    public PlaceReservationResponse placeReservation(final PlaceReservationRequest request) {
        placeReservationRequestValidator.validate(request);
        final User user = authenticationService.getCurrentUser();
        final Resource resource = resourceService.getResource(request.getResourceId());
        final Reservation reservation = reservationService.placeReservation(
                user,
                resource,
                toLocalDateTime(request.getStartsAt()),
                toLocalDateTime(request.getEndsAt()),
                ReservationTypes.getByName(request.getType().name()));
        final PlaceReservationResponse response = new PlaceReservationResponse();
        response.setReservationInfo(reservation.toReservationInfo());
        return response;
    }

    @Override
    @Transactional(propagation = REQUIRED, isolation = SERIALIZABLE)
    public UpdateReservationResponse updateReservation(final UpdateReservationRequest request) {
        updateReservationRequestValidator.validate(request);
        final User user = authenticationService.getCurrentUser();
        final Reservation reservation = reservationService.getReservation(request.getReservationId());

        checkSufficientPrivilegesToUpdate(user, reservation);
        checkReservationIsNotCancelledAndNotApproved(reservation);

        final Resource resource = resourceService.getResource(request.getResourceId());
        final Resource newResource = resourceService.getResource(request.getNewResourceId());
        final ActualReservation actualReservation = reservationService.getActualReservation(request.getReservationId());
        final ReservationType reservationType = ReservationTypes.getByName(request.getType().name());
        final ReservationType newReservationType = ReservationTypes.getByName(request.getNewType().name());

        checkReservationIsTheSame(actualReservation, resource, toLocalDateTime(request.getStartsAt()),
                toLocalDateTime(request.getEndsAt()), reservationType);

        final Reservation result = reservationService.updateReservation(user, reservation,
                newResource, toLocalDateTime(request.getNewStartsAt()),
                toLocalDateTime(request.getNewEndsAt()), newReservationType);
        final UpdateReservationResponse response = new UpdateReservationResponse();

        response.setReservationInfo(result.toReservationInfo());

        return response;
    }

    @Override
    @Transactional(propagation = REQUIRED, isolation = SERIALIZABLE)
    public ApproveReservationResponse approveReservation(final ApproveReservationRequest request) {
        approveReservationRequestValidator.validate(request);
        final User user = authenticationService.getCurrentUser();
        final Reservation reservation = reservationService.getReservation(request.getReservationId());

        checkSufficientPrivilegesToApprove(user, reservation);
        checkReservationIsNotCancelledAndNotApproved(reservation);

        final Resource resource = resourceService.getResource(request.getResourceId());
        final ActualReservation actualReservation = reservationService.getActualReservation(request.getReservationId());
        final ReservationType reservationType = ReservationTypes.getByName(request.getType().name());

        checkReservationIsTheSame(actualReservation, resource, toLocalDateTime(request.getStartsAt()),
                toLocalDateTime(request.getEndsAt()), reservationType);

        final Reservation result = reservationService.approveReservation(user, reservation);
        final ApproveReservationResponse response = new ApproveReservationResponse();

        response.setReservationInfo(result.toReservationInfo());

        return response;
    }

    @Override
    @Transactional(propagation = REQUIRED, isolation = SERIALIZABLE)
    public CancelReservationResponse cancelReservation(final CancelReservationRequest request) {
        cancelReservationRequestValidator.validate(request);
        final User user = authenticationService.getCurrentUser();
        final Reservation reservation = reservationService.getReservation(request.getReservationId());

        checkSufficientPrivilegesToCancel(user, reservation);
        checkReservationIsNotCancelled(reservation);

        final Resource resource = resourceService.getResource(request.getResourceId());
        final ActualReservation actualReservation = reservationService.getActualReservation(request.getReservationId());
        final ReservationType reservationType = ReservationTypes.getByName(request.getType().name());

        checkReservationIsTheSame(actualReservation, resource, toLocalDateTime(request.getStartsAt()),
                toLocalDateTime(request.getEndsAt()), reservationType);

        final Reservation result = reservationService.cancelReservation(user, reservation);
        final CancelReservationResponse response = new CancelReservationResponse();

        response.setReservationInfo(result.toReservationInfo());

        return response;
    }

    @Override
    @Transactional(propagation = REQUIRED, isolation = SERIALIZABLE)
    public ProposeNewTimeResponse proposeNewTime(final ProposeNewTimeRequest request) {
        proposeNewTimeRequestValidator.validate(request);
        final User user = authenticationService.getCurrentUser();
        final Reservation reservation = reservationService.getReservation(request.getReservationId());

        checkSufficientPrivilegesToProposeNewTime(user, reservation);
        checkReservationIsNotCancelled(reservation);

        final Resource resource = resourceService.getResource(request.getResourceId());
        final Resource newResource = resourceService.getResource(request.getNewResourceId());
        final ActualReservation actualReservation = reservationService.getActualReservation(request.getReservationId());
        final ReservationType reservationType = ReservationTypes.getByName(request.getType().name());
        final ReservationType newReservationType = ReservationTypes.getByName(request.getNewType().name());

        checkReservationIsTheSame(actualReservation, resource, toLocalDateTime(request.getStartsAt()),
                toLocalDateTime(request.getEndsAt()), reservationType);

        final Reservation result = reservationService.proposeNewTime(user, reservation,
                newResource, toLocalDateTime(request.getNewStartsAt()),
                toLocalDateTime(request.getNewEndsAt()), newReservationType);
        final ProposeNewTimeResponse response = new ProposeNewTimeResponse();

        response.setReservationInfo(result.toReservationInfo());

        return response;
    }

    private static void checkSufficientPrivilegesToProposeNewTime(final User user, final Reservation reservation) {
        checkActionByLeastModeratorAfterOwnerOrReverse(user, reservation);
    }

    private static void checkSufficientPrivilegesToCancel(final User user, final Reservation reservation) {
        checkSameUserOrLeastModerator(user, reservation);
    }

    private static void checkReservationIsNotCancelled(final Reservation reservation) {
        if (Objects.equals(getLastAction(reservation).getStatus(), CANCELED.getReservationStatus())) {
            throw raiseServiceFaultException(ACTION_IS_NOT_SUPPORTED_AT_RESERVATION_STATE, getActionIsNotSupportedMessage());
        }
    }

    private static void checkSameUserOrLeastModerator(final User user, final Reservation reservation) {
        if (reservation.getUser().getId() != user.getId() && user.getRole().equals(Roles.USER.getRole())) {
            throw raiseServiceFaultException(ACCESS_DENIED, getAccessDeniedMessage());
        }
    }

    private static void checkSufficientPrivilegesToApprove(final User user, final Reservation reservation) {
        checkActionByLeastModeratorAfterOwnerOrReverse(user, reservation);
    }

    private static void checkActionByLeastModeratorAfterOwnerOrReverse(final User user, final Reservation reservation) {
        final Action lastAction = getLastAction(reservation);
        if (Objects.equals(lastAction.getUser(), reservation.getUser())) {
            //last action made by owner, check current user is not a regular user
            if (Objects.equals(user.getRole(), Roles.USER.getRole())) {
                throw raiseServiceFaultException(ACCESS_DENIED, getAccessDeniedMessage());
            }
        } else {
            //last action made by moderator or higher, check current user is owner
            if (!Objects.equals(user, reservation.getUser())) {
                throw raiseServiceFaultException(ACCESS_DENIED, getAccessDeniedMessage());
            }
        }
    }

    private static void checkSufficientPrivilegesToUpdate(final User user, final Reservation reservation) {
        checkOwnerUpdatesLastOrLeastModeratorUpdatesLast(user, reservation);
    }

    private static void checkReservationIsNotCancelledAndNotApproved(final Reservation reservation) {
        final ReservationStatus status = getLastAction(reservation).getStatus();
        if (Objects.equals(status, CANCELED.getReservationStatus()) || Objects.equals(status, APPROVED.getReservationStatus())) {
            throw raiseServiceFaultException(ACTION_IS_NOT_SUPPORTED_AT_RESERVATION_STATE, getActionIsNotSupportedMessage());
        }
    }

    @SuppressWarnings({"OverlyComplexBooleanExpression", "MethodWithMoreThanThreeNegations"})
    private static void checkReservationIsTheSame(
            final ActualReservation actualReservation,
            final Resource resource,
            final LocalDateTime startsAt,
            final LocalDateTime endsAt,
            final ReservationType reservationType) {
        if (actualReservation.getResource().getId() != resource.getId()
                || !Objects.equals(actualReservation.getStartsAt(), valueOf(startsAt))
                || !Objects.equals(actualReservation.getEndsAt(), valueOf(endsAt))
                || !Objects.equals(actualReservation.getType(), reservationType)) {
            throw raiseServiceFaultException(RESERVATION_INFO_IS_DIFFERENT, getReservationInfoIsDifferentMessage());
        }
    }

    private static void checkOwnerUpdatesLastOrLeastModeratorUpdatesLast(final User user, final Reservation reservation) {
        final Action lastAction = getLastAction(reservation);
        if (Objects.equals(lastAction.getUser(), reservation.getUser())) {
            //last action made by owner, check current user is owner
            if (!Objects.equals(reservation.getUser(), user)) {
                throw raiseServiceFaultException(ACCESS_DENIED, getAccessDeniedMessage());
            }
        } else {
            //last action made by moderator or higher, check current user is not a regular user
            if (Objects.equals(user.getRole(), Roles.USER.getRole())) {
                throw raiseServiceFaultException(ACCESS_DENIED, getAccessDeniedMessage());
            }
        }
    }

    private static Action getLastAction(final Reservation reservation) {
        return Streams.findLast(reservation.getActions().stream())
                .orElseThrow(RuntimeUtil::raiseUninitializedEntityField);
    }
}
