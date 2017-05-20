package com.emc.internal.reserv.validator;

import com.emc.internal.reserv.dto.UpdateUserInfoRequest;
import com.emc.internal.reserv.dto.UpdateUserInfoRequestField;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.dto.FaultCode.INVALID_FIELD_VALUE;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidFieldMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.validator.RequestValidator.isMatchPattern;

/**
 * @author trofiv
 * @date 14.04.2017
 */
@Service
public class UpdateUserInfoRequestValidator implements RequestValidator<UpdateUserInfoRequest> {
    @Override
    @SuppressWarnings({"MethodWithMoreThanThreeNegations", "OverlyComplexMethod"})
    public void validate(final UpdateUserInfoRequest request) {
        if (!isMatchPattern(request.getUsername(), USERNAME)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UpdateUserInfoRequestField.USERNAME.value()));
        }
        if (request.getEmail() == null || request.getEmail().length() > MAX_EMAIL_LENGTH || !isMatchPattern(request.getEmail(), EMAIL)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UpdateUserInfoRequestField.EMAIL.value()));
        }
        if (request.getFirstName() != null && !isMatchPattern(request.getFirstName(), NAME)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UpdateUserInfoRequestField.FIRST_NAME.value()));
        }
        if (request.getLastName() != null && !isMatchPattern(request.getLastName(), NAME)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UpdateUserInfoRequestField.LAST_NAME.value()));
        }
        if (request.getMiddleName() != null && !isMatchPattern(request.getMiddleName(), NAME)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UpdateUserInfoRequestField.MIDDLE_NAME.value()));
        }
    }
}
