package com.emc.internal.reserv.converter;

/**
 * @author trofiv
 * @date 15.04.2017
 */
public interface FieldConverter<R extends Enum<?>> {
    Object convertField(final R field, final String value);
}
