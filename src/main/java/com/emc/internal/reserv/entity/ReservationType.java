package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.dto.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import static com.emc.internal.reserv.util.RuntimeUtil.raiseEnumValueNotMapped;

/**
 * @author trofiv
 * @date 27.02.2017
 */
@Entity
@Getter
@ToString
@EqualsAndHashCode
@Access(AccessType.FIELD)
@Table(name = "reservation_types")
@SuppressWarnings("WeakerAccess")
public class ReservationType {
    @Id
    @Column(name = "id", nullable = false)
    private final int id;
    @Basic
    @Column(name = "name", nullable = false, length = 25, unique = true)
    private final String name;

    @SuppressWarnings("unused")
    private ReservationType() {
        id = 0;
        name = null;
    }

    public ReservationType(final ReservationTypeBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public Type toType() {
        try {
            return Type.valueOf(this.name);
        } catch (IllegalArgumentException ignored) {
            throw raiseEnumValueNotMapped(this.name);
        }
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
