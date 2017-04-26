package com.emc.internal.reserv.converter;

import com.emc.internal.reserv.dto.UserSearchableField;
import com.google.common.primitives.Ints;
import org.springframework.stereotype.Service;

/**
 * @author trofiv
 * @date 15.04.2017
 */
@Service
public class UserSearchableFieldConverter implements FieldConverter<UserSearchableField> {
    @Override
    public Object convertField(final UserSearchableField field, final String value) {
        if (field == null) {
            return value;
        }

        switch (field) {
            case ID:
                //noinspection ReturnOfNull
                return value == null ? null : Ints.tryParse(value);
            case EMAIL:
            case USERNAME:
            case FIRST_NAME:
            case LAST_NAME:
            case MIDDLE_NAME:
            case ROLE:
                return value;
            default:
                throw new UnsupportedOperationException("Some fields have hot been covered!");
        }
    }
}
