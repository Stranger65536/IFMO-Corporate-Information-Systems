package com.emc.internal.reserv.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

/**
 * @author trofiv
 * @date 22.04.2017
 */
@Configuration
public class WebServerConfiguration {
    @Value("${spring.config.location}../static/")
    private String resourceDirectory;

    @Bean
    public SimpleUrlHandlerMapping myFaviconHandlerMapping() {
        final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MIN_VALUE);
        mapping.setUrlMap(singletonMap("/**/favicon.ico", customFaviconRequestHandler()));
        return mapping;
    }

    @Bean
    protected ResourceHttpRequestHandler customFaviconRequestHandler() {
        final ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        requestHandler.setLocations(singletonList(new FileSystemResource(resourceDirectory + "favicon.ico")));
        requestHandler.setCacheSeconds(0);
        return requestHandler;
    }
}
