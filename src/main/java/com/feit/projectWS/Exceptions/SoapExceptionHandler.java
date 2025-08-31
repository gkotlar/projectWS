
package com.feit.projectWS.Exceptions;

import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

import javax.xml.namespace.QName;

/**
 * Custom SOAP fault handler for better error responses
 */
public class SoapExceptionHandler extends SoapFaultMappingExceptionResolver {
    
    // private static final QName SERVICE_FAULT_CODE = new QName("SERVICE_FAULT");
    // private static final QName CLIENT_FAULT_CODE = new QName("CLIENT_FAULT");
    
    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        logger.warn("Exception processed ", ex);

        
        // Add detail to fault
        SoapFaultDetail detail = fault.addFaultDetail();
        detail.addFaultDetailElement(new QName("http://projectWS.feit.com/events", "message"))
              .addText(ex.getMessage());
    }
}




    

