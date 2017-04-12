package com.emc.internal.reserv.endpoint;

import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.service.ResourceService;
import https.internal_emc_com.reserv_io.ws.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Collection;
import java.util.Optional;

import static com.emc.internal.reserv.endpoint.EndpointConstants.NAMESPACE_URI;
import static com.emc.internal.reserv.entity.Resource.fromResourceInfo;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static https.internal_emc_com.reserv_io.ws.FaultCode.RESOURCE_DOES_NOT_EXIST;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;

/**
 * @author trofiv
 * @date 12.04.2017
 */
@Log4j2
@Endpoint
public class ResourceEndpoint {
    private final ResourceService resourceService;

    @Autowired
    public ResourceEndpoint(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateResourceRequest")
    @ResponsePayload
    public CreateResourceResponse createResource(@RequestPayload final CreateResourceRequest request) {
        //TODO validate input
        final Resource resource = resourceService.createResource(request.getName(), request.getLocation());
        final ResourceInfo info = resource.toResourceInfo();
        final CreateResourceResponse response = new CreateResourceResponse();
        response.setResourceInfo(info);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdateResourceRequest")
    @ResponsePayload
    public UpdateResourceResponse createResource(@RequestPayload final UpdateResourceRequest request) {
        //TODO validate input
        final Resource resource = resourceService.updateResource(fromResourceInfo(request.getResourceInfo()));
        final ResourceInfo info = resource.toResourceInfo();
        final UpdateResourceResponse response = new UpdateResourceResponse();
        response.setResourceInfo(info);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetResourceRequest")
    @ResponsePayload
    public GetResourceResponse getUser(@RequestPayload final GetResourceRequest request) {
        final Optional<Resource> optionalResource = resourceService.getResource(request.getId());
        if (optionalResource.isPresent()) {
            final GetResourceResponse response = new GetResourceResponse();
            response.setResourceInfo(optionalResource.get().toResourceInfo());
            return response;
        } else {
            throw raiseServiceFaultException(RESOURCE_DOES_NOT_EXIST,
                    format("No resource with id {0} was found!", request.getId()));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetResourcesRequest")
    @ResponsePayload
    public GetResourcesResponse getUsers(@RequestPayload final GetResourcesRequest request) {
        final Collection<Resource> resources = resourceService.getResources();
        final GetResourcesResponse response = new GetResourcesResponse();
        response.getResourceInfo().addAll(resources.stream().map(Resource::toResourceInfo).collect(toList()));
        return response;
    }
}
