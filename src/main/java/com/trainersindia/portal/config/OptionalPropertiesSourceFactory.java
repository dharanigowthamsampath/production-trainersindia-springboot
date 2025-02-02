package com.trainersindia.portal.config;

import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Properties;

public class OptionalPropertiesSourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        Properties properties = new Properties();
        Resource fileResource = resource.getResource();
        
        if (fileResource.exists()) {
            properties.load(fileResource.getInputStream());
            return new PropertiesPropertySource(name != null ? name : resource.getResource().getFilename(), properties);
        } else {
            return new PropertiesPropertySource("empty", new Properties());
        }
    }
} 