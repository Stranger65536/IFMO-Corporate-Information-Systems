package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.Reservation;
import https.internal_emc_com.reserv_io.ws.ReservationSearchableField;
import https.internal_emc_com.reserv_io.ws.SearchType;
import https.internal_emc_com.reserv_io.ws.SortingOrder;

import java.util.Collection;

/**
 * @author trofiv
 * @date 13.04.2017
 */
public interface ReservationService {
    Collection<Reservation> getReservations(final int page,
                                            final int pageSize,
                                            final ReservationSearchableField searchField,
                                            final SearchType searchType,
                                            final String searchValue,
                                            final String searchValueLowerBound,
                                            final String searchValueUpperBound,
                                            final SortingOrder sortingOrder,
                                            final ReservationSearchableField sortingField);
}
