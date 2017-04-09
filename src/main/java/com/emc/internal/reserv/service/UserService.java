package com.emc.internal.reserv.service;

/**
 * @author trofiv
 * @date 04.04.2017
 */
public interface UserService {
    int createUser(final String login, final String email, final String password);
}
