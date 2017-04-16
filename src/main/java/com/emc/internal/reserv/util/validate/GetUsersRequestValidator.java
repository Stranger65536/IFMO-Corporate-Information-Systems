package com.emc.internal.reserv.util.validate;

import https.internal_emc_com.reserv_io.ws.GetUsersRequest;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.util.RuntimeUtil.raiseForgotEnumBranchException;
import static com.emc.internal.reserv.util.validate.RequestValidator.*;

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
            switch (request.getSearchField()) {
                case ID:
                    validateIntegerSearchValue(request.getSearchType(), request.getSearchValue(),
                            request.getSearchValueLowerBound(), request.getSearchValueUpperBound());
                    break;
                case EMAIL:
                case USERNAME:
                case FIRST_NAME:
                case LAST_NAME:
                case MIDDLE_NAME:
                case ROLE:
                    validateStringSearchValue(request.getSearchType(), request.getSearchValue(),
                            request.getSearchValueLowerBound(), request.getSearchValueUpperBound());
                    break;
                default:
                    throw raiseForgotEnumBranchException();
            }
        }
    }
}
