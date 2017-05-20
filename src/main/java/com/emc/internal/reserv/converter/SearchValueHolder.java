package com.emc.internal.reserv.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author trofiv
 * @date 20.05.2017
 */
@Getter
@AllArgsConstructor
public class SearchValueHolder {
    private final Object searchValue;
    private final Object searchValueLowerBound;
    private final Object searchValueUpperBound;
}
