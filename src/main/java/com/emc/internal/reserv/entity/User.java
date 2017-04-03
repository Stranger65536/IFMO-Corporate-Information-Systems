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
@SuppressWarnings("DuplicateStringLiteralInspection")
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "login"))
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
    @Column(name = "login", nullable = false, unique = true, length = 25)
    private final String login;
    @Basic
    @Column(name = "email", nullable = false, unique = true, length = 25)
    private final String email;
    @Basic
    @Column(name = "password", nullable = false, length = 128)
    private final String password;
    @ManyToOne
    private final Role role;

    public User() {
        id = 0;
        firstName = null;
        lastName = null;
        middleName = null;
        login = null;
        email = null;
        password = null;
        role = null;
    }

    public User(final UserBuilder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.middleName = builder.middleName;
        this.login = builder.login;
        this.email = builder.email;
        this.password = builder.password;
        role = null;
    }

    public UserBuilder builder() {
        return new UserBuilder(this);
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class UserBuilder {
        private int id;
        private String firstName;
        private String lastName;
        private String middleName;
        private String login;
        private String email;
        private String password;

        public UserBuilder(final User model) {
            this.id = model.id;
            this.firstName = model.firstName;
            this.lastName = model.lastName;
            this.middleName = model.middleName;
            this.login = model.login;
            this.email = model.email;
            this.password = model.password;
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

        public UserBuilder login(final String login) {
            this.login = login;
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

        public User build() {
            return new User(this);
        }
    }
}
