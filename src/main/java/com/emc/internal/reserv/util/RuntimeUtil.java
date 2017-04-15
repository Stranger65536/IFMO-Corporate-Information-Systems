package com.emc.internal.reserv.util;

/**
 * @author trofiv
 * @date 11.04.2017
 */
public class RuntimeUtil {
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
}
