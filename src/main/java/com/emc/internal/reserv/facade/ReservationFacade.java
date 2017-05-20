package com.emc.internal.reserv.facade;

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
import com.emc.internal.reserv.dto.UpdateReservationRequest;
import com.emc.internal.reserv.dto.UpdateReservationResponse;

/**
 * @author trofiv
 * @date 17.04.2017
 */
public interface ReservationFacade {
    GetReservationResponse getReservation(GetReservationRequest request);

    GetReservationsResponse getReservations(final GetReservationsRequest request);

    PlaceReservationResponse placeReservation(final PlaceReservationRequest request);

    UpdateReservationResponse updateReservation(final UpdateReservationRequest request);

    ApproveReservationResponse approveReservation(final ApproveReservationRequest request);

    CancelReservationResponse cancelReservation(final CancelReservationRequest request);

    ProposeNewTimeResponse proposeNewTime(final ProposeNewTimeRequest request);
}
