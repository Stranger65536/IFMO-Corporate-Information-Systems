package com.emc.internal.reserv.util;

import com.emc.internal.reserv.dto.FaultCode;
import com.emc.internal.reserv.dto.ServiceFault;
import com.emc.internal.reserv.exception.ServiceFaultException;
import lombok.extern.log4j.Log4j2;

import static java.text.MessageFormat.format;

/**
 * @author trofiv
 * @date 12.04.2017
 */
@Log4j2
public class EndpointUtil {
    public static String getInvalidFieldMessage(final String fieldName) {
        return format("{0} field is invalid!", fieldName);
    }

    public static ServiceFaultException raiseServiceFaultException(final FaultCode code, final Exception e) {
        return raiseServiceFaultException(code, e.getMessage());
    }

    public static ServiceFaultException raiseServiceFaultException(final FaultCode code, final String message) {
        final ServiceFault fault = createServiceFault(code, message);
        return new ServiceFaultException(message, fault);
    }

    private static ServiceFault createServiceFault(final FaultCode code, final String message) {
        final ServiceFault fault = new ServiceFault();
        fault.setCode(code);
        fault.setDescription(message == null ? "" : message);
        return fault;
    }

    public static String getInvalidRoleMessage(final String name) {
        return format("Role with name ''{0}'' does not exist!", name);
    }

    public static String getInvalidReservationTypeMessage(final String name) {
        return format("Reservation type with name ''{0}'' does not exist!", name);
    }

    public static String getInvalidTimeRangeMessage(final String fieldName) {
        return format("Reservation time range is invalid, check field ''{0}''!", fieldName);
    }

    public static String getNonExistentUserIdMessage(final int userId) {
        return format("User with id ''{0}'' has not been found!", userId);
    }

    public static String getNonExistentReservationIdMessage(final long reservationId) {
        return format("Reservation with id ''{0}'' has not been found!", reservationId);
    }

    public static String getNonExistentUsernameMessage(final String username) {
        return format("User with username ''{0}'' has not been found!", username);
    }

    public static String getNonExistentResourceIdMessage(final int resourceId) {
        return format("Resource with id ''{0}'' has not been found!", resourceId);
    }

    public static String getUserEmailTakenMessage(final String email) {
        return format("User with email ''{0}'' already exists!", email);
    }

    public static String getUsernameTakenMessage(final String username) {
        return format("User with username ''{0}'' already exists!", username);
    }

    public static String getResourceIsNotUniqueMessage(final String name, final String location) {
        return format("Resource with name ''{0}'' and location ''{1}'' already exists!", name, location);
    }

    public static String getPendingReservationsLimitExceededMessage() {
        return "Can't perform the requested action due to exceeded limit of the reservations waiting for approval";
    }

    public static String getUnavailableEventOverlappingMessage() {
        return "Can't perform the requested action due to overlapping with an approved 'unavailable' reservation";
    }

    public static String getAccessDeniedMessage() {
        return format("You don''t have enough privileges to perform this action!");
    }

    public static String getActionIsNotSupportedMessage() {
        return format("Requested action is not supported at the current reservation state");
    }

    public static String getPasswordDoesNotMatchMessage() {
        return format("Password does not match the actual user password");
    }

    public static String getReservationInfoIsDifferentMessage() {
        return format("Actual reservation status is different from the specified information!");
    }
}
