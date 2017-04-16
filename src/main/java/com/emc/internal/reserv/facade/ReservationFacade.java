package com.emc.internal.reserv.facade;

import https.internal_emc_com.reserv_io.ws.*;

/**
 * @author trofiv
 * @date 17.04.2017
 */
public interface ReservationFacade {
    GetReservationsResponse getReservations(final GetReservationsRequest request);

    PlaceReservationResponse placeReservation(final PlaceReservationRequest request);

    UpdateReservationResponse updateReservation(final UpdateReservationRequest request);

    AcceptReservationResponse acceptReservation(final AcceptReservationRequest request);

    ProposeNewTimeResponse proposeNewTime(final ProposeNewTimeRequest request);
}
