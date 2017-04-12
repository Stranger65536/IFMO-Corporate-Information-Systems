package com.emc.internal.reserv.config.activity;

import com.emc.internal.reserv.repository.UserRepository;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author trofiv
 * @date 10.04.2017
 */
@Component
public class GroupManagerFactory implements SessionFactory {
    private final UserRepository userRepository;

    @Autowired
    public GroupManagerFactory(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Class<?> getSessionType() {
        return GroupIdentityManager.class;
    }


    @Override
    public Session openSession() {
        return new CustomGroupEntityManager(userRepository);
    }
}
