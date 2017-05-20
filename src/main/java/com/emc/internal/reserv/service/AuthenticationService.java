package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.User;

/**
 * @author trofiv
 * @date 19.05.2017
 */
public interface AuthenticationService {
    User getCurrentUser();
}
