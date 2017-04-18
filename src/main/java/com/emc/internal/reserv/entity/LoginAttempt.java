package com.emc.internal.reserv.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author trofiv
 * @date 27.02.2017
 */
@Entity
@Getter
@EqualsAndHashCode
@Access(AccessType.FIELD)
@Table(name = "login_attempts")
@SuppressWarnings({"DuplicateStringLiteralInspection", "WeakerAccess"})
public class LoginAttempt {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final long id;
    @Basic
    @Column(name = "ip_address", nullable = false, length = 45)
    private final String ipAddress;
    @Basic
    @Column(name = "login_time", nullable = false)
    private final Timestamp loginTime;
    @Basic
    @Column(name = "successful", nullable = false, columnDefinition = "BIT", length = 1)
    private final boolean successful;
    @Basic
    @Column(name = "user_id", nullable = false)
    private final int userId;

    public LoginAttempt() {
        id = 0;
        ipAddress = null;
        loginTime = null;
        successful = false;
        userId = 0;
    }

    public LoginAttempt(final LoginAttemptBuilder builder) {
        this.id = builder.id;
        this.ipAddress = builder.ipAddress;
        this.loginTime = builder.loginTime;
        this.successful = builder.successful;
        this.userId = builder.userId;
    }

    public LoginAttemptBuilder builder() {
        return new LoginAttemptBuilder(this);
    }

    @NoArgsConstructor
    @SuppressWarnings("PublicInnerClass")
    public static class LoginAttemptBuilder {
        private long id;
        private String ipAddress;
        private Timestamp loginTime;
        private boolean successful;
        private int userId;

        public LoginAttemptBuilder(final LoginAttempt model) {
            this.id = model.id;
            this.ipAddress = model.ipAddress;
            this.loginTime = model.loginTime;
            this.successful = model.successful;
            this.userId = model.userId;
        }

        public LoginAttemptBuilder id(final int id) {
            this.id = id;
            return this;
        }

        public LoginAttemptBuilder ipAddress(final String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public LoginAttemptBuilder loginTime(final Timestamp loginTime) {
            this.loginTime = loginTime;
            return this;
        }

        public LoginAttemptBuilder successful(final boolean successful) {
            this.successful = successful;
            return this;
        }

        public LoginAttemptBuilder userId(final int userId) {
            this.userId = userId;
            return this;
        }

        public LoginAttempt build() {
            return new LoginAttempt(this);
        }
    }
}
