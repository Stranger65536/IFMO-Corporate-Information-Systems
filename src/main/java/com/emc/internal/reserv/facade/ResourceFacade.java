package com.emc.internal.reserv.facade;

import https.internal_emc_com.reserv_io.ws.*;

/**
 * @author trofiv
 * @date 17.04.2017
 */
public interface ResourceFacade {
    CreateResourceResponse createResource(final CreateResourceRequest request);

    UpdateResourceResponse updateResource(final UpdateResourceRequest request);

    GetResourceResponse getResource(final GetResourceRequest request);

    GetResourcesResponse getResources(final GetResourcesRequest request);
}
