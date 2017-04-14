package com.emc.internal.reserv.entity;

import https.internal_emc_com.reserv_io.ws.ReservationInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Collection;

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
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "user_id")
    private final User user;
    @OneToMany
    @BatchSize(size = 10)
    @JoinColumn(name = "id")
    private final Collection<Action> actions;

    public Reservation() {
        id = 0;
        user = null;
        actions = null;
    }

    public Reservation(final ReservationBuilder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.actions = builder.actions;
    }

    public ReservationBuilder builder() {
        return new ReservationBuilder(this);
    }

    public ReservationInfo toReservationInfo() {
        final ReservationInfo info = new ReservationInfo();
        info.setId(this.id);
        //TODO fill
        return info;
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class ReservationBuilder {
        private long id;
        private User user;
        private Collection<Action> actions;

        public ReservationBuilder(final Reservation model) {
            this.id = model.id;
            this.user = model.user;
            this.actions = model.actions;
        }

        public ReservationBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public ReservationBuilder usersId(final User user) {
            this.user = user;
            return this;
        }

        public ReservationBuilder actions(final Collection<Action> actions) {
            this.actions = actions;
            return this;
        }

        public Reservation build() {
            return new Reservation(this);
        }
    }
}
