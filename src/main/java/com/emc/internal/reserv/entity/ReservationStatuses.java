package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.repository.ReservationStatusRepository;
import lombok.Getter;
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
public enum ReservationStatuses {
    APPROVED("Approved"),
    CANCELED("Canceled"),
    WAITING_FOR_APPROVAL("Waiting for approval"),
    NEW_TIME_PROPOSED("New time proposed");

    private final String name;
    @Getter
    private ReservationStatus reservationStatus;

    ReservationStatuses(final String name) {
        this.name = name;
    }

    @Component
    @SuppressWarnings("PublicInnerClass")
    public static class ReservationStatusRepositoryInjector {
        private final ReservationStatusRepository reservationStatusRepository;

        @Autowired
        public ReservationStatusRepositoryInjector(final ReservationStatusRepository reservationStatusRepository) {
            this.reservationStatusRepository = reservationStatusRepository;
        }

        @PostConstruct
        public void postConstruct() {
            for (ReservationStatuses status : EnumSet.allOf(ReservationStatuses.class)) {
                final Optional<ReservationStatus> optionalRow = reservationStatusRepository.findOneByName(status.name);
                status.reservationStatus = optionalRow.orElseThrow(() -> new ObjectNotFoundException(status.name, ReservationStatus.class.getSimpleName()));
            }
        }
    }
}
