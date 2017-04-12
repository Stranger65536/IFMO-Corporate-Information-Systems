package com.emc.internal.reserv.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author trofiv
 * @date 10.03.2017
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    @SuppressWarnings("ProhibitedExceptionDeclared")
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().anyRequest();//antMatchers("/*.html", "/js/**", "/css/**", "/error");
    }
}