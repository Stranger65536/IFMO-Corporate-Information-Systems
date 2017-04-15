package com.emc.internal.reserv.util.convert;

import com.google.common.primitives.Ints;
import https.internal_emc_com.reserv_io.ws.UserSearchableField;
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
                return Ints.tryParse(value);
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
