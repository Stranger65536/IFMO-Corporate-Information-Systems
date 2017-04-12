package com.emc.internal.reserv.config.activity;

import org.activiti.engine.impl.persistence.entity.MembershipEntityManager;

import static com.emc.internal.reserv.util.RuntimeUtil.explicitCallProhibitedError;

/**
 * @author trofiv
 * @date 10.04.2017
 */
public class CustomMembershipEntityManager extends MembershipEntityManager {
    @Override
    public void createMembership(final String userId, final String groupId) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }

    @Override
    public void deleteMembership(final String userId, final String groupId) {
        throw new UnsupportedOperationException(explicitCallProhibitedError());
    }
}
