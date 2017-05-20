package com.emc.internal.reserv.converter;

import com.emc.internal.reserv.dto.SearchType;

import java.util.function.Function;

import static com.emc.internal.reserv.dto.SearchType.CONTAINS;
import static com.emc.internal.reserv.util.RuntimeUtil.raiseForgotEnumBranchException;

/**
 * @author trofiv
 * @date 15.04.2017
 */
public interface FieldConverter<R extends Enum<?>> {
    static Object convertIfIsNotContainsSearch(
            final SearchType searchType,
            final Function<String, Object> converter,
            final String value) {
        return searchType == CONTAINS ? value : converter.apply(value);
    }

    default SearchValueHolder convertSearchValues(
            final SearchType searchType,
            final R field,
            final String value,
            final String valueLowerBound,
            final String valueUpperBound) {
        if (searchType != null) {
            switch (searchType) {
                case EQUALS:
                case LESS:
                case LESS_EQUAL:
                case GREATER:
                case GREATER_EQUAL:
                case CONTAINS:
                    return new SearchValueHolder(this.convertField(field, searchType, value), null, null);
                case BETWEEN:
                    return new SearchValueHolder(null,
                            this.convertField(field, searchType, valueLowerBound),
                            this.convertField(field, searchType, valueUpperBound));
                default:
                    throw raiseForgotEnumBranchException();
            }
        } else {
            return new SearchValueHolder(this.convertField(field, null, value), null, null);
        }
    }

    Object convertField(final R field, final SearchType searchType, final String value);
}
