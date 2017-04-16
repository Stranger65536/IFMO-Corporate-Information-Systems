package com.emc.internal.reserv.facade;

import com.emc.internal.reserv.entity.Reservation;
import com.emc.internal.reserv.service.ReservationService;
import https.internal_emc_com.reserv_io.ws.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

/**
 * @author trofiv
 * @date 17.04.2017
 */
@Service
public class ReservationFacadeImpl implements ReservationFacade {
    private final ReservationService reservationService;

    @Autowired
    public ReservationFacadeImpl(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public GetReservationsResponse getReservations(final GetReservationsRequest request) {
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

    @Override
    public PlaceReservationResponse placeReservation(final PlaceReservationRequest request) {
        //TODO validate input
        //noinspection ReturnOfNull
        return null;
    }

    @Override
    public UpdateReservationResponse updateReservation(final UpdateReservationRequest request) {
        //TODO validate input
        return null;
    }

    @Override
    public AcceptReservationResponse acceptReservation(final AcceptReservationRequest request) {
        return null;
    }

    @Override
    public ProposeNewTimeResponse proposeNewTime(final ProposeNewTimeRequest request) {
        //TODO validate input
        return null;
    }
}
