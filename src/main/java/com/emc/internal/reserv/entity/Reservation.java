package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.dto.ReservationInfo;
import com.emc.internal.reserv.util.RuntimeUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SortNatural;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

/**
 * @author trofiv
 * @date 27.02.2017
 */
@Entity
@Getter
@ToString
@EqualsAndHashCode
@Access(AccessType.FIELD)
@Table(name = "reservations")
@SuppressWarnings({"DuplicateStringLiteralInspection", "WeakerAccess"})
public class Reservation {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final long id;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "user_id", nullable = false)
    private final User user;
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    @OrderBy("id ASC")
    @SortNatural
    private final Collection<Action> actions;

    @SuppressWarnings("unused") //used by hibernate
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
        info.setUserId(ofNullable(this.user).orElseThrow(RuntimeUtil::raiseUninitializedEntityField).getId());
        info.getActionInfo().addAll(ofNullable(this.actions).orElseThrow(RuntimeUtil::raiseUninitializedEntityField)
                .stream().map(Action::toActionInfo).collect(toList()));
        return info;
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class ReservationBuilder {
        private long id;
        private User user;
        private Collection<Action> actions = new ArrayList<>();

        public ReservationBuilder(final Reservation model) {
            this.id = model.id;
            this.user = model.user;
            this.actions = model.actions;
        }

        public ReservationBuilder id(final long id) {
            this.id = id;
            return this;
        }

        public ReservationBuilder user(final User user) {
            this.user = user;
            return this;
        }

        @SuppressWarnings("unused") //sometimes it will be used
        public ReservationBuilder actions(final Collection<Action> actions) {
            this.actions = actions;
            return this;
        }

        public Reservation build() {
            return new Reservation(this);
        }
    }
}
