package com.emc.internal.reserv.validator;

import com.emc.internal.reserv.dto.UpdateReservationRequest;
import com.emc.internal.reserv.dto.UpdateReservationRequestField;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.dto.FaultCode.INVALID_FIELD_VALUE;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidFieldMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidTimeRangeMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.validator.RequestValidator.validateReservationType;

/**
 * @author trofiv
 * @date 26.04.2017
 */
@Service
public class UpdateReservationRequestValidator implements RequestValidator<UpdateReservationRequest> {
    @Override
    public void validate(final UpdateReservationRequest request) {
        if (request.getStartsAt() == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UpdateReservationRequestField.STARTS_AT.value()));
        }
        if (request.getEndsAt() == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UpdateReservationRequestField.ENDS_AT.value()));
        }
        if (request.getStartsAt().compare(request.getEndsAt()) > 0) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidTimeRangeMessage(UpdateReservationRequestField.STARTS_AT.value()));
        }
        if (request.getNewStartsAt() == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UpdateReservationRequestField.NEW_STARTS_AT.value()));
        }
        if (request.getNewEndsAt() == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(UpdateReservationRequestField.NEW_ENDS_AT.value()));
        }
        if (request.getNewStartsAt().compare(request.getNewEndsAt()) > 0) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidTimeRangeMessage(UpdateReservationRequestField.NEW_STARTS_AT.value()));
        }
        validateReservationType(request.getType());
        validateReservationType(request.getNewType());
    }
}
