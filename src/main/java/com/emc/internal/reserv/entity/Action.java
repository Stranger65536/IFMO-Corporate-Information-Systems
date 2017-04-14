package com.emc.internal.reserv.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author trofiv
 * @date 27.02.2017
 */
@Entity
@Getter
@EqualsAndHashCode
@Access(AccessType.FIELD)
@Table(name = "actions")
public class Action {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final long id;
    @Basic
    @Column(name = "time", nullable = false)
    private final Timestamp time;
    @Basic
    @Column(name = "reservation_start", nullable = false)
    private final Timestamp reservationStart;
    @Basic
    @Column(name = "reservation_end", nullable = false)
    private final Timestamp reservationEnd;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "user_id")
    private final User user;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "resource_id")
    private final Resource resource;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "type_id")
    private final ReservationType type;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "status_id")
    private final ActionStatus status;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "reservation_id")
    private final Reservation reservation;

    public Action() {
        id = 0;
        time = null;
        reservationStart = null;
        reservationEnd = null;
        user = null;
        resource = null;
        type = null;
        status = null;
        reservation = null;
    }

    public Action(final ReservationBuilder builder) {
        this.id = builder.id;
        this.time = builder.reservedAt;
        this.reservationStart = builder.reservationStart;
        this.reservationEnd = builder.reservationEnd;
        this.user = builder.user;
        this.resource = builder.resource;
        this.type = builder.type;
        this.status = builder.status;
        this.reservation = builder.reservation;
    }

    public ReservationBuilder builder() {
        return new ReservationBuilder(this);
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class ReservationBuilder {
        private long id;
        private Timestamp reservedAt;
        private Timestamp reservationStart;
        private Timestamp reservationEnd;
        private User user;
        private Resource resource;
        private ReservationType type;
        private ActionStatus status;
        private Reservation reservation;

        public ReservationBuilder(final Action model) {
            this.id = model.id;
            this.reservedAt = model.time;
            this.reservationStart = model.reservationStart;
            this.reservationEnd = model.reservationEnd;
            this.user = model.user;
            this.resource = model.resource;
            this.type = model.type;
            this.status = model.status;
            this.reservation = model.reservation;
        }

        public ReservationBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public ReservationBuilder reservedAt(final Timestamp reservedAt) {
            this.reservedAt = reservedAt;
            return this;
        }

        public ReservationBuilder reservationStart(final Timestamp reservationStart) {
            this.reservationStart = reservationStart;
            return this;
        }

        public ReservationBuilder reservationEnd(final Timestamp reservationEnd) {
            this.reservationEnd = reservationEnd;
            return this;
        }

        public ReservationBuilder user(final User user) {
            this.user = user;
            return this;
        }

        public ReservationBuilder resource(final Resource resource) {
            this.resource = resource;
            return this;
        }

        public ReservationBuilder type(final ReservationType type) {
            this.type = type;
            return this;
        }

        public ReservationBuilder status(final ActionStatus status) {
            this.status = status;
            return this;
        }

        public ReservationBuilder reservation(final Reservation reservation) {
            this.reservation = reservation;
            return this;
        }

        public Action build() {
            return new Action(this);
        }
    }
}
