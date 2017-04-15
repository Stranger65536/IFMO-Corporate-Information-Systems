package com.emc.internal.reserv.facade;

import https.internal_emc_com.reserv_io.ws.*;

/**
 * @author trofiv
 * @date 15.04.2017
 */
public interface UserFacade {
    void registerUser(final RegistrationRequest request);

    GetUserResponse getUser(final GetUserRequest request);

    GetUsersResponse getUsers(final GetUsersRequest request);
}
