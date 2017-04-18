package com.emc.internal.reserv.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * @author trofiv
 * @date 27.02.2017
 */
@Entity
@Getter
@EqualsAndHashCode
@Access(AccessType.FIELD)
@Table(name = "roles")
@SuppressWarnings("WeakerAccess")
public class Role implements GrantedAuthority {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private final String name;

    public Role() {
        id = 0;
        name = null;
    }

    public Role(final RoleBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public RoleBuilder builder() {
        return new RoleBuilder(this);
    }

    @Override
    @SuppressWarnings("SuspiciousGetterSetter")
    public String getAuthority() {
        return name;
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class RoleBuilder {
        private int id;
        private String name;

        public RoleBuilder(final Role model) {
            this.id = model.id;
            this.name = model.name;
        }

        public RoleBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public RoleBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public Role build() {
            return new Role(this);
        }
    }
}
