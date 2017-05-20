package com.emc.internal.reserv.validator;

import com.emc.internal.reserv.dto.GrantPermissionsRequest;
import com.emc.internal.reserv.entity.Roles;
import org.springframework.stereotype.Service;

/**
 * @author trofiv
 * @date 26.04.2017
 */
@Service
public class GrantPermissionsRequestValidator implements RequestValidator<GrantPermissionsRequest> {
    @Override
    public void validate(final GrantPermissionsRequest request) {
        Roles.getByName(request.getRole().value());
    }
}
