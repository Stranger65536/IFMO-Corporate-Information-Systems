package com.emc.internal.reserv.validator;

import com.emc.internal.reserv.dto.ApproveReservationRequestField;
import com.emc.internal.reserv.dto.ApproveReservationRequest;
import com.emc.internal.reserv.entity.ReservationTypes;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.dto.FaultCode.INVALID_FIELD_VALUE;
import static com.emc.internal.reserv.dto.FaultCode.RESERVATION_TYPE_DOES_NOT_EXIST;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidFieldMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidReservationTypeMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidTimeRangeMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;

/**
 * @author trofiv
 * @date 26.04.2017
 */
@Service
public class ApproveReservationRequestValidator implements RequestValidator<ApproveReservationRequest> {
    @Override
    public void validate(final ApproveReservationRequest request) {
        if (request.getStartsAt() == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(ApproveReservationRequestField.STARTS_AT.value()));
        }
        if (request.getEndsAt() == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(ApproveReservationRequestField.ENDS_AT.value()));
        }
        if (request.getStartsAt().compare(request.getEndsAt()) > 0) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidTimeRangeMessage(ApproveReservationRequestField.STARTS_AT.value()));
        }
        ReservationTypes.getById(request.getType()).orElseThrow(() ->
                raiseServiceFaultException(RESERVATION_TYPE_DOES_NOT_EXIST,
                        getInvalidReservationTypeMessage(request.getType())));
    }
}
