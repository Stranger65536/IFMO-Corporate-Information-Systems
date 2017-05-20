package com.emc.internal.reserv.facade;

import com.emc.internal.reserv.converter.FieldConverter;
import com.emc.internal.reserv.converter.SearchValueHolder;
import com.emc.internal.reserv.dto.ChangePasswordRequest;
import com.emc.internal.reserv.dto.GetUserRequest;
import com.emc.internal.reserv.dto.GetUserResponse;
import com.emc.internal.reserv.dto.GetUsersRequest;
import com.emc.internal.reserv.dto.GetUsersResponse;
import com.emc.internal.reserv.dto.GrantPermissionsRequest;
import com.emc.internal.reserv.dto.RegistrationRequest;
import com.emc.internal.reserv.dto.UpdateUserInfoRequest;
import com.emc.internal.reserv.dto.UserSearchableField;
import com.emc.internal.reserv.entity.Role;
import com.emc.internal.reserv.entity.Roles;
import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.service.AuthenticationService;
import com.emc.internal.reserv.service.UserService;
import com.emc.internal.reserv.validator.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;

import static com.emc.internal.reserv.dto.FaultCode.ACCESS_DENIED;
import static com.emc.internal.reserv.dto.FaultCode.PASSWORD_DOES_NOT_MATCH;
import static com.emc.internal.reserv.util.EndpointUtil.getAccessDeniedMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getPasswordDoesNotMatchMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.RuntimeUtil.hashPassword;
import static java.util.stream.Collectors.toList;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * @author trofiv
 * @date 15.04.2017
 */
@Service
public class UserFacadeImpl implements UserFacade {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final RequestValidator<RegistrationRequest> registrationRequestValidator;
    private final RequestValidator<GetUsersRequest> getUsersRequestValidator;
    private final RequestValidator<UpdateUserInfoRequest> updateUserInfoRequestValidator;
    private final RequestValidator<ChangePasswordRequest> changePasswordRequestValidator;
    private final RequestValidator<GrantPermissionsRequest> grantPermissionsRequestValidator;
    private final FieldConverter<UserSearchableField> userSearchableFieldConverter;

    @Autowired
    public UserFacadeImpl(
            final UserService userService,
            final AuthenticationService authenticationService,
            final RequestValidator<RegistrationRequest> registrationRequestValidator,
            final RequestValidator<GetUsersRequest> getUsersRequestValidator,
            final RequestValidator<UpdateUserInfoRequest> updateUserInfoRequestValidator,
            final RequestValidator<ChangePasswordRequest> changePasswordRequestValidator,
            final RequestValidator<GrantPermissionsRequest> grantPermissionsRequestValidator,
            final FieldConverter<UserSearchableField> userSearchableFieldConverter) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.registrationRequestValidator = registrationRequestValidator;
        this.getUsersRequestValidator = getUsersRequestValidator;
        this.updateUserInfoRequestValidator = updateUserInfoRequestValidator;
        this.changePasswordRequestValidator = changePasswordRequestValidator;
        this.grantPermissionsRequestValidator = grantPermissionsRequestValidator;
        this.userSearchableFieldConverter = userSearchableFieldConverter;
    }

    @Override
    //Callable service marked with the corresponding transactional level
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
    //Callable service marked with the corresponding transactional level
    public GetUserResponse getUser(final GetUserRequest request) {
        final GetUserResponse response = new GetUserResponse();
        response.setUserInfo(userService.getUser(request.getId()).toUserInfo());
        return response;
    }

    @Override
    //Callable service marked with the corresponding transactional level
    public GetUsersResponse getUsers(final GetUsersRequest request) {
        getUsersRequestValidator.validate(request);
        final SearchValueHolder convertedSearchValues = userSearchableFieldConverter.convertSearchValues(
                request.getSearchType(),
                request.getSearchField(),
                request.getSearchValue(),
                request.getSearchValueLowerBound(),
                request.getSearchValueUpperBound());
        final Collection<User> matchedUsers = userService.getUsers(
                request.getPage(),
                request.getPageSize(),
                request.getSearchField(),
                request.getSearchType(),
                convertedSearchValues.getSearchValue(),
                convertedSearchValues.getSearchValueLowerBound(),
                convertedSearchValues.getSearchValueUpperBound(),
                request.getSortingOrder(),
                request.getSortingField());
        final GetUsersResponse response = new GetUsersResponse();
        response.getUserInfo().addAll(matchedUsers.stream().map(User::toUserInfo).collect(toList()));
        return response;
    }

    @Override
    @Transactional(propagation = REQUIRED, isolation = SERIALIZABLE)
    public void updateUserInfo(final UpdateUserInfoRequest request) {
        updateUserInfoRequestValidator.validate(request);
        final User user = authenticationService.getCurrentUser();
        userService.updateUserInfo(
                user,
                request.getEmail(),
                request.getUsername(),
                request.getFirstName(),
                request.getMiddleName(),
                request.getLastName());
    }

    @Override
    @Transactional(propagation = REQUIRED, isolation = SERIALIZABLE)
    public void changePassword(final ChangePasswordRequest request) {
        changePasswordRequestValidator.validate(request);
        final User user = authenticationService.getCurrentUser();
        checkOldPasswordIsSame(user, request.getOldPassword());
        userService.updatePassword(user, request.getNewPassword());
    }

    private static void checkOldPasswordIsSame(final User user, final String oldPassword) {
        if (!Objects.equals(user.getPassword(), hashPassword(oldPassword))) {
            throw raiseServiceFaultException(PASSWORD_DOES_NOT_MATCH, getPasswordDoesNotMatchMessage());
        }
    }

    @Override
    @Transactional(propagation = REQUIRED, isolation = SERIALIZABLE)
    public void grantPermissions(final GrantPermissionsRequest request) {
        grantPermissionsRequestValidator.validate(request);
        final Role role = Roles.getByName(request.getRole().value());
        final User user = authenticationService.getCurrentUser();
        final User targetUser = userService.getUser(request.getUserId());
        checkEnoughPermissionsToGrant(user, targetUser, role);
        userService.grantPermissions(targetUser, role);
    }

    private static void checkEnoughPermissionsToGrant(final User user, final User targetUser, final Role role) {
        final Roles userRole = Roles.valueOf(user.getRole().getName());
        final Roles targetUserRole = Roles.valueOf(targetUser.getRole().getName());
        final Roles targetRole = Roles.valueOf(role.getName());

        if (userRole.getPriority() < targetRole.getPriority() || targetUserRole.getPriority() > userRole.getPriority()) {
            throw raiseServiceFaultException(ACCESS_DENIED, getAccessDeniedMessage());
        }
    }
}
