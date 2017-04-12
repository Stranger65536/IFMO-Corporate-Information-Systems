package com.emc.internal.reserv.config.activity;

import com.emc.internal.reserv.repository.UserRepository;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntityManager;

import java.util.List;
import java.util.Map;

import static com.emc.internal.reserv.config.activity.ActivityUtil.userToActivitiIdentityInfo;
import static com.emc.internal.reserv.util.RuntimeUtil.explicitCallProhibitedError;

/**
 * @author trofiv
 * @date 11.04.2017
 */
public class CustomIdentityInfoEntityManager extends IdentityInfoEntityManager {
    //TODO replace with service call
    private final UserRepository userRepository;

    public CustomIdentityInfoEntityManager(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void deleteUserInfoByUserIdAndKey(final String userId, final String key) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public void deleteIdentityInfo(final IdentityInfoEntity identityInfo) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    protected List<IdentityInfoEntity> findIdentityInfoDetails(final String identityInfoId) {
        return super.findIdentityInfoDetails(identityInfoId);
    }

    @Override
    public void setUserInfo(
            final String userId,
            final String userPassword,
            final String type,
            final String key,
            final String value,
            final String accountPassword,
            final Map<String, String> accountDetails) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public byte[] encryptPassword(final String accountPassword, final String userPassword) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public String decryptPassword(final byte[] storedPassword, final String userPassword) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public IdentityInfoEntity findUserInfoByUserIdAndKey(final String userId, final String key) {
        return userToActivitiIdentityInfo(userRepository.findOne(Integer.parseInt(userId)));
    }

    @Override
    public List<String> findUserInfoKeysByUserIdAndType(final String userId, final String type) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }
}
