package com.emc.internal.reserv.config;

import com.emc.internal.reserv.exception.ServiceFaultException;
import https.internal_emc_com.reserv_io.ws.FaultCode;
import https.internal_emc_com.reserv_io.ws.ServiceFault;
import lombok.extern.log4j.Log4j2;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

import javax.xml.namespace.QName;
import java.util.concurrent.ThreadLocalRandom;

import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFault;

/**
 * @author trofiv
 * @date 12.04.2017
 */
@Log4j2
public class DetailSoapFaultDefinitionExceptionResolver extends SoapFaultMappingExceptionResolver {
    private static final QName CODE = new QName("code");
    private static final QName DESCRIPTION = new QName("description");

    @Override
    protected void customizeFault(final Object endpoint, final Exception ex, final SoapFault fault) {
        //noinspection InstanceofConcreteClass
        if (ex instanceof ServiceFaultException) {
            final ServiceFault serviceFault = ((ServiceFaultException) ex).getServiceFault();
            final SoapFaultDetail detail = fault.addFaultDetail();
            detail.addFaultDetailElement(CODE).addText(serviceFault.getCode().value());
            detail.addFaultDetailElement(DESCRIPTION).addText(serviceFault.getDescription());
        } else {
            final String errorId = Long.toHexString(ThreadLocalRandom.current().nextLong());
            log.error("Unknown error {}", errorId, ex);
            final ServiceFault serviceFault = raiseServiceFault(FaultCode.UNKNOWN_ERROR, ex.getMessage() + "; Error id: " + errorId);
            final SoapFaultDetail detail = fault.addFaultDetail();
            detail.addFaultDetailElement(CODE).addText(serviceFault.getCode().value());
            detail.addFaultDetailElement(DESCRIPTION).addText(serviceFault.getDescription());
        }
    }
}