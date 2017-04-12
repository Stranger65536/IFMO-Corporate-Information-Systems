package com.emc.internal.reserv.config.activity;

import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author trofiv
 * @date 10.04.2017
 */
@Configuration
@AutoConfigureBefore(SecurityAutoConfiguration.class)
public class ProcessEngineConfigurator implements ProcessEngineConfigurationConfigurer {
    private final SessionFactory userManagerFactory;
    private final SessionFactory groupManagerFactory;
    private final SessionFactory membershipManagerFactory;
    private final SessionFactory identityInfoManagerFactory;

    @Autowired
    public ProcessEngineConfigurator(
            final SessionFactory userManagerFactory,
            final SessionFactory membershipManagerFactory,
            final SessionFactory groupManagerFactory,
            final SessionFactory identityInfoManagerFactory) {
        this.userManagerFactory = userManagerFactory;
        this.groupManagerFactory = groupManagerFactory;
        this.membershipManagerFactory = membershipManagerFactory;
        this.identityInfoManagerFactory = identityInfoManagerFactory;
    }

    @Override
    public void configure(final SpringProcessEngineConfiguration processEngineConfiguration) {
        processEngineConfiguration.setDbIdentityUsed(false);
        final List<SessionFactory> customSessionFactories =
                asList(userManagerFactory, groupManagerFactory, membershipManagerFactory, identityInfoManagerFactory);
        if (processEngineConfiguration.getCustomSessionFactories() == null) {
            processEngineConfiguration.setCustomSessionFactories(customSessionFactories);
        } else {
            processEngineConfiguration.getCustomSessionFactories().addAll(customSessionFactories);
        }
    }
}
