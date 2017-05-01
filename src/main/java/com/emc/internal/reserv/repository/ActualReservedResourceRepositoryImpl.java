package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.ReservationType;
import com.emc.internal.reserv.entity.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.sql.Timestamp;

/**
 * @author trofiv
 * @date 30.04.2017
 */
@Service
public class ActualReservedResourceRepositoryImpl implements ActualReservedResourceRepository {
    private static final String RESOURCE = "RESOURCE";
    private static final String RESERVATION_TYPE = "RESERVATION_TYPE";
    private static final String STARTS_AT = "STARTS_AT";
    private static final String ENDS_AT = "ENDS_AT";
    private static final String RESULT = "RESULT";
    private final EntityManager entityManager;

    @Autowired
    public ActualReservedResourceRepositoryImpl(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean hasOverlappingReservation(
            final Resource resource,
            final ReservationType reservationType,
            final Timestamp start,
            final Timestamp end) {
        final StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("HAS_OVERLAPPING_RESERVATION_WITH_TYPE")
                .registerStoredProcedureParameter(RESOURCE, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(RESERVATION_TYPE, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(STARTS_AT, Timestamp.class, ParameterMode.IN)
                .registerStoredProcedureParameter(ENDS_AT, Timestamp.class, ParameterMode.IN)
                .registerStoredProcedureParameter(RESULT, Boolean.class, ParameterMode.OUT)
                .setParameter(RESOURCE, resource.getId())
                .setParameter(RESERVATION_TYPE, reservationType.getId())
                .setParameter(STARTS_AT, start)
                .setParameter(ENDS_AT, end);
        query.execute();
        return (boolean) query.getOutputParameterValue(RESULT);
    }

    @Override
    public boolean hasOverlappingReservation(
            final Resource resource,
            final Timestamp start,
            final Timestamp end) {
        final StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("HAS_OVERLAPPING_RESERVATION")
                .registerStoredProcedureParameter(RESOURCE, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(STARTS_AT, Timestamp.class, ParameterMode.IN)
                .registerStoredProcedureParameter(ENDS_AT, Timestamp.class, ParameterMode.IN)
                .registerStoredProcedureParameter(RESULT, Boolean.class, ParameterMode.OUT)
                .setParameter(RESOURCE, resource)
                .setParameter(STARTS_AT, start)
                .setParameter(ENDS_AT, end);
        query.execute();
        return (boolean) query.getOutputParameterValue(RESULT);
    }
}
