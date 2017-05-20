package com.emc.internal.reserv.converter;

import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.UserSearchableField;
import com.google.common.primitives.Ints;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.converter.FieldConverter.convertIfIsNotContainsSearch;
import static com.emc.internal.reserv.util.RuntimeUtil.raiseFieldCoverageException;

/**
 * @author trofiv
 * @date 15.04.2017
 */
@Service
public class UserSearchableFieldConverter implements FieldConverter<UserSearchableField> {
    @Override
    public Object convertField(final UserSearchableField field, final SearchType searchType, final String value) {
        if (field == null) {
            return value;
        }

        switch (field) {
            case ID:
                return convertIfIsNotContainsSearch(searchType, v -> v == null ? null : Ints.tryParse(v), value);
            case EMAIL:
            case USERNAME:
            case FIRST_NAME:
            case LAST_NAME:
            case MIDDLE_NAME:
            case ROLE:
                return value;
            default:
                throw raiseFieldCoverageException();
        }
    }
}
