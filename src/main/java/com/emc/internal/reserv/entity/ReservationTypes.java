package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.repository.ReservationTypeRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public enum ReservationTypes {
    REGULAR,
    UNAVAILABLE;

    private static final Map<String, ReservationType> INDEX = new HashMap<>(ReservationTypes.values().length);
    @Getter
    private ReservationType reservationType;

    public static ReservationType getByName(final String name) {
        return Optional.ofNullable(INDEX.get(name)).orElseThrow(() ->
                raiseServiceFaultException(RESERVATION_TYPE_DOES_NOT_EXIST,
                        getInvalidReservationTypeMessage(name)));
    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public static boolean exists(final String name) {
        return INDEX.containsKey(name);
    }

    @Component
    @SuppressWarnings({"PublicInnerClass", "unused"})
    public static class ReservationTypeRepositoryInjector {
        private final ReservationTypeRepository reservationTypeRepository;

        @Autowired
        public ReservationTypeRepositoryInjector(final ReservationTypeRepository reservationTypeRepository) {
            this.reservationTypeRepository = reservationTypeRepository;
        }

        @PostConstruct
        public void postConstruct() {
            for (ReservationTypes type : EnumSet.allOf(ReservationTypes.class)) {
                final Optional<ReservationType> optionalRow = reservationTypeRepository.findOneByName(type.name());
                type.reservationType = optionalRow.orElseThrow(() -> new ObjectNotFoundException(type.name(), ReservationType.class.getSimpleName()));
                INDEX.put(type.name(), type.reservationType);
            }
        }
    }
}
