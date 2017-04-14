package com.emc.internal.reserv.endpoint;

import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.service.ReservationService;
import https.internal_emc_com.reserv_io.ws.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Collection;

import static com.emc.internal.reserv.endpoint.EndpointConstants.NAMESPACE_URI;
import static java.util.stream.Collectors.toList;

/**
 * @author trofiv
 * @date 12.04.2017
 */
@Log4j2
@Endpoint
public class ReservationEndpoint {
    private final ReservationService reservationService;

    @Autowired
    public ReservationEndpoint(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetReservationsRequest")
    @ResponsePayload
    public GetReservationsResponse getReservations(@RequestPayload final GetReservationsRequest request) {
        //TODO validate input
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PlaceReservationRequest")
    @ResponsePayload
    public PlaceReservationResponse placeReservation(@RequestPayload final PlaceReservationRequest request) {
        //TODO validate input
        return null;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdateReservationRequest")
    @ResponsePayload
    public void updateReservation(@RequestPayload final UpdateReservationRequest request) {
        //TODO validate input
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AcceptReservationRequest")
    @ResponsePayload
    public void acceptReservation(@RequestPayload final AcceptReservationRequest request) {
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProposeNewTimeRequest")
    @ResponsePayload
    public void proposeNewTime(@RequestPayload final ProposeNewTimeRequest request) {
        //TODO validate input
    }
}
