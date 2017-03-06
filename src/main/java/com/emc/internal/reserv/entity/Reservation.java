package com.emc.internal.reserv.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@Table(name = "reservations")
public class Reservation {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final long id;
    @Basic
    @Column(name = "user_id", nullable = false)
    @SuppressWarnings("DuplicateStringLiteralInspection")
    private final int userId;
    @Basic
    @Column(name = "reserved_at", nullable = false)
    private final Timestamp reservedAt;
    @Basic
    @Column(name = "reservation_start", nullable = false)
    private final Timestamp reservationStart;
    @Basic
    @Column(name = "reservation_end", nullable = false)
    private final Timestamp reservationEnd;
    @Basic
    @Column(name = "last_modified", nullable = false)
    private final Timestamp lastModified;
    @Basic
    @Column(name = "deleted", nullable = false, columnDefinition = "BIT", length = 1)
    private final boolean deleted;
    @Basic
    @Column(name = "resource_id", nullable = false)
    private final int resourceId;

    public Reservation() {
        id = 0;
        userId = 0;
        reservedAt = null;
        reservationStart = null;
        reservationEnd = null;
        lastModified = null;
        deleted = false;
        resourceId = 0;
    }

    public Reservation(final ReservationBuilder builder) {
        this.id = builder.id;
        this.userId = builder.usersId;
        this.reservedAt = builder.reservedAt;
        this.reservationStart = builder.reservationStart;
        this.reservationEnd = builder.reservationEnd;
        this.lastModified = builder.lastModified;
        this.deleted = builder.deleted;
        this.resourceId = builder.resourceId;
    }

    public ReservationBuilder builder() {
        return new ReservationBuilder(this);
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class ReservationBuilder {
        private long id;
        private int usersId;
        private Timestamp reservedAt;
        private Timestamp reservationStart;
        private Timestamp reservationEnd;
        private Timestamp lastModified;
        private boolean deleted;
        private int resourceId;

        public ReservationBuilder(final Reservation model) {
            this.id = model.id;
            this.usersId = model.userId;
            this.reservedAt = model.reservedAt;
            this.reservationStart = model.reservationStart;
            this.reservationEnd = model.reservationEnd;
            this.lastModified = model.lastModified;
            this.deleted = model.deleted;
            this.resourceId = model.resourceId;
        }

        public ReservationBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public ReservationBuilder usersId(final int usersId) {
            this.usersId = usersId;
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

        public ReservationBuilder lastModified(final Timestamp lastModify) {
            this.lastModified = lastModify;
            return this;
        }

        public ReservationBuilder deleted(final boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public ReservationBuilder resourceId(final int resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Reservation build() {
            return new Reservation(this);
        }
    }
}
