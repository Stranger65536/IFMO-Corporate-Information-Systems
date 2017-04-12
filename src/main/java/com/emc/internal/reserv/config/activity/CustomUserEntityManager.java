package com.emc.internal.reserv.config.activity;

import com.emc.internal.reserv.entity.User;
import com.emc.internal.reserv.repository.UserRepository;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.emc.internal.reserv.config.activity.ActivityUtil.roleToActivitiGroup;
import static com.emc.internal.reserv.config.activity.ActivityUtil.userToActivitiUser;
import static com.emc.internal.reserv.util.RuntimeUtil.explicitCallProhibitedError;
import static java.util.Collections.singletonList;

/**
 * @author trofiv
 * @date 10.04.2017
 */
public class CustomUserEntityManager extends UserEntityManager {
    //TODO replace with service call
    private final UserRepository userRepository;

    public CustomUserEntityManager(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public org.activiti.engine.identity.User createNewUser(final String userId) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public void insertUser(final org.activiti.engine.identity.User user) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public void updateUser(final org.activiti.engine.identity.User updatedUser) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public org.activiti.engine.identity.User findUserById(final String userId) {
        return userToActivitiUser(userRepository.findOne(Integer.parseInt(userId)));
    }


    @Override
    public void deleteUser(final String userId) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public List<org.activiti.engine.identity.User> findUserByQueryCriteria(final UserQueryImpl query, final Page page) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public long findUserCountByQueryCriteria(final UserQueryImpl query) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public List<Group> findGroupsByUser(final String userId) {
        return singletonList(roleToActivitiGroup(userRepository.findOne(Integer.parseInt(userId)).getRole()));
    }

    @Override
    public UserQuery createNewUserQuery() {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public IdentityInfoEntity findUserInfoByUserIdAndKey(final String userId, final String key) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public List<String> findUserInfoKeysByUserIdAndType(final String userId, final String type) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public Boolean checkPassword(final String userId, final String password) {
        final User user = userRepository.findOne(Integer.parseInt(userId));
        return Objects.equals(user.getPassword(), password);
    }

    @Override
    public List<org.activiti.engine.identity.User> findPotentialStarterUsers(final String processDefId) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public List<org.activiti.engine.identity.User> findUsersByNativeQuery(
            final Map<String, Object> parameterMap,
            final int firstResult,
            final int maxResults) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public long findUserCountByNativeQuery(final Map<String, Object> parameterMap) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public boolean isNewUser(final org.activiti.engine.identity.User user) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public Picture getUserPicture(final String userId) {
        //TODO add pictures support
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public void setUserPicture(final String userId, final Picture picture) {
        //TODO add pictures support
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }
}
