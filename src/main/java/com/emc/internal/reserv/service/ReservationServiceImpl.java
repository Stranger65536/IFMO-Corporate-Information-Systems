package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.Reservation;
import https.internal_emc_com.reserv_io.ws.ReservationField;
import https.internal_emc_com.reserv_io.ws.SearchType;
import https.internal_emc_com.reserv_io.ws.SortingOrder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * @author trofiv
 * @date 13.04.2017
 */
@Service
public class ReservationServiceImpl implements ReservationService {
    @Override
    public Collection<Reservation> getReservations(
            final int page,
            final int pageSize,
            final ReservationField searchField,
            final SearchType searchType,
            final String searchValue,
            final String searchValueLowerBound,
            final String searchValueUpperBound,
            final SortingOrder sortingOrder,
            final ReservationField sortingField) {
        //TODO
        return Collections.emptyList();
    }
}
