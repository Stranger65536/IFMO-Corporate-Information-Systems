package com.emc.internal.reserv.config.activity;

import com.emc.internal.reserv.repository.UserRepository;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author trofiv
 * @date 10.04.2017
 */
@Component
public class IdentityInfoManagerFactory implements SessionFactory {
    private final UserRepository userRepository;

    @Autowired
    public IdentityInfoManagerFactory(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Class<?> getSessionType() {
        return IdentityInfoEntityManager.class;
    }

    @Override
    public Session openSession() {
        return new CustomIdentityInfoEntityManager(userRepository);
    }
}
