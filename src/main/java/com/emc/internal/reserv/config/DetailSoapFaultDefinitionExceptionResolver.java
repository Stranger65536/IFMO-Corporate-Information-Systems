package com.emc.internal.reserv.config;

import com.emc.internal.reserv.dto.FaultCode;
import com.emc.internal.reserv.exception.ServiceFaultException;
import lombok.extern.log4j.Log4j2;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

import javax.xml.namespace.QName;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author trofiv
 * @date 12.04.2017
 */
@Log4j2
public class DetailSoapFaultDefinitionExceptionResolver extends SoapFaultMappingExceptionResolver {
    public static final QName CODE = new QName("code");
    public static final QName DESCRIPTION = new QName("description");

    @Override
    protected void customizeFault(final Object endpoint, final Exception ex, final SoapFault fault) {
        //noinspection InstanceofConcreteClass
        if (ex instanceof ServiceFaultException) {
            ((ServiceFaultException) ex).customizeFault(fault);
        } else {
            final String errorId = Long.toHexString(ThreadLocalRandom.current().nextLong());
            log.error("Unknown error {}", errorId, ex);
            final SoapFaultDetail detail = fault.addFaultDetail();
            detail.addFaultDetailElement(CODE).addText(FaultCode.UNKNOWN_ERROR.value());
            detail.addFaultDetailElement(DESCRIPTION).addText("Error id: " + errorId + "; " + ex.getMessage());
        }
    }
}