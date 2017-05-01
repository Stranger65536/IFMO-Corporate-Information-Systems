package com.emc.internal.reserv.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author trofiv
 * @date 27.02.2017
 */
@Entity
@Getter
@ToString
@EqualsAndHashCode
@Access(AccessType.FIELD)
@Table(name = "actual_reservations")
@SuppressWarnings({"DuplicateStringLiteralInspection", "WeakerAccess"})
public class ActualReservation {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final long id;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "owner_id", nullable = false)
    private final User owner;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "last_action_user_id", nullable = false)
    private final User lastActionUser;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "resource_id", nullable = false)
    private final Resource resource;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "type_id", nullable = false)
    private final ReservationType type;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "status_id", nullable = false)
    private final ReservationStatus status;
    @Basic
    @Column(name = "reservation_start", nullable = false)
    private final Timestamp startsAt;
    @Basic
    @Column(name = "reservation_end", nullable = false)
    private final Timestamp endsAt;
    @Basic
    @Column(name = "created_at", nullable = false)
    private final Timestamp createdOn;
    @Basic
    @Column(name = "updated_at", nullable = false)
    private final Timestamp updatedOn;

    public ActualReservation() {
        id = 0;
        owner = null;
        lastActionUser = null;
        resource = null;
        type = null;
        status = null;
        startsAt = null;
        endsAt = null;
        createdOn = null;
        updatedOn = null;
    }
}
