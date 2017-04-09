package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.repository.ReservationTypeRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
                type.reservationType = optionalRow.orElseThrow(() -> new ObjectNotFoundException(type.name, ReservationType.class.getSimpleName()));
            }
        }
    }
}
