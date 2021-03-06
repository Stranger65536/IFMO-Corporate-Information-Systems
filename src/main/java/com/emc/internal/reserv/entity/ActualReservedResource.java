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
@Table(name = "actual_reserved_resources")
@SuppressWarnings({"DuplicateStringLiteralInspection", "WeakerAccess"})
public class ActualReservedResource {
    @Id
    @Column(name = "id", nullable = false)
    private final long id;
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

    public ActualReservedResource() {
        id = 0;
        resource = null;
        type = null;
        status = null;
        startsAt = null;
        endsAt = null;
    }
}
