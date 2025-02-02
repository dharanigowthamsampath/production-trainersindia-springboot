package com.trainersindia.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "file:.env", factory = OptionalPropertiesSourceFactory.class)
public class EnvConfig {
} 