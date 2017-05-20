package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.Resource;

import java.util.Collection;

/**
 * @author trofiv
 * @date 13.04.2017
 */
public interface ResourceService {
    Resource createResource(final String name, final String location);

    Resource updateResource(final Resource resource);

    Resource getResource(final int id);

    Collection<Resource> getResources();
}
