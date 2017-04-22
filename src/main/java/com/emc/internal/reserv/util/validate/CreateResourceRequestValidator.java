package com.emc.internal.reserv.util.validate;

import com.emc.internal.reserv.dto.CreateResourceRequest;
import com.emc.internal.reserv.dto.CreateResourceRequestField;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.dto.FaultCode.INVALID_FIELD_VALUE;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidFieldMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;

/**
 * @author trofiv
 * @date 17.04.2017
 */
@Service
public class CreateResourceRequestValidator implements RequestValidator<CreateResourceRequest> {
    @Override
    public void validate(final CreateResourceRequest request) {
        if (request.getName() == null || !RESOURCE_NAME_LENGTH_RANGE.contains(request.getName().length())) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(CreateResourceRequestField.NAME.value()));
        }
        if (request.getLocation() != null && !RESOURCE_LOCATION_LENGTH_RANGE.contains(request.getLocation().length())) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(CreateResourceRequestField.LOCATION.value()));
        }
    }
}
