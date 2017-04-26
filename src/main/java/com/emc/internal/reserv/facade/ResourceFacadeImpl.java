package com.emc.internal.reserv.facade;

import com.emc.internal.reserv.dto.CreateResourceRequest;
import com.emc.internal.reserv.dto.CreateResourceResponse;
import com.emc.internal.reserv.dto.GetResourceRequest;
import com.emc.internal.reserv.dto.GetResourceResponse;
import com.emc.internal.reserv.dto.GetResourcesRequest;
import com.emc.internal.reserv.dto.GetResourcesResponse;
import com.emc.internal.reserv.dto.ResourceInfo;
import com.emc.internal.reserv.dto.UpdateResourceRequest;
import com.emc.internal.reserv.dto.UpdateResourceResponse;
import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.entity.Resource.ResourceBuilder;
import com.emc.internal.reserv.service.ResourceService;
import com.emc.internal.reserv.validator.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

import static com.emc.internal.reserv.dto.FaultCode.RESOURCE_DOES_NOT_EXIST;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;

/**
 * @author trofiv
 * @date 17.04.2017
 */
@Service
public class ResourceFacadeImpl implements ResourceFacade {
    private final ResourceService resourceService;
    private final RequestValidator<CreateResourceRequest> createResourceRequestValidator;
    private final RequestValidator<UpdateResourceRequest> updateResourceRequestValidator;

    @Autowired
    public ResourceFacadeImpl(
            final ResourceService resourceService,
            final RequestValidator<CreateResourceRequest> createResourceRequestValidator,
            final RequestValidator<UpdateResourceRequest> updateResourceRequestValidator) {
        this.resourceService = resourceService;
        this.createResourceRequestValidator = createResourceRequestValidator;
        this.updateResourceRequestValidator = updateResourceRequestValidator;
    }

    @Override
    //TODO cache update
    public CreateResourceResponse createResource(final CreateResourceRequest request) {
        createResourceRequestValidator.validate(request);
        final Resource resource = resourceService.createResource(request.getName(), request.getLocation());
        final ResourceInfo info = resource.toResourceInfo();
        final CreateResourceResponse response = new CreateResourceResponse();
        response.setResourceInfo(info);
        return response;
    }

    @Override
    //TODO cache update
    public UpdateResourceResponse updateResource(final UpdateResourceRequest request) {
        updateResourceRequestValidator.validate(request);
        final Resource resource = resourceService.updateResource(new ResourceBuilder()
                .id(request.getId())
                .name(request.getName())
                .location(request.getLocation())
                .build());
        final ResourceInfo info = resource.toResourceInfo();
        final UpdateResourceResponse response = new UpdateResourceResponse();
        response.setResourceInfo(info);
        return response;
    }

    @Override
    @SuppressWarnings("DuplicateStringLiteralInspection")
    //TODO cache
    public GetResourceResponse getResource(final GetResourceRequest request) {
        final Optional<Resource> optionalResource = resourceService.getResource(request.getId());
        if (optionalResource.isPresent()) {
            final GetResourceResponse response = new GetResourceResponse();
            response.setResourceInfo(optionalResource.get().toResourceInfo());
            return response;
        } else {
            throw raiseServiceFaultException(RESOURCE_DOES_NOT_EXIST,
                    format("No resource with id {0} has been found!", request.getId()));
        }
    }

    @Override
    //TODO cache
    public GetResourcesResponse getResources(final GetResourcesRequest request) {
        final Collection<Resource> resources = resourceService.getResources();
        final GetResourcesResponse response = new GetResourcesResponse();
        response.getResourceInfo().addAll(resources.stream().map(Resource::toResourceInfo).collect(toList()));
        return response;
    }
}
