package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.dto.UserInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
import javax.persistence.UniqueConstraint;

/**
 * @author trofiv
 * @date 27.02.2017
 */
@Entity
@Getter
@ToString(exclude = "password")
@EqualsAndHashCode
@Access(AccessType.FIELD)
@SuppressWarnings({"DuplicateStringLiteralInspection", "WeakerAccess"})
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;
    @Basic
    @Column(name = "first_name", length = 35)
    private final String firstName;
    @Basic
    @Column(name = "last_name", length = 35)
    private final String lastName;
    @Basic
    @Column(name = "middle_name", length = 35)
    private final String middleName;
    @Basic
    @Column(name = "username", nullable = false, unique = true, length = 25)
    private final String username;
    @Basic
    @Column(name = "email", nullable = false, unique = true, length = 254)
    private final String email;
    @Basic
    @Column(name = "password", nullable = false, length = 128)
    private final String password;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "role_id")
    private final Role role;

    public User() {
        id = 0;
        firstName = null;
        lastName = null;
        middleName = null;
        username = null;
        email = null;
        password = null;
        role = null;
    }

    public User(final UserBuilder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.middleName = builder.middleName;
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.role = builder.role;
    }

    public UserBuilder builder() {
        return new UserBuilder(this);
    }

    public UserInfo toUserInfo() {
        final UserInfo info = new UserInfo();
        info.setId(this.id);
        info.setUsername(this.username);
        info.setEmail(this.email);
        info.setFirstName(this.firstName);
        info.setLastName(this.lastName);
        info.setMiddleName(this.middleName);
        info.setRole(this.role == null
                ? null
                : com.emc.internal.reserv.dto.Role.fromValue(this.role.getName()));
        return info;
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class UserBuilder {
        private int id;
        private String firstName;
        private String lastName;
        private String middleName;
        private String username;
        private String email;
        private String password;
        private Role role;

        public UserBuilder(final User model) {
            this.id = model.id;
            this.firstName = model.firstName;
            this.lastName = model.lastName;
            this.middleName = model.middleName;
            this.username = model.username;
            this.email = model.email;
            this.password = model.password;
            this.role = model.role;
        }

        public UserBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public UserBuilder firstName(final String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(final String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder middleName(final String middleName) {
            this.middleName = middleName;
            return this;
        }

        public UserBuilder username(final String login) {
            this.username = login;
            return this;
        }

        public UserBuilder email(final String email) {
            this.email = email;
            return this;
        }

        public UserBuilder password(final String password) {
            this.password = password;
            return this;
        }

        public UserBuilder role(final Role role) {
            this.role = role;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
