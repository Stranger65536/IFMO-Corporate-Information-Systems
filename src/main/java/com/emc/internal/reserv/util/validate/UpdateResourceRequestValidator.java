package com.emc.internal.reserv.util.validate;

import com.emc.internal.reserv.dto.CreateResourceRequestField;
import com.emc.internal.reserv.dto.UpdateResourceRequest;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.dto.FaultCode.INVALID_FIELD_VALUE;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidFieldMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;

/**
 * @author trofiv
 * @date 17.04.2017
 */
@Service
public class UpdateResourceRequestValidator implements RequestValidator<UpdateResourceRequest> {
    @Override
    public void validate(final UpdateResourceRequest request) {
        if (request.getName() == null || !RESOURCE_NAME_LENGTH_RANGE.contains(request.getName().length())) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(CreateResourceRequestField.NAME.value()));
        }
        if (request.getName() != null && !RESOURCE_LOCATION_LENGTH_RANGE.contains(request.getName().length())) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(CreateResourceRequestField.NAME.value()));
        }
    }
}
