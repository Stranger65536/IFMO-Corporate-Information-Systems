package com.emc.internal.reserv.config;

import com.emc.internal.reserv.exception.ServiceFaultException;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.Properties;

/**
 * @author trofiv
 * @date 02.03.2017
 */
@EnableWs
@Configuration
@SuppressWarnings("WeakerAccess")
public class WebServiceConfig extends WsConfigurerAdapter {
    public static final String API_NAMESPACE_URI = "https://internal.emc.com/reserv-io/schema/api";
    public static final String REGISTER_NAMESPACE_URI = "https://internal.emc.com/reserv-io/schema/register";
    public static final String SERVLET_ENDPOINT = "/ws/*";
    public static final String REGISTER_WS_ENDPOINT = "/ws/register";
    public static final String API_WS_ENDPOINT = "/ws/api";
    public static final String REGISTER_SCHEMA = "register.xsd";
    public static final String API_SCHEMA = "api.xsd";

    @Value("${server.redirect-from-port}")
    private int httpPort;
    @Value("${server.port}")
    private int httpsPort;
    @Value("${spring.config.location}../static/schema/")
    private String schemaDirectory;

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(final ApplicationContext applicationContext) {
        final MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, SERVLET_ENDPOINT);
    }

    @Bean
    public SoapFaultMappingExceptionResolver exceptionResolver() {
        final SoapFaultMappingExceptionResolver exceptionResolver = new DetailSoapFaultDefinitionExceptionResolver();

        final SoapFaultDefinition faultDefinition = new SoapFaultDefinition();
        faultDefinition.setFaultCode(SoapFaultDefinition.SERVER);
        exceptionResolver.setDefaultFault(faultDefinition);

        final Properties errorMappings = new Properties();
        errorMappings.setProperty(Exception.class.getName(), SoapFaultDefinition.SERVER.toString());
        errorMappings.setProperty(ServiceFaultException.class.getName(), SoapFaultDefinition.SERVER.toString());
        exceptionResolver.setExceptionMappings(errorMappings);
        exceptionResolver.setOrder(1);

        return exceptionResolver;
    }

    @Bean(name = "api")
    public DefaultWsdl11Definition apiWsdlDefinition(@Qualifier("apiSchema") final XsdSchema schema) {
        final DefaultWsdl11Definition wsdlDefinition = new DefaultWsdl11Definition();
        wsdlDefinition.setPortTypeName("ReservIOApiPort");
        wsdlDefinition.setLocationUri(API_WS_ENDPOINT);
        wsdlDefinition.setTargetNamespace(API_NAMESPACE_URI);
        wsdlDefinition.setSchema(schema);
        return wsdlDefinition;
    }

    @Bean(name = "register")
    public DefaultWsdl11Definition registerWsdlDefinition(@Qualifier("registerSchema") final XsdSchema schema) {
        final DefaultWsdl11Definition wsdlDefinition = new DefaultWsdl11Definition();
        wsdlDefinition.setPortTypeName("ReservIORegisterPort");
        wsdlDefinition.setLocationUri(REGISTER_WS_ENDPOINT);
        wsdlDefinition.setTargetNamespace(REGISTER_NAMESPACE_URI);
        wsdlDefinition.setSchema(schema);
        return wsdlDefinition;
    }

    @Bean
    public XsdSchema apiSchema() {
        return new SimpleXsdSchema(new FileSystemResource(schemaDirectory + API_SCHEMA));
    }

    @Bean
    public XsdSchema registerSchema() {
        return new SimpleXsdSchema(new FileSystemResource(schemaDirectory + REGISTER_SCHEMA));
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        final TomcatEmbeddedServletContainerFactory tomcat = new CustomTomcatEmbeddedServletContainerFactory();
        tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
        return tomcat;
    }

    private Connector initiateHttpConnector() {
        final Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(httpPort);
        connector.setSecure(false);
        connector.setRedirectPort(httpsPort);
        return connector;
    }

    private static class CustomTomcatEmbeddedServletContainerFactory extends TomcatEmbeddedServletContainerFactory {
        @Override
        protected void postProcessContext(final Context context) {
            final SecurityConstraint securityConstraint = new SecurityConstraint();
            securityConstraint.setUserConstraint("CONFIDENTIAL");
            final SecurityCollection collection = new SecurityCollection();
            collection.addPattern("/*");
            securityConstraint.addCollection(collection);
            context.addConstraint(securityConstraint);
        }
    }
}
