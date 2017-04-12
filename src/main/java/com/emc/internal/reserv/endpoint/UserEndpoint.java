package com.emc.internal.reserv.endpoint;

import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.service.UserService;
import https.internal_emc_com.reserv_io.ws.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.WebServiceFaultException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Collection;
import java.util.Optional;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;

/**
 * @author trofiv
 * @date 12.04.2017
 */
@Log4j2
@Endpoint
public class UserEndpoint {
    private static final String NAMESPACE_URI = "https://internal.emc.com/reserv-io/ws";

    private final UserService userService;

    @Autowired
    public UserEndpoint(final UserService userService) {
        this.userService = userService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "RegistrationRequest")
    @ResponsePayload
    public void registerUser(@RequestPayload final RegistrationRequest request) {
        try {
            userService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getMiddleName());
        } catch (Exception e) {
            log.error("Can't register user with request {}!", request, e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload final GetUserRequest request) {
        final Optional<User> optionalUser = userService.getUser(request.getId());
        if (optionalUser.isPresent()) {
            final GetUserResponse response = new GetUserResponse();
            response.setUserInfo(optionalUser.get().toUserInfo());
            return response;
        } else {
            throw new WebServiceFaultException(format("No user with id {0} was found!", request.getId()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetUsersRequest")
    @ResponsePayload
    public GetUsersResponse getUsers(@RequestPayload final GetUsersRequest request) {
        final Collection<User> matchedUsers = userService.getUsers(
                request.getPage(),
                request.getPageSize(),
                request.getFilteringField(),
                request.getFilteringValue(),
                request.getSortingOrder(),
                request.getSortingField());
        final GetUsersResponse response = new GetUsersResponse();
        response.getUserInfo().addAll(matchedUsers.stream().map(User::toUserInfo).collect(toList()));
        return response;
    }
}
