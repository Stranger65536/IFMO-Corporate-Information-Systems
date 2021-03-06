package com.emc.internal.reserv.validator;

import com.emc.internal.reserv.dto.GetUsersRequest;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.util.RuntimeUtil.raiseForgotEnumBranchException;
import static com.emc.internal.reserv.validator.RequestValidator.validateIfIsNotContainsSearch;
import static com.emc.internal.reserv.validator.RequestValidator.validateIntegerSearchValue;
import static com.emc.internal.reserv.validator.RequestValidator.validatePageNumber;
import static com.emc.internal.reserv.validator.RequestValidator.validatePageSize;
import static com.emc.internal.reserv.validator.RequestValidator.validateSearchType;
import static com.emc.internal.reserv.validator.RequestValidator.validateStringSearchValue;

/**
 * @author trofiv
 * @date 14.04.2017
 */
@Service
public class GetUsersRequestValidator implements RequestValidator<GetUsersRequest> {
    @Override
    public void validate(final GetUsersRequest request) {
        validatePageSize(request.getPageSize());
        validatePageNumber(request.getPage());
        if (request.getSearchField() != null) {
            validateSearchType(request.getSearchType());
            switch (request.getSearchField()) {
                case ID:
                    validateIfIsNotContainsSearch(request.getSearchType(),
                            () -> validateIntegerSearchValue(request.getSearchType(), request.getSearchValue(),
                                    request.getSearchValueLowerBound(), request.getSearchValueUpperBound()));
                    break;
                case EMAIL:
                case USERNAME:
                case FIRST_NAME:
                case LAST_NAME:
                case MIDDLE_NAME:
                case ROLE:
                    validateIfIsNotContainsSearch(request.getSearchType(),
                            () -> validateStringSearchValue(request.getSearchType(), request.getSearchValue(),
                                    request.getSearchValueLowerBound(), request.getSearchValueUpperBound()));
                    break;
                default:
                    throw raiseForgotEnumBranchException();
            }
        }
    }
}
