package com.emc.internal.reserv.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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

    //TODO extract code duplicates
    public static String explicitCallProhibitedError() {
        final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        final String s = e.getClassName();
        return s.substring(s.lastIndexOf('.') + 1, s.length())
                + '.' + e.getMethodName()
                + " must not be called explicitly!";
    }

    public static String enterMethodMessage() {
        final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        final String s = e.getClassName();
        return s.substring(s.lastIndexOf('.') + 1, s.length())
                + '.' + e.getMethodName()
                + ".enter";
    }

    public static String exitMethodMessage() {
        final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        final String s = e.getClassName();
        return s.substring(s.lastIndexOf('.') + 1, s.length())
                + '.' + e.getMethodName()
                + ".exit";
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

    public static Timestamp toTimestamp(final XMLGregorianCalendar calendar) {
        return new Timestamp(calendar.toGregorianCalendar().getTimeInMillis());
    }

    public static UnsupportedOperationException raiseUninitializedEntityField() {
        return new UnsupportedOperationException("Mandatory entity field uninitialized!");
    }
}
