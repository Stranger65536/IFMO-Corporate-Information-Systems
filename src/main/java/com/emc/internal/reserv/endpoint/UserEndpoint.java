package com.emc.internal.reserv.endpoint;

import com.emc.internal.reserv.dto.ChangePasswordRequest;
import com.emc.internal.reserv.dto.GetUserRequest;
import com.emc.internal.reserv.dto.GetUserResponse;
import com.emc.internal.reserv.dto.GetUsersRequest;
import com.emc.internal.reserv.dto.GetUsersResponse;
import com.emc.internal.reserv.dto.GrantPermissionsRequest;
import com.emc.internal.reserv.dto.RegistrationRequest;
import com.emc.internal.reserv.dto.UpdateUserInfoRequest;
import com.emc.internal.reserv.facade.UserFacade;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import static com.emc.internal.reserv.config.WebServiceConfig.API_NAMESPACE_URI;
import static com.emc.internal.reserv.config.WebServiceConfig.REGISTER_NAMESPACE_URI;

/**
 * @author trofiv
 * @date 12.04.2017
 */
@Log4j2
@Endpoint
public class UserEndpoint {
    private final UserFacade userFacade;

    @Autowired
    public UserEndpoint(final UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PayloadRoot(namespace = REGISTER_NAMESPACE_URI, localPart = "RegistrationRequest")
    @ResponsePayload
    public void registerUser(@RequestPayload final RegistrationRequest request) {
        userFacade.registerUser(request);
    }

    @PayloadRoot(namespace = API_NAMESPACE_URI, localPart = "UpdateUserInfoRequest")
    @ResponsePayload
    public void updateUserInfo(@RequestPayload final UpdateUserInfoRequest request) {
        userFacade.updateUserInfo(request);
    }

    @PayloadRoot(namespace = API_NAMESPACE_URI, localPart = "ChangePasswordRequest")
    @ResponsePayload
    public void changePassword(@RequestPayload final ChangePasswordRequest request) {
        userFacade.changePassword(request);
    }

    @PayloadRoot(namespace = API_NAMESPACE_URI, localPart = "GrantPermissionsRequest")
    @ResponsePayload
    public void grantPermissions(@RequestPayload final GrantPermissionsRequest request) {
        userFacade.grantPermissions(request);
    }

    @PayloadRoot(namespace = API_NAMESPACE_URI, localPart = "GetUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload final GetUserRequest request) {
        return userFacade.getUser(request);
    }

    @PayloadRoot(namespace = API_NAMESPACE_URI, localPart = "GetUsersRequest")
    @ResponsePayload
    public GetUsersResponse getUsers(@RequestPayload final GetUsersRequest request) {
        return userFacade.getUsers(request);
    }
}
