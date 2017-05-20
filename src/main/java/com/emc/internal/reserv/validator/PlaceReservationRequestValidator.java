package com.emc.internal.reserv.validator;

import com.emc.internal.reserv.dto.PlaceReservationRequest;
import com.emc.internal.reserv.dto.PlaceReservationRequestField;
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
public class PlaceReservationRequestValidator implements RequestValidator<PlaceReservationRequest> {
    @Override
    public void validate(final PlaceReservationRequest request) {
        if (request.getStartsAt() == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(PlaceReservationRequestField.STARTS_AT.value()));
        }
        if (request.getEndsAt() == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(PlaceReservationRequestField.ENDS_AT.value()));
        }
        if (request.getStartsAt().compare(request.getEndsAt()) > 0) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidTimeRangeMessage(PlaceReservationRequestField.STARTS_AT.value()));
        }
        validateReservationType(request.getType());
    }
}
