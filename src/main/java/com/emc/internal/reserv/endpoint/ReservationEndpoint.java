package com.emc.internal.reserv.endpoint;

import com.emc.internal.reserv.facade.ReservationFacade;
import https.internal_emc_com.reserv_io.ws.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import static com.emc.internal.reserv.endpoint.EndpointConstants.NAMESPACE_URI;

/**
 * @author trofiv
 * @date 12.04.2017
 */
@Log4j2
@Endpoint
public class ReservationEndpoint {
    private final ReservationFacade reservationFacade;

    @Autowired
    public ReservationEndpoint(final ReservationFacade reservationFacade) {
        this.reservationFacade = reservationFacade;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetReservationsRequest")
    @ResponsePayload
    public GetReservationsResponse getReservations(@RequestPayload final GetReservationsRequest request) {
        return reservationFacade.getReservations(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PlaceReservationRequest")
    @ResponsePayload
    public PlaceReservationResponse placeReservation(@RequestPayload final PlaceReservationRequest request) {
        return reservationFacade.placeReservation(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdateReservationRequest")
    @ResponsePayload
    public UpdateReservationResponse updateReservation(@RequestPayload final UpdateReservationRequest request) {
        return reservationFacade.updateReservation(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AcceptReservationRequest")
    @ResponsePayload
    public AcceptReservationResponse acceptReservation(@RequestPayload final AcceptReservationRequest request) {
        return reservationFacade.acceptReservation(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ApproveReservationRequest")
    @ResponsePayload
    public ApproveReservationResponse acceptReservation(@RequestPayload final ApproveReservationRequest request) {
        return reservationFacade.approveReservation(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CancelReservationRequest")
    @ResponsePayload
    public CancelReservationResponse cancelReservation(@RequestPayload final CancelReservationRequest request) {
        return reservationFacade.cancelReservation(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProposeNewTimeRequest")
    @ResponsePayload
    public ProposeNewTimeResponse proposeNewTime(@RequestPayload final ProposeNewTimeRequest request) {
        return reservationFacade.proposeNewTime(request);
    }
}
