package com.emc.internal.reserv.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

/**
 * @author trofiv
 * @date 22.04.2017
 */
@Configuration
//TODO favicon
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

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new MyWebMvcConfigurerAdapter();
    }

    private static class MyWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {
        @Override
        public void addCorsMappings(final CorsRegistry registry) {
            registry.addMapping("/**/*").allowedOrigins("*");
        }
    }
}
