// java
package com.example.demo.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Use absolute file: URI so Spring serves the runtime-created uploads folder reliably
        String uploadLocation = Paths.get("uploads").toAbsolutePath().toUri().toString(); // e.g. file:/C:/.../uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation);
    }
}