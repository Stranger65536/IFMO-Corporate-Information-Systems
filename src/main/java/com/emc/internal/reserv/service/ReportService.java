package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.entity.User;

/**
 * @author trofiv
 * @date 24.05.2017
 */
public interface ReportService {
    long getReservationsNumber(final User user, final Resource resource);
}
