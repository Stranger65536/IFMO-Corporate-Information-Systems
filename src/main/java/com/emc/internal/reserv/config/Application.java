package com.emc.internal.reserv.config;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author trofiv
 * @date 21.02.2017
 */
@EnableTransactionManagement
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan(basePackages = "com.emc.internal.reserv")
@EntityScan(basePackages = "com.emc.internal.reserv.entity")
@EnableJpaRepositories(basePackages = "com.emc.internal.reserv.repository")
public class Application extends SpringBootServletInitializer {
    public static void main(final String[] args) {
        LogFactory.useLog4J2Logging();
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }
}
