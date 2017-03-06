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
@Table(name = "resources")
public class Resource {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private final String name;
    @Basic
    @Column(name = "location", length = 45)
    private final String location;

    public Resource() {
        id = 0;
        name = null;
        location = null;
    }

    public Resource(final ResourceBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.location = builder.location;
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class ResourceBuilder {
        private int id;
        private String name;
        private String location;

        public ResourceBuilder(final Resource model) {
            this.id = model.id;
            this.name = model.name;
            this.location = model.location;
        }

        public ResourceBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public ResourceBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public ResourceBuilder location(final String location) {
            this.location = location;
            return this;
        }

        public Resource build() {
            return new Resource(this);
        }
    }
}
