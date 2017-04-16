package com.emc.internal.reserv.facade;

import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.service.UserService;
import com.emc.internal.reserv.util.convert.FieldConverter;
import com.emc.internal.reserv.util.validate.RequestValidator;
import https.internal_emc_com.reserv_io.ws.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static https.internal_emc_com.reserv_io.ws.FaultCode.USER_DOES_NOT_EXIST;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;

/**
 * @author trofiv
 * @date 15.04.2017
 */
@Service
public class UserFacadeImpl implements UserFacade {
    private final UserService userService;
    private final RequestValidator<RegistrationRequest> registrationRequestValidator;
    private final RequestValidator<GetUsersRequest> getUsersRequestValidator;
    private final FieldConverter<UserSearchableField> userSearchableFieldConverter;

    @Autowired
    public UserFacadeImpl(
            final UserService userService,
            final RequestValidator<RegistrationRequest> registrationRequestValidator,
            final RequestValidator<GetUsersRequest> getUsersRequestValidator,
            final FieldConverter<UserSearchableField> userSearchableFieldConverter) {
        this.userService = userService;
        this.registrationRequestValidator = registrationRequestValidator;
        this.getUsersRequestValidator = getUsersRequestValidator;
        this.userSearchableFieldConverter = userSearchableFieldConverter;
    }

    @Override
    public void registerUser(final RegistrationRequest request) {
        registrationRequestValidator.validate(request);
        userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getMiddleName());
    }

    @Override
    public GetUserResponse getUser(final GetUserRequest request) {
        final Optional<User> optionalUser = userService.getUser(request.getId());
        if (optionalUser.isPresent()) {
            final GetUserResponse response = new GetUserResponse();
            response.setUserInfo(optionalUser.get().toUserInfo());
            return response;
        } else {
            throw raiseServiceFaultException(USER_DOES_NOT_EXIST,
                    format("No user with id {0} has been found!", request.getId()));
        }
    }

    @Override
    public GetUsersResponse getUsers(final GetUsersRequest request) {
        getUsersRequestValidator.validate(request);
        final Collection<User> matchedUsers = userService.getUsers(
                request.getPage(),
                request.getPageSize(),
                request.getSearchField(),
                request.getSearchType(),
                userSearchableFieldConverter.convertField(request.getSearchField(), request.getSearchValue()),
                userSearchableFieldConverter.convertField(request.getSearchField(), request.getSearchValueLowerBound()),
                userSearchableFieldConverter.convertField(request.getSearchField(), request.getSearchValueUpperBound()),
                request.getSortingOrder(),
                request.getSortingField());
        final GetUsersResponse response = new GetUsersResponse();
        response.getUserInfo().addAll(matchedUsers.stream().map(User::toUserInfo).collect(toList()));
        return response;
    }
}
