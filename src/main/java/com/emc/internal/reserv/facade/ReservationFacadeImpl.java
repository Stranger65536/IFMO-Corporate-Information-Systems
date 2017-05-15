package com.emc.internal.reserv.facade;

import com.emc.internal.reserv.dto.AcceptReservationRequest;
import com.emc.internal.reserv.dto.AcceptReservationResponse;
import com.emc.internal.reserv.dto.ApproveReservationRequest;
import com.emc.internal.reserv.dto.ApproveReservationResponse;
import com.emc.internal.reserv.dto.CancelReservationRequest;
import com.emc.internal.reserv.dto.CancelReservationResponse;
import com.emc.internal.reserv.dto.GetReservationsRequest;
import com.emc.internal.reserv.dto.GetReservationsResponse;
import com.emc.internal.reserv.dto.PlaceReservationRequest;
import com.emc.internal.reserv.dto.PlaceReservationResponse;
import com.emc.internal.reserv.dto.ProposeNewTimeRequest;
import com.emc.internal.reserv.dto.ProposeNewTimeResponse;
import com.emc.internal.reserv.dto.UpdateReservationRequest;
import com.emc.internal.reserv.dto.UpdateReservationResponse;
import com.emc.internal.reserv.entity.ActualReservation;
import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.entity.ReservationStatuses;
import com.emc.internal.reserv.entity.ReservationType;
import com.emc.internal.reserv.entity.ReservationTypes;
import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.entity.Roles;
import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.service.ReservationService;
import com.emc.internal.reserv.service.ResourceService;
import com.emc.internal.reserv.service.UserService;
import com.emc.internal.reserv.util.RuntimeUtil;
import com.emc.internal.reserv.validator.RequestValidator;
import com.google.common.collect.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

import static com.emc.internal.reserv.dto.FaultCode.ACCESS_DENIED;
import static com.emc.internal.reserv.dto.FaultCode.ACTION_IS_NOT_SUPPORTED_AT_RESERVATION_STATE;
import static com.emc.internal.reserv.dto.FaultCode.RESERVATION_DOES_NOT_EXIST;
import static com.emc.internal.reserv.dto.FaultCode.RESERVATION_INFO_IS_DIFFERENT;
import static com.emc.internal.reserv.dto.FaultCode.RESOURCE_DOES_NOT_EXIST;
import static com.emc.internal.reserv.dto.FaultCode.USER_DOES_NOT_EXIST;
import static com.emc.internal.reserv.util.EndpointUtil.getAccessDeniedMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getActionIsNotSupportedMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getNonexistentReservationIdMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getNonexistentResourceIdMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getNonexistentUsernameMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getReservationInfoIsDifferentMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.RuntimeUtil.toLocalDateTime;
import static java.sql.Timestamp.valueOf;
import static java.util.stream.Collectors.toList;

/**
 * @author trofiv
 * @date 17.04.2017
 */
@Service
public class ReservationFacadeImpl implements ReservationFacade {
    private final UserService userService;
    private final ResourceService resourceService;
    private final ReservationService reservationService;
    private final RequestValidator<GetReservationsRequest> getReservationsRequestValidator;
    private final RequestValidator<PlaceReservationRequest> placeReservationRequestValidator;
    private final RequestValidator<UpdateReservationRequest> updateReservationRequestValidator;
    private final RequestValidator<AcceptReservationRequest> acceptReservationRequestValidator;
    private final RequestValidator<ApproveReservationRequest> approveReservationRequestValidator;
    private final RequestValidator<CancelReservationRequest> cancelReservationRequestValidator;
    private final RequestValidator<ProposeNewTimeRequest> proposeNewTimeRequestValidator;

    @Autowired
    public ReservationFacadeImpl(
            final UserService userService,
            final ResourceService resourceService,
            final ReservationService reservationService,
            final RequestValidator<GetReservationsRequest> getReservationsRequestValidator,
            final RequestValidator<PlaceReservationRequest> placeReservationRequestValidator,
            final RequestValidator<UpdateReservationRequest> updateReservationRequestValidator,
            final RequestValidator<AcceptReservationRequest> acceptReservationRequestValidator,
            final RequestValidator<ApproveReservationRequest> approveReservationRequestValidator,
            final RequestValidator<CancelReservationRequest> cancelReservationRequestValidator,
            final RequestValidator<ProposeNewTimeRequest> proposeNewTimeRequestValidator) {
        this.userService = userService;
        this.resourceService = resourceService;
        this.reservationService = reservationService;
        this.getReservationsRequestValidator = getReservationsRequestValidator;
        this.placeReservationRequestValidator = placeReservationRequestValidator;
        this.updateReservationRequestValidator = updateReservationRequestValidator;
        this.acceptReservationRequestValidator = acceptReservationRequestValidator;
        this.approveReservationRequestValidator = approveReservationRequestValidator;
        this.cancelReservationRequestValidator = cancelReservationRequestValidator;
        this.proposeNewTimeRequestValidator = proposeNewTimeRequestValidator;
    }

    @Override
    public GetReservationsResponse getReservations(final GetReservationsRequest request) {
        getReservationsRequestValidator.validate(request);
        final Collection<Reservation> matchedReservations = reservationService.getReservations(
                request.getPage(),
                request.getPageSize(),
                request.getSearchField(),
                request.getSearchType(),
                request.getSearchValue(),
                request.getSearchValueLowerBound(),
                request.getSearchValueUpperBound(),
                request.getSortingOrder(),
                request.getSortingField());
        final GetReservationsResponse response = new GetReservationsResponse();
        response.getReservationInfo().addAll(matchedReservations.stream().map(Reservation::toReservationInfo).collect(toList()));
        return response;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public PlaceReservationResponse placeReservation(final PlaceReservationRequest request) {
        placeReservationRequestValidator.validate(request);
        final User user = getCurrentUser();
        final Resource resource = resourceService.getResource(request.getResourceId()).orElseThrow(() ->
                raiseServiceFaultException(RESOURCE_DOES_NOT_EXIST, getNonexistentResourceIdMessage(request.getResourceId())));
        //noinspection ConstantConditions optional check made at validator
        final Reservation reservation = reservationService.placeReservation(
                user,
                resource,
                toLocalDateTime(request.getStartsAt()),
                toLocalDateTime(request.getEndsAt()),
                ReservationTypes.getById(request.getType()).get());
        final PlaceReservationResponse response = new PlaceReservationResponse();
        response.setReservationInfo(reservation.toReservationInfo());
        return response;
    }

    private User getCurrentUser() {
        final String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUser(username).orElseThrow(() ->
                raiseServiceFaultException(USER_DOES_NOT_EXIST, getNonexistentUsernameMessage(username)));
    }

    @Override
    public UpdateReservationResponse updateReservation(final UpdateReservationRequest request) {
        updateReservationRequestValidator.validate(request);
        //noinspection ReturnOfNull
        return null;
    }

    @Override
    public AcceptReservationResponse acceptReservation(final AcceptReservationRequest request) {
        acceptReservationRequestValidator.validate(request);
        //noinspection ReturnOfNull
        return null;
    }

    @Override
    public ApproveReservationResponse approveReservation(final ApproveReservationRequest request) {
        approveReservationRequestValidator.validate(request);
        //noinspection ReturnOfNull
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public CancelReservationResponse cancelReservation(final CancelReservationRequest request) {
        cancelReservationRequestValidator.validate(request);
        final User user = getCurrentUser();
        final Reservation reservation = reservationService.getReservation(request.getReservationId()).orElseThrow(() ->
                raiseServiceFaultException(RESERVATION_DOES_NOT_EXIST, getNonexistentReservationIdMessage(request.getReservationId())));

        checkSufficientPrivilegesToCancel(user, reservation);
        checkReservationIsNotCancelled(reservation);

        final Resource resource = resourceService.getResource(request.getResourceId()).orElseThrow(() ->
                raiseServiceFaultException(RESOURCE_DOES_NOT_EXIST, getNonexistentResourceIdMessage(request.getResourceId())));
        final ActualReservation actualReservation = reservationService.getActualReservation(request.getReservationId()).orElseThrow(() ->
                raiseServiceFaultException(RESERVATION_DOES_NOT_EXIST, getNonexistentReservationIdMessage(request.getReservationId())));
        //noinspection ConstantConditions optional check made at validator
        final ReservationType reservationType = ReservationTypes.getById(request.getType()).get();

        checkReservationIsTheSame(actualReservation, resource, toLocalDateTime(request.getStartsAt()),
                toLocalDateTime(request.getEndsAt()), reservationType);

        final Reservation result = reservationService.cancelReservation(reservation);
        final CancelReservationResponse response = new CancelReservationResponse();

        response.setReservationInfo(result.toReservationInfo());

        return response;
    }

    @Override
    public ProposeNewTimeResponse proposeNewTime(final ProposeNewTimeRequest request) {
        proposeNewTimeRequestValidator.validate(request);
        //noinspection ReturnOfNull
        return null;
    }

    private static void checkSufficientPrivilegesToCancel(final User user, final Reservation reservation) {
        if (reservation.getUser().getId() != user.getId() && user.getRole().equals(Roles.USER.getRole())) {
            throw raiseServiceFaultException(ACCESS_DENIED, getAccessDeniedMessage());
        }
    }

    private static void checkReservationIsNotCancelled(final Reservation reservation) {
        if (Objects.equals(Streams.findLast(reservation.getActions().stream())
                        .orElseThrow(RuntimeUtil::raiseUninitializedEntityField).getStatus(),
                ReservationStatuses.CANCELED.getReservationStatus())) {
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
}
