package com.emc.internal.reserv.endpoint;

import com.emc.internal.reserv.dto.GetReservationsReportRequest;
import com.emc.internal.reserv.dto.GetReservationsReportResponse;
import com.emc.internal.reserv.facade.ReportFacade;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import static com.emc.internal.reserv.config.WebServiceConfig.REPORT_NAMESPACE_URI;

/**
 * @author trofiv
 * @date 12.04.2017
 */
@Log4j2
@Endpoint
public class ReportEndpoint {
    private final ReportFacade reportFacade;

    @Autowired
    public ReportEndpoint(final ReportFacade reportFacade) {
        this.reportFacade = reportFacade;
    }

    @PayloadRoot(namespace = REPORT_NAMESPACE_URI, localPart = "GetReservationsReportRequest")
    @ResponsePayload
    public GetReservationsReportResponse createResource(@RequestPayload final GetReservationsReportRequest request) {
        return reportFacade.createReport(request);
    }
}
