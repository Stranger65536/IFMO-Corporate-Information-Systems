package com.emc.internal.reserv.facade;

import com.emc.internal.reserv.dto.GetUserRequest;
import com.emc.internal.reserv.dto.GetUserResponse;
import com.emc.internal.reserv.dto.GetUsersRequest;
import com.emc.internal.reserv.dto.GetUsersResponse;
import com.emc.internal.reserv.dto.RegistrationRequest;

/**
 * @author trofiv
 * @date 15.04.2017
 */
public interface UserFacade {
    void registerUser(final RegistrationRequest request);

    GetUserResponse getUser(final GetUserRequest request);

    GetUsersResponse getUsers(final GetUsersRequest request);
}
