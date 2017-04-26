package com.emc.internal.reserv.validator;

import com.emc.internal.reserv.dto.ProposeNewTimeRequestField;
import com.emc.internal.reserv.dto.ProposeNewTimeRequest;
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
public class ProposeNewTimeRequestValidator implements RequestValidator<ProposeNewTimeRequest> {
    @Override
    public void validate(final ProposeNewTimeRequest request) {
        if (request.getStartsAt() == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(ProposeNewTimeRequestField.STARTS_AT.value()));
        }
        if (request.getEndsAt() == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(ProposeNewTimeRequestField.ENDS_AT.value()));
        }
        if (request.getStartsAt().compare(request.getEndsAt()) > 0) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidTimeRangeMessage(ProposeNewTimeRequestField.STARTS_AT.value()));
        }
        if (request.getNewStartsAt() == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(ProposeNewTimeRequestField.NEW_STARTS_AT.value()));
        }
        if (request.getNewEndsAt() == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(ProposeNewTimeRequestField.NEW_ENDS_AT.value()));
        }
        if (request.getNewStartsAt().compare(request.getNewEndsAt()) > 0) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidTimeRangeMessage(ProposeNewTimeRequestField.NEW_STARTS_AT.value()));
        }
        ReservationTypes.getById(request.getType()).orElseThrow(() ->
                raiseServiceFaultException(RESERVATION_TYPE_DOES_NOT_EXIST,
                        getInvalidReservationTypeMessage(request.getType())));
        ReservationTypes.getById(request.getNewType()).orElseThrow(() ->
                raiseServiceFaultException(RESERVATION_TYPE_DOES_NOT_EXIST,
                        getInvalidReservationTypeMessage(request.getType())));
    }
}
