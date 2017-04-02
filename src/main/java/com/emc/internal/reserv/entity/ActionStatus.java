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
@Table(name = "action_statuses")
public class ActionStatus {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;
    @Basic
    @Column(name = "name", nullable = false, length = 25, unique = true)
    private final String name;

    public ActionStatus() {
        id = 0;
        name = null;
    }

    public ActionStatus(final ActionStatusBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public ActionStatusBuilder builder() {
        return new ActionStatusBuilder(this);
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class ActionStatusBuilder {
        private int id;
        private String name;

        public ActionStatusBuilder(final ActionStatus model) {
            this.id = model.id;
            this.name = model.name;
        }

        public ActionStatusBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public ActionStatusBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public ActionStatus build() {
            return new ActionStatus(this);
        }
    }
}
