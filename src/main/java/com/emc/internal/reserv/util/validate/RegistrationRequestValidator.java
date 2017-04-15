package com.emc.internal.reserv.util.validate;

import https.internal_emc_com.reserv_io.ws.RegistrationRequest;
import https.internal_emc_com.reserv_io.ws.UserRegistrationRequestField;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.util.EndpointUtil.getInvalidFieldMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.validate.RequestValidator.*;
import static https.internal_emc_com.reserv_io.ws.FaultCode.INVALID_FIELD_VALUE;

/**
 * @author trofiv
 * @date 14.04.2017
 */
@Service
public class RegistrationRequestValidator implements RequestValidator<RegistrationRequest> {
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 32;

    @Override
    @SuppressWarnings({"MethodWithMoreThanThreeNegations", "OverlyComplexMethod"})
    public void validate(final RegistrationRequest request) {
        if (!isMatchPattern(request.getUsername(), USERNAME)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UserRegistrationRequestField.USERNAME.value()));
        }
        if (!isMatchPattern(request.getEmail(), EMAIL) || !isMatchMaxLength(request.getEmail(), MAX_EMAIL_LENGTH)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UserRegistrationRequestField.EMAIL.value()));
        }
        if (!isMatchLength(request.getPassword(), MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH)) {
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
