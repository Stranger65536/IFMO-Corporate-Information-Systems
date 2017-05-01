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
import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.entity.ReservationTypes;
import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.service.ReservationService;
import com.emc.internal.reserv.service.ResourceService;
import com.emc.internal.reserv.service.UserService;
import com.emc.internal.reserv.validator.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.emc.internal.reserv.dto.FaultCode.RESOURCE_DOES_NOT_EXIST;
import static com.emc.internal.reserv.dto.FaultCode.USER_DOES_NOT_EXIST;
import static com.emc.internal.reserv.util.EndpointUtil.getNonexistentResourceIdMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getNonexistentUsernameMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.RuntimeUtil.toLocalDateTime;
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
    public PlaceReservationResponse placeReservation(final PlaceReservationRequest request) {
        placeReservationRequestValidator.validate(request);
        final String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final User user = userService.getUser(username).orElseThrow(() ->
                raiseServiceFaultException(USER_DOES_NOT_EXIST, getNonexistentUsernameMessage(username)));
        final Resource resource = resourceService.getResource(request.getResourceId()).orElseThrow(() ->
                raiseServiceFaultException(RESOURCE_DOES_NOT_EXIST, getNonexistentResourceIdMessage(request.getResourceId())));
        //noinspection ConstantConditions optional check made at validator
        final Reservation reservation = reservationService.placeReservation(
                user,
                resource,
                toLocalDateTime(request.getStartsAt()),
                toLocalDateTime(request.getEndsAt()),
                ReservationTypes.getById(request.getType()).get());

        return null;
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
    public CancelReservationResponse cancelReservation(final CancelReservationRequest request) {
        cancelReservationRequestValidator.validate(request);
        //noinspection ReturnOfNull
        return null;
    }

    @Override
    public ProposeNewTimeResponse proposeNewTime(final ProposeNewTimeRequest request) {
        proposeNewTimeRequestValidator.validate(request);
        //noinspection ReturnOfNull
        return null;
    }
}
