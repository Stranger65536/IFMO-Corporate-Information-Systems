package com.emc.internal.reserv.validator;

import com.emc.internal.reserv.dto.GenericSearchRequestField;
import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.Type;
import com.emc.internal.reserv.entity.ReservationTypes;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.Range;

import java.util.function.Function;
import java.util.regex.Pattern;

import static com.emc.internal.reserv.dto.FaultCode.INVALID_FIELD_VALUE;
import static com.emc.internal.reserv.dto.FaultCode.RESERVATION_TYPE_DOES_NOT_EXIST;
import static com.emc.internal.reserv.dto.SearchType.CONTAINS;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidFieldMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidReservationTypeMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.RuntimeUtil.raiseForgotEnumBranchException;
import static javax.xml.bind.DatatypeConverter.parseDateTime;

/**
 * @author trofiv
 * @date 14.04.2017
 */
public interface RequestValidator<T> {
    int MAX_EMAIL_LENGTH = 254;
    Pattern NAME = Pattern.compile("[\\p{L} .'\\-]{0,35}");
    Pattern USERNAME = Pattern.compile("[\\p{L}\\p{M}\\p{S}\\p{N}\\p{P}]{5,32}");
    Pattern EMAIL = Pattern.compile("(([^<>()\\[\\]\\\\.,;:\\s@']+(\\.[^<>()\\[\\]\\\\.,;:\\s@']+)*)|('.+'))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))");
    Range<Integer> PAGE_SIZE_RANGE = Range.between(1, 100);
    Range<Integer> PASSWORD_LENGTH_RANGE = Range.between(5, 32);
    Range<Integer> RESOURCE_NAME_LENGTH_RANGE = Range.between(1, 25);
    Range<Integer> RESOURCE_LOCATION_LENGTH_RANGE = Range.between(0, 45);
    int MAX_SEARCH_VALUE_LENGTH = 254;
    int MIN_PAGE_NUMBER = 1;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean isInteger(final String value) {
        return value != null && Ints.tryParse(value) != null;
    }

    static boolean isDateTime(final String value) {
        if (value != null) {
            try {
                parseDateTime(value);
                return true;
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        } else {
            return false;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean isMatchPattern(final String value, final Pattern pattern) {
        return pattern.matcher(value).matches();
    }

    static void validateStringSearchValue(
            final SearchType searchType,
            final String value,
            final String valueLowerBound,
            final String valueUpperBound) {
        validateSearchValue(searchType, value, valueLowerBound, valueUpperBound,
                v -> v != null && v.length() <= MAX_SEARCH_VALUE_LENGTH);
    }

    @SuppressWarnings("MethodWithMoreThanThreeNegations")
    static void validateSearchValue(
            final SearchType searchType,
            final String value,
            final String valueLowerBound,
            final String valueUpperBound,
            final Function<String, Boolean> validator) {
        if (searchType != null) {
            switch (searchType) {
                case EQUALS:
                case LESS:
                case LESS_EQUAL:
                case GREATER:
                case GREATER_EQUAL:
                case CONTAINS:
                    if (!validator.apply(value)) {
                        throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(GenericSearchRequestField.SEARCH_VALUE.value()));
                    }
                    break;
                case BETWEEN:
                    if (!validator.apply(valueLowerBound)) {
                        throw raiseServiceFaultException(INVALID_FIELD_VALUE, GenericSearchRequestField.SEARCH_VALUE_LOWER_BOUND.value());
                    }
                    if (!validator.apply(valueUpperBound)) {
                        throw raiseServiceFaultException(INVALID_FIELD_VALUE, GenericSearchRequestField.SEARCH_VALUE_UPPER_BOUND.value());
                    }
                    break;
                default:
                    throw raiseForgotEnumBranchException();
            }
        }
    }

    static void validateIntegerSearchValue(
            final SearchType searchType,
            final String value,
            final String valueLowerBound,
            final String valueUpperBound) {
        validateSearchValue(searchType, value, valueLowerBound, valueUpperBound, RequestValidator::isInteger);
    }

    static void validateDateTimeSearchValue(
            final SearchType searchType,
            final String value,
            final String valueLowerBound,
            final String valueUpperBound) {
        validateSearchValue(searchType, value, valueLowerBound, valueUpperBound, RequestValidator::isDateTime);
    }

    static void validatePageNumber(final int page) {
        if (page < MIN_PAGE_NUMBER) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(GenericSearchRequestField.PAGE.value()));
        }
    }

    static void validatePageSize(final int pageSize) {
        if (!PAGE_SIZE_RANGE.contains(pageSize)) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(GenericSearchRequestField.PAGE_SIZE.value()));
        }
    }

    static void validateSearchType(final SearchType searchType) {
        if (searchType == null) {
            throw raiseServiceFaultException(INVALID_FIELD_VALUE, getInvalidFieldMessage(GenericSearchRequestField.SEARCH_TYPE.value()));
        }
    }

    static void validateIfIsNotContainsSearch(final SearchType searchType, final Runnable validator) {
        if (searchType != CONTAINS) {
            validator.run();
        }
    }

    static void validateReservationType(final Type type) {
        if (type != null) {
            ReservationTypes.getByName(type.name());
        } else {
            throw raiseServiceFaultException(RESERVATION_TYPE_DOES_NOT_EXIST,
                    getInvalidReservationTypeMessage(null));
        }
    }

    void validate(final T request);
}
