package com.emc.internal.reserv.exception;

import https.internal_emc_com.reserv_io.ws.ServiceFault;
import lombok.Getter;

/**
 * @author trofiv
 * @date 12.04.2017
 */
@Getter
public class ServiceFaultException extends RuntimeException {
    private final ServiceFault serviceFault;

    public ServiceFaultException(final String message, final ServiceFault serviceFault) {
        super(message);
        this.serviceFault = serviceFault;
    }

    public ServiceFaultException(final String message, final Throwable e, final ServiceFault serviceFault) {
        super(message, e);
        this.serviceFault = serviceFault;
    }
}
