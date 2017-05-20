package com.emc.internal.reserv.validator;

import com.emc.internal.reserv.dto.ChangePasswordRequest;
import com.emc.internal.reserv.dto.ChangePasswordRequestField;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.dto.FaultCode.INVALID_FIELD_VALUE;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidFieldMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;

/**
 * @author trofiv
 * @date 14.04.2017
 */
@Service
public class ChangePasswordRequestValidator implements RequestValidator<ChangePasswordRequest> {
    @Override
    @SuppressWarnings({"MethodWithMoreThanThreeNegations", "OverlyComplexMethod"})
    public void validate(final ChangePasswordRequest request) {
        if (request.getOldPassword() == null || !PASSWORD_LENGTH_RANGE.contains(request.getOldPassword().length())) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(ChangePasswordRequestField.OLD_PASSWORD.value()));
        }
        if (request.getOldPassword() == null || !PASSWORD_LENGTH_RANGE.contains(request.getOldPassword().length())) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(ChangePasswordRequestField.NEW_PASSWORD.value()));
        }
    }
}
