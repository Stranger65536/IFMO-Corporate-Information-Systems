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

import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;
import static com.emc.internal.reserv.util.RuntimeUtil.enterMethodMessage;
import static com.emc.internal.reserv.util.RuntimeUtil.exitMethodMessage;
import static https.internal_emc_com.reserv_io.ws.FaultCode.RESOURCE_DOES_NOT_EXIST;
import static https.internal_emc_com.reserv_io.ws.FaultCode.RESOURCE_IS_NOT_UNIQUE;
import static java.text.MessageFormat.format;
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
        log.info("{} " +
                        "name: {}, " +
                        "location: {}",
                enterMethodMessage(),
                name, location);
        if (resourceRepository.findOneByNameAndLocation(name, location).isPresent()) {
            throw raiseServiceFaultException(RESOURCE_IS_NOT_UNIQUE,
                    format("Resource with name {0} and location {1} is already registered",
                            name, location));
        }

        final Resource resource = new ResourceBuilder().name(name).location(location).build();
        final Resource result = resourceRepository.saveAndFlush(resource);

        log.info(exitMethodMessage());

        return result;
    }

    @Override
    @SuppressWarnings("DuplicateStringLiteralInspection")
    @Transactional(isolation = SERIALIZABLE, propagation = REQUIRED)
    public Resource updateResource(final Resource resource) {
        log.info("{} " +
                        "id: {}, " +
                        "name: {}, " +
                        "location: {}",
                enterMethodMessage(),
                resource.getId(), resource.getName(), resource.getLocation());
        final Optional<Resource> resourceOptional = resourceRepository
                .findOneByNameAndLocation(resource.getName(), resource.getLocation());

        if (!resourceOptional.isPresent()) {
            throw raiseServiceFaultException(RESOURCE_DOES_NOT_EXIST,
                    format("No resource with id {0} has been found!", resource.getId()));
        }

        if (resourceOptional.get().getId() != resource.getId()) {
            throw raiseServiceFaultException(RESOURCE_IS_NOT_UNIQUE,
                    format("Resource with name {0} and location {1} already exists",
                            resource.getName(), resource.getLocation()));
        }

        final Resource result = resourceRepository.saveAndFlush(resource);

        log.info(exitMethodMessage());

        return result;
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, propagation = REQUIRED)
    public Optional<Resource> getResource(final int id) {
        return Optional.ofNullable(resourceRepository.findOne(id));
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, propagation = REQUIRED)
    public Collection<Resource> getResources() {
        return resourceRepository.findAll();
    }
}
