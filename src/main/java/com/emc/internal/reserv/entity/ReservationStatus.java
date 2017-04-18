package com.emc.internal.reserv.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author trofiv
 * @date 27.02.2017
 */
@Entity
@Getter
@EqualsAndHashCode
@Access(AccessType.FIELD)
@Table(name = "reservation_statuses")
@SuppressWarnings("WeakerAccess")
public class ReservationStatus {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;
    @Basic
    @Column(name = "name", nullable = false, length = 25, unique = true)
    private final String name;

    public ReservationStatus() {
        id = 0;
        name = null;
    }

    public ReservationStatus(final ReservationStatusBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public ReservationStatusBuilder builder() {
        return new ReservationStatusBuilder(this);
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class ReservationStatusBuilder {
        private int id;
        private String name;

        public ReservationStatusBuilder(final ReservationStatus model) {
            this.id = model.id;
            this.name = model.name;
        }

        public ReservationStatusBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public ReservationStatusBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public ReservationStatus build() {
            return new ReservationStatus(this);
        }
    }
}
