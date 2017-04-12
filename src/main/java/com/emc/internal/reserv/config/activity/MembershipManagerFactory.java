package com.emc.internal.reserv.config.activity;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.MembershipIdentityManager;
import org.springframework.stereotype.Component;

/**
 * @author trofiv
 * @date 10.04.2017
 */
@Component
public class MembershipManagerFactory implements SessionFactory {
    @Override
    public Class<?> getSessionType() {
        return MembershipIdentityManager.class;
    }

    @Override
    public Session openSession() {
        return new CustomMembershipEntityManager();
    }
}
