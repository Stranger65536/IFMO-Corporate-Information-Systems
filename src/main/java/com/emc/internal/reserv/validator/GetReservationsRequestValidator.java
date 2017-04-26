package com.emc.internal.reserv.validator;

import com.emc.internal.reserv.dto.GetReservationsRequest;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.util.RuntimeUtil.raiseForgotEnumBranchException;
import static com.emc.internal.reserv.validator.RequestValidator.validateDateTimeSearchValue;
import static com.emc.internal.reserv.validator.RequestValidator.validateIntegerSearchValue;
import static com.emc.internal.reserv.validator.RequestValidator.validatePageNumber;
import static com.emc.internal.reserv.validator.RequestValidator.validatePageSize;
import static com.emc.internal.reserv.validator.RequestValidator.validateReservationStatus;
import static com.emc.internal.reserv.validator.RequestValidator.validateReservationType;

/**
 * @author trofiv
 * @date 26.04.2017
 */
@Service
public class GetReservationsRequestValidator implements RequestValidator<GetReservationsRequest> {
    @Override
    public void validate(final GetReservationsRequest request) {
        validatePageSize(request.getPageSize());
        validatePageNumber(request.getPage());
        if (request.getSearchField() != null) {
            switch (request.getSearchField()) {
                case ID:
                case RESOURCE_ID:
                case OWNER_ID:
                    validateIntegerSearchValue(request.getSearchType(), request.getSearchValue(),
                            request.getSearchValueLowerBound(), request.getSearchValueUpperBound());
                    break;
                case STARTS_AT:
                case ENDS_AT:
                case CREATED_ON:
                case UPDATED_ON:
                    validateDateTimeSearchValue(request.getSearchType(), request.getSearchValue(),
                            request.getSearchValueLowerBound(), request.getSearchValueUpperBound());
                    break;
                case STATUS:
                    validateReservationStatus(request.getSearchType(), request.getSearchValue(),
                            request.getSearchValueLowerBound(), request.getSearchValueUpperBound());
                    break;
                case TYPE:
                    validateReservationType(request.getSearchType(), request.getSearchValue(),
                            request.getSearchValueLowerBound(), request.getSearchValueUpperBound());
                    break;
                default:
                    throw raiseForgotEnumBranchException();
            }
        }
    }
}
