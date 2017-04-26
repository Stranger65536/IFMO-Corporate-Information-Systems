package com.emc.internal.reserv.converter;

import com.emc.internal.reserv.dto.ReservationSearchableField;
import com.google.common.primitives.Ints;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.springframework.stereotype.Service;

import static com.emc.internal.reserv.util.RuntimeUtil.raiseFieldCoverageException;
import static com.emc.internal.reserv.util.RuntimeUtil.toLocalDateTime;

/**
 * @author trofiv
 * @date 15.04.2017
 */
@Service
public class ReservationSearchableFieldConverter implements FieldConverter<ReservationSearchableField> {
    @Override
    public Object convertField(final ReservationSearchableField field, final String value) {
        if (field == null) {
            return value;
        }

        switch (field) {
            case ID:
            case OWNER_ID:
            case RESOURCE_ID:
            case STATUS:
            case TYPE:
                //noinspection ReturnOfNull
                return value == null ? null : Ints.tryParse(value);
            case STARTS_AT:
            case ENDS_AT:
            case CREATED_ON:
            case UPDATED_ON:
                return toLocalDateTime(XMLGregorianCalendarImpl.parse(value));
            default:
                throw raiseFieldCoverageException();
        }
    }
}
