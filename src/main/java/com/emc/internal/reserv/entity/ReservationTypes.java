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

/**
 * @author trofiv
 * @date 03.04.2017
 */
@Log4j2
public enum ReservationTypes {
    REGULAR("Regular"),
    UNAVAILABLE("Unavailable");

    private static final Map<Integer, ReservationType> INDEX = new HashMap<>(ReservationTypes.values().length);
    private final String name;
    @Getter
    private ReservationType reservationType;

    ReservationTypes(final String name) {
        this.name = name;
    }

    public static Optional<ReservationType> getById(final int id) {
        return Optional.ofNullable(INDEX.get(id));
    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public static boolean exists(final String id) {
        try {
            return INDEX.containsKey(Integer.parseInt(id));
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Component
    @SuppressWarnings("PublicInnerClass")
    public static class ReservationTypeRepositoryInjector {
        private final ReservationTypeRepository reservationTypeRepository;

        @Autowired
        public ReservationTypeRepositoryInjector(final ReservationTypeRepository reservationTypeRepository) {
            this.reservationTypeRepository = reservationTypeRepository;
        }

        @PostConstruct
        public void postConstruct() {
            for (ReservationTypes type : EnumSet.allOf(ReservationTypes.class)) {
                final Optional<ReservationType> optionalRow = reservationTypeRepository.findOneByName(type.name);
                type.reservationType = optionalRow.orElseThrow(() -> new ObjectNotFoundException(type.name, ReservationType.class.getSimpleName()));
                INDEX.put(type.reservationType.getId(), type.reservationType);
            }
        }
    }
}
