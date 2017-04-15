package com.emc.internal.reserv.util.validate;

import https.internal_emc_com.reserv_io.ws.GetUsersRequest;
import https.internal_emc_com.reserv_io.ws.GetUsersRequestField;
import https.internal_emc_com.reserv_io.ws.SearchType;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.util.EndpointUtil.getInvalidFieldMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.validate.RequestValidator.isInteger;
import static https.internal_emc_com.reserv_io.ws.FaultCode.INVALID_FIELD_VALUE;

/**
 * @author trofiv
 * @date 14.04.2017
 */
@Service
public class GetUsersRequestValidator implements RequestValidator<GetUsersRequest> {
    @Override
    public void validate(final GetUsersRequest request) {
        if (request.getSearchField() != null) {
            switch (request.getSearchField()) {
                case ID:
                    validateIntegerValue(request.getSearchType(), request.getSearchValue(),
                            request.getSearchValueLowerBound(), request.getSearchValueUpperBound());
                    break;
                case EMAIL:
                case USERNAME:
                case FIRST_NAME:
                case LAST_NAME:
                case MIDDLE_NAME:
                case ROLE:
                    break;
            }
        }
    }

    @SuppressWarnings("MethodWithMoreThanThreeNegations")
    private static void validateIntegerValue(
            final SearchType searchType,
            final String value,
            final String valueLowerBound,
            final String valueUpperBound) {
        if (searchType != null) {
            switch (searchType) {
                case EQUALS:
                case CONTAINS:
                    if (!isInteger(value)) {
                        throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(GetUsersRequestField.SEARCH_VALUE.value()));
                    }
                    break;
                case BETWEEN:
                    if (!isInteger(valueLowerBound)) {
                        throw raiseServiceFaultException(INVALID_FIELD_VALUE, GetUsersRequestField.SEARCH_VALUE_LOWER_BOUND.value());
                    }
                    if (!isInteger(valueUpperBound)) {
                        throw raiseServiceFaultException(INVALID_FIELD_VALUE, GetUsersRequestField.SEARCH_VALUE_UPPER_BOUND.value());
                    }

            }
        }
    }
}
