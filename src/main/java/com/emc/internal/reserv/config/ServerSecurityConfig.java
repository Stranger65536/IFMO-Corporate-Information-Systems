package com.emc.internal.reserv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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

    //TODO remove after final configuration
//    @Override
//    public void configure(final WebSecurity web) throws Exception {
//        web.debug(true);
//    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.requiresChannel().anyRequest().requiresSecure();
        http.authorizeRequests()
                .antMatchers("/*.html", "/**/*.xsd", "/*.ico", "/js/**", "/css/**", "/**/*.wsdl", "/error", REGISTER_WS_ENDPOINT).permitAll()
                .anyRequest().authenticated()
                .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringAntMatchers(REGISTER_WS_ENDPOINT)
                .and().httpBasic().authenticationEntryPoint(authenticationEntryPoint);
    }
}
