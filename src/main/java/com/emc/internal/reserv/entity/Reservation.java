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
    @Column(name = "deleted", nullable = false, columnDefinition = "BIT", length = 1)
    private final boolean deleted;

    public Reservation() {
        id = 0;
        userId = 0;
        deleted = false;
    }

    public Reservation(final ReservationBuilder builder) {
        this.id = builder.id;
        this.userId = builder.usersId;
        this.deleted = builder.deleted;
    }

    public ReservationBuilder builder() {
        return new ReservationBuilder(this);
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class ReservationBuilder {
        private long id;
        private int usersId;
        private boolean deleted;

        public ReservationBuilder(final Reservation model) {
            this.id = model.id;
            this.usersId = model.userId;
            this.deleted = model.deleted;
        }

        public ReservationBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public ReservationBuilder usersId(final int usersId) {
            this.usersId = usersId;
            return this;
        }

        public ReservationBuilder deleted(final boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Reservation build() {
            return new Reservation(this);
        }
    }
}
