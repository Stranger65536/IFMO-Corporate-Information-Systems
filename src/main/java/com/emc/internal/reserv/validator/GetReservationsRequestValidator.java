package com.emc.internal.reserv.validator;

import com.emc.internal.reserv.dto.GetReservationsRequest;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.util.RuntimeUtil.raiseForgotEnumBranchException;
import static com.emc.internal.reserv.validator.RequestValidator.validateDateTimeSearchValue;
import static com.emc.internal.reserv.validator.RequestValidator.validateIfIsNotContainsSearch;
import static com.emc.internal.reserv.validator.RequestValidator.validateIntegerSearchValue;
import static com.emc.internal.reserv.validator.RequestValidator.validatePageNumber;
import static com.emc.internal.reserv.validator.RequestValidator.validatePageSize;
import static com.emc.internal.reserv.validator.RequestValidator.validateSearchType;

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
            validateSearchType(request.getSearchType());
            switch (request.getSearchField()) {
                case ID:
                case RESOURCE:
                case OWNER:
                    validateIfIsNotContainsSearch(request.getSearchType(),
                            () -> validateIntegerSearchValue(request.getSearchType(), request.getSearchValue(),
                                    request.getSearchValueLowerBound(), request.getSearchValueUpperBound()));
                    break;
                case STARTS_AT:
                case ENDS_AT:
                case CREATED_AT:
                case UPDATED_AT:
                    validateIfIsNotContainsSearch(request.getSearchType(),
                            () -> validateDateTimeSearchValue(request.getSearchType(), request.getSearchValue(),
                                    request.getSearchValueLowerBound(), request.getSearchValueUpperBound()));
                    break;
                case STATUS:
                case TYPE:
                    break;
                default:
                    throw raiseForgotEnumBranchException();
            }
        }
    }
}
