package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.repository.ReservationStatusRepository;
import lombok.Getter;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.emc.internal.reserv.dto.FaultCode.RESERVATION_TYPE_DOES_NOT_EXIST;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidReservationTypeMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;

/**
 * @author trofiv
 * @date 03.04.2017
 */
public enum ReservationStatuses {
    APPROVED,
    CANCELED,
    WAITING_FOR_APPROVAL,
    NEW_TIME_PROPOSED;

    private static final Map<String, ReservationStatus> INDEX = new HashMap<>(ReservationStatuses.values().length);
    @Getter
    private ReservationStatus reservationStatus;

    public static ReservationStatus getByName(final String name) {
        return Optional.ofNullable(INDEX.get(name)).orElseThrow(() ->
                raiseServiceFaultException(RESERVATION_TYPE_DOES_NOT_EXIST,
                        getInvalidReservationTypeMessage(name)));
    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public static boolean exists(final String name) {
        try {
            return INDEX.containsKey(name);
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Component
    @SuppressWarnings({"PublicInnerClass", "unused"})
    public static class ReservationStatusRepositoryInjector {
        private final ReservationStatusRepository reservationStatusRepository;

        @Autowired
        public ReservationStatusRepositoryInjector(final ReservationStatusRepository reservationStatusRepository) {
            this.reservationStatusRepository = reservationStatusRepository;
        }

        @PostConstruct
        public void postConstruct() {
            for (ReservationStatuses status : EnumSet.allOf(ReservationStatuses.class)) {
                final Optional<ReservationStatus> optionalRow = reservationStatusRepository.findOneByName(status.name());
                status.reservationStatus = optionalRow.orElseThrow(() -> new ObjectNotFoundException(status.name(), ReservationStatus.class.getSimpleName()));
                INDEX.put(status.name(), status.reservationStatus);
            }
        }
    }
}
