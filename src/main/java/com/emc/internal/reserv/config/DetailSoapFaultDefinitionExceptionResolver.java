package com.emc.internal.reserv.config;

import com.emc.internal.reserv.exception.ServiceFaultException;
import https.internal_emc_com.reserv_io.ws.ServiceFault;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

import javax.xml.namespace.QName;

/**
 * @author trofiv
 * @date 12.04.2017
 */
public class DetailSoapFaultDefinitionExceptionResolver extends SoapFaultMappingExceptionResolver {
    private static final QName CODE = new QName("code");
    private static final QName DESCRIPTION = new QName("description");

    @Override
    protected void customizeFault(final Object endpoint, final Exception ex, final SoapFault fault) {
        logger.warn("Exception processed ", ex);
        //noinspection InstanceofConcreteClass
        if (ex instanceof ServiceFaultException) {
            final ServiceFault serviceFault = ((ServiceFaultException) ex).getServiceFault();
            final SoapFaultDetail detail = fault.addFaultDetail();
            detail.addFaultDetailElement(CODE).addText(serviceFault.getCode().value());
            detail.addFaultDetailElement(DESCRIPTION).addText(serviceFault.getDescription());
        }
    }

}