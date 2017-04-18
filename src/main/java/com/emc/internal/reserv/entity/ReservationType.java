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
@Table(name = "reservation_types")
@SuppressWarnings("WeakerAccess")
public class ReservationType {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;
    @Basic
    @Column(name = "name", nullable = false, length = 25, unique = true)
    private final String name;

    public ReservationType() {
        id = 0;
        name = null;
    }

    public ReservationType(final ReservationTypeBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public ReservationTypeBuilder builder() {
        return new ReservationTypeBuilder(this);
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class ReservationTypeBuilder {
        private int id;
        private String name;

        public ReservationTypeBuilder(final ReservationType model) {
            this.id = model.id;
            this.name = model.name;
        }

        public ReservationTypeBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public ReservationTypeBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public ReservationType build() {
            return new ReservationType(this);
        }
    }
}
