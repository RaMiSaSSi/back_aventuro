package com.example.demo.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageConfig.class);

    @Value("${app.upload.dir:uploads}")
    private String uploadBaseDir;

    @PostConstruct
    public void init() {
        try {
            // Create base uploads directory
            Path base = Paths.get(uploadBaseDir).toAbsolutePath().normalize();
            Files.createDirectories(base);
            logger.info("Created base upload directory: {}", base.toString());

            // Create subdirectories
            Path usersDir = base.resolve("users");
            Files.createDirectories(usersDir);
            logger.info("Created users upload directory: {}", usersDir.toString());

            Path imagesDir = base.resolve("images");
            Files.createDirectories(imagesDir);
            logger.info("Created images upload directory: {}", imagesDir.toString());

            Path bannersDir = base.resolve("banners");
            Files.createDirectories(bannersDir);
            logger.info("Created banners upload directory: {}", bannersDir.toString());

        } catch (IOException e) {
            logger.error("Could not create upload directories!", e);
            throw new RuntimeException("Could not create upload directories!", e);
        }
    }
}

