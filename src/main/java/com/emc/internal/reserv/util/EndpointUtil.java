package com.emc.internal.reserv.util;

import com.emc.internal.reserv.exception.ServiceFaultException;
import https.internal_emc_com.reserv_io.ws.FaultCode;
import https.internal_emc_com.reserv_io.ws.ServiceFault;

import static https.internal_emc_com.reserv_io.ws.FaultCode.USER_ALREADY_REGISTERED;
import static java.text.MessageFormat.format;

/**
 * @author trofiv
 * @date 12.04.2017
 */
public class EndpointUtil {
    public static ServiceFaultException raiseServiceFaultException(final FaultCode code, final String message) {
        final ServiceFault fault = new ServiceFault();
        fault.setCode(code);
        fault.setDescription(message);
        return new ServiceFaultException(message, fault);
    }
}
