package com.emc.internal.reserv.util;

import lombok.SneakyThrows;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

import static java.text.MessageFormat.format;
import static java.time.ZoneOffset.UTC;

/**
 * @author trofiv
 * @date 11.04.2017
 */
public class RuntimeUtil {
    private static final DatatypeFactory DATA_TYPE_FACTORY;

    static {
        try {
            DATA_TYPE_FACTORY = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String explicitCallProhibitedError() {
        return getMethodName() + " must not be called explicitly!";
    }

    private static String getMethodName() {
        final StackTraceElement e = Thread.currentThread().getStackTrace()[3];
        final String s = e.getClassName();
        return s.substring(s.lastIndexOf('.') + 1, s.length())
                + '.' + e.getMethodName();
    }

    public static String enterMethodMessage() {
        return getMethodName() + ".enter";
    }

    public static String exitMethodMessage() {
        return getMethodName() + ".exit";
    }

    public static UnsupportedOperationException raiseForgotEnumBranchException() {
        return new UnsupportedOperationException("Some enum branches have not been covered!");
    }

    public static XMLGregorianCalendar toCalendar(final Timestamp timestamp) {
        final LocalDateTime dataTime = timestamp.toLocalDateTime();

        final XMLGregorianCalendar calendar = DATA_TYPE_FACTORY.newXMLGregorianCalendar();

        calendar.setYear(dataTime.getYear());
        calendar.setMonth(dataTime.getMonthValue());
        calendar.setDay(dataTime.getDayOfMonth());
        calendar.setHour(dataTime.getHour());
        calendar.setMinute(dataTime.getMinute());
        calendar.setSecond(dataTime.getSecond());

        return calendar;
    }

    public static LocalDateTime toLocalDateTime(final Calendar calendar) {
        return calendar.toInstant().atOffset(UTC).toLocalDateTime();
    }

    public static LocalDateTime toLocalDateTime(final XMLGregorianCalendar calendar) {
        return calendar.toGregorianCalendar().toInstant().atOffset(UTC).toLocalDateTime();
    }

    public static UnsupportedOperationException raiseUninitializedEntityField() {
        return new UnsupportedOperationException("Mandatory entity field uninitialized!");
    }

    public static UnsupportedOperationException raiseEnumValueNotMapped(final String name) {
        return new UnsupportedOperationException(format("Mandatory enum value ''{0}'' is not mapped to the orm object!", name));
    }

    @SneakyThrows
    public static String hashPassword(final String password) {
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        final byte[] bytes = md.digest(password.getBytes("UTF-8"));
        return String.format("%0128x", new BigInteger(1, bytes));
    }

    public static UnsupportedOperationException raiseFieldCoverageException() {
        return new UnsupportedOperationException("Some fields have hot been covered!");
    }
}
