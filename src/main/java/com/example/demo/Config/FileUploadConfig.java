package com.example.demo.Config;

import jakarta.servlet.MultipartConfigElement;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class FileUploadConfig {

    // Define multipart limits to a very large value (effectively unlimited for practical purposes)
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // set very large sizes (e.g., 10 GB) to avoid MaxUploadSizeExceededException
        factory.setMaxFileSize(DataSize.ofGigabytes(10));
        factory.setMaxRequestSize(DataSize.ofGigabytes(10));
        return factory.createMultipartConfig();
    }

    // Customize the embedded Tomcat connector to accept large POST bodies and to not swallow large requests
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return (TomcatServletWebServerFactory factory) -> factory.addConnectorCustomizers((connector) -> {
            // disable max post size (use -1) so Tomcat accepts large request bodies
            connector.setMaxPostSize(-1);
            // if the protocol handler supports max swallow size, set it to -1 as well
            if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?> protocol) {
                protocol.setMaxSwallowSize(-1);
            }
        });
    }
}
