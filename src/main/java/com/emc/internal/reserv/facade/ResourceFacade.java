package com.emc.internal.reserv.facade;

import com.emc.internal.reserv.dto.CreateResourceRequest;
import com.emc.internal.reserv.dto.CreateResourceResponse;
import com.emc.internal.reserv.dto.GetResourceRequest;
import com.emc.internal.reserv.dto.GetResourceResponse;
import com.emc.internal.reserv.dto.GetResourcesRequest;
import com.emc.internal.reserv.dto.GetResourcesResponse;
import com.emc.internal.reserv.dto.UpdateResourceRequest;
import com.emc.internal.reserv.dto.UpdateResourceResponse;

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
