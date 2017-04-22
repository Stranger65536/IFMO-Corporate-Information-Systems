package com.emc.internal.reserv.exception;

import com.emc.internal.reserv.dto.ServiceFault;
import lombok.Getter;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;

import static com.emc.internal.reserv.config.DetailSoapFaultDefinitionExceptionResolver.CODE;
import static com.emc.internal.reserv.config.DetailSoapFaultDefinitionExceptionResolver.DESCRIPTION;
import static com.emc.internal.reserv.dto.FaultCode.UNKNOWN_ERROR;

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

    public void customizeFault(final SoapFault fault) {
        final SoapFaultDetail detail = fault.addFaultDetail();
        detail.addFaultDetailElement(CODE).addText(serviceFault == null
                ? UNKNOWN_ERROR.value()
                : serviceFault.getCode().value());
        detail.addFaultDetailElement(DESCRIPTION).addText(serviceFault == null
                ? ""
                : serviceFault.getDescription());
    }
}
