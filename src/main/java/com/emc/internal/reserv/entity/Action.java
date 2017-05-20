package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.dto.ActionInfo;
import com.emc.internal.reserv.util.RuntimeUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

import static com.emc.internal.reserv.util.RuntimeUtil.toCalendar;
import static java.util.Optional.ofNullable;

/**
 * @author trofiv
 * @date 27.02.2017
 */
@Entity
@Getter
@ToString(exclude = "reservation")
@EqualsAndHashCode
@Access(AccessType.FIELD)
@Table(name = "actions")
@SuppressWarnings({"DuplicateStringLiteralInspection", "WeakerAccess"})
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
    private final ReservationStatus status;
    @ManyToOne(cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "reservation_id")
    private final Reservation reservation;

    @SuppressWarnings("unused") //used by hibernate
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

    public Action(final ActionBuilder builder) {
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

    public ActionBuilder builder() {
        return new ActionBuilder(this);
    }

    public ActionInfo toActionInfo() {
        final ActionInfo info = new ActionInfo();
        info.setId(this.id);
        info.setTime(toCalendar(ofNullable(this.time).orElseThrow(RuntimeUtil::raiseUninitializedEntityField)));
        info.setStartsAt(toCalendar(ofNullable(this.reservationStart).orElseThrow(RuntimeUtil::raiseUninitializedEntityField)));
        info.setEndsAt(toCalendar(ofNullable(this.reservationEnd).orElseThrow(RuntimeUtil::raiseUninitializedEntityField)));
        info.setUserId(ofNullable(this.user).orElseThrow(RuntimeUtil::raiseUninitializedEntityField).getId());
        info.setResourceId(ofNullable(this.resource).orElseThrow(RuntimeUtil::raiseUninitializedEntityField).getId());
        info.setType(ofNullable(this.type).orElseThrow(RuntimeUtil::raiseUninitializedEntityField).toType());
        info.setStatus(ofNullable(this.status).orElseThrow(RuntimeUtil::raiseUninitializedEntityField).toStatus());
        info.setReservationId(ofNullable(this.reservation).orElseThrow(RuntimeUtil::raiseUninitializedEntityField).getId());
        return info;
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class ActionBuilder {
        private long id;
        private Timestamp reservedAt;
        private Timestamp reservationStart;
        private Timestamp reservationEnd;
        private User user;
        private Resource resource;
        private ReservationType type;
        private ReservationStatus status;
        private Reservation reservation;

        public ActionBuilder(final Action model) {
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

        public ActionBuilder id(final long id) {
            this.id = id;
            return this;
        }

        public ActionBuilder reservedAt(final Timestamp reservedAt) {
            this.reservedAt = reservedAt;
            return this;
        }

        public ActionBuilder reservationStart(final Timestamp reservationStart) {
            this.reservationStart = reservationStart;
            return this;
        }

        public ActionBuilder reservationEnd(final Timestamp reservationEnd) {
            this.reservationEnd = reservationEnd;
            return this;
        }

        public ActionBuilder user(final User user) {
            this.user = user;
            return this;
        }

        public ActionBuilder resource(final Resource resource) {
            this.resource = resource;
            return this;
        }

        public ActionBuilder type(final ReservationType type) {
            this.type = type;
            return this;
        }

        public ActionBuilder status(final ReservationStatus status) {
            this.status = status;
            return this;
        }

        public ActionBuilder reservation(final Reservation reservation) {
            this.reservation = reservation;
            return this;
        }

        public Action build() {
            return new Action(this);
        }
    }
}
