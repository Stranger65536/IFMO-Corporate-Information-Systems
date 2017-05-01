package com.emc.internal.reserv.validator;

import com.emc.internal.reserv.dto.RegistrationRequest;
import com.emc.internal.reserv.dto.UserRegistrationRequestField;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static com.emc.internal.reserv.dto.FaultCode.INVALID_FIELD_VALUE;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidFieldMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.validator.RequestValidator.isMatchPattern;

/**
 * @author trofiv
 * @date 14.04.2017
 */
@Service
public class RegistrationRequestValidator implements RequestValidator<RegistrationRequest> {
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final Pattern NAME = Pattern.compile("[\\p{L} .'\\-]{0,35}");
    private static final Pattern USERNAME = Pattern.compile("[\\p{L}\\p{M}\\p{S}\\p{N}\\p{P}]{5,32}");
    private static final Pattern EMAIL = Pattern.compile("(([^<>()\\[\\]\\\\.,;:\\s@']+(\\.[^<>()\\[\\]\\\\.,;:\\s@']+)*)|('.+'))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))");

    @Override
    @SuppressWarnings({"MethodWithMoreThanThreeNegations", "OverlyComplexMethod"})
    public void validate(final RegistrationRequest request) {
        if (!isMatchPattern(request.getUsername(), USERNAME)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UserRegistrationRequestField.USERNAME.value()));
        }
        if (request.getEmail() == null || request.getEmail().length() > MAX_EMAIL_LENGTH || !isMatchPattern(request.getEmail(), EMAIL)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UserRegistrationRequestField.EMAIL.value()));
        }
        if (request.getPassword() == null || !PASSWORD_LENGTH_RANGE.contains(request.getPassword().length())) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UserRegistrationRequestField.PASSWORD.value()));
        }
        if (request.getFirstName() != null && !isMatchPattern(request.getFirstName(), NAME)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UserRegistrationRequestField.FIRST_NAME.value()));
        }
        if (request.getLastName() != null && !isMatchPattern(request.getLastName(), NAME)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UserRegistrationRequestField.LAST_NAME.value()));
        }
        if (request.getMiddleName() != null && !isMatchPattern(request.getMiddleName(), NAME)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UserRegistrationRequestField.MIDDLE_NAME.value()));
        }
    }
}
