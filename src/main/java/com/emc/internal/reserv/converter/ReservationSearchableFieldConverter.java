package com.emc.internal.reserv.converter;

import com.emc.internal.reserv.dto.ReservationSearchableField;
import com.emc.internal.reserv.dto.SearchType;
import com.google.common.primitives.Ints;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.converter.FieldConverter.convertIfIsNotContainsSearch;
import static com.emc.internal.reserv.util.RuntimeUtil.raiseFieldCoverageException;
import static com.emc.internal.reserv.util.RuntimeUtil.toLocalDateTime;
import static java.sql.Timestamp.valueOf;
import static javax.xml.bind.DatatypeConverter.parseDateTime;

/**
 * @author trofiv
 * @date 15.04.2017
 */
@Service
public class ReservationSearchableFieldConverter implements FieldConverter<ReservationSearchableField> {
    @Override
    public Object convertField(final ReservationSearchableField field, final SearchType searchType, final String value) {
        if (field == null) {
            return value;
        }

        switch (field) {
            case ID:
            case OWNER:
            case RESOURCE:
                return convertIfIsNotContainsSearch(searchType, v -> v == null ? null : Ints.tryParse(v), value);
            case STARTS_AT:
            case ENDS_AT:
            case CREATED_ON:
            case UPDATED_ON:
                return convertIfIsNotContainsSearch(searchType, v -> valueOf(toLocalDateTime(parseDateTime(v))), value);
            case STATUS:
            case TYPE:
                return value;
            default:
                throw raiseFieldCoverageException();
        }
    }
}
