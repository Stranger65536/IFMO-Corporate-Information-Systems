package com.emc.internal.reserv.endpoint;

import com.emc.internal.reserv.facade.UserFacade;
import https.internal_emc_com.reserv_io.ws.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import static com.emc.internal.reserv.endpoint.EndpointConstants.NAMESPACE_URI;

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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "RegistrationRequest")
    @ResponsePayload
    public void registerUser(@RequestPayload final RegistrationRequest request) {
        userFacade.registerUser(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload final GetUserRequest request) {
        return userFacade.getUser(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetUsersRequest")
    @ResponsePayload
    public GetUsersResponse getUsers(@RequestPayload final GetUsersRequest request) {
        return userFacade.getUsers(request);
    }
}
