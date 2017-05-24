package com.emc.internal.reserv.facade;

import com.emc.internal.reserv.dto.GetReservationsReportRequest;
import com.emc.internal.reserv.dto.GetReservationsReportResponse;

/**
 * @author trofiv
 * @date 17.04.2017
 */
public interface ReportFacade {
    GetReservationsReportResponse createReport(final GetReservationsReportRequest request);
}
