package com.emc.internal.reserv.config.activity;

import com.emc.internal.reserv.repository.UserRepository;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;

import java.util.List;
import java.util.Map;

import static com.emc.internal.reserv.config.activity.ActivityUtil.roleToActivitiGroup;
import static com.emc.internal.reserv.util.RuntimeUtil.explicitCallProhibitedError;
import static java.util.Collections.singletonList;

/**
 * @author trofiv
 * @date 10.04.2017
 */
public class CustomGroupEntityManager extends GroupEntityManager {
    //TODO replace with service call
    private final UserRepository userRepository;

    public CustomGroupEntityManager(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Group createNewGroup(final String groupId) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public void insertGroup(final Group group) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public void updateGroup(final Group updatedGroup) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public void deleteGroup(final String groupId) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public GroupQuery createNewGroupQuery() {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public List<Group> findGroupByQueryCriteria(final GroupQueryImpl query, final Page page) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public long findGroupCountByQueryCriteria(final GroupQueryImpl query) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public List<Group> findGroupsByUser(final String userId) {
        return singletonList(roleToActivitiGroup(userRepository.findOne(Integer.parseInt(userId)).getRole()));
    }

    @Override
    public List<Group> findGroupsByNativeQuery(
            final Map<String, Object> parameterMap,
            final int firstResult,
            final int maxResults) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public long findGroupCountByNativeQuery(final Map<String, Object> parameterMap) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public boolean isNewGroup(final Group group) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }
}
