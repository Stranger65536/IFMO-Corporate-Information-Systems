package com.emc.internal.reserv.facade;

import com.emc.internal.reserv.dto.GetReservationsReportRequest;
import com.emc.internal.reserv.dto.GetReservationsReportResponse;
import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.service.ReportService;
import com.emc.internal.reserv.service.ResourceService;
import com.emc.internal.reserv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * @author trofiv
 * @date 17.04.2017
 */
@Service
public class ReportFacadeImpl implements ReportFacade {
    private final UserService userService;
    private final ResourceService resourceService;
    private final ReportService reportService;

    @Autowired
    public ReportFacadeImpl(
            final UserService userService,
            final ResourceService resourceService,
            final ReportService reportService) {
        this.userService = userService;
        this.resourceService = resourceService;
        this.reportService = reportService;
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, propagation = REQUIRED)
    public GetReservationsReportResponse createReport(final GetReservationsReportRequest request) {
        final User user = userService.getUser(request.getUserId());
        final Resource resource = resourceService.getResource(request.getResourceId());
        final long result = reportService.getReservationsNumber(user, resource);
        final GetReservationsReportResponse response = new GetReservationsReportResponse();
        response.setReservationNumber(result);
        return response;
    }
}
