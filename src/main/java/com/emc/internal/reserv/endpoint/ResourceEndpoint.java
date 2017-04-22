package com.emc.internal.reserv.endpoint;

import com.emc.internal.reserv.dto.CreateResourceRequest;
import com.emc.internal.reserv.dto.CreateResourceResponse;
import com.emc.internal.reserv.dto.GetResourceRequest;
import com.emc.internal.reserv.dto.GetResourceResponse;
import com.emc.internal.reserv.dto.GetResourcesRequest;
import com.emc.internal.reserv.dto.GetResourcesResponse;
import com.emc.internal.reserv.dto.UpdateResourceRequest;
import com.emc.internal.reserv.dto.UpdateResourceResponse;
import com.emc.internal.reserv.facade.ResourceFacade;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import static com.emc.internal.reserv.config.WebServiceConfig.API_NAMESPACE_URI;

/**
 * @author trofiv
 * @date 12.04.2017
 */
@Log4j2
@Endpoint
public class ResourceEndpoint {
    private final ResourceFacade resourceFacade;

    @Autowired
    public ResourceEndpoint(final ResourceFacade resourceFacade) {
        this.resourceFacade = resourceFacade;
    }

    @PayloadRoot(namespace = API_NAMESPACE_URI, localPart = "CreateResourceRequest")
    @ResponsePayload
    public CreateResourceResponse createResource(@RequestPayload final CreateResourceRequest request) {
        return resourceFacade.createResource(request);
    }

    @PayloadRoot(namespace = API_NAMESPACE_URI, localPart = "UpdateResourceRequest")
    @ResponsePayload
    public UpdateResourceResponse updateResource(@RequestPayload final UpdateResourceRequest request) {
        return resourceFacade.updateResource(request);
    }

    @PayloadRoot(namespace = API_NAMESPACE_URI, localPart = "GetResourceRequest")
    @ResponsePayload
    public GetResourceResponse getResource(@RequestPayload final GetResourceRequest request) {
        return resourceFacade.getResource(request);
    }

    @PayloadRoot(namespace = API_NAMESPACE_URI, localPart = "GetResourcesRequest")
    @ResponsePayload
    public GetResourcesResponse getResources(@RequestPayload final GetResourcesRequest request) {
        return resourceFacade.getResources(request);
    }
}
