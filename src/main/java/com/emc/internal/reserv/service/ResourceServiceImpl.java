package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.Resource;
import com.emc.internal.reserv.entity.Resource.ResourceBuilder;
import com.emc.internal.reserv.repository.ResourceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static com.emc.internal.reserv.dto.FaultCode.RESOURCE_DOES_NOT_EXIST;
import static com.emc.internal.reserv.dto.FaultCode.RESOURCE_IS_NOT_UNIQUE;
import static com.emc.internal.reserv.util.EndpointUtil.getNonExistentResourceIdMessage;
import static com.emc.internal.reserv.util.EndpointUtil.getResourceIsNotUniqueMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.RuntimeUtil.enterMethodMessage;
import static com.emc.internal.reserv.util.RuntimeUtil.exitMethodMessage;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * @author trofiv
 * @date 13.04.2017
 */
@Log4j2
@Service
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;

    @Autowired
    public ResourceServiceImpl(final ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    @SuppressWarnings("DuplicateStringLiteralInspection")
    @Transactional(isolation = SERIALIZABLE, propagation = REQUIRED)
    public Resource createResource(final String name, final String location) {
        log.debug("{} " +
                        "name: {}, " +
                        "location: {}",
                enterMethodMessage(),
                name, location);
        if (resourceRepository.findOneByNameAndLocation(name, location).isPresent()) {
            throw raiseServiceFaultException(RESOURCE_IS_NOT_UNIQUE,
                    getResourceIsNotUniqueMessage(name, location));
        }

        final Resource resource = new ResourceBuilder().name(name).location(location).build();
        final Resource result = resourceRepository.save(resource);

        log.debug(exitMethodMessage());

        return result;
    }

    @Override
    @SuppressWarnings("DuplicateStringLiteralInspection")
    @Transactional(isolation = SERIALIZABLE, propagation = REQUIRED)
    public Resource updateResource(final Resource resource) {
        log.debug("{} " +
                        "id: {}, " +
                        "name: {}, " +
                        "location: {}",
                enterMethodMessage(),
                resource.getId(), resource.getName(), resource.getLocation());
        getResourceOrThrow(resource.getId());

        final Optional<Resource> resourceOptionalByName = resourceRepository
                .findOneByNameAndLocation(resource.getName(), resource.getLocation());

        if (resourceOptionalByName.isPresent() && resourceOptionalByName.get().getId() != resource.getId()) {
            throw raiseServiceFaultException(RESOURCE_IS_NOT_UNIQUE,
                    getResourceIsNotUniqueMessage(resource.getName(), resource.getLocation()));
        }

        final Resource result = resourceRepository.save(resource);

        log.debug(exitMethodMessage());

        return result;
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, propagation = REQUIRED)
    public Resource getResource(final int id) {
        log.debug("{} id: {}", enterMethodMessage(), id);
        final Resource result = getResourceOrThrow(id);
        log.debug(exitMethodMessage());
        return result;
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, propagation = REQUIRED)
    public Collection<Resource> getResources() {
        log.debug(enterMethodMessage());
        final Collection<Resource> result = resourceRepository.findAll();
        log.debug(exitMethodMessage());
        return result;
    }

    private Resource getResourceOrThrow(final int id) {
        return Optional.ofNullable(resourceRepository.findOne(id)).orElseThrow(() ->
                raiseServiceFaultException(RESOURCE_DOES_NOT_EXIST, getNonExistentResourceIdMessage(id)));
    }
}
