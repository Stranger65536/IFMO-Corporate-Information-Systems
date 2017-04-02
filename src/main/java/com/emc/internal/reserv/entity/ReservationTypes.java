package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.repository.ReservationTypeRepository;
import com.emc.internal.reserv.repository.RoleRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;
import java.util.Optional;

/**
 * @author trofiv
 * @date 03.04.2017
 */
@Log4j2
public enum ReservationTypes {
    REGULAR("Regular"),
    UNAVAILABLE("Unavailable");

    private final String name;
    @Getter
    private ReservationType reservationType;

    ReservationTypes(final String name) {
        this.name = name;
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
                if (optionalRow.isPresent()) {
                    type.reservationType = optionalRow.get();
                } else {
                    log.error("Reservation type with name '{}' has not been found!", type.name);
                    throw new ObjectNotFoundException(type.name, ReservationType.class.getSimpleName());
                }
            }
        }
    }
}
