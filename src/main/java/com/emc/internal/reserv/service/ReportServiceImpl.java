package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.olap.OlapService;
import lombok.extern.log4j.Log4j2;
import mondrian.olap.MondrianException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.emc.internal.reserv.util.RuntimeUtil.enterMethodMessage;
import static com.emc.internal.reserv.util.RuntimeUtil.exitMethodMessage;
import static java.text.MessageFormat.format;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

/**
 * @author trofiv
 * @date 24.05.2017
 */
@Log4j2
@Service
public class ReportServiceImpl implements ReportService {
    private final OlapService olapService;

    @Autowired
    public ReportServiceImpl(final OlapService olapService) {
        this.olapService = olapService;
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public long getReservationsNumber(final User user, final Resource resource) {
        try {
            log.debug("{} " +
                            "user: {}," +
                            "resource: {}",
                    enterMethodMessage(), user, resource);

            final long result = ((Number) olapService.query(format("SELECT " +
                    "'{[Measures].[ReservationNumber]}' ON 0, " +
                    "'{[Users].[ID].&['{0}']}' ON 1, " +
                    "'{[Resources].[ID].&['{1}']}' ON 2 " +
                    "FROM [Reservations]", user.getId(), resource.getId())).getCell(new int[]{0, 0, 0}).getValue()).longValue();

            log.debug("{} ok, result: {}", exitMethodMessage(), result);
            return result;
        } catch (MondrianException ignored) {
            log.debug("{} exception, return 0", exitMethodMessage());
            return 0;
        }
    }
}
