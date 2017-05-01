package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.dto.ResourceInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author trofiv
 * @date 27.02.2017
 */
@Entity
@Getter
@ToString
@EqualsAndHashCode
@Access(AccessType.FIELD)
@Table(name = "resources")
@SuppressWarnings({"DuplicateStringLiteralInspection", "WeakerAccess"})
public class Resource {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;
    @Basic
    @Column(name = "name", nullable = false, length = 25)
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

    public ResourceInfo toResourceInfo() {
        final ResourceInfo info = new ResourceInfo();
        info.setId(this.id);
        info.setName(this.name);
        info.setLocation(this.location);
        return info;
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
