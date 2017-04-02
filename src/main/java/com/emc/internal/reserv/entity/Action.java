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
    @Basic
    @Column(name = "user_id", nullable = false)
    @SuppressWarnings("DuplicateStringLiteralInspection")
    private final int userId;
    @Basic
    @Column(name = "resource_id", nullable = false)
    private final int resourceId;
    @Basic
    @Column(name = "type_id", nullable = false)
    private final int typeId;
    @Basic
    @Column(name = "status_id", nullable = false)
    private final int statusId;
    @Basic
    @Column(name = "reservation_id", nullable = false)
    private final long reservationId;

    public Action() {
        id = 0;
        time = null;
        reservationStart = null;
        reservationEnd = null;
        userId = 0;
        resourceId = 0;
        typeId = 0;
        statusId = 0;
        reservationId = 0;
    }

    public Action(final ReservationBuilder builder) {
        this.id = builder.id;
        this.time = builder.reservedAt;
        this.reservationStart = builder.reservationStart;
        this.reservationEnd = builder.reservationEnd;
        this.userId = builder.usersId;
        this.resourceId = builder.resourceId;
        this.typeId = builder.typeId;
        this.statusId = builder.statusId;
        this.reservationId = builder.reservationId;
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
        private int usersId;
        private int resourceId;
        private int typeId;
        private int statusId;
        private long reservationId;

        public ReservationBuilder(final Action model) {
            this.id = model.id;
            this.reservedAt = model.time;
            this.reservationStart = model.reservationStart;
            this.reservationEnd = model.reservationEnd;
            this.usersId = model.userId;
            this.resourceId = model.resourceId;
            this.typeId = model.typeId;
            this.statusId = model.statusId;
            this.reservationId = model.reservationId;
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

        public ReservationBuilder usersId(final int usersId) {
            this.usersId = usersId;
            return this;
        }

        public ReservationBuilder resourceId(final int resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public ReservationBuilder typeId(final int typeId) {
            this.typeId = typeId;
            return this;
        }

        public ReservationBuilder statusId(final int statusId) {
            this.statusId = statusId;
            return this;
        }

        public ReservationBuilder reservationId(final int reservationId) {
            this.reservationId = reservationId;
            return this;
        }

        public Action build() {
            return new Action(this);
        }
    }
}
