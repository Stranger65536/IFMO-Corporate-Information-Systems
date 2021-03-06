package com.emc.internal.reserv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import static com.emc.internal.reserv.config.WebServiceConfig.REGISTER_WS_ENDPOINT;

/**
 * @author trofiv
 * @date 03.04.2017
 */
@Configuration
@EnableWebSecurity
@SuppressWarnings("ProhibitedExceptionDeclared")
public class ServerSecurityConfig extends WebSecurityConfigurerAdapter {
    private final BasicAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public ServerSecurityConfig(final BasicAuthenticationEntryPoint CustomBasicAuthenticationEntryPoint) {
        this.authenticationEntryPoint = CustomBasicAuthenticationEntryPoint;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.requiresChannel().anyRequest().requiresSecure();
        http.authorizeRequests()
                .antMatchers("/", "/*.html", "/**/*.xsd", "/*.ico", "/js/**", "/css/**", "/img/**", "/**/*.wsdl", "/error", REGISTER_WS_ENDPOINT).permitAll()
                .anyRequest().authenticated()
                .and().httpBasic().authenticationEntryPoint(authenticationEntryPoint)
                .and().csrf().disable();
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
