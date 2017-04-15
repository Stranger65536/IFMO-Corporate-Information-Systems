package com.emc.internal.reserv.util.validate;

import com.google.common.primitives.Ints;

import java.util.regex.Pattern;

/**
 * @author trofiv
 * @date 14.04.2017
 */
public interface RequestValidator<T> {
    Pattern USERNAME = Pattern.compile("[\\p{L}\\p{M}\\p{S}\\p{N}\\p{P}]{6,32}");
    Pattern EMAIL = Pattern.compile("(([^<>()\\[\\]\\\\.,;:\\s@']+(\\.[^<>()\\[\\]\\\\.,;:\\s@']+)*)|('.+'))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))");
    Pattern NAME = Pattern.compile("[\\\\p{L} .'\\-]{0,35}");

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean isInteger(final String value) {
        return Ints.tryParse(value) != null;
    }

    static boolean isIntegerRange(final String value, final int minValueInclusive, final int maxValueInclusive) {
        final Integer intValue = Ints.tryParse(value);
        return intValue != null && intValue >= minValueInclusive && intValue <= maxValueInclusive;
    }

    static boolean isMatchMaxLength(final String value, final int maxLength) {
        return value != null && value.length() <= maxLength;
    }

    static boolean isMatchLength(final String value, final int minLength, final int maxLength) {
        return value != null && value.length() >= minLength && value.length() <= maxLength;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean isMatchPattern(final String value, final Pattern pattern) {
        return pattern.matcher(value).matches();
    }

    void validate(final T request);
}
